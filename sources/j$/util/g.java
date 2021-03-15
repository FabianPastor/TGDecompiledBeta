package j$.util;

import j$.util.function.C;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;

public final /* synthetic */ class g implements C {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ Consumer var_a;

    public /* synthetic */ g(Consumer consumer) {
        this.var_a = consumer;
    }

    public final void accept(long j) {
        this.var_a.accept(Long.valueOf(j));
    }

    public C g(C c) {
        c.getClass();
        return new CLASSNAMEh(this, c);
    }
}
