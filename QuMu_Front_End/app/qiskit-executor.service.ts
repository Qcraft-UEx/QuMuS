import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Circuit } from './model/Circuit';
import { ManagerService } from './manager.service';
import { DictionaryService } from './dictionary.service';

@Injectable({
  providedIn: 'root'
})
export class QiskitExecutorService {
  constructor(private dict : DictionaryService, private client : HttpClient, private manager : ManagerService) { }

  runOne(program : Circuit, inputQubits : string, outputQubits : string, algorithm : string, qubits : number, reduceTable : boolean, inputs? : string[]) {
    let info = {
      program : program,
      inputQubits : inputQubits,
      outputQubits : outputQubits,
      qubits : qubits,
      algorithm : algorithm,
      reduceTable : reduceTable,
      inputs : inputs
    }
    let url = this.dict.getQiskitURL() + (algorithm=="Simple" ? "simple/executeOne" : "strategy/executeOne")
    return this.client.put<any>(url, info)
  }

  getCores() {
    return this.client.get<number>(this.dict.getQiskitURL() + "strategy/getCores")
  }

  executeWithoutStrategy(mutants : any[], originalResults : any[], algorithm : string, toleratedError : number) {
    let capped = []
    for (let i=0; i<mutants.length; i++) {
      capped.push({
        wholeCode : mutants[i].wholeCode,
        mutantIndex : mutants[i].mutantIndex
      })
    }
    let info = {
      outputQubits : this.manager.outputQubits,
      algorithm : algorithm,
      toleratedError : toleratedError,
      originalResults : originalResults,
      qubits : this.manager.selectedCircuit?.qubits,
      mutants : capped
    }
    let url = this.dict.getQiskitURL() + "simple/executeWithoutStrategy"
    return this.client.put<any>(url, info)
  }

  executeWithStrategy(mutants : any[], originalResults : any[], algorithm : string, toleratedError : number, reduceTable : boolean, inputs? : string[]) {
    let capped = []
    for (let i=0; i<mutants.length; i++) {
      capped.push({
        wholeCode : mutants[i].wholeCode,
        mutantIndex : mutants[i].mutantIndex
      })
    }
    let info = {
      inputQubits : this.manager.inputQubits,
      outputQubits : this.manager.outputQubits,
      algorithm : algorithm,
      toleratedError : toleratedError,
      originalResults : originalResults,
      qubits : this.manager.selectedCircuit?.qubits,
      mutants : capped,
      reduceTable : reduceTable,
      inputs : inputs
    }
    let url = this.dict.getQiskitURL() + "strategy/executeWithStrategy"
    return this.client.put<any>(url, info)
  }
}
