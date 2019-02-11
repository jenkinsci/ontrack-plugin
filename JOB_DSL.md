Job DSL
=======

The Ontrack plug-in provides the following extensions to the [Job DSL](https://github.com/nemerosa/ontrack/wiki/DSL).

## Table of contents

* [Build notifier](#build-notifier)
* [Promotion notifier](#promotion-notifier)
* [Validation notifier](#validation-notifier)
* [Triggers](#triggers)
* [DSL notifier](#dsl-notifier)
* [DSL step](#dsl-step)
* [Parameters](#parameters)
* [Future extensions](#future-extensions)

## Build notifier

```groovy
job(...) {
   publishers {
      ontrackBuild(project, branch, buildName, ignoreFailure = false, runInfo = false)
   }
}
```

## Promotion notifier

```groovy
job(...) {
   publishers {
      ontrackPromotion(project, branch, build, promotionLevel)
   }
}
```

## Validation notifier

```groovy
job(...) {
   publishers {
      ontrackValidation(project, branch, build, validationStamp, ignoreFailure = false, runInfo = false)
   }
}
```

## Triggers

```groovy
job(...) {
    triggers {
        ontrackTrigger '0 0 H/* * *', 'project', 'branch', 'PROMOTION', 'VERSION'
    }
}
```

and `PROMOTION` can be a valid Ontrack promotion level name or

* _blank_ to mean the last build
* `*` to mean the last promoted build

## DSL notifier

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

## DSL step

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

## Parameters

Single or multiple choice parameters can be created using the following steps:

```groovy
job("...") {
    parameters {
        ontrackChoiceParameter {
            // Name of the environment variable to create (required)
            name "SINGLE_PARAM"
            // Description to display for the parameter (defaults to "")
            description "A single parameter"
            // Ontrack DSL script (required)
            dsl "Ontrack script to run"
            // Value to extract from the result of the script (defaults to "name")
            valueProperty "name"
            // Sandbox environment (defaults to `false`)
            sandbox()
        }
    }
}
```

* The `ontrackSingleParameter` step, with the same parameters can be used to have a single computed parameter.
* The `ontrackMultipleChoiceParameter` step, with the same parameters can be used to have a multiple choice. In this case, the returned value must be a list.

## Future extensions

> Future versions of the Ontrack plug-in will bring Job DSL extensions to support:
> * environment contributions

