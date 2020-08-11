package j$;

import j$.util.function.Consumer;

public final /* synthetic */ class r implements Consumer {
    final /* synthetic */ java.util.function.Consumer a;

    private /* synthetic */ r(java.util.function.Consumer consumer) {
        this.a = consumer;
    }

    public static /* synthetic */ Consumer a(java.util.function.Consumer consumer) {
        if (consumer == null) {
            return null;
        }
        return consumer instanceof CLASSNAMEs ? ((CLASSNAMEs) consumer).a : new r(consumer);
    }

    public /* synthetic */ void accept(Object obj) {
        this.a.accept(obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return a(this.a.andThen(CLASSNAMEs.a(consumer)));
    }
}
