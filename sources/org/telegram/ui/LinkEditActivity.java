package org.telegram.ui;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_exportedChatInvite;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.SlideChooseView;

public class LinkEditActivity extends BaseFragment {
    private TextCheckCell approveCell;
    /* access modifiers changed from: private */
    public TextView buttonTextView;
    private Callback callback;
    private final long chatId;
    private TextView createTextView;
    int currentInviteDate;
    private final int[] defaultDates = {3600, 86400, 604800};
    private final int[] defaultUses = {1, 10, 100};
    private ArrayList<Integer> dispalyedDates = new ArrayList<>();
    private ArrayList<Integer> dispalyedUses = new ArrayList<>();
    private TextInfoPrivacyCell divider;
    private TextInfoPrivacyCell dividerName;
    private TextInfoPrivacyCell dividerUses;
    /* access modifiers changed from: private */
    public boolean finished;
    /* access modifiers changed from: private */
    public boolean firstLayout = true;
    /* access modifiers changed from: private */
    public boolean ignoreSet;
    TLRPC$TL_chatInviteExported inviteToEdit;
    boolean loading;
    /* access modifiers changed from: private */
    public EditText nameEditText;
    AlertDialog progressDialog;
    private TextSettingsCell revokeLink;
    boolean scrollToEnd;
    boolean scrollToStart;
    /* access modifiers changed from: private */
    public ScrollView scrollView;
    private SlideChooseView timeChooseView;
    private TextView timeEditText;
    private HeaderCell timeHeaderCell;
    private int type;
    private SlideChooseView usesChooseView;
    /* access modifiers changed from: private */
    public EditText usesEditText;
    private HeaderCell usesHeaderCell;

    public interface Callback {
        void onLinkCreated(TLObject tLObject);

        void onLinkEdited(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, TLObject tLObject);

        void onLinkRemoved(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported);

        void revokeLink(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported);
    }

