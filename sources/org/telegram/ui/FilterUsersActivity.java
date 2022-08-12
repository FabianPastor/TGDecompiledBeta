package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Property;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.ScrollView;
import androidx.annotation.Keep;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RecyclerListView;

public class FilterUsersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, View.OnClickListener {
    /* access modifiers changed from: private */
    public GroupCreateAdapter adapter;
    /* access modifiers changed from: private */
    public ArrayList<GroupCreateSpan> allSpans = new ArrayList<>();
    /* access modifiers changed from: private */
    public int containerHeight;
    /* access modifiers changed from: private */
    public GroupCreateSpan currentDeletingSpan;
    private FilterUsersActivityDelegate delegate;
    /* access modifiers changed from: private */
    public EditTextBoldCursor editText;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public int fieldY;
    /* access modifiers changed from: private */
    public int filterFlags;
    /* access modifiers changed from: private */
    public ImageView floatingButton;
    /* access modifiers changed from: private */
    public boolean ignoreScrollEvent;
    private ArrayList<Long> initialIds;
    /* access modifiers changed from: private */
    public boolean isInclude;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ScrollView scrollView;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public LongSparseArray<GroupCreateSpan> selectedContacts = new LongSparseArray<>();
    private int selectedCount;
    /* access modifiers changed from: private */
    public SpansContainer spansContainer;

    public interface FilterUsersActivityDelegate {
        void didSelectChats(ArrayList<Long> arrayList, int i);
    }

    static /* synthetic */ int access$1972(FilterUsersActivity filterUsersActivity, int i) {
        int i2 = i & filterUsersActivity.filterFlags;
        filterUsersActivity.filterFlags = i2;
        return i2;
    }

