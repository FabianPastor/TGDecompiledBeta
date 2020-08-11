package j$.util.stream;

import j$.lang.Iterable;
import j$.util.Spliterator;
import java.util.Arrays;

/* renamed from: j$.util.stream.q6  reason: case insensitive filesystem */
abstract class CLASSNAMEq6 extends CLASSNAMEj1 implements Iterable, Iterable {
    Object e = a(1 << this.a);
    Object[] f;

    /* access modifiers changed from: protected */
    public abstract int A(Object obj);

    /* access modifiers changed from: protected */
    public abstract Object[] G(int i);

    public abstract Object a(int i);

    public abstract Spliterator spliterator();

    /* access modifiers changed from: protected */
    public abstract void z(Object obj, int i, int i2, Object obj2);

    CLASSNAMEq6(int initialCapacity) {
        super(initialCapacity);
    }

    CLASSNAMEq6() {
    }

    /* access modifiers changed from: protected */
    public long B() {
        int i = this.c;
        if (i == 0) {
            return (long) A(this.e);
        }
        return ((long) A(this.f[i])) + this.d[i];
    }

    private void F() {
        if (this.f == null) {
            Object[] G = G(8);
            this.f = G;
            this.d = new long[8];
            G[0] = this.e;
        }
    }

    /* access modifiers changed from: protected */
    public final void D(long targetSize) {
        long capacity = B();
        if (targetSize > capacity) {
            F();
            int i = this.c;
            while (true) {
                i++;
                if (targetSize > capacity) {
                    Object[] objArr = this.f;
                    if (i >= objArr.length) {
                        int newSpineSize = objArr.length * 2;
                        this.f = Arrays.copyOf(objArr, newSpineSize);
                        this.d = Arrays.copyOf(this.d, newSpineSize);
                    }
                    int nextChunkSize = y(i);
                    this.f[i] = a(nextChunkSize);
                    long[] jArr = this.d;
                    jArr[i] = jArr[i - 1] + ((long) A(this.f[i - 1]));
                    capacity += (long) nextChunkSize;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void E() {
        D(B() + 1);
    }

    /* access modifiers changed from: protected */
    public int C(long index) {
        if (this.c == 0) {
            if (index < ((long) this.b)) {
                return 0;
            }
            throw new IndexOutOfBoundsException(Long.toString(index));
        } else if (index < count()) {
            for (int j = 0; j <= this.c; j++) {
                if (index < this.d[j] + ((long) A(this.f[j]))) {
                    return j;
                }
            }
            throw new IndexOutOfBoundsException(Long.toString(index));
        } else {
            throw new IndexOutOfBoundsException(Long.toString(index));
        }
    }

    public void f(Object array, int offset) {
        long finalOffset = ((long) offset) + count();
        if (finalOffset > ((long) A(array)) || finalOffset < ((long) offset)) {
            throw new IndexOutOfBoundsException("does not fit");
        } else if (this.c == 0) {
            System.arraycopy(this.e, 0, array, offset, this.b);
        } else {
            for (int i = 0; i < this.c; i++) {
                Object[] objArr = this.f;
                System.arraycopy(objArr[i], 0, array, offset, A(objArr[i]));
                offset += A(this.f[i]);
            }
            int i2 = this.b;
            if (i2 > 0) {
                System.arraycopy(this.e, 0, array, offset, i2);
            }
        }
    }

    public Object i() {
        long size = count();
        if (size < NUM) {
            T_ARR result = a((int) size);
            f(result, 0);
            return result;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    /* access modifiers changed from: protected */
    public void H() {
        if (this.b == A(this.e)) {
            F();
            int i = this.c;
            int i2 = i + 1;
            Object[] objArr = this.f;
            if (i2 >= objArr.length || objArr[i + 1] == null) {
                E();
            }
            this.b = 0;
            int i3 = this.c + 1;
            this.c = i3;
            this.e = this.f[i3];
        }
    }

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

    public void j(Object consumer) {
        for (int j = 0; j < this.c; j++) {
            Object[] objArr = this.f;
            z(objArr[j], 0, A(objArr[j]), consumer);
        }
        z(this.e, 0, this.b, consumer);
    }
}
