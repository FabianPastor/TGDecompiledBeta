package j$.util;

import j$.util.function.Consumer;
import j$.util.function.j;
import j$.util.function.k;

/* renamed from: j$.util.o  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEo implements k {
    public final /* synthetic */ Consumer a;

    public /* synthetic */ CLASSNAMEo(Consumer consumer) {
        this.a = consumer;
    }

    public final void accept(int i) {
        this.a.accept(Integer.valueOf(i));
    }

    public k l(k kVar) {
        kVar.getClass();
        return new j(this, kVar);
    }
}
