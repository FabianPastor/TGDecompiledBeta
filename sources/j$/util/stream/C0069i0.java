package j$.util.stream;

import j$.util.function.Consumer;

/* renamed from: j$.util.stream.i0  reason: case insensitive filesystem */
abstract class CLASSNAMEi0 implements P4 {
    boolean a;
    Object b;

    CLASSNAMEi0() {
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
        if (!this.a) {
            this.a = true;
            this.b = obj;
        }
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public /* synthetic */ void m() {
    }

    public /* synthetic */ void n(long j) {
    }

    public boolean o() {
        return this.a;
    }
}
