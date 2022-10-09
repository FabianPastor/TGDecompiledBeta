package j$.util.stream;
/* loaded from: classes2.dex */
public final /* synthetic */ class F implements j$.util.function.f {
    public final /* synthetic */ int a = 1;
    public final /* synthetic */ Object b;

    @Override // j$.util.function.f
    public final void accept(double d) {
        switch (this.a) {
            case 0:
                ((InterfaceCLASSNAMEm3) this.b).accept(d);
                return;
            default:
                ((J) this.b).a.accept(d);
                return;
        }
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        switch (this.a) {
            case 0:
                fVar.getClass();
                return new j$.util.function.e(this, fVar);
            default:
                fVar.getClass();
                return new j$.util.function.e(this, fVar);
        }
    }
}
