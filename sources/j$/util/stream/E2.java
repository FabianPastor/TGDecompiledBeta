package j$.util.stream;

import j$.util.function.P;

class E2 extends U2 {
    final /* synthetic */ P m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    E2(V2 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, P p) {
        super(upstream, inputShape, opFlags);
        this.m = p;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new D2(this, sink);
    }
}
