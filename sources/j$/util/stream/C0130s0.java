package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.s0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEs0 implements Consumer {
    public final /* synthetic */ BiConsumer a;
    public final /* synthetic */ Object b;

    public /* synthetic */ CLASSNAMEs0(BiConsumer biConsumer, Object obj) {
        this.a = biConsumer;
        this.b = obj;
    }

    public final void accept(Object obj) {
        this.a.accept(this.b, obj);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }
}
