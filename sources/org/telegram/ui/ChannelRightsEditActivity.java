package org.telegram.ui;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TimePicker;
import java.util.Calendar;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class ChannelRightsEditActivity extends BaseFragment {
    private static final int done_button = 1;
    private int addAdminsRow;
    private int addUsersRow;
    private TL_channelAdminRights adminRights;
    private int banUsersRow;
    private TL_channelBannedRights bannedRights;
    private boolean canEdit;
    private int cantEditInfoRow;
    private int changeInfoRow;
    private int chatId;
    private int currentType;
    private User currentUser;
    private ChannelRightsEditActivityDelegate delegate;
    private int deleteMessagesRow;
    private int editMesagesRow;
    private int embedLinksRow;
    private boolean isDemocracy;
    private boolean isMegagroup;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private TL_channelAdminRights myAdminRights;
    private int pinMessagesRow;
    private int postMessagesRow;
    private int removeAdminRow;
    private int removeAdminShadowRow;
    private int rightsShadowRow;
    private int rowCount;
    private int sendMediaRow;
    private int sendMessagesRow;
    private int sendStickersRow;
    private int untilDateRow;
    private int viewMessagesRow;

    public interface ChannelRightsEditActivityDelegate {
        void didSetRights(int i, TL_channelAdminRights tL_channelAdminRights, TL_channelBannedRights tL_channelBannedRights);
    }

    /* renamed from: org.telegram.ui.ChannelRightsEditActivity$1 */
    class C19871 extends ActionBarMenuOnItemClick {
        C19871() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChannelRightsEditActivity.this.finishFragment();
                return;
            }
            int i = 1;
            if (id == 1) {
                if (ChannelRightsEditActivity.this.currentType == 0) {
                    TL_channelAdminRights access$200;
                    if (ChannelRightsEditActivity.this.isMegagroup) {
                        access$200 = ChannelRightsEditActivity.this.adminRights;
                        ChannelRightsEditActivity.this.adminRights.edit_messages = false;
                        access$200.post_messages = false;
                    } else {
                        access$200 = ChannelRightsEditActivity.this.adminRights;
                        ChannelRightsEditActivity.this.adminRights.ban_users = false;
                        access$200.pin_messages = false;
                    }
                    MessagesController.getInstance(ChannelRightsEditActivity.this.currentAccount).setUserAdminRole(ChannelRightsEditActivity.this.chatId, ChannelRightsEditActivity.this.currentUser, ChannelRightsEditActivity.this.adminRights, ChannelRightsEditActivity.this.isMegagroup, ChannelRightsEditActivity.this.getFragmentForAlert(1));
                    if (ChannelRightsEditActivity.this.delegate != null) {
                        ChannelRightsEditActivityDelegate access$600 = ChannelRightsEditActivity.this.delegate;
                        if (!(ChannelRightsEditActivity.this.adminRights.change_info || ChannelRightsEditActivity.this.adminRights.post_messages || ChannelRightsEditActivity.this.adminRights.edit_messages || ChannelRightsEditActivity.this.adminRights.delete_messages || ChannelRightsEditActivity.this.adminRights.ban_users || ChannelRightsEditActivity.this.adminRights.invite_users || ChannelRightsEditActivity.this.adminRights.invite_link || ChannelRightsEditActivity.this.adminRights.pin_messages)) {
                            if (!ChannelRightsEditActivity.this.adminRights.add_admins) {
                                i = 0;
                            }
                        }
                        access$600.didSetRights(i, ChannelRightsEditActivity.this.adminRights, ChannelRightsEditActivity.this.bannedRights);
                    }
                } else if (ChannelRightsEditActivity.this.currentType == 1) {
                    MessagesController.getInstance(ChannelRightsEditActivity.this.currentAccount).setUserBannedRole(ChannelRightsEditActivity.this.chatId, ChannelRightsEditActivity.this.currentUser, ChannelRightsEditActivity.this.bannedRights, ChannelRightsEditActivity.this.isMegagroup, ChannelRightsEditActivity.this.getFragmentForAlert(1));
                    if (ChannelRightsEditActivity.this.bannedRights.view_messages) {
                        i = 0;
                    } else {
                        if (!(ChannelRightsEditActivity.this.bannedRights.send_messages || ChannelRightsEditActivity.this.bannedRights.send_stickers || ChannelRightsEditActivity.this.bannedRights.embed_links || ChannelRightsEditActivity.this.bannedRights.send_media || ChannelRightsEditActivity.this.bannedRights.send_gifs || ChannelRightsEditActivity.this.bannedRights.send_games)) {
                            if (!ChannelRightsEditActivity.this.bannedRights.send_inline) {
                                ChannelRightsEditActivity.this.bannedRights.until_date = 0;
                                i = 2;
                                if (ChannelRightsEditActivity.this.delegate != null) {
                                    ChannelRightsEditActivity.this.delegate.didSetRights(i, ChannelRightsEditActivity.this.adminRights, ChannelRightsEditActivity.this.bannedRights);
                                }
                            }
                        }
                        i = 1;
                    }
                    if (ChannelRightsEditActivity.this.delegate != null) {
                        ChannelRightsEditActivity.this.delegate.didSetRights(i, ChannelRightsEditActivity.this.adminRights, ChannelRightsEditActivity.this.bannedRights);
                    }
                }
                ChannelRightsEditActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelRightsEditActivity$3 */
    class C19883 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.ChannelRightsEditActivity$3$1 */
        class C09981 implements OnDateSetListener {

            /* renamed from: org.telegram.ui.ChannelRightsEditActivity$3$1$2 */
            class C09972 implements OnClickListener {
                C09972() {
                }

                public void onClick(DialogInterface dialog, int which) {
                }
            }

            C09981() {
            }

            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.set(year, month, dayOfMonth);
                final int time = (int) (calendar.getTime().getTime() / 1000);
                try {
                    TimePickerDialog dialog = new TimePickerDialog(ChannelRightsEditActivity.this.getParentActivity(), new OnTimeSetListener() {
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            ChannelRightsEditActivity.this.bannedRights.until_date = (time + (hourOfDay * 3600)) + (minute * 60);
                            ChannelRightsEditActivity.this.listViewAdapter.notifyItemChanged(ChannelRightsEditActivity.this.untilDateRow);
                        }
                    }, 0, 0, true);
                    dialog.setButton(-1, LocaleController.getString("Set", R.string.Set), dialog);
                    dialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new C09972());
                    ChannelRightsEditActivity.this.showDialog(dialog);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }

        /* renamed from: org.telegram.ui.ChannelRightsEditActivity$3$2 */
        class C09992 implements OnClickListener {
            C09992() {
            }

            public void onClick(DialogInterface dialog, int which) {
                ChannelRightsEditActivity.this.bannedRights.until_date = 0;
                ChannelRightsEditActivity.this.listViewAdapter.notifyItemChanged(ChannelRightsEditActivity.this.untilDateRow);
            }
        }

        /* renamed from: org.telegram.ui.ChannelRightsEditActivity$3$3 */
        class C10003 implements OnClickListener {
            C10003() {
            }

            public void onClick(DialogInterface dialog, int which) {
            }
        }

        C19883() {
        }

        public void onItemClick(View view, int position) {
            Throwable e;
            View view2 = view;
            int i = position;
            if (ChannelRightsEditActivity.this.canEdit) {
                if (i == 0) {
                    Bundle args = new Bundle();
                    args.putInt("user_id", ChannelRightsEditActivity.this.currentUser.id);
                    ChannelRightsEditActivity.this.presentFragment(new ProfileActivity(args));
                } else if (i == ChannelRightsEditActivity.this.removeAdminRow) {
                    if (ChannelRightsEditActivity.this.currentType == 0) {
                        MessagesController.getInstance(ChannelRightsEditActivity.this.currentAccount).setUserAdminRole(ChannelRightsEditActivity.this.chatId, ChannelRightsEditActivity.this.currentUser, new TL_channelAdminRights(), ChannelRightsEditActivity.this.isMegagroup, ChannelRightsEditActivity.this.getFragmentForAlert(0));
                    } else if (ChannelRightsEditActivity.this.currentType == 1) {
                        ChannelRightsEditActivity.this.bannedRights = new TL_channelBannedRights();
                        ChannelRightsEditActivity.this.bannedRights.view_messages = true;
                        ChannelRightsEditActivity.this.bannedRights.send_media = true;
                        ChannelRightsEditActivity.this.bannedRights.send_messages = true;
                        ChannelRightsEditActivity.this.bannedRights.send_stickers = true;
                        ChannelRightsEditActivity.this.bannedRights.send_gifs = true;
                        ChannelRightsEditActivity.this.bannedRights.send_games = true;
                        ChannelRightsEditActivity.this.bannedRights.send_inline = true;
                        ChannelRightsEditActivity.this.bannedRights.embed_links = true;
                        ChannelRightsEditActivity.this.bannedRights.until_date = 0;
                        MessagesController.getInstance(ChannelRightsEditActivity.this.currentAccount).setUserBannedRole(ChannelRightsEditActivity.this.chatId, ChannelRightsEditActivity.this.currentUser, ChannelRightsEditActivity.this.bannedRights, ChannelRightsEditActivity.this.isMegagroup, ChannelRightsEditActivity.this.getFragmentForAlert(0));
                    }
                    if (ChannelRightsEditActivity.this.delegate != null) {
                        ChannelRightsEditActivity.this.delegate.didSetRights(0, ChannelRightsEditActivity.this.adminRights, ChannelRightsEditActivity.this.bannedRights);
                    }
                    ChannelRightsEditActivity.this.finishFragment();
                } else if (i == ChannelRightsEditActivity.this.untilDateRow) {
                    if (ChannelRightsEditActivity.this.getParentActivity() != null) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(1);
                        int monthOfYear = calendar.get(2);
                        int dayOfMonth = calendar.get(5);
                        try {
                            DatePickerDialog dialog = new DatePickerDialog(ChannelRightsEditActivity.this.getParentActivity(), new C09981(), year, monthOfYear, dayOfMonth);
                            final DatePicker datePicker = dialog.getDatePicker();
                            Calendar date = Calendar.getInstance();
                            date.setTimeInMillis(System.currentTimeMillis());
                            date.set(11, date.getMinimum(11));
                            date.set(12, date.getMinimum(12));
                            date.set(13, date.getMinimum(13));
                            date.set(14, date.getMinimum(14));
                            datePicker.setMinDate(date.getTimeInMillis());
                            try {
                                date.setTimeInMillis(System.currentTimeMillis() + 31536000000L);
                                date.set(11, date.getMaximum(11));
                                date.set(12, date.getMaximum(12));
                                date.set(13, date.getMaximum(13));
                                date.set(14, date.getMaximum(14));
                                datePicker.setMaxDate(date.getTimeInMillis());
                                dialog.setButton(-1, LocaleController.getString("Set", R.string.Set), dialog);
                                dialog.setButton(-3, LocaleController.getString("UserRestrictionsUntilForever", R.string.UserRestrictionsUntilForever), new C09992());
                                dialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new C10003());
                                if (VERSION.SDK_INT >= 21) {
                                    dialog.setOnShowListener(new OnShowListener() {
                                        public void onShow(DialogInterface dialog) {
                                            int count = datePicker.getChildCount();
                                            for (int a = 0; a < count; a++) {
                                                View child = datePicker.getChildAt(a);
                                                LayoutParams layoutParams = child.getLayoutParams();
                                                layoutParams.width = -1;
                                                child.setLayoutParams(layoutParams);
                                            }
                                        }
                                    });
                                }
                                ChannelRightsEditActivity.this.showDialog(dialog);
                            } catch (Throwable e2) {
                                e = e2;
                                FileLog.m3e(e);
                            }
                        } catch (Throwable e22) {
                            int i2 = dayOfMonth;
                            e = e22;
                            FileLog.m3e(e);
                        }
                    }
                } else if (view2 instanceof TextCheckCell2) {
                    TextCheckCell2 checkCell = (TextCheckCell2) view2;
                    if (checkCell.isEnabled()) {
                        checkCell.setChecked(checkCell.isChecked() ^ true);
                        if (i == ChannelRightsEditActivity.this.changeInfoRow) {
                            ChannelRightsEditActivity.this.adminRights.change_info = true ^ ChannelRightsEditActivity.this.adminRights.change_info;
                        } else if (i == ChannelRightsEditActivity.this.postMessagesRow) {
                            ChannelRightsEditActivity.this.adminRights.post_messages = true ^ ChannelRightsEditActivity.this.adminRights.post_messages;
                        } else if (i == ChannelRightsEditActivity.this.editMesagesRow) {
                            ChannelRightsEditActivity.this.adminRights.edit_messages = true ^ ChannelRightsEditActivity.this.adminRights.edit_messages;
                        } else if (i == ChannelRightsEditActivity.this.deleteMessagesRow) {
                            ChannelRightsEditActivity.this.adminRights.delete_messages = true ^ ChannelRightsEditActivity.this.adminRights.delete_messages;
                        } else if (i == ChannelRightsEditActivity.this.addAdminsRow) {
                            ChannelRightsEditActivity.this.adminRights.add_admins = true ^ ChannelRightsEditActivity.this.adminRights.add_admins;
                        } else if (i == ChannelRightsEditActivity.this.banUsersRow) {
                            ChannelRightsEditActivity.this.adminRights.ban_users = true ^ ChannelRightsEditActivity.this.adminRights.ban_users;
                        } else if (i == ChannelRightsEditActivity.this.addUsersRow) {
                            TL_channelAdminRights access$200 = ChannelRightsEditActivity.this.adminRights;
                            boolean z = true ^ ChannelRightsEditActivity.this.adminRights.invite_users;
                            ChannelRightsEditActivity.this.adminRights.invite_link = z;
                            access$200.invite_users = z;
                        } else if (i == ChannelRightsEditActivity.this.pinMessagesRow) {
                            ChannelRightsEditActivity.this.adminRights.pin_messages = true ^ ChannelRightsEditActivity.this.adminRights.pin_messages;
                        } else if (ChannelRightsEditActivity.this.bannedRights != null) {
                            TL_channelBannedRights access$700;
                            TL_channelBannedRights access$7002;
                            TL_channelBannedRights access$7003;
                            boolean disabled = checkCell.isChecked() ^ true;
                            if (i == ChannelRightsEditActivity.this.viewMessagesRow) {
                                ChannelRightsEditActivity.this.bannedRights.view_messages ^= true;
                            } else if (i == ChannelRightsEditActivity.this.sendMessagesRow) {
                                ChannelRightsEditActivity.this.bannedRights.send_messages ^= true;
                            } else if (i == ChannelRightsEditActivity.this.sendMediaRow) {
                                ChannelRightsEditActivity.this.bannedRights.send_media ^= true;
                            } else if (i == ChannelRightsEditActivity.this.sendStickersRow) {
                                access$700 = ChannelRightsEditActivity.this.bannedRights;
                                access$7002 = ChannelRightsEditActivity.this.bannedRights;
                                access$7003 = ChannelRightsEditActivity.this.bannedRights;
                                boolean z2 = ChannelRightsEditActivity.this.bannedRights.send_stickers ^ true;
                                ChannelRightsEditActivity.this.bannedRights.send_inline = z2;
                                access$7003.send_gifs = z2;
                                access$7002.send_games = z2;
                                access$700.send_stickers = z2;
                            } else if (i == ChannelRightsEditActivity.this.embedLinksRow) {
                                ChannelRightsEditActivity.this.bannedRights.embed_links ^= true;
                            }
                            ViewHolder holder;
                            if (disabled) {
                                if (ChannelRightsEditActivity.this.bannedRights.view_messages && !ChannelRightsEditActivity.this.bannedRights.send_messages) {
                                    ChannelRightsEditActivity.this.bannedRights.send_messages = true;
                                    holder = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.sendMessagesRow);
                                    if (holder != null) {
                                        ((TextCheckCell2) holder.itemView).setChecked(false);
                                    }
                                }
                                if ((ChannelRightsEditActivity.this.bannedRights.view_messages || ChannelRightsEditActivity.this.bannedRights.send_messages) && !ChannelRightsEditActivity.this.bannedRights.send_media) {
                                    ChannelRightsEditActivity.this.bannedRights.send_media = true;
                                    holder = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.sendMediaRow);
                                    if (holder != null) {
                                        ((TextCheckCell2) holder.itemView).setChecked(false);
                                    }
                                }
                                if ((ChannelRightsEditActivity.this.bannedRights.view_messages || ChannelRightsEditActivity.this.bannedRights.send_messages || ChannelRightsEditActivity.this.bannedRights.send_media) && !ChannelRightsEditActivity.this.bannedRights.send_stickers) {
                                    access$700 = ChannelRightsEditActivity.this.bannedRights;
                                    access$7002 = ChannelRightsEditActivity.this.bannedRights;
                                    access$7003 = ChannelRightsEditActivity.this.bannedRights;
                                    ChannelRightsEditActivity.this.bannedRights.send_inline = true;
                                    access$7003.send_gifs = true;
                                    access$7002.send_games = true;
                                    access$700.send_stickers = true;
                                    holder = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.sendStickersRow);
                                    if (holder != null) {
                                        ((TextCheckCell2) holder.itemView).setChecked(false);
                                    }
                                }
                                if ((ChannelRightsEditActivity.this.bannedRights.view_messages || ChannelRightsEditActivity.this.bannedRights.send_messages || ChannelRightsEditActivity.this.bannedRights.send_media) && !ChannelRightsEditActivity.this.bannedRights.embed_links) {
                                    ChannelRightsEditActivity.this.bannedRights.embed_links = true;
                                    ViewHolder holder2 = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.embedLinksRow);
                                    if (holder2 != null) {
                                        ((TextCheckCell2) holder2.itemView).setChecked(false);
                                    }
                                }
                            } else {
                                if (!(ChannelRightsEditActivity.this.bannedRights.send_messages && ChannelRightsEditActivity.this.bannedRights.embed_links && ChannelRightsEditActivity.this.bannedRights.send_inline && ChannelRightsEditActivity.this.bannedRights.send_media) && ChannelRightsEditActivity.this.bannedRights.view_messages) {
                                    ChannelRightsEditActivity.this.bannedRights.view_messages = false;
                                    holder = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.viewMessagesRow);
                                    if (holder != null) {
                                        ((TextCheckCell2) holder.itemView).setChecked(true);
                                    }
                                }
                                if (!(ChannelRightsEditActivity.this.bannedRights.embed_links && ChannelRightsEditActivity.this.bannedRights.send_inline && ChannelRightsEditActivity.this.bannedRights.send_media) && ChannelRightsEditActivity.this.bannedRights.send_messages) {
                                    ChannelRightsEditActivity.this.bannedRights.send_messages = false;
                                    holder = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.sendMessagesRow);
                                    if (holder != null) {
                                        ((TextCheckCell2) holder.itemView).setChecked(true);
                                    }
                                }
                                if (!(ChannelRightsEditActivity.this.bannedRights.send_inline && ChannelRightsEditActivity.this.bannedRights.embed_links) && ChannelRightsEditActivity.this.bannedRights.send_media) {
                                    ChannelRightsEditActivity.this.bannedRights.send_media = false;
                                    ViewHolder holder3 = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.sendMediaRow);
                                    if (holder3 != null) {
                                        ((TextCheckCell2) holder3.itemView).setChecked(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelRightsEditActivity$4 */
    class C19894 implements ThemeDescriptionDelegate {
        C19894() {
        }

        public void didSetColor() {
            if (ChannelRightsEditActivity.this.listView != null) {
                int count = ChannelRightsEditActivity.this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = ChannelRightsEditActivity.this.listView.getChildAt(a);
                    if (child instanceof UserCell) {
                        ((UserCell) child).update(0);
                    }
                }
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            boolean z = false;
            if (!ChannelRightsEditActivity.this.canEdit) {
                return false;
            }
            int type = holder.getItemViewType();
            if (ChannelRightsEditActivity.this.currentType == 0 && type == 4) {
                int position = holder.getAdapterPosition();
                if (position == ChannelRightsEditActivity.this.changeInfoRow) {
                    return ChannelRightsEditActivity.this.myAdminRights.change_info;
                }
                if (position == ChannelRightsEditActivity.this.postMessagesRow) {
                    return ChannelRightsEditActivity.this.myAdminRights.post_messages;
                }
                if (position == ChannelRightsEditActivity.this.editMesagesRow) {
                    return ChannelRightsEditActivity.this.myAdminRights.edit_messages;
                }
                if (position == ChannelRightsEditActivity.this.deleteMessagesRow) {
                    return ChannelRightsEditActivity.this.myAdminRights.delete_messages;
                }
                if (position == ChannelRightsEditActivity.this.addAdminsRow) {
                    return ChannelRightsEditActivity.this.myAdminRights.add_admins;
                }
                if (position == ChannelRightsEditActivity.this.banUsersRow) {
                    return ChannelRightsEditActivity.this.myAdminRights.ban_users;
                }
                if (position == ChannelRightsEditActivity.this.addUsersRow) {
                    return ChannelRightsEditActivity.this.myAdminRights.invite_users;
                }
                if (position == ChannelRightsEditActivity.this.pinMessagesRow) {
                    return ChannelRightsEditActivity.this.myAdminRights.pin_messages;
                }
            }
            if (!(type == 3 || type == 1 || type == 5)) {
                z = true;
            }
            return z;
        }

        public int getItemCount() {
            return ChannelRightsEditActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new UserCell(this.mContext, 1, 0, false);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 2:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new TextCheckCell2(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new ShadowSectionCell(this.mContext);
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    holder.itemView.setData(ChannelRightsEditActivity.this.currentUser, null, null, 0);
                    return;
                case 1:
                    TextInfoPrivacyCell privacyCell = holder.itemView;
                    if (position == ChannelRightsEditActivity.this.cantEditInfoRow) {
                        privacyCell.setText(LocaleController.getString("EditAdminCantEdit", R.string.EditAdminCantEdit));
                        return;
                    }
                    return;
                case 2:
                    TextSettingsCell actionCell = holder.itemView;
                    if (position == ChannelRightsEditActivity.this.removeAdminRow) {
                        actionCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
                        actionCell.setTag(Theme.key_windowBackgroundWhiteRedText3);
                        if (ChannelRightsEditActivity.this.currentType == 0) {
                            actionCell.setText(LocaleController.getString("EditAdminRemoveAdmin", R.string.EditAdminRemoveAdmin), false);
                            return;
                        } else if (ChannelRightsEditActivity.this.currentType == 1) {
                            actionCell.setText(LocaleController.getString("UserRestrictionsBlock", R.string.UserRestrictionsBlock), false);
                            return;
                        } else {
                            return;
                        }
                    } else if (position == ChannelRightsEditActivity.this.untilDateRow) {
                        String value;
                        actionCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        actionCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                        if (ChannelRightsEditActivity.this.bannedRights.until_date != 0) {
                            if (Math.abs(((long) ChannelRightsEditActivity.this.bannedRights.until_date) - (System.currentTimeMillis() / 1000)) <= 315360000) {
                                value = LocaleController.formatDateForBan((long) ChannelRightsEditActivity.this.bannedRights.until_date);
                                actionCell.setTextAndValue(LocaleController.getString("UserRestrictionsUntil", R.string.UserRestrictionsUntil), value, false);
                                return;
                            }
                        }
                        value = LocaleController.getString("UserRestrictionsUntilForever", R.string.UserRestrictionsUntilForever);
                        actionCell.setTextAndValue(LocaleController.getString("UserRestrictionsUntil", R.string.UserRestrictionsUntil), value, false);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    HeaderCell headerCell = holder.itemView;
                    if (ChannelRightsEditActivity.this.currentType == 0) {
                        headerCell.setText(LocaleController.getString("EditAdminWhatCanDo", R.string.EditAdminWhatCanDo));
                        return;
                    } else if (ChannelRightsEditActivity.this.currentType == 1) {
                        headerCell.setText(LocaleController.getString("UserRestrictionsCanDo", R.string.UserRestrictionsCanDo));
                        return;
                    } else {
                        return;
                    }
                case 4:
                    TextCheckCell2 checkCell = holder.itemView;
                    if (position == ChannelRightsEditActivity.this.changeInfoRow) {
                        if (ChannelRightsEditActivity.this.isMegagroup) {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminChangeGroupInfo", R.string.EditAdminChangeGroupInfo), ChannelRightsEditActivity.this.adminRights.change_info, true);
                        } else {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminChangeChannelInfo", R.string.EditAdminChangeChannelInfo), ChannelRightsEditActivity.this.adminRights.change_info, true);
                        }
                    } else if (position == ChannelRightsEditActivity.this.postMessagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminPostMessages", R.string.EditAdminPostMessages), ChannelRightsEditActivity.this.adminRights.post_messages, true);
                    } else if (position == ChannelRightsEditActivity.this.editMesagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminEditMessages", R.string.EditAdminEditMessages), ChannelRightsEditActivity.this.adminRights.edit_messages, true);
                    } else if (position == ChannelRightsEditActivity.this.deleteMessagesRow) {
                        if (ChannelRightsEditActivity.this.isMegagroup) {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminGroupDeleteMessages", R.string.EditAdminGroupDeleteMessages), ChannelRightsEditActivity.this.adminRights.delete_messages, true);
                        } else {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminDeleteMessages", R.string.EditAdminDeleteMessages), ChannelRightsEditActivity.this.adminRights.delete_messages, true);
                        }
                    } else if (position == ChannelRightsEditActivity.this.addAdminsRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddAdmins", R.string.EditAdminAddAdmins), ChannelRightsEditActivity.this.adminRights.add_admins, false);
                    } else if (position == ChannelRightsEditActivity.this.banUsersRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminBanUsers", R.string.EditAdminBanUsers), ChannelRightsEditActivity.this.adminRights.ban_users, true);
                    } else if (position == ChannelRightsEditActivity.this.addUsersRow) {
                        if (ChannelRightsEditActivity.this.isDemocracy) {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddUsersViaLink", R.string.EditAdminAddUsersViaLink), ChannelRightsEditActivity.this.adminRights.invite_users, true);
                        } else {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddUsers", R.string.EditAdminAddUsers), ChannelRightsEditActivity.this.adminRights.invite_users, true);
                        }
                    } else if (position == ChannelRightsEditActivity.this.pinMessagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminPinMessages", R.string.EditAdminPinMessages), ChannelRightsEditActivity.this.adminRights.pin_messages, true);
                    } else if (position == ChannelRightsEditActivity.this.viewMessagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsRead", R.string.UserRestrictionsRead), ChannelRightsEditActivity.this.bannedRights.view_messages ^ true, true);
                    } else if (position == ChannelRightsEditActivity.this.sendMessagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSend", R.string.UserRestrictionsSend), ChannelRightsEditActivity.this.bannedRights.send_messages ^ true, true);
                    } else if (position == ChannelRightsEditActivity.this.sendMediaRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSendMedia", R.string.UserRestrictionsSendMedia), ChannelRightsEditActivity.this.bannedRights.send_media ^ true, true);
                    } else if (position == ChannelRightsEditActivity.this.sendStickersRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSendStickers", R.string.UserRestrictionsSendStickers), ChannelRightsEditActivity.this.bannedRights.send_stickers ^ true, true);
                    } else if (position == ChannelRightsEditActivity.this.embedLinksRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsEmbedLinks", R.string.UserRestrictionsEmbedLinks), ChannelRightsEditActivity.this.bannedRights.embed_links ^ true, true);
                    }
                    if (!(position == ChannelRightsEditActivity.this.sendMediaRow || position == ChannelRightsEditActivity.this.sendStickersRow)) {
                        if (position != ChannelRightsEditActivity.this.embedLinksRow) {
                            if (position == ChannelRightsEditActivity.this.sendMessagesRow) {
                                checkCell.setEnabled(ChannelRightsEditActivity.this.bannedRights.view_messages ^ true);
                                return;
                            }
                            return;
                        }
                    }
                    if (!(ChannelRightsEditActivity.this.bannedRights.send_messages || ChannelRightsEditActivity.this.bannedRights.view_messages)) {
                        z = true;
                    }
                    checkCell.setEnabled(z);
                    return;
                case 5:
                    ShadowSectionCell shadowCell = holder.itemView;
                    int access$3300 = ChannelRightsEditActivity.this.rightsShadowRow;
                    int i = R.drawable.greydivider;
                    if (position == access$3300) {
                        Context context = this.mContext;
                        if (ChannelRightsEditActivity.this.removeAdminRow == -1) {
                            i = R.drawable.greydivider_bottom;
                        }
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(context, i, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == ChannelRightsEditActivity.this.removeAdminShadowRow) {
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            }
            if (!(position == 1 || position == ChannelRightsEditActivity.this.rightsShadowRow)) {
                if (position != ChannelRightsEditActivity.this.removeAdminShadowRow) {
                    if (position == 2) {
                        return 3;
                    }
                    if (!(position == ChannelRightsEditActivity.this.changeInfoRow || position == ChannelRightsEditActivity.this.postMessagesRow || position == ChannelRightsEditActivity.this.editMesagesRow || position == ChannelRightsEditActivity.this.deleteMessagesRow || position == ChannelRightsEditActivity.this.addAdminsRow || position == ChannelRightsEditActivity.this.banUsersRow || position == ChannelRightsEditActivity.this.addUsersRow || position == ChannelRightsEditActivity.this.pinMessagesRow || position == ChannelRightsEditActivity.this.viewMessagesRow || position == ChannelRightsEditActivity.this.sendMessagesRow || position == ChannelRightsEditActivity.this.sendMediaRow || position == ChannelRightsEditActivity.this.sendStickersRow)) {
                        if (position != ChannelRightsEditActivity.this.embedLinksRow) {
                            if (position == ChannelRightsEditActivity.this.cantEditInfoRow) {
                                return 1;
                            }
                            return 2;
                        }
                    }
                    return 4;
                }
            }
            return 5;
        }
    }

    public ChannelRightsEditActivity(int userId, int channelId, TL_channelAdminRights rightsAdmin, TL_channelBannedRights rightsBanned, int type, boolean edit) {
        int i;
        TL_channelAdminRights tL_channelAdminRights = rightsAdmin;
        TL_channelBannedRights tL_channelBannedRights = rightsBanned;
        int i2 = type;
        this.chatId = channelId;
        this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(userId));
        this.currentType = i2;
        this.canEdit = edit;
        Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        if (chat != null) {
            r0.isMegagroup = chat.megagroup;
            r0.myAdminRights = chat.admin_rights;
        }
        if (r0.myAdminRights == null) {
            r0.myAdminRights = new TL_channelAdminRights();
            TL_channelAdminRights tL_channelAdminRights2 = r0.myAdminRights;
            TL_channelAdminRights tL_channelAdminRights3 = r0.myAdminRights;
            TL_channelAdminRights tL_channelAdminRights4 = r0.myAdminRights;
            TL_channelAdminRights tL_channelAdminRights5 = r0.myAdminRights;
            TL_channelAdminRights tL_channelAdminRights6 = r0.myAdminRights;
            TL_channelAdminRights tL_channelAdminRights7 = r0.myAdminRights;
            TL_channelAdminRights tL_channelAdminRights8 = r0.myAdminRights;
            TL_channelAdminRights tL_channelAdminRights9 = r0.myAdminRights;
            r0.myAdminRights.add_admins = true;
            tL_channelAdminRights9.pin_messages = true;
            tL_channelAdminRights8.invite_link = true;
            tL_channelAdminRights7.invite_users = true;
            tL_channelAdminRights6.ban_users = true;
            tL_channelAdminRights5.delete_messages = true;
            tL_channelAdminRights4.edit_messages = true;
            tL_channelAdminRights3.post_messages = true;
            tL_channelAdminRights2.change_info = true;
        }
        boolean initialIsSet = false;
        if (i2 == 0) {
            r0.adminRights = new TL_channelAdminRights();
            if (tL_channelAdminRights == null) {
                r0.adminRights.change_info = r0.myAdminRights.change_info;
                r0.adminRights.post_messages = r0.myAdminRights.post_messages;
                r0.adminRights.edit_messages = r0.myAdminRights.edit_messages;
                r0.adminRights.delete_messages = r0.myAdminRights.delete_messages;
                r0.adminRights.ban_users = r0.myAdminRights.ban_users;
                r0.adminRights.invite_users = r0.myAdminRights.invite_users;
                r0.adminRights.invite_link = r0.myAdminRights.invite_link;
                r0.adminRights.pin_messages = r0.myAdminRights.pin_messages;
                initialIsSet = false;
            } else {
                r0.adminRights.change_info = tL_channelAdminRights.change_info;
                r0.adminRights.post_messages = tL_channelAdminRights.post_messages;
                r0.adminRights.edit_messages = tL_channelAdminRights.edit_messages;
                r0.adminRights.delete_messages = tL_channelAdminRights.delete_messages;
                r0.adminRights.ban_users = tL_channelAdminRights.ban_users;
                r0.adminRights.invite_users = tL_channelAdminRights.invite_users;
                r0.adminRights.invite_link = tL_channelAdminRights.invite_link;
                r0.adminRights.pin_messages = tL_channelAdminRights.pin_messages;
                r0.adminRights.add_admins = tL_channelAdminRights.add_admins;
                if (!(r0.adminRights.change_info || r0.adminRights.post_messages || r0.adminRights.edit_messages || r0.adminRights.delete_messages || r0.adminRights.ban_users || r0.adminRights.invite_users || r0.adminRights.invite_link || r0.adminRights.pin_messages)) {
                    if (r0.adminRights.add_admins) {
                    }
                }
                initialIsSet = true;
            }
        } else {
            r0.bannedRights = new TL_channelBannedRights();
            if (tL_channelBannedRights == null) {
                TL_channelBannedRights tL_channelBannedRights2 = r0.bannedRights;
                TL_channelBannedRights tL_channelBannedRights3 = r0.bannedRights;
                TL_channelBannedRights tL_channelBannedRights4 = r0.bannedRights;
                TL_channelBannedRights tL_channelBannedRights5 = r0.bannedRights;
                TL_channelBannedRights tL_channelBannedRights6 = r0.bannedRights;
                TL_channelBannedRights tL_channelBannedRights7 = r0.bannedRights;
                TL_channelBannedRights tL_channelBannedRights8 = r0.bannedRights;
                r0.bannedRights.send_inline = true;
                tL_channelBannedRights8.send_games = true;
                tL_channelBannedRights7.send_gifs = true;
                tL_channelBannedRights6.send_stickers = true;
                tL_channelBannedRights5.embed_links = true;
                tL_channelBannedRights4.send_messages = true;
                tL_channelBannedRights3.send_media = true;
                tL_channelBannedRights2.view_messages = true;
            } else {
                r0.bannedRights.view_messages = tL_channelBannedRights.view_messages;
                r0.bannedRights.send_messages = tL_channelBannedRights.send_messages;
                r0.bannedRights.send_media = tL_channelBannedRights.send_media;
                r0.bannedRights.send_stickers = tL_channelBannedRights.send_stickers;
                r0.bannedRights.send_gifs = tL_channelBannedRights.send_gifs;
                r0.bannedRights.send_games = tL_channelBannedRights.send_games;
                r0.bannedRights.send_inline = tL_channelBannedRights.send_inline;
                r0.bannedRights.embed_links = tL_channelBannedRights.embed_links;
                r0.bannedRights.until_date = tL_channelBannedRights.until_date;
            }
            if (tL_channelBannedRights != null) {
                if (tL_channelBannedRights.view_messages) {
                }
            }
            initialIsSet = true;
        }
        r0.rowCount += 3;
        if (i2 == 0) {
            if (r0.isMegagroup) {
                i = r0.rowCount;
                r0.rowCount = i + 1;
                r0.changeInfoRow = i;
                i = r0.rowCount;
                r0.rowCount = i + 1;
                r0.deleteMessagesRow = i;
                i = r0.rowCount;
                r0.rowCount = i + 1;
                r0.banUsersRow = i;
                i = r0.rowCount;
                r0.rowCount = i + 1;
                r0.addUsersRow = i;
                i = r0.rowCount;
                r0.rowCount = i + 1;
                r0.pinMessagesRow = i;
                i = r0.rowCount;
                r0.rowCount = i + 1;
                r0.addAdminsRow = i;
                r0.isDemocracy = chat.democracy;
            } else {
                i = r0.rowCount;
                r0.rowCount = i + 1;
                r0.changeInfoRow = i;
                i = r0.rowCount;
                r0.rowCount = i + 1;
                r0.postMessagesRow = i;
                i = r0.rowCount;
                r0.rowCount = i + 1;
                r0.editMesagesRow = i;
                i = r0.rowCount;
                r0.rowCount = i + 1;
                r0.deleteMessagesRow = i;
                i = r0.rowCount;
                r0.rowCount = i + 1;
                r0.addUsersRow = i;
                i = r0.rowCount;
                r0.rowCount = i + 1;
                r0.addAdminsRow = i;
            }
        } else if (i2 == 1) {
            i = r0.rowCount;
            r0.rowCount = i + 1;
            r0.viewMessagesRow = i;
            i = r0.rowCount;
            r0.rowCount = i + 1;
            r0.sendMessagesRow = i;
            i = r0.rowCount;
            r0.rowCount = i + 1;
            r0.sendMediaRow = i;
            i = r0.rowCount;
            r0.rowCount = i + 1;
            r0.sendStickersRow = i;
            i = r0.rowCount;
            r0.rowCount = i + 1;
            r0.embedLinksRow = i;
            i = r0.rowCount;
            r0.rowCount = i + 1;
            r0.untilDateRow = i;
        }
        if (r0.canEdit && initialIsSet) {
            i = r0.rowCount;
            r0.rowCount = i + 1;
            r0.rightsShadowRow = i;
            i = r0.rowCount;
            r0.rowCount = i + 1;
            r0.removeAdminRow = i;
            i = r0.rowCount;
            r0.rowCount = i + 1;
            r0.removeAdminShadowRow = i;
            r0.cantEditInfoRow = -1;
            return;
        }
        r0.removeAdminRow = -1;
        r0.removeAdminShadowRow = -1;
        if (i2 != 0 || r0.canEdit) {
            i = r0.rowCount;
            r0.rowCount = i + 1;
            r0.rightsShadowRow = i;
            return;
        }
        r0.rightsShadowRow = -1;
        i = r0.rowCount;
        r0.rowCount = i + 1;
        r0.cantEditInfoRow = i;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("EditAdmin", R.string.EditAdmin));
        } else {
            this.actionBar.setTitle(LocaleController.getString("UserRestrictions", R.string.UserRestrictions));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C19871());
        if (this.canEdit) {
            this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        }
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        recyclerListView = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new C19883());
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public void setDelegate(ChannelRightsEditActivityDelegate channelRightsEditActivityDelegate) {
        this.delegate = channelRightsEditActivityDelegate;
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new C19894();
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[34];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{UserCell.class, TextSettingsCell.class, TextCheckCell2.class, HeaderCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        View view = this.listView;
        View view2 = view;
        themeDescriptionArr[11] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText3);
        view = this.listView;
        view2 = view;
        themeDescriptionArr[12] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueImageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked);
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        themeDescriptionArr[21] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        int i = 3;
        int i2 = 1;
        themeDescriptionArr[24] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, сellDelegate, Theme.key_windowBackgroundWhiteGrayText);
        view = this.listView;
        Class[] clsArr = new Class[i2];
        clsArr[0] = UserCell.class;
        String[] strArr = new String[i2];
        strArr[0] = "statusOnlineColor";
        themeDescriptionArr[25] = new ThemeDescription(view, 0, clsArr, strArr, null, null, сellDelegate, Theme.key_windowBackgroundWhiteBlueText);
        view = this.listView;
        clsArr = new Class[i2];
        clsArr[0] = UserCell.class;
        Drawable[] drawableArr = new Drawable[i];
        drawableArr[0] = Theme.avatar_photoDrawable;
        drawableArr[i2] = Theme.avatar_broadcastDrawable;
        drawableArr[2] = Theme.avatar_savedDrawable;
        themeDescriptionArr[26] = new ThemeDescription(view, 0, clsArr, null, drawableArr, null, Theme.key_avatar_text);
        ThemeDescriptionDelegate themeDescriptionDelegate = сellDelegate;
        themeDescriptionArr[27] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[30] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[31] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[32] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[33] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink);
        return themeDescriptionArr;
    }
}
