package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.Spliterator;
import j$.util.V;
import j$.util.function.Consumer;
import java.util.Comparator;

class U5 implements Spliterator {
    int a;
    final int b;
    int c;
    final int d;
    Object[] e;
    final /* synthetic */ CLASSNAMEd6 f;

    U5(CLASSNAMEd6 d6Var, int i, int i2, int i3, int i4) {
        this.f = d6Var;
        this.a = i;
        this.b = i2;
        this.c = i3;
        this.d = i4;
        Object[][] objArr = d6Var.f;
        this.e = objArr == null ? d6Var.e : objArr[i];
    }

    public boolean b(Consumer consumer) {
        consumer.getClass();
        int i = this.a;
        int i2 = this.b;
        if (i >= i2 && (i != i2 || this.c >= this.d)) {
            return false;
        }
        Object[] objArr = this.e;
        int i3 = this.c;
        this.c = i3 + 1;
        consumer.accept(objArr[i3]);
        if (this.c == this.e.length) {
            this.c = 0;
            int i4 = this.a + 1;
            this.a = i4;
            Object[][] objArr2 = this.f.f;
            if (objArr2 != null && i4 <= this.b) {
                this.e = objArr2[i4];
            }
        }
        return true;
    }

    public int characteristics() {
        return 16464;
    }

    public long estimateSize() {
        int i = this.a;
        int i2 = this.b;
        if (i == i2) {
            return ((long) this.d) - ((long) this.c);
        }
        long[] jArr = this.f.d;
        return ((jArr[i2] + ((long) this.d)) - jArr[i]) - ((long) this.c);
    }

    public void forEachRemaining(Consumer consumer) {
        int i;
        consumer.getClass();
        int i2 = this.a;
        int i3 = this.b;
        if (i2 < i3 || (i2 == i3 && this.c < this.d)) {
            int i4 = this.c;
            while (true) {
                i = this.b;
                if (i2 >= i) {
                    break;
                }
                Object[] objArr = this.f.f[i2];
                while (i4 < objArr.length) {
                    consumer.accept(objArr[i4]);
                    i4++;
                }
                i4 = 0;
                i2++;
            }
            Object[] objArr2 = this.a == i ? this.e : this.f.f[i];
            int i5 = this.d;
            while (i4 < i5) {
                consumer.accept(objArr2[i4]);
                i4++;
            }
            this.a = this.b;
            this.c = this.d;
        }
    }

    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return CLASSNAMEk.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEk.f(this, i);
    }

    public Spliterator trySplit() {
        int i = this.a;
        int i2 = this.b;
        if (i < i2) {
            CLASSNAMEd6 d6Var = this.f;
            int i3 = i2 - 1;
            U5 u5 = new U5(d6Var, i, i3, this.c, d6Var.f[i3].length);
            int i4 = this.b;
            this.a = i4;
            this.c = 0;
            this.e = this.f.f[i4];
            return u5;
        } else if (i != i2) {
            return null;
        } else {
            int i5 = this.d;
            int i6 = this.c;
            int i7 = (i5 - i6) / 2;
            if (i7 == 0) {
                return null;
            }
            Spliterator m = V.m(this.e, i6, i6 + i7, 1040);
            this.c += i7;
            return m;
        }
    }
}
