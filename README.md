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

