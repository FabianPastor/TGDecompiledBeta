package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
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

    public class TouchHelperCallback extends Callback {
        public boolean isLongPressDragEnabled() {
            return true;
        }

        public void onSwiped(ViewHolder viewHolder, int i) {
        }

        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 5) {
                return Callback.makeMovementFlags(0, 0);
            }
            return Callback.makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder viewHolder2) {
            if (viewHolder.getItemViewType() != viewHolder2.getItemViewType()) {
                return false;
            }
            PollCreateActivity.this.listAdapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            return true;
        }

        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
        }

        public void onSelectedChanged(ViewHolder viewHolder, int i) {
            if (i != 0) {
                PollCreateActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return PollCreateActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == PollCreateActivity.this.questionHeaderRow) {
                    headerCell.setText(LocaleController.getString("Question", NUM));
                } else if (i == PollCreateActivity.this.answerHeaderRow) {
                    headerCell.setText(LocaleController.getString("PollOptions", NUM));
                }
            } else if (itemViewType == 2) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                if (10 - PollCreateActivity.this.answersCount <= 0) {
                    textInfoPrivacyCell.setText(LocaleController.getString("AddAnOptionInfoMax", NUM));
                    return;
                }
                Object[] objArr = new Object[1];
                objArr[0] = LocaleController.formatPluralString("Option", 10 - PollCreateActivity.this.answersCount);
                textInfoPrivacyCell.setText(LocaleController.formatString("AddAnOptionInfo", NUM, objArr));
            } else if (itemViewType == 3) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
                textSettingsCell.setText(LocaleController.getString("AddAnOption", NUM), false);
            } else if (itemViewType == 4) {
                PollEditTextCell pollEditTextCell = (PollEditTextCell) viewHolder.itemView;
                pollEditTextCell.setTag(Integer.valueOf(1));
                pollEditTextCell.setTextAndHint(PollCreateActivity.this.questionString != null ? PollCreateActivity.this.questionString : "", LocaleController.getString("QuestionHint", NUM), false);
                pollEditTextCell.setTag(null);
            } else if (itemViewType == 5) {
                PollEditTextCell pollEditTextCell2 = (PollEditTextCell) viewHolder.itemView;
                pollEditTextCell2.setTag(Integer.valueOf(1));
                pollEditTextCell2.setTextAndHint(PollCreateActivity.this.answers[i - PollCreateActivity.this.answerStartRow], LocaleController.getString("OptionHint", NUM), true);
                pollEditTextCell2.setTag(null);
                if (PollCreateActivity.this.requestFieldFocusAtPosition == i) {
                    EditTextBoldCursor textView = pollEditTextCell2.getTextView();
                    textView.requestFocus();
                    AndroidUtilities.showKeyboard(textView);
                    PollCreateActivity.this.requestFieldFocusAtPosition = -1;
                }
                PollCreateActivity pollCreateActivity = PollCreateActivity.this;
                pollCreateActivity.setTextLeft(viewHolder.itemView, i - pollCreateActivity.answerStartRow);
            }
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 0) {
                PollCreateActivity.this.setTextLeft(viewHolder.itemView, viewHolder.getAdapterPosition() == PollCreateActivity.this.questionHeaderRow ? -1 : 0);
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() == PollCreateActivity.this.addAnswerRow;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View shadowSectionCell;
            String str = "windowBackgroundWhite";
            if (i == 0) {
                View headerCell = new HeaderCell(this.mContext, false, 21, 15, true);
                headerCell.setBackgroundColor(Theme.getColor(str));
            } else if (i == 1) {
                shadowSectionCell = new ShadowSectionCell(this.mContext);
            } else if (i == 2) {
                shadowSectionCell = new TextInfoPrivacyCell(this.mContext);
            } else if (i == 3) {
                shadowSectionCell = new TextSettingsCell(this.mContext);
                shadowSectionCell.setBackgroundColor(Theme.getColor(str));
            } else if (i != 4) {
                shadowSectionCell = new PollEditTextCell(this.mContext, new -$$Lambda$PollCreateActivity$ListAdapter$xPCTWTnLONqvpxDTUQukvNxp8wU(this)) {
                    /* Access modifiers changed, original: protected */
                    public boolean drawDivider() {
                        ViewHolder findContainingViewHolder = PollCreateActivity.this.listView.findContainingViewHolder(this);
                        if (findContainingViewHolder != null) {
                            int adapterPosition = findContainingViewHolder.getAdapterPosition();
                            if (PollCreateActivity.this.answersCount == 10 && adapterPosition == (PollCreateActivity.this.answerStartRow + PollCreateActivity.this.answersCount) - 1) {
                                return false;
                            }
                        }
                        return true;
                    }
                };
                shadowSectionCell.setBackgroundColor(Theme.getColor(str));
                shadowSectionCell.addTextWatcher(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        ViewHolder findContainingViewHolder = PollCreateActivity.this.listView.findContainingViewHolder(shadowSectionCell);
                        if (findContainingViewHolder != null) {
                            int adapterPosition = findContainingViewHolder.getAdapterPosition() - PollCreateActivity.this.answerStartRow;
                            if (adapterPosition >= 0 && adapterPosition < PollCreateActivity.this.answers.length) {
                                PollCreateActivity.this.answers[adapterPosition] = editable.toString();
                                PollCreateActivity.this.setTextLeft(shadowSectionCell, adapterPosition);
                                PollCreateActivity.this.checkDoneButton();
                            }
                        }
                    }
                });
                shadowSectionCell.setShowNextButton(true);
                EditTextBoldCursor textView = shadowSectionCell.getTextView();
                textView.setImeOptions(textView.getImeOptions() | 5);
                textView.setOnEditorActionListener(new -$$Lambda$PollCreateActivity$ListAdapter$fHXG5XRT2mioX4wywMDd12jepYQ(this, shadowSectionCell));
                textView.setOnKeyListener(new -$$Lambda$PollCreateActivity$ListAdapter$0Cdu-jo6aZB372n9nZKgtvUqB-g(shadowSectionCell));
            } else {
                shadowSectionCell = new PollEditTextCell(this.mContext, null);
                shadowSectionCell.setBackgroundColor(Theme.getColor(str));
                shadowSectionCell.addTextWatcher(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        if (shadowSectionCell.getTag() == null) {
                            PollCreateActivity.this.questionString = editable.toString();
                            ViewHolder findViewHolderForAdapterPosition = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(PollCreateActivity.this.questionHeaderRow);
                            if (findViewHolderForAdapterPosition != null) {
                                PollCreateActivity.this.setTextLeft(findViewHolderForAdapterPosition.itemView, -1);
                            }
                            PollCreateActivity.this.checkDoneButton();
                        }
                    }
                });
            }
            shadowSectionCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(shadowSectionCell);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$PollCreateActivity$ListAdapter(View view) {
            if (view.getTag() == null) {
                view.setTag(Integer.valueOf(1));
                ViewHolder findContainingViewHolder = PollCreateActivity.this.listView.findContainingViewHolder((View) view.getParent());
                if (findContainingViewHolder != null) {
                    int adapterPosition = findContainingViewHolder.getAdapterPosition();
                    int access$1400 = adapterPosition - PollCreateActivity.this.answerStartRow;
                    PollCreateActivity.this.listAdapter.notifyItemRemoved(findContainingViewHolder.getAdapterPosition());
                    System.arraycopy(PollCreateActivity.this.answers, access$1400 + 1, PollCreateActivity.this.answers, access$1400, (PollCreateActivity.this.answers.length - 1) - access$1400);
                    PollCreateActivity.this.answers[PollCreateActivity.this.answers.length - 1] = null;
                    PollCreateActivity.this.answersCount = PollCreateActivity.this.answersCount - 1;
                    if (PollCreateActivity.this.answersCount == PollCreateActivity.this.answers.length - 1) {
                        PollCreateActivity.this.listAdapter.notifyItemInserted((PollCreateActivity.this.answerStartRow + PollCreateActivity.this.answers.length) - 1);
                    }
                    findContainingViewHolder = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(adapterPosition - 1);
                    if (findContainingViewHolder != null) {
                        view = findContainingViewHolder.itemView;
                        if (view instanceof PollEditTextCell) {
                            ((PollEditTextCell) view).getTextView().requestFocus();
                        }
                    }
                    PollCreateActivity.this.checkDoneButton();
                    PollCreateActivity.this.updateRows();
                    PollCreateActivity.this.listAdapter.notifyItemChanged(PollCreateActivity.this.answerSectionRow);
                }
            }
        }

        public /* synthetic */ boolean lambda$onCreateViewHolder$1$PollCreateActivity$ListAdapter(PollEditTextCell pollEditTextCell, TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            ViewHolder findContainingViewHolder = PollCreateActivity.this.listView.findContainingViewHolder(pollEditTextCell);
            if (findContainingViewHolder != null) {
                int adapterPosition = findContainingViewHolder.getAdapterPosition();
                int access$1400 = adapterPosition - PollCreateActivity.this.answerStartRow;
                if (access$1400 == PollCreateActivity.this.answersCount - 1 && PollCreateActivity.this.answersCount < 10) {
                    PollCreateActivity.this.addNewField();
                } else if (access$1400 == PollCreateActivity.this.answersCount - 1) {
                    AndroidUtilities.hideKeyboard(pollEditTextCell.getTextView());
                } else {
                    ViewHolder findViewHolderForAdapterPosition = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(adapterPosition + 1);
                    if (findViewHolderForAdapterPosition != null) {
                        View view = findViewHolderForAdapterPosition.itemView;
                        if (view instanceof PollEditTextCell) {
                            ((PollEditTextCell) view).getTextView().requestFocus();
                        }
                    }
                }
            }
            return true;
        }

        static /* synthetic */ boolean lambda$onCreateViewHolder$2(PollEditTextCell pollEditTextCell, View view, int i, KeyEvent keyEvent) {
            EditTextBoldCursor editTextBoldCursor = (EditTextBoldCursor) view;
            if (i != 67 || keyEvent.getAction() != 0 || editTextBoldCursor.length() != 0) {
                return false;
            }
            pollEditTextCell.callOnDelete();
            return true;
        }

        public int getItemViewType(int i) {
            if (i == PollCreateActivity.this.questionHeaderRow || i == PollCreateActivity.this.answerHeaderRow) {
                return 0;
            }
            if (i == PollCreateActivity.this.questionSectionRow) {
                return 1;
            }
            if (i == PollCreateActivity.this.answerSectionRow) {
                return 2;
            }
            if (i == PollCreateActivity.this.addAnswerRow) {
                return 3;
            }
            return i == PollCreateActivity.this.questionRow ? 4 : 5;
        }

        public void swapElements(int i, int i2) {
            int access$1400 = i - PollCreateActivity.this.answerStartRow;
            int access$14002 = i2 - PollCreateActivity.this.answerStartRow;
            if (access$1400 >= 0 && access$14002 >= 0 && access$1400 < PollCreateActivity.this.answersCount && access$14002 < PollCreateActivity.this.answersCount) {
                String str = PollCreateActivity.this.answers[access$1400];
                PollCreateActivity.this.answers[access$1400] = PollCreateActivity.this.answers[access$14002];
                PollCreateActivity.this.answers[access$14002] = str;
                notifyItemMoved(i, i2);
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setTitle(LocaleController.getString("NewPoll", NUM));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (PollCreateActivity.this.checkDiscard()) {
                        PollCreateActivity.this.finishFragment();
                    }
                } else if (i == 1) {
                    TL_messageMediaPoll tL_messageMediaPoll = new TL_messageMediaPoll();
                    tL_messageMediaPoll.poll = new TL_poll();
                    TL_poll tL_poll = tL_messageMediaPoll.poll;
                    PollCreateActivity pollCreateActivity = PollCreateActivity.this;
                    tL_poll.question = pollCreateActivity.getFixedString(pollCreateActivity.questionString);
                    for (int i2 = 0; i2 < PollCreateActivity.this.answers.length; i2++) {
                        PollCreateActivity pollCreateActivity2 = PollCreateActivity.this;
                        if (!TextUtils.isEmpty(pollCreateActivity2.getFixedString(pollCreateActivity2.answers[i2]))) {
                            TL_pollAnswer tL_pollAnswer = new TL_pollAnswer();
                            PollCreateActivity pollCreateActivity3 = PollCreateActivity.this;
                            tL_pollAnswer.text = pollCreateActivity3.getFixedString(pollCreateActivity3.answers[i2]);
                            tL_pollAnswer.option = new byte[1];
                            tL_pollAnswer.option[0] = (byte) (tL_messageMediaPoll.poll.answers.size() + 48);
                            tL_messageMediaPoll.poll.answers.add(tL_pollAnswer);
                        }
                    }
                    tL_messageMediaPoll.results = new TL_pollResults();
                    PollCreateActivity.this.delegate.sendPoll(tL_messageMediaPoll);
                    PollCreateActivity.this.finishFragment();
                }
            }
        });
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
        this.progressView = new ContextProgressView(context, 1);
        this.progressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.1f);
        this.progressView.setScaleY(0.1f);
        this.progressView.setVisibility(4);
        this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context) {
            public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                rect.bottom += AndroidUtilities.dp(60.0f);
                return super.requestChildRectangleOnScreen(view, rect, z);
            }

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
            }

            public void requestLayout() {
                super.requestLayout();
            }
        };
        this.listView.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.listView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new -$$Lambda$PollCreateActivity$BSXK_C_StVS1Xs7U8esUPs-UHrg(this));
        checkDoneButton();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$PollCreateActivity(View view, int i) {
        if (i == this.addAnswerRow) {
            addNewField();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$PollCreateActivity$RBrZqW0OoyKbyRn7oclxtwGFtGc(this), 100);
        }
    }

    public /* synthetic */ void lambda$onTransitionAnimationEnd$1$PollCreateActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            ViewHolder findViewHolderForAdapterPosition = recyclerListView.findViewHolderForAdapterPosition(this.questionRow);
            if (findViewHolderForAdapterPosition != null) {
                EditTextBoldCursor textView = ((PollEditTextCell) findViewHolderForAdapterPosition.itemView).getTextView();
                textView.requestFocus();
                AndroidUtilities.showKeyboard(textView);
            }
        }
    }

    private String getFixedString(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        String str2;
        String str3;
        str = AndroidUtilities.getTrimmedString(str).toString();
        while (true) {
            str2 = "\n\n\n";
            str3 = "\n\n";
            if (!str.contains(str2)) {
                break;
            }
            str = str.replace(str2, str3);
        }
        while (str.startsWith(str2)) {
            str = str.replace(str2, str3);
        }
        return str;
    }

    private void checkDoneButton() {
        boolean z = false;
        if (!TextUtils.isEmpty(getFixedString(this.questionString)) && this.questionString.length() <= 255) {
            int i = 0;
            int i2 = 0;
            while (true) {
                String[] strArr = this.answers;
                if (i >= strArr.length) {
                    break;
                }
                if (!TextUtils.isEmpty(getFixedString(strArr[i]))) {
                    if (this.answers[i].length() > 100) {
                        i2 = 0;
                        break;
                    }
                    i2++;
                }
                i++;
            }
            if (i2 >= 2) {
                z = true;
            }
        }
        this.doneItem.setEnabled(z);
        this.doneItem.setAlpha(z ? 1.0f : 0.5f);
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
        i = this.answersCount;
        if (i != 0) {
            int i2 = this.rowCount;
            this.answerStartRow = i2;
            this.rowCount = i2 + i;
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
        boolean isEmpty = TextUtils.isEmpty(getFixedString(this.questionString));
        if (isEmpty) {
            for (int i = 0; i < this.answersCount; i++) {
                isEmpty = TextUtils.isEmpty(getFixedString(this.answers[i]));
                if (!isEmpty) {
                    break;
                }
            }
        }
        if (!isEmpty) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("CancelPollAlertTitle", NUM));
            builder.setMessage(LocaleController.getString("CancelPollAlertText", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$PollCreateActivity$-DXfIcr2KYyqZPStWFTU7JfUNVo(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            showDialog(builder.create());
        }
        return isEmpty;
    }

    public /* synthetic */ void lambda$checkDiscard$2$PollCreateActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    public void setDelegate(PollCreateActivityDelegate pollCreateActivityDelegate) {
        this.delegate = pollCreateActivityDelegate;
    }

    private void showEditDoneProgress(final boolean z) {
        AnimatorSet animatorSet = this.doneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.doneItemAnimation = new AnimatorSet();
        AnimatorSet animatorSet2;
        Animator[] animatorArr;
        if (z) {
            this.progressView.setVisibility(0);
            this.doneItem.setEnabled(false);
            animatorSet2 = this.doneItemAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{1.0f});
            animatorSet2.playTogether(animatorArr);
        } else {
            this.doneItem.getContentView().setVisibility(0);
            this.doneItem.setEnabled(true);
            animatorSet2 = this.doneItemAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{1.0f});
            animatorSet2.playTogether(animatorArr);
        }
        this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (PollCreateActivity.this.doneItemAnimation != null && PollCreateActivity.this.doneItemAnimation.equals(animator)) {
                    if (z) {
                        PollCreateActivity.this.doneItem.getContentView().setVisibility(4);
                    } else {
                        PollCreateActivity.this.progressView.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (PollCreateActivity.this.doneItemAnimation != null && PollCreateActivity.this.doneItemAnimation.equals(animator)) {
                    PollCreateActivity.this.doneItemAnimation = null;
                }
            }
        });
        this.doneItemAnimation.setDuration(150);
        this.doneItemAnimation.start();
    }

    private void setTextLeft(View view, int i) {
        String str = "windowBackgroundWhiteRedText5";
        String str2 = "windowBackgroundWhiteGrayText3";
        String str3 = "%d";
        String str4 = "";
        SimpleTextView textView2;
        if (view instanceof HeaderCell) {
            HeaderCell headerCell = (HeaderCell) view;
            if (i == -1) {
                String str5 = this.questionString;
                i = 255 - (str5 != null ? str5.length() : 0);
                if (((float) i) <= 76.5f) {
                    headerCell.setText2(String.format(str3, new Object[]{Integer.valueOf(i)}));
                    textView2 = headerCell.getTextView2();
                    if (i >= 0) {
                        str = str2;
                    }
                    textView2.setTextColor(Theme.getColor(str));
                    textView2.setTag(str);
                    return;
                }
                headerCell.setText2(str4);
                return;
            }
            headerCell.setText2(str4);
        } else if ((view instanceof PollEditTextCell) && i >= 0) {
            PollEditTextCell pollEditTextCell = (PollEditTextCell) view;
            String[] strArr = this.answers;
            i = 100 - (strArr[i] != null ? strArr[i].length() : 0);
            if (((float) i) <= 30.0f) {
                pollEditTextCell.setText2(String.format(str3, new Object[]{Integer.valueOf(i)}));
                textView2 = pollEditTextCell.getTextView2();
                if (i >= 0) {
                    str = str2;
                }
                textView2.setTextColor(Theme.getColor(str));
                textView2.setTag(str);
                return;
            }
            pollEditTextCell.setText2(str4);
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
        r1 = new ThemeDescription[19];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextSettingsCell.class, PollEditTextCell.class}, null, null, null, "windowBackgroundWhite");
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        r1[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r1[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r1[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r1[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        View view = this.listView;
        Class[] clsArr = new Class[]{HeaderCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        r1[7] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueHeader");
        view = this.listView;
        int i = ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{HeaderCell.class};
        strArr = new String[1];
        strArr[0] = "textView2";
        r1[8] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteRedText5");
        r1[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, null, null, null, "windowBackgroundWhiteGrayText3");
        r1[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r1[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteHintText");
        view = this.listView;
        i = ThemeDescription.FLAG_HINTTEXTCOLOR;
        clsArr = new Class[]{PollEditTextCell.class};
        strArr = new String[1];
        strArr[0] = "deleteImageView";
        r1[12] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText");
        r1[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{PollEditTextCell.class}, new String[]{"deleteImageView"}, null, null, null, "stickers_menuSelector");
        r1[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{PollEditTextCell.class}, new String[]{"textView2"}, null, null, null, "windowBackgroundWhiteRedText5");
        r1[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{PollEditTextCell.class}, new String[]{"textView2"}, null, null, null, "windowBackgroundWhiteGrayText3");
        r1[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r1[17] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r1[18] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteHintText");
        return r1;
    }
}
