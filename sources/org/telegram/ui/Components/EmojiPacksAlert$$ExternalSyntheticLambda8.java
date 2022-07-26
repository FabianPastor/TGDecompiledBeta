package org.telegram.ui.Components;

import android.view.View;
import java.util.ArrayList;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class EmojiPacksAlert$$ExternalSyntheticLambda8 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ EmojiPacksAlert f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ Theme.ResourcesProvider f$3;

    public /* synthetic */ EmojiPacksAlert$$ExternalSyntheticLambda8(EmojiPacksAlert emojiPacksAlert, ArrayList arrayList, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = emojiPacksAlert;
        this.f$1 = arrayList;
        this.f$2 = baseFragment;
        this.f$3 = resourcesProvider;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$new$0(this.f$1, this.f$2, this.f$3, view, i);
    }
}
