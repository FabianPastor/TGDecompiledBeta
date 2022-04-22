package org.telegram.ui.Components;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

public class ChatNotificationsPopupWrapper {
    View backItem;
    Callback callback;
    int currentAccount;
    long lastDismissTime;
    ActionBarMenuSubItem muteForLastSelected;
    private int muteForLastSelected1Time;
    ActionBarMenuSubItem muteForLastSelected2;
    private int muteForLastSelected2Time;
    ActionBarMenuSubItem muteUnmuteButton;
    ActionBarPopupWindow popupWindow;
    ActionBarMenuSubItem soundToggle;
    public ActionBarPopupWindow.ActionBarPopupWindowLayout windowLayout;

    public interface Callback {
        void dismiss();

        void muteFor(int i);

        void showCustomize();

        void toggleMute();

        void toggleSound();
    }

    public ChatNotificationsPopupWrapper(Context context, int i, PopupSwipeBackLayout popupSwipeBackLayout, boolean z, boolean z2, Callback callback2, Theme.ResourcesProvider resourcesProvider) {
        this.currentAccount = i;
        this.callback = callback2;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, z ? NUM : 0, resourcesProvider);
        this.windowLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setFitItems(true);
        if (popupSwipeBackLayout != null) {
            ActionBarMenuSubItem addItem = ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("Back", NUM), false, resourcesProvider);
            this.backItem = addItem;
            addItem.setOnClickListener(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda6(popupSwipeBackLayout));
        }
        ActionBarMenuSubItem addItem2 = ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("SoundOn", NUM), false, resourcesProvider);
        this.soundToggle = addItem2;
        addItem2.setOnClickListener(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda4(this, callback2));
        ActionBarMenuSubItem addItem3 = ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("MuteFor1h", NUM), false, resourcesProvider);
        this.muteForLastSelected = addItem3;
        addItem3.setOnClickListener(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda5(this, callback2));
        ActionBarMenuSubItem addItem4 = ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("MuteFor1h", NUM), false, resourcesProvider);
        this.muteForLastSelected2 = addItem4;
        addItem4.setOnClickListener(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda3(this, callback2));
        ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("MuteForPopup", NUM), false, resourcesProvider).setOnClickListener(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda0(this, context, i, callback2));
        ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("NotificationsCustomize", NUM), false, resourcesProvider).setOnClickListener(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda2(this, callback2));
        ActionBarMenuSubItem addItem5 = ActionBarMenuItem.addItem(this.windowLayout, 0, "", false, resourcesProvider);
        this.muteUnmuteButton = addItem5;
        addItem5.setOnClickListener(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda1(this, callback2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(Callback callback2, View view) {
        dismiss();
        callback2.toggleSound();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(Callback callback2, View view) {
        dismiss();
        callback2.muteFor(this.muteForLastSelected1Time);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(Callback callback2, View view) {
        dismiss();
        callback2.muteFor(this.muteForLastSelected2Time);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(Context context, int i, Callback callback2, View view) {
        dismiss();
        AlertsCreator.createMuteForPickerDialog(context, new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda10(i, callback2));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$new$4(int i, int i2, Callback callback2) {
        if (i != 0) {
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(i2);
            notificationsSettings.edit().putInt("last_selected_mute_until_time", i).putInt("last_selected_mute_until_time2", notificationsSettings.getInt("last_selected_mute_until_time", 0)).apply();
        }
        callback2.muteFor(i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7(Callback callback2, View view) {
        dismiss();
        callback2.showCustomize();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$9(Callback callback2, View view) {
        dismiss();
        AndroidUtilities.runOnUIThread(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda8(callback2));
    }

    private void dismiss() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
            this.popupWindow.dismiss();
        }
        this.callback.dismiss();
        this.lastDismissTime = System.currentTimeMillis();
    }

    /* renamed from: update */
    public void lambda$update$10(long j) {
        int i;
        int i2;
        int i3;
        if (System.currentTimeMillis() - this.lastDismissTime < 200) {
            AndroidUtilities.runOnUIThread(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda9(this, j));
            return;
        }
        boolean isDialogMuted = MessagesController.getInstance(this.currentAccount).isDialogMuted(j);
        if (isDialogMuted) {
            this.muteUnmuteButton.setTextAndIcon(LocaleController.getString("UnmuteNotifications", NUM), NUM);
            i = Theme.getColor("wallet_greenText");
            this.soundToggle.setVisibility(8);
        } else {
            this.muteUnmuteButton.setTextAndIcon(LocaleController.getString("MuteNotifications", NUM), NUM);
            int color = Theme.getColor("dialogTextRed");
            this.soundToggle.setVisibility(0);
            if (MessagesController.getInstance(this.currentAccount).isDialogNotificationsSoundEnabled(j)) {
                this.soundToggle.setTextAndIcon(LocaleController.getString("SoundOff", NUM), NUM);
            } else {
                this.soundToggle.setTextAndIcon(LocaleController.getString("SoundOn", NUM), NUM);
            }
            i = color;
        }
        if (isDialogMuted) {
            i2 = 0;
            i3 = 0;
        } else {
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
            i3 = notificationsSettings.getInt("last_selected_mute_until_time", 0);
            i2 = notificationsSettings.getInt("last_selected_mute_until_time2", 0);
        }
        if (i3 != 0) {
            this.muteForLastSelected1Time = i3;
            this.muteForLastSelected.setVisibility(0);
            this.muteForLastSelected.getImageView().setImageDrawable(TimerDrawable.getTtlIcon(i3));
            this.muteForLastSelected.setText(formatMuteForTime(i3));
        } else {
            this.muteForLastSelected.setVisibility(8);
        }
        if (i2 != 0) {
            this.muteForLastSelected2Time = i2;
            this.muteForLastSelected2.setVisibility(0);
            this.muteForLastSelected2.getImageView().setImageDrawable(TimerDrawable.getTtlIcon(i2));
            this.muteForLastSelected2.setText(formatMuteForTime(i2));
        } else {
            this.muteForLastSelected2.setVisibility(8);
        }
        this.muteUnmuteButton.setColors(i, i);
    }

    private String formatMuteForTime(int i) {
        StringBuilder sb = new StringBuilder();
        int i2 = i / 86400;
        int i3 = (i - (86400 * i2)) / 3600;
        if (i2 != 0) {
            sb.append(i2);
            sb.append(LocaleController.getString("SecretChatTimerDays", NUM));
        }
        if (i3 != 0) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(i3);
            sb.append(LocaleController.getString("SecretChatTimerHours", NUM));
        }
        return LocaleController.formatString("MuteForButton", NUM, sb.toString());
    }

    public void showAsOptions(BaseFragment baseFragment, View view, float f, float f2) {
        if (baseFragment != null && baseFragment.getFragmentView() != null) {
            ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(this.windowLayout, -2, -2);
            this.popupWindow = actionBarPopupWindow;
            actionBarPopupWindow.setPauseNotifications(true);
            this.popupWindow.setDismissAnimationDuration(220);
            this.popupWindow.setOutsideTouchable(true);
            this.popupWindow.setClippingEnabled(true);
            this.popupWindow.setAnimationStyle(NUM);
            this.popupWindow.setFocusable(true);
            this.windowLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.popupWindow.setInputMethodMode(2);
            this.popupWindow.getContentView().setFocusableInTouchMode(true);
            while (view != baseFragment.getFragmentView()) {
                f += view.getX();
                f2 += view.getY();
                view = (View) view.getParent();
            }
            this.popupWindow.showAtLocation(baseFragment.getFragmentView(), 0, (int) (f - (((float) this.windowLayout.getMeasuredWidth()) / 2.0f)), (int) (f2 - (((float) this.windowLayout.getMeasuredHeight()) / 2.0f)));
            this.popupWindow.dimBehind();
        }
    }
}
