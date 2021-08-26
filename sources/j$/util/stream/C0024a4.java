package j$.util.stream;

import j$.lang.e;
import j$.util.CLASSNAMEa;
import j$.util.x;
import j$.util.y;
import java.util.Arrays;
import java.util.Comparator;

/* renamed from: j$.util.stream.a4  reason: case insensitive filesystem */
abstract class CLASSNAMEa4 extends CLASSNAMEe implements Iterable, e {
    Object e = c(16);
    Object[] f;

    /* renamed from: j$.util.stream.a4$a */
    abstract class a implements x {
        int a;
        final int b;
        int c;
        final int d;
        Object e;

        a(int i, int i2, int i3, int i4) {
            this.a = i;
            this.b = i2;
            this.c = i3;
            this.d = i4;
            Object[] objArr = CLASSNAMEa4.this.f;
            this.e = objArr == null ? CLASSNAMEa4.this.e : objArr[i];
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
            long[] jArr = CLASSNAMEa4.this.d;
            return ((jArr[i2] + ((long) this.d)) - jArr[i]) - ((long) this.c);
        }

        /* access modifiers changed from: package-private */
        public abstract x f(Object obj, int i, int i2);

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
                    CLASSNAMEa4 a4Var = CLASSNAMEa4.this;
                    Object obj2 = a4Var.f[i2];
                    a4Var.t(obj2, i4, a4Var.u(obj2), obj);
                    i4 = 0;
                    i2++;
                }
                CLASSNAMEa4.this.t(this.a == i ? this.e : CLASSNAMEa4.this.f[i], i4, this.d, obj);
                this.a = this.b;
                this.c = this.d;
            }
        }

        public Comparator getComparator() {
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return CLASSNAMEa.e(this);
        }

        /* access modifiers changed from: package-private */
        public abstract x h(int i, int i2, int i3, int i4);

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return CLASSNAMEa.f(this, i);
        }

        /* renamed from: tryAdvance */
        public boolean k(Object obj) {
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
            if (this.c == CLASSNAMEa4.this.u(this.e)) {
                this.c = 0;
                int i4 = this.a + 1;
                this.a = i4;
                Object[] objArr = CLASSNAMEa4.this.f;
                if (objArr != null && i4 <= this.b) {
                    this.e = objArr[i4];
                }
            }
            return true;
        }

        public x trySplit() {
            int i = this.a;
            int i2 = this.b;
            if (i < i2) {
                int i3 = this.c;
                CLASSNAMEa4 a4Var = CLASSNAMEa4.this;
                x h = h(i, i2 - 1, i3, a4Var.u(a4Var.f[i2 - 1]));
                int i4 = this.b;
                this.a = i4;
                this.c = 0;
                this.e = CLASSNAMEa4.this.f[i4];
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
                x f2 = f(this.e, i6, i7);
                this.c += i7;
                return f2;
            }
        }
    }

    CLASSNAMEa4() {
    }

    CLASSNAMEa4(int i) {
        super(i);
    }

    private void y() {
        if (this.f == null) {
            Object[] z = z(8);
            this.f = z;
            this.d = new long[8];
            z[0] = this.e;
        }
    }

    /* access modifiers changed from: protected */
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
        long j = (long) i;
        long count = count() + j;
        if (count > ((long) u(obj)) || count < j) {
            throw new IndexOutOfBoundsException("does not fit");
        } else if (this.c == 0) {
            System.arraycopy(this.e, 0, obj, i, this.b);
        } else {
            for (int i2 = 0; i2 < this.c; i2++) {
                Object[] objArr = this.f;
                System.arraycopy(objArr[i2], 0, obj, i, u(objArr[i2]));
                i += u(this.f[i2]);
            }
            int i3 = this.b;
            if (i3 > 0) {
                System.arraycopy(this.e, 0, obj, i, i3);
            }
        }
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

    public abstract y spliterator();

    /* access modifiers changed from: protected */
    public abstract void t(Object obj, int i, int i2, Object obj2);

    /* access modifiers changed from: protected */
    public abstract int u(Object obj);

    /* access modifiers changed from: protected */
    public long v() {
        int i = this.c;
        if (i == 0) {
            return (long) u(this.e);
        }
        return ((long) u(this.f[i])) + this.d[i];
    }

    /* access modifiers changed from: protected */
    public int w(long j) {
        if (this.c == 0) {
            if (j < ((long) this.b)) {
                return 0;
            }
            throw new IndexOutOfBoundsException(Long.toString(j));
        } else if (j < count()) {
            for (int i = 0; i <= this.c; i++) {
                if (j < this.d[i] + ((long) u(this.f[i]))) {
                    return i;
                }
            }
            throw new IndexOutOfBoundsException(Long.toString(j));
        } else {
            throw new IndexOutOfBoundsException(Long.toString(j));
        }
    }

    /* access modifiers changed from: protected */
    public final void x(long j) {
        long v = v();
        if (j > v) {
            y();
            int i = this.c;
            while (true) {
                i++;
                if (j > v) {
                    Object[] objArr = this.f;
                    if (i >= objArr.length) {
                        int length = objArr.length * 2;
                        this.f = Arrays.copyOf(objArr, length);
                        this.d = Arrays.copyOf(this.d, length);
                    }
                    int s = s(i);
                    this.f[i] = c(s);
                    long[] jArr = this.d;
                    int i2 = i - 1;
                    jArr[i] = jArr[i2] + ((long) u(this.f[i2]));
                    v += (long) s;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public abstract Object[] z(int i);
}
