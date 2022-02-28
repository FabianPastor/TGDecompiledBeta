package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
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
import android.text.style.ForegroundColorSpan;
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
import androidx.annotation.Keep;
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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$InputUser;
import org.telegram.tgnet.TLRPC$TL_contact;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
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
    private TLRPC$ChatFull info;
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

    public interface ContactsAddActivityDelegate {

        /* renamed from: org.telegram.ui.GroupCreateActivity$ContactsAddActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$needAddBot(ContactsAddActivityDelegate contactsAddActivityDelegate, TLRPC$User tLRPC$User) {
            }
        }

        void didSelectUsers(ArrayList<TLRPC$User> arrayList, int i);

        void needAddBot(TLRPC$User tLRPC$User);
    }

    public interface GroupCreateActivityDelegate {
        void didSelectUsers(ArrayList<Long> arrayList);
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
        public void onMeasure(int i, int i2) {
            int i3;
            boolean z;
            char c;
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
                                c = 0;
                                this.animators.add(ObjectAnimator.ofFloat(childAt, "translationX", new float[]{f}));
                            } else {
                                c = 0;
                            }
                            float f2 = (float) dp2;
                            if (childAt.getTranslationY() != f2) {
                                ArrayList<Animator> arrayList = this.animators;
                                float[] fArr = new float[1];
                                fArr[c] = f2;
                                arrayList.add(ObjectAnimator.ofFloat(childAt, "translationY", fArr));
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
            GroupCreateActivity.this.editText.measure(View.MeasureSpec.makeMeasureSpec(dp - i4, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
            if (!this.animationStarted) {
                int dp5 = dp3 + AndroidUtilities.dp(42.0f);
                int dp6 = i4 + AndroidUtilities.dp(16.0f);
                int unused = GroupCreateActivity.this.fieldY = dp2;
                if (GroupCreateActivity.this.currentAnimation != null) {
                    int dp7 = dp2 + AndroidUtilities.dp(42.0f);
                    if (GroupCreateActivity.this.containerHeight != dp7) {
                        this.animators.add(ObjectAnimator.ofInt(GroupCreateActivity.this, "containerHeight", new int[]{dp7}));
                    }
                    GroupCreateActivity groupCreateActivity = GroupCreateActivity.this;
                    int unused2 = groupCreateActivity.measuredContainerHeight = Math.max(groupCreateActivity.containerHeight, dp7);
                    float f3 = (float) dp6;
                    if (GroupCreateActivity.this.editText.getTranslationX() != f3) {
                        this.animators.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, "translationX", new float[]{f3}));
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
                        public void onAnimationEnd(Animator animator) {
                            GroupCreateActivity.this.getNotificationCenter().onAnimationFinish(SpansContainer.this.animationIndex);
                            SpansContainer.this.requestLayout();
                        }
                    });
                    this.animationIndex = GroupCreateActivity.this.getNotificationCenter().setAnimationInProgress(this.animationIndex, (int[]) null);
                    GroupCreateActivity.this.currentAnimation.start();
                    this.animationStarted = true;
                } else {
                    GroupCreateActivity groupCreateActivity2 = GroupCreateActivity.this;
                    int unused3 = groupCreateActivity2.measuredContainerHeight = groupCreateActivity2.containerHeight = dp5;
                    GroupCreateActivity.this.editText.setTranslationX((float) dp6);
                    GroupCreateActivity.this.editText.setTranslationY((float) GroupCreateActivity.this.fieldY);
                }
            } else if (GroupCreateActivity.this.currentAnimation != null && !GroupCreateActivity.this.ignoreScrollEvent && this.removingSpan == null) {
                GroupCreateActivity.this.editText.bringPointIntoView(GroupCreateActivity.this.editText.getSelectionStart());
            }
            setMeasuredDimension(size, GroupCreateActivity.this.measuredContainerHeight);
            GroupCreateActivity.this.listView.setTranslationY(0.0f);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int childCount = getChildCount();
            for (int i5 = 0; i5 < childCount; i5++) {
                View childAt = getChildAt(i5);
                childAt.layout(0, 0, childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan groupCreateSpan) {
            GroupCreateActivity.this.allSpans.add(groupCreateSpan);
            GroupCreateActivity.this.selectedContacts.put(groupCreateSpan.getUid(), groupCreateSpan);
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
            this.addingSpan = groupCreateSpan;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.SCALE_X, new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.SCALE_Y, new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.ALPHA, new float[]{0.0f, 1.0f}));
            addView(groupCreateSpan);
        }

        public void removeSpan(final GroupCreateSpan groupCreateSpan) {
            boolean unused = GroupCreateActivity.this.ignoreScrollEvent = true;
            GroupCreateActivity.this.selectedContacts.remove(groupCreateSpan.getUid());
            GroupCreateActivity.this.allSpans.remove(groupCreateSpan);
            groupCreateSpan.setOnClickListener((View.OnClickListener) null);
            if (GroupCreateActivity.this.currentAnimation != null) {
                GroupCreateActivity.this.currentAnimation.setupEndValues();
                GroupCreateActivity.this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            AnimatorSet unused2 = GroupCreateActivity.this.currentAnimation = new AnimatorSet();
            GroupCreateActivity.this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(groupCreateSpan);
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
            this.removingSpan = groupCreateSpan;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.SCALE_X, new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.SCALE_Y, new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.ALPHA, new float[]{1.0f, 0.0f}));
            requestLayout();
        }
    }

    public GroupCreateActivity() {
    }

    public GroupCreateActivity(Bundle bundle) {
        super(bundle);
        this.chatType = bundle.getInt("chatType", 0);
        this.forImport = bundle.getBoolean("forImport", false);
        this.isAlwaysShare = bundle.getBoolean("isAlwaysShare", false);
        this.isNeverShare = bundle.getBoolean("isNeverShare", false);
        this.addToGroup = bundle.getBoolean("addToGroup", false);
        this.chatAddType = bundle.getInt("chatAddType", 0);
        this.chatId = bundle.getLong("chatId");
        this.channelId = bundle.getLong("channelId");
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

    public View createView(Context context) {
        String str;
        int i;
        this.searching = false;
        this.searchWas = false;
        this.allSpans.clear();
        this.selectedContacts.clear();
        this.currentDeletingSpan = null;
        this.doneButtonVisible = this.chatType == 2;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        int i2 = this.chatType;
        if (i2 == 2) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAddSubscribers", NUM));
        } else if (this.addToGroup) {
            if (this.channelId != 0) {
                this.actionBar.setTitle(LocaleController.getString("ChannelAddSubscribers", NUM));
            } else {
                this.actionBar.setTitle(LocaleController.getString("GroupAddMembers", NUM));
            }
        } else if (this.isAlwaysShare) {
            int i3 = this.chatAddType;
            if (i3 == 2) {
                this.actionBar.setTitle(LocaleController.getString("FilterAlwaysShow", NUM));
            } else if (i3 == 1) {
                this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", NUM));
            } else {
                this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", NUM));
            }
        } else if (this.isNeverShare) {
            int i4 = this.chatAddType;
            if (i4 == 2) {
                this.actionBar.setTitle(LocaleController.getString("FilterNeverShow", NUM));
            } else if (i4 == 1) {
                this.actionBar.setTitle(LocaleController.getString("NeverAllow", NUM));
            } else {
                this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", NUM));
            }
        } else {
            ActionBar actionBar = this.actionBar;
            if (i2 == 0) {
                i = NUM;
                str = "NewGroup";
            } else {
                i = NUM;
                str = "NewBroadcastList";
            }
            actionBar.setTitle(LocaleController.getString(str, i));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    GroupCreateActivity.this.finishFragment();
                } else if (i == 1) {
                    boolean unused = GroupCreateActivity.this.onDonePressed(true);
                }
            }
        });
        AnonymousClass2 r2 = new ViewGroup(context) {
            private VerticalPositionAutoAnimator verticalPositionAutoAnimator;

            public void onViewAdded(View view) {
                if (view == GroupCreateActivity.this.floatingButton && this.verticalPositionAutoAnimator == null) {
                    this.verticalPositionAutoAnimator = VerticalPositionAutoAnimator.attach(view);
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
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                float f = 56.0f;
                if (AndroidUtilities.isTablet() || size2 > size) {
                    GroupCreateActivity.this.maxSize = AndroidUtilities.dp(144.0f);
                } else {
                    GroupCreateActivity.this.maxSize = AndroidUtilities.dp(56.0f);
                }
                GroupCreateActivity.this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(GroupCreateActivity.this.maxSize, Integer.MIN_VALUE));
                GroupCreateActivity.this.listView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2 - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
                GroupCreateActivity.this.emptyView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2 - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
                if (GroupCreateActivity.this.floatingButton != null) {
                    if (Build.VERSION.SDK_INT < 21) {
                        f = 60.0f;
                    }
                    int dp = AndroidUtilities.dp(f);
                    GroupCreateActivity.this.floatingButton.measure(View.MeasureSpec.makeMeasureSpec(dp, NUM), View.MeasureSpec.makeMeasureSpec(dp, NUM));
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                GroupCreateActivity.this.scrollView.layout(0, 0, GroupCreateActivity.this.scrollView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight());
                GroupCreateActivity.this.listView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.listView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.listView.getMeasuredHeight());
                GroupCreateActivity.this.emptyView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.emptyView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.emptyView.getMeasuredHeight());
                if (GroupCreateActivity.this.floatingButton != null) {
                    int dp = LocaleController.isRTL ? AndroidUtilities.dp(14.0f) : ((i3 - i) - AndroidUtilities.dp(14.0f)) - GroupCreateActivity.this.floatingButton.getMeasuredWidth();
                    int dp2 = ((i4 - i2) - AndroidUtilities.dp(14.0f)) - GroupCreateActivity.this.floatingButton.getMeasuredHeight();
                    GroupCreateActivity.this.floatingButton.layout(dp, dp2, GroupCreateActivity.this.floatingButton.getMeasuredWidth() + dp, GroupCreateActivity.this.floatingButton.getMeasuredHeight() + dp2);
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                ActionBarLayout access$1700 = GroupCreateActivity.this.parentLayout;
                GroupCreateActivity groupCreateActivity = GroupCreateActivity.this;
                access$1700.drawHeaderShadow(canvas, Math.min(groupCreateActivity.maxSize, (groupCreateActivity.measuredContainerHeight + GroupCreateActivity.this.containerHeight) - GroupCreateActivity.this.measuredContainerHeight));
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (view == GroupCreateActivity.this.listView) {
                    canvas.save();
                    int left = view.getLeft();
                    GroupCreateActivity groupCreateActivity = GroupCreateActivity.this;
                    canvas.clipRect(left, Math.min(groupCreateActivity.maxSize, (groupCreateActivity.measuredContainerHeight + GroupCreateActivity.this.containerHeight) - GroupCreateActivity.this.measuredContainerHeight), view.getRight(), view.getBottom());
                    boolean drawChild = super.drawChild(canvas, view, j);
                    canvas.restore();
                    return drawChild;
                } else if (view != GroupCreateActivity.this.scrollView) {
                    return super.drawChild(canvas, view, j);
                } else {
                    canvas.save();
                    int left2 = view.getLeft();
                    int top = view.getTop();
                    int right = view.getRight();
                    GroupCreateActivity groupCreateActivity2 = GroupCreateActivity.this;
                    canvas.clipRect(left2, top, right, Math.min(groupCreateActivity2.maxSize, (groupCreateActivity2.measuredContainerHeight + GroupCreateActivity.this.containerHeight) - GroupCreateActivity.this.measuredContainerHeight));
                    boolean drawChild2 = super.drawChild(canvas, view, j);
                    canvas.restore();
                    return drawChild2;
                }
            }
        };
        this.fragmentView = r2;
        ViewGroup viewGroup = r2;
        viewGroup.setFocusableInTouchMode(true);
        viewGroup.setDescendantFocusability(131072);
        AnonymousClass3 r5 = new ScrollView(context) {
            public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                if (GroupCreateActivity.this.ignoreScrollEvent) {
                    boolean unused = GroupCreateActivity.this.ignoreScrollEvent = false;
                    return false;
                }
                rect.offset(view.getLeft() - view.getScrollX(), view.getTop() - view.getScrollY());
                rect.top += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(20.0f);
                rect.bottom += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(50.0f);
                return super.requestChildRectangleOnScreen(view, rect, z);
            }
        };
        this.scrollView = r5;
        r5.setClipChildren(false);
        viewGroup.setClipChildren(false);
        this.scrollView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("windowBackgroundWhite"));
        viewGroup.addView(this.scrollView);
        SpansContainer spansContainer2 = new SpansContainer(context);
        this.spansContainer = spansContainer2;
        this.scrollView.addView(spansContainer2, LayoutHelper.createFrame(-1, -2.0f));
        this.spansContainer.setOnClickListener(new GroupCreateActivity$$ExternalSyntheticLambda2(this));
        AnonymousClass4 r52 = new EditTextBoldCursor(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (GroupCreateActivity.this.currentDeletingSpan != null) {
                    GroupCreateActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    GroupCreateSpan unused = GroupCreateActivity.this.currentDeletingSpan = null;
                }
                if (motionEvent.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.editText = r52;
        r52.setTextSize(1, 16.0f);
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
        this.editText.setOnEditorActionListener(new GroupCreateActivity$$ExternalSyntheticLambda5(this));
        this.editText.setOnKeyListener(new View.OnKeyListener() {
            private boolean wasEmpty;

            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == 67) {
                    boolean z = true;
                    if (keyEvent.getAction() == 0) {
                        if (GroupCreateActivity.this.editText.length() != 0) {
                            z = false;
                        }
                        this.wasEmpty = z;
                    } else if (keyEvent.getAction() == 1 && this.wasEmpty && !GroupCreateActivity.this.allSpans.isEmpty()) {
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
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
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        flickerLoadingView.setViewType(6);
        flickerLoadingView.showDate(false);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, flickerLoadingView, 1);
        this.emptyView = stickerEmptyView;
        stickerEmptyView.addView(flickerLoadingView);
        this.emptyView.showProgress(true, false);
        this.emptyView.title.setText(LocaleController.getString("NoResult", NUM));
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
        RecyclerListView recyclerListView3 = this.listView;
        GroupCreateDividerItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        this.itemDecoration = groupCreateDividerItemDecoration;
        recyclerListView3.addItemDecoration(groupCreateDividerItemDecoration);
        viewGroup.addView(this.listView);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new GroupCreateActivity$$ExternalSyntheticLambda7(this, context));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    GroupCreateActivity.this.editText.hideActionMode();
                    AndroidUtilities.hideKeyboard(GroupCreateActivity.this.editText);
                }
            }
        });
        this.listView.setAnimateEmptyView(true, 0);
        ImageView imageView = new ImageView(context);
        this.floatingButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        int i5 = Build.VERSION.SDK_INT;
        if (i5 < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
        if (this.isNeverShare || this.isAlwaysShare || this.addToGroup) {
            this.floatingButton.setImageResource(NUM);
        } else {
            BackDrawable backDrawable = new BackDrawable(false);
            backDrawable.setArrowRotation(180);
            this.floatingButton.setImageDrawable(backDrawable);
        }
        if (i5 >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButton.setStateListAnimator(stateListAnimator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider(this) {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        viewGroup.addView(this.floatingButton);
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view) {
        this.editText.clearFocus();
        this.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.editText);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$1(TextView textView, int i, KeyEvent keyEvent) {
        return i == 6 && onDonePressed(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(Context context, View view, int i) {
        long j;
        boolean z = false;
        if (i == 0 && this.adapter.inviteViaLink != 0 && !this.adapter.searching) {
            PermanentLinkBottomSheet permanentLinkBottomSheet = new PermanentLinkBottomSheet(context, false, this, this.info, this.chatId, this.channelId != 0);
            this.sharedLinkBottomSheet = permanentLinkBottomSheet;
            showDialog(permanentLinkBottomSheet);
        } else if (view instanceof GroupCreateUserCell) {
            GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) view;
            Object object = groupCreateUserCell.getObject();
            boolean z2 = object instanceof TLRPC$User;
            if (z2) {
                j = ((TLRPC$User) object).id;
            } else if (object instanceof TLRPC$Chat) {
                j = -((TLRPC$Chat) object).id;
            } else {
                return;
            }
            LongSparseArray<TLObject> longSparseArray = this.ignoreUsers;
            if (longSparseArray == null || longSparseArray.indexOfKey(j) < 0) {
                if (this.selectedContacts.indexOfKey(j) >= 0) {
                    z = true;
                }
                if (z) {
                    this.spansContainer.removeSpan(this.selectedContacts.get(j));
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
                    if (z2) {
                        TLRPC$User tLRPC$User = (TLRPC$User) object;
                        if (this.addToGroup && tLRPC$User.bot) {
                            long j2 = this.channelId;
                            if (j2 == 0 && tLRPC$User.bot_nochats) {
                                try {
                                    BulletinFactory.of(this).createErrorBulletin(LocaleController.getString("BotCantJoinGroups", NUM)).show();
                                    return;
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                    return;
                                }
                            } else if (j2 != 0) {
                                TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(this.channelId));
                                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                                if (ChatObject.canAddAdmins(chat)) {
                                    builder2.setTitle(LocaleController.getString("AppName", NUM));
                                    builder2.setMessage(LocaleController.getString("AddBotAsAdmin", NUM));
                                    builder2.setPositiveButton(LocaleController.getString("MakeAdmin", NUM), new GroupCreateActivity$$ExternalSyntheticLambda0(this, tLRPC$User));
                                    builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                                } else {
                                    builder2.setMessage(LocaleController.getString("CantAddBotAsAdmin", NUM));
                                    builder2.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                                }
                                showDialog(builder2.create());
                                return;
                            }
                        }
                        getMessagesController().putUser(tLRPC$User, !this.searching);
                    } else {
                        getMessagesController().putChat((TLRPC$Chat) object, !this.searching);
                    }
                    GroupCreateSpan groupCreateSpan = new GroupCreateSpan(this.editText.getContext(), object);
                    this.spansContainer.addSpan(groupCreateSpan);
                    groupCreateSpan.setOnClickListener(this);
                }
                updateHint();
                if (this.searching || this.searchWas) {
                    AndroidUtilities.showKeyboard(this.editText);
                } else {
                    groupCreateUserCell.setChecked(!z, true);
                }
                if (this.editText.length() > 0) {
                    this.editText.setText((CharSequence) null);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
        this.delegate2.needAddBot(tLRPC$User);
        if (this.editText.length() > 0) {
            this.editText.setText((CharSequence) null);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(View view) {
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
    public void showItemsAnimated(final int i) {
        if (!this.isPaused) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    GroupCreateActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int childCount = GroupCreateActivity.this.listView.getChildCount();
                    AnimatorSet animatorSet = new AnimatorSet();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = GroupCreateActivity.this.listView.getChildAt(i);
                        if (GroupCreateActivity.this.listView.getChildAdapterPosition(childAt) >= i) {
                            childAt.setAlpha(0.0f);
                            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt, View.ALPHA, new float[]{0.0f, 1.0f});
                            ofFloat.setStartDelay((long) ((int) ((((float) Math.min(GroupCreateActivity.this.listView.getMeasuredHeight(), Math.max(0, childAt.getTop()))) / ((float) GroupCreateActivity.this.listView.getMeasuredHeight())) * 100.0f)));
                            ofFloat.setDuration(200);
                            animatorSet.playTogether(new Animator[]{ofFloat});
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.contactsDidLoad) {
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

    public void setIgnoreUsers(LongSparseArray<TLObject> longSparseArray) {
        this.ignoreUsers = longSparseArray;
    }

    public void setInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
    }

    @Keep
    public void setContainerHeight(int i) {
        int i2 = this.containerHeight - i;
        this.containerHeight = i;
        int min = Math.min(this.maxSize, this.measuredContainerHeight);
        int min2 = Math.min(this.maxSize, this.containerHeight);
        ScrollView scrollView2 = this.scrollView;
        scrollView2.scrollTo(0, Math.max(0, scrollView2.getScrollY() - i2));
        this.listView.setTranslationY((float) (min2 - min));
        this.fragmentView.invalidate();
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
                if (object instanceof TLRPC$User) {
                    j = ((TLRPC$User) object).id;
                } else {
                    j = object instanceof TLRPC$Chat ? -((TLRPC$Chat) object).id : 0;
                }
                if (j != 0) {
                    LongSparseArray<TLObject> longSparseArray = this.ignoreUsers;
                    if (longSparseArray == null || longSparseArray.indexOfKey(j) < 0) {
                        groupCreateUserCell.setChecked(this.selectedContacts.indexOfKey(j) >= 0, true);
                        groupCreateUserCell.setCheckBoxEnabled(true);
                    } else {
                        groupCreateUserCell.setChecked(true, false);
                        groupCreateUserCell.setCheckBoxEnabled(false);
                    }
                }
            }
        }
    }

    private void onAddToGroupDone(int i) {
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.selectedContacts.size(); i2++) {
            arrayList.add(getMessagesController().getUser(Long.valueOf(this.selectedContacts.keyAt(i2))));
        }
        ContactsAddActivityDelegate contactsAddActivityDelegate = this.delegate2;
        if (contactsAddActivityDelegate != null) {
            contactsAddActivityDelegate.didSelectUsers(arrayList, i);
        }
        finishFragment();
    }

    /* access modifiers changed from: private */
    public boolean onDonePressed(boolean z) {
        String str;
        String str2;
        if (this.selectedContacts.size() == 0 && this.chatType != 2) {
            return false;
        }
        if (!z || !this.addToGroup) {
            if (this.chatType == 2) {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < this.selectedContacts.size(); i++) {
                    TLRPC$InputUser inputUser = getMessagesController().getInputUser(getMessagesController().getUser(Long.valueOf(this.selectedContacts.keyAt(i))));
                    if (inputUser != null) {
                        arrayList.add(inputUser);
                    }
                }
                getMessagesController().addUsersToChannel(this.chatId, arrayList, (BaseFragment) null);
                getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                Bundle bundle = new Bundle();
                bundle.putLong("chat_id", this.chatId);
                presentFragment(new ChatActivity(bundle), true);
            } else if (!this.doneButtonVisible || this.selectedContacts.size() == 0) {
                return false;
            } else {
                if (this.addToGroup) {
                    onAddToGroupDone(0);
                } else {
                    ArrayList arrayList2 = new ArrayList();
                    for (int i2 = 0; i2 < this.selectedContacts.size(); i2++) {
                        arrayList2.add(Long.valueOf(this.selectedContacts.keyAt(i2)));
                    }
                    if (this.isAlwaysShare || this.isNeverShare) {
                        GroupCreateActivityDelegate groupCreateActivityDelegate = this.delegate;
                        if (groupCreateActivityDelegate != null) {
                            groupCreateActivityDelegate.didSelectUsers(arrayList2);
                        }
                        finishFragment();
                    } else {
                        Bundle bundle2 = new Bundle();
                        int size = arrayList2.size();
                        long[] jArr = new long[size];
                        for (int i3 = 0; i3 < size; i3++) {
                            jArr[i3] = ((Long) arrayList2.get(i3)).longValue();
                        }
                        bundle2.putLongArray("result", jArr);
                        bundle2.putInt("chatType", this.chatType);
                        bundle2.putBoolean("forImport", this.forImport);
                        presentFragment(new GroupCreateFinalActivity(bundle2));
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
                builder.setTitle(LocaleController.formatString("AddMembersAlertTitle", NUM, LocaleController.formatPluralString("Members", this.selectedContacts.size())));
            }
            StringBuilder sb = new StringBuilder();
            for (int i4 = 0; i4 < this.selectedContacts.size(); i4++) {
                TLRPC$User user = getMessagesController().getUser(Long.valueOf(this.selectedContacts.keyAt(i4)));
                if (user != null) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append("**");
                    sb.append(ContactsController.formatName(user.first_name, user.last_name));
                    sb.append("**");
                }
            }
            MessagesController messagesController = getMessagesController();
            long j = this.chatId;
            if (j == 0) {
                j = this.channelId;
            }
            TLRPC$Chat chat = messagesController.getChat(Long.valueOf(j));
            if (this.selectedContacts.size() > 5) {
                Object[] objArr = new Object[2];
                objArr[0] = LocaleController.formatPluralString("Members", this.selectedContacts.size());
                if (chat == null) {
                    str2 = "";
                } else {
                    str2 = chat.title;
                }
                objArr[1] = str2;
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", NUM, objArr)));
                String format = String.format("%d", new Object[]{Integer.valueOf(this.selectedContacts.size())});
                int indexOf = TextUtils.indexOf(spannableStringBuilder, format);
                if (indexOf >= 0) {
                    spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), indexOf, format.length() + indexOf, 33);
                }
                builder.setMessage(spannableStringBuilder);
            } else {
                Object[] objArr2 = new Object[2];
                objArr2[0] = sb;
                if (chat == null) {
                    str = "";
                } else {
                    str = chat.title;
                }
                objArr2[1] = str;
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", NUM, objArr2)));
            }
            CheckBoxCell[] checkBoxCellArr = new CheckBoxCell[1];
            if (!ChatObject.isChannel(chat)) {
                LinearLayout linearLayout = new LinearLayout(getParentActivity());
                linearLayout.setOrientation(1);
                checkBoxCellArr[0] = new CheckBoxCell(getParentActivity(), 1);
                checkBoxCellArr[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                checkBoxCellArr[0].setMultiline(true);
                if (this.selectedContacts.size() == 1) {
                    checkBoxCellArr[0].setText(AndroidUtilities.replaceTags(LocaleController.formatString("AddOneMemberForwardMessages", NUM, UserObject.getFirstName(getMessagesController().getUser(Long.valueOf(this.selectedContacts.keyAt(0)))))), "", true, false);
                } else {
                    checkBoxCellArr[0].setText(LocaleController.getString("AddMembersForwardMessages", NUM), "", true, false);
                }
                checkBoxCellArr[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                linearLayout.addView(checkBoxCellArr[0], LayoutHelper.createLinear(-1, -2));
                checkBoxCellArr[0].setOnClickListener(new GroupCreateActivity$$ExternalSyntheticLambda4(checkBoxCellArr));
                builder.setCustomViewOffset(12);
                builder.setView(linearLayout);
            }
            builder.setPositiveButton(LocaleController.getString("Add", NUM), new GroupCreateActivity$$ExternalSyntheticLambda1(this, checkBoxCellArr));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDonePressed$6(CheckBoxCell[] checkBoxCellArr, DialogInterface dialogInterface, int i) {
        int i2 = 0;
        if (checkBoxCellArr[0] != null && checkBoxCellArr[0].isChecked()) {
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
                this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", this.selectedContacts.size()));
            } else if (this.selectedContacts.size() == 0) {
                this.actionBar.setSubtitle(LocaleController.formatString("MembersCountZero", NUM, LocaleController.formatPluralString("Members", this.maxCount)));
            } else {
                String pluralString = LocaleController.getPluralString("MembersCountSelected", this.selectedContacts.size());
                this.actionBar.setSubtitle(String.format(pluralString, new Object[]{Integer.valueOf(this.selectedContacts.size()), Integer.valueOf(this.maxCount)}));
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
                public void onAnimationEnd(Animator animator) {
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

        public GroupCreateAdapter(Context context2) {
            TLRPC$Chat chat;
            this.context = context2;
            ArrayList<TLRPC$TL_contact> arrayList = GroupCreateActivity.this.getContactsController().contacts;
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$User user = GroupCreateActivity.this.getMessagesController().getUser(Long.valueOf(arrayList.get(i).user_id));
                if (user != null && !user.self && !user.deleted) {
                    this.contacts.add(user);
                }
            }
            if (GroupCreateActivity.this.isNeverShare || GroupCreateActivity.this.isAlwaysShare) {
                ArrayList<TLRPC$Dialog> allDialogs = GroupCreateActivity.this.getMessagesController().getAllDialogs();
                int size = allDialogs.size();
                for (int i2 = 0; i2 < size; i2++) {
                    TLRPC$Dialog tLRPC$Dialog = allDialogs.get(i2);
                    if (DialogObject.isChatDialog(tLRPC$Dialog.id) && (chat = GroupCreateActivity.this.getMessagesController().getChat(Long.valueOf(-tLRPC$Dialog.id))) != null && chat.migrated_to == null && (!ChatObject.isChannel(chat) || chat.megagroup)) {
                        this.contacts.add(chat);
                    }
                }
                Collections.sort(this.contacts, new Comparator<TLObject>(this, GroupCreateActivity.this) {
                    private String getName(TLObject tLObject) {
                        if (!(tLObject instanceof TLRPC$User)) {
                            return ((TLRPC$Chat) tLObject).title;
                        }
                        TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
                        return ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name);
                    }

                    public int compare(TLObject tLObject, TLObject tLObject2) {
                        return getName(tLObject).compareTo(getName(tLObject2));
                    }
                });
            }
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(false);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda4(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(int i) {
            GroupCreateActivity.this.showItemsAnimated(this.currentItemsCount);
            if (this.searchRunnable == null && !this.searchAdapterHelper.isSearchInProgress() && getItemCount() == 0) {
                GroupCreateActivity.this.emptyView.showProgress(false, true);
            }
            notifyDataSetChanged();
        }

        public void setSearching(boolean z) {
            if (this.searching != z) {
                this.searching = z;
                notifyDataSetChanged();
            }
        }

        public String getLetter(int i) {
            String str;
            String str2;
            if (this.searching || i < this.usersStartRow) {
                return null;
            }
            int size = this.contacts.size();
            int i2 = this.usersStartRow;
            if (i >= size + i2) {
                return null;
            }
            TLObject tLObject = this.contacts.get(i - i2);
            if (tLObject instanceof TLRPC$User) {
                TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
                str2 = tLRPC$User.first_name;
                str = tLRPC$User.last_name;
            } else {
                str2 = ((TLRPC$Chat) tLObject).title;
                str = "";
            }
            if (LocaleController.nameDisplayOrder == 1) {
                if (!TextUtils.isEmpty(str2)) {
                    return str2.substring(0, 1).toUpperCase();
                }
                if (!TextUtils.isEmpty(str)) {
                    return str.substring(0, 1).toUpperCase();
                }
            } else if (!TextUtils.isEmpty(str)) {
                return str.substring(0, 1).toUpperCase();
            } else {
                if (!TextUtils.isEmpty(str2)) {
                    return str2.substring(0, 1).toUpperCase();
                }
            }
            return "";
        }

        public int getItemCount() {
            this.noContactsStubRow = -1;
            if (this.searching) {
                int size = this.searchResult.size();
                int size2 = this.searchAdapterHelper.getLocalServerSearch().size();
                int size3 = this.searchAdapterHelper.getGlobalSearch().size();
                int i = size + size2;
                if (size3 != 0) {
                    i += size3 + 1;
                }
                this.currentItemsCount = i;
                return i;
            }
            int size4 = this.contacts.size();
            if (GroupCreateActivity.this.addToGroup) {
                if (GroupCreateActivity.this.chatId != 0) {
                    this.inviteViaLink = ChatObject.canUserDoAdminAction(GroupCreateActivity.this.getMessagesController().getChat(Long.valueOf(GroupCreateActivity.this.chatId)), 3) ? 1 : 0;
                } else if (GroupCreateActivity.this.channelId != 0) {
                    TLRPC$Chat chat = GroupCreateActivity.this.getMessagesController().getChat(Long.valueOf(GroupCreateActivity.this.channelId));
                    this.inviteViaLink = (!ChatObject.canUserDoAdminAction(chat, 3) || !TextUtils.isEmpty(chat.username)) ? 0 : 2;
                } else {
                    this.inviteViaLink = 0;
                }
                if (this.inviteViaLink != 0) {
                    this.usersStartRow = 1;
                    size4++;
                }
            }
            if (size4 == 0) {
                this.noContactsStubRow = 0;
                size4++;
            }
            this.currentItemsCount = size4;
            return size4;
        }

        /* JADX WARNING: type inference failed for: r5v5, types: [android.widget.FrameLayout, org.telegram.ui.GroupCreateActivity$GroupCreateAdapter$2, org.telegram.ui.Components.StickerEmptyView] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r4, int r5) {
            /*
                r3 = this;
                if (r5 == 0) goto L_0x0044
                r4 = 0
                r0 = 1
                if (r5 == r0) goto L_0x003b
                r1 = 3
                if (r5 == r1) goto L_0x0011
                org.telegram.ui.Cells.TextCell r4 = new org.telegram.ui.Cells.TextCell
                android.content.Context r5 = r3.context
                r4.<init>(r5)
                goto L_0x004b
            L_0x0011:
                org.telegram.ui.GroupCreateActivity$GroupCreateAdapter$2 r5 = new org.telegram.ui.GroupCreateActivity$GroupCreateAdapter$2
                android.content.Context r1 = r3.context
                r2 = 0
                r5.<init>(r3, r1, r2, r4)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r4 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r1 = -1
                r4.<init>((int) r1, (int) r1)
                r5.setLayoutParams(r4)
                android.widget.TextView r4 = r5.subtitle
                r1 = 8
                r4.setVisibility(r1)
                android.widget.TextView r4 = r5.title
                r1 = 2131626570(0x7f0e0a4a, float:1.888038E38)
                java.lang.String r2 = "NoContacts"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                r4.setText(r1)
                r5.setAnimateLayoutChange(r0)
                goto L_0x0042
            L_0x003b:
                org.telegram.ui.Cells.GroupCreateUserCell r5 = new org.telegram.ui.Cells.GroupCreateUserCell
                android.content.Context r1 = r3.context
                r5.<init>(r1, r0, r4, r4)
            L_0x0042:
                r4 = r5
                goto L_0x004b
            L_0x0044:
                org.telegram.ui.Cells.GroupCreateSectionCell r4 = new org.telegram.ui.Cells.GroupCreateSectionCell
                android.content.Context r5 = r3.context
                r4.<init>(r5)
            L_0x004b:
                org.telegram.ui.Components.RecyclerListView$Holder r5 = new org.telegram.ui.Components.RecyclerListView$Holder
                r5.<init>(r4)
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCreateActivity.GroupCreateAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            SpannableStringBuilder spannableStringBuilder;
            TLObject tLObject;
            long j;
            CharSequence charSequence;
            String str;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                GroupCreateSectionCell groupCreateSectionCell = (GroupCreateSectionCell) viewHolder.itemView;
                if (this.searching) {
                    groupCreateSectionCell.setText(LocaleController.getString("GlobalSearch", NUM));
                }
            } else if (itemViewType == 1) {
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) viewHolder.itemView;
                SpannableStringBuilder spannableStringBuilder2 = null;
                if (this.searching) {
                    int size = this.searchResult.size();
                    int size2 = this.searchAdapterHelper.getGlobalSearch().size();
                    int size3 = this.searchAdapterHelper.getLocalServerSearch().size();
                    if (i >= 0 && i < size) {
                        tLObject = (TLObject) this.searchResult.get(i);
                    } else if (i < size || i >= size3 + size) {
                        tLObject = (i <= size + size3 || i > (size2 + size) + size3) ? null : this.searchAdapterHelper.getGlobalSearch().get(((i - size) - size3) - 1);
                    } else {
                        tLObject = this.searchAdapterHelper.getLocalServerSearch().get(i - size);
                    }
                    if (tLObject != null) {
                        if (tLObject instanceof TLRPC$User) {
                            str = ((TLRPC$User) tLObject).username;
                        } else {
                            str = ((TLRPC$Chat) tLObject).username;
                        }
                        if (i < size) {
                            charSequence = this.searchResultNames.get(i);
                            if (charSequence != null && !TextUtils.isEmpty(str)) {
                                if (charSequence.toString().startsWith("@" + str)) {
                                    spannableStringBuilder2 = charSequence;
                                    charSequence = null;
                                }
                            }
                        } else if (i > size && !TextUtils.isEmpty(str)) {
                            String lastFoundUsername = this.searchAdapterHelper.getLastFoundUsername();
                            if (lastFoundUsername.startsWith("@")) {
                                lastFoundUsername = lastFoundUsername.substring(1);
                            }
                            try {
                                SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder();
                                spannableStringBuilder3.append("@");
                                spannableStringBuilder3.append(str);
                                int indexOfIgnoreCase = AndroidUtilities.indexOfIgnoreCase(str, lastFoundUsername);
                                if (indexOfIgnoreCase != -1) {
                                    int length = lastFoundUsername.length();
                                    if (indexOfIgnoreCase == 0) {
                                        length++;
                                    } else {
                                        indexOfIgnoreCase++;
                                    }
                                    spannableStringBuilder3.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), indexOfIgnoreCase, length + indexOfIgnoreCase, 33);
                                }
                                charSequence = null;
                                spannableStringBuilder2 = spannableStringBuilder3;
                            } catch (Exception unused) {
                                charSequence = null;
                                spannableStringBuilder2 = str;
                            }
                        }
                        SpannableStringBuilder spannableStringBuilder4 = spannableStringBuilder2;
                        spannableStringBuilder2 = charSequence;
                        spannableStringBuilder = spannableStringBuilder4;
                    }
                    charSequence = null;
                    SpannableStringBuilder spannableStringBuilder42 = spannableStringBuilder2;
                    spannableStringBuilder2 = charSequence;
                    spannableStringBuilder = spannableStringBuilder42;
                } else {
                    tLObject = this.contacts.get(i - this.usersStartRow);
                    spannableStringBuilder = null;
                }
                groupCreateUserCell.setObject(tLObject, spannableStringBuilder2, spannableStringBuilder);
                if (tLObject instanceof TLRPC$User) {
                    j = ((TLRPC$User) tLObject).id;
                } else {
                    j = tLObject instanceof TLRPC$Chat ? -((TLRPC$Chat) tLObject).id : 0;
                }
                if (j == 0) {
                    return;
                }
                if (GroupCreateActivity.this.ignoreUsers == null || GroupCreateActivity.this.ignoreUsers.indexOfKey(j) < 0) {
                    groupCreateUserCell.setChecked(GroupCreateActivity.this.selectedContacts.indexOfKey(j) >= 0, false);
                    groupCreateUserCell.setCheckBoxEnabled(true);
                    return;
                }
                groupCreateUserCell.setChecked(true, false);
                groupCreateUserCell.setCheckBoxEnabled(false);
            } else if (itemViewType == 2) {
                TextCell textCell = (TextCell) viewHolder.itemView;
                if (this.inviteViaLink == 2) {
                    textCell.setTextAndIcon(LocaleController.getString("ChannelInviteViaLink", NUM), NUM, false);
                } else {
                    textCell.setTextAndIcon(LocaleController.getString("InviteToGroupByLink", NUM), NUM, false);
                }
            }
        }

        public int getItemViewType(int i) {
            if (this.searching) {
                if (i == this.searchResult.size() + this.searchAdapterHelper.getLocalServerSearch().size()) {
                    return 0;
                }
                return 1;
            } else if (this.inviteViaLink != 0 && i == 0) {
                return 2;
            } else {
                if (this.noContactsStubRow == i) {
                    return 3;
                }
                return 1;
            }
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
            if (GroupCreateActivity.this.ignoreUsers == null) {
                return true;
            }
            View view = viewHolder.itemView;
            if (!(view instanceof GroupCreateUserCell)) {
                return true;
            }
            Object object = ((GroupCreateUserCell) view).getObject();
            if (!(object instanceof TLRPC$User) || GroupCreateActivity.this.ignoreUsers.indexOfKey(((TLRPC$User) object).id) < 0) {
                return true;
            }
            return false;
        }

        public void searchDialogs(String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            this.searchResult.clear();
            this.searchResultNames.clear();
            this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
            this.searchAdapterHelper.queryServerSearch((String) null, true, GroupCreateActivity.this.isAlwaysShare || GroupCreateActivity.this.isNeverShare, false, false, false, 0, false, 0, 0);
            notifyDataSetChanged();
            if (!TextUtils.isEmpty(str)) {
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda2 groupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda2 = new GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda2(this, str);
                this.searchRunnable = groupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda2;
                dispatchQueue.postRunnable(groupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda2, 300);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$searchDialogs$3(String str) {
            AndroidUtilities.runOnUIThread(new GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda0(this, str));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$searchDialogs$2(String str) {
            this.searchAdapterHelper.queryServerSearch(str, true, GroupCreateActivity.this.isAlwaysShare || GroupCreateActivity.this.isNeverShare, true, false, false, 0, false, 0, 0);
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda1 groupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda1 = new GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda1(this, str);
            this.searchRunnable = groupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda1;
            dispatchQueue.postRunnable(groupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda1);
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:36:0x00c8, code lost:
            if (r13.contains(" " + r3) != false) goto L_0x00d5;
         */
        /* JADX WARNING: Removed duplicated region for block: B:51:0x0128 A[LOOP:1: B:27:0x008c->B:51:0x0128, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:60:0x00d8 A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$searchDialogs$1(java.lang.String r18) {
            /*
                r17 = this;
                r0 = r17
                java.lang.String r1 = r18.trim()
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
                if (r3 != 0) goto L_0x0032
                int r3 = r2.length()
                if (r3 != 0) goto L_0x0033
            L_0x0032:
                r2 = 0
            L_0x0033:
                r3 = 0
                r5 = 1
                if (r2 == 0) goto L_0x0039
                r6 = 1
                goto L_0x003a
            L_0x0039:
                r6 = 0
            L_0x003a:
                int r6 = r6 + r5
                java.lang.String[] r7 = new java.lang.String[r6]
                r7[r3] = r1
                if (r2 == 0) goto L_0x0043
                r7[r5] = r2
            L_0x0043:
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                r8 = 0
            L_0x004e:
                java.util.ArrayList<org.telegram.tgnet.TLObject> r9 = r0.contacts
                int r9 = r9.size()
                if (r8 >= r9) goto L_0x0138
                java.util.ArrayList<org.telegram.tgnet.TLObject> r9 = r0.contacts
                java.lang.Object r9 = r9.get(r8)
                org.telegram.tgnet.TLObject r9 = (org.telegram.tgnet.TLObject) r9
                boolean r10 = r9 instanceof org.telegram.tgnet.TLRPC$User
                if (r10 == 0) goto L_0x0074
                r11 = r9
                org.telegram.tgnet.TLRPC$User r11 = (org.telegram.tgnet.TLRPC$User) r11
                java.lang.String r12 = r11.first_name
                java.lang.String r13 = r11.last_name
                java.lang.String r12 = org.telegram.messenger.ContactsController.formatName(r12, r13)
                java.lang.String r12 = r12.toLowerCase()
                java.lang.String r11 = r11.username
                goto L_0x007b
            L_0x0074:
                r11 = r9
                org.telegram.tgnet.TLRPC$Chat r11 = (org.telegram.tgnet.TLRPC$Chat) r11
                java.lang.String r12 = r11.title
                java.lang.String r11 = r11.username
            L_0x007b:
                org.telegram.messenger.LocaleController r13 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r13 = r13.getTranslitString(r12)
                boolean r14 = r12.equals(r13)
                if (r14 == 0) goto L_0x008a
                r13 = 0
            L_0x008a:
                r14 = 0
                r15 = 0
            L_0x008c:
                if (r14 >= r6) goto L_0x0130
                r3 = r7[r14]
                boolean r16 = r12.startsWith(r3)
                if (r16 != 0) goto L_0x00d5
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = " "
                r4.append(r5)
                r4.append(r3)
                java.lang.String r4 = r4.toString()
                boolean r4 = r12.contains(r4)
                if (r4 != 0) goto L_0x00d5
                if (r13 == 0) goto L_0x00cb
                boolean r4 = r13.startsWith(r3)
                if (r4 != 0) goto L_0x00d5
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r5)
                r4.append(r3)
                java.lang.String r4 = r4.toString()
                boolean r4 = r13.contains(r4)
                if (r4 == 0) goto L_0x00cb
                goto L_0x00d5
            L_0x00cb:
                if (r11 == 0) goto L_0x00d6
                boolean r4 = r11.startsWith(r3)
                if (r4 == 0) goto L_0x00d6
                r15 = 2
                goto L_0x00d6
            L_0x00d5:
                r15 = 1
            L_0x00d6:
                if (r15 == 0) goto L_0x0128
                r4 = 1
                if (r15 != r4) goto L_0x00fb
                if (r10 == 0) goto L_0x00ec
                r5 = r9
                org.telegram.tgnet.TLRPC$User r5 = (org.telegram.tgnet.TLRPC$User) r5
                java.lang.String r10 = r5.first_name
                java.lang.String r5 = r5.last_name
                java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r10, r5, r3)
                r2.add(r3)
                goto L_0x00f9
            L_0x00ec:
                r5 = r9
                org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC$Chat) r5
                java.lang.String r5 = r5.title
                r10 = 0
                java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r10, r3)
                r2.add(r3)
            L_0x00f9:
                r10 = 0
                goto L_0x0123
            L_0x00fb:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r10 = "@"
                r5.append(r10)
                r5.append(r11)
                java.lang.String r5 = r5.toString()
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>()
                r11.append(r10)
                r11.append(r3)
                java.lang.String r3 = r11.toString()
                r10 = 0
                java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r10, r3)
                r2.add(r3)
            L_0x0123:
                r1.add(r9)
                r3 = r10
                goto L_0x0132
            L_0x0128:
                r3 = 0
                r4 = 1
                int r14 = r14 + 1
                r3 = 0
                r5 = 1
                goto L_0x008c
            L_0x0130:
                r3 = 0
                r4 = 1
            L_0x0132:
                int r8 = r8 + 1
                r3 = 0
                r5 = 1
                goto L_0x004e
            L_0x0138:
                r0.updateSearchResults(r1, r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCreateActivity.GroupCreateAdapter.lambda$searchDialogs$1(java.lang.String):void");
        }

        private void updateSearchResults(ArrayList<Object> arrayList, ArrayList<CharSequence> arrayList2) {
            AndroidUtilities.runOnUIThread(new GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda3(this, arrayList, arrayList2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSearchResults$4(ArrayList arrayList, ArrayList arrayList2) {
            if (this.searching) {
                this.searchRunnable = null;
                this.searchResult = arrayList;
                this.searchResultNames = arrayList2;
                this.searchAdapterHelper.mergeResults(arrayList);
                GroupCreateActivity.this.showItemsAnimated(this.currentItemsCount);
                notifyDataSetChanged();
                if (this.searching && !this.searchAdapterHelper.isSearchInProgress() && getItemCount() == 0) {
                    GroupCreateActivity.this.emptyView.showProgress(false, true);
                }
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        GroupCreateActivity$$ExternalSyntheticLambda6 groupCreateActivity$$ExternalSyntheticLambda6 = new GroupCreateActivity$$ExternalSyntheticLambda6(this);
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
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GroupCreateSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{GroupCreateSectionCell.class}, new String[]{"drawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_sectionShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_sectionText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_sectionText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxDisabled"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        GroupCreateActivity$$ExternalSyntheticLambda6 groupCreateActivity$$ExternalSyntheticLambda62 = groupCreateActivity$$ExternalSyntheticLambda6;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, groupCreateActivity$$ExternalSyntheticLambda62, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, groupCreateActivity$$ExternalSyntheticLambda62, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, groupCreateActivity$$ExternalSyntheticLambda62, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, groupCreateActivity$$ExternalSyntheticLambda62, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, groupCreateActivity$$ExternalSyntheticLambda62, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, groupCreateActivity$$ExternalSyntheticLambda62, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, groupCreateActivity$$ExternalSyntheticLambda62, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_spanBackground"));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_spanText"));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "groupcreate_spanDelete"));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription(this.emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        PermanentLinkBottomSheet permanentLinkBottomSheet = this.sharedLinkBottomSheet;
        if (permanentLinkBottomSheet != null) {
            arrayList.addAll(permanentLinkBottomSheet.getThemeDescriptions());
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$7() {
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
