package j$;

import j$.util.function.Consumer;

/* renamed from: j$.z  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEz implements Consumer {
    final /* synthetic */ java.util.function.Consumer a;

    private /* synthetic */ CLASSNAMEz(java.util.function.Consumer consumer) {
        this.a = consumer;
    }

    public static /* synthetic */ Consumer b(java.util.function.Consumer consumer) {
        if (consumer == null) {
            return null;
        }
        return consumer instanceof A ? ((A) consumer).a : new CLASSNAMEz(consumer);
    }

    public /* synthetic */ void accept(Object obj) {
        this.a.accept(obj);
    }

    public /* synthetic */ Consumer f(Consumer consumer) {
        return b(this.a.andThen(A.a(consumer)));
    }
}
