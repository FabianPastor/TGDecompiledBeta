package j$.util.stream;

import java.util.Iterator;
/* renamed from: j$.util.stream.g  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public interface InterfaceCLASSNAMEg extends AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close();

    boolean isParallel();

    /* renamed from: iterator */
    Iterator mo303iterator();

    InterfaceCLASSNAMEg onClose(Runnable runnable);

    /* renamed from: parallel */
    InterfaceCLASSNAMEg mo304parallel();

    /* renamed from: sequential */
    InterfaceCLASSNAMEg mo305sequential();

    /* renamed from: spliterator */
    j$.util.u mo306spliterator();

    InterfaceCLASSNAMEg unordered();
}
