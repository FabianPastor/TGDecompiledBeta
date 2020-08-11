package j$.util.stream;

import j$.util.function.J;

class R2 extends U2 {
    final /* synthetic */ J m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    R2(V2 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, J j) {
        super(upstream, inputShape, opFlags);
        this.m = j;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new Q2(this, sink);
    }
}
