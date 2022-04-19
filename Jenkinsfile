pipeline {

    agent {
        node {
            label 'jenkins-jnlp'
        }
    }

    environment {
        HARBOR_CREDENTIAL_ID = 'harbor-id'
        HARBOR_REGISTRY_URL = '45.207.58.21:9000'
        HARBOR_NAMESPACE = 'gamemanager'
    }

    stages {

        stage('build') {
            steps {
                container('maven') {
                    sh 'ls -l'
                    sh 'mvn -Dmaven.test.skip=true clean package'
                }
            }
        }

        stage('push') {
            steps {
                container('jnlp') {
                    sh 'sleep 1'
                    sh 'cd gm-admin && docker build -f Dockerfile -t ${HARBOR_REGISTRY_URL}/${HARBOR_NAMESPACE}/gm-admin:${ENV_NAME}_${TAG_NAME} .'
                    sh 'cd gm-api && docker build -f Dockerfile -t ${HARBOR_REGISTRY_URL}/${HARBOR_NAMESPACE}/gm-api:${ENV_NAME}_${TAG_NAME} .'
                    sh 'cd gm-job && docker build -f Dockerfile -t ${HARBOR_REGISTRY_URL}/${HARBOR_NAMESPACE}/gm-job:${ENV_NAME}_${TAG_NAME} .'
                    withCredentials([usernamePassword(passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME', credentialsId: HARBOR_CREDENTIAL_ID,)]) {
                        sh 'echo ${DOCKER_PASSWORD} | docker login ${HARBOR_REGISTRY_URL} -u ${DOCKER_USERNAME} --password-stdin'
                        sh 'docker push ${HARBOR_REGISTRY_URL}/${HARBOR_NAMESPACE}/gm-admin:${ENV_NAME}_${TAG_NAME}'
                        sh 'docker push ${HARBOR_REGISTRY_URL}/${HARBOR_NAMESPACE}/gm-api:${ENV_NAME}_${TAG_NAME}'
                        sh 'docker push ${HARBOR_REGISTRY_URL}/${HARBOR_NAMESPACE}/gm-job:${ENV_NAME}_${TAG_NAME}'
                    }
                }
            }
        }

        stage('deploy') {
            steps {
                container('jnlp') {
                    sh 'sleep 1'
                    sh "cd gm-admin && sed -i 's/<ENV_NAME_TAG_NAME>/${ENV_NAME}_${TAG_NAME}/g' k8s.yaml && kubectl apply -f k8s.yaml -n ${ENV_NAME}"
                    sh "cd gm-api && sed -i 's/<ENV_NAME_TAG_NAME>/${ENV_NAME}_${TAG_NAME}/g' k8s.yaml && kubectl apply -f k8s.yaml -n ${ENV_NAME}"
                    sh "cd gm-job && sed -i 's/<ENV_NAME_TAG_NAME>/${ENV_NAME}_${TAG_NAME}/g' k8s.yaml && kubectl apply -f k8s.yaml -n ${ENV_NAME}"
                    sh 'sleep 10'
                    echo '=============================================================================================='
                    sh 'kubectl get all -A -owide'
                    echo '=============================================================================================='
                }
            }
        }

    }

}