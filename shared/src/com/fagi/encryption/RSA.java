package com.fagi.encryption;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by Marcus on 30-05-2016.
 */
public class RSA implements EncryptionAlgorithm {
    private final Random random;
    private final int keyLength;
    private final BigInteger e = BigInteger.valueOf(65537);
    private Key publicKey, secretKey;

    public RSA(Random random, int keyLength) {
        this.random = random;
        this.keyLength = keyLength;
    }

    private BigInteger generatePrime(int k, BigInteger e) {
        BigInteger res = null;

        while(res == null) {
            BigInteger prime = BigInteger.probablePrime(k, random);
            if(prime.subtract(BigInteger.ONE).gcd(e).equals(BigInteger.ONE)) {
                res = prime;
            }
        }

        return res;
    }

    public void generateKey() {
        BigInteger p = generatePrime(keyLength, e);
        BigInteger q = generatePrime(keyLength, e);
        BigInteger n = p.multiply(q);
        BigInteger d = e.modInverse(p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)));
        publicKey = new Key(n, e);
        secretKey = new Key(n, d);
    }

    public BigInteger encrypt (Key publicKey, byte[] msg) {
        BigInteger m = new BigInteger(msg);
        return m.modPow(publicKey.getExponent(), publicKey.getN());
    }

    public byte[] decrypt (BigInteger cipher) {
        return cipher.modPow(secretKey.getExponent(), secretKey.getN()).toByteArray();
    }

    public Key getPublicKey() { return this.publicKey; }
}