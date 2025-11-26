import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Circuit } from './model/Circuit';
import { ManagerService } from './manager.service';
import { Mutant } from './model/Mutant';
import { DictionaryService } from './dictionary.service';
import { Curl } from './model/Curl';

@Injectable({
  providedIn: 'root'
})
export class QumugenService {
  constructor(private dict : DictionaryService, private client : HttpClient, private manager : ManagerService) { }

  getOperatorsByFamily() {
    return this.client.get<any>(this.dict.getQumugenURL()+ "getOperatorsByFamily")
  }

  getOperators(family : string) {
    return this.client.get(this.dict.getQumugenURL() + "getOperators/" + family)
  }

  generateMutants(circuit : Circuit, selectedOperators : any[]) {
    let info = {
      circuit : circuit.quirkCode,
      operatorNames : selectedOperators,
      mutableColumns : circuit.mutableColumns,
      mutableRows : circuit.mutableRows,
      inputQubits : this.manager.inputQubits,
      generateWithAllInputs : this.manager.generateWithAllInputs
    }
    return this.client.put<any>(this.dict.getQumugenURL() + "generateQuirkMutants", info) 
  }

  async getQiskitCode(circuit : Circuit) {
    circuit.inputQubits = this.manager.inputQubits
    circuit.outputQubits = this.manager.outputQubits
    try {
      let code : Promise<any> = this.client.put<any>(this.dict.getQumugenURL() + "getQiskitCode?useTemplate=true&shots=" + this.manager.shots + 
        "&qiskitTemplate=" + this.manager.selectedQiskitTemplate, circuit).toPromise()
      return code
    } catch(error) {
      throw error
    }
  }

  getMultipleQiskitCode(mutants : Mutant[]) {
    let info = {
      outputQubits : this.manager.outputQubits,
      mutants : mutants
    }
    return this.client.put<any[]>(this.dict.getQumugenURL() + "getMultipleQiskitCode?shots=" + this.manager.shots + "&qiskitTemplate=" + this.manager.selectedQiskitTemplate, info)
  }

  buildUnexCurls(id: string, mutants: Mutant[]) {
    let info = {
      circuitId : id,
      mutants : mutants
    }
    return this.client.post<Curl[]>(this.dict.getQumugenURL() + "buildUnexCurls", info)
  }

  sendToUnex(curl: Curl) {
    curl.result = -1
    return this.client.post(this.dict.getQumugenURL() + "sendToUnex", curl)
  }

  getQiskitTemplates() {
    return this.client.get(this.dict.getQiskitURL() + "simple/getQiskitTemplates")
  }
}
