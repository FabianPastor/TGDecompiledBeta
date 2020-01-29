package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PollCreateActivity;

public class PollCreateActivity extends BaseFragment {
    private static final int MAX_ANSWER_LENGTH = 100;
    private static final int MAX_QUESTION_LENGTH = 255;
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public int addAnswerRow;
    /* access modifiers changed from: private */
    public int answerHeaderRow;
    /* access modifiers changed from: private */
    public int answerSectionRow;
    /* access modifiers changed from: private */
    public int answerStartRow;
    /* access modifiers changed from: private */
    public String[] answers = new String[10];
    /* access modifiers changed from: private */
    public int answersCount = 1;
    /* access modifiers changed from: private */
    public PollCreateActivityDelegate delegate;
    /* access modifiers changed from: private */
    public ActionBarMenuItem doneItem;
    /* access modifiers changed from: private */
    public AnimatorSet doneItemAnimation;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ChatActivity parentFragment;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    /* access modifiers changed from: private */
    public int questionHeaderRow;
    /* access modifiers changed from: private */
    public int questionRow;
    /* access modifiers changed from: private */
    public int questionSectionRow;
    /* access modifiers changed from: private */
    public String questionString;
    /* access modifiers changed from: private */
    public int requestFieldFocusAtPosition = -1;
    /* access modifiers changed from: private */
    public int rowCount;

    public interface PollCreateActivityDelegate {
        void sendPoll(TLRPC.TL_messageMediaPoll tL_messageMediaPoll, boolean z, int i);
    }

