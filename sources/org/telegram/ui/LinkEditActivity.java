package org.telegram.ui;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
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
import org.telegram.tgnet.TLRPC;
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
    public static final int CREATE_TYPE = 0;
    public static final int EDIT_TYPE = 1;
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
    TLRPC.TL_chatInviteExported inviteToEdit;
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

        void onLinkEdited(TLRPC.TL_chatInviteExported tL_chatInviteExported, TLObject tLObject);

        void onLinkRemoved(TLRPC.TL_chatInviteExported tL_chatInviteExported);

        void revokeLink(TLRPC.TL_chatInviteExported tL_chatInviteExported);
    }

    public LinkEditActivity(int type2, long chatId2) {
        this.type = type2;
        this.chatId = chatId2;
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
            public void onItemClick(int id) {
                if (id == -1) {
                    LinkEditActivity.this.finishFragment();
                    AndroidUtilities.hideKeyboard(LinkEditActivity.this.usesEditText);
                }
            }
        });
        TextView textView = new TextView(context2);
        this.createTextView = textView;
        textView.setEllipsize(TextUtils.TruncateAt.END);
        this.createTextView.setGravity(16);
        this.createTextView.setOnClickListener(new LinkEditActivity$$ExternalSyntheticLambda6(this));
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
        SizeNotifierFrameLayout contentView = new SizeNotifierFrameLayout(context2) {
            int oldKeyboardHeight;

            /* access modifiers changed from: protected */
            public AdjustPanLayoutHelper createAdjustPanLayoutHelper() {
                AdjustPanLayoutHelper panLayoutHelper = new AdjustPanLayoutHelper(this) {
                    /* access modifiers changed from: protected */
                    public void onTransitionStart(boolean keyboardVisible, int contentHeight) {
                        super.onTransitionStart(keyboardVisible, contentHeight);
                        LinkEditActivity.this.scrollView.getLayoutParams().height = contentHeight;
                    }

                    /* access modifiers changed from: protected */
                    public void onTransitionEnd() {
                        super.onTransitionEnd();
                        LinkEditActivity.this.scrollView.getLayoutParams().height = -1;
                        LinkEditActivity.this.scrollView.requestLayout();
                    }

                    /* access modifiers changed from: protected */
                    public void onPanTranslationUpdate(float y, float progress, boolean keyboardVisible) {
                        super.onPanTranslationUpdate(y, progress, keyboardVisible);
                        AnonymousClass2.this.setTranslationY(0.0f);
                    }

                    /* access modifiers changed from: protected */
                    public boolean heightAnimationEnabled() {
                        return !LinkEditActivity.this.finished;
                    }
                };
                panLayoutHelper.setCheckHierarchyHeight(true);
                return panLayoutHelper;
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
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                measureKeyboardHeight();
                boolean isNeedScrollToEnd = LinkEditActivity.this.usesEditText.isCursorVisible() || LinkEditActivity.this.nameEditText.isCursorVisible();
                if (this.oldKeyboardHeight != this.keyboardHeight && this.keyboardHeight > AndroidUtilities.dp(20.0f) && isNeedScrollToEnd) {
                    LinkEditActivity.this.scrollToEnd = true;
                    invalidate();
                } else if (LinkEditActivity.this.scrollView.getScrollY() == 0 && !isNeedScrollToEnd) {
                    LinkEditActivity.this.scrollToStart = true;
                    invalidate();
                }
                if (this.keyboardHeight != 0 && this.keyboardHeight < AndroidUtilities.dp(20.0f)) {
                    LinkEditActivity.this.usesEditText.clearFocus();
                    LinkEditActivity.this.nameEditText.clearFocus();
                }
                this.oldKeyboardHeight = this.keyboardHeight;
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int scrollY = LinkEditActivity.this.scrollView.getScrollY();
                super.onLayout(changed, l, t, r, b);
                if (scrollY != LinkEditActivity.this.scrollView.getScrollY() && !LinkEditActivity.this.scrollToEnd) {
                    LinkEditActivity.this.scrollView.setTranslationY((float) (LinkEditActivity.this.scrollView.getScrollY() - scrollY));
                    LinkEditActivity.this.scrollView.animate().cancel();
                    LinkEditActivity.this.scrollView.animate().translationY(0.0f).setDuration(250).setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator).start();
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (LinkEditActivity.this.scrollToEnd) {
                    LinkEditActivity.this.scrollToEnd = false;
                    LinkEditActivity.this.scrollView.smoothScrollTo(0, Math.max(0, LinkEditActivity.this.scrollView.getChildAt(0).getMeasuredHeight() - LinkEditActivity.this.scrollView.getMeasuredHeight()));
                } else if (LinkEditActivity.this.scrollToStart) {
                    LinkEditActivity.this.scrollToStart = false;
                    LinkEditActivity.this.scrollView.smoothScrollTo(0, 0);
                }
            }
        };
        this.fragmentView = contentView;
        LinearLayout linearLayout = new LinearLayout(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int topMargin;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                int elementsHeight = 0;
                int h = View.MeasureSpec.getSize(heightMeasureSpec);
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    if (!(child == LinkEditActivity.this.buttonTextView || child.getVisibility() == 8)) {
                        elementsHeight += child.getMeasuredHeight();
                    }
                }
                int buttonH = AndroidUtilities.dp(48.0f) + AndroidUtilities.dp(24.0f) + AndroidUtilities.dp(16.0f);
                if (elementsHeight >= h - buttonH) {
                    topMargin = AndroidUtilities.dp(24.0f);
                } else {
                    topMargin = (AndroidUtilities.dp(24.0f) + (h - buttonH)) - elementsHeight;
                }
                if (((LinearLayout.LayoutParams) LinkEditActivity.this.buttonTextView.getLayoutParams()).topMargin != topMargin) {
                    int oldMargin = ((LinearLayout.LayoutParams) LinkEditActivity.this.buttonTextView.getLayoutParams()).topMargin;
                    ((LinearLayout.LayoutParams) LinkEditActivity.this.buttonTextView.getLayoutParams()).topMargin = topMargin;
                    if (!LinkEditActivity.this.firstLayout) {
                        LinkEditActivity.this.buttonTextView.setTranslationY((float) (oldMargin - topMargin));
                        LinkEditActivity.this.buttonTextView.animate().translationY(0.0f).setDuration(250).setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator).start();
                    }
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                boolean unused = LinkEditActivity.this.firstLayout = false;
            }
        };
        LayoutTransition transition = new LayoutTransition();
        transition.setDuration(100);
        linearLayout.setLayoutTransition(transition);
        linearLayout.setOrientation(1);
        this.scrollView.addView(linearLayout);
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
        AnonymousClass4 r5 = new TextCheckCell(context2) {
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
        this.approveCell.setOnClickListener(new LinkEditActivity$$ExternalSyntheticLambda4(this));
        linearLayout.addView(this.approveCell, LayoutHelper.createLinear(-1, 56));
        TextInfoPrivacyCell hintCell = new TextInfoPrivacyCell(context2);
        hintCell.setBackground(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        hintCell.setText(LocaleController.getString("ApproveNewMembersDescription", NUM));
        linearLayout.addView(hintCell);
        HeaderCell headerCell = new HeaderCell(context2);
        this.timeHeaderCell = headerCell;
        headerCell.setText(LocaleController.getString("LimitByPeriod", NUM));
        linearLayout.addView(this.timeHeaderCell);
        SlideChooseView slideChooseView = new SlideChooseView(context2);
        this.timeChooseView = slideChooseView;
        linearLayout.addView(slideChooseView);
        TextView textView3 = new TextView(context2);
        this.timeEditText = textView3;
        textView3.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
        this.timeEditText.setGravity(16);
        this.timeEditText.setTextSize(1, 16.0f);
        this.timeEditText.setHint(LocaleController.getString("TimeLimitHint", NUM));
        this.timeEditText.setOnClickListener(new LinkEditActivity$$ExternalSyntheticLambda7(this, context2));
        this.timeChooseView.setCallback(new LinkEditActivity$$ExternalSyntheticLambda2(this));
        resetDates();
        linearLayout.addView(this.timeEditText, LayoutHelper.createLinear(-1, 50));
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
        this.divider = textInfoPrivacyCell;
        textInfoPrivacyCell.setText(LocaleController.getString("TimeLimitHelp", NUM));
        linearLayout.addView(this.divider);
        HeaderCell headerCell2 = new HeaderCell(context2);
        this.usesHeaderCell = headerCell2;
        headerCell2.setText(LocaleController.getString("LimitNumberOfUses", NUM));
        linearLayout.addView(this.usesHeaderCell);
        SlideChooseView slideChooseView2 = new SlideChooseView(context2);
        this.usesChooseView = slideChooseView2;
        slideChooseView2.setCallback(new LinkEditActivity$$ExternalSyntheticLambda3(this));
        resetUses();
        linearLayout.addView(this.usesChooseView);
        AnonymousClass5 r3 = new EditText(context2) {
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == 1) {
                    setCursorVisible(true);
                }
                return super.onTouchEvent(event);
            }
        };
        this.usesEditText = r3;
        r3.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
        this.usesEditText.setGravity(16);
        this.usesEditText.setTextSize(1, 16.0f);
        this.usesEditText.setHint(LocaleController.getString("UsesLimitHint", NUM));
        this.usesEditText.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        this.usesEditText.setInputType(2);
        this.usesEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                if (!LinkEditActivity.this.ignoreSet) {
                    if (editable.toString().equals("0")) {
                        LinkEditActivity.this.usesEditText.setText("");
                        return;
                    }
                    try {
                        int customUses = Integer.parseInt(editable.toString());
                        if (customUses > 100000) {
                            LinkEditActivity.this.resetUses();
                        } else {
                            LinkEditActivity.this.chooseUses(customUses);
                        }
                    } catch (NumberFormatException e) {
                        LinkEditActivity.this.resetUses();
                    }
                }
            }
        });
        linearLayout.addView(this.usesEditText, LayoutHelper.createLinear(-1, 50));
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
        this.dividerUses = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setText(LocaleController.getString("UsesLimitHelp", NUM));
        linearLayout.addView(this.dividerUses);
        AnonymousClass7 r32 = new EditText(context2) {
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == 1) {
                    setCursorVisible(true);
                }
                return super.onTouchEvent(event);
            }
        };
        this.nameEditText = r32;
        r32.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                SpannableStringBuilder builder = new SpannableStringBuilder(s);
                Emoji.replaceEmoji(builder, LinkEditActivity.this.nameEditText.getPaint().getFontMetricsInt(), (int) LinkEditActivity.this.nameEditText.getPaint().getTextSize(), false);
                int selection = LinkEditActivity.this.nameEditText.getSelectionStart();
                LinkEditActivity.this.nameEditText.removeTextChangedListener(this);
                LinkEditActivity.this.nameEditText.setText(builder);
                if (selection >= 0) {
                    LinkEditActivity.this.nameEditText.setSelection(selection);
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
        linearLayout.addView(this.nameEditText, LayoutHelper.createLinear(-1, 50));
        TextInfoPrivacyCell textInfoPrivacyCell3 = new TextInfoPrivacyCell(context2);
        this.dividerName = textInfoPrivacyCell3;
        textInfoPrivacyCell3.setBackground(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        this.dividerName.setText(LocaleController.getString("LinkNameHelp", NUM));
        linearLayout.addView(this.dividerName);
        if (this.type == 1) {
            TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
            this.revokeLink = textSettingsCell;
            textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.revokeLink.setText(LocaleController.getString("RevokeLink", NUM), false);
            this.revokeLink.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
            this.revokeLink.setOnClickListener(new LinkEditActivity$$ExternalSyntheticLambda5(this));
            linearLayout.addView(this.revokeLink);
        }
        contentView.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        linearLayout.addView(this.buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 15.0f, 16.0f, 16.0f));
        this.timeHeaderCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.timeChooseView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.timeEditText.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.usesHeaderCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.usesChooseView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.usesEditText.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.nameEditText.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        contentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.buttonTextView.setOnClickListener(new LinkEditActivity$$ExternalSyntheticLambda6(this));
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
        contentView.setClipChildren(false);
        this.scrollView.setClipChildren(false);
        linearLayout.setClipChildren(false);
        return contentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-LinkEditActivity  reason: not valid java name */
    public /* synthetic */ void m3715lambda$createView$0$orgtelegramuiLinkEditActivity(View view) {
        TextCheckCell cell = (TextCheckCell) view;
        boolean newIsChecked = !cell.isChecked();
        cell.setBackgroundColorAnimated(newIsChecked, Theme.getColor(newIsChecked ? "windowBackgroundChecked" : "windowBackgroundUnchecked"));
        cell.setChecked(newIsChecked);
        setUsesVisible(!newIsChecked);
        this.firstLayout = true;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-LinkEditActivity  reason: not valid java name */
    public /* synthetic */ void m3716lambda$createView$1$orgtelegramuiLinkEditActivity(boolean notify, int scheduleDate) {
        chooseDate(scheduleDate);
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-LinkEditActivity  reason: not valid java name */
    public /* synthetic */ void m3717lambda$createView$2$orgtelegramuiLinkEditActivity(Context context, View view) {
        AlertsCreator.createDatePickerDialog(context, -1, new LinkEditActivity$$ExternalSyntheticLambda1(this));
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-LinkEditActivity  reason: not valid java name */
    public /* synthetic */ void m3718lambda$createView$3$orgtelegramuiLinkEditActivity(int index) {
        if (index < this.dispalyedDates.size()) {
            this.timeEditText.setText(LocaleController.formatDateAudio((long) (this.dispalyedDates.get(index).intValue() + getConnectionsManager().getCurrentTime()), false));
        } else {
            this.timeEditText.setText("");
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-LinkEditActivity  reason: not valid java name */
    public /* synthetic */ void m3719lambda$createView$4$orgtelegramuiLinkEditActivity(int index) {
        this.usesEditText.clearFocus();
        this.ignoreSet = true;
        if (index < this.dispalyedUses.size()) {
            this.usesEditText.setText(this.dispalyedUses.get(index).toString());
        } else {
            this.usesEditText.setText("");
        }
        this.ignoreSet = false;
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-LinkEditActivity  reason: not valid java name */
    public /* synthetic */ void m3721lambda$createView$6$orgtelegramuiLinkEditActivity(View view) {
        AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
        builder2.setMessage(LocaleController.getString("RevokeAlert", NUM));
        builder2.setTitle(LocaleController.getString("RevokeLink", NUM));
        builder2.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new LinkEditActivity$$ExternalSyntheticLambda0(this));
        builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder2.create());
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-LinkEditActivity  reason: not valid java name */
    public /* synthetic */ void m3720lambda$createView$5$orgtelegramuiLinkEditActivity(DialogInterface dialogInterface2, int i2) {
        this.callback.revokeLink(this.inviteToEdit);
        finishFragment();
    }

    /* access modifiers changed from: private */
    public void onCreateClicked(View view) {
        if (!this.loading) {
            int timeIndex = this.timeChooseView.getSelectedIndex();
            if (timeIndex >= this.dispalyedDates.size() || this.dispalyedDates.get(timeIndex).intValue() >= 0) {
                int i = this.type;
                if (i == 0) {
                    AlertDialog alertDialog = this.progressDialog;
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                    this.loading = true;
                    AlertDialog alertDialog2 = new AlertDialog(getParentActivity(), 3);
                    this.progressDialog = alertDialog2;
                    alertDialog2.showDelayed(500);
                    TLRPC.TL_messages_exportChatInvite req = new TLRPC.TL_messages_exportChatInvite();
                    req.peer = getMessagesController().getInputPeer(-this.chatId);
                    req.legacy_revoke_permanent = false;
                    int i2 = this.timeChooseView.getSelectedIndex();
                    req.flags |= 1;
                    if (i2 < this.dispalyedDates.size()) {
                        req.expire_date = this.dispalyedDates.get(i2).intValue() + getConnectionsManager().getCurrentTime();
                    } else {
                        req.expire_date = 0;
                    }
                    int i3 = this.usesChooseView.getSelectedIndex();
                    req.flags |= 2;
                    if (i3 < this.dispalyedUses.size()) {
                        req.usage_limit = this.dispalyedUses.get(i3).intValue();
                    } else {
                        req.usage_limit = 0;
                    }
                    req.request_needed = this.approveCell.isChecked();
                    if (req.request_needed) {
                        req.usage_limit = 0;
                    }
                    req.title = this.nameEditText.getText().toString();
                    if (!TextUtils.isEmpty(req.title)) {
                        req.flags |= 16;
                    }
                    getConnectionsManager().sendRequest(req, new LinkEditActivity$$ExternalSyntheticLambda11(this));
                } else if (i == 1) {
                    AlertDialog alertDialog3 = this.progressDialog;
                    if (alertDialog3 != null) {
                        alertDialog3.dismiss();
                    }
                    TLRPC.TL_messages_editExportedChatInvite req2 = new TLRPC.TL_messages_editExportedChatInvite();
                    req2.link = this.inviteToEdit.link;
                    req2.revoked = false;
                    req2.peer = getMessagesController().getInputPeer(-this.chatId);
                    boolean edited = false;
                    int i4 = this.timeChooseView.getSelectedIndex();
                    if (i4 < this.dispalyedDates.size()) {
                        if (this.currentInviteDate != this.dispalyedDates.get(i4).intValue()) {
                            req2.flags |= 1;
                            req2.expire_date = this.dispalyedDates.get(i4).intValue() + getConnectionsManager().getCurrentTime();
                            edited = true;
                        }
                    } else if (this.currentInviteDate != 0) {
                        req2.flags |= 1;
                        req2.expire_date = 0;
                        edited = true;
                    }
                    int i5 = this.usesChooseView.getSelectedIndex();
                    if (i5 < this.dispalyedUses.size()) {
                        int newLimit = this.dispalyedUses.get(i5).intValue();
                        if (this.inviteToEdit.usage_limit != newLimit) {
                            req2.flags |= 2;
                            req2.usage_limit = newLimit;
                            edited = true;
                        }
                    } else if (this.inviteToEdit.usage_limit != 0) {
                        req2.flags |= 2;
                        req2.usage_limit = 0;
                        edited = true;
                    }
                    if (this.inviteToEdit.request_needed != this.approveCell.isChecked()) {
                        req2.flags |= 8;
                        req2.request_needed = this.approveCell.isChecked();
                        if (req2.request_needed) {
                            req2.flags |= 2;
                            req2.usage_limit = 0;
                        }
                        edited = true;
                    }
                    String newTitle = this.nameEditText.getText().toString();
                    if (!TextUtils.equals(this.inviteToEdit.title, newTitle)) {
                        req2.title = newTitle;
                        req2.flags |= 16;
                        edited = true;
                    }
                    if (edited) {
                        this.loading = true;
                        AlertDialog alertDialog4 = new AlertDialog(getParentActivity(), 3);
                        this.progressDialog = alertDialog4;
                        alertDialog4.showDelayed(500);
                        getConnectionsManager().sendRequest(req2, new LinkEditActivity$$ExternalSyntheticLambda10(this));
                        return;
                    }
                    finishFragment();
                }
            } else {
                AndroidUtilities.shakeView(this.timeEditText, 2.0f, 0);
                Vibrator vibrator = (Vibrator) this.timeEditText.getContext().getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
            }
        }
    }

    /* renamed from: lambda$onCreateClicked$8$org-telegram-ui-LinkEditActivity  reason: not valid java name */
    public /* synthetic */ void m3725lambda$onCreateClicked$8$orgtelegramuiLinkEditActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LinkEditActivity$$ExternalSyntheticLambda8(this, error, response));
    }

    /* renamed from: lambda$onCreateClicked$7$org-telegram-ui-LinkEditActivity  reason: not valid java name */
    public /* synthetic */ void m3724lambda$onCreateClicked$7$orgtelegramuiLinkEditActivity(TLRPC.TL_error error, TLObject response) {
        this.loading = false;
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        if (error == null) {
            Callback callback2 = this.callback;
            if (callback2 != null) {
                callback2.onLinkCreated(response);
            }
            finishFragment();
            return;
        }
        AlertsCreator.showSimpleAlert(this, error.text);
    }

    /* renamed from: lambda$onCreateClicked$10$org-telegram-ui-LinkEditActivity  reason: not valid java name */
    public /* synthetic */ void m3723lambda$onCreateClicked$10$orgtelegramuiLinkEditActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LinkEditActivity$$ExternalSyntheticLambda9(this, error, response));
    }

    /* renamed from: lambda$onCreateClicked$9$org-telegram-ui-LinkEditActivity  reason: not valid java name */
    public /* synthetic */ void m3726lambda$onCreateClicked$9$orgtelegramuiLinkEditActivity(TLRPC.TL_error error, TLObject response) {
        this.loading = false;
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        if (error == null) {
            if (response instanceof TLRPC.TL_messages_exportedChatInvite) {
                this.inviteToEdit = (TLRPC.TL_chatInviteExported) ((TLRPC.TL_messages_exportedChatInvite) response).invite;
            }
            Callback callback2 = this.callback;
            if (callback2 != null) {
                callback2.onLinkEdited(this.inviteToEdit, response);
            }
            finishFragment();
            return;
        }
        AlertsCreator.showSimpleAlert(this, error.text);
    }

    /* access modifiers changed from: private */
    public void chooseUses(int customUses) {
        int position = 0;
        boolean added = false;
        this.dispalyedUses.clear();
        int i = 0;
        while (true) {
            int[] iArr = this.defaultUses;
            if (i >= iArr.length) {
                break;
            }
            if (!added && customUses <= iArr[i]) {
                if (customUses != iArr[i]) {
                    this.dispalyedUses.add(Integer.valueOf(customUses));
                }
                position = i;
                added = true;
            }
            this.dispalyedUses.add(Integer.valueOf(this.defaultUses[i]));
            i++;
        }
        if (!added) {
            this.dispalyedUses.add(Integer.valueOf(customUses));
            position = this.defaultUses.length;
        }
        String[] options = new String[(this.dispalyedUses.size() + 1)];
        for (int i2 = 0; i2 < options.length; i2++) {
            if (i2 == options.length - 1) {
                options[i2] = LocaleController.getString("NoLimit", NUM);
            } else {
                options[i2] = this.dispalyedUses.get(i2).toString();
            }
        }
        this.usesChooseView.setOptions(position, options);
    }

    private void chooseDate(int selectedDate) {
        this.timeEditText.setText(LocaleController.formatDateAudio((long) selectedDate, false));
        int originDate = selectedDate;
        int selectedDate2 = selectedDate - getConnectionsManager().getCurrentTime();
        int position = 0;
        boolean added = false;
        this.dispalyedDates.clear();
        int i = 0;
        while (true) {
            int[] iArr = this.defaultDates;
            if (i >= iArr.length) {
                break;
            }
            if (!added && selectedDate2 < iArr[i]) {
                this.dispalyedDates.add(Integer.valueOf(selectedDate2));
                position = i;
                added = true;
            }
            this.dispalyedDates.add(Integer.valueOf(this.defaultDates[i]));
            i++;
        }
        if (!added) {
            this.dispalyedDates.add(Integer.valueOf(selectedDate2));
            position = this.defaultDates.length;
        }
        String[] options = new String[(this.dispalyedDates.size() + 1)];
        for (int i2 = 0; i2 < options.length; i2++) {
            if (i2 == options.length - 1) {
                options[i2] = LocaleController.getString("NoLimit", NUM);
            } else if (this.dispalyedDates.get(i2).intValue() == this.defaultDates[0]) {
                options[i2] = LocaleController.formatPluralString("Hours", 1, new Object[0]);
            } else if (this.dispalyedDates.get(i2).intValue() == this.defaultDates[1]) {
                options[i2] = LocaleController.formatPluralString("Days", 1, new Object[0]);
            } else if (this.dispalyedDates.get(i2).intValue() == this.defaultDates[2]) {
                options[i2] = LocaleController.formatPluralString("Weeks", 1, new Object[0]);
            } else if (((long) selectedDate2) < 86400) {
                options[i2] = LocaleController.getString("MessageScheduleToday", NUM);
            } else if (((long) selectedDate2) < 31449600) {
                options[i2] = LocaleController.getInstance().formatterScheduleDay.format(((long) originDate) * 1000);
            } else {
                options[i2] = LocaleController.getInstance().formatterYear.format(((long) originDate) * 1000);
            }
        }
        this.timeChooseView.setOptions(position, options);
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
                String[] options = {LocaleController.formatPluralString("Hours", 1, new Object[0]), LocaleController.formatPluralString("Days", 1, new Object[0]), LocaleController.formatPluralString("Weeks", 1, new Object[0]), LocaleController.getString("NoLimit", NUM)};
                this.timeChooseView.setOptions(options.length - 1, options);
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
                String[] options = {"1", "10", "100", LocaleController.getString("NoLimit", NUM)};
                this.usesChooseView.setOptions(options.length - 1, options);
                return;
            }
        }
    }

    public void setInviteToEdit(TLRPC.TL_chatInviteExported invite) {
        this.inviteToEdit = invite;
        if (this.fragmentView != null && invite != null) {
            if (invite.expire_date > 0) {
                chooseDate(invite.expire_date);
                this.currentInviteDate = this.dispalyedDates.get(this.timeChooseView.getSelectedIndex()).intValue();
            } else {
                this.currentInviteDate = 0;
            }
            if (invite.usage_limit > 0) {
                chooseUses(invite.usage_limit);
                this.usesEditText.setText(Integer.toString(invite.usage_limit));
            }
            this.approveCell.setBackgroundColor(Theme.getColor(invite.request_needed ? "windowBackgroundChecked" : "windowBackgroundUnchecked"));
            this.approveCell.setChecked(invite.request_needed);
            setUsesVisible(!invite.request_needed);
            if (!TextUtils.isEmpty(invite.title)) {
                SpannableStringBuilder builder = new SpannableStringBuilder(invite.title);
                Emoji.replaceEmoji(builder, this.nameEditText.getPaint().getFontMetricsInt(), (int) this.nameEditText.getPaint().getTextSize(), false);
                this.nameEditText.setText(builder);
            }
        }
    }

    private void setUsesVisible(boolean isVisible) {
        int i = 0;
        this.usesHeaderCell.setVisibility(isVisible ? 0 : 8);
        this.usesChooseView.setVisibility(isVisible ? 0 : 8);
        this.usesEditText.setVisibility(isVisible ? 0 : 8);
        TextInfoPrivacyCell textInfoPrivacyCell = this.dividerUses;
        if (!isVisible) {
            i = 8;
        }
        textInfoPrivacyCell.setVisibility(i);
        this.divider.setBackground(Theme.getThemedDrawable((Context) getParentActivity(), isVisible ? NUM : NUM, "windowBackgroundGrayShadow"));
    }

    public void finishFragment() {
        this.scrollView.getLayoutParams().height = this.scrollView.getHeight();
        this.finished = true;
        super.finishFragment();
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate descriptionDelegate = new LinkEditActivity$$ExternalSyntheticLambda12(this);
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription((View) this.timeHeaderCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.usesHeaderCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription(this.timeHeaderCell, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.usesHeaderCell, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.timeChooseView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.usesChooseView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.timeEditText, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.usesEditText, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.revokeLink, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription((View) this.divider, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.dividerUses, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.dividerName, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, descriptionDelegate, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, descriptionDelegate, "featuredStickers_addButton"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, descriptionDelegate, "featuredStickers_addButtonPressed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = descriptionDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "windowBackgroundWhiteBlackText"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = descriptionDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "featuredStickers_buttonText"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "windowBackgroundWhiteRedText5"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$11$org-telegram-ui-LinkEditActivity  reason: not valid java name */
    public /* synthetic */ void m3722lambda$getThemeDescriptions$11$orgtelegramuiLinkEditActivity() {
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
