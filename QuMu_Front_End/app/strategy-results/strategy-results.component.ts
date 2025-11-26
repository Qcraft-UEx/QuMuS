import { Component } from '@angular/core';
import { ManagerService } from '../manager.service';
import { Circuit } from '../model/Circuit';
import { AppComponent } from '../app.component';
import { QiskitExecutorService } from '../qiskit-executor.service';
import { QumugenService } from '../qumugen.service';
import { MutantsExecutor } from '../MutantsExecutor';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-strategy-results',
  templateUrl: './strategy-results.component.html',
  styleUrls: ['./strategy-results.component.css']
})
export class StrategyResultsComponent extends MutantsExecutor {

  numberOfRows : number = 0
  reduceTable : boolean = true
  currentMutant : number = 0

  currentInput : string = ""
  inputs : string[] = []

  constructor(public override sanitizer: DomSanitizer, public manager : ManagerService, public qe : QiskitExecutorService, private qumugen : QumugenService) {
    super(sanitizer)
  }

  runOne(circuit : Circuit, program? : string) { 
    AppComponent.error = ""
    
    if (this.stopped) {
      this.hideModal()
      return
    }

    let outputConsole = document.getElementById("consoles")
    outputConsole!.innerHTML = "<i>Results will appear here</i>"
    outputConsole!.scrollIntoView({behavior: 'smooth'});

    this.qe.runOne(circuit, this.manager.inputQubits, this.manager.outputQubits, this.manager.executionAlgorithm, this.manager.selectedCircuit!.qubits, this.reduceTable).subscribe(
      result => {
        this.hideModal();  // Ocultar el modal cuando termina
        if (this.stopped) {
          this.hideModal()
          return
        }

        outputConsole!.innerHTML=""
        if (program=="original")
          this.originalResults = result.executionResults
        else
          this.mutantResults = result.executionResults
      },
      error => {
        AppComponent.error = error.error ? error.error.message : error
        this.hideModal();  // Ocultar el modal cuando termina
      }
    )
  }

  runMutants() {
    AppComponent.error = ""
    this.runningMutants = true
    this.originalResults = []
    this.mutantResults = []
    this.aliveMutants = this.manager.mutants.length
    this.killedMutants = 0
    this.nonCompilableMutants = 0
    this.numberOfRows = 0
    this.currentMutant = 0

    if (this.stopped) {
      this.hideModal()
      return
    }

    this.showModal("Executing original");  // Mostrar el modal
    this.qe.runOne(this.manager.selectedCircuit!, this.manager.inputQubits, this.manager.outputQubits, this.manager.executionAlgorithm, this.manager.selectedCircuit!.qubits, this.reduceTable, this.inputs).subscribe(
      originalResults => {
        this.createTable()
        this.originalResults = originalResults
        if (this.reduceTable)
          this.drawOriginalsResuming(originalResults)
        else
          this.drawOriginals(originalResults)

        if (this.stopped) {
          this.hideModal()
          return
        }
        this.qe.getCores().subscribe(
          result => {
            let chunkSize = 2*result
            if (Math.pow(2, this.manager.selectedCircuit!.qubits)>chunkSize)
              chunkSize=1
            this._runMutants(0, chunkSize)
          },
          error => {
            throw error
          }
        )
      }
    )
  }

  private _runMutants(start : number, chunkSize : number) {
    if (this.stopped) {
      this.hideModal()
      return
    }

    let end = start + chunkSize
    if (end>this.manager.mutants.length)
      end = this.manager.mutants.length

    this.showModal(`Running mutants from ${start} to ${end}`);  // Mostrar el modal con el rango de mutantes

    let mutants = this.manager.mutants.slice(start, end)
    if (mutants.length>0) {
      this.qumugen.getMultipleQiskitCode(mutants).subscribe(
        results => {
          for (let i=results.length-1; i>=0; i--) {
            if (results[i].executable===false) {
              results.splice(i, 1)
              this.nonCompilableMutants++
            }
          }
          if (results.length==0) {
            let tr = document.getElementById("m0_0")
            let td = document.createElement("td")
            td.innerHTML = "N/C"
            td.setAttribute("rowspan", "" + this.numberOfRows)
            td.setAttribute("valign", "top")
            tr?.appendChild(td)
            start = start + chunkSize
            this.currentMutant = end
            this._runMutants(start, chunkSize)
          } else {
            this.qe.executeWithStrategy(results, this.originalResults,  this.manager.executionAlgorithm, this.manager.toleratedError, this.reduceTable, this.inputs).subscribe(
              result=> {
                if (this.reduceTable)
                  this.drawMutantsResuming(start, result)
                else
                  this.drawMutants(start, result)
                start = start + chunkSize
                this.currentMutant = end

                if (start >= this.manager.mutants.length) {
                  this.hideModal();  // Ocultar el modal cuando todos los mutantes se ejecutan
                } else {
                  this._runMutants(start, chunkSize);
                }
              },
              error => {
                this.hideModal();  // Ocultar el modal en caso de error
                throw error
              }
            )
          }
        },
        error => {
          this.hideModal();  // Ocultar el modal en caso de error
          throw error
        }
      )
    } else {
      this.hideModal();  // Ocultar el modal si no hay mutantes para ejecutar
    }
  }

