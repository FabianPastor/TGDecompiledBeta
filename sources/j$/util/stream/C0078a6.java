package j$.util.stream;

import j$.util.E;
import j$.util.V;
import j$.util.function.Consumer;
import j$.util.function.h;
import j$.util.function.y;
import java.util.Arrays;
import java.util.Iterator;

/* renamed from: j$.util.stream.a6  reason: case insensitive filesystem */
class CLASSNAMEa6 extends CLASSNAMEc6 implements y {
    CLASSNAMEa6() {
    }

    CLASSNAMEa6(int i) {
        super(i);
    }

    /* renamed from: B */
    public E spliterator() {
        return new Z5(this, 0, this.c, 0, this.b);
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

    public void forEach(Consumer consumer) {
        if (consumer instanceof y) {
            h((y) consumer);
        } else if (!L6.a) {
            spliterator().forEachRemaining(consumer);
        } else {
            L6.a(getClass(), "{0} calling SpinedBuffer.OfLong.forEach(Consumer)");
            throw null;
        }
    }

    public y g(y yVar) {
        yVar.getClass();
        return new h(this, yVar);
    }

    public Iterator iterator() {
        return V.h(spliterator());
    }

    /* access modifiers changed from: protected */
    public void t(Object obj, int i, int i2, Object obj2) {
        long[] jArr = (long[]) obj;
        y yVar = (y) obj2;
        while (i < i2) {
            yVar.accept(jArr[i]);
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
