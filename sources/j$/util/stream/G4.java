package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.h;
import j$.util.function.x;
import j$.util.function.y;

class G4 implements J4, CLASSNAMEs5 {
    private long a;
    final /* synthetic */ long b;
    final /* synthetic */ x c;

    G4(long j, x xVar) {
        this.b = j;
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
        this.a = this.c.applyAsLong(this.a, j);
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
        return Long.valueOf(this.a);
    }

    public void i(J4 j4) {
        accept(((G4) j4).a);
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
