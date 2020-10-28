package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.g;
import j$.util.function.u;

/* renamed from: j$.util.stream.n5  reason: case insensitive filesystem */
public abstract class CLASSNAMEn5 implements CLASSNAMEr5 {
    protected final CLASSNAMEt5 a;

    public CLASSNAMEn5(CLASSNAMEt5 t5Var) {
        t5Var.getClass();
        this.a = t5Var;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEk.c(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEk.b(this);
        throw null;
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEc3.b(this, num);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public u l(u uVar) {
        uVar.getClass();
        return new g(this, uVar);
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
