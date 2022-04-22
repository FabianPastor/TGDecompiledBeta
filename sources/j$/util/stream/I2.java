package j$.util.stream;

import j$.util.Optional;
import j$.util.function.Consumer;
import j$.util.function.b;

class I2 implements T2 {
    private boolean a;
    private Object b;
    final /* synthetic */ b c;

    I2(b bVar) {
        this.c = bVar;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEp1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEp1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEp1.e(this);
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

    public void h(T2 t2) {
        I2 i2 = (I2) t2;
        if (!i2.a) {
            accept(i2.b);
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
