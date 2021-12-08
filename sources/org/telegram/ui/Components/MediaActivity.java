package org.telegram.ui.Components;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v13, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v28, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v6, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r26) {
        /*
            r25 = this;
            r14 = r25
            r15 = r26
            org.telegram.ui.ActionBar.ActionBar r0 = r14.actionBar
            org.telegram.ui.ActionBar.BackDrawable r1 = new org.telegram.ui.ActionBar.BackDrawable
            r13 = 0
            r1.<init>(r13)
            r0.setBackButtonDrawable(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r14.actionBar
            r0.setCastShadows(r13)
            org.telegram.ui.ActionBar.ActionBar r0 = r14.actionBar
            r0.setAddToContainer(r13)
            org.telegram.ui.ActionBar.ActionBar r0 = r14.actionBar
            org.telegram.ui.Components.MediaActivity$1 r1 = new org.telegram.ui.Components.MediaActivity$1
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r15)
            r12 = r0
            org.telegram.ui.Components.MediaActivity$2 r0 = new org.telegram.ui.Components.MediaActivity$2
            r0.<init>(r15, r12)
            r11 = r0
            r14.fragmentView = r11
            org.telegram.ui.ActionBar.SimpleTextView r0 = new org.telegram.ui.ActionBar.SimpleTextView
            r0.<init>(r15)
            r14.nameTextView = r0
            r1 = 18
            r0.setTextSize(r1)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r14.nameTextView
            r1 = 3
            r0.setGravity(r1)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r14.nameTextView
            java.lang.String r1 = "fonts/rmedium.ttf"
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r1)
            r0.setTypeface(r1)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r14.nameTextView
            r1 = 1067869798(0x3fa66666, float:1.3)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            r0.setLeftDrawableTopPadding(r1)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r14.nameTextView
            r10 = 1
            r0.setScrollNonFitText(r10)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r14.nameTextView
            r1 = 2
            r0.setImportantForAccessibility(r1)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r14.nameTextView
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
            r0.<init>(r15)
            r14.avatarImageView = r0
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            r0.setAllowDecodeSingleFrame(r10)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r14.avatarImageView
            r1 = 1101529088(0x41a80000, float:21.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setRoundRadius(r1)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r14.avatarImageView
            r9 = 0
            r0.setPivotX(r9)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r14.avatarImageView
            r0.setPivotY(r9)
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r8 = r0
            r8.setProfile(r10)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r14.avatarImageView
            r0.setImageDrawable(r8)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r14.avatarImageView
            r1 = 42
            r2 = 1109917696(0x42280000, float:42.0)
            r4 = 1115684864(0x42800000, float:64.0)
            r6 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r2, r3, r4, r5, r6, r7)
            r12.addView(r0, r1)
            org.telegram.ui.Components.MediaActivity$4 r0 = new org.telegram.ui.Components.MediaActivity$4
            r0.<init>(r15, r15)
            r14.mediaCounterTextView = r0
            r1 = -2
            r2 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r4 = 1122762752(0x42eCLASSNAME, float:118.0)
            r6 = 1113587712(0x42600000, float:56.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r2, r3, r4, r5, r6, r7)
            r12.addView(r0, r1)
            org.telegram.ui.Components.MediaActivity$6 r7 = new org.telegram.ui.Components.MediaActivity$6
            long r3 = r14.dialogId
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r5 = r14.sharedMediaPreloader
            org.telegram.tgnet.TLRPC$ChatFull r6 = r14.currentChatInfo
            org.telegram.ui.Components.MediaActivity$5 r2 = new org.telegram.ui.Components.MediaActivity$5
            r2.<init>()
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r0 = r7
            r1 = r25
            r20 = r2
            r2 = r26
            r21 = r6
            r6 = r16
            r22 = r7
            r7 = r17
            r23 = r8
            r8 = r21
            r9 = r18
            r10 = r25
            r24 = r11
            r11 = r20
            r16 = r12
            r12 = r19
            r15 = 0
            r13 = r16
            r0.<init>(r1, r2, r3, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            r0 = r22
            r14.sharedMediaLayout = r0
            r1 = 1
            r0.setPinnedToTop(r1)
            org.telegram.ui.Components.SharedMediaLayout r0 = r14.sharedMediaLayout
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.getSearchItem()
            r2 = 0
            r0.setTranslationY(r2)
            org.telegram.ui.Components.SharedMediaLayout r0 = r14.sharedMediaLayout
            android.widget.ImageView r0 = r0.photoVideoOptionsItem
            r0.setTranslationY(r2)
            org.telegram.ui.Components.SharedMediaLayout r0 = r14.sharedMediaLayout
            r2 = r24
            r2.addView(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r14.actionBar
            r2.addView(r0)
            r0 = r16
            r2.addView(r0)
            r3 = 0
            long r4 = r14.dialogId
            boolean r4 = org.telegram.messenger.DialogObject.isEncryptedDialog(r4)
            if (r4 == 0) goto L_0x017d
            org.telegram.messenger.MessagesController r4 = r25.getMessagesController()
            long r5 = r14.dialogId
            int r5 = org.telegram.messenger.DialogObject.getEncryptedChatId(r5)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$EncryptedChat r4 = r4.getEncryptedChat(r5)
            if (r4 == 0) goto L_0x017a
            org.telegram.messenger.MessagesController r5 = r25.getMessagesController()
            long r6 = r4.user_id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            if (r5 == 0) goto L_0x0177
            org.telegram.ui.ActionBar.SimpleTextView r6 = r14.nameTextView
            java.lang.String r7 = r5.first_name
            java.lang.String r8 = r5.last_name
            java.lang.String r7 = org.telegram.messenger.ContactsController.formatName(r7, r8)
            r6.setText(r7)
            r6 = r23
            r6.setInfo((org.telegram.tgnet.TLRPC.User) r5)
            r3 = r5
            goto L_0x017c
        L_0x0177:
            r6 = r23
            goto L_0x017c
        L_0x017a:
            r6 = r23
        L_0x017c:
            goto L_0x01e2
        L_0x017d:
            r6 = r23
            long r4 = r14.dialogId
            boolean r4 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            if (r4 == 0) goto L_0x01c4
            int r4 = r14.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            long r7 = r14.dialogId
            java.lang.Long r5 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            if (r4 == 0) goto L_0x01c3
            boolean r5 = r4.self
            if (r5 == 0) goto L_0x01b2
            org.telegram.ui.ActionBar.SimpleTextView r5 = r14.nameTextView
            r7 = 2131627603(0x7f0e0e53, float:1.8882475E38)
            java.lang.String r8 = "SavedMessages"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r5.setText(r7)
            r6.setAvatarType(r1)
            r6.setSmallSize(r1)
            goto L_0x01c3
        L_0x01b2:
            org.telegram.ui.ActionBar.SimpleTextView r5 = r14.nameTextView
            java.lang.String r7 = r4.first_name
            java.lang.String r8 = r4.last_name
            java.lang.String r7 = org.telegram.messenger.ContactsController.formatName(r7, r8)
            r5.setText(r7)
            r6.setInfo((org.telegram.tgnet.TLRPC.User) r4)
            r3 = r4
        L_0x01c3:
            goto L_0x01e2
        L_0x01c4:
            int r4 = r14.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            long r7 = r14.dialogId
            long r7 = -r7
            java.lang.Long r5 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r5)
            if (r4 == 0) goto L_0x01e2
            org.telegram.ui.ActionBar.SimpleTextView r5 = r14.nameTextView
            java.lang.String r7 = r4.title
            r5.setText(r7)
            r6.setInfo((org.telegram.tgnet.TLRPC.Chat) r4)
            r3 = r4
        L_0x01e2:
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForUserOrChat(r3, r1)
            org.telegram.ui.ProfileActivity$AvatarImageView r5 = r14.avatarImageView
            java.lang.String r7 = "50_50"
            r5.setImage((org.telegram.messenger.ImageLocation) r4, (java.lang.String) r7, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r3)
            org.telegram.ui.ActionBar.SimpleTextView r5 = r14.nameTextView
            java.lang.CharSequence r5 = r5.getText()
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x0207
            org.telegram.ui.ActionBar.SimpleTextView r5 = r14.nameTextView
            r7 = 2131627815(0x7f0e0var_, float:1.8882905E38)
            java.lang.String r8 = "SharedContentTitle"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r5.setText(r7)
        L_0x0207:
            org.telegram.ui.Components.SharedMediaLayout r5 = r14.sharedMediaLayout
            boolean r5 = r5.isSearchItemVisible()
            if (r5 == 0) goto L_0x0218
            org.telegram.ui.Components.SharedMediaLayout r5 = r14.sharedMediaLayout
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r5.getSearchItem()
            r5.setVisibility(r15)
        L_0x0218:
            org.telegram.ui.Components.SharedMediaLayout r5 = r14.sharedMediaLayout
            boolean r5 = r5.isCalendarItemVisible()
            if (r5 == 0) goto L_0x0228
            org.telegram.ui.Components.SharedMediaLayout r5 = r14.sharedMediaLayout
            android.widget.ImageView r5 = r5.photoVideoOptionsItem
            r5.setVisibility(r15)
            goto L_0x0230
        L_0x0228:
            org.telegram.ui.Components.SharedMediaLayout r5 = r14.sharedMediaLayout
            android.widget.ImageView r5 = r5.photoVideoOptionsItem
            r7 = 4
            r5.setVisibility(r7)
        L_0x0230:
            r5 = 1065353216(0x3var_, float:1.0)
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r0, r1, r5, r15)
            r25.updateMediaCount()
            r25.m2410x87fdda83()
            return r2
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
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Photos", mediaCount[6]));
                } else if (this.sharedMediaLayout.getPhotosVideosTypeFilter() == 2) {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Videos", mediaCount[7]));
                } else {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Media", mediaCount[0]));
                }
            } else if (id == 1) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Files", mediaCount[1]));
            } else if (id == 2) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Voice", mediaCount[2]));
            } else if (id == 3) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Links", mediaCount[3]));
            } else if (id == 4) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("MusicFiles", mediaCount[4]));
            } else if (id == 5) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("GIFs", mediaCount[5]));
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
    public void m2410x87fdda83() {
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
}
