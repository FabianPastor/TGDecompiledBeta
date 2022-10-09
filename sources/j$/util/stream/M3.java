package j$.util.stream;

import j$.util.Comparator$CC;
import java.util.Arrays;
import java.util.Comparator;
/* loaded from: classes2.dex */
final class M3 extends AbstractCLASSNAMEc3 {
    private final boolean l;
    private final Comparator m;

    /* JADX INFO: Access modifiers changed from: package-private */
    public M3(AbstractCLASSNAMEc abstractCLASSNAMEc) {
        super(abstractCLASSNAMEc, EnumCLASSNAMEe4.REFERENCE, EnumCLASSNAMEd4.q | EnumCLASSNAMEd4.o);
        this.l = true;
        this.m = Comparator$CC.a();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public M3(AbstractCLASSNAMEc abstractCLASSNAMEc, Comparator comparator) {
        super(abstractCLASSNAMEc, EnumCLASSNAMEe4.REFERENCE, EnumCLASSNAMEd4.q | EnumCLASSNAMEd4.p);
        this.l = false;
        comparator.getClass();
        this.m = comparator;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    public A1 E0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, j$.util.function.m mVar) {
        if (!EnumCLASSNAMEd4.SORTED.d(abstractCLASSNAMEy2.s0()) || !this.l) {
            Object[] q = abstractCLASSNAMEy2.p0(uVar, true, mVar).q(mVar);
            Arrays.sort(q, this.m);
            return new D1(q);
        }
        return abstractCLASSNAMEy2.p0(uVar, false, mVar);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    public InterfaceCLASSNAMEm3 H0(int i, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        interfaceCLASSNAMEm3.getClass();
        return (!EnumCLASSNAMEd4.SORTED.d(i) || !this.l) ? EnumCLASSNAMEd4.SIZED.d(i) ? new R3(interfaceCLASSNAMEm3, this.m) : new N3(interfaceCLASSNAMEm3, this.m) : interfaceCLASSNAMEm3;
    }
}
