package j$.util.stream;

import j$.util.function.N;

class I2 extends CLASSNAMEy2 {
    final /* synthetic */ N m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    I2(V2 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, N n) {
        super(upstream, inputShape, opFlags);
        this.m = n;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new H2(this, sink);
    }
}
