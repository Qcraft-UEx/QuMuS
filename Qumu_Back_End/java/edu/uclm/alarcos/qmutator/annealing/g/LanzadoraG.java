package edu.uclm.alarcos.qmutator.annealing.g;

import java.util.List;

import edu.uclm.alarcos.qmutator.annealing.Combination;
import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.XValue;

public class LanzadoraG {

	public static void main(String[] args) throws Exception {
		//GH h = sudokyXYZ();
		//ConcreteFunction f = prepareSudokuXYZ(h);
		
		GH h = queens();

		System.out.println(h.toString());
		
		CH f = h.getFunction();
		System.out.println(f.toJSON());
		
		List<Combination> result = f.calculateAll(null, null, false, true);
		
		List<XValue> data;
		XValue datum;
		for (int i=0; i<result.size(); i++) {
			Combination comb = result.get(i);
			data = comb.getData();
			for (int j=0; j<data.size(); j++) {
				datum = data.get(j);
				System.out.print(datum.getValue() + (!datum.isFixed() ? "* " : " "));
			}
			System.out.println("-> " + comb.getValue());
		}
	}
	
	private static GH queens() throws Exception {
		GH h = new GH();
		
		{
			GRule filas = h.newRule();
			GIndexedSummation sum = filas.newIndexedSummation();
			sum.newFrom("i", 0);
			GParameter to = h.newParameter("n");
			sum.setTo(to);
			GUseOfX xij = sum.newUseOfX("i", "j");
			sum.setBody(xij);
			filas.setLeft(sum);
			filas.setRight(new GIntValue(1));
			filas.newIntervalForAll("j", 0, h.newParameter("n"));
		}
		
		{
			GRule columnas = h.newRule();
			GIndexedSummation sum = columnas.newIndexedSummation();
			sum.newFrom("j", 0);
			GParameter to = h.newParameter("n");
			sum.setTo(to);
			GUseOfX xij = sum.newUseOfX("i", "j");
			sum.setBody(xij);
			columnas.setLeft(sum);
			columnas.setRight(new GIntValue(1));
			columnas.newIntervalForAll("i", 0, h.newParameter("n"));
		}
		
		{
			GRule diagonales = h.newRule();
			GIndexedSummation sum = diagonales.newIndexedSummation();
			sum.newFrom("i", 0);
			GParameter to = h.newParameter("n");
			sum.setTo(to);
			GUseOfX xij = sum.newUseOfX("i", "j");
			sum.setBody(xij);
			diagonales.setLeft(sum);
			diagonales.setRight(new GIntValue(1));
			
			GSum left = new GSum(diagonales);
			left.add(new GIndexStringValue("i"));
			left.add(new GIntValue(1));
			diagonales.newIntervalForAll("j", left, h.newParameter("n"));
		}
		
		h.setXCardinals(5, 5);
		h.setParameterValue("n", 5);
		h.instantiate();
		return h;
	}
	
	private static GH testSetSum() throws Exception {
		GH h = new GH();
		
		GRule rule = h.newRule();
		GSetSummation sum = rule.newSetSummation();
		sum.setSet("i", h.newSet("N"));
		GUseOfX xij = sum.newUseOfX("i");
		sum.setBody(xij);
		
		h.setSetValues("N", 0, 1, 2);
		h.setXCardinals(5);
		h.instantiate();
		return h;
	}

