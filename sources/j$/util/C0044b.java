package j$.util;

import j$.util.function.Consumer;
import j$.util.function.I;
import j$.util.function.J;

/* renamed from: j$.util.b  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEb implements J {
    public final /* synthetic */ Consumer a;

    public /* synthetic */ CLASSNAMEb(Consumer consumer) {
        this.a = consumer;
    }

    public final void accept(long j) {
        this.a.accept(Long.valueOf(j));
    }

    public /* synthetic */ J h(J j) {
        return I.a(this, j);
    }
}
