package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.b  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEb implements Consumer {
    public final /* synthetic */ A2 a;

    public /* synthetic */ CLASSNAMEb(A2 a2) {
        this.a = a2;
    }

    public final void accept(Object obj) {
        this.a.accept(obj);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }
}