  private drawOriginals(results : any) {
    let table = document.getElementById("resultsTable")
    for (let i=0; i<results.length; i++) {
      let result = results[i]
      for (let j=0; j<result.executionResults.length; j++) {
        let tr = document.createElement("tr")
        this.numberOfRows++
        tr.setAttribute("id", "m" + i + "_" + j)
        table?.appendChild(tr)
        let td
        if (j==0) {
          td = document.createElement("td")
          td.innerHTML = result.mutantIndex + ("<br><sub>" + this.binary(result.mutantIndex, this.manager.selectedCircuit!.getNumberOfInputQubits()) + "</sub>")
          td.rowSpan = result.executionResults.length
          tr.appendChild(td)
          td.setAttribute("style", "border-bottom : solid 2px; border-right : solid 2px")
        }
        td = document.createElement("td")
        td.innerHTML = result.executionResults[j].order + "<sub>" + this.binary(result.executionResults[j].order, this.manager.selectedCircuit!.getNumberOfInputQubits()-1) + "</sub>"
        td.align = "right"
        if (j==result.executionResults.length-1)
          td.setAttribute("style", "border-bottom : solid 2px")
        tr.appendChild(td)
        td = document.createElement("td")
        td.innerHTML = result.executionResults[j].frequency
        if (result.executionResults[j].frequency>0)
          td.setAttribute("style", "background-color : lightgreen")
        td.align = "center"
        tr.appendChild(td)
        if (j==result.executionResults.length-1)
          td.setAttribute("style", "border-bottom : solid 2px")
      }
    }
  }

  private drawOriginalsResuming(results : any) {
    let table = document.getElementById("resultsTable")
    for (let i=0; i<results.length; i++) {
      let result = results[i]
      let tr = document.createElement("tr")
      this.numberOfRows++
      tr.setAttribute("id", "m" + i)
      table?.appendChild(tr)
      let tdInput = document.createElement("td")
      tdInput.innerHTML = result.mutantIndex + ("<br><sub>" + this.binary(result.mutantIndex, this.manager.selectedCircuit!.getNumberOfInputQubits()) + "</sub>")
      tr.appendChild(tdInput)

      let tdValue = document.createElement("td")
      tr.appendChild(tdValue)

      let tdFrequency = document.createElement("td")
      tr.appendChild(tdFrequency)

      for (let j=0; j<result.executionResults.length; j++) {
        if (result.executionResults[j].frequency==0)
          continue
        let divValue = document.createElement("div")
        divValue.innerHTML = result.executionResults[j].order + "<sub>" + this.binary(result.executionResults[j].order, this.manager.selectedCircuit!.getNumberOfInputQubits()-1) + "</sub>"
        
        let divFrequency  = document.createElement("div")
        divFrequency.innerHTML = result.executionResults[j].frequency
        
        tdValue.appendChild(divValue)
        tdFrequency.appendChild(divFrequency)
      }
    }
  }

  private binary(n : number, length : number) {
    let r = n.toString(2)
    for (let i=r.length; i<length; i++)
      r = "0" + r
    return r
  }

