package j$.util;

import j$.util.function.Consumer;
import j$.util.function.h;
import j$.util.function.y;

/* renamed from: j$.util.g  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEg implements y {
    public final /* synthetic */ Consumer a;

    public /* synthetic */ CLASSNAMEg(Consumer consumer) {
        this.a = consumer;
    }

    public final void accept(long j) {
        this.a.accept(Long.valueOf(j));
    }

    public y g(y yVar) {
        yVar.getClass();
        return new h(this, yVar);
    }
}
