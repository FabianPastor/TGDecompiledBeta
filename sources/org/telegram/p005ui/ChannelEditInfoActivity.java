package org.telegram.p005ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.C0403ActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.p005ui.Cells.AdminedChannelCell;
import org.telegram.p005ui.Cells.HeaderCell;
import org.telegram.p005ui.Cells.LoadingCell;
import org.telegram.p005ui.Cells.RadioButtonCell;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.Cells.TextBlockCell;
import org.telegram.p005ui.Cells.TextInfoPrivacyCell;
import org.telegram.p005ui.Cells.TextSettingsCell;
import org.telegram.p005ui.Components.EditTextBoldCursor;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_channels_checkUsername;
import org.telegram.tgnet.TLRPC.TL_channels_exportInvite;
import org.telegram.tgnet.TLRPC.TL_channels_getAdminedPublicChannels;
import org.telegram.tgnet.TLRPC.TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC.TL_chatInviteExported;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_chats;

/* renamed from: org.telegram.ui.ChannelEditInfoActivity */
public class ChannelEditInfoActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList();
    private ShadowSectionCell adminedInfoCell;
    private LinearLayout adminnedChannelsLayout;
    private boolean canCreatePublic = true;
    private int chatId;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextView checkTextView;
    private Chat currentChat;
    private EditText editText;
    private HeaderCell headerCell;
    private ChatFull info;
    private TextInfoPrivacyCell infoCell;
    private ExportedChatInvite invite;
    private boolean isPrivate;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private LinearLayout linearLayout;
    private LinearLayout linearLayoutTypeContainer;
    private LinearLayout linkContainer;
    private LoadingCell loadingAdminedCell;
    private boolean loadingAdminedChannels;
    private boolean loadingInvite;
    private TextBlockCell privateContainer;
    private LinearLayout publicContainer;
    private RadioButtonCell radioButtonCell1;
    private RadioButtonCell radioButtonCell2;
    private ShadowSectionCell sectionCell2;
    private TextSettingsCell textCell;
    private TextSettingsCell textCell2;
    private TextInfoPrivacyCell typeInfoCell;
    private EditTextBoldCursor usernameTextView;

    /* renamed from: org.telegram.ui.ChannelEditInfoActivity$2 */
    class C05622 implements TextWatcher {
        C05622() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            ChannelEditInfoActivity.this.checkUserName(ChannelEditInfoActivity.this.usernameTextView.getText().toString());
        }

        public void afterTextChanged(Editable editable) {
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditInfoActivity$1 */
    class C12911 extends ActionBarMenuOnItemClick {
        C12911() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChannelEditInfoActivity.this.lambda$checkDiscard$70$PassportActivity();
            } else if (id != 1) {
            } else {
                if (ChannelEditInfoActivity.this.isPrivate || (((ChannelEditInfoActivity.this.currentChat.username != null || ChannelEditInfoActivity.this.usernameTextView.length() == 0) && (ChannelEditInfoActivity.this.currentChat.username == null || ChannelEditInfoActivity.this.currentChat.username.equalsIgnoreCase(ChannelEditInfoActivity.this.usernameTextView.getText().toString()))) || ChannelEditInfoActivity.this.usernameTextView.length() == 0 || ChannelEditInfoActivity.this.lastNameAvailable)) {
                    String oldUserName = ChannelEditInfoActivity.this.currentChat.username != null ? ChannelEditInfoActivity.this.currentChat.username : TtmlNode.ANONYMOUS_REGION_ID;
                    String newUserName = ChannelEditInfoActivity.this.isPrivate ? TtmlNode.ANONYMOUS_REGION_ID : ChannelEditInfoActivity.this.usernameTextView.getText().toString();
                    if (!oldUserName.equals(newUserName)) {
                        MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).updateChannelUserName(ChannelEditInfoActivity.this.chatId, newUserName);
                        ChannelEditInfoActivity.this.currentChat.username = newUserName;
                    }
                    ChannelEditInfoActivity.this.lambda$checkDiscard$70$PassportActivity();
                    return;
                }
                Vibrator v = (Vibrator) ChannelEditInfoActivity.this.getParentActivity().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(200);
                }
                AndroidUtilities.shakeView(ChannelEditInfoActivity.this.checkTextView, 2.0f, 0);
            }
        }
    }

    public ChannelEditInfoActivity(int id) {
        this.chatId = id;
    }

    public boolean onFragmentCreate() {
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        if (this.currentChat == null) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new ChannelEditInfoActivity$$Lambda$0(this, countDownLatch));
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
        this.isPrivate = TextUtils.isEmpty(this.currentChat.username);
        if (this.isPrivate && this.currentChat.creator) {
            TL_channels_checkUsername req = new TL_channels_checkUsername();
            req.username = "1";
            req.channel = new TL_inputChannelEmpty();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChannelEditInfoActivity$$Lambda$1(this));
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        return super.onFragmentCreate();
    }

    final /* synthetic */ void lambda$onFragmentCreate$0$ChannelEditInfoActivity(CountDownLatch countDownLatch) {
        this.currentChat = MessagesStorage.getInstance(this.currentAccount).getChat(this.chatId);
        countDownLatch.countDown();
    }

    final /* synthetic */ void lambda$onFragmentCreate$2$ChannelEditInfoActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ChannelEditInfoActivity$$Lambda$17(this, error));
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
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
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
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C12911());
        this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.m9dp(56.0f));
        this.fragmentView = new ScrollView(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        ScrollView scrollView = this.fragmentView;
        scrollView.setFillViewport(true);
        this.linearLayout = new LinearLayout(context);
        scrollView.addView(this.linearLayout, new LayoutParams(-1, -2));
        this.linearLayout.setOrientation(1);
        if (this.currentChat.megagroup) {
            this.actionBar.setTitle(LocaleController.getString("GroupType", R.string.GroupType));
        } else {
            this.actionBar.setTitle(LocaleController.getString("ChannelType", R.string.ChannelType));
        }
        this.linearLayoutTypeContainer = new LinearLayout(context);
        this.linearLayoutTypeContainer.setOrientation(1);
        this.linearLayoutTypeContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout.addView(this.linearLayoutTypeContainer, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell1 = new RadioButtonCell(context);
        this.radioButtonCell1.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (this.currentChat.megagroup) {
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("MegaPublic", R.string.MegaPublic), LocaleController.getString("MegaPublicInfo", R.string.MegaPublicInfo), false, !this.isPrivate);
        } else {
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", R.string.ChannelPublic), LocaleController.getString("ChannelPublicInfo", R.string.ChannelPublicInfo), false, !this.isPrivate);
        }
        this.linearLayoutTypeContainer.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell1.setOnClickListener(new ChannelEditInfoActivity$$Lambda$2(this));
        this.radioButtonCell2 = new RadioButtonCell(context);
        this.radioButtonCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (this.currentChat.megagroup) {
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("MegaPrivate", R.string.MegaPrivate), LocaleController.getString("MegaPrivateInfo", R.string.MegaPrivateInfo), false, this.isPrivate);
        } else {
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate), LocaleController.getString("ChannelPrivateInfo", R.string.ChannelPrivateInfo), false, this.isPrivate);
        }
        this.linearLayoutTypeContainer.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell2.setOnClickListener(new ChannelEditInfoActivity$$Lambda$3(this));
        this.sectionCell2 = new ShadowSectionCell(context);
        this.linearLayout.addView(this.sectionCell2, LayoutHelper.createLinear(-1, -2));
        this.linkContainer = new LinearLayout(context);
        this.linkContainer.setOrientation(1);
        this.linkContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
        this.headerCell = new HeaderCell(context, 23);
        this.linkContainer.addView(this.headerCell);
        this.publicContainer = new LinearLayout(context);
        this.publicContainer.setOrientation(0);
        this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 23.0f, 7.0f, 23.0f, 0.0f));
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
        this.usernameTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", R.string.ChannelUsernamePlaceholder));
        this.usernameTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.usernameTextView.setCursorSize(AndroidUtilities.m9dp(20.0f));
        this.usernameTextView.setCursorWidth(1.5f);
        this.publicContainer.addView(this.usernameTextView, LayoutHelper.createLinear(-1, 36));
        this.usernameTextView.addTextChangedListener(new C05622());
        this.privateContainer = new TextBlockCell(context);
        this.privateContainer.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.linkContainer.addView(this.privateContainer);
        this.privateContainer.setOnClickListener(new ChannelEditInfoActivity$$Lambda$4(this));
        this.checkTextView = new TextView(context);
        this.checkTextView.setTextSize(1, 15.0f);
        this.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.checkTextView.setVisibility(8);
        this.linkContainer.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 17, 3, 17, 7));
        this.typeInfoCell = new TextInfoPrivacyCell(context);
        this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
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
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$3$ChannelEditInfoActivity(View v) {
        if (this.isPrivate) {
            this.isPrivate = false;
            updatePrivatePublic();
        }
    }

    final /* synthetic */ void lambda$createView$4$ChannelEditInfoActivity(View v) {
        if (!this.isPrivate) {
            this.isPrivate = true;
            updatePrivatePublic();
        }
    }

    final /* synthetic */ void lambda$createView$5$ChannelEditInfoActivity(View v) {
        if (this.invite != null) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.invite.link));
                Toast.makeText(getParentActivity(), LocaleController.getString("LinkCopied", R.string.LinkCopied), 0).show();
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad) {
            ChatFull chatFull = args[0];
            if (chatFull.f79id == this.chatId) {
                this.info = chatFull;
                this.invite = chatFull.exported_invite;
                updatePrivatePublic();
            }
        }
    }

    public void setInfo(ChatFull chatFull) {
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_channels_getAdminedPublicChannels(), new ChannelEditInfoActivity$$Lambda$5(this));
        }
    }

    final /* synthetic */ void lambda$loadAdminedChannels$11$ChannelEditInfoActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ChannelEditInfoActivity$$Lambda$12(this, response));
    }

    final /* synthetic */ void lambda$null$10$ChannelEditInfoActivity(TLObject response) {
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
                AdminedChannelCell adminedChannelCell = new AdminedChannelCell(getParentActivity(), new ChannelEditInfoActivity$$Lambda$13(this));
                adminedChannelCell.setChannel((Chat) res.chats.get(a), a == res.chats.size() + -1);
                this.adminedChannelCells.add(adminedChannelCell);
                this.adminnedChannelsLayout.addView(adminedChannelCell, LayoutHelper.createLinear(-1, 72));
                a++;
            }
            updatePrivatePublic();
        }
    }

    final /* synthetic */ void lambda$null$9$ChannelEditInfoActivity(View view) {
        Chat channel = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        if (channel.megagroup) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", R.string.RevokeLinkAlert, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + channel.username, channel.title)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", R.string.RevokeLinkAlertChannel, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + channel.username, channel.title)));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", R.string.RevokeButton), new ChannelEditInfoActivity$$Lambda$14(this, channel));
        showDialog(builder.create());
    }

    final /* synthetic */ void lambda$null$8$ChannelEditInfoActivity(Chat channel, DialogInterface dialogInterface, int i) {
        TL_channels_updateUsername req1 = new TL_channels_updateUsername();
        req1.channel = MessagesController.getInputChannel(channel);
        req1.username = TtmlNode.ANONYMOUS_REGION_ID;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, new ChannelEditInfoActivity$$Lambda$15(this), 64);
    }

    final /* synthetic */ void lambda$null$7$ChannelEditInfoActivity(TLObject response1, TL_error error1) {
        if (response1 instanceof TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new ChannelEditInfoActivity$$Lambda$16(this));
        }
    }

    final /* synthetic */ void lambda$null$6$ChannelEditInfoActivity() {
        this.canCreatePublic = true;
        if (this.usernameTextView.length() > 0) {
            checkUserName(this.usernameTextView.getText().toString());
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
                this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                this.adminnedChannelsLayout.setVisibility(8);
                this.linkContainer.setVisibility(0);
                this.loadingAdminedCell.setVisibility(8);
                if (this.currentChat.megagroup) {
                    this.typeInfoCell.setText(this.isPrivate ? LocaleController.getString("MegaPrivateLinkHelp", R.string.MegaPrivateLinkHelp) : LocaleController.getString("MegaUsernameHelp", R.string.MegaUsernameHelp));
                    this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", R.string.ChannelInviteLinkTitle) : LocaleController.getString("ChannelLinkTitle", R.string.ChannelLinkTitle));
                } else {
                    this.typeInfoCell.setText(this.isPrivate ? LocaleController.getString("ChannelPrivateLinkHelp", R.string.ChannelPrivateLinkHelp) : LocaleController.getString("ChannelUsernameHelp", R.string.ChannelUsernameHelp));
                    this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", R.string.ChannelInviteLinkTitle) : LocaleController.getString("ChannelLinkTitle", R.string.ChannelLinkTitle));
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
                this.linkContainer.setPadding(0, 0, 0, this.isPrivate ? 0 : AndroidUtilities.m9dp(7.0f));
                this.privateContainer.setText(this.invite != null ? this.invite.link : LocaleController.getString("Loading", R.string.Loading), false);
                TextView textView = this.checkTextView;
                if (!(this.isPrivate || this.checkTextView.length() == 0)) {
                    i = 0;
                }
                textView.setVisibility(i);
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
                    this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    this.adminedInfoCell.setBackgroundDrawable(null);
                } else {
                    this.adminedInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.adminedInfoCell.getContext(), (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    this.loadingAdminedCell.setVisibility(8);
                    this.adminnedChannelsLayout.setVisibility(0);
                }
            }
            RadioButtonCell radioButtonCell = this.radioButtonCell1;
            if (!this.isPrivate) {
                z = true;
            }
            radioButtonCell.setChecked(z, true);
            this.radioButtonCell2.setChecked(this.isPrivate, true);
            this.usernameTextView.clearFocus();
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
                this.checkTextView.setText(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                return false;
            }
            int a = 0;
            while (a < name.length()) {
                char ch = name.charAt(a);
                if (a != 0 || ch < '0' || ch > '9') {
                    if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                        this.checkTextView.setText(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
                        this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                        this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                        return false;
                    }
                    a++;
                } else if (this.currentChat.megagroup) {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumberMega", R.string.LinkInvalidStartNumberMega));
                    this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                    this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                    return false;
                } else {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", R.string.LinkInvalidStartNumber));
                    this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                    this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                    return false;
                }
            }
        }
        if (name == null || name.length() < 5) {
            if (this.currentChat.megagroup) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalidShortMega", R.string.LinkInvalidShortMega));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                return false;
            }
            this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", R.string.LinkInvalidShort));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            return false;
        } else if (name.length() > 32) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", R.string.LinkInvalidLong));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            return false;
        } else {
            this.checkTextView.setText(LocaleController.getString("LinkChecking", R.string.LinkChecking));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGrayText8);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
            this.lastCheckName = name;
            this.checkRunnable = new ChannelEditInfoActivity$$Lambda$6(this, name);
            AndroidUtilities.runOnUIThread(this.checkRunnable, 300);
            return true;
        }
    }

    final /* synthetic */ void lambda$checkUserName$14$ChannelEditInfoActivity(String name) {
        TL_channels_checkUsername req = new TL_channels_checkUsername();
        req.username = name;
        req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChannelEditInfoActivity$$Lambda$10(this, name), 2);
    }

    final /* synthetic */ void lambda$null$13$ChannelEditInfoActivity(String name, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ChannelEditInfoActivity$$Lambda$11(this, name, error, response));
    }

    final /* synthetic */ void lambda$null$12$ChannelEditInfoActivity(String name, TL_error error, TLObject response) {
        this.checkReqId = 0;
        if (this.lastCheckName != null && this.lastCheckName.equals(name)) {
            if (error == null && (response instanceof TL_boolTrue)) {
                this.checkTextView.setText(LocaleController.formatString("LinkAvailable", R.string.LinkAvailable, name));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGreenText);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText));
                this.lastNameAvailable = true;
                return;
            }
            if (error == null || !error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                this.checkTextView.setText(LocaleController.getString("LinkInUse", R.string.LinkInUse));
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChannelEditInfoActivity$$Lambda$7(this));
        }
    }

    final /* synthetic */ void lambda$generateLink$16$ChannelEditInfoActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ChannelEditInfoActivity$$Lambda$9(this, error, response));
    }

    final /* synthetic */ void lambda$null$15$ChannelEditInfoActivity(TL_error error, TLObject response) {
        if (error == null) {
            this.invite = (ExportedChatInvite) response;
            if (this.info != null) {
                this.info.exported_invite = this.invite;
            }
        }
        this.loadingInvite = false;
        if (this.privateContainer != null) {
            this.privateContainer.setText(this.invite != null ? this.invite.link : LocaleController.getString("Loading", R.string.Loading), false);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new ChannelEditInfoActivity$$Lambda$8(this);
        r10 = new ThemeDescription[52];
        r10[5] = new ThemeDescription(this.sectionCell2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[6] = new ThemeDescription(this.infoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[7] = new ThemeDescription(this.infoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r10[8] = new ThemeDescription(this.textCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[9] = new ThemeDescription(this.textCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText5);
        r10[10] = new ThemeDescription(this.textCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[11] = new ThemeDescription(this.textCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[12] = new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[13] = new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r10[14] = new ThemeDescription(this.linearLayoutTypeContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[15] = new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[16] = new ThemeDescription(this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r10[17] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[18] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        r10[19] = new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteRedText4);
        r10[20] = new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8);
        r10[21] = new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGreenText);
        r10[22] = new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[23] = new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r10[24] = new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText4);
        r10[25] = new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[26] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[27] = new ThemeDescription(this.privateContainer, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[28] = new ThemeDescription(this.privateContainer, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[29] = new ThemeDescription(this.loadingAdminedCell, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        r10[30] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[31] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        r10[32] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        r10[33] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[34] = new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r10[35] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[36] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        r10[37] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        r10[38] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[39] = new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r10[40] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[41] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText);
        r10[42] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_windowBackgroundWhiteLinkText);
        r10[43] = new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"deleteButton"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText);
        r10[44] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, cellDelegate, Theme.key_avatar_text);
        r10[45] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed);
        r10[46] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange);
        r10[47] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet);
        r10[48] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen);
        r10[49] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan);
        r10[50] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue);
        r10[51] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink);
        return r10;
    }

    final /* synthetic */ void lambda$getThemeDescriptions$17$ChannelEditInfoActivity() {
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
