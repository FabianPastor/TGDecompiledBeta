package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.x0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEx0 implements Consumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ Y2 var_a;
    public final /* synthetic */ Consumer b;

    public /* synthetic */ CLASSNAMEx0(Y2 y2, Consumer consumer) {
        this.var_a = y2;
        this.b = consumer;
    }

    public final void accept(Object obj) {
        this.var_a.g(this.b, obj);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }
}
