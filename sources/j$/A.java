package j$;

import java.util.function.Consumer;

public final /* synthetic */ class A implements Consumer {
    final /* synthetic */ j$.util.function.Consumer a;

    private /* synthetic */ A(j$.util.function.Consumer consumer) {
        this.a = consumer;
    }

    public static /* synthetic */ Consumer a(j$.util.function.Consumer consumer) {
        if (consumer == null) {
            return null;
        }
        return consumer instanceof CLASSNAMEz ? ((CLASSNAMEz) consumer).a : new A(consumer);
    }

    public /* synthetic */ void accept(Object obj) {
        this.a.accept(obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return a(this.a.f(CLASSNAMEz.b(consumer)));
    }
}
