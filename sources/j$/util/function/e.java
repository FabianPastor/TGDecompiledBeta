package j$.util.function;

public final /* synthetic */ class e implements f {
    public final /* synthetic */ f a;
    public final /* synthetic */ f b;

    public /* synthetic */ e(f fVar, f fVar2) {
        this.a = fVar;
        this.b = fVar2;
    }

    public final void accept(double d) {
        f fVar = this.a;
        f fVar2 = this.b;
        fVar.accept(d);
        fVar2.accept(d);
    }

    public f j(f fVar) {
        fVar.getClass();
        return new e(this, fVar);
    }
}
