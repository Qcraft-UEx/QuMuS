import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DictionaryService {

  //private static readonly dictURL : string = "https://alarcosj.esi.uclm.es/dictionary/dict/getURL/"
  private static readonly dictURL : string = "http://localhost:8505/dict/getURL/"
  private qumugenUrl? : string 
  private reperUrl?  : string 
  private qiskitUrl? : string 
  public qiskitTemplates? : any[] = []

  constructor() {
    this.getQumugenURL()
    this.getReperURL()
    this.getQiskitURL()
  }

  getQumugenURL() {
    let self = this
    if (!this.qumugenUrl) {
      let request = new XMLHttpRequest()
      request.open("GET", DictionaryService.dictURL + "qumugen", false)
      request.onreadystatechange = function() {
        self.qumugenUrl = JSON.parse(request.responseText).url
      }
      request.send()
    }
    if (this.qumugenUrl && this.qumugenUrl.indexOf("localhost")==-1)
      return this.qumugenUrl + "qumugen/"
    return this.qumugenUrl
  }

  getReperURL() {
    let self = this
    if (!this.reperUrl) {
      let request = new XMLHttpRequest()
      request.open("GET", DictionaryService.dictURL + "reper", false)
      request.onreadystatechange = function() {
        self.reperUrl = JSON.parse(request.responseText).url
      }
      request.send()
    }
    return this.reperUrl
  }

  getQiskitURL() {
    let self = this
    if (!this.qiskitUrl) {
      let request = new XMLHttpRequest()
      request.open("GET", DictionaryService.dictURL + "qe", false)
      request.onreadystatechange = function() {
        self.qiskitUrl = JSON.parse(request.responseText).url
        self.loadTargetSO()
      }
      request.send()
    }
    return this.qiskitUrl
  }

  loadTargetSO() {
    let self = this
    let request = new XMLHttpRequest()
    request.open("GET", this.qiskitUrl + "simple/getQiskitTemplates", false)
    request.onreadystatechange = function() {
      self.qiskitTemplates = request.responseText as any
    }
    request.send()
    return this.qiskitTemplates
  }
}
