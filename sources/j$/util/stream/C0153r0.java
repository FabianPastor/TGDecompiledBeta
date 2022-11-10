package j$.util.stream;

import java.util.concurrent.CountedCompleter;
/* renamed from: j$.util.stream.r0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
final class CLASSNAMEr0 extends CountedCompleter {
    private j$.util.u a;
    private final InterfaceCLASSNAMEm3 b;
    private final AbstractCLASSNAMEy2 c;
    private long d;

    CLASSNAMEr0(CLASSNAMEr0 CLASSNAMEr0, j$.util.u uVar) {
        super(CLASSNAMEr0);
        this.a = uVar;
        this.b = CLASSNAMEr0.b;
        this.d = CLASSNAMEr0.d;
        this.c = CLASSNAMEr0.c;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEr0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(null);
        this.b = interfaceCLASSNAMEm3;
        this.c = abstractCLASSNAMEy2;
        this.a = uVar;
        this.d = 0L;
    }

    @Override // java.util.concurrent.CountedCompleter
    public void compute() {
        j$.util.u moNUMtrySplit;
        j$.util.u uVar = this.a;
        long estimateSize = uVar.estimateSize();
        long j = this.d;
        if (j == 0) {
            j = AbstractCLASSNAMEf.h(estimateSize);
            this.d = j;
        }
        boolean d = EnumCLASSNAMEd4.SHORT_CIRCUIT.d(this.c.s0());
        boolean z = false;
        InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3 = this.b;
        CLASSNAMEr0 CLASSNAMEr0 = this;
        while (true) {
            if (d && interfaceCLASSNAMEm3.o()) {
                break;
            } else if (estimateSize <= j || (moNUMtrySplit = uVar.moNUMtrySplit()) == null) {
                break;
            } else {
                CLASSNAMEr0 CLASSNAMEr02 = new CLASSNAMEr0(CLASSNAMEr0, moNUMtrySplit);
                CLASSNAMEr0.addToPendingCount(1);
                if (z) {
                    uVar = moNUMtrySplit;
                } else {
                    CLASSNAMEr0 CLASSNAMEr03 = CLASSNAMEr0;
                    CLASSNAMEr0 = CLASSNAMEr02;
                    CLASSNAMEr02 = CLASSNAMEr03;
                }
                z = !z;
                CLASSNAMEr0.fork();
                CLASSNAMEr0 = CLASSNAMEr02;
                estimateSize = uVar.estimateSize();
            }
        }
        CLASSNAMEr0.c.n0(interfaceCLASSNAMEm3, uVar);
        CLASSNAMEr0.a = null;
        CLASSNAMEr0.propagateCompletion();
    }
}
