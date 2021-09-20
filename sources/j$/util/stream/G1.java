package j$.util.stream;

import j$.util.concurrent.a;
import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEb;
import j$.util.function.Function;

public final /* synthetic */ class G1 implements CLASSNAMEb {
    public static final /* synthetic */ G1 a = new G1();

    private /* synthetic */ G1() {
    }

    public BiFunction andThen(Function function) {
        function.getClass();
        return new a((BiFunction) this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return new P1((CLASSNAMEv1) obj, (CLASSNAMEv1) obj2);
    }
}
