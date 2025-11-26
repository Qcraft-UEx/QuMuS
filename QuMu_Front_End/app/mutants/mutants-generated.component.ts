import { Component, EventEmitter, Output } from '@angular/core';
import { ManagerService } from '../manager.service';
import { Mutant } from '../model/Mutant';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { AppComponent } from '../app.component';
import { QumugenService } from '../qumugen.service';
import { QiskitExecutorService } from '../qiskit-executor.service';
import { ReperService } from '../reper.service';
import { Curl } from '../model/Curl';
import { LoadingService } from '../loading.service';

@Component({
  selector: 'app-mutants-generated',
  templateUrl: './mutants-generated.component.html',
  styleUrls: ['./mutants-generated.component.css']
})
export class MutantsGeneratedComponent {

  url?: SafeResourceUrl

  hideQuirk: boolean = true
  hideQiskit: boolean = false

  toleratedError: number = 0.05

  unexCurls: Curl[] = []
  unexProgress: number = 0
  showUnexCurls: boolean = false
  batchSize: number = 100;

  JSON: JSON

  constructor(public sanitizer: DomSanitizer, public manager: ManagerService, private reper: ReperService, private qumugen: QumugenService, private qe: QiskitExecutorService, private loading: LoadingService) {
    this.url = sanitizer.bypassSecurityTrustResourceUrl(AppComponent.quirkUrl)
    this.JSON = JSON
  }

  saveMutants() {
    if (!this.manager.selectedCircuit)
      return
    this.reper.saveMutants(this.manager.selectedCircuit.id, this.manager.mutants).subscribe(
      ok => {
        alert("Mutants saved")
      },
      error => {
        alert("Error saving mutants: " + error)
      }
    )
  }

  buildUnexCurls() {
    if (!this.manager.selectedCircuit)
      return

    this.loading.show()
    this.qumugen.buildUnexCurls(this.manager.selectedCircuit.id, this.manager.mutants).subscribe(
      ok => {
        this.unexCurls = []
        for (let i = 0; i < ok.length; i++) {
          this.unexCurls.push(new Curl(ok[i]))
        }
        this.loading.hide()
        this.showUnexCurls = true
      },
      error => {
        this.loading.hide()
        alert("Error building Extremadura's curls: " + error)
      }
    )
  }

  sendAllToUnex() {
    let option = window.confirm("To the parrot (al loro 🦜)! This may collapse the target system or cause many response errors. We suggest to use the \"Send to UNEX by to one\" button")
    if (!option)
      return
    this.unexProgress = 0
    for (let i = 0; i < this.unexCurls.length; i++)
      this.sendToUnex(this.unexCurls[i])
  }

  async sendBatchToUnex() {
    let curls = this.unexCurls.filter(curl => curl.result == 0 || curl.result != 200);

    // Seleccionamos un lote de CURLs si exceden el tamaño batchSize
    if (curls.length > this.batchSize) {
      curls = curls.slice(0, this.batchSize);
    }

    this.unexProgress = 0; // Reiniciamos el progreso

    for (let i = 0; i < curls.length; i++) {
      try {
        this.sendToUnex(curls[i])
        curls[i].result = 200; // Actualizamos el resultado exitoso
      } catch (error) {
        curls[i].result = 400
      }

      // Actualizamos el progreso después de cada envío
      this.unexProgress = Math.floor(((i + 1) / curls.length) * 100);
    }
  }

  start: number = 0

  async sendToUnexOneByOne() {
    this.unexProgress = 0
    this.loading.show(); // Mostrar el loading una vez al inicio
    while (this.start < this.unexCurls.length) {
      let curl = this.unexCurls[this.start];

      if (curl.result != 200) {
        try {
          await this.qumugen.sendToUnex(curl).toPromise(); // Esperamos a que la petición se complete
          curl.result = 200;
          curl.visible = false;
        } catch (error) {
          curl.result = 400;
        }
        this.unexProgress++
      }

      this.start++;
    }

    this.loading.hide();
    this.start = 0;
  }

  sendToUnex(curl: Curl) {
    this.loading.show();
    this.unexProgress = 0; // Reiniciamos el progreso

    if (curl.result != 200) {
      this.qumugen.sendToUnex(curl).subscribe(
        ok => {
          this.unexProgress++; // Incrementamos el progreso tras un envío exitoso
          curl.result = 200;
          curl.visible = false;
          this.loading.hide(); // Ocultamos el loading solo después de completar el envío
        },
        error => {
          curl.result = error.status;
          this.loading.hide(); // También ocultamos el loading en caso de error
        }
      );
    } else {
      this.loading.hide(); // En caso de que no se haga el envío (curl.result ya es 200)
    }
  }

  selectMutant(mutant: Mutant) {
    AppComponent.error = ""
    if (mutant.mutantIndex == this.manager.selectedMutant?.mutantIndex) {
      this.manager.selectedMutant = undefined
      return
    }

    this.manager.showMutantCode = true
    if (this.manager.selectedCircuit) {
      this.qumugen.getQiskitCode(this.manager.selectedCircuit).then(
        code => {
          this.manager.selectedCircuit!.qiskitCode = code.wholeCode.split("\n")
        }
      )
    }
    this.manager.selectedMutant = mutant
    this.qumugen.getQiskitCode(mutant.circuit!).then(
      code => {
        this.url = this.sanitizer.bypassSecurityTrustResourceUrl(AppComponent.quirkUrl + "#circuit=" + mutant.circuit!.textQuirkCode)
        mutant.circuit!.qiskitCode = code.wholeCode.split("\n")
      }
    )
  }

  setMutantExecutionAlgorithm(e: any) {
    this.manager.executionAlgorithm = e.target.value
  }

}
