package j$.util.stream;

import j$.util.function.G;

/* renamed from: j$.util.stream.k2  reason: case insensitive filesystem */
class CLASSNAMEk2 extends CLASSNAMEy2 {
    final /* synthetic */ G m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEk2(CLASSNAMEz2 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, G g) {
        super(upstream, inputShape, opFlags);
        this.m = g;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new CLASSNAMEj2(this, sink);
    }
}
