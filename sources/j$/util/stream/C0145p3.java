package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.p3  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public class CLASSNAMEp3 extends AbstractCLASSNAMEc3 {
    final /* synthetic */ long l;
    final /* synthetic */ long m;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CLASSNAMEp3(AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i, long j, long j2) {
        super(abstractCLASSNAMEc, enumCLASSNAMEe4, i);
        this.l = j;
        this.m = j2;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    A1 E0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, j$.util.function.m mVar) {
        long q0 = abstractCLASSNAMEy2.q0(uVar);
        if (q0 > 0 && uVar.hasCharacteristics(16384)) {
            return AbstractCLASSNAMEx2.e(abstractCLASSNAMEy2, B3.b(abstractCLASSNAMEy2.r0(), uVar, this.l, this.m), true, mVar);
        }
        return !EnumCLASSNAMEd4.ORDERED.d(abstractCLASSNAMEy2.s0()) ? AbstractCLASSNAMEx2.e(this, L0(abstractCLASSNAMEy2.w0(uVar), this.l, this.m, q0), true, mVar) : (A1) new A3(this, abstractCLASSNAMEy2, uVar, mVar, this.l, this.m).invoke();
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    j$.util.u F0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar) {
        long d;
        long q0 = abstractCLASSNAMEy2.q0(uVar);
        if (q0 > 0 && uVar.hasCharacteristics(16384)) {
            j$.util.u w0 = abstractCLASSNAMEy2.w0(uVar);
            long j = this.l;
            d = B3.d(j, this.m);
            return new C4(w0, j, d);
        }
        return !EnumCLASSNAMEd4.ORDERED.d(abstractCLASSNAMEy2.s0()) ? L0(abstractCLASSNAMEy2.w0(uVar), this.l, this.m, q0) : ((A1) new A3(this, abstractCLASSNAMEy2, uVar, CLASSNAMEn3.a, this.l, this.m).invoke()).moNUMspliterator();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEc
    public InterfaceCLASSNAMEm3 H0(int i, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        return new CLASSNAMEo3(this, interfaceCLASSNAMEm3);
    }

    j$.util.u L0(j$.util.u uVar, long j, long j2, long j3) {
        long j4;
        long j5;
        if (j <= j3) {
            long j6 = j3 - j;
            j5 = j2 >= 0 ? Math.min(j2, j6) : j6;
            j4 = 0;
        } else {
            j4 = j;
            j5 = j2;
        }
        return new I4(uVar, j4, j5);
    }
}
