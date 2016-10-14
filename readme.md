# OAuth Protected API Example

This project contains two modules

1. api-example - an example web api that uses the OAuth filter
2. oauth-filter - a servlet filter that authenticates and authorizes requests using OAuth access tokens.

There are two `OAuthFilter` implementations. `OAuthJwtFilter` and `OAuthOpaqueFilter`.
These implement Servlet filters `Filter` and can be used to protect APIs build using Java.

The example also include a minimal web-server using `Spark` [sparkjava](http://sparkjava.com) that uses these filters
in its `before` clause.

Depending on the format of the access token, there are two approaches that can be taken.

1. If the token is a Json Web Token (JWT) then validate the token using a public key
2. If the token is a reference (opaque) token, then validate by calling the OAuth server's
[introspection](https://tools.ietf.org/search/rfc7662) endpoint.

## Filter overview

The filter is build to perform two tasks.

1. Authenticate the caller by validating the incoming access token
2. Authorize the operation by validating the scopes in the access token against the configured scopes

The authorization is very basic, and in this example only checks that all configured scopes are present in the
token. A more advanced scenario would likely want to check the HTTP method, along with sub-paths in order to determine
if the appropriate scope is present in the request.

## Using Json Web Tokens (JWT) as access token format

`OAuthJwtFilter` implements a filter that expects a Json Web Token, and that can validate the token either by
using a pre-shared certificate or by calling the OAuth servers Json Web Key Service (JWKS) endpoint.
The default is to use the JWKS service, as this provides a more maintainable deployment structure for microservices.

To test this using the Spark example simply change the before filter to get the filter using the function `getJwtFilter`
instead. Since it uses embedded spark, the FilterConfiguration is compiled in while in a normal case the web.xml would be
used for configuration.


## Using Opaque tokens as access token format

`OAuthOpaqueFilter` implements a filter that expects an opaque token. I.e. a token that needs to be introspected in order
to determine the contents. This requires the OAuth server to support [introspection](https://tools.ietf.org/search/rfc7662).
Introspection means that the API acts as an introspecting client, and therefore needs client credentials in order to
authenticate itself against the introspection endpoint.

Each new token received is introspected, then cached for a limited time. In production this should be refined to perhaps use
a shared cache or at least a datastore for the cache if there is a large number of requests coming in to the API.


## Scope bases authorization

The abstract class `OAuthFilter` implements a simple authorize method, that validates the incoming scopes against the
configured ones. It is simple to override this method in the implementing classes instead to perform more advanced authorization.


## Running the demo

When building with `mvn package`, a jar called `api-example-x.y.z.jar` is created in the
`target` directory of the api-example module. A full image jar is added with all dependencies included called
`api-example-1.0.0-jar-with-dependencies.jar`. This is a runnable JAR which can be
run with the following command:

```
java -jar api-example/target/api-example-1.0.0-jar-with-dependencies.jar
```

To use a specific HttpClientSupplier, just place a file called `OAuthFilter.properties` in
the working directory. For example:

```
cp /path-to-properties-file/OAuthFilter.properties .
java -jar api-example/target/api-example-1.0.0-jar-with-dependencies.jar
```

See more information about the properties file in the *Providing an external HttpClient* section.

*Note* Unsafe HTTP clients should *NEVER* be used in production.

## Configuring the Filter

To configure the filter, use the `web.xml` file of your application as shown in the
`server-example` project.

### Init-params for the OAuthJwtFilter

* oauthHost - hostname of the OAuth server.
* oauthPort - port of the OAuth server.
* jsonWebKeysPath - path to the JWKS endpoint on the OAuth server.
* scope - A space separated list of scopes required to access the API.
* minKidReloadTimeInSeconds - minimum time to reload the webKeys cache used by the Filter.

### Init-params for the OAuthOpaqueFilter

* oauthHost - hostname of the OAuth server.
* oauthPort - port of the OAuth server.
* introspectionPath - path to the introspection endpoint on the OAuth server.
* scope - A space separated list of scopes required to access the API.
* clientId - your application's client id to use for introspection.
* clientSecret - your application's client secret.


## Providing an external HttpClient

The `OAuthFilter` uses a [HttpClient](https://hc.apache.org/httpcomponents-client-ga/)
to communicate with the authentication server.

The HttpClient may be overridden by the web application by providing a properties
file in the following locations:

* `META-INF/services/OAuthFilter.properties` relative to the classpath
* `OAuthFilter.properties` relative to the working directory

The only accepted property is the name of a supplier class to be used to provide the HttpClient instance:

```properties
openid.httpClientSupplier.className=com.example.HttpClientSupplier
```

Replace `com.example.HttpClientSupplier` with the name of your own supplier class.

This class must be an instance of Java 8's `java.util.function.Supplier` interface,
and it must provide a `org.apache.http.client.HttpClient`.

It also must have a default constructor.

See `se.curity.examples.oauth.DefaultJwkHttpClientSupplier` for an example.
This will be used if no properties file is found.

## More Information

For more information, please contact [Curity](http://curity.io).

Copyright 2016 Curity AB
