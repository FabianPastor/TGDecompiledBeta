package org.telegram.messenger;

import java.math.BigInteger;
import org.telegram.tgnet.TLRPC;

public class SRPHelper {
    public static byte[] getBigIntegerBytes(BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes.length > 256) {
            byte[] correctedAuth = new byte[256];
            System.arraycopy(bytes, 1, correctedAuth, 0, 256);
            return correctedAuth;
        } else if (bytes.length >= 256) {
            return bytes;
        } else {
            byte[] correctedAuth2 = new byte[256];
            System.arraycopy(bytes, 0, correctedAuth2, 256 - bytes.length, bytes.length);
            for (int a = 0; a < 256 - bytes.length; a++) {
                correctedAuth2[a] = 0;
            }
            return correctedAuth2;
        }
    }

    public static byte[] getX(byte[] passwordBytes, TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo) {
        return Utilities.computeSHA256(algo.salt2, Utilities.computePBKDF2(Utilities.computeSHA256(algo.salt2, Utilities.computeSHA256(algo.salt1, passwordBytes, algo.salt1), algo.salt2), algo.salt1), algo.salt2);
    }

    public static BigInteger getV(byte[] passwordBytes, TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo) {
        BigInteger g = BigInteger.valueOf((long) algo.g);
        byte[] bigIntegerBytes = getBigIntegerBytes(g);
        return g.modPow(new BigInteger(1, getX(passwordBytes, algo)), new BigInteger(1, algo.p));
    }

    public static byte[] getVBytes(byte[] passwordBytes, TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo) {
        if (!Utilities.isGoodPrime(algo.p, algo.g)) {
            return null;
        }
        return getBigIntegerBytes(getV(passwordBytes, algo));
    }

    public static TLRPC.TL_inputCheckPasswordSRP startCheck(byte[] x_bytes, long srp_id, byte[] srp_B, TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo) {
        byte[] bArr = x_bytes;
        byte[] bArr2 = srp_B;
        TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow = algo;
        if (bArr == null || bArr2 == null || bArr2.length == 0) {
            long j = srp_id;
            return null;
        } else if (!Utilities.isGoodPrime(tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.p, tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.g)) {
            long j2 = srp_id;
            return null;
        } else {
            BigInteger g = BigInteger.valueOf((long) tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.g);
            byte[] g_bytes = getBigIntegerBytes(g);
            BigInteger p = new BigInteger(1, tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.p);
            BigInteger k = new BigInteger(1, Utilities.computeSHA256(tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.p, g_bytes));
            BigInteger x = new BigInteger(1, bArr);
            byte[] a_bytes = new byte[256];
            Utilities.random.nextBytes(a_bytes);
            BigInteger a = new BigInteger(1, a_bytes);
            byte[] A_bytes = getBigIntegerBytes(g.modPow(a, p));
            BigInteger B = new BigInteger(1, bArr2);
            if (B.compareTo(BigInteger.ZERO) <= 0) {
                BigInteger bigInteger = g;
                long j3 = srp_id;
                return null;
            } else if (B.compareTo(p) >= 0) {
                byte[] bArr3 = A_bytes;
                BigInteger bigInteger2 = g;
                long j4 = srp_id;
                return null;
            } else {
                byte[] B_bytes = getBigIntegerBytes(B);
                byte[] u_bytes = Utilities.computeSHA256(A_bytes, B_bytes);
                BigInteger u = new BigInteger(1, u_bytes);
                if (u.compareTo(BigInteger.ZERO) == 0) {
                    return null;
                }
                BigInteger B_kgx = B.subtract(k.multiply(g.modPow(x, p)).mod(p));
                byte[] bArr4 = u_bytes;
                if (B_kgx.compareTo(BigInteger.ZERO) < 0) {
                    B_kgx = B_kgx.add(p);
                }
                if (!Utilities.isGoodGaAndGb(B_kgx, p)) {
                    return null;
                }
                BigInteger S = B_kgx.modPow(a.add(u.multiply(x)), p);
                byte[] K_bytes = Utilities.computeSHA256(getBigIntegerBytes(S));
                BigInteger bigInteger3 = S;
                byte[] p_hash = Utilities.computeSHA256(tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.p);
                byte[] g_hash = Utilities.computeSHA256(g_bytes);
                BigInteger bigInteger4 = u;
                int i = 0;
                while (true) {
                    BigInteger g2 = g;
                    if (i < p_hash.length) {
                        p_hash[i] = (byte) (g_hash[i] ^ p_hash[i]);
                        i++;
                        g = g2;
                    } else {
                        TLRPC.TL_inputCheckPasswordSRP result = new TLRPC.TL_inputCheckPasswordSRP();
                        byte[] bArr5 = p_hash;
                        result.M1 = Utilities.computeSHA256(p_hash, Utilities.computeSHA256(tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt1), Utilities.computeSHA256(tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt2), A_bytes, B_bytes, K_bytes);
                        result.A = A_bytes;
                        byte[] bArr6 = A_bytes;
                        result.srp_id = srp_id;
                        return result;
                    }
                }
            }
        }
    }
}
