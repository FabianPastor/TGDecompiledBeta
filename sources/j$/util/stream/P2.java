package j$.util.stream;

import j$.util.function.L;

class P2 extends U2 {
    final /* synthetic */ L m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    P2(V2 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, L l) {
        super(upstream, inputShape, opFlags);
        this.m = l;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new O2(this, sink);
    }
}
