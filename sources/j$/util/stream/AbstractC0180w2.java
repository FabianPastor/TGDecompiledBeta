package j$.util.stream;

import java.util.concurrent.CountedCompleter;
/* renamed from: j$.util.stream.w2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
abstract class AbstractCLASSNAMEw2 extends CountedCompleter {
    protected final A1 a;
    protected final int b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCLASSNAMEw2(A1 a1, int i) {
        this.a = a1;
        this.b = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCLASSNAMEw2(AbstractCLASSNAMEw2 abstractCLASSNAMEw2, A1 a1, int i) {
        super(abstractCLASSNAMEw2);
        this.a = a1;
        this.b = i;
    }

    abstract void a();

    abstract AbstractCLASSNAMEw2 b(int i, int i2);

    @Override // java.util.concurrent.CountedCompleter
    public void compute() {
        AbstractCLASSNAMEw2 abstractCLASSNAMEw2 = this;
        while (abstractCLASSNAMEw2.a.p() != 0) {
            abstractCLASSNAMEw2.setPendingCount(abstractCLASSNAMEw2.a.p() - 1);
            int i = 0;
            int i2 = 0;
            while (i < abstractCLASSNAMEw2.a.p() - 1) {
                AbstractCLASSNAMEw2 b = abstractCLASSNAMEw2.b(i, abstractCLASSNAMEw2.b + i2);
                i2 = (int) (i2 + b.a.count());
                b.fork();
                i++;
            }
            abstractCLASSNAMEw2 = abstractCLASSNAMEw2.b(i, abstractCLASSNAMEw2.b + i2);
        }
        abstractCLASSNAMEw2.a();
        abstractCLASSNAMEw2.propagateCompletion();
    }
}
