package org.telegram.messenger;

import java.util.ArrayList;
import java.util.Comparator;
import org.telegram.messenger.MediaDataController;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda111 implements Comparator {
    public final /* synthetic */ ArrayList f$0;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda111(ArrayList arrayList) {
        this.f$0 = arrayList;
    }

    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$getEmojiSuggestions$154(this.f$0, (MediaDataController.KeywordResult) obj, (MediaDataController.KeywordResult) obj2);
    }
}
