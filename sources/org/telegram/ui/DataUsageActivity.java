package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.StatsController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
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
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate;

public class DataUsageActivity extends BaseFragment {
    private static final Interpolator interpolator = -$$Lambda$DataUsageActivity$wsORmtBp3T6D-3i_RtGAVFVpGzs.INSTANCE;
    private boolean animatingForward;
    private boolean backAnimation;
    private Paint backgroundPaint = new Paint();
    private int maximumVelocity;
    private ListAdapter mobileAdapter;
    private ListAdapter roamingAdapter;
    private ScrollSlidingTextTabStrip scrollSlidingTextTabStrip;
    private AnimatorSet tabsAnimation;
    private boolean tabsAnimationInProgress;
    private ViewPage[] viewPages = new ViewPage[2];
    private ListAdapter wifiAdapter;

    private class ViewPage extends FrameLayout {
        private LinearLayoutManager layoutManager;
        private ListAdapter listAdapter;
        private RecyclerListView listView;
        private int selectedType;

        public ViewPage(Context context) {
            super(context);
        }
    }

    private class ListAdapter extends SelectionAdapter {
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
        private Context mContext;
        private int messagesBytesReceivedRow;
        private int messagesBytesSentRow;
        private int messagesReceivedRow;
        private int messagesSection2Row;
        private int messagesSectionRow;
        private int messagesSentRow;
        private int photosBytesReceivedRow;
        private int photosBytesSentRow;
        private int photosReceivedRow;
        private int photosSection2Row;
        private int photosSectionRow;
        private int photosSentRow;
        private int resetRow;
        private int resetSection2Row;
        private int rowCount = 0;
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

        public ListAdapter(Context context, int i) {
            this.mContext = context;
            this.currentType = i;
            int i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.photosSectionRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.photosSentRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.photosReceivedRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.photosBytesSentRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.photosBytesReceivedRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.photosSection2Row = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.videosSectionRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.videosSentRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.videosReceivedRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.videosBytesSentRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.videosBytesReceivedRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.videosSection2Row = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.audiosSectionRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.audiosSentRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.audiosReceivedRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.audiosBytesSentRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.audiosBytesReceivedRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.audiosSection2Row = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.filesSectionRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.filesSentRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.filesReceivedRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.filesBytesSentRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.filesBytesReceivedRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.filesSection2Row = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.callsSectionRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.callsSentRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.callsReceivedRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.callsBytesSentRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.callsBytesReceivedRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.callsTotalTimeRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.callsSection2Row = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.messagesSectionRow = i2;
            this.messagesSentRow = -1;
            this.messagesReceivedRow = -1;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.messagesBytesSentRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.messagesBytesReceivedRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.messagesSection2Row = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.totalSectionRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.totalBytesSentRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.totalBytesReceivedRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.totalSection2Row = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.resetRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.resetSection2Row = i2;
        }

