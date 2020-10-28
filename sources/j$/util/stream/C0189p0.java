package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.h;
import j$.util.function.y;

/* renamed from: j$.util.stream.p0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEp0 implements CLASSNAMEs5 {
    public final /* synthetic */ CLASSNAMEa6 a;

    public /* synthetic */ CLASSNAMEp0(CLASSNAMEa6 a6Var) {
        this.a = a6Var;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEk.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEk.a(this);
        throw null;
    }

    public final void accept(long j) {
        this.a.accept(j);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        CLASSNAMEc3.c(this, l);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public y g(y yVar) {
        yVar.getClass();
        return new h(this, yVar);
    }

    public void m() {
    }

    public void n(long j) {
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
