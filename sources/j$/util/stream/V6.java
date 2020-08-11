package j$.util.stream;

import j$.util.T;
import j$.util.U;
import j$.util.function.Consumer;
import j$.util.function.I;
import j$.util.function.J;

final class V6 extends W6 implements U, J {
    long e;

    public /* synthetic */ boolean a(Consumer consumer) {
        return T.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        T.a(this, consumer);
    }

    public /* synthetic */ J h(J j) {
        return I.a(this, j);
    }

    public /* bridge */ /* synthetic */ void d(J j) {
        super.forEachRemaining(j);
    }

    public /* bridge */ /* synthetic */ boolean i(J j) {
        return super.tryAdvance(j);
    }

    public /* bridge */ /* synthetic */ U trySplit() {
        return (U) super.trySplit();
    }

    V6(U s, long skip, long limit) {
        super(s, skip, limit);
    }

    V6(U s, V6 parent) {
        super(s, parent);
    }

    public void accept(long value) {
        this.e = value;
    }

    /* access modifiers changed from: protected */
    /* renamed from: y */
    public void w(J action) {
        action.accept(this.e);
    }

    /* access modifiers changed from: protected */
    /* renamed from: z */
    public A6 x(int initialCapacity) {
        return new A6(initialCapacity);
    }

    /* access modifiers changed from: protected */
    /* renamed from: A */
    public U m(U s) {
        return new V6(s, this);
    }
}
