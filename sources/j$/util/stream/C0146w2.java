package j$.util.stream;

import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.w2  reason: case insensitive filesystem */
abstract class CLASSNAMEw2 extends CountedCompleter {
    protected final A1 a;
    protected final int b;

    CLASSNAMEw2(A1 a1, int i) {
        this.a = a1;
        this.b = i;
    }

    CLASSNAMEw2(CLASSNAMEw2 w2Var, A1 a1, int i) {
        super(w2Var);
        this.a = a1;
        this.b = i;
    }

    /* access modifiers changed from: package-private */
    public abstract void a();

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEw2 b(int i, int i2);

    public void compute() {
        CLASSNAMEw2 w2Var = this;
        while (w2Var.a.p() != 0) {
            w2Var.setPendingCount(w2Var.a.p() - 1);
            int i = 0;
            int i2 = 0;
            while (i < w2Var.a.p() - 1) {
                CLASSNAMEw2 b2 = w2Var.b(i, w2Var.b + i2);
                i2 = (int) (((long) i2) + b2.a.count());
                b2.fork();
                i++;
            }
            w2Var = w2Var.b(i, w2Var.b + i2);
        }
        w2Var.a();
        w2Var.propagateCompletion();
    }
}
