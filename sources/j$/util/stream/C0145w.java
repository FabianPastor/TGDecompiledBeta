package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEb;
import j$.util.function.Function;
import j$.util.function.n;

/* renamed from: j$.util.stream.w  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEw implements n {
    public final /* synthetic */ BiConsumer a;

    public /* synthetic */ CLASSNAMEw(BiConsumer biConsumer) {
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
