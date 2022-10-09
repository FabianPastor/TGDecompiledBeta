package j$.util.stream;

import j$.util.function.Consumer;
import java.util.Arrays;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.c2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public class CLASSNAMEc2 implements InterfaceCLASSNAMEw1 {
    final int[] a;
    int b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEc2(long j) {
        if (j < NUM) {
            this.a = new int[(int) j];
            this.b = 0;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEc2(int[] iArr) {
        this.a = iArr;
        this.b = iArr.length;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1, j$.util.stream.A1
    /* renamed from: b  reason: collision with other method in class */
    public InterfaceCLASSNAMEz1 mo288b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.A1
    public long count() {
        return this.b;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1
    public void d(Object obj, int i) {
        System.arraycopy(this.a, 0, (int[]) obj, i, this.b);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1
    public Object e() {
        int[] iArr = this.a;
        int length = iArr.length;
        int i = this.b;
        return length == i ? iArr : Arrays.copyOf(iArr, i);
    }

    @Override // j$.util.stream.A1
    /* renamed from: f */
    public /* synthetic */ void i(Integer[] numArr, int i) {
        AbstractCLASSNAMEo1.i(this, numArr, i);
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractCLASSNAMEo1.l(this, consumer);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1
    public void g(Object obj) {
        j$.util.function.l lVar = (j$.util.function.l) obj;
        for (int i = 0; i < this.b; i++) {
            lVar.accept(this.a[i]);
        }
    }

    @Override // j$.util.stream.A1
    /* renamed from: j */
    public /* synthetic */ InterfaceCLASSNAMEw1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.o(this, j, j2, mVar);
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
    public j$.util.w mo285spliterator() {
        return j$.util.L.k(this.a, 0, this.b, 1040);
    }

    public String toString() {
        return String.format("IntArrayNode[%d][%s]", Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1, j$.util.stream.A1
    /* renamed from: b */
    public /* bridge */ /* synthetic */ A1 mo288b(int i) {
        mo288b(i);
        throw null;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1, j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo285spliterator() {
        return j$.util.L.k(this.a, 0, this.b, 1040);
    }
}
