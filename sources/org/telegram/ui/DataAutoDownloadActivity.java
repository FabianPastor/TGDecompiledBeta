package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.MaxFileSizeCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckBoxCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class DataAutoDownloadActivity extends BaseFragment {
    private static final int done_button = 1;
    private int currentType;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int mChannelsRow;
    private int mContactsRow;
    private int mGroupRow;
    private int mPrivateRow;
    private int mSizeRow;
    private long maxSize;
    private int mobileDataChannelDownloadMask;
    private int mobileDataDownloadMask;
    private int mobileDataGroupDownloadMask;
    private int mobileDataPrivateDownloadMask;
    private int mobileMaxSize;
    private int mobileSection2Row;
    private int mobileSectionRow;
    private int rChannelsRow;
    private int rContactsRow;
    private int rGroupRow;
    private int rPrivateRow;
    private int rSizeRow;
    private int roamingChannelDownloadMask;
    private int roamingDownloadMask;
    private int roamingGroupDownloadMask;
    private int roamingMaxSize;
    private int roamingPrivateDownloadMask;
    private int roamingSection2Row;
    private int roamingSectionRow;
    private int rowCount;
    private int wChannelsRow;
    private int wContactsRow;
    private int wGroupRow;
    private int wPrivateRow;
    private int wSizeRow;
    private int wifiChannelDownloadMask;
    private int wifiDownloadMask;
    private int wifiGroupDownloadMask;
    private int wifiMaxSize;
    private int wifiPrivateDownloadMask;
    private int wifiSection2Row;
    private int wifiSectionRow;

    /* renamed from: org.telegram.ui.DataAutoDownloadActivity$1 */
    class C21331 extends ActionBarMenuOnItemClick {
        C21331() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                DataAutoDownloadActivity.this.finishFragment();
            } else if (id == 1) {
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).mobileDataDownloadMask[0] = DataAutoDownloadActivity.this.mobileDataDownloadMask;
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).mobileDataDownloadMask[1] = DataAutoDownloadActivity.this.mobileDataPrivateDownloadMask;
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).mobileDataDownloadMask[2] = DataAutoDownloadActivity.this.mobileDataGroupDownloadMask;
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).mobileDataDownloadMask[3] = DataAutoDownloadActivity.this.mobileDataChannelDownloadMask;
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).wifiDownloadMask[0] = DataAutoDownloadActivity.this.wifiDownloadMask;
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).wifiDownloadMask[1] = DataAutoDownloadActivity.this.wifiPrivateDownloadMask;
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).wifiDownloadMask[2] = DataAutoDownloadActivity.this.wifiGroupDownloadMask;
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).wifiDownloadMask[3] = DataAutoDownloadActivity.this.wifiChannelDownloadMask;
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).roamingDownloadMask[0] = DataAutoDownloadActivity.this.roamingDownloadMask;
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).roamingDownloadMask[1] = DataAutoDownloadActivity.this.roamingPrivateDownloadMask;
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).roamingDownloadMask[2] = DataAutoDownloadActivity.this.roamingGroupDownloadMask;
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).roamingDownloadMask[3] = DataAutoDownloadActivity.this.roamingChannelDownloadMask;
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).mobileMaxFileSize[DownloadController.maskToIndex(DataAutoDownloadActivity.this.currentType)] = DataAutoDownloadActivity.this.mobileMaxSize;
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).wifiMaxFileSize[DownloadController.maskToIndex(DataAutoDownloadActivity.this.currentType)] = DataAutoDownloadActivity.this.wifiMaxSize;
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).roamingMaxFileSize[DownloadController.maskToIndex(DataAutoDownloadActivity.this.currentType)] = DataAutoDownloadActivity.this.roamingMaxSize;
                Editor editor = MessagesController.getMainSettings(DataAutoDownloadActivity.this.currentAccount).edit();
                int a = 0;
                while (a < 4) {
                    editor.putInt("mobileDataDownloadMask" + (a != 0 ? Integer.valueOf(a) : TtmlNode.ANONYMOUS_REGION_ID), DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).mobileDataDownloadMask[a]);
                    editor.putInt("wifiDownloadMask" + (a != 0 ? Integer.valueOf(a) : TtmlNode.ANONYMOUS_REGION_ID), DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).wifiDownloadMask[a]);
                    editor.putInt("roamingDownloadMask" + (a != 0 ? Integer.valueOf(a) : TtmlNode.ANONYMOUS_REGION_ID), DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).roamingDownloadMask[a]);
                    a++;
                }
                editor.putInt("mobileMaxDownloadSize" + DownloadController.maskToIndex(DataAutoDownloadActivity.this.currentType), DataAutoDownloadActivity.this.mobileMaxSize);
                editor.putInt("wifiMaxDownloadSize" + DownloadController.maskToIndex(DataAutoDownloadActivity.this.currentType), DataAutoDownloadActivity.this.wifiMaxSize);
                editor.putInt("roamingMaxDownloadSize" + DownloadController.maskToIndex(DataAutoDownloadActivity.this.currentType), DataAutoDownloadActivity.this.roamingMaxSize);
                editor.commit();
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).checkAutodownloadSettings();
                DataAutoDownloadActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.DataAutoDownloadActivity$2 */
    class C21342 implements OnItemClickListener {
        C21342() {
        }

        public void onItemClick(View view, int position) {
            if (view instanceof TextCheckBoxCell) {
                int mask = DataAutoDownloadActivity.this.getMaskForRow(position);
                TextCheckBoxCell textCell = (TextCheckBoxCell) view;
                boolean isChecked = !textCell.isChecked();
                if (isChecked) {
                    mask |= DataAutoDownloadActivity.this.currentType;
                } else {
                    mask &= DataAutoDownloadActivity.this.currentType ^ -1;
                }
                DataAutoDownloadActivity.this.setMaskForRow(position, mask);
                textCell.setChecked(isChecked);
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return DataAutoDownloadActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = false;
            boolean z2 = true;
            switch (holder.getItemViewType()) {
                case 0:
                    if (position == DataAutoDownloadActivity.this.mobileSection2Row || position == DataAutoDownloadActivity.this.wifiSection2Row) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 1:
                    TextCheckBoxCell textCell = holder.itemView;
                    String string;
                    if (position == DataAutoDownloadActivity.this.mContactsRow || position == DataAutoDownloadActivity.this.wContactsRow || position == DataAutoDownloadActivity.this.rContactsRow) {
                        string = LocaleController.getString("AutodownloadContacts", R.string.AutodownloadContacts);
                        if ((DataAutoDownloadActivity.this.getMaskForRow(position) & DataAutoDownloadActivity.this.currentType) != 0) {
                            z = true;
                        }
                        textCell.setTextAndCheck(string, z, true);
                        return;
                    } else if (position == DataAutoDownloadActivity.this.mPrivateRow || position == DataAutoDownloadActivity.this.wPrivateRow || position == DataAutoDownloadActivity.this.rPrivateRow) {
                        string = LocaleController.getString("AutodownloadPrivateChats", R.string.AutodownloadPrivateChats);
                        if ((DataAutoDownloadActivity.this.getMaskForRow(position) & DataAutoDownloadActivity.this.currentType) != 0) {
                            z = true;
                        }
                        textCell.setTextAndCheck(string, z, true);
                        return;
                    } else if (position == DataAutoDownloadActivity.this.mChannelsRow || position == DataAutoDownloadActivity.this.wChannelsRow || position == DataAutoDownloadActivity.this.rChannelsRow) {
                        String string2 = LocaleController.getString("AutodownloadChannels", R.string.AutodownloadChannels);
                        boolean z3 = (DataAutoDownloadActivity.this.getMaskForRow(position) & DataAutoDownloadActivity.this.currentType) != 0;
                        if (DataAutoDownloadActivity.this.mSizeRow == -1) {
                            z2 = false;
                        }
                        textCell.setTextAndCheck(string2, z3, z2);
                        return;
                    } else if (position == DataAutoDownloadActivity.this.mGroupRow || position == DataAutoDownloadActivity.this.wGroupRow || position == DataAutoDownloadActivity.this.rGroupRow) {
                        string = LocaleController.getString("AutodownloadGroupChats", R.string.AutodownloadGroupChats);
                        if ((DataAutoDownloadActivity.this.getMaskForRow(position) & DataAutoDownloadActivity.this.currentType) != 0) {
                            z = true;
                        }
                        textCell.setTextAndCheck(string, z, true);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = holder.itemView;
                    if (position == DataAutoDownloadActivity.this.mobileSectionRow) {
                        headerCell.setText(LocaleController.getString("WhenUsingMobileData", R.string.WhenUsingMobileData));
                        return;
                    } else if (position == DataAutoDownloadActivity.this.wifiSectionRow) {
                        headerCell.setText(LocaleController.getString("WhenConnectedOnWiFi", R.string.WhenConnectedOnWiFi));
                        return;
                    } else if (position == DataAutoDownloadActivity.this.roamingSectionRow) {
                        headerCell.setText(LocaleController.getString("WhenRoaming", R.string.WhenRoaming));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    MaxFileSizeCell cell = holder.itemView;
                    if (position == DataAutoDownloadActivity.this.mSizeRow) {
                        cell.setSize((long) DataAutoDownloadActivity.this.mobileMaxSize, DataAutoDownloadActivity.this.maxSize);
                        cell.setTag(Integer.valueOf(0));
                        return;
                    } else if (position == DataAutoDownloadActivity.this.wSizeRow) {
                        cell.setSize((long) DataAutoDownloadActivity.this.wifiMaxSize, DataAutoDownloadActivity.this.maxSize);
                        cell.setTag(Integer.valueOf(1));
                        return;
                    } else if (position == DataAutoDownloadActivity.this.rSizeRow) {
                        cell.setSize((long) DataAutoDownloadActivity.this.roamingMaxSize, DataAutoDownloadActivity.this.maxSize);
                        cell.setTag(Integer.valueOf(2));
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return (position == DataAutoDownloadActivity.this.mSizeRow || position == DataAutoDownloadActivity.this.rSizeRow || position == DataAutoDownloadActivity.this.wSizeRow || position == DataAutoDownloadActivity.this.mobileSectionRow || position == DataAutoDownloadActivity.this.wifiSectionRow || position == DataAutoDownloadActivity.this.roamingSectionRow || position == DataAutoDownloadActivity.this.mobileSection2Row || position == DataAutoDownloadActivity.this.wifiSection2Row || position == DataAutoDownloadActivity.this.roamingSection2Row) ? false : true;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 1:
                    view = new TextCheckBoxCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new MaxFileSizeCell(this.mContext) {
                        protected void didChangedSizeValue(int value) {
                            Integer tag = (Integer) getTag();
                            if (tag.intValue() == 0) {
                                DataAutoDownloadActivity.this.mobileMaxSize = value;
                            } else if (tag.intValue() == 1) {
                                DataAutoDownloadActivity.this.wifiMaxSize = value;
                            } else if (tag.intValue() == 2) {
                                DataAutoDownloadActivity.this.roamingMaxSize = value;
                            }
                        }
                    };
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == DataAutoDownloadActivity.this.mobileSection2Row || position == DataAutoDownloadActivity.this.wifiSection2Row || position == DataAutoDownloadActivity.this.roamingSection2Row) {
                return 0;
            }
            if (position == DataAutoDownloadActivity.this.mobileSectionRow || position == DataAutoDownloadActivity.this.wifiSectionRow || position == DataAutoDownloadActivity.this.roamingSectionRow) {
                return 2;
            }
            if (position == DataAutoDownloadActivity.this.wSizeRow || position == DataAutoDownloadActivity.this.mSizeRow || position == DataAutoDownloadActivity.this.rSizeRow) {
                return 3;
            }
            return 1;
        }
    }

    public DataAutoDownloadActivity(int type) {
        this.currentType = type;
        if (this.currentType == 64) {
            this.maxSize = 8388608;
        } else if (this.currentType == 32) {
            this.maxSize = 10485760;
        } else {
            this.maxSize = NUM;
        }
        this.mobileDataDownloadMask = DownloadController.getInstance(this.currentAccount).mobileDataDownloadMask[0];
        this.mobileDataPrivateDownloadMask = DownloadController.getInstance(this.currentAccount).mobileDataDownloadMask[1];
        this.mobileDataGroupDownloadMask = DownloadController.getInstance(this.currentAccount).mobileDataDownloadMask[2];
        this.mobileDataChannelDownloadMask = DownloadController.getInstance(this.currentAccount).mobileDataDownloadMask[3];
        this.wifiDownloadMask = DownloadController.getInstance(this.currentAccount).wifiDownloadMask[0];
        this.wifiPrivateDownloadMask = DownloadController.getInstance(this.currentAccount).wifiDownloadMask[1];
        this.wifiGroupDownloadMask = DownloadController.getInstance(this.currentAccount).wifiDownloadMask[2];
        this.wifiChannelDownloadMask = DownloadController.getInstance(this.currentAccount).wifiDownloadMask[3];
        this.roamingDownloadMask = DownloadController.getInstance(this.currentAccount).roamingDownloadMask[0];
        this.roamingPrivateDownloadMask = DownloadController.getInstance(this.currentAccount).roamingDownloadMask[1];
        this.roamingGroupDownloadMask = DownloadController.getInstance(this.currentAccount).roamingDownloadMask[2];
        this.roamingChannelDownloadMask = DownloadController.getInstance(this.currentAccount).roamingDownloadMask[2];
        this.mobileMaxSize = DownloadController.getInstance(this.currentAccount).mobileMaxFileSize[DownloadController.maskToIndex(this.currentType)];
        this.wifiMaxSize = DownloadController.getInstance(this.currentAccount).wifiMaxFileSize[DownloadController.maskToIndex(this.currentType)];
        this.roamingMaxSize = DownloadController.getInstance(this.currentAccount).roamingMaxFileSize[DownloadController.maskToIndex(this.currentType)];
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.mobileSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.mContactsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.mPrivateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.mGroupRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.mChannelsRow = i;
        if (this.currentType != 1) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.mSizeRow = i;
        } else {
            this.mSizeRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.mobileSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.wifiSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.wContactsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.wPrivateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.wGroupRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.wChannelsRow = i;
        if (this.currentType != 1) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.wSizeRow = i;
        } else {
            this.wSizeRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.wifiSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.roamingSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rContactsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rPrivateRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rGroupRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rChannelsRow = i;
        if (this.currentType != 1) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.rSizeRow = i;
        } else {
            this.rSizeRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.roamingSection2Row = i;
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        if (this.currentType == 1) {
            this.actionBar.setTitle(LocaleController.getString("LocalPhotoCache", R.string.LocalPhotoCache));
        } else if (this.currentType == 2) {
            this.actionBar.setTitle(LocaleController.getString("AudioAutodownload", R.string.AudioAutodownload));
        } else if (this.currentType == 64) {
            this.actionBar.setTitle(LocaleController.getString("VideoMessagesAutodownload", R.string.VideoMessagesAutodownload));
        } else if (this.currentType == 4) {
            this.actionBar.setTitle(LocaleController.getString("LocalVideoCache", R.string.LocalVideoCache));
        } else if (this.currentType == 8) {
            this.actionBar.setTitle(LocaleController.getString("FilesDataUsage", R.string.FilesDataUsage));
        } else if (this.currentType == 16) {
            this.actionBar.setTitle(LocaleController.getString("AttachMusic", R.string.AttachMusic));
        } else if (this.currentType == 32) {
            this.actionBar.setTitle(LocaleController.getString("LocalGifCache", R.string.LocalGifCache));
        }
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C21331());
        this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C21342());
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private int getMaskForRow(int position) {
        if (position == this.mContactsRow) {
            return this.mobileDataDownloadMask;
        }
        if (position == this.mPrivateRow) {
            return this.mobileDataPrivateDownloadMask;
        }
        if (position == this.mGroupRow) {
            return this.mobileDataGroupDownloadMask;
        }
        if (position == this.mChannelsRow) {
            return this.mobileDataChannelDownloadMask;
        }
        if (position == this.wContactsRow) {
            return this.wifiDownloadMask;
        }
        if (position == this.wPrivateRow) {
            return this.wifiPrivateDownloadMask;
        }
        if (position == this.wGroupRow) {
            return this.wifiGroupDownloadMask;
        }
        if (position == this.wChannelsRow) {
            return this.wifiChannelDownloadMask;
        }
        if (position == this.rContactsRow) {
            return this.roamingDownloadMask;
        }
        if (position == this.rPrivateRow) {
            return this.roamingPrivateDownloadMask;
        }
        if (position == this.rGroupRow) {
            return this.roamingGroupDownloadMask;
        }
        if (position == this.rChannelsRow) {
            return this.roamingChannelDownloadMask;
        }
        return 0;
    }

    private void setMaskForRow(int position, int mask) {
        if (position == this.mContactsRow) {
            this.mobileDataDownloadMask = mask;
        } else if (position == this.mPrivateRow) {
            this.mobileDataPrivateDownloadMask = mask;
        } else if (position == this.mGroupRow) {
            this.mobileDataGroupDownloadMask = mask;
        } else if (position == this.mChannelsRow) {
            this.mobileDataChannelDownloadMask = mask;
        } else if (position == this.wContactsRow) {
            this.wifiDownloadMask = mask;
        } else if (position == this.wPrivateRow) {
            this.wifiPrivateDownloadMask = mask;
        } else if (position == this.wGroupRow) {
            this.wifiGroupDownloadMask = mask;
        } else if (position == this.wChannelsRow) {
            this.wifiChannelDownloadMask = mask;
        } else if (position == this.rContactsRow) {
            this.roamingDownloadMask = mask;
        } else if (position == this.rPrivateRow) {
            this.roamingPrivateDownloadMask = mask;
        } else if (position == this.rGroupRow) {
            this.roamingGroupDownloadMask = mask;
        } else if (position == this.rChannelsRow) {
            this.roamingChannelDownloadMask = mask;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r9 = new ThemeDescription[18];
        r9[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCheckBoxCell.class, MaxFileSizeCell.class, HeaderCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r9[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r9[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r9[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r9[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r9[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r9[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r9[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r9[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r9[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r9[10] = new ThemeDescription(this.listView, 0, new Class[]{MaxFileSizeCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[11] = new ThemeDescription(this.listView, 0, new Class[]{MaxFileSizeCell.class}, new String[]{"sizeTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[12] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[13] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, Theme.key_checkboxSquareUnchecked);
        r9[14] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, Theme.key_checkboxSquareDisabled);
        r9[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, Theme.key_checkboxSquareBackground);
        r9[16] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, Theme.key_checkboxSquareCheck);
        r9[17] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        return r9;
    }
}
