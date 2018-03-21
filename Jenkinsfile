String version = ''
String gitCommit = ''
String branchName = ''
String projectName = 'ontrack-plugin'

boolean pr = false

pipeline {

    agent any

    options {
        // General Jenkins job properties
        buildDiscarder(logRotator(numToKeepStr: '40'))
        // Timestamps
        timestamps()
        // No durability
        durabilityHint('PERFORMANCE_OPTIMIZED')
    }

    stages {

        stage('Setup') {
            steps {
                script {
                    branchName = ontrackBranchName(BRANCH_NAME)
                    echo "Ontrack branch name = ${branchName}"
                    pr = BRANCH_NAME ==~ 'PR-.*'
                }
                script {
                    if (pr) {
                        echo "No Ontrack setup for PR."
                    } else {
                        echo "Ontrack setup for ${branchName}"
                        ontrackBranchSetup(project: projectName, branch: branchName, script: """
                            branch.config {
                                gitBranch '${branchName}', [
                                    buildCommitLink: [
                                        id: 'git-commit-property'
                                    ]
                                ]
                            }
                        """)
                    }
                }
            }
        }

        stage('Build') {
            agent {
                docker {
                    image 'maven:3-alpine'
                    label 'docker'
                }
            }
            steps {
                sh '''\
mvn clean verify --batch-mode 
'''
            }
            post {
                always {
                    junit 'target/surefire-reports/**/*.xml'
                }
                success {
                    script {
                        if (!pr) {
                            ontrackBuild(project: projectName, branch: branchName, build: version, gitCommit: gitCommit)
                        }
                    }
                }
            }
        }


    }

}