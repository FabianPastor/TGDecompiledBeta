package j$.util.stream;

import j$.util.L;
import j$.util.function.Consumer;
import j$.util.function.k;
import j$.util.function.l;
import j$.util.u;
import java.util.Arrays;
import java.util.Iterator;

class W3 extends Z3 implements l {
    W3() {
    }

    W3(int i) {
        super(i);
    }

    /* renamed from: B */
    public u.a spliterator() {
        return new V3(this, 0, this.c, 0, this.b);
    }

    public void accept(int i) {
        A();
        int i2 = this.b;
        this.b = i2 + 1;
        ((int[]) this.e)[i2] = i;
    }

    public Object c(int i) {
        return new int[i];
    }

    public void forEach(Consumer consumer) {
        if (consumer instanceof l) {
            g((l) consumer);
        } else if (!Q4.a) {
            spliterator().forEachRemaining(consumer);
        } else {
            Q4.a(getClass(), "{0} calling SpinedBuffer.OfInt.forEach(Consumer)");
            throw null;
        }
    }

    public Iterator iterator() {
        return L.g(spliterator());
    }

    public l l(l lVar) {
        lVar.getClass();
        return new k(this, lVar);
    }

    /* access modifiers changed from: protected */
    public void t(Object obj, int i, int i2, Object obj2) {
        int[] iArr = (int[]) obj;
        l lVar = (l) obj2;
        while (i < i2) {
            lVar.accept(iArr[i]);
            i++;
        }
    }

    public String toString() {
        int[] iArr = (int[]) e();
        if (iArr.length < 200) {
            return String.format("%s[length=%d, chunks=%d]%s", new Object[]{getClass().getSimpleName(), Integer.valueOf(iArr.length), Integer.valueOf(this.c), Arrays.toString(iArr)});
        }
        return String.format("%s[length=%d, chunks=%d]%s...", new Object[]{getClass().getSimpleName(), Integer.valueOf(iArr.length), Integer.valueOf(this.c), Arrays.toString(Arrays.copyOf(iArr, 200))});
    }

    /* access modifiers changed from: protected */
    public int u(Object obj) {
        return ((int[]) obj).length;
    }

    /* access modifiers changed from: protected */
    public Object[] z(int i) {
        return new int[i][];
    }
}
