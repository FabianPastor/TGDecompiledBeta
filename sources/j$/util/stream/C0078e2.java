package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.u;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.e2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final class CLASSNAMEe2 extends W3 implements InterfaceCLASSNAMEw1, InterfaceCLASSNAMEq1 {
    @Override // j$.util.stream.W3
    public u.a B() {
        return super.moNUMspliterator();
    }

    @Override // j$.util.function.Consumer
    /* renamed from: C */
    public /* synthetic */ void accept(Integer num) {
        AbstractCLASSNAMEo1.b(this, num);
    }

    @Override // j$.util.stream.A1
    /* renamed from: D */
    public /* synthetic */ void i(Integer[] numArr, int i) {
        AbstractCLASSNAMEo1.i(this, numArr, i);
    }

    @Override // j$.util.stream.A1
    /* renamed from: E */
    public /* synthetic */ InterfaceCLASSNAMEw1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.o(this, j, j2, mVar);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEq1, j$.util.stream.InterfaceCLASSNAMEs1
    /* renamed from: a */
    public A1 moNUMa() {
        return this;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEq1, j$.util.stream.InterfaceCLASSNAMEs1
    /* renamed from: a  reason: collision with other method in class */
    public InterfaceCLASSNAMEw1 moNUMa() {
        return this;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void accept(double d) {
        AbstractCLASSNAMEo1.f(this);
        throw null;
    }

    @Override // j$.util.stream.W3, j$.util.function.l
    public void accept(int i) {
        super.accept(i);
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

    @Override // j$.util.stream.InterfaceCLASSNAMEz1, j$.util.stream.A1
    /* renamed from: b  reason: collision with other method in class */
    public InterfaceCLASSNAMEz1 moNUMb(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.Z3, j$.util.stream.InterfaceCLASSNAMEz1
    public void d(Object obj, int i) {
        super.d((int[]) obj, i);
    }

    @Override // j$.util.stream.Z3, j$.util.stream.InterfaceCLASSNAMEz1
    public Object e() {
        return (int[]) super.e();
    }

    @Override // j$.util.stream.Z3, j$.util.stream.InterfaceCLASSNAMEz1
    public void g(Object obj) {
        super.g((j$.util.function.l) obj);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void m() {
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void n(long j) {
        clear();
        x(j);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ boolean o() {
        return false;
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ int p() {
        return 0;
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ Object[] q(j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.g(this, mVar);
    }

    @Override // j$.util.stream.W3, j$.util.stream.Z3, java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator  reason: collision with other method in class */
    public j$.util.w moNUMspliterator() {
        return super.moNUMspliterator();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1, j$.util.stream.A1
    /* renamed from: b */
    public /* bridge */ /* synthetic */ A1 moNUMb(int i) {
        moNUMb(i);
        throw null;
    }

    @Override // j$.util.stream.W3, j$.util.stream.Z3, java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator */
    public j$.util.u moNUMspliterator() {
        return super.moNUMspliterator();
    }
}
