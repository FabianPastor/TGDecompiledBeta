package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_chatChannelParticipant;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_chatInviteImporter;
import org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputUserEmpty;
import org.telegram.tgnet.TLRPC$TL_messages_chatInviteImporters;
import org.telegram.tgnet.TLRPC$TL_messages_getChatInviteImporters;
import org.telegram.tgnet.TLRPC$TL_users_getUsers;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.RecyclerListView;

public class InviteLinkBottomSheet extends BottomSheet {
    Adapter adapter;
    /* access modifiers changed from: private */
    public boolean canEdit = true;
    /* access modifiers changed from: private */
    public int chatId;
    int creatorHeaderRow;
    int creatorRow;
    int divider2Row;
    int dividerRow;
    int emptyHintRow;
    int emptyView;
    int emptyView2;
    BaseFragment fragment;
    boolean hasMore;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    TLRPC$ChatFull info;
    TLRPC$TL_chatInviteExported invite;
    InviteDelegate inviteDelegate;
    ArrayList<TLRPC$TL_chatInviteImporter> invitedUsers = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean isChannel;
    int linkActionRow;
    int linkInfoRow;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    int loadingRow;
    private boolean permanent;
    int rowCount;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public View shadow;
    /* access modifiers changed from: private */
    public AnimatorSet shadowAnimation;
    /* access modifiers changed from: private */
    public final long timeDif;
    /* access modifiers changed from: private */
    public TextView titleTextView;
    private boolean titleVisible;
    HashMap<Integer, TLRPC$User> users;
    int usersEndRow;
    int usersHeaderRow;
    boolean usersLoading;
    int usersStartRow;

    public interface InviteDelegate {
        void linkRevoked(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported);

        void onLinkDeleted(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported);

        void onLinkEdited(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported);

