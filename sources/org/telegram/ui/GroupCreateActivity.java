package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.GroupCreateSectionCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.GroupCreateDividerItemDecoration;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PermanentLinkBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;

public class GroupCreateActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, View.OnClickListener {
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public GroupCreateAdapter adapter;
    /* access modifiers changed from: private */
    public boolean addToGroup;
    /* access modifiers changed from: private */
    public ArrayList<GroupCreateSpan> allSpans = new ArrayList<>();
    /* access modifiers changed from: private */
    public long channelId;
    private int chatAddType;
    /* access modifiers changed from: private */
    public long chatId;
    private int chatType = 0;
    /* access modifiers changed from: private */
    public int containerHeight;
    /* access modifiers changed from: private */
    public AnimatorSet currentAnimation;
    /* access modifiers changed from: private */
    public GroupCreateSpan currentDeletingSpan;
    private AnimatorSet currentDoneButtonAnimation;
    private GroupCreateActivityDelegate delegate;
    private ContactsAddActivityDelegate delegate2;
    private boolean doneButtonVisible;
    /* access modifiers changed from: private */
    public EditTextBoldCursor editText;
    /* access modifiers changed from: private */
    public StickerEmptyView emptyView;
    /* access modifiers changed from: private */
    public int fieldY;
    /* access modifiers changed from: private */
    public ImageView floatingButton;
    private boolean forImport;
    /* access modifiers changed from: private */
    public boolean ignoreScrollEvent;
    /* access modifiers changed from: private */
    public LongSparseArray<TLObject> ignoreUsers;
    private TLRPC.ChatFull info;
    /* access modifiers changed from: private */
    public boolean isAlwaysShare;
    /* access modifiers changed from: private */
    public boolean isNeverShare;
    /* access modifiers changed from: private */
    public GroupCreateDividerItemDecoration itemDecoration;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private int maxCount = getMessagesController().maxMegagroupCount;
    int maxSize;
    /* access modifiers changed from: private */
    public int measuredContainerHeight;
    /* access modifiers changed from: private */
    public ScrollView scrollView;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public LongSparseArray<GroupCreateSpan> selectedContacts = new LongSparseArray<>();
    private PermanentLinkBottomSheet sharedLinkBottomSheet;
    /* access modifiers changed from: private */
    public SpansContainer spansContainer;

    public interface GroupCreateActivityDelegate {
        void didSelectUsers(ArrayList<Long> arrayList);
    }

    public interface GroupCreateActivityImportDelegate {
        void didCreateChat(int i);
    }

    public interface ContactsAddActivityDelegate {
        void didSelectUsers(ArrayList<TLRPC.User> arrayList, int i);

        void needAddBot(TLRPC.User user);

