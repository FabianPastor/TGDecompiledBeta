package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.g3  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public abstract class AbstractCLASSNAMEg3 implements InterfaceCLASSNAMEk3 {
    protected final InterfaceCLASSNAMEm3 a;

    public AbstractCLASSNAMEg3(InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        interfaceCLASSNAMEm3.getClass();
        this.a = interfaceCLASSNAMEm3;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void accept(double d) {
        AbstractCLASSNAMEo1.f(this);
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

    @Override // j$.util.function.Consumer
    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        AbstractCLASSNAMEo1.b(this, num);
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void m() {
        this.a.m();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public boolean o() {
        return this.a.o();
    }
}
