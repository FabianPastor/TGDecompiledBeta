package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.p5  reason: case insensitive filesystem */
public abstract class CLASSNAMEp5 implements CLASSNAMEt5 {
    protected final CLASSNAMEt5 a;

    public CLASSNAMEp5(CLASSNAMEt5 t5Var) {
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

    public /* synthetic */ void accept(long j) {
        CLASSNAMEk.b(this);
        throw null;
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
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
