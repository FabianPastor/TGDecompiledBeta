package j$.util.stream;

import j$.util.function.Function;

/* renamed from: j$.util.stream.q5  reason: case insensitive filesystem */
class CLASSNAMEq5 extends K1 {
    final /* synthetic */ Function m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEq5(CLASSNAMEu5 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, Function function) {
        super(upstream, inputShape, opFlags);
        this.m = function;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new CLASSNAMEp5(this, sink);
    }
}
