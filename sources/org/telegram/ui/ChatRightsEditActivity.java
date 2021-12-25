package org.telegram.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$InputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC$TL_account_getPassword;
import org.telegram.tgnet.TLRPC$TL_account_password;
import org.telegram.tgnet.TLRPC$TL_channels_editCreator;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputChannel;
import org.telegram.tgnet.TLRPC$TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC$TL_inputCheckPasswordEmpty;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DialogRadioCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.UserCell2;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class ChatRightsEditActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public int addAdminsRow;
    /* access modifiers changed from: private */
    public int addUsersRow;
    /* access modifiers changed from: private */
    public TLRPC$TL_chatAdminRights adminRights;
    /* access modifiers changed from: private */
    public int anonymousRow;
    /* access modifiers changed from: private */
    public int banUsersRow;
    /* access modifiers changed from: private */
    public TLRPC$TL_chatBannedRights bannedRights;
    /* access modifiers changed from: private */
    public boolean canEdit;
    /* access modifiers changed from: private */
    public int cantEditInfoRow;
    /* access modifiers changed from: private */
    public int changeInfoRow;
    private long chatId;
    private String currentBannedRights = "";
    /* access modifiers changed from: private */
    public TLRPC$Chat currentChat;
    /* access modifiers changed from: private */
    public String currentRank;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public TLRPC$User currentUser;
    /* access modifiers changed from: private */
    public TLRPC$TL_chatBannedRights defaultBannedRights;
    private ChatRightsEditActivityDelegate delegate;
    /* access modifiers changed from: private */
    public int deleteMessagesRow;
    /* access modifiers changed from: private */
    public int editMesagesRow;
    /* access modifiers changed from: private */
    public int embedLinksRow;
    private boolean initialIsSet;
    private String initialRank;
    private boolean isAddingNew;
    /* access modifiers changed from: private */
    public boolean isChannel;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public TLRPC$TL_chatAdminRights myAdminRights;
    /* access modifiers changed from: private */
    public int pinMessagesRow;
    /* access modifiers changed from: private */
    public int postMessagesRow;
    /* access modifiers changed from: private */
    public int rankHeaderRow;
    /* access modifiers changed from: private */
    public int rankInfoRow;
    /* access modifiers changed from: private */
    public int rankRow;
    /* access modifiers changed from: private */
    public int removeAdminRow;
    /* access modifiers changed from: private */
    public int removeAdminShadowRow;
    /* access modifiers changed from: private */
    public int rightsShadowRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int sendMediaRow;
    /* access modifiers changed from: private */
    public int sendMessagesRow;
    /* access modifiers changed from: private */
    public int sendPollsRow;
    /* access modifiers changed from: private */
    public int sendStickersRow;
    /* access modifiers changed from: private */
    public int startVoiceChatRow;
    /* access modifiers changed from: private */
    public int transferOwnerRow;
    /* access modifiers changed from: private */
    public int transferOwnerShadowRow;
    /* access modifiers changed from: private */
    public int untilDateRow;
    /* access modifiers changed from: private */
    public int untilSectionRow;

    public interface ChatRightsEditActivityDelegate {
        void didChangeOwner(TLRPC$User tLRPC$User);

        void didSetRights(int i, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createView$1(DialogInterface dialogInterface, int i) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createView$3(DialogInterface dialogInterface, int i) {
    }

    public ChatRightsEditActivity(long j, long j2, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2, String str, int i, boolean z, boolean z2) {
        this.isAddingNew = z2;
        this.chatId = j2;
        this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j));
        this.currentType = i;
        this.canEdit = z;
        TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.chatId));
        this.currentChat = chat;
        str = str == null ? "" : str;
        this.currentRank = str;
        this.initialRank = str;
        boolean z3 = true;
        if (chat != null) {
            this.isChannel = ChatObject.isChannel(chat) && !this.currentChat.megagroup;
            this.myAdminRights = this.currentChat.admin_rights;
        }
        if (this.myAdminRights == null) {
            TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2 = new TLRPC$TL_chatAdminRights();
            this.myAdminRights = tLRPC$TL_chatAdminRights2;
            tLRPC$TL_chatAdminRights2.manage_call = true;
            tLRPC$TL_chatAdminRights2.add_admins = true;
            tLRPC$TL_chatAdminRights2.pin_messages = true;
            tLRPC$TL_chatAdminRights2.invite_users = true;
            tLRPC$TL_chatAdminRights2.ban_users = true;
            tLRPC$TL_chatAdminRights2.delete_messages = true;
            tLRPC$TL_chatAdminRights2.edit_messages = true;
            tLRPC$TL_chatAdminRights2.post_messages = true;
            tLRPC$TL_chatAdminRights2.change_info = true;
        }
        if (i == 0) {
            TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights3 = new TLRPC$TL_chatAdminRights();
            this.adminRights = tLRPC$TL_chatAdminRights3;
            if (tLRPC$TL_chatAdminRights == null) {
                TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights4 = this.myAdminRights;
                tLRPC$TL_chatAdminRights3.change_info = tLRPC$TL_chatAdminRights4.change_info;
                tLRPC$TL_chatAdminRights3.post_messages = tLRPC$TL_chatAdminRights4.post_messages;
                tLRPC$TL_chatAdminRights3.edit_messages = tLRPC$TL_chatAdminRights4.edit_messages;
                tLRPC$TL_chatAdminRights3.delete_messages = tLRPC$TL_chatAdminRights4.delete_messages;
                tLRPC$TL_chatAdminRights3.manage_call = tLRPC$TL_chatAdminRights4.manage_call;
                tLRPC$TL_chatAdminRights3.ban_users = tLRPC$TL_chatAdminRights4.ban_users;
                tLRPC$TL_chatAdminRights3.invite_users = tLRPC$TL_chatAdminRights4.invite_users;
                tLRPC$TL_chatAdminRights3.pin_messages = tLRPC$TL_chatAdminRights4.pin_messages;
                this.initialIsSet = false;
            } else {
                boolean z4 = tLRPC$TL_chatAdminRights.change_info;
                tLRPC$TL_chatAdminRights3.change_info = z4;
                boolean z5 = tLRPC$TL_chatAdminRights.post_messages;
                tLRPC$TL_chatAdminRights3.post_messages = z5;
                boolean z6 = tLRPC$TL_chatAdminRights.edit_messages;
                tLRPC$TL_chatAdminRights3.edit_messages = z6;
                boolean z7 = tLRPC$TL_chatAdminRights.delete_messages;
                tLRPC$TL_chatAdminRights3.delete_messages = z7;
                boolean z8 = tLRPC$TL_chatAdminRights.manage_call;
                tLRPC$TL_chatAdminRights3.manage_call = z8;
                boolean z9 = tLRPC$TL_chatAdminRights.ban_users;
                tLRPC$TL_chatAdminRights3.ban_users = z9;
                boolean z10 = tLRPC$TL_chatAdminRights.invite_users;
                tLRPC$TL_chatAdminRights3.invite_users = z10;
                boolean z11 = tLRPC$TL_chatAdminRights.pin_messages;
                tLRPC$TL_chatAdminRights3.pin_messages = z11;
                boolean z12 = tLRPC$TL_chatAdminRights.add_admins;
                tLRPC$TL_chatAdminRights3.add_admins = z12;
                boolean z13 = tLRPC$TL_chatAdminRights.anonymous;
                tLRPC$TL_chatAdminRights3.anonymous = z13;
                if (!z4 && !z5 && !z6 && !z7 && !z9 && !z10 && !z11 && !z12 && !z8 && !z13) {
                    z3 = false;
                }
                this.initialIsSet = z3;
            }
        } else {
            this.defaultBannedRights = tLRPC$TL_chatBannedRights;
            if (tLRPC$TL_chatBannedRights == null) {
                TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights3 = new TLRPC$TL_chatBannedRights();
                this.defaultBannedRights = tLRPC$TL_chatBannedRights3;
                tLRPC$TL_chatBannedRights3.pin_messages = false;
                tLRPC$TL_chatBannedRights3.change_info = false;
                tLRPC$TL_chatBannedRights3.invite_users = false;
                tLRPC$TL_chatBannedRights3.send_polls = false;
                tLRPC$TL_chatBannedRights3.send_inline = false;
                tLRPC$TL_chatBannedRights3.send_games = false;
                tLRPC$TL_chatBannedRights3.send_gifs = false;
                tLRPC$TL_chatBannedRights3.send_stickers = false;
                tLRPC$TL_chatBannedRights3.embed_links = false;
                tLRPC$TL_chatBannedRights3.send_messages = false;
                tLRPC$TL_chatBannedRights3.send_media = false;
                tLRPC$TL_chatBannedRights3.view_messages = false;
            }
            TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights4 = new TLRPC$TL_chatBannedRights();
            this.bannedRights = tLRPC$TL_chatBannedRights4;
            if (tLRPC$TL_chatBannedRights2 == null) {
                tLRPC$TL_chatBannedRights4.pin_messages = false;
                tLRPC$TL_chatBannedRights4.change_info = false;
                tLRPC$TL_chatBannedRights4.invite_users = false;
                tLRPC$TL_chatBannedRights4.send_polls = false;
                tLRPC$TL_chatBannedRights4.send_inline = false;
                tLRPC$TL_chatBannedRights4.send_games = false;
                tLRPC$TL_chatBannedRights4.send_gifs = false;
                tLRPC$TL_chatBannedRights4.send_stickers = false;
                tLRPC$TL_chatBannedRights4.embed_links = false;
                tLRPC$TL_chatBannedRights4.send_messages = false;
                tLRPC$TL_chatBannedRights4.send_media = false;
                tLRPC$TL_chatBannedRights4.view_messages = false;
            } else {
                tLRPC$TL_chatBannedRights4.view_messages = tLRPC$TL_chatBannedRights2.view_messages;
                tLRPC$TL_chatBannedRights4.send_messages = tLRPC$TL_chatBannedRights2.send_messages;
                tLRPC$TL_chatBannedRights4.send_media = tLRPC$TL_chatBannedRights2.send_media;
                tLRPC$TL_chatBannedRights4.send_stickers = tLRPC$TL_chatBannedRights2.send_stickers;
                tLRPC$TL_chatBannedRights4.send_gifs = tLRPC$TL_chatBannedRights2.send_gifs;
                tLRPC$TL_chatBannedRights4.send_games = tLRPC$TL_chatBannedRights2.send_games;
                tLRPC$TL_chatBannedRights4.send_inline = tLRPC$TL_chatBannedRights2.send_inline;
                tLRPC$TL_chatBannedRights4.embed_links = tLRPC$TL_chatBannedRights2.embed_links;
                tLRPC$TL_chatBannedRights4.send_polls = tLRPC$TL_chatBannedRights2.send_polls;
                tLRPC$TL_chatBannedRights4.invite_users = tLRPC$TL_chatBannedRights2.invite_users;
                tLRPC$TL_chatBannedRights4.change_info = tLRPC$TL_chatBannedRights2.change_info;
                tLRPC$TL_chatBannedRights4.pin_messages = tLRPC$TL_chatBannedRights2.pin_messages;
                tLRPC$TL_chatBannedRights4.until_date = tLRPC$TL_chatBannedRights2.until_date;
            }
            TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights5 = this.defaultBannedRights;
            if (tLRPC$TL_chatBannedRights5.view_messages) {
                tLRPC$TL_chatBannedRights4.view_messages = true;
            }
            if (tLRPC$TL_chatBannedRights5.send_messages) {
                tLRPC$TL_chatBannedRights4.send_messages = true;
            }
            if (tLRPC$TL_chatBannedRights5.send_media) {
                tLRPC$TL_chatBannedRights4.send_media = true;
            }
            if (tLRPC$TL_chatBannedRights5.send_stickers) {
                tLRPC$TL_chatBannedRights4.send_stickers = true;
            }
            if (tLRPC$TL_chatBannedRights5.send_gifs) {
                tLRPC$TL_chatBannedRights4.send_gifs = true;
            }
            if (tLRPC$TL_chatBannedRights5.send_games) {
                tLRPC$TL_chatBannedRights4.send_games = true;
            }
            if (tLRPC$TL_chatBannedRights5.send_inline) {
                tLRPC$TL_chatBannedRights4.send_inline = true;
            }
            if (tLRPC$TL_chatBannedRights5.embed_links) {
                tLRPC$TL_chatBannedRights4.embed_links = true;
            }
            if (tLRPC$TL_chatBannedRights5.send_polls) {
                tLRPC$TL_chatBannedRights4.send_polls = true;
            }
            if (tLRPC$TL_chatBannedRights5.invite_users) {
                tLRPC$TL_chatBannedRights4.invite_users = true;
            }
            if (tLRPC$TL_chatBannedRights5.change_info) {
                tLRPC$TL_chatBannedRights4.change_info = true;
            }
            if (tLRPC$TL_chatBannedRights5.pin_messages) {
                tLRPC$TL_chatBannedRights4.pin_messages = true;
            }
            this.currentBannedRights = ChatObject.getBannedRightsString(tLRPC$TL_chatBannedRights4);
            if (tLRPC$TL_chatBannedRights2 != null && tLRPC$TL_chatBannedRights2.view_messages) {
                z3 = false;
            }
            this.initialIsSet = z3;
        }
        updateRows(false);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("EditAdmin", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("UserRestrictions", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (ChatRightsEditActivity.this.checkDiscard()) {
                        ChatRightsEditActivity.this.finishFragment();
                    }
                } else if (i == 1) {
                    ChatRightsEditActivity.this.onDonePressed();
                }
            }
        });
        if (this.canEdit || (!this.isChannel && this.currentChat.creator && UserObject.isUserSelf(this.currentUser))) {
            this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
        }
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        View view = this.fragmentView;
        FrameLayout frameLayout2 = (FrameLayout) view;
        view.setFocusableInTouchMode(true);
        this.listView = new RecyclerListView(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        RecyclerListView recyclerListView2 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView2.setVerticalScrollbarPosition(i);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(ChatRightsEditActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatRightsEditActivity$$ExternalSyntheticLambda17(this, context));
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(Context context, View view, int i) {
        String str;
        if (!this.canEdit && (!this.currentChat.creator || this.currentType != 0 || i != this.anonymousRow)) {
            return;
        }
        if (i == 0) {
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", this.currentUser.id);
            presentFragment(new ProfileActivity(bundle));
        } else if (i == this.removeAdminRow) {
            int i2 = this.currentType;
            if (i2 == 0) {
                MessagesController.getInstance(this.currentAccount).setUserAdminRole(this.chatId, this.currentUser, new TLRPC$TL_chatAdminRights(), this.currentRank, this.isChannel, getFragmentForAlert(0), this.isAddingNew);
                ChatRightsEditActivityDelegate chatRightsEditActivityDelegate = this.delegate;
                if (chatRightsEditActivityDelegate != null) {
                    chatRightsEditActivityDelegate.didSetRights(0, this.adminRights, this.bannedRights, this.currentRank);
                }
                finishFragment();
            } else if (i2 == 1) {
                TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights = new TLRPC$TL_chatBannedRights();
                this.bannedRights = tLRPC$TL_chatBannedRights;
                tLRPC$TL_chatBannedRights.view_messages = true;
                tLRPC$TL_chatBannedRights.send_media = true;
                tLRPC$TL_chatBannedRights.send_messages = true;
                tLRPC$TL_chatBannedRights.send_stickers = true;
                tLRPC$TL_chatBannedRights.send_gifs = true;
                tLRPC$TL_chatBannedRights.send_games = true;
                tLRPC$TL_chatBannedRights.send_inline = true;
                tLRPC$TL_chatBannedRights.embed_links = true;
                tLRPC$TL_chatBannedRights.pin_messages = true;
                tLRPC$TL_chatBannedRights.send_polls = true;
                tLRPC$TL_chatBannedRights.invite_users = true;
                tLRPC$TL_chatBannedRights.change_info = true;
                tLRPC$TL_chatBannedRights.until_date = 0;
                onDonePressed();
            }
        } else if (i == this.transferOwnerRow) {
            lambda$initTransfer$8((TLRPC$InputCheckPasswordSRP) null, (TwoStepVerificationActivity) null);
        } else if (i == this.untilDateRow) {
            if (getParentActivity() != null) {
                BottomSheet.Builder builder = new BottomSheet.Builder(context);
                builder.setApplyTopPadding(false);
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(1);
                HeaderCell headerCell = new HeaderCell(context, "dialogTextBlue2", 23, 15, false);
                headerCell.setHeight(47);
                headerCell.setText(LocaleController.getString("UserRestrictionsDuration", NUM));
                linearLayout.addView(headerCell);
                LinearLayout linearLayout2 = new LinearLayout(context);
                linearLayout2.setOrientation(1);
                linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
                BottomSheet.BottomSheetCell[] bottomSheetCellArr = new BottomSheet.BottomSheetCell[5];
                for (int i3 = 0; i3 < 5; i3++) {
                    bottomSheetCellArr[i3] = new BottomSheet.BottomSheetCell(context, 0);
                    bottomSheetCellArr[i3].setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
                    bottomSheetCellArr[i3].setTag(Integer.valueOf(i3));
                    bottomSheetCellArr[i3].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    if (i3 == 0) {
                        str = LocaleController.getString("UserRestrictionsUntilForever", NUM);
                    } else if (i3 == 1) {
                        str = LocaleController.formatPluralString("Days", 1);
                    } else if (i3 == 2) {
                        str = LocaleController.formatPluralString("Weeks", 1);
                    } else if (i3 != 3) {
                        str = LocaleController.getString("UserRestrictionsCustom", NUM);
                    } else {
                        str = LocaleController.formatPluralString("Months", 1);
                    }
                    bottomSheetCellArr[i3].setTextAndIcon((CharSequence) str, 0);
                    linearLayout2.addView(bottomSheetCellArr[i3], LayoutHelper.createLinear(-1, -2));
                    bottomSheetCellArr[i3].setOnClickListener(new ChatRightsEditActivity$$ExternalSyntheticLambda9(this, builder));
                }
                builder.setCustomView(linearLayout);
                showDialog(builder.create());
            }
        } else if (view instanceof TextCheckCell2) {
            TextCheckCell2 textCheckCell2 = (TextCheckCell2) view;
            if (textCheckCell2.hasIcon()) {
                Toast.makeText(getParentActivity(), LocaleController.getString("UserRestrictionsDisabled", NUM), 0).show();
            } else if (textCheckCell2.isEnabled()) {
                textCheckCell2.setChecked(!textCheckCell2.isChecked());
                if (i == this.changeInfoRow) {
                    if (this.currentType == 0) {
                        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = this.adminRights;
                        tLRPC$TL_chatAdminRights.change_info = !tLRPC$TL_chatAdminRights.change_info;
                    } else {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2 = this.bannedRights;
                        tLRPC$TL_chatBannedRights2.change_info = !tLRPC$TL_chatBannedRights2.change_info;
                    }
                } else if (i == this.postMessagesRow) {
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2 = this.adminRights;
                    tLRPC$TL_chatAdminRights2.post_messages = !tLRPC$TL_chatAdminRights2.post_messages;
                } else if (i == this.editMesagesRow) {
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights3 = this.adminRights;
                    tLRPC$TL_chatAdminRights3.edit_messages = !tLRPC$TL_chatAdminRights3.edit_messages;
                } else if (i == this.deleteMessagesRow) {
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights4 = this.adminRights;
                    tLRPC$TL_chatAdminRights4.delete_messages = !tLRPC$TL_chatAdminRights4.delete_messages;
                } else if (i == this.addAdminsRow) {
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights5 = this.adminRights;
                    tLRPC$TL_chatAdminRights5.add_admins = !tLRPC$TL_chatAdminRights5.add_admins;
                } else if (i == this.anonymousRow) {
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights6 = this.adminRights;
                    tLRPC$TL_chatAdminRights6.anonymous = !tLRPC$TL_chatAdminRights6.anonymous;
                } else if (i == this.banUsersRow) {
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights7 = this.adminRights;
                    tLRPC$TL_chatAdminRights7.ban_users = !tLRPC$TL_chatAdminRights7.ban_users;
                } else if (i == this.startVoiceChatRow) {
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights8 = this.adminRights;
                    tLRPC$TL_chatAdminRights8.manage_call = !tLRPC$TL_chatAdminRights8.manage_call;
                } else if (i == this.addUsersRow) {
                    if (this.currentType == 0) {
                        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights9 = this.adminRights;
                        tLRPC$TL_chatAdminRights9.invite_users = !tLRPC$TL_chatAdminRights9.invite_users;
                    } else {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights3 = this.bannedRights;
                        tLRPC$TL_chatBannedRights3.invite_users = !tLRPC$TL_chatBannedRights3.invite_users;
                    }
                } else if (i == this.pinMessagesRow) {
                    if (this.currentType == 0) {
                        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights10 = this.adminRights;
                        tLRPC$TL_chatAdminRights10.pin_messages = !tLRPC$TL_chatAdminRights10.pin_messages;
                    } else {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights4 = this.bannedRights;
                        tLRPC$TL_chatBannedRights4.pin_messages = !tLRPC$TL_chatBannedRights4.pin_messages;
                    }
                } else if (this.bannedRights != null) {
                    boolean z = !textCheckCell2.isChecked();
                    int i4 = this.sendMessagesRow;
                    if (i == i4) {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights5 = this.bannedRights;
                        tLRPC$TL_chatBannedRights5.send_messages = !tLRPC$TL_chatBannedRights5.send_messages;
                    } else if (i == this.sendMediaRow) {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights6 = this.bannedRights;
                        tLRPC$TL_chatBannedRights6.send_media = !tLRPC$TL_chatBannedRights6.send_media;
                    } else if (i == this.sendStickersRow) {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights7 = this.bannedRights;
                        boolean z2 = !tLRPC$TL_chatBannedRights7.send_stickers;
                        tLRPC$TL_chatBannedRights7.send_inline = z2;
                        tLRPC$TL_chatBannedRights7.send_gifs = z2;
                        tLRPC$TL_chatBannedRights7.send_games = z2;
                        tLRPC$TL_chatBannedRights7.send_stickers = z2;
                    } else if (i == this.embedLinksRow) {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights8 = this.bannedRights;
                        tLRPC$TL_chatBannedRights8.embed_links = !tLRPC$TL_chatBannedRights8.embed_links;
                    } else if (i == this.sendPollsRow) {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights9 = this.bannedRights;
                        tLRPC$TL_chatBannedRights9.send_polls = !tLRPC$TL_chatBannedRights9.send_polls;
                    }
                    if (z) {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights10 = this.bannedRights;
                        if (tLRPC$TL_chatBannedRights10.view_messages && !tLRPC$TL_chatBannedRights10.send_messages) {
                            tLRPC$TL_chatBannedRights10.send_messages = true;
                            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i4);
                            if (findViewHolderForAdapterPosition != null) {
                                ((TextCheckCell2) findViewHolderForAdapterPosition.itemView).setChecked(false);
                            }
                        }
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights11 = this.bannedRights;
                        if ((tLRPC$TL_chatBannedRights11.view_messages || tLRPC$TL_chatBannedRights11.send_messages) && !tLRPC$TL_chatBannedRights11.send_media) {
                            tLRPC$TL_chatBannedRights11.send_media = true;
                            RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = this.listView.findViewHolderForAdapterPosition(this.sendMediaRow);
                            if (findViewHolderForAdapterPosition2 != null) {
                                ((TextCheckCell2) findViewHolderForAdapterPosition2.itemView).setChecked(false);
                            }
                        }
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights12 = this.bannedRights;
                        if ((tLRPC$TL_chatBannedRights12.view_messages || tLRPC$TL_chatBannedRights12.send_messages) && !tLRPC$TL_chatBannedRights12.send_polls) {
                            tLRPC$TL_chatBannedRights12.send_polls = true;
                            RecyclerView.ViewHolder findViewHolderForAdapterPosition3 = this.listView.findViewHolderForAdapterPosition(this.sendPollsRow);
                            if (findViewHolderForAdapterPosition3 != null) {
                                ((TextCheckCell2) findViewHolderForAdapterPosition3.itemView).setChecked(false);
                            }
                        }
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights13 = this.bannedRights;
                        if ((tLRPC$TL_chatBannedRights13.view_messages || tLRPC$TL_chatBannedRights13.send_messages) && !tLRPC$TL_chatBannedRights13.send_stickers) {
                            tLRPC$TL_chatBannedRights13.send_inline = true;
                            tLRPC$TL_chatBannedRights13.send_gifs = true;
                            tLRPC$TL_chatBannedRights13.send_games = true;
                            tLRPC$TL_chatBannedRights13.send_stickers = true;
                            RecyclerView.ViewHolder findViewHolderForAdapterPosition4 = this.listView.findViewHolderForAdapterPosition(this.sendStickersRow);
                            if (findViewHolderForAdapterPosition4 != null) {
                                ((TextCheckCell2) findViewHolderForAdapterPosition4.itemView).setChecked(false);
                            }
                        }
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights14 = this.bannedRights;
                        if ((tLRPC$TL_chatBannedRights14.view_messages || tLRPC$TL_chatBannedRights14.send_messages) && !tLRPC$TL_chatBannedRights14.embed_links) {
                            tLRPC$TL_chatBannedRights14.embed_links = true;
                            RecyclerView.ViewHolder findViewHolderForAdapterPosition5 = this.listView.findViewHolderForAdapterPosition(this.embedLinksRow);
                            if (findViewHolderForAdapterPosition5 != null) {
                                ((TextCheckCell2) findViewHolderForAdapterPosition5.itemView).setChecked(false);
                            }
                        }
                    } else {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights15 = this.bannedRights;
                        boolean z3 = tLRPC$TL_chatBannedRights15.send_messages;
                        if ((!z3 || !tLRPC$TL_chatBannedRights15.embed_links || !tLRPC$TL_chatBannedRights15.send_inline || !tLRPC$TL_chatBannedRights15.send_media || !tLRPC$TL_chatBannedRights15.send_polls) && tLRPC$TL_chatBannedRights15.view_messages) {
                            tLRPC$TL_chatBannedRights15.view_messages = false;
                        }
                        if ((!tLRPC$TL_chatBannedRights15.embed_links || !tLRPC$TL_chatBannedRights15.send_inline || !tLRPC$TL_chatBannedRights15.send_media || !tLRPC$TL_chatBannedRights15.send_polls) && z3) {
                            tLRPC$TL_chatBannedRights15.send_messages = false;
                            RecyclerView.ViewHolder findViewHolderForAdapterPosition6 = this.listView.findViewHolderForAdapterPosition(i4);
                            if (findViewHolderForAdapterPosition6 != null) {
                                ((TextCheckCell2) findViewHolderForAdapterPosition6.itemView).setChecked(true);
                            }
                        }
                    }
                }
                updateRows(true);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(BottomSheet.Builder builder, View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        if (intValue == 0) {
            this.bannedRights.until_date = 0;
            this.listViewAdapter.notifyItemChanged(this.untilDateRow);
        } else if (intValue == 1) {
            this.bannedRights.until_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 86400;
            this.listViewAdapter.notifyItemChanged(this.untilDateRow);
        } else if (intValue == 2) {
            this.bannedRights.until_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 604800;
            this.listViewAdapter.notifyItemChanged(this.untilDateRow);
        } else if (intValue == 3) {
            this.bannedRights.until_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 2592000;
            this.listViewAdapter.notifyItemChanged(this.untilDateRow);
        } else if (intValue == 4) {
            Calendar instance = Calendar.getInstance();
            try {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getParentActivity(), new ChatRightsEditActivity$$ExternalSyntheticLambda0(this), instance.get(1), instance.get(2), instance.get(5));
                DatePicker datePicker = datePickerDialog.getDatePicker();
                Calendar instance2 = Calendar.getInstance();
                instance2.setTimeInMillis(System.currentTimeMillis());
                instance2.set(11, instance2.getMinimum(11));
                instance2.set(12, instance2.getMinimum(12));
                instance2.set(13, instance2.getMinimum(13));
                instance2.set(14, instance2.getMinimum(14));
                datePicker.setMinDate(instance2.getTimeInMillis());
                instance2.setTimeInMillis(System.currentTimeMillis() + 31536000000L);
                instance2.set(11, instance2.getMaximum(11));
                instance2.set(12, instance2.getMaximum(12));
                instance2.set(13, instance2.getMaximum(13));
                instance2.set(14, instance2.getMaximum(14));
                datePicker.setMaxDate(instance2.getTimeInMillis());
                datePickerDialog.setButton(-1, LocaleController.getString("Set", NUM), datePickerDialog);
                datePickerDialog.setButton(-2, LocaleController.getString("Cancel", NUM), ChatRightsEditActivity$$ExternalSyntheticLambda7.INSTANCE);
                if (Build.VERSION.SDK_INT >= 21) {
                    datePickerDialog.setOnShowListener(new ChatRightsEditActivity$$ExternalSyntheticLambda8(datePicker));
                }
                showDialog(datePickerDialog);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        builder.getDismissRunnable().run();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(DatePicker datePicker, int i, int i2, int i3) {
        Calendar instance = Calendar.getInstance();
        instance.clear();
        instance.set(i, i2, i3);
        try {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getParentActivity(), new ChatRightsEditActivity$$ExternalSyntheticLambda1(this, (int) (instance.getTime().getTime() / 1000)), 0, 0, true);
            timePickerDialog.setButton(-1, LocaleController.getString("Set", NUM), timePickerDialog);
            timePickerDialog.setButton(-2, LocaleController.getString("Cancel", NUM), ChatRightsEditActivity$$ExternalSyntheticLambda6.INSTANCE);
            showDialog(timePickerDialog);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(int i, TimePicker timePicker, int i2, int i3) {
        this.bannedRights.until_date = i + (i2 * 3600) + (i3 * 60);
        this.listViewAdapter.notifyItemChanged(this.untilDateRow);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createView$4(DatePicker datePicker, DialogInterface dialogInterface) {
        int childCount = datePicker.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = datePicker.getChildAt(i);
            ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
            layoutParams.width = -1;
            childAt.setLayoutParams(layoutParams);
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    private boolean isDefaultAdminRights() {
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = this.adminRights;
        boolean z = tLRPC$TL_chatAdminRights.change_info;
        return (z && tLRPC$TL_chatAdminRights.delete_messages && tLRPC$TL_chatAdminRights.ban_users && tLRPC$TL_chatAdminRights.invite_users && tLRPC$TL_chatAdminRights.pin_messages && tLRPC$TL_chatAdminRights.manage_call && !tLRPC$TL_chatAdminRights.add_admins && !tLRPC$TL_chatAdminRights.anonymous) || (!z && !tLRPC$TL_chatAdminRights.delete_messages && !tLRPC$TL_chatAdminRights.ban_users && !tLRPC$TL_chatAdminRights.invite_users && !tLRPC$TL_chatAdminRights.pin_messages && !tLRPC$TL_chatAdminRights.manage_call && !tLRPC$TL_chatAdminRights.add_admins && !tLRPC$TL_chatAdminRights.anonymous);
    }

    private boolean hasAllAdminRights() {
        if (this.isChannel) {
            TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = this.adminRights;
            if (!tLRPC$TL_chatAdminRights.change_info || !tLRPC$TL_chatAdminRights.post_messages || !tLRPC$TL_chatAdminRights.edit_messages || !tLRPC$TL_chatAdminRights.delete_messages || !tLRPC$TL_chatAdminRights.invite_users || !tLRPC$TL_chatAdminRights.add_admins || !tLRPC$TL_chatAdminRights.manage_call) {
                return false;
            }
            return true;
        }
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2 = this.adminRights;
        if (!tLRPC$TL_chatAdminRights2.change_info || !tLRPC$TL_chatAdminRights2.delete_messages || !tLRPC$TL_chatAdminRights2.ban_users || !tLRPC$TL_chatAdminRights2.invite_users || !tLRPC$TL_chatAdminRights2.pin_messages || !tLRPC$TL_chatAdminRights2.add_admins || !tLRPC$TL_chatAdminRights2.manage_call) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: initTransfer */
    public void lambda$initTransfer$8(TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP, TwoStepVerificationActivity twoStepVerificationActivity) {
        TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP2;
        if (getParentActivity() != null) {
            if (tLRPC$InputCheckPasswordSRP == null || ChatObject.isChannel(this.currentChat)) {
                TLRPC$TL_channels_editCreator tLRPC$TL_channels_editCreator = new TLRPC$TL_channels_editCreator();
                if (ChatObject.isChannel(this.currentChat)) {
                    TLRPC$TL_inputChannel tLRPC$TL_inputChannel = new TLRPC$TL_inputChannel();
                    tLRPC$TL_channels_editCreator.channel = tLRPC$TL_inputChannel;
                    TLRPC$Chat tLRPC$Chat = this.currentChat;
                    tLRPC$TL_inputChannel.channel_id = tLRPC$Chat.id;
                    tLRPC$TL_inputChannel.access_hash = tLRPC$Chat.access_hash;
                } else {
                    tLRPC$TL_channels_editCreator.channel = new TLRPC$TL_inputChannelEmpty();
                }
                if (tLRPC$InputCheckPasswordSRP != null) {
                    tLRPC$InputCheckPasswordSRP2 = tLRPC$InputCheckPasswordSRP;
                } else {
                    tLRPC$InputCheckPasswordSRP2 = new TLRPC$TL_inputCheckPasswordEmpty();
                }
                tLRPC$TL_channels_editCreator.password = tLRPC$InputCheckPasswordSRP2;
                tLRPC$TL_channels_editCreator.user_id = getMessagesController().getInputUser(this.currentUser);
                getConnectionsManager().sendRequest(tLRPC$TL_channels_editCreator, new ChatRightsEditActivity$$ExternalSyntheticLambda14(this, tLRPC$InputCheckPasswordSRP, twoStepVerificationActivity, tLRPC$TL_channels_editCreator));
                return;
            }
            MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chatId, this, new ChatRightsEditActivity$$ExternalSyntheticLambda13(this, tLRPC$InputCheckPasswordSRP, twoStepVerificationActivity));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initTransfer$7(TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP, TwoStepVerificationActivity twoStepVerificationActivity, long j) {
        if (j != 0) {
            this.chatId = j;
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(j));
            lambda$initTransfer$8(tLRPC$InputCheckPasswordSRP, twoStepVerificationActivity);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initTransfer$14(TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP, TwoStepVerificationActivity twoStepVerificationActivity, TLRPC$TL_channels_editCreator tLRPC$TL_channels_editCreator, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ChatRightsEditActivity$$ExternalSyntheticLambda11(this, tLRPC$TL_error, tLRPC$InputCheckPasswordSRP, twoStepVerificationActivity, tLRPC$TL_channels_editCreator));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initTransfer$13(TLRPC$TL_error tLRPC$TL_error, TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP, TwoStepVerificationActivity twoStepVerificationActivity, TLRPC$TL_channels_editCreator tLRPC$TL_channels_editCreator) {
        int i;
        TLRPC$TL_error tLRPC$TL_error2 = tLRPC$TL_error;
        TwoStepVerificationActivity twoStepVerificationActivity2 = twoStepVerificationActivity;
        if (tLRPC$TL_error2 != null) {
            if (getParentActivity() != null) {
                if ("PASSWORD_HASH_INVALID".equals(tLRPC$TL_error2.text)) {
                    if (tLRPC$InputCheckPasswordSRP == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                        if (this.isChannel) {
                            builder.setTitle(LocaleController.getString("EditAdminChannelTransfer", NUM));
                        } else {
                            builder.setTitle(LocaleController.getString("EditAdminGroupTransfer", NUM));
                        }
                        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("EditAdminTransferReadyAlertText", NUM, this.currentChat.title, UserObject.getFirstName(this.currentUser))));
                        builder.setPositiveButton(LocaleController.getString("EditAdminTransferChangeOwner", NUM), new ChatRightsEditActivity$$ExternalSyntheticLambda3(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        showDialog(builder.create());
                    }
                } else if ("PASSWORD_MISSING".equals(tLRPC$TL_error2.text) || tLRPC$TL_error2.text.startsWith("PASSWORD_TOO_FRESH_") || tLRPC$TL_error2.text.startsWith("SESSION_TOO_FRESH_")) {
                    if (twoStepVerificationActivity2 != null) {
                        twoStepVerificationActivity.needHideProgress();
                    }
                    AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                    builder2.setTitle(LocaleController.getString("EditAdminTransferAlertTitle", NUM));
                    LinearLayout linearLayout = new LinearLayout(getParentActivity());
                    linearLayout.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(24.0f), 0);
                    linearLayout.setOrientation(1);
                    builder2.setView(linearLayout);
                    TextView textView = new TextView(getParentActivity());
                    textView.setTextColor(Theme.getColor("dialogTextBlack"));
                    textView.setTextSize(1, 16.0f);
                    textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                    if (this.isChannel) {
                        textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EditChannelAdminTransferAlertText", NUM, UserObject.getFirstName(this.currentUser))));
                    } else {
                        textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EditAdminTransferAlertText", NUM, UserObject.getFirstName(this.currentUser))));
                    }
                    linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
                    LinearLayout linearLayout2 = new LinearLayout(getParentActivity());
                    linearLayout2.setOrientation(0);
                    linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
                    ImageView imageView = new ImageView(getParentActivity());
                    imageView.setImageResource(NUM);
                    imageView.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(11.0f) : 0, AndroidUtilities.dp(9.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(11.0f), 0);
                    imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogTextBlack"), PorterDuff.Mode.MULTIPLY));
                    TextView textView2 = new TextView(getParentActivity());
                    textView2.setTextColor(Theme.getColor("dialogTextBlack"));
                    textView2.setTextSize(1, 16.0f);
                    textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                    textView2.setText(AndroidUtilities.replaceTags(LocaleController.getString("EditAdminTransferAlertText1", NUM)));
                    if (LocaleController.isRTL) {
                        linearLayout2.addView(textView2, LayoutHelper.createLinear(-1, -2));
                        linearLayout2.addView(imageView, LayoutHelper.createLinear(-2, -2, 5));
                    } else {
                        linearLayout2.addView(imageView, LayoutHelper.createLinear(-2, -2));
                        linearLayout2.addView(textView2, LayoutHelper.createLinear(-1, -2));
                    }
                    LinearLayout linearLayout3 = new LinearLayout(getParentActivity());
                    linearLayout3.setOrientation(0);
                    linearLayout.addView(linearLayout3, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
                    ImageView imageView2 = new ImageView(getParentActivity());
                    imageView2.setImageResource(NUM);
                    imageView2.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(11.0f) : 0, AndroidUtilities.dp(9.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(11.0f), 0);
                    imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogTextBlack"), PorterDuff.Mode.MULTIPLY));
                    TextView textView3 = new TextView(getParentActivity());
                    textView3.setTextColor(Theme.getColor("dialogTextBlack"));
                    textView3.setTextSize(1, 16.0f);
                    textView3.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                    textView3.setText(AndroidUtilities.replaceTags(LocaleController.getString("EditAdminTransferAlertText2", NUM)));
                    if (LocaleController.isRTL) {
                        linearLayout3.addView(textView3, LayoutHelper.createLinear(-1, -2));
                        i = 5;
                        linearLayout3.addView(imageView2, LayoutHelper.createLinear(-2, -2, 5));
                    } else {
                        i = 5;
                        linearLayout3.addView(imageView2, LayoutHelper.createLinear(-2, -2));
                        linearLayout3.addView(textView3, LayoutHelper.createLinear(-1, -2));
                    }
                    if ("PASSWORD_MISSING".equals(tLRPC$TL_error2.text)) {
                        builder2.setPositiveButton(LocaleController.getString("EditAdminTransferSetPassword", NUM), new ChatRightsEditActivity$$ExternalSyntheticLambda4(this));
                        builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    } else {
                        TextView textView4 = new TextView(getParentActivity());
                        textView4.setTextColor(Theme.getColor("dialogTextBlack"));
                        textView4.setTextSize(1, 16.0f);
                        if (!LocaleController.isRTL) {
                            i = 3;
                        }
                        textView4.setGravity(i | 48);
                        textView4.setText(LocaleController.getString("EditAdminTransferAlertText3", NUM));
                        linearLayout.addView(textView4, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
                        builder2.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                    }
                    showDialog(builder2.create());
                } else if ("SRP_ID_INVALID".equals(tLRPC$TL_error2.text)) {
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new ChatRightsEditActivity$$ExternalSyntheticLambda15(this, twoStepVerificationActivity2), 8);
                } else if (tLRPC$TL_error2.text.equals("CHANNELS_TOO_MUCH")) {
                    presentFragment(new TooManyCommunitiesActivity(1));
                } else {
                    if (twoStepVerificationActivity2 != null) {
                        twoStepVerificationActivity.needHideProgress();
                        twoStepVerificationActivity.finishFragment();
                    }
                    AlertsCreator.showAddUserAlert(tLRPC$TL_error2.text, this, this.isChannel, tLRPC$TL_channels_editCreator);
                }
            }
        } else if (tLRPC$InputCheckPasswordSRP != null) {
            this.delegate.didChangeOwner(this.currentUser);
            removeSelfFromStack();
            twoStepVerificationActivity.needHideProgress();
            twoStepVerificationActivity.finishFragment();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initTransfer$9(DialogInterface dialogInterface, int i) {
        TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
        twoStepVerificationActivity.setDelegate(new ChatRightsEditActivity$$ExternalSyntheticLambda18(this, twoStepVerificationActivity));
        presentFragment(twoStepVerificationActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initTransfer$10(DialogInterface dialogInterface, int i) {
        presentFragment(new TwoStepVerificationSetupActivity(6, (TLRPC$TL_account_password) null));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initTransfer$12(TwoStepVerificationActivity twoStepVerificationActivity, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ChatRightsEditActivity$$ExternalSyntheticLambda10(this, tLRPC$TL_error, tLObject, twoStepVerificationActivity));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initTransfer$11(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TwoStepVerificationActivity twoStepVerificationActivity) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            twoStepVerificationActivity.setCurrentPasswordInfo((byte[]) null, tLRPC$TL_account_password);
            TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$TL_account_password);
            lambda$initTransfer$8(twoStepVerificationActivity.getNewSrpPassword(), twoStepVerificationActivity);
        }
    }

    private void updateRows(boolean z) {
        int i;
        int min = Math.min(this.transferOwnerShadowRow, this.transferOwnerRow);
        this.changeInfoRow = -1;
        this.postMessagesRow = -1;
        this.editMesagesRow = -1;
        this.deleteMessagesRow = -1;
        this.addAdminsRow = -1;
        this.anonymousRow = -1;
        this.banUsersRow = -1;
        this.addUsersRow = -1;
        this.pinMessagesRow = -1;
        this.rightsShadowRow = -1;
        this.removeAdminRow = -1;
        this.removeAdminShadowRow = -1;
        this.cantEditInfoRow = -1;
        this.transferOwnerShadowRow = -1;
        this.transferOwnerRow = -1;
        this.rankHeaderRow = -1;
        this.rankRow = -1;
        this.rankInfoRow = -1;
        this.sendMessagesRow = -1;
        this.sendMediaRow = -1;
        this.sendStickersRow = -1;
        this.sendPollsRow = -1;
        this.embedLinksRow = -1;
        this.startVoiceChatRow = -1;
        this.untilSectionRow = -1;
        this.untilDateRow = -1;
        this.rowCount = 3;
        int i2 = this.currentType;
        if (i2 == 0) {
            if (this.isChannel) {
                int i3 = 3 + 1;
                this.rowCount = i3;
                this.changeInfoRow = 3;
                int i4 = i3 + 1;
                this.rowCount = i4;
                this.postMessagesRow = i3;
                int i5 = i4 + 1;
                this.rowCount = i5;
                this.editMesagesRow = i4;
                int i6 = i5 + 1;
                this.rowCount = i6;
                this.deleteMessagesRow = i5;
                int i7 = i6 + 1;
                this.rowCount = i7;
                this.addUsersRow = i6;
                int i8 = i7 + 1;
                this.rowCount = i8;
                this.startVoiceChatRow = i7;
                this.rowCount = i8 + 1;
                this.addAdminsRow = i8;
            } else {
                int i9 = 3 + 1;
                this.rowCount = i9;
                this.changeInfoRow = 3;
                int i10 = i9 + 1;
                this.rowCount = i10;
                this.deleteMessagesRow = i9;
                int i11 = i10 + 1;
                this.rowCount = i11;
                this.banUsersRow = i10;
                int i12 = i11 + 1;
                this.rowCount = i12;
                this.addUsersRow = i11;
                int i13 = i12 + 1;
                this.rowCount = i13;
                this.pinMessagesRow = i12;
                int i14 = i13 + 1;
                this.rowCount = i14;
                this.startVoiceChatRow = i13;
                int i15 = i14 + 1;
                this.rowCount = i15;
                this.addAdminsRow = i14;
                this.rowCount = i15 + 1;
                this.anonymousRow = i15;
            }
        } else if (i2 == 1) {
            int i16 = 3 + 1;
            this.rowCount = i16;
            this.sendMessagesRow = 3;
            int i17 = i16 + 1;
            this.rowCount = i17;
            this.sendMediaRow = i16;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.sendStickersRow = i17;
            int i19 = i18 + 1;
            this.rowCount = i19;
            this.sendPollsRow = i18;
            int i20 = i19 + 1;
            this.rowCount = i20;
            this.embedLinksRow = i19;
            int i21 = i20 + 1;
            this.rowCount = i21;
            this.addUsersRow = i20;
            int i22 = i21 + 1;
            this.rowCount = i22;
            this.pinMessagesRow = i21;
            int i23 = i22 + 1;
            this.rowCount = i23;
            this.changeInfoRow = i22;
            int i24 = i23 + 1;
            this.rowCount = i24;
            this.untilSectionRow = i23;
            this.rowCount = i24 + 1;
            this.untilDateRow = i24;
        }
        if (this.canEdit) {
            if (!this.isChannel && i2 == 0) {
                int i25 = this.rowCount;
                int i26 = i25 + 1;
                this.rowCount = i26;
                this.rightsShadowRow = i25;
                int i27 = i26 + 1;
                this.rowCount = i27;
                this.rankHeaderRow = i26;
                int i28 = i27 + 1;
                this.rowCount = i28;
                this.rankRow = i27;
                this.rowCount = i28 + 1;
                this.rankInfoRow = i28;
            }
            TLRPC$Chat tLRPC$Chat = this.currentChat;
            if (tLRPC$Chat != null && tLRPC$Chat.creator && i2 == 0 && hasAllAdminRights() && !this.currentUser.bot) {
                int i29 = this.rightsShadowRow;
                if (i29 == -1) {
                    int i30 = this.rowCount;
                    this.rowCount = i30 + 1;
                    this.transferOwnerShadowRow = i30;
                }
                int i31 = this.rowCount;
                int i32 = i31 + 1;
                this.rowCount = i32;
                this.transferOwnerRow = i31;
                if (i29 != -1) {
                    this.rowCount = i32 + 1;
                    this.transferOwnerShadowRow = i32;
                }
            }
            if (this.initialIsSet) {
                if (this.rightsShadowRow == -1) {
                    int i33 = this.rowCount;
                    this.rowCount = i33 + 1;
                    this.rightsShadowRow = i33;
                }
                int i34 = this.rowCount;
                int i35 = i34 + 1;
                this.rowCount = i35;
                this.removeAdminRow = i34;
                this.rowCount = i35 + 1;
                this.removeAdminShadowRow = i35;
            }
        } else if (i2 != 0) {
            int i36 = this.rowCount;
            this.rowCount = i36 + 1;
            this.rightsShadowRow = i36;
        } else if (this.isChannel || i2 != 0 || (this.currentRank.isEmpty() && (!this.currentChat.creator || !UserObject.isUserSelf(this.currentUser)))) {
            int i37 = this.rowCount;
            this.rowCount = i37 + 1;
            this.cantEditInfoRow = i37;
        } else {
            int i38 = this.rowCount;
            int i39 = i38 + 1;
            this.rowCount = i39;
            this.rightsShadowRow = i38;
            int i40 = i39 + 1;
            this.rowCount = i40;
            this.rankHeaderRow = i39;
            this.rowCount = i40 + 1;
            this.rankRow = i40;
            if (!this.currentChat.creator || !UserObject.isUserSelf(this.currentUser)) {
                int i41 = this.rowCount;
                this.rowCount = i41 + 1;
                this.cantEditInfoRow = i41;
            } else {
                int i42 = this.rowCount;
                this.rowCount = i42 + 1;
                this.rankInfoRow = i42;
            }
        }
        if (!z) {
            return;
        }
        if (min == -1 && (i = this.transferOwnerShadowRow) != -1) {
            this.listViewAdapter.notifyItemRangeInserted(Math.min(i, this.transferOwnerRow), 2);
        } else if (min != -1 && this.transferOwnerShadowRow == -1) {
            this.listViewAdapter.notifyItemRangeRemoved(min, 2);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0027, code lost:
        if (r0.codePointCount(0, r0.length()) > 16) goto L_0x0029;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDonePressed() {
        /*
            r14 = this;
            org.telegram.tgnet.TLRPC$Chat r0 = r14.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            r1 = 16
            r2 = -1
            r3 = 1
            r4 = 0
            if (r0 != 0) goto L_0x003f
            int r0 = r14.currentType
            if (r0 == r3) goto L_0x0029
            if (r0 != 0) goto L_0x003f
            boolean r0 = r14.isDefaultAdminRights()
            if (r0 == 0) goto L_0x0029
            int r0 = r14.rankRow
            if (r0 == r2) goto L_0x003f
            java.lang.String r0 = r14.currentRank
            int r5 = r0.length()
            int r0 = r0.codePointCount(r4, r5)
            if (r0 <= r1) goto L_0x003f
        L_0x0029:
            int r0 = r14.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r0)
            android.app.Activity r2 = r14.getParentActivity()
            long r3 = r14.chatId
            org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda12 r6 = new org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda12
            r6.<init>(r14)
            r5 = r14
            r1.convertToMegaGroup(r2, r3, r5, r6)
            return
        L_0x003f:
            int r0 = r14.currentType
            if (r0 != 0) goto L_0x0114
            int r0 = r14.rankRow
            if (r0 == r2) goto L_0x007f
            java.lang.String r0 = r14.currentRank
            int r2 = r0.length()
            int r0 = r0.codePointCount(r4, r2)
            if (r0 <= r1) goto L_0x007f
            org.telegram.ui.Components.RecyclerListView r0 = r14.listView
            int r1 = r14.rankRow
            r0.smoothScrollToPosition(r1)
            android.app.Activity r0 = r14.getParentActivity()
            java.lang.String r1 = "vibrator"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.os.Vibrator r0 = (android.os.Vibrator) r0
            if (r0 == 0) goto L_0x006d
            r1 = 200(0xc8, double:9.9E-322)
            r0.vibrate(r1)
        L_0x006d:
            org.telegram.ui.Components.RecyclerListView r0 = r14.listView
            int r1 = r14.rankHeaderRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x007e
            android.view.View r0 = r0.itemView
            r1 = 1073741824(0x40000000, float:2.0)
            org.telegram.messenger.AndroidUtilities.shakeView(r0, r1, r4)
        L_0x007e:
            return
        L_0x007f:
            boolean r0 = r14.isChannel
            if (r0 == 0) goto L_0x008a
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r14.adminRights
            r0.ban_users = r4
            r0.pin_messages = r4
            goto L_0x0090
        L_0x008a:
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r14.adminRights
            r0.edit_messages = r4
            r0.post_messages = r4
        L_0x0090:
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r14.adminRights
            boolean r1 = r0.change_info
            if (r1 != 0) goto L_0x00bd
            boolean r1 = r0.post_messages
            if (r1 != 0) goto L_0x00bd
            boolean r1 = r0.edit_messages
            if (r1 != 0) goto L_0x00bd
            boolean r1 = r0.delete_messages
            if (r1 != 0) goto L_0x00bd
            boolean r1 = r0.ban_users
            if (r1 != 0) goto L_0x00bd
            boolean r1 = r0.invite_users
            if (r1 != 0) goto L_0x00bd
            boolean r1 = r0.pin_messages
            if (r1 != 0) goto L_0x00bd
            boolean r1 = r0.add_admins
            if (r1 != 0) goto L_0x00bd
            boolean r1 = r0.anonymous
            if (r1 != 0) goto L_0x00bd
            boolean r1 = r0.manage_call
            if (r1 != 0) goto L_0x00bd
            r0.other = r3
            goto L_0x00bf
        L_0x00bd:
            r0.other = r4
        L_0x00bf:
            int r0 = r14.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r6 = r14.chatId
            org.telegram.tgnet.TLRPC$User r8 = r14.currentUser
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r9 = r14.adminRights
            java.lang.String r10 = r14.currentRank
            boolean r11 = r14.isChannel
            org.telegram.ui.ActionBar.BaseFragment r12 = r14.getFragmentForAlert(r3)
            boolean r13 = r14.isAddingNew
            r5.setUserAdminRole(r6, r8, r9, r10, r11, r12, r13)
            org.telegram.ui.ChatRightsEditActivity$ChatRightsEditActivityDelegate r0 = r14.delegate
            if (r0 == 0) goto L_0x0159
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r14.adminRights
            boolean r2 = r1.change_info
            if (r2 != 0) goto L_0x010c
            boolean r2 = r1.post_messages
            if (r2 != 0) goto L_0x010c
            boolean r2 = r1.edit_messages
            if (r2 != 0) goto L_0x010c
            boolean r2 = r1.delete_messages
            if (r2 != 0) goto L_0x010c
            boolean r2 = r1.ban_users
            if (r2 != 0) goto L_0x010c
            boolean r2 = r1.invite_users
            if (r2 != 0) goto L_0x010c
            boolean r2 = r1.pin_messages
            if (r2 != 0) goto L_0x010c
            boolean r2 = r1.add_admins
            if (r2 != 0) goto L_0x010c
            boolean r2 = r1.anonymous
            if (r2 != 0) goto L_0x010c
            boolean r2 = r1.manage_call
            if (r2 != 0) goto L_0x010c
            boolean r2 = r1.other
            if (r2 == 0) goto L_0x010b
            goto L_0x010c
        L_0x010b:
            r3 = 0
        L_0x010c:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = r14.bannedRights
            java.lang.String r4 = r14.currentRank
            r0.didSetRights(r3, r1, r2, r4)
            goto L_0x0159
        L_0x0114:
            if (r0 != r3) goto L_0x0159
            int r0 = r14.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r6 = r14.chatId
            org.telegram.tgnet.TLRPC$User r8 = r14.currentUser
            r9 = 0
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r10 = r14.bannedRights
            boolean r11 = r14.isChannel
            org.telegram.ui.ActionBar.BaseFragment r12 = r14.getFragmentForAlert(r3)
            r5.setParticipantBannedRole(r6, r8, r9, r10, r11, r12)
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r14.bannedRights
            boolean r1 = r0.send_messages
            if (r1 != 0) goto L_0x014e
            boolean r1 = r0.send_stickers
            if (r1 != 0) goto L_0x014e
            boolean r1 = r0.embed_links
            if (r1 != 0) goto L_0x014e
            boolean r1 = r0.send_media
            if (r1 != 0) goto L_0x014e
            boolean r1 = r0.send_gifs
            if (r1 != 0) goto L_0x014e
            boolean r1 = r0.send_games
            if (r1 != 0) goto L_0x014e
            boolean r1 = r0.send_inline
            if (r1 == 0) goto L_0x014b
            goto L_0x014e
        L_0x014b:
            r0.until_date = r4
            r3 = 2
        L_0x014e:
            org.telegram.ui.ChatRightsEditActivity$ChatRightsEditActivityDelegate r1 = r14.delegate
            if (r1 == 0) goto L_0x0159
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r14.adminRights
            java.lang.String r4 = r14.currentRank
            r1.didSetRights(r3, r2, r0, r4)
        L_0x0159:
            r14.finishFragment()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatRightsEditActivity.onDonePressed():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDonePressed$15(long j) {
        if (j != 0) {
            this.chatId = j;
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(j));
            onDonePressed();
        }
    }

    public void setDelegate(ChatRightsEditActivityDelegate chatRightsEditActivityDelegate) {
        this.delegate = chatRightsEditActivityDelegate;
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        boolean z;
        if (this.currentType == 1) {
            z = this.currentBannedRights.equals(ChatObject.getBannedRightsString(this.bannedRights));
        } else {
            z = this.initialRank.equals(this.currentRank);
        }
        if (!(!z)) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", NUM));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("UserRestrictionsApplyChangesText", NUM, MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.chatId)).title)));
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new ChatRightsEditActivity$$ExternalSyntheticLambda5(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new ChatRightsEditActivity$$ExternalSyntheticLambda2(this));
        showDialog(builder.create());
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$16(DialogInterface dialogInterface, int i) {
        onDonePressed();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$17(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    /* access modifiers changed from: private */
    public void setTextLeft(View view) {
        if (view instanceof HeaderCell) {
            HeaderCell headerCell = (HeaderCell) view;
            String str = this.currentRank;
            int codePointCount = 16 - (str != null ? str.codePointCount(0, str.length()) : 0);
            if (((float) codePointCount) <= 4.8f) {
                headerCell.setText2(String.format("%d", new Object[]{Integer.valueOf(codePointCount)}));
                SimpleTextView textView2 = headerCell.getTextView2();
                String str2 = codePointCount < 0 ? "windowBackgroundWhiteRedText5" : "windowBackgroundWhiteGrayText3";
                textView2.setTextColor(Theme.getColor(str2));
                textView2.setTag(str2);
                return;
            }
            headerCell.setText2("");
        }
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public boolean ignoreTextChange;
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (ChatRightsEditActivity.this.currentChat.creator && ChatRightsEditActivity.this.currentType == 0 && itemViewType == 4 && viewHolder.getAdapterPosition() == ChatRightsEditActivity.this.anonymousRow) {
                return true;
            }
            if (!ChatRightsEditActivity.this.canEdit) {
                return false;
            }
            if (ChatRightsEditActivity.this.currentType == 0 && itemViewType == 4) {
                int adapterPosition = viewHolder.getAdapterPosition();
                if (adapterPosition == ChatRightsEditActivity.this.changeInfoRow) {
                    return ChatRightsEditActivity.this.myAdminRights.change_info;
                }
                if (adapterPosition == ChatRightsEditActivity.this.postMessagesRow) {
                    return ChatRightsEditActivity.this.myAdminRights.post_messages;
                }
                if (adapterPosition == ChatRightsEditActivity.this.editMesagesRow) {
                    return ChatRightsEditActivity.this.myAdminRights.edit_messages;
                }
                if (adapterPosition == ChatRightsEditActivity.this.deleteMessagesRow) {
                    return ChatRightsEditActivity.this.myAdminRights.delete_messages;
                }
                if (adapterPosition == ChatRightsEditActivity.this.startVoiceChatRow) {
                    return ChatRightsEditActivity.this.myAdminRights.manage_call;
                }
                if (adapterPosition == ChatRightsEditActivity.this.addAdminsRow) {
                    return ChatRightsEditActivity.this.myAdminRights.add_admins;
                }
                if (adapterPosition == ChatRightsEditActivity.this.anonymousRow) {
                    return ChatRightsEditActivity.this.myAdminRights.anonymous;
                }
                if (adapterPosition == ChatRightsEditActivity.this.banUsersRow) {
                    return ChatRightsEditActivity.this.myAdminRights.ban_users;
                }
                if (adapterPosition == ChatRightsEditActivity.this.addUsersRow) {
                    return ChatRightsEditActivity.this.myAdminRights.invite_users;
                }
                if (adapterPosition == ChatRightsEditActivity.this.pinMessagesRow) {
                    return ChatRightsEditActivity.this.myAdminRights.pin_messages;
                }
            }
            if (itemViewType == 3 || itemViewType == 1 || itemViewType == 5) {
                return false;
            }
            return true;
        }

        public int getItemCount() {
            return ChatRightsEditActivity.this.rowCount;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: org.telegram.ui.Cells.UserCell2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v3, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v4, resolved type: org.telegram.ui.Cells.TextSettingsCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v6, resolved type: org.telegram.ui.Cells.TextCheckCell2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v7, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v8, resolved type: org.telegram.ui.Cells.TextDetailCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v9, resolved type: org.telegram.ui.Cells.PollEditTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v10, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v11, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v13, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v14, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v15, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v16, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r8v1, types: [android.view.View] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r7, int r8) {
            /*
                r6 = this;
                java.lang.String r7 = "windowBackgroundWhite"
                switch(r8) {
                    case 0: goto L_0x0080;
                    case 1: goto L_0x006a;
                    case 2: goto L_0x005b;
                    case 3: goto L_0x0044;
                    case 4: goto L_0x0035;
                    case 5: goto L_0x002d;
                    case 6: goto L_0x001e;
                    default: goto L_0x0005;
                }
            L_0x0005:
                org.telegram.ui.Cells.PollEditTextCell r8 = new org.telegram.ui.Cells.PollEditTextCell
                android.content.Context r0 = r6.mContext
                r1 = 0
                r8.<init>(r0, r1)
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r8.setBackgroundColor(r7)
                org.telegram.ui.ChatRightsEditActivity$ListAdapter$1 r7 = new org.telegram.ui.ChatRightsEditActivity$ListAdapter$1
                r7.<init>()
                r8.addTextWatcher(r7)
                goto L_0x0090
            L_0x001e:
                org.telegram.ui.Cells.TextDetailCell r8 = new org.telegram.ui.Cells.TextDetailCell
                android.content.Context r0 = r6.mContext
                r8.<init>(r0)
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r8.setBackgroundColor(r7)
                goto L_0x0090
            L_0x002d:
                org.telegram.ui.Cells.ShadowSectionCell r8 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r7 = r6.mContext
                r8.<init>(r7)
                goto L_0x0090
            L_0x0035:
                org.telegram.ui.Cells.TextCheckCell2 r8 = new org.telegram.ui.Cells.TextCheckCell2
                android.content.Context r0 = r6.mContext
                r8.<init>(r0)
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r8.setBackgroundColor(r7)
                goto L_0x0090
            L_0x0044:
                org.telegram.ui.Cells.HeaderCell r8 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r1 = r6.mContext
                r3 = 21
                r4 = 15
                r5 = 1
                java.lang.String r2 = "windowBackgroundWhiteBlueHeader"
                r0 = r8
                r0.<init>(r1, r2, r3, r4, r5)
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r8.setBackgroundColor(r7)
                goto L_0x0090
            L_0x005b:
                org.telegram.ui.Cells.TextSettingsCell r8 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r0 = r6.mContext
                r8.<init>(r0)
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r8.setBackgroundColor(r7)
                goto L_0x0090
            L_0x006a:
                org.telegram.ui.Cells.TextInfoPrivacyCell r8 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r7 = r6.mContext
                r8.<init>(r7)
                android.content.Context r7 = r6.mContext
                r0 = 2131165468(0x7var_c, float:1.7945154E38)
                java.lang.String r1 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r7, (int) r0, (java.lang.String) r1)
                r8.setBackgroundDrawable(r7)
                goto L_0x0090
            L_0x0080:
                org.telegram.ui.Cells.UserCell2 r8 = new org.telegram.ui.Cells.UserCell2
                android.content.Context r0 = r6.mContext
                r1 = 4
                r2 = 0
                r8.<init>(r0, r1, r2)
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r8.setBackgroundColor(r7)
            L_0x0090:
                org.telegram.ui.Components.RecyclerListView$Holder r7 = new org.telegram.ui.Components.RecyclerListView$Holder
                r7.<init>(r8)
                return r7
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatRightsEditActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            String str2;
            String str3;
            boolean z = false;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    ((UserCell2) viewHolder.itemView).setData(ChatRightsEditActivity.this.currentUser, (CharSequence) null, (CharSequence) null, 0);
                    return;
                case 1:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == ChatRightsEditActivity.this.cantEditInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("EditAdminCantEdit", NUM));
                        return;
                    } else if (i == ChatRightsEditActivity.this.rankInfoRow) {
                        if (!UserObject.isUserSelf(ChatRightsEditActivity.this.currentUser) || !ChatRightsEditActivity.this.currentChat.creator) {
                            str = LocaleController.getString("ChannelAdmin", NUM);
                        } else {
                            str = LocaleController.getString("ChannelCreator", NUM);
                        }
                        textInfoPrivacyCell.setText(LocaleController.formatString("EditAdminRankInfo", NUM, str));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i == ChatRightsEditActivity.this.removeAdminRow) {
                        textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
                        textSettingsCell.setTag("windowBackgroundWhiteRedText5");
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            textSettingsCell.setText(LocaleController.getString("EditAdminRemoveAdmin", NUM), false);
                            return;
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            textSettingsCell.setText(LocaleController.getString("UserRestrictionsBlock", NUM), false);
                            return;
                        } else {
                            return;
                        }
                    } else if (i == ChatRightsEditActivity.this.transferOwnerRow) {
                        textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        textSettingsCell.setTag("windowBackgroundWhiteBlackText");
                        if (ChatRightsEditActivity.this.isChannel) {
                            textSettingsCell.setText(LocaleController.getString("EditAdminChannelTransfer", NUM), false);
                            return;
                        } else {
                            textSettingsCell.setText(LocaleController.getString("EditAdminGroupTransfer", NUM), false);
                            return;
                        }
                    } else {
                        return;
                    }
                case 3:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == 2) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            headerCell.setText(LocaleController.getString("EditAdminWhatCanDo", NUM));
                            return;
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            headerCell.setText(LocaleController.getString("UserRestrictionsCanDo", NUM));
                            return;
                        } else {
                            return;
                        }
                    } else if (i == ChatRightsEditActivity.this.rankHeaderRow) {
                        headerCell.setText(LocaleController.getString("EditAdminRank", NUM));
                        return;
                    } else {
                        return;
                    }
                case 4:
                    TextCheckCell2 textCheckCell2 = (TextCheckCell2) viewHolder.itemView;
                    int i2 = NUM;
                    if (i == ChatRightsEditActivity.this.changeInfoRow) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            if (ChatRightsEditActivity.this.isChannel) {
                                textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminChangeChannelInfo", NUM), ChatRightsEditActivity.this.adminRights.change_info, true);
                            } else {
                                textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminChangeGroupInfo", NUM), ChatRightsEditActivity.this.adminRights.change_info, true);
                            }
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsChangeInfo", NUM), !ChatRightsEditActivity.this.bannedRights.change_info && !ChatRightsEditActivity.this.defaultBannedRights.change_info, false);
                            if (!ChatRightsEditActivity.this.defaultBannedRights.change_info) {
                                i2 = 0;
                            }
                            textCheckCell2.setIcon(i2);
                        }
                    } else if (i == ChatRightsEditActivity.this.postMessagesRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminPostMessages", NUM), ChatRightsEditActivity.this.adminRights.post_messages, true);
                    } else if (i == ChatRightsEditActivity.this.editMesagesRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminEditMessages", NUM), ChatRightsEditActivity.this.adminRights.edit_messages, true);
                    } else if (i == ChatRightsEditActivity.this.deleteMessagesRow) {
                        if (ChatRightsEditActivity.this.isChannel) {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminDeleteMessages", NUM), ChatRightsEditActivity.this.adminRights.delete_messages, true);
                        } else {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminGroupDeleteMessages", NUM), ChatRightsEditActivity.this.adminRights.delete_messages, true);
                        }
                    } else if (i == ChatRightsEditActivity.this.addAdminsRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminAddAdmins", NUM), ChatRightsEditActivity.this.adminRights.add_admins, ChatRightsEditActivity.this.anonymousRow != -1);
                    } else if (i == ChatRightsEditActivity.this.anonymousRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminSendAnonymously", NUM), ChatRightsEditActivity.this.adminRights.anonymous, false);
                    } else if (i == ChatRightsEditActivity.this.banUsersRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminBanUsers", NUM), ChatRightsEditActivity.this.adminRights.ban_users, true);
                    } else if (i == ChatRightsEditActivity.this.startVoiceChatRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("StartVoipChatPermission", NUM), ChatRightsEditActivity.this.adminRights.manage_call, true);
                    } else if (i == ChatRightsEditActivity.this.addUsersRow) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            if (ChatObject.isActionBannedByDefault(ChatRightsEditActivity.this.currentChat, 3)) {
                                textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminAddUsers", NUM), ChatRightsEditActivity.this.adminRights.invite_users, true);
                            } else {
                                textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminAddUsersViaLink", NUM), ChatRightsEditActivity.this.adminRights.invite_users, true);
                            }
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsInviteUsers", NUM), !ChatRightsEditActivity.this.bannedRights.invite_users && !ChatRightsEditActivity.this.defaultBannedRights.invite_users, true);
                            if (!ChatRightsEditActivity.this.defaultBannedRights.invite_users) {
                                i2 = 0;
                            }
                            textCheckCell2.setIcon(i2);
                        }
                    } else if (i == ChatRightsEditActivity.this.pinMessagesRow) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminPinMessages", NUM), ChatRightsEditActivity.this.adminRights.pin_messages, true);
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsPinMessages", NUM), !ChatRightsEditActivity.this.bannedRights.pin_messages && !ChatRightsEditActivity.this.defaultBannedRights.pin_messages, true);
                            if (!ChatRightsEditActivity.this.defaultBannedRights.pin_messages) {
                                i2 = 0;
                            }
                            textCheckCell2.setIcon(i2);
                        }
                    } else if (i == ChatRightsEditActivity.this.sendMessagesRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsSend", NUM), !ChatRightsEditActivity.this.bannedRights.send_messages && !ChatRightsEditActivity.this.defaultBannedRights.send_messages, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_messages) {
                            i2 = 0;
                        }
                        textCheckCell2.setIcon(i2);
                    } else if (i == ChatRightsEditActivity.this.sendMediaRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsSendMedia", NUM), !ChatRightsEditActivity.this.bannedRights.send_media && !ChatRightsEditActivity.this.defaultBannedRights.send_media, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_media) {
                            i2 = 0;
                        }
                        textCheckCell2.setIcon(i2);
                    } else if (i == ChatRightsEditActivity.this.sendStickersRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsSendStickers", NUM), !ChatRightsEditActivity.this.bannedRights.send_stickers && !ChatRightsEditActivity.this.defaultBannedRights.send_stickers, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_stickers) {
                            i2 = 0;
                        }
                        textCheckCell2.setIcon(i2);
                    } else if (i == ChatRightsEditActivity.this.embedLinksRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsEmbedLinks", NUM), !ChatRightsEditActivity.this.bannedRights.embed_links && !ChatRightsEditActivity.this.defaultBannedRights.embed_links, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.embed_links) {
                            i2 = 0;
                        }
                        textCheckCell2.setIcon(i2);
                    } else if (i == ChatRightsEditActivity.this.sendPollsRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsSendPolls", NUM), !ChatRightsEditActivity.this.bannedRights.send_polls && !ChatRightsEditActivity.this.defaultBannedRights.send_polls, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_polls) {
                            i2 = 0;
                        }
                        textCheckCell2.setIcon(i2);
                    }
                    if (i == ChatRightsEditActivity.this.sendMediaRow || i == ChatRightsEditActivity.this.sendStickersRow || i == ChatRightsEditActivity.this.embedLinksRow || i == ChatRightsEditActivity.this.sendPollsRow) {
                        if (!ChatRightsEditActivity.this.bannedRights.send_messages && !ChatRightsEditActivity.this.bannedRights.view_messages && !ChatRightsEditActivity.this.defaultBannedRights.send_messages && !ChatRightsEditActivity.this.defaultBannedRights.view_messages) {
                            z = true;
                        }
                        textCheckCell2.setEnabled(z);
                        return;
                    } else if (i == ChatRightsEditActivity.this.sendMessagesRow) {
                        if (!ChatRightsEditActivity.this.bannedRights.view_messages && !ChatRightsEditActivity.this.defaultBannedRights.view_messages) {
                            z = true;
                        }
                        textCheckCell2.setEnabled(z);
                        return;
                    } else {
                        return;
                    }
                case 5:
                    ShadowSectionCell shadowSectionCell = (ShadowSectionCell) viewHolder.itemView;
                    int i3 = NUM;
                    if (i == ChatRightsEditActivity.this.rightsShadowRow) {
                        Context context = this.mContext;
                        if (ChatRightsEditActivity.this.removeAdminRow == -1 && ChatRightsEditActivity.this.rankRow == -1) {
                            i3 = NUM;
                        }
                        shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, i3, "windowBackgroundGrayShadow"));
                        return;
                    } else if (i == ChatRightsEditActivity.this.removeAdminShadowRow) {
                        shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (i == ChatRightsEditActivity.this.rankInfoRow) {
                        Context context2 = this.mContext;
                        if (!ChatRightsEditActivity.this.canEdit) {
                            i3 = NUM;
                        }
                        shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, i3, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 6:
                    TextDetailCell textDetailCell = (TextDetailCell) viewHolder.itemView;
                    if (i == ChatRightsEditActivity.this.untilDateRow) {
                        if (ChatRightsEditActivity.this.bannedRights.until_date == 0 || Math.abs(((long) ChatRightsEditActivity.this.bannedRights.until_date) - (System.currentTimeMillis() / 1000)) > NUM) {
                            str2 = LocaleController.getString("UserRestrictionsUntilForever", NUM);
                        } else {
                            str2 = LocaleController.formatDateForBan((long) ChatRightsEditActivity.this.bannedRights.until_date);
                        }
                        textDetailCell.setTextAndValue(LocaleController.getString("UserRestrictionsDuration", NUM), str2, false);
                        return;
                    }
                    return;
                case 7:
                    PollEditTextCell pollEditTextCell = (PollEditTextCell) viewHolder.itemView;
                    if (!UserObject.isUserSelf(ChatRightsEditActivity.this.currentUser) || !ChatRightsEditActivity.this.currentChat.creator) {
                        str3 = LocaleController.getString("ChannelAdmin", NUM);
                    } else {
                        str3 = LocaleController.getString("ChannelCreator", NUM);
                    }
                    this.ignoreTextChange = true;
                    pollEditTextCell.getTextView().setEnabled(ChatRightsEditActivity.this.canEdit || ChatRightsEditActivity.this.currentChat.creator);
                    pollEditTextCell.getTextView().setSingleLine(true);
                    pollEditTextCell.getTextView().setImeOptions(6);
                    pollEditTextCell.setTextAndHint(ChatRightsEditActivity.this.currentRank, str3, false);
                    this.ignoreTextChange = false;
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getAdapterPosition() == ChatRightsEditActivity.this.rankHeaderRow) {
                ChatRightsEditActivity.this.setTextLeft(viewHolder.itemView);
            }
        }

        public void onViewDetachedFromWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getAdapterPosition() == ChatRightsEditActivity.this.rankRow && ChatRightsEditActivity.this.getParentActivity() != null) {
                AndroidUtilities.hideKeyboard(ChatRightsEditActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        public int getItemViewType(int i) {
            if (i == 0) {
                return 0;
            }
            if (i == 1 || i == ChatRightsEditActivity.this.rightsShadowRow || i == ChatRightsEditActivity.this.removeAdminShadowRow || i == ChatRightsEditActivity.this.untilSectionRow || i == ChatRightsEditActivity.this.transferOwnerShadowRow) {
                return 5;
            }
            if (i == 2 || i == ChatRightsEditActivity.this.rankHeaderRow) {
                return 3;
            }
            if (i == ChatRightsEditActivity.this.changeInfoRow || i == ChatRightsEditActivity.this.postMessagesRow || i == ChatRightsEditActivity.this.editMesagesRow || i == ChatRightsEditActivity.this.deleteMessagesRow || i == ChatRightsEditActivity.this.addAdminsRow || i == ChatRightsEditActivity.this.banUsersRow || i == ChatRightsEditActivity.this.addUsersRow || i == ChatRightsEditActivity.this.pinMessagesRow || i == ChatRightsEditActivity.this.sendMessagesRow || i == ChatRightsEditActivity.this.sendMediaRow || i == ChatRightsEditActivity.this.sendStickersRow || i == ChatRightsEditActivity.this.embedLinksRow || i == ChatRightsEditActivity.this.sendPollsRow || i == ChatRightsEditActivity.this.anonymousRow || i == ChatRightsEditActivity.this.startVoiceChatRow) {
                return 4;
            }
            if (i == ChatRightsEditActivity.this.cantEditInfoRow || i == ChatRightsEditActivity.this.rankInfoRow) {
                return 1;
            }
            if (i == ChatRightsEditActivity.this.untilDateRow) {
                return 6;
            }
            if (i == ChatRightsEditActivity.this.rankRow) {
                return 7;
            }
            return 2;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ChatRightsEditActivity$$ExternalSyntheticLambda16 chatRightsEditActivity$$ExternalSyntheticLambda16 = new ChatRightsEditActivity$$ExternalSyntheticLambda16(this);
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{UserCell2.class, TextSettingsCell.class, TextCheckCell2.class, HeaderCell.class, TextDetailCell.class, PollEditTextCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switch2Track"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switch2TrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        ChatRightsEditActivity$$ExternalSyntheticLambda16 chatRightsEditActivity$$ExternalSyntheticLambda162 = chatRightsEditActivity$$ExternalSyntheticLambda16;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) chatRightsEditActivity$$ExternalSyntheticLambda162, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) chatRightsEditActivity$$ExternalSyntheticLambda162, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ChatRightsEditActivity$$ExternalSyntheticLambda16 chatRightsEditActivity$$ExternalSyntheticLambda163 = chatRightsEditActivity$$ExternalSyntheticLambda16;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatRightsEditActivity$$ExternalSyntheticLambda163, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatRightsEditActivity$$ExternalSyntheticLambda163, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatRightsEditActivity$$ExternalSyntheticLambda163, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatRightsEditActivity$$ExternalSyntheticLambda163, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatRightsEditActivity$$ExternalSyntheticLambda163, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatRightsEditActivity$$ExternalSyntheticLambda163, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatRightsEditActivity$$ExternalSyntheticLambda163, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription((View) null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription((View) null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextGray2"));
        arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOX, new Class[]{DialogRadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogRadioBackground"));
        arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{DialogRadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogRadioBackgroundChecked"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$18() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof UserCell2) {
                    ((UserCell2) childAt).update(0);
                }
            }
        }
    }
}
