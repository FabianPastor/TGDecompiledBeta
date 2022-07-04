package j$.util.stream;

import j$.util.concurrent.a;
import j$.util.function.BiFunction;
import j$.util.function.Function;
import j$.util.function.b;

public final /* synthetic */ class F1 implements b {
    public static final /* synthetic */ F1 a = new F1();

    private /* synthetic */ F1() {
    }

    public BiFunction andThen(Function function) {
        function.getClass();
        return new a((BiFunction) this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return new O1((CLASSNAMEu1) obj, (CLASSNAMEu1) obj2);
    }
}