    public LinkEditActivity(int i, long j) {
        this.type = i;
        this.chatId = j;
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        int i = this.type;
        if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("NewLink", NUM));
        } else if (i == 1) {
            this.actionBar.setTitle(LocaleController.getString("EditLink", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    LinkEditActivity.this.finishFragment();
                    AndroidUtilities.hideKeyboard(LinkEditActivity.this.usesEditText);
                }
            }
        });
        TextView textView = new TextView(context2);
        this.createTextView = textView;
        textView.setEllipsize(TextUtils.TruncateAt.END);
        this.createTextView.setGravity(16);
        this.createTextView.setOnClickListener(new LinkEditActivity$$ExternalSyntheticLambda2(this));
        this.createTextView.setSingleLine();
        int i2 = this.type;
        if (i2 == 0) {
            this.createTextView.setText(LocaleController.getString("CreateLinkHeader", NUM));
        } else if (i2 == 1) {
            this.createTextView.setText(LocaleController.getString("SaveLinkHeader", NUM));
        }
        this.createTextView.setTextColor(Theme.getColor("actionBarDefaultTitle"));
        this.createTextView.setTextSize(1, 14.0f);
        this.createTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.createTextView.setPadding(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(18.0f), AndroidUtilities.dp(8.0f));
        this.actionBar.addView(this.createTextView, LayoutHelper.createFrame(-2, -2.0f, 8388629, 0.0f, (float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight / AndroidUtilities.dp(2.0f) : 0), 0.0f, 0.0f));
        this.scrollView = new ScrollView(context2);
        AnonymousClass2 r2 = new SizeNotifierFrameLayout(context2) {
            int oldKeyboardHeight;

            /* access modifiers changed from: protected */
            public AdjustPanLayoutHelper createAdjustPanLayoutHelper() {
                AnonymousClass1 r0 = new AdjustPanLayoutHelper(this) {
                    /* access modifiers changed from: protected */
                    public void onTransitionStart(boolean z, int i) {
                        super.onTransitionStart(z, i);
                        LinkEditActivity.this.scrollView.getLayoutParams().height = i;
                    }

                    /* access modifiers changed from: protected */
                    public void onTransitionEnd() {
                        super.onTransitionEnd();
                        LinkEditActivity.this.scrollView.getLayoutParams().height = -1;
                        LinkEditActivity.this.scrollView.requestLayout();
                    }

                    /* access modifiers changed from: protected */
                    public void onPanTranslationUpdate(float f, float f2, boolean z) {
                        super.onPanTranslationUpdate(f, f2, z);
                        AnonymousClass2.this.setTranslationY(0.0f);
                    }

                    /* access modifiers changed from: protected */
                    public boolean heightAnimationEnabled() {
                        return !LinkEditActivity.this.finished;
                    }
                };
                r0.setCheckHierarchyHeight(true);
                return r0;
            }

            /* access modifiers changed from: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                this.adjustPanLayoutHelper.onAttach();
            }

            /* access modifiers changed from: protected */
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                this.adjustPanLayoutHelper.onDetach();
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                measureKeyboardHeight();
                boolean z = LinkEditActivity.this.usesEditText.isCursorVisible() || LinkEditActivity.this.nameEditText.isCursorVisible();
                int i3 = this.oldKeyboardHeight;
                int i4 = this.keyboardHeight;
                if (i3 != i4 && i4 > AndroidUtilities.dp(20.0f) && z) {
                    LinkEditActivity.this.scrollToEnd = true;
                    invalidate();
                } else if (LinkEditActivity.this.scrollView.getScrollY() == 0 && !z) {
                    LinkEditActivity.this.scrollToStart = true;
                    invalidate();
                }
                int i5 = this.keyboardHeight;
                if (i5 != 0 && i5 < AndroidUtilities.dp(20.0f)) {
                    LinkEditActivity.this.usesEditText.clearFocus();
                    LinkEditActivity.this.nameEditText.clearFocus();
                }
                this.oldKeyboardHeight = this.keyboardHeight;
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int scrollY = LinkEditActivity.this.scrollView.getScrollY();
                super.onLayout(z, i, i2, i3, i4);
                if (scrollY != LinkEditActivity.this.scrollView.getScrollY()) {
                    LinkEditActivity linkEditActivity = LinkEditActivity.this;
                    if (!linkEditActivity.scrollToEnd) {
                        linkEditActivity.scrollView.setTranslationY((float) (LinkEditActivity.this.scrollView.getScrollY() - scrollY));
                        LinkEditActivity.this.scrollView.animate().cancel();
                        LinkEditActivity.this.scrollView.animate().translationY(0.0f).setDuration(250).setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator).start();
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                LinkEditActivity linkEditActivity = LinkEditActivity.this;
                if (linkEditActivity.scrollToEnd) {
                    linkEditActivity.scrollToEnd = false;
                    linkEditActivity.scrollView.smoothScrollTo(0, Math.max(0, LinkEditActivity.this.scrollView.getChildAt(0).getMeasuredHeight() - LinkEditActivity.this.scrollView.getMeasuredHeight()));
                } else if (linkEditActivity.scrollToStart) {
                    linkEditActivity.scrollToStart = false;
                    linkEditActivity.scrollView.smoothScrollTo(0, 0);
                }
            }
        };
        this.fragmentView = r2;
        AnonymousClass3 r8 = new LinearLayout(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int i3;
                super.onMeasure(i, i2);
                int size = View.MeasureSpec.getSize(i2);
                int i4 = 0;
                for (int i5 = 0; i5 < getChildCount(); i5++) {
                    View childAt = getChildAt(i5);
                    if (!(childAt == LinkEditActivity.this.buttonTextView || childAt.getVisibility() == 8)) {
                        i4 += childAt.getMeasuredHeight();
                    }
                }
                int dp = size - ((AndroidUtilities.dp(48.0f) + AndroidUtilities.dp(24.0f)) + AndroidUtilities.dp(16.0f));
                if (i4 >= dp) {
                    i3 = AndroidUtilities.dp(24.0f);
                } else {
                    i3 = (AndroidUtilities.dp(24.0f) + dp) - i4;
                }
                if (((LinearLayout.LayoutParams) LinkEditActivity.this.buttonTextView.getLayoutParams()).topMargin != i3) {
                    int i6 = ((LinearLayout.LayoutParams) LinkEditActivity.this.buttonTextView.getLayoutParams()).topMargin;
                    ((LinearLayout.LayoutParams) LinkEditActivity.this.buttonTextView.getLayoutParams()).topMargin = i3;
                    if (!LinkEditActivity.this.firstLayout) {
                        LinkEditActivity.this.buttonTextView.setTranslationY((float) (i6 - i3));
                        LinkEditActivity.this.buttonTextView.animate().translationY(0.0f).setDuration(250).setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator).start();
                    }
                    super.onMeasure(i, i2);
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                boolean unused = LinkEditActivity.this.firstLayout = false;
            }
        };
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setDuration(100);
        r8.setLayoutTransition(layoutTransition);
        r8.setOrientation(1);
        this.scrollView.addView(r8);
        TextView textView2 = new TextView(context2);
        this.buttonTextView = textView2;
        textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        int i3 = this.type;
        if (i3 == 0) {
            this.buttonTextView.setText(LocaleController.getString("CreateLink", NUM));
        } else if (i3 == 1) {
            this.buttonTextView.setText(LocaleController.getString("SaveLink", NUM));
        }
        AnonymousClass4 r5 = new TextCheckCell(this, context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                canvas.save();
                canvas.clipRect(0, 0, getWidth(), getHeight());
                super.onDraw(canvas);
                canvas.restore();
            }
        };
        this.approveCell = r5;
        r5.setBackgroundColor(Theme.getColor("windowBackgroundUnchecked"));
        this.approveCell.setColors("windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
        this.approveCell.setDrawCheckRipple(true);
        this.approveCell.setHeight(56);
        this.approveCell.setTag("windowBackgroundUnchecked");
        this.approveCell.setTextAndCheck(LocaleController.getString("ApproveNewMembers", NUM), false, false);
        this.approveCell.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.approveCell.setOnClickListener(new LinkEditActivity$$ExternalSyntheticLambda1(this));
        r8.addView(this.approveCell, LayoutHelper.createLinear(-1, 56));
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
        textInfoPrivacyCell.setBackground(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        textInfoPrivacyCell.setText(LocaleController.getString("ApproveNewMembersDescription", NUM));
        r8.addView(textInfoPrivacyCell);
        HeaderCell headerCell = new HeaderCell(context2);
        this.timeHeaderCell = headerCell;
        headerCell.setText(LocaleController.getString("LimitByPeriod", NUM));
        r8.addView(this.timeHeaderCell);
        SlideChooseView slideChooseView = new SlideChooseView(context2);
        this.timeChooseView = slideChooseView;
        r8.addView(slideChooseView);
        TextView textView3 = new TextView(context2);
        this.timeEditText = textView3;
        textView3.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
        this.timeEditText.setGravity(16);
        this.timeEditText.setTextSize(1, 16.0f);
        this.timeEditText.setHint(LocaleController.getString("TimeLimitHint", NUM));
        this.timeEditText.setOnClickListener(new LinkEditActivity$$ExternalSyntheticLambda4(this, context2));
        this.timeChooseView.setCallback(new LinkEditActivity$$ExternalSyntheticLambda11(this));
        resetDates();
        r8.addView(this.timeEditText, LayoutHelper.createLinear(-1, 50));
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
        this.divider = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setText(LocaleController.getString("TimeLimitHelp", NUM));
        r8.addView(this.divider);
        HeaderCell headerCell2 = new HeaderCell(context2);
        this.usesHeaderCell = headerCell2;
        headerCell2.setText(LocaleController.getString("LimitNumberOfUses", NUM));
        r8.addView(this.usesHeaderCell);
        SlideChooseView slideChooseView2 = new SlideChooseView(context2);
        this.usesChooseView = slideChooseView2;
        slideChooseView2.setCallback(new LinkEditActivity$$ExternalSyntheticLambda12(this));
        resetUses();
        r8.addView(this.usesChooseView);
        AnonymousClass5 r52 = new EditText(this, context2) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 1) {
                    setCursorVisible(true);
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.usesEditText = r52;
        r52.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
        this.usesEditText.setGravity(16);
        this.usesEditText.setTextSize(1, 16.0f);
        this.usesEditText.setHint(LocaleController.getString("UsesLimitHint", NUM));
        this.usesEditText.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        this.usesEditText.setInputType(2);
        this.usesEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (!LinkEditActivity.this.ignoreSet) {
                    if (editable.toString().equals("0")) {
                        LinkEditActivity.this.usesEditText.setText("");
                        return;
                    }
                    try {
                        int parseInt = Integer.parseInt(editable.toString());
                        if (parseInt > 100000) {
                            LinkEditActivity.this.resetUses();
                        } else {
                            LinkEditActivity.this.chooseUses(parseInt);
                        }
                    } catch (NumberFormatException unused) {
                        LinkEditActivity.this.resetUses();
                    }
                }
            }
        });
        r8.addView(this.usesEditText, LayoutHelper.createLinear(-1, 50));
        TextInfoPrivacyCell textInfoPrivacyCell3 = new TextInfoPrivacyCell(context2);
        this.dividerUses = textInfoPrivacyCell3;
        textInfoPrivacyCell3.setText(LocaleController.getString("UsesLimitHelp", NUM));
        r8.addView(this.dividerUses);
        AnonymousClass7 r53 = new EditText(this, context2) {
            @SuppressLint({"ClickableViewAccessibility"})
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 1) {
                    setCursorVisible(true);
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.nameEditText = r53;
        r53.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(editable);
                Emoji.replaceEmoji(spannableStringBuilder, LinkEditActivity.this.nameEditText.getPaint().getFontMetricsInt(), (int) LinkEditActivity.this.nameEditText.getPaint().getTextSize(), false);
                int selectionStart = LinkEditActivity.this.nameEditText.getSelectionStart();
                LinkEditActivity.this.nameEditText.removeTextChangedListener(this);
                LinkEditActivity.this.nameEditText.setText(spannableStringBuilder);
                if (selectionStart >= 0) {
                    LinkEditActivity.this.nameEditText.setSelection(selectionStart);
                }
                LinkEditActivity.this.nameEditText.addTextChangedListener(this);
            }
        });
        this.nameEditText.setCursorVisible(false);
        this.nameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
        this.nameEditText.setGravity(16);
        this.nameEditText.setHint(LocaleController.getString("LinkNameHint", NUM));
        this.nameEditText.setHintTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        this.nameEditText.setLines(1);
        this.nameEditText.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
        this.nameEditText.setSingleLine();
        this.nameEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameEditText.setTextSize(1, 16.0f);
        r8.addView(this.nameEditText, LayoutHelper.createLinear(-1, 50));
        TextInfoPrivacyCell textInfoPrivacyCell4 = new TextInfoPrivacyCell(context2);
        this.dividerName = textInfoPrivacyCell4;
        textInfoPrivacyCell4.setBackground(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        this.dividerName.setText(LocaleController.getString("LinkNameHelp", NUM));
        r8.addView(this.dividerName);
        if (this.type == 1) {
            TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
            this.revokeLink = textSettingsCell;
            textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.revokeLink.setText(LocaleController.getString("RevokeLink", NUM), false);
            this.revokeLink.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
            this.revokeLink.setOnClickListener(new LinkEditActivity$$ExternalSyntheticLambda3(this));
            r8.addView(this.revokeLink);
        }
        r2.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        r8.addView(this.buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 15.0f, 16.0f, 16.0f));
        this.timeHeaderCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.timeChooseView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.timeEditText.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.usesHeaderCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.usesChooseView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.usesEditText.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.nameEditText.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        r2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.buttonTextView.setOnClickListener(new LinkEditActivity$$ExternalSyntheticLambda2(this));
        this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.dividerUses.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        this.divider.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        this.usesEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.usesEditText.setHintTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        this.timeEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.timeEditText.setHintTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        this.usesEditText.setCursorVisible(false);
        setInviteToEdit(this.inviteToEdit);
        r2.setClipChildren(false);
        this.scrollView.setClipChildren(false);
        r8.setClipChildren(false);
        return r2;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view) {
        TextCheckCell textCheckCell = (TextCheckCell) view;
        boolean z = !textCheckCell.isChecked();
        textCheckCell.setBackgroundColorAnimated(z, Theme.getColor(z ? "windowBackgroundChecked" : "windowBackgroundUnchecked"));
        textCheckCell.setChecked(z);
        setUsesVisible(!z);
        this.firstLayout = true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(boolean z, int i) {
        chooseDate(i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(Context context, View view) {
        AlertsCreator.createDatePickerDialog(context, -1, new LinkEditActivity$$ExternalSyntheticLambda10(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(int i) {
        if (i < this.dispalyedDates.size()) {
            this.timeEditText.setText(LocaleController.formatDateAudio((long) (this.dispalyedDates.get(i).intValue() + getConnectionsManager().getCurrentTime()), false));
            return;
        }
        this.timeEditText.setText("");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(int i) {
        this.usesEditText.clearFocus();
        this.ignoreSet = true;
        if (i < this.dispalyedUses.size()) {
            this.usesEditText.setText(this.dispalyedUses.get(i).toString());
        } else {
            this.usesEditText.setText("");
        }
        this.ignoreSet = false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setMessage(LocaleController.getString("RevokeAlert", NUM));
        builder.setTitle(LocaleController.getString("RevokeLink", NUM));
        builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new LinkEditActivity$$ExternalSyntheticLambda0(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(DialogInterface dialogInterface, int i) {
        this.callback.revokeLink(this.inviteToEdit);
        finishFragment();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0170  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x018b  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x01a6  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x01d3  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x01de  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x01fb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreateClicked(android.view.View r9) {
        /*
            r8 = this;
            boolean r9 = r8.loading
            if (r9 == 0) goto L_0x0005
            return
        L_0x0005:
            org.telegram.ui.Components.SlideChooseView r9 = r8.timeChooseView
            int r9 = r9.getSelectedIndex()
            java.util.ArrayList<java.lang.Integer> r0 = r8.dispalyedDates
            int r0 = r0.size()
            r1 = 0
            if (r9 >= r0) goto L_0x003f
            java.util.ArrayList<java.lang.Integer> r0 = r8.dispalyedDates
            java.lang.Object r9 = r0.get(r9)
            java.lang.Integer r9 = (java.lang.Integer) r9
            int r9 = r9.intValue()
            if (r9 >= 0) goto L_0x003f
            android.widget.TextView r9 = r8.timeEditText
            r0 = 1073741824(0x40000000, float:2.0)
            org.telegram.messenger.AndroidUtilities.shakeView(r9, r0, r1)
            android.widget.TextView r9 = r8.timeEditText
            android.content.Context r9 = r9.getContext()
            java.lang.String r0 = "vibrator"
            java.lang.Object r9 = r9.getSystemService(r0)
            android.os.Vibrator r9 = (android.os.Vibrator) r9
            if (r9 == 0) goto L_0x003e
            r0 = 200(0xc8, double:9.9E-322)
            r9.vibrate(r0)
        L_0x003e:
            return
        L_0x003f:
            int r9 = r8.type
            r2 = 500(0x1f4, double:2.47E-321)
            r0 = 3
            r4 = 1
            if (r9 != 0) goto L_0x00f6
            org.telegram.ui.ActionBar.AlertDialog r9 = r8.progressDialog
            if (r9 == 0) goto L_0x004e
            r9.dismiss()
        L_0x004e:
            r8.loading = r4
            org.telegram.ui.ActionBar.AlertDialog r9 = new org.telegram.ui.ActionBar.AlertDialog
            android.app.Activity r5 = r8.getParentActivity()
            r9.<init>(r5, r0)
            r8.progressDialog = r9
            r9.showDelayed(r2)
            org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite r9 = new org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite
            r9.<init>()
            org.telegram.messenger.MessagesController r0 = r8.getMessagesController()
            long r2 = r8.chatId
            long r2 = -r2
            org.telegram.tgnet.TLRPC$InputPeer r0 = r0.getInputPeer((long) r2)
            r9.peer = r0
            r9.legacy_revoke_permanent = r1
            org.telegram.ui.Components.SlideChooseView r0 = r8.timeChooseView
            int r0 = r0.getSelectedIndex()
            int r2 = r9.flags
            r2 = r2 | r4
            r9.flags = r2
            java.util.ArrayList<java.lang.Integer> r2 = r8.dispalyedDates
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x009d
            java.util.ArrayList<java.lang.Integer> r2 = r8.dispalyedDates
            java.lang.Object r0 = r2.get(r0)
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            org.telegram.tgnet.ConnectionsManager r2 = r8.getConnectionsManager()
            int r2 = r2.getCurrentTime()
            int r0 = r0 + r2
            r9.expire_date = r0
            goto L_0x009f
        L_0x009d:
            r9.expire_date = r1
        L_0x009f:
            org.telegram.ui.Components.SlideChooseView r0 = r8.usesChooseView
            int r0 = r0.getSelectedIndex()
            int r2 = r9.flags
            r2 = r2 | 2
            r9.flags = r2
            java.util.ArrayList<java.lang.Integer> r2 = r8.dispalyedUses
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x00c2
            java.util.ArrayList<java.lang.Integer> r2 = r8.dispalyedUses
            java.lang.Object r0 = r2.get(r0)
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r9.usage_limit = r0
            goto L_0x00c4
        L_0x00c2:
            r9.usage_limit = r1
        L_0x00c4:
            org.telegram.ui.Cells.TextCheckCell r0 = r8.approveCell
            boolean r0 = r0.isChecked()
            r9.request_needed = r0
            if (r0 == 0) goto L_0x00d0
            r9.usage_limit = r1
        L_0x00d0:
            android.widget.EditText r0 = r8.nameEditText
            android.text.Editable r0 = r0.getText()
            java.lang.String r0 = r0.toString()
            r9.title = r0
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x00e8
            int r0 = r9.flags
            r0 = r0 | 16
            r9.flags = r0
        L_0x00e8:
            org.telegram.tgnet.ConnectionsManager r0 = r8.getConnectionsManager()
            org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda7 r1 = new org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda7
            r1.<init>(r8)
            r0.sendRequest(r9, r1)
            goto L_0x01fe
        L_0x00f6:
            if (r9 != r4) goto L_0x01fe
            org.telegram.ui.ActionBar.AlertDialog r9 = r8.progressDialog
            if (r9 == 0) goto L_0x00ff
            r9.dismiss()
        L_0x00ff:
            org.telegram.tgnet.TLRPC$TL_messages_editExportedChatInvite r9 = new org.telegram.tgnet.TLRPC$TL_messages_editExportedChatInvite
            r9.<init>()
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r5 = r8.inviteToEdit
            java.lang.String r5 = r5.link
            r9.link = r5
            r9.revoked = r1
            org.telegram.messenger.MessagesController r5 = r8.getMessagesController()
            long r6 = r8.chatId
            long r6 = -r6
            org.telegram.tgnet.TLRPC$InputPeer r5 = r5.getInputPeer((long) r6)
            r9.peer = r5
            org.telegram.ui.Components.SlideChooseView r5 = r8.timeChooseView
            int r5 = r5.getSelectedIndex()
            java.util.ArrayList<java.lang.Integer> r6 = r8.dispalyedDates
            int r6 = r6.size()
            if (r5 >= r6) goto L_0x0154
            int r6 = r8.currentInviteDate
            java.util.ArrayList<java.lang.Integer> r7 = r8.dispalyedDates
            java.lang.Object r7 = r7.get(r5)
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r7 = r7.intValue()
            if (r6 == r7) goto L_0x0161
            int r6 = r9.flags
            r6 = r6 | r4
            r9.flags = r6
            java.util.ArrayList<java.lang.Integer> r6 = r8.dispalyedDates
            java.lang.Object r5 = r6.get(r5)
            java.lang.Integer r5 = (java.lang.Integer) r5
            int r5 = r5.intValue()
            org.telegram.tgnet.ConnectionsManager r6 = r8.getConnectionsManager()
            int r6 = r6.getCurrentTime()
            int r5 = r5 + r6
            r9.expire_date = r5
            goto L_0x015f
        L_0x0154:
            int r5 = r8.currentInviteDate
            if (r5 == 0) goto L_0x0161
            int r5 = r9.flags
            r5 = r5 | r4
            r9.flags = r5
            r9.expire_date = r1
        L_0x015f:
            r5 = 1
            goto L_0x0162
        L_0x0161:
            r5 = 0
        L_0x0162:
            org.telegram.ui.Components.SlideChooseView r6 = r8.usesChooseView
            int r6 = r6.getSelectedIndex()
            java.util.ArrayList<java.lang.Integer> r7 = r8.dispalyedUses
            int r7 = r7.size()
            if (r6 >= r7) goto L_0x018b
            java.util.ArrayList<java.lang.Integer> r7 = r8.dispalyedUses
            java.lang.Object r6 = r7.get(r6)
            java.lang.Integer r6 = (java.lang.Integer) r6
            int r6 = r6.intValue()
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r7 = r8.inviteToEdit
            int r7 = r7.usage_limit
            if (r7 == r6) goto L_0x019a
            int r5 = r9.flags
            r5 = r5 | 2
            r9.flags = r5
            r9.usage_limit = r6
            goto L_0x0199
        L_0x018b:
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r6 = r8.inviteToEdit
            int r6 = r6.usage_limit
            if (r6 == 0) goto L_0x019a
            int r5 = r9.flags
            r5 = r5 | 2
            r9.flags = r5
            r9.usage_limit = r1
        L_0x0199:
            r5 = 1
        L_0x019a:
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r6 = r8.inviteToEdit
            boolean r6 = r6.request_needed
            org.telegram.ui.Cells.TextCheckCell r7 = r8.approveCell
            boolean r7 = r7.isChecked()
            if (r6 == r7) goto L_0x01bf
            int r5 = r9.flags
            r5 = r5 | 8
            r9.flags = r5
            org.telegram.ui.Cells.TextCheckCell r5 = r8.approveCell
            boolean r5 = r5.isChecked()
            r9.request_needed = r5
            if (r5 == 0) goto L_0x01be
            int r5 = r9.flags
            r5 = r5 | 2
            r9.flags = r5
            r9.usage_limit = r1
        L_0x01be:
            r5 = 1
        L_0x01bf:
            android.widget.EditText r1 = r8.nameEditText
            android.text.Editable r1 = r1.getText()
            java.lang.String r1 = r1.toString()
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r6 = r8.inviteToEdit
            java.lang.String r6 = r6.title
            boolean r6 = android.text.TextUtils.equals(r6, r1)
            if (r6 != 0) goto L_0x01dc
            r9.title = r1
            int r1 = r9.flags
            r1 = r1 | 16
            r9.flags = r1
            r5 = 1
        L_0x01dc:
            if (r5 == 0) goto L_0x01fb
            r8.loading = r4
            org.telegram.ui.ActionBar.AlertDialog r1 = new org.telegram.ui.ActionBar.AlertDialog
            android.app.Activity r4 = r8.getParentActivity()
            r1.<init>(r4, r0)
            r8.progressDialog = r1
            r1.showDelayed(r2)
            org.telegram.tgnet.ConnectionsManager r0 = r8.getConnectionsManager()
            org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda8 r1 = new org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda8
            r1.<init>(r8)
            r0.sendRequest(r9, r1)
            goto L_0x01fe
        L_0x01fb:
            r8.finishFragment()
        L_0x01fe:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LinkEditActivity.onCreateClicked(android.view.View):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateClicked$8(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LinkEditActivity$$ExternalSyntheticLambda6(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateClicked$7(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        this.loading = false;
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        if (tLRPC$TL_error == null) {
            Callback callback2 = this.callback;
            if (callback2 != null) {
                callback2.onLinkCreated(tLObject);
            }
            finishFragment();
            return;
        }
        AlertsCreator.showSimpleAlert(this, tLRPC$TL_error.text);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateClicked$10(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LinkEditActivity$$ExternalSyntheticLambda5(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateClicked$9(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        this.loading = false;
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        if (tLRPC$TL_error == null) {
            if (tLObject instanceof TLRPC$TL_messages_exportedChatInvite) {
                this.inviteToEdit = (TLRPC$TL_chatInviteExported) ((TLRPC$TL_messages_exportedChatInvite) tLObject).invite;
            }
            Callback callback2 = this.callback;
            if (callback2 != null) {
                callback2.onLinkEdited(this.inviteToEdit, tLObject);
            }
            finishFragment();
            return;
        }
        AlertsCreator.showSimpleAlert(this, tLRPC$TL_error.text);
    }

    /* access modifiers changed from: private */
    public void chooseUses(int i) {
        this.dispalyedUses.clear();
        int i2 = 0;
        boolean z = false;
        int i3 = 0;
        while (true) {
            int[] iArr = this.defaultUses;
            if (i2 >= iArr.length) {
                break;
            }
            if (!z && i <= iArr[i2]) {
                if (i != iArr[i2]) {
                    this.dispalyedUses.add(Integer.valueOf(i));
                }
                i3 = i2;
                z = true;
            }
            this.dispalyedUses.add(Integer.valueOf(this.defaultUses[i2]));
            i2++;
        }
        if (!z) {
            this.dispalyedUses.add(Integer.valueOf(i));
            i3 = this.defaultUses.length;
        }
        int size = this.dispalyedUses.size() + 1;
        String[] strArr = new String[size];
        for (int i4 = 0; i4 < size; i4++) {
            if (i4 == size - 1) {
                strArr[i4] = LocaleController.getString("NoLimit", NUM);
            } else {
                strArr[i4] = this.dispalyedUses.get(i4).toString();
            }
        }
        this.usesChooseView.setOptions(i3, strArr);
    }

    private void chooseDate(int i) {
        int i2 = i;
        long j = (long) i2;
        this.timeEditText.setText(LocaleController.formatDateAudio(j, false));
        int currentTime = i2 - getConnectionsManager().getCurrentTime();
        this.dispalyedDates.clear();
        int i3 = 0;
        boolean z = false;
        int i4 = 0;
        while (true) {
            int[] iArr = this.defaultDates;
            if (i3 >= iArr.length) {
                break;
            }
            if (!z && currentTime < iArr[i3]) {
                this.dispalyedDates.add(Integer.valueOf(currentTime));
                i4 = i3;
                z = true;
            }
            this.dispalyedDates.add(Integer.valueOf(this.defaultDates[i3]));
            i3++;
        }
        if (!z) {
            this.dispalyedDates.add(Integer.valueOf(currentTime));
            i4 = this.defaultDates.length;
        }
        int size = this.dispalyedDates.size() + 1;
        String[] strArr = new String[size];
        for (int i5 = 0; i5 < size; i5++) {
            if (i5 == size - 1) {
                strArr[i5] = LocaleController.getString("NoLimit", NUM);
            } else if (this.dispalyedDates.get(i5).intValue() == this.defaultDates[0]) {
                strArr[i5] = LocaleController.formatPluralString("Hours", 1);
            } else if (this.dispalyedDates.get(i5).intValue() == this.defaultDates[1]) {
                strArr[i5] = LocaleController.formatPluralString("Days", 1);
            } else if (this.dispalyedDates.get(i5).intValue() == this.defaultDates[2]) {
                strArr[i5] = LocaleController.formatPluralString("Weeks", 1);
            } else {
                long j2 = (long) currentTime;
                if (j2 < 86400) {
                    strArr[i5] = LocaleController.getString("MessageScheduleToday", NUM);
                } else if (j2 < 31449600) {
                    strArr[i5] = LocaleController.getInstance().formatterScheduleDay.format(j * 1000);
                } else {
                    strArr[i5] = LocaleController.getInstance().formatterYear.format(j * 1000);
                }
            }
        }
        this.timeChooseView.setOptions(i4, strArr);
    }

    private void resetDates() {
        this.dispalyedDates.clear();
        int i = 0;
        while (true) {
            int[] iArr = this.defaultDates;
            if (i < iArr.length) {
                this.dispalyedDates.add(Integer.valueOf(iArr[i]));
                i++;
            } else {
                this.timeChooseView.setOptions(3, LocaleController.formatPluralString("Hours", 1), LocaleController.formatPluralString("Days", 1), LocaleController.formatPluralString("Weeks", 1), LocaleController.getString("NoLimit", NUM));
                return;
            }
        }
    }

    public void setCallback(Callback callback2) {
        this.callback = callback2;
    }

    /* access modifiers changed from: private */
    public void resetUses() {
        this.dispalyedUses.clear();
        int i = 0;
        while (true) {
            int[] iArr = this.defaultUses;
            if (i < iArr.length) {
                this.dispalyedUses.add(Integer.valueOf(iArr[i]));
                i++;
            } else {
                this.usesChooseView.setOptions(3, "1", "10", "100", LocaleController.getString("NoLimit", NUM));
                return;
            }
        }
    }

    public void setInviteToEdit(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
        this.inviteToEdit = tLRPC$TL_chatInviteExported;
        if (this.fragmentView != null && tLRPC$TL_chatInviteExported != null) {
            int i = tLRPC$TL_chatInviteExported.expire_date;
            if (i > 0) {
                chooseDate(i);
                this.currentInviteDate = this.dispalyedDates.get(this.timeChooseView.getSelectedIndex()).intValue();
            } else {
                this.currentInviteDate = 0;
            }
            int i2 = tLRPC$TL_chatInviteExported.usage_limit;
            if (i2 > 0) {
                chooseUses(i2);
                this.usesEditText.setText(Integer.toString(tLRPC$TL_chatInviteExported.usage_limit));
            }
            this.approveCell.setBackgroundColor(Theme.getColor(tLRPC$TL_chatInviteExported.request_needed ? "windowBackgroundChecked" : "windowBackgroundUnchecked"));
            this.approveCell.setChecked(tLRPC$TL_chatInviteExported.request_needed);
            setUsesVisible(!tLRPC$TL_chatInviteExported.request_needed);
            if (!TextUtils.isEmpty(tLRPC$TL_chatInviteExported.title)) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(tLRPC$TL_chatInviteExported.title);
                Emoji.replaceEmoji(spannableStringBuilder, this.nameEditText.getPaint().getFontMetricsInt(), (int) this.nameEditText.getPaint().getTextSize(), false);
                this.nameEditText.setText(spannableStringBuilder);
            }
        }
    }

    private void setUsesVisible(boolean z) {
        int i = 0;
        this.usesHeaderCell.setVisibility(z ? 0 : 8);
        this.usesChooseView.setVisibility(z ? 0 : 8);
        this.usesEditText.setVisibility(z ? 0 : 8);
        TextInfoPrivacyCell textInfoPrivacyCell = this.dividerUses;
        if (!z) {
            i = 8;
        }
        textInfoPrivacyCell.setVisibility(i);
        this.divider.setBackground(Theme.getThemedDrawable((Context) getParentActivity(), z ? NUM : NUM, "windowBackgroundGrayShadow"));
    }

    public void finishFragment() {
        this.scrollView.getLayoutParams().height = this.scrollView.getHeight();
        this.finished = true;
        super.finishFragment();
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        LinkEditActivity$$ExternalSyntheticLambda9 linkEditActivity$$ExternalSyntheticLambda9 = new LinkEditActivity$$ExternalSyntheticLambda9(this);
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription((View) this.timeHeaderCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.usesHeaderCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.timeHeaderCell, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.usesHeaderCell, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.timeChooseView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.usesChooseView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.timeEditText, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.usesEditText, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.revokeLink, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.divider, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.dividerUses, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.dividerName, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        LinkEditActivity$$ExternalSyntheticLambda9 linkEditActivity$$ExternalSyntheticLambda92 = linkEditActivity$$ExternalSyntheticLambda9;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, linkEditActivity$$ExternalSyntheticLambda92, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, linkEditActivity$$ExternalSyntheticLambda92, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, linkEditActivity$$ExternalSyntheticLambda92, "featuredStickers_addButtonPressed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, linkEditActivity$$ExternalSyntheticLambda92, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, linkEditActivity$$ExternalSyntheticLambda92, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, linkEditActivity$$ExternalSyntheticLambda92, "featuredStickers_buttonText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, linkEditActivity$$ExternalSyntheticLambda92, "windowBackgroundWhiteRedText5"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$11() {
        TextInfoPrivacyCell textInfoPrivacyCell = this.dividerUses;
        if (textInfoPrivacyCell != null) {
            Context context = textInfoPrivacyCell.getContext();
            this.dividerUses.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
            this.divider.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
            this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
            this.usesEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.usesEditText.setHintTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            this.timeEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.timeEditText.setHintTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            TextSettingsCell textSettingsCell = this.revokeLink;
            if (textSettingsCell != null) {
                textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
            }
            this.createTextView.setTextColor(Theme.getColor("actionBarDefaultTitle"));
            this.dividerName.setBackground(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
            this.nameEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.nameEditText.setHintTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        }
    }
}
