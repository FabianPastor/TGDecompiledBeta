package j$.util.stream;

import java.util.Arrays;
/* loaded from: classes2.dex */
final class J3 extends Q {
    /* JADX INFO: Access modifiers changed from: package-private */
    public J3(AbstractCLASSNAMEc abstractCLASSNAMEc) {
        super(abstractCLASSNAMEc, EnumCLASSNAMEe4.DOUBLE_VALUE, EnumCLASSNAMEd4.q | EnumCLASSNAMEd4.o);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    public A1 E0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, j$.util.function.m mVar) {
        if (EnumCLASSNAMEd4.SORTED.d(abstractCLASSNAMEy2.s0())) {
            return abstractCLASSNAMEy2.p0(uVar, false, mVar);
        }
        double[] dArr = (double[]) ((InterfaceCLASSNAMEu1) abstractCLASSNAMEy2.p0(uVar, true, mVar)).e();
        Arrays.sort(dArr);
        return new T1(dArr);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    public InterfaceCLASSNAMEm3 H0(int i, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        interfaceCLASSNAMEm3.getClass();
        return EnumCLASSNAMEd4.SORTED.d(i) ? interfaceCLASSNAMEm3 : EnumCLASSNAMEd4.SIZED.d(i) ? new O3(interfaceCLASSNAMEm3) : new G3(interfaceCLASSNAMEm3);
    }
}
