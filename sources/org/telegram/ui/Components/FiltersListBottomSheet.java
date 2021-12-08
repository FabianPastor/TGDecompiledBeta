package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLRPC;
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

    public FiltersListBottomSheet(DialogsActivity baseFragment, ArrayList<Long> selectedDialogs) {
        super(baseFragment.getParentActivity(), false);
        this.dialogFilters = getCanAddDialogFilters(baseFragment, selectedDialogs);
        Context context = baseFragment.getParentActivity();
        this.containerView = new FrameLayout(context) {
            private boolean fullHeight;
            private RectF rect = new RectF();

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() != 0 || FiltersListBottomSheet.this.scrollOffsetY == 0 || ev.getY() >= ((float) FiltersListBottomSheet.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(ev);
                }
                FiltersListBottomSheet.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent e) {
                return !FiltersListBottomSheet.this.isDismissed() && super.onTouchEvent(e);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                boolean z = true;
                if (Build.VERSION.SDK_INT >= 21) {
                    boolean unused = FiltersListBottomSheet.this.ignoreLayout = true;
                    setPadding(FiltersListBottomSheet.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, FiltersListBottomSheet.this.backgroundPaddingLeft, 0);
                    boolean unused2 = FiltersListBottomSheet.this.ignoreLayout = false;
                }
                int contentSize = AndroidUtilities.dp(48.0f) + (AndroidUtilities.dp(48.0f) * FiltersListBottomSheet.this.adapter.getItemCount()) + FiltersListBottomSheet.this.backgroundPaddingTop + AndroidUtilities.statusBarHeight;
                double d = (double) (height / 5);
                Double.isNaN(d);
                int padding = ((double) contentSize) < d * 3.2d ? 0 : (height / 5) * 2;
                if (padding != 0 && contentSize < height) {
                    padding -= height - contentSize;
                }
                if (padding == 0) {
                    padding = FiltersListBottomSheet.this.backgroundPaddingTop;
                }
                if (FiltersListBottomSheet.this.listView.getPaddingTop() != padding) {
                    boolean unused3 = FiltersListBottomSheet.this.ignoreLayout = true;
                    FiltersListBottomSheet.this.listView.setPadding(AndroidUtilities.dp(10.0f), padding, AndroidUtilities.dp(10.0f), 0);
                    boolean unused4 = FiltersListBottomSheet.this.ignoreLayout = false;
                }
                if (contentSize < height) {
                    z = false;
                }
                this.fullHeight = z;
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(Math.min(contentSize, height), NUM));
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                FiltersListBottomSheet.this.updateLayout();
            }

            public void requestLayout() {
                if (!FiltersListBottomSheet.this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                int top = (FiltersListBottomSheet.this.scrollOffsetY - FiltersListBottomSheet.this.backgroundPaddingTop) - AndroidUtilities.dp(8.0f);
                int height = getMeasuredHeight() + AndroidUtilities.dp(36.0f) + FiltersListBottomSheet.this.backgroundPaddingTop;
                int statusBarHeight = 0;
                float radProgress = 1.0f;
                if (Build.VERSION.SDK_INT >= 21) {
                    top += AndroidUtilities.statusBarHeight;
                    height -= AndroidUtilities.statusBarHeight;
                    if (this.fullHeight) {
                        if (FiltersListBottomSheet.this.backgroundPaddingTop + top < AndroidUtilities.statusBarHeight * 2) {
                            int diff = Math.min(AndroidUtilities.statusBarHeight, ((AndroidUtilities.statusBarHeight * 2) - top) - FiltersListBottomSheet.this.backgroundPaddingTop);
                            top -= diff;
                            height += diff;
                            radProgress = 1.0f - Math.min(1.0f, ((float) (diff * 2)) / ((float) AndroidUtilities.statusBarHeight));
                        }
                        if (FiltersListBottomSheet.this.backgroundPaddingTop + top < AndroidUtilities.statusBarHeight) {
                            statusBarHeight = Math.min(AndroidUtilities.statusBarHeight, (AndroidUtilities.statusBarHeight - top) - FiltersListBottomSheet.this.backgroundPaddingTop);
                        }
                    }
                }
                FiltersListBottomSheet.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), height);
                FiltersListBottomSheet.this.shadowDrawable.draw(canvas);
                if (radProgress != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor("dialogBackground"));
                    this.rect.set((float) FiltersListBottomSheet.this.backgroundPaddingLeft, (float) (FiltersListBottomSheet.this.backgroundPaddingTop + top), (float) (getMeasuredWidth() - FiltersListBottomSheet.this.backgroundPaddingLeft), (float) (FiltersListBottomSheet.this.backgroundPaddingTop + top + AndroidUtilities.dp(24.0f)));
                    canvas.drawRoundRect(this.rect, ((float) AndroidUtilities.dp(12.0f)) * radProgress, ((float) AndroidUtilities.dp(12.0f)) * radProgress, Theme.dialogs_onlineCirclePaint);
                }
                if (statusBarHeight > 0) {
                    int color1 = Theme.getColor("dialogBackground");
                    Theme.dialogs_onlineCirclePaint.setColor(Color.argb(255, (int) (((float) Color.red(color1)) * 0.8f), (int) (((float) Color.green(color1)) * 0.8f), (int) (((float) Color.blue(color1)) * 0.8f)));
                    canvas.drawRect((float) FiltersListBottomSheet.this.backgroundPaddingLeft, (float) (AndroidUtilities.statusBarHeight - statusBarHeight), (float) (getMeasuredWidth() - FiltersListBottomSheet.this.backgroundPaddingLeft), (float) AndroidUtilities.statusBarHeight, Theme.dialogs_onlineCirclePaint);
                }
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        frameLayoutParams.topMargin = AndroidUtilities.dp(48.0f);
        View view = new View(context);
        this.shadow = view;
        view.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.shadow.setAlpha(0.0f);
        this.shadow.setVisibility(4);
        this.shadow.setTag(1);
        this.containerView.addView(this.shadow, frameLayoutParams);
        AnonymousClass2 r3 = new RecyclerListView(context) {
            public void requestLayout() {
                if (!FiltersListBottomSheet.this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.listView = r3;
        r3.setTag(14);
        this.listView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.listView.setClipToPadding(false);
        this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FiltersListBottomSheet.this.updateLayout();
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new FiltersListBottomSheet$$ExternalSyntheticLambda0(this));
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
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

    /* renamed from: lambda$new$0$org-telegram-ui-Components-FiltersListBottomSheet  reason: not valid java name */
    public /* synthetic */ void m2284lambda$new$0$orgtelegramuiComponentsFiltersListBottomSheet(View view, int position) {
        this.delegate.didSelectFilter(this.adapter.getItem(position));
        dismiss();
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
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
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = 0;
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            runShadowAnimation(true);
        } else {
            newOffset = top;
            runShadowAnimation(false);
        }
        if (this.scrollOffsetY != newOffset) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = newOffset;
            recyclerListView2.setTopGlowOffset(newOffset);
            this.titleTextView.setTranslationY((float) this.scrollOffsetY);
            this.shadow.setTranslationY((float) this.scrollOffsetY);
            this.containerView.invalidate();
        }
    }

    private void runShadowAnimation(final boolean show) {
        if ((show && this.shadow.getTag() != null) || (!show && this.shadow.getTag() == null)) {
            this.shadow.setTag(show ? null : 1);
            if (show) {
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
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation.setDuration(150);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (FiltersListBottomSheet.this.shadowAnimation != null && FiltersListBottomSheet.this.shadowAnimation.equals(animation)) {
                        if (!show) {
                            FiltersListBottomSheet.this.shadow.setVisibility(4);
                        }
                        AnimatorSet unused = FiltersListBottomSheet.this.shadowAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (FiltersListBottomSheet.this.shadowAnimation != null && FiltersListBottomSheet.this.shadowAnimation.equals(animation)) {
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

    public void didReceivedNotification(int id, int account, Object... args) {
        RecyclerListView recyclerListView;
        if (id == NotificationCenter.emojiLoaded && (recyclerListView = this.listView) != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                this.listView.getChildAt(a).invalidate();
            }
        }
    }

    public void setDelegate(FiltersListBottomSheetDelegate filtersListBottomSheetDelegate) {
        this.delegate = filtersListBottomSheetDelegate;
    }

    public static ArrayList<MessagesController.DialogFilter> getCanAddDialogFilters(BaseFragment fragment, ArrayList<Long> selectedDialogs) {
        ArrayList<MessagesController.DialogFilter> result = new ArrayList<>();
        ArrayList<MessagesController.DialogFilter> filters = fragment.getMessagesController().dialogFilters;
        int N = filters.size();
        for (int a = 0; a < N; a++) {
            MessagesController.DialogFilter filter = filters.get(a);
            if (!getDialogsCount(fragment, filter, selectedDialogs, true, true).isEmpty()) {
                result.add(filter);
            }
        }
        return result;
    }

    public static ArrayList<Long> getDialogsCount(BaseFragment fragment, MessagesController.DialogFilter filter, ArrayList<Long> selectedDialogs, boolean always, boolean check) {
        ArrayList<Long> dids = new ArrayList<>();
        int N2 = selectedDialogs.size();
        for (int b = 0; b < N2; b++) {
            long did = selectedDialogs.get(b).longValue();
            if (DialogObject.isEncryptedDialog(did)) {
                TLRPC.EncryptedChat encryptedChat = fragment.getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(did)));
                if (encryptedChat != null) {
                    did = encryptedChat.user_id;
                    if (dids.contains(Long.valueOf(did))) {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            if (filter == null || ((!always || !filter.alwaysShow.contains(Long.valueOf(did))) && (always || !filter.neverShow.contains(Long.valueOf(did))))) {
                dids.add(Long.valueOf(did));
                if (check) {
                    break;
                }
            }
        }
        return dids;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public ListAdapter(Context context2) {
            this.context = context2;
        }

        public MessagesController.DialogFilter getItem(int position) {
            if (position < FiltersListBottomSheet.this.dialogFilters.size()) {
                return (MessagesController.DialogFilter) FiltersListBottomSheet.this.dialogFilters.get(position);
            }
            return null;
        }

        public int getItemCount() {
            int count = FiltersListBottomSheet.this.dialogFilters.size();
            if (count < 10) {
                return count + 1;
            }
            return count;
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            BottomSheet.BottomSheetCell cell = new BottomSheet.BottomSheetCell(this.context, 0);
            cell.setBackground((Drawable) null);
            cell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(cell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int icon;
            BottomSheet.BottomSheetCell cell = (BottomSheet.BottomSheetCell) holder.itemView;
            if (position < FiltersListBottomSheet.this.dialogFilters.size()) {
                cell.getImageView().setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogIcon"), PorterDuff.Mode.MULTIPLY));
                MessagesController.DialogFilter filter = (MessagesController.DialogFilter) FiltersListBottomSheet.this.dialogFilters.get(position);
                cell.setTextColor(Theme.getColor("dialogTextBlack"));
                if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) == (MessagesController.DIALOG_FILTER_FLAG_CONTACTS | MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS)) {
                    icon = NUM;
                } else if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0 && (filter.flags & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) == MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) {
                    icon = NUM;
                } else if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) == MessagesController.DIALOG_FILTER_FLAG_CHANNELS) {
                    icon = NUM;
                } else if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) == MessagesController.DIALOG_FILTER_FLAG_GROUPS) {
                    icon = NUM;
                } else if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) == MessagesController.DIALOG_FILTER_FLAG_CONTACTS) {
                    icon = NUM;
                } else if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) == MessagesController.DIALOG_FILTER_FLAG_BOTS) {
                    icon = NUM;
                } else {
                    icon = NUM;
                }
                cell.setTextAndIcon((CharSequence) filter.name, icon);
                return;
            }
            cell.getImageView().setColorFilter((ColorFilter) null);
            Drawable drawable1 = this.context.getResources().getDrawable(NUM);
            Drawable drawable2 = this.context.getResources().getDrawable(NUM);
            drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor("switchTrackChecked"), PorterDuff.Mode.MULTIPLY));
            drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("checkboxCheck"), PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(drawable1, drawable2);
            cell.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            cell.setTextAndIcon((CharSequence) LocaleController.getString("CreateNewFilter", NUM), (Drawable) combinedDrawable);
        }
    }
}
