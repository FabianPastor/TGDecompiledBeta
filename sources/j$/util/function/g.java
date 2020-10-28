package j$.util.function;

public final /* synthetic */ class g implements u {
    public final /* synthetic */ u a;
    public final /* synthetic */ u b;

    public /* synthetic */ g(u uVar, u uVar2) {
        this.a = uVar;
        this.b = uVar2;
    }

    public final void accept(int i) {
        u uVar = this.a;
        u uVar2 = this.b;
        uVar.accept(i);
        uVar2.accept(i);
    }

    public u l(u uVar) {
        uVar.getClass();
        return new g(this, uVar);
    }
}
