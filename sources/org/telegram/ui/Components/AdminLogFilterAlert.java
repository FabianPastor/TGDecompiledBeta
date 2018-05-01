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
import org.telegram.messenger.C0446R;
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
    class C10705 implements OnClickListener {
        C10705() {
        }

        public void onClick(View view) {
            AdminLogFilterAlert.this.delegate.didSelectRights(AdminLogFilterAlert.this.currentFilter, AdminLogFilterAlert.this.selectedAdmins);
            AdminLogFilterAlert.this.dismiss();
        }
    }

    public interface AdminLogFilterAlertDelegate {
        void didSelectRights(TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, SparseArray<User> sparseArray);
    }

    /* renamed from: org.telegram.ui.Components.AdminLogFilterAlert$3 */
    class C20343 extends OnScrollListener {
        C20343() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            AdminLogFilterAlert.this.updateLayout();
        }
    }

    /* renamed from: org.telegram.ui.Components.AdminLogFilterAlert$4 */
    class C20354 implements OnItemClickListener {
        C20354() {
        }

        public void onItemClick(View view, int i) {
            C20354 c20354 = this;
            View view2 = view;
            int i2 = i;
            if (view2 instanceof CheckBoxCell) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) view2;
                boolean isChecked = checkBoxCell.isChecked();
                checkBoxCell.setChecked(isChecked ^ 1, true);
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
                TL_channelAdminLogEventsFilter access$100012;
                TL_channelAdminLogEventsFilter access$100013;
                int childCount;
                View childAt;
                ViewHolder findContainingViewHolder;
                if (i2 == 0) {
                    int i3;
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
                        access$10009 = AdminLogFilterAlert.this.currentFilter;
                        access$100010 = AdminLogFilterAlert.this.currentFilter;
                        access$100011 = AdminLogFilterAlert.this.currentFilter;
                        access$100012 = AdminLogFilterAlert.this.currentFilter;
                        access$100013 = AdminLogFilterAlert.this.currentFilter;
                        i3 = isChecked;
                        AdminLogFilterAlert.this.currentFilter.delete = false;
                        access$100013.edit = false;
                        access$100012.pinned = false;
                        access$100011.settings = false;
                        access$100010.info = false;
                        access$10009.demote = false;
                        access$10008.promote = false;
                        access$10007.unkick = false;
                        access$10006.kick = false;
                        access$10005.unban = false;
                        access$10004.ban = false;
                        access$10003.invite = false;
                        access$10002.leave = false;
                        access$1000.join = false;
                    } else {
                        i3 = isChecked;
                        AdminLogFilterAlert.this.currentFilter = null;
                    }
                    childCount = AdminLogFilterAlert.this.listView.getChildCount();
                    for (i2 = 0; i2 < childCount; i2++) {
                        childAt = AdminLogFilterAlert.this.listView.getChildAt(i2);
                        findContainingViewHolder = AdminLogFilterAlert.this.listView.findContainingViewHolder(childAt);
                        int adapterPosition = findContainingViewHolder.getAdapterPosition();
                        if (findContainingViewHolder.getItemViewType() == 0 && adapterPosition > 0 && adapterPosition < AdminLogFilterAlert.this.allAdminsRow - 1) {
                            ((CheckBoxCell) childAt).setChecked(i3 ^ 1, true);
                        }
                    }
                } else {
                    boolean z = isChecked;
                    if (i2 == AdminLogFilterAlert.this.allAdminsRow) {
                        if (z) {
                            AdminLogFilterAlert.this.selectedAdmins = new SparseArray();
                        } else {
                            AdminLogFilterAlert.this.selectedAdmins = null;
                        }
                        childCount = AdminLogFilterAlert.this.listView.getChildCount();
                        for (i2 = 0; i2 < childCount; i2++) {
                            childAt = AdminLogFilterAlert.this.listView.getChildAt(i2);
                            findContainingViewHolder = AdminLogFilterAlert.this.listView.findContainingViewHolder(childAt);
                            findContainingViewHolder.getAdapterPosition();
                            if (findContainingViewHolder.getItemViewType() == 2) {
                                ((CheckBoxUserCell) childAt).setChecked(z ^ 1, true);
                            }
                        }
                    } else {
                        TL_channelAdminLogEventsFilter access$100014;
                        if (AdminLogFilterAlert.this.currentFilter == null) {
                            AdminLogFilterAlert.this.currentFilter = new TL_channelAdminLogEventsFilter();
                            access$1000 = AdminLogFilterAlert.this.currentFilter;
                            access$100014 = AdminLogFilterAlert.this.currentFilter;
                            access$100013 = AdminLogFilterAlert.this.currentFilter;
                            access$10003 = AdminLogFilterAlert.this.currentFilter;
                            access$10004 = AdminLogFilterAlert.this.currentFilter;
                            access$10005 = AdminLogFilterAlert.this.currentFilter;
                            access$10006 = AdminLogFilterAlert.this.currentFilter;
                            access$10007 = AdminLogFilterAlert.this.currentFilter;
                            access$10008 = AdminLogFilterAlert.this.currentFilter;
                            access$10009 = AdminLogFilterAlert.this.currentFilter;
                            access$100010 = AdminLogFilterAlert.this.currentFilter;
                            access$100011 = AdminLogFilterAlert.this.currentFilter;
                            access$100012 = AdminLogFilterAlert.this.currentFilter;
                            AdminLogFilterAlert.this.currentFilter.delete = true;
                            access$100012.edit = true;
                            access$100011.pinned = true;
                            access$100010.settings = true;
                            access$10009.info = true;
                            access$10008.demote = true;
                            access$10007.promote = true;
                            access$10006.unkick = true;
                            access$10005.kick = true;
                            access$10004.unban = true;
                            access$10003.ban = true;
                            access$100013.invite = true;
                            access$100014.leave = true;
                            access$1000.join = true;
                            ViewHolder findViewHolderForAdapterPosition = AdminLogFilterAlert.this.listView.findViewHolderForAdapterPosition(0);
                            if (findViewHolderForAdapterPosition != null) {
                                ((CheckBoxCell) findViewHolderForAdapterPosition.itemView).setChecked(false, true);
                            }
                        }
                        i2 = i;
                        if (i2 == AdminLogFilterAlert.this.restrictionsRow) {
                            access$1000 = AdminLogFilterAlert.this.currentFilter;
                            access$10002 = AdminLogFilterAlert.this.currentFilter;
                            access$100014 = AdminLogFilterAlert.this.currentFilter;
                            boolean z2 = AdminLogFilterAlert.this.currentFilter.kick ^ true;
                            AdminLogFilterAlert.this.currentFilter.unban = z2;
                            access$100014.unkick = z2;
                            access$10002.ban = z2;
                            access$1000.kick = z2;
                        } else if (i2 == AdminLogFilterAlert.this.adminsRow) {
                            access$1000 = AdminLogFilterAlert.this.currentFilter;
                            isChecked = AdminLogFilterAlert.this.currentFilter.demote ^ true;
                            AdminLogFilterAlert.this.currentFilter.demote = isChecked;
                            access$1000.promote = isChecked;
                        } else if (i2 == AdminLogFilterAlert.this.membersRow) {
                            access$1000 = AdminLogFilterAlert.this.currentFilter;
                            isChecked = AdminLogFilterAlert.this.currentFilter.join ^ true;
                            AdminLogFilterAlert.this.currentFilter.join = isChecked;
                            access$1000.invite = isChecked;
                        } else if (i2 == AdminLogFilterAlert.this.infoRow) {
                            access$1000 = AdminLogFilterAlert.this.currentFilter;
                            isChecked = AdminLogFilterAlert.this.currentFilter.info ^ true;
                            AdminLogFilterAlert.this.currentFilter.settings = isChecked;
                            access$1000.info = isChecked;
                        } else if (i2 == AdminLogFilterAlert.this.deleteRow) {
                            AdminLogFilterAlert.this.currentFilter.delete ^= true;
                        } else if (i2 == AdminLogFilterAlert.this.editRow) {
                            AdminLogFilterAlert.this.currentFilter.edit ^= true;
                        } else if (i2 == AdminLogFilterAlert.this.pinnedRow) {
                            AdminLogFilterAlert.this.currentFilter.pinned ^= true;
                        } else if (i2 == AdminLogFilterAlert.this.leavingRow) {
                            AdminLogFilterAlert.this.currentFilter.leave ^= true;
                        }
                    }
                }
                if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.join || AdminLogFilterAlert.this.currentFilter.leave || AdminLogFilterAlert.this.currentFilter.leave || AdminLogFilterAlert.this.currentFilter.invite || AdminLogFilterAlert.this.currentFilter.ban || AdminLogFilterAlert.this.currentFilter.unban || AdminLogFilterAlert.this.currentFilter.kick || AdminLogFilterAlert.this.currentFilter.unkick || AdminLogFilterAlert.this.currentFilter.promote || AdminLogFilterAlert.this.currentFilter.demote || AdminLogFilterAlert.this.currentFilter.info || AdminLogFilterAlert.this.currentFilter.settings || AdminLogFilterAlert.this.currentFilter.pinned || AdminLogFilterAlert.this.currentFilter.edit || AdminLogFilterAlert.this.currentFilter.delete) {
                    AdminLogFilterAlert.this.saveButton.setEnabled(true);
                    AdminLogFilterAlert.this.saveButton.setAlpha(1.0f);
                    return;
                }
                AdminLogFilterAlert.this.saveButton.setEnabled(false);
                AdminLogFilterAlert.this.saveButton.setAlpha(0.5f);
            } else if (view2 instanceof CheckBoxUserCell) {
                CheckBoxUserCell checkBoxUserCell = (CheckBoxUserCell) view2;
                if (AdminLogFilterAlert.this.selectedAdmins == null) {
                    int i4;
                    AdminLogFilterAlert.this.selectedAdmins = new SparseArray();
                    ViewHolder findViewHolderForAdapterPosition2 = AdminLogFilterAlert.this.listView.findViewHolderForAdapterPosition(AdminLogFilterAlert.this.allAdminsRow);
                    if (findViewHolderForAdapterPosition2 != null) {
                        i4 = 0;
                        ((CheckBoxCell) findViewHolderForAdapterPosition2.itemView).setChecked(false, true);
                    } else {
                        i4 = 0;
                    }
                    while (i4 < AdminLogFilterAlert.this.currentAdmins.size()) {
                        User user = MessagesController.getInstance(AdminLogFilterAlert.this.currentAccount).getUser(Integer.valueOf(((ChannelParticipant) AdminLogFilterAlert.this.currentAdmins.get(i4)).user_id));
                        AdminLogFilterAlert.this.selectedAdmins.put(user.id, user);
                        i4++;
                    }
                }
                boolean isChecked2 = checkBoxUserCell.isChecked();
                User currentUser = checkBoxUserCell.getCurrentUser();
                if (isChecked2) {
                    AdminLogFilterAlert.this.selectedAdmins.remove(currentUser.id);
                } else {
                    AdminLogFilterAlert.this.selectedAdmins.put(currentUser.id, currentUser);
                }
                checkBoxUserCell.setChecked(isChecked2 ^ true, true);
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

        public int getItemViewType(int i) {
            if (i >= AdminLogFilterAlert.this.allAdminsRow - 1) {
                if (i != AdminLogFilterAlert.this.allAdminsRow) {
                    if (i == AdminLogFilterAlert.this.allAdminsRow - 1) {
                        return 1;
                    }
                    return 2;
                }
            }
            return 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    i = new CheckBoxCell(this.context, 1);
                    i.setBackgroundDrawable(Theme.getSelectorDrawable(null));
                    break;
                case 1:
                    viewGroup = new ShadowSectionCell(this.context);
                    viewGroup.setSize(18);
                    i = new FrameLayout(this.context);
                    ((FrameLayout) i).addView(viewGroup, LayoutHelper.createFrame(-1, -1.0f));
                    i.setBackgroundColor(Theme.getColor(Theme.key_dialogBackgroundGray));
                    break;
                case 2:
                    i = new CheckBoxUserCell(this.context, true);
                    break;
                default:
                    i = 0;
                    break;
            }
            return new Holder(i);
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
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.kick || !AdminLogFilterAlert.this.currentFilter.ban || !AdminLogFilterAlert.this.currentFilter.unkick || !AdminLogFilterAlert.this.currentFilter.unban) {
                            z = false;
                        }
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.adminsRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.promote || !AdminLogFilterAlert.this.currentFilter.demote) {
                            z = false;
                        }
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.membersRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.invite || !AdminLogFilterAlert.this.currentFilter.join) {
                            z = false;
                        }
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.infoRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.info) {
                            z = false;
                        }
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.deleteRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.delete) {
                            z = false;
                        }
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.editRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.edit) {
                            z = false;
                        }
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.pinnedRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.pinned) {
                            z = false;
                        }
                    }
                    checkBoxCell.setChecked(z, false);
                } else if (adapterPosition == AdminLogFilterAlert.this.leavingRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.leave) {
                            z = false;
                        }
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
                if (AdminLogFilterAlert.this.selectedAdmins != null) {
                    if (AdminLogFilterAlert.this.selectedAdmins.indexOfKey(adapterPosition) < 0) {
                        z = false;
                    }
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
                String str;
                if (i == 0) {
                    i = LocaleController.getString("EventLogFilterAll", C0446R.string.EventLogFilterAll);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter == null) {
                        z = true;
                    }
                    checkBoxCell.setText(i, str, z, true);
                } else if (i == AdminLogFilterAlert.this.restrictionsRow) {
                    i = LocaleController.getString("EventLogFilterNewRestrictions", C0446R.string.EventLogFilterNewRestrictions);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.kick && AdminLogFilterAlert.this.currentFilter.ban && AdminLogFilterAlert.this.currentFilter.unkick && AdminLogFilterAlert.this.currentFilter.unban)) {
                        z = true;
                    }
                    checkBoxCell.setText(i, str, z, true);
                } else if (i == AdminLogFilterAlert.this.adminsRow) {
                    i = LocaleController.getString("EventLogFilterNewAdmins", C0446R.string.EventLogFilterNewAdmins);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.promote && AdminLogFilterAlert.this.currentFilter.demote)) {
                        z = true;
                    }
                    checkBoxCell.setText(i, str, z, true);
                } else if (i == AdminLogFilterAlert.this.membersRow) {
                    i = LocaleController.getString("EventLogFilterNewMembers", C0446R.string.EventLogFilterNewMembers);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.invite && AdminLogFilterAlert.this.currentFilter.join)) {
                        z = true;
                    }
                    checkBoxCell.setText(i, str, z, true);
                } else if (i == AdminLogFilterAlert.this.infoRow) {
                    if (AdminLogFilterAlert.this.isMegagroup != 0) {
                        i = LocaleController.getString("EventLogFilterGroupInfo", C0446R.string.EventLogFilterGroupInfo);
                        str = TtmlNode.ANONYMOUS_REGION_ID;
                        if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.info) {
                            z = true;
                        }
                        checkBoxCell.setText(i, str, z, true);
                        return;
                    }
                    i = LocaleController.getString("EventLogFilterChannelInfo", C0446R.string.EventLogFilterChannelInfo);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.info) {
                        z = true;
                    }
                    checkBoxCell.setText(i, str, z, true);
                } else if (i == AdminLogFilterAlert.this.deleteRow) {
                    i = LocaleController.getString("EventLogFilterDeletedMessages", C0446R.string.EventLogFilterDeletedMessages);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.delete) {
                        z = true;
                    }
                    checkBoxCell.setText(i, str, z, true);
                } else if (i == AdminLogFilterAlert.this.editRow) {
                    i = LocaleController.getString("EventLogFilterEditedMessages", C0446R.string.EventLogFilterEditedMessages);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.edit) {
                        z = true;
                    }
                    checkBoxCell.setText(i, str, z, true);
                } else if (i == AdminLogFilterAlert.this.pinnedRow) {
                    i = LocaleController.getString("EventLogFilterPinnedMessages", C0446R.string.EventLogFilterPinnedMessages);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.pinned) {
                        z = true;
                    }
                    checkBoxCell.setText(i, str, z, true);
                } else if (i == AdminLogFilterAlert.this.leavingRow) {
                    i = LocaleController.getString("EventLogFilterLeavingMembers", C0446R.string.EventLogFilterLeavingMembers);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        if (!AdminLogFilterAlert.this.currentFilter.leave) {
                            z2 = false;
                        }
                    }
                    checkBoxCell.setText(i, str, z2, false);
                } else if (i == AdminLogFilterAlert.this.allAdminsRow) {
                    i = LocaleController.getString("EventLogAllAdmins", C0446R.string.EventLogAllAdmins);
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    if (AdminLogFilterAlert.this.selectedAdmins == null) {
                        z = true;
                    }
                    checkBoxCell.setText(i, str, z, true);
                }
            } else if (itemViewType == 2) {
                boolean z3;
                CheckBoxUserCell checkBoxUserCell = (CheckBoxUserCell) viewHolder.itemView;
                itemViewType = ((ChannelParticipant) AdminLogFilterAlert.this.currentAdmins.get((i - AdminLogFilterAlert.this.allAdminsRow) - 1)).user_id;
                User user = MessagesController.getInstance(AdminLogFilterAlert.this.currentAccount).getUser(Integer.valueOf(itemViewType));
                if (AdminLogFilterAlert.this.selectedAdmins != null) {
                    if (AdminLogFilterAlert.this.selectedAdmins.indexOfKey(itemViewType) < 0) {
                        z3 = false;
                        if (i != getItemCount() - 1) {
                            z = true;
                        }
                        checkBoxUserCell.setUser(user, z3, z);
                    }
                }
                z3 = true;
                if (i != getItemCount() - 1) {
                    z = true;
                }
                checkBoxUserCell.setUser(user, z3, z);
            }
        }
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    public AdminLogFilterAlert(Context context, TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, SparseArray<User> sparseArray, boolean z) {
        super(context, false);
        if (tL_channelAdminLogEventsFilter != null) {
            this.currentFilter = new TL_channelAdminLogEventsFilter();
            this.currentFilter.join = tL_channelAdminLogEventsFilter.join;
            this.currentFilter.leave = tL_channelAdminLogEventsFilter.leave;
            this.currentFilter.invite = tL_channelAdminLogEventsFilter.invite;
            this.currentFilter.ban = tL_channelAdminLogEventsFilter.ban;
            this.currentFilter.unban = tL_channelAdminLogEventsFilter.unban;
            this.currentFilter.kick = tL_channelAdminLogEventsFilter.kick;
            this.currentFilter.unkick = tL_channelAdminLogEventsFilter.unkick;
            this.currentFilter.promote = tL_channelAdminLogEventsFilter.promote;
            this.currentFilter.demote = tL_channelAdminLogEventsFilter.demote;
            this.currentFilter.info = tL_channelAdminLogEventsFilter.info;
            this.currentFilter.settings = tL_channelAdminLogEventsFilter.settings;
            this.currentFilter.pinned = tL_channelAdminLogEventsFilter.pinned;
            this.currentFilter.edit = tL_channelAdminLogEventsFilter.edit;
            this.currentFilter.delete = tL_channelAdminLogEventsFilter.delete;
        }
        if (sparseArray != null) {
            this.selectedAdmins = sparseArray.clone();
        }
        this.isMegagroup = z;
        if (this.isMegagroup != null) {
            this.restrictionsRow = 1;
            tL_channelAdminLogEventsFilter = 2;
        } else {
            this.restrictionsRow = -1;
            tL_channelAdminLogEventsFilter = 1;
        }
        int i = tL_channelAdminLogEventsFilter + 1;
        this.adminsRow = tL_channelAdminLogEventsFilter;
        tL_channelAdminLogEventsFilter = i + 1;
        this.membersRow = i;
        i = tL_channelAdminLogEventsFilter + 1;
        this.infoRow = tL_channelAdminLogEventsFilter;
        tL_channelAdminLogEventsFilter = i + 1;
        this.deleteRow = i;
        i = tL_channelAdminLogEventsFilter + 1;
        this.editRow = tL_channelAdminLogEventsFilter;
        if (this.isMegagroup != null) {
            tL_channelAdminLogEventsFilter = i + 1;
            this.pinnedRow = i;
        } else {
            this.pinnedRow = -1;
            tL_channelAdminLogEventsFilter = i;
        }
        this.leavingRow = tL_channelAdminLogEventsFilter;
        this.allAdminsRow = tL_channelAdminLogEventsFilter + 2;
        this.shadowDrawable = context.getResources().getDrawable(C0446R.drawable.sheet_shadow).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), Mode.MULTIPLY));
        this.containerView = new FrameLayout(context) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || AdminLogFilterAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) AdminLogFilterAlert.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                AdminLogFilterAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return (AdminLogFilterAlert.this.isDismissed() || super.onTouchEvent(motionEvent) == null) ? null : true;
            }

            protected void onMeasure(int i, int i2) {
                i2 = MeasureSpec.getSize(i2);
                if (VERSION.SDK_INT >= 21) {
                    i2 -= AndroidUtilities.statusBarHeight;
                }
                getMeasuredWidth();
                int dp = (AndroidUtilities.dp(48.0f) + ((AdminLogFilterAlert.this.isMegagroup ? 9 : 7) * AndroidUtilities.dp(48.0f))) + AdminLogFilterAlert.backgroundPaddingTop;
                if (AdminLogFilterAlert.this.currentAdmins != null) {
                    dp += ((AdminLogFilterAlert.this.currentAdmins.size() + 1) * AndroidUtilities.dp(48.0f)) + AndroidUtilities.dp(20.0f);
                }
                int i3 = i2 / 5;
                int i4 = ((float) dp) < ((float) i3) * 3.2f ? 0 : i3 * 2;
                if (i4 != 0 && dp < i2) {
                    i4 -= i2 - dp;
                }
                if (i4 == 0) {
                    i4 = AdminLogFilterAlert.backgroundPaddingTop;
                }
                if (AdminLogFilterAlert.this.listView.getPaddingTop() != i4) {
                    AdminLogFilterAlert.this.ignoreLayout = true;
                    AdminLogFilterAlert.this.listView.setPadding(0, i4, 0, 0);
                    AdminLogFilterAlert.this.ignoreLayout = false;
                }
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(Math.min(dp, i2), NUM));
            }

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
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
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                boolean onInterceptTouchEvent = StickerPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, AdminLogFilterAlert.this.listView, 0, null);
                if (super.onInterceptTouchEvent(motionEvent) != null || onInterceptTouchEvent) {
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
        tL_channelAdminLogEventsFilter = this.listView;
        sparseArray = new ListAdapter(context);
        this.adapter = sparseArray;
        tL_channelAdminLogEventsFilter.setAdapter(sparseArray);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setClipToPadding(false);
        this.listView.setEnabled(true);
        this.listView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        this.listView.setOnScrollListener(new C20343());
        this.listView.setOnItemClickListener(new C20354());
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        tL_channelAdminLogEventsFilter = new View(context);
        tL_channelAdminLogEventsFilter.setBackgroundResource(C0446R.drawable.header_shadow_reverse);
        this.containerView.addView(tL_channelAdminLogEventsFilter, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        this.saveButton = new BottomSheetCell(context, 1);
        this.saveButton.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.saveButton.setTextAndIcon(LocaleController.getString("Save", C0446R.string.Save).toUpperCase(), 0);
        this.saveButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        this.saveButton.setOnClickListener(new C10705());
        this.containerView.addView(this.saveButton, LayoutHelper.createFrame(-1, 48, 83));
        this.adapter.notifyDataSetChanged();
    }

    public void setCurrentAdmins(ArrayList<ChannelParticipant> arrayList) {
        this.currentAdmins = arrayList;
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
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
