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
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.ChannelLocation;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_channelLocation;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_chatParticipantCreator;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
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
import org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

public class ChatEditActivity extends BaseFragment implements ImageUpdaterDelegate, NotificationCenterDelegate {
    private static final int done_button = 1;
    private TextCell adminCell;
    private FileLocation avatar;
    private AnimatorSet avatarAnimation;
    private FileLocation avatarBig;
    private LinearLayout avatarContainer;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private ImageView avatarEditor;
    private BackupImageView avatarImage;
    private View avatarOverlay;
    private RadialProgressView avatarProgressView;
    private TextCell blockCell;
    private int chatId;
    private boolean createAfterUpload;
    private Chat currentChat;
    private TextSettingsCell deleteCell;
    private FrameLayout deleteContainer;
    private ShadowSectionCell deleteInfoCell;
    private EditTextBoldCursor descriptionTextView;
    private View doneButton;
    private boolean donePressed;
    private TextDetailCell historyCell;
    private boolean historyHidden;
    private ImageUpdater imageUpdater = new ImageUpdater();
    private ChatFull info;
    private LinearLayout infoContainer;
    private ShadowSectionCell infoSectionCell;
    private boolean isChannel;
    private TextDetailCell linkedCell;
    private TextDetailCell locationCell;
    private TextCell logCell;
    private TextCell membersCell;
    private EditTextEmoji nameTextView;
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
    private InputFile uploadedAvatar;

    public ChatEditActivity(Bundle bundle) {
        super(bundle);
        this.chatId = bundle.getInt("chat_id", 0);
    }

