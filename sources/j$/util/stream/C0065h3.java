package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.j;
import j$.util.function.k;

/* renamed from: j$.util.stream.h3  reason: case insensitive filesystem */
public abstract class CLASSNAMEh3 implements CLASSNAMEl3 {
    protected final CLASSNAMEn3 a;

    public CLASSNAMEh3(CLASSNAMEn3 n3Var) {
        n3Var.getClass();
        this.a = n3Var;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEp1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEp1.e(this);
        throw null;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEp1.b(this, num);
    }

    public k l(k kVar) {
        kVar.getClass();
        return new j(this, kVar);
    }

    public void m() {
        this.a.m();
    }

    public boolean o() {
        return this.a.o();
    }
}
