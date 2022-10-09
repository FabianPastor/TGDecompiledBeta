package j$.util.stream;

import j$.util.function.Consumer;
import java.util.Arrays;
import java.util.Iterator;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class U3 extends Z3 implements j$.util.function.f {
    /* JADX INFO: Access modifiers changed from: package-private */
    public U3() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public U3(int i) {
        super(i);
    }

    @Override // j$.util.stream.Z3, java.lang.Iterable, j$.lang.e
    /* renamed from: B */
    public j$.util.t mo289spliterator() {
        return new T3(this, 0, this.c, 0, this.b);
    }

    @Override // j$.util.function.f
    public void accept(double d) {
        A();
        int i = this.b;
        this.b = i + 1;
        ((double[]) this.e)[i] = d;
    }

    @Override // j$.util.stream.Z3
    public Object c(int i) {
        return new double[i];
    }

    @Override // j$.lang.e
    public void forEach(Consumer consumer) {
        if (consumer instanceof j$.util.function.f) {
            g((j$.util.function.f) consumer);
        } else if (!Q4.a) {
            mo289spliterator().forEachRemaining(consumer);
        } else {
            Q4.a(getClass(), "{0} calling SpinedBuffer.OfDouble.forEach(Consumer)");
            throw null;
        }
    }

    @Override // java.lang.Iterable
    public Iterator iterator() {
        return j$.util.L.f(mo289spliterator());
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }

    @Override // j$.util.stream.Z3
    protected void t(Object obj, int i, int i2, Object obj2) {
        double[] dArr = (double[]) obj;
        j$.util.function.f fVar = (j$.util.function.f) obj2;
        while (i < i2) {
            fVar.accept(dArr[i]);
            i++;
        }
    }

    public String toString() {
        double[] dArr = (double[]) e();
        return dArr.length < 200 ? String.format("%s[length=%d, chunks=%d]%s", getClass().getSimpleName(), Integer.valueOf(dArr.length), Integer.valueOf(this.c), Arrays.toString(dArr)) : String.format("%s[length=%d, chunks=%d]%s...", getClass().getSimpleName(), Integer.valueOf(dArr.length), Integer.valueOf(this.c), Arrays.toString(Arrays.copyOf(dArr, 200)));
    }

    @Override // j$.util.stream.Z3
    protected int u(Object obj) {
        return ((double[]) obj).length;
    }

    @Override // j$.util.stream.Z3
    protected Object[] z(int i) {
        return new double[i];
    }
}
