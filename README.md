# 3.1-Java-Covid-Management-System

## Sign your own SSL certificates
You can create a keystore with the Java Keytool utility that comes with any standard JDK distribution and can be located at `%JAVA_HOME%\bin`. On Windows this would usually be `C:\Program Files\Java\jdk-{your_java_version}\bin`.
1. Open a terminal on that directory and type the following command, then fill in the form to generate a Keystore file:
```
keytool -genkey -v -keystore {destination_directory}\covid_management_system.jks -alias cms -validity 365 -keyalg rsa
```
2. Export the Keystore to a certificate file. Password from step 1 is required:
```
keytool -export -alias cms -keystore {file_location_generated_in_step_1} -file {destination_directory}\covid_management_system.cert
```
3. Import the certificate to a Truststore. Password from step 1 is required:
```
keytool -import -alias cms -file {file_location_generated_in_step_2} -keystore {destination_directory}\covid_management_system.jts
```
