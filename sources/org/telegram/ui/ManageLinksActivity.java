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
import android.view.ViewGroup;
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
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.DotDividerSpan;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.InviteLinkBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkActionView;
import org.telegram.ui.Components.RecyclerItemsEnterAnimator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TimerParticles;
import org.telegram.ui.LinkEditActivity;

public class ManageLinksActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public long adminId;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_chatAdminWithInvites> admins = new ArrayList<>();
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
    public TLRPC.Chat currentChat;
    /* access modifiers changed from: private */
    public long currentChatId;
    boolean deletingRevokedLinks;
    /* access modifiers changed from: private */
    public int dividerRow;
    boolean hasMore;
    /* access modifiers changed from: private */
    public int helpRow;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull info;
    /* access modifiers changed from: private */
    public TLRPC.TL_chatInviteExported invite;
    /* access modifiers changed from: private */
    public InviteLinkBottomSheet inviteLinkBottomSheet;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_chatInviteExported> invites = new ArrayList<>();
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
    private RecyclerItemsEnterAnimator recyclerItemsEnterAnimator;
    /* access modifiers changed from: private */
    public int revokeAllDivider;
    /* access modifiers changed from: private */
    public int revokeAllRow;
    /* access modifiers changed from: private */
    public int revokedDivider;
    /* access modifiers changed from: private */
    public int revokedHeader;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_chatInviteExported> revokedInvites = new ArrayList<>();
    /* access modifiers changed from: private */
    public int revokedLinksEndRow;
    /* access modifiers changed from: private */
    public int revokedLinksStartRow;
    /* access modifiers changed from: private */
    public int rowCount;
    long timeDif;
    private boolean transitionFinished;
    Runnable updateTimerRunnable = new Runnable() {
        public void run() {
            if (ManageLinksActivity.this.listView != null) {
                for (int i = 0; i < ManageLinksActivity.this.listView.getChildCount(); i++) {
                    View child = ManageLinksActivity.this.listView.getChildAt(i);
                    if (child instanceof LinkCell) {
                        LinkCell linkCell = (LinkCell) child;
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
    public HashMap<Long, TLRPC.User> users = new HashMap<>();

    private static class EmptyView extends LinearLayout implements NotificationCenter.NotificationCenterDelegate {
        private static final String stickerSetName = "tg_placeholders_android";
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
            TLRPC.TL_messages_stickerSet set = MediaDataController.getInstance(this.currentAccount).getStickerSetByName("tg_placeholders_android");
            if (set == null) {
                set = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName("tg_placeholders_android");
            }
            if (set == null || set.documents.size() < 4) {
                MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("tg_placeholders_android", false, set == null);
                return;
            }
            TLRPC.Document document = (TLRPC.Document) set.documents.get(3);
            this.stickerView.setImage(ImageLocation.getForDocument(document), "104_104", "tgs", (Drawable) DocumentObject.getSvgThumb(document, "windowBackgroundGray", 1.0f), (Object) set);
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

        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.diceStickersDidLoad && "tg_placeholders_android".equals(args[0])) {
                setSticker();
            }
        }
    }

    public ManageLinksActivity(long chatId, long adminId2, int invitesCount2) {
        boolean z = false;
        this.loadRevoked = false;
        this.linkEditActivityCallback = new LinkEditActivity.Callback() {
            public void onLinkCreated(TLObject response) {
                if (response instanceof TLRPC.TL_chatInviteExported) {
                    AndroidUtilities.runOnUIThread(new ManageLinksActivity$6$$ExternalSyntheticLambda0(this, response), 200);
                }
            }

            /* renamed from: lambda$onLinkCreated$0$org-telegram-ui-ManageLinksActivity$6  reason: not valid java name */
            public /* synthetic */ void m3288lambda$onLinkCreated$0$orgtelegramuiManageLinksActivity$6(TLObject response) {
                DiffCallback callback = ManageLinksActivity.this.saveListState();
                ManageLinksActivity.this.invites.add(0, (TLRPC.TL_chatInviteExported) response);
                if (ManageLinksActivity.this.info != null) {
                    ManageLinksActivity.this.info.invitesCount++;
                    ManageLinksActivity.this.getMessagesStorage().saveChatLinksCount(ManageLinksActivity.this.currentChatId, ManageLinksActivity.this.info.invitesCount);
                }
                ManageLinksActivity.this.updateRecyclerViewAnimated(callback);
            }

            public void onLinkEdited(TLRPC.TL_chatInviteExported inviteToEdit, TLObject response) {
                if (response instanceof TLRPC.TL_messages_exportedChatInvite) {
                    TLRPC.TL_chatInviteExported edited = (TLRPC.TL_chatInviteExported) ((TLRPC.TL_messages_exportedChatInvite) response).invite;
                    ManageLinksActivity.this.fixDate(edited);
                    int i = 0;
                    while (i < ManageLinksActivity.this.invites.size()) {
                        if (!((TLRPC.TL_chatInviteExported) ManageLinksActivity.this.invites.get(i)).link.equals(inviteToEdit.link)) {
                            i++;
                        } else if (edited.revoked) {
                            DiffCallback callback = ManageLinksActivity.this.saveListState();
                            ManageLinksActivity.this.invites.remove(i);
                            ManageLinksActivity.this.revokedInvites.add(0, edited);
                            ManageLinksActivity.this.updateRecyclerViewAnimated(callback);
                            return;
                        } else {
                            ManageLinksActivity.this.invites.set(i, edited);
                            ManageLinksActivity.this.updateRows(true);
                            return;
                        }
                    }
                }
            }

            public void onLinkRemoved(TLRPC.TL_chatInviteExported removedInvite) {
                for (int i = 0; i < ManageLinksActivity.this.revokedInvites.size(); i++) {
                    if (((TLRPC.TL_chatInviteExported) ManageLinksActivity.this.revokedInvites.get(i)).link.equals(removedInvite.link)) {
                        DiffCallback callback = ManageLinksActivity.this.saveListState();
                        ManageLinksActivity.this.revokedInvites.remove(i);
                        ManageLinksActivity.this.updateRecyclerViewAnimated(callback);
                        return;
                    }
                }
            }

            public void revokeLink(TLRPC.TL_chatInviteExported inviteFinal) {
                ManageLinksActivity.this.revokeLink(inviteFinal);
            }
        };
        this.animationIndex = -1;
        this.currentChatId = chatId;
        this.invitesCount = invitesCount2;
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(chatId));
        this.currentChat = chat;
        this.isChannel = ChatObject.isChannel(chat) && !this.currentChat.megagroup;
        if (adminId2 == 0) {
            this.adminId = getAccountInstance().getUserConfig().clientUserId;
        } else {
            this.adminId = adminId2;
        }
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.adminId));
        if (this.adminId == getAccountInstance().getUserConfig().clientUserId || (user != null && !user.bot)) {
            z = true;
        }
        this.canEdit = z;
    }

    /* access modifiers changed from: private */
    public void loadLinks(boolean notify) {
        if (!this.loadAdmins || this.adminsLoaded) {
            TLRPC.TL_messages_getExportedChatInvites req = new TLRPC.TL_messages_getExportedChatInvites();
            req.peer = getMessagesController().getInputPeer(-this.currentChatId);
            if (this.adminId == getUserConfig().getClientUserId()) {
                req.admin_id = getMessagesController().getInputUser(getUserConfig().getCurrentUser());
            } else {
                req.admin_id = getMessagesController().getInputUser(this.adminId);
            }
            boolean revoked = this.loadRevoked;
            if (this.loadRevoked) {
                req.revoked = true;
                if (!this.revokedInvites.isEmpty()) {
                    req.flags |= 4;
                    ArrayList<TLRPC.TL_chatInviteExported> arrayList = this.revokedInvites;
                    req.offset_link = arrayList.get(arrayList.size() - 1).link;
                    ArrayList<TLRPC.TL_chatInviteExported> arrayList2 = this.revokedInvites;
                    req.offset_date = arrayList2.get(arrayList2.size() - 1).date;
                }
            } else if (!this.invites.isEmpty()) {
                req.flags |= 4;
                ArrayList<TLRPC.TL_chatInviteExported> arrayList3 = this.invites;
                req.offset_link = arrayList3.get(arrayList3.size() - 1).link;
                ArrayList<TLRPC.TL_chatInviteExported> arrayList4 = this.invites;
                req.offset_date = arrayList4.get(arrayList4.size() - 1).date;
            }
            this.linksLoading = true;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new ManageLinksActivity$$ExternalSyntheticLambda5(this, this.isPublic ? null : this.invite, revoked)), getClassGuid());
        } else {
            this.linksLoading = true;
            TLRPC.TL_messages_getAdminsWithInvites req2 = new TLRPC.TL_messages_getAdminsWithInvites();
            req2.peer = getMessagesController().getInputPeer(-this.currentChatId);
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req2, new ManageLinksActivity$$ExternalSyntheticLambda1(this)), getClassGuid());
        }
        if (notify) {
            updateRows(true);
        }
    }

    /* renamed from: lambda$loadLinks$1$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    public /* synthetic */ void m3279lambda$loadLinks$1$orgtelegramuiManageLinksActivity(TLRPC.TL_error error, TLObject response) {
        getNotificationCenter().doOnIdle(new ManageLinksActivity$$ExternalSyntheticLambda12(this, error, response));
    }

    /* renamed from: lambda$loadLinks$2$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    public /* synthetic */ void m3280lambda$loadLinks$2$orgtelegramuiManageLinksActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ManageLinksActivity$$ExternalSyntheticLambda13(this, error, response));
    }

    /* renamed from: lambda$loadLinks$0$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    public /* synthetic */ void m3278lambda$loadLinks$0$orgtelegramuiManageLinksActivity(TLRPC.TL_error error, TLObject response) {
        this.linksLoading = false;
        if (error == null) {
            TLRPC.TL_messages_chatAdminsWithInvites adminsWithInvites = (TLRPC.TL_messages_chatAdminsWithInvites) response;
            for (int i = 0; i < adminsWithInvites.admins.size(); i++) {
                TLRPC.TL_chatAdminWithInvites admin = adminsWithInvites.admins.get(i);
                if (admin.admin_id != getAccountInstance().getUserConfig().clientUserId) {
                    this.admins.add(admin);
                }
            }
            for (int i2 = 0; i2 < adminsWithInvites.users.size(); i2++) {
                TLRPC.User user = adminsWithInvites.users.get(i2);
                this.users.put(Long.valueOf(user.id), user);
            }
        }
        int oldRowsCount = this.rowCount;
        this.adminsLoaded = true;
        this.hasMore = false;
        if (this.admins.size() > 0 && this.recyclerItemsEnterAnimator != null && !this.isPaused && this.isOpened) {
            this.recyclerItemsEnterAnimator.showItemsAnimated(oldRowsCount + 1);
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v9, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: org.telegram.tgnet.TLRPC$TL_chatInviteExported} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$loadLinks$5$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3283lambda$loadLinks$5$orgtelegramuiManageLinksActivity(org.telegram.tgnet.TLRPC.TL_chatInviteExported r10, boolean r11, org.telegram.tgnet.TLObject r12, org.telegram.tgnet.TLRPC.TL_error r13) {
        /*
            r9 = this;
            r0 = 0
            if (r13 != 0) goto L_0x0038
            r1 = r12
            org.telegram.tgnet.TLRPC$TL_messages_exportedChatInvites r1 = (org.telegram.tgnet.TLRPC.TL_messages_exportedChatInvites) r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ExportedChatInvite> r2 = r1.invites
            int r2 = r2.size()
            if (r2 <= 0) goto L_0x0038
            if (r10 == 0) goto L_0x0038
            r2 = 0
        L_0x0011:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ExportedChatInvite> r3 = r1.invites
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0038
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ExportedChatInvite> r3 = r1.invites
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r3 = (org.telegram.tgnet.TLRPC.TL_chatInviteExported) r3
            java.lang.String r3 = r3.link
            java.lang.String r4 = r10.link
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x0035
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ExportedChatInvite> r3 = r1.invites
            java.lang.Object r3 = r3.remove(r2)
            r0 = r3
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r0 = (org.telegram.tgnet.TLRPC.TL_chatInviteExported) r0
            goto L_0x0038
        L_0x0035:
            int r2 = r2 + 1
            goto L_0x0011
        L_0x0038:
            r5 = r0
            org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda10 r1 = new org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda10
            r3 = r1
            r4 = r9
            r6 = r13
            r7 = r12
            r8 = r11
            r3.<init>(r4, r5, r6, r7, r8)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ManageLinksActivity.m3283lambda$loadLinks$5$orgtelegramuiManageLinksActivity(org.telegram.tgnet.TLRPC$TL_chatInviteExported, boolean, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* renamed from: lambda$loadLinks$4$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    public /* synthetic */ void m3282lambda$loadLinks$4$orgtelegramuiManageLinksActivity(TLRPC.TL_chatInviteExported finalPermanentLink, TLRPC.TL_error error, TLObject response, boolean revoked) {
        getNotificationCenter().doOnIdle(new ManageLinksActivity$$ExternalSyntheticLambda9(this, finalPermanentLink, error, response, revoked));
    }

    /* renamed from: lambda$loadLinks$3$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    public /* synthetic */ void m3281lambda$loadLinks$3$orgtelegramuiManageLinksActivity(TLRPC.TL_chatInviteExported finalPermanentLink, TLRPC.TL_error error, TLObject response, boolean revoked) {
        DiffCallback callback = saveListState();
        this.linksLoading = false;
        this.hasMore = false;
        if (finalPermanentLink != null) {
            this.invite = finalPermanentLink;
            TLRPC.ChatFull chatFull = this.info;
            if (chatFull != null) {
                chatFull.exported_invite = finalPermanentLink;
            }
        }
        boolean updateByDiffUtils = false;
        if (error == null) {
            TLRPC.TL_messages_exportedChatInvites invites2 = (TLRPC.TL_messages_exportedChatInvites) response;
            if (revoked) {
                for (int i = 0; i < invites2.invites.size(); i++) {
                    TLRPC.TL_chatInviteExported in = (TLRPC.TL_chatInviteExported) invites2.invites.get(i);
                    fixDate(in);
                    this.revokedInvites.add(in);
                }
            } else {
                if (this.adminId != getAccountInstance().getUserConfig().clientUserId && this.invites.size() == 0 && invites2.invites.size() > 0) {
                    this.invite = (TLRPC.TL_chatInviteExported) invites2.invites.get(0);
                    invites2.invites.remove(0);
                }
                for (int i2 = 0; i2 < invites2.invites.size(); i2++) {
                    TLRPC.TL_chatInviteExported in2 = (TLRPC.TL_chatInviteExported) invites2.invites.get(i2);
                    fixDate(in2);
                    this.invites.add(in2);
                }
            }
            for (int i3 = 0; i3 < invites2.users.size(); i3++) {
                this.users.put(Long.valueOf(invites2.users.get(i3).id), invites2.users.get(i3));
            }
            int oldRowsCount = this.rowCount;
            if (invites2.invites.size() == 0) {
                this.hasMore = false;
            } else if (revoked) {
                this.hasMore = this.revokedInvites.size() + 1 < invites2.count;
            } else {
                this.hasMore = this.invites.size() + 1 < invites2.count;
            }
            if (invites2.invites.size() <= 0 || !this.isOpened) {
                updateByDiffUtils = true;
            } else if (this.recyclerItemsEnterAnimator != null && !this.isPaused) {
                this.recyclerItemsEnterAnimator.showItemsAnimated(oldRowsCount + 1);
            }
            TLRPC.ChatFull chatFull2 = this.info;
            if (chatFull2 != null && !revoked) {
                chatFull2.invitesCount = invites2.count;
                getMessagesStorage().saveChatLinksCount(this.currentChatId, this.info.invitesCount);
            }
        } else {
            this.hasMore = false;
        }
        boolean loadNext = false;
        if (!this.hasMore && !this.loadRevoked && this.adminId == getAccountInstance().getUserConfig().clientUserId) {
            this.hasMore = true;
            this.loadAdmins = true;
            loadNext = true;
        } else if (!this.hasMore && !this.loadRevoked) {
            this.hasMore = true;
            this.loadRevoked = true;
            loadNext = true;
        }
        if (!this.hasMore || this.invites.size() + this.revokedInvites.size() + this.admins.size() >= 5) {
            resumeDelayedFragmentAnimation();
        }
        if (loadNext) {
            loadLinks(false);
        }
        if (!updateByDiffUtils || this.listViewAdapter == null || this.listView.getChildCount() <= 0) {
            updateRows(true);
        } else {
            updateRecyclerViewAnimated(callback);
        }
    }

    /* access modifiers changed from: private */
    public void updateRows(boolean notify) {
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.currentChatId));
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
            boolean otherAdmin = false;
            this.rowCount = 0;
            if (this.adminId != getAccountInstance().getUserConfig().clientUserId) {
                otherAdmin = true;
            }
            if (otherAdmin) {
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
            if (!otherAdmin) {
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
            if (!otherAdmin && this.invites.isEmpty() && this.createNewLinkRow >= 0 && (!this.linksLoading || this.loadAdmins || this.loadRevoked)) {
                int i11 = this.rowCount;
                this.rowCount = i11 + 1;
                this.createLinkHelpRow = i11;
            }
            if (!otherAdmin && this.admins.size() > 0) {
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
                } else if (otherAdmin && this.linksStartRow == -1) {
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
            if (!this.loadAdmins && !this.loadRevoked && ((this.linksLoading || this.hasMore) && !otherAdmin)) {
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
            if (listAdapter != null && notify) {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("InviteLinks", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ManageLinksActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context) {
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
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView.setTag("windowBackgroundGray");
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.listView.setLayoutManager(layoutManager);
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (ManageLinksActivity.this.hasMore && !ManageLinksActivity.this.linksLoading) {
                    if (ManageLinksActivity.this.rowCount - layoutManager.findLastVisibleItemPosition() < 10) {
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ManageLinksActivity$$ExternalSyntheticLambda7(this, context));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new ManageLinksActivity$$ExternalSyntheticLambda8(this));
        this.linkIcon = ContextCompat.getDrawable(context, NUM);
        this.linkIconRevoked = ContextCompat.getDrawable(context, NUM);
        this.linkIcon.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        updateRows(true);
        this.timeDif = ((long) getConnectionsManager().getCurrentTime()) - (System.currentTimeMillis() / 1000);
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    public /* synthetic */ void m3274lambda$createView$9$orgtelegramuiManageLinksActivity(Context context, View view, int position) {
        if (position == this.creatorRow) {
            TLRPC.User user = this.users.get(Long.valueOf(this.invite.admin_id));
            if (user != null) {
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", user.id);
                MessagesController.getInstance(UserConfig.selectedAccount).putUser(user, false);
                presentFragment(new ProfileActivity(bundle));
            }
        } else if (position == this.createNewLinkRow) {
            LinkEditActivity linkEditActivity = new LinkEditActivity(0, this.currentChatId);
            linkEditActivity.setCallback(this.linkEditActivityCallback);
            presentFragment(linkEditActivity);
        } else {
            int i = this.linksStartRow;
            if (position < i || position >= this.linksEndRow) {
                int i2 = this.revokedLinksStartRow;
                if (position >= i2 && position < this.revokedLinksEndRow) {
                    InviteLinkBottomSheet inviteLinkBottomSheet2 = new InviteLinkBottomSheet(context, this.revokedInvites.get(position - i2), this.info, this.users, this, this.currentChatId, false, this.isChannel);
                    this.inviteLinkBottomSheet = inviteLinkBottomSheet2;
                    inviteLinkBottomSheet2.show();
                } else if (position != this.revokeAllRow) {
                    int i3 = this.adminsStartRow;
                    if (position >= i3 && position < this.adminsEndRow) {
                        TLRPC.TL_chatAdminWithInvites admin = this.admins.get(position - i3);
                        if (this.users.containsKey(Long.valueOf(admin.admin_id))) {
                            getMessagesController().putUser(this.users.get(Long.valueOf(admin.admin_id)), false);
                        }
                        ManageLinksActivity manageLinksActivity = new ManageLinksActivity(this.currentChatId, admin.admin_id, admin.invites_count);
                        manageLinksActivity.setInfo(this.info, (TLRPC.ExportedChatInvite) null);
                        presentFragment(manageLinksActivity);
                    }
                } else if (!this.deletingRevokedLinks) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setTitle(LocaleController.getString("DeleteAllRevokedLinks", NUM));
                    builder.setMessage(LocaleController.getString("DeleteAllRevokedLinkHelp", NUM));
                    builder.setPositiveButton(LocaleController.getString("Delete", NUM), new ManageLinksActivity$$ExternalSyntheticLambda0(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder.create());
                }
            } else {
                InviteLinkBottomSheet inviteLinkBottomSheet3 = new InviteLinkBottomSheet(context, this.invites.get(position - i), this.info, this.users, this, this.currentChatId, false, this.isChannel);
                this.inviteLinkBottomSheet = inviteLinkBottomSheet3;
                inviteLinkBottomSheet3.setCanEdit(this.canEdit);
                this.inviteLinkBottomSheet.show();
            }
        }
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    public /* synthetic */ void m3273lambda$createView$8$orgtelegramuiManageLinksActivity(DialogInterface dialogInterface2, int i2) {
        TLRPC.TL_messages_deleteRevokedExportedChatInvites req = new TLRPC.TL_messages_deleteRevokedExportedChatInvites();
        req.peer = getMessagesController().getInputPeer(-this.currentChatId);
        if (this.adminId == getUserConfig().getClientUserId()) {
            req.admin_id = getMessagesController().getInputUser(getUserConfig().getCurrentUser());
        } else {
            req.admin_id = getMessagesController().getInputUser(this.adminId);
        }
        this.deletingRevokedLinks = true;
        getConnectionsManager().sendRequest(req, new ManageLinksActivity$$ExternalSyntheticLambda17(this));
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    public /* synthetic */ void m3272lambda$createView$7$orgtelegramuiManageLinksActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ManageLinksActivity$$ExternalSyntheticLambda11(this, error));
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    public /* synthetic */ void m3271lambda$createView$6$orgtelegramuiManageLinksActivity(TLRPC.TL_error error) {
        this.deletingRevokedLinks = false;
        if (error == null) {
            DiffCallback callback = saveListState();
            this.revokedInvites.clear();
            updateRecyclerViewAnimated(callback);
        }
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    public /* synthetic */ boolean m3270lambda$createView$10$orgtelegramuiManageLinksActivity(View view, int position) {
        if ((position < this.linksStartRow || position >= this.linksEndRow) && (position < this.revokedLinksStartRow || position >= this.revokedLinksEndRow)) {
            return false;
        }
        ((LinkCell) view).optionsView.callOnClick();
        view.performHapticFeedback(0, 2);
        return true;
    }

    public void setInfo(TLRPC.ChatFull chatFull, TLRPC.ExportedChatInvite invite2) {
        this.info = chatFull;
        this.invite = (TLRPC.TL_chatInviteExported) invite2;
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
            String str;
            int i;
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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), heightMeasureSpec);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            if (ManageLinksActivity.this.creatorRow == position || ManageLinksActivity.this.createNewLinkRow == position) {
                return true;
            }
            if (position >= ManageLinksActivity.this.linksStartRow && position < ManageLinksActivity.this.linksEndRow) {
                return true;
            }
            if ((position >= ManageLinksActivity.this.revokedLinksStartRow && position < ManageLinksActivity.this.revokedLinksEndRow) || position == ManageLinksActivity.this.revokeAllRow) {
                return true;
            }
            if (position < ManageLinksActivity.this.adminsStartRow || position >= ManageLinksActivity.this.adminsEndRow) {
                return false;
            }
            return true;
        }

        public int getItemCount() {
            return ManageLinksActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 1:
                    View view2 = new HeaderCell(this.mContext, 23);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view2;
                    break;
                case 2:
                    Context context = this.mContext;
                    ManageLinksActivity manageLinksActivity = ManageLinksActivity.this;
                    final LinkActionView linkActionView = new LinkActionView(context, manageLinksActivity, (BottomSheet) null, manageLinksActivity.currentChatId, true, ManageLinksActivity.this.isChannel);
                    linkActionView.setPermanent(true);
                    linkActionView.setDelegate(new LinkActionView.Delegate() {
                        public /* synthetic */ void editLink() {
                            LinkActionView.Delegate.CC.$default$editLink(this);
                        }

                        public /* synthetic */ void removeLink() {
                            LinkActionView.Delegate.CC.$default$removeLink(this);
                        }

                        public void revokeLink() {
                            ManageLinksActivity.this.revokePermanent();
                        }

                        public void showUsersForPermanentLink() {
                            InviteLinkBottomSheet unused = ManageLinksActivity.this.inviteLinkBottomSheet = new InviteLinkBottomSheet(linkActionView.getContext(), ManageLinksActivity.this.invite, ManageLinksActivity.this.info, ManageLinksActivity.this.users, ManageLinksActivity.this, ManageLinksActivity.this.currentChatId, true, ManageLinksActivity.this.isChannel);
                            ManageLinksActivity.this.inviteLinkBottomSheet.show();
                        }
                    });
                    View view3 = linkActionView;
                    view3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view3;
                    break;
                case 3:
                    View view4 = new TextCell(this.mContext);
                    view4.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view4;
                    break;
                case 4:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 5:
                    view = new LinkCell(this.mContext);
                    break;
                case 6:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setViewType(9);
                    flickerLoadingView.showDate(false);
                    View view5 = flickerLoadingView;
                    view5.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view5;
                    break;
                case 7:
                    View shadowSectionCell = new ShadowSectionCell(this.mContext);
                    shadowSectionCell.setBackground(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    view = shadowSectionCell;
                    break;
                case 8:
                    TextSettingsCell revokeAll = new TextSettingsCell(this.mContext);
                    revokeAll.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    revokeAll.setText(LocaleController.getString("DeleteAllRevokedLinks", NUM), false);
                    revokeAll.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
                    view = revokeAll;
                    break;
                case 9:
                    TextInfoPrivacyCell cell = new TextInfoPrivacyCell(this.mContext);
                    cell.setText(LocaleController.getString("CreateNewLinkHelp", NUM));
                    cell.setBackground(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    TextInfoPrivacyCell textInfoPrivacyCell = cell;
                    view = cell;
                    break;
                case 10:
                    ManageChatUserCell userCell = new ManageChatUserCell(this.mContext, 8, 6, false);
                    userCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = userCell;
                    break;
                default:
                    View view6 = new HintInnerCell(this.mContext);
                    view6.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundWhite"));
                    view = view6;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.TL_chatInviteExported invite;
            int p;
            TLRPC.User user;
            switch (holder.getItemViewType()) {
                case 1:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == ManageLinksActivity.this.permanentLinkHeaderRow) {
                        if (ManageLinksActivity.this.isPublic && ManageLinksActivity.this.adminId == ManageLinksActivity.this.getAccountInstance().getUserConfig().clientUserId) {
                            headerCell.setText(LocaleController.getString("PublicLink", NUM));
                            return;
                        } else if (ManageLinksActivity.this.adminId == ManageLinksActivity.this.getAccountInstance().getUserConfig().clientUserId) {
                            headerCell.setText(LocaleController.getString("ChannelInviteLinkTitle", NUM));
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("PermanentLinkForThisAdmin", NUM));
                            return;
                        }
                    } else if (position == ManageLinksActivity.this.revokedHeader) {
                        headerCell.setText(LocaleController.getString("RevokedLinks", NUM));
                        return;
                    } else if (position == ManageLinksActivity.this.linksHeaderRow) {
                        headerCell.setText(LocaleController.getString("LinksCreatedByThisAdmin", NUM));
                        return;
                    } else if (position == ManageLinksActivity.this.adminsHeaderRow) {
                        headerCell.setText(LocaleController.getString("LinksCreatedByOtherAdmins", NUM));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    LinkActionView linkActionView = (LinkActionView) holder.itemView;
                    linkActionView.setCanEdit(ManageLinksActivity.this.adminId == ManageLinksActivity.this.getAccountInstance().getUserConfig().clientUserId);
                    if (!ManageLinksActivity.this.isPublic || ManageLinksActivity.this.adminId != ManageLinksActivity.this.getAccountInstance().getUserConfig().clientUserId) {
                        linkActionView.hideRevokeOption(true ^ ManageLinksActivity.this.canEdit);
                        if (ManageLinksActivity.this.invite != null) {
                            TLRPC.TL_chatInviteExported inviteExported = ManageLinksActivity.this.invite;
                            linkActionView.setLink(inviteExported.link);
                            linkActionView.loadUsers(inviteExported, ManageLinksActivity.this.currentChatId);
                            return;
                        }
                        linkActionView.setLink((String) null);
                        linkActionView.loadUsers((TLRPC.TL_chatInviteExported) null, ManageLinksActivity.this.currentChatId);
                        return;
                    } else if (ManageLinksActivity.this.info != null) {
                        linkActionView.setLink("https://t.me/" + ManageLinksActivity.this.currentChat.username);
                        linkActionView.setUsers(0, (ArrayList<TLRPC.User>) null);
                        linkActionView.hideRevokeOption(true);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    Drawable drawable1 = this.mContext.getResources().getDrawable(NUM);
                    Drawable drawable2 = this.mContext.getResources().getDrawable(NUM);
                    drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor("switchTrackChecked"), PorterDuff.Mode.MULTIPLY));
                    drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("checkboxCheck"), PorterDuff.Mode.MULTIPLY));
                    ((TextCell) holder.itemView).setTextAndIcon(LocaleController.getString("CreateNewLink", NUM), new CombinedDrawable(drawable1, drawable2), true ^ ManageLinksActivity.this.invites.isEmpty());
                    return;
                case 5:
                    boolean drawDivider = true;
                    if (position < ManageLinksActivity.this.linksStartRow || position >= ManageLinksActivity.this.linksEndRow) {
                        invite = (TLRPC.TL_chatInviteExported) ManageLinksActivity.this.revokedInvites.get(position - ManageLinksActivity.this.revokedLinksStartRow);
                        if (position == ManageLinksActivity.this.revokedLinksEndRow - 1) {
                            drawDivider = false;
                        }
                    } else {
                        invite = (TLRPC.TL_chatInviteExported) ManageLinksActivity.this.invites.get(position - ManageLinksActivity.this.linksStartRow);
                        if (position == ManageLinksActivity.this.linksEndRow - 1) {
                            drawDivider = false;
                        }
                    }
                    LinkCell cell = (LinkCell) holder.itemView;
                    cell.setLink(invite, position - ManageLinksActivity.this.linksStartRow);
                    cell.drawDivider = drawDivider;
                    return;
                case 10:
                    ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
                    boolean drawDivider2 = true;
                    if (position == ManageLinksActivity.this.creatorRow) {
                        user = ManageLinksActivity.this.getMessagesController().getUser(Long.valueOf(ManageLinksActivity.this.adminId));
                        p = ManageLinksActivity.this.invitesCount;
                        drawDivider2 = false;
                    } else {
                        TLRPC.TL_chatAdminWithInvites admin = (TLRPC.TL_chatAdminWithInvites) ManageLinksActivity.this.admins.get(position - ManageLinksActivity.this.adminsStartRow);
                        TLRPC.User user2 = (TLRPC.User) ManageLinksActivity.this.users.get(Long.valueOf(admin.admin_id));
                        int count = admin.invites_count;
                        if (position == ManageLinksActivity.this.adminsEndRow - 1) {
                            drawDivider2 = false;
                            user = user2;
                            p = count;
                        } else {
                            user = user2;
                            p = count;
                        }
                    }
                    if (user != null) {
                        userCell.setData(user, ContactsController.formatName(user.first_name, user.last_name), LocaleController.formatPluralString("InviteLinkCount", p), drawDivider2);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int position) {
            if (position == ManageLinksActivity.this.helpRow) {
                return 0;
            }
            if (position == ManageLinksActivity.this.permanentLinkHeaderRow || position == ManageLinksActivity.this.revokedHeader || position == ManageLinksActivity.this.adminsHeaderRow || position == ManageLinksActivity.this.linksHeaderRow) {
                return 1;
            }
            if (position == ManageLinksActivity.this.permanentLinkRow) {
                return 2;
            }
            if (position == ManageLinksActivity.this.createNewLinkRow) {
                return 3;
            }
            if (position == ManageLinksActivity.this.dividerRow || position == ManageLinksActivity.this.revokedDivider || position == ManageLinksActivity.this.revokeAllDivider || position == ManageLinksActivity.this.creatorDividerRow || position == ManageLinksActivity.this.adminsDividerRow) {
                return 4;
            }
            if (position >= ManageLinksActivity.this.linksStartRow && position < ManageLinksActivity.this.linksEndRow) {
                return 5;
            }
            if (position >= ManageLinksActivity.this.revokedLinksStartRow && position < ManageLinksActivity.this.revokedLinksEndRow) {
                return 5;
            }
            if (position == ManageLinksActivity.this.linksLoadingRow) {
                return 6;
            }
            if (position == ManageLinksActivity.this.lastDivider) {
                return 7;
            }
            if (position == ManageLinksActivity.this.revokeAllRow) {
                return 8;
            }
            if (position == ManageLinksActivity.this.createLinkHelpRow) {
                return 9;
            }
            if (position == ManageLinksActivity.this.creatorRow) {
                return 10;
            }
            if (position < ManageLinksActivity.this.adminsStartRow || position >= ManageLinksActivity.this.adminsEndRow) {
                return 1;
            }
            return 10;
        }
    }

    /* access modifiers changed from: private */
    public void revokePermanent() {
        if (this.adminId == getAccountInstance().getUserConfig().clientUserId) {
            TLRPC.TL_messages_exportChatInvite req = new TLRPC.TL_messages_exportChatInvite();
            req.peer = getMessagesController().getInputPeer(-this.currentChatId);
            req.legacy_revoke_permanent = true;
            TLRPC.TL_chatInviteExported oldInvite = this.invite;
            this.invite = null;
            this.info.exported_invite = null;
            int reqId = getConnectionsManager().sendRequest(req, new ManageLinksActivity$$ExternalSyntheticLambda4(this, oldInvite));
            AndroidUtilities.updateVisibleRows(this.listView);
            getConnectionsManager().bindRequestToGuid(reqId, this.classGuid);
            return;
        }
        revokeLink(this.invite);
    }

    /* renamed from: lambda$revokePermanent$12$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    public /* synthetic */ void m3287lambda$revokePermanent$12$orgtelegramuiManageLinksActivity(TLRPC.TL_chatInviteExported oldInvite, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ManageLinksActivity$$ExternalSyntheticLambda15(this, error, response, oldInvite));
    }

    /* renamed from: lambda$revokePermanent$11$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    public /* synthetic */ void m3286lambda$revokePermanent$11$orgtelegramuiManageLinksActivity(TLRPC.TL_error error, TLObject response, TLRPC.TL_chatInviteExported oldInvite) {
        if (error == null) {
            TLRPC.TL_chatInviteExported tL_chatInviteExported = (TLRPC.TL_chatInviteExported) response;
            this.invite = tL_chatInviteExported;
            TLRPC.ChatFull chatFull = this.info;
            if (chatFull != null) {
                chatFull.exported_invite = tL_chatInviteExported;
            }
            if (getParentActivity() != null) {
                oldInvite.revoked = true;
                DiffCallback callback = saveListState();
                this.revokedInvites.add(0, oldInvite);
                updateRecyclerViewAnimated(callback);
                BulletinFactory.of(this).createSimpleBulletin(NUM, LocaleController.getString("InviteRevokedHint", NUM)).show();
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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int dp = AndroidUtilities.dp(48.0f);
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(94.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
            setMeasuredDimension(width, AndroidUtilities.dp(50.0f));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int viewLeft;
            int width = right - left;
            int viewTop = ((bottom - top) - this.textView.getTextHeight()) / 2;
            if (LocaleController.isRTL) {
                viewLeft = (getMeasuredWidth() - this.textView.getMeasuredWidth()) - AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? 70.0f : 25.0f);
            } else {
                viewLeft = AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? 70.0f : 25.0f);
            }
            SimpleTextView simpleTextView = this.textView;
            simpleTextView.layout(viewLeft, viewTop, simpleTextView.getMeasuredWidth() + viewLeft, this.textView.getMeasuredHeight() + viewTop);
            int viewLeft2 = !LocaleController.isRTL ? (AndroidUtilities.dp(70.0f) - this.imageView.getMeasuredWidth()) / 2 : (width - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(25.0f);
            ImageView imageView2 = this.imageView;
            imageView2.layout(viewLeft2, 0, imageView2.getMeasuredWidth() + viewLeft2, this.imageView.getMeasuredHeight());
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.divider) {
                canvas.drawLine((float) AndroidUtilities.dp(70.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() + AndroidUtilities.dp(23.0f)), (float) getMeasuredHeight(), Theme.dividerPaint);
            }
        }

        public void setTextAndIcon(String text, Drawable icon, boolean divider2) {
            this.textView.setText(text);
            this.imageView.setImageDrawable(icon);
            this.divider = divider2;
        }
    }

    private class LinkCell extends FrameLayout {
        private static final int LINK_STATE_BLUE = 0;
        private static final int LINK_STATE_GRAY = 4;
        private static final int LINK_STATE_GREEN = 1;
        private static final int LINK_STATE_RED = 3;
        private static final int LINK_STATE_YELLOW = 2;
        int animateFromState;
        boolean animateHideExpiring;
        float animateToStateProgress = 1.0f;
        boolean drawDivider;
        TLRPC.TL_chatInviteExported invite;
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
            this.optionsView.setOnClickListener(new ManageLinksActivity$LinkCell$$ExternalSyntheticLambda3(this));
            this.optionsView.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 1));
            addView(this.optionsView, LayoutHelper.createFrame(40, 48, 21));
            setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            setWillNotDraw(false);
        }

        /* renamed from: lambda$new$3$org-telegram-ui-ManageLinksActivity$LinkCell  reason: not valid java name */
        public /* synthetic */ void m3292lambda$new$3$orgtelegramuiManageLinksActivity$LinkCell(View view) {
            if (this.invite != null) {
                ArrayList<String> items = new ArrayList<>();
                ArrayList<Integer> icons = new ArrayList<>();
                ArrayList<Integer> actions = new ArrayList<>();
                boolean redLastItem = false;
                if (this.invite.revoked) {
                    items.add(LocaleController.getString("Delete", NUM));
                    icons.add(NUM);
                    actions.add(4);
                    redLastItem = true;
                } else {
                    items.add(LocaleController.getString("CopyLink", NUM));
                    icons.add(NUM);
                    actions.add(0);
                    items.add(LocaleController.getString("ShareLink", NUM));
                    icons.add(NUM);
                    actions.add(1);
                    if (!this.invite.permanent && ManageLinksActivity.this.canEdit) {
                        items.add(LocaleController.getString("EditLink", NUM));
                        icons.add(NUM);
                        actions.add(2);
                    }
                    if (ManageLinksActivity.this.canEdit) {
                        items.add(LocaleController.getString("RevokeLink", NUM));
                        icons.add(NUM);
                        actions.add(3);
                        redLastItem = true;
                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) ManageLinksActivity.this.getParentActivity());
                builder.setItems((CharSequence[]) items.toArray(new CharSequence[0]), AndroidUtilities.toIntArray(icons), new ManageLinksActivity$LinkCell$$ExternalSyntheticLambda0(this, actions));
                builder.setTitle(LocaleController.getString("InviteLink", NUM));
                AlertDialog alert = builder.create();
                builder.show();
                if (redLastItem) {
                    alert.setItemColor(items.size() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
                }
            }
        }

        /* renamed from: lambda$new$2$org-telegram-ui-ManageLinksActivity$LinkCell  reason: not valid java name */
        public /* synthetic */ void m3291lambda$new$2$orgtelegramuiManageLinksActivity$LinkCell(ArrayList actions, DialogInterface dialogInterface, int i) {
            switch (((Integer) actions.get(i)).intValue()) {
                case 0:
                    try {
                        if (this.invite.link != null) {
                            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.invite.link));
                            BulletinFactory.createCopyLinkBulletin((BaseFragment) ManageLinksActivity.this).show();
                            return;
                        }
                        return;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        return;
                    }
                case 1:
                    try {
                        if (this.invite.link != null) {
                            Intent intent = new Intent("android.intent.action.SEND");
                            intent.setType("text/plain");
                            intent.putExtra("android.intent.extra.TEXT", this.invite.link);
                            ManageLinksActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteToGroupByLink", NUM)), 500);
                            return;
                        }
                        return;
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                        return;
                    }
                case 2:
                    ManageLinksActivity.this.editLink(this.invite);
                    return;
                case 3:
                    TLRPC.TL_chatInviteExported inviteFinal = this.invite;
                    AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) ManageLinksActivity.this.getParentActivity());
                    builder2.setMessage(LocaleController.getString("RevokeAlert", NUM));
                    builder2.setTitle(LocaleController.getString("RevokeLink", NUM));
                    builder2.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new ManageLinksActivity$LinkCell$$ExternalSyntheticLambda1(this, inviteFinal));
                    builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    ManageLinksActivity.this.showDialog(builder2.create());
                    return;
                case 4:
                    TLRPC.TL_chatInviteExported inviteFinal2 = this.invite;
                    AlertDialog.Builder builder22 = new AlertDialog.Builder((Context) ManageLinksActivity.this.getParentActivity());
                    builder22.setTitle(LocaleController.getString("DeleteLink", NUM));
                    builder22.setMessage(LocaleController.getString("DeleteLinkHelp", NUM));
                    builder22.setPositiveButton(LocaleController.getString("Delete", NUM), new ManageLinksActivity$LinkCell$$ExternalSyntheticLambda2(this, inviteFinal2));
                    builder22.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    ManageLinksActivity.this.showDialog(builder22.create());
                    return;
                default:
                    return;
            }
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ManageLinksActivity$LinkCell  reason: not valid java name */
        public /* synthetic */ void m3289lambda$new$0$orgtelegramuiManageLinksActivity$LinkCell(TLRPC.TL_chatInviteExported inviteFinal, DialogInterface dialogInterface2, int i2) {
            ManageLinksActivity.this.revokeLink(inviteFinal);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-ManageLinksActivity$LinkCell  reason: not valid java name */
        public /* synthetic */ void m3290lambda$new$1$orgtelegramuiManageLinksActivity$LinkCell(TLRPC.TL_chatInviteExported inviteFinal, DialogInterface dialogInterface2, int i2) {
            ManageLinksActivity.this.deleteLink(inviteFinal);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), NUM));
            this.paint2.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x00d9  */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00dd  */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x00e9  */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x0101  */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x0113  */
        /* JADX WARNING: Removed duplicated region for block: B:62:0x0145  */
        /* JADX WARNING: Removed duplicated region for block: B:63:0x0148  */
        /* JADX WARNING: Removed duplicated region for block: B:85:0x0219  */
        /* JADX WARNING: Removed duplicated region for block: B:86:0x023e  */
        /* JADX WARNING: Removed duplicated region for block: B:89:0x0266  */
        /* JADX WARNING: Removed duplicated region for block: B:91:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r24) {
            /*
                r23 = this;
                r0 = r23
                r7 = r24
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r0.invite
                if (r1 != 0) goto L_0x0009
                return
            L_0x0009:
                r1 = 1108344832(0x42100000, float:36.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r1 = r23.getMeasuredHeight()
                int r9 = r1 / 2
                r1 = 0
                r2 = 1065353216(0x3var_, float:1.0)
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r3 = r0.invite
                boolean r3 = r3.expired
                if (r3 != 0) goto L_0x00b2
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r3 = r0.invite
                boolean r3 = r3.revoked
                if (r3 == 0) goto L_0x0028
                r6 = r1
                r15 = r2
                goto L_0x00b4
            L_0x0028:
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r3 = r0.invite
                int r3 = r3.expire_date
                if (r3 > 0) goto L_0x003a
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r3 = r0.invite
                int r3 = r3.usage_limit
                if (r3 <= 0) goto L_0x0035
                goto L_0x003a
            L_0x0035:
                r3 = 0
                r10 = r1
                r11 = r3
                goto L_0x00c1
            L_0x003a:
                r3 = 1065353216(0x3var_, float:1.0)
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r6 = r0.invite
                int r6 = r6.expire_date
                if (r6 <= 0) goto L_0x007a
                long r11 = java.lang.System.currentTimeMillis()
                org.telegram.ui.ManageLinksActivity r6 = org.telegram.ui.ManageLinksActivity.this
                long r13 = r6.timeDif
                r15 = 1000(0x3e8, double:4.94E-321)
                long r13 = r13 * r15
                long r11 = r11 + r13
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r6 = r0.invite
                int r6 = r6.expire_date
                long r13 = (long) r6
                long r13 = r13 * r15
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r6 = r0.invite
                int r6 = r6.start_date
                if (r6 > 0) goto L_0x0061
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r6 = r0.invite
                int r6 = r6.date
                goto L_0x0065
            L_0x0061:
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r6 = r0.invite
                int r6 = r6.start_date
            L_0x0065:
                long r4 = (long) r6
                long r4 = r4 * r15
                r6 = r1
                r15 = r2
                long r1 = r11 - r4
                r19 = r11
                long r10 = r13 - r4
                float r12 = (float) r1
                r21 = r1
                float r1 = (float) r10
                float r12 = r12 / r1
                r1 = 1065353216(0x3var_, float:1.0)
                float r2 = r1 - r12
                goto L_0x007c
            L_0x007a:
                r6 = r1
                r15 = r2
            L_0x007c:
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r0.invite
                int r1 = r1.usage_limit
                if (r1 <= 0) goto L_0x0093
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r0.invite
                int r1 = r1.usage_limit
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r4 = r0.invite
                int r4 = r4.usage
                int r1 = r1 - r4
                float r1 = (float) r1
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r4 = r0.invite
                int r4 = r4.usage_limit
                float r4 = (float) r4
                float r3 = r1 / r4
            L_0x0093:
                float r1 = java.lang.Math.min(r2, r3)
                r4 = 0
                int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r5 > 0) goto L_0x00ad
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r4 = r0.invite
                r5 = 1
                r4.expired = r5
                r4 = 3
                org.telegram.ui.ManageLinksActivity r5 = org.telegram.ui.ManageLinksActivity.this
                org.telegram.ui.Components.RecyclerListView r5 = r5.listView
                org.telegram.messenger.AndroidUtilities.updateVisibleRows(r5)
                r3 = r4
                goto L_0x00af
            L_0x00ad:
                r4 = 1
                r3 = r4
            L_0x00af:
                r10 = r1
                r11 = r3
                goto L_0x00c1
            L_0x00b2:
                r6 = r1
                r15 = r2
            L_0x00b4:
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r0.invite
                boolean r1 = r1.revoked
                if (r1 == 0) goto L_0x00bc
                r1 = 4
                goto L_0x00bd
            L_0x00bc:
                r1 = 3
            L_0x00bd:
                r3 = r1
                r11 = r3
                r10 = r6
                r2 = r15
            L_0x00c1:
                int r1 = r0.lastDrawingState
                r3 = 0
                if (r11 == r1) goto L_0x00df
                if (r1 < 0) goto L_0x00df
                r0.animateFromState = r1
                r4 = 0
                r0.animateToStateProgress = r4
                boolean r1 = r0.hasProgress(r1)
                if (r1 == 0) goto L_0x00dd
                boolean r1 = r0.hasProgress(r11)
                if (r1 != 0) goto L_0x00dd
                r1 = 1
                r0.animateHideExpiring = r1
                goto L_0x00df
            L_0x00dd:
                r0.animateHideExpiring = r3
            L_0x00df:
                r0.lastDrawingState = r11
                float r1 = r0.animateToStateProgress
                r4 = 1065353216(0x3var_, float:1.0)
                int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r5 == 0) goto L_0x00fb
                r5 = 1032000111(0x3d83126f, float:0.064)
                float r1 = r1 + r5
                r0.animateToStateProgress = r1
                int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r1 < 0) goto L_0x00f8
                r0.animateToStateProgress = r4
                r0.animateHideExpiring = r3
                goto L_0x00fb
            L_0x00f8:
                r23.invalidate()
            L_0x00fb:
                float r1 = r0.animateToStateProgress
                int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r1 == 0) goto L_0x0113
                int r1 = r0.animateFromState
                int r1 = r0.getColor(r1, r10)
                int r3 = r0.getColor(r11, r10)
                float r4 = r0.animateToStateProgress
                int r1 = androidx.core.graphics.ColorUtils.blendARGB(r1, r3, r4)
                r12 = r1
                goto L_0x0118
            L_0x0113:
                int r1 = r0.getColor(r11, r10)
                r12 = r1
            L_0x0118:
                android.graphics.Paint r1 = r0.paint
                r1.setColor(r12)
                float r1 = (float) r8
                float r3 = (float) r9
                r4 = 1107296256(0x42000000, float:32.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r4 = (float) r4
                r5 = 1073741824(0x40000000, float:2.0)
                float r4 = r4 / r5
                android.graphics.Paint r5 = r0.paint
                r7.drawCircle(r1, r3, r4, r5)
                boolean r1 = r0.animateHideExpiring
                if (r1 != 0) goto L_0x0148
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r0.invite
                boolean r1 = r1.expired
                if (r1 != 0) goto L_0x0145
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r0.invite
                int r1 = r1.expire_date
                if (r1 <= 0) goto L_0x0145
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r0.invite
                boolean r1 = r1.revoked
                if (r1 != 0) goto L_0x0145
                goto L_0x0148
            L_0x0145:
                r13 = r2
                goto L_0x0211
            L_0x0148:
                boolean r1 = r0.animateHideExpiring
                if (r1 == 0) goto L_0x0150
                float r2 = r0.lastDrawExpringProgress
                r13 = r2
                goto L_0x0151
            L_0x0150:
                r13 = r2
            L_0x0151:
                android.graphics.Paint r1 = r0.paint2
                r1.setColor(r12)
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
                r14 = 1135869952(0x43b40000, float:360.0)
                r2 = 1065353216(0x3var_, float:1.0)
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 == 0) goto L_0x01e5
                int r1 = r0.animateFromState
                boolean r1 = r0.hasProgress(r1)
                if (r1 == 0) goto L_0x018d
                boolean r1 = r0.animateHideExpiring
                if (r1 == 0) goto L_0x01e5
            L_0x018d:
                r24.save()
                boolean r1 = r0.animateHideExpiring
                if (r1 == 0) goto L_0x019b
                float r1 = r0.animateToStateProgress
                r2 = 1065353216(0x3var_, float:1.0)
                float r5 = r2 - r1
                goto L_0x019d
            L_0x019b:
                float r5 = r0.animateToStateProgress
            L_0x019d:
                r15 = r5
                r1 = 4604480259023595110(0x3feNUM, double:0.7)
                r3 = 1050253722(0x3e99999a, float:0.3)
                float r3 = r3 * r15
                double r3 = (double) r3
                java.lang.Double.isNaN(r3)
                double r3 = r3 + r1
                float r6 = (float) r3
                android.graphics.RectF r1 = r0.rectF
                float r1 = r1.centerX()
                android.graphics.RectF r2 = r0.rectF
                float r2 = r2.centerY()
                r7.scale(r6, r6, r1, r2)
                android.graphics.RectF r2 = r0.rectF
                r3 = -1028390912(0xffffffffc2b40000, float:-90.0)
                float r1 = -r13
                float r4 = r1 * r14
                r5 = 0
                android.graphics.Paint r1 = r0.paint2
                r17 = r1
                r1 = r24
                r18 = r6
                r6 = r17
                r1.drawArc(r2, r3, r4, r5, r6)
                org.telegram.ui.Components.TimerParticles r1 = r0.timerParticles
                android.graphics.Paint r3 = r0.paint2
                android.graphics.RectF r4 = r0.rectF
                float r2 = -r13
                float r5 = r2 * r14
                r2 = r24
                r6 = r15
                r1.draw(r2, r3, r4, r5, r6)
                r24.restore()
                goto L_0x0204
            L_0x01e5:
                android.graphics.RectF r2 = r0.rectF
                r3 = -1028390912(0xffffffffc2b40000, float:-90.0)
                float r1 = -r13
                float r4 = r1 * r14
                r5 = 0
                android.graphics.Paint r6 = r0.paint2
                r1 = r24
                r1.drawArc(r2, r3, r4, r5, r6)
                org.telegram.ui.Components.TimerParticles r1 = r0.timerParticles
                android.graphics.Paint r3 = r0.paint2
                android.graphics.RectF r4 = r0.rectF
                float r2 = -r13
                float r5 = r2 * r14
                r6 = 1065353216(0x3var_, float:1.0)
                r2 = r24
                r1.draw(r2, r3, r4, r5, r6)
            L_0x0204:
                org.telegram.ui.ManageLinksActivity r1 = org.telegram.ui.ManageLinksActivity.this
                boolean r1 = r1.isPaused
                if (r1 != 0) goto L_0x020f
                r23.invalidate()
            L_0x020f:
                r0.lastDrawExpringProgress = r13
            L_0x0211:
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r0.invite
                boolean r1 = r1.revoked
                r2 = 1094713344(0x41400000, float:12.0)
                if (r1 == 0) goto L_0x023e
                org.telegram.ui.ManageLinksActivity r1 = org.telegram.ui.ManageLinksActivity.this
                android.graphics.drawable.Drawable r1 = r1.linkIconRevoked
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r3 = r8 - r3
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r4 = r9 - r4
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r5 = r5 + r8
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = r2 + r9
                r1.setBounds(r3, r4, r5, r2)
                org.telegram.ui.ManageLinksActivity r1 = org.telegram.ui.ManageLinksActivity.this
                android.graphics.drawable.Drawable r1 = r1.linkIconRevoked
                r1.draw(r7)
                goto L_0x0262
            L_0x023e:
                org.telegram.ui.ManageLinksActivity r1 = org.telegram.ui.ManageLinksActivity.this
                android.graphics.drawable.Drawable r1 = r1.linkIcon
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r3 = r8 - r3
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r4 = r9 - r4
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r5 = r5 + r8
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = r2 + r9
                r1.setBounds(r3, r4, r5, r2)
                org.telegram.ui.ManageLinksActivity r1 = org.telegram.ui.ManageLinksActivity.this
                android.graphics.drawable.Drawable r1 = r1.linkIcon
                r1.draw(r7)
            L_0x0262:
                boolean r1 = r0.drawDivider
                if (r1 == 0) goto L_0x028c
                r1 = 1116471296(0x428CLASSNAME, float:70.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r2 = (float) r1
                int r1 = r23.getMeasuredHeight()
                r3 = 1
                int r1 = r1 - r3
                float r3 = (float) r1
                int r1 = r23.getMeasuredWidth()
                r4 = 1102577664(0x41b80000, float:23.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r1 = r1 + r4
                float r4 = (float) r1
                int r1 = r23.getMeasuredHeight()
                float r5 = (float) r1
                android.graphics.Paint r6 = org.telegram.ui.ActionBar.Theme.dividerPaint
                r1 = r24
                r1.drawLine(r2, r3, r4, r5, r6)
            L_0x028c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ManageLinksActivity.LinkCell.onDraw(android.graphics.Canvas):void");
        }

        private boolean hasProgress(int state) {
            return state == 2 || state == 1;
        }

        private int getColor(int state, float progress) {
            if (state == 3) {
                return Theme.getColor("chat_attachAudioBackground");
            }
            if (state == 1) {
                if (progress > 0.5f) {
                    return ColorUtils.blendARGB(Theme.getColor("chat_attachLocationBackground"), Theme.getColor("chat_attachPollBackground"), 1.0f - ((progress - 0.5f) / 0.5f));
                }
                return ColorUtils.blendARGB(Theme.getColor("chat_attachPollBackground"), Theme.getColor("chat_attachAudioBackground"), 1.0f - (progress / 0.5f));
            } else if (state == 2) {
                return Theme.getColor("chat_attachPollBackground");
            } else {
                if (state == 4) {
                    return Theme.getColor("chats_unreadCounterMuted");
                }
                return Theme.getColor("featuredStickers_addButton");
            }
        }

        public void setLink(TLRPC.TL_chatInviteExported invite2, int position2) {
            String str;
            int i;
            TLRPC.TL_chatInviteExported tL_chatInviteExported = invite2;
            this.timerRunning = false;
            TLRPC.TL_chatInviteExported tL_chatInviteExported2 = this.invite;
            if (tL_chatInviteExported2 == null || tL_chatInviteExported == null || !tL_chatInviteExported2.link.equals(tL_chatInviteExported.link)) {
                this.lastDrawingState = -1;
                this.animateToStateProgress = 1.0f;
            }
            this.invite = tL_chatInviteExported;
            this.position = position2;
            if (tL_chatInviteExported != null) {
                if (!TextUtils.isEmpty(tL_chatInviteExported.title)) {
                    SpannableStringBuilder builder = new SpannableStringBuilder(tL_chatInviteExported.title);
                    Emoji.replaceEmoji(builder, this.titleView.getPaint().getFontMetricsInt(), (int) this.titleView.getPaint().getTextSize(), false);
                    this.titleView.setText(builder);
                } else if (tL_chatInviteExported.link.startsWith("https://t.me/+")) {
                    this.titleView.setText(tL_chatInviteExported.link.substring("https://t.me/+".length()));
                } else if (tL_chatInviteExported.link.startsWith("https://t.me/joinchat/")) {
                    this.titleView.setText(tL_chatInviteExported.link.substring("https://t.me/joinchat/".length()));
                } else if (tL_chatInviteExported.link.startsWith("https://")) {
                    this.titleView.setText(tL_chatInviteExported.link.substring("https://".length()));
                } else {
                    this.titleView.setText(tL_chatInviteExported.link);
                }
                String joinedString = "";
                if (tL_chatInviteExported.usage == 0 && tL_chatInviteExported.usage_limit == 0 && tL_chatInviteExported.requested == 0) {
                    joinedString = LocaleController.getString("NoOneJoinedYet", NUM);
                } else if (tL_chatInviteExported.usage_limit > 0 && tL_chatInviteExported.usage == 0 && !tL_chatInviteExported.expired && !tL_chatInviteExported.revoked) {
                    joinedString = LocaleController.formatPluralString("CanJoin", tL_chatInviteExported.usage_limit);
                } else if (tL_chatInviteExported.usage_limit <= 0 || !tL_chatInviteExported.expired || !tL_chatInviteExported.revoked) {
                    if (tL_chatInviteExported.usage > 0) {
                        joinedString = LocaleController.formatPluralString("PeopleJoined", tL_chatInviteExported.usage);
                    }
                    if (tL_chatInviteExported.requested > 0) {
                        if (tL_chatInviteExported.usage > 0) {
                            joinedString = joinedString + ", ";
                        }
                        joinedString = joinedString + LocaleController.formatPluralString("JoinRequests", tL_chatInviteExported.requested);
                    }
                } else {
                    joinedString = LocaleController.formatPluralString("PeopleJoined", tL_chatInviteExported.usage) + ", " + LocaleController.formatPluralString("PeopleJoinedRemaining", tL_chatInviteExported.usage_limit - tL_chatInviteExported.usage);
                }
                if (tL_chatInviteExported.permanent && !tL_chatInviteExported.revoked) {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(joinedString);
                    DotDividerSpan dotDividerSpan = new DotDividerSpan();
                    dotDividerSpan.setTopPadding(AndroidUtilities.dp(1.5f));
                    spannableStringBuilder.append("  .  ").setSpan(dotDividerSpan, spannableStringBuilder.length() - 3, spannableStringBuilder.length() - 2, 0);
                    spannableStringBuilder.append(LocaleController.getString("Permanent", NUM));
                    this.subtitleView.setText(spannableStringBuilder);
                } else if (tL_chatInviteExported.expired || tL_chatInviteExported.revoked) {
                    if (tL_chatInviteExported.revoked && tL_chatInviteExported.usage == 0) {
                        joinedString = LocaleController.getString("NoOneJoined", NUM);
                    }
                    SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(joinedString);
                    DotDividerSpan dotDividerSpan2 = new DotDividerSpan();
                    dotDividerSpan2.setTopPadding(AndroidUtilities.dp(1.5f));
                    spannableStringBuilder2.append("  .  ").setSpan(dotDividerSpan2, spannableStringBuilder2.length() - 3, spannableStringBuilder2.length() - 2, 0);
                    if (tL_chatInviteExported.revoked || tL_chatInviteExported.usage_limit <= 0 || tL_chatInviteExported.usage < tL_chatInviteExported.usage_limit) {
                        if (tL_chatInviteExported.revoked) {
                            i = NUM;
                            str = "Revoked";
                        } else {
                            i = NUM;
                            str = "Expired";
                        }
                        spannableStringBuilder2.append(LocaleController.getString(str, i));
                    } else {
                        spannableStringBuilder2.append(LocaleController.getString("LinkLimitReached", NUM));
                    }
                    this.subtitleView.setText(spannableStringBuilder2);
                } else if (tL_chatInviteExported.expire_date > 0) {
                    SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder(joinedString);
                    DotDividerSpan dotDividerSpan3 = new DotDividerSpan();
                    dotDividerSpan3.setTopPadding(AndroidUtilities.dp(1.5f));
                    spannableStringBuilder3.append("  .  ").setSpan(dotDividerSpan3, spannableStringBuilder3.length() - 3, spannableStringBuilder3.length() - 2, 0);
                    long currentTime = System.currentTimeMillis() + (ManageLinksActivity.this.timeDif * 1000);
                    long timeLeft = (((long) tL_chatInviteExported.expire_date) * 1000) - currentTime;
                    if (timeLeft < 0) {
                        timeLeft = 0;
                    }
                    if (timeLeft > 86400000) {
                        spannableStringBuilder3.append(LocaleController.formatPluralString("DaysLeft", (int) (timeLeft / 86400000)));
                        long j = currentTime;
                    } else {
                        int m = (int) (((timeLeft / 1000) / 60) % 60);
                        long j2 = currentTime;
                        int i2 = m;
                        spannableStringBuilder3.append(String.format(Locale.ENGLISH, "%02d", new Object[]{Integer.valueOf((int) (((timeLeft / 1000) / 60) / 60))})).append(String.format(Locale.ENGLISH, ":%02d", new Object[]{Integer.valueOf(m)})).append(String.format(Locale.ENGLISH, ":%02d", new Object[]{Integer.valueOf((int) ((timeLeft / 1000) % 60))}));
                        this.timerRunning = true;
                    }
                    this.subtitleView.setText(spannableStringBuilder3);
                } else {
                    this.subtitleView.setText(joinedString);
                }
            }
        }
    }

    public void deleteLink(TLRPC.TL_chatInviteExported invite2) {
        TLRPC.TL_messages_deleteExportedChatInvite req = new TLRPC.TL_messages_deleteExportedChatInvite();
        req.link = invite2.link;
        req.peer = getMessagesController().getInputPeer(-this.currentChatId);
        getConnectionsManager().sendRequest(req, new ManageLinksActivity$$ExternalSyntheticLambda2(this, invite2));
    }

    /* renamed from: lambda$deleteLink$14$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    public /* synthetic */ void m3276lambda$deleteLink$14$orgtelegramuiManageLinksActivity(TLRPC.TL_chatInviteExported invite2, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ManageLinksActivity$$ExternalSyntheticLambda16(this, error, invite2));
    }

    /* renamed from: lambda$deleteLink$13$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    public /* synthetic */ void m3275lambda$deleteLink$13$orgtelegramuiManageLinksActivity(TLRPC.TL_error error, TLRPC.TL_chatInviteExported invite2) {
        if (error == null) {
            this.linkEditActivityCallback.onLinkRemoved(invite2);
        }
    }

    public void editLink(TLRPC.TL_chatInviteExported invite2) {
        LinkEditActivity activity = new LinkEditActivity(1, this.currentChatId);
        activity.setCallback(this.linkEditActivityCallback);
        activity.setInviteToEdit(invite2);
        presentFragment(activity);
    }

    public void revokeLink(TLRPC.TL_chatInviteExported invite2) {
        TLRPC.TL_messages_editExportedChatInvite req = new TLRPC.TL_messages_editExportedChatInvite();
        req.link = invite2.link;
        req.revoked = true;
        req.peer = getMessagesController().getInputPeer(-this.currentChatId);
        getConnectionsManager().sendRequest(req, new ManageLinksActivity$$ExternalSyntheticLambda3(this, invite2));
    }

    /* renamed from: lambda$revokeLink$16$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    public /* synthetic */ void m3285lambda$revokeLink$16$orgtelegramuiManageLinksActivity(TLRPC.TL_chatInviteExported invite2, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ManageLinksActivity$$ExternalSyntheticLambda14(this, error, response, invite2));
    }

    /* renamed from: lambda$revokeLink$15$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    public /* synthetic */ void m3284lambda$revokeLink$15$orgtelegramuiManageLinksActivity(TLRPC.TL_error error, TLObject response, TLRPC.TL_chatInviteExported invite2) {
        if (error == null) {
            if (response instanceof TLRPC.TL_messages_exportedChatInviteReplaced) {
                TLRPC.TL_messages_exportedChatInviteReplaced replaced = (TLRPC.TL_messages_exportedChatInviteReplaced) response;
                if (!this.isPublic) {
                    this.invite = (TLRPC.TL_chatInviteExported) replaced.new_invite;
                }
                invite2.revoked = true;
                DiffCallback callback = saveListState();
                if (this.isPublic && this.adminId == getAccountInstance().getUserConfig().getClientUserId()) {
                    this.invites.remove(invite2);
                    this.invites.add(0, (TLRPC.TL_chatInviteExported) replaced.new_invite);
                } else if (this.invite != null) {
                    this.invite = (TLRPC.TL_chatInviteExported) replaced.new_invite;
                }
                this.revokedInvites.add(0, invite2);
                updateRecyclerViewAnimated(callback);
            } else {
                this.linkEditActivityCallback.onLinkEdited(invite2, response);
                TLRPC.ChatFull chatFull = this.info;
                if (chatFull != null) {
                    chatFull.invitesCount--;
                    if (this.info.invitesCount < 0) {
                        this.info.invitesCount = 0;
                    }
                    getMessagesStorage().saveChatLinksCount(this.currentChatId, this.info.invitesCount);
                }
            }
            if (getParentActivity() != null) {
                BulletinFactory.of(this).createSimpleBulletin(NUM, LocaleController.getString("InviteRevokedHint", NUM)).show();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateRecyclerViewAnimated(DiffCallback callback) {
        if (this.isPaused || this.listViewAdapter == null || this.listView == null) {
            updateRows(true);
            return;
        }
        updateRows(false);
        callback.fillPositions(callback.newPositionToItem);
        DiffUtil.calculateDiff(callback).dispatchUpdatesTo((RecyclerView.Adapter) this.listViewAdapter);
        AndroidUtilities.updateVisibleRows(this.listView);
    }

    private class DiffCallback extends DiffUtil.Callback {
        SparseIntArray newPositionToItem;
        int oldAdminsEndRow;
        int oldAdminsStartRow;
        ArrayList<TLRPC.TL_chatInviteExported> oldLinks;
        int oldLinksEndRow;
        int oldLinksStartRow;
        SparseIntArray oldPositionToItem;
        ArrayList<TLRPC.TL_chatInviteExported> oldRevokedLinks;
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

        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            TLRPC.TL_chatInviteExported newItem;
            TLRPC.TL_chatInviteExported oldItem;
            if (((oldItemPosition >= this.oldLinksStartRow && oldItemPosition < this.oldLinksEndRow) || (oldItemPosition >= this.oldRevokedLinksStartRow && oldItemPosition < this.oldRevokedLinksEndRow)) && ((newItemPosition >= ManageLinksActivity.this.linksStartRow && newItemPosition < ManageLinksActivity.this.linksEndRow) || (newItemPosition >= ManageLinksActivity.this.revokedLinksStartRow && newItemPosition < ManageLinksActivity.this.revokedLinksEndRow))) {
                if (newItemPosition < ManageLinksActivity.this.linksStartRow || newItemPosition >= ManageLinksActivity.this.linksEndRow) {
                    newItem = (TLRPC.TL_chatInviteExported) ManageLinksActivity.this.revokedInvites.get(newItemPosition - ManageLinksActivity.this.revokedLinksStartRow);
                } else {
                    newItem = (TLRPC.TL_chatInviteExported) ManageLinksActivity.this.invites.get(newItemPosition - ManageLinksActivity.this.linksStartRow);
                }
                int i = this.oldLinksStartRow;
                if (oldItemPosition < i || oldItemPosition >= this.oldLinksEndRow) {
                    oldItem = this.oldRevokedLinks.get(oldItemPosition - this.oldRevokedLinksStartRow);
                } else {
                    oldItem = this.oldLinks.get(oldItemPosition - i);
                }
                return oldItem.link.equals(newItem.link);
            } else if (oldItemPosition < this.oldAdminsStartRow || oldItemPosition >= this.oldAdminsEndRow || newItemPosition < ManageLinksActivity.this.adminsStartRow || newItemPosition >= ManageLinksActivity.this.adminsEndRow) {
                int oldItem2 = this.oldPositionToItem.get(oldItemPosition, -1);
                int newItem2 = this.newPositionToItem.get(newItemPosition, -1);
                if (oldItem2 < 0 || oldItem2 != newItem2) {
                    return false;
                }
                return true;
            } else if (oldItemPosition - this.oldAdminsStartRow == newItemPosition - ManageLinksActivity.this.adminsStartRow) {
                return true;
            } else {
                return false;
            }
        }

        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return areItemsTheSame(oldItemPosition, newItemPosition);
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            int pointer = 0 + 1;
            put(pointer, ManageLinksActivity.this.helpRow, sparseIntArray);
            int pointer2 = pointer + 1;
            put(pointer2, ManageLinksActivity.this.permanentLinkHeaderRow, sparseIntArray);
            int pointer3 = pointer2 + 1;
            put(pointer3, ManageLinksActivity.this.permanentLinkRow, sparseIntArray);
            int pointer4 = pointer3 + 1;
            put(pointer4, ManageLinksActivity.this.dividerRow, sparseIntArray);
            int pointer5 = pointer4 + 1;
            put(pointer5, ManageLinksActivity.this.createNewLinkRow, sparseIntArray);
            int pointer6 = pointer5 + 1;
            put(pointer6, ManageLinksActivity.this.revokedHeader, sparseIntArray);
            int pointer7 = pointer6 + 1;
            put(pointer7, ManageLinksActivity.this.revokeAllRow, sparseIntArray);
            int pointer8 = pointer7 + 1;
            put(pointer8, ManageLinksActivity.this.createLinkHelpRow, sparseIntArray);
            int pointer9 = pointer8 + 1;
            put(pointer9, ManageLinksActivity.this.creatorRow, sparseIntArray);
            int pointer10 = pointer9 + 1;
            put(pointer10, ManageLinksActivity.this.creatorDividerRow, sparseIntArray);
            int pointer11 = pointer10 + 1;
            put(pointer11, ManageLinksActivity.this.adminsHeaderRow, sparseIntArray);
            int pointer12 = pointer11 + 1;
            put(pointer12, ManageLinksActivity.this.linksHeaderRow, sparseIntArray);
            put(pointer12 + 1, ManageLinksActivity.this.linksLoadingRow, sparseIntArray);
        }

        private void put(int id, int position, SparseIntArray sparseIntArray) {
            if (position >= 0) {
                sparseIntArray.put(position, id);
            }
        }
    }

    /* access modifiers changed from: private */
    public DiffCallback saveListState() {
        DiffCallback callback = new DiffCallback();
        callback.fillPositions(callback.oldPositionToItem);
        callback.oldLinksStartRow = this.linksStartRow;
        callback.oldLinksEndRow = this.linksEndRow;
        callback.oldRevokedLinksStartRow = this.revokedLinksStartRow;
        callback.oldRevokedLinksEndRow = this.revokedLinksEndRow;
        callback.oldAdminsStartRow = this.adminsStartRow;
        callback.oldAdminsEndRow = this.adminsEndRow;
        callback.oldRowCount = this.rowCount;
        callback.oldLinks.clear();
        callback.oldLinks.addAll(this.invites);
        callback.oldRevokedLinks.clear();
        callback.oldRevokedLinks.addAll(this.revokedInvites);
        return callback;
    }

    public void fixDate(TLRPC.TL_chatInviteExported edited) {
        boolean z = true;
        if (edited.expire_date > 0) {
            if (getConnectionsManager().getCurrentTime() < edited.expire_date) {
                z = false;
            }
            edited.expired = z;
        } else if (edited.usage_limit > 0) {
            if (edited.usage < edited.usage_limit) {
                z = false;
            }
            edited.expired = z;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ManageLinksActivity$$ExternalSyntheticLambda6(this);
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, LinkActionView.class, LinkCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteBlueText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundRed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundOrange"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundPink"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterMuted"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{LinkCell.class}, new String[]{"titleView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{LinkCell.class}, new String[]{"subtitleView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{LinkCell.class}, new String[]{"optionsView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menu"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$17$org-telegram-ui-ManageLinksActivity  reason: not valid java name */
    public /* synthetic */ void m3277x1210da6c() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) child).update(0);
                }
                if (child instanceof LinkActionView) {
                    ((LinkActionView) child).updateColors();
                }
            }
        }
        InviteLinkBottomSheet inviteLinkBottomSheet2 = this.inviteLinkBottomSheet;
        if (inviteLinkBottomSheet2 != null) {
            inviteLinkBottomSheet2.updateColors();
        }
    }

    public boolean needDelayOpenAnimation() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        InviteLinkBottomSheet inviteLinkBottomSheet2;
        super.onTransitionAnimationEnd(isOpen, backward);
        if (isOpen) {
            this.isOpened = true;
            if (backward && (inviteLinkBottomSheet2 = this.inviteLinkBottomSheet) != null && inviteLinkBottomSheet2.isNeedReopen) {
                this.inviteLinkBottomSheet.show();
            }
        }
        NotificationCenter.getInstance(this.currentAccount).onAnimationFinish(this.animationIndex);
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        super.onTransitionAnimationStart(isOpen, backward);
        this.animationIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.animationIndex, (int[]) null);
    }
}
