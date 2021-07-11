package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.y1  reason: case insensitive filesystem */
final class CLASSNAMEy1<S, T> extends CountedCompleter<Void> {
    private Spliterator a;
    private final A2 b;
    private final T1 c;
    private long d;

    CLASSNAMEy1(T1 t1, Spliterator spliterator, A2 a2) {
        super((CountedCompleter) null);
        this.b = a2;
        this.c = t1;
        this.a = spliterator;
        this.d = 0;
    }

    CLASSNAMEy1(CLASSNAMEy1 y1Var, Spliterator spliterator) {
        super(y1Var);
        this.a = spliterator;
        this.b = y1Var.b;
        this.d = y1Var.d;
        this.c = y1Var.c;
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
        boolean d2 = T2.SHORT_CIRCUIT.d(this.c.r0());
        boolean z = false;
        A2 a2 = this.b;
        CLASSNAMEy1 y1Var = this;
        while (true) {
            if (d2 && a2.o()) {
                break;
            } else if (estimateSize <= j || (trySplit = spliterator.trySplit()) == null) {
                y1Var.c.m0(a2, spliterator);
            } else {
                CLASSNAMEy1 y1Var2 = new CLASSNAMEy1(y1Var, trySplit);
                y1Var.addToPendingCount(1);
                if (z) {
                    spliterator = trySplit;
                } else {
                    CLASSNAMEy1 y1Var3 = y1Var;
                    y1Var = y1Var2;
                    y1Var2 = y1Var3;
                }
                z = !z;
                y1Var.fork();
                y1Var = y1Var2;
                estimateSize = spliterator.estimateSize();
            }
        }
        y1Var.c.m0(a2, spliterator);
        y1Var.a = null;
        y1Var.propagateCompletion();
    }
}
