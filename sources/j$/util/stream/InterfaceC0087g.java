package j$.util.stream;

import java.util.Iterator;
/* renamed from: j$.util.stream.g  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public interface InterfaceCLASSNAMEg extends AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close();

    boolean isParallel();

    /* renamed from: iterator */
    Iterator mo307iterator();

    InterfaceCLASSNAMEg onClose(Runnable runnable);

    /* renamed from: parallel */
    InterfaceCLASSNAMEg mo308parallel();

    /* renamed from: sequential */
    InterfaceCLASSNAMEg mo309sequential();

    /* renamed from: spliterator */
    j$.util.u mo310spliterator();

    InterfaceCLASSNAMEg unordered();
}
