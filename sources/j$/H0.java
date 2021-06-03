package j$;

import j$.util.Spliterator;
import j$.util.stream.CLASSNAMEl1;
import java.util.Iterator;
import java.util.stream.BaseStream;

public final /* synthetic */ class H0 implements CLASSNAMEl1 {
    final /* synthetic */ BaseStream a;

    private /* synthetic */ H0(BaseStream baseStream) {
        this.a = baseStream;
    }

    public static /* synthetic */ CLASSNAMEl1 m0(BaseStream baseStream) {
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

    public /* synthetic */ CLASSNAMEl1 onClose(Runnable runnable) {
        return m0(this.a.onClose(runnable));
    }

    public /* synthetic */ CLASSNAMEl1 parallel() {
        return m0(this.a.parallel());
    }

    public /* synthetic */ CLASSNAMEl1 sequential() {
        return m0(this.a.sequential());
    }

    public /* synthetic */ Spliterator spliterator() {
        return CLASSNAMEg.a(this.a.spliterator());
    }

    public /* synthetic */ CLASSNAMEl1 unordered() {
        return m0(this.a.unordered());
    }
}
