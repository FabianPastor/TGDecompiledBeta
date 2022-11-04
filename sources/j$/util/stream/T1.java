package j$.util.stream;

import j$.util.function.Consumer;
import java.util.Arrays;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class T1 implements InterfaceCLASSNAMEu1 {
    final double[] a;
    int b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public T1(long j) {
        if (j < NUM) {
            this.a = new double[(int) j];
            this.b = 0;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public T1(double[] dArr) {
        this.a = dArr;
        this.b = dArr.length;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1, j$.util.stream.A1
    /* renamed from: b  reason: collision with other method in class */
    public InterfaceCLASSNAMEz1 mo292b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.A1
    public long count() {
        return this.b;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1
    public void d(Object obj, int i) {
        System.arraycopy(this.a, 0, (double[]) obj, i, this.b);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1
    public Object e() {
        double[] dArr = this.a;
        int length = dArr.length;
        int i = this.b;
        return length == i ? dArr : Arrays.copyOf(dArr, i);
    }

    @Override // j$.util.stream.A1
    /* renamed from: f */
    public /* synthetic */ void i(Double[] dArr, int i) {
        AbstractCLASSNAMEo1.h(this, dArr, i);
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractCLASSNAMEo1.k(this, consumer);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1
    public void g(Object obj) {
        j$.util.function.f fVar = (j$.util.function.f) obj;
        for (int i = 0; i < this.b; i++) {
            fVar.accept(this.a[i]);
        }
    }

    @Override // j$.util.stream.A1
    /* renamed from: k */
    public /* synthetic */ InterfaceCLASSNAMEu1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.n(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ int p() {
        return 0;
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ Object[] q(j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.g(this, mVar);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1, j$.util.stream.A1
    /* renamed from: spliterator  reason: collision with other method in class */
    public j$.util.w mo289spliterator() {
        return j$.util.L.j(this.a, 0, this.b, 1040);
    }

    public String toString() {
        return String.format("DoubleArrayNode[%d][%s]", Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1, j$.util.stream.A1
    /* renamed from: b */
    public /* bridge */ /* synthetic */ A1 mo292b(int i) {
        mo292b(i);
        throw null;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1, j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo289spliterator() {
        return j$.util.L.j(this.a, 0, this.b, 1040);
    }
}
