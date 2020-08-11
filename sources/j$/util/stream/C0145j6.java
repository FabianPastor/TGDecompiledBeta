package j$.util.stream;

import j$.util.CLASSNAMEn;
import j$.util.O;
import j$.util.P;
import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.j6  reason: case insensitive filesystem */
class CLASSNAMEj6 extends CLASSNAMEp6 implements P {
    final /* synthetic */ CLASSNAMEk6 g;

    public /* synthetic */ boolean a(Consumer consumer) {
        return O.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        O.a(this, consumer);
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

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEj6(CLASSNAMEk6 this$0, int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
        super(this$0, firstSpineIndex, lastSpineIndex, firstSpineElementIndex, lastSpineElementFence);
        this.g = this$0;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: m */
    public CLASSNAMEj6 h(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
        return new CLASSNAMEj6(this.g, firstSpineIndex, lastSpineIndex, firstSpineElementIndex, lastSpineElementFence);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: k */
    public void b(double[] array, int index, CLASSNAMEt consumer) {
        consumer.accept(array[index]);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: l */
    public P g(double[] array, int offset, int len) {
        return CLASSNAMEn.a(array, offset, offset + len);
    }
}
