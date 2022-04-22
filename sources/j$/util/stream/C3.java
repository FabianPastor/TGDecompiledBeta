package j$.util.stream;

import j$.util.u;
import j$.util.v;
import j$.util.w;
import j$.util.y;

abstract class C3 {
    static y b(CLASSNAMEf4 f4Var, y yVar, long j, long j2) {
        long d = d(j, j2);
        int i = A3.a[f4Var.ordinal()];
        if (i == 1) {
            return new D4(yVar, j, d);
        }
        if (i == 2) {
            return new x4((v) yVar, j, d);
        }
        if (i == 3) {
            return new z4((w) yVar, j, d);
        }
        if (i == 4) {
            return new v4((u) yVar, j, d);
        }
        throw new IllegalStateException("Unknown shape " + f4Var);
    }

    static long c(long j, long j2, long j3) {
        if (j >= 0) {
            return Math.max(-1, Math.min(j - j2, j3));
        }
        return -1;
    }

    /* access modifiers changed from: private */
    public static long d(long j, long j2) {
        long j3 = j2 >= 0 ? j + j2 : Long.MAX_VALUE;
        if (j3 >= 0) {
            return j3;
        }
        return Long.MAX_VALUE;
    }

    private static int e(long j) {
        return (j != -1 ? CLASSNAMEe4.u : 0) | CLASSNAMEe4.t;
    }

    public static U f(CLASSNAMEc cVar, long j, long j2) {
        if (j >= 0) {
            return new CLASSNAMEz3(cVar, CLASSNAMEf4.DOUBLE_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static M0 g(CLASSNAMEc cVar, long j, long j2) {
        if (j >= 0) {
            return new CLASSNAMEt3(cVar, CLASSNAMEf4.INT_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static CLASSNAMEf1 h(CLASSNAMEc cVar, long j, long j2) {
        if (j >= 0) {
            return new CLASSNAMEw3(cVar, CLASSNAMEf4.LONG_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static Stream i(CLASSNAMEc cVar, long j, long j2) {
        if (j >= 0) {
            return new CLASSNAMEq3(cVar, CLASSNAMEf4.REFERENCE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }
}
