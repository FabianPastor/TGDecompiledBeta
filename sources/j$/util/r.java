package j$.util;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
public interface r extends p {
    void d(j$.util.function.q qVar);

    void forEachRemaining(Consumer consumer);

    @Override // java.util.Iterator
    /* renamed from: next */
    Long mo311next();

    long nextLong();
}
