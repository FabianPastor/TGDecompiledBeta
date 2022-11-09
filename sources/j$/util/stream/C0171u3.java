package j$.util.stream;
/* renamed from: j$.util.stream.u3  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
class CLASSNAMEu3 extends AbstractCLASSNAMEh3 {
    long b;
    long c;
    final /* synthetic */ CLASSNAMEv3 d;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CLASSNAMEu3(CLASSNAMEv3 CLASSNAMEv3, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.d = CLASSNAMEv3;
        this.b = CLASSNAMEv3.l;
        long j = CLASSNAMEv3.m;
        this.c = j < 0 ? Long.MAX_VALUE : j;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEl3, j$.util.function.q
    public void accept(long j) {
        long j2 = this.b;
        if (j2 != 0) {
            this.b = j2 - 1;
            return;
        }
        long j3 = this.c;
        if (j3 <= 0) {
            return;
        }
        this.c = j3 - 1;
        this.a.accept(j);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void n(long j) {
        this.a.n(B3.c(j, this.d.l, this.c));
    }

    @Override // j$.util.stream.AbstractCLASSNAMEh3, j$.util.stream.InterfaceCLASSNAMEm3
    public boolean o() {
        return this.c == 0 || this.a.o();
    }
}
