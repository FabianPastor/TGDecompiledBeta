package j$.util.function;

/* renamed from: j$.util.function.g  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEg implements w {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ w var_a;
    public final /* synthetic */ w b;

    public /* synthetic */ CLASSNAMEg(w wVar, w wVar2) {
        this.var_a = wVar;
        this.b = wVar2;
    }

    public final void accept(int i) {
        w wVar = this.var_a;
        w wVar2 = this.b;
        wVar.accept(i);
        wVar2.accept(i);
    }

    public w l(w wVar) {
        wVar.getClass();
        return new CLASSNAMEg(this, wVar);
    }
}
