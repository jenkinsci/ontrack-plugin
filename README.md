ontrack-jenkins
===============

This plug-in allow Jenkins to notify _[ontrack](https://github.com/nemerosa/ontrack)_ about events like build creation, status of running job, etc... but also allows Jenkins to require information from _ontrack_.

It allows to run the following actions:

* creating a new build
* promoting an existing build
* validating an existing build according to the result of the build
* execute any arbitrary [DSL](https://github.com/nemerosa/ontrack/wiki/DSL) as a step or as a publisher
* setup a build environment using the DSL
* evaluating a [condition](https://wiki.jenkins-ci.org/display/JENKINS/Run+Condition+Plugin) based on a DSL evaluation

## Setup

You can install the _Ontrack Jenkins plug-in_ on any Jenkins version starting from 2.7.

## Usage

To configure the build, go in _Manage Jenkins > Configure System_ and enter the following data in the _Ontrack configuration_ section:

* _Configuration name in ontrack_ — name of the Jenkins configuration _in Ontrack_ ; this configuration will be used to generated back links to the Jenkins instance you are configuring. This will be used by the _Build notifier_.
* _URL_ — base URL to the Ontrack instance
* _User_ and _Password_ — user used for the connection to Ontrack - this user must have enough rights for the actions it has to carry from within Jenkins. Usually, giving a _Controller_ role should be enough.

Five individual plug-ins are provided by the general _Ontrack plug-in_.

## Integrations

* [Job components](JOB_COMPONENTS.md)
* [Job DSL plug-in integration](JOB_DSL.md)
* [Ontrack DSL](ONTRACK_DSL.md) notes
