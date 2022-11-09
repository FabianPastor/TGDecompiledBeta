package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.h3  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public abstract class AbstractCLASSNAMEh3 implements InterfaceCLASSNAMEl3 {
    protected final InterfaceCLASSNAMEm3 a;

    public AbstractCLASSNAMEh3(InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        interfaceCLASSNAMEm3.getClass();
        this.a = interfaceCLASSNAMEm3;
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

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    @Override // j$.util.function.Consumer
    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        AbstractCLASSNAMEo1.c(this, l);
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
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
