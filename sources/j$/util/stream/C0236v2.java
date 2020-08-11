package j$.util.stream;

import j$.util.function.D;

/* renamed from: j$.util.stream.v2  reason: case insensitive filesystem */
class CLASSNAMEv2 extends CLASSNAMEy2 {
    final /* synthetic */ D m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEv2(CLASSNAMEz2 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, D d) {
        super(upstream, inputShape, opFlags);
        this.m = d;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new CLASSNAMEu2(this, sink);
    }
}
