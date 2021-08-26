package j$.util;

import j$.util.function.Consumer;
import j$.util.function.k;
import java.util.Comparator;

final class K implements v {
    private final int[] a;
    private int b;
    private final int c;
    private final int d;

    public K(int[] iArr, int i, int i2, int i3) {
        this.a = iArr;
        this.b = i;
        this.c = i2;
        this.d = i3 | 64 | 16384;
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.k(this, consumer);
    }

    /* renamed from: c */
    public void forEachRemaining(k kVar) {
        int i;
        kVar.getClass();
        int[] iArr = this.a;
        int length = iArr.length;
        int i2 = this.c;
        if (length >= i2 && (i = this.b) >= 0) {
            this.b = i2;
            if (i < i2) {
                do {
                    kVar.accept(iArr[i]);
                    i++;
                } while (i < i2);
            }
        }
    }

    public int characteristics() {
        return this.d;
    }

    public long estimateSize() {
        return (long) (this.c - this.b);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.c(this, consumer);
    }

    /* renamed from: g */
    public boolean tryAdvance(k kVar) {
        kVar.getClass();
        int i = this.b;
        if (i < 0 || i >= this.c) {
            return false;
        }
        int[] iArr = this.a;
        this.b = i + 1;
        kVar.accept(iArr[i]);
        return true;
    }

    public Comparator getComparator() {
        if (CLASSNAMEa.f(this, 4)) {
            return null;
        }
        throw new IllegalStateException();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return CLASSNAMEa.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEa.f(this, i);
    }

    public v trySplit() {
        int i = this.b;
        int i2 = (this.c + i) >>> 1;
        if (i >= i2) {
            return null;
        }
        int[] iArr = this.a;
        this.b = i2;
        return new K(iArr, i, i2, this.d);
    }
}
