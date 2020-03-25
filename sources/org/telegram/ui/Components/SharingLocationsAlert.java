package org.telegram.ui.Components;

import android.annotation.SuppressLint;
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
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    /* access modifiers changed from: private */
    public TextView textView;

    public interface SharingLocationsAlertDelegate {
        void didSelectLocation(LocationController.SharingLocationInfo sharingLocationInfo);
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public SharingLocationsAlert(Context context, SharingLocationsAlertDelegate sharingLocationsAlertDelegate) {
        super(context, false);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
        this.delegate = sharingLocationsAlertDelegate;
        Drawable mutate = context.getResources().getDrawable(NUM).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        AnonymousClass1 r12 = new FrameLayout(context) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || SharingLocationsAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) SharingLocationsAlert.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                SharingLocationsAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return !SharingLocationsAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int i3;
                int size = View.MeasureSpec.getSize(i2);
                if (Build.VERSION.SDK_INT >= 21) {
                    size -= AndroidUtilities.statusBarHeight;
                }
                getMeasuredWidth();
                int dp = AndroidUtilities.dp(56.0f) + AndroidUtilities.dp(56.0f) + 1 + (LocationController.getLocationsCount() * AndroidUtilities.dp(54.0f));
                int i4 = size / 5;
                if (dp < i4 * 3) {
                    i3 = AndroidUtilities.dp(8.0f);
                } else {
                    i3 = i4 * 2;
                    if (dp < size) {
                        i3 -= size - dp;
                    }
                }
                if (SharingLocationsAlert.this.listView.getPaddingTop() != i3) {
                    boolean unused = SharingLocationsAlert.this.ignoreLayout = true;
                    SharingLocationsAlert.this.listView.setPadding(0, i3, 0, AndroidUtilities.dp(8.0f));
                    boolean unused2 = SharingLocationsAlert.this.ignoreLayout = false;
                }
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(Math.min(dp, size), NUM));
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
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
        this.containerView = r12;
        r12.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, 0, i, 0);
        AnonymousClass2 r122 = new RecyclerListView(context) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                boolean onInterceptTouchEvent = ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, SharingLocationsAlert.this.listView, 0, (ContentPreviewViewer.ContentPreviewViewerDelegate) null);
                if (super.onInterceptTouchEvent(motionEvent) || onInterceptTouchEvent) {
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
        this.listView = r122;
        r122.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setClipToPadding(false);
        this.listView.setEnabled(true);
        this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                SharingLocationsAlert.this.updateLayout();
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                SharingLocationsAlert.this.lambda$new$0$SharingLocationsAlert(view, i);
            }
        });
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        View view = new View(context);
        view.setBackgroundResource(NUM);
        this.containerView.addView(view, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        PickerBottomLayout pickerBottomLayout = new PickerBottomLayout(context, false);
        pickerBottomLayout.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.containerView.addView(pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        pickerBottomLayout.cancelButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        pickerBottomLayout.cancelButton.setTextColor(Theme.getColor("dialogTextRed"));
        pickerBottomLayout.cancelButton.setText(LocaleController.getString("StopAllLocationSharings", NUM));
        pickerBottomLayout.cancelButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SharingLocationsAlert.this.lambda$new$1$SharingLocationsAlert(view);
            }
        });
        pickerBottomLayout.doneButtonTextView.setTextColor(Theme.getColor("dialogTextBlue2"));
        pickerBottomLayout.doneButtonTextView.setText(LocaleController.getString("Close", NUM).toUpperCase());
        pickerBottomLayout.doneButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        pickerBottomLayout.doneButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SharingLocationsAlert.this.lambda$new$2$SharingLocationsAlert(view);
            }
        });
        pickerBottomLayout.doneButtonBadgeTextView.setVisibility(8);
        this.adapter.notifyDataSetChanged();
    }

    public /* synthetic */ void lambda$new$0$SharingLocationsAlert(View view, int i) {
        int i2 = i - 1;
        if (i2 >= 0 && i2 < LocationController.getLocationsCount()) {
            this.delegate.didSelectLocation(getLocation(i2));
            dismiss();
        }
    }

    public /* synthetic */ void lambda$new$1$SharingLocationsAlert(View view) {
        for (int i = 0; i < 3; i++) {
            LocationController.getInstance(i).removeAllLocationSharings();
        }
        dismiss();
    }

    public /* synthetic */ void lambda$new$2$SharingLocationsAlert(View view) {
        dismiss();
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        int i = 0;
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop() - AndroidUtilities.dp(8.0f);
        if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
            i = top;
        }
        if (this.scrollOffsetY != i) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = i;
            recyclerListView2.setTopGlowOffset(i);
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

    /* access modifiers changed from: private */
    public LocationController.SharingLocationInfo getLocation(int i) {
        for (int i2 = 0; i2 < 3; i2++) {
            ArrayList<LocationController.SharingLocationInfo> arrayList = LocationController.getInstance(i2).sharingLocationsUI;
            if (i < arrayList.size()) {
                return arrayList.get(i);
            }
            i -= arrayList.size();
        }
        return null;
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public int getItemViewType(int i) {
            return i == 0 ? 1 : 0;
        }

        public ListAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            return LocationController.getLocationsCount() + 1;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            if (i != 0) {
                frameLayout = new FrameLayout(this, this.context) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + 1, NUM));
                    }

                    /* access modifiers changed from: protected */
                    public void onDraw(Canvas canvas) {
                        canvas.drawLine(0.0f, (float) AndroidUtilities.dp(40.0f), (float) getMeasuredWidth(), (float) AndroidUtilities.dp(40.0f), Theme.dividerPaint);
                    }
                };
                frameLayout.setWillNotDraw(false);
                TextView unused = SharingLocationsAlert.this.textView = new TextView(this.context);
                SharingLocationsAlert.this.textView.setTextColor(Theme.getColor("dialogIcon"));
                SharingLocationsAlert.this.textView.setTextSize(1, 14.0f);
                SharingLocationsAlert.this.textView.setGravity(17);
                SharingLocationsAlert.this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
                frameLayout.addView(SharingLocationsAlert.this.textView, LayoutHelper.createFrame(-1, 40.0f));
            } else {
                frameLayout = new SharingLiveLocationCell(this.context, false, 54);
            }
            return new RecyclerListView.Holder(frameLayout);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                ((SharingLiveLocationCell) viewHolder.itemView).setDialog(SharingLocationsAlert.this.getLocation(i - 1));
            } else if (itemViewType == 1 && SharingLocationsAlert.this.textView != null) {
                SharingLocationsAlert.this.textView.setText(LocaleController.formatString("SharingLiveLocationTitle", NUM, LocaleController.formatPluralString("Chats", LocationController.getLocationsCount())));
            }
        }
    }
}
