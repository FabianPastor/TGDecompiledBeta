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
import org.telegram.ui.ActionBar.Theme;

public class ChatNotificationsPopupWrapper {
    private static final String LAST_SELECTED_TIME_KEY_1 = "last_selected_mute_until_time";
    private static final String LAST_SELECTED_TIME_KEY_2 = "last_selected_mute_until_time2";
    View backItem;
    Callback callback;
    int currentAccount;
    private final boolean isProfile;
    long lastDismissTime;
    ActionBarMenuSubItem muteForLastSelected;
    private int muteForLastSelected1Time;
    ActionBarMenuSubItem muteForLastSelected2;
    private int muteForLastSelected2Time;
    ActionBarMenuSubItem muteUnmuteButton;
    ActionBarPopupWindow popupWindow;
    ActionBarMenuSubItem soundToggle;
    public ActionBarPopupWindow.ActionBarPopupWindowLayout windowLayout;

    public ChatNotificationsPopupWrapper(Context context, int currentAccount2, PopupSwipeBackLayout swipeBackLayout, boolean createBackground, boolean isProfile2, Callback callback2, Theme.ResourcesProvider resourcesProvider) {
        PopupSwipeBackLayout popupSwipeBackLayout = swipeBackLayout;
        Callback callback3 = callback2;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        this.currentAccount = currentAccount2;
        this.callback = callback3;
        this.isProfile = isProfile2;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, createBackground ? NUM : 0, resourcesProvider2);
        this.windowLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setFitItems(true);
        if (popupSwipeBackLayout != null) {
            ActionBarMenuSubItem addItem = ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("Back", NUM), false, resourcesProvider2);
            this.backItem = addItem;
            addItem.setOnClickListener(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda7(popupSwipeBackLayout));
        }
        ActionBarMenuSubItem addItem2 = ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("SoundOn", NUM), false, resourcesProvider2);
        this.soundToggle = addItem2;
        addItem2.setOnClickListener(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda2(this, callback3));
        ActionBarMenuSubItem addItem3 = ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("MuteFor1h", NUM), false, resourcesProvider2);
        this.muteForLastSelected = addItem3;
        addItem3.setOnClickListener(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda3(this, callback3));
        ActionBarMenuSubItem addItem4 = ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("MuteFor1h", NUM), false, resourcesProvider2);
        this.muteForLastSelected2 = addItem4;
        addItem4.setOnClickListener(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda4(this, callback3));
        ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("MuteForPopup", NUM), false, resourcesProvider2).setOnClickListener(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda0(this, context, resourcesProvider, currentAccount2, callback2));
        ActionBarMenuItem.addItem(this.windowLayout, NUM, LocaleController.getString("NotificationsCustomize", NUM), false, resourcesProvider2).setOnClickListener(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda5(this, callback3));
        ActionBarMenuSubItem addItem5 = ActionBarMenuItem.addItem(this.windowLayout, 0, "", false, resourcesProvider2);
        this.muteUnmuteButton = addItem5;
        addItem5.setOnClickListener(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda6(this, callback3));
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatNotificationsPopupWrapper  reason: not valid java name */
    public /* synthetic */ void m866xfd410a(Callback callback2, View view) {
        dismiss();
        callback2.toggleSound();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatNotificationsPopupWrapper  reason: not valid java name */
    public /* synthetic */ void m867x8dea5829(Callback callback2, View view) {
        dismiss();
        callback2.muteFor(this.muteForLastSelected1Time);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ChatNotificationsPopupWrapper  reason: not valid java name */
    public /* synthetic */ void m868x1ad76var_(Callback callback2, View view) {
        dismiss();
        callback2.muteFor(this.muteForLastSelected2Time);
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-ChatNotificationsPopupWrapper  reason: not valid java name */
    public /* synthetic */ void m869xCLASSNAMEeb4a5(Context context, Theme.ResourcesProvider resourcesProvider, int currentAccount2, Callback callback2, View view) {
        dismiss();
        AlertsCreator.createMuteForPickerDialog(context, resourcesProvider, new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda1(currentAccount2, callback2));
    }

    static /* synthetic */ void lambda$new$4(int inSecond, int currentAccount2, Callback callback2) {
        if (inSecond != 0) {
            SharedPreferences sharedPreferences = MessagesController.getNotificationsSettings(currentAccount2);
            sharedPreferences.edit().putInt("last_selected_mute_until_time", inSecond).putInt("last_selected_mute_until_time2", sharedPreferences.getInt("last_selected_mute_until_time", 0)).apply();
        }
        callback2.muteFor(inSecond);
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-ChatNotificationsPopupWrapper  reason: not valid java name */
    public /* synthetic */ void m870x4e8bcbc4(Callback callback2, View view) {
        dismiss();
        callback2.showCustomize();
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-ChatNotificationsPopupWrapper  reason: not valid java name */
    public /* synthetic */ void m871x6865fa02(Callback callback2, View view) {
        dismiss();
        AndroidUtilities.runOnUIThread(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda9(callback2));
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
    public void m872x80790d7d(long dialogId) {
        int color;
        int time1;
        int time12;
        if (System.currentTimeMillis() - this.lastDismissTime < 200) {
            AndroidUtilities.runOnUIThread(new ChatNotificationsPopupWrapper$$ExternalSyntheticLambda10(this, dialogId));
            return;
        }
        boolean muted = MessagesController.getInstance(this.currentAccount).isDialogMuted(dialogId);
        if (muted) {
            this.muteUnmuteButton.setTextAndIcon(LocaleController.getString("UnmuteNotifications", NUM), NUM);
            color = Theme.getColor("wallet_greenText");
            this.soundToggle.setVisibility(8);
        } else {
            this.muteUnmuteButton.setTextAndIcon(LocaleController.getString("MuteNotifications", NUM), NUM);
            color = Theme.getColor("dialogTextRed");
            this.soundToggle.setVisibility(0);
            if (MessagesController.getInstance(this.currentAccount).isDialogNotificationsSoundEnabled(dialogId)) {
                this.soundToggle.setTextAndIcon(LocaleController.getString("SoundOff", NUM), NUM);
            } else {
                this.soundToggle.setTextAndIcon(LocaleController.getString("SoundOn", NUM), NUM);
            }
        }
        if (muted) {
            time12 = 0;
            time1 = 0;
        } else {
            SharedPreferences sharedPreferences = MessagesController.getNotificationsSettings(this.currentAccount);
            int time13 = sharedPreferences.getInt("last_selected_mute_until_time", 0);
            int i = sharedPreferences.getInt("last_selected_mute_until_time2", 0);
            time12 = time13;
            time1 = i;
        }
        if (time12 != 0) {
            this.muteForLastSelected1Time = time12;
            this.muteForLastSelected.setVisibility(0);
            this.muteForLastSelected.getImageView().setImageDrawable(TimerDrawable.getTtlIcon(time12));
            this.muteForLastSelected.setText(formatMuteForTime(time12));
        } else {
            this.muteForLastSelected.setVisibility(8);
        }
        if (time1 != 0) {
            this.muteForLastSelected2Time = time1;
            this.muteForLastSelected2.setVisibility(0);
            this.muteForLastSelected2.getImageView().setImageDrawable(TimerDrawable.getTtlIcon(time1));
            this.muteForLastSelected2.setText(formatMuteForTime(time1));
        } else {
            this.muteForLastSelected2.setVisibility(8);
        }
        this.muteUnmuteButton.setColors(color, color);
    }

    private String formatMuteForTime(int time) {
        StringBuilder stringBuilder = new StringBuilder();
        int days = time / 86400;
        int hours = (time - (86400 * days)) / 3600;
        if (days != 0) {
            stringBuilder.append(days);
            stringBuilder.append(LocaleController.getString("SecretChatTimerDays", NUM));
        }
        if (hours != 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(hours);
            stringBuilder.append(LocaleController.getString("SecretChatTimerHours", NUM));
        }
        return LocaleController.formatString("MuteForButton", NUM, stringBuilder.toString());
    }

    /* JADX WARNING: type inference failed for: r3v15, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showAsOptions(org.telegram.ui.ActionBar.BaseFragment r9, android.view.View r10, float r11, float r12) {
        /*
            r8 = this;
            if (r9 == 0) goto L_0x009f
            android.view.View r0 = r9.getFragmentView()
            if (r0 != 0) goto L_0x000a
            goto L_0x009f
        L_0x000a:
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = new org.telegram.ui.ActionBar.ActionBarPopupWindow
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r1 = r8.windowLayout
            r2 = -2
            r0.<init>(r1, r2, r2)
            r8.popupWindow = r0
            r1 = 1
            r0.setPauseNotifications(r1)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r8.popupWindow
            r2 = 220(0xdc, float:3.08E-43)
            r0.setDismissAnimationDuration(r2)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r8.popupWindow
            r0.setOutsideTouchable(r1)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r8.popupWindow
            r0.setClippingEnabled(r1)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r8.popupWindow
            r2 = 2131689480(0x7f0var_, float:1.9007977E38)
            r0.setAnimationStyle(r2)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r8.popupWindow
            r0.setFocusable(r1)
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r0 = r8.windowLayout
            r2 = 1148846080(0x447a0000, float:1000.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r4 = -2147483648(0xfffffffvar_, float:-0.0)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r4)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r4)
            r0.measure(r3, r2)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r8.popupWindow
            r2 = 2
            r0.setInputMethodMode(r2)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r8.popupWindow
            android.view.View r0 = r0.getContentView()
            r0.setFocusableInTouchMode(r1)
            r0 = r11
            r1 = r12
            r2 = r10
        L_0x0061:
            android.view.View r3 = r9.getFragmentView()
            if (r2 == r3) goto L_0x0079
            float r3 = r2.getX()
            float r0 = r0 + r3
            float r3 = r2.getY()
            float r1 = r1 + r3
            android.view.ViewParent r3 = r2.getParent()
            r2 = r3
            android.view.View r2 = (android.view.View) r2
            goto L_0x0061
        L_0x0079:
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r3 = r8.windowLayout
            int r3 = r3.getMeasuredWidth()
            float r3 = (float) r3
            r4 = 1073741824(0x40000000, float:2.0)
            float r3 = r3 / r4
            float r0 = r0 - r3
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r3 = r8.windowLayout
            int r3 = r3.getMeasuredHeight()
            float r3 = (float) r3
            float r3 = r3 / r4
            float r1 = r1 - r3
            org.telegram.ui.ActionBar.ActionBarPopupWindow r3 = r8.popupWindow
            android.view.View r4 = r9.getFragmentView()
            r5 = 0
            int r6 = (int) r0
            int r7 = (int) r1
            r3.showAtLocation(r4, r5, r6, r7)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r3 = r8.popupWindow
            r3.dimBehind()
            return
        L_0x009f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatNotificationsPopupWrapper.showAsOptions(org.telegram.ui.ActionBar.BaseFragment, android.view.View, float, float):void");
    }

    public interface Callback {
        void dismiss();

        void muteFor(int i);

        void showCustomize();

        void toggleMute();

        void toggleSound();

        /* renamed from: org.telegram.ui.Components.ChatNotificationsPopupWrapper$Callback$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$dismiss(Callback _this) {
            }
        }
    }
}
