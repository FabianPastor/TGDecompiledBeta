package org.telegram.ui.Components;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.ProfileActivity;

public class MediaActivity extends BaseFragment implements SharedMediaLayout.SharedMediaPreloaderDelegate {
    ProfileActivity.AvatarImageView avatarImageView;
    private TLRPC.ChatFull currentChatInfo;
    private long dialogId;
    AudioPlayerAlert.ClippingTextViewSwitcher mediaCounterTextView;
    /* access modifiers changed from: private */
    public SimpleTextView nameTextView;
    SharedMediaLayout sharedMediaLayout;
    private SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader;

    public MediaActivity(Bundle args, SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader2) {
        super(args);
        this.sharedMediaPreloader = sharedMediaPreloader2;
    }

    public boolean onFragmentCreate() {
        this.dialogId = getArguments().getLong("dialog_id");
        if (this.sharedMediaPreloader == null) {
            SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader2 = new SharedMediaLayout.SharedMediaPreloader(this);
            this.sharedMediaPreloader = sharedMediaPreloader2;
            sharedMediaPreloader2.addDelegate(this);
        }
        return super.onFragmentCreate();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v4, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v13, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v32, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v8, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r26) {
        /*
            r25 = this;
            r15 = r25
            r14 = r26
            org.telegram.ui.ActionBar.ActionBar r0 = r15.actionBar
            org.telegram.ui.ActionBar.BackDrawable r1 = new org.telegram.ui.ActionBar.BackDrawable
            r13 = 0
            r1.<init>(r13)
            r0.setBackButtonDrawable(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r15.actionBar
            r0.setCastShadows(r13)
            org.telegram.ui.ActionBar.ActionBar r0 = r15.actionBar
            r0.setAddToContainer(r13)
            org.telegram.ui.ActionBar.ActionBar r0 = r15.actionBar
            org.telegram.ui.Components.MediaActivity$1 r1 = new org.telegram.ui.Components.MediaActivity$1
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r14)
            r12 = r0
            org.telegram.ui.Components.MediaActivity$2 r0 = new org.telegram.ui.Components.MediaActivity$2
            r0.<init>(r14, r12)
            r11 = r0
            r10 = 1
            r11.needBlur = r10
            r15.fragmentView = r11
            org.telegram.ui.ActionBar.SimpleTextView r0 = new org.telegram.ui.ActionBar.SimpleTextView
            r0.<init>(r14)
            r15.nameTextView = r0
            r1 = 18
            r0.setTextSize(r1)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r15.nameTextView
            r1 = 3
            r0.setGravity(r1)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r15.nameTextView
            java.lang.String r1 = "fonts/rmedium.ttf"
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r1)
            r0.setTypeface(r1)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r15.nameTextView
            r1 = 1067869798(0x3fa66666, float:1.3)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            r0.setLeftDrawableTopPadding(r1)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r15.nameTextView
            r0.setScrollNonFitText(r10)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r15.nameTextView
            r1 = 2
            r0.setImportantForAccessibility(r1)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r15.nameTextView
            r1 = -2
            r2 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r3 = 51
            r4 = 1122762752(0x42eCLASSNAME, float:118.0)
            r5 = 0
            r6 = 1113587712(0x42600000, float:56.0)
            r7 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r2, r3, r4, r5, r6, r7)
            r12.addView(r0, r1)
            org.telegram.ui.Components.MediaActivity$3 r0 = new org.telegram.ui.Components.MediaActivity$3
            r0.<init>(r14)
            r15.avatarImageView = r0
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            r0.setAllowDecodeSingleFrame(r10)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r15.avatarImageView
            r1 = 1101529088(0x41a80000, float:21.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setRoundRadius(r1)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r15.avatarImageView
            r9 = 0
            r0.setPivotX(r9)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r15.avatarImageView
            r0.setPivotY(r9)
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r8 = r0
            r8.setProfile(r10)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r15.avatarImageView
            r0.setImageDrawable(r8)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r15.avatarImageView
            r1 = 42
            r2 = 1109917696(0x42280000, float:42.0)
            r4 = 1115684864(0x42800000, float:64.0)
            r6 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r2, r3, r4, r5, r6, r7)
            r12.addView(r0, r1)
            org.telegram.ui.Components.MediaActivity$4 r0 = new org.telegram.ui.Components.MediaActivity$4
            r0.<init>(r14, r14)
            r15.mediaCounterTextView = r0
            r1 = -2
            r2 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r4 = 1122762752(0x42eCLASSNAME, float:118.0)
            r6 = 1113587712(0x42600000, float:56.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r2, r3, r4, r5, r6, r7)
            r12.addView(r0, r1)
            org.telegram.ui.Components.MediaActivity$6 r7 = new org.telegram.ui.Components.MediaActivity$6
            long r3 = r15.dialogId
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r5 = r15.sharedMediaPreloader
            org.telegram.tgnet.TLRPC$ChatFull r6 = r15.currentChatInfo
            org.telegram.ui.Components.MediaActivity$5 r2 = new org.telegram.ui.Components.MediaActivity$5
            r2.<init>()
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r16 = r25.getResourceProvider()
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r0 = r7
            r1 = r25
            r21 = r2
            r2 = r26
            r22 = r6
            r6 = r17
            r23 = r7
            r7 = r18
            r24 = r8
            r8 = r22
            r9 = r19
            r10 = r25
            r17 = r11
            r11 = r21
            r18 = r12
            r12 = r20
            r13 = r16
            r14 = r18
            r15 = r17
            r0.<init>(r1, r2, r3, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15)
            r0 = r25
            r1 = r23
            r0.sharedMediaLayout = r1
            r2 = 1
            r1.setPinnedToTop(r2)
            org.telegram.ui.Components.SharedMediaLayout r1 = r0.sharedMediaLayout
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.getSearchItem()
            r3 = 0
            r1.setTranslationY(r3)
            org.telegram.ui.Components.SharedMediaLayout r1 = r0.sharedMediaLayout
            android.widget.ImageView r1 = r1.photoVideoOptionsItem
            r1.setTranslationY(r3)
            org.telegram.ui.Components.SharedMediaLayout r1 = r0.sharedMediaLayout
            r3 = r17
            r3.addView(r1)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r3.addView(r1)
            r1 = r18
            r3.addView(r1)
            java.util.ArrayList<android.view.View> r4 = r3.blurBehindViews
            org.telegram.ui.Components.SharedMediaLayout r5 = r0.sharedMediaLayout
            r4.add(r5)
            r4 = 0
            long r5 = r0.dialogId
            boolean r5 = org.telegram.messenger.DialogObject.isEncryptedDialog(r5)
            if (r5 == 0) goto L_0x018f
            org.telegram.messenger.MessagesController r5 = r25.getMessagesController()
            long r6 = r0.dialogId
            int r6 = org.telegram.messenger.DialogObject.getEncryptedChatId(r6)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$EncryptedChat r5 = r5.getEncryptedChat(r6)
            if (r5 == 0) goto L_0x018c
            org.telegram.messenger.MessagesController r6 = r25.getMessagesController()
            long r7 = r5.user_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            if (r6 == 0) goto L_0x0189
            org.telegram.ui.ActionBar.SimpleTextView r7 = r0.nameTextView
            java.lang.String r8 = r6.first_name
            java.lang.String r9 = r6.last_name
            java.lang.String r8 = org.telegram.messenger.ContactsController.formatName(r8, r9)
            r7.setText(r8)
            r7 = r24
            r7.setInfo((org.telegram.tgnet.TLRPC.User) r6)
            r4 = r6
            goto L_0x018e
        L_0x0189:
            r7 = r24
            goto L_0x018e
        L_0x018c:
            r7 = r24
        L_0x018e:
            goto L_0x01f4
        L_0x018f:
            r7 = r24
            long r5 = r0.dialogId
            boolean r5 = org.telegram.messenger.DialogObject.isUserDialog(r5)
            if (r5 == 0) goto L_0x01d6
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            long r8 = r0.dialogId
            java.lang.Long r6 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            if (r5 == 0) goto L_0x01d5
            boolean r6 = r5.self
            if (r6 == 0) goto L_0x01c4
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.nameTextView
            r8 = 2131628077(0x7f0e102d, float:1.8883436E38)
            java.lang.String r9 = "SavedMessages"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r6.setText(r8)
            r7.setAvatarType(r2)
            r7.setSmallSize(r2)
            goto L_0x01d5
        L_0x01c4:
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.nameTextView
            java.lang.String r8 = r5.first_name
            java.lang.String r9 = r5.last_name
            java.lang.String r8 = org.telegram.messenger.ContactsController.formatName(r8, r9)
            r6.setText(r8)
            r7.setInfo((org.telegram.tgnet.TLRPC.User) r5)
            r4 = r5
        L_0x01d5:
            goto L_0x01f4
        L_0x01d6:
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            long r8 = r0.dialogId
            long r8 = -r8
            java.lang.Long r6 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r6)
            if (r5 == 0) goto L_0x01f4
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.nameTextView
            java.lang.String r8 = r5.title
            r6.setText(r8)
            r7.setInfo((org.telegram.tgnet.TLRPC.Chat) r5)
            r4 = r5
        L_0x01f4:
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForUserOrChat(r4, r2)
            org.telegram.ui.ProfileActivity$AvatarImageView r6 = r0.avatarImageView
            java.lang.String r8 = "50_50"
            r6.setImage((org.telegram.messenger.ImageLocation) r5, (java.lang.String) r8, (android.graphics.drawable.Drawable) r7, (java.lang.Object) r4)
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.nameTextView
            java.lang.CharSequence r6 = r6.getText()
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x0219
            org.telegram.ui.ActionBar.SimpleTextView r6 = r0.nameTextView
            r8 = 2131628296(0x7f0e1108, float:1.888388E38)
            java.lang.String r9 = "SharedContentTitle"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r6.setText(r8)
        L_0x0219:
            org.telegram.ui.Components.SharedMediaLayout r6 = r0.sharedMediaLayout
            boolean r6 = r6.isSearchItemVisible()
            if (r6 == 0) goto L_0x022c
            org.telegram.ui.Components.SharedMediaLayout r6 = r0.sharedMediaLayout
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r6.getSearchItem()
            r8 = 0
            r6.setVisibility(r8)
            goto L_0x022d
        L_0x022c:
            r8 = 0
        L_0x022d:
            org.telegram.ui.Components.SharedMediaLayout r6 = r0.sharedMediaLayout
            boolean r6 = r6.isCalendarItemVisible()
            if (r6 == 0) goto L_0x023d
            org.telegram.ui.Components.SharedMediaLayout r6 = r0.sharedMediaLayout
            android.widget.ImageView r6 = r6.photoVideoOptionsItem
            r6.setVisibility(r8)
            goto L_0x0245
        L_0x023d:
            org.telegram.ui.Components.SharedMediaLayout r6 = r0.sharedMediaLayout
            android.widget.ImageView r6 = r6.photoVideoOptionsItem
            r9 = 4
            r6.setVisibility(r9)
        L_0x0245:
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            r6.setDrawBlurBackground(r3)
            r6 = 1065353216(0x3var_, float:1.0)
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r1, r2, r6, r8)
            r25.updateMediaCount()
            r25.m1111x87fdda83()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MediaActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    public void updateMediaCount() {
        int id = this.sharedMediaLayout.getClosestTab();
        int[] mediaCount = this.sharedMediaPreloader.getLastMediaCount();
        if (id >= 0 && mediaCount[id] >= 0) {
            if (id == 0) {
                if (this.sharedMediaLayout.getPhotosVideosTypeFilter() == 1) {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Photos", mediaCount[6], new Object[0]));
                } else if (this.sharedMediaLayout.getPhotosVideosTypeFilter() == 2) {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Videos", mediaCount[7], new Object[0]));
                } else {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Media", mediaCount[0], new Object[0]));
                }
            } else if (id == 1) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Files", mediaCount[1], new Object[0]));
            } else if (id == 2) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Voice", mediaCount[2], new Object[0]));
            } else if (id == 3) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Links", mediaCount[3], new Object[0]));
            } else if (id == 4) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("MusicFiles", mediaCount[4], new Object[0]));
            } else if (id == 5) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("GIFs", mediaCount[5], new Object[0]));
            }
        }
    }

    public void setChatInfo(TLRPC.ChatFull currentChatInfo2) {
        this.currentChatInfo = currentChatInfo2;
    }

    public long getDialogId() {
        return this.dialogId;
    }

    public void mediaCountUpdated() {
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader2;
        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
        if (!(sharedMediaLayout2 == null || (sharedMediaPreloader2 = this.sharedMediaPreloader) == null)) {
            sharedMediaLayout2.setNewMediaCounts(sharedMediaPreloader2.getLastMediaCount());
        }
        updateMediaCount();
    }

    /* access modifiers changed from: private */
    /* renamed from: updateColors */
    public void m1111x87fdda83() {
        this.actionBar.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarActionModeDefaultSelector"), false);
        this.actionBar.setTitleColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate themeDelegate = new MediaActivity$$ExternalSyntheticLambda0(this);
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDelegate, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDelegate, "actionBarActionModeDefaultSelector"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDelegate, "windowBackgroundWhiteBlackText"));
        arrayList.addAll(this.sharedMediaLayout.getThemeDescriptions());
        return arrayList;
    }

    public boolean isLightStatusBar() {
        int color = Theme.getColor("windowBackgroundWhite");
        if (this.actionBar.isActionModeShowed()) {
            color = Theme.getColor("actionBarActionModeDefault");
        }
        return ColorUtils.calculateLuminance(color) > 0.699999988079071d;
    }
}
