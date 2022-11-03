package org.telegram.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_channels_checkUsername;
import org.telegram.tgnet.TLRPC$TL_channels_deactivateAllUsernames;
import org.telegram.tgnet.TLRPC$TL_channels_getAdminedPublicChannels;
import org.telegram.tgnet.TLRPC$TL_channels_reorderUsernames;
import org.telegram.tgnet.TLRPC$TL_channels_toggleUsername;
import org.telegram.tgnet.TLRPC$TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputChannel;
import org.telegram.tgnet.TLRPC$TL_messages_chats;
import org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite;
import org.telegram.tgnet.TLRPC$TL_username;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
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
import org.telegram.ui.ChangeUsernameActivity;
import org.telegram.ui.ChatEditTypeActivity;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.InviteLinkBottomSheet;
import org.telegram.ui.Components.JoinToSendSettingsView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkActionView;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes3.dex */
public class ChatEditTypeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ShadowSectionCell adminedInfoCell;
    private LinearLayout adminnedChannelsLayout;
    private long chatId;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextInfoPrivacyCell checkTextView;
    private TLRPC$Chat currentChat;
    private ActionBarMenuItem doneButton;
    private CrossfadeDrawable doneButtonDrawable;
    private ValueAnimator doneButtonDrawableAnimator;
    private EditTextBoldCursor editText;
    private ChangeUsernameActivity.UsernameCell editableUsernameCell;
    private Boolean editableUsernameUpdated;
    private Boolean editableUsernameWasActive;
    private HeaderCell headerCell;
    private HeaderCell headerCell2;
    private boolean ignoreScroll;
    private boolean ignoreTextChanges;
    private TLRPC$ChatFull info;
    private TextInfoPrivacyCell infoCell;
    private TLRPC$TL_chatInviteExported invite;
    private InviteLinkBottomSheet inviteLinkBottomSheet;
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
    private EditTextBoldCursor usernameTextView;
    private UsernamesListView usernamesListView;
    private ArrayList<TLRPC$TL_username> editableUsernames = new ArrayList<>();
    private ArrayList<TLRPC$TL_username> usernames = new ArrayList<>();
    private boolean canCreatePublic = true;
    private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList<>();
    HashMap<Long, TLRPC$User> usersMap = new HashMap<>();
    private Runnable enableDoneLoading = new Runnable() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda9
        @Override // java.lang.Runnable
        public final void run() {
            ChatEditTypeActivity.this.lambda$new$6();
        }
    };
    private boolean deactivatingLinks = false;

    public ChatEditTypeActivity(long j, boolean z) {
        this.chatId = j;
        this.isForcePublic = z;
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x0048, code lost:
        if (r0 == null) goto L9;
     */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onFragmentCreate() {
        /*
            Method dump skipped, instructions count: 297
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditTypeActivity.onFragmentCreate():boolean");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFragmentCreate$1(TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                ChatEditTypeActivity.this.lambda$onFragmentCreate$0(tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFragmentCreate$0(TLRPC$TL_error tLRPC$TL_error) {
        boolean z = tLRPC$TL_error == null || !tLRPC$TL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH");
        this.canCreatePublic = z;
        if (z || !getUserConfig().isPremium()) {
            return;
        }
        loadAdminedChannels();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        TLRPC$ChatFull tLRPC$ChatFull;
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        TextSettingsCell textSettingsCell = this.textCell2;
        if (textSettingsCell != null && (tLRPC$ChatFull = this.info) != null) {
            if (tLRPC$ChatFull.stickerset != null) {
                textSettingsCell.setTextAndValue(LocaleController.getString("GroupStickers", R.string.GroupStickers), this.info.stickerset.title, false);
            } else {
                textSettingsCell.setText(LocaleController.getString("GroupStickers", R.string.GroupStickers), false);
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyVisible() {
        EditTextBoldCursor editTextBoldCursor;
        super.onBecomeFullyVisible();
        if (!this.isForcePublic || (editTextBoldCursor = this.usernameTextView) == null) {
            return;
        }
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(this.usernameTextView);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ChatEditTypeActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1) {
                    ChatEditTypeActivity.this.finishFragment();
                } else if (i != 1) {
                } else {
                    ChatEditTypeActivity.this.processDone();
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        Drawable mutate = context.getResources().getDrawable(R.drawable.ic_ab_done).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultIcon"), PorterDuff.Mode.MULTIPLY));
        CrossfadeDrawable crossfadeDrawable = new CrossfadeDrawable(mutate, new CircularProgressDrawable(Theme.getColor("actionBarDefaultIcon")));
        this.doneButtonDrawable = crossfadeDrawable;
        this.doneButton = createMenu.addItemWithWidth(1, crossfadeDrawable, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", R.string.Done));
        ScrollView scrollView = new ScrollView(context) { // from class: org.telegram.ui.ChatEditTypeActivity.2
            @Override // android.widget.ScrollView, android.view.ViewGroup, android.view.ViewParent
            public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                rect.bottom += AndroidUtilities.dp(60.0f);
                return super.requestChildRectangleOnScreen(view, rect, z);
            }

            @Override // android.widget.ScrollView, android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0) {
                    return super.onTouchEvent(motionEvent);
                }
                return !ChatEditTypeActivity.this.ignoreScroll && super.onTouchEvent(motionEvent);
            }

            @Override // android.widget.ScrollView, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return !ChatEditTypeActivity.this.ignoreScroll && super.onInterceptTouchEvent(motionEvent);
            }
        };
        this.fragmentView = scrollView;
        scrollView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        ScrollView scrollView2 = (ScrollView) this.fragmentView;
        scrollView2.setFillViewport(true);
        LinearLayout linearLayout = new LinearLayout(context);
        this.linearLayout = linearLayout;
        scrollView2.addView(linearLayout, new FrameLayout.LayoutParams(-1, -2));
        this.linearLayout.setOrientation(1);
        if (this.isForcePublic) {
            this.actionBar.setTitle(LocaleController.getString("TypeLocationGroup", R.string.TypeLocationGroup));
        } else if (this.isChannel) {
            this.actionBar.setTitle(LocaleController.getString("ChannelSettingsTitle", R.string.ChannelSettingsTitle));
        } else {
            this.actionBar.setTitle(LocaleController.getString("GroupSettingsTitle", R.string.GroupSettingsTitle));
        }
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.linearLayoutTypeContainer = linearLayout2;
        linearLayout2.setOrientation(1);
        this.linearLayoutTypeContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout.addView(this.linearLayoutTypeContainer, LayoutHelper.createLinear(-1, -2));
        HeaderCell headerCell = new HeaderCell(context, 23);
        this.headerCell2 = headerCell;
        headerCell.setHeight(46);
        if (this.isChannel) {
            this.headerCell2.setText(LocaleController.getString("ChannelTypeHeader", R.string.ChannelTypeHeader));
        } else {
            this.headerCell2.setText(LocaleController.getString("GroupTypeHeader", R.string.GroupTypeHeader));
        }
        this.linearLayoutTypeContainer.addView(this.headerCell2);
        RadioButtonCell radioButtonCell = new RadioButtonCell(context);
        this.radioButtonCell2 = radioButtonCell;
        radioButtonCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (this.isChannel) {
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate), LocaleController.getString("ChannelPrivateInfo", R.string.ChannelPrivateInfo), false, this.isPrivate);
        } else {
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("MegaPrivate", R.string.MegaPrivate), LocaleController.getString("MegaPrivateInfo", R.string.MegaPrivateInfo), false, this.isPrivate);
        }
        this.linearLayoutTypeContainer.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatEditTypeActivity.this.lambda$createView$2(view);
            }
        });
        RadioButtonCell radioButtonCell2 = new RadioButtonCell(context);
        this.radioButtonCell1 = radioButtonCell2;
        radioButtonCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (this.isChannel) {
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", R.string.ChannelPublic), LocaleController.getString("ChannelPublicInfo", R.string.ChannelPublicInfo), false, !this.isPrivate);
        } else {
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("MegaPublic", R.string.MegaPublic), LocaleController.getString("MegaPublicInfo", R.string.MegaPublicInfo), false, !this.isPrivate);
        }
        this.linearLayoutTypeContainer.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell1.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatEditTypeActivity.this.lambda$createView$3(view);
            }
        });
        ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context);
        this.sectionCell2 = shadowSectionCell;
        this.linearLayout.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
        if (this.isForcePublic) {
            this.radioButtonCell2.setVisibility(8);
            this.radioButtonCell1.setVisibility(8);
            this.sectionCell2.setVisibility(8);
            this.headerCell2.setVisibility(8);
        }
        LinearLayout linearLayout3 = new LinearLayout(context);
        this.linkContainer = linearLayout3;
        linearLayout3.setOrientation(1);
        this.linkContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
        HeaderCell headerCell2 = new HeaderCell(context, 23);
        this.headerCell = headerCell2;
        this.linkContainer.addView(headerCell2);
        LinearLayout linearLayout4 = new LinearLayout(context);
        this.publicContainer = linearLayout4;
        linearLayout4.setOrientation(0);
        this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 23.0f, 7.0f, 23.0f, 0.0f));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        this.editText = editTextBoldCursor;
        editTextBoldCursor.setText(getMessagesController().linkPrefix + "/");
        this.editText.setTextSize(1, 18.0f);
        this.editText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.editText.setMaxLines(1);
        this.editText.setLines(1);
        this.editText.setEnabled(false);
        this.editText.setBackgroundDrawable(null);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setSingleLine(true);
        this.editText.setInputType(163840);
        this.editText.setImeOptions(6);
        this.publicContainer.addView(this.editText, LayoutHelper.createLinear(-2, 36));
        EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context) { // from class: org.telegram.ui.ChatEditTypeActivity.3
            @Override // org.telegram.ui.Components.EditTextBoldCursor, android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                StringBuilder sb = new StringBuilder();
                sb.append((CharSequence) getText());
                if (ChatEditTypeActivity.this.checkTextView != null && ChatEditTypeActivity.this.checkTextView.getTextView() != null && !TextUtils.isEmpty(ChatEditTypeActivity.this.checkTextView.getTextView().getText())) {
                    sb.append("\n");
                    sb.append(ChatEditTypeActivity.this.checkTextView.getTextView().getText());
                }
                accessibilityNodeInfo.setText(sb);
            }
        };
        this.usernameTextView = editTextBoldCursor2;
        editTextBoldCursor2.setTextSize(1, 18.0f);
        this.usernameTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.usernameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.usernameTextView.setMaxLines(1);
        this.usernameTextView.setLines(1);
        this.usernameTextView.setBackgroundDrawable(null);
        this.usernameTextView.setPadding(0, 0, 0, 0);
        this.usernameTextView.setSingleLine(true);
        this.usernameTextView.setInputType(163872);
        this.usernameTextView.setImeOptions(6);
        this.usernameTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", R.string.ChannelUsernamePlaceholder));
        this.usernameTextView.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.usernameTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        this.usernameTextView.setCursorWidth(1.5f);
        this.publicContainer.addView(this.usernameTextView, LayoutHelper.createLinear(-1, 36));
        this.usernameTextView.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.ChatEditTypeActivity.4
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (ChatEditTypeActivity.this.ignoreTextChanges) {
                    return;
                }
                String obj = ChatEditTypeActivity.this.usernameTextView.getText().toString();
                if (ChatEditTypeActivity.this.editableUsernameCell != null) {
                    ChatEditTypeActivity.this.editableUsernameCell.updateUsername(obj);
                }
                ChatEditTypeActivity.this.checkUserName(obj);
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                ChatEditTypeActivity.this.checkDoneButton();
            }
        });
        LinearLayout linearLayout5 = new LinearLayout(context);
        this.privateContainer = linearLayout5;
        linearLayout5.setOrientation(1);
        this.linkContainer.addView(this.privateContainer, LayoutHelper.createLinear(-1, -2));
        LinkActionView linkActionView = new LinkActionView(context, this, null, this.chatId, true, ChatObject.isChannel(this.currentChat));
        this.permanentLinkView = linkActionView;
        linkActionView.setDelegate(new LinkActionView.Delegate() { // from class: org.telegram.ui.ChatEditTypeActivity.5
            @Override // org.telegram.ui.Components.LinkActionView.Delegate
            public /* synthetic */ void editLink() {
                LinkActionView.Delegate.CC.$default$editLink(this);
            }

            @Override // org.telegram.ui.Components.LinkActionView.Delegate
            public /* synthetic */ void removeLink() {
                LinkActionView.Delegate.CC.$default$removeLink(this);
            }

            @Override // org.telegram.ui.Components.LinkActionView.Delegate
            public void revokeLink() {
                ChatEditTypeActivity.this.generateLink(true);
            }

            @Override // org.telegram.ui.Components.LinkActionView.Delegate
            public void showUsersForPermanentLink() {
                ChatEditTypeActivity chatEditTypeActivity = ChatEditTypeActivity.this;
                Context context2 = context;
                TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = ChatEditTypeActivity.this.invite;
                TLRPC$ChatFull tLRPC$ChatFull = ChatEditTypeActivity.this.info;
                ChatEditTypeActivity chatEditTypeActivity2 = ChatEditTypeActivity.this;
                chatEditTypeActivity.inviteLinkBottomSheet = new InviteLinkBottomSheet(context2, tLRPC$TL_chatInviteExported, tLRPC$ChatFull, chatEditTypeActivity2.usersMap, chatEditTypeActivity2, chatEditTypeActivity2.chatId, true, ChatObject.isChannel(ChatEditTypeActivity.this.currentChat));
                ChatEditTypeActivity.this.inviteLinkBottomSheet.show();
            }
        });
        this.permanentLinkView.setUsers(0, null);
        this.privateContainer.addView(this.permanentLinkView);
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
        this.checkTextView = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
        this.checkTextView.setBottomPadding(6);
        this.linearLayout.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2));
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
        this.typeInfoCell = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setImportantForAccessibility(1);
        this.linearLayout.addView(this.typeInfoCell, LayoutHelper.createLinear(-1, -2));
        LoadingCell loadingCell = new LoadingCell(context);
        this.loadingAdminedCell = loadingCell;
        this.linearLayout.addView(loadingCell, LayoutHelper.createLinear(-1, -2));
        LinearLayout linearLayout6 = new LinearLayout(context);
        this.adminnedChannelsLayout = linearLayout6;
        linearLayout6.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.adminnedChannelsLayout.setOrientation(1);
        this.linearLayout.addView(this.adminnedChannelsLayout, LayoutHelper.createLinear(-1, -2));
        ShadowSectionCell shadowSectionCell2 = new ShadowSectionCell(context);
        this.adminedInfoCell = shadowSectionCell2;
        this.linearLayout.addView(shadowSectionCell2, LayoutHelper.createLinear(-1, -2));
        LinearLayout linearLayout7 = this.linearLayout;
        UsernamesListView usernamesListView = new UsernamesListView(context);
        this.usernamesListView = usernamesListView;
        linearLayout7.addView(usernamesListView, LayoutHelper.createLinear(-1, -2));
        this.usernamesListView.setVisibility((this.isPrivate || this.usernames.isEmpty()) ? 8 : 0);
        TextCell textCell = new TextCell(context);
        this.manageLinksTextView = textCell;
        textCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.manageLinksTextView.setTextAndIcon(LocaleController.getString("ManageInviteLinks", R.string.ManageInviteLinks), R.drawable.msg_link2, false);
        this.manageLinksTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatEditTypeActivity.this.lambda$createView$4(view);
            }
        });
        this.linearLayout.addView(this.manageLinksTextView, LayoutHelper.createLinear(-1, -2));
        TextInfoPrivacyCell textInfoPrivacyCell3 = new TextInfoPrivacyCell(context);
        this.manageLinksInfoCell = textInfoPrivacyCell3;
        this.linearLayout.addView(textInfoPrivacyCell3, LayoutHelper.createLinear(-1, -2));
        JoinToSendSettingsView joinToSendSettingsView = new JoinToSendSettingsView(context, this.currentChat);
        this.joinContainer = joinToSendSettingsView;
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        joinToSendSettingsView.showJoinToSend((tLRPC$ChatFull == null || tLRPC$ChatFull.linked_chat_id == 0) ? false : true);
        this.linearLayout.addView(this.joinContainer);
        LinearLayout linearLayout8 = new LinearLayout(context);
        this.saveContainer = linearLayout8;
        linearLayout8.setOrientation(1);
        this.linearLayout.addView(this.saveContainer);
        HeaderCell headerCell3 = new HeaderCell(context, 23);
        this.saveHeaderCell = headerCell3;
        headerCell3.setHeight(46);
        this.saveHeaderCell.setText(LocaleController.getString("SavingContentTitle", R.string.SavingContentTitle));
        this.saveHeaderCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.saveContainer.addView(this.saveHeaderCell, LayoutHelper.createLinear(-1, -2));
        TextCheckCell textCheckCell = new TextCheckCell(context);
        this.saveRestrictCell = textCheckCell;
        textCheckCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.saveRestrictCell.setTextAndCheck(LocaleController.getString("RestrictSavingContent", R.string.RestrictSavingContent), this.isSaveRestricted, false);
        this.saveRestrictCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatEditTypeActivity.this.lambda$createView$5(view);
            }
        });
        this.saveContainer.addView(this.saveRestrictCell, LayoutHelper.createLinear(-1, -2));
        this.saveRestrictInfoCell = new TextInfoPrivacyCell(context);
        if (this.isChannel && !ChatObject.isMegagroup(this.currentChat)) {
            this.saveRestrictInfoCell.setText(LocaleController.getString("RestrictSavingContentInfoChannel", R.string.RestrictSavingContentInfoChannel));
        } else {
            this.saveRestrictInfoCell.setText(LocaleController.getString("RestrictSavingContentInfoGroup", R.string.RestrictSavingContentInfoGroup));
        }
        this.saveContainer.addView(this.saveRestrictInfoCell, LayoutHelper.createLinear(-1, -2));
        String publicUsername = ChatObject.getPublicUsername(this.currentChat, true);
        if (!this.isPrivate && publicUsername != null) {
            this.ignoreTextChanges = true;
            this.usernameTextView.setText(publicUsername);
            this.usernameTextView.setSelection(publicUsername.length());
            this.ignoreTextChanges = false;
        }
        updatePrivatePublic();
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(View view) {
        if (this.isPrivate) {
            return;
        }
        this.isPrivate = true;
        updatePrivatePublic();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(View view) {
        if (!this.isPrivate) {
            return;
        }
        if (!this.canCreatePublic) {
            showPremiumIncreaseLimitDialog();
            return;
        }
        this.isPrivate = false;
        updatePrivatePublic();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(View view) {
        ManageLinksActivity manageLinksActivity = new ManageLinksActivity(this.chatId, 0L, 0);
        manageLinksActivity.setInfo(this.info, this.invite);
        presentFragment(manageLinksActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(View view) {
        boolean z = !this.isSaveRestricted;
        this.isSaveRestricted = z;
        ((TextCheckCell) view).setChecked(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6() {
        updateDoneProgress(true);
    }

    private void updateDoneProgress(boolean z) {
        if (!z) {
            AndroidUtilities.cancelRunOnUIThread(this.enableDoneLoading);
        }
        if (this.doneButtonDrawable != null) {
            ValueAnimator valueAnimator = this.doneButtonDrawableAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            float[] fArr = new float[2];
            fArr[0] = this.doneButtonDrawable.getProgress();
            float f = 1.0f;
            fArr[1] = z ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.doneButtonDrawableAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ChatEditTypeActivity.this.lambda$updateDoneProgress$7(valueAnimator2);
                }
            });
            ValueAnimator valueAnimator2 = this.doneButtonDrawableAnimator;
            float progress = this.doneButtonDrawable.getProgress();
            if (!z) {
                f = 0.0f;
            }
            valueAnimator2.setDuration(Math.abs(progress - f) * 200.0f);
            this.doneButtonDrawableAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.doneButtonDrawableAnimator.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateDoneProgress$7(ValueAnimator valueAnimator) {
        this.doneButtonDrawable.setProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
        this.doneButtonDrawable.invalidateSelf();
    }

    private void showPremiumIncreaseLimitDialog() {
        if (getParentActivity() == null) {
            return;
        }
        LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(this, getParentActivity(), 2, this.currentAccount);
        limitReachedBottomSheet.parentIsChannel = this.isChannel;
        limitReachedBottomSheet.onSuccessRunnable = new Runnable() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                ChatEditTypeActivity.this.lambda$showPremiumIncreaseLimitDialog$8();
            }
        };
        showDialog(limitReachedBottomSheet);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showPremiumIncreaseLimitDialog$8() {
        this.canCreatePublic = true;
        updatePrivatePublic();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC$ChatFull tLRPC$ChatFull = (TLRPC$ChatFull) objArr[0];
            if (tLRPC$ChatFull.id != this.chatId) {
                return;
            }
            this.info = tLRPC$ChatFull;
            this.invite = tLRPC$ChatFull.exported_invite;
            updatePrivatePublic();
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

    /* JADX INFO: Access modifiers changed from: private */
    public void processDone() {
        AndroidUtilities.runOnUIThread(this.enableDoneLoading, 200L);
        if (this.currentChat.noforwards != this.isSaveRestricted) {
            MessagesController messagesController = getMessagesController();
            long j = this.chatId;
            TLRPC$Chat tLRPC$Chat = this.currentChat;
            boolean z = this.isSaveRestricted;
            tLRPC$Chat.noforwards = z;
            messagesController.toggleChatNoForwards(j, z);
        }
        if (!trySetUsername() || !tryUpdateJoinSettings()) {
            return;
        }
        finishFragment();
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x0029  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x003d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean tryUpdateJoinSettings() {
        /*
            r9 = this;
            boolean r0 = r9.isChannel
            r1 = 1
            if (r0 != 0) goto L77
            org.telegram.ui.Components.JoinToSendSettingsView r0 = r9.joinContainer
            if (r0 != 0) goto La
            goto L77
        La:
            android.app.Activity r0 = r9.getParentActivity()
            r2 = 0
            if (r0 != 0) goto L12
            return r2
        L12:
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 != 0) goto L26
            org.telegram.ui.Components.JoinToSendSettingsView r0 = r9.joinContainer
            boolean r3 = r0.isJoinToSend
            if (r3 != 0) goto L24
            boolean r0 = r0.isJoinRequest
            if (r0 == 0) goto L26
        L24:
            r0 = 1
            goto L27
        L26:
            r0 = 0
        L27:
            if (r0 == 0) goto L3d
            org.telegram.messenger.MessagesController r3 = r9.getMessagesController()
            android.app.Activity r4 = r9.getParentActivity()
            long r5 = r9.chatId
            org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda19 r8 = new org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda19
            r8.<init>()
            r7 = r9
            r3.convertToMegaGroup(r4, r5, r7, r8)
            return r2
        L3d:
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = r0.join_to_send
            org.telegram.ui.Components.JoinToSendSettingsView r2 = r9.joinContainer
            boolean r2 = r2.isJoinToSend
            if (r0 == r2) goto L5a
            org.telegram.messenger.MessagesController r3 = r9.getMessagesController()
            long r4 = r9.chatId
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            org.telegram.ui.Components.JoinToSendSettingsView r2 = r9.joinContainer
            boolean r6 = r2.isJoinToSend
            r0.join_to_send = r6
            r7 = 0
            r8 = 0
            r3.toggleChatJoinToSend(r4, r6, r7, r8)
        L5a:
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            boolean r0 = r0.join_request
            org.telegram.ui.Components.JoinToSendSettingsView r2 = r9.joinContainer
            boolean r2 = r2.isJoinRequest
            if (r0 == r2) goto L77
            org.telegram.messenger.MessagesController r3 = r9.getMessagesController()
            long r4 = r9.chatId
            org.telegram.tgnet.TLRPC$Chat r0 = r9.currentChat
            org.telegram.ui.Components.JoinToSendSettingsView r2 = r9.joinContainer
            boolean r6 = r2.isJoinRequest
            r0.join_request = r6
            r7 = 0
            r8 = 0
            r3.toggleChatJoinRequest(r4, r6, r7, r8)
        L77:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditTypeActivity.tryUpdateJoinSettings():boolean");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$tryUpdateJoinSettings$9(long j) {
        if (j != 0) {
            this.chatId = j;
            this.currentChat = getMessagesController().getChat(Long.valueOf(j));
            processDone();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class UsernamesListView extends RecyclerListView {
        private Adapter adapter;
        private Paint backgroundPaint;
        private ItemTouchHelper itemTouchHelper;
        private boolean needReorder;

        public UsernamesListView(Context context) {
            super(context);
            this.needReorder = false;
            this.backgroundPaint = new Paint(1);
            Adapter adapter = new Adapter();
            this.adapter = adapter;
            setAdapter(adapter);
            setLayoutManager(new LinearLayoutManager(context));
            setOnItemClickListener(new AnonymousClass1(ChatEditTypeActivity.this));
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelperCallback());
            this.itemTouchHelper = itemTouchHelper;
            itemTouchHelper.attachToRecyclerView(this);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: org.telegram.ui.ChatEditTypeActivity$UsernamesListView$1  reason: invalid class name */
        /* loaded from: classes3.dex */
        public class AnonymousClass1 implements RecyclerListView.OnItemClickListener {
            AnonymousClass1(ChatEditTypeActivity chatEditTypeActivity) {
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public void onItemClick(final View view, int i) {
                final TLRPC$TL_username tLRPC$TL_username;
                int i2;
                String str;
                int i3;
                String str2;
                int i4;
                String str3;
                if (!(view instanceof ChangeUsernameActivity.UsernameCell) || (tLRPC$TL_username = ((ChangeUsernameActivity.UsernameCell) view).currentUsername) == null) {
                    return;
                }
                if (tLRPC$TL_username.editable) {
                    if (((BaseFragment) ChatEditTypeActivity.this).fragmentView instanceof ScrollView) {
                        ((ScrollView) ((BaseFragment) ChatEditTypeActivity.this).fragmentView).smoothScrollTo(0, ChatEditTypeActivity.this.linkContainer.getTop() - AndroidUtilities.dp(128.0f));
                    }
                    ChatEditTypeActivity.this.usernameTextView.requestFocus();
                    AndroidUtilities.showKeyboard(ChatEditTypeActivity.this.usernameTextView);
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(UsernamesListView.this.getContext(), ChatEditTypeActivity.this.getResourceProvider());
                if (tLRPC$TL_username.active) {
                    i2 = R.string.UsernameDeactivateLink;
                    str = "UsernameDeactivateLink";
                } else {
                    i2 = R.string.UsernameActivateLink;
                    str = "UsernameActivateLink";
                }
                AlertDialog.Builder title = builder.setTitle(LocaleController.getString(str, i2));
                if (tLRPC$TL_username.active) {
                    i3 = R.string.UsernameDeactivateLinkChannelMessage;
                    str2 = "UsernameDeactivateLinkChannelMessage";
                } else {
                    i3 = R.string.UsernameActivateLinkChannelMessage;
                    str2 = "UsernameActivateLinkChannelMessage";
                }
                AlertDialog.Builder message = title.setMessage(LocaleController.getString(str2, i3));
                if (tLRPC$TL_username.active) {
                    i4 = R.string.Hide;
                    str3 = "Hide";
                } else {
                    i4 = R.string.Show;
                    str3 = "Show";
                }
                message.setPositiveButton(LocaleController.getString(str3, i4), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatEditTypeActivity$UsernamesListView$1$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i5) {
                        ChatEditTypeActivity.UsernamesListView.AnonymousClass1.this.lambda$onItemClick$4(tLRPC$TL_username, view, dialogInterface, i5);
                    }
                }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), ChatEditTypeActivity$UsernamesListView$1$$ExternalSyntheticLambda2.INSTANCE).show();
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$4(final TLRPC$TL_username tLRPC$TL_username, View view, DialogInterface dialogInterface, int i) {
                if (tLRPC$TL_username.editable) {
                    if (ChatEditTypeActivity.this.editableUsernameWasActive == null) {
                        ChatEditTypeActivity.this.editableUsernameWasActive = Boolean.valueOf(tLRPC$TL_username.active);
                    }
                    ChatEditTypeActivity chatEditTypeActivity = ChatEditTypeActivity.this;
                    boolean z = !tLRPC$TL_username.active;
                    tLRPC$TL_username.active = z;
                    chatEditTypeActivity.editableUsernameUpdated = Boolean.valueOf(z);
                } else {
                    TLRPC$TL_channels_toggleUsername tLRPC$TL_channels_toggleUsername = new TLRPC$TL_channels_toggleUsername();
                    TLRPC$TL_inputChannel tLRPC$TL_inputChannel = new TLRPC$TL_inputChannel();
                    tLRPC$TL_inputChannel.channel_id = ChatEditTypeActivity.this.currentChat.id;
                    tLRPC$TL_inputChannel.access_hash = ChatEditTypeActivity.this.currentChat.access_hash;
                    tLRPC$TL_channels_toggleUsername.channel = tLRPC$TL_inputChannel;
                    tLRPC$TL_channels_toggleUsername.username = tLRPC$TL_username.username;
                    final boolean z2 = tLRPC$TL_username.active;
                    boolean z3 = !z2;
                    tLRPC$TL_username.active = z3;
                    tLRPC$TL_channels_toggleUsername.active = z3;
                    ChatEditTypeActivity.this.getConnectionsManager().sendRequest(tLRPC$TL_channels_toggleUsername, new RequestDelegate() { // from class: org.telegram.ui.ChatEditTypeActivity$UsernamesListView$1$$ExternalSyntheticLambda5
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            ChatEditTypeActivity.UsernamesListView.AnonymousClass1.this.lambda$onItemClick$3(tLRPC$TL_username, z2, tLObject, tLRPC$TL_error);
                        }
                    }, 1024);
                }
                ((ChangeUsernameActivity.UsernameCell) view).update();
                ChatEditTypeActivity.this.checkDoneButton();
                UsernamesListView.this.toggleUsername(tLRPC$TL_username, tLRPC$TL_username.active);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$3(final TLRPC$TL_username tLRPC$TL_username, final boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                if (tLObject instanceof TLRPC$TL_boolTrue) {
                    return;
                }
                if (tLRPC$TL_error != null && "USERNAMES_ACTIVE_TOO_MUCH".equals(tLRPC$TL_error.text)) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatEditTypeActivity$UsernamesListView$1$$ExternalSyntheticLambda4
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatEditTypeActivity.UsernamesListView.AnonymousClass1.this.lambda$onItemClick$1(tLRPC$TL_username, z);
                        }
                    });
                } else {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatEditTypeActivity$UsernamesListView$1$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatEditTypeActivity.UsernamesListView.AnonymousClass1.this.lambda$onItemClick$2(tLRPC$TL_username, z);
                        }
                    });
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$1(final TLRPC$TL_username tLRPC$TL_username, final boolean z) {
                new AlertDialog.Builder(UsernamesListView.this.getContext(), ((RecyclerListView) UsernamesListView.this).resourcesProvider).setTitle(LocaleController.getString("UsernameActivateErrorTitle", R.string.UsernameActivateErrorTitle)).setMessage(LocaleController.getString("UsernameActivateErrorMessage", R.string.UsernameActivateErrorMessage)).setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatEditTypeActivity$UsernamesListView$1$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ChatEditTypeActivity.UsernamesListView.AnonymousClass1.this.lambda$onItemClick$0(tLRPC$TL_username, z, dialogInterface, i);
                    }
                }).show();
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$0(TLRPC$TL_username tLRPC$TL_username, boolean z, DialogInterface dialogInterface, int i) {
                UsernamesListView.this.toggleUsername(tLRPC$TL_username, z, true);
                ChatEditTypeActivity.this.checkDoneButton();
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$2(TLRPC$TL_username tLRPC$TL_username, boolean z) {
                UsernamesListView.this.toggleUsername(tLRPC$TL_username, z, true);
                ChatEditTypeActivity.this.checkDoneButton();
            }
        }

        public void toggleUsername(TLRPC$TL_username tLRPC$TL_username, boolean z) {
            toggleUsername(tLRPC$TL_username, z, false);
        }

        public void toggleUsername(TLRPC$TL_username tLRPC$TL_username, boolean z, boolean z2) {
            for (int i = 0; i < ChatEditTypeActivity.this.usernames.size(); i++) {
                if (ChatEditTypeActivity.this.usernames.get(i) == tLRPC$TL_username) {
                    toggleUsername(i + 1, z, z2);
                    return;
                }
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:31:0x008e  */
        /* JADX WARN: Removed duplicated region for block: B:46:0x00aa A[EDGE_INSN: B:46:0x00aa->B:39:0x00aa ?: BREAK  , SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void toggleUsername(int r5, boolean r6, boolean r7) {
            /*
                r4 = this;
                int r0 = r5 + (-1)
                if (r0 < 0) goto Lb3
                org.telegram.ui.ChatEditTypeActivity r1 = org.telegram.ui.ChatEditTypeActivity.this
                java.util.ArrayList r1 = org.telegram.ui.ChatEditTypeActivity.access$2100(r1)
                int r1 = r1.size()
                if (r0 < r1) goto L12
                goto Lb3
            L12:
                org.telegram.ui.ChatEditTypeActivity r1 = org.telegram.ui.ChatEditTypeActivity.this
                java.util.ArrayList r1 = org.telegram.ui.ChatEditTypeActivity.access$2100(r1)
                java.lang.Object r0 = r1.get(r0)
                org.telegram.tgnet.TLRPC$TL_username r0 = (org.telegram.tgnet.TLRPC$TL_username) r0
                r0.active = r6
                r0 = -1
                r1 = 0
                if (r6 == 0) goto L51
                r6 = 0
            L25:
                org.telegram.ui.ChatEditTypeActivity r2 = org.telegram.ui.ChatEditTypeActivity.this
                java.util.ArrayList r2 = org.telegram.ui.ChatEditTypeActivity.access$2100(r2)
                int r2 = r2.size()
                if (r6 >= r2) goto L45
                org.telegram.ui.ChatEditTypeActivity r2 = org.telegram.ui.ChatEditTypeActivity.this
                java.util.ArrayList r2 = org.telegram.ui.ChatEditTypeActivity.access$2100(r2)
                java.lang.Object r2 = r2.get(r6)
                org.telegram.tgnet.TLRPC$TL_username r2 = (org.telegram.tgnet.TLRPC$TL_username) r2
                boolean r2 = r2.active
                if (r2 != 0) goto L42
                goto L46
            L42:
                int r6 = r6 + 1
                goto L25
            L45:
                r6 = -1
            L46:
                if (r6 < 0) goto L88
                int r6 = r6 + (-1)
                int r6 = java.lang.Math.max(r1, r6)
            L4e:
                int r0 = r6 + 1
                goto L88
            L51:
                r6 = 0
                r2 = -1
            L53:
                org.telegram.ui.ChatEditTypeActivity r3 = org.telegram.ui.ChatEditTypeActivity.this
                java.util.ArrayList r3 = org.telegram.ui.ChatEditTypeActivity.access$2100(r3)
                int r3 = r3.size()
                if (r6 >= r3) goto L73
                org.telegram.ui.ChatEditTypeActivity r3 = org.telegram.ui.ChatEditTypeActivity.this
                java.util.ArrayList r3 = org.telegram.ui.ChatEditTypeActivity.access$2100(r3)
                java.lang.Object r3 = r3.get(r6)
                org.telegram.tgnet.TLRPC$TL_username r3 = (org.telegram.tgnet.TLRPC$TL_username) r3
                boolean r3 = r3.active
                if (r3 == 0) goto L70
                r2 = r6
            L70:
                int r6 = r6 + 1
                goto L53
            L73:
                if (r2 < 0) goto L88
                org.telegram.ui.ChatEditTypeActivity r6 = org.telegram.ui.ChatEditTypeActivity.this
                java.util.ArrayList r6 = org.telegram.ui.ChatEditTypeActivity.access$2100(r6)
                int r6 = r6.size()
                int r6 = r6 + (-1)
                int r2 = r2 + 1
                int r6 = java.lang.Math.min(r6, r2)
                goto L4e
            L88:
                int r6 = r4.getChildCount()
                if (r1 >= r6) goto Laa
                android.view.View r6 = r4.getChildAt(r1)
                int r2 = r4.getChildAdapterPosition(r6)
                if (r2 != r5) goto La7
                if (r7 == 0) goto L9d
                org.telegram.messenger.AndroidUtilities.shakeView(r6)
            L9d:
                boolean r7 = r6 instanceof org.telegram.ui.ChangeUsernameActivity.UsernameCell
                if (r7 == 0) goto Laa
                org.telegram.ui.ChangeUsernameActivity$UsernameCell r6 = (org.telegram.ui.ChangeUsernameActivity.UsernameCell) r6
                r6.update()
                goto Laa
            La7:
                int r1 = r1 + 1
                goto L88
            Laa:
                if (r0 < 0) goto Lb3
                if (r5 == r0) goto Lb3
                org.telegram.ui.ChatEditTypeActivity$UsernamesListView$Adapter r6 = r4.adapter
                r6.moveElement(r5, r0)
            Lb3:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatEditTypeActivity.UsernamesListView.toggleUsername(int, boolean, boolean):void");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(NUM, Integer.MIN_VALUE));
        }

        /* loaded from: classes3.dex */
        public class TouchHelperCallback extends ItemTouchHelper.Callback {
            @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
            }

            public TouchHelperCallback() {
            }

            @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder.getItemViewType() != 1 || !((ChangeUsernameActivity.UsernameCell) viewHolder.itemView).active) {
                    return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
                }
                return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
            }

            @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
                if (viewHolder.getItemViewType() == viewHolder2.getItemViewType()) {
                    View view = viewHolder2.itemView;
                    if ((view instanceof ChangeUsernameActivity.UsernameCell) && !((ChangeUsernameActivity.UsernameCell) view).active) {
                        return false;
                    }
                    UsernamesListView.this.adapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
                    return true;
                }
                return false;
            }

            @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
            public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
                super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
            }

            @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
                if (i == 0) {
                    ChatEditTypeActivity.this.ignoreScroll = false;
                    UsernamesListView.this.sendReorder();
                } else {
                    ChatEditTypeActivity.this.ignoreScroll = true;
                    UsernamesListView.this.cancelClickRunnables(false);
                    viewHolder.itemView.setPressed(true);
                }
                super.onSelectedChanged(viewHolder, i);
            }

            @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setPressed(false);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendReorder() {
            if (!this.needReorder || ChatEditTypeActivity.this.currentChat == null) {
                return;
            }
            this.needReorder = false;
            TLRPC$TL_channels_reorderUsernames tLRPC$TL_channels_reorderUsernames = new TLRPC$TL_channels_reorderUsernames();
            TLRPC$TL_inputChannel tLRPC$TL_inputChannel = new TLRPC$TL_inputChannel();
            tLRPC$TL_inputChannel.channel_id = ChatEditTypeActivity.this.currentChat.id;
            tLRPC$TL_inputChannel.access_hash = ChatEditTypeActivity.this.currentChat.access_hash;
            tLRPC$TL_channels_reorderUsernames.channel = tLRPC$TL_inputChannel;
            ArrayList<String> arrayList = new ArrayList<>();
            for (int i = 0; i < ChatEditTypeActivity.this.editableUsernames.size(); i++) {
                if (((TLRPC$TL_username) ChatEditTypeActivity.this.editableUsernames.get(i)).active) {
                    arrayList.add(((TLRPC$TL_username) ChatEditTypeActivity.this.editableUsernames.get(i)).username);
                }
            }
            for (int i2 = 0; i2 < ChatEditTypeActivity.this.usernames.size(); i2++) {
                if (((TLRPC$TL_username) ChatEditTypeActivity.this.usernames.get(i2)).active) {
                    arrayList.add(((TLRPC$TL_username) ChatEditTypeActivity.this.usernames.get(i2)).username);
                }
            }
            tLRPC$TL_channels_reorderUsernames.order = arrayList;
            ChatEditTypeActivity.this.getConnectionsManager().sendRequest(tLRPC$TL_channels_reorderUsernames, ChatEditTypeActivity$UsernamesListView$$ExternalSyntheticLambda0.INSTANCE);
            updateChat();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$sendReorder$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            boolean z = tLObject instanceof TLRPC$TL_boolTrue;
        }

        private void updateChat() {
            ChatEditTypeActivity.this.currentChat.usernames.clear();
            ChatEditTypeActivity.this.currentChat.usernames.addAll(ChatEditTypeActivity.this.editableUsernames);
            ChatEditTypeActivity.this.currentChat.usernames.addAll(ChatEditTypeActivity.this.usernames);
            ChatEditTypeActivity.this.getMessagesController().putChat(ChatEditTypeActivity.this.currentChat, true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public class Adapter extends RecyclerListView.SelectionAdapter {
            private Adapter() {
            }

            public void swapElements(int i, int i2) {
                int i3 = i - 1;
                int i4 = i2 - 1;
                if (i3 >= ChatEditTypeActivity.this.usernames.size() || i4 >= ChatEditTypeActivity.this.usernames.size()) {
                    return;
                }
                if (i != i2) {
                    UsernamesListView.this.needReorder = true;
                }
                swapListElements(ChatEditTypeActivity.this.usernames, i3, i4);
                notifyItemMoved(i, i2);
                int size = (ChatEditTypeActivity.this.usernames.size() + 1) - 1;
                if (i != size && i2 != size) {
                    return;
                }
                notifyItemChanged(i, 3);
                notifyItemChanged(i2, 3);
            }

            private void swapListElements(List<TLRPC$TL_username> list, int i, int i2) {
                list.set(i, list.get(i2));
                list.set(i2, list.get(i));
            }

            public void moveElement(int i, int i2) {
                int i3 = i - 1;
                int i4 = i2 - 1;
                if (i3 >= ChatEditTypeActivity.this.usernames.size() || i4 >= ChatEditTypeActivity.this.usernames.size()) {
                    return;
                }
                ChatEditTypeActivity.this.usernames.add(i4, (TLRPC$TL_username) ChatEditTypeActivity.this.usernames.remove(i3));
                notifyItemMoved(i, i2);
                int i5 = 0;
                while (i5 < ChatEditTypeActivity.this.usernames.size()) {
                    i5++;
                    notifyItemChanged(i5);
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            /* renamed from: onCreateViewHolder */
            public RecyclerView.ViewHolder mo1813onCreateViewHolder(ViewGroup viewGroup, int i) {
                if (i != 0) {
                    if (i == 1) {
                        return new RecyclerListView.Holder(new ChangeUsernameActivity.UsernameCell(UsernamesListView.this.getContext(), ((RecyclerListView) UsernamesListView.this).resourcesProvider) { // from class: org.telegram.ui.ChatEditTypeActivity.UsernamesListView.Adapter.1
                            @Override // org.telegram.ui.ChangeUsernameActivity.UsernameCell
                            protected String getUsernameEditable() {
                                if (ChatEditTypeActivity.this.usernameTextView == null) {
                                    return null;
                                }
                                return ChatEditTypeActivity.this.usernameTextView.getText().toString();
                            }
                        });
                    }
                    if (i == 2) {
                        return new RecyclerListView.Holder(new TextInfoPrivacyCell(UsernamesListView.this.getContext(), ((RecyclerListView) UsernamesListView.this).resourcesProvider));
                    }
                    return null;
                }
                return new RecyclerListView.Holder(new HeaderCell(UsernamesListView.this.getContext(), ((RecyclerListView) UsernamesListView.this).resourcesProvider));
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                int itemViewType = viewHolder.getItemViewType();
                if (itemViewType == 0) {
                    ((HeaderCell) viewHolder.itemView).setBackgroundColor(Theme.getColor("windowBackgroundWhite", ((RecyclerListView) UsernamesListView.this).resourcesProvider));
                    ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("UsernamesChannelHeader", R.string.UsernamesChannelHeader));
                    return;
                }
                boolean z = true;
                if (itemViewType != 1) {
                    if (itemViewType != 2) {
                        return;
                    }
                    ((TextInfoPrivacyCell) viewHolder.itemView).setText(LocaleController.getString("UsernamesChannelHelp", R.string.UsernamesChannelHelp));
                    ((TextInfoPrivacyCell) viewHolder.itemView).setBackgroundDrawable(Theme.getThemedDrawable(UsernamesListView.this.getContext(), R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                    return;
                }
                TLRPC$TL_username tLRPC$TL_username = (TLRPC$TL_username) ChatEditTypeActivity.this.usernames.get(i - 1);
                if (((ChangeUsernameActivity.UsernameCell) viewHolder.itemView).editable) {
                    ChatEditTypeActivity.this.editableUsernameCell = null;
                }
                ChangeUsernameActivity.UsernameCell usernameCell = (ChangeUsernameActivity.UsernameCell) viewHolder.itemView;
                if (i >= ChatEditTypeActivity.this.usernames.size()) {
                    z = false;
                }
                usernameCell.set(tLRPC$TL_username, z, false);
                if (tLRPC$TL_username == null || !tLRPC$TL_username.editable) {
                    return;
                }
                ChatEditTypeActivity.this.editableUsernameCell = (ChangeUsernameActivity.UsernameCell) viewHolder.itemView;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemViewType(int i) {
                if (i == 0) {
                    return 0;
                }
                return i <= ChatEditTypeActivity.this.usernames.size() ? 1 : 2;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return ChatEditTypeActivity.this.usernames.size() + 2;
            }

            @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return viewHolder.getItemViewType() == 1;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            int childAdapterPosition;
            int size = (ChatEditTypeActivity.this.usernames.size() + 1) - 1;
            int i = Integer.MAX_VALUE;
            int i2 = Integer.MIN_VALUE;
            for (int i3 = 0; i3 < getChildCount(); i3++) {
                View childAt = getChildAt(i3);
                if (childAt != null && (childAdapterPosition = getChildAdapterPosition(childAt)) >= 1 && childAdapterPosition <= size) {
                    i = Math.min(childAt.getTop(), i);
                    i2 = Math.max(childAt.getBottom(), i2);
                }
            }
            if (i < i2) {
                this.backgroundPaint.setColor(Theme.getColor("windowBackgroundWhite", this.resourcesProvider));
                canvas.drawRect(0.0f, i, getWidth(), i2, this.backgroundPaint);
            }
            super.dispatchDraw(canvas);
        }
    }

    private boolean trySetUsername() {
        if (getParentActivity() == null) {
            return false;
        }
        String publicUsername = ChatObject.getPublicUsername(this.currentChat, true);
        if (!this.isPrivate && (((publicUsername == 0 && this.usernameTextView.length() != 0) || (publicUsername != 0 && !publicUsername.equalsIgnoreCase(this.usernameTextView.getText().toString()))) && this.usernameTextView.length() != 0 && !this.lastNameAvailable)) {
            Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200L);
            }
            AndroidUtilities.shakeView(this.checkTextView);
            updateDoneProgress(false);
            return false;
        }
        String str = "";
        if (publicUsername == null) {
            publicUsername = str;
        }
        if (!this.isPrivate) {
            str = this.usernameTextView.getText().toString();
        }
        String str2 = str;
        if (publicUsername.equals(str2)) {
            return tryDeactivateAllLinks();
        } else if (!ChatObject.isChannel(this.currentChat)) {
            getMessagesController().convertToMegaGroup(getParentActivity(), this.chatId, this, new MessagesStorage.LongCallback() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda18
                @Override // org.telegram.messenger.MessagesStorage.LongCallback
                public final void run(long j) {
                    ChatEditTypeActivity.this.lambda$trySetUsername$10(j);
                }
            });
            return false;
        } else {
            getMessagesController().updateChannelUserName(this, this.chatId, str2, new Runnable() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    ChatEditTypeActivity.this.lambda$trySetUsername$11();
                }
            }, new Runnable() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    ChatEditTypeActivity.this.lambda$trySetUsername$12();
                }
            });
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$trySetUsername$10(long j) {
        if (j != 0) {
            this.chatId = j;
            this.currentChat = getMessagesController().getChat(Long.valueOf(j));
            processDone();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$trySetUsername$11() {
        this.currentChat = getMessagesController().getChat(Long.valueOf(this.chatId));
        processDone();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$trySetUsername$12() {
        updateDoneProgress(false);
    }

    private boolean tryDeactivateAllLinks() {
        if (!this.isPrivate || this.currentChat.usernames == null) {
            return true;
        }
        if (this.deactivatingLinks) {
            return false;
        }
        this.deactivatingLinks = true;
        boolean z = false;
        for (int i = 0; i < this.currentChat.usernames.size(); i++) {
            TLRPC$TL_username tLRPC$TL_username = this.currentChat.usernames.get(i);
            if (tLRPC$TL_username != null && tLRPC$TL_username.active && !tLRPC$TL_username.editable) {
                z = true;
            }
        }
        if (z) {
            TLRPC$TL_channels_deactivateAllUsernames tLRPC$TL_channels_deactivateAllUsernames = new TLRPC$TL_channels_deactivateAllUsernames();
            tLRPC$TL_channels_deactivateAllUsernames.channel = MessagesController.getInputChannel(this.currentChat);
            getConnectionsManager().sendRequest(tLRPC$TL_channels_deactivateAllUsernames, new RequestDelegate() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda20
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChatEditTypeActivity.this.lambda$tryDeactivateAllLinks$13(tLObject, tLRPC$TL_error);
                }
            });
        }
        return !z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$tryDeactivateAllLinks$13(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            for (int i = 0; i < this.currentChat.usernames.size(); i++) {
                TLRPC$TL_username tLRPC$TL_username = this.currentChat.usernames.get(i);
                if (tLRPC$TL_username != null && tLRPC$TL_username.active && !tLRPC$TL_username.editable) {
                    tLRPC$TL_username.active = false;
                }
            }
        }
        this.deactivatingLinks = false;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                ChatEditTypeActivity.this.processDone();
            }
        });
    }

    private void loadAdminedChannels() {
        if (this.loadingAdminedChannels || this.adminnedChannelsLayout == null) {
            return;
        }
        this.loadingAdminedChannels = true;
        updatePrivatePublic();
        getConnectionsManager().sendRequest(new TLRPC$TL_channels_getAdminedPublicChannels(), new RequestDelegate() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda22
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChatEditTypeActivity.this.lambda$loadAdminedChannels$20(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAdminedChannels$20(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                ChatEditTypeActivity.this.lambda$loadAdminedChannels$19(tLObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAdminedChannels$19(TLObject tLObject) {
        this.loadingAdminedChannels = false;
        if (tLObject == null || getParentActivity() == null) {
            return;
        }
        for (int i = 0; i < this.adminedChannelCells.size(); i++) {
            this.linearLayout.removeView(this.adminedChannelCells.get(i));
        }
        this.adminedChannelCells.clear();
        TLRPC$TL_messages_chats tLRPC$TL_messages_chats = (TLRPC$TL_messages_chats) tLObject;
        for (int i2 = 0; i2 < tLRPC$TL_messages_chats.chats.size(); i2++) {
            AdminedChannelCell adminedChannelCell = new AdminedChannelCell(getParentActivity(), new View.OnClickListener() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChatEditTypeActivity.this.lambda$loadAdminedChannels$18(view);
                }
            }, false, 0);
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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAdminedChannels$18(View view) {
        final TLRPC$Chat currentChannel = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        if (this.isChannel) {
            int i = R.string.RevokeLinkAlertChannel;
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", i, getMessagesController().linkPrefix + "/" + ChatObject.getPublicUsername(currentChannel), currentChannel.title)));
        } else {
            int i2 = R.string.RevokeLinkAlert;
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", i2, getMessagesController().linkPrefix + "/" + ChatObject.getPublicUsername(currentChannel), currentChannel.title)));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", R.string.RevokeButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i3) {
                ChatEditTypeActivity.this.lambda$loadAdminedChannels$17(currentChannel, dialogInterface, i3);
            }
        });
        showDialog(builder.create());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAdminedChannels$17(TLRPC$Chat tLRPC$Chat, DialogInterface dialogInterface, int i) {
        TLRPC$TL_channels_updateUsername tLRPC$TL_channels_updateUsername = new TLRPC$TL_channels_updateUsername();
        tLRPC$TL_channels_updateUsername.channel = MessagesController.getInputChannel(tLRPC$Chat);
        tLRPC$TL_channels_updateUsername.username = "";
        getConnectionsManager().sendRequest(tLRPC$TL_channels_updateUsername, new RequestDelegate() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda23
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChatEditTypeActivity.this.lambda$loadAdminedChannels$16(tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAdminedChannels$16(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    ChatEditTypeActivity.this.lambda$loadAdminedChannels$15();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAdminedChannels$15() {
        this.canCreatePublic = true;
        if (this.usernameTextView.length() > 0) {
            checkUserName(this.usernameTextView.getText().toString());
        }
        updatePrivatePublic();
    }

    private void updatePrivatePublic() {
        int i;
        String str;
        int i2;
        String str2;
        if (this.sectionCell2 == null) {
            return;
        }
        Drawable drawable = null;
        int i3 = 8;
        if (!this.isPrivate && !this.canCreatePublic && getUserConfig().isPremium()) {
            this.typeInfoCell.setText(LocaleController.getString("ChangePublicLimitReached", R.string.ChangePublicLimitReached));
            this.typeInfoCell.setTag("windowBackgroundWhiteRedText4");
            this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
            this.linkContainer.setVisibility(8);
            this.checkTextView.setVisibility(8);
            this.sectionCell2.setVisibility(8);
            this.adminedInfoCell.setVisibility(0);
            if (this.loadingAdminedChannels) {
                this.loadingAdminedCell.setVisibility(0);
                this.adminnedChannelsLayout.setVisibility(8);
                this.typeInfoCell.setBackgroundDrawable(this.checkTextView.getVisibility() == 0 ? null : Theme.getThemedDrawable(this.typeInfoCell.getContext(), R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                this.adminedInfoCell.setBackgroundDrawable(null);
            } else {
                ShadowSectionCell shadowSectionCell = this.adminedInfoCell;
                shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(shadowSectionCell.getContext(), R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                TextInfoPrivacyCell textInfoPrivacyCell = this.typeInfoCell;
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell.getContext(), R.drawable.greydivider_top, "windowBackgroundGrayShadow"));
                this.loadingAdminedCell.setVisibility(8);
                this.adminnedChannelsLayout.setVisibility(0);
            }
        } else {
            this.typeInfoCell.setTag("windowBackgroundWhiteGrayText4");
            this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
            if (this.isForcePublic) {
                this.sectionCell2.setVisibility(8);
            } else {
                this.sectionCell2.setVisibility(0);
            }
            this.adminedInfoCell.setVisibility(8);
            TextInfoPrivacyCell textInfoPrivacyCell2 = this.typeInfoCell;
            Context context = textInfoPrivacyCell2.getContext();
            int i4 = R.drawable.greydivider_bottom;
            textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context, i4, "windowBackgroundGrayShadow"));
            this.adminnedChannelsLayout.setVisibility(8);
            this.linkContainer.setVisibility(0);
            this.loadingAdminedCell.setVisibility(8);
            if (this.isChannel) {
                TextInfoPrivacyCell textInfoPrivacyCell3 = this.typeInfoCell;
                if (this.isPrivate) {
                    i2 = R.string.ChannelPrivateLinkHelp;
                    str2 = "ChannelPrivateLinkHelp";
                } else {
                    i2 = R.string.ChannelUsernameHelp;
                    str2 = "ChannelUsernameHelp";
                }
                textInfoPrivacyCell3.setText(LocaleController.getString(str2, i2));
                this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", R.string.ChannelInviteLinkTitle) : LocaleController.getString("ChannelLinkTitle", R.string.ChannelLinkTitle));
            } else {
                TextInfoPrivacyCell textInfoPrivacyCell4 = this.typeInfoCell;
                if (this.isPrivate) {
                    i = R.string.MegaPrivateLinkHelp;
                    str = "MegaPrivateLinkHelp";
                } else {
                    i = R.string.MegaUsernameHelp;
                    str = "MegaUsernameHelp";
                }
                textInfoPrivacyCell4.setText(LocaleController.getString(str, i));
                this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", R.string.ChannelInviteLinkTitle) : LocaleController.getString("ChannelLinkTitle", R.string.ChannelLinkTitle));
            }
            this.publicContainer.setVisibility(this.isPrivate ? 8 : 0);
            this.privateContainer.setVisibility(this.isPrivate ? 0 : 8);
            this.saveContainer.setVisibility(0);
            this.manageLinksTextView.setVisibility(0);
            this.manageLinksInfoCell.setVisibility(0);
            this.linkContainer.setPadding(0, 0, 0, this.isPrivate ? 0 : AndroidUtilities.dp(7.0f));
            LinkActionView linkActionView = this.permanentLinkView;
            TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = this.invite;
            linkActionView.setLink(tLRPC$TL_chatInviteExported != null ? tLRPC$TL_chatInviteExported.link : null);
            this.permanentLinkView.loadUsers(this.invite, this.chatId);
            TextInfoPrivacyCell textInfoPrivacyCell5 = this.checkTextView;
            textInfoPrivacyCell5.setVisibility((this.isPrivate || textInfoPrivacyCell5.length() == 0) ? 8 : 0);
            this.manageLinksInfoCell.setText(LocaleController.getString("ManageLinksInfoHelp", R.string.ManageLinksInfoHelp));
            if (this.isPrivate) {
                TextInfoPrivacyCell textInfoPrivacyCell6 = this.typeInfoCell;
                textInfoPrivacyCell6.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell6.getContext(), R.drawable.greydivider, "windowBackgroundGrayShadow"));
                this.manageLinksInfoCell.setBackground(Theme.getThemedDrawable(this.typeInfoCell.getContext(), i4, "windowBackgroundGrayShadow"));
            } else {
                TextInfoPrivacyCell textInfoPrivacyCell7 = this.typeInfoCell;
                if (this.checkTextView.getVisibility() != 0) {
                    drawable = Theme.getThemedDrawable(this.typeInfoCell.getContext(), i4, "windowBackgroundGrayShadow");
                }
                textInfoPrivacyCell7.setBackgroundDrawable(drawable);
            }
        }
        boolean z = true;
        this.radioButtonCell1.setChecked(!this.isPrivate, true);
        this.radioButtonCell2.setChecked(this.isPrivate, true);
        this.usernameTextView.clearFocus();
        JoinToSendSettingsView joinToSendSettingsView = this.joinContainer;
        if (joinToSendSettingsView != null) {
            joinToSendSettingsView.setVisibility((this.isChannel || this.isPrivate) ? 8 : 0);
            JoinToSendSettingsView joinToSendSettingsView2 = this.joinContainer;
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            if (tLRPC$ChatFull == null || tLRPC$ChatFull.linked_chat_id == 0) {
                z = false;
            }
            joinToSendSettingsView2.showJoinToSend(z);
        }
        UsernamesListView usernamesListView = this.usernamesListView;
        if (usernamesListView != null) {
            if (!this.isPrivate && !this.usernames.isEmpty()) {
                i3 = 0;
            }
            usernamesListView.setVisibility(i3);
        }
        checkDoneButton();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkDoneButton() {
        if (this.isPrivate || this.usernameTextView.length() > 0 || hasActiveLink()) {
            this.doneButton.setEnabled(true);
            this.doneButton.setAlpha(1.0f);
            return;
        }
        this.doneButton.setEnabled(false);
        this.doneButton.setAlpha(0.5f);
    }

    public boolean hasActiveLink() {
        if (this.usernames == null) {
            return false;
        }
        for (int i = 0; i < this.usernames.size(); i++) {
            TLRPC$TL_username tLRPC$TL_username = this.usernames.get(i);
            if (tLRPC$TL_username != null && tLRPC$TL_username.active && !TextUtils.isEmpty(tLRPC$TL_username.username)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkUserName(final String str) {
        if (str != null && str.length() > 0) {
            this.checkTextView.setVisibility(0);
        } else {
            this.checkTextView.setVisibility(8);
        }
        this.typeInfoCell.setBackgroundDrawable(this.checkTextView.getVisibility() == 0 ? null : Theme.getThemedDrawable(this.typeInfoCell.getContext(), R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
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
                this.checkTextView.setText(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
                this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
                return false;
            }
            for (int i = 0; i < str.length(); i++) {
                char charAt = str.charAt(i);
                if (i == 0 && charAt >= '0' && charAt <= '9') {
                    if (this.isChannel) {
                        this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", R.string.LinkInvalidStartNumber));
                    } else {
                        this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumberMega", R.string.LinkInvalidStartNumberMega));
                    }
                    this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
                    return false;
                } else if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && charAt != '_'))) {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
                    this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
                    return false;
                }
            }
        }
        if (str == null || str.length() < 5) {
            if (this.isChannel) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", R.string.LinkInvalidShort));
            } else {
                this.checkTextView.setText(LocaleController.getString("LinkInvalidShortMega", R.string.LinkInvalidShortMega));
            }
            this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
            return false;
        } else if (str.length() > 32) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", R.string.LinkInvalidLong));
            this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
            return false;
        } else {
            this.checkTextView.setText(LocaleController.getString("LinkChecking", R.string.LinkChecking));
            this.checkTextView.setTextColor("windowBackgroundWhiteGrayText8");
            this.lastCheckName = str;
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    ChatEditTypeActivity.this.lambda$checkUserName$23(str);
                }
            };
            this.checkRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 300L);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUserName$23(final String str) {
        TLRPC$TL_channels_checkUsername tLRPC$TL_channels_checkUsername = new TLRPC$TL_channels_checkUsername();
        tLRPC$TL_channels_checkUsername.username = str;
        tLRPC$TL_channels_checkUsername.channel = getMessagesController().getInputChannel(this.chatId);
        this.checkReqId = getConnectionsManager().sendRequest(tLRPC$TL_channels_checkUsername, new RequestDelegate() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda24
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChatEditTypeActivity.this.lambda$checkUserName$22(str, tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUserName$22(final String str, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                ChatEditTypeActivity.this.lambda$checkUserName$21(str, tLRPC$TL_error, tLObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUserName$21(String str, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        this.checkReqId = 0;
        String str2 = this.lastCheckName;
        if (str2 == null || !str2.equals(str)) {
            return;
        }
        if (tLRPC$TL_error == null && (tLObject instanceof TLRPC$TL_boolTrue)) {
            this.checkTextView.setText(LocaleController.formatString("LinkAvailable", R.string.LinkAvailable, str));
            this.checkTextView.setTextColor("windowBackgroundWhiteGreenText");
            this.lastNameAvailable = true;
            return;
        }
        if (tLRPC$TL_error != null && tLRPC$TL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
            this.canCreatePublic = false;
            showPremiumIncreaseLimitDialog();
        } else {
            this.checkTextView.setText(LocaleController.getString("LinkInUse", R.string.LinkInUse));
        }
        this.checkTextView.setTextColor("windowBackgroundWhiteRedText4");
        this.lastNameAvailable = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void generateLink(final boolean z) {
        TLRPC$TL_messages_exportChatInvite tLRPC$TL_messages_exportChatInvite = new TLRPC$TL_messages_exportChatInvite();
        tLRPC$TL_messages_exportChatInvite.legacy_revoke_permanent = true;
        tLRPC$TL_messages_exportChatInvite.peer = getMessagesController().getInputPeer(-this.chatId);
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_exportChatInvite, new RequestDelegate() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda25
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChatEditTypeActivity.this.lambda$generateLink$25(z, tLObject, tLRPC$TL_error);
            }
        }), this.classGuid);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$generateLink$25(final boolean z, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                ChatEditTypeActivity.this.lambda$generateLink$24(tLRPC$TL_error, tLObject, z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$generateLink$24(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        String str = null;
        if (tLRPC$TL_error == null) {
            TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = (TLRPC$TL_chatInviteExported) tLObject;
            this.invite = tLRPC$TL_chatInviteExported;
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            if (tLRPC$ChatFull != null) {
                tLRPC$ChatFull.exported_invite = tLRPC$TL_chatInviteExported;
            }
            if (z) {
                if (getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setMessage(LocaleController.getString("RevokeAlertNewLink", R.string.RevokeAlertNewLink));
                builder.setTitle(LocaleController.getString("RevokeLink", R.string.RevokeLink));
                builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
                showDialog(builder.create());
            }
        }
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ChatEditTypeActivity$$ExternalSyntheticLambda26
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ChatEditTypeActivity.this.lambda$getThemeDescriptions$26();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.sectionCell2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.infoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.infoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.textCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.textCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription(this.textCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.textCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.linearLayoutTypeContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.headerCell2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.saveHeaderCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.saveRestrictCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.saveRestrictCell, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.saveRestrictCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription(this.saveRestrictCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"));
        arrayList.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText8"));
        arrayList.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGreenText"));
        arrayList.add(new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"));
        arrayList.add(new ThemeDescription(this.manageLinksInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.manageLinksInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.manageLinksInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"));
        arrayList.add(new ThemeDescription(this.saveRestrictInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.saveRestrictInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.saveRestrictInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"));
        arrayList.add(new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.loadingAdminedCell, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
        arrayList.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        arrayList.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
        arrayList.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        arrayList.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        arrayList.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"deleteButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(null, 0, null, null, Theme.avatarDrawables, themeDescriptionDelegate, "avatar_text"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.manageLinksTextView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.manageLinksTextView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.manageLinksTextView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$26() {
        LinearLayout linearLayout = this.adminnedChannelsLayout;
        if (linearLayout != null) {
            int childCount = linearLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.adminnedChannelsLayout.getChildAt(i);
                if (childAt instanceof AdminedChannelCell) {
                    ((AdminedChannelCell) childAt).update();
                }
            }
        }
        this.permanentLinkView.updateColors();
        this.manageLinksTextView.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        InviteLinkBottomSheet inviteLinkBottomSheet = this.inviteLinkBottomSheet;
        if (inviteLinkBottomSheet != null) {
            inviteLinkBottomSheet.updateColors();
        }
    }
}
