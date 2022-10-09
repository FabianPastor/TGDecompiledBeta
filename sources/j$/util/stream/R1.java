package j$.util.stream;
/* loaded from: classes2.dex */
abstract class R1 extends C1 implements InterfaceCLASSNAMEz1 {
    /* JADX INFO: Access modifiers changed from: package-private */
    public R1(InterfaceCLASSNAMEz1 interfaceCLASSNAMEz1, InterfaceCLASSNAMEz1 interfaceCLASSNAMEz12) {
        super(interfaceCLASSNAMEz1, interfaceCLASSNAMEz12);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1
    public void d(Object obj, int i) {
        ((InterfaceCLASSNAMEz1) this.a).d(obj, i);
        ((InterfaceCLASSNAMEz1) this.b).d(obj, i + ((int) ((InterfaceCLASSNAMEz1) this.a).count()));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1
    public Object e() {
        long count = count();
        if (count < NUM) {
            Object c = c((int) count);
            d(c, 0);
            return c;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEz1
    public void g(Object obj) {
        ((InterfaceCLASSNAMEz1) this.a).g(obj);
        ((InterfaceCLASSNAMEz1) this.b).g(obj);
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ Object[] q(j$.util.function.m mVar) {
        return AbstractCLASSNAMEo1.g(this, mVar);
    }

    public String toString() {
        return count() < 32 ? String.format("%s[%s.%s]", getClass().getName(), this.a, this.b) : String.format("%s[size=%d]", getClass().getName(), Long.valueOf(count()));
    }
}
