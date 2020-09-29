package j$.util.stream;

import j$.C;
import j$.lang.Iterable;
import j$.util.Spliterator;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import j$.util.k0;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* renamed from: j$.util.stream.r6  reason: case insensitive filesystem */
class CLASSNAMEr6 extends CLASSNAMEj1 implements Consumer, Iterable, Iterable {
    protected Object[] e = new Object[(1 << this.a)];
    protected Object[][] f;

    public /* synthetic */ void forEach(java.util.function.Consumer consumer) {
        forEach(C.a(consumer));
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    CLASSNAMEr6() {
    }

    /* access modifiers changed from: protected */
    public long z() {
        int i = this.c;
        if (i == 0) {
            return (long) this.e.length;
        }
        return ((long) this.f[i].length) + this.d[i];
    }

    private void D() {
        if (this.f == null) {
            Object[][] objArr = new Object[8][];
            this.f = objArr;
            this.d = new long[8];
            objArr[0] = this.e;
        }
    }

    /* access modifiers changed from: protected */
    public final void A(long targetSize) {
        long capacity = z();
        if (targetSize > capacity) {
            D();
            int i = this.c;
            while (true) {
                i++;
                if (targetSize > capacity) {
                    Object[][] objArr = this.f;
                    if (i >= objArr.length) {
                        int newSpineSize = objArr.length * 2;
                        this.f = (Object[][]) Arrays.copyOf(objArr, newSpineSize);
                        this.d = Arrays.copyOf(this.d, newSpineSize);
                    }
                    int nextChunkSize = y(i);
                    Object[][] objArr2 = this.f;
                    objArr2[i] = new Object[nextChunkSize];
                    long[] jArr = this.d;
                    jArr[i] = jArr[i - 1] + ((long) objArr2[i - 1].length);
                    capacity += (long) nextChunkSize;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void C() {
        A(z() + 1);
    }

    public Object B(long index) {
        if (this.c == 0) {
            if (index < ((long) this.b)) {
                return this.e[(int) index];
            }
            throw new IndexOutOfBoundsException(Long.toString(index));
        } else if (index < count()) {
            for (int j = 0; j <= this.c; j++) {
                long[] jArr = this.d;
                long j2 = jArr[j];
                Object[][] objArr = this.f;
                if (index < j2 + ((long) objArr[j].length)) {
                    return objArr[j][(int) (index - jArr[j])];
                }
            }
            throw new IndexOutOfBoundsException(Long.toString(index));
        } else {
            throw new IndexOutOfBoundsException(Long.toString(index));
        }
    }

    public void m(Object[] array, int offset) {
        long finalOffset = ((long) offset) + count();
        if (finalOffset > ((long) array.length) || finalOffset < ((long) offset)) {
            throw new IndexOutOfBoundsException("does not fit");
        } else if (this.c == 0) {
            System.arraycopy(this.e, 0, array, offset, this.b);
        } else {
            for (int i = 0; i < this.c; i++) {
                Object[][] objArr = this.f;
                System.arraycopy(objArr[i], 0, array, offset, objArr[i].length);
                offset += this.f[i].length;
            }
            int i2 = this.b;
            if (i2 > 0) {
                System.arraycopy(this.e, 0, array, offset, i2);
            }
        }
    }

    public Object[] x(j$.util.function.C c) {
        long size = count();
        if (size < NUM) {
            E[] result = (Object[]) c.a((int) size);
            m(result, 0);
            return result;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public void clear() {
        Object[][] objArr = this.f;
        if (objArr != null) {
            this.e = objArr[0];
            int i = 0;
            while (true) {
                Object[] objArr2 = this.e;
                if (i >= objArr2.length) {
                    break;
                }
                objArr2[i] = null;
                i++;
            }
            this.f = null;
            this.d = null;
        } else {
            for (int i2 = 0; i2 < this.b; i2++) {
                this.e[i2] = null;
            }
        }
        this.b = 0;
        this.c = 0;
    }

    public Iterator iterator() {
        return k0.i(spliterator());
    }

    public void forEach(Consumer consumer) {
        for (int j = 0; j < this.c; j++) {
            for (E t : this.f[j]) {
                consumer.accept(t);
            }
        }
        for (int i = 0; i < this.b; i++) {
            consumer.accept(this.e[i]);
        }
    }

    public void accept(Object e2) {
        if (this.b == this.e.length) {
            D();
            int i = this.c;
            int i2 = i + 1;
            Object[][] objArr = this.f;
            if (i2 >= objArr.length || objArr[i + 1] == null) {
                C();
            }
            this.b = 0;
            int i3 = this.c + 1;
            this.c = i3;
            this.e = this.f[i3];
        }
        Object[] objArr2 = this.e;
        int i4 = this.b;
        this.b = i4 + 1;
        objArr2[i4] = e2;
    }

    public String toString() {
        List<E> list = new ArrayList<>();
        list.getClass();
        forEach((Consumer) new P(list));
        return "SpinedBuffer:" + list.toString();
    }

    public Spliterator spliterator() {
        return new CLASSNAMEi6(this, 0, this.c, 0, this.b);
    }
}
