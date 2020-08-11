package j$.util.stream;

import j$.util.function.Function;

class X4 extends U2 {
    final /* synthetic */ Function m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    X4(CLASSNAMEu5 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, Function function) {
        super(upstream, inputShape, opFlags);
        this.m = function;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new W4(this, sink);
    }
}
