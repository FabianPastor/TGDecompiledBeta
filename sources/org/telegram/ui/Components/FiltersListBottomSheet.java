package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Property;
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
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;

public class FiltersListBottomSheet extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public ListAdapter adapter;
    private FiltersListBottomSheetDelegate delegate;
    /* access modifiers changed from: private */
    public ArrayList<MessagesController.DialogFilter> dialogFilters;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public View shadow;
    /* access modifiers changed from: private */
    public AnimatorSet shadowAnimation;
    private TextView titleTextView;

    public interface FiltersListBottomSheetDelegate {
        void didSelectFilter(MessagesController.DialogFilter dialogFilter);
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public FiltersListBottomSheet(DialogsActivity dialogsActivity, ArrayList<Long> arrayList) {
        super(dialogsActivity.getParentActivity(), false);
        this.dialogFilters = getCanAddDialogFilters(dialogsActivity, arrayList);
        Activity parentActivity = dialogsActivity.getParentActivity();
        AnonymousClass1 r12 = new FrameLayout(parentActivity) {
            private boolean fullHeight;
            private RectF rect = new RectF();

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || FiltersListBottomSheet.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) FiltersListBottomSheet.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                FiltersListBottomSheet.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return !FiltersListBottomSheet.this.isDismissed() && super.onTouchEvent(motionEvent);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i2);
                boolean z = true;
                if (Build.VERSION.SDK_INT >= 21) {
                    boolean unused = FiltersListBottomSheet.this.ignoreLayout = true;
                    setPadding(FiltersListBottomSheet.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, FiltersListBottomSheet.this.backgroundPaddingLeft, 0);
                    boolean unused2 = FiltersListBottomSheet.this.ignoreLayout = false;
                }
                int dp = AndroidUtilities.dp(48.0f) + (AndroidUtilities.dp(48.0f) * FiltersListBottomSheet.this.adapter.getItemCount()) + FiltersListBottomSheet.this.backgroundPaddingTop + AndroidUtilities.statusBarHeight;
                int i3 = size / 5;
                double d = (double) i3;
                Double.isNaN(d);
                int i4 = ((double) dp) < d * 3.2d ? 0 : i3 * 2;
                if (i4 != 0 && dp < size) {
                    i4 -= size - dp;
                }
                if (i4 == 0) {
                    i4 = FiltersListBottomSheet.this.backgroundPaddingTop;
                }
                if (FiltersListBottomSheet.this.listView.getPaddingTop() != i4) {
                    boolean unused3 = FiltersListBottomSheet.this.ignoreLayout = true;
                    FiltersListBottomSheet.this.listView.setPadding(AndroidUtilities.dp(10.0f), i4, AndroidUtilities.dp(10.0f), 0);
                    boolean unused4 = FiltersListBottomSheet.this.ignoreLayout = false;
                }
                if (dp < size) {
                    z = false;
                }
                this.fullHeight = z;
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(Math.min(dp, size), NUM));
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                FiltersListBottomSheet.this.updateLayout();
            }

            public void requestLayout() {
                if (!FiltersListBottomSheet.this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:15:0x009c  */
            /* JADX WARNING: Removed duplicated region for block: B:17:0x00ed  */
            /* JADX WARNING: Removed duplicated region for block: B:19:? A[RETURN, SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onDraw(android.graphics.Canvas r13) {
                /*
                    r12 = this;
                    org.telegram.ui.Components.FiltersListBottomSheet r0 = org.telegram.ui.Components.FiltersListBottomSheet.this
                    int r0 = r0.scrollOffsetY
                    org.telegram.ui.Components.FiltersListBottomSheet r1 = org.telegram.ui.Components.FiltersListBottomSheet.this
                    int r1 = r1.backgroundPaddingTop
                    int r0 = r0 - r1
                    r1 = 1090519040(0x41000000, float:8.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    int r0 = r0 - r1
                    int r1 = r12.getMeasuredHeight()
                    r2 = 1108344832(0x42100000, float:36.0)
                    int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                    int r1 = r1 + r2
                    org.telegram.ui.Components.FiltersListBottomSheet r2 = org.telegram.ui.Components.FiltersListBottomSheet.this
                    int r2 = r2.backgroundPaddingTop
                    int r1 = r1 + r2
                    int r2 = android.os.Build.VERSION.SDK_INT
                    r3 = 0
                    r4 = 1065353216(0x3var_, float:1.0)
                    r5 = 21
                    if (r2 < r5) goto L_0x007d
                    int r2 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r0 = r0 + r2
                    int r1 = r1 - r2
                    boolean r2 = r12.fullHeight
                    if (r2 == 0) goto L_0x007d
                    org.telegram.ui.Components.FiltersListBottomSheet r2 = org.telegram.ui.Components.FiltersListBottomSheet.this
                    int r2 = r2.backgroundPaddingTop
                    int r2 = r2 + r0
                    int r5 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r6 = r5 * 2
                    if (r2 >= r6) goto L_0x0062
                    int r2 = r5 * 2
                    int r2 = r2 - r0
                    org.telegram.ui.Components.FiltersListBottomSheet r6 = org.telegram.ui.Components.FiltersListBottomSheet.this
                    int r6 = r6.backgroundPaddingTop
                    int r2 = r2 - r6
                    int r2 = java.lang.Math.min(r5, r2)
                    int r0 = r0 - r2
                    int r1 = r1 + r2
                    int r2 = r2 * 2
                    float r2 = (float) r2
                    int r5 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    float r5 = (float) r5
                    float r2 = r2 / r5
                    float r2 = java.lang.Math.min(r4, r2)
                    float r2 = r4 - r2
                    goto L_0x0064
                L_0x0062:
                    r2 = 1065353216(0x3var_, float:1.0)
                L_0x0064:
                    org.telegram.ui.Components.FiltersListBottomSheet r5 = org.telegram.ui.Components.FiltersListBottomSheet.this
                    int r5 = r5.backgroundPaddingTop
                    int r5 = r5 + r0
                    int r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    if (r5 >= r6) goto L_0x007f
                    int r5 = r6 - r0
                    org.telegram.ui.Components.FiltersListBottomSheet r7 = org.telegram.ui.Components.FiltersListBottomSheet.this
                    int r7 = r7.backgroundPaddingTop
                    int r5 = r5 - r7
                    int r5 = java.lang.Math.min(r6, r5)
                    goto L_0x0080
                L_0x007d:
                    r2 = 1065353216(0x3var_, float:1.0)
                L_0x007f:
                    r5 = 0
                L_0x0080:
                    org.telegram.ui.Components.FiltersListBottomSheet r6 = org.telegram.ui.Components.FiltersListBottomSheet.this
                    android.graphics.drawable.Drawable r6 = r6.shadowDrawable
                    int r7 = r12.getMeasuredWidth()
                    r6.setBounds(r3, r0, r7, r1)
                    org.telegram.ui.Components.FiltersListBottomSheet r1 = org.telegram.ui.Components.FiltersListBottomSheet.this
                    android.graphics.drawable.Drawable r1 = r1.shadowDrawable
                    r1.draw(r13)
                    java.lang.String r1 = "dialogBackground"
                    int r3 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                    if (r3 == 0) goto L_0x00eb
                    android.graphics.Paint r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    int r4 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                    r3.setColor(r4)
                    android.graphics.RectF r3 = r12.rect
                    org.telegram.ui.Components.FiltersListBottomSheet r4 = org.telegram.ui.Components.FiltersListBottomSheet.this
                    int r4 = r4.backgroundPaddingLeft
                    float r4 = (float) r4
                    org.telegram.ui.Components.FiltersListBottomSheet r6 = org.telegram.ui.Components.FiltersListBottomSheet.this
                    int r6 = r6.backgroundPaddingTop
                    int r6 = r6 + r0
                    float r6 = (float) r6
                    int r7 = r12.getMeasuredWidth()
                    org.telegram.ui.Components.FiltersListBottomSheet r8 = org.telegram.ui.Components.FiltersListBottomSheet.this
                    int r8 = r8.backgroundPaddingLeft
                    int r7 = r7 - r8
                    float r7 = (float) r7
                    org.telegram.ui.Components.FiltersListBottomSheet r8 = org.telegram.ui.Components.FiltersListBottomSheet.this
                    int r8 = r8.backgroundPaddingTop
                    int r8 = r8 + r0
                    r0 = 1103101952(0x41CLASSNAME, float:24.0)
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                    int r8 = r8 + r0
                    float r0 = (float) r8
                    r3.set(r4, r6, r7, r0)
                    android.graphics.RectF r0 = r12.rect
                    r3 = 1094713344(0x41400000, float:12.0)
                    int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
                    float r4 = (float) r4
                    float r4 = r4 * r2
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                    float r3 = (float) r3
                    float r3 = r3 * r2
                    android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r13.drawRoundRect(r0, r4, r3, r2)
                L_0x00eb:
                    if (r5 <= 0) goto L_0x0137
                    int r0 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                    r1 = 255(0xff, float:3.57E-43)
                    int r2 = android.graphics.Color.red(r0)
                    float r2 = (float) r2
                    r3 = 1061997773(0x3f4ccccd, float:0.8)
                    float r2 = r2 * r3
                    int r2 = (int) r2
                    int r4 = android.graphics.Color.green(r0)
                    float r4 = (float) r4
                    float r4 = r4 * r3
                    int r4 = (int) r4
                    int r0 = android.graphics.Color.blue(r0)
                    float r0 = (float) r0
                    float r0 = r0 * r3
                    int r0 = (int) r0
                    int r0 = android.graphics.Color.argb(r1, r2, r4, r0)
                    android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r1.setColor(r0)
                    org.telegram.ui.Components.FiltersListBottomSheet r0 = org.telegram.ui.Components.FiltersListBottomSheet.this
                    int r0 = r0.backgroundPaddingLeft
                    float r7 = (float) r0
                    int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    int r0 = r0 - r5
                    float r8 = (float) r0
                    int r0 = r12.getMeasuredWidth()
                    org.telegram.ui.Components.FiltersListBottomSheet r1 = org.telegram.ui.Components.FiltersListBottomSheet.this
                    int r1 = r1.backgroundPaddingLeft
                    int r0 = r0 - r1
                    float r9 = (float) r0
                    int r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                    float r10 = (float) r0
                    android.graphics.Paint r11 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
                    r6 = r13
                    r6.drawRect(r7, r8, r9, r10, r11)
                L_0x0137:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FiltersListBottomSheet.AnonymousClass1.onDraw(android.graphics.Canvas):void");
            }
        };
        this.containerView = r12;
        r12.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, 0, i, 0);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(48.0f);
        View view = new View(parentActivity);
        this.shadow = view;
        view.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.shadow.setAlpha(0.0f);
        this.shadow.setVisibility(4);
        this.shadow.setTag(1);
        this.containerView.addView(this.shadow, layoutParams);
        AnonymousClass2 r122 = new RecyclerListView(parentActivity) {
            public void requestLayout() {
                if (!FiltersListBottomSheet.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.listView = r122;
        r122.setTag(14);
        this.listView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(parentActivity);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.listView.setClipToPadding(false);
        this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                FiltersListBottomSheet.this.updateLayout();
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new FiltersListBottomSheet$$ExternalSyntheticLambda0(this));
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        TextView textView = new TextView(parentActivity);
        this.titleTextView = textView;
        textView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setTextColor(Theme.getColor("dialogTextBlack"));
        this.titleTextView.setTextSize(1, 20.0f);
        this.titleTextView.setLinkTextColor(Theme.getColor("dialogTextLink"));
        this.titleTextView.setHighlightColor(Theme.getColor("dialogLinkSelection"));
        this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.titleTextView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.titleTextView.setGravity(16);
        this.titleTextView.setText(LocaleController.getString("FilterChoose", NUM));
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.containerView.addView(this.titleTextView, LayoutHelper.createFrame(-1, 50.0f, 51, 0.0f, 0.0f, 40.0f, 0.0f));
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view, int i) {
        this.delegate.didSelectFilter(this.adapter.getItem(i));
        dismiss();
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.titleTextView.setTranslationY((float) this.scrollOffsetY);
            this.shadow.setTranslationY((float) this.scrollOffsetY);
            this.containerView.invalidate();
            return;
        }
        int i = 0;
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop();
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            runShadowAnimation(true);
        } else {
            runShadowAnimation(false);
            i = top;
        }
        if (this.scrollOffsetY != i) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = i;
            recyclerListView2.setTopGlowOffset(i);
            this.titleTextView.setTranslationY((float) this.scrollOffsetY);
            this.shadow.setTranslationY((float) this.scrollOffsetY);
            this.containerView.invalidate();
        }
    }

    private void runShadowAnimation(final boolean z) {
        if ((z && this.shadow.getTag() != null) || (!z && this.shadow.getTag() == null)) {
            this.shadow.setTag(z ? null : 1);
            if (z) {
                this.shadow.setVisibility(0);
            }
            AnimatorSet animatorSet = this.shadowAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.shadowAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation.setDuration(150);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (FiltersListBottomSheet.this.shadowAnimation != null && FiltersListBottomSheet.this.shadowAnimation.equals(animator)) {
                        if (!z) {
                            FiltersListBottomSheet.this.shadow.setVisibility(4);
                        }
                        AnimatorSet unused = FiltersListBottomSheet.this.shadowAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (FiltersListBottomSheet.this.shadowAnimation != null && FiltersListBottomSheet.this.shadowAnimation.equals(animator)) {
                        AnimatorSet unused = FiltersListBottomSheet.this.shadowAnimation = null;
                    }
                }
            });
            this.shadowAnimation.start();
        }
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        RecyclerListView recyclerListView;
        if (i == NotificationCenter.emojiLoaded && (recyclerListView = this.listView) != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i3 = 0; i3 < childCount; i3++) {
                this.listView.getChildAt(i3).invalidate();
            }
        }
    }

    public void setDelegate(FiltersListBottomSheetDelegate filtersListBottomSheetDelegate) {
        this.delegate = filtersListBottomSheetDelegate;
    }

    public static ArrayList<MessagesController.DialogFilter> getCanAddDialogFilters(BaseFragment baseFragment, ArrayList<Long> arrayList) {
        ArrayList<MessagesController.DialogFilter> arrayList2 = new ArrayList<>();
        ArrayList<MessagesController.DialogFilter> arrayList3 = baseFragment.getMessagesController().dialogFilters;
        int size = arrayList3.size();
        for (int i = 0; i < size; i++) {
            MessagesController.DialogFilter dialogFilter = arrayList3.get(i);
            if (!getDialogsCount(baseFragment, dialogFilter, arrayList, true, true).isEmpty()) {
                arrayList2.add(dialogFilter);
            }
        }
        return arrayList2;
    }

    public static ArrayList<Integer> getDialogsCount(BaseFragment baseFragment, MessagesController.DialogFilter dialogFilter, ArrayList<Long> arrayList, boolean z, boolean z2) {
        ArrayList<Integer> arrayList2 = new ArrayList<>();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            long longValue = arrayList.get(i).longValue();
            int i2 = (int) longValue;
            if (i2 == 0) {
                TLRPC$EncryptedChat encryptedChat = baseFragment.getMessagesController().getEncryptedChat(Integer.valueOf((int) (longValue >> 32)));
                if (encryptedChat != null) {
                    i2 = encryptedChat.user_id;
                    if (arrayList2.contains(Integer.valueOf(i2))) {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            if (dialogFilter == null || ((!z || !dialogFilter.alwaysShow.contains(Integer.valueOf(i2))) && (z || !dialogFilter.neverShow.contains(Integer.valueOf(i2))))) {
                arrayList2.add(Integer.valueOf(i2));
                if (z2) {
                    break;
                }
            }
        }
        return arrayList2;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public ListAdapter(Context context2) {
            this.context = context2;
        }

        public MessagesController.DialogFilter getItem(int i) {
            if (i < FiltersListBottomSheet.this.dialogFilters.size()) {
                return (MessagesController.DialogFilter) FiltersListBottomSheet.this.dialogFilters.get(i);
            }
            return null;
        }

        public int getItemCount() {
            int size = FiltersListBottomSheet.this.dialogFilters.size();
            return size < 10 ? size + 1 : size;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            BottomSheet.BottomSheetCell bottomSheetCell = new BottomSheet.BottomSheetCell(this.context, 0);
            bottomSheetCell.setBackground((Drawable) null);
            bottomSheetCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(bottomSheetCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            BottomSheet.BottomSheetCell bottomSheetCell = (BottomSheet.BottomSheetCell) viewHolder.itemView;
            if (i < FiltersListBottomSheet.this.dialogFilters.size()) {
                bottomSheetCell.getImageView().setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogIcon"), PorterDuff.Mode.MULTIPLY));
                MessagesController.DialogFilter dialogFilter = (MessagesController.DialogFilter) FiltersListBottomSheet.this.dialogFilters.get(i);
                bottomSheetCell.setTextColor(Theme.getColor("dialogTextBlack"));
                int i3 = dialogFilter.flags;
                if ((MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS & i3) == (MessagesController.DIALOG_FILTER_FLAG_CONTACTS | MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS)) {
                    i2 = NUM;
                } else {
                    if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ & i3) != 0) {
                        int i4 = MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS;
                        if ((i3 & i4) == i4) {
                            i2 = NUM;
                        }
                    }
                    if ((MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS & i3) == MessagesController.DIALOG_FILTER_FLAG_CHANNELS) {
                        i2 = NUM;
                    } else if ((MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS & i3) == MessagesController.DIALOG_FILTER_FLAG_GROUPS) {
                        i2 = NUM;
                    } else if ((MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS & i3) == MessagesController.DIALOG_FILTER_FLAG_CONTACTS) {
                        i2 = NUM;
                    } else {
                        i2 = (i3 & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) == MessagesController.DIALOG_FILTER_FLAG_BOTS ? NUM : NUM;
                    }
                }
                bottomSheetCell.setTextAndIcon((CharSequence) dialogFilter.name, i2);
                return;
            }
            bottomSheetCell.getImageView().setColorFilter((ColorFilter) null);
            Drawable drawable = this.context.getResources().getDrawable(NUM);
            Drawable drawable2 = this.context.getResources().getDrawable(NUM);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("switchTrackChecked"), PorterDuff.Mode.MULTIPLY));
            drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("checkboxCheck"), PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(drawable, drawable2);
            bottomSheetCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            bottomSheetCell.setTextAndIcon((CharSequence) LocaleController.getString("CreateNewFilter", NUM), (Drawable) combinedDrawable);
        }
    }
}
