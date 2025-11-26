package edu.uclm.alarcos.qmutator.annealing.mathml;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.uclm.alarcos.qmutator.annealing.g.GExpr;
import edu.uclm.alarcos.qmutator.annealing.g.GForAll;
import edu.uclm.alarcos.qmutator.annealing.g.GFrom;
import edu.uclm.alarcos.qmutator.annealing.g.GH;
import edu.uclm.alarcos.qmutator.annealing.g.GIndexIntValue;
import edu.uclm.alarcos.qmutator.annealing.g.GIndexStringValue;
import edu.uclm.alarcos.qmutator.annealing.g.GIndexedValue;
import edu.uclm.alarcos.qmutator.annealing.g.GIntValue;
import edu.uclm.alarcos.qmutator.annealing.g.GProduct;
import edu.uclm.alarcos.qmutator.annealing.g.GRule;
import edu.uclm.alarcos.qmutator.annealing.g.GIndexedSummation;
import edu.uclm.alarcos.qmutator.annealing.g.GUseOfX;
import edu.uclm.alarcos.qmutator.annealing.g.GVariable;

public class Math2Qumu {
	
	private Document document;
	
	private static final int SUMMATORY_FROM = 0,
			SUMMATORY_TO = 1,
			SUMMATORY_BODY = 2, 
			FORALL_LEFT = 3,
			FORALL_RIGHT = 4;
	
	private int context;
	
