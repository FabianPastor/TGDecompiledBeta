package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
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

public class ChatEditActivity extends BaseFragment implements ImageUpdater.ImageUpdaterDelegate, NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    private TextCell adminCell;
    private TLRPC.FileLocation avatar;
    /* access modifiers changed from: private */
    public AnimatorSet avatarAnimation;
    private TLRPC.FileLocation avatarBig;
    private LinearLayout avatarContainer;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    /* access modifiers changed from: private */
    public ImageView avatarEditor;
    /* access modifiers changed from: private */
    public BackupImageView avatarImage;
    /* access modifiers changed from: private */
    public View avatarOverlay;
    /* access modifiers changed from: private */
    public RadialProgressView avatarProgressView;
    private TextCell blockCell;
    private int chatId;
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
    private ImageUpdater imageUpdater = new ImageUpdater();
    private TLRPC.ChatFull info;
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
    private TLRPC.InputFile uploadedAvatar;

    static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public ChatEditActivity(Bundle bundle) {
        super(bundle);
        this.chatId = bundle.getInt("chat_id", 0);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x004e, code lost:
        if (r5.info == null) goto L_0x0050;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onFragmentCreate() {
        /*
            r5 = this;
            int r0 = r5.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r1 = r5.chatId
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            r5.currentChat = r0
            org.telegram.tgnet.TLRPC$Chat r0 = r5.currentChat
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x0051
            int r0 = r5.currentAccount
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)
            int r3 = r5.chatId
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChatSync(r3)
            r5.currentChat = r0
            org.telegram.tgnet.TLRPC$Chat r0 = r5.currentChat
            if (r0 == 0) goto L_0x0050
            int r0 = r5.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.tgnet.TLRPC$Chat r3 = r5.currentChat
            r0.putChat(r3, r1)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r5.info
            if (r0 != 0) goto L_0x0051
            int r0 = r5.currentAccount
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)
            int r3 = r5.chatId
            java.util.concurrent.CountDownLatch r4 = new java.util.concurrent.CountDownLatch
            r4.<init>(r1)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r0.loadChatInfo(r3, r4, r2, r2)
            r5.info = r0
            org.telegram.tgnet.TLRPC$ChatFull r0 = r5.info
            if (r0 != 0) goto L_0x0051
        L_0x0050:
            return r2
        L_0x0051:
            org.telegram.tgnet.TLRPC$Chat r0 = r5.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x0060
            org.telegram.tgnet.TLRPC$Chat r0 = r5.currentChat
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0060
            goto L_0x0061
        L_0x0060:
            r1 = 0
        L_0x0061:
            r5.isChannel = r1
            org.telegram.ui.Components.ImageUpdater r0 = r5.imageUpdater
            r0.parentFragment = r5
            r0.delegate = r5
            org.telegram.tgnet.TLRPC$Chat r0 = r5.currentChat
            boolean r0 = r0.signatures
            r5.signMessages = r0
            int r0 = r5.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad
            r0.addObserver(r5, r1)
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
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
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
    }

    public void onPause() {
        super.onPause();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onPause();
        }
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
        int i;
        TLRPC.ChatFull chatFull;
        TLRPC.ChatFull chatFull2;
        TLRPC.ChatFull chatFull3;
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
        AnonymousClass2 r2 = new SizeNotifierFrameLayout(context2, SharedConfig.smoothKeyboard) {
            private boolean ignoreLayout;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                int paddingTop = size2 - getPaddingTop();
                measureChildWithMargins(ChatEditActivity.this.actionBar, i, 0, i2, 0);
                if ((SharedConfig.smoothKeyboard ? 0 : getKeyboardHeight()) > AndroidUtilities.dp(20.0f)) {
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
            /* JADX WARNING: Removed duplicated region for block: B:29:0x0078  */
            /* JADX WARNING: Removed duplicated region for block: B:36:0x0092  */
            /* JADX WARNING: Removed duplicated region for block: B:44:0x00b9  */
            /* JADX WARNING: Removed duplicated region for block: B:45:0x00c2  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
                /*
                    r10 = this;
                    int r11 = r10.getChildCount()
                    boolean r0 = org.telegram.messenger.SharedConfig.smoothKeyboard
                    r1 = 0
                    if (r0 == 0) goto L_0x000b
                    r0 = 0
                    goto L_0x000f
                L_0x000b:
                    int r0 = r10.getKeyboardHeight()
                L_0x000f:
                    r2 = 1101004800(0x41a00000, float:20.0)
                    int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                    if (r0 > r2) goto L_0x002c
                    boolean r2 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                    if (r2 != 0) goto L_0x002c
                    boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r2 != 0) goto L_0x002c
                    org.telegram.ui.ChatEditActivity r2 = org.telegram.ui.ChatEditActivity.this
                    org.telegram.ui.Components.EditTextEmoji r2 = r2.nameTextView
                    int r2 = r2.getEmojiPadding()
                    goto L_0x002d
                L_0x002c:
                    r2 = 0
                L_0x002d:
                    r10.setBottomClip(r2)
                L_0x0030:
                    if (r1 >= r11) goto L_0x00d5
                    android.view.View r3 = r10.getChildAt(r1)
                    int r4 = r3.getVisibility()
                    r5 = 8
                    if (r4 != r5) goto L_0x0040
                    goto L_0x00d1
                L_0x0040:
                    android.view.ViewGroup$LayoutParams r4 = r3.getLayoutParams()
                    android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
                    int r5 = r3.getMeasuredWidth()
                    int r6 = r3.getMeasuredHeight()
                    int r7 = r4.gravity
                    r8 = -1
                    if (r7 != r8) goto L_0x0055
                    r7 = 51
                L_0x0055:
                    r8 = r7 & 7
                    r7 = r7 & 112(0x70, float:1.57E-43)
                    r8 = r8 & 7
                    r9 = 1
                    if (r8 == r9) goto L_0x0069
                    r9 = 5
                    if (r8 == r9) goto L_0x0064
                    int r8 = r4.leftMargin
                    goto L_0x0074
                L_0x0064:
                    int r8 = r14 - r5
                    int r9 = r4.rightMargin
                    goto L_0x0073
                L_0x0069:
                    int r8 = r14 - r12
                    int r8 = r8 - r5
                    int r8 = r8 / 2
                    int r9 = r4.leftMargin
                    int r8 = r8 + r9
                    int r9 = r4.rightMargin
                L_0x0073:
                    int r8 = r8 - r9
                L_0x0074:
                    r9 = 16
                    if (r7 == r9) goto L_0x0092
                    r9 = 48
                    if (r7 == r9) goto L_0x008a
                    r9 = 80
                    if (r7 == r9) goto L_0x0083
                    int r4 = r4.topMargin
                    goto L_0x009f
                L_0x0083:
                    int r7 = r15 - r2
                    int r7 = r7 - r13
                    int r7 = r7 - r6
                    int r4 = r4.bottomMargin
                    goto L_0x009d
                L_0x008a:
                    int r4 = r4.topMargin
                    int r7 = r10.getPaddingTop()
                    int r4 = r4 + r7
                    goto L_0x009f
                L_0x0092:
                    int r7 = r15 - r2
                    int r7 = r7 - r13
                    int r7 = r7 - r6
                    int r7 = r7 / 2
                    int r9 = r4.topMargin
                    int r7 = r7 + r9
                    int r4 = r4.bottomMargin
                L_0x009d:
                    int r4 = r7 - r4
                L_0x009f:
                    org.telegram.ui.ChatEditActivity r7 = org.telegram.ui.ChatEditActivity.this
                    org.telegram.ui.Components.EditTextEmoji r7 = r7.nameTextView
                    if (r7 == 0) goto L_0x00cc
                    org.telegram.ui.ChatEditActivity r7 = org.telegram.ui.ChatEditActivity.this
                    org.telegram.ui.Components.EditTextEmoji r7 = r7.nameTextView
                    boolean r7 = r7.isPopupView(r3)
                    if (r7 == 0) goto L_0x00cc
                    boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r4 == 0) goto L_0x00c2
                    int r4 = r10.getMeasuredHeight()
                    int r7 = r3.getMeasuredHeight()
                    goto L_0x00cb
                L_0x00c2:
                    int r4 = r10.getMeasuredHeight()
                    int r4 = r4 + r0
                    int r7 = r3.getMeasuredHeight()
                L_0x00cb:
                    int r4 = r4 - r7
                L_0x00cc:
                    int r5 = r5 + r8
                    int r6 = r6 + r4
                    r3.layout(r8, r4, r5, r6)
                L_0x00d1:
                    int r1 = r1 + 1
                    goto L_0x0030
                L_0x00d5:
                    r10.notifyHeightChanged()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.AnonymousClass2.onLayout(boolean, int, int, int, int):void");
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        r2.setOnTouchListener($$Lambda$ChatEditActivity$P4nSuGWkKCrAaBVoDH4QiQur0s.INSTANCE);
        this.fragmentView = r2;
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        ScrollView scrollView = new ScrollView(context2);
        scrollView.setFillViewport(true);
        r2.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        scrollView.addView(linearLayout, new FrameLayout.LayoutParams(-1, -2));
        linearLayout.setOrientation(1);
        this.actionBar.setTitle(LocaleController.getString("ChannelEdit", NUM));
        this.avatarContainer = new LinearLayout(context2);
        this.avatarContainer.setOrientation(1);
        this.avatarContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout.addView(this.avatarContainer, LayoutHelper.createLinear(-1, -2));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.avatarContainer.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
        this.avatarImage = new BackupImageView(context2) {
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
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
        int i2 = 5;
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
        if (ChatObject.canChangeChatInfo(this.currentChat)) {
            this.avatarDrawable.setInfo(5, (String) null, (String) null);
            final Paint paint = new Paint(1);
            paint.setColor(NUM);
            this.avatarOverlay = new View(context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (ChatEditActivity.this.avatarImage != null && ChatEditActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                        paint.setAlpha((int) (ChatEditActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                        canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(32.0f), paint);
                    }
                }
            };
            frameLayout.addView(this.avatarOverlay, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            this.avatarOverlay.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$2$ChatEditActivity(view);
                }
            });
            this.avatarOverlay.setContentDescription(LocaleController.getString("ChoosePhoto", NUM));
            this.avatarEditor = new ImageView(context2) {
                public void invalidate(int i, int i2, int i3, int i4) {
                    super.invalidate(i, i2, i3, i4);
                    ChatEditActivity.this.avatarOverlay.invalidate();
                }

                public void invalidate() {
                    super.invalidate();
                    ChatEditActivity.this.avatarOverlay.invalidate();
                }
            };
            this.avatarEditor.setScaleType(ImageView.ScaleType.CENTER);
            this.avatarEditor.setImageResource(NUM);
            this.avatarEditor.setEnabled(false);
            this.avatarEditor.setClickable(false);
            frameLayout.addView(this.avatarEditor, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            this.avatarProgressView = new RadialProgressView(context2);
            this.avatarProgressView.setSize(AndroidUtilities.dp(30.0f));
            this.avatarProgressView.setProgressColor(-1);
            frameLayout.addView(this.avatarProgressView, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            showAvatarProgress(false, false);
        } else {
            this.avatarDrawable.setInfo(5, this.currentChat.title, (String) null);
        }
        this.nameTextView = new EditTextEmoji(context2, r2, this, 0);
        if (this.isChannel) {
            this.nameTextView.setHint(LocaleController.getString("EnterChannelName", NUM));
        } else {
            this.nameTextView.setHint(LocaleController.getString("GroupName", NUM));
        }
        this.nameTextView.setEnabled(ChatObject.canChangeChatInfo(this.currentChat));
        EditTextEmoji editTextEmoji2 = this.nameTextView;
        editTextEmoji2.setFocusable(editTextEmoji2.isEnabled());
        this.nameTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(128)});
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 16, LocaleController.isRTL ? 5.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 5.0f, 0.0f));
        this.settingsContainer = new LinearLayout(context2);
        this.settingsContainer.setOrientation(1);
        this.settingsContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout.addView(this.settingsContainer, LayoutHelper.createLinear(-1, -2));
        this.descriptionTextView = new EditTextBoldCursor(context2);
        this.descriptionTextView.setTextSize(1, 16.0f);
        this.descriptionTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.descriptionTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
        this.descriptionTextView.setBackgroundDrawable((Drawable) null);
        EditTextBoldCursor editTextBoldCursor = this.descriptionTextView;
        if (!LocaleController.isRTL) {
            i2 = 3;
        }
        editTextBoldCursor.setGravity(i2);
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
        this.settingsContainer.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 23.0f, 12.0f, 23.0f, 6.0f));
        this.descriptionTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ChatEditActivity.this.lambda$createView$3$ChatEditActivity(textView, i, keyEvent);
            }
        });
        this.descriptionTextView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
        this.settingsTopSectionCell = new ShadowSectionCell(context2);
        linearLayout.addView(this.settingsTopSectionCell, LayoutHelper.createLinear(-1, -2));
        this.typeEditContainer = new LinearLayout(context2);
        this.typeEditContainer.setOrientation(1);
        this.typeEditContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout.addView(this.typeEditContainer, LayoutHelper.createLinear(-1, -2));
        if (this.currentChat.megagroup && ((chatFull3 = this.info) == null || chatFull3.can_set_location)) {
            this.locationCell = new TextDetailCell(context2);
            this.locationCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.locationCell, LayoutHelper.createLinear(-1, -2));
            this.locationCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$5$ChatEditActivity(view);
                }
            });
        }
        if (this.currentChat.creator && ((chatFull2 = this.info) == null || chatFull2.can_set_username)) {
            this.typeCell = new TextDetailCell(context2);
            this.typeCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.typeCell, LayoutHelper.createLinear(-1, -2));
            this.typeCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$6$ChatEditActivity(view);
                }
            });
        }
        if (ChatObject.isChannel(this.currentChat) && ((this.isChannel && ChatObject.canUserDoAdminAction(this.currentChat, 1)) || (!this.isChannel && ChatObject.canUserDoAdminAction(this.currentChat, 0)))) {
            this.linkedCell = new TextDetailCell(context2);
            this.linkedCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.linkedCell, LayoutHelper.createLinear(-1, -2));
            this.linkedCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$7$ChatEditActivity(view);
                }
            });
        }
        if (!this.isChannel && ChatObject.canBlockUsers(this.currentChat) && (ChatObject.isChannel(this.currentChat) || this.currentChat.creator)) {
            this.historyCell = new TextDetailCell(context2);
            this.historyCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.historyCell, LayoutHelper.createLinear(-1, -2));
            this.historyCell.setOnClickListener(new View.OnClickListener(context2) {
                private final /* synthetic */ Context f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$9$ChatEditActivity(this.f$1, view);
                }
            });
        }
        if (this.isChannel) {
            this.signCell = new TextCheckCell(context2);
            this.signCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.signCell.setTextAndValueAndCheck(LocaleController.getString("ChannelSignMessages", NUM), LocaleController.getString("ChannelSignMessagesInfo", NUM), this.signMessages, true, false);
            this.typeEditContainer.addView(this.signCell, LayoutHelper.createFrame(-1, -2.0f));
            this.signCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$10$ChatEditActivity(view);
                }
            });
        }
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (!(!ChatObject.canChangeChatInfo(this.currentChat) && this.signCell == null && this.historyCell == null)) {
            this.doneButton = createMenu.addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
            this.doneButton.setContentDescription(LocaleController.getString("Done", NUM));
        }
        if (!(this.locationCell == null && this.signCell == null && this.historyCell == null && this.typeCell == null && this.linkedCell == null)) {
            this.settingsSectionCell = new ShadowSectionCell(context2);
            linearLayout.addView(this.settingsSectionCell, LayoutHelper.createLinear(-1, -2));
        }
        this.infoContainer = new LinearLayout(context2);
        this.infoContainer.setOrientation(1);
        this.infoContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout.addView(this.infoContainer, LayoutHelper.createLinear(-1, -2));
        this.blockCell = new TextCell(context2);
        this.blockCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.blockCell.setVisibility((ChatObject.isChannel(this.currentChat) || this.currentChat.creator) ? 0 : 8);
        this.blockCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditActivity.this.lambda$createView$11$ChatEditActivity(view);
            }
        });
        this.adminCell = new TextCell(context2);
        this.adminCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.adminCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditActivity.this.lambda$createView$12$ChatEditActivity(view);
            }
        });
        this.membersCell = new TextCell(context2);
        this.membersCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.membersCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditActivity.this.lambda$createView$13$ChatEditActivity(view);
            }
        });
        if (ChatObject.isChannel(this.currentChat)) {
            this.logCell = new TextCell(context2);
            this.logCell.setTextAndIcon(LocaleController.getString("EventLog", NUM), NUM, false);
            this.logCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.logCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$14$ChatEditActivity(view);
                }
            });
        }
        if (!this.isChannel) {
            i = -2;
            this.infoContainer.addView(this.blockCell, LayoutHelper.createLinear(-1, -2));
        } else {
            i = -2;
        }
        this.infoContainer.addView(this.adminCell, LayoutHelper.createLinear(-1, i));
        this.infoContainer.addView(this.membersCell, LayoutHelper.createLinear(-1, i));
        if (this.isChannel) {
            this.infoContainer.addView(this.blockCell, LayoutHelper.createLinear(-1, i));
        }
        TextCell textCell = this.logCell;
        if (textCell != null) {
            this.infoContainer.addView(textCell, LayoutHelper.createLinear(-1, i));
        }
        this.infoSectionCell = new ShadowSectionCell(context2);
        linearLayout.addView(this.infoSectionCell, LayoutHelper.createLinear(-1, i));
        if (!ChatObject.hasAdminRights(this.currentChat)) {
            this.infoContainer.setVisibility(8);
            this.settingsTopSectionCell.setVisibility(8);
        }
        if (!this.isChannel && (chatFull = this.info) != null && chatFull.can_set_stickers) {
            this.stickersContainer = new FrameLayout(context2);
            this.stickersContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            linearLayout.addView(this.stickersContainer, LayoutHelper.createLinear(-1, -2));
            this.stickersCell = new TextSettingsCell(context2);
            this.stickersCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.stickersCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.stickersContainer.addView(this.stickersCell, LayoutHelper.createFrame(-1, -2.0f));
            this.stickersCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$createView$15$ChatEditActivity(view);
                }
            });
            this.stickersInfoCell3 = new TextInfoPrivacyCell(context2);
            this.stickersInfoCell3.setText(LocaleController.getString("GroupStickersInfo", NUM));
            linearLayout.addView(this.stickersInfoCell3, LayoutHelper.createLinear(-1, -2));
        }
        if (this.currentChat.creator) {
            this.deleteContainer = new FrameLayout(context2);
            this.deleteContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            linearLayout.addView(this.deleteContainer, LayoutHelper.createLinear(-1, -2));
            this.deleteCell = new TextSettingsCell(context2);
            this.deleteCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
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
                    ChatEditActivity.this.lambda$createView$17$ChatEditActivity(view);
                }
            });
            this.deleteInfoCell = new ShadowSectionCell(context2);
            this.deleteInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            linearLayout.addView(this.deleteInfoCell, LayoutHelper.createLinear(-1, -2));
        } else if (!this.isChannel && this.stickersInfoCell3 == null) {
            this.infoSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        }
        TextInfoPrivacyCell textInfoPrivacyCell = this.stickersInfoCell3;
        if (textInfoPrivacyCell != null) {
            if (this.deleteInfoCell == null) {
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            } else {
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            }
        }
        this.nameTextView.setText(this.currentChat.title);
        EditTextEmoji editTextEmoji3 = this.nameTextView;
        editTextEmoji3.setSelection(editTextEmoji3.length());
        TLRPC.ChatFull chatFull4 = this.info;
        if (chatFull4 != null) {
            this.descriptionTextView.setText(chatFull4.about);
        }
        TLRPC.Chat chat = this.currentChat;
        TLRPC.ChatPhoto chatPhoto = chat.photo;
        if (chatPhoto != null) {
            this.avatar = chatPhoto.photo_small;
            this.avatarBig = chatPhoto.photo_big;
            this.avatarImage.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) this.avatarDrawable, (Object) this.currentChat);
        } else {
            this.avatarImage.setImageDrawable(this.avatarDrawable);
        }
        updateFields(true);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$ChatEditActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new Runnable() {
            public final void run() {
                ChatEditActivity.this.lambda$null$1$ChatEditActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$1$ChatEditActivity() {
        this.avatar = null;
        this.avatarBig = null;
        this.uploadedAvatar = null;
        showAvatarProgress(false, true);
        this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) this.currentChat);
    }

    public /* synthetic */ boolean lambda$createView$3$ChatEditActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    public /* synthetic */ void lambda$createView$5$ChatEditActivity(View view) {
        if (AndroidUtilities.isGoogleMapsInstalled(this)) {
            LocationActivity locationActivity = new LocationActivity(4);
            locationActivity.setDialogId((long) (-this.chatId));
            TLRPC.ChatFull chatFull = this.info;
            if (chatFull != null) {
                TLRPC.ChannelLocation channelLocation = chatFull.location;
                if (channelLocation instanceof TLRPC.TL_channelLocation) {
                    locationActivity.setInitialLocation((TLRPC.TL_channelLocation) channelLocation);
                }
            }
            locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate() {
                public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
                    ChatEditActivity.this.lambda$null$4$ChatEditActivity(messageMedia, i, z, i2);
                }
            });
            presentFragment(locationActivity);
        }
    }

    public /* synthetic */ void lambda$null$4$ChatEditActivity(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
        TLRPC.TL_channelLocation tL_channelLocation = new TLRPC.TL_channelLocation();
        tL_channelLocation.address = messageMedia.address;
        tL_channelLocation.geo_point = messageMedia.geo;
        TLRPC.ChatFull chatFull = this.info;
        chatFull.location = tL_channelLocation;
        chatFull.flags |= 32768;
        updateFields(false);
        getMessagesController().loadFullChat(this.chatId, 0, true);
    }

    public /* synthetic */ void lambda$createView$6$ChatEditActivity(View view) {
        int i = this.chatId;
        TextDetailCell textDetailCell = this.locationCell;
        ChatEditTypeActivity chatEditTypeActivity = new ChatEditTypeActivity(i, textDetailCell != null && textDetailCell.getVisibility() == 0);
        chatEditTypeActivity.setInfo(this.info);
        presentFragment(chatEditTypeActivity);
    }

    public /* synthetic */ void lambda$createView$7$ChatEditActivity(View view) {
        ChatLinkActivity chatLinkActivity = new ChatLinkActivity(this.chatId);
        chatLinkActivity.setInfo(this.info);
        presentFragment(chatLinkActivity);
    }

    public /* synthetic */ void lambda$createView$9$ChatEditActivity(Context context, View view) {
        BottomSheet.Builder builder = new BottomSheet.Builder(context);
        builder.setApplyTopPadding(false);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        HeaderCell headerCell = new HeaderCell(context, true, 23, 15, false);
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
                private final /* synthetic */ RadioButtonCell[] f$1;
                private final /* synthetic */ BottomSheet.Builder f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$null$8$ChatEditActivity(this.f$1, this.f$2, view);
                }
            });
        }
        builder.setCustomView(linearLayout);
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$null$8$ChatEditActivity(RadioButtonCell[] radioButtonCellArr, BottomSheet.Builder builder, View view) {
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

    public /* synthetic */ void lambda$createView$10$ChatEditActivity(View view) {
        this.signMessages = !this.signMessages;
        ((TextCheckCell) view).setChecked(this.signMessages);
    }

    public /* synthetic */ void lambda$createView$11$ChatEditActivity(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("chat_id", this.chatId);
        bundle.putInt("type", !this.isChannel ? 3 : 0);
        ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle);
        chatUsersActivity.setInfo(this.info);
        presentFragment(chatUsersActivity);
    }

    public /* synthetic */ void lambda$createView$12$ChatEditActivity(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("chat_id", this.chatId);
        bundle.putInt("type", 1);
        ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle);
        chatUsersActivity.setInfo(this.info);
        presentFragment(chatUsersActivity);
    }

    public /* synthetic */ void lambda$createView$13$ChatEditActivity(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("chat_id", this.chatId);
        bundle.putInt("type", 2);
        ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle);
        chatUsersActivity.setInfo(this.info);
        presentFragment(chatUsersActivity);
    }

    public /* synthetic */ void lambda$createView$14$ChatEditActivity(View view) {
        presentFragment(new ChannelAdminLogActivity(this.currentChat));
    }

    public /* synthetic */ void lambda$createView$15$ChatEditActivity(View view) {
        GroupStickersActivity groupStickersActivity = new GroupStickersActivity(this.currentChat.id);
        groupStickersActivity.setInfo(this.info);
        presentFragment(groupStickersActivity);
    }

    public /* synthetic */ void lambda$createView$17$ChatEditActivity(View view) {
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, true, false, this.currentChat, (TLRPC.User) null, false, new MessagesStorage.BooleanCallback() {
            public final void run(boolean z) {
                ChatEditActivity.this.lambda$null$16$ChatEditActivity(z);
            }
        });
    }

    public /* synthetic */ void lambda$null$16$ChatEditActivity(boolean z) {
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(-((long) this.chatId)));
        } else {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chatId, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId())), this.info, true, false);
        finishFragment();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        EditTextBoldCursor editTextBoldCursor;
        if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = objArr[0];
            if (chatFull.id == this.chatId) {
                if (this.info == null && (editTextBoldCursor = this.descriptionTextView) != null) {
                    editTextBoldCursor.setText(chatFull.about);
                }
                this.info = chatFull;
                this.historyHidden = !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory;
                updateFields(false);
            }
        }
    }

    public void didUploadPhoto(TLRPC.InputFile inputFile, TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2) {
        AndroidUtilities.runOnUIThread(new Runnable(inputFile, photoSize2, photoSize) {
            private final /* synthetic */ TLRPC.InputFile f$1;
            private final /* synthetic */ TLRPC.PhotoSize f$2;
            private final /* synthetic */ TLRPC.PhotoSize f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChatEditActivity.this.lambda$didUploadPhoto$18$ChatEditActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$didUploadPhoto$18$ChatEditActivity(TLRPC.InputFile inputFile, TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2) {
        if (inputFile != null) {
            this.uploadedAvatar = inputFile;
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
            return;
        }
        this.avatar = photoSize.location;
        this.avatarBig = photoSize2.location;
        this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", (Drawable) this.avatarDrawable, (Object) this.currentChat);
        showAvatarProgress(true, false);
    }

    public String getInitialSearchString() {
        return this.nameTextView.getText().toString();
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        String str;
        EditTextEmoji editTextEmoji;
        EditTextBoldCursor editTextBoldCursor;
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull == null || (str = chatFull.about) == null) {
            str = "";
        }
        if ((this.info == null || !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory == this.historyHidden) && this.imageUpdater.uploadingImage == null && (((editTextEmoji = this.nameTextView) == null || this.currentChat.title.equals(editTextEmoji.getText().toString())) && ((editTextBoldCursor = this.descriptionTextView) == null || str.equals(editTextBoldCursor.getText().toString())))) {
            boolean z = this.signMessages;
            TLRPC.Chat chat = this.currentChat;
            if (z == chat.signatures && this.uploadedAvatar == null && (this.avatar != null || !(chat.photo instanceof TLRPC.TL_chatPhoto))) {
                return true;
            }
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
                ChatEditActivity.this.lambda$checkDiscard$19$ChatEditActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatEditActivity.this.lambda$checkDiscard$20$ChatEditActivity(dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    public /* synthetic */ void lambda$checkDiscard$19$ChatEditActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    public /* synthetic */ void lambda$checkDiscard$20$ChatEditActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    private int getAdminCount() {
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull == null) {
            return 1;
        }
        int size = chatFull.participants.participants.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC.ChatParticipant chatParticipant = this.info.participants.participants.get(i2);
            if ((chatParticipant instanceof TLRPC.TL_chatParticipantAdmin) || (chatParticipant instanceof TLRPC.TL_chatParticipantCreator)) {
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
            if (r0 != 0) goto L_0x013d
            org.telegram.ui.Components.EditTextEmoji r0 = r5.nameTextView
            if (r0 != 0) goto L_0x000a
            goto L_0x013d
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
            org.telegram.ui.-$$Lambda$ChatEditActivity$aa_B5hJiMKKENUg343hyaLuvH4Y r3 = new org.telegram.ui.-$$Lambda$ChatEditActivity$aa_B5hJiMKKENUg343hyaLuvH4Y
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
            java.lang.String r1 = r1.uploadingImage
            if (r1 == 0) goto L_0x0098
            r5.createAfterUpload = r0
            org.telegram.ui.ActionBar.AlertDialog r0 = new org.telegram.ui.ActionBar.AlertDialog
            android.app.Activity r1 = r5.getParentActivity()
            r2 = 3
            r0.<init>(r1, r2)
            r5.progressDialog = r0
            org.telegram.ui.ActionBar.AlertDialog r0 = r5.progressDialog
            org.telegram.ui.-$$Lambda$ChatEditActivity$2WjVM2RAUq-UrGGHPwPwkCVtYvs r1 = new org.telegram.ui.-$$Lambda$ChatEditActivity$2WjVM2RAUq-UrGGHPwPwkCVtYvs
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
            org.telegram.tgnet.TLRPC$InputFile r0 = r5.uploadedAvatar
            if (r0 == 0) goto L_0x0122
            int r0 = r5.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r1 = r5.chatId
            org.telegram.tgnet.TLRPC$InputFile r2 = r5.uploadedAvatar
            org.telegram.tgnet.TLRPC$FileLocation r3 = r5.avatar
            org.telegram.tgnet.TLRPC$FileLocation r4 = r5.avatarBig
            r0.changeChatAvatar(r1, r2, r3, r4)
            goto L_0x013a
        L_0x0122:
            org.telegram.tgnet.TLRPC$FileLocation r0 = r5.avatar
            if (r0 != 0) goto L_0x013a
            org.telegram.tgnet.TLRPC$Chat r0 = r5.currentChat
            org.telegram.tgnet.TLRPC$ChatPhoto r0 = r0.photo
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_chatPhoto
            if (r0 == 0) goto L_0x013a
            int r0 = r5.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r1 = r5.chatId
            r2 = 0
            r0.changeChatAvatar(r1, r2, r2, r2)
        L_0x013a:
            r5.finishFragment()
        L_0x013d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.processDone():void");
    }

    public /* synthetic */ void lambda$processDone$21$ChatEditActivity(int i) {
        if (i == 0) {
            this.donePressed = false;
            return;
        }
        this.chatId = i;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(i));
        this.donePressed = false;
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull != null) {
            chatFull.hidden_prehistory = true;
        }
        processDone();
    }

    public /* synthetic */ void lambda$processDone$22$ChatEditActivity(DialogInterface dialogInterface) {
        this.createAfterUpload = false;
        this.progressDialog = null;
        this.donePressed = false;
    }

    private void showAvatarProgress(final boolean z, boolean z2) {
        if (this.avatarEditor != null) {
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.avatarAnimation = null;
            }
            if (z2) {
                this.avatarAnimation = new AnimatorSet();
                if (z) {
                    this.avatarProgressView.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f})});
                } else {
                    this.avatarEditor.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f})});
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (ChatEditActivity.this.avatarAnimation != null && ChatEditActivity.this.avatarEditor != null) {
                            if (z) {
                                ChatEditActivity.this.avatarEditor.setVisibility(4);
                            } else {
                                ChatEditActivity.this.avatarProgressView.setVisibility(4);
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
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(4);
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
            } else {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(0);
                this.avatarProgressView.setAlpha(0.0f);
                this.avatarProgressView.setVisibility(4);
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

    public void restoreSelfArgs(Bundle bundle) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.currentPicturePath = bundle.getString("path");
        }
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        if (chatFull != null) {
            if (this.currentChat == null) {
                this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
            }
            this.historyHidden = !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0037, code lost:
        r3 = r10.info;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateFields(boolean r11) {
        /*
            r10 = this;
            if (r11 == 0) goto L_0x0016
            int r11 = r10.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            int r0 = r10.chatId
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r11 = r11.getChat(r0)
            if (r11 == 0) goto L_0x0016
            r10.currentChat = r11
        L_0x0016:
            org.telegram.tgnet.TLRPC$Chat r11 = r10.currentChat
            java.lang.String r11 = r11.username
            boolean r11 = android.text.TextUtils.isEmpty(r11)
            org.telegram.ui.Cells.TextDetailCell r0 = r10.historyCell
            r1 = 8
            r2 = 0
            if (r0 == 0) goto L_0x0046
            org.telegram.tgnet.TLRPC$ChatFull r3 = r10.info
            if (r3 == 0) goto L_0x0033
            org.telegram.tgnet.TLRPC$ChannelLocation r3 = r3.location
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_channelLocation
            if (r3 == 0) goto L_0x0033
            r0.setVisibility(r1)
            goto L_0x0046
        L_0x0033:
            org.telegram.ui.Cells.TextDetailCell r0 = r10.historyCell
            if (r11 == 0) goto L_0x0041
            org.telegram.tgnet.TLRPC$ChatFull r3 = r10.info
            if (r3 == 0) goto L_0x003f
            int r3 = r3.linked_chat_id
            if (r3 != 0) goto L_0x0041
        L_0x003f:
            r3 = 0
            goto L_0x0043
        L_0x0041:
            r3 = 8
        L_0x0043:
            r0.setVisibility(r3)
        L_0x0046:
            org.telegram.ui.Cells.ShadowSectionCell r0 = r10.settingsSectionCell
            if (r0 == 0) goto L_0x0077
            org.telegram.ui.Cells.TextCheckCell r3 = r10.signCell
            if (r3 != 0) goto L_0x0073
            org.telegram.ui.Cells.TextDetailCell r3 = r10.typeCell
            if (r3 != 0) goto L_0x0073
            org.telegram.ui.Cells.TextDetailCell r3 = r10.linkedCell
            if (r3 == 0) goto L_0x005c
            int r3 = r3.getVisibility()
            if (r3 == 0) goto L_0x0073
        L_0x005c:
            org.telegram.ui.Cells.TextDetailCell r3 = r10.historyCell
            if (r3 == 0) goto L_0x0066
            int r3 = r3.getVisibility()
            if (r3 == 0) goto L_0x0073
        L_0x0066:
            org.telegram.ui.Cells.TextDetailCell r3 = r10.locationCell
            if (r3 == 0) goto L_0x0070
            int r3 = r3.getVisibility()
            if (r3 == 0) goto L_0x0073
        L_0x0070:
            r3 = 8
            goto L_0x0074
        L_0x0073:
            r3 = 0
        L_0x0074:
            r0.setVisibility(r3)
        L_0x0077:
            org.telegram.ui.Cells.TextCell r0 = r10.logCell
            if (r0 == 0) goto L_0x0093
            org.telegram.tgnet.TLRPC$Chat r3 = r10.currentChat
            boolean r3 = r3.megagroup
            if (r3 == 0) goto L_0x008f
            org.telegram.tgnet.TLRPC$ChatFull r3 = r10.info
            if (r3 == 0) goto L_0x008c
            int r3 = r3.participants_count
            r4 = 200(0xc8, float:2.8E-43)
            if (r3 <= r4) goto L_0x008c
            goto L_0x008f
        L_0x008c:
            r3 = 8
            goto L_0x0090
        L_0x008f:
            r3 = 0
        L_0x0090:
            r0.setVisibility(r3)
        L_0x0093:
            org.telegram.ui.Cells.TextDetailCell r0 = r10.linkedCell
            r3 = 1
            if (r0 == 0) goto L_0x0152
            org.telegram.tgnet.TLRPC$ChatFull r0 = r10.info
            if (r0 == 0) goto L_0x014d
            boolean r4 = r10.isChannel
            if (r4 != 0) goto L_0x00a6
            int r0 = r0.linked_chat_id
            if (r0 != 0) goto L_0x00a6
            goto L_0x014d
        L_0x00a6:
            org.telegram.ui.Cells.TextDetailCell r0 = r10.linkedCell
            r0.setVisibility(r2)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r10.info
            int r0 = r0.linked_chat_id
            r4 = 2131624931(0x7f0e03e3, float:1.8877056E38)
            java.lang.String r5 = "Discussion"
            if (r0 != 0) goto L_0x00ca
            org.telegram.ui.Cells.TextDetailCell r0 = r10.linkedCell
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 2131624938(0x7f0e03ea, float:1.887707E38)
            java.lang.String r6 = "DiscussionInfo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r0.setTextAndValue(r4, r5, r3)
            goto L_0x0152
        L_0x00ca:
            org.telegram.messenger.MessagesController r0 = r10.getMessagesController()
            org.telegram.tgnet.TLRPC$ChatFull r6 = r10.info
            int r6 = r6.linked_chat_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r6)
            if (r0 != 0) goto L_0x00e2
            org.telegram.ui.Cells.TextDetailCell r0 = r10.linkedCell
            r0.setVisibility(r1)
            goto L_0x0152
        L_0x00e2:
            boolean r6 = r10.isChannel
            if (r6 == 0) goto L_0x0117
            java.lang.String r6 = r0.username
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x00fa
            org.telegram.ui.Cells.TextDetailCell r6 = r10.linkedCell
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.String r0 = r0.title
            r6.setTextAndValue(r4, r0, r3)
            goto L_0x0152
        L_0x00fa:
            org.telegram.ui.Cells.TextDetailCell r6 = r10.linkedCell
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "@"
            r5.append(r7)
            java.lang.String r0 = r0.username
            r5.append(r0)
            java.lang.String r0 = r5.toString()
            r6.setTextAndValue(r4, r0, r3)
            goto L_0x0152
        L_0x0117:
            java.lang.String r4 = r0.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            r5 = 2131625466(0x7f0e05fa, float:1.887814E38)
            java.lang.String r6 = "LinkedChannel"
            if (r4 == 0) goto L_0x0130
            org.telegram.ui.Cells.TextDetailCell r4 = r10.linkedCell
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            java.lang.String r0 = r0.title
            r4.setTextAndValue(r5, r0, r2)
            goto L_0x0152
        L_0x0130:
            org.telegram.ui.Cells.TextDetailCell r4 = r10.linkedCell
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "@"
            r6.append(r7)
            java.lang.String r0 = r0.username
            r6.append(r0)
            java.lang.String r0 = r6.toString()
            r4.setTextAndValue(r5, r0, r2)
            goto L_0x0152
        L_0x014d:
            org.telegram.ui.Cells.TextDetailCell r0 = r10.linkedCell
            r0.setVisibility(r1)
        L_0x0152:
            org.telegram.ui.Cells.TextDetailCell r0 = r10.locationCell
            if (r0 == 0) goto L_0x018d
            org.telegram.tgnet.TLRPC$ChatFull r4 = r10.info
            if (r4 == 0) goto L_0x0188
            boolean r4 = r4.can_set_location
            if (r4 == 0) goto L_0x0188
            r0.setVisibility(r2)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r10.info
            org.telegram.tgnet.TLRPC$ChannelLocation r0 = r0.location
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channelLocation
            r5 = 2131624286(0x7f0e015e, float:1.8875747E38)
            java.lang.String r6 = "AttachLocation"
            if (r4 == 0) goto L_0x017c
            org.telegram.tgnet.TLRPC$TL_channelLocation r0 = (org.telegram.tgnet.TLRPC.TL_channelLocation) r0
            org.telegram.ui.Cells.TextDetailCell r4 = r10.locationCell
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            java.lang.String r0 = r0.address
            r4.setTextAndValue(r5, r0, r3)
            goto L_0x018d
        L_0x017c:
            org.telegram.ui.Cells.TextDetailCell r0 = r10.locationCell
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r5)
            java.lang.String r5 = "Unknown address"
            r0.setTextAndValue(r4, r5, r3)
            goto L_0x018d
        L_0x0188:
            org.telegram.ui.Cells.TextDetailCell r0 = r10.locationCell
            r0.setVisibility(r1)
        L_0x018d:
            org.telegram.ui.Cells.TextDetailCell r0 = r10.typeCell
            if (r0 == 0) goto L_0x026e
            org.telegram.tgnet.TLRPC$ChatFull r0 = r10.info
            if (r0 == 0) goto L_0x01f8
            org.telegram.tgnet.TLRPC$ChannelLocation r0 = r0.location
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channelLocation
            if (r0 == 0) goto L_0x01f8
            if (r11 == 0) goto L_0x01a7
            r11 = 2131626897(0x7f0e0b91, float:1.8881043E38)
            java.lang.String r0 = "TypeLocationGroupEdit"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r0, r11)
            goto L_0x01d1
        L_0x01a7:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r0 = "https://"
            r11.append(r0)
            int r0 = r10.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.String r0 = r0.linkPrefix
            r11.append(r0)
            java.lang.String r0 = "/%s"
            r11.append(r0)
            java.lang.String r11 = r11.toString()
            java.lang.Object[] r0 = new java.lang.Object[r3]
            org.telegram.tgnet.TLRPC$Chat r4 = r10.currentChat
            java.lang.String r4 = r4.username
            r0[r2] = r4
            java.lang.String r11 = java.lang.String.format(r11, r0)
        L_0x01d1:
            org.telegram.ui.Cells.TextDetailCell r0 = r10.typeCell
            r4 = 2131626896(0x7f0e0b90, float:1.8881041E38)
            java.lang.String r5 = "TypeLocationGroup"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.Cells.TextDetailCell r5 = r10.historyCell
            if (r5 == 0) goto L_0x01e6
            int r5 = r5.getVisibility()
            if (r5 == 0) goto L_0x01f0
        L_0x01e6:
            org.telegram.ui.Cells.TextDetailCell r5 = r10.linkedCell
            if (r5 == 0) goto L_0x01f2
            int r5 = r5.getVisibility()
            if (r5 != 0) goto L_0x01f2
        L_0x01f0:
            r5 = 1
            goto L_0x01f3
        L_0x01f2:
            r5 = 0
        L_0x01f3:
            r0.setTextAndValue(r4, r11, r5)
            goto L_0x026e
        L_0x01f8:
            boolean r0 = r10.isChannel
            if (r0 == 0) goto L_0x020e
            if (r11 == 0) goto L_0x0204
            r11 = 2131626899(0x7f0e0b93, float:1.8881047E38)
            java.lang.String r0 = "TypePrivate"
            goto L_0x0209
        L_0x0204:
            r11 = 2131626901(0x7f0e0b95, float:1.8881051E38)
            java.lang.String r0 = "TypePublic"
        L_0x0209:
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r0, r11)
            goto L_0x021f
        L_0x020e:
            if (r11 == 0) goto L_0x0216
            r11 = 2131626900(0x7f0e0b94, float:1.888105E38)
            java.lang.String r0 = "TypePrivateGroup"
            goto L_0x021b
        L_0x0216:
            r11 = 2131626902(0x7f0e0b96, float:1.8881053E38)
            java.lang.String r0 = "TypePublicGroup"
        L_0x021b:
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r0, r11)
        L_0x021f:
            boolean r0 = r10.isChannel
            if (r0 == 0) goto L_0x0249
            org.telegram.ui.Cells.TextDetailCell r0 = r10.typeCell
            r4 = 2131624603(0x7f0e029b, float:1.887639E38)
            java.lang.String r5 = "ChannelType"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.Cells.TextDetailCell r5 = r10.historyCell
            if (r5 == 0) goto L_0x0238
            int r5 = r5.getVisibility()
            if (r5 == 0) goto L_0x0242
        L_0x0238:
            org.telegram.ui.Cells.TextDetailCell r5 = r10.linkedCell
            if (r5 == 0) goto L_0x0244
            int r5 = r5.getVisibility()
            if (r5 != 0) goto L_0x0244
        L_0x0242:
            r5 = 1
            goto L_0x0245
        L_0x0244:
            r5 = 0
        L_0x0245:
            r0.setTextAndValue(r4, r11, r5)
            goto L_0x026e
        L_0x0249:
            org.telegram.ui.Cells.TextDetailCell r0 = r10.typeCell
            r4 = 2131625309(0x7f0e055d, float:1.8877822E38)
            java.lang.String r5 = "GroupType"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.Cells.TextDetailCell r5 = r10.historyCell
            if (r5 == 0) goto L_0x025e
            int r5 = r5.getVisibility()
            if (r5 == 0) goto L_0x0268
        L_0x025e:
            org.telegram.ui.Cells.TextDetailCell r5 = r10.linkedCell
            if (r5 == 0) goto L_0x026a
            int r5 = r5.getVisibility()
            if (r5 != 0) goto L_0x026a
        L_0x0268:
            r5 = 1
            goto L_0x026b
        L_0x026a:
            r5 = 0
        L_0x026b:
            r0.setTextAndValue(r4, r11, r5)
        L_0x026e:
            org.telegram.tgnet.TLRPC$ChatFull r11 = r10.info
            if (r11 == 0) goto L_0x0297
            org.telegram.ui.Cells.TextDetailCell r11 = r10.historyCell
            if (r11 == 0) goto L_0x0297
            boolean r11 = r10.historyHidden
            if (r11 == 0) goto L_0x0280
            r11 = 2131624632(0x7f0e02b8, float:1.887645E38)
            java.lang.String r0 = "ChatHistoryHidden"
            goto L_0x0285
        L_0x0280:
            r11 = 2131624635(0x7f0e02bb, float:1.8876455E38)
            java.lang.String r0 = "ChatHistoryVisible"
        L_0x0285:
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r0, r11)
            org.telegram.ui.Cells.TextDetailCell r0 = r10.historyCell
            r4 = 2131624631(0x7f0e02b7, float:1.8876447E38)
            java.lang.String r5 = "ChatHistory"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setTextAndValue(r4, r11, r2)
        L_0x0297:
            org.telegram.ui.Cells.TextSettingsCell r11 = r10.stickersCell
            r0 = 2131625307(0x7f0e055b, float:1.8877818E38)
            java.lang.String r4 = "GroupStickers"
            if (r11 == 0) goto L_0x02bb
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.info
            org.telegram.tgnet.TLRPC$StickerSet r5 = r5.stickerset
            if (r5 == 0) goto L_0x02b4
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r4, r0)
            org.telegram.tgnet.TLRPC$ChatFull r6 = r10.info
            org.telegram.tgnet.TLRPC$StickerSet r6 = r6.stickerset
            java.lang.String r6 = r6.title
            r11.setTextAndValue(r5, r6, r2)
            goto L_0x02bb
        L_0x02b4:
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r11.setText(r5, r2)
        L_0x02bb:
            org.telegram.ui.Cells.TextCell r11 = r10.membersCell
            if (r11 == 0) goto L_0x048e
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.info
            r6 = 2131624555(0x7f0e026b, float:1.8876293E38)
            java.lang.String r7 = "ChannelMembers"
            r8 = 2131165255(0x7var_, float:1.7944722E38)
            if (r5 == 0) goto L_0x0429
            boolean r5 = r10.isChannel
            java.lang.String r9 = "%d"
            if (r5 == 0) goto L_0x0323
            r1 = 2131624599(0x7f0e0297, float:1.8876382E38)
            java.lang.String r5 = "ChannelSubscribers"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            java.lang.Object[] r5 = new java.lang.Object[r3]
            org.telegram.tgnet.TLRPC$ChatFull r6 = r10.info
            int r6 = r6.participants_count
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r2] = r6
            java.lang.String r5 = java.lang.String.format(r9, r5)
            r11.setTextAndValueAndIcon(r1, r5, r8, r3)
            org.telegram.ui.Cells.TextCell r11 = r10.blockCell
            r1 = 2131624525(0x7f0e024d, float:1.8876232E38)
            java.lang.String r5 = "ChannelBlacklist"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            java.lang.Object[] r5 = new java.lang.Object[r3]
            org.telegram.tgnet.TLRPC$ChatFull r6 = r10.info
            int r7 = r6.banned_count
            int r6 = r6.kicked_count
            int r6 = java.lang.Math.max(r7, r6)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r2] = r6
            java.lang.String r5 = java.lang.String.format(r9, r5)
            r6 = 2131165253(0x7var_, float:1.7944718E38)
            org.telegram.ui.Cells.TextCell r7 = r10.logCell
            if (r7 == 0) goto L_0x031d
            int r7 = r7.getVisibility()
            if (r7 != 0) goto L_0x031d
            r7 = 1
            goto L_0x031e
        L_0x031d:
            r7 = 0
        L_0x031e:
            r11.setTextAndValueAndIcon(r1, r5, r6, r7)
            goto L_0x03fa
        L_0x0323:
            org.telegram.tgnet.TLRPC$Chat r11 = r10.currentChat
            boolean r11 = org.telegram.messenger.ChatObject.isChannel(r11)
            if (r11 == 0) goto L_0x0352
            org.telegram.ui.Cells.TextCell r11 = r10.membersCell
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.Object[] r6 = new java.lang.Object[r3]
            org.telegram.tgnet.TLRPC$ChatFull r7 = r10.info
            int r7 = r7.participants_count
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r6[r2] = r7
            java.lang.String r6 = java.lang.String.format(r9, r6)
            org.telegram.ui.Cells.TextCell r7 = r10.logCell
            if (r7 == 0) goto L_0x034d
            int r7 = r7.getVisibility()
            if (r7 != 0) goto L_0x034d
            r7 = 1
            goto L_0x034e
        L_0x034d:
            r7 = 0
        L_0x034e:
            r11.setTextAndValueAndIcon(r5, r6, r8, r7)
            goto L_0x037e
        L_0x0352:
            org.telegram.ui.Cells.TextCell r11 = r10.membersCell
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.Object[] r6 = new java.lang.Object[r3]
            org.telegram.tgnet.TLRPC$ChatFull r7 = r10.info
            org.telegram.tgnet.TLRPC$ChatParticipants r7 = r7.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r7 = r7.participants
            int r7 = r7.size()
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r6[r2] = r7
            java.lang.String r6 = java.lang.String.format(r9, r6)
            org.telegram.ui.Cells.TextCell r7 = r10.logCell
            if (r7 == 0) goto L_0x037a
            int r7 = r7.getVisibility()
            if (r7 != 0) goto L_0x037a
            r7 = 1
            goto L_0x037b
        L_0x037a:
            r7 = 0
        L_0x037b:
            r11.setTextAndValueAndIcon(r5, r6, r8, r7)
        L_0x037e:
            org.telegram.tgnet.TLRPC$Chat r11 = r10.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r11 = r11.default_banned_rights
            if (r11 == 0) goto L_0x03d2
            boolean r11 = r11.send_stickers
            if (r11 != 0) goto L_0x038a
            r11 = 1
            goto L_0x038b
        L_0x038a:
            r11 = 0
        L_0x038b:
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r5.default_banned_rights
            boolean r5 = r5.send_media
            if (r5 != 0) goto L_0x0395
            int r11 = r11 + 1
        L_0x0395:
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r5.default_banned_rights
            boolean r5 = r5.embed_links
            if (r5 != 0) goto L_0x039f
            int r11 = r11 + 1
        L_0x039f:
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r5.default_banned_rights
            boolean r5 = r5.send_messages
            if (r5 != 0) goto L_0x03a9
            int r11 = r11 + 1
        L_0x03a9:
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r5.default_banned_rights
            boolean r5 = r5.pin_messages
            if (r5 != 0) goto L_0x03b3
            int r11 = r11 + 1
        L_0x03b3:
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r5.default_banned_rights
            boolean r5 = r5.send_polls
            if (r5 != 0) goto L_0x03bd
            int r11 = r11 + 1
        L_0x03bd:
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r5.default_banned_rights
            boolean r5 = r5.invite_users
            if (r5 != 0) goto L_0x03c7
            int r11 = r11 + 1
        L_0x03c7:
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r5.default_banned_rights
            boolean r5 = r5.change_info
            if (r5 != 0) goto L_0x03d4
            int r11 = r11 + 1
            goto L_0x03d4
        L_0x03d2:
            r11 = 8
        L_0x03d4:
            org.telegram.ui.Cells.TextCell r5 = r10.blockCell
            r6 = 2131624579(0x7f0e0283, float:1.8876342E38)
            java.lang.String r7 = "ChannelPermissions"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r7[r2] = r11
            java.lang.Integer r11 = java.lang.Integer.valueOf(r1)
            r7[r3] = r11
            java.lang.String r11 = "%d/%d"
            java.lang.String r11 = java.lang.String.format(r11, r7)
            r1 = 2131165251(0x7var_, float:1.7944714E38)
            r5.setTextAndValueAndIcon(r6, r11, r1, r3)
        L_0x03fa:
            org.telegram.ui.Cells.TextCell r11 = r10.adminCell
            r1 = 2131624520(0x7f0e0248, float:1.8876222E38)
            java.lang.String r5 = "ChannelAdministrators"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            java.lang.Object[] r5 = new java.lang.Object[r3]
            org.telegram.tgnet.TLRPC$Chat r6 = r10.currentChat
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r6 == 0) goto L_0x0414
            org.telegram.tgnet.TLRPC$ChatFull r6 = r10.info
            int r6 = r6.admins_count
            goto L_0x0418
        L_0x0414:
            int r6 = r10.getAdminCount()
        L_0x0418:
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r2] = r6
            java.lang.String r5 = java.lang.String.format(r9, r5)
            r6 = 2131165247(0x7var_f, float:1.7944706E38)
            r11.setTextAndValueAndIcon(r1, r5, r6, r3)
            goto L_0x048e
        L_0x0429:
            boolean r1 = r10.isChannel
            if (r1 == 0) goto L_0x0458
            r1 = 2131624599(0x7f0e0297, float:1.8876382E38)
            java.lang.String r5 = "ChannelSubscribers"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r11.setTextAndIcon((java.lang.String) r1, (int) r8, (boolean) r3)
            org.telegram.ui.Cells.TextCell r11 = r10.blockCell
            r1 = 2131624525(0x7f0e024d, float:1.8876232E38)
            java.lang.String r5 = "ChannelBlacklist"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r5 = 2131165253(0x7var_, float:1.7944718E38)
            org.telegram.ui.Cells.TextCell r6 = r10.logCell
            if (r6 == 0) goto L_0x0453
            int r6 = r6.getVisibility()
            if (r6 != 0) goto L_0x0453
            r6 = 1
            goto L_0x0454
        L_0x0453:
            r6 = 0
        L_0x0454:
            r11.setTextAndIcon((java.lang.String) r1, (int) r5, (boolean) r6)
            goto L_0x047d
        L_0x0458:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.Cells.TextCell r5 = r10.logCell
            if (r5 == 0) goto L_0x0468
            int r5 = r5.getVisibility()
            if (r5 != 0) goto L_0x0468
            r5 = 1
            goto L_0x0469
        L_0x0468:
            r5 = 0
        L_0x0469:
            r11.setTextAndIcon((java.lang.String) r1, (int) r8, (boolean) r5)
            org.telegram.ui.Cells.TextCell r11 = r10.blockCell
            r1 = 2131624579(0x7f0e0283, float:1.8876342E38)
            java.lang.String r5 = "ChannelPermissions"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r5 = 2131165251(0x7var_, float:1.7944714E38)
            r11.setTextAndIcon((java.lang.String) r1, (int) r5, (boolean) r3)
        L_0x047d:
            org.telegram.ui.Cells.TextCell r11 = r10.adminCell
            r1 = 2131624520(0x7f0e0248, float:1.8876222E38)
            java.lang.String r5 = "ChannelAdministrators"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r5 = 2131165247(0x7var_f, float:1.7944706E38)
            r11.setTextAndIcon((java.lang.String) r1, (int) r5, (boolean) r3)
        L_0x048e:
            org.telegram.ui.Cells.TextSettingsCell r11 = r10.stickersCell
            if (r11 == 0) goto L_0x04af
            org.telegram.tgnet.TLRPC$ChatFull r1 = r10.info
            if (r1 == 0) goto L_0x04af
            org.telegram.tgnet.TLRPC$StickerSet r1 = r1.stickerset
            if (r1 == 0) goto L_0x04a8
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            org.telegram.tgnet.TLRPC$ChatFull r1 = r10.info
            org.telegram.tgnet.TLRPC$StickerSet r1 = r1.stickerset
            java.lang.String r1 = r1.title
            r11.setTextAndValue(r0, r1, r2)
            goto L_0x04af
        L_0x04a8:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            r11.setText(r0, r2)
        L_0x04af:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.updateFields(boolean):void");
    }

    public ThemeDescription[] getThemeDescriptions() {
        $$Lambda$ChatEditActivity$HxxxUSJLb6eQcLNiQbebCuwv8Vw r9 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ChatEditActivity.this.lambda$getThemeDescriptions$23$ChatEditActivity();
            }
        };
        $$Lambda$ChatEditActivity$HxxxUSJLb6eQcLNiQbebCuwv8Vw r7 = r9;
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.membersCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription((View) this.membersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.membersCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"), new ThemeDescription(this.adminCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription((View) this.adminCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.adminCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"), new ThemeDescription(this.blockCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription((View) this.blockCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.blockCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"), new ThemeDescription(this.logCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription((View) this.logCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.logCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"), new ThemeDescription(this.typeCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription((View) this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.historyCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription((View) this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.locationCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription((View) this.locationCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.locationCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.avatarContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.settingsContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.typeEditContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.deleteContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.stickersContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.infoContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.settingsTopSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription(this.settingsSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription(this.deleteInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription(this.infoSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription(this.signCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription((View) this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"), new ThemeDescription((View) this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"), new ThemeDescription(this.deleteCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription((View) this.deleteCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"), new ThemeDescription(this.stickersCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription((View) this.stickersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.stickersInfoCell3, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.stickersInfoCell3, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, r7, "avatar_text"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundRed"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundOrange"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundViolet"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundGreen"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundCyan"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundBlue"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundPink")};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$23$ChatEditActivity() {
        if (this.avatarImage != null) {
            this.avatarDrawable.setInfo(5, (String) null, (String) null);
            this.avatarImage.invalidate();
        }
    }
}
