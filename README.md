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

See below for details about using the DSL.

### DSL notifier

Add _Ontrack: DSL_ in the _Post build actions_ in order to run the Ontrack DSL in the post build actions.

See below for details about using the DSL.

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

## DSL

The [Ontrack DSL](https://github.com/nemerosa/ontrack/wiki/DSL) allows you to pilot Ontrack using a simple script language.

The script is written using Groovy with Ontrack and Jenkins specific extensions.

An `ontrack` object is made available - please look in the [Ontrack DSL documentation](https://github.com/nemerosa/ontrack/wiki/DSL) for the details of what you can do.

A `jenkins` object is made available, in order to allow you to have access to the current build. This object has the following methods and properties:

* `build` - access to the [current build](http://javadoc.jenkins-ci.org/hudson/model/AbstractBuild.html)
* `listener` - access to the [build listener](http://javadoc.jenkins-ci.org/hudson/model/BuildListener.html)
* `env(String name, String value)` - sets an environment variable in the current build
* `success` - `true` if the current status of the build is SUCCESS
* `unstable` - `true` if the current status of the build is UNSTABLE
* `failure` - `true` if the current status of the build is FAILURE

All DSL steps and actions take additional parameters other than the DSL script itself.

### Inject environment

Comma-separated list of environment variables or parameters to make available into the script as Groovy variables. For example, if you put `BUILD_NUMBER,SVN_REVISION`, the `BUILD_NUMBER` and `SVN_REVISION` values can be directly accessed from within the DSL.

### Inject properties

You can define variables to inject into the script by using a property-like format.

For example, the following text:

```
BRANCH = 1.0
BUILD = ${VERSION}
```

would inject the corresponding `BRANCH` and `BUILD` variables in the script:

```groovy
ontrack.branch('PRJ', BRANCH).build(VERSION, "Build ${VERSION}")
```

In the text:

* declare properties using `name = value` syntax
* empty lines are ignored
* lines started by `#` are ignored
* patterns like `${VAR}` are expanded using `VAR` from the current
environment variables.

### Ontrack log

If set, the connections (request + response) to Ontrack will be logged in the build console output. This can be useful for debugging the plug-in's behaviour.

## Job DSL for Ontrack

The Ontrack plug-in provides the following extensions to the [Job DSL](https://github.com/nemerosa/ontrack/wiki/DSL).

### Build notifier

```groovy
job(...) {
   publishers {
      ontrackBuild(project, branch, buildName)
   }
}
```

### Promotion notifier

```groovy
job(...) {
   publishers {
      ontrackPromotion(project, branch, build, promotionLevel)
   }
}
```

### Validation notifier

```groovy
job(...) {
   publishers {
      ontrackValidation(project, branch, build, validationStamp)
   }
}
```

### DSL notifier

```groovy
job(...) {
   publishers {
      ontrackDsl {
         // Path to the DSL (relative to workspace)
         path(String value)
         // Ontrack DSL script
         script(String value)
         // Injects environment variables
         // Can be called several times
         environment(String... names)
         // Inject property values
         properties(String properties)
         // Enables or disables the log
         log(boolean enabled = true)
      }
   }
}
```

### DSL step

```groovy
job(...) {
   steps {
      ontrackDsl {
         // Path to the DSL (relative to workspace)
         path(String value)
         // Ontrack DSL script
         script(String value)
         // Injects environment variables
         // Can be called several times
         environment(String... names)
         // Inject property values
         properties(String properties)
         // Enables or disables the log
         log(boolean enabled = true)
      }
   }
}
```

### Future extensions

> Future versions of the Ontrack plug-in will bring Job DSL extensions to support:
> * environment contributions
> * parameters
>
> This mostly depend on the version of the Job DSL which the Ontrack DSL must support (1.35 as of now).

## Implementation notes

All the plug-in actions rely themselves on the DSL language. Only the most basic and common actions (build creation, promotion, validation) have been extracted as separate actions.

Everything else should be encoded using the DSL step or action.
