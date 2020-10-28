package j$.util.stream;

import j$.util.C;
import j$.util.V;
import j$.util.function.Consumer;
import j$.util.function.f;
import j$.util.function.q;
import java.util.Arrays;
import java.util.Iterator;

class W5 extends CLASSNAMEc6 implements q {
    W5() {
    }

    W5(int i) {
        super(i);
    }

    /* renamed from: B */
    public C spliterator() {
        return new V5(this, 0, this.c, 0, this.b);
    }

    public void accept(double d) {
        A();
        int i = this.b;
        this.b = i + 1;
        ((double[]) this.e)[i] = d;
    }

    public Object c(int i) {
        return new double[i];
    }

    public void forEach(Consumer consumer) {
        if (consumer instanceof q) {
            h((q) consumer);
        } else if (!L6.a) {
            spliterator().forEachRemaining(consumer);
        } else {
            L6.a(getClass(), "{0} calling SpinedBuffer.OfDouble.forEach(Consumer)");
            throw null;
        }
    }

    public Iterator iterator() {
        return V.f(spliterator());
    }

    public q k(q qVar) {
        qVar.getClass();
        return new f(this, qVar);
    }

    /* access modifiers changed from: protected */
    public void t(Object obj, int i, int i2, Object obj2) {
        double[] dArr = (double[]) obj;
        q qVar = (q) obj2;
        while (i < i2) {
            qVar.accept(dArr[i]);
            i++;
        }
    }

    public String toString() {
        double[] dArr = (double[]) e();
        if (dArr.length < 200) {
            return String.format("%s[length=%d, chunks=%d]%s", new Object[]{getClass().getSimpleName(), Integer.valueOf(dArr.length), Integer.valueOf(this.c), Arrays.toString(dArr)});
        }
        return String.format("%s[length=%d, chunks=%d]%s...", new Object[]{getClass().getSimpleName(), Integer.valueOf(dArr.length), Integer.valueOf(this.c), Arrays.toString(Arrays.copyOf(dArr, 200))});
    }

    /* access modifiers changed from: protected */
    public int u(Object obj) {
        return ((double[]) obj).length;
    }

    /* access modifiers changed from: protected */
    public Object[] z(int i) {
        return new double[i][];
    }
}
