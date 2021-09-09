package j$.util.stream;

import j$.util.CLASSNAMEl;
import j$.util.function.CLASSNAMEa;
import j$.util.function.x;
import j$.util.function.y;

public final /* synthetic */ class Y implements y {
    public static final /* synthetic */ Y a = new Y();

    private /* synthetic */ Y() {
    }

    public y a(y yVar) {
        yVar.getClass();
        return new x(this, yVar, 1);
    }

    public y b(y yVar) {
        yVar.getClass();
        return new x(this, yVar, 0);
    }

    public y negate() {
        return new CLASSNAMEa(this);
    }

    public final boolean test(Object obj) {
        return ((CLASSNAMEl) obj).c();
    }
}
