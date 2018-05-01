package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.SharingLiveLocationCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.StickerPreviewViewer;

public class SharingLocationsAlert extends BottomSheet implements NotificationCenterDelegate {
    private ListAdapter adapter;
    private SharingLocationsAlertDelegate delegate;
    private boolean ignoreLayout;
    private RecyclerListView listView;
    private int reqId;
    private int scrollOffsetY;
    private Drawable shadowDrawable;
    private TextView textView;
    private Pattern urlPattern;

    /* renamed from: org.telegram.ui.Components.SharingLocationsAlert$5 */
    class C13015 implements OnClickListener {
        C13015() {
        }

        public void onClick(View view) {
            for (view = null; view < 3; view++) {
                LocationController.getInstance(view).removeAllLocationSharings();
            }
            SharingLocationsAlert.this.dismiss();
        }
    }

    /* renamed from: org.telegram.ui.Components.SharingLocationsAlert$6 */
    class C13026 implements OnClickListener {
        C13026() {
        }

        public void onClick(View view) {
            SharingLocationsAlert.this.dismiss();
        }
    }

    public interface SharingLocationsAlertDelegate {
        void didSelectLocation(SharingLocationInfo sharingLocationInfo);
    }

    /* renamed from: org.telegram.ui.Components.SharingLocationsAlert$3 */
    class C20843 extends OnScrollListener {
        C20843() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            SharingLocationsAlert.this.updateLayout();
        }
    }

    /* renamed from: org.telegram.ui.Components.SharingLocationsAlert$4 */
    class C20854 implements OnItemClickListener {
        C20854() {
        }

        public void onItemClick(View view, int i) {
            i--;
            if (i >= 0) {
                if (i < LocationController.getLocationsCount()) {
                    SharingLocationsAlert.this.delegate.didSelectLocation(SharingLocationsAlert.this.getLocation(i));
                    SharingLocationsAlert.this.dismiss();
                }
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context context;

        public int getItemViewType(int i) {
            return i == 0 ? 1 : 0;
        }

        public ListAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            return LocationController.getLocationsCount() + 1;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == null ? true : null;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                i = new FrameLayout(this.context) {
                    protected void onMeasure(int i, int i2) {
                        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + 1, NUM));
                    }

                    protected void onDraw(Canvas canvas) {
                        canvas.drawLine(0.0f, (float) AndroidUtilities.dp(40.0f), (float) getMeasuredWidth(), (float) AndroidUtilities.dp(40.0f), Theme.dividerPaint);
                    }
                };
                i.setWillNotDraw(false);
                SharingLocationsAlert.this.textView = new TextView(this.context);
                SharingLocationsAlert.this.textView.setTextColor(Theme.getColor(Theme.key_dialogIcon));
                SharingLocationsAlert.this.textView.setTextSize(1, 14.0f);
                SharingLocationsAlert.this.textView.setGravity(17);
                SharingLocationsAlert.this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
                i.addView(SharingLocationsAlert.this.textView, LayoutHelper.createFrame(-1, 40.0f));
            } else {
                i = new SharingLiveLocationCell(this.context, false);
            }
            return new Holder(i);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            switch (viewHolder.getItemViewType()) {
                case 0:
                    ((SharingLiveLocationCell) viewHolder.itemView).setDialog(SharingLocationsAlert.this.getLocation(i - 1));
                    return;
                case 1:
                    if (SharingLocationsAlert.this.textView != null) {
                        SharingLocationsAlert.this.textView.setText(LocaleController.formatString("SharingLiveLocationTitle", C0446R.string.SharingLiveLocationTitle, LocaleController.formatPluralString("Chats", LocationController.getLocationsCount())));
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    public SharingLocationsAlert(Context context, SharingLocationsAlertDelegate sharingLocationsAlertDelegate) {
        super(context, false);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
        this.delegate = sharingLocationsAlertDelegate;
        this.shadowDrawable = context.getResources().getDrawable(C0446R.drawable.sheet_shadow).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), Mode.MULTIPLY));
        this.containerView = new FrameLayout(context) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || SharingLocationsAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) SharingLocationsAlert.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                SharingLocationsAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return (SharingLocationsAlert.this.isDismissed() || super.onTouchEvent(motionEvent) == null) ? null : true;
            }

            protected void onMeasure(int i, int i2) {
                i2 = MeasureSpec.getSize(i2);
                if (VERSION.SDK_INT >= 21) {
                    i2 -= AndroidUtilities.statusBarHeight;
                }
                getMeasuredWidth();
                int dp = ((AndroidUtilities.dp(56.0f) + AndroidUtilities.dp(56.0f)) + 1) + (LocationController.getLocationsCount() * AndroidUtilities.dp(54.0f));
                int i3 = i2 / 5;
                if (dp < i3 * 3) {
                    i3 = AndroidUtilities.dp(8.0f);
                } else {
                    i3 *= 2;
                    if (dp < i2) {
                        i3 -= i2 - dp;
                    }
                }
                if (SharingLocationsAlert.this.listView.getPaddingTop() != i3) {
                    SharingLocationsAlert.this.ignoreLayout = true;
                    SharingLocationsAlert.this.listView.setPadding(0, i3, 0, AndroidUtilities.dp(8.0f));
                    SharingLocationsAlert.this.ignoreLayout = false;
                }
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(Math.min(dp, i2), NUM));
            }

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                SharingLocationsAlert.this.updateLayout();
            }

            public void requestLayout() {
                if (!SharingLocationsAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            protected void onDraw(Canvas canvas) {
                SharingLocationsAlert.this.shadowDrawable.setBounds(0, SharingLocationsAlert.this.scrollOffsetY - SharingLocationsAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                SharingLocationsAlert.this.shadowDrawable.draw(canvas);
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
        this.listView = new RecyclerListView(context) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                boolean onInterceptTouchEvent = StickerPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, SharingLocationsAlert.this.listView, 0, null);
                if (super.onInterceptTouchEvent(motionEvent) != null || onInterceptTouchEvent) {
                    return true;
                }
                return false;
            }

            public void requestLayout() {
                if (!SharingLocationsAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.listView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        sharingLocationsAlertDelegate = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        sharingLocationsAlertDelegate.setAdapter(listAdapter);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setClipToPadding(false);
        this.listView.setEnabled(true);
        this.listView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        this.listView.setOnScrollListener(new C20843());
        this.listView.setOnItemClickListener(new C20854());
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        sharingLocationsAlertDelegate = new View(context);
        sharingLocationsAlertDelegate.setBackgroundResource(C0446R.drawable.header_shadow_reverse);
        this.containerView.addView(sharingLocationsAlertDelegate, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        sharingLocationsAlertDelegate = new PickerBottomLayout(context, false);
        sharingLocationsAlertDelegate.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.containerView.addView(sharingLocationsAlertDelegate, LayoutHelper.createFrame(-1, 48, 83));
        sharingLocationsAlertDelegate.cancelButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        sharingLocationsAlertDelegate.cancelButton.setTextColor(Theme.getColor(Theme.key_dialogTextRed));
        sharingLocationsAlertDelegate.cancelButton.setText(LocaleController.getString("StopAllLocationSharings", C0446R.string.StopAllLocationSharings));
        sharingLocationsAlertDelegate.cancelButton.setOnClickListener(new C13015());
        sharingLocationsAlertDelegate.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        sharingLocationsAlertDelegate.doneButtonTextView.setText(LocaleController.getString("Close", C0446R.string.Close).toUpperCase());
        sharingLocationsAlertDelegate.doneButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        sharingLocationsAlertDelegate.doneButton.setOnClickListener(new C13026());
        sharingLocationsAlertDelegate.doneButtonBadgeTextView.setVisibility(8);
        this.adapter.notifyDataSetChanged();
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.liveLocationsChanged) {
            return;
        }
        if (LocationController.getLocationsCount() == 0) {
            dismiss();
        } else {
            this.adapter.notifyDataSetChanged();
        }
    }

    private SharingLocationInfo getLocation(int i) {
        for (int i2 = 0; i2 < 3; i2++) {
            ArrayList arrayList = LocationController.getInstance(i2).sharingLocationsUI;
            if (i < arrayList.size()) {
                return (SharingLocationInfo) arrayList.get(i);
            }
            i -= arrayList.size();
        }
        return 0;
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
    }
}
