package j$.util.stream;

import j$.util.Spliterator;
import j$.util.stream.CLASSNAMEk1;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;

/* renamed from: j$.util.stream.k1  reason: case insensitive filesystem */
abstract class CLASSNAMEk1<P_IN, P_OUT, R, K extends CLASSNAMEk1<P_IN, P_OUT, R, K>> extends CountedCompleter<R> {
    static final int a = (ForkJoinPool.getCommonPoolParallelism() << 2);
    protected final T1 b;
    protected Spliterator c;
    protected long d;
    protected CLASSNAMEk1 e;
    protected CLASSNAMEk1 f;
    private Object g;

    protected CLASSNAMEk1(T1 t1, Spliterator spliterator) {
        super((CountedCompleter) null);
        this.b = t1;
        this.c = spliterator;
        this.d = 0;
    }

    protected CLASSNAMEk1(CLASSNAMEk1 k1Var, Spliterator spliterator) {
        super(k1Var);
        this.c = spliterator;
        this.b = k1Var.b;
        this.d = k1Var.d;
    }

    public static long h(long j) {
        long j2 = j / ((long) a);
        if (j2 > 0) {
            return j2;
        }
        return 1;
    }

    /* access modifiers changed from: protected */
    public abstract Object a();

    /* access modifiers changed from: protected */
    public Object b() {
        return this.g;
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEk1 c() {
        return (CLASSNAMEk1) getCompleter();
    }

    public void compute() {
        Spliterator trySplit;
        Spliterator spliterator = this.c;
        long estimateSize = spliterator.estimateSize();
        long j = this.d;
        if (j == 0) {
            j = h(estimateSize);
            this.d = j;
        }
        boolean z = false;
        CLASSNAMEk1 k1Var = this;
        while (estimateSize > j && (trySplit = spliterator.trySplit()) != null) {
            CLASSNAMEk1 f2 = k1Var.f(trySplit);
            k1Var.e = f2;
            CLASSNAMEk1 f3 = k1Var.f(spliterator);
            k1Var.f = f3;
            k1Var.setPendingCount(1);
            if (z) {
                spliterator = trySplit;
                k1Var = f2;
                f2 = f3;
            } else {
                k1Var = f3;
            }
            z = !z;
            f2.fork();
            estimateSize = spliterator.estimateSize();
        }
        k1Var.g(k1Var.a());
        k1Var.tryComplete();
    }

    /* access modifiers changed from: protected */
    public boolean d() {
        return this.e == null;
    }

    /* access modifiers changed from: protected */
    public boolean e() {
        return c() == null;
    }

    /* access modifiers changed from: protected */
    public abstract CLASSNAMEk1 f(Spliterator spliterator);

    /* access modifiers changed from: protected */
    public void g(Object obj) {
        this.g = obj;
    }

    public Object getRawResult() {
        return this.g;
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        this.c = null;
        this.f = null;
        this.e = null;
    }

    /* access modifiers changed from: protected */
    public void setRawResult(Object obj) {
        if (obj != null) {
            throw new IllegalStateException();
        }
    }
}
