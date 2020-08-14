package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.stream.Sink;

/* renamed from: j$.util.stream.b2  reason: case insensitive filesystem */
final class CLASSNAMEb2 extends CountedCompleter {
    private Spliterator a;
    private final G5 b;
    private final CLASSNAMEq4 c;
    private long d;

    CLASSNAMEb2(CLASSNAMEq4 helper, Spliterator spliterator, G5 sink) {
        super((CountedCompleter) null);
        this.b = sink;
        this.c = helper;
        this.a = spliterator;
        this.d = 0;
    }

    CLASSNAMEb2(CLASSNAMEb2 parent, Spliterator spliterator) {
        super(parent);
        this.a = spliterator;
        this.b = parent.b;
        this.d = parent.d;
        this.c = parent.c;
    }

    public void compute() {
        CLASSNAMEb2 b2Var;
        Spliterator spliterator = this.a;
        long sizeEstimate = spliterator.estimateSize();
        long j = this.d;
        long sizeThreshold = j;
        if (j == 0) {
            long j2 = CLASSNAMEk1.j(sizeEstimate);
            sizeThreshold = j2;
            this.d = j2;
        }
        boolean isShortCircuit = CLASSNAMEu6.SHORT_CIRCUIT.f(this.c.r0());
        boolean forkRight = false;
        Sink<S> taskSink = this.b;
        CLASSNAMEb2 b2Var2 = this;
        while (true) {
            if (!isShortCircuit || !taskSink.u()) {
                if (sizeEstimate <= sizeThreshold) {
                    break;
                }
                Spliterator trySplit = spliterator.trySplit();
                Spliterator spliterator2 = trySplit;
                if (trySplit == null) {
                    break;
                }
                CLASSNAMEb2 b2Var3 = new CLASSNAMEb2(b2Var2, spliterator2);
                b2Var2.addToPendingCount(1);
                if (forkRight) {
                    forkRight = false;
                    spliterator = spliterator2;
                    b2Var = b2Var2;
                    b2Var2 = b2Var3;
                } else {
                    forkRight = true;
                    b2Var = b2Var3;
                }
                b2Var.fork();
                sizeEstimate = spliterator.estimateSize();
            } else {
                break;
            }
        }
        b2Var2.c.c(taskSink, spliterator);
        b2Var2.a = null;
        b2Var2.propagateCompletion();
    }
}
