package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.LinkActionView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LinkEditActivity;
import org.telegram.ui.ManageLinksActivity;
import org.telegram.ui.ProfileActivity;

public class InviteLinkBottomSheet extends BottomSheet {
    Adapter adapter;
    /* access modifiers changed from: private */
    public boolean canEdit = true;
    /* access modifiers changed from: private */
    public long chatId;
    int creatorHeaderRow;
    int creatorRow;
    int divider2Row;
    int divider3Row;
    int dividerRow;
    int emptyHintRow;
    int emptyView;
    int emptyView2;
    int emptyView3;
    BaseFragment fragment;
    boolean hasMore;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    TLRPC.ChatFull info;
    TLRPC.TL_chatInviteExported invite;
    InviteDelegate inviteDelegate;
    /* access modifiers changed from: private */
    public boolean isChannel;
    public boolean isNeedReopen = false;
    int joinedEndRow;
    int joinedHeaderRow;
    int joinedStartRow;
    ArrayList<TLRPC.TL_chatInviteImporter> joinedUsers = new ArrayList<>();
    int linkActionRow;
    int linkInfoRow;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    int loadingRow;
    private boolean permanent;
    int requestedEndRow;
    int requestedHeaderRow;
    int requestedStartRow;
    ArrayList<TLRPC.TL_chatInviteImporter> requestedUsers = new ArrayList<>();
    int rowCount;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public View shadow;
    /* access modifiers changed from: private */
    public AnimatorSet shadowAnimation;
    /* access modifiers changed from: private */
    public final long timeDif;
    private TextView titleTextView;
    private boolean titleVisible;
    HashMap<Long, TLRPC.User> users;
    boolean usersLoading;

    public interface InviteDelegate {
        void linkRevoked(TLRPC.TL_chatInviteExported tL_chatInviteExported);

        void onLinkDeleted(TLRPC.TL_chatInviteExported tL_chatInviteExported);

        void onLinkEdited(TLRPC.TL_chatInviteExported tL_chatInviteExported);

