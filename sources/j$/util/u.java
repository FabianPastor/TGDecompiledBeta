package j$.util;

import j$.util.function.Consumer;
import java.util.Comparator;
/* loaded from: classes2.dex */
public interface u {

    /* loaded from: classes2.dex */
    public interface a extends w {
        @Override // j$.util.u
        boolean b(Consumer consumer);

        void c(j$.util.function.l lVar);

        @Override // j$.util.u
        void forEachRemaining(Consumer consumer);

        boolean g(j$.util.function.l lVar);

        @Override // j$.util.w, j$.util.u
        /* renamed from: trySplit */
        a mo326trySplit();
    }

    boolean b(Consumer consumer);

    int characteristics();

    long estimateSize();

    void forEachRemaining(Consumer consumer);

    Comparator getComparator();

    long getExactSizeIfKnown();

    boolean hasCharacteristics(int i);

    /* renamed from: trySplit */
    u mo326trySplit();
}
