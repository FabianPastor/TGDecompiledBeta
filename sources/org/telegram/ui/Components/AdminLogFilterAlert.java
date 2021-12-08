package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.CheckBoxUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.RecyclerListView;

public class AdminLogFilterAlert extends BottomSheet {
    private ListAdapter adapter;
    /* access modifiers changed from: private */
    public int adminsRow;
    /* access modifiers changed from: private */
    public int allAdminsRow;
    /* access modifiers changed from: private */
    public int callsRow;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.ChannelParticipant> currentAdmins;
    /* access modifiers changed from: private */
    public TLRPC.TL_channelAdminLogEventsFilter currentFilter;
    private AdminLogFilterAlertDelegate delegate;
    /* access modifiers changed from: private */
    public int deleteRow;
    /* access modifiers changed from: private */
    public int editRow;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    /* access modifiers changed from: private */
    public int infoRow;
    /* access modifiers changed from: private */
    public int invitesRow;
    /* access modifiers changed from: private */
    public boolean isMegagroup;
    /* access modifiers changed from: private */
    public int leavingRow;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public int membersRow;
    private FrameLayout pickerBottomLayout;
    /* access modifiers changed from: private */
    public int pinnedRow;
    private int reqId;
    /* access modifiers changed from: private */
    public int restrictionsRow;
    private BottomSheet.BottomSheetCell saveButton;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC.User> selectedAdmins;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private Pattern urlPattern;

