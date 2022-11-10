package j$.util.stream;

import j$.util.function.Consumer;
import java.util.Arrays;
import java.util.Iterator;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class Y3 extends Z3 implements j$.util.function.q {
    /* JADX INFO: Access modifiers changed from: package-private */
    public Y3() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Y3(int i) {
        super(i);
    }

    @Override // j$.util.stream.Z3, java.lang.Iterable, j$.lang.e
    /* renamed from: B */
    public j$.util.v moNUMspliterator() {
        return new X3(this, 0, this.c, 0, this.b);
    }

    @Override // j$.util.function.q
    public void accept(long j) {
        A();
        int i = this.b;
        this.b = i + 1;
        ((long[]) this.e)[i] = j;
    }

    @Override // j$.util.stream.Z3
    public Object c(int i) {
        return new long[i];
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }

    @Override // j$.lang.e
    public void forEach(Consumer consumer) {
        if (consumer instanceof j$.util.function.q) {
            g((j$.util.function.q) consumer);
        } else if (!Q4.a) {
            moNUMspliterator().forEachRemaining(consumer);
        } else {
            Q4.a(getClass(), "{0} calling SpinedBuffer.OfLong.forEach(Consumer)");
            throw null;
        }
    }

    @Override // java.lang.Iterable
    public Iterator iterator() {
        return j$.util.L.h(moNUMspliterator());
    }

    @Override // j$.util.stream.Z3
    protected void t(Object obj, int i, int i2, Object obj2) {
        long[] jArr = (long[]) obj;
        j$.util.function.q qVar = (j$.util.function.q) obj2;
        while (i < i2) {
            qVar.accept(jArr[i]);
            i++;
        }
    }

    public String toString() {
        long[] jArr = (long[]) e();
        return jArr.length < 200 ? String.format("%s[length=%d, chunks=%d]%s", getClass().getSimpleName(), Integer.valueOf(jArr.length), Integer.valueOf(this.c), Arrays.toString(jArr)) : String.format("%s[length=%d, chunks=%d]%s...", getClass().getSimpleName(), Integer.valueOf(jArr.length), Integer.valueOf(this.c), Arrays.toString(Arrays.copyOf(jArr, 200)));
    }

    @Override // j$.util.stream.Z3
    protected int u(Object obj) {
        return ((long[]) obj).length;
    }

    @Override // j$.util.stream.Z3
    protected Object[] z(int i) {
        return new long[i];
    }
}