        void permanentLinkReplaced(TLRPC.TL_chatInviteExported tL_chatInviteExported, TLRPC.TL_chatInviteExported tL_chatInviteExported2);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public InviteLinkBottomSheet(android.content.Context r26, org.telegram.tgnet.TLRPC.TL_chatInviteExported r27, org.telegram.tgnet.TLRPC.ChatFull r28, java.util.HashMap<java.lang.Long, org.telegram.tgnet.TLRPC.User> r29, org.telegram.ui.ActionBar.BaseFragment r30, long r31, boolean r33, boolean r34) {
        /*
            r25 = this;
            r0 = r25
            r1 = r26
            r2 = r27
            r3 = r29
            r4 = r30
            r5 = r33
            r6 = 0
            r0.<init>(r1, r6)
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            r0.joinedUsers = r7
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            r0.requestedUsers = r7
            r7 = 1
            r0.canEdit = r7
            r0.isNeedReopen = r6
            r0.invite = r2
            r0.users = r3
            r0.fragment = r4
            r8 = r28
            r0.info = r8
            r9 = r31
            r0.chatId = r9
            r0.permanent = r5
            r11 = r34
            r0.isChannel = r11
            if (r3 != 0) goto L_0x0040
            java.util.HashMap r12 = new java.util.HashMap
            r12.<init>()
            r0.users = r12
        L_0x0040:
            int r12 = r0.currentAccount
            org.telegram.tgnet.ConnectionsManager r12 = org.telegram.tgnet.ConnectionsManager.getInstance(r12)
            int r12 = r12.getCurrentTime()
            long r12 = (long) r12
            long r14 = java.lang.System.currentTimeMillis()
            r16 = 1000(0x3e8, double:4.94E-321)
            long r14 = r14 / r16
            long r12 = r12 - r14
            r0.timeDif = r12
            org.telegram.ui.Components.InviteLinkBottomSheet$1 r12 = new org.telegram.ui.Components.InviteLinkBottomSheet$1
            r12.<init>(r1)
            r0.containerView = r12
            android.view.ViewGroup r12 = r0.containerView
            r12.setWillNotDraw(r6)
            android.widget.FrameLayout$LayoutParams r12 = new android.widget.FrameLayout$LayoutParams
            r13 = -1
            int r14 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r15 = 51
            r12.<init>(r13, r14, r15)
            r13 = 1111490560(0x42400000, float:48.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.topMargin = r14
            android.view.View r14 = new android.view.View
            r14.<init>(r1)
            r0.shadow = r14
            r15 = 0
            r14.setAlpha(r15)
            android.view.View r14 = r0.shadow
            r13 = 4
            r14.setVisibility(r13)
            android.view.View r14 = r0.shadow
            java.lang.Integer r15 = java.lang.Integer.valueOf(r7)
            r14.setTag(r15)
            android.view.ViewGroup r14 = r0.containerView
            android.view.View r15 = r0.shadow
            r14.addView(r15, r12)
            org.telegram.ui.Components.InviteLinkBottomSheet$2 r14 = new org.telegram.ui.Components.InviteLinkBottomSheet$2
            r14.<init>(r1)
            r0.listView = r14
            r15 = 14
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            r14.setTag(r15)
            androidx.recyclerview.widget.LinearLayoutManager r14 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r15 = r25.getContext()
            r14.<init>(r15, r7, r6)
            org.telegram.ui.Components.RecyclerListView r15 = r0.listView
            r15.setLayoutManager(r14)
            org.telegram.ui.Components.RecyclerListView r15 = r0.listView
            org.telegram.ui.Components.InviteLinkBottomSheet$Adapter r13 = new org.telegram.ui.Components.InviteLinkBottomSheet$Adapter
            r7 = 0
            r13.<init>()
            r0.adapter = r13
            r15.setAdapter(r13)
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            r7.setVerticalScrollBarEnabled(r6)
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            r7.setClipToPadding(r6)
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            r13 = 1
            r7.setNestedScrollingEnabled(r13)
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            org.telegram.ui.Components.InviteLinkBottomSheet$3 r13 = new org.telegram.ui.Components.InviteLinkBottomSheet$3
            r13.<init>(r14)
            r7.setOnScrollListener(r13)
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            org.telegram.ui.Components.InviteLinkBottomSheet$$ExternalSyntheticLambda4 r13 = new org.telegram.ui.Components.InviteLinkBottomSheet$$ExternalSyntheticLambda4
            r13.<init>(r0, r2, r3, r4)
            r7.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r13)
            android.widget.TextView r7 = new android.widget.TextView
            r7.<init>(r1)
            r0.titleTextView = r7
            r13 = 1
            r7.setLines(r13)
            android.widget.TextView r7 = r0.titleTextView
            r7.setSingleLine(r13)
            android.widget.TextView r7 = r0.titleTextView
            r15 = 1101004800(0x41a00000, float:20.0)
            r7.setTextSize(r13, r15)
            android.widget.TextView r7 = r0.titleTextView
            android.text.TextUtils$TruncateAt r13 = android.text.TextUtils.TruncateAt.END
            r7.setEllipsize(r13)
            android.widget.TextView r7 = r0.titleTextView
            r13 = 1099956224(0x41900000, float:18.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r7.setPadding(r15, r6, r13, r6)
            android.widget.TextView r7 = r0.titleTextView
            r13 = 16
            r7.setGravity(r13)
            android.widget.TextView r7 = r0.titleTextView
            java.lang.String r13 = "fonts/rmedium.ttf"
            android.graphics.Typeface r13 = org.telegram.messenger.AndroidUtilities.getTypeface(r13)
            r7.setTypeface(r13)
            r7 = 2131626014(0x7f0e081e, float:1.8879252E38)
            java.lang.String r13 = "InviteLink"
            if (r5 != 0) goto L_0x0160
            boolean r15 = r2.expired
            if (r15 == 0) goto L_0x013f
            android.widget.TextView r7 = r0.titleTextView
            r13 = 2131625565(0x7f0e065d, float:1.8878342E38)
            java.lang.String r15 = "ExpiredLink"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            r7.setText(r13)
            goto L_0x015b
        L_0x013f:
            boolean r15 = r2.revoked
            if (r15 == 0) goto L_0x0152
            android.widget.TextView r7 = r0.titleTextView
            r13 = 2131627587(0x7f0e0e43, float:1.8882443E38)
            java.lang.String r15 = "RevokedLink"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            r7.setText(r13)
            goto L_0x015b
        L_0x0152:
            android.widget.TextView r15 = r0.titleTextView
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r13, r7)
            r15.setText(r7)
        L_0x015b:
            r7 = 1
            r0.titleVisible = r7
            r13 = 0
            goto L_0x0177
        L_0x0160:
            android.widget.TextView r15 = r0.titleTextView
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r13, r7)
            r15.setText(r7)
            r0.titleVisible = r6
            android.widget.TextView r7 = r0.titleTextView
            r13 = 4
            r7.setVisibility(r13)
            android.widget.TextView r7 = r0.titleTextView
            r13 = 0
            r7.setAlpha(r13)
        L_0x0177:
            java.lang.String r7 = r2.title
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 != 0) goto L_0x01a3
            android.text.SpannableStringBuilder r7 = new android.text.SpannableStringBuilder
            java.lang.String r15 = r2.title
            r7.<init>(r15)
            android.widget.TextView r15 = r0.titleTextView
            android.text.TextPaint r15 = r15.getPaint()
            android.graphics.Paint$FontMetricsInt r15 = r15.getFontMetricsInt()
            android.widget.TextView r13 = r0.titleTextView
            android.text.TextPaint r13 = r13.getPaint()
            float r13 = r13.getTextSize()
            int r13 = (int) r13
            org.telegram.messenger.Emoji.replaceEmoji(r7, r15, r13, r6)
            android.widget.TextView r6 = r0.titleTextView
            r6.setText(r7)
        L_0x01a3:
            android.view.ViewGroup r6 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            r18 = -1
            r19 = -1082130432(0xffffffffbvar_, float:-1.0)
            r20 = 51
            r21 = 0
            boolean r13 = r0.titleVisible
            if (r13 != 0) goto L_0x01b6
            r22 = 0
            goto L_0x01b8
        L_0x01b6:
            r22 = 1111490560(0x42400000, float:48.0)
        L_0x01b8:
            r23 = 0
            r24 = 0
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r6.addView(r7, r13)
            android.view.ViewGroup r6 = r0.containerView
            android.widget.TextView r7 = r0.titleTextView
            r17 = -1
            boolean r13 = r0.titleVisible
            if (r13 != 0) goto L_0x01d0
            r18 = 1111490560(0x42400000, float:48.0)
            goto L_0x01d4
        L_0x01d0:
            r13 = 1112014848(0x42480000, float:50.0)
            r18 = 1112014848(0x42480000, float:50.0)
        L_0x01d4:
            r19 = 51
            r20 = 0
            r21 = 0
            r22 = 0
            r23 = 0
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r6.addView(r7, r13)
            r25.updateRows()
            r25.loadUsers()
            if (r3 == 0) goto L_0x01f9
            long r6 = r2.admin_id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            java.lang.Object r6 = r3.get(r6)
            if (r6 != 0) goto L_0x01fc
        L_0x01f9:
            r25.loadCreator()
        L_0x01fc:
            r25.updateColors()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InviteLinkBottomSheet.<init>(android.content.Context, org.telegram.tgnet.TLRPC$TL_chatInviteExported, org.telegram.tgnet.TLRPC$ChatFull, java.util.HashMap, org.telegram.ui.ActionBar.BaseFragment, long, boolean, boolean):void");
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-InviteLinkBottomSheet  reason: not valid java name */
    public /* synthetic */ void m2363lambda$new$1$orgtelegramuiComponentsInviteLinkBottomSheet(TLRPC.TL_chatInviteExported invite2, HashMap users2, BaseFragment fragment2, View view, int position) {
        if (position != this.creatorRow || invite2.admin_id != UserConfig.getInstance(this.currentAccount).clientUserId) {
            boolean isRequestedUserRow = true;
            boolean isJoinedUserRow = position >= this.joinedStartRow && position < this.joinedEndRow;
            if (position < this.requestedStartRow || position >= this.requestedEndRow) {
                isRequestedUserRow = false;
            }
            if ((position == this.creatorRow || isJoinedUserRow || isRequestedUserRow) && users2 != null) {
                long userId = invite2.admin_id;
                if (isJoinedUserRow) {
                    userId = this.joinedUsers.get(position - this.joinedStartRow).user_id;
                } else if (isRequestedUserRow) {
                    userId = this.requestedUsers.get(position - this.requestedStartRow).user_id;
                }
                TLRPC.User user = (TLRPC.User) users2.get(Long.valueOf(userId));
                if (user != null) {
                    MessagesController.getInstance(UserConfig.selectedAccount).putUser(user, false);
                    AndroidUtilities.runOnUIThread(new InviteLinkBottomSheet$$ExternalSyntheticLambda1(this, user, fragment2), 100);
                    dismiss();
                }
            }
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-InviteLinkBottomSheet  reason: not valid java name */
    public /* synthetic */ void m2362lambda$new$0$orgtelegramuiComponentsInviteLinkBottomSheet(TLRPC.User user, BaseFragment fragment2) {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", user.id);
        fragment2.presentFragment(new ProfileActivity(bundle));
        this.isNeedReopen = true;
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
        int count = this.listView.getHiddenChildCount();
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            updateColorForView(this.listView.getChildAt(i));
        }
        for (int a = 0; a < count; a++) {
            updateColorForView(this.listView.getHiddenChildAt(a));
        }
        int count2 = this.listView.getCachedChildCount();
        for (int a2 = 0; a2 < count2; a2++) {
            updateColorForView(this.listView.getCachedChildAt(a2));
        }
        int count3 = this.listView.getAttachedScrapChildCount();
        for (int a3 = 0; a3 < count3; a3++) {
            updateColorForView(this.listView.getAttachedScrapChildAt(a3));
        }
        this.containerView.invalidate();
    }

    public void show() {
        super.show();
        this.isNeedReopen = false;
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
        RecyclerView.ViewHolder holder = this.listView.getChildViewHolder(view);
        if (holder == null) {
            return;
        }
        if (holder.getItemViewType() == 7) {
            CombinedDrawable combinedDrawable2 = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(view.getContext(), NUM, "windowBackgroundGrayShadow"), 0, 0);
            combinedDrawable2.setFullsize(true);
            view.setBackgroundDrawable(combinedDrawable2);
        } else if (holder.getItemViewType() == 2) {
            CombinedDrawable combinedDrawable3 = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(view.getContext(), NUM, "windowBackgroundGrayShadow"), 0, 0);
            combinedDrawable3.setFullsize(true);
            view.setBackgroundDrawable(combinedDrawable3);
        }
    }

