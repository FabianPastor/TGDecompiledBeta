package j$.util.stream;

import j$.util.concurrent.a;
import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEb;
import j$.util.function.Function;

public final /* synthetic */ class J1 implements CLASSNAMEb {
    public static final /* synthetic */ J1 a = new J1();

    private /* synthetic */ J1() {
    }

    public final Object apply(Object obj, Object obj2) {
        return new Q1((CLASSNAMEx1) obj, (CLASSNAMEx1) obj2);
    }

    public BiFunction b(Function function) {
        function.getClass();
        return new a((BiFunction) this, function);
    }
}
