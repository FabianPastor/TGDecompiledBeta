package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.o0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
abstract class AbstractCLASSNAMEo0 implements N4, O4 {
    private final boolean a;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractCLASSNAMEo0(boolean z) {
        this.a = z;
    }

    public /* synthetic */ void accept(double d) {
        AbstractCLASSNAMEo1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        AbstractCLASSNAMEo1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        AbstractCLASSNAMEo1.e(this);
        throw null;
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    @Override // j$.util.stream.N4
    public int b() {
        if (this.a) {
            return 0;
        }
        return EnumCLASSNAMEd4.r;
    }

    @Override // j$.util.stream.N4
    public Object c(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar) {
        (this.a ? new CLASSNAMEq0(abstractCLASSNAMEy2, uVar, this) : new CLASSNAMEr0(abstractCLASSNAMEy2, uVar, abstractCLASSNAMEy2.v0(this))).invoke();
        return null;
    }

    @Override // j$.util.stream.N4
    public Object d(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar) {
        AbstractCLASSNAMEc abstractCLASSNAMEc = (AbstractCLASSNAMEc) abstractCLASSNAMEy2;
        abstractCLASSNAMEc.n0(abstractCLASSNAMEc.v0(this), uVar);
        return null;
    }

    @Override // j$.util.function.y
    public /* bridge */ /* synthetic */ Object get() {
        return null;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void m() {
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void n(long j) {
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ boolean o() {
        return false;
    }
}
