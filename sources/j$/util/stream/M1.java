package j$.util.stream;

import j$.util.concurrent.a;
import j$.util.function.BiFunction;
import j$.util.function.Function;
import j$.util.function.b;

public final /* synthetic */ class M1 implements b {
    public static final /* synthetic */ M1 a = new M1();

    private /* synthetic */ M1() {
    }

    public BiFunction andThen(Function function) {
        function.getClass();
        return new a((BiFunction) this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return new S1((A1) obj, (A1) obj2);
    }
}
