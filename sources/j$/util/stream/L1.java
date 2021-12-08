package j$.util.stream;

import j$.util.concurrent.a;
import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEb;
import j$.util.function.Function;

public final /* synthetic */ class L1 implements CLASSNAMEb {
    public static final /* synthetic */ L1 a = new L1();

    private /* synthetic */ L1() {
    }

    public BiFunction andThen(Function function) {
        function.getClass();
        return new a((BiFunction) this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return new R1((CLASSNAMEz1) obj, (CLASSNAMEz1) obj2);
    }
}
