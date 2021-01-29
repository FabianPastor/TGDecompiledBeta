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
import android.util.SparseArray;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
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
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FilterUsersActivity;

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
    private ArrayList<Integer> initialIds;
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
    public SparseArray<GroupCreateSpan> selectedContacts = new SparseArray<>();
    private int selectedCount;
    /* access modifiers changed from: private */
    public SpansContainer spansContainer;

    public interface FilterUsersActivityDelegate {
        void didSelectChats(ArrayList<Integer> arrayList, int i);
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
            int uid = groupCreateSpan.getUid();
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
            int uid = groupCreateSpan.getUid();
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

    public FilterUsersActivity(boolean z, ArrayList<Integer> arrayList, int i) {
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
            if (groupCreateSpan.getUid() == Integer.MIN_VALUE) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_CONTACTS ^ -1;
            } else if (groupCreateSpan.getUid() == -NUM) {
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
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.isInclude) {
            this.actionBar.setTitle(LocaleController.getString("FilterAlwaysShow", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("FilterNeverShow", NUM));
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
        this.spansContainer.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FilterUsersActivity.this.lambda$createView$0$FilterUsersActivity(view);
            }
        });
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
        this.editText.setHintText(LocaleController.getString("SearchForPeopleAndGroups", NUM));
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
                        if (groupCreateSpan.getUid() == Integer.MIN_VALUE) {
                            FilterUsersActivity filterUsersActivity = FilterUsersActivity.this;
                            int unused = filterUsersActivity.filterFlags = filterUsersActivity.filterFlags & (MessagesController.DIALOG_FILTER_FLAG_CONTACTS ^ -1);
                        } else if (groupCreateSpan.getUid() == -NUM) {
                            FilterUsersActivity filterUsersActivity2 = FilterUsersActivity.this;
                            int unused2 = filterUsersActivity2.filterFlags = filterUsersActivity2.filterFlags & (MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS ^ -1);
                        } else if (groupCreateSpan.getUid() == -NUM) {
                            FilterUsersActivity filterUsersActivity3 = FilterUsersActivity.this;
                            int unused3 = filterUsersActivity3.filterFlags = filterUsersActivity3.filterFlags & (MessagesController.DIALOG_FILTER_FLAG_GROUPS ^ -1);
                        } else if (groupCreateSpan.getUid() == -NUM) {
                            FilterUsersActivity filterUsersActivity4 = FilterUsersActivity.this;
                            int unused4 = filterUsersActivity4.filterFlags = filterUsersActivity4.filterFlags & (MessagesController.DIALOG_FILTER_FLAG_CHANNELS ^ -1);
                        } else if (groupCreateSpan.getUid() == -NUM) {
                            FilterUsersActivity filterUsersActivity5 = FilterUsersActivity.this;
                            int unused5 = filterUsersActivity5.filterFlags = filterUsersActivity5.filterFlags & (MessagesController.DIALOG_FILTER_FLAG_BOTS ^ -1);
                        } else if (groupCreateSpan.getUid() == -NUM) {
                            FilterUsersActivity filterUsersActivity6 = FilterUsersActivity.this;
                            int unused6 = filterUsersActivity6.filterFlags = filterUsersActivity6.filterFlags & (MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED ^ -1);
                        } else if (groupCreateSpan.getUid() == -NUM) {
                            FilterUsersActivity filterUsersActivity7 = FilterUsersActivity.this;
                            int unused7 = filterUsersActivity7.filterFlags = filterUsersActivity7.filterFlags & (MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ ^ -1);
                        } else if (groupCreateSpan.getUid() == -NUM) {
                            FilterUsersActivity filterUsersActivity8 = FilterUsersActivity.this;
                            int unused8 = filterUsersActivity8.filterFlags = filterUsersActivity8.filterFlags & (MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED ^ -1);
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
                        FilterUsersActivity.this.emptyView.setText(LocaleController.getString("NoResult", NUM));
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
        this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
        viewGroup.addView(this.emptyView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setFastScrollEnabled();
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                FilterUsersActivity.this.lambda$createView$1$FilterUsersActivity(view, i);
            }
        });
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
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
        this.floatingButton.setImageResource(NUM);
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
        this.floatingButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FilterUsersActivity.this.lambda$createView$2$FilterUsersActivity(view);
            }
        });
        this.floatingButton.setContentDescription(LocaleController.getString("Next", NUM));
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
        ArrayList<Integer> arrayList = this.initialIds;
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = this.initialIds.size();
            for (int i5 = 0; i5 < size; i5++) {
                Integer num = this.initialIds.get(i5);
                if (num.intValue() > 0) {
                    obj = getMessagesController().getUser(num);
                } else {
                    obj = getMessagesController().getChat(Integer.valueOf(-num.intValue()));
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
    /* renamed from: lambda$createView$0 */
    public /* synthetic */ void lambda$createView$0$FilterUsersActivity(View view) {
        this.editText.clearFocus();
        this.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.editText);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$1 */
    public /* synthetic */ void lambda$createView$1$FilterUsersActivity(View view, int i) {
        int i2;
        int i3;
        if (view instanceof GroupCreateUserCell) {
            GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) view;
            Object object = groupCreateUserCell.getObject();
            boolean z = object instanceof String;
            if (z) {
                if (this.isInclude) {
                    if (i == 1) {
                        i3 = MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
                        i2 = Integer.MIN_VALUE;
                    } else if (i == 2) {
                        i3 = MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
                        i2 = -NUM;
                    } else if (i == 3) {
                        i3 = MessagesController.DIALOG_FILTER_FLAG_GROUPS;
                        i2 = -NUM;
                    } else if (i == 4) {
                        i3 = MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
                        i2 = -NUM;
                    } else {
                        i3 = MessagesController.DIALOG_FILTER_FLAG_BOTS;
                        i2 = -NUM;
                    }
                } else if (i == 1) {
                    i3 = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;
                    i2 = -NUM;
                } else if (i == 2) {
                    i3 = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ;
                    i2 = -NUM;
                } else {
                    i3 = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;
                    i2 = -NUM;
                }
                if (groupCreateUserCell.isChecked()) {
                    this.filterFlags = (i3 ^ -1) & this.filterFlags;
                } else {
                    this.filterFlags = i3 | this.filterFlags;
                }
            } else if (object instanceof TLRPC$User) {
                i2 = ((TLRPC$User) object).id;
            } else if (object instanceof TLRPC$Chat) {
                i2 = -((TLRPC$Chat) object).id;
            } else {
                return;
            }
            boolean z2 = this.selectedContacts.indexOfKey(i2) >= 0;
            if (z2) {
                this.spansContainer.removeSpan(this.selectedContacts.get(i2));
            } else if (z || this.selectedCount < 100) {
                if (object instanceof TLRPC$User) {
                    MessagesController.getInstance(this.currentAccount).putUser((TLRPC$User) object, !this.searching);
                } else if (object instanceof TLRPC$Chat) {
                    MessagesController.getInstance(this.currentAccount).putChat((TLRPC$Chat) object, !this.searching);
                }
                GroupCreateSpan groupCreateSpan = new GroupCreateSpan(this.editText.getContext(), object);
                this.spansContainer.addSpan(groupCreateSpan, true);
                groupCreateSpan.setOnClickListener(this);
            } else {
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
    /* renamed from: lambda$createView$2 */
    public /* synthetic */ void lambda$createView$2$FilterUsersActivity(View view) {
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
                if ((intValue & 2) != 0 || (intValue & 1) != 0 || (intValue & 4) != 0) {
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
        int i;
        int childCount = this.listView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.listView.getChildAt(i2);
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
                            i = Integer.MIN_VALUE;
                            break;
                        case 1:
                            i = -NUM;
                            break;
                        case 2:
                            i = -NUM;
                            break;
                        case 3:
                            i = -NUM;
                            break;
                        case 4:
                            i = -NUM;
                            break;
                        case 5:
                            i = -NUM;
                            break;
                        case 6:
                            i = -NUM;
                            break;
                        default:
                            i = -NUM;
                            break;
                    }
                } else {
                    i = object instanceof TLRPC$User ? ((TLRPC$User) object).id : object instanceof TLRPC$Chat ? -((TLRPC$Chat) object).id : 0;
                }
                if (i != 0) {
                    groupCreateUserCell.setChecked(this.selectedContacts.indexOfKey(i) >= 0, true);
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
                arrayList.add(Integer.valueOf(this.selectedContacts.keyAt(i)));
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
        this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
    }

    /* access modifiers changed from: private */
    public void updateHint() {
        int i = this.selectedCount;
        if (i == 0) {
            this.actionBar.setSubtitle(LocaleController.formatString("MembersCountZero", NUM, LocaleController.formatPluralString("Chats", 100)));
            return;
        }
        this.actionBar.setSubtitle(String.format(LocaleController.getPluralString("MembersCountSelected", i), new Object[]{Integer.valueOf(this.selectedCount), 100}));
    }

    public void setDelegate(FilterUsersActivityDelegate filterUsersActivityDelegate) {
        this.delegate = filterUsersActivityDelegate;
    }

    public class GroupCreateAdapter extends RecyclerListView.FastScrollAdapter {
        private ArrayList<TLObject> contacts = new ArrayList<>();
        private Context context;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<TLObject> searchResult = new ArrayList<>();
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
                int i2 = (int) allDialogs.get(i).id;
                if (i2 != 0) {
                    if (i2 > 0) {
                        TLRPC$User user = FilterUsersActivity.this.getMessagesController().getUser(Integer.valueOf(i2));
                        if (user != null) {
                            this.contacts.add(user);
                            if (UserObject.isUserSelf(user)) {
                                z = true;
                            }
                        }
                    } else {
                        TLRPC$Chat chat = FilterUsersActivity.this.getMessagesController().getChat(Integer.valueOf(-i2));
                        if (chat != null) {
                            this.contacts.add(chat);
                        }
                    }
                }
            }
            if (!z) {
                this.contacts.add(0, FilterUsersActivity.this.getMessagesController().getUser(Integer.valueOf(FilterUsersActivity.this.getUserConfig().clientUserId)));
            }
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(false);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setAllowGlobalResults(false);
            this.searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
                public /* synthetic */ boolean canApplySearchResults(int i) {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
                }

                public /* synthetic */ SparseArray getExcludeCallParticipants() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
                }

                public /* synthetic */ SparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                public final void onDataSetChanged(int i) {
                    FilterUsersActivity.GroupCreateAdapter.this.lambda$new$0$FilterUsersActivity$GroupCreateAdapter(i);
                }

                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$FilterUsersActivity$GroupCreateAdapter(int i) {
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
                view = new GroupCreateUserCell(this.context, true, 0, true);
            }
            return new RecyclerListView.Holder(view);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v0, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: java.lang.StringBuilder} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:102:0x0219  */
        /* JADX WARNING: Removed duplicated region for block: B:113:? A[RETURN, SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x01c5  */
        /* JADX WARNING: Removed duplicated region for block: B:85:0x01cb  */
        /* JADX WARNING: Removed duplicated region for block: B:91:0x01db  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r12, int r13) {
            /*
                r11 = this;
                int r0 = r12.getItemViewType()
                r1 = 2
                r2 = 1
                if (r0 == r2) goto L_0x002e
                if (r0 == r1) goto L_0x000c
                goto L_0x022e
            L_0x000c:
                android.view.View r12 = r12.itemView
                org.telegram.ui.Cells.GraySectionCell r12 = (org.telegram.ui.Cells.GraySectionCell) r12
                if (r13 != 0) goto L_0x0020
                r13 = 2131625395(0x7f0e05b3, float:1.8877997E38)
                java.lang.String r0 = "FilterChatTypes"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x022e
            L_0x0020:
                r13 = 2131625396(0x7f0e05b4, float:1.8877999E38)
                java.lang.String r0 = "FilterChats"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                r12.setText(r13)
                goto L_0x022e
            L_0x002e:
                android.view.View r12 = r12.itemView
                org.telegram.ui.Cells.GroupCreateUserCell r12 = (org.telegram.ui.Cells.GroupCreateUserCell) r12
                boolean r0 = r11.searching
                r3 = 0
                r4 = 0
                if (r0 == 0) goto L_0x0119
                java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r11.searchResult
                int r0 = r0.size()
                org.telegram.ui.Adapters.SearchAdapterHelper r1 = r11.searchAdapterHelper
                java.util.ArrayList r1 = r1.getGlobalSearch()
                int r1 = r1.size()
                org.telegram.ui.Adapters.SearchAdapterHelper r5 = r11.searchAdapterHelper
                java.util.ArrayList r5 = r5.getLocalServerSearch()
                int r5 = r5.size()
                if (r13 < 0) goto L_0x005d
                if (r13 >= r0) goto L_0x005d
                java.util.ArrayList<org.telegram.tgnet.TLObject> r1 = r11.searchResult
                java.lang.Object r1 = r1.get(r13)
                goto L_0x0087
            L_0x005d:
                if (r13 < r0) goto L_0x0070
                int r6 = r5 + r0
                if (r13 >= r6) goto L_0x0070
                org.telegram.ui.Adapters.SearchAdapterHelper r1 = r11.searchAdapterHelper
                java.util.ArrayList r1 = r1.getLocalServerSearch()
                int r5 = r13 - r0
                java.lang.Object r1 = r1.get(r5)
                goto L_0x0087
            L_0x0070:
                int r6 = r0 + r5
                if (r13 <= r6) goto L_0x0086
                int r1 = r1 + r0
                int r1 = r1 + r5
                if (r13 >= r1) goto L_0x0086
                org.telegram.ui.Adapters.SearchAdapterHelper r1 = r11.searchAdapterHelper
                java.util.ArrayList r1 = r1.getGlobalSearch()
                int r6 = r13 - r0
                int r6 = r6 - r5
                java.lang.Object r1 = r1.get(r6)
                goto L_0x0087
            L_0x0086:
                r1 = r3
            L_0x0087:
                if (r1 == 0) goto L_0x01c0
                boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$User
                if (r5 == 0) goto L_0x0093
                r5 = r1
                org.telegram.tgnet.TLRPC$User r5 = (org.telegram.tgnet.TLRPC$User) r5
                java.lang.String r5 = r5.username
                goto L_0x0098
            L_0x0093:
                r5 = r1
                org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC$Chat) r5
                java.lang.String r5 = r5.username
            L_0x0098:
                java.lang.String r6 = "@"
                if (r13 >= r0) goto L_0x00ca
                java.util.ArrayList<java.lang.CharSequence> r0 = r11.searchResultNames
                java.lang.Object r13 = r0.get(r13)
                java.lang.CharSequence r13 = (java.lang.CharSequence) r13
                if (r13 == 0) goto L_0x01c1
                boolean r0 = android.text.TextUtils.isEmpty(r5)
                if (r0 != 0) goto L_0x01c1
                java.lang.String r0 = r13.toString()
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                r7.append(r6)
                r7.append(r5)
                java.lang.String r5 = r7.toString()
                boolean r0 = r0.startsWith(r5)
                if (r0 == 0) goto L_0x01c1
                r10 = r3
                r3 = r13
                r13 = r10
                goto L_0x01c1
            L_0x00ca:
                if (r13 <= r0) goto L_0x01c0
                boolean r13 = android.text.TextUtils.isEmpty(r5)
                if (r13 != 0) goto L_0x01c0
                org.telegram.ui.Adapters.SearchAdapterHelper r13 = r11.searchAdapterHelper
                java.lang.String r13 = r13.getLastFoundUsername()
                boolean r0 = r13.startsWith(r6)
                if (r0 == 0) goto L_0x00e2
                java.lang.String r13 = r13.substring(r2)
            L_0x00e2:
                android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x0115 }
                r0.<init>()     // Catch:{ Exception -> 0x0115 }
                r0.append(r6)     // Catch:{ Exception -> 0x0115 }
                r0.append(r5)     // Catch:{ Exception -> 0x0115 }
                int r6 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r5, r13)     // Catch:{ Exception -> 0x0115 }
                r7 = -1
                if (r6 == r7) goto L_0x0111
                int r13 = r13.length()     // Catch:{ Exception -> 0x0115 }
                if (r6 != 0) goto L_0x00fd
                int r13 = r13 + 1
                goto L_0x00ff
            L_0x00fd:
                int r6 = r6 + 1
            L_0x00ff:
                android.text.style.ForegroundColorSpan r7 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0115 }
                java.lang.String r8 = "windowBackgroundWhiteBlueText4"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)     // Catch:{ Exception -> 0x0115 }
                r7.<init>(r8)     // Catch:{ Exception -> 0x0115 }
                int r13 = r13 + r6
                r8 = 33
                r0.setSpan(r7, r6, r13, r8)     // Catch:{ Exception -> 0x0115 }
            L_0x0111:
                r13 = r3
                r3 = r0
                goto L_0x01c1
            L_0x0115:
                r13 = r3
                r3 = r5
                goto L_0x01c1
            L_0x0119:
                int r0 = r11.usersStartRow
                if (r13 >= r0) goto L_0x01b9
                org.telegram.ui.FilterUsersActivity r0 = org.telegram.ui.FilterUsersActivity.this
                boolean r0 = r0.isInclude
                if (r0 == 0) goto L_0x0176
                if (r13 != r2) goto L_0x0136
                r13 = 2131625401(0x7f0e05b9, float:1.8878009E38)
                java.lang.String r0 = "FilterContacts"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                int r0 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_CONTACTS
                java.lang.String r1 = "contacts"
                goto L_0x01a3
            L_0x0136:
                if (r13 != r1) goto L_0x0146
                r13 = 2131625431(0x7f0e05d7, float:1.887807E38)
                java.lang.String r0 = "FilterNonContacts"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                int r0 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS
                java.lang.String r1 = "non_contacts"
                goto L_0x01a3
            L_0x0146:
                r0 = 3
                if (r13 != r0) goto L_0x0157
                r13 = 2131625418(0x7f0e05ca, float:1.8878043E38)
                java.lang.String r0 = "FilterGroups"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                int r0 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_GROUPS
                java.lang.String r1 = "groups"
                goto L_0x01a3
            L_0x0157:
                r0 = 4
                if (r13 != r0) goto L_0x0168
                r13 = 2131625392(0x7f0e05b0, float:1.887799E38)
                java.lang.String r0 = "FilterChannels"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                int r0 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_CHANNELS
                java.lang.String r1 = "channels"
                goto L_0x01a3
            L_0x0168:
                r13 = 2131625391(0x7f0e05af, float:1.8877989E38)
                java.lang.String r0 = "FilterBots"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                int r0 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_BOTS
                java.lang.String r1 = "bots"
                goto L_0x01a3
            L_0x0176:
                if (r13 != r2) goto L_0x0186
                r13 = 2131625421(0x7f0e05cd, float:1.887805E38)
                java.lang.String r0 = "FilterMuted"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                int r0 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED
                java.lang.String r1 = "muted"
                goto L_0x01a3
            L_0x0186:
                if (r13 != r1) goto L_0x0196
                r13 = 2131625432(0x7f0e05d8, float:1.8878072E38)
                java.lang.String r0 = "FilterRead"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                int r0 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ
                java.lang.String r1 = "read"
                goto L_0x01a3
            L_0x0196:
                r13 = 2131625388(0x7f0e05ac, float:1.8877983E38)
                java.lang.String r0 = "FilterArchived"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
                int r0 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED
                java.lang.String r1 = "archived"
            L_0x01a3:
                r12.setObject(r1, r13, r3)
                org.telegram.ui.FilterUsersActivity r13 = org.telegram.ui.FilterUsersActivity.this
                int r13 = r13.filterFlags
                r13 = r13 & r0
                if (r13 != r0) goto L_0x01b1
                r13 = 1
                goto L_0x01b2
            L_0x01b1:
                r13 = 0
            L_0x01b2:
                r12.setChecked(r13, r4)
                r12.setCheckBoxEnabled(r2)
                return
            L_0x01b9:
                java.util.ArrayList<org.telegram.tgnet.TLObject> r1 = r11.contacts
                int r13 = r13 - r0
                java.lang.Object r1 = r1.get(r13)
            L_0x01c0:
                r13 = r3
            L_0x01c1:
                boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$User
                if (r0 == 0) goto L_0x01cb
                r0 = r1
                org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
                int r0 = r0.id
                goto L_0x01d7
            L_0x01cb:
                boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$Chat
                if (r0 == 0) goto L_0x01d6
                r0 = r1
                org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
                int r0 = r0.id
                int r0 = -r0
                goto L_0x01d7
            L_0x01d6:
                r0 = 0
            L_0x01d7:
                boolean r5 = r11.searching
                if (r5 != 0) goto L_0x0214
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                org.telegram.ui.FilterUsersActivity r5 = org.telegram.ui.FilterUsersActivity.this
                org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
                java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r5 = r5.dialogFilters
                int r6 = r5.size()
                r7 = 0
            L_0x01ed:
                if (r7 >= r6) goto L_0x0214
                java.lang.Object r8 = r5.get(r7)
                org.telegram.messenger.MessagesController$DialogFilter r8 = (org.telegram.messenger.MessagesController.DialogFilter) r8
                org.telegram.ui.FilterUsersActivity r9 = org.telegram.ui.FilterUsersActivity.this
                org.telegram.messenger.AccountInstance r9 = r9.getAccountInstance()
                boolean r9 = r8.includesDialog(r9, r0)
                if (r9 == 0) goto L_0x0211
                int r9 = r3.length()
                if (r9 <= 0) goto L_0x020c
                java.lang.String r9 = ", "
                r3.append(r9)
            L_0x020c:
                java.lang.String r8 = r8.name
                r3.append(r8)
            L_0x0211:
                int r7 = r7 + 1
                goto L_0x01ed
            L_0x0214:
                r12.setObject(r1, r13, r3)
                if (r0 == 0) goto L_0x022e
                org.telegram.ui.FilterUsersActivity r13 = org.telegram.ui.FilterUsersActivity.this
                android.util.SparseArray r13 = r13.selectedContacts
                int r13 = r13.indexOfKey(r0)
                if (r13 < 0) goto L_0x0227
                r13 = 1
                goto L_0x0228
            L_0x0227:
                r13 = 0
            L_0x0228:
                r12.setChecked(r13, r4)
                r12.setCheckBoxEnabled(r2)
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

        public int getPositionForScrollProgress(float f) {
            return (int) (((float) getItemCount()) * f);
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
                this.searchAdapterHelper.mergeResults((ArrayList<TLObject>) null);
                this.searchAdapterHelper.queryServerSearch((String) null, true, true, false, false, false, 0, false, 0, 0);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$FilterUsersActivity$GroupCreateAdapter$WpsDt72XjbKzaIwsRMVb8QyieVA r1 = new Runnable(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FilterUsersActivity.GroupCreateAdapter.this.lambda$searchDialogs$3$FilterUsersActivity$GroupCreateAdapter(this.f$1);
                }
            };
            this.searchRunnable = r1;
            dispatchQueue.postRunnable(r1, 300);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$searchDialogs$3 */
        public /* synthetic */ void lambda$searchDialogs$3$FilterUsersActivity$GroupCreateAdapter(String str) {
            AndroidUtilities.runOnUIThread(new Runnable(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FilterUsersActivity.GroupCreateAdapter.this.lambda$null$2$FilterUsersActivity$GroupCreateAdapter(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$2 */
        public /* synthetic */ void lambda$null$2$FilterUsersActivity$GroupCreateAdapter(String str) {
            this.searchAdapterHelper.queryServerSearch(str, true, true, true, true, false, 0, false, 0, 0);
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$FilterUsersActivity$GroupCreateAdapter$zZ_ABm2LzTBP5pXmZBLXJPBt0Pw r1 = new Runnable(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FilterUsersActivity.GroupCreateAdapter.this.lambda$null$1$FilterUsersActivity$GroupCreateAdapter(this.f$1);
                }
            };
            this.searchRunnable = r1;
            dispatchQueue.postRunnable(r1);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$1 */
        public /* synthetic */ void lambda$null$1$FilterUsersActivity$GroupCreateAdapter(String str) {
            String str2;
            int i;
            String str3;
            String lowerCase = str.trim().toLowerCase();
            if (lowerCase.length() == 0) {
                updateSearchResults(new ArrayList(), new ArrayList());
                return;
            }
            String translitString = LocaleController.getInstance().getTranslitString(lowerCase);
            String str4 = null;
            if (lowerCase.equals(translitString) || translitString.length() == 0) {
                translitString = null;
            }
            char c = 0;
            char c2 = 1;
            int i2 = (translitString != null ? 1 : 0) + 1;
            String[] strArr = new String[i2];
            strArr[0] = lowerCase;
            if (translitString != null) {
                strArr[1] = translitString;
            }
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int i3 = 0;
            while (i3 < this.contacts.size()) {
                TLObject tLObject = this.contacts.get(i3);
                String[] strArr2 = new String[3];
                boolean z = tLObject instanceof TLRPC$User;
                if (z) {
                    TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
                    strArr2[c] = ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name).toLowerCase();
                    str2 = tLRPC$User.username;
                    if (UserObject.isReplyUser(tLRPC$User)) {
                        strArr2[2] = LocaleController.getString("RepliesTitle", NUM).toLowerCase();
                    } else if (tLRPC$User.self) {
                        strArr2[2] = LocaleController.getString("SavedMessages", NUM).toLowerCase();
                    }
                } else {
                    TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) tLObject;
                    strArr2[c] = tLRPC$Chat.title.toLowerCase();
                    str2 = tLRPC$Chat.username;
                }
                strArr2[c2] = LocaleController.getInstance().getTranslitString(strArr2[c]);
                if (strArr2[c].equals(strArr2[c2])) {
                    strArr2[c2] = str4;
                }
                int i4 = 0;
                char c3 = 0;
                while (true) {
                    if (i4 >= i2) {
                        i = i2;
                        str3 = str4;
                        break;
                    }
                    String str5 = strArr[i4];
                    int i5 = 0;
                    while (true) {
                        if (i5 >= 3) {
                            i = i2;
                            break;
                        }
                        String str6 = strArr2[i5];
                        if (str6 != null) {
                            if (str6.startsWith(str5)) {
                                i = i2;
                                break;
                            }
                            StringBuilder sb = new StringBuilder();
                            i = i2;
                            sb.append(" ");
                            sb.append(str5);
                            if (str6.contains(sb.toString())) {
                                break;
                            }
                        } else {
                            i = i2;
                        }
                        i5++;
                        i2 = i;
                    }
                    c3 = 1;
                    if (c3 == 0 && str2 != null && str2.toLowerCase().startsWith(str5)) {
                        c3 = 2;
                    }
                    if (c3 != 0) {
                        if (c3 == 1) {
                            if (z) {
                                TLRPC$User tLRPC$User2 = (TLRPC$User) tLObject;
                                arrayList2.add(AndroidUtilities.generateSearchName(tLRPC$User2.first_name, tLRPC$User2.last_name, str5));
                            } else {
                                arrayList2.add(AndroidUtilities.generateSearchName(((TLRPC$Chat) tLObject).title, (String) null, str5));
                            }
                            str3 = null;
                        } else {
                            str3 = null;
                            arrayList2.add(AndroidUtilities.generateSearchName("@" + str2, (String) null, "@" + str5));
                        }
                        arrayList.add(tLObject);
                    } else {
                        i4++;
                        str4 = null;
                        i2 = i;
                    }
                }
                i3++;
                str4 = str3;
                i2 = i;
                c = 0;
                c2 = 1;
            }
            updateSearchResults(arrayList, arrayList2);
        }

        private void updateSearchResults(ArrayList<TLObject> arrayList, ArrayList<CharSequence> arrayList2) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList, arrayList2) {
                public final /* synthetic */ ArrayList f$1;
                public final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    FilterUsersActivity.GroupCreateAdapter.this.lambda$updateSearchResults$4$FilterUsersActivity$GroupCreateAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$updateSearchResults$4 */
        public /* synthetic */ void lambda$updateSearchResults$4$FilterUsersActivity$GroupCreateAdapter(ArrayList arrayList, ArrayList arrayList2) {
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
        $$Lambda$FilterUsersActivity$b9VaH2b6cApvvdsidx2kD4VbZc r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                FilterUsersActivity.this.lambda$getThemeDescriptions$3$FilterUsersActivity();
            }
        };
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
        $$Lambda$FilterUsersActivity$b9VaH2b6cApvvdsidx2kD4VbZc r8 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_spanBackground"));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_spanText"));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_spanDelete"));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundBlue"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getThemeDescriptions$3 */
    public /* synthetic */ void lambda$getThemeDescriptions$3$FilterUsersActivity() {
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
