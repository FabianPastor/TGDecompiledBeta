package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.CLASSNAMEv;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.h;
import j$.util.function.x;
import j$.util.function.y;

class I4 implements J4, CLASSNAMEs5 {
    private boolean a;
    private long b;
    final /* synthetic */ x c;

    I4(x xVar) {
        this.c = xVar;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEk.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEk.a(this);
        throw null;
    }

    public void accept(long j) {
        if (this.a) {
            this.a = false;
        } else {
            j = this.c.applyAsLong(this.b, j);
        }
        this.b = j;
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

    public Object get() {
        return this.a ? CLASSNAMEv.a() : CLASSNAMEv.d(this.b);
    }

    public void i(J4 j4) {
        I4 i4 = (I4) j4;
        if (!i4.a) {
            accept(i4.b);
        }
    }

    public void m() {
    }

    public void n(long j) {
        this.a = true;
        this.b = 0;
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
