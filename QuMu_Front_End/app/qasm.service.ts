import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class QasmService {

  constructor(private client : HttpClient) { }

  import(qasmURL: string) {
    return this.client.get(qasmURL, {responseType:'text'})
  }


}
