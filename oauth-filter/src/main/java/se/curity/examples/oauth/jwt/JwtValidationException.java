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

public class JwtValidationException extends Exception
{
    public JwtValidationException(String msg)
    {
        super(msg);
    }
    public JwtValidationException(String msg, Throwable t)
    {
        super(msg,t);
    }

}
