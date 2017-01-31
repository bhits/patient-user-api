# Short Description
The Patient User API is responsible for managing the patient user account.

# Full Description

# Supported Source Code Tags and Current `Dockerfile` Link

[`0.15.0 (latest)`](https://github.com/bhits/patient-user-api/releases/tag/0.15.0), [`0.12.0`](https://github.com/bhits/patient-user-api/releases/tag/0.12.0)

[`Current Dockerfile`](https://github.com/bhits/patient-user-api/blob/master/patient-user/src/main/docker/Dockerfile)

For more information about this image, the source code, and its history, please see the [GitHub repository](https://github.com/bhits/patient-user-api).

# What is Patient User API?

The Patient User API is responsible for managing the patient user creation process, including user creation and activation, user scope management, email token generation, and extracting existing user profile from the Patient Health Record API (PHR) in the Consent2Share (C2S) application.

For more information and related downloads for Consent2Share, please visit [Consent2Share](https://bhits.github.io/consent2share/).
# How to use this image


## Start a Patient User instance

Be sure to familiarize yourself with the repository's [README.md](https://github.com/bhits/patient-user-api) file before starting the instance.

`docker run  --name patient-user -d bhits/patient-user:latest <additional program arguments>`

*NOTE: In order for this API to fully function as a microservice in the Consent2Share application, it is required to setup the dependency microservices and support level infrastructure. Please refer to the [Consent2Share Deployment Guide](https://github.com/bhits/consent2share/releases/download/2.1.0/c2s-deployment-guide.pdf) for instructions to setup the Consent2Share infrastructure.*


## Configure

This API runs with a [default configuration](https://github.com/bhits/patient-user-api/blob/master/patient-user/src/main/resources/application.yml) that is primarily targeted for the development environment.  The Spring profile `docker` is actived by default when building images. [Spring Boot](https://projects.spring.io/spring-boot/) supports several methods to override the default configuration to configure the API for a certain deployment environment. 

Here is example to override default database password:

`docker run -d bhits/patient-user:latest --spring.datasource.password=strongpassword`

## Using a custom configuration file

To use custom `application.yml`, mount the file to the docker host and set the environment variable `spring.config.location`.

`docker run -v "/path/on/dockerhost/C2S_PROPS/patient-user/application.yml:/java/C2S_PROPS/patient-user/application.yml" -d bhits/patient-user:tag --spring.config.location="file:/java/C2S_PROPS/patient-user/"`

## Environment Variables

When you start the Patient User image, you can edit the configuration of the Patient User instance by passing one or more environment variables on the command line. 

### JAR_FILE

This environment variable is used to setup which jar file will run. you need mount the jar file to the root of container.

`docker run --name patient-user -e JAR_FILE="patient-user-latest.jar" -v "/path/on/dockerhost/patient-user-latest.jar:/patient-user-latest.jar" -d bhits/patient-user:latest`

### JAVA_OPTS 

This environment variable is used to setup JVM argument, such as memory configuration.

`docker run --name patient-user -e "JAVA_OPTS=-Xms512m -Xmx700m -Xss1m" -d bhits/patient-user:latest`

### DEFAULT_PROGRAM_ARGS 

This environment variable is used to setup application arugument. The default value of is "--spring.profiles.active=docker".

`docker run --name patient-user -e DEFAULT_PROGRAM_ARGS="--spring.profiles.active=ssl,docker" -d bhits/patient-user:latest`

# Supported Docker versions

This image is officially supported on Docker version 1.12.1.

Support for older versions (down to 1.6) is provided on a best-effort basis.

Please see the [Docker installation documentation](https://docs.docker.com/engine/installation/) for details on how to upgrade your Docker daemon.

# License

View [license](https://github.com/bhits/patient-user-api/blob/master/LICENSE) information for the software contained in this image.

# User Feedback

## Documentation 

Documentation for this image is stored in the [bhits/patient-user-api](https://github.com/bhits/patient-user-api) GitHub repository. Be sure to familiarize yourself with the repository's README.md file before attempting a pull request.

## Issues

If you have any problems with or questions about this image, please contact us through a [GitHub issue](https://github.com/bhits/patient-user-api/issues).