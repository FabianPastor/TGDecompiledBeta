package j$;

import j$.util.function.Consumer;

public final /* synthetic */ class C implements Consumer {
    final /* synthetic */ java.util.function.Consumer a;

    private /* synthetic */ C(java.util.function.Consumer consumer) {
        this.a = consumer;
    }

    public static /* synthetic */ Consumer a(java.util.function.Consumer consumer) {
        if (consumer == null) {
            return null;
        }
        return consumer instanceof D ? ((D) consumer).a : new C(consumer);
    }

    public /* synthetic */ void accept(Object obj) {
        this.a.accept(obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return a(this.a.andThen(D.a(consumer)));
    }
}
