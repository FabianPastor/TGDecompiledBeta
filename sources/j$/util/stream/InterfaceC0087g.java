package j$.util.stream;

import java.util.Iterator;
/* renamed from: j$.util.stream.g  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public interface InterfaceCLASSNAMEg extends AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close();

    boolean isParallel();

    /* renamed from: iterator */
    Iterator moNUMiterator();

    InterfaceCLASSNAMEg onClose(Runnable runnable);

    /* renamed from: parallel */
    InterfaceCLASSNAMEg moNUMparallel();

    /* renamed from: sequential */
    InterfaceCLASSNAMEg moNUMsequential();

    /* renamed from: spliterator */
    j$.util.u moNUMspliterator();

    InterfaceCLASSNAMEg unordered();
}
