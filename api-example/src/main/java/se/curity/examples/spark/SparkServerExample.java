/*
 * Copyright (C) 2016 Curity AB.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.curity.examples.spark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.examples.oauth.AuthenticatedUser;
import se.curity.examples.oauth.OAuthFilter;
import se.curity.examples.oauth.OAuthJwtFilter;
import se.curity.examples.oauth.OAuthOpaqueFilter;
import spark.servlet.SparkApplication;

import javax.servlet.ServletException;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.port;

public class SparkServerExample implements SparkApplication
{

    private static final Logger _logger = LoggerFactory.getLogger(SparkServerExample.class);

    @Override
    public void init()
    {
        _logger.debug("Initializing OAuth protected API");
        get("/hello_world", (req, res) ->{
            AuthenticatedUser user = (AuthenticatedUser)req.attribute(OAuthFilter.PRINCIPAL);
            return "Hello "+ user.getSubject() + " from an OAuth protected world!";
        });
    }

    private void initStandalone() throws ServletException
    {
        init();
        OAuthFilter filter = getOpaqueFilter();
        before(((request, response) -> {
            filter.doFilter(request.raw(), response.raw(), null);
            if(response.raw().isCommitted())
            {
                halt();
            }
        }));
    }

    public static void main(String[] args) throws ServletException
    {
        port(9090);
        new SparkServerExample().initStandalone ();
    }

    private OAuthFilter getJwtFilter() throws ServletException
    {
        EmbeddedSparkJwtFilterConfig filterParams = new EmbeddedSparkJwtFilterConfig("localhost",
                "8443",
                "/oauth/v2/metadata/jwks",
                "read",
                "3600");
        OAuthFilter filter = new OAuthJwtFilter();

        filter.init(filterParams);
        return filter;
    }


    private OAuthFilter getOpaqueFilter() throws ServletException
    {
        EmbeddedSparkOpaqueFilterConfig filterParams = new EmbeddedSparkOpaqueFilterConfig("localhost",
                "8443",
                "/oauth/v2/introspection",
                "gateway-client",
                "Password1",
                "read");
        OAuthFilter filter = new OAuthOpaqueFilter();

        filter.init(filterParams);
        return filter;
    }


}
