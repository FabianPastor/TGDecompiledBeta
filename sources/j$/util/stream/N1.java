package j$.util.stream;

import j$.util.concurrent.a;
import j$.util.function.BiFunction;
import j$.util.function.Function;
import j$.util.function.b;

public final /* synthetic */ class N1 implements b {
    public static final /* synthetic */ N1 a = new N1();

    private /* synthetic */ N1() {
    }

    public BiFunction andThen(Function function) {
        function.getClass();
        return new a((BiFunction) this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return new T1((B1) obj, (B1) obj2);
    }
}
