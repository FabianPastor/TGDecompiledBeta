package j$.util.stream;

import j$.util.function.CLASSNAMEu;

class C1 extends K1 {
    final /* synthetic */ CLASSNAMEu m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C1(L1 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, CLASSNAMEu uVar) {
        super(upstream, inputShape, opFlags);
        this.m = uVar;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new B1(this, sink);
    }
}
