import { Component } from '@angular/core';
import { ManagerService } from '../manager.service';
import { QiskitExecutorService } from '../qiskit-executor.service';
import { QumugenService } from '../qumugen.service';
import { Circuit } from '../model/Circuit';
import { AppComponent } from '../app.component';
import { MutantsExecutor } from '../MutantsExecutor';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-simple-result',
  templateUrl: './simple-results.component.html',
  styleUrls: ['./simple-results.component.css']
})
export class SimpleResultsComponent extends MutantsExecutor {

  constructor(public override sanitizer: DomSanitizer, public manager : ManagerService, public qe : QiskitExecutorService, private qumugen : QumugenService) {
    super(sanitizer)
  }

  runOne(circuit : Circuit, program? : string) {
    AppComponent.error = ""
    if (this.stopped)
      return
    this.showModal("Executing original")

    let outputConsole = document.getElementById("consoles")
    outputConsole!.innerHTML = "<i>Results will appear here</i>"
    outputConsole!.scrollIntoView({behavior: 'smooth'});

    this.qe.runOne(circuit, this.manager.inputQubits, this.manager.outputQubits, this.manager.executionAlgorithm, this.manager.selectedCircuit!.qubits, false).subscribe(
      result => {
        this.hideModal()
        outputConsole!.innerHTML=""
        if (program=="original")
          this.originalResults = result[0].executionResults
        else
          this.mutantResults = result[0].executionResults
      },
      error => {
        AppComponent.error = error.error ? error.error.message : error
        this.hideModal()
      }
    )
  }

  override runMutants() {
    AppComponent.error = ""
    this.runningMutants = true
    this.originalResults = []
    this.mutantResults = []
    this.aliveMutants = 0
    this.killedMutants = 0

    if (this.stopped)
      return

    this.showModal("Excuting original")

    this.qe.runOne(this.manager.selectedCircuit!, this.manager.inputQubits,  this.manager.outputQubits, this.manager.executionAlgorithm, this.manager.selectedCircuit!.qubits, false).subscribe(
      originalResults => {
        this.hideModal()
        if (this.stopped)
          return

        this.originalResults = originalResults
        let header1 = document.getElementById("header1")
        let header2 = document.getElementById("header2")
        let children = header1!.childElementCount
        for (let i=1; i<children; i++) {
          let child = header1?.childNodes.item(1)
          header1?.removeChild(child!)
          child = header2?.childNodes.item(3)
          header2?.removeChild(child!)
        }
        if (this.stopped)
          return

        this.qe.getCores().subscribe(
          result => {
            let chunkSize = 2*result
            this._runMutants(0, chunkSize)
          },
          error => {
            this.hideModal()
            throw error
          }
        )
      }
    )
  }

  private _runMutants(start : number, chunkSize : number) {
    let end = start + chunkSize
    if (end>this.manager.mutants.length)
      end = this.manager.mutants.length

    this.showModal(`Running mutants from ${start} to ${end}`); // Mostrar el modal con el rango de mutantes

    let mutants = this.manager.mutants.slice(start, end)
    if (mutants.length>0) {
      this.qumugen.getMultipleQiskitCode(mutants).subscribe(
        results => {
            this.qe.executeWithoutStrategy(results, this.originalResults,  this.manager.executionAlgorithm, this.manager.toleratedError).subscribe(
              result=> {
                this.drawSimple(result)
                start = start + chunkSize
                if (this.stopped)
                  return

                if (start >= this.manager.mutants.length) {
                  this.hideModal(); // Ocultar el modal cuando termine la ejecución de todos los mutantes
                } else {
                  this._runMutants(start, chunkSize); // Continuar con el siguiente lote de mutantes
                }
              },
              error => {
                this.hideModal()
                throw error
              }
            )
        },
        error => {
          this.hideModal()
          throw error
        }
      )
    }
  }

  private drawSimple(result : any) {
    for (let i=0; i<result.length; i++) {
      let header1 = document.getElementById("header1")
      let th = document.createElement("th")
      header1!.appendChild(th)
      th.innerHTML = "m" + result[i].mutantIndex
      if (result[i].killed)
        th.setAttribute("style", "color : red")
      else
        th.setAttribute("style", "color : lightgreen")
      let header2 = document.getElementById("header2")
      th = document.createElement("th")
      header2!.appendChild(th)
      th.innerHTML = result[i].error
      if (result[i].killed) {
        th.setAttribute("style", "color : red")
        this.killedMutants++
      } else {
        th.setAttribute("style", "color : lightgreen")
        this.aliveMutants++
      }

      for (let j=0; j<result[i].executionResults.length; j++) {
        let tr = document.getElementById("row" + j)
        let td = document.createElement("td")
        td.setAttribute("align", "right")
        td.innerHTML = result[i].executionResults[j].frequency
        if (result[i].executionResults[j].frequency>0)
          td.setAttribute("style", "background-color : lightgreen")
        tr!.appendChild(td)
      }
    }
  }
}