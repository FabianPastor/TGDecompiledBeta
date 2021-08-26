package j$.util.function;

public final /* synthetic */ class r implements s {
    public final /* synthetic */ int a;
    public final /* synthetic */ s b;
    public final /* synthetic */ s c;

    public /* synthetic */ r(s sVar, s sVar2, int i) {
        this.a = i;
        if (i != 1) {
            this.b = sVar;
            this.c = sVar2;
            return;
        }
        this.b = sVar;
        this.c = sVar2;
    }

    public s a(s sVar) {
        switch (this.a) {
            case 0:
                sVar.getClass();
                return new r(this, sVar, 0);
            default:
                sVar.getClass();
                return new r(this, sVar, 0);
        }
    }

    public final long applyAsLong(long j) {
        switch (this.a) {
            case 0:
                return this.c.applyAsLong(this.b.applyAsLong(j));
            default:
                return this.b.applyAsLong(this.c.applyAsLong(j));
        }
    }

    public s b(s sVar) {
        switch (this.a) {
            case 0:
                sVar.getClass();
                return new r(this, sVar, 1);
            default:
                sVar.getClass();
                return new r(this, sVar, 1);
        }
    }
}
