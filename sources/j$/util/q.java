package j$.util;

import j$.util.function.Consumer;
import j$.util.function.o;
import j$.util.function.p;

public final /* synthetic */ class q implements p {
    public final /* synthetic */ Consumer a;

    public /* synthetic */ q(Consumer consumer) {
        this.a = consumer;
    }

    public final void accept(long j) {
        this.a.accept(Long.valueOf(j));
    }

    public p f(p pVar) {
        pVar.getClass();
        return new o(this, pVar);
    }
}
