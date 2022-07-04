package j$.util;

import j$.util.function.Consumer;
import java.util.Comparator;

final class B implements u {
    private final Object[] a;
    private int b;
    private final int c;
    private final int d;

    public B(Object[] objArr, int i, int i2, int i3) {
        this.a = objArr;
        this.b = i;
        this.c = i2;
        this.d = i3 | 64 | 16384;
    }

    public boolean b(Consumer consumer) {
        consumer.getClass();
        int i = this.b;
        if (i < 0 || i >= this.c) {
            return false;
        }
        Object[] objArr = this.a;
        this.b = i + 1;
        consumer.accept(objArr[i]);
        return true;
    }

    public int characteristics() {
        return this.d;
    }

    public long estimateSize() {
        return (long) (this.c - this.b);
    }

    public void forEachRemaining(Consumer consumer) {
        int i;
        consumer.getClass();
        Object[] objArr = this.a;
        int length = objArr.length;
        int i2 = this.c;
        if (length >= i2 && (i = this.b) >= 0) {
            this.b = i2;
            if (i < i2) {
                do {
                    consumer.accept(objArr[i]);
                    i++;
                } while (i < i2);
            }
        }
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

    public u trySplit() {
        int i = this.b;
        int i2 = (this.c + i) >>> 1;
        if (i >= i2) {
            return null;
        }
        Object[] objArr = this.a;
        this.b = i2;
        return new B(objArr, i, i2, this.d);
    }
}
