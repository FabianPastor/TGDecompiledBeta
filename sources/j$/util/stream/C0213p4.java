package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.C;
import java.util.Collection;
import java.util.stream.Node;

/* renamed from: j$.util.stream.p4  reason: case insensitive filesystem */
final class CLASSNAMEp4 {
    private static final CLASSNAMEt3 a = new O3();
    private static final CLASSNAMEo3 b = new M3();
    private static final CLASSNAMEq3 c = new N3();
    private static final CLASSNAMEm3 d = new L3();
    /* access modifiers changed from: private */
    public static final int[] e = new int[0];
    /* access modifiers changed from: private */
    public static final long[] f = new long[0];
    /* access modifiers changed from: private */
    public static final double[] g = new double[0];

    static CLASSNAMEt3 m(CLASSNAMEv6 shape) {
        int ordinal = shape.ordinal();
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
        throw new IllegalStateException("Unknown shape " + shape);
    }

    static CLASSNAMEt3 j(CLASSNAMEv6 shape, CLASSNAMEt3 left, CLASSNAMEt3 right) {
        int ordinal = shape.ordinal();
        if (ordinal == 0) {
            return new H3(left, right);
        }
        if (ordinal == 1) {
            return new E3((CLASSNAMEo3) left, (CLASSNAMEo3) right);
        }
        if (ordinal == 2) {
            return new F3((CLASSNAMEq3) left, (CLASSNAMEq3) right);
        }
        if (ordinal == 3) {
            return new D3((CLASSNAMEm3) left, (CLASSNAMEm3) right);
        }
        throw new IllegalStateException("Unknown shape " + shape);
    }

    static CLASSNAMEt3 z(Object[] array) {
        return new CLASSNAMEw3(array);
    }

    static CLASSNAMEt3 y(Collection c2) {
        return new CLASSNAMEx3(c2);
    }

    /* access modifiers changed from: package-private */
    public static CLASSNAMEk3 e(long exactSizeIfKnown, C c2) {
        if (exactSizeIfKnown < 0 || exactSizeIfKnown >= NUM) {
            return d();
        }
        return new Q3(exactSizeIfKnown, c2);
    }

    static CLASSNAMEk3 d() {
        return new CLASSNAMEi4();
    }

    static CLASSNAMEo3 w(int[] array) {
        return new R3(array);
    }

    static CLASSNAMEi3 s(long exactSizeIfKnown) {
        if (exactSizeIfKnown < 0 || exactSizeIfKnown >= NUM) {
            return r();
        }
        return new S3(exactSizeIfKnown);
    }

    static CLASSNAMEi3 r() {
        return new T3();
    }

    static CLASSNAMEq3 x(long[] array) {
        return new CLASSNAMEa4(array);
    }

    static CLASSNAMEj3 u(long exactSizeIfKnown) {
        if (exactSizeIfKnown < 0 || exactSizeIfKnown >= NUM) {
            return t();
        }
        return new CLASSNAMEb4(exactSizeIfKnown);
    }

    static CLASSNAMEj3 t() {
        return new CLASSNAMEc4();
    }

    static CLASSNAMEm3 v(double[] array) {
        return new I3(array);
    }

    static CLASSNAMEh3 l(long exactSizeIfKnown) {
        if (exactSizeIfKnown < 0 || exactSizeIfKnown >= NUM) {
            return k();
        }
        return new J3(exactSizeIfKnown);
    }

    static CLASSNAMEh3 k() {
        return new K3();
    }

    public static CLASSNAMEt3 f(CLASSNAMEq4 helper, Spliterator spliterator, boolean flattenTree, C c2) {
        long size = helper.p0(spliterator);
        if (size < 0 || !spliterator.hasCharacteristics(16384)) {
            Node<P_OUT> node = (CLASSNAMEt3) new B3(helper, c2, spliterator).invoke();
            return flattenTree ? n(node, c2) : node;
        } else if (size < NUM) {
            P_OUT[] array = (Object[]) c2.a((int) size);
            new CLASSNAMEg4(spliterator, helper, array).invoke();
            return z(array);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static CLASSNAMEo3 h(CLASSNAMEq4 helper, Spliterator spliterator, boolean flattenTree) {
        long size = helper.p0(spliterator);
        if (size < 0 || !spliterator.hasCharacteristics(16384)) {
            CLASSNAMEo3 node = (CLASSNAMEo3) new CLASSNAMEz3(helper, spliterator).invoke();
            return flattenTree ? p(node) : node;
        } else if (size < NUM) {
            int[] array = new int[((int) size)];
            new CLASSNAMEe4(spliterator, helper, array).invoke();
            return w(array);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static CLASSNAMEq3 i(CLASSNAMEq4 helper, Spliterator spliterator, boolean flattenTree) {
        long size = helper.p0(spliterator);
        if (size < 0 || !spliterator.hasCharacteristics(16384)) {
            CLASSNAMEq3 node = (CLASSNAMEq3) new A3(helper, spliterator).invoke();
            return flattenTree ? q(node) : node;
        } else if (size < NUM) {
            long[] array = new long[((int) size)];
            new CLASSNAMEf4(spliterator, helper, array).invoke();
            return x(array);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static CLASSNAMEm3 g(CLASSNAMEq4 helper, Spliterator spliterator, boolean flattenTree) {
        long size = helper.p0(spliterator);
        if (size < 0 || !spliterator.hasCharacteristics(16384)) {
            CLASSNAMEm3 node = (CLASSNAMEm3) new CLASSNAMEy3(helper, spliterator).invoke();
            return flattenTree ? o(node) : node;
        } else if (size < NUM) {
            double[] array = new double[((int) size)];
            new CLASSNAMEd4(spliterator, helper, array).invoke();
            return v(array);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static CLASSNAMEt3 n(CLASSNAMEt3 node, C c2) {
        if (node.w() <= 0) {
            return node;
        }
        long size = node.count();
        if (size < NUM) {
            T[] array = (Object[]) c2.a((int) size);
            new CLASSNAMEn4(node, array, 0).invoke();
            return z(array);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public static CLASSNAMEo3 p(CLASSNAMEo3 node) {
        if (node.w() <= 0) {
            return node;
        }
        long size = node.count();
        if (size < NUM) {
            int[] array = new int[((int) size)];
            new CLASSNAMEk4(node, array, 0).invoke();
            return w(array);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public static CLASSNAMEq3 q(CLASSNAMEq3 node) {
        if (node.w() <= 0) {
            return node;
        }
        long size = node.count();
        if (size < NUM) {
            long[] array = new long[((int) size)];
            new CLASSNAMEl4(node, array, 0).invoke();
            return x(array);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public static CLASSNAMEm3 o(CLASSNAMEm3 node) {
        if (node.w() <= 0) {
            return node;
        }
        long size = node.count();
        if (size < NUM) {
            double[] array = new double[((int) size)];
            new CLASSNAMEj4(node, array, 0).invoke();
            return v(array);
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
