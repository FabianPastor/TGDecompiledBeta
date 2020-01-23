package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC.Chat;
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
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class PollCreateActivity extends BaseFragment {
    private static final int MAX_ANSWER_LENGTH = 100;
    private static final int MAX_QUESTION_LENGTH = 255;
    private static final int done_button = 1;
    private int addAnswerRow;
    private boolean anonymousPoll;
    private int anonymousRow;
    private int answerHeaderRow;
    private int answerSectionRow;
    private int answerStartRow;
    private String[] answers = new String[10];
    private boolean[] answersChecks = new boolean[10];
    private int answersCount;
    private PollCreateActivityDelegate delegate;
    private ActionBarMenuItem doneItem;
    private boolean hintShowed;
    private HintView hintView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean multipleChoise;
    private int multipleRow;
    private ChatActivity parentFragment;
    private int questionHeaderRow;
    private int questionRow;
    private int questionSectionRow;
    private String questionString;
    private int quizOnly;
    private boolean quizPoll;
    private int quizRow;
    private int requestFieldFocusAtPosition;
    private int rowCount;
    private int settingsHeaderRow;
    private int settingsSectionRow;

    public interface PollCreateActivityDelegate {
        void sendPoll(TL_messageMediaPoll tL_messageMediaPoll, HashMap<String, String> hashMap, boolean z, int i);
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
            if (itemViewType != 0) {
                boolean z = false;
                if (itemViewType == 6) {
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    if (i == PollCreateActivity.this.anonymousRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("PollAnonymous", NUM), PollCreateActivity.this.anonymousPoll, true);
                        textCheckCell.setEnabled(true, null);
                        return;
                    } else if (i == PollCreateActivity.this.multipleRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("PollMultiple", NUM), PollCreateActivity.this.multipleChoise, true);
                        textCheckCell.setEnabled(true, null);
                        return;
                    } else if (i == PollCreateActivity.this.quizRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("PollQuiz", NUM), PollCreateActivity.this.quizPoll, false);
                        if (PollCreateActivity.this.quizOnly == 0) {
                            z = true;
                        }
                        textCheckCell.setEnabled(z, null);
                        return;
                    } else {
                        return;
                    }
                } else if (itemViewType == 2) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    if (i == PollCreateActivity.this.settingsSectionRow) {
                        if (PollCreateActivity.this.quizOnly == 2) {
                            textInfoPrivacyCell.setText(null);
                            return;
                        } else {
                            textInfoPrivacyCell.setText(LocaleController.getString("QuizInfo", NUM));
                            return;
                        }
                    } else if (10 - PollCreateActivity.this.answersCount <= 0) {
                        textInfoPrivacyCell.setText(LocaleController.getString("AddAnOptionInfoMax", NUM));
                        return;
                    } else {
                        Object[] objArr = new Object[1];
                        objArr[0] = LocaleController.formatPluralString("Option", 10 - PollCreateActivity.this.answersCount);
                        textInfoPrivacyCell.setText(LocaleController.formatString("AddAnOptionInfo", NUM, objArr));
                        return;
                    }
                } else if (itemViewType == 3) {
                    TextCell textCell = (TextCell) viewHolder.itemView;
                    textCell.setColors(null, "windowBackgroundWhiteBlueText4");
                    Drawable drawable = this.mContext.getResources().getDrawable(NUM);
                    Drawable drawable2 = this.mContext.getResources().getDrawable(NUM);
                    drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("switchTrackChecked"), Mode.MULTIPLY));
                    drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("checkboxCheck"), Mode.MULTIPLY));
                    textCell.setTextAndIcon(LocaleController.getString("AddAnOption", NUM), new CombinedDrawable(drawable, drawable2), false);
                    return;
                } else {
                    return;
                }
            }
            HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
            if (i == PollCreateActivity.this.questionHeaderRow) {
                headerCell.setText(LocaleController.getString("Question", NUM));
            } else if (i == PollCreateActivity.this.answerHeaderRow) {
                headerCell.setText(LocaleController.getString("PollOptions", NUM));
            } else if (i == PollCreateActivity.this.settingsHeaderRow) {
                headerCell.setText(LocaleController.getString("Settings", NUM));
            }
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0 || itemViewType == 5) {
                PollCreateActivity.this.setTextLeft(viewHolder.itemView, viewHolder.getAdapterPosition() == PollCreateActivity.this.questionHeaderRow ? -1 : 0);
            }
            if (itemViewType == 4) {
                PollEditTextCell pollEditTextCell = (PollEditTextCell) viewHolder.itemView;
                pollEditTextCell.setTag(Integer.valueOf(1));
                pollEditTextCell.setTextAndHint(PollCreateActivity.this.questionString != null ? PollCreateActivity.this.questionString : "", LocaleController.getString("QuestionHint", NUM), false);
                pollEditTextCell.setTag(null);
            } else if (itemViewType == 5) {
                itemViewType = viewHolder.getAdapterPosition();
                PollEditTextCell pollEditTextCell2 = (PollEditTextCell) viewHolder.itemView;
                pollEditTextCell2.setTag(Integer.valueOf(1));
                pollEditTextCell2.setTextAndHint(PollCreateActivity.this.answers[itemViewType - PollCreateActivity.this.answerStartRow], LocaleController.getString("OptionHint", NUM), true);
                pollEditTextCell2.setTag(null);
                if (PollCreateActivity.this.requestFieldFocusAtPosition == itemViewType) {
                    EditTextBoldCursor textView = pollEditTextCell2.getTextView();
                    textView.requestFocus();
                    AndroidUtilities.showKeyboard(textView);
                    PollCreateActivity.this.requestFieldFocusAtPosition = -1;
                }
                PollCreateActivity pollCreateActivity = PollCreateActivity.this;
                pollCreateActivity.setTextLeft(viewHolder.itemView, itemViewType - pollCreateActivity.answerStartRow);
            }
        }

        public void onViewDetachedFromWindow(ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 4) {
                EditTextBoldCursor textView = ((PollEditTextCell) viewHolder.itemView).getTextView();
                if (textView.isFocused()) {
                    textView.clearFocus();
                    AndroidUtilities.hideKeyboard(textView);
                }
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == PollCreateActivity.this.addAnswerRow || adapterPosition == PollCreateActivity.this.anonymousRow || adapterPosition == PollCreateActivity.this.multipleRow || (PollCreateActivity.this.quizOnly == 0 && adapterPosition == PollCreateActivity.this.quizRow);
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
                shadowSectionCell = new TextCell(this.mContext);
                shadowSectionCell.setBackgroundColor(Theme.getColor(str));
            } else if (i == 4) {
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
            } else if (i != 6) {
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

                    /* Access modifiers changed, original: protected */
                    public boolean shouldShowCheckBox() {
                        return PollCreateActivity.this.quizPoll;
                    }

                    /* Access modifiers changed, original: protected */
                    public void onCheckBoxClick(PollEditTextCell pollEditTextCell, boolean z) {
                        if (z && PollCreateActivity.this.quizPoll) {
                            int i;
                            for (i = 0; i < PollCreateActivity.this.answersChecks.length; i++) {
                                PollCreateActivity.this.answersChecks[i] = false;
                            }
                            PollCreateActivity.this.listView.getChildCount();
                            for (i = PollCreateActivity.this.answerStartRow; i < PollCreateActivity.this.answerStartRow + PollCreateActivity.this.answersCount; i++) {
                                ViewHolder findViewHolderForAdapterPosition = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(i);
                                if (findViewHolderForAdapterPosition != null) {
                                    View view = findViewHolderForAdapterPosition.itemView;
                                    if (view instanceof PollEditTextCell) {
                                        ((PollEditTextCell) view).setChecked(false, true);
                                    }
                                }
                            }
                        }
                        super.onCheckBoxClick(pollEditTextCell, z);
                        ViewHolder findContainingViewHolder = PollCreateActivity.this.listView.findContainingViewHolder(pollEditTextCell);
                        if (findContainingViewHolder != null) {
                            PollCreateActivity.this.answersChecks[findContainingViewHolder.getAdapterPosition() - PollCreateActivity.this.answerStartRow] = z;
                        }
                        PollCreateActivity.this.checkDoneButton();
                    }

                    /* Access modifiers changed, original: protected */
                    public boolean isChecked(PollEditTextCell pollEditTextCell) {
                        ViewHolder findContainingViewHolder = PollCreateActivity.this.listView.findContainingViewHolder(pollEditTextCell);
                        if (findContainingViewHolder == null) {
                            return false;
                        }
                        return PollCreateActivity.this.answersChecks[findContainingViewHolder.getAdapterPosition() - PollCreateActivity.this.answerStartRow];
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
                shadowSectionCell = new TextCheckCell(this.mContext);
                shadowSectionCell.setBackgroundColor(Theme.getColor(str));
            }
            shadowSectionCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(shadowSectionCell);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$PollCreateActivity$ListAdapter(View view) {
            if (view.getTag() == null) {
                view.setTag(Integer.valueOf(1));
                PollEditTextCell pollEditTextCell = (PollEditTextCell) view.getParent();
                ViewHolder findContainingViewHolder = PollCreateActivity.this.listView.findContainingViewHolder(pollEditTextCell);
                if (findContainingViewHolder != null) {
                    int adapterPosition = findContainingViewHolder.getAdapterPosition();
                    int access$2500 = adapterPosition - PollCreateActivity.this.answerStartRow;
                    PollCreateActivity.this.listAdapter.notifyItemRemoved(findContainingViewHolder.getAdapterPosition());
                    int i = access$2500 + 1;
                    System.arraycopy(PollCreateActivity.this.answers, i, PollCreateActivity.this.answers, access$2500, (PollCreateActivity.this.answers.length - 1) - access$2500);
                    System.arraycopy(PollCreateActivity.this.answersChecks, i, PollCreateActivity.this.answersChecks, access$2500, (PollCreateActivity.this.answersChecks.length - 1) - access$2500);
                    PollCreateActivity.this.answers[PollCreateActivity.this.answers.length - 1] = null;
                    PollCreateActivity.this.answersChecks[PollCreateActivity.this.answersChecks.length - 1] = false;
                    PollCreateActivity.this.answersCount = PollCreateActivity.this.answersCount - 1;
                    if (PollCreateActivity.this.answersCount == PollCreateActivity.this.answers.length - 1) {
                        PollCreateActivity.this.listAdapter.notifyItemInserted((PollCreateActivity.this.answerStartRow + PollCreateActivity.this.answers.length) - 1);
                    }
                    ViewHolder findViewHolderForAdapterPosition = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(adapterPosition - 1);
                    EditTextBoldCursor textView = pollEditTextCell.getTextView();
                    if (findViewHolderForAdapterPosition != null) {
                        View view2 = findViewHolderForAdapterPosition.itemView;
                        if (view2 instanceof PollEditTextCell) {
                            ((PollEditTextCell) view2).getTextView().requestFocus();
                            textView.clearFocus();
                            PollCreateActivity.this.checkDoneButton();
                            PollCreateActivity.this.updateRows();
                            PollCreateActivity.this.listAdapter.notifyItemChanged(PollCreateActivity.this.answerSectionRow);
                        }
                    }
                    AndroidUtilities.hideKeyboard(textView);
                    textView.clearFocus();
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
                int access$2500 = adapterPosition - PollCreateActivity.this.answerStartRow;
                if (access$2500 == PollCreateActivity.this.answersCount - 1 && PollCreateActivity.this.answersCount < 10) {
                    PollCreateActivity.this.addNewField();
                } else if (access$2500 == PollCreateActivity.this.answersCount - 1) {
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
            if (i == PollCreateActivity.this.questionHeaderRow || i == PollCreateActivity.this.answerHeaderRow || i == PollCreateActivity.this.settingsHeaderRow) {
                return 0;
            }
            if (i == PollCreateActivity.this.questionSectionRow) {
                return 1;
            }
            if (i == PollCreateActivity.this.answerSectionRow || i == PollCreateActivity.this.settingsSectionRow) {
                return 2;
            }
            if (i == PollCreateActivity.this.addAnswerRow) {
                return 3;
            }
            if (i == PollCreateActivity.this.questionRow) {
                return 4;
            }
            return (i == PollCreateActivity.this.anonymousRow || i == PollCreateActivity.this.multipleRow || i == PollCreateActivity.this.quizRow) ? 6 : 5;
        }

        public void swapElements(int i, int i2) {
            int access$2500 = i - PollCreateActivity.this.answerStartRow;
            int access$25002 = i2 - PollCreateActivity.this.answerStartRow;
            if (access$2500 >= 0 && access$25002 >= 0 && access$2500 < PollCreateActivity.this.answersCount && access$25002 < PollCreateActivity.this.answersCount) {
                String str = PollCreateActivity.this.answers[access$2500];
                PollCreateActivity.this.answers[access$2500] = PollCreateActivity.this.answers[access$25002];
                PollCreateActivity.this.answers[access$25002] = str;
                notifyItemMoved(i, i2);
            }
        }
    }

    public PollCreateActivity(ChatActivity chatActivity, Boolean bool) {
        int i = 1;
        this.answersCount = 1;
        this.anonymousPoll = true;
        this.requestFieldFocusAtPosition = -1;
        this.parentFragment = chatActivity;
        if (bool != null) {
            this.quizPoll = bool.booleanValue();
            if (!this.quizPoll) {
                i = 2;
            }
            this.quizOnly = i;
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
                    int i2 = 0;
                    if (!PollCreateActivity.this.quizPoll || PollCreateActivity.this.doneItem.getAlpha() == 1.0f) {
                        TL_messageMediaPoll tL_messageMediaPoll = new TL_messageMediaPoll();
                        tL_messageMediaPoll.poll = new TL_poll();
                        tL_messageMediaPoll.poll.multiple_choice = PollCreateActivity.this.multipleChoise;
                        tL_messageMediaPoll.poll.quiz = PollCreateActivity.this.quizPoll;
                        tL_messageMediaPoll.poll.public_voters = PollCreateActivity.this.anonymousPoll ^ 1;
                        TL_poll tL_poll = tL_messageMediaPoll.poll;
                        PollCreateActivity pollCreateActivity = PollCreateActivity.this;
                        tL_poll.question = pollCreateActivity.getFixedString(pollCreateActivity.questionString);
                        SerializedData serializedData = new SerializedData(10);
                        int i3 = 0;
                        while (i3 < PollCreateActivity.this.answers.length) {
                            PollCreateActivity pollCreateActivity2 = PollCreateActivity.this;
                            if (!TextUtils.isEmpty(pollCreateActivity2.getFixedString(pollCreateActivity2.answers[i3]))) {
                                TL_pollAnswer tL_pollAnswer = new TL_pollAnswer();
                                PollCreateActivity pollCreateActivity3 = PollCreateActivity.this;
                                tL_pollAnswer.text = pollCreateActivity3.getFixedString(pollCreateActivity3.answers[i3]);
                                tL_pollAnswer.option = new byte[1];
                                tL_pollAnswer.option[0] = (byte) (tL_messageMediaPoll.poll.answers.size() + 48);
                                tL_messageMediaPoll.poll.answers.add(tL_pollAnswer);
                                if ((PollCreateActivity.this.multipleChoise || PollCreateActivity.this.quizPoll) && PollCreateActivity.this.answersChecks[i3]) {
                                    serializedData.writeByte(tL_pollAnswer.option[0]);
                                }
                            }
                            i3++;
                        }
                        HashMap hashMap = new HashMap();
                        hashMap.put("answers", Utilities.bytesToHex(serializedData.toByteArray()));
                        tL_messageMediaPoll.results = new TL_pollResults();
                        if (PollCreateActivity.this.parentFragment.isInScheduleMode()) {
                            AlertsCreator.createScheduleDatePickerDialog(PollCreateActivity.this.getParentActivity(), PollCreateActivity.this.parentFragment.getDialogId(), new -$$Lambda$PollCreateActivity$1$7mDxOR-eTq9iu19UCqI6FzmRSHA(this, tL_messageMediaPoll, hashMap));
                        } else {
                            PollCreateActivity.this.delegate.sendPoll(tL_messageMediaPoll, hashMap, true, 0);
                            PollCreateActivity.this.finishFragment();
                        }
                    } else {
                        i = 0;
                        while (i2 < PollCreateActivity.this.answersChecks.length) {
                            PollCreateActivity pollCreateActivity4 = PollCreateActivity.this;
                            if (!TextUtils.isEmpty(pollCreateActivity4.getFixedString(pollCreateActivity4.answers[i2])) && PollCreateActivity.this.answersChecks[i2]) {
                                i++;
                            }
                            i2++;
                        }
                        if (i <= 0) {
                            PollCreateActivity.this.showQuizHint();
                        }
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$PollCreateActivity$1(TL_messageMediaPoll tL_messageMediaPoll, HashMap hashMap, boolean z, int i) {
                PollCreateActivity.this.delegate.sendPoll(tL_messageMediaPoll, hashMap, z, i);
                PollCreateActivity.this.finishFragment();
            }
        });
        this.doneItem = this.actionBar.createMenu().addItem(1, LocaleController.getString("Create", NUM).toUpperCase());
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
        this.hintView = new HintView(context, 4);
        this.hintView.setText(LocaleController.getString("PollTapToSelect", NUM));
        this.hintView.setAlpha(0.0f);
        this.hintView.setVisibility(4);
        frameLayout.addView(this.hintView, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
        checkDoneButton();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$PollCreateActivity(View view, int i) {
        if (i == this.addAnswerRow) {
            addNewField();
        } else if (view instanceof TextCheckCell) {
            boolean z;
            ViewHolder findViewHolderForAdapterPosition;
            TextCheckCell textCheckCell = (TextCheckCell) view;
            boolean z2 = this.quizPoll;
            if (i == this.anonymousRow) {
                z = this.anonymousPoll ^ true;
                this.anonymousPoll = z;
            } else {
                int i2 = this.multipleRow;
                if (i == i2) {
                    z = this.multipleChoise ^ true;
                    this.multipleChoise = z;
                    if (this.multipleChoise && z2) {
                        this.quizPoll = false;
                        findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.quizRow);
                        if (findViewHolderForAdapterPosition != null) {
                            ((TextCheckCell) findViewHolderForAdapterPosition.itemView).setChecked(false);
                        } else {
                            this.listAdapter.notifyItemChanged(this.quizRow);
                        }
                    }
                } else if (this.quizOnly == 0) {
                    boolean z3 = z2 ^ 1;
                    this.quizPoll = z3;
                    if (this.quizPoll && this.multipleChoise) {
                        this.multipleChoise = false;
                        ViewHolder findViewHolderForAdapterPosition2 = this.listView.findViewHolderForAdapterPosition(i2);
                        if (findViewHolderForAdapterPosition2 != null) {
                            ((TextCheckCell) findViewHolderForAdapterPosition2.itemView).setChecked(false);
                        } else {
                            this.listAdapter.notifyItemChanged(this.multipleRow);
                        }
                    }
                    if (this.quizPoll) {
                        i2 = 0;
                        Object obj = null;
                        while (true) {
                            boolean[] zArr = this.answersChecks;
                            if (i2 >= zArr.length) {
                                break;
                            }
                            if (obj != null) {
                                zArr[i2] = false;
                            } else if (zArr[i2]) {
                                obj = 1;
                            }
                            i2++;
                        }
                    }
                    z = z3;
                } else {
                    return;
                }
            }
            if (this.hintShowed && !this.quizPoll) {
                this.hintView.hide();
            }
            this.listView.getChildCount();
            for (int i3 = this.answerStartRow; i3 < this.answerStartRow + this.answersCount; i3++) {
                findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i3);
                if (findViewHolderForAdapterPosition != null) {
                    View view2 = findViewHolderForAdapterPosition.itemView;
                    if (view2 instanceof PollEditTextCell) {
                        PollEditTextCell pollEditTextCell = (PollEditTextCell) view2;
                        pollEditTextCell.setShowCheckBox(this.quizPoll, true);
                        pollEditTextCell.setChecked(this.answersChecks[i3 - this.answerStartRow], z2);
                        if (pollEditTextCell.getTop() > AndroidUtilities.dp(40.0f) && i == this.quizRow && !this.hintShowed) {
                            this.hintView.showForView(pollEditTextCell.getCheckBox(), true);
                            this.hintShowed = true;
                        }
                    }
                }
            }
            textCheckCell.setChecked(z);
            checkDoneButton();
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

    private void showQuizHint() {
        this.listView.getChildCount();
        for (int i = this.answerStartRow; i < this.answerStartRow + this.answersCount; i++) {
            ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
            if (findViewHolderForAdapterPosition != null) {
                View view = findViewHolderForAdapterPosition.itemView;
                if (view instanceof PollEditTextCell) {
                    PollEditTextCell pollEditTextCell = (PollEditTextCell) view;
                    if (pollEditTextCell.getTop() > AndroidUtilities.dp(40.0f)) {
                        this.hintView.showForView(pollEditTextCell.getCheckBox(), true);
                        return;
                    }
                } else {
                    continue;
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:41:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0083  */
    private void checkDoneButton() {
        /*
        r7 = this;
        r0 = r7.quizPoll;
        r1 = 0;
        if (r0 == 0) goto L_0x0025;
    L_0x0005:
        r0 = 0;
        r2 = 0;
    L_0x0007:
        r3 = r7.answersChecks;
        r3 = r3.length;
        if (r0 >= r3) goto L_0x0026;
    L_0x000c:
        r3 = r7.answers;
        r3 = r3[r0];
        r3 = r7.getFixedString(r3);
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x0022;
    L_0x001a:
        r3 = r7.answersChecks;
        r3 = r3[r0];
        if (r3 == 0) goto L_0x0022;
    L_0x0020:
        r2 = r2 + 1;
    L_0x0022:
        r0 = r0 + 1;
        goto L_0x0007;
    L_0x0025:
        r2 = 0;
    L_0x0026:
        r0 = r7.questionString;
        r0 = r7.getFixedString(r0);
        r0 = android.text.TextUtils.isEmpty(r0);
        r3 = 1;
        if (r0 != 0) goto L_0x0070;
    L_0x0033:
        r0 = r7.questionString;
        r0 = r0.length();
        r4 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        if (r0 <= r4) goto L_0x003e;
    L_0x003d:
        goto L_0x0070;
    L_0x003e:
        r0 = 0;
        r4 = 0;
    L_0x0040:
        r5 = r7.answers;
        r6 = r5.length;
        if (r0 >= r6) goto L_0x0064;
    L_0x0045:
        r5 = r5[r0];
        r5 = r7.getFixedString(r5);
        r5 = android.text.TextUtils.isEmpty(r5);
        if (r5 != 0) goto L_0x0061;
    L_0x0051:
        r5 = r7.answers;
        r5 = r5[r0];
        r5 = r5.length();
        r6 = 100;
        if (r5 <= r6) goto L_0x005f;
    L_0x005d:
        r4 = 0;
        goto L_0x0064;
    L_0x005f:
        r4 = r4 + 1;
    L_0x0061:
        r0 = r0 + 1;
        goto L_0x0040;
    L_0x0064:
        r0 = 2;
        if (r4 < r0) goto L_0x0070;
    L_0x0067:
        r0 = r7.quizPoll;
        if (r0 == 0) goto L_0x006e;
    L_0x006b:
        if (r2 >= r3) goto L_0x006e;
    L_0x006d:
        goto L_0x0070;
    L_0x006e:
        r0 = 1;
        goto L_0x0071;
    L_0x0070:
        r0 = 0;
    L_0x0071:
        r4 = r7.doneItem;
        r5 = r7.quizPoll;
        if (r5 == 0) goto L_0x0079;
    L_0x0077:
        if (r2 == 0) goto L_0x007b;
    L_0x0079:
        if (r0 == 0) goto L_0x007c;
    L_0x007b:
        r1 = 1;
    L_0x007c:
        r4.setEnabled(r1);
        r1 = r7.doneItem;
        if (r0 == 0) goto L_0x0086;
    L_0x0083:
        r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0088;
    L_0x0086:
        r0 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
    L_0x0088:
        r1.setAlpha(r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PollCreateActivity.checkDoneButton():void");
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
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsHeaderRow = i;
        Chat currentChat = this.parentFragment.getCurrentChat();
        if (!ChatObject.isChannel(currentChat) || currentChat.megagroup) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.anonymousRow = i;
        } else {
            this.anonymousRow = -1;
        }
        if (this.quizOnly != 1) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.multipleRow = i;
        } else {
            this.multipleRow = -1;
        }
        if (this.quizOnly != 2) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.quizRow = i;
        } else {
            this.quizRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsSectionRow = i;
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
            builder.setPositiveButton(LocaleController.getString("PassportDiscard", NUM), new -$$Lambda$PollCreateActivity$-DXfIcr2KYyqZPStWFTU7JfUNVo(this));
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
        boolean[] zArr = this.answersChecks;
        int i = this.answersCount;
        zArr[i] = false;
        this.answersCount = i + 1;
        if (this.answersCount == this.answers.length) {
            this.listAdapter.notifyItemRemoved(this.addAnswerRow);
        }
        this.listAdapter.notifyItemInserted(this.addAnswerRow);
        updateRows();
        this.requestFieldFocusAtPosition = (this.answerStartRow + this.answersCount) - 1;
        this.listAdapter.notifyItemChanged(this.answerSectionRow);
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[27];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, PollEditTextCell.class, TextCheckCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        View view = this.listView;
        Class[] clsArr = new Class[]{HeaderCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[7] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueHeader");
        view = this.listView;
        int i = ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{HeaderCell.class};
        strArr = new String[1];
        strArr[0] = "textView2";
        themeDescriptionArr[8] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteRedText5");
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, null, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteHintText");
        view = this.listView;
        i = ThemeDescription.FLAG_HINTTEXTCOLOR;
        clsArr = new Class[]{PollEditTextCell.class};
        strArr = new String[1];
        strArr[0] = "deleteImageView";
        themeDescriptionArr[12] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayIcon");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"moveImageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{PollEditTextCell.class}, new String[]{"deleteImageView"}, null, null, null, "stickers_menuSelector");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{PollEditTextCell.class}, new String[]{"textView2"}, null, null, null, "windowBackgroundWhiteRedText5");
        view = this.listView;
        clsArr = new Class[]{PollEditTextCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        themeDescriptionArr[16] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayIcon");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{PollEditTextCell.class}, new String[]{"checkBox"}, null, null, null, "checkboxCheck");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrack");
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        themeDescriptionArr[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[24] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueText4");
        view = this.listView;
        i = ThemeDescription.FLAG_BACKGROUNDFILTER;
        clsArr = new Class[]{TextCell.class};
        strArr = new String[1];
        strArr[0] = "imageView";
        themeDescriptionArr[25] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "switchTrackChecked");
        themeDescriptionArr[26] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "checkboxCheck");
        return themeDescriptionArr;
    }
}
