import { DomSanitizer, SafeResourceUrl } from "@angular/platform-browser"
import { Circuit } from "./model/Circuit"

export abstract class MutantsExecutor {
    originalResults : any
    mutantResults : any[] = []
    runningMutants : boolean = false
    killedMutants : number = 0
    aliveMutants : number = 0
    nonCompilableMutants : number = 0

    quirkUrl : string = "https://algassert.com/quirk"
    url?  : SafeResourceUrl
    JSON: JSON

    stopped = false

    constructor(public sanitizer: DomSanitizer) {
        this.JSON = JSON
    }

    abstract runOne(circuit : Circuit, program? : string) : void
    abstract runMutants() : void

    stopExecution() {
        this.showModal("Stopping execution")
        this.stopped = true
    }    

    protected showModal(message: string) {
        let existingModal = document.getElementById("modal");
        if (existingModal)
            existingModal.remove(); // Eliminar el modal existente si hay uno
    
        let modal = document.createElement("div");
        modal.id = "modal";
        modal.style.position = "fixed";
        modal.style.top = "50%";
        modal.style.left = "50%";
        modal.style.transform = "translate(-50%, -50%)";
        modal.style.padding = "20px";
        modal.style.backgroundColor = "rgba(0, 0, 0, 0.8)";
        modal.style.color = "white";
        modal.style.borderRadius = "10px";
        modal.style.textAlign = "center";
        modal.style.zIndex = "1000";
    
        modal.innerHTML = `<p>${message}</p>`;
        document.body.appendChild(modal);
    }
    
    protected hideModal() {
        let modal = document.getElementById("modal");
        if (modal) 
        modal.remove();
    }
}
  