package j$.util.stream;

import j$.util.function.CLASSNAMEv;

class F1 extends K1 {
    final /* synthetic */ CLASSNAMEv m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    F1(L1 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, CLASSNAMEv vVar) {
        super(upstream, inputShape, opFlags);
        this.m = vVar;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new E1(this, sink);
    }
}
