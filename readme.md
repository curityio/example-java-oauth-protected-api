# OAuth Protected API Example

[![Quality](https://img.shields.io/badge/quality-demo-red)](https://curity.io/resources/code-examples/status/)
[![Availability](https://img.shields.io/badge/availability-source-blue)](https://curity.io/resources/code-examples/status/)

This project contains an example web api that uses Curity's OAuth filter library. The example includes a minimal web-server
using `Spark` [sparkjava](http://sparkjava.com) that uses the filter in its `before` clause.

Depending on the format of the access token, there are two approaches that can be taken.

1. If the token is a Json Web Token (JWT) then validate the token using a public key
2. If the token is a reference (opaque) token, then validate by calling the OAuth server's
[introspection](https://tools.ietf.org/search/rfc7662) endpoint.

Each approach can be handled by using a proper `OAuthFilter` implementation: `OAuthJwtFilter` or `OAuthOpaqueFilter`.
These implement Servlet filters `Filter` and can be used to protect APIs build using Java.

## Filter overview

The filter is build to perform two tasks.

1. Validate the integrity of the incoming access token.
2. Authorize the operation by validating the scopes in the access token against the configured scopes.

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
`api-example-3.0.0-jar-with-dependencies.jar`. This is a runnable JAR which can be
run with the following command:

```
java -jar api-example/target/api-example-3.0.0-jar-with-dependencies.jar
```

To learn how to provide your own HTTP client, check the [filter's documentation](https://github.com/curityio/oauth-filter-for-java#providing-an-external-httpclient).

*Note* Unsafe HTTP clients should *NEVER* be used in production.

## Configuring the Filter

To configure the filter, use the `web.xml` file of your application as shown in the `server-example` project.

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

## More Information

For more information, please contact [Curity](http://curity.io).

Copyright 2016 Curity AB
