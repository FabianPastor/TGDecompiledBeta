package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventsFilter;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.CheckBoxUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.ContentPreviewViewer;

public class AdminLogFilterAlert extends BottomSheet {
    private ListAdapter adapter;
    private int adminsRow;
    private int allAdminsRow;
    private ArrayList<ChannelParticipant> currentAdmins;
    private TL_channelAdminLogEventsFilter currentFilter;
    private AdminLogFilterAlertDelegate delegate;
    private int deleteRow;
    private int editRow;
    private boolean ignoreLayout;
    private int infoRow;
    private boolean isMegagroup;
    private int leavingRow;
    private RecyclerListView listView;
    private int membersRow;
    private FrameLayout pickerBottomLayout;
    private int pinnedRow;
    private int reqId;
    private int restrictionsRow;
    private BottomSheetCell saveButton;
    private int scrollOffsetY;
    private SparseArray<User> selectedAdmins;
    private Drawable shadowDrawable;
    private Pattern urlPattern;

    public interface AdminLogFilterAlertDelegate {
        void didSelectRights(TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, SparseArray<User> sparseArray);
    }

    private class ListAdapter extends SelectionAdapter {
        private Context context;

        public ListAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            return (AdminLogFilterAlert.this.isMegagroup ? 9 : 7) + (AdminLogFilterAlert.this.currentAdmins != null ? AdminLogFilterAlert.this.currentAdmins.size() + 2 : 0);
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

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View checkBoxCell;
            if (i == 0) {
                checkBoxCell = new CheckBoxCell(this.context, 1, 21);
                checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            } else if (i == 1) {
                ShadowSectionCell shadowSectionCell = new ShadowSectionCell(this.context, 18);
                checkBoxCell = new FrameLayout(this.context);
                checkBoxCell.addView(shadowSectionCell, LayoutHelper.createFrame(-1, -1.0f));
                checkBoxCell.setBackgroundColor(Theme.getColor("dialogBackgroundGray"));
            } else if (i != 2) {
                checkBoxCell = null;
            } else {
                checkBoxCell = new CheckBoxUserCell(this.context, true);
            }
            return new Holder(checkBoxCell);
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
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
                    if (!(AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.kick && AdminLogFilterAlert.this.currentFilter.ban && AdminLogFilterAlert.this.currentFilter.unkick && AdminLogFilterAlert.this.currentFilter.unban))) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.adminsRow) {
                    if (!(AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.promote && AdminLogFilterAlert.this.currentFilter.demote))) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.membersRow) {
                    if (!(AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.invite && AdminLogFilterAlert.this.currentFilter.join))) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.infoRow) {
                    if (!(AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.info)) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.deleteRow) {
                    if (!(AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.delete)) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.editRow) {
                    if (!(AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.edit)) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.pinnedRow) {
                    if (!(AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.pinned)) {
                        z = false;
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.leavingRow) {
                    if (!(AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.leave)) {
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
                adapterPosition = ((ChannelParticipant) AdminLogFilterAlert.this.currentAdmins.get((adapterPosition - AdminLogFilterAlert.this.allAdminsRow) - 1)).user_id;
                if (AdminLogFilterAlert.this.selectedAdmins != null && AdminLogFilterAlert.this.selectedAdmins.indexOfKey(adapterPosition) < 0) {
                    z = false;
                }
                checkBoxUserCell.setChecked(z, false);
            }
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            boolean z2 = true;
            if (itemViewType == 0) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) viewHolder.itemView;
                String str = "";
                String string;
                if (i == 0) {
                    string = LocaleController.getString("EventLogFilterAll", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null) {
                        z = true;
                    }
                    checkBoxCell.setText(string, str, z, true);
                } else if (i == AdminLogFilterAlert.this.restrictionsRow) {
                    string = LocaleController.getString("EventLogFilterNewRestrictions", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.kick && AdminLogFilterAlert.this.currentFilter.ban && AdminLogFilterAlert.this.currentFilter.unkick && AdminLogFilterAlert.this.currentFilter.unban)) {
                        z = true;
                    }
                    checkBoxCell.setText(string, str, z, true);
                } else if (i == AdminLogFilterAlert.this.adminsRow) {
                    string = LocaleController.getString("EventLogFilterNewAdmins", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.promote && AdminLogFilterAlert.this.currentFilter.demote)) {
                        z = true;
                    }
                    checkBoxCell.setText(string, str, z, true);
                } else if (i == AdminLogFilterAlert.this.membersRow) {
                    string = LocaleController.getString("EventLogFilterNewMembers", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.invite && AdminLogFilterAlert.this.currentFilter.join)) {
                        z = true;
                    }
                    checkBoxCell.setText(string, str, z, true);
                } else if (i == AdminLogFilterAlert.this.infoRow) {
                    if (AdminLogFilterAlert.this.isMegagroup) {
                        string = LocaleController.getString("EventLogFilterGroupInfo", NUM);
                        if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.info) {
                            z = true;
                        }
                        checkBoxCell.setText(string, str, z, true);
                        return;
                    }
                    string = LocaleController.getString("EventLogFilterChannelInfo", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.info) {
                        z = true;
                    }
                    checkBoxCell.setText(string, str, z, true);
                } else if (i == AdminLogFilterAlert.this.deleteRow) {
                    string = LocaleController.getString("EventLogFilterDeletedMessages", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.delete) {
                        z = true;
                    }
                    checkBoxCell.setText(string, str, z, true);
                } else if (i == AdminLogFilterAlert.this.editRow) {
                    string = LocaleController.getString("EventLogFilterEditedMessages", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.edit) {
                        z = true;
                    }
                    checkBoxCell.setText(string, str, z, true);
                } else if (i == AdminLogFilterAlert.this.pinnedRow) {
                    string = LocaleController.getString("EventLogFilterPinnedMessages", NUM);
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.pinned) {
                        z = true;
                    }
                    checkBoxCell.setText(string, str, z, true);
                } else if (i == AdminLogFilterAlert.this.leavingRow) {
                    string = LocaleController.getString("EventLogFilterLeavingMembers", NUM);
                    if (!(AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.leave)) {
                        z2 = false;
                    }
                    checkBoxCell.setText(string, str, z2, false);
                } else if (i == AdminLogFilterAlert.this.allAdminsRow) {
                    string = LocaleController.getString("EventLogAllAdmins", NUM);
                    if (AdminLogFilterAlert.this.selectedAdmins == null) {
                        z = true;
                    }
                    checkBoxCell.setText(string, str, z, true);
                }
            } else if (itemViewType == 2) {
                CheckBoxUserCell checkBoxUserCell = (CheckBoxUserCell) viewHolder.itemView;
                itemViewType = ((ChannelParticipant) AdminLogFilterAlert.this.currentAdmins.get((i - AdminLogFilterAlert.this.allAdminsRow) - 1)).user_id;
                User user = MessagesController.getInstance(AdminLogFilterAlert.this.currentAccount).getUser(Integer.valueOf(itemViewType));
                boolean z3 = AdminLogFilterAlert.this.selectedAdmins == null || AdminLogFilterAlert.this.selectedAdmins.indexOfKey(itemViewType) >= 0;
                if (i != getItemCount() - 1) {
                    z = true;
                }
                checkBoxUserCell.setUser(user, z3, z);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public AdminLogFilterAlert(Context context, TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, SparseArray<User> sparseArray, boolean z) {
        int i;
        super(context, false, 0);
        if (tL_channelAdminLogEventsFilter != null) {
            this.currentFilter = new TL_channelAdminLogEventsFilter();
            TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter2 = this.currentFilter;
            tL_channelAdminLogEventsFilter2.join = tL_channelAdminLogEventsFilter.join;
            tL_channelAdminLogEventsFilter2.leave = tL_channelAdminLogEventsFilter.leave;
            tL_channelAdminLogEventsFilter2.invite = tL_channelAdminLogEventsFilter.invite;
            tL_channelAdminLogEventsFilter2.ban = tL_channelAdminLogEventsFilter.ban;
            tL_channelAdminLogEventsFilter2.unban = tL_channelAdminLogEventsFilter.unban;
            tL_channelAdminLogEventsFilter2.kick = tL_channelAdminLogEventsFilter.kick;
            tL_channelAdminLogEventsFilter2.unkick = tL_channelAdminLogEventsFilter.unkick;
            tL_channelAdminLogEventsFilter2.promote = tL_channelAdminLogEventsFilter.promote;
            tL_channelAdminLogEventsFilter2.demote = tL_channelAdminLogEventsFilter.demote;
            tL_channelAdminLogEventsFilter2.info = tL_channelAdminLogEventsFilter.info;
            tL_channelAdminLogEventsFilter2.settings = tL_channelAdminLogEventsFilter.settings;
            tL_channelAdminLogEventsFilter2.pinned = tL_channelAdminLogEventsFilter.pinned;
            tL_channelAdminLogEventsFilter2.edit = tL_channelAdminLogEventsFilter.edit;
            tL_channelAdminLogEventsFilter2.delete = tL_channelAdminLogEventsFilter.delete;
        }
        if (sparseArray != null) {
            this.selectedAdmins = sparseArray.clone();
        }
        this.isMegagroup = z;
        if (this.isMegagroup) {
            this.restrictionsRow = 1;
            i = 2;
        } else {
            this.restrictionsRow = -1;
            i = 1;
        }
        int i2 = i + 1;
        this.adminsRow = i;
        i = i2 + 1;
        this.membersRow = i2;
        i2 = i + 1;
        this.infoRow = i;
        i = i2 + 1;
        this.deleteRow = i2;
        i2 = i + 1;
        this.editRow = i;
        if (this.isMegagroup) {
            i = i2 + 1;
            this.pinnedRow = i2;
        } else {
            this.pinnedRow = -1;
            i = i2;
        }
        this.leavingRow = i;
        this.allAdminsRow = i + 2;
        this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), Mode.MULTIPLY));
        this.containerView = new FrameLayout(context) {
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

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                i2 = MeasureSpec.getSize(i2);
                if (VERSION.SDK_INT >= 21) {
                    i2 -= AndroidUtilities.statusBarHeight;
                }
                getMeasuredWidth();
                int dp = (AndroidUtilities.dp(48.0f) + ((AdminLogFilterAlert.this.isMegagroup ? 9 : 7) * AndroidUtilities.dp(48.0f))) + AdminLogFilterAlert.this.backgroundPaddingTop;
                if (AdminLogFilterAlert.this.currentAdmins != null) {
                    dp += ((AdminLogFilterAlert.this.currentAdmins.size() + 1) * AndroidUtilities.dp(48.0f)) + AndroidUtilities.dp(20.0f);
                }
                int i3 = i2 / 5;
                int i4 = ((float) dp) < ((float) i3) * 3.2f ? 0 : i3 * 2;
                if (i4 != 0 && dp < i2) {
                    i4 -= i2 - dp;
                }
                if (i4 == 0) {
                    i4 = AdminLogFilterAlert.this.backgroundPaddingTop;
                }
                if (AdminLogFilterAlert.this.listView.getPaddingTop() != i4) {
                    AdminLogFilterAlert.this.ignoreLayout = true;
                    AdminLogFilterAlert.this.listView.setPadding(0, i4, 0, 0);
                    AdminLogFilterAlert.this.ignoreLayout = false;
                }
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(Math.min(dp, i2), NUM));
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                AdminLogFilterAlert.this.updateLayout();
            }

            public void requestLayout() {
                if (!AdminLogFilterAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                AdminLogFilterAlert.this.shadowDrawable.setBounds(0, AdminLogFilterAlert.this.scrollOffsetY - AdminLogFilterAlert.this.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                AdminLogFilterAlert.this.shadowDrawable.draw(canvas);
            }
        };
        this.containerView.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        int i3 = this.backgroundPaddingLeft;
        viewGroup.setPadding(i3, 0, i3, 0);
        this.listView = new RecyclerListView(context) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                boolean onInterceptTouchEvent = ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, AdminLogFilterAlert.this.listView, 0, null);
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
        this.listView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setClipToPadding(false);
        this.listView.setEnabled(true);
        this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                AdminLogFilterAlert.this.updateLayout();
            }
        });
        this.listView.setOnItemClickListener(new -$$Lambda$AdminLogFilterAlert$PQg5JdNAPilzJKQuE1cFzZ18AW4(this));
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        View view = new View(context);
        view.setBackgroundResource(NUM);
        this.containerView.addView(view, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        this.saveButton = new BottomSheetCell(context, 1);
        this.saveButton.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.saveButton.setTextAndIcon(LocaleController.getString("Save", NUM).toUpperCase(), 0);
        this.saveButton.setTextColor(Theme.getColor("dialogTextBlue2"));
        this.saveButton.setOnClickListener(new -$$Lambda$AdminLogFilterAlert$P34SvMRGiZ3R-8FDoPqX6OJH8LA(this));
        this.containerView.addView(this.saveButton, LayoutHelper.createFrame(-1, 48, 83));
        this.adapter.notifyDataSetChanged();
    }

    public /* synthetic */ void lambda$new$0$AdminLogFilterAlert(View view, int i) {
        int i2 = 0;
        boolean z;
        if (view instanceof CheckBoxCell) {
            TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter;
            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
            boolean isChecked = checkBoxCell.isChecked();
            checkBoxCell.setChecked(isChecked ^ 1, true);
            int childCount;
            View childAt;
            ViewHolder findContainingViewHolder;
            if (i == 0) {
                if (isChecked) {
                    this.currentFilter = new TL_channelAdminLogEventsFilter();
                    tL_channelAdminLogEventsFilter = this.currentFilter;
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
                childCount = this.listView.getChildCount();
                for (i = 0; i < childCount; i++) {
                    childAt = this.listView.getChildAt(i);
                    findContainingViewHolder = this.listView.findContainingViewHolder(childAt);
                    int adapterPosition = findContainingViewHolder.getAdapterPosition();
                    if (findContainingViewHolder.getItemViewType() == 0 && adapterPosition > 0 && adapterPosition < this.allAdminsRow - 1) {
                        ((CheckBoxCell) childAt).setChecked(isChecked ^ 1, true);
                    }
                }
            } else if (i == this.allAdminsRow) {
                if (isChecked) {
                    this.selectedAdmins = new SparseArray();
                } else {
                    this.selectedAdmins = null;
                }
                childCount = this.listView.getChildCount();
                for (i = 0; i < childCount; i++) {
                    childAt = this.listView.getChildAt(i);
                    findContainingViewHolder = this.listView.findContainingViewHolder(childAt);
                    findContainingViewHolder.getAdapterPosition();
                    if (findContainingViewHolder.getItemViewType() == 2) {
                        ((CheckBoxUserCell) childAt).setChecked(isChecked ^ 1, true);
                    }
                }
            } else {
                if (this.currentFilter == null) {
                    this.currentFilter = new TL_channelAdminLogEventsFilter();
                    tL_channelAdminLogEventsFilter = this.currentFilter;
                    tL_channelAdminLogEventsFilter.delete = true;
                    tL_channelAdminLogEventsFilter.edit = true;
                    tL_channelAdminLogEventsFilter.pinned = true;
                    tL_channelAdminLogEventsFilter.settings = true;
                    tL_channelAdminLogEventsFilter.info = true;
                    tL_channelAdminLogEventsFilter.demote = true;
                    tL_channelAdminLogEventsFilter.promote = true;
                    tL_channelAdminLogEventsFilter.unkick = true;
                    tL_channelAdminLogEventsFilter.kick = true;
                    tL_channelAdminLogEventsFilter.unban = true;
                    tL_channelAdminLogEventsFilter.ban = true;
                    tL_channelAdminLogEventsFilter.invite = true;
                    tL_channelAdminLogEventsFilter.leave = true;
                    tL_channelAdminLogEventsFilter.join = true;
                    ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(0);
                    if (findViewHolderForAdapterPosition != null) {
                        ((CheckBoxCell) findViewHolderForAdapterPosition.itemView).setChecked(false, true);
                    }
                }
                if (i == this.restrictionsRow) {
                    tL_channelAdminLogEventsFilter = this.currentFilter;
                    i = tL_channelAdminLogEventsFilter.kick ^ 1;
                    tL_channelAdminLogEventsFilter.unban = i;
                    tL_channelAdminLogEventsFilter.unkick = i;
                    tL_channelAdminLogEventsFilter.ban = i;
                    tL_channelAdminLogEventsFilter.kick = i;
                } else if (i == this.adminsRow) {
                    tL_channelAdminLogEventsFilter = this.currentFilter;
                    i = tL_channelAdminLogEventsFilter.demote ^ 1;
                    tL_channelAdminLogEventsFilter.demote = i;
                    tL_channelAdminLogEventsFilter.promote = i;
                } else if (i == this.membersRow) {
                    tL_channelAdminLogEventsFilter = this.currentFilter;
                    i = tL_channelAdminLogEventsFilter.join ^ 1;
                    tL_channelAdminLogEventsFilter.join = i;
                    tL_channelAdminLogEventsFilter.invite = i;
                } else if (i == this.infoRow) {
                    tL_channelAdminLogEventsFilter = this.currentFilter;
                    i = tL_channelAdminLogEventsFilter.info ^ 1;
                    tL_channelAdminLogEventsFilter.settings = i;
                    tL_channelAdminLogEventsFilter.info = i;
                } else if (i == this.deleteRow) {
                    tL_channelAdminLogEventsFilter = this.currentFilter;
                    tL_channelAdminLogEventsFilter.delete ^= 1;
                } else if (i == this.editRow) {
                    tL_channelAdminLogEventsFilter = this.currentFilter;
                    tL_channelAdminLogEventsFilter.edit ^= 1;
                } else if (i == this.pinnedRow) {
                    tL_channelAdminLogEventsFilter = this.currentFilter;
                    tL_channelAdminLogEventsFilter.pinned ^= 1;
                } else if (i == this.leavingRow) {
                    tL_channelAdminLogEventsFilter = this.currentFilter;
                    tL_channelAdminLogEventsFilter.leave ^= 1;
                }
            }
            tL_channelAdminLogEventsFilter = this.currentFilter;
            if (!(tL_channelAdminLogEventsFilter == null || tL_channelAdminLogEventsFilter.join)) {
                z = tL_channelAdminLogEventsFilter.leave;
                if (!(z || z || tL_channelAdminLogEventsFilter.invite || tL_channelAdminLogEventsFilter.ban || tL_channelAdminLogEventsFilter.unban || tL_channelAdminLogEventsFilter.kick || tL_channelAdminLogEventsFilter.unkick || tL_channelAdminLogEventsFilter.promote || tL_channelAdminLogEventsFilter.demote || tL_channelAdminLogEventsFilter.info || tL_channelAdminLogEventsFilter.settings || tL_channelAdminLogEventsFilter.pinned || tL_channelAdminLogEventsFilter.edit || tL_channelAdminLogEventsFilter.delete)) {
                    this.saveButton.setEnabled(false);
                    this.saveButton.setAlpha(0.5f);
                    return;
                }
            }
            this.saveButton.setEnabled(true);
            this.saveButton.setAlpha(1.0f);
        } else if (view instanceof CheckBoxUserCell) {
            CheckBoxUserCell checkBoxUserCell = (CheckBoxUserCell) view;
            if (this.selectedAdmins == null) {
                this.selectedAdmins = new SparseArray();
                ViewHolder findViewHolderForAdapterPosition2 = this.listView.findViewHolderForAdapterPosition(this.allAdminsRow);
                if (findViewHolderForAdapterPosition2 != null) {
                    ((CheckBoxCell) findViewHolderForAdapterPosition2.itemView).setChecked(false, true);
                }
                while (i2 < this.currentAdmins.size()) {
                    User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((ChannelParticipant) this.currentAdmins.get(i2)).user_id));
                    this.selectedAdmins.put(user.id, user);
                    i2++;
                }
            }
            z = checkBoxUserCell.isChecked();
            User currentUser = checkBoxUserCell.getCurrentUser();
            if (z) {
                this.selectedAdmins.remove(currentUser.id);
            } else {
                this.selectedAdmins.put(currentUser.id, currentUser);
            }
            checkBoxUserCell.setChecked(z ^ 1, true);
        }
    }

    public /* synthetic */ void lambda$new$1$AdminLogFilterAlert(View view) {
        this.delegate.didSelectRights(this.currentFilter, this.selectedAdmins);
        dismiss();
    }

    public void setCurrentAdmins(ArrayList<ChannelParticipant> arrayList) {
        this.currentAdmins = arrayList;
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void setAdminLogFilterAlertDelegate(AdminLogFilterAlertDelegate adminLogFilterAlertDelegate) {
        this.delegate = adminLogFilterAlertDelegate;
    }

    @SuppressLint({"NewApi"})
    private void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        View childAt = this.listView.getChildAt(0);
        Holder holder = (Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop() - AndroidUtilities.dp(8.0f);
        if (top <= 0 || holder == null || holder.getAdapterPosition() != 0) {
            top = 0;
        }
        if (this.scrollOffsetY != top) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = top;
            recyclerListView2.setTopGlowOffset(top);
            this.containerView.invalidate();
        }
    }
}
