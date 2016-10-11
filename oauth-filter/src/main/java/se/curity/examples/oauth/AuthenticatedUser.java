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


import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.Map;

public class AuthenticatedUser
{
    private final String _sub;
    private final String _scope;

    public AuthenticatedUser(String subject, @Nullable String scope)
    {
        _sub = subject;
        _scope = scope;
    }

    public String getSubject()
    {
        return _sub;
    }

    public @Nullable String getScope()
    {
        return _scope;
    }

    public static AuthenticatedUser fromMap(Map<String, Object> tokenData)
    {
        Preconditions.checkNotNull(tokenData.get("sub"));
        String sub = (String)tokenData.get("sub");
        @Nullable String scope = (String) tokenData.get("scope");

        return new AuthenticatedUser(sub, scope);
    }
}
