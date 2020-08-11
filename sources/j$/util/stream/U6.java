package j$.util.stream;

import j$.util.Q;
import j$.util.S;
import j$.util.function.A;
import j$.util.function.B;
import j$.util.function.Consumer;

final class U6 extends W6 implements S, B {
    int e;

    public /* synthetic */ boolean a(Consumer consumer) {
        return Q.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Q.a(this, consumer);
    }

    public /* synthetic */ B q(B b) {
        return A.a(this, b);
    }

    public /* bridge */ /* synthetic */ void c(B b) {
        super.forEachRemaining(b);
    }

    public /* bridge */ /* synthetic */ boolean f(B b) {
        return super.tryAdvance(b);
    }

    public /* bridge */ /* synthetic */ S trySplit() {
        return (S) super.trySplit();
    }

    U6(S s, long skip, long limit) {
        super(s, skip, limit);
    }

    U6(S s, U6 parent) {
        super(s, parent);
    }

    public void accept(int value) {
        this.e = value;
    }

    /* access modifiers changed from: protected */
    /* renamed from: y */
    public void w(B action) {
        action.accept(this.e);
    }

    /* access modifiers changed from: protected */
    /* renamed from: z */
    public CLASSNAMEz6 x(int initialCapacity) {
        return new CLASSNAMEz6(initialCapacity);
    }

    /* access modifiers changed from: protected */
    /* renamed from: A */
    public S m(S s) {
        return new U6(s, this);
    }
}
