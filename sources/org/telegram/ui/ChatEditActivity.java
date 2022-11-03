package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChannelLocation;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$ChatReactions;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$Reaction;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_channelLocation;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_chatReactionsNone;
import org.telegram.tgnet.TLRPC$TL_chatReactionsSome;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_exportedChatInvites;
import org.telegram.tgnet.TLRPC$TL_messages_getExportedChatInvites;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_reactionEmoji;
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
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.LocationActivity;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes3.dex */
public class ChatEditActivity extends BaseFragment implements ImageUpdater.ImageUpdaterDelegate, NotificationCenter.NotificationCenterDelegate {
    private TextCell adminCell;
    private TLRPC$ChatReactions availableReactions;
    private TLRPC$FileLocation avatar;
    private AnimatorSet avatarAnimation;
    private LinearLayout avatarContainer;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private View avatarOverlay;
    private RadialProgressView avatarProgressView;
    private TextCell blockCell;
    RLottieDrawable cameraDrawable;
    private boolean canForum;
    private long chatId;
    private boolean createAfterUpload;
    private TLRPC$Chat currentChat;
    private TextSettingsCell deleteCell;
    private FrameLayout deleteContainer;
    private ShadowSectionCell deleteInfoCell;
    private EditTextBoldCursor descriptionTextView;
    private View doneButton;
    private boolean donePressed;
    private boolean forum;
    private TextCell forumsCell;
    private TextCell historyCell;
    private boolean historyHidden;
    private ImageUpdater imageUpdater;
    private TLRPC$ChatFull info;
    private LinearLayout infoContainer;
    private ShadowSectionCell infoSectionCell;
    private TextCell inviteLinksCell;
    private boolean isChannel;
    private LinearLayout linearLayout;
    private TextCell linkedCell;
    private TextCell locationCell;
    private TextCell logCell;
    private TextCell memberRequestsCell;
    private TextCell membersCell;
    private EditTextEmoji nameTextView;
    private AlertDialog progressDialog;
    private PhotoViewer.PhotoViewerProvider provider;
    private TextCell reactionsCell;
    private TextCell setAvatarCell;
    private LinearLayout settingsContainer;
    private TextInfoPrivacyCell settingsSectionCell;
    private ShadowSectionCell settingsTopSectionCell;
    private TextCell signCell;
    private boolean signMessages;
    private TextCell stickersCell;
    private FrameLayout stickersContainer;
    private TextInfoPrivacyCell stickersInfoCell;
    private TextCell typeCell;
    private LinearLayout typeEditContainer;
    private UndoView undoView;
    private ValueAnimator updateHistoryShowAnimator;

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$2(View view, MotionEvent motionEvent) {
        return true;
    }

