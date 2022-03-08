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

				checkout([$class: 'GitSCM', 
							branches: [[name: '*/main']], 
							doGenerateSubmoduleConfigurations: false, 
							extensions: [[$class: 'RelativeTargetDirectory', 
							relativeTargetDir: 'sample-c-codes']], 
							submoduleCfg: [], 
							$class: 'CloneOption', depth: 1, noTags: true, reference: '', shallow: true,
							userRemoteConfigs: [[url: 'https://github.com/isildur13/sample-c-codes.git']]])

				sh "ls -la"	
			}


		}


	}




}
