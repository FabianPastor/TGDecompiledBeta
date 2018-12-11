package org.telegram.p005ui;

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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.CLASSNAMEC;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.p005ui.Cells.AdminedChannelCell;
import org.telegram.p005ui.Cells.HeaderCell;
import org.telegram.p005ui.Cells.LoadingCell;
import org.telegram.p005ui.Cells.RadioButtonCell;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.Cells.TextBlockCell;
import org.telegram.p005ui.Cells.TextCheckCell;
import org.telegram.p005ui.Cells.TextInfoPrivacyCell;
import org.telegram.p005ui.Cells.TextSettingsCell;
import org.telegram.p005ui.Components.AvatarDrawable;
import org.telegram.p005ui.Components.BackupImageView;
import org.telegram.p005ui.Components.EditTextBoldCursor;
import org.telegram.p005ui.Components.ImageUpdater;
import org.telegram.p005ui.Components.ImageUpdater.ImageUpdaterDelegate;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.tgnet.ConnectionsManager;
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
import org.telegram.tgnet.TLRPC.TL_secureFile;

/* renamed from: org.telegram.ui.ChannelEditInfoActivity */
public class ChannelEditInfoActivity extends BaseFragment implements NotificationCenterDelegate, ImageUpdaterDelegate {
    private static final int done_button = 1;
    private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList();
    private ShadowSectionCell adminedInfoCell;
    private LinearLayout adminnedChannelsLayout;
    private FileLocation avatar;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImage;
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
    private ImageUpdater imageUpdater = new ImageUpdater();
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

