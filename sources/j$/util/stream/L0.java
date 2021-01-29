package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEg;
import j$.util.function.Consumer;
import j$.util.function.w;
import j$.util.k;
import j$.util.stream.A2;
import j$.util.stream.S2;

public final /* synthetic */ class L0 implements A2.f {
    public final /* synthetic */ S2.c a;

    public /* synthetic */ L0(S2.c cVar) {
        this.a = cVar;
    }

    public /* synthetic */ void accept(double d) {
        k.c(this);
        throw null;
    }

    public final void accept(int i) {
        this.a.accept(i);
    }

    public /* synthetic */ void accept(long j) {
        k.b(this);
        throw null;
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        Q1.b(this, num);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public w l(w wVar) {
        wVar.getClass();
        return new CLASSNAMEg(this, wVar);
    }

    public void m() {
    }

    public void n(long j) {
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
