package j$.util.stream;

import j$.h0;

class J2 extends CLASSNAMEy5 {
    final /* synthetic */ K2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    J2(K2 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void accept(long t) {
        this.a.accept(((h0) this.b.m).a(t));
    }
}
