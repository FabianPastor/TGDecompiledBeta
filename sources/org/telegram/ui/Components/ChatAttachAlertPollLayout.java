package org.telegram.ui.Components;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.RecyclerListView;

public class ChatAttachAlertPollLayout extends ChatAttachAlert.AttachAlertLayout {
    public static final int MAX_ANSWER_LENGTH = 100;
    public static final int MAX_QUESTION_LENGTH = 255;
    public static final int MAX_SOLUTION_LENGTH = 200;
    private static final int done_button = 40;
    /* access modifiers changed from: private */
    public int addAnswerRow;
    private boolean allowNesterScroll;
    /* access modifiers changed from: private */
    public boolean anonymousPoll = true;
    /* access modifiers changed from: private */
    public int anonymousRow;
    /* access modifiers changed from: private */
    public int answerHeaderRow;
    /* access modifiers changed from: private */
    public int answerSectionRow;
    /* access modifiers changed from: private */
    public int answerStartRow;
    /* access modifiers changed from: private */
    public String[] answers = new String[10];
    /* access modifiers changed from: private */
    public boolean[] answersChecks = new boolean[10];
    /* access modifiers changed from: private */
    public int answersCount = 1;
    private PollCreateActivityDelegate delegate;
    /* access modifiers changed from: private */
    public int emptyRow;
    private boolean hintShowed;
    /* access modifiers changed from: private */
    public HintView hintView;
    private boolean ignoreLayout;
    /* access modifiers changed from: private */
    public SimpleItemAnimator itemAnimator;
    private FillLastLinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean multipleChoise;
    /* access modifiers changed from: private */
    public int multipleRow;
    /* access modifiers changed from: private */
    public int paddingRow;
    /* access modifiers changed from: private */
    public int questionHeaderRow;
    /* access modifiers changed from: private */
    public int questionRow;
    /* access modifiers changed from: private */
    public int questionSectionRow;
    /* access modifiers changed from: private */
    public String questionString;
    /* access modifiers changed from: private */
    public int quizOnly;
    /* access modifiers changed from: private */
    public boolean quizPoll;
    /* access modifiers changed from: private */
    public int quizRow;
    /* access modifiers changed from: private */
    public int requestFieldFocusAtPosition = -1;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int settingsHeaderRow;
    /* access modifiers changed from: private */
    public int settingsSectionRow;
    /* access modifiers changed from: private */
    public int solutionInfoRow;
    /* access modifiers changed from: private */
    public int solutionRow;
    /* access modifiers changed from: private */
    public CharSequence solutionString;
    /* access modifiers changed from: private */
    public int topPadding;

    public interface PollCreateActivityDelegate {
        void sendPoll(TLRPC.TL_messageMediaPoll tL_messageMediaPoll, HashMap<String, String> hashMap, boolean z, int i);
    }

    static /* synthetic */ int access$1210(ChatAttachAlertPollLayout x0) {
        int i = x0.answersCount;
        x0.answersCount = i - 1;
        return i;
    }

    private static class EmptyView extends View {
        public EmptyView(Context context) {
            super(context);
        }
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public TouchHelperCallback() {
        }

        public boolean isLongPressDragEnabled() {
            return true;
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 5) {
                return makeMovementFlags(0, 0);
            }
            return makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            if (source.getItemViewType() != target.getItemViewType()) {
                return false;
            }
            ChatAttachAlertPollLayout.this.listAdapter.swapElements(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != 0) {
                ChatAttachAlertPollLayout.this.listView.setItemAnimator(ChatAttachAlertPollLayout.this.itemAnimator);
                ChatAttachAlertPollLayout.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
                viewHolder.itemView.setBackgroundColor(ChatAttachAlertPollLayout.this.getThemedColor("dialogBackground"));
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
            viewHolder.itemView.setBackground((Drawable) null);
        }
    }

