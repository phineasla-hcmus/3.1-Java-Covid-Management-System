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
4. Write down the password for `covid_management_system.jks` in `payment\src\main\resources\.sslconfig.properties`
5. Write down the password for `covid_management_system.jts` in `client\src\main\resources\.sslconfig.properties`
6. Store both `.jks` and `.jts` in the project root directory (`.jks` is needed for payment while `.jts` is for client, other directory may also work but I haven't tested it).

NOTE: both `.jks` and `.jts` files should be named `covid_management_system` because they are hardcoded.