    /* JADX WARNING: Missing block: B:7:0x004e, code skipped:
            if (r5.info == null) goto L_0x0050;
     */
    public boolean onFragmentCreate() {
        /*
        r5 = this;
        r0 = r5.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r1 = r5.chatId;
        r1 = java.lang.Integer.valueOf(r1);
        r0 = r0.getChat(r1);
        r5.currentChat = r0;
        r0 = r5.currentChat;
        r1 = 1;
        r2 = 0;
        if (r0 != 0) goto L_0x0051;
    L_0x0018:
        r0 = r5.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r3 = r5.chatId;
        r0 = r0.getChatSync(r3);
        r5.currentChat = r0;
        r0 = r5.currentChat;
        if (r0 == 0) goto L_0x0050;
    L_0x002a:
        r0 = r5.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r3 = r5.currentChat;
        r0.putChat(r3, r1);
        r0 = r5.info;
        if (r0 != 0) goto L_0x0051;
    L_0x0039:
        r0 = r5.currentAccount;
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r3 = r5.chatId;
        r4 = new java.util.concurrent.CountDownLatch;
        r4.<init>(r1);
        r0 = r0.loadChatInfo(r3, r4, r2, r2);
        r5.info = r0;
        r0 = r5.info;
        if (r0 != 0) goto L_0x0051;
    L_0x0050:
        return r2;
    L_0x0051:
        r0 = r5.currentChat;
        r0 = org.telegram.messenger.ChatObject.isChannel(r0);
        if (r0 == 0) goto L_0x0060;
    L_0x0059:
        r0 = r5.currentChat;
        r0 = r0.megagroup;
        if (r0 != 0) goto L_0x0060;
    L_0x005f:
        goto L_0x0061;
    L_0x0060:
        r1 = 0;
    L_0x0061:
        r5.isChannel = r1;
        r0 = r5.imageUpdater;
        r0.parentFragment = r5;
        r0.delegate = r5;
        r0 = r5.currentChat;
        r0 = r0.signatures;
        r5.signMessages = r0;
        r0 = r5.currentAccount;
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r1 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad;
        r0.addObserver(r5, r1);
        r0 = super.onFragmentCreate();
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.onFragmentCreate():boolean");
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.clear();
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
        ChatFull chatFull;
        int i;
        Context context2 = context;
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
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
        AnonymousClass2 anonymousClass2 = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                int size = MeasureSpec.getSize(i);
                int size2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                size2 -= getPaddingTop();
                measureChildWithMargins(ChatEditActivity.this.actionBar, i, 0, i2, 0);
                int i3 = 0;
                if (getKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                    this.ignoreLayout = true;
                    ChatEditActivity.this.nameTextView.hideEmojiView();
                    this.ignoreLayout = false;
                }
                int childCount = getChildCount();
                while (i3 < childCount) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8 || childAt == ChatEditActivity.this.actionBar)) {
                        if (ChatEditActivity.this.nameTextView == null || !ChatEditActivity.this.nameTextView.isPopupView(childAt)) {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec((size2 - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                    i3++;
                }
            }

            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Removed duplicated region for block: B:41:0x00bc  */
            /* JADX WARNING: Removed duplicated region for block: B:40:0x00b3  */
            /* JADX WARNING: Removed duplicated region for block: B:32:0x008c  */
            /* JADX WARNING: Removed duplicated region for block: B:25:0x0072  */
            /* JADX WARNING: Removed duplicated region for block: B:40:0x00b3  */
            /* JADX WARNING: Removed duplicated region for block: B:41:0x00bc  */
            public void onLayout(boolean r10, int r11, int r12, int r13, int r14) {
                /*
                r9 = this;
                r10 = r9.getChildCount();
                r0 = r9.getKeyboardHeight();
                r1 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r2 = 0;
                if (r0 > r1) goto L_0x0026;
            L_0x0011:
                r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow;
                if (r0 != 0) goto L_0x0026;
            L_0x0015:
                r0 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r0 != 0) goto L_0x0026;
            L_0x001b:
                r0 = org.telegram.ui.ChatEditActivity.this;
                r0 = r0.nameTextView;
                r0 = r0.getEmojiPadding();
                goto L_0x0027;
            L_0x0026:
                r0 = 0;
            L_0x0027:
                r9.setBottomClip(r0);
            L_0x002a:
                if (r2 >= r10) goto L_0x00d3;
            L_0x002c:
                r1 = r9.getChildAt(r2);
                r3 = r1.getVisibility();
                r4 = 8;
                if (r3 != r4) goto L_0x003a;
            L_0x0038:
                goto L_0x00cf;
            L_0x003a:
                r3 = r1.getLayoutParams();
                r3 = (android.widget.FrameLayout.LayoutParams) r3;
                r4 = r1.getMeasuredWidth();
                r5 = r1.getMeasuredHeight();
                r6 = r3.gravity;
                r7 = -1;
                if (r6 != r7) goto L_0x004f;
            L_0x004d:
                r6 = 51;
            L_0x004f:
                r7 = r6 & 7;
                r6 = r6 & 112;
                r7 = r7 & 7;
                r8 = 1;
                if (r7 == r8) goto L_0x0063;
            L_0x0058:
                r8 = 5;
                if (r7 == r8) goto L_0x005e;
            L_0x005b:
                r7 = r3.leftMargin;
                goto L_0x006e;
            L_0x005e:
                r7 = r13 - r4;
                r8 = r3.rightMargin;
                goto L_0x006d;
            L_0x0063:
                r7 = r13 - r11;
                r7 = r7 - r4;
                r7 = r7 / 2;
                r8 = r3.leftMargin;
                r7 = r7 + r8;
                r8 = r3.rightMargin;
            L_0x006d:
                r7 = r7 - r8;
            L_0x006e:
                r8 = 16;
                if (r6 == r8) goto L_0x008c;
            L_0x0072:
                r8 = 48;
                if (r6 == r8) goto L_0x0084;
            L_0x0076:
                r8 = 80;
                if (r6 == r8) goto L_0x007d;
            L_0x007a:
                r3 = r3.topMargin;
                goto L_0x0099;
            L_0x007d:
                r6 = r14 - r0;
                r6 = r6 - r12;
                r6 = r6 - r5;
                r3 = r3.bottomMargin;
                goto L_0x0097;
            L_0x0084:
                r3 = r3.topMargin;
                r6 = r9.getPaddingTop();
                r3 = r3 + r6;
                goto L_0x0099;
            L_0x008c:
                r6 = r14 - r0;
                r6 = r6 - r12;
                r6 = r6 - r5;
                r6 = r6 / 2;
                r8 = r3.topMargin;
                r6 = r6 + r8;
                r3 = r3.bottomMargin;
            L_0x0097:
                r3 = r6 - r3;
            L_0x0099:
                r6 = org.telegram.ui.ChatEditActivity.this;
                r6 = r6.nameTextView;
                if (r6 == 0) goto L_0x00ca;
            L_0x00a1:
                r6 = org.telegram.ui.ChatEditActivity.this;
                r6 = r6.nameTextView;
                r6 = r6.isPopupView(r1);
                if (r6 == 0) goto L_0x00ca;
            L_0x00ad:
                r3 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r3 == 0) goto L_0x00bc;
            L_0x00b3:
                r3 = r9.getMeasuredHeight();
                r6 = r1.getMeasuredHeight();
                goto L_0x00c9;
            L_0x00bc:
                r3 = r9.getMeasuredHeight();
                r6 = r9.getKeyboardHeight();
                r3 = r3 + r6;
                r6 = r1.getMeasuredHeight();
            L_0x00c9:
                r3 = r3 - r6;
            L_0x00ca:
                r4 = r4 + r7;
                r5 = r5 + r3;
                r1.layout(r7, r3, r4, r5);
            L_0x00cf:
                r2 = r2 + 1;
                goto L_0x002a;
            L_0x00d3:
                r9.notifyHeightChanged();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity$AnonymousClass2.onLayout(boolean, int, int, int, int):void");
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        anonymousClass2.setOnTouchListener(-$$Lambda$ChatEditActivity$P4nSuGWkKCrAaBVoDH4QiQu-r0s.INSTANCE);
        this.fragmentView = anonymousClass2;
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        ScrollView scrollView = new ScrollView(context2);
        scrollView.setFillViewport(true);
        anonymousClass2.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        scrollView.addView(linearLayout, new LayoutParams(-1, -2));
        linearLayout.setOrientation(1);
        this.actionBar.setTitle(LocaleController.getString("ChannelEdit", NUM));
        this.avatarContainer = new LinearLayout(context2);
        this.avatarContainer.setOrientation(1);
        String str = "windowBackgroundWhite";
        this.avatarContainer.setBackgroundColor(Theme.getColor(str));
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
            this.avatarDrawable.setInfo(5, null, null);
            final Paint paint = new Paint(1);
            paint.setColor(NUM);
            this.avatarOverlay = new View(context2) {
                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    if (ChatEditActivity.this.avatarImage != null && ChatEditActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                        paint.setAlpha((int) (ChatEditActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                        canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(32.0f), paint);
                    }
                }
            };
            frameLayout.addView(this.avatarOverlay, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            this.avatarOverlay.setOnClickListener(new -$$Lambda$ChatEditActivity$e25giWs801dRMAXxXii-6SnZpz8(this));
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
            this.avatarEditor.setScaleType(ScaleType.CENTER);
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
            this.avatarDrawable.setInfo(5, this.currentChat.title, null);
        }
        this.nameTextView = new EditTextEmoji(context2, anonymousClass2, this, 0);
        if (this.isChannel) {
            this.nameTextView.setHint(LocaleController.getString("EnterChannelName", NUM));
        } else {
            this.nameTextView.setHint(LocaleController.getString("GroupName", NUM));
        }
        this.nameTextView.setEnabled(ChatObject.canChangeChatInfo(this.currentChat));
        editTextEmoji = this.nameTextView;
        editTextEmoji.setFocusable(editTextEmoji.isEnabled());
        this.nameTextView.setFilters(new InputFilter[]{new LengthFilter(128)});
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 16, LocaleController.isRTL ? 5.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 5.0f, 0.0f));
        this.settingsContainer = new LinearLayout(context2);
        this.settingsContainer.setOrientation(1);
        this.settingsContainer.setBackgroundColor(Theme.getColor(str));
        linearLayout.addView(this.settingsContainer, LayoutHelper.createLinear(-1, -2));
        this.descriptionTextView = new EditTextBoldCursor(context2);
        this.descriptionTextView.setTextSize(1, 16.0f);
        this.descriptionTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        String str2 = "windowBackgroundWhiteBlackText";
        this.descriptionTextView.setTextColor(Theme.getColor(str2));
        this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
        this.descriptionTextView.setBackgroundDrawable(null);
        EditTextBoldCursor editTextBoldCursor = this.descriptionTextView;
        if (!LocaleController.isRTL) {
            i2 = 3;
        }
        editTextBoldCursor.setGravity(i2);
        this.descriptionTextView.setInputType(180225);
        this.descriptionTextView.setImeOptions(6);
        this.descriptionTextView.setEnabled(ChatObject.canChangeChatInfo(this.currentChat));
        editTextBoldCursor = this.descriptionTextView;
        editTextBoldCursor.setFocusable(editTextBoldCursor.isEnabled());
        this.descriptionTextView.setFilters(new InputFilter[]{new LengthFilter(255)});
        this.descriptionTextView.setHint(LocaleController.getString("DescriptionOptionalPlaceholder", NUM));
        this.descriptionTextView.setCursorColor(Theme.getColor(str2));
        this.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        this.descriptionTextView.setCursorWidth(1.5f);
        this.settingsContainer.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 23.0f, 12.0f, 23.0f, 6.0f));
        this.descriptionTextView.setOnEditorActionListener(new -$$Lambda$ChatEditActivity$TnZLSoKgOlebKNu-vTumz0jfp9s(this));
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
        this.typeEditContainer.setBackgroundColor(Theme.getColor(str));
        linearLayout.addView(this.typeEditContainer, LayoutHelper.createLinear(-1, -2));
        if (this.currentChat.megagroup) {
            chatFull = this.info;
            if (chatFull == null || chatFull.can_set_location) {
                this.locationCell = new TextDetailCell(context2);
                this.locationCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                this.typeEditContainer.addView(this.locationCell, LayoutHelper.createLinear(-1, -2));
                this.locationCell.setOnClickListener(new -$$Lambda$ChatEditActivity$takgJ7d_dj5vza0E_4qO74BhrTA(this));
            }
        }
        if (this.currentChat.creator) {
            chatFull = this.info;
            if (chatFull == null || chatFull.can_set_username) {
                this.typeCell = new TextDetailCell(context2);
                this.typeCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                this.typeEditContainer.addView(this.typeCell, LayoutHelper.createLinear(-1, -2));
                this.typeCell.setOnClickListener(new -$$Lambda$ChatEditActivity$vs7xjVOaqM3gt8vxvzKAx_LFF8w(this));
            }
        }
        if (ChatObject.isChannel(this.currentChat) && ((this.isChannel && ChatObject.canUserDoAdminAction(this.currentChat, 1)) || (!this.isChannel && ChatObject.canUserDoAdminAction(this.currentChat, 0)))) {
            this.linkedCell = new TextDetailCell(context2);
            this.linkedCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.linkedCell, LayoutHelper.createLinear(-1, -2));
            this.linkedCell.setOnClickListener(new -$$Lambda$ChatEditActivity$7pTxutCXFS3I-dEFQEtx3V3MoNY(this));
        }
        if (!this.isChannel && ChatObject.canBlockUsers(this.currentChat) && (ChatObject.isChannel(this.currentChat) || this.currentChat.creator)) {
            this.historyCell = new TextDetailCell(context2);
            this.historyCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.historyCell, LayoutHelper.createLinear(-1, -2));
            this.historyCell.setOnClickListener(new -$$Lambda$ChatEditActivity$09TV-rCS9LlBhDCDNwCIiuSpx5I(this, context2));
        }
        if (this.isChannel) {
            this.signCell = new TextCheckCell(context2);
            this.signCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.signCell.setTextAndValueAndCheck(LocaleController.getString("ChannelSignMessages", NUM), LocaleController.getString("ChannelSignMessagesInfo", NUM), this.signMessages, true, false);
            this.typeEditContainer.addView(this.signCell, LayoutHelper.createFrame(-1, -2.0f));
            this.signCell.setOnClickListener(new -$$Lambda$ChatEditActivity$Z_VSyPell-FXQ74xw_1QaAWQHLA(this));
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
        this.infoContainer.setBackgroundColor(Theme.getColor(str));
        linearLayout.addView(this.infoContainer, LayoutHelper.createLinear(-1, -2));
        this.blockCell = new TextCell(context2);
        this.blockCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        TextCell textCell = this.blockCell;
        int i3 = (ChatObject.isChannel(this.currentChat) || this.currentChat.creator) ? 0 : 8;
        textCell.setVisibility(i3);
        this.blockCell.setOnClickListener(new -$$Lambda$ChatEditActivity$BW8nfxB2gbGLRBoiaMPR3BQCdjM(this));
        this.adminCell = new TextCell(context2);
        this.adminCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.adminCell.setOnClickListener(new -$$Lambda$ChatEditActivity$R8nmnQgVpQwtOgAwCTLlWo2fY0k(this));
        this.membersCell = new TextCell(context2);
        this.membersCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.membersCell.setOnClickListener(new -$$Lambda$ChatEditActivity$-_qhLtKqz7XJ52ia47Bo4pX1C7s(this));
        if (ChatObject.isChannel(this.currentChat)) {
            this.logCell = new TextCell(context2);
            this.logCell.setTextAndIcon(LocaleController.getString("EventLog", NUM), NUM, false);
            this.logCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.logCell.setOnClickListener(new -$$Lambda$ChatEditActivity$51Cw78hSbx5h61mWEeKV8Wy14wg(this));
        }
        if (this.isChannel) {
            i = -2;
        } else {
            i = -2;
            this.infoContainer.addView(this.blockCell, LayoutHelper.createLinear(-1, -2));
        }
        this.infoContainer.addView(this.adminCell, LayoutHelper.createLinear(-1, i));
        this.infoContainer.addView(this.membersCell, LayoutHelper.createLinear(-1, i));
        if (this.isChannel) {
            this.infoContainer.addView(this.blockCell, LayoutHelper.createLinear(-1, i));
        }
        textCell = this.logCell;
        if (textCell != null) {
            this.infoContainer.addView(textCell, LayoutHelper.createLinear(-1, i));
        }
        this.infoSectionCell = new ShadowSectionCell(context2);
        linearLayout.addView(this.infoSectionCell, LayoutHelper.createLinear(-1, i));
        if (!ChatObject.hasAdminRights(this.currentChat)) {
            this.infoContainer.setVisibility(8);
            this.settingsTopSectionCell.setVisibility(8);
        }
        if (!this.isChannel) {
            chatFull = this.info;
            if (chatFull != null && chatFull.can_set_stickers) {
                this.stickersContainer = new FrameLayout(context2);
                this.stickersContainer.setBackgroundColor(Theme.getColor(str));
                linearLayout.addView(this.stickersContainer, LayoutHelper.createLinear(-1, -2));
                this.stickersCell = new TextSettingsCell(context2);
                this.stickersCell.setTextColor(Theme.getColor(str2));
                this.stickersCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                this.stickersContainer.addView(this.stickersCell, LayoutHelper.createFrame(-1, -2.0f));
                this.stickersCell.setOnClickListener(new -$$Lambda$ChatEditActivity$9jffPBh2wTbtBLkhgMaFTjxV_p8(this));
                this.stickersInfoCell3 = new TextInfoPrivacyCell(context2);
                this.stickersInfoCell3.setText(LocaleController.getString("GroupStickersInfo", NUM));
                linearLayout.addView(this.stickersInfoCell3, LayoutHelper.createLinear(-1, -2));
            }
        }
        str2 = "windowBackgroundGrayShadow";
        if (this.currentChat.creator) {
            this.deleteContainer = new FrameLayout(context2);
            this.deleteContainer.setBackgroundColor(Theme.getColor(str));
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
            this.deleteCell.setOnClickListener(new -$$Lambda$ChatEditActivity$mqbBMd-ZFUEPPnbCelSVFNiZ_78(this));
            this.deleteInfoCell = new ShadowSectionCell(context2);
            this.deleteInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str2));
            linearLayout.addView(this.deleteInfoCell, LayoutHelper.createLinear(-1, -2));
        } else if (!this.isChannel && this.stickersInfoCell3 == null) {
            this.infoSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str2));
        }
        TextInfoPrivacyCell textInfoPrivacyCell = this.stickersInfoCell3;
        if (textInfoPrivacyCell != null) {
            if (this.deleteInfoCell == null) {
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str2));
            } else {
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str2));
            }
        }
        this.nameTextView.setText(this.currentChat.title);
        EditTextEmoji editTextEmoji2 = this.nameTextView;
        editTextEmoji2.setSelection(editTextEmoji2.length());
        ChatFull chatFull2 = this.info;
        if (chatFull2 != null) {
            this.descriptionTextView.setText(chatFull2.about);
        }
        Chat chat = this.currentChat;
        ChatPhoto chatPhoto = chat.photo;
        if (chatPhoto != null) {
            this.avatar = chatPhoto.photo_small;
            this.avatarBig = chatPhoto.photo_big;
            this.avatarImage.setImage(ImageLocation.getForChat(chat, false), "50_50", this.avatarDrawable, this.currentChat);
        } else {
            this.avatarImage.setImageDrawable(this.avatarDrawable);
        }
        updateFields(true);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$ChatEditActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new -$$Lambda$ChatEditActivity$f_8oUZZhQBkN64UOvBwDk_25NR8(this));
    }

    public /* synthetic */ void lambda$null$1$ChatEditActivity() {
        this.avatar = null;
        this.avatarBig = null;
        this.uploadedAvatar = null;
        showAvatarProgress(false, true);
        this.avatarImage.setImage(null, null, this.avatarDrawable, this.currentChat);
    }

    public /* synthetic */ boolean lambda$createView$3$ChatEditActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 6) {
            View view = this.doneButton;
            if (view != null) {
                view.performClick();
                return true;
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$createView$5$ChatEditActivity(View view) {
        if (AndroidUtilities.isGoogleMapsInstalled(this)) {
            LocationActivity locationActivity = new LocationActivity(4);
            locationActivity.setDialogId((long) (-this.chatId));
            ChatFull chatFull = this.info;
            if (chatFull != null) {
                ChannelLocation channelLocation = chatFull.location;
                if (channelLocation instanceof TL_channelLocation) {
                    locationActivity.setInitialLocation((TL_channelLocation) channelLocation);
                }
            }
            locationActivity.setDelegate(new -$$Lambda$ChatEditActivity$p1iHQUCNbkBH3bG3cN0lfw9rr7s(this));
            presentFragment(locationActivity);
        }
    }

    public /* synthetic */ void lambda$null$4$ChatEditActivity(MessageMedia messageMedia, int i, boolean z, int i2) {
        TL_channelLocation tL_channelLocation = new TL_channelLocation();
        tL_channelLocation.address = messageMedia.address;
        tL_channelLocation.geo_point = messageMedia.geo;
        ChatFull chatFull = this.info;
        chatFull.location = tL_channelLocation;
        chatFull.flags |= 32768;
        updateFields(false);
        getMessagesController().loadFullChat(this.chatId, 0, true);
    }

    public /* synthetic */ void lambda$createView$6$ChatEditActivity(View view) {
        int i = this.chatId;
        TextDetailCell textDetailCell = this.locationCell;
        boolean z = textDetailCell != null && textDetailCell.getVisibility() == 0;
        ChatEditTypeActivity chatEditTypeActivity = new ChatEditTypeActivity(i, z);
        chatEditTypeActivity.setInfo(this.info);
        presentFragment(chatEditTypeActivity);
    }

    public /* synthetic */ void lambda$createView$7$ChatEditActivity(View view) {
        ChatLinkActivity chatLinkActivity = new ChatLinkActivity(this.chatId);
        chatLinkActivity.setInfo(this.info);
        presentFragment(chatLinkActivity);
    }

    public /* synthetic */ void lambda$createView$9$ChatEditActivity(Context context, View view) {
        Builder builder = new Builder(context);
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
                radioButtonCellArr[i].setTextAndValue(LocaleController.getString("ChatHistoryVisible", NUM), LocaleController.getString("ChatHistoryVisibleInfo", NUM), true, this.historyHidden ^ 1);
            } else {
                String str = "ChatHistoryHidden";
                if (ChatObject.isChannel(this.currentChat)) {
                    radioButtonCellArr[i].setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("ChatHistoryHiddenInfo", NUM), false, this.historyHidden);
                } else {
                    radioButtonCellArr[i].setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("ChatHistoryHiddenInfo2", NUM), false, this.historyHidden);
                }
            }
            linearLayout2.addView(radioButtonCellArr[i], LayoutHelper.createLinear(-1, -2));
            radioButtonCellArr[i].setOnClickListener(new -$$Lambda$ChatEditActivity$3pCHSyKy6HoSUauSbggX3F-8yYQ(this, radioButtonCellArr, builder));
        }
        builder.setCustomView(linearLayout);
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$null$8$ChatEditActivity(RadioButtonCell[] radioButtonCellArr, Builder builder, View view) {
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
        this.signMessages ^= 1;
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
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, true, false, this.currentChat, null, false, new -$$Lambda$ChatEditActivity$IJynWloQN__MO98JqQBGBgBHx7g(this));
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
        if (i == NotificationCenter.chatInfoDidLoad) {
            ChatFull chatFull = (ChatFull) objArr[0];
            if (chatFull.id == this.chatId) {
                if (this.info == null) {
                    EditTextBoldCursor editTextBoldCursor = this.descriptionTextView;
                    if (editTextBoldCursor != null) {
                        editTextBoldCursor.setText(chatFull.about);
                    }
                }
                this.info = chatFull;
                boolean z = !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory;
                this.historyHidden = z;
                updateFields(false);
            }
        }
    }

    public void didUploadPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChatEditActivity$Zc-Tjl3CSVWcZ1A1nWCXrjm6Yhc(this, inputFile, photoSize2, photoSize));
    }

    public /* synthetic */ void lambda$didUploadPhoto$18$ChatEditActivity(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        if (inputFile != null) {
            this.uploadedAvatar = inputFile;
            if (this.createAfterUpload) {
                try {
                    if (this.progressDialog != null && this.progressDialog.isShowing()) {
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
        this.avatar = photoSize.location;
        this.avatarBig = photoSize2.location;
        this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", this.avatarDrawable, this.currentChat);
        showAvatarProgress(true, false);
    }

    public String getInitialSearchString() {
        return this.nameTextView.getText().toString();
    }

    /* JADX WARNING: Missing block: B:3:0x0006, code skipped:
            if (r0 != null) goto L_0x000b;
     */
    private boolean checkDiscard() {
        /*
        r3 = this;
        r0 = r3.info;
        if (r0 == 0) goto L_0x0009;
    L_0x0004:
        r0 = r0.about;
        if (r0 == 0) goto L_0x0009;
    L_0x0008:
        goto L_0x000b;
    L_0x0009:
        r0 = "";
    L_0x000b:
        r1 = r3.info;
        if (r1 == 0) goto L_0x001f;
    L_0x000f:
        r1 = r3.currentChat;
        r1 = org.telegram.messenger.ChatObject.isChannel(r1);
        if (r1 == 0) goto L_0x001f;
    L_0x0017:
        r1 = r3.info;
        r1 = r1.hidden_prehistory;
        r2 = r3.historyHidden;
        if (r1 != r2) goto L_0x0066;
    L_0x001f:
        r1 = r3.imageUpdater;
        r1 = r1.uploadingImage;
        if (r1 != 0) goto L_0x0066;
    L_0x0025:
        r1 = r3.nameTextView;
        if (r1 == 0) goto L_0x003b;
    L_0x0029:
        r2 = r3.currentChat;
        r2 = r2.title;
        r1 = r1.getText();
        r1 = r1.toString();
        r1 = r2.equals(r1);
        if (r1 == 0) goto L_0x0066;
    L_0x003b:
        r1 = r3.descriptionTextView;
        if (r1 == 0) goto L_0x004d;
    L_0x003f:
        r1 = r1.getText();
        r1 = r1.toString();
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0066;
    L_0x004d:
        r0 = r3.signMessages;
        r1 = r3.currentChat;
        r2 = r1.signatures;
        if (r0 != r2) goto L_0x0066;
    L_0x0055:
        r0 = r3.uploadedAvatar;
        if (r0 != 0) goto L_0x0066;
    L_0x0059:
        r0 = r3.avatar;
        if (r0 != 0) goto L_0x0064;
    L_0x005d:
        r0 = r1.photo;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_chatPhoto;
        if (r0 == 0) goto L_0x0064;
    L_0x0063:
        goto L_0x0066;
    L_0x0064:
        r0 = 1;
        return r0;
    L_0x0066:
        r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r1 = r3.getParentActivity();
        r0.<init>(r1);
        r1 = NUM; // 0x7f0e0b71 float:1.8880978E38 double:1.0531636037E-314;
        r2 = "UserRestrictionsApplyChanges";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setTitle(r1);
        r1 = r3.isChannel;
        if (r1 == 0) goto L_0x008c;
    L_0x007f:
        r1 = NUM; // 0x7f0e0282 float:1.887634E38 double:1.053162474E-314;
        r2 = "ChannelSettingsChangedAlert";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setMessage(r1);
        goto L_0x0098;
    L_0x008c:
        r1 = NUM; // 0x7f0e0538 float:1.8877747E38 double:1.0531628167E-314;
        r2 = "GroupSettingsChangedAlert";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setMessage(r1);
    L_0x0098:
        r1 = NUM; // 0x7f0e00fc float:1.8875549E38 double:1.053162281E-314;
        r2 = "ApplyTheme";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r2 = new org.telegram.ui.-$$Lambda$ChatEditActivity$NBEr6CX4NZ1r3XbdnOXbearPc6k;
        r2.<init>(r3);
        r0.setPositiveButton(r1, r2);
        r1 = NUM; // 0x7f0e07d1 float:1.8879096E38 double:1.0531631453E-314;
        r2 = "PassportDiscard";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r2 = new org.telegram.ui.-$$Lambda$ChatEditActivity$TOgShEf6MXFD3VhufzCjnieIAt0;
        r2.<init>(r3);
        r0.setNegativeButton(r1, r2);
        r0 = r0.create();
        r3.showDialog(r0);
        r0 = 0;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.checkDiscard():boolean");
    }

    public /* synthetic */ void lambda$checkDiscard$19$ChatEditActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    public /* synthetic */ void lambda$checkDiscard$20$ChatEditActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    private int getAdminCount() {
        ChatFull chatFull = this.info;
        if (chatFull == null) {
            return 1;
        }
        int size = chatFull.participants.participants.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            ChatParticipant chatParticipant = (ChatParticipant) this.info.participants.participants.get(i2);
            if ((chatParticipant instanceof TL_chatParticipantAdmin) || (chatParticipant instanceof TL_chatParticipantCreator)) {
                i++;
            }
        }
        return i;
    }

    /* JADX WARNING: Missing block: B:34:0x00c7, code skipped:
            if (r1 != null) goto L_0x00cc;
     */
    private void processDone() {
        /*
        r5 = this;
        r0 = r5.donePressed;
        if (r0 != 0) goto L_0x013d;
    L_0x0004:
        r0 = r5.nameTextView;
        if (r0 != 0) goto L_0x000a;
    L_0x0008:
        goto L_0x013d;
    L_0x000a:
        r0 = r0.length();
        if (r0 != 0) goto L_0x002d;
    L_0x0010:
        r0 = r5.getParentActivity();
        r1 = "vibrator";
        r0 = r0.getSystemService(r1);
        r0 = (android.os.Vibrator) r0;
        if (r0 == 0) goto L_0x0024;
    L_0x001f:
        r1 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r0.vibrate(r1);
    L_0x0024:
        r0 = r5.nameTextView;
        r1 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r2 = 0;
        org.telegram.messenger.AndroidUtilities.shakeView(r0, r1, r2);
        return;
    L_0x002d:
        r0 = 1;
        r5.donePressed = r0;
        r1 = r5.currentChat;
        r1 = org.telegram.messenger.ChatObject.isChannel(r1);
        if (r1 != 0) goto L_0x0051;
    L_0x0038:
        r1 = r5.historyHidden;
        if (r1 != 0) goto L_0x0051;
    L_0x003c:
        r0 = r5.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r1 = r5.getParentActivity();
        r2 = r5.chatId;
        r3 = new org.telegram.ui.-$$Lambda$ChatEditActivity$aa_B5hJiMKKENUg343hyaLuvH4Y;
        r3.<init>(r5);
        r0.convertToMegaGroup(r1, r2, r5, r3);
        return;
    L_0x0051:
        r1 = r5.info;
        if (r1 == 0) goto L_0x0074;
    L_0x0055:
        r1 = r5.currentChat;
        r1 = org.telegram.messenger.ChatObject.isChannel(r1);
        if (r1 == 0) goto L_0x0074;
    L_0x005d:
        r1 = r5.info;
        r2 = r1.hidden_prehistory;
        r3 = r5.historyHidden;
        if (r2 == r3) goto L_0x0074;
    L_0x0065:
        r1.hidden_prehistory = r3;
        r1 = r5.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = r5.chatId;
        r3 = r5.historyHidden;
        r1.toogleChannelInvitesHistory(r2, r3);
    L_0x0074:
        r1 = r5.imageUpdater;
        r1 = r1.uploadingImage;
        if (r1 == 0) goto L_0x0098;
    L_0x007a:
        r5.createAfterUpload = r0;
        r0 = new org.telegram.ui.ActionBar.AlertDialog;
        r1 = r5.getParentActivity();
        r2 = 3;
        r0.<init>(r1, r2);
        r5.progressDialog = r0;
        r0 = r5.progressDialog;
        r1 = new org.telegram.ui.-$$Lambda$ChatEditActivity$2WjVM2RAUq-UrGGHPwPwkCVtYvs;
        r1.<init>(r5);
        r0.setOnCancelListener(r1);
        r0 = r5.progressDialog;
        r0.show();
        return;
    L_0x0098:
        r1 = r5.currentChat;
        r1 = r1.title;
        r2 = r5.nameTextView;
        r2 = r2.getText();
        r2 = r2.toString();
        r1 = r1.equals(r2);
        if (r1 != 0) goto L_0x00c1;
    L_0x00ac:
        r1 = r5.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = r5.chatId;
        r3 = r5.nameTextView;
        r3 = r3.getText();
        r3 = r3.toString();
        r1.changeChatTitle(r2, r3);
    L_0x00c1:
        r1 = r5.info;
        if (r1 == 0) goto L_0x00ca;
    L_0x00c5:
        r1 = r1.about;
        if (r1 == 0) goto L_0x00ca;
    L_0x00c9:
        goto L_0x00cc;
    L_0x00ca:
        r1 = "";
    L_0x00cc:
        r2 = r5.descriptionTextView;
        if (r2 == 0) goto L_0x00f5;
    L_0x00d0:
        r2 = r2.getText();
        r2 = r2.toString();
        r1 = r1.equals(r2);
        if (r1 != 0) goto L_0x00f5;
    L_0x00de:
        r1 = r5.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = r5.chatId;
        r3 = r5.descriptionTextView;
        r3 = r3.getText();
        r3 = r3.toString();
        r4 = r5.info;
        r1.updateChatAbout(r2, r3, r4);
    L_0x00f5:
        r1 = r5.signMessages;
        r2 = r5.currentChat;
        r3 = r2.signatures;
        if (r1 == r3) goto L_0x010c;
    L_0x00fd:
        r2.signatures = r0;
        r0 = r5.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r1 = r5.chatId;
        r2 = r5.signMessages;
        r0.toogleChannelSignatures(r1, r2);
    L_0x010c:
        r0 = r5.uploadedAvatar;
        if (r0 == 0) goto L_0x0122;
    L_0x0110:
        r0 = r5.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r1 = r5.chatId;
        r2 = r5.uploadedAvatar;
        r3 = r5.avatar;
        r4 = r5.avatarBig;
        r0.changeChatAvatar(r1, r2, r3, r4);
        goto L_0x013a;
    L_0x0122:
        r0 = r5.avatar;
        if (r0 != 0) goto L_0x013a;
    L_0x0126:
        r0 = r5.currentChat;
        r0 = r0.photo;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_chatPhoto;
        if (r0 == 0) goto L_0x013a;
    L_0x012e:
        r0 = r5.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r1 = r5.chatId;
        r2 = 0;
        r0.changeChatAvatar(r1, r2, r2, r2);
    L_0x013a:
        r5.finishFragment();
    L_0x013d:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditActivity.processDone():void");
    }

    public /* synthetic */ void lambda$processDone$21$ChatEditActivity(int i) {
        this.chatId = i;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(i));
        this.donePressed = false;
        ChatFull chatFull = this.info;
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
                AnimatorSet animatorSet2;
                Animator[] animatorArr;
                if (z) {
                    this.avatarProgressView.setVisibility(0);
                    animatorSet2 = this.avatarAnimation;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{0.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f});
                    animatorSet2.playTogether(animatorArr);
                } else {
                    this.avatarEditor.setVisibility(0);
                    animatorSet2 = this.avatarAnimation;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{1.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f});
                    animatorSet2.playTogether(animatorArr);
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
                            ChatEditActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        ChatEditActivity.this.avatarAnimation = null;
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
        String str;
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            str = imageUpdater.currentPicturePath;
            if (str != null) {
                bundle.putString("path", str);
            }
        }
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            str = editTextEmoji.getText().toString();
            if (str != null && str.length() != 0) {
                bundle.putString("nameTextView", str);
            }
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.currentPicturePath = bundle.getString("path");
        }
    }

    public void setInfo(ChatFull chatFull) {
        this.info = chatFull;
        if (chatFull != null) {
            if (this.currentChat == null) {
                this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
            }
            boolean z = !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory;
            this.historyHidden = z;
        }
    }

    private void updateFields(boolean z) {
        ChatFull chatFull;
        int i;
        ChatFull chatFull2;
        String str;
        String str2;
        String string;
        int i2;
        String str3;
        boolean z2;
        if (z) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
            if (chat != null) {
                this.currentChat = chat;
            }
        }
        z = TextUtils.isEmpty(this.currentChat.username);
        TextDetailCell textDetailCell = this.historyCell;
        if (textDetailCell != null) {
            chatFull = this.info;
            if (chatFull == null || !(chatFull.location instanceof TL_channelLocation)) {
                textDetailCell = this.historyCell;
                if (z) {
                    chatFull = this.info;
                    if (chatFull == null || chatFull.linked_chat_id == 0) {
                        i = 0;
                        textDetailCell.setVisibility(i);
                    }
                }
                i = 8;
                textDetailCell.setVisibility(i);
            } else {
                textDetailCell.setVisibility(8);
            }
        }
        ShadowSectionCell shadowSectionCell = this.settingsSectionCell;
        if (shadowSectionCell != null) {
            if (this.signCell == null && this.typeCell == null) {
                TextDetailCell textDetailCell2 = this.linkedCell;
                if (textDetailCell2 == null || textDetailCell2.getVisibility() != 0) {
                    textDetailCell2 = this.historyCell;
                    if (textDetailCell2 == null || textDetailCell2.getVisibility() != 0) {
                        textDetailCell2 = this.locationCell;
                        if (textDetailCell2 == null || textDetailCell2.getVisibility() != 0) {
                            i = 8;
                            shadowSectionCell.setVisibility(i);
                        }
                    }
                }
            }
            i = 0;
            shadowSectionCell.setVisibility(i);
        }
        TextCell textCell = this.logCell;
        if (textCell != null) {
            if (this.currentChat.megagroup) {
                chatFull = this.info;
                if (chatFull == null || chatFull.participants_count <= 200) {
                    i = 8;
                    textCell.setVisibility(i);
                }
            }
            i = 0;
            textCell.setVisibility(i);
        }
        if (this.linkedCell != null) {
            chatFull2 = this.info;
            if (chatFull2 == null || (!this.isChannel && chatFull2.linked_chat_id == 0)) {
                this.linkedCell.setVisibility(8);
            } else {
                this.linkedCell.setVisibility(0);
                str = "Discussion";
                if (this.info.linked_chat_id == 0) {
                    this.linkedCell.setTextAndValue(LocaleController.getString(str, NUM), LocaleController.getString("DiscussionInfo", NUM), true);
                } else {
                    Chat chat2 = getMessagesController().getChat(Integer.valueOf(this.info.linked_chat_id));
                    if (chat2 == null) {
                        this.linkedCell.setVisibility(8);
                    } else if (!this.isChannel) {
                        str2 = "LinkedChannel";
                        if (TextUtils.isEmpty(chat2.username)) {
                            this.linkedCell.setTextAndValue(LocaleController.getString(str2, NUM), chat2.title, false);
                        } else {
                            TextDetailCell textDetailCell3 = this.linkedCell;
                            str = LocaleController.getString(str2, NUM);
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("@");
                            stringBuilder.append(chat2.username);
                            textDetailCell3.setTextAndValue(str, stringBuilder.toString(), false);
                        }
                    } else if (TextUtils.isEmpty(chat2.username)) {
                        this.linkedCell.setTextAndValue(LocaleController.getString(str, NUM), chat2.title, true);
                    } else {
                        TextDetailCell textDetailCell4 = this.linkedCell;
                        string = LocaleController.getString(str, NUM);
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("@");
                        stringBuilder2.append(chat2.username);
                        textDetailCell4.setTextAndValue(string, stringBuilder2.toString(), true);
                    }
                }
            }
        }
        textDetailCell = this.locationCell;
        if (textDetailCell != null) {
            ChatFull chatFull3 = this.info;
            if (chatFull3 == null || !chatFull3.can_set_location) {
                this.locationCell.setVisibility(8);
            } else {
                textDetailCell.setVisibility(0);
                ChannelLocation channelLocation = this.info.location;
                str2 = "AttachLocation";
                if (channelLocation instanceof TL_channelLocation) {
                    this.locationCell.setTextAndValue(LocaleController.getString(str2, NUM), ((TL_channelLocation) channelLocation).address, true);
                } else {
                    this.locationCell.setTextAndValue(LocaleController.getString(str2, NUM), "Unknown address", true);
                }
            }
        }
        if (this.typeCell != null) {
            chatFull2 = this.info;
            String string2;
            TextDetailCell textDetailCell5;
            if (chatFull2 == null || !(chatFull2.location instanceof TL_channelLocation)) {
                if (this.isChannel) {
                    if (z) {
                        i2 = NUM;
                        str3 = "TypePrivate";
                    } else {
                        i2 = NUM;
                        str3 = "TypePublic";
                    }
                    string2 = LocaleController.getString(str3, i2);
                } else {
                    if (z) {
                        i2 = NUM;
                        str3 = "TypePrivateGroup";
                    } else {
                        i2 = NUM;
                        str3 = "TypePublicGroup";
                    }
                    string2 = LocaleController.getString(str3, i2);
                }
                if (this.isChannel) {
                    textDetailCell = this.typeCell;
                    string = LocaleController.getString("ChannelType", NUM);
                    textDetailCell5 = this.historyCell;
                    if (textDetailCell5 == null || textDetailCell5.getVisibility() != 0) {
                        textDetailCell5 = this.linkedCell;
                        if (textDetailCell5 == null || textDetailCell5.getVisibility() != 0) {
                            z2 = false;
                            textDetailCell.setTextAndValue(string, string2, z2);
                        }
                    }
                    z2 = true;
                    textDetailCell.setTextAndValue(string, string2, z2);
                } else {
                    textDetailCell = this.typeCell;
                    string = LocaleController.getString("GroupType", NUM);
                    textDetailCell5 = this.historyCell;
                    if (textDetailCell5 == null || textDetailCell5.getVisibility() != 0) {
                        textDetailCell5 = this.linkedCell;
                        if (textDetailCell5 == null || textDetailCell5.getVisibility() != 0) {
                            z2 = false;
                            textDetailCell.setTextAndValue(string, string2, z2);
                        }
                    }
                    z2 = true;
                    textDetailCell.setTextAndValue(string, string2, z2);
                }
            } else {
                if (z) {
                    string2 = LocaleController.getString("TypeLocationGroupEdit", NUM);
                } else {
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("https://");
                    stringBuilder3.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
                    stringBuilder3.append("/%s");
                    string2 = String.format(stringBuilder3.toString(), new Object[]{this.currentChat.username});
                }
                textDetailCell = this.typeCell;
                string = LocaleController.getString("TypeLocationGroup", NUM);
                textDetailCell5 = this.historyCell;
                if (textDetailCell5 == null || textDetailCell5.getVisibility() != 0) {
                    textDetailCell5 = this.linkedCell;
                    if (textDetailCell5 == null || textDetailCell5.getVisibility() != 0) {
                        z2 = false;
                        textDetailCell.setTextAndValue(string, string2, z2);
                    }
                }
                z2 = true;
                textDetailCell.setTextAndValue(string, string2, z2);
            }
        }
        if (!(this.info == null || this.historyCell == null)) {
            if (this.historyHidden) {
                i2 = NUM;
                str3 = "ChatHistoryHidden";
            } else {
                i2 = NUM;
                str3 = "ChatHistoryVisible";
            }
            this.historyCell.setTextAndValue(LocaleController.getString("ChatHistory", NUM), LocaleController.getString(str3, i2), false);
        }
        TextSettingsCell textSettingsCell = this.stickersCell;
        string = "GroupStickers";
        if (textSettingsCell != null) {
            if (this.info.stickerset != null) {
                textSettingsCell.setTextAndValue(LocaleController.getString(string, NUM), this.info.stickerset.title, false);
            } else {
                textSettingsCell.setText(LocaleController.getString(string, NUM), false);
            }
        }
        TextCell textCell2 = this.membersCell;
        if (textCell2 != null) {
            String str4 = "ChannelMembers";
            String string3;
            if (this.info != null) {
                Object[] objArr;
                String str5 = "%d";
                TextCell textCell3;
                boolean z3;
                if (this.isChannel) {
                    textCell2.setTextAndValueAndIcon(LocaleController.getString("ChannelSubscribers", NUM), String.format(str5, new Object[]{Integer.valueOf(this.info.participants_count)}), NUM, true);
                    textCell2 = this.blockCell;
                    string3 = LocaleController.getString("ChannelBlacklist", NUM);
                    objArr = new Object[1];
                    ChatFull chatFull4 = this.info;
                    objArr[0] = Integer.valueOf(Math.max(chatFull4.banned_count, chatFull4.kicked_count));
                    str = String.format(str5, objArr);
                    textCell3 = this.logCell;
                    z3 = textCell3 != null && textCell3.getVisibility() == 0;
                    textCell2.setTextAndValueAndIcon(string3, str, NUM, z3);
                } else {
                    if (ChatObject.isChannel(this.currentChat)) {
                        textCell2 = this.membersCell;
                        str = LocaleController.getString(str4, NUM);
                        str2 = String.format(str5, new Object[]{Integer.valueOf(this.info.participants_count)});
                        textCell3 = this.logCell;
                        z3 = textCell3 != null && textCell3.getVisibility() == 0;
                        textCell2.setTextAndValueAndIcon(str, str2, NUM, z3);
                    } else {
                        textCell2 = this.membersCell;
                        str = LocaleController.getString(str4, NUM);
                        str2 = String.format(str5, new Object[]{Integer.valueOf(this.info.participants.participants.size())});
                        textCell3 = this.logCell;
                        z3 = textCell3 != null && textCell3.getVisibility() == 0;
                        textCell2.setTextAndValueAndIcon(str, str2, NUM, z3);
                    }
                    TL_chatBannedRights tL_chatBannedRights = this.currentChat.default_banned_rights;
                    if (tL_chatBannedRights != null) {
                        i2 = !tL_chatBannedRights.send_stickers ? 1 : 0;
                        if (!this.currentChat.default_banned_rights.send_media) {
                            i2++;
                        }
                        if (!this.currentChat.default_banned_rights.embed_links) {
                            i2++;
                        }
                        if (!this.currentChat.default_banned_rights.send_messages) {
                            i2++;
                        }
                        if (!this.currentChat.default_banned_rights.pin_messages) {
                            i2++;
                        }
                        if (!this.currentChat.default_banned_rights.send_polls) {
                            i2++;
                        }
                        if (!this.currentChat.default_banned_rights.invite_users) {
                            i2++;
                        }
                        if (!this.currentChat.default_banned_rights.change_info) {
                            i2++;
                        }
                    } else {
                        i2 = 8;
                    }
                    this.blockCell.setTextAndValueAndIcon(LocaleController.getString("ChannelPermissions", NUM), String.format("%d/%d", new Object[]{Integer.valueOf(i2), Integer.valueOf(8)}), NUM, true);
                }
                textCell2 = this.adminCell;
                string3 = LocaleController.getString("ChannelAdministrators", NUM);
                objArr = new Object[1];
                objArr[0] = Integer.valueOf(ChatObject.isChannel(this.currentChat) ? this.info.admins_count : getAdminCount());
                textCell2.setTextAndValueAndIcon(string3, String.format(str5, objArr), NUM, true);
            } else {
                if (this.isChannel) {
                    textCell2.setTextAndIcon(LocaleController.getString("ChannelSubscribers", NUM), NUM, true);
                    textCell2 = this.blockCell;
                    string3 = LocaleController.getString("ChannelBlacklist", NUM);
                    TextCell textCell4 = this.logCell;
                    boolean z4 = textCell4 != null && textCell4.getVisibility() == 0;
                    textCell2.setTextAndIcon(string3, NUM, z4);
                } else {
                    string3 = LocaleController.getString(str4, NUM);
                    TextCell textCell5 = this.logCell;
                    z2 = textCell5 != null && textCell5.getVisibility() == 0;
                    textCell2.setTextAndIcon(string3, NUM, z2);
                    this.blockCell.setTextAndIcon(LocaleController.getString("ChannelPermissions", NUM), NUM, true);
                }
                this.adminCell.setTextAndIcon(LocaleController.getString("ChannelAdministrators", NUM), NUM, true);
            }
        }
        textSettingsCell = this.stickersCell;
        if (textSettingsCell != null) {
            ChatFull chatFull5 = this.info;
            if (chatFull5 == null) {
                return;
            }
            if (chatFull5.stickerset != null) {
                textSettingsCell.setTextAndValue(LocaleController.getString(string, NUM), this.info.stickerset.title, false);
            } else {
                textSettingsCell.setText(LocaleController.getString(string, NUM), false);
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$ChatEditActivity$HxxxUSJLb6eQcLNiQbebCuwv8Vw -__lambda_chateditactivity_hxxxusjlb6eqclniqbebcuwv8vw = new -$$Lambda$ChatEditActivity$HxxxUSJLb6eQcLNiQbebCuwv8Vw(this);
        r10 = new ThemeDescription[60];
        View view = this.membersCell;
        int i = ThemeDescription.FLAG_TEXTCOLOR;
        Class[] clsArr = new Class[]{TextCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        r10[6] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.membersCell;
        clsArr = new Class[]{TextCell.class};
        strArr = new String[1];
        strArr[0] = "imageView";
        r10[7] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayIcon");
        r10[8] = new ThemeDescription(this.adminCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r10[9] = new ThemeDescription(this.adminCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[10] = new ThemeDescription(this.adminCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        r10[11] = new ThemeDescription(this.blockCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r10[12] = new ThemeDescription(this.blockCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[13] = new ThemeDescription(this.blockCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        r10[14] = new ThemeDescription(this.logCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r10[15] = new ThemeDescription(this.logCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[16] = new ThemeDescription(this.logCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        r10[17] = new ThemeDescription(this.typeCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r10[18] = new ThemeDescription(this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.typeCell;
        clsArr = new Class[]{TextDetailCell.class};
        strArr = new String[1];
        strArr[0] = "valueTextView";
        r10[19] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText2");
        r10[20] = new ThemeDescription(this.historyCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r10[21] = new ThemeDescription(this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[22] = new ThemeDescription(this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        r10[23] = new ThemeDescription(this.locationCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r10[24] = new ThemeDescription(this.locationCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[25] = new ThemeDescription(this.locationCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        r10[26] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r10[27] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText");
        r10[28] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField");
        r10[29] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated");
        r10[30] = new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r10[31] = new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText");
        r10[32] = new ThemeDescription(this.avatarContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        r10[33] = new ThemeDescription(this.settingsContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        r10[34] = new ThemeDescription(this.typeEditContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        r10[35] = new ThemeDescription(this.deleteContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        r10[36] = new ThemeDescription(this.stickersContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        r10[37] = new ThemeDescription(this.infoContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        r10[38] = new ThemeDescription(this.settingsTopSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r10[39] = new ThemeDescription(this.settingsSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r10[40] = new ThemeDescription(this.deleteInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r10[41] = new ThemeDescription(this.infoSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r10[42] = new ThemeDescription(this.signCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r10[43] = new ThemeDescription(this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.signCell;
        clsArr = new Class[]{TextCheckCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        r10[44] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "switchTrack");
        r10[45] = new ThemeDescription(this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        r10[46] = new ThemeDescription(this.deleteCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r10[47] = new ThemeDescription(this.deleteCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteRedText5");
        r10[48] = new ThemeDescription(this.stickersCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r10[49] = new ThemeDescription(this.stickersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[50] = new ThemeDescription(this.stickersInfoCell3, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r10[51] = new ThemeDescription(this.stickersInfoCell3, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        -$$Lambda$ChatEditActivity$HxxxUSJLb6eQcLNiQbebCuwv8Vw -__lambda_chateditactivity_hxxxusjlb6eqclniqbebcuwv8vw2 = -__lambda_chateditactivity_hxxxusjlb6eqclniqbebcuwv8vw;
        r10[52] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_savedDrawable}, -__lambda_chateditactivity_hxxxusjlb6eqclniqbebcuwv8vw2, "avatar_text");
        r10[53] = new ThemeDescription(null, 0, null, null, null, -__lambda_chateditactivity_hxxxusjlb6eqclniqbebcuwv8vw2, "avatar_backgroundRed");
        r10[54] = new ThemeDescription(null, 0, null, null, null, -__lambda_chateditactivity_hxxxusjlb6eqclniqbebcuwv8vw2, "avatar_backgroundOrange");
        r10[55] = new ThemeDescription(null, 0, null, null, null, -__lambda_chateditactivity_hxxxusjlb6eqclniqbebcuwv8vw2, "avatar_backgroundViolet");
        r10[56] = new ThemeDescription(null, 0, null, null, null, -__lambda_chateditactivity_hxxxusjlb6eqclniqbebcuwv8vw2, "avatar_backgroundGreen");
        r10[57] = new ThemeDescription(null, 0, null, null, null, -__lambda_chateditactivity_hxxxusjlb6eqclniqbebcuwv8vw2, "avatar_backgroundCyan");
        r10[58] = new ThemeDescription(null, 0, null, null, null, -__lambda_chateditactivity_hxxxusjlb6eqclniqbebcuwv8vw2, "avatar_backgroundBlue");
        r10[59] = new ThemeDescription(null, 0, null, null, null, -__lambda_chateditactivity_hxxxusjlb6eqclniqbebcuwv8vw2, "avatar_backgroundPink");
        return r10;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$23$ChatEditActivity() {
        if (this.avatarImage != null) {
            this.avatarDrawable.setInfo(5, null, null);
            this.avatarImage.invalidate();
        }
    }
}
