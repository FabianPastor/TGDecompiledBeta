package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
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
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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

public class FilterUsersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, View.OnClickListener {
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public GroupCreateAdapter adapter;
    /* access modifiers changed from: private */
    public ArrayList<GroupCreateSpan> allSpans = new ArrayList<>();
    /* access modifiers changed from: private */
    public int containerHeight;
    /* access modifiers changed from: private */
    public GroupCreateSpan currentDeletingSpan;
    private AnimatorSet currentDoneButtonAnimation;
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

    static /* synthetic */ int access$1972(FilterUsersActivity x0, int x1) {
        int i = x0.filterFlags & x1;
        x0.filterFlags = i;
        return i;
    }

    static /* synthetic */ int access$508(FilterUsersActivity x0) {
        int i = x0.selectedCount;
        x0.selectedCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$510(FilterUsersActivity x0) {
        int i = x0.selectedCount;
        x0.selectedCount = i - 1;
        return i;
    }

    private static class ItemDecoration extends RecyclerView.ItemDecoration {
        private boolean single;
        private int skipRows;

        private ItemDecoration() {
        }

        public void setSingle(boolean value) {
            this.single = value;
        }

        public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            RecyclerView recyclerView = parent;
            int width = parent.getWidth();
            int childCount = parent.getChildCount() - (this.single ^ true ? 1 : 0);
            int i = 0;
            while (i < childCount) {
                View child = recyclerView.getChildAt(i);
                View nextChild = i < childCount + -1 ? recyclerView.getChildAt(i + 1) : null;
                if (recyclerView.getChildAdapterPosition(child) >= this.skipRows && !(child instanceof GraySectionCell) && !(nextChild instanceof GraySectionCell)) {
                    int top = child.getBottom();
                    canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(72.0f), (float) top, (float) (width - (LocaleController.isRTL ? AndroidUtilities.dp(72.0f) : 0)), (float) top, Theme.dividerPaint);
                }
                i++;
            }
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.top = 1;
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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int minWidth;
            int count = getChildCount();
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int maxWidth = width - AndroidUtilities.dp(26.0f);
            int currentLineWidth = 0;
            int y = AndroidUtilities.dp(10.0f);
            int allCurrentLineWidth = 0;
            int allY = AndroidUtilities.dp(10.0f);
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (child instanceof GroupCreateSpan) {
                    child.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
                    if (child != this.removingSpan && child.getMeasuredWidth() + currentLineWidth > maxWidth) {
                        y += child.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        currentLineWidth = 0;
                    }
                    if (child.getMeasuredWidth() + allCurrentLineWidth > maxWidth) {
                        allY += child.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        allCurrentLineWidth = 0;
                    }
                    int x = AndroidUtilities.dp(13.0f) + currentLineWidth;
                    if (!this.animationStarted) {
                        View view = this.removingSpan;
                        if (child == view) {
                            child.setTranslationX((float) (AndroidUtilities.dp(13.0f) + allCurrentLineWidth));
                            child.setTranslationY((float) allY);
                        } else if (view != null) {
                            if (child.getTranslationX() != ((float) x)) {
                                this.animators.add(ObjectAnimator.ofFloat(child, View.TRANSLATION_X, new float[]{(float) x}));
                            }
                            if (child.getTranslationY() != ((float) y)) {
                                this.animators.add(ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, new float[]{(float) y}));
                            }
                        } else {
                            child.setTranslationX((float) x);
                            child.setTranslationY((float) y);
                        }
                    }
                    if (child != this.removingSpan) {
                        currentLineWidth += child.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                    }
                    allCurrentLineWidth += child.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                }
            }
            if (AndroidUtilities.isTablet() != 0) {
                minWidth = AndroidUtilities.dp(372.0f) / 3;
            } else {
                minWidth = (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(158.0f)) / 3;
            }
            if (maxWidth - currentLineWidth < minWidth) {
                currentLineWidth = 0;
                y += AndroidUtilities.dp(40.0f);
            }
            if (maxWidth - allCurrentLineWidth < minWidth) {
                allY += AndroidUtilities.dp(40.0f);
            }
            FilterUsersActivity.this.editText.measure(View.MeasureSpec.makeMeasureSpec(maxWidth - currentLineWidth, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
            if (!this.animationStarted) {
                int currentHeight = AndroidUtilities.dp(42.0f) + allY;
                int fieldX = AndroidUtilities.dp(16.0f) + currentLineWidth;
                int unused = FilterUsersActivity.this.fieldY = y;
                if (this.currentAnimation != null) {
                    int resultHeight = AndroidUtilities.dp(42.0f) + y;
                    if (FilterUsersActivity.this.containerHeight != resultHeight) {
                        int i = count;
                        this.animators.add(ObjectAnimator.ofInt(FilterUsersActivity.this, "containerHeight", new int[]{resultHeight}));
                    }
                    if (FilterUsersActivity.this.editText.getTranslationX() != ((float) fieldX)) {
                        int i2 = maxWidth;
                        this.animators.add(ObjectAnimator.ofFloat(FilterUsersActivity.this.editText, View.TRANSLATION_X, new float[]{(float) fieldX}));
                    }
                    if (FilterUsersActivity.this.editText.getTranslationY() != ((float) FilterUsersActivity.this.fieldY)) {
                        this.animators.add(ObjectAnimator.ofFloat(FilterUsersActivity.this.editText, View.TRANSLATION_Y, new float[]{(float) FilterUsersActivity.this.fieldY}));
                    }
                    FilterUsersActivity.this.editText.setAllowDrawCursor(false);
                    this.currentAnimation.playTogether(this.animators);
                    this.currentAnimation.start();
                    this.animationStarted = true;
                } else {
                    int i3 = maxWidth;
                    int unused2 = FilterUsersActivity.this.containerHeight = currentHeight;
                    FilterUsersActivity.this.editText.setTranslationX((float) fieldX);
                    FilterUsersActivity.this.editText.setTranslationY((float) FilterUsersActivity.this.fieldY);
                }
            } else {
                int i4 = maxWidth;
                if (this.currentAnimation != null && !FilterUsersActivity.this.ignoreScrollEvent && this.removingSpan == null) {
                    FilterUsersActivity.this.editText.bringPointIntoView(FilterUsersActivity.this.editText.getSelectionStart());
                }
            }
            setMeasuredDimension(width, FilterUsersActivity.this.containerHeight);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int count = getChildCount();
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan span, boolean animated) {
            FilterUsersActivity.this.allSpans.add(span);
            long uid = span.getUid();
            if (uid > -NUM) {
                FilterUsersActivity.access$508(FilterUsersActivity.this);
            }
            FilterUsersActivity.this.selectedContacts.put(uid, span);
            FilterUsersActivity.this.editText.setHintVisible(false);
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            if (animated) {
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
                this.addingSpan = span;
                this.animators.clear();
                this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.SCALE_X, new float[]{0.01f, 1.0f}));
                this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.SCALE_Y, new float[]{0.01f, 1.0f}));
                this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.ALPHA, new float[]{0.0f, 1.0f}));
            }
            addView(span);
        }

        public void removeSpan(final GroupCreateSpan span) {
            boolean unused = FilterUsersActivity.this.ignoreScrollEvent = true;
            long uid = span.getUid();
            if (uid > -NUM) {
                FilterUsersActivity.access$510(FilterUsersActivity.this);
            }
            FilterUsersActivity.this.selectedContacts.remove(uid);
            FilterUsersActivity.this.allSpans.remove(span);
            span.setOnClickListener((View.OnClickListener) null);
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
                    SpansContainer.this.removeView(span);
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
            this.removingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.SCALE_X, new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.SCALE_Y, new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.ALPHA, new float[]{1.0f, 0.0f}));
            requestLayout();
        }
    }

    public FilterUsersActivity(boolean include, ArrayList<Long> arrayList, int flags) {
        this.isInclude = include;
        this.filterFlags = flags;
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

    public void onClick(View v) {
        GroupCreateSpan span = (GroupCreateSpan) v;
        if (span.isDeleting()) {
            this.currentDeletingSpan = null;
            this.spansContainer.removeSpan(span);
            if (span.getUid() == -2147483648L) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_CONTACTS ^ -1;
            } else if (span.getUid() == -2147483647L) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS ^ -1;
            } else if (span.getUid() == -NUM) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_GROUPS ^ -1;
            } else if (span.getUid() == -NUM) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_CHANNELS ^ -1;
            } else if (span.getUid() == -NUM) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_BOTS ^ -1;
            } else if (span.getUid() == -NUM) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED ^ -1;
            } else if (span.getUid() == -NUM) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ ^ -1;
            } else if (span.getUid() == -NUM) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED ^ -1;
            }
            updateHint();
            checkVisibleRows();
            return;
        }
        GroupCreateSpan groupCreateSpan = this.currentDeletingSpan;
        if (groupCreateSpan != null) {
            groupCreateSpan.cancelDeleteAnimation();
        }
        this.currentDeletingSpan = span;
        span.startDeleteAnimation();
    }

    public View createView(Context context) {
        TLObject object;
        int flag;
        Object object2;
        Context context2 = context;
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
            public void onItemClick(int id) {
                if (id == -1) {
                    FilterUsersActivity.this.finishFragment();
                } else if (id == 1) {
                    boolean unused = FilterUsersActivity.this.onDonePressed(true);
                }
            }
        });
        this.fragmentView = new ViewGroup(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int maxSize;
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(width, height);
                float f = 56.0f;
                if (AndroidUtilities.isTablet() || height > width) {
                    maxSize = AndroidUtilities.dp(144.0f);
                } else {
                    maxSize = AndroidUtilities.dp(56.0f);
                }
                FilterUsersActivity.this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(maxSize, Integer.MIN_VALUE));
                FilterUsersActivity.this.listView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height - FilterUsersActivity.this.scrollView.getMeasuredHeight(), NUM));
                FilterUsersActivity.this.emptyView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height - FilterUsersActivity.this.scrollView.getMeasuredHeight(), NUM));
                if (FilterUsersActivity.this.floatingButton != null) {
                    if (Build.VERSION.SDK_INT < 21) {
                        f = 60.0f;
                    }
                    int w = AndroidUtilities.dp(f);
                    FilterUsersActivity.this.floatingButton.measure(View.MeasureSpec.makeMeasureSpec(w, NUM), View.MeasureSpec.makeMeasureSpec(w, NUM));
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                FilterUsersActivity.this.scrollView.layout(0, 0, FilterUsersActivity.this.scrollView.getMeasuredWidth(), FilterUsersActivity.this.scrollView.getMeasuredHeight());
                FilterUsersActivity.this.listView.layout(0, FilterUsersActivity.this.scrollView.getMeasuredHeight(), FilterUsersActivity.this.listView.getMeasuredWidth(), FilterUsersActivity.this.scrollView.getMeasuredHeight() + FilterUsersActivity.this.listView.getMeasuredHeight());
                FilterUsersActivity.this.emptyView.layout(0, FilterUsersActivity.this.scrollView.getMeasuredHeight(), FilterUsersActivity.this.emptyView.getMeasuredWidth(), FilterUsersActivity.this.scrollView.getMeasuredHeight() + FilterUsersActivity.this.emptyView.getMeasuredHeight());
                if (FilterUsersActivity.this.floatingButton != null) {
                    int l = LocaleController.isRTL ? AndroidUtilities.dp(14.0f) : ((right - left) - AndroidUtilities.dp(14.0f)) - FilterUsersActivity.this.floatingButton.getMeasuredWidth();
                    int t = ((bottom - top) - AndroidUtilities.dp(14.0f)) - FilterUsersActivity.this.floatingButton.getMeasuredHeight();
                    FilterUsersActivity.this.floatingButton.layout(l, t, FilterUsersActivity.this.floatingButton.getMeasuredWidth() + l, FilterUsersActivity.this.floatingButton.getMeasuredHeight() + t);
                }
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == FilterUsersActivity.this.listView || child == FilterUsersActivity.this.emptyView) {
                    FilterUsersActivity.this.parentLayout.drawHeaderShadow(canvas, FilterUsersActivity.this.scrollView.getMeasuredHeight());
                }
                return result;
            }
        };
        ViewGroup frameLayout = (ViewGroup) this.fragmentView;
        AnonymousClass3 r6 = new ScrollView(context2) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                if (FilterUsersActivity.this.ignoreScrollEvent) {
                    boolean unused = FilterUsersActivity.this.ignoreScrollEvent = false;
                    return false;
                }
                rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
                rectangle.top += FilterUsersActivity.this.fieldY + AndroidUtilities.dp(20.0f);
                rectangle.bottom += FilterUsersActivity.this.fieldY + AndroidUtilities.dp(50.0f);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        this.scrollView = r6;
        r6.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("windowBackgroundWhite"));
        frameLayout.addView(this.scrollView);
        SpansContainer spansContainer2 = new SpansContainer(context2);
        this.spansContainer = spansContainer2;
        this.scrollView.addView(spansContainer2, LayoutHelper.createFrame(-1, -2.0f));
        this.spansContainer.setOnClickListener(new FilterUsersActivity$$ExternalSyntheticLambda0(this));
        AnonymousClass4 r62 = new EditTextBoldCursor(context2) {
            public boolean onTouchEvent(MotionEvent event) {
                if (FilterUsersActivity.this.currentDeletingSpan != null) {
                    FilterUsersActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    GroupCreateSpan unused = FilterUsersActivity.this.currentDeletingSpan = null;
                }
                if (event.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(event);
            }
        };
        this.editText = r62;
        r62.setTextSize(1, 16.0f);
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
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.spansContainer.addView(this.editText);
        this.editText.setHintText(LocaleController.getString("SearchForPeopleAndGroups", NUM));
        this.editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        this.editText.setOnKeyListener(new View.OnKeyListener() {
            private boolean wasEmpty;

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 67) {
                    boolean z = true;
                    if (event.getAction() == 0) {
                        if (FilterUsersActivity.this.editText.length() != 0) {
                            z = false;
                        }
                        this.wasEmpty = z;
                    } else if (event.getAction() == 1 && this.wasEmpty && !FilterUsersActivity.this.allSpans.isEmpty()) {
                        GroupCreateSpan span = (GroupCreateSpan) FilterUsersActivity.this.allSpans.get(FilterUsersActivity.this.allSpans.size() - 1);
                        FilterUsersActivity.this.spansContainer.removeSpan(span);
                        if (span.getUid() == -2147483648L) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_CONTACTS ^ -1);
                        } else if (span.getUid() == -2147483647L) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS ^ -1);
                        } else if (span.getUid() == -NUM) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_GROUPS ^ -1);
                        } else if (span.getUid() == -NUM) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_CHANNELS ^ -1);
                        } else if (span.getUid() == -NUM) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_BOTS ^ -1);
                        } else if (span.getUid() == -NUM) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED ^ -1);
                        } else if (span.getUid() == -NUM) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ ^ -1);
                        } else if (span.getUid() == -NUM) {
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
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
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
        this.emptyView = new EmptyTextProgressView(context2);
        if (ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
        frameLayout.addView(this.emptyView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setFastScrollEnabled(0);
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        GroupCreateAdapter groupCreateAdapter = new GroupCreateAdapter(context2);
        this.adapter = groupCreateAdapter;
        recyclerListView2.setAdapter(groupCreateAdapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        this.listView.addItemDecoration(new ItemDecoration());
        frameLayout.addView(this.listView);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new FilterUsersActivity$$ExternalSyntheticLambda3(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(FilterUsersActivity.this.editText);
                }
            }
        });
        ImageView imageView = new ImageView(context2);
        this.floatingButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(NUM).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(drawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
        this.floatingButton.setImageResource(NUM);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButton.setStateListAnimator(animator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        frameLayout.addView(this.floatingButton);
        this.floatingButton.setOnClickListener(new FilterUsersActivity$$ExternalSyntheticLambda1(this));
        this.floatingButton.setContentDescription(LocaleController.getString("Next", NUM));
        int N = this.isInclude ? 5 : 3;
        for (int position = 1; position <= N; position++) {
            if (this.isInclude) {
                if (position == 1) {
                    flag = MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
                    object2 = "contacts";
                } else if (position == 2) {
                    flag = MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
                    object2 = "non_contacts";
                } else if (position == 3) {
                    object2 = "groups";
                    flag = MessagesController.DIALOG_FILTER_FLAG_GROUPS;
                } else if (position == 4) {
                    object2 = "channels";
                    flag = MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
                } else {
                    object2 = "bots";
                    flag = MessagesController.DIALOG_FILTER_FLAG_BOTS;
                }
            } else if (position == 1) {
                object2 = "muted";
                flag = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;
            } else if (position == 2) {
                object2 = "read";
                flag = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ;
            } else {
                object2 = "archived";
                flag = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;
            }
            if ((this.filterFlags & flag) != 0) {
                GroupCreateSpan span = new GroupCreateSpan(this.editText.getContext(), object2);
                this.spansContainer.addSpan(span, false);
                span.setOnClickListener(this);
            }
        }
        ArrayList<Long> arrayList = this.initialIds;
        if (arrayList != null && !arrayList.isEmpty()) {
            int N2 = this.initialIds.size();
            for (int a = 0; a < N2; a++) {
                Long id = this.initialIds.get(a);
                if (id.longValue() > 0) {
                    object = getMessagesController().getUser(id);
                } else {
                    object = getMessagesController().getChat(Long.valueOf(-id.longValue()));
                }
                if (object != null) {
                    GroupCreateSpan span2 = new GroupCreateSpan(this.editText.getContext(), (Object) object);
                    this.spansContainer.addSpan(span2, false);
                    span2.setOnClickListener(this);
                }
            }
        }
        updateHint();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-FilterUsersActivity  reason: not valid java name */
    public /* synthetic */ void m2160lambda$createView$0$orgtelegramuiFilterUsersActivity(View v) {
        this.editText.clearFocus();
        this.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.editText);
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-FilterUsersActivity  reason: not valid java name */
    public /* synthetic */ void m2161lambda$createView$1$orgtelegramuiFilterUsersActivity(View view, int position) {
        long id;
        int flag;
        if (view instanceof GroupCreateUserCell) {
            GroupCreateUserCell cell = (GroupCreateUserCell) view;
            Object object = cell.getObject();
            if (object instanceof String) {
                if (this.isInclude) {
                    if (position == 1) {
                        flag = MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
                        id = -2147483648L;
                    } else if (position == 2) {
                        flag = MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
                        id = -2147483647L;
                    } else if (position == 3) {
                        flag = MessagesController.DIALOG_FILTER_FLAG_GROUPS;
                        id = -NUM;
                    } else if (position == 4) {
                        flag = MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
                        id = -NUM;
                    } else {
                        flag = MessagesController.DIALOG_FILTER_FLAG_BOTS;
                        id = -NUM;
                    }
                } else if (position == 1) {
                    flag = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;
                    id = -NUM;
                } else if (position == 2) {
                    flag = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ;
                    id = -NUM;
                } else {
                    flag = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;
                    id = -NUM;
                }
                if (cell.isChecked()) {
                    this.filterFlags &= flag ^ -1;
                } else {
                    this.filterFlags |= flag;
                }
            } else if (object instanceof TLRPC.User) {
                id = ((TLRPC.User) object).id;
            } else if (object instanceof TLRPC.Chat) {
                id = -((TLRPC.Chat) object).id;
            } else {
                return;
            }
            boolean z = false;
            boolean z2 = this.selectedContacts.indexOfKey(id) >= 0;
            boolean exists = z2;
            if (z2) {
                this.spansContainer.removeSpan(this.selectedContacts.get(id));
            } else if ((object instanceof String) || this.selectedCount < 100) {
                if (object instanceof TLRPC.User) {
                    MessagesController.getInstance(this.currentAccount).putUser((TLRPC.User) object, !this.searching);
                } else if (object instanceof TLRPC.Chat) {
                    MessagesController.getInstance(this.currentAccount).putChat((TLRPC.Chat) object, !this.searching);
                }
                GroupCreateSpan span = new GroupCreateSpan(this.editText.getContext(), object);
                this.spansContainer.addSpan(span, true);
                span.setOnClickListener(this);
            } else {
                return;
            }
            updateHint();
            if (this.searching || this.searchWas) {
                AndroidUtilities.showKeyboard(this.editText);
            } else {
                if (!exists) {
                    z = true;
                }
                cell.setChecked(z, true);
            }
            if (this.editText.length() > 0) {
                this.editText.setText((CharSequence) null);
            }
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-FilterUsersActivity  reason: not valid java name */
    public /* synthetic */ void m2162lambda$createView$2$orgtelegramuiFilterUsersActivity(View v) {
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.contactsDidLoad) {
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null) {
                emptyTextProgressView.showTextView();
            }
            GroupCreateAdapter groupCreateAdapter = this.adapter;
            if (groupCreateAdapter != null) {
                groupCreateAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            if (this.listView != null) {
                int mask = args[0].intValue();
                int count = this.listView.getChildCount();
                if ((MessagesController.UPDATE_MASK_AVATAR & mask) != 0 || (MessagesController.UPDATE_MASK_NAME & mask) != 0 || (MessagesController.UPDATE_MASK_STATUS & mask) != 0) {
                    for (int a = 0; a < count; a++) {
                        View child = this.listView.getChildAt(a);
                        if (child instanceof GroupCreateUserCell) {
                            ((GroupCreateUserCell) child).update(mask);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.chatDidCreated) {
            removeSelfFromStack();
        }
    }

    public void setContainerHeight(int value) {
        this.containerHeight = value;
        SpansContainer spansContainer2 = this.spansContainer;
        if (spansContainer2 != null) {
            spansContainer2.requestLayout();
        }
    }

    public int getContainerHeight() {
        return this.containerHeight;
    }

    /* access modifiers changed from: private */
    public void checkVisibleRows() {
        long id;
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof GroupCreateUserCell) {
                GroupCreateUserCell cell = (GroupCreateUserCell) child;
                Object object = cell.getObject();
                boolean z = false;
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
                            id = -2147483648L;
                            break;
                        case 1:
                            id = -2147483647L;
                            break;
                        case 2:
                            id = -NUM;
                            break;
                        case 3:
                            id = -NUM;
                            break;
                        case 4:
                            id = -NUM;
                            break;
                        case 5:
                            id = -NUM;
                            break;
                        case 6:
                            id = -NUM;
                            break;
                        default:
                            id = -NUM;
                            break;
                    }
                } else if (object instanceof TLRPC.User) {
                    id = ((TLRPC.User) object).id;
                } else if (object instanceof TLRPC.Chat) {
                    id = -((TLRPC.Chat) object).id;
                } else {
                    id = 0;
                }
                if (id != 0) {
                    if (this.selectedContacts.indexOfKey(id) >= 0) {
                        z = true;
                    }
                    cell.setChecked(z, true);
                    cell.setCheckBoxEnabled(true);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean onDonePressed(boolean alert) {
        ArrayList<Long> result = new ArrayList<>();
        for (int a = 0; a < this.selectedContacts.size(); a++) {
            if (this.selectedContacts.keyAt(a) > -NUM) {
                result.add(Long.valueOf(this.selectedContacts.keyAt(a)));
            }
        }
        FilterUsersActivityDelegate filterUsersActivityDelegate = this.delegate;
        if (filterUsersActivityDelegate != null) {
            filterUsersActivityDelegate.didSelectChats(result, this.filterFlags);
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
        if (this.selectedCount == 0) {
            this.actionBar.setSubtitle(LocaleController.formatString("MembersCountZero", NUM, LocaleController.formatPluralString("Chats", 100)));
            return;
        }
        this.actionBar.setSubtitle(String.format(LocaleController.getPluralString("MembersCountSelected", this.selectedCount), new Object[]{Integer.valueOf(this.selectedCount), 100}));
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

        public GroupCreateAdapter(Context ctx) {
            this.usersStartRow = FilterUsersActivity.this.isInclude ? 7 : 5;
            this.context = ctx;
            boolean hasSelf = false;
            ArrayList<TLRPC.Dialog> dialogs = FilterUsersActivity.this.getMessagesController().getAllDialogs();
            int N = dialogs.size();
            for (int a = 0; a < N; a++) {
                TLRPC.Dialog dialog = dialogs.get(a);
                if (!DialogObject.isEncryptedDialog(dialog.id)) {
                    if (DialogObject.isUserDialog(dialog.id)) {
                        TLRPC.User user = FilterUsersActivity.this.getMessagesController().getUser(Long.valueOf(dialog.id));
                        if (user != null) {
                            this.contacts.add(user);
                            if (UserObject.isUserSelf(user)) {
                                hasSelf = true;
                            }
                        }
                    } else {
                        TLRPC.Chat chat = FilterUsersActivity.this.getMessagesController().getChat(Long.valueOf(-dialog.id));
                        if (chat != null) {
                            this.contacts.add(chat);
                        }
                    }
                }
            }
            if (!hasSelf) {
                this.contacts.add(0, FilterUsersActivity.this.getMessagesController().getUser(Long.valueOf(FilterUsersActivity.this.getUserConfig().clientUserId)));
            }
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(false);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setAllowGlobalResults(false);
            this.searchAdapterHelper.setDelegate(new FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda4(this));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-FilterUsersActivity$GroupCreateAdapter  reason: not valid java name */
        public /* synthetic */ void m2164xa4656924(int searchId) {
            if (this.searchRunnable == null && !this.searchAdapterHelper.isSearchInProgress()) {
                FilterUsersActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        public void setSearching(boolean value) {
            if (this.searching != value) {
                this.searching = value;
                notifyDataSetChanged();
            }
        }

        public String getLetter(int position) {
            return null;
        }

        public int getItemCount() {
            int count;
            if (this.searching) {
                return this.searchResult.size() + this.searchAdapterHelper.getLocalServerSearch().size() + this.searchAdapterHelper.getGlobalSearch().size();
            }
            if (FilterUsersActivity.this.isInclude) {
                count = 7;
            } else {
                count = 5;
            }
            return count + this.contacts.size();
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 1:
                    view = new GroupCreateUserCell(this.context, 1, 0, true);
                    break;
                default:
                    view = new GraySectionCell(this.context);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v0, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v50, resolved type: java.lang.StringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v5, resolved type: java.lang.StringBuilder} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r18, int r19) {
            /*
                r17 = this;
                r1 = r17
                r2 = r18
                r3 = r19
                int r0 = r18.getItemViewType()
                switch(r0) {
                    case 1: goto L_0x0031;
                    case 2: goto L_0x000f;
                    default: goto L_0x000d;
                }
            L_0x000d:
                goto L_0x0253
            L_0x000f:
                android.view.View r0 = r2.itemView
                org.telegram.ui.Cells.GraySectionCell r0 = (org.telegram.ui.Cells.GraySectionCell) r0
                if (r3 != 0) goto L_0x0023
                r4 = 2131625739(0x7f0e070b, float:1.8878694E38)
                java.lang.String r5 = "FilterChatTypes"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setText(r4)
                goto L_0x0253
            L_0x0023:
                r4 = 2131625740(0x7f0e070c, float:1.8878696E38)
                java.lang.String r5 = "FilterChats"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setText(r4)
                goto L_0x0253
            L_0x0031:
                android.view.View r0 = r2.itemView
                r4 = r0
                org.telegram.ui.Cells.GroupCreateUserCell r4 = (org.telegram.ui.Cells.GroupCreateUserCell) r4
                r5 = 0
                r6 = 0
                boolean r0 = r1.searching
                r8 = 1
                if (r0 == 0) goto L_0x0130
                java.util.ArrayList<java.lang.Object> r0 = r1.searchResult
                int r9 = r0.size()
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getGlobalSearch()
                int r10 = r0.size()
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getLocalServerSearch()
                int r11 = r0.size()
                if (r3 < 0) goto L_0x0063
                if (r3 >= r9) goto L_0x0063
                java.util.ArrayList<java.lang.Object> r0 = r1.searchResult
                java.lang.Object r0 = r0.get(r3)
                r12 = r0
                goto L_0x0091
            L_0x0063:
                if (r3 < r9) goto L_0x0077
                int r0 = r11 + r9
                if (r3 >= r0) goto L_0x0077
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getLocalServerSearch()
                int r12 = r3 - r9
                java.lang.Object r0 = r0.get(r12)
                r12 = r0
                goto L_0x0091
            L_0x0077:
                int r0 = r9 + r11
                if (r3 <= r0) goto L_0x008f
                int r0 = r10 + r9
                int r0 = r0 + r11
                if (r3 >= r0) goto L_0x008f
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getGlobalSearch()
                int r12 = r3 - r9
                int r12 = r12 - r11
                java.lang.Object r0 = r0.get(r12)
                r12 = r0
                goto L_0x0091
            L_0x008f:
                r0 = 0
                r12 = r0
            L_0x0091:
                if (r12 == 0) goto L_0x012e
                boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC.User
                if (r0 == 0) goto L_0x009e
                r0 = r12
                org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
                java.lang.String r0 = r0.username
                r13 = r0
                goto L_0x00a4
            L_0x009e:
                r0 = r12
                org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
                java.lang.String r0 = r0.username
                r13 = r0
            L_0x00a4:
                java.lang.String r0 = "@"
                if (r3 >= r9) goto L_0x00d7
                java.util.ArrayList<java.lang.CharSequence> r14 = r1.searchResultNames
                java.lang.Object r14 = r14.get(r3)
                r6 = r14
                java.lang.CharSequence r6 = (java.lang.CharSequence) r6
                if (r6 == 0) goto L_0x012e
                boolean r14 = android.text.TextUtils.isEmpty(r13)
                if (r14 != 0) goto L_0x012e
                java.lang.String r14 = r6.toString()
                java.lang.StringBuilder r15 = new java.lang.StringBuilder
                r15.<init>()
                r15.append(r0)
                r15.append(r13)
                java.lang.String r0 = r15.toString()
                boolean r0 = r14.startsWith(r0)
                if (r0 == 0) goto L_0x012e
                r0 = r6
                r5 = 0
                r6 = r5
                r5 = r0
                goto L_0x012e
            L_0x00d7:
                if (r3 <= r9) goto L_0x012e
                boolean r14 = android.text.TextUtils.isEmpty(r13)
                if (r14 != 0) goto L_0x012e
                org.telegram.ui.Adapters.SearchAdapterHelper r14 = r1.searchAdapterHelper
                java.lang.String r14 = r14.getLastFoundUsername()
                boolean r15 = r14.startsWith(r0)
                if (r15 == 0) goto L_0x00ef
                java.lang.String r14 = r14.substring(r8)
            L_0x00ef:
                android.text.SpannableStringBuilder r15 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x012b }
                r15.<init>()     // Catch:{ Exception -> 0x012b }
                r15.append(r0)     // Catch:{ Exception -> 0x012b }
                r15.append(r13)     // Catch:{ Exception -> 0x012b }
                int r0 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r13, r14)     // Catch:{ Exception -> 0x012b }
                r16 = r0
                r7 = -1
                if (r0 == r7) goto L_0x0128
                int r0 = r14.length()     // Catch:{ Exception -> 0x012b }
                if (r16 != 0) goto L_0x010e
                int r0 = r0 + 1
                r7 = r16
                goto L_0x0112
            L_0x010e:
                int r16 = r16 + 1
                r7 = r16
            L_0x0112:
                android.text.style.ForegroundColorSpan r8 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x012b }
                java.lang.String r16 = "windowBackgroundWhiteBlueText4"
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)     // Catch:{ Exception -> 0x012b }
                r8.<init>(r2)     // Catch:{ Exception -> 0x012b }
                int r2 = r7 + r0
                r16 = r0
                r0 = 33
                r15.setSpan(r8, r7, r2, r0)     // Catch:{ Exception -> 0x012b }
                r16 = r7
            L_0x0128:
                r0 = r15
                r5 = r0
                goto L_0x012e
            L_0x012b:
                r0 = move-exception
                r2 = r13
                r5 = r2
            L_0x012e:
                goto L_0x01de
            L_0x0130:
                int r0 = r1.usersStartRow
                if (r3 >= r0) goto L_0x01d6
                org.telegram.ui.FilterUsersActivity r0 = org.telegram.ui.FilterUsersActivity.this
                boolean r0 = r0.isInclude
                r2 = 2
                if (r0 == 0) goto L_0x018f
                r7 = 1
                if (r3 != r7) goto L_0x014f
                r0 = 2131625745(0x7f0e0711, float:1.8878707E38)
                java.lang.String r2 = "FilterContacts"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                java.lang.String r2 = "contacts"
                int r6 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_CONTACTS
                goto L_0x01bd
            L_0x014f:
                if (r3 != r2) goto L_0x015f
                r0 = 2131625775(0x7f0e072f, float:1.8878767E38)
                java.lang.String r2 = "FilterNonContacts"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                java.lang.String r2 = "non_contacts"
                int r6 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS
                goto L_0x01bd
            L_0x015f:
                r0 = 3
                if (r3 != r0) goto L_0x0170
                r0 = 2131625762(0x7f0e0722, float:1.8878741E38)
                java.lang.String r2 = "FilterGroups"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                java.lang.String r2 = "groups"
                int r6 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_GROUPS
                goto L_0x01bd
            L_0x0170:
                r0 = 4
                if (r3 != r0) goto L_0x0181
                r0 = 2131625736(0x7f0e0708, float:1.8878688E38)
                java.lang.String r2 = "FilterChannels"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                java.lang.String r2 = "channels"
                int r6 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_CHANNELS
                goto L_0x01bd
            L_0x0181:
                r0 = 2131625735(0x7f0e0707, float:1.8878686E38)
                java.lang.String r2 = "FilterBots"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                java.lang.String r2 = "bots"
                int r6 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_BOTS
                goto L_0x01bd
            L_0x018f:
                r7 = 1
                if (r3 != r7) goto L_0x01a0
                r0 = 2131625765(0x7f0e0725, float:1.8878747E38)
                java.lang.String r2 = "FilterMuted"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                java.lang.String r2 = "muted"
                int r6 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED
                goto L_0x01bd
            L_0x01a0:
                if (r3 != r2) goto L_0x01b0
                r0 = 2131625776(0x7f0e0730, float:1.887877E38)
                java.lang.String r2 = "FilterRead"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                java.lang.String r2 = "read"
                int r6 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ
                goto L_0x01bd
            L_0x01b0:
                r0 = 2131625732(0x7f0e0704, float:1.887868E38)
                java.lang.String r2 = "FilterArchived"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                java.lang.String r2 = "archived"
                int r6 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED
            L_0x01bd:
                r7 = 0
                r4.setObject(r2, r0, r7)
                org.telegram.ui.FilterUsersActivity r7 = org.telegram.ui.FilterUsersActivity.this
                int r7 = r7.filterFlags
                r7 = r7 & r6
                if (r7 != r6) goto L_0x01cc
                r7 = 1
                goto L_0x01cd
            L_0x01cc:
                r7 = 0
            L_0x01cd:
                r8 = 0
                r4.setChecked(r7, r8)
                r7 = 1
                r4.setCheckBoxEnabled(r7)
                return
            L_0x01d6:
                java.util.ArrayList<org.telegram.tgnet.TLObject> r2 = r1.contacts
                int r0 = r3 - r0
                java.lang.Object r12 = r2.get(r0)
            L_0x01de:
                boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC.User
                if (r0 == 0) goto L_0x01e8
                r0 = r12
                org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
                long r7 = r0.id
                goto L_0x01f5
            L_0x01e8:
                boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC.Chat
                if (r0 == 0) goto L_0x01f3
                r0 = r12
                org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
                long r7 = r0.id
                long r7 = -r7
                goto L_0x01f5
            L_0x01f3:
                r7 = 0
            L_0x01f5:
                boolean r0 = r1.searching
                if (r0 != 0) goto L_0x0233
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                org.telegram.ui.FilterUsersActivity r2 = org.telegram.ui.FilterUsersActivity.this
                org.telegram.messenger.MessagesController r2 = r2.getMessagesController()
                java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r2 = r2.dialogFilters
                r9 = 0
                int r10 = r2.size()
            L_0x020b:
                if (r9 >= r10) goto L_0x0232
                java.lang.Object r11 = r2.get(r9)
                org.telegram.messenger.MessagesController$DialogFilter r11 = (org.telegram.messenger.MessagesController.DialogFilter) r11
                org.telegram.ui.FilterUsersActivity r13 = org.telegram.ui.FilterUsersActivity.this
                org.telegram.messenger.AccountInstance r13 = r13.getAccountInstance()
                boolean r13 = r11.includesDialog(r13, r7)
                if (r13 == 0) goto L_0x022f
                int r13 = r0.length()
                if (r13 <= 0) goto L_0x022a
                java.lang.String r13 = ", "
                r0.append(r13)
            L_0x022a:
                java.lang.String r13 = r11.name
                r0.append(r13)
            L_0x022f:
                int r9 = r9 + 1
                goto L_0x020b
            L_0x0232:
                r5 = r0
            L_0x0233:
                r4.setObject(r12, r6, r5)
                r9 = 0
                int r0 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                if (r0 == 0) goto L_0x0253
                org.telegram.ui.FilterUsersActivity r0 = org.telegram.ui.FilterUsersActivity.this
                androidx.collection.LongSparseArray r0 = r0.selectedContacts
                int r0 = r0.indexOfKey(r7)
                if (r0 < 0) goto L_0x024a
                r0 = 1
                goto L_0x024b
            L_0x024a:
                r0 = 0
            L_0x024b:
                r2 = 0
                r4.setChecked(r0, r2)
                r2 = 1
                r4.setCheckBoxEnabled(r2)
            L_0x0253:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.FilterUsersActivity.GroupCreateAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int position) {
            if (this.searching) {
                return 1;
            }
            if (FilterUsersActivity.this.isInclude) {
                if (position == 0 || position == 6) {
                    return 2;
                }
            } else if (position == 0 || position == 4) {
                return 2;
            }
            return 1;
        }

        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            position[0] = (int) (((float) getItemCount()) * progress);
            position[1] = 0;
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof GroupCreateUserCell) {
                ((GroupCreateUserCell) holder.itemView).recycle();
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 1;
        }

        public void searchDialogs(String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (query == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
                this.searchAdapterHelper.queryServerSearch((String) null, true, true, false, false, false, 0, false, 0, 0);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda2 filterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda2 = new FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda2(this, query);
            this.searchRunnable = filterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda2;
            dispatchQueue.postRunnable(filterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda2, 300);
        }

        /* renamed from: lambda$searchDialogs$3$org-telegram-ui-FilterUsersActivity$GroupCreateAdapter  reason: not valid java name */
        public /* synthetic */ void m2167x107f0f1e(String query) {
            AndroidUtilities.runOnUIThread(new FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda1(this, query));
        }

        /* renamed from: lambda$searchDialogs$2$org-telegram-ui-FilterUsersActivity$GroupCreateAdapter  reason: not valid java name */
        public /* synthetic */ void m2166xvar_bc3f(String query) {
            this.searchAdapterHelper.queryServerSearch(query, true, true, true, true, false, 0, false, 0, 0);
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda0 filterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda0 = new FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda0(this, query);
            this.searchRunnable = filterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda0;
            dispatchQueue.postRunnable(filterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda0);
        }

        /* renamed from: lambda$searchDialogs$1$org-telegram-ui-FilterUsersActivity$GroupCreateAdapter  reason: not valid java name */
        public /* synthetic */ void m2165xe126960(String query) {
            String username;
            String search1;
            String search2;
            String search12 = query.trim().toLowerCase();
            if (search12.length() == 0) {
                updateSearchResults(new ArrayList(), new ArrayList());
                return;
            }
            String search22 = LocaleController.getInstance().getTranslitString(search12);
            if (search12.equals(search22) || search22.length() == 0) {
                search22 = null;
            }
            char c = 0;
            char c2 = 1;
            String[] search = new String[((search22 != null ? 1 : 0) + 1)];
            search[0] = search12;
            if (search22 != null) {
                search[1] = search22;
            }
            ArrayList<Object> resultArray = new ArrayList<>();
            ArrayList<CharSequence> resultArrayNames = new ArrayList<>();
            int a = 0;
            while (a < this.contacts.size()) {
                TLObject object = this.contacts.get(a);
                String[] names = new String[3];
                if (object instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) object;
                    names[c] = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                    username = user.username;
                    if (UserObject.isReplyUser(user)) {
                        names[2] = LocaleController.getString("RepliesTitle", NUM).toLowerCase();
                    } else if (user.self) {
                        names[2] = LocaleController.getString("SavedMessages", NUM).toLowerCase();
                    }
                } else {
                    TLRPC.Chat chat = (TLRPC.Chat) object;
                    names[c] = chat.title.toLowerCase();
                    username = chat.username;
                }
                names[c2] = LocaleController.getInstance().getTranslitString(names[c]);
                if (names[c].equals(names[c2])) {
                    names[c2] = null;
                }
                int found = 0;
                int length = search.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        search1 = search12;
                        search2 = search22;
                        break;
                    }
                    String q = search[i];
                    int i2 = 0;
                    while (true) {
                        if (i2 >= names.length) {
                            search1 = search12;
                            search2 = search22;
                            break;
                        }
                        String name = names[i2];
                        if (name != null) {
                            if (name.startsWith(q)) {
                                search1 = search12;
                                search2 = search22;
                                break;
                            }
                            search1 = search12;
                            StringBuilder sb = new StringBuilder();
                            search2 = search22;
                            sb.append(" ");
                            sb.append(q);
                            if (name.contains(sb.toString())) {
                                break;
                            }
                        } else {
                            search1 = search12;
                            search2 = search22;
                        }
                        i2++;
                        search12 = search1;
                        search22 = search2;
                    }
                    found = 1;
                    if (found == 0 && username != null && username.toLowerCase().startsWith(q)) {
                        found = 2;
                    }
                    if (found != 0) {
                        if (found != 1) {
                            resultArrayNames.add(AndroidUtilities.generateSearchName("@" + username, (String) null, "@" + q));
                        } else if (object instanceof TLRPC.User) {
                            TLRPC.User user2 = (TLRPC.User) object;
                            resultArrayNames.add(AndroidUtilities.generateSearchName(user2.first_name, user2.last_name, q));
                        } else {
                            resultArrayNames.add(AndroidUtilities.generateSearchName(((TLRPC.Chat) object).title, (String) null, q));
                        }
                        resultArray.add(object);
                    } else {
                        i++;
                        search12 = search1;
                        search22 = search2;
                    }
                }
                a++;
                search12 = search1;
                search22 = search2;
                c = 0;
                c2 = 1;
            }
            updateSearchResults(resultArray, resultArrayNames);
        }

        private void updateSearchResults(ArrayList<Object> users, ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda3(this, users, names));
        }

        /* renamed from: lambda$updateSearchResults$4$org-telegram-ui-FilterUsersActivity$GroupCreateAdapter  reason: not valid java name */
        public /* synthetic */ void m2168xCLASSNAMEfb(ArrayList users, ArrayList names) {
            if (this.searching) {
                this.searchRunnable = null;
                this.searchResult = users;
                this.searchResultNames = names;
                this.searchAdapterHelper.mergeResults(users);
                if (this.searching && !this.searchAdapterHelper.isSearchInProgress()) {
                    FilterUsersActivity.this.emptyView.showTextView();
                }
                notifyDataSetChanged();
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new FilterUsersActivity$$ExternalSyntheticLambda2(this);
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollActive"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollInactive"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_hintText"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_cursor"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_sectionText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxDisabled"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "avatar_backgroundRed"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "avatar_backgroundOrange"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "avatar_backgroundViolet"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundGreen"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundPink"));
        themeDescriptions.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_spanBackground"));
        themeDescriptions.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_spanText"));
        themeDescriptions.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_spanDelete"));
        themeDescriptions.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundBlue"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$3$org-telegram-ui-FilterUsersActivity  reason: not valid java name */
    public /* synthetic */ void m2163xCLASSNAMEb04bb() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) child).update(0);
                }
            }
        }
    }
}
