package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ChatThemeBottomSheet$$ExternalSyntheticLambda6 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ ChatThemeBottomSheet f$0;
    public final /* synthetic */ ChatActivity.ThemeDelegate f$1;

    public /* synthetic */ ChatThemeBottomSheet$$ExternalSyntheticLambda6(ChatThemeBottomSheet chatThemeBottomSheet, ChatActivity.ThemeDelegate themeDelegate) {
        this.f$0 = chatThemeBottomSheet;
        this.f$1 = themeDelegate;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.m3914lambda$new$1$orgtelegramuiComponentsChatThemeBottomSheet(this.f$1, view, i);
    }
}
