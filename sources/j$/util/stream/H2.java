package j$.util.stream;

import j$.i0;

class H2 extends CLASSNAMEy5 {
    final /* synthetic */ I2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    H2(I2 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void accept(long t) {
        this.a.accept(((i0) this.b.m).a(t));
    }
}
