package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Parcelable;
import android.provider.Settings.System;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_resetNotifySettings;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class NotificationsSettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private ListAdapter adapter;
    private int androidAutoAlertRow;
    private int badgeNumberRow;
    private int callsRingtoneRow;
    private int callsSectionRow;
    private int callsSectionRow2;
    private int callsVibrateRow;
    private int contactJoinedRow;
    private int eventsSectionRow;
    private int eventsSectionRow2;
    private int groupAlertRow;
    private int groupLedRow;
    private int groupPopupNotificationRow;
    private int groupPreviewRow;
    private int groupPriorityRow;
    private int groupSectionRow;
    private int groupSectionRow2;
    private int groupSoundRow;
    private int groupVibrateRow;
    private int inappPreviewRow;
    private int inappPriorityRow;
    private int inappSectionRow;
    private int inappSectionRow2;
    private int inappSoundRow;
    private int inappVibrateRow;
    private int inchatSoundRow;
    private RecyclerListView listView;
    private int messageAlertRow;
    private int messageLedRow;
    private int messagePopupNotificationRow;
    private int messagePreviewRow;
    private int messagePriorityRow;
    private int messageSectionRow;
    private int messageSoundRow;
    private int messageVibrateRow;
    private int notificationsServiceConnectionRow;
    private int notificationsServiceRow;
    private int otherSectionRow;
    private int otherSectionRow2;
    private int pinnedMessageRow;
    private int repeatRow;
    private int resetNotificationsRow;
    private int resetSectionRow;
    private int resetSectionRow2;
    private boolean reseting = false;
    private int rowCount = 0;

    /* renamed from: org.telegram.ui.NotificationsSettingsActivity$1 */
    class C22091 extends ActionBarMenuOnItemClick {
        C22091() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                NotificationsSettingsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.NotificationsSettingsActivity$3 */
    class C22113 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.NotificationsSettingsActivity$3$1 */
        class C22101 implements RequestDelegate {

            /* renamed from: org.telegram.ui.NotificationsSettingsActivity$3$1$1 */
            class C15431 implements Runnable {
                C15431() {
                }

                public void run() {
                    MessagesController.getInstance(NotificationsSettingsActivity.this.currentAccount).enableJoined = true;
                    NotificationsSettingsActivity.this.reseting = false;
                    Editor edit = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount).edit();
                    edit.clear();
                    edit.commit();
                    NotificationsSettingsActivity.this.adapter.notifyDataSetChanged();
                    if (NotificationsSettingsActivity.this.getParentActivity() != null) {
                        Toast.makeText(NotificationsSettingsActivity.this.getParentActivity(), LocaleController.getString("ResetNotificationsText", C0446R.string.ResetNotificationsText), 0).show();
                    }
                }
            }

            C22101() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C15431());
            }
        }

        C22113() {
        }

        public void onItemClick(View view, final int i) {
            boolean z;
            TextCheckCell textCheckCell;
            SharedPreferences notificationsSettings;
            Editor edit;
            boolean z2 = false;
            if (i != NotificationsSettingsActivity.this.messageAlertRow) {
                if (i != NotificationsSettingsActivity.this.groupAlertRow) {
                    if (i != NotificationsSettingsActivity.this.messagePreviewRow) {
                        if (i != NotificationsSettingsActivity.this.groupPreviewRow) {
                            Parcelable parcelable = null;
                            int i2 = 2;
                            if (!(i == NotificationsSettingsActivity.this.messageSoundRow || i == NotificationsSettingsActivity.this.groupSoundRow)) {
                                if (i != NotificationsSettingsActivity.this.callsRingtoneRow) {
                                    if (i != NotificationsSettingsActivity.this.resetNotificationsRow) {
                                        Editor edit2;
                                        if (i == NotificationsSettingsActivity.this.inappSoundRow) {
                                            i = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                                            edit2 = i.edit();
                                            i = i.getBoolean("EnableInAppSounds", true);
                                            edit2.putBoolean("EnableInAppSounds", i ^ 1);
                                            edit2.commit();
                                        } else if (i == NotificationsSettingsActivity.this.inappVibrateRow) {
                                            i = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                                            edit2 = i.edit();
                                            i = i.getBoolean("EnableInAppVibrate", true);
                                            edit2.putBoolean("EnableInAppVibrate", i ^ 1);
                                            edit2.commit();
                                        } else if (i == NotificationsSettingsActivity.this.inappPreviewRow) {
                                            i = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                                            edit2 = i.edit();
                                            i = i.getBoolean("EnableInAppPreview", true);
                                            edit2.putBoolean("EnableInAppPreview", i ^ 1);
                                            edit2.commit();
                                        } else if (i == NotificationsSettingsActivity.this.inchatSoundRow) {
                                            i = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                                            edit2 = i.edit();
                                            i = i.getBoolean("EnableInChatSound", true);
                                            edit2.putBoolean("EnableInChatSound", i ^ 1);
                                            edit2.commit();
                                            NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).setInChatSoundEnabled(i ^ 1);
                                        } else if (i == NotificationsSettingsActivity.this.inappPriorityRow) {
                                            i = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                                            edit2 = i.edit();
                                            i = i.getBoolean("EnableInAppPriority", false);
                                            edit2.putBoolean("EnableInAppPriority", i ^ 1);
                                            edit2.commit();
                                        } else if (i == NotificationsSettingsActivity.this.contactJoinedRow) {
                                            i = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                                            edit2 = i.edit();
                                            i = i.getBoolean("EnableContactJoined", true);
                                            MessagesController.getInstance(NotificationsSettingsActivity.this.currentAccount).enableJoined = i ^ 1;
                                            edit2.putBoolean("EnableContactJoined", i ^ 1);
                                            edit2.commit();
                                        } else if (i == NotificationsSettingsActivity.this.pinnedMessageRow) {
                                            i = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                                            edit2 = i.edit();
                                            i = i.getBoolean("PinnedMessages", true);
                                            edit2.putBoolean("PinnedMessages", i ^ 1);
                                            edit2.commit();
                                        } else if (i == NotificationsSettingsActivity.this.androidAutoAlertRow) {
                                            i = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                                            edit2 = i.edit();
                                            i = i.getBoolean("EnableAutoNotifications", false);
                                            edit2.putBoolean("EnableAutoNotifications", i ^ 1);
                                            edit2.commit();
                                        } else {
                                            if (i == NotificationsSettingsActivity.this.badgeNumberRow) {
                                                i = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount).edit();
                                                z = NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeNumber;
                                                NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeNumber = z ^ 1;
                                                i.putBoolean("badgeNumber", NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeNumber);
                                                i.commit();
                                                NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).setBadgeEnabled(z ^ 1);
                                            } else if (i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                                                i = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                                                z = i.getBoolean("pushConnection", true);
                                                i = i.edit();
                                                i.putBoolean("pushConnection", z ^ 1);
                                                i.commit();
                                                if (z) {
                                                    ConnectionsManager.getInstance(NotificationsSettingsActivity.this.currentAccount).setPushConnectionEnabled(false);
                                                } else {
                                                    ConnectionsManager.getInstance(NotificationsSettingsActivity.this.currentAccount).setPushConnectionEnabled(true);
                                                }
                                            } else if (i == NotificationsSettingsActivity.this.notificationsServiceRow) {
                                                i = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                                                z = i.getBoolean("pushService", true);
                                                i = i.edit();
                                                i.putBoolean("pushService", z ^ 1);
                                                i.commit();
                                                if (z) {
                                                    ApplicationLoader.stopPushService();
                                                } else {
                                                    ApplicationLoader.startPushService();
                                                }
                                            } else {
                                                if (i != NotificationsSettingsActivity.this.messageLedRow) {
                                                    if (i != NotificationsSettingsActivity.this.groupLedRow) {
                                                        if (i != NotificationsSettingsActivity.this.messagePopupNotificationRow) {
                                                            if (i != NotificationsSettingsActivity.this.groupPopupNotificationRow) {
                                                                if (!(i == NotificationsSettingsActivity.this.messageVibrateRow || i == NotificationsSettingsActivity.this.groupVibrateRow)) {
                                                                    if (i != NotificationsSettingsActivity.this.callsVibrateRow) {
                                                                        if (i != NotificationsSettingsActivity.this.messagePriorityRow) {
                                                                            if (i != NotificationsSettingsActivity.this.groupPriorityRow) {
                                                                                if (i == NotificationsSettingsActivity.this.repeatRow) {
                                                                                    Builder builder = new Builder(NotificationsSettingsActivity.this.getParentActivity());
                                                                                    builder.setTitle(LocaleController.getString("RepeatNotifications", C0446R.string.RepeatNotifications));
                                                                                    builder.setItems(new CharSequence[]{LocaleController.getString("RepeatDisabled", C0446R.string.RepeatDisabled), LocaleController.formatPluralString("Minutes", 5), LocaleController.formatPluralString("Minutes", 10), LocaleController.formatPluralString("Minutes", 30), LocaleController.formatPluralString("Hours", 1), LocaleController.formatPluralString("Hours", 2), LocaleController.formatPluralString("Hours", 4)}, new OnClickListener() {
                                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                                            dialogInterface = 5;
                                                                                            if (i != 1) {
                                                                                                dialogInterface = i == 2 ? 10 : i == 3 ? 30 : i == 4 ? 60 : i == 5 ? 120 : i == 6 ? PsExtractor.VIDEO_STREAM_MASK : null;
                                                                                            }
                                                                                            MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount).edit().putInt("repeat_messages", dialogInterface).commit();
                                                                                            NotificationsSettingsActivity.this.adapter.notifyItemChanged(i);
                                                                                        }
                                                                                    });
                                                                                    builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                                                                                    NotificationsSettingsActivity.this.showDialog(builder.create());
                                                                                }
                                                                            }
                                                                        }
                                                                        NotificationsSettingsActivity.this.showDialog(AlertsCreator.createPrioritySelectDialog(NotificationsSettingsActivity.this.getParentActivity(), NotificationsSettingsActivity.this, 0, i == NotificationsSettingsActivity.this.groupPriorityRow, i == NotificationsSettingsActivity.this.messagePriorityRow, new Runnable() {
                                                                            public void run() {
                                                                                NotificationsSettingsActivity.this.adapter.notifyItemChanged(i);
                                                                            }
                                                                        }));
                                                                    }
                                                                }
                                                                if (NotificationsSettingsActivity.this.getParentActivity() != null) {
                                                                    String str;
                                                                    if (i == NotificationsSettingsActivity.this.messageVibrateRow) {
                                                                        str = "vibrate_messages";
                                                                    } else if (i == NotificationsSettingsActivity.this.groupVibrateRow) {
                                                                        str = "vibrate_group";
                                                                    } else if (i == NotificationsSettingsActivity.this.callsVibrateRow) {
                                                                        str = "vibrate_calls";
                                                                    }
                                                                    NotificationsSettingsActivity.this.showDialog(AlertsCreator.createVibrationSelectDialog(NotificationsSettingsActivity.this.getParentActivity(), NotificationsSettingsActivity.this, 0, str, new Runnable() {
                                                                        public void run() {
                                                                            NotificationsSettingsActivity.this.adapter.notifyItemChanged(i);
                                                                        }
                                                                    }));
                                                                } else {
                                                                    return;
                                                                }
                                                            }
                                                        }
                                                        if (NotificationsSettingsActivity.this.getParentActivity() != null) {
                                                            NotificationsSettingsActivity.this.showDialog(AlertsCreator.createPopupSelectDialog(NotificationsSettingsActivity.this.getParentActivity(), NotificationsSettingsActivity.this, i == NotificationsSettingsActivity.this.groupPopupNotificationRow, i == NotificationsSettingsActivity.this.messagePopupNotificationRow, new Runnable() {
                                                                public void run() {
                                                                    NotificationsSettingsActivity.this.adapter.notifyItemChanged(i);
                                                                }
                                                            }));
                                                        } else {
                                                            return;
                                                        }
                                                    }
                                                }
                                                if (NotificationsSettingsActivity.this.getParentActivity() != null) {
                                                    NotificationsSettingsActivity.this.showDialog(AlertsCreator.createColorSelectDialog(NotificationsSettingsActivity.this.getParentActivity(), 0, i == NotificationsSettingsActivity.this.groupLedRow, i == NotificationsSettingsActivity.this.messageLedRow, new Runnable() {
                                                        public void run() {
                                                            NotificationsSettingsActivity.this.adapter.notifyItemChanged(i);
                                                        }
                                                    }));
                                                } else {
                                                    return;
                                                }
                                            }
                                            i = z;
                                        }
                                        if (view instanceof TextCheckCell) {
                                            textCheckCell = (TextCheckCell) view;
                                            if (i == 0) {
                                                z2 = true;
                                            }
                                            textCheckCell.setChecked(z2);
                                        }
                                    } else if (NotificationsSettingsActivity.this.reseting == 0) {
                                        NotificationsSettingsActivity.this.reseting = true;
                                        ConnectionsManager.getInstance(NotificationsSettingsActivity.this.currentAccount).sendRequest(new TL_account_resetNotifySettings(), new C22101());
                                    } else {
                                        return;
                                    }
                                    i = 0;
                                    if (view instanceof TextCheckCell) {
                                        textCheckCell = (TextCheckCell) view;
                                        if (i == 0) {
                                            z2 = true;
                                        }
                                        textCheckCell.setChecked(z2);
                                    }
                                }
                            }
                            try {
                                notificationsSettings = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                                Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
                                intent.putExtra("android.intent.extra.ringtone.TYPE", i == NotificationsSettingsActivity.this.callsRingtoneRow ? 1 : 2);
                                intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                                String str2 = "android.intent.extra.ringtone.DEFAULT_URI";
                                if (i == NotificationsSettingsActivity.this.callsRingtoneRow) {
                                    i2 = 1;
                                }
                                intent.putExtra(str2, RingtoneManager.getDefaultUri(i2));
                                Uri uri = i == NotificationsSettingsActivity.this.callsRingtoneRow ? System.DEFAULT_RINGTONE_URI : System.DEFAULT_NOTIFICATION_URI;
                                str2 = uri != null ? uri.getPath() : null;
                                String string;
                                if (i == NotificationsSettingsActivity.this.messageSoundRow) {
                                    string = notificationsSettings.getString("GlobalSoundPath", str2);
                                    if (!(string == null || string.equals("NoSound"))) {
                                        if (!string.equals(str2)) {
                                            parcelable = Uri.parse(string);
                                        }
                                    }
                                    intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", parcelable);
                                    NotificationsSettingsActivity.this.startActivityForResult(intent, i);
                                    i = 0;
                                    if (view instanceof TextCheckCell) {
                                        textCheckCell = (TextCheckCell) view;
                                        if (i == 0) {
                                            z2 = true;
                                        }
                                        textCheckCell.setChecked(z2);
                                    }
                                } else if (i == NotificationsSettingsActivity.this.groupSoundRow) {
                                    string = notificationsSettings.getString("GroupSoundPath", str2);
                                    if (!(string == null || string.equals("NoSound"))) {
                                        if (!string.equals(str2)) {
                                            parcelable = Uri.parse(string);
                                        }
                                    }
                                    intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", parcelable);
                                    NotificationsSettingsActivity.this.startActivityForResult(intent, i);
                                    i = 0;
                                    if (view instanceof TextCheckCell) {
                                        textCheckCell = (TextCheckCell) view;
                                        if (i == 0) {
                                            z2 = true;
                                        }
                                        textCheckCell.setChecked(z2);
                                    }
                                } else {
                                    if (i == NotificationsSettingsActivity.this.callsRingtoneRow) {
                                        string = notificationsSettings.getString("CallsRingtonfePath", str2);
                                        if (!(string == null || string.equals("NoSound"))) {
                                            if (!string.equals(str2)) {
                                                parcelable = Uri.parse(string);
                                            }
                                        }
                                    }
                                    intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", parcelable);
                                    NotificationsSettingsActivity.this.startActivityForResult(intent, i);
                                    i = 0;
                                    if (view instanceof TextCheckCell) {
                                        textCheckCell = (TextCheckCell) view;
                                        if (i == 0) {
                                            z2 = true;
                                        }
                                        textCheckCell.setChecked(z2);
                                    }
                                }
                                parcelable = uri;
                                intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", parcelable);
                                NotificationsSettingsActivity.this.startActivityForResult(intent, i);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                            i = 0;
                            if (view instanceof TextCheckCell) {
                                textCheckCell = (TextCheckCell) view;
                                if (i == 0) {
                                    z2 = true;
                                }
                                textCheckCell.setChecked(z2);
                            }
                        }
                    }
                    notificationsSettings = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    edit = notificationsSettings.edit();
                    if (i == NotificationsSettingsActivity.this.messagePreviewRow) {
                        z = notificationsSettings.getBoolean("EnablePreviewAll", true);
                        edit.putBoolean("EnablePreviewAll", z ^ 1);
                    } else if (i == NotificationsSettingsActivity.this.groupPreviewRow) {
                        z = notificationsSettings.getBoolean("EnablePreviewGroup", true);
                        edit.putBoolean("EnablePreviewGroup", z ^ 1);
                    } else {
                        z = false;
                    }
                    edit.commit();
                    NotificationsSettingsActivity.this.updateServerNotificationsSettings(i == NotificationsSettingsActivity.this.groupPreviewRow ? 1 : 0);
                    i = z;
                    if (view instanceof TextCheckCell) {
                        textCheckCell = (TextCheckCell) view;
                        if (i == 0) {
                            z2 = true;
                        }
                        textCheckCell.setChecked(z2);
                    }
                }
            }
            notificationsSettings = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
            edit = notificationsSettings.edit();
            if (i == NotificationsSettingsActivity.this.messageAlertRow) {
                z = notificationsSettings.getBoolean("EnableAll", true);
                edit.putBoolean("EnableAll", z ^ 1);
            } else if (i == NotificationsSettingsActivity.this.groupAlertRow) {
                z = notificationsSettings.getBoolean("EnableGroup", true);
                edit.putBoolean("EnableGroup", z ^ 1);
            } else {
                z = false;
            }
            edit.commit();
            NotificationsSettingsActivity.this.updateServerNotificationsSettings(i == NotificationsSettingsActivity.this.groupAlertRow ? 1 : 0);
            i = z;
            if (view instanceof TextCheckCell) {
                textCheckCell = (TextCheckCell) view;
                if (i == 0) {
                    z2 = true;
                }
                textCheckCell.setChecked(z2);
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getAdapterPosition();
            return (viewHolder == NotificationsSettingsActivity.this.messageSectionRow || viewHolder == NotificationsSettingsActivity.this.groupSectionRow || viewHolder == NotificationsSettingsActivity.this.inappSectionRow || viewHolder == NotificationsSettingsActivity.this.eventsSectionRow || viewHolder == NotificationsSettingsActivity.this.otherSectionRow || viewHolder == NotificationsSettingsActivity.this.resetSectionRow || viewHolder == NotificationsSettingsActivity.this.eventsSectionRow2 || viewHolder == NotificationsSettingsActivity.this.groupSectionRow2 || viewHolder == NotificationsSettingsActivity.this.inappSectionRow2 || viewHolder == NotificationsSettingsActivity.this.otherSectionRow2 || viewHolder == NotificationsSettingsActivity.this.resetSectionRow2 || viewHolder == NotificationsSettingsActivity.this.callsSectionRow2 || viewHolder == NotificationsSettingsActivity.this.callsSectionRow) ? null : true;
        }

        public int getItemCount() {
            return NotificationsSettingsActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new HeaderCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    viewGroup = new TextCheckCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    viewGroup = new TextDetailSettingsCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    viewGroup = new TextColorCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    viewGroup = new ShadowSectionCell(this.mContext);
                    break;
                default:
                    viewGroup = new TextSettingsCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            int i2 = 0;
            SharedPreferences notificationsSettings;
            if (itemViewType != 5) {
                switch (itemViewType) {
                    case 0:
                        HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                        if (i == NotificationsSettingsActivity.this.messageSectionRow) {
                            headerCell.setText(LocaleController.getString("MessageNotifications", C0446R.string.MessageNotifications));
                            return;
                        } else if (i == NotificationsSettingsActivity.this.groupSectionRow) {
                            headerCell.setText(LocaleController.getString("GroupNotifications", C0446R.string.GroupNotifications));
                            return;
                        } else if (i == NotificationsSettingsActivity.this.inappSectionRow) {
                            headerCell.setText(LocaleController.getString("InAppNotifications", C0446R.string.InAppNotifications));
                            return;
                        } else if (i == NotificationsSettingsActivity.this.eventsSectionRow) {
                            headerCell.setText(LocaleController.getString("Events", C0446R.string.Events));
                            return;
                        } else if (i == NotificationsSettingsActivity.this.otherSectionRow) {
                            headerCell.setText(LocaleController.getString("NotificationsOther", C0446R.string.NotificationsOther));
                            return;
                        } else if (i == NotificationsSettingsActivity.this.resetSectionRow) {
                            headerCell.setText(LocaleController.getString("Reset", C0446R.string.Reset));
                            return;
                        } else if (i == NotificationsSettingsActivity.this.callsSectionRow) {
                            headerCell.setText(LocaleController.getString("VoipNotificationSettings", C0446R.string.VoipNotificationSettings));
                            return;
                        } else {
                            return;
                        }
                    case 1:
                        TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                        viewHolder = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                        if (i == NotificationsSettingsActivity.this.messageAlertRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("Alert", C0446R.string.Alert), viewHolder.getBoolean("EnableAll", true), true);
                            return;
                        } else if (i == NotificationsSettingsActivity.this.groupAlertRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("Alert", C0446R.string.Alert), viewHolder.getBoolean("EnableGroup", true), true);
                            return;
                        } else if (i == NotificationsSettingsActivity.this.messagePreviewRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("MessagePreview", C0446R.string.MessagePreview), viewHolder.getBoolean("EnablePreviewAll", true), true);
                            return;
                        } else if (i == NotificationsSettingsActivity.this.groupPreviewRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("MessagePreview", C0446R.string.MessagePreview), viewHolder.getBoolean("EnablePreviewGroup", true), true);
                            return;
                        } else if (i == NotificationsSettingsActivity.this.inappSoundRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("InAppSounds", C0446R.string.InAppSounds), viewHolder.getBoolean("EnableInAppSounds", true), true);
                            return;
                        } else if (i == NotificationsSettingsActivity.this.inappVibrateRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("InAppVibrate", C0446R.string.InAppVibrate), viewHolder.getBoolean("EnableInAppVibrate", true), true);
                            return;
                        } else if (i == NotificationsSettingsActivity.this.inappPreviewRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("InAppPreview", C0446R.string.InAppPreview), viewHolder.getBoolean("EnableInAppPreview", true), true);
                            return;
                        } else if (i == NotificationsSettingsActivity.this.inappPriorityRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("NotificationsImportance", C0446R.string.NotificationsImportance), viewHolder.getBoolean("EnableInAppPriority", false), false);
                            return;
                        } else if (i == NotificationsSettingsActivity.this.contactJoinedRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("ContactJoined", C0446R.string.ContactJoined), viewHolder.getBoolean("EnableContactJoined", true), true);
                            return;
                        } else if (i == NotificationsSettingsActivity.this.pinnedMessageRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("PinnedMessages", C0446R.string.PinnedMessages), viewHolder.getBoolean("PinnedMessages", true), false);
                            return;
                        } else if (i == NotificationsSettingsActivity.this.androidAutoAlertRow) {
                            textCheckCell.setTextAndCheck("Android Auto", viewHolder.getBoolean("EnableAutoNotifications", false), true);
                            return;
                        } else if (i == NotificationsSettingsActivity.this.notificationsServiceRow) {
                            textCheckCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsService", C0446R.string.NotificationsService), LocaleController.getString("NotificationsServiceInfo", C0446R.string.NotificationsServiceInfo), viewHolder.getBoolean("pushService", true), true, true);
                            return;
                        } else if (i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                            textCheckCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsServiceConnection", C0446R.string.NotificationsServiceConnection), LocaleController.getString("NotificationsServiceConnectionInfo", C0446R.string.NotificationsServiceConnectionInfo), viewHolder.getBoolean("pushConnection", true), true, true);
                            return;
                        } else if (i == NotificationsSettingsActivity.this.badgeNumberRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("BadgeNumber", C0446R.string.BadgeNumber), NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeNumber, true);
                            return;
                        } else if (i == NotificationsSettingsActivity.this.inchatSoundRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("InChatSound", C0446R.string.InChatSound), viewHolder.getBoolean("EnableInChatSound", true), true);
                            return;
                        } else if (i == NotificationsSettingsActivity.this.callsVibrateRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("Vibrate", C0446R.string.Vibrate), viewHolder.getBoolean("EnableCallVibrate", true), true);
                            return;
                        } else {
                            return;
                        }
                    case 2:
                        TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) viewHolder.itemView;
                        textDetailSettingsCell.setMultilineDetail(true);
                        textDetailSettingsCell.setTextAndValue(LocaleController.getString("ResetAllNotifications", C0446R.string.ResetAllNotifications), LocaleController.getString("UndoAllCustom", C0446R.string.UndoAllCustom), false);
                        return;
                    case 3:
                        TextColorCell textColorCell = (TextColorCell) viewHolder.itemView;
                        notificationsSettings = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                        if (i == NotificationsSettingsActivity.this.messageLedRow) {
                            i = notificationsSettings.getInt("MessagesLed", -16776961);
                        } else {
                            i = notificationsSettings.getInt("GroupLed", -16776961);
                        }
                        while (i2 < 9) {
                            if (TextColorCell.colorsToSave[i2] == i) {
                                i = TextColorCell.colors[i2];
                                textColorCell.setTextAndColor(LocaleController.getString("LedColor", C0446R.string.LedColor), i, true);
                                return;
                            }
                            i2++;
                        }
                        textColorCell.setTextAndColor(LocaleController.getString("LedColor", C0446R.string.LedColor), i, true);
                        return;
                    default:
                        return;
                }
            }
            TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
            notificationsSettings = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
            if (!(i == NotificationsSettingsActivity.this.messageSoundRow || i == NotificationsSettingsActivity.this.groupSoundRow)) {
                if (i != NotificationsSettingsActivity.this.callsRingtoneRow) {
                    if (!(i == NotificationsSettingsActivity.this.messageVibrateRow || i == NotificationsSettingsActivity.this.groupVibrateRow)) {
                        if (i != NotificationsSettingsActivity.this.callsVibrateRow) {
                            if (i == NotificationsSettingsActivity.this.repeatRow) {
                                i = notificationsSettings.getInt("repeat_messages", 60);
                                if (i == 0) {
                                    i = LocaleController.getString("RepeatNotificationsNever", C0446R.string.RepeatNotificationsNever);
                                } else if (i < 60) {
                                    i = LocaleController.formatPluralString("Minutes", i);
                                } else {
                                    i = LocaleController.formatPluralString("Hours", i / 60);
                                }
                                textSettingsCell.setTextAndValue(LocaleController.getString("RepeatNotifications", C0446R.string.RepeatNotifications), i, false);
                                return;
                            }
                            if (i != NotificationsSettingsActivity.this.messagePriorityRow) {
                                if (i != NotificationsSettingsActivity.this.groupPriorityRow) {
                                    if (i == NotificationsSettingsActivity.this.messagePopupNotificationRow || i == NotificationsSettingsActivity.this.groupPopupNotificationRow) {
                                        if (i == NotificationsSettingsActivity.this.messagePopupNotificationRow) {
                                            i2 = notificationsSettings.getInt("popupAll", 0);
                                        } else if (i == NotificationsSettingsActivity.this.groupPopupNotificationRow) {
                                            i2 = notificationsSettings.getInt("popupGroup", 0);
                                        }
                                        if (i2 == 0) {
                                            i = LocaleController.getString("NoPopup", C0446R.string.NoPopup);
                                        } else if (i2 == 1) {
                                            i = LocaleController.getString("OnlyWhenScreenOn", C0446R.string.OnlyWhenScreenOn);
                                        } else if (i2 == 2) {
                                            i = LocaleController.getString("OnlyWhenScreenOff", C0446R.string.OnlyWhenScreenOff);
                                        } else {
                                            i = LocaleController.getString("AlwaysShowPopup", C0446R.string.AlwaysShowPopup);
                                        }
                                        textSettingsCell.setTextAndValue(LocaleController.getString("PopupNotification", C0446R.string.PopupNotification), i, true);
                                        return;
                                    }
                                    return;
                                }
                            }
                            i = i == NotificationsSettingsActivity.this.messagePriorityRow ? notificationsSettings.getInt("priority_messages", 1) : i == NotificationsSettingsActivity.this.groupPriorityRow ? notificationsSettings.getInt("priority_group", 1) : 0;
                            if (i == 0) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", C0446R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityHigh", C0446R.string.NotificationsPriorityHigh), false);
                                return;
                            }
                            if (i != 1) {
                                if (i != 2) {
                                    if (i == 4) {
                                        textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", C0446R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityLow", C0446R.string.NotificationsPriorityLow), false);
                                        return;
                                    } else if (i == 5) {
                                        textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", C0446R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityMedium", C0446R.string.NotificationsPriorityMedium), false);
                                        return;
                                    } else {
                                        return;
                                    }
                                }
                            }
                            textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", C0446R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityUrgent", C0446R.string.NotificationsPriorityUrgent), false);
                            return;
                        }
                    }
                    if (i == NotificationsSettingsActivity.this.messageVibrateRow) {
                        i2 = notificationsSettings.getInt("vibrate_messages", 0);
                    } else if (i == NotificationsSettingsActivity.this.groupVibrateRow) {
                        i2 = notificationsSettings.getInt("vibrate_group", 0);
                    } else if (i == NotificationsSettingsActivity.this.callsVibrateRow) {
                        i2 = notificationsSettings.getInt("vibrate_calls", 0);
                    }
                    if (i2 == 0) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0446R.string.Vibrate), LocaleController.getString("VibrationDefault", C0446R.string.VibrationDefault), true);
                        return;
                    } else if (i2 == 1) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0446R.string.Vibrate), LocaleController.getString("Short", C0446R.string.Short), true);
                        return;
                    } else if (i2 == 2) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0446R.string.Vibrate), LocaleController.getString("VibrationDisabled", C0446R.string.VibrationDisabled), true);
                        return;
                    } else if (i2 == 3) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0446R.string.Vibrate), LocaleController.getString("Long", C0446R.string.Long), true);
                        return;
                    } else if (i2 == 4) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", C0446R.string.Vibrate), LocaleController.getString("OnlyIfSilent", C0446R.string.OnlyIfSilent), true);
                        return;
                    } else {
                        return;
                    }
                }
            }
            String str = null;
            if (i == NotificationsSettingsActivity.this.messageSoundRow) {
                str = notificationsSettings.getString("GlobalSound", LocaleController.getString("SoundDefault", C0446R.string.SoundDefault));
            } else if (i == NotificationsSettingsActivity.this.groupSoundRow) {
                str = notificationsSettings.getString("GroupSound", LocaleController.getString("SoundDefault", C0446R.string.SoundDefault));
            } else if (i == NotificationsSettingsActivity.this.callsRingtoneRow) {
                str = notificationsSettings.getString("CallsRingtone", LocaleController.getString("DefaultRingtone", C0446R.string.DefaultRingtone));
            }
            if (str.equals("NoSound")) {
                str = LocaleController.getString("NoSound", C0446R.string.NoSound);
            }
            if (i == NotificationsSettingsActivity.this.callsRingtoneRow) {
                textSettingsCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", C0446R.string.VoipSettingsRingtone), str, true);
            } else {
                textSettingsCell.setTextAndValue(LocaleController.getString("Sound", C0446R.string.Sound), str, true);
            }
        }

        public int getItemViewType(int i) {
            if (!(i == NotificationsSettingsActivity.this.messageSectionRow || i == NotificationsSettingsActivity.this.groupSectionRow || i == NotificationsSettingsActivity.this.inappSectionRow || i == NotificationsSettingsActivity.this.eventsSectionRow || i == NotificationsSettingsActivity.this.otherSectionRow || i == NotificationsSettingsActivity.this.resetSectionRow)) {
                if (i != NotificationsSettingsActivity.this.callsSectionRow) {
                    if (!(i == NotificationsSettingsActivity.this.messageAlertRow || i == NotificationsSettingsActivity.this.messagePreviewRow || i == NotificationsSettingsActivity.this.groupAlertRow || i == NotificationsSettingsActivity.this.groupPreviewRow || i == NotificationsSettingsActivity.this.inappSoundRow || i == NotificationsSettingsActivity.this.inappVibrateRow || i == NotificationsSettingsActivity.this.inappPreviewRow || i == NotificationsSettingsActivity.this.contactJoinedRow || i == NotificationsSettingsActivity.this.pinnedMessageRow || i == NotificationsSettingsActivity.this.notificationsServiceRow || i == NotificationsSettingsActivity.this.badgeNumberRow || i == NotificationsSettingsActivity.this.inappPriorityRow || i == NotificationsSettingsActivity.this.inchatSoundRow || i == NotificationsSettingsActivity.this.androidAutoAlertRow)) {
                        if (i != NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                            if (i != NotificationsSettingsActivity.this.messageLedRow) {
                                if (i != NotificationsSettingsActivity.this.groupLedRow) {
                                    if (!(i == NotificationsSettingsActivity.this.eventsSectionRow2 || i == NotificationsSettingsActivity.this.groupSectionRow2 || i == NotificationsSettingsActivity.this.inappSectionRow2 || i == NotificationsSettingsActivity.this.otherSectionRow2 || i == NotificationsSettingsActivity.this.resetSectionRow2)) {
                                        if (i != NotificationsSettingsActivity.this.callsSectionRow2) {
                                            return i == NotificationsSettingsActivity.this.resetNotificationsRow ? 2 : 5;
                                        }
                                    }
                                    return 4;
                                }
                            }
                            return 3;
                        }
                    }
                    return 1;
                }
            }
            return 0;
        }
    }

    public void updateServerNotificationsSettings(boolean z) {
    }

    public boolean onFragmentCreate() {
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.messageSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageAlertRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagePreviewRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageLedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageVibrateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagePopupNotificationRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messageSoundRow = i;
        if (VERSION.SDK_INT >= 21) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.messagePriorityRow = i;
        } else {
            this.messagePriorityRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupAlertRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupPreviewRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupLedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupVibrateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupPopupNotificationRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.groupSoundRow = i;
        if (VERSION.SDK_INT >= 21) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.groupPriorityRow = i;
        } else {
            this.groupPriorityRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappSoundRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappVibrateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inappPreviewRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.inchatSoundRow = i;
        if (VERSION.SDK_INT >= 21) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.inappPriorityRow = i;
        } else {
            this.inappPriorityRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsVibrateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsRingtoneRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.eventsSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.eventsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.contactJoinedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.pinnedMessageRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.otherSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.otherSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.notificationsServiceRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.notificationsServiceConnectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.badgeNumberRow = i;
        this.androidAutoAlertRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.repeatRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetNotificationsRow = i;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", C0446R.string.NotificationsAndSounds));
        this.actionBar.setActionBarMenuOnItemClick(new C22091());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView = new RecyclerListView(context);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setOnItemClickListener(new C22113());
        return this.fragmentView;
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i2 == -1) {
            Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            intent = null;
            if (uri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(getParentActivity(), uri);
                if (ringtone != null) {
                    if (i == this.callsRingtoneRow) {
                        if (uri.equals(System.DEFAULT_RINGTONE_URI) != null) {
                            intent = LocaleController.getString("DefaultRingtone", C0446R.string.DefaultRingtone);
                        } else {
                            intent = ringtone.getTitle(getParentActivity());
                        }
                    } else if (uri.equals(System.DEFAULT_NOTIFICATION_URI) != null) {
                        intent = LocaleController.getString("SoundDefault", C0446R.string.SoundDefault);
                    } else {
                        intent = ringtone.getTitle(getParentActivity());
                    }
                    ringtone.stop();
                }
            }
            Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            if (i == this.messageSoundRow) {
                if (intent == null || uri == null) {
                    edit.putString("GlobalSound", "NoSound");
                    edit.putString("GlobalSoundPath", "NoSound");
                } else {
                    edit.putString("GlobalSound", intent);
                    edit.putString("GlobalSoundPath", uri.toString());
                }
            } else if (i == this.groupSoundRow) {
                if (intent == null || uri == null) {
                    edit.putString("GroupSound", "NoSound");
                    edit.putString("GroupSoundPath", "NoSound");
                } else {
                    edit.putString("GroupSound", intent);
                    edit.putString("GroupSoundPath", uri.toString());
                }
            } else if (i == this.callsRingtoneRow) {
                if (intent == null || uri == null) {
                    edit.putString("CallsRingtone", "NoSound");
                    edit.putString("CallsRingtonePath", "NoSound");
                } else {
                    edit.putString("CallsRingtone", intent);
                    edit.putString("CallsRingtonePath", uri.toString());
                }
            }
            edit.commit();
            this.adapter.notifyItemChanged(i);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.notificationsSettingsUpdated) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[22];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextDetailSettingsCell.class, TextColorCell.class, TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        themeDescriptionArr[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        return themeDescriptionArr;
    }
}
