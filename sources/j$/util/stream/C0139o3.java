package j$.util.stream;
/* renamed from: j$.util.stream.o3  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
class CLASSNAMEo3 extends AbstractCLASSNAMEi3 {
    long b;
    long c;
    final /* synthetic */ CLASSNAMEp3 d;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CLASSNAMEo3(CLASSNAMEp3 CLASSNAMEp3, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.d = CLASSNAMEp3;
        this.b = CLASSNAMEp3.l;
        long j = CLASSNAMEp3.m;
        this.c = j < 0 ? Long.MAX_VALUE : j;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        long j = this.b;
        if (j != 0) {
            this.b = j - 1;
            return;
        }
        long j2 = this.c;
        if (j2 <= 0) {
            return;
        }
        this.c = j2 - 1;
        this.a.accept((InterfaceCLASSNAMEm3) obj);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void n(long j) {
        this.a.n(B3.c(j, this.d.l, this.c));
    }

    @Override // j$.util.stream.AbstractCLASSNAMEi3, j$.util.stream.InterfaceCLASSNAMEm3
    public boolean o() {
        return this.c == 0 || this.a.o();
    }
}
