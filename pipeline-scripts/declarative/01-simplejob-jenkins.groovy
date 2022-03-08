pipeline{

	agent {label "master"}

	options{

		timestamps()

	}

	stages{

		stage("First stage"){
			steps{
			sh "echo Hello"
			}
		}

		stage("Cloning the sample repo"){
			steps{

				git(branch: 'master',url: 'https://github.com/papanito/jenkins-pipeline-helper.git')

				sh "ls -la"	
			}


		}


	}




}
