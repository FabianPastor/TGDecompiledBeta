package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.CheckBoxUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ContentPreviewViewer;

public class AdminLogFilterAlert extends BottomSheet {
    private ListAdapter adapter;
    /* access modifiers changed from: private */
    public int adminsRow;
    /* access modifiers changed from: private */
    public int allAdminsRow;
    /* access modifiers changed from: private */
    public int callsRow;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$ChannelParticipant> currentAdmins;
    /* access modifiers changed from: private */
    public TLRPC$TL_channelAdminLogEventsFilter currentFilter;
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
    /* access modifiers changed from: private */
    public int pinnedRow;
    /* access modifiers changed from: private */
    public int restrictionsRow;
    private BottomSheet.BottomSheetCell saveButton;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public SparseArray<TLRPC$User> selectedAdmins;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;

    public interface AdminLogFilterAlertDelegate {
        void didSelectRights(TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter, SparseArray<TLRPC$User> sparseArray);
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public AdminLogFilterAlert(Context context, TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter, SparseArray<TLRPC$User> sparseArray, boolean z) {
        super(context, false);
        int i;
        if (tLRPC$TL_channelAdminLogEventsFilter != null) {
            TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter2 = new TLRPC$TL_channelAdminLogEventsFilter();
            this.currentFilter = tLRPC$TL_channelAdminLogEventsFilter2;
            tLRPC$TL_channelAdminLogEventsFilter2.join = tLRPC$TL_channelAdminLogEventsFilter.join;
            tLRPC$TL_channelAdminLogEventsFilter2.leave = tLRPC$TL_channelAdminLogEventsFilter.leave;
            tLRPC$TL_channelAdminLogEventsFilter2.invite = tLRPC$TL_channelAdminLogEventsFilter.invite;
            tLRPC$TL_channelAdminLogEventsFilter2.ban = tLRPC$TL_channelAdminLogEventsFilter.ban;
            tLRPC$TL_channelAdminLogEventsFilter2.unban = tLRPC$TL_channelAdminLogEventsFilter.unban;
            tLRPC$TL_channelAdminLogEventsFilter2.kick = tLRPC$TL_channelAdminLogEventsFilter.kick;
            tLRPC$TL_channelAdminLogEventsFilter2.unkick = tLRPC$TL_channelAdminLogEventsFilter.unkick;
            tLRPC$TL_channelAdminLogEventsFilter2.promote = tLRPC$TL_channelAdminLogEventsFilter.promote;
            tLRPC$TL_channelAdminLogEventsFilter2.demote = tLRPC$TL_channelAdminLogEventsFilter.demote;
            tLRPC$TL_channelAdminLogEventsFilter2.info = tLRPC$TL_channelAdminLogEventsFilter.info;
            tLRPC$TL_channelAdminLogEventsFilter2.settings = tLRPC$TL_channelAdminLogEventsFilter.settings;
            tLRPC$TL_channelAdminLogEventsFilter2.pinned = tLRPC$TL_channelAdminLogEventsFilter.pinned;
            tLRPC$TL_channelAdminLogEventsFilter2.edit = tLRPC$TL_channelAdminLogEventsFilter.edit;
            tLRPC$TL_channelAdminLogEventsFilter2.delete = tLRPC$TL_channelAdminLogEventsFilter.delete;
            tLRPC$TL_channelAdminLogEventsFilter2.group_call = tLRPC$TL_channelAdminLogEventsFilter.group_call;
            tLRPC$TL_channelAdminLogEventsFilter2.invites = tLRPC$TL_channelAdminLogEventsFilter.invites;
        }
        if (sparseArray != null) {
            this.selectedAdmins = sparseArray.clone();
        }
        this.isMegagroup = z;
        if (z) {
            this.restrictionsRow = 1;
            i = 2;
        } else {
            this.restrictionsRow = -1;
            i = 1;
        }
        int i2 = i + 1;
        this.adminsRow = i;
        int i3 = i2 + 1;
        this.membersRow = i2;
        int i4 = i3 + 1;
        this.invitesRow = i3;
        int i5 = i4 + 1;
        this.infoRow = i4;
        int i6 = i5 + 1;
        this.deleteRow = i5;
        int i7 = i6 + 1;
        this.editRow = i6;
        if (z) {
            this.pinnedRow = i7;
            i7++;
        } else {
            this.pinnedRow = -1;
        }
        int i8 = i7 + 1;
        this.leavingRow = i7;
        this.callsRow = i8;
        this.allAdminsRow = i8 + 2;
        Drawable mutate = context.getResources().getDrawable(NUM).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        AnonymousClass1 r11 = new FrameLayout(context) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || AdminLogFilterAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) AdminLogFilterAlert.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                AdminLogFilterAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return !AdminLogFilterAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i2);
                if (Build.VERSION.SDK_INT >= 21) {
                    size -= AndroidUtilities.statusBarHeight;
                }
                getMeasuredWidth();
                int dp = AndroidUtilities.dp(48.0f) + ((AdminLogFilterAlert.this.isMegagroup ? 10 : 7) * AndroidUtilities.dp(48.0f)) + AdminLogFilterAlert.this.backgroundPaddingTop;
                if (AdminLogFilterAlert.this.currentAdmins != null) {
                    dp += ((AdminLogFilterAlert.this.currentAdmins.size() + 1) * AndroidUtilities.dp(48.0f)) + AndroidUtilities.dp(20.0f);
                }
                int i3 = size / 5;
                int i4 = ((float) dp) < ((float) i3) * 3.2f ? 0 : i3 * 2;
                if (i4 != 0 && dp < size) {
                    i4 -= size - dp;
                }
                if (i4 == 0) {
                    i4 = AdminLogFilterAlert.this.backgroundPaddingTop;
                }
                if (AdminLogFilterAlert.this.listView.getPaddingTop() != i4) {
                    boolean unused = AdminLogFilterAlert.this.ignoreLayout = true;
                    AdminLogFilterAlert.this.listView.setPadding(0, i4, 0, 0);
                    boolean unused2 = AdminLogFilterAlert.this.ignoreLayout = false;
                }
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(Math.min(dp, size), NUM));
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                AdminLogFilterAlert.this.updateLayout();
            }

            public void requestLayout() {
                if (!AdminLogFilterAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                AdminLogFilterAlert.this.shadowDrawable.setBounds(0, AdminLogFilterAlert.this.scrollOffsetY - AdminLogFilterAlert.this.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                AdminLogFilterAlert.this.shadowDrawable.draw(canvas);
            }
        };
        this.containerView = r11;
        r11.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        int i9 = this.backgroundPaddingLeft;
        viewGroup.setPadding(i9, 0, i9, 0);
        AnonymousClass2 r112 = new RecyclerListView(context) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                boolean onInterceptTouchEvent = ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, AdminLogFilterAlert.this.listView, 0, (ContentPreviewViewer.ContentPreviewViewerDelegate) null);
                if (super.onInterceptTouchEvent(motionEvent) || onInterceptTouchEvent) {
                    return true;
                }
                return false;
            }

            public void requestLayout() {
                if (!AdminLogFilterAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.listView = r112;
        r112.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setClipToPadding(false);
        this.listView.setEnabled(true);
        this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                AdminLogFilterAlert.this.updateLayout();
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                AdminLogFilterAlert.this.lambda$new$0$AdminLogFilterAlert(view, i);
            }
        });
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        View view = new View(context);
        view.setBackgroundResource(NUM);
        this.containerView.addView(view, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        BottomSheet.BottomSheetCell bottomSheetCell = new BottomSheet.BottomSheetCell(context, 1);
        this.saveButton = bottomSheetCell;
        bottomSheetCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.saveButton.setTextAndIcon((CharSequence) LocaleController.getString("Save", NUM).toUpperCase(), 0);
        this.saveButton.setTextColor(Theme.getColor("dialogTextBlue2"));
        this.saveButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                AdminLogFilterAlert.this.lambda$new$1$AdminLogFilterAlert(view);
            }
        });
        this.containerView.addView(this.saveButton, LayoutHelper.createFrame(-1, 48, 83));
        this.adapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$AdminLogFilterAlert(View view, int i) {
        if (view instanceof CheckBoxCell) {
            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
            boolean isChecked = checkBoxCell.isChecked();
            checkBoxCell.setChecked(!isChecked, true);
            if (i == 0) {
                if (isChecked) {
                    TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter = new TLRPC$TL_channelAdminLogEventsFilter();
                    this.currentFilter = tLRPC$TL_channelAdminLogEventsFilter;
                    tLRPC$TL_channelAdminLogEventsFilter.invites = false;
                    tLRPC$TL_channelAdminLogEventsFilter.group_call = false;
                    tLRPC$TL_channelAdminLogEventsFilter.delete = false;
                    tLRPC$TL_channelAdminLogEventsFilter.edit = false;
                    tLRPC$TL_channelAdminLogEventsFilter.pinned = false;
                    tLRPC$TL_channelAdminLogEventsFilter.settings = false;
                    tLRPC$TL_channelAdminLogEventsFilter.info = false;
                    tLRPC$TL_channelAdminLogEventsFilter.demote = false;
                    tLRPC$TL_channelAdminLogEventsFilter.promote = false;
                    tLRPC$TL_channelAdminLogEventsFilter.unkick = false;
                    tLRPC$TL_channelAdminLogEventsFilter.kick = false;
                    tLRPC$TL_channelAdminLogEventsFilter.unban = false;
                    tLRPC$TL_channelAdminLogEventsFilter.ban = false;
                    tLRPC$TL_channelAdminLogEventsFilter.invite = false;
                    tLRPC$TL_channelAdminLogEventsFilter.leave = false;
                    tLRPC$TL_channelAdminLogEventsFilter.join = false;
                } else {
                    this.currentFilter = null;
                }
                int childCount = this.listView.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = this.listView.getChildAt(i2);
                    RecyclerView.ViewHolder findContainingViewHolder = this.listView.findContainingViewHolder(childAt);
                    int adapterPosition = findContainingViewHolder.getAdapterPosition();
                    if (findContainingViewHolder.getItemViewType() == 0 && adapterPosition > 0 && adapterPosition < this.allAdminsRow - 1) {
                        ((CheckBoxCell) childAt).setChecked(!isChecked, true);
                    }
                }
            } else if (i == this.allAdminsRow) {
                if (isChecked) {
                    this.selectedAdmins = new SparseArray<>();
                } else {
                    this.selectedAdmins = null;
                }
                int childCount2 = this.listView.getChildCount();
                for (int i3 = 0; i3 < childCount2; i3++) {
                    View childAt2 = this.listView.getChildAt(i3);
                    RecyclerView.ViewHolder findContainingViewHolder2 = this.listView.findContainingViewHolder(childAt2);
                    findContainingViewHolder2.getAdapterPosition();
                    if (findContainingViewHolder2.getItemViewType() == 2) {
                        ((CheckBoxUserCell) childAt2).setChecked(!isChecked, true);
                    }
                }
            } else {
                if (this.currentFilter == null) {
                    TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter2 = new TLRPC$TL_channelAdminLogEventsFilter();
                    this.currentFilter = tLRPC$TL_channelAdminLogEventsFilter2;
                    tLRPC$TL_channelAdminLogEventsFilter2.invites = true;
                    tLRPC$TL_channelAdminLogEventsFilter2.group_call = true;
                    tLRPC$TL_channelAdminLogEventsFilter2.delete = true;
                    tLRPC$TL_channelAdminLogEventsFilter2.edit = true;
                    tLRPC$TL_channelAdminLogEventsFilter2.pinned = true;
                    tLRPC$TL_channelAdminLogEventsFilter2.settings = true;
                    tLRPC$TL_channelAdminLogEventsFilter2.info = true;
                    tLRPC$TL_channelAdminLogEventsFilter2.demote = true;
                    tLRPC$TL_channelAdminLogEventsFilter2.promote = true;
                    tLRPC$TL_channelAdminLogEventsFilter2.unkick = true;
                    tLRPC$TL_channelAdminLogEventsFilter2.kick = true;
                    tLRPC$TL_channelAdminLogEventsFilter2.unban = true;
                    tLRPC$TL_channelAdminLogEventsFilter2.ban = true;
                    tLRPC$TL_channelAdminLogEventsFilter2.invite = true;
                    tLRPC$TL_channelAdminLogEventsFilter2.leave = true;
                    tLRPC$TL_channelAdminLogEventsFilter2.join = true;
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(0);
                    if (findViewHolderForAdapterPosition != null) {
                        ((CheckBoxCell) findViewHolderForAdapterPosition.itemView).setChecked(false, true);
                    }
                }
                if (i == this.restrictionsRow) {
                    TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter3 = this.currentFilter;
                    boolean z = !tLRPC$TL_channelAdminLogEventsFilter3.kick;
                    tLRPC$TL_channelAdminLogEventsFilter3.unban = z;
                    tLRPC$TL_channelAdminLogEventsFilter3.unkick = z;
                    tLRPC$TL_channelAdminLogEventsFilter3.ban = z;
                    tLRPC$TL_channelAdminLogEventsFilter3.kick = z;
                } else if (i == this.adminsRow) {
                    TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter4 = this.currentFilter;
                    boolean z2 = !tLRPC$TL_channelAdminLogEventsFilter4.demote;
                    tLRPC$TL_channelAdminLogEventsFilter4.demote = z2;
                    tLRPC$TL_channelAdminLogEventsFilter4.promote = z2;
                } else if (i == this.membersRow) {
                    TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter5 = this.currentFilter;
                    boolean z3 = !tLRPC$TL_channelAdminLogEventsFilter5.join;
                    tLRPC$TL_channelAdminLogEventsFilter5.join = z3;
                    tLRPC$TL_channelAdminLogEventsFilter5.invite = z3;
                } else if (i == this.infoRow) {
                    TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter6 = this.currentFilter;
                    boolean z4 = !tLRPC$TL_channelAdminLogEventsFilter6.info;
                    tLRPC$TL_channelAdminLogEventsFilter6.settings = z4;
                    tLRPC$TL_channelAdminLogEventsFilter6.info = z4;
                } else if (i == this.deleteRow) {
                    TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter7 = this.currentFilter;
                    tLRPC$TL_channelAdminLogEventsFilter7.delete = !tLRPC$TL_channelAdminLogEventsFilter7.delete;
                } else if (i == this.editRow) {
                    TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter8 = this.currentFilter;
                    tLRPC$TL_channelAdminLogEventsFilter8.edit = !tLRPC$TL_channelAdminLogEventsFilter8.edit;
                } else if (i == this.pinnedRow) {
                    TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter9 = this.currentFilter;
                    tLRPC$TL_channelAdminLogEventsFilter9.pinned = !tLRPC$TL_channelAdminLogEventsFilter9.pinned;
                } else if (i == this.leavingRow) {
                    TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter10 = this.currentFilter;
                    tLRPC$TL_channelAdminLogEventsFilter10.leave = !tLRPC$TL_channelAdminLogEventsFilter10.leave;
                } else if (i == this.callsRow) {
                    TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter11 = this.currentFilter;
                    tLRPC$TL_channelAdminLogEventsFilter11.group_call = !tLRPC$TL_channelAdminLogEventsFilter11.group_call;
                } else if (i == this.invitesRow) {
                    TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter12 = this.currentFilter;
                    tLRPC$TL_channelAdminLogEventsFilter12.invites = !tLRPC$TL_channelAdminLogEventsFilter12.invites;
                }
            }
            TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter13 = this.currentFilter;
            if (tLRPC$TL_channelAdminLogEventsFilter13 == null || tLRPC$TL_channelAdminLogEventsFilter13.join || tLRPC$TL_channelAdminLogEventsFilter13.leave || tLRPC$TL_channelAdminLogEventsFilter13.invite || tLRPC$TL_channelAdminLogEventsFilter13.ban || tLRPC$TL_channelAdminLogEventsFilter13.invites || tLRPC$TL_channelAdminLogEventsFilter13.unban || tLRPC$TL_channelAdminLogEventsFilter13.kick || tLRPC$TL_channelAdminLogEventsFilter13.unkick || tLRPC$TL_channelAdminLogEventsFilter13.promote || tLRPC$TL_channelAdminLogEventsFilter13.demote || tLRPC$TL_channelAdminLogEventsFilter13.info || tLRPC$TL_channelAdminLogEventsFilter13.settings || tLRPC$TL_channelAdminLogEventsFilter13.pinned || tLRPC$TL_channelAdminLogEventsFilter13.edit || tLRPC$TL_channelAdminLogEventsFilter13.delete || tLRPC$TL_channelAdminLogEventsFilter13.group_call) {
                this.saveButton.setEnabled(true);
                this.saveButton.setAlpha(1.0f);
                return;
            }
            this.saveButton.setEnabled(false);
            this.saveButton.setAlpha(0.5f);
        } else if (view instanceof CheckBoxUserCell) {
            CheckBoxUserCell checkBoxUserCell = (CheckBoxUserCell) view;
            if (this.selectedAdmins == null) {
                this.selectedAdmins = new SparseArray<>();
                RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = this.listView.findViewHolderForAdapterPosition(this.allAdminsRow);
                if (findViewHolderForAdapterPosition2 != null) {
                    ((CheckBoxCell) findViewHolderForAdapterPosition2.itemView).setChecked(false, true);
                }
                for (int i4 = 0; i4 < this.currentAdmins.size(); i4++) {
                    TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentAdmins.get(i4).user_id));
                    this.selectedAdmins.put(user.id, user);
                }
            }
            boolean isChecked2 = checkBoxUserCell.isChecked();
            TLRPC$User currentUser = checkBoxUserCell.getCurrentUser();
            if (isChecked2) {
                this.selectedAdmins.remove(currentUser.id);
            } else {
                this.selectedAdmins.put(currentUser.id, currentUser);
            }
            checkBoxUserCell.setChecked(!isChecked2, true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$AdminLogFilterAlert(View view) {
        this.delegate.didSelectRights(this.currentFilter, this.selectedAdmins);
        dismiss();
    }

    public void setCurrentAdmins(ArrayList<TLRPC$ChannelParticipant> arrayList) {
        this.currentAdmins = arrayList;
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void setAdminLogFilterAlertDelegate(AdminLogFilterAlertDelegate adminLogFilterAlertDelegate) {
        this.delegate = adminLogFilterAlertDelegate;
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        int i = 0;
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop() - AndroidUtilities.dp(8.0f);
        if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
            i = top;
        }
        if (this.scrollOffsetY != i) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = i;
            recyclerListView2.setTopGlowOffset(i);
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

        public int getItemViewType(int i) {
            if (i < AdminLogFilterAlert.this.allAdminsRow - 1 || i == AdminLogFilterAlert.this.allAdminsRow) {
                return 0;
            }
            if (i == AdminLogFilterAlert.this.allAdminsRow - 1) {
                return 1;
            }
            return 2;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            FrameLayout frameLayout2;
            if (i == 0) {
                frameLayout2 = new CheckBoxCell(this.context, 1, 21);
            } else if (i == 1) {
                ShadowSectionCell shadowSectionCell = new ShadowSectionCell(this.context, 18);
                frameLayout2 = new FrameLayout(this.context);
                frameLayout2.addView(shadowSectionCell, LayoutHelper.createFrame(-1, -1.0f));
                frameLayout2.setBackgroundColor(Theme.getColor("dialogBackgroundGray"));
            } else if (i != 2) {
                frameLayout = null;
                return new RecyclerListView.Holder(frameLayout);
            } else {
                frameLayout2 = new CheckBoxUserCell(this.context, true);
            }
            frameLayout = frameLayout2;
            return new RecyclerListView.Holder(frameLayout);
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) viewHolder.itemView;
                if (adapterPosition == 0) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.restrictionsRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && (!AdminLogFilterAlert.this.currentFilter.kick || !AdminLogFilterAlert.this.currentFilter.ban || !AdminLogFilterAlert.this.currentFilter.unkick || !AdminLogFilterAlert.this.currentFilter.unban)) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.adminsRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && (!AdminLogFilterAlert.this.currentFilter.promote || !AdminLogFilterAlert.this.currentFilter.demote)) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.membersRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && (!AdminLogFilterAlert.this.currentFilter.invite || !AdminLogFilterAlert.this.currentFilter.join)) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.infoRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.info) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.deleteRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.delete) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.editRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.edit) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.pinnedRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.pinned) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.leavingRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.leave) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.callsRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.group_call) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.invitesRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.invites) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.allAdminsRow) {
                    if (AdminLogFilterAlert.this.selectedAdmins != null) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                }
            } else if (itemViewType == 2) {
                CheckBoxUserCell checkBoxUserCell = (CheckBoxUserCell) viewHolder.itemView;
                int i = ((TLRPC$ChannelParticipant) AdminLogFilterAlert.this.currentAdmins.get((adapterPosition - AdminLogFilterAlert.this.allAdminsRow) - 1)).user_id;
                if (AdminLogFilterAlert.this.selectedAdmins != null && AdminLogFilterAlert.this.selectedAdmins.indexOfKey(i) < 0) {
                    z = false;
                }
                checkBoxUserCell.setChecked(z, false);
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            boolean z2 = true;
            if (itemViewType == 0) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) viewHolder.itemView;
                if (i == 0) {
                    String string = LocaleController.getString("EventLogFilterAll", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null) {
                        z = true;
                    }
                    checkBoxCell.setText(string, "", z, true);
                } else if (i == AdminLogFilterAlert.this.restrictionsRow) {
                    String string2 = LocaleController.getString("EventLogFilterNewRestrictions", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.kick && AdminLogFilterAlert.this.currentFilter.ban && AdminLogFilterAlert.this.currentFilter.unkick && AdminLogFilterAlert.this.currentFilter.unban)) {
                        z = true;
                    }
                    checkBoxCell.setText(string2, "", z, true);
                } else if (i == AdminLogFilterAlert.this.adminsRow) {
                    String string3 = LocaleController.getString("EventLogFilterNewAdmins", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.promote && AdminLogFilterAlert.this.currentFilter.demote)) {
                        z = true;
                    }
                    checkBoxCell.setText(string3, "", z, true);
                } else if (i == AdminLogFilterAlert.this.membersRow) {
                    String string4 = LocaleController.getString("EventLogFilterNewMembers", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.invite && AdminLogFilterAlert.this.currentFilter.join)) {
                        z = true;
                    }
                    checkBoxCell.setText(string4, "", z, true);
                } else if (i == AdminLogFilterAlert.this.infoRow) {
                    if (AdminLogFilterAlert.this.isMegagroup) {
                        String string5 = LocaleController.getString("EventLogFilterGroupInfo", NUM);
                        if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.info) {
                            z = true;
                        }
                        checkBoxCell.setText(string5, "", z, true);
                        return;
                    }
                    String string6 = LocaleController.getString("EventLogFilterChannelInfo", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.info) {
                        z = true;
                    }
                    checkBoxCell.setText(string6, "", z, true);
                } else if (i == AdminLogFilterAlert.this.deleteRow) {
                    String string7 = LocaleController.getString("EventLogFilterDeletedMessages", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.delete) {
                        z = true;
                    }
                    checkBoxCell.setText(string7, "", z, true);
                } else if (i == AdminLogFilterAlert.this.editRow) {
                    String string8 = LocaleController.getString("EventLogFilterEditedMessages", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.edit) {
                        z = true;
                    }
                    checkBoxCell.setText(string8, "", z, true);
                } else if (i == AdminLogFilterAlert.this.pinnedRow) {
                    String string9 = LocaleController.getString("EventLogFilterPinnedMessages", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.pinned) {
                        z = true;
                    }
                    checkBoxCell.setText(string9, "", z, true);
                } else if (i == AdminLogFilterAlert.this.leavingRow) {
                    String string10 = LocaleController.getString("EventLogFilterLeavingMembers", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.leave) {
                        z = true;
                    }
                    checkBoxCell.setText(string10, "", z, true);
                } else if (i == AdminLogFilterAlert.this.callsRow) {
                    String string11 = LocaleController.getString("EventLogFilterCalls", NUM);
                    if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.group_call) {
                        z2 = false;
                    }
                    checkBoxCell.setText(string11, "", z2, false);
                } else if (i == AdminLogFilterAlert.this.invitesRow) {
                    String string12 = LocaleController.getString("EventLogFilterInvites", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.invites) {
                        z = true;
                    }
                    checkBoxCell.setText(string12, "", z, true);
                } else if (i == AdminLogFilterAlert.this.allAdminsRow) {
                    String string13 = LocaleController.getString("EventLogAllAdmins", NUM);
                    if (AdminLogFilterAlert.this.selectedAdmins == null) {
                        z = true;
                    }
                    checkBoxCell.setText(string13, "", z, true);
                }
            } else if (itemViewType == 2) {
                CheckBoxUserCell checkBoxUserCell = (CheckBoxUserCell) viewHolder.itemView;
                int i2 = ((TLRPC$ChannelParticipant) AdminLogFilterAlert.this.currentAdmins.get((i - AdminLogFilterAlert.this.allAdminsRow) - 1)).user_id;
                TLRPC$User user = MessagesController.getInstance(AdminLogFilterAlert.this.currentAccount).getUser(Integer.valueOf(i2));
                boolean z3 = AdminLogFilterAlert.this.selectedAdmins == null || AdminLogFilterAlert.this.selectedAdmins.indexOfKey(i2) >= 0;
                if (i != getItemCount() - 1) {
                    z = true;
                }
                checkBoxUserCell.setUser(user, z3, z);
            }
        }
    }
}
