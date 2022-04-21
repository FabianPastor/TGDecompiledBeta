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
import android.os.Vibrator;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.PhotoViewer;

public class ChatEditActivity extends BaseFragment implements ImageUpdater.ImageUpdaterDelegate, NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    private TextCell adminCell;
    private List<String> availableReactions = Collections.emptyList();
    private TLRPC.FileLocation avatar;
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
    private TLRPC.Chat currentChat;
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
    private TLRPC.ChatFull info;
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
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            if (fileLocation == null) {
                return null;
            }
            TLRPC.FileLocation photoBig = null;
            TLRPC.Chat chat = ChatEditActivity.this.getMessagesController().getChat(Long.valueOf(ChatEditActivity.this.chatId));
            if (!(chat == null || chat.photo == null || chat.photo.photo_big == null)) {
                photoBig = chat.photo.photo_big;
            }
            if (photoBig == null || photoBig.local_id != fileLocation.local_id || photoBig.volume_id != fileLocation.volume_id || photoBig.dc_id != fileLocation.dc_id) {
                return null;
            }
            int[] coords = new int[2];
            ChatEditActivity.this.avatarImage.getLocationInWindow(coords);
            PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
            int i = 0;
            object.viewX = coords[0];
            int i2 = coords[1];
            if (Build.VERSION.SDK_INT < 21) {
                i = AndroidUtilities.statusBarHeight;
            }
            object.viewY = i2 - i;
            object.parentView = ChatEditActivity.this.avatarImage;
            object.imageReceiver = ChatEditActivity.this.avatarImage.getImageReceiver();
            object.dialogId = -ChatEditActivity.this.chatId;
            object.thumb = object.imageReceiver.getBitmapSafe();
            object.size = -1;
            object.radius = ChatEditActivity.this.avatarImage.getImageReceiver().getRoundRadius();
            object.scale = ChatEditActivity.this.avatarContainer.getScaleX();
            object.canEdit = true;
            return object;
        }

        public void willHidePhotoViewer() {
            ChatEditActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
        }

        public void openPhotoForEdit(String file, String thumb, boolean isVideo) {
            ChatEditActivity.this.imageUpdater.openPhotoForEdit(file, thumb, 0, isVideo);
        }
    };
    private TextCell reactionsCell;
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

    public ChatEditActivity(Bundle args) {
        super(args);
        this.chatId = args.getLong("chat_id", 0);
    }

    public boolean onFragmentCreate() {
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
        this.currentChat = chat;
        boolean z = true;
        if (chat == null) {
            TLRPC.Chat chatSync = MessagesStorage.getInstance(this.currentAccount).getChatSync(this.chatId);
            this.currentChat = chatSync;
            if (chatSync == null) {
                return false;
            }
            getMessagesController().putChat(this.currentChat, true);
            if (this.info == null) {
                TLRPC.ChatFull loadChatInfo = MessagesStorage.getInstance(this.currentAccount).loadChatInfo(this.chatId, ChatObject.isChannel(this.currentChat), new CountDownLatch(1), false, false);
                this.info = loadChatInfo;
                if (loadChatInfo == null) {
                    return false;
                }
            }
        }
        this.avatarDrawable.setInfo(5, this.currentChat.title, (String) null);
        if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
            z = false;
        }
        this.isChannel = z;
        this.imageUpdater.parentFragment = this;
        this.imageUpdater.setDelegate(this);
        this.signMessages = this.currentChat.signatures;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatAvailableReactionsUpdated);
        if (this.info != null) {
            loadLinksCount();
        }
        return super.onFragmentCreate();
    }

    private void loadLinksCount() {
        TLRPC.TL_messages_getExportedChatInvites req = new TLRPC.TL_messages_getExportedChatInvites();
        req.peer = getMessagesController().getInputPeer(-this.chatId);
        req.admin_id = getMessagesController().getInputUser(getUserConfig().getCurrentUser());
        req.limit = 0;
        getConnectionsManager().sendRequest(req, new ChatEditActivity$$ExternalSyntheticLambda20(this));
    }

    /* renamed from: lambda$loadLinksCount$1$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1918lambda$loadLinksCount$1$orgtelegramuiChatEditActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatEditActivity$$ExternalSyntheticLambda17(this, error, response));
    }

    /* renamed from: lambda$loadLinksCount$0$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1917lambda$loadLinksCount$0$orgtelegramuiChatEditActivity(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            this.info.invitesCount = ((TLRPC.TL_messages_exportedChatInvites) response).count;
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

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        this.imageUpdater.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
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
        TLRPC.ChatFull chatFull;
        TLRPC.ChatFull chatFull2;
        TLRPC.ChatFull chatFull3;
        TLRPC.ChatFull chatFull4;
        Context context2 = context;
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (ChatEditActivity.this.checkDiscard()) {
                        ChatEditActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    ChatEditActivity.this.processDone();
                }
            }
        });
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                int heightSize2 = heightSize - getPaddingTop();
                measureChildWithMargins(ChatEditActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                if (measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                    this.ignoreLayout = true;
                    ChatEditActivity.this.nameTextView.hideEmojiView();
                    this.ignoreLayout = false;
                }
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == ChatEditActivity.this.actionBar)) {
                        if (ChatEditActivity.this.nameTextView == null || !ChatEditActivity.this.nameTextView.isPopupView(child)) {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (heightSize2 - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec((heightSize2 - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int childLeft;
                int childTop;
                int count = getChildCount();
                int keyboardSize = measureKeyboardHeight();
                int paddingBottom = (keyboardSize > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) ? 0 : ChatEditActivity.this.nameTextView.getEmojiPadding();
                setBottomClip(paddingBottom);
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        switch (gravity & 7 & 7) {
                            case 1:
                                childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                                break;
                            case 5:
                                childLeft = (r - width) - lp.rightMargin;
                                break;
                            default:
                                childLeft = lp.leftMargin;
                                break;
                        }
                        switch (verticalGravity) {
                            case 16:
                                childTop = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case 48:
                                childTop = lp.topMargin + getPaddingTop();
                                break;
                            case 80:
                                childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (ChatEditActivity.this.nameTextView != null && ChatEditActivity.this.nameTextView.isPopupView(child)) {
                            if (AndroidUtilities.isTablet()) {
                                childTop = getMeasuredHeight() - child.getMeasuredHeight();
                            } else {
                                childTop = (getMeasuredHeight() + keyboardSize) - child.getMeasuredHeight();
                            }
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                notifyHeightChanged();
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        sizeNotifierFrameLayout.setOnTouchListener(ChatEditActivity$$ExternalSyntheticLambda13.INSTANCE);
        this.fragmentView = sizeNotifierFrameLayout;
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        ScrollView scrollView = new ScrollView(context2);
        scrollView.setFillViewport(true);
        sizeNotifierFrameLayout.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout1 = new LinearLayout(context2);
        scrollView.addView(linearLayout1, new FrameLayout.LayoutParams(-1, -2));
        linearLayout1.setOrientation(1);
        this.actionBar.setTitle(LocaleController.getString("ChannelEdit", NUM));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.avatarContainer = linearLayout;
        linearLayout.setOrientation(1);
        this.avatarContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout1.addView(this.avatarContainer, LayoutHelper.createLinear(-1, -2));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.avatarContainer.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
        AnonymousClass4 r10 = new BackupImageView(context2) {
            public void invalidate() {
                if (ChatEditActivity.this.avatarOverlay != null) {
                    ChatEditActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate();
            }

            public void invalidate(int l, int t, int r, int b) {
                if (ChatEditActivity.this.avatarOverlay != null) {
                    ChatEditActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate(l, t, r, b);
            }
        };
        this.avatarImage = r10;
        r10.setRoundRadius(AndroidUtilities.dp(32.0f));
        if (ChatObject.canChangeChatInfo(this.currentChat)) {
            frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 8.0f));
            final Paint paint = new Paint(1);
            paint.setColor(NUM);
            AnonymousClass5 r11 = new View(context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (ChatEditActivity.this.avatarImage != null && ChatEditActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                        paint.setAlpha((int) (ChatEditActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                        canvas.drawCircle(((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f, ((float) getMeasuredWidth()) / 2.0f, paint);
                    }
                }
            };
            this.avatarOverlay = r11;
            frameLayout.addView(r11, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 8.0f));
            RadialProgressView radialProgressView = new RadialProgressView(context2);
            this.avatarProgressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(30.0f));
            this.avatarProgressView.setProgressColor(-1);
            this.avatarProgressView.setNoProgress(false);
            frameLayout.addView(this.avatarProgressView, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 8.0f));
            showAvatarProgress(false, false);
            this.avatarContainer.setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda7(this));
        } else {
            frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
        }
        EditTextEmoji editTextEmoji2 = new EditTextEmoji(context2, sizeNotifierFrameLayout, this, 0);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                ChatEditActivity.this.avatarDrawable.setInfo(5, ChatEditActivity.this.nameTextView.getText().toString(), (String) null);
                if (ChatEditActivity.this.avatarImage != null) {
                    ChatEditActivity.this.avatarImage.invalidate();
                }
            }
        });
        this.nameTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(128)});
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 16, LocaleController.isRTL ? 5.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 5.0f, 0.0f));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        this.settingsContainer = linearLayout2;
        linearLayout2.setOrientation(1);
        this.settingsContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout1.addView(this.settingsContainer, LayoutHelper.createLinear(-1, -2));
        if (ChatObject.canChangeChatInfo(this.currentChat)) {
            AnonymousClass7 r112 = new TextCell(context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
                }
            };
            this.setAvatarCell = r112;
            r112.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.setAvatarCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
            this.setAvatarCell.setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda8(this));
            this.settingsContainer.addView(this.setAvatarCell, LayoutHelper.createLinear(-1, -2));
        }
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.descriptionTextView = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 16.0f);
        this.descriptionTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.descriptionTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
        this.descriptionTextView.setBackgroundDrawable((Drawable) null);
        this.descriptionTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.descriptionTextView.setInputType(180225);
        this.descriptionTextView.setImeOptions(6);
        this.descriptionTextView.setEnabled(ChatObject.canChangeChatInfo(this.currentChat));
        EditTextBoldCursor editTextBoldCursor2 = this.descriptionTextView;
        editTextBoldCursor2.setFocusable(editTextBoldCursor2.isEnabled());
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
        this.descriptionTextView.setOnEditorActionListener(new ChatEditActivity$$ExternalSyntheticLambda14(this));
        this.descriptionTextView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
            }
        });
        ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context2);
        this.settingsTopSectionCell = shadowSectionCell;
        linearLayout1.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
        LinearLayout linearLayout3 = new LinearLayout(context2);
        this.typeEditContainer = linearLayout3;
        linearLayout3.setOrientation(1);
        this.typeEditContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout1.addView(this.typeEditContainer, LayoutHelper.createLinear(-1, -2));
        if (this.currentChat.megagroup && ((chatFull4 = this.info) == null || chatFull4.can_set_location)) {
            TextDetailCell textDetailCell = new TextDetailCell(context2);
            this.locationCell = textDetailCell;
            textDetailCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.locationCell, LayoutHelper.createLinear(-1, -2));
            this.locationCell.setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda9(this));
        }
        if (this.currentChat.creator && ((chatFull3 = this.info) == null || chatFull3.can_set_username)) {
            TextDetailCell textDetailCell2 = new TextDetailCell(context2);
            this.typeCell = textDetailCell2;
            textDetailCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.typeCell, LayoutHelper.createLinear(-1, -2));
            this.typeCell.setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda25(this));
        }
        if (ChatObject.isChannel(this.currentChat) && ((this.isChannel && ChatObject.canUserDoAdminAction(this.currentChat, 1)) || (!this.isChannel && ChatObject.canUserDoAdminAction(this.currentChat, 0)))) {
            TextDetailCell textDetailCell3 = new TextDetailCell(context2);
            this.linkedCell = textDetailCell3;
            textDetailCell3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.linkedCell, LayoutHelper.createLinear(-1, -2));
            this.linkedCell.setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda26(this));
        }
        if (!this.isChannel && ChatObject.canBlockUsers(this.currentChat) && (ChatObject.isChannel(this.currentChat) || this.currentChat.creator)) {
            TextDetailCell textDetailCell4 = new TextDetailCell(context2);
            this.historyCell = textDetailCell4;
            textDetailCell4.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.historyCell, LayoutHelper.createLinear(-1, -2));
            this.historyCell.setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda10(this, context2));
        }
        if (this.isChannel) {
            TextCheckCell textCheckCell = new TextCheckCell(context2);
            this.signCell = textCheckCell;
            textCheckCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.signCell.setTextAndValueAndCheck(LocaleController.getString("ChannelSignMessages", NUM), LocaleController.getString("ChannelSignMessagesInfo", NUM), this.signMessages, true, false);
            this.typeEditContainer.addView(this.signCell, LayoutHelper.createFrame(-1, -2.0f));
            this.signCell.setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda27(this));
        }
        ActionBarMenu menu = this.actionBar.createMenu();
        if (!(!ChatObject.canChangeChatInfo(this.currentChat) && this.signCell == null && this.historyCell == null)) {
            ActionBarMenuItem addItemWithWidth = menu.addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
            this.doneButton = addItemWithWidth;
            addItemWithWidth.setContentDescription(LocaleController.getString("Done", NUM));
        }
        if (!(this.locationCell == null && this.signCell == null && this.historyCell == null && this.typeCell == null && this.linkedCell == null)) {
            ShadowSectionCell shadowSectionCell2 = new ShadowSectionCell(context2);
            this.settingsSectionCell = shadowSectionCell2;
            linearLayout1.addView(shadowSectionCell2, LayoutHelper.createLinear(-1, -2));
        }
        LinearLayout linearLayout4 = new LinearLayout(context2);
        this.infoContainer = linearLayout4;
        linearLayout4.setOrientation(1);
        this.infoContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout1.addView(this.infoContainer, LayoutHelper.createLinear(-1, -2));
        TextCell textCell = new TextCell(context2);
        this.blockCell = textCell;
        textCell.setBackground(Theme.getSelectorDrawable(false));
        this.blockCell.setVisibility((ChatObject.isChannel(this.currentChat) || this.currentChat.creator || (ChatObject.hasAdminRights(this.currentChat) && ChatObject.canChangeChatInfo(this.currentChat))) ? 0 : 8);
        this.blockCell.setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda28(this));
        TextCell textCell2 = new TextCell(context2);
        this.inviteLinksCell = textCell2;
        textCell2.setBackground(Theme.getSelectorDrawable(false));
        this.inviteLinksCell.setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda29(this));
        TextCell textCell3 = new TextCell(context2);
        this.reactionsCell = textCell3;
        textCell3.setBackground(Theme.getSelectorDrawable(false));
        this.reactionsCell.setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda30(this));
        TextCell textCell4 = new TextCell(context2);
        this.adminCell = textCell4;
        textCell4.setBackground(Theme.getSelectorDrawable(false));
        this.adminCell.setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda1(this));
        TextCell textCell5 = new TextCell(context2);
        this.membersCell = textCell5;
        textCell5.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.membersCell.setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda2(this));
        if (!ChatObject.isChannelAndNotMegaGroup(this.currentChat)) {
            TextCell textCell6 = new TextCell(context2);
            this.memberRequestsCell = textCell6;
            textCell6.setBackground(Theme.getSelectorDrawable(false));
            this.memberRequestsCell.setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda3(this));
        }
        if (ChatObject.isChannel(this.currentChat) || this.currentChat.gigagroup) {
            TextCell textCell7 = new TextCell(context2);
            this.logCell = textCell7;
            textCell7.setTextAndIcon(LocaleController.getString("EventLog", NUM), NUM, false);
            this.logCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.logCell.setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda4(this));
        }
        this.infoContainer.addView(this.reactionsCell, LayoutHelper.createLinear(-1, -2));
        if (!this.isChannel && !this.currentChat.gigagroup) {
            this.infoContainer.addView(this.blockCell, LayoutHelper.createLinear(-1, -2));
        }
        if (!this.isChannel) {
            this.infoContainer.addView(this.inviteLinksCell, LayoutHelper.createLinear(-1, -2));
        }
        this.infoContainer.addView(this.adminCell, LayoutHelper.createLinear(-1, -2));
        this.infoContainer.addView(this.membersCell, LayoutHelper.createLinear(-1, -2));
        if (!(this.memberRequestsCell == null || (chatFull2 = this.info) == null || chatFull2.requests_pending <= 0)) {
            this.infoContainer.addView(this.memberRequestsCell, LayoutHelper.createLinear(-1, -2));
        }
        if (this.isChannel) {
            this.infoContainer.addView(this.inviteLinksCell, LayoutHelper.createLinear(-1, -2));
        }
        if (this.isChannel || this.currentChat.gigagroup) {
            this.infoContainer.addView(this.blockCell, LayoutHelper.createLinear(-1, -2));
        }
        TextCell textCell8 = this.logCell;
        if (textCell8 != null) {
            this.infoContainer.addView(textCell8, LayoutHelper.createLinear(-1, -2));
        }
        ShadowSectionCell shadowSectionCell3 = new ShadowSectionCell(context2);
        this.infoSectionCell = shadowSectionCell3;
        linearLayout1.addView(shadowSectionCell3, LayoutHelper.createLinear(-1, -2));
        if (!ChatObject.hasAdminRights(this.currentChat)) {
            this.infoContainer.setVisibility(8);
            this.settingsTopSectionCell.setVisibility(8);
        }
        if (!this.isChannel && (chatFull = this.info) != null && chatFull.can_set_stickers) {
            FrameLayout frameLayout2 = new FrameLayout(context2);
            this.stickersContainer = frameLayout2;
            frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            linearLayout1.addView(this.stickersContainer, LayoutHelper.createLinear(-1, -2));
            TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
            this.stickersCell = textSettingsCell;
            textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.stickersCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.stickersContainer.addView(this.stickersCell, LayoutHelper.createFrame(-1, -2.0f));
            this.stickersCell.setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda5(this));
            TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
            this.stickersInfoCell3 = textInfoPrivacyCell;
            textInfoPrivacyCell.setText(LocaleController.getString("GroupStickersInfo", NUM));
            linearLayout1.addView(this.stickersInfoCell3, LayoutHelper.createLinear(-1, -2));
        }
        if (this.currentChat.creator) {
            FrameLayout frameLayout3 = new FrameLayout(context2);
            this.deleteContainer = frameLayout3;
            frameLayout3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            linearLayout1.addView(this.deleteContainer, LayoutHelper.createLinear(-1, -2));
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
            this.deleteCell.setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda6(this));
            ShadowSectionCell shadowSectionCell4 = new ShadowSectionCell(context2);
            this.deleteInfoCell = shadowSectionCell4;
            shadowSectionCell4.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            linearLayout1.addView(this.deleteInfoCell, LayoutHelper.createLinear(-1, -2));
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
        sizeNotifierFrameLayout.addView(undoView2, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        this.nameTextView.setText(this.currentChat.title);
        EditTextEmoji editTextEmoji4 = this.nameTextView;
        editTextEmoji4.setSelection(editTextEmoji4.length());
        TLRPC.ChatFull chatFull5 = this.info;
        if (chatFull5 != null) {
            this.descriptionTextView.setText(chatFull5.about);
        }
        setAvatar();
        updateFields(true);
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$2(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1908lambda$createView$3$orgtelegramuiChatEditActivity(View v) {
        ImageLocation videoLocation;
        if (!this.imageUpdater.isUploadingImage()) {
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
            if (chat.photo != null && chat.photo.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                if (chat.photo.dc_id != 0) {
                    chat.photo.photo_big.dc_id = chat.photo.dc_id;
                }
                TLRPC.ChatFull chatFull = this.info;
                if (chatFull == null || !(chatFull.chat_photo instanceof TLRPC.TL_photo) || this.info.chat_photo.video_sizes.isEmpty()) {
                    videoLocation = null;
                } else {
                    videoLocation = ImageLocation.getForPhoto(this.info.chat_photo.video_sizes.get(0), this.info.chat_photo);
                }
                PhotoViewer.getInstance().openPhotoWithVideo(chat.photo.photo_big, videoLocation, this.provider);
            }
        }
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1911lambda$createView$6$orgtelegramuiChatEditActivity(View v) {
        this.imageUpdater.openMenu(this.avatar != null, new ChatEditActivity$$ExternalSyntheticLambda15(this), new ChatEditActivity$$ExternalSyntheticLambda24(this));
        this.cameraDrawable.setCurrentFrame(0);
        this.cameraDrawable.setCustomEndFrame(43);
        this.setAvatarCell.imageView.playAnimation();
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1909lambda$createView$4$orgtelegramuiChatEditActivity() {
        this.avatar = null;
        MessagesController.getInstance(this.currentAccount).changeChatAvatar(this.chatId, (TLRPC.TL_inputChatPhoto) null, (TLRPC.InputFile) null, (TLRPC.InputFile) null, 0.0d, (String) null, (TLRPC.FileLocation) null, (TLRPC.FileLocation) null, (Runnable) null);
        showAvatarProgress(false, true);
        this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) this.currentChat);
        this.cameraDrawable.setCurrentFrame(0);
        this.setAvatarCell.imageView.playAnimation();
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1910lambda$createView$5$orgtelegramuiChatEditActivity(DialogInterface dialogInterface) {
        if (!this.imageUpdater.isUploadingImage()) {
            this.cameraDrawable.setCustomEndFrame(86);
            this.setAvatarCell.imageView.playAnimation();
            return;
        }
        this.cameraDrawable.setCurrentFrame(0, false);
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ boolean m1912lambda$createView$7$orgtelegramuiChatEditActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1914lambda$createView$9$orgtelegramuiChatEditActivity(View v) {
        if (AndroidUtilities.isGoogleMapsInstalled(this)) {
            LocationActivity fragment = new LocationActivity(4);
            fragment.setDialogId(-this.chatId);
            TLRPC.ChatFull chatFull = this.info;
            if (chatFull != null && (chatFull.location instanceof TLRPC.TL_channelLocation)) {
                fragment.setInitialLocation((TLRPC.TL_channelLocation) this.info.location);
            }
            fragment.setDelegate(new ChatEditActivity$$ExternalSyntheticLambda23(this));
            presentFragment(fragment);
        }
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1913lambda$createView$8$orgtelegramuiChatEditActivity(TLRPC.MessageMedia location, int live, boolean notify, int scheduleDate) {
        TLRPC.TL_channelLocation channelLocation = new TLRPC.TL_channelLocation();
        channelLocation.address = location.address;
        channelLocation.geo_point = location.geo;
        this.info.location = channelLocation;
        this.info.flags |= 32768;
        updateFields(false);
        getMessagesController().loadFullChat(this.chatId, 0, true);
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1893lambda$createView$10$orgtelegramuiChatEditActivity(View v) {
        long j = this.chatId;
        TextDetailCell textDetailCell = this.locationCell;
        ChatEditTypeActivity fragment = new ChatEditTypeActivity(j, textDetailCell != null && textDetailCell.getVisibility() == 0);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1894lambda$createView$11$orgtelegramuiChatEditActivity(View v) {
        ChatLinkActivity fragment = new ChatLinkActivity(this.chatId);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1896lambda$createView$13$orgtelegramuiChatEditActivity(Context context, View v) {
        Context context2 = context;
        BottomSheet.Builder builder = new BottomSheet.Builder(context2);
        builder.setApplyTopPadding(false);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        HeaderCell headerCell = new HeaderCell(context, "dialogTextBlue2", 23, 15, false);
        headerCell.setHeight(47);
        headerCell.setText(LocaleController.getString("ChatHistory", NUM));
        linearLayout.addView(headerCell);
        LinearLayout linearLayoutInviteContainer = new LinearLayout(context2);
        linearLayoutInviteContainer.setOrientation(1);
        linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(-1, -2));
        RadioButtonCell[] buttons = new RadioButtonCell[2];
        int a = 0;
        for (int i = 2; a < i; i = 2) {
            buttons[a] = new RadioButtonCell(context2, true);
            buttons[a].setTag(Integer.valueOf(a));
            buttons[a].setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (a == 0) {
                buttons[a].setTextAndValue(LocaleController.getString("ChatHistoryVisible", NUM), LocaleController.getString("ChatHistoryVisibleInfo", NUM), true, !this.historyHidden);
            } else if (ChatObject.isChannel(this.currentChat)) {
                buttons[a].setTextAndValue(LocaleController.getString("ChatHistoryHidden", NUM), LocaleController.getString("ChatHistoryHiddenInfo", NUM), false, this.historyHidden);
            } else {
                buttons[a].setTextAndValue(LocaleController.getString("ChatHistoryHidden", NUM), LocaleController.getString("ChatHistoryHiddenInfo2", NUM), false, this.historyHidden);
            }
            linearLayoutInviteContainer.addView(buttons[a], LayoutHelper.createLinear(-1, -2));
            buttons[a].setOnClickListener(new ChatEditActivity$$ExternalSyntheticLambda12(this, buttons, builder));
            a++;
        }
        builder.setCustomView(linearLayout);
        showDialog(builder.create());
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1895lambda$createView$12$orgtelegramuiChatEditActivity(RadioButtonCell[] buttons, BottomSheet.Builder builder, View v2) {
        Integer tag = (Integer) v2.getTag();
        boolean z = false;
        buttons[0].setChecked(tag.intValue() == 0, true);
        buttons[1].setChecked(tag.intValue() == 1, true);
        if (tag.intValue() == 1) {
            z = true;
        }
        this.historyHidden = z;
        builder.getDismissRunnable().run();
        updateFields(true);
    }

    /* renamed from: lambda$createView$14$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1897lambda$createView$14$orgtelegramuiChatEditActivity(View v) {
        boolean z = !this.signMessages;
        this.signMessages = z;
        ((TextCheckCell) v).setChecked(z);
    }

    /* renamed from: lambda$createView$15$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1898lambda$createView$15$orgtelegramuiChatEditActivity(View v) {
        Bundle args = new Bundle();
        args.putLong("chat_id", this.chatId);
        args.putInt("type", (this.isChannel || this.currentChat.gigagroup) ? 0 : 3);
        ChatUsersActivity fragment = new ChatUsersActivity(args);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$16$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1899lambda$createView$16$orgtelegramuiChatEditActivity(View v) {
        ManageLinksActivity fragment = new ManageLinksActivity(this.chatId, 0, 0);
        TLRPC.ChatFull chatFull = this.info;
        fragment.setInfo(chatFull, chatFull.exported_invite);
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$17$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1900lambda$createView$17$orgtelegramuiChatEditActivity(View v) {
        Bundle args = new Bundle();
        args.putLong("chat_id", this.chatId);
        ChatReactionsEditActivity reactionsEditActivity = new ChatReactionsEditActivity(args);
        reactionsEditActivity.setInfo(this.info);
        presentFragment(reactionsEditActivity);
    }

    /* renamed from: lambda$createView$18$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1901lambda$createView$18$orgtelegramuiChatEditActivity(View v) {
        Bundle args = new Bundle();
        args.putLong("chat_id", this.chatId);
        args.putInt("type", 1);
        ChatUsersActivity fragment = new ChatUsersActivity(args);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$19$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1902lambda$createView$19$orgtelegramuiChatEditActivity(View v) {
        Bundle args = new Bundle();
        args.putLong("chat_id", this.chatId);
        args.putInt("type", 2);
        ChatUsersActivity fragment = new ChatUsersActivity(args);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$20$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1903lambda$createView$20$orgtelegramuiChatEditActivity(View v) {
        presentFragment(new MemberRequestsActivity(this.chatId));
    }

    /* renamed from: lambda$createView$21$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1904lambda$createView$21$orgtelegramuiChatEditActivity(View v) {
        presentFragment(new ChannelAdminLogActivity(this.currentChat));
    }

    /* renamed from: lambda$createView$22$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1905lambda$createView$22$orgtelegramuiChatEditActivity(View v) {
        GroupStickersActivity groupStickersActivity = new GroupStickersActivity(this.currentChat.id);
        groupStickersActivity.setInfo(this.info);
        presentFragment(groupStickersActivity);
    }

    /* renamed from: lambda$createView$24$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1907lambda$createView$24$orgtelegramuiChatEditActivity(View v) {
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, true, false, this.currentChat, (TLRPC.User) null, false, true, false, new ChatEditActivity$$ExternalSyntheticLambda18(this), (Theme.ResourcesProvider) null);
    }

    /* renamed from: lambda$createView$23$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1906lambda$createView$23$orgtelegramuiChatEditActivity(boolean param) {
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(-this.chatId));
        } else {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        finishFragment();
        getNotificationCenter().postNotificationName(NotificationCenter.needDeleteDialog, Long.valueOf(-this.currentChat.id), null, this.currentChat, Boolean.valueOf(param));
    }

    private void setAvatar() {
        TLRPC.Chat chat;
        boolean hasPhoto;
        if (this.avatarImage != null && (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) != null) {
            this.currentChat = chat;
            if (chat.photo != null) {
                this.avatar = this.currentChat.photo.photo_small;
                ImageLocation location = ImageLocation.getForUserOrChat(this.currentChat, 1);
                this.avatarImage.setForUserOrChat(this.currentChat, this.avatarDrawable);
                hasPhoto = location != null;
            } else {
                this.avatarImage.setImageDrawable(this.avatarDrawable);
                hasPhoto = false;
            }
            if (this.setAvatarCell != null) {
                if (hasPhoto || this.imageUpdater.isUploadingImage()) {
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

    public void didReceivedNotification(int id, int account, Object... args) {
        EditTextBoldCursor editTextBoldCursor;
        if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = args[0];
            if (chatFull.id == this.chatId) {
                if (this.info == null && (editTextBoldCursor = this.descriptionTextView) != null) {
                    editTextBoldCursor.setText(chatFull.about);
                }
                boolean z = true;
                boolean infoWasEmpty = this.info == null;
                this.info = chatFull;
                if (ChatObject.isChannel(this.currentChat) && !this.info.hidden_prehistory) {
                    z = false;
                }
                this.historyHidden = z;
                updateFields(false);
                if (infoWasEmpty) {
                    loadLinksCount();
                }
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            if ((MessagesController.UPDATE_MASK_AVATAR & args[0].intValue()) != 0) {
                setAvatar();
            }
        } else if (id == NotificationCenter.chatAvailableReactionsUpdated) {
            long chatId2 = args[0].longValue();
            if (chatId2 == this.chatId) {
                TLRPC.ChatFull chatFull2 = getMessagesController().getChatFull(chatId2);
                this.info = chatFull2;
                if (chatFull2 != null) {
                    this.availableReactions = chatFull2.available_reactions;
                }
                updateReactionsCell();
            }
        }
    }

    public void onUploadProgressChanged(float progress) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView != null) {
            radialProgressView.setProgress(progress);
        }
    }

    public void didStartUpload(boolean isVideo) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView != null) {
            radialProgressView.setProgress(0.0f);
        }
    }

    public void didUploadPhoto(TLRPC.InputFile photo, TLRPC.InputFile video, double videoStartTimestamp, String videoPath, TLRPC.PhotoSize bigSize, TLRPC.PhotoSize smallSize) {
        AndroidUtilities.runOnUIThread(new ChatEditActivity$$ExternalSyntheticLambda16(this, smallSize, photo, video, videoStartTimestamp, videoPath, bigSize));
    }

    /* renamed from: lambda$didUploadPhoto$25$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1915lambda$didUploadPhoto$25$orgtelegramuiChatEditActivity(TLRPC.PhotoSize smallSize, TLRPC.InputFile photo, TLRPC.InputFile video, double videoStartTimestamp, String videoPath, TLRPC.PhotoSize bigSize) {
        TLRPC.PhotoSize photoSize = smallSize;
        TLRPC.FileLocation fileLocation = photoSize.location;
        this.avatar = fileLocation;
        if (photo == null && video == null) {
            this.avatarImage.setImage(ImageLocation.getForLocal(fileLocation), "50_50", (Drawable) this.avatarDrawable, (Object) this.currentChat);
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
        getMessagesController().changeChatAvatar(this.chatId, (TLRPC.TL_inputChatPhoto) null, photo, video, videoStartTimestamp, videoPath, photoSize.location, bigSize.location, (Runnable) null);
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
        EditTextBoldCursor editTextBoldCursor;
        TLRPC.ChatFull chatFull = this.info;
        String about = (chatFull == null || chatFull.about == null) ? "" : this.info.about;
        if ((this.info == null || !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory == this.historyHidden) && ((this.nameTextView == null || this.currentChat.title.equals(this.nameTextView.getText().toString())) && (((editTextBoldCursor = this.descriptionTextView) == null || about.equals(editTextBoldCursor.getText().toString())) && this.signMessages == this.currentChat.signatures))) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", NUM));
        if (this.isChannel) {
            builder.setMessage(LocaleController.getString("ChannelSettingsChangedAlert", NUM));
        } else {
            builder.setMessage(LocaleController.getString("GroupSettingsChangedAlert", NUM));
        }
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new ChatEditActivity$$ExternalSyntheticLambda11(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new ChatEditActivity$$ExternalSyntheticLambda22(this));
        showDialog(builder.create());
        return false;
    }

    /* renamed from: lambda$checkDiscard$26$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1891lambda$checkDiscard$26$orgtelegramuiChatEditActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* renamed from: lambda$checkDiscard$27$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1892lambda$checkDiscard$27$orgtelegramuiChatEditActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    private int getAdminCount() {
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull == null) {
            return 1;
        }
        int count = 0;
        int N = chatFull.participants.participants.size();
        for (int a = 0; a < N; a++) {
            TLRPC.ChatParticipant chatParticipant = this.info.participants.participants.get(a);
            if ((chatParticipant instanceof TLRPC.TL_chatParticipantAdmin) || (chatParticipant instanceof TLRPC.TL_chatParticipantCreator)) {
                count++;
            }
        }
        return count;
    }

    /* access modifiers changed from: private */
    public void processDone() {
        EditTextEmoji editTextEmoji;
        boolean z;
        if (!this.donePressed && (editTextEmoji = this.nameTextView) != null) {
            if (editTextEmoji.length() == 0) {
                Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(200);
                }
                AndroidUtilities.shakeView(this.nameTextView, 2.0f, 0);
                return;
            }
            this.donePressed = true;
            if (ChatObject.isChannel(this.currentChat) || this.historyHidden) {
                if (!(this.info == null || !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory == (z = this.historyHidden))) {
                    this.info.hidden_prehistory = z;
                    getMessagesController().toogleChannelInvitesHistory(this.chatId, this.historyHidden);
                }
                if (this.imageUpdater.isUploadingImage()) {
                    this.createAfterUpload = true;
                    AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
                    this.progressDialog = alertDialog;
                    alertDialog.setOnCancelListener(new ChatEditActivity$$ExternalSyntheticLambda0(this));
                    this.progressDialog.show();
                    return;
                }
                if (!this.currentChat.title.equals(this.nameTextView.getText().toString())) {
                    getMessagesController().changeChatTitle(this.chatId, this.nameTextView.getText().toString());
                }
                TLRPC.ChatFull chatFull = this.info;
                String about = (chatFull == null || chatFull.about == null) ? "" : this.info.about;
                EditTextBoldCursor editTextBoldCursor = this.descriptionTextView;
                if (editTextBoldCursor != null && !about.equals(editTextBoldCursor.getText().toString())) {
                    getMessagesController().updateChatAbout(this.chatId, this.descriptionTextView.getText().toString(), this.info);
                }
                if (this.signMessages != this.currentChat.signatures) {
                    this.currentChat.signatures = true;
                    getMessagesController().toogleChannelSignatures(this.chatId, this.signMessages);
                }
                finishFragment();
                return;
            }
            getMessagesController().convertToMegaGroup(getParentActivity(), this.chatId, this, new ChatEditActivity$$ExternalSyntheticLambda19(this));
        }
    }

    /* renamed from: lambda$processDone$28$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1919lambda$processDone$28$orgtelegramuiChatEditActivity(long param) {
        if (param == 0) {
            this.donePressed = false;
            return;
        }
        this.chatId = param;
        this.currentChat = getMessagesController().getChat(Long.valueOf(param));
        this.donePressed = false;
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull != null) {
            chatFull.hidden_prehistory = true;
        }
        processDone();
    }

    /* renamed from: lambda$processDone$29$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1920lambda$processDone$29$orgtelegramuiChatEditActivity(DialogInterface dialog) {
        this.createAfterUpload = false;
        this.progressDialog = null;
        this.donePressed = false;
    }

    private void showAvatarProgress(final boolean show, boolean animated) {
        if (this.avatarProgressView != null) {
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.avatarAnimation = null;
            }
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.avatarAnimation = animatorSet2;
                if (show) {
                    this.avatarProgressView.setVisibility(0);
                    this.avatarOverlay.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.avatarOverlay, View.ALPHA, new float[]{1.0f})});
                } else {
                    animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.avatarOverlay, View.ALPHA, new float[]{0.0f})});
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ChatEditActivity.this.avatarAnimation != null && ChatEditActivity.this.avatarProgressView != null) {
                            if (!show) {
                                ChatEditActivity.this.avatarProgressView.setVisibility(4);
                                ChatEditActivity.this.avatarOverlay.setVisibility(4);
                            }
                            AnimatorSet unused = ChatEditActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        AnimatorSet unused = ChatEditActivity.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (show) {
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

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.imageUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (!(imageUpdater2 == null || imageUpdater2.currentPicturePath == null)) {
            args.putString("path", this.imageUpdater.currentPicturePath);
        }
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            String text = editTextEmoji.getText().toString();
            if (text.length() != 0) {
                args.putString("nameTextView", text);
            }
        }
    }

    public void restoreSelfArgs(Bundle args) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.currentPicturePath = args.getString("path");
        }
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        if (chatFull != null) {
            if (this.currentChat == null) {
                this.currentChat = getMessagesController().getChat(Long.valueOf(this.chatId));
            }
            this.historyHidden = !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory;
            this.availableReactions = this.info.available_reactions;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003b, code lost:
        r7 = r0.info;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateFields(boolean r17) {
        /*
            r16 = this;
            r0 = r16
            if (r17 == 0) goto L_0x0016
            org.telegram.messenger.MessagesController r1 = r16.getMessagesController()
            long r2 = r0.chatId
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            if (r1 == 0) goto L_0x0016
            r0.currentChat = r1
        L_0x0016:
            org.telegram.tgnet.TLRPC$Chat r1 = r0.currentChat
            java.lang.String r1 = r1.username
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            org.telegram.ui.Cells.TextDetailCell r2 = r0.historyCell
            r3 = 0
            r5 = 8
            r6 = 0
            if (r2 == 0) goto L_0x004c
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.info
            if (r2 == 0) goto L_0x0037
            org.telegram.tgnet.TLRPC$ChannelLocation r2 = r2.location
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channelLocation
            if (r2 == 0) goto L_0x0037
            org.telegram.ui.Cells.TextDetailCell r2 = r0.historyCell
            r2.setVisibility(r5)
            goto L_0x004c
        L_0x0037:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.historyCell
            if (r1 == 0) goto L_0x0047
            org.telegram.tgnet.TLRPC$ChatFull r7 = r0.info
            if (r7 == 0) goto L_0x0045
            long r7 = r7.linked_chat_id
            int r9 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r9 != 0) goto L_0x0047
        L_0x0045:
            r7 = 0
            goto L_0x0049
        L_0x0047:
            r7 = 8
        L_0x0049:
            r2.setVisibility(r7)
        L_0x004c:
            org.telegram.ui.Cells.ShadowSectionCell r2 = r0.settingsSectionCell
            if (r2 == 0) goto L_0x007d
            org.telegram.ui.Cells.TextCheckCell r7 = r0.signCell
            if (r7 != 0) goto L_0x0079
            org.telegram.ui.Cells.TextDetailCell r7 = r0.typeCell
            if (r7 != 0) goto L_0x0079
            org.telegram.ui.Cells.TextDetailCell r7 = r0.linkedCell
            if (r7 == 0) goto L_0x0062
            int r7 = r7.getVisibility()
            if (r7 == 0) goto L_0x0079
        L_0x0062:
            org.telegram.ui.Cells.TextDetailCell r7 = r0.historyCell
            if (r7 == 0) goto L_0x006c
            int r7 = r7.getVisibility()
            if (r7 == 0) goto L_0x0079
        L_0x006c:
            org.telegram.ui.Cells.TextDetailCell r7 = r0.locationCell
            if (r7 == 0) goto L_0x0076
            int r7 = r7.getVisibility()
            if (r7 == 0) goto L_0x0079
        L_0x0076:
            r7 = 8
            goto L_0x007a
        L_0x0079:
            r7 = 0
        L_0x007a:
            r2.setVisibility(r7)
        L_0x007d:
            org.telegram.ui.Cells.TextCell r2 = r0.logCell
            if (r2 == 0) goto L_0x009f
            org.telegram.tgnet.TLRPC$Chat r7 = r0.currentChat
            boolean r7 = r7.megagroup
            if (r7 == 0) goto L_0x009b
            org.telegram.tgnet.TLRPC$Chat r7 = r0.currentChat
            boolean r7 = r7.gigagroup
            if (r7 != 0) goto L_0x009b
            org.telegram.tgnet.TLRPC$ChatFull r7 = r0.info
            if (r7 == 0) goto L_0x0098
            int r7 = r7.participants_count
            r8 = 200(0xc8, float:2.8E-43)
            if (r7 <= r8) goto L_0x0098
            goto L_0x009b
        L_0x0098:
            r7 = 8
            goto L_0x009c
        L_0x009b:
            r7 = 0
        L_0x009c:
            r2.setVisibility(r7)
        L_0x009f:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.linkedCell
            r7 = 1
            if (r2 == 0) goto L_0x0160
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.info
            if (r2 == 0) goto L_0x015b
            boolean r8 = r0.isChannel
            if (r8 != 0) goto L_0x00b4
            long r8 = r2.linked_chat_id
            int r2 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r2 != 0) goto L_0x00b4
            goto L_0x015b
        L_0x00b4:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.linkedCell
            r2.setVisibility(r6)
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.info
            long r8 = r2.linked_chat_id
            r2 = 2131625401(0x7f0e05b9, float:1.8878009E38)
            java.lang.String r10 = "Discussion"
            int r11 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r11 != 0) goto L_0x00da
            org.telegram.ui.Cells.TextDetailCell r3 = r0.linkedCell
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            r4 = 2131625408(0x7f0e05c0, float:1.8878023E38)
            java.lang.String r8 = "DiscussionInfo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r3.setTextAndValue(r2, r4, r7)
            goto L_0x0160
        L_0x00da:
            org.telegram.messenger.MessagesController r3 = r16.getMessagesController()
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.info
            long r8 = r4.linked_chat_id
            java.lang.Long r4 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            if (r3 != 0) goto L_0x00f2
            org.telegram.ui.Cells.TextDetailCell r2 = r0.linkedCell
            r2.setVisibility(r5)
            goto L_0x0160
        L_0x00f2:
            boolean r4 = r0.isChannel
            java.lang.String r8 = "@"
            if (r4 == 0) goto L_0x0127
            java.lang.String r4 = r3.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x010c
            org.telegram.ui.Cells.TextDetailCell r4 = r0.linkedCell
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            java.lang.String r8 = r3.title
            r4.setTextAndValue(r2, r8, r7)
            goto L_0x0160
        L_0x010c:
            org.telegram.ui.Cells.TextDetailCell r4 = r0.linkedCell
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r8)
            java.lang.String r8 = r3.username
            r9.append(r8)
            java.lang.String r8 = r9.toString()
            r4.setTextAndValue(r2, r8, r7)
            goto L_0x0160
        L_0x0127:
            java.lang.String r2 = r3.username
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            r4 = 2131626305(0x7f0e0941, float:1.8879842E38)
            java.lang.String r9 = "LinkedChannel"
            if (r2 == 0) goto L_0x0140
            org.telegram.ui.Cells.TextDetailCell r2 = r0.linkedCell
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r4)
            java.lang.String r8 = r3.title
            r2.setTextAndValue(r4, r8, r6)
            goto L_0x0160
        L_0x0140:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.linkedCell
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r4)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r8)
            java.lang.String r8 = r3.username
            r9.append(r8)
            java.lang.String r8 = r9.toString()
            r2.setTextAndValue(r4, r8, r6)
            goto L_0x0160
        L_0x015b:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.linkedCell
            r2.setVisibility(r5)
        L_0x0160:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.locationCell
            if (r2 == 0) goto L_0x01a1
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.info
            if (r2 == 0) goto L_0x019c
            boolean r2 = r2.can_set_location
            if (r2 == 0) goto L_0x019c
            org.telegram.ui.Cells.TextDetailCell r2 = r0.locationCell
            r2.setVisibility(r6)
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.info
            org.telegram.tgnet.TLRPC$ChannelLocation r2 = r2.location
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channelLocation
            r3 = 2131624433(0x7f0e01f1, float:1.8876046E38)
            java.lang.String r4 = "AttachLocation"
            if (r2 == 0) goto L_0x0190
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.info
            org.telegram.tgnet.TLRPC$ChannelLocation r2 = r2.location
            org.telegram.tgnet.TLRPC$TL_channelLocation r2 = (org.telegram.tgnet.TLRPC.TL_channelLocation) r2
            org.telegram.ui.Cells.TextDetailCell r8 = r0.locationCell
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.String r4 = r2.address
            r8.setTextAndValue(r3, r4, r7)
            goto L_0x01a1
        L_0x0190:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.locationCell
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.String r4 = "Unknown address"
            r2.setTextAndValue(r3, r4, r7)
            goto L_0x01a1
        L_0x019c:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.locationCell
            r2.setVisibility(r5)
        L_0x01a1:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.typeCell
            if (r2 == 0) goto L_0x0294
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.info
            if (r2 == 0) goto L_0x020a
            org.telegram.tgnet.TLRPC$ChannelLocation r2 = r2.location
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channelLocation
            if (r2 == 0) goto L_0x020a
            if (r1 == 0) goto L_0x01bb
            r2 = 2131628482(0x7f0e11c2, float:1.8884258E38)
            java.lang.String r3 = "TypeLocationGroupEdit"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x01e3
        L_0x01bb:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "https://"
            r2.append(r3)
            org.telegram.messenger.MessagesController r3 = r16.getMessagesController()
            java.lang.String r3 = r3.linkPrefix
            r2.append(r3)
            java.lang.String r3 = "/%s"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.Object[] r3 = new java.lang.Object[r7]
            org.telegram.tgnet.TLRPC$Chat r4 = r0.currentChat
            java.lang.String r4 = r4.username
            r3[r6] = r4
            java.lang.String r2 = java.lang.String.format(r2, r3)
        L_0x01e3:
            org.telegram.ui.Cells.TextDetailCell r3 = r0.typeCell
            r4 = 2131628481(0x7f0e11c1, float:1.8884256E38)
            java.lang.String r8 = "TypeLocationGroup"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            org.telegram.ui.Cells.TextDetailCell r8 = r0.historyCell
            if (r8 == 0) goto L_0x01f8
            int r8 = r8.getVisibility()
            if (r8 == 0) goto L_0x0202
        L_0x01f8:
            org.telegram.ui.Cells.TextDetailCell r8 = r0.linkedCell
            if (r8 == 0) goto L_0x0204
            int r8 = r8.getVisibility()
            if (r8 != 0) goto L_0x0204
        L_0x0202:
            r8 = 1
            goto L_0x0205
        L_0x0204:
            r8 = 0
        L_0x0205:
            r3.setTextAndValue(r4, r2, r8)
            goto L_0x0294
        L_0x020a:
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            boolean r2 = r2.noforwards
            boolean r3 = r0.isChannel
            if (r3 == 0) goto L_0x022c
            if (r1 == 0) goto L_0x0222
            if (r2 == 0) goto L_0x021c
            r3 = 2131628487(0x7f0e11c7, float:1.8884268E38)
            java.lang.String r4 = "TypePrivateRestrictedForwards"
            goto L_0x0227
        L_0x021c:
            r3 = 2131628484(0x7f0e11c4, float:1.8884262E38)
            java.lang.String r4 = "TypePrivate"
            goto L_0x0227
        L_0x0222:
            r3 = 2131628488(0x7f0e11c8, float:1.888427E38)
            java.lang.String r4 = "TypePublic"
        L_0x0227:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x0245
        L_0x022c:
            if (r1 == 0) goto L_0x023c
            if (r2 == 0) goto L_0x0236
            r3 = 2131628486(0x7f0e11c6, float:1.8884266E38)
            java.lang.String r4 = "TypePrivateGroupRestrictedForwards"
            goto L_0x0241
        L_0x0236:
            r3 = 2131628485(0x7f0e11c5, float:1.8884264E38)
            java.lang.String r4 = "TypePrivateGroup"
            goto L_0x0241
        L_0x023c:
            r3 = 2131628489(0x7f0e11c9, float:1.8884272E38)
            java.lang.String r4 = "TypePublicGroup"
        L_0x0241:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
        L_0x0245:
            boolean r4 = r0.isChannel
            if (r4 == 0) goto L_0x026f
            org.telegram.ui.Cells.TextDetailCell r4 = r0.typeCell
            r8 = 2131624896(0x7f0e03c0, float:1.8876985E38)
            java.lang.String r9 = "ChannelType"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            org.telegram.ui.Cells.TextDetailCell r9 = r0.historyCell
            if (r9 == 0) goto L_0x025e
            int r9 = r9.getVisibility()
            if (r9 == 0) goto L_0x0268
        L_0x025e:
            org.telegram.ui.Cells.TextDetailCell r9 = r0.linkedCell
            if (r9 == 0) goto L_0x026a
            int r9 = r9.getVisibility()
            if (r9 != 0) goto L_0x026a
        L_0x0268:
            r9 = 1
            goto L_0x026b
        L_0x026a:
            r9 = 0
        L_0x026b:
            r4.setTextAndValue(r8, r3, r9)
            goto L_0x0294
        L_0x026f:
            org.telegram.ui.Cells.TextDetailCell r4 = r0.typeCell
            r8 = 2131626002(0x7f0e0812, float:1.8879228E38)
            java.lang.String r9 = "GroupType"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            org.telegram.ui.Cells.TextDetailCell r9 = r0.historyCell
            if (r9 == 0) goto L_0x0284
            int r9 = r9.getVisibility()
            if (r9 == 0) goto L_0x028e
        L_0x0284:
            org.telegram.ui.Cells.TextDetailCell r9 = r0.linkedCell
            if (r9 == 0) goto L_0x0290
            int r9 = r9.getVisibility()
            if (r9 != 0) goto L_0x0290
        L_0x028e:
            r9 = 1
            goto L_0x0291
        L_0x0290:
            r9 = 0
        L_0x0291:
            r4.setTextAndValue(r8, r3, r9)
        L_0x0294:
            org.telegram.ui.Cells.TextDetailCell r2 = r0.historyCell
            if (r2 == 0) goto L_0x02b9
            boolean r2 = r0.historyHidden
            if (r2 == 0) goto L_0x02a2
            r2 = 2131624936(0x7f0e03e8, float:1.8877066E38)
            java.lang.String r3 = "ChatHistoryHidden"
            goto L_0x02a7
        L_0x02a2:
            r2 = 2131624939(0x7f0e03eb, float:1.8877072E38)
            java.lang.String r3 = "ChatHistoryVisible"
        L_0x02a7:
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Cells.TextDetailCell r3 = r0.historyCell
            r4 = 2131624935(0x7f0e03e7, float:1.8877064E38)
            java.lang.String r8 = "ChatHistory"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r3.setTextAndValue(r4, r2, r6)
        L_0x02b9:
            org.telegram.ui.Cells.TextSettingsCell r2 = r0.stickersCell
            r3 = 2131626000(0x7f0e0810, float:1.8879224E38)
            java.lang.String r4 = "GroupStickers"
            if (r2 == 0) goto L_0x02e3
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.info
            if (r2 == 0) goto L_0x02da
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.stickerset
            if (r2 == 0) goto L_0x02da
            org.telegram.ui.Cells.TextSettingsCell r2 = r0.stickersCell
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.tgnet.TLRPC$ChatFull r9 = r0.info
            org.telegram.tgnet.TLRPC$StickerSet r9 = r9.stickerset
            java.lang.String r9 = r9.title
            r2.setTextAndValue(r8, r9, r6)
            goto L_0x02e3
        L_0x02da:
            org.telegram.ui.Cells.TextSettingsCell r2 = r0.stickersCell
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r8, r6)
        L_0x02e3:
            org.telegram.ui.Cells.TextCell r2 = r0.membersCell
            if (r2 == 0) goto L_0x05cb
            org.telegram.tgnet.TLRPC$ChatFull r8 = r0.info
            r9 = 2131624891(0x7f0e03bb, float:1.8876975E38)
            java.lang.String r10 = "ChannelSubscribers"
            java.lang.String r12 = "ChannelMembers"
            r14 = 2131624813(0x7f0e036d, float:1.8876816E38)
            java.lang.String r15 = "ChannelBlacklist"
            r3 = 2131165268(0x7var_, float:1.7944748E38)
            if (r8 == 0) goto L_0x04e8
            org.telegram.ui.Cells.TextCell r2 = r0.memberRequestsCell
            if (r2 == 0) goto L_0x0329
            android.view.ViewParent r2 = r2.getParent()
            if (r2 != 0) goto L_0x031a
            android.widget.LinearLayout r2 = r0.infoContainer
            org.telegram.ui.Cells.TextCell r8 = r0.membersCell
            int r2 = r2.indexOfChild(r8)
            int r2 = r2 + r7
            android.widget.LinearLayout r8 = r0.infoContainer
            org.telegram.ui.Cells.TextCell r5 = r0.memberRequestsCell
            r11 = -1
            r13 = -2
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r13)
            r8.addView(r5, r2, r11)
        L_0x031a:
            org.telegram.ui.Cells.TextCell r2 = r0.memberRequestsCell
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.info
            int r5 = r5.requests_pending
            if (r5 <= 0) goto L_0x0324
            r5 = 0
            goto L_0x0326
        L_0x0324:
            r5 = 8
        L_0x0326:
            r2.setVisibility(r5)
        L_0x0329:
            boolean r2 = r0.isChannel
            java.lang.String r5 = "%d"
            if (r2 == 0) goto L_0x037b
            org.telegram.ui.Cells.TextCell r2 = r0.membersCell
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r9)
            java.lang.Object[] r9 = new java.lang.Object[r7]
            org.telegram.tgnet.TLRPC$ChatFull r10 = r0.info
            int r10 = r10.participants_count
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r9[r6] = r10
            java.lang.String r9 = java.lang.String.format(r5, r9)
            r2.setTextAndValueAndIcon(r8, r9, r3, r7)
            org.telegram.ui.Cells.TextCell r2 = r0.blockCell
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r14)
            java.lang.Object[] r8 = new java.lang.Object[r7]
            org.telegram.tgnet.TLRPC$ChatFull r9 = r0.info
            int r9 = r9.banned_count
            org.telegram.tgnet.TLRPC$ChatFull r10 = r0.info
            int r10 = r10.kicked_count
            int r9 = java.lang.Math.max(r9, r10)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r8[r6] = r9
            java.lang.String r8 = java.lang.String.format(r5, r8)
            org.telegram.ui.Cells.TextCell r9 = r0.logCell
            if (r9 == 0) goto L_0x0372
            int r9 = r9.getVisibility()
            if (r9 != 0) goto L_0x0372
            r9 = 1
            goto L_0x0373
        L_0x0372:
            r9 = 0
        L_0x0373:
            r10 = 2131165265(0x7var_, float:1.7944742E38)
            r2.setTextAndValueAndIcon(r3, r8, r10, r9)
            goto L_0x04b8
        L_0x037b:
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x03a0
            org.telegram.ui.Cells.TextCell r2 = r0.membersCell
            r8 = 2131624845(0x7f0e038d, float:1.8876881E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r12, r8)
            java.lang.Object[] r9 = new java.lang.Object[r7]
            org.telegram.tgnet.TLRPC$ChatFull r10 = r0.info
            int r10 = r10.participants_count
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r9[r6] = r10
            java.lang.String r9 = java.lang.String.format(r5, r9)
            r2.setTextAndValueAndIcon(r8, r9, r3, r7)
            goto L_0x03cd
        L_0x03a0:
            r8 = 2131624845(0x7f0e038d, float:1.8876881E38)
            org.telegram.ui.Cells.TextCell r2 = r0.membersCell
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r12, r8)
            java.lang.Object[] r9 = new java.lang.Object[r7]
            org.telegram.tgnet.TLRPC$ChatFull r10 = r0.info
            org.telegram.tgnet.TLRPC$ChatParticipants r10 = r10.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r10 = r10.participants
            int r10 = r10.size()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r9[r6] = r10
            java.lang.String r9 = java.lang.String.format(r5, r9)
            org.telegram.ui.Cells.TextCell r10 = r0.memberRequestsCell
            int r10 = r10.getVisibility()
            if (r10 != 0) goto L_0x03c9
            r10 = 1
            goto L_0x03ca
        L_0x03c9:
            r10 = 0
        L_0x03ca:
            r2.setTextAndValueAndIcon(r8, r9, r3, r10)
        L_0x03cd:
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            boolean r2 = r2.gigagroup
            if (r2 == 0) goto L_0x0406
            org.telegram.ui.Cells.TextCell r2 = r0.blockCell
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r14)
            java.lang.Object[] r8 = new java.lang.Object[r7]
            org.telegram.tgnet.TLRPC$ChatFull r9 = r0.info
            int r9 = r9.banned_count
            org.telegram.tgnet.TLRPC$ChatFull r10 = r0.info
            int r10 = r10.kicked_count
            int r9 = java.lang.Math.max(r9, r10)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r8[r6] = r9
            java.lang.String r8 = java.lang.String.format(r5, r8)
            org.telegram.ui.Cells.TextCell r9 = r0.logCell
            if (r9 == 0) goto L_0x03fd
            int r9 = r9.getVisibility()
            if (r9 != 0) goto L_0x03fd
            r9 = 1
            goto L_0x03fe
        L_0x03fd:
            r9 = 0
        L_0x03fe:
            r10 = 2131165265(0x7var_, float:1.7944742E38)
            r2.setTextAndValueAndIcon(r3, r8, r10, r9)
            goto L_0x0488
        L_0x0406:
            r2 = 0
            org.telegram.tgnet.TLRPC$Chat r3 = r0.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            if (r3 == 0) goto L_0x045e
            org.telegram.tgnet.TLRPC$Chat r3 = r0.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            boolean r3 = r3.send_stickers
            if (r3 != 0) goto L_0x0417
            int r2 = r2 + 1
        L_0x0417:
            org.telegram.tgnet.TLRPC$Chat r3 = r0.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            boolean r3 = r3.send_media
            if (r3 != 0) goto L_0x0421
            int r2 = r2 + 1
        L_0x0421:
            org.telegram.tgnet.TLRPC$Chat r3 = r0.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            boolean r3 = r3.embed_links
            if (r3 != 0) goto L_0x042b
            int r2 = r2 + 1
        L_0x042b:
            org.telegram.tgnet.TLRPC$Chat r3 = r0.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            boolean r3 = r3.send_messages
            if (r3 != 0) goto L_0x0435
            int r2 = r2 + 1
        L_0x0435:
            org.telegram.tgnet.TLRPC$Chat r3 = r0.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            boolean r3 = r3.pin_messages
            if (r3 != 0) goto L_0x043f
            int r2 = r2 + 1
        L_0x043f:
            org.telegram.tgnet.TLRPC$Chat r3 = r0.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            boolean r3 = r3.send_polls
            if (r3 != 0) goto L_0x0449
            int r2 = r2 + 1
        L_0x0449:
            org.telegram.tgnet.TLRPC$Chat r3 = r0.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            boolean r3 = r3.invite_users
            if (r3 != 0) goto L_0x0453
            int r2 = r2 + 1
        L_0x0453:
            org.telegram.tgnet.TLRPC$Chat r3 = r0.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            boolean r3 = r3.change_info
            if (r3 != 0) goto L_0x0460
            int r2 = r2 + 1
            goto L_0x0460
        L_0x045e:
            r2 = 8
        L_0x0460:
            org.telegram.ui.Cells.TextCell r3 = r0.blockCell
            r8 = 2131624870(0x7f0e03a6, float:1.8876932E38)
            java.lang.String r9 = "ChannelPermissions"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            java.lang.Integer r10 = java.lang.Integer.valueOf(r2)
            r9[r6] = r10
            r10 = 8
            java.lang.Integer r11 = java.lang.Integer.valueOf(r10)
            r9[r7] = r11
            java.lang.String r10 = "%d/%d"
            java.lang.String r9 = java.lang.String.format(r10, r9)
            r10 = 2131165262(0x7var_e, float:1.7944736E38)
            r3.setTextAndValueAndIcon(r8, r9, r10, r7)
        L_0x0488:
            org.telegram.ui.Cells.TextCell r2 = r0.memberRequestsCell
            if (r2 == 0) goto L_0x04b8
            r3 = 2131626439(0x7f0e09c7, float:1.8880114E38)
            java.lang.String r8 = "MemberRequests"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            java.lang.Object[] r8 = new java.lang.Object[r7]
            org.telegram.tgnet.TLRPC$ChatFull r9 = r0.info
            int r9 = r9.requests_pending
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r8[r6] = r9
            java.lang.String r8 = java.lang.String.format(r5, r8)
            r9 = 2131165266(0x7var_, float:1.7944744E38)
            org.telegram.ui.Cells.TextCell r10 = r0.logCell
            if (r10 == 0) goto L_0x04b4
            int r10 = r10.getVisibility()
            if (r10 != 0) goto L_0x04b4
            r10 = 1
            goto L_0x04b5
        L_0x04b4:
            r10 = 0
        L_0x04b5:
            r2.setTextAndValueAndIcon(r3, r8, r9, r10)
        L_0x04b8:
            org.telegram.ui.Cells.TextCell r2 = r0.adminCell
            r3 = 2131624808(0x7f0e0368, float:1.8876806E38)
            java.lang.String r8 = "ChannelAdministrators"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            java.lang.Object[] r8 = new java.lang.Object[r7]
            org.telegram.tgnet.TLRPC$Chat r9 = r0.currentChat
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r9)
            if (r9 == 0) goto L_0x04d2
            org.telegram.tgnet.TLRPC$ChatFull r9 = r0.info
            int r9 = r9.admins_count
            goto L_0x04d6
        L_0x04d2:
            int r9 = r16.getAdminCount()
        L_0x04d6:
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r8[r6] = r9
            java.lang.String r5 = java.lang.String.format(r5, r8)
            r8 = 2131165256(0x7var_, float:1.7944724E38)
            r2.setTextAndValueAndIcon(r3, r5, r8, r7)
            goto L_0x0566
        L_0x04e8:
            boolean r5 = r0.isChannel
            if (r5 == 0) goto L_0x050d
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r2.setTextAndIcon((java.lang.String) r5, (int) r3, (boolean) r7)
            org.telegram.ui.Cells.TextCell r2 = r0.blockCell
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r14)
            org.telegram.ui.Cells.TextCell r5 = r0.logCell
            if (r5 == 0) goto L_0x0505
            int r5 = r5.getVisibility()
            if (r5 != 0) goto L_0x0505
            r5 = 1
            goto L_0x0506
        L_0x0505:
            r5 = 0
        L_0x0506:
            r8 = 2131165265(0x7var_, float:1.7944742E38)
            r2.setTextAndIcon((java.lang.String) r3, (int) r8, (boolean) r5)
            goto L_0x0555
        L_0x050d:
            r5 = 2131624845(0x7f0e038d, float:1.8876881E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r12, r5)
            org.telegram.ui.Cells.TextCell r8 = r0.logCell
            if (r8 == 0) goto L_0x0520
            int r8 = r8.getVisibility()
            if (r8 != 0) goto L_0x0520
            r8 = 1
            goto L_0x0521
        L_0x0520:
            r8 = 0
        L_0x0521:
            r2.setTextAndIcon((java.lang.String) r5, (int) r3, (boolean) r8)
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            boolean r2 = r2.gigagroup
            if (r2 == 0) goto L_0x0544
            org.telegram.ui.Cells.TextCell r2 = r0.blockCell
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r14)
            org.telegram.ui.Cells.TextCell r5 = r0.logCell
            if (r5 == 0) goto L_0x053c
            int r5 = r5.getVisibility()
            if (r5 != 0) goto L_0x053c
            r5 = 1
            goto L_0x053d
        L_0x053c:
            r5 = 0
        L_0x053d:
            r8 = 2131165265(0x7var_, float:1.7944742E38)
            r2.setTextAndIcon((java.lang.String) r3, (int) r8, (boolean) r5)
            goto L_0x0555
        L_0x0544:
            org.telegram.ui.Cells.TextCell r2 = r0.blockCell
            r3 = 2131624870(0x7f0e03a6, float:1.8876932E38)
            java.lang.String r5 = "ChannelPermissions"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r5 = 2131165262(0x7var_e, float:1.7944736E38)
            r2.setTextAndIcon((java.lang.String) r3, (int) r5, (boolean) r7)
        L_0x0555:
            org.telegram.ui.Cells.TextCell r2 = r0.adminCell
            r3 = 2131624808(0x7f0e0368, float:1.8876806E38)
            java.lang.String r5 = "ChannelAdministrators"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r5 = 2131165256(0x7var_, float:1.7944724E38)
            r2.setTextAndIcon((java.lang.String) r3, (int) r5, (boolean) r7)
        L_0x0566:
            org.telegram.ui.Cells.TextCell r2 = r0.reactionsCell
            org.telegram.tgnet.TLRPC$Chat r3 = r0.currentChat
            boolean r3 = org.telegram.messenger.ChatObject.canChangeChatInfo(r3)
            if (r3 == 0) goto L_0x0572
            r10 = 0
            goto L_0x0574
        L_0x0572:
            r10 = 8
        L_0x0574:
            r2.setVisibility(r10)
            r16.updateReactionsCell()
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.info
            if (r2 == 0) goto L_0x05c4
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            r3 = 3
            boolean r2 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r2, r3)
            if (r2 == 0) goto L_0x05c4
            if (r1 != 0) goto L_0x0590
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            boolean r2 = r2.creator
            if (r2 == 0) goto L_0x0590
            goto L_0x05c4
        L_0x0590:
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.info
            int r2 = r2.invitesCount
            if (r2 <= 0) goto L_0x05b0
            org.telegram.ui.Cells.TextCell r2 = r0.inviteLinksCell
            r3 = 2131626161(0x7f0e08b1, float:1.887955E38)
            java.lang.String r5 = "InviteLinks"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.info
            int r5 = r5.invitesCount
            java.lang.String r5 = java.lang.Integer.toString(r5)
            r8 = 2131165259(0x7var_b, float:1.794473E38)
            r2.setTextAndValueAndIcon(r3, r5, r8, r7)
            goto L_0x05cb
        L_0x05b0:
            org.telegram.ui.Cells.TextCell r2 = r0.inviteLinksCell
            r3 = 2131626161(0x7f0e08b1, float:1.887955E38)
            java.lang.String r5 = "InviteLinks"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r5 = 2131165259(0x7var_b, float:1.794473E38)
            java.lang.String r8 = "1"
            r2.setTextAndValueAndIcon(r3, r8, r5, r7)
            goto L_0x05cb
        L_0x05c4:
            org.telegram.ui.Cells.TextCell r2 = r0.inviteLinksCell
            r3 = 8
            r2.setVisibility(r3)
        L_0x05cb:
            org.telegram.ui.Cells.TextSettingsCell r2 = r0.stickersCell
            if (r2 == 0) goto L_0x05f6
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.info
            if (r2 == 0) goto L_0x05f6
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.stickerset
            if (r2 == 0) goto L_0x05ea
            org.telegram.ui.Cells.TextSettingsCell r2 = r0.stickersCell
            r3 = 2131626000(0x7f0e0810, float:1.8879224E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.info
            org.telegram.tgnet.TLRPC$StickerSet r4 = r4.stickerset
            java.lang.String r4 = r4.title
            r2.setTextAndValue(r3, r4, r6)
            goto L_0x05f6
        L_0x05ea:
            r3 = 2131626000(0x7f0e0810, float:1.8879224E38)
            org.telegram.ui.Cells.TextSettingsCell r2 = r0.stickersCell
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3, r6)
        L_0x05f6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.updateFields(boolean):void");
    }

    private void updateReactionsCell() {
        String str;
        int count = 0;
        for (int i = 0; i < this.availableReactions.size(); i++) {
            TLRPC.TL_availableReaction reaction = getMediaDataController().getReactionsMap().get(this.availableReactions.get(i));
            if (reaction != null && !reaction.inactive) {
                count++;
            }
        }
        int reacts = Math.min(getMediaDataController().getEnabledReactionsList().size(), count);
        TextCell textCell = this.reactionsCell;
        String string = LocaleController.getString("Reactions", NUM);
        if (reacts == 0) {
            str = LocaleController.getString("ReactionsOff", NUM);
        } else {
            str = LocaleController.formatString("ReactionsCount", NUM, Integer.valueOf(reacts), Integer.valueOf(getMediaDataController().getEnabledReactionsList().size()));
        }
        textCell.setTextAndValueAndIcon(string, str, NUM, true);
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ChatEditActivity$$ExternalSyntheticLambda21(this);
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.setAvatarCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.setAvatarCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        themeDescriptions.add(new ThemeDescription((View) this.setAvatarCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        themeDescriptions.add(new ThemeDescription(this.membersCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.membersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.membersCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription(this.adminCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.adminCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.adminCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription(this.inviteLinksCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.inviteLinksCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.inviteLinksCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        if (this.memberRequestsCell != null) {
            themeDescriptions.add(new ThemeDescription(this.memberRequestsCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
            themeDescriptions.add(new ThemeDescription((View) this.memberRequestsCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            themeDescriptions.add(new ThemeDescription((View) this.memberRequestsCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        }
        themeDescriptions.add(new ThemeDescription(this.blockCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.blockCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.blockCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription(this.logCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.logCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.logCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription(this.typeCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription(this.historyCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription(this.locationCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.locationCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.locationCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        themeDescriptions.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        themeDescriptions.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.avatarContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.settingsContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.typeEditContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.deleteContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.stickersContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.infoContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.settingsTopSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.settingsSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.deleteInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.infoSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.signCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        themeDescriptions.add(new ThemeDescription((View) this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription(this.deleteCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.deleteCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        themeDescriptions.add(new ThemeDescription(this.stickersCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.stickersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.stickersInfoCell3, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.stickersInfoCell3, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, cellDelegate, "avatar_text"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundRed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundOrange"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundPink"));
        themeDescriptions.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        themeDescriptions.add(new ThemeDescription(this.reactionsCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.reactionsCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.reactionsCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$30$org-telegram-ui-ChatEditActivity  reason: not valid java name */
    public /* synthetic */ void m1916lambda$getThemeDescriptions$30$orgtelegramuiChatEditActivity() {
        BackupImageView backupImageView = this.avatarImage;
        if (backupImageView != null) {
            backupImageView.invalidate();
        }
    }
}
