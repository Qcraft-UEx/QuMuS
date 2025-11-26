import { Circuit } from "./Circuit"

export class Mutant {
    mutantIndex? : number
    mutatedColumn? : number
    mutatedRow? : number
    mutationOperator? : string
    circuit? : Circuit

    constructor(mutant : any) {
        this.mutantIndex = mutant.mutantIndex
        this.mutatedColumn = mutant.mutatedColumn
        this.mutatedRow = mutant.mutatedRow
        this.mutationOperator = mutant.mutationOperator
        this.circuit = new Circuit(undefined, mutant.quirk)
    }
}