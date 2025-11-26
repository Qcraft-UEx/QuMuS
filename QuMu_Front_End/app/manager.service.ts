import { Injectable } from '@angular/core';
import { Circuit } from './model/Circuit';
import { Mutant } from './model/Mutant';

@Injectable({
  providedIn: 'root'
})
export class ManagerService {
  selectedCircuit? : Circuit
  mutants : Mutant[] = []

  selectedMutant? : Mutant
  showMutantCode : boolean = false

  inputQubits : string = ""
  outputQubits : string = ""
  shots : number = 100

  executionAlgorithm : string = "Simple"
  toleratedError : number = 0.05
  generateWithAllInputs: boolean = true

  selectedQiskitTemplate? : string
  qiskitTemplates : string[] = []

  setSelectedCircuit(circuit : Circuit) {
    this.selectedCircuit = circuit
    let qubits = this.selectedCircuit.getQubits()
    this.inputQubits = ""
    this.outputQubits = ""
    for (let i=0; i<qubits; i++) {
      this.inputQubits = this.inputQubits + i + ","
      this.outputQubits = this.outputQubits + i + ","
    }
    if (this.inputQubits.endsWith(","))
      this.inputQubits = this.inputQubits.substring(0, this.inputQubits.length-1)
    this.selectedCircuit.inputQubits = this.inputQubits

    if (this.outputQubits.endsWith(","))
      this.outputQubits = this.outputQubits.substring(0, this.outputQubits.length-1)
    this.selectedCircuit.outputQubits = this.outputQubits
  }

  setMutants(mutants: any) {
    this.mutants = []
    for (let i=0; i<mutants.length; i++) {
      let mutant = new Mutant(mutants[i])
      this.mutants.push(mutant)
    }
  }

  getNumberOfInputQubits() {
    return this.inputQubits.split(",").length
  }
}
