package j$.util;

import j$.util.function.Consumer;
import j$.util.function.p;

public final /* synthetic */ class q implements j$.util.function.q {
    public final /* synthetic */ Consumer a;

    public /* synthetic */ q(Consumer consumer) {
        this.a = consumer;
    }

    public final void accept(long j) {
        this.a.accept(Long.valueOf(j));
    }

    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new p(this, qVar);
    }
}