    /* renamed from: org.telegram.ui.ChannelEditInfoActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
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
                    if (ChannelEditInfoActivity.this.imageUpdater.uploadingImage != null) {
                        ChannelEditInfoActivity.this.createAfterUpload = true;
                        ChannelEditInfoActivity.this.progressDialog = new AlertDialog(ChannelEditInfoActivity.this.getParentActivity(), 1);
                        ChannelEditInfoActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", CLASSNAMER.string.Loading));
                        ChannelEditInfoActivity.this.progressDialog.setCanceledOnTouchOutside(false);
                        ChannelEditInfoActivity.this.progressDialog.setCancelable(false);
                        ChannelEditInfoActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), new ChannelEditInfoActivity$1$$Lambda$0(this));
                        ChannelEditInfoActivity.this.progressDialog.show();
                        return;
                    }
                    if (ChannelEditInfoActivity.this.usernameTextView != null) {
                        String newUserName;
                        String oldUserName = ChannelEditInfoActivity.this.currentChat.username != null ? ChannelEditInfoActivity.this.currentChat.username : TtmlNode.ANONYMOUS_REGION_ID;
                        if (ChannelEditInfoActivity.this.isPrivate) {
                            newUserName = TtmlNode.ANONYMOUS_REGION_ID;
                        } else {
                            newUserName = ChannelEditInfoActivity.this.usernameTextView.getText().toString();
                        }
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
                    if (ChannelEditInfoActivity.this.headerCell2 != null && ChannelEditInfoActivity.this.headerCell2.getVisibility() == 0 && ChannelEditInfoActivity.this.info != null && ((ChannelEditInfoActivity.this.currentChat.creator || (ChannelEditInfoActivity.this.currentChat.admin_rights != null && ChannelEditInfoActivity.this.currentChat.admin_rights.change_info)) && ChannelEditInfoActivity.this.info.hidden_prehistory != ChannelEditInfoActivity.this.historyHidden)) {
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

        final /* synthetic */ void lambda$onItemClick$0$ChannelEditInfoActivity$1(DialogInterface dialog, int which) {
            ChannelEditInfoActivity.this.createAfterUpload = false;
            ChannelEditInfoActivity.this.progressDialog = null;
            ChannelEditInfoActivity.this.donePressed = false;
            try {
                dialog.dismiss();
            } catch (Throwable e) {
                FileLog.m14e(e);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditInfoActivity$2 */
    class CLASSNAME implements TextWatcher {
        CLASSNAME() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            String obj;
            AvatarDrawable access$2500 = ChannelEditInfoActivity.this.avatarDrawable;
            if (ChannelEditInfoActivity.this.nameTextView.length() > 0) {
                obj = ChannelEditInfoActivity.this.nameTextView.getText().toString();
            } else {
                obj = null;
            }
            access$2500.setInfo(5, obj, null, false);
            ChannelEditInfoActivity.this.avatarImage.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditInfoActivity$3 */
    class CLASSNAME implements TextWatcher {
        CLASSNAME() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void afterTextChanged(Editable editable) {
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditInfoActivity$4 */
    class CLASSNAME implements TextWatcher {
        CLASSNAME() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            ChannelEditInfoActivity.this.checkUserName(ChannelEditInfoActivity.this.usernameTextView.getText().toString());
        }

        public void afterTextChanged(Editable editable) {
        }
    }

    public ChannelEditInfoActivity(Bundle args) {
        super(args);
        this.chatId = args.getInt("chat_id", 0);
    }

    public boolean onFragmentCreate() {
        boolean z = false;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        if (this.currentChat == null) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new ChannelEditInfoActivity$$Lambda$0(this, countDownLatch));
            try {
                countDownLatch.await();
            } catch (Throwable e) {
                FileLog.m14e(e);
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
                    FileLog.m14e(e2);
                }
                if (this.info == null) {
                    return false;
                }
            }
        }
        if (this.currentChat.username == null || this.currentChat.username.length() == 0) {
            z = true;
        }
        this.isPrivate = z;
        if (this.isPrivate && this.currentChat.creator) {
            TL_channels_checkUsername req = new TL_channels_checkUsername();
            req.username = "1";
            req.channel = new TL_inputChannelEmpty();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChannelEditInfoActivity$$Lambda$1(this));
        }
        this.imageUpdater.parentFragment = this;
        this.imageUpdater.delegate = this;
        this.signMessages = this.currentChat.signatures;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoaded);
        return super.onFragmentCreate();
    }

    final /* synthetic */ void lambda$onFragmentCreate$0$ChannelEditInfoActivity(CountDownLatch countDownLatch) {
        this.currentChat = MessagesStorage.getInstance(this.currentAccount).getChat(this.chatId);
        countDownLatch.countDown();
    }

    final /* synthetic */ void lambda$onFragmentCreate$2$ChannelEditInfoActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ChannelEditInfoActivity$$Lambda$27(this, error));
    }

    final /* synthetic */ void lambda$null$1$ChannelEditInfoActivity(TL_error error) {
        boolean z = error == null || !error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH");
        this.canCreatePublic = z;
        if (!this.canCreatePublic) {
            loadAdminedChannels();
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.imageUpdater != null) {
            this.imageUpdater.clear();
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        if (this.textCell2 != null && this.info != null) {
            if (this.info.stickerset != null) {
                this.textCell2.setTextAndValue(LocaleController.getString("GroupStickers", CLASSNAMER.string.GroupStickers), this.info.stickerset.title, false);
            } else {
                this.textCell2.setText(LocaleController.getString("GroupStickers", CLASSNAMER.string.GroupStickers), false);
            }
        }
    }

    public View createView(Context context) {
        float f;
        float f2;
        this.actionBar.setBackButtonImage(CLASSNAMER.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, CLASSNAMER.drawable.ic_done, AndroidUtilities.m10dp(56.0f));
        this.fragmentView = new ScrollView(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        ScrollView scrollView = this.fragmentView;
        scrollView.setFillViewport(true);
        this.linearLayout = new LinearLayout(context);
        scrollView.addView(this.linearLayout, new LayoutParams(-1, -2));
        this.linearLayout.setOrientation(1);
        this.actionBar.setTitle(LocaleController.getString("ChannelEdit", CLASSNAMER.string.ChannelEdit));
        this.linearLayout2 = new LinearLayout(context);
        this.linearLayout2.setOrientation(1);
        this.linearLayout2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout.addView(this.linearLayout2, LayoutHelper.createLinear(-1, -2));
        FrameLayout frameLayout = new FrameLayout(context);
        this.linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.m10dp(32.0f));
        this.avatarDrawable.setInfo(5, null, null, false);
        this.avatarDrawable.setDrawPhoto(true);
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
        this.avatarImage.setOnClickListener(new ChannelEditInfoActivity$$Lambda$2(this));
        this.nameTextView = new EditTextBoldCursor(context);
        if (this.currentChat.megagroup) {
            this.nameTextView.setHint(LocaleController.getString("GroupName", CLASSNAMER.string.GroupName));
        } else {
            this.nameTextView.setHint(LocaleController.getString("EnterChannelName", CLASSNAMER.string.EnterChannelName));
        }
        this.nameTextView.setMaxLines(4);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.nameTextView.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        this.nameTextView.setImeOptions(CLASSNAMEC.ENCODING_PCM_MU_LAW);
        this.nameTextView.setInputType(16385);
        this.nameTextView.setPadding(0, 0, 0, AndroidUtilities.m10dp(8.0f));
        this.nameTextView.setEnabled(ChatObject.canChangeChatInfo(this.currentChat));
        this.nameTextView.setFocusable(this.nameTextView.isEnabled());
        this.nameTextView.setFilters(new InputFilter[]{new LengthFilter(100)});
        this.nameTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setCursorSize(AndroidUtilities.m10dp(20.0f));
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setCursorWidth(1.5f);
        view = this.nameTextView;
        f = LocaleController.isRTL ? 16.0f : 96.0f;
        if (LocaleController.isRTL) {
            f2 = 96.0f;
        } else {
            f2 = 16.0f;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(-1, -2.0f, 16, f, 0.0f, f2, 0.0f));
        this.nameTextView.addTextChangedListener(new CLASSNAME());
        this.lineView = new View(context);
        this.lineView.setBackgroundColor(Theme.getColor(Theme.key_divider));
        this.linearLayout.addView(this.lineView, new LinearLayout.LayoutParams(-1, 1));
        this.linearLayout3 = new LinearLayout(context);
        this.linearLayout3.setOrientation(1);
        this.linearLayout3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout.addView(this.linearLayout3, LayoutHelper.createLinear(-1, -2));
        this.descriptionTextView = new EditTextBoldCursor(context);
        this.descriptionTextView.setTextSize(1, 16.0f);
        this.descriptionTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.descriptionTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.m10dp(6.0f));
        this.descriptionTextView.setBackgroundDrawable(null);
        this.descriptionTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.descriptionTextView.setInputType(180225);
        this.descriptionTextView.setImeOptions(6);
        this.descriptionTextView.setEnabled(ChatObject.canChangeChatInfo(this.currentChat));
        this.descriptionTextView.setFocusable(this.descriptionTextView.isEnabled());
        this.descriptionTextView.setFilters(new InputFilter[]{new LengthFilter(255)});
        this.descriptionTextView.setHint(LocaleController.getString("DescriptionOptionalPlaceholder", CLASSNAMER.string.DescriptionOptionalPlaceholder));
        this.descriptionTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.descriptionTextView.setCursorSize(AndroidUtilities.m10dp(20.0f));
        this.descriptionTextView.setCursorWidth(1.5f);
        this.linearLayout3.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 17.0f, 12.0f, 17.0f, 6.0f));
        this.descriptionTextView.setOnEditorActionListener(new ChannelEditInfoActivity$$Lambda$3(this));
        this.descriptionTextView.addTextChangedListener(new CLASSNAME());
        this.sectionCell = new ShadowSectionCell(context);
        this.linearLayout.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        this.container1 = new FrameLayout(context);
        this.container1.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout.addView(this.container1, LayoutHelper.createLinear(-1, -2));
        if (this.currentChat.creator && (this.info == null || this.info.can_set_username)) {
            this.linearLayoutTypeContainer = new LinearLayout(context);
            this.linearLayoutTypeContainer.setOrientation(1);
            this.linearLayoutTypeContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.linearLayout.addView(this.linearLayoutTypeContainer, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell1 = new RadioButtonCell(context);
            this.radioButtonCell1.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (this.currentChat.megagroup) {
                this.radioButtonCell1.setTextAndValue(LocaleController.getString("MegaPublic", CLASSNAMER.string.MegaPublic), LocaleController.getString("MegaPublicInfo", CLASSNAMER.string.MegaPublicInfo), !this.isPrivate);
            } else {
                this.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", CLASSNAMER.string.ChannelPublic), LocaleController.getString("ChannelPublicInfo", CLASSNAMER.string.ChannelPublicInfo), !this.isPrivate);
            }
            this.linearLayoutTypeContainer.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell1.setOnClickListener(new ChannelEditInfoActivity$$Lambda$4(this));
            this.radioButtonCell2 = new RadioButtonCell(context);
            this.radioButtonCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (this.currentChat.megagroup) {
                this.radioButtonCell2.setTextAndValue(LocaleController.getString("MegaPrivate", CLASSNAMER.string.MegaPrivate), LocaleController.getString("MegaPrivateInfo", CLASSNAMER.string.MegaPrivateInfo), this.isPrivate);
            } else {
                this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", CLASSNAMER.string.ChannelPrivate), LocaleController.getString("ChannelPrivateInfo", CLASSNAMER.string.ChannelPrivateInfo), this.isPrivate);
            }
            this.linearLayoutTypeContainer.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell2.setOnClickListener(new ChannelEditInfoActivity$$Lambda$5(this));
            this.sectionCell2 = new ShadowSectionCell(context);
            this.linearLayout.addView(this.sectionCell2, LayoutHelper.createLinear(-1, -2));
            this.linkContainer = new LinearLayout(context);
            this.linkContainer.setOrientation(1);
            this.linkContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
            this.headerCell = new HeaderCell(context);
            this.linkContainer.addView(this.headerCell);
            this.publicContainer = new LinearLayout(context);
            this.publicContainer.setOrientation(0);
            this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 17.0f, 7.0f, 17.0f, 0.0f));
            this.editText = new EditText(context);
            this.editText.setText(MessagesController.getInstance(this.currentAccount).linkPrefix + "/");
            this.editText.setTextSize(1, 18.0f);
            this.editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.editText.setMaxLines(1);
            this.editText.setLines(1);
            this.editText.setEnabled(false);
            this.editText.setBackgroundDrawable(null);
            this.editText.setPadding(0, 0, 0, 0);
            this.editText.setSingleLine(true);
            this.editText.setInputType(163840);
            this.editText.setImeOptions(6);
            this.publicContainer.addView(this.editText, LayoutHelper.createLinear(-2, 36));
            this.usernameTextView = new EditTextBoldCursor(context);
            this.usernameTextView.setTextSize(1, 18.0f);
            if (!this.isPrivate) {
                this.usernameTextView.setText(this.currentChat.username);
            }
            this.usernameTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.usernameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.usernameTextView.setMaxLines(1);
            this.usernameTextView.setLines(1);
            this.usernameTextView.setBackgroundDrawable(null);
            this.usernameTextView.setPadding(0, 0, 0, 0);
            this.usernameTextView.setSingleLine(true);
            this.usernameTextView.setInputType(163872);
            this.usernameTextView.setImeOptions(6);
            this.usernameTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", CLASSNAMER.string.ChannelUsernamePlaceholder));
            this.usernameTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.usernameTextView.setCursorSize(AndroidUtilities.m10dp(20.0f));
            this.usernameTextView.setCursorWidth(1.5f);
            this.publicContainer.addView(this.usernameTextView, LayoutHelper.createLinear(-1, 36));
            this.usernameTextView.addTextChangedListener(new CLASSNAME());
            this.privateContainer = new TextBlockCell(context);
            this.privateContainer.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.linkContainer.addView(this.privateContainer);
            this.privateContainer.setOnClickListener(new ChannelEditInfoActivity$$Lambda$6(this));
            this.checkTextView = new TextView(context);
            this.checkTextView.setTextSize(1, 15.0f);
            this.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.checkTextView.setVisibility(8);
            this.linkContainer.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 17, 3, 17, 7));
            this.typeInfoCell = new TextInfoPrivacyCell(context);
            this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context, CLASSNAMER.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.linearLayout.addView(this.typeInfoCell, LayoutHelper.createLinear(-1, -2));
            this.loadingAdminedCell = new LoadingCell(context);
            this.linearLayout.addView(this.loadingAdminedCell, LayoutHelper.createLinear(-1, -2));
            this.adminnedChannelsLayout = new LinearLayout(context);
            this.adminnedChannelsLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.adminnedChannelsLayout.setOrientation(1);
            this.linearLayout.addView(this.adminnedChannelsLayout, LayoutHelper.createLinear(-1, -2));
            this.adminedInfoCell = new ShadowSectionCell(context);
            this.linearLayout.addView(this.adminedInfoCell, LayoutHelper.createLinear(-1, -2));
            updatePrivatePublic();
        }
        if ((this.currentChat.creator || (this.currentChat.admin_rights != null && this.currentChat.admin_rights.change_info)) && this.currentChat.megagroup) {
            this.headerCell2 = new HeaderCell(context);
            this.headerCell2.setText(LocaleController.getString("ChatHistory", CLASSNAMER.string.ChatHistory));
            this.headerCell2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.linearLayout.addView(this.headerCell2);
            this.linearLayoutInviteContainer = new LinearLayout(context);
            this.linearLayoutInviteContainer.setOrientation(1);
            this.linearLayoutInviteContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.linearLayout.addView(this.linearLayoutInviteContainer, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell3 = new RadioButtonCell(context);
            this.radioButtonCell3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.radioButtonCell3.setTextAndValue(LocaleController.getString("ChatHistoryVisible", CLASSNAMER.string.ChatHistoryVisible), LocaleController.getString("ChatHistoryVisibleInfo", CLASSNAMER.string.ChatHistoryVisibleInfo), !this.historyHidden);
            this.linearLayoutInviteContainer.addView(this.radioButtonCell3, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell3.setOnClickListener(new ChannelEditInfoActivity$$Lambda$7(this));
            this.radioButtonCell4 = new RadioButtonCell(context);
            this.radioButtonCell4.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.radioButtonCell4.setTextAndValue(LocaleController.getString("ChatHistoryHidden", CLASSNAMER.string.ChatHistoryHidden), LocaleController.getString("ChatHistoryHiddenInfo", CLASSNAMER.string.ChatHistoryHiddenInfo), this.historyHidden);
            this.linearLayoutInviteContainer.addView(this.radioButtonCell4, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell4.setOnClickListener(new ChannelEditInfoActivity$$Lambda$8(this));
            this.sectionCell3 = new ShadowSectionCell(context);
            this.linearLayout.addView(this.sectionCell3, LayoutHelper.createLinear(-1, -2));
            updatePrivatePublic();
        }
        this.lineView2 = new View(context);
        this.lineView2.setBackgroundColor(Theme.getColor(Theme.key_divider));
        this.linearLayout.addView(this.lineView2, new LinearLayout.LayoutParams(-1, 1));
        this.container2 = new FrameLayout(context);
        this.container2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.container3 = new FrameLayout(context);
        this.container3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout.addView(this.container3, LayoutHelper.createLinear(-1, -2));
        this.lineView3 = new View(context);
        this.lineView3.setBackgroundColor(Theme.getColor(Theme.key_divider));
        this.linearLayout.addView(this.lineView3, new LinearLayout.LayoutParams(-1, 1));
        this.linearLayout.addView(this.container2, LayoutHelper.createLinear(-1, -2));
        if (!this.currentChat.megagroup) {
            this.textCheckCell = new TextCheckCell(context);
            this.textCheckCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.textCheckCell.setTextAndCheck(LocaleController.getString("ChannelSignMessages", CLASSNAMER.string.ChannelSignMessages), this.signMessages, false);
            this.container2.addView(this.textCheckCell, LayoutHelper.createFrame(-1, -2.0f));
            this.textCheckCell.setOnClickListener(new ChannelEditInfoActivity$$Lambda$9(this));
            this.infoCell = new TextInfoPrivacyCell(context);
            this.infoCell.setBackgroundDrawable(Theme.getThemedDrawable(context, CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            this.infoCell.setText(LocaleController.getString("ChannelSignMessagesInfo", CLASSNAMER.string.ChannelSignMessagesInfo));
            this.linearLayout.addView(this.infoCell, LayoutHelper.createLinear(-1, -2));
        } else if (this.info != null && this.info.can_set_stickers) {
            this.textCell2 = new TextSettingsCell(context);
            this.textCell2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (this.info.stickerset != null) {
                this.textCell2.setTextAndValue(LocaleController.getString("GroupStickers", CLASSNAMER.string.GroupStickers), this.info.stickerset.title, false);
            } else {
                this.textCell2.setText(LocaleController.getString("GroupStickers", CLASSNAMER.string.GroupStickers), false);
            }
            this.container3.addView(this.textCell2, LayoutHelper.createFrame(-1, -2.0f));
            this.textCell2.setOnClickListener(new ChannelEditInfoActivity$$Lambda$10(this));
            this.infoCell3 = new TextInfoPrivacyCell(context);
            this.infoCell3.setText(LocaleController.getString("GroupStickersInfo", CLASSNAMER.string.GroupStickersInfo));
            this.linearLayout.addView(this.infoCell3, LayoutHelper.createLinear(-1, -2));
        }
        if (this.currentChat.creator) {
            this.container3 = new FrameLayout(context);
            this.container3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.linearLayout.addView(this.container3, LayoutHelper.createLinear(-1, -2));
            this.textCell = new TextSettingsCell(context);
            this.textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
            this.textCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (this.currentChat.megagroup) {
                this.textCell.setText(LocaleController.getString("DeleteMega", CLASSNAMER.string.DeleteMega), false);
            } else {
                this.textCell.setText(LocaleController.getString("ChannelDelete", CLASSNAMER.string.ChannelDelete), false);
            }
            this.container3.addView(this.textCell, LayoutHelper.createFrame(-1, -2.0f));
            this.textCell.setOnClickListener(new ChannelEditInfoActivity$$Lambda$11(this));
            this.infoCell2 = new TextInfoPrivacyCell(context);
            this.infoCell2.setBackgroundDrawable(Theme.getThemedDrawable(context, CLASSNAMER.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            if (this.currentChat.megagroup) {
                this.infoCell2.setText(LocaleController.getString("MegaDeleteInfo", CLASSNAMER.string.MegaDeleteInfo));
            } else {
                this.infoCell2.setText(LocaleController.getString("ChannelDeleteInfo", CLASSNAMER.string.ChannelDeleteInfo));
            }
            this.linearLayout.addView(this.infoCell2, LayoutHelper.createLinear(-1, -2));
        } else {
            if (!this.currentChat.megagroup) {
                this.infoCell.setBackgroundDrawable(Theme.getThemedDrawable(context, CLASSNAMER.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            } else if (this.infoCell3 == null) {
                this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, CLASSNAMER.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            }
            this.lineView3.setVisibility(8);
            this.lineView2.setVisibility(8);
        }
        if (this.infoCell3 != null) {
            if (this.infoCell2 == null) {
                this.infoCell3.setBackgroundDrawable(Theme.getThemedDrawable(context, CLASSNAMER.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            } else {
                this.infoCell3.setBackgroundDrawable(Theme.getThemedDrawable(context, CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            }
        }
        this.nameTextView.setText(this.currentChat.title);
        this.nameTextView.setSelection(this.nameTextView.length());
        if (this.info != null) {
            this.descriptionTextView.setText(this.info.about);
        }
        if (this.currentChat.photo != null) {
            this.avatar = this.currentChat.photo.photo_small;
            this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable);
        } else {
            this.avatarImage.setImageDrawable(this.avatarDrawable);
        }
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$4$ChannelEditInfoActivity(View view) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setItems(this.avatar != null ? new CharSequence[]{LocaleController.getString("FromCamera", CLASSNAMER.string.FromCamera), LocaleController.getString("FromGalley", CLASSNAMER.string.FromGalley), LocaleController.getString("DeletePhoto", CLASSNAMER.string.DeletePhoto)} : new CharSequence[]{LocaleController.getString("FromCamera", CLASSNAMER.string.FromCamera), LocaleController.getString("FromGalley", CLASSNAMER.string.FromGalley)}, new ChannelEditInfoActivity$$Lambda$26(this));
            showDialog(builder.create());
        }
    }

    final /* synthetic */ void lambda$null$3$ChannelEditInfoActivity(DialogInterface dialogInterface, int i) {
        if (i == 0) {
            this.imageUpdater.openCamera();
        } else if (i == 1) {
            this.imageUpdater.openGallery();
        } else if (i == 2) {
            this.avatar = null;
            this.uploadedAvatar = null;
            this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable);
        }
    }

    final /* synthetic */ boolean lambda$createView$5$ChannelEditInfoActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6 || this.doneButton == null) {
            return false;
        }
        this.doneButton.performClick();
        return true;
    }

    final /* synthetic */ void lambda$createView$6$ChannelEditInfoActivity(View v) {
        if (this.isPrivate) {
            this.isPrivate = false;
            updatePrivatePublic();
        }
    }

    final /* synthetic */ void lambda$createView$7$ChannelEditInfoActivity(View v) {
        if (!this.isPrivate) {
            this.isPrivate = true;
            updatePrivatePublic();
        }
    }

    final /* synthetic */ void lambda$createView$8$ChannelEditInfoActivity(View v) {
        if (this.invite != null) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.invite.link));
                Toast.makeText(getParentActivity(), LocaleController.getString("LinkCopied", CLASSNAMER.string.LinkCopied), 0).show();
            } catch (Throwable e) {
                FileLog.m14e(e);
            }
        }
    }

    final /* synthetic */ void lambda$createView$9$ChannelEditInfoActivity(View v) {
        this.radioButtonCell3.setChecked(true, true);
        this.radioButtonCell4.setChecked(false, true);
        this.historyHidden = false;
    }

    final /* synthetic */ void lambda$createView$10$ChannelEditInfoActivity(View v) {
        this.radioButtonCell3.setChecked(false, true);
        this.radioButtonCell4.setChecked(true, true);
        this.historyHidden = true;
    }

    final /* synthetic */ void lambda$createView$11$ChannelEditInfoActivity(View v) {
        this.signMessages = !this.signMessages;
        ((TextCheckCell) v).setChecked(this.signMessages);
    }

    final /* synthetic */ void lambda$createView$12$ChannelEditInfoActivity(View v) {
        GroupStickersActivity groupStickersActivity = new GroupStickersActivity(this.currentChat.var_id);
        groupStickersActivity.setInfo(this.info);
        presentFragment(groupStickersActivity);
    }

    final /* synthetic */ void lambda$createView$14$ChannelEditInfoActivity(View v) {
        Builder builder = new Builder(getParentActivity());
        if (this.currentChat.megagroup) {
            builder.setMessage(LocaleController.getString("MegaDeleteAlert", CLASSNAMER.string.MegaDeleteAlert));
        } else {
            builder.setMessage(LocaleController.getString("ChannelDeleteAlert", CLASSNAMER.string.ChannelDeleteAlert));
        }
        builder.setTitle(LocaleController.getString("AppName", CLASSNAMER.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", CLASSNAMER.string.OK), new ChannelEditInfoActivity$$Lambda$25(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), null);
        showDialog(builder.create());
    }

    final /* synthetic */ void lambda$null$13$ChannelEditInfoActivity(DialogInterface dialogInterface, int i) {
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(-((long) this.chatId)));
        } else {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chatId, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId())), this.info, true);
        finishFragment();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = args[0];
            if (chatFull.var_id == this.chatId) {
                if (this.info == null) {
                    this.descriptionTextView.setText(chatFull.about);
                    this.historyHidden = chatFull.hidden_prehistory;
                    if (this.radioButtonCell3 != null) {
                        boolean z;
                        RadioButtonCell radioButtonCell = this.radioButtonCell3;
                        if (this.historyHidden) {
                            z = false;
                        } else {
                            z = true;
                        }
                        radioButtonCell.setChecked(z, false);
                        this.radioButtonCell4.setChecked(this.historyHidden, false);
                    }
                }
                this.info = chatFull;
                this.invite = chatFull.exported_invite;
                updatePrivatePublic();
            }
        }
    }

    public void didUploadedPhoto(InputFile file, PhotoSize small, PhotoSize big, TL_secureFile secureFile) {
        AndroidUtilities.runOnUIThread(new ChannelEditInfoActivity$$Lambda$12(this, file, small));
    }

    final /* synthetic */ void lambda$didUploadedPhoto$15$ChannelEditInfoActivity(InputFile file, PhotoSize small) {
        this.uploadedAvatar = file;
        this.avatar = small.location;
        this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable);
        if (this.createAfterUpload) {
            try {
                if (this.progressDialog != null && this.progressDialog.isShowing()) {
                    this.progressDialog.dismiss();
                    this.progressDialog = null;
                }
            } catch (Throwable e) {
                FileLog.m14e(e);
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
        if (!this.loadingAdminedChannels && this.adminnedChannelsLayout != null) {
            this.loadingAdminedChannels = true;
            updatePrivatePublic();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_channels_getAdminedPublicChannels(), new ChannelEditInfoActivity$$Lambda$13(this));
        }
    }

    final /* synthetic */ void lambda$loadAdminedChannels$21$ChannelEditInfoActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ChannelEditInfoActivity$$Lambda$20(this, response));
    }

    final /* synthetic */ void lambda$null$20$ChannelEditInfoActivity(TLObject response) {
        this.loadingAdminedChannels = false;
        if (response != null && getParentActivity() != null) {
            int a;
            for (a = 0; a < this.adminedChannelCells.size(); a++) {
                this.linearLayout.removeView((View) this.adminedChannelCells.get(a));
            }
            this.adminedChannelCells.clear();
            TL_messages_chats res = (TL_messages_chats) response;
            a = 0;
            while (a < res.chats.size()) {
                AdminedChannelCell adminedChannelCell = new AdminedChannelCell(getParentActivity(), new ChannelEditInfoActivity$$Lambda$21(this));
                adminedChannelCell.setChannel((Chat) res.chats.get(a), a == res.chats.size() + -1);
                this.adminedChannelCells.add(adminedChannelCell);
                this.adminnedChannelsLayout.addView(adminedChannelCell, LayoutHelper.createLinear(-1, 72));
                a++;
            }
            updatePrivatePublic();
        }
    }

    final /* synthetic */ void lambda$null$19$ChannelEditInfoActivity(View view) {
        Chat channel = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", CLASSNAMER.string.AppName));
        if (channel.megagroup) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", CLASSNAMER.string.RevokeLinkAlert, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + channel.username, channel.title)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", CLASSNAMER.string.RevokeLinkAlertChannel, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + channel.username, channel.title)));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", CLASSNAMER.string.RevokeButton), new ChannelEditInfoActivity$$Lambda$22(this, channel));
        showDialog(builder.create());
    }

    final /* synthetic */ void lambda$null$18$ChannelEditInfoActivity(Chat channel, DialogInterface dialogInterface, int i) {
        TL_channels_updateUsername req1 = new TL_channels_updateUsername();
        req1.channel = MessagesController.getInputChannel(channel);
        req1.username = TtmlNode.ANONYMOUS_REGION_ID;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, new ChannelEditInfoActivity$$Lambda$23(this), 64);
    }

    final /* synthetic */ void lambda$null$17$ChannelEditInfoActivity(TLObject response1, TL_error error1) {
        if (response1 instanceof TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new ChannelEditInfoActivity$$Lambda$24(this));
        }
    }

    final /* synthetic */ void lambda$null$16$ChannelEditInfoActivity() {
        this.canCreatePublic = true;
        if (this.nameTextView.length() > 0) {
            checkUserName(this.nameTextView.getText().toString());
        }
        updatePrivatePublic();
    }

    private void updatePrivatePublic() {
        int i = 8;
        boolean z = false;
        if (this.sectionCell2 != null) {
            if (this.isPrivate || this.canCreatePublic) {
                int i2;
                this.typeInfoCell.setTag(Theme.key_windowBackgroundWhiteGrayText4);
                this.typeInfoCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
                this.sectionCell2.setVisibility(0);
                this.adminedInfoCell.setVisibility(8);
                this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                this.adminnedChannelsLayout.setVisibility(8);
                this.linkContainer.setVisibility(0);
                this.loadingAdminedCell.setVisibility(8);
                if (this.currentChat.megagroup) {
                    this.typeInfoCell.setText(this.isPrivate ? LocaleController.getString("MegaPrivateLinkHelp", CLASSNAMER.string.MegaPrivateLinkHelp) : LocaleController.getString("MegaUsernameHelp", CLASSNAMER.string.MegaUsernameHelp));
                    this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", CLASSNAMER.string.ChannelInviteLinkTitle) : LocaleController.getString("ChannelLinkTitle", CLASSNAMER.string.ChannelLinkTitle));
                } else {
                    this.typeInfoCell.setText(this.isPrivate ? LocaleController.getString("ChannelPrivateLinkHelp", CLASSNAMER.string.ChannelPrivateLinkHelp) : LocaleController.getString("ChannelUsernameHelp", CLASSNAMER.string.ChannelUsernameHelp));
                    this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", CLASSNAMER.string.ChannelInviteLinkTitle) : LocaleController.getString("ChannelLinkTitle", CLASSNAMER.string.ChannelLinkTitle));
                }
                LinearLayout linearLayout = this.publicContainer;
                if (this.isPrivate) {
                    i2 = 8;
                } else {
                    i2 = 0;
                }
                linearLayout.setVisibility(i2);
                TextBlockCell textBlockCell = this.privateContainer;
                if (this.isPrivate) {
                    i2 = 0;
                } else {
                    i2 = 8;
                }
                textBlockCell.setVisibility(i2);
                this.linkContainer.setPadding(0, 0, 0, this.isPrivate ? 0 : AndroidUtilities.m10dp(7.0f));
                this.privateContainer.setText(this.invite != null ? this.invite.link : LocaleController.getString("Loading", CLASSNAMER.string.Loading), false);
                TextView textView = this.checkTextView;
                if (this.isPrivate || this.checkTextView.length() == 0) {
                    i2 = 8;
                } else {
                    i2 = 0;
                }
                textView.setVisibility(i2);
                if (this.headerCell2 != null) {
                    HeaderCell headerCell = this.headerCell2;
                    if (this.isPrivate) {
                        i2 = 0;
                    } else {
                        i2 = 8;
                    }
                    headerCell.setVisibility(i2);
                    linearLayout = this.linearLayoutInviteContainer;
                    if (this.isPrivate) {
                        i2 = 0;
                    } else {
                        i2 = 8;
                    }
                    linearLayout.setVisibility(i2);
                    ShadowSectionCell shadowSectionCell = this.sectionCell3;
                    if (this.isPrivate) {
                        i = 0;
                    }
                    shadowSectionCell.setVisibility(i);
                }
            } else {
                this.typeInfoCell.setText(LocaleController.getString("ChangePublicLimitReached", CLASSNAMER.string.ChangePublicLimitReached));
                this.typeInfoCell.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.typeInfoCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                this.linkContainer.setVisibility(8);
                this.sectionCell2.setVisibility(8);
                this.adminedInfoCell.setVisibility(0);
                if (this.loadingAdminedChannels) {
                    this.loadingAdminedCell.setVisibility(0);
                    this.adminnedChannelsLayout.setVisibility(8);
                    this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), CLASSNAMER.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    this.adminedInfoCell.setBackgroundDrawable(null);
                } else {
                    this.adminedInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.adminedInfoCell.getContext(), CLASSNAMER.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    this.loadingAdminedCell.setVisibility(8);
                    this.adminnedChannelsLayout.setVisibility(0);
                }
                if (this.headerCell2 != null) {
                    this.headerCell2.setVisibility(8);
                    this.linearLayoutInviteContainer.setVisibility(8);
                    this.sectionCell3.setVisibility(8);
                }
            }
            RadioButtonCell radioButtonCell = this.radioButtonCell1;
            if (!this.isPrivate) {
                z = true;
            }
            radioButtonCell.setChecked(z, true);
            this.radioButtonCell2.setChecked(this.isPrivate, true);
            this.usernameTextView.clearFocus();
            AndroidUtilities.hideKeyboard(this.nameTextView);
        }
    }

    private boolean checkUserName(String name) {
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
            if (name.startsWith("_") || name.endsWith("_")) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalid", CLASSNAMER.string.LinkInvalid));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                return false;
            }
            int a = 0;
            while (a < name.length()) {
                char ch = name.charAt(a);
                if (a != 0 || ch < '0' || ch > '9') {
                    if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                        this.checkTextView.setText(LocaleController.getString("LinkInvalid", CLASSNAMER.string.LinkInvalid));
                        this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                        this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                        return false;
                    }
                    a++;
                } else if (this.currentChat.megagroup) {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumberMega", CLASSNAMER.string.LinkInvalidStartNumberMega));
                    this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                    this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                    return false;
                } else {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", CLASSNAMER.string.LinkInvalidStartNumber));
                    this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                    this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                    return false;
                }
            }
        }
        if (name == null || name.length() < 5) {
            if (this.currentChat.megagroup) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalidShortMega", CLASSNAMER.string.LinkInvalidShortMega));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                return false;
            }
            this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", CLASSNAMER.string.LinkInvalidShort));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            return false;
        } else if (name.length() > 32) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", CLASSNAMER.string.LinkInvalidLong));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            return false;
        } else {
            this.checkTextView.setText(LocaleController.getString("LinkChecking", CLASSNAMER.string.LinkChecking));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGrayText8);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
            this.lastCheckName = name;
            this.checkRunnable = new ChannelEditInfoActivity$$Lambda$14(this, name);
            AndroidUtilities.runOnUIThread(this.checkRunnable, 300);
            return true;
        }
    }

    final /* synthetic */ void lambda$checkUserName$24$ChannelEditInfoActivity(String name) {
        TL_channels_checkUsername req = new TL_channels_checkUsername();
        req.username = name;
        req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChannelEditInfoActivity$$Lambda$18(this, name), 2);
    }

    final /* synthetic */ void lambda$null$23$ChannelEditInfoActivity(String name, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ChannelEditInfoActivity$$Lambda$19(this, name, error, response));
    }

    final /* synthetic */ void lambda$null$22$ChannelEditInfoActivity(String name, TL_error error, TLObject response) {
        this.checkReqId = 0;
        if (this.lastCheckName != null && this.lastCheckName.equals(name)) {
            if (error == null && (response instanceof TL_boolTrue)) {
                this.checkTextView.setText(LocaleController.formatString("LinkAvailable", CLASSNAMER.string.LinkAvailable, name));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGreenText);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText));
                this.lastNameAvailable = true;
                return;
            }
            if (error == null || !error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                this.checkTextView.setText(LocaleController.getString("LinkInUse", CLASSNAMER.string.LinkInUse));
            } else {
                this.canCreatePublic = false;
                loadAdminedChannels();
            }
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            this.lastNameAvailable = false;
        }
    }

    private void generateLink() {
        if (!this.loadingInvite && this.invite == null) {
            this.loadingInvite = true;
            TL_channels_exportInvite req = new TL_channels_exportInvite();
            req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChannelEditInfoActivity$$Lambda$15(this));
        }
    }

    final /* synthetic */ void lambda$generateLink$26$ChannelEditInfoActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ChannelEditInfoActivity$$Lambda$17(this, error, response));
    }

    final /* synthetic */ void lambda$null$25$ChannelEditInfoActivity(TL_error error, TLObject response) {
        if (error == null) {
            this.invite = (ExportedChatInvite) response;
            if (this.info != null) {
                this.info.exported_invite = this.invite;
            }
        }
        this.loadingInvite = false;
        if (this.privateContainer != null) {
            this.privateContainer.setText(this.invite != null ? this.invite.link : LocaleController.getString("Loading", CLASSNAMER.string.Loading), false);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new ChannelEditInfoActivity$$Lambda$16(this);
        r10 = new ThemeDescription[93];
        r10[17] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, cellDelegate, Theme.key_avatar_text);
        r10[18] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue);
        r10[19] = new ThemeDescription(this.lineView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider);
        r10[20] = new ThemeDescription(this.lineView2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider);
        r10[21] = new ThemeDescription(this.lineView3, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider);
        r10[22] = new ThemeDescription(this.sectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[23] = new ThemeDescription(this.sectionCell2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[24] = new ThemeDescription(this.sectionCell3, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[25] = new ThemeDescription(this.textCheckCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[26] = new ThemeDescription(this.textCheckCell, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[27] = new ThemeDescription(this.textCheckCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb);
        r10[28] = new ThemeDescription(this.textCheckCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        r10[29] = new ThemeDescription(this.textCheckCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked);
        r10[30] = new ThemeDescription(this.textCheckCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        r10[31] = new ThemeDescription(this.infoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[32] = new ThemeDescription(this.infoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r10[33] = new ThemeDescription(this.textCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[34] = new ThemeDescription(this.textCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText5);
        r10[35] = new ThemeDescription(this.textCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[36] = new ThemeDescription(this.textCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[37] = new ThemeDescription(this.infoCell2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[38] = new ThemeDescription(this.infoCell2, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r10[39] = new ThemeDescription(this.infoCell3, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[40] = new ThemeDescription(this.infoCell3, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r10[41] = new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[42] = new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r10[43] = new ThemeDescription(this.linearLayoutTypeContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[44] = new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[45] = new ThemeDescription(this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r10[46] = new ThemeDescription(this.headerCell2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r10[47] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[48] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r10[49] = new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteRedText4);
        r10[50] = new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8);
        r10[51] = new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGreenText);
        r10[52] = new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[53] = new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r10[54] = new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText4);
        r10[55] = new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[56] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[57] = new ThemeDescription(this.privateContainer, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[58] = new ThemeDescription(this.privateContainer, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[59] = new ThemeDescription(this.loadingAdminedCell, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        r10[60] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[61] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        r10[62] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        r10[63] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[64] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r10[65] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[66] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        r10[67] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        r10[68] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[69] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r10[70] = new ThemeDescription(this.linearLayoutInviteContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[71] = new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[72] = new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        r10[73] = new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        r10[74] = new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[75] = new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r10[76] = new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[77] = new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        r10[78] = new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        r10[79] = new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[80] = new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r10[81] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[82] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText);
        r10[83] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_windowBackgroundWhiteLinkText);
        r10[84] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"deleteButton"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText);
        r10[85] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, cellDelegate, Theme.key_avatar_text);
        r10[86] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed);
        r10[87] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange);
        r10[88] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet);
        r10[89] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen);
        r10[90] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan);
        r10[91] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue);
        r10[92] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink);
        return r10;
    }

    final /* synthetic */ void lambda$getThemeDescriptions$27$ChannelEditInfoActivity() {
        if (this.avatarImage != null) {
            String obj;
            AvatarDrawable avatarDrawable = this.avatarDrawable;
            if (this.nameTextView.length() > 0) {
                obj = this.nameTextView.getText().toString();
            } else {
                obj = null;
            }
            avatarDrawable.setInfo(5, obj, null, false);
            this.avatarImage.invalidate();
        }
        if (this.adminnedChannelsLayout != null) {
            int count = this.adminnedChannelsLayout.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.adminnedChannelsLayout.getChildAt(a);
                if (child instanceof AdminedChannelCell) {
                    ((AdminedChannelCell) child).update();
                }
            }
        }
    }
}