        void permanentLinkReplaced(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported2);
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public InviteLinkBottomSheet(android.content.Context r20, org.telegram.tgnet.TLRPC$TL_chatInviteExported r21, org.telegram.tgnet.TLRPC$ChatFull r22, java.util.HashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$User> r23, org.telegram.ui.ActionBar.BaseFragment r24, int r25, boolean r26, boolean r27) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r23
            r4 = r24
            r5 = r26
            r6 = 0
            r0.<init>(r1, r6)
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            r0.invitedUsers = r7
            r7 = 1
            r0.canEdit = r7
            r0.invite = r2
            r0.users = r3
            r0.fragment = r4
            r8 = r22
            r0.info = r8
            r8 = r25
            r0.chatId = r8
            r0.permanent = r5
            r8 = r27
            r0.isChannel = r8
            if (r3 != 0) goto L_0x0037
            java.util.HashMap r8 = new java.util.HashMap
            r8.<init>()
            r0.users = r8
        L_0x0037:
            int r8 = r0.currentAccount
            org.telegram.tgnet.ConnectionsManager r8 = org.telegram.tgnet.ConnectionsManager.getInstance(r8)
            int r8 = r8.getCurrentTime()
            long r8 = (long) r8
            long r10 = java.lang.System.currentTimeMillis()
            r12 = 1000(0x3e8, double:4.94E-321)
            long r10 = r10 / r12
            long r8 = r8 - r10
            r0.timeDif = r8
            org.telegram.ui.Components.InviteLinkBottomSheet$1 r8 = new org.telegram.ui.Components.InviteLinkBottomSheet$1
            r8.<init>(r1)
            r0.containerView = r8
            r8.setWillNotDraw(r6)
            android.widget.FrameLayout$LayoutParams r8 = new android.widget.FrameLayout$LayoutParams
            r9 = -1
            int r10 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r11 = 51
            r8.<init>(r9, r10, r11)
            r9 = 1111490560(0x42400000, float:48.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r8.topMargin = r10
            android.view.View r10 = new android.view.View
            r10.<init>(r1)
            r0.shadow = r10
            r11 = 0
            r10.setAlpha(r11)
            android.view.View r10 = r0.shadow
            r12 = 4
            r10.setVisibility(r12)
            android.view.View r10 = r0.shadow
            java.lang.Integer r13 = java.lang.Integer.valueOf(r7)
            r10.setTag(r13)
            android.view.ViewGroup r10 = r0.containerView
            android.view.View r13 = r0.shadow
            r10.addView(r13, r8)
            org.telegram.ui.Components.InviteLinkBottomSheet$2 r8 = new org.telegram.ui.Components.InviteLinkBottomSheet$2
            r8.<init>(r1)
            r0.listView = r8
            r10 = 14
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r8.setTag(r10)
            androidx.recyclerview.widget.LinearLayoutManager r8 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r10 = r19.getContext()
            r8.<init>(r10, r7, r6)
            org.telegram.ui.Components.RecyclerListView r10 = r0.listView
            r10.setLayoutManager(r8)
            org.telegram.ui.Components.RecyclerListView r10 = r0.listView
            org.telegram.ui.Components.InviteLinkBottomSheet$Adapter r13 = new org.telegram.ui.Components.InviteLinkBottomSheet$Adapter
            r14 = 0
            r13.<init>()
            r0.adapter = r13
            r10.setAdapter(r13)
            org.telegram.ui.Components.RecyclerListView r10 = r0.listView
            r10.setVerticalScrollBarEnabled(r6)
            org.telegram.ui.Components.RecyclerListView r10 = r0.listView
            r10.setClipToPadding(r6)
            org.telegram.ui.Components.RecyclerListView r10 = r0.listView
            r10.setNestedScrollingEnabled(r7)
            org.telegram.ui.Components.RecyclerListView r10 = r0.listView
            org.telegram.ui.Components.InviteLinkBottomSheet$3 r13 = new org.telegram.ui.Components.InviteLinkBottomSheet$3
            r13.<init>(r8)
            r10.setOnScrollListener(r13)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            org.telegram.ui.Components.InviteLinkBottomSheet$4 r10 = new org.telegram.ui.Components.InviteLinkBottomSheet$4
            r10.<init>(r2, r3, r4)
            r8.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r10)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r0.titleTextView = r4
            r4.setLines(r7)
            android.widget.TextView r1 = r0.titleTextView
            r1.setSingleLine(r7)
            android.widget.TextView r1 = r0.titleTextView
            r4 = 1101004800(0x41a00000, float:20.0)
            r1.setTextSize(r7, r4)
            android.widget.TextView r1 = r0.titleTextView
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
            r1.setEllipsize(r4)
            android.widget.TextView r1 = r0.titleTextView
            r4 = 1099956224(0x41900000, float:18.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.setPadding(r8, r6, r4, r6)
            android.widget.TextView r1 = r0.titleTextView
            r4 = 16
            r1.setGravity(r4)
            android.widget.TextView r1 = r0.titleTextView
            java.lang.String r4 = "fonts/rmedium.ttf"
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4)
            r1.setTypeface(r4)
            r1 = 2131625901(0x7f0e07ad, float:1.8879023E38)
            java.lang.String r4 = "InviteLink"
            if (r5 != 0) goto L_0x0150
            boolean r5 = r2.expired
            if (r5 == 0) goto L_0x0131
            android.widget.TextView r1 = r0.titleTextView
            r4 = 2131625482(0x7f0e060a, float:1.8878173E38)
            java.lang.String r5 = "ExpiredLink"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.setText(r4)
            goto L_0x014d
        L_0x0131:
            boolean r5 = r2.revoked
            if (r5 == 0) goto L_0x0144
            android.widget.TextView r1 = r0.titleTextView
            r4 = 2131627316(0x7f0e0d34, float:1.8881893E38)
            java.lang.String r5 = "RevokedLink"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.setText(r4)
            goto L_0x014d
        L_0x0144:
            android.widget.TextView r5 = r0.titleTextView
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r5.setText(r1)
        L_0x014d:
            r0.titleVisible = r7
            goto L_0x0165
        L_0x0150:
            android.widget.TextView r5 = r0.titleTextView
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r5.setText(r1)
            r0.titleVisible = r6
            android.widget.TextView r1 = r0.titleTextView
            r1.setVisibility(r12)
            android.widget.TextView r1 = r0.titleTextView
            r1.setAlpha(r11)
        L_0x0165:
            android.view.ViewGroup r1 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            r12 = -1
            r13 = -1082130432(0xffffffffbvar_, float:-1.0)
            r14 = 51
            r15 = 0
            boolean r5 = r0.titleVisible
            if (r5 != 0) goto L_0x0176
            r16 = 0
            goto L_0x0178
        L_0x0176:
            r16 = 1111490560(0x42400000, float:48.0)
        L_0x0178:
            r17 = 0
            r18 = 0
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r1.addView(r4, r5)
            android.view.ViewGroup r1 = r0.containerView
            android.widget.TextView r4 = r0.titleTextView
            r10 = -1
            boolean r5 = r0.titleVisible
            if (r5 != 0) goto L_0x018f
            r11 = 1111490560(0x42400000, float:48.0)
            goto L_0x0193
        L_0x018f:
            r9 = 1112014848(0x42480000, float:50.0)
            r11 = 1112014848(0x42480000, float:50.0)
        L_0x0193:
            r12 = 51
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r1.addView(r4, r5)
            r19.updateRows()
            r19.loadUsers()
            if (r3 == 0) goto L_0x01b5
            int r1 = r2.admin_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            java.lang.Object r1 = r3.get(r1)
            if (r1 != 0) goto L_0x01b8
        L_0x01b5:
            r19.loadCreator()
        L_0x01b8:
            r19.updateColors()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InviteLinkBottomSheet.<init>(android.content.Context, org.telegram.tgnet.TLRPC$TL_chatInviteExported, org.telegram.tgnet.TLRPC$ChatFull, java.util.HashMap, org.telegram.ui.ActionBar.BaseFragment, int, boolean, boolean):void");
    }

