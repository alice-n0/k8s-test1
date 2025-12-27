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
        
        NAMESPACE = "test"
        RELEASE_NAME = "k8s-test1"
        CHART_PATH = "./helm/k8s-test1"
    }
    options {
        disableConcurrentBuilds()
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
        
      stage('deploy helm') {
            steps {
                withCredentials([file(credentialsId: 'k8s_master_config', variable: 'KUBECONFIG')]) {
                    sh """
                        helm upgrade --install ${RELEASE_NAME} ${CHART_PATH} \
                        --wait --timeout=10m
                        --namespace ${NAMESPACE} \
                        --set image.repository=${IMAGE_NAME} \
                        --set image.tag=${IMAGE_TAG}
                    """
                }
            }
        }
    }

    post {
        success {
            echo '배포 성공'
        }
        failure {
            echo '배포 실패'
        }
    }
}
