package j$.wrappers;

import j$.util.stream.CLASSNAMEg;
import j$.util.y;
import java.util.Iterator;
import java.util.stream.BaseStream;

public final /* synthetic */ class H0 implements CLASSNAMEg {
    final /* synthetic */ BaseStream a;

    private /* synthetic */ H0(BaseStream baseStream) {
        this.a = baseStream;
    }

    public static /* synthetic */ CLASSNAMEg n0(BaseStream baseStream) {
        if (baseStream == null) {
            return null;
        }
        return baseStream instanceof I0 ? ((I0) baseStream).a : new H0(baseStream);
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ Iterator iterator() {
        return this.a.iterator();
    }

    public /* synthetic */ CLASSNAMEg onClose(Runnable runnable) {
        return n0(this.a.onClose(runnable));
    }

    public /* synthetic */ CLASSNAMEg parallel() {
        return n0(this.a.parallel());
    }

    public /* synthetic */ CLASSNAMEg sequential() {
        return n0(this.a.sequential());
    }

    public /* synthetic */ y spliterator() {
        return CLASSNAMEg.a(this.a.spliterator());
    }

    public /* synthetic */ CLASSNAMEg unordered() {
        return n0(this.a.unordered());
    }
}
