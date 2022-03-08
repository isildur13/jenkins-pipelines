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

		stage("go back to jenkins node with the binary 1"){
			steps{
				sh 'ls -la'
			}
		}


		stage("checkout main branch of store-builds"){
			steps{
				checkout([$class: 'GitSCM', branches: [[name: '*/main']], 
					extensions:  [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'store-builds']],  
					userRemoteConfigs: [[credentialsId: 'isildur13', 
					url: 'git@github.com:isildur13/store-builds.git']]])
			
				sh 'cp output store-builds'

				dir('store-builds') {
					sh "git add -A"
					sh 'git commit -m "added latest"'
					sh "git push"
				}
			
			}
		}



	}




}
