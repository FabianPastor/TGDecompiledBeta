package j$.util;

import j$.util.function.Consumer;
import j$.util.function.e;
import j$.util.function.f;

/* renamed from: j$.util.m  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEm implements f {
    public final /* synthetic */ Consumer a;

    public /* synthetic */ CLASSNAMEm(Consumer consumer) {
        this.a = consumer;
    }

    public final void accept(double d) {
        this.a.accept(Double.valueOf(d));
    }

    public f j(f fVar) {
        fVar.getClass();
        return new e(this, fVar);
    }
}
