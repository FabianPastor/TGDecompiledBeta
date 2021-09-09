package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.CLASSNAMEa;
import j$.util.function.x;
import j$.util.function.y;

public final /* synthetic */ class X implements y {
    public static final /* synthetic */ X a = new X();

    private /* synthetic */ X() {
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
        return ((CLASSNAMEk) obj).c();
    }
}
