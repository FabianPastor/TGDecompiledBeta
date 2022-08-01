package org.telegram.ui.Components;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.ProfileActivity;

public class MediaActivity extends BaseFragment implements SharedMediaLayout.SharedMediaPreloaderDelegate {
    ProfileActivity.AvatarImageView avatarImageView;
    private TLRPC$ChatFull currentChatInfo;
    private long dialogId;
    AudioPlayerAlert.ClippingTextViewSwitcher mediaCounterTextView;
    /* access modifiers changed from: private */
    public SimpleTextView nameTextView;
    SharedMediaLayout sharedMediaLayout;
    private SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader;

    public MediaActivity(Bundle bundle, SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader2) {
        super(bundle);
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v21, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v34, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v35, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v36, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0209  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x021f  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x022a  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0233  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x023b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r26) {
        /*
            r25 = this;
            r15 = r25
            r2 = r26
            org.telegram.ui.ActionBar.ActionBar r0 = r15.actionBar
            org.telegram.ui.ActionBar.BackDrawable r1 = new org.telegram.ui.ActionBar.BackDrawable
            r14 = 0
            r1.<init>(r14)
            r0.setBackButtonDrawable(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r15.actionBar
            r0.setCastShadows(r14)
            org.telegram.ui.ActionBar.ActionBar r0 = r15.actionBar
            r0.setAddToContainer(r14)
            org.telegram.ui.ActionBar.ActionBar r0 = r15.actionBar
            org.telegram.ui.Components.MediaActivity$1 r1 = new org.telegram.ui.Components.MediaActivity$1
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            android.widget.FrameLayout r13 = new android.widget.FrameLayout
            r13.<init>(r2)
            org.telegram.ui.Components.MediaActivity$2 r12 = new org.telegram.ui.Components.MediaActivity$2
            r12.<init>(r2, r13)
            r11 = 1
            r12.needBlur = r11
            r15.fragmentView = r12
            org.telegram.ui.ActionBar.SimpleTextView r0 = new org.telegram.ui.ActionBar.SimpleTextView
            r0.<init>(r2)
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
            r0.setScrollNonFitText(r11)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r15.nameTextView
            r1 = 2
            r0.setImportantForAccessibility(r1)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r15.nameTextView
            r3 = -2
            r4 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r5 = 51
            r6 = 1122762752(0x42eCLASSNAME, float:118.0)
            r7 = 0
            r8 = 1113587712(0x42600000, float:56.0)
            r9 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r5, r6, r7, r8, r9)
            r13.addView(r0, r1)
            org.telegram.ui.Components.MediaActivity$3 r0 = new org.telegram.ui.Components.MediaActivity$3
            r0.<init>(r15, r2)
            r15.avatarImageView = r0
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            r0.setAllowDecodeSingleFrame(r11)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r15.avatarImageView
            r1 = 1101529088(0x41a80000, float:21.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setRoundRadius(r1)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r15.avatarImageView
            r10 = 0
            r0.setPivotX(r10)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r15.avatarImageView
            r0.setPivotY(r10)
            org.telegram.ui.Components.AvatarDrawable r9 = new org.telegram.ui.Components.AvatarDrawable
            r9.<init>()
            r9.setProfile(r11)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r15.avatarImageView
            r0.setImageDrawable(r9)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r15.avatarImageView
            r16 = 42
            r17 = 1109917696(0x42280000, float:42.0)
            r18 = 51
            r19 = 1115684864(0x42800000, float:64.0)
            r20 = 0
            r21 = 0
            r22 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r13.addView(r0, r1)
            org.telegram.ui.Components.MediaActivity$4 r0 = new org.telegram.ui.Components.MediaActivity$4
            r0.<init>(r15, r2, r2)
            r15.mediaCounterTextView = r0
            r16 = -2
            r17 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r19 = 1122762752(0x42eCLASSNAME, float:118.0)
            r21 = 1113587712(0x42600000, float:56.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r13.addView(r0, r1)
            org.telegram.ui.Components.MediaActivity$6 r8 = new org.telegram.ui.Components.MediaActivity$6
            long r3 = r15.dialogId
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r5 = r15.sharedMediaPreloader
            org.telegram.tgnet.TLRPC$ChatFull r7 = r15.currentChatInfo
            org.telegram.ui.Components.MediaActivity$5 r6 = new org.telegram.ui.Components.MediaActivity$5
            r6.<init>()
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r16 = r25.getResourceProvider()
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r0 = r8
            r1 = r25
            r21 = r6
            r6 = r17
            r17 = r7
            r7 = r18
            r23 = r8
            r8 = r17
            r24 = r9
            r9 = r19
            r10 = r25
            r11 = r21
            r26 = r12
            r12 = r20
            r17 = r13
            r13 = r16
            r14 = r17
            r15 = r26
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
            r3 = r26
            r3.addView(r1)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r3.addView(r1)
            r1 = r17
            r3.addView(r1)
            java.util.ArrayList<android.view.View> r4 = r3.blurBehindViews
            org.telegram.ui.Components.SharedMediaLayout r5 = r0.sharedMediaLayout
            r4.add(r5)
            long r4 = r0.dialogId
            boolean r4 = org.telegram.messenger.DialogObject.isEncryptedDialog(r4)
            r5 = 0
            if (r4 == 0) goto L_0x018e
            org.telegram.messenger.MessagesController r4 = r25.getMessagesController()
            long r6 = r0.dialogId
            int r6 = org.telegram.messenger.DialogObject.getEncryptedChatId(r6)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$EncryptedChat r4 = r4.getEncryptedChat(r6)
            if (r4 == 0) goto L_0x018b
            org.telegram.messenger.MessagesController r6 = r25.getMessagesController()
            long r7 = r4.user_id
            java.lang.Long r4 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r4 = r6.getUser(r4)
            if (r4 == 0) goto L_0x018b
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.nameTextView
            java.lang.String r6 = r4.first_name
            java.lang.String r7 = r4.last_name
            java.lang.String r6 = org.telegram.messenger.ContactsController.formatName(r6, r7)
            r5.setText(r6)
            r6 = r24
            r6.setInfo((org.telegram.tgnet.TLRPC$User) r4)
            goto L_0x01f1
        L_0x018b:
            r6 = r24
            goto L_0x01f2
        L_0x018e:
            r6 = r24
            long r7 = r0.dialogId
            boolean r4 = org.telegram.messenger.DialogObject.isUserDialog(r7)
            if (r4 == 0) goto L_0x01d4
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            long r7 = r0.dialogId
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r7)
            if (r4 == 0) goto L_0x01f2
            boolean r7 = r4.self
            if (r7 == 0) goto L_0x01c3
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.nameTextView
            r7 = 2131628153(0x7f0e1079, float:1.888359E38)
            java.lang.String r8 = "SavedMessages"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r4.setText(r7)
            r6.setAvatarType(r2)
            r6.setSmallSize(r2)
            goto L_0x01f2
        L_0x01c3:
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.nameTextView
            java.lang.String r7 = r4.first_name
            java.lang.String r8 = r4.last_name
            java.lang.String r7 = org.telegram.messenger.ContactsController.formatName(r7, r8)
            r5.setText(r7)
            r6.setInfo((org.telegram.tgnet.TLRPC$User) r4)
            goto L_0x01f1
        L_0x01d4:
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            long r7 = r0.dialogId
            long r7 = -r7
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r7)
            if (r4 == 0) goto L_0x01f2
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.nameTextView
            java.lang.String r7 = r4.title
            r5.setText(r7)
            r6.setInfo((org.telegram.tgnet.TLRPC$Chat) r4)
        L_0x01f1:
            r5 = r4
        L_0x01f2:
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForUserOrChat(r5, r2)
            org.telegram.ui.ProfileActivity$AvatarImageView r7 = r0.avatarImageView
            java.lang.String r8 = "50_50"
            r7.setImage((org.telegram.messenger.ImageLocation) r4, (java.lang.String) r8, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r5)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.nameTextView
            java.lang.CharSequence r4 = r4.getText()
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x0217
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.nameTextView
            r5 = 2131628372(0x7f0e1154, float:1.8884035E38)
            java.lang.String r6 = "SharedContentTitle"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r4.setText(r5)
        L_0x0217:
            org.telegram.ui.Components.SharedMediaLayout r4 = r0.sharedMediaLayout
            boolean r4 = r4.isSearchItemVisible()
            if (r4 == 0) goto L_0x022a
            org.telegram.ui.Components.SharedMediaLayout r4 = r0.sharedMediaLayout
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r4.getSearchItem()
            r5 = 0
            r4.setVisibility(r5)
            goto L_0x022b
        L_0x022a:
            r5 = 0
        L_0x022b:
            org.telegram.ui.Components.SharedMediaLayout r4 = r0.sharedMediaLayout
            boolean r4 = r4.isCalendarItemVisible()
            if (r4 == 0) goto L_0x023b
            org.telegram.ui.Components.SharedMediaLayout r4 = r0.sharedMediaLayout
            android.widget.ImageView r4 = r4.photoVideoOptionsItem
            r4.setVisibility(r5)
            goto L_0x0243
        L_0x023b:
            org.telegram.ui.Components.SharedMediaLayout r4 = r0.sharedMediaLayout
            android.widget.ImageView r4 = r4.photoVideoOptionsItem
            r6 = 4
            r4.setVisibility(r6)
        L_0x0243:
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            r4.setDrawBlurBackground(r3)
            r4 = 1065353216(0x3var_, float:1.0)
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r1, r2, r4, r5)
            r25.updateMediaCount()
            r25.lambda$getThemeDescriptions$0()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MediaActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    public void updateMediaCount() {
        int closestTab = this.sharedMediaLayout.getClosestTab();
        int[] lastMediaCount = this.sharedMediaPreloader.getLastMediaCount();
        if (closestTab >= 0 && lastMediaCount[closestTab] >= 0) {
            if (closestTab == 0) {
                if (this.sharedMediaLayout.getPhotosVideosTypeFilter() == 1) {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Photos", lastMediaCount[6], new Object[0]));
                } else if (this.sharedMediaLayout.getPhotosVideosTypeFilter() == 2) {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Videos", lastMediaCount[7], new Object[0]));
                } else {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Media", lastMediaCount[0], new Object[0]));
                }
            } else if (closestTab == 1) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Files", lastMediaCount[1], new Object[0]));
            } else if (closestTab == 2) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Voice", lastMediaCount[2], new Object[0]));
            } else if (closestTab == 3) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Links", lastMediaCount[3], new Object[0]));
            } else if (closestTab == 4) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("MusicFiles", lastMediaCount[4], new Object[0]));
            } else if (closestTab == 5) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("GIFs", lastMediaCount[5], new Object[0]));
            }
        }
    }

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.currentChatInfo = tLRPC$ChatFull;
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
    public void lambda$getThemeDescriptions$0() {
        this.actionBar.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarActionModeDefaultSelector"), false);
        this.actionBar.setTitleColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        MediaActivity$$ExternalSyntheticLambda0 mediaActivity$$ExternalSyntheticLambda0 = new MediaActivity$$ExternalSyntheticLambda0(this);
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        MediaActivity$$ExternalSyntheticLambda0 mediaActivity$$ExternalSyntheticLambda02 = mediaActivity$$ExternalSyntheticLambda0;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, mediaActivity$$ExternalSyntheticLambda02, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, mediaActivity$$ExternalSyntheticLambda02, "actionBarActionModeDefaultSelector"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, mediaActivity$$ExternalSyntheticLambda02, "windowBackgroundWhiteBlackText"));
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
