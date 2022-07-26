package org.telegram.ui;

import android.animation.ValueAnimator;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import org.telegram.messenger.AccountInstance;
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
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
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
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RecyclerListView;

public class ChatRightsEditActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public int addAdminsRow;
    /* access modifiers changed from: private */
    public FrameLayout addBotButton;
    /* access modifiers changed from: private */
    public FrameLayout addBotButtonContainer;
    /* access modifiers changed from: private */
    public int addBotButtonRow;
    /* access modifiers changed from: private */
    public AnimatedTextView addBotButtonText;
    /* access modifiers changed from: private */
    public int addUsersRow;
    /* access modifiers changed from: private */
    public TLRPC$TL_chatAdminRights adminRights;
    /* access modifiers changed from: private */
    public int anonymousRow;
    /* access modifiers changed from: private */
    public boolean asAdmin;
    private ValueAnimator asAdminAnimator;
    /* access modifiers changed from: private */
    public float asAdminT;
    /* access modifiers changed from: private */
    public int banUsersRow;
    /* access modifiers changed from: private */
    public TLRPC$TL_chatBannedRights bannedRights;
    private String botHash;
    /* access modifiers changed from: private */
    public boolean canEdit;
    /* access modifiers changed from: private */
    public int cantEditInfoRow;
    /* access modifiers changed from: private */
    public int changeInfoRow;
    private long chatId;
    private String currentBannedRights;
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
    private CrossfadeDrawable doneDrawable;
    private ValueAnimator doneDrawableAnimator;
    /* access modifiers changed from: private */
    public int editMesagesRow;
    /* access modifiers changed from: private */
    public int embedLinksRow;
    private boolean initialAsAdmin;
    private boolean initialIsSet;
    private String initialRank;
    private boolean isAddingNew;
    /* access modifiers changed from: private */
    public boolean isChannel;
    private LinearLayoutManager linearLayoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public boolean loading = false;
    /* access modifiers changed from: private */
    public int manageRow;
    /* access modifiers changed from: private */
    public TLRPC$TL_chatAdminRights myAdminRights;
    /* access modifiers changed from: private */
    public int pinMessagesRow;
    /* access modifiers changed from: private */
    public int postMessagesRow;
    /* access modifiers changed from: private */
    public PollEditTextCell rankEditTextCell;
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

    public ChatRightsEditActivity(long j, long j2, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2, String str, int i, boolean z, boolean z2, String str2) {
        boolean z3;
        TLRPC$UserFull userFull;
        TLRPC$Chat tLRPC$Chat;
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2 = tLRPC$TL_chatAdminRights;
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights3 = tLRPC$TL_chatBannedRights;
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights4 = tLRPC$TL_chatBannedRights2;
        int i2 = i;
        float f = 0.0f;
        this.asAdminT = 0.0f;
        this.asAdmin = false;
        this.initialAsAdmin = false;
        String str3 = "";
        this.currentBannedRights = str3;
        this.isAddingNew = z2;
        this.chatId = j2;
        this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j));
        this.currentType = i2;
        this.canEdit = z;
        this.botHash = str2;
        TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.chatId));
        this.currentChat = chat;
        str3 = str != null ? str : str3;
        this.currentRank = str3;
        this.initialRank = str3;
        boolean z4 = true;
        if (chat != null) {
            this.isChannel = ChatObject.isChannel(chat) && !this.currentChat.megagroup;
            this.myAdminRights = this.currentChat.admin_rights;
        }
        if (this.myAdminRights == null) {
            this.myAdminRights = emptyAdminRights(this.currentType != 2 || ((tLRPC$Chat = this.currentChat) != null && tLRPC$Chat.creator));
        }
        if (i2 == 0 || i2 == 2) {
            if (i2 == 2 && (userFull = getMessagesController().getUserFull(j)) != null) {
                TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights3 = this.isChannel ? userFull.bot_broadcast_admin_rights : userFull.bot_group_admin_rights;
                if (tLRPC$TL_chatAdminRights3 != null) {
                    if (tLRPC$TL_chatAdminRights2 == null) {
                        tLRPC$TL_chatAdminRights2 = tLRPC$TL_chatAdminRights3;
                    } else {
                        tLRPC$TL_chatAdminRights2.ban_users = tLRPC$TL_chatAdminRights2.ban_users || tLRPC$TL_chatAdminRights3.ban_users;
                        tLRPC$TL_chatAdminRights2.add_admins = tLRPC$TL_chatAdminRights2.add_admins || tLRPC$TL_chatAdminRights3.add_admins;
                        tLRPC$TL_chatAdminRights2.post_messages = tLRPC$TL_chatAdminRights2.post_messages || tLRPC$TL_chatAdminRights3.post_messages;
                        tLRPC$TL_chatAdminRights2.pin_messages = tLRPC$TL_chatAdminRights2.pin_messages || tLRPC$TL_chatAdminRights3.pin_messages;
                        tLRPC$TL_chatAdminRights2.delete_messages = tLRPC$TL_chatAdminRights2.delete_messages || tLRPC$TL_chatAdminRights3.delete_messages;
                        tLRPC$TL_chatAdminRights2.change_info = tLRPC$TL_chatAdminRights2.change_info || tLRPC$TL_chatAdminRights3.change_info;
                        tLRPC$TL_chatAdminRights2.anonymous = tLRPC$TL_chatAdminRights2.anonymous || tLRPC$TL_chatAdminRights3.anonymous;
                        tLRPC$TL_chatAdminRights2.edit_messages = tLRPC$TL_chatAdminRights2.edit_messages || tLRPC$TL_chatAdminRights3.edit_messages;
                        tLRPC$TL_chatAdminRights2.manage_call = tLRPC$TL_chatAdminRights2.manage_call || tLRPC$TL_chatAdminRights3.manage_call;
                        tLRPC$TL_chatAdminRights2.other = tLRPC$TL_chatAdminRights2.other || tLRPC$TL_chatAdminRights3.other;
                    }
                }
            }
            if (tLRPC$TL_chatAdminRights2 == null) {
                this.initialAsAdmin = false;
                if (i2 == 2) {
                    this.adminRights = emptyAdminRights(false);
                    boolean z5 = this.isChannel;
                    this.asAdmin = z5;
                    this.asAdminT = z5 ? 1.0f : f;
                    this.initialIsSet = false;
                } else {
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights4 = new TLRPC$TL_chatAdminRights();
                    this.adminRights = tLRPC$TL_chatAdminRights4;
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights5 = this.myAdminRights;
                    tLRPC$TL_chatAdminRights4.change_info = tLRPC$TL_chatAdminRights5.change_info;
                    tLRPC$TL_chatAdminRights4.post_messages = tLRPC$TL_chatAdminRights5.post_messages;
                    tLRPC$TL_chatAdminRights4.edit_messages = tLRPC$TL_chatAdminRights5.edit_messages;
                    tLRPC$TL_chatAdminRights4.delete_messages = tLRPC$TL_chatAdminRights5.delete_messages;
                    tLRPC$TL_chatAdminRights4.manage_call = tLRPC$TL_chatAdminRights5.manage_call;
                    tLRPC$TL_chatAdminRights4.ban_users = tLRPC$TL_chatAdminRights5.ban_users;
                    tLRPC$TL_chatAdminRights4.invite_users = tLRPC$TL_chatAdminRights5.invite_users;
                    tLRPC$TL_chatAdminRights4.pin_messages = tLRPC$TL_chatAdminRights5.pin_messages;
                    tLRPC$TL_chatAdminRights4.other = tLRPC$TL_chatAdminRights5.other;
                    this.initialIsSet = false;
                }
            } else {
                this.initialAsAdmin = true;
                TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights6 = new TLRPC$TL_chatAdminRights();
                this.adminRights = tLRPC$TL_chatAdminRights6;
                boolean z6 = tLRPC$TL_chatAdminRights2.change_info;
                tLRPC$TL_chatAdminRights6.change_info = z6;
                boolean z7 = tLRPC$TL_chatAdminRights2.post_messages;
                tLRPC$TL_chatAdminRights6.post_messages = z7;
                boolean z8 = tLRPC$TL_chatAdminRights2.edit_messages;
                tLRPC$TL_chatAdminRights6.edit_messages = z8;
                boolean z9 = tLRPC$TL_chatAdminRights2.delete_messages;
                tLRPC$TL_chatAdminRights6.delete_messages = z9;
                boolean z10 = tLRPC$TL_chatAdminRights2.manage_call;
                tLRPC$TL_chatAdminRights6.manage_call = z10;
                boolean z11 = tLRPC$TL_chatAdminRights2.ban_users;
                tLRPC$TL_chatAdminRights6.ban_users = z11;
                boolean z12 = tLRPC$TL_chatAdminRights2.invite_users;
                tLRPC$TL_chatAdminRights6.invite_users = z12;
                boolean z13 = tLRPC$TL_chatAdminRights2.pin_messages;
                tLRPC$TL_chatAdminRights6.pin_messages = z13;
                boolean z14 = tLRPC$TL_chatAdminRights2.add_admins;
                tLRPC$TL_chatAdminRights6.add_admins = z14;
                boolean z15 = tLRPC$TL_chatAdminRights2.anonymous;
                tLRPC$TL_chatAdminRights6.anonymous = z15;
                boolean z16 = tLRPC$TL_chatAdminRights2.other;
                tLRPC$TL_chatAdminRights6.other = z16;
                boolean z17 = z6 || z7 || z8 || z9 || z11 || z12 || z13 || z14 || z10 || z15 || z16;
                this.initialIsSet = z17;
                if (i2 == 2) {
                    boolean z18 = this.isChannel || z17;
                    this.asAdmin = z18;
                    this.asAdminT = z18 ? 1.0f : 0.0f;
                    this.initialIsSet = false;
                }
            }
            TLRPC$Chat tLRPC$Chat2 = this.currentChat;
            if (tLRPC$Chat2 != null) {
                this.defaultBannedRights = tLRPC$Chat2.default_banned_rights;
            }
            if (this.defaultBannedRights == null) {
                TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights5 = new TLRPC$TL_chatBannedRights();
                this.defaultBannedRights = tLRPC$TL_chatBannedRights5;
                z3 = true;
                tLRPC$TL_chatBannedRights5.pin_messages = true;
                tLRPC$TL_chatBannedRights5.change_info = true;
                tLRPC$TL_chatBannedRights5.invite_users = true;
                tLRPC$TL_chatBannedRights5.send_polls = true;
                tLRPC$TL_chatBannedRights5.send_inline = true;
                tLRPC$TL_chatBannedRights5.send_games = true;
                tLRPC$TL_chatBannedRights5.send_gifs = true;
                tLRPC$TL_chatBannedRights5.send_stickers = true;
                tLRPC$TL_chatBannedRights5.embed_links = true;
                tLRPC$TL_chatBannedRights5.send_messages = true;
                tLRPC$TL_chatBannedRights5.send_media = true;
                tLRPC$TL_chatBannedRights5.view_messages = true;
            } else {
                z3 = true;
            }
            TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights6 = this.defaultBannedRights;
            if (!tLRPC$TL_chatBannedRights6.change_info) {
                this.adminRights.change_info = z3;
            }
            if (!tLRPC$TL_chatBannedRights6.pin_messages) {
                this.adminRights.pin_messages = z3;
            }
        } else if (i2 == 1) {
            this.defaultBannedRights = tLRPC$TL_chatBannedRights3;
            if (tLRPC$TL_chatBannedRights3 == null) {
                TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights7 = new TLRPC$TL_chatBannedRights();
                this.defaultBannedRights = tLRPC$TL_chatBannedRights7;
                tLRPC$TL_chatBannedRights7.pin_messages = false;
                tLRPC$TL_chatBannedRights7.change_info = false;
                tLRPC$TL_chatBannedRights7.invite_users = false;
                tLRPC$TL_chatBannedRights7.send_polls = false;
                tLRPC$TL_chatBannedRights7.send_inline = false;
                tLRPC$TL_chatBannedRights7.send_games = false;
                tLRPC$TL_chatBannedRights7.send_gifs = false;
                tLRPC$TL_chatBannedRights7.send_stickers = false;
                tLRPC$TL_chatBannedRights7.embed_links = false;
                tLRPC$TL_chatBannedRights7.send_messages = false;
                tLRPC$TL_chatBannedRights7.send_media = false;
                tLRPC$TL_chatBannedRights7.view_messages = false;
            }
            TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights8 = new TLRPC$TL_chatBannedRights();
            this.bannedRights = tLRPC$TL_chatBannedRights8;
            if (tLRPC$TL_chatBannedRights4 == null) {
                tLRPC$TL_chatBannedRights8.pin_messages = false;
                tLRPC$TL_chatBannedRights8.change_info = false;
                tLRPC$TL_chatBannedRights8.invite_users = false;
                tLRPC$TL_chatBannedRights8.send_polls = false;
                tLRPC$TL_chatBannedRights8.send_inline = false;
                tLRPC$TL_chatBannedRights8.send_games = false;
                tLRPC$TL_chatBannedRights8.send_gifs = false;
                tLRPC$TL_chatBannedRights8.send_stickers = false;
                tLRPC$TL_chatBannedRights8.embed_links = false;
                tLRPC$TL_chatBannedRights8.send_messages = false;
                tLRPC$TL_chatBannedRights8.send_media = false;
                tLRPC$TL_chatBannedRights8.view_messages = false;
            } else {
                tLRPC$TL_chatBannedRights8.view_messages = tLRPC$TL_chatBannedRights4.view_messages;
                tLRPC$TL_chatBannedRights8.send_messages = tLRPC$TL_chatBannedRights4.send_messages;
                tLRPC$TL_chatBannedRights8.send_media = tLRPC$TL_chatBannedRights4.send_media;
                tLRPC$TL_chatBannedRights8.send_stickers = tLRPC$TL_chatBannedRights4.send_stickers;
                tLRPC$TL_chatBannedRights8.send_gifs = tLRPC$TL_chatBannedRights4.send_gifs;
                tLRPC$TL_chatBannedRights8.send_games = tLRPC$TL_chatBannedRights4.send_games;
                tLRPC$TL_chatBannedRights8.send_inline = tLRPC$TL_chatBannedRights4.send_inline;
                tLRPC$TL_chatBannedRights8.embed_links = tLRPC$TL_chatBannedRights4.embed_links;
                tLRPC$TL_chatBannedRights8.send_polls = tLRPC$TL_chatBannedRights4.send_polls;
                tLRPC$TL_chatBannedRights8.invite_users = tLRPC$TL_chatBannedRights4.invite_users;
                tLRPC$TL_chatBannedRights8.change_info = tLRPC$TL_chatBannedRights4.change_info;
                tLRPC$TL_chatBannedRights8.pin_messages = tLRPC$TL_chatBannedRights4.pin_messages;
                tLRPC$TL_chatBannedRights8.until_date = tLRPC$TL_chatBannedRights4.until_date;
            }
            TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights9 = this.defaultBannedRights;
            if (tLRPC$TL_chatBannedRights9.view_messages) {
                tLRPC$TL_chatBannedRights8.view_messages = true;
            }
            if (tLRPC$TL_chatBannedRights9.send_messages) {
                tLRPC$TL_chatBannedRights8.send_messages = true;
            }
            if (tLRPC$TL_chatBannedRights9.send_media) {
                tLRPC$TL_chatBannedRights8.send_media = true;
            }
            if (tLRPC$TL_chatBannedRights9.send_stickers) {
                tLRPC$TL_chatBannedRights8.send_stickers = true;
            }
            if (tLRPC$TL_chatBannedRights9.send_gifs) {
                tLRPC$TL_chatBannedRights8.send_gifs = true;
            }
            if (tLRPC$TL_chatBannedRights9.send_games) {
                tLRPC$TL_chatBannedRights8.send_games = true;
            }
            if (tLRPC$TL_chatBannedRights9.send_inline) {
                tLRPC$TL_chatBannedRights8.send_inline = true;
            }
            if (tLRPC$TL_chatBannedRights9.embed_links) {
                tLRPC$TL_chatBannedRights8.embed_links = true;
            }
            if (tLRPC$TL_chatBannedRights9.send_polls) {
                tLRPC$TL_chatBannedRights8.send_polls = true;
            }
            if (tLRPC$TL_chatBannedRights9.invite_users) {
                tLRPC$TL_chatBannedRights8.invite_users = true;
            }
            if (tLRPC$TL_chatBannedRights9.change_info) {
                tLRPC$TL_chatBannedRights8.change_info = true;
            }
            if (tLRPC$TL_chatBannedRights9.pin_messages) {
                tLRPC$TL_chatBannedRights8.pin_messages = true;
            }
            this.currentBannedRights = ChatObject.getBannedRightsString(tLRPC$TL_chatBannedRights8);
            if (tLRPC$TL_chatBannedRights4 != null && tLRPC$TL_chatBannedRights4.view_messages) {
                z4 = false;
            }
            this.initialIsSet = z4;
        }
        updateRows(false);
    }

    public static TLRPC$TL_chatAdminRights emptyAdminRights(boolean z) {
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = new TLRPC$TL_chatAdminRights();
        tLRPC$TL_chatAdminRights.manage_call = z;
        tLRPC$TL_chatAdminRights.add_admins = z;
        tLRPC$TL_chatAdminRights.pin_messages = z;
        tLRPC$TL_chatAdminRights.invite_users = z;
        tLRPC$TL_chatAdminRights.ban_users = z;
        tLRPC$TL_chatAdminRights.delete_messages = z;
        tLRPC$TL_chatAdminRights.edit_messages = z;
        tLRPC$TL_chatAdminRights.post_messages = z;
        tLRPC$TL_chatAdminRights.change_info = z;
        return tLRPC$TL_chatAdminRights;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        int i2 = this.currentType;
        if (i2 == 0) {
            this.actionBar.setTitle(LocaleController.getString("EditAdmin", NUM));
        } else if (i2 == 2) {
            this.actionBar.setTitle(LocaleController.getString("AddBot", NUM));
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
            ActionBarMenu createMenu = this.actionBar.createMenu();
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultIcon"), PorterDuff.Mode.MULTIPLY));
            this.doneDrawable = new CrossfadeDrawable(mutate, new CircularProgressDrawable(Theme.getColor("actionBarDefaultIcon")));
            createMenu.addItemWithWidth(1, 0, AndroidUtilities.dp(56.0f), (CharSequence) LocaleController.getString("Done", NUM));
            createMenu.getItem(1).setIcon((Drawable) this.doneDrawable);
        }
        AnonymousClass2 r0 = new FrameLayout(context) {
            private int previousHeight = -1;

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                int i5 = i4 - i2;
                int i6 = this.previousHeight;
                if (i6 != -1 && Math.abs(i6 - i5) > AndroidUtilities.dp(20.0f)) {
                    ChatRightsEditActivity.this.listView.smoothScrollToPosition(ChatRightsEditActivity.this.rowCount - 1);
                }
                this.previousHeight = i5;
            }
        };
        this.fragmentView = r0;
        r0.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        View view = this.fragmentView;
        FrameLayout frameLayout = (FrameLayout) view;
        view.setFocusableInTouchMode(true);
        AnonymousClass3 r02 = new RecyclerListView(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (ChatRightsEditActivity.this.loading) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (ChatRightsEditActivity.this.loading) {
                    return false;
                }
                return super.onInterceptTouchEvent(motionEvent);
            }
        };
        this.listView = r02;
        r02.setClipChildren(this.currentType != 2);
        AnonymousClass4 r03 = new LinearLayoutManager(this, context, 1, false) {
            /* access modifiers changed from: protected */
            public int getExtraLayoutSpace(RecyclerView.State state) {
                return 5000;
            }
        };
        this.linearLayoutManager = r03;
        r03.setInitialPrefetchItemCount(100);
        this.listView.setLayoutManager(this.linearLayoutManager);
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        if (this.currentType == 2) {
            this.listView.setResetSelectorOnChanged(false);
        }
        defaultItemAnimator.setDelayAnimations(false);
        this.listView.setItemAnimator(defaultItemAnimator);
        RecyclerListView recyclerListView2 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView2.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(ChatRightsEditActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatRightsEditActivity$$ExternalSyntheticLambda25(this, context));
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(Context context, View view, int i) {
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights;
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2;
        String str;
        Context context2 = context;
        View view2 = view;
        int i2 = i;
        if (!this.canEdit && (!this.currentChat.creator || this.currentType != 0 || i2 != this.anonymousRow)) {
            return;
        }
        if (i2 == 0) {
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", this.currentUser.id);
            presentFragment(new ProfileActivity(bundle));
            return;
        }
        boolean z = false;
        if (i2 == this.removeAdminRow) {
            int i3 = this.currentType;
            if (i3 == 0) {
                MessagesController.getInstance(this.currentAccount).setUserAdminRole(this.chatId, this.currentUser, new TLRPC$TL_chatAdminRights(), this.currentRank, this.isChannel, getFragmentForAlert(0), this.isAddingNew, false, (String) null, (Runnable) null);
                ChatRightsEditActivityDelegate chatRightsEditActivityDelegate = this.delegate;
                if (chatRightsEditActivityDelegate != null) {
                    chatRightsEditActivityDelegate.didSetRights(0, this.adminRights, this.bannedRights, this.currentRank);
                }
                finishFragment();
            } else if (i3 == 1) {
                TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights3 = new TLRPC$TL_chatBannedRights();
                this.bannedRights = tLRPC$TL_chatBannedRights3;
                tLRPC$TL_chatBannedRights3.view_messages = true;
                tLRPC$TL_chatBannedRights3.send_media = true;
                tLRPC$TL_chatBannedRights3.send_messages = true;
                tLRPC$TL_chatBannedRights3.send_stickers = true;
                tLRPC$TL_chatBannedRights3.send_gifs = true;
                tLRPC$TL_chatBannedRights3.send_games = true;
                tLRPC$TL_chatBannedRights3.send_inline = true;
                tLRPC$TL_chatBannedRights3.embed_links = true;
                tLRPC$TL_chatBannedRights3.pin_messages = true;
                tLRPC$TL_chatBannedRights3.send_polls = true;
                tLRPC$TL_chatBannedRights3.invite_users = true;
                tLRPC$TL_chatBannedRights3.change_info = true;
                tLRPC$TL_chatBannedRights3.until_date = 0;
                onDonePressed();
            }
        } else if (i2 == this.transferOwnerRow) {
            lambda$initTransfer$8((TLRPC$InputCheckPasswordSRP) null, (TwoStepVerificationActivity) null);
        } else if (i2 == this.untilDateRow) {
            if (getParentActivity() != null) {
                BottomSheet.Builder builder = new BottomSheet.Builder(context2);
                builder.setApplyTopPadding(false);
                LinearLayout linearLayout = new LinearLayout(context2);
                linearLayout.setOrientation(1);
                HeaderCell headerCell = new HeaderCell(context, "dialogTextBlue2", 23, 15, false);
                headerCell.setHeight(47);
                headerCell.setText(LocaleController.getString("UserRestrictionsDuration", NUM));
                linearLayout.addView(headerCell);
                LinearLayout linearLayout2 = new LinearLayout(context2);
                linearLayout2.setOrientation(1);
                linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
                BottomSheet.BottomSheetCell[] bottomSheetCellArr = new BottomSheet.BottomSheetCell[5];
                for (int i4 = 0; i4 < 5; i4++) {
                    bottomSheetCellArr[i4] = new BottomSheet.BottomSheetCell(context2, 0);
                    bottomSheetCellArr[i4].setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
                    bottomSheetCellArr[i4].setTag(Integer.valueOf(i4));
                    bottomSheetCellArr[i4].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    if (i4 == 0) {
                        str = LocaleController.getString("UserRestrictionsUntilForever", NUM);
                    } else if (i4 == 1) {
                        str = LocaleController.formatPluralString("Days", 1, new Object[0]);
                    } else if (i4 == 2) {
                        str = LocaleController.formatPluralString("Weeks", 1, new Object[0]);
                    } else if (i4 != 3) {
                        str = LocaleController.getString("UserRestrictionsCustom", NUM);
                    } else {
                        str = LocaleController.formatPluralString("Months", 1, new Object[0]);
                    }
                    bottomSheetCellArr[i4].setTextAndIcon((CharSequence) str, 0);
                    linearLayout2.addView(bottomSheetCellArr[i4], LayoutHelper.createLinear(-1, -2));
                    bottomSheetCellArr[i4].setOnClickListener(new ChatRightsEditActivity$$ExternalSyntheticLambda12(this, builder));
                }
                builder.setCustomView(linearLayout);
                showDialog(builder.create());
            }
        } else if (view2 instanceof TextCheckCell2) {
            TextCheckCell2 textCheckCell2 = (TextCheckCell2) view2;
            if (textCheckCell2.hasIcon()) {
                if (this.currentType != 2) {
                    new AlertDialog.Builder((Context) getParentActivity()).setTitle(LocaleController.getString("UserRestrictionsCantModify", NUM)).setMessage(LocaleController.getString("UserRestrictionsCantModifyDisabled", NUM)).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null).create().show();
                }
            } else if (!textCheckCell2.isEnabled()) {
                int i5 = this.currentType;
                if (i5 != 2 && i5 != 0) {
                    return;
                }
                if ((i2 == this.changeInfoRow && (tLRPC$TL_chatBannedRights2 = this.defaultBannedRights) != null && !tLRPC$TL_chatBannedRights2.change_info) || (i2 == this.pinMessagesRow && (tLRPC$TL_chatBannedRights = this.defaultBannedRights) != null && !tLRPC$TL_chatBannedRights.pin_messages)) {
                    new AlertDialog.Builder((Context) getParentActivity()).setTitle(LocaleController.getString("UserRestrictionsCantModify", NUM)).setMessage(LocaleController.getString("UserRestrictionsCantModifyEnabled", NUM)).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null).create().show();
                }
            } else {
                if (this.currentType != 2) {
                    textCheckCell2.setChecked(!textCheckCell2.isChecked());
                }
                boolean isChecked = textCheckCell2.isChecked();
                if (i2 == this.manageRow) {
                    isChecked = !this.asAdmin;
                    this.asAdmin = isChecked;
                    updateAsAdmin(true);
                } else if (i2 == this.changeInfoRow) {
                    int i6 = this.currentType;
                    if (i6 == 0 || i6 == 2) {
                        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = this.adminRights;
                        isChecked = !tLRPC$TL_chatAdminRights.change_info;
                        tLRPC$TL_chatAdminRights.change_info = isChecked;
                    } else {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights4 = this.bannedRights;
                        isChecked = !tLRPC$TL_chatBannedRights4.change_info;
                        tLRPC$TL_chatBannedRights4.change_info = isChecked;
                    }
                } else if (i2 == this.postMessagesRow) {
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2 = this.adminRights;
                    isChecked = !tLRPC$TL_chatAdminRights2.post_messages;
                    tLRPC$TL_chatAdminRights2.post_messages = isChecked;
                } else if (i2 == this.editMesagesRow) {
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights3 = this.adminRights;
                    isChecked = !tLRPC$TL_chatAdminRights3.edit_messages;
                    tLRPC$TL_chatAdminRights3.edit_messages = isChecked;
                } else if (i2 == this.deleteMessagesRow) {
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights4 = this.adminRights;
                    isChecked = !tLRPC$TL_chatAdminRights4.delete_messages;
                    tLRPC$TL_chatAdminRights4.delete_messages = isChecked;
                } else if (i2 == this.addAdminsRow) {
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights5 = this.adminRights;
                    isChecked = !tLRPC$TL_chatAdminRights5.add_admins;
                    tLRPC$TL_chatAdminRights5.add_admins = isChecked;
                } else if (i2 == this.anonymousRow) {
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights6 = this.adminRights;
                    isChecked = !tLRPC$TL_chatAdminRights6.anonymous;
                    tLRPC$TL_chatAdminRights6.anonymous = isChecked;
                } else if (i2 == this.banUsersRow) {
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights7 = this.adminRights;
                    isChecked = !tLRPC$TL_chatAdminRights7.ban_users;
                    tLRPC$TL_chatAdminRights7.ban_users = isChecked;
                } else if (i2 == this.startVoiceChatRow) {
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights8 = this.adminRights;
                    isChecked = !tLRPC$TL_chatAdminRights8.manage_call;
                    tLRPC$TL_chatAdminRights8.manage_call = isChecked;
                } else if (i2 == this.addUsersRow) {
                    int i7 = this.currentType;
                    if (i7 == 0 || i7 == 2) {
                        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights9 = this.adminRights;
                        isChecked = !tLRPC$TL_chatAdminRights9.invite_users;
                        tLRPC$TL_chatAdminRights9.invite_users = isChecked;
                    } else {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights5 = this.bannedRights;
                        isChecked = !tLRPC$TL_chatBannedRights5.invite_users;
                        tLRPC$TL_chatBannedRights5.invite_users = isChecked;
                    }
                } else if (i2 == this.pinMessagesRow) {
                    int i8 = this.currentType;
                    if (i8 == 0 || i8 == 2) {
                        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights10 = this.adminRights;
                        isChecked = !tLRPC$TL_chatAdminRights10.pin_messages;
                        tLRPC$TL_chatAdminRights10.pin_messages = isChecked;
                    } else {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights6 = this.bannedRights;
                        isChecked = !tLRPC$TL_chatBannedRights6.pin_messages;
                        tLRPC$TL_chatBannedRights6.pin_messages = isChecked;
                    }
                } else if (this.currentType == 1 && this.bannedRights != null) {
                    boolean z2 = !textCheckCell2.isChecked();
                    int i9 = this.sendMessagesRow;
                    if (i2 == i9) {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights7 = this.bannedRights;
                        isChecked = !tLRPC$TL_chatBannedRights7.send_messages;
                        tLRPC$TL_chatBannedRights7.send_messages = isChecked;
                    } else if (i2 == this.sendMediaRow) {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights8 = this.bannedRights;
                        isChecked = !tLRPC$TL_chatBannedRights8.send_media;
                        tLRPC$TL_chatBannedRights8.send_media = isChecked;
                    } else if (i2 == this.sendStickersRow) {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights9 = this.bannedRights;
                        isChecked = !tLRPC$TL_chatBannedRights9.send_stickers;
                        tLRPC$TL_chatBannedRights9.send_inline = isChecked;
                        tLRPC$TL_chatBannedRights9.send_gifs = isChecked;
                        tLRPC$TL_chatBannedRights9.send_games = isChecked;
                        tLRPC$TL_chatBannedRights9.send_stickers = isChecked;
                    } else if (i2 == this.embedLinksRow) {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights10 = this.bannedRights;
                        isChecked = !tLRPC$TL_chatBannedRights10.embed_links;
                        tLRPC$TL_chatBannedRights10.embed_links = isChecked;
                    } else if (i2 == this.sendPollsRow) {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights11 = this.bannedRights;
                        isChecked = !tLRPC$TL_chatBannedRights11.send_polls;
                        tLRPC$TL_chatBannedRights11.send_polls = isChecked;
                    }
                    if (z2) {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights12 = this.bannedRights;
                        if (tLRPC$TL_chatBannedRights12.view_messages && !tLRPC$TL_chatBannedRights12.send_messages) {
                            tLRPC$TL_chatBannedRights12.send_messages = true;
                            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i9);
                            if (findViewHolderForAdapterPosition != null) {
                                ((TextCheckCell2) findViewHolderForAdapterPosition.itemView).setChecked(false);
                            }
                        }
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights13 = this.bannedRights;
                        if ((tLRPC$TL_chatBannedRights13.view_messages || tLRPC$TL_chatBannedRights13.send_messages) && !tLRPC$TL_chatBannedRights13.send_media) {
                            tLRPC$TL_chatBannedRights13.send_media = true;
                            RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = this.listView.findViewHolderForAdapterPosition(this.sendMediaRow);
                            if (findViewHolderForAdapterPosition2 != null) {
                                ((TextCheckCell2) findViewHolderForAdapterPosition2.itemView).setChecked(false);
                            }
                        }
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights14 = this.bannedRights;
                        if ((tLRPC$TL_chatBannedRights14.view_messages || tLRPC$TL_chatBannedRights14.send_messages) && !tLRPC$TL_chatBannedRights14.send_polls) {
                            tLRPC$TL_chatBannedRights14.send_polls = true;
                            RecyclerView.ViewHolder findViewHolderForAdapterPosition3 = this.listView.findViewHolderForAdapterPosition(this.sendPollsRow);
                            if (findViewHolderForAdapterPosition3 != null) {
                                ((TextCheckCell2) findViewHolderForAdapterPosition3.itemView).setChecked(false);
                            }
                        }
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights15 = this.bannedRights;
                        if ((tLRPC$TL_chatBannedRights15.view_messages || tLRPC$TL_chatBannedRights15.send_messages) && !tLRPC$TL_chatBannedRights15.send_stickers) {
                            tLRPC$TL_chatBannedRights15.send_inline = true;
                            tLRPC$TL_chatBannedRights15.send_gifs = true;
                            tLRPC$TL_chatBannedRights15.send_games = true;
                            tLRPC$TL_chatBannedRights15.send_stickers = true;
                            RecyclerView.ViewHolder findViewHolderForAdapterPosition4 = this.listView.findViewHolderForAdapterPosition(this.sendStickersRow);
                            if (findViewHolderForAdapterPosition4 != null) {
                                ((TextCheckCell2) findViewHolderForAdapterPosition4.itemView).setChecked(false);
                            }
                        }
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights16 = this.bannedRights;
                        if ((tLRPC$TL_chatBannedRights16.view_messages || tLRPC$TL_chatBannedRights16.send_messages) && !tLRPC$TL_chatBannedRights16.embed_links) {
                            tLRPC$TL_chatBannedRights16.embed_links = true;
                            RecyclerView.ViewHolder findViewHolderForAdapterPosition5 = this.listView.findViewHolderForAdapterPosition(this.embedLinksRow);
                            if (findViewHolderForAdapterPosition5 != null) {
                                ((TextCheckCell2) findViewHolderForAdapterPosition5.itemView).setChecked(false);
                            }
                        }
                    } else {
                        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights17 = this.bannedRights;
                        boolean z3 = tLRPC$TL_chatBannedRights17.send_messages;
                        if ((!z3 || !tLRPC$TL_chatBannedRights17.embed_links || !tLRPC$TL_chatBannedRights17.send_inline || !tLRPC$TL_chatBannedRights17.send_media || !tLRPC$TL_chatBannedRights17.send_polls) && tLRPC$TL_chatBannedRights17.view_messages) {
                            tLRPC$TL_chatBannedRights17.view_messages = false;
                        }
                        if ((!tLRPC$TL_chatBannedRights17.embed_links || !tLRPC$TL_chatBannedRights17.send_inline || !tLRPC$TL_chatBannedRights17.send_media || !tLRPC$TL_chatBannedRights17.send_polls) && z3) {
                            tLRPC$TL_chatBannedRights17.send_messages = false;
                            RecyclerView.ViewHolder findViewHolderForAdapterPosition6 = this.listView.findViewHolderForAdapterPosition(i9);
                            if (findViewHolderForAdapterPosition6 != null) {
                                ((TextCheckCell2) findViewHolderForAdapterPosition6.itemView).setChecked(true);
                            }
                        }
                    }
                }
                if (this.currentType == 2) {
                    if (this.asAdmin && isChecked) {
                        z = true;
                    }
                    textCheckCell2.setChecked(z);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(getParentActivity(), new ChatRightsEditActivity$$ExternalSyntheticLambda2(this), instance.get(1), instance.get(2), instance.get(5));
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
                datePickerDialog.setButton(-2, LocaleController.getString("Cancel", NUM), ChatRightsEditActivity$$ExternalSyntheticLambda10.INSTANCE);
                if (Build.VERSION.SDK_INT >= 21) {
                    datePickerDialog.setOnShowListener(new ChatRightsEditActivity$$ExternalSyntheticLambda11(datePicker));
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
            TimePickerDialog timePickerDialog = new TimePickerDialog(getParentActivity(), new ChatRightsEditActivity$$ExternalSyntheticLambda3(this, (int) (instance.getTime().getTime() / 1000)), 0, 0, true);
            timePickerDialog.setButton(-1, LocaleController.getString("Set", NUM), timePickerDialog);
            timePickerDialog.setButton(-2, LocaleController.getString("Cancel", NUM), ChatRightsEditActivity$$ExternalSyntheticLambda9.INSTANCE);
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
                getConnectionsManager().sendRequest(tLRPC$TL_channels_editCreator, new ChatRightsEditActivity$$ExternalSyntheticLambda22(this, tLRPC$InputCheckPasswordSRP, twoStepVerificationActivity, tLRPC$TL_channels_editCreator));
                return;
            }
            MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chatId, this, new ChatRightsEditActivity$$ExternalSyntheticLambda21(this, tLRPC$InputCheckPasswordSRP, twoStepVerificationActivity));
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
        AndroidUtilities.runOnUIThread(new ChatRightsEditActivity$$ExternalSyntheticLambda16(this, tLRPC$TL_error, tLRPC$InputCheckPasswordSRP, twoStepVerificationActivity, tLRPC$TL_channels_editCreator));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initTransfer$13(TLRPC$TL_error tLRPC$TL_error, TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP, TwoStepVerificationActivity twoStepVerificationActivity, TLRPC$TL_channels_editCreator tLRPC$TL_channels_editCreator) {
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
                        builder.setPositiveButton(LocaleController.getString("EditAdminTransferChangeOwner", NUM), new ChatRightsEditActivity$$ExternalSyntheticLambda6(this));
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
                        linearLayout3.addView(imageView2, LayoutHelper.createLinear(-2, -2, 5));
                    } else {
                        linearLayout3.addView(imageView2, LayoutHelper.createLinear(-2, -2));
                        linearLayout3.addView(textView3, LayoutHelper.createLinear(-1, -2));
                    }
                    if ("PASSWORD_MISSING".equals(tLRPC$TL_error2.text)) {
                        builder2.setPositiveButton(LocaleController.getString("EditAdminTransferSetPassword", NUM), new ChatRightsEditActivity$$ExternalSyntheticLambda7(this));
                        builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    } else {
                        TextView textView4 = new TextView(getParentActivity());
                        textView4.setTextColor(Theme.getColor("dialogTextBlack"));
                        textView4.setTextSize(1, 16.0f);
                        textView4.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                        textView4.setText(LocaleController.getString("EditAdminTransferAlertText3", NUM));
                        linearLayout.addView(textView4, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
                        builder2.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                    }
                    showDialog(builder2.create());
                } else if ("SRP_ID_INVALID".equals(tLRPC$TL_error2.text)) {
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new ChatRightsEditActivity$$ExternalSyntheticLambda23(this, twoStepVerificationActivity2), 8);
                } else if (!tLRPC$TL_error2.text.equals("CHANNELS_TOO_MUCH")) {
                    if (twoStepVerificationActivity2 != null) {
                        twoStepVerificationActivity.needHideProgress();
                        twoStepVerificationActivity.finishFragment();
                    }
                    AlertsCreator.showAddUserAlert(tLRPC$TL_error2.text, this, this.isChannel, tLRPC$TL_channels_editCreator);
                } else if (getParentActivity() == null || AccountInstance.getInstance(this.currentAccount).getUserConfig().isPremium()) {
                    presentFragment(new TooManyCommunitiesActivity(1));
                } else {
                    showDialog(new LimitReachedBottomSheet(this, getParentActivity(), 5, this.currentAccount));
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
        twoStepVerificationActivity.setDelegate(new ChatRightsEditActivity$$ExternalSyntheticLambda26(this, twoStepVerificationActivity));
        presentFragment(twoStepVerificationActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initTransfer$10(DialogInterface dialogInterface, int i) {
        presentFragment(new TwoStepVerificationSetupActivity(6, (TLRPC$TL_account_password) null));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initTransfer$12(TwoStepVerificationActivity twoStepVerificationActivity, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ChatRightsEditActivity$$ExternalSyntheticLambda15(this, tLRPC$TL_error, tLObject, twoStepVerificationActivity));
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
        this.manageRow = -1;
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
        this.addBotButtonRow = -1;
        this.rowCount = 3;
        int i2 = this.currentType;
        if (i2 == 0 || i2 == 2) {
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
                if (i2 == 2) {
                    this.rowCount = 3 + 1;
                    this.manageRow = 3;
                }
                int i9 = this.rowCount;
                int i10 = i9 + 1;
                this.rowCount = i10;
                this.changeInfoRow = i9;
                int i11 = i10 + 1;
                this.rowCount = i11;
                this.deleteMessagesRow = i10;
                int i12 = i11 + 1;
                this.rowCount = i12;
                this.banUsersRow = i11;
                int i13 = i12 + 1;
                this.rowCount = i13;
                this.addUsersRow = i12;
                int i14 = i13 + 1;
                this.rowCount = i14;
                this.pinMessagesRow = i13;
                int i15 = i14 + 1;
                this.rowCount = i15;
                this.startVoiceChatRow = i14;
                int i16 = i15 + 1;
                this.rowCount = i16;
                this.addAdminsRow = i15;
                this.rowCount = i16 + 1;
                this.anonymousRow = i16;
            }
        } else if (i2 == 1) {
            int i17 = 3 + 1;
            this.rowCount = i17;
            this.sendMessagesRow = 3;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.sendMediaRow = i17;
            int i19 = i18 + 1;
            this.rowCount = i19;
            this.sendStickersRow = i18;
            int i20 = i19 + 1;
            this.rowCount = i20;
            this.sendPollsRow = i19;
            int i21 = i20 + 1;
            this.rowCount = i21;
            this.embedLinksRow = i20;
            int i22 = i21 + 1;
            this.rowCount = i22;
            this.addUsersRow = i21;
            int i23 = i22 + 1;
            this.rowCount = i23;
            this.pinMessagesRow = i22;
            int i24 = i23 + 1;
            this.rowCount = i24;
            this.changeInfoRow = i23;
            int i25 = i24 + 1;
            this.rowCount = i25;
            this.untilSectionRow = i24;
            this.rowCount = i25 + 1;
            this.untilDateRow = i25;
        }
        int i26 = this.rowCount;
        if (this.canEdit) {
            if (!this.isChannel && (i2 == 0 || (i2 == 2 && this.asAdmin))) {
                int i27 = i26 + 1;
                this.rowCount = i27;
                this.rightsShadowRow = i26;
                int i28 = i27 + 1;
                this.rowCount = i28;
                this.rankHeaderRow = i27;
                int i29 = i28 + 1;
                this.rowCount = i29;
                this.rankRow = i28;
                this.rowCount = i29 + 1;
                this.rankInfoRow = i29;
            }
            TLRPC$Chat tLRPC$Chat = this.currentChat;
            if (tLRPC$Chat != null && tLRPC$Chat.creator && i2 == 0 && hasAllAdminRights() && !this.currentUser.bot) {
                int i30 = this.rightsShadowRow;
                if (i30 == -1) {
                    int i31 = this.rowCount;
                    this.rowCount = i31 + 1;
                    this.transferOwnerShadowRow = i31;
                }
                int i32 = this.rowCount;
                int i33 = i32 + 1;
                this.rowCount = i33;
                this.transferOwnerRow = i32;
                if (i30 != -1) {
                    this.rowCount = i33 + 1;
                    this.transferOwnerShadowRow = i33;
                }
            }
            if (this.initialIsSet) {
                if (this.rightsShadowRow == -1) {
                    int i34 = this.rowCount;
                    this.rowCount = i34 + 1;
                    this.rightsShadowRow = i34;
                }
                int i35 = this.rowCount;
                int i36 = i35 + 1;
                this.rowCount = i36;
                this.removeAdminRow = i35;
                this.rowCount = i36 + 1;
                this.removeAdminShadowRow = i36;
            }
        } else if (i2 != 0) {
            this.rowCount = i26 + 1;
            this.rightsShadowRow = i26;
        } else if (this.isChannel || (this.currentRank.isEmpty() && (!this.currentChat.creator || !UserObject.isUserSelf(this.currentUser)))) {
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
        if (this.currentType == 2) {
            int i43 = this.rowCount;
            this.rowCount = i43 + 1;
            this.addBotButtonRow = i43;
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
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002f, code lost:
        if (r0.codePointCount(0, r0.length()) <= 16) goto L_0x0031;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003d, code lost:
        if (isDefaultAdminRights() == false) goto L_0x003f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDonePressed() {
        /*
            r22 = this;
            r13 = r22
            boolean r0 = r13.loading
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            org.telegram.tgnet.TLRPC$Chat r0 = r13.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            r1 = 16
            r2 = -1
            r3 = 2
            r4 = 1
            r5 = 0
            if (r0 != 0) goto L_0x0056
            int r0 = r13.currentType
            if (r0 == r4) goto L_0x003f
            if (r0 != 0) goto L_0x0031
            boolean r0 = r22.isDefaultAdminRights()
            if (r0 == 0) goto L_0x003f
            int r0 = r13.rankRow
            if (r0 == r2) goto L_0x0031
            java.lang.String r0 = r13.currentRank
            int r6 = r0.length()
            int r0 = r0.codePointCount(r5, r6)
            if (r0 > r1) goto L_0x003f
        L_0x0031:
            int r0 = r13.currentType
            if (r0 != r3) goto L_0x0056
            java.lang.String r0 = r13.currentRank
            if (r0 != 0) goto L_0x003f
            boolean r0 = r22.isDefaultAdminRights()
            if (r0 != 0) goto L_0x0056
        L_0x003f:
            int r0 = r13.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            android.app.Activity r1 = r22.getParentActivity()
            long r2 = r13.chatId
            org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda20 r5 = new org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda20
            r5.<init>(r13)
            r4 = r22
            r0.convertToMegaGroup(r1, r2, r4, r5)
            return
        L_0x0056:
            int r0 = r13.currentType
            if (r0 == 0) goto L_0x005c
            if (r0 != r3) goto L_0x00d8
        L_0x005c:
            int r0 = r13.rankRow
            if (r0 == r2) goto L_0x0098
            java.lang.String r0 = r13.currentRank
            int r2 = r0.length()
            int r0 = r0.codePointCount(r5, r2)
            if (r0 <= r1) goto L_0x0098
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            int r1 = r13.rankRow
            r0.smoothScrollToPosition(r1)
            android.app.Activity r0 = r22.getParentActivity()
            java.lang.String r1 = "vibrator"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.os.Vibrator r0 = (android.os.Vibrator) r0
            if (r0 == 0) goto L_0x0086
            r1 = 200(0xc8, double:9.9E-322)
            r0.vibrate(r1)
        L_0x0086:
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            int r1 = r13.rankHeaderRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x0097
            android.view.View r0 = r0.itemView
            r1 = 1073741824(0x40000000, float:2.0)
            org.telegram.messenger.AndroidUtilities.shakeView(r0, r1, r5)
        L_0x0097:
            return
        L_0x0098:
            boolean r0 = r13.isChannel
            if (r0 == 0) goto L_0x00a3
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r13.adminRights
            r0.ban_users = r5
            r0.pin_messages = r5
            goto L_0x00a9
        L_0x00a3:
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r13.adminRights
            r0.edit_messages = r5
            r0.post_messages = r5
        L_0x00a9:
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r13.adminRights
            boolean r1 = r0.change_info
            if (r1 != 0) goto L_0x00d6
            boolean r1 = r0.post_messages
            if (r1 != 0) goto L_0x00d6
            boolean r1 = r0.edit_messages
            if (r1 != 0) goto L_0x00d6
            boolean r1 = r0.delete_messages
            if (r1 != 0) goto L_0x00d6
            boolean r1 = r0.ban_users
            if (r1 != 0) goto L_0x00d6
            boolean r1 = r0.invite_users
            if (r1 != 0) goto L_0x00d6
            boolean r1 = r0.pin_messages
            if (r1 != 0) goto L_0x00d6
            boolean r1 = r0.add_admins
            if (r1 != 0) goto L_0x00d6
            boolean r1 = r0.anonymous
            if (r1 != 0) goto L_0x00d6
            boolean r1 = r0.manage_call
            if (r1 != 0) goto L_0x00d6
            r0.other = r4
            goto L_0x00d8
        L_0x00d6:
            r0.other = r5
        L_0x00d8:
            int r0 = r13.currentType
            if (r0 != 0) goto L_0x010c
            org.telegram.ui.ChatRightsEditActivity$ChatRightsEditActivityDelegate r0 = r13.delegate
            if (r0 != 0) goto L_0x00e2
            r14 = 1
            goto L_0x00e3
        L_0x00e2:
            r14 = 0
        L_0x00e3:
            r13.setLoading(r4)
            int r0 = r13.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r1 = r13.chatId
            org.telegram.tgnet.TLRPC$User r3 = r13.currentUser
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r4 = r13.adminRights
            java.lang.String r5 = r13.currentRank
            boolean r6 = r13.isChannel
            boolean r8 = r13.isAddingNew
            r9 = 0
            r10 = 0
            org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda13 r11 = new org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda13
            r11.<init>(r13)
            org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda17 r12 = new org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda17
            r12.<init>(r13)
            r7 = r22
            r0.setUserAdminRole(r1, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r4 = r14
            goto L_0x0208
        L_0x010c:
            if (r0 != r4) goto L_0x015c
            int r0 = r13.currentAccount
            org.telegram.messenger.MessagesController r14 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r0 = r13.chatId
            org.telegram.tgnet.TLRPC$User r2 = r13.currentUser
            r18 = 0
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r6 = r13.bannedRights
            boolean r7 = r13.isChannel
            org.telegram.ui.ActionBar.BaseFragment r21 = r13.getFragmentForAlert(r4)
            r15 = r0
            r17 = r2
            r19 = r6
            r20 = r7
            r14.setParticipantBannedRole(r15, r17, r18, r19, r20, r21)
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r13.bannedRights
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
            r0.until_date = r5
            goto L_0x014f
        L_0x014e:
            r3 = 1
        L_0x014f:
            org.telegram.ui.ChatRightsEditActivity$ChatRightsEditActivityDelegate r1 = r13.delegate
            if (r1 == 0) goto L_0x0208
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r13.adminRights
            java.lang.String r5 = r13.currentRank
            r1.didSetRights(r3, r2, r0, r5)
            goto L_0x0208
        L_0x015c:
            if (r0 != r3) goto L_0x0208
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r22.getParentActivity()
            r0.<init>((android.content.Context) r1)
            boolean r1 = r13.asAdmin
            r2 = 2131624255(0x7f0e013f, float:1.8875685E38)
            java.lang.String r6 = "AddBot"
            if (r1 == 0) goto L_0x017a
            r1 = 2131624256(0x7f0e0140, float:1.8875687E38)
            java.lang.String r7 = "AddBotAdmin"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r7, r1)
            goto L_0x017e
        L_0x017a:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r6, r2)
        L_0x017e:
            r0.setTitle(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r13.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 == 0) goto L_0x0191
            org.telegram.tgnet.TLRPC$Chat r1 = r13.currentChat
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x0191
            r1 = 1
            goto L_0x0192
        L_0x0191:
            r1 = 0
        L_0x0192:
            org.telegram.tgnet.TLRPC$Chat r7 = r13.currentChat
            if (r7 != 0) goto L_0x0199
            java.lang.String r7 = ""
            goto L_0x019b
        L_0x0199:
            java.lang.String r7 = r7.title
        L_0x019b:
            boolean r8 = r13.asAdmin
            if (r8 == 0) goto L_0x01bd
            if (r1 == 0) goto L_0x01af
            r1 = 2131624261(0x7f0e0145, float:1.8875697E38)
            java.lang.Object[] r3 = new java.lang.Object[r4]
            r3[r5] = r7
            java.lang.String r4 = "AddBotMessageAdminChannel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
            goto L_0x01d2
        L_0x01af:
            r1 = 2131624262(0x7f0e0146, float:1.8875699E38)
            java.lang.Object[] r3 = new java.lang.Object[r4]
            r3[r5] = r7
            java.lang.String r4 = "AddBotMessageAdminGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
            goto L_0x01d2
        L_0x01bd:
            r1 = 2131624283(0x7f0e015b, float:1.8875741E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            org.telegram.tgnet.TLRPC$User r8 = r13.currentUser
            java.lang.String r8 = org.telegram.messenger.UserObject.getUserName(r8)
            r3[r5] = r8
            r3[r4] = r7
            java.lang.String r4 = "AddMembersAlertNamesText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
        L_0x01d2:
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r0.setMessage(r1)
            r1 = 2131624836(0x7f0e0384, float:1.8876863E38)
            java.lang.String r3 = "Cancel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r3 = 0
            r0.setNegativeButton(r1, r3)
            boolean r1 = r13.asAdmin
            if (r1 == 0) goto L_0x01f4
            r1 = 2131624253(0x7f0e013d, float:1.887568E38)
            java.lang.String r2 = "AddAsAdmin"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x01f8
        L_0x01f4:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r6, r2)
        L_0x01f8:
            org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda8 r2 = new org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda8
            r2.<init>(r13)
            r0.setPositiveButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r13.showDialog(r0)
            r4 = 0
        L_0x0208:
            if (r4 == 0) goto L_0x020d
            r22.finishFragment()
        L_0x020d:
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDonePressed$16() {
        ChatRightsEditActivityDelegate chatRightsEditActivityDelegate = this.delegate;
        if (chatRightsEditActivityDelegate != null) {
            TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = this.adminRights;
            chatRightsEditActivityDelegate.didSetRights((tLRPC$TL_chatAdminRights.change_info || tLRPC$TL_chatAdminRights.post_messages || tLRPC$TL_chatAdminRights.edit_messages || tLRPC$TL_chatAdminRights.delete_messages || tLRPC$TL_chatAdminRights.ban_users || tLRPC$TL_chatAdminRights.invite_users || tLRPC$TL_chatAdminRights.pin_messages || tLRPC$TL_chatAdminRights.add_admins || tLRPC$TL_chatAdminRights.anonymous || tLRPC$TL_chatAdminRights.manage_call || tLRPC$TL_chatAdminRights.other) ? 1 : 0, tLRPC$TL_chatAdminRights, this.bannedRights, this.currentRank);
            finishFragment();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onDonePressed$17(TLRPC$TL_error tLRPC$TL_error) {
        setLoading(false);
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDonePressed$21(DialogInterface dialogInterface, int i) {
        setLoading(true);
        ChatRightsEditActivity$$ExternalSyntheticLambda14 chatRightsEditActivity$$ExternalSyntheticLambda14 = new ChatRightsEditActivity$$ExternalSyntheticLambda14(this);
        if (this.asAdmin || this.initialAsAdmin) {
            getMessagesController().setUserAdminRole(this.currentChat.id, this.currentUser, this.asAdmin ? this.adminRights : emptyAdminRights(false), this.currentRank, false, this, this.isAddingNew, this.asAdmin, this.botHash, chatRightsEditActivity$$ExternalSyntheticLambda14, new ChatRightsEditActivity$$ExternalSyntheticLambda19(this));
        } else {
            getMessagesController().addUserToChat(this.currentChat.id, this.currentUser, 0, this.botHash, this, true, chatRightsEditActivity$$ExternalSyntheticLambda14, new ChatRightsEditActivity$$ExternalSyntheticLambda18(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDonePressed$18() {
        ChatRightsEditActivityDelegate chatRightsEditActivityDelegate = this.delegate;
        if (chatRightsEditActivityDelegate != null) {
            chatRightsEditActivityDelegate.didSetRights(0, this.asAdmin ? this.adminRights : null, (TLRPC$TL_chatBannedRights) null, this.currentRank);
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        bundle.putLong("chat_id", this.currentChat.id);
        if (!getMessagesController().checkCanOpenChat(bundle, this)) {
            setLoading(false);
            return;
        }
        ChatActivity chatActivity = new ChatActivity(bundle);
        presentFragment(chatActivity, true);
        if (BulletinFactory.canShowBulletin(chatActivity)) {
            boolean z = this.isAddingNew;
            if (z && this.asAdmin) {
                BulletinFactory.createAddedAsAdminBulletin(chatActivity, this.currentUser.first_name).show();
            } else if (!z && !this.initialAsAdmin && this.asAdmin) {
                BulletinFactory.createPromoteToAdminBulletin(chatActivity, this.currentUser.first_name).show();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onDonePressed$19(TLRPC$TL_error tLRPC$TL_error) {
        setLoading(false);
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onDonePressed$20(TLRPC$TL_error tLRPC$TL_error) {
        setLoading(false);
        return true;
    }

    public void setLoading(boolean z) {
        ValueAnimator valueAnimator = this.doneDrawableAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.loading = !z;
        this.actionBar.getBackButton().setEnabled(!z);
        CrossfadeDrawable crossfadeDrawable = this.doneDrawable;
        if (crossfadeDrawable != null) {
            float[] fArr = new float[2];
            fArr[0] = crossfadeDrawable.getProgress();
            fArr[1] = z ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.doneDrawableAnimator = ofFloat;
            ofFloat.addUpdateListener(new ChatRightsEditActivity$$ExternalSyntheticLambda0(this));
            this.doneDrawableAnimator.setDuration((long) (Math.abs(this.doneDrawable.getProgress() - (z ? 1.0f : 0.0f)) * 150.0f));
            this.doneDrawableAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setLoading$22(ValueAnimator valueAnimator) {
        this.doneDrawable.setProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
        this.doneDrawable.invalidateSelf();
    }

    public void setDelegate(ChatRightsEditActivityDelegate chatRightsEditActivityDelegate) {
        this.delegate = chatRightsEditActivityDelegate;
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        boolean z;
        int i = this.currentType;
        if (i == 2) {
            return true;
        }
        if (i == 1) {
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
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new ChatRightsEditActivity$$ExternalSyntheticLambda4(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new ChatRightsEditActivity$$ExternalSyntheticLambda5(this));
        showDialog(builder.create());
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$23(DialogInterface dialogInterface, int i) {
        onDonePressed();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$24(DialogInterface dialogInterface, int i) {
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
            if (ChatRightsEditActivity.this.currentType == 2) {
                setHasStableIds(true);
            }
            this.mContext = context;
        }

        public long getItemId(int i) {
            if (ChatRightsEditActivity.this.currentType != 2) {
                return super.getItemId(i);
            }
            if (i == ChatRightsEditActivity.this.manageRow) {
                return 1;
            }
            if (i == ChatRightsEditActivity.this.changeInfoRow) {
                return 2;
            }
            if (i == ChatRightsEditActivity.this.postMessagesRow) {
                return 3;
            }
            if (i == ChatRightsEditActivity.this.editMesagesRow) {
                return 4;
            }
            if (i == ChatRightsEditActivity.this.deleteMessagesRow) {
                return 5;
            }
            if (i == ChatRightsEditActivity.this.addAdminsRow) {
                return 6;
            }
            if (i == ChatRightsEditActivity.this.anonymousRow) {
                return 7;
            }
            if (i == ChatRightsEditActivity.this.banUsersRow) {
                return 8;
            }
            if (i == ChatRightsEditActivity.this.addUsersRow) {
                return 9;
            }
            if (i == ChatRightsEditActivity.this.pinMessagesRow) {
                return 10;
            }
            if (i == ChatRightsEditActivity.this.rightsShadowRow) {
                return 11;
            }
            if (i == ChatRightsEditActivity.this.removeAdminRow) {
                return 12;
            }
            if (i == ChatRightsEditActivity.this.removeAdminShadowRow) {
                return 13;
            }
            if (i == ChatRightsEditActivity.this.cantEditInfoRow) {
                return 14;
            }
            if (i == ChatRightsEditActivity.this.transferOwnerShadowRow) {
                return 15;
            }
            if (i == ChatRightsEditActivity.this.transferOwnerRow) {
                return 16;
            }
            if (i == ChatRightsEditActivity.this.rankHeaderRow) {
                return 17;
            }
            if (i == ChatRightsEditActivity.this.rankRow) {
                return 18;
            }
            if (i == ChatRightsEditActivity.this.rankInfoRow) {
                return 19;
            }
            if (i == ChatRightsEditActivity.this.sendMessagesRow) {
                return 20;
            }
            if (i == ChatRightsEditActivity.this.sendMediaRow) {
                return 21;
            }
            if (i == ChatRightsEditActivity.this.sendStickersRow) {
                return 22;
            }
            if (i == ChatRightsEditActivity.this.sendPollsRow) {
                return 23;
            }
            if (i == ChatRightsEditActivity.this.embedLinksRow) {
                return 24;
            }
            if (i == ChatRightsEditActivity.this.startVoiceChatRow) {
                return 25;
            }
            if (i == ChatRightsEditActivity.this.untilSectionRow) {
                return 26;
            }
            if (i == ChatRightsEditActivity.this.untilDateRow) {
                return 27;
            }
            return i == ChatRightsEditActivity.this.addBotButtonRow ? 28 : 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (ChatRightsEditActivity.this.currentChat.creator && ((ChatRightsEditActivity.this.currentType == 0 || (ChatRightsEditActivity.this.currentType == 2 && ChatRightsEditActivity.this.asAdmin)) && itemViewType == 4 && viewHolder.getAdapterPosition() == ChatRightsEditActivity.this.anonymousRow)) {
                return true;
            }
            if (!ChatRightsEditActivity.this.canEdit) {
                return false;
            }
            if ((ChatRightsEditActivity.this.currentType == 0 || ChatRightsEditActivity.this.currentType == 2) && itemViewType == 4) {
                int adapterPosition = viewHolder.getAdapterPosition();
                if (adapterPosition == ChatRightsEditActivity.this.manageRow) {
                    if (ChatRightsEditActivity.this.myAdminRights.add_admins) {
                        return true;
                    }
                    if (ChatRightsEditActivity.this.currentChat == null || !ChatRightsEditActivity.this.currentChat.creator) {
                        return false;
                    }
                    return true;
                } else if (ChatRightsEditActivity.this.currentType == 2 && !ChatRightsEditActivity.this.asAdmin) {
                    return false;
                } else {
                    if (adapterPosition == ChatRightsEditActivity.this.changeInfoRow) {
                        if (!ChatRightsEditActivity.this.myAdminRights.change_info || (ChatRightsEditActivity.this.defaultBannedRights != null && !ChatRightsEditActivity.this.defaultBannedRights.change_info)) {
                            return false;
                        }
                        return true;
                    } else if (adapterPosition == ChatRightsEditActivity.this.postMessagesRow) {
                        return ChatRightsEditActivity.this.myAdminRights.post_messages;
                    } else {
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
                            if (!ChatRightsEditActivity.this.myAdminRights.pin_messages || (ChatRightsEditActivity.this.defaultBannedRights != null && !ChatRightsEditActivity.this.defaultBannedRights.pin_messages)) {
                                return false;
                            }
                            return true;
                        }
                    }
                }
            }
            if (itemViewType == 3 || itemViewType == 1 || itemViewType == 5 || itemViewType == 8) {
                return false;
            }
            return true;
        }

        public int getItemCount() {
            return ChatRightsEditActivity.this.rowCount;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v3, resolved type: org.telegram.ui.Cells.UserCell2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v8, resolved type: org.telegram.ui.Cells.TextCheckCell2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v10, resolved type: org.telegram.ui.Cells.TextDetailCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v12, resolved type: org.telegram.ui.Cells.PollEditTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v22, resolved type: org.telegram.ui.Cells.TextSettingsCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v23, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v24, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v25, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v26, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v27, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r15v2 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r14, int r15) {
            /*
                r13 = this;
                java.lang.String r14 = "windowBackgroundWhite"
                r0 = 0
                switch(r15) {
                    case 0: goto L_0x01d0;
                    case 1: goto L_0x01ba;
                    case 2: goto L_0x0006;
                    case 3: goto L_0x01a3;
                    case 4: goto L_0x0194;
                    case 5: goto L_0x018c;
                    case 6: goto L_0x017d;
                    case 7: goto L_0x015f;
                    case 8: goto L_0x0016;
                    default: goto L_0x0006;
                }
            L_0x0006:
                org.telegram.ui.Cells.TextSettingsCell r15 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r0 = r13.mContext
                r15.<init>(r0)
                int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)
                r15.setBackgroundColor(r14)
                goto L_0x01df
            L_0x0016:
                org.telegram.ui.ChatRightsEditActivity r14 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r15 = new android.widget.FrameLayout
                android.content.Context r1 = r13.mContext
                r15.<init>(r1)
                android.widget.FrameLayout unused = r14.addBotButtonContainer = r15
                org.telegram.ui.ChatRightsEditActivity r14 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r14 = r14.addBotButtonContainer
                java.lang.String r15 = "windowBackgroundGray"
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                r14.setBackgroundColor(r1)
                org.telegram.ui.ChatRightsEditActivity r14 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r1 = new android.widget.FrameLayout
                android.content.Context r2 = r13.mContext
                r1.<init>(r2)
                android.widget.FrameLayout unused = r14.addBotButton = r1
                org.telegram.ui.ChatRightsEditActivity r14 = org.telegram.ui.ChatRightsEditActivity.this
                org.telegram.ui.Components.AnimatedTextView r1 = new org.telegram.ui.Components.AnimatedTextView
                android.content.Context r2 = r13.mContext
                r3 = 1
                r1.<init>(r2, r3, r0, r0)
                org.telegram.ui.Components.AnimatedTextView unused = r14.addBotButtonText = r1
                org.telegram.ui.ChatRightsEditActivity r14 = org.telegram.ui.ChatRightsEditActivity.this
                org.telegram.ui.Components.AnimatedTextView r14 = r14.addBotButtonText
                java.lang.String r1 = "fonts/rmedium.ttf"
                android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r1)
                r14.setTypeface(r1)
                org.telegram.ui.ChatRightsEditActivity r14 = org.telegram.ui.ChatRightsEditActivity.this
                org.telegram.ui.Components.AnimatedTextView r14 = r14.addBotButtonText
                r1 = -1
                r14.setTextColor(r1)
                org.telegram.ui.ChatRightsEditActivity r14 = org.telegram.ui.ChatRightsEditActivity.this
                org.telegram.ui.Components.AnimatedTextView r14 = r14.addBotButtonText
                r2 = 1096810496(0x41600000, float:14.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                r14.setTextSize(r2)
                org.telegram.ui.ChatRightsEditActivity r14 = org.telegram.ui.ChatRightsEditActivity.this
                org.telegram.ui.Components.AnimatedTextView r14 = r14.addBotButtonText
                r2 = 17
                r14.setGravity(r2)
                org.telegram.ui.ChatRightsEditActivity r14 = org.telegram.ui.ChatRightsEditActivity.this
                org.telegram.ui.Components.AnimatedTextView r14 = r14.addBotButtonText
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r5 = 2131624258(0x7f0e0142, float:1.887569E38)
                java.lang.String r6 = "AddBotButton"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r4.append(r5)
                java.lang.String r5 = " "
                r4.append(r5)
                org.telegram.ui.ChatRightsEditActivity r5 = org.telegram.ui.ChatRightsEditActivity.this
                boolean r5 = r5.asAdmin
                if (r5 == 0) goto L_0x00a8
                r5 = 2131624259(0x7f0e0143, float:1.8875693E38)
                java.lang.String r6 = "AddBotButtonAsAdmin"
                goto L_0x00ad
            L_0x00a8:
                r5 = 2131624260(0x7f0e0144, float:1.8875695E38)
                java.lang.String r6 = "AddBotButtonAsMember"
            L_0x00ad:
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r4.append(r5)
                java.lang.String r4 = r4.toString()
                r14.setText(r4)
                org.telegram.ui.ChatRightsEditActivity r14 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r14 = r14.addBotButton
                org.telegram.ui.ChatRightsEditActivity r4 = org.telegram.ui.ChatRightsEditActivity.this
                org.telegram.ui.Components.AnimatedTextView r4 = r4.addBotButtonText
                r5 = -2
                android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r5, (int) r2)
                r14.addView(r4, r2)
                org.telegram.ui.ChatRightsEditActivity r14 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r14 = r14.addBotButton
                float[] r2 = new float[r3]
                r3 = 1082130432(0x40800000, float:4.0)
                r2[r0] = r3
                java.lang.String r3 = "featuredStickers_addButton"
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.AdaptiveRipple.filledRect((java.lang.String) r3, (float[]) r2)
                r14.setBackground(r2)
                org.telegram.ui.ChatRightsEditActivity r14 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r14 = r14.addBotButton
                org.telegram.ui.ChatRightsEditActivity$ListAdapter$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.ChatRightsEditActivity$ListAdapter$$ExternalSyntheticLambda0
                r2.<init>(r13)
                r14.setOnClickListener(r2)
                org.telegram.ui.ChatRightsEditActivity r14 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r14 = r14.addBotButtonContainer
                org.telegram.ui.ChatRightsEditActivity r2 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r2 = r2.addBotButton
                r6 = -1
                r7 = 1111490560(0x42400000, float:48.0)
                r8 = 119(0x77, float:1.67E-43)
                r9 = 1096810496(0x41600000, float:14.0)
                r10 = 1105199104(0x41e00000, float:28.0)
                r11 = 1096810496(0x41600000, float:14.0)
                r12 = 1096810496(0x41600000, float:14.0)
                android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
                r14.addView(r2, r3)
                org.telegram.ui.ChatRightsEditActivity r14 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r14 = r14.addBotButtonContainer
                androidx.recyclerview.widget.RecyclerView$LayoutParams r2 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r2.<init>((int) r1, (int) r5)
                r14.setLayoutParams(r2)
                android.view.View r14 = new android.view.View
                android.content.Context r1 = r13.mContext
                r14.<init>(r1)
                int r15 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                r14.setBackgroundColor(r15)
                org.telegram.ui.ChatRightsEditActivity r15 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r15 = r15.addBotButtonContainer
                r15.setClipChildren(r0)
                org.telegram.ui.ChatRightsEditActivity r15 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r15 = r15.addBotButtonContainer
                r15.setClipToPadding(r0)
                org.telegram.ui.ChatRightsEditActivity r15 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r15 = r15.addBotButtonContainer
                r0 = -1
                r1 = 1145569280(0x44480000, float:800.0)
                r2 = 87
                r3 = 0
                r4 = 0
                r5 = 0
                r6 = -1001914368(0xffffffffCLASSNAME, float:-800.0)
                android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r0, r1, r2, r3, r4, r5, r6)
                r15.addView(r14, r0)
                org.telegram.ui.ChatRightsEditActivity r14 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r14 = r14.addBotButtonContainer
                goto L_0x01e0
            L_0x015f:
                org.telegram.ui.ChatRightsEditActivity r15 = org.telegram.ui.ChatRightsEditActivity.this
                org.telegram.ui.Cells.PollEditTextCell r0 = new org.telegram.ui.Cells.PollEditTextCell
                android.content.Context r1 = r13.mContext
                r2 = 0
                r0.<init>(r1, r2)
                org.telegram.ui.Cells.PollEditTextCell r15 = r15.rankEditTextCell = r0
                int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)
                r15.setBackgroundColor(r14)
                org.telegram.ui.ChatRightsEditActivity$ListAdapter$1 r14 = new org.telegram.ui.ChatRightsEditActivity$ListAdapter$1
                r14.<init>()
                r15.addTextWatcher(r14)
                goto L_0x01df
            L_0x017d:
                org.telegram.ui.Cells.TextDetailCell r15 = new org.telegram.ui.Cells.TextDetailCell
                android.content.Context r0 = r13.mContext
                r15.<init>(r0)
                int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)
                r15.setBackgroundColor(r14)
                goto L_0x01df
            L_0x018c:
                org.telegram.ui.Cells.ShadowSectionCell r14 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r15 = r13.mContext
                r14.<init>(r15)
                goto L_0x01e0
            L_0x0194:
                org.telegram.ui.Cells.TextCheckCell2 r15 = new org.telegram.ui.Cells.TextCheckCell2
                android.content.Context r0 = r13.mContext
                r15.<init>(r0)
                int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)
                r15.setBackgroundColor(r14)
                goto L_0x01df
            L_0x01a3:
                org.telegram.ui.Cells.HeaderCell r15 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r1 = r13.mContext
                r3 = 21
                r4 = 15
                r5 = 1
                java.lang.String r2 = "windowBackgroundWhiteBlueHeader"
                r0 = r15
                r0.<init>(r1, r2, r3, r4, r5)
                int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)
                r15.setBackgroundColor(r14)
                goto L_0x01df
            L_0x01ba:
                org.telegram.ui.Cells.TextInfoPrivacyCell r14 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r15 = r13.mContext
                r14.<init>(r15)
                android.content.Context r15 = r13.mContext
                r0 = 2131165436(0x7var_fc, float:1.794509E38)
                java.lang.String r1 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r15, (int) r0, (java.lang.String) r1)
                r14.setBackgroundDrawable(r15)
                goto L_0x01e0
            L_0x01d0:
                org.telegram.ui.Cells.UserCell2 r15 = new org.telegram.ui.Cells.UserCell2
                android.content.Context r1 = r13.mContext
                r2 = 4
                r15.<init>(r1, r2, r0)
                int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)
                r15.setBackgroundColor(r14)
            L_0x01df:
                r14 = r15
            L_0x01e0:
                org.telegram.ui.Components.RecyclerListView$Holder r15 = new org.telegram.ui.Components.RecyclerListView$Holder
                r15.<init>(r14)
                return r15
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatRightsEditActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$0(View view) {
            ChatRightsEditActivity.this.onDonePressed();
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            String str2;
            String str3;
            boolean z = true;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    ((UserCell2) viewHolder.itemView).setData(ChatRightsEditActivity.this.currentUser, (CharSequence) null, ChatRightsEditActivity.this.currentType == 2 ? LocaleController.getString("Bot", NUM) : null, 0);
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
                        if (ChatRightsEditActivity.this.currentType == 2 || (ChatRightsEditActivity.this.currentUser != null && ChatRightsEditActivity.this.currentUser.bot)) {
                            headerCell.setText(LocaleController.getString("BotRestrictionsCanDo", NUM));
                            return;
                        } else if (ChatRightsEditActivity.this.currentType == 0) {
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
                    boolean z2 = ChatRightsEditActivity.this.currentType != 2 || ChatRightsEditActivity.this.asAdmin;
                    boolean z3 = ChatRightsEditActivity.this.currentChat != null && ChatRightsEditActivity.this.currentChat.creator;
                    int i2 = NUM;
                    if (i == ChatRightsEditActivity.this.manageRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("ManageGroup", NUM), ChatRightsEditActivity.this.asAdmin, true);
                        if (ChatRightsEditActivity.this.myAdminRights.add_admins || z3) {
                            i2 = 0;
                        }
                        textCheckCell2.setIcon(i2);
                    } else if (i == ChatRightsEditActivity.this.changeInfoRow) {
                        if (ChatRightsEditActivity.this.currentType == 0 || ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.isChannel) {
                                textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminChangeChannelInfo", NUM), (z2 && ChatRightsEditActivity.this.adminRights.change_info) || !ChatRightsEditActivity.this.defaultBannedRights.change_info, true);
                            } else {
                                textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminChangeGroupInfo", NUM), (z2 && ChatRightsEditActivity.this.adminRights.change_info) || !ChatRightsEditActivity.this.defaultBannedRights.change_info, true);
                            }
                            if (ChatRightsEditActivity.this.currentType == 2) {
                                if (ChatRightsEditActivity.this.myAdminRights.change_info || z3) {
                                    i2 = 0;
                                }
                                textCheckCell2.setIcon(i2);
                            }
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsChangeInfo", NUM), !ChatRightsEditActivity.this.bannedRights.change_info && !ChatRightsEditActivity.this.defaultBannedRights.change_info, false);
                            if (!ChatRightsEditActivity.this.defaultBannedRights.change_info) {
                                i2 = 0;
                            }
                            textCheckCell2.setIcon(i2);
                        }
                    } else if (i == ChatRightsEditActivity.this.postMessagesRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminPostMessages", NUM), z2 && ChatRightsEditActivity.this.adminRights.post_messages, true);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.post_messages || z3) {
                                i2 = 0;
                            }
                            textCheckCell2.setIcon(i2);
                        }
                    } else if (i == ChatRightsEditActivity.this.editMesagesRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminEditMessages", NUM), z2 && ChatRightsEditActivity.this.adminRights.edit_messages, true);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.edit_messages || z3) {
                                i2 = 0;
                            }
                            textCheckCell2.setIcon(i2);
                        }
                    } else if (i == ChatRightsEditActivity.this.deleteMessagesRow) {
                        if (ChatRightsEditActivity.this.isChannel) {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminDeleteMessages", NUM), z2 && ChatRightsEditActivity.this.adminRights.delete_messages, true);
                        } else {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminGroupDeleteMessages", NUM), z2 && ChatRightsEditActivity.this.adminRights.delete_messages, true);
                        }
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.delete_messages || z3) {
                                i2 = 0;
                            }
                            textCheckCell2.setIcon(i2);
                        }
                    } else if (i == ChatRightsEditActivity.this.addAdminsRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminAddAdmins", NUM), z2 && ChatRightsEditActivity.this.adminRights.add_admins, ChatRightsEditActivity.this.anonymousRow != -1);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.add_admins || z3) {
                                i2 = 0;
                            }
                            textCheckCell2.setIcon(i2);
                        }
                    } else if (i == ChatRightsEditActivity.this.anonymousRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminSendAnonymously", NUM), z2 && ChatRightsEditActivity.this.adminRights.anonymous, false);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.anonymous || z3) {
                                i2 = 0;
                            }
                            textCheckCell2.setIcon(i2);
                        }
                    } else if (i == ChatRightsEditActivity.this.banUsersRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminBanUsers", NUM), z2 && ChatRightsEditActivity.this.adminRights.ban_users, true);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.ban_users || z3) {
                                i2 = 0;
                            }
                            textCheckCell2.setIcon(i2);
                        }
                    } else if (i == ChatRightsEditActivity.this.startVoiceChatRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("StartVoipChatPermission", NUM), z2 && ChatRightsEditActivity.this.adminRights.manage_call, true);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.manage_call || z3) {
                                i2 = 0;
                            }
                            textCheckCell2.setIcon(i2);
                        }
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
                        } else if (ChatRightsEditActivity.this.currentType == 2) {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminAddUsersViaLink", NUM), z2 && ChatRightsEditActivity.this.adminRights.invite_users, true);
                            if (ChatRightsEditActivity.this.myAdminRights.invite_users || z3) {
                                i2 = 0;
                            }
                            textCheckCell2.setIcon(i2);
                        }
                    } else if (i == ChatRightsEditActivity.this.pinMessagesRow) {
                        if (ChatRightsEditActivity.this.currentType == 0 || ChatRightsEditActivity.this.currentType == 2) {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminPinMessages", NUM), (z2 && ChatRightsEditActivity.this.adminRights.pin_messages) || !ChatRightsEditActivity.this.defaultBannedRights.pin_messages, true);
                            if (ChatRightsEditActivity.this.currentType == 2) {
                                if (ChatRightsEditActivity.this.myAdminRights.pin_messages || z3) {
                                    i2 = 0;
                                }
                                textCheckCell2.setIcon(i2);
                            }
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
                    if (ChatRightsEditActivity.this.currentType != 2) {
                        if (i == ChatRightsEditActivity.this.sendMediaRow || i == ChatRightsEditActivity.this.sendStickersRow || i == ChatRightsEditActivity.this.embedLinksRow || i == ChatRightsEditActivity.this.sendPollsRow) {
                            if (ChatRightsEditActivity.this.bannedRights.send_messages || ChatRightsEditActivity.this.bannedRights.view_messages || ChatRightsEditActivity.this.defaultBannedRights.send_messages || ChatRightsEditActivity.this.defaultBannedRights.view_messages) {
                                z = false;
                            }
                            textCheckCell2.setEnabled(z);
                            return;
                        } else if (i == ChatRightsEditActivity.this.sendMessagesRow) {
                            if (ChatRightsEditActivity.this.bannedRights.view_messages || ChatRightsEditActivity.this.defaultBannedRights.view_messages) {
                                z = false;
                            }
                            textCheckCell2.setEnabled(z);
                            return;
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                case 5:
                    ShadowSectionCell shadowSectionCell = (ShadowSectionCell) viewHolder.itemView;
                    if (ChatRightsEditActivity.this.currentType == 2 && (i == ChatRightsEditActivity.this.rightsShadowRow || i == ChatRightsEditActivity.this.rankInfoRow)) {
                        shadowSectionCell.setAlpha(ChatRightsEditActivity.this.asAdminT);
                    } else {
                        shadowSectionCell.setAlpha(1.0f);
                    }
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
            if (i == ChatRightsEditActivity.this.changeInfoRow || i == ChatRightsEditActivity.this.postMessagesRow || i == ChatRightsEditActivity.this.editMesagesRow || i == ChatRightsEditActivity.this.deleteMessagesRow || i == ChatRightsEditActivity.this.addAdminsRow || i == ChatRightsEditActivity.this.banUsersRow || i == ChatRightsEditActivity.this.addUsersRow || i == ChatRightsEditActivity.this.pinMessagesRow || i == ChatRightsEditActivity.this.sendMessagesRow || i == ChatRightsEditActivity.this.sendMediaRow || i == ChatRightsEditActivity.this.sendStickersRow || i == ChatRightsEditActivity.this.embedLinksRow || i == ChatRightsEditActivity.this.sendPollsRow || i == ChatRightsEditActivity.this.anonymousRow || i == ChatRightsEditActivity.this.startVoiceChatRow || i == ChatRightsEditActivity.this.manageRow) {
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
            if (i == ChatRightsEditActivity.this.addBotButtonRow) {
                return 8;
            }
            return 2;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0064, code lost:
        if (r5.creator == false) goto L_0x0068;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x007d, code lost:
        if (r8.defaultBannedRights.change_info != false) goto L_0x010c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00d6, code lost:
        if (r8.defaultBannedRights.pin_messages != false) goto L_0x010c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0107, code lost:
        if (r5.creator == false) goto L_0x0068;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateAsAdmin(boolean r9) {
        /*
            r8 = this;
            android.widget.FrameLayout r0 = r8.addBotButton
            if (r0 == 0) goto L_0x0007
            r0.invalidate()
        L_0x0007:
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            int r0 = r0.getChildCount()
            r1 = 0
            r2 = 0
        L_0x000f:
            r3 = 1
            if (r2 >= r0) goto L_0x0118
            org.telegram.ui.Components.RecyclerListView r4 = r8.listView
            android.view.View r4 = r4.getChildAt(r2)
            org.telegram.ui.Components.RecyclerListView r5 = r8.listView
            int r5 = r5.getChildAdapterPosition(r4)
            boolean r6 = r4 instanceof org.telegram.ui.Cells.TextCheckCell2
            if (r6 == 0) goto L_0x0114
            boolean r6 = r8.asAdmin
            if (r6 != 0) goto L_0x0054
            int r6 = r8.changeInfoRow
            if (r5 != r6) goto L_0x0030
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r6 = r8.defaultBannedRights
            boolean r6 = r6.change_info
            if (r6 == 0) goto L_0x003a
        L_0x0030:
            int r6 = r8.pinMessagesRow
            if (r5 != r6) goto L_0x0044
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r6 = r8.defaultBannedRights
            boolean r6 = r6.pin_messages
            if (r6 != 0) goto L_0x0044
        L_0x003a:
            org.telegram.ui.Cells.TextCheckCell2 r4 = (org.telegram.ui.Cells.TextCheckCell2) r4
            r4.setChecked(r3)
            r4.setEnabled(r1, r1)
            goto L_0x0114
        L_0x0044:
            org.telegram.ui.Cells.TextCheckCell2 r4 = (org.telegram.ui.Cells.TextCheckCell2) r4
            r4.setChecked(r1)
            int r6 = r8.manageRow
            if (r5 != r6) goto L_0x004e
            goto L_0x004f
        L_0x004e:
            r3 = 0
        L_0x004f:
            r4.setEnabled(r3, r9)
            goto L_0x0114
        L_0x0054:
            int r7 = r8.manageRow
            if (r5 != r7) goto L_0x006b
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r5 = r8.myAdminRights
            boolean r5 = r5.add_admins
            if (r5 != 0) goto L_0x010c
            org.telegram.tgnet.TLRPC$Chat r5 = r8.currentChat
            if (r5 == 0) goto L_0x0068
            boolean r5 = r5.creator
            if (r5 == 0) goto L_0x0068
            goto L_0x010c
        L_0x0068:
            r3 = 0
            goto L_0x010c
        L_0x006b:
            int r6 = r8.changeInfoRow
            if (r5 != r6) goto L_0x0081
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r5 = r8.adminRights
            boolean r6 = r5.change_info
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r5 = r8.myAdminRights
            boolean r5 = r5.change_info
            if (r5 == 0) goto L_0x0068
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r8.defaultBannedRights
            boolean r5 = r5.change_info
            if (r5 == 0) goto L_0x0068
            goto L_0x010c
        L_0x0081:
            int r6 = r8.postMessagesRow
            if (r5 != r6) goto L_0x008f
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r8.adminRights
            boolean r6 = r3.post_messages
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r8.myAdminRights
            boolean r3 = r3.post_messages
            goto L_0x010c
        L_0x008f:
            int r6 = r8.editMesagesRow
            if (r5 != r6) goto L_0x009d
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r8.adminRights
            boolean r6 = r3.edit_messages
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r8.myAdminRights
            boolean r3 = r3.edit_messages
            goto L_0x010c
        L_0x009d:
            int r6 = r8.deleteMessagesRow
            if (r5 != r6) goto L_0x00aa
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r8.adminRights
            boolean r6 = r3.delete_messages
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r8.myAdminRights
            boolean r3 = r3.delete_messages
            goto L_0x010c
        L_0x00aa:
            int r6 = r8.banUsersRow
            if (r5 != r6) goto L_0x00b7
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r8.adminRights
            boolean r6 = r3.ban_users
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r8.myAdminRights
            boolean r3 = r3.ban_users
            goto L_0x010c
        L_0x00b7:
            int r6 = r8.addUsersRow
            if (r5 != r6) goto L_0x00c4
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r8.adminRights
            boolean r6 = r3.invite_users
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r8.myAdminRights
            boolean r3 = r3.invite_users
            goto L_0x010c
        L_0x00c4:
            int r6 = r8.pinMessagesRow
            if (r5 != r6) goto L_0x00d9
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r5 = r8.adminRights
            boolean r6 = r5.pin_messages
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r5 = r8.myAdminRights
            boolean r5 = r5.pin_messages
            if (r5 == 0) goto L_0x0068
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r5 = r8.defaultBannedRights
            boolean r5 = r5.pin_messages
            if (r5 == 0) goto L_0x0068
            goto L_0x010c
        L_0x00d9:
            int r6 = r8.startVoiceChatRow
            if (r5 != r6) goto L_0x00e6
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r8.adminRights
            boolean r6 = r3.manage_call
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r8.myAdminRights
            boolean r3 = r3.manage_call
            goto L_0x010c
        L_0x00e6:
            int r6 = r8.addAdminsRow
            if (r5 != r6) goto L_0x00f3
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r8.adminRights
            boolean r6 = r3.add_admins
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r8.myAdminRights
            boolean r3 = r3.add_admins
            goto L_0x010c
        L_0x00f3:
            int r6 = r8.anonymousRow
            if (r5 != r6) goto L_0x010a
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r5 = r8.adminRights
            boolean r6 = r5.anonymous
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r5 = r8.myAdminRights
            boolean r5 = r5.anonymous
            if (r5 != 0) goto L_0x010c
            org.telegram.tgnet.TLRPC$Chat r5 = r8.currentChat
            if (r5 == 0) goto L_0x0068
            boolean r5 = r5.creator
            if (r5 == 0) goto L_0x0068
            goto L_0x010c
        L_0x010a:
            r3 = 0
            r6 = 0
        L_0x010c:
            org.telegram.ui.Cells.TextCheckCell2 r4 = (org.telegram.ui.Cells.TextCheckCell2) r4
            r4.setChecked(r6)
            r4.setEnabled(r3, r9)
        L_0x0114:
            int r2 = r2 + 1
            goto L_0x000f
        L_0x0118:
            org.telegram.ui.ChatRightsEditActivity$ListAdapter r0 = r8.listViewAdapter
            r0.notifyDataSetChanged()
            org.telegram.ui.Components.AnimatedTextView r0 = r8.addBotButtonText
            if (r0 == 0) goto L_0x0156
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r4 = 2131624258(0x7f0e0142, float:1.887569E38)
            java.lang.String r5 = "AddBotButton"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.append(r4)
            java.lang.String r4 = " "
            r2.append(r4)
            boolean r4 = r8.asAdmin
            if (r4 == 0) goto L_0x0141
            r4 = 2131624259(0x7f0e0143, float:1.8875693E38)
            java.lang.String r5 = "AddBotButtonAsAdmin"
            goto L_0x0146
        L_0x0141:
            r4 = 2131624260(0x7f0e0144, float:1.8875695E38)
            java.lang.String r5 = "AddBotButtonAsMember"
        L_0x0146:
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            boolean r4 = r8.asAdmin
            r0.setText(r2, r9, r4)
        L_0x0156:
            android.animation.ValueAnimator r0 = r8.asAdminAnimator
            if (r0 == 0) goto L_0x0160
            r0.cancel()
            r0 = 0
            r8.asAdminAnimator = r0
        L_0x0160:
            r0 = 1065353216(0x3var_, float:1.0)
            r2 = 0
            if (r9 == 0) goto L_0x01a1
            r9 = 2
            float[] r9 = new float[r9]
            float r4 = r8.asAdminT
            r9[r1] = r4
            boolean r1 = r8.asAdmin
            if (r1 == 0) goto L_0x0173
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x0174
        L_0x0173:
            r1 = 0
        L_0x0174:
            r9[r3] = r1
            android.animation.ValueAnimator r9 = android.animation.ValueAnimator.ofFloat(r9)
            r8.asAdminAnimator = r9
            org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda1 r1 = new org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda1
            r1.<init>(r8)
            r9.addUpdateListener(r1)
            android.animation.ValueAnimator r9 = r8.asAdminAnimator
            float r1 = r8.asAdminT
            boolean r3 = r8.asAdmin
            if (r3 == 0) goto L_0x018d
            goto L_0x018e
        L_0x018d:
            r0 = 0
        L_0x018e:
            float r1 = r1 - r0
            float r0 = java.lang.Math.abs(r1)
            r1 = 1128792064(0x43480000, float:200.0)
            float r0 = r0 * r1
            long r0 = (long) r0
            r9.setDuration(r0)
            android.animation.ValueAnimator r9 = r8.asAdminAnimator
            r9.start()
            goto L_0x01b0
        L_0x01a1:
            boolean r9 = r8.asAdmin
            if (r9 == 0) goto L_0x01a6
            goto L_0x01a7
        L_0x01a6:
            r0 = 0
        L_0x01a7:
            r8.asAdminT = r0
            android.widget.FrameLayout r9 = r8.addBotButton
            if (r9 == 0) goto L_0x01b0
            r9.invalidate()
        L_0x01b0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatRightsEditActivity.updateAsAdmin(boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateAsAdmin$25(ValueAnimator valueAnimator) {
        this.asAdminT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        FrameLayout frameLayout = this.addBotButton;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ChatRightsEditActivity$$ExternalSyntheticLambda24 chatRightsEditActivity$$ExternalSyntheticLambda24 = new ChatRightsEditActivity$$ExternalSyntheticLambda24(this);
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
        ChatRightsEditActivity$$ExternalSyntheticLambda24 chatRightsEditActivity$$ExternalSyntheticLambda242 = chatRightsEditActivity$$ExternalSyntheticLambda24;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) chatRightsEditActivity$$ExternalSyntheticLambda242, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) chatRightsEditActivity$$ExternalSyntheticLambda242, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ChatRightsEditActivity$$ExternalSyntheticLambda24 chatRightsEditActivity$$ExternalSyntheticLambda243 = chatRightsEditActivity$$ExternalSyntheticLambda24;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatRightsEditActivity$$ExternalSyntheticLambda243, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatRightsEditActivity$$ExternalSyntheticLambda243, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatRightsEditActivity$$ExternalSyntheticLambda243, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatRightsEditActivity$$ExternalSyntheticLambda243, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatRightsEditActivity$$ExternalSyntheticLambda243, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatRightsEditActivity$$ExternalSyntheticLambda243, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatRightsEditActivity$$ExternalSyntheticLambda243, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription((View) null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription((View) null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextGray2"));
        arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOX, new Class[]{DialogRadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogRadioBackground"));
        arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{DialogRadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogRadioBackgroundChecked"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$26() {
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
