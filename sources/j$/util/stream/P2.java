package j$.util.stream;
/* loaded from: classes2.dex */
class P2 extends U2 {
    final /* synthetic */ j$.util.function.o b;
    final /* synthetic */ long c;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public P2(EnumCLASSNAMEe4 enumCLASSNAMEe4, j$.util.function.o oVar, long j) {
        super(enumCLASSNAMEe4);
        this.b = oVar;
        this.c = j;
    }

    @Override // j$.util.stream.U2
    public S2 a() {
        return new Q2(this.c, this.b);
    }
}
