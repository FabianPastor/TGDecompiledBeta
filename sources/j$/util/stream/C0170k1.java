package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;

/* renamed from: j$.util.stream.k1  reason: case insensitive filesystem */
abstract class CLASSNAMEk1 extends CountedCompleter {
    static final int g = (ForkJoinPool.getCommonPoolParallelism() << 2);
    protected final CLASSNAMEq4 a;
    protected Spliterator b;
    protected long c;
    protected CLASSNAMEk1 d;
    protected CLASSNAMEk1 e;
    private Object f;

    /* access modifiers changed from: protected */
    public abstract Object a();

    /* access modifiers changed from: protected */
    public abstract CLASSNAMEk1 h(Spliterator spliterator);

    protected CLASSNAMEk1(CLASSNAMEq4 helper, Spliterator spliterator) {
        super((CountedCompleter) null);
        this.a = helper;
        this.b = spliterator;
        this.c = 0;
    }

    protected CLASSNAMEk1(CLASSNAMEk1 parent, Spliterator spliterator) {
        super(parent);
        this.b = spliterator;
        this.a = parent.a;
        this.c = parent.c;
    }

    public static long j(long sizeEstimate) {
        long est = sizeEstimate / ((long) g);
        if (est > 0) {
            return est;
        }
        return 1;
    }

    /* access modifiers changed from: protected */
    public final long d(long sizeEstimate) {
        long j = this.c;
        long s = j;
        if (j != 0) {
            return s;
        }
        long j2 = j(sizeEstimate);
        this.c = j2;
        return j2;
    }

    public Object getRawResult() {
        return this.f;
    }

    /* access modifiers changed from: protected */
    public void setRawResult(Object result) {
        if (result != null) {
            throw new IllegalStateException();
        }
    }

    /* access modifiers changed from: protected */
    public Object b() {
        return this.f;
    }

    /* access modifiers changed from: protected */
    public void i(Object localResult) {
        this.f = localResult;
    }

    /* access modifiers changed from: protected */
    public boolean e() {
        return this.d == null;
    }

    /* access modifiers changed from: protected */
    public boolean g() {
        return c() == null;
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEk1 c() {
        return (CLASSNAMEk1) getCompleter();
    }

    public void compute() {
        K taskToFork;
        Spliterator spliterator = this.b;
        long sizeEstimate = spliterator.estimateSize();
        long sizeThreshold = d(sizeEstimate);
        boolean forkRight = false;
        K task = this;
        while (sizeEstimate > sizeThreshold) {
            Spliterator trySplit = spliterator.trySplit();
            Spliterator spliterator2 = trySplit;
            if (trySplit == null) {
                break;
            }
            K h = task.h(spliterator2);
            K leftChild = h;
            task.d = h;
            K h2 = task.h(spliterator);
            K rightChild = h2;
            task.e = h2;
            task.setPendingCount(1);
            if (forkRight) {
                forkRight = false;
                spliterator = spliterator2;
                task = leftChild;
                taskToFork = rightChild;
            } else {
                forkRight = true;
                task = rightChild;
                taskToFork = leftChild;
            }
            taskToFork.fork();
            sizeEstimate = spliterator.estimateSize();
        }
        task.i(task.a());
        task.tryComplete();
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        this.b = null;
        this.e = null;
        this.d = null;
    }

    /* access modifiers changed from: protected */
    public boolean f() {
        K node = this;
        while (node != null) {
            K parent = node.c();
            if (parent != null && parent.d != node) {
                return false;
            }
            node = parent;
        }
        return true;
    }
}
