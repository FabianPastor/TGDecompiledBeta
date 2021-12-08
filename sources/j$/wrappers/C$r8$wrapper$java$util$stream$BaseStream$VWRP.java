package j$.wrappers;

import j$.util.Spliterator;
import j$.util.stream.BaseStream;
import java.util.Iterator;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$stream$BaseStream$-V-WRP  reason: invalid class name */
/* compiled from: BaseStream */
public final /* synthetic */ class C$r8$wrapper$java$util$stream$BaseStream$VWRP implements BaseStream {
    final /* synthetic */ java.util.stream.BaseStream wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$stream$BaseStream$VWRP(java.util.stream.BaseStream baseStream) {
        this.wrappedValue = baseStream;
    }

    public static /* synthetic */ BaseStream convert(java.util.stream.BaseStream baseStream) {
        if (baseStream == null) {
            return null;
        }
        return baseStream instanceof C$r8$wrapper$java$util$stream$BaseStream$WRP ? ((C$r8$wrapper$java$util$stream$BaseStream$WRP) baseStream).wrappedValue : new C$r8$wrapper$java$util$stream$BaseStream$VWRP(baseStream);
    }

    public /* synthetic */ void close() {
        this.wrappedValue.close();
    }

    public /* synthetic */ boolean isParallel() {
        return this.wrappedValue.isParallel();
    }

    public /* synthetic */ Iterator iterator() {
        return this.wrappedValue.iterator();
    }

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return convert(this.wrappedValue.onClose(runnable));
    }

    public /* synthetic */ BaseStream parallel() {
        return convert(this.wrappedValue.parallel());
    }

    public /* synthetic */ BaseStream sequential() {
        return convert(this.wrappedValue.sequential());
    }

    public /* synthetic */ Spliterator spliterator() {
        return C$r8$wrapper$java$util$Spliterator$VWRP.convert(this.wrappedValue.spliterator());
    }

    public /* synthetic */ BaseStream unordered() {
        return convert(this.wrappedValue.unordered());
    }
}
