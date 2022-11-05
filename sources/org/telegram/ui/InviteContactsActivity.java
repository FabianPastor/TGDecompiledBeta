package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
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
import androidx.annotation.Keep;
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
import org.telegram.messenger.R;
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
import org.telegram.ui.InviteContactsActivity;
/* loaded from: classes3.dex */
public class InviteContactsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, View.OnClickListener {
    private InviteAdapter adapter;
    private int containerHeight;
    private TextView counterTextView;
    private FrameLayout counterView;
    private GroupCreateSpan currentDeletingSpan;
    private GroupCreateDividerItemDecoration decoration;
    private EditTextBoldCursor editText;
    private EmptyTextProgressView emptyView;
    private int fieldY;
    private boolean ignoreScrollEvent;
    private TextView infoTextView;
    private RecyclerListView listView;
    private ArrayList<ContactsController.Contact> phoneBookContacts;
    private ScrollView scrollView;
    private boolean searchWas;
    private boolean searching;
    private SpansContainer spansContainer;
    private TextView textView;
    private HashMap<String, GroupCreateSpan> selectedContacts = new HashMap<>();
    private ArrayList<GroupCreateSpan> allSpans = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class SpansContainer extends ViewGroup {
        private View addingSpan;
        private boolean animationStarted;
        private ArrayList<Animator> animators;
        private AnimatorSet currentAnimation;
        private View removingSpan;

        public SpansContainer(Context context) {
            super(context);
            this.animators = new ArrayList<>();
        }

        @Override // android.view.View
        protected void onMeasure(int i, int i2) {
            int min;
            boolean z;
            float f;
            float f2;
            int i3;
            char c;
            int childCount = getChildCount();
            int size = View.MeasureSpec.getSize(i);
            float f3 = 32.0f;
            int dp = size - AndroidUtilities.dp(32.0f);
            int dp2 = AndroidUtilities.dp(12.0f);
            int dp3 = AndroidUtilities.dp(12.0f);
            int i4 = 0;
            int i5 = 0;
            int i6 = 0;
            while (i4 < childCount) {
                View childAt = getChildAt(i4);
                if (childAt instanceof GroupCreateSpan) {
                    childAt.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(f3), NUM));
                    if (childAt == this.removingSpan || childAt.getMeasuredWidth() + i5 <= dp) {
                        f = 12.0f;
                    } else {
                        f = 12.0f;
                        dp2 += childAt.getMeasuredHeight() + AndroidUtilities.dp(12.0f);
                        i5 = 0;
                    }
                    if (childAt.getMeasuredWidth() + i6 > dp) {
                        dp3 += childAt.getMeasuredHeight() + AndroidUtilities.dp(f);
                        f2 = 16.0f;
                        i6 = 0;
                    } else {
                        f2 = 16.0f;
                    }
                    int dp4 = AndroidUtilities.dp(f2) + i5;
                    if (!this.animationStarted) {
                        View view = this.removingSpan;
                        if (childAt == view) {
                            childAt.setTranslationX(AndroidUtilities.dp(f2) + i6);
                            childAt.setTranslationY(dp3);
                        } else if (view != null) {
                            float f4 = dp4;
                            if (childAt.getTranslationX() != f4) {
                                i3 = 1;
                                c = 0;
                                this.animators.add(ObjectAnimator.ofFloat(childAt, "translationX", f4));
                            } else {
                                i3 = 1;
                                c = 0;
                            }
                            float f5 = dp2;
                            if (childAt.getTranslationY() != f5) {
                                ArrayList<Animator> arrayList = this.animators;
                                float[] fArr = new float[i3];
                                fArr[c] = f5;
                                arrayList.add(ObjectAnimator.ofFloat(childAt, "translationY", fArr));
                            }
                        } else {
                            childAt.setTranslationX(dp4);
                            childAt.setTranslationY(dp2);
                        }
                    }
                    if (childAt != this.removingSpan) {
                        i5 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                    }
                    i6 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                }
                i4++;
                f3 = 32.0f;
            }
            if (AndroidUtilities.isTablet()) {
                min = AndroidUtilities.dp(366.0f) / 3;
            } else {
                Point point = AndroidUtilities.displaySize;
                min = (Math.min(point.x, point.y) - AndroidUtilities.dp(164.0f)) / 3;
            }
            if (dp - i5 < min) {
                dp2 += AndroidUtilities.dp(44.0f);
                i5 = 0;
            }
            if (dp - i6 < min) {
                dp3 += AndroidUtilities.dp(44.0f);
            }
            InviteContactsActivity.this.editText.measure(View.MeasureSpec.makeMeasureSpec(dp - i5, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
            if (!this.animationStarted) {
                int dp5 = dp3 + AndroidUtilities.dp(44.0f);
                int dp6 = i5 + AndroidUtilities.dp(16.0f);
                InviteContactsActivity.this.fieldY = dp2;
                if (this.currentAnimation == null) {
                    InviteContactsActivity.this.containerHeight = dp5;
                    InviteContactsActivity.this.editText.setTranslationX(dp6);
                    InviteContactsActivity.this.editText.setTranslationY(InviteContactsActivity.this.fieldY);
                } else {
                    int dp7 = dp2 + AndroidUtilities.dp(44.0f);
                    if (InviteContactsActivity.this.containerHeight != dp7) {
                        this.animators.add(ObjectAnimator.ofInt(InviteContactsActivity.this, "containerHeight", dp7));
                    }
                    float f6 = dp6;
                    if (InviteContactsActivity.this.editText.getTranslationX() != f6) {
                        this.animators.add(ObjectAnimator.ofFloat(InviteContactsActivity.this.editText, "translationX", f6));
                    }
                    if (InviteContactsActivity.this.editText.getTranslationY() != InviteContactsActivity.this.fieldY) {
                        z = false;
                        this.animators.add(ObjectAnimator.ofFloat(InviteContactsActivity.this.editText, "translationY", InviteContactsActivity.this.fieldY));
                    } else {
                        z = false;
                    }
                    InviteContactsActivity.this.editText.setAllowDrawCursor(z);
                    this.currentAnimation.playTogether(this.animators);
                    this.currentAnimation.start();
                    this.animationStarted = true;
                }
            } else if (this.currentAnimation != null && !InviteContactsActivity.this.ignoreScrollEvent && this.removingSpan == null) {
                InviteContactsActivity.this.editText.bringPointIntoView(InviteContactsActivity.this.editText.getSelectionStart());
            }
            setMeasuredDimension(size, InviteContactsActivity.this.containerHeight);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int childCount = getChildCount();
            for (int i5 = 0; i5 < childCount; i5++) {
                View childAt = getChildAt(i5);
                childAt.layout(0, 0, childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan groupCreateSpan) {
            InviteContactsActivity.this.allSpans.add(groupCreateSpan);
            InviteContactsActivity.this.selectedContacts.put(groupCreateSpan.getKey(), groupCreateSpan);
            InviteContactsActivity.this.editText.setHintVisible(false);
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.currentAnimation = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.InviteContactsActivity.SpansContainer.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.addingSpan = null;
                    SpansContainer.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                    InviteContactsActivity.this.editText.setAllowDrawCursor(true);
                }
            });
            this.currentAnimation.setDuration(150L);
            this.addingSpan = groupCreateSpan;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleX", 0.01f, 1.0f));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleY", 0.01f, 1.0f));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "alpha", 0.0f, 1.0f));
            addView(groupCreateSpan);
        }

        public void removeSpan(final GroupCreateSpan groupCreateSpan) {
            InviteContactsActivity.this.ignoreScrollEvent = true;
            InviteContactsActivity.this.selectedContacts.remove(groupCreateSpan.getKey());
            InviteContactsActivity.this.allSpans.remove(groupCreateSpan);
            groupCreateSpan.setOnClickListener(null);
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.currentAnimation = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.InviteContactsActivity.SpansContainer.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(groupCreateSpan);
                    SpansContainer.this.removingSpan = null;
                    SpansContainer.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                    InviteContactsActivity.this.editText.setAllowDrawCursor(true);
                    if (InviteContactsActivity.this.allSpans.isEmpty()) {
                        InviteContactsActivity.this.editText.setHintVisible(true);
                    }
                }
            });
            this.currentAnimation.setDuration(150L);
            this.removingSpan = groupCreateSpan;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleX", 1.0f, 0.01f));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleY", 1.0f, 0.01f));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "alpha", 1.0f, 0.0f));
            requestLayout();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsImported);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        GroupCreateSpan groupCreateSpan = (GroupCreateSpan) view;
        if (groupCreateSpan.isDeleting()) {
            this.currentDeletingSpan = null;
            this.spansContainer.removeSpan(groupCreateSpan);
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.allSpans.clear();
        this.selectedContacts.clear();
        this.currentDeletingSpan = null;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("InviteFriends", R.string.InviteFriends));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.InviteContactsActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1) {
                    InviteContactsActivity.this.finishFragment();
                }
            }
        });
        ViewGroup viewGroup = new ViewGroup(context) { // from class: org.telegram.ui.InviteContactsActivity.2
            @Override // android.view.View
            protected void onMeasure(int i, int i2) {
                int dp;
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                if (AndroidUtilities.isTablet() || size2 > size) {
                    dp = AndroidUtilities.dp(144.0f);
                } else {
                    dp = AndroidUtilities.dp(56.0f);
                }
                InviteContactsActivity.this.infoTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
                InviteContactsActivity.this.counterView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
                int measuredHeight = InviteContactsActivity.this.infoTextView.getVisibility() == 0 ? InviteContactsActivity.this.infoTextView.getMeasuredHeight() : InviteContactsActivity.this.counterView.getMeasuredHeight();
                InviteContactsActivity.this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
                InviteContactsActivity.this.listView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((size2 - InviteContactsActivity.this.scrollView.getMeasuredHeight()) - measuredHeight, NUM));
                InviteContactsActivity.this.emptyView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((size2 - InviteContactsActivity.this.scrollView.getMeasuredHeight()) - AndroidUtilities.dp(72.0f), NUM));
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                InviteContactsActivity.this.scrollView.layout(0, 0, InviteContactsActivity.this.scrollView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight());
                InviteContactsActivity.this.listView.layout(0, InviteContactsActivity.this.scrollView.getMeasuredHeight(), InviteContactsActivity.this.listView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight() + InviteContactsActivity.this.listView.getMeasuredHeight());
                InviteContactsActivity.this.emptyView.layout(0, InviteContactsActivity.this.scrollView.getMeasuredHeight() + AndroidUtilities.dp(72.0f), InviteContactsActivity.this.emptyView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight() + InviteContactsActivity.this.emptyView.getMeasuredHeight());
                int i5 = i4 - i2;
                int measuredHeight = i5 - InviteContactsActivity.this.infoTextView.getMeasuredHeight();
                InviteContactsActivity.this.infoTextView.layout(0, measuredHeight, InviteContactsActivity.this.infoTextView.getMeasuredWidth(), InviteContactsActivity.this.infoTextView.getMeasuredHeight() + measuredHeight);
                int measuredHeight2 = i5 - InviteContactsActivity.this.counterView.getMeasuredHeight();
                InviteContactsActivity.this.counterView.layout(0, measuredHeight2, InviteContactsActivity.this.counterView.getMeasuredWidth(), InviteContactsActivity.this.counterView.getMeasuredHeight() + measuredHeight2);
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view == InviteContactsActivity.this.listView || view == InviteContactsActivity.this.emptyView) {
                    ((BaseFragment) InviteContactsActivity.this).parentLayout.drawHeaderShadow(canvas, InviteContactsActivity.this.scrollView.getMeasuredHeight());
                }
                return drawChild;
            }
        };
        this.fragmentView = viewGroup;
        ViewGroup viewGroup2 = viewGroup;
        ScrollView scrollView = new ScrollView(context) { // from class: org.telegram.ui.InviteContactsActivity.3
            @Override // android.widget.ScrollView, android.view.ViewGroup, android.view.ViewParent
            public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                if (InviteContactsActivity.this.ignoreScrollEvent) {
                    InviteContactsActivity.this.ignoreScrollEvent = false;
                    return false;
                }
                rect.offset(view.getLeft() - view.getScrollX(), view.getTop() - view.getScrollY());
                rect.top += InviteContactsActivity.this.fieldY + AndroidUtilities.dp(20.0f);
                rect.bottom += InviteContactsActivity.this.fieldY + AndroidUtilities.dp(50.0f);
                return super.requestChildRectangleOnScreen(view, rect, z);
            }
        };
        this.scrollView = scrollView;
        scrollView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("windowBackgroundWhite"));
        viewGroup2.addView(this.scrollView);
        SpansContainer spansContainer = new SpansContainer(context);
        this.spansContainer = spansContainer;
        this.scrollView.addView(spansContainer, LayoutHelper.createFrame(-1, -2.0f));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) { // from class: org.telegram.ui.InviteContactsActivity.4
            @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (InviteContactsActivity.this.currentDeletingSpan != null) {
                    InviteContactsActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    InviteContactsActivity.this.currentDeletingSpan = null;
                }
                if (motionEvent.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.editText = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.editText.setHintColor(Theme.getColor("groupcreate_hintText"));
        this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.editText.setCursorColor(Theme.getColor("groupcreate_cursor"));
        this.editText.setCursorWidth(1.5f);
        this.editText.setInputType(655536);
        this.editText.setSingleLine(true);
        this.editText.setBackgroundDrawable(null);
        this.editText.setVerticalScrollBarEnabled(false);
        this.editText.setHorizontalScrollBarEnabled(false);
        this.editText.setTextIsSelectable(false);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setImeOptions(NUM);
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.spansContainer.addView(this.editText);
        this.editText.setHintText(LocaleController.getString("SearchFriends", R.string.SearchFriends));
        this.editText.setCustomSelectionActionModeCallback(new ActionMode.Callback(this) { // from class: org.telegram.ui.InviteContactsActivity.5
            @Override // android.view.ActionMode.Callback
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override // android.view.ActionMode.Callback
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override // android.view.ActionMode.Callback
            public void onDestroyActionMode(ActionMode actionMode) {
            }

            @Override // android.view.ActionMode.Callback
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }
        });
        this.editText.setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.InviteContactsActivity.6
            private boolean wasEmpty;

            @Override // android.view.View.OnKeyListener
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                boolean z = true;
                if (keyEvent.getAction() == 0) {
                    if (InviteContactsActivity.this.editText.length() != 0) {
                        z = false;
                    }
                    this.wasEmpty = z;
                } else if (keyEvent.getAction() == 1 && this.wasEmpty && !InviteContactsActivity.this.allSpans.isEmpty()) {
                    InviteContactsActivity.this.spansContainer.removeSpan((GroupCreateSpan) InviteContactsActivity.this.allSpans.get(InviteContactsActivity.this.allSpans.size() - 1));
                    InviteContactsActivity.this.updateHint();
                    InviteContactsActivity.this.checkVisibleRows();
                    return true;
                }
                return false;
            }
        });
        this.editText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.InviteContactsActivity.7
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (InviteContactsActivity.this.editText.length() != 0) {
                    InviteContactsActivity.this.searching = true;
                    InviteContactsActivity.this.searchWas = true;
                    InviteContactsActivity.this.adapter.setSearching(true);
                    InviteContactsActivity.this.adapter.searchDialogs(InviteContactsActivity.this.editText.getText().toString());
                    InviteContactsActivity.this.listView.setFastScrollVisible(false);
                    InviteContactsActivity.this.listView.setVerticalScrollBarEnabled(true);
                    InviteContactsActivity.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                    return;
                }
                InviteContactsActivity.this.closeSearch();
            }
        });
        this.emptyView = new EmptyTextProgressView(context);
        if (ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        viewGroup2.addView(this.emptyView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        InviteAdapter inviteAdapter = new InviteAdapter(context);
        this.adapter = inviteAdapter;
        recyclerListView2.setAdapter(inviteAdapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        RecyclerListView recyclerListView3 = this.listView;
        GroupCreateDividerItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        this.decoration = groupCreateDividerItemDecoration;
        recyclerListView3.addItemDecoration(groupCreateDividerItemDecoration);
        viewGroup2.addView(this.listView);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.InviteContactsActivity$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                InviteContactsActivity.this.lambda$createView$0(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.InviteContactsActivity.8
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(InviteContactsActivity.this.editText);
                }
            }
        });
        TextView textView = new TextView(context);
        this.infoTextView = textView;
        textView.setBackgroundColor(Theme.getColor("contacts_inviteBackground"));
        this.infoTextView.setTextColor(Theme.getColor("contacts_inviteText"));
        this.infoTextView.setGravity(17);
        this.infoTextView.setText(LocaleController.getString("InviteFriendsHelp", R.string.InviteFriendsHelp));
        this.infoTextView.setTextSize(1, 13.0f);
        this.infoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.infoTextView.setPadding(AndroidUtilities.dp(17.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(17.0f), AndroidUtilities.dp(9.0f));
        viewGroup2.addView(this.infoTextView, LayoutHelper.createFrame(-1, -2, 83));
        FrameLayout frameLayout = new FrameLayout(context);
        this.counterView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor("contacts_inviteBackground"));
        this.counterView.setVisibility(4);
        viewGroup2.addView(this.counterView, LayoutHelper.createFrame(-1, 48, 83));
        this.counterView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.InviteContactsActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                InviteContactsActivity.this.lambda$createView$1(view);
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        this.counterView.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 17));
        TextView textView2 = new TextView(context);
        this.counterTextView = textView2;
        textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.counterTextView.setTextSize(1, 14.0f);
        this.counterTextView.setTextColor(Theme.getColor("contacts_inviteBackground"));
        this.counterTextView.setGravity(17);
        this.counterTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(10.0f), Theme.getColor("contacts_inviteText")));
        this.counterTextView.setMinWidth(AndroidUtilities.dp(20.0f));
        this.counterTextView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(1.0f));
        linearLayout.addView(this.counterTextView, LayoutHelper.createLinear(-2, 20, 16, 0, 0, 10, 0));
        TextView textView3 = new TextView(context);
        this.textView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.textView.setTextColor(Theme.getColor("contacts_inviteText"));
        this.textView.setGravity(17);
        this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.textView.setText(LocaleController.getString("InviteToTelegram", R.string.InviteToTelegram).toUpperCase());
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        linearLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2, 16));
        updateHint();
        this.adapter.notifyDataSetChanged();
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view, int i) {
        InviteUserCell inviteUserCell;
        ContactsController.Contact contact;
        if (i == 0 && !this.searching) {
            try {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                String inviteText = ContactsController.getInstance(this.currentAccount).getInviteText(0);
                intent.putExtra("android.intent.extra.TEXT", inviteText);
                getParentActivity().startActivityForResult(Intent.createChooser(intent, inviteText), 500);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (!(view instanceof InviteUserCell) || (contact = (inviteUserCell = (InviteUserCell) view).getContact()) == null) {
        } else {
            boolean containsKey = this.selectedContacts.containsKey(contact.key);
            if (containsKey) {
                this.spansContainer.removeSpan(this.selectedContacts.get(contact.key));
            } else {
                GroupCreateSpan groupCreateSpan = new GroupCreateSpan(this.editText.getContext(), contact);
                this.spansContainer.addSpan(groupCreateSpan);
                groupCreateSpan.setOnClickListener(this);
            }
            updateHint();
            if (this.searching || this.searchWas) {
                AndroidUtilities.showKeyboard(this.editText);
            } else {
                inviteUserCell.setChecked(!containsKey, true);
            }
            if (this.editText.length() <= 0) {
                return;
            }
            this.editText.setText((CharSequence) null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view) {
        try {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (int i2 = 0; i2 < this.allSpans.size(); i2++) {
                ContactsController.Contact contact = this.allSpans.get(i2).getContact();
                if (sb.length() != 0) {
                    sb.append(';');
                }
                sb.append(contact.phones.get(0));
                if (i2 == 0 && this.allSpans.size() == 1) {
                    i = contact.imported;
                }
            }
            Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse("smsto:" + sb.toString()));
            intent.putExtra("sms_body", ContactsController.getInstance(this.currentAccount).getInviteText(i));
            getParentActivity().startActivityForResult(intent, 500);
        } catch (Exception e) {
            FileLog.e(e);
        }
        finishFragment();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        EditTextBoldCursor editTextBoldCursor = this.editText;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.contactsImported) {
            fetchContacts();
        }
    }

    @Keep
    public void setContainerHeight(int i) {
        this.containerHeight = i;
        SpansContainer spansContainer = this.spansContainer;
        if (spansContainer != null) {
            spansContainer.requestLayout();
        }
    }

    @Keep
    public int getContainerHeight() {
        return this.containerHeight;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkVisibleRows() {
        InviteUserCell inviteUserCell;
        ContactsController.Contact contact;
        int childCount = this.listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.listView.getChildAt(i);
            if ((childAt instanceof InviteUserCell) && (contact = (inviteUserCell = (InviteUserCell) childAt).getContact()) != null) {
                inviteUserCell.setChecked(this.selectedContacts.containsKey(contact.key), true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateHint() {
        if (this.selectedContacts.isEmpty()) {
            this.infoTextView.setVisibility(0);
            this.counterView.setVisibility(4);
            return;
        }
        this.infoTextView.setVisibility(4);
        this.counterView.setVisibility(0);
        this.counterTextView.setText(String.format("%d", Integer.valueOf(this.selectedContacts.size())));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeSearch() {
        this.searching = false;
        this.searchWas = false;
        this.adapter.setSearching(false);
        this.adapter.searchDialogs(null);
        this.listView.setFastScrollVisible(true);
        this.listView.setVerticalScrollBarEnabled(false);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
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

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$fetchContacts$2(ContactsController.Contact contact, ContactsController.Contact contact2) {
        int i = contact.imported;
        int i2 = contact2.imported;
        if (i > i2) {
            return -1;
        }
        return i < i2 ? 1 : 0;
    }

    /* loaded from: classes3.dex */
    public class InviteAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private ArrayList<ContactsController.Contact> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Timer searchTimer;
        private boolean searching;

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public InviteAdapter(Context context) {
            this.context = context;
        }

        public void setSearching(boolean z) {
            if (this.searching == z) {
                return;
            }
            this.searching = z;
            notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (!this.searching) {
                return InviteContactsActivity.this.phoneBookContacts.size() + 1;
            }
            return this.searchResult.size();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1818onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            if (i == 1) {
                InviteTextCell inviteTextCell = new InviteTextCell(this.context);
                inviteTextCell.setTextAndIcon(LocaleController.getString("ShareTelegram", R.string.ShareTelegram), R.drawable.share);
                frameLayout = inviteTextCell;
            } else {
                frameLayout = new InviteUserCell(this.context, true);
            }
            return new RecyclerListView.Holder(frameLayout);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ContactsController.Contact contact;
            CharSequence charSequence;
            if (viewHolder.getItemViewType() != 0) {
                return;
            }
            InviteUserCell inviteUserCell = (InviteUserCell) viewHolder.itemView;
            if (!this.searching) {
                contact = (ContactsController.Contact) InviteContactsActivity.this.phoneBookContacts.get(i - 1);
                charSequence = null;
            } else {
                contact = this.searchResult.get(i);
                charSequence = this.searchResultNames.get(i);
            }
            inviteUserCell.setUser(contact, charSequence);
            inviteUserCell.setChecked(InviteContactsActivity.this.selectedContacts.containsKey(contact.key), false);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return (this.searching || i != 0) ? 0 : 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof InviteUserCell) {
                ((InviteUserCell) view).recycle();
            }
        }

        public void searchDialogs(String str) {
            try {
                Timer timer = this.searchTimer;
                if (timer != null) {
                    timer.cancel();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
                return;
            }
            Timer timer2 = new Timer();
            this.searchTimer = timer2;
            timer2.schedule(new AnonymousClass1(str), 200L, 300L);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: org.telegram.ui.InviteContactsActivity$InviteAdapter$1  reason: invalid class name */
        /* loaded from: classes3.dex */
        public class AnonymousClass1 extends TimerTask {
            final /* synthetic */ String val$query;

            AnonymousClass1(String str) {
                this.val$query = str;
            }

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                try {
                    InviteAdapter.this.searchTimer.cancel();
                    InviteAdapter.this.searchTimer = null;
                } catch (Exception e) {
                    FileLog.e(e);
                }
                final String str = this.val$query;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.InviteContactsActivity$InviteAdapter$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        InviteContactsActivity.InviteAdapter.AnonymousClass1.this.lambda$run$1(str);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$run$1(final String str) {
                Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.InviteContactsActivity$InviteAdapter$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        InviteContactsActivity.InviteAdapter.AnonymousClass1.this.lambda$run$0(str);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            /* JADX WARN: Code restructure failed: missing block: B:34:0x00c6, code lost:
                if (r11.contains(" " + r14) != false) goto L39;
             */
            /* JADX WARN: Removed duplicated region for block: B:38:0x00da A[LOOP:1: B:25:0x008a->B:38:0x00da, LOOP_END] */
            /* JADX WARN: Removed duplicated region for block: B:45:0x00cb A[SYNTHETIC] */
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public /* synthetic */ void lambda$run$0(java.lang.String r17) {
                /*
                    r16 = this;
                    r0 = r16
                    java.lang.String r1 = r17.trim()
                    java.lang.String r1 = r1.toLowerCase()
                    int r2 = r1.length()
                    if (r2 != 0) goto L20
                    org.telegram.ui.InviteContactsActivity$InviteAdapter r1 = org.telegram.ui.InviteContactsActivity.InviteAdapter.this
                    java.util.ArrayList r2 = new java.util.ArrayList
                    r2.<init>()
                    java.util.ArrayList r3 = new java.util.ArrayList
                    r3.<init>()
                    org.telegram.ui.InviteContactsActivity.InviteAdapter.access$2600(r1, r2, r3)
                    return
                L20:
                    org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
                    java.lang.String r2 = r2.getTranslitString(r1)
                    boolean r3 = r1.equals(r2)
                    r4 = 0
                    if (r3 != 0) goto L35
                    int r3 = r2.length()
                    if (r3 != 0) goto L36
                L35:
                    r2 = r4
                L36:
                    r3 = 0
                    r5 = 1
                    if (r2 == 0) goto L3c
                    r6 = 1
                    goto L3d
                L3c:
                    r6 = 0
                L3d:
                    int r6 = r6 + r5
                    java.lang.String[] r7 = new java.lang.String[r6]
                    r7[r3] = r1
                    if (r2 == 0) goto L46
                    r7[r5] = r2
                L46:
                    java.util.ArrayList r1 = new java.util.ArrayList
                    r1.<init>()
                    java.util.ArrayList r2 = new java.util.ArrayList
                    r2.<init>()
                    r8 = 0
                L51:
                    org.telegram.ui.InviteContactsActivity$InviteAdapter r9 = org.telegram.ui.InviteContactsActivity.InviteAdapter.this
                    org.telegram.ui.InviteContactsActivity r9 = org.telegram.ui.InviteContactsActivity.this
                    java.util.ArrayList r9 = org.telegram.ui.InviteContactsActivity.access$2400(r9)
                    int r9 = r9.size()
                    if (r8 >= r9) goto Le3
                    org.telegram.ui.InviteContactsActivity$InviteAdapter r9 = org.telegram.ui.InviteContactsActivity.InviteAdapter.this
                    org.telegram.ui.InviteContactsActivity r9 = org.telegram.ui.InviteContactsActivity.this
                    java.util.ArrayList r9 = org.telegram.ui.InviteContactsActivity.access$2400(r9)
                    java.lang.Object r9 = r9.get(r8)
                    org.telegram.messenger.ContactsController$Contact r9 = (org.telegram.messenger.ContactsController.Contact) r9
                    java.lang.String r10 = r9.first_name
                    java.lang.String r11 = r9.last_name
                    java.lang.String r10 = org.telegram.messenger.ContactsController.formatName(r10, r11)
                    java.lang.String r10 = r10.toLowerCase()
                    org.telegram.messenger.LocaleController r11 = org.telegram.messenger.LocaleController.getInstance()
                    java.lang.String r11 = r11.getTranslitString(r10)
                    boolean r12 = r10.equals(r11)
                    if (r12 == 0) goto L88
                    r11 = r4
                L88:
                    r12 = 0
                    r13 = 0
                L8a:
                    if (r12 >= r6) goto Lde
                    r14 = r7[r12]
                    boolean r15 = r10.startsWith(r14)
                    if (r15 != 0) goto Lc8
                    java.lang.StringBuilder r15 = new java.lang.StringBuilder
                    r15.<init>()
                    java.lang.String r3 = " "
                    r15.append(r3)
                    r15.append(r14)
                    java.lang.String r15 = r15.toString()
                    boolean r15 = r10.contains(r15)
                    if (r15 != 0) goto Lc8
                    if (r11 == 0) goto Lc9
                    boolean r15 = r11.startsWith(r14)
                    if (r15 != 0) goto Lc8
                    java.lang.StringBuilder r15 = new java.lang.StringBuilder
                    r15.<init>()
                    r15.append(r3)
                    r15.append(r14)
                    java.lang.String r3 = r15.toString()
                    boolean r3 = r11.contains(r3)
                    if (r3 == 0) goto Lc9
                Lc8:
                    r13 = 1
                Lc9:
                    if (r13 == 0) goto Lda
                    java.lang.String r3 = r9.first_name
                    java.lang.String r10 = r9.last_name
                    java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r10, r14)
                    r2.add(r3)
                    r1.add(r9)
                    goto Lde
                Lda:
                    int r12 = r12 + 1
                    r3 = 0
                    goto L8a
                Lde:
                    int r8 = r8 + 1
                    r3 = 0
                    goto L51
                Le3:
                    org.telegram.ui.InviteContactsActivity$InviteAdapter r3 = org.telegram.ui.InviteContactsActivity.InviteAdapter.this
                    org.telegram.ui.InviteContactsActivity.InviteAdapter.access$2600(r3, r1, r2)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.InviteContactsActivity.InviteAdapter.AnonymousClass1.lambda$run$0(java.lang.String):void");
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateSearchResults(final ArrayList<ContactsController.Contact> arrayList, final ArrayList<CharSequence> arrayList2) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.InviteContactsActivity$InviteAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    InviteContactsActivity.InviteAdapter.this.lambda$updateSearchResults$0(arrayList, arrayList2);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSearchResults$0(ArrayList arrayList, ArrayList arrayList2) {
            if (!this.searching) {
                return;
            }
            this.searchResult = arrayList;
            this.searchResultNames = arrayList2;
            notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            int itemCount = getItemCount();
            boolean z = false;
            InviteContactsActivity.this.emptyView.setVisibility(itemCount == 1 ? 0 : 4);
            GroupCreateDividerItemDecoration groupCreateDividerItemDecoration = InviteContactsActivity.this.decoration;
            if (itemCount == 1) {
                z = true;
            }
            groupCreateDividerItemDecoration.setSingle(z);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.InviteContactsActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                InviteContactsActivity.this.lambda$getThemeDescriptions$3();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollActive"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollInactive"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "groupcreate_hintText"));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "groupcreate_cursor"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GroupCreateSectionCell.class}, null, null, null, "graySection"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{GroupCreateSectionCell.class}, new String[]{"drawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_sectionShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_sectionText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_sectionText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{InviteUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{InviteUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{InviteUserCell.class}, null, Theme.avatarDrawables, null, "avatar_text"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{InviteTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{InviteTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "groupcreate_spanBackground"));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "groupcreate_spanText"));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "groupcreate_spanDelete"));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription(this.infoTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "contacts_inviteText"));
        arrayList.add(new ThemeDescription(this.infoTextView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "contacts_inviteBackground"));
        arrayList.add(new ThemeDescription(this.counterView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "contacts_inviteBackground"));
        arrayList.add(new ThemeDescription(this.counterTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "contacts_inviteBackground"));
        arrayList.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "contacts_inviteText"));
        arrayList.add(new ThemeDescription(this.counterTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "contacts_inviteText"));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$3() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof InviteUserCell) {
                    ((InviteUserCell) childAt).update(0);
                }
            }
        }
    }
}
