package j$.util.stream;

import j$.util.concurrent.a;
import j$.util.function.BiFunction;
import j$.util.function.Function;
import j$.util.function.b;

public final /* synthetic */ class K1 implements b {
    public static final /* synthetic */ K1 a = new K1();

    private /* synthetic */ K1() {
    }

    public BiFunction andThen(Function function) {
        function.getClass();
        return new a((BiFunction) this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return new Q1((CLASSNAMEy1) obj, (CLASSNAMEy1) obj2);
    }
}
