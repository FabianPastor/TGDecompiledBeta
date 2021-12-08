package j$.util.stream;

import j$.util.function.m;
import j$.util.y;

/* renamed from: j$.util.stream.y2  reason: case insensitive filesystem */
abstract class CLASSNAMEy2 {
    private static final B1 a = new CLASSNAMEa2((C1) null);
    private static final CLASSNAMEx1 b = new Y1();
    private static final CLASSNAMEz1 c = new Z1();
    private static final CLASSNAMEv1 d = new X1();
    /* access modifiers changed from: private */
    public static final int[] e = new int[0];
    /* access modifiers changed from: private */
    public static final long[] f = new long[0];
    /* access modifiers changed from: private */
    public static final double[] g = new double[0];

    static CLASSNAMEt1 d(long j, m mVar) {
        return (j < 0 || j >= NUM) ? new CLASSNAMEu2() : new CLASSNAMEc2(j, mVar);
    }

    public static B1 e(CLASSNAMEz2 z2Var, y yVar, boolean z, m mVar) {
        long q0 = z2Var.q0(yVar);
        if (q0 < 0 || !yVar.hasCharacteristics(16384)) {
            B1 b1 = (B1) new I1(z2Var, mVar, yVar).invoke();
            return z ? l(b1, mVar) : b1;
        } else if (q0 < NUM) {
            Object[] objArr = (Object[]) mVar.apply((int) q0);
            new CLASSNAMEs2(yVar, z2Var, objArr).invoke();
            return new E1(objArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static CLASSNAMEv1 f(CLASSNAMEz2 z2Var, y yVar, boolean z) {
        long q0 = z2Var.q0(yVar);
        if (q0 < 0 || !yVar.hasCharacteristics(16384)) {
            CLASSNAMEv1 v1Var = (CLASSNAMEv1) new I1(z2Var, yVar, 0).invoke();
            return z ? m(v1Var) : v1Var;
        } else if (q0 < NUM) {
            double[] dArr = new double[((int) q0)];
            new CLASSNAMEp2(yVar, z2Var, dArr).invoke();
            return new U1(dArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static CLASSNAMEx1 g(CLASSNAMEz2 z2Var, y yVar, boolean z) {
        long q0 = z2Var.q0(yVar);
        if (q0 < 0 || !yVar.hasCharacteristics(16384)) {
            CLASSNAMEx1 x1Var = (CLASSNAMEx1) new I1(z2Var, yVar, 1).invoke();
            return z ? n(x1Var) : x1Var;
        } else if (q0 < NUM) {
            int[] iArr = new int[((int) q0)];
            new CLASSNAMEq2(yVar, z2Var, iArr).invoke();
            return new CLASSNAMEd2(iArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static CLASSNAMEz1 h(CLASSNAMEz2 z2Var, y yVar, boolean z) {
        long q0 = z2Var.q0(yVar);
        if (q0 < 0 || !yVar.hasCharacteristics(16384)) {
            CLASSNAMEz1 z1Var = (CLASSNAMEz1) new I1(z2Var, yVar, 2).invoke();
            return z ? o(z1Var) : z1Var;
        } else if (q0 < NUM) {
            long[] jArr = new long[((int) q0)];
            new CLASSNAMEr2(yVar, z2Var, jArr).invoke();
            return new CLASSNAMEm2(jArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    static B1 i(CLASSNAMEf4 f4Var, B1 b1, B1 b12) {
        int i = C1.a[f4Var.ordinal()];
        if (i == 1) {
            return new T1(b1, b12);
        }
        if (i == 2) {
            return new Q1((CLASSNAMEx1) b1, (CLASSNAMEx1) b12);
        }
        if (i == 3) {
            return new R1((CLASSNAMEz1) b1, (CLASSNAMEz1) b12);
        }
        if (i == 4) {
            return new P1((CLASSNAMEv1) b1, (CLASSNAMEv1) b12);
        }
        throw new IllegalStateException("Unknown shape " + f4Var);
    }

    static CLASSNAMEq1 j(long j) {
        return (j < 0 || j >= NUM) ? new W1() : new V1(j);
    }

    static B1 k(CLASSNAMEf4 f4Var) {
        int i = C1.a[f4Var.ordinal()];
        if (i == 1) {
            return a;
        }
        if (i == 2) {
            return b;
        }
        if (i == 3) {
            return c;
        }
        if (i == 4) {
            return d;
        }
        throw new IllegalStateException("Unknown shape " + f4Var);
    }

    public static B1 l(B1 b1, m mVar) {
        if (b1.p() <= 0) {
            return b1;
        }
        long count = b1.count();
        if (count < NUM) {
            Object[] objArr = (Object[]) mVar.apply((int) count);
            new CLASSNAMEw2(b1, objArr, 0, (C1) null).invoke();
            return new E1(objArr);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public static CLASSNAMEv1 m(CLASSNAMEv1 v1Var) {
        if (v1Var.p() <= 0) {
            return v1Var;
        }
        long count = v1Var.count();
        if (count < NUM) {
            double[] dArr = new double[((int) count)];
            new CLASSNAMEv2(v1Var, dArr, 0).invoke();
            return new U1(dArr);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public static CLASSNAMEx1 n(CLASSNAMEx1 x1Var) {
        if (x1Var.p() <= 0) {
            return x1Var;
        }
        long count = x1Var.count();
        if (count < NUM) {
            int[] iArr = new int[((int) count)];
            new CLASSNAMEv2(x1Var, iArr, 0).invoke();
            return new CLASSNAMEd2(iArr);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public static CLASSNAMEz1 o(CLASSNAMEz1 z1Var) {
        if (z1Var.p() <= 0) {
            return z1Var;
        }
        long count = z1Var.count();
        if (count < NUM) {
            long[] jArr = new long[((int) count)];
            new CLASSNAMEv2(z1Var, jArr, 0).invoke();
            return new CLASSNAMEm2(jArr);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    static CLASSNAMEr1 p(long j) {
        return (j < 0 || j >= NUM) ? new CLASSNAMEf2() : new CLASSNAMEe2(j);
    }

    static CLASSNAMEs1 q(long j) {
        return (j < 0 || j >= NUM) ? new CLASSNAMEo2() : new CLASSNAMEn2(j);
    }
}
