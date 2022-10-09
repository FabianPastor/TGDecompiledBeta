package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.l0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final class CLASSNAMEl0 extends AbstractCLASSNAMEo0 implements InterfaceCLASSNAMEk3 {
    final j$.util.function.l b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEl0(j$.util.function.l lVar, boolean z) {
        super(z);
        this.b = lVar;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEo0, j$.util.stream.InterfaceCLASSNAMEm3
    public void accept(int i) {
        this.b.accept(i);
    }

    @Override // j$.util.function.Consumer
    /* renamed from: e */
    public /* synthetic */ void accept(Integer num) {
        AbstractCLASSNAMEo1.b(this, num);
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }
}
