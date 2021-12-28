package org.telegram.ui.Components;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v20, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v33, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v34, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v35, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x01f1  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0207  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0212  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x021b  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0223  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r26) {
        /*
            r25 = this;
            r14 = r25
            r2 = r26
            org.telegram.ui.ActionBar.ActionBar r0 = r14.actionBar
            org.telegram.ui.ActionBar.BackDrawable r1 = new org.telegram.ui.ActionBar.BackDrawable
            r15 = 0
            r1.<init>(r15)
            r0.setBackButtonDrawable(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r14.actionBar
            r0.setCastShadows(r15)
            org.telegram.ui.ActionBar.ActionBar r0 = r14.actionBar
            r0.setAddToContainer(r15)
            org.telegram.ui.ActionBar.ActionBar r0 = r14.actionBar
            org.telegram.ui.Components.MediaActivity$1 r1 = new org.telegram.ui.Components.MediaActivity$1
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            android.widget.FrameLayout r13 = new android.widget.FrameLayout
            r13.<init>(r2)
            org.telegram.ui.Components.MediaActivity$2 r12 = new org.telegram.ui.Components.MediaActivity$2
            r12.<init>(r2, r13)
            r14.fragmentView = r12
            org.telegram.ui.ActionBar.SimpleTextView r0 = new org.telegram.ui.ActionBar.SimpleTextView
            r0.<init>(r2)
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
            r11 = 1
            r0.setScrollNonFitText(r11)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r14.nameTextView
            r1 = 2
            r0.setImportantForAccessibility(r1)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r14.nameTextView
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
            r0.<init>(r14, r2)
            r14.avatarImageView = r0
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            r0.setAllowDecodeSingleFrame(r11)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r14.avatarImageView
            r1 = 1101529088(0x41a80000, float:21.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setRoundRadius(r1)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r14.avatarImageView
            r10 = 0
            r0.setPivotX(r10)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r14.avatarImageView
            r0.setPivotY(r10)
            org.telegram.ui.Components.AvatarDrawable r9 = new org.telegram.ui.Components.AvatarDrawable
            r9.<init>()
            r9.setProfile(r11)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r14.avatarImageView
            r0.setImageDrawable(r9)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r14.avatarImageView
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
            r0.<init>(r14, r2, r2)
            r14.mediaCounterTextView = r0
            r16 = -2
            r17 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r19 = 1122762752(0x42eCLASSNAME, float:118.0)
            r21 = 1113587712(0x42600000, float:56.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r13.addView(r0, r1)
            org.telegram.ui.Components.MediaActivity$6 r8 = new org.telegram.ui.Components.MediaActivity$6
            long r3 = r14.dialogId
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r5 = r14.sharedMediaPreloader
            org.telegram.tgnet.TLRPC$ChatFull r7 = r14.currentChatInfo
            org.telegram.ui.Components.MediaActivity$5 r6 = new org.telegram.ui.Components.MediaActivity$5
            r6.<init>()
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r0 = r8
            r1 = r25
            r20 = r6
            r6 = r16
            r16 = r7
            r7 = r17
            r15 = r8
            r8 = r16
            r23 = r9
            r9 = r18
            r10 = r25
            r11 = r20
            r24 = r12
            r12 = r19
            r26 = r13
            r0.<init>(r1, r2, r3, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            r14.sharedMediaLayout = r15
            r0 = 1
            r15.setPinnedToTop(r0)
            org.telegram.ui.Components.SharedMediaLayout r1 = r14.sharedMediaLayout
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.getSearchItem()
            r2 = 0
            r1.setTranslationY(r2)
            org.telegram.ui.Components.SharedMediaLayout r1 = r14.sharedMediaLayout
            android.widget.ImageView r1 = r1.photoVideoOptionsItem
            r1.setTranslationY(r2)
            org.telegram.ui.Components.SharedMediaLayout r1 = r14.sharedMediaLayout
            r2 = r24
            r2.addView(r1)
            org.telegram.ui.ActionBar.ActionBar r1 = r14.actionBar
            r2.addView(r1)
            r1 = r26
            r2.addView(r1)
            long r3 = r14.dialogId
            boolean r3 = org.telegram.messenger.DialogObject.isEncryptedDialog(r3)
            r4 = 0
            if (r3 == 0) goto L_0x0176
            org.telegram.messenger.MessagesController r3 = r25.getMessagesController()
            long r5 = r14.dialogId
            int r5 = org.telegram.messenger.DialogObject.getEncryptedChatId(r5)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r5)
            if (r3 == 0) goto L_0x0173
            org.telegram.messenger.MessagesController r5 = r25.getMessagesController()
            long r6 = r3.user_id
            java.lang.Long r3 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r3 = r5.getUser(r3)
            if (r3 == 0) goto L_0x0173
            org.telegram.ui.ActionBar.SimpleTextView r4 = r14.nameTextView
            java.lang.String r5 = r3.first_name
            java.lang.String r6 = r3.last_name
            java.lang.String r5 = org.telegram.messenger.ContactsController.formatName(r5, r6)
            r4.setText(r5)
            r5 = r23
            r5.setInfo((org.telegram.tgnet.TLRPC$User) r3)
            goto L_0x01d9
        L_0x0173:
            r5 = r23
            goto L_0x01da
        L_0x0176:
            r5 = r23
            long r6 = r14.dialogId
            boolean r3 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r3 == 0) goto L_0x01bc
            int r3 = r14.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r6 = r14.dialogId
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r6)
            if (r3 == 0) goto L_0x01da
            boolean r6 = r3.self
            if (r6 == 0) goto L_0x01ab
            org.telegram.ui.ActionBar.SimpleTextView r3 = r14.nameTextView
            r6 = 2131627629(0x7f0e0e6d, float:1.8882528E38)
            java.lang.String r7 = "SavedMessages"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r3.setText(r6)
            r5.setAvatarType(r0)
            r5.setSmallSize(r0)
            goto L_0x01da
        L_0x01ab:
            org.telegram.ui.ActionBar.SimpleTextView r4 = r14.nameTextView
            java.lang.String r6 = r3.first_name
            java.lang.String r7 = r3.last_name
            java.lang.String r6 = org.telegram.messenger.ContactsController.formatName(r6, r7)
            r4.setText(r6)
            r5.setInfo((org.telegram.tgnet.TLRPC$User) r3)
            goto L_0x01d9
        L_0x01bc:
            int r3 = r14.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r6 = r14.dialogId
            long r6 = -r6
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r6)
            if (r3 == 0) goto L_0x01da
            org.telegram.ui.ActionBar.SimpleTextView r4 = r14.nameTextView
            java.lang.String r6 = r3.title
            r4.setText(r6)
            r5.setInfo((org.telegram.tgnet.TLRPC$Chat) r3)
        L_0x01d9:
            r4 = r3
        L_0x01da:
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForUserOrChat(r4, r0)
            org.telegram.ui.ProfileActivity$AvatarImageView r6 = r14.avatarImageView
            java.lang.String r7 = "50_50"
            r6.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r7, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r4)
            org.telegram.ui.ActionBar.SimpleTextView r3 = r14.nameTextView
            java.lang.CharSequence r3 = r3.getText()
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 == 0) goto L_0x01ff
            org.telegram.ui.ActionBar.SimpleTextView r3 = r14.nameTextView
            r4 = 2131627841(0x7f0e0var_, float:1.8882958E38)
            java.lang.String r5 = "SharedContentTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setText(r4)
        L_0x01ff:
            org.telegram.ui.Components.SharedMediaLayout r3 = r14.sharedMediaLayout
            boolean r3 = r3.isSearchItemVisible()
            if (r3 == 0) goto L_0x0212
            org.telegram.ui.Components.SharedMediaLayout r3 = r14.sharedMediaLayout
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r3.getSearchItem()
            r4 = 0
            r3.setVisibility(r4)
            goto L_0x0213
        L_0x0212:
            r4 = 0
        L_0x0213:
            org.telegram.ui.Components.SharedMediaLayout r3 = r14.sharedMediaLayout
            boolean r3 = r3.isCalendarItemVisible()
            if (r3 == 0) goto L_0x0223
            org.telegram.ui.Components.SharedMediaLayout r3 = r14.sharedMediaLayout
            android.widget.ImageView r3 = r3.photoVideoOptionsItem
            r3.setVisibility(r4)
            goto L_0x022b
        L_0x0223:
            org.telegram.ui.Components.SharedMediaLayout r3 = r14.sharedMediaLayout
            android.widget.ImageView r3 = r3.photoVideoOptionsItem
            r5 = 4
            r3.setVisibility(r5)
        L_0x022b:
            r3 = 1065353216(0x3var_, float:1.0)
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r1, r0, r3, r4)
            r25.updateMediaCount()
            r25.lambda$getThemeDescriptions$0()
            return r2
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
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Photos", lastMediaCount[6]));
                } else if (this.sharedMediaLayout.getPhotosVideosTypeFilter() == 2) {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Videos", lastMediaCount[7]));
                } else {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Media", lastMediaCount[0]));
                }
            } else if (closestTab == 1) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Files", lastMediaCount[1]));
            } else if (closestTab == 2) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Voice", lastMediaCount[2]));
            } else if (closestTab == 3) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Links", lastMediaCount[3]));
            } else if (closestTab == 4) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("MusicFiles", lastMediaCount[4]));
            } else if (closestTab == 5) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("GIFs", lastMediaCount[5]));
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
}
