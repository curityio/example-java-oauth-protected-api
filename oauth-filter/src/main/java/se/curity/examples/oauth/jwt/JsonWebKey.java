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
