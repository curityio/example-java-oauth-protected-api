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

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * THIS IS NOT A PRODUCTION CLASS.
 * It is an override to run the filter using SparkJava without web.xml
 * It is useful for testing and development purposes only.
 */
public class EmbeddedSparkJwtFilterConfig implements FilterConfig
{
    private final Map<String,String> _params;

    public EmbeddedSparkJwtFilterConfig(String oauthHost, String oauthPort,
                                        String jsonWebKeysPath,
                                        String scope, String minKidReloadTimeInSeconds,
                                        String issuer, String audience)
    {
        _params = new HashMap<>();
        _params.put("oauthHost", oauthHost);
        _params.put("oauthPort", oauthPort);
        _params.put("jsonWebKeysPath", jsonWebKeysPath);
        _params.put("scope", scope);
        _params.put("minKidReloadTimeInSeconds", minKidReloadTimeInSeconds);
        _params.put("audience", audience);
        _params.put("issuer", issuer);
    }

    @Override
    public String getFilterName()
    {
        return "EmbeddedSparkJwtFilterConfig";
    }

    @Override
    public ServletContext getServletContext()
    {
        return null;
    }

    @Override
    public String getInitParameter(String s)
    {
        return _params.get(s);
    }

    @Override
    public Enumeration<String> getInitParameterNames()
    {
        return new EnumerationFromIteration(_params.keySet().iterator());
    }

    public class EnumerationFromIteration implements Enumeration {

        private Iterator iterator;

        EnumerationFromIteration(Iterator iterator){
            this.iterator = iterator;
        }


        public EnumerationFromIteration(Map map) {
            iterator = map.keySet().iterator();
        }


        @Override
        public boolean hasMoreElements() {
            return iterator.hasNext();
        }


        @Override
        public Object nextElement() {
            return iterator.next();
        }

    }
}
