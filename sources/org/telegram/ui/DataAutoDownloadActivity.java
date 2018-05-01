package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
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
    class C21201 extends ActionBarMenuOnItemClick {
        C21201() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                DataAutoDownloadActivity.this.finishFragment();
            } else if (i == 1) {
                StringBuilder stringBuilder;
                int i2 = 0;
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
                i = MessagesController.getMainSettings(DataAutoDownloadActivity.this.currentAccount).edit();
                while (i2 < 4) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("mobileDataDownloadMask");
                    stringBuilder.append(i2 != 0 ? Integer.valueOf(i2) : TtmlNode.ANONYMOUS_REGION_ID);
                    i.putInt(stringBuilder.toString(), DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).mobileDataDownloadMask[i2]);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("wifiDownloadMask");
                    stringBuilder.append(i2 != 0 ? Integer.valueOf(i2) : TtmlNode.ANONYMOUS_REGION_ID);
                    i.putInt(stringBuilder.toString(), DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).wifiDownloadMask[i2]);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("roamingDownloadMask");
                    stringBuilder.append(i2 != 0 ? Integer.valueOf(i2) : TtmlNode.ANONYMOUS_REGION_ID);
                    i.putInt(stringBuilder.toString(), DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).roamingDownloadMask[i2]);
                    i2++;
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append("mobileMaxDownloadSize");
                stringBuilder.append(DownloadController.maskToIndex(DataAutoDownloadActivity.this.currentType));
                i.putInt(stringBuilder.toString(), DataAutoDownloadActivity.this.mobileMaxSize);
                stringBuilder = new StringBuilder();
                stringBuilder.append("wifiMaxDownloadSize");
                stringBuilder.append(DownloadController.maskToIndex(DataAutoDownloadActivity.this.currentType));
                i.putInt(stringBuilder.toString(), DataAutoDownloadActivity.this.wifiMaxSize);
                stringBuilder = new StringBuilder();
                stringBuilder.append("roamingMaxDownloadSize");
                stringBuilder.append(DownloadController.maskToIndex(DataAutoDownloadActivity.this.currentType));
                i.putInt(stringBuilder.toString(), DataAutoDownloadActivity.this.roamingMaxSize);
                i.commit();
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).checkAutodownloadSettings();
                DataAutoDownloadActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.DataAutoDownloadActivity$2 */
    class C21212 implements OnItemClickListener {
        C21212() {
        }

        public void onItemClick(View view, int i) {
            if (view instanceof TextCheckBoxCell) {
                int access$3600 = DataAutoDownloadActivity.this.getMaskForRow(i);
                TextCheckBoxCell textCheckBoxCell = (TextCheckBoxCell) view;
                boolean isChecked = textCheckBoxCell.isChecked() ^ 1;
                if (isChecked) {
                    access$3600 |= DataAutoDownloadActivity.this.currentType;
                } else {
                    access$3600 &= DataAutoDownloadActivity.this.currentType ^ -1;
                }
                DataAutoDownloadActivity.this.setMaskForRow(i, access$3600);
                textCheckBoxCell.setChecked(isChecked);
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

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    if (i != DataAutoDownloadActivity.this.mobileSection2Row) {
                        if (i != DataAutoDownloadActivity.this.wifiSection2Row) {
                            viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                            return;
                        }
                    }
                    viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    return;
                case 1:
                    String string;
                    TextCheckBoxCell textCheckBoxCell = (TextCheckBoxCell) viewHolder.itemView;
                    if (!(i == DataAutoDownloadActivity.this.mContactsRow || i == DataAutoDownloadActivity.this.wContactsRow)) {
                        if (i != DataAutoDownloadActivity.this.rContactsRow) {
                            if (!(i == DataAutoDownloadActivity.this.mPrivateRow || i == DataAutoDownloadActivity.this.wPrivateRow)) {
                                if (i != DataAutoDownloadActivity.this.rPrivateRow) {
                                    if (!(i == DataAutoDownloadActivity.this.mChannelsRow || i == DataAutoDownloadActivity.this.wChannelsRow)) {
                                        if (i != DataAutoDownloadActivity.this.rChannelsRow) {
                                            if (i == DataAutoDownloadActivity.this.mGroupRow || i == DataAutoDownloadActivity.this.wGroupRow || i == DataAutoDownloadActivity.this.rGroupRow) {
                                                string = LocaleController.getString("AutodownloadGroupChats", C0446R.string.AutodownloadGroupChats);
                                                if ((DataAutoDownloadActivity.this.getMaskForRow(i) & DataAutoDownloadActivity.this.currentType) != 0) {
                                                    z = true;
                                                }
                                                textCheckBoxCell.setTextAndCheck(string, z, true);
                                                return;
                                            }
                                            return;
                                        }
                                    }
                                    string = LocaleController.getString("AutodownloadChannels", C0446R.string.AutodownloadChannels);
                                    i = (DataAutoDownloadActivity.this.getMaskForRow(i) & DataAutoDownloadActivity.this.currentType) != 0 ? 1 : 0;
                                    if (DataAutoDownloadActivity.this.mSizeRow != -1) {
                                        z = true;
                                    }
                                    textCheckBoxCell.setTextAndCheck(string, i, z);
                                    return;
                                }
                            }
                            string = LocaleController.getString("AutodownloadPrivateChats", C0446R.string.AutodownloadPrivateChats);
                            if ((DataAutoDownloadActivity.this.getMaskForRow(i) & DataAutoDownloadActivity.this.currentType) != 0) {
                                z = true;
                            }
                            textCheckBoxCell.setTextAndCheck(string, z, true);
                            return;
                        }
                    }
                    string = LocaleController.getString("AutodownloadContacts", C0446R.string.AutodownloadContacts);
                    if ((DataAutoDownloadActivity.this.getMaskForRow(i) & DataAutoDownloadActivity.this.currentType) != 0) {
                        z = true;
                    }
                    textCheckBoxCell.setTextAndCheck(string, z, true);
                    return;
                case 2:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == DataAutoDownloadActivity.this.mobileSectionRow) {
                        headerCell.setText(LocaleController.getString("WhenUsingMobileData", C0446R.string.WhenUsingMobileData));
                        return;
                    } else if (i == DataAutoDownloadActivity.this.wifiSectionRow) {
                        headerCell.setText(LocaleController.getString("WhenConnectedOnWiFi", C0446R.string.WhenConnectedOnWiFi));
                        return;
                    } else if (i == DataAutoDownloadActivity.this.roamingSectionRow) {
                        headerCell.setText(LocaleController.getString("WhenRoaming", C0446R.string.WhenRoaming));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    MaxFileSizeCell maxFileSizeCell = (MaxFileSizeCell) viewHolder.itemView;
                    if (i == DataAutoDownloadActivity.this.mSizeRow) {
                        maxFileSizeCell.setSize((long) DataAutoDownloadActivity.this.mobileMaxSize, DataAutoDownloadActivity.this.maxSize);
                        maxFileSizeCell.setTag(Integer.valueOf(0));
                        return;
                    } else if (i == DataAutoDownloadActivity.this.wSizeRow) {
                        maxFileSizeCell.setSize((long) DataAutoDownloadActivity.this.wifiMaxSize, DataAutoDownloadActivity.this.maxSize);
                        maxFileSizeCell.setTag(Integer.valueOf(1));
                        return;
                    } else if (i == DataAutoDownloadActivity.this.rSizeRow) {
                        maxFileSizeCell.setSize((long) DataAutoDownloadActivity.this.roamingMaxSize, DataAutoDownloadActivity.this.maxSize);
                        maxFileSizeCell.setTag(Integer.valueOf(2));
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getAdapterPosition();
            return (viewHolder == DataAutoDownloadActivity.this.mSizeRow || viewHolder == DataAutoDownloadActivity.this.rSizeRow || viewHolder == DataAutoDownloadActivity.this.wSizeRow || viewHolder == DataAutoDownloadActivity.this.mobileSectionRow || viewHolder == DataAutoDownloadActivity.this.wifiSectionRow || viewHolder == DataAutoDownloadActivity.this.roamingSectionRow || viewHolder == DataAutoDownloadActivity.this.mobileSection2Row || viewHolder == DataAutoDownloadActivity.this.wifiSection2Row || viewHolder == DataAutoDownloadActivity.this.roamingSection2Row) ? null : true;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new ShadowSectionCell(this.mContext);
                    break;
                case 1:
                    viewGroup = new TextCheckBoxCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    viewGroup = new HeaderCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    viewGroup = new MaxFileSizeCell(this.mContext) {
                        protected void didChangedSizeValue(int i) {
                            Integer num = (Integer) getTag();
                            if (num.intValue() == 0) {
                                DataAutoDownloadActivity.this.mobileMaxSize = i;
                            } else if (num.intValue() == 1) {
                                DataAutoDownloadActivity.this.wifiMaxSize = i;
                            } else if (num.intValue() == 2) {
                                DataAutoDownloadActivity.this.roamingMaxSize = i;
                            }
                        }
                    };
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    viewGroup = null;
                    break;
            }
            viewGroup.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(viewGroup);
        }

        public int getItemViewType(int i) {
            if (!(i == DataAutoDownloadActivity.this.mobileSection2Row || i == DataAutoDownloadActivity.this.wifiSection2Row)) {
                if (i != DataAutoDownloadActivity.this.roamingSection2Row) {
                    if (!(i == DataAutoDownloadActivity.this.mobileSectionRow || i == DataAutoDownloadActivity.this.wifiSectionRow)) {
                        if (i != DataAutoDownloadActivity.this.roamingSectionRow) {
                            if (!(i == DataAutoDownloadActivity.this.wSizeRow || i == DataAutoDownloadActivity.this.mSizeRow)) {
                                if (i != DataAutoDownloadActivity.this.rSizeRow) {
                                    return 1;
                                }
                            }
                            return 3;
                        }
                    }
                    return 2;
                }
            }
            return 0;
        }
    }

    public DataAutoDownloadActivity(int i) {
        this.currentType = i;
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
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        if (this.currentType == 1) {
            this.actionBar.setTitle(LocaleController.getString("LocalPhotoCache", C0446R.string.LocalPhotoCache));
        } else if (this.currentType == 2) {
            this.actionBar.setTitle(LocaleController.getString("AudioAutodownload", C0446R.string.AudioAutodownload));
        } else if (this.currentType == 64) {
            this.actionBar.setTitle(LocaleController.getString("VideoMessagesAutodownload", C0446R.string.VideoMessagesAutodownload));
        } else if (this.currentType == 4) {
            this.actionBar.setTitle(LocaleController.getString("LocalVideoCache", C0446R.string.LocalVideoCache));
        } else if (this.currentType == 8) {
            this.actionBar.setTitle(LocaleController.getString("FilesDataUsage", C0446R.string.FilesDataUsage));
        } else if (this.currentType == 16) {
            this.actionBar.setTitle(LocaleController.getString("AttachMusic", C0446R.string.AttachMusic));
        } else if (this.currentType == 32) {
            this.actionBar.setTitle(LocaleController.getString("LocalGifCache", C0446R.string.LocalGifCache));
        }
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C21201());
        this.actionBar.createMenu().addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C21212());
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private int getMaskForRow(int i) {
        if (i == this.mContactsRow) {
            return this.mobileDataDownloadMask;
        }
        if (i == this.mPrivateRow) {
            return this.mobileDataPrivateDownloadMask;
        }
        if (i == this.mGroupRow) {
            return this.mobileDataGroupDownloadMask;
        }
        if (i == this.mChannelsRow) {
            return this.mobileDataChannelDownloadMask;
        }
        if (i == this.wContactsRow) {
            return this.wifiDownloadMask;
        }
        if (i == this.wPrivateRow) {
            return this.wifiPrivateDownloadMask;
        }
        if (i == this.wGroupRow) {
            return this.wifiGroupDownloadMask;
        }
        if (i == this.wChannelsRow) {
            return this.wifiChannelDownloadMask;
        }
        if (i == this.rContactsRow) {
            return this.roamingDownloadMask;
        }
        if (i == this.rPrivateRow) {
            return this.roamingPrivateDownloadMask;
        }
        if (i == this.rGroupRow) {
            return this.roamingGroupDownloadMask;
        }
        return i == this.rChannelsRow ? this.roamingChannelDownloadMask : 0;
    }

    private void setMaskForRow(int i, int i2) {
        if (i == this.mContactsRow) {
            this.mobileDataDownloadMask = i2;
        } else if (i == this.mPrivateRow) {
            this.mobileDataPrivateDownloadMask = i2;
        } else if (i == this.mGroupRow) {
            this.mobileDataGroupDownloadMask = i2;
        } else if (i == this.mChannelsRow) {
            this.mobileDataChannelDownloadMask = i2;
        } else if (i == this.wContactsRow) {
            this.wifiDownloadMask = i2;
        } else if (i == this.wPrivateRow) {
            this.wifiPrivateDownloadMask = i2;
        } else if (i == this.wGroupRow) {
            this.wifiGroupDownloadMask = i2;
        } else if (i == this.wChannelsRow) {
            this.wifiChannelDownloadMask = i2;
        } else if (i == this.rContactsRow) {
            this.roamingDownloadMask = i2;
        } else if (i == this.rPrivateRow) {
            this.roamingPrivateDownloadMask = i2;
        } else if (i == this.rGroupRow) {
            this.roamingGroupDownloadMask = i2;
        } else if (i == this.rChannelsRow) {
            this.roamingChannelDownloadMask = i2;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[18];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCheckBoxCell.class, MaxFileSizeCell.class, HeaderCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{MaxFileSizeCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{MaxFileSizeCell.class}, new String[]{"sizeTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, Theme.key_checkboxSquareUnchecked);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, Theme.key_checkboxSquareDisabled);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, Theme.key_checkboxSquareBackground);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, Theme.key_checkboxSquareCheck);
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        return themeDescriptionArr;
    }
}
