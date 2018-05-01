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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
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
    class C19931 extends ActionBarMenuOnItemClick {
        C19931() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ChannelRightsEditActivity.this.finishFragment();
                return;
            }
            int i2 = 1;
            if (i == 1) {
                if (ChannelRightsEditActivity.this.currentType == 0) {
                    if (ChannelRightsEditActivity.this.isMegagroup != 0) {
                        i = ChannelRightsEditActivity.this.adminRights;
                        ChannelRightsEditActivity.this.adminRights.edit_messages = false;
                        i.post_messages = false;
                    } else {
                        i = ChannelRightsEditActivity.this.adminRights;
                        ChannelRightsEditActivity.this.adminRights.ban_users = false;
                        i.pin_messages = false;
                    }
                    MessagesController.getInstance(ChannelRightsEditActivity.this.currentAccount).setUserAdminRole(ChannelRightsEditActivity.this.chatId, ChannelRightsEditActivity.this.currentUser, ChannelRightsEditActivity.this.adminRights, ChannelRightsEditActivity.this.isMegagroup, ChannelRightsEditActivity.this.getFragmentForAlert(1));
                    if (ChannelRightsEditActivity.this.delegate != 0) {
                        i = ChannelRightsEditActivity.this.delegate;
                        if (!(ChannelRightsEditActivity.this.adminRights.change_info || ChannelRightsEditActivity.this.adminRights.post_messages || ChannelRightsEditActivity.this.adminRights.edit_messages || ChannelRightsEditActivity.this.adminRights.delete_messages || ChannelRightsEditActivity.this.adminRights.ban_users || ChannelRightsEditActivity.this.adminRights.invite_users || ChannelRightsEditActivity.this.adminRights.invite_link || ChannelRightsEditActivity.this.adminRights.pin_messages)) {
                            if (!ChannelRightsEditActivity.this.adminRights.add_admins) {
                                i2 = 0;
                            }
                        }
                        i.didSetRights(i2, ChannelRightsEditActivity.this.adminRights, ChannelRightsEditActivity.this.bannedRights);
                    }
                } else if (ChannelRightsEditActivity.this.currentType == 1) {
                    MessagesController.getInstance(ChannelRightsEditActivity.this.currentAccount).setUserBannedRole(ChannelRightsEditActivity.this.chatId, ChannelRightsEditActivity.this.currentUser, ChannelRightsEditActivity.this.bannedRights, ChannelRightsEditActivity.this.isMegagroup, ChannelRightsEditActivity.this.getFragmentForAlert(1));
                    if (ChannelRightsEditActivity.this.bannedRights.view_messages != 0) {
                        i2 = 0;
                    } else if (ChannelRightsEditActivity.this.bannedRights.send_messages == 0 && ChannelRightsEditActivity.this.bannedRights.send_stickers == 0 && ChannelRightsEditActivity.this.bannedRights.embed_links == 0 && ChannelRightsEditActivity.this.bannedRights.send_media == 0 && ChannelRightsEditActivity.this.bannedRights.send_gifs == 0 && ChannelRightsEditActivity.this.bannedRights.send_games == 0) {
                        if (ChannelRightsEditActivity.this.bannedRights.send_inline == 0) {
                            ChannelRightsEditActivity.this.bannedRights.until_date = 0;
                            i2 = 2;
                        }
                    }
                    if (ChannelRightsEditActivity.this.delegate != 0) {
                        ChannelRightsEditActivity.this.delegate.didSetRights(i2, ChannelRightsEditActivity.this.adminRights, ChannelRightsEditActivity.this.bannedRights);
                    }
                }
                ChannelRightsEditActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelRightsEditActivity$3 */
    class C19943 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.ChannelRightsEditActivity$3$1 */
        class C10041 implements OnDateSetListener {

            /* renamed from: org.telegram.ui.ChannelRightsEditActivity$3$1$2 */
            class C10032 implements OnClickListener {
                public void onClick(DialogInterface dialogInterface, int i) {
                }

                C10032() {
                }
            }

            C10041() {
            }

            public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                datePicker = Calendar.getInstance();
                datePicker.clear();
                datePicker.set(i, i2, i3);
                datePicker = (int) (datePicker.getTime().getTime() / 1000);
                try {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(ChannelRightsEditActivity.this.getParentActivity(), new OnTimeSetListener() {
                        public void onTimeSet(TimePicker timePicker, int i, int i2) {
                            ChannelRightsEditActivity.this.bannedRights.until_date = (datePicker + (i * 3600)) + (i2 * 60);
                            ChannelRightsEditActivity.this.listViewAdapter.notifyItemChanged(ChannelRightsEditActivity.this.untilDateRow);
                        }
                    }, 0, 0, true);
                    timePickerDialog.setButton(-1, LocaleController.getString("Set", C0446R.string.Set), timePickerDialog);
                    timePickerDialog.setButton(-2, LocaleController.getString("Cancel", C0446R.string.Cancel), new C10032());
                    ChannelRightsEditActivity.this.showDialog(timePickerDialog);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }

        /* renamed from: org.telegram.ui.ChannelRightsEditActivity$3$2 */
        class C10052 implements OnClickListener {
            C10052() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ChannelRightsEditActivity.this.bannedRights.until_date = 0;
                ChannelRightsEditActivity.this.listViewAdapter.notifyItemChanged(ChannelRightsEditActivity.this.untilDateRow);
            }
        }

        /* renamed from: org.telegram.ui.ChannelRightsEditActivity$3$3 */
        class C10063 implements OnClickListener {
            public void onClick(DialogInterface dialogInterface, int i) {
            }

            C10063() {
            }
        }

        C19943() {
        }

        public void onItemClick(View view, int i) {
            if (ChannelRightsEditActivity.this.canEdit) {
                if (i == 0) {
                    view = new Bundle();
                    view.putInt("user_id", ChannelRightsEditActivity.this.currentUser.id);
                    ChannelRightsEditActivity.this.presentFragment(new ProfileActivity(view));
                } else if (i == ChannelRightsEditActivity.this.removeAdminRow) {
                    if (ChannelRightsEditActivity.this.currentType == null) {
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
                        view = Calendar.getInstance();
                        try {
                            View datePickerDialog = new DatePickerDialog(ChannelRightsEditActivity.this.getParentActivity(), new C10041(), view.get(1), view.get(2), view.get(5));
                            i = datePickerDialog.getDatePicker();
                            Calendar instance = Calendar.getInstance();
                            instance.setTimeInMillis(System.currentTimeMillis());
                            instance.set(11, instance.getMinimum(11));
                            instance.set(12, instance.getMinimum(12));
                            instance.set(13, instance.getMinimum(13));
                            instance.set(14, instance.getMinimum(14));
                            i.setMinDate(instance.getTimeInMillis());
                            instance.setTimeInMillis(System.currentTimeMillis() + 31536000000L);
                            instance.set(11, instance.getMaximum(11));
                            instance.set(12, instance.getMaximum(12));
                            instance.set(13, instance.getMaximum(13));
                            instance.set(14, instance.getMaximum(14));
                            i.setMaxDate(instance.getTimeInMillis());
                            datePickerDialog.setButton(-1, LocaleController.getString("Set", C0446R.string.Set), datePickerDialog);
                            datePickerDialog.setButton(-3, LocaleController.getString("UserRestrictionsUntilForever", C0446R.string.UserRestrictionsUntilForever), new C10052());
                            datePickerDialog.setButton(-2, LocaleController.getString("Cancel", C0446R.string.Cancel), new C10063());
                            if (VERSION.SDK_INT >= 21) {
                                datePickerDialog.setOnShowListener(new OnShowListener() {
                                    public void onShow(DialogInterface dialogInterface) {
                                        dialogInterface = i.getChildCount();
                                        for (int i = 0; i < dialogInterface; i++) {
                                            View childAt = i.getChildAt(i);
                                            LayoutParams layoutParams = childAt.getLayoutParams();
                                            layoutParams.width = -1;
                                            childAt.setLayoutParams(layoutParams);
                                        }
                                    }
                                });
                            }
                            ChannelRightsEditActivity.this.showDialog(datePickerDialog);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                } else if (view instanceof TextCheckCell2) {
                    TextCheckCell2 textCheckCell2 = (TextCheckCell2) view;
                    if (textCheckCell2.isEnabled()) {
                        textCheckCell2.setChecked(textCheckCell2.isChecked() ^ true);
                        if (i == ChannelRightsEditActivity.this.changeInfoRow) {
                            ChannelRightsEditActivity.this.adminRights.change_info ^= 1;
                        } else if (i == ChannelRightsEditActivity.this.postMessagesRow) {
                            ChannelRightsEditActivity.this.adminRights.post_messages ^= 1;
                        } else if (i == ChannelRightsEditActivity.this.editMesagesRow) {
                            ChannelRightsEditActivity.this.adminRights.edit_messages ^= 1;
                        } else if (i == ChannelRightsEditActivity.this.deleteMessagesRow) {
                            ChannelRightsEditActivity.this.adminRights.delete_messages ^= 1;
                        } else if (i == ChannelRightsEditActivity.this.addAdminsRow) {
                            ChannelRightsEditActivity.this.adminRights.add_admins ^= 1;
                        } else if (i == ChannelRightsEditActivity.this.banUsersRow) {
                            ChannelRightsEditActivity.this.adminRights.ban_users ^= 1;
                        } else if (i == ChannelRightsEditActivity.this.addUsersRow) {
                            view = ChannelRightsEditActivity.this.adminRights;
                            boolean z = ChannelRightsEditActivity.this.adminRights.invite_users ^ true;
                            ChannelRightsEditActivity.this.adminRights.invite_link = z;
                            view.invite_users = z;
                        } else if (i == ChannelRightsEditActivity.this.pinMessagesRow) {
                            ChannelRightsEditActivity.this.adminRights.pin_messages ^= 1;
                        } else if (ChannelRightsEditActivity.this.bannedRights != null) {
                            TL_channelBannedRights access$700;
                            view = textCheckCell2.isChecked() ^ 1;
                            if (i == ChannelRightsEditActivity.this.viewMessagesRow) {
                                ChannelRightsEditActivity.this.bannedRights.view_messages ^= true;
                            } else if (i == ChannelRightsEditActivity.this.sendMessagesRow) {
                                ChannelRightsEditActivity.this.bannedRights.send_messages ^= true;
                            } else if (i == ChannelRightsEditActivity.this.sendMediaRow) {
                                ChannelRightsEditActivity.this.bannedRights.send_media ^= true;
                            } else if (i == ChannelRightsEditActivity.this.sendStickersRow) {
                                i = ChannelRightsEditActivity.this.bannedRights;
                                access$700 = ChannelRightsEditActivity.this.bannedRights;
                                TL_channelBannedRights access$7002 = ChannelRightsEditActivity.this.bannedRights;
                                boolean z2 = ChannelRightsEditActivity.this.bannedRights.send_stickers ^ true;
                                ChannelRightsEditActivity.this.bannedRights.send_inline = z2;
                                access$7002.send_gifs = z2;
                                access$700.send_games = z2;
                                i.send_stickers = z2;
                            } else if (i == ChannelRightsEditActivity.this.embedLinksRow) {
                                ChannelRightsEditActivity.this.bannedRights.embed_links ^= true;
                            }
                            if (view != null) {
                                if (ChannelRightsEditActivity.this.bannedRights.view_messages != null && ChannelRightsEditActivity.this.bannedRights.send_messages == null) {
                                    ChannelRightsEditActivity.this.bannedRights.send_messages = true;
                                    view = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.sendMessagesRow);
                                    if (view != null) {
                                        ((TextCheckCell2) view.itemView).setChecked(false);
                                    }
                                }
                                if (!(ChannelRightsEditActivity.this.bannedRights.view_messages == null && ChannelRightsEditActivity.this.bannedRights.send_messages == null) && ChannelRightsEditActivity.this.bannedRights.send_media == null) {
                                    ChannelRightsEditActivity.this.bannedRights.send_media = true;
                                    view = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.sendMediaRow);
                                    if (view != null) {
                                        ((TextCheckCell2) view.itemView).setChecked(false);
                                    }
                                }
                                if (!(ChannelRightsEditActivity.this.bannedRights.view_messages == null && ChannelRightsEditActivity.this.bannedRights.send_messages == null && ChannelRightsEditActivity.this.bannedRights.send_media == null) && ChannelRightsEditActivity.this.bannedRights.send_stickers == null) {
                                    view = ChannelRightsEditActivity.this.bannedRights;
                                    i = ChannelRightsEditActivity.this.bannedRights;
                                    access$700 = ChannelRightsEditActivity.this.bannedRights;
                                    ChannelRightsEditActivity.this.bannedRights.send_inline = true;
                                    access$700.send_gifs = true;
                                    i.send_games = true;
                                    view.send_stickers = true;
                                    view = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.sendStickersRow);
                                    if (view != null) {
                                        ((TextCheckCell2) view.itemView).setChecked(false);
                                    }
                                }
                                if (!(ChannelRightsEditActivity.this.bannedRights.view_messages == null && ChannelRightsEditActivity.this.bannedRights.send_messages == null && ChannelRightsEditActivity.this.bannedRights.send_media == null) && ChannelRightsEditActivity.this.bannedRights.embed_links == null) {
                                    ChannelRightsEditActivity.this.bannedRights.embed_links = true;
                                    view = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.embedLinksRow);
                                    if (view != null) {
                                        ((TextCheckCell2) view.itemView).setChecked(false);
                                    }
                                }
                            } else {
                                if ((ChannelRightsEditActivity.this.bannedRights.send_messages == null || ChannelRightsEditActivity.this.bannedRights.embed_links == null || ChannelRightsEditActivity.this.bannedRights.send_inline == null || ChannelRightsEditActivity.this.bannedRights.send_media == null) && ChannelRightsEditActivity.this.bannedRights.view_messages != null) {
                                    ChannelRightsEditActivity.this.bannedRights.view_messages = false;
                                    view = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.viewMessagesRow);
                                    if (view != null) {
                                        ((TextCheckCell2) view.itemView).setChecked(true);
                                    }
                                }
                                if ((ChannelRightsEditActivity.this.bannedRights.embed_links == null || ChannelRightsEditActivity.this.bannedRights.send_inline == null || ChannelRightsEditActivity.this.bannedRights.send_media == null) && ChannelRightsEditActivity.this.bannedRights.send_messages != null) {
                                    ChannelRightsEditActivity.this.bannedRights.send_messages = false;
                                    view = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.sendMessagesRow);
                                    if (view != null) {
                                        ((TextCheckCell2) view.itemView).setChecked(true);
                                    }
                                }
                                if ((ChannelRightsEditActivity.this.bannedRights.send_inline == null || ChannelRightsEditActivity.this.bannedRights.embed_links == null) && ChannelRightsEditActivity.this.bannedRights.send_media != null) {
                                    ChannelRightsEditActivity.this.bannedRights.send_media = false;
                                    view = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.sendMediaRow);
                                    if (view != null) {
                                        ((TextCheckCell2) view.itemView).setChecked(true);
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
    class C19954 implements ThemeDescriptionDelegate {
        C19954() {
        }

        public void didSetColor() {
            if (ChannelRightsEditActivity.this.listView != null) {
                int childCount = ChannelRightsEditActivity.this.listView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = ChannelRightsEditActivity.this.listView.getChildAt(i);
                    if (childAt instanceof UserCell) {
                        ((UserCell) childAt).update(0);
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

        public boolean isEnabled(ViewHolder viewHolder) {
            boolean z = false;
            if (!ChannelRightsEditActivity.this.canEdit) {
                return false;
            }
            int itemViewType = viewHolder.getItemViewType();
            if (ChannelRightsEditActivity.this.currentType == 0 && itemViewType == 4) {
                viewHolder = viewHolder.getAdapterPosition();
                if (viewHolder == ChannelRightsEditActivity.this.changeInfoRow) {
                    return ChannelRightsEditActivity.this.myAdminRights.change_info;
                }
                if (viewHolder == ChannelRightsEditActivity.this.postMessagesRow) {
                    return ChannelRightsEditActivity.this.myAdminRights.post_messages;
                }
                if (viewHolder == ChannelRightsEditActivity.this.editMesagesRow) {
                    return ChannelRightsEditActivity.this.myAdminRights.edit_messages;
                }
                if (viewHolder == ChannelRightsEditActivity.this.deleteMessagesRow) {
                    return ChannelRightsEditActivity.this.myAdminRights.delete_messages;
                }
                if (viewHolder == ChannelRightsEditActivity.this.addAdminsRow) {
                    return ChannelRightsEditActivity.this.myAdminRights.add_admins;
                }
                if (viewHolder == ChannelRightsEditActivity.this.banUsersRow) {
                    return ChannelRightsEditActivity.this.myAdminRights.ban_users;
                }
                if (viewHolder == ChannelRightsEditActivity.this.addUsersRow) {
                    return ChannelRightsEditActivity.this.myAdminRights.invite_users;
                }
                if (viewHolder == ChannelRightsEditActivity.this.pinMessagesRow) {
                    return ChannelRightsEditActivity.this.myAdminRights.pin_messages;
                }
            }
            if (!(itemViewType == 3 || itemViewType == 1 || itemViewType == 5)) {
                z = true;
            }
            return z;
        }

        public int getItemCount() {
            return ChannelRightsEditActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new UserCell(this.mContext, 1, 0, false);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    viewGroup = new TextInfoPrivacyCell(this.mContext);
                    viewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 2:
                    viewGroup = new TextSettingsCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    viewGroup = new HeaderCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    viewGroup = new TextCheckCell2(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    viewGroup = new ShadowSectionCell(this.mContext);
                    break;
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    ((UserCell) viewHolder.itemView).setData(ChannelRightsEditActivity.this.currentUser, null, null, 0);
                    return;
                case 1:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == ChannelRightsEditActivity.this.cantEditInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("EditAdminCantEdit", C0446R.string.EditAdminCantEdit));
                        return;
                    }
                    return;
                case 2:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i == ChannelRightsEditActivity.this.removeAdminRow) {
                        textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
                        textSettingsCell.setTag(Theme.key_windowBackgroundWhiteRedText3);
                        if (ChannelRightsEditActivity.this.currentType == 0) {
                            textSettingsCell.setText(LocaleController.getString("EditAdminRemoveAdmin", C0446R.string.EditAdminRemoveAdmin), false);
                            return;
                        } else if (ChannelRightsEditActivity.this.currentType == 1) {
                            textSettingsCell.setText(LocaleController.getString("UserRestrictionsBlock", C0446R.string.UserRestrictionsBlock), false);
                            return;
                        } else {
                            return;
                        }
                    } else if (i == ChannelRightsEditActivity.this.untilDateRow) {
                        textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        textSettingsCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                        if (ChannelRightsEditActivity.this.bannedRights.until_date != 0) {
                            if (Math.abs(((long) ChannelRightsEditActivity.this.bannedRights.until_date) - (System.currentTimeMillis() / 1000)) <= 315360000) {
                                i = LocaleController.formatDateForBan((long) ChannelRightsEditActivity.this.bannedRights.until_date);
                                textSettingsCell.setTextAndValue(LocaleController.getString("UserRestrictionsUntil", C0446R.string.UserRestrictionsUntil), i, false);
                                return;
                            }
                        }
                        i = LocaleController.getString("UserRestrictionsUntilForever", C0446R.string.UserRestrictionsUntilForever);
                        textSettingsCell.setTextAndValue(LocaleController.getString("UserRestrictionsUntil", C0446R.string.UserRestrictionsUntil), i, false);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (ChannelRightsEditActivity.this.currentType == 0) {
                        headerCell.setText(LocaleController.getString("EditAdminWhatCanDo", C0446R.string.EditAdminWhatCanDo));
                        return;
                    } else if (ChannelRightsEditActivity.this.currentType == 1) {
                        headerCell.setText(LocaleController.getString("UserRestrictionsCanDo", C0446R.string.UserRestrictionsCanDo));
                        return;
                    } else {
                        return;
                    }
                case 4:
                    TextCheckCell2 textCheckCell2 = (TextCheckCell2) viewHolder.itemView;
                    if (i == ChannelRightsEditActivity.this.changeInfoRow) {
                        if (ChannelRightsEditActivity.this.isMegagroup) {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminChangeGroupInfo", C0446R.string.EditAdminChangeGroupInfo), ChannelRightsEditActivity.this.adminRights.change_info, true);
                        } else {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminChangeChannelInfo", C0446R.string.EditAdminChangeChannelInfo), ChannelRightsEditActivity.this.adminRights.change_info, true);
                        }
                    } else if (i == ChannelRightsEditActivity.this.postMessagesRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminPostMessages", C0446R.string.EditAdminPostMessages), ChannelRightsEditActivity.this.adminRights.post_messages, true);
                    } else if (i == ChannelRightsEditActivity.this.editMesagesRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminEditMessages", C0446R.string.EditAdminEditMessages), ChannelRightsEditActivity.this.adminRights.edit_messages, true);
                    } else if (i == ChannelRightsEditActivity.this.deleteMessagesRow) {
                        if (ChannelRightsEditActivity.this.isMegagroup) {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminGroupDeleteMessages", C0446R.string.EditAdminGroupDeleteMessages), ChannelRightsEditActivity.this.adminRights.delete_messages, true);
                        } else {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminDeleteMessages", C0446R.string.EditAdminDeleteMessages), ChannelRightsEditActivity.this.adminRights.delete_messages, true);
                        }
                    } else if (i == ChannelRightsEditActivity.this.addAdminsRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminAddAdmins", C0446R.string.EditAdminAddAdmins), ChannelRightsEditActivity.this.adminRights.add_admins, false);
                    } else if (i == ChannelRightsEditActivity.this.banUsersRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminBanUsers", C0446R.string.EditAdminBanUsers), ChannelRightsEditActivity.this.adminRights.ban_users, true);
                    } else if (i == ChannelRightsEditActivity.this.addUsersRow) {
                        if (ChannelRightsEditActivity.this.isDemocracy) {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminAddUsersViaLink", C0446R.string.EditAdminAddUsersViaLink), ChannelRightsEditActivity.this.adminRights.invite_users, true);
                        } else {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminAddUsers", C0446R.string.EditAdminAddUsers), ChannelRightsEditActivity.this.adminRights.invite_users, true);
                        }
                    } else if (i == ChannelRightsEditActivity.this.pinMessagesRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminPinMessages", C0446R.string.EditAdminPinMessages), ChannelRightsEditActivity.this.adminRights.pin_messages, true);
                    } else if (i == ChannelRightsEditActivity.this.viewMessagesRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsRead", C0446R.string.UserRestrictionsRead), ChannelRightsEditActivity.this.bannedRights.view_messages ^ true, true);
                    } else if (i == ChannelRightsEditActivity.this.sendMessagesRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsSend", C0446R.string.UserRestrictionsSend), ChannelRightsEditActivity.this.bannedRights.send_messages ^ true, true);
                    } else if (i == ChannelRightsEditActivity.this.sendMediaRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsSendMedia", C0446R.string.UserRestrictionsSendMedia), ChannelRightsEditActivity.this.bannedRights.send_media ^ true, true);
                    } else if (i == ChannelRightsEditActivity.this.sendStickersRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsSendStickers", C0446R.string.UserRestrictionsSendStickers), ChannelRightsEditActivity.this.bannedRights.send_stickers ^ true, true);
                    } else if (i == ChannelRightsEditActivity.this.embedLinksRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("UserRestrictionsEmbedLinks", C0446R.string.UserRestrictionsEmbedLinks), ChannelRightsEditActivity.this.bannedRights.embed_links ^ true, true);
                    }
                    if (!(i == ChannelRightsEditActivity.this.sendMediaRow || i == ChannelRightsEditActivity.this.sendStickersRow)) {
                        if (i != ChannelRightsEditActivity.this.embedLinksRow) {
                            if (i == ChannelRightsEditActivity.this.sendMessagesRow) {
                                textCheckCell2.setEnabled(ChannelRightsEditActivity.this.bannedRights.view_messages ^ 1);
                                return;
                            }
                            return;
                        }
                    }
                    if (ChannelRightsEditActivity.this.bannedRights.send_messages == 0 && ChannelRightsEditActivity.this.bannedRights.view_messages == 0) {
                        z = true;
                    }
                    textCheckCell2.setEnabled(z);
                    return;
                case 5:
                    ShadowSectionCell shadowSectionCell = (ShadowSectionCell) viewHolder.itemView;
                    int access$3300 = ChannelRightsEditActivity.this.rightsShadowRow;
                    int i2 = C0446R.drawable.greydivider;
                    if (i == access$3300) {
                        i = this.mContext;
                        if (ChannelRightsEditActivity.this.removeAdminRow == -1) {
                            i2 = C0446R.drawable.greydivider_bottom;
                        }
                        shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(i, i2, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == ChannelRightsEditActivity.this.removeAdminShadowRow) {
                        shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            if (i == 0) {
                return 0;
            }
            if (!(i == 1 || i == ChannelRightsEditActivity.this.rightsShadowRow)) {
                if (i != ChannelRightsEditActivity.this.removeAdminShadowRow) {
                    if (i == 2) {
                        return 3;
                    }
                    if (!(i == ChannelRightsEditActivity.this.changeInfoRow || i == ChannelRightsEditActivity.this.postMessagesRow || i == ChannelRightsEditActivity.this.editMesagesRow || i == ChannelRightsEditActivity.this.deleteMessagesRow || i == ChannelRightsEditActivity.this.addAdminsRow || i == ChannelRightsEditActivity.this.banUsersRow || i == ChannelRightsEditActivity.this.addUsersRow || i == ChannelRightsEditActivity.this.pinMessagesRow || i == ChannelRightsEditActivity.this.viewMessagesRow || i == ChannelRightsEditActivity.this.sendMessagesRow || i == ChannelRightsEditActivity.this.sendMediaRow || i == ChannelRightsEditActivity.this.sendStickersRow)) {
                        if (i != ChannelRightsEditActivity.this.embedLinksRow) {
                            return i == ChannelRightsEditActivity.this.cantEditInfoRow ? 1 : 2;
                        }
                    }
                    return 4;
                }
            }
            return 5;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ChannelRightsEditActivity(int i, int i2, TL_channelAdminRights tL_channelAdminRights, TL_channelBannedRights tL_channelBannedRights, int i3, boolean z) {
        this.chatId = i2;
        this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
        this.currentType = i3;
        this.canEdit = z;
        i = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        if (i != 0) {
            this.isMegagroup = i.megagroup;
            this.myAdminRights = i.admin_rights;
        }
        if (this.myAdminRights == 0) {
            this.myAdminRights = new TL_channelAdminRights();
            i2 = this.myAdminRights;
            TL_channelAdminRights tL_channelAdminRights2 = this.myAdminRights;
            TL_channelAdminRights tL_channelAdminRights3 = this.myAdminRights;
            TL_channelAdminRights tL_channelAdminRights4 = this.myAdminRights;
            TL_channelAdminRights tL_channelAdminRights5 = this.myAdminRights;
            TL_channelAdminRights tL_channelAdminRights6 = this.myAdminRights;
            TL_channelAdminRights tL_channelAdminRights7 = this.myAdminRights;
            TL_channelAdminRights tL_channelAdminRights8 = this.myAdminRights;
            this.myAdminRights.add_admins = true;
            tL_channelAdminRights8.pin_messages = true;
            tL_channelAdminRights7.invite_link = true;
            tL_channelAdminRights6.invite_users = true;
            tL_channelAdminRights5.ban_users = true;
            tL_channelAdminRights4.delete_messages = true;
            tL_channelAdminRights3.edit_messages = true;
            tL_channelAdminRights2.post_messages = true;
            i2.change_info = true;
        }
        i2 = 0;
        if (i3 == 0) {
            this.adminRights = new TL_channelAdminRights();
            if (tL_channelAdminRights == null) {
                this.adminRights.change_info = this.myAdminRights.change_info;
                this.adminRights.post_messages = this.myAdminRights.post_messages;
                this.adminRights.edit_messages = this.myAdminRights.edit_messages;
                this.adminRights.delete_messages = this.myAdminRights.delete_messages;
                this.adminRights.ban_users = this.myAdminRights.ban_users;
                this.adminRights.invite_users = this.myAdminRights.invite_users;
                this.adminRights.invite_link = this.myAdminRights.invite_link;
                this.adminRights.pin_messages = this.myAdminRights.pin_messages;
            } else {
                this.adminRights.change_info = tL_channelAdminRights.change_info;
                this.adminRights.post_messages = tL_channelAdminRights.post_messages;
                this.adminRights.edit_messages = tL_channelAdminRights.edit_messages;
                this.adminRights.delete_messages = tL_channelAdminRights.delete_messages;
                this.adminRights.ban_users = tL_channelAdminRights.ban_users;
                this.adminRights.invite_users = tL_channelAdminRights.invite_users;
                this.adminRights.invite_link = tL_channelAdminRights.invite_link;
                this.adminRights.pin_messages = tL_channelAdminRights.pin_messages;
                this.adminRights.add_admins = tL_channelAdminRights.add_admins;
                if (this.adminRights.change_info == null) {
                    if (this.adminRights.post_messages == null) {
                        if (this.adminRights.edit_messages == null) {
                            if (this.adminRights.delete_messages == null) {
                                if (this.adminRights.ban_users == null) {
                                    if (this.adminRights.invite_users == null) {
                                        if (this.adminRights.invite_link == null) {
                                            if (this.adminRights.pin_messages == null) {
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            this.rowCount += 3;
            if (i3 == 0) {
                if (this.isMegagroup != null) {
                    tL_channelAdminRights = this.rowCount;
                    this.rowCount = tL_channelAdminRights + 1;
                    this.changeInfoRow = tL_channelAdminRights;
                    tL_channelAdminRights = this.rowCount;
                    this.rowCount = tL_channelAdminRights + 1;
                    this.deleteMessagesRow = tL_channelAdminRights;
                    tL_channelAdminRights = this.rowCount;
                    this.rowCount = tL_channelAdminRights + 1;
                    this.banUsersRow = tL_channelAdminRights;
                    tL_channelAdminRights = this.rowCount;
                    this.rowCount = tL_channelAdminRights + 1;
                    this.addUsersRow = tL_channelAdminRights;
                    tL_channelAdminRights = this.rowCount;
                    this.rowCount = tL_channelAdminRights + 1;
                    this.pinMessagesRow = tL_channelAdminRights;
                    tL_channelAdminRights = this.rowCount;
                    this.rowCount = tL_channelAdminRights + 1;
                    this.addAdminsRow = tL_channelAdminRights;
                    this.isDemocracy = i.democracy;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.changeInfoRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.postMessagesRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.editMesagesRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.deleteMessagesRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.addUsersRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.addAdminsRow = i;
                }
            } else if (i3 == 1) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.viewMessagesRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.sendMessagesRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.sendMediaRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.sendStickersRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.embedLinksRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.untilDateRow = i;
            }
            if (this.canEdit != 0 || r10 == 0) {
                this.removeAdminRow = -1;
                this.removeAdminShadowRow = -1;
                if (i3 == 0 || this.canEdit != 0) {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.rightsShadowRow = i;
                }
                this.rightsShadowRow = -1;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.cantEditInfoRow = i;
                return;
            }
            i = this.rowCount;
            this.rowCount = i + 1;
            this.rightsShadowRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.removeAdminRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.removeAdminShadowRow = i;
            this.cantEditInfoRow = -1;
            return;
        }
        this.bannedRights = new TL_channelBannedRights();
        if (tL_channelBannedRights == null) {
            tL_channelAdminRights = this.bannedRights;
            TL_channelBannedRights tL_channelBannedRights2 = this.bannedRights;
            TL_channelBannedRights tL_channelBannedRights3 = this.bannedRights;
            TL_channelBannedRights tL_channelBannedRights4 = this.bannedRights;
            TL_channelBannedRights tL_channelBannedRights5 = this.bannedRights;
            TL_channelBannedRights tL_channelBannedRights6 = this.bannedRights;
            TL_channelBannedRights tL_channelBannedRights7 = this.bannedRights;
            this.bannedRights.send_inline = true;
            tL_channelBannedRights7.send_games = true;
            tL_channelBannedRights6.send_gifs = true;
            tL_channelBannedRights5.send_stickers = true;
            tL_channelBannedRights4.embed_links = true;
            tL_channelBannedRights3.send_messages = true;
            tL_channelBannedRights2.send_media = true;
            tL_channelAdminRights.view_messages = true;
        } else {
            this.bannedRights.view_messages = tL_channelBannedRights.view_messages;
            this.bannedRights.send_messages = tL_channelBannedRights.send_messages;
            this.bannedRights.send_media = tL_channelBannedRights.send_media;
            this.bannedRights.send_stickers = tL_channelBannedRights.send_stickers;
            this.bannedRights.send_gifs = tL_channelBannedRights.send_gifs;
            this.bannedRights.send_games = tL_channelBannedRights.send_games;
            this.bannedRights.send_inline = tL_channelBannedRights.send_inline;
            this.bannedRights.embed_links = tL_channelBannedRights.embed_links;
            this.bannedRights.until_date = tL_channelBannedRights.until_date;
        }
        if (tL_channelBannedRights != null) {
            if (tL_channelBannedRights.view_messages == null) {
            }
            this.rowCount += 3;
            if (i3 == 0) {
                if (i3 == 1) {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.viewMessagesRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.sendMessagesRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.sendMediaRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.sendStickersRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.embedLinksRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.untilDateRow = i;
                }
            } else if (this.isMegagroup != null) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.changeInfoRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.postMessagesRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.editMesagesRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.deleteMessagesRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.addUsersRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.addAdminsRow = i;
            } else {
                tL_channelAdminRights = this.rowCount;
                this.rowCount = tL_channelAdminRights + 1;
                this.changeInfoRow = tL_channelAdminRights;
                tL_channelAdminRights = this.rowCount;
                this.rowCount = tL_channelAdminRights + 1;
                this.deleteMessagesRow = tL_channelAdminRights;
                tL_channelAdminRights = this.rowCount;
                this.rowCount = tL_channelAdminRights + 1;
                this.banUsersRow = tL_channelAdminRights;
                tL_channelAdminRights = this.rowCount;
                this.rowCount = tL_channelAdminRights + 1;
                this.addUsersRow = tL_channelAdminRights;
                tL_channelAdminRights = this.rowCount;
                this.rowCount = tL_channelAdminRights + 1;
                this.pinMessagesRow = tL_channelAdminRights;
                tL_channelAdminRights = this.rowCount;
                this.rowCount = tL_channelAdminRights + 1;
                this.addAdminsRow = tL_channelAdminRights;
                this.isDemocracy = i.democracy;
            }
            if (this.canEdit != 0) {
            }
            this.removeAdminRow = -1;
            this.removeAdminShadowRow = -1;
            if (i3 == 0) {
            }
            i = this.rowCount;
            this.rowCount = i + 1;
            this.rightsShadowRow = i;
        }
        i2 = 1;
        this.rowCount += 3;
        if (i3 == 0) {
            if (this.isMegagroup != null) {
                tL_channelAdminRights = this.rowCount;
                this.rowCount = tL_channelAdminRights + 1;
                this.changeInfoRow = tL_channelAdminRights;
                tL_channelAdminRights = this.rowCount;
                this.rowCount = tL_channelAdminRights + 1;
                this.deleteMessagesRow = tL_channelAdminRights;
                tL_channelAdminRights = this.rowCount;
                this.rowCount = tL_channelAdminRights + 1;
                this.banUsersRow = tL_channelAdminRights;
                tL_channelAdminRights = this.rowCount;
                this.rowCount = tL_channelAdminRights + 1;
                this.addUsersRow = tL_channelAdminRights;
                tL_channelAdminRights = this.rowCount;
                this.rowCount = tL_channelAdminRights + 1;
                this.pinMessagesRow = tL_channelAdminRights;
                tL_channelAdminRights = this.rowCount;
                this.rowCount = tL_channelAdminRights + 1;
                this.addAdminsRow = tL_channelAdminRights;
                this.isDemocracy = i.democracy;
            } else {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.changeInfoRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.postMessagesRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.editMesagesRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.deleteMessagesRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.addUsersRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.addAdminsRow = i;
            }
        } else if (i3 == 1) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.viewMessagesRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.sendMessagesRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.sendMediaRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.sendStickersRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.embedLinksRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.untilDateRow = i;
        }
        if (this.canEdit != 0) {
        }
        this.removeAdminRow = -1;
        this.removeAdminShadowRow = -1;
        if (i3 == 0) {
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rightsShadowRow = i;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("EditAdmin", C0446R.string.EditAdmin));
        } else {
            this.actionBar.setTitle(LocaleController.getString("UserRestrictions", C0446R.string.UserRestrictions));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C19931());
        if (this.canEdit) {
            this.actionBar.createMenu().addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        }
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context);
        LayoutManager c23322 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setLayoutManager(c23322);
        RecyclerListView recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        context = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        context.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new C19943());
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
        C19954 c19954 = new C19954();
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[34];
        int i = 3;
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{UserCell.class, TextSettingsCell.class, TextCheckCell2.class, HeaderCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[i] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        View view = this.listView;
        View view2 = view;
        themeDescriptionArr[9] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        view = this.listView;
        view2 = view;
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
        view = this.listView;
        view2 = view;
        themeDescriptionArr[21] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        ThemeDescriptionDelegate themeDescriptionDelegate = c19954;
        themeDescriptionArr[24] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteGrayText);
        themeDescriptionArr[25] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlueText);
        View view3 = this.listView;
        Class[] clsArr = new Class[]{UserCell.class};
        Drawable[] drawableArr = new Drawable[i];
        drawableArr[0] = Theme.avatar_photoDrawable;
        drawableArr[1] = Theme.avatar_broadcastDrawable;
        drawableArr[2] = Theme.avatar_savedDrawable;
        themeDescriptionArr[26] = new ThemeDescription(view3, 0, clsArr, null, drawableArr, null, Theme.key_avatar_text);
        C19954 c199542 = c19954;
        themeDescriptionArr[27] = new ThemeDescription(null, 0, null, null, null, c199542, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, null, c199542, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, c199542, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[30] = new ThemeDescription(null, 0, null, null, null, c199542, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[31] = new ThemeDescription(null, 0, null, null, null, c199542, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[32] = new ThemeDescription(null, 0, null, null, null, c199542, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[33] = new ThemeDescription(null, 0, null, null, null, c199542, Theme.key_avatar_backgroundPink);
        return themeDescriptionArr;
    }
}
