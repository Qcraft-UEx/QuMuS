<p align="center">
   <picture>
     <source media="(prefers-color-scheme: dark)" srcset="https://github.com/Qcraft-UEx/Qcraft/blob/main/docs/_images/qcraft_logo.png?raw=true" width="60%">
     <img src="https://github.com/Qcraft-UEx/Qcraft/blob/main/docs/_images/qcraft_logo.png?raw=true" width="60%" alt="Qcraft Logo">
   </picture>
</p>

# QuMuS: Quantum Mutation and Scheduling Tool
[![PyPI Version](https://img.shields.io/pypi/v/autoscheduler.svg)](https://pypi.org/project/autoscheduler/)
![Python Versions](https://img.shields.io/badge/python-3.9%20|%203.10%20|%203.11%20|%203.12%20|%203.13-blue.svg)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/Qcraft-UEx/QCRAFT/blob/main/LICENSE)

**QuMuS** is a tool designed to optimize quantum software testing through the generation and scheduling of quantum circuit mutants.
This tool combines two main components:

1. QMu (Quantum Mutator): Responsible for the creation of quantum circuit mutants based on a given Circuit Under Test (CUT) and a configurable set of mutation operators.
2. Scheduler: Executes the parallel composition of mutants on real quantum hardware, minimizing execution time and cost.

Together, these components provide a complete pipeline for quantum mutation testing, from the creation of mutants to their optimized execution on real quantum devices.

---

## Overview

Traditional quantum mutation testing executes mutants sequentially, resulting in high costs and long queue times on quantum hardware.
QuMu overcomes this by generating mutants automatically and scheduling multiple mutants in parallel within a single execution.

The tool has been validated on the IBM Quantum Platform, achieving:

- Average execution time reduction: 92.2%
- Average cost reduction: >90%

---

## Main Features

1. Quantum Mutant Generation (QMu)
- Web-based interface to define the Circuit Under Test (CUT).
- Supports loading circuits from Quirk, Qiskit, or JSON format.
- Offers 18 mutation operators classified into:
    - Initialization errors
    - Swap gates
    - Control gate modifications
    - Other gate transformations
- Option to generate mutants for all input states to ensure exhaustive test coverage.
- Sends mutants in JSON format to the Scheduler for execution.

2. Scheduling (Scheduler)
- Stores mutants in a queue and applies a variant of FIFO scheduling (AL-FIFO).
- Combines multiple mutants into a single composite quantum circuit, depending on available qubits.
- Executes on real quantum hardware (IBM Quantum, AWS Braket, etc.).
- Automatically unschedules and separates the results for each mutant after execution.
- Results are stored in a database and visualized via the web interface.

---
## Installation

Install all dependencies:

```bash
pip install -r requirements.txt
```

Initialize the MongoDB database:

```bash
cd db
sudo docker compose up --build
```

---

## Usage

---

## Changelog
The changelog is available [here](https://github.com/Qcraft-UEx/QCRAFT-AutoScheduler/blob/main/CHANGELOG.md)

---

## License
QCRAFT AutoScheduler is licensed under the [MIT License](https://github.com/Qcraft-UEx/QCRAFT/blob/main/LICENSE)
