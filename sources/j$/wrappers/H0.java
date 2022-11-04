package j$.wrappers;

import j$.util.stream.InterfaceCLASSNAMEg;
import java.util.Iterator;
import java.util.stream.BaseStream;
/* loaded from: classes2.dex */
public final /* synthetic */ class H0 implements InterfaceCLASSNAMEg {
    final /* synthetic */ BaseStream a;

    private /* synthetic */ H0(BaseStream baseStream) {
        this.a = baseStream;
    }

    public static /* synthetic */ InterfaceCLASSNAMEg n0(BaseStream baseStream) {
        if (baseStream == null) {
            return null;
        }
        return baseStream instanceof I0 ? ((I0) baseStream).a : new H0(baseStream);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg, java.lang.AutoCloseable
    public /* synthetic */ void close() {
        this.a.close();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: iterator */
    public /* synthetic */ Iterator mo307iterator() {
        return this.a.iterator();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public /* synthetic */ InterfaceCLASSNAMEg onClose(Runnable runnable) {
        return n0(this.a.onClose(runnable));
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: parallel */
    public /* synthetic */ InterfaceCLASSNAMEg mo308parallel() {
        return n0(this.a.parallel());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: sequential */
    public /* synthetic */ InterfaceCLASSNAMEg mo309sequential() {
        return n0(this.a.sequential());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: spliterator */
    public /* synthetic */ j$.util.u mo310spliterator() {
        return CLASSNAMEg.a(this.a.spliterator());
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public /* synthetic */ InterfaceCLASSNAMEg unordered() {
        return n0(this.a.unordered());
    }
}
