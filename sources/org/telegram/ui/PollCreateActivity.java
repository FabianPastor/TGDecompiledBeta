package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.DefaultItemAnimator;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC.TL_poll;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.TL_pollResults;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class PollCreateActivity extends BaseFragment {
    private static final int MAX_ANSWER_LENGTH = 100;
    private static final int MAX_QUESTION_LENGTH = 255;
    private static final int done_button = 1;
    private int addAnswerRow;
    private int answerHeaderRow;
    private int answerSectionRow;
    private int answerStartRow;
    private String[] answers = new String[10];
    private int answersCount = 1;
    private PollCreateActivityDelegate delegate;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimation;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private ContextProgressView progressView;
    private int questionHeaderRow;
    private int questionRow;
    private int questionSectionRow;
    private String questionString;
    private int requestFieldFocusAtPosition = -1;
    private int rowCount;

    public interface PollCreateActivityDelegate {
        void sendPoll(TL_messageMediaPoll tL_messageMediaPoll);
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return PollCreateActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            PollEditTextCell textCell;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell cell = holder.itemView;
                    if (position == PollCreateActivity.this.questionHeaderRow) {
                        cell.setText(LocaleController.getString("Question", R.string.Question));
                        return;
                    } else if (position == PollCreateActivity.this.answerHeaderRow) {
                        cell.setText(LocaleController.getString("PollOptions", R.string.PollOptions));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell cell2 = holder.itemView;
                    cell2.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                    if (10 - PollCreateActivity.this.answersCount <= 0) {
                        cell2.setText(LocaleController.getString("AddAnOptionInfoMax", R.string.AddAnOptionInfoMax));
                        return;
                    } else {
                        cell2.setText(LocaleController.formatString("AddAnOptionInfo", R.string.AddAnOptionInfo, LocaleController.formatPluralString("Option", 10 - PollCreateActivity.this.answersCount)));
                        return;
                    }
                case 3:
                    TextSettingsCell textCell2 = holder.itemView;
                    textCell2.setTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
                    textCell2.setText(LocaleController.getString("AddAnOption", R.string.AddAnOption), false);
                    return;
                case 4:
                    textCell = holder.itemView;
                    textCell.setTag(Integer.valueOf(1));
                    textCell.setTextAndHint(PollCreateActivity.this.questionString != null ? PollCreateActivity.this.questionString : "", LocaleController.getString("QuestionHint", R.string.QuestionHint), false);
                    textCell.setTag(null);
                    return;
                case 5:
                    textCell = (PollEditTextCell) holder.itemView;
                    textCell.setTag(Integer.valueOf(1));
                    textCell.setTextAndHint(PollCreateActivity.this.answers[position - PollCreateActivity.this.answerStartRow], LocaleController.getString("OptionHint", R.string.OptionHint), true);
                    textCell.setTag(null);
                    if (PollCreateActivity.this.requestFieldFocusAtPosition == position) {
                        EditTextBoldCursor editText = textCell.getTextView();
                        editText.requestFocus();
                        AndroidUtilities.showKeyboard(editText);
                        PollCreateActivity.this.requestFieldFocusAtPosition = -1;
                    }
                    PollCreateActivity.this.setTextLeft(holder.itemView, position - PollCreateActivity.this.answerStartRow);
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(ViewHolder holder) {
            switch (holder.getItemViewType()) {
                case 0:
                    PollCreateActivity.this.setTextLeft(holder.itemView, holder.getAdapterPosition() == PollCreateActivity.this.questionHeaderRow ? -1 : 0);
                    return;
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getAdapterPosition() == PollCreateActivity.this.addAnswerRow;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            final View cell;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(this.mContext, false, 21, 15, true);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 3:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 4:
                    cell = new PollEditTextCell(this.mContext, null);
                    cell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    cell.addTextWatcher(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        public void afterTextChanged(Editable s) {
                            if (cell.getTag() == null) {
                                PollCreateActivity.this.questionString = s.toString();
                                ViewHolder holder = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(PollCreateActivity.this.questionHeaderRow);
                                if (holder != null) {
                                    PollCreateActivity.this.setTextLeft(holder.itemView, -1);
                                }
                                PollCreateActivity.this.checkDoneButton();
                            }
                        }
                    });
                    view = cell;
                    break;
                default:
                    cell = new PollEditTextCell(this.mContext, new PollCreateActivity$ListAdapter$$Lambda$0(this)) {
                        protected boolean drawDivider() {
                            ViewHolder holder = PollCreateActivity.this.listView.findContainingViewHolder(this);
                            if (holder != null) {
                                int position = holder.getAdapterPosition();
                                if (PollCreateActivity.this.answersCount == 10 && position == (PollCreateActivity.this.answerStartRow + PollCreateActivity.this.answersCount) - 1) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    };
                    cell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    cell.addTextWatcher(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        public void afterTextChanged(Editable s) {
                            ViewHolder holder = PollCreateActivity.this.listView.findContainingViewHolder(cell);
                            if (holder != null) {
                                int index = holder.getAdapterPosition() - PollCreateActivity.this.answerStartRow;
                                if (index >= 0 && index < PollCreateActivity.this.answers.length) {
                                    PollCreateActivity.this.answers[index] = s.toString();
                                    PollCreateActivity.this.setTextLeft(cell, index);
                                    PollCreateActivity.this.checkDoneButton();
                                }
                            }
                        }
                    });
                    cell.setShowNextButton(true);
                    EditTextBoldCursor editText = cell.getTextView();
                    editText.setImeOptions(editText.getImeOptions() | 5);
                    editText.setOnEditorActionListener(new PollCreateActivity$ListAdapter$$Lambda$1(this, cell));
                    editText.setOnKeyListener(new PollCreateActivity$ListAdapter$$Lambda$2(cell));
                    view = cell;
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        final /* synthetic */ void lambda$onCreateViewHolder$0$PollCreateActivity$ListAdapter(View v) {
            if (v.getTag() == null) {
                v.setTag(Integer.valueOf(1));
                ViewHolder holder = PollCreateActivity.this.listView.findContainingViewHolder((View) v.getParent());
                if (holder != null) {
                    int position = holder.getAdapterPosition();
                    int index = position - PollCreateActivity.this.answerStartRow;
                    PollCreateActivity.this.listAdapter.notifyItemRemoved(holder.getAdapterPosition());
                    System.arraycopy(PollCreateActivity.this.answers, index + 1, PollCreateActivity.this.answers, index, (PollCreateActivity.this.answers.length - 1) - index);
                    PollCreateActivity.this.answers[PollCreateActivity.this.answers.length - 1] = null;
                    PollCreateActivity.this.answersCount = PollCreateActivity.this.answersCount - 1;
                    if (PollCreateActivity.this.answersCount == PollCreateActivity.this.answers.length - 1) {
                        PollCreateActivity.this.listAdapter.notifyItemInserted((PollCreateActivity.this.answerStartRow + PollCreateActivity.this.answers.length) - 1);
                    }
                    holder = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(position - 1);
                    if (holder != null && (holder.itemView instanceof PollEditTextCell)) {
                        holder.itemView.getTextView().requestFocus();
                    }
                    PollCreateActivity.this.checkDoneButton();
                    PollCreateActivity.this.updateRows();
                    PollCreateActivity.this.listAdapter.notifyItemChanged(PollCreateActivity.this.answerSectionRow);
                }
            }
        }

        final /* synthetic */ boolean lambda$onCreateViewHolder$1$PollCreateActivity$ListAdapter(PollEditTextCell cell, TextView v, int actionId, KeyEvent event) {
            if (actionId != 5) {
                return false;
            }
            ViewHolder holder = PollCreateActivity.this.listView.findContainingViewHolder(cell);
            if (holder != null) {
                int position = holder.getAdapterPosition();
                int index = position - PollCreateActivity.this.answerStartRow;
                if (index == PollCreateActivity.this.answersCount - 1 && PollCreateActivity.this.answersCount < 10) {
                    PollCreateActivity.this.addNewField();
                } else if (index == PollCreateActivity.this.answersCount - 1) {
                    AndroidUtilities.hideKeyboard(cell.getTextView());
                } else {
                    holder = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(position + 1);
                    if (holder != null && (holder.itemView instanceof PollEditTextCell)) {
                        holder.itemView.getTextView().requestFocus();
                    }
                }
            }
            return true;
        }

        static final /* synthetic */ boolean lambda$onCreateViewHolder$2$PollCreateActivity$ListAdapter(PollEditTextCell cell, View v, int keyCode, KeyEvent event) {
            EditTextBoldCursor field = (EditTextBoldCursor) v;
            if (keyCode != 67 || event.getAction() != 0 || field.length() != 0) {
                return false;
            }
            cell.callOnDelete();
            return true;
        }

        public int getItemViewType(int position) {
            if (position == PollCreateActivity.this.questionHeaderRow || position == PollCreateActivity.this.answerHeaderRow) {
                return 0;
            }
            if (position == PollCreateActivity.this.questionSectionRow) {
                return 1;
            }
            if (position == PollCreateActivity.this.answerSectionRow) {
                return 2;
            }
            if (position == PollCreateActivity.this.addAnswerRow) {
                return 3;
            }
            if (position == PollCreateActivity.this.questionRow) {
                return 4;
            }
            return 5;
        }

        public void swapElements(int fromIndex, int toIndex) {
            int idx1 = fromIndex - PollCreateActivity.this.answerStartRow;
            int idx2 = toIndex - PollCreateActivity.this.answerStartRow;
            if (idx1 >= 0 && idx2 >= 0 && idx1 < PollCreateActivity.this.answersCount && idx2 < PollCreateActivity.this.answersCount) {
                String from = PollCreateActivity.this.answers[idx1];
                PollCreateActivity.this.answers[idx1] = PollCreateActivity.this.answers[idx2];
                PollCreateActivity.this.answers[idx2] = from;
                notifyItemMoved(fromIndex, toIndex);
            }
        }
    }

    public class TouchHelperCallback extends Callback {
        public boolean isLongPressDragEnabled() {
            return true;
        }

        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 5) {
                return Callback.makeMovementFlags(0, 0);
            }
            return Callback.makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, ViewHolder source, ViewHolder target) {
            if (source.getItemViewType() != target.getItemViewType()) {
                return false;
            }
            PollCreateActivity.this.listAdapter.swapElements(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        public void onChildDraw(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        public void onSelectedChanged(ViewHolder viewHolder, int actionState) {
            if (actionState != 0) {
                PollCreateActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        public void onSwiped(ViewHolder viewHolder, int direction) {
        }

        public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("NewPoll", R.string.NewPoll));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (PollCreateActivity.this.checkDiscard()) {
                        PollCreateActivity.this.lambda$checkDiscard$70$PassportActivity();
                    }
                } else if (id == 1) {
                    TL_messageMediaPoll poll = new TL_messageMediaPoll();
                    poll.poll = new TL_poll();
                    poll.poll.question = PollCreateActivity.this.getFixedString(PollCreateActivity.this.questionString);
                    for (int a = 0; a < PollCreateActivity.this.answers.length; a++) {
                        if (!TextUtils.isEmpty(PollCreateActivity.this.getFixedString(PollCreateActivity.this.answers[a]))) {
                            TL_pollAnswer answer = new TL_pollAnswer();
                            answer.text = PollCreateActivity.this.getFixedString(PollCreateActivity.this.answers[a]);
                            answer.option = new byte[1];
                            answer.option[0] = (byte) (poll.poll.answers.size() + 48);
                            poll.poll.answers.add(answer);
                        }
                    }
                    poll.results = new TL_pollResults();
                    PollCreateActivity.this.delegate.sendPoll(poll);
                    PollCreateActivity.this.lambda$checkDiscard$70$PassportActivity();
                }
            }
        });
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.progressView = new ContextProgressView(context, 1);
        this.progressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.1f);
        this.progressView.setScaleY(0.1f);
        this.progressView.setVisibility(4);
        this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                rectangle.bottom += AndroidUtilities.dp(60.0f);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        this.listView.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.listView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new PollCreateActivity$$Lambda$0(this));
        checkDoneButton();
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$0$PollCreateActivity(View view, int position) {
        if (position == this.addAnswerRow) {
            addNewField();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.runOnUIThread(new PollCreateActivity$$Lambda$1(this), 100);
        }
    }

    final /* synthetic */ void lambda$onTransitionAnimationEnd$1$PollCreateActivity() {
        if (this.listView != null) {
            ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.questionRow);
            if (holder != null) {
                EditTextBoldCursor editText = holder.itemView.getTextView();
                editText.requestFocus();
                AndroidUtilities.showKeyboard(editText);
            }
        }
    }

    private String getFixedString(String text) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }
        text = AndroidUtilities.getTrimmedString(text).toString();
        while (text.contains("\n\n\n")) {
            text = text.replace("\n\n\n", "\n\n");
        }
        while (text.startsWith("\n\n\n")) {
            text = text.replace("\n\n\n", "\n\n");
        }
        String str = text;
        return text;
    }

    private void checkDoneButton() {
        boolean enabled = true;
        if (TextUtils.isEmpty(getFixedString(this.questionString)) || this.questionString.length() > 255) {
            enabled = false;
        } else {
            int count = 0;
            for (int a = 0; a < this.answers.length; a++) {
                if (!TextUtils.isEmpty(getFixedString(this.answers[a]))) {
                    if (this.answers[a].length() > 100) {
                        count = 0;
                        break;
                    }
                    count++;
                }
            }
            if (count < 2) {
                enabled = false;
            }
        }
        this.doneItem.setEnabled(enabled);
        this.doneItem.setAlpha(enabled ? 1.0f : 0.5f);
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.questionHeaderRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.questionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.questionSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.answerHeaderRow = i;
        if (this.answersCount != 0) {
            this.answerStartRow = this.rowCount;
            this.rowCount += this.answersCount;
        } else {
            this.answerStartRow = -1;
        }
        if (this.answersCount != this.answers.length) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.addAnswerRow = i;
        } else {
            this.addAnswerRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.answerSectionRow = i;
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    private boolean checkDiscard() {
        boolean allowDiscard = TextUtils.isEmpty(getFixedString(this.questionString));
        if (allowDiscard) {
            for (int a = 0; a < this.answersCount; a++) {
                allowDiscard = TextUtils.isEmpty(getFixedString(this.answers[a]));
                if (!allowDiscard) {
                    break;
                }
            }
        }
        if (!allowDiscard) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("CancelPollAlertTitle", R.string.CancelPollAlertTitle));
            builder.setMessage(LocaleController.getString("CancelPollAlertText", R.string.CancelPollAlertText));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new PollCreateActivity$$Lambda$2(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        }
        return allowDiscard;
    }

    public void setDelegate(PollCreateActivityDelegate pollCreateActivityDelegate) {
        this.delegate = pollCreateActivityDelegate;
    }

    private void showEditDoneProgress(final boolean show) {
        if (this.doneItemAnimation != null) {
            this.doneItemAnimation.cancel();
        }
        this.doneItemAnimation = new AnimatorSet();
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (show) {
            this.progressView.setVisibility(0);
            this.doneItem.setEnabled(false);
            animatorSet = this.doneItemAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), View.SCALE_X, new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), View.SCALE_Y, new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), View.ALPHA, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
        } else {
            this.doneItem.getImageView().setVisibility(0);
            this.doneItem.setEnabled(true);
            animatorSet = this.doneItemAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), View.SCALE_X, new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), View.SCALE_Y, new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), View.ALPHA, new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
        }
        this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (PollCreateActivity.this.doneItemAnimation != null && PollCreateActivity.this.doneItemAnimation.equals(animation)) {
                    if (show) {
                        PollCreateActivity.this.doneItem.getImageView().setVisibility(4);
                    } else {
                        PollCreateActivity.this.progressView.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (PollCreateActivity.this.doneItemAnimation != null && PollCreateActivity.this.doneItemAnimation.equals(animation)) {
                    PollCreateActivity.this.doneItemAnimation = null;
                }
            }
        });
        this.doneItemAnimation.setDuration(150);
        this.doneItemAnimation.start();
    }

    private void setTextLeft(View cell, int index) {
        int length;
        int left;
        SimpleTextView textView;
        String key;
        if (cell instanceof HeaderCell) {
            HeaderCell headerCell = (HeaderCell) cell;
            if (index == -1) {
                if (this.questionString != null) {
                    length = this.questionString.length();
                } else {
                    length = 0;
                }
                left = 255 - length;
                if (((float) left) <= 76.5f) {
                    headerCell.setText2(String.format("%d", new Object[]{Integer.valueOf(left)}));
                    textView = headerCell.getTextView2();
                    key = left < 0 ? "windowBackgroundWhiteRedText5" : "windowBackgroundWhiteGrayText3";
                    textView.setTextColor(Theme.getColor(key));
                    textView.setTag(key);
                    return;
                }
                headerCell.setText2("");
                return;
            }
            headerCell.setText2("");
        } else if ((cell instanceof PollEditTextCell) && index >= 0) {
            PollEditTextCell textCell = (PollEditTextCell) cell;
            if (this.answers[index] != null) {
                length = this.answers[index].length();
            } else {
                length = 0;
            }
            left = 100 - length;
            if (((float) left) <= 30.0f) {
                textCell.setText2(String.format("%d", new Object[]{Integer.valueOf(left)}));
                textView = textCell.getTextView2();
                key = left < 0 ? "windowBackgroundWhiteRedText5" : "windowBackgroundWhiteGrayText3";
                textView.setTextColor(Theme.getColor(key));
                textView.setTag(key);
                return;
            }
            textCell.setText2("");
        }
    }

    private void addNewField() {
        this.answersCount++;
        if (this.answersCount == this.answers.length) {
            this.listAdapter.notifyItemRemoved(this.addAnswerRow);
        }
        this.listAdapter.notifyItemInserted(this.addAnswerRow);
        updateRows();
        this.requestFieldFocusAtPosition = (this.answerStartRow + this.answersCount) - 1;
        this.listAdapter.notifyItemChanged(this.answerSectionRow);
    }

    public ThemeDescription[] getThemeDescriptions() {
        r9 = new ThemeDescription[19];
        r9[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextSettingsCell.class, PollEditTextCell.class}, null, null, null, "windowBackgroundWhite");
        r9[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        r9[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r9[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r9[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r9[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r9[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r9[7] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        r9[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, null, null, null, "windowBackgroundWhiteRedText5");
        r9[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, null, null, null, "windowBackgroundWhiteGrayText3");
        r9[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r9[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteHintText");
        r9[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"deleteImageView"}, null, null, null, "windowBackgroundWhiteGrayText");
        r9[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{PollEditTextCell.class}, new String[]{"deleteImageView"}, null, null, null, "stickers_menuSelector");
        r9[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{PollEditTextCell.class}, new String[]{"textView2"}, null, null, null, "windowBackgroundWhiteRedText5");
        r9[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{PollEditTextCell.class}, new String[]{"textView2"}, null, null, null, "windowBackgroundWhiteGrayText3");
        r9[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r9[17] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r9[18] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteHintText");
        return r9;
    }
}
