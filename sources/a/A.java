package a;

import java.util.function.Consumer;

public final /* synthetic */ class A implements Consumer {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j$.util.function.Consumer f5a;

    private /* synthetic */ A(j$.util.function.Consumer consumer) {
        this.f5a = consumer;
    }

    public static /* synthetic */ Consumer a(j$.util.function.Consumer consumer) {
        if (consumer == null) {
            return null;
        }
        return consumer instanceof CLASSNAMEz ? ((CLASSNAMEz) consumer).var_a : new A(consumer);
    }

    public /* synthetic */ void accept(Object obj) {
        this.f5a.accept(obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return a(this.f5a.f(CLASSNAMEz.b(consumer)));
    }
}
