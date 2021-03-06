package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$ExportedChatInvite;
import org.telegram.tgnet.TLRPC$TL_chatAdminWithInvites;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_chatAdminsWithInvites;
import org.telegram.tgnet.TLRPC$TL_messages_deleteExportedChatInvite;
import org.telegram.tgnet.TLRPC$TL_messages_deleteRevokedExportedChatInvites;
import org.telegram.tgnet.TLRPC$TL_messages_editExportedChatInvite;
import org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite;
import org.telegram.tgnet.TLRPC$TL_messages_exportedChatInvite;
import org.telegram.tgnet.TLRPC$TL_messages_exportedChatInviteReplaced;
import org.telegram.tgnet.TLRPC$TL_messages_exportedChatInvites;
import org.telegram.tgnet.TLRPC$TL_messages_getAdminsWithInvites;
import org.telegram.tgnet.TLRPC$TL_messages_getExportedChatInvites;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.DotDividerSpan;
import org.telegram.ui.Components.InviteLinkBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkActionView;
import org.telegram.ui.Components.RecyclerItemsEnterAnimator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TimerParticles;
import org.telegram.ui.LinkEditActivity;
import org.telegram.ui.ManageLinksActivity;

public class ManageLinksActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public int adminId;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$TL_chatAdminWithInvites> admins = new ArrayList<>();
    /* access modifiers changed from: private */
    public int adminsDividerRow;
    /* access modifiers changed from: private */
    public int adminsEndRow;
    /* access modifiers changed from: private */
    public int adminsHeaderRow;
    boolean adminsLoaded;
    /* access modifiers changed from: private */
    public int adminsStartRow;
    int animationIndex;
    /* access modifiers changed from: private */
    public boolean canEdit;
    /* access modifiers changed from: private */
    public int createLinkHelpRow;
    /* access modifiers changed from: private */
    public int createNewLinkRow;
    /* access modifiers changed from: private */
    public int creatorDividerRow;
    /* access modifiers changed from: private */
    public int creatorRow;
    /* access modifiers changed from: private */
    public TLRPC$Chat currentChat;
    /* access modifiers changed from: private */
    public int currentChatId;
    boolean deletingRevokedLinks;
    /* access modifiers changed from: private */
    public int dividerRow;
    boolean hasMore;
    /* access modifiers changed from: private */
    public int helpRow;
    /* access modifiers changed from: private */
    public TLRPC$ChatFull info;
    /* access modifiers changed from: private */
    public TLRPC$TL_chatInviteExported invite;
    /* access modifiers changed from: private */
    public InviteLinkBottomSheet inviteLinkBottomSheet;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$TL_chatInviteExported> invites = new ArrayList<>();
    /* access modifiers changed from: private */
    public int invitesCount;
    /* access modifiers changed from: private */
    public boolean isChannel;
    private boolean isOpened;
    /* access modifiers changed from: private */
    public boolean isPublic;
    /* access modifiers changed from: private */
    public int lastDivider;
    private final LinkEditActivity.Callback linkEditActivityCallback;
    Drawable linkIcon;
    Drawable linkIconRevoked;
    /* access modifiers changed from: private */
    public int linksEndRow;
    /* access modifiers changed from: private */
    public int linksHeaderRow;
    boolean linksLoading;
    /* access modifiers changed from: private */
    public int linksLoadingRow;
    /* access modifiers changed from: private */
    public int linksStartRow;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private ListAdapter listViewAdapter;
    boolean loadAdmins;
    boolean loadRevoked;
    /* access modifiers changed from: private */
    public int permanentLinkHeaderRow;
    /* access modifiers changed from: private */
    public int permanentLinkRow;
    /* access modifiers changed from: private */
    public RecyclerItemsEnterAnimator recyclerItemsEnterAnimator;
    /* access modifiers changed from: private */
    public int revokeAllDivider;
    /* access modifiers changed from: private */
    public int revokeAllRow;
    /* access modifiers changed from: private */
    public int revokedDivider;
    /* access modifiers changed from: private */
    public int revokedHeader;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$TL_chatInviteExported> revokedInvites = new ArrayList<>();
    /* access modifiers changed from: private */
    public int revokedLinksEndRow;
    /* access modifiers changed from: private */
    public int revokedLinksStartRow;
    /* access modifiers changed from: private */
    public int rowCount;
    long timeDif;
    Runnable updateTimerRunnable = new Runnable() {
        public void run() {
            if (ManageLinksActivity.this.listView != null) {
                for (int i = 0; i < ManageLinksActivity.this.listView.getChildCount(); i++) {
                    View childAt = ManageLinksActivity.this.listView.getChildAt(i);
                    if (childAt instanceof LinkCell) {
                        LinkCell linkCell = (LinkCell) childAt;
                        if (linkCell.timerRunning) {
                            linkCell.setLink(linkCell.invite, linkCell.position);
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(this, 500);
            }
        }
    };
    /* access modifiers changed from: private */
    public HashMap<Integer, TLRPC$User> users = new HashMap<>();

    public boolean needDelayOpenAnimation() {
        return true;
    }

    private static class EmptyView extends LinearLayout implements NotificationCenter.NotificationCenterDelegate {
        private final int currentAccount = UserConfig.selectedAccount;
        private BackupImageView stickerView;

        public EmptyView(Context context) {
            super(context);
            setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
            setOrientation(1);
            BackupImageView backupImageView = new BackupImageView(context);
            this.stickerView = backupImageView;
            addView(backupImageView, LayoutHelper.createLinear(104, 104, 49, 0, 2, 0, 0));
        }

        private void setSticker() {
            TLRPC$TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByName("tg_placeholders");
            if (stickerSetByName == null) {
                stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName("tg_placeholders");
            }
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = stickerSetByName;
            if (tLRPC$TL_messages_stickerSet == null || tLRPC$TL_messages_stickerSet.documents.size() < 4) {
                MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("tg_placeholders", false, tLRPC$TL_messages_stickerSet == null);
                return;
            }
            TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(3);
            this.stickerView.setImage(ImageLocation.getForDocument(tLRPC$Document), "104_104", "tgs", (Drawable) DocumentObject.getSvgThumb(tLRPC$Document, "windowBackgroundGray", 1.0f), (Object) tLRPC$TL_messages_stickerSet);
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            setSticker();
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.diceStickersDidLoad && "tg_placeholders".equals(objArr[0])) {
                setSticker();
            }
        }
    }

    public ManageLinksActivity(int i, int i2, int i3) {
        boolean z = false;
        this.loadRevoked = false;
        this.linkEditActivityCallback = new LinkEditActivity.Callback() {
            public void onLinkCreated(TLObject tLObject) {
                if (tLObject instanceof TLRPC$TL_chatInviteExported) {
                    AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
                        public final /* synthetic */ TLObject f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            ManageLinksActivity.AnonymousClass6.this.lambda$onLinkCreated$0$ManageLinksActivity$6(this.f$1);
                        }
                    }, 200);
                }
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onLinkCreated$0 */
            public /* synthetic */ void lambda$onLinkCreated$0$ManageLinksActivity$6(TLObject tLObject) {
                DiffCallback access$4300 = ManageLinksActivity.this.saveListState();
                ManageLinksActivity.this.invites.add(0, (TLRPC$TL_chatInviteExported) tLObject);
                ManageLinksActivity.this.updateRecyclerViewAnimated(access$4300);
                if (ManageLinksActivity.this.info != null) {
                    ManageLinksActivity.this.info.invitesCount++;
                    ManageLinksActivity.this.getMessagesStorage().saveChatLinksCount(ManageLinksActivity.this.currentChatId, ManageLinksActivity.this.info.invitesCount);
                }
            }

            public void onLinkEdited(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, TLObject tLObject) {
                if (tLObject instanceof TLRPC$TL_messages_exportedChatInvite) {
                    TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported2 = (TLRPC$TL_chatInviteExported) ((TLRPC$TL_messages_exportedChatInvite) tLObject).invite;
                    ManageLinksActivity.this.fixDate(tLRPC$TL_chatInviteExported2);
                    int i = 0;
                    while (i < ManageLinksActivity.this.invites.size()) {
                        if (!((TLRPC$TL_chatInviteExported) ManageLinksActivity.this.invites.get(i)).link.equals(tLRPC$TL_chatInviteExported.link)) {
                            i++;
                        } else if (tLRPC$TL_chatInviteExported2.revoked) {
                            DiffCallback access$4300 = ManageLinksActivity.this.saveListState();
                            ManageLinksActivity.this.invites.remove(i);
                            ManageLinksActivity.this.revokedInvites.add(0, tLRPC$TL_chatInviteExported2);
                            ManageLinksActivity.this.updateRecyclerViewAnimated(access$4300);
                            return;
                        } else {
                            ManageLinksActivity.this.invites.set(i, tLRPC$TL_chatInviteExported2);
                            ManageLinksActivity.this.updateRows(true);
                            return;
                        }
                    }
                }
            }

            public void onLinkRemoved(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
                for (int i = 0; i < ManageLinksActivity.this.revokedInvites.size(); i++) {
                    if (((TLRPC$TL_chatInviteExported) ManageLinksActivity.this.revokedInvites.get(i)).link.equals(tLRPC$TL_chatInviteExported.link)) {
                        DiffCallback access$4300 = ManageLinksActivity.this.saveListState();
                        ManageLinksActivity.this.revokedInvites.remove(i);
                        ManageLinksActivity.this.updateRecyclerViewAnimated(access$4300);
                        return;
                    }
                }
            }

            public void revokeLink(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
                ManageLinksActivity.this.revokeLink(tLRPC$TL_chatInviteExported);
            }
        };
        this.animationIndex = -1;
        this.currentChatId = i;
        this.invitesCount = i3;
        TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(i));
        this.currentChat = chat;
        this.isChannel = ChatObject.isChannel(chat) && !this.currentChat.megagroup;
        if (i2 == 0) {
            this.adminId = getAccountInstance().getUserConfig().clientUserId;
        } else {
            this.adminId = i2;
        }
        TLRPC$User user = getMessagesController().getUser(Integer.valueOf(this.adminId));
        if (this.adminId == getAccountInstance().getUserConfig().clientUserId || (user != null && !user.bot)) {
            z = true;
        }
        this.canEdit = z;
    }

    /* access modifiers changed from: private */
    public void loadLinks(boolean z) {
        if (!this.loadAdmins || this.adminsLoaded) {
            TLRPC$TL_messages_getExportedChatInvites tLRPC$TL_messages_getExportedChatInvites = new TLRPC$TL_messages_getExportedChatInvites();
            tLRPC$TL_messages_getExportedChatInvites.peer = getMessagesController().getInputPeer(-this.currentChatId);
            if (this.adminId == getUserConfig().getClientUserId()) {
                tLRPC$TL_messages_getExportedChatInvites.admin_id = getMessagesController().getInputUser(getUserConfig().getCurrentUser());
            } else {
                tLRPC$TL_messages_getExportedChatInvites.admin_id = getMessagesController().getInputUser(this.adminId);
            }
            boolean z2 = this.loadRevoked;
            if (z2) {
                tLRPC$TL_messages_getExportedChatInvites.revoked = true;
                if (!this.revokedInvites.isEmpty()) {
                    tLRPC$TL_messages_getExportedChatInvites.flags |= 4;
                    ArrayList<TLRPC$TL_chatInviteExported> arrayList = this.revokedInvites;
                    tLRPC$TL_messages_getExportedChatInvites.offset_link = arrayList.get(arrayList.size() - 1).link;
                    ArrayList<TLRPC$TL_chatInviteExported> arrayList2 = this.revokedInvites;
                    tLRPC$TL_messages_getExportedChatInvites.offset_date = arrayList2.get(arrayList2.size() - 1).date;
                }
            } else if (!this.invites.isEmpty()) {
                tLRPC$TL_messages_getExportedChatInvites.flags |= 4;
                ArrayList<TLRPC$TL_chatInviteExported> arrayList3 = this.invites;
                tLRPC$TL_messages_getExportedChatInvites.offset_link = arrayList3.get(arrayList3.size() - 1).link;
                ArrayList<TLRPC$TL_chatInviteExported> arrayList4 = this.invites;
                tLRPC$TL_messages_getExportedChatInvites.offset_date = arrayList4.get(arrayList4.size() - 1).date;
            }
            this.linksLoading = true;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_getExportedChatInvites, new RequestDelegate(this.isPublic ? null : this.invite, z2) {
                public final /* synthetic */ TLRPC$TL_chatInviteExported f$1;
                public final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ManageLinksActivity.this.lambda$loadLinks$5$ManageLinksActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            }), getClassGuid());
        } else {
            this.linksLoading = true;
            TLRPC$TL_messages_getAdminsWithInvites tLRPC$TL_messages_getAdminsWithInvites = new TLRPC$TL_messages_getAdminsWithInvites();
            tLRPC$TL_messages_getAdminsWithInvites.peer = getMessagesController().getInputPeer(-this.currentChatId);
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_getAdminsWithInvites, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ManageLinksActivity.this.lambda$loadLinks$2$ManageLinksActivity(tLObject, tLRPC$TL_error);
                }
            }), getClassGuid());
        }
        if (z) {
            updateRows(true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadLinks$2 */
    public /* synthetic */ void lambda$loadLinks$2$ManageLinksActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ManageLinksActivity.this.lambda$loadLinks$1$ManageLinksActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadLinks$1 */
    public /* synthetic */ void lambda$loadLinks$1$ManageLinksActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        getNotificationCenter().doOnIdle(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ManageLinksActivity.this.lambda$loadLinks$0$ManageLinksActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadLinks$0 */
    public /* synthetic */ void lambda$loadLinks$0$ManageLinksActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        RecyclerItemsEnterAnimator recyclerItemsEnterAnimator2;
        this.linksLoading = false;
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_chatAdminsWithInvites tLRPC$TL_messages_chatAdminsWithInvites = (TLRPC$TL_messages_chatAdminsWithInvites) tLObject;
            for (int i = 0; i < tLRPC$TL_messages_chatAdminsWithInvites.admins.size(); i++) {
                TLRPC$TL_chatAdminWithInvites tLRPC$TL_chatAdminWithInvites = tLRPC$TL_messages_chatAdminsWithInvites.admins.get(i);
                if (tLRPC$TL_chatAdminWithInvites.admin_id != getAccountInstance().getUserConfig().clientUserId) {
                    this.admins.add(tLRPC$TL_chatAdminWithInvites);
                }
            }
            for (int i2 = 0; i2 < tLRPC$TL_messages_chatAdminsWithInvites.users.size(); i2++) {
                TLRPC$User tLRPC$User = tLRPC$TL_messages_chatAdminsWithInvites.users.get(i2);
                this.users.put(Integer.valueOf(tLRPC$User.id), tLRPC$User);
            }
        }
        int i3 = this.rowCount;
        this.adminsLoaded = true;
        this.hasMore = false;
        if (this.admins.size() > 0 && (recyclerItemsEnterAnimator2 = this.recyclerItemsEnterAnimator) != null && !this.isPaused && this.isOpened) {
            recyclerItemsEnterAnimator2.showItemsAnimated(i3 + 1);
        }
        if (!this.hasMore || this.invites.size() + this.revokedInvites.size() + this.admins.size() >= 5) {
            resumeDelayedFragmentAnimation();
        }
        if (!this.hasMore && !this.loadRevoked) {
            this.hasMore = true;
            this.loadRevoked = true;
            loadLinks(false);
        }
        updateRows(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadLinks$5 */
    public /* synthetic */ void lambda$loadLinks$5$ManageLinksActivity(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported2;
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_exportedChatInvites tLRPC$TL_messages_exportedChatInvites = (TLRPC$TL_messages_exportedChatInvites) tLObject;
            if (tLRPC$TL_messages_exportedChatInvites.invites.size() > 0 && tLRPC$TL_chatInviteExported != null) {
                int i = 0;
                while (true) {
                    if (i >= tLRPC$TL_messages_exportedChatInvites.invites.size()) {
                        break;
                    } else if (((TLRPC$TL_chatInviteExported) tLRPC$TL_messages_exportedChatInvites.invites.get(i)).link.equals(tLRPC$TL_chatInviteExported.link)) {
                        tLRPC$TL_chatInviteExported2 = (TLRPC$TL_chatInviteExported) tLRPC$TL_messages_exportedChatInvites.invites.remove(i);
                        break;
                    } else {
                        i++;
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_chatInviteExported2, tLRPC$TL_error, tLObject, z) {
                    public final /* synthetic */ TLRPC$TL_chatInviteExported f$1;
                    public final /* synthetic */ TLRPC$TL_error f$2;
                    public final /* synthetic */ TLObject f$3;
                    public final /* synthetic */ boolean f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run() {
                        ManageLinksActivity.this.lambda$loadLinks$4$ManageLinksActivity(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
            }
        }
        tLRPC$TL_chatInviteExported2 = null;
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_chatInviteExported2, tLRPC$TL_error, tLObject, z) {
            public final /* synthetic */ TLRPC$TL_chatInviteExported f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;
            public final /* synthetic */ TLObject f$3;
            public final /* synthetic */ boolean f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                ManageLinksActivity.this.lambda$loadLinks$4$ManageLinksActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadLinks$4 */
    public /* synthetic */ void lambda$loadLinks$4$ManageLinksActivity(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        getNotificationCenter().doOnIdle(new Runnable(tLRPC$TL_chatInviteExported, tLRPC$TL_error, tLObject, z) {
            public final /* synthetic */ TLRPC$TL_chatInviteExported f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;
            public final /* synthetic */ TLObject f$3;
            public final /* synthetic */ boolean f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                ManageLinksActivity.this.lambda$loadLinks$3$ManageLinksActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0156  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0167  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x016b  */
    /* renamed from: lambda$loadLinks$3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadLinks$3$ManageLinksActivity(org.telegram.tgnet.TLRPC$TL_chatInviteExported r6, org.telegram.tgnet.TLRPC$TL_error r7, org.telegram.tgnet.TLObject r8, boolean r9) {
        /*
            r5 = this;
            r0 = 0
            r5.linksLoading = r0
            r5.hasMore = r0
            if (r6 == 0) goto L_0x000f
            r5.invite = r6
            org.telegram.tgnet.TLRPC$ChatFull r1 = r5.info
            if (r1 == 0) goto L_0x000f
            r1.exported_invite = r6
        L_0x000f:
            org.telegram.ui.ManageLinksActivity$DiffCallback r6 = r5.saveListState()
            r1 = 1
            if (r7 != 0) goto L_0x0109
            org.telegram.tgnet.TLRPC$TL_messages_exportedChatInvites r8 = (org.telegram.tgnet.TLRPC$TL_messages_exportedChatInvites) r8
            if (r9 == 0) goto L_0x0036
            r7 = 0
        L_0x001b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ExportedChatInvite> r2 = r8.invites
            int r2 = r2.size()
            if (r7 >= r2) goto L_0x007f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ExportedChatInvite> r2 = r8.invites
            java.lang.Object r2 = r2.get(r7)
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r2 = (org.telegram.tgnet.TLRPC$TL_chatInviteExported) r2
            r5.fixDate(r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_chatInviteExported> r3 = r5.revokedInvites
            r3.add(r2)
            int r7 = r7 + 1
            goto L_0x001b
        L_0x0036:
            int r7 = r5.adminId
            org.telegram.messenger.AccountInstance r2 = r5.getAccountInstance()
            org.telegram.messenger.UserConfig r2 = r2.getUserConfig()
            int r2 = r2.clientUserId
            if (r7 == r2) goto L_0x0063
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_chatInviteExported> r7 = r5.invites
            int r7 = r7.size()
            if (r7 != 0) goto L_0x0063
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ExportedChatInvite> r7 = r8.invites
            int r7 = r7.size()
            if (r7 <= 0) goto L_0x0063
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ExportedChatInvite> r7 = r8.invites
            java.lang.Object r7 = r7.get(r0)
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r7 = (org.telegram.tgnet.TLRPC$TL_chatInviteExported) r7
            r5.invite = r7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ExportedChatInvite> r7 = r8.invites
            r7.remove(r0)
        L_0x0063:
            r7 = 0
        L_0x0064:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ExportedChatInvite> r2 = r8.invites
            int r2 = r2.size()
            if (r7 >= r2) goto L_0x007f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ExportedChatInvite> r2 = r8.invites
            java.lang.Object r2 = r2.get(r7)
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r2 = (org.telegram.tgnet.TLRPC$TL_chatInviteExported) r2
            r5.fixDate(r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_chatInviteExported> r3 = r5.invites
            r3.add(r2)
            int r7 = r7 + 1
            goto L_0x0064
        L_0x007f:
            r7 = 0
        L_0x0080:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r8.users
            int r2 = r2.size()
            if (r7 >= r2) goto L_0x00a6
            java.util.HashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$User> r2 = r5.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r8.users
            java.lang.Object r3 = r3.get(r7)
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
            int r3 = r3.id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r8.users
            java.lang.Object r4 = r4.get(r7)
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC$User) r4
            r2.put(r3, r4)
            int r7 = r7 + 1
            goto L_0x0080
        L_0x00a6:
            int r7 = r5.rowCount
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ExportedChatInvite> r2 = r8.invites
            int r2 = r2.size()
            if (r2 != 0) goto L_0x00b3
            r5.hasMore = r0
            goto L_0x00d6
        L_0x00b3:
            if (r9 == 0) goto L_0x00c6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_chatInviteExported> r2 = r5.revokedInvites
            int r2 = r2.size()
            int r2 = r2 + r1
            int r3 = r8.count
            if (r2 >= r3) goto L_0x00c2
            r2 = 1
            goto L_0x00c3
        L_0x00c2:
            r2 = 0
        L_0x00c3:
            r5.hasMore = r2
            goto L_0x00d6
        L_0x00c6:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_chatInviteExported> r2 = r5.invites
            int r2 = r2.size()
            int r2 = r2 + r1
            int r3 = r8.count
            if (r2 >= r3) goto L_0x00d3
            r2 = 1
            goto L_0x00d4
        L_0x00d3:
            r2 = 0
        L_0x00d4:
            r5.hasMore = r2
        L_0x00d6:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ExportedChatInvite> r2 = r8.invites
            int r2 = r2.size()
            if (r2 <= 0) goto L_0x00f0
            boolean r2 = r5.isOpened
            if (r2 == 0) goto L_0x00f0
            org.telegram.ui.Components.RecyclerItemsEnterAnimator r2 = r5.recyclerItemsEnterAnimator
            if (r2 == 0) goto L_0x00ee
            boolean r3 = r5.isPaused
            if (r3 != 0) goto L_0x00ee
            int r7 = r7 + r1
            r2.showItemsAnimated(r7)
        L_0x00ee:
            r7 = 0
            goto L_0x00f1
        L_0x00f0:
            r7 = 1
        L_0x00f1:
            org.telegram.tgnet.TLRPC$ChatFull r2 = r5.info
            if (r2 == 0) goto L_0x010c
            if (r9 != 0) goto L_0x010c
            int r8 = r8.count
            r2.invitesCount = r8
            org.telegram.messenger.MessagesStorage r8 = r5.getMessagesStorage()
            int r9 = r5.currentChatId
            org.telegram.tgnet.TLRPC$ChatFull r2 = r5.info
            int r2 = r2.invitesCount
            r8.saveChatLinksCount(r9, r2)
            goto L_0x010c
        L_0x0109:
            r5.hasMore = r0
            r7 = 0
        L_0x010c:
            boolean r8 = r5.hasMore
            if (r8 != 0) goto L_0x0127
            boolean r8 = r5.loadRevoked
            if (r8 != 0) goto L_0x0127
            int r8 = r5.adminId
            org.telegram.messenger.AccountInstance r9 = r5.getAccountInstance()
            org.telegram.messenger.UserConfig r9 = r9.getUserConfig()
            int r9 = r9.clientUserId
            if (r8 != r9) goto L_0x0127
            r5.hasMore = r1
            r5.loadAdmins = r1
            goto L_0x0133
        L_0x0127:
            boolean r8 = r5.hasMore
            if (r8 != 0) goto L_0x0135
            boolean r8 = r5.loadRevoked
            if (r8 != 0) goto L_0x0135
            r5.hasMore = r1
            r5.loadRevoked = r1
        L_0x0133:
            r8 = 1
            goto L_0x0136
        L_0x0135:
            r8 = 0
        L_0x0136:
            boolean r9 = r5.hasMore
            if (r9 == 0) goto L_0x0151
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_chatInviteExported> r9 = r5.invites
            int r9 = r9.size()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_chatInviteExported> r2 = r5.revokedInvites
            int r2 = r2.size()
            int r9 = r9 + r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_chatAdminWithInvites> r2 = r5.admins
            int r2 = r2.size()
            int r9 = r9 + r2
            r2 = 5
            if (r9 < r2) goto L_0x0154
        L_0x0151:
            r5.resumeDelayedFragmentAnimation()
        L_0x0154:
            if (r8 == 0) goto L_0x0159
            r5.loadLinks(r0)
        L_0x0159:
            if (r7 == 0) goto L_0x016b
            org.telegram.ui.ManageLinksActivity$ListAdapter r7 = r5.listViewAdapter
            if (r7 == 0) goto L_0x016b
            org.telegram.ui.Components.RecyclerListView r7 = r5.listView
            int r7 = r7.getChildCount()
            if (r7 <= 0) goto L_0x016b
            r5.updateRecyclerViewAnimated(r6)
            goto L_0x016e
        L_0x016b:
            r5.updateRows(r1)
        L_0x016e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ManageLinksActivity.lambda$loadLinks$3$ManageLinksActivity(org.telegram.tgnet.TLRPC$TL_chatInviteExported, org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, boolean):void");
    }

    /* access modifiers changed from: private */
    public void updateRows(boolean z) {
        TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.currentChatId));
        this.currentChat = chat;
        if (chat != null) {
            this.creatorRow = -1;
            this.creatorDividerRow = -1;
            this.linksStartRow = -1;
            this.linksEndRow = -1;
            this.linksLoadingRow = -1;
            this.revokedLinksStartRow = -1;
            this.revokedLinksEndRow = -1;
            this.revokedHeader = -1;
            this.revokedDivider = -1;
            this.lastDivider = -1;
            this.revokeAllRow = -1;
            this.revokeAllDivider = -1;
            this.createLinkHelpRow = -1;
            this.helpRow = -1;
            this.createNewLinkRow = -1;
            this.adminsEndRow = -1;
            this.adminsStartRow = -1;
            this.adminsDividerRow = -1;
            this.adminsHeaderRow = -1;
            this.linksHeaderRow = -1;
            this.dividerRow = -1;
            boolean z2 = false;
            this.rowCount = 0;
            if (this.adminId != getAccountInstance().getUserConfig().clientUserId) {
                z2 = true;
            }
            if (z2) {
                int i = this.rowCount;
                int i2 = i + 1;
                this.rowCount = i2;
                this.creatorRow = i;
                this.rowCount = i2 + 1;
                this.creatorDividerRow = i2;
            } else {
                int i3 = this.rowCount;
                this.rowCount = i3 + 1;
                this.helpRow = i3;
            }
            int i4 = this.rowCount;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.permanentLinkHeaderRow = i4;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.permanentLinkRow = i5;
            if (!z2) {
                int i7 = i6 + 1;
                this.rowCount = i7;
                this.dividerRow = i6;
                this.rowCount = i7 + 1;
                this.createNewLinkRow = i7;
            } else if (!this.invites.isEmpty()) {
                int i8 = this.rowCount;
                int i9 = i8 + 1;
                this.rowCount = i9;
                this.dividerRow = i8;
                this.rowCount = i9 + 1;
                this.linksHeaderRow = i9;
            }
            if (!this.invites.isEmpty()) {
                int i10 = this.rowCount;
                this.linksStartRow = i10;
                int size = i10 + this.invites.size();
                this.rowCount = size;
                this.linksEndRow = size;
            }
            if (!z2 && this.invites.isEmpty() && this.createNewLinkRow >= 0 && (!this.linksLoading || this.loadAdmins || this.loadRevoked)) {
                int i11 = this.rowCount;
                this.rowCount = i11 + 1;
                this.createLinkHelpRow = i11;
            }
            if (!z2 && this.admins.size() > 0) {
                if ((!this.invites.isEmpty() || this.createNewLinkRow >= 0) && this.createLinkHelpRow == -1) {
                    int i12 = this.rowCount;
                    this.rowCount = i12 + 1;
                    this.adminsDividerRow = i12;
                }
                int i13 = this.rowCount;
                int i14 = i13 + 1;
                this.rowCount = i14;
                this.adminsHeaderRow = i13;
                this.adminsStartRow = i14;
                int size2 = i14 + this.admins.size();
                this.rowCount = size2;
                this.adminsEndRow = size2;
            }
            if (!this.revokedInvites.isEmpty()) {
                if (this.adminsStartRow >= 0) {
                    int i15 = this.rowCount;
                    this.rowCount = i15 + 1;
                    this.revokedDivider = i15;
                } else if ((!this.invites.isEmpty() || this.createNewLinkRow >= 0) && this.createLinkHelpRow == -1) {
                    int i16 = this.rowCount;
                    this.rowCount = i16 + 1;
                    this.revokedDivider = i16;
                } else if (z2 && this.linksStartRow == -1) {
                    int i17 = this.rowCount;
                    this.rowCount = i17 + 1;
                    this.revokedDivider = i17;
                }
                int i18 = this.rowCount;
                int i19 = i18 + 1;
                this.rowCount = i19;
                this.revokedHeader = i18;
                this.revokedLinksStartRow = i19;
                int size3 = i19 + this.revokedInvites.size();
                this.rowCount = size3;
                this.revokedLinksEndRow = size3;
                int i20 = size3 + 1;
                this.rowCount = i20;
                this.revokeAllDivider = size3;
                this.rowCount = i20 + 1;
                this.revokeAllRow = i20;
            }
            if (!this.loadAdmins && !this.loadRevoked && ((this.linksLoading || this.hasMore) && !z2)) {
                int i21 = this.rowCount;
                this.rowCount = i21 + 1;
                this.linksLoadingRow = i21;
            }
            if (!this.invites.isEmpty() || !this.revokedInvites.isEmpty()) {
                int i22 = this.rowCount;
                this.rowCount = i22 + 1;
                this.lastDivider = i22;
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null && z) {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("InviteLinks", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ManageLinksActivity.this.finishFragment();
                }
            }
        });
        AnonymousClass3 r0 = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                AndroidUtilities.runOnUIThread(ManageLinksActivity.this.updateTimerRunnable, 500);
            }

            /* access modifiers changed from: protected */
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                AndroidUtilities.cancelRunOnUIThread(ManageLinksActivity.this.updateTimerRunnable);
            }
        };
        this.fragmentView = r0;
        r0.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView.setTag("windowBackgroundGray");
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context) {
            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                ManageLinksActivity.this.recyclerItemsEnterAnimator.dispatchDraw();
                super.dispatchDraw(canvas);
            }

            /* access modifiers changed from: protected */
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                ManageLinksActivity.this.recyclerItemsEnterAnimator.onDetached();
            }
        };
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.listView.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
                ManageLinksActivity manageLinksActivity = ManageLinksActivity.this;
                if (manageLinksActivity.hasMore && !manageLinksActivity.linksLoading) {
                    if (ManageLinksActivity.this.rowCount - linearLayoutManager.findLastVisibleItemPosition() < 10) {
                        ManageLinksActivity.this.loadLinks(true);
                    }
                }
            }
        });
        this.recyclerItemsEnterAnimator = new RecyclerItemsEnterAnimator(this.listView, false);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.listView.setItemAnimator(defaultItemAnimator);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(context) {
            public final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onItemClick(View view, int i) {
                ManageLinksActivity.this.lambda$createView$9$ManageLinksActivity(this.f$1, view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return ManageLinksActivity.this.lambda$createView$10$ManageLinksActivity(view, i);
            }
        });
        this.linkIcon = ContextCompat.getDrawable(context, NUM);
        this.linkIconRevoked = ContextCompat.getDrawable(context, NUM);
        this.linkIcon.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        updateRows(true);
        this.timeDif = ((long) getConnectionsManager().getCurrentTime()) - (System.currentTimeMillis() / 1000);
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$9 */
    public /* synthetic */ void lambda$createView$9$ManageLinksActivity(Context context, View view, int i) {
        if (i == this.creatorRow) {
            TLRPC$User tLRPC$User = this.users.get(Integer.valueOf(this.invite.admin_id));
            if (tLRPC$User != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", tLRPC$User.id);
                MessagesController.getInstance(UserConfig.selectedAccount).putUser(tLRPC$User, false);
                presentFragment(new ProfileActivity(bundle));
            }
        } else if (i == this.createNewLinkRow) {
            LinkEditActivity linkEditActivity = new LinkEditActivity(0, this.currentChatId);
            linkEditActivity.setCallback(this.linkEditActivityCallback);
            presentFragment(linkEditActivity);
        } else {
            int i2 = this.linksStartRow;
            if (i < i2 || i >= this.linksEndRow) {
                int i3 = this.revokedLinksStartRow;
                if (i >= i3 && i < this.revokedLinksEndRow) {
                    InviteLinkBottomSheet inviteLinkBottomSheet2 = new InviteLinkBottomSheet(context, this.revokedInvites.get(i - i3), this.info, this.users, this, this.currentChatId, false, this.isChannel);
                    this.inviteLinkBottomSheet = inviteLinkBottomSheet2;
                    inviteLinkBottomSheet2.show();
                } else if (i != this.revokeAllRow) {
                    int i4 = this.adminsStartRow;
                    if (i >= i4 && i < this.adminsEndRow) {
                        TLRPC$TL_chatAdminWithInvites tLRPC$TL_chatAdminWithInvites = this.admins.get(i - i4);
                        if (this.users.containsKey(Integer.valueOf(tLRPC$TL_chatAdminWithInvites.admin_id))) {
                            getMessagesController().putUser(this.users.get(Integer.valueOf(tLRPC$TL_chatAdminWithInvites.admin_id)), false);
                        }
                        ManageLinksActivity manageLinksActivity = new ManageLinksActivity(this.currentChatId, tLRPC$TL_chatAdminWithInvites.admin_id, tLRPC$TL_chatAdminWithInvites.invites_count);
                        manageLinksActivity.setInfo(this.info, (TLRPC$ExportedChatInvite) null);
                        presentFragment(manageLinksActivity);
                    }
                } else if (!this.deletingRevokedLinks) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setTitle(LocaleController.getString("DeleteAllRevokedLinks", NUM));
                    builder.setMessage(LocaleController.getString("DeleteAllRevokedLinkHelp", NUM));
                    builder.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ManageLinksActivity.this.lambda$createView$8$ManageLinksActivity(dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder.create());
                }
            } else {
                InviteLinkBottomSheet inviteLinkBottomSheet3 = new InviteLinkBottomSheet(context, this.invites.get(i - i2), this.info, this.users, this, this.currentChatId, false, this.isChannel);
                this.inviteLinkBottomSheet = inviteLinkBottomSheet3;
                inviteLinkBottomSheet3.setCanEdit(this.canEdit);
                this.inviteLinkBottomSheet.show();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$8 */
    public /* synthetic */ void lambda$createView$8$ManageLinksActivity(DialogInterface dialogInterface, int i) {
        TLRPC$TL_messages_deleteRevokedExportedChatInvites tLRPC$TL_messages_deleteRevokedExportedChatInvites = new TLRPC$TL_messages_deleteRevokedExportedChatInvites();
        tLRPC$TL_messages_deleteRevokedExportedChatInvites.peer = getMessagesController().getInputPeer(-this.currentChatId);
        if (this.adminId == getUserConfig().getClientUserId()) {
            tLRPC$TL_messages_deleteRevokedExportedChatInvites.admin_id = getMessagesController().getInputUser(getUserConfig().getCurrentUser());
        } else {
            tLRPC$TL_messages_deleteRevokedExportedChatInvites.admin_id = getMessagesController().getInputUser(this.adminId);
        }
        this.deletingRevokedLinks = true;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_deleteRevokedExportedChatInvites, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ManageLinksActivity.this.lambda$createView$7$ManageLinksActivity(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$7 */
    public /* synthetic */ void lambda$createView$7$ManageLinksActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            public final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ManageLinksActivity.this.lambda$createView$6$ManageLinksActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$6 */
    public /* synthetic */ void lambda$createView$6$ManageLinksActivity(TLRPC$TL_error tLRPC$TL_error) {
        this.deletingRevokedLinks = false;
        if (tLRPC$TL_error == null) {
            DiffCallback saveListState = saveListState();
            this.revokedInvites.clear();
            updateRecyclerViewAnimated(saveListState);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$10 */
    public /* synthetic */ boolean lambda$createView$10$ManageLinksActivity(View view, int i) {
        if ((i < this.linksStartRow || i >= this.linksEndRow) && (i < this.revokedLinksStartRow || i >= this.revokedLinksEndRow)) {
            return false;
        }
        ((LinkCell) view).optionsView.callOnClick();
        view.performHapticFeedback(0, 2);
        return true;
    }

    public void setInfo(TLRPC$ChatFull tLRPC$ChatFull, TLRPC$ExportedChatInvite tLRPC$ExportedChatInvite) {
        this.info = tLRPC$ChatFull;
        this.invite = (TLRPC$TL_chatInviteExported) tLRPC$ExportedChatInvite;
        this.isPublic = !TextUtils.isEmpty(this.currentChat.username);
        loadLinks(true);
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public class HintInnerCell extends FrameLayout {
        private EmptyView emptyView;
        private TextView messageTextView;

        public HintInnerCell(Context context) {
            super(context);
            int i;
            String str;
            EmptyView emptyView2 = new EmptyView(context);
            this.emptyView = emptyView2;
            addView(emptyView2, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 10.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.messageTextView = textView;
            textView.setTextColor(Theme.getColor("chats_message"));
            this.messageTextView.setTextSize(1, 14.0f);
            this.messageTextView.setGravity(17);
            TextView textView2 = this.messageTextView;
            if (ManageLinksActivity.this.isChannel) {
                i = NUM;
                str = "PrimaryLinkHelpChannel";
            } else {
                i = NUM;
                str = "PrimaryLinkHelp";
            }
            textView2.setText(LocaleController.getString(str, i));
            addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 143.0f, 52.0f, 18.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), i2);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            if (ManageLinksActivity.this.creatorRow == adapterPosition || ManageLinksActivity.this.createNewLinkRow == adapterPosition) {
                return true;
            }
            if (adapterPosition >= ManageLinksActivity.this.linksStartRow && adapterPosition < ManageLinksActivity.this.linksEndRow) {
                return true;
            }
            if ((adapterPosition >= ManageLinksActivity.this.revokedLinksStartRow && adapterPosition < ManageLinksActivity.this.revokedLinksEndRow) || adapterPosition == ManageLinksActivity.this.revokeAllRow) {
                return true;
            }
            if (adapterPosition < ManageLinksActivity.this.adminsStartRow || adapterPosition >= ManageLinksActivity.this.adminsEndRow) {
                return false;
            }
            return true;
        }

        public int getItemCount() {
            return ManageLinksActivity.this.rowCount;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v2, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v3, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: org.telegram.ui.ManageLinksActivity$TextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v6, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v7, resolved type: org.telegram.ui.ManageLinksActivity$LinkCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v8, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v15, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v9, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v11, resolved type: org.telegram.ui.Cells.TextSettingsCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v23, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v13, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v14, resolved type: org.telegram.ui.ManageLinksActivity$HintInnerCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v15, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v16, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v17, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v18, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v19, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v29, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v20, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v30, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v21, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v22, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX WARNING: type inference failed for: r13v16, types: [org.telegram.ui.Cells.ShadowSectionCell, android.view.View] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r12, int r13) {
            /*
                r11 = this;
                java.lang.String r12 = "windowBackgroundGrayShadow"
                r0 = 1
                r1 = 0
                r2 = 2131165443(0x7var_, float:1.7945103E38)
                java.lang.String r3 = "windowBackgroundWhite"
                switch(r13) {
                    case 1: goto L_0x00ec;
                    case 2: goto L_0x00c3;
                    case 3: goto L_0x00b4;
                    case 4: goto L_0x00ac;
                    case 5: goto L_0x00a2;
                    case 6: goto L_0x0088;
                    case 7: goto L_0x0075;
                    case 8: goto L_0x0050;
                    case 9: goto L_0x0033;
                    case 10: goto L_0x0020;
                    default: goto L_0x000c;
                }
            L_0x000c:
                org.telegram.ui.ManageLinksActivity$HintInnerCell r12 = new org.telegram.ui.ManageLinksActivity$HintInnerCell
                org.telegram.ui.ManageLinksActivity r13 = org.telegram.ui.ManageLinksActivity.this
                android.content.Context r0 = r11.mContext
                r12.<init>(r0)
                android.content.Context r13 = r11.mContext
                android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r13, (int) r2, (java.lang.String) r3)
                r12.setBackgroundDrawable(r13)
                goto L_0x00fc
            L_0x0020:
                org.telegram.ui.Cells.ManageChatUserCell r12 = new org.telegram.ui.Cells.ManageChatUserCell
                android.content.Context r13 = r11.mContext
                r0 = 8
                r2 = 6
                r12.<init>(r13, r0, r2, r1)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r12.setBackgroundColor(r13)
                goto L_0x00fc
            L_0x0033:
                org.telegram.ui.Cells.TextInfoPrivacyCell r13 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r0 = r11.mContext
                r13.<init>(r0)
                r0 = 2131625049(0x7f0e0459, float:1.8877295E38)
                java.lang.String r1 = "CreateNewLinkHelp"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setText(r0)
                android.content.Context r0 = r11.mContext
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r0, (int) r2, (java.lang.String) r12)
                r13.setBackground(r12)
                goto L_0x0085
            L_0x0050:
                org.telegram.ui.Cells.TextSettingsCell r12 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r13 = r11.mContext
                r12.<init>(r13)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r12.setBackgroundColor(r13)
                r13 = 2131625138(0x7f0e04b2, float:1.8877475E38)
                java.lang.String r0 = "DeleteAllRevokedLinks"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13, r1)
                java.lang.String r13 = "windowBackgroundWhiteRedText5"
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
                r12.setTextColor(r13)
                goto L_0x00fc
            L_0x0075:
                org.telegram.ui.Cells.ShadowSectionCell r13 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r0 = r11.mContext
                r13.<init>(r0)
                android.content.Context r0 = r11.mContext
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r0, (int) r2, (java.lang.String) r12)
                r13.setBackground(r12)
            L_0x0085:
                r12 = r13
                goto L_0x00fc
            L_0x0088:
                org.telegram.ui.Components.FlickerLoadingView r12 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r13 = r11.mContext
                r12.<init>(r13)
                r12.setIsSingleCell(r0)
                r13 = 9
                r12.setViewType(r13)
                r12.showDate(r1)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r12.setBackgroundColor(r13)
                goto L_0x00fc
            L_0x00a2:
                org.telegram.ui.ManageLinksActivity$LinkCell r12 = new org.telegram.ui.ManageLinksActivity$LinkCell
                org.telegram.ui.ManageLinksActivity r13 = org.telegram.ui.ManageLinksActivity.this
                android.content.Context r0 = r11.mContext
                r12.<init>(r0)
                goto L_0x00fc
            L_0x00ac:
                org.telegram.ui.Cells.ShadowSectionCell r12 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r13 = r11.mContext
                r12.<init>(r13)
                goto L_0x00fc
            L_0x00b4:
                org.telegram.ui.ManageLinksActivity$TextCell r12 = new org.telegram.ui.ManageLinksActivity$TextCell
                android.content.Context r13 = r11.mContext
                r12.<init>(r13)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r12.setBackgroundColor(r13)
                goto L_0x00fc
            L_0x00c3:
                org.telegram.ui.Components.LinkActionView r12 = new org.telegram.ui.Components.LinkActionView
                android.content.Context r5 = r11.mContext
                org.telegram.ui.ManageLinksActivity r6 = org.telegram.ui.ManageLinksActivity.this
                r7 = 0
                int r8 = r6.currentChatId
                r9 = 1
                org.telegram.ui.ManageLinksActivity r13 = org.telegram.ui.ManageLinksActivity.this
                boolean r10 = r13.isChannel
                r4 = r12
                r4.<init>(r5, r6, r7, r8, r9, r10)
                r12.setPermanent(r0)
                org.telegram.ui.ManageLinksActivity$ListAdapter$1 r13 = new org.telegram.ui.ManageLinksActivity$ListAdapter$1
                r13.<init>(r12)
                r12.setDelegate(r13)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r12.setBackgroundColor(r13)
                goto L_0x00fc
            L_0x00ec:
                org.telegram.ui.Cells.HeaderCell r12 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r13 = r11.mContext
                r0 = 23
                r12.<init>(r13, r0)
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r12.setBackgroundColor(r13)
            L_0x00fc:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r13 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r1 = -2
                r13.<init>((int) r0, (int) r1)
                r12.setLayoutParams(r13)
                org.telegram.ui.Components.RecyclerListView$Holder r13 = new org.telegram.ui.Components.RecyclerListView$Holder
                r13.<init>(r12)
                return r13
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ManageLinksActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:25:0x00b3, code lost:
            if (r7 == (org.telegram.ui.ManageLinksActivity.access$800(r5.this$0) - 1)) goto L_0x00d3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x00d1, code lost:
            if (r7 == (org.telegram.ui.ManageLinksActivity.access$1000(r5.this$0) - 1)) goto L_0x00d3;
         */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x0075  */
        /* JADX WARNING: Removed duplicated region for block: B:67:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r6, int r7) {
            /*
                r5 = this;
                int r0 = r6.getItemViewType()
                r1 = 1
                if (r0 == r1) goto L_0x01df
                r2 = 2
                r3 = 0
                if (r0 == r2) goto L_0x0142
                r2 = 3
                if (r0 == r2) goto L_0x00e6
                r2 = 5
                if (r0 == r2) goto L_0x0088
                r2 = 10
                if (r0 == r2) goto L_0x0017
                goto L_0x0280
            L_0x0017:
                android.view.View r6 = r6.itemView
                org.telegram.ui.Cells.ManageChatUserCell r6 = (org.telegram.ui.Cells.ManageChatUserCell) r6
                org.telegram.ui.ManageLinksActivity r0 = org.telegram.ui.ManageLinksActivity.this
                int r0 = r0.creatorRow
                if (r7 != r0) goto L_0x003f
                org.telegram.ui.ManageLinksActivity r7 = org.telegram.ui.ManageLinksActivity.this
                org.telegram.messenger.MessagesController r7 = r7.getMessagesController()
                org.telegram.ui.ManageLinksActivity r0 = org.telegram.ui.ManageLinksActivity.this
                int r0 = r0.adminId
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$User r7 = r7.getUser(r0)
                org.telegram.ui.ManageLinksActivity r0 = org.telegram.ui.ManageLinksActivity.this
                int r0 = r0.invitesCount
            L_0x003d:
                r1 = 0
                goto L_0x0073
            L_0x003f:
                org.telegram.ui.ManageLinksActivity r0 = org.telegram.ui.ManageLinksActivity.this
                int r0 = r0.adminsStartRow
                int r0 = r7 - r0
                org.telegram.ui.ManageLinksActivity r2 = org.telegram.ui.ManageLinksActivity.this
                java.util.ArrayList r2 = r2.admins
                java.lang.Object r0 = r2.get(r0)
                org.telegram.tgnet.TLRPC$TL_chatAdminWithInvites r0 = (org.telegram.tgnet.TLRPC$TL_chatAdminWithInvites) r0
                org.telegram.ui.ManageLinksActivity r2 = org.telegram.ui.ManageLinksActivity.this
                java.util.HashMap r2 = r2.users
                int r4 = r0.admin_id
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                java.lang.Object r2 = r2.get(r4)
                org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
                int r0 = r0.invites_count
                org.telegram.ui.ManageLinksActivity r4 = org.telegram.ui.ManageLinksActivity.this
                int r4 = r4.adminsEndRow
                int r4 = r4 - r1
                if (r7 != r4) goto L_0x0072
                r7 = r2
                goto L_0x003d
            L_0x0072:
                r7 = r2
            L_0x0073:
                if (r7 == 0) goto L_0x0280
                java.lang.String r2 = r7.first_name
                java.lang.String r3 = r7.last_name
                java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
                java.lang.String r3 = "InviteLinkCount"
                java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r3, r0)
                r6.setData(r7, r2, r0, r1)
                goto L_0x0280
            L_0x0088:
                org.telegram.ui.ManageLinksActivity r0 = org.telegram.ui.ManageLinksActivity.this
                int r0 = r0.linksStartRow
                if (r7 < r0) goto L_0x00b6
                org.telegram.ui.ManageLinksActivity r0 = org.telegram.ui.ManageLinksActivity.this
                int r0 = r0.linksEndRow
                if (r7 >= r0) goto L_0x00b6
                org.telegram.ui.ManageLinksActivity r0 = org.telegram.ui.ManageLinksActivity.this
                java.util.ArrayList r0 = r0.invites
                org.telegram.ui.ManageLinksActivity r2 = org.telegram.ui.ManageLinksActivity.this
                int r2 = r2.linksStartRow
                int r2 = r7 - r2
                java.lang.Object r0 = r0.get(r2)
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r0 = (org.telegram.tgnet.TLRPC$TL_chatInviteExported) r0
                org.telegram.ui.ManageLinksActivity r2 = org.telegram.ui.ManageLinksActivity.this
                int r2 = r2.linksEndRow
                int r2 = r2 - r1
                if (r7 != r2) goto L_0x00d4
                goto L_0x00d3
            L_0x00b6:
                org.telegram.ui.ManageLinksActivity r0 = org.telegram.ui.ManageLinksActivity.this
                java.util.ArrayList r0 = r0.revokedInvites
                org.telegram.ui.ManageLinksActivity r2 = org.telegram.ui.ManageLinksActivity.this
                int r2 = r2.revokedLinksStartRow
                int r2 = r7 - r2
                java.lang.Object r0 = r0.get(r2)
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r0 = (org.telegram.tgnet.TLRPC$TL_chatInviteExported) r0
                org.telegram.ui.ManageLinksActivity r2 = org.telegram.ui.ManageLinksActivity.this
                int r2 = r2.revokedLinksEndRow
                int r2 = r2 - r1
                if (r7 != r2) goto L_0x00d4
            L_0x00d3:
                r1 = 0
            L_0x00d4:
                android.view.View r6 = r6.itemView
                org.telegram.ui.ManageLinksActivity$LinkCell r6 = (org.telegram.ui.ManageLinksActivity.LinkCell) r6
                org.telegram.ui.ManageLinksActivity r2 = org.telegram.ui.ManageLinksActivity.this
                int r2 = r2.linksStartRow
                int r7 = r7 - r2
                r6.setLink(r0, r7)
                r6.drawDivider = r1
                goto L_0x0280
            L_0x00e6:
                android.view.View r6 = r6.itemView
                org.telegram.ui.ManageLinksActivity$TextCell r6 = (org.telegram.ui.ManageLinksActivity.TextCell) r6
                android.content.Context r7 = r5.mContext
                android.content.res.Resources r7 = r7.getResources()
                r0 = 2131165952(0x7var_, float:1.7946136E38)
                android.graphics.drawable.Drawable r7 = r7.getDrawable(r0)
                android.content.Context r0 = r5.mContext
                android.content.res.Resources r0 = r0.getResources()
                r2 = 2131165953(0x7var_, float:1.7946138E38)
                android.graphics.drawable.Drawable r0 = r0.getDrawable(r2)
                android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
                java.lang.String r3 = "switchTrackChecked"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
                r2.<init>(r3, r4)
                r7.setColorFilter(r2)
                android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
                java.lang.String r3 = "checkboxCheck"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
                r2.<init>(r3, r4)
                r0.setColorFilter(r2)
                org.telegram.ui.Components.CombinedDrawable r2 = new org.telegram.ui.Components.CombinedDrawable
                r2.<init>(r7, r0)
                r7 = 2131625048(0x7f0e0458, float:1.8877293E38)
                java.lang.String r0 = "CreateNewLink"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                org.telegram.ui.ManageLinksActivity r0 = org.telegram.ui.ManageLinksActivity.this
                java.util.ArrayList r0 = r0.invites
                boolean r0 = r0.isEmpty()
                r0 = r0 ^ r1
                r6.setTextAndIcon(r7, r2, r0)
                goto L_0x0280
            L_0x0142:
                android.view.View r6 = r6.itemView
                org.telegram.ui.Components.LinkActionView r6 = (org.telegram.ui.Components.LinkActionView) r6
                org.telegram.ui.ManageLinksActivity r7 = org.telegram.ui.ManageLinksActivity.this
                int r7 = r7.adminId
                org.telegram.ui.ManageLinksActivity r0 = org.telegram.ui.ManageLinksActivity.this
                org.telegram.messenger.AccountInstance r0 = r0.getAccountInstance()
                org.telegram.messenger.UserConfig r0 = r0.getUserConfig()
                int r0 = r0.clientUserId
                if (r7 != r0) goto L_0x015c
                r7 = 1
                goto L_0x015d
            L_0x015c:
                r7 = 0
            L_0x015d:
                r6.setCanEdit(r7)
                org.telegram.ui.ManageLinksActivity r7 = org.telegram.ui.ManageLinksActivity.this
                boolean r7 = r7.isPublic
                r0 = 0
                if (r7 == 0) goto L_0x01a9
                org.telegram.ui.ManageLinksActivity r7 = org.telegram.ui.ManageLinksActivity.this
                int r7 = r7.adminId
                org.telegram.ui.ManageLinksActivity r2 = org.telegram.ui.ManageLinksActivity.this
                org.telegram.messenger.AccountInstance r2 = r2.getAccountInstance()
                org.telegram.messenger.UserConfig r2 = r2.getUserConfig()
                int r2 = r2.clientUserId
                if (r7 != r2) goto L_0x01a9
                org.telegram.ui.ManageLinksActivity r7 = org.telegram.ui.ManageLinksActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r7 = r7.info
                if (r7 == 0) goto L_0x0280
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                java.lang.String r2 = "https://t.me/"
                r7.append(r2)
                org.telegram.ui.ManageLinksActivity r2 = org.telegram.ui.ManageLinksActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                java.lang.String r2 = r2.username
                r7.append(r2)
                java.lang.String r7 = r7.toString()
                r6.setLink(r7)
                r6.setUsers(r3, r0)
                r6.hideRevokeOption(r1)
                goto L_0x0280
            L_0x01a9:
                org.telegram.ui.ManageLinksActivity r7 = org.telegram.ui.ManageLinksActivity.this
                boolean r7 = r7.canEdit
                r7 = r7 ^ r1
                r6.hideRevokeOption(r7)
                org.telegram.ui.ManageLinksActivity r7 = org.telegram.ui.ManageLinksActivity.this
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r7 = r7.invite
                if (r7 == 0) goto L_0x01d1
                org.telegram.ui.ManageLinksActivity r7 = org.telegram.ui.ManageLinksActivity.this
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r7 = r7.invite
                java.lang.String r0 = r7.link
                r6.setLink(r0)
                org.telegram.ui.ManageLinksActivity r0 = org.telegram.ui.ManageLinksActivity.this
                int r0 = r0.currentChatId
                r6.loadUsers(r7, r0)
                goto L_0x0280
            L_0x01d1:
                r6.setLink(r0)
                org.telegram.ui.ManageLinksActivity r7 = org.telegram.ui.ManageLinksActivity.this
                int r7 = r7.currentChatId
                r6.loadUsers(r0, r7)
                goto L_0x0280
            L_0x01df:
                android.view.View r6 = r6.itemView
                org.telegram.ui.Cells.HeaderCell r6 = (org.telegram.ui.Cells.HeaderCell) r6
                org.telegram.ui.ManageLinksActivity r0 = org.telegram.ui.ManageLinksActivity.this
                int r0 = r0.permanentLinkHeaderRow
                if (r7 != r0) goto L_0x0242
                org.telegram.ui.ManageLinksActivity r7 = org.telegram.ui.ManageLinksActivity.this
                boolean r7 = r7.isPublic
                if (r7 == 0) goto L_0x0214
                org.telegram.ui.ManageLinksActivity r7 = org.telegram.ui.ManageLinksActivity.this
                int r7 = r7.adminId
                org.telegram.ui.ManageLinksActivity r0 = org.telegram.ui.ManageLinksActivity.this
                org.telegram.messenger.AccountInstance r0 = r0.getAccountInstance()
                org.telegram.messenger.UserConfig r0 = r0.getUserConfig()
                int r0 = r0.clientUserId
                if (r7 != r0) goto L_0x0214
                r7 = 2131627133(0x7f0e0c7d, float:1.8881522E38)
                java.lang.String r0 = "PublicLink"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7)
                goto L_0x0280
            L_0x0214:
                org.telegram.ui.ManageLinksActivity r7 = org.telegram.ui.ManageLinksActivity.this
                int r7 = r7.adminId
                org.telegram.ui.ManageLinksActivity r0 = org.telegram.ui.ManageLinksActivity.this
                org.telegram.messenger.AccountInstance r0 = r0.getAccountInstance()
                org.telegram.messenger.UserConfig r0 = r0.getUserConfig()
                int r0 = r0.clientUserId
                if (r7 != r0) goto L_0x0235
                r7 = 2131624733(0x7f0e031d, float:1.8876654E38)
                java.lang.String r0 = "ChannelInviteLinkTitle"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7)
                goto L_0x0280
            L_0x0235:
                r7 = 2131626939(0x7f0e0bbb, float:1.8881128E38)
                java.lang.String r0 = "PermanentLinkForThisAdmin"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7)
                goto L_0x0280
            L_0x0242:
                org.telegram.ui.ManageLinksActivity r0 = org.telegram.ui.ManageLinksActivity.this
                int r0 = r0.revokedHeader
                if (r7 != r0) goto L_0x0257
                r7 = 2131627317(0x7f0e0d35, float:1.8881895E38)
                java.lang.String r0 = "RevokedLinks"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7)
                goto L_0x0280
            L_0x0257:
                org.telegram.ui.ManageLinksActivity r0 = org.telegram.ui.ManageLinksActivity.this
                int r0 = r0.linksHeaderRow
                if (r7 != r0) goto L_0x026c
                r7 = 2131626025(0x7f0e0829, float:1.8879275E38)
                java.lang.String r0 = "LinksCreatedByThisAdmin"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7)
                goto L_0x0280
            L_0x026c:
                org.telegram.ui.ManageLinksActivity r0 = org.telegram.ui.ManageLinksActivity.this
                int r0 = r0.adminsHeaderRow
                if (r7 != r0) goto L_0x0280
                r7 = 2131626024(0x7f0e0828, float:1.8879273E38)
                java.lang.String r0 = "LinksCreatedByOtherAdmins"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7)
            L_0x0280:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ManageLinksActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        public int getItemViewType(int i) {
            if (i == ManageLinksActivity.this.helpRow) {
                return 0;
            }
            if (i == ManageLinksActivity.this.permanentLinkHeaderRow || i == ManageLinksActivity.this.revokedHeader || i == ManageLinksActivity.this.adminsHeaderRow || i == ManageLinksActivity.this.linksHeaderRow) {
                return 1;
            }
            if (i == ManageLinksActivity.this.permanentLinkRow) {
                return 2;
            }
            if (i == ManageLinksActivity.this.createNewLinkRow) {
                return 3;
            }
            if (i == ManageLinksActivity.this.dividerRow || i == ManageLinksActivity.this.revokedDivider || i == ManageLinksActivity.this.revokeAllDivider || i == ManageLinksActivity.this.creatorDividerRow || i == ManageLinksActivity.this.adminsDividerRow) {
                return 4;
            }
            if (i >= ManageLinksActivity.this.linksStartRow && i < ManageLinksActivity.this.linksEndRow) {
                return 5;
            }
            if (i >= ManageLinksActivity.this.revokedLinksStartRow && i < ManageLinksActivity.this.revokedLinksEndRow) {
                return 5;
            }
            if (i == ManageLinksActivity.this.linksLoadingRow) {
                return 6;
            }
            if (i == ManageLinksActivity.this.lastDivider) {
                return 7;
            }
            if (i == ManageLinksActivity.this.revokeAllRow) {
                return 8;
            }
            if (i == ManageLinksActivity.this.createLinkHelpRow) {
                return 9;
            }
            if (i == ManageLinksActivity.this.creatorRow) {
                return 10;
            }
            if (i < ManageLinksActivity.this.adminsStartRow || i >= ManageLinksActivity.this.adminsEndRow) {
                return 1;
            }
            return 10;
        }
    }

    /* access modifiers changed from: private */
    public void revokePermanent() {
        if (this.adminId == getAccountInstance().getUserConfig().clientUserId) {
            TLRPC$TL_messages_exportChatInvite tLRPC$TL_messages_exportChatInvite = new TLRPC$TL_messages_exportChatInvite();
            tLRPC$TL_messages_exportChatInvite.peer = getMessagesController().getInputPeer(-this.currentChatId);
            tLRPC$TL_messages_exportChatInvite.legacy_revoke_permanent = true;
            TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = this.invite;
            this.invite = null;
            this.info.exported_invite = null;
            int sendRequest = getConnectionsManager().sendRequest(tLRPC$TL_messages_exportChatInvite, new RequestDelegate(tLRPC$TL_chatInviteExported) {
                public final /* synthetic */ TLRPC$TL_chatInviteExported f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ManageLinksActivity.this.lambda$revokePermanent$12$ManageLinksActivity(this.f$1, tLObject, tLRPC$TL_error);
                }
            });
            AndroidUtilities.updateVisibleRows(this.listView);
            getConnectionsManager().bindRequestToGuid(sendRequest, this.classGuid);
            return;
        }
        revokeLink(this.invite);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$revokePermanent$12 */
    public /* synthetic */ void lambda$revokePermanent$12$ManageLinksActivity(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, tLRPC$TL_chatInviteExported) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ TLRPC$TL_chatInviteExported f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ManageLinksActivity.this.lambda$revokePermanent$11$ManageLinksActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$revokePermanent$11 */
    public /* synthetic */ void lambda$revokePermanent$11$ManageLinksActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported2 = (TLRPC$TL_chatInviteExported) tLObject;
            this.invite = tLRPC$TL_chatInviteExported2;
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            if (tLRPC$ChatFull != null) {
                tLRPC$ChatFull.exported_invite = tLRPC$TL_chatInviteExported2;
            }
            if (getParentActivity() != null) {
                tLRPC$TL_chatInviteExported.revoked = true;
                DiffCallback saveListState = saveListState();
                this.revokedInvites.add(0, tLRPC$TL_chatInviteExported);
                updateRecyclerViewAnimated(saveListState);
                BulletinFactory.of((BaseFragment) this).createSimpleBulletin(NUM, LocaleController.getString("InviteRevokedHint", NUM)).show();
            }
        }
    }

    public static class TextCell extends FrameLayout {
        boolean divider;
        private ImageView imageView;
        private SimpleTextView textView;

        public TextCell(Context context) {
            super(context);
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.textView = simpleTextView;
            simpleTextView.setTextSize(16);
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText2"));
            this.textView.setTag("windowBackgroundWhiteBlueText2");
            addView(this.textView);
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView);
            setWillNotDraw(false);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            AndroidUtilities.dp(48.0f);
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(94.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
            setMeasuredDimension(size, AndroidUtilities.dp(50.0f));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5;
            int i6 = i3 - i;
            int textHeight = ((i4 - i2) - this.textView.getTextHeight()) / 2;
            if (LocaleController.isRTL) {
                i5 = (getMeasuredWidth() - this.textView.getMeasuredWidth()) - AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? 70.0f : 25.0f);
            } else {
                i5 = AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? 70.0f : 25.0f);
            }
            SimpleTextView simpleTextView = this.textView;
            simpleTextView.layout(i5, textHeight, simpleTextView.getMeasuredWidth() + i5, this.textView.getMeasuredHeight() + textHeight);
            int dp = !LocaleController.isRTL ? (AndroidUtilities.dp(70.0f) - this.imageView.getMeasuredWidth()) / 2 : (i6 - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(25.0f);
            ImageView imageView2 = this.imageView;
            imageView2.layout(dp, 0, imageView2.getMeasuredWidth() + dp, this.imageView.getMeasuredHeight());
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.divider) {
                canvas.drawLine((float) AndroidUtilities.dp(70.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() + AndroidUtilities.dp(23.0f)), (float) getMeasuredHeight(), Theme.dividerPaint);
            }
        }

        public void setTextAndIcon(String str, Drawable drawable, boolean z) {
            this.textView.setText(str);
            this.imageView.setImageDrawable(drawable);
            this.divider = z;
        }
    }

    private class LinkCell extends FrameLayout {
        int animateFromState;
        boolean animateHideExpiring;
        float animateToStateProgress = 1.0f;
        boolean drawDivider;
        TLRPC$TL_chatInviteExported invite;
        float lastDrawExpringProgress;
        int lastDrawingState;
        ImageView optionsView;
        Paint paint = new Paint(1);
        Paint paint2 = new Paint(1);
        int position;
        RectF rectF = new RectF();
        TextView subtitleView;
        private TimerParticles timerParticles = new TimerParticles();
        boolean timerRunning;
        TextView titleView;

        private boolean hasProgress(int i) {
            return i == 2 || i == 1;
        }

        public LinkCell(Context context) {
            super(context);
            this.paint2.setStyle(Paint.Style.STROKE);
            this.paint2.setStrokeCap(Paint.Cap.ROUND);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 16, 70.0f, 0.0f, 30.0f, 0.0f));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 16.0f);
            this.titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.titleView.setLines(1);
            this.titleView.setEllipsize(TextUtils.TruncateAt.END);
            TextView textView2 = new TextView(context);
            this.subtitleView = textView2;
            textView2.setTextSize(1, 13.0f);
            this.subtitleView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            linearLayout.addView(this.titleView, LayoutHelper.createLinear(-1, -2));
            linearLayout.addView(this.subtitleView, LayoutHelper.createLinear(-1, -2, 0.0f, 6.0f, 0.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.optionsView = imageView;
            imageView.setImageDrawable(ContextCompat.getDrawable(context, NUM));
            this.optionsView.setScaleType(ImageView.ScaleType.CENTER);
            this.optionsView.setColorFilter(Theme.getColor("stickers_menu"));
            this.optionsView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ManageLinksActivity.LinkCell.this.lambda$new$3$ManageLinksActivity$LinkCell(view);
                }
            });
            this.optionsView.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 1));
            addView(this.optionsView, LayoutHelper.createFrame(40, 48, 21));
            setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            setWillNotDraw(false);
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:18:0x00fd  */
        /* JADX WARNING: Removed duplicated region for block: B:20:? A[RETURN, SYNTHETIC] */
        /* renamed from: lambda$new$3 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$new$3$ManageLinksActivity$LinkCell(android.view.View r8) {
            /*
                r7 = this;
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r8 = r7.invite
                if (r8 != 0) goto L_0x0005
                return
            L_0x0005:
                java.util.ArrayList r8 = new java.util.ArrayList
                r8.<init>()
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r2 = r7.invite
                boolean r2 = r2.revoked
                r3 = 2131165729(0x7var_, float:1.7945683E38)
                r4 = 0
                r5 = 1
                if (r2 == 0) goto L_0x003d
                r2 = 2131625126(0x7f0e04a6, float:1.8877451E38)
                java.lang.String r6 = "Delete"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
                r8.add(r2)
                java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
                r0.add(r2)
                r2 = 4
                java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                r1.add(r2)
            L_0x003a:
                r2 = 1
                goto L_0x00c9
            L_0x003d:
                r2 = 2131625031(0x7f0e0447, float:1.8877258E38)
                java.lang.String r6 = "CopyLink"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
                r8.add(r2)
                r2 = 2131165727(0x7var_f, float:1.794568E38)
                java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                r0.add(r2)
                java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
                r1.add(r2)
                r2 = 2131627507(0x7f0e0df3, float:1.888228E38)
                java.lang.String r6 = "ShareLink"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
                r8.add(r2)
                r2 = 2131165819(0x7var_b, float:1.7945866E38)
                java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                r0.add(r2)
                java.lang.Integer r2 = java.lang.Integer.valueOf(r5)
                r1.add(r2)
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r2 = r7.invite
                boolean r2 = r2.permanent
                if (r2 != 0) goto L_0x00a3
                org.telegram.ui.ManageLinksActivity r2 = org.telegram.ui.ManageLinksActivity.this
                boolean r2 = r2.canEdit
                if (r2 == 0) goto L_0x00a3
                r2 = 2131625288(0x7f0e0548, float:1.887778E38)
                java.lang.String r6 = "EditLink"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
                r8.add(r2)
                r2 = 2131165738(0x7var_a, float:1.7945702E38)
                java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                r0.add(r2)
                r2 = 2
                java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                r1.add(r2)
            L_0x00a3:
                org.telegram.ui.ManageLinksActivity r2 = org.telegram.ui.ManageLinksActivity.this
                boolean r2 = r2.canEdit
                if (r2 == 0) goto L_0x00c8
                r2 = 2131627312(0x7f0e0d30, float:1.8881885E38)
                java.lang.String r6 = "RevokeLink"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
                r8.add(r2)
                java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
                r0.add(r2)
                r2 = 3
                java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                r1.add(r2)
                goto L_0x003a
            L_0x00c8:
                r2 = 0
            L_0x00c9:
                org.telegram.ui.ActionBar.AlertDialog$Builder r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder
                org.telegram.ui.ManageLinksActivity r6 = org.telegram.ui.ManageLinksActivity.this
                android.app.Activity r6 = r6.getParentActivity()
                r3.<init>((android.content.Context) r6)
                java.lang.CharSequence[] r4 = new java.lang.CharSequence[r4]
                java.lang.Object[] r4 = r8.toArray(r4)
                java.lang.CharSequence[] r4 = (java.lang.CharSequence[]) r4
                int[] r0 = org.telegram.messenger.AndroidUtilities.toIntArray(r0)
                org.telegram.ui.-$$Lambda$ManageLinksActivity$LinkCell$6WOTWE2QlARDKBPxMERVDFywqN8 r6 = new org.telegram.ui.-$$Lambda$ManageLinksActivity$LinkCell$6WOTWE2QlARDKBPxMERVDFywqN8
                r6.<init>(r1)
                r3.setItems(r4, r0, r6)
                r0 = 2131625901(0x7f0e07ad, float:1.8879023E38)
                java.lang.String r1 = "InviteLink"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r3.setTitle(r0)
                org.telegram.ui.ActionBar.AlertDialog r0 = r3.create()
                r3.show()
                if (r2 == 0) goto L_0x0111
                int r8 = r8.size()
                int r8 = r8 - r5
                java.lang.String r1 = "dialogTextRed2"
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                java.lang.String r2 = "dialogRedIcon"
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r0.setItemColor(r8, r1, r2)
            L_0x0111:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ManageLinksActivity.LinkCell.lambda$new$3$ManageLinksActivity$LinkCell(android.view.View):void");
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$2 */
        public /* synthetic */ void lambda$new$2$ManageLinksActivity$LinkCell(ArrayList arrayList, DialogInterface dialogInterface, int i) {
            int intValue = ((Integer) arrayList.get(i)).intValue();
            if (intValue == 0) {
                try {
                    if (this.invite.link != null) {
                        ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.invite.link));
                        BulletinFactory.createCopyLinkBulletin((BaseFragment) ManageLinksActivity.this).show();
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (intValue == 1) {
                try {
                    if (this.invite.link != null) {
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("text/plain");
                        intent.putExtra("android.intent.extra.TEXT", this.invite.link);
                        ManageLinksActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteToGroupByLink", NUM)), 500);
                    }
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            } else if (intValue == 2) {
                ManageLinksActivity.this.editLink(this.invite);
            } else if (intValue == 3) {
                TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = this.invite;
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) ManageLinksActivity.this.getParentActivity());
                builder.setMessage(LocaleController.getString("RevokeAlert", NUM));
                builder.setTitle(LocaleController.getString("RevokeLink", NUM));
                builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new DialogInterface.OnClickListener(tLRPC$TL_chatInviteExported) {
                    public final /* synthetic */ TLRPC$TL_chatInviteExported f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ManageLinksActivity.LinkCell.this.lambda$new$0$ManageLinksActivity$LinkCell(this.f$1, dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                ManageLinksActivity.this.showDialog(builder.create());
            } else if (intValue == 4) {
                TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported2 = this.invite;
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) ManageLinksActivity.this.getParentActivity());
                builder2.setTitle(LocaleController.getString("DeleteLink", NUM));
                builder2.setMessage(LocaleController.getString("DeleteLinkHelp", NUM));
                builder2.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener(tLRPC$TL_chatInviteExported2) {
                    public final /* synthetic */ TLRPC$TL_chatInviteExported f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ManageLinksActivity.LinkCell.this.lambda$new$1$ManageLinksActivity$LinkCell(this.f$1, dialogInterface, i);
                    }
                });
                builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                ManageLinksActivity.this.showDialog(builder2.create());
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$ManageLinksActivity$LinkCell(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, DialogInterface dialogInterface, int i) {
            ManageLinksActivity.this.revokeLink(tLRPC$TL_chatInviteExported);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$1 */
        public /* synthetic */ void lambda$new$1$ManageLinksActivity$LinkCell(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, DialogInterface dialogInterface, int i) {
            ManageLinksActivity.this.deleteLink(tLRPC$TL_chatInviteExported);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), NUM));
            this.paint2.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:57:0x0105, code lost:
            if (r4.revoked == false) goto L_0x0107;
         */
        /* JADX WARNING: Removed duplicated region for block: B:78:0x01cc  */
        /* JADX WARNING: Removed duplicated region for block: B:79:0x01f1  */
        /* JADX WARNING: Removed duplicated region for block: B:82:0x0219  */
        /* JADX WARNING: Removed duplicated region for block: B:84:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r16) {
            /*
                r15 = this;
                r0 = r15
                r7 = r16
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r0.invite
                if (r1 != 0) goto L_0x0008
                return
            L_0x0008:
                r1 = 1108344832(0x42100000, float:36.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r1 = r15.getMeasuredHeight()
                int r9 = r1 / 2
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r0.invite
                boolean r2 = r1.expired
                r5 = 0
                r10 = 1
                r6 = 1065353216(0x3var_, float:1.0)
                if (r2 != 0) goto L_0x0083
                boolean r2 = r1.revoked
                if (r2 == 0) goto L_0x0023
                goto L_0x0083
            L_0x0023:
                int r2 = r1.expire_date
                if (r2 > 0) goto L_0x0031
                int r1 = r1.usage_limit
                if (r1 <= 0) goto L_0x002c
                goto L_0x0031
            L_0x002c:
                r1 = 1065353216(0x3var_, float:1.0)
                r2 = 0
                r3 = 0
                goto L_0x008d
            L_0x0031:
                if (r2 <= 0) goto L_0x0058
                long r1 = java.lang.System.currentTimeMillis()
                org.telegram.ui.ManageLinksActivity r11 = org.telegram.ui.ManageLinksActivity.this
                long r11 = r11.timeDif
                r13 = 1000(0x3e8, double:4.94E-321)
                long r11 = r11 * r13
                long r1 = r1 + r11
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r11 = r0.invite
                int r12 = r11.expire_date
                long r3 = (long) r12
                long r3 = r3 * r13
                int r12 = r11.start_date
                if (r12 > 0) goto L_0x004d
                int r12 = r11.date
            L_0x004d:
                long r11 = (long) r12
                long r11 = r11 * r13
                long r1 = r1 - r11
                long r3 = r3 - r11
                float r1 = (float) r1
                float r2 = (float) r3
                float r1 = r1 / r2
                float r1 = r6 - r1
                goto L_0x005a
            L_0x0058:
                r1 = 1065353216(0x3var_, float:1.0)
            L_0x005a:
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r2 = r0.invite
                int r3 = r2.usage_limit
                if (r3 <= 0) goto L_0x0068
                int r2 = r2.usage
                int r2 = r3 - r2
                float r2 = (float) r2
                float r3 = (float) r3
                float r2 = r2 / r3
                goto L_0x006a
            L_0x0068:
                r2 = 1065353216(0x3var_, float:1.0)
            L_0x006a:
                float r2 = java.lang.Math.min(r1, r2)
                int r3 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
                if (r3 > 0) goto L_0x0081
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r3 = r0.invite
                r3.expired = r10
                org.telegram.ui.ManageLinksActivity r3 = org.telegram.ui.ManageLinksActivity.this
                org.telegram.ui.Components.RecyclerListView r3 = r3.listView
                org.telegram.messenger.AndroidUtilities.updateVisibleRows(r3)
                r3 = 3
                goto L_0x008d
            L_0x0081:
                r3 = 1
                goto L_0x008d
            L_0x0083:
                boolean r1 = r1.revoked
                if (r1 == 0) goto L_0x0089
                r3 = 4
                goto L_0x008a
            L_0x0089:
                r3 = 3
            L_0x008a:
                r1 = 1065353216(0x3var_, float:1.0)
                r2 = 0
            L_0x008d:
                int r4 = r0.lastDrawingState
                if (r3 == r4) goto L_0x00a9
                if (r4 < 0) goto L_0x00a9
                r0.animateFromState = r4
                r0.animateToStateProgress = r5
                boolean r4 = r15.hasProgress(r4)
                if (r4 == 0) goto L_0x00a6
                boolean r4 = r15.hasProgress(r3)
                if (r4 != 0) goto L_0x00a6
                r0.animateHideExpiring = r10
                goto L_0x00a9
            L_0x00a6:
                r4 = 0
                r0.animateHideExpiring = r4
            L_0x00a9:
                r0.lastDrawingState = r3
                float r4 = r0.animateToStateProgress
                int r5 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r5 == 0) goto L_0x00c4
                r5 = 1032000111(0x3d83126f, float:0.064)
                float r4 = r4 + r5
                r0.animateToStateProgress = r4
                int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r4 < 0) goto L_0x00c1
                r0.animateToStateProgress = r6
                r4 = 0
                r0.animateHideExpiring = r4
                goto L_0x00c4
            L_0x00c1:
                r15.invalidate()
            L_0x00c4:
                float r4 = r0.animateToStateProgress
                int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r4 == 0) goto L_0x00db
                int r4 = r0.animateFromState
                int r4 = r15.getColor(r4, r2)
                int r2 = r15.getColor(r3, r2)
                float r3 = r0.animateToStateProgress
                int r2 = androidx.core.graphics.ColorUtils.blendARGB(r4, r2, r3)
                goto L_0x00df
            L_0x00db:
                int r2 = r15.getColor(r3, r2)
            L_0x00df:
                android.graphics.Paint r3 = r0.paint
                r3.setColor(r2)
                float r3 = (float) r8
                float r4 = (float) r9
                r5 = 1107296256(0x42000000, float:32.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r5
                r11 = 1073741824(0x40000000, float:2.0)
                float r5 = r5 / r11
                android.graphics.Paint r11 = r0.paint
                r7.drawCircle(r3, r4, r5, r11)
                boolean r3 = r0.animateHideExpiring
                if (r3 != 0) goto L_0x0107
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r4 = r0.invite
                boolean r5 = r4.expired
                if (r5 != 0) goto L_0x01c4
                int r5 = r4.expire_date
                if (r5 <= 0) goto L_0x01c4
                boolean r4 = r4.revoked
                if (r4 != 0) goto L_0x01c4
            L_0x0107:
                if (r3 == 0) goto L_0x010b
                float r1 = r0.lastDrawExpringProgress
            L_0x010b:
                r11 = r1
                android.graphics.Paint r1 = r0.paint2
                r1.setColor(r2)
                android.graphics.RectF r1 = r0.rectF
                r2 = 1101004800(0x41a00000, float:20.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r3 = r8 - r3
                float r3 = (float) r3
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r4 = r9 - r4
                float r4 = (float) r4
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r5 = r5 + r8
                float r5 = (float) r5
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = r2 + r9
                float r2 = (float) r2
                r1.set(r3, r4, r5, r2)
                float r1 = r0.animateToStateProgress
                r2 = 1135869952(0x43b40000, float:360.0)
                int r1 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
                if (r1 == 0) goto L_0x0197
                int r1 = r0.animateFromState
                boolean r1 = r15.hasProgress(r1)
                if (r1 == 0) goto L_0x0146
                boolean r1 = r0.animateHideExpiring
                if (r1 == 0) goto L_0x0197
            L_0x0146:
                r16.save()
                boolean r1 = r0.animateHideExpiring
                if (r1 == 0) goto L_0x0152
                float r1 = r0.animateToStateProgress
                float r6 = r6 - r1
                r12 = r6
                goto L_0x0155
            L_0x0152:
                float r1 = r0.animateToStateProgress
                r12 = r1
            L_0x0155:
                r3 = 4604480259023595110(0x3feNUM, double:0.7)
                r1 = 1050253722(0x3e99999a, float:0.3)
                float r1 = r1 * r12
                double r5 = (double) r1
                java.lang.Double.isNaN(r5)
                double r5 = r5 + r3
                float r1 = (float) r5
                android.graphics.RectF r3 = r0.rectF
                float r3 = r3.centerX()
                android.graphics.RectF r4 = r0.rectF
                float r4 = r4.centerY()
                r7.scale(r1, r1, r3, r4)
                android.graphics.RectF r3 = r0.rectF
                r4 = -1028390912(0xffffffffc2b40000, float:-90.0)
                float r1 = -r11
                float r13 = r1 * r2
                r5 = 0
                android.graphics.Paint r6 = r0.paint2
                r1 = r16
                r2 = r3
                r3 = r4
                r4 = r13
                r1.drawArc(r2, r3, r4, r5, r6)
                org.telegram.ui.Components.TimerParticles r1 = r0.timerParticles
                android.graphics.Paint r3 = r0.paint2
                android.graphics.RectF r4 = r0.rectF
                r2 = r16
                r5 = r13
                r6 = r12
                r1.draw(r2, r3, r4, r5, r6)
                r16.restore()
                goto L_0x01b7
            L_0x0197:
                android.graphics.RectF r3 = r0.rectF
                r4 = -1028390912(0xffffffffc2b40000, float:-90.0)
                float r1 = -r11
                float r12 = r1 * r2
                r5 = 0
                android.graphics.Paint r6 = r0.paint2
                r1 = r16
                r2 = r3
                r3 = r4
                r4 = r12
                r1.drawArc(r2, r3, r4, r5, r6)
                org.telegram.ui.Components.TimerParticles r1 = r0.timerParticles
                android.graphics.Paint r3 = r0.paint2
                android.graphics.RectF r4 = r0.rectF
                r6 = 1065353216(0x3var_, float:1.0)
                r2 = r16
                r5 = r12
                r1.draw(r2, r3, r4, r5, r6)
            L_0x01b7:
                org.telegram.ui.ManageLinksActivity r1 = org.telegram.ui.ManageLinksActivity.this
                boolean r1 = r1.isPaused
                if (r1 != 0) goto L_0x01c2
                r15.invalidate()
            L_0x01c2:
                r0.lastDrawExpringProgress = r11
            L_0x01c4:
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r0.invite
                boolean r1 = r1.revoked
                r2 = 1094713344(0x41400000, float:12.0)
                if (r1 == 0) goto L_0x01f1
                org.telegram.ui.ManageLinksActivity r1 = org.telegram.ui.ManageLinksActivity.this
                android.graphics.drawable.Drawable r1 = r1.linkIconRevoked
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r3 = r8 - r3
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r4 = r9 - r4
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r8 = r8 + r5
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r9 = r9 + r2
                r1.setBounds(r3, r4, r8, r9)
                org.telegram.ui.ManageLinksActivity r1 = org.telegram.ui.ManageLinksActivity.this
                android.graphics.drawable.Drawable r1 = r1.linkIconRevoked
                r1.draw(r7)
                goto L_0x0215
            L_0x01f1:
                org.telegram.ui.ManageLinksActivity r1 = org.telegram.ui.ManageLinksActivity.this
                android.graphics.drawable.Drawable r1 = r1.linkIcon
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r3 = r8 - r3
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r4 = r9 - r4
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r8 = r8 + r5
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r9 = r9 + r2
                r1.setBounds(r3, r4, r8, r9)
                org.telegram.ui.ManageLinksActivity r1 = org.telegram.ui.ManageLinksActivity.this
                android.graphics.drawable.Drawable r1 = r1.linkIcon
                r1.draw(r7)
            L_0x0215:
                boolean r1 = r0.drawDivider
                if (r1 == 0) goto L_0x023e
                r1 = 1116471296(0x428CLASSNAME, float:70.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r2 = (float) r1
                int r1 = r15.getMeasuredHeight()
                int r1 = r1 - r10
                float r3 = (float) r1
                int r1 = r15.getMeasuredWidth()
                r4 = 1102577664(0x41b80000, float:23.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r1 = r1 + r4
                float r4 = (float) r1
                int r1 = r15.getMeasuredHeight()
                float r5 = (float) r1
                android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dividerPaint
                r1 = r16
                r1.drawLine(r2, r3, r4, r5, r6)
            L_0x023e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ManageLinksActivity.LinkCell.onDraw(android.graphics.Canvas):void");
        }

        private int getColor(int i, float f) {
            if (i == 3) {
                return Theme.getColor("chat_attachAudioBackground");
            }
            if (i == 1) {
                if (f > 0.5f) {
                    return ColorUtils.blendARGB(Theme.getColor("chat_attachLocationBackground"), Theme.getColor("chat_attachPollBackground"), 1.0f - ((f - 0.5f) / 0.5f));
                }
                return ColorUtils.blendARGB(Theme.getColor("chat_attachPollBackground"), Theme.getColor("chat_attachAudioBackground"), 1.0f - (f / 0.5f));
            } else if (i == 2) {
                return Theme.getColor("chat_attachPollBackground");
            } else {
                if (i == 4) {
                    return Theme.getColor("chats_unreadCounterMuted");
                }
                return Theme.getColor("featuredStickers_addButton");
            }
        }

        public void setLink(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, int i) {
            String str;
            String str2;
            int i2;
            int i3;
            this.timerRunning = false;
            TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported2 = this.invite;
            if (tLRPC$TL_chatInviteExported2 == null || tLRPC$TL_chatInviteExported == null || !tLRPC$TL_chatInviteExported2.link.equals(tLRPC$TL_chatInviteExported.link)) {
                this.lastDrawingState = -1;
                this.animateToStateProgress = 1.0f;
            }
            this.invite = tLRPC$TL_chatInviteExported;
            this.position = i;
            if (tLRPC$TL_chatInviteExported != null) {
                if (tLRPC$TL_chatInviteExported.link.startsWith("https://t.me/+")) {
                    this.titleView.setText(tLRPC$TL_chatInviteExported.link.substring(14));
                } else if (tLRPC$TL_chatInviteExported.link.startsWith("https://t.me/joinchat/")) {
                    this.titleView.setText(tLRPC$TL_chatInviteExported.link.substring(22));
                } else if (tLRPC$TL_chatInviteExported.link.startsWith("https://")) {
                    this.titleView.setText(tLRPC$TL_chatInviteExported.link.substring(8));
                } else {
                    this.titleView.setText(tLRPC$TL_chatInviteExported.link);
                }
                int i4 = tLRPC$TL_chatInviteExported.usage;
                if (i4 == 0 && tLRPC$TL_chatInviteExported.usage_limit == 0) {
                    str = LocaleController.getString("NoOneJoinedYet", NUM);
                } else {
                    int i5 = tLRPC$TL_chatInviteExported.usage_limit;
                    if (i5 > 0 && i4 == 0 && !tLRPC$TL_chatInviteExported.expired && !tLRPC$TL_chatInviteExported.revoked) {
                        str = LocaleController.formatPluralString("CanJoin", i5);
                    } else if (i5 <= 0 || !tLRPC$TL_chatInviteExported.expired || !tLRPC$TL_chatInviteExported.revoked) {
                        str = LocaleController.formatPluralString("PeopleJoined", i4);
                    } else {
                        str = LocaleController.formatPluralString("PeopleJoined", tLRPC$TL_chatInviteExported.usage) + ", " + LocaleController.formatPluralString("PeopleJoinedRemaining", tLRPC$TL_chatInviteExported.usage_limit - tLRPC$TL_chatInviteExported.usage);
                    }
                }
                if (tLRPC$TL_chatInviteExported.permanent && !tLRPC$TL_chatInviteExported.revoked) {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
                    DotDividerSpan dotDividerSpan = new DotDividerSpan();
                    dotDividerSpan.setTopPadding(AndroidUtilities.dp(1.5f));
                    spannableStringBuilder.append("  .  ").setSpan(dotDividerSpan, spannableStringBuilder.length() - 3, spannableStringBuilder.length() - 2, 0);
                    spannableStringBuilder.append(LocaleController.getString("Permanent", NUM));
                    this.subtitleView.setText(spannableStringBuilder);
                } else if (tLRPC$TL_chatInviteExported.expired || tLRPC$TL_chatInviteExported.revoked) {
                    if (tLRPC$TL_chatInviteExported.revoked && tLRPC$TL_chatInviteExported.usage == 0) {
                        str = LocaleController.getString("NoOneJoined", NUM);
                    }
                    SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(str);
                    DotDividerSpan dotDividerSpan2 = new DotDividerSpan();
                    dotDividerSpan2.setTopPadding(AndroidUtilities.dp(1.5f));
                    spannableStringBuilder2.append("  .  ").setSpan(dotDividerSpan2, spannableStringBuilder2.length() - 3, spannableStringBuilder2.length() - 2, 0);
                    boolean z = tLRPC$TL_chatInviteExported.revoked;
                    if (z || (i3 = tLRPC$TL_chatInviteExported.usage_limit) <= 0 || tLRPC$TL_chatInviteExported.usage < i3) {
                        if (z) {
                            i2 = NUM;
                            str2 = "Revoked";
                        } else {
                            i2 = NUM;
                            str2 = "Expired";
                        }
                        spannableStringBuilder2.append(LocaleController.getString(str2, i2));
                    } else {
                        spannableStringBuilder2.append(LocaleController.getString("LinkLimitReached", NUM));
                    }
                    this.subtitleView.setText(spannableStringBuilder2);
                } else if (tLRPC$TL_chatInviteExported.expire_date > 0) {
                    SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder(str);
                    DotDividerSpan dotDividerSpan3 = new DotDividerSpan();
                    dotDividerSpan3.setTopPadding(AndroidUtilities.dp(1.5f));
                    spannableStringBuilder3.append("  .  ").setSpan(dotDividerSpan3, spannableStringBuilder3.length() - 3, spannableStringBuilder3.length() - 2, 0);
                    long currentTimeMillis = (((long) tLRPC$TL_chatInviteExported.expire_date) * 1000) - (System.currentTimeMillis() + (ManageLinksActivity.this.timeDif * 1000));
                    if (currentTimeMillis < 0) {
                        currentTimeMillis = 0;
                    }
                    if (currentTimeMillis > 86400000) {
                        spannableStringBuilder3.append(LocaleController.formatPluralString("DaysLeft", (int) (currentTimeMillis / 86400000)));
                    } else {
                        long j = currentTimeMillis / 1000;
                        int i6 = (int) (j % 60);
                        long j2 = j / 60;
                        int i7 = (int) (j2 / 60);
                        Locale locale = Locale.ENGLISH;
                        spannableStringBuilder3.append(String.format(locale, "%02d", new Object[]{Integer.valueOf(i7)})).append(String.format(locale, ":%02d", new Object[]{Integer.valueOf((int) (j2 % 60))})).append(String.format(locale, ":%02d", new Object[]{Integer.valueOf(i6)}));
                        this.timerRunning = true;
                    }
                    this.subtitleView.setText(spannableStringBuilder3);
                } else {
                    this.subtitleView.setText(str);
                }
            }
        }
    }

    public void deleteLink(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
        TLRPC$TL_messages_deleteExportedChatInvite tLRPC$TL_messages_deleteExportedChatInvite = new TLRPC$TL_messages_deleteExportedChatInvite();
        tLRPC$TL_messages_deleteExportedChatInvite.link = tLRPC$TL_chatInviteExported.link;
        tLRPC$TL_messages_deleteExportedChatInvite.peer = getMessagesController().getInputPeer(-this.currentChatId);
        getConnectionsManager().sendRequest(tLRPC$TL_messages_deleteExportedChatInvite, new RequestDelegate(tLRPC$TL_chatInviteExported) {
            public final /* synthetic */ TLRPC$TL_chatInviteExported f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ManageLinksActivity.this.lambda$deleteLink$14$ManageLinksActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$deleteLink$14 */
    public /* synthetic */ void lambda$deleteLink$14$ManageLinksActivity(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLRPC$TL_chatInviteExported) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLRPC$TL_chatInviteExported f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ManageLinksActivity.this.lambda$deleteLink$13$ManageLinksActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$deleteLink$13 */
    public /* synthetic */ void lambda$deleteLink$13$ManageLinksActivity(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
        if (tLRPC$TL_error == null) {
            this.linkEditActivityCallback.onLinkRemoved(tLRPC$TL_chatInviteExported);
        }
    }

    public void editLink(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
        LinkEditActivity linkEditActivity = new LinkEditActivity(1, this.currentChatId);
        linkEditActivity.setCallback(this.linkEditActivityCallback);
        linkEditActivity.setInviteToEdit(tLRPC$TL_chatInviteExported);
        presentFragment(linkEditActivity);
    }

    public void revokeLink(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
        TLRPC$TL_messages_editExportedChatInvite tLRPC$TL_messages_editExportedChatInvite = new TLRPC$TL_messages_editExportedChatInvite();
        tLRPC$TL_messages_editExportedChatInvite.link = tLRPC$TL_chatInviteExported.link;
        tLRPC$TL_messages_editExportedChatInvite.revoked = true;
        tLRPC$TL_messages_editExportedChatInvite.peer = getMessagesController().getInputPeer(-this.currentChatId);
        getConnectionsManager().sendRequest(tLRPC$TL_messages_editExportedChatInvite, new RequestDelegate(tLRPC$TL_chatInviteExported) {
            public final /* synthetic */ TLRPC$TL_chatInviteExported f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ManageLinksActivity.this.lambda$revokeLink$16$ManageLinksActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$revokeLink$16 */
    public /* synthetic */ void lambda$revokeLink$16$ManageLinksActivity(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, tLRPC$TL_chatInviteExported) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ TLRPC$TL_chatInviteExported f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ManageLinksActivity.this.lambda$revokeLink$15$ManageLinksActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$revokeLink$15 */
    public /* synthetic */ void lambda$revokeLink$15$ManageLinksActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
        if (tLRPC$TL_error == null) {
            if (tLObject instanceof TLRPC$TL_messages_exportedChatInviteReplaced) {
                TLRPC$TL_messages_exportedChatInviteReplaced tLRPC$TL_messages_exportedChatInviteReplaced = (TLRPC$TL_messages_exportedChatInviteReplaced) tLObject;
                if (!this.isPublic) {
                    this.invite = (TLRPC$TL_chatInviteExported) tLRPC$TL_messages_exportedChatInviteReplaced.new_invite;
                }
                tLRPC$TL_chatInviteExported.revoked = true;
                DiffCallback saveListState = saveListState();
                if (this.isPublic && this.adminId == getAccountInstance().getUserConfig().getClientUserId()) {
                    this.invites.remove(tLRPC$TL_chatInviteExported);
                    this.invites.add(0, (TLRPC$TL_chatInviteExported) tLRPC$TL_messages_exportedChatInviteReplaced.new_invite);
                } else if (this.invite != null) {
                    this.invite = (TLRPC$TL_chatInviteExported) tLRPC$TL_messages_exportedChatInviteReplaced.new_invite;
                }
                this.revokedInvites.add(0, tLRPC$TL_chatInviteExported);
                updateRecyclerViewAnimated(saveListState);
            } else {
                this.linkEditActivityCallback.onLinkEdited(tLRPC$TL_chatInviteExported, tLObject);
                TLRPC$ChatFull tLRPC$ChatFull = this.info;
                if (tLRPC$ChatFull != null) {
                    int i = tLRPC$ChatFull.invitesCount - 1;
                    tLRPC$ChatFull.invitesCount = i;
                    if (i < 0) {
                        tLRPC$ChatFull.invitesCount = 0;
                    }
                    getMessagesStorage().saveChatLinksCount(this.currentChatId, this.info.invitesCount);
                }
            }
            if (getParentActivity() != null) {
                BulletinFactory.of((BaseFragment) this).createSimpleBulletin(NUM, LocaleController.getString("InviteRevokedHint", NUM)).show();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateRecyclerViewAnimated(DiffCallback diffCallback) {
        if (this.isPaused || this.listViewAdapter == null || this.listView == null) {
            updateRows(true);
            return;
        }
        updateRows(false);
        diffCallback.fillPositions(diffCallback.newPositionToItem);
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo((RecyclerView.Adapter) this.listViewAdapter);
        AndroidUtilities.updateVisibleRows(this.listView);
    }

    private class DiffCallback extends DiffUtil.Callback {
        SparseIntArray newPositionToItem;
        int oldAdminsEndRow;
        int oldAdminsStartRow;
        ArrayList<TLRPC$TL_chatInviteExported> oldLinks;
        int oldLinksEndRow;
        int oldLinksStartRow;
        SparseIntArray oldPositionToItem;
        ArrayList<TLRPC$TL_chatInviteExported> oldRevokedLinks;
        int oldRevokedLinksEndRow;
        int oldRevokedLinksStartRow;
        int oldRowCount;

        private DiffCallback() {
            this.oldPositionToItem = new SparseIntArray();
            this.newPositionToItem = new SparseIntArray();
            this.oldLinks = new ArrayList<>();
            this.oldRevokedLinks = new ArrayList<>();
        }

        public int getOldListSize() {
            return this.oldRowCount;
        }

        public int getNewListSize() {
            return ManageLinksActivity.this.rowCount;
        }

        public boolean areItemsTheSame(int i, int i2) {
            TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported;
            TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported2;
            if (((i >= this.oldLinksStartRow && i < this.oldLinksEndRow) || (i >= this.oldRevokedLinksStartRow && i < this.oldRevokedLinksEndRow)) && ((i2 >= ManageLinksActivity.this.linksStartRow && i2 < ManageLinksActivity.this.linksEndRow) || (i2 >= ManageLinksActivity.this.revokedLinksStartRow && i2 < ManageLinksActivity.this.revokedLinksEndRow))) {
                if (i2 < ManageLinksActivity.this.linksStartRow || i2 >= ManageLinksActivity.this.linksEndRow) {
                    tLRPC$TL_chatInviteExported = (TLRPC$TL_chatInviteExported) ManageLinksActivity.this.revokedInvites.get(i2 - ManageLinksActivity.this.revokedLinksStartRow);
                } else {
                    tLRPC$TL_chatInviteExported = (TLRPC$TL_chatInviteExported) ManageLinksActivity.this.invites.get(i2 - ManageLinksActivity.this.linksStartRow);
                }
                int i3 = this.oldLinksStartRow;
                if (i < i3 || i >= this.oldLinksEndRow) {
                    tLRPC$TL_chatInviteExported2 = this.oldRevokedLinks.get(i - this.oldRevokedLinksStartRow);
                } else {
                    tLRPC$TL_chatInviteExported2 = this.oldLinks.get(i - i3);
                }
                return tLRPC$TL_chatInviteExported2.link.equals(tLRPC$TL_chatInviteExported.link);
            } else if (i < this.oldAdminsStartRow || i >= this.oldAdminsEndRow || i2 < ManageLinksActivity.this.adminsStartRow || i2 >= ManageLinksActivity.this.adminsEndRow) {
                int i4 = this.oldPositionToItem.get(i, -1);
                int i5 = this.newPositionToItem.get(i2, -1);
                if (i4 < 0 || i4 != i5) {
                    return false;
                }
                return true;
            } else if (i - this.oldAdminsStartRow == i2 - ManageLinksActivity.this.adminsStartRow) {
                return true;
            } else {
                return false;
            }
        }

        public boolean areContentsTheSame(int i, int i2) {
            return areItemsTheSame(i, i2);
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            put(1, ManageLinksActivity.this.helpRow, sparseIntArray);
            put(2, ManageLinksActivity.this.permanentLinkHeaderRow, sparseIntArray);
            put(3, ManageLinksActivity.this.permanentLinkRow, sparseIntArray);
            put(4, ManageLinksActivity.this.dividerRow, sparseIntArray);
            put(5, ManageLinksActivity.this.createNewLinkRow, sparseIntArray);
            put(6, ManageLinksActivity.this.revokedHeader, sparseIntArray);
            put(7, ManageLinksActivity.this.revokeAllRow, sparseIntArray);
            put(8, ManageLinksActivity.this.createLinkHelpRow, sparseIntArray);
            put(9, ManageLinksActivity.this.creatorRow, sparseIntArray);
            put(10, ManageLinksActivity.this.creatorDividerRow, sparseIntArray);
            put(11, ManageLinksActivity.this.adminsHeaderRow, sparseIntArray);
            put(12, ManageLinksActivity.this.linksHeaderRow, sparseIntArray);
            put(13, ManageLinksActivity.this.linksLoadingRow, sparseIntArray);
        }

        private void put(int i, int i2, SparseIntArray sparseIntArray) {
            if (i2 >= 0) {
                sparseIntArray.put(i2, i);
            }
        }
    }

    /* access modifiers changed from: private */
    public DiffCallback saveListState() {
        DiffCallback diffCallback = new DiffCallback();
        diffCallback.fillPositions(diffCallback.oldPositionToItem);
        diffCallback.oldLinksStartRow = this.linksStartRow;
        diffCallback.oldLinksEndRow = this.linksEndRow;
        diffCallback.oldRevokedLinksStartRow = this.revokedLinksStartRow;
        diffCallback.oldRevokedLinksEndRow = this.revokedLinksEndRow;
        diffCallback.oldAdminsStartRow = this.adminsStartRow;
        diffCallback.oldAdminsEndRow = this.adminsEndRow;
        diffCallback.oldRowCount = this.rowCount;
        diffCallback.oldLinks.clear();
        diffCallback.oldLinks.addAll(this.invites);
        diffCallback.oldRevokedLinks.clear();
        diffCallback.oldRevokedLinks.addAll(this.revokedInvites);
        return diffCallback;
    }

    public void fixDate(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
        boolean z = true;
        if (tLRPC$TL_chatInviteExported.expire_date > 0) {
            if (getConnectionsManager().getCurrentTime() < tLRPC$TL_chatInviteExported.expire_date) {
                z = false;
            }
            tLRPC$TL_chatInviteExported.expired = z;
            return;
        }
        int i = tLRPC$TL_chatInviteExported.usage_limit;
        if (i > 0) {
            if (tLRPC$TL_chatInviteExported.usage < i) {
                z = false;
            }
            tLRPC$TL_chatInviteExported.expired = z;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$ManageLinksActivity$qcjO18qpwUV4GqkS6UuogTVHJg r11 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ManageLinksActivity.this.lambda$getThemeDescriptions$17$ManageLinksActivity();
            }
        };
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, LinkActionView.class, LinkCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        $$Lambda$ManageLinksActivity$qcjO18qpwUV4GqkS6UuogTVHJg r9 = r11;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        $$Lambda$ManageLinksActivity$qcjO18qpwUV4GqkS6UuogTVHJg r8 = r11;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterMuted"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LinkCell.class}, new String[]{"titleView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LinkCell.class}, new String[]{"subtitleView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{LinkCell.class}, new String[]{"optionsView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menu"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getThemeDescriptions$17 */
    public /* synthetic */ void lambda$getThemeDescriptions$17$ManageLinksActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) childAt).update(0);
                }
                if (childAt instanceof LinkActionView) {
                    ((LinkActionView) childAt).updateColors();
                }
            }
        }
        InviteLinkBottomSheet inviteLinkBottomSheet2 = this.inviteLinkBottomSheet;
        if (inviteLinkBottomSheet2 != null) {
            inviteLinkBottomSheet2.updateColors();
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        super.onTransitionAnimationEnd(z, z2);
        if (z) {
            this.isOpened = true;
        }
        NotificationCenter.getInstance(this.currentAccount).onAnimationFinish(this.animationIndex);
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        super.onTransitionAnimationStart(z, z2);
        this.animationIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.animationIndex, (int[]) null);
    }
}