  private drawMutants(start : number, mutantResults : any) {
    let cont = start-1
    for (let i=0; i<mutantResults.length; i++) {
      cont = cont + 1
      let killed = false
      let th = document.getElementById("hm" + cont)
      let mutantResult = mutantResults[i]
      for (let j=0; j<mutantResult.length; j++) {
        let executionResults = mutantResult[j].executionResults
        for (let k=0; k<executionResults.length; k++) {
          let executionResult = executionResults[k]
          let tr = document.getElementById("m" + j + "_" + k)
          let td = document.createElement("td")
          td.innerHTML = executionResult.frequency
          td.align = "center"
          tr?.appendChild(td)
          if (mutantResult[j].killed) {
            td.setAttribute("style", td.getAttribute("style") + "; color : red")
            if (!killed) {
              th!.setAttribute("style", th!.getAttribute("style") + "; color : red")
              killed = true
              this.killedMutants++
              this.aliveMutants--
            }
          } else
            td.setAttribute("style", td.getAttribute("style") + "; color : green")    
          if (executionResult.frequency>0)
            td.setAttribute("style", td.getAttribute("style") + "; background-color : lightgreen") 
          if (k==executionResults.length-1)
            td.setAttribute("style", td.getAttribute("style") + "; border-bottom : solid 2px")
        }
      }
      if (killed) {
        document.getElementById("gm" + cont)?.setAttribute("style", "color : red")
      } else {
        document.getElementById("gm" + cont)?.setAttribute("style", "color : green")
      }
    }
  }

  private drawMutantsResuming(start : number, mutantResults : any) {
    let cont = start-1
    for (let i=0; i<mutantResults.length; i++) {
      cont = cont + 1
      let killed = false
      let th = document.getElementById("hm" + cont)
      let mutantResult = mutantResults[i]
      for (let j=0; j<mutantResult.length; j++) {
        let tr = document.getElementById("m" + j)
        let td = document.createElement("td")
        td.innerHTML = mutantResult[j].error
        tr?.appendChild(td)
        if (mutantResult[j].killed && !killed) {
          killed = true
          this.killedMutants++
          this.aliveMutants--
          th?.setAttribute("style", th.getAttribute("style") + "; color : red")
        } 
      }
      if (killed) {
        document.getElementById("gm" + cont)?.setAttribute("style", "color : red")
      } else {
        document.getElementById("gm" + cont)?.setAttribute("style", "color : green")
      }
    }
  }

  private createTable() {
    let span = document.getElementById("resultsSpan")
    span!.innerHTML = ""
    let table = document.createElement("table")
    table.id = "resultsTable"
    span?.appendChild(table)
    let tr = document.createElement("tr")
    tr.id = "header1"
    table.appendChild(tr)
    let th = document.createElement("th")
    tr.appendChild(th)
    th.setAttribute("colspan", "3")
    th.setAttribute("style", "border : solid 2px")
    th.innerHTML = "Original"
    th = document.createElement("th")
    tr.appendChild(th)
    th.setAttribute("id", "headerMutants")
    th.innerHTML = "Mutants"
    th.setAttribute("style", "border : solid 2px")
    th.setAttribute("colspan", "" + this.manager.mutants.length)

    tr = document.createElement("tr")
    tr.id = "header2"
    table.appendChild(tr)
    th = document.createElement("th")
    tr.appendChild(th)
    th.setAttribute("rowspan", "2")
    th.innerHTML = "Input"
    th.setAttribute("style", "border-left : solid 2px; border-bottom : solid 2px")
    th = document.createElement("th")
    tr.appendChild(th)
    th.setAttribute("colspan", "2")
    th.innerHTML = "Output"
    th.setAttribute("style", "border-left : solid 2px; border-bottom : solid 2px")

    for (let i=0; i<this.manager.mutants.length; i++) {
      th = document.createElement("th")
      th.innerHTML = "m" + i
      th.setAttribute("id", "hm" + i)
      th.setAttribute("rowspan", "2")
      if (i==this.manager.mutants.length-1)
        th.setAttribute("style", "border-left : solid 2px; border-bottom : solid 2px; border-right : solid 2px")
      else
        th.setAttribute("style", "border-left : solid 2px; border-bottom : solid 2px")
      tr.appendChild(th)
    }

    tr = document.createElement("tr")
    tr.id = "header3"
    table.appendChild(tr)
    th = document.createElement("th")
    tr.appendChild(th)
    th.innerHTML = "Value"
    th.setAttribute("style", "border-left : solid 2px; border-bottom : solid 2px")
    th = document.createElement("th")
    tr.appendChild(th)
    th.innerHTML = "Frequency"    
    th.setAttribute("style", "border-left : solid 2px; border-bottom : solid 2px; border-right : solid 2px")
  }

  addInput() {
    if (this.currentInput.trim().length>0)
      this.inputs.push(this.currentInput);
  }

  removeInput(index : number) {
    this.inputs.splice(index, 1)
  }
}