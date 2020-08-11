package j$.util.stream;

import j$.util.function.Function;

/* renamed from: j$.util.stream.m5  reason: case insensitive filesystem */
class CLASSNAMEm5 extends CLASSNAMEt5 {
    final /* synthetic */ Function m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEm5(CLASSNAMEu5 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, Function function) {
        super(upstream, inputShape, opFlags);
        this.m = function;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new CLASSNAMEl5(this, sink);
    }
}
