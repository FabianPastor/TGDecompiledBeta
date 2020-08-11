package j$.util.stream;

import j$.util.N;
import j$.util.V;
import java.util.Comparator;

/* renamed from: j$.util.stream.p6  reason: case insensitive filesystem */
abstract class CLASSNAMEp6 implements V {
    int a;
    final int b;
    int c;
    final int d;
    Object e;
    final /* synthetic */ CLASSNAMEq6 f;

    /* access modifiers changed from: package-private */
    public abstract void b(Object obj, int i, Object obj2);

    /* access modifiers changed from: package-private */
    public abstract V g(Object obj, int i, int i2);

    public /* synthetic */ Comparator getComparator() {
        N.a(this);
        throw null;
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return N.b(this);
    }

    /* access modifiers changed from: package-private */
    public abstract V h(int i, int i2, int i3, int i4);

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return N.c(this, i);
    }

    static {
        Class<CLASSNAMEr6> cls = CLASSNAMEr6.class;
    }

    CLASSNAMEp6(CLASSNAMEq6 this$0, int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
        this.f = this$0;
        this.a = firstSpineIndex;
        this.b = lastSpineIndex;
        this.c = firstSpineElementIndex;
        this.d = lastSpineElementFence;
        Object[] objArr = this$0.f;
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

    public boolean tryAdvance(Object consumer) {
        consumer.getClass();
        int i = this.a;
        int i2 = this.b;
        if (i >= i2 && (i != i2 || this.c >= this.d)) {
            return false;
        }
        Object obj = this.e;
        int i3 = this.c;
        this.c = i3 + 1;
        b(obj, i3, consumer);
        if (this.c == this.f.A(this.e)) {
            this.c = 0;
            int i4 = this.a + 1;
            this.a = i4;
            Object[] objArr = this.f.f;
            if (objArr != null && i4 <= this.b) {
                this.e = objArr[i4];
            }
        }
        return true;
    }

    public void forEachRemaining(Object consumer) {
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
                CLASSNAMEq6 q6Var = this.f;
                T_ARR chunk = q6Var.f[sp];
                q6Var.z(chunk, i4, q6Var.A(chunk), consumer);
                i4 = 0;
                sp++;
            }
            this.f.z(this.a == i ? this.e : this.f.f[i], i4, this.d, consumer);
            this.a = this.b;
            this.c = this.d;
        }
    }

    public V trySplit() {
        int i = this.a;
        int i2 = this.b;
        if (i < i2) {
            int i3 = this.c;
            CLASSNAMEq6 q6Var = this.f;
            T_SPLITR ret = h(i, i2 - 1, i3, q6Var.A(q6Var.f[i2 - 1]));
            int i4 = this.b;
            this.a = i4;
            this.c = 0;
            this.e = this.f.f[i4];
            return ret;
        } else if (i != i2) {
            return null;
        } else {
            int i5 = this.d;
            int i6 = this.c;
            int t = (i5 - i6) / 2;
            if (t == 0) {
                return null;
            }
            T_SPLITR ret2 = g(this.e, i6, t);
            this.c += t;
            return ret2;
        }
    }
}
