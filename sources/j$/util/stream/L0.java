package j$.util.stream;

import j$.time.a;
import j$.util.function.CLASSNAMEg;
import j$.util.function.Consumer;
import j$.util.function.w;
import j$.util.stream.A2;
import j$.util.stream.S2;

public final /* synthetic */ class L0 implements A2.f {
    public final /* synthetic */ S2.c a;

    public /* synthetic */ L0(S2.c cVar) {
        this.a = cVar;
    }

    public /* synthetic */ void accept(double d) {
        a.c(this);
        throw null;
    }

    public final void accept(int i) {
        this.a.accept(i);
    }

    public /* synthetic */ void accept(long j) {
        a.b(this);
        throw null;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        Q1.b(this, num);
    }

    public w k(w wVar) {
        wVar.getClass();
        return new CLASSNAMEg(this, wVar);
    }

    public void l() {
    }

    public void m(long j) {
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
