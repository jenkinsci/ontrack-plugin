/**
 * Jenkins job definitions
 */

freeStyleJob("${SEED_PROJECT}-${SEED_BRANCH}-build") {
    description "Continuous Integration for the Ontrack Jenkins plug-in"
    logRotator(numToKeep = 40)
    deliveryPipelineConfiguration('Commit', 'CI')
    jdk 'JDK7'
    label 'docker'
    scm {
        git {
            remote {
                url PROJECT_SCM_URL
                branch "origin/${BRANCH}"
                credentials 'GITHUB'
            }
            extensions {
                wipeOutWorkspace()
                localBranch "${BRANCH}"
            }
        }
    }
    steps {
        maven {
            goals 'clean verify --batch-mode'
            mavenInstallation 'Maven-3.3.9'
            properties(argLine: '-Xmx512m')
        }
    }
    publishers {
        archiveJunit("**/target/surefire-reports/*.xml")
        tasks(
                '**/*.java,**/*.groovy,**/*.xml,**/*.html,**/*.js',
                '**/target/**,seed/**',
                'FIXME', 'TODO', '@Deprecated', true
        )
        archiveArtifacts {
            pattern 'target/*.hpi'
        }
    }
}

freeStyleJob("${SEED_PROJECT}-${SEED_BRANCH}-release") {
    description "Release job for the Ontrack Jenkins plug-in"
    logRotator(numToKeep = 40)
    deliveryPipelineConfiguration('Release', 'Publication')
    parameters {
        stringParam('VERSION', '', '')
        stringParam('NEXT_VERSION', '', '')
    }
    jdk 'JDK7'
    label 'master'
    scm {
        git {
            remote {
                url PROJECT_SCM_URL
                branch "origin/${BRANCH}"
                credentials 'jenkins'
            }
            extensions {
                wipeOutWorkspace()
                localBranch "${BRANCH}"
            }
        }
    }
    wrappers {
        toolenv('Maven-3.3.9')
    }
    steps {
        shell """\
export PATH=\${MAVEN_3_2_X_HOME}/bin:\$PATH
mvn versions:set -DgenerateBackupPoms=false -DnewVersion=\${VERSION} --batch-mode

git config --local user.email "jenkins@nemerosa.net"
git config --local user.name "Jenkins"
git commit -am "Release \${VERSION}"
git tag "\${VERSION}"
"""
        maven {
            mavenInstallation('Maven-3.3.9')
            goals('clean deploy --batch-mode')
            providedSettings('JenkinsIOSettings')
        }
        shell """\
export PATH=\${MAVEN_3_2_X_HOME}/bin:\$PATH
mvn versions:set -DgenerateBackupPoms=false -DnewVersion=\${NEXT_VERSION}-SNAPSHOT --batch-mode
git commit -am "Starting \${NEXT_VERSION}"
"""
    }
    publishers {
        git {
            pushOnlyIfSuccess()
            branch('origin', BRANCH)
            tag('origin', '${VERSION}') {
                message('Release ${VERSION}')
                create()
                update()
            }
        }
        tasks(
                '**/*.java,**/*.groovy,**/*.xml,**/*.html,**/*.js',
                '**/target/**,seed/**',
                'FIXME', 'TODO', '@Deprecated', true
        )
        buildDescription('', 'v${VERSION}', '', 'v${VERSION}')
    }
}
