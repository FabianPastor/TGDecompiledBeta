package j$.util.stream;

import j$.J;

/* renamed from: j$.util.stream.u2  reason: case insensitive filesystem */
class CLASSNAMEu2 extends CLASSNAMEx5 {
    final /* synthetic */ CLASSNAMEv2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEu2(CLASSNAMEv2 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void s(long size) {
        this.a.s(-1);
    }

    public void accept(int t) {
        if (((J) this.b.m).e(t)) {
            this.a.accept(t);
        }
    }
}
