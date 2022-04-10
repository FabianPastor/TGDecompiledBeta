package j$.util;

import j$.util.function.Consumer;
import j$.util.function.k;
import j$.util.function.l;

/* renamed from: j$.util.o  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEo implements l {
    public final /* synthetic */ Consumer a;

    public /* synthetic */ CLASSNAMEo(Consumer consumer) {
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
