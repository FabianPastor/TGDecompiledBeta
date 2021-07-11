package j$.util.stream;

import j$.util.stream.A2;
import java.util.Comparator;

abstract class F2<T> extends A2.d<T, T> {
    protected final Comparator b;
    protected boolean c;

    F2(A2 a2, Comparator comparator) {
        super(a2);
        this.b = comparator;
    }

    public final boolean o() {
        this.c = true;
        return false;
    }
}
