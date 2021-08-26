package j$.util.stream;

import j$.util.N;
import j$.util.function.Consumer;
import j$.util.function.o;
import j$.util.function.p;
import j$.util.w;
import java.util.Arrays;
import java.util.Iterator;

class Z3 extends CLASSNAMEa4 implements p {
    Z3() {
    }

    Z3(int i) {
        super(i);
    }

    /* renamed from: B */
    public w spliterator() {
        return new Y3(this, 0, this.c, 0, this.b);
    }

    public void accept(long j) {
        A();
        int i = this.b;
        this.b = i + 1;
        ((long[]) this.e)[i] = j;
    }

    public Object c(int i) {
        return new long[i];
    }

    public p f(p pVar) {
        pVar.getClass();
        return new o(this, pVar);
    }

    public void forEach(Consumer consumer) {
        if (consumer instanceof p) {
            g((p) consumer);
        } else if (!R4.a) {
            spliterator().forEachRemaining(consumer);
        } else {
            R4.a(getClass(), "{0} calling SpinedBuffer.OfLong.forEach(Consumer)");
            throw null;
        }
    }

    public Iterator iterator() {
        return N.h(spliterator());
    }

    /* access modifiers changed from: protected */
    public void t(Object obj, int i, int i2, Object obj2) {
        long[] jArr = (long[]) obj;
        p pVar = (p) obj2;
        while (i < i2) {
            pVar.accept(jArr[i]);
            i++;
        }
    }

    public String toString() {
        long[] jArr = (long[]) e();
        if (jArr.length < 200) {
            return String.format("%s[length=%d, chunks=%d]%s", new Object[]{getClass().getSimpleName(), Integer.valueOf(jArr.length), Integer.valueOf(this.c), Arrays.toString(jArr)});
        }
        return String.format("%s[length=%d, chunks=%d]%s...", new Object[]{getClass().getSimpleName(), Integer.valueOf(jArr.length), Integer.valueOf(this.c), Arrays.toString(Arrays.copyOf(jArr, 200))});
    }

    /* access modifiers changed from: protected */
    public int u(Object obj) {
        return ((long[]) obj).length;
    }

    /* access modifiers changed from: protected */
    public Object[] z(int i) {
        return new long[i][];
    }
}
