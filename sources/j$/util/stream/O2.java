package j$.util.stream;

import j$.f0;

class O2 extends CLASSNAMEy5 {
    final /* synthetic */ P2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    O2(P2 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void s(long size) {
        this.a.s(-1);
    }

    public void accept(long t) {
        if (((f0) this.b.m).e(t)) {
            this.a.accept(t);
        }
    }
}
