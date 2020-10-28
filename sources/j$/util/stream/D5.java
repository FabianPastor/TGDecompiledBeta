package j$.util.stream;

import j$.util.C;
import j$.util.D;
import j$.util.E;
import j$.util.Spliterator;

final class D5 {
    static Spliterator b(CLASSNAMEh6 h6Var, Spliterator spliterator, long j, long j2) {
        long d = d(j, j2);
        int ordinal = h6Var.ordinal();
        if (ordinal == 0) {
            return new y6(spliterator, j, d);
        }
        if (ordinal == 1) {
            return new v6((D) spliterator, j, d);
        }
        if (ordinal == 2) {
            return new w6((E) spliterator, j, d);
        }
        if (ordinal == 3) {
            return new u6((C) spliterator, j, d);
        }
        throw new IllegalStateException("Unknown shape " + h6Var);
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
        return (j != -1 ? CLASSNAMEg6.z : 0) | CLASSNAMEg6.y;
    }

    public static L1 f(CLASSNAMEh1 h1Var, long j, long j2) {
        if (j >= 0) {
            return new B5(h1Var, CLASSNAMEh6.DOUBLE_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static CLASSNAMEx2 g(CLASSNAMEh1 h1Var, long j, long j2) {
        if (j >= 0) {
            return new CLASSNAMEx5(h1Var, CLASSNAMEh6.INT_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static T2 h(CLASSNAMEh1 h1Var, long j, long j2) {
        if (j >= 0) {
            return new CLASSNAMEz5(h1Var, CLASSNAMEh6.LONG_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static Stream i(CLASSNAMEh1 h1Var, long j, long j2) {
        if (j >= 0) {
            return new CLASSNAMEv5(h1Var, CLASSNAMEh6.REFERENCE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }
}
