package j$.util.stream;

import j$.util.u;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;

/* renamed from: j$.util.stream.f  reason: case insensitive filesystem */
abstract class CLASSNAMEf extends CountedCompleter {
    static final int g = (ForkJoinPool.getCommonPoolParallelism() << 2);
    protected final CLASSNAMEy2 a;
    protected u b;
    protected long c;
    protected CLASSNAMEf d;
    protected CLASSNAMEf e;
    private Object f;

    protected CLASSNAMEf(CLASSNAMEf fVar, u uVar) {
        super(fVar);
        this.b = uVar;
        this.a = fVar.a;
        this.c = fVar.c;
    }

    protected CLASSNAMEf(CLASSNAMEy2 y2Var, u uVar) {
        super((CountedCompleter) null);
        this.a = y2Var;
        this.b = uVar;
        this.c = 0;
    }

    public static long h(long j) {
        long j2 = j / ((long) g);
        if (j2 > 0) {
            return j2;
        }
        return 1;
    }

    /* access modifiers changed from: protected */
    public abstract Object a();

    /* access modifiers changed from: protected */
    public Object b() {
        return this.f;
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEf c() {
        return (CLASSNAMEf) getCompleter();
    }

    public void compute() {
        u trySplit;
        u uVar = this.b;
        long estimateSize = uVar.estimateSize();
        long j = this.c;
        if (j == 0) {
            j = h(estimateSize);
            this.c = j;
        }
        boolean z = false;
        CLASSNAMEf fVar = this;
        while (estimateSize > j && (trySplit = uVar.trySplit()) != null) {
            CLASSNAMEf f2 = fVar.f(trySplit);
            fVar.d = f2;
            CLASSNAMEf f3 = fVar.f(uVar);
            fVar.e = f3;
            fVar.setPendingCount(1);
            if (z) {
                uVar = trySplit;
                fVar = f2;
                f2 = f3;
            } else {
                fVar = f3;
            }
            z = !z;
            f2.fork();
            estimateSize = uVar.estimateSize();
        }
        fVar.g(fVar.a());
        fVar.tryComplete();
    }

    /* access modifiers changed from: protected */
    public boolean d() {
        return this.d == null;
    }

    /* access modifiers changed from: protected */
    public boolean e() {
        return c() == null;
    }

    /* access modifiers changed from: protected */
    public abstract CLASSNAMEf f(u uVar);

    /* access modifiers changed from: protected */
    public void g(Object obj) {
        this.f = obj;
    }

    public Object getRawResult() {
        return this.f;
    }

    public void onCompletion(CountedCompleter countedCompleter) {
        this.b = null;
        this.e = null;
        this.d = null;
    }

    /* access modifiers changed from: protected */
    public void setRawResult(Object obj) {
        if (obj != null) {
            throw new IllegalStateException();
        }
    }
}
