package j$.util.stream;

import j$.lang.Iterable;
import j$.util.Spliterator;
import java.util.Arrays;

/* renamed from: j$.util.stream.c6  reason: case insensitive filesystem */
abstract class CLASSNAMEc6 extends CLASSNAMEj1 implements Iterable, Iterable {
    Object e = c(16);
    Object[] f;

    CLASSNAMEc6() {
    }

    CLASSNAMEc6(int i) {
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

    public void h(Object obj) {
        for (int i = 0; i < this.c; i++) {
            Object[] objArr = this.f;
            t(objArr[i], 0, u(objArr[i]), obj);
        }
        t(this.e, 0, this.b, obj);
    }

    public abstract Spliterator spliterator();

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
