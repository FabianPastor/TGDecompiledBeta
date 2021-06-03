package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEb;
import j$.util.function.Function;
import j$.util.function.n;

public final /* synthetic */ class G implements n {
    public final /* synthetic */ BiConsumer a;

    public /* synthetic */ G(BiConsumer biConsumer) {
        this.a = biConsumer;
    }

    public BiFunction a(Function function) {
        function.getClass();
        return new CLASSNAMEb(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        this.a.accept(obj, obj2);
        return obj;
    }
}
