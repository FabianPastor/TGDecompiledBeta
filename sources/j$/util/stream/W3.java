package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.u;
import java.util.Arrays;
import java.util.Iterator;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class W3 extends Z3 implements j$.util.function.l {
    /* JADX INFO: Access modifiers changed from: package-private */
    public W3() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public W3(int i) {
        super(i);
    }

    @Override // j$.util.stream.Z3, java.lang.Iterable, j$.lang.e
    /* renamed from: B */
    public u.a mo289spliterator() {
        return new V3(this, 0, this.c, 0, this.b);
    }

    @Override // j$.util.function.l
    public void accept(int i) {
        A();
        int i2 = this.b;
        this.b = i2 + 1;
        ((int[]) this.e)[i2] = i;
    }

    @Override // j$.util.stream.Z3
    public Object c(int i) {
        return new int[i];
    }

    @Override // j$.lang.e
    public void forEach(Consumer consumer) {
        if (consumer instanceof j$.util.function.l) {
            g((j$.util.function.l) consumer);
        } else if (!Q4.a) {
            mo289spliterator().forEachRemaining(consumer);
        } else {
            Q4.a(getClass(), "{0} calling SpinedBuffer.OfInt.forEach(Consumer)");
            throw null;
        }
    }

    @Override // java.lang.Iterable
    public Iterator iterator() {
        return j$.util.L.g(mo289spliterator());
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }

    @Override // j$.util.stream.Z3
    protected void t(Object obj, int i, int i2, Object obj2) {
        int[] iArr = (int[]) obj;
        j$.util.function.l lVar = (j$.util.function.l) obj2;
        while (i < i2) {
            lVar.accept(iArr[i]);
            i++;
        }
    }

    public String toString() {
        int[] iArr = (int[]) e();
        return iArr.length < 200 ? String.format("%s[length=%d, chunks=%d]%s", getClass().getSimpleName(), Integer.valueOf(iArr.length), Integer.valueOf(this.c), Arrays.toString(iArr)) : String.format("%s[length=%d, chunks=%d]%s...", getClass().getSimpleName(), Integer.valueOf(iArr.length), Integer.valueOf(this.c), Arrays.toString(Arrays.copyOf(iArr, 200)));
    }

    @Override // j$.util.stream.Z3
    protected int u(Object obj) {
        return ((int[]) obj).length;
    }

    @Override // j$.util.stream.Z3
    protected Object[] z(int i) {
        return new int[i];
    }
}
