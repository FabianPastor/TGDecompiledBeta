package j$.util.function;

public final /* synthetic */ class h implements y {
    public final /* synthetic */ y a;
    public final /* synthetic */ y b;

    public /* synthetic */ h(y yVar, y yVar2) {
        this.a = yVar;
        this.b = yVar2;
    }

    public final void accept(long j) {
        y yVar = this.a;
        y yVar2 = this.b;
        yVar.accept(j);
        yVar2.accept(j);
    }

    public y g(y yVar) {
        yVar.getClass();
        return new h(this, yVar);
    }
}
