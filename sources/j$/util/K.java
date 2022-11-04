package j$.util;

import j$.util.function.Consumer;
import java.util.Comparator;
/* loaded from: classes2.dex */
final class K implements v {
    private final long[] a;
    private int b;
    private final int c;
    private final int d;

    public K(long[] jArr, int i, int i2, int i3) {
        this.a = jArr;
        this.b = i;
        this.c = i2;
        this.d = i3 | 64 | 16384;
    }

    @Override // j$.util.v, j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractCLASSNAMEa.l(this, consumer);
    }

    @Override // j$.util.u
    public int characteristics() {
        return this.d;
    }

    @Override // j$.util.w
    /* renamed from: d */
    public void forEachRemaining(j$.util.function.q qVar) {
        int i;
        qVar.getClass();
        long[] jArr = this.a;
        int length = jArr.length;
        int i2 = this.c;
        if (length < i2 || (i = this.b) < 0) {
            return;
        }
        this.b = i2;
        if (i >= i2) {
            return;
        }
        do {
            qVar.accept(jArr[i]);
            i++;
        } while (i < i2);
    }

    @Override // j$.util.u
    public long estimateSize() {
        return this.c - this.b;
    }

    @Override // j$.util.v, j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractCLASSNAMEa.d(this, consumer);
    }

    @Override // j$.util.u
    public Comparator getComparator() {
        if (AbstractCLASSNAMEa.f(this, 4)) {
            return null;
        }
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

    @Override // j$.util.w
    /* renamed from: i */
    public boolean tryAdvance(j$.util.function.q qVar) {
        qVar.getClass();
        int i = this.b;
        if (i < 0 || i >= this.c) {
            return false;
        }
        long[] jArr = this.a;
        this.b = i + 1;
        qVar.accept(jArr[i]);
        return true;
    }

    @Override // j$.util.v, j$.util.w, j$.util.u
    /* renamed from: trySplit  reason: collision with other method in class */
    public v mo326trySplit() {
        int i = this.b;
        int i2 = (this.c + i) >>> 1;
        if (i >= i2) {
            return null;
        }
        long[] jArr = this.a;
        this.b = i2;
        return new K(jArr, i, i2, this.d);
    }
}
