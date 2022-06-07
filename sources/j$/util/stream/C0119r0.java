package j$.util.stream;

import j$.util.u;
import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.r0  reason: case insensitive filesystem */
final class CLASSNAMEr0 extends CountedCompleter {
    private u a;
    private final CLASSNAMEm3 b;
    private final CLASSNAMEy2 c;
    private long d;

    CLASSNAMEr0(CLASSNAMEr0 r0Var, u uVar) {
        super(r0Var);
        this.a = uVar;
        this.b = r0Var.b;
        this.d = r0Var.d;
        this.c = r0Var.c;
    }

    CLASSNAMEr0(CLASSNAMEy2 y2Var, u uVar, CLASSNAMEm3 m3Var) {
        super((CountedCompleter) null);
        this.b = m3Var;
        this.c = y2Var;
        this.a = uVar;
        this.d = 0;
    }

    public void compute() {
        u trySplit;
        u uVar = this.a;
        long estimateSize = uVar.estimateSize();
        long j = this.d;
        if (j == 0) {
            j = CLASSNAMEf.h(estimateSize);
            this.d = j;
        }
        boolean d2 = CLASSNAMEd4.SHORT_CIRCUIT.d(this.c.s0());
        boolean z = false;
        CLASSNAMEm3 m3Var = this.b;
        CLASSNAMEr0 r0Var = this;
        while (true) {
            if (d2 && m3Var.o()) {
                break;
            } else if (estimateSize <= j || (trySplit = uVar.trySplit()) == null) {
                r0Var.c.n0(m3Var, uVar);
            } else {
                CLASSNAMEr0 r0Var2 = new CLASSNAMEr0(r0Var, trySplit);
                r0Var.addToPendingCount(1);
                if (z) {
                    uVar = trySplit;
                } else {
                    CLASSNAMEr0 r0Var3 = r0Var;
                    r0Var = r0Var2;
                    r0Var2 = r0Var3;
                }
                z = !z;
                r0Var.fork();
                r0Var = r0Var2;
                estimateSize = uVar.estimateSize();
            }
        }
        r0Var.c.n0(m3Var, uVar);
        r0Var.a = null;
        r0Var.propagateCompletion();
    }
}
