package j$.util;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
public interface t extends w {
    @Override // j$.util.u
    boolean b(Consumer consumer);

    void e(j$.util.function.f fVar);

    @Override // j$.util.u
    void forEachRemaining(Consumer consumer);

    boolean k(j$.util.function.f fVar);

    @Override // j$.util.w, j$.util.u
    /* renamed from: trySplit */
    t mo322trySplit();
}
