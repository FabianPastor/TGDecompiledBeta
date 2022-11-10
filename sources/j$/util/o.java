package j$.util;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class o implements j$.util.function.l {
    public final /* synthetic */ Consumer a;

    @Override // j$.util.function.l
    public final void accept(int i) {
        this.a.accept(Integer.valueOf(i));
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }
}