        public int getItemCount() {
            return this.rowCount;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            String str = "windowBackgroundGrayShadow";
            if (itemViewType != 0) {
                int i2 = 3;
                boolean z = false;
                if (itemViewType == 1) {
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i == this.resetRow) {
                        String str2 = "windowBackgroundWhiteRedText2";
                        textSettingsCell.setTag(str2);
                        textSettingsCell.setText(LocaleController.getString("ResetStatistics", NUM), false);
                        textSettingsCell.setTextColor(Theme.getColor(str2));
                        return;
                    }
                    String str3 = "windowBackgroundWhiteBlackText";
                    textSettingsCell.setTag(str3);
                    textSettingsCell.setTextColor(Theme.getColor(str3));
                    if (i == this.callsSentRow || i == this.callsReceivedRow || i == this.callsBytesSentRow || i == this.callsBytesReceivedRow) {
                        i2 = 0;
                    } else if (i == this.messagesSentRow || i == this.messagesReceivedRow || i == this.messagesBytesSentRow || i == this.messagesBytesReceivedRow) {
                        i2 = 1;
                    } else if (i == this.photosSentRow || i == this.photosReceivedRow || i == this.photosBytesSentRow || i == this.photosBytesReceivedRow) {
                        i2 = 4;
                    } else if (!(i == this.audiosSentRow || i == this.audiosReceivedRow || i == this.audiosBytesSentRow || i == this.audiosBytesReceivedRow)) {
                        i2 = (i == this.videosSentRow || i == this.videosReceivedRow || i == this.videosBytesSentRow || i == this.videosBytesReceivedRow) ? 2 : (i == this.filesSentRow || i == this.filesReceivedRow || i == this.filesBytesSentRow || i == this.filesBytesReceivedRow) ? 5 : 6;
                    }
                    String str4 = "%d";
                    if (i == this.callsSentRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("OutgoingCalls", NUM), String.format(str4, new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(this.currentType, i2))}), true);
                    } else if (i == this.callsReceivedRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("IncomingCalls", NUM), String.format(str4, new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(this.currentType, i2))}), true);
                    } else if (i == this.callsTotalTimeRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("CallsTotalTime", NUM), AndroidUtilities.formatShortDuration(StatsController.getInstance(DataUsageActivity.this.currentAccount).getCallsTotalTime(this.currentType)), false);
                    } else if (i == this.messagesSentRow || i == this.photosSentRow || i == this.videosSentRow || i == this.audiosSentRow || i == this.filesSentRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("CountSent", NUM), String.format(str4, new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(this.currentType, i2))}), true);
                    } else if (i == this.messagesReceivedRow || i == this.photosReceivedRow || i == this.videosReceivedRow || i == this.audiosReceivedRow || i == this.filesReceivedRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("CountReceived", NUM), String.format(str4, new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(this.currentType, i2))}), true);
                    } else if (i == this.messagesBytesSentRow || i == this.photosBytesSentRow || i == this.videosBytesSentRow || i == this.audiosBytesSentRow || i == this.filesBytesSentRow || i == this.callsBytesSentRow || i == this.totalBytesSentRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("BytesSent", NUM), AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentBytesCount(this.currentType, i2)), true);
                    } else if (i == this.messagesBytesReceivedRow || i == this.photosBytesReceivedRow || i == this.videosBytesReceivedRow || i == this.audiosBytesReceivedRow || i == this.filesBytesReceivedRow || i == this.callsBytesReceivedRow || i == this.totalBytesReceivedRow) {
                        str3 = LocaleController.getString("BytesReceived", NUM);
                        str4 = AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getReceivedBytesCount(this.currentType, i2));
                        if (i != this.totalBytesReceivedRow) {
                            z = true;
                        }
                        textSettingsCell.setTextAndValue(str3, str4, z);
                    }
                } else if (itemViewType == 2) {
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == this.totalSectionRow) {
                        headerCell.setText(LocaleController.getString("TotalDataUsage", NUM));
                    } else if (i == this.callsSectionRow) {
                        headerCell.setText(LocaleController.getString("CallsDataUsage", NUM));
                    } else if (i == this.filesSectionRow) {
                        headerCell.setText(LocaleController.getString("FilesDataUsage", NUM));
                    } else if (i == this.audiosSectionRow) {
                        headerCell.setText(LocaleController.getString("LocalAudioCache", NUM));
                    } else if (i == this.videosSectionRow) {
                        headerCell.setText(LocaleController.getString("LocalVideoCache", NUM));
                    } else if (i == this.photosSectionRow) {
                        headerCell.setText(LocaleController.getString("LocalPhotoCache", NUM));
                    } else if (i == this.messagesSectionRow) {
                        headerCell.setText(LocaleController.getString("MessagesDataUsage", NUM));
                    }
                } else if (itemViewType == 3) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                    textInfoPrivacyCell.setText(LocaleController.formatString("NetworkUsageSince", NUM, LocaleController.getInstance().formatterStats.format(StatsController.getInstance(DataUsageActivity.this.currentAccount).getResetStatsDate(this.currentType))));
                }
            } else if (i == this.resetSection2Row) {
                viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
            } else {
                viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() == this.resetRow;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textSettingsCell;
            if (i != 0) {
                String str = "windowBackgroundWhite";
                if (i == 1) {
                    textSettingsCell = new TextSettingsCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(str));
                } else if (i != 2) {
                    textSettingsCell = i != 3 ? null : new TextInfoPrivacyCell(this.mContext);
                } else {
                    textSettingsCell = new HeaderCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(str));
                }
            } else {
                textSettingsCell = new ShadowSectionCell(this.mContext);
            }
            textSettingsCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(textSettingsCell);
        }

        public int getItemViewType(int i) {
            if (i == this.resetSection2Row) {
                return 3;
            }
            if (i == this.callsSection2Row || i == this.filesSection2Row || i == this.audiosSection2Row || i == this.videosSection2Row || i == this.photosSection2Row || i == this.messagesSection2Row || i == this.totalSection2Row) {
                return 0;
            }
            return (i == this.totalSectionRow || i == this.callsSectionRow || i == this.filesSectionRow || i == this.audiosSectionRow || i == this.videosSectionRow || i == this.photosSectionRow || i == this.messagesSectionRow) ? 2 : 1;
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setTitle(LocaleController.getString("NetworkUsage", NUM));
        boolean z = false;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setClipContent(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    DataUsageActivity.this.finishFragment();
                }
            }
        });
        this.hasOwnBackground = true;
        this.mobileAdapter = new ListAdapter(context, 0);
        this.wifiAdapter = new ListAdapter(context, 1);
        this.roamingAdapter = new ListAdapter(context, 2);
        this.scrollSlidingTextTabStrip = new ScrollSlidingTextTabStrip(context);
        this.scrollSlidingTextTabStrip.setUseSameWidth(true);
        this.actionBar.addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 44, 83));
        this.scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTabStripDelegate() {
            public void onPageSelected(int i, boolean z) {
                if (DataUsageActivity.this.viewPages[0].selectedType != i) {
                    DataUsageActivity dataUsageActivity = DataUsageActivity.this;
                    dataUsageActivity.swipeBackEnabled = i == dataUsageActivity.scrollSlidingTextTabStrip.getFirstTabId();
                    DataUsageActivity.this.viewPages[1].selectedType = i;
                    DataUsageActivity.this.viewPages[1].setVisibility(0);
                    DataUsageActivity.this.switchToCurrentSelectedMode(true);
                    DataUsageActivity.this.animatingForward = z;
                }
            }

            public void onPageScrolled(float f) {
                if (f != 1.0f || DataUsageActivity.this.viewPages[1].getVisibility() == 0) {
                    if (DataUsageActivity.this.animatingForward) {
                        DataUsageActivity.this.viewPages[0].setTranslationX((-f) * ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                        DataUsageActivity.this.viewPages[1].setTranslationX(((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) - (((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) * f));
                    } else {
                        DataUsageActivity.this.viewPages[0].setTranslationX(((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) * f);
                        DataUsageActivity.this.viewPages[1].setTranslationX((((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) * f) - ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                    if (f == 1.0f) {
                        ViewPage viewPage = DataUsageActivity.this.viewPages[0];
                        DataUsageActivity.this.viewPages[0] = DataUsageActivity.this.viewPages[1];
                        DataUsageActivity.this.viewPages[1] = viewPage;
                        DataUsageActivity.this.viewPages[1].setVisibility(8);
                    }
                }
            }
        });
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        AnonymousClass3 anonymousClass3 = new FrameLayout(context) {
            private boolean globalIgnoreLayout;
            private boolean maybeStartTracking;
            private boolean startedTracking;
            private int startedTrackingPointerId;
            private int startedTrackingX;
            private int startedTrackingY;
            private VelocityTracker velocityTracker;

            private boolean prepareForMoving(MotionEvent motionEvent, boolean z) {
                int nextPageId = DataUsageActivity.this.scrollSlidingTextTabStrip.getNextPageId(z);
                if (nextPageId < 0) {
                    return false;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                this.maybeStartTracking = false;
                this.startedTracking = true;
                this.startedTrackingX = (int) motionEvent.getX();
                DataUsageActivity.this.actionBar.setEnabled(false);
                DataUsageActivity.this.scrollSlidingTextTabStrip.setEnabled(false);
                DataUsageActivity.this.viewPages[1].selectedType = nextPageId;
                DataUsageActivity.this.viewPages[1].setVisibility(0);
                DataUsageActivity.this.animatingForward = z;
                DataUsageActivity.this.switchToCurrentSelectedMode(true);
                if (z) {
                    DataUsageActivity.this.viewPages[1].setTranslationX((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth());
                } else {
                    DataUsageActivity.this.viewPages[1].setTranslationX((float) (-DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                }
                return true;
            }

            public void forceHasOverlappingRendering(boolean z) {
                super.forceHasOverlappingRendering(z);
            }

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                setMeasuredDimension(MeasureSpec.getSize(i), MeasureSpec.getSize(i2));
                measureChildWithMargins(DataUsageActivity.this.actionBar, i, 0, i2, 0);
                int measuredHeight = DataUsageActivity.this.actionBar.getMeasuredHeight();
                this.globalIgnoreLayout = true;
                int i3 = 0;
                int i4 = 0;
                while (i4 < DataUsageActivity.this.viewPages.length) {
                    if (!(DataUsageActivity.this.viewPages[i4] == null || DataUsageActivity.this.viewPages[i4].listView == null)) {
                        DataUsageActivity.this.viewPages[i4].listView.setPadding(0, measuredHeight, 0, AndroidUtilities.dp(4.0f));
                    }
                    i4++;
                }
                this.globalIgnoreLayout = false;
                measuredHeight = getChildCount();
                while (i3 < measuredHeight) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8 || childAt == DataUsageActivity.this.actionBar)) {
                        measureChildWithMargins(childAt, i, 0, i2, 0);
                    }
                    i3++;
                }
            }

            /* Access modifiers changed, original: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (DataUsageActivity.this.parentLayout != null) {
                    DataUsageActivity.this.parentLayout.drawHeaderShadow(canvas, DataUsageActivity.this.actionBar.getMeasuredHeight() + ((int) DataUsageActivity.this.actionBar.getTranslationY()));
                }
            }

            public void requestLayout() {
                if (!this.globalIgnoreLayout) {
                    super.requestLayout();
                }
            }

            /* JADX WARNING: Removed duplicated region for block: B:18:0x00a0  */
            /* JADX WARNING: Removed duplicated region for block: B:18:0x00a0  */
            public boolean checkTabsAnimationInProgress() {
                /*
                r7 = this;
                r0 = org.telegram.ui.DataUsageActivity.this;
                r0 = r0.tabsAnimationInProgress;
                r1 = 0;
                if (r0 == 0) goto L_0x00c3;
            L_0x0009:
                r0 = org.telegram.ui.DataUsageActivity.this;
                r0 = r0.backAnimation;
                r2 = -1;
                r3 = 0;
                r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r5 = 1;
                if (r0 == 0) goto L_0x0059;
            L_0x0016:
                r0 = org.telegram.ui.DataUsageActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r1];
                r0 = r0.getTranslationX();
                r0 = java.lang.Math.abs(r0);
                r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
                if (r0 >= 0) goto L_0x009d;
            L_0x002a:
                r0 = org.telegram.ui.DataUsageActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r1];
                r0.setTranslationX(r3);
                r0 = org.telegram.ui.DataUsageActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r5];
                r3 = org.telegram.ui.DataUsageActivity.this;
                r3 = r3.viewPages;
                r3 = r3[r1];
                r3 = r3.getMeasuredWidth();
                r4 = org.telegram.ui.DataUsageActivity.this;
                r4 = r4.animatingForward;
                if (r4 == 0) goto L_0x0052;
            L_0x0051:
                r2 = 1;
            L_0x0052:
                r3 = r3 * r2;
                r2 = (float) r3;
                r0.setTranslationX(r2);
                goto L_0x009e;
            L_0x0059:
                r0 = org.telegram.ui.DataUsageActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r5];
                r0 = r0.getTranslationX();
                r0 = java.lang.Math.abs(r0);
                r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
                if (r0 >= 0) goto L_0x009d;
            L_0x006d:
                r0 = org.telegram.ui.DataUsageActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r1];
                r4 = org.telegram.ui.DataUsageActivity.this;
                r4 = r4.viewPages;
                r4 = r4[r1];
                r4 = r4.getMeasuredWidth();
                r6 = org.telegram.ui.DataUsageActivity.this;
                r6 = r6.animatingForward;
                if (r6 == 0) goto L_0x008a;
            L_0x0089:
                goto L_0x008b;
            L_0x008a:
                r2 = 1;
            L_0x008b:
                r4 = r4 * r2;
                r2 = (float) r4;
                r0.setTranslationX(r2);
                r0 = org.telegram.ui.DataUsageActivity.this;
                r0 = r0.viewPages;
                r0 = r0[r5];
                r0.setTranslationX(r3);
                goto L_0x009e;
            L_0x009d:
                r5 = 0;
            L_0x009e:
                if (r5 == 0) goto L_0x00bc;
            L_0x00a0:
                r0 = org.telegram.ui.DataUsageActivity.this;
                r0 = r0.tabsAnimation;
                if (r0 == 0) goto L_0x00b7;
            L_0x00a8:
                r0 = org.telegram.ui.DataUsageActivity.this;
                r0 = r0.tabsAnimation;
                r0.cancel();
                r0 = org.telegram.ui.DataUsageActivity.this;
                r2 = 0;
                r0.tabsAnimation = r2;
            L_0x00b7:
                r0 = org.telegram.ui.DataUsageActivity.this;
                r0.tabsAnimationInProgress = r1;
            L_0x00bc:
                r0 = org.telegram.ui.DataUsageActivity.this;
                r0 = r0.tabsAnimationInProgress;
                return r0;
            L_0x00c3:
                return r1;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DataUsageActivity$AnonymousClass3.checkTabsAnimationInProgress():boolean");
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return checkTabsAnimationInProgress() || DataUsageActivity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(motionEvent);
            }

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                DataUsageActivity.this.backgroundPaint.setColor(Theme.getColor("windowBackgroundGray"));
                canvas.drawRect(0.0f, ((float) DataUsageActivity.this.actionBar.getMeasuredHeight()) + DataUsageActivity.this.actionBar.getTranslationY(), (float) getMeasuredWidth(), (float) getMeasuredHeight(), DataUsageActivity.this.backgroundPaint);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (DataUsageActivity.this.parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
                    return false;
                }
                boolean z = true;
                VelocityTracker velocityTracker;
                if (motionEvent != null && motionEvent.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                    this.startedTrackingPointerId = motionEvent.getPointerId(0);
                    this.maybeStartTracking = true;
                    this.startedTrackingX = (int) motionEvent.getX();
                    this.startedTrackingY = (int) motionEvent.getY();
                    velocityTracker = this.velocityTracker;
                    if (velocityTracker != null) {
                        velocityTracker.clear();
                    }
                } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    int x = (int) (motionEvent.getX() - ((float) this.startedTrackingX));
                    int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
                    this.velocityTracker.addMovement(motionEvent);
                    if (this.startedTracking && ((DataUsageActivity.this.animatingForward && x > 0) || (!DataUsageActivity.this.animatingForward && x < 0))) {
                        if (!prepareForMoving(motionEvent, x < 0)) {
                            this.maybeStartTracking = true;
                            this.startedTracking = false;
                        }
                    }
                    if (this.maybeStartTracking && !this.startedTracking) {
                        if (((float) Math.abs(x)) >= AndroidUtilities.getPixelsInCM(0.3f, true) && Math.abs(x) / 3 > abs) {
                            if (x >= 0) {
                                z = false;
                            }
                            prepareForMoving(motionEvent, z);
                        }
                    } else if (this.startedTracking) {
                        if (DataUsageActivity.this.animatingForward) {
                            DataUsageActivity.this.viewPages[0].setTranslationX((float) x);
                            DataUsageActivity.this.viewPages[1].setTranslationX((float) (DataUsageActivity.this.viewPages[0].getMeasuredWidth() + x));
                        } else {
                            DataUsageActivity.this.viewPages[0].setTranslationX((float) x);
                            DataUsageActivity.this.viewPages[1].setTranslationX((float) (x - DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                        }
                        DataUsageActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DataUsageActivity.this.viewPages[1].selectedType, ((float) Math.abs(x)) / ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                } else if (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6)) {
                    float xVelocity;
                    float yVelocity;
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000, (float) DataUsageActivity.this.maximumVelocity);
                    if (!this.startedTracking) {
                        xVelocity = this.velocityTracker.getXVelocity();
                        yVelocity = this.velocityTracker.getYVelocity();
                        if (Math.abs(xVelocity) >= 3000.0f && Math.abs(xVelocity) > Math.abs(yVelocity)) {
                            prepareForMoving(motionEvent, xVelocity < 0.0f);
                        }
                    }
                    if (this.startedTracking) {
                        int round;
                        float x2 = DataUsageActivity.this.viewPages[0].getX();
                        DataUsageActivity.this.tabsAnimation = new AnimatorSet();
                        xVelocity = this.velocityTracker.getXVelocity();
                        yVelocity = this.velocityTracker.getYVelocity();
                        DataUsageActivity dataUsageActivity = DataUsageActivity.this;
                        boolean z2 = Math.abs(x2) < ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(xVelocity) < 3500.0f || Math.abs(xVelocity) < Math.abs(yVelocity));
                        dataUsageActivity.backAnimation = z2;
                        AnimatorSet access$1700;
                        Animator[] animatorArr;
                        if (DataUsageActivity.this.backAnimation) {
                            x2 = Math.abs(x2);
                            if (DataUsageActivity.this.animatingForward) {
                                access$1700 = DataUsageActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) DataUsageActivity.this.viewPages[1].getMeasuredWidth()});
                                access$1700.playTogether(animatorArr);
                            } else {
                                access$1700 = DataUsageActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) (-DataUsageActivity.this.viewPages[1].getMeasuredWidth())});
                                access$1700.playTogether(animatorArr);
                            }
                        } else {
                            x2 = ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) - Math.abs(x2);
                            if (DataUsageActivity.this.animatingForward) {
                                access$1700 = DataUsageActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) (-DataUsageActivity.this.viewPages[0].getMeasuredWidth())});
                                animatorArr[1] = ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f});
                                access$1700.playTogether(animatorArr);
                            } else {
                                access$1700 = DataUsageActivity.this.tabsAnimation;
                                animatorArr = new Animator[2];
                                animatorArr[0] = ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()});
                                animatorArr[1] = ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f});
                                access$1700.playTogether(animatorArr);
                            }
                        }
                        DataUsageActivity.this.tabsAnimation.setInterpolator(DataUsageActivity.interpolator);
                        int measuredWidth = getMeasuredWidth();
                        float f = (float) (measuredWidth / 2);
                        f += AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (x2 * 1.0f) / ((float) measuredWidth))) * f;
                        float abs2 = Math.abs(xVelocity);
                        if (abs2 > 0.0f) {
                            round = Math.round(Math.abs(f / abs2) * 1000.0f) * 4;
                        } else {
                            round = (int) (((x2 / ((float) getMeasuredWidth())) + 1.0f) * 100.0f);
                        }
                        DataUsageActivity.this.tabsAnimation.setDuration((long) Math.max(150, Math.min(round, 600)));
                        DataUsageActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                DataUsageActivity.this.tabsAnimation = null;
                                if (DataUsageActivity.this.backAnimation) {
                                    DataUsageActivity.this.viewPages[1].setVisibility(8);
                                } else {
                                    ViewPage viewPage = DataUsageActivity.this.viewPages[0];
                                    DataUsageActivity.this.viewPages[0] = DataUsageActivity.this.viewPages[1];
                                    DataUsageActivity.this.viewPages[1] = viewPage;
                                    DataUsageActivity.this.viewPages[1].setVisibility(8);
                                    DataUsageActivity dataUsageActivity = DataUsageActivity.this;
                                    dataUsageActivity.swipeBackEnabled = dataUsageActivity.viewPages[0].selectedType == DataUsageActivity.this.scrollSlidingTextTabStrip.getFirstTabId();
                                    DataUsageActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DataUsageActivity.this.viewPages[0].selectedType, 1.0f);
                                }
                                DataUsageActivity.this.tabsAnimationInProgress = false;
                                AnonymousClass3.this.maybeStartTracking = false;
                                AnonymousClass3.this.startedTracking = false;
                                DataUsageActivity.this.actionBar.setEnabled(true);
                                DataUsageActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                            }
                        });
                        DataUsageActivity.this.tabsAnimation.start();
                        DataUsageActivity.this.tabsAnimationInProgress = true;
                    } else {
                        this.maybeStartTracking = false;
                        this.startedTracking = false;
                        DataUsageActivity.this.actionBar.setEnabled(true);
                        DataUsageActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                    }
                    velocityTracker = this.velocityTracker;
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                }
                return this.startedTracking;
            }
        };
        this.fragmentView = anonymousClass3;
        anonymousClass3.setWillNotDraw(false);
        int i = 0;
        int i2 = -1;
        int i3 = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                break;
            }
            if (!(i != 0 || viewPageArr[i] == null || viewPageArr[i].layoutManager == null)) {
                i2 = this.viewPages[i].layoutManager.findFirstVisibleItemPosition();
                if (i2 != this.viewPages[i].layoutManager.getItemCount() - 1) {
                    Holder holder = (Holder) this.viewPages[i].listView.findViewHolderForAdapterPosition(i2);
                    if (holder != null) {
                        i3 = holder.itemView.getTop();
                    }
                }
                i2 = -1;
            }
            AnonymousClass4 anonymousClass4 = new ViewPage(context) {
                public void setTranslationX(float f) {
                    super.setTranslationX(f);
                    if (DataUsageActivity.this.tabsAnimationInProgress && DataUsageActivity.this.viewPages[0] == this) {
                        DataUsageActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DataUsageActivity.this.viewPages[1].selectedType, Math.abs(DataUsageActivity.this.viewPages[0].getTranslationX()) / ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                }
            };
            anonymousClass3.addView(anonymousClass4, LayoutHelper.createFrame(-1, -1.0f));
            ViewPage[] viewPageArr2 = this.viewPages;
            viewPageArr2[i] = anonymousClass4;
            LinearLayoutManager access$2902 = viewPageArr2[i].layoutManager = new LinearLayoutManager(context, 1, false) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            RecyclerListView recyclerListView = new RecyclerListView(context);
            this.viewPages[i].listView = recyclerListView;
            this.viewPages[i].listView.setItemAnimator(null);
            this.viewPages[i].listView.setClipToPadding(false);
            this.viewPages[i].listView.setSectionsType(2);
            this.viewPages[i].listView.setLayoutManager(access$2902);
            ViewPage[] viewPageArr3 = this.viewPages;
            viewPageArr3[i].addView(viewPageArr3[i].listView, LayoutHelper.createFrame(-1, -1.0f));
            this.viewPages[i].listView.setOnItemClickListener(new -$$Lambda$DataUsageActivity$NwaR7lgfeGPKETAnvYA4hx0PZOY(this, recyclerListView));
            this.viewPages[i].listView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    if (i != 1) {
                        int i2 = (int) (-DataUsageActivity.this.actionBar.getTranslationY());
                        i = ActionBar.getCurrentActionBarHeight();
                        if (i2 != 0 && i2 != i) {
                            if (i2 < i / 2) {
                                DataUsageActivity.this.viewPages[0].listView.smoothScrollBy(0, -i2);
                            } else {
                                DataUsageActivity.this.viewPages[0].listView.smoothScrollBy(0, i - i2);
                            }
                        }
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    if (recyclerView == DataUsageActivity.this.viewPages[0].listView) {
                        float translationY = DataUsageActivity.this.actionBar.getTranslationY();
                        float f = translationY - ((float) i2);
                        if (f < ((float) (-ActionBar.getCurrentActionBarHeight()))) {
                            f = (float) (-ActionBar.getCurrentActionBarHeight());
                        } else if (f > 0.0f) {
                            f = 0.0f;
                        }
                        if (f != translationY) {
                            DataUsageActivity.this.setScrollY(f);
                        }
                    }
                }
            });
            if (i == 0 && i2 != -1) {
                access$2902.scrollToPositionWithOffset(i2, i3);
            }
            if (i != 0) {
                this.viewPages[i].setVisibility(8);
            }
            i++;
        }
        anonymousClass3.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        updateTabs();
        switchToCurrentSelectedMode(false);
        if (this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId()) {
            z = true;
        }
        this.swipeBackEnabled = z;
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$DataUsageActivity(RecyclerListView recyclerListView, View view, int i) {
        if (getParentActivity() != null) {
            ListAdapter listAdapter = (ListAdapter) recyclerListView.getAdapter();
            if (i == listAdapter.resetRow) {
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("ResetStatisticsAlertTitle", NUM));
                builder.setMessage(LocaleController.getString("ResetStatisticsAlert", NUM));
                builder.setPositiveButton(LocaleController.getString("Reset", NUM), new -$$Lambda$DataUsageActivity$ZhhoKKmFdkBYmiX0nsi78u9hycQ(this, listAdapter));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                AlertDialog create = builder.create();
                showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$1$DataUsageActivity(ListAdapter listAdapter, DialogInterface dialogInterface, int i) {
        StatsController.getInstance(this.currentAccount).resetStats(listAdapter.currentType);
        listAdapter.notifyDataSetChanged();
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.mobileAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        listAdapter = this.wifiAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        listAdapter = this.roamingAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void setScrollY(float f) {
        this.actionBar.setTranslationY(f);
        int i = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i < viewPageArr.length) {
                viewPageArr[i].listView.setPinnedSectionOffsetY((int) f);
                i++;
            } else {
                this.fragmentView.invalidate();
                return;
            }
        }
    }

    private void updateTabs() {
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStrip != null) {
            scrollSlidingTextTabStrip.addTextTab(0, LocaleController.getString("NetworkUsageMobile", NUM));
            this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("NetworkUsageWiFi", NUM));
            this.scrollSlidingTextTabStrip.addTextTab(2, LocaleController.getString("NetworkUsageRoaming", NUM));
            this.scrollSlidingTextTabStrip.setVisibility(0);
            this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
            int currentTabId = this.scrollSlidingTextTabStrip.getCurrentTabId();
            if (currentTabId >= 0) {
                this.viewPages[0].selectedType = currentTabId;
            }
            this.scrollSlidingTextTabStrip.finishAddingTabs();
        }
    }

    private void switchToCurrentSelectedMode(boolean z) {
        ViewPage[] viewPageArr;
        int i = 0;
        while (true) {
            viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                break;
            }
            viewPageArr[i].listView.stopScroll();
            i++;
        }
        Adapter adapter = viewPageArr[z].listView.getAdapter();
        this.viewPages[z].listView.setPinnedHeaderShadowDrawable(null);
        if (this.viewPages[z].selectedType == 0) {
            if (adapter != this.mobileAdapter) {
                this.viewPages[z].listView.setAdapter(this.mobileAdapter);
            }
        } else if (this.viewPages[z].selectedType == 1) {
            if (adapter != this.wifiAdapter) {
                this.viewPages[z].listView.setAdapter(this.wifiAdapter);
            }
        } else if (this.viewPages[z].selectedType == 2 && adapter != this.roamingAdapter) {
            this.viewPages[z].listView.setAdapter(this.roamingAdapter);
        }
        this.viewPages[z].listView.setVisibility(0);
        if (this.actionBar.getTranslationY() != 0.0f) {
            this.viewPages[z].layoutManager.scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, 0, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, "actionBarTabActiveText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, "actionBarTabUnactiveText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, null, null, null, "actionBarTabLine"));
        arrayList.add(new ThemeDescription(null, 0, null, null, new Drawable[]{this.scrollSlidingTextTabStrip.getSelectorDrawable()}, null, "actionBarTabSelector"));
        int i = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
            }
            arrayList.add(new ThemeDescription(viewPageArr[i].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class}, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
            View access$900 = this.viewPages[i].listView;
            Class[] clsArr = new Class[]{HeaderCell.class};
            String[] strArr = new String[1];
            strArr[0] = "textView";
            arrayList.add(new ThemeDescription(access$900, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueHeader"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteRedText2"));
            i++;
        }
    }
}
