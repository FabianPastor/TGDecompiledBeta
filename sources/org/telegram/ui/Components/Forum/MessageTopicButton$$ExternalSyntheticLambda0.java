package org.telegram.ui.Components.Forum;

import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AnimatedEmojiSpan;
/* loaded from: classes3.dex */
public final /* synthetic */ class MessageTopicButton$$ExternalSyntheticLambda0 implements AnimatedEmojiSpan.InvalidateHolder {
    public final /* synthetic */ ChatMessageCell f$0;

    @Override // org.telegram.ui.Components.AnimatedEmojiSpan.InvalidateHolder
    public final void invalidate() {
        this.f$0.invalidateOutbounds();
    }
}
