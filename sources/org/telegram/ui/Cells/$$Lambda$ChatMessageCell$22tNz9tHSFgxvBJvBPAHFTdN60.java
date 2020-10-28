package org.telegram.ui.Cells;

import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.Comparator;
import org.telegram.ui.Cells.ChatMessageCell;

/* renamed from: org.telegram.ui.Cells.-$$Lambda$ChatMessageCell$22tNz9tHSF-gxvBJvBPAHFTdN60  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatMessageCell$22tNz9tHSFgxvBJvBPAHFTdN60 implements Comparator, j$.util.Comparator {
    public static final /* synthetic */ $$Lambda$ChatMessageCell$22tNz9tHSFgxvBJvBPAHFTdN60 INSTANCE = new $$Lambda$ChatMessageCell$22tNz9tHSFgxvBJvBPAHFTdN60();

    private /* synthetic */ $$Lambda$ChatMessageCell$22tNz9tHSFgxvBJvBPAHFTdN60() {
    }

    public final int compare(Object obj, Object obj2) {
        return ChatMessageCell.lambda$setMessageContent$0((ChatMessageCell.PollButton) obj, (ChatMessageCell.PollButton) obj2);
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
