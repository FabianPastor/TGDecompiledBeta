package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.h;
import j$.util.function.y;

/* renamed from: j$.util.stream.o5  reason: case insensitive filesystem */
public abstract class CLASSNAMEo5 implements CLASSNAMEs5 {
    protected final CLASSNAMEt5 a;

    public CLASSNAMEo5(CLASSNAMEt5 t5Var) {
        t5Var.getClass();
        this.a = t5Var;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEk.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEk.a(this);
        throw null;
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
        this.a.m();
    }

    public void n(long j) {
        this.a.n(j);
    }

    public boolean p() {
        return this.a.p();
    }
}
