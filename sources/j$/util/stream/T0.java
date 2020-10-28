package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.f;
import j$.util.function.q;

public final /* synthetic */ class T0 implements CLASSNAMEq5 {
    public final /* synthetic */ W5 a;

    public /* synthetic */ T0(W5 w5) {
        this.a = w5;
    }

    public final void accept(double d) {
        this.a.accept(d);
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEk.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEk.b(this);
        throw null;
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Double d) {
        CLASSNAMEc3.a(this, d);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public q k(q qVar) {
        qVar.getClass();
        return new f(this, qVar);
    }

    public void m() {
    }

    public void n(long j) {
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
