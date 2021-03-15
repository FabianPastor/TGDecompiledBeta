package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEg;
import j$.util.function.Consumer;
import j$.util.function.w;
import j$.util.k;
import j$.util.stream.A2;

/* renamed from: j$.util.stream.e  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEe implements A2.f {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ w var_a;

    public /* synthetic */ CLASSNAMEe(w wVar) {
        this.var_a = wVar;
    }

    public /* synthetic */ void accept(double d) {
        k.c(this);
        throw null;
    }

    public final void accept(int i) {
        this.var_a.accept(i);
    }

    public /* synthetic */ void accept(long j) {
        k.b(this);
        throw null;
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        Q1.b(this, num);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public w l(w wVar) {
        wVar.getClass();
        return new CLASSNAMEg(this, wVar);
    }

    public void m() {
    }

    public void n(long j) {
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
