package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.GroupCreateSectionCell;
import org.telegram.ui.Cells.InviteTextCell;
import org.telegram.ui.Cells.InviteUserCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.GroupCreateDividerItemDecoration;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class InviteContactsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, View.OnClickListener {
    /* access modifiers changed from: private */
    public InviteAdapter adapter;
    /* access modifiers changed from: private */
    public ArrayList<GroupCreateSpan> allSpans = new ArrayList<>();
    /* access modifiers changed from: private */
    public int containerHeight;
    private TextView counterTextView;
    /* access modifiers changed from: private */
    public FrameLayout counterView;
    /* access modifiers changed from: private */
    public GroupCreateSpan currentDeletingSpan;
    /* access modifiers changed from: private */
    public GroupCreateDividerItemDecoration decoration;
    /* access modifiers changed from: private */
    public EditTextBoldCursor editText;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public int fieldY;
    /* access modifiers changed from: private */
    public boolean ignoreScrollEvent;
    /* access modifiers changed from: private */
    public TextView infoTextView;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ArrayList<ContactsController.Contact> phoneBookContacts;
    /* access modifiers changed from: private */
    public ScrollView scrollView;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public HashMap<String, GroupCreateSpan> selectedContacts = new HashMap<>();
    /* access modifiers changed from: private */
    public SpansContainer spansContainer;
    private TextView textView;

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
            boolean z;
            int count = getChildCount();
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            float f = 32.0f;
            int maxWidth = width - AndroidUtilities.dp(32.0f);
            int currentLineWidth = 0;
            int y = AndroidUtilities.dp(12.0f);
            int allCurrentLineWidth = 0;
            int allY = AndroidUtilities.dp(12.0f);
            int a = 0;
            while (a < count) {
                View child = getChildAt(a);
                if (child instanceof GroupCreateSpan) {
                    child.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(f), NUM));
                    if (child != this.removingSpan && child.getMeasuredWidth() + currentLineWidth > maxWidth) {
                        y += child.getMeasuredHeight() + AndroidUtilities.dp(12.0f);
                        currentLineWidth = 0;
                    }
                    if (child.getMeasuredWidth() + allCurrentLineWidth > maxWidth) {
                        allY += child.getMeasuredHeight() + AndroidUtilities.dp(12.0f);
                        allCurrentLineWidth = 0;
                    }
                    int x = AndroidUtilities.dp(16.0f) + currentLineWidth;
                    if (!this.animationStarted) {
                        View view = this.removingSpan;
                        if (child == view) {
                            child.setTranslationX((float) (AndroidUtilities.dp(16.0f) + allCurrentLineWidth));
                            child.setTranslationY((float) allY);
                        } else if (view != null) {
                            if (child.getTranslationX() != ((float) x)) {
                                this.animators.add(ObjectAnimator.ofFloat(child, "translationX", new float[]{(float) x}));
                            }
                            if (child.getTranslationY() != ((float) y)) {
                                this.animators.add(ObjectAnimator.ofFloat(child, "translationY", new float[]{(float) y}));
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
                a++;
                f = 32.0f;
            }
            if (AndroidUtilities.isTablet()) {
                minWidth = AndroidUtilities.dp(366.0f) / 3;
            } else {
                minWidth = (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(164.0f)) / 3;
            }
            if (maxWidth - currentLineWidth < minWidth) {
                currentLineWidth = 0;
                y += AndroidUtilities.dp(44.0f);
            }
            if (maxWidth - allCurrentLineWidth < minWidth) {
                allY += AndroidUtilities.dp(44.0f);
            }
            InviteContactsActivity.this.editText.measure(View.MeasureSpec.makeMeasureSpec(maxWidth - currentLineWidth, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
            if (!this.animationStarted) {
                int currentHeight = AndroidUtilities.dp(44.0f) + allY;
                int fieldX = AndroidUtilities.dp(16.0f) + currentLineWidth;
                int unused = InviteContactsActivity.this.fieldY = y;
                if (this.currentAnimation != null) {
                    int resultHeight = AndroidUtilities.dp(44.0f) + y;
                    if (InviteContactsActivity.this.containerHeight != resultHeight) {
                        int i = count;
                        int i2 = minWidth;
                        this.animators.add(ObjectAnimator.ofInt(InviteContactsActivity.this, "containerHeight", new int[]{resultHeight}));
                    } else {
                        int i3 = minWidth;
                    }
                    if (InviteContactsActivity.this.editText.getTranslationX() != ((float) fieldX)) {
                        this.animators.add(ObjectAnimator.ofFloat(InviteContactsActivity.this.editText, "translationX", new float[]{(float) fieldX}));
                    }
                    if (InviteContactsActivity.this.editText.getTranslationY() != ((float) InviteContactsActivity.this.fieldY)) {
                        z = false;
                        this.animators.add(ObjectAnimator.ofFloat(InviteContactsActivity.this.editText, "translationY", new float[]{(float) InviteContactsActivity.this.fieldY}));
                    } else {
                        z = false;
                    }
                    InviteContactsActivity.this.editText.setAllowDrawCursor(z);
                    this.currentAnimation.playTogether(this.animators);
                    this.currentAnimation.start();
                    this.animationStarted = true;
                } else {
                    int i4 = minWidth;
                    int unused2 = InviteContactsActivity.this.containerHeight = currentHeight;
                    InviteContactsActivity.this.editText.setTranslationX((float) fieldX);
                    InviteContactsActivity.this.editText.setTranslationY((float) InviteContactsActivity.this.fieldY);
                }
            } else {
                int i5 = minWidth;
                if (this.currentAnimation != null && !InviteContactsActivity.this.ignoreScrollEvent && this.removingSpan == null) {
                    InviteContactsActivity.this.editText.bringPointIntoView(InviteContactsActivity.this.editText.getSelectionStart());
                }
            }
            setMeasuredDimension(width, InviteContactsActivity.this.containerHeight);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int count = getChildCount();
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan span) {
            InviteContactsActivity.this.allSpans.add(span);
            InviteContactsActivity.this.selectedContacts.put(span.getKey(), span);
            InviteContactsActivity.this.editText.setHintVisible(false);
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
                    View unused = SpansContainer.this.addingSpan = null;
                    AnimatorSet unused2 = SpansContainer.this.currentAnimation = null;
                    boolean unused3 = SpansContainer.this.animationStarted = false;
                    InviteContactsActivity.this.editText.setAllowDrawCursor(true);
                }
            });
            this.currentAnimation.setDuration(150);
            this.addingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleX", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleY", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "alpha", new float[]{0.0f, 1.0f}));
            addView(span);
        }

        public void removeSpan(final GroupCreateSpan span) {
            boolean unused = InviteContactsActivity.this.ignoreScrollEvent = true;
            InviteContactsActivity.this.selectedContacts.remove(span.getKey());
            InviteContactsActivity.this.allSpans.remove(span);
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
                    InviteContactsActivity.this.editText.setAllowDrawCursor(true);
                    if (InviteContactsActivity.this.allSpans.isEmpty()) {
                        InviteContactsActivity.this.editText.setHintVisible(true);
                    }
                }
            });
            this.currentAnimation.setDuration(150);
            this.removingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleX", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleY", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "alpha", new float[]{1.0f, 0.0f}));
            requestLayout();
        }
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsImported);
        fetchContacts();
        if (!UserConfig.getInstance(this.currentAccount).contactsReimported) {
            ContactsController.getInstance(this.currentAccount).forceImportContacts();
            UserConfig.getInstance(this.currentAccount).contactsReimported = true;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsImported);
    }

    public void onClick(View v) {
        GroupCreateSpan span = (GroupCreateSpan) v;
        if (span.isDeleting()) {
            this.currentDeletingSpan = null;
            this.spansContainer.removeSpan(span);
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
        Context context2 = context;
        this.searching = false;
        this.searchWas = false;
        this.allSpans.clear();
        this.selectedContacts.clear();
        this.currentDeletingSpan = null;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("InviteFriends", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    InviteContactsActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new ViewGroup(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int maxSize;
                int h;
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(width, height);
                if (AndroidUtilities.isTablet() || height > width) {
                    maxSize = AndroidUtilities.dp(144.0f);
                } else {
                    maxSize = AndroidUtilities.dp(56.0f);
                }
                InviteContactsActivity.this.infoTextView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(maxSize, Integer.MIN_VALUE));
                InviteContactsActivity.this.counterView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
                if (InviteContactsActivity.this.infoTextView.getVisibility() == 0) {
                    h = InviteContactsActivity.this.infoTextView.getMeasuredHeight();
                } else {
                    h = InviteContactsActivity.this.counterView.getMeasuredHeight();
                }
                InviteContactsActivity.this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(maxSize, Integer.MIN_VALUE));
                InviteContactsActivity.this.listView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec((height - InviteContactsActivity.this.scrollView.getMeasuredHeight()) - h, NUM));
                InviteContactsActivity.this.emptyView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec((height - InviteContactsActivity.this.scrollView.getMeasuredHeight()) - AndroidUtilities.dp(72.0f), NUM));
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                InviteContactsActivity.this.scrollView.layout(0, 0, InviteContactsActivity.this.scrollView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight());
                InviteContactsActivity.this.listView.layout(0, InviteContactsActivity.this.scrollView.getMeasuredHeight(), InviteContactsActivity.this.listView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight() + InviteContactsActivity.this.listView.getMeasuredHeight());
                InviteContactsActivity.this.emptyView.layout(0, InviteContactsActivity.this.scrollView.getMeasuredHeight() + AndroidUtilities.dp(72.0f), InviteContactsActivity.this.emptyView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight() + InviteContactsActivity.this.emptyView.getMeasuredHeight());
                int y = (bottom - top) - InviteContactsActivity.this.infoTextView.getMeasuredHeight();
                InviteContactsActivity.this.infoTextView.layout(0, y, InviteContactsActivity.this.infoTextView.getMeasuredWidth(), InviteContactsActivity.this.infoTextView.getMeasuredHeight() + y);
                int y2 = (bottom - top) - InviteContactsActivity.this.counterView.getMeasuredHeight();
                InviteContactsActivity.this.counterView.layout(0, y2, InviteContactsActivity.this.counterView.getMeasuredWidth(), InviteContactsActivity.this.counterView.getMeasuredHeight() + y2);
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == InviteContactsActivity.this.listView || child == InviteContactsActivity.this.emptyView) {
                    InviteContactsActivity.this.parentLayout.drawHeaderShadow(canvas, InviteContactsActivity.this.scrollView.getMeasuredHeight());
                }
                return result;
            }
        };
        ViewGroup frameLayout = (ViewGroup) this.fragmentView;
        AnonymousClass3 r6 = new ScrollView(context2) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                if (InviteContactsActivity.this.ignoreScrollEvent) {
                    boolean unused = InviteContactsActivity.this.ignoreScrollEvent = false;
                    return false;
                }
                rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
                rectangle.top += InviteContactsActivity.this.fieldY + AndroidUtilities.dp(20.0f);
                rectangle.bottom += InviteContactsActivity.this.fieldY + AndroidUtilities.dp(50.0f);
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
        AnonymousClass4 r62 = new EditTextBoldCursor(context2) {
            public boolean onTouchEvent(MotionEvent event) {
                if (InviteContactsActivity.this.currentDeletingSpan != null) {
                    InviteContactsActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    GroupCreateSpan unused = InviteContactsActivity.this.currentDeletingSpan = null;
                }
                if (event.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(event);
            }
        };
        this.editText = r62;
        r62.setTextSize(1, 18.0f);
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
        this.editText.setHintText(LocaleController.getString("SearchFriends", NUM));
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
                boolean z = true;
                if (event.getAction() == 0) {
                    if (InviteContactsActivity.this.editText.length() != 0) {
                        z = false;
                    }
                    this.wasEmpty = z;
                } else if (event.getAction() == 1 && this.wasEmpty && !InviteContactsActivity.this.allSpans.isEmpty()) {
                    InviteContactsActivity.this.spansContainer.removeSpan((GroupCreateSpan) InviteContactsActivity.this.allSpans.get(InviteContactsActivity.this.allSpans.size() - 1));
                    InviteContactsActivity.this.updateHint();
                    InviteContactsActivity.this.checkVisibleRows();
                    return true;
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
                if (InviteContactsActivity.this.editText.length() != 0) {
                    boolean unused = InviteContactsActivity.this.searching = true;
                    boolean unused2 = InviteContactsActivity.this.searchWas = true;
                    InviteContactsActivity.this.adapter.setSearching(true);
                    InviteContactsActivity.this.adapter.searchDialogs(InviteContactsActivity.this.editText.getText().toString());
                    InviteContactsActivity.this.listView.setFastScrollVisible(false);
                    InviteContactsActivity.this.listView.setVerticalScrollBarEnabled(true);
                    InviteContactsActivity.this.emptyView.setText(LocaleController.getString("NoResult", NUM));
                    return;
                }
                InviteContactsActivity.this.closeSearch();
            }
        });
        this.emptyView = new EmptyTextProgressView(context2);
        if (ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
        frameLayout.addView(this.emptyView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        InviteAdapter inviteAdapter = new InviteAdapter(context2);
        this.adapter = inviteAdapter;
        recyclerListView2.setAdapter(inviteAdapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        RecyclerListView recyclerListView3 = this.listView;
        GroupCreateDividerItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        this.decoration = groupCreateDividerItemDecoration;
        recyclerListView3.addItemDecoration(groupCreateDividerItemDecoration);
        frameLayout.addView(this.listView);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new InviteContactsActivity$$ExternalSyntheticLambda3(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(InviteContactsActivity.this.editText);
                }
            }
        });
        TextView textView2 = new TextView(context2);
        this.infoTextView = textView2;
        textView2.setBackgroundColor(Theme.getColor("contacts_inviteBackground"));
        this.infoTextView.setTextColor(Theme.getColor("contacts_inviteText"));
        this.infoTextView.setGravity(17);
        this.infoTextView.setText(LocaleController.getString("InviteFriendsHelp", NUM));
        this.infoTextView.setTextSize(1, 13.0f);
        this.infoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.infoTextView.setPadding(AndroidUtilities.dp(17.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(17.0f), AndroidUtilities.dp(9.0f));
        frameLayout.addView(this.infoTextView, LayoutHelper.createFrame(-1, -2, 83));
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.counterView = frameLayout2;
        frameLayout2.setBackgroundColor(Theme.getColor("contacts_inviteBackground"));
        this.counterView.setVisibility(4);
        frameLayout.addView(this.counterView, LayoutHelper.createFrame(-1, 48, 83));
        this.counterView.setOnClickListener(new InviteContactsActivity$$ExternalSyntheticLambda0(this));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        this.counterView.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 17));
        TextView textView3 = new TextView(context2);
        this.counterTextView = textView3;
        textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.counterTextView.setTextSize(1, 14.0f);
        this.counterTextView.setTextColor(Theme.getColor("contacts_inviteBackground"));
        this.counterTextView.setGravity(17);
        this.counterTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(10.0f), Theme.getColor("contacts_inviteText")));
        this.counterTextView.setMinWidth(AndroidUtilities.dp(20.0f));
        this.counterTextView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(1.0f));
        linearLayout.addView(this.counterTextView, LayoutHelper.createLinear(-2, 20, 16, 0, 0, 10, 0));
        TextView textView4 = new TextView(context2);
        this.textView = textView4;
        textView4.setTextSize(1, 14.0f);
        this.textView.setTextColor(Theme.getColor("contacts_inviteText"));
        this.textView.setGravity(17);
        this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.textView.setText(LocaleController.getString("InviteToTelegram", NUM).toUpperCase());
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        linearLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2, 16));
        updateHint();
        this.adapter.notifyDataSetChanged();
        return this.fragmentView;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x003a, code lost:
        r1 = (org.telegram.ui.Cells.InviteUserCell) r7;
     */
    /* renamed from: lambda$createView$0$org-telegram-ui-InviteContactsActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3610lambda$createView$0$orgtelegramuiInviteContactsActivity(android.view.View r7, int r8) {
        /*
            r6 = this;
            r0 = 0
            if (r8 != 0) goto L_0x0035
            boolean r1 = r6.searching
            if (r1 != 0) goto L_0x0035
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0030 }
            java.lang.String r2 = "android.intent.action.SEND"
            r1.<init>(r2)     // Catch:{ Exception -> 0x0030 }
            java.lang.String r2 = "text/plain"
            r1.setType(r2)     // Catch:{ Exception -> 0x0030 }
            int r2 = r6.currentAccount     // Catch:{ Exception -> 0x0030 }
            org.telegram.messenger.ContactsController r2 = org.telegram.messenger.ContactsController.getInstance(r2)     // Catch:{ Exception -> 0x0030 }
            java.lang.String r0 = r2.getInviteText(r0)     // Catch:{ Exception -> 0x0030 }
            java.lang.String r2 = "android.intent.extra.TEXT"
            r1.putExtra(r2, r0)     // Catch:{ Exception -> 0x0030 }
            android.app.Activity r2 = r6.getParentActivity()     // Catch:{ Exception -> 0x0030 }
            android.content.Intent r3 = android.content.Intent.createChooser(r1, r0)     // Catch:{ Exception -> 0x0030 }
            r4 = 500(0x1f4, float:7.0E-43)
            r2.startActivityForResult(r3, r4)     // Catch:{ Exception -> 0x0030 }
            goto L_0x0034
        L_0x0030:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0034:
            return
        L_0x0035:
            boolean r1 = r7 instanceof org.telegram.ui.Cells.InviteUserCell
            if (r1 != 0) goto L_0x003a
            return
        L_0x003a:
            r1 = r7
            org.telegram.ui.Cells.InviteUserCell r1 = (org.telegram.ui.Cells.InviteUserCell) r1
            org.telegram.messenger.ContactsController$Contact r2 = r1.getContact()
            if (r2 != 0) goto L_0x0044
            return
        L_0x0044:
            java.util.HashMap<java.lang.String, org.telegram.ui.Components.GroupCreateSpan> r3 = r6.selectedContacts
            java.lang.String r4 = r2.key
            boolean r3 = r3.containsKey(r4)
            r4 = r3
            if (r3 == 0) goto L_0x005f
            java.util.HashMap<java.lang.String, org.telegram.ui.Components.GroupCreateSpan> r3 = r6.selectedContacts
            java.lang.String r5 = r2.key
            java.lang.Object r3 = r3.get(r5)
            org.telegram.ui.Components.GroupCreateSpan r3 = (org.telegram.ui.Components.GroupCreateSpan) r3
            org.telegram.ui.InviteContactsActivity$SpansContainer r5 = r6.spansContainer
            r5.removeSpan(r3)
            goto L_0x0072
        L_0x005f:
            org.telegram.ui.Components.GroupCreateSpan r3 = new org.telegram.ui.Components.GroupCreateSpan
            org.telegram.ui.Components.EditTextBoldCursor r5 = r6.editText
            android.content.Context r5 = r5.getContext()
            r3.<init>((android.content.Context) r5, (org.telegram.messenger.ContactsController.Contact) r2)
            org.telegram.ui.InviteContactsActivity$SpansContainer r5 = r6.spansContainer
            r5.addSpan(r3)
            r3.setOnClickListener(r6)
        L_0x0072:
            r6.updateHint()
            boolean r3 = r6.searching
            if (r3 != 0) goto L_0x0086
            boolean r3 = r6.searchWas
            if (r3 == 0) goto L_0x007e
            goto L_0x0086
        L_0x007e:
            r3 = 1
            if (r4 != 0) goto L_0x0082
            r0 = 1
        L_0x0082:
            r1.setChecked(r0, r3)
            goto L_0x008b
        L_0x0086:
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.editText
            org.telegram.messenger.AndroidUtilities.showKeyboard(r0)
        L_0x008b:
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.editText
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0099
            org.telegram.ui.Components.EditTextBoldCursor r0 = r6.editText
            r3 = 0
            r0.setText(r3)
        L_0x0099:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.InviteContactsActivity.m3610lambda$createView$0$orgtelegramuiInviteContactsActivity(android.view.View, int):void");
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-InviteContactsActivity  reason: not valid java name */
    public /* synthetic */ void m3611lambda$createView$1$orgtelegramuiInviteContactsActivity(View v) {
        try {
            StringBuilder builder = new StringBuilder();
            int num = 0;
            for (int a = 0; a < this.allSpans.size(); a++) {
                ContactsController.Contact contact = this.allSpans.get(a).getContact();
                if (builder.length() != 0) {
                    builder.append(';');
                }
                builder.append(contact.phones.get(0));
                if (a == 0 && this.allSpans.size() == 1) {
                    num = contact.imported;
                }
            }
            Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse("smsto:" + builder.toString()));
            intent.putExtra("sms_body", ContactsController.getInstance(this.currentAccount).getInviteText(num));
            getParentActivity().startActivityForResult(intent, 500);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        finishFragment();
    }

    public void onResume() {
        super.onResume();
        EditTextBoldCursor editTextBoldCursor = this.editText;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.contactsImported) {
            fetchContacts();
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
    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0013, code lost:
        r3 = (org.telegram.ui.Cells.InviteUserCell) r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void checkVisibleRows() {
        /*
            r7 = this;
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            int r0 = r0.getChildCount()
            r1 = 0
        L_0x0007:
            if (r1 >= r0) goto L_0x002b
            org.telegram.ui.Components.RecyclerListView r2 = r7.listView
            android.view.View r2 = r2.getChildAt(r1)
            boolean r3 = r2 instanceof org.telegram.ui.Cells.InviteUserCell
            if (r3 == 0) goto L_0x0028
            r3 = r2
            org.telegram.ui.Cells.InviteUserCell r3 = (org.telegram.ui.Cells.InviteUserCell) r3
            org.telegram.messenger.ContactsController$Contact r4 = r3.getContact()
            if (r4 == 0) goto L_0x0028
            java.util.HashMap<java.lang.String, org.telegram.ui.Components.GroupCreateSpan> r5 = r7.selectedContacts
            java.lang.String r6 = r4.key
            boolean r5 = r5.containsKey(r6)
            r6 = 1
            r3.setChecked(r5, r6)
        L_0x0028:
            int r1 = r1 + 1
            goto L_0x0007
        L_0x002b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.InviteContactsActivity.checkVisibleRows():void");
    }

    /* access modifiers changed from: private */
    public void updateHint() {
        if (this.selectedContacts.isEmpty()) {
            this.infoTextView.setVisibility(0);
            this.counterView.setVisibility(4);
            return;
        }
        this.infoTextView.setVisibility(4);
        this.counterView.setVisibility(0);
        this.counterTextView.setText(String.format("%d", new Object[]{Integer.valueOf(this.selectedContacts.size())}));
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

    private void fetchContacts() {
        ArrayList<ContactsController.Contact> arrayList = new ArrayList<>(ContactsController.getInstance(this.currentAccount).phoneBookContacts);
        this.phoneBookContacts = arrayList;
        Collections.sort(arrayList, InviteContactsActivity$$ExternalSyntheticLambda1.INSTANCE);
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (emptyTextProgressView != null) {
            emptyTextProgressView.showTextView();
        }
        InviteAdapter inviteAdapter = this.adapter;
        if (inviteAdapter != null) {
            inviteAdapter.notifyDataSetChanged();
        }
    }

    static /* synthetic */ int lambda$fetchContacts$2(ContactsController.Contact o1, ContactsController.Contact o2) {
        if (o1.imported > o2.imported) {
            return -1;
        }
        if (o1.imported < o2.imported) {
            return 1;
        }
        return 0;
    }

    public class InviteAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private ArrayList<ContactsController.Contact> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        /* access modifiers changed from: private */
        public Timer searchTimer;
        private boolean searching;

        public InviteAdapter(Context ctx) {
            this.context = ctx;
        }

        public void setSearching(boolean value) {
            if (this.searching != value) {
                this.searching = value;
                notifyDataSetChanged();
            }
        }

        public int getItemCount() {
            if (this.searching) {
                return this.searchResult.size();
            }
            return InviteContactsActivity.this.phoneBookContacts.size() + 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 1:
                    view = new InviteTextCell(this.context);
                    ((InviteTextCell) view).setTextAndIcon(LocaleController.getString("ShareTelegram", NUM), NUM);
                    break;
                default:
                    view = new InviteUserCell(this.context, true);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CharSequence name;
            ContactsController.Contact contact;
            switch (holder.getItemViewType()) {
                case 0:
                    InviteUserCell cell = (InviteUserCell) holder.itemView;
                    if (this.searching) {
                        contact = this.searchResult.get(position);
                        name = this.searchResultNames.get(position);
                    } else {
                        contact = (ContactsController.Contact) InviteContactsActivity.this.phoneBookContacts.get(position - 1);
                        name = null;
                    }
                    cell.setUser(contact, name);
                    cell.setChecked(InviteContactsActivity.this.selectedContacts.containsKey(contact.key), false);
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (this.searching || position != 0) {
                return 0;
            }
            return 1;
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof InviteUserCell) {
                ((InviteUserCell) holder.itemView).recycle();
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public void searchDialogs(final String query) {
            try {
                Timer timer = this.searchTimer;
                if (timer != null) {
                    timer.cancel();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (query == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
                return;
            }
            Timer timer2 = new Timer();
            this.searchTimer = timer2;
            timer2.schedule(new TimerTask() {
                public void run() {
                    try {
                        InviteAdapter.this.searchTimer.cancel();
                        Timer unused = InviteAdapter.this.searchTimer = null;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    AndroidUtilities.runOnUIThread(new InviteContactsActivity$InviteAdapter$1$$ExternalSyntheticLambda1(this, query));
                }

                /* renamed from: lambda$run$1$org-telegram-ui-InviteContactsActivity$InviteAdapter$1  reason: not valid java name */
                public /* synthetic */ void m3615x7a8a28b9(String query) {
                    Utilities.searchQueue.postRunnable(new InviteContactsActivity$InviteAdapter$1$$ExternalSyntheticLambda0(this, query));
                }

                /* JADX WARNING: Code restructure failed: missing block: B:32:0x00c6, code lost:
                    if (r10.contains(" " + r14) != false) goto L_0x00c8;
                 */
                /* JADX WARNING: Removed duplicated region for block: B:36:0x00db A[LOOP:1: B:23:0x008a->B:36:0x00db, LOOP_END] */
                /* JADX WARNING: Removed duplicated region for block: B:43:0x00cc A[SYNTHETIC] */
                /* renamed from: lambda$run$0$org-telegram-ui-InviteContactsActivity$InviteAdapter$1  reason: not valid java name */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public /* synthetic */ void m3614x7953d5da(java.lang.String r17) {
                    /*
                        r16 = this;
                        r0 = r16
                        java.lang.String r1 = r17.trim()
                        java.lang.String r1 = r1.toLowerCase()
                        int r2 = r1.length()
                        if (r2 != 0) goto L_0x0020
                        org.telegram.ui.InviteContactsActivity$InviteAdapter r2 = org.telegram.ui.InviteContactsActivity.InviteAdapter.this
                        java.util.ArrayList r3 = new java.util.ArrayList
                        r3.<init>()
                        java.util.ArrayList r4 = new java.util.ArrayList
                        r4.<init>()
                        r2.updateSearchResults(r3, r4)
                        return
                    L_0x0020:
                        org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
                        java.lang.String r2 = r2.getTranslitString(r1)
                        boolean r3 = r1.equals(r2)
                        if (r3 != 0) goto L_0x0034
                        int r3 = r2.length()
                        if (r3 != 0) goto L_0x0035
                    L_0x0034:
                        r2 = 0
                    L_0x0035:
                        r3 = 0
                        r4 = 1
                        if (r2 == 0) goto L_0x003b
                        r5 = 1
                        goto L_0x003c
                    L_0x003b:
                        r5 = 0
                    L_0x003c:
                        int r5 = r5 + r4
                        java.lang.String[] r5 = new java.lang.String[r5]
                        r5[r3] = r1
                        if (r2 == 0) goto L_0x0045
                        r5[r4] = r2
                    L_0x0045:
                        java.util.ArrayList r4 = new java.util.ArrayList
                        r4.<init>()
                        java.util.ArrayList r6 = new java.util.ArrayList
                        r6.<init>()
                        r7 = 0
                    L_0x0050:
                        org.telegram.ui.InviteContactsActivity$InviteAdapter r8 = org.telegram.ui.InviteContactsActivity.InviteAdapter.this
                        org.telegram.ui.InviteContactsActivity r8 = org.telegram.ui.InviteContactsActivity.this
                        java.util.ArrayList r8 = r8.phoneBookContacts
                        int r8 = r8.size()
                        if (r7 >= r8) goto L_0x00e4
                        org.telegram.ui.InviteContactsActivity$InviteAdapter r8 = org.telegram.ui.InviteContactsActivity.InviteAdapter.this
                        org.telegram.ui.InviteContactsActivity r8 = org.telegram.ui.InviteContactsActivity.this
                        java.util.ArrayList r8 = r8.phoneBookContacts
                        java.lang.Object r8 = r8.get(r7)
                        org.telegram.messenger.ContactsController$Contact r8 = (org.telegram.messenger.ContactsController.Contact) r8
                        java.lang.String r9 = r8.first_name
                        java.lang.String r10 = r8.last_name
                        java.lang.String r9 = org.telegram.messenger.ContactsController.formatName(r9, r10)
                        java.lang.String r9 = r9.toLowerCase()
                        org.telegram.messenger.LocaleController r10 = org.telegram.messenger.LocaleController.getInstance()
                        java.lang.String r10 = r10.getTranslitString(r9)
                        boolean r11 = r9.equals(r10)
                        if (r11 == 0) goto L_0x0087
                        r10 = 0
                    L_0x0087:
                        r11 = 0
                        int r12 = r5.length
                        r13 = 0
                    L_0x008a:
                        if (r13 >= r12) goto L_0x00df
                        r14 = r5[r13]
                        boolean r15 = r9.startsWith(r14)
                        if (r15 != 0) goto L_0x00c8
                        java.lang.StringBuilder r15 = new java.lang.StringBuilder
                        r15.<init>()
                        java.lang.String r3 = " "
                        r15.append(r3)
                        r15.append(r14)
                        java.lang.String r15 = r15.toString()
                        boolean r15 = r9.contains(r15)
                        if (r15 != 0) goto L_0x00c8
                        if (r10 == 0) goto L_0x00ca
                        boolean r15 = r10.startsWith(r14)
                        if (r15 != 0) goto L_0x00c8
                        java.lang.StringBuilder r15 = new java.lang.StringBuilder
                        r15.<init>()
                        r15.append(r3)
                        r15.append(r14)
                        java.lang.String r3 = r15.toString()
                        boolean r3 = r10.contains(r3)
                        if (r3 == 0) goto L_0x00ca
                    L_0x00c8:
                        r3 = 1
                        r11 = r3
                    L_0x00ca:
                        if (r11 == 0) goto L_0x00db
                        java.lang.String r3 = r8.first_name
                        java.lang.String r12 = r8.last_name
                        java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r12, r14)
                        r6.add(r3)
                        r4.add(r8)
                        goto L_0x00df
                    L_0x00db:
                        int r13 = r13 + 1
                        r3 = 0
                        goto L_0x008a
                    L_0x00df:
                        int r7 = r7 + 1
                        r3 = 0
                        goto L_0x0050
                    L_0x00e4:
                        org.telegram.ui.InviteContactsActivity$InviteAdapter r3 = org.telegram.ui.InviteContactsActivity.InviteAdapter.this
                        r3.updateSearchResults(r4, r6)
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.InviteContactsActivity.InviteAdapter.AnonymousClass1.m3614x7953d5da(java.lang.String):void");
                }
            }, 200, 300);
        }

        /* access modifiers changed from: private */
        public void updateSearchResults(ArrayList<ContactsController.Contact> users, ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new InviteContactsActivity$InviteAdapter$$ExternalSyntheticLambda0(this, users, names));
        }

        /* renamed from: lambda$updateSearchResults$0$org-telegram-ui-InviteContactsActivity$InviteAdapter  reason: not valid java name */
        public /* synthetic */ void m3613xCLASSNAMEafcf3(ArrayList users, ArrayList names) {
            if (this.searching) {
                this.searchResult = users;
                this.searchResultNames = names;
                notifyDataSetChanged();
            }
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            int count = getItemCount();
            boolean z = false;
            InviteContactsActivity.this.emptyView.setVisibility(count == 1 ? 0 : 4);
            GroupCreateDividerItemDecoration access$2700 = InviteContactsActivity.this.decoration;
            if (count == 1) {
                z = true;
            }
            access$2700.setSingle(z);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new InviteContactsActivity$$ExternalSyntheticLambda2(this);
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
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GroupCreateSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{GroupCreateSectionCell.class}, new String[]{"drawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_sectionShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_sectionText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_sectionText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{InviteUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{InviteUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{InviteUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "avatar_backgroundRed"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "avatar_backgroundOrange"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundPink"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{InviteTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{InviteTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_spanBackground"));
        themeDescriptions.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_spanText"));
        themeDescriptions.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_spanDelete"));
        themeDescriptions.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription(this.infoTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contacts_inviteText"));
        themeDescriptions.add(new ThemeDescription(this.infoTextView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contacts_inviteBackground"));
        themeDescriptions.add(new ThemeDescription(this.counterView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contacts_inviteBackground"));
        themeDescriptions.add(new ThemeDescription(this.counterTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contacts_inviteBackground"));
        themeDescriptions.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contacts_inviteText"));
        themeDescriptions.add(new ThemeDescription(this.counterTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contacts_inviteText"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$3$org-telegram-ui-InviteContactsActivity  reason: not valid java name */
    public /* synthetic */ void m3612xbbfbd00f() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof InviteUserCell) {
                    ((InviteUserCell) child).update(0);
                }
            }
        }
    }
}
