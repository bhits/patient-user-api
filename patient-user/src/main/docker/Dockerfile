FROM java:8
VOLUME /java
ARG finalName
ADD $finalName /patient-user.jar
ENTRYPOINT ["java","-Djavax.net.ssl.trustStore=/java/keystore/mhc.keystore","-Djavax.net.ssl.trustStorePassword=changeit","-jar","/patient-user.jar","--spring.profiles.active=mysql,docker"]