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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC$TL_poll;
import org.telegram.tgnet.TLRPC$TL_pollAnswer;
import org.telegram.tgnet.TLRPC$TL_pollResults;
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
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.RecyclerListView;

public class ChatAttachAlertPollLayout extends ChatAttachAlert.AttachAlertLayout {
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
        void sendPoll(TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll, HashMap<String, String> hashMap, boolean z, int i);
    }

    /* access modifiers changed from: package-private */
    public int needsActionBar() {
        return 1;
    }

    static /* synthetic */ int access$1210(ChatAttachAlertPollLayout chatAttachAlertPollLayout) {
        int i = chatAttachAlertPollLayout.answersCount;
        chatAttachAlertPollLayout.answersCount = i - 1;
        return i;
    }

    private static class EmptyView extends View {
        public EmptyView(Context context) {
            super(context);
        }
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
            ChatAttachAlertPollLayout.this.listAdapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            return true;
        }

        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (i != 0) {
                ChatAttachAlertPollLayout.this.listView.setItemAnimator(ChatAttachAlertPollLayout.this.itemAnimator);
                ChatAttachAlertPollLayout.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
                viewHolder.itemView.setBackgroundColor(Theme.getColor("dialogBackground"));
            }
            super.onSelectedChanged(viewHolder, i);
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
            viewHolder.itemView.setBackground((Drawable) null);
        }
    }

    public ChatAttachAlertPollLayout(ChatAttachAlert chatAttachAlert, Context context) {
        super(chatAttachAlert, context);
        updateRows();
        this.listAdapter = new ListAdapter(context);
        AnonymousClass1 r1 = new RecyclerListView(this, context) {
            /* access modifiers changed from: protected */
            public void requestChildOnScreen(View view, View view2) {
                if (view instanceof PollEditTextCell) {
                    super.requestChildOnScreen(view, view2);
                }
            }

            public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                rect.bottom += AndroidUtilities.dp(60.0f);
                return super.requestChildRectangleOnScreen(view, rect, z);
            }
        };
        this.listView = r1;
        AnonymousClass2 r2 = new DefaultItemAnimator() {
            /* access modifiers changed from: protected */
            public void onMoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                if (viewHolder.getAdapterPosition() == 0) {
                    ChatAttachAlertPollLayout chatAttachAlertPollLayout = ChatAttachAlertPollLayout.this;
                    chatAttachAlertPollLayout.parentAlert.updateLayout(chatAttachAlertPollLayout, true, 0);
                }
            }
        };
        this.itemAnimator = r2;
        r1.setItemAnimator(r2);
        this.listView.setClipToPadding(false);
        this.listView.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass3 r22 = new FillLastLinearLayoutManager(context, 1, false, AndroidUtilities.dp(53.0f), this.listView) {
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                AnonymousClass1 r2 = new LinearSmoothScroller(recyclerView.getContext()) {
                    public int calculateDyToMakeVisible(View view, int i) {
                        return super.calculateDyToMakeVisible(view, i) - (ChatAttachAlertPollLayout.this.topPadding - AndroidUtilities.dp(7.0f));
                    }

                    /* access modifiers changed from: protected */
                    public int calculateTimeForDeceleration(int i) {
                        return super.calculateTimeForDeceleration(i) * 2;
                    }
                };
                r2.setTargetPosition(i);
                startSmoothScroll(r2);
            }

            /* access modifiers changed from: protected */
            public int[] getChildRectangleOnScreenScrollAmount(View view, Rect rect) {
                int[] iArr = new int[2];
                int height = getHeight() - getPaddingBottom();
                int top = (view.getTop() + rect.top) - view.getScrollY();
                int height2 = rect.height() + top;
                int i = top - 0;
                int min = Math.min(0, i);
                int max = Math.max(0, height2 - height);
                if (min == 0) {
                    min = Math.min(i, max);
                }
                iArr[0] = 0;
                iArr[1] = min;
                return iArr;
            }
        };
        this.layoutManager = r22;
        recyclerListView.setLayoutManager(r22);
        this.layoutManager.setSkipFirstItem();
        new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.listView);
        addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setPreserveFocusAfterLayout(true);
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                ChatAttachAlertPollLayout.this.lambda$new$0$ChatAttachAlertPollLayout(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ChatAttachAlertPollLayout chatAttachAlertPollLayout = ChatAttachAlertPollLayout.this;
                chatAttachAlertPollLayout.parentAlert.updateLayout(chatAttachAlertPollLayout, true, i2);
                if (i2 != 0 && ChatAttachAlertPollLayout.this.hintView != null) {
                    ChatAttachAlertPollLayout.this.hintView.hide();
                }
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                RecyclerListView.Holder holder;
                if (i == 0) {
                    int dp = AndroidUtilities.dp(13.0f);
                    int backgroundPaddingTop = ChatAttachAlertPollLayout.this.parentAlert.getBackgroundPaddingTop();
                    if (((ChatAttachAlertPollLayout.this.parentAlert.scrollOffsetY[0] - backgroundPaddingTop) - dp) + backgroundPaddingTop < ActionBar.getCurrentActionBarHeight() && (holder = (RecyclerListView.Holder) ChatAttachAlertPollLayout.this.listView.findViewHolderForAdapterPosition(1)) != null && holder.itemView.getTop() > AndroidUtilities.dp(53.0f)) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ChatAttachAlertPollLayout(View view, int i) {
        boolean z;
        if (i == this.addAnswerRow) {
            addNewField();
        } else if (view instanceof TextCheckCell) {
            TextCheckCell textCheckCell = (TextCheckCell) view;
            boolean z2 = this.quizPoll;
            if (i == this.anonymousRow) {
                z = !this.anonymousPoll;
                this.anonymousPoll = z;
            } else if (i == this.multipleRow) {
                z = !this.multipleChoise;
                this.multipleChoise = z;
                if (z && z2) {
                    int i2 = this.solutionRow;
                    this.quizPoll = false;
                    updateRows();
                    this.listView.setItemAnimator(this.itemAnimator);
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.quizRow);
                    if (findViewHolderForAdapterPosition != null) {
                        ((TextCheckCell) findViewHolderForAdapterPosition.itemView).setChecked(false);
                    } else {
                        this.listAdapter.notifyItemChanged(this.quizRow);
                    }
                    this.listAdapter.notifyItemRangeRemoved(i2, 2);
                    this.listAdapter.notifyItemChanged(this.emptyRow);
                }
            } else if (this.quizOnly == 0) {
                this.listView.setItemAnimator(this.itemAnimator);
                z = !this.quizPoll;
                this.quizPoll = z;
                int i3 = this.solutionRow;
                updateRows();
                if (this.quizPoll) {
                    this.listAdapter.notifyItemRangeInserted(this.solutionRow, 2);
                } else {
                    this.listAdapter.notifyItemRangeRemoved(i3, 2);
                }
                this.listAdapter.notifyItemChanged(this.emptyRow);
                if (this.quizPoll && this.multipleChoise) {
                    this.multipleChoise = false;
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = this.listView.findViewHolderForAdapterPosition(this.multipleRow);
                    if (findViewHolderForAdapterPosition2 != null) {
                        ((TextCheckCell) findViewHolderForAdapterPosition2.itemView).setChecked(false);
                    } else {
                        this.listAdapter.notifyItemChanged(this.multipleRow);
                    }
                }
                if (this.quizPoll) {
                    int i4 = 0;
                    boolean z3 = false;
                    while (true) {
                        boolean[] zArr = this.answersChecks;
                        if (i4 >= zArr.length) {
                            break;
                        }
                        if (z3) {
                            zArr[i4] = false;
                        } else if (zArr[i4]) {
                            z3 = true;
                        }
                        i4++;
                    }
                }
            } else {
                return;
            }
            if (this.hintShowed && !this.quizPoll) {
                this.hintView.hide();
            }
            this.listView.getChildCount();
            for (int i5 = this.answerStartRow; i5 < this.answerStartRow + this.answersCount; i5++) {
                RecyclerView.ViewHolder findViewHolderForAdapterPosition3 = this.listView.findViewHolderForAdapterPosition(i5);
                if (findViewHolderForAdapterPosition3 != null) {
                    View view2 = findViewHolderForAdapterPosition3.itemView;
                    if (view2 instanceof PollEditTextCell) {
                        PollEditTextCell pollEditTextCell = (PollEditTextCell) view2;
                        pollEditTextCell.setShowCheckBox(this.quizPoll, true);
                        pollEditTextCell.setChecked(this.answersChecks[i5 - this.answerStartRow], z2);
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

    /* access modifiers changed from: package-private */
    public void onResume() {
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public void onHideShowProgress(float f) {
        ActionBarMenuItem actionBarMenuItem = this.parentAlert.doneItem;
        actionBarMenuItem.setAlpha((actionBarMenuItem.isEnabled() ? 1.0f : 0.5f) * f);
    }

    /* access modifiers changed from: package-private */
    public void onMenuItemClick(int i) {
        if (i == 40) {
            if (!this.quizPoll || this.parentAlert.doneItem.getAlpha() == 1.0f) {
                TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll = new TLRPC$TL_messageMediaPoll();
                TLRPC$TL_poll tLRPC$TL_poll = new TLRPC$TL_poll();
                tLRPC$TL_messageMediaPoll.poll = tLRPC$TL_poll;
                tLRPC$TL_poll.multiple_choice = this.multipleChoise;
                tLRPC$TL_poll.quiz = this.quizPoll;
                tLRPC$TL_poll.public_voters = !this.anonymousPoll;
                tLRPC$TL_poll.question = getFixedString(this.questionString).toString();
                SerializedData serializedData = new SerializedData(10);
                int i2 = 0;
                while (true) {
                    String[] strArr = this.answers;
                    if (i2 >= strArr.length) {
                        break;
                    }
                    if (!TextUtils.isEmpty(getFixedString(strArr[i2]))) {
                        TLRPC$TL_pollAnswer tLRPC$TL_pollAnswer = new TLRPC$TL_pollAnswer();
                        tLRPC$TL_pollAnswer.text = getFixedString(this.answers[i2]).toString();
                        byte[] bArr = new byte[1];
                        tLRPC$TL_pollAnswer.option = bArr;
                        bArr[0] = (byte) (tLRPC$TL_messageMediaPoll.poll.answers.size() + 48);
                        tLRPC$TL_messageMediaPoll.poll.answers.add(tLRPC$TL_pollAnswer);
                        if ((this.multipleChoise || this.quizPoll) && this.answersChecks[i2]) {
                            serializedData.writeByte(tLRPC$TL_pollAnswer.option[0]);
                        }
                    }
                    i2++;
                }
                HashMap hashMap = new HashMap();
                hashMap.put("answers", Utilities.bytesToHex(serializedData.toByteArray()));
                tLRPC$TL_messageMediaPoll.results = new TLRPC$TL_pollResults();
                CharSequence fixedString = getFixedString(this.solutionString);
                if (fixedString != null) {
                    tLRPC$TL_messageMediaPoll.results.solution = fixedString.toString();
                    ArrayList<TLRPC$MessageEntity> entities = MediaDataController.getInstance(this.parentAlert.currentAccount).getEntities(new CharSequence[]{fixedString}, true);
                    if (entities != null && !entities.isEmpty()) {
                        tLRPC$TL_messageMediaPoll.results.solution_entities = entities;
                    }
                    if (!TextUtils.isEmpty(tLRPC$TL_messageMediaPoll.results.solution)) {
                        tLRPC$TL_messageMediaPoll.results.flags |= 16;
                    }
                }
                ChatActivity chatActivity = (ChatActivity) this.parentAlert.baseFragment;
                if (chatActivity.isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog(chatActivity.getParentActivity(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(tLRPC$TL_messageMediaPoll, hashMap) {
                        public final /* synthetic */ TLRPC$TL_messageMediaPoll f$1;
                        public final /* synthetic */ HashMap f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void didSelectDate(boolean z, int i) {
                            ChatAttachAlertPollLayout.this.lambda$onMenuItemClick$1$ChatAttachAlertPollLayout(this.f$1, this.f$2, z, i);
                        }
                    });
                    return;
                }
                this.delegate.sendPoll(tLRPC$TL_messageMediaPoll, hashMap, true, 0);
                this.parentAlert.dismiss();
                return;
            }
            int i3 = 0;
            for (int i4 = 0; i4 < this.answersChecks.length; i4++) {
                if (!TextUtils.isEmpty(getFixedString(this.answers[i4])) && this.answersChecks[i4]) {
                    i3++;
                }
            }
            if (i3 <= 0) {
                showQuizHint();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onMenuItemClick$1 */
    public /* synthetic */ void lambda$onMenuItemClick$1$ChatAttachAlertPollLayout(TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll, HashMap hashMap, boolean z, int i) {
        this.delegate.sendPoll(tLRPC$TL_messageMediaPoll, hashMap, z, i);
        this.parentAlert.dismiss();
    }

    /* access modifiers changed from: package-private */
    public int getCurrentItemTop() {
        View childAt;
        if (this.listView.getChildCount() <= 0 || (childAt = this.listView.getChildAt(1)) == null) {
            return Integer.MAX_VALUE;
        }
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int y = ((int) childAt.getY()) - AndroidUtilities.dp(8.0f);
        int i = (y <= 0 || holder == null || holder.getAdapterPosition() != 1) ? 0 : y;
        if (y < 0 || holder == null || holder.getAdapterPosition() != 0) {
            y = i;
        }
        return y + AndroidUtilities.dp(25.0f);
    }

    /* access modifiers changed from: package-private */
    public int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(17.0f);
    }

    public void setTranslationY(float f) {
        super.setTranslationY(f);
        this.parentAlert.getSheetContainer().invalidate();
    }

    /* access modifiers changed from: package-private */
    public int getListTopPadding() {
        return this.topPadding;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x003e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onPreMeasure(int r3, int r4) {
        /*
            r2 = this;
            org.telegram.ui.Components.ChatAttachAlert r3 = r2.parentAlert
            org.telegram.ui.Components.SizeNotifierFrameLayout r3 = r3.sizeNotifierFrameLayout
            int r3 = r3.measureKeyboardHeight()
            r0 = 1101004800(0x41a00000, float:20.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1 = 0
            if (r3 <= r0) goto L_0x001d
            r3 = 1112539136(0x42500000, float:52.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            org.telegram.ui.Components.ChatAttachAlert r4 = r2.parentAlert
            r4.setAllowNestedScroll(r1)
            goto L_0x0046
        L_0x001d:
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r3 != 0) goto L_0x0031
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r3.x
            int r3 = r3.y
            if (r0 <= r3) goto L_0x0031
            float r3 = (float) r4
            r4 = 1080033280(0x40600000, float:3.5)
            float r3 = r3 / r4
            int r3 = (int) r3
            goto L_0x0035
        L_0x0031:
            int r4 = r4 / 5
            int r3 = r4 * 2
        L_0x0035:
            r4 = 1095761920(0x41500000, float:13.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
            if (r3 >= 0) goto L_0x003f
            r3 = 0
        L_0x003f:
            org.telegram.ui.Components.ChatAttachAlert r4 = r2.parentAlert
            boolean r0 = r2.allowNesterScroll
            r4.setAllowNestedScroll(r0)
        L_0x0046:
            r4 = 1
            r2.ignoreLayout = r4
            int r4 = r2.topPadding
            if (r4 == r3) goto L_0x005c
            r2.topPadding = r3
            org.telegram.ui.Components.RecyclerListView r3 = r2.listView
            r4 = 0
            r3.setItemAnimator(r4)
            org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter r3 = r2.listAdapter
            int r4 = r2.paddingRow
            r3.notifyItemChanged(r4)
        L_0x005c:
            r2.ignoreLayout = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPollLayout.onPreMeasure(int, int):void");
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

    public static CharSequence getFixedString(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return charSequence;
        }
        CharSequence trimmedString = AndroidUtilities.getTrimmedString(charSequence);
        while (TextUtils.indexOf(trimmedString, "\n\n\n") >= 0) {
            trimmedString = TextUtils.replace(trimmedString, new String[]{"\n\n\n"}, new CharSequence[]{"\n\n"});
        }
        while (TextUtils.indexOf(trimmedString, "\n\n\n") == 0) {
            trimmedString = TextUtils.replace(trimmedString, new String[]{"\n\n\n"}, new CharSequence[]{"\n\n"});
        }
        return trimmedString;
    }

    private void showQuizHint() {
        this.listView.getChildCount();
        for (int i = this.answerStartRow; i < this.answerStartRow + this.answersCount; i++) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
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

    /* access modifiers changed from: private */
    public void checkDoneButton() {
        int i;
        boolean z = false;
        if (this.quizPoll) {
            i = 0;
            for (int i2 = 0; i2 < this.answersChecks.length; i2++) {
                if (!TextUtils.isEmpty(getFixedString(this.answers[i2])) && this.answersChecks[i2]) {
                    i++;
                }
            }
        } else {
            i = 0;
        }
        boolean z2 = (TextUtils.isEmpty(getFixedString(this.solutionString)) || this.solutionString.length() <= 200) && !TextUtils.isEmpty(getFixedString(this.questionString)) && this.questionString.length() <= 255;
        int i3 = 0;
        int i4 = 0;
        boolean z3 = false;
        while (true) {
            String[] strArr = this.answers;
            if (i3 >= strArr.length) {
                break;
            }
            if (!TextUtils.isEmpty(getFixedString(strArr[i3]))) {
                if (this.answers[i3].length() > 100) {
                    i4 = 0;
                    z3 = true;
                    break;
                }
                i4++;
                z3 = true;
            }
            i3++;
        }
        if (i4 < 2 || (this.quizPoll && i < 1)) {
            z2 = false;
        }
        if (!TextUtils.isEmpty(this.solutionString) || !TextUtils.isEmpty(this.questionString) || z3) {
            this.allowNesterScroll = false;
        } else {
            this.allowNesterScroll = true;
        }
        this.parentAlert.setAllowNestedScroll(this.allowNesterScroll);
        ActionBarMenuItem actionBarMenuItem = this.parentAlert.doneItem;
        if ((this.quizPoll && i == 0) || z2) {
            z = true;
        }
        actionBarMenuItem.setEnabled(z);
        this.parentAlert.doneItem.setAlpha(z2 ? 1.0f : 0.5f);
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
        TLRPC$Chat currentChat = ((ChatActivity) this.parentAlert.baseFragment).getCurrentChat();
        if (!ChatObject.isChannel(currentChat) || currentChat.megagroup) {
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
        boolean isEmpty = TextUtils.isEmpty(getFixedString(this.questionString));
        if (isEmpty) {
            int i = 0;
            while (i < this.answersCount && (isEmpty = TextUtils.isEmpty(getFixedString(this.answers[i])))) {
                i++;
            }
        }
        if (!isEmpty) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentAlert.baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("CancelPollAlertTitle", NUM));
            builder.setMessage(LocaleController.getString("CancelPollAlertText", NUM));
            builder.setPositiveButton(LocaleController.getString("PassportDiscard", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatAttachAlertPollLayout.this.lambda$checkDiscard$2$ChatAttachAlertPollLayout(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.show();
        }
        return isEmpty;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkDiscard$2 */
    public /* synthetic */ void lambda$checkDiscard$2$ChatAttachAlertPollLayout(DialogInterface dialogInterface, int i) {
        this.parentAlert.dismiss();
    }

    public void setDelegate(PollCreateActivityDelegate pollCreateActivityDelegate) {
        this.delegate = pollCreateActivityDelegate;
    }

    /* access modifiers changed from: private */
    public void setTextLeft(View view, int i) {
        int i2;
        if (view instanceof PollEditTextCell) {
            PollEditTextCell pollEditTextCell = (PollEditTextCell) view;
            int i3 = 100;
            if (i == this.questionRow) {
                String str = this.questionString;
                i2 = 255 - (str != null ? str.length() : 0);
                i3 = 255;
            } else if (i == this.solutionRow) {
                CharSequence charSequence = this.solutionString;
                i2 = 200 - (charSequence != null ? charSequence.length() : 0);
                i3 = 200;
            } else {
                int i4 = this.answerStartRow;
                if (i >= i4 && i < this.answersCount + i4) {
                    int i5 = i - i4;
                    String[] strArr = this.answers;
                    i2 = 100 - (strArr[i5] != null ? strArr[i5].length() : 0);
                } else {
                    return;
                }
            }
            float f = (float) i3;
            if (((float) i2) <= f - (0.7f * f)) {
                pollEditTextCell.setText2(String.format("%d", new Object[]{Integer.valueOf(i2)}));
                SimpleTextView textView2 = pollEditTextCell.getTextView2();
                String str2 = i2 < 0 ? "windowBackgroundWhiteRedText5" : "windowBackgroundWhiteGrayText3";
                textView2.setTextColor(Theme.getColor(str2));
                textView2.setTag(str2);
                return;
            }
            pollEditTextCell.setText2("");
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

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            int i2 = 3;
            boolean z = true;
            if (itemViewType != 0) {
                boolean z2 = false;
                if (itemViewType == 6) {
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    if (i == ChatAttachAlertPollLayout.this.anonymousRow) {
                        String string = LocaleController.getString("PollAnonymous", NUM);
                        boolean access$1400 = ChatAttachAlertPollLayout.this.anonymousPoll;
                        if (!(ChatAttachAlertPollLayout.this.multipleRow == -1 && ChatAttachAlertPollLayout.this.quizRow == -1)) {
                            z2 = true;
                        }
                        textCheckCell.setTextAndCheck(string, access$1400, z2);
                        textCheckCell.setEnabled(true, (ArrayList<Animator>) null);
                    } else if (i == ChatAttachAlertPollLayout.this.multipleRow) {
                        String string2 = LocaleController.getString("PollMultiple", NUM);
                        boolean access$1700 = ChatAttachAlertPollLayout.this.multipleChoise;
                        if (ChatAttachAlertPollLayout.this.quizRow != -1) {
                            z2 = true;
                        }
                        textCheckCell.setTextAndCheck(string2, access$1700, z2);
                        textCheckCell.setEnabled(true, (ArrayList<Animator>) null);
                    } else if (i == ChatAttachAlertPollLayout.this.quizRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("PollQuiz", NUM), ChatAttachAlertPollLayout.this.quizPoll, false);
                        if (ChatAttachAlertPollLayout.this.quizOnly != 0) {
                            z = false;
                        }
                        textCheckCell.setEnabled(z, (ArrayList<Animator>) null);
                    }
                } else if (itemViewType == 2) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    combinedDrawable.setFullsize(true);
                    textInfoPrivacyCell.setBackgroundDrawable(combinedDrawable);
                    if (i == ChatAttachAlertPollLayout.this.solutionInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("AddAnExplanationInfo", NUM));
                    } else if (i == ChatAttachAlertPollLayout.this.settingsSectionRow) {
                        if (ChatAttachAlertPollLayout.this.quizOnly != 0) {
                            textInfoPrivacyCell.setText((CharSequence) null);
                        } else {
                            textInfoPrivacyCell.setText(LocaleController.getString("QuizInfo", NUM));
                        }
                    } else if (10 - ChatAttachAlertPollLayout.this.answersCount <= 0) {
                        textInfoPrivacyCell.setText(LocaleController.getString("AddAnOptionInfoMax", NUM));
                    } else {
                        textInfoPrivacyCell.setText(LocaleController.formatString("AddAnOptionInfo", NUM, LocaleController.formatPluralString("Option", 10 - ChatAttachAlertPollLayout.this.answersCount)));
                    }
                } else if (itemViewType == 3) {
                    TextCell textCell = (TextCell) viewHolder.itemView;
                    textCell.setColors((String) null, "windowBackgroundWhiteBlueText4");
                    Drawable drawable = this.mContext.getResources().getDrawable(NUM);
                    Drawable drawable2 = this.mContext.getResources().getDrawable(NUM);
                    drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("switchTrackChecked"), PorterDuff.Mode.MULTIPLY));
                    drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("checkboxCheck"), PorterDuff.Mode.MULTIPLY));
                    textCell.setTextAndIcon(LocaleController.getString("AddAnOption", NUM), (Drawable) new CombinedDrawable(drawable, drawable2), false);
                }
            } else {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == ChatAttachAlertPollLayout.this.questionHeaderRow) {
                    headerCell.getTextView().setGravity(19);
                    headerCell.setText(LocaleController.getString("PollQuestion", NUM));
                    return;
                }
                TextView textView = headerCell.getTextView();
                if (LocaleController.isRTL) {
                    i2 = 5;
                }
                textView.setGravity(i2 | 16);
                if (i == ChatAttachAlertPollLayout.this.answerHeaderRow) {
                    if (ChatAttachAlertPollLayout.this.quizOnly == 1) {
                        headerCell.setText(LocaleController.getString("QuizAnswers", NUM));
                    } else {
                        headerCell.setText(LocaleController.getString("AnswerOptions", NUM));
                    }
                } else if (i == ChatAttachAlertPollLayout.this.settingsHeaderRow) {
                    headerCell.setText(LocaleController.getString("Settings", NUM));
                }
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            CharSequence charSequence = "";
            if (itemViewType == 4) {
                PollEditTextCell pollEditTextCell = (PollEditTextCell) viewHolder.itemView;
                pollEditTextCell.setTag(1);
                if (ChatAttachAlertPollLayout.this.questionString != null) {
                    charSequence = ChatAttachAlertPollLayout.this.questionString;
                }
                pollEditTextCell.setTextAndHint(charSequence, LocaleController.getString("QuestionHint", NUM), false);
                pollEditTextCell.setTag((Object) null);
                ChatAttachAlertPollLayout.this.setTextLeft(viewHolder.itemView, viewHolder.getAdapterPosition());
            } else if (itemViewType == 5) {
                int adapterPosition = viewHolder.getAdapterPosition();
                PollEditTextCell pollEditTextCell2 = (PollEditTextCell) viewHolder.itemView;
                pollEditTextCell2.setTag(1);
                pollEditTextCell2.setTextAndHint(ChatAttachAlertPollLayout.this.answers[adapterPosition - ChatAttachAlertPollLayout.this.answerStartRow], LocaleController.getString("OptionHint", NUM), true);
                pollEditTextCell2.setTag((Object) null);
                if (ChatAttachAlertPollLayout.this.requestFieldFocusAtPosition == adapterPosition) {
                    EditTextBoldCursor textView = pollEditTextCell2.getTextView();
                    textView.requestFocus();
                    AndroidUtilities.showKeyboard(textView);
                    int unused = ChatAttachAlertPollLayout.this.requestFieldFocusAtPosition = -1;
                }
                ChatAttachAlertPollLayout.this.setTextLeft(viewHolder.itemView, adapterPosition);
            } else if (itemViewType == 7) {
                PollEditTextCell pollEditTextCell3 = (PollEditTextCell) viewHolder.itemView;
                pollEditTextCell3.setTag(1);
                if (ChatAttachAlertPollLayout.this.solutionString != null) {
                    charSequence = ChatAttachAlertPollLayout.this.solutionString;
                }
                pollEditTextCell3.setTextAndHint(charSequence, LocaleController.getString("AddAnExplanation", NUM), false);
                pollEditTextCell3.setTag((Object) null);
                ChatAttachAlertPollLayout.this.setTextLeft(viewHolder.itemView, viewHolder.getAdapterPosition());
            }
        }

        public void onViewDetachedFromWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 4) {
                EditTextBoldCursor textView = ((PollEditTextCell) viewHolder.itemView).getTextView();
                if (textView.isFocused()) {
                    textView.clearFocus();
                    AndroidUtilities.hideKeyboard(textView);
                }
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == ChatAttachAlertPollLayout.this.addAnswerRow || adapterPosition == ChatAttachAlertPollLayout.this.anonymousRow || adapterPosition == ChatAttachAlertPollLayout.this.multipleRow || (ChatAttachAlertPollLayout.this.quizOnly == 0 && adapterPosition == ChatAttachAlertPollLayout.this.quizRow);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v9, resolved type: org.telegram.ui.Cells.PollEditTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v11, resolved type: org.telegram.ui.Cells.PollEditTextCell} */
        /* JADX WARNING: type inference failed for: r11v4, types: [org.telegram.ui.Cells.ShadowSectionCell, android.view.View] */
        /* JADX WARNING: type inference failed for: r11v5, types: [org.telegram.ui.Cells.TextInfoPrivacyCell] */
        /* JADX WARNING: type inference failed for: r11v6, types: [org.telegram.ui.Cells.TextCell] */
        /* JADX WARNING: type inference failed for: r12v8 */
        /* JADX WARNING: type inference failed for: r11v9, types: [org.telegram.ui.Cells.TextCheckCell] */
        /* JADX WARNING: type inference failed for: r11v11, types: [android.view.View, org.telegram.ui.Components.ChatAttachAlertPollLayout$EmptyView] */
        /* JADX WARNING: type inference failed for: r11v12, types: [org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$5] */
        /* JADX WARNING: type inference failed for: r11v13, types: [org.telegram.ui.Cells.PollEditTextCell, org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$6] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 3 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r11, int r12) {
            /*
                r10 = this;
                r11 = 0
                java.lang.String r0 = "windowBackgroundGray"
                r1 = 1
                switch(r12) {
                    case 0: goto L_0x00bc;
                    case 1: goto L_0x0095;
                    case 2: goto L_0x008d;
                    case 3: goto L_0x0085;
                    case 4: goto L_0x0071;
                    case 5: goto L_0x0007;
                    case 6: goto L_0x0069;
                    case 7: goto L_0x0056;
                    case 8: goto L_0x0046;
                    case 9: goto L_0x003d;
                    default: goto L_0x0007;
                }
            L_0x0007:
                org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$6 r11 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$6
                android.content.Context r12 = r10.mContext
                org.telegram.ui.Components.-$$Lambda$ChatAttachAlertPollLayout$ListAdapter$KNQ7gU60k6g7bRsfSuyq9P8e0LU r0 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlertPollLayout$ListAdapter$KNQ7gU60k6g7bRsfSuyq9P8e0LU
                r0.<init>()
                r11.<init>(r12, r0)
                org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$7 r12 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$7
                r12.<init>(r11)
                r11.addTextWatcher(r12)
                r11.setShowNextButton(r1)
                org.telegram.ui.Components.EditTextBoldCursor r12 = r11.getTextView()
                int r0 = r12.getImeOptions()
                r0 = r0 | 5
                r12.setImeOptions(r0)
                org.telegram.ui.Components.-$$Lambda$ChatAttachAlertPollLayout$ListAdapter$HHvA2RH7fkzBL_OTnB56ALzJK20 r0 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlertPollLayout$ListAdapter$HHvA2RH7fkzBL_OTnB56ALzJK20
                r0.<init>(r11)
                r12.setOnEditorActionListener(r0)
                org.telegram.ui.Components.-$$Lambda$ChatAttachAlertPollLayout$ListAdapter$3unNk7Tvar_PRVsnwtsEJUGMGsuk r0 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlertPollLayout$ListAdapter$3unNk7Tvar_PRVsnwtsEJUGMGsuk
                r0.<init>()
                r12.setOnKeyListener(r0)
                goto L_0x00cb
            L_0x003d:
                org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$5 r11 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$5
                android.content.Context r12 = r10.mContext
                r11.<init>(r12)
                goto L_0x00cb
            L_0x0046:
                org.telegram.ui.Components.ChatAttachAlertPollLayout$EmptyView r11 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$EmptyView
                android.content.Context r12 = r10.mContext
                r11.<init>(r12)
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r11.setBackgroundColor(r12)
                goto L_0x00cb
            L_0x0056:
                org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$3 r12 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$3
                android.content.Context r0 = r10.mContext
                r12.<init>(r0, r1, r11)
                r12.createErrorTextView()
                org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$4 r11 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$4
                r11.<init>(r12)
                r12.addTextWatcher(r11)
                goto L_0x0083
            L_0x0069:
                org.telegram.ui.Cells.TextCheckCell r11 = new org.telegram.ui.Cells.TextCheckCell
                android.content.Context r12 = r10.mContext
                r11.<init>(r12)
                goto L_0x00cb
            L_0x0071:
                org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$1 r12 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$1
                android.content.Context r0 = r10.mContext
                r12.<init>(r0, r11)
                r12.createErrorTextView()
                org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$2 r11 = new org.telegram.ui.Components.ChatAttachAlertPollLayout$ListAdapter$2
                r11.<init>(r12)
                r12.addTextWatcher(r11)
            L_0x0083:
                r11 = r12
                goto L_0x00cb
            L_0x0085:
                org.telegram.ui.Cells.TextCell r11 = new org.telegram.ui.Cells.TextCell
                android.content.Context r12 = r10.mContext
                r11.<init>(r12)
                goto L_0x00cb
            L_0x008d:
                org.telegram.ui.Cells.TextInfoPrivacyCell r11 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r12 = r10.mContext
                r11.<init>(r12)
                goto L_0x00cb
            L_0x0095:
                org.telegram.ui.Cells.ShadowSectionCell r11 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r12 = r10.mContext
                r11.<init>(r12)
                android.content.Context r12 = r10.mContext
                r2 = 2131165446(0x7var_, float:1.794511E38)
                java.lang.String r3 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r12, (int) r2, (java.lang.String) r3)
                org.telegram.ui.Components.CombinedDrawable r2 = new org.telegram.ui.Components.CombinedDrawable
                android.graphics.drawable.ColorDrawable r3 = new android.graphics.drawable.ColorDrawable
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r3.<init>(r0)
                r2.<init>(r3, r12)
                r2.setFullsize(r1)
                r11.setBackgroundDrawable(r2)
                goto L_0x00cb
            L_0x00bc:
                org.telegram.ui.Cells.HeaderCell r11 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r5 = r10.mContext
                r7 = 21
                r8 = 15
                r9 = 0
                java.lang.String r6 = "windowBackgroundWhiteBlueHeader"
                r4 = r11
                r4.<init>(r5, r6, r7, r8, r9)
            L_0x00cb:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r12 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r1 = -2
                r12.<init>((int) r0, (int) r1)
                r11.setLayoutParams(r12)
                org.telegram.ui.Components.RecyclerListView$Holder r12 = new org.telegram.ui.Components.RecyclerListView$Holder
                r12.<init>(r11)
                return r12
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPollLayout.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onCreateViewHolder$0 */
        public /* synthetic */ void lambda$onCreateViewHolder$0$ChatAttachAlertPollLayout$ListAdapter(View view) {
            int adapterPosition;
            if (view.getTag() == null) {
                view.setTag(1);
                PollEditTextCell pollEditTextCell = (PollEditTextCell) view.getParent();
                RecyclerView.ViewHolder findContainingViewHolder = ChatAttachAlertPollLayout.this.listView.findContainingViewHolder(pollEditTextCell);
                if (findContainingViewHolder != null && (adapterPosition = findContainingViewHolder.getAdapterPosition()) != -1) {
                    ChatAttachAlertPollLayout.this.listView.setItemAnimator(ChatAttachAlertPollLayout.this.itemAnimator);
                    int access$2100 = adapterPosition - ChatAttachAlertPollLayout.this.answerStartRow;
                    ChatAttachAlertPollLayout.this.listAdapter.notifyItemRemoved(adapterPosition);
                    int i = access$2100 + 1;
                    System.arraycopy(ChatAttachAlertPollLayout.this.answers, i, ChatAttachAlertPollLayout.this.answers, access$2100, (ChatAttachAlertPollLayout.this.answers.length - 1) - access$2100);
                    System.arraycopy(ChatAttachAlertPollLayout.this.answersChecks, i, ChatAttachAlertPollLayout.this.answersChecks, access$2100, (ChatAttachAlertPollLayout.this.answersChecks.length - 1) - access$2100);
                    ChatAttachAlertPollLayout.this.answers[ChatAttachAlertPollLayout.this.answers.length - 1] = null;
                    ChatAttachAlertPollLayout.this.answersChecks[ChatAttachAlertPollLayout.this.answersChecks.length - 1] = false;
                    ChatAttachAlertPollLayout.access$1210(ChatAttachAlertPollLayout.this);
                    if (ChatAttachAlertPollLayout.this.answersCount == ChatAttachAlertPollLayout.this.answers.length - 1) {
                        ChatAttachAlertPollLayout.this.listAdapter.notifyItemInserted((ChatAttachAlertPollLayout.this.answerStartRow + ChatAttachAlertPollLayout.this.answers.length) - 1);
                    }
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition = ChatAttachAlertPollLayout.this.listView.findViewHolderForAdapterPosition(adapterPosition - 1);
                    EditTextBoldCursor textView = pollEditTextCell.getTextView();
                    if (findViewHolderForAdapterPosition != null) {
                        View view2 = findViewHolderForAdapterPosition.itemView;
                        if (view2 instanceof PollEditTextCell) {
                            ((PollEditTextCell) view2).getTextView().requestFocus();
                            textView.clearFocus();
                            ChatAttachAlertPollLayout.this.checkDoneButton();
                            ChatAttachAlertPollLayout.this.updateRows();
                            ChatAttachAlertPollLayout.this.listAdapter.notifyItemChanged(ChatAttachAlertPollLayout.this.answerSectionRow);
                            ChatAttachAlertPollLayout.this.listAdapter.notifyItemChanged(ChatAttachAlertPollLayout.this.emptyRow);
                        }
                    }
                    if (textView.isFocused()) {
                        AndroidUtilities.hideKeyboard(textView);
                    }
                    textView.clearFocus();
                    ChatAttachAlertPollLayout.this.checkDoneButton();
                    ChatAttachAlertPollLayout.this.updateRows();
                    ChatAttachAlertPollLayout.this.listAdapter.notifyItemChanged(ChatAttachAlertPollLayout.this.answerSectionRow);
                    ChatAttachAlertPollLayout.this.listAdapter.notifyItemChanged(ChatAttachAlertPollLayout.this.emptyRow);
                }
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onCreateViewHolder$1 */
        public /* synthetic */ boolean lambda$onCreateViewHolder$1$ChatAttachAlertPollLayout$ListAdapter(PollEditTextCell pollEditTextCell, TextView textView, int i, KeyEvent keyEvent) {
            int adapterPosition;
            if (i != 5) {
                return false;
            }
            RecyclerView.ViewHolder findContainingViewHolder = ChatAttachAlertPollLayout.this.listView.findContainingViewHolder(pollEditTextCell);
            if (!(findContainingViewHolder == null || (adapterPosition = findContainingViewHolder.getAdapterPosition()) == -1)) {
                int access$2100 = adapterPosition - ChatAttachAlertPollLayout.this.answerStartRow;
                if (access$2100 == ChatAttachAlertPollLayout.this.answersCount - 1 && ChatAttachAlertPollLayout.this.answersCount < 10) {
                    ChatAttachAlertPollLayout.this.addNewField();
                } else if (access$2100 == ChatAttachAlertPollLayout.this.answersCount - 1) {
                    AndroidUtilities.hideKeyboard(pollEditTextCell.getTextView());
                } else {
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition = ChatAttachAlertPollLayout.this.listView.findViewHolderForAdapterPosition(adapterPosition + 1);
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
            if (i == ChatAttachAlertPollLayout.this.questionHeaderRow || i == ChatAttachAlertPollLayout.this.answerHeaderRow || i == ChatAttachAlertPollLayout.this.settingsHeaderRow) {
                return 0;
            }
            if (i == ChatAttachAlertPollLayout.this.questionSectionRow) {
                return 1;
            }
            if (i == ChatAttachAlertPollLayout.this.answerSectionRow || i == ChatAttachAlertPollLayout.this.settingsSectionRow || i == ChatAttachAlertPollLayout.this.solutionInfoRow) {
                return 2;
            }
            if (i == ChatAttachAlertPollLayout.this.addAnswerRow) {
                return 3;
            }
            if (i == ChatAttachAlertPollLayout.this.questionRow) {
                return 4;
            }
            if (i == ChatAttachAlertPollLayout.this.solutionRow) {
                return 7;
            }
            if (i == ChatAttachAlertPollLayout.this.anonymousRow || i == ChatAttachAlertPollLayout.this.multipleRow || i == ChatAttachAlertPollLayout.this.quizRow) {
                return 6;
            }
            if (i == ChatAttachAlertPollLayout.this.emptyRow) {
                return 8;
            }
            return i == ChatAttachAlertPollLayout.this.paddingRow ? 9 : 5;
        }

        public void swapElements(int i, int i2) {
            int access$2100 = i - ChatAttachAlertPollLayout.this.answerStartRow;
            int access$21002 = i2 - ChatAttachAlertPollLayout.this.answerStartRow;
            if (access$2100 >= 0 && access$21002 >= 0 && access$2100 < ChatAttachAlertPollLayout.this.answersCount && access$21002 < ChatAttachAlertPollLayout.this.answersCount) {
                String str = ChatAttachAlertPollLayout.this.answers[access$2100];
                ChatAttachAlertPollLayout.this.answers[access$2100] = ChatAttachAlertPollLayout.this.answers[access$21002];
                ChatAttachAlertPollLayout.this.answers[access$21002] = str;
                boolean z = ChatAttachAlertPollLayout.this.answersChecks[access$2100];
                ChatAttachAlertPollLayout.this.answersChecks[access$2100] = ChatAttachAlertPollLayout.this.answersChecks[access$21002];
                ChatAttachAlertPollLayout.this.answersChecks[access$21002] = z;
                notifyItemMoved(i, i2);
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{EmptyView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"deleteImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"moveImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{PollEditTextCell.class}, new String[]{"deleteImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menuSelector"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{PollEditTextCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{PollEditTextCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{PollEditTextCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        return arrayList;
    }
}
