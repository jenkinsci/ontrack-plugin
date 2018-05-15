Ontrack plug-in features
========================

### Build notifier

Add _Ontrack: Build creation_ in the _Post build actions_ in order to create a build for an existing branch.

Parameters are the project name, the branch name and the name of the build to create. Each parameter can use `${VAR}` notations in order to get the value for a `VAR` environment variable or parameter.

The Ontrack build will be associated with a link to the Jenkins build.

If the _Run info_ checkbox is checked, some run info will be associated
with the build, containing the build execution time, the URL of the build
and the trigger cause.

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

If the _Run info_ checkbox is checked, some run info will be associated
with the build, containing the build execution time, the URL of the build
and the trigger cause.

### Triggers

Use the _Ontrack: Trigger_ to add a trigger which fires the job
according to the indicated setup.

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

In [Declarative Pipeline Syntax](https://jenkins.io/doc/book/pipeline/syntax/#declarative-pipeline), parameters can be specified as in the example below,

```groovy
    parameters {
        ontrackSingleParam(name: 'RELEASE_NUMBER', description: 'Release number', dsl: "ontrack.branch('PRJ', 'BRANCH')", valueProperty: 'name')
    }
```

and then used as in the example below.

```groovy
    params.RELEASE_NUMBER
```

### Parameter choice

The DSL can be used to allow the selection among a list of values
computed by the DSL.

Select _Ontrack: Parameter Choice_ in the list of parameters.

Enter a DSL which returns a list of objects (a single object would
   be converted into a singleton list) and extracts a property of
   each item using the _Value property_ field. The resulting list of
   strings is then used for the selection.

In [Declarative Pipeline Syntax](https://jenkins.io/doc/book/pipeline/syntax/#declarative-pipeline), parameters can be specified as in the example below,

```groovy
    parameters {
        ontrackChoiceParam(name: 'RELEASE_NUMBER', description: 'Release number', dsl: "ontrack.branch('PRJ', 'BRANCH').standardFilter(count: 5)", valueProperty: 'name')
    }
```

and then used as in the example below.

```groovy
    params.RELEASE_NUMBER
```

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