	private static GH cajasXY() throws Exception {
		GH h = new GH();
		
		GRule rule1 = h.newRule();
		{
			GIndexedSummation sum = rule1.newIndexedSummation();
			sum.newFrom("j", 0);
			GParameter to = h.newParameter("m");
			sum.setTo(to);
			GIndexedValue pij = sum.newIndexedValue("v", "i", "j");
			GUseOfX xij = sum.newUseOfX("i", "j");
			GProduct product = new GProduct();
			product.add(pij, xij);
			sum.setBody(product);
			rule1.setLeft(sum);
			rule1.newIntervalForAll("i", 0, h.newParameter("n"));
		}
		
		GRule rule2 = h.newRule();
		{
			GIndexedSummation sum = rule2.newIndexedSummation();
			sum.newFrom("j", 0);
			GParameter to = h.newParameter("m");
			sum.setTo(to);
			GUseOfX xij = sum.newUseOfX("i", "j");
			sum.setBody(xij);
			rule2.setLeft(sum);
			rule2.newIntervalForAll("i", 0, h.newParameter("n"));
			
			rule2.setRight(h.newParameter("K"));
			rule2.setLambda(10.0);
		}
		

		h.setParameterValue("n", 5);
		h.setParameterValue("m", 4);
		h.setParameterValue("K", 2);
		h.newVariable("v", 5, 4);
		h.setVariable("v", 2.0, 0, 0); h.setVariable("v", 1.0, 0, 1); h.setVariable("v", 3.0, 0, 2); h.setVariable("v", 4.0, 0, 3);
		h.setVariable("v", 3.0, 1, 0); h.setVariable("v", 2.0, 1, 1); h.setVariable("v", 1.0, 1, 2); h.setVariable("v", 4.0, 1, 3);
		h.setVariable("v", 5.0, 2, 0); h.setVariable("v", 2.0, 2, 1); h.setVariable("v", 6.0, 2, 2); h.setVariable("v", 7.0, 2, 3);
		h.setVariable("v", 1.0, 3, 0); h.setVariable("v", 8.0, 3, 1); h.setVariable("v", 9.0, 3, 2); h.setVariable("v", 3.0, 3, 3);
		h.setVariable("v", 2.0, 4, 0); h.setVariable("v", 4.0, 4, 1); h.setVariable("v", 2.0, 4, 2); h.setVariable("v", 1.0, 4, 3);
		
		h.setXCardinals(5, 4);
		h.instantiate();
		
		return h;
	}
	
	private static GH cajasX() throws Exception {
		GH h = new GH();
		
		GRule rule1 = h.newRule();
		GIndexedSummation sum = rule1.newIndexedSummation();
		sum.newFrom("i", 0);
		GParameter to = h.newParameter("n");
		sum.setTo(to);
		GIndexedValue ci = sum.newIndexedValue("c", "i");
		GUseOfX xi = sum.newUseOfX("i");
		GProduct product = new GProduct();
		product.add(ci, xi);
		sum.setBody(product);
		rule1.setLeft(sum);
		

		GRule rule2 = h.newRule();
		rule2.setLambda(1.0);
		GIndexedSummation sum2 = rule2.newIndexedSummation();
		sum2.newFrom("i", 0);
		sum2.setTo(h.newParameter("n"));
		
		GUseOfX uox = sum2.newUseOfX("i");
		sum2.setBody(uox);
		GParameter K = h.newParameter("K");
		
		rule2.setLeft(sum2);
		rule2.setRight(K);
		
		return h;
	}
	
	private static GH sudokyXY() throws Exception {
		GH h = new GH();
		
		{
			GRule filas = h.newRule();
			filas.setLambda(1.0);
			
			GIndexedSummation sum = filas.newIndexedSummation();
			sum.newFrom("j", 0);
			GParameter to = h.newParameter("n");
			sum.setTo(to);
			GUseOfX xijk = sum.newUseOfX("i", "j", "k");
			sum.setBody(xijk);
			
			filas.setLeft(sum);
			filas.setRight(new GIntValue(1));
			filas.setLambda(1.0);
			filas.newIntervalForAll("i", 0, h.newParameter("n"));
			filas.newIntervalForAll("k", 0, h.newParameter("n"));
			System.out.println(filas.toJSON());
		}
		
		{
			GRule columnas = h.newRule();
			columnas.setLambda(1.0);
			
			GIndexedSummation sum = columnas.newIndexedSummation();
			sum.newFrom("i", 0);
			GParameter to = h.newParameter("n");
			sum.setTo(to);
			GUseOfX xijk = sum.newUseOfX("i", "j", "k");
			sum.setBody(xijk);
			
			columnas.setLeft(sum);
			columnas.setRight(new GIntValue(1));
			columnas.setLambda(1.0);
			columnas.newIntervalForAll("j", 0, h.newParameter("n"));
			columnas.newIntervalForAll("k", 0, h.newParameter("n"));
			System.out.println(columnas.toJSON());
		}
		
		h.setParameterValue("n", 4);
		
		CH f = h.setXCardinals(4, 4, 4); 
		for (int i=0; i<4; i++) 
			for (int j=0; j<4; j++) 
				for (int k=0; k<4; k++) 
					f.setXValue(0, i, j, k);

		f.setXValue(null, 0, 3, 1);
		f.setXValue(null, 1, 0, 0);
		f.setXValue(null, 1, 0, 3);
		f.setXValue(null, 1, 2, 2);
		f.setXValue(null, 1, 3, 0);
		f.setXValue(null, 1, 3, 3);
		f.setXValue(null, 2, 0, 0);
		f.setXValue(null, 2, 0, 3);
		f.setXValue(null, 2, 1, 2);
		f.setXValue(null, 2, 2, 2);
		f.setXValue(null, 2, 3, 0);
		f.setXValue(null, 2, 3, 3);
		f.setXValue(null, 3, 0, 1);
		
		f.setXValue(1, 0, 0, 2);
		f.setXValue(1, 0, 0, 2);
		f.setXValue(1, 0, 1, 3);
		f.setXValue(1, 2, 2, 1);
		f.setXValue(1, 3, 1, 0);
		f.setXValue(1, 3, 2, 3);
		f.setXValue(1, 3, 3, 2);
		
		h.instantiate();
		
		return h;
	}
	