        /* renamed from: org.telegram.ui.GroupCreateActivity$ContactsAddActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$needAddBot(ContactsAddActivityDelegate _this, TLRPC.User user) {
            }
        }
    }

    private class SpansContainer extends ViewGroup {
        /* access modifiers changed from: private */
        public View addingSpan;
        /* access modifiers changed from: private */
        public int animationIndex = -1;
        /* access modifiers changed from: private */
        public boolean animationStarted;
        private ArrayList<Animator> animators = new ArrayList<>();
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
            GroupCreateActivity.this.editText.measure(View.MeasureSpec.makeMeasureSpec(maxWidth - currentLineWidth, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
            if (!this.animationStarted) {
                int currentHeight = AndroidUtilities.dp(42.0f) + allY;
                int fieldX = AndroidUtilities.dp(16.0f) + currentLineWidth;
                int unused = GroupCreateActivity.this.fieldY = y;
                if (GroupCreateActivity.this.currentAnimation != null) {
                    int resultHeight = AndroidUtilities.dp(42.0f) + y;
                    if (GroupCreateActivity.this.containerHeight != resultHeight) {
                        int i = count;
                        int i2 = maxWidth;
                        this.animators.add(ObjectAnimator.ofInt(GroupCreateActivity.this, "containerHeight", new int[]{resultHeight}));
                    } else {
                        int i3 = maxWidth;
                    }
                    GroupCreateActivity groupCreateActivity = GroupCreateActivity.this;
                    int unused2 = groupCreateActivity.measuredContainerHeight = Math.max(groupCreateActivity.containerHeight, resultHeight);
                    if (GroupCreateActivity.this.editText.getTranslationX() != ((float) fieldX)) {
                        this.animators.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, "translationX", new float[]{(float) fieldX}));
                    }
                    if (GroupCreateActivity.this.editText.getTranslationY() != ((float) GroupCreateActivity.this.fieldY)) {
                        z = false;
                        this.animators.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, "translationY", new float[]{(float) GroupCreateActivity.this.fieldY}));
                    } else {
                        z = false;
                    }
                    GroupCreateActivity.this.editText.setAllowDrawCursor(z);
                    GroupCreateActivity.this.currentAnimation.playTogether(this.animators);
                    GroupCreateActivity.this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            GroupCreateActivity.this.getNotificationCenter().onAnimationFinish(SpansContainer.this.animationIndex);
                            SpansContainer.this.requestLayout();
                        }
                    });
                    this.animationIndex = GroupCreateActivity.this.getNotificationCenter().setAnimationInProgress(this.animationIndex, (int[]) null);
                    GroupCreateActivity.this.currentAnimation.start();
                    this.animationStarted = true;
                } else {
                    int i4 = maxWidth;
                    GroupCreateActivity groupCreateActivity2 = GroupCreateActivity.this;
                    int unused3 = groupCreateActivity2.measuredContainerHeight = groupCreateActivity2.containerHeight = currentHeight;
                    GroupCreateActivity.this.editText.setTranslationX((float) fieldX);
                    GroupCreateActivity.this.editText.setTranslationY((float) GroupCreateActivity.this.fieldY);
                }
            } else {
                int i5 = maxWidth;
                if (GroupCreateActivity.this.currentAnimation != null && !GroupCreateActivity.this.ignoreScrollEvent && this.removingSpan == null) {
                    GroupCreateActivity.this.editText.bringPointIntoView(GroupCreateActivity.this.editText.getSelectionStart());
                }
            }
            setMeasuredDimension(width, GroupCreateActivity.this.measuredContainerHeight);
            GroupCreateActivity.this.listView.setTranslationY(0.0f);
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
            GroupCreateActivity.this.allSpans.add(span);
            GroupCreateActivity.this.selectedContacts.put(span.getUid(), span);
            GroupCreateActivity.this.editText.setHintVisible(false);
            if (GroupCreateActivity.this.currentAnimation != null) {
                GroupCreateActivity.this.currentAnimation.setupEndValues();
                GroupCreateActivity.this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            AnimatorSet unused = GroupCreateActivity.this.currentAnimation = new AnimatorSet();
            GroupCreateActivity.this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    View unused = SpansContainer.this.addingSpan = null;
                    AnimatorSet unused2 = GroupCreateActivity.this.currentAnimation = null;
                    boolean unused3 = SpansContainer.this.animationStarted = false;
                    GroupCreateActivity.this.editText.setAllowDrawCursor(true);
                }
            });
            GroupCreateActivity.this.currentAnimation.setDuration(150);
            this.addingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.SCALE_X, new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.SCALE_Y, new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.ALPHA, new float[]{0.0f, 1.0f}));
            addView(span);
        }

        public void removeSpan(final GroupCreateSpan span) {
            boolean unused = GroupCreateActivity.this.ignoreScrollEvent = true;
            GroupCreateActivity.this.selectedContacts.remove(span.getUid());
            GroupCreateActivity.this.allSpans.remove(span);
            span.setOnClickListener((View.OnClickListener) null);
            if (GroupCreateActivity.this.currentAnimation != null) {
                GroupCreateActivity.this.currentAnimation.setupEndValues();
                GroupCreateActivity.this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            AnimatorSet unused2 = GroupCreateActivity.this.currentAnimation = new AnimatorSet();
            GroupCreateActivity.this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(span);
                    View unused = SpansContainer.this.removingSpan = null;
                    AnimatorSet unused2 = GroupCreateActivity.this.currentAnimation = null;
                    boolean unused3 = SpansContainer.this.animationStarted = false;
                    GroupCreateActivity.this.editText.setAllowDrawCursor(true);
                    if (GroupCreateActivity.this.allSpans.isEmpty()) {
                        GroupCreateActivity.this.editText.setHintVisible(true);
                    }
                }
            });
            GroupCreateActivity.this.currentAnimation.setDuration(150);
            this.removingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.SCALE_X, new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.SCALE_Y, new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.ALPHA, new float[]{1.0f, 0.0f}));
            requestLayout();
        }
    }

    public GroupCreateActivity() {
    }

    public GroupCreateActivity(Bundle args) {
        super(args);
        this.chatType = args.getInt("chatType", 0);
        this.forImport = args.getBoolean("forImport", false);
        this.isAlwaysShare = args.getBoolean("isAlwaysShare", false);
        this.isNeverShare = args.getBoolean("isNeverShare", false);
        this.addToGroup = args.getBoolean("addToGroup", false);
        this.chatAddType = args.getInt("chatAddType", 0);
        this.chatId = args.getLong("chatId");
        this.channelId = args.getLong("channelId");
        if (this.isAlwaysShare || this.isNeverShare || this.addToGroup) {
            this.maxCount = 0;
        } else {
            this.maxCount = this.chatType == 0 ? getMessagesController().maxMegagroupCount : getMessagesController().maxBroadcastCount;
        }
    }

    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.contactsDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().addObserver(this, NotificationCenter.chatDidCreated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.contactsDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().removeObserver(this, NotificationCenter.chatDidCreated);
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
        String str;
        int i;
        Context context2 = context;
        this.searching = false;
        this.searchWas = false;
        this.allSpans.clear();
        this.selectedContacts.clear();
        this.currentDeletingSpan = null;
        this.doneButtonVisible = this.chatType == 2;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.chatType == 2) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAddSubscribers", NUM));
        } else if (this.addToGroup) {
            if (this.channelId != 0) {
                this.actionBar.setTitle(LocaleController.getString("ChannelAddSubscribers", NUM));
            } else {
                this.actionBar.setTitle(LocaleController.getString("GroupAddMembers", NUM));
            }
        } else if (this.isAlwaysShare) {
            int i2 = this.chatAddType;
            if (i2 == 2) {
                this.actionBar.setTitle(LocaleController.getString("FilterAlwaysShow", NUM));
            } else if (i2 == 1) {
                this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", NUM));
            } else {
                this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", NUM));
            }
        } else if (this.isNeverShare) {
            int i3 = this.chatAddType;
            if (i3 == 2) {
                this.actionBar.setTitle(LocaleController.getString("FilterNeverShow", NUM));
            } else if (i3 == 1) {
                this.actionBar.setTitle(LocaleController.getString("NeverAllow", NUM));
            } else {
                this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", NUM));
            }
        } else {
            ActionBar actionBar = this.actionBar;
            if (this.chatType == 0) {
                i = NUM;
                str = "NewGroup";
            } else {
                i = NUM;
                str = "NewBroadcastList";
            }
            actionBar.setTitle(LocaleController.getString(str, i));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    GroupCreateActivity.this.finishFragment();
                } else if (id == 1) {
                    boolean unused = GroupCreateActivity.this.onDonePressed(true);
                }
            }
        });
        this.fragmentView = new ViewGroup(context2) {
            private VerticalPositionAutoAnimator verticalPositionAutoAnimator;

            public void onViewAdded(View child) {
                if (child == GroupCreateActivity.this.floatingButton && this.verticalPositionAutoAnimator == null) {
                    this.verticalPositionAutoAnimator = VerticalPositionAutoAnimator.attach(child);
                }
            }

            /* access modifiers changed from: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                VerticalPositionAutoAnimator verticalPositionAutoAnimator2 = this.verticalPositionAutoAnimator;
                if (verticalPositionAutoAnimator2 != null) {
                    verticalPositionAutoAnimator2.ignoreNextLayout();
                }
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(width, height);
                float f = 56.0f;
                if (AndroidUtilities.isTablet() || height > width) {
                    GroupCreateActivity.this.maxSize = AndroidUtilities.dp(144.0f);
                } else {
                    GroupCreateActivity.this.maxSize = AndroidUtilities.dp(56.0f);
                }
                GroupCreateActivity.this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(GroupCreateActivity.this.maxSize, Integer.MIN_VALUE));
                GroupCreateActivity.this.listView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
                GroupCreateActivity.this.emptyView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
                if (GroupCreateActivity.this.floatingButton != null) {
                    if (Build.VERSION.SDK_INT < 21) {
                        f = 60.0f;
                    }
                    int w = AndroidUtilities.dp(f);
                    GroupCreateActivity.this.floatingButton.measure(View.MeasureSpec.makeMeasureSpec(w, NUM), View.MeasureSpec.makeMeasureSpec(w, NUM));
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                GroupCreateActivity.this.scrollView.layout(0, 0, GroupCreateActivity.this.scrollView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight());
                GroupCreateActivity.this.listView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.listView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.listView.getMeasuredHeight());
                GroupCreateActivity.this.emptyView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.emptyView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.emptyView.getMeasuredHeight());
                if (GroupCreateActivity.this.floatingButton != null) {
                    int l = LocaleController.isRTL ? AndroidUtilities.dp(14.0f) : ((right - left) - AndroidUtilities.dp(14.0f)) - GroupCreateActivity.this.floatingButton.getMeasuredWidth();
                    int t = ((bottom - top) - AndroidUtilities.dp(14.0f)) - GroupCreateActivity.this.floatingButton.getMeasuredHeight();
                    GroupCreateActivity.this.floatingButton.layout(l, t, GroupCreateActivity.this.floatingButton.getMeasuredWidth() + l, GroupCreateActivity.this.floatingButton.getMeasuredHeight() + t);
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                GroupCreateActivity.this.parentLayout.drawHeaderShadow(canvas, Math.min(GroupCreateActivity.this.maxSize, (GroupCreateActivity.this.measuredContainerHeight + GroupCreateActivity.this.containerHeight) - GroupCreateActivity.this.measuredContainerHeight));
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child == GroupCreateActivity.this.listView) {
                    canvas.save();
                    canvas.clipRect(child.getLeft(), Math.min(GroupCreateActivity.this.maxSize, (GroupCreateActivity.this.measuredContainerHeight + GroupCreateActivity.this.containerHeight) - GroupCreateActivity.this.measuredContainerHeight), child.getRight(), child.getBottom());
                    boolean result = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return result;
                } else if (child != GroupCreateActivity.this.scrollView) {
                    return super.drawChild(canvas, child, drawingTime);
                } else {
                    canvas.save();
                    canvas.clipRect(child.getLeft(), child.getTop(), child.getRight(), Math.min(GroupCreateActivity.this.maxSize, (GroupCreateActivity.this.measuredContainerHeight + GroupCreateActivity.this.containerHeight) - GroupCreateActivity.this.measuredContainerHeight));
                    boolean result2 = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return result2;
                }
            }
        };
        ViewGroup frameLayout = (ViewGroup) this.fragmentView;
        frameLayout.setFocusableInTouchMode(true);
        frameLayout.setDescendantFocusability(131072);
        AnonymousClass3 r7 = new ScrollView(context2) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                if (GroupCreateActivity.this.ignoreScrollEvent) {
                    boolean unused = GroupCreateActivity.this.ignoreScrollEvent = false;
                    return false;
                }
                rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
                rectangle.top += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(20.0f);
                rectangle.bottom += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(50.0f);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        this.scrollView = r7;
        r7.setClipChildren(false);
        frameLayout.setClipChildren(false);
        this.scrollView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("windowBackgroundWhite"));
        frameLayout.addView(this.scrollView);
        SpansContainer spansContainer2 = new SpansContainer(context2);
        this.spansContainer = spansContainer2;
        this.scrollView.addView(spansContainer2, LayoutHelper.createFrame(-1, -2.0f));
        this.spansContainer.setOnClickListener(new GroupCreateActivity$$ExternalSyntheticLambda2(this));
        AnonymousClass4 r72 = new EditTextBoldCursor(context2) {
            public boolean onTouchEvent(MotionEvent event) {
                if (GroupCreateActivity.this.currentDeletingSpan != null) {
                    GroupCreateActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    GroupCreateSpan unused = GroupCreateActivity.this.currentDeletingSpan = null;
                }
                if (event.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(event);
            }
        };
        this.editText = r72;
        r72.setTextSize(1, 16.0f);
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
        updateEditTextHint();
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
        this.editText.setOnEditorActionListener(new GroupCreateActivity$$ExternalSyntheticLambda5(this));
        this.editText.setOnKeyListener(new View.OnKeyListener() {
            private boolean wasEmpty;

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 67) {
                    boolean z = true;
                    if (event.getAction() == 0) {
                        if (GroupCreateActivity.this.editText.length() != 0) {
                            z = false;
                        }
                        this.wasEmpty = z;
                    } else if (event.getAction() == 1 && this.wasEmpty && !GroupCreateActivity.this.allSpans.isEmpty()) {
                        GroupCreateActivity.this.spansContainer.removeSpan((GroupCreateSpan) GroupCreateActivity.this.allSpans.get(GroupCreateActivity.this.allSpans.size() - 1));
                        GroupCreateActivity.this.updateHint();
                        GroupCreateActivity.this.checkVisibleRows();
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
                if (GroupCreateActivity.this.editText.length() != 0) {
                    if (!GroupCreateActivity.this.adapter.searching) {
                        boolean unused = GroupCreateActivity.this.searching = true;
                        boolean unused2 = GroupCreateActivity.this.searchWas = true;
                        GroupCreateActivity.this.adapter.setSearching(true);
                        GroupCreateActivity.this.itemDecoration.setSearching(true);
                        GroupCreateActivity.this.listView.setFastScrollVisible(false);
                        GroupCreateActivity.this.listView.setVerticalScrollBarEnabled(true);
                    }
                    GroupCreateActivity.this.adapter.searchDialogs(GroupCreateActivity.this.editText.getText().toString());
                    GroupCreateActivity.this.emptyView.showProgress(true, false);
                    return;
                }
                GroupCreateActivity.this.closeSearch();
            }
        });
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context2);
        flickerLoadingView.setViewType(6);
        flickerLoadingView.showDate(false);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context2, flickerLoadingView, 1);
        this.emptyView = stickerEmptyView;
        stickerEmptyView.addView(flickerLoadingView);
        this.emptyView.showProgress(true, false);
        this.emptyView.title.setText(LocaleController.getString("NoResult", NUM));
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
        RecyclerListView recyclerListView3 = this.listView;
        GroupCreateDividerItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        this.itemDecoration = groupCreateDividerItemDecoration;
        recyclerListView3.addItemDecoration(groupCreateDividerItemDecoration);
        frameLayout.addView(this.listView);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new GroupCreateActivity$$ExternalSyntheticLambda7(this, context2));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    GroupCreateActivity.this.editText.hideActionMode();
                    AndroidUtilities.hideKeyboard(GroupCreateActivity.this.editText);
                }
            }
        });
        this.listView.setAnimateEmptyView(true, 0);
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
        if (this.isNeverShare || this.isAlwaysShare || this.addToGroup) {
            this.floatingButton.setImageResource(NUM);
        } else {
            BackDrawable backDrawable = new BackDrawable(false);
            backDrawable.setArrowRotation(180);
            this.floatingButton.setImageDrawable(backDrawable);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            LinearLayoutManager linearLayoutManager2 = linearLayoutManager;
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButton.setStateListAnimator(animator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        frameLayout.addView(this.floatingButton);
        this.floatingButton.setOnClickListener(new GroupCreateActivity$$ExternalSyntheticLambda3(this));
        if (this.chatType != 2) {
            this.floatingButton.setVisibility(4);
            this.floatingButton.setScaleX(0.0f);
            this.floatingButton.setScaleY(0.0f);
            this.floatingButton.setAlpha(0.0f);
        }
        this.floatingButton.setContentDescription(LocaleController.getString("Next", NUM));
        updateHint();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-GroupCreateActivity  reason: not valid java name */
    public /* synthetic */ void m3569lambda$createView$0$orgtelegramuiGroupCreateActivity(View v) {
        this.editText.clearFocus();
        this.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.editText);
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-GroupCreateActivity  reason: not valid java name */
    public /* synthetic */ boolean m3570lambda$createView$1$orgtelegramuiGroupCreateActivity(TextView v, int actionId, KeyEvent event) {
        return actionId == 6 && onDonePressed(true);
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-GroupCreateActivity  reason: not valid java name */
    public /* synthetic */ void m3572lambda$createView$3$orgtelegramuiGroupCreateActivity(Context context, View view, int position) {
        long id;
        View view2 = view;
        if (position == 0 && this.adapter.inviteViaLink != 0 && !this.adapter.searching) {
            PermanentLinkBottomSheet permanentLinkBottomSheet = new PermanentLinkBottomSheet(context, false, this, this.info, this.chatId, this.channelId != 0);
            this.sharedLinkBottomSheet = permanentLinkBottomSheet;
            showDialog(permanentLinkBottomSheet);
        } else if (view2 instanceof GroupCreateUserCell) {
            GroupCreateUserCell cell = (GroupCreateUserCell) view2;
            Object object = cell.getObject();
            if (object instanceof TLRPC.User) {
                id = ((TLRPC.User) object).id;
            } else if (object instanceof TLRPC.Chat) {
                id = -((TLRPC.Chat) object).id;
            } else {
                return;
            }
            LongSparseArray<TLObject> longSparseArray = this.ignoreUsers;
            if (longSparseArray == null || longSparseArray.indexOfKey(id) < 0) {
                boolean z = this.selectedContacts.indexOfKey(id) >= 0;
                boolean exists = z;
                if (z) {
                    this.spansContainer.removeSpan(this.selectedContacts.get(id));
                } else if (this.maxCount != 0 && this.selectedContacts.size() == this.maxCount) {
                    return;
                } else {
                    if (this.chatType == 0 && this.selectedContacts.size() == getMessagesController().maxGroupCount) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setMessage(LocaleController.getString("SoftUserLimitAlert", NUM));
                        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                        showDialog(builder.create());
                        return;
                    }
                    if (object instanceof TLRPC.User) {
                        TLRPC.User user = (TLRPC.User) object;
                        if (this.addToGroup && user.bot) {
                            String str = "OK";
                            if (this.channelId == 0 && user.bot_nochats) {
                                try {
                                    BulletinFactory.of(this).createErrorBulletin(LocaleController.getString("BotCantJoinGroups", NUM)).show();
                                    return;
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                    return;
                                }
                            } else if (this.channelId != 0) {
                                TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.channelId));
                                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                                if (ChatObject.canAddAdmins(chat)) {
                                    builder2.setTitle(LocaleController.getString("AppName", NUM));
                                    builder2.setMessage(LocaleController.getString("AddBotAsAdmin", NUM));
                                    builder2.setPositiveButton(LocaleController.getString("MakeAdmin", NUM), new GroupCreateActivity$$ExternalSyntheticLambda0(this, user));
                                    builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                                } else {
                                    builder2.setMessage(LocaleController.getString("CantAddBotAsAdmin", NUM));
                                    builder2.setPositiveButton(LocaleController.getString(str, NUM), (DialogInterface.OnClickListener) null);
                                }
                                showDialog(builder2.create());
                                return;
                            }
                        }
                        getMessagesController().putUser(user, !this.searching);
                    } else {
                        getMessagesController().putChat((TLRPC.Chat) object, !this.searching);
                    }
                    GroupCreateSpan span = new GroupCreateSpan(this.editText.getContext(), object);
                    this.spansContainer.addSpan(span);
                    span.setOnClickListener(this);
                }
                updateHint();
                if (this.searching || this.searchWas) {
                    AndroidUtilities.showKeyboard(this.editText);
                } else {
                    cell.setChecked(!exists, true);
                }
                if (this.editText.length() > 0) {
                    this.editText.setText((CharSequence) null);
                }
            }
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-GroupCreateActivity  reason: not valid java name */
    public /* synthetic */ void m3571lambda$createView$2$orgtelegramuiGroupCreateActivity(TLRPC.User user, DialogInterface dialogInterface, int i) {
        this.delegate2.needAddBot(user);
        if (this.editText.length() > 0) {
            this.editText.setText((CharSequence) null);
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-GroupCreateActivity  reason: not valid java name */
    public /* synthetic */ void m3573lambda$createView$4$orgtelegramuiGroupCreateActivity(View v) {
        onDonePressed(true);
    }

    /* access modifiers changed from: private */
    public void updateEditTextHint() {
        GroupCreateAdapter groupCreateAdapter;
        EditTextBoldCursor editTextBoldCursor = this.editText;
        if (editTextBoldCursor != null) {
            if (this.chatType == 2) {
                editTextBoldCursor.setHintText(LocaleController.getString("AddMutual", NUM));
            } else if (this.addToGroup || ((groupCreateAdapter = this.adapter) != null && groupCreateAdapter.noContactsStubRow == 0)) {
                this.editText.setHintText(LocaleController.getString("SearchForPeople", NUM));
            } else if (this.isAlwaysShare || this.isNeverShare) {
                this.editText.setHintText(LocaleController.getString("SearchForPeopleAndGroups", NUM));
            } else {
                this.editText.setHintText(LocaleController.getString("SendMessageTo", NUM));
            }
        }
    }

    /* access modifiers changed from: private */
    public void showItemsAnimated(final int from) {
        if (!this.isPaused) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    GroupCreateActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int n = GroupCreateActivity.this.listView.getChildCount();
                    AnimatorSet animatorSet = new AnimatorSet();
                    for (int i = 0; i < n; i++) {
                        View child = GroupCreateActivity.this.listView.getChildAt(i);
                        if (GroupCreateActivity.this.listView.getChildAdapterPosition(child) >= from) {
                            child.setAlpha(0.0f);
                            ObjectAnimator a = ObjectAnimator.ofFloat(child, View.ALPHA, new float[]{0.0f, 1.0f});
                            a.setStartDelay((long) ((int) ((((float) Math.min(GroupCreateActivity.this.listView.getMeasuredHeight(), Math.max(0, child.getTop()))) / ((float) GroupCreateActivity.this.listView.getMeasuredHeight())) * 100.0f)));
                            a.setDuration(200);
                            animatorSet.playTogether(new Animator[]{a});
                        }
                    }
                    animatorSet.start();
                    return true;
                }
            });
        }
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.contactsDidLoad) {
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

    public void setIgnoreUsers(LongSparseArray<TLObject> users) {
        this.ignoreUsers = users;
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
    }

    public void setContainerHeight(int value) {
        this.containerHeight = value;
        int measuredH = Math.min(this.maxSize, this.measuredContainerHeight);
        int currentH = Math.min(this.maxSize, this.containerHeight);
        ScrollView scrollView2 = this.scrollView;
        scrollView2.scrollTo(0, Math.max(0, scrollView2.getScrollY() - (this.containerHeight - value)));
        this.listView.setTranslationY((float) (currentH - measuredH));
        this.fragmentView.invalidate();
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
                if (object instanceof TLRPC.User) {
                    id = ((TLRPC.User) object).id;
                } else if (object instanceof TLRPC.Chat) {
                    id = -((TLRPC.Chat) object).id;
                } else {
                    id = 0;
                }
                if (id != 0) {
                    LongSparseArray<TLObject> longSparseArray = this.ignoreUsers;
                    boolean z = false;
                    if (longSparseArray == null || longSparseArray.indexOfKey(id) < 0) {
                        if (this.selectedContacts.indexOfKey(id) >= 0) {
                            z = true;
                        }
                        cell.setChecked(z, true);
                        cell.setCheckBoxEnabled(true);
                    } else {
                        cell.setChecked(true, false);
                        cell.setCheckBoxEnabled(false);
                    }
                }
            }
        }
    }

    private void onAddToGroupDone(int count) {
        ArrayList<TLRPC.User> result = new ArrayList<>();
        for (int a = 0; a < this.selectedContacts.size(); a++) {
            result.add(getMessagesController().getUser(Long.valueOf(this.selectedContacts.keyAt(a))));
        }
        ContactsAddActivityDelegate contactsAddActivityDelegate = this.delegate2;
        if (contactsAddActivityDelegate != null) {
            contactsAddActivityDelegate.didSelectUsers(result, count);
        }
        finishFragment();
    }

    /* access modifiers changed from: private */
    public boolean onDonePressed(boolean alert) {
        String str;
        String str2;
        if (this.selectedContacts.size() == 0 && this.chatType != 2) {
            return false;
        }
        if (!alert || !this.addToGroup) {
            if (this.chatType == 2) {
                ArrayList<TLRPC.InputUser> result = new ArrayList<>();
                for (int a = 0; a < this.selectedContacts.size(); a++) {
                    TLRPC.InputUser user = getMessagesController().getInputUser(getMessagesController().getUser(Long.valueOf(this.selectedContacts.keyAt(a))));
                    if (user != null) {
                        result.add(user);
                    }
                }
                getMessagesController().addUsersToChannel(this.chatId, result, (BaseFragment) null);
                getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                Bundle args2 = new Bundle();
                args2.putLong("chat_id", this.chatId);
                presentFragment(new ChatActivity(args2), true);
            } else if (!this.doneButtonVisible || this.selectedContacts.size() == 0) {
                return false;
            } else {
                if (this.addToGroup) {
                    onAddToGroupDone(0);
                } else {
                    ArrayList<Long> result2 = new ArrayList<>();
                    for (int a2 = 0; a2 < this.selectedContacts.size(); a2++) {
                        result2.add(Long.valueOf(this.selectedContacts.keyAt(a2)));
                    }
                    if (this.isAlwaysShare != 0 || this.isNeverShare) {
                        GroupCreateActivityDelegate groupCreateActivityDelegate = this.delegate;
                        if (groupCreateActivityDelegate != null) {
                            groupCreateActivityDelegate.didSelectUsers(result2);
                        }
                        finishFragment();
                    } else {
                        Bundle args = new Bundle();
                        long[] array = new long[result2.size()];
                        for (int a3 = 0; a3 < array.length; a3++) {
                            array[a3] = result2.get(a3).longValue();
                        }
                        args.putLongArray("result", array);
                        args.putInt("chatType", this.chatType);
                        args.putBoolean("forImport", this.forImport);
                        presentFragment(new GroupCreateFinalActivity(args));
                    }
                }
            }
        } else if (getParentActivity() == null) {
            return false;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            if (this.selectedContacts.size() == 1) {
                builder.setTitle(LocaleController.getString("AddOneMemberAlertTitle", NUM));
            } else {
                builder.setTitle(LocaleController.formatString("AddMembersAlertTitle", NUM, LocaleController.formatPluralString("Members", this.selectedContacts.size(), new Object[0])));
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (int a4 = 0; a4 < this.selectedContacts.size(); a4++) {
                TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(this.selectedContacts.keyAt(a4)));
                if (user2 != null) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append("**");
                    stringBuilder.append(ContactsController.formatName(user2.first_name, user2.last_name));
                    stringBuilder.append("**");
                }
            }
            MessagesController messagesController = getMessagesController();
            long j = this.chatId;
            if (j == 0) {
                j = this.channelId;
            }
            TLRPC.Chat chat = messagesController.getChat(Long.valueOf(j));
            if (this.selectedContacts.size() > 5) {
                Object[] objArr = new Object[2];
                objArr[0] = LocaleController.formatPluralString("Members", this.selectedContacts.size(), new Object[0]);
                if (chat == null) {
                    str2 = "";
                } else {
                    str2 = chat.title;
                }
                objArr[1] = str2;
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", NUM, objArr)));
                String countString = String.format("%d", new Object[]{Integer.valueOf(this.selectedContacts.size())});
                int index = TextUtils.indexOf(spannableStringBuilder, countString);
                if (index >= 0) {
                    spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), index, countString.length() + index, 33);
                }
                builder.setMessage(spannableStringBuilder);
            } else {
                Object[] objArr2 = new Object[2];
                objArr2[0] = stringBuilder;
                if (chat == null) {
                    str = "";
                } else {
                    str = chat.title;
                }
                objArr2[1] = str;
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", NUM, objArr2)));
            }
            CheckBoxCell[] cells = new CheckBoxCell[1];
            if (!ChatObject.isChannel(chat)) {
                LinearLayout linearLayout = new LinearLayout(getParentActivity());
                linearLayout.setOrientation(1);
                cells[0] = new CheckBoxCell(getParentActivity(), 1);
                cells[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                cells[0].setMultiline(true);
                if (this.selectedContacts.size() == 1) {
                    cells[0].setText(AndroidUtilities.replaceTags(LocaleController.formatString("AddOneMemberForwardMessages", NUM, UserObject.getFirstName(getMessagesController().getUser(Long.valueOf(this.selectedContacts.keyAt(0)))))), "", true, false);
                } else {
                    cells[0].setText(LocaleController.getString("AddMembersForwardMessages", NUM), "", true, false);
                }
                cells[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                linearLayout.addView(cells[0], LayoutHelper.createLinear(-1, -2));
                cells[0].setOnClickListener(new GroupCreateActivity$$ExternalSyntheticLambda4(cells));
                builder.setCustomViewOffset(12);
                builder.setView(linearLayout);
            }
            builder.setPositiveButton(LocaleController.getString("Add", NUM), new GroupCreateActivity$$ExternalSyntheticLambda1(this, cells));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
        return true;
    }

    /* renamed from: lambda$onDonePressed$6$org-telegram-ui-GroupCreateActivity  reason: not valid java name */
    public /* synthetic */ void m3575lambda$onDonePressed$6$orgtelegramuiGroupCreateActivity(CheckBoxCell[] cells, DialogInterface dialogInterface, int i) {
        int i2 = 0;
        if (cells[0] != null && cells[0].isChecked()) {
            i2 = 100;
        }
        onAddToGroupDone(i2);
    }

    /* access modifiers changed from: private */
    public void closeSearch() {
        this.searching = false;
        this.searchWas = false;
        this.itemDecoration.setSearching(false);
        this.adapter.setSearching(false);
        this.adapter.searchDialogs((String) null);
        this.listView.setFastScrollVisible(true);
        this.listView.setVerticalScrollBarEnabled(false);
        showItemsAnimated(0);
    }

    /* access modifiers changed from: private */
    public void updateHint() {
        if (!this.isAlwaysShare && !this.isNeverShare && !this.addToGroup) {
            if (this.chatType == 2) {
                this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", this.selectedContacts.size(), new Object[0]));
            } else if (this.selectedContacts.size() == 0) {
                this.actionBar.setSubtitle(LocaleController.formatString("MembersCountZero", NUM, LocaleController.formatPluralString("Members", this.maxCount, new Object[0])));
            } else {
                String str = LocaleController.getPluralString("MembersCountSelected", this.selectedContacts.size());
                this.actionBar.setSubtitle(String.format(str, new Object[]{Integer.valueOf(this.selectedContacts.size()), Integer.valueOf(this.maxCount)}));
            }
        }
        if (this.chatType == 2) {
            return;
        }
        if (this.doneButtonVisible && this.allSpans.isEmpty()) {
            AnimatorSet animatorSet = this.currentDoneButtonAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.currentDoneButtonAnimation = animatorSet2;
            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingButton, View.ALPHA, new float[]{0.0f})});
            this.currentDoneButtonAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    GroupCreateActivity.this.floatingButton.setVisibility(4);
                }
            });
            this.currentDoneButtonAnimation.setDuration(180);
            this.currentDoneButtonAnimation.start();
            this.doneButtonVisible = false;
        } else if (!this.doneButtonVisible && !this.allSpans.isEmpty()) {
            AnimatorSet animatorSet3 = this.currentDoneButtonAnimation;
            if (animatorSet3 != null) {
                animatorSet3.cancel();
            }
            this.currentDoneButtonAnimation = new AnimatorSet();
            this.floatingButton.setVisibility(0);
            this.currentDoneButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButton, View.ALPHA, new float[]{1.0f})});
            this.currentDoneButtonAnimation.setDuration(180);
            this.currentDoneButtonAnimation.start();
            this.doneButtonVisible = true;
        }
    }

    public void setDelegate(GroupCreateActivityDelegate groupCreateActivityDelegate) {
        this.delegate = groupCreateActivityDelegate;
    }

    public void setDelegate(ContactsAddActivityDelegate contactsAddActivityDelegate) {
        this.delegate2 = contactsAddActivityDelegate;
    }

    public class GroupCreateAdapter extends RecyclerListView.FastScrollAdapter {
        private ArrayList<TLObject> contacts = new ArrayList<>();
        private Context context;
        private int currentItemsCount;
        /* access modifiers changed from: private */
        public int inviteViaLink;
        /* access modifiers changed from: private */
        public int noContactsStubRow;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<Object> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;
        /* access modifiers changed from: private */
        public boolean searching;
        private int usersStartRow;

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            GroupCreateActivity.this.updateEditTextHint();
        }

        public GroupCreateAdapter(Context ctx) {
            TLRPC.Chat chat;
            this.context = ctx;
            ArrayList<TLRPC.TL_contact> arrayList = GroupCreateActivity.this.getContactsController().contacts;
            for (int a = 0; a < arrayList.size(); a++) {
                TLRPC.User user = GroupCreateActivity.this.getMessagesController().getUser(Long.valueOf(arrayList.get(a).user_id));
                if (user != null && !user.self && !user.deleted) {
                    this.contacts.add(user);
                }
            }
            if (GroupCreateActivity.this.isNeverShare != 0 || GroupCreateActivity.this.isAlwaysShare) {
                ArrayList<TLRPC.Dialog> dialogs = GroupCreateActivity.this.getMessagesController().getAllDialogs();
                int N = dialogs.size();
                for (int a2 = 0; a2 < N; a2++) {
                    TLRPC.Dialog dialog = dialogs.get(a2);
                    if (DialogObject.isChatDialog(dialog.id) && (chat = GroupCreateActivity.this.getMessagesController().getChat(Long.valueOf(-dialog.id))) != null && chat.migrated_to == null && (!ChatObject.isChannel(chat) || chat.megagroup)) {
                        this.contacts.add(chat);
                    }
                }
                Collections.sort(this.contacts, new Comparator<TLObject>(GroupCreateActivity.this) {
                    private String getName(TLObject object) {
                        if (!(object instanceof TLRPC.User)) {
                            return ((TLRPC.Chat) object).title;
                        }
                        TLRPC.User user = (TLRPC.User) object;
                        return ContactsController.formatName(user.first_name, user.last_name);
                    }

                    public int compare(TLObject o1, TLObject o2) {
                        return getName(o1).compareTo(getName(o2));
                    }
                });
            }
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(false);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda4(this));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-GroupCreateActivity$GroupCreateAdapter  reason: not valid java name */
        public /* synthetic */ void m3576x6d31CLASSNAME(int searchId) {
            GroupCreateActivity.this.showItemsAnimated(this.currentItemsCount);
            if (this.searchRunnable == null && !this.searchAdapterHelper.isSearchInProgress() && getItemCount() == 0) {
                GroupCreateActivity.this.emptyView.showProgress(false, true);
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
            String firstName;
            String lastName;
            if (this.searching || position < this.usersStartRow) {
                return null;
            }
            int size = this.contacts.size();
            int i = this.usersStartRow;
            if (position >= size + i) {
                return null;
            }
            TLObject object = this.contacts.get(position - i);
            if (object instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) object;
                firstName = user.first_name;
                lastName = user.last_name;
            } else {
                firstName = ((TLRPC.Chat) object).title;
                lastName = "";
            }
            if (LocaleController.nameDisplayOrder == 1) {
                if (!TextUtils.isEmpty(firstName)) {
                    return firstName.substring(0, 1).toUpperCase();
                }
                if (!TextUtils.isEmpty(lastName)) {
                    return lastName.substring(0, 1).toUpperCase();
                }
                return "";
            } else if (!TextUtils.isEmpty(lastName)) {
                return lastName.substring(0, 1).toUpperCase();
            } else {
                if (!TextUtils.isEmpty(firstName)) {
                    return firstName.substring(0, 1).toUpperCase();
                }
                return "";
            }
        }

        public int getItemCount() {
            this.noContactsStubRow = -1;
            if (this.searching) {
                int count = this.searchResult.size();
                int localServerCount = this.searchAdapterHelper.getLocalServerSearch().size();
                int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
                int count2 = count + localServerCount;
                if (globalCount != 0) {
                    count2 += globalCount + 1;
                }
                this.currentItemsCount = count2;
                return count2;
            }
            int count3 = this.contacts.size();
            if (GroupCreateActivity.this.addToGroup) {
                if (GroupCreateActivity.this.chatId != 0) {
                    this.inviteViaLink = ChatObject.canUserDoAdminAction(GroupCreateActivity.this.getMessagesController().getChat(Long.valueOf(GroupCreateActivity.this.chatId)), 3) ? 1 : 0;
                } else if (GroupCreateActivity.this.channelId != 0) {
                    TLRPC.Chat chat = GroupCreateActivity.this.getMessagesController().getChat(Long.valueOf(GroupCreateActivity.this.channelId));
                    this.inviteViaLink = (!ChatObject.canUserDoAdminAction(chat, 3) || !TextUtils.isEmpty(chat.username)) ? 0 : 2;
                } else {
                    this.inviteViaLink = 0;
                }
                if (this.inviteViaLink != 0) {
                    this.usersStartRow = 1;
                    count3++;
                }
            }
            if (count3 == 0) {
                this.noContactsStubRow = 0;
                count3++;
            }
            this.currentItemsCount = count3;
            return count3;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new GroupCreateSectionCell(this.context);
                    break;
                case 1:
                    view = new GroupCreateUserCell(this.context, 1, 0, false);
                    break;
                case 3:
                    StickerEmptyView stickerEmptyView = new StickerEmptyView(this.context, (View) null, 0) {
                        /* access modifiers changed from: protected */
                        public void onAttachedToWindow() {
                            super.onAttachedToWindow();
                            this.stickerView.getImageReceiver().startAnimation();
                        }
                    };
                    stickerEmptyView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    stickerEmptyView.subtitle.setVisibility(8);
                    stickerEmptyView.title.setText(LocaleController.getString("NoContacts", NUM));
                    stickerEmptyView.setAnimateLayoutChange(true);
                    view = stickerEmptyView;
                    break;
                default:
                    view = new TextCell(this.context);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v0, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v2, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v31, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: java.lang.String} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:66:0x0165  */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x016b  */
        /* JADX WARNING: Removed duplicated region for block: B:73:0x017e  */
        /* JADX WARNING: Removed duplicated region for block: B:89:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r19, int r20) {
            /*
                r18 = this;
                r1 = r18
                r2 = r19
                r3 = r20
                int r0 = r19.getItemViewType()
                r4 = 0
                switch(r0) {
                    case 0: goto L_0x01b3;
                    case 1: goto L_0x0038;
                    case 2: goto L_0x0010;
                    default: goto L_0x000e;
                }
            L_0x000e:
                goto L_0x01c7
            L_0x0010:
                android.view.View r0 = r2.itemView
                org.telegram.ui.Cells.TextCell r0 = (org.telegram.ui.Cells.TextCell) r0
                int r5 = r1.inviteViaLink
                r6 = 2
                r7 = 2131165783(0x7var_, float:1.7945793E38)
                if (r5 != r6) goto L_0x002a
                r5 = 2131624906(0x7f0e03ca, float:1.8877005E38)
                java.lang.String r6 = "ChannelInviteViaLink"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r0.setTextAndIcon((java.lang.String) r5, (int) r7, (boolean) r4)
                goto L_0x01c7
            L_0x002a:
                r5 = 2131626280(0x7f0e0928, float:1.8879792E38)
                java.lang.String r6 = "InviteToGroupByLink"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r0.setTextAndIcon((java.lang.String) r5, (int) r7, (boolean) r4)
                goto L_0x01c7
            L_0x0038:
                android.view.View r0 = r2.itemView
                r5 = r0
                org.telegram.ui.Cells.GroupCreateUserCell r5 = (org.telegram.ui.Cells.GroupCreateUserCell) r5
                r6 = 0
                r7 = 0
                boolean r0 = r1.searching
                r8 = 1
                if (r0 == 0) goto L_0x014f
                java.util.ArrayList<java.lang.Object> r0 = r1.searchResult
                int r9 = r0.size()
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getGlobalSearch()
                int r10 = r0.size()
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getLocalServerSearch()
                int r11 = r0.size()
                if (r3 < 0) goto L_0x006c
                if (r3 >= r9) goto L_0x006c
                java.util.ArrayList<java.lang.Object> r0 = r1.searchResult
                java.lang.Object r0 = r0.get(r3)
                org.telegram.tgnet.TLObject r0 = (org.telegram.tgnet.TLObject) r0
                r12 = r0
                goto L_0x009f
            L_0x006c:
                if (r3 < r9) goto L_0x0082
                int r0 = r11 + r9
                if (r3 >= r0) goto L_0x0082
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getLocalServerSearch()
                int r12 = r3 - r9
                java.lang.Object r0 = r0.get(r12)
                org.telegram.tgnet.TLObject r0 = (org.telegram.tgnet.TLObject) r0
                r12 = r0
                goto L_0x009f
            L_0x0082:
                int r0 = r9 + r11
                if (r3 <= r0) goto L_0x009d
                int r0 = r10 + r9
                int r0 = r0 + r11
                if (r3 > r0) goto L_0x009d
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getGlobalSearch()
                int r12 = r3 - r9
                int r12 = r12 - r11
                int r12 = r12 - r8
                java.lang.Object r0 = r0.get(r12)
                org.telegram.tgnet.TLObject r0 = (org.telegram.tgnet.TLObject) r0
                r12 = r0
                goto L_0x009f
            L_0x009d:
                r0 = 0
                r12 = r0
            L_0x009f:
                if (r12 == 0) goto L_0x014a
                boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC.User
                if (r0 == 0) goto L_0x00ac
                r0 = r12
                org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
                java.lang.String r0 = r0.username
                r13 = r0
                goto L_0x00b2
            L_0x00ac:
                r0 = r12
                org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
                java.lang.String r0 = r0.username
                r13 = r0
            L_0x00b2:
                java.lang.String r0 = "@"
                if (r3 >= r9) goto L_0x00e6
                java.util.ArrayList<java.lang.CharSequence> r14 = r1.searchResultNames
                java.lang.Object r14 = r14.get(r3)
                r7 = r14
                java.lang.CharSequence r7 = (java.lang.CharSequence) r7
                if (r7 == 0) goto L_0x014e
                boolean r14 = android.text.TextUtils.isEmpty(r13)
                if (r14 != 0) goto L_0x014e
                java.lang.String r14 = r7.toString()
                java.lang.StringBuilder r15 = new java.lang.StringBuilder
                r15.<init>()
                r15.append(r0)
                r15.append(r13)
                java.lang.String r0 = r15.toString()
                boolean r0 = r14.startsWith(r0)
                if (r0 == 0) goto L_0x014e
                r0 = r7
                r6 = 0
                r7 = r6
                r6 = r0
                goto L_0x014e
            L_0x00e6:
                if (r3 <= r9) goto L_0x0147
                boolean r14 = android.text.TextUtils.isEmpty(r13)
                if (r14 != 0) goto L_0x0147
                org.telegram.ui.Adapters.SearchAdapterHelper r14 = r1.searchAdapterHelper
                java.lang.String r14 = r14.getLastFoundUsername()
                boolean r15 = r14.startsWith(r0)
                if (r15 == 0) goto L_0x00fe
                java.lang.String r14 = r14.substring(r8)
            L_0x00fe:
                android.text.SpannableStringBuilder r15 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x0141 }
                r15.<init>()     // Catch:{ Exception -> 0x0141 }
                r15.append(r0)     // Catch:{ Exception -> 0x0141 }
                r15.append(r13)     // Catch:{ Exception -> 0x0141 }
                int r0 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r13, r14)     // Catch:{ Exception -> 0x0141 }
                r16 = r0
                r4 = -1
                if (r0 == r4) goto L_0x013c
                int r0 = r14.length()     // Catch:{ Exception -> 0x0141 }
                if (r16 != 0) goto L_0x011d
                int r0 = r0 + 1
                r4 = r16
                goto L_0x0121
            L_0x011d:
                int r16 = r16 + 1
                r4 = r16
            L_0x0121:
                android.text.style.ForegroundColorSpan r8 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0141 }
                java.lang.String r16 = "windowBackgroundWhiteBlueText4"
                r17 = r6
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r16)     // Catch:{ Exception -> 0x013a }
                r8.<init>(r6)     // Catch:{ Exception -> 0x013a }
                int r6 = r4 + r0
                r16 = r0
                r0 = 33
                r15.setSpan(r8, r4, r6, r0)     // Catch:{ Exception -> 0x013a }
                r16 = r4
                goto L_0x013e
            L_0x013a:
                r0 = move-exception
                goto L_0x0144
            L_0x013c:
                r17 = r6
            L_0x013e:
                r0 = r15
                r6 = r0
                goto L_0x014e
            L_0x0141:
                r0 = move-exception
                r17 = r6
            L_0x0144:
                r4 = r13
                r6 = r4
                goto L_0x014e
            L_0x0147:
                r17 = r6
                goto L_0x014c
            L_0x014a:
                r17 = r6
            L_0x014c:
                r6 = r17
            L_0x014e:
                goto L_0x015e
            L_0x014f:
                r17 = r6
                java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r1.contacts
                int r4 = r1.usersStartRow
                int r4 = r3 - r4
                java.lang.Object r0 = r0.get(r4)
                r12 = r0
                org.telegram.tgnet.TLObject r12 = (org.telegram.tgnet.TLObject) r12
            L_0x015e:
                r5.setObject(r12, r7, r6)
                boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC.User
                if (r0 == 0) goto L_0x016b
                r0 = r12
                org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
                long r8 = r0.id
                goto L_0x0178
            L_0x016b:
                boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC.Chat
                if (r0 == 0) goto L_0x0176
                r0 = r12
                org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
                long r8 = r0.id
                long r8 = -r8
                goto L_0x0178
            L_0x0176:
                r8 = 0
            L_0x0178:
                r10 = 0
                int r0 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
                if (r0 == 0) goto L_0x01c7
                org.telegram.ui.GroupCreateActivity r0 = org.telegram.ui.GroupCreateActivity.this
                androidx.collection.LongSparseArray r0 = r0.ignoreUsers
                if (r0 == 0) goto L_0x019b
                org.telegram.ui.GroupCreateActivity r0 = org.telegram.ui.GroupCreateActivity.this
                androidx.collection.LongSparseArray r0 = r0.ignoreUsers
                int r0 = r0.indexOfKey(r8)
                if (r0 < 0) goto L_0x019b
                r4 = 0
                r10 = 1
                r5.setChecked(r10, r4)
                r5.setCheckBoxEnabled(r4)
                goto L_0x01c7
            L_0x019b:
                org.telegram.ui.GroupCreateActivity r0 = org.telegram.ui.GroupCreateActivity.this
                androidx.collection.LongSparseArray r0 = r0.selectedContacts
                int r0 = r0.indexOfKey(r8)
                if (r0 < 0) goto L_0x01a9
                r4 = 1
                goto L_0x01aa
            L_0x01a9:
                r4 = 0
            L_0x01aa:
                r10 = 0
                r5.setChecked(r4, r10)
                r4 = 1
                r5.setCheckBoxEnabled(r4)
                goto L_0x01c7
            L_0x01b3:
                android.view.View r0 = r2.itemView
                org.telegram.ui.Cells.GroupCreateSectionCell r0 = (org.telegram.ui.Cells.GroupCreateSectionCell) r0
                boolean r4 = r1.searching
                if (r4 == 0) goto L_0x01c7
                r4 = 2131626079(0x7f0e085f, float:1.8879384E38)
                java.lang.String r5 = "GlobalSearch"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setText(r4)
            L_0x01c7:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCreateActivity.GroupCreateAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int position) {
            if (this.searching) {
                if (position == this.searchResult.size() + this.searchAdapterHelper.getLocalServerSearch().size()) {
                    return 0;
                }
                return 1;
            } else if (this.inviteViaLink != 0 && position == 0) {
                return 2;
            } else {
                if (this.noContactsStubRow == position) {
                    return 3;
                }
                return 1;
            }
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
            if (GroupCreateActivity.this.ignoreUsers != null && (holder.itemView instanceof GroupCreateUserCell)) {
                Object object = ((GroupCreateUserCell) holder.itemView).getObject();
                if (!(object instanceof TLRPC.User) || GroupCreateActivity.this.ignoreUsers.indexOfKey(((TLRPC.User) object).id) < 0) {
                    return true;
                }
                return false;
            }
            return true;
        }

        public void searchDialogs(String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            this.searchResult.clear();
            this.searchResultNames.clear();
            this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
            this.searchAdapterHelper.queryServerSearch((String) null, true, GroupCreateActivity.this.isAlwaysShare || GroupCreateActivity.this.isNeverShare, false, false, false, 0, false, 0, 0);
            notifyDataSetChanged();
            if (!TextUtils.isEmpty(query)) {
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda2 groupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda2 = new GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda2(this, query);
                this.searchRunnable = groupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda2;
                dispatchQueue.postRunnable(groupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda2, 300);
            }
        }

        /* renamed from: lambda$searchDialogs$3$org-telegram-ui-GroupCreateActivity$GroupCreateAdapter  reason: not valid java name */
        public /* synthetic */ void m3579xd94b6953(String query) {
            AndroidUtilities.runOnUIThread(new GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda1(this, query));
        }

        /* renamed from: lambda$searchDialogs$2$org-telegram-ui-GroupCreateActivity$GroupCreateAdapter  reason: not valid java name */
        public /* synthetic */ void m3578xd8151674(String query) {
            this.searchAdapterHelper.queryServerSearch(query, true, GroupCreateActivity.this.isAlwaysShare || GroupCreateActivity.this.isNeverShare, true, false, false, 0, false, 0, 0);
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda0 groupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda0 = new GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda0(this, query);
            this.searchRunnable = groupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda0;
            dispatchQueue.postRunnable(groupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda0);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:36:0x00cc, code lost:
            if (r12.contains(" " + r3) != false) goto L_0x00dc;
         */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x012f A[LOOP:1: B:27:0x008e->B:52:0x012f, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x00e0 A[SYNTHETIC] */
        /* renamed from: lambda$searchDialogs$1$org-telegram-ui-GroupCreateActivity$GroupCreateAdapter  reason: not valid java name */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void m3577xd6deCLASSNAME(java.lang.String r19) {
            /*
                r18 = this;
                r0 = r18
                java.lang.String r1 = r19.trim()
                java.lang.String r1 = r1.toLowerCase()
                int r2 = r1.length()
                if (r2 != 0) goto L_0x001e
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                r0.updateSearchResults(r2, r3)
                return
            L_0x001e:
                org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r2 = r2.getTranslitString(r1)
                boolean r3 = r1.equals(r2)
                if (r3 != 0) goto L_0x0032
                int r3 = r2.length()
                if (r3 != 0) goto L_0x0033
            L_0x0032:
                r2 = 0
            L_0x0033:
                r3 = 0
                r4 = 1
                if (r2 == 0) goto L_0x0039
                r5 = 1
                goto L_0x003a
            L_0x0039:
                r5 = 0
            L_0x003a:
                int r5 = r5 + r4
                java.lang.String[] r5 = new java.lang.String[r5]
                r5[r3] = r1
                if (r2 == 0) goto L_0x0043
                r5[r4] = r2
            L_0x0043:
                java.util.ArrayList r6 = new java.util.ArrayList
                r6.<init>()
                java.util.ArrayList r7 = new java.util.ArrayList
                r7.<init>()
                r8 = 0
            L_0x004e:
                java.util.ArrayList<org.telegram.tgnet.TLObject> r9 = r0.contacts
                int r9 = r9.size()
                if (r8 >= r9) goto L_0x0141
                java.util.ArrayList<org.telegram.tgnet.TLObject> r9 = r0.contacts
                java.lang.Object r9 = r9.get(r8)
                org.telegram.tgnet.TLObject r9 = (org.telegram.tgnet.TLObject) r9
                boolean r10 = r9 instanceof org.telegram.tgnet.TLRPC.User
                if (r10 == 0) goto L_0x0074
                r10 = r9
                org.telegram.tgnet.TLRPC$User r10 = (org.telegram.tgnet.TLRPC.User) r10
                java.lang.String r11 = r10.first_name
                java.lang.String r12 = r10.last_name
                java.lang.String r11 = org.telegram.messenger.ContactsController.formatName(r11, r12)
                java.lang.String r11 = r11.toLowerCase()
                java.lang.String r10 = r10.username
                goto L_0x007c
            L_0x0074:
                r10 = r9
                org.telegram.tgnet.TLRPC$Chat r10 = (org.telegram.tgnet.TLRPC.Chat) r10
                java.lang.String r11 = r10.title
                java.lang.String r12 = r10.username
                r10 = r12
            L_0x007c:
                org.telegram.messenger.LocaleController r12 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r12 = r12.getTranslitString(r11)
                boolean r13 = r11.equals(r12)
                if (r13 == 0) goto L_0x008b
                r12 = 0
            L_0x008b:
                r13 = 0
                int r14 = r5.length
                r15 = 0
            L_0x008e:
                if (r15 >= r14) goto L_0x0137
                r3 = r5[r15]
                boolean r16 = r11.startsWith(r3)
                if (r16 != 0) goto L_0x00da
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r17 = r1
                java.lang.String r1 = " "
                r4.append(r1)
                r4.append(r3)
                java.lang.String r4 = r4.toString()
                boolean r4 = r11.contains(r4)
                if (r4 != 0) goto L_0x00dc
                if (r12 == 0) goto L_0x00cf
                boolean r4 = r12.startsWith(r3)
                if (r4 != 0) goto L_0x00dc
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r1)
                r4.append(r3)
                java.lang.String r1 = r4.toString()
                boolean r1 = r12.contains(r1)
                if (r1 == 0) goto L_0x00cf
                goto L_0x00dc
            L_0x00cf:
                if (r10 == 0) goto L_0x00de
                boolean r1 = r10.startsWith(r3)
                if (r1 == 0) goto L_0x00de
                r1 = 2
                r13 = r1
                goto L_0x00de
            L_0x00da:
                r17 = r1
            L_0x00dc:
                r1 = 1
                r13 = r1
            L_0x00de:
                if (r13 == 0) goto L_0x012f
                r1 = 0
                r4 = 1
                if (r13 != r4) goto L_0x0104
                boolean r14 = r9 instanceof org.telegram.tgnet.TLRPC.User
                if (r14 == 0) goto L_0x00f7
                r1 = r9
                org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
                java.lang.String r14 = r1.first_name
                java.lang.String r15 = r1.last_name
                java.lang.CharSequence r14 = org.telegram.messenger.AndroidUtilities.generateSearchName(r14, r15, r3)
                r7.add(r14)
                goto L_0x012b
            L_0x00f7:
                r14 = r9
                org.telegram.tgnet.TLRPC$Chat r14 = (org.telegram.tgnet.TLRPC.Chat) r14
                java.lang.String r15 = r14.title
                java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.generateSearchName(r15, r1, r3)
                r7.add(r1)
                goto L_0x012b
            L_0x0104:
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r14.<init>()
                java.lang.String r15 = "@"
                r14.append(r15)
                r14.append(r10)
                java.lang.String r14 = r14.toString()
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r15)
                r4.append(r3)
                java.lang.String r4 = r4.toString()
                java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.generateSearchName(r14, r1, r4)
                r7.add(r1)
            L_0x012b:
                r6.add(r9)
                goto L_0x0139
            L_0x012f:
                int r15 = r15 + 1
                r1 = r17
                r3 = 0
                r4 = 1
                goto L_0x008e
            L_0x0137:
                r17 = r1
            L_0x0139:
                int r8 = r8 + 1
                r1 = r17
                r3 = 0
                r4 = 1
                goto L_0x004e
            L_0x0141:
                r0.updateSearchResults(r6, r7)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCreateActivity.GroupCreateAdapter.m3577xd6deCLASSNAME(java.lang.String):void");
        }

        private void updateSearchResults(ArrayList<Object> users, ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda3(this, users, names));
        }

        /* renamed from: lambda$updateSearchResults$4$org-telegram-ui-GroupCreateActivity$GroupCreateAdapter  reason: not valid java name */
        public /* synthetic */ void m3580x892cvar_(ArrayList users, ArrayList names) {
            if (this.searching) {
                this.searchRunnable = null;
                this.searchResult = users;
                this.searchResultNames = names;
                this.searchAdapterHelper.mergeResults(users);
                GroupCreateActivity.this.showItemsAnimated(this.currentItemsCount);
                notifyDataSetChanged();
                if (this.searching && !this.searchAdapterHelper.isSearchInProgress() && getItemCount() == 0) {
                    GroupCreateActivity.this.emptyView.showProgress(false, true);
                }
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new GroupCreateActivity$$ExternalSyntheticLambda6(this);
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
        themeDescriptions.add(new ThemeDescription(this.emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        PermanentLinkBottomSheet permanentLinkBottomSheet = this.sharedLinkBottomSheet;
        if (permanentLinkBottomSheet != null) {
            themeDescriptions.addAll(permanentLinkBottomSheet.getThemeDescriptions());
        }
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$7$org-telegram-ui-GroupCreateActivity  reason: not valid java name */
    public /* synthetic */ void m3574x175d62aa() {
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
