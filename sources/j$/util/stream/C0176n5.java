package j$.util.stream;

import j$.util.function.B;

/* renamed from: j$.util.stream.n5  reason: case insensitive filesystem */
class CLASSNAMEn5 extends CLASSNAMEz5 {
    B b;
    final /* synthetic */ CLASSNAMEo5 c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEn5(CLASSNAMEo5 this$1, G5 downstream) {
        super(downstream);
        this.c = this$1;
        G5 g5 = this.a;
        g5.getClass();
        this.b = new CLASSNAMEa1(g5);
    }

    public void s(long size) {
        this.a.s(-1);
    }

    public void accept(Object u) {
        A2 result = (A2) this.c.m.apply(u);
        if (result != null) {
            try {
                result.sequential().S(this.b);
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
