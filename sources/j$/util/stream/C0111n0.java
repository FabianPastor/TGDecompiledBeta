package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEf;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.k;
import j$.util.stream.A2;

/* renamed from: j$.util.stream.n0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEn0 implements A2.e {
    public final /* synthetic */ q a;

    public /* synthetic */ CLASSNAMEn0(q qVar) {
        this.a = qVar;
    }

    public final void accept(double d) {
        this.a.accept(d);
    }

    public /* synthetic */ void accept(int i) {
        k.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        k.b(this);
        throw null;
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Double d) {
        Q1.a(this, d);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public q k(q qVar) {
        qVar.getClass();
        return new CLASSNAMEf(this, qVar);
    }

    public void m() {
    }

    public void n(long j) {
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
