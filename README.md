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

Finally, the Ontrack plugin defines an extension to the [Job DSL](https://wiki.jenkins-ci.org/display/JENKINS/Job+DSL+Plugin) so that it can be setup programmatically.

## Setup

You can install the _Ontrack Jenkins plug-in_ on any Jenkins version starting from 1.580.

## Usage

To configure the build, go in _Manage Jenkins > Configure System_ and enter the following data in the _Ontrack configuration_ section:

* _Configuration name in ontrack_ — name of the Jenkins configuration _in Ontrack_ ; this configuration will be used to generated back links to the Jenkins instance you are configuring. This will be used by the _Build notifier_.
* _URL_ — base URL to the Ontrack instance
* _User_ and _Password_ — user used for the connection to Ontrack - this user must have enough rights for the actions it has to carry from within Jenkins. Usually, giving a _Controller_ role should be enough.

Five individual plug-ins are provided by the general _Ontrack plug-in_.

### Build notifier

Add _Ontrack: Build creation_ in the _Post build actions_ in order to create a build for an existing branch.

Parameters are the project name, the branch name and the name of the build to create. Each parameter can use `${VAR}` notations in order to get the value for a `VAR` environment variable or parameter.

The Ontrack build will be associated with a link to the Jenkins build.

### Promotion notifier

Add _Ontrack: Promoted run creation_ in the _Post build actions_ in order to promote an existing build.

Parameters are the project name, the branch name, the name of the build to promote and the name of the promotion level. Each parameter can use `${VAR}` notations in order to get the value for a `VAR` environment variable or parameter.

### Validation notifier

Add _Ontrack: Validation run creation_ in the _Post build actions_ in order to create a validation run for an existing build.

Parameters are the project name, the branch name, the name of the build to validate and the name of the validation stamp. Each parameter can use `${VAR}` notations in order to get the value for a `VAR` environment variable or parameter.

The status of the validation run depends on the Jenkins's build current result:

Jenkins | Ontrack
--------|--------
SUCCESS | PASSED
UNSTABLE | WARNING
ABORTED | INTERRUPTED
_Other_ | FAILED

### DSL step

Add _Ontrack: DSL_ in the _Build steps_ in order to run the Ontrack DSL in the build steps. The build will fail or succeed according to the result of the DSL.

See [Ontrack DSL](ONTRACK_DSL.md) for details about using the Ontrack DSL.

### DSL notifier

Add _Ontrack: DSL_ in the _Post build actions_ in order to run the Ontrack DSL in the post build actions.

See [Ontrack DSL](ONTRACK_DSL.md) for details about using the Ontrack DSL.

### Parameter

The DSL can be used to allow the computation of a parameter for the
running build.

Select _Ontrack: Single Parameter_ in the list of parameters.

Enter a DSL which returns one object and extracts a property of this
object using the _Value property_ field. The resulting string will
be used as the value for the parameter.

### Parameter choice

The DSL can be used to allow the selection among a list of values
computed by the DSL.

Select _Ontrack: Parameter Choice_ in the list of parameters.

Enter a DSL which returns a list of objects (a single object would
   be converted into a singleton list) and extracts a property of
   each item using the _Value property_ field. The resulting list of
   strings is then used for the selection.

### Run condition

The Ontrack Jenkins plug-in provides a [Run Condition](https://wiki.jenkins-ci.org/display/JENKINS/Run+Condition+Plugin) which evaluates the result of the DSL into a `boolean`.

The DSL configuration is the same than above.

The result of the DSL execution is evaluated according to the following rules:

* if a `String` different than '' (_blank_), evaluates to `false`
* if a `Boolean`, uses its value
* in any other case, evaluates to `true`

### Environment setup

The Ontrack plugin provides an extension to the [EnvInject plugin](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin), allowing to run an Ontrack DSL in order to setup the build environment.

The DSL must return a map of `<name, value>` and this is injected into the build environment.

Following variables are bound to the script context:

* `ontrack` - see the _DSL_ section below
* `jenkins` - see the _DSL_ section below
* `build` - the current [`AbstractBuild`](http://javadoc.jenkins-ci.org/hudson/model/AbstractBuild.html) being configured
* `out` - a `PrintStream` which can be used for logging
* `env` - [`hudson.EnvVars`](http://javadoc.jenkins-ci.org/hudson/EnvVars.html) instance which can be used to access current environment or build parameters.

> The set of bound variables is different than the one used in other DSL actions.

## Job DSL for Ontrack

See [Job DSL](JOB_DSL.md) for details about the integration with
the [Job DSL plug-in](https://github.com/nemerosa/ontrack/wiki/DSL).

## Implementation notes

All the plug-in actions rely themselves on the [DSL](ONTRACK_DSL.md) language.
Only the most basic and common actions (build creation, promotion, 
validation) have been extracted as separate actions.

Everything else should be encoded using the DSL step or action.
