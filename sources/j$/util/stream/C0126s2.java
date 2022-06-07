package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.u;
import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.s2  reason: case insensitive filesystem */
abstract class CLASSNAMEs2 extends CountedCompleter implements CLASSNAMEm3 {
    protected final u a;
    protected final CLASSNAMEy2 b;
    protected final long c;
    protected long d;
    protected long e;
    protected int f;
    protected int g;

    CLASSNAMEs2(CLASSNAMEs2 s2Var, u uVar, long j, long j2, int i) {
        super(s2Var);
        this.a = uVar;
        this.b = s2Var.b;
        this.c = s2Var.c;
        this.d = j;
        this.e = j2;
        if (j < 0 || j2 < 0 || (j + j2) - 1 >= ((long) i)) {
            throw new IllegalArgumentException(String.format("offset and length interval [%d, %d + %d) is not within array size interval [0, %d)", new Object[]{Long.valueOf(j), Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(i)}));
        }
    }

    CLASSNAMEs2(u uVar, CLASSNAMEy2 y2Var, int i) {
        this.a = uVar;
        this.b = y2Var;
        this.c = CLASSNAMEf.h(uVar.estimateSize());
        this.d = 0;
        this.e = (long) i;
    }

    public /* synthetic */ void accept(double d2) {
        CLASSNAMEo1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEo1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEo1.e(this);
        throw null;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEs2 b(u uVar, long j, long j2);

    public void compute() {
        u trySplit;
        u uVar = this.a;
        CLASSNAMEs2 s2Var = this;
        while (uVar.estimateSize() > s2Var.c && (trySplit = uVar.trySplit()) != null) {
            s2Var.setPendingCount(1);
            long estimateSize = trySplit.estimateSize();
            s2Var.b(trySplit, s2Var.d, estimateSize).fork();
            s2Var = s2Var.b(uVar, s2Var.d + estimateSize, s2Var.e - estimateSize);
        }
        CLASSNAMEc cVar = (CLASSNAMEc) s2Var.b;
        cVar.getClass();
        cVar.n0(cVar.v0(s2Var), uVar);
        s2Var.propagateCompletion();
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
