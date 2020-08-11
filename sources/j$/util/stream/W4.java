package j$.util.stream;

import j$.util.function.J;

class W4 extends CLASSNAMEz5 {
    J b;
    final /* synthetic */ X4 c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    W4(X4 this$1, G5 downstream) {
        super(downstream);
        this.c = this$1;
        G5 g5 = this.a;
        g5.getClass();
        this.b = new CLASSNAMEf1(g5);
    }

    public void s(long size) {
        this.a.s(-1);
    }

    public void accept(Object u) {
        W2 result = (W2) this.c.m.apply(u);
        if (result != null) {
            try {
                result.sequential().i(this.b);
            } catch (Throwable th) {
            }
        }
        if (result != null) {
            result.close();
            return;
        }
        return;
        throw th;
    }
}
