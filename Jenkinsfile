pipeline {
    agent {
        docker {
            image "maven:3.5.3-jdk-8"
        }
    }
    options {
        // General Jenkins job properties
        buildDiscarder(logRotator(numToKeepStr: '40'))
        // Timestamps
        timestamps()
        // No durability
        durabilityHint('PERFORMANCE_OPTIMIZED')
    }
    stages {
        stage("Build") {
            steps {
                sh 'mvn --batch-mode clean verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
    }
}