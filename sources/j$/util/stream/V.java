package j$.util.stream;

import j$.util.Optional;
import j$.util.function.CLASSNAMEa;
import j$.util.function.w;
import j$.util.function.x;

public final /* synthetic */ class V implements x {
    public static final /* synthetic */ V a = new V();

    private /* synthetic */ V() {
    }

    public x a(x xVar) {
        xVar.getClass();
        return new w(this, xVar, 1);
    }

    public x c(x xVar) {
        xVar.getClass();
        return new w(this, xVar, 0);
    }

    public x negate() {
        return new CLASSNAMEa(this);
    }

    public final boolean test(Object obj) {
        return ((Optional) obj).isPresent();
    }
}
