import { Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ReperService } from '../reper.service';
import { Circuit } from '../model/Circuit';
import { AppComponent } from '../app.component';
import { ManagerService } from '../manager.service';
import { QumugenService } from '../qumugen.service';
import { QasmService } from '../qasm.service';

@Component({
  selector: 'app-circuit',
  templateUrl: './circuit.component.html',
  styleUrls: ['./circuit.component.css']
})
export class CircuitComponent implements OnInit {

  url  : SafeResourceUrl

  qiskitURL : string = "https://quantum-circuit.com/api/get/circuit/ARcZq5J2vNuktzRD6?format=qiskit"

  randomQubits : number = 5
  randomColumns : number = 5
  randomButDeterministic : boolean = false
  randomAndStartWithH : boolean = true

  originalCircuitName? : string
  selectedCircuit : Circuit = new Circuit()
  hideQuirk : boolean = true

  circuits : Circuit[] = []

  constructor(public sanitizer: DomSanitizer, private reper : ReperService, private manager : ManagerService, private qumugen : QumugenService, private qasm : QasmService) { 
    this.url = sanitizer.bypassSecurityTrustResourceUrl(AppComponent.quirkUrl)
  }

  ngOnInit(): void {
    this.reper.getCircuits().subscribe(
      circuits => {
        AppComponent.error = ""
        for (let i=0; i<circuits.length; i++) {
          this.circuits.push(new Circuit(circuits[i].id, circuits[i].quirkCode))
        }
      },
      error => {
        AppComponent.error = error.error ? error.error.message : error
      }
    )
  }

  importQiskit() {
    if (this.qiskitURL) {
      this.qasm.import(this.qiskitURL).subscribe(
        code => {
          this.selectedCircuit.buildFromQiskitCode(code)
        },
        error => {
          AppComponent.error = error.error ? error.error.message : error
        }
      )
    }
  } 

  draw() {
    this.url = this.sanitizer.bypassSecurityTrustResourceUrl(AppComponent.quirkUrl + "#circuit=" + this.selectedCircuit.textQuirkCode)
    this.hideQuirk = false
  }

  save() {
    if (this.selectedCircuit.id.trim().length==0) {
      AppComponent.error = "Please, give a name to the circuit"
      return
    }
    if (!this.selectCircuit) {
      AppComponent.error = "Please, write the Quirk code of the circuit"
      return
    }
    this.selectedCircuit.quirkCode=JSON.parse(this.selectedCircuit.textQuirkCode)
    this.reper.save(this.selectedCircuit).subscribe(
      result => {
        AppComponent.error = ""
      },
      error => {
        AppComponent.error = error.error ? error.error.message : error
      }
    )
  }

  selectCircuit() {
    this.selectedCircuit = this.circuits.filter(c => c.id==this.originalCircuitName).at(0)!
    this.manager.setSelectedCircuit(this.selectedCircuit)
    this.qumugen.getQiskitCode(this.selectedCircuit).then(
      result=> {
        this.selectedCircuit.qiskitCode = result.wholeCode.split("\n")
      }
    )
  }

  generateRandomCircuit() {
    this.selectedCircuit.randomize(this.randomQubits, this.randomColumns, this.randomButDeterministic, this.randomAndStartWithH)
  }

  generateExtremaduraCircuit() {
    this.selectedCircuit.generateExtremaduraCircuit(this.randomQubits, this.randomColumns)
  }
}
