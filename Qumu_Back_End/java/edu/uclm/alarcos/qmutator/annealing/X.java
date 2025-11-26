package edu.uclm.alarcos.qmutator.annealing;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class X {
	private String name;
	private XValueList values;
	private List<Integer> emptyPositions;
	private List<Integer> divisors;
	private int numberOfCombinations;
	private int cardinal;
	private Integer[] cardinals;
	private int[] prods;

	public X() {
		this.values = new XValueList();
		this.numberOfCombinations = 1;
	}

	public void setCardinals(Integer... xCardinals) {
		setUp(xCardinals);
	}

	private void setUp(Integer... xCardinals) {
		this.cardinal = 1;
		this.cardinals = xCardinals;
		for (int i=0; i<xCardinals.length; i++)
			this.cardinal = this.cardinal * xCardinals[i];
		
		this.prods = new int[xCardinals.length];
		for (int i=1; i<this.cardinals.length; i++) {
			this.prods[i-1] = 1;
			for (int j=i; j<this.cardinals.length; j++) {
				this.prods[i-1] = this.prods[i-1] * this.cardinals[j];
			}
		}
		
		for (int i=0; i<this.cardinal; i++)
			this.values.add(new XValue());
	}
	
	public void setCardinals(ArrayList<Integer> xCardinals) {
		Integer[] cardinals = new Integer[xCardinals.size()];
		for (int i=0; i<xCardinals.size(); i++)
			cardinals[i] = xCardinals.get(i);
		this.setUp(cardinals);
	}

	public void setCardinals(JSONArray jsaCardinals) {
		Integer[] cardinals = new Integer[jsaCardinals.length()];
		for (int i=0; i<jsaCardinals.length(); i++) 
			cardinals[i] = jsaCardinals.getInt(i);
		this.setUp(cardinals);
	}
	
	public int getCardinal() {
		return cardinal;
	}
	
	public int getCardinal(int index) {
		return this.cardinals[index];
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("type", this.getClass().getSimpleName());
		jso.put("name", this.name);
		JSONArray jsaCardinals = new JSONArray();
		for (Integer i : this.cardinals)
			jsaCardinals.put(i);
		jso.put("cardinals", jsaCardinals);
		jso.put("values", this.values.toJSON());
		return jso;
	}
	
	public XValue getValue(int... index) {
		int pos = this.getPos(index);
		return this.values.get(pos);
	}

	public void setValue(Integer value, int... coords) {
		int pos = getPos(coords);
		if (value==null)
			this.values.set(pos, new XValue());
		else
			this.values.set(pos, value);
	}

	int getPos(int... coords) {
		int pos = 0;
		for (int i=0; i<this.prods.length-1; i++) 
			pos = pos + coords[i] * this.prods[i];
		pos = pos + coords[coords.length-1];
		return pos;
	}

	public void setValues(JSONArray jsaxValues) {
		JSONObject jsoValue;
		XValue xValue;
		for (int i=0; i<jsaxValues.length(); i++) {
			jsoValue = jsaxValues.getJSONObject(i);
			xValue = new XValue(jsoValue);
			this.values.set(i, xValue);
		}
	}

	public void prepare() {
		this.numberOfCombinations = 1;
		this.emptyPositions = new ArrayList<>();
		this.divisors = new ArrayList<>();
		
		XValue xValue;
		for (int i=0; i<values.size(); i++) {
			xValue = values.get(i);
			if (!xValue.isFixed()) {
				this.emptyPositions.add(i);
				this.numberOfCombinations = 2 * this.numberOfCombinations;
			}
		}
		
		int divisor = 1;
		for (int i=0; i<this.emptyPositions.size()-1; i++)
			divisor = divisor * 2;
		
		for (int i=0; i<this.emptyPositions.size(); i++) {
			this.divisors.add(divisor);
			divisor = divisor / 2;
		}
	}
	
	public List<XValue> getCombination(int index) {
		List<XValue> result = new ArrayList<>();
		XValue xValue;
		for (int i=0; i<values.size(); i++) {
			xValue = values.get(i);
			if (xValue.isFixed())
				result.add(xValue);
			else 
				result.add(null);
		}
		for (int i=0; i<this.emptyPositions.size(); i++) {
			int emptyPosition = this.emptyPositions.get(i);
			int value = (index/this.divisors.get(i)) % 2;
			xValue = new XValue();
			xValue.setIndex(emptyPosition);
			xValue.setValue(value);
			xValue.setFixed(false);
			result.set(emptyPosition, xValue);
		}
		return result;
	}
	
	public int getNumberOfCombinations() {
		return this.numberOfCombinations;
	}
	
	@Override
	public String toString() {
		String r = "x=";
		for (Integer cardinal : this.cardinals)
			r = r + cardinal + ", ";
		r = r.substring(0, r.length()-2);
		return r;
	}

	public int[] getProds() {
		return this.prods;
	}
}
