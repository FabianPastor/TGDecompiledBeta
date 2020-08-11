package j$.util.stream;

import java.util.concurrent.CountedCompleter;

/* renamed from: j$.util.stream.o4  reason: case insensitive filesystem */
abstract class CLASSNAMEo4 extends CountedCompleter {
    protected final CLASSNAMEt3 a;
    protected final int b;

    /* access modifiers changed from: package-private */
    public abstract void a();

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEo4 b(int i, int i2);

    CLASSNAMEo4(CLASSNAMEt3 node, int offset) {
        this.a = node;
        this.b = offset;
    }

    CLASSNAMEo4(CLASSNAMEo4 parent, CLASSNAMEt3 node, int offset) {
        super(parent);
        this.a = node;
        this.b = offset;
    }

    public void compute() {
        CLASSNAMEo4 o4Var = this;
        while (o4Var.a.w() != 0) {
            o4Var.setPendingCount(o4Var.a.w() - 1);
            int size = 0;
            int i = 0;
            while (i < o4Var.a.w() - 1) {
                K leftTask = o4Var.b(i, o4Var.b + size);
                size = (int) (((long) size) + leftTask.a.count());
                leftTask.fork();
                i++;
            }
            o4Var = o4Var.b(i, o4Var.b + size);
        }
        o4Var.a();
        o4Var.propagateCompletion();
    }
}
