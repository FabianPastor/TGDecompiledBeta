package j$.util.stream;

import j$.util.function.x;

class A1 extends U2 {
    final /* synthetic */ x m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    A1(L1 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, x xVar) {
        super(upstream, inputShape, opFlags);
        this.m = xVar;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new CLASSNAMEz1(this, sink);
    }
}
