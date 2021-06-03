package j$.util.stream;

import j$.time.a;
import j$.util.function.CLASSNAMEf;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.stream.A2;

/* renamed from: j$.util.stream.l0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEl0 implements A2.e {
    public final /* synthetic */ q a;

    public /* synthetic */ CLASSNAMEl0(q qVar) {
        this.a = qVar;
    }

    public final void accept(double d) {
        this.a.accept(d);
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

    /* renamed from: b */
    public /* synthetic */ void accept(Double d) {
        Q1.a(this, d);
    }

    public q j(q qVar) {
        qVar.getClass();
        return new CLASSNAMEf(this, qVar);
    }

    public void l() {
    }

    public void m(long j) {
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
