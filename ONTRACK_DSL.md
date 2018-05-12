Ontrack DSL
===========

The [Ontrack DSL](http://nemerosa.github.io/ontrack/release/latest/doc/index.html#dsl) allows you to pilot Ontrack using a simple script language.

The script is written using Groovy with Ontrack and Jenkins specific extensions.

An `ontrack` object is made available - please look in the [Ontrack DSL documentation](http://nemerosa.github.io/ontrack/release/latest/doc/index.html#dsl) for the details of what you can do.

A `jenkins` object is made available, in order to allow you to have access to the current build. This object has the following methods and properties:

* `build` - access to the [current build](http://javadoc.jenkins-ci.org/hudson/model/Run.html)
* `listener` - access to the [build listener](http://javadoc.jenkins-ci.org/hudson/model/TaskListener.html)
* `env(String name, String value)` - sets an environment variable in the current build
* `success` - `true` if the current status of the build is SUCCESS
* `unstable` - `true` if the current status of the build is UNSTABLE
* `failure` - `true` if the current status of the build is FAILURE
* `runInfo` - a run info map, ready to be set on builds or validation runs, or `null`
  if no run info can be computed

All DSL steps and actions take additional parameters other than the DSL script itself.

## Inject environment

Comma-separated list of environment variables or parameters to make available into the script as Groovy variables. For example, if you put `BUILD_NUMBER,SVN_REVISION`, the `BUILD_NUMBER` and `SVN_REVISION` values can be directly accessed from within the DSL.

## Inject properties

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

## Ontrack log

If set, the connections (request + response) to Ontrack will be logged in the build console output. This can be useful for debugging the plug-in's behaviour.
