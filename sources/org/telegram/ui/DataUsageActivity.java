package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar;
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
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate;

public class DataUsageActivity extends BaseFragment {
    private static final Interpolator interpolator = DataUsageActivity$$Lambda$2.$instance;
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

        public ListAdapter(Context context, int type) {
            this.mContext = context;
            this.currentType = type;
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
            this.messagesSentRow = -1;
            this.messagesReceivedRow = -1;
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
        }

        public int getItemCount() {
            return this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    if (position == this.resetSection2Row) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 1:
                    TextSettingsCell textCell = holder.itemView;
                    if (position == this.resetRow) {
                        textCell.setTag("windowBackgroundWhiteRedText2");
                        textCell.setText(LocaleController.getString("ResetStatistics", NUM), false);
                        textCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText2"));
                        return;
                    }
                    int type;
                    textCell.setTag("windowBackgroundWhiteBlackText");
                    textCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                    if (position == this.callsSentRow || position == this.callsReceivedRow || position == this.callsBytesSentRow || position == this.callsBytesReceivedRow) {
                        type = 0;
                    } else if (position == this.messagesSentRow || position == this.messagesReceivedRow || position == this.messagesBytesSentRow || position == this.messagesBytesReceivedRow) {
                        type = 1;
                    } else if (position == this.photosSentRow || position == this.photosReceivedRow || position == this.photosBytesSentRow || position == this.photosBytesReceivedRow) {
                        type = 4;
                    } else if (position == this.audiosSentRow || position == this.audiosReceivedRow || position == this.audiosBytesSentRow || position == this.audiosBytesReceivedRow) {
                        type = 3;
                    } else if (position == this.videosSentRow || position == this.videosReceivedRow || position == this.videosBytesSentRow || position == this.videosBytesReceivedRow) {
                        type = 2;
                    } else if (position == this.filesSentRow || position == this.filesReceivedRow || position == this.filesBytesSentRow || position == this.filesBytesReceivedRow) {
                        type = 5;
                    } else {
                        type = 6;
                    }
                    if (position == this.callsSentRow) {
                        textCell.setTextAndValue(LocaleController.getString("OutgoingCalls", NUM), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(this.currentType, type))}), true);
                        return;
                    } else if (position == this.callsReceivedRow) {
                        textCell.setTextAndValue(LocaleController.getString("IncomingCalls", NUM), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(this.currentType, type))}), true);
                        return;
                    } else if (position == this.callsTotalTimeRow) {
                        String time;
                        int total = StatsController.getInstance(DataUsageActivity.this.currentAccount).getCallsTotalTime(this.currentType);
                        int hours = total / 3600;
                        total -= hours * 3600;
                        total -= (total / 60) * 60;
                        if (hours != 0) {
                            time = String.format("%d:%02d:%02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(total)});
                        } else {
                            time = String.format("%d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(total)});
                        }
                        textCell.setTextAndValue(LocaleController.getString("CallsTotalTime", NUM), time, false);
                        return;
                    } else if (position == this.messagesSentRow || position == this.photosSentRow || position == this.videosSentRow || position == this.audiosSentRow || position == this.filesSentRow) {
                        textCell.setTextAndValue(LocaleController.getString("CountSent", NUM), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(this.currentType, type))}), true);
                        return;
                    } else if (position == this.messagesReceivedRow || position == this.photosReceivedRow || position == this.videosReceivedRow || position == this.audiosReceivedRow || position == this.filesReceivedRow) {
                        textCell.setTextAndValue(LocaleController.getString("CountReceived", NUM), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(this.currentType, type))}), true);
                        return;
                    } else if (position == this.messagesBytesSentRow || position == this.photosBytesSentRow || position == this.videosBytesSentRow || position == this.audiosBytesSentRow || position == this.filesBytesSentRow || position == this.callsBytesSentRow || position == this.totalBytesSentRow) {
                        textCell.setTextAndValue(LocaleController.getString("BytesSent", NUM), AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentBytesCount(this.currentType, type)), true);
                        return;
                    } else if (position == this.messagesBytesReceivedRow || position == this.photosBytesReceivedRow || position == this.videosBytesReceivedRow || position == this.audiosBytesReceivedRow || position == this.filesBytesReceivedRow || position == this.callsBytesReceivedRow || position == this.totalBytesReceivedRow) {
                        textCell.setTextAndValue(LocaleController.getString("BytesReceived", NUM), AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getReceivedBytesCount(this.currentType, type)), position != this.totalBytesReceivedRow);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = holder.itemView;
                    if (position == this.totalSectionRow) {
                        headerCell.setText(LocaleController.getString("TotalDataUsage", NUM));
                        return;
                    } else if (position == this.callsSectionRow) {
                        headerCell.setText(LocaleController.getString("CallsDataUsage", NUM));
                        return;
                    } else if (position == this.filesSectionRow) {
                        headerCell.setText(LocaleController.getString("FilesDataUsage", NUM));
                        return;
                    } else if (position == this.audiosSectionRow) {
                        headerCell.setText(LocaleController.getString("LocalAudioCache", NUM));
                        return;
                    } else if (position == this.videosSectionRow) {
                        headerCell.setText(LocaleController.getString("LocalVideoCache", NUM));
                        return;
                    } else if (position == this.photosSectionRow) {
                        headerCell.setText(LocaleController.getString("LocalPhotoCache", NUM));
                        return;
                    } else if (position == this.messagesSectionRow) {
                        headerCell.setText(LocaleController.getString("MessagesDataUsage", NUM));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextInfoPrivacyCell cell = holder.itemView;
                    cell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    cell.setText(LocaleController.formatString("NetworkUsageSince", NUM, LocaleController.getInstance().formatterStats.format(StatsController.getInstance(DataUsageActivity.this.currentAccount).getResetStatsDate(this.currentType))));
                    return;
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getAdapterPosition() == this.resetRow;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 1:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 2:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 3:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == this.resetSection2Row) {
                return 3;
            }
            if (position == this.resetSection2Row || position == this.callsSection2Row || position == this.filesSection2Row || position == this.audiosSection2Row || position == this.videosSection2Row || position == this.photosSection2Row || position == this.messagesSection2Row || position == this.totalSection2Row) {
                return 0;
            }
            if (position == this.totalSectionRow || position == this.callsSectionRow || position == this.filesSectionRow || position == this.audiosSectionRow || position == this.videosSectionRow || position == this.photosSectionRow || position == this.messagesSectionRow) {
                return 2;
            }
            return 1;
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setTitle(LocaleController.getString("NetworkUsage", NUM));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setClipContent(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
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
            public void onPageSelected(int id, boolean forward) {
                if (DataUsageActivity.this.viewPages[0].selectedType != id) {
                    boolean z;
                    DataUsageActivity dataUsageActivity = DataUsageActivity.this;
                    if (id == DataUsageActivity.this.scrollSlidingTextTabStrip.getFirstTabId()) {
                        z = true;
                    } else {
                        z = false;
                    }
                    dataUsageActivity.swipeBackEnabled = z;
                    DataUsageActivity.this.viewPages[1].selectedType = id;
                    DataUsageActivity.this.viewPages[1].setVisibility(0);
                    DataUsageActivity.this.switchToCurrentSelectedMode(true);
                    DataUsageActivity.this.animatingForward = forward;
                }
            }

            public void onPageScrolled(float progress) {
                if (progress != 1.0f || DataUsageActivity.this.viewPages[1].getVisibility() == 0) {
                    if (DataUsageActivity.this.animatingForward) {
                        DataUsageActivity.this.viewPages[0].setTranslationX((-progress) * ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                        DataUsageActivity.this.viewPages[1].setTranslationX(((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) - (((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) * progress));
                    } else {
                        DataUsageActivity.this.viewPages[0].setTranslationX(((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) * progress);
                        DataUsageActivity.this.viewPages[1].setTranslationX((((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) * progress) - ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                    if (progress == 1.0f) {
                        ViewPage tempPage = DataUsageActivity.this.viewPages[0];
                        DataUsageActivity.this.viewPages[0] = DataUsageActivity.this.viewPages[1];
                        DataUsageActivity.this.viewPages[1] = tempPage;
                        DataUsageActivity.this.viewPages[1].setVisibility(8);
                    }
                }
            }
        });
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        FrameLayout frameLayout = new FrameLayout(context) {
            private boolean globalIgnoreLayout;
            private boolean maybeStartTracking;
            private boolean startedTracking;
            private int startedTrackingPointerId;
            private int startedTrackingX;
            private int startedTrackingY;
            private VelocityTracker velocityTracker;

            private boolean prepareForMoving(MotionEvent ev, boolean forward) {
                int id = DataUsageActivity.this.scrollSlidingTextTabStrip.getNextPageId(forward);
                if (id < 0) {
                    return false;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                this.maybeStartTracking = false;
                this.startedTracking = true;
                this.startedTrackingX = (int) ev.getX();
                DataUsageActivity.this.actionBar.setEnabled(false);
                DataUsageActivity.this.scrollSlidingTextTabStrip.setEnabled(false);
                DataUsageActivity.this.viewPages[1].selectedType = id;
                DataUsageActivity.this.viewPages[1].setVisibility(0);
                DataUsageActivity.this.animatingForward = forward;
                DataUsageActivity.this.switchToCurrentSelectedMode(true);
                if (forward) {
                    DataUsageActivity.this.viewPages[1].setTranslationX((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth());
                } else {
                    DataUsageActivity.this.viewPages[1].setTranslationX((float) (-DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                }
                return true;
            }

            public void forceHasOverlappingRendering(boolean hasOverlappingRendering) {
                super.forceHasOverlappingRendering(hasOverlappingRendering);
            }

            /* Access modifiers changed, original: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
                measureChildWithMargins(DataUsageActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int actionBarHeight = DataUsageActivity.this.actionBar.getMeasuredHeight();
                this.globalIgnoreLayout = true;
                int a = 0;
                while (a < DataUsageActivity.this.viewPages.length) {
                    if (!(DataUsageActivity.this.viewPages[a] == null || DataUsageActivity.this.viewPages[a].listView == null)) {
                        DataUsageActivity.this.viewPages[a].listView.setPadding(0, actionBarHeight, 0, AndroidUtilities.dp(4.0f));
                    }
                    a++;
                }
                this.globalIgnoreLayout = false;
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == DataUsageActivity.this.actionBar)) {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    }
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

            public boolean checkTabsAnimationInProgress() {
                int i = -1;
                int i2 = 1;
                if (!DataUsageActivity.this.tabsAnimationInProgress) {
                    return false;
                }
                boolean cancel = false;
                ViewPage viewPage;
                int measuredWidth;
                if (DataUsageActivity.this.backAnimation) {
                    if (Math.abs(DataUsageActivity.this.viewPages[0].getTranslationX()) < 1.0f) {
                        DataUsageActivity.this.viewPages[0].setTranslationX(0.0f);
                        viewPage = DataUsageActivity.this.viewPages[1];
                        measuredWidth = DataUsageActivity.this.viewPages[0].getMeasuredWidth();
                        if (!DataUsageActivity.this.animatingForward) {
                            i2 = -1;
                        }
                        viewPage.setTranslationX((float) (i2 * measuredWidth));
                        cancel = true;
                    }
                } else if (Math.abs(DataUsageActivity.this.viewPages[1].getTranslationX()) < 1.0f) {
                    viewPage = DataUsageActivity.this.viewPages[0];
                    measuredWidth = DataUsageActivity.this.viewPages[0].getMeasuredWidth();
                    if (!DataUsageActivity.this.animatingForward) {
                        i = 1;
                    }
                    viewPage.setTranslationX((float) (i * measuredWidth));
                    DataUsageActivity.this.viewPages[1].setTranslationX(0.0f);
                    cancel = true;
                }
                if (cancel) {
                    if (DataUsageActivity.this.tabsAnimation != null) {
                        DataUsageActivity.this.tabsAnimation.cancel();
                        DataUsageActivity.this.tabsAnimation = null;
                    }
                    DataUsageActivity.this.tabsAnimationInProgress = false;
                }
                return DataUsageActivity.this.tabsAnimationInProgress;
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return checkTabsAnimationInProgress() || DataUsageActivity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(ev);
            }

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                DataUsageActivity.this.backgroundPaint.setColor(Theme.getColor("windowBackgroundGray"));
                canvas.drawRect(0.0f, DataUsageActivity.this.actionBar.getTranslationY() + ((float) DataUsageActivity.this.actionBar.getMeasuredHeight()), (float) getMeasuredWidth(), (float) getMeasuredHeight(), DataUsageActivity.this.backgroundPaint);
            }

            public boolean onTouchEvent(MotionEvent ev) {
                if (DataUsageActivity.this.parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
                    return false;
                }
                boolean z;
                if (ev != null && ev.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                    this.startedTrackingPointerId = ev.getPointerId(0);
                    this.maybeStartTracking = true;
                    this.startedTrackingX = (int) ev.getX();
                    this.startedTrackingY = (int) ev.getY();
                    if (this.velocityTracker != null) {
                        this.velocityTracker.clear();
                    }
                } else if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    int dx = (int) (ev.getX() - ((float) this.startedTrackingX));
                    int dy = Math.abs(((int) ev.getY()) - this.startedTrackingY);
                    this.velocityTracker.addMovement(ev);
                    if (this.startedTracking && ((DataUsageActivity.this.animatingForward && dx > 0) || (!DataUsageActivity.this.animatingForward && dx < 0))) {
                        if (!prepareForMoving(ev, dx < 0)) {
                            this.maybeStartTracking = true;
                            this.startedTracking = false;
                        }
                    }
                    if (this.maybeStartTracking && !this.startedTracking) {
                        if (((float) Math.abs(dx)) >= AndroidUtilities.getPixelsInCM(0.3f, true) && Math.abs(dx) / 3 > dy) {
                            if (dx < 0) {
                                z = true;
                            } else {
                                z = false;
                            }
                            prepareForMoving(ev, z);
                        }
                    } else if (this.startedTracking) {
                        if (DataUsageActivity.this.animatingForward) {
                            DataUsageActivity.this.viewPages[0].setTranslationX((float) dx);
                            DataUsageActivity.this.viewPages[1].setTranslationX((float) (DataUsageActivity.this.viewPages[0].getMeasuredWidth() + dx));
                        } else {
                            DataUsageActivity.this.viewPages[0].setTranslationX((float) dx);
                            DataUsageActivity.this.viewPages[1].setTranslationX((float) (dx - DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                        }
                        DataUsageActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DataUsageActivity.this.viewPages[1].selectedType, ((float) Math.abs(dx)) / ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                } else if (ev != null && ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6)) {
                    float velX;
                    float velY;
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000, (float) DataUsageActivity.this.maximumVelocity);
                    if (!this.startedTracking) {
                        velX = this.velocityTracker.getXVelocity();
                        velY = this.velocityTracker.getYVelocity();
                        if (Math.abs(velX) >= 3000.0f && Math.abs(velX) > Math.abs(velY)) {
                            prepareForMoving(ev, velX < 0.0f);
                        }
                    }
                    if (this.startedTracking) {
                        float dx2;
                        int duration;
                        float x = DataUsageActivity.this.viewPages[0].getX();
                        DataUsageActivity.this.tabsAnimation = new AnimatorSet();
                        velX = this.velocityTracker.getXVelocity();
                        velY = this.velocityTracker.getYVelocity();
                        DataUsageActivity dataUsageActivity = DataUsageActivity.this;
                        z = Math.abs(x) < ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(velX) < 3500.0f || Math.abs(velX) < Math.abs(velY));
                        dataUsageActivity.backAnimation = z;
                        AnimatorSet access$1700;
                        Animator[] animatorArr;
                        if (DataUsageActivity.this.backAnimation) {
                            dx2 = Math.abs(x);
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
                            dx2 = ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) - Math.abs(x);
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
                        int width = getMeasuredWidth();
                        int halfWidth = width / 2;
                        float distance = ((float) halfWidth) + (((float) halfWidth) * AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (1.0f * dx2) / ((float) width))));
                        velX = Math.abs(velX);
                        if (velX > 0.0f) {
                            duration = Math.round(1000.0f * Math.abs(distance / velX)) * 4;
                        } else {
                            duration = (int) ((1.0f + (dx2 / ((float) getMeasuredWidth()))) * 100.0f);
                        }
                        DataUsageActivity.this.tabsAnimation.setDuration((long) Math.max(150, Math.min(duration, 600)));
                        DataUsageActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                DataUsageActivity.this.tabsAnimation = null;
                                if (DataUsageActivity.this.backAnimation) {
                                    DataUsageActivity.this.viewPages[1].setVisibility(8);
                                } else {
                                    boolean z;
                                    ViewPage tempPage = DataUsageActivity.this.viewPages[0];
                                    DataUsageActivity.this.viewPages[0] = DataUsageActivity.this.viewPages[1];
                                    DataUsageActivity.this.viewPages[1] = tempPage;
                                    DataUsageActivity.this.viewPages[1].setVisibility(8);
                                    DataUsageActivity dataUsageActivity = DataUsageActivity.this;
                                    if (DataUsageActivity.this.viewPages[0].selectedType == DataUsageActivity.this.scrollSlidingTextTabStrip.getFirstTabId()) {
                                        z = true;
                                    } else {
                                        z = false;
                                    }
                                    dataUsageActivity.swipeBackEnabled = z;
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
                    if (this.velocityTracker != null) {
                        this.velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                }
                return this.startedTracking;
            }
        };
        this.fragmentView = frameLayout;
        frameLayout.setWillNotDraw(false);
        int scrollToPositionOnRecreate = -1;
        int scrollToOffsetOnRecreate = 0;
        int a = 0;
        while (a < this.viewPages.length) {
            if (!(a != 0 || this.viewPages[a] == null || this.viewPages[a].layoutManager == null)) {
                scrollToPositionOnRecreate = this.viewPages[a].layoutManager.findFirstVisibleItemPosition();
                if (scrollToPositionOnRecreate != this.viewPages[a].layoutManager.getItemCount() - 1) {
                    Holder holder = (Holder) this.viewPages[a].listView.findViewHolderForAdapterPosition(scrollToPositionOnRecreate);
                    if (holder != null) {
                        scrollToOffsetOnRecreate = holder.itemView.getTop();
                    } else {
                        scrollToPositionOnRecreate = -1;
                    }
                } else {
                    scrollToPositionOnRecreate = -1;
                }
            }
            ViewPage ViewPage = new ViewPage(context) {
                public void setTranslationX(float translationX) {
                    super.setTranslationX(translationX);
                    if (DataUsageActivity.this.tabsAnimationInProgress && DataUsageActivity.this.viewPages[0] == this) {
                        DataUsageActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DataUsageActivity.this.viewPages[1].selectedType, Math.abs(DataUsageActivity.this.viewPages[0].getTranslationX()) / ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                }
            };
            frameLayout.addView(ViewPage, LayoutHelper.createFrame(-1, -1.0f));
            this.viewPages[a] = ViewPage;
            LinearLayoutManager layoutManager = this.viewPages[a].layoutManager = new LinearLayoutManager(context, 1, false) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            RecyclerListView listView = new RecyclerListView(context);
            this.viewPages[a].listView = listView;
            this.viewPages[a].listView.setItemAnimator(null);
            this.viewPages[a].listView.setClipToPadding(false);
            this.viewPages[a].listView.setSectionsType(2);
            this.viewPages[a].listView.setLayoutManager(layoutManager);
            this.viewPages[a].addView(this.viewPages[a].listView, LayoutHelper.createFrame(-1, -1.0f));
            this.viewPages[a].listView.setOnItemClickListener(new DataUsageActivity$$Lambda$0(this, listView));
            this.viewPages[a].listView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState != 1) {
                        int scrollY = (int) (-DataUsageActivity.this.actionBar.getTranslationY());
                        int actionBarHeight = ActionBar.getCurrentActionBarHeight();
                        if (scrollY != 0 && scrollY != actionBarHeight) {
                            if (scrollY < actionBarHeight / 2) {
                                DataUsageActivity.this.viewPages[0].listView.smoothScrollBy(0, -scrollY);
                            } else {
                                DataUsageActivity.this.viewPages[0].listView.smoothScrollBy(0, actionBarHeight - scrollY);
                            }
                        }
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (recyclerView == DataUsageActivity.this.viewPages[0].listView) {
                        float currentTranslation = DataUsageActivity.this.actionBar.getTranslationY();
                        float newTranslation = currentTranslation - ((float) dy);
                        if (newTranslation < ((float) (-ActionBar.getCurrentActionBarHeight()))) {
                            newTranslation = (float) (-ActionBar.getCurrentActionBarHeight());
                        } else if (newTranslation > 0.0f) {
                            newTranslation = 0.0f;
                        }
                        if (newTranslation != currentTranslation) {
                            DataUsageActivity.this.setScrollY(newTranslation);
                        }
                    }
                }
            });
            if (a == 0 && scrollToPositionOnRecreate != -1) {
                layoutManager.scrollToPositionWithOffset(scrollToPositionOnRecreate, scrollToOffsetOnRecreate);
            }
            if (a != 0) {
                this.viewPages[a].setVisibility(8);
            }
            a++;
        }
        frameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        updateTabs();
        switchToCurrentSelectedMode(false);
        this.swipeBackEnabled = this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId();
        return this.fragmentView;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$2$DataUsageActivity(RecyclerListView listView, View view, int position) {
        if (getParentActivity() != null) {
            ListAdapter adapter = (ListAdapter) listView.getAdapter();
            if (position == adapter.resetRow) {
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setMessage(LocaleController.getString("ResetStatisticsAlert", NUM));
                builder.setPositiveButton(LocaleController.getString("Reset", NUM), new DataUsageActivity$$Lambda$1(this, adapter));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                showDialog(builder.create());
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$1$DataUsageActivity(ListAdapter adapter, DialogInterface dialogInterface, int i) {
        StatsController.getInstance(this.currentAccount).resetStats(adapter.currentType);
        adapter.notifyDataSetChanged();
    }

    public void onResume() {
        super.onResume();
        if (this.mobileAdapter != null) {
            this.mobileAdapter.notifyDataSetChanged();
        }
        if (this.wifiAdapter != null) {
            this.wifiAdapter.notifyDataSetChanged();
        }
        if (this.roamingAdapter != null) {
            this.roamingAdapter.notifyDataSetChanged();
        }
    }

    private void setScrollY(float value) {
        this.actionBar.setTranslationY(value);
        for (ViewPage access$900 : this.viewPages) {
            access$900.listView.setPinnedSectionOffsetY((int) value);
        }
        this.fragmentView.invalidate();
    }

    private void updateTabs() {
        if (this.scrollSlidingTextTabStrip != null) {
            this.scrollSlidingTextTabStrip.addTextTab(0, LocaleController.getString("NetworkUsageMobile", NUM));
            this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("NetworkUsageWiFi", NUM));
            this.scrollSlidingTextTabStrip.addTextTab(2, LocaleController.getString("NetworkUsageRoaming", NUM));
            this.scrollSlidingTextTabStrip.setVisibility(0);
            this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
            int id = this.scrollSlidingTextTabStrip.getCurrentTabId();
            if (id >= 0) {
                this.viewPages[0].selectedType = id;
            }
            this.scrollSlidingTextTabStrip.finishAddingTabs();
        }
    }

    private void switchToCurrentSelectedMode(boolean animated) {
        int a;
        for (ViewPage access$900 : this.viewPages) {
            access$900.listView.stopScroll();
        }
        if (animated) {
            a = 1;
        } else {
            a = 0;
        }
        Adapter currentAdapter = this.viewPages[a].listView.getAdapter();
        this.viewPages[a].listView.setPinnedHeaderShadowDrawable(null);
        if (this.viewPages[a].selectedType == 0) {
            if (currentAdapter != this.mobileAdapter) {
                this.viewPages[a].listView.setAdapter(this.mobileAdapter);
            }
        } else if (this.viewPages[a].selectedType == 1) {
            if (currentAdapter != this.wifiAdapter) {
                this.viewPages[a].listView.setAdapter(this.wifiAdapter);
            }
        } else if (this.viewPages[a].selectedType == 2 && currentAdapter != this.roamingAdapter) {
            this.viewPages[a].listView.setAdapter(this.roamingAdapter);
        }
        this.viewPages[a].listView.setVisibility(0);
        if (this.actionBar.getTranslationY() != 0.0f) {
            this.viewPages[a].layoutManager.scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, 0, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, "actionBarDefaultSubtitle"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(null, 0, null, this.scrollSlidingTextTabStrip.getRectPaint(), null, null, "actionBarDefaultTitle"));
        for (int a = 0; a < this.viewPages.length; a++) {
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class}, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader"));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4"));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText"));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteRedText2"));
        }
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
    }
}
