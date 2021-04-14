package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.r0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEr0 implements Consumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ BiConsumer var_a;
    public final /* synthetic */ Object b;

    public /* synthetic */ CLASSNAMEr0(BiConsumer biConsumer, Object obj) {
        this.var_a = biConsumer;
        this.b = obj;
    }

    public final void accept(Object obj) {
        this.var_a.accept(this.b, obj);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }
}
