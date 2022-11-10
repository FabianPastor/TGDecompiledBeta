package j$.util.stream;

import j$.util.AbstractCLASSNAMEa;
import j$.util.u;
import j$.wrappers.CLASSNAMEh;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Spliterator;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public abstract class Z3 extends AbstractCLASSNAMEe implements Iterable, j$.lang.e {
    Object e;
    Object[] f;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public abstract class a implements j$.util.w {
        int a;
        final int b;
        int c;
        final int d;
        Object e;

        /* JADX INFO: Access modifiers changed from: package-private */
        public a(int i, int i2, int i3, int i4) {
            this.a = i;
            this.b = i2;
            this.c = i3;
            this.d = i4;
            Object[] objArr = Z3.this.f;
            this.e = objArr == null ? Z3.this.e : objArr[i];
        }

        abstract void a(Object obj, int i, Object obj2);

        @Override // j$.util.u
        public int characteristics() {
            return 16464;
        }

        @Override // j$.util.u
        public long estimateSize() {
            int i = this.a;
            int i2 = this.b;
            if (i == i2) {
                return this.d - this.c;
            }
            long[] jArr = Z3.this.d;
            return ((jArr[i2] + this.d) - jArr[i]) - this.c;
        }

        abstract j$.util.w f(Object obj, int i, int i2);

        @Override // j$.util.w
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
                    Z3 z3 = Z3.this;
                    Object obj2 = z3.f[i2];
                    z3.t(obj2, i4, z3.u(obj2), obj);
                    i4 = 0;
                    i2++;
                }
                Z3.this.t(this.a == i ? this.e : Z3.this.f[i], i4, this.d, obj);
                this.a = this.b;
                this.c = this.d;
            }
        }

        @Override // j$.util.u
        public Comparator getComparator() {
            throw new IllegalStateException();
        }

        @Override // j$.util.u
        public /* synthetic */ long getExactSizeIfKnown() {
            return AbstractCLASSNAMEa.e(this);
        }

        abstract j$.util.w h(int i, int i2, int i3, int i4);

        @Override // j$.util.u
        public /* synthetic */ boolean hasCharacteristics(int i) {
            return AbstractCLASSNAMEa.f(this, i);
        }

        @Override // j$.util.w
        /* renamed from: tryAdvance */
        public boolean k(Object obj) {
            obj.getClass();
            int i = this.a;
            int i2 = this.b;
            if (i < i2 || (i == i2 && this.c < this.d)) {
                Object obj2 = this.e;
                int i3 = this.c;
                this.c = i3 + 1;
                a(obj2, i3, obj);
                if (this.c == Z3.this.u(this.e)) {
                    this.c = 0;
                    int i4 = this.a + 1;
                    this.a = i4;
                    Object[] objArr = Z3.this.f;
                    if (objArr != null && i4 <= this.b) {
                        this.e = objArr[i4];
                    }
                }
                return true;
            }
            return false;
        }

        @Override // j$.util.w, j$.util.u
        /* renamed from: trySplit */
        public /* bridge */ /* synthetic */ j$.util.t moNUMtrySplit() {
            return (j$.util.t) moNUMtrySplit();
        }

        @Override // j$.util.w, j$.util.u
        /* renamed from: trySplit  reason: collision with other method in class */
        public /* bridge */ /* synthetic */ u.a moNUMtrySplit() {
            return (u.a) moNUMtrySplit();
        }

        @Override // j$.util.w, j$.util.u
        /* renamed from: trySplit  reason: collision with other method in class */
        public /* bridge */ /* synthetic */ j$.util.v moNUMtrySplit() {
            return (j$.util.v) moNUMtrySplit();
        }

        @Override // j$.util.w, j$.util.u
        /* renamed from: trySplit */
        public j$.util.w moNUMtrySplit() {
            int i = this.a;
            int i2 = this.b;
            if (i < i2) {
                int i3 = this.c;
                Z3 z3 = Z3.this;
                j$.util.w h = h(i, i2 - 1, i3, z3.u(z3.f[i2 - 1]));
                int i4 = this.b;
                this.a = i4;
                this.c = 0;
                this.e = Z3.this.f[i4];
                return h;
            } else if (i != i2) {
                return null;
            } else {
                int i5 = this.d;
                int i6 = this.c;
                int i7 = (i5 - i6) / 2;
                if (i7 == 0) {
                    return null;
                }
                j$.util.w f = f(this.e, i6, i7);
                this.c += i7;
                return f;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Z3() {
        this.e = c(16);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Z3(int i) {
        super(i);
        this.e = c(1 << this.a);
    }

    private void y() {
        if (this.f == null) {
            Object[] z = z(8);
            this.f = z;
            this.d = new long[8];
            z[0] = this.e;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void A() {
        if (this.b == u(this.e)) {
            y();
            int i = this.c;
            int i2 = i + 1;
            Object[] objArr = this.f;
            if (i2 >= objArr.length || objArr[i + 1] == null) {
                x(v() + 1);
            }
            this.b = 0;
            int i3 = this.c + 1;
            this.c = i3;
            this.e = this.f[i3];
        }
    }

    public abstract Object c(int i);

    @Override // j$.util.stream.AbstractCLASSNAMEe
    public void clear() {
        Object[] objArr = this.f;
        if (objArr != null) {
            this.e = objArr[0];
            this.f = null;
            this.d = null;
        }
        this.b = 0;
        this.c = 0;
    }

    public void d(Object obj, int i) {
        long j = i;
        long count = count() + j;
        if (count > u(obj) || count < j) {
            throw new IndexOutOfBoundsException("does not fit");
        }
        if (this.c == 0) {
            System.arraycopy(this.e, 0, obj, i, this.b);
            return;
        }
        for (int i2 = 0; i2 < this.c; i2++) {
            Object[] objArr = this.f;
            System.arraycopy(objArr[i2], 0, obj, i, u(objArr[i2]));
            i += u(this.f[i2]);
        }
        int i3 = this.b;
        if (i3 <= 0) {
            return;
        }
        System.arraycopy(this.e, 0, obj, i, i3);
    }

    public Object e() {
        long count = count();
        if (count < NUM) {
            Object c = c((int) count);
            d(c, 0);
            return c;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public void g(Object obj) {
        for (int i = 0; i < this.c; i++) {
            Object[] objArr = this.f;
            t(objArr[i], 0, u(objArr[i]), obj);
        }
        t(this.e, 0, this.b, obj);
    }

    /* renamed from: spliterator */
    public abstract j$.util.u moNUMspliterator();

    @Override // java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator  reason: collision with other method in class */
    public /* synthetic */ Spliterator moNUMspliterator() {
        return CLASSNAMEh.a(moNUMspliterator());
    }

    protected abstract void t(Object obj, int i, int i2, Object obj2);

    protected abstract int u(Object obj);

    protected long v() {
        int i = this.c;
        if (i == 0) {
            return u(this.e);
        }
        return u(this.f[i]) + this.d[i];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int w(long j) {
        if (this.c == 0) {
            if (j >= this.b) {
                throw new IndexOutOfBoundsException(Long.toString(j));
            }
            return 0;
        } else if (j >= count()) {
            throw new IndexOutOfBoundsException(Long.toString(j));
        } else {
            for (int i = 0; i <= this.c; i++) {
                if (j < this.d[i] + u(this.f[i])) {
                    return i;
                }
            }
            throw new IndexOutOfBoundsException(Long.toString(j));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void x(long j) {
        int i;
        long v = v();
        if (j > v) {
            y();
            int i2 = this.c;
            while (true) {
                i2++;
                if (j <= v) {
                    return;
                }
                Object[] objArr = this.f;
                if (i2 >= objArr.length) {
                    int length = objArr.length * 2;
                    this.f = Arrays.copyOf(objArr, length);
                    this.d = Arrays.copyOf(this.d, length);
                }
                int s = s(i2);
                this.f[i2] = c(s);
                long[] jArr = this.d;
                jArr[i2] = jArr[i2 - 1] + u(this.f[i]);
                v += s;
            }
        }
    }

    protected abstract Object[] z(int i);
}
