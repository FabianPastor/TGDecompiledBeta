package j$.util.function;

public final /* synthetic */ class j implements k {
    public final /* synthetic */ k a;
    public final /* synthetic */ k b;

    public /* synthetic */ j(k kVar, k kVar2) {
        this.a = kVar;
        this.b = kVar2;
    }

    public final void accept(int i) {
        k kVar = this.a;
        k kVar2 = this.b;
        kVar.accept(i);
        kVar2.accept(i);
    }

    public k l(k kVar) {
        kVar.getClass();
        return new j(this, kVar);
    }
}
