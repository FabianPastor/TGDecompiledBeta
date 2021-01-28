package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewTreeObserver;
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
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
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
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_deleteExportedChatInvite;
import org.telegram.tgnet.TLRPC$TL_messages_deleteRevokedExportedChatInvites;
import org.telegram.tgnet.TLRPC$TL_messages_editExportedChatInvite;
import org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite;
import org.telegram.tgnet.TLRPC$TL_messages_exportedChatInvites;
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
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.DotDividerSpan;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.InviteLinkBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkActionView;
import org.telegram.ui.Components.LoadingStickerDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TimerParticles;
import org.telegram.ui.LinkEditActivity;
import org.telegram.ui.ManageLinksActivity;

public class ManageLinksActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int createLinkHelpRow;
    /* access modifiers changed from: private */
    public int createNewLinkRow;
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
    public ArrayList<TLRPC$TL_chatInviteExported> invites;
    /* access modifiers changed from: private */
    public boolean isPublic;
    /* access modifiers changed from: private */
    public int lastDivider;
    /* access modifiers changed from: private */
    public final LinkEditActivity.Callback linkEditActivityCallback;
    Drawable linkIcon;
    Drawable linkIconRevoked;
    /* access modifiers changed from: private */
    public int linksEndRow;
    boolean linksLoading;
    /* access modifiers changed from: private */
    public int linksLoadingRow;
    /* access modifiers changed from: private */
    public int linksStartRow;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private ListAdapter listViewAdapter;
    boolean loadRevoked;
    /* access modifiers changed from: private */
    public int permanentLinkHeaderRow;
    /* access modifiers changed from: private */
    public int permanentLinkRow;
    /* access modifiers changed from: private */
    public int revokeAllDivider;
    /* access modifiers changed from: private */
    public int revokeAllRow;
    /* access modifiers changed from: private */
    public int revokedDivider;
    /* access modifiers changed from: private */
    public int revokedHeader;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$TL_chatInviteExported> revokedInvites;
    /* access modifiers changed from: private */
    public int revokedLinksEndRow;
    /* access modifiers changed from: private */
    public int revokedLinksStartRow;
    /* access modifiers changed from: private */
    public int rowCount;
    long timeDif;
    /* access modifiers changed from: private */
    public HashMap<Integer, TLRPC$User> users;

    public void didReceivedNotification(int i, int i2, Object... objArr) {
    }

    private static class EmptyView extends LinearLayout implements NotificationCenter.NotificationCenterDelegate {
        private int currentAccount = UserConfig.selectedAccount;
        private LoadingStickerDrawable drawable;
        private BackupImageView stickerView;

        public EmptyView(Context context) {
            super(context);
            setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
            setOrientation(1);
            this.stickerView = new BackupImageView(context);
            LoadingStickerDrawable loadingStickerDrawable = new LoadingStickerDrawable(this.stickerView, "M476.1,397.4CLASSNAME.8-47.2,0.3-105.9-50.9-120c-2.5-6.9-7.8-12.7-15-16.4l0.4-229.4c0-12.3-10-22.4-22.4-22.4H128.5c-12.3,0-22.4,10-22.4,22.4l-0.4,229.8v0c0,6.7,2.9,12.6,7.6,16.7c-51.6,15.9-79.2,77.2-48.1,116.4c-8.7,11.7-13.4,27.5-14,47.2c-1.7,34.5,21.6,45.8,55.9,45.8CLASSNAME.3,0,99.1,4.6,105.1-36.2CLASSNAME.5,0.9,7.1-37.3-6.5-53.3CLASSNAME.4-22.4,18.3-52.9,4.9-78.2c-0.7-5.3-3.8-9.8-8.1-12.6c-1.5-2-1.6-2-2.1-2.7c0.2-1,1.2-11.8-3.4-20.9h138.5c-4.8,8.8-4.7,17-2.9,22.1c-5.3,4.8-6.8,12.3-5.2,17c-11.4,24.9-10,53.8,4.3,77.5c-6.8,9.7-11.2,21.7-12.6,31.6c-0.2-0.2-0.4-0.3-0.6-0.5c0.8-3.3,0.4-6.4-1.3-7.8c9.3-12.1-4.5-29.2-17-21.7c-3.8-2.8-10.6-3.2-18.1-0.5c-2.4-10.6-21.1-10.6-28.6-1c-1.3,0.3-2.9,0.8-4.5,1.9c-5.2-0.9-10.9,0.1-14.1,4.4c-6.9,3-9.5,10.4-7.8,17c-0.9,1.8-1.1,4-0.8,6.3c-1.6,1.2-2.3,3.1-2,4.9c0.1,0.6,10.4,56.6,11.2,62c0.3,1.8,1.5,3.2,3.1,3.9c8.7,3.4,12,3.8,30.1,9.4c2.7,0.8,2.4,0.8,6.7-0.1CLASSNAME.4-3.5,30.2-8.9,30.8-9.2c1.6-0.6,2.7-2,3.1-3.7c0.1-0.4,6.8-36.5,10-53.2c0.9,4.2,3.3,7.3,7.4,7.5c1.2,7.8,4.4,14.5,9.5,19.9CLASSNAME.4,17.3,44.9,15.7,64.9,16.1CLASSNAME.3,0.8,74.5,1.5,84.4-24.4CLASSNAME.9,453.5,491.3,421.3,476.1,397.4z", AndroidUtilities.dp(104.0f), AndroidUtilities.dp(104.0f));
            this.drawable = loadingStickerDrawable;
            this.stickerView.setImageDrawable(loadingStickerDrawable);
            addView(this.stickerView, LayoutHelper.createLinear(104, 104, 49, 0, 2, 0, 0));
        }

        private void setSticker() {
            TLRPC$TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByName("UtyaDuck");
            if (stickerSetByName == null) {
                stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName("UtyaDuck");
            }
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = stickerSetByName;
            if (tLRPC$TL_messages_stickerSet == null || tLRPC$TL_messages_stickerSet.documents.size() < 34) {
                MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("UtyaDuck", false, tLRPC$TL_messages_stickerSet == null);
                this.stickerView.setImageDrawable(this.drawable);
                return;
            }
            this.stickerView.setImage(ImageLocation.getForDocument(tLRPC$TL_messages_stickerSet.documents.get(33)), "104_104", "tgs", (Drawable) this.drawable, (Object) tLRPC$TL_messages_stickerSet);
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
            if (i == NotificationCenter.diceStickersDidLoad && "UtyaDuck".equals(objArr[0])) {
                setSticker();
            }
        }
    }

    /* access modifiers changed from: private */
    public void loadLinks() {
        TLRPC$TL_messages_getExportedChatInvites tLRPC$TL_messages_getExportedChatInvites = new TLRPC$TL_messages_getExportedChatInvites();
        tLRPC$TL_messages_getExportedChatInvites.peer = getMessagesController().getInputPeer(-this.currentChatId);
        tLRPC$TL_messages_getExportedChatInvites.admin_id = getMessagesController().getInputUser(getUserConfig().getCurrentUser());
        boolean z = this.loadRevoked;
        if (z) {
            tLRPC$TL_messages_getExportedChatInvites.revoked = true;
            if (!this.revokedInvites.isEmpty()) {
                tLRPC$TL_messages_getExportedChatInvites.flags |= 4;
                ArrayList<TLRPC$TL_chatInviteExported> arrayList = this.revokedInvites;
                tLRPC$TL_messages_getExportedChatInvites.offset_link = arrayList.get(arrayList.size() - 1).link;
            }
        } else if (!this.invites.isEmpty()) {
            tLRPC$TL_messages_getExportedChatInvites.flags |= 4;
            ArrayList<TLRPC$TL_chatInviteExported> arrayList2 = this.invites;
            tLRPC$TL_messages_getExportedChatInvites.offset_link = arrayList2.get(arrayList2.size() - 1).link;
        }
        this.linksLoading = true;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getExportedChatInvites, new RequestDelegate(this.isPublic ? null : this.invite, z) {
            public final /* synthetic */ TLRPC$TL_chatInviteExported f$1;
            public final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ManageLinksActivity.this.lambda$loadLinks$2$ManageLinksActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadLinks$2 */
    public /* synthetic */ void lambda$loadLinks$2$ManageLinksActivity(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                        ManageLinksActivity.this.lambda$null$1$ManageLinksActivity(this.f$1, this.f$2, this.f$3, this.f$4);
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
                ManageLinksActivity.this.lambda$null$1$ManageLinksActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$1 */
    public /* synthetic */ void lambda$null$1$ManageLinksActivity(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
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
                ManageLinksActivity.this.lambda$null$0$ManageLinksActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$0 */
    public /* synthetic */ void lambda$null$0$ManageLinksActivity(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        boolean z2 = false;
        this.linksLoading = false;
        this.hasMore = false;
        if (tLRPC$TL_chatInviteExported != null) {
            this.invite = tLRPC$TL_chatInviteExported;
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            if (tLRPC$ChatFull != null) {
                tLRPC$ChatFull.exported_invite = tLRPC$TL_chatInviteExported;
            }
        }
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_exportedChatInvites tLRPC$TL_messages_exportedChatInvites = (TLRPC$TL_messages_exportedChatInvites) tLObject;
            if (z) {
                for (int i = 0; i < tLRPC$TL_messages_exportedChatInvites.invites.size(); i++) {
                    TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported2 = (TLRPC$TL_chatInviteExported) tLRPC$TL_messages_exportedChatInvites.invites.get(i);
                    fixDate(tLRPC$TL_chatInviteExported2);
                    this.revokedInvites.add(tLRPC$TL_chatInviteExported2);
                }
            } else {
                for (int i2 = 0; i2 < tLRPC$TL_messages_exportedChatInvites.invites.size(); i2++) {
                    TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported3 = (TLRPC$TL_chatInviteExported) tLRPC$TL_messages_exportedChatInvites.invites.get(i2);
                    fixDate(tLRPC$TL_chatInviteExported3);
                    this.invites.add(tLRPC$TL_chatInviteExported3);
                }
            }
            for (int i3 = 0; i3 < tLRPC$TL_messages_exportedChatInvites.users.size(); i3++) {
                this.users.put(Integer.valueOf(tLRPC$TL_messages_exportedChatInvites.users.get(i3).id), tLRPC$TL_messages_exportedChatInvites.users.get(i3));
            }
            int i4 = this.rowCount;
            if (tLRPC$TL_messages_exportedChatInvites.invites.size() == 0) {
                this.hasMore = false;
            } else if (z) {
                if (this.revokedInvites.size() + 1 < tLRPC$TL_messages_exportedChatInvites.count) {
                    z2 = true;
                }
                this.hasMore = z2;
            } else {
                if (this.invites.size() + 1 < tLRPC$TL_messages_exportedChatInvites.count) {
                    z2 = true;
                }
                this.hasMore = z2;
            }
            if (tLRPC$TL_messages_exportedChatInvites.invites.size() > 0 || this.loadRevoked) {
                showItemsAnimated(i4 - 1);
            }
            if (!this.hasMore && !this.loadRevoked) {
                this.hasMore = true;
                this.loadRevoked = true;
                loadLinks();
            }
            updateRows(true);
            TLRPC$ChatFull tLRPC$ChatFull2 = this.info;
            if (tLRPC$ChatFull2 != null && !z) {
                tLRPC$ChatFull2.invitesCount = tLRPC$TL_messages_exportedChatInvites.count;
                getMessagesStorage().saveChatLinksCount(this.currentChatId, this.info.invitesCount);
            }
        }
    }

    private void updateRows(boolean z) {
        TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.currentChatId));
        this.currentChat = chat;
        if (chat != null) {
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
            this.rowCount = 0;
            int i = 0 + 1;
            this.rowCount = i;
            this.helpRow = 0;
            int i2 = i + 1;
            this.rowCount = i2;
            this.permanentLinkHeaderRow = i;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.permanentLinkRow = i2;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.dividerRow = i3;
            this.rowCount = i4 + 1;
            this.createNewLinkRow = i4;
            if (!this.invites.isEmpty()) {
                int i5 = this.rowCount;
                this.linksStartRow = i5;
                int size = i5 + this.invites.size();
                this.rowCount = size;
                this.linksEndRow = size;
            }
            if (!this.revokedInvites.isEmpty()) {
                int i6 = this.rowCount;
                int i7 = i6 + 1;
                this.rowCount = i7;
                this.revokedDivider = i6;
                int i8 = i7 + 1;
                this.rowCount = i8;
                this.revokedHeader = i7;
                this.revokedLinksStartRow = i8;
                int size2 = i8 + this.revokedInvites.size();
                this.rowCount = size2;
                this.revokedLinksEndRow = size2;
                int i9 = size2 + 1;
                this.rowCount = i9;
                this.revokeAllDivider = size2;
                this.rowCount = i9 + 1;
                this.revokeAllRow = i9;
            }
            if (this.linksLoading || this.hasMore) {
                int i10 = this.rowCount;
                this.rowCount = i10 + 1;
                this.linksLoadingRow = i10;
            }
            if (this.invites.isEmpty() && this.revokedInvites.isEmpty() && !this.linksLoading) {
                int i11 = this.rowCount;
                this.rowCount = i11 + 1;
                this.createLinkHelpRow = i11;
            }
            if (!this.invites.isEmpty() || !this.revokedInvites.isEmpty()) {
                int i12 = this.rowCount;
                this.rowCount = i12 + 1;
                this.lastDivider = i12;
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
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView.setTag("windowBackgroundGray");
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context);
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
                        ManageLinksActivity.this.loadLinks();
                    }
                }
            }
        });
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.listView.setItemAnimator(defaultItemAnimator);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(context) {
            public final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onItemClick(View view, int i) {
                ManageLinksActivity.this.lambda$createView$6$ManageLinksActivity(this.f$1, view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return ManageLinksActivity.this.lambda$createView$7$ManageLinksActivity(view, i);
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
    /* renamed from: lambda$createView$6 */
    public /* synthetic */ void lambda$createView$6$ManageLinksActivity(Context context, View view, int i) {
        if (i == this.createNewLinkRow) {
            LinkEditActivity linkEditActivity = new LinkEditActivity(0, this.currentChatId);
            linkEditActivity.setCallback(this.linkEditActivityCallback);
            presentFragment(linkEditActivity);
            return;
        }
        int i2 = this.linksStartRow;
        if (i < i2 || i >= this.linksEndRow) {
            int i3 = this.revokedLinksStartRow;
            if (i >= i3 && i < this.revokedLinksEndRow) {
                InviteLinkBottomSheet inviteLinkBottomSheet2 = new InviteLinkBottomSheet(context, this.revokedInvites.get(i - i3), this.info, this.users, this, this.currentChatId, false);
                this.inviteLinkBottomSheet = inviteLinkBottomSheet2;
                inviteLinkBottomSheet2.show();
            } else if (i == this.revokeAllRow && !this.deletingRevokedLinks) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("DeleteAllRevokedLinks", NUM));
                builder.setMessage(LocaleController.getString("DeleteAllRevokedLinkHelp", NUM));
                builder.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ManageLinksActivity.this.lambda$null$5$ManageLinksActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            }
        } else {
            InviteLinkBottomSheet inviteLinkBottomSheet3 = new InviteLinkBottomSheet(context, this.invites.get(i - i2), this.info, this.users, this, this.currentChatId, false);
            this.inviteLinkBottomSheet = inviteLinkBottomSheet3;
            inviteLinkBottomSheet3.show();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$5 */
    public /* synthetic */ void lambda$null$5$ManageLinksActivity(DialogInterface dialogInterface, int i) {
        TLRPC$TL_messages_deleteRevokedExportedChatInvites tLRPC$TL_messages_deleteRevokedExportedChatInvites = new TLRPC$TL_messages_deleteRevokedExportedChatInvites();
        tLRPC$TL_messages_deleteRevokedExportedChatInvites.peer = getMessagesController().getInputPeer(-this.currentChatId);
        this.deletingRevokedLinks = true;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_deleteRevokedExportedChatInvites, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ManageLinksActivity.this.lambda$null$4$ManageLinksActivity(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$4 */
    public /* synthetic */ void lambda$null$4$ManageLinksActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            public final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ManageLinksActivity.this.lambda$null$3$ManageLinksActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$3 */
    public /* synthetic */ void lambda$null$3$ManageLinksActivity(TLRPC$TL_error tLRPC$TL_error) {
        this.deletingRevokedLinks = false;
        if (tLRPC$TL_error == null) {
            DiffCallback saveListState = saveListState();
            this.revokedInvites.clear();
            updateRows(false);
            saveListState.fillPositions(saveListState.newPositionToItem);
            DiffUtil.calculateDiff(saveListState).dispatchUpdatesTo((RecyclerView.Adapter) this.listViewAdapter);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$7 */
    public /* synthetic */ boolean lambda$createView$7$ManageLinksActivity(View view, int i) {
        if ((i < this.linksStartRow || i >= this.linksEndRow) && (i < this.revokedLinksStartRow || i >= this.revokedLinksEndRow)) {
            return false;
        }
        ((LinkCell) view).optionsView.callOnClick();
        view.performHapticFeedback(0, 2);
        return true;
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

        public HintInnerCell(ManageLinksActivity manageLinksActivity, Context context) {
            super(context);
            EmptyView emptyView2 = new EmptyView(context);
            this.emptyView = emptyView2;
            addView(emptyView2, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 10.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.messageTextView = textView;
            textView.setTextColor(Theme.getColor("chats_message"));
            this.messageTextView.setTextSize(1, 14.0f);
            this.messageTextView.setGravity(17);
            this.messageTextView.setText(LocaleController.getString("PrimaryLinkHelp", NUM));
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
            if (ManageLinksActivity.this.createNewLinkRow == adapterPosition) {
                return true;
            }
            if (adapterPosition >= ManageLinksActivity.this.linksStartRow && adapterPosition < ManageLinksActivity.this.linksEndRow) {
                return true;
            }
            if ((adapterPosition < ManageLinksActivity.this.revokedLinksStartRow || adapterPosition >= ManageLinksActivity.this.revokedLinksEndRow) && adapterPosition != ManageLinksActivity.this.revokeAllRow) {
                return false;
            }
            return true;
        }

        public int getItemCount() {
            return ManageLinksActivity.this.rowCount;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v2, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v4, resolved type: org.telegram.ui.ManageLinksActivity$TextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v6, resolved type: org.telegram.ui.ManageLinksActivity$LinkCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v7, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v8, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v9, resolved type: org.telegram.ui.Cells.TextSettingsCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v10, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v11, resolved type: org.telegram.ui.ManageLinksActivity$HintInnerCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v12, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v13, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v14, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v15, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v16, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v17, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v18, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v19, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v20, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX WARNING: type inference failed for: r12v1, types: [android.view.View] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r11, int r12) {
            /*
                r10 = this;
                java.lang.String r11 = "windowBackgroundGrayShadow"
                r0 = 0
                r1 = 1
                r2 = 2131165449(0x7var_, float:1.7945115E38)
                java.lang.String r3 = "windowBackgroundWhite"
                switch(r12) {
                    case 1: goto L_0x00d5;
                    case 2: goto L_0x00b2;
                    case 3: goto L_0x00a3;
                    case 4: goto L_0x009b;
                    case 5: goto L_0x0091;
                    case 6: goto L_0x0077;
                    case 7: goto L_0x0066;
                    case 8: goto L_0x0040;
                    case 9: goto L_0x0022;
                    default: goto L_0x000e;
                }
            L_0x000e:
                org.telegram.ui.ManageLinksActivity$HintInnerCell r12 = new org.telegram.ui.ManageLinksActivity$HintInnerCell
                org.telegram.ui.ManageLinksActivity r11 = org.telegram.ui.ManageLinksActivity.this
                android.content.Context r0 = r10.mContext
                r12.<init>(r11, r0)
                android.content.Context r11 = r10.mContext
                android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r11, (int) r2, (java.lang.String) r3)
                r12.setBackgroundDrawable(r11)
                goto L_0x00e5
            L_0x0022:
                org.telegram.ui.Cells.TextInfoPrivacyCell r12 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r0 = r10.mContext
                r12.<init>(r0)
                r0 = 2131624964(0x7f0e0404, float:1.8877123E38)
                java.lang.String r1 = "CreateNewLinkHelp"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r12.setText(r0)
                android.content.Context r0 = r10.mContext
                android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r0, (int) r2, (java.lang.String) r11)
                r12.setBackground(r11)
                goto L_0x00e5
            L_0x0040:
                org.telegram.ui.Cells.TextSettingsCell r12 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r11 = r10.mContext
                r12.<init>(r11)
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r12.setBackgroundColor(r11)
                r11 = 2131625039(0x7f0e044f, float:1.8877275E38)
                java.lang.String r1 = "DeleteAllRevokedLinks"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r1, r11)
                r12.setText(r11, r0)
                java.lang.String r11 = "windowBackgroundWhiteRedText5"
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                r12.setTextColor(r11)
                goto L_0x00e5
            L_0x0066:
                org.telegram.ui.Cells.ShadowSectionCell r12 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r0 = r10.mContext
                r12.<init>(r0)
                android.content.Context r0 = r10.mContext
                android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r0, (int) r2, (java.lang.String) r11)
                r12.setBackground(r11)
                goto L_0x00e5
            L_0x0077:
                org.telegram.ui.Components.FlickerLoadingView r12 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r11 = r10.mContext
                r12.<init>(r11)
                r12.setIsSingleCell(r1)
                r11 = 9
                r12.setViewType(r11)
                r12.showDate(r0)
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r12.setBackgroundColor(r11)
                goto L_0x00e5
            L_0x0091:
                org.telegram.ui.ManageLinksActivity$LinkCell r12 = new org.telegram.ui.ManageLinksActivity$LinkCell
                org.telegram.ui.ManageLinksActivity r11 = org.telegram.ui.ManageLinksActivity.this
                android.content.Context r0 = r10.mContext
                r12.<init>(r0)
                goto L_0x00e5
            L_0x009b:
                org.telegram.ui.Cells.ShadowSectionCell r12 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r11 = r10.mContext
                r12.<init>(r11)
                goto L_0x00e5
            L_0x00a3:
                org.telegram.ui.ManageLinksActivity$TextCell r12 = new org.telegram.ui.ManageLinksActivity$TextCell
                android.content.Context r11 = r10.mContext
                r12.<init>(r11)
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r12.setBackgroundColor(r11)
                goto L_0x00e5
            L_0x00b2:
                org.telegram.ui.Components.LinkActionView r12 = new org.telegram.ui.Components.LinkActionView
                android.content.Context r5 = r10.mContext
                org.telegram.ui.ManageLinksActivity r6 = org.telegram.ui.ManageLinksActivity.this
                r7 = 0
                int r8 = r6.currentChatId
                r9 = 1
                r4 = r12
                r4.<init>(r5, r6, r7, r8, r9)
                r12.setPermanent(r1)
                org.telegram.ui.ManageLinksActivity$ListAdapter$1 r11 = new org.telegram.ui.ManageLinksActivity$ListAdapter$1
                r11.<init>(r12)
                r12.setDelegate(r11)
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r12.setBackgroundColor(r11)
                goto L_0x00e5
            L_0x00d5:
                org.telegram.ui.Cells.HeaderCell r12 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r11 = r10.mContext
                r0 = 23
                r12.<init>(r11, r0)
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r12.setBackgroundColor(r11)
            L_0x00e5:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r11 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r1 = -2
                r11.<init>((int) r0, (int) r1)
                r12.setLayoutParams(r11)
                org.telegram.ui.Components.RecyclerListView$Holder r11 = new org.telegram.ui.Components.RecyclerListView$Holder
                r11.<init>(r12)
                return r11
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ManageLinksActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 1) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == ManageLinksActivity.this.permanentLinkHeaderRow) {
                    headerCell.setText(LocaleController.getString("ChannelLinkTitle", NUM));
                } else {
                    headerCell.setText(LocaleController.getString("RevokedLinks", NUM));
                }
            } else if (itemViewType == 2) {
                LinkActionView linkActionView = (LinkActionView) viewHolder.itemView;
                if (!ManageLinksActivity.this.isPublic) {
                    linkActionView.setPublic(false);
                    if (ManageLinksActivity.this.invite != null) {
                        TLRPC$TL_chatInviteExported access$1100 = ManageLinksActivity.this.invite;
                        linkActionView.setLink(access$1100.link);
                        linkActionView.loadUsers(access$1100, ManageLinksActivity.this.currentChatId);
                        return;
                    }
                    linkActionView.setLink((String) null);
                } else if (ManageLinksActivity.this.info != null) {
                    linkActionView.setLink("https://t.me/" + ManageLinksActivity.this.currentChat.username);
                    linkActionView.setUsers(0, (ArrayList<TLRPC$User>) null);
                    linkActionView.setPublic(true);
                }
            } else if (itemViewType == 3) {
                Drawable drawable = this.mContext.getResources().getDrawable(NUM);
                Drawable drawable2 = this.mContext.getResources().getDrawable(NUM);
                drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("switchTrackChecked"), PorterDuff.Mode.MULTIPLY));
                drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("checkboxCheck"), PorterDuff.Mode.MULTIPLY));
                ((TextCell) viewHolder.itemView).setTextAndIcon(LocaleController.getString("CreateNewLink", NUM), new CombinedDrawable(drawable, drawable2), false);
            } else if (itemViewType == 5) {
                if (i < ManageLinksActivity.this.linksStartRow || i >= ManageLinksActivity.this.linksEndRow) {
                    tLRPC$TL_chatInviteExported = (TLRPC$TL_chatInviteExported) ManageLinksActivity.this.revokedInvites.get(i - ManageLinksActivity.this.revokedLinksStartRow);
                } else {
                    tLRPC$TL_chatInviteExported = (TLRPC$TL_chatInviteExported) ManageLinksActivity.this.invites.get(i - ManageLinksActivity.this.linksStartRow);
                }
                ((LinkCell) viewHolder.itemView).setLink(tLRPC$TL_chatInviteExported, i - ManageLinksActivity.this.linksStartRow);
            }
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
            if (i == ManageLinksActivity.this.permanentLinkHeaderRow || i == ManageLinksActivity.this.revokedHeader) {
                return 1;
            }
            if (i == ManageLinksActivity.this.permanentLinkRow) {
                return 2;
            }
            if (i == ManageLinksActivity.this.createNewLinkRow) {
                return 3;
            }
            if (i == ManageLinksActivity.this.dividerRow || i == ManageLinksActivity.this.revokedDivider || i == ManageLinksActivity.this.revokeAllDivider) {
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
            return 1;
        }
    }

    /* access modifiers changed from: private */
    public void revokePermanent() {
        TLRPC$TL_messages_exportChatInvite tLRPC$TL_messages_exportChatInvite = new TLRPC$TL_messages_exportChatInvite();
        tLRPC$TL_messages_exportChatInvite.peer = getMessagesController().getInputPeer(-this.currentChatId);
        TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = this.invite;
        this.invite = null;
        this.info.exported_invite = null;
        this.listViewAdapter.notifyItemChanged(this.permanentLinkRow);
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_exportChatInvite, new RequestDelegate(tLRPC$TL_chatInviteExported) {
            public final /* synthetic */ TLRPC$TL_chatInviteExported f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ManageLinksActivity.this.lambda$revokePermanent$9$ManageLinksActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        }), this.classGuid);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$revokePermanent$9 */
    public /* synthetic */ void lambda$revokePermanent$9$ManageLinksActivity(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                ManageLinksActivity.this.lambda$null$8$ManageLinksActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$8 */
    public /* synthetic */ void lambda$null$8$ManageLinksActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported2 = (TLRPC$TL_chatInviteExported) tLObject;
            this.invite = tLRPC$TL_chatInviteExported2;
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            if (tLRPC$ChatFull != null) {
                tLRPC$ChatFull.exported_invite = tLRPC$TL_chatInviteExported2;
            }
            if (getParentActivity() != null) {
                this.listViewAdapter.notifyItemChanged(this.permanentLinkRow);
                tLRPC$TL_chatInviteExported.revoked = true;
                DiffCallback saveListState = saveListState();
                this.revokedInvites.add(0, tLRPC$TL_chatInviteExported);
                updateRows(false);
                saveListState.fillPositions(saveListState.newPositionToItem);
                DiffUtil.calculateDiff(saveListState).dispatchUpdatesTo((RecyclerView.Adapter) this.listViewAdapter);
                AndroidUtilities.updateVisibleRows(this.listView);
            }
        }
    }

    public static class TextCell extends FrameLayout {
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

        public void setTextAndIcon(String str, Drawable drawable, boolean z) {
            this.textView.setText(str);
            this.imageView.setImageDrawable(drawable);
        }
    }

    private class LinkCell extends FrameLayout {
        int animateFromState;
        boolean animateHideExpiring;
        float animateToStateProgress = 1.0f;
        TLRPC$TL_chatInviteExported invite;
        float lastDrawExpringProgress;
        int lastDrawingState;
        ImageView optionsView;
        Paint paint = new Paint(1);
        Paint paint2 = new Paint(1);
        RectF rectF = new RectF();
        TextView subtitleView;
        private TimerParticles timerParticles = new TimerParticles();
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
            this.optionsView.setColorFilter(Theme.getColor("dialogTextGray3"));
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
        /* renamed from: lambda$new$3 */
        public /* synthetic */ void lambda$new$3$ManageLinksActivity$LinkCell(View view) {
            if (this.invite != null) {
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                ArrayList arrayList3 = new ArrayList();
                if (this.invite.revoked) {
                    arrayList.add(LocaleController.getString("Delete", NUM));
                    arrayList2.add(NUM);
                    arrayList3.add(4);
                } else {
                    arrayList.add(LocaleController.getString("Copy", NUM));
                    arrayList2.add(NUM);
                    arrayList3.add(0);
                    arrayList.add(LocaleController.getString("Share", NUM));
                    arrayList2.add(NUM);
                    arrayList3.add(1);
                    arrayList.add(LocaleController.getString("Edit", NUM));
                    arrayList2.add(NUM);
                    arrayList3.add(2);
                    arrayList.add(LocaleController.getString("RevokeLink", NUM));
                    arrayList2.add(NUM);
                    arrayList3.add(3);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) ManageLinksActivity.this.getParentActivity());
                builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), AndroidUtilities.toIntArray(arrayList2), new DialogInterface.OnClickListener(arrayList3) {
                    public final /* synthetic */ ArrayList f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ManageLinksActivity.LinkCell.this.lambda$null$2$ManageLinksActivity$LinkCell(this.f$1, dialogInterface, i);
                    }
                });
                builder.setTitle(LocaleController.getString("InviteLink", NUM));
                AlertDialog create = builder.create();
                builder.show();
                create.setItemColor(arrayList.size() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$2 */
        public /* synthetic */ void lambda$null$2$ManageLinksActivity$LinkCell(ArrayList arrayList, DialogInterface dialogInterface, int i) {
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
                        ManageLinksActivity.LinkCell.this.lambda$null$0$ManageLinksActivity$LinkCell(this.f$1, dialogInterface, i);
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
                        ManageLinksActivity.LinkCell.this.lambda$null$1$ManageLinksActivity$LinkCell(this.f$1, dialogInterface, i);
                    }
                });
                builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                ManageLinksActivity.this.showDialog(builder2.create());
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$0 */
        public /* synthetic */ void lambda$null$0$ManageLinksActivity$LinkCell(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, DialogInterface dialogInterface, int i) {
            ManageLinksActivity.this.revokeLink(tLRPC$TL_chatInviteExported);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$1 */
        public /* synthetic */ void lambda$null$1$ManageLinksActivity$LinkCell(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, DialogInterface dialogInterface, int i) {
            ManageLinksActivity.this.deleteLink(tLRPC$TL_chatInviteExported);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), NUM));
            this.paint2.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:48:0x00da, code lost:
            if (r4.revoked == false) goto L_0x00dc;
         */
        /* JADX WARNING: Removed duplicated region for block: B:69:0x01a0  */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x01c5  */
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
                r10 = 1065353216(0x3var_, float:1.0)
                if (r2 != 0) goto L_0x0058
                boolean r2 = r1.revoked
                if (r2 == 0) goto L_0x0021
                goto L_0x0058
            L_0x0021:
                int r1 = r1.expire_date
                if (r1 <= 0) goto L_0x0055
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
                if (r12 > 0) goto L_0x003f
                int r12 = r11.date
            L_0x003f:
                long r5 = (long) r12
                long r5 = r5 * r13
                long r1 = r1 - r5
                long r3 = r3 - r5
                float r1 = (float) r1
                float r2 = (float) r3
                float r1 = r1 / r2
                float r1 = r10 - r1
                r2 = 0
                int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r3 > 0) goto L_0x0053
                r2 = 1
                r11.expired = r2
                r3 = 3
                goto L_0x0060
            L_0x0053:
                r3 = 1
                goto L_0x0060
            L_0x0055:
                r1 = 0
                r3 = 0
                goto L_0x0060
            L_0x0058:
                boolean r1 = r1.revoked
                if (r1 == 0) goto L_0x005e
                r3 = 4
                goto L_0x005f
            L_0x005e:
                r3 = 3
            L_0x005f:
                r1 = 0
            L_0x0060:
                int r2 = r0.lastDrawingState
                if (r3 == r2) goto L_0x007e
                if (r2 < 0) goto L_0x007e
                r0.animateFromState = r2
                r4 = 0
                r0.animateToStateProgress = r4
                boolean r2 = r15.hasProgress(r2)
                if (r2 == 0) goto L_0x007b
                boolean r2 = r15.hasProgress(r3)
                if (r2 != 0) goto L_0x007b
                r2 = 1
                r0.animateHideExpiring = r2
                goto L_0x007e
            L_0x007b:
                r2 = 0
                r0.animateHideExpiring = r2
            L_0x007e:
                r0.lastDrawingState = r3
                float r2 = r0.animateToStateProgress
                int r4 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
                if (r4 == 0) goto L_0x0099
                r4 = 1032000111(0x3d83126f, float:0.064)
                float r2 = r2 + r4
                r0.animateToStateProgress = r2
                int r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
                if (r2 < 0) goto L_0x0096
                r0.animateToStateProgress = r10
                r2 = 0
                r0.animateHideExpiring = r2
                goto L_0x0099
            L_0x0096:
                r15.invalidate()
            L_0x0099:
                float r2 = r0.animateToStateProgress
                int r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
                if (r2 == 0) goto L_0x00b0
                int r2 = r0.animateFromState
                int r2 = r15.getColor(r2, r1)
                int r3 = r15.getColor(r3, r1)
                float r4 = r0.animateToStateProgress
                int r2 = androidx.core.graphics.ColorUtils.blendARGB(r2, r3, r4)
                goto L_0x00b4
            L_0x00b0:
                int r2 = r15.getColor(r3, r1)
            L_0x00b4:
                android.graphics.Paint r3 = r0.paint
                r3.setColor(r2)
                float r3 = (float) r8
                float r4 = (float) r9
                r5 = 1107296256(0x42000000, float:32.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r5
                r6 = 1073741824(0x40000000, float:2.0)
                float r5 = r5 / r6
                android.graphics.Paint r6 = r0.paint
                r7.drawCircle(r3, r4, r5, r6)
                boolean r3 = r0.animateHideExpiring
                if (r3 != 0) goto L_0x00dc
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r4 = r0.invite
                boolean r5 = r4.expired
                if (r5 != 0) goto L_0x0198
                int r5 = r4.expire_date
                if (r5 <= 0) goto L_0x0198
                boolean r4 = r4.revoked
                if (r4 != 0) goto L_0x0198
            L_0x00dc:
                if (r3 == 0) goto L_0x00e0
                float r1 = r0.lastDrawExpringProgress
            L_0x00e0:
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
                int r1 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
                if (r1 == 0) goto L_0x016b
                int r1 = r0.animateFromState
                boolean r1 = r15.hasProgress(r1)
                if (r1 == 0) goto L_0x011b
                boolean r1 = r0.animateHideExpiring
                if (r1 == 0) goto L_0x016b
            L_0x011b:
                r16.save()
                boolean r1 = r0.animateHideExpiring
                if (r1 == 0) goto L_0x0126
                float r1 = r0.animateToStateProgress
                float r10 = r10 - r1
                goto L_0x0129
            L_0x0126:
                float r1 = r0.animateToStateProgress
                r10 = r1
            L_0x0129:
                r3 = 4604480259023595110(0x3feNUM, double:0.7)
                r1 = 1050253722(0x3e99999a, float:0.3)
                float r1 = r1 * r10
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
                r2 = r16
                r5 = r12
                r6 = r10
                r1.draw(r2, r3, r4, r5, r6)
                r16.restore()
                goto L_0x018b
            L_0x016b:
                android.graphics.RectF r3 = r0.rectF
                r4 = -1028390912(0xffffffffc2b40000, float:-90.0)
                float r1 = -r11
                float r10 = r1 * r2
                r5 = 0
                android.graphics.Paint r6 = r0.paint2
                r1 = r16
                r2 = r3
                r3 = r4
                r4 = r10
                r1.drawArc(r2, r3, r4, r5, r6)
                org.telegram.ui.Components.TimerParticles r1 = r0.timerParticles
                android.graphics.Paint r3 = r0.paint2
                android.graphics.RectF r4 = r0.rectF
                r6 = 1065353216(0x3var_, float:1.0)
                r2 = r16
                r5 = r10
                r1.draw(r2, r3, r4, r5, r6)
            L_0x018b:
                org.telegram.ui.ManageLinksActivity r1 = org.telegram.ui.ManageLinksActivity.this
                boolean r1 = r1.isPaused
                if (r1 != 0) goto L_0x0196
                r15.invalidate()
            L_0x0196:
                r0.lastDrawExpringProgress = r11
            L_0x0198:
                org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r0.invite
                boolean r1 = r1.revoked
                r2 = 1094713344(0x41400000, float:12.0)
                if (r1 == 0) goto L_0x01c5
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
                goto L_0x01e9
            L_0x01c5:
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
            L_0x01e9:
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
                    return Theme.getColor("windowBackgroundWhiteGrayIcon");
                }
                return Theme.getColor("featuredStickers_addButton");
            }
        }

        public void setLink(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, int i) {
            String str;
            int i2;
            int i3;
            TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported2 = this.invite;
            if (tLRPC$TL_chatInviteExported2 == null || tLRPC$TL_chatInviteExported == null || !tLRPC$TL_chatInviteExported2.link.equals(tLRPC$TL_chatInviteExported.link)) {
                this.lastDrawingState = -1;
                this.animateToStateProgress = 1.0f;
            }
            this.invite = tLRPC$TL_chatInviteExported;
            if (tLRPC$TL_chatInviteExported.link.startsWith("https://")) {
                this.titleView.setText(tLRPC$TL_chatInviteExported.link.substring(8));
            } else {
                this.titleView.setText(tLRPC$TL_chatInviteExported.link);
            }
            int i4 = tLRPC$TL_chatInviteExported.usage;
            String string = i4 == 0 ? LocaleController.getString("NoOneJoinedYet", NUM) : LocaleController.formatPluralString("PeopleJoined", i4);
            if (tLRPC$TL_chatInviteExported.expired || tLRPC$TL_chatInviteExported.revoked) {
                if (tLRPC$TL_chatInviteExported.revoked && tLRPC$TL_chatInviteExported.usage == 0) {
                    string = LocaleController.getString("NoOneJoined", NUM);
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
                DotDividerSpan dotDividerSpan = new DotDividerSpan();
                dotDividerSpan.setTopPadding(AndroidUtilities.dp(1.5f));
                spannableStringBuilder.append("  .  ").setSpan(dotDividerSpan, spannableStringBuilder.length() - 3, spannableStringBuilder.length() - 2, 0);
                boolean z = tLRPC$TL_chatInviteExported.revoked;
                if (z || (i3 = tLRPC$TL_chatInviteExported.usage_limit) <= 0 || tLRPC$TL_chatInviteExported.usage < i3) {
                    if (z) {
                        i2 = NUM;
                        str = "Revoked";
                    } else {
                        i2 = NUM;
                        str = "Expired";
                    }
                    spannableStringBuilder.append(LocaleController.getString(str, i2));
                } else {
                    spannableStringBuilder.append(LocaleController.getString("LinkLimitReached", NUM));
                }
                this.subtitleView.setText(spannableStringBuilder);
                return;
            }
            this.subtitleView.setText(string);
        }
    }

    public void deleteLink(final TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
        TLRPC$TL_messages_deleteExportedChatInvite tLRPC$TL_messages_deleteExportedChatInvite = new TLRPC$TL_messages_deleteExportedChatInvite();
        tLRPC$TL_messages_deleteExportedChatInvite.link = tLRPC$TL_chatInviteExported.link;
        tLRPC$TL_messages_deleteExportedChatInvite.peer = getMessagesController().getInputPeer(-this.currentChatId);
        getConnectionsManager().sendRequest(tLRPC$TL_messages_deleteExportedChatInvite, new RequestDelegate() {
            public void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLRPC$TL_chatInviteExported) {
                    public final /* synthetic */ TLRPC$TL_error f$1;
                    public final /* synthetic */ TLRPC$TL_chatInviteExported f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        ManageLinksActivity.AnonymousClass3.this.lambda$run$0$ManageLinksActivity$3(this.f$1, this.f$2);
                    }
                });
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$run$0 */
            public /* synthetic */ void lambda$run$0$ManageLinksActivity$3(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
                if (tLRPC$TL_error == null) {
                    ManageLinksActivity.this.linkEditActivityCallback.onLinkRemoved(tLRPC$TL_chatInviteExported);
                }
            }
        });
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
                ManageLinksActivity.this.lambda$revokeLink$11$ManageLinksActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$revokeLink$11 */
    public /* synthetic */ void lambda$revokeLink$11$ManageLinksActivity(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLRPC$TL_chatInviteExported, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLRPC$TL_chatInviteExported f$2;
            public final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ManageLinksActivity.this.lambda$null$10$ManageLinksActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$10 */
    public /* synthetic */ void lambda$null$10$ManageLinksActivity(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
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
    }

    private void showItemsAnimated(final int i) {
        if (!this.isPaused && this.listView != null) {
            final View view = null;
            for (int i2 = 0; i2 < this.listView.getChildCount(); i2++) {
                View childAt = this.listView.getChildAt(i2);
                if (this.listView.getChildAdapterPosition(childAt) >= 0 && (childAt instanceof FlickerLoadingView)) {
                    view = childAt;
                }
            }
            if (view != null) {
                this.listView.removeView(view);
            }
            this.listView.invalidate();
            if (view != null && view.getParent() == null) {
                final RecyclerView.LayoutManager layoutManager = this.listView.getLayoutManager();
                this.listView.addView(view);
                if (layoutManager != null) {
                    layoutManager.ignoreView(view);
                    ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{view.getAlpha(), 0.0f});
                    ofFloat.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            view.setAlpha(1.0f);
                            layoutManager.stopIgnoringView(view);
                            ManageLinksActivity.this.listView.removeView(view);
                        }
                    });
                    ofFloat.start();
                }
            }
            this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    ManageLinksActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int childCount = ManageLinksActivity.this.listView.getChildCount();
                    AnimatorSet animatorSet = new AnimatorSet();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = ManageLinksActivity.this.listView.getChildAt(i);
                        if (childAt != view && ManageLinksActivity.this.listView.getChildAdapterPosition(childAt) >= i) {
                            childAt.setAlpha(0.0f);
                            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt, View.ALPHA, new float[]{0.0f, 1.0f});
                            ofFloat.setStartDelay((long) ((int) ((((float) Math.min(ManageLinksActivity.this.listView.getMeasuredHeight(), Math.max(0, childAt.getTop()))) / ((float) ManageLinksActivity.this.listView.getMeasuredHeight())) * 100.0f)));
                            ofFloat.setDuration(200);
                            animatorSet.playTogether(new Animator[]{ofFloat});
                        }
                    }
                    animatorSet.start();
                    return false;
                }
            });
        }
    }

    private class DiffCallback extends DiffUtil.Callback {
        SparseIntArray newPositionToItem;
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
            if (((i < this.oldLinksStartRow || i >= this.oldLinksEndRow) && (i < this.oldRevokedLinksStartRow || i >= this.oldRevokedLinksEndRow)) || ((i2 < ManageLinksActivity.this.linksStartRow || i2 >= ManageLinksActivity.this.linksEndRow) && (i2 < ManageLinksActivity.this.revokedLinksStartRow || i2 >= ManageLinksActivity.this.revokedLinksEndRow))) {
                int i3 = this.oldPositionToItem.get(i, -1);
                return i3 >= 0 && i3 == this.newPositionToItem.get(i2, -1);
            }
            if (i2 < ManageLinksActivity.this.linksStartRow || i2 >= ManageLinksActivity.this.linksEndRow) {
                tLRPC$TL_chatInviteExported = (TLRPC$TL_chatInviteExported) ManageLinksActivity.this.revokedInvites.get(i2 - ManageLinksActivity.this.revokedLinksStartRow);
            } else {
                tLRPC$TL_chatInviteExported = (TLRPC$TL_chatInviteExported) ManageLinksActivity.this.invites.get(i2 - ManageLinksActivity.this.linksStartRow);
            }
            int i4 = this.oldLinksStartRow;
            if (i < i4 || i >= this.oldLinksEndRow) {
                tLRPC$TL_chatInviteExported2 = this.oldRevokedLinks.get(i - this.oldRevokedLinksStartRow);
            } else {
                tLRPC$TL_chatInviteExported2 = this.oldLinks.get(i - i4);
            }
            return tLRPC$TL_chatInviteExported2.link.equals(tLRPC$TL_chatInviteExported.link);
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
            put(7, ManageLinksActivity.this.revokedDivider, sparseIntArray);
            put(8, ManageLinksActivity.this.lastDivider, sparseIntArray);
            put(9, ManageLinksActivity.this.revokeAllDivider, sparseIntArray);
            put(10, ManageLinksActivity.this.revokeAllRow, sparseIntArray);
            put(11, ManageLinksActivity.this.createLinkHelpRow, sparseIntArray);
        }

        private void put(int i, int i2, SparseIntArray sparseIntArray) {
            if (i2 >= 0) {
                sparseIntArray.put(i2, i);
            }
        }
    }

    private DiffCallback saveListState() {
        DiffCallback diffCallback = new DiffCallback();
        diffCallback.fillPositions(diffCallback.oldPositionToItem);
        diffCallback.oldLinksStartRow = this.linksStartRow;
        diffCallback.oldLinksEndRow = this.linksEndRow;
        diffCallback.oldRevokedLinksStartRow = this.revokedLinksStartRow;
        diffCallback.oldRevokedLinksEndRow = this.revokedLinksEndRow;
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
        $$Lambda$ManageLinksActivity$86k7sLXnsBouVYh5zhGq5vv_zQ r11 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ManageLinksActivity.this.lambda$getThemeDescriptions$12$ManageLinksActivity();
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
        $$Lambda$ManageLinksActivity$86k7sLXnsBouVYh5zhGq5vv_zQ r9 = r11;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        $$Lambda$ManageLinksActivity$86k7sLXnsBouVYh5zhGq5vv_zQ r8 = r11;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LinkCell.class}, new String[]{"titleView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LinkCell.class}, new String[]{"subtitleView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{LinkCell.class}, new String[]{"optionsView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextGray3"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getThemeDescriptions$12 */
    public /* synthetic */ void lambda$getThemeDescriptions$12$ManageLinksActivity() {
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
}
