package j$.util.stream;

import j$.util.CLASSNAMEj;
import j$.util.function.CLASSNAMEa;
import j$.util.function.x;
import j$.util.function.y;

public final /* synthetic */ class W implements y {
    public static final /* synthetic */ W a = new W();

    private /* synthetic */ W() {
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
        return ((CLASSNAMEj) obj).c();
    }
}
