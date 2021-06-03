package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_channels_checkUsername;
import org.telegram.tgnet.TLRPC$TL_channels_getAdminedPublicChannels;
import org.telegram.tgnet.TLRPC$TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_chats;
import org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite;
import org.telegram.tgnet.TLRPC$User;
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
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.InviteLinkBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkActionView;

public class ChatEditTypeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList<>();
    private ShadowSectionCell adminedInfoCell;
    private LinearLayout adminnedChannelsLayout;
    private boolean canCreatePublic = true;
    /* access modifiers changed from: private */
    public int chatId;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextInfoPrivacyCell checkTextView;
    /* access modifiers changed from: private */
    public TLRPC$Chat currentChat;
    private ActionBarMenuItem doneButton;
    private EditTextBoldCursor editText;
    private HeaderCell headerCell;
    private HeaderCell headerCell2;
    /* access modifiers changed from: private */
    public boolean ignoreTextChanges;
    /* access modifiers changed from: private */
    public TLRPC$ChatFull info;
    private TextInfoPrivacyCell infoCell;
    /* access modifiers changed from: private */
    public TLRPC$TL_chatInviteExported invite;
    /* access modifiers changed from: private */
    public InviteLinkBottomSheet inviteLinkBottomSheet;
    private boolean isChannel;
    private boolean isForcePublic;
    private boolean isPrivate;
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
    private ShadowSectionCell sectionCell2;
    private TextSettingsCell textCell;
    private TextSettingsCell textCell2;
    private TextInfoPrivacyCell typeInfoCell;
    /* access modifiers changed from: private */
    public EditTextBoldCursor usernameTextView;
    HashMap<Integer, TLRPC$User> usersMap = new HashMap<>();

    public ChatEditTypeActivity(int i, boolean z) {
        this.chatId = i;
        this.isForcePublic = z;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0048, code lost:
        if (r0 == null) goto L_0x004a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onFragmentCreate() {
        /*
            r9 = this;
            org.telegram.messenger.MessagesController r0 = r9.getMessagesController()
            int r1 = r9.chatId
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            r9.currentChat = r0
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x004b
            org.telegram.messenger.MessagesStorage r0 = r9.getMessagesStorage()
            int r3 = r9.chatId
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChatSync(r3)
            r9.currentChat = r0
            if (r0 == 0) goto L_0x004a
            org.telegram.messenger.MessagesController r0 = r9.getMessagesController()
            org.telegram.tgnet.TLRPC$Chat r3 = r9.currentChat
            r0.putChat(r3, r2)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r9.info
            if (r0 != 0) goto L_0x004b
            org.telegram.messenger.MessagesStorage r3 = r9.getMessagesStorage()
            int r4 = r9.chatId
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r0)
            java.util.concurrent.CountDownLatch r6 = new java.util.concurrent.CountDownLatch
            r6.<init>(r2)
            r7 = 0
            r8 = 0
            org.telegram.tgnet.TLRPC$ChatFull r0 = r3.loadChatInfo(r4, r5, r6, r7, r8)
            r9.info = r0
            if (r0 != 0) goto L_0x004b
        L_0x004a:
            return r1
        L_0x004b:
            boolean r0 = r9.isForcePublic
            if (r0 != 0) goto L_0x005b
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            java.lang.String r0 = r0.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x005b
            r0 = 1
            goto L_0x005c
        L_0x005b:
            r0 = 0
        L_0x005c:
            r9.isPrivate = r0
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x006d
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x006d
            r1 = 1
        L_0x006d:
            r9.isChannel = r1
            boolean r0 = r9.isForcePublic
            if (r0 == 0) goto L_0x007d
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            java.lang.String r0 = r0.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0087
        L_0x007d:
            boolean r0 = r9.isPrivate
            if (r0 == 0) goto L_0x00a3
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = r0.creator
            if (r0 == 0) goto L_0x00a3
        L_0x0087:
            org.telegram.tgnet.TLRPC$TL_channels_checkUsername r0 = new org.telegram.tgnet.TLRPC$TL_channels_checkUsername
            r0.<init>()
            java.lang.String r1 = "1"
            r0.username = r1
            org.telegram.tgnet.TLRPC$TL_inputChannelEmpty r1 = new org.telegram.tgnet.TLRPC$TL_inputChannelEmpty
            r1.<init>()
            r0.channel = r1
            org.telegram.tgnet.ConnectionsManager r1 = r9.getConnectionsManager()
            org.telegram.ui.-$$Lambda$ChatEditTypeActivity$D-mmdVYSONU5EQU5x4KjgeB4t7k r3 = new org.telegram.ui.-$$Lambda$ChatEditTypeActivity$D-mmdVYSONU5EQU5x4KjgeB4t7k
            r3.<init>()
            r1.sendRequest(r0, r3)
        L_0x00a3:
            boolean r0 = r9.isPrivate
            if (r0 == 0) goto L_0x00b6
            org.telegram.tgnet.TLRPC$ChatFull r0 = r9.info
            if (r0 == 0) goto L_0x00b6
            org.telegram.messenger.MessagesController r0 = r9.getMessagesController()
            int r1 = r9.chatId
            int r3 = r9.classGuid
            r0.loadFullChat(r1, r3, r2)
        L_0x00b6:
            org.telegram.messenger.NotificationCenter r0 = r9.getNotificationCenter()
            int r1 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad
            r0.addObserver(r9, r1)
            boolean r0 = super.onFragmentCreate()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditTypeActivity.onFragmentCreate():boolean");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFragmentCreate$1 */
    public /* synthetic */ void lambda$onFragmentCreate$1$ChatEditTypeActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            public final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ChatEditTypeActivity.this.lambda$onFragmentCreate$0$ChatEditTypeActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFragmentCreate$0 */
    public /* synthetic */ void lambda$onFragmentCreate$0$ChatEditTypeActivity(TLRPC$TL_error tLRPC$TL_error) {
        boolean z = tLRPC$TL_error == null || !tLRPC$TL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH");
        this.canCreatePublic = z;
        if (!z) {
            loadAdminedChannels();
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        TLRPC$ChatFull tLRPC$ChatFull;
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        TextSettingsCell textSettingsCell = this.textCell2;
        if (!(textSettingsCell == null || (tLRPC$ChatFull = this.info) == null)) {
            if (tLRPC$ChatFull.stickerset != null) {
                textSettingsCell.setTextAndValue(LocaleController.getString("GroupStickers", NUM), this.info.stickerset.title, false);
            } else {
                textSettingsCell.setText(LocaleController.getString("GroupStickers", NUM), false);
            }
        }
        TLRPC$ChatFull tLRPC$ChatFull2 = this.info;
        if (tLRPC$ChatFull2 != null) {
            TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = tLRPC$ChatFull2.exported_invite;
            this.invite = tLRPC$TL_chatInviteExported;
            this.permanentLinkView.setLink(tLRPC$TL_chatInviteExported == null ? null : tLRPC$TL_chatInviteExported.link);
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
        String str;
        final Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ChatEditTypeActivity.this.finishFragment();
                } else if (i == 1) {
                    ChatEditTypeActivity.this.processDone();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
        AnonymousClass2 r0 = new ScrollView(context2) {
            public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                rect.bottom += AndroidUtilities.dp(60.0f);
                return super.requestChildRectangleOnScreen(view, rect, z);
            }
        };
        this.fragmentView = r0;
        r0.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
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
        this.radioButtonCell2.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditTypeActivity.this.lambda$createView$2$ChatEditTypeActivity(view);
            }
        });
        RadioButtonCell radioButtonCell3 = new RadioButtonCell(context2);
        this.radioButtonCell1 = radioButtonCell3;
        radioButtonCell3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (this.isChannel) {
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", NUM), LocaleController.getString("ChannelPublicInfo", NUM), false, !this.isPrivate);
        } else {
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("MegaPublic", NUM), LocaleController.getString("MegaPublicInfo", NUM), false, !this.isPrivate);
        }
        this.linearLayoutTypeContainer.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell1.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditTypeActivity.this.lambda$createView$3$ChatEditTypeActivity(view);
            }
        });
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
        EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context2);
        this.usernameTextView = editTextBoldCursor2;
        editTextBoldCursor2.setTextSize(1, 18.0f);
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
        LinkActionView linkActionView = r0;
        LinkActionView linkActionView2 = new LinkActionView(context, this, (BottomSheet) null, this.chatId, true, ChatObject.isChannel(this.currentChat));
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
                TLRPC$TL_chatInviteExported access$700 = ChatEditTypeActivity.this.invite;
                TLRPC$ChatFull access$800 = ChatEditTypeActivity.this.info;
                ChatEditTypeActivity chatEditTypeActivity2 = ChatEditTypeActivity.this;
                InviteLinkBottomSheet unused = chatEditTypeActivity.inviteLinkBottomSheet = new InviteLinkBottomSheet(context, access$700, access$800, chatEditTypeActivity2.usersMap, chatEditTypeActivity2, chatEditTypeActivity2.chatId, true, ChatObject.isChannel(ChatEditTypeActivity.this.currentChat));
                ChatEditTypeActivity.this.inviteLinkBottomSheet.show();
            }
        });
        this.permanentLinkView.setUsers(0, (ArrayList<TLRPC$User>) null);
        this.privateContainer.addView(this.permanentLinkView);
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
        this.checkTextView = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        this.checkTextView.setBottomPadding(6);
        this.linearLayout.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2));
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
        this.typeInfoCell = textInfoPrivacyCell2;
        this.linearLayout.addView(textInfoPrivacyCell2, LayoutHelper.createLinear(-1, -2));
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
        this.manageLinksTextView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditTypeActivity.this.lambda$createView$4$ChatEditTypeActivity(view);
            }
        });
        this.manageLinksTextView.setTextAndIcon(LocaleController.getString("ManageInviteLinks", NUM), NUM, false);
        this.manageLinksTextView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditTypeActivity.this.lambda$createView$5$ChatEditTypeActivity(view);
            }
        });
        this.linearLayout.addView(this.manageLinksTextView, LayoutHelper.createLinear(-1, -2));
        TextInfoPrivacyCell textInfoPrivacyCell3 = new TextInfoPrivacyCell(context2);
        this.manageLinksInfoCell = textInfoPrivacyCell3;
        this.linearLayout.addView(textInfoPrivacyCell3, LayoutHelper.createLinear(-1, -2));
        if (!this.isPrivate && (str = this.currentChat.username) != null) {
            this.ignoreTextChanges = true;
            this.usernameTextView.setText(str);
            this.usernameTextView.setSelection(this.currentChat.username.length());
            this.ignoreTextChanges = false;
        }
        updatePrivatePublic();
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$2 */
    public /* synthetic */ void lambda$createView$2$ChatEditTypeActivity(View view) {
        if (!this.isPrivate) {
            this.isPrivate = true;
            updatePrivatePublic();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$3 */
    public /* synthetic */ void lambda$createView$3$ChatEditTypeActivity(View view) {
        if (this.isPrivate) {
            this.isPrivate = false;
            updatePrivatePublic();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$4 */
    public /* synthetic */ void lambda$createView$4$ChatEditTypeActivity(View view) {
        if (this.invite == null) {
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$5 */
    public /* synthetic */ void lambda$createView$5$ChatEditTypeActivity(View view) {
        ManageLinksActivity manageLinksActivity = new ManageLinksActivity(this.chatId, 0, 0);
        manageLinksActivity.setInfo(this.info, this.invite);
        presentFragment(manageLinksActivity);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC$ChatFull tLRPC$ChatFull = objArr[0];
            if (tLRPC$ChatFull.id == this.chatId) {
                this.info = tLRPC$ChatFull;
                this.invite = tLRPC$ChatFull.exported_invite;
                updatePrivatePublic();
            }
        }
    }

    public void setInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
        if (tLRPC$ChatFull != null) {
            TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = tLRPC$ChatFull.exported_invite;
            if (tLRPC$TL_chatInviteExported != null) {
                this.invite = tLRPC$TL_chatInviteExported;
            } else {
                generateLink(false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void processDone() {
        if (trySetUsername()) {
            finishFragment();
        }
    }

    private boolean trySetUsername() {
        String str;
        if (getParentActivity() == null) {
            return false;
        }
        if (this.isPrivate || (((this.currentChat.username != null || this.usernameTextView.length() == 0) && ((str = this.currentChat.username) == null || str.equalsIgnoreCase(this.usernameTextView.getText().toString()))) || this.usernameTextView.length() == 0 || this.lastNameAvailable)) {
            String str2 = this.currentChat.username;
            String str3 = "";
            if (str2 == null) {
                str2 = str3;
            }
            if (!this.isPrivate) {
                str3 = this.usernameTextView.getText().toString();
            }
            if (str2.equals(str3)) {
                return true;
            }
            if (!ChatObject.isChannel(this.currentChat)) {
                getMessagesController().convertToMegaGroup(getParentActivity(), this.chatId, this, new MessagesStorage.IntCallback() {
                    public final void run(int i) {
                        ChatEditTypeActivity.this.lambda$trySetUsername$6$ChatEditTypeActivity(i);
                    }
                });
                return false;
            }
            getMessagesController().updateChannelUserName(this.chatId, str3);
            this.currentChat.username = str3;
            return true;
        }
        Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
        AndroidUtilities.shakeView(this.checkTextView, 2.0f, 0);
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$trySetUsername$6 */
    public /* synthetic */ void lambda$trySetUsername$6$ChatEditTypeActivity(int i) {
        if (i != 0) {
            this.chatId = i;
            this.currentChat = getMessagesController().getChat(Integer.valueOf(i));
            processDone();
        }
    }

    private void loadAdminedChannels() {
        if (!this.loadingAdminedChannels && this.adminnedChannelsLayout != null) {
            this.loadingAdminedChannels = true;
            updatePrivatePublic();
            getConnectionsManager().sendRequest(new TLRPC$TL_channels_getAdminedPublicChannels(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChatEditTypeActivity.this.lambda$loadAdminedChannels$12$ChatEditTypeActivity(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAdminedChannels$12 */
    public /* synthetic */ void lambda$loadAdminedChannels$12$ChatEditTypeActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            public final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ChatEditTypeActivity.this.lambda$loadAdminedChannels$11$ChatEditTypeActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAdminedChannels$11 */
    public /* synthetic */ void lambda$loadAdminedChannels$11$ChatEditTypeActivity(TLObject tLObject) {
        this.loadingAdminedChannels = false;
        if (tLObject != null && getParentActivity() != null) {
            for (int i = 0; i < this.adminedChannelCells.size(); i++) {
                this.linearLayout.removeView(this.adminedChannelCells.get(i));
            }
            this.adminedChannelCells.clear();
            TLRPC$TL_messages_chats tLRPC$TL_messages_chats = (TLRPC$TL_messages_chats) tLObject;
            for (int i2 = 0; i2 < tLRPC$TL_messages_chats.chats.size(); i2++) {
                AdminedChannelCell adminedChannelCell = new AdminedChannelCell(getParentActivity(), new View.OnClickListener() {
                    public final void onClick(View view) {
                        ChatEditTypeActivity.this.lambda$loadAdminedChannels$10$ChatEditTypeActivity(view);
                    }
                });
                TLRPC$Chat tLRPC$Chat = tLRPC$TL_messages_chats.chats.get(i2);
                boolean z = true;
                if (i2 != tLRPC$TL_messages_chats.chats.size() - 1) {
                    z = false;
                }
                adminedChannelCell.setChannel(tLRPC$Chat, z);
                this.adminedChannelCells.add(adminedChannelCell);
                this.adminnedChannelsLayout.addView(adminedChannelCell, LayoutHelper.createLinear(-1, 72));
            }
            updatePrivatePublic();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAdminedChannels$10 */
    public /* synthetic */ void lambda$loadAdminedChannels$10$ChatEditTypeActivity(View view) {
        TLRPC$Chat currentChannel = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", NUM));
        if (this.isChannel) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", NUM, getMessagesController().linkPrefix + "/" + currentChannel.username, currentChannel.title)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", NUM, getMessagesController().linkPrefix + "/" + currentChannel.username, currentChannel.title)));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new DialogInterface.OnClickListener(currentChannel) {
            public final /* synthetic */ TLRPC$Chat f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatEditTypeActivity.this.lambda$loadAdminedChannels$9$ChatEditTypeActivity(this.f$1, dialogInterface, i);
            }
        });
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAdminedChannels$9 */
    public /* synthetic */ void lambda$loadAdminedChannels$9$ChatEditTypeActivity(TLRPC$Chat tLRPC$Chat, DialogInterface dialogInterface, int i) {
        TLRPC$TL_channels_updateUsername tLRPC$TL_channels_updateUsername = new TLRPC$TL_channels_updateUsername();
        tLRPC$TL_channels_updateUsername.channel = MessagesController.getInputChannel(tLRPC$Chat);
        tLRPC$TL_channels_updateUsername.username = "";
        getConnectionsManager().sendRequest(tLRPC$TL_channels_updateUsername, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChatEditTypeActivity.this.lambda$loadAdminedChannels$8$ChatEditTypeActivity(tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAdminedChannels$8 */
    public /* synthetic */ void lambda$loadAdminedChannels$8$ChatEditTypeActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ChatEditTypeActivity.this.lambda$loadAdminedChannels$7$ChatEditTypeActivity();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAdminedChannels$7 */
    public /* synthetic */ void lambda$loadAdminedChannels$7$ChatEditTypeActivity() {
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
            if (this.isPrivate || this.canCreatePublic) {
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
                this.manageLinksTextView.setVisibility(0);
                this.manageLinksInfoCell.setVisibility(0);
                this.linkContainer.setPadding(0, 0, 0, this.isPrivate ? 0 : AndroidUtilities.dp(7.0f));
                LinkActionView linkActionView = this.permanentLinkView;
                TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = this.invite;
                linkActionView.setLink(tLRPC$TL_chatInviteExported != null ? tLRPC$TL_chatInviteExported.link : null);
                this.permanentLinkView.loadUsers(this.invite, this.chatId);
                TextInfoPrivacyCell textInfoPrivacyCell4 = this.checkTextView;
                if (!this.isPrivate && textInfoPrivacyCell4.length() != 0) {
                    i3 = 0;
                }
                textInfoPrivacyCell4.setVisibility(i3);
                if (this.isPrivate) {
                    TextInfoPrivacyCell textInfoPrivacyCell5 = this.typeInfoCell;
                    textInfoPrivacyCell5.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell5.getContext(), NUM, "windowBackgroundGrayShadow"));
                    this.manageLinksInfoCell.setBackground(Theme.getThemedDrawable(this.typeInfoCell.getContext(), NUM, "windowBackgroundGrayShadow"));
                    this.manageLinksInfoCell.setText(LocaleController.getString("ManageLinksInfoHelp", NUM));
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
    public boolean checkUserName(String str) {
        if (str == null || str.length() <= 0) {
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
        if (str != null) {
            if (str.startsWith("_") || str.endsWith("_")) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalid", NUM));
                this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
                return false;
            }
            int i = 0;
            while (i < str.length()) {
                char charAt = str.charAt(i);
                if (i == 0 && charAt >= '0' && charAt <= '9') {
                    if (this.isChannel) {
                        this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", NUM));
                    } else {
                        this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumberMega", NUM));
                    }
                    this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
                    return false;
                } else if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && charAt != '_'))) {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalid", NUM));
                    this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
                    return false;
                } else {
                    i++;
                }
            }
        }
        if (str == null || str.length() < 5) {
            if (this.isChannel) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", NUM));
            } else {
                this.checkTextView.setText(LocaleController.getString("LinkInvalidShortMega", NUM));
            }
            this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
            return false;
        } else if (str.length() > 32) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", NUM));
            this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
            return false;
        } else {
            this.checkTextView.setText(LocaleController.getString("LinkChecking", NUM));
            this.checkTextView.setTextColor("windowBackgroundWhiteGrayText8");
            this.lastCheckName = str;
            $$Lambda$ChatEditTypeActivity$g1WvU72dCCNnQt9Q_4IuDs84k r0 = new Runnable(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChatEditTypeActivity.this.lambda$checkUserName$15$ChatEditTypeActivity(this.f$1);
                }
            };
            this.checkRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 300);
            return true;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkUserName$15 */
    public /* synthetic */ void lambda$checkUserName$15$ChatEditTypeActivity(String str) {
        TLRPC$TL_channels_checkUsername tLRPC$TL_channels_checkUsername = new TLRPC$TL_channels_checkUsername();
        tLRPC$TL_channels_checkUsername.username = str;
        tLRPC$TL_channels_checkUsername.channel = getMessagesController().getInputChannel(this.chatId);
        this.checkReqId = getConnectionsManager().sendRequest(tLRPC$TL_channels_checkUsername, new RequestDelegate(str) {
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChatEditTypeActivity.this.lambda$checkUserName$14$ChatEditTypeActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkUserName$14 */
    public /* synthetic */ void lambda$checkUserName$14$ChatEditTypeActivity(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str, tLRPC$TL_error, tLObject) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;
            public final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChatEditTypeActivity.this.lambda$checkUserName$13$ChatEditTypeActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkUserName$13 */
    public /* synthetic */ void lambda$checkUserName$13$ChatEditTypeActivity(String str, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        this.checkReqId = 0;
        String str2 = this.lastCheckName;
        if (str2 != null && str2.equals(str)) {
            if (tLRPC$TL_error != null || !(tLObject instanceof TLRPC$TL_boolTrue)) {
                if (tLRPC$TL_error == null || !tLRPC$TL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                    this.checkTextView.setText(LocaleController.getString("LinkInUse", NUM));
                } else {
                    this.canCreatePublic = false;
                    loadAdminedChannels();
                }
                this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
                this.lastNameAvailable = false;
                return;
            }
            this.checkTextView.setText(LocaleController.formatString("LinkAvailable", NUM, str));
            this.checkTextView.setTextColor("windowBackgroundWhiteGreenText");
            this.lastNameAvailable = true;
        }
    }

    /* access modifiers changed from: private */
    public void generateLink(boolean z) {
        this.loadingInvite = true;
        TLRPC$TL_messages_exportChatInvite tLRPC$TL_messages_exportChatInvite = new TLRPC$TL_messages_exportChatInvite();
        tLRPC$TL_messages_exportChatInvite.legacy_revoke_permanent = true;
        tLRPC$TL_messages_exportChatInvite.peer = getMessagesController().getInputPeer(-this.chatId);
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_exportChatInvite, new RequestDelegate(z) {
            public final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChatEditTypeActivity.this.lambda$generateLink$17$ChatEditTypeActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        }), this.classGuid);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$generateLink$17 */
    public /* synthetic */ void lambda$generateLink$17$ChatEditTypeActivity(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, z) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChatEditTypeActivity.this.lambda$generateLink$16$ChatEditTypeActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$generateLink$16 */
    public /* synthetic */ void lambda$generateLink$16$ChatEditTypeActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        String str = null;
        if (tLRPC$TL_error == null) {
            TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = (TLRPC$TL_chatInviteExported) tLObject;
            this.invite = tLRPC$TL_chatInviteExported;
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            if (tLRPC$ChatFull != null) {
                tLRPC$ChatFull.exported_invite = tLRPC$TL_chatInviteExported;
            }
            if (z) {
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
            TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported2 = this.invite;
            if (tLRPC$TL_chatInviteExported2 != null) {
                str = tLRPC$TL_chatInviteExported2.link;
            }
            linkActionView.setLink(str);
            this.permanentLinkView.loadUsers(this.invite, this.chatId);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$ChatEditTypeActivity$jiT1XCon7noHCVcWJTzVTj8uiyA r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ChatEditTypeActivity.this.lambda$getThemeDescriptions$18$ChatEditTypeActivity();
            }
        };
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.sectionCell2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.infoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.infoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.textCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.textCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription(this.textCell2, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.textCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.linearLayoutTypeContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.headerCell2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription((View) this.checkTextView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"));
        arrayList.add(new ThemeDescription((View) this.checkTextView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText8"));
        arrayList.add(new ThemeDescription((View) this.checkTextView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGreenText"));
        arrayList.add(new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"));
        arrayList.add(new ThemeDescription(this.manageLinksInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.manageLinksInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.manageLinksInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"));
        arrayList.add(new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.loadingAdminedCell, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
        arrayList.add(new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        arrayList.add(new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
        arrayList.add(new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        arrayList.add(new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        arrayList.add(new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"deleteButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        $$Lambda$ChatEditTypeActivity$jiT1XCon7noHCVcWJTzVTj8uiyA r8 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, r8, "avatar_text"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.manageLinksTextView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.manageLinksTextView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.manageLinksTextView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getThemeDescriptions$18 */
    public /* synthetic */ void lambda$getThemeDescriptions$18$ChatEditTypeActivity() {
        LinearLayout linearLayout2 = this.adminnedChannelsLayout;
        if (linearLayout2 != null) {
            int childCount = linearLayout2.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.adminnedChannelsLayout.getChildAt(i);
                if (childAt instanceof AdminedChannelCell) {
                    ((AdminedChannelCell) childAt).update();
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
