package j$.util.stream;

import j$.util.function.w;

/* renamed from: j$.util.stream.y1  reason: case insensitive filesystem */
class CLASSNAMEy1 extends CLASSNAMEy2 {
    final /* synthetic */ w m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEy1(L1 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, w wVar) {
        super(upstream, inputShape, opFlags);
        this.m = wVar;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new CLASSNAMEx1(this, sink);
    }
}
