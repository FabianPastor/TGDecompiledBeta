package j$.util.stream;

import j$.time.a;
import j$.util.function.C;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.stream.A2;

public final /* synthetic */ class V0 implements A2.g {
    public final /* synthetic */ C a;

    public /* synthetic */ V0(C c) {
        this.a = c;
    }

    public /* synthetic */ void accept(double d) {
        a.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        a.a(this);
        throw null;
    }

    public final void accept(long j) {
        this.a.accept(j);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        Q1.c(this, l);
    }

    public C f(C c) {
        c.getClass();
        return new CLASSNAMEh(this, c);
    }

    public void l() {
    }

    public void m(long j) {
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
