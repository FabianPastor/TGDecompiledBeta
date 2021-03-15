package a;

import j$.util.function.Consumer;

/* renamed from: a.z  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEz implements Consumer {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ java.util.function.Consumer var_a;

    private /* synthetic */ CLASSNAMEz(java.util.function.Consumer consumer) {
        this.var_a = consumer;
    }

    public static /* synthetic */ Consumer b(java.util.function.Consumer consumer) {
        if (consumer == null) {
            return null;
        }
        return consumer instanceof A ? ((A) consumer).f5a : new CLASSNAMEz(consumer);
    }

    public /* synthetic */ void accept(Object obj) {
        this.var_a.accept(obj);
    }

    public /* synthetic */ Consumer f(Consumer consumer) {
        return b(this.var_a.andThen(A.a(consumer)));
    }
}
