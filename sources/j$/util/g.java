package j$.util;

import j$.util.function.C;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;

public final /* synthetic */ class g implements C {
    public final /* synthetic */ Consumer a;

    public /* synthetic */ g(Consumer consumer) {
        this.a = consumer;
    }

    public final void accept(long j) {
        this.a.accept(Long.valueOf(j));
    }

    public C f(C c) {
        c.getClass();
        return new CLASSNAMEh(this, c);
    }
}
