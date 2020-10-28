package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.v;

/* renamed from: j$.util.stream.h4  reason: case insensitive filesystem */
final class CLASSNAMEh4 {
    private static final CLASSNAMEl3 a = new G3((CLASSNAMEm3) null);
    private static final CLASSNAMEi3 b = new E3();
    private static final CLASSNAMEj3 c = new F3();
    private static final CLASSNAMEh3 d = new D3();
    /* access modifiers changed from: private */
    public static final int[] e = new int[0];
    /* access modifiers changed from: private */
    public static final long[] f = new long[0];
    /* access modifiers changed from: private */
    public static final double[] g = new double[0];

    static CLASSNAMEg3 d(long j, v vVar) {
        return (j < 0 || j >= NUM) ? new CLASSNAMEa4() : new I3(j, vVar);
    }

    public static CLASSNAMEl3 e(CLASSNAMEi4 i4Var, Spliterator spliterator, boolean z, v vVar) {
        long p0 = i4Var.p0(spliterator);
        if (p0 < 0 || !spliterator.hasCharacteristics(16384)) {
            CLASSNAMEl3 l3Var = (CLASSNAMEl3) new CLASSNAMEt3(i4Var, vVar, spliterator).invoke();
            return z ? l(l3Var, vVar) : l3Var;
        } else if (p0 < NUM) {
            Object[] objArr = (Object[]) vVar.apply((int) p0);
            new Y3(spliterator, i4Var, objArr).invoke();
            return new CLASSNAMEo3(objArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static CLASSNAMEh3 f(CLASSNAMEi4 i4Var, Spliterator spliterator, boolean z) {
        long p0 = i4Var.p0(spliterator);
        if (p0 < 0 || !spliterator.hasCharacteristics(16384)) {
            CLASSNAMEh3 h3Var = (CLASSNAMEh3) new CLASSNAMEq3(i4Var, spliterator).invoke();
            return z ? m(h3Var) : h3Var;
        } else if (p0 < NUM) {
            double[] dArr = new double[((int) p0)];
            new V3(spliterator, i4Var, dArr).invoke();
            return new A3(dArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static CLASSNAMEi3 g(CLASSNAMEi4 i4Var, Spliterator spliterator, boolean z) {
        long p0 = i4Var.p0(spliterator);
        if (p0 < 0 || !spliterator.hasCharacteristics(16384)) {
            CLASSNAMEi3 i3Var = (CLASSNAMEi3) new CLASSNAMEr3(i4Var, spliterator).invoke();
            return z ? n(i3Var) : i3Var;
        } else if (p0 < NUM) {
            int[] iArr = new int[((int) p0)];
            new W3(spliterator, i4Var, iArr).invoke();
            return new J3(iArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static CLASSNAMEj3 h(CLASSNAMEi4 i4Var, Spliterator spliterator, boolean z) {
        long p0 = i4Var.p0(spliterator);
        if (p0 < 0 || !spliterator.hasCharacteristics(16384)) {
            CLASSNAMEj3 j3Var = (CLASSNAMEj3) new CLASSNAMEs3(i4Var, spliterator).invoke();
            return z ? o(j3Var) : j3Var;
        } else if (p0 < NUM) {
            long[] jArr = new long[((int) p0)];
            new X3(spliterator, i4Var, jArr).invoke();
            return new S3(jArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    static CLASSNAMEl3 i(CLASSNAMEh6 h6Var, CLASSNAMEl3 l3Var, CLASSNAMEl3 l3Var2) {
        int ordinal = h6Var.ordinal();
        if (ordinal == 0) {
            return new CLASSNAMEz3(l3Var, l3Var2);
        }
        if (ordinal == 1) {
            return new CLASSNAMEw3((CLASSNAMEi3) l3Var, (CLASSNAMEi3) l3Var2);
        }
        if (ordinal == 2) {
            return new CLASSNAMEx3((CLASSNAMEj3) l3Var, (CLASSNAMEj3) l3Var2);
        }
        if (ordinal == 3) {
            return new CLASSNAMEv3((CLASSNAMEh3) l3Var, (CLASSNAMEh3) l3Var2);
        }
        throw new IllegalStateException("Unknown shape " + h6Var);
    }

    static CLASSNAMEd3 j(long j) {
        return (j < 0 || j >= NUM) ? new C3() : new B3(j);
    }

    static CLASSNAMEl3 k(CLASSNAMEh6 h6Var) {
        int ordinal = h6Var.ordinal();
        if (ordinal == 0) {
            return a;
        }
        if (ordinal == 1) {
            return b;
        }
        if (ordinal == 2) {
            return c;
        }
        if (ordinal == 3) {
            return d;
        }
        throw new IllegalStateException("Unknown shape " + h6Var);
    }

    public static CLASSNAMEl3 l(CLASSNAMEl3 l3Var, v vVar) {
        if (l3Var.o() <= 0) {
            return l3Var;
        }
        long count = l3Var.count();
        if (count < NUM) {
            Object[] objArr = (Object[]) vVar.apply((int) count);
            new CLASSNAMEf4(l3Var, objArr, 0, (CLASSNAMEm3) null).invoke();
            return new CLASSNAMEo3(objArr);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public static CLASSNAMEh3 m(CLASSNAMEh3 h3Var) {
        if (h3Var.o() <= 0) {
            return h3Var;
        }
        long count = h3Var.count();
        if (count < NUM) {
            double[] dArr = new double[((int) count)];
            new CLASSNAMEb4(h3Var, dArr, 0, (CLASSNAMEm3) null).invoke();
            return new A3(dArr);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public static CLASSNAMEi3 n(CLASSNAMEi3 i3Var) {
        if (i3Var.o() <= 0) {
            return i3Var;
        }
        long count = i3Var.count();
        if (count < NUM) {
            int[] iArr = new int[((int) count)];
            new CLASSNAMEc4(i3Var, iArr, 0, (CLASSNAMEm3) null).invoke();
            return new J3(iArr);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public static CLASSNAMEj3 o(CLASSNAMEj3 j3Var) {
        if (j3Var.o() <= 0) {
            return j3Var;
        }
        long count = j3Var.count();
        if (count < NUM) {
            long[] jArr = new long[((int) count)];
            new CLASSNAMEd4(j3Var, jArr, 0, (CLASSNAMEm3) null).invoke();
            return new S3(jArr);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    static CLASSNAMEe3 p(long j) {
        return (j < 0 || j >= NUM) ? new L3() : new K3(j);
    }

    static CLASSNAMEf3 q(long j) {
        return (j < 0 || j >= NUM) ? new U3() : new T3(j);
    }
}
