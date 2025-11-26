import { Component } from '@angular/core';
import { ManagerService } from './manager.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'qumuclient';

  public static error : string = ""

  public static quirkUrl = "https://algassert.com/quirk"

  constructor(public manager : ManagerService) {}

  getError() {
    return AppComponent.error
  }

}
