package j$.util.stream;

import j$.time.a;
import j$.util.function.Consumer;

abstract class M1<T> implements A2<T> {
    boolean a;
    boolean b;

    M1(N1 n1) {
        this.b = !n1.c;
    }

    public /* synthetic */ void accept(double d) {
        a.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        a.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        a.b(this);
        throw null;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public void l() {
    }

    public void m(long j) {
    }

    public boolean o() {
        return this.a;
    }
}
