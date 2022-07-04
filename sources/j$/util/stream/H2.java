package j$.util.stream;

import j$.util.Optional;
import j$.util.function.Consumer;
import j$.util.function.b;

class H2 implements S2 {
    private boolean a;
    private Object b;
    final /* synthetic */ b c;

    H2(b bVar) {
        this.c = bVar;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEo1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEo1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEo1.e(this);
        throw null;
    }

    public void accept(Object obj) {
        if (this.a) {
            this.a = false;
        } else {
            obj = this.c.apply(this.b, obj);
        }
        this.b = obj;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public Object get() {
        return this.a ? Optional.empty() : Optional.of(this.b);
    }

    public void h(S2 s2) {
        H2 h2 = (H2) s2;
        if (!h2.a) {
            accept(h2.b);
        }
    }

    public /* synthetic */ void m() {
    }

    public void n(long j) {
        this.a = true;
        this.b = null;
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
