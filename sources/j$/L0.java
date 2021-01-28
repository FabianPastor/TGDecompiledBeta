package j$;

import j$.util.stream.CLASSNAMEl1;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.BaseStream;

public final /* synthetic */ class L0 implements BaseStream {
    final /* synthetic */ CLASSNAMEl1 a;

    private /* synthetic */ L0(CLASSNAMEl1 l1Var) {
        this.a = l1Var;
    }

    public static /* synthetic */ BaseStream m0(CLASSNAMEl1 l1Var) {
        if (l1Var == null) {
            return null;
        }
        return l1Var instanceof K0 ? ((K0) l1Var).a : new L0(l1Var);
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
        return m0(this.a.onClose(runnable));
    }

    public /* synthetic */ BaseStream parallel() {
        return m0(this.a.parallel());
    }

    public /* synthetic */ BaseStream sequential() {
        return m0(this.a.sequential());
    }

    public /* synthetic */ Spliterator spliterator() {
        return CLASSNAMEk.a(this.a.spliterator());
    }

    public /* synthetic */ BaseStream unordered() {
        return m0(this.a.unordered());
    }
}
