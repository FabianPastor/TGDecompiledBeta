package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.k;

abstract class M1<T> implements A2<T> {

    /* renamed from: a  reason: collision with root package name */
    boolean var_a;
    boolean b;

    M1(N1 n1) {
        this.b = !n1.b;
    }

    public /* synthetic */ void accept(double d) {
        k.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        k.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        k.b(this);
        throw null;
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void m() {
    }

    public void n(long j) {
    }

    public boolean p() {
        return this.var_a;
    }
}
