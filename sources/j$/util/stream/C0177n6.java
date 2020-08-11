package j$.util.stream;

import j$.util.CLASSNAMEn;
import j$.util.T;
import j$.util.U;
import j$.util.function.Consumer;
import j$.util.function.J;

/* renamed from: j$.util.stream.n6  reason: case insensitive filesystem */
class CLASSNAMEn6 extends CLASSNAMEp6 implements U {
    final /* synthetic */ CLASSNAMEo6 g;

    public /* synthetic */ boolean a(Consumer consumer) {
        return T.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        T.a(this, consumer);
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

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEn6(CLASSNAMEo6 this$0, int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
        super(this$0, firstSpineIndex, lastSpineIndex, firstSpineElementIndex, lastSpineElementFence);
        this.g = this$0;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: m */
    public CLASSNAMEn6 h(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
        return new CLASSNAMEn6(this.g, firstSpineIndex, lastSpineIndex, firstSpineElementIndex, lastSpineElementFence);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: k */
    public void b(long[] array, int index, J consumer) {
        consumer.accept(array[index]);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: l */
    public U g(long[] array, int offset, int len) {
        return CLASSNAMEn.c(array, offset, offset + len);
    }
}
