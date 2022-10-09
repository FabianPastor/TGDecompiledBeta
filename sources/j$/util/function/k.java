package j$.util.function;
/* loaded from: classes2.dex */
public final /* synthetic */ class k implements l {
    public final /* synthetic */ l a;
    public final /* synthetic */ l b;

    public /* synthetic */ k(l lVar, l lVar2) {
        this.a = lVar;
        this.b = lVar2;
    }

    @Override // j$.util.function.l
    public final void accept(int i) {
        l lVar = this.a;
        l lVar2 = this.b;
        lVar.accept(i);
        lVar2.accept(i);
    }

    @Override // j$.util.function.l
    public l l(l lVar) {
        lVar.getClass();
        return new k(this, lVar);
    }
}
