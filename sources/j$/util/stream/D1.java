package j$.util.stream;

import j$.util.function.Consumer;
import java.util.Arrays;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class D1 implements A1 {
    final Object[] a;
    int b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public D1(long j, j$.util.function.m mVar) {
        if (j < NUM) {
            this.a = (Object[]) mVar.apply((int) j);
            this.b = 0;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public D1(Object[] objArr) {
        this.a = objArr;
        this.b = objArr.length;
    }

    @Override // j$.util.stream.A1
    /* renamed from: b */
    public A1 mo292b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.A1
    public long count() {
        return this.b;
    }

    @Override // j$.util.stream.A1
    public void forEach(Consumer consumer) {
        for (int i = 0; i < this.b; i++) {
            consumer.accept(this.a[i]);
        }
    }

    @Override // j$.util.stream.A1
    public void i(Object[] objArr, int i) {
        System.arraycopy(this.a, 0, objArr, i, this.b);
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ int p() {
        return 0;
    }

    @Override // j$.util.stream.A1
    public Object[] q(j$.util.function.m mVar) {
        Object[] objArr = this.a;
        if (objArr.length == this.b) {
            return objArr;
        }
        throw new IllegalStateException();
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ A1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.q(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo289spliterator() {
        return j$.util.L.m(this.a, 0, this.b, 1040);
    }

    public String toString() {
        return String.format("ArrayNode[%d][%s]", Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a));
    }
}
