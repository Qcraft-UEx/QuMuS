package edu.uclm.alarcos.qmutator.annealing;

import java.util.List;

import org.json.JSONObject;

public class LanzadorEjemplos {
	
	public static void main(String[] args) throws Exception {
		CH f;
		//f = x1();
		f = trivial();
		f = trivialXY();
		//f = cajas2();
		//f = cajas5();
		JSONObject jso = f.toJSON();
		System.out.println(jso);
		
		CH f2 = new CH(jso);
		List<Combination> result = f2.calculateAll(null, null, false, true);
		
		for (int i=0; i<result.size(); i++) {
			Combination comb = result.get(i);
			List<XValue> data = comb.getData();
			for (int j=0; j<data.size(); j++)
				System.out.print(data.get(j).getValue() + " ");
			System.out.println("-> " + comb.getValue());
		}
	}
	
	private static CH x1() throws Exception {
		CH f = new CH();
		f.setX("x", 1);
		f.add(new Sum(f).add("x_0"));
		f.prepare();
		return f;
	}

	private static CH trivial() throws Exception {
		CH f = new CH();
		f.setX("x", 2);
		Sum sum = new Sum(f).
				add("x_0").
				add("x_1").
				add(5);
		f.add(sum);
		f.prepare();
		return f;
	}
	
	private static CH trivialXY() throws Exception {
		CH f = new CH();
		f.setX("x", 2, 2);
		Sum sum = new Sum(f).
				add("x_0_0").
				add("x_0_1").
				add("x_1_0").
				add("x_1_1").
				add(5);
		f.add(sum);
		f.prepare();
		return f;
	}

	private static CH sudoku() throws Exception {
		CH f = new CH();
		f.setX("x", 64);

		Sum[] filas = {
			new Sum(f).add("x_0").add("x_2").add("x_4").add("x_6").add(-1),
			new Sum(f).add("x_1").add("x_3").add("x_5").add("x_7").add(-1),
			new Sum(f).add("x_8").add("x_10").add("x_12").add("x_14").add(-1),
			new Sum(f).add("x_9").add("x_11").add("x_13").add("x_15").add(-1),
			
			new Sum(f).add("x_16").add("x_18").add("x_20").add("x_22").add(-1),
			new Sum(f).add("x_17").add("x_19").add("x_21").add("x_23").add(-1),
			new Sum(f).add("x_24").add("x_26").add("x_28").add("x_30").add(-1),
			new Sum(f).add("x_25").add("x_27").add("x_29").add("x_31").add(-1),
			
			new Sum(f).add("x_32").add("x_34").add("x_36").add("x_38").add(-1),
			new Sum(f).add("x_33").add("x_35").add("x_37").add("x_39").add(-1),
			new Sum(f).add("x_40").add("x_42").add("x_44").add("x_46").add(-1),
			new Sum(f).add("x_41").add("x_43").add("x_45").add("x_47").add(-1),
			
			new Sum(f).add("x_48").add("x_50").add("x_52").add("x_54").add(-1),
			new Sum(f).add("x_49").add("x_51").add("x_53").add("x_55").add(-1),
			new Sum(f).add("x_56").add("x_58").add("x_60").add("x_62").add(-1),
			new Sum(f).add("x_57").add("x_59").add("x_61").add("x_63").add(-1),
		};
		
		Sum[] columnas = {
			new Sum(f).add("x_0").add("x_16").add("x_32").add("x_48").add(-1),
			new Sum(f).add("x_8").add("x_24").add("x_40").add("x_56").add(-1),
			new Sum(f).add("x_1").add("x_17").add("x_33").add("x_49").add(-1),
			new Sum(f).add("x_9").add("x_25").add("x_41").add("x_57").add(-1),
			
			new Sum(f).add("x_2").add("x_18").add("x_34").add("x_50").add(-1),
			new Sum(f).add("x_10").add("x_26").add("x_42").add("x_58").add(-1),
			new Sum(f).add("x_3").add("x_19").add("x_35").add("x_51").add(-1),
			new Sum(f).add("x_11").add("x_27").add("x_43").add("x_59").add(-1),
			
			new Sum(f).add("x_4").add("x_20").add("x_36").add("x_52").add(-1),
			new Sum(f).add("x_12").add("x_28").add("x_44").add("x_60").add(-1),
			new Sum(f).add("x_5").add("x_21").add("x_37").add("x_53").add(-1),
			new Sum(f).add("x_13").add("x_29").add("x_45").add("x_61").add(-1),
			
			new Sum(f).add("x_6").add("x_22").add("x_38").add("x_54").add(-1),
			new Sum(f).add("x_14").add("x_30").add("x_46").add("x_62").add(-1),
			new Sum(f).add("x_7").add("x_23").add("x_39").add("x_55").add(-1),
			new Sum(f).add("x_15").add("x_31").add("x_47").add("x_63").add(-1)
		};
		
		int[][] sumasRecuadros = {
				{ 0, 2, 16, 18 }, { 1, 3, 17, 19 }, { 8, 10, 24, 26 }, { 9, 11, 25, 27 },
				{ 4, 6, 20, 22 }, { 5, 7, 21, 23 }, { 12, 14, 28, 30 }, { 13, 15, 29, 31 },
				{ 32, 34, 48, 50 }, { 33, 35, 49, 51 }, { 40, 42, 56, 58 }, { 41, 43, 57, 59 },
				{ 36, 38, 52, 54 }, { 37, 39, 53, 55 }, { 44, 46, 60, 62 }, { 45, 47, 61, 63 }
		};
		
		Sum[] recuadros = new Sum[16];
		int cont = 0;
		for (int i=0; i<16; i++) {
			Sum recuadro = new Sum(f);
			for (int j=0; j<4; j++) {
				String x = "x_" + sumasRecuadros[i][j];
				recuadro.add(x);
			}
			recuadro.add(-1);
			recuadros[cont++] = recuadro;
		}
		
		Lambda lambdaFilas = new Lambda(f, 10);
		ResultExpr[] constraints = new ResultExpr[filas.length + columnas.length + recuadros.length];
		for (int i=0; i<filas.length; i++) {
			Square square = new Square(f);
			square.setExpr(filas[i]);
			constraints[i] = new Product(f, lambdaFilas, square);
		}
		
		Lambda lambdaColumnas = new Lambda(f, 10);
		for (int i=0; i<columnas.length; i++) {
			Square square = new Square(f);
			square.setExpr(columnas[i]);
			constraints[filas.length + i] = new Product(f, lambdaColumnas, square);;
		}
		
		Lambda lambdaRecuadros = new Lambda(f, 10);
		for (int i=0; i<recuadros.length; i++) {
			Square square = new Square(f);
			square.setExpr(recuadros[i]);
			constraints[filas.length + columnas.length + i] = new Product(f, lambdaRecuadros, square);
		}		
				
		f.setZero(0, 1, 2, 3, 5, 6, 
				9, 10, 12, 13, 14, 15, 
				17, 18, 20, 21, 23, 
				24, 26, 27, 29, 
				33, 34, 35, 36, 39, 
				40, 43, 44, 45, 46, 
				48, 51, 52, 53, 54, 55,
				56, 57, 58, 59, 60, 63);
		
		f.setOne(4, 8, 11, 19, 37, 50, 61, 62);
		
		f.add(constraints);

		return f;
	}
	
