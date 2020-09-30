package j$.util;

import j$.util.function.A;
import j$.util.function.B;
import j$.util.function.Consumer;

/* renamed from: j$.util.i  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEi implements B {
    public final /* synthetic */ Consumer a;

    public /* synthetic */ CLASSNAMEi(Consumer consumer) {
        this.a = consumer;
    }

    public final void accept(int i) {
        this.a.accept(Integer.valueOf(i));
    }

    public /* synthetic */ B q(B b) {
        return A.a(this, b);
    }
}
