package j$.util.stream;

import j$.util.N;
import j$.util.function.Consumer;
import j$.util.function.e;
import j$.util.function.f;
import j$.util.u;
import java.util.Arrays;
import java.util.Iterator;

class V3 extends CLASSNAMEa4 implements f {
    V3() {
    }

    V3(int i) {
        super(i);
    }

    /* renamed from: B */
    public u spliterator() {
        return new U3(this, 0, this.c, 0, this.b);
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
        if (consumer instanceof f) {
            g((f) consumer);
        } else if (!R4.a) {
            spliterator().forEachRemaining(consumer);
        } else {
            R4.a(getClass(), "{0} calling SpinedBuffer.OfDouble.forEach(Consumer)");
            throw null;
        }
    }

    public Iterator iterator() {
        return N.f(spliterator());
    }

    public f j(f fVar) {
        fVar.getClass();
        return new e(this, fVar);
    }

    /* access modifiers changed from: protected */
    public void t(Object obj, int i, int i2, Object obj2) {
        double[] dArr = (double[]) obj;
        f fVar = (f) obj2;
        while (i < i2) {
            fVar.accept(dArr[i]);
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
