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
				sh "cd sample-c-codes/hello-world && ./output && exit 0"
			}
		}


	}




}
