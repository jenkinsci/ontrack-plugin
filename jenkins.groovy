/**
 * Jenkins job definitions
 */

folder {
    name "ontrack-jenkins"
}

job {
    name "ontrack-jenkins/ontrack-jenkins-ci"
    description "Continuous Integration for the Ontrack Jenkins plug-in"
    logRotator(numToKeep = 40)
    deliveryPipelineConfiguration('Commit', 'CI')
    jdk 'JDK8u20'
    scm {
        git {
            remote {
                url 'git@github.com:nemerosa/ontrack-jenkins.git'
                branch "origin/master"
            }
            localBranch "master"
        }
    }
    triggers {
        scm 'H/5 * * * *'
    }
}