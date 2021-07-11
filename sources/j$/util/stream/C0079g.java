package j$.util.stream;

import j$.time.a;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.g  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEg implements A2 {
    public final /* synthetic */ S2 a;

    public /* synthetic */ CLASSNAMEg(S2 s2) {
        this.a = s2;
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

    public final void accept(Object obj) {
        this.a.accept(obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public void l() {
    }

    public void m(long j) {
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
