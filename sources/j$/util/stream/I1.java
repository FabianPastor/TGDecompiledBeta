package j$.util.stream;

import j$.util.concurrent.a;
import j$.util.function.BiFunction;
import j$.util.function.Function;
import j$.util.function.b;

public final /* synthetic */ class I1 implements b {
    public static final /* synthetic */ I1 a = new I1();

    private /* synthetic */ I1() {
    }

    public BiFunction andThen(Function function) {
        function.getClass();
        return new a((BiFunction) this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return new P1((CLASSNAMEw1) obj, (CLASSNAMEw1) obj2);
    }
}
