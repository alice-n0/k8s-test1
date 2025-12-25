pipeline {
    agent any

    tools {
        jdk 'jdk-17'
    }

    environment {
        JAVA_HOME = tool 'jdk-17'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
        IMAGE_NAME = "hyeonjin5012/k8s-test1"
        IMAGE_TAG  = "${BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Gradle Build') {
            steps {
                sh '''
                chmod +x gradlew
                ./gradlew clean build
                '''
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
                '''
            }
        }

        stage('Docker Login & Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'docker_password',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    sh '''
                    echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                    echo ${IMAGE_NAME}:${IMAGE_TAG}
                    docker push ${IMAGE_NAME}:${IMAGE_TAG}
                    '''
                }
            }
        }
        
        stage('deploy k8s'){
            steps{
                sh '''
                kubectl apply -f k8s/pv.yaml
                kubectl apply -f k8s/pvc.yaml
                export IMAGE_NAME=hyeonjin5012/k8s-test1
                export IMAGE_TAG=${BUILD_NUMBER}

                envsubst < k8s/deployment.yaml | kubectl apply -f -

                kubectl apply -f k8s/service.yaml
                kubectl rollout status deployment/k8s-test1 -n test
                '''
            }
        }
    }

    post {
        always {
            sh 'docker logout || true'
        }
        success {
            echo 'Jenkins pipeline 성공'
        }
        failure {
            echo 'Jenkins pipeline 실패'
        }
    }
}
