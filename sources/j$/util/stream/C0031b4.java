package j$.util.stream;

import j$.lang.e;
import j$.util.N;
import j$.util.function.Consumer;
import j$.util.y;
import j$.wrappers.CLASSNAMEw;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* renamed from: j$.util.stream.b4  reason: case insensitive filesystem */
class CLASSNAMEb4 extends CLASSNAMEe implements Consumer, Iterable, e {
    protected Object[] e = new Object[16];
    protected Object[][] f;

    CLASSNAMEb4() {
    }

    private void v() {
        if (this.f == null) {
            Object[][] objArr = new Object[8][];
            this.f = objArr;
            this.d = new long[8];
            objArr[0] = this.e;
        }
    }

    public void accept(Object obj) {
        if (this.b == this.e.length) {
            v();
            int i = this.c;
            int i2 = i + 1;
            Object[][] objArr = this.f;
            if (i2 >= objArr.length || objArr[i + 1] == null) {
                u(t() + 1);
            }
            this.b = 0;
            int i3 = this.c + 1;
            this.c = i3;
            this.e = this.f[i3];
        }
        Object[] objArr2 = this.e;
        int i4 = this.b;
        this.b = i4 + 1;
        objArr2[i4] = obj;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
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

    public void forEach(Consumer consumer) {
        for (int i = 0; i < this.c; i++) {
            for (Object accept : this.f[i]) {
                consumer.accept(accept);
            }
        }
        for (int i2 = 0; i2 < this.b; i2++) {
            consumer.accept(this.e[i2]);
        }
    }

    public /* synthetic */ void forEach(java.util.function.Consumer consumer) {
        forEach(CLASSNAMEw.b(consumer));
    }

    public void i(Object[] objArr, int i) {
        long j = (long) i;
        long count = count() + j;
        if (count > ((long) objArr.length) || count < j) {
            throw new IndexOutOfBoundsException("does not fit");
        } else if (this.c == 0) {
            System.arraycopy(this.e, 0, objArr, i, this.b);
        } else {
            for (int i2 = 0; i2 < this.c; i2++) {
                Object[][] objArr2 = this.f;
                System.arraycopy(objArr2[i2], 0, objArr, i, objArr2[i2].length);
                i += this.f[i2].length;
            }
            int i3 = this.b;
            if (i3 > 0) {
                System.arraycopy(this.e, 0, objArr, i, i3);
            }
        }
    }

    public Iterator iterator() {
        return N.i(spliterator());
    }

    public y spliterator() {
        return new T3(this, 0, this.c, 0, this.b);
    }

    /* access modifiers changed from: protected */
    public long t() {
        int i = this.c;
        if (i == 0) {
            return (long) this.e.length;
        }
        return ((long) this.f[i].length) + this.d[i];
    }

    public String toString() {
        ArrayList arrayList = new ArrayList();
        forEach(new CLASSNAMEb((List) arrayList));
        return "SpinedBuffer:" + arrayList.toString();
    }

    /* access modifiers changed from: protected */
    public final void u(long j) {
        long t = t();
        if (j > t) {
            v();
            int i = this.c;
            while (true) {
                i++;
                if (j > t) {
                    Object[][] objArr = this.f;
                    if (i >= objArr.length) {
                        int length = objArr.length * 2;
                        this.f = (Object[][]) Arrays.copyOf(objArr, length);
                        this.d = Arrays.copyOf(this.d, length);
                    }
                    int s = s(i);
                    Object[][] objArr2 = this.f;
                    objArr2[i] = new Object[s];
                    long[] jArr = this.d;
                    int i2 = i - 1;
                    jArr[i] = jArr[i2] + ((long) objArr2[i2].length);
                    t += (long) s;
                } else {
                    return;
                }
            }
        }
    }
}
