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

class JsonWebKey
{
    private JsonWebKeyType kty;
    private String kid;
    private String use;
    private String alg;
    private String x;
    private String y;
    private String crv;
    private String n;
    private String e;

    public String getKid()
    {
        return kid;
    }

    public JsonWebKeyType getKty(){
        return kty;
    }

    public String getUse()
    {
        return use;
    }

    public String getX()
    {
        return x;
    }

    public String getY()
    {
        return y;
    }

    public String getCrv()
    {
        return crv;
    }

    public String getModulus()
    {
        return n;
    }

    public String getExponent()
    {
        return e;
    }

    public String getAlg()
    {
        return alg;
    }
}
