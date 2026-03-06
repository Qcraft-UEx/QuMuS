<p align="center">
   <picture>
     <source media="(prefers-color-scheme: dark)" srcset="https://github.com/Qcraft-UEx/Qcraft/blob/main/docs/_images/qcraft_logo.png?raw=true" width="60%">
     <img src="https://github.com/Qcraft-UEx/Qcraft/blob/main/docs/_images/qcraft_logo.png?raw=true" width="60%" alt="Qcraft Logo">
   </picture>
</p>

# QuMuS: Quantum Mutation and Scheduling Tool
[![PyPI Version](https://img.shields.io/pypi/v/autoscheduler.svg)](https://pypi.org/project/autoscheduler/)
![Python Versions](https://img.shields.io/badge/python-3.9%20|%203.10%20|%203.11%20|%203.12%20|%203.13-blue.svg)
![Web App](https://img.shields.io/badge/web--app-QuMu%20Client%20(React%20%2B%20Flask)-brightgreen)
![Database](https://img.shields.io/badge/database-MongoDB-green)
![Providers](https://img.shields.io/badge/quantum%20providers-IBM%20Quantum%20%7C%20AWS%20Braket-purple)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/Qcraft-UEx/QCRAFT/blob/main/LICENSE)

**QuMuS** is a tool designed to optimize quantum software testing through the generation and scheduling of quantum circuit mutants.
This tool combines two main components:

1. QuMu (Quantum Mutator): Responsible for the creation of quantum circuit mutants based on a given Circuit Under Test (CUT) and a configurable set of mutation operators.
2. Scheduler: Executes the parallel composition of mutants on real quantum hardware, minimizing execution time and cost.

Together, these components provide a complete pipeline for quantum mutation testing, from the creation of mutants to their optimized execution on real quantum devices.

<p align="center">
   <picture>
     <source media="(prefers-color-scheme: dark)" srcset="https://github.com/Qcraft-UEx/QuMuS/blob/main/images/qumus1.jpg?raw=true" width="60%">
     <img src="https://github.com/Qcraft-UEx/QuMuS/blob/main/images/qumus1.jpg?raw=true" width="60%" alt="QuMuS1">
   </picture>
</p>


---

## Overview

Traditional quantum mutation testing executes mutants sequentially, resulting in high costs and long queue times on quantum hardware.
QuMuS overcomes this by generating mutants automatically and scheduling multiple mutants in parallel within a single execution.

The tool has been validated on the IBM Quantum Platform, achieving:

- Average execution time reduction: 92.2%
- Average cost reduction: >90%

---

## Main Features

1. Quantum Mutant Generation (QuMu)
- Web-based interface to define the Circuit Under Test (CUT).
- Supports loading circuits from Quirk, Qiskit, or JSON format.
    - The exported JSON will look similar to:
```bash
{
  "url": "https://algassert.com/quirk#circuit={'cols':[['H'],['•','X'],['Measure','Measure']]}",
  "shots": 10000
}
```
- Offers 18 mutation operators classified into:
    - Initialization errors
    - Swap gates
    - Control gate modifications
    - Other gate transformations
- Option to generate mutants for all input states to ensure exhaustive test coverage.
- Sends mutants in JSON format to the Scheduler for execution.

<p align="center">
   <picture>
     <source media="(prefers-color-scheme: dark)" srcset="https://github.com/Qcraft-UEx/QuMuS/blob/main/images/qumus2.jpg?raw=true" width="60%">
     <img src="https://github.com/Qcraft-UEx/QuMuS/blob/main/images/qumus2.jpg?raw=true" width="60%" alt="QuMuS2">
   </picture>
</p>


2. Scheduling (Scheduler)
- Stores mutants in a queue and applies a variant of FIFO scheduling (AL-FIFO).
- Combines multiple mutants into a single composite quantum circuit, depending on available qubits.
- Executes on real quantum hardware (IBM Quantum, AWS Braket, etc.).
- Automatically unschedules and separates the results for each mutant after execution.
- Results are stored in a database and visualized via the web interface.

<p align="center">
   <picture>
     <source media="(prefers-color-scheme: dark)" srcset="https://github.com/Qcraft-UEx/QuMuS/blob/main/images/qumus3.png?raw=true" width="45%">
     <img src="https://github.com/Qcraft-UEx/QuMuS/blob/main/images/qumus3.png?raw=true" width="45%" alt="QuMuS3">
   </picture>
</p>


---
## Installation

### Installing QuMu (Mutant Generator)
The QuMu Client is available online and requires no local installation. Anyway, the source code is available in this repository for downloading.

You can access it directly from your browser: https://alarcosj.esi.uclm.es/qumu


### Installing the Scheduler
Install all dependencies:

```bash
pip install -r requirements.txt
```

Initialize the MongoDB database:

```bash
cd db
sudo docker compose up --build
```

### Quantum Provider Configuration
Users must supply valid IBM Quantum API credentials through:

```bash
export QISKIT_IBM_TOKEN="your_api_key"
```

---

## Usage

1. The Client (QuMu) generates all mutants from a given Circuit Under Test. Choosing mutation operators and input configurations.

2. These mutants are sent to the Scheduler.

3. The Scheduler groups them into composite circuits according to available qubits.

4. The combined circuit is executed on the selected quantum provider.

5. Results are unscheduled, split per mutant, and stored for analysis.

This approach allows massive parallel execution of mutants, optimizing time, cost, and quantum resource usage.

---

## Validation 
QuMuS has been experimentally validated on real quantum hardware from both IBM Quantum and Amazon Braket to demonstrate its effectiveness in executing large sets of quantum circuit mutants through multi-programming.

The validation focuses on efficiency improvements (execution time and cost reduction) and statistical fidelity preservation of results when executing mutants in parallel.

To quantify fidelity, we used two statistical metrics commonly employed in NISQ noise analysis:

- Hellinger Distance (HD)
- Jensen–Shannon Divergence (JSD)

These metrics measure how close the probability distributions obtained from scheduled executions are compared to executing each mutant independently.

### IBM Quantum Validation

The main evaluation was performed on IBM Quantum's 127-qubit ibm_brisbane processor, using 10,000 shots per execution.

Three representative quantum circuits were selected:

| Circuit              | Qubits | Mutants |
| -------------------- | ------ | ------- |
| Stochastic circuit   | 15     | 1,344   |
| Adder                | 7      | 1,570   |
| Adder (deep variant) | 7      | 9,280   |

These circuits represent different execution scenarios:
- Deterministic circuits (adder): easier error detection.
- Deep circuits: more sensitive to noise accumulation.
- Probabilistic circuits: suitable for distributional analysis.

Results:

| Example     | Time (s)                | Cost (USD)                | HD    | JSD |
| ----------- | ----------------------- | ------------------------- | ----- | --- |
| Stochastic  | 10,752 → 600 (94.41%)   | $17,203 → $960 (94.41%)   | 22.1% | 12% |
| Adder       | 9,420 → 528 (94.39%)    | $15,072 → $844.8 (94.40%) | 35.7% | 3%  |
| Adder-phase | 92,800 → 7,733 (91.66%) | $148,480 → $12,373 (92%)  | 15.4% | 5%  |

<p align="center"> 
   <picture>
     <source media="(prefers-color-scheme: dark)" srcset="https://github.com/Qcraft-UEx/QuMuS/blob/6d026eabe93d4987b3e12e1ff6cd2f0ca3c383f8/mutants-noise-results/ibm/plot/hellinger_jensen__ibm_plot.png?raw=true" width="80%">
     <img src="https://github.com/Qcraft-UEx/QuMuS/blob/6d026eabe93d4987b3e12e1ff6cd2f0ca3c383f8/mutants-noise-results/ibm/plot/hellinger_jensen__ibm_plot.png?raw=true" width="60%" alt="Results_ibm">
   </picture>
</p>

### Amazon Braket Validation
To demonstrate compatibility with Amazon Braket, an additional experiment was conducted using the Rigetti Ankaa-3 processor (82 qubits). 

Because Braket devices currently offer fewer qubits than IBM machines, executing the full mutant set would require a large number of tasks. Therefore, a representative sample of 10% of the mutants from the deep 7-qubit adder circuit was selected.
- Total scheduled tasks executed: 16
- Full experiment would require: >790 tasks

Fidelity Results:

The probability distributions obtained from scheduled executions remained close to those obtained from isolated executions.

| Metric                         | Value  |
| ------------------------------ | ------ |
| Mean Hellinger Distance        | 24% |
| Mean Jensen–Shannon Divergence | 0.5% |

<p align="center"> 
   <picture>
     <source media="(prefers-color-scheme: dark)" srcset="https://github.com/Qcraft-UEx/QuMuS/blob/6d026eabe93d4987b3e12e1ff6cd2f0ca3c383f8/mutants-noise-results/ibm/plot/hellinger_jensen__aws_plot.png?raw=true" width="60%">
     <img src="https://github.com/Qcraft-UEx/QuMuS/blob/6d026eabe93d4987b3e12e1ff6cd2f0ca3c383f8/mutants-noise-results/ibm/plot/hellinger_jensen__aws_plot.png?raw=true" width="60%" alt="Results_ibm">
   </picture>
</p>

Cost Reduction:
| Execution Mode       | Cost  |
| -------------------- | ----- |
| Scheduled execution  | $58.8 |
| Individual execution | $1674 |

This represents a 96.48% cost reduction using QuMuS scheduling.

### Summary

Across both IBM Quantum and Amazon Braket platforms, QuMuS demonstrates:

- 90% reduction in execution time
- 90% reduction in execution cost
- Acceptable statistical fidelity for mutation analysis
- Scalability to thousands of mutants on real NISQ hardware

These results confirm that QuMuS enables large-scale quantum mutation testing campaigns that would otherwise be impractical using sequential executions.

---

## Changelog
The changelog is available [here](https://github.com/Qcraft-UEx/QCRAFT-AutoScheduler/blob/main/CHANGELOG.md)

---

## License
QuMuS is licensed under the [MIT License](https://github.com/Qcraft-UEx/QCRAFT/blob/main/LICENSE)

---

## Collaborators

<div align="center">
  <img src="https://github.com/Qcraft-UEx/QCRAFT/blob/main/docs/_images/Logo_UNEX.png" width="30%" alt="Logo UNEX"/>
  <img src="https://github.com/Qcraft-UEx/QCRAFT/blob/main/docs/_images/Logo_UCLM.jpg" width="30%" alt="Logo UCLM"/>
</div>

