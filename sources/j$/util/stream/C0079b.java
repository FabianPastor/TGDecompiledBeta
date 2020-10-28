package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.b  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEb implements Consumer {
    public final /* synthetic */ CLASSNAMEt5 a;

    public /* synthetic */ CLASSNAMEb(CLASSNAMEt5 t5Var) {
        this.a = t5Var;
    }

    public final void accept(Object obj) {
        this.a.accept(obj);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }
}
