package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.Components.EditTextEmoji.AnonymousClass3;

final /* synthetic */ class EditTextEmoji$3$$Lambda$0 implements OnClickListener {
    private final AnonymousClass3 arg$1;

    EditTextEmoji$3$$Lambda$0(AnonymousClass3 anonymousClass3) {
        this.arg$1 = anonymousClass3;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onClearEmojiRecent$0$EditTextEmoji$3(dialogInterface, i);
    }
}
