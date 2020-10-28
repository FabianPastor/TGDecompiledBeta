package j$.util;

import j$.util.function.Consumer;
import j$.util.function.u;
import java.util.Comparator;

final class S implements D {
    private final int[] a;
    private int b;
    private final int c;
    private final int d;

    public S(int[] iArr, int i, int i2, int i3) {
        this.a = iArr;
        this.b = i;
        this.c = i2;
        this.d = i3 | 64 | 16384;
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.e(this, consumer);
    }

    /* renamed from: c */
    public void forEachRemaining(u uVar) {
        int i;
        uVar.getClass();
        int[] iArr = this.a;
        int length = iArr.length;
        int i2 = this.c;
        if (length >= i2 && (i = this.b) >= 0) {
            this.b = i2;
            if (i < i2) {
                do {
                    uVar.accept(iArr[i]);
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
        CLASSNAMEw.b(this, consumer);
    }

    public Comparator getComparator() {
        if (CLASSNAMEk.f(this, 4)) {
            return null;
        }
        throw new IllegalStateException();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return CLASSNAMEk.e(this);
    }

    /* renamed from: h */
    public boolean tryAdvance(u uVar) {
        uVar.getClass();
        int i = this.b;
        if (i < 0 || i >= this.c) {
            return false;
        }
        int[] iArr = this.a;
        this.b = i + 1;
        uVar.accept(iArr[i]);
        return true;
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEk.f(this, i);
    }

    public D trySplit() {
        int i = this.b;
        int i2 = (this.c + i) >>> 1;
        if (i >= i2) {
            return null;
        }
        int[] iArr = this.a;
        this.b = i2;
        return new S(iArr, i, i2, this.d);
    }
}
