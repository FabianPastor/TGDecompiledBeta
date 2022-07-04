package j$.util.stream;

import j$.util.t;
import j$.util.u;
import j$.util.v;

abstract class B3 {
    static u b(CLASSNAMEe4 e4Var, u uVar, long j, long j2) {
        long d = d(j, j2);
        int i = CLASSNAMEz3.a[e4Var.ordinal()];
        if (i == 1) {
            return new C4(uVar, j, d);
        }
        if (i == 2) {
            return new w4((u.a) uVar, j, d);
        }
        if (i == 3) {
            return new y4((v) uVar, j, d);
        }
        if (i == 4) {
            return new u4((t) uVar, j, d);
        }
        throw new IllegalStateException("Unknown shape " + e4Var);
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
        return (j != -1 ? CLASSNAMEd4.u : 0) | CLASSNAMEd4.t;
    }

    public static U f(CLASSNAMEc cVar, long j, long j2) {
        if (j >= 0) {
            return new CLASSNAMEy3(cVar, CLASSNAMEe4.DOUBLE_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static IntStream g(CLASSNAMEc cVar, long j, long j2) {
        if (j >= 0) {
            return new CLASSNAMEs3(cVar, CLASSNAMEe4.INT_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static CLASSNAMEe1 h(CLASSNAMEc cVar, long j, long j2) {
        if (j >= 0) {
            return new CLASSNAMEv3(cVar, CLASSNAMEe4.LONG_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static Stream i(CLASSNAMEc cVar, long j, long j2) {
        if (j >= 0) {
            return new CLASSNAMEp3(cVar, CLASSNAMEe4.REFERENCE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }
}