    private void loadCreator() {
        TLRPC.TL_users_getUsers req = new TLRPC.TL_users_getUsers();
        req.id.add(MessagesController.getInstance(UserConfig.selectedAccount).getInputUser(this.invite.admin_id));
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, new InviteLinkBottomSheet$$ExternalSyntheticLambda2(this));
    }

    /* renamed from: lambda$loadCreator$2$org-telegram-ui-Components-InviteLinkBottomSheet  reason: not valid java name */
    public /* synthetic */ void m2359x3644d1d(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (error == null) {
                    InviteLinkBottomSheet.this.users.put(Long.valueOf(InviteLinkBottomSheet.this.invite.admin_id), (TLRPC.User) ((TLRPC.Vector) response).objects.get(0));
                    InviteLinkBottomSheet.this.adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    private void updateRows() {
        boolean needUsers = false;
        this.rowCount = 0;
        this.dividerRow = -1;
        this.divider2Row = -1;
        this.divider3Row = -1;
        this.joinedHeaderRow = -1;
        this.joinedStartRow = -1;
        this.joinedEndRow = -1;
        this.emptyView2 = -1;
        this.emptyView3 = -1;
        this.linkActionRow = -1;
        this.linkInfoRow = -1;
        this.emptyHintRow = -1;
        this.requestedHeaderRow = -1;
        this.requestedStartRow = -1;
        this.requestedEndRow = -1;
        this.loadingRow = -1;
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
        this.rowCount = i4 + 1;
        this.emptyView = i4;
        if (this.invite.usage > 0 || this.invite.usage_limit > 0 || this.invite.requested > 0) {
            needUsers = true;
        }
        boolean usersLoaded = false;
        if (!this.joinedUsers.isEmpty()) {
            int i5 = this.rowCount;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.dividerRow = i5;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.joinedHeaderRow = i6;
            this.joinedStartRow = i7;
            int size = i7 + this.joinedUsers.size();
            this.rowCount = size;
            this.joinedEndRow = size;
            this.rowCount = size + 1;
            this.emptyView2 = size;
            usersLoaded = true;
        }
        if (!this.requestedUsers.isEmpty()) {
            int i8 = this.rowCount;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.divider2Row = i8;
            int i10 = i9 + 1;
            this.rowCount = i10;
            this.requestedHeaderRow = i9;
            this.requestedStartRow = i10;
            int size2 = i10 + this.requestedUsers.size();
            this.rowCount = size2;
            this.requestedEndRow = size2;
            this.rowCount = size2 + 1;
            this.emptyView3 = size2;
            usersLoaded = true;
        }
        if (needUsers && !usersLoaded) {
            int i11 = this.rowCount;
            int i12 = i11 + 1;
            this.rowCount = i12;
            this.dividerRow = i11;
            int i13 = i12 + 1;
            this.rowCount = i13;
            this.loadingRow = i12;
            this.rowCount = i13 + 1;
            this.emptyView2 = i13;
        }
        if (this.emptyHintRow == -1) {
            int i14 = this.rowCount;
            this.rowCount = i14 + 1;
            this.divider3Row = i14;
        }
        this.adapter.notifyDataSetChanged();
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
        }

        public int getItemViewType(int position) {
            if (position == InviteLinkBottomSheet.this.creatorHeaderRow || position == InviteLinkBottomSheet.this.requestedHeaderRow || position == InviteLinkBottomSheet.this.joinedHeaderRow) {
                return 0;
            }
            if (position == InviteLinkBottomSheet.this.creatorRow) {
                return 1;
            }
            if (position >= InviteLinkBottomSheet.this.requestedStartRow && position < InviteLinkBottomSheet.this.requestedEndRow) {
                return 1;
            }
            if (position >= InviteLinkBottomSheet.this.joinedStartRow && position < InviteLinkBottomSheet.this.joinedEndRow) {
                return 1;
            }
            if (position == InviteLinkBottomSheet.this.dividerRow || position == InviteLinkBottomSheet.this.divider2Row) {
                return 2;
            }
            if (position == InviteLinkBottomSheet.this.linkActionRow) {
                return 3;
            }
            if (position == InviteLinkBottomSheet.this.linkInfoRow) {
                return 4;
            }
            if (position == InviteLinkBottomSheet.this.loadingRow) {
                return 5;
            }
            if (position == InviteLinkBottomSheet.this.emptyView || position == InviteLinkBottomSheet.this.emptyView2 || position == InviteLinkBottomSheet.this.emptyView3) {
                return 6;
            }
            if (position == InviteLinkBottomSheet.this.divider3Row) {
                return 7;
            }
            if (position == InviteLinkBottomSheet.this.emptyHintRow) {
                return 8;
            }
            return 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            Context context = parent.getContext();
            switch (viewType) {
                case 1:
                    view = new UserCell(context, 12, 0, true);
                    break;
                case 2:
                    view = new ShadowSectionCell(context, 12, Theme.getColor("windowBackgroundGray"));
                    break;
                case 3:
                    BaseFragment baseFragment = InviteLinkBottomSheet.this.fragment;
                    InviteLinkBottomSheet inviteLinkBottomSheet = InviteLinkBottomSheet.this;
                    LinkActionView linkActionView = new LinkActionView(context, baseFragment, inviteLinkBottomSheet, inviteLinkBottomSheet.chatId, false, InviteLinkBottomSheet.this.isChannel);
                    linkActionView.setDelegate(new LinkActionView.Delegate() {
                        public /* synthetic */ void showUsersForPermanentLink() {
                            LinkActionView.Delegate.CC.$default$showUsersForPermanentLink(this);
                        }

                        public void revokeLink() {
                            if (InviteLinkBottomSheet.this.fragment instanceof ManageLinksActivity) {
                                ((ManageLinksActivity) InviteLinkBottomSheet.this.fragment).revokeLink(InviteLinkBottomSheet.this.invite);
                            } else {
                                TLRPC.TL_messages_editExportedChatInvite req = new TLRPC.TL_messages_editExportedChatInvite();
                                req.link = InviteLinkBottomSheet.this.invite.link;
                                req.revoked = true;
                                req.peer = MessagesController.getInstance(InviteLinkBottomSheet.this.currentAccount).getInputPeer(-InviteLinkBottomSheet.this.chatId);
                                ConnectionsManager.getInstance(InviteLinkBottomSheet.this.currentAccount).sendRequest(req, new InviteLinkBottomSheet$Adapter$1$$ExternalSyntheticLambda3(this));
                            }
                            InviteLinkBottomSheet.this.dismiss();
                        }

                        /* renamed from: lambda$revokeLink$1$org-telegram-ui-Components-InviteLinkBottomSheet$Adapter$1  reason: not valid java name */
                        public /* synthetic */ void m2367x3dCLASSNAMEcc2(TLObject response, TLRPC.TL_error error) {
                            AndroidUtilities.runOnUIThread(new InviteLinkBottomSheet$Adapter$1$$ExternalSyntheticLambda1(this, error, response));
                        }

                        /* renamed from: lambda$revokeLink$0$org-telegram-ui-Components-InviteLinkBottomSheet$Adapter$1  reason: not valid java name */
                        public /* synthetic */ void m2366x37c2CLASSNAME(TLRPC.TL_error error, TLObject response) {
                            if (error != null) {
                                return;
                            }
                            if (response instanceof TLRPC.TL_messages_exportedChatInviteReplaced) {
                                TLRPC.TL_messages_exportedChatInviteReplaced replaced = (TLRPC.TL_messages_exportedChatInviteReplaced) response;
                                if (InviteLinkBottomSheet.this.info != null) {
                                    InviteLinkBottomSheet.this.info.exported_invite = (TLRPC.TL_chatInviteExported) replaced.new_invite;
                                }
                                if (InviteLinkBottomSheet.this.inviteDelegate != null) {
                                    InviteLinkBottomSheet.this.inviteDelegate.permanentLinkReplaced(InviteLinkBottomSheet.this.invite, InviteLinkBottomSheet.this.info.exported_invite);
                                    return;
                                }
                                return;
                            }
                            if (InviteLinkBottomSheet.this.info != null) {
                                TLRPC.ChatFull chatFull = InviteLinkBottomSheet.this.info;
                                chatFull.invitesCount--;
                                if (InviteLinkBottomSheet.this.info.invitesCount < 0) {
                                    InviteLinkBottomSheet.this.info.invitesCount = 0;
                                }
                                MessagesStorage.getInstance(InviteLinkBottomSheet.this.currentAccount).saveChatLinksCount(InviteLinkBottomSheet.this.chatId, InviteLinkBottomSheet.this.info.invitesCount);
                            }
                            if (InviteLinkBottomSheet.this.inviteDelegate != null) {
                                InviteLinkBottomSheet.this.inviteDelegate.linkRevoked(InviteLinkBottomSheet.this.invite);
                            }
                        }

                        public void editLink() {
                            if (InviteLinkBottomSheet.this.fragment instanceof ManageLinksActivity) {
                                ((ManageLinksActivity) InviteLinkBottomSheet.this.fragment).editLink(InviteLinkBottomSheet.this.invite);
                            } else {
                                LinkEditActivity activity = new LinkEditActivity(1, InviteLinkBottomSheet.this.chatId);
                                activity.setInviteToEdit(InviteLinkBottomSheet.this.invite);
                                activity.setCallback(new LinkEditActivity.Callback() {
                                    public void onLinkCreated(TLObject response) {
                                    }

                                    public void onLinkEdited(TLRPC.TL_chatInviteExported inviteToEdit, TLObject response) {
                                        if (InviteLinkBottomSheet.this.inviteDelegate != null) {
                                            InviteLinkBottomSheet.this.inviteDelegate.onLinkEdited(inviteToEdit);
                                        }
                                    }

                                    public void onLinkRemoved(TLRPC.TL_chatInviteExported inviteFinal) {
                                    }

                                    public void revokeLink(TLRPC.TL_chatInviteExported inviteFinal) {
                                    }
                                });
                                InviteLinkBottomSheet.this.fragment.presentFragment(activity);
                            }
                            InviteLinkBottomSheet.this.dismiss();
                        }

                        public void removeLink() {
                            if (InviteLinkBottomSheet.this.fragment instanceof ManageLinksActivity) {
                                ((ManageLinksActivity) InviteLinkBottomSheet.this.fragment).deleteLink(InviteLinkBottomSheet.this.invite);
                            } else {
                                TLRPC.TL_messages_deleteExportedChatInvite req = new TLRPC.TL_messages_deleteExportedChatInvite();
                                req.link = InviteLinkBottomSheet.this.invite.link;
                                req.peer = MessagesController.getInstance(InviteLinkBottomSheet.this.currentAccount).getInputPeer(-InviteLinkBottomSheet.this.chatId);
                                ConnectionsManager.getInstance(InviteLinkBottomSheet.this.currentAccount).sendRequest(req, new InviteLinkBottomSheet$Adapter$1$$ExternalSyntheticLambda2(this));
                            }
                            InviteLinkBottomSheet.this.dismiss();
                        }

                        /* renamed from: lambda$removeLink$3$org-telegram-ui-Components-InviteLinkBottomSheet$Adapter$1  reason: not valid java name */
                        public /* synthetic */ void m2365x3var_(TLObject response, TLRPC.TL_error error) {
                            AndroidUtilities.runOnUIThread(new InviteLinkBottomSheet$Adapter$1$$ExternalSyntheticLambda0(this, error));
                        }

                        /* renamed from: lambda$removeLink$2$org-telegram-ui-Components-InviteLinkBottomSheet$Adapter$1  reason: not valid java name */
                        public /* synthetic */ void m2364x393c9de3(TLRPC.TL_error error) {
                            if (error == null && InviteLinkBottomSheet.this.inviteDelegate != null) {
                                InviteLinkBottomSheet.this.inviteDelegate.onLinkDeleted(InviteLinkBottomSheet.this.invite);
                            }
                        }
                    });
                    linkActionView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    view = linkActionView;
                    break;
                case 4:
                    TimerPrivacyCell timerPrivacyCell = new TimerPrivacyCell(context);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
                    combinedDrawable.setFullsize(true);
                    timerPrivacyCell.setBackground(combinedDrawable);
                    view = timerPrivacyCell;
                    break;
                case 5:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setViewType(10);
                    flickerLoadingView.showDate(false);
                    flickerLoadingView.setPaddingLeft(AndroidUtilities.dp(10.0f));
                    FlickerLoadingView flickerLoadingView2 = flickerLoadingView;
                    view = flickerLoadingView;
                    break;
                case 6:
                    view = new View(context) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(5.0f), NUM));
                        }
                    };
                    break;
                case 7:
                    View view2 = new ShadowSectionCell(context, 12);
                    CombinedDrawable combinedDrawable2 = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"), 0, 0);
                    combinedDrawable2.setFullsize(true);
                    view2.setBackgroundDrawable(combinedDrawable2);
                    view = view2;
                    break;
                case 8:
                    view = new EmptyHintRow(context);
                    break;
                default:
                    HeaderCell headerCell = new HeaderCell(context, "windowBackgroundWhiteBlueHeader", 21, 15, true);
                    headerCell.getTextView2().setTextColor(Theme.getColor("windowBackgroundWhiteRedText"));
                    headerCell.getTextView2().setTextSize(15);
                    headerCell.getTextView2().setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    HeaderCell headerCell2 = headerCell;
                    view = headerCell;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.User user;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == InviteLinkBottomSheet.this.creatorHeaderRow) {
                        headerCell.setText(LocaleController.getString("LinkCreatedeBy", NUM));
                        headerCell.setText2((CharSequence) null);
                        return;
                    } else if (i == InviteLinkBottomSheet.this.joinedHeaderRow) {
                        if (InviteLinkBottomSheet.this.invite.usage > 0) {
                            headerCell.setText(LocaleController.formatPluralString("PeopleJoined", InviteLinkBottomSheet.this.invite.usage));
                        } else {
                            headerCell.setText(LocaleController.getString("NoOneJoined", NUM));
                        }
                        if (InviteLinkBottomSheet.this.invite.expired || InviteLinkBottomSheet.this.invite.revoked || InviteLinkBottomSheet.this.invite.usage_limit <= 0 || InviteLinkBottomSheet.this.invite.usage <= 0) {
                            headerCell.setText2((CharSequence) null);
                            return;
                        } else {
                            headerCell.setText2(LocaleController.formatPluralString("PeopleJoinedRemaining", InviteLinkBottomSheet.this.invite.usage_limit - InviteLinkBottomSheet.this.invite.usage));
                            return;
                        }
                    } else if (i == InviteLinkBottomSheet.this.requestedHeaderRow) {
                        headerCell.setText(LocaleController.formatPluralString("JoinRequests", InviteLinkBottomSheet.this.invite.requested));
                        return;
                    } else {
                        return;
                    }
                case 1:
                    UserCell userCell = (UserCell) viewHolder.itemView;
                    String role = null;
                    String status = null;
                    if (i == InviteLinkBottomSheet.this.creatorRow) {
                        user = InviteLinkBottomSheet.this.users.get(Long.valueOf(InviteLinkBottomSheet.this.invite.admin_id));
                        if (user == null) {
                            user = MessagesController.getInstance(InviteLinkBottomSheet.this.currentAccount).getUser(Long.valueOf(InviteLinkBottomSheet.this.invite.admin_id));
                        }
                        if (user != null) {
                            status = LocaleController.formatDateAudio((long) InviteLinkBottomSheet.this.invite.date, false);
                        }
                        if (!(InviteLinkBottomSheet.this.info == null || user == null || InviteLinkBottomSheet.this.info.participants == null)) {
                            int i2 = 0;
                            while (true) {
                                if (i2 < InviteLinkBottomSheet.this.info.participants.participants.size()) {
                                    if (InviteLinkBottomSheet.this.info.participants.participants.get(i2).user_id == user.id) {
                                        TLRPC.ChatParticipant part = InviteLinkBottomSheet.this.info.participants.participants.get(i2);
                                        if (part instanceof TLRPC.TL_chatChannelParticipant) {
                                            TLRPC.ChannelParticipant channelParticipant = ((TLRPC.TL_chatChannelParticipant) part).channelParticipant;
                                            role = !TextUtils.isEmpty(channelParticipant.rank) ? channelParticipant.rank : channelParticipant instanceof TLRPC.TL_channelParticipantCreator ? LocaleController.getString("ChannelCreator", NUM) : channelParticipant instanceof TLRPC.TL_channelParticipantAdmin ? LocaleController.getString("ChannelAdmin", NUM) : null;
                                        } else {
                                            role = part instanceof TLRPC.TL_chatParticipantCreator ? LocaleController.getString("ChannelCreator", NUM) : part instanceof TLRPC.TL_chatParticipantAdmin ? LocaleController.getString("ChannelAdmin", NUM) : null;
                                        }
                                    } else {
                                        i2++;
                                    }
                                }
                            }
                        }
                    } else {
                        int startRow = InviteLinkBottomSheet.this.joinedStartRow;
                        List<TLRPC.TL_chatInviteImporter> usersList = InviteLinkBottomSheet.this.joinedUsers;
                        if (InviteLinkBottomSheet.this.requestedStartRow != -1 && i >= InviteLinkBottomSheet.this.requestedStartRow) {
                            startRow = InviteLinkBottomSheet.this.requestedStartRow;
                            usersList = InviteLinkBottomSheet.this.requestedUsers;
                        }
                        user = InviteLinkBottomSheet.this.users.get(Long.valueOf(usersList.get(i - startRow).user_id));
                    }
                    userCell.setAdminRole(role);
                    userCell.setData(user, (CharSequence) null, status, 0, false);
                    return;
                case 3:
                    LinkActionView actionView = (LinkActionView) viewHolder.itemView;
                    actionView.setUsers(0, (ArrayList<TLRPC.User>) null);
                    actionView.setLink(InviteLinkBottomSheet.this.invite.link);
                    actionView.setRevoke(InviteLinkBottomSheet.this.invite.revoked);
                    actionView.setPermanent(InviteLinkBottomSheet.this.invite.permanent);
                    actionView.setCanEdit(InviteLinkBottomSheet.this.canEdit);
                    actionView.hideRevokeOption(!InviteLinkBottomSheet.this.canEdit);
                    return;
                case 4:
                    TimerPrivacyCell privacyCell = (TimerPrivacyCell) viewHolder.itemView;
                    privacyCell.cancelTimer();
                    privacyCell.timer = false;
                    privacyCell.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
                    privacyCell.setFixedSize(0);
                    if (InviteLinkBottomSheet.this.invite.revoked) {
                        privacyCell.setText(LocaleController.getString("LinkIsNoActive", NUM));
                        return;
                    } else if (InviteLinkBottomSheet.this.invite.expired) {
                        if (InviteLinkBottomSheet.this.invite.usage_limit <= 0 || InviteLinkBottomSheet.this.invite.usage_limit != InviteLinkBottomSheet.this.invite.usage) {
                            privacyCell.setText(LocaleController.getString("LinkIsExpired", NUM));
                            privacyCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText"));
                            return;
                        }
                        privacyCell.setText(LocaleController.getString("LinkIsExpiredLimitReached", NUM));
                        return;
                    } else if (InviteLinkBottomSheet.this.invite.expire_date > 0) {
                        long currentTime = System.currentTimeMillis() + (InviteLinkBottomSheet.this.timeDif * 1000);
                        long timeLeft = (((long) InviteLinkBottomSheet.this.invite.expire_date) * 1000) - currentTime;
                        if (timeLeft < 0) {
                            timeLeft = 0;
                        }
                        if (timeLeft > 86400000) {
                            privacyCell.setText(LocaleController.formatString("LinkExpiresIn", NUM, LocaleController.formatDateAudio((long) InviteLinkBottomSheet.this.invite.expire_date, false)));
                            return;
                        }
                        long j = currentTime;
                        privacyCell.timer = true;
                        privacyCell.runTimer();
                        privacyCell.setText(LocaleController.formatString("LinkExpiresInTime", NUM, String.format(Locale.ENGLISH, "%02d", new Object[]{Integer.valueOf((int) (((timeLeft / 1000) / 60) / 60))}) + String.format(Locale.ENGLISH, ":%02d", new Object[]{Integer.valueOf((int) (((timeLeft / 1000) / 60) % 60))}) + String.format(Locale.ENGLISH, ":%02d", new Object[]{Integer.valueOf((int) ((timeLeft / 1000) % 60))})));
                        return;
                    } else {
                        privacyCell.setFixedSize(12);
                        privacyCell.setText((CharSequence) null);
                        return;
                    }
                case 8:
                    EmptyHintRow emptyHintRow = (EmptyHintRow) viewHolder.itemView;
                    if (InviteLinkBottomSheet.this.invite.usage_limit > 0) {
                        emptyHintRow.textView.setText(LocaleController.formatPluralString("PeopleCanJoinViaLinkCount", InviteLinkBottomSheet.this.invite.usage_limit));
                        emptyHintRow.textView.setVisibility(0);
                        return;
                    }
                    emptyHintRow.textView.setVisibility(8);
                    return;
                default:
                    return;
            }
        }

        public int getItemCount() {
            return InviteLinkBottomSheet.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == InviteLinkBottomSheet.this.creatorRow ? InviteLinkBottomSheet.this.invite.admin_id != UserConfig.getInstance(InviteLinkBottomSheet.this.currentAccount).clientUserId : (position >= InviteLinkBottomSheet.this.joinedStartRow && position < InviteLinkBottomSheet.this.joinedEndRow) || (position >= InviteLinkBottomSheet.this.requestedStartRow && position < InviteLinkBottomSheet.this.requestedEndRow);
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
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = 0;
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            runShadowAnimation(true);
        } else {
            newOffset = top;
            runShadowAnimation(false);
        }
        if (this.scrollOffsetY != newOffset) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = newOffset;
            recyclerListView2.setTopGlowOffset(newOffset);
            TextView textView = this.titleTextView;
            if (textView != null) {
                textView.setTranslationY((float) this.scrollOffsetY);
            }
            this.shadow.setTranslationY((float) this.scrollOffsetY);
            this.containerView.invalidate();
        }
    }

    private void runShadowAnimation(final boolean show) {
        if ((show && this.shadow.getTag() != null) || (!show && this.shadow.getTag() == null)) {
            this.shadow.setTag(show ? null : 1);
            if (show) {
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
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            if (!this.titleVisible) {
                AnimatorSet animatorSet3 = this.shadowAnimation;
                Animator[] animatorArr2 = new Animator[1];
                TextView textView = this.titleTextView;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                if (!show) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                animatorArr2[0] = ObjectAnimator.ofFloat(textView, property2, fArr2);
                animatorSet3.playTogether(animatorArr2);
            }
            this.shadowAnimation.setDuration(150);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (InviteLinkBottomSheet.this.shadowAnimation != null && InviteLinkBottomSheet.this.shadowAnimation.equals(animation)) {
                        if (!show) {
                            InviteLinkBottomSheet.this.shadow.setVisibility(4);
                        }
                        AnimatorSet unused = InviteLinkBottomSheet.this.shadowAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (InviteLinkBottomSheet.this.shadowAnimation != null && InviteLinkBottomSheet.this.shadowAnimation.equals(animation)) {
                        AnimatorSet unused = InviteLinkBottomSheet.this.shadowAnimation = null;
                    }
                }
            });
            this.shadowAnimation.start();
        }
    }

    public void loadUsers() {
        boolean loadRequestedUsers;
        if (!this.usersLoading) {
            boolean z = false;
            boolean hasMoreJoinedUsers = this.invite.usage > this.joinedUsers.size();
            if (this.invite.request_needed && this.invite.requested > this.requestedUsers.size()) {
                z = true;
            }
            boolean hasMoreRequestedUsers = z;
            if (hasMoreJoinedUsers) {
                loadRequestedUsers = false;
            } else if (hasMoreRequestedUsers) {
                loadRequestedUsers = true;
            } else {
                return;
            }
            List<TLRPC.TL_chatInviteImporter> importersList = loadRequestedUsers ? this.requestedUsers : this.joinedUsers;
            TLRPC.TL_messages_getChatInviteImporters req = new TLRPC.TL_messages_getChatInviteImporters();
            req.flags |= 2;
            req.link = this.invite.link;
            req.peer = MessagesController.getInstance(UserConfig.selectedAccount).getInputPeer(-this.chatId);
            req.requested = loadRequestedUsers;
            if (importersList.isEmpty()) {
                req.offset_user = new TLRPC.TL_inputUserEmpty();
            } else {
                TLRPC.TL_chatInviteImporter invitedUser = importersList.get(importersList.size() - 1);
                req.offset_user = MessagesController.getInstance(this.currentAccount).getInputUser(this.users.get(Long.valueOf(invitedUser.user_id)));
                req.offset_date = invitedUser.date;
            }
            this.usersLoading = true;
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, new InviteLinkBottomSheet$$ExternalSyntheticLambda3(this, importersList, loadRequestedUsers, hasMoreRequestedUsers));
        }
    }

    /* renamed from: lambda$loadUsers$4$org-telegram-ui-Components-InviteLinkBottomSheet  reason: not valid java name */
    public /* synthetic */ void m2361x3da8babf(List importersList, boolean loadRequestedUsers, boolean hasMoreRequestedUsers, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new InviteLinkBottomSheet$$ExternalSyntheticLambda0(this, error, response, importersList, loadRequestedUsers, hasMoreRequestedUsers));
    }

    /* renamed from: lambda$loadUsers$3$org-telegram-ui-Components-InviteLinkBottomSheet  reason: not valid java name */
    public /* synthetic */ void m2360x4bfvar_a0(TLRPC.TL_error error, TLObject response, List importersList, boolean loadRequestedUsers, boolean hasMoreRequestedUsers) {
        if (error == null) {
            TLRPC.TL_messages_chatInviteImporters inviteImporters = (TLRPC.TL_messages_chatInviteImporters) response;
            importersList.addAll(inviteImporters.importers);
            for (int i = 0; i < inviteImporters.users.size(); i++) {
                TLRPC.User user = inviteImporters.users.get(i);
                this.users.put(Long.valueOf(user.id), user);
            }
            boolean z = true;
            if (loadRequestedUsers) {
                if (importersList.size() >= inviteImporters.count) {
                    z = false;
                }
            } else if (importersList.size() >= inviteImporters.count && !hasMoreRequestedUsers) {
                z = false;
            }
            this.hasMore = z;
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
                int p;
                if (!(InviteLinkBottomSheet.this.listView == null || InviteLinkBottomSheet.this.listView.getAdapter() == null || (p = InviteLinkBottomSheet.this.listView.getChildAdapterPosition(TimerPrivacyCell.this)) < 0)) {
                    InviteLinkBottomSheet.this.adapter.onBindViewHolder(InviteLinkBottomSheet.this.listView.getChildViewHolder(TimerPrivacyCell.this), p);
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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(84.0f), NUM));
        }
    }

    public void setCanEdit(boolean canEdit2) {
        this.canEdit = canEdit2;
    }
}
