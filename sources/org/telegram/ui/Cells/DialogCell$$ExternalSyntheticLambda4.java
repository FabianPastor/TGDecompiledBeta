package org.telegram.ui.Cells;

import java.util.Comparator;
import org.telegram.messenger.MessageObject;
/* loaded from: classes3.dex */
public final /* synthetic */ class DialogCell$$ExternalSyntheticLambda4 implements Comparator {
    public static final /* synthetic */ DialogCell$$ExternalSyntheticLambda4 INSTANCE = new DialogCell$$ExternalSyntheticLambda4();

    private /* synthetic */ DialogCell$$ExternalSyntheticLambda4() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$updateMessageThumbs$4;
        lambda$updateMessageThumbs$4 = DialogCell.lambda$updateMessageThumbs$4((MessageObject) obj, (MessageObject) obj2);
        return lambda$updateMessageThumbs$4;
    }
}
