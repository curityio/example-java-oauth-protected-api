/*
 * Copyright (C) 2016 Curity AB. All rights reserved.
 *
 * The contents of this file are the property of Curity AB.
 * You may not copy or use this file, in either source code
 * or executable form, except in compliance with terms
 * set by Curity AB.
 *
 * For further information, please contact Curity AB.
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
                                        String scope, String minKidReloadTimeInSeconds)
    {
        _params = new HashMap<>();
        _params.put("oauthHost", oauthHost);
        _params.put("oauthPort", oauthPort);
        _params.put("jsonWebKeysPath", jsonWebKeysPath);
        _params.put("scope", scope);
        _params.put("minKidReloadTimeInSeconds", minKidReloadTimeInSeconds);
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
