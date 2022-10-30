package org.telegram.ui.Cells;

import java.util.Comparator;
import org.telegram.ui.Cells.ChatMessageCell;
/* loaded from: classes3.dex */
public final /* synthetic */ class ChatMessageCell$$ExternalSyntheticLambda8 implements Comparator {
    public static final /* synthetic */ ChatMessageCell$$ExternalSyntheticLambda8 INSTANCE = new ChatMessageCell$$ExternalSyntheticLambda8();

    private /* synthetic */ ChatMessageCell$$ExternalSyntheticLambda8() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$setMessageContent$7;
        lambda$setMessageContent$7 = ChatMessageCell.lambda$setMessageContent$7((ChatMessageCell.PollButton) obj, (ChatMessageCell.PollButton) obj2);
        return lambda$setMessageContent$7;
    }
}
