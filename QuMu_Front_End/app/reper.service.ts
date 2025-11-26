import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Circuit } from './model/Circuit';
import { DictionaryService } from './dictionary.service';
import { Mutant } from './model/Mutant';

@Injectable({
  providedIn: 'root'
})
export class ReperService {
  private right : string = "?db=quantum_mutation&collection="

  constructor(private dict : DictionaryService, private client : HttpClient) { }

  getCircuits() {
    return this.client.get<any>(this.dict.getReperURL() + "findAll" + this.right + "circuits")
  }

  save(circuit : Circuit) {
    return this.client.put<any>(this.dict.getReperURL() + "saveJSON" + this.right + "circuits", circuit)
  }

  saveMutants(id: string, mutants: Mutant[]) {
    let info = {
      circuitId : id,
      mutants : mutants
    }
    return this.client.put<any>(this.dict.getReperURL() + "saveJSONs" + this.right + "mutants", info)
  }
}