package j$.util.stream;

/* renamed from: j$.util.stream.e2  reason: case insensitive filesystem */
class CLASSNAMEe2 extends CLASSNAMEx5 {
    final /* synthetic */ CLASSNAMEf2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEe2(CLASSNAMEf2 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void accept(int t) {
        this.b.m.accept(t);
        this.a.accept(t);
    }
}
