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

import org.apache.commons.codec.binary.Base64;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

class RsaPublicKeyCreator
{

    public static PublicKey createPublicKey(String modulus, String exponent) throws InvalidKeySpecException, NoSuchAlgorithmException
    {
        Base64 decoder = new Base64(true);
        BigInteger bigModulus = new BigInteger(1, decoder.decode(modulus));
        BigInteger bigExponent = new BigInteger(1, decoder.decode(exponent));
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(bigModulus, bigExponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        return publicKey;
    }

}
