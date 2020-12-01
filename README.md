Simple JEE servlet application providing build numbers using HTTP REST method.
The build numbers are being managed inside HSQL database started automatically by the application.
All parameters are set inside web.xml file.

To compile:
```
mvn install
```

To run locally inside the Jetty container:
```
mvn jetty:run
```

To get next build number, simply call e.g.:

http://localhost:8080/buildnum/service/version?groupId=com.example&artifactId=example&artifactVersion=1.1&format=num&action=increment

Valid values for 'action' parameter: increment, set, reset, show
Valid values for 'format' parameter: num, props
Other parameters: buildNumber (for 'set' action only), artifactClassifier, prefix (for 'props' format only)

