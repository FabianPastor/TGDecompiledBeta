package j$.util.stream;

import j$.util.u;
/* loaded from: classes2.dex */
abstract class B3 {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static j$.util.u b(EnumCLASSNAMEe4 enumCLASSNAMEe4, j$.util.u uVar, long j, long j2) {
        long d = d(j, j2);
        int i = AbstractCLASSNAMEz3.a[enumCLASSNAMEe4.ordinal()];
        if (i != 1) {
            if (i == 2) {
                return new w4((u.a) uVar, j, d);
            }
            if (i == 3) {
                return new y4((j$.util.v) uVar, j, d);
            }
            if (i == 4) {
                return new u4((j$.util.t) uVar, j, d);
            }
            throw new IllegalStateException("Unknown shape " + enumCLASSNAMEe4);
        }
        return new C4(uVar, j, d);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long c(long j, long j2, long j3) {
        if (j >= 0) {
            return Math.max(-1L, Math.min(j - j2, j3));
        }
        return -1L;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long d(long j, long j2) {
        long j3 = j2 >= 0 ? j + j2 : Long.MAX_VALUE;
        if (j3 >= 0) {
            return j3;
        }
        return Long.MAX_VALUE;
    }

    private static int e(long j) {
        return (j != -1 ? EnumCLASSNAMEd4.u : 0) | EnumCLASSNAMEd4.t;
    }

    public static U f(AbstractCLASSNAMEc abstractCLASSNAMEc, long j, long j2) {
        if (j >= 0) {
            return new CLASSNAMEy3(abstractCLASSNAMEc, EnumCLASSNAMEe4.DOUBLE_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static IntStream g(AbstractCLASSNAMEc abstractCLASSNAMEc, long j, long j2) {
        if (j >= 0) {
            return new CLASSNAMEs3(abstractCLASSNAMEc, EnumCLASSNAMEe4.INT_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static InterfaceCLASSNAMEe1 h(AbstractCLASSNAMEc abstractCLASSNAMEc, long j, long j2) {
        if (j >= 0) {
            return new CLASSNAMEv3(abstractCLASSNAMEc, EnumCLASSNAMEe4.LONG_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static Stream i(AbstractCLASSNAMEc abstractCLASSNAMEc, long j, long j2) {
        if (j >= 0) {
            return new CLASSNAMEp3(abstractCLASSNAMEc, EnumCLASSNAMEe4.REFERENCE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }
}