    public ChatAttachAlertPollLayout(ChatAttachAlert alert, Context context, Theme.ResourcesProvider resourcesProvider) {
        super(alert, context, resourcesProvider);
        updateRows();
        this.listAdapter = new ListAdapter(context);
        AnonymousClass1 r2 = new RecyclerListView(context) {
            /* access modifiers changed from: protected */
            public void requestChildOnScreen(View child, View focused) {
                if (child instanceof PollEditTextCell) {
                    super.requestChildOnScreen(child, focused);
                }
            }

            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                rectangle.bottom += AndroidUtilities.dp(60.0f);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        this.listView = r2;
        AnonymousClass2 r3 = new DefaultItemAnimator() {
            /* access modifiers changed from: protected */
            public void onMoveAnimationUpdate(RecyclerView.ViewHolder holder) {
                if (holder.getAdapterPosition() == 0) {
                    ChatAttachAlertPollLayout.this.parentAlert.updateLayout(ChatAttachAlertPollLayout.this, true, 0);
                }
            }
        };
        this.itemAnimator = r3;
        r2.setItemAnimator(r3);
        this.listView.setClipToPadding(false);
        this.listView.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass3 r32 = new FillLastLinearLayoutManager(context, 1, false, AndroidUtilities.dp(53.0f), this.listView) {
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                    public int calculateDyToMakeVisible(View view, int snapPreference) {
                        return super.calculateDyToMakeVisible(view, snapPreference) - (ChatAttachAlertPollLayout.this.topPadding - AndroidUtilities.dp(7.0f));
                    }

                    /* access modifiers changed from: protected */
                    public int calculateTimeForDeceleration(int dx) {
                        return super.calculateTimeForDeceleration(dx) * 2;
                    }
                };
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }

            /* access modifiers changed from: protected */
            public int[] getChildRectangleOnScreenScrollAmount(View child, Rect rect) {
                int[] out = new int[2];
                int parentBottom = getHeight() - getPaddingBottom();
                int childTop = (child.getTop() + rect.top) - child.getScrollY();
                int offScreenTop = Math.min(0, childTop + 0);
                int dy = offScreenTop != 0 ? offScreenTop : Math.min(childTop + 0, Math.max(0, (rect.height() + childTop) - parentBottom));
                out[0] = 0;
                out[1] = dy;
                return out;
            }
        };
        this.layoutManager = r32;
        recyclerListView.setLayoutManager(r32);
        this.layoutManager.setSkipFirstItem();
        new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.listView);
        addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setPreserveFocusAfterLayout(true);
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatAttachAlertPollLayout$$ExternalSyntheticLambda2(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ChatAttachAlertPollLayout.this.parentAlert.updateLayout(ChatAttachAlertPollLayout.this, true, dy);
                if (dy != 0 && ChatAttachAlertPollLayout.this.hintView != null) {
                    ChatAttachAlertPollLayout.this.hintView.hide();
                }
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                RecyclerListView.Holder holder;
                if (newState == 0) {
                    int offset = AndroidUtilities.dp(13.0f);
                    int backgroundPaddingTop = ChatAttachAlertPollLayout.this.parentAlert.getBackgroundPaddingTop();
                    if (((ChatAttachAlertPollLayout.this.parentAlert.scrollOffsetY[0] - backgroundPaddingTop) - offset) + backgroundPaddingTop < ActionBar.getCurrentActionBarHeight() && (holder = (RecyclerListView.Holder) ChatAttachAlertPollLayout.this.listView.findViewHolderForAdapterPosition(1)) != null && holder.itemView.getTop() > AndroidUtilities.dp(53.0f)) {
                        ChatAttachAlertPollLayout.this.listView.smoothScrollBy(0, holder.itemView.getTop() - AndroidUtilities.dp(53.0f));
                    }
                }
            }
        });
        HintView hintView2 = new HintView(context, 4);
        this.hintView = hintView2;
        hintView2.setText(LocaleController.getString("PollTapToSelect", NUM));
        this.hintView.setAlpha(0.0f);
        this.hintView.setVisibility(4);
        addView(this.hintView, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
        checkDoneButton();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatAttachAlertPollLayout  reason: not valid java name */
    public /* synthetic */ void m2191x3180e5dc(View view, int position) {
        boolean checked;
        if (position == this.addAnswerRow) {
            addNewField();
        } else if (view instanceof TextCheckCell) {
            TextCheckCell cell = (TextCheckCell) view;
            boolean wasChecksBefore = this.quizPoll;
            if (position == this.anonymousRow) {
                checked = !this.anonymousPoll;
                this.anonymousPoll = checked;
            } else if (position == this.multipleRow) {
                boolean z = !this.multipleChoise;
                this.multipleChoise = z;
                boolean checked2 = z;
                if (z && this.quizPoll) {
                    int prevSolutionRow = this.solutionRow;
                    this.quizPoll = false;
                    updateRows();
                    this.listView.setItemAnimator(this.itemAnimator);
                    RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.quizRow);
                    if (holder != null) {
                        ((TextCheckCell) holder.itemView).setChecked(false);
                    } else {
                        this.listAdapter.notifyItemChanged(this.quizRow);
                    }
                    this.listAdapter.notifyItemRangeRemoved(prevSolutionRow, 2);
                    this.listAdapter.notifyItemChanged(this.emptyRow);
                }
                checked = checked2;
            } else if (this.quizOnly == 0) {
                this.listView.setItemAnimator(this.itemAnimator);
                checked = !this.quizPoll;
                this.quizPoll = checked;
                int prevSolutionRow2 = this.solutionRow;
                updateRows();
                if (this.quizPoll) {
                    this.listAdapter.notifyItemRangeInserted(this.solutionRow, 2);
                } else {
                    this.listAdapter.notifyItemRangeRemoved(prevSolutionRow2, 2);
                }
                this.listAdapter.notifyItemChanged(this.emptyRow);
                if (this.quizPoll && this.multipleChoise) {
                    this.multipleChoise = false;
                    RecyclerView.ViewHolder holder2 = this.listView.findViewHolderForAdapterPosition(this.multipleRow);
                    if (holder2 != null) {
                        ((TextCheckCell) holder2.itemView).setChecked(false);
                    } else {
                        this.listAdapter.notifyItemChanged(this.multipleRow);
                    }
                }
                if (this.quizPoll) {
                    boolean was = false;
                    int a = 0;
                    while (true) {
                        boolean[] zArr = this.answersChecks;
                        if (a >= zArr.length) {
                            break;
                        }
                        if (was) {
                            zArr[a] = false;
                        } else if (zArr[a]) {
                            was = true;
                        }
                        a++;
                    }
                }
            } else {
                return;
            }
            if (this.hintShowed && !this.quizPoll) {
                this.hintView.hide();
            }
            int childCount = this.listView.getChildCount();
            for (int a2 = this.answerStartRow; a2 < this.answerStartRow + this.answersCount; a2++) {
                RecyclerView.ViewHolder holder3 = this.listView.findViewHolderForAdapterPosition(a2);
                if (holder3 != null && (holder3.itemView instanceof PollEditTextCell)) {
                    PollEditTextCell pollEditTextCell = (PollEditTextCell) holder3.itemView;
                    pollEditTextCell.setShowCheckBox(this.quizPoll, true);
                    pollEditTextCell.setChecked(this.answersChecks[a2 - this.answerStartRow], wasChecksBefore);
                    if (pollEditTextCell.getTop() > AndroidUtilities.dp(40.0f) && position == this.quizRow && !this.hintShowed) {
                        this.hintView.showForView(pollEditTextCell.getCheckBox(), true);
                        this.hintShowed = true;
                    }
                }
            }
            cell.setChecked(checked);
            checkDoneButton();
        }
    }

    /* access modifiers changed from: package-private */
    public int needsActionBar() {
        return 1;
    }

    /* access modifiers changed from: package-private */
    public void onResume() {
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public void onHideShowProgress(float progress) {
        this.parentAlert.doneItem.setAlpha((this.parentAlert.doneItem.isEnabled() ? 1.0f : 0.5f) * progress);
    }

    /* access modifiers changed from: package-private */
    public void onMenuItemClick(int id) {
        if (id != 40) {
            return;
        }
        if (!this.quizPoll || this.parentAlert.doneItem.getAlpha() == 1.0f) {
            TLRPC.TL_messageMediaPoll poll = new TLRPC.TL_messageMediaPoll();
            poll.poll = new TLRPC.TL_poll();
            poll.poll.multiple_choice = this.multipleChoise;
            poll.poll.quiz = this.quizPoll;
            poll.poll.public_voters = !this.anonymousPoll;
            poll.poll.question = getFixedString(this.questionString).toString();
            SerializedData serializedData = new SerializedData(10);
            int a = 0;
            while (true) {
                String[] strArr = this.answers;
                if (a >= strArr.length) {
                    break;
                }
                if (!TextUtils.isEmpty(getFixedString(strArr[a]))) {
                    TLRPC.TL_pollAnswer answer = new TLRPC.TL_pollAnswer();
                    answer.text = getFixedString(this.answers[a]).toString();
                    answer.option = new byte[1];
                    answer.option[0] = (byte) (poll.poll.answers.size() + 48);
                    poll.poll.answers.add(answer);
                    if ((this.multipleChoise || this.quizPoll) && this.answersChecks[a]) {
                        serializedData.writeByte(answer.option[0]);
                    }
                }
                a++;
            }
            HashMap<String, String> params = new HashMap<>();
            params.put("answers", Utilities.bytesToHex(serializedData.toByteArray()));
            poll.results = new TLRPC.TL_pollResults();
            CharSequence solution = getFixedString(this.solutionString);
            if (solution != null) {
                poll.results.solution = solution.toString();
                ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance(this.parentAlert.currentAccount).getEntities(new CharSequence[]{solution}, true);
                if (entities != null && !entities.isEmpty()) {
                    poll.results.solution_entities = entities;
                }
                if (!TextUtils.isEmpty(poll.results.solution)) {
                    poll.results.flags |= 16;
                }
            }
            ChatActivity chatActivity = (ChatActivity) this.parentAlert.baseFragment;
            if (chatActivity.isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(chatActivity.getParentActivity(), chatActivity.getDialogId(), new ChatAttachAlertPollLayout$$ExternalSyntheticLambda1(this, poll, params));
                return;
            }
            this.delegate.sendPoll(poll, params, true, 0);
            this.parentAlert.dismiss();
            return;
        }
        int checksCount = 0;
        for (int a2 = 0; a2 < this.answersChecks.length; a2++) {
            if (!TextUtils.isEmpty(getFixedString(this.answers[a2])) && this.answersChecks[a2]) {
                checksCount++;
            }
        }
        if (checksCount <= 0) {
            showQuizHint();
        }
    }

    /* renamed from: lambda$onMenuItemClick$1$org-telegram-ui-Components-ChatAttachAlertPollLayout  reason: not valid java name */
    public /* synthetic */ void m2192x54var_cc4(TLRPC.TL_messageMediaPoll poll, HashMap params, boolean notify, int scheduleDate) {
        this.delegate.sendPoll(poll, params, notify, scheduleDate);
        this.parentAlert.dismiss();
    }

    /* access modifiers changed from: package-private */
    public int getCurrentItemTop() {
        View child;
        if (this.listView.getChildCount() <= 0 || (child = this.listView.getChildAt(1)) == null) {
            return Integer.MAX_VALUE;
        }
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = ((int) child.getY()) - AndroidUtilities.dp(8.0f);
        int newOffset = (top <= 0 || holder == null || holder.getAdapterPosition() != 1) ? 0 : top;
        if (top >= 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        return AndroidUtilities.dp(25.0f) + newOffset;
    }

    /* access modifiers changed from: package-private */
    public int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(17.0f);
    }

    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        this.parentAlert.getSheetContainer().invalidate();
    }

    /* access modifiers changed from: package-private */
    public int getListTopPadding() {
        return this.topPadding;
    }

    /* access modifiers changed from: package-private */
    public void onPreMeasure(int availableWidth, int availableHeight) {
        int padding;
        int padding2;
        if (this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            padding = AndroidUtilities.dp(52.0f);
            this.parentAlert.setAllowNestedScroll(false);
        } else {
            if (AndroidUtilities.isTablet() != 0 || AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) {
                padding2 = (availableHeight / 5) * 2;
            } else {
                padding2 = (int) (((float) availableHeight) / 3.5f);
            }
            padding = padding2 - AndroidUtilities.dp(13.0f);
            if (padding < 0) {
                padding = 0;
            }
            this.parentAlert.setAllowNestedScroll(this.allowNesterScroll);
        }
        this.ignoreLayout = true;
        if (this.topPadding != padding) {
            this.topPadding = padding;
            this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
            this.listAdapter.notifyItemChanged(this.paddingRow);
        }
        this.ignoreLayout = false;
    }

    /* access modifiers changed from: package-private */
    public int getButtonsHideOffset() {
        return AndroidUtilities.dp(70.0f);
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: package-private */
    public void scrollToTop() {
        this.listView.smoothScrollToPosition(1);
    }

    public static CharSequence getFixedString(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }
        CharSequence text2 = AndroidUtilities.getTrimmedString(text);
        while (TextUtils.indexOf(text2, "\n\n\n") >= 0) {
            text2 = TextUtils.replace(text2, new String[]{"\n\n\n"}, new CharSequence[]{"\n\n"});
        }
        while (TextUtils.indexOf(text2, "\n\n\n") == 0) {
            text2 = TextUtils.replace(text2, new String[]{"\n\n\n"}, new CharSequence[]{"\n\n"});
        }
        return text2;
    }

    private void showQuizHint() {
        int childCount = this.listView.getChildCount();
        for (int a = this.answerStartRow; a < this.answerStartRow + this.answersCount; a++) {
            RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(a);
            if (holder != null && (holder.itemView instanceof PollEditTextCell)) {
                PollEditTextCell pollEditTextCell = (PollEditTextCell) holder.itemView;
                if (pollEditTextCell.getTop() > AndroidUtilities.dp(40.0f)) {
                    this.hintView.showForView(pollEditTextCell.getCheckBox(), true);
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkDoneButton() {
        boolean enabled = true;
        int checksCount = 0;
        if (this.quizPoll) {
            for (int a = 0; a < this.answersChecks.length; a++) {
                if (!TextUtils.isEmpty(getFixedString(this.answers[a])) && this.answersChecks[a]) {
                    checksCount++;
                }
            }
        }
        int count = 0;
        if (!TextUtils.isEmpty(getFixedString(this.solutionString)) && this.solutionString.length() > 200) {
            enabled = false;
        } else if (TextUtils.isEmpty(getFixedString(this.questionString)) || this.questionString.length() > 255) {
            enabled = false;
        }
        boolean hasAnswers = false;
        int a2 = 0;
        while (true) {
            String[] strArr = this.answers;
            if (a2 >= strArr.length) {
                break;
            }
            if (!TextUtils.isEmpty(getFixedString(strArr[a2]))) {
                hasAnswers = true;
                if (this.answers[a2].length() > 100) {
                    count = 0;
                    break;
                }
                count++;
            }
            a2++;
        }
        boolean z = true;
        if (count < 2 || (this.quizPoll && checksCount < 1)) {
            enabled = false;
        }
        if (!TextUtils.isEmpty(this.solutionString) || !TextUtils.isEmpty(this.questionString) || hasAnswers) {
            this.allowNesterScroll = false;
        } else {
            this.allowNesterScroll = true;
        }
        this.parentAlert.setAllowNestedScroll(this.allowNesterScroll);
        ActionBarMenuItem actionBarMenuItem = this.parentAlert.doneItem;
        if ((!this.quizPoll || checksCount != 0) && !enabled) {
            z = false;
        }
        actionBarMenuItem.setEnabled(z);
        this.parentAlert.doneItem.setAlpha(enabled ? 1.0f : 0.5f);
    }

    /* access modifiers changed from: private */
    public void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.paddingRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.questionHeaderRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.questionRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.questionSectionRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.answerHeaderRow = i4;
        int i6 = this.answersCount;
        if (i6 != 0) {
            this.answerStartRow = i5;
            this.rowCount = i5 + i6;
        } else {
            this.answerStartRow = -1;
        }
        if (i6 != this.answers.length) {
            int i7 = this.rowCount;
            this.rowCount = i7 + 1;
            this.addAnswerRow = i7;
        } else {
            this.addAnswerRow = -1;
        }
        int i8 = this.rowCount;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.answerSectionRow = i8;
        this.rowCount = i9 + 1;
        this.settingsHeaderRow = i9;
        TLRPC.Chat chat = ((ChatActivity) this.parentAlert.baseFragment).getCurrentChat();
        if (!ChatObject.isChannel(chat) || chat.megagroup) {
            int i10 = this.rowCount;
            this.rowCount = i10 + 1;
            this.anonymousRow = i10;
        } else {
            this.anonymousRow = -1;
        }
        int i11 = this.quizOnly;
        if (i11 != 1) {
            int i12 = this.rowCount;
            this.rowCount = i12 + 1;
            this.multipleRow = i12;
        } else {
            this.multipleRow = -1;
        }
        if (i11 == 0) {
            int i13 = this.rowCount;
            this.rowCount = i13 + 1;
            this.quizRow = i13;
        } else {
            this.quizRow = -1;
        }
        int i14 = this.rowCount;
        int i15 = i14 + 1;
        this.rowCount = i15;
        this.settingsSectionRow = i14;
        if (this.quizPoll) {
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.solutionRow = i15;
            this.rowCount = i16 + 1;
            this.solutionInfoRow = i16;
        } else {
            this.solutionRow = -1;
            this.solutionInfoRow = -1;
        }
        int i17 = this.rowCount;
        this.rowCount = i17 + 1;
        this.emptyRow = i17;
    }

    /* access modifiers changed from: package-private */
    public void onShow() {
        if (this.quizOnly == 1) {
            this.parentAlert.actionBar.setTitle(LocaleController.getString("NewQuiz", NUM));
        } else {
            this.parentAlert.actionBar.setTitle(LocaleController.getString("NewPoll", NUM));
        }
        this.parentAlert.doneItem.setVisibility(0);
        this.layoutManager.scrollToPositionWithOffset(0, 0);
    }

    /* access modifiers changed from: package-private */
    public void onHidden() {
        this.parentAlert.doneItem.setVisibility(4);
    }

    /* access modifiers changed from: package-private */
    public boolean onBackPressed() {
        if (!checkDiscard()) {
            return true;
        }
        return super.onBackPressed();
    }

    private boolean checkDiscard() {
        boolean allowDiscard = TextUtils.isEmpty(getFixedString(this.questionString));
        if (allowDiscard) {
            int a = 0;
            while (a < this.answersCount && (allowDiscard = TextUtils.isEmpty(getFixedString(this.answers[a])))) {
                a++;
            }
        }
        if (!allowDiscard) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentAlert.baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("CancelPollAlertTitle", NUM));
            builder.setMessage(LocaleController.getString("CancelPollAlertText", NUM));
            builder.setPositiveButton(LocaleController.getString("PassportDiscard", NUM), new ChatAttachAlertPollLayout$$ExternalSyntheticLambda0(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.show();
        }
        return allowDiscard;
    }

    /* renamed from: lambda$checkDiscard$2$org-telegram-ui-Components-ChatAttachAlertPollLayout  reason: not valid java name */
    public /* synthetic */ void m2190x9CLASSNAMEa(DialogInterface dialogInterface, int i) {
        this.parentAlert.dismiss();
    }

    public void setDelegate(PollCreateActivityDelegate pollCreateActivityDelegate) {
        this.delegate = pollCreateActivityDelegate;
    }

    /* access modifiers changed from: private */
    public void setTextLeft(View cell, int index) {
        int left;
        int max;
        if (cell instanceof PollEditTextCell) {
            PollEditTextCell textCell = (PollEditTextCell) cell;
            if (index == this.questionRow) {
                max = 255;
                String str = this.questionString;
                left = 255 - (str != null ? str.length() : 0);
            } else if (index == this.solutionRow) {
                max = 200;
                CharSequence charSequence = this.solutionString;
                left = 200 - (charSequence != null ? charSequence.length() : 0);
            } else {
                int max2 = this.answerStartRow;
                if (index >= max2 && index < this.answersCount + max2) {
                    int index2 = index - max2;
                    max = 100;
                    String[] strArr = this.answers;
                    left = 100 - (strArr[index2] != null ? strArr[index2].length() : 0);
                } else {
                    return;
                }
            }
            if (((float) left) <= ((float) max) - (((float) max) * 0.7f)) {
                textCell.setText2(String.format("%d", new Object[]{Integer.valueOf(left)}));
                SimpleTextView textView = textCell.getTextView2();
                String key = left < 0 ? "windowBackgroundWhiteRedText5" : "windowBackgroundWhiteGrayText3";
                textView.setTextColor(getThemedColor(key));
                textView.setTag(key);
                return;
            }
            textCell.setText2("");
        }
    }

    /* access modifiers changed from: private */
    public void addNewField() {
        this.listView.setItemAnimator(this.itemAnimator);
        boolean[] zArr = this.answersChecks;
        int i = this.answersCount;
        zArr[i] = false;
        int i2 = i + 1;
        this.answersCount = i2;
        if (i2 == this.answers.length) {
            this.listAdapter.notifyItemRemoved(this.addAnswerRow);
        }
        this.listAdapter.notifyItemInserted(this.addAnswerRow);
        updateRows();
        this.requestFieldFocusAtPosition = (this.answerStartRow + this.answersCount) - 1;
        this.listAdapter.notifyItemChanged(this.answerSectionRow);
        this.listAdapter.notifyItemChanged(this.emptyRow);
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ChatAttachAlertPollLayout.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell cell = (HeaderCell) holder.itemView;
                    if (position == ChatAttachAlertPollLayout.this.questionHeaderRow) {
                        cell.getTextView().setGravity(19);
                        cell.setText(LocaleController.getString("PollQuestion", NUM));
                        return;
                    }
                    cell.getTextView().setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                    if (position == ChatAttachAlertPollLayout.this.answerHeaderRow) {
                        if (ChatAttachAlertPollLayout.this.quizOnly == 1) {
                            cell.setText(LocaleController.getString("QuizAnswers", NUM));
                            return;
                        } else {
                            cell.setText(LocaleController.getString("AnswerOptions", NUM));
                            return;
                        }
                    } else if (position == ChatAttachAlertPollLayout.this.settingsHeaderRow) {
                        cell.setText(LocaleController.getString("Settings", NUM));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell cell2 = (TextInfoPrivacyCell) holder.itemView;
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(ChatAttachAlertPollLayout.this.getThemedColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    combinedDrawable.setFullsize(true);
                    cell2.setBackgroundDrawable(combinedDrawable);
                    if (position == ChatAttachAlertPollLayout.this.solutionInfoRow) {
                        cell2.setText(LocaleController.getString("AddAnExplanationInfo", NUM));
                        return;
                    } else if (position == ChatAttachAlertPollLayout.this.settingsSectionRow) {
                        if (ChatAttachAlertPollLayout.this.quizOnly != 0) {
                            cell2.setText((CharSequence) null);
                            return;
                        } else {
                            cell2.setText(LocaleController.getString("QuizInfo", NUM));
                            return;
                        }
                    } else if (10 - ChatAttachAlertPollLayout.this.answersCount <= 0) {
                        cell2.setText(LocaleController.getString("AddAnOptionInfoMax", NUM));
                        return;
                    } else {
                        cell2.setText(LocaleController.formatString("AddAnOptionInfo", NUM, LocaleController.formatPluralString("Option", 10 - ChatAttachAlertPollLayout.this.answersCount)));
                        return;
                    }
                case 3:
                    TextCell textCell = (TextCell) holder.itemView;
                    textCell.setColors((String) null, "windowBackgroundWhiteBlueText4");
                    Drawable drawable1 = this.mContext.getResources().getDrawable(NUM);
                    Drawable drawable2 = this.mContext.getResources().getDrawable(NUM);
                    drawable1.setColorFilter(new PorterDuffColorFilter(ChatAttachAlertPollLayout.this.getThemedColor("switchTrackChecked"), PorterDuff.Mode.MULTIPLY));
                    drawable2.setColorFilter(new PorterDuffColorFilter(ChatAttachAlertPollLayout.this.getThemedColor("checkboxCheck"), PorterDuff.Mode.MULTIPLY));
                    textCell.setTextAndIcon(LocaleController.getString("AddAnOption", NUM), (Drawable) new CombinedDrawable(drawable1, drawable2), false);
                    return;
                case 6:
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position == ChatAttachAlertPollLayout.this.anonymousRow) {
                        String string = LocaleController.getString("PollAnonymous", NUM);
                        boolean access$1400 = ChatAttachAlertPollLayout.this.anonymousPoll;
                        if (!(ChatAttachAlertPollLayout.this.multipleRow == -1 && ChatAttachAlertPollLayout.this.quizRow == -1)) {
                            z = true;
                        }
                        checkCell.setTextAndCheck(string, access$1400, z);
                        checkCell.setEnabled(true, (ArrayList<Animator>) null);
                        return;
                    } else if (position == ChatAttachAlertPollLayout.this.multipleRow) {
                        String string2 = LocaleController.getString("PollMultiple", NUM);
                        boolean access$1700 = ChatAttachAlertPollLayout.this.multipleChoise;
                        if (ChatAttachAlertPollLayout.this.quizRow != -1) {
                            z = true;
                        }
                        checkCell.setTextAndCheck(string2, access$1700, z);
                        checkCell.setEnabled(true, (ArrayList<Animator>) null);
                        return;
                    } else if (position == ChatAttachAlertPollLayout.this.quizRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("PollQuiz", NUM), ChatAttachAlertPollLayout.this.quizPoll, false);
                        if (ChatAttachAlertPollLayout.this.quizOnly == 0) {
                            z = true;
                        }
                        checkCell.setEnabled(z, (ArrayList<Animator>) null);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            CharSequence charSequence = "";
            if (viewType == 4) {
                PollEditTextCell textCell = (PollEditTextCell) holder.itemView;
                textCell.setTag(1);
                if (ChatAttachAlertPollLayout.this.questionString != null) {
                    charSequence = ChatAttachAlertPollLayout.this.questionString;
                }
                textCell.setTextAndHint(charSequence, LocaleController.getString("QuestionHint", NUM), false);
                textCell.setTag((Object) null);
                ChatAttachAlertPollLayout.this.setTextLeft(holder.itemView, holder.getAdapterPosition());
            } else if (viewType == 5) {
                int position = holder.getAdapterPosition();
                PollEditTextCell textCell2 = (PollEditTextCell) holder.itemView;
                textCell2.setTag(1);
                textCell2.setTextAndHint(ChatAttachAlertPollLayout.this.answers[position - ChatAttachAlertPollLayout.this.answerStartRow], LocaleController.getString("OptionHint", NUM), true);
                textCell2.setTag((Object) null);
                if (ChatAttachAlertPollLayout.this.requestFieldFocusAtPosition == position) {
                    EditTextBoldCursor editText = textCell2.getTextView();
                    editText.requestFocus();
                    AndroidUtilities.showKeyboard(editText);
                    int unused = ChatAttachAlertPollLayout.this.requestFieldFocusAtPosition = -1;
                }
                ChatAttachAlertPollLayout.this.setTextLeft(holder.itemView, position);
            } else if (viewType == 7) {
                PollEditTextCell textCell3 = (PollEditTextCell) holder.itemView;
                textCell3.setTag(1);
                if (ChatAttachAlertPollLayout.this.solutionString != null) {
                    charSequence = ChatAttachAlertPollLayout.this.solutionString;
                }
                textCell3.setTextAndHint(charSequence, LocaleController.getString("AddAnExplanation", NUM), false);
                textCell3.setTag((Object) null);
                ChatAttachAlertPollLayout.this.setTextLeft(holder.itemView, holder.getAdapterPosition());
            }
        }

        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 4) {
                EditTextBoldCursor editText = ((PollEditTextCell) holder.itemView).getTextView();
                if (editText.isFocused()) {
                    editText.clearFocus();
                    AndroidUtilities.hideKeyboard(editText);
                }
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == ChatAttachAlertPollLayout.this.addAnswerRow || position == ChatAttachAlertPollLayout.this.anonymousRow || position == ChatAttachAlertPollLayout.this.multipleRow || (ChatAttachAlertPollLayout.this.quizOnly == 0 && position == ChatAttachAlertPollLayout.this.quizRow);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$3} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v13, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v14, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v15, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v16, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v17, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v18, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v19, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v20, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r0v3, types: [org.telegram.ui.Cells.ShadowSectionCell, android.view.View] */
        /* JADX WARNING: type inference failed for: r0v4, types: [org.telegram.ui.Cells.TextInfoPrivacyCell] */
        /* JADX WARNING: type inference failed for: r0v5, types: [org.telegram.ui.Cells.TextCell] */
        /* JADX WARNING: type inference failed for: r0v7, types: [org.telegram.ui.Cells.TextCheckCell] */
        /* JADX WARNING: type inference failed for: r0v9, types: [android.view.View, org.telegram.ui.Components.ChatAttachAlertPollLayout$EmptyView] */
        /* JADX WARNING: type inference failed for: r0v10, types: [org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$5] */
        /* JADX WARNING: type inference failed for: r0v11, types: [org.telegram.ui.Cells.PollEditTextCell, org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$6] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 3 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r11, int r12) {
            /*
                r10 = this;
                r0 = 0
                java.lang.String r1 = "windowBackgroundGray"
                r2 = 1
                switch(r12) {
                    case 0: goto L_0x00c5;
                    case 1: goto L_0x009b;
                    case 2: goto L_0x0093;
                    case 3: goto L_0x008b;
                    case 4: goto L_0x0076;
                    case 5: goto L_0x0007;
                    case 6: goto L_0x006e;
                    case 7: goto L_0x0059;
                    case 8: goto L_0x0047;
                    case 9: goto L_0x003e;
                    default: goto L_0x0007;
                }
            L_0x0007:
                org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$6 r0 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$6
                android.content.Context r1 = r10.mContext
                org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$$ExternalSyntheticLambda0 r3 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$$ExternalSyntheticLambda0
                r3.<init>(r10)
                r0.<init>(r1, r3)
                org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$7 r1 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$7
                r1.<init>(r0)
                r0.addTextWatcher(r1)
                r0.setShowNextButton(r2)
                org.telegram.ui.Components.EditTextBoldCursor r1 = r0.getTextView()
                int r2 = r1.getImeOptions()
                r2 = r2 | 5
                r1.setImeOptions(r2)
                org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$$ExternalSyntheticLambda2
                r2.<init>(r10, r0)
                r1.setOnEditorActionListener(r2)
                org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$$ExternalSyntheticLambda1 r2 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$$ExternalSyntheticLambda1
                r2.<init>(r0)
                r1.setOnKeyListener(r2)
                r2 = r0
                goto L_0x00d5
            L_0x003e:
                org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$5 r0 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$5
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                goto L_0x00d5
            L_0x0047:
                org.telegram.ui.Components.ChatAttachAlertPollLayout$EmptyView r0 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$EmptyView
                android.content.Context r2 = r10.mContext
                r0.<init>(r2)
                org.telegram.ui.Components.ChatAttachAlertPollLayout r2 = org.telegram.ui.Components.ChatAttachAlertPollLayout.this
                int r1 = r2.getThemedColor(r1)
                r0.setBackgroundColor(r1)
                goto L_0x00d5
            L_0x0059:
                org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$3 r1 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$3
                android.content.Context r3 = r10.mContext
                r1.<init>(r3, r2, r0)
                r0 = r1
                r0.createErrorTextView()
                org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$4 r1 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$4
                r1.<init>(r0)
                r0.addTextWatcher(r1)
                r1 = r0
                goto L_0x00d5
            L_0x006e:
                org.telegram.ui.Cells.TextCheckCell r0 = new org.telegram.ui.Cells.TextCheckCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                goto L_0x00d5
            L_0x0076:
                org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$1 r1 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$1
                android.content.Context r2 = r10.mContext
                r1.<init>(r2, r0)
                r0 = r1
                r0.createErrorTextView()
                org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$2 r1 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$2
                r1.<init>(r0)
                r0.addTextWatcher(r1)
                r1 = r0
                goto L_0x00d5
            L_0x008b:
                org.telegram.ui.Cells.TextCell r0 = new org.telegram.ui.Cells.TextCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                goto L_0x00d5
            L_0x0093:
                org.telegram.ui.Cells.TextInfoPrivacyCell r0 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                goto L_0x00d5
            L_0x009b:
                org.telegram.ui.Cells.ShadowSectionCell r0 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r3 = r10.mContext
                r0.<init>(r3)
                android.content.Context r3 = r10.mContext
                r4 = 2131165465(0x7var_, float:1.7945148E38)
                java.lang.String r5 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r3, (int) r4, (java.lang.String) r5)
                org.telegram.ui.Components.CombinedDrawable r4 = new org.telegram.ui.Components.CombinedDrawable
                android.graphics.drawable.ColorDrawable r5 = new android.graphics.drawable.ColorDrawable
                org.telegram.ui.Components.ChatAttachAlertPollLayout r6 = org.telegram.ui.Components.ChatAttachAlertPollLayout.this
                int r1 = r6.getThemedColor(r1)
                r5.<init>(r1)
                r4.<init>(r5, r3)
                r1 = r4
                r1.setFullsize(r2)
                r0.setBackgroundDrawable(r1)
                goto L_0x00d5
            L_0x00c5:
                org.telegram.ui.Cells.HeaderCell r0 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r5 = r10.mContext
                r7 = 21
                r8 = 15
                r9 = 0
                java.lang.String r6 = "windowBackgroundWhiteBlueHeader"
                r4 = r0
                r4.<init>(r5, r6, r7, r8, r9)
            L_0x00d5:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r2 = -1
                r3 = -2
                r1.<init>((int) r2, (int) r3)
                r0.setLayoutParams(r1)
                org.telegram.ui.Components.RecyclerListView$Holder r1 = new org.telegram.ui.Components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPollLayout.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-Components-ChatAttachAlertPollLayout$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m2193xd77380a3(View v) {
            int position;
            if (v.getTag() == null) {
                v.setTag(1);
                PollEditTextCell p = (PollEditTextCell) v.getParent();
                RecyclerView.ViewHolder holder = ChatAttachAlertPollLayout.this.listView.findContainingViewHolder(p);
                if (holder != null && (position = holder.getAdapterPosition()) != -1) {
                    ChatAttachAlertPollLayout.this.listView.setItemAnimator(ChatAttachAlertPollLayout.this.itemAnimator);
                    int index = position - ChatAttachAlertPollLayout.this.answerStartRow;
                    ChatAttachAlertPollLayout.this.listAdapter.notifyItemRemoved(position);
                    System.arraycopy(ChatAttachAlertPollLayout.this.answers, index + 1, ChatAttachAlertPollLayout.this.answers, index, (ChatAttachAlertPollLayout.this.answers.length - 1) - index);
                    System.arraycopy(ChatAttachAlertPollLayout.this.answersChecks, index + 1, ChatAttachAlertPollLayout.this.answersChecks, index, (ChatAttachAlertPollLayout.this.answersChecks.length - 1) - index);
                    ChatAttachAlertPollLayout.this.answers[ChatAttachAlertPollLayout.this.answers.length - 1] = null;
                    ChatAttachAlertPollLayout.this.answersChecks[ChatAttachAlertPollLayout.this.answersChecks.length - 1] = false;
                    ChatAttachAlertPollLayout.access$1210(ChatAttachAlertPollLayout.this);
                    if (ChatAttachAlertPollLayout.this.answersCount == ChatAttachAlertPollLayout.this.answers.length - 1) {
                        ChatAttachAlertPollLayout.this.listAdapter.notifyItemInserted((ChatAttachAlertPollLayout.this.answerStartRow + ChatAttachAlertPollLayout.this.answers.length) - 1);
                    }
                    RecyclerView.ViewHolder holder2 = ChatAttachAlertPollLayout.this.listView.findViewHolderForAdapterPosition(position - 1);
                    EditTextBoldCursor editText = p.getTextView();
                    if (holder2 != null && (holder2.itemView instanceof PollEditTextCell)) {
                        ((PollEditTextCell) holder2.itemView).getTextView().requestFocus();
                    } else if (editText.isFocused()) {
                        AndroidUtilities.hideKeyboard(editText);
                    }
                    editText.clearFocus();
                    ChatAttachAlertPollLayout.this.checkDoneButton();
                    ChatAttachAlertPollLayout.this.updateRows();
                    ChatAttachAlertPollLayout.this.listAdapter.notifyItemChanged(ChatAttachAlertPollLayout.this.answerSectionRow);
                    ChatAttachAlertPollLayout.this.listAdapter.notifyItemChanged(ChatAttachAlertPollLayout.this.emptyRow);
                }
            }
        }

        /* renamed from: lambda$onCreateViewHolder$1$org-telegram-ui-Components-ChatAttachAlertPollLayout$ListAdapter  reason: not valid java name */
        public /* synthetic */ boolean m2194x41a308c2(PollEditTextCell cell, TextView v, int actionId, KeyEvent event) {
            int position;
            if (actionId != 5) {
                return false;
            }
            RecyclerView.ViewHolder holder = ChatAttachAlertPollLayout.this.listView.findContainingViewHolder(cell);
            if (!(holder == null || (position = holder.getAdapterPosition()) == -1)) {
                int index = position - ChatAttachAlertPollLayout.this.answerStartRow;
                if (index == ChatAttachAlertPollLayout.this.answersCount - 1 && ChatAttachAlertPollLayout.this.answersCount < 10) {
                    ChatAttachAlertPollLayout.this.addNewField();
                } else if (index == ChatAttachAlertPollLayout.this.answersCount - 1) {
                    AndroidUtilities.hideKeyboard(cell.getTextView());
                } else {
                    RecyclerView.ViewHolder holder2 = ChatAttachAlertPollLayout.this.listView.findViewHolderForAdapterPosition(position + 1);
                    if (holder2 != null && (holder2.itemView instanceof PollEditTextCell)) {
                        ((PollEditTextCell) holder2.itemView).getTextView().requestFocus();
                    }
                }
            }
            return true;
        }

        static /* synthetic */ boolean lambda$onCreateViewHolder$2(PollEditTextCell cell, View v, int keyCode, KeyEvent event) {
            EditTextBoldCursor field = (EditTextBoldCursor) v;
            if (keyCode != 67 || event.getAction() != 0 || field.length() != 0) {
                return false;
            }
            cell.callOnDelete();
            return true;
        }

        public int getItemViewType(int position) {
            if (position == ChatAttachAlertPollLayout.this.questionHeaderRow || position == ChatAttachAlertPollLayout.this.answerHeaderRow || position == ChatAttachAlertPollLayout.this.settingsHeaderRow) {
                return 0;
            }
            if (position == ChatAttachAlertPollLayout.this.questionSectionRow) {
                return 1;
            }
            if (position == ChatAttachAlertPollLayout.this.answerSectionRow || position == ChatAttachAlertPollLayout.this.settingsSectionRow || position == ChatAttachAlertPollLayout.this.solutionInfoRow) {
                return 2;
            }
            if (position == ChatAttachAlertPollLayout.this.addAnswerRow) {
                return 3;
            }
            if (position == ChatAttachAlertPollLayout.this.questionRow) {
                return 4;
            }
            if (position == ChatAttachAlertPollLayout.this.solutionRow) {
                return 7;
            }
            if (position == ChatAttachAlertPollLayout.this.anonymousRow || position == ChatAttachAlertPollLayout.this.multipleRow || position == ChatAttachAlertPollLayout.this.quizRow) {
                return 6;
            }
            if (position == ChatAttachAlertPollLayout.this.emptyRow) {
                return 8;
            }
            if (position == ChatAttachAlertPollLayout.this.paddingRow) {
                return 9;
            }
            return 5;
        }

        public void swapElements(int fromIndex, int toIndex) {
            int idx1 = fromIndex - ChatAttachAlertPollLayout.this.answerStartRow;
            int idx2 = toIndex - ChatAttachAlertPollLayout.this.answerStartRow;
            if (idx1 >= 0 && idx2 >= 0 && idx1 < ChatAttachAlertPollLayout.this.answersCount && idx2 < ChatAttachAlertPollLayout.this.answersCount) {
                String from = ChatAttachAlertPollLayout.this.answers[idx1];
                ChatAttachAlertPollLayout.this.answers[idx1] = ChatAttachAlertPollLayout.this.answers[idx2];
                ChatAttachAlertPollLayout.this.answers[idx2] = from;
                boolean temp = ChatAttachAlertPollLayout.this.answersChecks[idx1];
                ChatAttachAlertPollLayout.this.answersChecks[idx1] = ChatAttachAlertPollLayout.this.answersChecks[idx2];
                ChatAttachAlertPollLayout.this.answersChecks[idx2] = temp;
                notifyItemMoved(fromIndex, toIndex);
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{EmptyView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"deleteImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"moveImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{PollEditTextCell.class}, new String[]{"deleteImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menuSelector"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{PollEditTextCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{PollEditTextCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{PollEditTextCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        return themeDescriptions;
    }
}
