package j$.util.stream;

import j$.util.CLASSNAMEn;
import j$.util.Q;
import j$.util.S;
import j$.util.function.B;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.l6  reason: case insensitive filesystem */
class CLASSNAMEl6 extends CLASSNAMEp6 implements S {
    final /* synthetic */ CLASSNAMEm6 g;

    public /* synthetic */ boolean a(Consumer consumer) {
        return Q.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Q.a(this, consumer);
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

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEl6(CLASSNAMEm6 this$0, int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
        super(this$0, firstSpineIndex, lastSpineIndex, firstSpineElementIndex, lastSpineElementFence);
        this.g = this$0;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: m */
    public CLASSNAMEl6 h(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
        return new CLASSNAMEl6(this.g, firstSpineIndex, lastSpineIndex, firstSpineElementIndex, lastSpineElementFence);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: k */
    public void b(int[] array, int index, B consumer) {
        consumer.accept(array[index]);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: l */
    public S g(int[] array, int offset, int len) {
        return CLASSNAMEn.b(array, offset, offset + len);
    }
}