	private static GH sudokyXYZ() throws Exception {
		GH h = new GH();
		
		{
			GRule filas = h.newRule();
			filas.setLambda(1.0);
			
			GIndexedSummation sum = filas.newIndexedSummation();
			sum.newFrom("j", 0);
			GParameter to = h.newParameter("n");
			sum.setTo(to);
			GUseOfX xijk = sum.newUseOfX("i", "j", "k");
			sum.setBody(xijk);
			
			filas.setLeft(sum);
			filas.setRight(new GIntValue(1));
			filas.setLambda(1.0);
			filas.newIntervalForAll("i", 0, h.newParameter("n"));
			filas.newIntervalForAll("k", 0, h.newParameter("n"));
		}
		
		{
			GRule columnas = h.newRule();
			columnas.setLambda(1.0);
			
			GIndexedSummation sum = columnas.newIndexedSummation();
			sum.newFrom("i", 0);
			GParameter to = h.newParameter("n");
			sum.setTo(to);
			GUseOfX xijk = sum.newUseOfX("i", "j", "k");
			sum.setBody(xijk);
			
			columnas.setLeft(sum);
			columnas.setRight(new GIntValue(1));
			columnas.setLambda(1.0);
			columnas.newIntervalForAll("j", 0, h.newParameter("n"));
			columnas.newIntervalForAll("k", 0, h.newParameter("n"));
		}
		
		{
			GRule recuadros = h.newRule();
			recuadros.setLambda(1.0);
			
			GSetSummation sum = recuadros.newSetSummation();
			GSet N = h.newSet("N");
			sum.setSet("i", N);
			GUseOfX xijk = sum.newUseOfX("i", "j", "k");
			sum.setBody(xijk);
			
			recuadros.setRight(new GIntValue(1));
			recuadros.setLambda(1.0);
			recuadros.newForAll("j", h.newSet("N"));
			recuadros.newIntervalForAll("k", 0, h.newParameter("n"));
		}
		
		
		h.setParameterValue("n", 4);
		h.setSetValues("N", 0, 2);
		
		CH f = h.setXCardinals(4, 4, 4); 
		for (int i=0; i<4; i++) 
			for (int j=0; j<4; j++) 
				for (int k=0; k<4; k++) 
					f.setXValue(0, i, j, k);

		f.setXValue(null, 0, 3, 1);
		f.setXValue(null, 1, 0, 0);
		f.setXValue(null, 1, 0, 3);
		f.setXValue(null, 1, 2, 2);
		f.setXValue(null, 1, 3, 0);
		f.setXValue(null, 1, 3, 3);
		f.setXValue(null, 2, 0, 0);
		f.setXValue(null, 2, 0, 3);
		f.setXValue(null, 2, 1, 2);
		f.setXValue(null, 2, 2, 2);
		f.setXValue(null, 2, 3, 0);
		f.setXValue(null, 2, 3, 3);
		f.setXValue(null, 3, 0, 1);
		
		f.setXValue(1, 0, 0, 2);
		f.setXValue(1, 0, 0, 2);
		f.setXValue(1, 0, 1, 3);
		f.setXValue(1, 2, 2, 1);
		f.setXValue(1, 3, 1, 0);
		f.setXValue(1, 3, 2, 3);
		f.setXValue(1, 3, 3, 2);
		
		h.instantiate();
		
		return h;
	}
}
