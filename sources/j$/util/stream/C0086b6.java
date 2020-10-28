package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.F;
import java.util.Comparator;

/* renamed from: j$.util.stream.b6  reason: case insensitive filesystem */
abstract class CLASSNAMEb6 implements F {
    int a;
    final int b;
    int c;
    final int d;
    Object e;
    final /* synthetic */ CLASSNAMEc6 f;

    CLASSNAMEb6(CLASSNAMEc6 c6Var, int i, int i2, int i3, int i4) {
        this.f = c6Var;
        this.a = i;
        this.b = i2;
        this.c = i3;
        this.d = i4;
        Object[] objArr = c6Var.f;
        this.e = objArr == null ? c6Var.e : objArr[i];
    }

    /* access modifiers changed from: package-private */
    public abstract void a(Object obj, int i, Object obj2);

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

    /* access modifiers changed from: package-private */
    public abstract F f(Object obj, int i, int i2);

    /* renamed from: forEachRemaining */
    public void e(Object obj) {
        int i;
        obj.getClass();
        int i2 = this.a;
        int i3 = this.b;
        if (i2 < i3 || (i2 == i3 && this.c < this.d)) {
            int i4 = this.c;
            while (true) {
                i = this.b;
                if (i2 >= i) {
                    break;
                }
                CLASSNAMEc6 c6Var = this.f;
                Object obj2 = c6Var.f[i2];
                c6Var.t(obj2, i4, c6Var.u(obj2), obj);
                i4 = 0;
                i2++;
            }
            this.f.t(this.a == i ? this.e : this.f.f[i], i4, this.d, obj);
            this.a = this.b;
            this.c = this.d;
        }
    }

    /* access modifiers changed from: package-private */
    public abstract F g(int i, int i2, int i3, int i4);

    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return CLASSNAMEk.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEk.f(this, i);
    }

    /* renamed from: tryAdvance */
    public boolean o(Object obj) {
        obj.getClass();
        int i = this.a;
        int i2 = this.b;
        if (i >= i2 && (i != i2 || this.c >= this.d)) {
            return false;
        }
        Object obj2 = this.e;
        int i3 = this.c;
        this.c = i3 + 1;
        a(obj2, i3, obj);
        if (this.c == this.f.u(this.e)) {
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

    public F trySplit() {
        int i = this.a;
        int i2 = this.b;
        if (i < i2) {
            int i3 = this.c;
            CLASSNAMEc6 c6Var = this.f;
            F g = g(i, i2 - 1, i3, c6Var.u(c6Var.f[i2 - 1]));
            int i4 = this.b;
            this.a = i4;
            this.c = 0;
            this.e = this.f.f[i4];
            return g;
        } else if (i != i2) {
            return null;
        } else {
            int i5 = this.d;
            int i6 = this.c;
            int i7 = (i5 - i6) / 2;
            if (i7 == 0) {
                return null;
            }
            F f2 = f(this.e, i6, i7);
            this.c += i7;
            return f2;
        }
    }
}
