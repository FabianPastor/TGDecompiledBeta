package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.o0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEo0 implements Consumer {
    public final /* synthetic */ BiConsumer a;
    public final /* synthetic */ Object b;

    public /* synthetic */ CLASSNAMEo0(BiConsumer biConsumer, Object obj) {
        this.a = biConsumer;
        this.b = obj;
    }

    public final void accept(Object obj) {
        this.a.accept(this.b, obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }
}
