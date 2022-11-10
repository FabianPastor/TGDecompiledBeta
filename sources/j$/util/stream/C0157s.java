package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
/* renamed from: j$.util.stream.s  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
class CLASSNAMEs extends AbstractCLASSNAMEc3 {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEs(AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i) {
        super(abstractCLASSNAMEc, enumCLASSNAMEe4, i);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    A1 E0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, j$.util.function.m mVar) {
        if (EnumCLASSNAMEd4.DISTINCT.d(abstractCLASSNAMEy2.s0())) {
            return abstractCLASSNAMEy2.p0(uVar, false, mVar);
        }
        if (EnumCLASSNAMEd4.ORDERED.d(abstractCLASSNAMEy2.s0())) {
            return L0(abstractCLASSNAMEy2, uVar);
        }
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        new CLASSNAMEn0(new CLASSNAMEo(atomicBoolean, concurrentHashMap), false).c(abstractCLASSNAMEy2, uVar);
        Collection keySet = concurrentHashMap.keySet();
        if (atomicBoolean.get()) {
            HashSet hashSet = new HashSet(keySet);
            hashSet.add(null);
            keySet = hashSet;
        }
        return new E1(keySet);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    j$.util.u F0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar) {
        return EnumCLASSNAMEd4.DISTINCT.d(abstractCLASSNAMEy2.s0()) ? abstractCLASSNAMEy2.w0(uVar) : EnumCLASSNAMEd4.ORDERED.d(abstractCLASSNAMEy2.s0()) ? ((E1) L0(abstractCLASSNAMEy2, uVar)).moNUMspliterator() : new CLASSNAMEm4(abstractCLASSNAMEy2.w0(uVar));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEc
    public InterfaceCLASSNAMEm3 H0(int i, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        interfaceCLASSNAMEm3.getClass();
        return EnumCLASSNAMEd4.DISTINCT.d(i) ? interfaceCLASSNAMEm3 : EnumCLASSNAMEd4.SORTED.d(i) ? new CLASSNAMEq(this, interfaceCLASSNAMEm3) : new r(this, interfaceCLASSNAMEm3);
    }

    A1 L0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar) {
        CLASSNAMEp CLASSNAMEp = CLASSNAMEp.a;
        CLASSNAMEm CLASSNAMEm = CLASSNAMEm.a;
        return new E1((Collection) new CLASSNAMEz2(EnumCLASSNAMEe4.REFERENCE, CLASSNAMEn.a, CLASSNAMEm, CLASSNAMEp).c(abstractCLASSNAMEy2, uVar));
    }
}
