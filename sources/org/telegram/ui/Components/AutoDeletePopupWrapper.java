package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;

public class AutoDeletePopupWrapper {
    View backItem;
    Callback callback;
    private final ActionBarMenuSubItem disableItem;
    long lastDismissTime;
    public ActionBarPopupWindow.ActionBarPopupWindowLayout windowLayout;

    public interface Callback {
        void dismiss();

        void setAutoDeleteHistory(int i, int i2);
    }

    public AutoDeletePopupWrapper(Context context, PopupSwipeBackLayout popupSwipeBackLayout, Callback callback2, boolean z, Theme.ResourcesProvider resourcesProvider) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, z ? R.drawable.popup_fixed_alert : 0, resourcesProvider);
        this.windowLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setFitItems(true);
        this.callback = callback2;
        if (popupSwipeBackLayout != null) {
            ActionBarMenuSubItem addItem = ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_arrow_back, LocaleController.getString("Back", R.string.Back), false, resourcesProvider);
            this.backItem = addItem;
            addItem.setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda5(popupSwipeBackLayout));
        }
        ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_autodelete_1d, LocaleController.getString("AutoDelete1Day", R.string.AutoDelete1Day), false, resourcesProvider).setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda4(this, callback2));
        ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_autodelete_1w, LocaleController.getString("AutoDelete7Days", R.string.AutoDelete7Days), false, resourcesProvider).setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda2(this, callback2));
        ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_autodelete_1m, LocaleController.getString("AutoDelete1Month", R.string.AutoDelete1Month), false, resourcesProvider).setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda1(this, callback2));
        ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_customize, LocaleController.getString("AutoDeleteCustom", R.string.AutoDeleteCustom), false, resourcesProvider).setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda0(this, context, resourcesProvider, callback2));
        ActionBarMenuSubItem addItem2 = ActionBarMenuItem.addItem(this.windowLayout, R.drawable.msg_disable, LocaleController.getString("AutoDeleteDisable", R.string.AutoDeleteDisable), false, resourcesProvider);
        this.disableItem = addItem2;
        addItem2.setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda3(this, callback2));
        addItem2.setColors(Theme.getColor("dialogTextRed2"), Theme.getColor("dialogTextRed2"));
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor("actionBarDefaultSubmenuSeparator", resourcesProvider));
        int i = R.id.fit_width_tag;
        frameLayout.setTag(i, 1);
        this.windowLayout.addView(frameLayout, LayoutHelper.createLinear(-1, 8));
        TextView textView = new TextView(context);
        textView.setTag(i, 1);
        textView.setPadding(AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f));
        textView.setTextSize(1, 13.0f);
        textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        textView.setText(LocaleController.getString("AutoDeletePopupDescription", R.string.AutoDeletePopupDescription));
        this.windowLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(Callback callback2, View view) {
        dismiss();
        callback2.setAutoDeleteHistory(86400, 70);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(Callback callback2, View view) {
        dismiss();
        callback2.setAutoDeleteHistory(604800, 70);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(Callback callback2, View view) {
        dismiss();
        callback2.setAutoDeleteHistory(2678400, 70);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(Context context, Theme.ResourcesProvider resourcesProvider, Callback callback2, View view) {
        dismiss();
        AlertsCreator.createAutoDeleteDatePickerDialog(context, resourcesProvider, new AutoDeletePopupWrapper$$ExternalSyntheticLambda7(callback2));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$new$4(Callback callback2, boolean z, int i) {
        callback2.setAutoDeleteHistory(i * 60, i == 0 ? 71 : 70);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(Callback callback2, View view) {
        dismiss();
        callback2.setAutoDeleteHistory(0, 71);
    }

    private void dismiss() {
        this.callback.dismiss();
        this.lastDismissTime = System.currentTimeMillis();
    }

    /* renamed from: updateItems */
    public void lambda$updateItems$7(int i) {
        if (System.currentTimeMillis() - this.lastDismissTime < 200) {
            AndroidUtilities.runOnUIThread(new AutoDeletePopupWrapper$$ExternalSyntheticLambda6(this, i));
        } else if (i == 0) {
            this.disableItem.setVisibility(8);
        } else {
            this.disableItem.setVisibility(0);
        }
    }
}
