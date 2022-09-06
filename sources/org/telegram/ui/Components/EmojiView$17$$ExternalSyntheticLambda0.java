package org.telegram.ui.Components;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.Components.EmojiView;

public final /* synthetic */ class EmojiView$17$$ExternalSyntheticLambda0 implements Comparator {
    public final /* synthetic */ EmojiView.AnonymousClass17 f$0;

    public /* synthetic */ EmojiView$17$$ExternalSyntheticLambda0(EmojiView.AnonymousClass17 r1) {
        this.f$0 = r1;
    }

    public final int compare(Object obj, Object obj2) {
        return this.f$0.lambda$stickerSetPositionChanged$0((TLRPC$TL_messages_stickerSet) obj, (TLRPC$TL_messages_stickerSet) obj2);
    }
}
