package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.RequestDelegate;
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
import org.telegram.tgnet.TLRPC$TL_channelLocation;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputChatPhoto;
import org.telegram.tgnet.TLRPC$TL_messages_exportedChatInvites;
import org.telegram.tgnet.TLRPC$TL_messages_getExportedChatInvites;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
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
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.LocationActivity;
import org.telegram.ui.PhotoViewer;

public class ChatEditActivity extends BaseFragment implements ImageUpdater.ImageUpdaterDelegate, NotificationCenter.NotificationCenterDelegate {
    private TextCell adminCell;
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
    /* access modifiers changed from: private */
    public int chatId;
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
            TLRPC$Chat chat = ChatEditActivity.this.getMessagesController().getChat(Integer.valueOf(ChatEditActivity.this.chatId));
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
    private TextCell setAvatarCell;
    private LinearLayout settingsContainer;
    private ShadowSectionCell settingsSectionCell;
    private ShadowSectionCell settingsTopSectionCell;
    private TextCheckCell signCell;
    private boolean signMessages;
    private TextSettingsCell stickersCell;
    private FrameLayout stickersContainer;
    private TextInfoPrivacyCell stickersInfoCell3;
    private TextDetailCell typeCell;
    private LinearLayout typeEditContainer;
    private UndoView undoView;

    static /* synthetic */ boolean lambda$createView$2(View view, MotionEvent motionEvent) {
        return true;
    }

