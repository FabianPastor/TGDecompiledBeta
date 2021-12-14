package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.y;
import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.t2  reason: case insensitive filesystem */
abstract class CLASSNAMEt2 extends CountedCompleter implements CLASSNAMEn3 {
    protected final y a;
    protected final CLASSNAMEz2 b;
    protected final long c;
    protected long d;
    protected long e;
    protected int f;
    protected int g;

    CLASSNAMEt2(CLASSNAMEt2 t2Var, y yVar, long j, long j2, int i) {
        super(t2Var);
        this.a = yVar;
        this.b = t2Var.b;
        this.c = t2Var.c;
        this.d = j;
        this.e = j2;
        if (j < 0 || j2 < 0 || (j + j2) - 1 >= ((long) i)) {
            throw new IllegalArgumentException(String.format("offset and length interval [%d, %d + %d) is not within array size interval [0, %d)", new Object[]{Long.valueOf(j), Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(i)}));
        }
    }

    CLASSNAMEt2(y yVar, CLASSNAMEz2 z2Var, int i) {
        this.a = yVar;
        this.b = z2Var;
        this.c = CLASSNAMEf.h(yVar.estimateSize());
        this.d = 0;
        this.e = (long) i;
    }

    public /* synthetic */ void accept(double d2) {
        CLASSNAMEp1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEp1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEp1.e(this);
        throw null;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEt2 b(y yVar, long j, long j2);

    public void compute() {
        y trySplit;
        y yVar = this.a;
        CLASSNAMEt2 t2Var = this;
        while (yVar.estimateSize() > t2Var.c && (trySplit = yVar.trySplit()) != null) {
            t2Var.setPendingCount(1);
            long estimateSize = trySplit.estimateSize();
            t2Var.b(trySplit, t2Var.d, estimateSize).fork();
            t2Var = t2Var.b(yVar, t2Var.d + estimateSize, t2Var.e - estimateSize);
        }
        CLASSNAMEc cVar = (CLASSNAMEc) t2Var.b;
        cVar.getClass();
        cVar.n0(cVar.v0(t2Var), yVar);
        t2Var.propagateCompletion();
    }

    public /* synthetic */ void m() {
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

    public /* synthetic */ boolean o() {
        return false;
    }
}
