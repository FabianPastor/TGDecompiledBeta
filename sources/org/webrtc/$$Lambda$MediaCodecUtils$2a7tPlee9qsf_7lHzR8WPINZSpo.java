package org.webrtc;

import android.media.MediaCodecInfo;
import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.Comparator;

/* renamed from: org.webrtc.-$$Lambda$MediaCodecUtils$2a7tPlee9qsf_7lHzR8WPINZSpo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaCodecUtils$2a7tPlee9qsf_7lHzR8WPINZSpo implements Comparator, j$.util.Comparator {
    public static final /* synthetic */ $$Lambda$MediaCodecUtils$2a7tPlee9qsf_7lHzR8WPINZSpo INSTANCE = new $$Lambda$MediaCodecUtils$2a7tPlee9qsf_7lHzR8WPINZSpo();

    private /* synthetic */ $$Lambda$MediaCodecUtils$2a7tPlee9qsf_7lHzR8WPINZSpo() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((MediaCodecInfo) obj).getName().compareTo(((MediaCodecInfo) obj2).getName());
    }

    public /* synthetic */ Comparator reversed() {
        return Comparator.CC.$default$reversed(this);
    }

    public /* synthetic */ java.util.Comparator thenComparing(Function function) {
        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
    }

    public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
        return Comparator.CC.$default$thenComparing(this, function, comparator);
    }

    public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
    }

    public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
        return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
    }

    public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
        return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
    }

    public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
        return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
    }
}
