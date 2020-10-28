package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.g;
import j$.util.function.u;

/* renamed from: j$.util.stream.e  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEe implements CLASSNAMEr5 {
    public final /* synthetic */ u a;

    public /* synthetic */ CLASSNAMEe(u uVar) {
        this.a = uVar;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEk.c(this);
        throw null;
    }

    public final void accept(int i) {
        this.a.accept(i);
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEk.b(this);
        throw null;
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEc3.b(this, num);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public u l(u uVar) {
        uVar.getClass();
        return new g(this, uVar);
    }

    public void m() {
    }

    public void n(long j) {
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
