# OAuth - authentication using access tokens, Spark Example

This project is an example of a Java web application configured to use an
`OAuth Filter` protecting access to the application.

The application is an extremely simple [Spark](http://sparkjava.com) app,
but it could be built using any framework which supports Java Servlets.

## Building

To build this project, use Maven and run the following command:

```
mvn package
```

This will create a WAR or JAR file in the `target` directory. This file is ready to be deployed,
not requiring any external dependencies.


## Deploying

To deploy the WAR file in Jetty or Tomcat, change the Pom packaging format to `war` and simply drop it in the Jetty's `webapps` directory.

For other servers, please check their documentation on how to deploy WAR files.

## Configuring

The configuration of the OAuthFilter is done in the `web.xml` file which is under
`src/main/webapp/WEB-INF/`.

You can also config a HttpClient to be used by the filter to connect to the authentication server.

This is done in the `src/main/resources/META-INF/services/OAuthFilter.properties` file.

**Make sure to either delete this properties file (which will cause the Filter to use a safe default HttpClient)**
or create your own, safe HttpClient supplier when deploying your server to production**.

## Testing

Once the server is running, you can try it by hitting with your favourite browser
a URL similar to:

```
http://localhost:8080/server-example-1.0.0-SNAPSHOT/hello_world
```

The hostname and port depend on your Server's configuration.

The first path depends on the name of the war file you deployed.
If the file is called `myapp.war`, then the first part of the path be simply `myapp`.

The rest of the path should be endpoints configured with [Spark](http://sparkjava.com).

You can add as many endpoints as you want in the `se.curity.examples.spark.SparkServerExample`.

The only configured endpoint so far is `/hello_world`, which should, once the user is authenticated,
just return `"Welcome to an OAuth protected world"`, or 401 on invalid access tokens.

## More Information

For more information, please contact [Curity](http://curity.io).

Copyright 2016 Curity AB