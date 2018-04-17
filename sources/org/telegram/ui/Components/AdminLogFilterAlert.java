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
import android.view.View.OnClickListener;
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
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.StickerPreviewViewer;

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

    /* renamed from: org.telegram.ui.Components.AdminLogFilterAlert$5 */
    class C10645 implements OnClickListener {
        C10645() {
        }

        public void onClick(View v) {
            AdminLogFilterAlert.this.delegate.didSelectRights(AdminLogFilterAlert.this.currentFilter, AdminLogFilterAlert.this.selectedAdmins);
            AdminLogFilterAlert.this.dismiss();
        }
    }

    public interface AdminLogFilterAlertDelegate {
        void didSelectRights(TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, SparseArray<User> sparseArray);
    }

    /* renamed from: org.telegram.ui.Components.AdminLogFilterAlert$3 */
    class C20283 extends OnScrollListener {
        C20283() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            AdminLogFilterAlert.this.updateLayout();
        }
    }

    /* renamed from: org.telegram.ui.Components.AdminLogFilterAlert$4 */
    class C20294 implements OnItemClickListener {
        C20294() {
        }

        public void onItemClick(View view, int position) {
            C20294 c20294 = this;
            View view2 = view;
            int i = position;
            boolean z;
            if (view2 instanceof CheckBoxCell) {
                CheckBoxCell cell = (CheckBoxCell) view2;
                boolean isChecked = cell.isChecked();
                cell.setChecked(isChecked ^ 1, true);
                TL_channelAdminLogEventsFilter access$1000;
                TL_channelAdminLogEventsFilter access$10002;
                TL_channelAdminLogEventsFilter access$10003;
                TL_channelAdminLogEventsFilter access$10004;
                TL_channelAdminLogEventsFilter access$10005;
                TL_channelAdminLogEventsFilter access$10006;
                TL_channelAdminLogEventsFilter access$10007;
                TL_channelAdminLogEventsFilter access$10008;
                TL_channelAdminLogEventsFilter access$10009;
                TL_channelAdminLogEventsFilter access$100010;
                TL_channelAdminLogEventsFilter access$100011;
                boolean isChecked2;
                int count;
                if (i == 0) {
                    if (isChecked) {
                        AdminLogFilterAlert.this.currentFilter = new TL_channelAdminLogEventsFilter();
                        access$1000 = AdminLogFilterAlert.this.currentFilter;
                        access$10002 = AdminLogFilterAlert.this.currentFilter;
                        access$10003 = AdminLogFilterAlert.this.currentFilter;
                        access$10004 = AdminLogFilterAlert.this.currentFilter;
                        access$10005 = AdminLogFilterAlert.this.currentFilter;
                        access$10006 = AdminLogFilterAlert.this.currentFilter;
                        access$10007 = AdminLogFilterAlert.this.currentFilter;
                        access$10008 = AdminLogFilterAlert.this.currentFilter;
                        TL_channelAdminLogEventsFilter access$100012 = AdminLogFilterAlert.this.currentFilter;
                        access$10009 = AdminLogFilterAlert.this.currentFilter;
                        access$100010 = AdminLogFilterAlert.this.currentFilter;
                        cell = AdminLogFilterAlert.this.currentFilter;
                        access$100011 = AdminLogFilterAlert.this.currentFilter;
                        isChecked2 = isChecked;
                        AdminLogFilterAlert.this.currentFilter.delete = false;
                        access$100011.edit = false;
                        cell.pinned = false;
                        access$100010.settings = false;
                        access$10009.info = false;
                        access$100012.demote = false;
                        access$10008.promote = false;
                        access$10007.unkick = false;
                        access$10006.kick = false;
                        access$10005.unban = false;
                        access$10004.ban = false;
                        access$10003.invite = false;
                        access$10002.leave = false;
                        access$1000.join = false;
                    } else {
                        isChecked2 = isChecked;
                        AdminLogFilterAlert.this.currentFilter = null;
                    }
                    count = AdminLogFilterAlert.this.listView.getChildCount();
                    for (i = 0; i < count; i++) {
                        ViewHolder holder;
                        cell = AdminLogFilterAlert.this.listView.getChildAt(i);
                        holder = AdminLogFilterAlert.this.listView.findContainingViewHolder(cell);
                        int pos = holder.getAdapterPosition();
                        if (holder.getItemViewType() == 0 && pos > 0 && pos < AdminLogFilterAlert.this.allAdminsRow - 1) {
                            cell.setChecked(isChecked2 ^ 1, true);
                        }
                    }
                    i = position;
                } else {
                    isChecked2 = isChecked;
                    if (position == AdminLogFilterAlert.this.allAdminsRow) {
                        if (isChecked2) {
                            AdminLogFilterAlert.this.selectedAdmins = new SparseArray();
                        } else {
                            AdminLogFilterAlert.this.selectedAdmins = null;
                        }
                        count = AdminLogFilterAlert.this.listView.getChildCount();
                        for (int a = 0; a < count; a++) {
                            View child = AdminLogFilterAlert.this.listView.getChildAt(a);
                            ViewHolder holder2 = AdminLogFilterAlert.this.listView.findContainingViewHolder(child);
                            int pos2 = holder2.getAdapterPosition();
                            if (holder2.getItemViewType() == 2) {
                                ((CheckBoxUserCell) child).setChecked(isChecked2 ^ 1, true);
                            }
                        }
                    } else {
                        TL_channelAdminLogEventsFilter access$100013;
                        if (AdminLogFilterAlert.this.currentFilter == null) {
                            AdminLogFilterAlert.this.currentFilter = new TL_channelAdminLogEventsFilter();
                            access$100011 = AdminLogFilterAlert.this.currentFilter;
                            access$100013 = AdminLogFilterAlert.this.currentFilter;
                            access$100010 = AdminLogFilterAlert.this.currentFilter;
                            access$10009 = AdminLogFilterAlert.this.currentFilter;
                            TL_channelAdminLogEventsFilter access$100014 = AdminLogFilterAlert.this.currentFilter;
                            access$1000 = AdminLogFilterAlert.this.currentFilter;
                            access$10002 = AdminLogFilterAlert.this.currentFilter;
                            access$10003 = AdminLogFilterAlert.this.currentFilter;
                            access$10004 = AdminLogFilterAlert.this.currentFilter;
                            access$10005 = AdminLogFilterAlert.this.currentFilter;
                            access$10006 = AdminLogFilterAlert.this.currentFilter;
                            access$10007 = AdminLogFilterAlert.this.currentFilter;
                            access$10008 = AdminLogFilterAlert.this.currentFilter;
                            AdminLogFilterAlert.this.currentFilter.delete = true;
                            access$10008.edit = true;
                            access$10007.pinned = true;
                            access$10006.settings = true;
                            access$10005.info = true;
                            access$10004.demote = true;
                            access$10003.promote = true;
                            access$10002.unkick = true;
                            access$1000.kick = true;
                            access$100014.unban = true;
                            access$10009.ban = true;
                            access$100010.invite = true;
                            access$100013.leave = true;
                            access$100011.join = true;
                            ViewHolder holder3 = AdminLogFilterAlert.this.listView.findViewHolderForAdapterPosition(0);
                            if (holder3 != null) {
                                ((CheckBoxCell) holder3.itemView).setChecked(false, true);
                            }
                        }
                        i = position;
                        if (i == AdminLogFilterAlert.this.restrictionsRow) {
                            access$100011 = AdminLogFilterAlert.this.currentFilter;
                            access$100013 = AdminLogFilterAlert.this.currentFilter;
                            access$100010 = AdminLogFilterAlert.this.currentFilter;
                            isChecked = AdminLogFilterAlert.this.currentFilter.kick ^ true;
                            AdminLogFilterAlert.this.currentFilter.unban = isChecked;
                            access$100010.unkick = isChecked;
                            access$100013.ban = isChecked;
                            access$100011.kick = isChecked;
                        } else if (i == AdminLogFilterAlert.this.adminsRow) {
                            access$100011 = AdminLogFilterAlert.this.currentFilter;
                            z = AdminLogFilterAlert.this.currentFilter.demote ^ true;
                            AdminLogFilterAlert.this.currentFilter.demote = z;
                            access$100011.promote = z;
                        } else if (i == AdminLogFilterAlert.this.membersRow) {
                            access$100011 = AdminLogFilterAlert.this.currentFilter;
                            z = AdminLogFilterAlert.this.currentFilter.join ^ true;
                            AdminLogFilterAlert.this.currentFilter.join = z;
                            access$100011.invite = z;
                        } else if (i == AdminLogFilterAlert.this.infoRow) {
                            access$100011 = AdminLogFilterAlert.this.currentFilter;
                            z = AdminLogFilterAlert.this.currentFilter.info ^ true;
                            AdminLogFilterAlert.this.currentFilter.settings = z;
                            access$100011.info = z;
                        } else if (i == AdminLogFilterAlert.this.deleteRow) {
                            AdminLogFilterAlert.this.currentFilter.delete ^= true;
                        } else if (i == AdminLogFilterAlert.this.editRow) {
                            AdminLogFilterAlert.this.currentFilter.edit ^= true;
                        } else if (i == AdminLogFilterAlert.this.pinnedRow) {
                            AdminLogFilterAlert.this.currentFilter.pinned ^= true;
                        } else if (i == AdminLogFilterAlert.this.leavingRow) {
                            AdminLogFilterAlert.this.currentFilter.leave ^= true;
                        }
                    }
                }
                if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.join || AdminLogFilterAlert.this.currentFilter.leave || AdminLogFilterAlert.this.currentFilter.leave || AdminLogFilterAlert.this.currentFilter.invite || AdminLogFilterAlert.this.currentFilter.ban || AdminLogFilterAlert.this.currentFilter.unban || AdminLogFilterAlert.this.currentFilter.kick || AdminLogFilterAlert.this.currentFilter.unkick || AdminLogFilterAlert.this.currentFilter.promote || AdminLogFilterAlert.this.currentFilter.demote || AdminLogFilterAlert.this.currentFilter.info || AdminLogFilterAlert.this.currentFilter.settings || AdminLogFilterAlert.this.currentFilter.pinned || AdminLogFilterAlert.this.currentFilter.edit || AdminLogFilterAlert.this.currentFilter.delete) {
                    AdminLogFilterAlert.this.saveButton.setEnabled(true);
                    AdminLogFilterAlert.this.saveButton.setAlpha(1.0f);
                } else {
                    AdminLogFilterAlert.this.saveButton.setEnabled(false);
                    AdminLogFilterAlert.this.saveButton.setAlpha(0.5f);
                }
                view2 = view;
                return;
            }
            view2 = view;
            if (view2 instanceof CheckBoxUserCell) {
                CheckBoxUserCell checkBoxUserCell = (CheckBoxUserCell) view2;
                if (AdminLogFilterAlert.this.selectedAdmins == null) {
                    int a2;
                    AdminLogFilterAlert.this.selectedAdmins = new SparseArray();
                    holder = AdminLogFilterAlert.this.listView.findViewHolderForAdapterPosition(AdminLogFilterAlert.this.allAdminsRow);
                    if (holder != null) {
                        a2 = 0;
                        ((CheckBoxCell) holder.itemView).setChecked(false, true);
                    } else {
                        a2 = 0;
                    }
                    while (true) {
                        pos = a2;
                        if (pos >= AdminLogFilterAlert.this.currentAdmins.size()) {
                            break;
                        }
                        User user = MessagesController.getInstance(AdminLogFilterAlert.this.currentAccount).getUser(Integer.valueOf(((ChannelParticipant) AdminLogFilterAlert.this.currentAdmins.get(pos)).user_id));
                        AdminLogFilterAlert.this.selectedAdmins.put(user.id, user);
                        a2 = pos + 1;
                    }
                }
                z = checkBoxUserCell.isChecked();
                User user2 = checkBoxUserCell.getCurrentUser();
                if (z) {
                    AdminLogFilterAlert.this.selectedAdmins.remove(user2.id);
                } else {
                    AdminLogFilterAlert.this.selectedAdmins.put(user2.id, user2);
                }
                checkBoxUserCell.setChecked(z ^ 1, true);
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context context;

        public ListAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            return (AdminLogFilterAlert.this.isMegagroup ? 9 : 7) + (AdminLogFilterAlert.this.currentAdmins != null ? 2 + AdminLogFilterAlert.this.currentAdmins.size() : 0);
        }

        public int getItemViewType(int position) {
            if (position >= AdminLogFilterAlert.this.allAdminsRow - 1) {
                if (position != AdminLogFilterAlert.this.allAdminsRow) {
                    if (position == AdminLogFilterAlert.this.allAdminsRow - 1) {
                        return 1;
                    }
                    return 2;
                }
            }
            return 0;
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new CheckBoxCell(this.context, 1);
                    view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    break;
                case 1:
                    ShadowSectionCell shadowSectionCell = new ShadowSectionCell(this.context);
                    shadowSectionCell.setSize(18);
                    view = new FrameLayout(this.context);
                    ((FrameLayout) view).addView(shadowSectionCell, LayoutHelper.createFrame(-1, -1.0f));
                    view.setBackgroundColor(Theme.getColor(Theme.key_dialogBackgroundGray));
                    break;
                case 2:
                    view = new CheckBoxUserCell(this.context, true);
                    break;
                default:
                    break;
            }
            return new Holder(view);
        }

        public void onViewAttachedToWindow(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            int itemViewType = holder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                CheckBoxCell cell = holder.itemView;
                if (position == 0) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        z = false;
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.restrictionsRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.kick || !AdminLogFilterAlert.this.currentFilter.ban || !AdminLogFilterAlert.this.currentFilter.unkick || !AdminLogFilterAlert.this.currentFilter.unban) {
                            z = false;
                        }
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.adminsRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.promote || !AdminLogFilterAlert.this.currentFilter.demote) {
                            z = false;
                        }
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.membersRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.invite || !AdminLogFilterAlert.this.currentFilter.join) {
                            z = false;
                        }
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.infoRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.info) {
                            z = false;
                        }
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.deleteRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.delete) {
                            z = false;
                        }
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.editRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.edit) {
                            z = false;
                        }
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.pinnedRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.pinned) {
                            z = false;
                        }
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.leavingRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.leave) {
                            z = false;
                        }
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.allAdminsRow) {
                    if (AdminLogFilterAlert.this.selectedAdmins != null) {
                        z = false;
                    }
                    cell.setChecked(z, false);
                }
            } else if (itemViewType == 2) {
                CheckBoxUserCell userCell = holder.itemView;
                int userId = ((ChannelParticipant) AdminLogFilterAlert.this.currentAdmins.get((position - AdminLogFilterAlert.this.allAdminsRow) - 1)).user_id;
                if (AdminLogFilterAlert.this.selectedAdmins != null) {
                    if (AdminLogFilterAlert.this.selectedAdmins.indexOfKey(userId) < 0) {
                        z = false;
                    }
                }
                userCell.setChecked(z, false);
            }
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            boolean z = false;
            boolean z2 = true;
            if (itemViewType == 0) {
                CheckBoxCell cell = holder.itemView;
                String string;
                String str;
                if (position == 0) {
                    string = LocaleController.getString("EventLogFilterAll", R.string.EventLogFilterAll);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter == null) {
                        z = true;
                    }
                    cell.setText(string, str, z, true);
                } else if (position == AdminLogFilterAlert.this.restrictionsRow) {
                    string = LocaleController.getString("EventLogFilterNewRestrictions", R.string.EventLogFilterNewRestrictions);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.kick || !AdminLogFilterAlert.this.currentFilter.ban || !AdminLogFilterAlert.this.currentFilter.unkick || !AdminLogFilterAlert.this.currentFilter.unban) {
                            cell.setText(string, str, z, true);
                        }
                    }
                    z = true;
                    cell.setText(string, str, z, true);
                } else if (position == AdminLogFilterAlert.this.adminsRow) {
                    string = LocaleController.getString("EventLogFilterNewAdmins", R.string.EventLogFilterNewAdmins);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.promote || !AdminLogFilterAlert.this.currentFilter.demote) {
                            cell.setText(string, str, z, true);
                        }
                    }
                    z = true;
                    cell.setText(string, str, z, true);
                } else if (position == AdminLogFilterAlert.this.membersRow) {
                    string = LocaleController.getString("EventLogFilterNewMembers", R.string.EventLogFilterNewMembers);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.invite || !AdminLogFilterAlert.this.currentFilter.join) {
                            cell.setText(string, str, z, true);
                        }
                    }
                    z = true;
                    cell.setText(string, str, z, true);
                } else if (position == AdminLogFilterAlert.this.infoRow) {
                    if (AdminLogFilterAlert.this.isMegagroup) {
                        string = LocaleController.getString("EventLogFilterGroupInfo", R.string.EventLogFilterGroupInfo);
                        str = TtmlNode.ANONYMOUS_REGION_ID;
                        if (AdminLogFilterAlert.this.currentFilter != null) {
                            if (!AdminLogFilterAlert.this.currentFilter.info) {
                                cell.setText(string, str, z, true);
                                return;
                            }
                        }
                        z = true;
                        cell.setText(string, str, z, true);
                        return;
                    }
                    string = LocaleController.getString("EventLogFilterChannelInfo", R.string.EventLogFilterChannelInfo);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.info) {
                            cell.setText(string, str, z, true);
                        }
                    }
                    z = true;
                    cell.setText(string, str, z, true);
                } else if (position == AdminLogFilterAlert.this.deleteRow) {
                    string = LocaleController.getString("EventLogFilterDeletedMessages", R.string.EventLogFilterDeletedMessages);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.delete) {
                            cell.setText(string, str, z, true);
                        }
                    }
                    z = true;
                    cell.setText(string, str, z, true);
                } else if (position == AdminLogFilterAlert.this.editRow) {
                    string = LocaleController.getString("EventLogFilterEditedMessages", R.string.EventLogFilterEditedMessages);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.edit) {
                            cell.setText(string, str, z, true);
                        }
                    }
                    z = true;
                    cell.setText(string, str, z, true);
                } else if (position == AdminLogFilterAlert.this.pinnedRow) {
                    string = LocaleController.getString("EventLogFilterPinnedMessages", R.string.EventLogFilterPinnedMessages);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.pinned) {
                            cell.setText(string, str, z, true);
                        }
                    }
                    z = true;
                    cell.setText(string, str, z, true);
                } else if (position == AdminLogFilterAlert.this.leavingRow) {
                    string = LocaleController.getString("EventLogFilterLeavingMembers", R.string.EventLogFilterLeavingMembers);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.leave) {
                            z2 = false;
                        }
                    }
                    cell.setText(string, str, z2, false);
                } else if (position == AdminLogFilterAlert.this.allAdminsRow) {
                    string = LocaleController.getString("EventLogAllAdmins", R.string.EventLogAllAdmins);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.selectedAdmins == null) {
                        z = true;
                    }
                    cell.setText(string, str, z, true);
                }
            } else if (itemViewType == 2) {
                boolean z3;
                CheckBoxUserCell userCell = holder.itemView;
                int userId = ((ChannelParticipant) AdminLogFilterAlert.this.currentAdmins.get((position - AdminLogFilterAlert.this.allAdminsRow) - 1)).user_id;
                User user = MessagesController.getInstance(AdminLogFilterAlert.this.currentAccount).getUser(Integer.valueOf(userId));
                if (AdminLogFilterAlert.this.selectedAdmins != null) {
                    if (AdminLogFilterAlert.this.selectedAdmins.indexOfKey(userId) < 0) {
                        z3 = false;
                        if (position != getItemCount() - 1) {
                            z = true;
                        }
                        userCell.setUser(user, z3, z);
                    }
                }
                z3 = true;
                if (position != getItemCount() - 1) {
                    z = true;
                }
                userCell.setUser(user, z3, z);
            }
        }
    }

    public AdminLogFilterAlert(Context context, TL_channelAdminLogEventsFilter filter, SparseArray<User> admins, boolean megagroup) {
        int rowCount;
        Context context2 = context;
        TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter = filter;
        super(context2, false);
        if (tL_channelAdminLogEventsFilter != null) {
            r0.currentFilter = new TL_channelAdminLogEventsFilter();
            r0.currentFilter.join = tL_channelAdminLogEventsFilter.join;
            r0.currentFilter.leave = tL_channelAdminLogEventsFilter.leave;
            r0.currentFilter.invite = tL_channelAdminLogEventsFilter.invite;
            r0.currentFilter.ban = tL_channelAdminLogEventsFilter.ban;
            r0.currentFilter.unban = tL_channelAdminLogEventsFilter.unban;
            r0.currentFilter.kick = tL_channelAdminLogEventsFilter.kick;
            r0.currentFilter.unkick = tL_channelAdminLogEventsFilter.unkick;
            r0.currentFilter.promote = tL_channelAdminLogEventsFilter.promote;
            r0.currentFilter.demote = tL_channelAdminLogEventsFilter.demote;
            r0.currentFilter.info = tL_channelAdminLogEventsFilter.info;
            r0.currentFilter.settings = tL_channelAdminLogEventsFilter.settings;
            r0.currentFilter.pinned = tL_channelAdminLogEventsFilter.pinned;
            r0.currentFilter.edit = tL_channelAdminLogEventsFilter.edit;
            r0.currentFilter.delete = tL_channelAdminLogEventsFilter.delete;
        }
        if (admins != null) {
            r0.selectedAdmins = admins.clone();
        }
        r0.isMegagroup = megagroup;
        int rowCount2 = 1;
        if (r0.isMegagroup) {
            rowCount = 1 + 1;
            r0.restrictionsRow = 1;
            rowCount2 = rowCount;
        } else {
            r0.restrictionsRow = -1;
        }
        rowCount = rowCount2 + 1;
        r0.adminsRow = rowCount2;
        rowCount2 = rowCount + 1;
        r0.membersRow = rowCount;
        rowCount = rowCount2 + 1;
        r0.infoRow = rowCount2;
        rowCount2 = rowCount + 1;
        r0.deleteRow = rowCount;
        rowCount = rowCount2 + 1;
        r0.editRow = rowCount2;
        if (r0.isMegagroup) {
            rowCount2 = rowCount + 1;
            r0.pinnedRow = rowCount;
        } else {
            r0.pinnedRow = -1;
            rowCount2 = rowCount;
        }
        r0.leavingRow = rowCount2;
        r0.allAdminsRow = rowCount2 + 2;
        r0.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow).mutate();
        r0.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), Mode.MULTIPLY));
        r0.containerView = new FrameLayout(context2) {
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() != 0 || AdminLogFilterAlert.this.scrollOffsetY == 0 || ev.getY() >= ((float) AdminLogFilterAlert.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(ev);
                }
                AdminLogFilterAlert.this.dismiss();
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
                int contentSize = (AndroidUtilities.dp(48.0f) + ((AdminLogFilterAlert.this.isMegagroup ? 9 : 7) * AndroidUtilities.dp(48.0f))) + AdminLogFilterAlert.backgroundPaddingTop;
                if (AdminLogFilterAlert.this.currentAdmins != null) {
                    contentSize += ((AdminLogFilterAlert.this.currentAdmins.size() + 1) * AndroidUtilities.dp(48.0f)) + AndroidUtilities.dp(20.0f);
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
        r0.containerView.setWillNotDraw(false);
        r0.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
        r0.listView = new RecyclerListView(context2) {
            public boolean onInterceptTouchEvent(MotionEvent event) {
                boolean result = StickerPreviewViewer.getInstance().onInterceptTouchEvent(event, AdminLogFilterAlert.this.listView, 0, null);
                if (!super.onInterceptTouchEvent(event)) {
                    if (!result) {
                        return false;
                    }
                }
                return true;
            }

            public void requestLayout() {
                if (!AdminLogFilterAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        r0.listView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        RecyclerListView recyclerListView = r0.listView;
        Adapter listAdapter = new ListAdapter(context2);
        r0.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        r0.listView.setVerticalScrollBarEnabled(false);
        r0.listView.setClipToPadding(false);
        r0.listView.setEnabled(true);
        r0.listView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        r0.listView.setOnScrollListener(new C20283());
        r0.listView.setOnItemClickListener(new C20294());
        r0.containerView.addView(r0.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        View shadow = new View(context2);
        shadow.setBackgroundResource(R.drawable.header_shadow_reverse);
        r0.containerView.addView(shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        r0.saveButton = new BottomSheetCell(context2, 1);
        r0.saveButton.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        r0.saveButton.setTextAndIcon(LocaleController.getString("Save", R.string.Save).toUpperCase(), 0);
        r0.saveButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        r0.saveButton.setOnClickListener(new C10645());
        r0.containerView.addView(r0.saveButton, LayoutHelper.createFrame(-1, 48, 83));
        r0.adapter.notifyDataSetChanged();
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
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = this.listView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        paddingTop = 0;
        View child = this.listView.getChildAt(0);
        Holder holder = (Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop() - AndroidUtilities.dp(8.0f);
        if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
            paddingTop = top;
        }
        if (this.scrollOffsetY != paddingTop) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = paddingTop;
            recyclerListView2.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
        }
    }
}
