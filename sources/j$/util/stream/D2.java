package j$.util.stream;
/* loaded from: classes2.dex */
class D2 extends U2 {
    public final /* synthetic */ int b = 1;
    final /* synthetic */ Object c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public D2(EnumCLASSNAMEe4 enumCLASSNAMEe4, j$.util.function.b bVar) {
        super(enumCLASSNAMEe4);
        this.c = bVar;
    }

    @Override // j$.util.stream.U2
    public S2 a() {
        switch (this.b) {
            case 0:
                return new E2((j$.util.function.d) this.c);
            case 1:
                return new H2((j$.util.function.b) this.c);
            case 2:
                return new N2((j$.util.function.j) this.c);
            default:
                return new R2((j$.util.function.o) this.c);
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public D2(EnumCLASSNAMEe4 enumCLASSNAMEe4, j$.util.function.d dVar) {
        super(enumCLASSNAMEe4);
        this.c = dVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public D2(EnumCLASSNAMEe4 enumCLASSNAMEe4, j$.util.function.j jVar) {
        super(enumCLASSNAMEe4);
        this.c = jVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public D2(EnumCLASSNAMEe4 enumCLASSNAMEe4, j$.util.function.o oVar) {
        super(enumCLASSNAMEe4);
        this.c = oVar;
    }
}
