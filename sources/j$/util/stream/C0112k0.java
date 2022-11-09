package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.k0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final class CLASSNAMEk0 extends AbstractCLASSNAMEo0 implements InterfaceCLASSNAMEj3 {
    final j$.util.function.f b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEk0(j$.util.function.f fVar, boolean z) {
        super(z);
        this.b = fVar;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEo0, j$.util.stream.InterfaceCLASSNAMEm3
    public void accept(double d) {
        this.b.accept(d);
    }

    @Override // j$.util.function.Consumer
    /* renamed from: e */
    public /* synthetic */ void accept(Double d) {
        AbstractCLASSNAMEo1.a(this, d);
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }
}
