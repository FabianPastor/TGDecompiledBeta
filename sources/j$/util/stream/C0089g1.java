package j$.util.stream;
/* renamed from: j$.util.stream.g1  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
class CLASSNAMEg1 extends AbstractCLASSNAMEj1 implements InterfaceCLASSNAMEk3 {
    final /* synthetic */ EnumCLASSNAMEk1 c;
    final /* synthetic */ j$.wrappers.V d;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CLASSNAMEg1(EnumCLASSNAMEk1 enumCLASSNAMEk1, j$.wrappers.V v) {
        super(enumCLASSNAMEk1);
        this.c = enumCLASSNAMEk1;
        this.d = v;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEj1, j$.util.stream.InterfaceCLASSNAMEm3
    public void accept(int i) {
        boolean z;
        boolean z2;
        if (!this.a) {
            boolean b = this.d.b(i);
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
    public /* synthetic */ void accept(Integer num) {
        AbstractCLASSNAMEo1.b(this, num);
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }
}
