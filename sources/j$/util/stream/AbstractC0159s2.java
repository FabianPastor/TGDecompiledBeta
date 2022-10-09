package j$.util.stream;

import j$.util.function.Consumer;
import java.util.concurrent.CountedCompleter;
/* renamed from: j$.util.stream.s2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
abstract class AbstractCLASSNAMEs2 extends CountedCompleter implements InterfaceCLASSNAMEm3 {
    protected final j$.util.u a;
    protected final AbstractCLASSNAMEy2 b;
    protected final long c;
    protected long d;
    protected long e;
    protected int f;
    protected int g;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCLASSNAMEs2(AbstractCLASSNAMEs2 abstractCLASSNAMEs2, j$.util.u uVar, long j, long j2, int i) {
        super(abstractCLASSNAMEs2);
        this.a = uVar;
        this.b = abstractCLASSNAMEs2.b;
        this.c = abstractCLASSNAMEs2.c;
        this.d = j;
        this.e = j2;
        if (j < 0 || j2 < 0 || (j + j2) - 1 >= i) {
            throw new IllegalArgumentException(String.format("offset and length interval [%d, %d + %d) is not within array size interval [0, %d)", Long.valueOf(j), Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(i)));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCLASSNAMEs2(j$.util.u uVar, AbstractCLASSNAMEy2 abstractCLASSNAMEy2, int i) {
        this.a = uVar;
        this.b = abstractCLASSNAMEy2;
        this.c = AbstractCLASSNAMEf.h(uVar.estimateSize());
        this.d = 0L;
        this.e = i;
    }

    public /* synthetic */ void accept(double d) {
        AbstractCLASSNAMEo1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        AbstractCLASSNAMEo1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        AbstractCLASSNAMEo1.e(this);
        throw null;
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    abstract AbstractCLASSNAMEs2 b(j$.util.u uVar, long j, long j2);

    @Override // java.util.concurrent.CountedCompleter
    public void compute() {
        j$.util.u mo322trySplit;
        j$.util.u uVar = this.a;
        AbstractCLASSNAMEs2 abstractCLASSNAMEs2 = this;
        while (uVar.estimateSize() > abstractCLASSNAMEs2.c && (mo322trySplit = uVar.mo322trySplit()) != null) {
            abstractCLASSNAMEs2.setPendingCount(1);
            long estimateSize = mo322trySplit.estimateSize();
            abstractCLASSNAMEs2.b(mo322trySplit, abstractCLASSNAMEs2.d, estimateSize).fork();
            abstractCLASSNAMEs2 = abstractCLASSNAMEs2.b(uVar, abstractCLASSNAMEs2.d + estimateSize, abstractCLASSNAMEs2.e - estimateSize);
        }
        AbstractCLASSNAMEc abstractCLASSNAMEc = (AbstractCLASSNAMEc) abstractCLASSNAMEs2.b;
        abstractCLASSNAMEc.getClass();
        abstractCLASSNAMEc.n0(abstractCLASSNAMEc.v0(abstractCLASSNAMEs2), uVar);
        abstractCLASSNAMEs2.propagateCompletion();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ void m() {
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
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

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public /* synthetic */ boolean o() {
        return false;
    }
}
