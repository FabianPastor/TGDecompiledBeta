package j$.util.stream;

import j$.util.function.CLASSNAMEt;

class H1 extends K1 {
    final /* synthetic */ CLASSNAMEt m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    H1(L1 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, CLASSNAMEt tVar) {
        super(upstream, inputShape, opFlags);
        this.m = tVar;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new G1(this, sink);
    }
}