    public interface AdminLogFilterAlertDelegate {
        void didSelectRights(TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, LongSparseArray<TLRPC.User> longSparseArray);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public AdminLogFilterAlert(android.content.Context r18, org.telegram.tgnet.TLRPC.TL_channelAdminLogEventsFilter r19, androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC.User> r20, boolean r21) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = r19
            r3 = r21
            r4 = 0
            r0.<init>(r1, r4)
            if (r2 == 0) goto L_0x0073
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = new org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter
            r5.<init>()
            r0.currentFilter = r5
            boolean r6 = r2.join
            r5.join = r6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.leave
            r5.leave = r6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.invite
            r5.invite = r6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.ban
            r5.ban = r6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.unban
            r5.unban = r6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.kick
            r5.kick = r6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.unkick
            r5.unkick = r6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.promote
            r5.promote = r6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.demote
            r5.demote = r6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.info
            r5.info = r6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.settings
            r5.settings = r6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.pinned
            r5.pinned = r6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.edit
            r5.edit = r6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.delete
            r5.delete = r6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.group_call
            r5.group_call = r6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.invites
            r5.invites = r6
        L_0x0073:
            if (r20 == 0) goto L_0x007b
            androidx.collection.LongSparseArray r5 = r20.clone()
            r0.selectedAdmins = r5
        L_0x007b:
            r0.isMegagroup = r3
            r5 = 1
            r6 = -1
            if (r3 == 0) goto L_0x0087
            int r7 = r5 + 1
            r0.restrictionsRow = r5
            r5 = r7
            goto L_0x0089
        L_0x0087:
            r0.restrictionsRow = r6
        L_0x0089:
            int r7 = r5 + 1
            r0.adminsRow = r5
            int r5 = r7 + 1
            r0.membersRow = r7
            int r7 = r5 + 1
            r0.invitesRow = r5
            int r5 = r7 + 1
            r0.infoRow = r7
            int r7 = r5 + 1
            r0.deleteRow = r5
            int r5 = r7 + 1
            r0.editRow = r7
            if (r3 == 0) goto L_0x00a9
            int r7 = r5 + 1
            r0.pinnedRow = r5
            r5 = r7
            goto L_0x00ab
        L_0x00a9:
            r0.pinnedRow = r6
        L_0x00ab:
            int r7 = r5 + 1
            r0.leavingRow = r5
            int r5 = r7 + 1
            r0.callsRow = r7
            r7 = 1
            int r5 = r5 + r7
            r0.allAdminsRow = r5
            android.content.res.Resources r8 = r18.getResources()
            r9 = 2131166073(0x7var_, float:1.7946381E38)
            android.graphics.drawable.Drawable r8 = r8.getDrawable(r9)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r0.shadowDrawable = r8
            android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
            java.lang.String r10 = "dialogBackground"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r9.<init>(r10, r11)
            r8.setColorFilter(r9)
            org.telegram.ui.Components.AdminLogFilterAlert$1 r8 = new org.telegram.ui.Components.AdminLogFilterAlert$1
            r8.<init>(r1)
            r0.containerView = r8
            android.view.ViewGroup r8 = r0.containerView
            r8.setWillNotDraw(r4)
            android.view.ViewGroup r8 = r0.containerView
            int r9 = r0.backgroundPaddingLeft
            int r10 = r0.backgroundPaddingLeft
            r8.setPadding(r9, r4, r10, r4)
            org.telegram.ui.Components.AdminLogFilterAlert$2 r8 = new org.telegram.ui.Components.AdminLogFilterAlert$2
            r8.<init>(r1)
            r0.listView = r8
            androidx.recyclerview.widget.LinearLayoutManager r9 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r10 = r17.getContext()
            r9.<init>(r10, r7, r4)
            r8.setLayoutManager(r9)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            org.telegram.ui.Components.AdminLogFilterAlert$ListAdapter r9 = new org.telegram.ui.Components.AdminLogFilterAlert$ListAdapter
            r9.<init>(r1)
            r0.adapter = r9
            r8.setAdapter(r9)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            r8.setVerticalScrollBarEnabled(r4)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            r8.setClipToPadding(r4)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            r8.setEnabled(r7)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            java.lang.String r9 = "dialogScrollGlow"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r8.setGlowColor(r9)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            org.telegram.ui.Components.AdminLogFilterAlert$3 r9 = new org.telegram.ui.Components.AdminLogFilterAlert$3
            r9.<init>()
            r8.setOnScrollListener(r9)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            org.telegram.ui.Components.AdminLogFilterAlert$$ExternalSyntheticLambda1 r9 = new org.telegram.ui.Components.AdminLogFilterAlert$$ExternalSyntheticLambda1
            r9.<init>(r0)
            r8.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r9)
            android.view.ViewGroup r8 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r9 = r0.listView
            r10 = -1
            r11 = -1082130432(0xffffffffbvar_, float:-1.0)
            r12 = 51
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r8.addView(r9, r10)
            android.view.View r8 = new android.view.View
            r8.<init>(r1)
            r9 = 2131165483(0x7var_b, float:1.7945184E38)
            r8.setBackgroundResource(r9)
            android.view.ViewGroup r9 = r0.containerView
            r10 = -1
            r11 = 1077936128(0x40400000, float:3.0)
            r12 = 83
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r9.addView(r8, r10)
            org.telegram.ui.ActionBar.BottomSheet$BottomSheetCell r9 = new org.telegram.ui.ActionBar.BottomSheet$BottomSheetCell
            r9.<init>(r1, r7)
            r0.saveButton = r9
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r4)
            r9.setBackgroundDrawable(r7)
            org.telegram.ui.ActionBar.BottomSheet$BottomSheetCell r7 = r0.saveButton
            r9 = 2131627592(0x7f0e0e48, float:1.8882453E38)
            java.lang.String r10 = "Save"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            java.lang.String r9 = r9.toUpperCase()
            r7.setTextAndIcon((java.lang.CharSequence) r9, (int) r4)
            org.telegram.ui.ActionBar.BottomSheet$BottomSheetCell r4 = r0.saveButton
            java.lang.String r7 = "dialogTextBlue2"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r4.setTextColor(r7)
            org.telegram.ui.ActionBar.BottomSheet$BottomSheetCell r4 = r0.saveButton
            org.telegram.ui.Components.AdminLogFilterAlert$$ExternalSyntheticLambda0 r7 = new org.telegram.ui.Components.AdminLogFilterAlert$$ExternalSyntheticLambda0
            r7.<init>(r0)
            r4.setOnClickListener(r7)
            android.view.ViewGroup r4 = r0.containerView
            org.telegram.ui.ActionBar.BottomSheet$BottomSheetCell r7 = r0.saveButton
            r9 = 48
            r10 = 83
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r9, (int) r10)
            r4.addView(r7, r6)
            org.telegram.ui.Components.AdminLogFilterAlert$ListAdapter r4 = r0.adapter
            r4.notifyDataSetChanged()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AdminLogFilterAlert.<init>(android.content.Context, org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter, androidx.collection.LongSparseArray, boolean):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-AdminLogFilterAlert  reason: not valid java name */
    public /* synthetic */ void m1989lambda$new$0$orgtelegramuiComponentsAdminLogFilterAlert(View view, int position) {
        if (view instanceof CheckBoxCell) {
            CheckBoxCell cell = (CheckBoxCell) view;
            boolean isChecked = cell.isChecked();
            cell.setChecked(!isChecked, true);
            if (position == 0) {
                if (isChecked) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter = new TLRPC.TL_channelAdminLogEventsFilter();
                    this.currentFilter = tL_channelAdminLogEventsFilter;
                    tL_channelAdminLogEventsFilter.invites = false;
                    tL_channelAdminLogEventsFilter.group_call = false;
                    tL_channelAdminLogEventsFilter.delete = false;
                    tL_channelAdminLogEventsFilter.edit = false;
                    tL_channelAdminLogEventsFilter.pinned = false;
                    tL_channelAdminLogEventsFilter.settings = false;
                    tL_channelAdminLogEventsFilter.info = false;
                    tL_channelAdminLogEventsFilter.demote = false;
                    tL_channelAdminLogEventsFilter.promote = false;
                    tL_channelAdminLogEventsFilter.unkick = false;
                    tL_channelAdminLogEventsFilter.kick = false;
                    tL_channelAdminLogEventsFilter.unban = false;
                    tL_channelAdminLogEventsFilter.ban = false;
                    tL_channelAdminLogEventsFilter.invite = false;
                    tL_channelAdminLogEventsFilter.leave = false;
                    tL_channelAdminLogEventsFilter.join = false;
                } else {
                    this.currentFilter = null;
                }
                int count = this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.listView.getChildAt(a);
                    RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(child);
                    int pos = holder.getAdapterPosition();
                    if (holder.getItemViewType() == 0 && pos > 0 && pos < this.allAdminsRow - 1) {
                        ((CheckBoxCell) child).setChecked(!isChecked, true);
                    }
                }
            } else if (position == this.allAdminsRow) {
                if (isChecked) {
                    this.selectedAdmins = new LongSparseArray<>();
                } else {
                    this.selectedAdmins = null;
                }
                int count2 = this.listView.getChildCount();
                for (int a2 = 0; a2 < count2; a2++) {
                    View child2 = this.listView.getChildAt(a2);
                    RecyclerView.ViewHolder holder2 = this.listView.findContainingViewHolder(child2);
                    int adapterPosition = holder2.getAdapterPosition();
                    if (holder2.getItemViewType() == 2) {
                        ((CheckBoxUserCell) child2).setChecked(!isChecked, true);
                    }
                }
            } else {
                if (this.currentFilter == null) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter2 = new TLRPC.TL_channelAdminLogEventsFilter();
                    this.currentFilter = tL_channelAdminLogEventsFilter2;
                    tL_channelAdminLogEventsFilter2.invites = true;
                    tL_channelAdminLogEventsFilter2.group_call = true;
                    tL_channelAdminLogEventsFilter2.delete = true;
                    tL_channelAdminLogEventsFilter2.edit = true;
                    tL_channelAdminLogEventsFilter2.pinned = true;
                    tL_channelAdminLogEventsFilter2.settings = true;
                    tL_channelAdminLogEventsFilter2.info = true;
                    tL_channelAdminLogEventsFilter2.demote = true;
                    tL_channelAdminLogEventsFilter2.promote = true;
                    tL_channelAdminLogEventsFilter2.unkick = true;
                    tL_channelAdminLogEventsFilter2.kick = true;
                    tL_channelAdminLogEventsFilter2.unban = true;
                    tL_channelAdminLogEventsFilter2.ban = true;
                    tL_channelAdminLogEventsFilter2.invite = true;
                    tL_channelAdminLogEventsFilter2.leave = true;
                    tL_channelAdminLogEventsFilter2.join = true;
                    RecyclerView.ViewHolder holder3 = this.listView.findViewHolderForAdapterPosition(0);
                    if (holder3 != null) {
                        ((CheckBoxCell) holder3.itemView).setChecked(false, true);
                    }
                }
                if (position == this.restrictionsRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter3 = this.currentFilter;
                    boolean z = !tL_channelAdminLogEventsFilter3.kick;
                    tL_channelAdminLogEventsFilter3.unban = z;
                    tL_channelAdminLogEventsFilter3.unkick = z;
                    tL_channelAdminLogEventsFilter3.ban = z;
                    tL_channelAdminLogEventsFilter3.kick = z;
                } else if (position == this.adminsRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter4 = this.currentFilter;
                    boolean z2 = !tL_channelAdminLogEventsFilter4.demote;
                    tL_channelAdminLogEventsFilter4.demote = z2;
                    tL_channelAdminLogEventsFilter4.promote = z2;
                } else if (position == this.membersRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter5 = this.currentFilter;
                    boolean z3 = !tL_channelAdminLogEventsFilter5.join;
                    tL_channelAdminLogEventsFilter5.join = z3;
                    tL_channelAdminLogEventsFilter5.invite = z3;
                } else if (position == this.infoRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter6 = this.currentFilter;
                    boolean z4 = !tL_channelAdminLogEventsFilter6.info;
                    tL_channelAdminLogEventsFilter6.settings = z4;
                    tL_channelAdminLogEventsFilter6.info = z4;
                } else if (position == this.deleteRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter7 = this.currentFilter;
                    tL_channelAdminLogEventsFilter7.delete = !tL_channelAdminLogEventsFilter7.delete;
                } else if (position == this.editRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter8 = this.currentFilter;
                    tL_channelAdminLogEventsFilter8.edit = !tL_channelAdminLogEventsFilter8.edit;
                } else if (position == this.pinnedRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter9 = this.currentFilter;
                    tL_channelAdminLogEventsFilter9.pinned = !tL_channelAdminLogEventsFilter9.pinned;
                } else if (position == this.leavingRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter10 = this.currentFilter;
                    tL_channelAdminLogEventsFilter10.leave = !tL_channelAdminLogEventsFilter10.leave;
                } else if (position == this.callsRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter11 = this.currentFilter;
                    tL_channelAdminLogEventsFilter11.group_call = !tL_channelAdminLogEventsFilter11.group_call;
                } else if (position == this.invitesRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter12 = this.currentFilter;
                    tL_channelAdminLogEventsFilter12.invites = !tL_channelAdminLogEventsFilter12.invites;
                }
            }
            TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter13 = this.currentFilter;
            if (tL_channelAdminLogEventsFilter13 == null || tL_channelAdminLogEventsFilter13.join || this.currentFilter.leave || this.currentFilter.invite || this.currentFilter.ban || this.currentFilter.invites || this.currentFilter.unban || this.currentFilter.kick || this.currentFilter.unkick || this.currentFilter.promote || this.currentFilter.demote || this.currentFilter.info || this.currentFilter.settings || this.currentFilter.pinned || this.currentFilter.edit || this.currentFilter.delete || this.currentFilter.group_call) {
                this.saveButton.setEnabled(true);
                this.saveButton.setAlpha(1.0f);
                return;
            }
            this.saveButton.setEnabled(false);
            this.saveButton.setAlpha(0.5f);
        } else if (view instanceof CheckBoxUserCell) {
            CheckBoxUserCell checkBoxUserCell = (CheckBoxUserCell) view;
            if (this.selectedAdmins == null) {
                this.selectedAdmins = new LongSparseArray<>();
                RecyclerView.ViewHolder holder4 = this.listView.findViewHolderForAdapterPosition(this.allAdminsRow);
                if (holder4 != null) {
                    ((CheckBoxCell) holder4.itemView).setChecked(false, true);
                }
                for (int a3 = 0; a3 < this.currentAdmins.size(); a3++) {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(this.currentAdmins.get(a3).peer)));
                    this.selectedAdmins.put(user.id, user);
                }
            }
            boolean isChecked2 = checkBoxUserCell.isChecked();
            TLRPC.User user2 = checkBoxUserCell.getCurrentUser();
            if (isChecked2) {
                this.selectedAdmins.remove(user2.id);
            } else {
                this.selectedAdmins.put(user2.id, user2);
            }
            checkBoxUserCell.setChecked(!isChecked2, true);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-AdminLogFilterAlert  reason: not valid java name */
    public /* synthetic */ void m1990lambda$new$1$orgtelegramuiComponentsAdminLogFilterAlert(View v) {
        this.delegate.didSelectRights(this.currentFilter, this.selectedAdmins);
        dismiss();
    }

    public void setCurrentAdmins(ArrayList<TLRPC.ChannelParticipant> admins) {
        this.currentAdmins = admins;
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public void setAdminLogFilterAlertDelegate(AdminLogFilterAlertDelegate adminLogFilterAlertDelegate) {
        this.delegate = adminLogFilterAlertDelegate;
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        int newOffset = 0;
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop() - AndroidUtilities.dp(8.0f);
        if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        if (this.scrollOffsetY != newOffset) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = newOffset;
            recyclerListView2.setTopGlowOffset(newOffset);
            this.containerView.invalidate();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public ListAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            return (AdminLogFilterAlert.this.isMegagroup ? 11 : 8) + (AdminLogFilterAlert.this.currentAdmins != null ? AdminLogFilterAlert.this.currentAdmins.size() + 2 : 0);
        }

        public int getItemViewType(int position) {
            if (position < AdminLogFilterAlert.this.allAdminsRow - 1 || position == AdminLogFilterAlert.this.allAdminsRow) {
                return 0;
            }
            if (position == AdminLogFilterAlert.this.allAdminsRow - 1) {
                return 1;
            }
            return 2;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FrameLayout view = null;
            switch (viewType) {
                case 0:
                    view = new CheckBoxCell(this.context, 1, 21, AdminLogFilterAlert.this.resourcesProvider);
                    break;
                case 1:
                    ShadowSectionCell shadowSectionCell = new ShadowSectionCell(this.context, 18);
                    view = new FrameLayout(this.context);
                    view.addView(shadowSectionCell, LayoutHelper.createFrame(-1, -1.0f));
                    view.setBackgroundColor(Theme.getColor("dialogBackgroundGray"));
                    break;
                case 2:
                    view = new CheckBoxUserCell(this.context, true);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    CheckBoxCell cell = (CheckBoxCell) holder.itemView;
                    if (position == 0) {
                        if (AdminLogFilterAlert.this.currentFilter != null) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.restrictionsRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && (!AdminLogFilterAlert.this.currentFilter.kick || !AdminLogFilterAlert.this.currentFilter.ban || !AdminLogFilterAlert.this.currentFilter.unkick || !AdminLogFilterAlert.this.currentFilter.unban)) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.adminsRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && (!AdminLogFilterAlert.this.currentFilter.promote || !AdminLogFilterAlert.this.currentFilter.demote)) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.membersRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && (!AdminLogFilterAlert.this.currentFilter.invite || !AdminLogFilterAlert.this.currentFilter.join)) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.infoRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.info) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.deleteRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.delete) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.editRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.edit) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.pinnedRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.pinned) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.leavingRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.leave) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.callsRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.group_call) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.invitesRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.invites) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.allAdminsRow) {
                        if (AdminLogFilterAlert.this.selectedAdmins != null) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    CheckBoxUserCell userCell = (CheckBoxUserCell) holder.itemView;
                    long userId = MessageObject.getPeerId(((TLRPC.ChannelParticipant) AdminLogFilterAlert.this.currentAdmins.get((position - AdminLogFilterAlert.this.allAdminsRow) - 1)).peer);
                    if (AdminLogFilterAlert.this.selectedAdmins != null && AdminLogFilterAlert.this.selectedAdmins.indexOfKey(userId) < 0) {
                        z = false;
                    }
                    userCell.setChecked(z, false);
                    return;
                default:
                    return;
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean z = false;
            boolean z2 = true;
            switch (holder.getItemViewType()) {
                case 0:
                    CheckBoxCell cell = (CheckBoxCell) holder.itemView;
                    if (position == 0) {
                        String string = LocaleController.getString("EventLogFilterAll", NUM);
                        if (AdminLogFilterAlert.this.currentFilter == null) {
                            z = true;
                        }
                        cell.setText(string, "", z, true);
                        return;
                    } else if (position == AdminLogFilterAlert.this.restrictionsRow) {
                        String string2 = LocaleController.getString("EventLogFilterNewRestrictions", NUM);
                        if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.kick && AdminLogFilterAlert.this.currentFilter.ban && AdminLogFilterAlert.this.currentFilter.unkick && AdminLogFilterAlert.this.currentFilter.unban)) {
                            z = true;
                        }
                        cell.setText(string2, "", z, true);
                        return;
                    } else if (position == AdminLogFilterAlert.this.adminsRow) {
                        String string3 = LocaleController.getString("EventLogFilterNewAdmins", NUM);
                        if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.promote && AdminLogFilterAlert.this.currentFilter.demote)) {
                            z = true;
                        }
                        cell.setText(string3, "", z, true);
                        return;
                    } else if (position == AdminLogFilterAlert.this.membersRow) {
                        String string4 = LocaleController.getString("EventLogFilterNewMembers", NUM);
                        if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.invite && AdminLogFilterAlert.this.currentFilter.join)) {
                            z = true;
                        }
                        cell.setText(string4, "", z, true);
                        return;
                    } else if (position == AdminLogFilterAlert.this.infoRow) {
                        if (AdminLogFilterAlert.this.isMegagroup) {
                            String string5 = LocaleController.getString("EventLogFilterGroupInfo", NUM);
                            if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.info) {
                                z = true;
                            }
                            cell.setText(string5, "", z, true);
                            return;
                        }
                        String string6 = LocaleController.getString("EventLogFilterChannelInfo", NUM);
                        if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.info) {
                            z = true;
                        }
                        cell.setText(string6, "", z, true);
                        return;
                    } else if (position == AdminLogFilterAlert.this.deleteRow) {
                        String string7 = LocaleController.getString("EventLogFilterDeletedMessages", NUM);
                        if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.delete) {
                            z = true;
                        }
                        cell.setText(string7, "", z, true);
                        return;
                    } else if (position == AdminLogFilterAlert.this.editRow) {
                        String string8 = LocaleController.getString("EventLogFilterEditedMessages", NUM);
                        if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.edit) {
                            z = true;
                        }
                        cell.setText(string8, "", z, true);
                        return;
                    } else if (position == AdminLogFilterAlert.this.pinnedRow) {
                        String string9 = LocaleController.getString("EventLogFilterPinnedMessages", NUM);
                        if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.pinned) {
                            z = true;
                        }
                        cell.setText(string9, "", z, true);
                        return;
                    } else if (position == AdminLogFilterAlert.this.leavingRow) {
                        String string10 = LocaleController.getString("EventLogFilterLeavingMembers", NUM);
                        boolean z3 = AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.leave;
                        if (AdminLogFilterAlert.this.callsRow != -1) {
                            z = true;
                        }
                        cell.setText(string10, "", z3, z);
                        return;
                    } else if (position == AdminLogFilterAlert.this.callsRow) {
                        String string11 = LocaleController.getString("EventLogFilterCalls", NUM);
                        if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.group_call) {
                            z2 = false;
                        }
                        cell.setText(string11, "", z2, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.invitesRow) {
                        String string12 = LocaleController.getString("EventLogFilterInvites", NUM);
                        if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.invites) {
                            z = true;
                        }
                        cell.setText(string12, "", z, true);
                        return;
                    } else if (position == AdminLogFilterAlert.this.allAdminsRow) {
                        String string13 = LocaleController.getString("EventLogAllAdmins", NUM);
                        if (AdminLogFilterAlert.this.selectedAdmins == null) {
                            z = true;
                        }
                        cell.setText(string13, "", z, true);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    CheckBoxUserCell userCell = (CheckBoxUserCell) holder.itemView;
                    long userId = MessageObject.getPeerId(((TLRPC.ChannelParticipant) AdminLogFilterAlert.this.currentAdmins.get((position - AdminLogFilterAlert.this.allAdminsRow) - 1)).peer);
                    TLRPC.User user = MessagesController.getInstance(AdminLogFilterAlert.this.currentAccount).getUser(Long.valueOf(userId));
                    boolean z4 = AdminLogFilterAlert.this.selectedAdmins == null || AdminLogFilterAlert.this.selectedAdmins.indexOfKey(userId) >= 0;
                    if (position != getItemCount() - 1) {
                        z = true;
                    }
                    userCell.setUser(user, z4, z);
                    return;
                default:
                    return;
            }
        }
    }
}
