package j$.util.stream;

/* renamed from: j$.util.stream.r2  reason: case insensitive filesystem */
class CLASSNAMEr2 extends CLASSNAMEx5 {
    final /* synthetic */ CLASSNAMEs2 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEr2(CLASSNAMEs2 this$1, G5 downstream) {
        super(downstream);
        this.b = this$1;
    }

    public void s(long size) {
        this.a.s(-1);
    }

    public void accept(int t) {
        A2 result = (A2) this.b.m.a(t);
        if (result != null) {
            try {
                result.sequential().S(new G(this));
            } catch (Throwable th) {
            }
        }
        if (result != null) {
            result.close();
            return;
        }
        return;
        throw th;
    }

    public /* synthetic */ void a(int i) {
        this.a.accept(i);
    }
}