    static /* synthetic */ int access$508(FilterUsersActivity filterUsersActivity) {
        int i = filterUsersActivity.selectedCount;
        filterUsersActivity.selectedCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$510(FilterUsersActivity filterUsersActivity) {
        int i = filterUsersActivity.selectedCount;
        filterUsersActivity.selectedCount = i - 1;
        return i;
    }

    private static class ItemDecoration extends RecyclerView.ItemDecoration {
        private boolean single;
        private int skipRows;

        private ItemDecoration() {
        }

        public void onDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
            int width = recyclerView.getWidth();
            int childCount = recyclerView.getChildCount() - (this.single ^ true ? 1 : 0);
            int i = 0;
            while (i < childCount) {
                View childAt = recyclerView.getChildAt(i);
                View childAt2 = i < childCount + -1 ? recyclerView.getChildAt(i + 1) : null;
                if (recyclerView.getChildAdapterPosition(childAt) >= this.skipRows && !(childAt instanceof GraySectionCell) && !(childAt2 instanceof GraySectionCell)) {
                    float bottom = (float) childAt.getBottom();
                    canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(72.0f), bottom, (float) (width - (LocaleController.isRTL ? AndroidUtilities.dp(72.0f) : 0)), bottom, Theme.dividerPaint);
                }
                i++;
            }
        }

        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            super.getItemOffsets(rect, view, recyclerView, state);
            rect.top = 1;
        }
    }

    private class SpansContainer extends ViewGroup {
        /* access modifiers changed from: private */
        public View addingSpan;
        /* access modifiers changed from: private */
        public boolean animationStarted;
        private ArrayList<Animator> animators = new ArrayList<>();
        /* access modifiers changed from: private */
        public AnimatorSet currentAnimation;
        /* access modifiers changed from: private */
        public View removingSpan;

        public SpansContainer(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int i3;
            int childCount = getChildCount();
            int size = View.MeasureSpec.getSize(i);
            int dp = size - AndroidUtilities.dp(26.0f);
            int dp2 = AndroidUtilities.dp(10.0f);
            int dp3 = AndroidUtilities.dp(10.0f);
            int i4 = 0;
            int i5 = 0;
            for (int i6 = 0; i6 < childCount; i6++) {
                View childAt = getChildAt(i6);
                if (childAt instanceof GroupCreateSpan) {
                    childAt.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
                    if (childAt != this.removingSpan && childAt.getMeasuredWidth() + i4 > dp) {
                        dp2 += childAt.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        i4 = 0;
                    }
                    if (childAt.getMeasuredWidth() + i5 > dp) {
                        dp3 += childAt.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        i5 = 0;
                    }
                    int dp4 = AndroidUtilities.dp(13.0f) + i4;
                    if (!this.animationStarted) {
                        View view = this.removingSpan;
                        if (childAt == view) {
                            childAt.setTranslationX((float) (AndroidUtilities.dp(13.0f) + i5));
                            childAt.setTranslationY((float) dp3);
                        } else if (view != null) {
                            float f = (float) dp4;
                            if (childAt.getTranslationX() != f) {
                                this.animators.add(ObjectAnimator.ofFloat(childAt, View.TRANSLATION_X, new float[]{f}));
                            }
                            float f2 = (float) dp2;
                            if (childAt.getTranslationY() != f2) {
                                this.animators.add(ObjectAnimator.ofFloat(childAt, View.TRANSLATION_Y, new float[]{f2}));
                            }
                        } else {
                            childAt.setTranslationX((float) dp4);
                            childAt.setTranslationY((float) dp2);
                        }
                    }
                    if (childAt != this.removingSpan) {
                        i4 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                    }
                    i5 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                }
            }
            if (AndroidUtilities.isTablet()) {
                i3 = AndroidUtilities.dp(372.0f) / 3;
            } else {
                Point point = AndroidUtilities.displaySize;
                i3 = (Math.min(point.x, point.y) - AndroidUtilities.dp(158.0f)) / 3;
            }
            if (dp - i4 < i3) {
                dp2 += AndroidUtilities.dp(40.0f);
                i4 = 0;
            }
            if (dp - i5 < i3) {
                dp3 += AndroidUtilities.dp(40.0f);
            }
            FilterUsersActivity.this.editText.measure(View.MeasureSpec.makeMeasureSpec(dp - i4, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
            if (!this.animationStarted) {
                int dp5 = dp3 + AndroidUtilities.dp(42.0f);
                int dp6 = i4 + AndroidUtilities.dp(16.0f);
                int unused = FilterUsersActivity.this.fieldY = dp2;
                if (this.currentAnimation != null) {
                    int dp7 = dp2 + AndroidUtilities.dp(42.0f);
                    if (FilterUsersActivity.this.containerHeight != dp7) {
                        this.animators.add(ObjectAnimator.ofInt(FilterUsersActivity.this, "containerHeight", new int[]{dp7}));
                    }
                    float f3 = (float) dp6;
                    if (FilterUsersActivity.this.editText.getTranslationX() != f3) {
                        this.animators.add(ObjectAnimator.ofFloat(FilterUsersActivity.this.editText, View.TRANSLATION_X, new float[]{f3}));
                    }
                    if (FilterUsersActivity.this.editText.getTranslationY() != ((float) FilterUsersActivity.this.fieldY)) {
                        this.animators.add(ObjectAnimator.ofFloat(FilterUsersActivity.this.editText, View.TRANSLATION_Y, new float[]{(float) FilterUsersActivity.this.fieldY}));
                    }
                    FilterUsersActivity.this.editText.setAllowDrawCursor(false);
                    this.currentAnimation.playTogether(this.animators);
                    this.currentAnimation.start();
                    this.animationStarted = true;
                } else {
                    int unused2 = FilterUsersActivity.this.containerHeight = dp5;
                    FilterUsersActivity.this.editText.setTranslationX((float) dp6);
                    FilterUsersActivity.this.editText.setTranslationY((float) FilterUsersActivity.this.fieldY);
                }
            } else if (this.currentAnimation != null && !FilterUsersActivity.this.ignoreScrollEvent && this.removingSpan == null) {
                FilterUsersActivity.this.editText.bringPointIntoView(FilterUsersActivity.this.editText.getSelectionStart());
            }
            setMeasuredDimension(size, FilterUsersActivity.this.containerHeight);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int childCount = getChildCount();
            for (int i5 = 0; i5 < childCount; i5++) {
                View childAt = getChildAt(i5);
                childAt.layout(0, 0, childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan groupCreateSpan, boolean z) {
            FilterUsersActivity.this.allSpans.add(groupCreateSpan);
            long uid = groupCreateSpan.getUid();
            if (uid > -NUM) {
                FilterUsersActivity.access$508(FilterUsersActivity.this);
            }
            FilterUsersActivity.this.selectedContacts.put(uid, groupCreateSpan);
            FilterUsersActivity.this.editText.setHintVisible(false);
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            if (z) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.currentAnimation = animatorSet2;
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        View unused = SpansContainer.this.addingSpan = null;
                        AnimatorSet unused2 = SpansContainer.this.currentAnimation = null;
                        boolean unused3 = SpansContainer.this.animationStarted = false;
                        FilterUsersActivity.this.editText.setAllowDrawCursor(true);
                    }
                });
                this.currentAnimation.setDuration(150);
                this.addingSpan = groupCreateSpan;
                this.animators.clear();
                this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.SCALE_X, new float[]{0.01f, 1.0f}));
                this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.SCALE_Y, new float[]{0.01f, 1.0f}));
                this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.ALPHA, new float[]{0.0f, 1.0f}));
            }
            addView(groupCreateSpan);
        }

        public void removeSpan(final GroupCreateSpan groupCreateSpan) {
            boolean unused = FilterUsersActivity.this.ignoreScrollEvent = true;
            long uid = groupCreateSpan.getUid();
            if (uid > -NUM) {
                FilterUsersActivity.access$510(FilterUsersActivity.this);
            }
            FilterUsersActivity.this.selectedContacts.remove(uid);
            FilterUsersActivity.this.allSpans.remove(groupCreateSpan);
            groupCreateSpan.setOnClickListener((View.OnClickListener) null);
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.currentAnimation = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(groupCreateSpan);
                    View unused = SpansContainer.this.removingSpan = null;
                    AnimatorSet unused2 = SpansContainer.this.currentAnimation = null;
                    boolean unused3 = SpansContainer.this.animationStarted = false;
                    FilterUsersActivity.this.editText.setAllowDrawCursor(true);
                    if (FilterUsersActivity.this.allSpans.isEmpty()) {
                        FilterUsersActivity.this.editText.setHintVisible(true);
                    }
                }
            });
            this.currentAnimation.setDuration(150);
            this.removingSpan = groupCreateSpan;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.SCALE_X, new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.SCALE_Y, new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.ALPHA, new float[]{1.0f, 0.0f}));
            requestLayout();
        }
    }

    public FilterUsersActivity(boolean z, ArrayList<Long> arrayList, int i) {
        this.isInclude = z;
        this.filterFlags = i;
        this.initialIds = arrayList;
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
    }

    public void onClick(View view) {
        GroupCreateSpan groupCreateSpan = (GroupCreateSpan) view;
        if (groupCreateSpan.isDeleting()) {
            this.currentDeletingSpan = null;
            this.spansContainer.removeSpan(groupCreateSpan);
            if (groupCreateSpan.getUid() == -2147483648L) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_CONTACTS ^ -1;
            } else if (groupCreateSpan.getUid() == -2147483647L) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS ^ -1;
            } else if (groupCreateSpan.getUid() == -NUM) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_GROUPS ^ -1;
            } else if (groupCreateSpan.getUid() == -NUM) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_CHANNELS ^ -1;
            } else if (groupCreateSpan.getUid() == -NUM) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_BOTS ^ -1;
            } else if (groupCreateSpan.getUid() == -NUM) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED ^ -1;
            } else if (groupCreateSpan.getUid() == -NUM) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ ^ -1;
            } else if (groupCreateSpan.getUid() == -NUM) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED ^ -1;
            }
            updateHint();
            checkVisibleRows();
            return;
        }
        GroupCreateSpan groupCreateSpan2 = this.currentDeletingSpan;
        if (groupCreateSpan2 != null) {
            groupCreateSpan2.cancelDeleteAnimation();
        }
        this.currentDeletingSpan = groupCreateSpan;
        groupCreateSpan.startDeleteAnimation();
    }

    public View createView(Context context) {
        Object obj;
        String str;
        int i;
        this.searching = false;
        this.searchWas = false;
        this.allSpans.clear();
        this.selectedContacts.clear();
        this.currentDeletingSpan = null;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.isInclude) {
            this.actionBar.setTitle(LocaleController.getString("FilterAlwaysShow", R.string.FilterAlwaysShow));
        } else {
            this.actionBar.setTitle(LocaleController.getString("FilterNeverShow", R.string.FilterNeverShow));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    FilterUsersActivity.this.finishFragment();
                } else if (i == 1) {
                    boolean unused = FilterUsersActivity.this.onDonePressed(true);
                }
            }
        });
        AnonymousClass2 r2 = new ViewGroup(context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int i3;
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                float f = 56.0f;
                if (AndroidUtilities.isTablet() || size2 > size) {
                    i3 = AndroidUtilities.dp(144.0f);
                } else {
                    i3 = AndroidUtilities.dp(56.0f);
                }
                FilterUsersActivity.this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE));
                FilterUsersActivity.this.listView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2 - FilterUsersActivity.this.scrollView.getMeasuredHeight(), NUM));
                FilterUsersActivity.this.emptyView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2 - FilterUsersActivity.this.scrollView.getMeasuredHeight(), NUM));
                if (FilterUsersActivity.this.floatingButton != null) {
                    if (Build.VERSION.SDK_INT < 21) {
                        f = 60.0f;
                    }
                    int dp = AndroidUtilities.dp(f);
                    FilterUsersActivity.this.floatingButton.measure(View.MeasureSpec.makeMeasureSpec(dp, NUM), View.MeasureSpec.makeMeasureSpec(dp, NUM));
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                FilterUsersActivity.this.scrollView.layout(0, 0, FilterUsersActivity.this.scrollView.getMeasuredWidth(), FilterUsersActivity.this.scrollView.getMeasuredHeight());
                FilterUsersActivity.this.listView.layout(0, FilterUsersActivity.this.scrollView.getMeasuredHeight(), FilterUsersActivity.this.listView.getMeasuredWidth(), FilterUsersActivity.this.scrollView.getMeasuredHeight() + FilterUsersActivity.this.listView.getMeasuredHeight());
                FilterUsersActivity.this.emptyView.layout(0, FilterUsersActivity.this.scrollView.getMeasuredHeight(), FilterUsersActivity.this.emptyView.getMeasuredWidth(), FilterUsersActivity.this.scrollView.getMeasuredHeight() + FilterUsersActivity.this.emptyView.getMeasuredHeight());
                if (FilterUsersActivity.this.floatingButton != null) {
                    int dp = LocaleController.isRTL ? AndroidUtilities.dp(14.0f) : ((i3 - i) - AndroidUtilities.dp(14.0f)) - FilterUsersActivity.this.floatingButton.getMeasuredWidth();
                    int dp2 = ((i4 - i2) - AndroidUtilities.dp(14.0f)) - FilterUsersActivity.this.floatingButton.getMeasuredHeight();
                    FilterUsersActivity.this.floatingButton.layout(dp, dp2, FilterUsersActivity.this.floatingButton.getMeasuredWidth() + dp, FilterUsersActivity.this.floatingButton.getMeasuredHeight() + dp2);
                }
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view == FilterUsersActivity.this.listView || view == FilterUsersActivity.this.emptyView) {
                    FilterUsersActivity.this.parentLayout.drawHeaderShadow(canvas, FilterUsersActivity.this.scrollView.getMeasuredHeight());
                }
                return drawChild;
            }
        };
        this.fragmentView = r2;
        ViewGroup viewGroup = r2;
        AnonymousClass3 r4 = new ScrollView(context) {
            public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                if (FilterUsersActivity.this.ignoreScrollEvent) {
                    boolean unused = FilterUsersActivity.this.ignoreScrollEvent = false;
                    return false;
                }
                rect.offset(view.getLeft() - view.getScrollX(), view.getTop() - view.getScrollY());
                rect.top += FilterUsersActivity.this.fieldY + AndroidUtilities.dp(20.0f);
                rect.bottom += FilterUsersActivity.this.fieldY + AndroidUtilities.dp(50.0f);
                return super.requestChildRectangleOnScreen(view, rect, z);
            }
        };
        this.scrollView = r4;
        r4.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("windowBackgroundWhite"));
        viewGroup.addView(this.scrollView);
        SpansContainer spansContainer2 = new SpansContainer(context);
        this.spansContainer = spansContainer2;
        this.scrollView.addView(spansContainer2, LayoutHelper.createFrame(-1, -2.0f));
        this.spansContainer.setOnClickListener(new FilterUsersActivity$$ExternalSyntheticLambda1(this));
        AnonymousClass4 r42 = new EditTextBoldCursor(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (FilterUsersActivity.this.currentDeletingSpan != null) {
                    FilterUsersActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    GroupCreateSpan unused = FilterUsersActivity.this.currentDeletingSpan = null;
                }
                if (motionEvent.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.editText = r42;
        r42.setTextSize(1, 16.0f);
        this.editText.setHintColor(Theme.getColor("groupcreate_hintText"));
        this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.editText.setCursorColor(Theme.getColor("groupcreate_cursor"));
        this.editText.setCursorWidth(1.5f);
        this.editText.setInputType(655536);
        this.editText.setSingleLine(true);
        this.editText.setBackgroundDrawable((Drawable) null);
        this.editText.setVerticalScrollBarEnabled(false);
        this.editText.setHorizontalScrollBarEnabled(false);
        this.editText.setTextIsSelectable(false);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setImeOptions(NUM);
        int i2 = 5;
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.spansContainer.addView(this.editText);
        this.editText.setHintText(LocaleController.getString("SearchForPeopleAndGroups", R.string.SearchForPeopleAndGroups));
        this.editText.setCustomSelectionActionModeCallback(new ActionMode.Callback(this) {
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }
        });
        this.editText.setOnKeyListener(new View.OnKeyListener() {
            private boolean wasEmpty;

            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == 67) {
                    boolean z = true;
                    if (keyEvent.getAction() == 0) {
                        if (FilterUsersActivity.this.editText.length() != 0) {
                            z = false;
                        }
                        this.wasEmpty = z;
                    } else if (keyEvent.getAction() == 1 && this.wasEmpty && !FilterUsersActivity.this.allSpans.isEmpty()) {
                        GroupCreateSpan groupCreateSpan = (GroupCreateSpan) FilterUsersActivity.this.allSpans.get(FilterUsersActivity.this.allSpans.size() - 1);
                        FilterUsersActivity.this.spansContainer.removeSpan(groupCreateSpan);
                        if (groupCreateSpan.getUid() == -2147483648L) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_CONTACTS ^ -1);
                        } else if (groupCreateSpan.getUid() == -2147483647L) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS ^ -1);
                        } else if (groupCreateSpan.getUid() == -NUM) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_GROUPS ^ -1);
                        } else if (groupCreateSpan.getUid() == -NUM) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_CHANNELS ^ -1);
                        } else if (groupCreateSpan.getUid() == -NUM) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_BOTS ^ -1);
                        } else if (groupCreateSpan.getUid() == -NUM) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED ^ -1);
                        } else if (groupCreateSpan.getUid() == -NUM) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ ^ -1);
                        } else if (groupCreateSpan.getUid() == -NUM) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED ^ -1);
                        }
                        FilterUsersActivity.this.updateHint();
                        FilterUsersActivity.this.checkVisibleRows();
                        return true;
                    }
                }
                return false;
            }
        });
        this.editText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (FilterUsersActivity.this.editText.length() != 0) {
                    if (!FilterUsersActivity.this.adapter.searching) {
                        boolean unused = FilterUsersActivity.this.searching = true;
                        boolean unused2 = FilterUsersActivity.this.searchWas = true;
                        FilterUsersActivity.this.adapter.setSearching(true);
                        FilterUsersActivity.this.listView.setFastScrollVisible(false);
                        FilterUsersActivity.this.listView.setVerticalScrollBarEnabled(true);
                        FilterUsersActivity.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                        FilterUsersActivity.this.emptyView.showProgress();
                    }
                    FilterUsersActivity.this.adapter.searchDialogs(FilterUsersActivity.this.editText.getText().toString());
                    return;
                }
                FilterUsersActivity.this.closeSearch();
            }
        });
        this.emptyView = new EmptyTextProgressView(context);
        if (ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        viewGroup.addView(this.emptyView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setFastScrollEnabled(0);
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        GroupCreateAdapter groupCreateAdapter = new GroupCreateAdapter(context);
        this.adapter = groupCreateAdapter;
        recyclerListView2.setAdapter(groupCreateAdapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        this.listView.addItemDecoration(new ItemDecoration());
        viewGroup.addView(this.listView);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new FilterUsersActivity$$ExternalSyntheticLambda3(this, context));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(FilterUsersActivity.this.editText);
                }
            }
        });
        ImageView imageView = new ImageView(context);
        this.floatingButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        int i3 = Build.VERSION.SDK_INT;
        if (i3 < 21) {
            Drawable mutate = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
        this.floatingButton.setImageResource(R.drawable.floating_check);
        if (i3 >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            ImageView imageView2 = this.floatingButton;
            Property property = View.TRANSLATION_Z;
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(imageView2, property, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, property, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButton.setStateListAnimator(stateListAnimator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider(this) {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        viewGroup.addView(this.floatingButton);
        this.floatingButton.setOnClickListener(new FilterUsersActivity$$ExternalSyntheticLambda0(this));
        this.floatingButton.setContentDescription(LocaleController.getString("Next", R.string.Next));
        if (!this.isInclude) {
            i2 = 3;
        }
        for (int i4 = 1; i4 <= i2; i4++) {
            if (this.isInclude) {
                if (i4 == 1) {
                    i = MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
                    str = "contacts";
                } else if (i4 == 2) {
                    i = MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
                    str = "non_contacts";
                } else if (i4 == 3) {
                    i = MessagesController.DIALOG_FILTER_FLAG_GROUPS;
                    str = "groups";
                } else if (i4 == 4) {
                    i = MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
                    str = "channels";
                } else {
                    i = MessagesController.DIALOG_FILTER_FLAG_BOTS;
                    str = "bots";
                }
            } else if (i4 == 1) {
                i = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;
                str = "muted";
            } else if (i4 == 2) {
                i = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ;
                str = "read";
            } else {
                i = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;
                str = "archived";
            }
            if ((i & this.filterFlags) != 0) {
                GroupCreateSpan groupCreateSpan = new GroupCreateSpan(this.editText.getContext(), (Object) str);
                this.spansContainer.addSpan(groupCreateSpan, false);
                groupCreateSpan.setOnClickListener(this);
            }
        }
        ArrayList<Long> arrayList = this.initialIds;
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = this.initialIds.size();
            for (int i5 = 0; i5 < size; i5++) {
                Long l = this.initialIds.get(i5);
                if (l.longValue() > 0) {
                    obj = getMessagesController().getUser(l);
                } else {
                    obj = getMessagesController().getChat(Long.valueOf(-l.longValue()));
                }
                if (obj != null) {
                    GroupCreateSpan groupCreateSpan2 = new GroupCreateSpan(this.editText.getContext(), obj);
                    this.spansContainer.addSpan(groupCreateSpan2, false);
                    groupCreateSpan2.setOnClickListener(this);
                }
            }
        }
        updateHint();
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view) {
        this.editText.clearFocus();
        this.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.editText);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(Context context, View view, int i) {
        long j;
        int i2;
        if (view instanceof GroupCreateUserCell) {
            GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) view;
            Object object = groupCreateUserCell.getObject();
            boolean z = object instanceof String;
            if (z) {
                if (this.isInclude) {
                    if (i == 1) {
                        i2 = MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
                        j = -2147483648L;
                    } else if (i == 2) {
                        i2 = MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
                        j = -2147483647L;
                    } else if (i == 3) {
                        i2 = MessagesController.DIALOG_FILTER_FLAG_GROUPS;
                        j = -NUM;
                    } else if (i == 4) {
                        i2 = MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
                        j = -NUM;
                    } else {
                        i2 = MessagesController.DIALOG_FILTER_FLAG_BOTS;
                        j = -NUM;
                    }
                } else if (i == 1) {
                    i2 = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;
                    j = -NUM;
                } else if (i == 2) {
                    i2 = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ;
                    j = -NUM;
                } else {
                    i2 = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;
                    j = -NUM;
                }
                if (groupCreateUserCell.isChecked()) {
                    this.filterFlags = (i2 ^ -1) & this.filterFlags;
                } else {
                    this.filterFlags = i2 | this.filterFlags;
                }
            } else if (object instanceof TLRPC$User) {
                j = ((TLRPC$User) object).id;
            } else if (object instanceof TLRPC$Chat) {
                j = -((TLRPC$Chat) object).id;
            } else {
                return;
            }
            boolean z2 = this.selectedContacts.indexOfKey(j) >= 0;
            if (z2) {
                this.spansContainer.removeSpan(this.selectedContacts.get(j));
            } else if ((z || getUserConfig().isPremium() || this.selectedCount < MessagesController.getInstance(this.currentAccount).dialogFiltersChatsLimitDefault) && this.selectedCount < MessagesController.getInstance(this.currentAccount).dialogFiltersChatsLimitPremium) {
                if (object instanceof TLRPC$User) {
                    MessagesController.getInstance(this.currentAccount).putUser((TLRPC$User) object, !this.searching);
                } else if (object instanceof TLRPC$Chat) {
                    MessagesController.getInstance(this.currentAccount).putChat((TLRPC$Chat) object, !this.searching);
                }
                GroupCreateSpan groupCreateSpan = new GroupCreateSpan(this.editText.getContext(), object);
                this.spansContainer.addSpan(groupCreateSpan, true);
                groupCreateSpan.setOnClickListener(this);
            } else {
                LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(this, context, 4, this.currentAccount);
                limitReachedBottomSheet.setCurrentValue(this.selectedCount);
                showDialog(limitReachedBottomSheet);
                return;
            }
            updateHint();
            if (this.searching || this.searchWas) {
                AndroidUtilities.showKeyboard(this.editText);
            } else {
                groupCreateUserCell.setChecked(!z2, true);
            }
            if (this.editText.length() > 0) {
                this.editText.setText((CharSequence) null);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(View view) {
        onDonePressed(true);
    }

    public void onResume() {
        super.onResume();
        EditTextBoldCursor editTextBoldCursor = this.editText;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.contactsDidLoad) {
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null) {
                emptyTextProgressView.showTextView();
            }
            GroupCreateAdapter groupCreateAdapter = this.adapter;
            if (groupCreateAdapter != null) {
                groupCreateAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.updateInterfaces) {
            if (this.listView != null) {
                int intValue = objArr[0].intValue();
                int childCount = this.listView.getChildCount();
                if ((MessagesController.UPDATE_MASK_AVATAR & intValue) != 0 || (MessagesController.UPDATE_MASK_NAME & intValue) != 0 || (MessagesController.UPDATE_MASK_STATUS & intValue) != 0) {
                    for (int i3 = 0; i3 < childCount; i3++) {
                        View childAt = this.listView.getChildAt(i3);
                        if (childAt instanceof GroupCreateUserCell) {
                            ((GroupCreateUserCell) childAt).update(intValue);
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.chatDidCreated) {
            removeSelfFromStack();
        }
    }

    @Keep
    public void setContainerHeight(int i) {
        this.containerHeight = i;
        SpansContainer spansContainer2 = this.spansContainer;
        if (spansContainer2 != null) {
            spansContainer2.requestLayout();
        }
    }

    @Keep
    public int getContainerHeight() {
        return this.containerHeight;
    }

    /* access modifiers changed from: private */
    public void checkVisibleRows() {
        long j;
        int childCount = this.listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof GroupCreateUserCell) {
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) childAt;
                Object object = groupCreateUserCell.getObject();
                if (object instanceof String) {
                    String str = (String) object;
                    char c = 65535;
                    switch (str.hashCode()) {
                        case -1716307998:
                            if (str.equals("archived")) {
                                c = 7;
                                break;
                            }
                            break;
                        case -1237460524:
                            if (str.equals("groups")) {
                                c = 2;
                                break;
                            }
                            break;
                        case -1197490811:
                            if (str.equals("non_contacts")) {
                                c = 1;
                                break;
                            }
                            break;
                        case -567451565:
                            if (str.equals("contacts")) {
                                c = 0;
                                break;
                            }
                            break;
                        case 3029900:
                            if (str.equals("bots")) {
                                c = 4;
                                break;
                            }
                            break;
                        case 3496342:
                            if (str.equals("read")) {
                                c = 6;
                                break;
                            }
                            break;
                        case 104264043:
                            if (str.equals("muted")) {
                                c = 5;
                                break;
                            }
                            break;
                        case 1432626128:
                            if (str.equals("channels")) {
                                c = 3;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            j = -2147483648L;
                            break;
                        case 1:
                            j = -2147483647L;
                            break;
                        case 2:
                            j = -NUM;
                            break;
                        case 3:
                            j = -NUM;
                            break;
                        case 4:
                            j = -NUM;
                            break;
                        case 5:
                            j = -NUM;
                            break;
                        case 6:
                            j = -NUM;
                            break;
                        default:
                            j = -NUM;
                            break;
                    }
                } else {
                    j = object instanceof TLRPC$User ? ((TLRPC$User) object).id : object instanceof TLRPC$Chat ? -((TLRPC$Chat) object).id : 0;
                }
                if (j != 0) {
                    groupCreateUserCell.setChecked(this.selectedContacts.indexOfKey(j) >= 0, true);
                    groupCreateUserCell.setCheckBoxEnabled(true);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean onDonePressed(boolean z) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.selectedContacts.size(); i++) {
            if (this.selectedContacts.keyAt(i) > -NUM) {
                arrayList.add(Long.valueOf(this.selectedContacts.keyAt(i)));
            }
        }
        FilterUsersActivityDelegate filterUsersActivityDelegate = this.delegate;
        if (filterUsersActivityDelegate != null) {
            filterUsersActivityDelegate.didSelectChats(arrayList, this.filterFlags);
        }
        finishFragment();
        return true;
    }

    /* access modifiers changed from: private */
    public void closeSearch() {
        this.searching = false;
        this.searchWas = false;
        this.adapter.setSearching(false);
        this.adapter.searchDialogs((String) null);
        this.listView.setFastScrollVisible(true);
        this.listView.setVerticalScrollBarEnabled(false);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
    }

    /* access modifiers changed from: private */
    public void updateHint() {
        int i = getUserConfig().isPremium() ? getMessagesController().dialogFiltersChatsLimitPremium : getMessagesController().dialogFiltersChatsLimitDefault;
        int i2 = this.selectedCount;
        if (i2 == 0) {
            this.actionBar.setSubtitle(LocaleController.formatString("MembersCountZero", R.string.MembersCountZero, LocaleController.formatPluralString("Chats", i, new Object[0])));
        } else {
            this.actionBar.setSubtitle(String.format(LocaleController.getPluralString("MembersCountSelected", i2), new Object[]{Integer.valueOf(this.selectedCount), Integer.valueOf(i)}));
        }
    }

    public void setDelegate(FilterUsersActivityDelegate filterUsersActivityDelegate) {
        this.delegate = filterUsersActivityDelegate;
    }

    public class GroupCreateAdapter extends RecyclerListView.FastScrollAdapter {
        private ArrayList<TLObject> contacts = new ArrayList<>();
        private Context context;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<Object> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;
        /* access modifiers changed from: private */
        public boolean searching;
        private final int usersStartRow;

        public String getLetter(int i) {
            return null;
        }

        public GroupCreateAdapter(Context context2) {
            this.usersStartRow = FilterUsersActivity.this.isInclude ? 7 : 5;
            this.context = context2;
            ArrayList<TLRPC$Dialog> allDialogs = FilterUsersActivity.this.getMessagesController().getAllDialogs();
            int size = allDialogs.size();
            boolean z = false;
            for (int i = 0; i < size; i++) {
                TLRPC$Dialog tLRPC$Dialog = allDialogs.get(i);
                if (!DialogObject.isEncryptedDialog(tLRPC$Dialog.id)) {
                    if (DialogObject.isUserDialog(tLRPC$Dialog.id)) {
                        TLRPC$User user = FilterUsersActivity.this.getMessagesController().getUser(Long.valueOf(tLRPC$Dialog.id));
                        if (user != null) {
                            this.contacts.add(user);
                            if (UserObject.isUserSelf(user)) {
                                z = true;
                            }
                        }
                    } else {
                        TLRPC$Chat chat = FilterUsersActivity.this.getMessagesController().getChat(Long.valueOf(-tLRPC$Dialog.id));
                        if (chat != null) {
                            this.contacts.add(chat);
                        }
                    }
                }
            }
            if (!z) {
                this.contacts.add(0, FilterUsersActivity.this.getMessagesController().getUser(Long.valueOf(FilterUsersActivity.this.getUserConfig().clientUserId)));
            }
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(false);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setAllowGlobalResults(false);
            this.searchAdapterHelper.setDelegate(new FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda4(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(int i) {
            if (this.searchRunnable == null && !this.searchAdapterHelper.isSearchInProgress()) {
                FilterUsersActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        public void setSearching(boolean z) {
            if (this.searching != z) {
                this.searching = z;
                notifyDataSetChanged();
            }
        }

        public int getItemCount() {
            int i;
            int size;
            if (this.searching) {
                i = this.searchResult.size();
                size = this.searchAdapterHelper.getLocalServerSearch().size() + this.searchAdapterHelper.getGlobalSearch().size();
            } else {
                i = FilterUsersActivity.this.isInclude ? 7 : 5;
                size = this.contacts.size();
            }
            return i + size;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 1) {
                view = new GraySectionCell(this.context);
            } else {
                view = new GroupCreateUserCell(this.context, 1, 0, true);
            }
            return new RecyclerListView.Holder(view);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v0, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v37, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v5, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v46, resolved type: java.lang.CharSequence} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v6, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v14, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v15, resolved type: java.lang.String} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:102:0x0219  */
        /* JADX WARNING: Removed duplicated region for block: B:113:? A[RETURN, SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x01c3  */
        /* JADX WARNING: Removed duplicated region for block: B:85:0x01c9  */
        /* JADX WARNING: Removed duplicated region for block: B:91:0x01d9  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r18, int r19) {
            /*
                r17 = this;
                r0 = r17
                r1 = r18
                r2 = r19
                int r3 = r18.getItemViewType()
                r4 = 2
                r5 = 1
                if (r3 == r5) goto L_0x0032
                if (r3 == r4) goto L_0x0012
                goto L_0x022e
            L_0x0012:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.GraySectionCell r1 = (org.telegram.ui.Cells.GraySectionCell) r1
                if (r2 != 0) goto L_0x0025
                int r2 = org.telegram.messenger.R.string.FilterChatTypes
                java.lang.String r3 = "FilterChatTypes"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x022e
            L_0x0025:
                int r2 = org.telegram.messenger.R.string.FilterChats
                java.lang.String r3 = "FilterChats"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x022e
            L_0x0032:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.GroupCreateUserCell r1 = (org.telegram.ui.Cells.GroupCreateUserCell) r1
                boolean r3 = r0.searching
                r6 = 0
                r7 = 0
                if (r3 == 0) goto L_0x011e
                java.util.ArrayList<java.lang.Object> r3 = r0.searchResult
                int r3 = r3.size()
                org.telegram.ui.Adapters.SearchAdapterHelper r4 = r0.searchAdapterHelper
                java.util.ArrayList r4 = r4.getGlobalSearch()
                int r4 = r4.size()
                org.telegram.ui.Adapters.SearchAdapterHelper r8 = r0.searchAdapterHelper
                java.util.ArrayList r8 = r8.getLocalServerSearch()
                int r8 = r8.size()
                if (r2 < 0) goto L_0x0061
                if (r2 >= r3) goto L_0x0061
                java.util.ArrayList<java.lang.Object> r4 = r0.searchResult
                java.lang.Object r4 = r4.get(r2)
                goto L_0x008b
            L_0x0061:
                if (r2 < r3) goto L_0x0074
                int r9 = r8 + r3
                if (r2 >= r9) goto L_0x0074
                org.telegram.ui.Adapters.SearchAdapterHelper r4 = r0.searchAdapterHelper
                java.util.ArrayList r4 = r4.getLocalServerSearch()
                int r8 = r2 - r3
                java.lang.Object r4 = r4.get(r8)
                goto L_0x008b
            L_0x0074:
                int r9 = r3 + r8
                if (r2 <= r9) goto L_0x008a
                int r4 = r4 + r3
                int r4 = r4 + r8
                if (r2 >= r4) goto L_0x008a
                org.telegram.ui.Adapters.SearchAdapterHelper r4 = r0.searchAdapterHelper
                java.util.ArrayList r4 = r4.getGlobalSearch()
                int r9 = r2 - r3
                int r9 = r9 - r8
                java.lang.Object r4 = r4.get(r9)
                goto L_0x008b
            L_0x008a:
                r4 = r7
            L_0x008b:
                if (r4 == 0) goto L_0x01bc
                boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$User
                if (r8 == 0) goto L_0x0097
                r8 = r4
                org.telegram.tgnet.TLRPC$User r8 = (org.telegram.tgnet.TLRPC$User) r8
                java.lang.String r8 = r8.username
                goto L_0x009c
            L_0x0097:
                r8 = r4
                org.telegram.tgnet.TLRPC$Chat r8 = (org.telegram.tgnet.TLRPC$Chat) r8
                java.lang.String r8 = r8.username
            L_0x009c:
                java.lang.String r9 = "@"
                if (r2 >= r3) goto L_0x00d0
                java.util.ArrayList<java.lang.CharSequence> r3 = r0.searchResultNames
                java.lang.Object r2 = r3.get(r2)
                java.lang.CharSequence r2 = (java.lang.CharSequence) r2
                if (r2 == 0) goto L_0x01bd
                boolean r3 = android.text.TextUtils.isEmpty(r8)
                if (r3 != 0) goto L_0x01bd
                java.lang.String r3 = r2.toString()
                java.lang.StringBuilder r10 = new java.lang.StringBuilder
                r10.<init>()
                r10.append(r9)
                r10.append(r8)
                java.lang.String r8 = r10.toString()
                boolean r3 = r3.startsWith(r8)
                if (r3 == 0) goto L_0x01bd
                r16 = r7
                r7 = r2
                r2 = r16
                goto L_0x01bd
            L_0x00d0:
                if (r2 <= r3) goto L_0x01bc
                boolean r2 = android.text.TextUtils.isEmpty(r8)
                if (r2 != 0) goto L_0x01bc
                org.telegram.ui.Adapters.SearchAdapterHelper r2 = r0.searchAdapterHelper
                java.lang.String r2 = r2.getLastFoundUsername()
                boolean r3 = r2.startsWith(r9)
                if (r3 == 0) goto L_0x00e8
                java.lang.String r2 = r2.substring(r5)
            L_0x00e8:
                android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x011a }
                r3.<init>()     // Catch:{ Exception -> 0x011a }
                r3.append(r9)     // Catch:{ Exception -> 0x011a }
                r3.append(r8)     // Catch:{ Exception -> 0x011a }
                int r9 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r8, r2)     // Catch:{ Exception -> 0x011a }
                r10 = -1
                if (r9 == r10) goto L_0x0116
                int r2 = r2.length()     // Catch:{ Exception -> 0x011a }
                if (r9 != 0) goto L_0x0103
                int r2 = r2 + 1
                goto L_0x0105
            L_0x0103:
                int r9 = r9 + 1
            L_0x0105:
                android.text.style.ForegroundColorSpan r10 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x011a }
                java.lang.String r11 = "windowBackgroundWhiteBlueText4"
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)     // Catch:{ Exception -> 0x011a }
                r10.<init>(r11)     // Catch:{ Exception -> 0x011a }
                int r2 = r2 + r9
                r11 = 33
                r3.setSpan(r10, r9, r2, r11)     // Catch:{ Exception -> 0x011a }
            L_0x0116:
                r2 = r7
                r7 = r3
                goto L_0x01bd
            L_0x011a:
                r2 = r7
                r7 = r8
                goto L_0x01bd
            L_0x011e:
                int r3 = r0.usersStartRow
                if (r2 >= r3) goto L_0x01b5
                org.telegram.ui.FilterUsersActivity r3 = org.telegram.ui.FilterUsersActivity.this
                boolean r3 = r3.isInclude
                if (r3 == 0) goto L_0x0175
                if (r2 != r5) goto L_0x0139
                int r2 = org.telegram.messenger.R.string.FilterContacts
                java.lang.String r3 = "FilterContacts"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_CONTACTS
                java.lang.String r4 = "contacts"
                goto L_0x019f
            L_0x0139:
                if (r2 != r4) goto L_0x0148
                int r2 = org.telegram.messenger.R.string.FilterNonContacts
                java.lang.String r3 = "FilterNonContacts"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS
                java.lang.String r4 = "non_contacts"
                goto L_0x019f
            L_0x0148:
                r3 = 3
                if (r2 != r3) goto L_0x0158
                int r2 = org.telegram.messenger.R.string.FilterGroups
                java.lang.String r3 = "FilterGroups"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_GROUPS
                java.lang.String r4 = "groups"
                goto L_0x019f
            L_0x0158:
                r3 = 4
                if (r2 != r3) goto L_0x0168
                int r2 = org.telegram.messenger.R.string.FilterChannels
                java.lang.String r3 = "FilterChannels"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_CHANNELS
                java.lang.String r4 = "channels"
                goto L_0x019f
            L_0x0168:
                int r2 = org.telegram.messenger.R.string.FilterBots
                java.lang.String r3 = "FilterBots"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_BOTS
                java.lang.String r4 = "bots"
                goto L_0x019f
            L_0x0175:
                if (r2 != r5) goto L_0x0184
                int r2 = org.telegram.messenger.R.string.FilterMuted
                java.lang.String r3 = "FilterMuted"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED
                java.lang.String r4 = "muted"
                goto L_0x019f
            L_0x0184:
                if (r2 != r4) goto L_0x0193
                int r2 = org.telegram.messenger.R.string.FilterRead
                java.lang.String r3 = "FilterRead"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ
                java.lang.String r4 = "read"
                goto L_0x019f
            L_0x0193:
                int r2 = org.telegram.messenger.R.string.FilterArchived
                java.lang.String r3 = "FilterArchived"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED
                java.lang.String r4 = "archived"
            L_0x019f:
                r1.setObject(r4, r2, r7)
                org.telegram.ui.FilterUsersActivity r2 = org.telegram.ui.FilterUsersActivity.this
                int r2 = r2.filterFlags
                r2 = r2 & r3
                if (r2 != r3) goto L_0x01ad
                r2 = 1
                goto L_0x01ae
            L_0x01ad:
                r2 = 0
            L_0x01ae:
                r1.setChecked(r2, r6)
                r1.setCheckBoxEnabled(r5)
                return
            L_0x01b5:
                java.util.ArrayList<org.telegram.tgnet.TLObject> r4 = r0.contacts
                int r2 = r2 - r3
                java.lang.Object r4 = r4.get(r2)
            L_0x01bc:
                r2 = r7
            L_0x01bd:
                boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$User
                r8 = 0
                if (r3 == 0) goto L_0x01c9
                r3 = r4
                org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
                long r10 = r3.id
                goto L_0x01d5
            L_0x01c9:
                boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$Chat
                if (r3 == 0) goto L_0x01d4
                r3 = r4
                org.telegram.tgnet.TLRPC$Chat r3 = (org.telegram.tgnet.TLRPC$Chat) r3
                long r10 = r3.id
                long r10 = -r10
                goto L_0x01d5
            L_0x01d4:
                r10 = r8
            L_0x01d5:
                boolean r3 = r0.searching
                if (r3 != 0) goto L_0x0212
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                org.telegram.ui.FilterUsersActivity r3 = org.telegram.ui.FilterUsersActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r3 = r3.dialogFilters
                int r12 = r3.size()
                r13 = 0
            L_0x01eb:
                if (r13 >= r12) goto L_0x0212
                java.lang.Object r14 = r3.get(r13)
                org.telegram.messenger.MessagesController$DialogFilter r14 = (org.telegram.messenger.MessagesController.DialogFilter) r14
                org.telegram.ui.FilterUsersActivity r15 = org.telegram.ui.FilterUsersActivity.this
                org.telegram.messenger.AccountInstance r15 = r15.getAccountInstance()
                boolean r15 = r14.includesDialog(r15, r10)
                if (r15 == 0) goto L_0x020f
                int r15 = r7.length()
                if (r15 <= 0) goto L_0x020a
                java.lang.String r15 = ", "
                r7.append(r15)
            L_0x020a:
                java.lang.String r14 = r14.name
                r7.append(r14)
            L_0x020f:
                int r13 = r13 + 1
                goto L_0x01eb
            L_0x0212:
                r1.setObject(r4, r2, r7)
                int r2 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
                if (r2 == 0) goto L_0x022e
                org.telegram.ui.FilterUsersActivity r2 = org.telegram.ui.FilterUsersActivity.this
                androidx.collection.LongSparseArray r2 = r2.selectedContacts
                int r2 = r2.indexOfKey(r10)
                if (r2 < 0) goto L_0x0227
                r2 = 1
                goto L_0x0228
            L_0x0227:
                r2 = 0
            L_0x0228:
                r1.setChecked(r2, r6)
                r1.setCheckBoxEnabled(r5)
            L_0x022e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.FilterUsersActivity.GroupCreateAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (this.searching) {
                return 1;
            }
            if (FilterUsersActivity.this.isInclude) {
                if (i == 0 || i == 6) {
                    return 2;
                }
            } else if (i == 0 || i == 4) {
                return 2;
            }
            return 1;
        }

        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            iArr[0] = (int) (((float) getItemCount()) * f);
            iArr[1] = 0;
        }

        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof GroupCreateUserCell) {
                ((GroupCreateUserCell) view).recycle();
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1;
        }

        public void searchDialogs(String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
                this.searchAdapterHelper.queryServerSearch((String) null, true, true, false, false, false, 0, false, 0, 0);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda2 filterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda2 = new FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda2(this, str);
            this.searchRunnable = filterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda2;
            dispatchQueue.postRunnable(filterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda2, 300);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$searchDialogs$3(String str) {
            AndroidUtilities.runOnUIThread(new FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda0(this, str));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$searchDialogs$2(String str) {
            this.searchAdapterHelper.queryServerSearch(str, true, true, true, true, false, 0, false, 0, 0);
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda1 filterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda1 = new FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda1(this, str);
            this.searchRunnable = filterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda1;
            dispatchQueue.postRunnable(filterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda1);
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:43:0x00f7, code lost:
            r15 = 1;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$searchDialogs$1(java.lang.String r19) {
            /*
                r18 = this;
                r0 = r18
                java.lang.String r1 = r19.trim()
                java.lang.String r1 = r1.toLowerCase()
                int r2 = r1.length()
                if (r2 != 0) goto L_0x001e
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                r0.updateSearchResults(r1, r2)
                return
            L_0x001e:
                org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r2 = r2.getTranslitString(r1)
                boolean r3 = r1.equals(r2)
                r4 = 0
                if (r3 != 0) goto L_0x0033
                int r3 = r2.length()
                if (r3 != 0) goto L_0x0034
            L_0x0033:
                r2 = r4
            L_0x0034:
                r3 = 0
                r5 = 1
                if (r2 == 0) goto L_0x003a
                r6 = 1
                goto L_0x003b
            L_0x003a:
                r6 = 0
            L_0x003b:
                int r6 = r6 + r5
                java.lang.String[] r7 = new java.lang.String[r6]
                r7[r3] = r1
                if (r2 == 0) goto L_0x0044
                r7[r5] = r2
            L_0x0044:
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                r8 = 0
            L_0x004f:
                java.util.ArrayList<org.telegram.tgnet.TLObject> r9 = r0.contacts
                int r9 = r9.size()
                if (r8 >= r9) goto L_0x017c
                java.util.ArrayList<org.telegram.tgnet.TLObject> r9 = r0.contacts
                java.lang.Object r9 = r9.get(r8)
                org.telegram.tgnet.TLObject r9 = (org.telegram.tgnet.TLObject) r9
                r10 = 3
                java.lang.String[] r11 = new java.lang.String[r10]
                boolean r12 = r9 instanceof org.telegram.tgnet.TLRPC$User
                r13 = 2
                if (r12 == 0) goto L_0x00a2
                r14 = r9
                org.telegram.tgnet.TLRPC$User r14 = (org.telegram.tgnet.TLRPC$User) r14
                java.lang.String r15 = r14.first_name
                java.lang.String r10 = r14.last_name
                java.lang.String r10 = org.telegram.messenger.ContactsController.formatName(r15, r10)
                java.lang.String r10 = r10.toLowerCase()
                r11[r3] = r10
                java.lang.String r10 = r14.username
                boolean r15 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r14)
                if (r15 == 0) goto L_0x008f
                int r14 = org.telegram.messenger.R.string.RepliesTitle
                java.lang.String r15 = "RepliesTitle"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
                java.lang.String r14 = r14.toLowerCase()
                r11[r13] = r14
                goto L_0x00af
            L_0x008f:
                boolean r14 = r14.self
                if (r14 == 0) goto L_0x00af
                int r14 = org.telegram.messenger.R.string.SavedMessages
                java.lang.String r15 = "SavedMessages"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
                java.lang.String r14 = r14.toLowerCase()
                r11[r13] = r14
                goto L_0x00af
            L_0x00a2:
                r10 = r9
                org.telegram.tgnet.TLRPC$Chat r10 = (org.telegram.tgnet.TLRPC$Chat) r10
                java.lang.String r14 = r10.title
                java.lang.String r14 = r14.toLowerCase()
                r11[r3] = r14
                java.lang.String r10 = r10.username
            L_0x00af:
                org.telegram.messenger.LocaleController r14 = org.telegram.messenger.LocaleController.getInstance()
                r15 = r11[r3]
                java.lang.String r14 = r14.getTranslitString(r15)
                r11[r5] = r14
                r14 = r11[r3]
                r15 = r11[r5]
                boolean r14 = r14.equals(r15)
                if (r14 == 0) goto L_0x00c7
                r11[r5] = r4
            L_0x00c7:
                r14 = 0
                r15 = 0
            L_0x00c9:
                if (r14 >= r6) goto L_0x016f
                r3 = r7[r14]
                r4 = 0
            L_0x00ce:
                r13 = 3
                if (r4 >= r13) goto L_0x0101
                r13 = r11[r4]
                if (r13 == 0) goto L_0x00f9
                boolean r16 = r13.startsWith(r3)
                if (r16 != 0) goto L_0x00f5
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r17 = r6
                java.lang.String r6 = " "
                r5.append(r6)
                r5.append(r3)
                java.lang.String r5 = r5.toString()
                boolean r5 = r13.contains(r5)
                if (r5 == 0) goto L_0x00fb
                goto L_0x00f7
            L_0x00f5:
                r17 = r6
            L_0x00f7:
                r15 = 1
                goto L_0x0103
            L_0x00f9:
                r17 = r6
            L_0x00fb:
                int r4 = r4 + 1
                r6 = r17
                r5 = 1
                goto L_0x00ce
            L_0x0101:
                r17 = r6
            L_0x0103:
                if (r15 != 0) goto L_0x0112
                if (r10 == 0) goto L_0x0112
                java.lang.String r4 = r10.toLowerCase()
                boolean r4 = r4.startsWith(r3)
                if (r4 == 0) goto L_0x0112
                r15 = 2
            L_0x0112:
                if (r15 == 0) goto L_0x0163
                r4 = 1
                if (r15 != r4) goto L_0x0137
                if (r12 == 0) goto L_0x0128
                r5 = r9
                org.telegram.tgnet.TLRPC$User r5 = (org.telegram.tgnet.TLRPC$User) r5
                java.lang.String r6 = r5.first_name
                java.lang.String r5 = r5.last_name
                java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r6, r5, r3)
                r2.add(r3)
                goto L_0x0135
            L_0x0128:
                r5 = r9
                org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC$Chat) r5
                java.lang.String r5 = r5.title
                r6 = 0
                java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r6, r3)
                r2.add(r3)
            L_0x0135:
                r6 = 0
                goto L_0x015f
            L_0x0137:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "@"
                r5.append(r6)
                r5.append(r10)
                java.lang.String r5 = r5.toString()
                java.lang.StringBuilder r10 = new java.lang.StringBuilder
                r10.<init>()
                r10.append(r6)
                r10.append(r3)
                java.lang.String r3 = r10.toString()
                r6 = 0
                java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r6, r3)
                r2.add(r3)
            L_0x015f:
                r1.add(r9)
                goto L_0x0173
            L_0x0163:
                r4 = 1
                r6 = 0
                int r14 = r14 + 1
                r4 = r6
                r6 = r17
                r3 = 0
                r5 = 1
                r13 = 2
                goto L_0x00c9
            L_0x016f:
                r17 = r6
                r6 = r4
                r4 = 1
            L_0x0173:
                int r8 = r8 + 1
                r4 = r6
                r6 = r17
                r3 = 0
                r5 = 1
                goto L_0x004f
            L_0x017c:
                r0.updateSearchResults(r1, r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.FilterUsersActivity.GroupCreateAdapter.lambda$searchDialogs$1(java.lang.String):void");
        }

        private void updateSearchResults(ArrayList<Object> arrayList, ArrayList<CharSequence> arrayList2) {
            AndroidUtilities.runOnUIThread(new FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda3(this, arrayList, arrayList2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSearchResults$4(ArrayList arrayList, ArrayList arrayList2) {
            if (this.searching) {
                this.searchRunnable = null;
                this.searchResult = arrayList;
                this.searchResultNames = arrayList2;
                this.searchAdapterHelper.mergeResults(arrayList);
                if (this.searching && !this.searchAdapterHelper.isSearchInProgress()) {
                    FilterUsersActivity.this.emptyView.showTextView();
                }
                notifyDataSetChanged();
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        FilterUsersActivity$$ExternalSyntheticLambda2 filterUsersActivity$$ExternalSyntheticLambda2 = new FilterUsersActivity$$ExternalSyntheticLambda2(this);
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollActive"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollInactive"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_hintText"));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_cursor"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_sectionText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxDisabled"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        FilterUsersActivity$$ExternalSyntheticLambda2 filterUsersActivity$$ExternalSyntheticLambda22 = filterUsersActivity$$ExternalSyntheticLambda2;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, filterUsersActivity$$ExternalSyntheticLambda22, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, filterUsersActivity$$ExternalSyntheticLambda22, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, filterUsersActivity$$ExternalSyntheticLambda22, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, filterUsersActivity$$ExternalSyntheticLambda22, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, filterUsersActivity$$ExternalSyntheticLambda22, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, filterUsersActivity$$ExternalSyntheticLambda22, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, filterUsersActivity$$ExternalSyntheticLambda22, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_spanBackground"));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_spanText"));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_spanDelete"));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundBlue"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$3() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) childAt).update(0);
                }
            }
        }
    }
}
