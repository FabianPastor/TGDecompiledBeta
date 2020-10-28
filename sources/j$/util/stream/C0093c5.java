package j$.util.stream;

/* renamed from: j$.util.stream.c5  reason: case insensitive filesystem */
class CLASSNAMEc5 extends CLASSNAMEp5 {
    final /* synthetic */ CLASSNAMEd5 b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEc5(CLASSNAMEd5 d5Var, CLASSNAMEt5 t5Var) {
        super(t5Var);
        this.b = d5Var;
    }

    public void accept(Object obj) {
        Stream stream = (Stream) this.b.l.apply(obj);
        if (stream != null) {
            try {
                ((Stream) stream.sequential()).forEach(this.a);
            } catch (Throwable unused) {
            }
        }
        if (stream != null) {
            stream.close();
            return;
        }
        return;
        throw th;
    }

    public void n(long j) {
        this.a.n(-1);
    }
}
