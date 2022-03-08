pipeline{
	agent {label "master"}

	options{

		timestamps()

	}

	stages{

		stage("clean workspace"){
			steps{
			cleanWs()
			}
		}


		stage("First stage"){
			steps{
			sh "echo Hello"
			}
		}

		stage("Cloning the sample repo"){
			steps{

				checkout([$class: 'GitSCM', branches: [[name: '*/main']],
							extensions: [[$class: 'CloneOption', 
							depth: 1, 
							noTags: true, 
							reference: '', 
							shallow: true], [$class: 'RelativeTargetDirectory', 
							relativeTargetDir: 'sample-c-codes']], 
							userRemoteConfigs: [[url: 'https://github.com/isildur13/sample-c-codes.git']]])

				sh "ls -la"	
				stash includes: '', name: 'sample-c-codes' 
			}
		}



		stage("Build and check"){
			agent {label "alpha"}
			steps{
				unstash 'sample-c-codes'
				sh "cd sample-c-codes/hello-world && make build"
				sh "cd sample-c-codes/hello-world && ./output"
				stash includes: 'sample-c-codes/hello-world/output', name: 'output-binary' 
			}
			
		}


		stage("go back to jenkins node with the binary"){
			steps{
				cleanWs()
				unstash 'output-binary'
				sh 'mv sample-c-codes/hello-world/output . && rm -rf sample-c-codes'
				sh 'ls -la'
			}
		}

		stage("go back to jenkins node with the binary 1"){
			steps{
				sh 'ls -la'
			}
		}



		stage("checkout main branch of store-builds"){
			steps{
				checkout([$class: 'GitSCM', branches: [[name: '*/main']], 
					extensions: [[$class: 'RelativeTargetDirectory', 
					relativeTargetDir: 'store-builds']], userRemoteConfigs: [[credentialsId: 'isildur13', 
					url: 'git@github.com:isildur13/store-builds.git']]])

					sh '''		
					cd store-builds &&
					git checkout main &&
					git status &&
					git config user.email "panchalyash13@gmail.com" &&
					git config user.name "isildur13" &&
				    cd .. &&
					cp output ./store-builds/ &&
					ls -la ./store-builds 
					
					'''


					dir('store-builds'){
						sshagent (credentials: ['isildur13']) {

							sh "ls -la"	

							sh """ 
							git add -A 
							git commit -m "adding commit for tag v-${env.BUILD_NUMBER}" 
							"""

							sh "git tag -a v-${env.BUILD_NUMBER} -m 'releasing v-${env.BUILD_NUMBER}'"

							sh "git push git@github.com:isildur13/store-builds.git --tags"
						}
					}
					
			}
			
		}
	}

}
