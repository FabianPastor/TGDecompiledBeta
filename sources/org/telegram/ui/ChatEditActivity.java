package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChannelLocation;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_channelLocation;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputChatPhoto;
import org.telegram.tgnet.TLRPC$TL_messages_exportedChatInvites;
import org.telegram.tgnet.TLRPC$TL_messages_getExportedChatInvites;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.PhotoViewer;

public class ChatEditActivity extends BaseFragment implements ImageUpdater.ImageUpdaterDelegate, NotificationCenter.NotificationCenterDelegate {
    private TextCell adminCell;
    private List<String> availableReactions = Collections.emptyList();
    private TLRPC$FileLocation avatar;
    /* access modifiers changed from: private */
    public AnimatorSet avatarAnimation;
    /* access modifiers changed from: private */
    public LinearLayout avatarContainer;
    /* access modifiers changed from: private */
    public AvatarDrawable avatarDrawable = new AvatarDrawable();
    /* access modifiers changed from: private */
    public BackupImageView avatarImage;
    /* access modifiers changed from: private */
    public View avatarOverlay;
    /* access modifiers changed from: private */
    public RadialProgressView avatarProgressView;
    private TextCell blockCell;
    RLottieDrawable cameraDrawable;
    /* access modifiers changed from: private */
    public long chatId;
    private boolean createAfterUpload;
    private TLRPC$Chat currentChat;
    private TextSettingsCell deleteCell;
    private FrameLayout deleteContainer;
    private ShadowSectionCell deleteInfoCell;
    private EditTextBoldCursor descriptionTextView;
    private View doneButton;
    private boolean donePressed;
    private TextDetailCell historyCell;
    private boolean historyHidden;
    /* access modifiers changed from: private */
    public ImageUpdater imageUpdater = new ImageUpdater(true);
    private TLRPC$ChatFull info;
    private LinearLayout infoContainer;
    private ShadowSectionCell infoSectionCell;
    private TextCell inviteLinksCell;
    private boolean isChannel;
    private TextDetailCell linkedCell;
    private TextDetailCell locationCell;
    private TextCell logCell;
    private TextCell memberRequestsCell;
    private TextCell membersCell;
    /* access modifiers changed from: private */
    public EditTextEmoji nameTextView;
    private AlertDialog progressDialog;
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i, boolean z) {
            TLRPC$FileLocation tLRPC$FileLocation2;
            TLRPC$ChatPhoto tLRPC$ChatPhoto;
            if (tLRPC$FileLocation == null) {
                return null;
            }
            TLRPC$Chat chat = ChatEditActivity.this.getMessagesController().getChat(Long.valueOf(ChatEditActivity.this.chatId));
            if (chat == null || (tLRPC$ChatPhoto = chat.photo) == null || (tLRPC$FileLocation2 = tLRPC$ChatPhoto.photo_big) == null) {
                tLRPC$FileLocation2 = null;
            }
            if (tLRPC$FileLocation2 == null || tLRPC$FileLocation2.local_id != tLRPC$FileLocation.local_id || tLRPC$FileLocation2.volume_id != tLRPC$FileLocation.volume_id || tLRPC$FileLocation2.dc_id != tLRPC$FileLocation.dc_id) {
                return null;
            }
            int[] iArr = new int[2];
            ChatEditActivity.this.avatarImage.getLocationInWindow(iArr);
            PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
            int i2 = 0;
            placeProviderObject.viewX = iArr[0];
            int i3 = iArr[1];
            if (Build.VERSION.SDK_INT < 21) {
                i2 = AndroidUtilities.statusBarHeight;
            }
            placeProviderObject.viewY = i3 - i2;
            placeProviderObject.parentView = ChatEditActivity.this.avatarImage;
            placeProviderObject.imageReceiver = ChatEditActivity.this.avatarImage.getImageReceiver();
            placeProviderObject.dialogId = -ChatEditActivity.this.chatId;
            placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmapSafe();
            placeProviderObject.size = -1;
            placeProviderObject.radius = ChatEditActivity.this.avatarImage.getImageReceiver().getRoundRadius();
            placeProviderObject.scale = ChatEditActivity.this.avatarContainer.getScaleX();
            placeProviderObject.canEdit = true;
            return placeProviderObject;
        }

        public void willHidePhotoViewer() {
            ChatEditActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
        }

        public void openPhotoForEdit(String str, String str2, boolean z) {
            ChatEditActivity.this.imageUpdater.openPhotoForEdit(str, str2, 0, z);
        }
    };
    private TextCell reactionsCell;
    private TextCell setAvatarCell;
    private LinearLayout settingsContainer;
    private ShadowSectionCell settingsSectionCell;
    private ShadowSectionCell settingsTopSectionCell;
    private TextCheckCell signCell;
    private boolean signMessages;
    private TextCell stickersCell;
    private FrameLayout stickersContainer;
    private TextInfoPrivacyCell stickersInfoCell;
    private TextDetailCell typeCell;
    private LinearLayout typeEditContainer;
    private UndoView undoView;

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$2(View view, MotionEvent motionEvent) {
        return true;
    }

    public ChatEditActivity(Bundle bundle) {
        super(bundle);
        this.chatId = bundle.getLong("chat_id", 0);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x004c, code lost:
        if (r0 == null) goto L_0x004e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onFragmentCreate() {
        /*
            r10 = this;
            org.telegram.messenger.MessagesController r0 = r10.getMessagesController()
            long r1 = r10.chatId
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            r10.currentChat = r0
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x004f
            int r0 = r10.currentAccount
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)
            long r3 = r10.chatId
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChatSync(r3)
            r10.currentChat = r0
            if (r0 == 0) goto L_0x004e
            org.telegram.messenger.MessagesController r0 = r10.getMessagesController()
            org.telegram.tgnet.TLRPC$Chat r3 = r10.currentChat
            r0.putChat(r3, r1)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r10.info
            if (r0 != 0) goto L_0x004f
            int r0 = r10.currentAccount
            org.telegram.messenger.MessagesStorage r3 = org.telegram.messenger.MessagesStorage.getInstance(r0)
            long r4 = r10.chatId
            org.telegram.tgnet.TLRPC$Chat r0 = r10.currentChat
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r0)
            java.util.concurrent.CountDownLatch r7 = new java.util.concurrent.CountDownLatch
            r7.<init>(r1)
            r8 = 0
            r9 = 0
            org.telegram.tgnet.TLRPC$ChatFull r0 = r3.loadChatInfo(r4, r6, r7, r8, r9)
            r10.info = r0
            if (r0 != 0) goto L_0x004f
        L_0x004e:
            return r2
        L_0x004f:
            org.telegram.ui.Components.AvatarDrawable r0 = r10.avatarDrawable
            r3 = 5
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            java.lang.String r5 = r5.title
            r6 = 0
            r0.setInfo(r3, r5, r6)
            org.telegram.tgnet.TLRPC$Chat r0 = r10.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x006a
            org.telegram.tgnet.TLRPC$Chat r0 = r10.currentChat
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x006a
            goto L_0x006b
        L_0x006a:
            r1 = 0
        L_0x006b:
            r10.isChannel = r1
            org.telegram.ui.Components.ImageUpdater r0 = r10.imageUpdater
            r0.parentFragment = r10
            r0.setDelegate(r10)
            org.telegram.tgnet.TLRPC$Chat r0 = r10.currentChat
            boolean r0 = r0.signatures
            r10.signMessages = r0
            int r0 = r10.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad
            r0.addObserver(r10, r1)
            int r0 = r10.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.updateInterfaces
            r0.addObserver(r10, r1)
            int r0 = r10.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.chatAvailableReactionsUpdated
            r0.addObserver(r10, r1)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r10.info
            if (r0 == 0) goto L_0x00a2
            r10.loadLinksCount()
        L_0x00a2:
            boolean r0 = super.onFragmentCreate()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.onFragmentCreate():boolean");
    }

    private void loadLinksCount() {
        TLRPC$TL_messages_getExportedChatInvites tLRPC$TL_messages_getExportedChatInvites = new TLRPC$TL_messages_getExportedChatInvites();
        tLRPC$TL_messages_getExportedChatInvites.peer = getMessagesController().getInputPeer(-this.chatId);
        tLRPC$TL_messages_getExportedChatInvites.admin_id = getMessagesController().getInputUser(getUserConfig().getCurrentUser());
        tLRPC$TL_messages_getExportedChatInvites.limit = 0;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getExportedChatInvites, new ChatEditActivity$$ExternalSyntheticLambda29(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadLinksCount$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ChatEditActivity$$ExternalSyntheticLambda26(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadLinksCount$0(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            this.info.invitesCount = ((TLRPC$TL_messages_exportedChatInvites) tLObject).count;
            getMessagesStorage().saveChatLinksCount(this.chatId, this.info.invitesCount);
            updateFields(false);
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.clear();
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatAvailableReactionsUpdated);
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    public void onResume() {
        super.onResume();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
            this.nameTextView.getEditText().requestFocus();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        updateFields(true);
        this.imageUpdater.onResume();
    }

    public void onPause() {
        super.onPause();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onPause();
        }
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
        this.imageUpdater.onPause();
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    public void dismissCurrentDialog() {
        if (!this.imageUpdater.dismissCurrentDialog(this.visibleDialog)) {
            super.dismissCurrentDialog();
        }
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return this.imageUpdater.dismissDialogOnPause(dialog) && super.dismissDialogOnPause(dialog);
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        this.imageUpdater.onRequestPermissionsResultFragment(i, strArr, iArr);
    }

    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            return checkDiscard();
        }
        this.nameTextView.hidePopup(true);
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:146:0x05bf  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0633  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x066d  */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x069a  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x06e6  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x06fc  */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x070a  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x074b  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x07cf  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0815  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r32) {
        /*
            r31 = this;
            r6 = r31
            r7 = r32
            org.telegram.ui.Components.EditTextEmoji r0 = r6.nameTextView
            if (r0 == 0) goto L_0x000b
            r0.onDestroy()
        L_0x000b:
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r1 = 2131165449(0x7var_, float:1.7945115E38)
            r0.setBackButtonImage(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r8 = 1
            r0.setAllowOverlayTitle(r8)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.ChatEditActivity$2 r1 = new org.telegram.ui.ChatEditActivity$2
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            org.telegram.ui.ChatEditActivity$3 r9 = new org.telegram.ui.ChatEditActivity$3
            r9.<init>(r7)
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda22 r0 = org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda22.INSTANCE
            r9.setOnTouchListener(r0)
            r6.fragmentView = r9
            java.lang.String r0 = "windowBackgroundGray"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            r9.setBackgroundColor(r0)
            android.widget.ScrollView r0 = new android.widget.ScrollView
            r0.<init>(r7)
            r0.setFillViewport(r8)
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            r10 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r1)
            r9.addView(r0, r1)
            android.widget.LinearLayout r11 = new android.widget.LinearLayout
            r11.<init>(r7)
            android.widget.FrameLayout$LayoutParams r1 = new android.widget.FrameLayout$LayoutParams
            r12 = -2
            r1.<init>(r10, r12)
            r0.addView(r11, r1)
            r11.setOrientation(r8)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r1 = 2131624912(0x7f0e03d0, float:1.8877017E38)
            java.lang.String r2 = "ChannelEdit"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r7)
            r6.avatarContainer = r0
            r0.setOrientation(r8)
            android.widget.LinearLayout r0 = r6.avatarContainer
            java.lang.String r13 = "windowBackgroundWhite"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r0.setBackgroundColor(r1)
            android.widget.LinearLayout r0 = r6.avatarContainer
            android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r12)
            r11.addView(r0, r1)
            android.widget.FrameLayout r14 = new android.widget.FrameLayout
            r14.<init>(r7)
            android.widget.LinearLayout r0 = r6.avatarContainer
            android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r12)
            r0.addView(r14, r1)
            org.telegram.ui.ChatEditActivity$4 r0 = new org.telegram.ui.ChatEditActivity$4
            r0.<init>(r7)
            r6.avatarImage = r0
            r1 = 1107296256(0x42000000, float:32.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setRoundRadius(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canChangeChatInfo(r0)
            r16 = 3
            r1 = 0
            r5 = 1098907648(0x41800000, float:16.0)
            r4 = 0
            if (r0 == 0) goto L_0x0162
            org.telegram.ui.Components.BackupImageView r0 = r6.avatarImage
            r17 = 64
            r18 = 1115684864(0x42800000, float:64.0)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x00bf
            r3 = 5
            goto L_0x00c0
        L_0x00bf:
            r3 = 3
        L_0x00c0:
            r19 = r3 | 48
            if (r2 == 0) goto L_0x00c7
            r20 = 0
            goto L_0x00c9
        L_0x00c7:
            r20 = 1098907648(0x41800000, float:16.0)
        L_0x00c9:
            r21 = 1094713344(0x41400000, float:12.0)
            if (r2 == 0) goto L_0x00d0
            r22 = 1098907648(0x41800000, float:16.0)
            goto L_0x00d2
        L_0x00d0:
            r22 = 0
        L_0x00d2:
            r23 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r14.addView(r0, r2)
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>(r8)
            r2 = 1426063360(0x55000000, float:8.796093E12)
            r0.setColor(r2)
            org.telegram.ui.ChatEditActivity$5 r2 = new org.telegram.ui.ChatEditActivity$5
            r2.<init>(r7, r0)
            r6.avatarOverlay = r2
            r17 = 64
            r18 = 1115684864(0x42800000, float:64.0)
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x00f6
            r3 = 5
            goto L_0x00f7
        L_0x00f6:
            r3 = 3
        L_0x00f7:
            r19 = r3 | 48
            if (r0 == 0) goto L_0x00fe
            r20 = 0
            goto L_0x0100
        L_0x00fe:
            r20 = 1098907648(0x41800000, float:16.0)
        L_0x0100:
            r21 = 1094713344(0x41400000, float:12.0)
            if (r0 == 0) goto L_0x0107
            r22 = 1098907648(0x41800000, float:16.0)
            goto L_0x0109
        L_0x0107:
            r22 = 0
        L_0x0109:
            r23 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r14.addView(r2, r0)
            org.telegram.ui.Components.RadialProgressView r0 = new org.telegram.ui.Components.RadialProgressView
            r0.<init>(r7)
            r6.avatarProgressView = r0
            r2 = 1106247680(0x41var_, float:30.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setSize(r2)
            org.telegram.ui.Components.RadialProgressView r0 = r6.avatarProgressView
            r0.setProgressColor(r10)
            org.telegram.ui.Components.RadialProgressView r0 = r6.avatarProgressView
            r0.setNoProgress(r4)
            org.telegram.ui.Components.RadialProgressView r0 = r6.avatarProgressView
            r17 = 64
            r18 = 1115684864(0x42800000, float:64.0)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0138
            r3 = 5
            goto L_0x0139
        L_0x0138:
            r3 = 3
        L_0x0139:
            r19 = r3 | 48
            if (r2 == 0) goto L_0x0140
            r20 = 0
            goto L_0x0142
        L_0x0140:
            r20 = 1098907648(0x41800000, float:16.0)
        L_0x0142:
            r21 = 1094713344(0x41400000, float:12.0)
            if (r2 == 0) goto L_0x0149
            r22 = 1098907648(0x41800000, float:16.0)
            goto L_0x014b
        L_0x0149:
            r22 = 0
        L_0x014b:
            r23 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r14.addView(r0, r1)
            r6.showAvatarProgress(r4, r4)
            android.widget.LinearLayout r0 = r6.avatarContainer
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda18 r1 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda18
            r1.<init>(r6)
            r0.setOnClickListener(r1)
            goto L_0x018a
        L_0x0162:
            org.telegram.ui.Components.BackupImageView r0 = r6.avatarImage
            r17 = 64
            r18 = 1115684864(0x42800000, float:64.0)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x016e
            r3 = 5
            goto L_0x016f
        L_0x016e:
            r3 = 3
        L_0x016f:
            r19 = r3 | 48
            if (r2 == 0) goto L_0x0176
            r20 = 0
            goto L_0x0178
        L_0x0176:
            r20 = 1098907648(0x41800000, float:16.0)
        L_0x0178:
            r21 = 1094713344(0x41400000, float:12.0)
            if (r2 == 0) goto L_0x017f
            r22 = 1098907648(0x41800000, float:16.0)
            goto L_0x0181
        L_0x017f:
            r22 = 0
        L_0x0181:
            r23 = 1094713344(0x41400000, float:12.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r14.addView(r0, r1)
        L_0x018a:
            org.telegram.ui.Components.EditTextEmoji r3 = new org.telegram.ui.Components.EditTextEmoji
            r17 = 0
            r18 = 0
            r0 = r3
            r1 = r32
            r2 = r9
            r15 = r3
            r3 = r31
            r10 = 0
            r4 = r17
            r5 = r18
            r0.<init>(r1, r2, r3, r4, r5)
            r6.nameTextView = r15
            boolean r0 = r6.isChannel
            if (r0 == 0) goto L_0x01b2
            r0 = 2131625675(0x7f0e06cb, float:1.8878565E38)
            java.lang.String r1 = "EnterChannelName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r15.setHint(r0)
            goto L_0x01be
        L_0x01b2:
            r0 = 2131626136(0x7f0e0898, float:1.88795E38)
            java.lang.String r1 = "GroupName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r15.setHint(r0)
        L_0x01be:
            org.telegram.ui.Components.EditTextEmoji r0 = r6.nameTextView
            org.telegram.tgnet.TLRPC$Chat r1 = r6.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.canChangeChatInfo(r1)
            r0.setEnabled(r1)
            org.telegram.ui.Components.EditTextEmoji r0 = r6.nameTextView
            boolean r1 = r0.isEnabled()
            r0.setFocusable(r1)
            org.telegram.ui.Components.EditTextEmoji r0 = r6.nameTextView
            org.telegram.ui.Components.EditTextCaption r0 = r0.getEditText()
            org.telegram.ui.ChatEditActivity$6 r1 = new org.telegram.ui.ChatEditActivity$6
            r1.<init>()
            r0.addTextChangedListener(r1)
            android.text.InputFilter[] r0 = new android.text.InputFilter[r8]
            android.text.InputFilter$LengthFilter r1 = new android.text.InputFilter$LengthFilter
            r2 = 128(0x80, float:1.794E-43)
            r1.<init>(r2)
            r0[r10] = r1
            org.telegram.ui.Components.EditTextEmoji r1 = r6.nameTextView
            r1.setFilters(r0)
            org.telegram.ui.Components.EditTextEmoji r0 = r6.nameTextView
            r24 = -1
            r25 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r26 = 16
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            r2 = 1084227584(0x40a00000, float:5.0)
            r3 = 1119879168(0x42CLASSNAME, float:96.0)
            if (r1 == 0) goto L_0x0203
            r27 = 1084227584(0x40a00000, float:5.0)
            goto L_0x0205
        L_0x0203:
            r27 = 1119879168(0x42CLASSNAME, float:96.0)
        L_0x0205:
            r28 = 0
            if (r1 == 0) goto L_0x020c
            r29 = 1119879168(0x42CLASSNAME, float:96.0)
            goto L_0x020e
        L_0x020c:
            r29 = 1084227584(0x40a00000, float:5.0)
        L_0x020e:
            r30 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r14.addView(r0, r1)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r7)
            r6.settingsContainer = r0
            r0.setOrientation(r8)
            android.widget.LinearLayout r0 = r6.settingsContainer
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r0.setBackgroundColor(r1)
            android.widget.LinearLayout r0 = r6.settingsContainer
            r1 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r1, r12)
            r11.addView(r0, r2)
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canChangeChatInfo(r0)
            if (r0 == 0) goto L_0x0269
            org.telegram.ui.ChatEditActivity$7 r0 = new org.telegram.ui.ChatEditActivity$7
            r0.<init>(r6, r7)
            r6.setAvatarCell = r0
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r0.setBackgroundDrawable(r1)
            org.telegram.ui.Cells.TextCell r0 = r6.setAvatarCell
            java.lang.String r1 = "windowBackgroundWhiteBlueIcon"
            java.lang.String r2 = "windowBackgroundWhiteBlueButton"
            r0.setColors(r1, r2)
            org.telegram.ui.Cells.TextCell r0 = r6.setAvatarCell
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda12 r1 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda12
            r1.<init>(r6)
            r0.setOnClickListener(r1)
            android.widget.LinearLayout r0 = r6.settingsContainer
            org.telegram.ui.Cells.TextCell r1 = r6.setAvatarCell
            r2 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r12)
            r0.addView(r1, r3)
        L_0x0269:
            org.telegram.ui.Components.EditTextBoldCursor r0 = new org.telegram.ui.Components.EditTextBoldCursor
            r0.<init>(r7)
            r6.descriptionTextView = r0
            r1 = 1098907648(0x41800000, float:16.0)
            r0.setTextSize(r8, r1)
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.descriptionTextView
            java.lang.String r1 = "windowBackgroundWhiteHintText"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setHintTextColor(r1)
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.descriptionTextView
            java.lang.String r1 = "windowBackgroundWhiteBlackText"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r2)
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.descriptionTextView
            r2 = 1086324736(0x40CLASSNAME, float:6.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r10, r10, r10, r2)
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.descriptionTextView
            r2 = 0
            r0.setBackgroundDrawable(r2)
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.descriptionTextView
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x02a4
            r15 = 5
            goto L_0x02a5
        L_0x02a4:
            r15 = 3
        L_0x02a5:
            r0.setGravity(r15)
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.descriptionTextView
            r2 = 180225(0x2CLASSNAME, float:2.52549E-40)
            r0.setInputType(r2)
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.descriptionTextView
            r2 = 6
            r0.setImeOptions(r2)
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.descriptionTextView
            org.telegram.tgnet.TLRPC$Chat r2 = r6.currentChat
            boolean r2 = org.telegram.messenger.ChatObject.canChangeChatInfo(r2)
            r0.setEnabled(r2)
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.descriptionTextView
            boolean r2 = r0.isEnabled()
            r0.setFocusable(r2)
            android.text.InputFilter[] r0 = new android.text.InputFilter[r8]
            android.text.InputFilter$LengthFilter r2 = new android.text.InputFilter$LengthFilter
            r3 = 255(0xff, float:3.57E-43)
            r2.<init>(r3)
            r0[r10] = r2
            org.telegram.ui.Components.EditTextBoldCursor r2 = r6.descriptionTextView
            r2.setFilters(r0)
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.descriptionTextView
            r2 = 2131625471(0x7f0e05ff, float:1.887815E38)
            java.lang.String r3 = "DescriptionOptionalPlaceholder"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setHint(r2)
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.descriptionTextView
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setCursorColor(r1)
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.descriptionTextView
            r1 = 1101004800(0x41a00000, float:20.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setCursorSize(r1)
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.descriptionTextView
            r1 = 1069547520(0x3fCLASSNAME, float:1.5)
            r0.setCursorWidth(r1)
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.descriptionTextView
            boolean r0 = r0.isEnabled()
            if (r0 == 0) goto L_0x0321
            android.widget.LinearLayout r0 = r6.settingsContainer
            org.telegram.ui.Components.EditTextBoldCursor r1 = r6.descriptionTextView
            r14 = -1
            r15 = -2
            r16 = 1102577664(0x41b80000, float:23.0)
            r17 = 1097859072(0x41700000, float:15.0)
            r18 = 1102577664(0x41b80000, float:23.0)
            r19 = 1091567616(0x41100000, float:9.0)
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r14, r15, r16, r17, r18, r19)
            r0.addView(r1, r2)
            goto L_0x0336
        L_0x0321:
            android.widget.LinearLayout r0 = r6.settingsContainer
            org.telegram.ui.Components.EditTextBoldCursor r1 = r6.descriptionTextView
            r14 = -1
            r15 = -2
            r16 = 1102577664(0x41b80000, float:23.0)
            r17 = 1094713344(0x41400000, float:12.0)
            r18 = 1102577664(0x41b80000, float:23.0)
            r19 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r14, r15, r16, r17, r18, r19)
            r0.addView(r1, r2)
        L_0x0336:
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.descriptionTextView
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda23 r1 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda23
            r1.<init>(r6)
            r0.setOnEditorActionListener(r1)
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.descriptionTextView
            org.telegram.ui.ChatEditActivity$8 r1 = new org.telegram.ui.ChatEditActivity$8
            r1.<init>(r6)
            r0.addTextChangedListener(r1)
            org.telegram.ui.Cells.ShadowSectionCell r0 = new org.telegram.ui.Cells.ShadowSectionCell
            r0.<init>(r7)
            r6.settingsTopSectionCell = r0
            r1 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r1, r12)
            r11.addView(r0, r2)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r7)
            r6.typeEditContainer = r0
            r0.setOrientation(r8)
            android.widget.LinearLayout r0 = r6.typeEditContainer
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r0.setBackgroundColor(r2)
            android.widget.LinearLayout r0 = r6.typeEditContainer
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r1, r12)
            r11.addView(r0, r2)
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = r0.megagroup
            if (r0 == 0) goto L_0x03a7
            org.telegram.tgnet.TLRPC$ChatFull r0 = r6.info
            if (r0 == 0) goto L_0x0383
            boolean r0 = r0.can_set_location
            if (r0 == 0) goto L_0x03a7
        L_0x0383:
            org.telegram.ui.Cells.TextDetailCell r0 = new org.telegram.ui.Cells.TextDetailCell
            r0.<init>(r7)
            r6.locationCell = r0
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r0.setBackgroundDrawable(r1)
            android.widget.LinearLayout r0 = r6.typeEditContainer
            org.telegram.ui.Cells.TextDetailCell r1 = r6.locationCell
            r2 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r12)
            r0.addView(r1, r3)
            org.telegram.ui.Cells.TextDetailCell r0 = r6.locationCell
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda11 r1 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda11
            r1.<init>(r6)
            r0.setOnClickListener(r1)
        L_0x03a7:
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = r0.creator
            if (r0 == 0) goto L_0x03d9
            org.telegram.tgnet.TLRPC$ChatFull r0 = r6.info
            if (r0 == 0) goto L_0x03b5
            boolean r0 = r0.can_set_username
            if (r0 == 0) goto L_0x03d9
        L_0x03b5:
            org.telegram.ui.Cells.TextDetailCell r0 = new org.telegram.ui.Cells.TextDetailCell
            r0.<init>(r7)
            r6.typeCell = r0
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r0.setBackgroundDrawable(r1)
            android.widget.LinearLayout r0 = r6.typeEditContainer
            org.telegram.ui.Cells.TextDetailCell r1 = r6.typeCell
            r2 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r12)
            r0.addView(r1, r3)
            org.telegram.ui.Cells.TextDetailCell r0 = r6.typeCell
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda6 r1 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda6
            r1.<init>(r6)
            r0.setOnClickListener(r1)
        L_0x03d9:
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x041d
            boolean r0 = r6.isChannel
            if (r0 == 0) goto L_0x03ed
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r0, r8)
            if (r0 != 0) goto L_0x03f9
        L_0x03ed:
            boolean r0 = r6.isChannel
            if (r0 != 0) goto L_0x041d
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r0, r10)
            if (r0 == 0) goto L_0x041d
        L_0x03f9:
            org.telegram.ui.Cells.TextDetailCell r0 = new org.telegram.ui.Cells.TextDetailCell
            r0.<init>(r7)
            r6.linkedCell = r0
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r0.setBackgroundDrawable(r1)
            android.widget.LinearLayout r0 = r6.typeEditContainer
            org.telegram.ui.Cells.TextDetailCell r1 = r6.linkedCell
            r2 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r12)
            r0.addView(r1, r3)
            org.telegram.ui.Cells.TextDetailCell r0 = r6.linkedCell
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda7 r1 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda7
            r1.<init>(r6)
            r0.setOnClickListener(r1)
        L_0x041d:
            boolean r0 = r6.isChannel
            if (r0 != 0) goto L_0x045b
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.canBlockUsers(r0)
            if (r0 == 0) goto L_0x045b
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 != 0) goto L_0x0437
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = r0.creator
            if (r0 == 0) goto L_0x045b
        L_0x0437:
            org.telegram.ui.Cells.TextDetailCell r0 = new org.telegram.ui.Cells.TextDetailCell
            r0.<init>(r7)
            r6.historyCell = r0
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r0.setBackgroundDrawable(r1)
            android.widget.LinearLayout r0 = r6.typeEditContainer
            org.telegram.ui.Cells.TextDetailCell r1 = r6.historyCell
            r2 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r12)
            r0.addView(r1, r3)
            org.telegram.ui.Cells.TextDetailCell r0 = r6.historyCell
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda20 r1 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda20
            r1.<init>(r6, r7)
            r0.setOnClickListener(r1)
        L_0x045b:
            boolean r0 = r6.isChannel
            r1 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            if (r0 == 0) goto L_0x04a4
            org.telegram.ui.Cells.TextCheckCell r0 = new org.telegram.ui.Cells.TextCheckCell
            r0.<init>(r7)
            r6.signCell = r0
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r0.setBackgroundDrawable(r2)
            org.telegram.ui.Cells.TextCheckCell r14 = r6.signCell
            r0 = 2131624974(0x7f0e040e, float:1.8877143E38)
            java.lang.String r2 = "ChannelSignMessages"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r0 = 2131624975(0x7f0e040f, float:1.8877145E38)
            java.lang.String r2 = "ChannelSignMessagesInfo"
            java.lang.String r16 = org.telegram.messenger.LocaleController.getString(r2, r0)
            boolean r0 = r6.signMessages
            r18 = 1
            r19 = 0
            r17 = r0
            r14.setTextAndValueAndCheck(r15, r16, r17, r18, r19)
            android.widget.LinearLayout r0 = r6.typeEditContainer
            org.telegram.ui.Cells.TextCheckCell r2 = r6.signCell
            r3 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r1)
            r0.addView(r2, r4)
            org.telegram.ui.Cells.TextCheckCell r0 = r6.signCell
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda17 r2 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda17
            r2.<init>(r6)
            r0.setOnClickListener(r2)
        L_0x04a4:
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            org.telegram.tgnet.TLRPC$Chat r2 = r6.currentChat
            boolean r2 = org.telegram.messenger.ChatObject.canChangeChatInfo(r2)
            if (r2 != 0) goto L_0x04ba
            org.telegram.ui.Cells.TextCheckCell r2 = r6.signCell
            if (r2 != 0) goto L_0x04ba
            org.telegram.ui.Cells.TextDetailCell r2 = r6.historyCell
            if (r2 == 0) goto L_0x04d5
        L_0x04ba:
            r2 = 2131165450(0x7var_a, float:1.7945117E38)
            r3 = 1113587712(0x42600000, float:56.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.addItemWithWidth(r8, r2, r3)
            r6.doneButton = r0
            r2 = 2131625534(0x7f0e063e, float:1.8878279E38)
            java.lang.String r3 = "Done"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setContentDescription(r2)
        L_0x04d5:
            org.telegram.ui.Cells.TextDetailCell r0 = r6.locationCell
            if (r0 != 0) goto L_0x04ec
            org.telegram.ui.Cells.TextCheckCell r0 = r6.signCell
            if (r0 != 0) goto L_0x04ec
            org.telegram.ui.Cells.TextDetailCell r0 = r6.historyCell
            if (r0 != 0) goto L_0x04ec
            org.telegram.ui.Cells.TextDetailCell r0 = r6.typeCell
            if (r0 != 0) goto L_0x04ec
            org.telegram.ui.Cells.TextDetailCell r0 = r6.linkedCell
            if (r0 == 0) goto L_0x04ea
            goto L_0x04ec
        L_0x04ea:
            r2 = -1
            goto L_0x04fb
        L_0x04ec:
            org.telegram.ui.Cells.ShadowSectionCell r0 = new org.telegram.ui.Cells.ShadowSectionCell
            r0.<init>(r7)
            r6.settingsSectionCell = r0
            r2 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r12)
            r11.addView(r0, r3)
        L_0x04fb:
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r7)
            r6.infoContainer = r0
            r0.setOrientation(r8)
            android.widget.LinearLayout r0 = r6.infoContainer
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r0.setBackgroundColor(r3)
            android.widget.LinearLayout r0 = r6.infoContainer
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r12)
            r11.addView(r0, r3)
            org.telegram.ui.Cells.TextCell r0 = new org.telegram.ui.Cells.TextCell
            r0.<init>(r7)
            r6.blockCell = r0
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r0.setBackground(r2)
            org.telegram.ui.Cells.TextCell r0 = r6.blockCell
            org.telegram.tgnet.TLRPC$Chat r2 = r6.currentChat
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            r4 = 8
            if (r2 != 0) goto L_0x0549
            org.telegram.tgnet.TLRPC$Chat r2 = r6.currentChat
            boolean r3 = r2.creator
            if (r3 != 0) goto L_0x0549
            boolean r2 = org.telegram.messenger.ChatObject.hasAdminRights(r2)
            if (r2 == 0) goto L_0x0546
            org.telegram.tgnet.TLRPC$Chat r2 = r6.currentChat
            boolean r2 = org.telegram.messenger.ChatObject.canChangeChatInfo(r2)
            if (r2 == 0) goto L_0x0546
            goto L_0x0549
        L_0x0546:
            r2 = 8
            goto L_0x054a
        L_0x0549:
            r2 = 0
        L_0x054a:
            r0.setVisibility(r2)
            org.telegram.ui.Cells.TextCell r0 = r6.blockCell
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda10 r2 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda10
            r2.<init>(r6)
            r0.setOnClickListener(r2)
            org.telegram.ui.Cells.TextCell r0 = new org.telegram.ui.Cells.TextCell
            r0.<init>(r7)
            r6.inviteLinksCell = r0
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r0.setBackground(r2)
            org.telegram.ui.Cells.TextCell r0 = r6.inviteLinksCell
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda4 r2 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda4
            r2.<init>(r6)
            r0.setOnClickListener(r2)
            org.telegram.ui.Cells.TextCell r0 = new org.telegram.ui.Cells.TextCell
            r0.<init>(r7)
            r6.reactionsCell = r0
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r0.setBackground(r2)
            org.telegram.ui.Cells.TextCell r0 = r6.reactionsCell
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda8 r2 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda8
            r2.<init>(r6)
            r0.setOnClickListener(r2)
            org.telegram.ui.Cells.TextCell r0 = new org.telegram.ui.Cells.TextCell
            r0.<init>(r7)
            r6.adminCell = r0
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r0.setBackground(r2)
            org.telegram.ui.Cells.TextCell r0 = r6.adminCell
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda13 r2 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda13
            r2.<init>(r6)
            r0.setOnClickListener(r2)
            org.telegram.ui.Cells.TextCell r0 = new org.telegram.ui.Cells.TextCell
            r0.<init>(r7)
            r6.membersCell = r0
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r0.setBackgroundDrawable(r2)
            org.telegram.ui.Cells.TextCell r0 = r6.membersCell
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda19 r2 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda19
            r2.<init>(r6)
            r0.setOnClickListener(r2)
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannelAndNotMegaGroup(r0)
            if (r0 != 0) goto L_0x05d7
            org.telegram.ui.Cells.TextCell r0 = new org.telegram.ui.Cells.TextCell
            r0.<init>(r7)
            r6.memberRequestsCell = r0
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r0.setBackground(r2)
            org.telegram.ui.Cells.TextCell r0 = r6.memberRequestsCell
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda16 r2 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda16
            r2.<init>(r6)
            r0.setOnClickListener(r2)
        L_0x05d7:
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 != 0) goto L_0x05e5
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = r0.gigagroup
            if (r0 == 0) goto L_0x060e
        L_0x05e5:
            org.telegram.ui.Cells.TextCell r0 = new org.telegram.ui.Cells.TextCell
            r0.<init>(r7)
            r6.logCell = r0
            r2 = 2131625698(0x7f0e06e2, float:1.8878611E38)
            java.lang.String r3 = "EventLog"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 2131165792(0x7var_, float:1.7945811E38)
            r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r10)
            org.telegram.ui.Cells.TextCell r0 = r6.logCell
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r0.setBackground(r2)
            org.telegram.ui.Cells.TextCell r0 = r6.logCell
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda9 r2 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda9
            r2.<init>(r6)
            r0.setOnClickListener(r2)
        L_0x060e:
            android.widget.LinearLayout r0 = r6.infoContainer
            org.telegram.ui.Cells.TextCell r2 = r6.reactionsCell
            r3 = -1
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r12)
            r0.addView(r2, r5)
            boolean r0 = r6.isChannel
            if (r0 != 0) goto L_0x062f
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = r0.gigagroup
            if (r0 != 0) goto L_0x062f
            android.widget.LinearLayout r0 = r6.infoContainer
            org.telegram.ui.Cells.TextCell r2 = r6.blockCell
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r12)
            r0.addView(r2, r5)
        L_0x062f:
            boolean r0 = r6.isChannel
            if (r0 != 0) goto L_0x063e
            android.widget.LinearLayout r0 = r6.infoContainer
            org.telegram.ui.Cells.TextCell r2 = r6.inviteLinksCell
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r12)
            r0.addView(r2, r5)
        L_0x063e:
            android.widget.LinearLayout r0 = r6.infoContainer
            org.telegram.ui.Cells.TextCell r2 = r6.adminCell
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r12)
            r0.addView(r2, r5)
            android.widget.LinearLayout r0 = r6.infoContainer
            org.telegram.ui.Cells.TextCell r2 = r6.membersCell
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r12)
            r0.addView(r2, r5)
            org.telegram.ui.Cells.TextCell r0 = r6.memberRequestsCell
            if (r0 == 0) goto L_0x0669
            org.telegram.tgnet.TLRPC$ChatFull r2 = r6.info
            if (r2 == 0) goto L_0x0669
            int r2 = r2.requests_pending
            if (r2 <= 0) goto L_0x0669
            android.widget.LinearLayout r2 = r6.infoContainer
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r12)
            r2.addView(r0, r5)
        L_0x0669:
            boolean r0 = r6.isChannel
            if (r0 == 0) goto L_0x0678
            android.widget.LinearLayout r0 = r6.infoContainer
            org.telegram.ui.Cells.TextCell r2 = r6.inviteLinksCell
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r12)
            r0.addView(r2, r5)
        L_0x0678:
            boolean r0 = r6.isChannel
            if (r0 != 0) goto L_0x0682
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = r0.gigagroup
            if (r0 == 0) goto L_0x068e
        L_0x0682:
            android.widget.LinearLayout r0 = r6.infoContainer
            org.telegram.ui.Cells.TextCell r2 = r6.blockCell
            r3 = -1
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r12)
            r0.addView(r2, r5)
        L_0x068e:
            boolean r0 = r6.isChannel
            if (r0 != 0) goto L_0x06e6
            org.telegram.tgnet.TLRPC$ChatFull r0 = r6.info
            if (r0 == 0) goto L_0x06e6
            boolean r0 = r0.can_set_stickers
            if (r0 == 0) goto L_0x06e6
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.stickersContainer = r0
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r0.setBackgroundColor(r2)
            android.widget.FrameLayout r0 = r6.stickersContainer
            r2 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r12)
            r11.addView(r0, r3)
            org.telegram.ui.Cells.TextCell r0 = new org.telegram.ui.Cells.TextCell
            r0.<init>(r7)
            r6.stickersCell = r0
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r0.setBackground(r2)
            org.telegram.ui.Cells.TextCell r0 = r6.stickersCell
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda5 r2 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda5
            r2.<init>(r6)
            r0.setOnClickListener(r2)
            org.telegram.ui.Cells.TextCell r0 = r6.stickersCell
            r0.setPrioritizeTitleOverValue(r8)
            android.widget.FrameLayout r0 = r6.stickersContainer
            org.telegram.ui.Cells.TextCell r2 = r6.stickersCell
            r3 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r1)
            r0.addView(r2, r5)
            org.telegram.ui.Cells.TextCell r0 = r6.stickersCell
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda15 r2 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda15
            r2.<init>(r6)
            r0.setOnClickListener(r2)
            goto L_0x06f4
        L_0x06e6:
            org.telegram.ui.Cells.TextCell r0 = r6.logCell
            if (r0 == 0) goto L_0x06f4
            android.widget.LinearLayout r2 = r6.infoContainer
            r3 = -1
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r12)
            r2.addView(r0, r5)
        L_0x06f4:
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.hasAdminRights(r0)
            if (r0 != 0) goto L_0x0706
            android.widget.LinearLayout r0 = r6.infoContainer
            r0.setVisibility(r4)
            org.telegram.ui.Cells.ShadowSectionCell r0 = r6.settingsTopSectionCell
            r0.setVisibility(r4)
        L_0x0706:
            org.telegram.ui.Cells.TextCell r0 = r6.stickersCell
            if (r0 != 0) goto L_0x0719
            org.telegram.ui.Cells.ShadowSectionCell r0 = new org.telegram.ui.Cells.ShadowSectionCell
            r0.<init>(r7)
            r6.infoSectionCell = r0
            r2 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r12)
            r11.addView(r0, r3)
        L_0x0719:
            boolean r0 = r6.isChannel
            if (r0 != 0) goto L_0x0740
            org.telegram.tgnet.TLRPC$ChatFull r0 = r6.info
            if (r0 == 0) goto L_0x0740
            boolean r0 = r0.can_set_stickers
            if (r0 == 0) goto L_0x0740
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r0.<init>(r7)
            r6.stickersInfoCell = r0
            r2 = 2131626142(0x7f0e089e, float:1.8879512E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString((int) r2)
            r0.setText(r2)
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = r6.stickersInfoCell
            r2 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r12)
            r11.addView(r0, r3)
        L_0x0740:
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = r0.creator
            r2 = 2131165436(0x7var_fc, float:1.794509E38)
            java.lang.String r3 = "windowBackgroundGrayShadow"
            if (r0 == 0) goto L_0x07cb
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.deleteContainer = r0
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r0.setBackgroundColor(r4)
            android.widget.FrameLayout r0 = r6.deleteContainer
            r4 = -1
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r12)
            r11.addView(r0, r5)
            org.telegram.ui.Cells.TextSettingsCell r0 = new org.telegram.ui.Cells.TextSettingsCell
            r0.<init>(r7)
            r6.deleteCell = r0
            java.lang.String r4 = "windowBackgroundWhiteRedText5"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r0.setTextColor(r4)
            org.telegram.ui.Cells.TextSettingsCell r0 = r6.deleteCell
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r0.setBackgroundDrawable(r4)
            boolean r0 = r6.isChannel
            if (r0 == 0) goto L_0x078f
            org.telegram.ui.Cells.TextSettingsCell r0 = r6.deleteCell
            r4 = 2131624904(0x7f0e03c8, float:1.8877E38)
            java.lang.String r5 = "ChannelDelete"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4, r10)
            goto L_0x079d
        L_0x078f:
            org.telegram.ui.Cells.TextSettingsCell r0 = r6.deleteCell
            r4 = 2131625392(0x7f0e05b0, float:1.887799E38)
            java.lang.String r5 = "DeleteAndExitButton"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4, r10)
        L_0x079d:
            android.widget.FrameLayout r0 = r6.deleteContainer
            org.telegram.ui.Cells.TextSettingsCell r4 = r6.deleteCell
            r5 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r1)
            r0.addView(r4, r1)
            org.telegram.ui.Cells.TextSettingsCell r0 = r6.deleteCell
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda14 r1 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda14
            r1.<init>(r6)
            r0.setOnClickListener(r1)
            org.telegram.ui.Cells.ShadowSectionCell r0 = new org.telegram.ui.Cells.ShadowSectionCell
            r0.<init>(r7)
            r6.deleteInfoCell = r0
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r7, (int) r2, (java.lang.String) r3)
            r0.setBackground(r1)
            org.telegram.ui.Cells.ShadowSectionCell r0 = r6.deleteInfoCell
            r1 = -1
            android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear(r1, r12)
            r11.addView(r0, r1)
        L_0x07cb:
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = r6.stickersInfoCell
            if (r0 == 0) goto L_0x07e5
            org.telegram.ui.Cells.ShadowSectionCell r1 = r6.deleteInfoCell
            if (r1 != 0) goto L_0x07db
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r7, (int) r2, (java.lang.String) r3)
            r0.setBackground(r1)
            goto L_0x07e5
        L_0x07db:
            r1 = 2131165435(0x7var_fb, float:1.7945087E38)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r7, (int) r1, (java.lang.String) r3)
            r0.setBackground(r1)
        L_0x07e5:
            org.telegram.ui.Components.UndoView r0 = new org.telegram.ui.Components.UndoView
            r0.<init>(r7)
            r6.undoView = r0
            r10 = -1
            r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r12 = 83
            r13 = 1090519040(0x41000000, float:8.0)
            r14 = 0
            r15 = 1090519040(0x41000000, float:8.0)
            r16 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r9.addView(r0, r1)
            org.telegram.ui.Components.EditTextEmoji r0 = r6.nameTextView
            org.telegram.tgnet.TLRPC$Chat r1 = r6.currentChat
            java.lang.String r1 = r1.title
            r0.setText(r1)
            org.telegram.ui.Components.EditTextEmoji r0 = r6.nameTextView
            int r1 = r0.length()
            r0.setSelection(r1)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r6.info
            if (r0 == 0) goto L_0x081c
            org.telegram.ui.Components.EditTextBoldCursor r1 = r6.descriptionTextView
            java.lang.String r0 = r0.about
            r1.setText(r0)
        L_0x081c:
            r31.setAvatar()
            r6.updateFields(r8)
            android.view.View r0 = r6.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0009, code lost:
        r4 = getMessagesController().getChat(java.lang.Long.valueOf(r3.chatId));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$3(android.view.View r4) {
        /*
            r3 = this;
            org.telegram.ui.Components.ImageUpdater r4 = r3.imageUpdater
            boolean r4 = r4.isUploadingImage()
            if (r4 == 0) goto L_0x0009
            return
        L_0x0009:
            org.telegram.messenger.MessagesController r4 = r3.getMessagesController()
            long r0 = r3.chatId
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r0)
            org.telegram.tgnet.TLRPC$ChatPhoto r0 = r4.photo
            if (r0 == 0) goto L_0x006a
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_big
            if (r0 == 0) goto L_0x006a
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            android.app.Activity r1 = r3.getParentActivity()
            r0.setParentActivity(r1)
            org.telegram.tgnet.TLRPC$ChatPhoto r0 = r4.photo
            int r1 = r0.dc_id
            if (r1 == 0) goto L_0x0034
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_big
            r0.dc_id = r1
        L_0x0034:
            org.telegram.tgnet.TLRPC$ChatFull r0 = r3.info
            if (r0 == 0) goto L_0x005c
            org.telegram.tgnet.TLRPC$Photo r0 = r0.chat_photo
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r1 == 0) goto L_0x005c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r0 = r0.video_sizes
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x005c
            org.telegram.tgnet.TLRPC$ChatFull r0 = r3.info
            org.telegram.tgnet.TLRPC$Photo r0 = r0.chat_photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r0 = r0.video_sizes
            r1 = 0
            java.lang.Object r0 = r0.get(r1)
            org.telegram.tgnet.TLRPC$VideoSize r0 = (org.telegram.tgnet.TLRPC$VideoSize) r0
            org.telegram.tgnet.TLRPC$ChatFull r1 = r3.info
            org.telegram.tgnet.TLRPC$Photo r1 = r1.chat_photo
            org.telegram.messenger.ImageLocation r0 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$VideoSize) r0, (org.telegram.tgnet.TLRPC$Photo) r1)
            goto L_0x005d
        L_0x005c:
            r0 = 0
        L_0x005d:
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            org.telegram.tgnet.TLRPC$ChatPhoto r4 = r4.photo
            org.telegram.tgnet.TLRPC$FileLocation r4 = r4.photo_big
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r2 = r3.provider
            r1.openPhotoWithVideo(r4, r0, r2)
        L_0x006a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.lambda$createView$3(android.view.View):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new ChatEditActivity$$ExternalSyntheticLambda24(this), new ChatEditActivity$$ExternalSyntheticLambda3(this));
        this.cameraDrawable.setCurrentFrame(0);
        this.cameraDrawable.setCustomEndFrame(43);
        this.setAvatarCell.imageView.playAnimation();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4() {
        this.avatar = null;
        MessagesController.getInstance(this.currentAccount).changeChatAvatar(this.chatId, (TLRPC$TL_inputChatPhoto) null, (TLRPC$InputFile) null, (TLRPC$InputFile) null, 0.0d, (String) null, (TLRPC$FileLocation) null, (TLRPC$FileLocation) null, (Runnable) null);
        showAvatarProgress(false, true);
        this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) this.currentChat);
        this.cameraDrawable.setCurrentFrame(0);
        this.setAvatarCell.imageView.playAnimation();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(DialogInterface dialogInterface) {
        if (!this.imageUpdater.isUploadingImage()) {
            this.cameraDrawable.setCustomEndFrame(86);
            this.setAvatarCell.imageView.playAnimation();
            return;
        }
        this.cameraDrawable.setCurrentFrame(0, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$7(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$9(View view) {
        if (AndroidUtilities.isGoogleMapsInstalled(this)) {
            LocationActivity locationActivity = new LocationActivity(4);
            locationActivity.setDialogId(-this.chatId);
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            if (tLRPC$ChatFull != null) {
                TLRPC$ChannelLocation tLRPC$ChannelLocation = tLRPC$ChatFull.location;
                if (tLRPC$ChannelLocation instanceof TLRPC$TL_channelLocation) {
                    locationActivity.setInitialLocation((TLRPC$TL_channelLocation) tLRPC$ChannelLocation);
                }
            }
            locationActivity.setDelegate(new ChatEditActivity$$ExternalSyntheticLambda31(this));
            presentFragment(locationActivity);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$8(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
        TLRPC$TL_channelLocation tLRPC$TL_channelLocation = new TLRPC$TL_channelLocation();
        tLRPC$TL_channelLocation.address = tLRPC$MessageMedia.address;
        tLRPC$TL_channelLocation.geo_point = tLRPC$MessageMedia.geo;
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        tLRPC$ChatFull.location = tLRPC$TL_channelLocation;
        tLRPC$ChatFull.flags |= 32768;
        updateFields(false);
        getMessagesController().loadFullChat(this.chatId, 0, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$10(View view) {
        long j = this.chatId;
        TextDetailCell textDetailCell = this.locationCell;
        ChatEditTypeActivity chatEditTypeActivity = new ChatEditTypeActivity(j, textDetailCell != null && textDetailCell.getVisibility() == 0);
        chatEditTypeActivity.setInfo(this.info);
        presentFragment(chatEditTypeActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$11(View view) {
        ChatLinkActivity chatLinkActivity = new ChatLinkActivity(this.chatId);
        chatLinkActivity.setInfo(this.info);
        presentFragment(chatLinkActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$13(Context context, View view) {
        BottomSheet.Builder builder = new BottomSheet.Builder(context);
        builder.setApplyTopPadding(false);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        HeaderCell headerCell = new HeaderCell(context, "dialogTextBlue2", 23, 15, false);
        headerCell.setHeight(47);
        headerCell.setText(LocaleController.getString("ChatHistory", NUM));
        linearLayout.addView(headerCell);
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(1);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
        RadioButtonCell[] radioButtonCellArr = new RadioButtonCell[2];
        for (int i = 0; i < 2; i++) {
            radioButtonCellArr[i] = new RadioButtonCell(context, true);
            radioButtonCellArr[i].setTag(Integer.valueOf(i));
            radioButtonCellArr[i].setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (i == 0) {
                radioButtonCellArr[i].setTextAndValue(LocaleController.getString("ChatHistoryVisible", NUM), LocaleController.getString("ChatHistoryVisibleInfo", NUM), true, !this.historyHidden);
            } else if (ChatObject.isChannel(this.currentChat)) {
                radioButtonCellArr[i].setTextAndValue(LocaleController.getString("ChatHistoryHidden", NUM), LocaleController.getString("ChatHistoryHiddenInfo", NUM), false, this.historyHidden);
            } else {
                radioButtonCellArr[i].setTextAndValue(LocaleController.getString("ChatHistoryHidden", NUM), LocaleController.getString("ChatHistoryHiddenInfo2", NUM), false, this.historyHidden);
            }
            linearLayout2.addView(radioButtonCellArr[i], LayoutHelper.createLinear(-1, -2));
            radioButtonCellArr[i].setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda21(this, radioButtonCellArr, builder));
        }
        builder.setCustomView(linearLayout);
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$12(RadioButtonCell[] radioButtonCellArr, BottomSheet.Builder builder, View view) {
        Integer num = (Integer) view.getTag();
        boolean z = false;
        radioButtonCellArr[0].setChecked(num.intValue() == 0, true);
        radioButtonCellArr[1].setChecked(num.intValue() == 1, true);
        if (num.intValue() == 1) {
            z = true;
        }
        this.historyHidden = z;
        builder.getDismissRunnable().run();
        updateFields(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$14(View view) {
        boolean z = !this.signMessages;
        this.signMessages = z;
        ((TextCheckCell) view).setChecked(z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$15(View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", this.chatId);
        bundle.putInt("type", (this.isChannel || this.currentChat.gigagroup) ? 0 : 3);
        ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle);
        chatUsersActivity.setInfo(this.info);
        presentFragment(chatUsersActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$16(View view) {
        ManageLinksActivity manageLinksActivity = new ManageLinksActivity(this.chatId, 0, 0);
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        manageLinksActivity.setInfo(tLRPC$ChatFull, tLRPC$ChatFull.exported_invite);
        presentFragment(manageLinksActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$17(View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", this.chatId);
        ChatReactionsEditActivity chatReactionsEditActivity = new ChatReactionsEditActivity(bundle);
        chatReactionsEditActivity.setInfo(this.info);
        presentFragment(chatReactionsEditActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$18(View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", this.chatId);
        bundle.putInt("type", 1);
        ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle);
        chatUsersActivity.setInfo(this.info);
        presentFragment(chatUsersActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$19(View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", this.chatId);
        bundle.putInt("type", 2);
        ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle);
        chatUsersActivity.setInfo(this.info);
        presentFragment(chatUsersActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$20(View view) {
        presentFragment(new MemberRequestsActivity(this.chatId));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$21(View view) {
        presentFragment(new ChannelAdminLogActivity(this.currentChat));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$22(View view) {
        presentFragment(new ChannelAdminLogActivity(this.currentChat));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$23(View view) {
        GroupStickersActivity groupStickersActivity = new GroupStickersActivity(this.currentChat.id);
        groupStickersActivity.setInfo(this.info);
        presentFragment(groupStickersActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$25(View view) {
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, true, false, this.currentChat, (TLRPC$User) null, false, true, false, new ChatEditActivity$$ExternalSyntheticLambda27(this), (Theme.ResourcesProvider) null);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$24(boolean z) {
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(-this.chatId));
        } else {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        finishFragment();
        getNotificationCenter().postNotificationName(NotificationCenter.needDeleteDialog, Long.valueOf(-this.currentChat.id), null, this.currentChat, Boolean.valueOf(z));
    }

    private void setAvatar() {
        TLRPC$Chat chat;
        if (this.avatarImage != null && (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) != null) {
            this.currentChat = chat;
            TLRPC$ChatPhoto tLRPC$ChatPhoto = chat.photo;
            boolean z = false;
            if (tLRPC$ChatPhoto != null) {
                this.avatar = tLRPC$ChatPhoto.photo_small;
                ImageLocation forUserOrChat = ImageLocation.getForUserOrChat(chat, 1);
                this.avatarImage.setForUserOrChat(this.currentChat, this.avatarDrawable);
                if (forUserOrChat != null) {
                    z = true;
                }
            } else {
                this.avatarImage.setImageDrawable(this.avatarDrawable);
            }
            if (this.setAvatarCell != null) {
                if (z || this.imageUpdater.isUploadingImage()) {
                    this.setAvatarCell.setTextAndIcon(LocaleController.getString("ChatSetNewPhoto", NUM), NUM, true);
                } else {
                    this.setAvatarCell.setTextAndIcon(LocaleController.getString("ChatSetPhotoOrVideo", NUM), NUM, true);
                }
                if (this.cameraDrawable == null) {
                    this.cameraDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(50.0f), AndroidUtilities.dp(50.0f), false, (int[]) null);
                }
                this.setAvatarCell.imageView.setTranslationY((float) (-AndroidUtilities.dp(9.0f)));
                this.setAvatarCell.imageView.setTranslationX((float) (-AndroidUtilities.dp(8.0f)));
                this.setAvatarCell.imageView.setAnimation(this.cameraDrawable);
            }
            if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                PhotoViewer.getInstance().checkCurrentImageVisibility();
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        EditTextBoldCursor editTextBoldCursor;
        if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC$ChatFull tLRPC$ChatFull = objArr[0];
            if (tLRPC$ChatFull.id == this.chatId) {
                if (this.info == null && (editTextBoldCursor = this.descriptionTextView) != null) {
                    editTextBoldCursor.setText(tLRPC$ChatFull.about);
                }
                boolean z = true;
                boolean z2 = this.info == null;
                this.info = tLRPC$ChatFull;
                if (ChatObject.isChannel(this.currentChat) && !this.info.hidden_prehistory) {
                    z = false;
                }
                this.historyHidden = z;
                updateFields(false);
                if (z2) {
                    loadLinksCount();
                }
            }
        } else if (i == NotificationCenter.updateInterfaces) {
            if ((objArr[0].intValue() & MessagesController.UPDATE_MASK_AVATAR) != 0) {
                setAvatar();
            }
        } else if (i == NotificationCenter.chatAvailableReactionsUpdated) {
            long longValue = objArr[0].longValue();
            if (longValue == this.chatId) {
                TLRPC$ChatFull chatFull = getMessagesController().getChatFull(longValue);
                this.info = chatFull;
                if (chatFull != null) {
                    this.availableReactions = chatFull.available_reactions;
                }
                updateReactionsCell();
            }
        }
    }

    public void onUploadProgressChanged(float f) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView != null) {
            radialProgressView.setProgress(f);
        }
    }

    public void didStartUpload(boolean z) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView != null) {
            radialProgressView.setProgress(0.0f);
        }
    }

    public void didUploadPhoto(TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, String str, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
        AndroidUtilities.runOnUIThread(new ChatEditActivity$$ExternalSyntheticLambda25(this, tLRPC$PhotoSize2, tLRPC$InputFile, tLRPC$InputFile2, d, str, tLRPC$PhotoSize));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didUploadPhoto$26(TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, String str, TLRPC$PhotoSize tLRPC$PhotoSize2) {
        TLRPC$PhotoSize tLRPC$PhotoSize3 = tLRPC$PhotoSize;
        TLRPC$FileLocation tLRPC$FileLocation = tLRPC$PhotoSize3.location;
        this.avatar = tLRPC$FileLocation;
        if (tLRPC$InputFile == null && tLRPC$InputFile2 == null) {
            this.avatarImage.setImage(ImageLocation.getForLocal(tLRPC$FileLocation), "50_50", (Drawable) this.avatarDrawable, (Object) this.currentChat);
            this.setAvatarCell.setTextAndIcon(LocaleController.getString("ChatSetNewPhoto", NUM), NUM, true);
            if (this.cameraDrawable == null) {
                this.cameraDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(50.0f), AndroidUtilities.dp(50.0f), false, (int[]) null);
            }
            this.setAvatarCell.imageView.setTranslationY((float) (-AndroidUtilities.dp(9.0f)));
            this.setAvatarCell.imageView.setTranslationX((float) (-AndroidUtilities.dp(8.0f)));
            this.setAvatarCell.imageView.setAnimation(this.cameraDrawable);
            showAvatarProgress(true, false);
            return;
        }
        getMessagesController().changeChatAvatar(this.chatId, (TLRPC$TL_inputChatPhoto) null, tLRPC$InputFile, tLRPC$InputFile2, d, str, tLRPC$PhotoSize3.location, tLRPC$PhotoSize2.location, (Runnable) null);
        if (this.createAfterUpload) {
            try {
                AlertDialog alertDialog = this.progressDialog;
                if (alertDialog != null && alertDialog.isShowing()) {
                    this.progressDialog.dismiss();
                    this.progressDialog = null;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.donePressed = false;
            this.doneButton.performClick();
        }
        showAvatarProgress(false, true);
    }

    public String getInitialSearchString() {
        return this.nameTextView.getText().toString();
    }

    public void showConvertTooltip() {
        this.undoView.showWithAction(0, 76, (Runnable) null);
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        String str;
        EditTextEmoji editTextEmoji;
        EditTextBoldCursor editTextBoldCursor;
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        if (tLRPC$ChatFull == null || (str = tLRPC$ChatFull.about) == null) {
            str = "";
        }
        if ((tLRPC$ChatFull == null || !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory == this.historyHidden) && (((editTextEmoji = this.nameTextView) == null || this.currentChat.title.equals(editTextEmoji.getText().toString())) && (((editTextBoldCursor = this.descriptionTextView) == null || str.equals(editTextBoldCursor.getText().toString())) && this.signMessages == this.currentChat.signatures))) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", NUM));
        if (this.isChannel) {
            builder.setMessage(LocaleController.getString("ChannelSettingsChangedAlert", NUM));
        } else {
            builder.setMessage(LocaleController.getString("GroupSettingsChangedAlert", NUM));
        }
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new ChatEditActivity$$ExternalSyntheticLambda2(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new ChatEditActivity$$ExternalSyntheticLambda1(this));
        showDialog(builder.create());
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$27(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$28(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    private int getAdminCount() {
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        if (tLRPC$ChatFull == null) {
            return 1;
        }
        int size = tLRPC$ChatFull.participants.participants.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC$ChatParticipant tLRPC$ChatParticipant = this.info.participants.participants.get(i2);
            if ((tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantAdmin) || (tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantCreator)) {
                i++;
            }
        }
        return i;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x005b, code lost:
        r1 = r8.info;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processDone() {
        /*
            r8 = this;
            boolean r0 = r8.donePressed
            if (r0 != 0) goto L_0x0105
            org.telegram.ui.Components.EditTextEmoji r0 = r8.nameTextView
            if (r0 != 0) goto L_0x000a
            goto L_0x0105
        L_0x000a:
            int r0 = r0.length()
            if (r0 != 0) goto L_0x002c
            android.app.Activity r0 = r8.getParentActivity()
            java.lang.String r1 = "vibrator"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.os.Vibrator r0 = (android.os.Vibrator) r0
            if (r0 == 0) goto L_0x0023
            r1 = 200(0xc8, double:9.9E-322)
            r0.vibrate(r1)
        L_0x0023:
            org.telegram.ui.Components.EditTextEmoji r0 = r8.nameTextView
            r1 = 1073741824(0x40000000, float:2.0)
            r2 = 0
            org.telegram.messenger.AndroidUtilities.shakeView(r0, r1, r2)
            return
        L_0x002c:
            r0 = 1
            r8.donePressed = r0
            org.telegram.tgnet.TLRPC$Chat r1 = r8.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 != 0) goto L_0x004f
            boolean r1 = r8.historyHidden
            if (r1 != 0) goto L_0x004f
            org.telegram.messenger.MessagesController r2 = r8.getMessagesController()
            android.app.Activity r3 = r8.getParentActivity()
            long r4 = r8.chatId
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda28 r7 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda28
            r7.<init>(r8)
            r6 = r8
            r2.convertToMegaGroup(r3, r4, r6, r7)
            return
        L_0x004f:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r8.info
            if (r1 == 0) goto L_0x0070
            org.telegram.tgnet.TLRPC$Chat r1 = r8.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 == 0) goto L_0x0070
            org.telegram.tgnet.TLRPC$ChatFull r1 = r8.info
            boolean r2 = r1.hidden_prehistory
            boolean r3 = r8.historyHidden
            if (r2 == r3) goto L_0x0070
            r1.hidden_prehistory = r3
            org.telegram.messenger.MessagesController r1 = r8.getMessagesController()
            long r2 = r8.chatId
            boolean r4 = r8.historyHidden
            r1.toggleChannelInvitesHistory(r2, r4)
        L_0x0070:
            org.telegram.ui.Components.ImageUpdater r1 = r8.imageUpdater
            boolean r1 = r1.isUploadingImage()
            if (r1 == 0) goto L_0x0094
            r8.createAfterUpload = r0
            org.telegram.ui.ActionBar.AlertDialog r0 = new org.telegram.ui.ActionBar.AlertDialog
            android.app.Activity r1 = r8.getParentActivity()
            r2 = 3
            r0.<init>(r1, r2)
            r8.progressDialog = r0
            org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda0 r1 = new org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda0
            r1.<init>(r8)
            r0.setOnCancelListener(r1)
            org.telegram.ui.ActionBar.AlertDialog r0 = r8.progressDialog
            r0.show()
            return
        L_0x0094:
            org.telegram.tgnet.TLRPC$Chat r1 = r8.currentChat
            java.lang.String r1 = r1.title
            org.telegram.ui.Components.EditTextEmoji r2 = r8.nameTextView
            android.text.Editable r2 = r2.getText()
            java.lang.String r2 = r2.toString()
            boolean r1 = r1.equals(r2)
            if (r1 != 0) goto L_0x00bb
            org.telegram.messenger.MessagesController r1 = r8.getMessagesController()
            long r2 = r8.chatId
            org.telegram.ui.Components.EditTextEmoji r4 = r8.nameTextView
            android.text.Editable r4 = r4.getText()
            java.lang.String r4 = r4.toString()
            r1.changeChatTitle(r2, r4)
        L_0x00bb:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r8.info
            if (r1 == 0) goto L_0x00c4
            java.lang.String r1 = r1.about
            if (r1 == 0) goto L_0x00c4
            goto L_0x00c6
        L_0x00c4:
            java.lang.String r1 = ""
        L_0x00c6:
            org.telegram.ui.Components.EditTextBoldCursor r2 = r8.descriptionTextView
            if (r2 == 0) goto L_0x00ed
            android.text.Editable r2 = r2.getText()
            java.lang.String r2 = r2.toString()
            boolean r1 = r1.equals(r2)
            if (r1 != 0) goto L_0x00ed
            org.telegram.messenger.MessagesController r1 = r8.getMessagesController()
            long r2 = r8.chatId
            org.telegram.ui.Components.EditTextBoldCursor r4 = r8.descriptionTextView
            android.text.Editable r4 = r4.getText()
            java.lang.String r4 = r4.toString()
            org.telegram.tgnet.TLRPC$ChatFull r5 = r8.info
            r1.updateChatAbout(r2, r4, r5)
        L_0x00ed:
            boolean r1 = r8.signMessages
            org.telegram.tgnet.TLRPC$Chat r2 = r8.currentChat
            boolean r3 = r2.signatures
            if (r1 == r3) goto L_0x0102
            r2.signatures = r0
            org.telegram.messenger.MessagesController r0 = r8.getMessagesController()
            long r1 = r8.chatId
            boolean r3 = r8.signMessages
            r0.toggleChannelSignatures(r1, r3)
        L_0x0102:
            r8.finishFragment()
        L_0x0105:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.processDone():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$29(long j) {
        if (j == 0) {
            this.donePressed = false;
            return;
        }
        this.chatId = j;
        this.currentChat = getMessagesController().getChat(Long.valueOf(j));
        this.donePressed = false;
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        if (tLRPC$ChatFull != null) {
            tLRPC$ChatFull.hidden_prehistory = true;
        }
        processDone();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$30(DialogInterface dialogInterface) {
        this.createAfterUpload = false;
        this.progressDialog = null;
        this.donePressed = false;
    }

    private void showAvatarProgress(final boolean z, boolean z2) {
        if (this.avatarProgressView != null) {
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.avatarAnimation = null;
            }
            if (z2) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.avatarAnimation = animatorSet2;
                if (z) {
                    this.avatarProgressView.setVisibility(0);
                    this.avatarOverlay.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.avatarOverlay, View.ALPHA, new float[]{1.0f})});
                } else {
                    animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.avatarOverlay, View.ALPHA, new float[]{0.0f})});
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (ChatEditActivity.this.avatarAnimation != null && ChatEditActivity.this.avatarProgressView != null) {
                            if (!z) {
                                ChatEditActivity.this.avatarProgressView.setVisibility(4);
                                ChatEditActivity.this.avatarOverlay.setVisibility(4);
                            }
                            AnimatorSet unused = ChatEditActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        AnimatorSet unused = ChatEditActivity.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (z) {
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
                this.avatarOverlay.setAlpha(1.0f);
                this.avatarOverlay.setVisibility(0);
            } else {
                this.avatarProgressView.setAlpha(0.0f);
                this.avatarProgressView.setVisibility(4);
                this.avatarOverlay.setAlpha(0.0f);
                this.avatarOverlay.setVisibility(4);
            }
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        this.imageUpdater.onActivityResult(i, i2, intent);
    }

    public void saveSelfArgs(Bundle bundle) {
        String str;
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (!(imageUpdater2 == null || (str = imageUpdater2.currentPicturePath) == null)) {
            bundle.putString("path", str);
        }
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            String obj = editTextEmoji.getText().toString();
            if (obj.length() != 0) {
                bundle.putString("nameTextView", obj);
            }
        }
    }

    public void setInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
        if (tLRPC$ChatFull != null) {
            if (this.currentChat == null) {
                this.currentChat = getMessagesController().getChat(Long.valueOf(this.chatId));
            }
            this.historyHidden = !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory;
            this.availableReactions = this.info.available_reactions;
        }
    }

    private void updateFields(boolean z) {
        int i;
        String str;
        int i2;
        String str2;
        TextDetailCell textDetailCell;
        TextDetailCell textDetailCell2;
        String str3;
        int i3;
        String str4;
        int i4;
        String str5;
        TextDetailCell textDetailCell3;
        TLRPC$ChatFull tLRPC$ChatFull;
        TextDetailCell textDetailCell4;
        TextDetailCell textDetailCell5;
        TextDetailCell textDetailCell6;
        TLRPC$Chat chat;
        if (z && (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) != null) {
            this.currentChat = chat;
        }
        boolean isEmpty = TextUtils.isEmpty(this.currentChat.username);
        TextDetailCell textDetailCell7 = this.historyCell;
        if (textDetailCell7 != null) {
            TLRPC$ChatFull tLRPC$ChatFull2 = this.info;
            if (tLRPC$ChatFull2 == null || !(tLRPC$ChatFull2.location instanceof TLRPC$TL_channelLocation)) {
                textDetailCell7.setVisibility((!isEmpty || !(tLRPC$ChatFull2 == null || tLRPC$ChatFull2.linked_chat_id == 0)) ? 8 : 0);
            } else {
                textDetailCell7.setVisibility(8);
            }
        }
        ShadowSectionCell shadowSectionCell = this.settingsSectionCell;
        if (shadowSectionCell != null) {
            shadowSectionCell.setVisibility((this.signCell == null && this.typeCell == null && ((textDetailCell4 = this.linkedCell) == null || textDetailCell4.getVisibility() != 0) && (((textDetailCell5 = this.historyCell) == null || textDetailCell5.getVisibility() != 0) && ((textDetailCell6 = this.locationCell) == null || textDetailCell6.getVisibility() != 0))) ? 8 : 0);
        }
        TextCell textCell = this.logCell;
        if (textCell != null) {
            TLRPC$Chat tLRPC$Chat = this.currentChat;
            textCell.setVisibility((!tLRPC$Chat.megagroup || tLRPC$Chat.gigagroup || ((tLRPC$ChatFull = this.info) != null && tLRPC$ChatFull.participants_count > 200)) ? 0 : 8);
        }
        TextDetailCell textDetailCell8 = this.linkedCell;
        if (textDetailCell8 != null) {
            TLRPC$ChatFull tLRPC$ChatFull3 = this.info;
            if (tLRPC$ChatFull3 == null || (!this.isChannel && tLRPC$ChatFull3.linked_chat_id == 0)) {
                textDetailCell8.setVisibility(8);
            } else {
                textDetailCell8.setVisibility(0);
                if (this.info.linked_chat_id == 0) {
                    this.linkedCell.setTextAndValue(LocaleController.getString("Discussion", NUM), LocaleController.getString("DiscussionInfo", NUM), true);
                } else {
                    TLRPC$Chat chat2 = getMessagesController().getChat(Long.valueOf(this.info.linked_chat_id));
                    if (chat2 == null) {
                        this.linkedCell.setVisibility(8);
                    } else if (this.isChannel) {
                        if (TextUtils.isEmpty(chat2.username)) {
                            this.linkedCell.setTextAndValue(LocaleController.getString("Discussion", NUM), chat2.title, true);
                        } else {
                            this.linkedCell.setTextAndValue(LocaleController.getString("Discussion", NUM), "@" + chat2.username, true);
                        }
                    } else if (TextUtils.isEmpty(chat2.username)) {
                        this.linkedCell.setTextAndValue(LocaleController.getString("LinkedChannel", NUM), chat2.title, false);
                    } else {
                        this.linkedCell.setTextAndValue(LocaleController.getString("LinkedChannel", NUM), "@" + chat2.username, false);
                    }
                }
            }
        }
        TextDetailCell textDetailCell9 = this.locationCell;
        if (textDetailCell9 != null) {
            TLRPC$ChatFull tLRPC$ChatFull4 = this.info;
            if (tLRPC$ChatFull4 == null || !tLRPC$ChatFull4.can_set_location) {
                textDetailCell9.setVisibility(8);
            } else {
                textDetailCell9.setVisibility(0);
                TLRPC$ChannelLocation tLRPC$ChannelLocation = this.info.location;
                if (tLRPC$ChannelLocation instanceof TLRPC$TL_channelLocation) {
                    this.locationCell.setTextAndValue(LocaleController.getString("AttachLocation", NUM), ((TLRPC$TL_channelLocation) tLRPC$ChannelLocation).address, true);
                } else {
                    this.locationCell.setTextAndValue(LocaleController.getString("AttachLocation", NUM), "Unknown address", true);
                }
            }
        }
        if (this.typeCell != null) {
            TLRPC$ChatFull tLRPC$ChatFull5 = this.info;
            if (tLRPC$ChatFull5 == null || !(tLRPC$ChatFull5.location instanceof TLRPC$TL_channelLocation)) {
                boolean z2 = this.currentChat.noforwards;
                if (this.isChannel) {
                    if (!isEmpty) {
                        i4 = NUM;
                        str4 = "TypePublic";
                    } else if (z2) {
                        i4 = NUM;
                        str4 = "TypePrivateRestrictedForwards";
                    } else {
                        i4 = NUM;
                        str4 = "TypePrivate";
                    }
                    str2 = LocaleController.getString(str4, i4);
                } else {
                    if (!isEmpty) {
                        i3 = NUM;
                        str3 = "TypePublicGroup";
                    } else if (z2) {
                        i3 = NUM;
                        str3 = "TypePrivateGroupRestrictedForwards";
                    } else {
                        i3 = NUM;
                        str3 = "TypePrivateGroup";
                    }
                    str2 = LocaleController.getString(str3, i3);
                }
                if (this.isChannel) {
                    TextDetailCell textDetailCell10 = this.typeCell;
                    String string = LocaleController.getString("ChannelType", NUM);
                    TextDetailCell textDetailCell11 = this.historyCell;
                    textDetailCell10.setTextAndValue(string, str2, (textDetailCell11 != null && textDetailCell11.getVisibility() == 0) || ((textDetailCell2 = this.linkedCell) != null && textDetailCell2.getVisibility() == 0));
                } else {
                    TextDetailCell textDetailCell12 = this.typeCell;
                    String string2 = LocaleController.getString("GroupType", NUM);
                    TextDetailCell textDetailCell13 = this.historyCell;
                    textDetailCell12.setTextAndValue(string2, str2, (textDetailCell13 != null && textDetailCell13.getVisibility() == 0) || ((textDetailCell = this.linkedCell) != null && textDetailCell.getVisibility() == 0));
                }
            } else {
                if (isEmpty) {
                    str5 = LocaleController.getString("TypeLocationGroupEdit", NUM);
                } else {
                    str5 = String.format("https://" + getMessagesController().linkPrefix + "/%s", new Object[]{this.currentChat.username});
                }
                TextDetailCell textDetailCell14 = this.typeCell;
                String string3 = LocaleController.getString("TypeLocationGroup", NUM);
                TextDetailCell textDetailCell15 = this.historyCell;
                textDetailCell14.setTextAndValue(string3, str5, (textDetailCell15 != null && textDetailCell15.getVisibility() == 0) || ((textDetailCell3 = this.linkedCell) != null && textDetailCell3.getVisibility() == 0));
            }
        }
        if (this.historyCell != null) {
            if (this.historyHidden) {
                i2 = NUM;
                str = "ChatHistoryHidden";
            } else {
                i2 = NUM;
                str = "ChatHistoryVisible";
            }
            this.historyCell.setTextAndValue(LocaleController.getString("ChatHistory", NUM), LocaleController.getString(str, i2), false);
        }
        TextCell textCell2 = this.membersCell;
        if (textCell2 != null) {
            if (this.info != null) {
                TextCell textCell3 = this.memberRequestsCell;
                if (textCell3 != null) {
                    if (textCell3.getParent() == null) {
                        this.infoContainer.addView(this.memberRequestsCell, this.infoContainer.indexOfChild(this.membersCell) + 1, LayoutHelper.createLinear(-1, -2));
                    }
                    this.memberRequestsCell.setVisibility(this.info.requests_pending > 0 ? 0 : 8);
                }
                if (this.isChannel) {
                    this.membersCell.setTextAndValueAndIcon(LocaleController.getString("ChannelSubscribers", NUM), String.format("%d", new Object[]{Integer.valueOf(this.info.participants_count)}), NUM, true);
                    TextCell textCell4 = this.blockCell;
                    String string4 = LocaleController.getString("ChannelBlacklist", NUM);
                    TLRPC$ChatFull tLRPC$ChatFull6 = this.info;
                    String format = String.format("%d", new Object[]{Integer.valueOf(Math.max(tLRPC$ChatFull6.banned_count, tLRPC$ChatFull6.kicked_count))});
                    TextCell textCell5 = this.logCell;
                    textCell4.setTextAndValueAndIcon(string4, format, NUM, textCell5 != null && textCell5.getVisibility() == 0);
                } else {
                    if (ChatObject.isChannel(this.currentChat)) {
                        this.membersCell.setTextAndValueAndIcon(LocaleController.getString("ChannelMembers", NUM), String.format("%d", new Object[]{Integer.valueOf(this.info.participants_count)}), NUM, true);
                    } else {
                        this.membersCell.setTextAndValueAndIcon(LocaleController.getString("ChannelMembers", NUM), String.format("%d", new Object[]{Integer.valueOf(this.info.participants.participants.size())}), NUM, this.memberRequestsCell.getVisibility() == 0);
                    }
                    TLRPC$Chat tLRPC$Chat2 = this.currentChat;
                    if (tLRPC$Chat2.gigagroup) {
                        TextCell textCell6 = this.blockCell;
                        String string5 = LocaleController.getString("ChannelBlacklist", NUM);
                        TLRPC$ChatFull tLRPC$ChatFull7 = this.info;
                        String format2 = String.format("%d", new Object[]{Integer.valueOf(Math.max(tLRPC$ChatFull7.banned_count, tLRPC$ChatFull7.kicked_count))});
                        TextCell textCell7 = this.logCell;
                        textCell6.setTextAndValueAndIcon(string5, format2, NUM, textCell7 != null && textCell7.getVisibility() == 0);
                    } else {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights = tLRPC$Chat2.default_banned_rights;
                        if (tLRPC$TL_chatBannedRights != null) {
                            int i5 = !tLRPC$TL_chatBannedRights.send_stickers;
                            if (!tLRPC$TL_chatBannedRights.send_media) {
                                i5++;
                            }
                            if (!tLRPC$TL_chatBannedRights.embed_links) {
                                i5++;
                            }
                            if (!tLRPC$TL_chatBannedRights.send_messages) {
                                i5++;
                            }
                            if (!tLRPC$TL_chatBannedRights.pin_messages) {
                                i5++;
                            }
                            if (!tLRPC$TL_chatBannedRights.send_polls) {
                                i5++;
                            }
                            if (!tLRPC$TL_chatBannedRights.invite_users) {
                                i5++;
                            }
                            i = !tLRPC$TL_chatBannedRights.change_info ? i5 + 1 : i5;
                        } else {
                            i = 8;
                        }
                        this.blockCell.setTextAndValueAndIcon(LocaleController.getString("ChannelPermissions", NUM), String.format("%d/%d", new Object[]{Integer.valueOf(i), 8}), NUM, true);
                    }
                    TextCell textCell8 = this.memberRequestsCell;
                    if (textCell8 != null) {
                        String string6 = LocaleController.getString("MemberRequests", NUM);
                        String format3 = String.format("%d", new Object[]{Integer.valueOf(this.info.requests_pending)});
                        TextCell textCell9 = this.logCell;
                        textCell8.setTextAndValueAndIcon(string6, format3, NUM, textCell9 != null && textCell9.getVisibility() == 0);
                    }
                }
                TextCell textCell10 = this.adminCell;
                String string7 = LocaleController.getString("ChannelAdministrators", NUM);
                Object[] objArr = new Object[1];
                objArr[0] = Integer.valueOf(ChatObject.isChannel(this.currentChat) ? this.info.admins_count : getAdminCount());
                textCell10.setTextAndValueAndIcon(string7, String.format("%d", objArr), NUM, true);
            } else {
                if (this.isChannel) {
                    textCell2.setTextAndIcon(LocaleController.getString("ChannelSubscribers", NUM), NUM, true);
                    TextCell textCell11 = this.blockCell;
                    String string8 = LocaleController.getString("ChannelBlacklist", NUM);
                    TextCell textCell12 = this.logCell;
                    textCell11.setTextAndIcon(string8, NUM, textCell12 != null && textCell12.getVisibility() == 0);
                } else {
                    String string9 = LocaleController.getString("ChannelMembers", NUM);
                    TextCell textCell13 = this.logCell;
                    textCell2.setTextAndIcon(string9, NUM, textCell13 != null && textCell13.getVisibility() == 0);
                    if (this.currentChat.gigagroup) {
                        TextCell textCell14 = this.blockCell;
                        String string10 = LocaleController.getString("ChannelBlacklist", NUM);
                        TextCell textCell15 = this.logCell;
                        textCell14.setTextAndIcon(string10, NUM, textCell15 != null && textCell15.getVisibility() == 0);
                    } else {
                        this.blockCell.setTextAndIcon(LocaleController.getString("ChannelPermissions", NUM), NUM, true);
                    }
                }
                this.adminCell.setTextAndIcon(LocaleController.getString("ChannelAdministrators", NUM), NUM, true);
            }
            this.reactionsCell.setVisibility(ChatObject.canChangeChatInfo(this.currentChat) ? 0 : 8);
            updateReactionsCell();
            if (this.info == null || !ChatObject.canUserDoAdminAction(this.currentChat, 3) || (!isEmpty && this.currentChat.creator)) {
                this.inviteLinksCell.setVisibility(8);
            } else if (this.info.invitesCount > 0) {
                this.inviteLinksCell.setTextAndValueAndIcon(LocaleController.getString("InviteLinks", NUM), Integer.toString(this.info.invitesCount), NUM, true);
            } else {
                this.inviteLinksCell.setTextAndValueAndIcon(LocaleController.getString("InviteLinks", NUM), "1", NUM, true);
            }
        }
        TextCell textCell16 = this.stickersCell;
        if (textCell16 != null && this.info != null) {
            String string11 = LocaleController.getString(NUM);
            TLRPC$StickerSet tLRPC$StickerSet = this.info.stickerset;
            textCell16.setTextAndValueAndIcon(string11, tLRPC$StickerSet != null ? tLRPC$StickerSet.title : LocaleController.getString(NUM), NUM, false);
        }
    }

    private void updateReactionsCell() {
        String str;
        int i = 0;
        for (int i2 = 0; i2 < this.availableReactions.size(); i2++) {
            TLRPC$TL_availableReaction tLRPC$TL_availableReaction = getMediaDataController().getReactionsMap().get(this.availableReactions.get(i2));
            if (tLRPC$TL_availableReaction != null && !tLRPC$TL_availableReaction.inactive) {
                i++;
            }
        }
        int min = Math.min(getMediaDataController().getEnabledReactionsList().size(), i);
        TextCell textCell = this.reactionsCell;
        String string = LocaleController.getString("Reactions", NUM);
        if (min == 0) {
            str = LocaleController.getString("ReactionsOff", NUM);
        } else {
            str = LocaleController.formatString("ReactionsCount", NUM, Integer.valueOf(min), Integer.valueOf(getMediaDataController().getEnabledReactionsList().size()));
        }
        textCell.setTextAndValueAndIcon(string, str, NUM, true);
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ChatEditActivity$$ExternalSyntheticLambda30 chatEditActivity$$ExternalSyntheticLambda30 = new ChatEditActivity$$ExternalSyntheticLambda30(this);
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.setAvatarCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.setAvatarCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        arrayList.add(new ThemeDescription((View) this.setAvatarCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        arrayList.add(new ThemeDescription(this.membersCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.membersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.membersCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription(this.adminCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.adminCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.adminCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription(this.inviteLinksCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.inviteLinksCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.inviteLinksCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        if (this.memberRequestsCell != null) {
            arrayList.add(new ThemeDescription(this.memberRequestsCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription((View) this.memberRequestsCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) this.memberRequestsCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        }
        arrayList.add(new ThemeDescription(this.blockCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.blockCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.blockCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription(this.logCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.logCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.logCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription(this.typeCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.historyCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.locationCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.locationCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.locationCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.avatarContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.settingsContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.typeEditContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.deleteContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.stickersContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.infoContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.settingsTopSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.settingsSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.deleteInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.signCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.deleteCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.deleteCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription(this.stickersCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.stickersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.stickersInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.stickersInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        ChatEditActivity$$ExternalSyntheticLambda30 chatEditActivity$$ExternalSyntheticLambda302 = chatEditActivity$$ExternalSyntheticLambda30;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, chatEditActivity$$ExternalSyntheticLambda302, "avatar_text"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatEditActivity$$ExternalSyntheticLambda302, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatEditActivity$$ExternalSyntheticLambda302, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatEditActivity$$ExternalSyntheticLambda302, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatEditActivity$$ExternalSyntheticLambda302, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatEditActivity$$ExternalSyntheticLambda302, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatEditActivity$$ExternalSyntheticLambda302, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatEditActivity$$ExternalSyntheticLambda302, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.reactionsCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.reactionsCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.reactionsCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$31() {
        BackupImageView backupImageView = this.avatarImage;
        if (backupImageView != null) {
            backupImageView.invalidate();
        }
    }
}
