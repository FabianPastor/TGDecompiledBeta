package org.telegram.ui;

import android.util.SparseIntArray;
import androidx.viewpager.widget.ViewPager;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ReactedUsersListView;

public final /* synthetic */ class ChatActivity$110$$ExternalSyntheticLambda0 implements ReactedUsersListView.OnHeightChangedListener {
    public final /* synthetic */ SparseIntArray f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ ViewPager f$3;
    public final /* synthetic */ ActionBarPopupWindow.ActionBarPopupWindowLayout f$4;
    public final /* synthetic */ int[] f$5;

    public /* synthetic */ ChatActivity$110$$ExternalSyntheticLambda0(SparseIntArray sparseIntArray, int i, int i2, ViewPager viewPager, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, int[] iArr) {
        this.f$0 = sparseIntArray;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = viewPager;
        this.f$4 = actionBarPopupWindowLayout;
        this.f$5 = iArr;
    }

    public final void onHeightChanged(ReactedUsersListView reactedUsersListView, int i) {
        ChatActivity.AnonymousClass110.lambda$instantiateItem$1(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, reactedUsersListView, i);
    }
}
