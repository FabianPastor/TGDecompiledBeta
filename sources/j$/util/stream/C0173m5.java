package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.f;
import j$.util.function.q;

/* renamed from: j$.util.stream.m5  reason: case insensitive filesystem */
public abstract class CLASSNAMEm5 implements CLASSNAMEq5 {
    protected final CLASSNAMEt5 a;

    public CLASSNAMEm5(CLASSNAMEt5 t5Var) {
        t5Var.getClass();
        this.a = t5Var;
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
        this.a.m();
    }

    public void n(long j) {
        this.a.n(j);
    }

    public boolean p() {
        return this.a.p();
    }
}
