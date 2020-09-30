package j$.util.stream;

import j$.util.function.CLASSNAMEt;

/* renamed from: j$.util.stream.p5  reason: case insensitive filesystem */
class CLASSNAMEp5 extends CLASSNAMEz5 {
    CLASSNAMEt b;
    final /* synthetic */ CLASSNAMEq5 c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEp5(CLASSNAMEq5 this$1, G5 downstream) {
        super(downstream);
        this.c = this$1;
        G5 g5 = this.a;
        g5.getClass();
        this.b = new B(g5);
    }

    public void s(long size) {
        this.a.s(-1);
    }

    public void accept(Object u) {
        M1 result = (M1) this.c.m.apply(u);
        if (result != null) {
            try {
                result.sequential().n(this.b);
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
