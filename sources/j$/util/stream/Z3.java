package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.Spliterator;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import java.util.concurrent.CountedCompleter;

abstract class Z3 extends CountedCompleter implements CLASSNAMEt5 {
    protected final Spliterator a;
    protected final CLASSNAMEi4 b;
    protected final long c;
    protected long d;
    protected long e;
    protected int f;
    protected int g;

    Z3(Spliterator spliterator, CLASSNAMEi4 i4Var, int i) {
        this.a = spliterator;
        this.b = i4Var;
        this.c = CLASSNAMEk1.h(spliterator.estimateSize());
        this.d = 0;
        this.e = (long) i;
    }

    Z3(Z3 z3, Spliterator spliterator, long j, long j2, int i) {
        super(z3);
        this.a = spliterator;
        this.b = z3.b;
        this.c = z3.c;
        this.d = j;
        this.e = j2;
        if (j < 0 || j2 < 0 || (j + j2) - 1 >= ((long) i)) {
            throw new IllegalArgumentException(String.format("offset and length interval [%d, %d + %d) is not within array size interval [0, %d)", new Object[]{Long.valueOf(j), Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(i)}));
        }
    }

    public /* synthetic */ void accept(double d2) {
        CLASSNAMEk.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEk.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEk.b(this);
        throw null;
    }

    /* access modifiers changed from: package-private */
    public abstract Z3 b(Spliterator spliterator, long j, long j2);

    public void compute() {
        Spliterator trySplit;
        Spliterator spliterator = this.a;
        Z3 z3 = this;
        while (spliterator.estimateSize() > z3.c && (trySplit = spliterator.trySplit()) != null) {
            z3.setPendingCount(1);
            long estimateSize = trySplit.estimateSize();
            z3.b(trySplit, z3.d, estimateSize).fork();
            z3 = z3.b(spliterator, z3.d + estimateSize, z3.e - estimateSize);
        }
        CLASSNAMEh1 h1Var = (CLASSNAMEh1) z3.b;
        h1Var.getClass();
        h1Var.m0(h1Var.u0(z3), spliterator);
        z3.propagateCompletion();
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void m() {
    }

    public void n(long j) {
        long j2 = this.e;
        if (j <= j2) {
            int i = (int) this.d;
            this.f = i;
            this.g = i + ((int) j2);
            return;
        }
        throw new IllegalStateException("size passed to Sink.begin exceeds array length");
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
