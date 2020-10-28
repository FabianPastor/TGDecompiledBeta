package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.y0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEy0 implements Consumer {
    public final /* synthetic */ q6 a;
    public final /* synthetic */ Consumer b;

    public /* synthetic */ CLASSNAMEy0(q6 q6Var, Consumer consumer) {
        this.a = q6Var;
        this.b = consumer;
    }

    public final void accept(Object obj) {
        this.a.g(this.b, obj);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }
}
