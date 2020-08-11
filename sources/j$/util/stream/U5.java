package j$.util.stream;

import java.util.Comparator;

abstract class U5 extends CLASSNAMEz5 {
    protected final Comparator b;
    protected boolean c;

    U5(G5 downstream, Comparator comparator) {
        super(downstream);
        this.b = comparator;
    }

    public final boolean u() {
        this.c = true;
        return false;
    }
}
