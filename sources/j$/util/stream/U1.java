package j$.util.stream;

import j$.util.function.Consumer;
import java.util.Arrays;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class U1 extends T1 implements InterfaceCLASSNAMEp1 {
    /* JADX INFO: Access modifiers changed from: package-private */
    public U1(long j) {
        super(j);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEp1, j$.util.stream.InterfaceCLASSNAMEs1
    /* renamed from: a */
    public /* bridge */ /* synthetic */ A1 moNUMa() {
        moNUMa();
        return this;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEp1, j$.util.stream.InterfaceCLASSNAMEs1
    /* renamed from: a  reason: collision with other method in class */
    public InterfaceCLASSNAMEu1 moNUMa() {
        if (this.b >= this.a.length) {
            return this;
        }
        throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", Integer.valueOf(this.b), Integer.valueOf(this.a.length)));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void accept(double d) {
        int i = this.b;
        double[] dArr = this.a;
        if (i < dArr.length) {
            this.b = i + 1;
            dArr[i] = d;
            return;
        }
        throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", Integer.valueOf(this.a.length)));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void accept(int i) {
        AbstractCLASSNAMEo1.d(this);
        throw null;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3, j$.util.stream.InterfaceCLASSNAMEl3, j$.util.function.q
    public /* synthetic */ void accept(long j) {
        AbstractCLASSNAMEo1.e(this);
        throw null;
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }

    @Override // j$.util.function.Consumer
    /* renamed from: l */
    public /* synthetic */ void accept(Double d) {
        AbstractCLASSNAMEo1.a(this, d);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void m() {
        if (this.b >= this.a.length) {
            return;
        }
        throw new IllegalStateException(String.format("End size %d is less than fixed size %d", Integer.valueOf(this.b), Integer.valueOf(this.a.length)));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void n(long j) {
        if (j == this.a.length) {
            this.b = 0;
            return;
        }
        throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d", Long.valueOf(j), Integer.valueOf(this.a.length)));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ boolean o() {
        return false;
    }

    @Override // j$.util.stream.T1
    public String toString() {
        return String.format("DoubleFixedNodeBuilder[%d][%s]", Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a));
    }
}
