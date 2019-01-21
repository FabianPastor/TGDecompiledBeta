package org.telegram.messenger;

import java.math.BigInteger;
import org.telegram.tgnet.TLRPC.TL_inputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;

public class SRPHelper {
    public static byte[] getBigIntegerBytes(BigInteger value) {
        byte[] bytes = value.toByteArray();
        byte[] correctedAuth;
        if (bytes.length > 256) {
            correctedAuth = new byte[256];
            System.arraycopy(bytes, 1, correctedAuth, 0, 256);
            return correctedAuth;
        } else if (bytes.length >= 256) {
            return bytes;
        } else {
            correctedAuth = new byte[256];
            System.arraycopy(bytes, 0, correctedAuth, 256 - bytes.length, bytes.length);
            for (int a = 0; a < 256 - bytes.length; a++) {
                correctedAuth[a] = (byte) 0;
            }
            return correctedAuth;
        }
    }

    public static byte[] getX(byte[] passwordBytes, TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo) {
        byte[] x_bytes = Utilities.computeSHA256(algo.salt1, passwordBytes, algo.salt1);
        x_bytes = Utilities.computePBKDF2(Utilities.computeSHA256(algo.salt2, x_bytes, algo.salt2), algo.salt1);
        return Utilities.computeSHA256(algo.salt2, x_bytes, algo.salt2);
    }

    public static BigInteger getV(byte[] passwordBytes, TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo) {
        BigInteger g = BigInteger.valueOf((long) algo.g);
        byte[] g_bytes = getBigIntegerBytes(g);
        return g.modPow(new BigInteger(1, getX(passwordBytes, algo)), new BigInteger(1, algo.p));
    }

    public static byte[] getVBytes(byte[] passwordBytes, TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo) {
        if (Utilities.isGoodPrime(algo.p, algo.g)) {
            return getBigIntegerBytes(getV(passwordBytes, algo));
        }
        return null;
    }

    public static TL_inputCheckPasswordSRP startCheck(byte[] x_bytes, long srp_id, byte[] srp_B, TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo) {
        if (x_bytes == null || srp_B == null || srp_B.length == 0 || !Utilities.isGoodPrime(algo.p, algo.g)) {
            return null;
        }
        BigInteger g = BigInteger.valueOf((long) algo.g);
        byte[] g_bytes = getBigIntegerBytes(g);
        BigInteger bigInteger = new BigInteger(1, algo.p);
        bigInteger = new BigInteger(1, Utilities.computeSHA256(algo.p, g_bytes));
        bigInteger = new BigInteger(1, x_bytes);
        byte[] a_bytes = new byte[256];
        Utilities.random.nextBytes(a_bytes);
        BigInteger a = new BigInteger(1, a_bytes);
        byte[] A_bytes = getBigIntegerBytes(g.modPow(a, bigInteger));
        BigInteger B = new BigInteger(1, srp_B);
        if (B.compareTo(BigInteger.ZERO) <= 0 || B.compareTo(bigInteger) >= 0) {
            return null;
        }
        byte[] B_bytes = getBigIntegerBytes(B);
        bigInteger = new BigInteger(1, Utilities.computeSHA256(A_bytes, B_bytes));
        if (bigInteger.compareTo(BigInteger.ZERO) == 0) {
            return null;
        }
        BigInteger B_kgx = B.subtract(bigInteger.multiply(g.modPow(bigInteger, bigInteger)).mod(bigInteger));
        if (B_kgx.compareTo(BigInteger.ZERO) < 0) {
            B_kgx = B_kgx.add(bigInteger);
        }
        if (!Utilities.isGoodGaAndGb(B_kgx, bigInteger)) {
            return null;
        }
        byte[] K_bytes = Utilities.computeSHA256(getBigIntegerBytes(B_kgx.modPow(a.add(bigInteger.multiply(bigInteger)), bigInteger)));
        byte[] p_hash = Utilities.computeSHA256(algo.p);
        byte[] g_hash = Utilities.computeSHA256(g_bytes);
        for (int i = 0; i < p_hash.length; i++) {
            p_hash[i] = (byte) (g_hash[i] ^ p_hash[i]);
        }
        TL_inputCheckPasswordSRP result = new TL_inputCheckPasswordSRP();
        result.M1 = Utilities.computeSHA256(p_hash, Utilities.computeSHA256(algo.salt1), Utilities.computeSHA256(algo.salt2), A_bytes, B_bytes, K_bytes);
        result.A = A_bytes;
        result.srp_id = srp_id;
        return result;
    }
}
