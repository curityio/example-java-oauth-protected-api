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
