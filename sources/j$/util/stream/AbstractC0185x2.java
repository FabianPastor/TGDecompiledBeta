package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.x2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public abstract class AbstractCLASSNAMEx2 {
    private static final A1 a = new Z1(null);
    private static final InterfaceCLASSNAMEw1 b = new X1();
    private static final InterfaceCLASSNAMEy1 c = new Y1();
    private static final InterfaceCLASSNAMEu1 d = new W1();
    private static final int[] e = new int[0];
    private static final long[] f = new long[0];
    private static final double[] g = new double[0];

    /* JADX INFO: Access modifiers changed from: package-private */
    public static InterfaceCLASSNAMEs1 d(long j, j$.util.function.m mVar) {
        return (j < 0 || j >= NUM) ? new CLASSNAMEt2() : new CLASSNAMEb2(j, mVar);
    }

    public static A1 e(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        long q0 = abstractCLASSNAMEy2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            A1 a1 = (A1) new H1(abstractCLASSNAMEy2, mVar, uVar).invoke();
            return z ? l(a1, mVar) : a1;
        } else if (q0 < NUM) {
            Object[] objArr = (Object[]) mVar.apply((int) q0);
            new CLASSNAMEr2(uVar, abstractCLASSNAMEy2, objArr).invoke();
            return new D1(objArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static InterfaceCLASSNAMEu1 f(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, boolean z) {
        long q0 = abstractCLASSNAMEy2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            InterfaceCLASSNAMEu1 interfaceCLASSNAMEu1 = (InterfaceCLASSNAMEu1) new H1(abstractCLASSNAMEy2, uVar, 0).invoke();
            return z ? m(interfaceCLASSNAMEu1) : interfaceCLASSNAMEu1;
        } else if (q0 < NUM) {
            double[] dArr = new double[(int) q0];
            new CLASSNAMEo2(uVar, abstractCLASSNAMEy2, dArr).invoke();
            return new T1(dArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static InterfaceCLASSNAMEw1 g(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, boolean z) {
        long q0 = abstractCLASSNAMEy2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            InterfaceCLASSNAMEw1 interfaceCLASSNAMEw1 = (InterfaceCLASSNAMEw1) new H1(abstractCLASSNAMEy2, uVar, 1).invoke();
            return z ? n(interfaceCLASSNAMEw1) : interfaceCLASSNAMEw1;
        } else if (q0 < NUM) {
            int[] iArr = new int[(int) q0];
            new CLASSNAMEp2(uVar, abstractCLASSNAMEy2, iArr).invoke();
            return new CLASSNAMEc2(iArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static InterfaceCLASSNAMEy1 h(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, boolean z) {
        long q0 = abstractCLASSNAMEy2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            InterfaceCLASSNAMEy1 interfaceCLASSNAMEy1 = (InterfaceCLASSNAMEy1) new H1(abstractCLASSNAMEy2, uVar, 2).invoke();
            return z ? o(interfaceCLASSNAMEy1) : interfaceCLASSNAMEy1;
        } else if (q0 < NUM) {
            long[] jArr = new long[(int) q0];
            new CLASSNAMEq2(uVar, abstractCLASSNAMEy2, jArr).invoke();
            return new CLASSNAMEl2(jArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static A1 i(EnumCLASSNAMEe4 enumCLASSNAMEe4, A1 a1, A1 a12) {
        int i = B1.a[enumCLASSNAMEe4.ordinal()];
        if (i != 1) {
            if (i == 2) {
                return new P1((InterfaceCLASSNAMEw1) a1, (InterfaceCLASSNAMEw1) a12);
            }
            if (i == 3) {
                return new Q1((InterfaceCLASSNAMEy1) a1, (InterfaceCLASSNAMEy1) a12);
            }
            if (i == 4) {
                return new O1((InterfaceCLASSNAMEu1) a1, (InterfaceCLASSNAMEu1) a12);
            }
            throw new IllegalStateException("Unknown shape " + enumCLASSNAMEe4);
        }
        return new S1(a1, a12);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static InterfaceCLASSNAMEp1 j(long j) {
        return (j < 0 || j >= NUM) ? new V1() : new U1(j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static A1 k(EnumCLASSNAMEe4 enumCLASSNAMEe4) {
        int i = B1.a[enumCLASSNAMEe4.ordinal()];
        if (i != 1) {
            if (i == 2) {
                return b;
            }
            if (i == 3) {
                return c;
            }
            if (i == 4) {
                return d;
            }
            throw new IllegalStateException("Unknown shape " + enumCLASSNAMEe4);
        }
        return a;
    }

    public static A1 l(A1 a1, j$.util.function.m mVar) {
        if (a1.p() > 0) {
            long count = a1.count();
            if (count < NUM) {
                Object[] objArr = (Object[]) mVar.apply((int) count);
                new CLASSNAMEv2(a1, objArr, 0, (B1) null).invoke();
                return new D1(objArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return a1;
    }

    public static InterfaceCLASSNAMEu1 m(InterfaceCLASSNAMEu1 interfaceCLASSNAMEu1) {
        if (interfaceCLASSNAMEu1.p() > 0) {
            long count = interfaceCLASSNAMEu1.count();
            if (count < NUM) {
                double[] dArr = new double[(int) count];
                new CLASSNAMEu2(interfaceCLASSNAMEu1, dArr, 0).invoke();
                return new T1(dArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return interfaceCLASSNAMEu1;
    }

    public static InterfaceCLASSNAMEw1 n(InterfaceCLASSNAMEw1 interfaceCLASSNAMEw1) {
        if (interfaceCLASSNAMEw1.p() > 0) {
            long count = interfaceCLASSNAMEw1.count();
            if (count < NUM) {
                int[] iArr = new int[(int) count];
                new CLASSNAMEu2(interfaceCLASSNAMEw1, iArr, 0).invoke();
                return new CLASSNAMEc2(iArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return interfaceCLASSNAMEw1;
    }

    public static InterfaceCLASSNAMEy1 o(InterfaceCLASSNAMEy1 interfaceCLASSNAMEy1) {
        if (interfaceCLASSNAMEy1.p() > 0) {
            long count = interfaceCLASSNAMEy1.count();
            if (count < NUM) {
                long[] jArr = new long[(int) count];
                new CLASSNAMEu2(interfaceCLASSNAMEy1, jArr, 0).invoke();
                return new CLASSNAMEl2(jArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return interfaceCLASSNAMEy1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static InterfaceCLASSNAMEq1 p(long j) {
        return (j < 0 || j >= NUM) ? new CLASSNAMEe2() : new CLASSNAMEd2(j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static InterfaceCLASSNAMEr1 q(long j) {
        return (j < 0 || j >= NUM) ? new CLASSNAMEn2() : new CLASSNAMEm2(j);
    }
}
