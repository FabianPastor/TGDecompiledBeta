package j$.util.stream;

import j$.util.function.M;

class K2 extends K1 {
    final /* synthetic */ M m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    K2(V2 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, M m2) {
        super(upstream, inputShape, opFlags);
        this.m = m2;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new J2(this, sink);
    }
}
