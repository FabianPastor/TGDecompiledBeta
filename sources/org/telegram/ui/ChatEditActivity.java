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
import org.telegram.messenger.UserConfig;
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
import org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_inputChatPhoto;
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

    static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public ChatEditActivity(Bundle bundle) {
        super(bundle);
        this.chatId = bundle.getInt("chat_id", 0);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0048, code lost:
        if (r0 == null) goto L_0x004a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onFragmentCreate() {
        /*
            r6 = this;
            int r0 = r6.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r1 = r6.chatId
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            r6.currentChat = r0
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x004b
            int r0 = r6.currentAccount
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)
            int r3 = r6.chatId
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChatSync(r3)
            r6.currentChat = r0
            if (r0 == 0) goto L_0x004a
            int r0 = r6.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.tgnet.TLRPC$Chat r3 = r6.currentChat
            r0.putChat(r3, r1)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r6.info
            if (r0 != 0) goto L_0x004b
            int r0 = r6.currentAccount
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)
            int r3 = r6.chatId
            java.util.concurrent.CountDownLatch r4 = new java.util.concurrent.CountDownLatch
            r4.<init>(r1)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r0.loadChatInfo(r3, r4, r2, r2)
            r6.info = r0
            if (r0 != 0) goto L_0x004b
        L_0x004a:
            return r2
        L_0x004b:
            org.telegram.ui.Components.AvatarDrawable r0 = r6.avatarDrawable
            r3 = 5
            org.telegram.tgnet.TLRPC$Chat r4 = r6.currentChat
            java.lang.String r4 = r4.title
            r5 = 0
            r0.setInfo(r3, r4, r5)
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x0065
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0065
            goto L_0x0066
        L_0x0065:
            r1 = 0
        L_0x0066:
            r6.isChannel = r1
            org.telegram.ui.Components.ImageUpdater r0 = r6.imageUpdater
            r0.parentFragment = r6
            r0.setDelegate(r6)
            org.telegram.tgnet.TLRPC$Chat r0 = r6.currentChat
            boolean r0 = r0.signatures
            r6.signMessages = r0
            int r0 = r6.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad
            r0.addObserver(r6, r1)
            int r0 = r6.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.updateInterfaces
            r0.addObserver(r6, r1)
            boolean r0 = super.onFragmentCreate()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.onFragmentCreate():boolean");
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
        this.imageUpdater.onPause();
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
        r2.setOnTouchListener($$Lambda$ChatEditActivity$P4nSuGWkKCrAaBVoDH4QiQur0s.INSTANCE);
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
        if (ChatObject.canChangeChatInfo(this.currentChat)) {
            frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 8.0f));
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
            frameLayout.addView(r15, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 8.0f));
            RadialProgressView radialProgressView = new RadialProgressView(context2);
            this.avatarProgressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(30.0f));
            this.avatarProgressView.setProgressColor(-1);
            this.avatarProgressView.setNoProgress(false);
            frameLayout.addView(this.avatarProgressView, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 8.0f));
            showAvatarProgress(false, false);
            this.avatarContainer.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$1$ChatEditActivity(view);
                }
            });
        } else {
            frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
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
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 16, LocaleController.isRTL ? 5.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 5.0f, 0.0f));
        LinearLayout linearLayout3 = new LinearLayout(context2);
        this.settingsContainer = linearLayout3;
        linearLayout3.setOrientation(1);
        this.settingsContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout.addView(this.settingsContainer, LayoutHelper.createLinear(-1, -2));
        if (ChatObject.canChangeChatInfo(this.currentChat)) {
            AnonymousClass7 r22 = new TextCell(this, context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
                }
            };
            this.setAvatarCell = r22;
            r22.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.setAvatarCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
            this.setAvatarCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$3$ChatEditActivity(view);
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
                return ChatEditActivity.this.lambda$createView$4$ChatEditActivity(textView, i, keyEvent);
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
                    ChatEditActivity.this.lambda$createView$6$ChatEditActivity(view);
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
                    ChatEditActivity.this.lambda$createView$7$ChatEditActivity(view);
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
                    ChatEditActivity.this.lambda$createView$8$ChatEditActivity(view);
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
                    ChatEditActivity.this.lambda$createView$10$ChatEditActivity(this.f$1, view);
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
                    ChatEditActivity.this.lambda$createView$11$ChatEditActivity(view);
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
                ChatEditActivity.this.lambda$createView$12$ChatEditActivity(view);
            }
        });
        TextCell textCell2 = new TextCell(context2);
        this.adminCell = textCell2;
        textCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.adminCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditActivity.this.lambda$createView$13$ChatEditActivity(view);
            }
        });
        TextCell textCell3 = new TextCell(context2);
        this.membersCell = textCell3;
        textCell3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.membersCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditActivity.this.lambda$createView$14$ChatEditActivity(view);
            }
        });
        if (ChatObject.isChannel(this.currentChat)) {
            TextCell textCell4 = new TextCell(context2);
            this.logCell = textCell4;
            textCell4.setTextAndIcon(LocaleController.getString("EventLog", NUM), NUM, false);
            this.logCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.logCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$15$ChatEditActivity(view);
                }
            });
        }
        if (!this.isChannel) {
            this.infoContainer.addView(this.blockCell, LayoutHelper.createLinear(-1, -2));
        }
        this.infoContainer.addView(this.adminCell, LayoutHelper.createLinear(-1, -2));
        this.infoContainer.addView(this.membersCell, LayoutHelper.createLinear(-1, -2));
        if (this.isChannel) {
            this.infoContainer.addView(this.blockCell, LayoutHelper.createLinear(-1, -2));
        }
        TextCell textCell5 = this.logCell;
        if (textCell5 != null) {
            this.infoContainer.addView(textCell5, LayoutHelper.createLinear(-1, -2));
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
                    ChatEditActivity.this.lambda$createView$16$ChatEditActivity(view);
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
            } else if (this.currentChat.megagroup) {
                this.deleteCell.setText(LocaleController.getString("DeleteMega", NUM), false);
            } else {
                this.deleteCell.setText(LocaleController.getString("DeleteAndExitButton", NUM), false);
            }
            this.deleteContainer.addView(this.deleteCell, LayoutHelper.createFrame(-1, -2.0f));
            this.deleteCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$18$ChatEditActivity(view);
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
        this.nameTextView.setText(this.currentChat.title);
        EditTextEmoji editTextEmoji4 = this.nameTextView;
        editTextEmoji4.setSelection(editTextEmoji4.length());
        TLRPC$ChatFull tLRPC$ChatFull4 = this.info;
        if (tLRPC$ChatFull4 != null) {
            this.descriptionTextView.setText(tLRPC$ChatFull4.about);
        }
        setAvatar();
        updateFields(true);
        return this.fragmentView;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0009, code lost:
        r4 = getMessagesController().getChat(java.lang.Integer.valueOf(r3.chatId));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$1$ChatEditActivity(android.view.View r4) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.lambda$createView$1$ChatEditActivity(android.view.View):void");
    }

    public /* synthetic */ void lambda$createView$3$ChatEditActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new Runnable() {
            public final void run() {
                ChatEditActivity.this.lambda$null$2$ChatEditActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$2$ChatEditActivity() {
        this.avatar = null;
        MessagesController.getInstance(this.currentAccount).changeChatAvatar(this.chatId, (TLRPC$TL_inputChatPhoto) null, (TLRPC$InputFile) null, (TLRPC$InputFile) null, 0.0d, (String) null, (TLRPC$FileLocation) null, (TLRPC$FileLocation) null);
        showAvatarProgress(false, true);
        this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) this.currentChat);
    }

    public /* synthetic */ boolean lambda$createView$4$ChatEditActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    public /* synthetic */ void lambda$createView$6$ChatEditActivity(View view) {
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
                    ChatEditActivity.this.lambda$null$5$ChatEditActivity(tLRPC$MessageMedia, i, z, i2);
                }
            });
            presentFragment(locationActivity);
        }
    }

    public /* synthetic */ void lambda$null$5$ChatEditActivity(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
        TLRPC$TL_channelLocation tLRPC$TL_channelLocation = new TLRPC$TL_channelLocation();
        tLRPC$TL_channelLocation.address = tLRPC$MessageMedia.address;
        tLRPC$TL_channelLocation.geo_point = tLRPC$MessageMedia.geo;
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        tLRPC$ChatFull.location = tLRPC$TL_channelLocation;
        tLRPC$ChatFull.flags |= 32768;
        updateFields(false);
        getMessagesController().loadFullChat(this.chatId, 0, true);
    }

    public /* synthetic */ void lambda$createView$7$ChatEditActivity(View view) {
        int i = this.chatId;
        TextDetailCell textDetailCell = this.locationCell;
        ChatEditTypeActivity chatEditTypeActivity = new ChatEditTypeActivity(i, textDetailCell != null && textDetailCell.getVisibility() == 0);
        chatEditTypeActivity.setInfo(this.info);
        presentFragment(chatEditTypeActivity);
    }

    public /* synthetic */ void lambda$createView$8$ChatEditActivity(View view) {
        ChatLinkActivity chatLinkActivity = new ChatLinkActivity(this.chatId);
        chatLinkActivity.setInfo(this.info);
        presentFragment(chatLinkActivity);
    }

    public /* synthetic */ void lambda$createView$10$ChatEditActivity(Context context, View view) {
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
                    ChatEditActivity.this.lambda$null$9$ChatEditActivity(this.f$1, this.f$2, view);
                }
            });
        }
        builder.setCustomView(linearLayout);
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$null$9$ChatEditActivity(RadioButtonCell[] radioButtonCellArr, BottomSheet.Builder builder, View view) {
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

    public /* synthetic */ void lambda$createView$11$ChatEditActivity(View view) {
        boolean z = !this.signMessages;
        this.signMessages = z;
        ((TextCheckCell) view).setChecked(z);
    }

    public /* synthetic */ void lambda$createView$12$ChatEditActivity(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("chat_id", this.chatId);
        bundle.putInt("type", !this.isChannel ? 3 : 0);
        ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle);
        chatUsersActivity.setInfo(this.info);
        presentFragment(chatUsersActivity);
    }

    public /* synthetic */ void lambda$createView$13$ChatEditActivity(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("chat_id", this.chatId);
        bundle.putInt("type", 1);
        ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle);
        chatUsersActivity.setInfo(this.info);
        presentFragment(chatUsersActivity);
    }

    public /* synthetic */ void lambda$createView$14$ChatEditActivity(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("chat_id", this.chatId);
        bundle.putInt("type", 2);
        ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle);
        chatUsersActivity.setInfo(this.info);
        presentFragment(chatUsersActivity);
    }

    public /* synthetic */ void lambda$createView$15$ChatEditActivity(View view) {
        presentFragment(new ChannelAdminLogActivity(this.currentChat));
    }

    public /* synthetic */ void lambda$createView$16$ChatEditActivity(View view) {
        GroupStickersActivity groupStickersActivity = new GroupStickersActivity(this.currentChat.id);
        groupStickersActivity.setInfo(this.info);
        presentFragment(groupStickersActivity);
    }

    public /* synthetic */ void lambda$createView$18$ChatEditActivity(View view) {
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, true, false, this.currentChat, (TLRPC$User) null, false, new MessagesStorage.BooleanCallback() {
            public final void run(boolean z) {
                ChatEditActivity.this.lambda$null$17$ChatEditActivity(z);
            }
        });
    }

    public /* synthetic */ void lambda$null$17$ChatEditActivity(boolean z) {
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(-((long) this.chatId)));
        } else {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chatId, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId())), this.info, true, false);
        finishFragment();
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
                this.info = tLRPC$ChatFull;
                this.historyHidden = !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory;
                updateFields(false);
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
                ChatEditActivity.this.lambda$didUploadPhoto$19$ChatEditActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    public /* synthetic */ void lambda$didUploadPhoto$19$ChatEditActivity(TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, String str, TLRPC$PhotoSize tLRPC$PhotoSize2) {
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
                if (this.progressDialog != null && this.progressDialog.isShowing()) {
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

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        String str;
        EditTextEmoji editTextEmoji;
        EditTextBoldCursor editTextBoldCursor;
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        if (tLRPC$ChatFull == null || (str = tLRPC$ChatFull.about) == null) {
            str = "";
        }
        if ((this.info == null || !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory == this.historyHidden) && (((editTextEmoji = this.nameTextView) == null || this.currentChat.title.equals(editTextEmoji.getText().toString())) && (((editTextBoldCursor = this.descriptionTextView) == null || str.equals(editTextBoldCursor.getText().toString())) && this.signMessages == this.currentChat.signatures))) {
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
                ChatEditActivity.this.lambda$checkDiscard$20$ChatEditActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatEditActivity.this.lambda$checkDiscard$21$ChatEditActivity(dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    public /* synthetic */ void lambda$checkDiscard$20$ChatEditActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    public /* synthetic */ void lambda$checkDiscard$21$ChatEditActivity(DialogInterface dialogInterface, int i) {
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
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x005c, code lost:
        r1 = r5.info;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processDone() {
        /*
            r5 = this;
            boolean r0 = r5.donePressed
            if (r0 != 0) goto L_0x010e
            org.telegram.ui.Components.EditTextEmoji r0 = r5.nameTextView
            if (r0 != 0) goto L_0x000a
            goto L_0x010e
        L_0x000a:
            int r0 = r0.length()
            if (r0 != 0) goto L_0x002c
            android.app.Activity r0 = r5.getParentActivity()
            java.lang.String r1 = "vibrator"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.os.Vibrator r0 = (android.os.Vibrator) r0
            if (r0 == 0) goto L_0x0023
            r1 = 200(0xc8, double:9.9E-322)
            r0.vibrate(r1)
        L_0x0023:
            org.telegram.ui.Components.EditTextEmoji r0 = r5.nameTextView
            r1 = 1073741824(0x40000000, float:2.0)
            r2 = 0
            org.telegram.messenger.AndroidUtilities.shakeView(r0, r1, r2)
            return
        L_0x002c:
            r0 = 1
            r5.donePressed = r0
            org.telegram.tgnet.TLRPC$Chat r1 = r5.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 != 0) goto L_0x0050
            boolean r1 = r5.historyHidden
            if (r1 != 0) goto L_0x0050
            int r0 = r5.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            android.app.Activity r1 = r5.getParentActivity()
            int r2 = r5.chatId
            org.telegram.ui.-$$Lambda$ChatEditActivity$eeF5vkdcRE_KG2_N_FcMjezkb-Y r3 = new org.telegram.ui.-$$Lambda$ChatEditActivity$eeF5vkdcRE_KG2_N_FcMjezkb-Y
            r3.<init>()
            r0.convertToMegaGroup(r1, r2, r5, r3)
            return
        L_0x0050:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r5.info
            if (r1 == 0) goto L_0x0073
            org.telegram.tgnet.TLRPC$Chat r1 = r5.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 == 0) goto L_0x0073
            org.telegram.tgnet.TLRPC$ChatFull r1 = r5.info
            boolean r2 = r1.hidden_prehistory
            boolean r3 = r5.historyHidden
            if (r2 == r3) goto L_0x0073
            r1.hidden_prehistory = r3
            int r1 = r5.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = r5.chatId
            boolean r3 = r5.historyHidden
            r1.toogleChannelInvitesHistory(r2, r3)
        L_0x0073:
            org.telegram.ui.Components.ImageUpdater r1 = r5.imageUpdater
            boolean r1 = r1.isUploadingImage()
            if (r1 == 0) goto L_0x0097
            r5.createAfterUpload = r0
            org.telegram.ui.ActionBar.AlertDialog r0 = new org.telegram.ui.ActionBar.AlertDialog
            android.app.Activity r1 = r5.getParentActivity()
            r2 = 3
            r0.<init>(r1, r2)
            r5.progressDialog = r0
            org.telegram.ui.-$$Lambda$ChatEditActivity$YQP7Ob5bdqCts_QOtznHKjwiyoI r1 = new org.telegram.ui.-$$Lambda$ChatEditActivity$YQP7Ob5bdqCts_QOtznHKjwiyoI
            r1.<init>()
            r0.setOnCancelListener(r1)
            org.telegram.ui.ActionBar.AlertDialog r0 = r5.progressDialog
            r0.show()
            return
        L_0x0097:
            org.telegram.tgnet.TLRPC$Chat r1 = r5.currentChat
            java.lang.String r1 = r1.title
            org.telegram.ui.Components.EditTextEmoji r2 = r5.nameTextView
            android.text.Editable r2 = r2.getText()
            java.lang.String r2 = r2.toString()
            boolean r1 = r1.equals(r2)
            if (r1 != 0) goto L_0x00c0
            int r1 = r5.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = r5.chatId
            org.telegram.ui.Components.EditTextEmoji r3 = r5.nameTextView
            android.text.Editable r3 = r3.getText()
            java.lang.String r3 = r3.toString()
            r1.changeChatTitle(r2, r3)
        L_0x00c0:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r5.info
            if (r1 == 0) goto L_0x00c9
            java.lang.String r1 = r1.about
            if (r1 == 0) goto L_0x00c9
            goto L_0x00cb
        L_0x00c9:
            java.lang.String r1 = ""
        L_0x00cb:
            org.telegram.ui.Components.EditTextBoldCursor r2 = r5.descriptionTextView
            if (r2 == 0) goto L_0x00f4
            android.text.Editable r2 = r2.getText()
            java.lang.String r2 = r2.toString()
            boolean r1 = r1.equals(r2)
            if (r1 != 0) goto L_0x00f4
            int r1 = r5.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = r5.chatId
            org.telegram.ui.Components.EditTextBoldCursor r3 = r5.descriptionTextView
            android.text.Editable r3 = r3.getText()
            java.lang.String r3 = r3.toString()
            org.telegram.tgnet.TLRPC$ChatFull r4 = r5.info
            r1.updateChatAbout(r2, r3, r4)
        L_0x00f4:
            boolean r1 = r5.signMessages
            org.telegram.tgnet.TLRPC$Chat r2 = r5.currentChat
            boolean r3 = r2.signatures
            if (r1 == r3) goto L_0x010b
            r2.signatures = r0
            int r0 = r5.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r1 = r5.chatId
            boolean r2 = r5.signMessages
            r0.toogleChannelSignatures(r1, r2)
        L_0x010b:
            r5.finishFragment()
        L_0x010e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.processDone():void");
    }

    public /* synthetic */ void lambda$processDone$22$ChatEditActivity(int i) {
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

    public /* synthetic */ void lambda$processDone$23$ChatEditActivity(DialogInterface dialogInterface) {
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
        String obj;
        String str;
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (!(imageUpdater2 == null || (str = imageUpdater2.currentPicturePath) == null)) {
            bundle.putString("path", str);
        }
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null && (obj = editTextEmoji.getText().toString()) != null && obj.length() != 0) {
            bundle.putString("nameTextView", obj);
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

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0039, code lost:
        r5 = r0.info;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateFields(boolean r17) {
        /*
            r16 = this;
            r0 = r16
            if (r17 == 0) goto L_0x0018
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = r0.chatId
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            if (r1 == 0) goto L_0x0018
            r0.currentChat = r1
        L_0x0018:
            org.telegram.tgnet.TLRPC$Chat r1 = r0.currentChat
            java.lang.String r1 = r1.username
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            org.telegram.ui.Cells.TextDetailCell r2 = r0.historyCell
            r3 = 8
            r4 = 0
            if (r2 == 0) goto L_0x0048
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.info
            if (r5 == 0) goto L_0x0035
            org.telegram.tgnet.TLRPC$ChannelLocation r5 = r5.location
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
            if (r5 == 0) goto L_0x0035
            r2.setVisibility(r3)
            goto L_0x0048
        L_0x0035:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.historyCell
            if (r1 == 0) goto L_0x0043
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.info
            if (r5 == 0) goto L_0x0041
            int r5 = r5.linked_chat_id
            if (r5 != 0) goto L_0x0043
        L_0x0041:
            r5 = 0
            goto L_0x0045
        L_0x0043:
            r5 = 8
        L_0x0045:
            r2.setVisibility(r5)
        L_0x0048:
            org.telegram.ui.Cells.ShadowSectionCell r2 = r0.settingsSectionCell
            if (r2 == 0) goto L_0x0079
            org.telegram.ui.Cells.TextCheckCell r5 = r0.signCell
            if (r5 != 0) goto L_0x0075
            org.telegram.ui.Cells.TextDetailCell r5 = r0.typeCell
            if (r5 != 0) goto L_0x0075
            org.telegram.ui.Cells.TextDetailCell r5 = r0.linkedCell
            if (r5 == 0) goto L_0x005e
            int r5 = r5.getVisibility()
            if (r5 == 0) goto L_0x0075
        L_0x005e:
            org.telegram.ui.Cells.TextDetailCell r5 = r0.historyCell
            if (r5 == 0) goto L_0x0068
            int r5 = r5.getVisibility()
            if (r5 == 0) goto L_0x0075
        L_0x0068:
            org.telegram.ui.Cells.TextDetailCell r5 = r0.locationCell
            if (r5 == 0) goto L_0x0072
            int r5 = r5.getVisibility()
            if (r5 == 0) goto L_0x0075
        L_0x0072:
            r5 = 8
            goto L_0x0076
        L_0x0075:
            r5 = 0
        L_0x0076:
            r2.setVisibility(r5)
        L_0x0079:
            org.telegram.ui.Cells.TextCell r2 = r0.logCell
            if (r2 == 0) goto L_0x0095
            org.telegram.tgnet.TLRPC$Chat r5 = r0.currentChat
            boolean r5 = r5.megagroup
            if (r5 == 0) goto L_0x0091
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.info
            if (r5 == 0) goto L_0x008e
            int r5 = r5.participants_count
            r6 = 200(0xc8, float:2.8E-43)
            if (r5 <= r6) goto L_0x008e
            goto L_0x0091
        L_0x008e:
            r5 = 8
            goto L_0x0092
        L_0x0091:
            r5 = 0
        L_0x0092:
            r2.setVisibility(r5)
        L_0x0095:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.linkedCell
            r5 = 1
            if (r2 == 0) goto L_0x0152
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.info
            if (r2 == 0) goto L_0x014d
            boolean r6 = r0.isChannel
            if (r6 != 0) goto L_0x00a8
            int r2 = r2.linked_chat_id
            if (r2 != 0) goto L_0x00a8
            goto L_0x014d
        L_0x00a8:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.linkedCell
            r2.setVisibility(r4)
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.info
            int r2 = r2.linked_chat_id
            r6 = 2131625070(0x7f0e046e, float:1.8877338E38)
            java.lang.String r7 = "Discussion"
            if (r2 != 0) goto L_0x00cc
            org.telegram.ui.Cells.TextDetailCell r2 = r0.linkedCell
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r7 = 2131625077(0x7f0e0475, float:1.8877352E38)
            java.lang.String r8 = "DiscussionInfo"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r2.setTextAndValue(r6, r7, r5)
            goto L_0x0152
        L_0x00cc:
            org.telegram.messenger.MessagesController r2 = r16.getMessagesController()
            org.telegram.tgnet.TLRPC$ChatFull r8 = r0.info
            int r8 = r8.linked_chat_id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r8)
            if (r2 != 0) goto L_0x00e4
            org.telegram.ui.Cells.TextDetailCell r2 = r0.linkedCell
            r2.setVisibility(r3)
            goto L_0x0152
        L_0x00e4:
            boolean r8 = r0.isChannel
            java.lang.String r9 = "@"
            if (r8 == 0) goto L_0x0119
            java.lang.String r8 = r2.username
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x00fe
            org.telegram.ui.Cells.TextDetailCell r8 = r0.linkedCell
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.String r2 = r2.title
            r8.setTextAndValue(r6, r2, r5)
            goto L_0x0152
        L_0x00fe:
            org.telegram.ui.Cells.TextDetailCell r8 = r0.linkedCell
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r9)
            java.lang.String r2 = r2.username
            r7.append(r2)
            java.lang.String r2 = r7.toString()
            r8.setTextAndValue(r6, r2, r5)
            goto L_0x0152
        L_0x0119:
            java.lang.String r6 = r2.username
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            r7 = 2131625720(0x7f0e06f8, float:1.8878656E38)
            java.lang.String r8 = "LinkedChannel"
            if (r6 == 0) goto L_0x0132
            org.telegram.ui.Cells.TextDetailCell r6 = r0.linkedCell
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.String r2 = r2.title
            r6.setTextAndValue(r7, r2, r4)
            goto L_0x0152
        L_0x0132:
            org.telegram.ui.Cells.TextDetailCell r6 = r0.linkedCell
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r9)
            java.lang.String r2 = r2.username
            r8.append(r2)
            java.lang.String r2 = r8.toString()
            r6.setTextAndValue(r7, r2, r4)
            goto L_0x0152
        L_0x014d:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.linkedCell
            r2.setVisibility(r3)
        L_0x0152:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.locationCell
            if (r2 == 0) goto L_0x018d
            org.telegram.tgnet.TLRPC$ChatFull r6 = r0.info
            if (r6 == 0) goto L_0x0188
            boolean r6 = r6.can_set_location
            if (r6 == 0) goto L_0x0188
            r2.setVisibility(r4)
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.info
            org.telegram.tgnet.TLRPC$ChannelLocation r2 = r2.location
            boolean r6 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
            r7 = 2131624347(0x7f0e019b, float:1.8875871E38)
            java.lang.String r8 = "AttachLocation"
            if (r6 == 0) goto L_0x017c
            org.telegram.tgnet.TLRPC$TL_channelLocation r2 = (org.telegram.tgnet.TLRPC$TL_channelLocation) r2
            org.telegram.ui.Cells.TextDetailCell r6 = r0.locationCell
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.String r2 = r2.address
            r6.setTextAndValue(r7, r2, r5)
            goto L_0x018d
        L_0x017c:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.locationCell
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.String r7 = "Unknown address"
            r2.setTextAndValue(r6, r7, r5)
            goto L_0x018d
        L_0x0188:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.locationCell
            r2.setVisibility(r3)
        L_0x018d:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.typeCell
            if (r2 == 0) goto L_0x026e
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.info
            if (r2 == 0) goto L_0x01f8
            org.telegram.tgnet.TLRPC$ChannelLocation r2 = r2.location
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
            if (r2 == 0) goto L_0x01f8
            if (r1 == 0) goto L_0x01a7
            r1 = 2131627312(0x7f0e0d30, float:1.8881885E38)
            java.lang.String r2 = "TypeLocationGroupEdit"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01d1
        L_0x01a7:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "https://"
            r1.append(r2)
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.String r2 = r2.linkPrefix
            r1.append(r2)
            java.lang.String r2 = "/%s"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.Object[] r2 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Chat r6 = r0.currentChat
            java.lang.String r6 = r6.username
            r2[r4] = r6
            java.lang.String r1 = java.lang.String.format(r1, r2)
        L_0x01d1:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.typeCell
            r6 = 2131627311(0x7f0e0d2f, float:1.8881883E38)
            java.lang.String r7 = "TypeLocationGroup"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.Cells.TextDetailCell r7 = r0.historyCell
            if (r7 == 0) goto L_0x01e6
            int r7 = r7.getVisibility()
            if (r7 == 0) goto L_0x01f0
        L_0x01e6:
            org.telegram.ui.Cells.TextDetailCell r7 = r0.linkedCell
            if (r7 == 0) goto L_0x01f2
            int r7 = r7.getVisibility()
            if (r7 != 0) goto L_0x01f2
        L_0x01f0:
            r7 = 1
            goto L_0x01f3
        L_0x01f2:
            r7 = 0
        L_0x01f3:
            r2.setTextAndValue(r6, r1, r7)
            goto L_0x026e
        L_0x01f8:
            boolean r2 = r0.isChannel
            if (r2 == 0) goto L_0x020e
            if (r1 == 0) goto L_0x0204
            r1 = 2131627314(0x7f0e0d32, float:1.8881889E38)
            java.lang.String r2 = "TypePrivate"
            goto L_0x0209
        L_0x0204:
            r1 = 2131627316(0x7f0e0d34, float:1.8881893E38)
            java.lang.String r2 = "TypePublic"
        L_0x0209:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x021f
        L_0x020e:
            if (r1 == 0) goto L_0x0216
            r1 = 2131627315(0x7f0e0d33, float:1.888189E38)
            java.lang.String r2 = "TypePrivateGroup"
            goto L_0x021b
        L_0x0216:
            r1 = 2131627317(0x7f0e0d35, float:1.8881895E38)
            java.lang.String r2 = "TypePublicGroup"
        L_0x021b:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x021f:
            boolean r2 = r0.isChannel
            if (r2 == 0) goto L_0x0249
            org.telegram.ui.Cells.TextDetailCell r2 = r0.typeCell
            r6 = 2131624688(0x7f0e02f0, float:1.8876563E38)
            java.lang.String r7 = "ChannelType"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.Cells.TextDetailCell r7 = r0.historyCell
            if (r7 == 0) goto L_0x0238
            int r7 = r7.getVisibility()
            if (r7 == 0) goto L_0x0242
        L_0x0238:
            org.telegram.ui.Cells.TextDetailCell r7 = r0.linkedCell
            if (r7 == 0) goto L_0x0244
            int r7 = r7.getVisibility()
            if (r7 != 0) goto L_0x0244
        L_0x0242:
            r7 = 1
            goto L_0x0245
        L_0x0244:
            r7 = 0
        L_0x0245:
            r2.setTextAndValue(r6, r1, r7)
            goto L_0x026e
        L_0x0249:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.typeCell
            r6 = 2131625545(0x7f0e0649, float:1.88783E38)
            java.lang.String r7 = "GroupType"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.Cells.TextDetailCell r7 = r0.historyCell
            if (r7 == 0) goto L_0x025e
            int r7 = r7.getVisibility()
            if (r7 == 0) goto L_0x0268
        L_0x025e:
            org.telegram.ui.Cells.TextDetailCell r7 = r0.linkedCell
            if (r7 == 0) goto L_0x026a
            int r7 = r7.getVisibility()
            if (r7 != 0) goto L_0x026a
        L_0x0268:
            r7 = 1
            goto L_0x026b
        L_0x026a:
            r7 = 0
        L_0x026b:
            r2.setTextAndValue(r6, r1, r7)
        L_0x026e:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.info
            if (r1 == 0) goto L_0x0297
            org.telegram.ui.Cells.TextDetailCell r1 = r0.historyCell
            if (r1 == 0) goto L_0x0297
            boolean r1 = r0.historyHidden
            if (r1 == 0) goto L_0x0280
            r1 = 2131624727(0x7f0e0317, float:1.8876642E38)
            java.lang.String r2 = "ChatHistoryHidden"
            goto L_0x0285
        L_0x0280:
            r1 = 2131624730(0x7f0e031a, float:1.8876648E38)
            java.lang.String r2 = "ChatHistoryVisible"
        L_0x0285:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.Cells.TextDetailCell r2 = r0.historyCell
            r6 = 2131624726(0x7f0e0316, float:1.887664E38)
            java.lang.String r7 = "ChatHistory"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r2.setTextAndValue(r6, r1, r4)
        L_0x0297:
            org.telegram.ui.Cells.TextSettingsCell r1 = r0.stickersCell
            r2 = 2131625543(0x7f0e0647, float:1.8878297E38)
            java.lang.String r6 = "GroupStickers"
            if (r1 == 0) goto L_0x02bb
            org.telegram.tgnet.TLRPC$ChatFull r7 = r0.info
            org.telegram.tgnet.TLRPC$StickerSet r7 = r7.stickerset
            if (r7 == 0) goto L_0x02b4
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r6, r2)
            org.telegram.tgnet.TLRPC$ChatFull r8 = r0.info
            org.telegram.tgnet.TLRPC$StickerSet r8 = r8.stickerset
            java.lang.String r8 = r8.title
            r1.setTextAndValue(r7, r8, r4)
            goto L_0x02bb
        L_0x02b4:
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r1.setText(r7, r4)
        L_0x02bb:
            org.telegram.ui.Cells.TextCell r1 = r0.membersCell
            if (r1 == 0) goto L_0x0481
            org.telegram.tgnet.TLRPC$ChatFull r7 = r0.info
            java.lang.String r8 = "ChannelPermissions"
            r9 = 2131165254(0x7var_, float:1.794472E38)
            r10 = 2131624607(0x7f0e029f, float:1.8876398E38)
            java.lang.String r11 = "ChannelBlacklist"
            r12 = 2131624684(0x7f0e02ec, float:1.8876555E38)
            java.lang.String r13 = "ChannelSubscribers"
            r14 = 2131624638(0x7f0e02be, float:1.8876461E38)
            java.lang.String r15 = "ChannelMembers"
            r2 = 2131165256(0x7var_, float:1.7944724E38)
            if (r7 == 0) goto L_0x042b
            boolean r7 = r0.isChannel
            java.lang.String r3 = "%d"
            if (r7 == 0) goto L_0x0325
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r13, r12)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$ChatFull r12 = r0.info
            int r12 = r12.participants_count
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r8[r4] = r12
            java.lang.String r8 = java.lang.String.format(r3, r8)
            r1.setTextAndValueAndIcon(r7, r8, r2, r5)
            org.telegram.ui.Cells.TextCell r1 = r0.blockCell
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r11, r10)
            java.lang.Object[] r7 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$ChatFull r8 = r0.info
            int r10 = r8.banned_count
            int r8 = r8.kicked_count
            int r8 = java.lang.Math.max(r10, r8)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r4] = r8
            java.lang.String r7 = java.lang.String.format(r3, r7)
            org.telegram.ui.Cells.TextCell r8 = r0.logCell
            if (r8 == 0) goto L_0x031f
            int r8 = r8.getVisibility()
            if (r8 != 0) goto L_0x031f
            r8 = 1
            goto L_0x0320
        L_0x031f:
            r8 = 0
        L_0x0320:
            r1.setTextAndValueAndIcon(r2, r7, r9, r8)
            goto L_0x03fc
        L_0x0325:
            org.telegram.tgnet.TLRPC$Chat r1 = r0.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 == 0) goto L_0x0354
            org.telegram.ui.Cells.TextCell r1 = r0.membersCell
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r15, r14)
            java.lang.Object[] r9 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$ChatFull r10 = r0.info
            int r10 = r10.participants_count
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r9[r4] = r10
            java.lang.String r9 = java.lang.String.format(r3, r9)
            org.telegram.ui.Cells.TextCell r10 = r0.logCell
            if (r10 == 0) goto L_0x034f
            int r10 = r10.getVisibility()
            if (r10 != 0) goto L_0x034f
            r10 = 1
            goto L_0x0350
        L_0x034f:
            r10 = 0
        L_0x0350:
            r1.setTextAndValueAndIcon(r7, r9, r2, r10)
            goto L_0x0380
        L_0x0354:
            org.telegram.ui.Cells.TextCell r1 = r0.membersCell
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r15, r14)
            java.lang.Object[] r9 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$ChatFull r10 = r0.info
            org.telegram.tgnet.TLRPC$ChatParticipants r10 = r10.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r10 = r10.participants
            int r10 = r10.size()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r9[r4] = r10
            java.lang.String r9 = java.lang.String.format(r3, r9)
            org.telegram.ui.Cells.TextCell r10 = r0.logCell
            if (r10 == 0) goto L_0x037c
            int r10 = r10.getVisibility()
            if (r10 != 0) goto L_0x037c
            r10 = 1
            goto L_0x037d
        L_0x037c:
            r10 = 0
        L_0x037d:
            r1.setTextAndValueAndIcon(r7, r9, r2, r10)
        L_0x0380:
            org.telegram.tgnet.TLRPC$Chat r1 = r0.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r1.default_banned_rights
            if (r1 == 0) goto L_0x03d4
            boolean r1 = r1.send_stickers
            if (r1 != 0) goto L_0x038c
            r1 = 1
            goto L_0x038d
        L_0x038c:
            r1 = 0
        L_0x038d:
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = r2.default_banned_rights
            boolean r2 = r2.send_media
            if (r2 != 0) goto L_0x0397
            int r1 = r1 + 1
        L_0x0397:
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = r2.default_banned_rights
            boolean r2 = r2.embed_links
            if (r2 != 0) goto L_0x03a1
            int r1 = r1 + 1
        L_0x03a1:
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = r2.default_banned_rights
            boolean r2 = r2.send_messages
            if (r2 != 0) goto L_0x03ab
            int r1 = r1 + 1
        L_0x03ab:
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = r2.default_banned_rights
            boolean r2 = r2.pin_messages
            if (r2 != 0) goto L_0x03b5
            int r1 = r1 + 1
        L_0x03b5:
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = r2.default_banned_rights
            boolean r2 = r2.send_polls
            if (r2 != 0) goto L_0x03bf
            int r1 = r1 + 1
        L_0x03bf:
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = r2.default_banned_rights
            boolean r2 = r2.invite_users
            if (r2 != 0) goto L_0x03c9
            int r1 = r1 + 1
        L_0x03c9:
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = r2.default_banned_rights
            boolean r2 = r2.change_info
            if (r2 != 0) goto L_0x03d6
            int r1 = r1 + 1
            goto L_0x03d6
        L_0x03d4:
            r1 = 8
        L_0x03d6:
            org.telegram.ui.Cells.TextCell r2 = r0.blockCell
            r7 = 2131624663(0x7f0e02d7, float:1.8876512E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r8[r4] = r1
            r1 = 8
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r8[r5] = r1
            java.lang.String r1 = "%d/%d"
            java.lang.String r1 = java.lang.String.format(r1, r8)
            r8 = 2131165252(0x7var_, float:1.7944716E38)
            r2.setTextAndValueAndIcon(r7, r1, r8, r5)
        L_0x03fc:
            org.telegram.ui.Cells.TextCell r1 = r0.adminCell
            r2 = 2131624602(0x7f0e029a, float:1.8876388E38)
            java.lang.String r7 = "ChannelAdministrators"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)
            java.lang.Object[] r7 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Chat r8 = r0.currentChat
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r8 == 0) goto L_0x0416
            org.telegram.tgnet.TLRPC$ChatFull r8 = r0.info
            int r8 = r8.admins_count
            goto L_0x041a
        L_0x0416:
            int r8 = r16.getAdminCount()
        L_0x041a:
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r4] = r8
            java.lang.String r3 = java.lang.String.format(r3, r7)
            r7 = 2131165247(0x7var_f, float:1.7944706E38)
            r1.setTextAndValueAndIcon(r2, r3, r7, r5)
            goto L_0x0481
        L_0x042b:
            boolean r3 = r0.isChannel
            if (r3 == 0) goto L_0x044d
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r1.setTextAndIcon((java.lang.String) r3, (int) r2, (boolean) r5)
            org.telegram.ui.Cells.TextCell r1 = r0.blockCell
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r11, r10)
            org.telegram.ui.Cells.TextCell r3 = r0.logCell
            if (r3 == 0) goto L_0x0448
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x0448
            r3 = 1
            goto L_0x0449
        L_0x0448:
            r3 = 0
        L_0x0449:
            r1.setTextAndIcon((java.lang.String) r2, (int) r9, (boolean) r3)
            goto L_0x0470
        L_0x044d:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r14)
            org.telegram.ui.Cells.TextCell r7 = r0.logCell
            if (r7 == 0) goto L_0x045d
            int r7 = r7.getVisibility()
            if (r7 != 0) goto L_0x045d
            r7 = 1
            goto L_0x045e
        L_0x045d:
            r7 = 0
        L_0x045e:
            r1.setTextAndIcon((java.lang.String) r3, (int) r2, (boolean) r7)
            org.telegram.ui.Cells.TextCell r1 = r0.blockCell
            r2 = 2131624663(0x7f0e02d7, float:1.8876512E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r8, r2)
            r3 = 2131165252(0x7var_, float:1.7944716E38)
            r1.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r5)
        L_0x0470:
            org.telegram.ui.Cells.TextCell r1 = r0.adminCell
            r2 = 2131624602(0x7f0e029a, float:1.8876388E38)
            java.lang.String r3 = "ChannelAdministrators"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 2131165247(0x7var_f, float:1.7944706E38)
            r1.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r5)
        L_0x0481:
            org.telegram.ui.Cells.TextSettingsCell r1 = r0.stickersCell
            if (r1 == 0) goto L_0x04a8
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.info
            if (r2 == 0) goto L_0x04a8
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.stickerset
            if (r2 == 0) goto L_0x049e
            r2 = 2131625543(0x7f0e0647, float:1.8878297E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            org.telegram.tgnet.TLRPC$ChatFull r3 = r0.info
            org.telegram.tgnet.TLRPC$StickerSet r3 = r3.stickerset
            java.lang.String r3 = r3.title
            r1.setTextAndValue(r2, r3, r4)
            goto L_0x04a8
        L_0x049e:
            r2 = 2131625543(0x7f0e0647, float:1.8878297E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r1.setText(r2, r4)
        L_0x04a8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.updateFields(boolean):void");
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$ChatEditActivity$fBJ7aa3OF9xcYC_U5j999UpsGw r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ChatEditActivity.this.lambda$getThemeDescriptions$24$ChatEditActivity();
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
        $$Lambda$ChatEditActivity$fBJ7aa3OF9xcYC_U5j999UpsGw r8 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, r8, "avatar_text"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
        return arrayList;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$24$ChatEditActivity() {
        BackupImageView backupImageView = this.avatarImage;
        if (backupImageView != null) {
            backupImageView.invalidate();
        }
    }
}