    public void updateColors() {
        TextView textView = this.titleTextView;
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.titleTextView.setLinkTextColor(Theme.getColor("dialogTextLink"));
            this.titleTextView.setHighlightColor(Theme.getColor("dialogLinkSelection"));
            if (!this.titleVisible) {
                this.titleTextView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
        }
        this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.shadow.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        setBackgroundColor(Theme.getColor("dialogBackground"));
        int hiddenChildCount = this.listView.getHiddenChildCount();
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            updateColorForView(this.listView.getChildAt(i));
        }
        for (int i2 = 0; i2 < hiddenChildCount; i2++) {
            updateColorForView(this.listView.getHiddenChildAt(i2));
        }
        int cachedChildCount = this.listView.getCachedChildCount();
        for (int i3 = 0; i3 < cachedChildCount; i3++) {
            updateColorForView(this.listView.getCachedChildAt(i3));
        }
        int attachedScrapChildCount = this.listView.getAttachedScrapChildCount();
        for (int i4 = 0; i4 < attachedScrapChildCount; i4++) {
            updateColorForView(this.listView.getAttachedScrapChildAt(i4));
        }
        this.containerView.invalidate();
    }

    private void updateColorForView(View view) {
        if (view instanceof HeaderCell) {
            ((HeaderCell) view).getTextView().setTextColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
        } else if (view instanceof LinkActionView) {
            ((LinkActionView) view).updateColors();
        } else if (view instanceof TextInfoPrivacyCell) {
            CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(view.getContext(), NUM, "windowBackgroundGrayShadow"));
            combinedDrawable.setFullsize(true);
            view.setBackground(combinedDrawable);
            ((TextInfoPrivacyCell) view).setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        } else if (view instanceof UserCell) {
            ((UserCell) view).update(0);
        }
        RecyclerView.ViewHolder childViewHolder = this.listView.getChildViewHolder(view);
        if (childViewHolder == null) {
            return;
        }
        if (childViewHolder.getItemViewType() == 7) {
            CombinedDrawable combinedDrawable2 = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(view.getContext(), NUM, "windowBackgroundGrayShadow"), 0, 0);
            combinedDrawable2.setFullsize(true);
            view.setBackgroundDrawable(combinedDrawable2);
        } else if (childViewHolder.getItemViewType() == 2) {
            CombinedDrawable combinedDrawable3 = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(view.getContext(), NUM, "windowBackgroundGrayShadow"), 0, 0);
            combinedDrawable3.setFullsize(true);
            view.setBackgroundDrawable(combinedDrawable3);
        }
    }

    private void loadCreator() {
        TLRPC$TL_users_getUsers tLRPC$TL_users_getUsers = new TLRPC$TL_users_getUsers();
        tLRPC$TL_users_getUsers.id.add(MessagesController.getInstance(UserConfig.selectedAccount).getInputUser(this.invite.admin_id));
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tLRPC$TL_users_getUsers, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                InviteLinkBottomSheet.this.lambda$loadCreator$0$InviteLinkBottomSheet(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadCreator$0 */
    public /* synthetic */ void lambda$loadCreator$0$InviteLinkBottomSheet(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (tLRPC$TL_error == null) {
                    InviteLinkBottomSheet inviteLinkBottomSheet = InviteLinkBottomSheet.this;
                    inviteLinkBottomSheet.users.put(Integer.valueOf(inviteLinkBottomSheet.invite.admin_id), (TLRPC$User) ((TLRPC$Vector) tLObject).objects.get(0));
                    InviteLinkBottomSheet.this.adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void updateRows() {
        this.rowCount = 0;
        this.dividerRow = -1;
        this.divider2Row = -1;
        this.usersHeaderRow = -1;
        this.usersStartRow = -1;
        this.usersEndRow = -1;
        this.emptyView2 = -1;
        this.linkActionRow = -1;
        this.linkInfoRow = -1;
        this.emptyHintRow = -1;
        if (!this.permanent) {
            int i = 0 + 1;
            this.rowCount = i;
            this.linkActionRow = 0;
            this.rowCount = i + 1;
            this.linkInfoRow = i;
        }
        int i2 = this.rowCount;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.creatorHeaderRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.creatorRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.emptyView = i4;
        TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = this.invite;
        if (!(tLRPC$TL_chatInviteExported.usage == 0 && tLRPC$TL_chatInviteExported.usage_limit == 0)) {
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.dividerRow = i5;
            this.rowCount = i6 + 1;
            this.usersHeaderRow = i6;
            if (!this.invitedUsers.isEmpty()) {
                int i7 = this.rowCount;
                this.usersStartRow = i7;
                int size = i7 + this.invitedUsers.size();
                this.rowCount = size;
                this.usersEndRow = size;
                this.rowCount = size + 1;
                this.emptyView2 = size;
            } else if (this.invite.usage > 0) {
                int i8 = this.rowCount;
                int i9 = i8 + 1;
                this.rowCount = i9;
                this.loadingRow = i8;
                this.rowCount = i9 + 1;
                this.emptyView2 = i9;
            } else {
                int i10 = this.rowCount;
                this.rowCount = i10 + 1;
                this.emptyHintRow = i10;
            }
        }
        if (this.emptyHintRow == -1) {
            int i11 = this.rowCount;
            this.rowCount = i11 + 1;
            this.divider2Row = i11;
        }
        this.adapter.notifyDataSetChanged();
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
        }

        public int getItemViewType(int i) {
            InviteLinkBottomSheet inviteLinkBottomSheet = InviteLinkBottomSheet.this;
            if (i == inviteLinkBottomSheet.creatorHeaderRow) {
                return 0;
            }
            if (i == inviteLinkBottomSheet.creatorRow) {
                return 1;
            }
            if (i >= inviteLinkBottomSheet.usersStartRow && i < inviteLinkBottomSheet.usersEndRow) {
                return 1;
            }
            if (i == inviteLinkBottomSheet.dividerRow) {
                return 2;
            }
            if (i == inviteLinkBottomSheet.linkActionRow) {
                return 3;
            }
            if (i == inviteLinkBottomSheet.linkInfoRow) {
                return 4;
            }
            if (i == inviteLinkBottomSheet.loadingRow) {
                return 5;
            }
            if (i == inviteLinkBottomSheet.emptyView || i == inviteLinkBottomSheet.emptyView2) {
                return 6;
            }
            if (i == inviteLinkBottomSheet.divider2Row) {
                return 7;
            }
            if (i == inviteLinkBottomSheet.emptyHintRow) {
                return 8;
            }
            return 0;
        }

        /* JADX WARNING: type inference failed for: r10v2, types: [android.view.View] */
        /* JADX WARNING: type inference failed for: r10v6 */
        /* JADX WARNING: type inference failed for: r10v12 */
        /* JADX WARNING: type inference failed for: r10v13 */
        /* JADX WARNING: type inference failed for: r0v12, types: [org.telegram.ui.Components.LinkActionView] */
        /* JADX WARNING: type inference failed for: r10v14 */
        /* JADX WARNING: type inference failed for: r10v15 */
        /* JADX WARNING: type inference failed for: r10v16 */
        /* JADX WARNING: type inference failed for: r0v13, types: [org.telegram.ui.Cells.HeaderCell] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r10, int r11) {
            /*
                r9 = this;
                android.content.Context r1 = r10.getContext()
                java.lang.String r10 = "windowBackgroundGrayShadow"
                r7 = -2
                r8 = -1
                java.lang.String r0 = "windowBackgroundGray"
                r2 = 12
                r3 = 1
                r4 = 0
                switch(r11) {
                    case 1: goto L_0x00d8;
                    case 2: goto L_0x00ce;
                    case 3: goto L_0x00a8;
                    case 4: goto L_0x0084;
                    case 5: goto L_0x0073;
                    case 6: goto L_0x006d;
                    case 7: goto L_0x004c;
                    case 8: goto L_0x0043;
                    default: goto L_0x0011;
                }
            L_0x0011:
                org.telegram.ui.Cells.HeaderCell r10 = new org.telegram.ui.Cells.HeaderCell
                r3 = 21
                r4 = 15
                r5 = 1
                java.lang.String r2 = "windowBackgroundWhiteBlueHeader"
                r0 = r10
                r0.<init>(r1, r2, r3, r4, r5)
                org.telegram.ui.ActionBar.SimpleTextView r11 = r10.getTextView2()
                java.lang.String r0 = "windowBackgroundWhiteRedText"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r11.setTextColor(r0)
                org.telegram.ui.ActionBar.SimpleTextView r11 = r10.getTextView2()
                r0 = 15
                r11.setTextSize(r0)
                org.telegram.ui.ActionBar.SimpleTextView r11 = r10.getTextView2()
                java.lang.String r0 = "fonts/rmedium.ttf"
                android.graphics.Typeface r0 = org.telegram.messenger.AndroidUtilities.getTypeface(r0)
                r11.setTypeface(r0)
                goto L_0x00dd
            L_0x0043:
                org.telegram.ui.Components.InviteLinkBottomSheet$EmptyHintRow r10 = new org.telegram.ui.Components.InviteLinkBottomSheet$EmptyHintRow
                org.telegram.ui.Components.InviteLinkBottomSheet r11 = org.telegram.ui.Components.InviteLinkBottomSheet.this
                r10.<init>(r1)
                goto L_0x00dd
            L_0x004c:
                org.telegram.ui.Cells.ShadowSectionCell r11 = new org.telegram.ui.Cells.ShadowSectionCell
                r11.<init>(r1, r2)
                r2 = 2131165443(0x7var_, float:1.7945103E38)
                android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r2, (java.lang.String) r10)
                android.graphics.drawable.ColorDrawable r1 = new android.graphics.drawable.ColorDrawable
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r1.<init>(r0)
                org.telegram.ui.Components.CombinedDrawable r0 = new org.telegram.ui.Components.CombinedDrawable
                r0.<init>(r1, r10, r4, r4)
                r0.setFullsize(r3)
                r11.setBackgroundDrawable(r0)
                goto L_0x00a6
            L_0x006d:
                org.telegram.ui.Components.InviteLinkBottomSheet$Adapter$2 r10 = new org.telegram.ui.Components.InviteLinkBottomSheet$Adapter$2
                r10.<init>(r1)
                goto L_0x00dd
            L_0x0073:
                org.telegram.ui.Components.FlickerLoadingView r10 = new org.telegram.ui.Components.FlickerLoadingView
                r10.<init>(r1)
                r10.setIsSingleCell(r3)
                r11 = 10
                r10.setViewType(r11)
                r10.showDate(r4)
                goto L_0x00dd
            L_0x0084:
                org.telegram.ui.Components.InviteLinkBottomSheet$TimerPrivacyCell r11 = new org.telegram.ui.Components.InviteLinkBottomSheet$TimerPrivacyCell
                org.telegram.ui.Components.InviteLinkBottomSheet r2 = org.telegram.ui.Components.InviteLinkBottomSheet.this
                r11.<init>(r1)
                org.telegram.ui.Components.CombinedDrawable r2 = new org.telegram.ui.Components.CombinedDrawable
                android.graphics.drawable.ColorDrawable r4 = new android.graphics.drawable.ColorDrawable
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r4.<init>(r0)
                r0 = 2131165442(0x7var_, float:1.7945101E38)
                android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r0, (java.lang.String) r10)
                r2.<init>(r4, r10)
                r2.setFullsize(r3)
                r11.setBackground(r2)
            L_0x00a6:
                r10 = r11
                goto L_0x00dd
            L_0x00a8:
                org.telegram.ui.Components.LinkActionView r10 = new org.telegram.ui.Components.LinkActionView
                org.telegram.ui.Components.InviteLinkBottomSheet r3 = org.telegram.ui.Components.InviteLinkBottomSheet.this
                org.telegram.ui.ActionBar.BaseFragment r2 = r3.fragment
                int r4 = r3.chatId
                r5 = 0
                org.telegram.ui.Components.InviteLinkBottomSheet r11 = org.telegram.ui.Components.InviteLinkBottomSheet.this
                boolean r6 = r11.isChannel
                r0 = r10
                r0.<init>(r1, r2, r3, r4, r5, r6)
                org.telegram.ui.Components.InviteLinkBottomSheet$Adapter$1 r11 = new org.telegram.ui.Components.InviteLinkBottomSheet$Adapter$1
                r11.<init>()
                r10.setDelegate(r11)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r11 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r11.<init>((int) r8, (int) r7)
                r10.setLayoutParams(r11)
                goto L_0x00dd
            L_0x00ce:
                org.telegram.ui.Cells.ShadowSectionCell r10 = new org.telegram.ui.Cells.ShadowSectionCell
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r10.<init>(r1, r2, r11)
                goto L_0x00dd
            L_0x00d8:
                org.telegram.ui.Cells.UserCell r10 = new org.telegram.ui.Cells.UserCell
                r10.<init>(r1, r2, r4, r3)
            L_0x00dd:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r11 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r11.<init>((int) r8, (int) r7)
                r10.setLayoutParams(r11)
                org.telegram.ui.Components.RecyclerListView$Holder r11 = new org.telegram.ui.Components.RecyclerListView$Holder
                r11.<init>(r10)
                return r11
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InviteLinkBottomSheet.Adapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            int i3;
            String str;
            TLRPC$User tLRPC$User;
            String string;
            int itemViewType = viewHolder.getItemViewType();
            String str2 = null;
            if (itemViewType != 0) {
                int i4 = 0;
                if (itemViewType == 1) {
                    UserCell userCell = (UserCell) viewHolder.itemView;
                    InviteLinkBottomSheet inviteLinkBottomSheet = InviteLinkBottomSheet.this;
                    if (i == inviteLinkBottomSheet.creatorRow) {
                        TLRPC$User tLRPC$User2 = inviteLinkBottomSheet.users.get(Integer.valueOf(inviteLinkBottomSheet.invite.admin_id));
                        if (tLRPC$User2 == null) {
                            tLRPC$User2 = MessagesController.getInstance(InviteLinkBottomSheet.this.currentAccount).getUser(Integer.valueOf(InviteLinkBottomSheet.this.invite.admin_id));
                        }
                        String formatDateAudio = tLRPC$User2 != null ? LocaleController.formatDateAudio((long) InviteLinkBottomSheet.this.invite.date, false) : null;
                        TLRPC$ChatFull tLRPC$ChatFull = InviteLinkBottomSheet.this.info;
                        if (tLRPC$ChatFull != null && tLRPC$User2 != null && tLRPC$ChatFull.participants != null) {
                            while (true) {
                                if (i4 >= InviteLinkBottomSheet.this.info.participants.participants.size()) {
                                    break;
                                } else if (InviteLinkBottomSheet.this.info.participants.participants.get(i4).user_id == tLRPC$User2.id) {
                                    TLRPC$ChatParticipant tLRPC$ChatParticipant = InviteLinkBottomSheet.this.info.participants.participants.get(i4);
                                    if (tLRPC$ChatParticipant instanceof TLRPC$TL_chatChannelParticipant) {
                                        TLRPC$ChannelParticipant tLRPC$ChannelParticipant = ((TLRPC$TL_chatChannelParticipant) tLRPC$ChatParticipant).channelParticipant;
                                        if (!TextUtils.isEmpty(tLRPC$ChannelParticipant.rank)) {
                                            str2 = tLRPC$ChannelParticipant.rank;
                                        } else if (tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantCreator) {
                                            str2 = LocaleController.getString("ChannelCreator", NUM);
                                        } else if (tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantAdmin) {
                                            str2 = LocaleController.getString("ChannelAdmin", NUM);
                                        }
                                    } else {
                                        if (tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantCreator) {
                                            string = LocaleController.getString("ChannelCreator", NUM);
                                        } else if (tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantAdmin) {
                                            string = LocaleController.getString("ChannelAdmin", NUM);
                                        }
                                        str2 = string;
                                    }
                                } else {
                                    i4++;
                                }
                            }
                        }
                        tLRPC$User = tLRPC$User2;
                        str = formatDateAudio;
                    } else {
                        tLRPC$User = InviteLinkBottomSheet.this.users.get(Integer.valueOf(inviteLinkBottomSheet.invitedUsers.get(i - inviteLinkBottomSheet.usersStartRow).user_id));
                        str = null;
                    }
                    userCell.setAdminRole(str2);
                    userCell.setData(tLRPC$User, (CharSequence) null, str, 0, false);
                } else if (itemViewType == 3) {
                    LinkActionView linkActionView = (LinkActionView) viewHolder.itemView;
                    linkActionView.setUsers(0, (ArrayList<TLRPC$User>) null);
                    linkActionView.setLink(InviteLinkBottomSheet.this.invite.link);
                    linkActionView.setRevoke(InviteLinkBottomSheet.this.invite.revoked);
                    linkActionView.setPermanent(InviteLinkBottomSheet.this.invite.permanent);
                    linkActionView.setCanEdit(InviteLinkBottomSheet.this.canEdit);
                    linkActionView.hideRevokeOption(!InviteLinkBottomSheet.this.canEdit);
                } else if (itemViewType == 4) {
                    TimerPrivacyCell timerPrivacyCell = (TimerPrivacyCell) viewHolder.itemView;
                    timerPrivacyCell.cancelTimer();
                    timerPrivacyCell.timer = false;
                    timerPrivacyCell.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
                    timerPrivacyCell.setFixedSize(0);
                    TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = InviteLinkBottomSheet.this.invite;
                    if (tLRPC$TL_chatInviteExported.revoked) {
                        timerPrivacyCell.setText(LocaleController.getString("LinkIsNoActive", NUM));
                    } else if (tLRPC$TL_chatInviteExported.expired) {
                        int i5 = tLRPC$TL_chatInviteExported.usage_limit;
                        if (i5 <= 0 || i5 != tLRPC$TL_chatInviteExported.usage) {
                            timerPrivacyCell.setText(LocaleController.getString("LinkIsExpired", NUM));
                            timerPrivacyCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText"));
                            return;
                        }
                        timerPrivacyCell.setText(LocaleController.getString("LinkIsExpiredLimitReached", NUM));
                    } else if (tLRPC$TL_chatInviteExported.expire_date > 0) {
                        long currentTimeMillis = System.currentTimeMillis() + (InviteLinkBottomSheet.this.timeDif * 1000);
                        int i6 = InviteLinkBottomSheet.this.invite.expire_date;
                        long j = (((long) i6) * 1000) - currentTimeMillis;
                        if (j < 0) {
                            j = 0;
                        }
                        if (j > 86400000) {
                            timerPrivacyCell.setText(LocaleController.formatString("LinkExpiresIn", NUM, LocaleController.formatDateAudio((long) i6, false)));
                            return;
                        }
                        long j2 = j / 1000;
                        int i7 = (int) (j2 % 60);
                        long j3 = j2 / 60;
                        int i8 = (int) (j3 / 60);
                        StringBuilder sb = new StringBuilder();
                        Locale locale = Locale.ENGLISH;
                        sb.append(String.format(locale, "%02d", new Object[]{Integer.valueOf(i8)}));
                        sb.append(String.format(locale, ":%02d", new Object[]{Integer.valueOf((int) (j3 % 60))}));
                        sb.append(String.format(locale, ":%02d", new Object[]{Integer.valueOf(i7)}));
                        String sb2 = sb.toString();
                        timerPrivacyCell.timer = true;
                        timerPrivacyCell.runTimer();
                        timerPrivacyCell.setText(LocaleController.formatString("LinkExpiresInTime", NUM, sb2));
                    } else {
                        timerPrivacyCell.setFixedSize(12);
                        timerPrivacyCell.setText((CharSequence) null);
                    }
                } else if (itemViewType == 8) {
                    EmptyHintRow emptyHintRow = (EmptyHintRow) viewHolder.itemView;
                    int i9 = InviteLinkBottomSheet.this.invite.usage_limit;
                    if (i9 > 0) {
                        emptyHintRow.textView.setText(LocaleController.formatPluralString("PeopleCanJoinViaLinkCount", i9));
                        emptyHintRow.textView.setVisibility(0);
                        return;
                    }
                    emptyHintRow.textView.setVisibility(8);
                }
            } else {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                InviteLinkBottomSheet inviteLinkBottomSheet2 = InviteLinkBottomSheet.this;
                if (i == inviteLinkBottomSheet2.creatorHeaderRow) {
                    headerCell.setText(LocaleController.getString("LinkCreatedeBy", NUM));
                    headerCell.setText2((CharSequence) null);
                } else if (i == inviteLinkBottomSheet2.usersHeaderRow) {
                    int i10 = inviteLinkBottomSheet2.invite.usage;
                    if (i10 > 0) {
                        headerCell.setText(LocaleController.formatPluralString("PeopleJoined", i10));
                    } else {
                        headerCell.setText(LocaleController.getString("NoOneJoined", NUM));
                    }
                    TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported2 = InviteLinkBottomSheet.this.invite;
                    if (tLRPC$TL_chatInviteExported2.expired || tLRPC$TL_chatInviteExported2.revoked || (i2 = tLRPC$TL_chatInviteExported2.usage_limit) <= 0 || (i3 = tLRPC$TL_chatInviteExported2.usage) <= 0) {
                        headerCell.setText2((CharSequence) null);
                    } else {
                        headerCell.setText2(LocaleController.formatPluralString("PeopleJoinedRemaining", i2 - i3));
                    }
                }
            }
        }

        public int getItemCount() {
            return InviteLinkBottomSheet.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            InviteLinkBottomSheet inviteLinkBottomSheet = InviteLinkBottomSheet.this;
            return adapterPosition == inviteLinkBottomSheet.creatorRow ? inviteLinkBottomSheet.invite.admin_id != UserConfig.getInstance(inviteLinkBottomSheet.currentAccount).clientUserId : adapterPosition >= inviteLinkBottomSheet.usersStartRow && adapterPosition < inviteLinkBottomSheet.usersEndRow;
        }
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.titleTextView.setTranslationY((float) this.scrollOffsetY);
            this.shadow.setTranslationY((float) this.scrollOffsetY);
            this.containerView.invalidate();
            return;
        }
        int i = 0;
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop();
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            runShadowAnimation(true);
        } else {
            runShadowAnimation(false);
            i = top;
        }
        if (this.scrollOffsetY != i) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = i;
            recyclerListView2.setTopGlowOffset(i);
            TextView textView = this.titleTextView;
            if (textView != null) {
                textView.setTranslationY((float) this.scrollOffsetY);
            }
            this.shadow.setTranslationY((float) this.scrollOffsetY);
            this.containerView.invalidate();
        }
    }

    private void runShadowAnimation(final boolean z) {
        if ((z && this.shadow.getTag() != null) || (!z && this.shadow.getTag() == null)) {
            this.shadow.setTag(z ? null : 1);
            if (z) {
                this.shadow.setVisibility(0);
                this.titleTextView.setVisibility(0);
            }
            AnimatorSet animatorSet = this.shadowAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.shadowAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            float f = 1.0f;
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            if (!this.titleVisible) {
                AnimatorSet animatorSet3 = this.shadowAnimation;
                Animator[] animatorArr2 = new Animator[1];
                TextView textView = this.titleTextView;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                if (!z) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                animatorArr2[0] = ObjectAnimator.ofFloat(textView, property2, fArr2);
                animatorSet3.playTogether(animatorArr2);
            }
            this.shadowAnimation.setDuration(150);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (InviteLinkBottomSheet.this.shadowAnimation != null && InviteLinkBottomSheet.this.shadowAnimation.equals(animator)) {
                        if (!z) {
                            InviteLinkBottomSheet.this.shadow.setVisibility(4);
                            InviteLinkBottomSheet.this.titleTextView.setVisibility(4);
                        }
                        AnimatorSet unused = InviteLinkBottomSheet.this.shadowAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (InviteLinkBottomSheet.this.shadowAnimation != null && InviteLinkBottomSheet.this.shadowAnimation.equals(animator)) {
                        AnimatorSet unused = InviteLinkBottomSheet.this.shadowAnimation = null;
                    }
                }
            });
            this.shadowAnimation.start();
        }
    }

    public void loadUsers() {
        if (this.invite.usage > 0 && !this.usersLoading) {
            TLRPC$TL_messages_getChatInviteImporters tLRPC$TL_messages_getChatInviteImporters = new TLRPC$TL_messages_getChatInviteImporters();
            tLRPC$TL_messages_getChatInviteImporters.link = this.invite.link;
            tLRPC$TL_messages_getChatInviteImporters.peer = MessagesController.getInstance(UserConfig.selectedAccount).getInputPeer(-this.chatId);
            if (this.invitedUsers.isEmpty()) {
                tLRPC$TL_messages_getChatInviteImporters.offset_user = new TLRPC$TL_inputUserEmpty();
            } else {
                ArrayList<TLRPC$TL_chatInviteImporter> arrayList = this.invitedUsers;
                TLRPC$TL_chatInviteImporter tLRPC$TL_chatInviteImporter = arrayList.get(arrayList.size() - 1);
                tLRPC$TL_messages_getChatInviteImporters.offset_user = MessagesController.getInstance(this.currentAccount).getInputUser(this.users.get(Integer.valueOf(tLRPC$TL_chatInviteImporter.user_id)));
                tLRPC$TL_messages_getChatInviteImporters.offset_date = tLRPC$TL_chatInviteImporter.date;
            }
            this.usersLoading = true;
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tLRPC$TL_messages_getChatInviteImporters, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    InviteLinkBottomSheet.this.lambda$loadUsers$2$InviteLinkBottomSheet(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadUsers$2 */
    public /* synthetic */ void lambda$loadUsers$2$InviteLinkBottomSheet(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                InviteLinkBottomSheet.this.lambda$loadUsers$1$InviteLinkBottomSheet(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadUsers$1 */
    public /* synthetic */ void lambda$loadUsers$1$InviteLinkBottomSheet(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_chatInviteImporters tLRPC$TL_messages_chatInviteImporters = (TLRPC$TL_messages_chatInviteImporters) tLObject;
            this.invitedUsers.addAll(tLRPC$TL_messages_chatInviteImporters.importers);
            for (int i = 0; i < tLRPC$TL_messages_chatInviteImporters.users.size(); i++) {
                TLRPC$User tLRPC$User = tLRPC$TL_messages_chatInviteImporters.users.get(i);
                this.users.put(Integer.valueOf(tLRPC$User.id), tLRPC$User);
            }
            this.hasMore = this.invitedUsers.size() < tLRPC$TL_messages_chatInviteImporters.count;
            updateRows();
        }
        this.usersLoading = false;
    }

    public void setInviteDelegate(InviteDelegate inviteDelegate2) {
        this.inviteDelegate = inviteDelegate2;
    }

    private class TimerPrivacyCell extends TextInfoPrivacyCell {
        boolean timer;
        Runnable timerRunnable = new Runnable() {
            public void run() {
                int childAdapterPosition;
                if (!(InviteLinkBottomSheet.this.listView == null || InviteLinkBottomSheet.this.listView.getAdapter() == null || (childAdapterPosition = InviteLinkBottomSheet.this.listView.getChildAdapterPosition(TimerPrivacyCell.this)) < 0)) {
                    InviteLinkBottomSheet inviteLinkBottomSheet = InviteLinkBottomSheet.this;
                    inviteLinkBottomSheet.adapter.onBindViewHolder(inviteLinkBottomSheet.listView.getChildViewHolder(TimerPrivacyCell.this), childAdapterPosition);
                }
                AndroidUtilities.runOnUIThread(this);
            }
        };

        public TimerPrivacyCell(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            runTimer();
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            cancelTimer();
        }

        public void cancelTimer() {
            AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
        }

        public void runTimer() {
            cancelTimer();
            if (this.timer) {
                AndroidUtilities.runOnUIThread(this.timerRunnable, 500);
            }
        }
    }

    private class EmptyHintRow extends FrameLayout {
        TextView textView;

        public EmptyHintRow(Context context) {
            super(context);
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            this.textView.setGravity(1);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 16, 60.0f, 0.0f, 60.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(84.0f), NUM));
        }
    }

    public void setCanEdit(boolean z) {
        this.canEdit = z;
    }
}
