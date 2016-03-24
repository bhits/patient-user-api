FROM java:8
VOLUME /java
ADD patient-user-0.2.0-SNAPSHOT.jar /patient-user.jar
ENTRYPOINT ["java","-Djavax.net.ssl.trustStore=/java/keystore/mhc.keystore","-Djavax.net.ssl.trustStorePassword=changeit","-jar","/patient-user.jar"]