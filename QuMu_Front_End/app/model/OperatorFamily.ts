export class OperatorFamily {
    name? : string
    operators : Operator[] = []

    constructor(name : string, operators : any[]) {
        this.name = name
        for (let i=0; i<operators.length; i++)
            this.operators.push(new Operator(operators[i]))
    }

    select() {
        this.operators.forEach(op => op.selected=!op.selected)
    }
}

export class Operator {
    name? : string
    printedName? : string
    selected : boolean = false

    constructor(operator : any) {
        this.name = operator.name
        this.printedName = operator.printedName
    }
}