package j$.util.stream;

import j$.I;

class E1 extends CLASSNAMEw5 {
    final /* synthetic */ F1 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    E1(F1 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void s(long size) {
        this.a.s(-1);
    }

    public void accept(double t) {
        if (((I) this.b.m).e(t)) {
            this.a.accept(t);
        }
    }
}
