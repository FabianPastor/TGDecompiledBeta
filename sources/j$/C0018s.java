package j$;

import java.util.function.Consumer;

/* renamed from: j$.s  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEs implements Consumer {
    final /* synthetic */ j$.util.function.Consumer a;

    private /* synthetic */ CLASSNAMEs(j$.util.function.Consumer consumer) {
        this.a = consumer;
    }

    public static /* synthetic */ Consumer a(j$.util.function.Consumer consumer) {
        if (consumer == null) {
            return null;
        }
        return consumer instanceof r ? ((r) consumer).a : new CLASSNAMEs(consumer);
    }

    public /* synthetic */ void accept(Object obj) {
        this.a.accept(obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return a(this.a.g(r.a(consumer)));
    }
}
