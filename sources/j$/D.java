package j$;

import java.util.function.Consumer;

public final /* synthetic */ class D implements Consumer {
    final /* synthetic */ j$.util.function.Consumer a;

    private /* synthetic */ D(j$.util.function.Consumer consumer) {
        this.a = consumer;
    }

    public static /* synthetic */ Consumer a(j$.util.function.Consumer consumer) {
        if (consumer == null) {
            return null;
        }
        return consumer instanceof C ? ((C) consumer).a : new D(consumer);
    }

    public /* synthetic */ void accept(Object obj) {
        this.a.accept(obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return a(this.a.g(C.a(consumer)));
    }
}
