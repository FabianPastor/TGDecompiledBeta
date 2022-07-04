package j$.util.stream;

import j$.util.function.m;
import j$.util.u;

/* renamed from: j$.util.stream.x2  reason: case insensitive filesystem */
abstract class CLASSNAMEx2 {
    private static final A1 a = new Z1((B1) null);
    private static final CLASSNAMEw1 b = new X1();
    private static final CLASSNAMEy1 c = new Y1();
    private static final CLASSNAMEu1 d = new W1();
    /* access modifiers changed from: private */
    public static final int[] e = new int[0];
    /* access modifiers changed from: private */
    public static final long[] f = new long[0];
    /* access modifiers changed from: private */
    public static final double[] g = new double[0];

    static CLASSNAMEs1 d(long j, m mVar) {
        return (j < 0 || j >= NUM) ? new CLASSNAMEt2() : new CLASSNAMEb2(j, mVar);
    }

    public static A1 e(CLASSNAMEy2 y2Var, u uVar, boolean z, m mVar) {
        long q0 = y2Var.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            A1 a1 = (A1) new H1(y2Var, mVar, uVar).invoke();
            return z ? l(a1, mVar) : a1;
        } else if (q0 < NUM) {
            Object[] objArr = (Object[]) mVar.apply((int) q0);
            new CLASSNAMEr2(uVar, y2Var, objArr).invoke();
            return new D1(objArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static CLASSNAMEu1 f(CLASSNAMEy2 y2Var, u uVar, boolean z) {
        long q0 = y2Var.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            CLASSNAMEu1 u1Var = (CLASSNAMEu1) new H1(y2Var, uVar, 0).invoke();
            return z ? m(u1Var) : u1Var;
        } else if (q0 < NUM) {
            double[] dArr = new double[((int) q0)];
            new CLASSNAMEo2(uVar, y2Var, dArr).invoke();
            return new T1(dArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static CLASSNAMEw1 g(CLASSNAMEy2 y2Var, u uVar, boolean z) {
        long q0 = y2Var.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            CLASSNAMEw1 w1Var = (CLASSNAMEw1) new H1(y2Var, uVar, 1).invoke();
            return z ? n(w1Var) : w1Var;
        } else if (q0 < NUM) {
            int[] iArr = new int[((int) q0)];
            new CLASSNAMEp2(uVar, y2Var, iArr).invoke();
            return new CLASSNAMEc2(iArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static CLASSNAMEy1 h(CLASSNAMEy2 y2Var, u uVar, boolean z) {
        long q0 = y2Var.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            CLASSNAMEy1 y1Var = (CLASSNAMEy1) new H1(y2Var, uVar, 2).invoke();
            return z ? o(y1Var) : y1Var;
        } else if (q0 < NUM) {
            long[] jArr = new long[((int) q0)];
            new CLASSNAMEq2(uVar, y2Var, jArr).invoke();
            return new CLASSNAMEl2(jArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    static A1 i(CLASSNAMEe4 e4Var, A1 a1, A1 a12) {
        int i = B1.a[e4Var.ordinal()];
        if (i == 1) {
            return new S1(a1, a12);
        }
        if (i == 2) {
            return new P1((CLASSNAMEw1) a1, (CLASSNAMEw1) a12);
        }
        if (i == 3) {
            return new Q1((CLASSNAMEy1) a1, (CLASSNAMEy1) a12);
        }
        if (i == 4) {
            return new O1((CLASSNAMEu1) a1, (CLASSNAMEu1) a12);
        }
        throw new IllegalStateException("Unknown shape " + e4Var);
    }

    static CLASSNAMEp1 j(long j) {
        return (j < 0 || j >= NUM) ? new V1() : new U1(j);
    }

    static A1 k(CLASSNAMEe4 e4Var) {
        int i = B1.a[e4Var.ordinal()];
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
        throw new IllegalStateException("Unknown shape " + e4Var);
    }

    public static A1 l(A1 a1, m mVar) {
        if (a1.p() <= 0) {
            return a1;
        }
        long count = a1.count();
        if (count < NUM) {
            Object[] objArr = (Object[]) mVar.apply((int) count);
            new CLASSNAMEv2(a1, objArr, 0, (B1) null).invoke();
            return new D1(objArr);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public static CLASSNAMEu1 m(CLASSNAMEu1 u1Var) {
        if (u1Var.p() <= 0) {
            return u1Var;
        }
        long count = u1Var.count();
        if (count < NUM) {
            double[] dArr = new double[((int) count)];
            new CLASSNAMEu2(u1Var, dArr, 0).invoke();
            return new T1(dArr);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public static CLASSNAMEw1 n(CLASSNAMEw1 w1Var) {
        if (w1Var.p() <= 0) {
            return w1Var;
        }
        long count = w1Var.count();
        if (count < NUM) {
            int[] iArr = new int[((int) count)];
            new CLASSNAMEu2(w1Var, iArr, 0).invoke();
            return new CLASSNAMEc2(iArr);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public static CLASSNAMEy1 o(CLASSNAMEy1 y1Var) {
        if (y1Var.p() <= 0) {
            return y1Var;
        }
        long count = y1Var.count();
        if (count < NUM) {
            long[] jArr = new long[((int) count)];
            new CLASSNAMEu2(y1Var, jArr, 0).invoke();
            return new CLASSNAMEl2(jArr);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    static CLASSNAMEq1 p(long j) {
        return (j < 0 || j >= NUM) ? new CLASSNAMEe2() : new CLASSNAMEd2(j);
    }

    static CLASSNAMEr1 q(long j) {
        return (j < 0 || j >= NUM) ? new CLASSNAMEn2() : new CLASSNAMEm2(j);
    }
}
