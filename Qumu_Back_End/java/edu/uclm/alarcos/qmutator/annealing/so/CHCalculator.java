package edu.uclm.alarcos.qmutator.annealing.so;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import edu.uclm.alarcos.qmutator.annealing.CH;
import edu.uclm.alarcos.qmutator.annealing.Combination;

public class CHCalculator {

	private CH ch;
	private String workingFolder;

	public CHCalculator(CH ch) {
		this.ch = ch;
		this.workingFolder = System.getProperty("java.io.tmpdir");
		this.workingFolder = this.workingFolder.replace('\\', '/');
		if (!this.workingFolder.endsWith("/"))
			this.workingFolder = this.workingFolder + "/";
		this.workingFolder = this.workingFolder + "qumu/";
	}

	public List<Combination> calculate() {
		int cores = Runtime.getRuntime().availableProcessors();
		int combinations = ch.getNumberOfCombinations();
		int load = combinations/cores;
		
		List<Combination> result = new Vector<>();
		
		CombinationSetCalculator[] ccc = new CombinationSetCalculator[cores]; 
		Thread[] tt = new Thread[cores];
		int currentLoadStart = 0;
		for (int i=0; i<cores-1; i++) {
			CombinationSetCalculator cc = new CombinationSetCalculator(result, ch, currentLoadStart, currentLoadStart + load);
			currentLoadStart = currentLoadStart + load + 1;
			tt[i] = new Thread(cc);
			ccc[i] = cc;
		}
		
		CombinationSetCalculator cc = new CombinationSetCalculator(result, ch, currentLoadStart, combinations-1);
		//CombinationSetCalculator cc = new CombinationSetCalculator(best50, ch, 17490, 17491);

		tt[cores-1] = new Thread(cc);
		ccc[cores-1] = cc;
		
		for (int i=0; i<cores; i++) {
			tt[i].start();
		}
		
		for (int i=0; i<cores; i++) {
			try {
				tt[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		result.sort(new Comparator<Combination>() {
			@Override
			public int compare(Combination o1, Combination o2) {
				return (int) (o1.getValue()-o2.getValue());
			}
		});
		
		List<Combination> best50 = new ArrayList<>();
		int n = result.size()<50 ? result.size() : 50;
		for (int i=0; i<n; i++)
			best50.add(result.get(i));
		
		return best50;
	}
	
}
