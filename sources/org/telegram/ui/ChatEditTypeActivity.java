package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AdminedChannelCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.InviteLinkBottomSheet;
import org.telegram.ui.Components.JoinToSendSettingsView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkActionView;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;

public class ChatEditTypeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList<>();
    private ShadowSectionCell adminedInfoCell;
    private LinearLayout adminnedChannelsLayout;
    private boolean canCreatePublic = true;
    /* access modifiers changed from: private */
    public long chatId;
    private int checkReqId;
    private Runnable checkRunnable;
    /* access modifiers changed from: private */
    public TextInfoPrivacyCell checkTextView;
    /* access modifiers changed from: private */
    public TLRPC.Chat currentChat;
    private ActionBarMenuItem doneButton;
    private EditTextBoldCursor editText;
    private HeaderCell headerCell;
    private HeaderCell headerCell2;
    /* access modifiers changed from: private */
    public boolean ignoreTextChanges;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull info;
    private TextInfoPrivacyCell infoCell;
    /* access modifiers changed from: private */
    public TLRPC.TL_chatInviteExported invite;
    /* access modifiers changed from: private */
    public InviteLinkBottomSheet inviteLinkBottomSheet;
    private boolean isChannel;
    private boolean isForcePublic;
    private boolean isPrivate;
    private boolean isSaveRestricted;
    private JoinToSendSettingsView joinContainer;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private LinearLayout linearLayout;
    private LinearLayout linearLayoutTypeContainer;
    private LinearLayout linkContainer;
    private LoadingCell loadingAdminedCell;
    private boolean loadingAdminedChannels;
    private boolean loadingInvite;
    private TextInfoPrivacyCell manageLinksInfoCell;
    private TextCell manageLinksTextView;
    private LinkActionView permanentLinkView;
    private LinearLayout privateContainer;
    private LinearLayout publicContainer;
    private RadioButtonCell radioButtonCell1;
    private RadioButtonCell radioButtonCell2;
    private LinearLayout saveContainer;
    private HeaderCell saveHeaderCell;
    private TextCheckCell saveRestrictCell;
    private TextInfoPrivacyCell saveRestrictInfoCell;
    private ShadowSectionCell sectionCell2;
    private TextSettingsCell textCell;
    private TextSettingsCell textCell2;
    private TextInfoPrivacyCell typeInfoCell;
    /* access modifiers changed from: private */
    public EditTextBoldCursor usernameTextView;
    HashMap<Long, TLRPC.User> usersMap = new HashMap<>();

    public ChatEditTypeActivity(long id, boolean forcePublic) {
        this.chatId = id;
        this.isForcePublic = forcePublic;
    }

    public boolean onFragmentCreate() {
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
        this.currentChat = chat;
        boolean z = false;
        if (chat == null) {
            TLRPC.Chat chatSync = getMessagesStorage().getChatSync(this.chatId);
            this.currentChat = chatSync;
            if (chatSync == null) {
                return false;
            }
            getMessagesController().putChat(this.currentChat, true);
            if (this.info == null) {
                TLRPC.ChatFull loadChatInfo = getMessagesStorage().loadChatInfo(this.chatId, ChatObject.isChannel(this.currentChat), new CountDownLatch(1), false, false);
                this.info = loadChatInfo;
                if (loadChatInfo == null) {
                    return false;
                }
            }
        }
        this.isPrivate = !this.isForcePublic && TextUtils.isEmpty(this.currentChat.username);
        if (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup) {
            z = true;
        }
        this.isChannel = z;
        this.isSaveRestricted = this.currentChat.noforwards;
        if ((this.isForcePublic && TextUtils.isEmpty(this.currentChat.username)) || (this.isPrivate && this.currentChat.creator)) {
            TLRPC.TL_channels_checkUsername req = new TLRPC.TL_channels_checkUsername();
            req.username = "1";
            req.channel = new TLRPC.TL_inputChannelEmpty();
            getConnectionsManager().sendRequest(req, new ChatEditTypeActivity$$ExternalSyntheticLambda8(this));
        }
        if (this.isPrivate && this.info != null) {
            getMessagesController().loadFullChat(this.chatId, this.classGuid, true);
        }
        getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
        return super.onFragmentCreate();
    }

    /* renamed from: lambda$onFragmentCreate$1$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3226lambda$onFragmentCreate$1$orgtelegramuiChatEditTypeActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatEditTypeActivity$$ExternalSyntheticLambda2(this, error));
    }

    /* renamed from: lambda$onFragmentCreate$0$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3225lambda$onFragmentCreate$0$orgtelegramuiChatEditTypeActivity(TLRPC.TL_error error) {
        boolean z = error == null || !error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH");
        this.canCreatePublic = z;
        if (!z && getUserConfig().isPremium()) {
            loadAdminedChannels();
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        TLRPC.ChatFull chatFull;
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        if (!(this.textCell2 == null || (chatFull = this.info) == null)) {
            if (chatFull.stickerset != null) {
                this.textCell2.setTextAndValue(LocaleController.getString("GroupStickers", NUM), this.info.stickerset.title, false);
            } else {
                this.textCell2.setText(LocaleController.getString("GroupStickers", NUM), false);
            }
        }
        TLRPC.ChatFull chatFull2 = this.info;
        if (chatFull2 != null) {
            TLRPC.TL_chatInviteExported tL_chatInviteExported = chatFull2.exported_invite;
            this.invite = tL_chatInviteExported;
            this.permanentLinkView.setLink(tL_chatInviteExported == null ? null : tL_chatInviteExported.link);
            this.permanentLinkView.loadUsers(this.invite, this.chatId);
        }
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyVisible() {
        EditTextBoldCursor editTextBoldCursor;
        super.onBecomeFullyVisible();
        if (this.isForcePublic && (editTextBoldCursor = this.usernameTextView) != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.usernameTextView);
        }
    }

    public View createView(Context context) {
        final Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChatEditTypeActivity.this.finishFragment();
                } else if (id == 1) {
                    ChatEditTypeActivity.this.processDone();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), (CharSequence) LocaleController.getString("Done", NUM));
        this.fragmentView = new ScrollView(context2) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                rectangle.bottom += AndroidUtilities.dp(60.0f);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        ScrollView scrollView = (ScrollView) this.fragmentView;
        scrollView.setFillViewport(true);
        LinearLayout linearLayout2 = new LinearLayout(context2);
        this.linearLayout = linearLayout2;
        scrollView.addView(linearLayout2, new FrameLayout.LayoutParams(-1, -2));
        this.linearLayout.setOrientation(1);
        if (this.isForcePublic) {
            this.actionBar.setTitle(LocaleController.getString("TypeLocationGroup", NUM));
        } else if (this.isChannel) {
            this.actionBar.setTitle(LocaleController.getString("ChannelSettingsTitle", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("GroupSettingsTitle", NUM));
        }
        LinearLayout linearLayout3 = new LinearLayout(context2);
        this.linearLayoutTypeContainer = linearLayout3;
        linearLayout3.setOrientation(1);
        this.linearLayoutTypeContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout.addView(this.linearLayoutTypeContainer, LayoutHelper.createLinear(-1, -2));
        HeaderCell headerCell3 = new HeaderCell(context2, 23);
        this.headerCell2 = headerCell3;
        headerCell3.setHeight(46);
        if (this.isChannel) {
            this.headerCell2.setText(LocaleController.getString("ChannelTypeHeader", NUM));
        } else {
            this.headerCell2.setText(LocaleController.getString("GroupTypeHeader", NUM));
        }
        this.linearLayoutTypeContainer.addView(this.headerCell2);
        RadioButtonCell radioButtonCell = new RadioButtonCell(context2);
        this.radioButtonCell2 = radioButtonCell;
        radioButtonCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (this.isChannel) {
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", NUM), LocaleController.getString("ChannelPrivateInfo", NUM), false, this.isPrivate);
        } else {
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("MegaPrivate", NUM), LocaleController.getString("MegaPrivateInfo", NUM), false, this.isPrivate);
        }
        this.linearLayoutTypeContainer.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell2.setOnClickListener(new ChatEditTypeActivity$$ExternalSyntheticLambda11(this));
        RadioButtonCell radioButtonCell3 = new RadioButtonCell(context2);
        this.radioButtonCell1 = radioButtonCell3;
        radioButtonCell3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (this.isChannel) {
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", NUM), LocaleController.getString("ChannelPublicInfo", NUM), false, !this.isPrivate);
        } else {
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("MegaPublic", NUM), LocaleController.getString("MegaPublicInfo", NUM), false, !this.isPrivate);
        }
        this.linearLayoutTypeContainer.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell1.setOnClickListener(new ChatEditTypeActivity$$ExternalSyntheticLambda13(this));
        ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context2);
        this.sectionCell2 = shadowSectionCell;
        this.linearLayout.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
        if (this.isForcePublic) {
            this.radioButtonCell2.setVisibility(8);
            this.radioButtonCell1.setVisibility(8);
            this.sectionCell2.setVisibility(8);
            this.headerCell2.setVisibility(8);
        }
        LinearLayout linearLayout4 = new LinearLayout(context2);
        this.linkContainer = linearLayout4;
        linearLayout4.setOrientation(1);
        this.linkContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
        HeaderCell headerCell4 = new HeaderCell(context2, 23);
        this.headerCell = headerCell4;
        this.linkContainer.addView(headerCell4);
        LinearLayout linearLayout5 = new LinearLayout(context2);
        this.publicContainer = linearLayout5;
        linearLayout5.setOrientation(0);
        this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 23.0f, 7.0f, 23.0f, 0.0f));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.editText = editTextBoldCursor;
        editTextBoldCursor.setText(getMessagesController().linkPrefix + "/");
        this.editText.setTextSize(1, 18.0f);
        this.editText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.editText.setMaxLines(1);
        this.editText.setLines(1);
        this.editText.setEnabled(false);
        this.editText.setBackgroundDrawable((Drawable) null);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setSingleLine(true);
        this.editText.setInputType(163840);
        this.editText.setImeOptions(6);
        this.publicContainer.addView(this.editText, LayoutHelper.createLinear(-2, 36));
        AnonymousClass3 r0 = new EditTextBoldCursor(context2) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                StringBuilder sb = new StringBuilder();
                sb.append(getText());
                if (!(ChatEditTypeActivity.this.checkTextView == null || ChatEditTypeActivity.this.checkTextView.getTextView() == null || TextUtils.isEmpty(ChatEditTypeActivity.this.checkTextView.getTextView().getText()))) {
                    sb.append("\n");
                    sb.append(ChatEditTypeActivity.this.checkTextView.getTextView().getText());
                }
                info.setText(sb);
            }
        };
        this.usernameTextView = r0;
        r0.setTextSize(1, 18.0f);
        this.usernameTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.usernameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.usernameTextView.setMaxLines(1);
        this.usernameTextView.setLines(1);
        this.usernameTextView.setBackgroundDrawable((Drawable) null);
        this.usernameTextView.setPadding(0, 0, 0, 0);
        this.usernameTextView.setSingleLine(true);
        this.usernameTextView.setInputType(163872);
        this.usernameTextView.setImeOptions(6);
        this.usernameTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", NUM));
        this.usernameTextView.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.usernameTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        this.usernameTextView.setCursorWidth(1.5f);
        this.publicContainer.addView(this.usernameTextView, LayoutHelper.createLinear(-1, 36));
        this.usernameTextView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!ChatEditTypeActivity.this.ignoreTextChanges) {
                    ChatEditTypeActivity chatEditTypeActivity = ChatEditTypeActivity.this;
                    boolean unused = chatEditTypeActivity.checkUserName(chatEditTypeActivity.usernameTextView.getText().toString());
                }
            }

            public void afterTextChanged(Editable editable) {
                ChatEditTypeActivity.this.checkDoneButton();
            }
        });
        LinearLayout linearLayout6 = new LinearLayout(context2);
        this.privateContainer = linearLayout6;
        linearLayout6.setOrientation(1);
        this.linkContainer.addView(this.privateContainer, LayoutHelper.createLinear(-1, -2));
        LinkActionView linkActionView = new LinkActionView(context, this, (BottomSheet) null, this.chatId, true, ChatObject.isChannel(this.currentChat));
        this.permanentLinkView = linkActionView;
        linkActionView.setDelegate(new LinkActionView.Delegate() {
            public /* synthetic */ void editLink() {
                LinkActionView.Delegate.CC.$default$editLink(this);
            }

            public /* synthetic */ void removeLink() {
                LinkActionView.Delegate.CC.$default$removeLink(this);
            }

            public void revokeLink() {
                ChatEditTypeActivity.this.generateLink(true);
            }

            public void showUsersForPermanentLink() {
                ChatEditTypeActivity chatEditTypeActivity = ChatEditTypeActivity.this;
                Context context = context2;
                TLRPC.TL_chatInviteExported access$800 = ChatEditTypeActivity.this.invite;
                TLRPC.ChatFull access$900 = ChatEditTypeActivity.this.info;
                HashMap<Long, TLRPC.User> hashMap = ChatEditTypeActivity.this.usersMap;
                ChatEditTypeActivity chatEditTypeActivity2 = ChatEditTypeActivity.this;
                InviteLinkBottomSheet unused = chatEditTypeActivity.inviteLinkBottomSheet = new InviteLinkBottomSheet(context, access$800, access$900, hashMap, chatEditTypeActivity2, chatEditTypeActivity2.chatId, true, ChatObject.isChannel(ChatEditTypeActivity.this.currentChat));
                ChatEditTypeActivity.this.inviteLinkBottomSheet.show();
            }
        });
        this.permanentLinkView.setUsers(0, (ArrayList<TLRPC.User>) null);
        this.privateContainer.addView(this.permanentLinkView);
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
        this.checkTextView = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        this.checkTextView.setBottomPadding(6);
        this.linearLayout.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2));
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
        this.typeInfoCell = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setImportantForAccessibility(1);
        this.linearLayout.addView(this.typeInfoCell, LayoutHelper.createLinear(-1, -2));
        LoadingCell loadingCell = new LoadingCell(context2);
        this.loadingAdminedCell = loadingCell;
        this.linearLayout.addView(loadingCell, LayoutHelper.createLinear(-1, -2));
        LinearLayout linearLayout7 = new LinearLayout(context2);
        this.adminnedChannelsLayout = linearLayout7;
        linearLayout7.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.adminnedChannelsLayout.setOrientation(1);
        this.linearLayout.addView(this.adminnedChannelsLayout, LayoutHelper.createLinear(-1, -2));
        ShadowSectionCell shadowSectionCell2 = new ShadowSectionCell(context2);
        this.adminedInfoCell = shadowSectionCell2;
        this.linearLayout.addView(shadowSectionCell2, LayoutHelper.createLinear(-1, -2));
        TextCell textCell3 = new TextCell(context2);
        this.manageLinksTextView = textCell3;
        textCell3.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.manageLinksTextView.setTextAndIcon(LocaleController.getString("ManageInviteLinks", NUM), NUM, false);
        this.manageLinksTextView.setOnClickListener(new ChatEditTypeActivity$$ExternalSyntheticLambda14(this));
        this.linearLayout.addView(this.manageLinksTextView, LayoutHelper.createLinear(-1, -2));
        TextInfoPrivacyCell textInfoPrivacyCell3 = new TextInfoPrivacyCell(context2);
        this.manageLinksInfoCell = textInfoPrivacyCell3;
        this.linearLayout.addView(textInfoPrivacyCell3, LayoutHelper.createLinear(-1, -2));
        JoinToSendSettingsView joinToSendSettingsView = new JoinToSendSettingsView(context2, this.currentChat);
        this.joinContainer = joinToSendSettingsView;
        this.linearLayout.addView(joinToSendSettingsView);
        LinearLayout linearLayout8 = new LinearLayout(context2);
        this.saveContainer = linearLayout8;
        linearLayout8.setOrientation(1);
        this.linearLayout.addView(this.saveContainer);
        HeaderCell headerCell5 = new HeaderCell(context2, 23);
        this.saveHeaderCell = headerCell5;
        headerCell5.setHeight(46);
        this.saveHeaderCell.setText(LocaleController.getString("SavingContentTitle", NUM));
        this.saveHeaderCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.saveContainer.addView(this.saveHeaderCell, LayoutHelper.createLinear(-1, -2));
        TextCheckCell textCheckCell = new TextCheckCell(context2);
        this.saveRestrictCell = textCheckCell;
        textCheckCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.saveRestrictCell.setTextAndCheck(LocaleController.getString("RestrictSavingContent", NUM), this.isSaveRestricted, false);
        this.saveRestrictCell.setOnClickListener(new ChatEditTypeActivity$$ExternalSyntheticLambda15(this));
        this.saveContainer.addView(this.saveRestrictCell, LayoutHelper.createLinear(-1, -2));
        this.saveRestrictInfoCell = new TextInfoPrivacyCell(context2);
        if (!this.isChannel || ChatObject.isMegagroup(this.currentChat)) {
            this.saveRestrictInfoCell.setText(LocaleController.getString("RestrictSavingContentInfoGroup", NUM));
        } else {
            this.saveRestrictInfoCell.setText(LocaleController.getString("RestrictSavingContentInfoChannel", NUM));
        }
        this.saveContainer.addView(this.saveRestrictInfoCell, LayoutHelper.createLinear(-1, -2));
        if (!this.isPrivate && this.currentChat.username != null) {
            this.ignoreTextChanges = true;
            this.usernameTextView.setText(this.currentChat.username);
            this.usernameTextView.setSelection(this.currentChat.username.length());
            this.ignoreTextChanges = false;
        }
        updatePrivatePublic();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3212lambda$createView$2$orgtelegramuiChatEditTypeActivity(View v) {
        if (!this.isPrivate) {
            this.isPrivate = true;
            updatePrivatePublic();
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3213lambda$createView$3$orgtelegramuiChatEditTypeActivity(View v) {
        if (this.isPrivate) {
            if (!this.canCreatePublic) {
                showPremiumIncreaseLimitDialog();
                return;
            }
            this.isPrivate = false;
            updatePrivatePublic();
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3214lambda$createView$4$orgtelegramuiChatEditTypeActivity(View v) {
        ManageLinksActivity fragment = new ManageLinksActivity(this.chatId, 0, 0);
        fragment.setInfo(this.info, this.invite);
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3215lambda$createView$5$orgtelegramuiChatEditTypeActivity(View v) {
        boolean z = !this.isSaveRestricted;
        this.isSaveRestricted = z;
        ((TextCheckCell) v).setChecked(z);
    }

    private void showPremiumIncreaseLimitDialog() {
        if (getParentActivity() != null) {
            LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(this, getParentActivity(), 2, this.currentAccount);
            limitReachedBottomSheet.parentIsChannel = this.isChannel;
            limitReachedBottomSheet.onSuccessRunnable = new ChatEditTypeActivity$$ExternalSyntheticLambda18(this);
            showDialog(limitReachedBottomSheet);
        }
    }

    /* renamed from: lambda$showPremiumIncreaseLimitDialog$6$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3227x8397f4f() {
        this.canCreatePublic = true;
        updatePrivatePublic();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = args[0];
            if (chatFull.id == this.chatId) {
                this.info = chatFull;
                this.invite = chatFull.exported_invite;
                updatePrivatePublic();
            }
        }
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        if (chatFull == null) {
            return;
        }
        if (chatFull.exported_invite != null) {
            this.invite = chatFull.exported_invite;
        } else {
            generateLink(false);
        }
    }

    /* access modifiers changed from: private */
    public void processDone() {
        if (this.currentChat.noforwards != this.isSaveRestricted) {
            MessagesController messagesController = getMessagesController();
            long j = this.chatId;
            TLRPC.Chat chat = this.currentChat;
            boolean z = this.isSaveRestricted;
            chat.noforwards = z;
            messagesController.toggleChatNoForwards(j, z);
        }
        if (trySetUsername() && tryUpdateJoinSettings()) {
            finishFragment();
        }
    }

    private boolean tryUpdateJoinSettings() {
        if (this.isChannel || this.joinContainer == null) {
            return true;
        }
        if (getParentActivity() == null) {
            return false;
        }
        if (!ChatObject.isChannel(this.currentChat) && (this.joinContainer.isJoinToSend || this.joinContainer.isJoinRequest)) {
            getMessagesController().convertToMegaGroup(getParentActivity(), this.chatId, this, new ChatEditTypeActivity$$ExternalSyntheticLambda5(this));
            return false;
        }
        if (this.currentChat.join_to_send != this.joinContainer.isJoinToSend) {
            MessagesController messagesController = getMessagesController();
            long j = this.chatId;
            TLRPC.Chat chat = this.currentChat;
            boolean z = this.joinContainer.isJoinToSend;
            chat.join_to_send = z;
            messagesController.toggleChatJoinToSend(j, z, (Runnable) null, (Runnable) null);
        }
        if (this.currentChat.join_request != this.joinContainer.isJoinRequest) {
            MessagesController messagesController2 = getMessagesController();
            long j2 = this.chatId;
            TLRPC.Chat chat2 = this.currentChat;
            boolean z2 = this.joinContainer.isJoinRequest;
            chat2.join_request = z2;
            messagesController2.toggleChatJoinRequest(j2, z2, (Runnable) null, (Runnable) null);
        }
        return true;
    }

    /* renamed from: lambda$tryUpdateJoinSettings$7$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3229x80b1b5de(long param) {
        if (param != 0) {
            this.chatId = param;
            this.currentChat = getMessagesController().getChat(Long.valueOf(param));
            processDone();
        }
    }

    private boolean trySetUsername() {
        if (getParentActivity() == null) {
            return false;
        }
        if (this.isPrivate || (((this.currentChat.username != null || this.usernameTextView.length() == 0) && (this.currentChat.username == null || this.currentChat.username.equalsIgnoreCase(this.usernameTextView.getText().toString()))) || this.usernameTextView.length() == 0 || this.lastNameAvailable)) {
            String newUserName = "";
            String oldUserName = this.currentChat.username != null ? this.currentChat.username : newUserName;
            if (!this.isPrivate) {
                newUserName = this.usernameTextView.getText().toString();
            }
            if (oldUserName.equals(newUserName)) {
                return true;
            }
            if (!ChatObject.isChannel(this.currentChat)) {
                getMessagesController().convertToMegaGroup(getParentActivity(), this.chatId, this, new ChatEditTypeActivity$$ExternalSyntheticLambda4(this));
                return false;
            }
            getMessagesController().updateChannelUserName(this.chatId, newUserName);
            this.currentChat.username = newUserName;
            return true;
        }
        Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
        if (v != null) {
            v.vibrate(200);
        }
        AndroidUtilities.shakeView(this.checkTextView, 2.0f, 0);
        return false;
    }

    /* renamed from: lambda$trySetUsername$8$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3228lambda$trySetUsername$8$orgtelegramuiChatEditTypeActivity(long param) {
        if (param != 0) {
            this.chatId = param;
            this.currentChat = getMessagesController().getChat(Long.valueOf(param));
            processDone();
        }
    }

    private void loadAdminedChannels() {
        if (!this.loadingAdminedChannels && this.adminnedChannelsLayout != null) {
            this.loadingAdminedChannels = true;
            updatePrivatePublic();
            getConnectionsManager().sendRequest(new TLRPC.TL_channels_getAdminedPublicChannels(), new ChatEditTypeActivity$$ExternalSyntheticLambda7(this));
        }
    }

    /* renamed from: lambda$loadAdminedChannels$14$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3223xe2cabbf9(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatEditTypeActivity$$ExternalSyntheticLambda1(this, response));
    }

    /* renamed from: lambda$loadAdminedChannels$13$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3222x81781f5a(TLObject response) {
        this.loadingAdminedChannels = false;
        if (response != null && getParentActivity() != null) {
            for (int a = 0; a < this.adminedChannelCells.size(); a++) {
                this.linearLayout.removeView(this.adminedChannelCells.get(a));
            }
            this.adminedChannelCells.clear();
            TLRPC.TL_messages_chats res = (TLRPC.TL_messages_chats) response;
            for (int a2 = 0; a2 < res.chats.size(); a2++) {
                AdminedChannelCell adminedChannelCell = new AdminedChannelCell(getParentActivity(), new ChatEditTypeActivity$$ExternalSyntheticLambda16(this), false, 0);
                TLRPC.Chat chat = (TLRPC.Chat) res.chats.get(a2);
                boolean z = true;
                if (a2 != res.chats.size() - 1) {
                    z = false;
                }
                adminedChannelCell.setChannel(chat, z);
                this.adminedChannelCells.add(adminedChannelCell);
                this.adminnedChannelsLayout.addView(adminedChannelCell, LayoutHelper.createLinear(-1, 72));
            }
            updatePrivatePublic();
        }
    }

    /* renamed from: lambda$loadAdminedChannels$12$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3221x202582bb(View view) {
        TLRPC.Chat channel = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", NUM));
        if (this.isChannel) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", NUM, getMessagesController().linkPrefix + "/" + channel.username, channel.title)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", NUM, getMessagesController().linkPrefix + "/" + channel.username, channel.title)));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new ChatEditTypeActivity$$ExternalSyntheticLambda0(this, channel));
        showDialog(builder.create());
    }

    /* renamed from: lambda$loadAdminedChannels$11$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3220xbed2e61c(TLRPC.Chat channel, DialogInterface dialogInterface, int i) {
        TLRPC.TL_channels_updateUsername req1 = new TLRPC.TL_channels_updateUsername();
        req1.channel = MessagesController.getInputChannel(channel);
        req1.username = "";
        getConnectionsManager().sendRequest(req1, new ChatEditTypeActivity$$ExternalSyntheticLambda6(this), 64);
    }

    /* renamed from: lambda$loadAdminedChannels$10$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3219x5d80497d(TLObject response1, TLRPC.TL_error error1) {
        if (response1 instanceof TLRPC.TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new ChatEditTypeActivity$$ExternalSyntheticLambda17(this));
        }
    }

    /* renamed from: lambda$loadAdminedChannels$9$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3224x6855e815() {
        this.canCreatePublic = true;
        if (this.usernameTextView.length() > 0) {
            checkUserName(this.usernameTextView.getText().toString());
        }
        updatePrivatePublic();
    }

    private void updatePrivatePublic() {
        String str;
        int i;
        String str2;
        int i2;
        if (this.sectionCell2 != null) {
            Drawable drawable = null;
            int i3 = 8;
            if (this.isPrivate || this.canCreatePublic || !getUserConfig().isPremium()) {
                this.typeInfoCell.setTag("windowBackgroundWhiteGrayText4");
                this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
                if (this.isForcePublic) {
                    this.sectionCell2.setVisibility(8);
                } else {
                    this.sectionCell2.setVisibility(0);
                }
                this.adminedInfoCell.setVisibility(8);
                TextInfoPrivacyCell textInfoPrivacyCell = this.typeInfoCell;
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell.getContext(), NUM, "windowBackgroundGrayShadow"));
                this.adminnedChannelsLayout.setVisibility(8);
                this.linkContainer.setVisibility(0);
                this.loadingAdminedCell.setVisibility(8);
                if (this.isChannel) {
                    TextInfoPrivacyCell textInfoPrivacyCell2 = this.typeInfoCell;
                    if (this.isPrivate) {
                        i2 = NUM;
                        str2 = "ChannelPrivateLinkHelp";
                    } else {
                        i2 = NUM;
                        str2 = "ChannelUsernameHelp";
                    }
                    textInfoPrivacyCell2.setText(LocaleController.getString(str2, i2));
                    this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", NUM) : LocaleController.getString("ChannelLinkTitle", NUM));
                } else {
                    TextInfoPrivacyCell textInfoPrivacyCell3 = this.typeInfoCell;
                    if (this.isPrivate) {
                        i = NUM;
                        str = "MegaPrivateLinkHelp";
                    } else {
                        i = NUM;
                        str = "MegaUsernameHelp";
                    }
                    textInfoPrivacyCell3.setText(LocaleController.getString(str, i));
                    this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", NUM) : LocaleController.getString("ChannelLinkTitle", NUM));
                }
                this.publicContainer.setVisibility(this.isPrivate ? 8 : 0);
                this.privateContainer.setVisibility(this.isPrivate ? 0 : 8);
                this.saveContainer.setVisibility(0);
                this.manageLinksTextView.setVisibility(0);
                this.manageLinksInfoCell.setVisibility(0);
                this.linkContainer.setPadding(0, 0, 0, this.isPrivate ? 0 : AndroidUtilities.dp(7.0f));
                LinkActionView linkActionView = this.permanentLinkView;
                TLRPC.TL_chatInviteExported tL_chatInviteExported = this.invite;
                linkActionView.setLink(tL_chatInviteExported != null ? tL_chatInviteExported.link : null);
                this.permanentLinkView.loadUsers(this.invite, this.chatId);
                TextInfoPrivacyCell textInfoPrivacyCell4 = this.checkTextView;
                textInfoPrivacyCell4.setVisibility((this.isPrivate || textInfoPrivacyCell4.length() == 0) ? 8 : 0);
                this.manageLinksInfoCell.setText(LocaleController.getString("ManageLinksInfoHelp", NUM));
                if (this.isPrivate) {
                    TextInfoPrivacyCell textInfoPrivacyCell5 = this.typeInfoCell;
                    textInfoPrivacyCell5.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell5.getContext(), NUM, "windowBackgroundGrayShadow"));
                    this.manageLinksInfoCell.setBackground(Theme.getThemedDrawable(this.typeInfoCell.getContext(), NUM, "windowBackgroundGrayShadow"));
                } else {
                    TextInfoPrivacyCell textInfoPrivacyCell6 = this.typeInfoCell;
                    if (this.checkTextView.getVisibility() != 0) {
                        drawable = Theme.getThemedDrawable(this.typeInfoCell.getContext(), NUM, "windowBackgroundGrayShadow");
                    }
                    textInfoPrivacyCell6.setBackgroundDrawable(drawable);
                }
            } else {
                this.typeInfoCell.setText(LocaleController.getString("ChangePublicLimitReached", NUM));
                this.typeInfoCell.setTag("windowBackgroundWhiteRedText4");
                this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                this.linkContainer.setVisibility(8);
                this.checkTextView.setVisibility(8);
                this.sectionCell2.setVisibility(8);
                this.adminedInfoCell.setVisibility(0);
                if (this.loadingAdminedChannels) {
                    this.loadingAdminedCell.setVisibility(0);
                    this.adminnedChannelsLayout.setVisibility(8);
                    this.typeInfoCell.setBackgroundDrawable(this.checkTextView.getVisibility() == 0 ? null : Theme.getThemedDrawable(this.typeInfoCell.getContext(), NUM, "windowBackgroundGrayShadow"));
                    this.adminedInfoCell.setBackgroundDrawable((Drawable) null);
                } else {
                    ShadowSectionCell shadowSectionCell = this.adminedInfoCell;
                    shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(shadowSectionCell.getContext(), NUM, "windowBackgroundGrayShadow"));
                    TextInfoPrivacyCell textInfoPrivacyCell7 = this.typeInfoCell;
                    textInfoPrivacyCell7.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell7.getContext(), NUM, "windowBackgroundGrayShadow"));
                    this.loadingAdminedCell.setVisibility(8);
                    this.adminnedChannelsLayout.setVisibility(0);
                }
            }
            this.radioButtonCell1.setChecked(!this.isPrivate, true);
            this.radioButtonCell2.setChecked(this.isPrivate, true);
            this.usernameTextView.clearFocus();
            JoinToSendSettingsView joinToSendSettingsView = this.joinContainer;
            if (joinToSendSettingsView != null) {
                if (!this.isChannel && !this.isPrivate) {
                    i3 = 0;
                }
                joinToSendSettingsView.setVisibility(i3);
            }
            checkDoneButton();
        }
    }

    /* access modifiers changed from: private */
    public void checkDoneButton() {
        if (this.isPrivate || this.usernameTextView.length() > 0) {
            this.doneButton.setEnabled(true);
            this.doneButton.setAlpha(1.0f);
            return;
        }
        this.doneButton.setEnabled(false);
        this.doneButton.setAlpha(0.5f);
    }

    /* access modifiers changed from: private */
    public boolean checkUserName(String name) {
        if (name == null || name.length() <= 0) {
            this.checkTextView.setVisibility(8);
        } else {
            this.checkTextView.setVisibility(0);
        }
        this.typeInfoCell.setBackgroundDrawable(this.checkTextView.getVisibility() == 0 ? null : Theme.getThemedDrawable(this.typeInfoCell.getContext(), NUM, "windowBackgroundGrayShadow"));
        Runnable runnable = this.checkRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                getConnectionsManager().cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        if (name != null) {
            if (name.startsWith("_") || name.endsWith("_")) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalid", NUM));
                this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
                return false;
            }
            int a = 0;
            while (a < name.length()) {
                char ch = name.charAt(a);
                if (a == 0 && ch >= '0' && ch <= '9') {
                    if (this.isChannel) {
                        this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", NUM));
                    } else {
                        this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumberMega", NUM));
                    }
                    this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
                    return false;
                } else if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalid", NUM));
                    this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
                    return false;
                } else {
                    a++;
                }
            }
        }
        if (name == null || name.length() < 5) {
            if (this.isChannel) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", NUM));
            } else {
                this.checkTextView.setText(LocaleController.getString("LinkInvalidShortMega", NUM));
            }
            this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
            return false;
        } else if (name.length() > 32) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", NUM));
            this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
            return false;
        } else {
            this.checkTextView.setText(LocaleController.getString("LinkChecking", NUM));
            this.checkTextView.setTextColor("windowBackgroundWhiteGrayText8");
            this.lastCheckName = name;
            ChatEditTypeActivity$$ExternalSyntheticLambda19 chatEditTypeActivity$$ExternalSyntheticLambda19 = new ChatEditTypeActivity$$ExternalSyntheticLambda19(this, name);
            this.checkRunnable = chatEditTypeActivity$$ExternalSyntheticLambda19;
            AndroidUtilities.runOnUIThread(chatEditTypeActivity$$ExternalSyntheticLambda19, 300);
            return true;
        }
    }

    /* renamed from: lambda$checkUserName$17$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3211lambda$checkUserName$17$orgtelegramuiChatEditTypeActivity(String name) {
        TLRPC.TL_channels_checkUsername req = new TLRPC.TL_channels_checkUsername();
        req.username = name;
        req.channel = getMessagesController().getInputChannel(this.chatId);
        this.checkReqId = getConnectionsManager().sendRequest(req, new ChatEditTypeActivity$$ExternalSyntheticLambda9(this, name), 2);
    }

    /* renamed from: lambda$checkUserName$16$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3210lambda$checkUserName$16$orgtelegramuiChatEditTypeActivity(String name, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatEditTypeActivity$$ExternalSyntheticLambda20(this, name, error, response));
    }

    /* renamed from: lambda$checkUserName$15$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3209lambda$checkUserName$15$orgtelegramuiChatEditTypeActivity(String name, TLRPC.TL_error error, TLObject response) {
        this.checkReqId = 0;
        String str = this.lastCheckName;
        if (str != null && str.equals(name)) {
            if (error != null || !(response instanceof TLRPC.TL_boolTrue)) {
                if (error == null || !error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                    this.checkTextView.setText(LocaleController.getString("LinkInUse", NUM));
                } else {
                    this.canCreatePublic = false;
                    showPremiumIncreaseLimitDialog();
                }
                this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
                this.lastNameAvailable = false;
                return;
            }
            this.checkTextView.setText(LocaleController.formatString("LinkAvailable", NUM, name));
            this.checkTextView.setTextColor("windowBackgroundWhiteGreenText");
            this.lastNameAvailable = true;
        }
    }

    /* access modifiers changed from: private */
    public void generateLink(boolean newRequest) {
        this.loadingInvite = true;
        TLRPC.TL_messages_exportChatInvite req = new TLRPC.TL_messages_exportChatInvite();
        req.legacy_revoke_permanent = true;
        req.peer = getMessagesController().getInputPeer(-this.chatId);
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new ChatEditTypeActivity$$ExternalSyntheticLambda10(this, newRequest)), this.classGuid);
    }

    /* renamed from: lambda$generateLink$19$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3217lambda$generateLink$19$orgtelegramuiChatEditTypeActivity(boolean newRequest, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatEditTypeActivity$$ExternalSyntheticLambda3(this, error, response, newRequest));
    }

    /* renamed from: lambda$generateLink$18$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3216lambda$generateLink$18$orgtelegramuiChatEditTypeActivity(TLRPC.TL_error error, TLObject response, boolean newRequest) {
        String str = null;
        if (error == null) {
            TLRPC.TL_chatInviteExported tL_chatInviteExported = (TLRPC.TL_chatInviteExported) response;
            this.invite = tL_chatInviteExported;
            TLRPC.ChatFull chatFull = this.info;
            if (chatFull != null) {
                chatFull.exported_invite = tL_chatInviteExported;
            }
            if (newRequest) {
                if (getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setMessage(LocaleController.getString("RevokeAlertNewLink", NUM));
                    builder.setTitle(LocaleController.getString("RevokeLink", NUM));
                    builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder.create());
                } else {
                    return;
                }
            }
        }
        this.loadingInvite = false;
        LinkActionView linkActionView = this.permanentLinkView;
        if (linkActionView != null) {
            TLRPC.TL_chatInviteExported tL_chatInviteExported2 = this.invite;
            if (tL_chatInviteExported2 != null) {
                str = tL_chatInviteExported2.link;
            }
            linkActionView.setLink(str);
            this.permanentLinkView.loadUsers(this.invite, this.chatId);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ChatEditTypeActivity$$ExternalSyntheticLambda12(this);
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.sectionCell2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.infoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.infoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription(this.textCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.textCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        themeDescriptions.add(new ThemeDescription(this.textCell2, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.textCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.linearLayoutTypeContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription((View) this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.headerCell2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.saveHeaderCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.saveRestrictCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.saveRestrictCell, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.saveRestrictCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        themeDescriptions.add(new ThemeDescription((View) this.saveRestrictCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.checkTextView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"));
        themeDescriptions.add(new ThemeDescription((View) this.checkTextView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText8"));
        themeDescriptions.add(new ThemeDescription((View) this.checkTextView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGreenText"));
        themeDescriptions.add(new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"));
        themeDescriptions.add(new ThemeDescription(this.manageLinksInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.manageLinksInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.manageLinksInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"));
        themeDescriptions.add(new ThemeDescription(this.saveRestrictInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.saveRestrictInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.saveRestrictInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"));
        themeDescriptions.add(new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription((View) this.loadingAdminedCell, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        themeDescriptions.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        themeDescriptions.add(new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"deleteButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, cellDelegate, "avatar_text"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "avatar_backgroundRed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundOrange"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundPink"));
        themeDescriptions.add(new ThemeDescription(this.manageLinksTextView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.manageLinksTextView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.manageLinksTextView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$20$org-telegram-ui-ChatEditTypeActivity  reason: not valid java name */
    public /* synthetic */ void m3218x86f4e52a() {
        LinearLayout linearLayout2 = this.adminnedChannelsLayout;
        if (linearLayout2 != null) {
            int count = linearLayout2.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.adminnedChannelsLayout.getChildAt(a);
                if (child instanceof AdminedChannelCell) {
                    ((AdminedChannelCell) child).update();
                }
            }
        }
        this.permanentLinkView.updateColors();
        this.manageLinksTextView.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        InviteLinkBottomSheet inviteLinkBottomSheet2 = this.inviteLinkBottomSheet;
        if (inviteLinkBottomSheet2 != null) {
            inviteLinkBottomSheet2.updateColors();
        }
    }
}
