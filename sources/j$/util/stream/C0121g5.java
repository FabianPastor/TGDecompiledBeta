package j$.util.stream;

import j$.util.function.ToIntFunction;

/* renamed from: j$.util.stream.g5  reason: case insensitive filesystem */
class CLASSNAMEg5 extends CLASSNAMEy2 {
    final /* synthetic */ ToIntFunction m;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEg5(CLASSNAMEu5 this$0, CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags, ToIntFunction toIntFunction) {
        super(upstream, inputShape, opFlags);
        this.m = toIntFunction;
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        return new CLASSNAMEf5(this, sink);
    }
}
