Pipeline DSL integration
========================

[Components](JOB_COMPONENTS.md) of the Ontrack plug-in can be called in
the [Jenkins pipeline DSL](https://jenkins.io/doc/book/pipeline/).

Following steps are currently available.

### Branch name

Most of the steps below will need a reference on an
[Ontrack branch name](http://nemerosa.github.io/ontrack/release/latest/doc/index.html#model).

When using the pipeline in a multibranch job, the `BRANCH_NAME` contains the name of the
branch in the SCM, but this name might not be suitable for Ontrack in terms of
accepted characters. For example, whereas `release/1.0` is a valid name for a Git
branch, it is _not_ valid for an Ontrack branch.

This step allows to sanitize a string to be used as a valid Ontrack branch name.

```groovy
String branchName
pipeline {
    stages {
        stage('Setup') {
            steps {
                script {
                    branchName = ontrackBranchName(BRANCH_NAME)
                    echo "Ontrack branch name = ${branchName}"
                }
            }
        }
    }
}
``` 

In the sample above, if `BRANCH_NAME` is equal to `release/1.0`, `branchName` would
be then equal to `release-1.0`.

Any character which is not accepted by Ontrack will be replaced by the `-` character.
This replacement string can be overridden by using the `branchReplacement` attribute.
For example, to use an underscore instead:

```groovy
branchName = ontrackBranchName(branch: BRANCH_NAME, branchReplacement: '_')
```

### Project set-up

The `ontrackProjectSetup` step can be used to prepare an Ontrack project, and to create
it when it does not exist.

It will typically be used in a "setup" stage.

Example:

```groovy
pipeline {
    stages {
        stage('Setup') {
            steps {
                ontrackProjectSetup(
                    project: 'my-project',
                    script: '''\
                        project.config {
                            autoValidationStamp(true, true)
                            // ... Other project configurations properties
                        }
                        '''
                )
            }
        }
    }
}
```

The `script` will contain a `project` reference to the Ontrack project. If the project
does not exist, it is created.

Two other optional parameters are also available:

* `logging` (`boolean`) - defaults to `false` - enables logging of the calls to Ontrack
* `bindings` (map `String` --> any) - defaults to empty - additional objects to make
  available to the configuration script 

### Branch set-up

The `ontrackBranchSetup` step can be used to prepare an Ontrack branch, and to create
it when it does not exist.

It will typically be used in a "setup" stage.

Example, in conjunction with the normalisation of the branch name:

```groovy
String branchName
pipeline {
    stages {
        stage('Setup') {
            steps {
                script {
                    branchName = ontrackBranchName(BRANCH_NAME)
                    echo "Ontrack branch name = ${branchName}"
                }
                ontrackBranchSetup(
                    project: 'ontrack', 
                    branch: branchName, 
                    script: """\
                        branch.config {
                            gitBranch '${branchName}', [
                                buildCommitLink: [
                                    id: 'git-commit-property'
                                ]
                            ]
                        }
                    """
                )
            }
        }
    }
}
```

The script above will configure the branch to associate builds to a Git commit property.

The `script` will contain a `branch` reference to the Ontrack branch. If the branch
does not exist, it is created.

Two other optional parameters are also available:

* `logging` (`boolean`) - defaults to `false` - enables logging of the calls to Ontrack
* `bindings` (map `String` --> any) - defaults to empty - additional objects to make
  available to the configuration script 

### Build creation step

The `ontrackBuild` step creates an Ontrack build for an existing branch.

Example:

```groovy
pipeline {
    stages {
        stage('Build') {
            // ...
            // Computes the `version` variable
            // ...
            post {
                success {
                    ontrackBuild(
                        project: 'my-project',
                        branch: branchName,
                        build: version,
                    )
                }
            }
        }
    }
}
```

The build name will depend on your project.

Since associating a Git commit with the build is very frequent, you can provide it
directly:

```groovy
pipeline {
    stages {
        stage('Build') {
            // ...
            // Computes the `version` variable
            // Computes the `gitCommit` variable
            // ...
            post {
                success {
                    ontrackBuild(
                        project: 'my-project',
                        branch: branchName,
                        build: version,
                        gitCommit: gitCommit,
                    )
                }
            }
        }
    }
}
```

Note that a run info will always be associated with the build if
the version of the remote Ontrack instance is compatible
(>= 2.35 or >= 3.35).

### Validation step

The `ontrackValidate` step creates a validation run for a build
and a validation stamp. The status of the validation may be fixed
or computed.

Note that a run info will always be associated with the validation if
the version of the remote Ontrack instance is compatible
(>= 2.35 or >= 3.35).

Example:

```groovy
pipeline {
    stages {
        stage('Validation') {
            post {
                success {
                    ontrackValidate(
                        project: 'my-project',
                        branch: branchName,
                        build: version,
                        validationStamp: "my-validation"
                    )
                }
            }
        }
    }
}
```

#### Validation status

The status of the validation run can be provided using different ways:

* explicitly, using the `validationStatus` parameter, for example: `validationStatus: 'PASSED'`
* using a `hudson.model.Result` instance, for example: `buildResult: currentBuild.result`
* if nothing is specified, the result is computed from the current stage (if available) or
  the current build

#### Validation data

The step can be used to associate some data with the validation run.

Note that the data may be validated by the validation stamp at server side, and
might therefore be rejected.

Three parameters do control the validation data:

* `data` - the data to be sent
* `dataType` - the type of data
* `dataValidation` - `true` by default. If `true`, no validation status is computed or sent,
  unless it is explicitly specified using the `validationStatus` parameter

A data type can be specified using its fully qualified name, but most of the same, you'll
want to use predefined aliases.

For example:

* for a fraction:

```groovy
ontrackValidate ...,
    dataType: 'fraction',
    data: [numerorator: 99, denominator: 100],
```

* for a CHML (critical / high / medium / low) type:

```groovy
ontrackValidate ...,
    dataType: 'chtml',
    data: [critical: 0, high: 1, medium: 10, low: 1000],
    // Components can be omitted, they'll be set to 0
```

* for some textual data:

```groovy
ontrackValidate ...,
    dataType: 'text',
    data: [value: 'Some text'],
```

* for some (integer) number data:

```groovy
ontrackValidate ...,
    dataType: 'number',
    data: [value: 42],
```

* for some (integer) percentage data:

```groovy
ontrackValidate ...,
    dataType: 'percentage',
    data: [value: 42],
```

Of course, it is possible to specify a complete type by using its FQCN:

```groovy
ontrackValidate ...,
    dataType: 'net.nemerosa.ontrack.extension.general.validation.ThresholdPercentageValidationDataType',
    data: [value: 42],
```

(this code is equivalent to the previous one)

#### Test results as validation data

A common use case would be to associate the test results of a stage (tests passed vs total
of tests) as a fraction data to a validation run.

A typical usage:

```groovy
script {
    def results = junit ...
    ontrackValidate ..., testResults: results
}
```

The fraction sent as data will be computed as follows:

* `numerator` = `passCount`
* `denominator` = `totalCount - skipCount`

Note that a step like `junit` returns an instance of `hudson.tasks.junit.TestResultSummary`
but this can be replaced by any other object having the same properties than
as mentioned above.

If some `testResults` parameter is set, any `data` or `dataType` parameter is ignored. 


### Promotion step

The `ontrackPromote` step creates a promotion run for a build
and a promotion level.

Example:

```groovy
pipeline {
    stages {
        stage('Promotion') {
            post {
                success {
                    ontrackPromote(
                        project: 'my-project',
                        branch: branchName,
                        build: version,
                        promotionLevel: "my-level"
                    )
                }
            }
        }
    }
}
```

### Ontrack DSL step

The `ontrackScript` step runs an Ontrack DSL script.

Example:

```groovy
ontrackScript logging: true, script: ' ... ', bindings: [VERSION: '1.0.0']
```

### Ontrack GraphQL step

The `ontrackGraphQL` step runs a GraphQL query
against the Ontrack server and returns the result as
a JSON object.

Example:

```groovy
ontrackGraphQL(script: '{projects{name}}')
```

The script above would return something like:

````json
{
  "data": {
    "projects": [{
      "name": "ontrack"
    }]
  }
}
````

### Ontrack trigger

The `ontrackTrigger` can be used as trigger.

Example:

```groovy
pipeline {
    triggers {
        ontrackTrigger spec: '@nightly', project: 'my-project', branch: 'master', promotion: 'SILVER', parameterName: 'VERSION'
    }
}
```

This example sets a trigger which checks every night (`@nightly`)
if there is a _new_ `PLATINUM` version for the given project / branch.

If there is a new one, the pipeline is fired, and the `VERSION` parameter
contains the build name.

Other Ontrack steps can then be used to retrieve additional information.
 