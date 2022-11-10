package j$.util;

import j$.util.function.Consumer;
import java.util.Comparator;
/* loaded from: classes2.dex */
final class F extends H implements v {
    @Override // j$.util.v, j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractCLASSNAMEa.l(this, consumer);
    }

    @Override // j$.util.v
    public void d(j$.util.function.q qVar) {
        qVar.getClass();
    }

    @Override // j$.util.v, j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractCLASSNAMEa.d(this, consumer);
    }

    @Override // j$.util.u
    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    @Override // j$.util.u
    public /* synthetic */ long getExactSizeIfKnown() {
        return AbstractCLASSNAMEa.e(this);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean hasCharacteristics(int i) {
        return AbstractCLASSNAMEa.f(this, i);
    }

    @Override // j$.util.v
    public boolean i(j$.util.function.q qVar) {
        qVar.getClass();
        return false;
    }

    @Override // j$.util.H, j$.util.t, j$.util.w, j$.util.u
    /* renamed from: trySplit */
    public /* bridge */ /* synthetic */ v moNUMtrySplit() {
        return null;
    }

    @Override // j$.util.H, j$.util.t, j$.util.w, j$.util.u
    /* renamed from: trySplit  reason: collision with other method in class */
    public /* bridge */ /* synthetic */ w moNUMtrySplit() {
        return null;
    }
}