    static /* synthetic */ int access$1410(PollCreateActivity pollCreateActivity) {
        int i = pollCreateActivity.answersCount;
        pollCreateActivity.answersCount = i - 1;
        return i;
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public boolean isLongPressDragEnabled() {
            return true;
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }

        public TouchHelperCallback() {
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 5) {
                return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
            }
            return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            if (viewHolder.getItemViewType() != viewHolder2.getItemViewType()) {
                return false;
            }
            PollCreateActivity.this.listAdapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            return true;
        }

        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (i != 0) {
                PollCreateActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    public PollCreateActivity(ChatActivity chatActivity) {
        this.parentFragment = chatActivity;
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
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (PollCreateActivity.this.checkDiscard()) {
                        PollCreateActivity.this.finishFragment();
                    }
                } else if (i == 1) {
                    TLRPC.TL_messageMediaPoll tL_messageMediaPoll = new TLRPC.TL_messageMediaPoll();
                    tL_messageMediaPoll.poll = new TLRPC.TL_poll();
                    TLRPC.TL_poll tL_poll = tL_messageMediaPoll.poll;
                    PollCreateActivity pollCreateActivity = PollCreateActivity.this;
                    tL_poll.question = pollCreateActivity.getFixedString(pollCreateActivity.questionString);
                    for (int i2 = 0; i2 < PollCreateActivity.this.answers.length; i2++) {
                        PollCreateActivity pollCreateActivity2 = PollCreateActivity.this;
                        if (!TextUtils.isEmpty(pollCreateActivity2.getFixedString(pollCreateActivity2.answers[i2]))) {
                            TLRPC.TL_pollAnswer tL_pollAnswer = new TLRPC.TL_pollAnswer();
                            PollCreateActivity pollCreateActivity3 = PollCreateActivity.this;
                            tL_pollAnswer.text = pollCreateActivity3.getFixedString(pollCreateActivity3.answers[i2]);
                            tL_pollAnswer.option = new byte[1];
                            tL_pollAnswer.option[0] = (byte) (tL_messageMediaPoll.poll.answers.size() + 48);
                            tL_messageMediaPoll.poll.answers.add(tL_pollAnswer);
                        }
                    }
                    tL_messageMediaPoll.results = new TLRPC.TL_pollResults();
                    if (PollCreateActivity.this.parentFragment.isInScheduleMode()) {
                        AlertsCreator.createScheduleDatePickerDialog(PollCreateActivity.this.getParentActivity(), PollCreateActivity.this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(tL_messageMediaPoll) {
                            private final /* synthetic */ TLRPC.TL_messageMediaPoll f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void didSelectDate(boolean z, int i) {
                                PollCreateActivity.AnonymousClass1.this.lambda$onItemClick$0$PollCreateActivity$1(this.f$1, z, i);
                            }
                        });
                        return;
                    }
                    PollCreateActivity.this.delegate.sendPoll(tL_messageMediaPoll, true, 0);
                    PollCreateActivity.this.finishFragment();
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$PollCreateActivity$1(TLRPC.TL_messageMediaPoll tL_messageMediaPoll, boolean z, int i) {
                PollCreateActivity.this.delegate.sendPoll(tL_messageMediaPoll, z, i);
                PollCreateActivity.this.finishFragment();
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
        this.listView = new RecyclerListView(context) {
            public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                rect.bottom += AndroidUtilities.dp(60.0f);
                return super.requestChildRectangleOnScreen(view, rect, z);
            }

            /* access modifiers changed from: protected */
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
        ((FrameLayout) this.fragmentView).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                PollCreateActivity.this.lambda$createView$0$PollCreateActivity(view, i);
            }
        });
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
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    PollCreateActivity.this.lambda$onTransitionAnimationEnd$1$PollCreateActivity();
                }
            }, 100);
        }
    }

    public /* synthetic */ void lambda$onTransitionAnimationEnd$1$PollCreateActivity() {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && (findViewHolderForAdapterPosition = recyclerListView.findViewHolderForAdapterPosition(this.questionRow)) != null) {
            EditTextBoldCursor textView = ((PollEditTextCell) findViewHolderForAdapterPosition.itemView).getTextView();
            textView.requestFocus();
            AndroidUtilities.showKeyboard(textView);
        }
    }

    /* access modifiers changed from: private */
    public String getFixedString(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        String charSequence = AndroidUtilities.getTrimmedString(str).toString();
        while (charSequence.contains("\n\n\n")) {
            charSequence = charSequence.replace("\n\n\n", "\n\n");
        }
        while (charSequence.startsWith("\n\n\n")) {
            charSequence = charSequence.replace("\n\n\n", "\n\n");
        }
        return charSequence;
    }

    /* access modifiers changed from: private */
    public void checkDoneButton() {
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

    /* access modifiers changed from: private */
    public void updateRows() {
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.questionHeaderRow = i;
        int i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.questionRow = i2;
        int i3 = this.rowCount;
        this.rowCount = i3 + 1;
        this.questionSectionRow = i3;
        int i4 = this.rowCount;
        this.rowCount = i4 + 1;
        this.answerHeaderRow = i4;
        int i5 = this.answersCount;
        if (i5 != 0) {
            int i6 = this.rowCount;
            this.answerStartRow = i6;
            this.rowCount = i6 + i5;
        } else {
            this.answerStartRow = -1;
        }
        if (this.answersCount != this.answers.length) {
            int i7 = this.rowCount;
            this.rowCount = i7 + 1;
            this.addAnswerRow = i7;
        } else {
            this.addAnswerRow = -1;
        }
        int i8 = this.rowCount;
        this.rowCount = i8 + 1;
        this.answerSectionRow = i8;
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        boolean isEmpty = TextUtils.isEmpty(getFixedString(this.questionString));
        if (isEmpty) {
            int i = 0;
            while (i < this.answersCount && (isEmpty = TextUtils.isEmpty(getFixedString(this.answers[i])))) {
                i++;
            }
        }
        if (!isEmpty) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("CancelPollAlertTitle", NUM));
            builder.setMessage(LocaleController.getString("CancelPollAlertText", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PollCreateActivity.this.lambda$checkDiscard$2$PollCreateActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
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
        if (z) {
            this.progressView.setVisibility(0);
            this.doneItem.setEnabled(false);
            this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{1.0f})});
        } else {
            this.doneItem.getContentView().setVisibility(0);
            this.doneItem.setEnabled(true);
            this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{1.0f})});
        }
        this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (PollCreateActivity.this.doneItemAnimation != null && PollCreateActivity.this.doneItemAnimation.equals(animator)) {
                    if (!z) {
                        PollCreateActivity.this.progressView.setVisibility(4);
                    } else {
                        PollCreateActivity.this.doneItem.getContentView().setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (PollCreateActivity.this.doneItemAnimation != null && PollCreateActivity.this.doneItemAnimation.equals(animator)) {
                    AnimatorSet unused = PollCreateActivity.this.doneItemAnimation = null;
                }
            }
        });
        this.doneItemAnimation.setDuration(150);
        this.doneItemAnimation.start();
    }

    /* access modifiers changed from: private */
    public void setTextLeft(View view, int i) {
        String str = "windowBackgroundWhiteRedText5";
        if (view instanceof HeaderCell) {
            HeaderCell headerCell = (HeaderCell) view;
            if (i == -1) {
                String str2 = this.questionString;
                int length = 255 - (str2 != null ? str2.length() : 0);
                if (((float) length) <= 76.5f) {
                    headerCell.setText2(String.format("%d", new Object[]{Integer.valueOf(length)}));
                    SimpleTextView textView2 = headerCell.getTextView2();
                    if (length >= 0) {
                        str = "windowBackgroundWhiteGrayText3";
                    }
                    textView2.setTextColor(Theme.getColor(str));
                    textView2.setTag(str);
                    return;
                }
                headerCell.setText2("");
                return;
            }
            headerCell.setText2("");
        } else if ((view instanceof PollEditTextCell) && i >= 0) {
            PollEditTextCell pollEditTextCell = (PollEditTextCell) view;
            String[] strArr = this.answers;
            int length2 = 100 - (strArr[i] != null ? strArr[i].length() : 0);
            if (((float) length2) <= 30.0f) {
                pollEditTextCell.setText2(String.format("%d", new Object[]{Integer.valueOf(length2)}));
                SimpleTextView textView22 = pollEditTextCell.getTextView2();
                if (length2 >= 0) {
                    str = "windowBackgroundWhiteGrayText3";
                }
                textView22.setTextColor(Theme.getColor(str));
                textView22.setTag(str);
                return;
            }
            pollEditTextCell.setText2("");
        }
    }

    /* access modifiers changed from: private */
    public void addNewField() {
        this.answersCount++;
        if (this.answersCount == this.answers.length) {
            this.listAdapter.notifyItemRemoved(this.addAnswerRow);
        }
        this.listAdapter.notifyItemInserted(this.addAnswerRow);
        updateRows();
        this.requestFieldFocusAtPosition = (this.answerStartRow + this.answersCount) - 1;
        this.listAdapter.notifyItemChanged(this.answerSectionRow);
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return PollCreateActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
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
                } else {
                    textInfoPrivacyCell.setText(LocaleController.formatString("AddAnOptionInfo", NUM, LocaleController.formatPluralString("Option", 10 - PollCreateActivity.this.answersCount)));
                }
            } else if (itemViewType == 3) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
                textSettingsCell.setText(LocaleController.getString("AddAnOption", NUM), false);
            } else if (itemViewType == 4) {
                PollEditTextCell pollEditTextCell = (PollEditTextCell) viewHolder.itemView;
                pollEditTextCell.setTag(1);
                pollEditTextCell.setTextAndHint(PollCreateActivity.this.questionString != null ? PollCreateActivity.this.questionString : "", LocaleController.getString("QuestionHint", NUM), false);
                pollEditTextCell.setTag((Object) null);
            } else if (itemViewType == 5) {
                PollEditTextCell pollEditTextCell2 = (PollEditTextCell) viewHolder.itemView;
                pollEditTextCell2.setTag(1);
                pollEditTextCell2.setTextAndHint(PollCreateActivity.this.answers[i - PollCreateActivity.this.answerStartRow], LocaleController.getString("OptionHint", NUM), true);
                pollEditTextCell2.setTag((Object) null);
                if (PollCreateActivity.this.requestFieldFocusAtPosition == i) {
                    EditTextBoldCursor textView = pollEditTextCell2.getTextView();
                    textView.requestFocus();
                    AndroidUtilities.showKeyboard(textView);
                    int unused = PollCreateActivity.this.requestFieldFocusAtPosition = -1;
                }
                PollCreateActivity pollCreateActivity = PollCreateActivity.this;
                pollCreateActivity.setTextLeft(viewHolder.itemView, i - pollCreateActivity.answerStartRow);
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0 || itemViewType == 5) {
                PollCreateActivity.this.setTextLeft(viewHolder.itemView, viewHolder.getAdapterPosition() == PollCreateActivity.this.questionHeaderRow ? -1 : 0);
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() == PollCreateActivity.this.addAnswerRow;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v1, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v6, resolved type: org.telegram.ui.Cells.PollEditTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v8, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v9, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v10, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v11, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r8v3, types: [org.telegram.ui.Cells.ShadowSectionCell] */
        /* JADX WARNING: type inference failed for: r8v4, types: [org.telegram.ui.Cells.TextInfoPrivacyCell] */
        /* JADX WARNING: type inference failed for: r8v5, types: [org.telegram.ui.Cells.TextSettingsCell, android.view.View] */
        /* JADX WARNING: type inference failed for: r8v7, types: [org.telegram.ui.Cells.PollEditTextCell, org.telegram.ui.PollCreateActivity$ListAdapter$2, android.widget.FrameLayout] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 2 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r7, int r8) {
            /*
                r6 = this;
                java.lang.String r7 = "windowBackgroundWhite"
                if (r8 == 0) goto L_0x0084
                r0 = 1
                if (r8 == r0) goto L_0x007c
                r1 = 2
                if (r8 == r1) goto L_0x0074
                r1 = 3
                if (r8 == r1) goto L_0x0065
                r1 = 4
                if (r8 == r1) goto L_0x004d
                org.telegram.ui.PollCreateActivity$ListAdapter$2 r8 = new org.telegram.ui.PollCreateActivity$ListAdapter$2
                android.content.Context r1 = r6.mContext
                org.telegram.ui.-$$Lambda$PollCreateActivity$ListAdapter$xPCTWTnLONqvpxDTUQukvNxp8wU r2 = new org.telegram.ui.-$$Lambda$PollCreateActivity$ListAdapter$xPCTWTnLONqvpxDTUQukvNxp8wU
                r2.<init>()
                r8.<init>(r1, r2)
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r8.setBackgroundColor(r7)
                org.telegram.ui.PollCreateActivity$ListAdapter$3 r7 = new org.telegram.ui.PollCreateActivity$ListAdapter$3
                r7.<init>(r8)
                r8.addTextWatcher(r7)
                r8.setShowNextButton(r0)
                org.telegram.ui.Components.EditTextBoldCursor r7 = r8.getTextView()
                int r0 = r7.getImeOptions()
                r0 = r0 | 5
                r7.setImeOptions(r0)
                org.telegram.ui.-$$Lambda$PollCreateActivity$ListAdapter$fHXG5XRT2mioX4wywMDd12jepYQ r0 = new org.telegram.ui.-$$Lambda$PollCreateActivity$ListAdapter$fHXG5XRT2mioX4wywMDd12jepYQ
                r0.<init>(r8)
                r7.setOnEditorActionListener(r0)
                org.telegram.ui.-$$Lambda$PollCreateActivity$ListAdapter$0Cdu-jo6aZB372n9nZKgtvUqB-g r0 = new org.telegram.ui.-$$Lambda$PollCreateActivity$ListAdapter$0Cdu-jo6aZB372n9nZKgtvUqB-g
                r0.<init>()
                r7.setOnKeyListener(r0)
                goto L_0x0099
            L_0x004d:
                org.telegram.ui.Cells.PollEditTextCell r8 = new org.telegram.ui.Cells.PollEditTextCell
                android.content.Context r0 = r6.mContext
                r1 = 0
                r8.<init>(r0, r1)
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r8.setBackgroundColor(r7)
                org.telegram.ui.PollCreateActivity$ListAdapter$1 r7 = new org.telegram.ui.PollCreateActivity$ListAdapter$1
                r7.<init>(r8)
                r8.addTextWatcher(r7)
                goto L_0x0099
            L_0x0065:
                org.telegram.ui.Cells.TextSettingsCell r8 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r0 = r6.mContext
                r8.<init>(r0)
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r8.setBackgroundColor(r7)
                goto L_0x0099
            L_0x0074:
                org.telegram.ui.Cells.TextInfoPrivacyCell r8 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r7 = r6.mContext
                r8.<init>(r7)
                goto L_0x0099
            L_0x007c:
                org.telegram.ui.Cells.ShadowSectionCell r8 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r7 = r6.mContext
                r8.<init>(r7)
                goto L_0x0099
            L_0x0084:
                org.telegram.ui.Cells.HeaderCell r8 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r1 = r6.mContext
                r2 = 0
                r3 = 21
                r4 = 15
                r5 = 1
                r0 = r8
                r0.<init>(r1, r2, r3, r4, r5)
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r8.setBackgroundColor(r7)
            L_0x0099:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r7 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r1 = -2
                r7.<init>((int) r0, (int) r1)
                r8.setLayoutParams(r7)
                org.telegram.ui.Components.RecyclerListView$Holder r7 = new org.telegram.ui.Components.RecyclerListView$Holder
                r7.<init>(r8)
                return r7
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PollCreateActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$PollCreateActivity$ListAdapter(View view) {
            if (view.getTag() == null) {
                view.setTag(1);
                RecyclerView.ViewHolder findContainingViewHolder = PollCreateActivity.this.listView.findContainingViewHolder((View) view.getParent());
                if (findContainingViewHolder != null) {
                    int adapterPosition = findContainingViewHolder.getAdapterPosition();
                    int access$1500 = adapterPosition - PollCreateActivity.this.answerStartRow;
                    PollCreateActivity.this.listAdapter.notifyItemRemoved(findContainingViewHolder.getAdapterPosition());
                    System.arraycopy(PollCreateActivity.this.answers, access$1500 + 1, PollCreateActivity.this.answers, access$1500, (PollCreateActivity.this.answers.length - 1) - access$1500);
                    PollCreateActivity.this.answers[PollCreateActivity.this.answers.length - 1] = null;
                    PollCreateActivity.access$1410(PollCreateActivity.this);
                    if (PollCreateActivity.this.answersCount == PollCreateActivity.this.answers.length - 1) {
                        PollCreateActivity.this.listAdapter.notifyItemInserted((PollCreateActivity.this.answerStartRow + PollCreateActivity.this.answers.length) - 1);
                    }
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(adapterPosition - 1);
                    if (findViewHolderForAdapterPosition != null) {
                        View view2 = findViewHolderForAdapterPosition.itemView;
                        if (view2 instanceof PollEditTextCell) {
                            ((PollEditTextCell) view2).getTextView().requestFocus();
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
            RecyclerView.ViewHolder findContainingViewHolder = PollCreateActivity.this.listView.findContainingViewHolder(pollEditTextCell);
            if (findContainingViewHolder != null) {
                int adapterPosition = findContainingViewHolder.getAdapterPosition();
                int access$1500 = adapterPosition - PollCreateActivity.this.answerStartRow;
                if (access$1500 == PollCreateActivity.this.answersCount - 1 && PollCreateActivity.this.answersCount < 10) {
                    PollCreateActivity.this.addNewField();
                } else if (access$1500 == PollCreateActivity.this.answersCount - 1) {
                    AndroidUtilities.hideKeyboard(pollEditTextCell.getTextView());
                } else {
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(adapterPosition + 1);
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
            int access$1500 = i - PollCreateActivity.this.answerStartRow;
            int access$15002 = i2 - PollCreateActivity.this.answerStartRow;
            if (access$1500 >= 0 && access$15002 >= 0 && access$1500 < PollCreateActivity.this.answersCount && access$15002 < PollCreateActivity.this.answersCount) {
                String str = PollCreateActivity.this.answers[access$1500];
                PollCreateActivity.this.answers[access$1500] = PollCreateActivity.this.answers[access$15002];
                PollCreateActivity.this.answers[access$15002] = str;
                notifyItemMoved(i, i2);
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextSettingsCell.class, PollEditTextCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"deleteImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{PollEditTextCell.class}, new String[]{"deleteImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menuSelector"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{PollEditTextCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{PollEditTextCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText")};
    }
}
