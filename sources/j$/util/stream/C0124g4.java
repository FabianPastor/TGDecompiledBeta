package j$.util.stream;

import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.g4  reason: case insensitive filesystem */
abstract class CLASSNAMEg4 extends CountedCompleter {
    protected final CLASSNAMEl3 a;
    protected final int b;

    CLASSNAMEg4(CLASSNAMEg4 g4Var, CLASSNAMEl3 l3Var, int i) {
        super(g4Var);
        this.a = l3Var;
        this.b = i;
    }

    CLASSNAMEg4(CLASSNAMEl3 l3Var, int i) {
        this.a = l3Var;
        this.b = i;
    }

    /* access modifiers changed from: package-private */
    public abstract void a();

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEg4 b(int i, int i2);

    public void compute() {
        CLASSNAMEg4 g4Var = this;
        while (g4Var.a.o() != 0) {
            g4Var.setPendingCount(g4Var.a.o() - 1);
            int i = 0;
            int i2 = 0;
            while (i < g4Var.a.o() - 1) {
                CLASSNAMEg4 b2 = g4Var.b(i, g4Var.b + i2);
                i2 = (int) (((long) i2) + b2.a.count());
                b2.fork();
                i++;
            }
            g4Var = g4Var.b(i, g4Var.b + i2);
        }
        g4Var.a();
        g4Var.propagateCompletion();
    }
}