	public Math2Qumu(String code) {
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			code = clean(code);
			db = dbf.newDocumentBuilder();
			if (!code.startsWith("<math"))
				code = "<math>" + code + "</math>";
			
			this.document = db.parse(new ByteArrayInputStream(code.getBytes()));
			this.context = SUMMATORY_FROM;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	public GH buildH() throws Exception {
		GH h = new GH();
		Node root = document.getFirstChild();
		
		NodeList children = root.getChildNodes();
		GRule rule=null;
		for (int i=0; i<children.getLength(); i++) {
			Node child = children.item(i);
			String childName = child.getNodeName();
			if (childName.equals("munderover")) {
				Node child1 = child.getFirstChild();
				if (!child1.getNodeName().equals("mo")) 
					throw new Exception("Expected mo node");
				String op = child1.getFirstChild().getNodeValue();
				if (!op.equals("&#x2211;") && !op.equals("∑"))
					throw new Exception("Expected summatory");
				
				rule = buildSummatory(child, h).getRule();
				i = i +1 ;
				continue;
			}
			
			if (childName.equals("mo")) {
				String op = child.getFirstChild().getNodeValue();
				if (op.equals("&#x2200;") || op.equalsIgnoreCase("forAll") || op.equals("∀")) {
					if (rule==null)
						throw new Exception("Found forAll with no active rule");
					buildForAll(child, h, rule);
					i = i + 3;
					continue;
				}
			}
		}
		return h;
	}

	private GForAll buildForAll(Node child, GH h, GRule rule) throws Exception {
		Node variableNode = child.getNextSibling();
		Node operatorNode = variableNode.getNextSibling();
		Node intervalNode = operatorNode.getNextSibling();
		
		String op = operatorNode.getFirstChild().getNodeValue();
		if (!op.equals("in") && !op.equals("&#8712;") && !op.equals("∈"))
			throw new Exception("Expected \"in\" or \"∈\"");
		
		String variable = variableNode.getFirstChild().getNodeValue();
		
		Node leftLimit = intervalNode.getFirstChild();
		Node rightLimit = leftLimit.getNextSibling();
		
		this.context = FORALL_LEFT;
		GExpr start = buildGExpr(leftLimit, h);
		
		this.context = FORALL_RIGHT;
		GExpr end = buildGExpr(rightLimit, h);
		
		//GForAll expr = rule.newForAll(variable, start, end);
		//return expr;
		return null;
	}

	private GIndexedSummation buildSummatory(Node munderover, GH h) throws Exception {	
		GRule rule = h.newRule();
		GIndexedSummation sum = rule.newIndexedSummation();

		Node from = munderover.getFirstChild().getNextSibling();
		sum.setFrom(buildFrom(from, sum));

		Node to = from.getNextSibling();
		this.context = SUMMATORY_TO;
		sum.setTo(buildGExpr(to, h));
		
		this.context = SUMMATORY_BODY;
		Node body = munderover.getNextSibling();
		if (body.getNodeName().equals("mfenced"))
			sum.setBody(buildGExpr(body, h));
		
		return sum;
	}

	private GExpr buildGExpr(Node node, GH h) throws Exception {
		if (node.getNodeName().equals("mn")) {
			int value = Integer.parseInt(node.getFirstChild().getNodeValue());
			GIntValue expr = new GIntValue(value);
			return expr;
		}
		
		if (node.getNodeName().equals("mi")) {
			if (this.context==SUMMATORY_FROM) {
				// En principio, aquí no se entra, porque el from se construye en
				// buildFrom(...)
			}
			
			if (this.context==SUMMATORY_TO) {
				return buildParameter(node, h);
			}
			
			if (this.context==SUMMATORY_BODY) {
				if (this.isParameter(node)) {
					
				}
			}
			
			if (this.context==FORALL_LEFT) {
				
			}
			
			if (this.context==FORALL_RIGHT) {
				if (this.isParameter(node)) {
					return this.buildParameter(node, h);
				}
			}
		}
		
		if (node.getNodeName().equals("mfenced")) {
			NodeList children = node.getChildNodes();
			if (children.getLength()==1) {
				
			} 
			
			if (children.getLength()==3) {
				Node child1 = children.item(0);
				Node child2 = children.item(1);
				Node child3 = children.item(2);
				
				if (!child2.getNodeName().equals("mo"))
					throw new Exception("Expected mo");
				String op = child2.getFirstChild().getNodeValue();
				if (!op.equals("*") && !op.equals("·") && !op.equals("x"))
					throw new Exception("Expected *");
				
				GExpr left = buildGExpr(child1, h);
				GExpr right = buildGExpr(child3, h);
				GProduct expr = new GProduct();
				expr.add(left, right);
				return expr;
			}
			
			throw new Exception("Expected 1 or 3 arguments in mfenced");
		} 
		
		if (node.getNodeName().equals("msub")) {
			NodeList children = node.getChildNodes();
			if (children.getLength()!=2)
				throw new Exception("Expected two children in msub");
			
			Node child1 = children.item(0);
			Node child2 = children.item(1);

			String id = child1.getFirstChild().getNodeValue();
			if (child2.getNodeName().equals("mi")) {
				
			}
			
			if (child2.getNodeName().equals("mrow")) {
				NodeList indexNodes = child2.getChildNodes();
				String[] indexNames = new String[indexNodes.getLength()];
				for (int i=0; i<indexNames.length; i++) {
					String indexName = indexNodes.item(i).getFirstChild().getNodeValue();
					indexNames[i] = indexName;
				}
				if (id.equalsIgnoreCase("x")) {
					GUseOfX expr = new GUseOfX();
					for (int i=0; i<indexNames.length; i++) {
						try {
							Integer indexValue = Integer.parseInt(indexNames[i]);
							expr.addIndex(new GIndexIntValue(indexValue));
						} catch (Exception e) {
							expr.addIndex(new GIndexStringValue(indexNames[i]));
						}
					}
					return expr;
				}
				GIndexedValue expr = new GIndexedValue();
				GVariable variable = h.newVariable(id, indexNames.length);
				expr.setVariable(variable);
				for (int i=0; i<indexNames.length; i++) {
					try {
						Integer indexValue = Integer.parseInt(indexNames[i]);
						expr.addIndex(new GIndexIntValue(indexValue));
					} catch (Exception e) {
						expr.addIndex(new GIndexStringValue(indexNames[i]));
					}
				}
				return expr;
			}
			
		}
		return null;
	}

	private GExpr buildParameter(Node node, GH h) {
		JSONObject jso = new JSONObject();
		jso.put("type", "GParameter");
		String id = node.getFirstChild().getNodeValue();
		jso.put("parameter", id);
		GExpr expr = GExpr.build(jso, h);
		return expr;
	}

	private GFrom buildFrom(Node node, GIndexedSummation sum) throws Exception {
		if (!node.getNodeName().equals("mrow"))
			throw new Exception("Expected mrow node as 2nd child of munderover");
		Node mi = node.getFirstChild();
		if (!mi.getNodeName().equals("mi"))
			throw new Exception("Expected mi node as 1st child of mrow");
		String indexName = mi.getFirstChild().getNodeValue();
		
		Node mo = mi.getNextSibling();
		if (!mo.getNodeName().equals("mo"))
			throw new Exception("Expected mo node as 2nd child of mrow");
		
		Node mn = mo.getNextSibling();
		if (!mn.getNodeName().equals("mn"))
			throw new Exception("Expected mn node as 1st child of mrow");
		String right = mn.getFirstChild().getNodeValue();
		
		GFrom from = sum.newFrom(indexName, Integer.parseInt(right));
		return from;
	}

	private boolean isParameter(Node node) {
		Node parent = node.getParentNode();
		if (parent.getNodeName().equals("mrow"))
			return false;
		return true;
	}
	
	private String clean(String info) {
		String code = "";
		boolean inside = false;
		char c;
		for (int i=0; i<info.length(); i++) {
			c = info.charAt(i);
			if (c=='<')
				inside = true;
			if (c=='>')
				inside = false;
			if (inside)
				code = code + c;
			else if (!Character.isWhitespace(c))
				code = code + c;
		}
		return code;
	}
}
