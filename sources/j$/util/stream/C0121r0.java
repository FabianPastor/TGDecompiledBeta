package j$.util.stream;

import j$.util.y;
import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.r0  reason: case insensitive filesystem */
final class CLASSNAMEr0 extends CountedCompleter {
    private y a;
    private final CLASSNAMEn3 b;
    private final CLASSNAMEz2 c;
    private long d;

    CLASSNAMEr0(CLASSNAMEr0 r0Var, y yVar) {
        super(r0Var);
        this.a = yVar;
        this.b = r0Var.b;
        this.d = r0Var.d;
        this.c = r0Var.c;
    }

    CLASSNAMEr0(CLASSNAMEz2 z2Var, y yVar, CLASSNAMEn3 n3Var) {
        super((CountedCompleter) null);
        this.b = n3Var;
        this.c = z2Var;
        this.a = yVar;
        this.d = 0;
    }

    public void compute() {
        y trySplit;
        y yVar = this.a;
        long estimateSize = yVar.estimateSize();
        long j = this.d;
        if (j == 0) {
            j = CLASSNAMEf.h(estimateSize);
            this.d = j;
        }
        boolean d2 = CLASSNAMEe4.SHORT_CIRCUIT.d(this.c.s0());
        boolean z = false;
        CLASSNAMEn3 n3Var = this.b;
        CLASSNAMEr0 r0Var = this;
        while (true) {
            if (d2 && n3Var.o()) {
                break;
            } else if (estimateSize <= j || (trySplit = yVar.trySplit()) == null) {
                r0Var.c.n0(n3Var, yVar);
            } else {
                CLASSNAMEr0 r0Var2 = new CLASSNAMEr0(r0Var, trySplit);
                r0Var.addToPendingCount(1);
                if (z) {
                    yVar = trySplit;
                } else {
                    CLASSNAMEr0 r0Var3 = r0Var;
                    r0Var = r0Var2;
                    r0Var2 = r0Var3;
                }
                z = !z;
                r0Var.fork();
                r0Var = r0Var2;
                estimateSize = yVar.estimateSize();
            }
        }
        r0Var.c.n0(n3Var, yVar);
        r0Var.a = null;
        r0Var.propagateCompletion();
    }
}
