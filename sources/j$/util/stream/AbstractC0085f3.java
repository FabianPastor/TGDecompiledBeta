package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.f3  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public abstract class AbstractCLASSNAMEf3 implements InterfaceCLASSNAMEj3 {
    protected final InterfaceCLASSNAMEm3 a;

    public AbstractCLASSNAMEf3(InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        interfaceCLASSNAMEm3.getClass();
        this.a = interfaceCLASSNAMEm3;
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

    @Override // j$.util.function.Consumer
    /* renamed from: b */
    public /* synthetic */ void accept(Double d) {
        AbstractCLASSNAMEo1.a(this, d);
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
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
