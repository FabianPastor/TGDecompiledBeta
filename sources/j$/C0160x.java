package j$;

import java.util.function.Consumer;

/* renamed from: j$.x  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEx implements Consumer {
    final /* synthetic */ j$.util.function.Consumer a;

    private /* synthetic */ CLASSNAMEx(j$.util.function.Consumer consumer) {
        this.a = consumer;
    }

    public static /* synthetic */ Consumer a(j$.util.function.Consumer consumer) {
        if (consumer == null) {
            return null;
        }
        return consumer instanceof CLASSNAMEw ? ((CLASSNAMEw) consumer).a : new CLASSNAMEx(consumer);
    }

    public /* synthetic */ void accept(Object obj) {
        this.a.accept(obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return a(this.a.andThen(CLASSNAMEw.b(consumer)));
    }
}
