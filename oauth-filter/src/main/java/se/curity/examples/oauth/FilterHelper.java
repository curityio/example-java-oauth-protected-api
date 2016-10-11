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

package se.curity.examples.oauth;

import com.google.common.collect.*;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;

// intentionally package-private
final class FilterHelper
{
    private FilterHelper()
    {
        // no instantiation - static functions only
    }

    static ImmutableMultimap<String, String> initParamsMapFrom(FilterConfig config)
    {
        Multimap<String, String> result = Multimaps.newListMultimap(
                new LinkedHashMap<>(),
                ArrayList::new);

        Enumeration<?> names = config.getInitParameterNames();
        while (names.hasMoreElements())
        {
            String name = names.nextElement().toString();
            if (config.getInitParameter(name) != null)
            {
                result.put(name, config.getInitParameter(name));
            }
        }
        return ImmutableMultimap.copyOf(result);
    }

    static String getInitParamValue(String name, Multimap<String, String> initParams)
    {
        Optional<String> value = getSingleValue(name, initParams);
        if (value.isPresent())
        {
            return value.get();
        }
        else
        {
            throw new IllegalStateException(missingInitParamMessage(name));
        }
    }

    static <T> T getInitParamValue(String name, Multimap<String, String> initParams,
                                   Function<String, T> converter)
    {
        return converter.apply(getInitParamValue(name, initParams));
    }

    static <T> Optional<T> getOptionalInitParamValue(String name, Multimap<String, String> initParams,
                                                     Function<String, T> converter)
    {
        Optional<String> value = getSingleValue(name, initParams);
        if (value.isPresent())
        {
            return Optional.ofNullable(converter.apply(value.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    private static Optional<String> getSingleValue(String name, Multimap<String, String> initParams)
    {
        Collection<String> values = initParams.get(name);
        if (values.size() > 1)
        {
            throw new IllegalStateException(
                    String.format("More than one value for parameter [%s]", name));
        }
        return Optional.ofNullable(Iterables.getFirst(values, null));
    }

    static String missingInitParamMessage(String paramName)
    {
        return String.format("%s - missing required initParam [%s]",
                OAuthFilter.class.getName(),
                paramName);
    }


}
