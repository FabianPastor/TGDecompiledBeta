package j$.util.stream;

import j$.util.function.Consumer;
import j$.wrappers.CLASSNAMEh;
import j$.wrappers.CLASSNAMEw;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.a4  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public class CLASSNAMEa4 extends AbstractCLASSNAMEe implements Consumer, Iterable, j$.lang.e {
    protected Object[] e = new Object[16];
    protected Object[][] f;

    private void v() {
        if (this.f == null) {
            Object[][] objArr = new Object[8];
            this.f = objArr;
            this.d = new long[8];
            objArr[0] = this.e;
        }
    }

    @Override // j$.util.function.Consumer
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

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    @Override // j$.util.stream.AbstractCLASSNAMEe
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

    @Override // j$.lang.e
    public void forEach(Consumer consumer) {
        for (int i = 0; i < this.c; i++) {
            for (Object obj : this.f[i]) {
                consumer.accept(obj);
            }
        }
        for (int i2 = 0; i2 < this.b; i2++) {
            consumer.accept(this.e[i2]);
        }
    }

    @Override // java.lang.Iterable
    public /* synthetic */ void forEach(java.util.function.Consumer consumer) {
        forEach(CLASSNAMEw.b(consumer));
    }

    public void i(Object[] objArr, int i) {
        long j = i;
        long count = count() + j;
        if (count > objArr.length || count < j) {
            throw new IndexOutOfBoundsException("does not fit");
        }
        if (this.c == 0) {
            System.arraycopy(this.e, 0, objArr, i, this.b);
            return;
        }
        for (int i2 = 0; i2 < this.c; i2++) {
            Object[][] objArr2 = this.f;
            System.arraycopy(objArr2[i2], 0, objArr, i, objArr2[i2].length);
            i += this.f[i2].length;
        }
        int i3 = this.b;
        if (i3 <= 0) {
            return;
        }
        System.arraycopy(this.e, 0, objArr, i, i3);
    }

    @Override // java.lang.Iterable
    public Iterator iterator() {
        return j$.util.L.i(moNUMspliterator());
    }

    @Override // java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator */
    public j$.util.u moNUMspliterator() {
        return new S3(this, 0, this.c, 0, this.b);
    }

    @Override // java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator  reason: collision with other method in class */
    public /* synthetic */ Spliterator moNUMspliterator() {
        return CLASSNAMEh.a(moNUMspliterator());
    }

    protected long t() {
        int i = this.c;
        if (i == 0) {
            return this.e.length;
        }
        return this.f[i].length + this.d[i];
    }

    public String toString() {
        ArrayList arrayList = new ArrayList();
        forEach(new CLASSNAMEb(arrayList));
        return "SpinedBuffer:" + arrayList.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void u(long j) {
        Object[][] objArr;
        int i;
        long t = t();
        if (j > t) {
            v();
            int i2 = this.c;
            while (true) {
                i2++;
                if (j <= t) {
                    return;
                }
                Object[][] objArr2 = this.f;
                if (i2 >= objArr2.length) {
                    int length = objArr2.length * 2;
                    this.f = (Object[][]) Arrays.copyOf(objArr2, length);
                    this.d = Arrays.copyOf(this.d, length);
                }
                int s = s(i2);
                this.f[i2] = new Object[s];
                long[] jArr = this.d;
                jArr[i2] = jArr[i2 - 1] + objArr[i].length;
                t += s;
            }
        }
    }
}
