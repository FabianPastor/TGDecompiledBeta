package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
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
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_chatPhoto;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

public class ChatEditActivity extends BaseFragment implements NotificationCenterDelegate, ImageUpdaterDelegate {
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

    public ChatEditActivity(Bundle args) {
        super(args);
        this.chatId = args.getInt("chat_id", 0);
    }

    public boolean onFragmentCreate() {
        boolean z = true;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        if (this.currentChat == null) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new ChatEditActivity$$Lambda$0(this, countDownLatch));
            try {
                countDownLatch.await();
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (this.currentChat == null) {
                return false;
            }
            MessagesController.getInstance(this.currentAccount).putChat(this.currentChat, true);
            if (this.info == null) {
                MessagesStorage.getInstance(this.currentAccount).loadChatInfo(this.chatId, countDownLatch, false, false);
                try {
                    countDownLatch.await();
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
                if (this.info == null) {
                    return false;
                }
            }
        }
        if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
            z = false;
        }
        this.isChannel = z;
        this.imageUpdater.parentFragment = this;
        this.imageUpdater.delegate = this;
        this.signMessages = this.currentChat.signatures;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        return super.onFragmentCreate();
    }

    final /* synthetic */ void lambda$onFragmentCreate$0$ChatEditActivity(CountDownLatch countDownLatch) {
        this.currentChat = MessagesStorage.getInstance(this.currentAccount).getChat(this.chatId);
        countDownLatch.countDown();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.imageUpdater != null) {
            this.imageUpdater.clear();
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        if (this.nameTextView != null) {
            this.nameTextView.onDestroy();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.nameTextView != null) {
            this.nameTextView.onResume();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        updateFields(true);
    }

    public void onPause() {
        super.onPause();
        if (this.nameTextView != null) {
            this.nameTextView.onPause();
        }
    }

    public boolean onBackPressed() {
        if (this.nameTextView == null || !this.nameTextView.isPopupShowing()) {
            return checkDiscard();
        }
        this.nameTextView.hidePopup(true);
        return false;
    }

    public View createView(Context context) {
        float f;
        float f2;
        if (this.nameTextView != null) {
            this.nameTextView.onDestroy();
        }
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (ChatEditActivity.this.checkDiscard()) {
                        ChatEditActivity.this.lambda$checkDiscard$18$ChatUsersActivity();
                    }
                } else if (id == 1) {
                    ChatEditActivity.this.processDone();
                }
            }
        });
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                heightSize -= getPaddingTop();
                measureChildWithMargins(ChatEditActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int keyboardSize = getKeyboardHeight();
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == ChatEditActivity.this.actionBar)) {
                        if (ChatEditActivity.this.nameTextView == null || !ChatEditActivity.this.nameTextView.isPopupView(child)) {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec((heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                }
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int count = getChildCount();
                int paddingBottom = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) ? 0 : ChatEditActivity.this.nameTextView.getEmojiPadding();
                setBottomClip(paddingBottom);
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        int childLeft;
                        int childTop;
                        LayoutParams lp = (LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        switch ((gravity & 7) & 7) {
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
                                childTop = (getMeasuredHeight() + getKeyboardHeight()) - child.getMeasuredHeight();
                            }
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                notifyHeightChanged();
            }
        };
        sizeNotifierFrameLayout.setOnTouchListener(ChatEditActivity$$Lambda$1.$instance);
        this.fragmentView = sizeNotifierFrameLayout;
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        sizeNotifierFrameLayout.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout1 = new LinearLayout(context);
        scrollView.addView(linearLayout1, new LayoutParams(-1, -2));
        linearLayout1.setOrientation(1);
        this.actionBar.setTitle(LocaleController.getString("ChannelEdit", R.string.ChannelEdit));
        this.avatarContainer = new LinearLayout(context);
        this.avatarContainer.setOrientation(1);
        this.avatarContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout1.addView(this.avatarContainer, LayoutHelper.createLinear(-1, -2));
        FrameLayout frameLayout = new FrameLayout(context);
        this.avatarContainer.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
        this.avatarImage = new BackupImageView(context) {
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
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
        View view = this.avatarImage;
        int i = (LocaleController.isRTL ? 5 : 3) | 48;
        if (LocaleController.isRTL) {
            f = 0.0f;
        } else {
            f = 16.0f;
        }
        if (LocaleController.isRTL) {
            f2 = 16.0f;
        } else {
            f2 = 0.0f;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(64, 64.0f, i, f, 12.0f, f2, 12.0f));
        if (ChatObject.canChangeChatInfo(this.currentChat)) {
            this.avatarDrawable.setInfo(5, null, null, false);
            final Paint paint = new Paint(1);
            paint.setColor(NUM);
            this.avatarOverlay = new View(context) {
                protected void onDraw(Canvas canvas) {
                    if (ChatEditActivity.this.avatarImage != null && ChatEditActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                        paint.setAlpha((int) (85.0f * ChatEditActivity.this.avatarImage.getImageReceiver().getCurrentAlpha()));
                        canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(32.0f), paint);
                    }
                }
            };
            view = this.avatarOverlay;
            i = (LocaleController.isRTL ? 5 : 3) | 48;
            if (LocaleController.isRTL) {
                f = 0.0f;
            } else {
                f = 16.0f;
            }
            if (LocaleController.isRTL) {
                f2 = 16.0f;
            } else {
                f2 = 0.0f;
            }
            frameLayout.addView(view, LayoutHelper.createFrame(64, 64.0f, i, f, 12.0f, f2, 12.0f));
            this.avatarOverlay.setOnClickListener(new ChatEditActivity$$Lambda$2(this));
            this.avatarEditor = new ImageView(context) {
                public void invalidate(int l, int t, int r, int b) {
                    super.invalidate(l, t, r, b);
                    ChatEditActivity.this.avatarOverlay.invalidate();
                }

                public void invalidate() {
                    super.invalidate();
                    ChatEditActivity.this.avatarOverlay.invalidate();
                }
            };
            this.avatarEditor.setScaleType(ScaleType.CENTER);
            this.avatarEditor.setImageResource(R.drawable.menu_camera_av);
            this.avatarEditor.setEnabled(false);
            this.avatarEditor.setClickable(false);
            frameLayout.addView(this.avatarEditor, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            this.avatarProgressView = new RadialProgressView(context);
            this.avatarProgressView.setSize(AndroidUtilities.dp(30.0f));
            this.avatarProgressView.setProgressColor(-1);
            frameLayout.addView(this.avatarProgressView, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            showAvatarProgress(false, false);
        } else {
            this.avatarDrawable.setInfo(5, this.currentChat.title, null, false);
        }
        this.nameTextView = new EditTextEmoji((Activity) context, sizeNotifierFrameLayout, this);
        if (this.isChannel) {
            this.nameTextView.setHint(LocaleController.getString("EnterChannelName", R.string.EnterChannelName));
        } else {
            this.nameTextView.setHint(LocaleController.getString("GroupName", R.string.GroupName));
        }
        this.nameTextView.setEnabled(ChatObject.canChangeChatInfo(this.currentChat));
        this.nameTextView.setFocusable(this.nameTextView.isEnabled());
        this.nameTextView.setFilters(new InputFilter[]{new LengthFilter(100)});
        view = this.nameTextView;
        f = LocaleController.isRTL ? 5.0f : 96.0f;
        if (LocaleController.isRTL) {
            f2 = 96.0f;
        } else {
            f2 = 5.0f;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(-1, -2.0f, 16, f, 0.0f, f2, 0.0f));
        this.settingsContainer = new LinearLayout(context);
        this.settingsContainer.setOrientation(1);
        this.settingsContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout1.addView(this.settingsContainer, LayoutHelper.createLinear(-1, -2));
        this.descriptionTextView = new EditTextBoldCursor(context);
        this.descriptionTextView.setTextSize(1, 16.0f);
        this.descriptionTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.descriptionTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
        this.descriptionTextView.setBackgroundDrawable(null);
        this.descriptionTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.descriptionTextView.setInputType(180225);
        this.descriptionTextView.setImeOptions(6);
        this.descriptionTextView.setEnabled(ChatObject.canChangeChatInfo(this.currentChat));
        this.descriptionTextView.setFocusable(this.descriptionTextView.isEnabled());
        this.descriptionTextView.setFilters(new InputFilter[]{new LengthFilter(255)});
        this.descriptionTextView.setHint(LocaleController.getString("DescriptionOptionalPlaceholder", R.string.DescriptionOptionalPlaceholder));
        this.descriptionTextView.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        this.descriptionTextView.setCursorWidth(1.5f);
        this.settingsContainer.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 23.0f, 12.0f, 23.0f, 6.0f));
        this.descriptionTextView.setOnEditorActionListener(new ChatEditActivity$$Lambda$3(this));
        this.descriptionTextView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
            }
        });
        this.settingsTopSectionCell = new ShadowSectionCell(context);
        linearLayout1.addView(this.settingsTopSectionCell, LayoutHelper.createLinear(-1, -2));
        this.typeEditContainer = new LinearLayout(context);
        this.typeEditContainer.setOrientation(1);
        this.typeEditContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout1.addView(this.typeEditContainer, LayoutHelper.createLinear(-1, -2));
        if (this.currentChat.creator && (this.info == null || this.info.can_set_username)) {
            this.typeCell = new TextDetailCell(context);
            this.typeCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.typeCell, LayoutHelper.createLinear(-1, -2));
            this.typeCell.setOnClickListener(new ChatEditActivity$$Lambda$4(this));
        }
        if (!this.isChannel && ChatObject.canBlockUsers(this.currentChat) && (ChatObject.isChannel(this.currentChat) || this.currentChat.creator)) {
            this.historyCell = new TextDetailCell(context);
            this.historyCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.historyCell, LayoutHelper.createLinear(-1, -2));
            this.historyCell.setOnClickListener(new ChatEditActivity$$Lambda$5(this, context));
        }
        if (this.isChannel) {
            this.signCell = new TextCheckCell(context);
            this.signCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.signCell.setTextAndValueAndCheck(LocaleController.getString("ChannelSignMessages", R.string.ChannelSignMessages), LocaleController.getString("ChannelSignMessagesInfo", R.string.ChannelSignMessagesInfo), this.signMessages, true, false);
            this.typeEditContainer.addView(this.signCell, LayoutHelper.createFrame(-1, -2.0f));
            this.signCell.setOnClickListener(new ChatEditActivity$$Lambda$6(this));
        }
        ActionBarMenu menu = this.actionBar.createMenu();
        if (!(!ChatObject.canChangeChatInfo(this.currentChat) && this.signCell == null && this.historyCell == null)) {
            this.doneButton = menu.addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        }
        if (!(this.signCell == null && this.historyCell == null)) {
            this.settingsSectionCell = new ShadowSectionCell(context);
            linearLayout1.addView(this.settingsSectionCell, LayoutHelper.createLinear(-1, -2));
        }
        this.infoContainer = new LinearLayout(context);
        this.infoContainer.setOrientation(1);
        this.infoContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout1.addView(this.infoContainer, LayoutHelper.createLinear(-1, -2));
        this.blockCell = new TextCell(context);
        this.blockCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        TextCell textCell = this.blockCell;
        int i2 = (ChatObject.isChannel(this.currentChat) || this.currentChat.creator) ? 0 : 8;
        textCell.setVisibility(i2);
        this.blockCell.setOnClickListener(new ChatEditActivity$$Lambda$7(this));
        this.adminCell = new TextCell(context);
        this.adminCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.adminCell.setOnClickListener(new ChatEditActivity$$Lambda$8(this));
        this.membersCell = new TextCell(context);
        this.membersCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.membersCell.setOnClickListener(new ChatEditActivity$$Lambda$9(this));
        if (ChatObject.isChannel(this.currentChat)) {
            this.logCell = new TextCell(context);
            this.logCell.setTextAndIcon(LocaleController.getString("EventLog", R.string.EventLog), R.drawable.group_log, false);
            this.logCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.logCell.setOnClickListener(new ChatEditActivity$$Lambda$10(this));
        }
        if (!this.isChannel) {
            this.infoContainer.addView(this.blockCell, LayoutHelper.createLinear(-1, -2));
        }
        this.infoContainer.addView(this.adminCell, LayoutHelper.createLinear(-1, -2));
        this.infoContainer.addView(this.membersCell, LayoutHelper.createLinear(-1, -2));
        if (this.isChannel) {
            this.infoContainer.addView(this.blockCell, LayoutHelper.createLinear(-1, -2));
        }
        if (this.logCell != null) {
            this.infoContainer.addView(this.logCell, LayoutHelper.createLinear(-1, -2));
        }
        this.infoSectionCell = new ShadowSectionCell(context);
        linearLayout1.addView(this.infoSectionCell, LayoutHelper.createLinear(-1, -2));
        if (!ChatObject.hasAdminRights(this.currentChat)) {
            this.infoContainer.setVisibility(8);
            this.settingsTopSectionCell.setVisibility(8);
        }
        if (!(this.isChannel || this.info == null || !this.info.can_set_stickers)) {
            this.stickersContainer = new FrameLayout(context);
            this.stickersContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            linearLayout1.addView(this.stickersContainer, LayoutHelper.createLinear(-1, -2));
            this.stickersCell = new TextSettingsCell(context);
            this.stickersCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.stickersCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.stickersContainer.addView(this.stickersCell, LayoutHelper.createFrame(-1, -2.0f));
            this.stickersCell.setOnClickListener(new ChatEditActivity$$Lambda$11(this));
            this.stickersInfoCell3 = new TextInfoPrivacyCell(context);
            this.stickersInfoCell3.setText(LocaleController.getString("GroupStickersInfo", R.string.GroupStickersInfo));
            linearLayout1.addView(this.stickersInfoCell3, LayoutHelper.createLinear(-1, -2));
        }
        if (this.currentChat.creator) {
            this.deleteContainer = new FrameLayout(context);
            this.deleteContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            linearLayout1.addView(this.deleteContainer, LayoutHelper.createLinear(-1, -2));
            this.deleteCell = new TextSettingsCell(context);
            this.deleteCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
            this.deleteCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (this.isChannel) {
                this.deleteCell.setText(LocaleController.getString("ChannelDelete", R.string.ChannelDelete), false);
            } else {
                this.deleteCell.setText(LocaleController.getString("DeleteMega", R.string.DeleteMega), false);
            }
            this.deleteContainer.addView(this.deleteCell, LayoutHelper.createFrame(-1, -2.0f));
            this.deleteCell.setOnClickListener(new ChatEditActivity$$Lambda$12(this));
            this.deleteInfoCell = new ShadowSectionCell(context);
            this.deleteInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
            linearLayout1.addView(this.deleteInfoCell, LayoutHelper.createLinear(-1, -2));
        } else if (!this.isChannel && this.stickersInfoCell3 == null) {
            this.infoSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
        }
        if (this.stickersInfoCell3 != null) {
            if (this.deleteInfoCell == null) {
                this.stickersInfoCell3.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
            } else {
                this.stickersInfoCell3.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider, "windowBackgroundGrayShadow"));
            }
        }
        this.nameTextView.setText(this.currentChat.title);
        this.nameTextView.setSelection(this.nameTextView.length());
        if (this.info != null) {
            this.descriptionTextView.setText(this.info.about);
        }
        if (this.currentChat.photo != null) {
            this.avatar = this.currentChat.photo.photo_small;
            this.avatarBig = this.currentChat.photo.photo_big;
            this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable, this.currentChat);
        } else {
            this.avatarImage.setImageDrawable(this.avatarDrawable);
        }
        updateFields(true);
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$3$ChatEditActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new ChatEditActivity$$Lambda$21(this));
    }

    final /* synthetic */ void lambda$null$2$ChatEditActivity() {
        this.avatar = null;
        this.avatarBig = null;
        this.uploadedAvatar = null;
        showAvatarProgress(false, true);
        this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable, this.currentChat);
    }

    final /* synthetic */ boolean lambda$createView$4$ChatEditActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6 || this.doneButton == null) {
            return false;
        }
        this.doneButton.performClick();
        return true;
    }

    final /* synthetic */ void lambda$createView$5$ChatEditActivity(View v) {
        ChatEditTypeActivity fragment = new ChatEditTypeActivity(this.chatId);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    final /* synthetic */ void lambda$createView$7$ChatEditActivity(Context context, View v) {
        Builder builder = new Builder(context);
        builder.setApplyTopPadding(false);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        HeaderCell headerCell = new HeaderCell(context, true, 23, 15, false);
        headerCell.setHeight(47);
        headerCell.setText(LocaleController.getString("ChatHistory", R.string.ChatHistory));
        linearLayout.addView(headerCell);
        LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
        linearLayoutInviteContainer.setOrientation(1);
        linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(-1, -2));
        RadioButtonCell[] buttons = new RadioButtonCell[2];
        for (int a = 0; a < 2; a++) {
            buttons[a] = new RadioButtonCell(context, true);
            buttons[a].setTag(Integer.valueOf(a));
            buttons[a].setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (a == 0) {
                buttons[a].setTextAndValue(LocaleController.getString("ChatHistoryVisible", R.string.ChatHistoryVisible), LocaleController.getString("ChatHistoryVisibleInfo", R.string.ChatHistoryVisibleInfo), true, !this.historyHidden);
            } else if (ChatObject.isChannel(this.currentChat)) {
                buttons[a].setTextAndValue(LocaleController.getString("ChatHistoryHidden", R.string.ChatHistoryHidden), LocaleController.getString("ChatHistoryHiddenInfo", R.string.ChatHistoryHiddenInfo), false, this.historyHidden);
            } else {
                buttons[a].setTextAndValue(LocaleController.getString("ChatHistoryHidden", R.string.ChatHistoryHidden), LocaleController.getString("ChatHistoryHiddenInfo2", R.string.ChatHistoryHiddenInfo2), false, this.historyHidden);
            }
            linearLayoutInviteContainer.addView(buttons[a], LayoutHelper.createLinear(-1, -2));
            buttons[a].setOnClickListener(new ChatEditActivity$$Lambda$20(this, buttons, builder));
        }
        builder.setCustomView(linearLayout);
        showDialog(builder.create());
    }

    final /* synthetic */ void lambda$null$6$ChatEditActivity(RadioButtonCell[] buttons, Builder builder, View v2) {
        boolean z;
        boolean z2 = false;
        Integer tag = (Integer) v2.getTag();
        buttons[0].setChecked(tag.intValue() == 0, true);
        RadioButtonCell radioButtonCell = buttons[1];
        if (tag.intValue() == 1) {
            z = true;
        } else {
            z = false;
        }
        radioButtonCell.setChecked(z, true);
        if (tag.intValue() == 1) {
            z2 = true;
        }
        this.historyHidden = z2;
        builder.getDismissRunnable().run();
        updateFields(true);
    }

    final /* synthetic */ void lambda$createView$8$ChatEditActivity(View v) {
        this.signMessages = !this.signMessages;
        ((TextCheckCell) v).setChecked(this.signMessages);
    }

    final /* synthetic */ void lambda$createView$9$ChatEditActivity(View v) {
        Bundle args = new Bundle();
        args.putInt("chat_id", this.chatId);
        args.putInt("type", !this.isChannel ? 3 : 0);
        ChatUsersActivity fragment = new ChatUsersActivity(args);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    final /* synthetic */ void lambda$createView$10$ChatEditActivity(View v) {
        Bundle args = new Bundle();
        args.putInt("chat_id", this.chatId);
        args.putInt("type", 1);
        ChatUsersActivity fragment = new ChatUsersActivity(args);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    final /* synthetic */ void lambda$createView$11$ChatEditActivity(View v) {
        Bundle args = new Bundle();
        args.putInt("chat_id", this.chatId);
        args.putInt("type", 2);
        ChatUsersActivity fragment = new ChatUsersActivity(args);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    final /* synthetic */ void lambda$createView$12$ChatEditActivity(View v) {
        presentFragment(new ChannelAdminLogActivity(this.currentChat));
    }

    final /* synthetic */ void lambda$createView$13$ChatEditActivity(View v) {
        GroupStickersActivity groupStickersActivity = new GroupStickersActivity(this.currentChat.id);
        groupStickersActivity.setInfo(this.info);
        presentFragment(groupStickersActivity);
    }

    final /* synthetic */ void lambda$createView$15$ChatEditActivity(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        if (this.isChannel) {
            builder.setMessage(LocaleController.getString("ChannelDeleteAlert", R.string.ChannelDeleteAlert));
        } else {
            builder.setMessage(LocaleController.getString("MegaDeleteAlert", R.string.MegaDeleteAlert));
        }
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ChatEditActivity$$Lambda$19(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    final /* synthetic */ void lambda$null$14$ChatEditActivity(DialogInterface dialogInterface, int i) {
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(-((long) this.chatId)));
        } else {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chatId, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId())), this.info, true);
        lambda$checkDiscard$18$ChatUsersActivity();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad) {
            ChatFull chatFull = args[0];
            if (chatFull.id == this.chatId) {
                boolean z;
                if (this.info == null && this.descriptionTextView != null) {
                    this.descriptionTextView.setText(chatFull.about);
                }
                this.info = chatFull;
                if (!ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory) {
                    z = true;
                } else {
                    z = false;
                }
                this.historyHidden = z;
                updateFields(false);
            }
        }
    }

    public void didUploadPhoto(InputFile file, PhotoSize bigSize, PhotoSize smallSize) {
        AndroidUtilities.runOnUIThread(new ChatEditActivity$$Lambda$13(this, file, smallSize, bigSize));
    }

    final /* synthetic */ void lambda$didUploadPhoto$16$ChatEditActivity(InputFile file, PhotoSize smallSize, PhotoSize bigSize) {
        if (file != null) {
            this.uploadedAvatar = file;
            if (this.createAfterUpload) {
                try {
                    if (this.progressDialog != null && this.progressDialog.isShowing()) {
                        this.progressDialog.dismiss();
                        this.progressDialog = null;
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
                this.donePressed = false;
                this.doneButton.performClick();
            }
            showAvatarProgress(false, true);
            return;
        }
        this.avatar = smallSize.location;
        this.avatarBig = bigSize.location;
        this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable, this.currentChat);
        showAvatarProgress(true, false);
    }

    public String getInitialSearchString() {
        return this.nameTextView.getText().toString();
    }

    private boolean checkDiscard() {
        if ((this.info == null || !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory == this.historyHidden) && this.imageUpdater.uploadingImage == null && this.currentChat.title.equals(this.nameTextView.getText().toString()) && ((this.descriptionTextView == null || this.info == null || this.info.about.equals(this.descriptionTextView.getText().toString())) && this.signMessages == this.currentChat.signatures && this.uploadedAvatar == null && (this.avatar != null || !(this.currentChat.photo instanceof TL_chatPhoto)))) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", R.string.UserRestrictionsApplyChanges));
        if (this.isChannel) {
            builder.setMessage(LocaleController.getString("ChannelSettingsChangedAlert", R.string.ChannelSettingsChangedAlert));
        } else {
            builder.setMessage(LocaleController.getString("GroupSettingsChangedAlert", R.string.GroupSettingsChangedAlert));
        }
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", R.string.ApplyTheme), new ChatEditActivity$$Lambda$14(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new ChatEditActivity$$Lambda$15(this));
        showDialog(builder.create());
        return false;
    }

    private int getAdminCount() {
        if (this.info == null) {
            return 1;
        }
        int count = 0;
        int N = this.info.participants.participants.size();
        for (int a = 0; a < N; a++) {
            ChatParticipant chatParticipant = (ChatParticipant) this.info.participants.participants.get(a);
            if ((chatParticipant instanceof TL_chatParticipantAdmin) || (chatParticipant instanceof TL_chatParticipantCreator)) {
                count++;
            }
        }
        return count;
    }

    private void processDone() {
        if (!this.donePressed) {
            if (this.nameTextView.length() == 0) {
                Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(200);
                }
                AndroidUtilities.shakeView(this.nameTextView, 2.0f, 0);
                return;
            }
            this.donePressed = true;
            if (ChatObject.isChannel(this.currentChat) || this.historyHidden) {
                if (!(this.info == null || !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory == this.historyHidden)) {
                    this.info.hidden_prehistory = this.historyHidden;
                    MessagesController.getInstance(this.currentAccount).toogleChannelInvitesHistory(this.chatId, this.historyHidden);
                }
                if (this.imageUpdater.uploadingImage != null) {
                    this.createAfterUpload = true;
                    this.progressDialog = new AlertDialog(getParentActivity(), 3);
                    this.progressDialog.setOnCancelListener(new ChatEditActivity$$Lambda$17(this));
                    this.progressDialog.show();
                    return;
                }
                if (!this.currentChat.title.equals(this.nameTextView.getText().toString())) {
                    MessagesController.getInstance(this.currentAccount).changeChatTitle(this.chatId, this.nameTextView.getText().toString());
                }
                if (!(this.descriptionTextView == null || this.info == null || this.info.about.equals(this.descriptionTextView.getText().toString()))) {
                    MessagesController.getInstance(this.currentAccount).updateChatAbout(this.chatId, this.descriptionTextView.getText().toString(), this.info);
                }
                if (this.signMessages != this.currentChat.signatures) {
                    this.currentChat.signatures = true;
                    MessagesController.getInstance(this.currentAccount).toogleChannelSignatures(this.chatId, this.signMessages);
                }
                if (this.uploadedAvatar != null) {
                    MessagesController.getInstance(this.currentAccount).changeChatAvatar(this.chatId, this.uploadedAvatar, this.avatar, this.avatarBig);
                } else if (this.avatar == null && (this.currentChat.photo instanceof TL_chatPhoto)) {
                    MessagesController.getInstance(this.currentAccount).changeChatAvatar(this.chatId, null, null, null);
                }
                lambda$checkDiscard$18$ChatUsersActivity();
                return;
            }
            MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chatId, new ChatEditActivity$$Lambda$16(this));
        }
    }

    final /* synthetic */ void lambda$processDone$19$ChatEditActivity(int param) {
        this.chatId = param;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(param));
        this.donePressed = false;
        this.info.hidden_prehistory = true;
        processDone();
    }

    final /* synthetic */ void lambda$processDone$20$ChatEditActivity(DialogInterface dialog) {
        this.createAfterUpload = false;
        this.progressDialog = null;
        this.donePressed = false;
    }

    private void showAvatarProgress(final boolean show, boolean animated) {
        if (this.avatarEditor != null) {
            if (this.avatarAnimation != null) {
                this.avatarAnimation.cancel();
                this.avatarAnimation = null;
            }
            if (animated) {
                this.avatarAnimation = new AnimatorSet();
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                if (show) {
                    this.avatarProgressView.setVisibility(0);
                    animatorSet = this.avatarAnimation;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{0.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f});
                    animatorSet.playTogether(animatorArr);
                } else {
                    this.avatarEditor.setVisibility(0);
                    animatorSet = this.avatarAnimation;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{1.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ChatEditActivity.this.avatarAnimation != null && ChatEditActivity.this.avatarEditor != null) {
                            if (show) {
                                ChatEditActivity.this.avatarEditor.setVisibility(4);
                            } else {
                                ChatEditActivity.this.avatarProgressView.setVisibility(4);
                            }
                            ChatEditActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        ChatEditActivity.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (show) {
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

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.imageUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        if (!(this.imageUpdater == null || this.imageUpdater.currentPicturePath == null)) {
            args.putString("path", this.imageUpdater.currentPicturePath);
        }
        if (this.nameTextView != null) {
            String text = this.nameTextView.getText().toString();
            if (text != null && text.length() != 0) {
                args.putString("nameTextView", text);
            }
        }
    }

    public void restoreSelfArgs(Bundle args) {
        if (this.imageUpdater != null) {
            this.imageUpdater.currentPicturePath = args.getString("path");
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

    private void updateFields(boolean updateChat) {
        TextCell textCell;
        int i;
        if (updateChat) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
            if (chat != null) {
                this.currentChat = chat;
            }
        }
        boolean isPrivate = TextUtils.isEmpty(this.currentChat.username);
        if (this.historyCell != null) {
            this.historyCell.setVisibility(isPrivate ? 0 : 8);
        }
        if (this.settingsSectionCell != null && this.signCell == null && (this.historyCell == null || this.historyCell.getVisibility() != 0)) {
            this.settingsSectionCell.setVisibility(8);
        }
        if (this.logCell != null) {
            textCell = this.logCell;
            if (!this.currentChat.megagroup || (this.info != null && this.info.participants_count > 200)) {
                i = 0;
            } else {
                i = 8;
            }
            textCell.setVisibility(i);
        }
        if (this.typeCell != null) {
            String type = this.isChannel ? isPrivate ? LocaleController.getString("TypePrivate", R.string.TypePrivate) : LocaleController.getString("TypePublic", R.string.TypePublic) : isPrivate ? LocaleController.getString("TypePrivateGroup", R.string.TypePrivateGroup) : LocaleController.getString("TypePublicGroup", R.string.TypePublicGroup);
            if (this.isChannel) {
                this.typeCell.setTextAndValue(LocaleController.getString("ChannelType", R.string.ChannelType), type, true);
            } else {
                this.typeCell.setTextAndValue(LocaleController.getString("GroupType", R.string.GroupType), type, true);
            }
        }
        if (!(this.info == null || this.historyCell == null)) {
            this.historyCell.setTextAndValue(LocaleController.getString("ChatHistory", R.string.ChatHistory), this.historyHidden ? LocaleController.getString("ChatHistoryHidden", R.string.ChatHistoryHidden) : LocaleController.getString("ChatHistoryVisible", R.string.ChatHistoryVisible), false);
        }
        if (this.stickersCell != null) {
            if (this.info.stickerset != null) {
                this.stickersCell.setTextAndValue(LocaleController.getString("GroupStickers", R.string.GroupStickers), this.info.stickerset.title, false);
            } else {
                this.stickersCell.setText(LocaleController.getString("GroupStickers", R.string.GroupStickers), false);
            }
        }
        if (this.membersCell != null) {
            TextCell textCell2;
            String string;
            boolean z;
            if (this.info != null) {
                String format;
                if (this.isChannel) {
                    this.membersCell.setTextAndValueAndIcon(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers), String.format("%d", new Object[]{Integer.valueOf(this.info.participants_count)}), R.drawable.actions_viewmembers, true);
                    textCell2 = this.blockCell;
                    string = LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist);
                    format = String.format("%d", new Object[]{Integer.valueOf(Math.max(this.info.banned_count, this.info.kicked_count))});
                    if (this.logCell == null || this.logCell.getVisibility() != 0) {
                        z = false;
                    } else {
                        z = true;
                    }
                    textCell2.setTextAndValueAndIcon(string, format, R.drawable.actions_removed, z);
                } else {
                    String format2;
                    if (ChatObject.isChannel(this.currentChat)) {
                        textCell = this.membersCell;
                        format = LocaleController.getString("ChannelMembers", R.string.ChannelMembers);
                        format2 = String.format("%d", new Object[]{Integer.valueOf(this.info.participants_count)});
                        if (this.logCell == null || this.logCell.getVisibility() != 0) {
                            z = false;
                        } else {
                            z = true;
                        }
                        textCell.setTextAndValueAndIcon(format, format2, R.drawable.actions_viewmembers, z);
                    } else {
                        textCell = this.membersCell;
                        format = LocaleController.getString("ChannelMembers", R.string.ChannelMembers);
                        format2 = String.format("%d", new Object[]{Integer.valueOf(this.info.participants.participants.size())});
                        z = this.logCell != null && this.logCell.getVisibility() == 0;
                        textCell.setTextAndValueAndIcon(format, format2, R.drawable.actions_viewmembers, z);
                    }
                    int count = 0;
                    if (this.currentChat.default_banned_rights != null) {
                        if (!this.currentChat.default_banned_rights.send_stickers) {
                            count = 0 + 1;
                        }
                        if (!this.currentChat.default_banned_rights.send_media) {
                            count++;
                        }
                        if (!this.currentChat.default_banned_rights.embed_links) {
                            count++;
                        }
                        if (!this.currentChat.default_banned_rights.send_messages) {
                            count++;
                        }
                        if (!this.currentChat.default_banned_rights.pin_messages) {
                            count++;
                        }
                        if (!this.currentChat.default_banned_rights.send_polls) {
                            count++;
                        }
                        if (!this.currentChat.default_banned_rights.invite_users) {
                            count++;
                        }
                        if (!this.currentChat.default_banned_rights.change_info) {
                            count++;
                        }
                    } else {
                        count = 8;
                    }
                    this.blockCell.setTextAndValueAndIcon(LocaleController.getString("ChannelPermissions", R.string.ChannelPermissions), String.format("%d/%d", new Object[]{Integer.valueOf(count), Integer.valueOf(8)}), R.drawable.actions_permissions, true);
                }
                textCell2 = this.adminCell;
                string = LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators);
                format = "%d";
                Object[] objArr = new Object[1];
                if (ChatObject.isChannel(this.currentChat)) {
                    i = this.info.admins_count;
                } else {
                    i = getAdminCount();
                }
                objArr[0] = Integer.valueOf(i);
                textCell2.setTextAndValueAndIcon(string, String.format(format, objArr), R.drawable.actions_addadmin, true);
            } else {
                if (this.isChannel) {
                    this.membersCell.setTextAndIcon(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers), R.drawable.actions_viewmembers, true);
                    textCell2 = this.blockCell;
                    string = LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist);
                    z = this.logCell != null && this.logCell.getVisibility() == 0;
                    textCell2.setTextAndIcon(string, R.drawable.actions_removed, z);
                } else {
                    textCell2 = this.membersCell;
                    string = LocaleController.getString("ChannelMembers", R.string.ChannelMembers);
                    if (this.logCell == null || this.logCell.getVisibility() != 0) {
                        z = false;
                    } else {
                        z = true;
                    }
                    textCell2.setTextAndIcon(string, R.drawable.actions_viewmembers, z);
                    this.blockCell.setTextAndIcon(LocaleController.getString("ChannelPermissions", R.string.ChannelPermissions), R.drawable.actions_permissions, true);
                }
                this.adminCell.setTextAndIcon(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators), R.drawable.actions_addadmin, true);
            }
        }
        if (this.stickersCell != null && this.info != null) {
            if (this.info.stickerset != null) {
                this.stickersCell.setTextAndValue(LocaleController.getString("GroupStickers", R.string.GroupStickers), this.info.stickerset.title, false);
            } else {
                this.stickersCell.setText(LocaleController.getString("GroupStickers", R.string.GroupStickers), false);
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new ChatEditActivity$$Lambda$18(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[57];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[5] = new ThemeDescription(this.membersCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[6] = new ThemeDescription(this.membersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[7] = new ThemeDescription(this.membersCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        themeDescriptionArr[8] = new ThemeDescription(this.adminCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[9] = new ThemeDescription(this.adminCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[10] = new ThemeDescription(this.adminCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        themeDescriptionArr[11] = new ThemeDescription(this.blockCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[12] = new ThemeDescription(this.blockCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[13] = new ThemeDescription(this.blockCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        themeDescriptionArr[14] = new ThemeDescription(this.logCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[15] = new ThemeDescription(this.logCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[16] = new ThemeDescription(this.logCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        themeDescriptionArr[17] = new ThemeDescription(this.typeCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[18] = new ThemeDescription(this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[19] = new ThemeDescription(this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[20] = new ThemeDescription(this.historyCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[21] = new ThemeDescription(this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[22] = new ThemeDescription(this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[23] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[24] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText");
        themeDescriptionArr[25] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField");
        themeDescriptionArr[26] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated");
        themeDescriptionArr[27] = new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[28] = new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText");
        themeDescriptionArr[29] = new ThemeDescription(this.avatarContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[30] = new ThemeDescription(this.settingsContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[31] = new ThemeDescription(this.typeEditContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[32] = new ThemeDescription(this.deleteContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[33] = new ThemeDescription(this.stickersContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[34] = new ThemeDescription(this.infoContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[35] = new ThemeDescription(this.settingsTopSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[36] = new ThemeDescription(this.settingsSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[37] = new ThemeDescription(this.deleteInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[38] = new ThemeDescription(this.infoSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[39] = new ThemeDescription(this.signCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[40] = new ThemeDescription(this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[41] = new ThemeDescription(this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrack");
        themeDescriptionArr[42] = new ThemeDescription(this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        themeDescriptionArr[43] = new ThemeDescription(this.deleteCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[44] = new ThemeDescription(this.deleteCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteRedText5");
        themeDescriptionArr[45] = new ThemeDescription(this.stickersCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[46] = new ThemeDescription(this.stickersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[47] = new ThemeDescription(this.stickersInfoCell3, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[48] = new ThemeDescription(this.stickersInfoCell3, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[49] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, cellDelegate, "avatar_text");
        themeDescriptionArr[50] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundRed");
        themeDescriptionArr[51] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundOrange");
        themeDescriptionArr[52] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundViolet");
        themeDescriptionArr[53] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundGreen");
        themeDescriptionArr[54] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundCyan");
        themeDescriptionArr[55] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundBlue");
        themeDescriptionArr[56] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundPink");
        return themeDescriptionArr;
    }

    final /* synthetic */ void lambda$getThemeDescriptions$21$ChatEditActivity() {
        if (this.avatarImage != null) {
            this.avatarDrawable.setInfo(5, null, null, false);
            this.avatarImage.invalidate();
        }
    }
}
