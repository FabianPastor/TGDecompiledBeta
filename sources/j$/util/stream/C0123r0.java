package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.r0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEr0 implements Consumer {
    public final /* synthetic */ BiConsumer a;
    public final /* synthetic */ Object b;

    public /* synthetic */ CLASSNAMEr0(BiConsumer biConsumer, Object obj) {
        this.a = biConsumer;
        this.b = obj;
    }

    public final void accept(Object obj) {
        this.a.accept(this.b, obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}
