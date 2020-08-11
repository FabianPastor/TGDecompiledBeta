package j$.util.stream;

import j$.util.CLASSNAMEn;
import j$.util.N;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import java.util.Comparator;

/* renamed from: j$.util.stream.i6  reason: case insensitive filesystem */
class CLASSNAMEi6 implements Spliterator {
    int a;
    final int b;
    int c;
    final int d;
    Object[] e;
    final /* synthetic */ CLASSNAMEr6 f;

    public /* synthetic */ Comparator getComparator() {
        N.a(this);
        throw null;
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return N.b(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return N.c(this, i);
    }

    static {
        Class<CLASSNAMEr6> cls = CLASSNAMEr6.class;
    }

    CLASSNAMEi6(CLASSNAMEr6 this$0, int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
        this.f = this$0;
        this.a = firstSpineIndex;
        this.b = lastSpineIndex;
        this.c = firstSpineElementIndex;
        this.d = lastSpineElementFence;
        Object[][] objArr = this$0.f;
        this.e = objArr == null ? this$0.e : objArr[firstSpineIndex];
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

    public int characteristics() {
        return 16464;
    }

    public boolean a(Consumer consumer) {
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

    public void forEachRemaining(Consumer consumer) {
        int i;
        consumer.getClass();
        int i2 = this.a;
        int i3 = this.b;
        if (i2 < i3 || (i2 == i3 && this.c < this.d)) {
            int i4 = this.c;
            int sp = this.a;
            while (true) {
                i = this.b;
                if (sp >= i) {
                    break;
                }
                E[] chunk = this.f.f[sp];
                while (i4 < chunk.length) {
                    consumer.accept(chunk[i4]);
                    i4++;
                }
                i4 = 0;
                sp++;
            }
            E[] chunk2 = this.a == i ? this.e : this.f.f[i];
            int hElementIndex = this.d;
            while (i4 < hElementIndex) {
                consumer.accept(chunk2[i4]);
                i4++;
            }
            this.a = this.b;
            this.c = this.d;
        }
    }

    public Spliterator trySplit() {
        int i = this.a;
        int i2 = this.b;
        if (i < i2) {
            CLASSNAMEr6 r6Var = this.f;
            CLASSNAMEi6 i6Var = new CLASSNAMEi6(r6Var, i, i2 - 1, this.c, r6Var.f[i2 - 1].length);
            int i3 = this.b;
            this.a = i3;
            this.c = 0;
            this.e = this.f.f[i3];
            return i6Var;
        } else if (i != i2) {
            return null;
        } else {
            int i4 = this.d;
            int i5 = this.c;
            int t = (i4 - i5) / 2;
            if (t == 0) {
                return null;
            }
            Spliterator d2 = CLASSNAMEn.d(this.e, i5, i5 + t);
            this.c += t;
            return d2;
        }
    }
}
