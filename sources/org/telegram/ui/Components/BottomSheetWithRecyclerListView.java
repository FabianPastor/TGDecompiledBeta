package org.telegram.ui.Components;

import android.content.Context;
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
    public abstract RecyclerListView.SelectionAdapter createAdapter();

    /* access modifiers changed from: protected */
    public abstract CharSequence getTitle();

    public BottomSheetWithRecyclerListView(BaseFragment fragment, boolean needFocus, final boolean hasFixedSize2) {
        super(fragment.getParentActivity(), needFocus);
        this.baseFragment = fragment;
        this.hasFixedSize = hasFixedSize2;
        final Context context = fragment.getParentActivity();
        final Drawable headerShadowDrawable = ContextCompat.getDrawable(context, NUM).mutate();
        final FrameLayout containerView = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int unused = BottomSheetWithRecyclerListView.this.contentHeight = View.MeasureSpec.getSize(heightMeasureSpec);
                BottomSheetWithRecyclerListView.this.onPreMeasure(widthMeasureSpec, heightMeasureSpec);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                if (!hasFixedSize2) {
                    RecyclerView.ViewHolder holder = BottomSheetWithRecyclerListView.this.recyclerListView.findViewHolderForAdapterPosition(0);
                    int top = -AndroidUtilities.dp(16.0f);
                    if (holder != null) {
                        top = holder.itemView.getBottom() - AndroidUtilities.dp(16.0f);
                    }
                    float progressToFullView = 1.0f - (((float) (AndroidUtilities.dp(16.0f) + top)) / ((float) AndroidUtilities.dp(56.0f)));
                    if (progressToFullView < 0.0f) {
                        progressToFullView = 0.0f;
                    }
                    AndroidUtilities.updateViewVisibilityAnimated(BottomSheetWithRecyclerListView.this.actionBar, progressToFullView != 0.0f, 1.0f, BottomSheetWithRecyclerListView.this.wasDrawn);
                    BottomSheetWithRecyclerListView.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), getMeasuredHeight());
                    BottomSheetWithRecyclerListView.this.shadowDrawable.draw(canvas);
                    BottomSheetWithRecyclerListView.this.onPreDraw(canvas, top, progressToFullView);
                }
                super.dispatchDraw(canvas);
                if (!(BottomSheetWithRecyclerListView.this.actionBar == null || BottomSheetWithRecyclerListView.this.actionBar.getVisibility() != 0 || BottomSheetWithRecyclerListView.this.actionBar.getAlpha() == 0.0f)) {
                    headerShadowDrawable.setBounds(0, BottomSheetWithRecyclerListView.this.actionBar.getBottom(), getMeasuredWidth(), BottomSheetWithRecyclerListView.this.actionBar.getBottom() + headerShadowDrawable.getIntrinsicHeight());
                    headerShadowDrawable.setAlpha((int) (BottomSheetWithRecyclerListView.this.actionBar.getAlpha() * 255.0f));
                    headerShadowDrawable.draw(canvas);
                }
                BottomSheetWithRecyclerListView.this.wasDrawn = true;
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (hasFixedSize2 || !BottomSheetWithRecyclerListView.this.clipToActionBar || child != BottomSheetWithRecyclerListView.this.recyclerListView) {
                    return super.drawChild(canvas, child, drawingTime);
                }
                canvas.save();
                canvas.clipRect(0, BottomSheetWithRecyclerListView.this.actionBar.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
                super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return true;
            }

            public boolean dispatchTouchEvent(MotionEvent event) {
                if (event.getAction() == 0 && event.getY() < ((float) BottomSheetWithRecyclerListView.this.shadowDrawable.getBounds().top)) {
                    BottomSheetWithRecyclerListView.this.dismiss();
                }
                return super.dispatchTouchEvent(event);
            }
        };
        RecyclerListView recyclerListView2 = new RecyclerListView(context);
        this.recyclerListView = recyclerListView2;
        recyclerListView2.setLayoutManager(new LinearLayoutManager(context));
        final RecyclerListView.SelectionAdapter adapter = createAdapter();
        if (hasFixedSize2) {
            this.recyclerListView.setHasFixedSize(true);
            this.recyclerListView.setAdapter(adapter);
            setCustomView(containerView);
            containerView.addView(this.recyclerListView, LayoutHelper.createFrame(-1, -2.0f));
        } else {
            this.recyclerListView.setAdapter(new RecyclerListView.SelectionAdapter() {
                public boolean isEnabled(RecyclerView.ViewHolder holder) {
                    return adapter.isEnabled(holder);
                }

                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    if (viewType == -1000) {
                        return new RecyclerListView.Holder(new View(context) {
                            /* access modifiers changed from: protected */
                            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                                int h;
                                if (BottomSheetWithRecyclerListView.this.contentHeight == 0) {
                                    h = AndroidUtilities.dp(300.0f);
                                } else {
                                    h = (int) (((float) BottomSheetWithRecyclerListView.this.contentHeight) * BottomSheetWithRecyclerListView.this.topPadding);
                                }
                                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(h, NUM));
                            }
                        });
                    }
                    return adapter.onCreateViewHolder(parent, viewType);
                }

                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    if (position != 0) {
                        adapter.onBindViewHolder(holder, position - 1);
                    }
                }

                public int getItemViewType(int position) {
                    if (position == 0) {
                        return -1000;
                    }
                    return adapter.getItemViewType(position - 1);
                }

                public int getItemCount() {
                    return adapter.getItemCount() + 1;
                }
            });
            this.containerView = containerView;
            AnonymousClass3 r5 = new ActionBar(context) {
                public void setAlpha(float alpha) {
                    if (getAlpha() != alpha) {
                        super.setAlpha(alpha);
                        containerView.invalidate();
                    }
                }

                public void setTag(Object tag) {
                    super.setTag(tag);
                    BottomSheetWithRecyclerListView.this.updateStatusBar();
                }
            };
            this.actionBar = r5;
            r5.setBackgroundColor(getThemedColor("dialogBackground"));
            this.actionBar.setTitleColor(getThemedColor("windowBackgroundWhiteBlackText"));
            this.actionBar.setItemsBackgroundColor(getThemedColor("actionBarActionModeDefaultSelector"), false);
            this.actionBar.setItemsColor(getThemedColor("actionBarActionModeDefaultIcon"), false);
            this.actionBar.setCastShadows(true);
            this.actionBar.setBackButtonImage(NUM);
            this.actionBar.setTitle(getTitle());
            this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                public void onItemClick(int id) {
                    if (id == -1) {
                        BottomSheetWithRecyclerListView.this.dismiss();
                    }
                }
            });
            containerView.addView(this.recyclerListView);
            containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f, 0, 6.0f, 0.0f, 6.0f, 0.0f));
            this.recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    containerView.invalidate();
                }
            });
        }
        onViewCreated(containerView);
        updateStatusBar();
    }

    /* access modifiers changed from: protected */
    public void onPreMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    }

    /* access modifiers changed from: protected */
    public void onPreDraw(Canvas canvas, int top, float progressToFullView) {
    }

    private boolean isLightStatusBar() {
        return ColorUtils.calculateLuminance(Theme.getColor("dialogBackground")) > 0.699999988079071d;
    }

    public void onViewCreated(FrameLayout containerView) {
    }

    public void notifyDataSetChanged() {
        this.recyclerListView.getAdapter().notifyDataSetChanged();
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
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
