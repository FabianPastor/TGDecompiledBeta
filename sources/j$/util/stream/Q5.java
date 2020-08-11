package j$.util.stream;

import j$.util.P;
import j$.util.S;
import j$.util.Spliterator;
import j$.util.U;
import j$.util.function.C;

final class Q5 {
    /* access modifiers changed from: private */
    public static long e(long size, long skip, long limit) {
        if (size >= 0) {
            return Math.max(-1, Math.min(size - skip, limit));
        }
        return -1;
    }

    /* access modifiers changed from: private */
    public static long f(long skip, long limit) {
        long sliceFence = limit >= 0 ? skip + limit : Long.MAX_VALUE;
        if (sliceFence >= 0) {
            return sliceFence;
        }
        return Long.MAX_VALUE;
    }

    /* access modifiers changed from: private */
    public static Spliterator n(CLASSNAMEv6 shape, Spliterator spliterator, long skip, long limit) {
        long sliceFence = f(skip, limit);
        int ordinal = shape.ordinal();
        if (ordinal == 0) {
            return new R6(spliterator, skip, sliceFence);
        }
        if (ordinal == 1) {
            return new O6((S) spliterator, skip, sliceFence);
        }
        if (ordinal == 2) {
            return new P6((U) spliterator, skip, sliceFence);
        }
        if (ordinal == 3) {
            return new N6((P) spliterator, skip, sliceFence);
        }
        throw new IllegalStateException("Unknown shape " + shape);
    }

    /* access modifiers changed from: private */
    public static C g() {
        return CLASSNAMEu0.a;
    }

    static /* synthetic */ Object[] i(int size) {
        return new Object[size];
    }

    public static Stream m(CLASSNAMEh1 upstream, long skip, long limit) {
        if (skip >= 0) {
            return new I5(upstream, CLASSNAMEv6.REFERENCE, h(limit), skip, limit);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + skip);
    }

    public static A2 k(CLASSNAMEh1 upstream, long skip, long limit) {
        if (skip >= 0) {
            return new K5(upstream, CLASSNAMEv6.INT_VALUE, h(limit), skip, limit);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + skip);
    }

    public static W2 l(CLASSNAMEh1 upstream, long skip, long limit) {
        if (skip >= 0) {
            return new M5(upstream, CLASSNAMEv6.LONG_VALUE, h(limit), skip, limit);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + skip);
    }

    public static M1 j(CLASSNAMEh1 upstream, long skip, long limit) {
        if (skip >= 0) {
            return new O5(upstream, CLASSNAMEv6.DOUBLE_VALUE, h(limit), skip, limit);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + skip);
    }

    private static int h(long limit) {
        return CLASSNAMEu6.y | (limit != -1 ? CLASSNAMEu6.z : 0);
    }
}
