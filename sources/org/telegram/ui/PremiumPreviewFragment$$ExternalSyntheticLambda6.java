package org.telegram.ui;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.PremiumPreviewFragment;

public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda6 implements Comparator {
    public final /* synthetic */ MessagesController f$0;

    public /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda6(MessagesController messagesController) {
        this.f$0 = messagesController;
    }

    public final int compare(Object obj, Object obj2) {
        return PremiumPreviewFragment.lambda$fillPremiumFeaturesList$3(this.f$0, (PremiumPreviewFragment.PremiumFeatureData) obj, (PremiumPreviewFragment.PremiumFeatureData) obj2);
    }
}
