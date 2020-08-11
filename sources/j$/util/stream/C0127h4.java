package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.h4  reason: case insensitive filesystem */
abstract class CLASSNAMEh4 extends CountedCompleter implements G5 {
    protected final Spliterator a;
    protected final CLASSNAMEq4 b;
    protected final long c;
    protected long d;
    protected long e;
    protected int f;
    protected int g;

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEh4 a(Spliterator spliterator, long j, long j2);

    public /* synthetic */ void accept(double d2) {
        CLASSNAMEv5.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEv5.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEv5.b(this);
        throw null;
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    public /* synthetic */ void r() {
        CLASSNAMEv5.f();
    }

    public /* synthetic */ boolean u() {
        CLASSNAMEv5.e();
        return false;
    }

    CLASSNAMEh4(Spliterator spliterator, CLASSNAMEq4 helper, int arrayLength) {
        this.a = spliterator;
        this.b = helper;
        this.c = CLASSNAMEk1.j(spliterator.estimateSize());
        this.d = 0;
        this.e = (long) arrayLength;
    }

    CLASSNAMEh4(CLASSNAMEh4 parent, Spliterator spliterator, long offset, long length, int arrayLength) {
        super(parent);
        this.a = spliterator;
        this.b = parent.b;
        this.c = parent.c;
        this.d = offset;
        this.e = length;
        if (offset < 0 || length < 0 || (offset + length) - 1 >= ((long) arrayLength)) {
            throw new IllegalArgumentException(String.format("offset and length interval [%d, %d + %d) is not within array size interval [0, %d)", new Object[]{Long.valueOf(offset), Long.valueOf(offset), Long.valueOf(length), Integer.valueOf(arrayLength)}));
        }
    }

    public void compute() {
        CLASSNAMEh4 h4Var = this;
        Spliterator spliterator = this.a;
        while (spliterator.estimateSize() > h4Var.c) {
            Spliterator trySplit = spliterator.trySplit();
            Spliterator spliterator2 = trySplit;
            if (trySplit == null) {
                break;
            }
            h4Var.setPendingCount(1);
            long leftSplitSize = spliterator2.estimateSize();
            h4Var.a(spliterator2, h4Var.d, leftSplitSize).fork();
            h4Var = h4Var.a(spliterator, h4Var.d + leftSplitSize, h4Var.e - leftSplitSize);
        }
        h4Var.b.t0(h4Var, spliterator);
        h4Var.propagateCompletion();
    }

    public void s(long size) {
        long j = this.e;
        if (size <= j) {
            int i = (int) this.d;
            this.f = i;
            this.g = i + ((int) j);
            return;
        }
        throw new IllegalStateException("size passed to Sink.begin exceeds array length");
    }
}
