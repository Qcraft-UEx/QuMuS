export class Curl {
    location : string
    header : string
    data : string
    curlText : string
    result : number = 0
    visible : boolean = true
    curlTextVisible : boolean = false

    constructor(init : any) {
        this.location = init.location
        this.header = init.header
        this.data = init.data
        this.curlText = init.curlText
    }
}