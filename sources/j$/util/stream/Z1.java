package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.CountedCompleter;

final class Z1 extends CountedCompleter {
    private Spliterator a;
    private final CLASSNAMEt5 b;
    private final CLASSNAMEi4 c;
    private long d;

    Z1(Z1 z1, Spliterator spliterator) {
        super(z1);
        this.a = spliterator;
        this.b = z1.b;
        this.d = z1.d;
        this.c = z1.c;
    }

    Z1(CLASSNAMEi4 i4Var, Spliterator spliterator, CLASSNAMEt5 t5Var) {
        super((CountedCompleter) null);
        this.b = t5Var;
        this.c = i4Var;
        this.a = spliterator;
        this.d = 0;
    }

    public void compute() {
        Spliterator trySplit;
        Spliterator spliterator = this.a;
        long estimateSize = spliterator.estimateSize();
        long j = this.d;
        if (j == 0) {
            j = CLASSNAMEk1.h(estimateSize);
            this.d = j;
        }
        boolean d2 = CLASSNAMEg6.SHORT_CIRCUIT.d(this.c.r0());
        boolean z = false;
        CLASSNAMEt5 t5Var = this.b;
        Z1 z1 = this;
        while (true) {
            if (d2 && t5Var.p()) {
                break;
            } else if (estimateSize <= j || (trySplit = spliterator.trySplit()) == null) {
                z1.c.m0(t5Var, spliterator);
            } else {
                Z1 z12 = new Z1(z1, trySplit);
                z1.addToPendingCount(1);
                if (z) {
                    spliterator = trySplit;
                } else {
                    Z1 z13 = z1;
                    z1 = z12;
                    z12 = z13;
                }
                z = !z;
                z1.fork();
                z1 = z12;
                estimateSize = spliterator.estimateSize();
            }
        }
        z1.c.m0(t5Var, spliterator);
        z1.a = null;
        z1.propagateCompletion();
    }
}
