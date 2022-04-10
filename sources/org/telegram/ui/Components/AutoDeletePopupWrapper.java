package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
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
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, z ? NUM : 0, resourcesProvider);
        this.windowLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setFitItems(true);
        this.callback = callback2;
        if (popupSwipeBackLayout != null) {
            ActionBarMenuSubItem addItem = ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("Back", NUM), false, resourcesProvider);
            this.backItem = addItem;
            addItem.setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda5(popupSwipeBackLayout));
        }
        ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("AutoDelete1Day", NUM), false, resourcesProvider).setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda4(this, callback2));
        ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("AutoDelete7Days", NUM), false, resourcesProvider).setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda2(this, callback2));
        ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("AutoDelete1Month", NUM), false, resourcesProvider).setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda1(this, callback2));
        ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("AutoDeleteCustom", NUM), false, resourcesProvider).setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda0(this, context, callback2));
        ActionBarMenuSubItem addItem2 = ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("AutoDeleteDisable", NUM), false, resourcesProvider);
        this.disableItem = addItem2;
        addItem2.setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda3(this, callback2));
        addItem2.setColors(Theme.getColor("dialogTextRed2"), Theme.getColor("dialogTextRed2"));
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor("graySection"));
        frameLayout.setTag(NUM, 1);
        this.windowLayout.addView(frameLayout, LayoutHelper.createLinear(-1, 8));
        TextView textView = new TextView(context);
        textView.setTag(NUM, 1);
        textView.setPadding(AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f));
        textView.setTextSize(1, 13.0f);
        textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        textView.setText(LocaleController.getString("AutoDeletePopupDescription", NUM));
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
    public /* synthetic */ void lambda$new$5(Context context, Callback callback2, View view) {
        dismiss();
        AlertsCreator.createAutoDeleteDatePickerDialog(context, new AutoDeletePopupWrapper$$ExternalSyntheticLambda7(callback2));
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
