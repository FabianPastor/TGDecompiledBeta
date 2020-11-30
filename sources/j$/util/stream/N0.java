package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.k;

public final /* synthetic */ class N0 implements A2 {
    public final /* synthetic */ Consumer a;

    public /* synthetic */ N0(Consumer consumer) {
        this.a = consumer;
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

    public final void accept(Object obj) {
        this.a.accept(obj);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void m() {
    }

    public void n(long j) {
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
