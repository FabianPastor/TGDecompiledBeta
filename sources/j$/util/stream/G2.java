package j$.util.stream;

import j$.util.function.K;

class G2 extends CLASSNAMEt5 {
    final /* synthetic */ K m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    G2(V2 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, K k) {
        super(upstream, inputShape, opFlags);
        this.m = k;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new F2(this, sink);
    }
}