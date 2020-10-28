package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.n;

/* renamed from: j$.util.stream.s4  reason: case insensitive filesystem */
class CLASSNAMEs4 extends K4 implements J4 {
    final /* synthetic */ Object b;
    final /* synthetic */ BiFunction c;
    final /* synthetic */ n d;

    CLASSNAMEs4(Object obj, BiFunction biFunction, n nVar) {
        this.b = obj;
        this.c = biFunction;
        this.d = nVar;
    }

    public /* synthetic */ void accept(double d2) {
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

    public void accept(Object obj) {
        this.a = this.c.apply(this.a, obj);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void i(J4 j4) {
        this.a = this.d.apply(this.a, ((CLASSNAMEs4) j4).a);
    }

    public void m() {
    }

    public void n(long j) {
        this.a = this.b;
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
