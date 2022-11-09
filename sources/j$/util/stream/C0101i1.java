package j$.util.stream;
/* renamed from: j$.util.stream.i1  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
class CLASSNAMEi1 extends AbstractCLASSNAMEj1 implements InterfaceCLASSNAMEj3 {
    final /* synthetic */ EnumCLASSNAMEk1 c;
    final /* synthetic */ j$.wrappers.E d;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CLASSNAMEi1(EnumCLASSNAMEk1 enumCLASSNAMEk1, j$.wrappers.E e) {
        super(enumCLASSNAMEk1);
        this.c = enumCLASSNAMEk1;
        this.d = e;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEj1, j$.util.stream.InterfaceCLASSNAMEm3
    public void accept(double d) {
        boolean z;
        boolean z2;
        if (!this.a) {
            boolean b = this.d.b(d);
            z = this.c.a;
            if (b != z) {
                return;
            }
            this.a = true;
            z2 = this.c.b;
            this.b = z2;
        }
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
}
