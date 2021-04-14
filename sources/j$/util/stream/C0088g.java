package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.k;

/* renamed from: j$.util.stream.g  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEg implements A2 {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ S2 var_a;

    public /* synthetic */ CLASSNAMEg(S2 s2) {
        this.var_a = s2;
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
        this.var_a.accept(obj);
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
