# Omniplanner

*"This application converts input SVG format into the requested PDF, JPG, or PNG formats."*


## Table of Contents
1.[Getting Started Terraform Deployment Steps](#steps-to-deploy-lambda-omni-planner-through-terraform)

2.[Java Maven Steps](#java-maven)

## Steps to deploy lambda omni-planner through Terraform

1.cd into the directly where all the terraform, lambda and image generator codes are present.

Ex. In my case the directory is “aws-lambda-terraform-java-play-deployed/” like shown in below picture

![](src/main/resources/static/images/build.png)

2.When we cd into the directory “aws-lambda-terraform-java-play-deployed/” we can see the files  shown in picture below.

![](src/main/resources/static/images/Ls.png)


3.Now run the command ./build_and_deploy.sh or bash build_and_deploy.sh as shown in below.

![](src/main/resources/static/images/build.png)

OR

![](src/main/resources/static/images/build_bash.png)

4.Now the above code will run the shell script which will test, build and create the artifact and also create terraform workspace and deploy the artifact to lambda. If you want to run same code in different environment change the .tfvars file and change workspace names in the places shown below
Ex. for me i have created a cors_test workspace and selected workspace before deploying the code.

![](src/main/resources/static/images/build_bash_sh.png)

## Java Maven 
### Steps & Commands: Java and Maven commands (To Run  & Build locally )

**Note :** 

>These steps are optional and can be used to just build, run and test locally, As all these steps are executed automatically when deployed through terraform.

>Make sure you are inside the “terraform/aws-lambda-terraform-java/imagegenerator" directory and import it as a maven project in STS ide or in Intellij Idea you can open the above directory directly.

#### Run the application locally

1. Go to pom.xml and uncomment the below dependency 

```
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-function-web</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-function-compiler</artifactId>
            </dependency>
```
**NOTE :**

>And make sure to comment it when building a jar to deploy at AWS, As above dependency is not required at cloud environment.

2.Set the below properties in  system env

> accessKey=(value you may get it from aws console); accessSecret=(value you may get it from aws console); region=us-east-1

or if you are using IDE for eg STS or Intellij Idea
right click on ImageGeneratorApplication.java ->> go to modify run configuration ->> inside Environment Variable row mentioned  below properties separated by ";" and apply and run.

> accessKey=(value you may get it from aws console); accessSecret=(value you may get it from aws console); region=us-east-1

3.when accessing the spring cloud function locally enclosed the whole request inside 

```
{
  "generatorInput": {
    "paramDictionary": {}
  }
}

```
#### Build the JAR locally

1. mvn clean compile install
2. mvn clean compile install -DskipTests (build skipping the test)

you can run the above command through command prompt(from java maven project directory) or through IDE.
