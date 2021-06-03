package j$.util.function;

import j$.util.function.Consumer;

/* renamed from: j$.util.function.e  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEe implements Consumer {
    public final /* synthetic */ Consumer a;
    public final /* synthetic */ Consumer b;

    public /* synthetic */ CLASSNAMEe(Consumer consumer, Consumer consumer2) {
        this.a = consumer;
        this.b = consumer2;
    }

    public final void accept(Object obj) {
        Consumer consumer = this.a;
        Consumer consumer2 = this.b;
        consumer.accept(obj);
        consumer2.accept(obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}
