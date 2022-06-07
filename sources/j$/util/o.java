package j$.util;

import j$.util.function.Consumer;
import j$.util.function.k;
import j$.util.function.l;

public final /* synthetic */ class o implements l {
    public final /* synthetic */ Consumer a;

    public /* synthetic */ o(Consumer consumer) {
        this.a = consumer;
    }

    public final void accept(int i) {
        this.a.accept(Integer.valueOf(i));
    }

    public l l(l lVar) {
        lVar.getClass();
        return new k(this, lVar);
    }
}
