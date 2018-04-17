package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.util.ArrayList;
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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_channels_checkUsername;
import org.telegram.tgnet.TLRPC.TL_channels_exportInvite;
import org.telegram.tgnet.TLRPC.TL_channels_getAdminedPublicChannels;
import org.telegram.tgnet.TLRPC.TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC.TL_chatInviteExported;
import org.telegram.tgnet.TLRPC.TL_chatPhoto;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_chats;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.AdminedChannelCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ChannelEditInfoActivity extends BaseFragment implements NotificationCenterDelegate, AvatarUpdaterDelegate {
    private static final int done_button = 1;
    private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList();
    private ShadowSectionCell adminedInfoCell;
    private LinearLayout adminnedChannelsLayout;
    private FileLocation avatar;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImage;
    private AvatarUpdater avatarUpdater = new AvatarUpdater();
    private boolean canCreatePublic = true;
    private int chatId;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextView checkTextView;
    private FrameLayout container1;
    private FrameLayout container2;
    private FrameLayout container3;
    private FrameLayout container4;
    private boolean createAfterUpload;
    private Chat currentChat;
    private EditTextBoldCursor descriptionTextView;
    private View doneButton;
    private boolean donePressed;
    private EditText editText;
    private HeaderCell headerCell;
    private HeaderCell headerCell2;
    private boolean historyHidden;
    private ChatFull info;
    private TextInfoPrivacyCell infoCell;
    private TextInfoPrivacyCell infoCell2;
    private TextInfoPrivacyCell infoCell3;
    private ExportedChatInvite invite;
    private boolean isPrivate;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private View lineView;
    private View lineView2;
    private View lineView3;
    private LinearLayout linearLayout;
    private LinearLayout linearLayout2;
    private LinearLayout linearLayout3;
    private LinearLayout linearLayoutInviteContainer;
    private LinearLayout linearLayoutTypeContainer;
    private LinearLayout linkContainer;
    private LoadingCell loadingAdminedCell;
    private boolean loadingAdminedChannels;
    private boolean loadingInvite;
    private EditTextBoldCursor nameTextView;
    private TextBlockCell privateContainer;
    private AlertDialog progressDialog;
    private LinearLayout publicContainer;
    private RadioButtonCell radioButtonCell1;
    private RadioButtonCell radioButtonCell2;
    private RadioButtonCell radioButtonCell3;
    private RadioButtonCell radioButtonCell4;
    private ShadowSectionCell sectionCell;
    private ShadowSectionCell sectionCell2;
    private ShadowSectionCell sectionCell3;
    private boolean signMessages;
    private TextSettingsCell textCell;
    private TextSettingsCell textCell2;
    private TextCheckCell textCheckCell;
    private TextInfoPrivacyCell typeInfoCell;
    private InputFile uploadedAvatar;
    private EditTextBoldCursor usernameTextView;

    /* renamed from: org.telegram.ui.ChannelEditInfoActivity$4 */
    class C09854 implements OnClickListener {

        /* renamed from: org.telegram.ui.ChannelEditInfoActivity$4$1 */
        class C09841 implements DialogInterface.OnClickListener {
            C09841() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    ChannelEditInfoActivity.this.avatarUpdater.openCamera();
                } else if (i == 1) {
                    ChannelEditInfoActivity.this.avatarUpdater.openGallery();
                } else if (i == 2) {
                    ChannelEditInfoActivity.this.avatar = null;
                    ChannelEditInfoActivity.this.uploadedAvatar = null;
                    ChannelEditInfoActivity.this.avatarImage.setImage(ChannelEditInfoActivity.this.avatar, "50_50", ChannelEditInfoActivity.this.avatarDrawable);
                }
            }
        }

        C09854() {
        }

        public void onClick(View view) {
            if (ChannelEditInfoActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(ChannelEditInfoActivity.this.getParentActivity());
                builder.setItems(ChannelEditInfoActivity.this.avatar != null ? new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley), LocaleController.getString("DeletePhoto", R.string.DeletePhoto)} : new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley)}, new C09841());
                ChannelEditInfoActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditInfoActivity$5 */
    class C09865 implements TextWatcher {
        C09865() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            ChannelEditInfoActivity.this.avatarDrawable.setInfo(5, ChannelEditInfoActivity.this.nameTextView.length() > 0 ? ChannelEditInfoActivity.this.nameTextView.getText().toString() : null, null, false);
            ChannelEditInfoActivity.this.avatarImage.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditInfoActivity$6 */
    class C09876 implements OnEditorActionListener {
        C09876() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || ChannelEditInfoActivity.this.doneButton == null) {
                return false;
            }
            ChannelEditInfoActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditInfoActivity$7 */
    class C09887 implements TextWatcher {
        C09887() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void afterTextChanged(Editable editable) {
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditInfoActivity$8 */
    class C09898 implements OnClickListener {
        C09898() {
        }

        public void onClick(View v) {
            if (ChannelEditInfoActivity.this.isPrivate) {
                ChannelEditInfoActivity.this.isPrivate = false;
                ChannelEditInfoActivity.this.updatePrivatePublic();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditInfoActivity$9 */
    class C09909 implements OnClickListener {
        C09909() {
        }

        public void onClick(View v) {
            if (!ChannelEditInfoActivity.this.isPrivate) {
                ChannelEditInfoActivity.this.isPrivate = true;
                ChannelEditInfoActivity.this.updatePrivatePublic();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditInfoActivity$2 */
    class C19822 implements RequestDelegate {
        C19822() {
        }

        public void run(TLObject response, final TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    boolean z;
                    ChannelEditInfoActivity channelEditInfoActivity = ChannelEditInfoActivity.this;
                    if (error != null) {
                        if (error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                            z = false;
                            channelEditInfoActivity.canCreatePublic = z;
                            if (!ChannelEditInfoActivity.this.canCreatePublic) {
                                ChannelEditInfoActivity.this.loadAdminedChannels();
                            }
                        }
                    }
                    z = true;
                    channelEditInfoActivity.canCreatePublic = z;
                    if (!ChannelEditInfoActivity.this.canCreatePublic) {
                        ChannelEditInfoActivity.this.loadAdminedChannels();
                    }
                }
            });
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditInfoActivity$3 */
    class C19833 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.ChannelEditInfoActivity$3$1 */
        class C09831 implements DialogInterface.OnClickListener {
            C09831() {
            }

            public void onClick(DialogInterface dialog, int which) {
                ChannelEditInfoActivity.this.createAfterUpload = false;
                ChannelEditInfoActivity.this.progressDialog = null;
                ChannelEditInfoActivity.this.donePressed = false;
                try {
                    dialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }

        C19833() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChannelEditInfoActivity.this.finishFragment();
            } else if (id == 1 && !ChannelEditInfoActivity.this.donePressed) {
                Vibrator v;
                if (ChannelEditInfoActivity.this.nameTextView.length() == 0) {
                    v = (Vibrator) ChannelEditInfoActivity.this.getParentActivity().getSystemService("vibrator");
                    if (v != null) {
                        v.vibrate(200);
                    }
                    AndroidUtilities.shakeView(ChannelEditInfoActivity.this.nameTextView, 2.0f, 0);
                } else if (ChannelEditInfoActivity.this.usernameTextView == null || ChannelEditInfoActivity.this.isPrivate || (((ChannelEditInfoActivity.this.currentChat.username != null || ChannelEditInfoActivity.this.usernameTextView.length() == 0) && (ChannelEditInfoActivity.this.currentChat.username == null || ChannelEditInfoActivity.this.currentChat.username.equalsIgnoreCase(ChannelEditInfoActivity.this.usernameTextView.getText().toString()))) || ChannelEditInfoActivity.this.nameTextView.length() == 0 || ChannelEditInfoActivity.this.lastNameAvailable)) {
                    ChannelEditInfoActivity.this.donePressed = true;
                    if (ChannelEditInfoActivity.this.avatarUpdater.uploadingAvatar != null) {
                        ChannelEditInfoActivity.this.createAfterUpload = true;
                        ChannelEditInfoActivity.this.progressDialog = new AlertDialog(ChannelEditInfoActivity.this.getParentActivity(), 1);
                        ChannelEditInfoActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                        ChannelEditInfoActivity.this.progressDialog.setCanceledOnTouchOutside(false);
                        ChannelEditInfoActivity.this.progressDialog.setCancelable(false);
                        ChannelEditInfoActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new C09831());
                        ChannelEditInfoActivity.this.progressDialog.show();
                        return;
                    }
                    if (ChannelEditInfoActivity.this.usernameTextView != null) {
                        String oldUserName = ChannelEditInfoActivity.this.currentChat.username != null ? ChannelEditInfoActivity.this.currentChat.username : TtmlNode.ANONYMOUS_REGION_ID;
                        String newUserName = ChannelEditInfoActivity.this.isPrivate ? TtmlNode.ANONYMOUS_REGION_ID : ChannelEditInfoActivity.this.usernameTextView.getText().toString();
                        if (!oldUserName.equals(newUserName)) {
                            MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).updateChannelUserName(ChannelEditInfoActivity.this.chatId, newUserName);
                        }
                    }
                    if (!ChannelEditInfoActivity.this.currentChat.title.equals(ChannelEditInfoActivity.this.nameTextView.getText().toString())) {
                        MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).changeChatTitle(ChannelEditInfoActivity.this.chatId, ChannelEditInfoActivity.this.nameTextView.getText().toString());
                    }
                    if (!(ChannelEditInfoActivity.this.info == null || ChannelEditInfoActivity.this.info.about.equals(ChannelEditInfoActivity.this.descriptionTextView.getText().toString()))) {
                        MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).updateChannelAbout(ChannelEditInfoActivity.this.chatId, ChannelEditInfoActivity.this.descriptionTextView.getText().toString(), ChannelEditInfoActivity.this.info);
                    }
                    if (!(ChannelEditInfoActivity.this.headerCell2 == null || ChannelEditInfoActivity.this.headerCell2.getVisibility() != 0 || ChannelEditInfoActivity.this.info == null || !ChannelEditInfoActivity.this.currentChat.creator || ChannelEditInfoActivity.this.info.hidden_prehistory == ChannelEditInfoActivity.this.historyHidden)) {
                        ChannelEditInfoActivity.this.info.hidden_prehistory = ChannelEditInfoActivity.this.historyHidden;
                        MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).toogleChannelInvitesHistory(ChannelEditInfoActivity.this.chatId, ChannelEditInfoActivity.this.historyHidden);
                    }
                    if (ChannelEditInfoActivity.this.signMessages != ChannelEditInfoActivity.this.currentChat.signatures) {
                        ChannelEditInfoActivity.this.currentChat.signatures = true;
                        MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).toogleChannelSignatures(ChannelEditInfoActivity.this.chatId, ChannelEditInfoActivity.this.signMessages);
                    }
                    if (ChannelEditInfoActivity.this.uploadedAvatar != null) {
                        MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).changeChatAvatar(ChannelEditInfoActivity.this.chatId, ChannelEditInfoActivity.this.uploadedAvatar);
                    } else if (ChannelEditInfoActivity.this.avatar == null && (ChannelEditInfoActivity.this.currentChat.photo instanceof TL_chatPhoto)) {
                        MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).changeChatAvatar(ChannelEditInfoActivity.this.chatId, null);
                    }
                    ChannelEditInfoActivity.this.finishFragment();
                } else {
                    v = (Vibrator) ChannelEditInfoActivity.this.getParentActivity().getSystemService("vibrator");
                    if (v != null) {
                        v.vibrate(200);
                    }
                    AndroidUtilities.shakeView(ChannelEditInfoActivity.this.checkTextView, 2.0f, 0);
                }
            }
        }
    }

    public ChannelEditInfoActivity(Bundle args) {
        super(args);
        this.chatId = args.getInt("chat_id", 0);
    }

    public boolean onFragmentCreate() {
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        boolean z = true;
        if (this.currentChat == null) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    ChannelEditInfoActivity.this.currentChat = MessagesStorage.getInstance(ChannelEditInfoActivity.this.currentAccount).getChat(ChannelEditInfoActivity.this.chatId);
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await();
            } catch (Throwable e) {
                FileLog.m3e(e);
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
                    FileLog.m3e(e2);
                }
                if (this.info == null) {
                    return false;
                }
            }
        }
        if (this.currentChat.username != null) {
            if (this.currentChat.username.length() != 0) {
                z = false;
            }
        }
        this.isPrivate = z;
        if (this.isPrivate && this.currentChat.creator) {
            TL_channels_checkUsername req = new TL_channels_checkUsername();
            req.username = "1";
            req.channel = new TL_inputChannelEmpty();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new C19822());
        }
        this.avatarUpdater.parentFragment = this;
        this.avatarUpdater.delegate = this;
        this.signMessages = this.currentChat.signatures;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoaded);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.avatarUpdater != null) {
            this.avatarUpdater.clear();
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        if (this.textCell2 != null && this.info != null) {
            if (this.info.stickerset != null) {
                this.textCell2.setTextAndValue(LocaleController.getString("GroupStickers", R.string.GroupStickers), this.info.stickerset.title, false);
            } else {
                this.textCell2.setText(LocaleController.getString("GroupStickers", R.string.GroupStickers), false);
            }
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C19833());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new ScrollView(context2);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        ScrollView scrollView = this.fragmentView;
        scrollView.setFillViewport(true);
        this.linearLayout = new LinearLayout(context2);
        scrollView.addView(this.linearLayout, new LayoutParams(-1, -2));
        this.linearLayout.setOrientation(1);
        this.actionBar.setTitle(LocaleController.getString("ChannelEdit", R.string.ChannelEdit));
        this.linearLayout2 = new LinearLayout(context2);
        this.linearLayout2.setOrientation(1);
        this.linearLayout2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout.addView(this.linearLayout2, LayoutHelper.createLinear(-1, -2));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
        this.avatarImage = new BackupImageView(context2);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.avatarDrawable.setInfo(5, null, null, false);
        this.avatarDrawable.setDrawPhoto(true);
        float f = 16.0f;
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, 48 | (LocaleController.isRTL ? 5 : 3), LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
        r0.avatarImage.setOnClickListener(new C09854());
        r0.nameTextView = new EditTextBoldCursor(context2);
        if (r0.currentChat.megagroup) {
            r0.nameTextView.setHint(LocaleController.getString("GroupName", R.string.GroupName));
        } else {
            r0.nameTextView.setHint(LocaleController.getString("EnterChannelName", R.string.EnterChannelName));
        }
        r0.nameTextView.setMaxLines(4);
        r0.nameTextView.setGravity(16 | (LocaleController.isRTL ? 5 : 3));
        r0.nameTextView.setTextSize(1, f);
        r0.nameTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        r0.nameTextView.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        r0.nameTextView.setImeOptions(268435456);
        r0.nameTextView.setInputType(16385);
        r0.nameTextView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        r0.nameTextView.setEnabled(ChatObject.canChangeChatInfo(r0.currentChat));
        r0.nameTextView.setFocusable(r0.nameTextView.isEnabled());
        r0.nameTextView.setFilters(new InputFilter[]{new LengthFilter(100)});
        r0.nameTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.nameTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        r0.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.nameTextView.setCursorWidth(1.5f);
        frameLayout.addView(r0.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 16, LocaleController.isRTL ? f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : f, 0.0f));
        r0.nameTextView.addTextChangedListener(new C09865());
        r0.lineView = new View(context2);
        r0.lineView.setBackgroundColor(Theme.getColor(Theme.key_divider));
        r0.linearLayout.addView(r0.lineView, new LinearLayout.LayoutParams(-1, 1));
        r0.linearLayout3 = new LinearLayout(context2);
        r0.linearLayout3.setOrientation(1);
        r0.linearLayout3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        r0.linearLayout.addView(r0.linearLayout3, LayoutHelper.createLinear(-1, -2));
        r0.descriptionTextView = new EditTextBoldCursor(context2);
        r0.descriptionTextView.setTextSize(1, f);
        r0.descriptionTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        r0.descriptionTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
        r0.descriptionTextView.setBackgroundDrawable(null);
        r0.descriptionTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        r0.descriptionTextView.setInputType(180225);
        r0.descriptionTextView.setImeOptions(6);
        r0.descriptionTextView.setEnabled(ChatObject.canChangeChatInfo(r0.currentChat));
        r0.descriptionTextView.setFocusable(r0.descriptionTextView.isEnabled());
        r0.descriptionTextView.setFilters(new InputFilter[]{new LengthFilter(255)});
        r0.descriptionTextView.setHint(LocaleController.getString("DescriptionOptionalPlaceholder", R.string.DescriptionOptionalPlaceholder));
        r0.descriptionTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        r0.descriptionTextView.setCursorWidth(1.5f);
        r0.linearLayout3.addView(r0.descriptionTextView, LayoutHelper.createLinear(-1, -2, 17.0f, 12.0f, 17.0f, 6.0f));
        r0.descriptionTextView.setOnEditorActionListener(new C09876());
        r0.descriptionTextView.addTextChangedListener(new C09887());
        r0.sectionCell = new ShadowSectionCell(context2);
        r0.linearLayout.addView(r0.sectionCell, LayoutHelper.createLinear(-1, -2));
        r0.container1 = new FrameLayout(context2);
        r0.container1.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        r0.linearLayout.addView(r0.container1, LayoutHelper.createLinear(-1, -2));
        if (r0.currentChat.creator && (r0.info == null || r0.info.can_set_username)) {
            r0.linearLayoutTypeContainer = new LinearLayout(context2);
            r0.linearLayoutTypeContainer.setOrientation(1);
            r0.linearLayoutTypeContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            r0.linearLayout.addView(r0.linearLayoutTypeContainer, LayoutHelper.createLinear(-1, -2));
            r0.radioButtonCell1 = new RadioButtonCell(context2);
            r0.radioButtonCell1.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (r0.currentChat.megagroup) {
                r0.radioButtonCell1.setTextAndValue(LocaleController.getString("MegaPublic", R.string.MegaPublic), LocaleController.getString("MegaPublicInfo", R.string.MegaPublicInfo), r0.isPrivate ^ true);
            } else {
                r0.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", R.string.ChannelPublic), LocaleController.getString("ChannelPublicInfo", R.string.ChannelPublicInfo), r0.isPrivate ^ true);
            }
            r0.linearLayoutTypeContainer.addView(r0.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
            r0.radioButtonCell1.setOnClickListener(new C09898());
            r0.radioButtonCell2 = new RadioButtonCell(context2);
            r0.radioButtonCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (r0.currentChat.megagroup) {
                r0.radioButtonCell2.setTextAndValue(LocaleController.getString("MegaPrivate", R.string.MegaPrivate), LocaleController.getString("MegaPrivateInfo", R.string.MegaPrivateInfo), r0.isPrivate);
            } else {
                r0.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate), LocaleController.getString("ChannelPrivateInfo", R.string.ChannelPrivateInfo), r0.isPrivate);
            }
            r0.linearLayoutTypeContainer.addView(r0.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
            r0.radioButtonCell2.setOnClickListener(new C09909());
            r0.sectionCell2 = new ShadowSectionCell(context2);
            r0.linearLayout.addView(r0.sectionCell2, LayoutHelper.createLinear(-1, -2));
            r0.linkContainer = new LinearLayout(context2);
            r0.linkContainer.setOrientation(1);
            r0.linkContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            r0.linearLayout.addView(r0.linkContainer, LayoutHelper.createLinear(-1, -2));
            r0.headerCell = new HeaderCell(context2);
            r0.linkContainer.addView(r0.headerCell);
            r0.publicContainer = new LinearLayout(context2);
            r0.publicContainer.setOrientation(0);
            r0.linkContainer.addView(r0.publicContainer, LayoutHelper.createLinear(-1, 36, 17.0f, 7.0f, 17.0f, 0.0f));
            r0.editText = new EditText(context2);
            EditText editText = r0.editText;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(MessagesController.getInstance(r0.currentAccount).linkPrefix);
            stringBuilder.append("/");
            editText.setText(stringBuilder.toString());
            r0.editText.setTextSize(1, 18.0f);
            r0.editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r0.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.editText.setMaxLines(1);
            r0.editText.setLines(1);
            r0.editText.setEnabled(false);
            r0.editText.setBackgroundDrawable(null);
            r0.editText.setPadding(0, 0, 0, 0);
            r0.editText.setSingleLine(true);
            r0.editText.setInputType(163840);
            r0.editText.setImeOptions(6);
            r0.publicContainer.addView(r0.editText, LayoutHelper.createLinear(-2, 36));
            r0.usernameTextView = new EditTextBoldCursor(context2);
            r0.usernameTextView.setTextSize(1, 18.0f);
            if (!r0.isPrivate) {
                r0.usernameTextView.setText(r0.currentChat.username);
            }
            r0.usernameTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r0.usernameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.usernameTextView.setMaxLines(1);
            r0.usernameTextView.setLines(1);
            r0.usernameTextView.setBackgroundDrawable(null);
            r0.usernameTextView.setPadding(0, 0, 0, 0);
            r0.usernameTextView.setSingleLine(true);
            r0.usernameTextView.setInputType(163872);
            r0.usernameTextView.setImeOptions(6);
            r0.usernameTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", R.string.ChannelUsernamePlaceholder));
            r0.usernameTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.usernameTextView.setCursorSize(AndroidUtilities.dp(20.0f));
            r0.usernameTextView.setCursorWidth(1.5f);
            r0.publicContainer.addView(r0.usernameTextView, LayoutHelper.createLinear(-1, 36));
            r0.usernameTextView.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    ChannelEditInfoActivity.this.checkUserName(ChannelEditInfoActivity.this.usernameTextView.getText().toString());
                }

                public void afterTextChanged(Editable editable) {
                }
            });
            r0.privateContainer = new TextBlockCell(context2);
            r0.privateContainer.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            r0.linkContainer.addView(r0.privateContainer);
            r0.privateContainer.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (ChannelEditInfoActivity.this.invite != null) {
                        try {
                            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", ChannelEditInfoActivity.this.invite.link));
                            Toast.makeText(ChannelEditInfoActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", R.string.LinkCopied), 0).show();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }
            });
            r0.checkTextView = new TextView(context2);
            r0.checkTextView.setTextSize(1, 15.0f);
            r0.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            r0.checkTextView.setVisibility(8);
            r0.linkContainer.addView(r0.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 17, 3, 17, 7));
            r0.typeInfoCell = new TextInfoPrivacyCell(context2);
            r0.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            r0.linearLayout.addView(r0.typeInfoCell, LayoutHelper.createLinear(-1, -2));
            r0.loadingAdminedCell = new LoadingCell(context2);
            r0.linearLayout.addView(r0.loadingAdminedCell, LayoutHelper.createLinear(-1, -2));
            r0.adminnedChannelsLayout = new LinearLayout(context2);
            r0.adminnedChannelsLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            r0.adminnedChannelsLayout.setOrientation(1);
            r0.linearLayout.addView(r0.adminnedChannelsLayout, LayoutHelper.createLinear(-1, -2));
            r0.adminedInfoCell = new ShadowSectionCell(context2);
            r0.linearLayout.addView(r0.adminedInfoCell, LayoutHelper.createLinear(-1, -2));
            updatePrivatePublic();
        }
        if (r0.currentChat.creator && r0.currentChat.megagroup) {
            r0.headerCell2 = new HeaderCell(context2);
            r0.headerCell2.setText(LocaleController.getString("ChatHistory", R.string.ChatHistory));
            r0.headerCell2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            r0.linearLayout.addView(r0.headerCell2);
            r0.linearLayoutInviteContainer = new LinearLayout(context2);
            r0.linearLayoutInviteContainer.setOrientation(1);
            r0.linearLayoutInviteContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            r0.linearLayout.addView(r0.linearLayoutInviteContainer, LayoutHelper.createLinear(-1, -2));
            r0.radioButtonCell3 = new RadioButtonCell(context2);
            r0.radioButtonCell3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            r0.radioButtonCell3.setTextAndValue(LocaleController.getString("ChatHistoryVisible", R.string.ChatHistoryVisible), LocaleController.getString("ChatHistoryVisibleInfo", R.string.ChatHistoryVisibleInfo), r0.historyHidden ^ true);
            r0.linearLayoutInviteContainer.addView(r0.radioButtonCell3, LayoutHelper.createLinear(-1, -2));
            r0.radioButtonCell3.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ChannelEditInfoActivity.this.radioButtonCell3.setChecked(true, true);
                    ChannelEditInfoActivity.this.radioButtonCell4.setChecked(false, true);
                    ChannelEditInfoActivity.this.historyHidden = false;
                }
            });
            r0.radioButtonCell4 = new RadioButtonCell(context2);
            r0.radioButtonCell4.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            r0.radioButtonCell4.setTextAndValue(LocaleController.getString("ChatHistoryHidden", R.string.ChatHistoryHidden), LocaleController.getString("ChatHistoryHiddenInfo", R.string.ChatHistoryHiddenInfo), r0.historyHidden);
            r0.linearLayoutInviteContainer.addView(r0.radioButtonCell4, LayoutHelper.createLinear(-1, -2));
            r0.radioButtonCell4.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ChannelEditInfoActivity.this.radioButtonCell3.setChecked(false, true);
                    ChannelEditInfoActivity.this.radioButtonCell4.setChecked(true, true);
                    ChannelEditInfoActivity.this.historyHidden = true;
                }
            });
            r0.sectionCell3 = new ShadowSectionCell(context2);
            r0.linearLayout.addView(r0.sectionCell3, LayoutHelper.createLinear(-1, -2));
            updatePrivatePublic();
        }
        r0.lineView2 = new View(context2);
        r0.lineView2.setBackgroundColor(Theme.getColor(Theme.key_divider));
        r0.linearLayout.addView(r0.lineView2, new LinearLayout.LayoutParams(-1, 1));
        r0.container2 = new FrameLayout(context2);
        r0.container2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        r0.container3 = new FrameLayout(context2);
        r0.container3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        r0.linearLayout.addView(r0.container3, LayoutHelper.createLinear(-1, -2));
        r0.lineView3 = new View(context2);
        r0.lineView3.setBackgroundColor(Theme.getColor(Theme.key_divider));
        r0.linearLayout.addView(r0.lineView3, new LinearLayout.LayoutParams(-1, 1));
        r0.linearLayout.addView(r0.container2, LayoutHelper.createLinear(-1, -2));
        if (!r0.currentChat.megagroup) {
            r0.textCheckCell = new TextCheckCell(context2);
            r0.textCheckCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            r0.textCheckCell.setTextAndCheck(LocaleController.getString("ChannelSignMessages", R.string.ChannelSignMessages), r0.signMessages, false);
            r0.container2.addView(r0.textCheckCell, LayoutHelper.createFrame(-1, -2.0f));
            r0.textCheckCell.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ChannelEditInfoActivity.this.signMessages = ChannelEditInfoActivity.this.signMessages ^ 1;
                    ((TextCheckCell) v).setChecked(ChannelEditInfoActivity.this.signMessages);
                }
            });
            r0.infoCell = new TextInfoPrivacyCell(context2);
            r0.infoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            r0.infoCell.setText(LocaleController.getString("ChannelSignMessagesInfo", R.string.ChannelSignMessagesInfo));
            r0.linearLayout.addView(r0.infoCell, LayoutHelper.createLinear(-1, -2));
        } else if (r0.info != null && r0.info.can_set_stickers) {
            r0.textCell2 = new TextSettingsCell(context2);
            r0.textCell2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.textCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (r0.info.stickerset != null) {
                r0.textCell2.setTextAndValue(LocaleController.getString("GroupStickers", R.string.GroupStickers), r0.info.stickerset.title, false);
            } else {
                r0.textCell2.setText(LocaleController.getString("GroupStickers", R.string.GroupStickers), false);
            }
            r0.container3.addView(r0.textCell2, LayoutHelper.createFrame(-1, -2.0f));
            r0.textCell2.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    GroupStickersActivity groupStickersActivity = new GroupStickersActivity(ChannelEditInfoActivity.this.currentChat.id);
                    groupStickersActivity.setInfo(ChannelEditInfoActivity.this.info);
                    ChannelEditInfoActivity.this.presentFragment(groupStickersActivity);
                }
            });
            r0.infoCell3 = new TextInfoPrivacyCell(context2);
            r0.infoCell3.setText(LocaleController.getString("GroupStickersInfo", R.string.GroupStickersInfo));
            r0.linearLayout.addView(r0.infoCell3, LayoutHelper.createLinear(-1, -2));
        }
        if (r0.currentChat.creator) {
            r0.container3 = new FrameLayout(context2);
            r0.container3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            r0.linearLayout.addView(r0.container3, LayoutHelper.createLinear(-1, -2));
            r0.textCell = new TextSettingsCell(context2);
            r0.textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
            r0.textCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (r0.currentChat.megagroup) {
                r0.textCell.setText(LocaleController.getString("DeleteMega", R.string.DeleteMega), false);
            } else {
                r0.textCell.setText(LocaleController.getString("ChannelDelete", R.string.ChannelDelete), false);
            }
            r0.container3.addView(r0.textCell, LayoutHelper.createFrame(-1, -2.0f));
            r0.textCell.setOnClickListener(new OnClickListener() {

                /* renamed from: org.telegram.ui.ChannelEditInfoActivity$16$1 */
                class C09741 implements DialogInterface.OnClickListener {
                    C09741() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (AndroidUtilities.isTablet()) {
                            NotificationCenter.getInstance(ChannelEditInfoActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(-((long) ChannelEditInfoActivity.this.chatId)));
                        } else {
                            NotificationCenter.getInstance(ChannelEditInfoActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        }
                        MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).deleteUserFromChat(ChannelEditInfoActivity.this.chatId, MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(ChannelEditInfoActivity.this.currentAccount).getClientUserId())), ChannelEditInfoActivity.this.info, true);
                        ChannelEditInfoActivity.this.finishFragment();
                    }
                }

                public void onClick(View v) {
                    Builder builder = new Builder(ChannelEditInfoActivity.this.getParentActivity());
                    if (ChannelEditInfoActivity.this.currentChat.megagroup) {
                        builder.setMessage(LocaleController.getString("MegaDeleteAlert", R.string.MegaDeleteAlert));
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelDeleteAlert", R.string.ChannelDeleteAlert));
                    }
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C09741());
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    ChannelEditInfoActivity.this.showDialog(builder.create());
                }
            });
            r0.infoCell2 = new TextInfoPrivacyCell(context2);
            r0.infoCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            if (r0.currentChat.megagroup) {
                r0.infoCell2.setText(LocaleController.getString("MegaDeleteInfo", R.string.MegaDeleteInfo));
            } else {
                r0.infoCell2.setText(LocaleController.getString("ChannelDeleteInfo", R.string.ChannelDeleteInfo));
            }
            r0.linearLayout.addView(r0.infoCell2, LayoutHelper.createLinear(-1, -2));
        } else {
            if (!r0.currentChat.megagroup) {
                r0.infoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            } else if (r0.infoCell3 == null) {
                r0.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            }
            r0.lineView3.setVisibility(8);
            r0.lineView2.setVisibility(8);
        }
        if (r0.infoCell3 != null) {
            if (r0.infoCell2 == null) {
                r0.infoCell3.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            } else {
                r0.infoCell3.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            }
        }
        r0.nameTextView.setText(r0.currentChat.title);
        r0.nameTextView.setSelection(r0.nameTextView.length());
        if (r0.info != null) {
            r0.descriptionTextView.setText(r0.info.about);
        }
        if (r0.currentChat.photo != null) {
            r0.avatar = r0.currentChat.photo.photo_small;
            r0.avatarImage.setImage(r0.avatar, "50_50", r0.avatarDrawable);
        } else {
            r0.avatarImage.setImageDrawable(r0.avatarDrawable);
        }
        return r0.fragmentView;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = args[0];
            if (chatFull.id == this.chatId) {
                if (this.info == null) {
                    this.descriptionTextView.setText(chatFull.about);
                    this.historyHidden = chatFull.hidden_prehistory;
                    if (this.radioButtonCell3 != null) {
                        this.radioButtonCell3.setChecked(this.historyHidden ^ 1, false);
                        this.radioButtonCell4.setChecked(this.historyHidden, false);
                    }
                }
                this.info = chatFull;
                this.invite = chatFull.exported_invite;
                updatePrivatePublic();
            }
        }
    }

    public void didUploadedPhoto(final InputFile file, final PhotoSize small, PhotoSize big) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                ChannelEditInfoActivity.this.uploadedAvatar = file;
                ChannelEditInfoActivity.this.avatar = small.location;
                ChannelEditInfoActivity.this.avatarImage.setImage(ChannelEditInfoActivity.this.avatar, "50_50", ChannelEditInfoActivity.this.avatarDrawable);
                if (ChannelEditInfoActivity.this.createAfterUpload) {
                    try {
                        if (ChannelEditInfoActivity.this.progressDialog != null && ChannelEditInfoActivity.this.progressDialog.isShowing()) {
                            ChannelEditInfoActivity.this.progressDialog.dismiss();
                            ChannelEditInfoActivity.this.progressDialog = null;
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    ChannelEditInfoActivity.this.donePressed = false;
                    ChannelEditInfoActivity.this.doneButton.performClick();
                }
            }
        });
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.avatarUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        if (!(this.avatarUpdater == null || this.avatarUpdater.currentPicturePath == null)) {
            args.putString("path", this.avatarUpdater.currentPicturePath);
        }
        if (this.nameTextView != null) {
            String text = this.nameTextView.getText().toString();
            if (text != null && text.length() != 0) {
                args.putString("nameTextView", text);
            }
        }
    }

    public void restoreSelfArgs(Bundle args) {
        if (this.avatarUpdater != null) {
            this.avatarUpdater.currentPicturePath = args.getString("path");
        }
    }

    public void setInfo(ChatFull chatFull) {
        if (this.info == null && chatFull != null) {
            this.historyHidden = chatFull.hidden_prehistory;
        }
        this.info = chatFull;
        if (chatFull == null) {
            return;
        }
        if (chatFull.exported_invite instanceof TL_chatInviteExported) {
            this.invite = chatFull.exported_invite;
        } else {
            generateLink();
        }
    }

    private void loadAdminedChannels() {
        if (!this.loadingAdminedChannels) {
            if (this.adminnedChannelsLayout != null) {
                this.loadingAdminedChannels = true;
                updatePrivatePublic();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_channels_getAdminedPublicChannels(), new RequestDelegate() {
                    public void run(final TLObject response, TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {

                            /* renamed from: org.telegram.ui.ChannelEditInfoActivity$18$1$1 */
                            class C09771 implements OnClickListener {
                                C09771() {
                                }

                                public void onClick(View view) {
                                    final Chat channel = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
                                    Builder builder = new Builder(ChannelEditInfoActivity.this.getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    Object[] objArr;
                                    StringBuilder stringBuilder;
                                    if (channel.megagroup) {
                                        objArr = new Object[2];
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).linkPrefix);
                                        stringBuilder.append("/");
                                        stringBuilder.append(channel.username);
                                        objArr[0] = stringBuilder.toString();
                                        objArr[1] = channel.title;
                                        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", R.string.RevokeLinkAlert, objArr)));
                                    } else {
                                        objArr = new Object[2];
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).linkPrefix);
                                        stringBuilder.append("/");
                                        stringBuilder.append(channel.username);
                                        objArr[0] = stringBuilder.toString();
                                        objArr[1] = channel.title;
                                        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", R.string.RevokeLinkAlertChannel, objArr)));
                                    }
                                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                    builder.setPositiveButton(LocaleController.getString("RevokeButton", R.string.RevokeButton), new DialogInterface.OnClickListener() {

                                        /* renamed from: org.telegram.ui.ChannelEditInfoActivity$18$1$1$1$1 */
                                        class C19801 implements RequestDelegate {

                                            /* renamed from: org.telegram.ui.ChannelEditInfoActivity$18$1$1$1$1$1 */
                                            class C09751 implements Runnable {
                                                C09751() {
                                                }

                                                public void run() {
                                                    ChannelEditInfoActivity.this.canCreatePublic = true;
                                                    if (ChannelEditInfoActivity.this.nameTextView.length() > 0) {
                                                        ChannelEditInfoActivity.this.checkUserName(ChannelEditInfoActivity.this.nameTextView.getText().toString());
                                                    }
                                                    ChannelEditInfoActivity.this.updatePrivatePublic();
                                                }
                                            }

                                            C19801() {
                                            }

                                            public void run(TLObject response, TL_error error) {
                                                if (response instanceof TL_boolTrue) {
                                                    AndroidUtilities.runOnUIThread(new C09751());
                                                }
                                            }
                                        }

                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            TL_channels_updateUsername req = new TL_channels_updateUsername();
                                            req.channel = MessagesController.getInputChannel(channel);
                                            req.username = TtmlNode.ANONYMOUS_REGION_ID;
                                            ConnectionsManager.getInstance(ChannelEditInfoActivity.this.currentAccount).sendRequest(req, new C19801(), 64);
                                        }
                                    });
                                    ChannelEditInfoActivity.this.showDialog(builder.create());
                                }
                            }

                            public void run() {
                                ChannelEditInfoActivity.this.loadingAdminedChannels = false;
                                if (response != null && ChannelEditInfoActivity.this.getParentActivity() != null) {
                                    for (int a = 0; a < ChannelEditInfoActivity.this.adminedChannelCells.size(); a++) {
                                        ChannelEditInfoActivity.this.linearLayout.removeView((View) ChannelEditInfoActivity.this.adminedChannelCells.get(a));
                                    }
                                    ChannelEditInfoActivity.this.adminedChannelCells.clear();
                                    TL_messages_chats res = response;
                                    for (int a2 = 0; a2 < res.chats.size(); a2++) {
                                        AdminedChannelCell adminedChannelCell = new AdminedChannelCell(ChannelEditInfoActivity.this.getParentActivity(), new C09771());
                                        Chat chat = (Chat) res.chats.get(a2);
                                        boolean z = true;
                                        if (a2 != res.chats.size() - 1) {
                                            z = false;
                                        }
                                        adminedChannelCell.setChannel(chat, z);
                                        ChannelEditInfoActivity.this.adminedChannelCells.add(adminedChannelCell);
                                        ChannelEditInfoActivity.this.adminnedChannelsLayout.addView(adminedChannelCell, LayoutHelper.createLinear(-1, 72));
                                    }
                                    ChannelEditInfoActivity.this.updatePrivatePublic();
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    private void updatePrivatePublic() {
        if (this.sectionCell2 != null) {
            int i = 0;
            if (this.isPrivate || this.canCreatePublic) {
                this.typeInfoCell.setTag(Theme.key_windowBackgroundWhiteGrayText4);
                this.typeInfoCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
                this.sectionCell2.setVisibility(0);
                this.adminedInfoCell.setVisibility(8);
                this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                this.adminnedChannelsLayout.setVisibility(8);
                this.linkContainer.setVisibility(0);
                this.loadingAdminedCell.setVisibility(8);
                TextInfoPrivacyCell textInfoPrivacyCell;
                String str;
                int i2;
                if (this.currentChat.megagroup) {
                    textInfoPrivacyCell = this.typeInfoCell;
                    if (this.isPrivate) {
                        str = "MegaPrivateLinkHelp";
                        i2 = R.string.MegaPrivateLinkHelp;
                    } else {
                        str = "MegaUsernameHelp";
                        i2 = R.string.MegaUsernameHelp;
                    }
                    textInfoPrivacyCell.setText(LocaleController.getString(str, i2));
                    this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", R.string.ChannelInviteLinkTitle) : LocaleController.getString("ChannelLinkTitle", R.string.ChannelLinkTitle));
                } else {
                    textInfoPrivacyCell = this.typeInfoCell;
                    if (this.isPrivate) {
                        str = "ChannelPrivateLinkHelp";
                        i2 = R.string.ChannelPrivateLinkHelp;
                    } else {
                        str = "ChannelUsernameHelp";
                        i2 = R.string.ChannelUsernameHelp;
                    }
                    textInfoPrivacyCell.setText(LocaleController.getString(str, i2));
                    this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", R.string.ChannelInviteLinkTitle) : LocaleController.getString("ChannelLinkTitle", R.string.ChannelLinkTitle));
                }
                this.publicContainer.setVisibility(this.isPrivate ? 8 : 0);
                this.privateContainer.setVisibility(this.isPrivate ? 0 : 8);
                this.linkContainer.setPadding(0, 0, 0, this.isPrivate ? 0 : AndroidUtilities.dp(7.0f));
                this.privateContainer.setText(this.invite != null ? this.invite.link : LocaleController.getString("Loading", R.string.Loading), false);
                TextView textView = this.checkTextView;
                int i3 = (this.isPrivate || this.checkTextView.length() == 0) ? 8 : 0;
                textView.setVisibility(i3);
                if (this.headerCell2 != null) {
                    this.headerCell2.setVisibility(this.isPrivate ? 0 : 8);
                    this.linearLayoutInviteContainer.setVisibility(this.isPrivate ? 0 : 8);
                    ShadowSectionCell shadowSectionCell = this.sectionCell3;
                    if (!this.isPrivate) {
                        i = 8;
                    }
                    shadowSectionCell.setVisibility(i);
                }
            } else {
                this.typeInfoCell.setText(LocaleController.getString("ChangePublicLimitReached", R.string.ChangePublicLimitReached));
                this.typeInfoCell.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.typeInfoCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                this.linkContainer.setVisibility(8);
                this.sectionCell2.setVisibility(8);
                this.adminedInfoCell.setVisibility(0);
                if (this.loadingAdminedChannels) {
                    this.loadingAdminedCell.setVisibility(0);
                    this.adminnedChannelsLayout.setVisibility(8);
                    this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    this.adminedInfoCell.setBackgroundDrawable(null);
                } else {
                    this.adminedInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.adminedInfoCell.getContext(), R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    this.loadingAdminedCell.setVisibility(8);
                    this.adminnedChannelsLayout.setVisibility(0);
                }
                if (this.headerCell2 != null) {
                    this.headerCell2.setVisibility(8);
                    this.linearLayoutInviteContainer.setVisibility(8);
                    this.sectionCell3.setVisibility(8);
                }
            }
            this.radioButtonCell1.setChecked(this.isPrivate ^ true, true);
            this.radioButtonCell2.setChecked(this.isPrivate, true);
            this.usernameTextView.clearFocus();
            AndroidUtilities.hideKeyboard(this.nameTextView);
        }
    }

    private boolean checkUserName(final String name) {
        if (name == null || name.length() <= 0) {
            this.checkTextView.setVisibility(8);
        } else {
            this.checkTextView.setVisibility(0);
        }
        if (this.checkRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        if (name != null) {
            if (!name.startsWith("_")) {
                if (!name.endsWith("_")) {
                    int a = 0;
                    while (a < name.length()) {
                        char ch = name.charAt(a);
                        if (a == 0 && ch >= '0' && ch <= '9') {
                            if (this.currentChat.megagroup) {
                                this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumberMega", R.string.LinkInvalidStartNumberMega));
                                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                            } else {
                                this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", R.string.LinkInvalidStartNumber));
                                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                            }
                            return false;
                        } else if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                            this.checkTextView.setText(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
                            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                            return false;
                        } else {
                            a++;
                        }
                    }
                }
            }
            this.checkTextView.setText(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            return false;
        }
        if (name != null) {
            if (name.length() >= 5) {
                if (name.length() > 32) {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", R.string.LinkInvalidLong));
                    this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                    this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                    return false;
                }
                this.checkTextView.setText(LocaleController.getString("LinkChecking", R.string.LinkChecking));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGrayText8);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
                this.lastCheckName = name;
                this.checkRunnable = new Runnable() {

                    /* renamed from: org.telegram.ui.ChannelEditInfoActivity$19$1 */
                    class C19811 implements RequestDelegate {
                        C19811() {
                        }

                        public void run(final TLObject response, final TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    ChannelEditInfoActivity.this.checkReqId = 0;
                                    if (ChannelEditInfoActivity.this.lastCheckName != null && ChannelEditInfoActivity.this.lastCheckName.equals(name)) {
                                        if (error == null && (response instanceof TL_boolTrue)) {
                                            ChannelEditInfoActivity.this.checkTextView.setText(LocaleController.formatString("LinkAvailable", R.string.LinkAvailable, name));
                                            ChannelEditInfoActivity.this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGreenText);
                                            ChannelEditInfoActivity.this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText));
                                            ChannelEditInfoActivity.this.lastNameAvailable = true;
                                            return;
                                        }
                                        if (error == null || !error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                                            ChannelEditInfoActivity.this.checkTextView.setText(LocaleController.getString("LinkInUse", R.string.LinkInUse));
                                        } else {
                                            ChannelEditInfoActivity.this.canCreatePublic = false;
                                            ChannelEditInfoActivity.this.loadAdminedChannels();
                                        }
                                        ChannelEditInfoActivity.this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                                        ChannelEditInfoActivity.this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                                        ChannelEditInfoActivity.this.lastNameAvailable = false;
                                    }
                                }
                            });
                        }
                    }

                    public void run() {
                        TL_channels_checkUsername req = new TL_channels_checkUsername();
                        req.username = name;
                        req.channel = MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).getInputChannel(ChannelEditInfoActivity.this.chatId);
                        ChannelEditInfoActivity.this.checkReqId = ConnectionsManager.getInstance(ChannelEditInfoActivity.this.currentAccount).sendRequest(req, new C19811(), 2);
                    }
                };
                AndroidUtilities.runOnUIThread(this.checkRunnable, 300);
                return true;
            }
        }
        if (this.currentChat.megagroup) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidShortMega", R.string.LinkInvalidShortMega));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
        } else {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", R.string.LinkInvalidShort));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
        }
        return false;
    }

    private void generateLink() {
        if (!this.loadingInvite) {
            if (this.invite == null) {
                this.loadingInvite = true;
                TL_channels_exportInvite req = new TL_channels_exportInvite();
                req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (error == null) {
                                    ChannelEditInfoActivity.this.invite = (ExportedChatInvite) response;
                                }
                                ChannelEditInfoActivity.this.loadingInvite = false;
                                if (ChannelEditInfoActivity.this.privateContainer != null) {
                                    ChannelEditInfoActivity.this.privateContainer.setText(ChannelEditInfoActivity.this.invite != null ? ChannelEditInfoActivity.this.invite.link : LocaleController.getString("Loading", R.string.Loading), false);
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                int a = 0;
                if (ChannelEditInfoActivity.this.avatarImage != null) {
                    ChannelEditInfoActivity.this.avatarDrawable.setInfo(5, ChannelEditInfoActivity.this.nameTextView.length() > 0 ? ChannelEditInfoActivity.this.nameTextView.getText().toString() : null, null, false);
                    ChannelEditInfoActivity.this.avatarImage.invalidate();
                }
                if (ChannelEditInfoActivity.this.adminnedChannelsLayout != null) {
                    int count = ChannelEditInfoActivity.this.adminnedChannelsLayout.getChildCount();
                    while (a < count) {
                        View child = ChannelEditInfoActivity.this.adminnedChannelsLayout.getChildAt(a);
                        if (child instanceof AdminedChannelCell) {
                            ((AdminedChannelCell) child).update();
                        }
                        a++;
                    }
                }
            }
        };
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[93];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[5] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[6] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[7] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[8] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[9] = new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[10] = new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[11] = new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[12] = new ThemeDescription(this.linearLayout3, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[13] = new ThemeDescription(this.container1, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[14] = new ThemeDescription(this.container2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[15] = new ThemeDescription(this.container3, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[16] = new ThemeDescription(this.container4, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[17] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, сellDelegate, Theme.key_avatar_text);
        themeDescriptionArr[18] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[19] = new ThemeDescription(this.lineView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider);
        themeDescriptionArr[20] = new ThemeDescription(this.lineView2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider);
        themeDescriptionArr[21] = new ThemeDescription(this.lineView3, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider);
        themeDescriptionArr[22] = new ThemeDescription(this.sectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[23] = new ThemeDescription(this.sectionCell2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[24] = new ThemeDescription(this.sectionCell3, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[25] = new ThemeDescription(this.textCheckCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[26] = new ThemeDescription(this.textCheckCell, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[27] = new ThemeDescription(this.textCheckCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb);
        themeDescriptionArr[28] = new ThemeDescription(this.textCheckCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        themeDescriptionArr[29] = new ThemeDescription(this.textCheckCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked);
        themeDescriptionArr[30] = new ThemeDescription(this.textCheckCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        themeDescriptionArr[31] = new ThemeDescription(this.infoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[32] = new ThemeDescription(this.infoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[33] = new ThemeDescription(this.textCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[34] = new ThemeDescription(this.textCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText5);
        themeDescriptionArr[35] = new ThemeDescription(this.textCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[36] = new ThemeDescription(this.textCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[37] = new ThemeDescription(this.infoCell2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[38] = new ThemeDescription(this.infoCell2, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[39] = new ThemeDescription(this.infoCell3, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[40] = new ThemeDescription(this.infoCell3, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[41] = new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[42] = new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[43] = new ThemeDescription(this.linearLayoutTypeContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[44] = new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[45] = new ThemeDescription(this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        themeDescriptionArr[46] = new ThemeDescription(this.headerCell2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        themeDescriptionArr[47] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[48] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[49] = new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteRedText4);
        themeDescriptionArr[50] = new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8);
        themeDescriptionArr[51] = new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGreenText);
        themeDescriptionArr[52] = new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[53] = new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[54] = new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText4);
        themeDescriptionArr[55] = new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[56] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[57] = new ThemeDescription(this.privateContainer, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[58] = new ThemeDescription(this.privateContainer, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[59] = new ThemeDescription(this.loadingAdminedCell, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[60] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[61] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        themeDescriptionArr[62] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        themeDescriptionArr[63] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[64] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[65] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[66] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        themeDescriptionArr[67] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        themeDescriptionArr[68] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[69] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[70] = new ThemeDescription(this.linearLayoutInviteContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[71] = new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[72] = new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        themeDescriptionArr[73] = new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        themeDescriptionArr[74] = new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[75] = new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[76] = new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[77] = new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        themeDescriptionArr[78] = new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        themeDescriptionArr[79] = new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[80] = new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[81] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[82] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText);
        themeDescriptionArr[83] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_windowBackgroundWhiteLinkText);
        themeDescriptionArr[84] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"deleteButton"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText);
        themeDescriptionArr[85] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, сellDelegate, Theme.key_avatar_text);
        ThemeDescriptionDelegate themeDescriptionDelegate = сellDelegate;
        themeDescriptionArr[86] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[87] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[88] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[89] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[90] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[91] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[92] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink);
        return themeDescriptionArr;
    }
}
