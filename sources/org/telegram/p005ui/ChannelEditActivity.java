package org.telegram.p005ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.widget.Toast;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.BottomSheet.Builder;
import org.telegram.p005ui.ActionBar.C0403ActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.p005ui.Cells.DialogRadioCell;
import org.telegram.p005ui.Cells.HeaderCell;
import org.telegram.p005ui.Cells.RadioButtonCell;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.Cells.TextBlockCell;
import org.telegram.p005ui.Cells.TextCell;
import org.telegram.p005ui.Cells.TextCheckCell;
import org.telegram.p005ui.Cells.TextDetailCell;
import org.telegram.p005ui.Cells.TextInfoPrivacyCell;
import org.telegram.p005ui.Cells.TextSettingsCell;
import org.telegram.p005ui.Components.AvatarDrawable;
import org.telegram.p005ui.Components.BackupImageView;
import org.telegram.p005ui.Components.EditTextBoldCursor;
import org.telegram.p005ui.Components.EditTextEmoji;
import org.telegram.p005ui.Components.ImageUpdater;
import org.telegram.p005ui.Components.ImageUpdater.ImageUpdaterDelegate;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.SizeNotifierFrameLayout;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_channels_exportInvite;
import org.telegram.tgnet.TLRPC.TL_chatInviteExported;
import org.telegram.tgnet.TLRPC.TL_chatPhoto;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_secureFile;

/* renamed from: org.telegram.ui.ChannelEditActivity */
public class ChannelEditActivity extends BaseFragment implements NotificationCenterDelegate, ImageUpdaterDelegate {
    private static final int done_button = 1;
    private TextDetailCell addMembersCell;
    private TextCell adminCell;
    private FileLocation avatar;
    private LinearLayout avatarContainer;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImage;
    private TextCell blockCell;
    private int chatId;
    private boolean createAfterUpload;
    private Chat currentChat;
    private TextSettingsCell deleteCell;
    private FrameLayout deleteContainer;
    private ShadowSectionCell deleteInfoCell;
    private boolean democracy;
    private EditTextBoldCursor descriptionTextView;
    private View doneButton;
    private boolean donePressed;
    private TextDetailCell historyCell;
    private boolean historyHidden;
    private ImageUpdater imageUpdater = new ImageUpdater();
    private ChatFull info;
    private LinearLayout infoContainer;
    private ShadowSectionCell infoSectionCell;
    private ExportedChatInvite invite;
    private LinearLayout linkContainer;
    private HeaderCell linkHeaderCell;
    private ShadowSectionCell linkSectionCell;
    private TextBlockCell linkTextCell;
    private boolean loadingInvite;
    private TextCell membersCell;
    private EditTextEmoji nameTextView;
    private AlertDialog progressDialog;
    private TextCell revokeCell;
    private LinearLayout settingsContainer;
    private View settingsLineView;
    private ShadowSectionCell settingsSectionCell;
    private TextCheckCell signCell;
    private FrameLayout signContainer;
    private TextInfoPrivacyCell signInfoCell;
    private boolean signMessages;
    private TextSettingsCell stickersCell;
    private TextInfoPrivacyCell stickersInfoCell3;
    private TextDetailCell typeCell;
    private InputFile uploadedAvatar;

    /* renamed from: org.telegram.ui.ChannelEditActivity$4 */
    class C05614 implements TextWatcher {
        C05614() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void afterTextChanged(Editable editable) {
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity$1 */
    class C12891 extends ActionBarMenuOnItemClick {
        C12891() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChannelEditActivity.this.lambda$checkDiscard$70$PassportActivity();
            } else if (id == 1 && !ChannelEditActivity.this.donePressed) {
                if (ChannelEditActivity.this.nameTextView.length() == 0) {
                    Vibrator v = (Vibrator) ChannelEditActivity.this.getParentActivity().getSystemService("vibrator");
                    if (v != null) {
                        v.vibrate(200);
                    }
                    AndroidUtilities.shakeView(ChannelEditActivity.this.nameTextView, 2.0f, 0);
                    return;
                }
                ChannelEditActivity.this.donePressed = true;
                if (ChannelEditActivity.this.imageUpdater.uploadingImage != null) {
                    ChannelEditActivity.this.createAfterUpload = true;
                    ChannelEditActivity.this.progressDialog = new AlertDialog(ChannelEditActivity.this.getParentActivity(), 1);
                    ChannelEditActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                    ChannelEditActivity.this.progressDialog.setCanceledOnTouchOutside(false);
                    ChannelEditActivity.this.progressDialog.setCancelable(false);
                    ChannelEditActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new ChannelEditActivity$1$$Lambda$0(this));
                    ChannelEditActivity.this.progressDialog.show();
                    return;
                }
                if (!(ChannelEditActivity.this.info == null || ChannelEditActivity.this.info.hidden_prehistory == ChannelEditActivity.this.historyHidden)) {
                    ChannelEditActivity.this.info.hidden_prehistory = ChannelEditActivity.this.historyHidden;
                    MessagesController.getInstance(ChannelEditActivity.this.currentAccount).toogleChannelInvitesHistory(ChannelEditActivity.this.chatId, ChannelEditActivity.this.historyHidden);
                }
                if (ChannelEditActivity.this.democracy != ChannelEditActivity.this.currentChat.democracy) {
                    ChannelEditActivity.this.currentChat.democracy = ChannelEditActivity.this.democracy;
                    MessagesController.getInstance(ChannelEditActivity.this.currentAccount).toogleChannelInvites(ChannelEditActivity.this.chatId, ChannelEditActivity.this.democracy);
                }
                if (!ChannelEditActivity.this.currentChat.title.equals(ChannelEditActivity.this.nameTextView.getText().toString())) {
                    MessagesController.getInstance(ChannelEditActivity.this.currentAccount).changeChatTitle(ChannelEditActivity.this.chatId, ChannelEditActivity.this.nameTextView.getText().toString());
                }
                if (!(ChannelEditActivity.this.info == null || ChannelEditActivity.this.info.about.equals(ChannelEditActivity.this.descriptionTextView.getText().toString()))) {
                    MessagesController.getInstance(ChannelEditActivity.this.currentAccount).updateChannelAbout(ChannelEditActivity.this.chatId, ChannelEditActivity.this.descriptionTextView.getText().toString(), ChannelEditActivity.this.info);
                }
                if (ChannelEditActivity.this.signMessages != ChannelEditActivity.this.currentChat.signatures) {
                    ChannelEditActivity.this.currentChat.signatures = true;
                    MessagesController.getInstance(ChannelEditActivity.this.currentAccount).toogleChannelSignatures(ChannelEditActivity.this.chatId, ChannelEditActivity.this.signMessages);
                }
                if (ChannelEditActivity.this.uploadedAvatar != null) {
                    MessagesController.getInstance(ChannelEditActivity.this.currentAccount).changeChatAvatar(ChannelEditActivity.this.chatId, ChannelEditActivity.this.uploadedAvatar);
                } else if (ChannelEditActivity.this.avatar == null && (ChannelEditActivity.this.currentChat.photo instanceof TL_chatPhoto)) {
                    MessagesController.getInstance(ChannelEditActivity.this.currentAccount).changeChatAvatar(ChannelEditActivity.this.chatId, null);
                }
                ChannelEditActivity.this.lambda$checkDiscard$70$PassportActivity();
            }
        }

        final /* synthetic */ void lambda$onItemClick$0$ChannelEditActivity$1(DialogInterface dialog, int which) {
            ChannelEditActivity.this.createAfterUpload = false;
            ChannelEditActivity.this.progressDialog = null;
            ChannelEditActivity.this.donePressed = false;
            try {
                dialog.dismiss();
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
    }

    public ChannelEditActivity(Bundle args) {
        super(args);
        this.chatId = args.getInt("chat_id", 0);
    }

    public boolean onFragmentCreate() {
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        if (this.currentChat == null) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new ChannelEditActivity$$Lambda$0(this, countDownLatch));
            try {
                countDownLatch.await();
            } catch (Throwable e) {
                FileLog.m13e(e);
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
                    FileLog.m13e(e2);
                }
                if (this.info == null) {
                    return false;
                }
            }
        }
        this.democracy = this.currentChat.democracy;
        this.imageUpdater.parentFragment = this;
        this.imageUpdater.delegate = this;
        this.signMessages = this.currentChat.signatures;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        return super.onFragmentCreate();
    }

    final /* synthetic */ void lambda$onFragmentCreate$0$ChannelEditActivity(CountDownLatch countDownLatch) {
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
        updateFields();
    }

    public void onPause() {
        super.onPause();
        if (this.nameTextView != null) {
            this.nameTextView.onPause();
        }
    }

    public boolean onBackPressed() {
        if (this.nameTextView == null || !this.nameTextView.isPopupShowing()) {
            return true;
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
        this.actionBar.setActionBarMenuOnItemClick(new C12891());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.m9dp(56.0f));
        View c12902 = new SizeNotifierFrameLayout(context) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                heightSize -= getPaddingTop();
                measureChildWithMargins(ChannelEditActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int keyboardSize = getKeyboardHeight();
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == ChannelEditActivity.this.actionBar)) {
                        if (ChannelEditActivity.this.nameTextView == null || !ChannelEditActivity.this.nameTextView.isPopupView(child)) {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.m9dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec((heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                }
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int count = getChildCount();
                int paddingBottom = (getKeyboardHeight() > AndroidUtilities.m9dp(20.0f) || AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) ? 0 : ChannelEditActivity.this.nameTextView.getEmojiPadding();
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
                        if (ChannelEditActivity.this.nameTextView != null && ChannelEditActivity.this.nameTextView.isPopupView(child)) {
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
        c12902.setOnTouchListener(ChannelEditActivity$$Lambda$1.$instance);
        this.fragmentView = c12902;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        c12902 = new ScrollView(context);
        c12902.setFillViewport(true);
        c12902.addView(c12902, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout1 = new LinearLayout(context);
        c12902.addView(linearLayout1, new LayoutParams(-1, -2));
        linearLayout1.setOrientation(1);
        this.actionBar.setTitle(LocaleController.getString("ChannelEdit", R.string.ChannelEdit));
        this.avatarContainer = new LinearLayout(context);
        this.avatarContainer.setOrientation(1);
        this.avatarContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        linearLayout1.addView(this.avatarContainer, LayoutHelper.createLinear(-1, -2));
        FrameLayout frameLayout = new FrameLayout(context);
        this.avatarContainer.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.m9dp(32.0f));
        this.avatarDrawable.setInfo(5, null, null, false);
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
        Paint paint = new Paint(1);
        paint.setColor(NUM);
        final Paint paint2 = paint;
        ImageView avatarEditor = new ImageView(context) {
            protected void onDraw(Canvas canvas) {
                if (ChannelEditActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                    paint2.setAlpha((int) (85.0f * ChannelEditActivity.this.avatarImage.getImageReceiver().getCurrentAlpha()));
                    canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.m9dp(32.0f), paint2);
                }
                super.onDraw(canvas);
            }
        };
        avatarEditor.setImageResource(R.drawable.menu_camera_av);
        avatarEditor.setScaleType(ScaleType.CENTER);
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
        frameLayout.addView(avatarEditor, LayoutHelper.createFrame(64, 64.0f, i, f, 12.0f, f2, 12.0f));
        avatarEditor.setOnClickListener(new ChannelEditActivity$$Lambda$2(this));
        this.nameTextView = new EditTextEmoji((Activity) context, c12902, this);
        if (this.currentChat.megagroup) {
            this.nameTextView.setHint(LocaleController.getString("GroupName", R.string.GroupName));
        } else {
            this.nameTextView.setHint(LocaleController.getString("EnterChannelName", R.string.EnterChannelName));
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
        this.settingsContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        linearLayout1.addView(this.settingsContainer, LayoutHelper.createLinear(-1, -2));
        this.descriptionTextView = new EditTextBoldCursor(context);
        this.descriptionTextView.setTextSize(1, 16.0f);
        this.descriptionTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.descriptionTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.m9dp(6.0f));
        this.descriptionTextView.setBackgroundDrawable(null);
        this.descriptionTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.descriptionTextView.setInputType(180225);
        this.descriptionTextView.setImeOptions(6);
        this.descriptionTextView.setEnabled(ChatObject.canChangeChatInfo(this.currentChat));
        this.descriptionTextView.setFocusable(this.descriptionTextView.isEnabled());
        this.descriptionTextView.setFilters(new InputFilter[]{new LengthFilter(255)});
        this.descriptionTextView.setHint(LocaleController.getString("DescriptionOptionalPlaceholder", R.string.DescriptionOptionalPlaceholder));
        this.descriptionTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.descriptionTextView.setCursorSize(AndroidUtilities.m9dp(20.0f));
        this.descriptionTextView.setCursorWidth(1.5f);
        this.settingsContainer.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 23.0f, 12.0f, 23.0f, 6.0f));
        this.descriptionTextView.setOnEditorActionListener(new ChannelEditActivity$$Lambda$3(this));
        this.descriptionTextView.addTextChangedListener(new C05614());
        if ((this.currentChat.creator && (this.info == null || this.info.can_set_username)) || (this.currentChat.megagroup && (this.currentChat.creator || (this.currentChat.admin_rights != null && this.currentChat.admin_rights.change_info)))) {
            this.settingsLineView = new View(context);
            this.settingsLineView.setBackgroundColor(Theme.getColor(Theme.key_divider));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, 1);
            layoutParams.leftMargin = LocaleController.isRTL ? 0 : AndroidUtilities.m9dp(19.0f);
            layoutParams.rightMargin = LocaleController.isRTL ? AndroidUtilities.m9dp(19.0f) : 0;
            this.settingsContainer.addView(this.settingsLineView, layoutParams);
        }
        if (this.currentChat.creator && (this.info == null || this.info.can_set_username)) {
            this.typeCell = new TextDetailCell(context);
            this.typeCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.settingsContainer.addView(this.typeCell, LayoutHelper.createLinear(-1, -2));
            this.typeCell.setOnClickListener(new ChannelEditActivity$$Lambda$4(this));
        }
        if (this.currentChat.megagroup && (this.currentChat.creator || (this.currentChat.admin_rights != null && this.currentChat.admin_rights.change_info))) {
            this.addMembersCell = new TextDetailCell(context);
            this.addMembersCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.settingsContainer.addView(this.addMembersCell, LayoutHelper.createLinear(-1, -2));
            this.addMembersCell.setOnClickListener(new ChannelEditActivity$$Lambda$5(this, context));
            this.historyCell = new TextDetailCell(context);
            this.historyCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.settingsContainer.addView(this.historyCell, LayoutHelper.createLinear(-1, -2));
            this.historyCell.setOnClickListener(new ChannelEditActivity$$Lambda$6(this, context));
        }
        this.settingsSectionCell = new ShadowSectionCell(context);
        linearLayout1.addView(this.settingsSectionCell, LayoutHelper.createLinear(-1, -2));
        if (this.currentChat.creator && (this.info == null || this.info.can_set_username)) {
            this.linkContainer = new LinearLayout(context);
            this.linkContainer.setOrientation(1);
            this.linkContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            linearLayout1.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
            this.linkHeaderCell = new HeaderCell(context, 23);
            this.linkHeaderCell.setText(LocaleController.getString("ChannelInviteLinkTitle", R.string.ChannelInviteLinkTitle));
            this.linkContainer.addView(this.linkHeaderCell);
            this.linkTextCell = new TextBlockCell(context);
            this.linkTextCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.linkContainer.addView(this.linkTextCell);
            this.linkTextCell.setOnClickListener(new ChannelEditActivity$$Lambda$7(this));
            this.revokeCell = new TextCell(context);
            this.revokeCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.revokeCell.setText(LocaleController.getString("RevokeLink", R.string.RevokeLink), false);
            this.linkContainer.addView(this.revokeCell, LayoutHelper.createLinear(-1, -2));
            this.revokeCell.setOnClickListener(new ChannelEditActivity$$Lambda$8(this));
            this.linkSectionCell = new ShadowSectionCell(context);
            linearLayout1.addView(this.linkSectionCell, LayoutHelper.createLinear(-1, -2));
        }
        if (this.currentChat.megagroup) {
            this.infoContainer = new LinearLayout(context);
            this.infoContainer.setOrientation(1);
            this.infoContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            linearLayout1.addView(this.infoContainer, LayoutHelper.createLinear(-1, -2));
            this.membersCell = new TextCell(context);
            this.membersCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.infoContainer.addView(this.membersCell, LayoutHelper.createLinear(-1, -2));
            this.membersCell.setOnClickListener(new ChannelEditActivity$$Lambda$9(this));
            this.adminCell = new TextCell(context);
            this.adminCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.infoContainer.addView(this.adminCell, LayoutHelper.createLinear(-1, -2));
            this.adminCell.setOnClickListener(new ChannelEditActivity$$Lambda$10(this));
            this.blockCell = new TextCell(context);
            this.blockCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.infoContainer.addView(this.blockCell, LayoutHelper.createLinear(-1, -2));
            this.blockCell.setOnClickListener(new ChannelEditActivity$$Lambda$11(this));
            this.infoSectionCell = new ShadowSectionCell(context);
            linearLayout1.addView(this.infoSectionCell, LayoutHelper.createLinear(-1, -2));
        }
        this.signContainer = new FrameLayout(context);
        this.signContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.deleteContainer = new FrameLayout(context);
        this.deleteContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        linearLayout1.addView(this.deleteContainer, LayoutHelper.createLinear(-1, -2));
        linearLayout1.addView(this.signContainer, LayoutHelper.createLinear(-1, -2));
        if (!this.currentChat.megagroup) {
            this.signCell = new TextCheckCell(context);
            this.signCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.signCell.setTextAndCheck(LocaleController.getString("ChannelSignMessages", R.string.ChannelSignMessages), this.signMessages, false);
            this.signContainer.addView(this.signCell, LayoutHelper.createFrame(-1, -2.0f));
            this.signCell.setOnClickListener(new ChannelEditActivity$$Lambda$12(this));
            this.signInfoCell = new TextInfoPrivacyCell(context);
            this.signInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            this.signInfoCell.setText(LocaleController.getString("ChannelSignMessagesInfo", R.string.ChannelSignMessagesInfo));
            linearLayout1.addView(this.signInfoCell, LayoutHelper.createLinear(-1, -2));
        } else if (this.info != null && this.info.can_set_stickers) {
            this.stickersCell = new TextSettingsCell(context);
            this.stickersCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.stickersCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.deleteContainer.addView(this.stickersCell, LayoutHelper.createFrame(-1, -2.0f));
            this.stickersCell.setOnClickListener(new ChannelEditActivity$$Lambda$13(this));
            this.stickersInfoCell3 = new TextInfoPrivacyCell(context);
            this.stickersInfoCell3.setText(LocaleController.getString("GroupStickersInfo", R.string.GroupStickersInfo));
            linearLayout1.addView(this.stickersInfoCell3, LayoutHelper.createLinear(-1, -2));
        }
        if (this.currentChat.creator) {
            this.deleteContainer = new FrameLayout(context);
            this.deleteContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            linearLayout1.addView(this.deleteContainer, LayoutHelper.createLinear(-1, -2));
            this.deleteCell = new TextSettingsCell(context);
            this.deleteCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
            this.deleteCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (this.currentChat.megagroup) {
                this.deleteCell.setText(LocaleController.getString("DeleteMega", R.string.DeleteMega), false);
            } else {
                this.deleteCell.setText(LocaleController.getString("ChannelDelete", R.string.ChannelDelete), false);
            }
            this.deleteContainer.addView(this.deleteCell, LayoutHelper.createFrame(-1, -2.0f));
            this.deleteCell.setOnClickListener(new ChannelEditActivity$$Lambda$14(this));
            this.deleteInfoCell = new ShadowSectionCell(context);
            this.deleteInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            linearLayout1.addView(this.deleteInfoCell, LayoutHelper.createLinear(-1, -2));
        } else if (!this.currentChat.megagroup) {
            this.signInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        } else if (this.stickersInfoCell3 == null) {
            this.infoSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        }
        if (this.stickersInfoCell3 != null) {
            if (this.deleteInfoCell == null) {
                this.stickersInfoCell3.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            } else {
                this.stickersInfoCell3.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            }
        }
        this.nameTextView.setText(this.currentChat.title);
        this.nameTextView.setSelection(this.nameTextView.length());
        if (this.info != null) {
            this.descriptionTextView.setText(this.info.about);
        }
        if (this.currentChat.photo != null) {
            this.avatar = this.currentChat.photo.photo_small;
            this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable, this.currentChat);
        } else {
            this.avatarImage.setImageDrawable(this.avatarDrawable);
        }
        updateFields();
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$3$ChannelEditActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new ChannelEditActivity$$Lambda$22(this));
    }

    final /* synthetic */ void lambda$null$2$ChannelEditActivity() {
        this.avatar = null;
        this.uploadedAvatar = null;
        this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable, this.currentChat);
    }

    final /* synthetic */ boolean lambda$createView$4$ChannelEditActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6 || this.doneButton == null) {
            return false;
        }
        this.doneButton.performClick();
        return true;
    }

    final /* synthetic */ void lambda$createView$5$ChannelEditActivity(View v) {
        ChannelEditInfoActivity fragment = new ChannelEditInfoActivity(this.chatId);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    final /* synthetic */ void lambda$createView$7$ChannelEditActivity(Context context, View v) {
        Builder builder = new Builder(context);
        builder.setApplyTopPadding(false);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        HeaderCell headerCell = new HeaderCell(context, true, 23);
        headerCell.setHeight(47);
        headerCell.setText(LocaleController.getString("WhoCanAddMembers", R.string.WhoCanAddMembers));
        linearLayout.addView(headerCell);
        LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
        linearLayoutInviteContainer.setOrientation(1);
        linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(-1, -2));
        DialogRadioCell[] buttons = new DialogRadioCell[2];
        for (int a = 0; a < 2; a++) {
            buttons[a] = new DialogRadioCell(context, true);
            buttons[a].setTag(Integer.valueOf(a));
            buttons[a].setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (a == 0) {
                buttons[a].setText(LocaleController.getString("WhoCanAddMembersAllMembers", R.string.WhoCanAddMembersAllMembers), this.democracy, true);
            } else {
                buttons[a].setText(LocaleController.getString("WhoCanAddMembersAdmins", R.string.WhoCanAddMembersAdmins), !this.democracy, false);
            }
            linearLayoutInviteContainer.addView(buttons[a], LayoutHelper.createLinear(-1, -2));
            buttons[a].setOnClickListener(new ChannelEditActivity$$Lambda$21(this, buttons, builder));
        }
        builder.setCustomView(linearLayout);
        showDialog(builder.create());
    }

    final /* synthetic */ void lambda$null$6$ChannelEditActivity(DialogRadioCell[] buttons, Builder builder, View v2) {
        boolean z;
        boolean z2 = true;
        Integer tag = (Integer) v2.getTag();
        buttons[0].setChecked(tag.intValue() == 0, true);
        DialogRadioCell dialogRadioCell = buttons[1];
        if (tag.intValue() == 1) {
            z = true;
        } else {
            z = false;
        }
        dialogRadioCell.setChecked(z, true);
        if (tag.intValue() != 0) {
            z2 = false;
        }
        this.democracy = z2;
        builder.getDismissRunnable().run();
        updateFields();
    }

    final /* synthetic */ void lambda$createView$9$ChannelEditActivity(Context context, View v) {
        Builder builder = new Builder(context);
        builder.setApplyTopPadding(false);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        HeaderCell headerCell = new HeaderCell(context, true, 23);
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
            } else {
                buttons[a].setTextAndValue(LocaleController.getString("ChatHistoryHidden", R.string.ChatHistoryHidden), LocaleController.getString("ChatHistoryHiddenInfo", R.string.ChatHistoryHiddenInfo), false, this.historyHidden);
            }
            linearLayoutInviteContainer.addView(buttons[a], LayoutHelper.createLinear(-1, -2));
            buttons[a].setOnClickListener(new ChannelEditActivity$$Lambda$20(this, buttons, builder));
        }
        builder.setCustomView(linearLayout);
        showDialog(builder.create());
    }

    final /* synthetic */ void lambda$null$8$ChannelEditActivity(RadioButtonCell[] buttons, Builder builder, View v2) {
        boolean z;
        boolean z2 = true;
        Integer tag = (Integer) v2.getTag();
        buttons[0].setChecked(tag.intValue() == 0, true);
        RadioButtonCell radioButtonCell = buttons[1];
        if (tag.intValue() == 1) {
            z = true;
        } else {
            z = false;
        }
        radioButtonCell.setChecked(z, true);
        if (tag.intValue() != 1) {
            z2 = false;
        }
        this.historyHidden = z2;
        builder.getDismissRunnable().run();
        updateFields();
    }

    final /* synthetic */ void lambda$createView$10$ChannelEditActivity(View v) {
        if (this.invite != null) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.invite.link));
                Toast.makeText(getParentActivity(), LocaleController.getString("LinkCopied", R.string.LinkCopied), 0).show();
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
    }

    final /* synthetic */ void lambda$createView$11$ChannelEditActivity(View v) {
        generateLink(true);
    }

    final /* synthetic */ void lambda$createView$12$ChannelEditActivity(View v) {
        Bundle args = new Bundle();
        args.putInt("chat_id", this.chatId);
        args.putInt("type", 2);
        presentFragment(new ChannelUsersActivity(args));
    }

    final /* synthetic */ void lambda$createView$13$ChannelEditActivity(View v) {
        Bundle args = new Bundle();
        args.putInt("chat_id", this.chatId);
        args.putInt("type", 1);
        presentFragment(new ChannelUsersActivity(args));
    }

    final /* synthetic */ void lambda$createView$14$ChannelEditActivity(View v) {
        Bundle args = new Bundle();
        args.putInt("chat_id", this.chatId);
        args.putInt("type", 0);
        presentFragment(new ChannelUsersActivity(args));
    }

    final /* synthetic */ void lambda$createView$15$ChannelEditActivity(View v) {
        this.signMessages = !this.signMessages;
        ((TextCheckCell) v).setChecked(this.signMessages);
    }

    final /* synthetic */ void lambda$createView$16$ChannelEditActivity(View v) {
        GroupStickersActivity groupStickersActivity = new GroupStickersActivity(this.currentChat.f78id);
        groupStickersActivity.setInfo(this.info);
        presentFragment(groupStickersActivity);
    }

    final /* synthetic */ void lambda$createView$18$ChannelEditActivity(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        if (this.currentChat.megagroup) {
            builder.setMessage(LocaleController.getString("MegaDeleteAlert", R.string.MegaDeleteAlert));
        } else {
            builder.setMessage(LocaleController.getString("ChannelDeleteAlert", R.string.ChannelDeleteAlert));
        }
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ChannelEditActivity$$Lambda$19(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    final /* synthetic */ void lambda$null$17$ChannelEditActivity(DialogInterface dialogInterface, int i) {
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(-((long) this.chatId)));
        } else {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chatId, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId())), this.info, true);
        lambda$checkDiscard$70$PassportActivity();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad) {
            ChatFull chatFull = args[0];
            if (chatFull.f79id == this.chatId) {
                if (this.info == null) {
                    this.descriptionTextView.setText(chatFull.about);
                }
                this.info = chatFull;
                if (!(chatFull.exported_invite instanceof TL_chatInviteExported) || TextUtils.isEmpty(chatFull.exported_invite.link)) {
                    generateLink(false);
                } else {
                    this.invite = chatFull.exported_invite;
                }
                this.historyHidden = this.info.hidden_prehistory;
                updateFields();
            }
        }
    }

    public void didUploadedPhoto(InputFile file, PhotoSize photoSize, TL_secureFile secureFile) {
        AndroidUtilities.runOnUIThread(new ChannelEditActivity$$Lambda$15(this, file, photoSize));
    }

    final /* synthetic */ void lambda$didUploadedPhoto$19$ChannelEditActivity(InputFile file, PhotoSize photoSize) {
        this.uploadedAvatar = file;
        this.avatar = photoSize.location;
        this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable, this.currentChat);
        if (this.createAfterUpload) {
            try {
                if (this.progressDialog != null && this.progressDialog.isShowing()) {
                    this.progressDialog.dismiss();
                    this.progressDialog = null;
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
            this.donePressed = false;
            this.doneButton.performClick();
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
            if (!(chatFull.exported_invite instanceof TL_chatInviteExported) || TextUtils.isEmpty(chatFull.exported_invite.link)) {
                generateLink(false);
            } else {
                this.invite = chatFull.exported_invite;
            }
            this.historyHidden = this.info.hidden_prehistory;
        }
    }

    private void updateFields() {
        boolean isPrivate = TextUtils.isEmpty(this.currentChat.username);
        if (this.linkContainer != null) {
            if (isPrivate) {
                this.linkTextCell.setText(this.invite != null ? this.invite.link : LocaleController.getString("Loading", R.string.Loading), true);
            } else {
                this.linkTextCell.setText(String.format("https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/%s", new Object[]{this.currentChat.username}), isPrivate);
            }
            this.linkContainer.setPadding(0, 0, 0, isPrivate ? 0 : AndroidUtilities.m9dp(7.0f));
            this.revokeCell.setVisibility(isPrivate ? 0 : 8);
        }
        if (this.typeCell != null) {
            String type = this.currentChat.megagroup ? isPrivate ? LocaleController.getString("TypePrivateGroup", R.string.TypePrivateGroup) : LocaleController.getString("TypePublicGroup", R.string.TypePublicGroup) : isPrivate ? LocaleController.getString("TypePrivate", R.string.TypePrivate) : LocaleController.getString("TypePublic", R.string.TypePublic);
            if (this.currentChat.megagroup) {
                this.typeCell.setTextAndValue(LocaleController.getString("GroupType", R.string.GroupType), type, true);
            } else {
                this.typeCell.setTextAndValue(LocaleController.getString("ChannelType", R.string.ChannelType), type, true);
            }
        }
        if (this.addMembersCell != null) {
            this.addMembersCell.setTextAndValue(LocaleController.getString("WhoCanAddMembers", R.string.WhoCanAddMembers), this.democracy ? LocaleController.getString("WhoCanAddMembersAllMembers", R.string.WhoCanAddMembersAllMembers) : LocaleController.getString("WhoCanAddMembersAdmins", R.string.WhoCanAddMembersAdmins), true);
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
            if (this.info != null) {
                this.membersCell.setTextAndValueAndIcon(LocaleController.getString("ChannelMembers", R.string.ChannelMembers), String.format("%d", new Object[]{Integer.valueOf(this.info.participants_count)}), R.drawable.menu_newgroup, true);
                this.adminCell.setTextAndValueAndIcon(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators), String.format("%d", new Object[]{Integer.valueOf(this.info.admins_count)}), R.drawable.profile_admin, true);
                this.blockCell.setTextAndValueAndIcon(LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist), String.format("%d", new Object[]{Integer.valueOf(Math.max(this.info.banned_count, this.info.kicked_count))}), R.drawable.profile_ban, false);
            } else {
                this.membersCell.setTextAndIcon(LocaleController.getString("ChannelMembers", R.string.ChannelMembers), R.drawable.menu_newgroup, true);
                this.adminCell.setTextAndIcon(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators), R.drawable.profile_admin, true);
                this.blockCell.setTextAndIcon(LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist), R.drawable.profile_ban, false);
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

    private void generateLink(boolean force) {
        if (!this.loadingInvite) {
            if (this.invite == null || force) {
                this.loadingInvite = true;
                TL_channels_exportInvite req = new TL_channels_exportInvite();
                req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChannelEditActivity$$Lambda$16(this));
            }
        }
    }

    final /* synthetic */ void lambda$generateLink$21$ChannelEditActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ChannelEditActivity$$Lambda$18(this, error, response));
    }

    final /* synthetic */ void lambda$null$20$ChannelEditActivity(TL_error error, TLObject response) {
        if (error == null) {
            this.invite = (ExportedChatInvite) response;
            if (this.info != null) {
                this.info.exported_invite = this.invite;
            }
        }
        this.loadingInvite = false;
        if (this.linkTextCell != null) {
            this.linkTextCell.setText(this.invite != null ? this.invite.link : LocaleController.getString("Loading", R.string.Loading), false);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new ChannelEditActivity$$Lambda$17(this);
        r10 = new ThemeDescription[69];
        r10[6] = new ThemeDescription(this.revokeCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[7] = new ThemeDescription(this.membersCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[8] = new ThemeDescription(this.membersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[9] = new ThemeDescription(this.membersCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        r10[10] = new ThemeDescription(this.adminCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[11] = new ThemeDescription(this.adminCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[12] = new ThemeDescription(this.adminCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        r10[13] = new ThemeDescription(this.blockCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[14] = new ThemeDescription(this.blockCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[15] = new ThemeDescription(this.blockCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        r10[16] = new ThemeDescription(this.typeCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[17] = new ThemeDescription(this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[18] = new ThemeDescription(this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r10[19] = new ThemeDescription(this.addMembersCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[20] = new ThemeDescription(this.addMembersCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[21] = new ThemeDescription(this.addMembersCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r10[22] = new ThemeDescription(this.historyCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[23] = new ThemeDescription(this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[24] = new ThemeDescription(this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r10[25] = new ThemeDescription(this.linkTextCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[26] = new ThemeDescription(this.linkTextCell, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[27] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[28] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r10[29] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r10[30] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r10[31] = new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[32] = new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r10[33] = new ThemeDescription(this.avatarContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[34] = new ThemeDescription(this.settingsContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[35] = new ThemeDescription(this.signContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[36] = new ThemeDescription(this.deleteContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[37] = new ThemeDescription(this.infoContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[38] = new ThemeDescription(this.settingsLineView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider);
        r10[39] = new ThemeDescription(this.linkSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[40] = new ThemeDescription(this.settingsSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[41] = new ThemeDescription(this.deleteInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[42] = new ThemeDescription(this.infoSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[43] = new ThemeDescription(this.signCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[44] = new ThemeDescription(this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[45] = new ThemeDescription(this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        r10[46] = new ThemeDescription(this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        r10[47] = new ThemeDescription(this.signInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[48] = new ThemeDescription(this.signInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r10[49] = new ThemeDescription(this.deleteCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[50] = new ThemeDescription(this.deleteCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText5);
        r10[51] = new ThemeDescription(this.stickersCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[52] = new ThemeDescription(this.stickersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[53] = new ThemeDescription(this.stickersInfoCell3, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[54] = new ThemeDescription(this.stickersInfoCell3, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r10[55] = new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[56] = new ThemeDescription(this.linkHeaderCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r10[57] = new ThemeDescription(null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, null, null, null, Theme.key_dialogTextBlack);
        r10[58] = new ThemeDescription(null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, null, null, null, Theme.key_dialogTextGray2);
        r10[59] = new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOX, new Class[]{DialogRadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_dialogRadioBackground);
        r10[60] = new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{DialogRadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_dialogRadioBackgroundChecked);
        r10[61] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, cellDelegate, Theme.key_avatar_text);
        r10[62] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed);
        r10[63] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange);
        r10[64] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet);
        r10[65] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen);
        r10[66] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan);
        r10[67] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue);
        r10[68] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink);
        return r10;
    }

    final /* synthetic */ void lambda$getThemeDescriptions$22$ChannelEditActivity() {
        if (this.avatarImage != null) {
            this.avatarDrawable.setInfo(5, null, null, false);
            this.avatarImage.invalidate();
        }
    }
}
