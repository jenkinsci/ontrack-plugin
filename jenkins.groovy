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
    steps {
        maven {
            goals 'clean verify'
            mavenInstallation 'Maven-3.2.x'
        }
    }
    publishers {
        archiveJunit("**/target/surefire-reports/*.xml")
        tasks(
                '**/*.java,**/*.groovy,**/*.xml,**/*.html,**/*.js',
                '**/target/**',
                'FIXME', 'TODO', '@Deprecated', true
        )
        buildPipelineTrigger("ontrack-jenkins/ontrack-jenkins-release") {
            parameters {
                currentBuild()
            }
        }
    }
}

job {
    name "ontrack-jenkins/ontrack-jenkins-release"
    description "Release job for the Ontrack Jenkins plug-in"
    logRotator(numToKeep = 40)
    deliveryPipelineConfiguration('Commit', 'CI')
    parameters {
        stringParam('VERSION', '', '')
        stringParam('NEXT_VERSION', '', '')
    }
    jdk 'JDK8u20'
    scm {
        git {
            remote {
                url 'git@github.com:nemerosa/ontrack-jenkins.git'
                branch "origin/master"
            }
            wipeOutWorkspace true
            localBranch "master"
        }
    }
    wrappers {
        toolenv('Maven-3.2.x')
    }
    steps {
        shell '''\
export PATH=${MAVEN_3_2_X_HOME}/bin:$PATH
mvn versions:set -DgenerateBackupPoms=false -Dversion=${VERSION}
git commit -am "Release ${VERSION}"
git tag "${VERSION}"
'''
        maven {
            goals 'clean install'
            mavenInstallation 'Maven-3.2.x'
        }
        shell '''\
export PATH=${MAVEN_3_2_X_HOME}/bin:$PATH
mvn versions:set -DgenerateBackupPoms=false -Dversion=${NEXT_VERSION}-SNAPSHOT
git commit -am "Starting ${NEXT_VERSION}"
'''
    }
    publishers {
        archiveJunit("**/target/surefire-reports/*.xml")
        tasks(
                '**/*.java,**/*.groovy,**/*.xml,**/*.html,**/*.js',
                '**/target/**',
                'FIXME', 'TODO', '@Deprecated', true
        )
    }
}

view(type: DeliveryPipelineView) {
    name "ontrack-jenkins/Pipeline"
    pipelineInstances(4)
    enableManualTriggers()
    showChangeLog()
    updateInterval(5)
    pipelines {
        component("ontrack-jenkins", "ontrack-jenkins/ontrack-jenkins-ci")
    }
}