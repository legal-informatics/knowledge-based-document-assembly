# **Knowledge-Based Legal Document Assembly** #
This project is developed as a proof of concept for research on legal document assembling. It should present law students complete assembly process with dependencies of law regulations and case facts on final outcome. As a learning system, it should also provide explanations and legal sources related to particular assembly step.

Currently, it supports legal documents such as indictments and motions to indict but, properly configured, it could support another types of legal documents as well. 

Table of contents
=================
  * [About](#about)
  * [Implementation](#implementation)
  * [The project structure](#the-project-structure)
  * [Usage](#usage)
    * [Requirements](#requirements)
    * [Usage scenario example](#usage-scenario-example)

# About
The project presents a method for law students training in legal document assembly using formal knowledge found in legal regulations and tacit knowledge found as a pattern present in structure and content of legal acts. Legal rules (formal knowledge), written in [LegalRuleML](https://www.oasis-open.org/committees/legalruleml/) format, are transformed into [RuleML](http://wiki.ruleml.org/)-like syntax supported by [DR-DEVICE](lpis.csd.auth.gr/systems/dr-device.html), while document templates (tacit knowledge) are accepted in [ToXgene](www.cs.toronto.edu/tox/toxgene/) format. Since this project is currently oriented on indictments and motions to indict, user is asked to give facts which confirms a criminal offence commitment and facts needed for content completion of those legal documents.

Using rulebase and gathered facts, DR-DEVICE resolves if criminal offence is committed and also gives the proof for its conclusion. Such proof is then transformed into argument graph using Carneades Argument Format ([CAF](http://carneades.github.io/)). Also, using given facts and appropriate document template, ToXgene generates document using [Akoma Ntoso](http://www.akomantoso.org/) format.

# Implementation
This project is developed in Java using [Eclipse](https://eclipse.org) IDE. Connection with DR-DEVICE is achieved by setting the path to the folder where DR-DEVICE reasoner is installed. Since DR-DEVICE is built on MS Windows version of CLIPS rule system, therefore this project is also MS Windows dependent.

Because this project relies on XSL transformations, it uses libraries such as [Xalan](http://xalan.apache.org/index.html) and [Saxon](http://saxon.sourceforge.net/).

# The project structure
Beside common folders in eclipse project structure, some configuration and generated files are placed in `input`, `output` and `temp` folder. Descriptions of those files are as follows:

    .
    ├── ...
    ├── input  # exercise input files
    │   ├── exercise_art246.xml  # exercise configuration related to article 246 of Criminal Code
    │   ├── indictment_art246.tsl  # indictment template used by exercise_art246.xml
    │   ├── exercise_art297para2.xml  # exercise configuration related to article 297 paragraph 2 of Criminal Code
    │   ├── indictment_art246.tsl  # indictment template used by exercise_art297para2.xml
    │   ├── lrml2dr-device.xsl  # XSL transforming rulebase in LegaRuleML format into RuleML-like syntax
    │   ├── rulebase.xml  # rulebase in LegaRuleML format
    │   └── proof2caf.xsl  # XSL tranforming reasoning proof into Carneades argument format
    ├── lib  # contains required jar libraries
    ├── output  # exercise output files
    │   └── caf.xml  # generated argument graph in Carneades argument format
    │   └── indictment-document.xml  # generated indictment written in AkomaNtoso format
    ├── src  # project source code
    ├── temp  # temporary files
    └── ...

# Usage
For using legal document assembly system, there are some requirements which have to be satisfied before its executing.

## Requirements
To run legal document assembly system, some prerequisite must be fulfilled:

- MS Windows OS is mandatory since DR-DEVICE demands it
- DR-DEVICE reasoner must be correctly installed
- path to DR-DEVICE folder must be set

## Usage scenario example
Invoking the `main` method of `legal.documentassembly.cli.Main` class executes command line user interface of legal document assembly system.

As an user choose which exercise to run, typing its filename, the question-answering process starts. When all steps are completed the exercise is finished and output files are generated.
For example, question answering may look as follows:

    defendant's name: John Doe
    substance: cannabis
    quantity (g): 50

During the exercise, user is able to return to previous steps by entering minus sign. While answering on repeated question, the previously given answer is offered as a default answer.

    defendant's name: John Doe
    substance: -
    defendant's name [John Doe]:

