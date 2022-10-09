package j$.wrappers;

import j$.util.stream.InterfaceCLASSNAMEg;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.BaseStream;
/* loaded from: classes2.dex */
public final /* synthetic */ class I0 implements BaseStream {
    final /* synthetic */ InterfaceCLASSNAMEg a;

    private /* synthetic */ I0(InterfaceCLASSNAMEg interfaceCLASSNAMEg) {
        this.a = interfaceCLASSNAMEg;
    }

    public static /* synthetic */ BaseStream n0(InterfaceCLASSNAMEg interfaceCLASSNAMEg) {
        if (interfaceCLASSNAMEg == null) {
            return null;
        }
        return interfaceCLASSNAMEg instanceof H0 ? ((H0) interfaceCLASSNAMEg).a : new I0(interfaceCLASSNAMEg);
    }

    @Override // java.util.stream.BaseStream, java.lang.AutoCloseable
    public /* synthetic */ void close() {
        this.a.close();
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ Iterator iterator() {
        return this.a.mo303iterator();
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return n0(this.a.onClose(runnable));
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ BaseStream parallel() {
        return n0(this.a.mo304parallel());
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ BaseStream sequential() {
        return n0(this.a.mo305sequential());
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ Spliterator spliterator() {
        return CLASSNAMEh.a(this.a.mo306spliterator());
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ BaseStream unordered() {
        return n0(this.a.unordered());
    }
}
