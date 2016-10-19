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

package se.curity.examples.oauth.jwt;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.util.Map;
import java.util.Optional;

public interface JwtValidator extends Closeable
{
    /**
     * The Jwt validator is a class that takes a JWT and makes sure the Signature is valid
     * @param jwt
     * @return the content of the token body if token signature is valid, otherwise null
     * @throws JwtValidationException
     */
    public @Nullable Map<String, Object> validate(String jwt) throws JwtValidationException;

    /**
     * Validates both the signature and the content of the token
     * Returns a map with the contents if valid, and an empty Optional for invalid tokens
     * @param jwt
     * @return
     * @throws JwtValidationException
     */
    public  Optional<Map<String, Object>>  validateAll(String jwt,String audience, String issuer) throws JwtValidationException;
}
