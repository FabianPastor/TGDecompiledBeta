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

    public AutoDeletePopupWrapper(Context context, PopupSwipeBackLayout swipeBackLayout, Callback callback2, boolean createBackground, Theme.ResourcesProvider resourcesProvider) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, createBackground ? NUM : 0, resourcesProvider);
        this.windowLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setFitItems(true);
        this.callback = callback2;
        if (swipeBackLayout != null) {
            ActionBarMenuSubItem addItem = ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("Back", NUM), false, resourcesProvider);
            this.backItem = addItem;
            addItem.setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda5(swipeBackLayout));
        }
        ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("AutoDelete1Day", NUM), false, resourcesProvider).setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda1(this, callback2));
        ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("AutoDelete7Days", NUM), false, resourcesProvider).setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda2(this, callback2));
        ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("AutoDelete1Month", NUM), false, resourcesProvider).setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda3(this, callback2));
        ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("AutoDeleteCustom", NUM), false, resourcesProvider).setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda0(this, context, resourcesProvider, callback2));
        ActionBarMenuSubItem addItem2 = ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("AutoDeleteDisable", NUM), false, resourcesProvider);
        this.disableItem = addItem2;
        addItem2.setOnClickListener(new AutoDeletePopupWrapper$$ExternalSyntheticLambda4(this, callback2));
        addItem2.setColors(Theme.getColor("dialogTextRed2"), Theme.getColor("dialogTextRed2"));
        View gap = new FrameLayout(context);
        gap.setBackgroundColor(Theme.getColor("actionBarDefaultSubmenuSeparator", resourcesProvider));
        gap.setTag(NUM, 1);
        this.windowLayout.addView(gap, LayoutHelper.createLinear(-1, 8));
        TextView textView = new TextView(context);
        textView.setTag(NUM, 1);
        textView.setPadding(AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f));
        textView.setTextSize(1, 13.0f);
        textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        textView.setText(LocaleController.getString("AutoDeletePopupDescription", NUM));
        this.windowLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-AutoDeletePopupWrapper  reason: not valid java name */
    public /* synthetic */ void m557lambda$new$1$orgtelegramuiComponentsAutoDeletePopupWrapper(Callback callback2, View view) {
        dismiss();
        callback2.setAutoDeleteHistory(86400, 70);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-AutoDeletePopupWrapper  reason: not valid java name */
    public /* synthetic */ void m558lambda$new$2$orgtelegramuiComponentsAutoDeletePopupWrapper(Callback callback2, View view) {
        dismiss();
        callback2.setAutoDeleteHistory(604800, 70);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-AutoDeletePopupWrapper  reason: not valid java name */
    public /* synthetic */ void m559lambda$new$3$orgtelegramuiComponentsAutoDeletePopupWrapper(Callback callback2, View view) {
        dismiss();
        callback2.setAutoDeleteHistory(2678400, 70);
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-AutoDeletePopupWrapper  reason: not valid java name */
    public /* synthetic */ void m560lambda$new$5$orgtelegramuiComponentsAutoDeletePopupWrapper(Context context, Theme.ResourcesProvider resourcesProvider, Callback callback2, View view) {
        dismiss();
        AlertsCreator.createAutoDeleteDatePickerDialog(context, resourcesProvider, new AutoDeletePopupWrapper$$ExternalSyntheticLambda7(callback2));
    }

    static /* synthetic */ void lambda$new$4(Callback callback2, boolean notify, int timeInMinutes) {
        callback2.setAutoDeleteHistory(timeInMinutes * 60, timeInMinutes == 0 ? 71 : 70);
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-AutoDeletePopupWrapper  reason: not valid java name */
    public /* synthetic */ void m561lambda$new$6$orgtelegramuiComponentsAutoDeletePopupWrapper(Callback callback2, View view) {
        dismiss();
        callback2.setAutoDeleteHistory(0, 71);
    }

    private void dismiss() {
        this.callback.dismiss();
        this.lastDismissTime = System.currentTimeMillis();
    }

    /* renamed from: updateItems */
    public void m562xf8eeadab(int ttl) {
        if (System.currentTimeMillis() - this.lastDismissTime < 200) {
            AndroidUtilities.runOnUIThread(new AutoDeletePopupWrapper$$ExternalSyntheticLambda6(this, ttl));
        } else if (ttl == 0) {
            this.disableItem.setVisibility(8);
        } else {
            this.disableItem.setVisibility(0);
        }
    }
}
