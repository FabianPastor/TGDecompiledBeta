package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
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
    public int chatId;
    int creatorHeaderRow;
    int creatorRow;
    int divider2Row;
    int dividerRow;
    int emptyView;
    int emptyView2;
    BaseFragment fragment;
    boolean hasMore;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    TLRPC$ChatFull info;
    TLRPC$TL_chatInviteExported invite;
    ArrayList<TLRPC$TL_chatInviteImporter> invitedUsers = new ArrayList<>();
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
    private TextView titleTextView;
    HashMap<Integer, TLRPC$User> users;
    int usersEndRow;
    int usersHeaderRow;
    boolean usersLoading;
    int usersStartRow;

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public InviteLinkBottomSheet(android.content.Context r20, org.telegram.tgnet.TLRPC$TL_chatInviteExported r21, org.telegram.tgnet.TLRPC$ChatFull r22, java.util.HashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$User> r23, org.telegram.ui.ActionBar.BaseFragment r24, int r25, boolean r26) {
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
            r0.invite = r2
            r0.users = r3
            r0.fragment = r4
            r7 = r22
            r0.info = r7
            r7 = r25
            r0.chatId = r7
            r0.permanent = r5
            org.telegram.ui.Components.InviteLinkBottomSheet$1 r7 = new org.telegram.ui.Components.InviteLinkBottomSheet$1
            r7.<init>(r1)
            r0.containerView = r7
            r7.setWillNotDraw(r6)
            android.widget.FrameLayout$LayoutParams r7 = new android.widget.FrameLayout$LayoutParams
            int r8 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r9 = -1
            r10 = 51
            r7.<init>(r9, r8, r10)
            r8 = 1111490560(0x42400000, float:48.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r7.topMargin = r9
            android.view.View r9 = new android.view.View
            r9.<init>(r1)
            r0.shadow = r9
            r10 = 0
            r9.setAlpha(r10)
            android.view.View r9 = r0.shadow
            r11 = 4
            r9.setVisibility(r11)
            android.view.View r9 = r0.shadow
            r11 = 1
            java.lang.Integer r12 = java.lang.Integer.valueOf(r11)
            r9.setTag(r12)
            android.view.ViewGroup r9 = r0.containerView
            android.view.View r12 = r0.shadow
            r9.addView(r12, r7)
            org.telegram.ui.Components.InviteLinkBottomSheet$2 r7 = new org.telegram.ui.Components.InviteLinkBottomSheet$2
            r7.<init>(r1)
            r0.listView = r7
            r9 = 14
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r7.setTag(r9)
            androidx.recyclerview.widget.LinearLayoutManager r7 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r9 = r19.getContext()
            r7.<init>(r9, r11, r6)
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            r9.setLayoutManager(r7)
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            org.telegram.ui.Components.InviteLinkBottomSheet$Adapter r12 = new org.telegram.ui.Components.InviteLinkBottomSheet$Adapter
            r13 = 0
            r12.<init>()
            r0.adapter = r12
            r9.setAdapter(r12)
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            r9.setVerticalScrollBarEnabled(r6)
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            r9.setClipToPadding(r6)
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            r9.setNestedScrollingEnabled(r11)
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            org.telegram.ui.Components.InviteLinkBottomSheet$3 r12 = new org.telegram.ui.Components.InviteLinkBottomSheet$3
            r12.<init>(r7)
            r9.setOnScrollListener(r12)
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            org.telegram.ui.Components.InviteLinkBottomSheet$4 r9 = new org.telegram.ui.Components.InviteLinkBottomSheet$4
            r9.<init>(r3, r2, r4)
            r7.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r9)
            android.view.ViewGroup r4 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            if (r5 == 0) goto L_0x00be
            r16 = 0
            goto L_0x00c0
        L_0x00be:
            r16 = 1111490560(0x42400000, float:48.0)
        L_0x00c0:
            r17 = 0
            r18 = 0
            r12 = -1
            r13 = -1082130432(0xffffffffbvar_, float:-1.0)
            r14 = 51
            r15 = 0
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r4.addView(r7, r8)
            if (r5 != 0) goto L_0x013e
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r0.titleTextView = r4
            r4.setLines(r11)
            android.widget.TextView r1 = r0.titleTextView
            r1.setSingleLine(r11)
            android.widget.TextView r1 = r0.titleTextView
            r4 = 1101004800(0x41a00000, float:20.0)
            r1.setTextSize(r11, r4)
            android.widget.TextView r1 = r0.titleTextView
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
            r1.setEllipsize(r4)
            android.widget.TextView r1 = r0.titleTextView
            r4 = 1099956224(0x41900000, float:18.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.setPadding(r5, r6, r4, r6)
            android.widget.TextView r1 = r0.titleTextView
            r4 = 16
            r1.setGravity(r4)
            android.widget.TextView r1 = r0.titleTextView
            boolean r4 = r2.revoked
            if (r4 == 0) goto L_0x0112
            r4 = 2131627079(0x7f0e0CLASSNAME, float:1.8881412E38)
            java.lang.String r5 = "RevokedLink"
            goto L_0x0117
        L_0x0112:
            r4 = 2131625750(0x7f0e0716, float:1.8878717E38)
            java.lang.String r5 = "InviteLink"
        L_0x0117:
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.setText(r4)
            android.widget.TextView r1 = r0.titleTextView
            java.lang.String r4 = "fonts/rmedium.ttf"
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4)
            r1.setTypeface(r4)
            android.view.ViewGroup r1 = r0.containerView
            android.widget.TextView r4 = r0.titleTextView
            r5 = -1
            r6 = 1112014848(0x42480000, float:50.0)
            r7 = 51
            r8 = 0
            r9 = 0
            r10 = 1109393408(0x42200000, float:40.0)
            r11 = 0
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6, r7, r8, r9, r10, r11)
            r1.addView(r4, r5)
        L_0x013e:
            r19.updateRows()
            r19.loadUsers()
            int r1 = r2.admin_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            java.lang.Object r1 = r3.get(r1)
            if (r1 != 0) goto L_0x0153
            r19.loadCreator()
        L_0x0153:
            r19.updateColors()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InviteLinkBottomSheet.<init>(android.content.Context, org.telegram.tgnet.TLRPC$TL_chatInviteExported, org.telegram.tgnet.TLRPC$ChatFull, java.util.HashMap, org.telegram.ui.ActionBar.BaseFragment, int, boolean):void");
    }

    public void updateColors() {
        TextView textView = this.titleTextView;
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.titleTextView.setLinkTextColor(Theme.getColor("dialogTextLink"));
            this.titleTextView.setHighlightColor(Theme.getColor("dialogLinkSelection"));
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
        if (this.invite.usage > 0) {
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
            } else {
                int i8 = this.rowCount;
                this.rowCount = i8 + 1;
                this.loadingRow = i8;
            }
            int i9 = this.rowCount;
            this.rowCount = i9 + 1;
            this.emptyView2 = i9;
        }
        int i10 = this.rowCount;
        this.rowCount = i10 + 1;
        this.divider2Row = i10;
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
            return 0;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v2, resolved type: org.telegram.ui.Cells.UserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v3, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v5, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v6, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v7, resolved type: org.telegram.ui.Components.InviteLinkBottomSheet$Adapter$2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v8, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v9, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v10, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v11, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v12, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v13, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v14, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v15, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v16, resolved type: org.telegram.ui.Components.LinkActionView} */
        /* JADX WARNING: type inference failed for: r10v1, types: [android.view.View] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r9, int r10) {
            /*
                r8 = this;
                android.content.Context r1 = r9.getContext()
                java.lang.String r9 = "windowBackgroundGrayShadow"
                r6 = -2
                r7 = -1
                java.lang.String r0 = "windowBackgroundGray"
                r2 = 12
                r3 = 1
                r4 = 0
                switch(r10) {
                    case 1: goto L_0x009b;
                    case 2: goto L_0x0091;
                    case 3: goto L_0x0071;
                    case 4: goto L_0x0050;
                    case 5: goto L_0x003f;
                    case 6: goto L_0x0039;
                    case 7: goto L_0x0018;
                    default: goto L_0x0011;
                }
            L_0x0011:
                org.telegram.ui.Cells.HeaderCell r10 = new org.telegram.ui.Cells.HeaderCell
                r10.<init>(r1)
                goto L_0x00a0
            L_0x0018:
                org.telegram.ui.Cells.ShadowSectionCell r10 = new org.telegram.ui.Cells.ShadowSectionCell
                r10.<init>(r1, r2)
                r2 = 2131165449(0x7var_, float:1.7945115E38)
                android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r2, (java.lang.String) r9)
                android.graphics.drawable.ColorDrawable r1 = new android.graphics.drawable.ColorDrawable
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r1.<init>(r0)
                org.telegram.ui.Components.CombinedDrawable r0 = new org.telegram.ui.Components.CombinedDrawable
                r0.<init>(r1, r9, r4, r4)
                r0.setFullsize(r3)
                r10.setBackgroundDrawable(r0)
                goto L_0x00a0
            L_0x0039:
                org.telegram.ui.Components.InviteLinkBottomSheet$Adapter$2 r10 = new org.telegram.ui.Components.InviteLinkBottomSheet$Adapter$2
                r10.<init>(r8, r1)
                goto L_0x00a0
            L_0x003f:
                org.telegram.ui.Components.FlickerLoadingView r10 = new org.telegram.ui.Components.FlickerLoadingView
                r10.<init>(r1)
                r10.setIsSingleCell(r3)
                r9 = 10
                r10.setViewType(r9)
                r10.showDate(r4)
                goto L_0x00a0
            L_0x0050:
                org.telegram.ui.Cells.TextInfoPrivacyCell r10 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                r10.<init>(r1)
                org.telegram.ui.Components.CombinedDrawable r2 = new org.telegram.ui.Components.CombinedDrawable
                android.graphics.drawable.ColorDrawable r4 = new android.graphics.drawable.ColorDrawable
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r4.<init>(r0)
                r0 = 2131165448(0x7var_, float:1.7945113E38)
                android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r0, (java.lang.String) r9)
                r2.<init>(r4, r9)
                r2.setFullsize(r3)
                r10.setBackground(r2)
                goto L_0x00a0
            L_0x0071:
                org.telegram.ui.Components.LinkActionView r10 = new org.telegram.ui.Components.LinkActionView
                org.telegram.ui.Components.InviteLinkBottomSheet r3 = org.telegram.ui.Components.InviteLinkBottomSheet.this
                org.telegram.ui.ActionBar.BaseFragment r2 = r3.fragment
                int r4 = r3.chatId
                r5 = 0
                r0 = r10
                r0.<init>(r1, r2, r3, r4, r5)
                org.telegram.ui.Components.InviteLinkBottomSheet$Adapter$1 r9 = new org.telegram.ui.Components.InviteLinkBottomSheet$Adapter$1
                r9.<init>()
                r10.setDelegate(r9)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r9 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r9.<init>((int) r7, (int) r6)
                r10.setLayoutParams(r9)
                goto L_0x00a0
            L_0x0091:
                org.telegram.ui.Cells.ShadowSectionCell r10 = new org.telegram.ui.Cells.ShadowSectionCell
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r10.<init>(r1, r2, r9)
                goto L_0x00a0
            L_0x009b:
                org.telegram.ui.Cells.UserCell r10 = new org.telegram.ui.Cells.UserCell
                r10.<init>(r1, r2, r4, r3)
            L_0x00a0:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r9 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r9.<init>((int) r7, (int) r6)
                r10.setLayoutParams(r9)
                org.telegram.ui.Components.RecyclerListView$Holder r9 = new org.telegram.ui.Components.RecyclerListView$Holder
                r9.<init>(r10)
                return r9
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InviteLinkBottomSheet.Adapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            TLRPC$User tLRPC$User;
            String string;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                int i2 = 0;
                String str2 = null;
                if (itemViewType == 1) {
                    UserCell userCell = (UserCell) viewHolder.itemView;
                    InviteLinkBottomSheet inviteLinkBottomSheet = InviteLinkBottomSheet.this;
                    if (i == inviteLinkBottomSheet.creatorRow) {
                        TLRPC$User tLRPC$User2 = inviteLinkBottomSheet.users.get(Integer.valueOf(inviteLinkBottomSheet.invite.admin_id));
                        String formatDateAudio = tLRPC$User2 != null ? LocaleController.formatDateAudio((long) InviteLinkBottomSheet.this.invite.date, false) : null;
                        if (InviteLinkBottomSheet.this.info != null && tLRPC$User2 != null) {
                            while (true) {
                                if (i2 >= InviteLinkBottomSheet.this.info.participants.participants.size()) {
                                    break;
                                } else if (InviteLinkBottomSheet.this.info.participants.participants.get(i2).user_id == tLRPC$User2.id) {
                                    TLRPC$ChatParticipant tLRPC$ChatParticipant = InviteLinkBottomSheet.this.info.participants.participants.get(i2);
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
                                    i2++;
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
                } else if (itemViewType == 4) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = InviteLinkBottomSheet.this.invite;
                    if (tLRPC$TL_chatInviteExported.revoked) {
                        textInfoPrivacyCell.setText(LocaleController.getString("LinkIsNoActive", NUM));
                    } else if (tLRPC$TL_chatInviteExported.expired) {
                        textInfoPrivacyCell.setText(LocaleController.getString("LinkIsExpired", NUM));
                    } else {
                        int i3 = tLRPC$TL_chatInviteExported.expire_date;
                        if (i3 > 0) {
                            textInfoPrivacyCell.setText(LocaleController.formatString("LinkExpiresIn", NUM, LocaleController.formatDateAudio((long) i3, false)));
                        } else {
                            textInfoPrivacyCell.setText((CharSequence) null);
                        }
                    }
                }
            } else {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                InviteLinkBottomSheet inviteLinkBottomSheet2 = InviteLinkBottomSheet.this;
                if (i == inviteLinkBottomSheet2.creatorHeaderRow) {
                    headerCell.setText(LocaleController.getString("LinkCreatedeBy", NUM));
                } else if (i == inviteLinkBottomSheet2.usersHeaderRow) {
                    headerCell.setText(LocaleController.formatPluralString("PeopleJoined", inviteLinkBottomSheet2.invite.usage));
                }
            }
        }

        public int getItemCount() {
            return InviteLinkBottomSheet.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            InviteLinkBottomSheet inviteLinkBottomSheet = InviteLinkBottomSheet.this;
            if (adapterPosition != inviteLinkBottomSheet.creatorRow) {
                return adapterPosition >= inviteLinkBottomSheet.usersStartRow && adapterPosition < inviteLinkBottomSheet.usersEndRow;
            }
            return true;
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
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation.setDuration(150);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (InviteLinkBottomSheet.this.shadowAnimation != null && InviteLinkBottomSheet.this.shadowAnimation.equals(animator)) {
                        if (!z) {
                            InviteLinkBottomSheet.this.shadow.setVisibility(4);
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
                InviteLinkBottomSheet.this.lambda$null$1$InviteLinkBottomSheet(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$1 */
    public /* synthetic */ void lambda$null$1$InviteLinkBottomSheet(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
}