    public ChatEditActivity(Bundle bundle) {
        super(bundle);
        this.chatId = bundle.getInt("chat_id", 0);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0050, code lost:
        if (r0 == null) goto L_0x0052;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onFragmentCreate() {
        /*
            r9 = this;
            int r0 = r9.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r1 = r9.chatId
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            r9.currentChat = r0
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x0053
            int r0 = r9.currentAccount
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)
            int r3 = r9.chatId
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChatSync(r3)
            r9.currentChat = r0
            if (r0 == 0) goto L_0x0052
            int r0 = r9.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.tgnet.TLRPC$Chat r3 = r9.currentChat
            r0.putChat(r3, r1)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r9.info
            if (r0 != 0) goto L_0x0053
            int r0 = r9.currentAccount
            org.telegram.messenger.MessagesStorage r3 = org.telegram.messenger.MessagesStorage.getInstance(r0)
            int r4 = r9.chatId
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r0)
            java.util.concurrent.CountDownLatch r6 = new java.util.concurrent.CountDownLatch
            r6.<init>(r1)
            r7 = 0
            r8 = 0
            org.telegram.tgnet.TLRPC$ChatFull r0 = r3.loadChatInfo(r4, r5, r6, r7, r8)
            r9.info = r0
            if (r0 != 0) goto L_0x0053
        L_0x0052:
            return r2
        L_0x0053:
            org.telegram.ui.Components.AvatarDrawable r0 = r9.avatarDrawable
            r3 = 5
            org.telegram.tgnet.TLRPC$Chat r4 = r9.currentChat
            java.lang.String r4 = r4.title
            r5 = 0
            r0.setInfo(r3, r4, r5)
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x006d
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x006d
            goto L_0x006e
        L_0x006d:
            r1 = 0
        L_0x006e:
            r9.isChannel = r1
            org.telegram.ui.Components.ImageUpdater r0 = r9.imageUpdater
            r0.parentFragment = r9
            r0.setDelegate(r9)
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = r0.signatures
            r9.signMessages = r0
            int r0 = r9.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad
            r0.addObserver(r9, r1)
            int r0 = r9.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.updateInterfaces
            r0.addObserver(r9, r1)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r9.info
            if (r0 == 0) goto L_0x009a
            r9.loadLinksCount()
        L_0x009a:
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
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getExportedChatInvites, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChatEditActivity.this.lambda$loadLinksCount$1$ChatEditActivity(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadLinksCount$1 */
    public /* synthetic */ void lambda$loadLinksCount$1$ChatEditActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ChatEditActivity.this.lambda$null$0$ChatEditActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$0 */
    public /* synthetic */ void lambda$null$0$ChatEditActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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

    public View createView(Context context) {
        TLRPC$ChatFull tLRPC$ChatFull;
        TLRPC$ChatFull tLRPC$ChatFull2;
        TLRPC$ChatFull tLRPC$ChatFull3;
        Context context2 = context;
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (ChatEditActivity.this.checkDiscard()) {
                        ChatEditActivity.this.finishFragment();
                    }
                } else if (i == 1) {
                    ChatEditActivity.this.processDone();
                }
            }
        });
        AnonymousClass3 r2 = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                int paddingTop = size2 - getPaddingTop();
                measureChildWithMargins(ChatEditActivity.this.actionBar, i, 0, i2, 0);
                if (measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                    this.ignoreLayout = true;
                    ChatEditActivity.this.nameTextView.hideEmojiView();
                    this.ignoreLayout = false;
                }
                int childCount = getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8 || childAt == ChatEditActivity.this.actionBar)) {
                        if (ChatEditActivity.this.nameTextView == null || !ChatEditActivity.this.nameTextView.isPopupView(childAt)) {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (paddingTop - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((paddingTop - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:25:0x0072  */
            /* JADX WARNING: Removed duplicated region for block: B:32:0x008c  */
            /* JADX WARNING: Removed duplicated region for block: B:40:0x00b3  */
            /* JADX WARNING: Removed duplicated region for block: B:41:0x00bc  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
                /*
                    r10 = this;
                    int r11 = r10.getChildCount()
                    int r0 = r10.measureKeyboardHeight()
                    r1 = 1101004800(0x41a00000, float:20.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    r2 = 0
                    if (r0 > r1) goto L_0x0026
                    boolean r1 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                    if (r1 != 0) goto L_0x0026
                    boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r1 != 0) goto L_0x0026
                    org.telegram.ui.ChatEditActivity r1 = org.telegram.ui.ChatEditActivity.this
                    org.telegram.ui.Components.EditTextEmoji r1 = r1.nameTextView
                    int r1 = r1.getEmojiPadding()
                    goto L_0x0027
                L_0x0026:
                    r1 = 0
                L_0x0027:
                    r10.setBottomClip(r1)
                L_0x002a:
                    if (r2 >= r11) goto L_0x00cf
                    android.view.View r3 = r10.getChildAt(r2)
                    int r4 = r3.getVisibility()
                    r5 = 8
                    if (r4 != r5) goto L_0x003a
                    goto L_0x00cb
                L_0x003a:
                    android.view.ViewGroup$LayoutParams r4 = r3.getLayoutParams()
                    android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
                    int r5 = r3.getMeasuredWidth()
                    int r6 = r3.getMeasuredHeight()
                    int r7 = r4.gravity
                    r8 = -1
                    if (r7 != r8) goto L_0x004f
                    r7 = 51
                L_0x004f:
                    r8 = r7 & 7
                    r7 = r7 & 112(0x70, float:1.57E-43)
                    r8 = r8 & 7
                    r9 = 1
                    if (r8 == r9) goto L_0x0063
                    r9 = 5
                    if (r8 == r9) goto L_0x005e
                    int r8 = r4.leftMargin
                    goto L_0x006e
                L_0x005e:
                    int r8 = r14 - r5
                    int r9 = r4.rightMargin
                    goto L_0x006d
                L_0x0063:
                    int r8 = r14 - r12
                    int r8 = r8 - r5
                    int r8 = r8 / 2
                    int r9 = r4.leftMargin
                    int r8 = r8 + r9
                    int r9 = r4.rightMargin
                L_0x006d:
                    int r8 = r8 - r9
                L_0x006e:
                    r9 = 16
                    if (r7 == r9) goto L_0x008c
                    r9 = 48
                    if (r7 == r9) goto L_0x0084
                    r9 = 80
                    if (r7 == r9) goto L_0x007d
                    int r4 = r4.topMargin
                    goto L_0x0099
                L_0x007d:
                    int r7 = r15 - r1
                    int r7 = r7 - r13
                    int r7 = r7 - r6
                    int r4 = r4.bottomMargin
                    goto L_0x0097
                L_0x0084:
                    int r4 = r4.topMargin
                    int r7 = r10.getPaddingTop()
                    int r4 = r4 + r7
                    goto L_0x0099
                L_0x008c:
                    int r7 = r15 - r1
                    int r7 = r7 - r13
                    int r7 = r7 - r6
                    int r7 = r7 / 2
                    int r9 = r4.topMargin
                    int r7 = r7 + r9
                    int r4 = r4.bottomMargin
                L_0x0097:
                    int r4 = r7 - r4
                L_0x0099:
                    org.telegram.ui.ChatEditActivity r7 = org.telegram.ui.ChatEditActivity.this
                    org.telegram.ui.Components.EditTextEmoji r7 = r7.nameTextView
                    if (r7 == 0) goto L_0x00c6
                    org.telegram.ui.ChatEditActivity r7 = org.telegram.ui.ChatEditActivity.this
                    org.telegram.ui.Components.EditTextEmoji r7 = r7.nameTextView
                    boolean r7 = r7.isPopupView(r3)
                    if (r7 == 0) goto L_0x00c6
                    boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r4 == 0) goto L_0x00bc
                    int r4 = r10.getMeasuredHeight()
                    int r7 = r3.getMeasuredHeight()
                    goto L_0x00c5
                L_0x00bc:
                    int r4 = r10.getMeasuredHeight()
                    int r4 = r4 + r0
                    int r7 = r3.getMeasuredHeight()
                L_0x00c5:
                    int r4 = r4 - r7
                L_0x00c6:
                    int r5 = r5 + r8
                    int r6 = r6 + r4
                    r3.layout(r8, r4, r5, r6)
                L_0x00cb:
                    int r2 = r2 + 1
                    goto L_0x002a
                L_0x00cf:
                    r10.notifyHeightChanged()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.AnonymousClass3.onLayout(boolean, int, int, int, int):void");
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        r2.setOnTouchListener($$Lambda$ChatEditActivity$DZ9c_Xtx6di58wVsbEUkT4s0j8.INSTANCE);
        this.fragmentView = r2;
        r2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        ScrollView scrollView = new ScrollView(context2);
        scrollView.setFillViewport(true);
        r2.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        scrollView.addView(linearLayout, new FrameLayout.LayoutParams(-1, -2));
        linearLayout.setOrientation(1);
        this.actionBar.setTitle(LocaleController.getString("ChannelEdit", NUM));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        this.avatarContainer = linearLayout2;
        linearLayout2.setOrientation(1);
        this.avatarContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout.addView(this.avatarContainer, LayoutHelper.createLinear(-1, -2));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.avatarContainer.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
        AnonymousClass4 r9 = new BackupImageView(context2) {
            public void invalidate() {
                if (ChatEditActivity.this.avatarOverlay != null) {
                    ChatEditActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate();
            }

            public void invalidate(int i, int i2, int i3, int i4) {
                if (ChatEditActivity.this.avatarOverlay != null) {
                    ChatEditActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate(i, i2, i3, i4);
            }
        };
        this.avatarImage = r9;
        r9.setRoundRadius(AndroidUtilities.dp(32.0f));
        int i = 5;
        float f = 0.0f;
        if (ChatObject.canChangeChatInfo(this.currentChat)) {
            BackupImageView backupImageView = this.avatarImage;
            boolean z = LocaleController.isRTL;
            frameLayout.addView(backupImageView, LayoutHelper.createFrame(64, 64.0f, (z ? 5 : 3) | 48, z ? 0.0f : 16.0f, 12.0f, z ? 16.0f : 0.0f, 8.0f));
            final Paint paint = new Paint(1);
            paint.setColor(NUM);
            AnonymousClass5 r15 = new View(context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (ChatEditActivity.this.avatarImage != null && ChatEditActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                        paint.setAlpha((int) (ChatEditActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                        canvas.drawCircle(((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f, ((float) getMeasuredWidth()) / 2.0f, paint);
                    }
                }
            };
            this.avatarOverlay = r15;
            boolean z2 = LocaleController.isRTL;
            frameLayout.addView(r15, LayoutHelper.createFrame(64, 64.0f, (z2 ? 5 : 3) | 48, z2 ? 0.0f : 16.0f, 12.0f, z2 ? 16.0f : 0.0f, 8.0f));
            RadialProgressView radialProgressView = new RadialProgressView(context2);
            this.avatarProgressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(30.0f));
            this.avatarProgressView.setProgressColor(-1);
            this.avatarProgressView.setNoProgress(false);
            RadialProgressView radialProgressView2 = this.avatarProgressView;
            boolean z3 = LocaleController.isRTL;
            int i2 = (z3 ? 5 : 3) | 48;
            float f2 = z3 ? 0.0f : 16.0f;
            if (z3) {
                f = 16.0f;
            }
            frameLayout.addView(radialProgressView2, LayoutHelper.createFrame(64, 64.0f, i2, f2, 12.0f, f, 8.0f));
            showAvatarProgress(false, false);
            this.avatarContainer.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$3$ChatEditActivity(view);
                }
            });
        } else {
            BackupImageView backupImageView2 = this.avatarImage;
            boolean z4 = LocaleController.isRTL;
            int i3 = (z4 ? 5 : 3) | 48;
            float f3 = z4 ? 0.0f : 16.0f;
            if (z4) {
                f = 16.0f;
            }
            frameLayout.addView(backupImageView2, LayoutHelper.createFrame(64, 64.0f, i3, f3, 12.0f, f, 12.0f));
        }
        EditTextEmoji editTextEmoji2 = new EditTextEmoji(context2, r2, this, 0);
        this.nameTextView = editTextEmoji2;
        if (this.isChannel) {
            editTextEmoji2.setHint(LocaleController.getString("EnterChannelName", NUM));
        } else {
            editTextEmoji2.setHint(LocaleController.getString("GroupName", NUM));
        }
        this.nameTextView.setEnabled(ChatObject.canChangeChatInfo(this.currentChat));
        EditTextEmoji editTextEmoji3 = this.nameTextView;
        editTextEmoji3.setFocusable(editTextEmoji3.isEnabled());
        this.nameTextView.getEditText().addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                ChatEditActivity.this.avatarDrawable.setInfo(5, ChatEditActivity.this.nameTextView.getText().toString(), (String) null);
                if (ChatEditActivity.this.avatarImage != null) {
                    ChatEditActivity.this.avatarImage.invalidate();
                }
            }
        });
        this.nameTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(128)});
        EditTextEmoji editTextEmoji4 = this.nameTextView;
        boolean z5 = LocaleController.isRTL;
        frameLayout.addView(editTextEmoji4, LayoutHelper.createFrame(-1, -2.0f, 16, z5 ? 5.0f : 96.0f, 0.0f, z5 ? 96.0f : 5.0f, 0.0f));
        LinearLayout linearLayout3 = new LinearLayout(context2);
        this.settingsContainer = linearLayout3;
        linearLayout3.setOrientation(1);
        this.settingsContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout.addView(this.settingsContainer, LayoutHelper.createLinear(-1, -2));
        if (ChatObject.canChangeChatInfo(this.currentChat)) {
            AnonymousClass7 r4 = new TextCell(this, context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
                }
            };
            this.setAvatarCell = r4;
            r4.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.setAvatarCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
            this.setAvatarCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$5$ChatEditActivity(view);
                }
            });
            this.settingsContainer.addView(this.setAvatarCell, LayoutHelper.createLinear(-1, -2));
        }
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.descriptionTextView = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 16.0f);
        this.descriptionTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.descriptionTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
        this.descriptionTextView.setBackgroundDrawable((Drawable) null);
        EditTextBoldCursor editTextBoldCursor2 = this.descriptionTextView;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        editTextBoldCursor2.setGravity(i);
        this.descriptionTextView.setInputType(180225);
        this.descriptionTextView.setImeOptions(6);
        this.descriptionTextView.setEnabled(ChatObject.canChangeChatInfo(this.currentChat));
        EditTextBoldCursor editTextBoldCursor3 = this.descriptionTextView;
        editTextBoldCursor3.setFocusable(editTextBoldCursor3.isEnabled());
        this.descriptionTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(255)});
        this.descriptionTextView.setHint(LocaleController.getString("DescriptionOptionalPlaceholder", NUM));
        this.descriptionTextView.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        this.descriptionTextView.setCursorWidth(1.5f);
        if (this.descriptionTextView.isEnabled()) {
            this.settingsContainer.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 23.0f, 15.0f, 23.0f, 9.0f));
        } else {
            this.settingsContainer.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 23.0f, 12.0f, 23.0f, 6.0f));
        }
        this.descriptionTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ChatEditActivity.this.lambda$createView$6$ChatEditActivity(textView, i, keyEvent);
            }
        });
        this.descriptionTextView.addTextChangedListener(new TextWatcher(this) {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
        ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context2);
        this.settingsTopSectionCell = shadowSectionCell;
        linearLayout.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
        LinearLayout linearLayout4 = new LinearLayout(context2);
        this.typeEditContainer = linearLayout4;
        linearLayout4.setOrientation(1);
        this.typeEditContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout.addView(this.typeEditContainer, LayoutHelper.createLinear(-1, -2));
        if (this.currentChat.megagroup && ((tLRPC$ChatFull3 = this.info) == null || tLRPC$ChatFull3.can_set_location)) {
            TextDetailCell textDetailCell = new TextDetailCell(context2);
            this.locationCell = textDetailCell;
            textDetailCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.locationCell, LayoutHelper.createLinear(-1, -2));
            this.locationCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$8$ChatEditActivity(view);
                }
            });
        }
        if (this.currentChat.creator && ((tLRPC$ChatFull2 = this.info) == null || tLRPC$ChatFull2.can_set_username)) {
            TextDetailCell textDetailCell2 = new TextDetailCell(context2);
            this.typeCell = textDetailCell2;
            textDetailCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.typeCell, LayoutHelper.createLinear(-1, -2));
            this.typeCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$9$ChatEditActivity(view);
                }
            });
        }
        if (ChatObject.isChannel(this.currentChat) && ((this.isChannel && ChatObject.canUserDoAdminAction(this.currentChat, 1)) || (!this.isChannel && ChatObject.canUserDoAdminAction(this.currentChat, 0)))) {
            TextDetailCell textDetailCell3 = new TextDetailCell(context2);
            this.linkedCell = textDetailCell3;
            textDetailCell3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.linkedCell, LayoutHelper.createLinear(-1, -2));
            this.linkedCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$10$ChatEditActivity(view);
                }
            });
        }
        if (!this.isChannel && ChatObject.canBlockUsers(this.currentChat) && (ChatObject.isChannel(this.currentChat) || this.currentChat.creator)) {
            TextDetailCell textDetailCell4 = new TextDetailCell(context2);
            this.historyCell = textDetailCell4;
            textDetailCell4.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.historyCell, LayoutHelper.createLinear(-1, -2));
            this.historyCell.setOnClickListener(new View.OnClickListener(context2) {
                public final /* synthetic */ Context f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$12$ChatEditActivity(this.f$1, view);
                }
            });
        }
        if (this.isChannel) {
            TextCheckCell textCheckCell = new TextCheckCell(context2);
            this.signCell = textCheckCell;
            textCheckCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.signCell.setTextAndValueAndCheck(LocaleController.getString("ChannelSignMessages", NUM), LocaleController.getString("ChannelSignMessagesInfo", NUM), this.signMessages, true, false);
            this.typeEditContainer.addView(this.signCell, LayoutHelper.createFrame(-1, -2.0f));
            this.signCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$13$ChatEditActivity(view);
                }
            });
        }
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (!(!ChatObject.canChangeChatInfo(this.currentChat) && this.signCell == null && this.historyCell == null)) {
            ActionBarMenuItem addItemWithWidth = createMenu.addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
            this.doneButton = addItemWithWidth;
            addItemWithWidth.setContentDescription(LocaleController.getString("Done", NUM));
        }
        if (!(this.locationCell == null && this.signCell == null && this.historyCell == null && this.typeCell == null && this.linkedCell == null)) {
            ShadowSectionCell shadowSectionCell2 = new ShadowSectionCell(context2);
            this.settingsSectionCell = shadowSectionCell2;
            linearLayout.addView(shadowSectionCell2, LayoutHelper.createLinear(-1, -2));
        }
        LinearLayout linearLayout5 = new LinearLayout(context2);
        this.infoContainer = linearLayout5;
        linearLayout5.setOrientation(1);
        this.infoContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout.addView(this.infoContainer, LayoutHelper.createLinear(-1, -2));
        TextCell textCell = new TextCell(context2);
        this.blockCell = textCell;
        textCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.blockCell.setVisibility((ChatObject.isChannel(this.currentChat) || this.currentChat.creator) ? 0 : 8);
        this.blockCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditActivity.this.lambda$createView$14$ChatEditActivity(view);
            }
        });
        TextCell textCell2 = new TextCell(context2);
        this.inviteLinksCell = textCell2;
        textCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.inviteLinksCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditActivity.this.lambda$createView$15$ChatEditActivity(view);
            }
        });
        TextCell textCell3 = new TextCell(context2);
        this.adminCell = textCell3;
        textCell3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.adminCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditActivity.this.lambda$createView$16$ChatEditActivity(view);
            }
        });
        TextCell textCell4 = new TextCell(context2);
        this.membersCell = textCell4;
        textCell4.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.membersCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditActivity.this.lambda$createView$17$ChatEditActivity(view);
            }
        });
        if (ChatObject.isChannel(this.currentChat) || this.currentChat.gigagroup) {
            TextCell textCell5 = new TextCell(context2);
            this.logCell = textCell5;
            textCell5.setTextAndIcon(LocaleController.getString("EventLog", NUM), NUM, false);
            this.logCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.logCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$18$ChatEditActivity(view);
                }
            });
        }
        if (!this.isChannel && !this.currentChat.gigagroup) {
            this.infoContainer.addView(this.blockCell, LayoutHelper.createLinear(-1, -2));
        }
        if (!this.isChannel) {
            this.infoContainer.addView(this.inviteLinksCell, LayoutHelper.createLinear(-1, -2));
        }
        this.infoContainer.addView(this.adminCell, LayoutHelper.createLinear(-1, -2));
        this.infoContainer.addView(this.membersCell, LayoutHelper.createLinear(-1, -2));
        if (this.isChannel) {
            this.infoContainer.addView(this.inviteLinksCell, LayoutHelper.createLinear(-1, -2));
        }
        if (this.isChannel || this.currentChat.gigagroup) {
            this.infoContainer.addView(this.blockCell, LayoutHelper.createLinear(-1, -2));
        }
        TextCell textCell6 = this.logCell;
        if (textCell6 != null) {
            this.infoContainer.addView(textCell6, LayoutHelper.createLinear(-1, -2));
        }
        ShadowSectionCell shadowSectionCell3 = new ShadowSectionCell(context2);
        this.infoSectionCell = shadowSectionCell3;
        linearLayout.addView(shadowSectionCell3, LayoutHelper.createLinear(-1, -2));
        if (!ChatObject.hasAdminRights(this.currentChat)) {
            this.infoContainer.setVisibility(8);
            this.settingsTopSectionCell.setVisibility(8);
        }
        if (!this.isChannel && (tLRPC$ChatFull = this.info) != null && tLRPC$ChatFull.can_set_stickers) {
            FrameLayout frameLayout2 = new FrameLayout(context2);
            this.stickersContainer = frameLayout2;
            frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            linearLayout.addView(this.stickersContainer, LayoutHelper.createLinear(-1, -2));
            TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
            this.stickersCell = textSettingsCell;
            textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.stickersCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.stickersContainer.addView(this.stickersCell, LayoutHelper.createFrame(-1, -2.0f));
            this.stickersCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$19$ChatEditActivity(view);
                }
            });
            TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
            this.stickersInfoCell3 = textInfoPrivacyCell;
            textInfoPrivacyCell.setText(LocaleController.getString("GroupStickersInfo", NUM));
            linearLayout.addView(this.stickersInfoCell3, LayoutHelper.createLinear(-1, -2));
        }
        if (this.currentChat.creator) {
            FrameLayout frameLayout3 = new FrameLayout(context2);
            this.deleteContainer = frameLayout3;
            frameLayout3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            linearLayout.addView(this.deleteContainer, LayoutHelper.createLinear(-1, -2));
            TextSettingsCell textSettingsCell2 = new TextSettingsCell(context2);
            this.deleteCell = textSettingsCell2;
            textSettingsCell2.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
            this.deleteCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (this.isChannel) {
                this.deleteCell.setText(LocaleController.getString("ChannelDelete", NUM), false);
            } else {
                this.deleteCell.setText(LocaleController.getString("DeleteAndExitButton", NUM), false);
            }
            this.deleteContainer.addView(this.deleteCell, LayoutHelper.createFrame(-1, -2.0f));
            this.deleteCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$21$ChatEditActivity(view);
                }
            });
            ShadowSectionCell shadowSectionCell4 = new ShadowSectionCell(context2);
            this.deleteInfoCell = shadowSectionCell4;
            shadowSectionCell4.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            linearLayout.addView(this.deleteInfoCell, LayoutHelper.createLinear(-1, -2));
        } else if (!this.isChannel && this.stickersInfoCell3 == null) {
            this.infoSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        }
        TextInfoPrivacyCell textInfoPrivacyCell2 = this.stickersInfoCell3;
        if (textInfoPrivacyCell2 != null) {
            if (this.deleteInfoCell == null) {
                textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            } else {
                textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            }
        }
        UndoView undoView2 = new UndoView(context2);
        this.undoView = undoView2;
        r2.addView(undoView2, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        this.nameTextView.setText(this.currentChat.title);
        EditTextEmoji editTextEmoji5 = this.nameTextView;
        editTextEmoji5.setSelection(editTextEmoji5.length());
        TLRPC$ChatFull tLRPC$ChatFull4 = this.info;
        if (tLRPC$ChatFull4 != null) {
            this.descriptionTextView.setText(tLRPC$ChatFull4.about);
        }
        setAvatar();
        updateFields(true);
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0009, code lost:
        r4 = getMessagesController().getChat(java.lang.Integer.valueOf(r3.chatId));
     */
    /* renamed from: lambda$createView$3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$3$ChatEditActivity(android.view.View r4) {
        /*
            r3 = this;
            org.telegram.ui.Components.ImageUpdater r4 = r3.imageUpdater
            boolean r4 = r4.isUploadingImage()
            if (r4 == 0) goto L_0x0009
            return
        L_0x0009:
            org.telegram.messenger.MessagesController r4 = r3.getMessagesController()
            int r0 = r3.chatId
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.lambda$createView$3$ChatEditActivity(android.view.View):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$5 */
    public /* synthetic */ void lambda$createView$5$ChatEditActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new Runnable() {
            public final void run() {
                ChatEditActivity.this.lambda$null$4$ChatEditActivity();
            }
        }, (DialogInterface.OnDismissListener) null);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$4 */
    public /* synthetic */ void lambda$null$4$ChatEditActivity() {
        this.avatar = null;
        MessagesController.getInstance(this.currentAccount).changeChatAvatar(this.chatId, (TLRPC$TL_inputChatPhoto) null, (TLRPC$InputFile) null, (TLRPC$InputFile) null, 0.0d, (String) null, (TLRPC$FileLocation) null, (TLRPC$FileLocation) null);
        showAvatarProgress(false, true);
        this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) this.currentChat);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$6 */
    public /* synthetic */ boolean lambda$createView$6$ChatEditActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$8 */
    public /* synthetic */ void lambda$createView$8$ChatEditActivity(View view) {
        if (AndroidUtilities.isGoogleMapsInstalled(this)) {
            LocationActivity locationActivity = new LocationActivity(4);
            locationActivity.setDialogId((long) (-this.chatId));
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            if (tLRPC$ChatFull != null) {
                TLRPC$ChannelLocation tLRPC$ChannelLocation = tLRPC$ChatFull.location;
                if (tLRPC$ChannelLocation instanceof TLRPC$TL_channelLocation) {
                    locationActivity.setInitialLocation((TLRPC$TL_channelLocation) tLRPC$ChannelLocation);
                }
            }
            locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate() {
                public final void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
                    ChatEditActivity.this.lambda$null$7$ChatEditActivity(tLRPC$MessageMedia, i, z, i2);
                }
            });
            presentFragment(locationActivity);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$7 */
    public /* synthetic */ void lambda$null$7$ChatEditActivity(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
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
    /* renamed from: lambda$createView$9 */
    public /* synthetic */ void lambda$createView$9$ChatEditActivity(View view) {
        int i = this.chatId;
        TextDetailCell textDetailCell = this.locationCell;
        ChatEditTypeActivity chatEditTypeActivity = new ChatEditTypeActivity(i, textDetailCell != null && textDetailCell.getVisibility() == 0);
        chatEditTypeActivity.setInfo(this.info);
        presentFragment(chatEditTypeActivity);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$10 */
    public /* synthetic */ void lambda$createView$10$ChatEditActivity(View view) {
        ChatLinkActivity chatLinkActivity = new ChatLinkActivity(this.chatId);
        chatLinkActivity.setInfo(this.info);
        presentFragment(chatLinkActivity);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$12 */
    public /* synthetic */ void lambda$createView$12$ChatEditActivity(Context context, View view) {
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
            radioButtonCellArr[i].setOnClickListener(new View.OnClickListener(radioButtonCellArr, builder) {
                public final /* synthetic */ RadioButtonCell[] f$1;
                public final /* synthetic */ BottomSheet.Builder f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$null$11$ChatEditActivity(this.f$1, this.f$2, view);
                }
            });
        }
        builder.setCustomView(linearLayout);
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$11 */
    public /* synthetic */ void lambda$null$11$ChatEditActivity(RadioButtonCell[] radioButtonCellArr, BottomSheet.Builder builder, View view) {
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
    /* renamed from: lambda$createView$13 */
    public /* synthetic */ void lambda$createView$13$ChatEditActivity(View view) {
        boolean z = !this.signMessages;
        this.signMessages = z;
        ((TextCheckCell) view).setChecked(z);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$14 */
    public /* synthetic */ void lambda$createView$14$ChatEditActivity(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("chat_id", this.chatId);
        bundle.putInt("type", (this.isChannel || this.currentChat.gigagroup) ? 0 : 3);
        ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle);
        chatUsersActivity.setInfo(this.info);
        presentFragment(chatUsersActivity);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$15 */
    public /* synthetic */ void lambda$createView$15$ChatEditActivity(View view) {
        ManageLinksActivity manageLinksActivity = new ManageLinksActivity(this.chatId, 0, 0);
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        manageLinksActivity.setInfo(tLRPC$ChatFull, tLRPC$ChatFull.exported_invite);
        presentFragment(manageLinksActivity);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$16 */
    public /* synthetic */ void lambda$createView$16$ChatEditActivity(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("chat_id", this.chatId);
        bundle.putInt("type", 1);
        ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle);
        chatUsersActivity.setInfo(this.info);
        presentFragment(chatUsersActivity);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$17 */
    public /* synthetic */ void lambda$createView$17$ChatEditActivity(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("chat_id", this.chatId);
        bundle.putInt("type", 2);
        ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle);
        chatUsersActivity.setInfo(this.info);
        presentFragment(chatUsersActivity);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$18 */
    public /* synthetic */ void lambda$createView$18$ChatEditActivity(View view) {
        presentFragment(new ChannelAdminLogActivity(this.currentChat));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$19 */
    public /* synthetic */ void lambda$createView$19$ChatEditActivity(View view) {
        GroupStickersActivity groupStickersActivity = new GroupStickersActivity(this.currentChat.id);
        groupStickersActivity.setInfo(this.info);
        presentFragment(groupStickersActivity);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$21 */
    public /* synthetic */ void lambda$createView$21$ChatEditActivity(View view) {
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, true, false, this.currentChat, (TLRPC$User) null, false, true, new MessagesStorage.BooleanCallback() {
            public final void run(boolean z) {
                ChatEditActivity.this.lambda$null$20$ChatEditActivity(z);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$20 */
    public /* synthetic */ void lambda$null$20$ChatEditActivity(boolean z) {
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(-((long) this.chatId)));
        } else {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        finishFragment();
        getNotificationCenter().postNotificationName(NotificationCenter.needDeleteDialog, Long.valueOf((long) (-this.currentChat.id)), null, this.currentChat, Boolean.valueOf(z));
    }

    private void setAvatar() {
        TLRPC$Chat chat;
        if (this.avatarImage != null && (chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId))) != null) {
            this.currentChat = chat;
            TLRPC$ChatPhoto tLRPC$ChatPhoto = chat.photo;
            boolean z = false;
            if (tLRPC$ChatPhoto != null) {
                this.avatar = tLRPC$ChatPhoto.photo_small;
                ImageLocation forChat = ImageLocation.getForChat(chat, false);
                this.avatarImage.setImage(forChat, "50_50", (Drawable) this.avatarDrawable, (Object) this.currentChat);
                if (forChat != null) {
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
        } else if (i == NotificationCenter.updateInterfaces && (objArr[0].intValue() & 2) != 0) {
            setAvatar();
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
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$PhotoSize2, tLRPC$InputFile, tLRPC$InputFile2, d, str, tLRPC$PhotoSize) {
            public final /* synthetic */ TLRPC$PhotoSize f$1;
            public final /* synthetic */ TLRPC$InputFile f$2;
            public final /* synthetic */ TLRPC$InputFile f$3;
            public final /* synthetic */ double f$4;
            public final /* synthetic */ String f$5;
            public final /* synthetic */ TLRPC$PhotoSize f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r7;
                this.f$6 = r8;
            }

            public final void run() {
                ChatEditActivity.this.lambda$didUploadPhoto$22$ChatEditActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didUploadPhoto$22 */
    public /* synthetic */ void lambda$didUploadPhoto$22$ChatEditActivity(TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, String str, TLRPC$PhotoSize tLRPC$PhotoSize2) {
        TLRPC$PhotoSize tLRPC$PhotoSize3 = tLRPC$PhotoSize;
        TLRPC$FileLocation tLRPC$FileLocation = tLRPC$PhotoSize3.location;
        this.avatar = tLRPC$FileLocation;
        if (tLRPC$InputFile == null && tLRPC$InputFile2 == null) {
            this.avatarImage.setImage(ImageLocation.getForLocal(tLRPC$FileLocation), "50_50", (Drawable) this.avatarDrawable, (Object) this.currentChat);
            this.setAvatarCell.setTextAndIcon(LocaleController.getString("ChatSetNewPhoto", NUM), NUM, true);
            showAvatarProgress(true, false);
            return;
        }
        MessagesController.getInstance(this.currentAccount).changeChatAvatar(this.chatId, (TLRPC$TL_inputChatPhoto) null, tLRPC$InputFile, tLRPC$InputFile2, d, str, tLRPC$PhotoSize3.location, tLRPC$PhotoSize2.location);
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
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatEditActivity.this.lambda$checkDiscard$23$ChatEditActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatEditActivity.this.lambda$checkDiscard$24$ChatEditActivity(dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkDiscard$23 */
    public /* synthetic */ void lambda$checkDiscard$23$ChatEditActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkDiscard$24 */
    public /* synthetic */ void lambda$checkDiscard$24$ChatEditActivity(DialogInterface dialogInterface, int i) {
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
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x005d, code lost:
        r1 = r5.info;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processDone() {
        /*
            r5 = this;
            boolean r0 = r5.donePressed
            if (r0 != 0) goto L_0x010f
            org.telegram.ui.Components.EditTextEmoji r0 = r5.nameTextView
            if (r0 != 0) goto L_0x000a
            goto L_0x010f
        L_0x000a:
            int r0 = r0.length()
            if (r0 != 0) goto L_0x002d
            android.app.Activity r0 = r5.getParentActivity()
            java.lang.String r1 = "vibrator"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.os.Vibrator r0 = (android.os.Vibrator) r0
            if (r0 == 0) goto L_0x0024
            r1 = 200(0xc8, double:9.9E-322)
            r0.vibrate(r1)
        L_0x0024:
            org.telegram.ui.Components.EditTextEmoji r0 = r5.nameTextView
            r1 = 1073741824(0x40000000, float:2.0)
            r2 = 0
            org.telegram.messenger.AndroidUtilities.shakeView(r0, r1, r2)
            return
        L_0x002d:
            r0 = 1
            r5.donePressed = r0
            org.telegram.tgnet.TLRPC$Chat r1 = r5.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 != 0) goto L_0x0051
            boolean r1 = r5.historyHidden
            if (r1 != 0) goto L_0x0051
            int r0 = r5.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            android.app.Activity r1 = r5.getParentActivity()
            int r2 = r5.chatId
            org.telegram.ui.-$$Lambda$ChatEditActivity$N2TkNLUMn57R0plwzGf7KZ5D2CE r3 = new org.telegram.ui.-$$Lambda$ChatEditActivity$N2TkNLUMn57R0plwzGf7KZ5D2CE
            r3.<init>()
            r0.convertToMegaGroup(r1, r2, r5, r3)
            return
        L_0x0051:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r5.info
            if (r1 == 0) goto L_0x0074
            org.telegram.tgnet.TLRPC$Chat r1 = r5.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 == 0) goto L_0x0074
            org.telegram.tgnet.TLRPC$ChatFull r1 = r5.info
            boolean r2 = r1.hidden_prehistory
            boolean r3 = r5.historyHidden
            if (r2 == r3) goto L_0x0074
            r1.hidden_prehistory = r3
            int r1 = r5.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = r5.chatId
            boolean r3 = r5.historyHidden
            r1.toogleChannelInvitesHistory(r2, r3)
        L_0x0074:
            org.telegram.ui.Components.ImageUpdater r1 = r5.imageUpdater
            boolean r1 = r1.isUploadingImage()
            if (r1 == 0) goto L_0x0098
            r5.createAfterUpload = r0
            org.telegram.ui.ActionBar.AlertDialog r0 = new org.telegram.ui.ActionBar.AlertDialog
            android.app.Activity r1 = r5.getParentActivity()
            r2 = 3
            r0.<init>(r1, r2)
            r5.progressDialog = r0
            org.telegram.ui.-$$Lambda$ChatEditActivity$hKXZJVx3myVHTfr7bv3MGkNFvbo r1 = new org.telegram.ui.-$$Lambda$ChatEditActivity$hKXZJVx3myVHTfr7bv3MGkNFvbo
            r1.<init>()
            r0.setOnCancelListener(r1)
            org.telegram.ui.ActionBar.AlertDialog r0 = r5.progressDialog
            r0.show()
            return
        L_0x0098:
            org.telegram.tgnet.TLRPC$Chat r1 = r5.currentChat
            java.lang.String r1 = r1.title
            org.telegram.ui.Components.EditTextEmoji r2 = r5.nameTextView
            android.text.Editable r2 = r2.getText()
            java.lang.String r2 = r2.toString()
            boolean r1 = r1.equals(r2)
            if (r1 != 0) goto L_0x00c1
            int r1 = r5.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = r5.chatId
            org.telegram.ui.Components.EditTextEmoji r3 = r5.nameTextView
            android.text.Editable r3 = r3.getText()
            java.lang.String r3 = r3.toString()
            r1.changeChatTitle(r2, r3)
        L_0x00c1:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r5.info
            if (r1 == 0) goto L_0x00ca
            java.lang.String r1 = r1.about
            if (r1 == 0) goto L_0x00ca
            goto L_0x00cc
        L_0x00ca:
            java.lang.String r1 = ""
        L_0x00cc:
            org.telegram.ui.Components.EditTextBoldCursor r2 = r5.descriptionTextView
            if (r2 == 0) goto L_0x00f5
            android.text.Editable r2 = r2.getText()
            java.lang.String r2 = r2.toString()
            boolean r1 = r1.equals(r2)
            if (r1 != 0) goto L_0x00f5
            int r1 = r5.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = r5.chatId
            org.telegram.ui.Components.EditTextBoldCursor r3 = r5.descriptionTextView
            android.text.Editable r3 = r3.getText()
            java.lang.String r3 = r3.toString()
            org.telegram.tgnet.TLRPC$ChatFull r4 = r5.info
            r1.updateChatAbout(r2, r3, r4)
        L_0x00f5:
            boolean r1 = r5.signMessages
            org.telegram.tgnet.TLRPC$Chat r2 = r5.currentChat
            boolean r3 = r2.signatures
            if (r1 == r3) goto L_0x010c
            r2.signatures = r0
            int r0 = r5.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r1 = r5.chatId
            boolean r2 = r5.signMessages
            r0.toogleChannelSignatures(r1, r2)
        L_0x010c:
            r5.finishFragment()
        L_0x010f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.processDone():void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processDone$25 */
    public /* synthetic */ void lambda$processDone$25$ChatEditActivity(int i) {
        if (i == 0) {
            this.donePressed = false;
            return;
        }
        this.chatId = i;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(i));
        this.donePressed = false;
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        if (tLRPC$ChatFull != null) {
            tLRPC$ChatFull.hidden_prehistory = true;
        }
        processDone();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processDone$26 */
    public /* synthetic */ void lambda$processDone$26$ChatEditActivity(DialogInterface dialogInterface) {
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
                this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
            }
            this.historyHidden = !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory;
        }
    }

    private void updateFields(boolean z) {
        TLRPC$ChatFull tLRPC$ChatFull;
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
        TLRPC$ChatFull tLRPC$ChatFull2;
        TextDetailCell textDetailCell4;
        TextDetailCell textDetailCell5;
        TextDetailCell textDetailCell6;
        TLRPC$Chat chat;
        if (z && (chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId))) != null) {
            this.currentChat = chat;
        }
        boolean isEmpty = TextUtils.isEmpty(this.currentChat.username);
        TextDetailCell textDetailCell7 = this.historyCell;
        if (textDetailCell7 != null) {
            TLRPC$ChatFull tLRPC$ChatFull3 = this.info;
            if (tLRPC$ChatFull3 == null || !(tLRPC$ChatFull3.location instanceof TLRPC$TL_channelLocation)) {
                textDetailCell7.setVisibility((!isEmpty || !(tLRPC$ChatFull3 == null || tLRPC$ChatFull3.linked_chat_id == 0)) ? 8 : 0);
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
            textCell.setVisibility((!tLRPC$Chat.megagroup || tLRPC$Chat.gigagroup || ((tLRPC$ChatFull2 = this.info) != null && tLRPC$ChatFull2.participants_count > 200)) ? 0 : 8);
        }
        TextDetailCell textDetailCell8 = this.linkedCell;
        if (textDetailCell8 != null) {
            TLRPC$ChatFull tLRPC$ChatFull4 = this.info;
            if (tLRPC$ChatFull4 == null || (!this.isChannel && tLRPC$ChatFull4.linked_chat_id == 0)) {
                textDetailCell8.setVisibility(8);
            } else {
                textDetailCell8.setVisibility(0);
                if (this.info.linked_chat_id == 0) {
                    this.linkedCell.setTextAndValue(LocaleController.getString("Discussion", NUM), LocaleController.getString("DiscussionInfo", NUM), true);
                } else {
                    TLRPC$Chat chat2 = getMessagesController().getChat(Integer.valueOf(this.info.linked_chat_id));
                    if (chat2 == null) {
                        this.linkedCell.setVisibility(8);
                    } else if (this.isChannel) {
                        if (TextUtils.isEmpty(chat2.username)) {
                            this.linkedCell.setTextAndValue(LocaleController.getString("Discussion", NUM), chat2.title, true);
                        } else {
                            TextDetailCell textDetailCell9 = this.linkedCell;
                            String string = LocaleController.getString("Discussion", NUM);
                            textDetailCell9.setTextAndValue(string, "@" + chat2.username, true);
                        }
                    } else if (TextUtils.isEmpty(chat2.username)) {
                        this.linkedCell.setTextAndValue(LocaleController.getString("LinkedChannel", NUM), chat2.title, false);
                    } else {
                        TextDetailCell textDetailCell10 = this.linkedCell;
                        String string2 = LocaleController.getString("LinkedChannel", NUM);
                        textDetailCell10.setTextAndValue(string2, "@" + chat2.username, false);
                    }
                }
            }
        }
        TextDetailCell textDetailCell11 = this.locationCell;
        if (textDetailCell11 != null) {
            TLRPC$ChatFull tLRPC$ChatFull5 = this.info;
            if (tLRPC$ChatFull5 == null || !tLRPC$ChatFull5.can_set_location) {
                textDetailCell11.setVisibility(8);
            } else {
                textDetailCell11.setVisibility(0);
                TLRPC$ChannelLocation tLRPC$ChannelLocation = this.info.location;
                if (tLRPC$ChannelLocation instanceof TLRPC$TL_channelLocation) {
                    this.locationCell.setTextAndValue(LocaleController.getString("AttachLocation", NUM), ((TLRPC$TL_channelLocation) tLRPC$ChannelLocation).address, true);
                } else {
                    this.locationCell.setTextAndValue(LocaleController.getString("AttachLocation", NUM), "Unknown address", true);
                }
            }
        }
        if (this.typeCell != null) {
            TLRPC$ChatFull tLRPC$ChatFull6 = this.info;
            if (tLRPC$ChatFull6 == null || !(tLRPC$ChatFull6.location instanceof TLRPC$TL_channelLocation)) {
                if (this.isChannel) {
                    if (isEmpty) {
                        i4 = NUM;
                        str4 = "TypePrivate";
                    } else {
                        i4 = NUM;
                        str4 = "TypePublic";
                    }
                    str2 = LocaleController.getString(str4, i4);
                } else {
                    if (isEmpty) {
                        i3 = NUM;
                        str3 = "TypePrivateGroup";
                    } else {
                        i3 = NUM;
                        str3 = "TypePublicGroup";
                    }
                    str2 = LocaleController.getString(str3, i3);
                }
                if (this.isChannel) {
                    TextDetailCell textDetailCell12 = this.typeCell;
                    String string3 = LocaleController.getString("ChannelType", NUM);
                    TextDetailCell textDetailCell13 = this.historyCell;
                    textDetailCell12.setTextAndValue(string3, str2, (textDetailCell13 != null && textDetailCell13.getVisibility() == 0) || ((textDetailCell2 = this.linkedCell) != null && textDetailCell2.getVisibility() == 0));
                } else {
                    TextDetailCell textDetailCell14 = this.typeCell;
                    String string4 = LocaleController.getString("GroupType", NUM);
                    TextDetailCell textDetailCell15 = this.historyCell;
                    textDetailCell14.setTextAndValue(string4, str2, (textDetailCell15 != null && textDetailCell15.getVisibility() == 0) || ((textDetailCell = this.linkedCell) != null && textDetailCell.getVisibility() == 0));
                }
            } else {
                if (isEmpty) {
                    str5 = LocaleController.getString("TypeLocationGroupEdit", NUM);
                } else {
                    str5 = String.format("https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/%s", new Object[]{this.currentChat.username});
                }
                TextDetailCell textDetailCell16 = this.typeCell;
                String string5 = LocaleController.getString("TypeLocationGroup", NUM);
                TextDetailCell textDetailCell17 = this.historyCell;
                textDetailCell16.setTextAndValue(string5, str5, (textDetailCell17 != null && textDetailCell17.getVisibility() == 0) || ((textDetailCell3 = this.linkedCell) != null && textDetailCell3.getVisibility() == 0));
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
        TextSettingsCell textSettingsCell = this.stickersCell;
        if (textSettingsCell != null) {
            TLRPC$ChatFull tLRPC$ChatFull7 = this.info;
            if (tLRPC$ChatFull7 == null || tLRPC$ChatFull7.stickerset == null) {
                textSettingsCell.setText(LocaleController.getString("GroupStickers", NUM), false);
            } else {
                textSettingsCell.setTextAndValue(LocaleController.getString("GroupStickers", NUM), this.info.stickerset.title, false);
            }
        }
        TextCell textCell2 = this.membersCell;
        if (textCell2 != null) {
            if (this.info != null) {
                if (this.isChannel) {
                    textCell2.setTextAndValueAndIcon(LocaleController.getString("ChannelSubscribers", NUM), String.format("%d", new Object[]{Integer.valueOf(this.info.participants_count)}), NUM, true);
                    TextCell textCell3 = this.blockCell;
                    String string6 = LocaleController.getString("ChannelBlacklist", NUM);
                    TLRPC$ChatFull tLRPC$ChatFull8 = this.info;
                    String format = String.format("%d", new Object[]{Integer.valueOf(Math.max(tLRPC$ChatFull8.banned_count, tLRPC$ChatFull8.kicked_count))});
                    TextCell textCell4 = this.logCell;
                    textCell3.setTextAndValueAndIcon(string6, format, NUM, textCell4 != null && textCell4.getVisibility() == 0);
                } else {
                    if (ChatObject.isChannel(this.currentChat)) {
                        TextCell textCell5 = this.membersCell;
                        String string7 = LocaleController.getString("ChannelMembers", NUM);
                        String format2 = String.format("%d", new Object[]{Integer.valueOf(this.info.participants_count)});
                        TextCell textCell6 = this.logCell;
                        textCell5.setTextAndValueAndIcon(string7, format2, NUM, textCell6 != null && textCell6.getVisibility() == 0);
                    } else {
                        TextCell textCell7 = this.membersCell;
                        String string8 = LocaleController.getString("ChannelMembers", NUM);
                        String format3 = String.format("%d", new Object[]{Integer.valueOf(this.info.participants.participants.size())});
                        TextCell textCell8 = this.logCell;
                        textCell7.setTextAndValueAndIcon(string8, format3, NUM, textCell8 != null && textCell8.getVisibility() == 0);
                    }
                    TLRPC$Chat tLRPC$Chat2 = this.currentChat;
                    if (tLRPC$Chat2.gigagroup) {
                        TextCell textCell9 = this.blockCell;
                        String string9 = LocaleController.getString("ChannelBlacklist", NUM);
                        TLRPC$ChatFull tLRPC$ChatFull9 = this.info;
                        String format4 = String.format("%d", new Object[]{Integer.valueOf(Math.max(tLRPC$ChatFull9.banned_count, tLRPC$ChatFull9.kicked_count))});
                        TextCell textCell10 = this.logCell;
                        textCell9.setTextAndValueAndIcon(string9, format4, NUM, textCell10 != null && textCell10.getVisibility() == 0);
                    } else {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights = tLRPC$Chat2.default_banned_rights;
                        if (tLRPC$TL_chatBannedRights != null) {
                            int i5 = !tLRPC$TL_chatBannedRights.send_stickers ? 1 : 0;
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
                }
                TextCell textCell11 = this.adminCell;
                String string10 = LocaleController.getString("ChannelAdministrators", NUM);
                Object[] objArr = new Object[1];
                objArr[0] = Integer.valueOf(ChatObject.isChannel(this.currentChat) ? this.info.admins_count : getAdminCount());
                textCell11.setTextAndValueAndIcon(string10, String.format("%d", objArr), NUM, true);
            } else {
                if (this.isChannel) {
                    textCell2.setTextAndIcon(LocaleController.getString("ChannelSubscribers", NUM), NUM, true);
                    TextCell textCell12 = this.blockCell;
                    String string11 = LocaleController.getString("ChannelBlacklist", NUM);
                    TextCell textCell13 = this.logCell;
                    textCell12.setTextAndIcon(string11, NUM, textCell13 != null && textCell13.getVisibility() == 0);
                } else {
                    String string12 = LocaleController.getString("ChannelMembers", NUM);
                    TextCell textCell14 = this.logCell;
                    textCell2.setTextAndIcon(string12, NUM, textCell14 != null && textCell14.getVisibility() == 0);
                    if (this.currentChat.gigagroup) {
                        TextCell textCell15 = this.blockCell;
                        String string13 = LocaleController.getString("ChannelBlacklist", NUM);
                        TextCell textCell16 = this.logCell;
                        textCell15.setTextAndIcon(string13, NUM, textCell16 != null && textCell16.getVisibility() == 0);
                    } else {
                        this.blockCell.setTextAndIcon(LocaleController.getString("ChannelPermissions", NUM), NUM, true);
                    }
                }
                this.adminCell.setTextAndIcon(LocaleController.getString("ChannelAdministrators", NUM), NUM, true);
            }
            if (this.info == null || !ChatObject.canUserDoAdminAction(this.currentChat, 3) || (!isEmpty && this.currentChat.creator)) {
                this.inviteLinksCell.setVisibility(8);
            } else if (this.info.invitesCount > 0) {
                this.inviteLinksCell.setTextAndValueAndIcon(LocaleController.getString("InviteLinks", NUM), Integer.toString(this.info.invitesCount), NUM, true);
            } else {
                this.inviteLinksCell.setTextAndValueAndIcon(LocaleController.getString("InviteLinks", NUM), "1", NUM, true);
            }
        }
        TextSettingsCell textSettingsCell2 = this.stickersCell;
        if (textSettingsCell2 != null && (tLRPC$ChatFull = this.info) != null) {
            if (tLRPC$ChatFull.stickerset != null) {
                textSettingsCell2.setTextAndValue(LocaleController.getString("GroupStickers", NUM), this.info.stickerset.title, false);
            } else {
                textSettingsCell2.setText(LocaleController.getString("GroupStickers", NUM), false);
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$ChatEditActivity$II9hWQMBN5UIYcnhPQ2pczjBaE0 r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ChatEditActivity.this.lambda$getThemeDescriptions$27$ChatEditActivity();
            }
        };
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
        arrayList.add(new ThemeDescription(this.infoSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.signCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.deleteCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.deleteCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription(this.stickersCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.stickersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.stickersInfoCell3, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.stickersInfoCell3, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        $$Lambda$ChatEditActivity$II9hWQMBN5UIYcnhPQ2pczjBaE0 r8 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, r8, "avatar_text"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getThemeDescriptions$27 */
    public /* synthetic */ void lambda$getThemeDescriptions$27$ChatEditActivity() {
        BackupImageView backupImageView = this.avatarImage;
        if (backupImageView != null) {
            backupImageView.invalidate();
        }
    }
}
