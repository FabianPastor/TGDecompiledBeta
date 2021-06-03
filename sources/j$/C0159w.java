package j$;

import j$.util.function.Consumer;

/* renamed from: j$.w  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEw implements Consumer {
    final /* synthetic */ java.util.function.Consumer a;

    private /* synthetic */ CLASSNAMEw(java.util.function.Consumer consumer) {
        this.a = consumer;
    }

    public static /* synthetic */ Consumer b(java.util.function.Consumer consumer) {
        if (consumer == null) {
            return null;
        }
        return consumer instanceof CLASSNAMEx ? ((CLASSNAMEx) consumer).a : new CLASSNAMEw(consumer);
    }

    public /* synthetic */ void accept(Object obj) {
        this.a.accept(obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return b(this.a.andThen(CLASSNAMEx.a(consumer)));
    }
}
