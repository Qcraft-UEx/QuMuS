import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http'
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { OperatorsComponent } from './operators/operators.component';
import { CircuitComponent } from './circuit/circuit.component';
import { MutantCodeComponent } from './mutant-code/mutant-code.component'
import { MutantsGeneratedComponent } from './mutants/mutants-generated.component';
import { SimpleResultsComponent } from './simple-results/simple-results.component';
import { StrategyResultsComponent } from './strategy-results/strategy-results.component';
import { SpinnerComponent } from './spinner/spinner.component';

@NgModule({
  declarations: [
    AppComponent,
    OperatorsComponent,
    CircuitComponent,
    MutantCodeComponent,
    MutantsGeneratedComponent,
    SimpleResultsComponent,
    StrategyResultsComponent,
    SpinnerComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
