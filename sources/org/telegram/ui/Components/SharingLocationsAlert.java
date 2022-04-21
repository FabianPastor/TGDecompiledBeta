package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.SharingLiveLocationCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ContentPreviewViewer;

public class SharingLocationsAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private ListAdapter adapter;
    private SharingLocationsAlertDelegate delegate;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private int reqId;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    /* access modifiers changed from: private */
    public TextView textView;
    private Pattern urlPattern;

    public interface SharingLocationsAlertDelegate {
        void didSelectLocation(LocationController.SharingLocationInfo sharingLocationInfo);
    }

    public SharingLocationsAlert(Context context, SharingLocationsAlertDelegate sharingLocationsAlertDelegate, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
        this.delegate = sharingLocationsAlertDelegate;
        Drawable mutate = context.getResources().getDrawable(NUM).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        this.containerView = new FrameLayout(context) {
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() != 0 || SharingLocationsAlert.this.scrollOffsetY == 0 || ev.getY() >= ((float) SharingLocationsAlert.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(ev);
                }
                SharingLocationsAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent e) {
                return !SharingLocationsAlert.this.isDismissed() && super.onTouchEvent(e);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int padding;
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                if (Build.VERSION.SDK_INT >= 21) {
                    height -= AndroidUtilities.statusBarHeight;
                }
                int measuredWidth = getMeasuredWidth();
                int contentSize = AndroidUtilities.dp(56.0f) + AndroidUtilities.dp(56.0f) + 1 + (LocationController.getLocationsCount() * AndroidUtilities.dp(54.0f));
                if (contentSize < (height / 5) * 3) {
                    padding = AndroidUtilities.dp(8.0f);
                } else {
                    padding = (height / 5) * 2;
                    if (contentSize < height) {
                        padding -= height - contentSize;
                    }
                }
                if (SharingLocationsAlert.this.listView.getPaddingTop() != padding) {
                    boolean unused = SharingLocationsAlert.this.ignoreLayout = true;
                    SharingLocationsAlert.this.listView.setPadding(0, padding, 0, AndroidUtilities.dp(8.0f));
                    boolean unused2 = SharingLocationsAlert.this.ignoreLayout = false;
                }
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(Math.min(contentSize, height), NUM));
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                SharingLocationsAlert.this.updateLayout();
            }

            public void requestLayout() {
                if (!SharingLocationsAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                SharingLocationsAlert.this.shadowDrawable.setBounds(0, SharingLocationsAlert.this.scrollOffsetY - SharingLocationsAlert.this.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                SharingLocationsAlert.this.shadowDrawable.draw(canvas);
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
        AnonymousClass2 r1 = new RecyclerListView(context) {
            public boolean onInterceptTouchEvent(MotionEvent event) {
                return super.onInterceptTouchEvent(event) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(event, SharingLocationsAlert.this.listView, 0, (ContentPreviewViewer.ContentPreviewViewerDelegate) null, this.resourcesProvider);
            }

            public void requestLayout() {
                if (!SharingLocationsAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.listView = r1;
        r1.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setClipToPadding(false);
        this.listView.setEnabled(true);
        this.listView.setGlowColor(getThemedColor("dialogScrollGlow"));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                SharingLocationsAlert.this.updateLayout();
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new SharingLocationsAlert$$ExternalSyntheticLambda2(this));
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        View shadow = new View(context);
        shadow.setBackgroundResource(NUM);
        this.containerView.addView(shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        PickerBottomLayout pickerBottomLayout = new PickerBottomLayout(context, false);
        pickerBottomLayout.setBackgroundColor(getThemedColor("dialogBackground"));
        this.containerView.addView(pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        pickerBottomLayout.cancelButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        pickerBottomLayout.cancelButton.setTextColor(getThemedColor("dialogTextRed"));
        pickerBottomLayout.cancelButton.setText(LocaleController.getString("StopAllLocationSharings", NUM));
        pickerBottomLayout.cancelButton.setOnClickListener(new SharingLocationsAlert$$ExternalSyntheticLambda0(this));
        pickerBottomLayout.doneButtonTextView.setTextColor(getThemedColor("dialogTextBlue2"));
        pickerBottomLayout.doneButtonTextView.setText(LocaleController.getString("Close", NUM).toUpperCase());
        pickerBottomLayout.doneButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        pickerBottomLayout.doneButton.setOnClickListener(new SharingLocationsAlert$$ExternalSyntheticLambda1(this));
        pickerBottomLayout.doneButtonBadgeTextView.setVisibility(8);
        this.adapter.notifyDataSetChanged();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-SharingLocationsAlert  reason: not valid java name */
    public /* synthetic */ void m4388lambda$new$0$orgtelegramuiComponentsSharingLocationsAlert(View view, int position) {
        int position2 = position - 1;
        if (position2 >= 0 && position2 < LocationController.getLocationsCount()) {
            this.delegate.didSelectLocation(getLocation(position2));
            dismiss();
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-SharingLocationsAlert  reason: not valid java name */
    public /* synthetic */ void m4389lambda$new$1$orgtelegramuiComponentsSharingLocationsAlert(View view) {
        for (int a = 0; a < 3; a++) {
            LocationController.getInstance(a).removeAllLocationSharings();
        }
        dismiss();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-SharingLocationsAlert  reason: not valid java name */
    public /* synthetic */ void m4390lambda$new$2$orgtelegramuiComponentsSharingLocationsAlert(View view) {
        dismiss();
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        int newOffset = 0;
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop() - AndroidUtilities.dp(8.0f);
        if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        if (this.scrollOffsetY != newOffset) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = newOffset;
            recyclerListView2.setTopGlowOffset(newOffset);
            this.containerView.invalidate();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id != NotificationCenter.liveLocationsChanged) {
            return;
        }
        if (LocationController.getLocationsCount() == 0) {
            dismiss();
        } else {
            this.adapter.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public LocationController.SharingLocationInfo getLocation(int position) {
        for (int a = 0; a < 3; a++) {
            ArrayList<LocationController.SharingLocationInfo> infos = LocationController.getInstance(a).sharingLocationsUI;
            if (position < infos.size()) {
                return infos.get(position);
            }
            position -= infos.size();
        }
        return null;
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public ListAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            return LocationController.getLocationsCount() + 1;
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new SharingLiveLocationCell(this.context, false, 54, SharingLocationsAlert.this.resourcesProvider);
                    break;
                default:
                    FrameLayout frameLayout = new FrameLayout(this.context) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + 1, NUM));
                        }

                        /* access modifiers changed from: protected */
                        public void onDraw(Canvas canvas) {
                            canvas.drawLine(0.0f, (float) AndroidUtilities.dp(40.0f), (float) getMeasuredWidth(), (float) AndroidUtilities.dp(40.0f), Theme.dividerPaint);
                        }
                    };
                    frameLayout.setWillNotDraw(false);
                    TextView unused = SharingLocationsAlert.this.textView = new TextView(this.context);
                    SharingLocationsAlert.this.textView.setTextColor(SharingLocationsAlert.this.getThemedColor("dialogIcon"));
                    SharingLocationsAlert.this.textView.setTextSize(1, 14.0f);
                    SharingLocationsAlert.this.textView.setGravity(17);
                    SharingLocationsAlert.this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
                    frameLayout.addView(SharingLocationsAlert.this.textView, LayoutHelper.createFrame(-1, 40.0f));
                    view = frameLayout;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    ((SharingLiveLocationCell) holder.itemView).setDialog(SharingLocationsAlert.this.getLocation(position - 1));
                    return;
                case 1:
                    if (SharingLocationsAlert.this.textView != null) {
                        SharingLocationsAlert.this.textView.setText(LocaleController.formatString("SharingLiveLocationTitle", NUM, LocaleController.formatPluralString("Chats", LocationController.getLocationsCount())));
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }
}
