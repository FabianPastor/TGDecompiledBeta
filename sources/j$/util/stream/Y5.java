package j$.util.stream;

import j$.util.D;
import j$.util.V;
import j$.util.function.Consumer;
import j$.util.function.g;
import j$.util.function.u;
import java.util.Arrays;
import java.util.Iterator;

class Y5 extends CLASSNAMEc6 implements u {
    Y5() {
    }

    Y5(int i) {
        super(i);
    }

    /* renamed from: B */
    public D spliterator() {
        return new X5(this, 0, this.c, 0, this.b);
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
        if (consumer instanceof u) {
            h((u) consumer);
        } else if (!L6.a) {
            spliterator().forEachRemaining(consumer);
        } else {
            L6.a(getClass(), "{0} calling SpinedBuffer.OfInt.forEach(Consumer)");
            throw null;
        }
    }

    public Iterator iterator() {
        return V.g(spliterator());
    }

    public u l(u uVar) {
        uVar.getClass();
        return new g(this, uVar);
    }

    /* access modifiers changed from: protected */
    public void t(Object obj, int i, int i2, Object obj2) {
        int[] iArr = (int[]) obj;
        u uVar = (u) obj2;
        while (i < i2) {
            uVar.accept(iArr[i]);
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
