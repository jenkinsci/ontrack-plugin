Job DSL
=======

The Ontrack plug-in provides the following extensions to the [Job DSL](https://github.com/nemerosa/ontrack/wiki/DSL).

## Build notifier

```groovy
job(...) {
   publishers {
      ontrackBuild(project, branch, buildName)
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
      ontrackValidation(project, branch, build, validationStamp)
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

## Future extensions

> Future versions of the Ontrack plug-in will bring Job DSL extensions to support:
> * environment contributions
> * parameters
>
> This mostly depend on the version of the Job DSL which the Ontrack DSL must support (1.35 as of now).

