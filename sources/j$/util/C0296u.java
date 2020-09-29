package j$.util;

import j$.util.Iterator;
import j$.util.function.Consumer;
import java.util.Iterator;

/* renamed from: j$.util.u  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEu {
    public static /* synthetic */ void a(Iterator it, Consumer consumer) {
        if (it instanceof Iterator) {
            ((Iterator) it).forEachRemaining(consumer);
        } else {
            Iterator.CC.$default$forEachRemaining(it, consumer);
        }
    }
}
