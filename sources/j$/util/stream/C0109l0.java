package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEf;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.k;
import j$.util.stream.A2;

/* renamed from: j$.util.stream.l0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEl0 implements A2.e {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ q var_a;

    public /* synthetic */ CLASSNAMEl0(q qVar) {
        this.var_a = qVar;
    }

    public final void accept(double d) {
        this.var_a.accept(d);
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
