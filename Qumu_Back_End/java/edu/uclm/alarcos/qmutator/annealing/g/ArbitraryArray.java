package edu.uclm.alarcos.qmutator.annealing.g;

import java.util.ArrayList;

import org.json.JSONArray;

public class ArbitraryArray<T> {
	
	private ArrayList<T> values;
	private int[] dimensions;
	private int[] prods;
	
	public void setDimensions(int... dimensions) {
		this.dimensions = dimensions;
		int r = 1;
		for (int dimension : dimensions)
			r = r * dimension;
		this.values = new ArrayList<T>();
		for (int i=0; i<r; i++)
			this.values.add(null);
		
		this.prods = new int[dimensions.length];
		for (int i=1; i<this.dimensions.length; i++) {
			this.prods[i-1] = 1;
			for (int j=i; j<dimensions.length; j++) {
				this.prods[i-1] = this.prods[i-1] * dimensions[j];
			}
		}
	}
	
	public void setDimensions(ArrayList<Integer> dimensions) {
		int[] iDimensions = new int[dimensions.size()];
		for (int i=0; i<dimensions.size(); i++)
			iDimensions[i] = dimensions.get(i);
		this.setDimensions(iDimensions);
	}
	
	public void setValues(ArrayList<T> values) {
		this.values = values;
	}

	public int setValue(T value, int... coords) {
		int pos = getPos(coords);
		this.values.set(pos, value);
		return pos;
	}

	public int getPos(int... coords) {
		int pos = 0;
		for (int i=0; i<this.prods.length-1; i++) 
			pos = pos + coords[i] * this.prods[i];
		pos = pos + coords[coords.length-1];
		return pos;
	}
	
	public T get(int... coords) {
		int pos = getPos(coords);
		return this.values.get(pos);
	}

	public double getCardinal() {
		double r = 1;
		for (int dimension : dimensions)
			r = r * dimension;
		return r;
	}

	public int[] getDimensions() {
		return this.dimensions;
	}

	public JSONArray toJSON() {
		JSONArray jsa = new JSONArray();
		for (T value : this.values)
			jsa.put(value);
		return jsa;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.values==null)
			return "";
		for (T value : this.values)
			sb.append(value + ",");
		String r = sb.toString();
		r = r.substring(0, r.length()-2);
		return r;
	}

	public static void main(String[] args) {
		double cont = 1;
		
		int x = 2, y = 3, z = 2, t = 5; 
		ArbitraryArray<Double> m = new ArbitraryArray<Double>();
		m.setDimensions(x, y, z, t);
		for (int i=0; i<x; i++)
			for (int j=0; j<y; j++)
				for (int k=0; k<z; k++)
					for (int l=0; l<t; l++)
						System.out.println(m.setValue(cont++, i, j, k, l));
		
		for (int i=0; i<x; i++)
			for (int j=0; j<y; j++)
				for (int k=0; k<z; k++)
					for (int l=0; l<t; l++)
						System.out.println(m.get(i, j, k, l));
		
	}
}
