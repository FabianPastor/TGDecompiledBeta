package j$.util.stream;

import j$.util.function.C;

/* renamed from: j$.util.stream.m2  reason: case insensitive filesystem */
class CLASSNAMEm2 extends CLASSNAMEt5 {
    final /* synthetic */ C m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEm2(CLASSNAMEz2 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, C c) {
        super(upstream, inputShape, opFlags);
        this.m = c;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new CLASSNAMEl2(this, sink);
    }
}
