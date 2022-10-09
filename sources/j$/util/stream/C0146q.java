package j$.util.stream;
/* renamed from: j$.util.stream.q  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
class CLASSNAMEq extends AbstractCLASSNAMEi3 {
    boolean b;
    Object c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEq(CLASSNAMEs CLASSNAMEs, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        if (obj == null) {
            if (this.b) {
                return;
            }
            this.b = true;
            InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3 = this.a;
            this.c = null;
            interfaceCLASSNAMEm3.accept((InterfaceCLASSNAMEm3) null);
            return;
        }
        Object obj2 = this.c;
        if (obj2 != null && obj.equals(obj2)) {
            return;
        }
        InterfaceCLASSNAMEm3 interfaceCLASSNAMEm32 = this.a;
        this.c = obj;
        interfaceCLASSNAMEm32.accept((InterfaceCLASSNAMEm3) obj);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEi3, j$.util.stream.InterfaceCLASSNAMEm3
    public void m() {
        this.b = false;
        this.c = null;
        this.a.m();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void n(long j) {
        this.b = false;
        this.c = null;
        this.a.n(-1L);
    }
}
