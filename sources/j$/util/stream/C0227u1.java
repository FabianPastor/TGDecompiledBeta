package j$.util.stream;

import j$.util.function.y;

/* renamed from: j$.util.stream.u1  reason: case insensitive filesystem */
class CLASSNAMEu1 extends K1 {
    final /* synthetic */ y m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEu1(L1 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, y yVar) {
        super(upstream, inputShape, opFlags);
        this.m = yVar;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new CLASSNAMEt1(this, sink);
    }
}
