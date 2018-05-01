package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class DataUsageActivity extends BaseFragment {
    private int audiosBytesReceivedRow;
    private int audiosBytesSentRow;
    private int audiosReceivedRow;
    private int audiosSection2Row;
    private int audiosSectionRow;
    private int audiosSentRow;
    private int callsBytesReceivedRow;
    private int callsBytesSentRow;
    private int callsReceivedRow;
    private int callsSection2Row;
    private int callsSectionRow;
    private int callsSentRow;
    private int callsTotalTimeRow;
    private int currentType;
    private int filesBytesReceivedRow;
    private int filesBytesSentRow;
    private int filesReceivedRow;
    private int filesSection2Row;
    private int filesSectionRow;
    private int filesSentRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int messagesBytesReceivedRow;
    private int messagesBytesSentRow;
    private int messagesReceivedRow = -1;
    private int messagesSection2Row;
    private int messagesSectionRow;
    private int messagesSentRow = -1;
    private int photosBytesReceivedRow;
    private int photosBytesSentRow;
    private int photosReceivedRow;
    private int photosSection2Row;
    private int photosSectionRow;
    private int photosSentRow;
    private int resetRow;
    private int resetSection2Row;
    private int rowCount;
    private int totalBytesReceivedRow;
    private int totalBytesSentRow;
    private int totalSection2Row;
    private int totalSectionRow;
    private int videosBytesReceivedRow;
    private int videosBytesSentRow;
    private int videosReceivedRow;
    private int videosSection2Row;
    private int videosSectionRow;
    private int videosSentRow;

    /* renamed from: org.telegram.ui.DataUsageActivity$1 */
    class C21251 extends ActionBarMenuOnItemClick {
        C21251() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                DataUsageActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.DataUsageActivity$2 */
    class C21262 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.DataUsageActivity$2$1 */
        class C13751 implements OnClickListener {
            C13751() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                StatsController.getInstance(DataUsageActivity.this.currentAccount).resetStats(DataUsageActivity.this.currentType);
                DataUsageActivity.this.listAdapter.notifyDataSetChanged();
            }
        }

        C21262() {
        }

        public void onItemClick(View view, int i) {
            if (DataUsageActivity.this.getParentActivity() != null && i == DataUsageActivity.this.resetRow) {
                view = new Builder(DataUsageActivity.this.getParentActivity());
                view.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                view.setMessage(LocaleController.getString("ResetStatisticsAlert", C0446R.string.ResetStatisticsAlert));
                view.setPositiveButton(LocaleController.getString("Reset", C0446R.string.Reset), new C13751());
                view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                DataUsageActivity.this.showDialog(view.create());
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return DataUsageActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    if (i == DataUsageActivity.this.resetSection2Row) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 1:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i == DataUsageActivity.this.resetRow) {
                        textSettingsCell.setTag(Theme.key_windowBackgroundWhiteRedText2);
                        textSettingsCell.setText(LocaleController.getString("ResetStatistics", C0446R.string.ResetStatistics), false);
                        textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText2));
                        return;
                    }
                    int i2;
                    String string;
                    String formatFileSize;
                    textSettingsCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                    textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    if (!(i == DataUsageActivity.this.callsSentRow || i == DataUsageActivity.this.callsReceivedRow || i == DataUsageActivity.this.callsBytesSentRow)) {
                        if (i != DataUsageActivity.this.callsBytesReceivedRow) {
                            if (!(i == DataUsageActivity.this.messagesSentRow || i == DataUsageActivity.this.messagesReceivedRow || i == DataUsageActivity.this.messagesBytesSentRow)) {
                                if (i != DataUsageActivity.this.messagesBytesReceivedRow) {
                                    if (!(i == DataUsageActivity.this.photosSentRow || i == DataUsageActivity.this.photosReceivedRow || i == DataUsageActivity.this.photosBytesSentRow)) {
                                        if (i != DataUsageActivity.this.photosBytesReceivedRow) {
                                            if (!(i == DataUsageActivity.this.audiosSentRow || i == DataUsageActivity.this.audiosReceivedRow || i == DataUsageActivity.this.audiosBytesSentRow)) {
                                                if (i != DataUsageActivity.this.audiosBytesReceivedRow) {
                                                    if (!(i == DataUsageActivity.this.videosSentRow || i == DataUsageActivity.this.videosReceivedRow || i == DataUsageActivity.this.videosBytesSentRow)) {
                                                        if (i != DataUsageActivity.this.videosBytesReceivedRow) {
                                                            if (!(i == DataUsageActivity.this.filesSentRow || i == DataUsageActivity.this.filesReceivedRow || i == DataUsageActivity.this.filesBytesSentRow)) {
                                                                if (i != DataUsageActivity.this.filesBytesReceivedRow) {
                                                                    i2 = 6;
                                                                    if (i != DataUsageActivity.this.callsSentRow) {
                                                                        textSettingsCell.setTextAndValue(LocaleController.getString("OutgoingCalls", C0446R.string.OutgoingCalls), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                                                        return;
                                                                    } else if (i != DataUsageActivity.this.callsReceivedRow) {
                                                                        textSettingsCell.setTextAndValue(LocaleController.getString("IncomingCalls", C0446R.string.IncomingCalls), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                                                        return;
                                                                    } else if (i != DataUsageActivity.this.callsTotalTimeRow) {
                                                                        i = StatsController.getInstance(DataUsageActivity.this.currentAccount).getCallsTotalTime(DataUsageActivity.this.currentType);
                                                                        i2 = i / 3600;
                                                                        i -= i2 * 3600;
                                                                        i -= (i / 60) * 60;
                                                                        if (i2 != 0) {
                                                                            i = String.format("%d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(r5), Integer.valueOf(i)});
                                                                        } else {
                                                                            i = String.format("%d:%02d", new Object[]{Integer.valueOf(r5), Integer.valueOf(i)});
                                                                        }
                                                                        textSettingsCell.setTextAndValue(LocaleController.getString("CallsTotalTime", C0446R.string.CallsTotalTime), i, false);
                                                                        return;
                                                                    } else {
                                                                        if (!(i == DataUsageActivity.this.messagesSentRow || i == DataUsageActivity.this.photosSentRow || i == DataUsageActivity.this.videosSentRow || i == DataUsageActivity.this.audiosSentRow)) {
                                                                            if (i == DataUsageActivity.this.filesSentRow) {
                                                                                if (!(i == DataUsageActivity.this.messagesReceivedRow || i == DataUsageActivity.this.photosReceivedRow || i == DataUsageActivity.this.videosReceivedRow || i == DataUsageActivity.this.audiosReceivedRow)) {
                                                                                    if (i == DataUsageActivity.this.filesReceivedRow) {
                                                                                        if (!(i == DataUsageActivity.this.messagesBytesSentRow || i == DataUsageActivity.this.photosBytesSentRow || i == DataUsageActivity.this.videosBytesSentRow || i == DataUsageActivity.this.audiosBytesSentRow || i == DataUsageActivity.this.filesBytesSentRow || i == DataUsageActivity.this.callsBytesSentRow)) {
                                                                                            if (i != DataUsageActivity.this.totalBytesSentRow) {
                                                                                                if (i != DataUsageActivity.this.messagesBytesReceivedRow || i == DataUsageActivity.this.photosBytesReceivedRow || i == DataUsageActivity.this.videosBytesReceivedRow || i == DataUsageActivity.this.audiosBytesReceivedRow || i == DataUsageActivity.this.filesBytesReceivedRow || i == DataUsageActivity.this.callsBytesReceivedRow || i == DataUsageActivity.this.totalBytesReceivedRow) {
                                                                                                    string = LocaleController.getString("BytesReceived", C0446R.string.BytesReceived);
                                                                                                    formatFileSize = AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getReceivedBytesCount(DataUsageActivity.this.currentType, i2));
                                                                                                    if (i != DataUsageActivity.this.totalBytesReceivedRow) {
                                                                                                        z = true;
                                                                                                    }
                                                                                                    textSettingsCell.setTextAndValue(string, formatFileSize, z);
                                                                                                    return;
                                                                                                }
                                                                                                return;
                                                                                            }
                                                                                        }
                                                                                        textSettingsCell.setTextAndValue(LocaleController.getString("BytesSent", C0446R.string.BytesSent), AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentBytesCount(DataUsageActivity.this.currentType, i2)), true);
                                                                                        return;
                                                                                    }
                                                                                }
                                                                                textSettingsCell.setTextAndValue(LocaleController.getString("CountReceived", C0446R.string.CountReceived), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                                                                return;
                                                                            }
                                                                        }
                                                                        textSettingsCell.setTextAndValue(LocaleController.getString("CountSent", C0446R.string.CountSent), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                                                        return;
                                                                    }
                                                                }
                                                            }
                                                            i2 = 5;
                                                            if (i != DataUsageActivity.this.callsSentRow) {
                                                                textSettingsCell.setTextAndValue(LocaleController.getString("OutgoingCalls", C0446R.string.OutgoingCalls), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                                                return;
                                                            } else if (i != DataUsageActivity.this.callsReceivedRow) {
                                                                textSettingsCell.setTextAndValue(LocaleController.getString("IncomingCalls", C0446R.string.IncomingCalls), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                                                return;
                                                            } else if (i != DataUsageActivity.this.callsTotalTimeRow) {
                                                                i = StatsController.getInstance(DataUsageActivity.this.currentAccount).getCallsTotalTime(DataUsageActivity.this.currentType);
                                                                i2 = i / 3600;
                                                                i -= i2 * 3600;
                                                                i -= (i / 60) * 60;
                                                                if (i2 != 0) {
                                                                    i = String.format("%d:%02d", new Object[]{Integer.valueOf(r5), Integer.valueOf(i)});
                                                                } else {
                                                                    i = String.format("%d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(r5), Integer.valueOf(i)});
                                                                }
                                                                textSettingsCell.setTextAndValue(LocaleController.getString("CallsTotalTime", C0446R.string.CallsTotalTime), i, false);
                                                                return;
                                                            } else if (i == DataUsageActivity.this.filesSentRow) {
                                                                textSettingsCell.setTextAndValue(LocaleController.getString("CountSent", C0446R.string.CountSent), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                                                return;
                                                            } else if (i == DataUsageActivity.this.filesReceivedRow) {
                                                                textSettingsCell.setTextAndValue(LocaleController.getString("CountReceived", C0446R.string.CountReceived), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                                                return;
                                                            } else if (i != DataUsageActivity.this.totalBytesSentRow) {
                                                                if (i != DataUsageActivity.this.messagesBytesReceivedRow) {
                                                                    break;
                                                                }
                                                                string = LocaleController.getString("BytesReceived", C0446R.string.BytesReceived);
                                                                formatFileSize = AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getReceivedBytesCount(DataUsageActivity.this.currentType, i2));
                                                                if (i != DataUsageActivity.this.totalBytesReceivedRow) {
                                                                    z = true;
                                                                }
                                                                textSettingsCell.setTextAndValue(string, formatFileSize, z);
                                                                return;
                                                            } else {
                                                                textSettingsCell.setTextAndValue(LocaleController.getString("BytesSent", C0446R.string.BytesSent), AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentBytesCount(DataUsageActivity.this.currentType, i2)), true);
                                                                return;
                                                            }
                                                        }
                                                    }
                                                    i2 = 2;
                                                    if (i != DataUsageActivity.this.callsSentRow) {
                                                        textSettingsCell.setTextAndValue(LocaleController.getString("OutgoingCalls", C0446R.string.OutgoingCalls), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                                        return;
                                                    } else if (i != DataUsageActivity.this.callsReceivedRow) {
                                                        textSettingsCell.setTextAndValue(LocaleController.getString("IncomingCalls", C0446R.string.IncomingCalls), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                                        return;
                                                    } else if (i != DataUsageActivity.this.callsTotalTimeRow) {
                                                        i = StatsController.getInstance(DataUsageActivity.this.currentAccount).getCallsTotalTime(DataUsageActivity.this.currentType);
                                                        i2 = i / 3600;
                                                        i -= i2 * 3600;
                                                        i -= (i / 60) * 60;
                                                        if (i2 != 0) {
                                                            i = String.format("%d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(r5), Integer.valueOf(i)});
                                                        } else {
                                                            i = String.format("%d:%02d", new Object[]{Integer.valueOf(r5), Integer.valueOf(i)});
                                                        }
                                                        textSettingsCell.setTextAndValue(LocaleController.getString("CallsTotalTime", C0446R.string.CallsTotalTime), i, false);
                                                        return;
                                                    } else if (i == DataUsageActivity.this.filesSentRow) {
                                                        textSettingsCell.setTextAndValue(LocaleController.getString("CountSent", C0446R.string.CountSent), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                                        return;
                                                    } else if (i == DataUsageActivity.this.filesReceivedRow) {
                                                        textSettingsCell.setTextAndValue(LocaleController.getString("CountReceived", C0446R.string.CountReceived), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                                        return;
                                                    } else if (i != DataUsageActivity.this.totalBytesSentRow) {
                                                        textSettingsCell.setTextAndValue(LocaleController.getString("BytesSent", C0446R.string.BytesSent), AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentBytesCount(DataUsageActivity.this.currentType, i2)), true);
                                                        return;
                                                    } else {
                                                        if (i != DataUsageActivity.this.messagesBytesReceivedRow) {
                                                        }
                                                        string = LocaleController.getString("BytesReceived", C0446R.string.BytesReceived);
                                                        formatFileSize = AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getReceivedBytesCount(DataUsageActivity.this.currentType, i2));
                                                        if (i != DataUsageActivity.this.totalBytesReceivedRow) {
                                                            z = true;
                                                        }
                                                        textSettingsCell.setTextAndValue(string, formatFileSize, z);
                                                        return;
                                                    }
                                                }
                                            }
                                            i2 = 3;
                                            if (i != DataUsageActivity.this.callsSentRow) {
                                                textSettingsCell.setTextAndValue(LocaleController.getString("OutgoingCalls", C0446R.string.OutgoingCalls), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                                return;
                                            } else if (i != DataUsageActivity.this.callsReceivedRow) {
                                                textSettingsCell.setTextAndValue(LocaleController.getString("IncomingCalls", C0446R.string.IncomingCalls), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                                return;
                                            } else if (i != DataUsageActivity.this.callsTotalTimeRow) {
                                                i = StatsController.getInstance(DataUsageActivity.this.currentAccount).getCallsTotalTime(DataUsageActivity.this.currentType);
                                                i2 = i / 3600;
                                                i -= i2 * 3600;
                                                i -= (i / 60) * 60;
                                                if (i2 != 0) {
                                                    i = String.format("%d:%02d", new Object[]{Integer.valueOf(r5), Integer.valueOf(i)});
                                                } else {
                                                    i = String.format("%d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(r5), Integer.valueOf(i)});
                                                }
                                                textSettingsCell.setTextAndValue(LocaleController.getString("CallsTotalTime", C0446R.string.CallsTotalTime), i, false);
                                                return;
                                            } else if (i == DataUsageActivity.this.filesSentRow) {
                                                textSettingsCell.setTextAndValue(LocaleController.getString("CountSent", C0446R.string.CountSent), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                                return;
                                            } else if (i == DataUsageActivity.this.filesReceivedRow) {
                                                textSettingsCell.setTextAndValue(LocaleController.getString("CountReceived", C0446R.string.CountReceived), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                                return;
                                            } else if (i != DataUsageActivity.this.totalBytesSentRow) {
                                                if (i != DataUsageActivity.this.messagesBytesReceivedRow) {
                                                }
                                                string = LocaleController.getString("BytesReceived", C0446R.string.BytesReceived);
                                                formatFileSize = AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getReceivedBytesCount(DataUsageActivity.this.currentType, i2));
                                                if (i != DataUsageActivity.this.totalBytesReceivedRow) {
                                                    z = true;
                                                }
                                                textSettingsCell.setTextAndValue(string, formatFileSize, z);
                                                return;
                                            } else {
                                                textSettingsCell.setTextAndValue(LocaleController.getString("BytesSent", C0446R.string.BytesSent), AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentBytesCount(DataUsageActivity.this.currentType, i2)), true);
                                                return;
                                            }
                                        }
                                    }
                                    i2 = 4;
                                    if (i != DataUsageActivity.this.callsSentRow) {
                                        textSettingsCell.setTextAndValue(LocaleController.getString("OutgoingCalls", C0446R.string.OutgoingCalls), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                        return;
                                    } else if (i != DataUsageActivity.this.callsReceivedRow) {
                                        textSettingsCell.setTextAndValue(LocaleController.getString("IncomingCalls", C0446R.string.IncomingCalls), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                        return;
                                    } else if (i != DataUsageActivity.this.callsTotalTimeRow) {
                                        i = StatsController.getInstance(DataUsageActivity.this.currentAccount).getCallsTotalTime(DataUsageActivity.this.currentType);
                                        i2 = i / 3600;
                                        i -= i2 * 3600;
                                        i -= (i / 60) * 60;
                                        if (i2 != 0) {
                                            i = String.format("%d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(r5), Integer.valueOf(i)});
                                        } else {
                                            i = String.format("%d:%02d", new Object[]{Integer.valueOf(r5), Integer.valueOf(i)});
                                        }
                                        textSettingsCell.setTextAndValue(LocaleController.getString("CallsTotalTime", C0446R.string.CallsTotalTime), i, false);
                                        return;
                                    } else if (i == DataUsageActivity.this.filesSentRow) {
                                        textSettingsCell.setTextAndValue(LocaleController.getString("CountSent", C0446R.string.CountSent), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                        return;
                                    } else if (i == DataUsageActivity.this.filesReceivedRow) {
                                        textSettingsCell.setTextAndValue(LocaleController.getString("CountReceived", C0446R.string.CountReceived), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                        return;
                                    } else if (i != DataUsageActivity.this.totalBytesSentRow) {
                                        textSettingsCell.setTextAndValue(LocaleController.getString("BytesSent", C0446R.string.BytesSent), AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentBytesCount(DataUsageActivity.this.currentType, i2)), true);
                                        return;
                                    } else {
                                        if (i != DataUsageActivity.this.messagesBytesReceivedRow) {
                                        }
                                        string = LocaleController.getString("BytesReceived", C0446R.string.BytesReceived);
                                        formatFileSize = AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getReceivedBytesCount(DataUsageActivity.this.currentType, i2));
                                        if (i != DataUsageActivity.this.totalBytesReceivedRow) {
                                            z = true;
                                        }
                                        textSettingsCell.setTextAndValue(string, formatFileSize, z);
                                        return;
                                    }
                                }
                            }
                            i2 = 1;
                            if (i != DataUsageActivity.this.callsSentRow) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("OutgoingCalls", C0446R.string.OutgoingCalls), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                return;
                            } else if (i != DataUsageActivity.this.callsReceivedRow) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("IncomingCalls", C0446R.string.IncomingCalls), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                return;
                            } else if (i != DataUsageActivity.this.callsTotalTimeRow) {
                                i = StatsController.getInstance(DataUsageActivity.this.currentAccount).getCallsTotalTime(DataUsageActivity.this.currentType);
                                i2 = i / 3600;
                                i -= i2 * 3600;
                                i -= (i / 60) * 60;
                                if (i2 != 0) {
                                    i = String.format("%d:%02d", new Object[]{Integer.valueOf(r5), Integer.valueOf(i)});
                                } else {
                                    i = String.format("%d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(r5), Integer.valueOf(i)});
                                }
                                textSettingsCell.setTextAndValue(LocaleController.getString("CallsTotalTime", C0446R.string.CallsTotalTime), i, false);
                                return;
                            } else if (i == DataUsageActivity.this.filesSentRow) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("CountSent", C0446R.string.CountSent), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                return;
                            } else if (i == DataUsageActivity.this.filesReceivedRow) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("CountReceived", C0446R.string.CountReceived), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                                return;
                            } else if (i != DataUsageActivity.this.totalBytesSentRow) {
                                if (i != DataUsageActivity.this.messagesBytesReceivedRow) {
                                }
                                string = LocaleController.getString("BytesReceived", C0446R.string.BytesReceived);
                                formatFileSize = AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getReceivedBytesCount(DataUsageActivity.this.currentType, i2));
                                if (i != DataUsageActivity.this.totalBytesReceivedRow) {
                                    z = true;
                                }
                                textSettingsCell.setTextAndValue(string, formatFileSize, z);
                                return;
                            } else {
                                textSettingsCell.setTextAndValue(LocaleController.getString("BytesSent", C0446R.string.BytesSent), AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentBytesCount(DataUsageActivity.this.currentType, i2)), true);
                                return;
                            }
                        }
                    }
                    i2 = 0;
                    if (i != DataUsageActivity.this.callsSentRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("OutgoingCalls", C0446R.string.OutgoingCalls), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                        return;
                    } else if (i != DataUsageActivity.this.callsReceivedRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("IncomingCalls", C0446R.string.IncomingCalls), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                        return;
                    } else if (i != DataUsageActivity.this.callsTotalTimeRow) {
                        i = StatsController.getInstance(DataUsageActivity.this.currentAccount).getCallsTotalTime(DataUsageActivity.this.currentType);
                        i2 = i / 3600;
                        i -= i2 * 3600;
                        i -= (i / 60) * 60;
                        if (i2 != 0) {
                            i = String.format("%d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(r5), Integer.valueOf(i)});
                        } else {
                            i = String.format("%d:%02d", new Object[]{Integer.valueOf(r5), Integer.valueOf(i)});
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("CallsTotalTime", C0446R.string.CallsTotalTime), i, false);
                        return;
                    } else if (i == DataUsageActivity.this.filesSentRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("CountSent", C0446R.string.CountSent), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                        return;
                    } else if (i == DataUsageActivity.this.filesReceivedRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("CountReceived", C0446R.string.CountReceived), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(DataUsageActivity.this.currentType, i2))}), true);
                        return;
                    } else if (i != DataUsageActivity.this.totalBytesSentRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("BytesSent", C0446R.string.BytesSent), AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentBytesCount(DataUsageActivity.this.currentType, i2)), true);
                        return;
                    } else {
                        if (i != DataUsageActivity.this.messagesBytesReceivedRow) {
                        }
                        string = LocaleController.getString("BytesReceived", C0446R.string.BytesReceived);
                        formatFileSize = AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getReceivedBytesCount(DataUsageActivity.this.currentType, i2));
                        if (i != DataUsageActivity.this.totalBytesReceivedRow) {
                            z = true;
                        }
                        textSettingsCell.setTextAndValue(string, formatFileSize, z);
                        return;
                    }
                case 2:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == DataUsageActivity.this.totalSectionRow) {
                        headerCell.setText(LocaleController.getString("TotalDataUsage", C0446R.string.TotalDataUsage));
                        return;
                    } else if (i == DataUsageActivity.this.callsSectionRow) {
                        headerCell.setText(LocaleController.getString("CallsDataUsage", C0446R.string.CallsDataUsage));
                        return;
                    } else if (i == DataUsageActivity.this.filesSectionRow) {
                        headerCell.setText(LocaleController.getString("FilesDataUsage", C0446R.string.FilesDataUsage));
                        return;
                    } else if (i == DataUsageActivity.this.audiosSectionRow) {
                        headerCell.setText(LocaleController.getString("LocalAudioCache", C0446R.string.LocalAudioCache));
                        return;
                    } else if (i == DataUsageActivity.this.videosSectionRow) {
                        headerCell.setText(LocaleController.getString("LocalVideoCache", C0446R.string.LocalVideoCache));
                        return;
                    } else if (i == DataUsageActivity.this.photosSectionRow) {
                        headerCell.setText(LocaleController.getString("LocalPhotoCache", C0446R.string.LocalPhotoCache));
                        return;
                    } else if (i == DataUsageActivity.this.messagesSectionRow) {
                        headerCell.setText(LocaleController.getString("MessagesDataUsage", C0446R.string.MessagesDataUsage));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    textInfoPrivacyCell.setText(LocaleController.formatString("NetworkUsageSince", C0446R.string.NetworkUsageSince, LocaleController.getInstance().formatterStats.format(StatsController.getInstance(DataUsageActivity.this.currentAccount).getResetStatsDate(DataUsageActivity.this.currentType))));
                    return;
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() == DataUsageActivity.this.resetRow ? true : null;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new ShadowSectionCell(this.mContext);
                    break;
                case 1:
                    viewGroup = new TextSettingsCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    viewGroup = new HeaderCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    viewGroup = new TextInfoPrivacyCell(this.mContext);
                    break;
                default:
                    viewGroup = null;
                    break;
            }
            viewGroup.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(viewGroup);
        }

        public int getItemViewType(int i) {
            if (i == DataUsageActivity.this.resetSection2Row) {
                return 3;
            }
            if (!(i == DataUsageActivity.this.resetSection2Row || i == DataUsageActivity.this.callsSection2Row || i == DataUsageActivity.this.filesSection2Row || i == DataUsageActivity.this.audiosSection2Row || i == DataUsageActivity.this.videosSection2Row || i == DataUsageActivity.this.photosSection2Row || i == DataUsageActivity.this.messagesSection2Row)) {
                if (i != DataUsageActivity.this.totalSection2Row) {
                    if (!(i == DataUsageActivity.this.totalSectionRow || i == DataUsageActivity.this.callsSectionRow || i == DataUsageActivity.this.filesSectionRow || i == DataUsageActivity.this.audiosSectionRow || i == DataUsageActivity.this.videosSectionRow || i == DataUsageActivity.this.photosSectionRow)) {
                        if (i != DataUsageActivity.this.messagesSectionRow) {
                            return 1;
                        }
                    }
                    return 2;
                }
            }
            return 0;
        }
    }

    public DataUsageActivity(int i) {
        this.currentType = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.photosSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.photosSentRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.photosReceivedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.photosBytesSentRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.photosBytesReceivedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.photosSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.videosSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.videosSentRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.videosReceivedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.videosBytesSentRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.videosBytesReceivedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.videosSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.audiosSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.audiosSentRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.audiosReceivedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.audiosBytesSentRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.audiosBytesReceivedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.audiosSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.filesSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.filesSentRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.filesReceivedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.filesBytesSentRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.filesBytesReceivedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.filesSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsSentRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsReceivedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsBytesSentRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsBytesReceivedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsTotalTimeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagesSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagesBytesSentRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagesBytesReceivedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagesSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.totalSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.totalBytesSentRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.totalBytesReceivedRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.totalSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetSection2Row = i;
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("MobileUsage", C0446R.string.MobileUsage));
        } else if (this.currentType == 1) {
            this.actionBar.setTitle(LocaleController.getString("WiFiUsage", C0446R.string.WiFiUsage));
        } else if (this.currentType == 2) {
            this.actionBar.setTitle(LocaleController.getString("RoamingUsage", C0446R.string.RoamingUsage));
        }
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new C21251());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C21262());
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[16];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r1[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r1[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r1[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r1[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r1[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r1[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r1[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r1[10] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r1[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r1[12] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r1[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[14] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r1[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText2);
        return r1;
    }
}
