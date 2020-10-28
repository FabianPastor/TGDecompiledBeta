package j$.util;

import j$.util.function.Consumer;
import j$.util.function.g;
import j$.util.function.u;

/* renamed from: j$.util.h  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEh implements u {
    public final /* synthetic */ Consumer a;

    public /* synthetic */ CLASSNAMEh(Consumer consumer) {
        this.a = consumer;
    }

    public final void accept(int i) {
        this.a.accept(Integer.valueOf(i));
    }

    public u l(u uVar) {
        uVar.getClass();
        return new g(this, uVar);
    }
}
