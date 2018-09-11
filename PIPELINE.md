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
                        validationStamp: "my-validation",
                        buildResult: currentBuild.result
                    )
                }
            }
        }
    }
}
```

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
 