	private static CH cajas2() throws Exception {
		CH f = new CH();
		f.setX("x", 2);

		Sum sum = new Sum(f, 
			new Product(f, new IndexedVariableValue(f, "p", 0), new UseOfX(f, "x_0")),
			new Product(f, new IndexedVariableValue(f, "p", 1), new UseOfX(f, "x_1"))
		);
		
		Sum sum2 = new Sum(f, 
			new Product(f, new IndexedVariableValue(f, "w", 0), new UseOfX(f, "x_0")),
			new Product(f, new IndexedVariableValue(f, "w", 1), new UseOfX(f, "x_1")),
			new DoubleValue(f, -6)
		);
		Square sq = new Square(f).setExpr(sum2);		
		
		f.add(sum.setLambda(1), sq.setLambda(1));
		f.setVariableValues("w", 1.0, 2.0);
		f.setVariableValues("p", -4.0, -1.0);
		
		f.prepare();
		return f;
	}
	
	private static CH cajas5() throws Exception {
		CH f = new CH();
		f.setX("Boxes", 5);

		Sum sum = new Sum(f, 
			new Product(f, new IndexedVariableValue(f, "Prices", 0), new UseOfX(f, "Boxes_0")),
			new Product(f, new IndexedVariableValue(f, "Prices", 1), new UseOfX(f, "Boxes_1")),
			new Product(f, new IndexedVariableValue(f, "Prices", 2), new UseOfX(f, "Boxes_2")),
			new Product(f, new IndexedVariableValue(f, "Prices", 3), new UseOfX(f, "Boxes_3")),
			new Product(f, new IndexedVariableValue(f, "Prices", 4), new UseOfX(f, "Boxes_4"))
		);
		
		Sum sum2 = new Sum(f, 
			new Product(f, new IndexedVariableValue(f, "Weights", 0), new UseOfX(f, "Boxes_0")),
			new Product(f, new IndexedVariableValue(f, "Weights", 1), new UseOfX(f, "Boxes_1")),
			new Product(f, new IndexedVariableValue(f, "Weights", 2), new UseOfX(f, "Boxes_2")),
			new Product(f, new IndexedVariableValue(f, "Weights", 3), new UseOfX(f, "Boxes_3")),
			new Product(f, new IndexedVariableValue(f, "Weights", 4), new UseOfX(f, "Boxes_4")),
			new DoubleValue(f, -10)
		);
		Square sq = new Square(f).setExpr(sum2);		
		
		f.add(sum, sq.setLambda(2.0));
		f.setVariableValues("Weights", 1.0, 2.0, 3.0, 4.0, 4.0);
		f.setVariableValues("Prices", 4.0, 1.0, 2.0, 3.0, 5.0);
		
		f.prepare();
		return f;
	}
}
