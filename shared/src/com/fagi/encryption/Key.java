package com.fagi.encryption;

import java.math.BigInteger;

/**
 * Created by Marcus on 30-05-2016.
 */
public class Key {

    private final BigInteger n;
    private final BigInteger exponent;

    public Key(BigInteger n, BigInteger exponent) {
        this.n = n;
        this.exponent = exponent;
    }

    public BigInteger getN() { return this.n; }
    public BigInteger getExponent() { return this.exponent; }
}
