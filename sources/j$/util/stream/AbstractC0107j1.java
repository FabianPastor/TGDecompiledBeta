package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.j1  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
abstract class AbstractCLASSNAMEj1 implements InterfaceCLASSNAMEm3 {
    boolean a;
    boolean b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCLASSNAMEj1(EnumCLASSNAMEk1 enumCLASSNAMEk1) {
        boolean z;
        z = enumCLASSNAMEk1.b;
        this.b = !z;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void accept(double d) {
        AbstractCLASSNAMEo1.f(this);
        throw null;
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

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void m() {
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void n(long j) {
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public boolean o() {
        return this.a;
    }
}
