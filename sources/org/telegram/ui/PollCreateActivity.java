package org.telegram.ui;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.SerializedData;
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
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatAttachAlertPollLayout;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class PollCreateActivity extends BaseFragment {
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public int addAnswerRow;
    /* access modifiers changed from: private */
    public boolean anonymousPoll;
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
    public int answersCount;
    /* access modifiers changed from: private */
    public PollCreateActivityDelegate delegate;
    /* access modifiers changed from: private */
    public ActionBarMenuItem doneItem;
    private boolean hintShowed;
    /* access modifiers changed from: private */
    public HintView hintView;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean multipleChoise;
    /* access modifiers changed from: private */
    public int multipleRow;
    /* access modifiers changed from: private */
    public ChatActivity parentFragment;
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
    public int requestFieldFocusAtPosition;
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

    public interface PollCreateActivityDelegate {
        void sendPoll(TLRPC.TL_messageMediaPoll tL_messageMediaPoll, HashMap<String, String> hashMap, boolean z, int i);
    }

    static /* synthetic */ int access$2210(PollCreateActivity x0) {
        int i = x0.answersCount;
        x0.answersCount = i - 1;
        return i;
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
            PollCreateActivity.this.listAdapter.swapElements(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != 0) {
                PollCreateActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    public PollCreateActivity(ChatActivity chatActivity, Boolean quiz) {
        int i = 1;
        this.answersCount = 1;
        this.anonymousPoll = true;
        this.requestFieldFocusAtPosition = -1;
        this.parentFragment = chatActivity;
        if (quiz != null) {
            boolean booleanValue = quiz.booleanValue();
            this.quizPoll = booleanValue;
            this.quizOnly = !booleanValue ? 2 : i;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        if (this.quizOnly == 1) {
            this.actionBar.setTitle(LocaleController.getString("NewQuiz", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NewPoll", NUM));
        }
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (PollCreateActivity.this.checkDiscard()) {
                        PollCreateActivity.this.finishFragment();
                    }
                } else if (id != 1) {
                } else {
                    if (!PollCreateActivity.this.quizPoll || PollCreateActivity.this.doneItem.getAlpha() == 1.0f) {
                        TLRPC.TL_messageMediaPoll poll = new TLRPC.TL_messageMediaPoll();
                        poll.poll = new TLRPC.TL_poll();
                        poll.poll.multiple_choice = PollCreateActivity.this.multipleChoise;
                        poll.poll.quiz = PollCreateActivity.this.quizPoll;
                        poll.poll.public_voters = !PollCreateActivity.this.anonymousPoll;
                        poll.poll.question = ChatAttachAlertPollLayout.getFixedString(PollCreateActivity.this.questionString).toString();
                        SerializedData serializedData = new SerializedData(10);
                        for (int a = 0; a < PollCreateActivity.this.answers.length; a++) {
                            if (!TextUtils.isEmpty(ChatAttachAlertPollLayout.getFixedString(PollCreateActivity.this.answers[a]))) {
                                TLRPC.TL_pollAnswer answer = new TLRPC.TL_pollAnswer();
                                answer.text = ChatAttachAlertPollLayout.getFixedString(PollCreateActivity.this.answers[a]).toString();
                                answer.option = new byte[1];
                                answer.option[0] = (byte) (poll.poll.answers.size() + 48);
                                poll.poll.answers.add(answer);
                                if ((PollCreateActivity.this.multipleChoise || PollCreateActivity.this.quizPoll) && PollCreateActivity.this.answersChecks[a]) {
                                    serializedData.writeByte(answer.option[0]);
                                }
                            }
                        }
                        HashMap<String, String> params = new HashMap<>();
                        params.put("answers", Utilities.bytesToHex(serializedData.toByteArray()));
                        poll.results = new TLRPC.TL_pollResults();
                        CharSequence solution = ChatAttachAlertPollLayout.getFixedString(PollCreateActivity.this.solutionString);
                        if (solution != null) {
                            poll.results.solution = solution.toString();
                            ArrayList<TLRPC.MessageEntity> entities = PollCreateActivity.this.getMediaDataController().getEntities(new CharSequence[]{solution}, true);
                            if (entities != null && !entities.isEmpty()) {
                                poll.results.solution_entities = entities;
                            }
                            if (!TextUtils.isEmpty(poll.results.solution)) {
                                poll.results.flags |= 16;
                            }
                        }
                        if (PollCreateActivity.this.parentFragment.isInScheduleMode()) {
                            AlertsCreator.createScheduleDatePickerDialog(PollCreateActivity.this.getParentActivity(), PollCreateActivity.this.parentFragment.getDialogId(), new PollCreateActivity$1$$ExternalSyntheticLambda0(this, poll, params));
                            return;
                        }
                        PollCreateActivity.this.delegate.sendPoll(poll, params, true, 0);
                        PollCreateActivity.this.finishFragment();
                        return;
                    }
                    int checksCount = 0;
                    for (int a2 = 0; a2 < PollCreateActivity.this.answersChecks.length; a2++) {
                        if (!TextUtils.isEmpty(ChatAttachAlertPollLayout.getFixedString(PollCreateActivity.this.answers[a2])) && PollCreateActivity.this.answersChecks[a2]) {
                            checksCount++;
                        }
                    }
                    if (checksCount <= 0) {
                        PollCreateActivity.this.showQuizHint();
                    }
                }
            }

            /* renamed from: lambda$onItemClick$0$org-telegram-ui-PollCreateActivity$1  reason: not valid java name */
            public /* synthetic */ void m3644lambda$onItemClick$0$orgtelegramuiPollCreateActivity$1(TLRPC.TL_messageMediaPoll poll, HashMap params, boolean notify, int scheduleDate) {
                PollCreateActivity.this.delegate.sendPoll(poll, params, notify, scheduleDate);
                PollCreateActivity.this.finishFragment();
            }
        });
        this.doneItem = this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString("Create", NUM).toUpperCase());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        AnonymousClass2 r4 = new RecyclerListView(context) {
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
        this.listView = r4;
        r4.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.listView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new PollCreateActivity$$ExternalSyntheticLambda1(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy != 0 && PollCreateActivity.this.hintView != null) {
                    PollCreateActivity.this.hintView.hide();
                }
            }
        });
        HintView hintView2 = new HintView(context, 4);
        this.hintView = hintView2;
        hintView2.setText(LocaleController.getString("PollTapToSelect", NUM));
        this.hintView.setAlpha(0.0f);
        this.hintView.setVisibility(4);
        frameLayout.addView(this.hintView, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
        checkDoneButton();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-PollCreateActivity  reason: not valid java name */
    public /* synthetic */ void m3643lambda$createView$0$orgtelegramuiPollCreateActivity(View view, int position) {
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
                    RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.quizRow);
                    if (holder != null) {
                        ((TextCheckCell) holder.itemView).setChecked(false);
                    } else {
                        this.listAdapter.notifyItemChanged(this.quizRow);
                    }
                    this.listAdapter.notifyItemRangeRemoved(prevSolutionRow, 2);
                }
                checked = checked2;
            } else if (this.quizOnly == 0) {
                checked = !this.quizPoll;
                this.quizPoll = checked;
                int prevSolutionRow2 = this.solutionRow;
                updateRows();
                if (this.quizPoll) {
                    this.listAdapter.notifyItemRangeInserted(this.solutionRow, 2);
                } else {
                    this.listAdapter.notifyItemRangeRemoved(prevSolutionRow2, 2);
                }
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

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    /* access modifiers changed from: private */
    public void showQuizHint() {
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
                if (!TextUtils.isEmpty(ChatAttachAlertPollLayout.getFixedString(this.answers[a])) && this.answersChecks[a]) {
                    checksCount++;
                }
            }
        }
        boolean z = true;
        if (!TextUtils.isEmpty(ChatAttachAlertPollLayout.getFixedString(this.solutionString)) && this.solutionString.length() > 200) {
            enabled = false;
        } else if (TextUtils.isEmpty(ChatAttachAlertPollLayout.getFixedString(this.questionString)) || this.questionString.length() > 255) {
            enabled = false;
        } else {
            int count = 0;
            int a2 = 0;
            while (true) {
                String[] strArr = this.answers;
                if (a2 >= strArr.length) {
                    break;
                }
                if (!TextUtils.isEmpty(ChatAttachAlertPollLayout.getFixedString(strArr[a2]))) {
                    if (this.answers[a2].length() > 100) {
                        count = 0;
                        break;
                    }
                    count++;
                }
                a2++;
            }
            if (count < 2 || (this.quizPoll && checksCount < 1)) {
                enabled = false;
            }
        }
        ActionBarMenuItem actionBarMenuItem = this.doneItem;
        if ((!this.quizPoll || checksCount != 0) && !enabled) {
            z = false;
        }
        actionBarMenuItem.setEnabled(z);
        this.doneItem.setAlpha(enabled ? 1.0f : 0.5f);
    }

    /* access modifiers changed from: private */
    public void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.questionHeaderRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.questionRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.questionSectionRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.answerHeaderRow = i3;
        int i5 = this.answersCount;
        if (i5 != 0) {
            this.answerStartRow = i4;
            this.rowCount = i4 + i5;
        } else {
            this.answerStartRow = -1;
        }
        if (i5 != this.answers.length) {
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.addAnswerRow = i6;
        } else {
            this.addAnswerRow = -1;
        }
        int i7 = this.rowCount;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.answerSectionRow = i7;
        this.rowCount = i8 + 1;
        this.settingsHeaderRow = i8;
        TLRPC.Chat chat = this.parentFragment.getCurrentChat();
        if (!ChatObject.isChannel(chat) || chat.megagroup) {
            int i9 = this.rowCount;
            this.rowCount = i9 + 1;
            this.anonymousRow = i9;
        } else {
            this.anonymousRow = -1;
        }
        int i10 = this.quizOnly;
        if (i10 != 1) {
            int i11 = this.rowCount;
            this.rowCount = i11 + 1;
            this.multipleRow = i11;
        } else {
            this.multipleRow = -1;
        }
        if (i10 == 0) {
            int i12 = this.rowCount;
            this.rowCount = i12 + 1;
            this.quizRow = i12;
        } else {
            this.quizRow = -1;
        }
        int i13 = this.rowCount;
        int i14 = i13 + 1;
        this.rowCount = i14;
        this.settingsSectionRow = i13;
        if (this.quizPoll) {
            int i15 = i14 + 1;
            this.rowCount = i15;
            this.solutionRow = i14;
            this.rowCount = i15 + 1;
            this.solutionInfoRow = i15;
            return;
        }
        this.solutionRow = -1;
        this.solutionInfoRow = -1;
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        boolean allowDiscard = TextUtils.isEmpty(ChatAttachAlertPollLayout.getFixedString(this.questionString));
        if (allowDiscard) {
            int a = 0;
            while (a < this.answersCount && (allowDiscard = TextUtils.isEmpty(ChatAttachAlertPollLayout.getFixedString(this.answers[a])))) {
                a++;
            }
        }
        if (!allowDiscard) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("CancelPollAlertTitle", NUM));
            builder.setMessage(LocaleController.getString("CancelPollAlertText", NUM));
            builder.setPositiveButton(LocaleController.getString("PassportDiscard", NUM), new PollCreateActivity$$ExternalSyntheticLambda0(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
        return allowDiscard;
    }

    /* renamed from: lambda$checkDiscard$1$org-telegram-ui-PollCreateActivity  reason: not valid java name */
    public /* synthetic */ void m3642lambda$checkDiscard$1$orgtelegramuiPollCreateActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
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
                textView.setTextColor(Theme.getColor(key));
                textView.setTag(key);
                return;
            }
            textCell.setText2("");
        }
    }

    /* access modifiers changed from: private */
    public void addNewField() {
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
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return PollCreateActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean z = true;
            boolean z2 = false;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell cell = (HeaderCell) holder.itemView;
                    if (position == PollCreateActivity.this.questionHeaderRow) {
                        cell.setText(LocaleController.getString("PollQuestion", NUM));
                        return;
                    } else if (position == PollCreateActivity.this.answerHeaderRow) {
                        if (PollCreateActivity.this.quizOnly == 1) {
                            cell.setText(LocaleController.getString("QuizAnswers", NUM));
                            return;
                        } else {
                            cell.setText(LocaleController.getString("AnswerOptions", NUM));
                            return;
                        }
                    } else if (position == PollCreateActivity.this.settingsHeaderRow) {
                        cell.setText(LocaleController.getString("Settings", NUM));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell cell2 = (TextInfoPrivacyCell) holder.itemView;
                    cell2.setFixedSize(0);
                    cell2.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    if (position == PollCreateActivity.this.solutionInfoRow) {
                        cell2.setText(LocaleController.getString("AddAnExplanationInfo", NUM));
                        return;
                    } else if (position == PollCreateActivity.this.settingsSectionRow) {
                        if (PollCreateActivity.this.quizOnly != 0) {
                            cell2.setFixedSize(12);
                            cell2.setText((CharSequence) null);
                            return;
                        }
                        cell2.setText(LocaleController.getString("QuizInfo", NUM));
                        return;
                    } else if (10 - PollCreateActivity.this.answersCount <= 0) {
                        cell2.setText(LocaleController.getString("AddAnOptionInfoMax", NUM));
                        return;
                    } else {
                        cell2.setText(LocaleController.formatString("AddAnOptionInfo", NUM, LocaleController.formatPluralString("Option", 10 - PollCreateActivity.this.answersCount)));
                        return;
                    }
                case 3:
                    TextCell textCell = (TextCell) holder.itemView;
                    textCell.setColors((String) null, "windowBackgroundWhiteBlueText4");
                    Drawable drawable1 = this.mContext.getResources().getDrawable(NUM);
                    Drawable drawable2 = this.mContext.getResources().getDrawable(NUM);
                    drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor("switchTrackChecked"), PorterDuff.Mode.MULTIPLY));
                    drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("checkboxCheck"), PorterDuff.Mode.MULTIPLY));
                    textCell.setTextAndIcon(LocaleController.getString("AddAnOption", NUM), (Drawable) new CombinedDrawable(drawable1, drawable2), false);
                    return;
                case 6:
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position == PollCreateActivity.this.anonymousRow) {
                        String string = LocaleController.getString("PollAnonymous", NUM);
                        boolean access$900 = PollCreateActivity.this.anonymousPoll;
                        if (!(PollCreateActivity.this.multipleRow == -1 && PollCreateActivity.this.quizRow == -1)) {
                            z2 = true;
                        }
                        checkCell.setTextAndCheck(string, access$900, z2);
                        checkCell.setEnabled(true, (ArrayList<Animator>) null);
                        return;
                    } else if (position == PollCreateActivity.this.multipleRow) {
                        String string2 = LocaleController.getString("PollMultiple", NUM);
                        boolean access$800 = PollCreateActivity.this.multipleChoise;
                        if (PollCreateActivity.this.quizRow != -1) {
                            z2 = true;
                        }
                        checkCell.setTextAndCheck(string2, access$800, z2);
                        checkCell.setEnabled(true, (ArrayList<Animator>) null);
                        return;
                    } else if (position == PollCreateActivity.this.quizRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("PollQuiz", NUM), PollCreateActivity.this.quizPoll, false);
                        if (PollCreateActivity.this.quizOnly != 0) {
                            z = false;
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
                if (PollCreateActivity.this.questionString != null) {
                    charSequence = PollCreateActivity.this.questionString;
                }
                textCell.setTextAndHint(charSequence, LocaleController.getString("QuestionHint", NUM), false);
                textCell.setTag((Object) null);
                PollCreateActivity.this.setTextLeft(holder.itemView, holder.getAdapterPosition());
            } else if (viewType == 5) {
                int position = holder.getAdapterPosition();
                PollEditTextCell textCell2 = (PollEditTextCell) holder.itemView;
                textCell2.setTag(1);
                textCell2.setTextAndHint(PollCreateActivity.this.answers[position - PollCreateActivity.this.answerStartRow], LocaleController.getString("OptionHint", NUM), true);
                textCell2.setTag((Object) null);
                if (PollCreateActivity.this.requestFieldFocusAtPosition == position) {
                    EditTextBoldCursor editText = textCell2.getTextView();
                    editText.requestFocus();
                    AndroidUtilities.showKeyboard(editText);
                    int unused = PollCreateActivity.this.requestFieldFocusAtPosition = -1;
                }
                PollCreateActivity.this.setTextLeft(holder.itemView, position);
            } else if (viewType == 7) {
                PollEditTextCell textCell3 = (PollEditTextCell) holder.itemView;
                textCell3.setTag(1);
                if (PollCreateActivity.this.solutionString != null) {
                    charSequence = PollCreateActivity.this.solutionString;
                }
                textCell3.setTextAndHint(charSequence, LocaleController.getString("AddAnExplanation", NUM), false);
                textCell3.setTag((Object) null);
                PollCreateActivity.this.setTextLeft(holder.itemView, holder.getAdapterPosition());
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
            return position == PollCreateActivity.this.addAnswerRow || position == PollCreateActivity.this.anonymousRow || position == PollCreateActivity.this.multipleRow || (PollCreateActivity.this.quizOnly == 0 && position == PollCreateActivity.this.quizRow);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View headerCell = new HeaderCell(this.mContext, "windowBackgroundWhiteBlueHeader", 21, 15, false);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = headerCell;
                    break;
                case 1:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 3:
                    View view2 = new TextCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view2;
                    break;
                case 4:
                    final PollEditTextCell cell = new PollEditTextCell(this.mContext, (View.OnClickListener) null);
                    cell.createErrorTextView();
                    cell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    cell.addTextWatcher(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        public void afterTextChanged(Editable s) {
                            if (cell.getTag() == null) {
                                String unused = PollCreateActivity.this.questionString = s.toString();
                                RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(PollCreateActivity.this.questionRow);
                                if (holder != null) {
                                    PollCreateActivity.this.setTextLeft(holder.itemView, PollCreateActivity.this.questionRow);
                                }
                                PollCreateActivity.this.checkDoneButton();
                            }
                        }
                    });
                    view = cell;
                    break;
                case 6:
                    View view3 = new TextCheckCell(this.mContext);
                    view3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view3;
                    break;
                case 7:
                    final AnonymousClass2 r0 = new PollEditTextCell(this.mContext, true, (View.OnClickListener) null) {
                        /* access modifiers changed from: protected */
                        public void onActionModeStart(EditTextBoldCursor editText, ActionMode actionMode) {
                            if (editText.isFocused() && editText.hasSelection()) {
                                Menu menu = actionMode.getMenu();
                                if (menu.findItem(16908321) != null) {
                                    PollCreateActivity.this.parentFragment.fillActionModeMenu(menu);
                                }
                            }
                        }
                    };
                    r0.createErrorTextView();
                    r0.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    r0.addTextWatcher(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        public void afterTextChanged(Editable s) {
                            if (r0.getTag() == null) {
                                CharSequence unused = PollCreateActivity.this.solutionString = s;
                                RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(PollCreateActivity.this.solutionRow);
                                if (holder != null) {
                                    PollCreateActivity.this.setTextLeft(holder.itemView, PollCreateActivity.this.solutionRow);
                                }
                                PollCreateActivity.this.checkDoneButton();
                            }
                        }
                    });
                    view = r0;
                    break;
                default:
                    final AnonymousClass4 r1 = new PollEditTextCell(this.mContext, new PollCreateActivity$ListAdapter$$ExternalSyntheticLambda0(this)) {
                        /* access modifiers changed from: protected */
                        public boolean drawDivider() {
                            RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findContainingViewHolder(this);
                            if (holder != null) {
                                int position = holder.getAdapterPosition();
                                if (PollCreateActivity.this.answersCount == 10 && position == (PollCreateActivity.this.answerStartRow + PollCreateActivity.this.answersCount) - 1) {
                                    return false;
                                }
                                return true;
                            }
                            return true;
                        }

                        /* access modifiers changed from: protected */
                        public boolean shouldShowCheckBox() {
                            return PollCreateActivity.this.quizPoll;
                        }

                        /* access modifiers changed from: protected */
                        public void onCheckBoxClick(PollEditTextCell editText, boolean checked) {
                            int position;
                            if (checked && PollCreateActivity.this.quizPoll) {
                                Arrays.fill(PollCreateActivity.this.answersChecks, false);
                                int childCount = PollCreateActivity.this.listView.getChildCount();
                                for (int a = PollCreateActivity.this.answerStartRow; a < PollCreateActivity.this.answerStartRow + PollCreateActivity.this.answersCount; a++) {
                                    RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(a);
                                    if (holder != null && (holder.itemView instanceof PollEditTextCell)) {
                                        ((PollEditTextCell) holder.itemView).setChecked(false, true);
                                    }
                                }
                            }
                            super.onCheckBoxClick(editText, checked);
                            RecyclerView.ViewHolder holder2 = PollCreateActivity.this.listView.findContainingViewHolder(editText);
                            if (!(holder2 == null || (position = holder2.getAdapterPosition()) == -1)) {
                                PollCreateActivity.this.answersChecks[position - PollCreateActivity.this.answerStartRow] = checked;
                            }
                            PollCreateActivity.this.checkDoneButton();
                        }

                        /* access modifiers changed from: protected */
                        public boolean isChecked(PollEditTextCell editText) {
                            int position;
                            RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findContainingViewHolder(editText);
                            if (holder == null || (position = holder.getAdapterPosition()) == -1) {
                                return false;
                            }
                            return PollCreateActivity.this.answersChecks[position - PollCreateActivity.this.answerStartRow];
                        }
                    };
                    r1.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    r1.addTextWatcher(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        public void afterTextChanged(Editable s) {
                            int index;
                            RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findContainingViewHolder(r1);
                            if (holder != null && (index = holder.getAdapterPosition() - PollCreateActivity.this.answerStartRow) >= 0 && index < PollCreateActivity.this.answers.length) {
                                PollCreateActivity.this.answers[index] = s.toString();
                                PollCreateActivity.this.setTextLeft(r1, index);
                                PollCreateActivity.this.checkDoneButton();
                            }
                        }
                    });
                    r1.setShowNextButton(true);
                    EditTextBoldCursor editText = r1.getTextView();
                    editText.setImeOptions(editText.getImeOptions() | 5);
                    editText.setOnEditorActionListener(new PollCreateActivity$ListAdapter$$ExternalSyntheticLambda2(this, r1));
                    editText.setOnKeyListener(new PollCreateActivity$ListAdapter$$ExternalSyntheticLambda1(r1));
                    AnonymousClass4 r2 = r1;
                    view = r1;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-PollCreateActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m3645xvar_c(View v) {
            int position;
            if (v.getTag() == null) {
                v.setTag(1);
                PollEditTextCell p = (PollEditTextCell) v.getParent();
                RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findContainingViewHolder(p);
                if (holder != null && (position = holder.getAdapterPosition()) != -1) {
                    int index = position - PollCreateActivity.this.answerStartRow;
                    PollCreateActivity.this.listAdapter.notifyItemRemoved(position);
                    System.arraycopy(PollCreateActivity.this.answers, index + 1, PollCreateActivity.this.answers, index, (PollCreateActivity.this.answers.length - 1) - index);
                    System.arraycopy(PollCreateActivity.this.answersChecks, index + 1, PollCreateActivity.this.answersChecks, index, (PollCreateActivity.this.answersChecks.length - 1) - index);
                    PollCreateActivity.this.answers[PollCreateActivity.this.answers.length - 1] = null;
                    PollCreateActivity.this.answersChecks[PollCreateActivity.this.answersChecks.length - 1] = false;
                    PollCreateActivity.access$2210(PollCreateActivity.this);
                    if (PollCreateActivity.this.answersCount == PollCreateActivity.this.answers.length - 1) {
                        PollCreateActivity.this.listAdapter.notifyItemInserted((PollCreateActivity.this.answerStartRow + PollCreateActivity.this.answers.length) - 1);
                    }
                    RecyclerView.ViewHolder holder2 = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(position - 1);
                    EditTextBoldCursor editText = p.getTextView();
                    if (holder2 != null && (holder2.itemView instanceof PollEditTextCell)) {
                        ((PollEditTextCell) holder2.itemView).getTextView().requestFocus();
                    } else if (editText.isFocused()) {
                        AndroidUtilities.hideKeyboard(editText);
                    }
                    editText.clearFocus();
                    PollCreateActivity.this.checkDoneButton();
                    PollCreateActivity.this.updateRows();
                    PollCreateActivity.this.listAdapter.notifyItemChanged(PollCreateActivity.this.answerSectionRow);
                }
            }
        }

        /* renamed from: lambda$onCreateViewHolder$1$org-telegram-ui-PollCreateActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ boolean m3646x2beeCLASSNAMEb(PollEditTextCell cell, TextView v, int actionId, KeyEvent event) {
            int position;
            if (actionId != 5) {
                return false;
            }
            RecyclerView.ViewHolder holder = PollCreateActivity.this.listView.findContainingViewHolder(cell);
            if (!(holder == null || (position = holder.getAdapterPosition()) == -1)) {
                int index = position - PollCreateActivity.this.answerStartRow;
                if (index == PollCreateActivity.this.answersCount - 1 && PollCreateActivity.this.answersCount < 10) {
                    PollCreateActivity.this.addNewField();
                } else if (index == PollCreateActivity.this.answersCount - 1) {
                    AndroidUtilities.hideKeyboard(cell.getTextView());
                } else {
                    RecyclerView.ViewHolder holder2 = PollCreateActivity.this.listView.findViewHolderForAdapterPosition(position + 1);
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
            if (position == PollCreateActivity.this.questionHeaderRow || position == PollCreateActivity.this.answerHeaderRow || position == PollCreateActivity.this.settingsHeaderRow) {
                return 0;
            }
            if (position == PollCreateActivity.this.questionSectionRow) {
                return 1;
            }
            if (position == PollCreateActivity.this.answerSectionRow || position == PollCreateActivity.this.settingsSectionRow || position == PollCreateActivity.this.solutionInfoRow) {
                return 2;
            }
            if (position == PollCreateActivity.this.addAnswerRow) {
                return 3;
            }
            if (position == PollCreateActivity.this.questionRow) {
                return 4;
            }
            if (position == PollCreateActivity.this.solutionRow) {
                return 7;
            }
            if (position == PollCreateActivity.this.anonymousRow || position == PollCreateActivity.this.multipleRow || position == PollCreateActivity.this.quizRow) {
                return 6;
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
                boolean temp = PollCreateActivity.this.answersChecks[idx1];
                PollCreateActivity.this.answersChecks[idx1] = PollCreateActivity.this.answersChecks[idx2];
                PollCreateActivity.this.answersChecks[idx2] = temp;
                notifyItemMoved(fromIndex, toIndex);
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, PollEditTextCell.class, TextCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
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
