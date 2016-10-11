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