    public ChatEditActivity(Bundle bundle) {
        super(bundle);
        this.provider = new PhotoViewer.EmptyPhotoViewerProvider() { // from class: org.telegram.ui.ChatEditActivity.1
            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
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
                placeProviderObject.size = -1L;
                placeProviderObject.radius = ChatEditActivity.this.avatarImage.getImageReceiver().getRoundRadius();
                placeProviderObject.scale = ChatEditActivity.this.avatarContainer.getScaleX();
                placeProviderObject.canEdit = true;
                return placeProviderObject;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public void willHidePhotoViewer() {
                ChatEditActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public void openPhotoForEdit(String str, String str2, boolean z) {
                ChatEditActivity.this.imageUpdater.openPhotoForEdit(str, str2, 0, z);
            }
        };
        this.avatarDrawable = new AvatarDrawable();
        this.imageUpdater = new ImageUpdater(true);
        this.chatId = bundle.getLong("chat_id", 0L);
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x0095, code lost:
        if (java.lang.Math.max(r3 == null ? 0 : r3.participants_count, r0.participants_count) >= getMessagesController().forumUpgradeParticipantsMin) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x00a1, code lost:
        if (r0.linked_chat_id != 0) goto L23;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x00a4, code lost:
        r1 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x004c, code lost:
        if (r0 == null) goto L9;
     */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
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
            if (r0 != 0) goto L4f
            int r0 = r10.currentAccount
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)
            long r3 = r10.chatId
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChatSync(r3)
            r10.currentChat = r0
            if (r0 == 0) goto L4e
            org.telegram.messenger.MessagesController r0 = r10.getMessagesController()
            org.telegram.tgnet.TLRPC$Chat r3 = r10.currentChat
            r0.putChat(r3, r1)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r10.info
            if (r0 != 0) goto L4f
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
            if (r0 != 0) goto L4f
        L4e:
            return r2
        L4f:
            org.telegram.ui.Components.AvatarDrawable r0 = r10.avatarDrawable
            r3 = 5
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            java.lang.String r5 = r5.title
            r6 = 0
            r0.setInfo(r3, r5, r6)
            org.telegram.tgnet.TLRPC$Chat r0 = r10.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L6b
            org.telegram.tgnet.TLRPC$Chat r0 = r10.currentChat
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L6b
            r0 = 1
            goto L6c
        L6b:
            r0 = 0
        L6c:
            r10.isChannel = r0
            org.telegram.ui.Components.ImageUpdater r0 = r10.imageUpdater
            r0.parentFragment = r10
            r0.setDelegate(r10)
            org.telegram.tgnet.TLRPC$Chat r0 = r10.currentChat
            boolean r3 = r0.signatures
            r10.signMessages = r3
            boolean r3 = r0.forum
            r10.forum = r3
            if (r3 != 0) goto L97
            org.telegram.tgnet.TLRPC$ChatFull r3 = r10.info
            if (r3 != 0) goto L87
            r3 = 0
            goto L89
        L87:
            int r3 = r3.participants_count
        L89:
            int r0 = r0.participants_count
            int r0 = java.lang.Math.max(r3, r0)
            org.telegram.messenger.MessagesController r3 = r10.getMessagesController()
            int r3 = r3.forumUpgradeParticipantsMin
            if (r0 < r3) goto La4
        L97:
            org.telegram.tgnet.TLRPC$ChatFull r0 = r10.info
            if (r0 == 0) goto La5
            long r3 = r0.linked_chat_id
            r5 = 0
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 != 0) goto La4
            goto La5
        La4:
            r1 = 0
        La5:
            r10.canForum = r1
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
            if (r0 == 0) goto Lcf
            r10.loadLinksCount()
        Lcf:
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
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getExportedChatInvites, new RequestDelegate() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda31
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChatEditActivity.this.lambda$loadLinksCount$1(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadLinksCount$1(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda28
            @Override // java.lang.Runnable
            public final void run() {
                ChatEditActivity.this.lambda$loadLinksCount$0(tLRPC$TL_error, tLObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadLinksCount$0(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            this.info.invitesCount = ((TLRPC$TL_messages_exportedChatInvites) tLObject).count;
            getMessagesStorage().saveChatLinksCount(this.chatId, this.info.invitesCount);
            updateFields(false, false);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.clear();
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatAvailableReactionsUpdated);
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
            this.nameTextView.getEditText().requestFocus();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        updateFields(true, true);
        this.imageUpdater.onResume();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onPause();
        }
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
        this.imageUpdater.onPause();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void dismissCurrentDialog() {
        if (this.imageUpdater.dismissCurrentDialog(this.visibleDialog)) {
            return;
        }
        super.dismissCurrentDialog();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean dismissDialogOnPause(Dialog dialog) {
        return this.imageUpdater.dismissDialogOnPause(dialog) && super.dismissDialogOnPause(dialog);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        this.imageUpdater.onRequestPermissionsResultFragment(i, strArr, iArr);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null && editTextEmoji.isPopupShowing()) {
            this.nameTextView.hidePopup(true);
            return false;
        }
        return checkDiscard();
    }

    /* JADX WARN: Removed duplicated region for block: B:163:0x065e  */
    /* JADX WARN: Removed duplicated region for block: B:176:0x06d1  */
    /* JADX WARN: Removed duplicated region for block: B:186:0x070b  */
    /* JADX WARN: Removed duplicated region for block: B:201:0x078c  */
    /* JADX WARN: Removed duplicated region for block: B:204:0x079f  */
    /* JADX WARN: Removed duplicated region for block: B:207:0x07ad  */
    /* JADX WARN: Removed duplicated region for block: B:217:0x07ea  */
    /* JADX WARN: Removed duplicated region for block: B:224:0x0872  */
    /* JADX WARN: Removed duplicated region for block: B:230:0x08d0  */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.view.View createView(final android.content.Context r34) {
        /*
            Method dump skipped, instructions count: 2272
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.createView(android.content.Context):android.view.View");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(View view) {
        TLRPC$Chat chat;
        TLRPC$ChatPhoto tLRPC$ChatPhoto;
        ImageLocation imageLocation;
        if (this.imageUpdater.isUploadingImage() || (tLRPC$ChatPhoto = (chat = getMessagesController().getChat(Long.valueOf(this.chatId))).photo) == null || tLRPC$ChatPhoto.photo_big == null) {
            return;
        }
        PhotoViewer.getInstance().setParentActivity(this);
        TLRPC$ChatPhoto tLRPC$ChatPhoto2 = chat.photo;
        int i = tLRPC$ChatPhoto2.dc_id;
        if (i != 0) {
            tLRPC$ChatPhoto2.photo_big.dc_id = i;
        }
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        if (tLRPC$ChatFull != null) {
            TLRPC$Photo tLRPC$Photo = tLRPC$ChatFull.chat_photo;
            if ((tLRPC$Photo instanceof TLRPC$TL_photo) && !tLRPC$Photo.video_sizes.isEmpty()) {
                imageLocation = ImageLocation.getForPhoto(this.info.chat_photo.video_sizes.get(0), this.info.chat_photo);
                PhotoViewer.getInstance().openPhotoWithVideo(chat.photo.photo_big, imageLocation, this.provider);
            }
        }
        imageLocation = null;
        PhotoViewer.getInstance().openPhotoWithVideo(chat.photo.photo_big, imageLocation, this.provider);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new Runnable() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda26
            @Override // java.lang.Runnable
            public final void run() {
                ChatEditActivity.this.lambda$createView$4();
            }
        }, new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda4
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                ChatEditActivity.this.lambda$createView$5(dialogInterface);
            }
        });
        this.cameraDrawable.setCurrentFrame(0);
        this.cameraDrawable.setCustomEndFrame(43);
        this.setAvatarCell.imageView.playAnimation();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4() {
        this.avatar = null;
        MessagesController.getInstance(this.currentAccount).changeChatAvatar(this.chatId, null, null, null, 0.0d, null, null, null, null);
        showAvatarProgress(false, true);
        this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, this.currentChat);
        this.cameraDrawable.setCurrentFrame(0);
        this.setAvatarCell.imageView.playAnimation();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(DialogInterface dialogInterface) {
        if (!this.imageUpdater.isUploadingImage()) {
            this.cameraDrawable.setCustomEndFrame(86);
            this.setAvatarCell.imageView.playAnimation();
            return;
        }
        this.cameraDrawable.setCurrentFrame(0, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$7(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$9(View view) {
        if (!AndroidUtilities.isMapsInstalled(this)) {
            return;
        }
        LocationActivity locationActivity = new LocationActivity(4);
        locationActivity.setDialogId(-this.chatId);
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        if (tLRPC$ChatFull != null) {
            TLRPC$ChannelLocation tLRPC$ChannelLocation = tLRPC$ChatFull.location;
            if (tLRPC$ChannelLocation instanceof TLRPC$TL_channelLocation) {
                locationActivity.setInitialLocation((TLRPC$TL_channelLocation) tLRPC$ChannelLocation);
            }
        }
        locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda33
            @Override // org.telegram.ui.LocationActivity.LocationActivityDelegate
            public final void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
                ChatEditActivity.this.lambda$createView$8(tLRPC$MessageMedia, i, z, i2);
            }
        });
        presentFragment(locationActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$8(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
        TLRPC$TL_channelLocation tLRPC$TL_channelLocation = new TLRPC$TL_channelLocation();
        tLRPC$TL_channelLocation.address = tLRPC$MessageMedia.address;
        tLRPC$TL_channelLocation.geo_point = tLRPC$MessageMedia.geo;
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        tLRPC$ChatFull.location = tLRPC$TL_channelLocation;
        tLRPC$ChatFull.flags |= 32768;
        updateFields(false, true);
        getMessagesController().loadFullChat(this.chatId, 0, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$10(View view) {
        long j = this.chatId;
        TextCell textCell = this.locationCell;
        ChatEditTypeActivity chatEditTypeActivity = new ChatEditTypeActivity(j, textCell != null && textCell.getVisibility() == 0);
        chatEditTypeActivity.setInfo(this.info);
        presentFragment(chatEditTypeActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$11(View view) {
        ChatLinkActivity chatLinkActivity = new ChatLinkActivity(this.chatId);
        chatLinkActivity.setInfo(this.info);
        presentFragment(chatLinkActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$13(Context context, View view) {
        final BottomSheet.Builder builder = new BottomSheet.Builder(context);
        builder.setApplyTopPadding(false);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        HeaderCell headerCell = new HeaderCell(context, "dialogTextBlue2", 23, 15, false);
        headerCell.setHeight(47);
        headerCell.setText(LocaleController.getString("ChatHistory", R.string.ChatHistory));
        linearLayout.addView(headerCell);
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(1);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
        final RadioButtonCell[] radioButtonCellArr = new RadioButtonCell[2];
        for (int i = 0; i < 2; i++) {
            radioButtonCellArr[i] = new RadioButtonCell(context, true);
            radioButtonCellArr[i].setTag(Integer.valueOf(i));
            radioButtonCellArr[i].setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (i == 0) {
                radioButtonCellArr[i].setTextAndValue(LocaleController.getString("ChatHistoryVisible", R.string.ChatHistoryVisible), LocaleController.getString("ChatHistoryVisibleInfo", R.string.ChatHistoryVisibleInfo), true, !this.historyHidden);
            } else if (ChatObject.isChannel(this.currentChat)) {
                radioButtonCellArr[i].setTextAndValue(LocaleController.getString("ChatHistoryHidden", R.string.ChatHistoryHidden), LocaleController.getString("ChatHistoryHiddenInfo", R.string.ChatHistoryHiddenInfo), false, this.historyHidden);
            } else {
                radioButtonCellArr[i].setTextAndValue(LocaleController.getString("ChatHistoryHidden", R.string.ChatHistoryHidden), LocaleController.getString("ChatHistoryHiddenInfo2", R.string.ChatHistoryHiddenInfo2), false, this.historyHidden);
            }
            linearLayout2.addView(radioButtonCellArr[i], LayoutHelper.createLinear(-1, -2));
            radioButtonCellArr[i].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda23
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChatEditActivity.this.lambda$createView$12(radioButtonCellArr, builder, view2);
                }
            });
        }
        builder.setCustomView(linearLayout);
        showDialog(builder.create());
    }

    /* JADX INFO: Access modifiers changed from: private */
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
        updateFields(true, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$14(View view) {
        boolean z = !this.signMessages;
        this.signMessages = z;
        ((TextCell) view).setChecked(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$15(FrameLayout frameLayout, View view) {
        SpannableStringBuilder replaceTags;
        if (!this.canForum) {
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            if (tLRPC$ChatFull != null && tLRPC$ChatFull.linked_chat_id != 0) {
                replaceTags = AndroidUtilities.replaceTags(LocaleController.getString("ChannelTopicsDiscussionForbidden", R.string.ChannelTopicsDiscussionForbidden));
            } else {
                replaceTags = AndroidUtilities.replaceTags(LocaleController.formatPluralString("ChannelTopicsForbidden", getMessagesController().forumUpgradeParticipantsMin, new Object[0]));
            }
            BulletinFactory.of(this).createSimpleBulletin(R.raw.topics, replaceTags).show();
            frameLayout.performHapticFeedback(3);
            return;
        }
        boolean z = !this.forum;
        this.forum = z;
        this.avatarImage.animateToRoundRadius(AndroidUtilities.dp(z ? 16.0f : 32.0f));
        ((TextCell) view).setChecked(this.forum);
        updateFields(false, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$16(View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", this.chatId);
        bundle.putInt("type", (this.isChannel || this.currentChat.gigagroup) ? 0 : 3);
        ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle);
        chatUsersActivity.setInfo(this.info);
        presentFragment(chatUsersActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$17(View view) {
        ManageLinksActivity manageLinksActivity = new ManageLinksActivity(this.chatId, 0L, 0);
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        manageLinksActivity.setInfo(tLRPC$ChatFull, tLRPC$ChatFull.exported_invite);
        presentFragment(manageLinksActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$18(View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", this.chatId);
        ChatReactionsEditActivity chatReactionsEditActivity = new ChatReactionsEditActivity(bundle);
        chatReactionsEditActivity.setInfo(this.info);
        presentFragment(chatReactionsEditActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$19(View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", this.chatId);
        bundle.putInt("type", 1);
        ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle);
        chatUsersActivity.setInfo(this.info);
        presentFragment(chatUsersActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$20(View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", this.chatId);
        bundle.putInt("type", 2);
        ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle);
        chatUsersActivity.setInfo(this.info);
        presentFragment(chatUsersActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$21(View view) {
        presentFragment(new MemberRequestsActivity(this.chatId));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$22(View view) {
        presentFragment(new ChannelAdminLogActivity(this.currentChat));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$23(View view) {
        presentFragment(new ChannelAdminLogActivity(this.currentChat));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$24(View view) {
        GroupStickersActivity groupStickersActivity = new GroupStickersActivity(this.currentChat.id);
        groupStickersActivity.setInfo(this.info);
        presentFragment(groupStickersActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$26(View view) {
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, true, false, this.currentChat, null, false, true, false, new MessagesStorage.BooleanCallback() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda29
            @Override // org.telegram.messenger.MessagesStorage.BooleanCallback
            public final void run(boolean z) {
                ChatEditActivity.this.lambda$createView$25(z);
            }
        }, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$25(boolean z) {
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
        if (this.avatarImage == null || (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) == null) {
            return;
        }
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
                this.setAvatarCell.setTextAndIcon(LocaleController.getString("ChatSetNewPhoto", R.string.ChatSetNewPhoto), R.drawable.msg_addphoto, true);
            } else {
                this.setAvatarCell.setTextAndIcon(LocaleController.getString("ChatSetPhotoOrVideo", R.string.ChatSetPhotoOrVideo), R.drawable.msg_addphoto, true);
            }
            if (this.cameraDrawable == null) {
                int i = R.raw.camera_outline;
                this.cameraDrawable = new RLottieDrawable(i, "" + i, AndroidUtilities.dp(50.0f), AndroidUtilities.dp(50.0f), false, null);
            }
            this.setAvatarCell.imageView.setTranslationY(-AndroidUtilities.dp(9.0f));
            this.setAvatarCell.imageView.setTranslationX(-AndroidUtilities.dp(8.0f));
            this.setAvatarCell.imageView.setAnimation(this.cameraDrawable);
        }
        if (!PhotoViewer.hasInstance() || !PhotoViewer.getInstance().isVisible()) {
            return;
        }
        PhotoViewer.getInstance().checkCurrentImageVisibility();
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x001b, code lost:
        if (java.lang.Math.max(r0 == null ? 0 : r0.participants_count, r6.currentChat.participants_count) >= getMessagesController().forumUpgradeParticipantsMin) goto L19;
     */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0033  */
    /* JADX WARN: Removed duplicated region for block: B:25:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateCanForum() {
        /*
            r6 = this;
            boolean r0 = r6.forum
            r1 = 0
            if (r0 != 0) goto L1d
            org.telegram.tgnet.TLRPC$ChatFull r0 = r6.info
            if (r0 != 0) goto Lb
            r0 = 0
            goto Ld
        Lb:
            int r0 = r0.participants_count
        Ld:
            org.telegram.tgnet.TLRPC$Chat r2 = r6.currentChat
            int r2 = r2.participants_count
            int r0 = java.lang.Math.max(r0, r2)
            org.telegram.messenger.MessagesController r2 = r6.getMessagesController()
            int r2 = r2.forumUpgradeParticipantsMin
            if (r0 < r2) goto L2a
        L1d:
            org.telegram.tgnet.TLRPC$ChatFull r0 = r6.info
            if (r0 == 0) goto L2c
            long r2 = r0.linked_chat_id
            r4 = 0
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 != 0) goto L2a
            goto L2c
        L2a:
            r0 = 0
            goto L2d
        L2c:
            r0 = 1
        L2d:
            r6.canForum = r0
            org.telegram.ui.Cells.TextCell r0 = r6.forumsCell
            if (r0 == 0) goto L41
            org.telegram.ui.Components.Switch r0 = r0.getCheckBox()
            boolean r2 = r6.canForum
            if (r2 == 0) goto L3c
            goto L3e
        L3c:
            int r1 = org.telegram.messenger.R.drawable.permission_locked
        L3e:
            r0.setIcon(r1)
        L41:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.updateCanForum():void");
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        EditTextBoldCursor editTextBoldCursor;
        boolean z = true;
        if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC$ChatFull tLRPC$ChatFull = (TLRPC$ChatFull) objArr[0];
            if (tLRPC$ChatFull.id != this.chatId) {
                return;
            }
            if (this.info == null && (editTextBoldCursor = this.descriptionTextView) != null) {
                editTextBoldCursor.setText(tLRPC$ChatFull.about);
            }
            boolean z2 = this.info == null;
            this.info = tLRPC$ChatFull;
            updateCanForum();
            if (ChatObject.isChannel(this.currentChat) && !this.info.hidden_prehistory) {
                z = false;
            }
            this.historyHidden = z;
            updateFields(false, false);
            if (!z2) {
                return;
            }
            loadLinksCount();
        } else if (i == NotificationCenter.updateInterfaces) {
            if ((((Integer) objArr[0]).intValue() & MessagesController.UPDATE_MASK_AVATAR) == 0) {
                return;
            }
            setAvatar();
        } else if (i != NotificationCenter.chatAvailableReactionsUpdated) {
        } else {
            long longValue = ((Long) objArr[0]).longValue();
            if (longValue != this.chatId) {
                return;
            }
            TLRPC$ChatFull chatFull = getMessagesController().getChatFull(longValue);
            this.info = chatFull;
            if (chatFull != null) {
                this.availableReactions = chatFull.available_reactions;
            }
            updateReactionsCell(true);
        }
    }

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public void onUploadProgressChanged(float f) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView == null) {
            return;
        }
        radialProgressView.setProgress(f);
    }

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public void didStartUpload(boolean z) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView == null) {
            return;
        }
        radialProgressView.setProgress(0.0f);
    }

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public void didUploadPhoto(final TLRPC$InputFile tLRPC$InputFile, final TLRPC$InputFile tLRPC$InputFile2, final double d, final String str, final TLRPC$PhotoSize tLRPC$PhotoSize, final TLRPC$PhotoSize tLRPC$PhotoSize2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                ChatEditActivity.this.lambda$didUploadPhoto$27(tLRPC$PhotoSize2, tLRPC$InputFile, tLRPC$InputFile2, d, str, tLRPC$PhotoSize);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$didUploadPhoto$27(TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, String str, TLRPC$PhotoSize tLRPC$PhotoSize2) {
        TLRPC$FileLocation tLRPC$FileLocation = tLRPC$PhotoSize.location;
        this.avatar = tLRPC$FileLocation;
        if (tLRPC$InputFile != null || tLRPC$InputFile2 != null) {
            getMessagesController().changeChatAvatar(this.chatId, null, tLRPC$InputFile, tLRPC$InputFile2, d, str, tLRPC$PhotoSize.location, tLRPC$PhotoSize2.location, null);
            if (this.createAfterUpload) {
                try {
                    AlertDialog alertDialog = this.progressDialog;
                    if (alertDialog != null && alertDialog.isShowing()) {
                        this.progressDialog.dismiss();
                        this.progressDialog = null;
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                this.donePressed = false;
                this.doneButton.performClick();
            }
            showAvatarProgress(false, true);
            return;
        }
        this.avatarImage.setImage(ImageLocation.getForLocal(tLRPC$FileLocation), "50_50", this.avatarDrawable, this.currentChat);
        this.setAvatarCell.setTextAndIcon(LocaleController.getString("ChatSetNewPhoto", R.string.ChatSetNewPhoto), R.drawable.msg_addphoto, true);
        if (this.cameraDrawable == null) {
            int i = R.raw.camera_outline;
            this.cameraDrawable = new RLottieDrawable(i, "" + i, AndroidUtilities.dp(50.0f), AndroidUtilities.dp(50.0f), false, null);
        }
        this.setAvatarCell.imageView.setTranslationY(-AndroidUtilities.dp(9.0f));
        this.setAvatarCell.imageView.setTranslationX(-AndroidUtilities.dp(8.0f));
        this.setAvatarCell.imageView.setAnimation(this.cameraDrawable);
        showAvatarProgress(true, false);
    }

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public String getInitialSearchString() {
        return this.nameTextView.getText().toString();
    }

    public void showConvertTooltip() {
        this.undoView.showWithAction(0L, 76, (Runnable) null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkDiscard() {
        String str;
        EditTextEmoji editTextEmoji;
        EditTextBoldCursor editTextBoldCursor;
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        if (tLRPC$ChatFull == null || (str = tLRPC$ChatFull.about) == null) {
            str = "";
        }
        if ((tLRPC$ChatFull == null || !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory == this.historyHidden) && (((editTextEmoji = this.nameTextView) == null || this.currentChat.title.equals(editTextEmoji.getText().toString())) && ((editTextBoldCursor = this.descriptionTextView) == null || str.equals(editTextBoldCursor.getText().toString())))) {
            boolean z = this.signMessages;
            TLRPC$Chat tLRPC$Chat = this.currentChat;
            if (z == tLRPC$Chat.signatures && this.forum == tLRPC$Chat.forum) {
                return true;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", R.string.UserRestrictionsApplyChanges));
        if (this.isChannel) {
            builder.setMessage(LocaleController.getString("ChannelSettingsChangedAlert", R.string.ChannelSettingsChangedAlert));
        } else {
            builder.setMessage(LocaleController.getString("GroupSettingsChangedAlert", R.string.GroupSettingsChangedAlert));
        }
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", R.string.ApplyTheme), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatEditActivity.this.lambda$checkDiscard$28(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda3
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatEditActivity.this.lambda$checkDiscard$29(dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$28(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$29(DialogInterface dialogInterface, int i) {
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

    /* JADX INFO: Access modifiers changed from: private */
    public void processDone() {
        EditTextEmoji editTextEmoji;
        String str;
        if (this.donePressed || (editTextEmoji = this.nameTextView) == null) {
            return;
        }
        if (editTextEmoji.length() == 0) {
            Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200L);
            }
            AndroidUtilities.shakeView(this.nameTextView);
            return;
        }
        this.donePressed = true;
        if (!ChatObject.isChannel(this.currentChat) && (!this.historyHidden || this.forum)) {
            getMessagesController().convertToMegaGroup(getParentActivity(), this.chatId, this, new MessagesStorage.LongCallback() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda30
                @Override // org.telegram.messenger.MessagesStorage.LongCallback
                public final void run(long j) {
                    ChatEditActivity.this.lambda$processDone$30(j);
                }
            });
            return;
        }
        if (this.info != null && ChatObject.isChannel(this.currentChat)) {
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            boolean z = tLRPC$ChatFull.hidden_prehistory;
            boolean z2 = this.historyHidden;
            if (z != z2) {
                tLRPC$ChatFull.hidden_prehistory = z2;
                getMessagesController().toggleChannelInvitesHistory(this.chatId, this.historyHidden);
            }
        }
        if (this.imageUpdater.isUploadingImage()) {
            this.createAfterUpload = true;
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog = alertDialog;
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialogInterface) {
                    ChatEditActivity.this.lambda$processDone$31(dialogInterface);
                }
            });
            this.progressDialog.show();
            return;
        }
        if (!this.currentChat.title.equals(this.nameTextView.getText().toString())) {
            getMessagesController().changeChatTitle(this.chatId, this.nameTextView.getText().toString());
        }
        TLRPC$ChatFull tLRPC$ChatFull2 = this.info;
        if (tLRPC$ChatFull2 == null || (str = tLRPC$ChatFull2.about) == null) {
            str = "";
        }
        EditTextBoldCursor editTextBoldCursor = this.descriptionTextView;
        if (editTextBoldCursor != null && !str.equals(editTextBoldCursor.getText().toString())) {
            getMessagesController().updateChatAbout(this.chatId, this.descriptionTextView.getText().toString(), this.info);
        }
        boolean z3 = this.signMessages;
        TLRPC$Chat tLRPC$Chat = this.currentChat;
        if (z3 != tLRPC$Chat.signatures) {
            tLRPC$Chat.signatures = true;
            getMessagesController().toggleChannelSignatures(this.chatId, this.signMessages);
        }
        if (this.forum != this.currentChat.forum) {
            getMessagesController().toggleChannelForum(this.chatId, this.forum);
            List<BaseFragment> fragmentStack = getParentLayout().getFragmentStack();
            for (int i = 0; i < fragmentStack.size(); i++) {
                if ((fragmentStack.get(i) instanceof ChatActivity) && ((ChatActivity) fragmentStack.get(i)).getArguments().getLong("chat_id") == this.chatId) {
                    getParentLayout().removeFragmentFromStack(i);
                    Bundle bundle = new Bundle();
                    bundle.putLong("chat_id", this.chatId);
                    getParentLayout().addFragmentToStack(new TopicsFragment(bundle), i);
                }
            }
        }
        finishFragment();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$30(long j) {
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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$31(DialogInterface dialogInterface) {
        this.createAfterUpload = false;
        this.progressDialog = null;
        this.donePressed = false;
    }

    private void showAvatarProgress(final boolean z, boolean z2) {
        if (this.avatarProgressView == null) {
            return;
        }
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
                this.avatarAnimation.playTogether(ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.avatarOverlay, View.ALPHA, 1.0f));
            } else {
                animatorSet2.playTogether(ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.avatarOverlay, View.ALPHA, 0.0f));
            }
            this.avatarAnimation.setDuration(180L);
            this.avatarAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatEditActivity.9
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (ChatEditActivity.this.avatarAnimation == null || ChatEditActivity.this.avatarProgressView == null) {
                        return;
                    }
                    if (!z) {
                        ChatEditActivity.this.avatarProgressView.setVisibility(4);
                        ChatEditActivity.this.avatarOverlay.setVisibility(4);
                    }
                    ChatEditActivity.this.avatarAnimation = null;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    ChatEditActivity.this.avatarAnimation = null;
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int i, int i2, Intent intent) {
        this.imageUpdater.onActivityResult(i, i2, intent);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void saveSelfArgs(Bundle bundle) {
        String str;
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null && (str = imageUpdater.currentPicturePath) != null) {
            bundle.putString("path", str);
        }
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            String obj = editTextEmoji.getText().toString();
            if (obj.length() == 0) {
                return;
            }
            bundle.putString("nameTextView", obj);
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

    private void updateFields(boolean z, boolean z2) {
        int i;
        int i2;
        String str;
        TLRPC$ChatFull tLRPC$ChatFull;
        int i3;
        String str2;
        String string;
        TextCell textCell;
        TextCell textCell2;
        TextCell textCell3;
        TextCell textCell4;
        int i4;
        String str3;
        String format;
        TextCell textCell5;
        TextCell textCell6;
        TLRPC$ChatFull tLRPC$ChatFull2;
        TextCell textCell7;
        TextCell textCell8;
        TextCell textCell9;
        TLRPC$Chat chat;
        if (z && (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) != null) {
            this.currentChat = chat;
        }
        boolean z3 = !ChatObject.isPublic(this.currentChat);
        TextInfoPrivacyCell textInfoPrivacyCell = this.settingsSectionCell;
        if (textInfoPrivacyCell != null) {
            textInfoPrivacyCell.setVisibility((this.signCell == null && this.typeCell == null && ((textCell7 = this.linkedCell) == null || textCell7.getVisibility() != 0) && (((textCell8 = this.historyCell) == null || textCell8.getVisibility() != 0) && ((textCell9 = this.locationCell) == null || textCell9.getVisibility() != 0))) ? 8 : 0);
        }
        TextCell textCell10 = this.logCell;
        if (textCell10 != null) {
            TLRPC$Chat tLRPC$Chat = this.currentChat;
            textCell10.setVisibility((!tLRPC$Chat.megagroup || tLRPC$Chat.gigagroup || ((tLRPC$ChatFull2 = this.info) != null && tLRPC$ChatFull2.participants_count > 200)) ? 0 : 8);
        }
        TextCell textCell11 = this.linkedCell;
        if (textCell11 != null) {
            TLRPC$ChatFull tLRPC$ChatFull3 = this.info;
            if (tLRPC$ChatFull3 == null || (!this.isChannel && tLRPC$ChatFull3.linked_chat_id == 0)) {
                textCell11.setVisibility(8);
            } else {
                textCell11.setVisibility(0);
                if (this.info.linked_chat_id == 0) {
                    this.linkedCell.setTextAndValueAndIcon(LocaleController.getString("Discussion", R.string.Discussion), LocaleController.getString("DiscussionInfoShort", R.string.DiscussionInfoShort), R.drawable.msg_discuss, true);
                } else {
                    TLRPC$Chat chat2 = getMessagesController().getChat(Long.valueOf(this.info.linked_chat_id));
                    if (chat2 == null) {
                        this.linkedCell.setVisibility(8);
                    } else if (this.isChannel) {
                        String publicUsername = ChatObject.getPublicUsername(chat2);
                        if (TextUtils.isEmpty(publicUsername)) {
                            this.linkedCell.setTextAndValueAndIcon(LocaleController.getString("Discussion", R.string.Discussion), chat2.title, R.drawable.msg_discuss, true);
                        } else {
                            this.linkedCell.setTextAndValueAndIcon(LocaleController.getString("Discussion", R.string.Discussion), "@" + publicUsername, R.drawable.msg_discuss, true);
                        }
                    } else {
                        String publicUsername2 = ChatObject.getPublicUsername(chat2);
                        if (TextUtils.isEmpty(publicUsername2)) {
                            TextCell textCell12 = this.linkedCell;
                            String string2 = LocaleController.getString("LinkedChannel", R.string.LinkedChannel);
                            String str4 = chat2.title;
                            int i5 = R.drawable.msg_channel;
                            TextCell textCell13 = this.forumsCell;
                            textCell12.setTextAndValueAndIcon(string2, str4, i5, textCell13 != null && textCell13.getVisibility() == 0);
                        } else {
                            TextCell textCell14 = this.linkedCell;
                            String string3 = LocaleController.getString("LinkedChannel", R.string.LinkedChannel);
                            String str5 = "@" + publicUsername2;
                            int i6 = R.drawable.msg_channel;
                            TextCell textCell15 = this.forumsCell;
                            textCell14.setTextAndValueAndIcon(string3, str5, i6, textCell15 != null && textCell15.getVisibility() == 0);
                        }
                    }
                }
            }
        }
        TextCell textCell16 = this.locationCell;
        if (textCell16 != null) {
            TLRPC$ChatFull tLRPC$ChatFull4 = this.info;
            if (tLRPC$ChatFull4 != null && tLRPC$ChatFull4.can_set_location) {
                textCell16.setVisibility(0);
                TLRPC$ChannelLocation tLRPC$ChannelLocation = this.info.location;
                if (tLRPC$ChannelLocation instanceof TLRPC$TL_channelLocation) {
                    this.locationCell.setTextAndValue(LocaleController.getString("AttachLocation", R.string.AttachLocation), ((TLRPC$TL_channelLocation) tLRPC$ChannelLocation).address, z2, true);
                } else {
                    this.locationCell.setTextAndValue(LocaleController.getString("AttachLocation", R.string.AttachLocation), "Unknown address", z2, true);
                }
            } else {
                textCell16.setVisibility(8);
            }
        }
        if (this.typeCell != null) {
            TLRPC$ChatFull tLRPC$ChatFull5 = this.info;
            if (tLRPC$ChatFull5 != null && (tLRPC$ChatFull5.location instanceof TLRPC$TL_channelLocation)) {
                if (z3) {
                    format = LocaleController.getString("TypeLocationGroupEdit", R.string.TypeLocationGroupEdit);
                } else {
                    format = String.format("https://" + getMessagesController().linkPrefix + "/%s", ChatObject.getPublicUsername(this.currentChat));
                }
                TextCell textCell17 = this.typeCell;
                String string4 = LocaleController.getString("TypeLocationGroup", R.string.TypeLocationGroup);
                int i7 = R.drawable.msg_channel;
                TextCell textCell18 = this.historyCell;
                textCell17.setTextAndValueAndIcon(string4, format, i7, (textCell18 != null && textCell18.getVisibility() == 0) || ((textCell5 = this.linkedCell) != null && textCell5.getVisibility() == 0) || ((textCell6 = this.forumsCell) != null && textCell6.getVisibility() == 0));
            } else {
                boolean z4 = this.currentChat.noforwards;
                if (this.isChannel) {
                    if (!z3) {
                        i4 = R.string.TypePublic;
                        str3 = "TypePublic";
                    } else if (z4) {
                        i4 = R.string.TypePrivateRestrictedForwards;
                        str3 = "TypePrivateRestrictedForwards";
                    } else {
                        i4 = R.string.TypePrivate;
                        str3 = "TypePrivate";
                    }
                    string = LocaleController.getString(str3, i4);
                } else {
                    if (!z3) {
                        i3 = R.string.TypePublicGroup;
                        str2 = "TypePublicGroup";
                    } else if (z4) {
                        i3 = R.string.TypePrivateGroupRestrictedForwards;
                        str2 = "TypePrivateGroupRestrictedForwards";
                    } else {
                        i3 = R.string.TypePrivateGroup;
                        str2 = "TypePrivateGroup";
                    }
                    string = LocaleController.getString(str2, i3);
                }
                if (this.isChannel) {
                    TextCell textCell19 = this.typeCell;
                    String string5 = LocaleController.getString("ChannelType", R.string.ChannelType);
                    int i8 = R.drawable.msg_channel;
                    TextCell textCell20 = this.historyCell;
                    textCell19.setTextAndValueAndIcon(string5, string, i8, (textCell20 != null && textCell20.getVisibility() == 0) || ((textCell3 = this.linkedCell) != null && textCell3.getVisibility() == 0) || ((textCell4 = this.forumsCell) != null && textCell4.getVisibility() == 0));
                } else {
                    TextCell textCell21 = this.typeCell;
                    String string6 = LocaleController.getString("GroupType", R.string.GroupType);
                    int i9 = R.drawable.msg_groups;
                    TextCell textCell22 = this.historyCell;
                    textCell21.setTextAndValueAndIcon(string6, string, i9, (textCell22 != null && textCell22.getVisibility() == 0) || ((textCell = this.linkedCell) != null && textCell.getVisibility() == 0) || ((textCell2 = this.forumsCell) != null && textCell2.getVisibility() == 0));
                }
            }
        }
        if (this.historyCell != null) {
            if (!this.historyHidden || this.forum) {
                i2 = R.string.ChatHistoryVisible;
                str = "ChatHistoryVisible";
            } else {
                i2 = R.string.ChatHistoryHidden;
                str = "ChatHistoryHidden";
            }
            this.historyCell.setTextAndValueAndIcon(LocaleController.getString("ChatHistoryShort", R.string.ChatHistoryShort), LocaleController.getString(str, i2), z2, R.drawable.msg_discuss, this.forumsCell != null);
            this.historyCell.setEnabled(!this.forum);
            updateHistoryShow(!this.forum && z3 && ((tLRPC$ChatFull = this.info) == null || tLRPC$ChatFull.linked_chat_id == 0) && (tLRPC$ChatFull == null || !(tLRPC$ChatFull.location instanceof TLRPC$TL_channelLocation)), z2);
        }
        TextCell textCell23 = this.membersCell;
        if (textCell23 != null) {
            if (this.info != null) {
                TextCell textCell24 = this.memberRequestsCell;
                if (textCell24 != null) {
                    if (textCell24.getParent() == null) {
                        this.infoContainer.addView(this.memberRequestsCell, this.infoContainer.indexOfChild(this.membersCell) + 1, LayoutHelper.createLinear(-1, -2));
                    }
                    this.memberRequestsCell.setVisibility(this.info.requests_pending > 0 ? 0 : 8);
                }
                if (this.isChannel) {
                    this.membersCell.setTextAndValueAndIcon(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers), String.format("%d", Integer.valueOf(this.info.participants_count)), R.drawable.msg_groups, true);
                    TextCell textCell25 = this.blockCell;
                    String string7 = LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist);
                    TLRPC$ChatFull tLRPC$ChatFull6 = this.info;
                    String format2 = String.format("%d", Integer.valueOf(Math.max(tLRPC$ChatFull6.banned_count, tLRPC$ChatFull6.kicked_count)));
                    int i10 = R.drawable.msg_user_remove;
                    TextCell textCell26 = this.logCell;
                    textCell25.setTextAndValueAndIcon(string7, format2, i10, textCell26 != null && textCell26.getVisibility() == 0);
                } else {
                    if (ChatObject.isChannel(this.currentChat)) {
                        this.membersCell.setTextAndValueAndIcon(LocaleController.getString("ChannelMembers", R.string.ChannelMembers), String.format("%d", Integer.valueOf(this.info.participants_count)), R.drawable.msg_groups, true);
                    } else {
                        this.membersCell.setTextAndValueAndIcon(LocaleController.getString("ChannelMembers", R.string.ChannelMembers), String.format("%d", Integer.valueOf(this.info.participants.participants.size())), R.drawable.msg_groups, this.memberRequestsCell.getVisibility() == 0);
                    }
                    TLRPC$Chat tLRPC$Chat2 = this.currentChat;
                    if (tLRPC$Chat2.gigagroup) {
                        TextCell textCell27 = this.blockCell;
                        String string8 = LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist);
                        TLRPC$ChatFull tLRPC$ChatFull7 = this.info;
                        String format3 = String.format("%d", Integer.valueOf(Math.max(tLRPC$ChatFull7.banned_count, tLRPC$ChatFull7.kicked_count)));
                        int i11 = R.drawable.msg_user_remove;
                        TextCell textCell28 = this.logCell;
                        textCell27.setTextAndValueAndIcon(string8, format3, i11, textCell28 != null && textCell28.getVisibility() == 0);
                    } else {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights = tLRPC$Chat2.default_banned_rights;
                        int i12 = 9;
                        if (tLRPC$TL_chatBannedRights != null) {
                            i = !tLRPC$TL_chatBannedRights.send_stickers ? 1 : 0;
                            if (!tLRPC$TL_chatBannedRights.send_media) {
                                i++;
                            }
                            if (!tLRPC$TL_chatBannedRights.embed_links) {
                                i++;
                            }
                            if (!tLRPC$TL_chatBannedRights.send_messages) {
                                i++;
                            }
                            if (!tLRPC$TL_chatBannedRights.pin_messages) {
                                i++;
                            }
                            if (!tLRPC$TL_chatBannedRights.send_polls) {
                                i++;
                            }
                            if (!tLRPC$TL_chatBannedRights.invite_users) {
                                i++;
                            }
                            if (this.forum && !tLRPC$TL_chatBannedRights.manage_topics) {
                                i++;
                            }
                            if (!tLRPC$TL_chatBannedRights.change_info) {
                                i++;
                            }
                        } else {
                            i = this.forum ? 9 : 8;
                        }
                        TextCell textCell29 = this.blockCell;
                        String string9 = LocaleController.getString("ChannelPermissions", R.string.ChannelPermissions);
                        Object[] objArr = new Object[2];
                        objArr[0] = Integer.valueOf(i);
                        if (!this.forum) {
                            i12 = 8;
                        }
                        objArr[1] = Integer.valueOf(i12);
                        textCell29.setTextAndValueAndIcon(string9, String.format("%d/%d", objArr), z2, R.drawable.msg_permissions, true);
                    }
                    TextCell textCell30 = this.memberRequestsCell;
                    if (textCell30 != null) {
                        String string10 = LocaleController.getString("MemberRequests", R.string.MemberRequests);
                        String format4 = String.format("%d", Integer.valueOf(this.info.requests_pending));
                        int i13 = R.drawable.msg_requests;
                        TextCell textCell31 = this.logCell;
                        textCell30.setTextAndValueAndIcon(string10, format4, i13, textCell31 != null && textCell31.getVisibility() == 0);
                    }
                }
                TextCell textCell32 = this.adminCell;
                String string11 = LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators);
                Object[] objArr2 = new Object[1];
                objArr2[0] = Integer.valueOf(ChatObject.isChannel(this.currentChat) ? this.info.admins_count : getAdminCount());
                textCell32.setTextAndValueAndIcon(string11, String.format("%d", objArr2), R.drawable.msg_admins, true);
            } else {
                if (this.isChannel) {
                    textCell23.setTextAndIcon(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers), R.drawable.msg_groups, true);
                    TextCell textCell33 = this.blockCell;
                    String string12 = LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist);
                    int i14 = R.drawable.msg_chats_remove;
                    TextCell textCell34 = this.logCell;
                    textCell33.setTextAndIcon(string12, i14, textCell34 != null && textCell34.getVisibility() == 0);
                } else {
                    String string13 = LocaleController.getString("ChannelMembers", R.string.ChannelMembers);
                    int i15 = R.drawable.msg_groups;
                    TextCell textCell35 = this.logCell;
                    textCell23.setTextAndIcon(string13, i15, textCell35 != null && textCell35.getVisibility() == 0);
                    if (this.currentChat.gigagroup) {
                        TextCell textCell36 = this.blockCell;
                        String string14 = LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist);
                        int i16 = R.drawable.msg_chats_remove;
                        TextCell textCell37 = this.logCell;
                        textCell36.setTextAndIcon(string14, i16, textCell37 != null && textCell37.getVisibility() == 0);
                    } else {
                        this.blockCell.setTextAndIcon(LocaleController.getString("ChannelPermissions", R.string.ChannelPermissions), R.drawable.msg_permissions, true);
                    }
                }
                this.adminCell.setTextAndIcon(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators), R.drawable.msg_admins, true);
            }
            this.reactionsCell.setVisibility(ChatObject.canChangeChatInfo(this.currentChat) ? 0 : 8);
            updateReactionsCell(z2);
            if (this.info == null || !ChatObject.canUserDoAdminAction(this.currentChat, 3) || (!z3 && this.currentChat.creator)) {
                this.inviteLinksCell.setVisibility(8);
            } else if (this.info.invitesCount > 0) {
                this.inviteLinksCell.setTextAndValueAndIcon(LocaleController.getString("InviteLinks", R.string.InviteLinks), Integer.toString(this.info.invitesCount), R.drawable.msg_link2, true);
            } else {
                this.inviteLinksCell.setTextAndValueAndIcon(LocaleController.getString("InviteLinks", R.string.InviteLinks), "1", R.drawable.msg_link2, true);
            }
        }
        TextCell textCell38 = this.stickersCell;
        if (textCell38 == null || this.info == null) {
            return;
        }
        String string15 = LocaleController.getString(R.string.GroupStickers);
        TLRPC$StickerSet tLRPC$StickerSet = this.info.stickerset;
        textCell38.setTextAndValueAndIcon(string15, tLRPC$StickerSet != null ? tLRPC$StickerSet.title : LocaleController.getString(R.string.Add), R.drawable.msg_sticker, false);
    }

    private void updateHistoryShow(final boolean z, boolean z2) {
        ValueAnimator valueAnimator = this.updateHistoryShowAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        int i = 8;
        float f = 0.0f;
        if (this.historyCell.getAlpha() <= 0.0f && !z) {
            this.historyCell.setVisibility(8);
            return;
        }
        float f2 = 1.0f;
        if (this.historyCell.getVisibility() == 0 && this.historyCell.getAlpha() >= 1.0f && z) {
            return;
        }
        final ArrayList arrayList = new ArrayList();
        boolean z3 = false;
        for (int i2 = 0; i2 < this.typeEditContainer.getChildCount(); i2++) {
            if (!z3 && this.typeEditContainer.getChildAt(i2) == this.historyCell) {
                z3 = true;
            } else if (z3) {
                arrayList.add(this.typeEditContainer.getChildAt(i2));
            }
        }
        boolean z4 = false;
        for (int i3 = 0; i3 < this.linearLayout.getChildCount(); i3++) {
            if (!z4 && this.linearLayout.getChildAt(i3) == this.typeEditContainer) {
                z4 = true;
            } else if (z4) {
                arrayList.add(this.linearLayout.getChildAt(i3));
            }
        }
        if (this.historyCell.getVisibility() != 0) {
            this.historyCell.setAlpha(0.0f);
            TextCell textCell = this.historyCell;
            textCell.setTranslationY((-textCell.getHeight()) / 2.0f);
        }
        this.historyCell.setVisibility(0);
        for (int i4 = 0; i4 < arrayList.size(); i4++) {
            ((View) arrayList.get(i4)).setTranslationY((-this.historyCell.getHeight()) * (1.0f - this.historyCell.getAlpha()));
        }
        if (z2) {
            float[] fArr = new float[2];
            fArr[0] = this.historyCell.getAlpha();
            if (z) {
                f = 1.0f;
            }
            fArr[1] = f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.updateHistoryShowAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ChatEditActivity.this.lambda$updateHistoryShow$32(arrayList, valueAnimator2);
                }
            });
            this.updateHistoryShowAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatEditActivity.10
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    ChatEditActivity.this.historyCell.setVisibility(z ? 0 : 8);
                    for (int i5 = 0; i5 < arrayList.size(); i5++) {
                        ((View) arrayList.get(i5)).setTranslationY(0.0f);
                    }
                }
            });
            this.updateHistoryShowAnimator.setDuration(320L);
            this.updateHistoryShowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.updateHistoryShowAnimator.start();
            return;
        }
        this.historyCell.setAlpha(z ? 1.0f : 0.0f);
        TextCell textCell2 = this.historyCell;
        textCell2.setTranslationY(((-textCell2.getHeight()) / 2.0f) * (z ? 0.0f : 1.0f));
        TextCell textCell3 = this.historyCell;
        if (!z) {
            f2 = 0.0f;
        }
        textCell3.setScaleY((f2 * 0.8f) + 0.2f);
        TextCell textCell4 = this.historyCell;
        if (z) {
            i = 0;
        }
        textCell4.setVisibility(i);
        for (int i5 = 0; i5 < arrayList.size(); i5++) {
            ((View) arrayList.get(i5)).setTranslationY(0.0f);
        }
        this.updateHistoryShowAnimator = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateHistoryShow$32(ArrayList arrayList, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.historyCell.setAlpha(floatValue);
        TextCell textCell = this.historyCell;
        float f = 1.0f - floatValue;
        textCell.setTranslationY(((-textCell.getHeight()) / 2.0f) * f);
        this.historyCell.setScaleY((floatValue * 0.8f) + 0.2f);
        for (int i = 0; i < arrayList.size(); i++) {
            ((View) arrayList.get(i)).setTranslationY((-this.historyCell.getHeight()) * f);
        }
    }

    private void updateReactionsCell(boolean z) {
        String string;
        TLRPC$TL_availableReaction tLRPC$TL_availableReaction;
        TLRPC$ChatReactions tLRPC$ChatReactions = this.availableReactions;
        if (tLRPC$ChatReactions == null || (tLRPC$ChatReactions instanceof TLRPC$TL_chatReactionsNone)) {
            string = LocaleController.getString("ReactionsOff", R.string.ReactionsOff);
        } else if (tLRPC$ChatReactions instanceof TLRPC$TL_chatReactionsSome) {
            TLRPC$TL_chatReactionsSome tLRPC$TL_chatReactionsSome = (TLRPC$TL_chatReactionsSome) tLRPC$ChatReactions;
            int i = 0;
            for (int i2 = 0; i2 < tLRPC$TL_chatReactionsSome.reactions.size(); i2++) {
                TLRPC$Reaction tLRPC$Reaction = tLRPC$TL_chatReactionsSome.reactions.get(i2);
                if ((tLRPC$Reaction instanceof TLRPC$TL_reactionEmoji) && (tLRPC$TL_availableReaction = getMediaDataController().getReactionsMap().get(((TLRPC$TL_reactionEmoji) tLRPC$Reaction).emoticon)) != null && !tLRPC$TL_availableReaction.inactive) {
                    i++;
                }
            }
            int min = Math.min(getMediaDataController().getEnabledReactionsList().size(), i);
            if (min == 0) {
                string = LocaleController.getString("ReactionsOff", R.string.ReactionsOff);
            } else {
                string = LocaleController.formatString("ReactionsCount", R.string.ReactionsCount, Integer.valueOf(min), Integer.valueOf(getMediaDataController().getEnabledReactionsList().size()));
            }
        } else {
            string = LocaleController.getString("ReactionsAll", R.string.ReactionsAll);
        }
        this.reactionsCell.setTextAndValueAndIcon(LocaleController.getString("Reactions", R.string.Reactions), string, z, R.drawable.msg_reactions2, true);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda32
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ChatEditActivity.this.lambda$getThemeDescriptions$33();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.setAvatarCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.setAvatarCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        arrayList.add(new ThemeDescription(this.setAvatarCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        arrayList.add(new ThemeDescription(this.membersCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.membersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.membersCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription(this.adminCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.adminCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.adminCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription(this.inviteLinksCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.inviteLinksCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.inviteLinksCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        if (this.memberRequestsCell != null) {
            arrayList.add(new ThemeDescription(this.memberRequestsCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription(this.memberRequestsCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.memberRequestsCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        }
        arrayList.add(new ThemeDescription(this.blockCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.blockCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.blockCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription(this.logCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.logCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.logCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription(this.typeCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.historyCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.locationCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.locationCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.locationCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.avatarContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.settingsContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.typeEditContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.deleteContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.stickersContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.infoContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.settingsTopSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.settingsSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.deleteInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.signCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription(this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.deleteCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.deleteCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription(this.stickersCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.stickersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.stickersInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.stickersInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(null, 0, null, null, Theme.avatarDrawables, themeDescriptionDelegate, "avatar_text"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "undo_background"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.reactionsCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.reactionsCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.reactionsCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$33() {
        BackupImageView backupImageView = this.avatarImage;
        if (backupImageView != null) {
            backupImageView.invalidate();
        }
    }
}
