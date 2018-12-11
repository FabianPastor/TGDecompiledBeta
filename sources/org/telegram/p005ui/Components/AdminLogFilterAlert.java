package org.telegram.p005ui.Components;

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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.BottomSheet;
import org.telegram.p005ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Cells.CheckBoxCell;
import org.telegram.p005ui.Cells.CheckBoxUserCell;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.p005ui.StickerPreviewViewer;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventsFilter;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.Components.AdminLogFilterAlert */
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

    /* renamed from: org.telegram.ui.Components.AdminLogFilterAlert$AdminLogFilterAlertDelegate */
    public interface AdminLogFilterAlertDelegate {
        void didSelectRights(TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, SparseArray<User> sparseArray);
    }

    /* renamed from: org.telegram.ui.Components.AdminLogFilterAlert$3 */
    class CLASSNAME extends OnScrollListener {
        CLASSNAME() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            AdminLogFilterAlert.this.updateLayout();
        }
    }

    /* renamed from: org.telegram.ui.Components.AdminLogFilterAlert$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context context;

        public ListAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            return (AdminLogFilterAlert.this.isMegagroup ? 9 : 7) + (AdminLogFilterAlert.this.currentAdmins != null ? AdminLogFilterAlert.this.currentAdmins.size() + 2 : 0);
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

        public boolean isEnabled(ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new CheckBoxCell(this.context, 1, 21);
                    view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    break;
                case 1:
                    ShadowSectionCell shadowSectionCell = new ShadowSectionCell(this.context, 18);
                    view = new FrameLayout(this.context);
                    ((FrameLayout) view).addView(shadowSectionCell, LayoutHelper.createFrame(-1, -1.0f));
                    view.setBackgroundColor(Theme.getColor(Theme.key_dialogBackgroundGray));
                    break;
                case 2:
                    view = new CheckBoxUserCell(this.context, true);
                    break;
            }
            return new Holder(view);
        }

        public void onViewAttachedToWindow(ViewHolder holder) {
            boolean z = true;
            int position = holder.getAdapterPosition();
            switch (holder.getItemViewType()) {
                case 0:
                    CheckBoxCell cell = holder.itemView;
                    if (position == 0) {
                        cell.setChecked(AdminLogFilterAlert.this.currentFilter == null, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.restrictionsRow) {
                        if (!(AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.kick && AdminLogFilterAlert.this.currentFilter.ban && AdminLogFilterAlert.this.currentFilter.unkick && AdminLogFilterAlert.this.currentFilter.unban))) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.adminsRow) {
                        if (!(AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.promote && AdminLogFilterAlert.this.currentFilter.demote))) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.membersRow) {
                        if (!(AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.invite && AdminLogFilterAlert.this.currentFilter.join))) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.infoRow) {
                        if (!(AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.info)) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.deleteRow) {
                        if (!(AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.delete)) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.editRow) {
                        if (!(AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.edit)) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.pinnedRow) {
                        if (!(AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.pinned)) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.leavingRow) {
                        if (!(AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.leave)) {
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
                    CheckBoxUserCell userCell = holder.itemView;
                    int userId = ((ChannelParticipant) AdminLogFilterAlert.this.currentAdmins.get((position - AdminLogFilterAlert.this.allAdminsRow) - 1)).user_id;
                    if (AdminLogFilterAlert.this.selectedAdmins != null && AdminLogFilterAlert.this.selectedAdmins.indexOfKey(userId) < 0) {
                        z = false;
                    }
                    userCell.setChecked(z, false);
                    return;
                default:
                    return;
            }
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = false;
            boolean z2 = true;
            switch (holder.getItemViewType()) {
                case 0:
                    CheckBoxCell cell = holder.itemView;
                    String string;
                    String str;
                    if (position == 0) {
                        cell.setText(LocaleController.getString("EventLogFilterAll", R.string.EventLogFilterAll), TtmlNode.ANONYMOUS_REGION_ID, AdminLogFilterAlert.this.currentFilter == null, true);
                        return;
                    } else if (position == AdminLogFilterAlert.this.restrictionsRow) {
                        string = LocaleController.getString("EventLogFilterNewRestrictions", R.string.EventLogFilterNewRestrictions);
                        str = TtmlNode.ANONYMOUS_REGION_ID;
                        if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.kick && AdminLogFilterAlert.this.currentFilter.ban && AdminLogFilterAlert.this.currentFilter.unkick && AdminLogFilterAlert.this.currentFilter.unban)) {
                            z = true;
                        }
                        cell.setText(string, str, z, true);
                        return;
                    } else if (position == AdminLogFilterAlert.this.adminsRow) {
                        string = LocaleController.getString("EventLogFilterNewAdmins", R.string.EventLogFilterNewAdmins);
                        str = TtmlNode.ANONYMOUS_REGION_ID;
                        if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.promote && AdminLogFilterAlert.this.currentFilter.demote)) {
                            z = true;
                        }
                        cell.setText(string, str, z, true);
                        return;
                    } else if (position == AdminLogFilterAlert.this.membersRow) {
                        string = LocaleController.getString("EventLogFilterNewMembers", R.string.EventLogFilterNewMembers);
                        str = TtmlNode.ANONYMOUS_REGION_ID;
                        if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.invite && AdminLogFilterAlert.this.currentFilter.join)) {
                            z = true;
                        }
                        cell.setText(string, str, z, true);
                        return;
                    } else if (position == AdminLogFilterAlert.this.infoRow) {
                        if (AdminLogFilterAlert.this.isMegagroup) {
                            string = LocaleController.getString("EventLogFilterGroupInfo", R.string.EventLogFilterGroupInfo);
                            str = TtmlNode.ANONYMOUS_REGION_ID;
                            if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.info) {
                                z = true;
                            }
                            cell.setText(string, str, z, true);
                            return;
                        }
                        string = LocaleController.getString("EventLogFilterChannelInfo", R.string.EventLogFilterChannelInfo);
                        str = TtmlNode.ANONYMOUS_REGION_ID;
                        if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.info) {
                            z = true;
                        }
                        cell.setText(string, str, z, true);
                        return;
                    } else if (position == AdminLogFilterAlert.this.deleteRow) {
                        string = LocaleController.getString("EventLogFilterDeletedMessages", R.string.EventLogFilterDeletedMessages);
                        str = TtmlNode.ANONYMOUS_REGION_ID;
                        if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.delete) {
                            z = true;
                        }
                        cell.setText(string, str, z, true);
                        return;
                    } else if (position == AdminLogFilterAlert.this.editRow) {
                        string = LocaleController.getString("EventLogFilterEditedMessages", R.string.EventLogFilterEditedMessages);
                        str = TtmlNode.ANONYMOUS_REGION_ID;
                        if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.edit) {
                            z = true;
                        }
                        cell.setText(string, str, z, true);
                        return;
                    } else if (position == AdminLogFilterAlert.this.pinnedRow) {
                        string = LocaleController.getString("EventLogFilterPinnedMessages", R.string.EventLogFilterPinnedMessages);
                        str = TtmlNode.ANONYMOUS_REGION_ID;
                        if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.pinned) {
                            z = true;
                        }
                        cell.setText(string, str, z, true);
                        return;
                    } else if (position == AdminLogFilterAlert.this.leavingRow) {
                        string = LocaleController.getString("EventLogFilterLeavingMembers", R.string.EventLogFilterLeavingMembers);
                        str = TtmlNode.ANONYMOUS_REGION_ID;
                        if (!(AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.leave)) {
                            z2 = false;
                        }
                        cell.setText(string, str, z2, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.allAdminsRow) {
                        string = LocaleController.getString("EventLogAllAdmins", R.string.EventLogAllAdmins);
                        str = TtmlNode.ANONYMOUS_REGION_ID;
                        if (AdminLogFilterAlert.this.selectedAdmins == null) {
                            z = true;
                        }
                        cell.setText(string, str, z, true);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    CheckBoxUserCell userCell = holder.itemView;
                    int userId = ((ChannelParticipant) AdminLogFilterAlert.this.currentAdmins.get((position - AdminLogFilterAlert.this.allAdminsRow) - 1)).user_id;
                    User user = MessagesController.getInstance(AdminLogFilterAlert.this.currentAccount).getUser(Integer.valueOf(userId));
                    boolean z3 = AdminLogFilterAlert.this.selectedAdmins == null || AdminLogFilterAlert.this.selectedAdmins.indexOfKey(userId) >= 0;
                    if (position == getItemCount() - 1) {
                        z2 = false;
                    }
                    userCell.setUser(user, z3, z2);
                    return;
                default:
                    return;
            }
        }
    }

    public AdminLogFilterAlert(Context context, TL_channelAdminLogEventsFilter filter, SparseArray<User> admins, boolean megagroup) {
        int rowCount;
        super(context, false);
        if (filter != null) {
            this.currentFilter = new TL_channelAdminLogEventsFilter();
            this.currentFilter.join = filter.join;
            this.currentFilter.leave = filter.leave;
            this.currentFilter.invite = filter.invite;
            this.currentFilter.ban = filter.ban;
            this.currentFilter.unban = filter.unban;
            this.currentFilter.kick = filter.kick;
            this.currentFilter.unkick = filter.unkick;
            this.currentFilter.promote = filter.promote;
            this.currentFilter.demote = filter.demote;
            this.currentFilter.info = filter.info;
            this.currentFilter.settings = filter.settings;
            this.currentFilter.pinned = filter.pinned;
            this.currentFilter.edit = filter.edit;
            this.currentFilter.delete = filter.delete;
        }
        if (admins != null) {
            this.selectedAdmins = admins.clone();
        }
        this.isMegagroup = megagroup;
        int rowCount2 = 1;
        if (this.isMegagroup) {
            rowCount = 1 + 1;
            this.restrictionsRow = 1;
            rowCount2 = rowCount;
        } else {
            this.restrictionsRow = -1;
        }
        rowCount = rowCount2 + 1;
        this.adminsRow = rowCount2;
        rowCount2 = rowCount + 1;
        this.membersRow = rowCount;
        rowCount = rowCount2 + 1;
        this.infoRow = rowCount2;
        rowCount2 = rowCount + 1;
        this.deleteRow = rowCount;
        rowCount = rowCount2 + 1;
        this.editRow = rowCount2;
        if (this.isMegagroup) {
            rowCount2 = rowCount + 1;
            this.pinnedRow = rowCount;
        } else {
            this.pinnedRow = -1;
            rowCount2 = rowCount;
        }
        this.leavingRow = rowCount2;
        this.allAdminsRow = rowCount2 + 2;
        this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), Mode.MULTIPLY));
        this.containerView = new FrameLayout(context) {
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() != 0 || AdminLogFilterAlert.this.scrollOffsetY == 0 || ev.getY() >= ((float) AdminLogFilterAlert.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(ev);
                }
                AdminLogFilterAlert.this.lambda$new$4$EmbedBottomSheet();
                return true;
            }

            public boolean onTouchEvent(MotionEvent e) {
                return !AdminLogFilterAlert.this.isDismissed() && super.onTouchEvent(e);
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int height = MeasureSpec.getSize(heightMeasureSpec);
                if (VERSION.SDK_INT >= 21) {
                    height -= AndroidUtilities.statusBarHeight;
                }
                int measuredWidth = getMeasuredWidth();
                int contentSize = (((AdminLogFilterAlert.this.isMegagroup ? 9 : 7) * AndroidUtilities.m9dp(48.0f)) + AndroidUtilities.m9dp(48.0f)) + AdminLogFilterAlert.backgroundPaddingTop;
                if (AdminLogFilterAlert.this.currentAdmins != null) {
                    contentSize += ((AdminLogFilterAlert.this.currentAdmins.size() + 1) * AndroidUtilities.m9dp(48.0f)) + AndroidUtilities.m9dp(20.0f);
                }
                int padding = ((float) contentSize) < ((float) (height / 5)) * 3.2f ? 0 : (height / 5) * 2;
                if (padding != 0 && contentSize < height) {
                    padding -= height - contentSize;
                }
                if (padding == 0) {
                    padding = AdminLogFilterAlert.backgroundPaddingTop;
                }
                if (AdminLogFilterAlert.this.listView.getPaddingTop() != padding) {
                    AdminLogFilterAlert.this.ignoreLayout = true;
                    AdminLogFilterAlert.this.listView.setPadding(0, padding, 0, 0);
                    AdminLogFilterAlert.this.ignoreLayout = false;
                }
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Math.min(contentSize, height), NUM));
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                AdminLogFilterAlert.this.updateLayout();
            }

            public void requestLayout() {
                if (!AdminLogFilterAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            protected void onDraw(Canvas canvas) {
                AdminLogFilterAlert.this.shadowDrawable.setBounds(0, AdminLogFilterAlert.this.scrollOffsetY - AdminLogFilterAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                AdminLogFilterAlert.this.shadowDrawable.draw(canvas);
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
        this.listView = new RecyclerListView(context) {
            public boolean onInterceptTouchEvent(MotionEvent event) {
                boolean result = StickerPreviewViewer.getInstance().onInterceptTouchEvent(event, AdminLogFilterAlert.this.listView, 0, null);
                if (super.onInterceptTouchEvent(event) || result) {
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
        Adapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setClipToPadding(false);
        this.listView.setEnabled(true);
        this.listView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        this.listView.setOnScrollListener(new CLASSNAME());
        this.listView.setOnItemClickListener(new AdminLogFilterAlert$$Lambda$0(this));
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        View shadow = new View(context);
        shadow.setBackgroundResource(R.drawable.header_shadow_reverse);
        this.containerView.addView(shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        this.saveButton = new BottomSheetCell(context, 1);
        this.saveButton.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.saveButton.setTextAndIcon(LocaleController.getString("Save", R.string.Save).toUpperCase(), 0);
        this.saveButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        this.saveButton.setOnClickListener(new AdminLogFilterAlert$$Lambda$1(this));
        this.containerView.addView(this.saveButton, LayoutHelper.createFrame(-1, 48, 83));
        this.adapter.notifyDataSetChanged();
    }

    final /* synthetic */ void lambda$new$0$AdminLogFilterAlert(View view, int position) {
        boolean isChecked;
        int a;
        ViewHolder holder;
        if (view instanceof CheckBoxCell) {
            CheckBoxCell cell = (CheckBoxCell) view;
            isChecked = cell.isChecked();
            cell.setChecked(!isChecked, true);
            TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter;
            TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter2;
            TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter3;
            TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter4;
            TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter5;
            TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter6;
            TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter7;
            TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter8;
            TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter9;
            TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter10;
            TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter11;
            TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter12;
            TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter13;
            int count;
            View child;
            int pos;
            boolean z;
            if (position == 0) {
                if (isChecked) {
                    this.currentFilter = new TL_channelAdminLogEventsFilter();
                    tL_channelAdminLogEventsFilter = this.currentFilter;
                    tL_channelAdminLogEventsFilter2 = this.currentFilter;
                    tL_channelAdminLogEventsFilter3 = this.currentFilter;
                    tL_channelAdminLogEventsFilter4 = this.currentFilter;
                    tL_channelAdminLogEventsFilter5 = this.currentFilter;
                    tL_channelAdminLogEventsFilter6 = this.currentFilter;
                    tL_channelAdminLogEventsFilter7 = this.currentFilter;
                    tL_channelAdminLogEventsFilter8 = this.currentFilter;
                    tL_channelAdminLogEventsFilter9 = this.currentFilter;
                    tL_channelAdminLogEventsFilter10 = this.currentFilter;
                    tL_channelAdminLogEventsFilter11 = this.currentFilter;
                    tL_channelAdminLogEventsFilter12 = this.currentFilter;
                    tL_channelAdminLogEventsFilter13 = this.currentFilter;
                    this.currentFilter.delete = false;
                    tL_channelAdminLogEventsFilter13.edit = false;
                    tL_channelAdminLogEventsFilter12.pinned = false;
                    tL_channelAdminLogEventsFilter11.settings = false;
                    tL_channelAdminLogEventsFilter10.info = false;
                    tL_channelAdminLogEventsFilter9.demote = false;
                    tL_channelAdminLogEventsFilter8.promote = false;
                    tL_channelAdminLogEventsFilter7.unkick = false;
                    tL_channelAdminLogEventsFilter6.kick = false;
                    tL_channelAdminLogEventsFilter5.unban = false;
                    tL_channelAdminLogEventsFilter4.ban = false;
                    tL_channelAdminLogEventsFilter3.invite = false;
                    tL_channelAdminLogEventsFilter2.leave = false;
                    tL_channelAdminLogEventsFilter.join = false;
                } else {
                    this.currentFilter = null;
                }
                count = this.listView.getChildCount();
                for (a = 0; a < count; a++) {
                    child = this.listView.getChildAt(a);
                    holder = this.listView.findContainingViewHolder(child);
                    pos = holder.getAdapterPosition();
                    if (holder.getItemViewType() == 0 && pos > 0 && pos < this.allAdminsRow - 1) {
                        CheckBoxCell checkBoxCell = (CheckBoxCell) child;
                        if (isChecked) {
                            z = false;
                        } else {
                            z = true;
                        }
                        checkBoxCell.setChecked(z, true);
                    }
                }
            } else if (position == this.allAdminsRow) {
                if (isChecked) {
                    this.selectedAdmins = new SparseArray();
                } else {
                    this.selectedAdmins = null;
                }
                count = this.listView.getChildCount();
                for (a = 0; a < count; a++) {
                    child = this.listView.getChildAt(a);
                    holder = this.listView.findContainingViewHolder(child);
                    pos = holder.getAdapterPosition();
                    if (holder.getItemViewType() == 2) {
                        CheckBoxUserCell userCell = (CheckBoxUserCell) child;
                        if (isChecked) {
                            z = false;
                        } else {
                            z = true;
                        }
                        userCell.setChecked(z, true);
                    }
                }
            } else {
                if (this.currentFilter == null) {
                    this.currentFilter = new TL_channelAdminLogEventsFilter();
                    tL_channelAdminLogEventsFilter = this.currentFilter;
                    tL_channelAdminLogEventsFilter2 = this.currentFilter;
                    tL_channelAdminLogEventsFilter3 = this.currentFilter;
                    tL_channelAdminLogEventsFilter4 = this.currentFilter;
                    tL_channelAdminLogEventsFilter5 = this.currentFilter;
                    tL_channelAdminLogEventsFilter6 = this.currentFilter;
                    tL_channelAdminLogEventsFilter7 = this.currentFilter;
                    tL_channelAdminLogEventsFilter8 = this.currentFilter;
                    tL_channelAdminLogEventsFilter9 = this.currentFilter;
                    tL_channelAdminLogEventsFilter10 = this.currentFilter;
                    tL_channelAdminLogEventsFilter11 = this.currentFilter;
                    tL_channelAdminLogEventsFilter12 = this.currentFilter;
                    tL_channelAdminLogEventsFilter13 = this.currentFilter;
                    this.currentFilter.delete = true;
                    tL_channelAdminLogEventsFilter13.edit = true;
                    tL_channelAdminLogEventsFilter12.pinned = true;
                    tL_channelAdminLogEventsFilter11.settings = true;
                    tL_channelAdminLogEventsFilter10.info = true;
                    tL_channelAdminLogEventsFilter9.demote = true;
                    tL_channelAdminLogEventsFilter8.promote = true;
                    tL_channelAdminLogEventsFilter7.unkick = true;
                    tL_channelAdminLogEventsFilter6.kick = true;
                    tL_channelAdminLogEventsFilter5.unban = true;
                    tL_channelAdminLogEventsFilter4.ban = true;
                    tL_channelAdminLogEventsFilter3.invite = true;
                    tL_channelAdminLogEventsFilter2.leave = true;
                    tL_channelAdminLogEventsFilter.join = true;
                    holder = this.listView.findViewHolderForAdapterPosition(0);
                    if (holder != null) {
                        ((CheckBoxCell) holder.itemView).setChecked(false, true);
                    }
                }
                if (position == this.restrictionsRow) {
                    tL_channelAdminLogEventsFilter2 = this.currentFilter;
                    tL_channelAdminLogEventsFilter3 = this.currentFilter;
                    tL_channelAdminLogEventsFilter4 = this.currentFilter;
                    tL_channelAdminLogEventsFilter5 = this.currentFilter;
                    if (this.currentFilter.kick) {
                        z = false;
                    } else {
                        z = true;
                    }
                    tL_channelAdminLogEventsFilter5.unban = z;
                    tL_channelAdminLogEventsFilter4.unkick = z;
                    tL_channelAdminLogEventsFilter3.ban = z;
                    tL_channelAdminLogEventsFilter2.kick = z;
                } else if (position == this.adminsRow) {
                    tL_channelAdminLogEventsFilter2 = this.currentFilter;
                    tL_channelAdminLogEventsFilter3 = this.currentFilter;
                    z = !this.currentFilter.demote;
                    tL_channelAdminLogEventsFilter3.demote = z;
                    tL_channelAdminLogEventsFilter2.promote = z;
                } else if (position == this.membersRow) {
                    tL_channelAdminLogEventsFilter2 = this.currentFilter;
                    tL_channelAdminLogEventsFilter3 = this.currentFilter;
                    z = !this.currentFilter.join;
                    tL_channelAdminLogEventsFilter3.join = z;
                    tL_channelAdminLogEventsFilter2.invite = z;
                } else if (position == this.infoRow) {
                    tL_channelAdminLogEventsFilter2 = this.currentFilter;
                    tL_channelAdminLogEventsFilter3 = this.currentFilter;
                    z = !this.currentFilter.info;
                    tL_channelAdminLogEventsFilter3.settings = z;
                    tL_channelAdminLogEventsFilter2.info = z;
                } else if (position == this.deleteRow) {
                    this.currentFilter.delete = !this.currentFilter.delete;
                } else if (position == this.editRow) {
                    this.currentFilter.edit = !this.currentFilter.edit;
                } else if (position == this.pinnedRow) {
                    this.currentFilter.pinned = !this.currentFilter.pinned;
                } else if (position == this.leavingRow) {
                    this.currentFilter.leave = !this.currentFilter.leave;
                }
            }
            if (this.currentFilter == null || this.currentFilter.join || this.currentFilter.leave || this.currentFilter.leave || this.currentFilter.invite || this.currentFilter.ban || this.currentFilter.unban || this.currentFilter.kick || this.currentFilter.unkick || this.currentFilter.promote || this.currentFilter.demote || this.currentFilter.info || this.currentFilter.settings || this.currentFilter.pinned || this.currentFilter.edit || this.currentFilter.delete) {
                this.saveButton.setEnabled(true);
                this.saveButton.setAlpha(1.0f);
                return;
            }
            this.saveButton.setEnabled(false);
            this.saveButton.setAlpha(0.5f);
        } else if (view instanceof CheckBoxUserCell) {
            User user;
            CheckBoxUserCell checkBoxUserCell = (CheckBoxUserCell) view;
            if (this.selectedAdmins == null) {
                this.selectedAdmins = new SparseArray();
                holder = this.listView.findViewHolderForAdapterPosition(this.allAdminsRow);
                if (holder != null) {
                    ((CheckBoxCell) holder.itemView).setChecked(false, true);
                }
                for (a = 0; a < this.currentAdmins.size(); a++) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((ChannelParticipant) this.currentAdmins.get(a)).user_id));
                    this.selectedAdmins.put(user.var_id, user);
                }
            }
            isChecked = checkBoxUserCell.isChecked();
            user = checkBoxUserCell.getCurrentUser();
            if (isChecked) {
                this.selectedAdmins.remove(user.var_id);
            } else {
                this.selectedAdmins.put(user.var_id, user);
            }
            checkBoxUserCell.setChecked(!isChecked, true);
        }
    }

    final /* synthetic */ void lambda$new$1$AdminLogFilterAlert(View v) {
        this.delegate.didSelectRights(this.currentFilter, this.selectedAdmins);
        lambda$new$4$EmbedBottomSheet();
    }

    public void setCurrentAdmins(ArrayList<ChannelParticipant> admins) {
        this.currentAdmins = admins;
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void setAdminLogFilterAlertDelegate(AdminLogFilterAlertDelegate adminLogFilterAlertDelegate) {
        this.delegate = adminLogFilterAlertDelegate;
    }

    @SuppressLint({"NewApi"})
    private void updateLayout() {
        int newOffset = 0;
        RecyclerListView recyclerListView;
        if (this.listView.getChildCount() <= 0) {
            recyclerListView = this.listView;
            int paddingTop = this.listView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        View child = this.listView.getChildAt(0);
        Holder holder = (Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop() - AndroidUtilities.m9dp(8.0f);
        if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        if (this.scrollOffsetY != newOffset) {
            recyclerListView = this.listView;
            this.scrollOffsetY = newOffset;
            recyclerListView.setTopGlowOffset(newOffset);
            this.containerView.invalidate();
        }
    }
}
