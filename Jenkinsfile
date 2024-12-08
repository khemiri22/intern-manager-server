pipeline {
    agent any
    environment {
            DOCKER_IMAGE = 'khemiri22/intern-manager-server-image'
            IMAGE_TAG = "build-${BUILD_NUMBER}"
            REGISTRY_CREDENTIALS = "docker-hub-credentials"
            K8S_CREDENTIALS = 'aks-secret-file'

        }
    tools{
        maven 'maven-3.9.7'
    }
    stages{
        stage('Build Maven App'){
            steps{
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: 'github-access-token', url: 'https://github.com/khemiri22/intern-manager-server']])
                sh 'mvn clean package'
            }
        }
        stage('Build docker image'){
            steps{
                script{
                     dockerImage = docker.build("${DOCKER_IMAGE}:${IMAGE_TAG}")
                }
            }
        }
        stage('Push image to Hub'){
            steps{
                script{
                   docker.withRegistry( '', REGISTRY_CREDENTIALS ) {
                                           dockerImage.push()
                                        }
                }
            }
        }
        stage('Cleaning up') {
                    steps {
                        sh "docker system prune -a --volumes -f"
                    }
                }

        stage('Update Kubernetes Manifest') {
                    steps {
                        script {
                            sh "sed -i 's@DOCKER_IMAGE:IMAGE_TAG@${DOCKER_IMAGE}:${IMAGE_TAG}@g' k8s/intern-manager-server-deployment.yml"
                        }
                    }
                }

        stage ('Azure Kubernetes Service Deploy') {
                    steps {
                        withKubeConfig([caCertificate: '', clusterName: '', contextName: '', credentialsId: K8S_CREDENTIALS, namespace: '', restrictKubeConfigAccess: false, serverUrl: '']) {
                            sh "kubectl apply -f k8s/ --namespace ingress-nginx"
                        }
                    }
                 }
    }
}