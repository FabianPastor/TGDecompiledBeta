package j$.util.stream;

import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;
/* renamed from: j$.util.stream.f  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
abstract class AbstractCLASSNAMEf extends CountedCompleter {
    static final int g = ForkJoinPool.getCommonPoolParallelism() << 2;
    protected final AbstractCLASSNAMEy2 a;
    protected j$.util.u b;
    protected long c;
    protected AbstractCLASSNAMEf d;
    protected AbstractCLASSNAMEf e;
    private Object f;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractCLASSNAMEf(AbstractCLASSNAMEf abstractCLASSNAMEf, j$.util.u uVar) {
        super(abstractCLASSNAMEf);
        this.b = uVar;
        this.a = abstractCLASSNAMEf.a;
        this.c = abstractCLASSNAMEf.c;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractCLASSNAMEf(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar) {
        super(null);
        this.a = abstractCLASSNAMEy2;
        this.b = uVar;
        this.c = 0L;
    }

    public static long h(long j) {
        long j2 = j / g;
        if (j2 > 0) {
            return j2;
        }
        return 1L;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract Object a();

    /* JADX INFO: Access modifiers changed from: protected */
    public Object b() {
        return this.f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractCLASSNAMEf c() {
        return (AbstractCLASSNAMEf) getCompleter();
    }

    @Override // java.util.concurrent.CountedCompleter
    public void compute() {
        j$.util.u mo322trySplit;
        j$.util.u uVar = this.b;
        long estimateSize = uVar.estimateSize();
        long j = this.c;
        if (j == 0) {
            j = h(estimateSize);
            this.c = j;
        }
        boolean z = false;
        AbstractCLASSNAMEf abstractCLASSNAMEf = this;
        while (estimateSize > j && (mo322trySplit = uVar.mo322trySplit()) != null) {
            AbstractCLASSNAMEf f = abstractCLASSNAMEf.f(mo322trySplit);
            abstractCLASSNAMEf.d = f;
            AbstractCLASSNAMEf f2 = abstractCLASSNAMEf.f(uVar);
            abstractCLASSNAMEf.e = f2;
            abstractCLASSNAMEf.setPendingCount(1);
            if (z) {
                uVar = mo322trySplit;
                abstractCLASSNAMEf = f;
                f = f2;
            } else {
                abstractCLASSNAMEf = f2;
            }
            z = !z;
            f.fork();
            estimateSize = uVar.estimateSize();
        }
        abstractCLASSNAMEf.g(abstractCLASSNAMEf.a());
        abstractCLASSNAMEf.tryComplete();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean d() {
        return this.d == null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean e() {
        return c() == null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract AbstractCLASSNAMEf f(j$.util.u uVar);

    /* JADX INFO: Access modifiers changed from: protected */
    public void g(Object obj) {
        this.f = obj;
    }

    @Override // java.util.concurrent.CountedCompleter, java.util.concurrent.ForkJoinTask
    public Object getRawResult() {
        return this.f;
    }

    @Override // java.util.concurrent.CountedCompleter
    public void onCompletion(CountedCompleter countedCompleter) {
        this.b = null;
        this.e = null;
        this.d = null;
    }

    @Override // java.util.concurrent.CountedCompleter, java.util.concurrent.ForkJoinTask
    protected void setRawResult(Object obj) {
        if (obj == null) {
            return;
        }
        throw new IllegalStateException();
    }
}
