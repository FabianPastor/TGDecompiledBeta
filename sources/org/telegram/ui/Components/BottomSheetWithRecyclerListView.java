package org.telegram.ui.Components;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;

public abstract class BottomSheetWithRecyclerListView extends BottomSheet {
    protected ActionBar actionBar;
    private BaseFragment baseFragment;
    protected boolean clipToActionBar;
    /* access modifiers changed from: private */
    public int contentHeight;
    public final boolean hasFixedSize;
    protected RecyclerListView recyclerListView;
    public float topPadding = 0.4f;
    boolean wasDrawn;

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    /* access modifiers changed from: protected */
    public abstract RecyclerListView.SelectionAdapter createAdapter();

    /* access modifiers changed from: protected */
    public abstract CharSequence getTitle();

    /* access modifiers changed from: protected */
    public void onPreDraw(Canvas canvas, int i, float f) {
    }

    /* access modifiers changed from: protected */
    public void onPreMeasure(int i, int i2) {
    }

    public void onViewCreated(FrameLayout frameLayout) {
    }

    public BottomSheetWithRecyclerListView(BaseFragment baseFragment2, boolean z, final boolean z2) {
        super(baseFragment2.getParentActivity(), z);
        this.baseFragment = baseFragment2;
        this.hasFixedSize = z2;
        final Activity parentActivity = baseFragment2.getParentActivity();
        final Drawable mutate = ContextCompat.getDrawable(parentActivity, R.drawable.header_shadow).mutate();
        final AnonymousClass1 r0 = new FrameLayout(parentActivity) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int unused = BottomSheetWithRecyclerListView.this.contentHeight = View.MeasureSpec.getSize(i2);
                BottomSheetWithRecyclerListView.this.onPreMeasure(i, i2);
                super.onMeasure(i, i2);
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                if (!z2) {
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition = BottomSheetWithRecyclerListView.this.recyclerListView.findViewHolderForAdapterPosition(0);
                    int i = -AndroidUtilities.dp(16.0f);
                    if (findViewHolderForAdapterPosition != null) {
                        i = findViewHolderForAdapterPosition.itemView.getBottom() - AndroidUtilities.dp(16.0f);
                    }
                    float dp = 1.0f - (((float) (AndroidUtilities.dp(16.0f) + i)) / ((float) AndroidUtilities.dp(56.0f)));
                    if (dp < 0.0f) {
                        dp = 0.0f;
                    }
                    BottomSheetWithRecyclerListView bottomSheetWithRecyclerListView = BottomSheetWithRecyclerListView.this;
                    AndroidUtilities.updateViewVisibilityAnimated(bottomSheetWithRecyclerListView.actionBar, dp != 0.0f, 1.0f, bottomSheetWithRecyclerListView.wasDrawn);
                    BottomSheetWithRecyclerListView.this.shadowDrawable.setBounds(0, i, getMeasuredWidth(), getMeasuredHeight());
                    BottomSheetWithRecyclerListView.this.shadowDrawable.draw(canvas);
                    BottomSheetWithRecyclerListView.this.onPreDraw(canvas, i, dp);
                }
                super.dispatchDraw(canvas);
                ActionBar actionBar = BottomSheetWithRecyclerListView.this.actionBar;
                if (!(actionBar == null || actionBar.getVisibility() != 0 || BottomSheetWithRecyclerListView.this.actionBar.getAlpha() == 0.0f)) {
                    mutate.setBounds(0, BottomSheetWithRecyclerListView.this.actionBar.getBottom(), getMeasuredWidth(), BottomSheetWithRecyclerListView.this.actionBar.getBottom() + mutate.getIntrinsicHeight());
                    mutate.setAlpha((int) (BottomSheetWithRecyclerListView.this.actionBar.getAlpha() * 255.0f));
                    mutate.draw(canvas);
                }
                BottomSheetWithRecyclerListView.this.wasDrawn = true;
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (!z2) {
                    BottomSheetWithRecyclerListView bottomSheetWithRecyclerListView = BottomSheetWithRecyclerListView.this;
                    if (bottomSheetWithRecyclerListView.clipToActionBar && view == bottomSheetWithRecyclerListView.recyclerListView) {
                        canvas.save();
                        canvas.clipRect(0, BottomSheetWithRecyclerListView.this.actionBar.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
                        super.drawChild(canvas, view, j);
                        canvas.restore();
                        return true;
                    }
                }
                return super.drawChild(canvas, view, j);
            }

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && motionEvent.getY() < ((float) BottomSheetWithRecyclerListView.this.shadowDrawable.getBounds().top)) {
                    BottomSheetWithRecyclerListView.this.dismiss();
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        };
        RecyclerListView recyclerListView2 = new RecyclerListView(parentActivity);
        this.recyclerListView = recyclerListView2;
        recyclerListView2.setLayoutManager(new LinearLayoutManager(parentActivity));
        final RecyclerListView.SelectionAdapter createAdapter = createAdapter();
        if (z2) {
            this.recyclerListView.setHasFixedSize(true);
            this.recyclerListView.setAdapter(createAdapter);
            setCustomView(r0);
            r0.addView(this.recyclerListView, LayoutHelper.createFrame(-1, -2.0f));
        } else {
            this.recyclerListView.setAdapter(new RecyclerListView.SelectionAdapter() {
                public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                    return createAdapter.isEnabled(viewHolder);
                }

                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    if (i == -1000) {
                        return new RecyclerListView.Holder(new View(parentActivity) {
                            /* access modifiers changed from: protected */
                            public void onMeasure(int i, int i2) {
                                int i3;
                                if (BottomSheetWithRecyclerListView.this.contentHeight == 0) {
                                    i3 = AndroidUtilities.dp(300.0f);
                                } else {
                                    i3 = (int) (((float) BottomSheetWithRecyclerListView.this.contentHeight) * BottomSheetWithRecyclerListView.this.topPadding);
                                }
                                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(i3, NUM));
                            }
                        });
                    }
                    return createAdapter.onCreateViewHolder(viewGroup, i);
                }

                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                    if (i != 0) {
                        createAdapter.onBindViewHolder(viewHolder, i - 1);
                    }
                }

                public int getItemViewType(int i) {
                    if (i == 0) {
                        return -1000;
                    }
                    return createAdapter.getItemViewType(i - 1);
                }

                public int getItemCount() {
                    return createAdapter.getItemCount() + 1;
                }
            });
            this.containerView = r0;
            AnonymousClass3 r10 = new ActionBar(parentActivity) {
                public void setAlpha(float f) {
                    if (getAlpha() != f) {
                        super.setAlpha(f);
                        r0.invalidate();
                    }
                }

                public void setTag(Object obj) {
                    super.setTag(obj);
                    BottomSheetWithRecyclerListView.this.updateStatusBar();
                }
            };
            this.actionBar = r10;
            r10.setBackgroundColor(getThemedColor("dialogBackground"));
            this.actionBar.setTitleColor(getThemedColor("windowBackgroundWhiteBlackText"));
            this.actionBar.setItemsBackgroundColor(getThemedColor("actionBarActionModeDefaultSelector"), false);
            this.actionBar.setItemsColor(getThemedColor("actionBarActionModeDefaultIcon"), false);
            this.actionBar.setCastShadows(true);
            this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            this.actionBar.setTitle(getTitle());
            this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                public void onItemClick(int i) {
                    if (i == -1) {
                        BottomSheetWithRecyclerListView.this.dismiss();
                    }
                }
            });
            r0.addView(this.recyclerListView);
            r0.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f, 0, 6.0f, 0.0f, 6.0f, 0.0f));
            this.recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener(this) {
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    super.onScrolled(recyclerView, i, i2);
                    r0.invalidate();
                }
            });
        }
        onViewCreated(r0);
        updateStatusBar();
    }

    private boolean isLightStatusBar() {
        return ColorUtils.calculateLuminance(Theme.getColor("dialogBackground")) > 0.699999988079071d;
    }

    public void notifyDataSetChanged() {
        this.recyclerListView.getAdapter().notifyDataSetChanged();
    }

    public BaseFragment getBaseFragment() {
        return this.baseFragment;
    }

    /* access modifiers changed from: private */
    public void updateStatusBar() {
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null && actionBar2.getTag() != null) {
            AndroidUtilities.setLightStatusBar(getWindow(), isLightStatusBar());
        } else if (this.baseFragment != null) {
            AndroidUtilities.setLightStatusBar(getWindow(), this.baseFragment.isLightStatusBar());
        }
    }
}
