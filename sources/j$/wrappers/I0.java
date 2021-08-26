package j$.wrappers;

import j$.util.stream.CLASSNAMEg;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.BaseStream;

public final /* synthetic */ class I0 implements BaseStream {
    final /* synthetic */ CLASSNAMEg a;

    private /* synthetic */ I0(CLASSNAMEg gVar) {
        this.a = gVar;
    }

    public static /* synthetic */ BaseStream n0(CLASSNAMEg gVar) {
        if (gVar == null) {
            return null;
        }
        return gVar instanceof H0 ? ((H0) gVar).a : new I0(gVar);
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

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return n0(this.a.onClose(runnable));
    }

    public /* synthetic */ BaseStream parallel() {
        return n0(this.a.parallel());
    }

    public /* synthetic */ BaseStream sequential() {
        return n0(this.a.sequential());
    }

    public /* synthetic */ Spliterator spliterator() {
        return CLASSNAMEh.a(this.a.spliterator());
    }

    public /* synthetic */ BaseStream unordered() {
        return n0(this.a.unordered());
    }
}
