package j$.util.stream;

import j$.util.O;
import j$.util.P;
import j$.util.function.CLASSNAMEs;
import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;

final class T6 extends W6 implements P, CLASSNAMEt {
    double e;

    public /* synthetic */ boolean a(Consumer consumer) {
        return O.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        O.a(this, consumer);
    }

    public /* synthetic */ CLASSNAMEt p(CLASSNAMEt tVar) {
        return CLASSNAMEs.a(this, tVar);
    }

    public /* bridge */ /* synthetic */ void e(CLASSNAMEt tVar) {
        super.forEachRemaining(tVar);
    }

    public /* bridge */ /* synthetic */ boolean j(CLASSNAMEt tVar) {
        return super.tryAdvance(tVar);
    }

    public /* bridge */ /* synthetic */ P trySplit() {
        return (P) super.trySplit();
    }

    T6(P s, long skip, long limit) {
        super(s, skip, limit);
    }

    T6(P s, T6 parent) {
        super(s, parent);
    }

    public void accept(double value) {
        this.e = value;
    }

    /* access modifiers changed from: protected */
    /* renamed from: y */
    public void w(CLASSNAMEt action) {
        action.accept(this.e);
    }

    /* access modifiers changed from: protected */
    /* renamed from: z */
    public CLASSNAMEy6 x(int initialCapacity) {
        return new CLASSNAMEy6(initialCapacity);
    }

    /* access modifiers changed from: protected */
    /* renamed from: A */
    public P m(P s) {
        return new T6(s, this);
    }
}
