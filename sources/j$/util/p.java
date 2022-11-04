package j$.util;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
public interface p extends java.util.Iterator {

    /* loaded from: classes2.dex */
    public interface a extends p {
        void c(j$.util.function.l lVar);

        void forEachRemaining(Consumer consumer);

        @Override // java.util.Iterator
        /* renamed from: next */
        Integer mo313next();

        int nextInt();
    }

    void forEachRemaining(Object obj);
}
