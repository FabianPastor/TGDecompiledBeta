package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.b  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEb implements Consumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ A2 var_a;

    public /* synthetic */ CLASSNAMEb(A2 a2) {
        this.var_a = a2;
    }

    public final void accept(Object obj) {
        this.var_a.accept(obj);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }
}
