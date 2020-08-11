package j$.util.stream;

import j$.util.function.E;

/* renamed from: j$.util.stream.q2  reason: case insensitive filesystem */
class CLASSNAMEq2 extends K1 {
    final /* synthetic */ E m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEq2(CLASSNAMEz2 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, E e) {
        super(upstream, inputShape, opFlags);
        this.m = e;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new CLASSNAMEp2(this, sink);
    }
}
