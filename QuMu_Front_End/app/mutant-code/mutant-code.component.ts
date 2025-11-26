import { Component } from '@angular/core';
import { ManagerService } from '../manager.service';
import { DomSanitizer } from '@angular/platform-browser';
import { AppComponent } from '../app.component';
import { Circuit } from '../model/Circuit';
import { QiskitExecutorService } from '../qiskit-executor.service';
import { MutantsExecutor } from '../MutantsExecutor';

@Component({
  selector: 'app-mutant-code',
  templateUrl: './mutant-code.component.html',
  styleUrls: ['./mutant-code.component.css']
})
export class MutantCodeComponent extends MutantsExecutor {

  hideQuirk : boolean = true
  hideQiskit : boolean = false

  constructor(public override sanitizer: DomSanitizer, public manager : ManagerService, private qe : QiskitExecutorService) { 
    super(sanitizer)
    this.url = sanitizer.bypassSecurityTrustResourceUrl(AppComponent.quirkUrl)
    this.JSON = JSON
  }

  showOrHideQuirk() {
    this.hideQuirk = !this.hideQuirk
    this.url = this.sanitizer.bypassSecurityTrustResourceUrl(AppComponent.quirkUrl + "#circuit=" + this.manager.selectedMutant?.circuit?.textQuirkCode)
  }

  runOne(circuit : Circuit, program? : string) {
    AppComponent.error = ""
    let outputConsole = document.getElementById("consoles")
    outputConsole!.innerHTML = "<i>Results will appear here</i>"
    outputConsole!.scrollIntoView({behavior: 'smooth'});

    this.qe.runOne(circuit, this.manager.inputQubits, this.manager.outputQubits, "Simple", this.manager.selectedCircuit!.qubits, false).subscribe(
      result => {
        outputConsole!.innerHTML=""
        if (program=="original")
          this.originalResults = result.executionResults
        else
          this.mutantResults = result.executionResults
      },
      error => {
        AppComponent.error = error.error ? error.error.message : error
      }
    )
  }

  override runMutants(): void {
      
  }
}
