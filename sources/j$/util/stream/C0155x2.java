package j$.util.stream;

import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.x2  reason: case insensitive filesystem */
abstract class CLASSNAMEx2 extends CountedCompleter {
    protected final B1 a;
    protected final int b;

    CLASSNAMEx2(B1 b1, int i) {
        this.a = b1;
        this.b = i;
    }

    CLASSNAMEx2(CLASSNAMEx2 x2Var, B1 b1, int i) {
        super(x2Var);
        this.a = b1;
        this.b = i;
    }

    /* access modifiers changed from: package-private */
    public abstract void a();

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEx2 b(int i, int i2);

    public void compute() {
        CLASSNAMEx2 x2Var = this;
        while (x2Var.a.p() != 0) {
            x2Var.setPendingCount(x2Var.a.p() - 1);
            int i = 0;
            int i2 = 0;
            while (i < x2Var.a.p() - 1) {
                CLASSNAMEx2 b2 = x2Var.b(i, x2Var.b + i2);
                i2 = (int) (((long) i2) + b2.a.count());
                b2.fork();
                i++;
            }
            x2Var = x2Var.b(i, x2Var.b + i2);
        }
        x2Var.a();
        x2Var.propagateCompletion();
    }
}
