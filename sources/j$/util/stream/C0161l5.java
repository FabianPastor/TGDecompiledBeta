package j$.util.stream;

/* renamed from: j$.util.stream.l5  reason: case insensitive filesystem */
class CLASSNAMEl5 extends CLASSNAMEz5 {
    final /* synthetic */ CLASSNAMEm5 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEl5(CLASSNAMEm5 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void s(long size) {
        this.a.s(-1);
    }

    public void accept(Object u) {
        Stream stream = (Stream) this.b.m.apply(u);
        if (stream != null) {
            try {
                ((Stream) stream.sequential()).forEach(this.a);
            } catch (Throwable th) {
            }
        }
        if (stream != null) {
            stream.close();
            return;
        }
        return;
        throw th;
    }
}
