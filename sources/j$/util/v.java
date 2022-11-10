package j$.util;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
public interface v extends w {
    @Override // j$.util.u
    boolean b(Consumer consumer);

    void d(j$.util.function.q qVar);

    @Override // j$.util.u
    void forEachRemaining(Consumer consumer);

    boolean i(j$.util.function.q qVar);

    @Override // j$.util.w, j$.util.u
    /* renamed from: trySplit */
    v moNUMtrySplit();
}
