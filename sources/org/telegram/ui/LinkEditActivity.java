package org.telegram.ui;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
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
    int currentInviteDate;
    private final int[] defaultDates = {3600, 86400, 604800};
    private final int[] defaultUses = {1, 10, 100};
    private ArrayList<Integer> dispalyedDates = new ArrayList<>();
    private ArrayList<Integer> dispalyedUses = new ArrayList<>();
    private TextInfoPrivacyCell divider;
    private TextInfoPrivacyCell dividerUses;
    /* access modifiers changed from: private */
    public boolean finished;
    /* access modifiers changed from: private */
    public boolean firstLayout = true;
    /* access modifiers changed from: private */
    public boolean ignoreSet;
    TLRPC$TL_chatInviteExported inviteToEdit;
    boolean loading;
    AlertDialog progressDialog;
    private TextSettingsCell revokeLink;
    boolean scrollToEnd;
    /* access modifiers changed from: private */
    public ScrollView scrollView;
    private SlideChooseView timeChooseView;
    private TextView timeEditText;
    private HeaderCell timeHeaderCell;
    private int type;
    private SlideChooseView usesChooseView;
    /* access modifiers changed from: private */
    public EditText usesEditText;
    /* access modifiers changed from: private */
    public boolean usesHasFocus;
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
                int i3 = this.oldKeyboardHeight;
                int i4 = this.keyboardHeight;
                if (i3 != i4 && i4 > AndroidUtilities.dp(20.0f) && LinkEditActivity.this.usesHasFocus) {
                    LinkEditActivity.this.scrollToEnd = true;
                    invalidate();
                }
                if (this.keyboardHeight < AndroidUtilities.dp(20.0f)) {
                    LinkEditActivity.this.usesEditText.clearFocus();
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
                }
            }
        };
        this.fragmentView = r2;
        AnonymousClass3 r4 = new LinearLayout(context2) {
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
        r4.setLayoutTransition(layoutTransition);
        r4.setOrientation(1);
        this.scrollView.addView(r4);
        TextView textView = new TextView(context2);
        this.buttonTextView = textView;
        textView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        int i2 = this.type;
        if (i2 == 0) {
            this.buttonTextView.setText(LocaleController.getString("CreateLink", NUM));
        } else if (i2 == 1) {
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
        r4.addView(this.approveCell, LayoutHelper.createLinear(-1, 56));
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
        textInfoPrivacyCell.setBackground(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        textInfoPrivacyCell.setText(LocaleController.getString("ApproveNewMembersDescription", NUM));
        r4.addView(textInfoPrivacyCell);
        HeaderCell headerCell = new HeaderCell(context2);
        this.timeHeaderCell = headerCell;
        headerCell.setText(LocaleController.getString("LimitByPeriod", NUM));
        r4.addView(this.timeHeaderCell);
        SlideChooseView slideChooseView = new SlideChooseView(context2);
        this.timeChooseView = slideChooseView;
        r4.addView(slideChooseView);
        TextView textView2 = new TextView(context2);
        this.timeEditText = textView2;
        textView2.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
        this.timeEditText.setGravity(16);
        this.timeEditText.setTextSize(1, 16.0f);
        this.timeEditText.setHint(LocaleController.getString("TimeLimitHint", NUM));
        this.timeEditText.setOnClickListener(new LinkEditActivity$$ExternalSyntheticLambda4(this, context2));
        this.timeChooseView.setCallback(new LinkEditActivity$$ExternalSyntheticLambda12(this));
        resetDates();
        r4.addView(this.timeEditText, LayoutHelper.createLinear(-1, 50));
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
        this.divider = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setText(LocaleController.getString("TimeLimitHelp", NUM));
        r4.addView(this.divider);
        HeaderCell headerCell2 = new HeaderCell(context2);
        this.usesHeaderCell = headerCell2;
        headerCell2.setText(LocaleController.getString("LimitNumberOfUses", NUM));
        r4.addView(this.usesHeaderCell);
        SlideChooseView slideChooseView2 = new SlideChooseView(context2);
        this.usesChooseView = slideChooseView2;
        slideChooseView2.setCallback(new LinkEditActivity$$ExternalSyntheticLambda13(this));
        resetUses();
        r4.addView(this.usesChooseView);
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
        this.usesEditText.setOnFocusChangeListener(new LinkEditActivity$$ExternalSyntheticLambda5(this));
        r4.addView(this.usesEditText, LayoutHelper.createLinear(-1, 50));
        TextInfoPrivacyCell textInfoPrivacyCell3 = new TextInfoPrivacyCell(context2);
        this.dividerUses = textInfoPrivacyCell3;
        textInfoPrivacyCell3.setText(LocaleController.getString("UsesLimitHelp", NUM));
        r4.addView(this.dividerUses);
        if (this.type == 1) {
            TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
            this.revokeLink = textSettingsCell;
            textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.revokeLink.setText(LocaleController.getString("RevokeLink", NUM), false);
            this.revokeLink.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
            this.revokeLink.setOnClickListener(new LinkEditActivity$$ExternalSyntheticLambda2(this));
            r4.addView(this.revokeLink);
        }
        r2.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        r4.addView(this.buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 15.0f, 16.0f, 16.0f));
        this.timeHeaderCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.timeChooseView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.timeEditText.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.usesHeaderCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.usesChooseView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.usesEditText.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        r2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.buttonTextView.setOnClickListener(new LinkEditActivity$$ExternalSyntheticLambda3(this, context2));
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
        r4.setClipChildren(false);
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
        AlertsCreator.createDatePickerDialog(context, -1, new LinkEditActivity$$ExternalSyntheticLambda11(this));
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
    public /* synthetic */ void lambda$createView$5(View view, boolean z) {
        this.usesHasFocus = z;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$7(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setMessage(LocaleController.getString("RevokeAlert", NUM));
        builder.setTitle(LocaleController.getString("RevokeLink", NUM));
        builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new LinkEditActivity$$ExternalSyntheticLambda0(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(DialogInterface dialogInterface, int i) {
        this.callback.revokeLink(this.inviteToEdit);
        finishFragment();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0154  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x016f  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x01a5  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x01be  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$12(android.content.Context r9, android.view.View r10) {
        /*
            r8 = this;
            boolean r10 = r8.loading
            if (r10 == 0) goto L_0x0005
            return
        L_0x0005:
            org.telegram.ui.Components.SlideChooseView r10 = r8.timeChooseView
            int r10 = r10.getSelectedIndex()
            java.util.ArrayList<java.lang.Integer> r0 = r8.dispalyedDates
            int r0 = r0.size()
            r1 = 0
            if (r10 >= r0) goto L_0x003f
            java.util.ArrayList<java.lang.Integer> r0 = r8.dispalyedDates
            java.lang.Object r10 = r0.get(r10)
            java.lang.Integer r10 = (java.lang.Integer) r10
            int r10 = r10.intValue()
            if (r10 >= 0) goto L_0x003f
            android.widget.TextView r9 = r8.timeEditText
            r10 = 1073741824(0x40000000, float:2.0)
            org.telegram.messenger.AndroidUtilities.shakeView(r9, r10, r1)
            android.widget.TextView r9 = r8.timeEditText
            android.content.Context r9 = r9.getContext()
            java.lang.String r10 = "vibrator"
            java.lang.Object r9 = r9.getSystemService(r10)
            android.os.Vibrator r9 = (android.os.Vibrator) r9
            if (r9 == 0) goto L_0x003e
            r0 = 200(0xc8, double:9.9E-322)
            r9.vibrate(r0)
        L_0x003e:
            return
        L_0x003f:
            int r10 = r8.type
            r2 = 500(0x1f4, double:2.47E-321)
            r0 = 3
            r4 = 1
            if (r10 != 0) goto L_0x00da
            org.telegram.ui.ActionBar.AlertDialog r10 = r8.progressDialog
            if (r10 == 0) goto L_0x004e
            r10.dismiss()
        L_0x004e:
            r8.loading = r4
            org.telegram.ui.ActionBar.AlertDialog r10 = new org.telegram.ui.ActionBar.AlertDialog
            r10.<init>(r9, r0)
            r8.progressDialog = r10
            r10.showDelayed(r2)
            org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite r9 = new org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite
            r9.<init>()
            org.telegram.messenger.MessagesController r10 = r8.getMessagesController()
            long r2 = r8.chatId
            long r2 = -r2
            org.telegram.tgnet.TLRPC$InputPeer r10 = r10.getInputPeer((long) r2)
            r9.peer = r10
            r9.legacy_revoke_permanent = r1
            org.telegram.ui.Components.SlideChooseView r10 = r8.timeChooseView
            int r10 = r10.getSelectedIndex()
            int r0 = r9.flags
            r0 = r0 | r4
            r9.flags = r0
            java.util.ArrayList<java.lang.Integer> r0 = r8.dispalyedDates
            int r0 = r0.size()
            if (r10 >= r0) goto L_0x0099
            java.util.ArrayList<java.lang.Integer> r0 = r8.dispalyedDates
            java.lang.Object r10 = r0.get(r10)
            java.lang.Integer r10 = (java.lang.Integer) r10
            int r10 = r10.intValue()
            org.telegram.tgnet.ConnectionsManager r0 = r8.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            int r10 = r10 + r0
            r9.expire_date = r10
            goto L_0x009b
        L_0x0099:
            r9.expire_date = r1
        L_0x009b:
            org.telegram.ui.Components.SlideChooseView r10 = r8.usesChooseView
            int r10 = r10.getSelectedIndex()
            int r0 = r9.flags
            r0 = r0 | 2
            r9.flags = r0
            java.util.ArrayList<java.lang.Integer> r0 = r8.dispalyedUses
            int r0 = r0.size()
            if (r10 >= r0) goto L_0x00be
            java.util.ArrayList<java.lang.Integer> r0 = r8.dispalyedUses
            java.lang.Object r10 = r0.get(r10)
            java.lang.Integer r10 = (java.lang.Integer) r10
            int r10 = r10.intValue()
            r9.usage_limit = r10
            goto L_0x00c0
        L_0x00be:
            r9.usage_limit = r1
        L_0x00c0:
            org.telegram.ui.Cells.TextCheckCell r10 = r8.approveCell
            boolean r10 = r10.isChecked()
            r9.request_needed = r10
            if (r10 == 0) goto L_0x00cc
            r9.usage_limit = r1
        L_0x00cc:
            org.telegram.tgnet.ConnectionsManager r10 = r8.getConnectionsManager()
            org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda8 r0 = new org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda8
            r0.<init>(r8)
            r10.sendRequest(r9, r0)
            goto L_0x01c1
        L_0x00da:
            if (r10 != r4) goto L_0x01c1
            org.telegram.ui.ActionBar.AlertDialog r10 = r8.progressDialog
            if (r10 == 0) goto L_0x00e3
            r10.dismiss()
        L_0x00e3:
            org.telegram.tgnet.TLRPC$TL_messages_editExportedChatInvite r10 = new org.telegram.tgnet.TLRPC$TL_messages_editExportedChatInvite
            r10.<init>()
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r5 = r8.inviteToEdit
            java.lang.String r5 = r5.link
            r10.link = r5
            r10.revoked = r1
            org.telegram.messenger.MessagesController r5 = r8.getMessagesController()
            long r6 = r8.chatId
            long r6 = -r6
            org.telegram.tgnet.TLRPC$InputPeer r5 = r5.getInputPeer((long) r6)
            r10.peer = r5
            org.telegram.ui.Components.SlideChooseView r5 = r8.timeChooseView
            int r5 = r5.getSelectedIndex()
            java.util.ArrayList<java.lang.Integer> r6 = r8.dispalyedDates
            int r6 = r6.size()
            if (r5 >= r6) goto L_0x0138
            int r6 = r8.currentInviteDate
            java.util.ArrayList<java.lang.Integer> r7 = r8.dispalyedDates
            java.lang.Object r7 = r7.get(r5)
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r7 = r7.intValue()
            if (r6 == r7) goto L_0x0145
            int r6 = r10.flags
            r6 = r6 | r4
            r10.flags = r6
            java.util.ArrayList<java.lang.Integer> r6 = r8.dispalyedDates
            java.lang.Object r5 = r6.get(r5)
            java.lang.Integer r5 = (java.lang.Integer) r5
            int r5 = r5.intValue()
            org.telegram.tgnet.ConnectionsManager r6 = r8.getConnectionsManager()
            int r6 = r6.getCurrentTime()
            int r5 = r5 + r6
            r10.expire_date = r5
            goto L_0x0143
        L_0x0138:
            int r5 = r8.currentInviteDate
            if (r5 == 0) goto L_0x0145
            int r5 = r10.flags
            r5 = r5 | r4
            r10.flags = r5
            r10.expire_date = r1
        L_0x0143:
            r5 = 1
            goto L_0x0146
        L_0x0145:
            r5 = 0
        L_0x0146:
            org.telegram.ui.Components.SlideChooseView r6 = r8.usesChooseView
            int r6 = r6.getSelectedIndex()
            java.util.ArrayList<java.lang.Integer> r7 = r8.dispalyedUses
            int r7 = r7.size()
            if (r6 >= r7) goto L_0x016f
            java.util.ArrayList<java.lang.Integer> r7 = r8.dispalyedUses
            java.lang.Object r6 = r7.get(r6)
            java.lang.Integer r6 = (java.lang.Integer) r6
            int r6 = r6.intValue()
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r7 = r8.inviteToEdit
            int r7 = r7.usage_limit
            if (r7 == r6) goto L_0x017e
            int r5 = r10.flags
            r5 = r5 | 2
            r10.flags = r5
            r10.usage_limit = r6
            goto L_0x017d
        L_0x016f:
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r6 = r8.inviteToEdit
            int r6 = r6.usage_limit
            if (r6 == 0) goto L_0x017e
            int r5 = r10.flags
            r5 = r5 | 2
            r10.flags = r5
            r10.usage_limit = r1
        L_0x017d:
            r5 = 1
        L_0x017e:
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r6 = r8.inviteToEdit
            boolean r6 = r6.request_needed
            org.telegram.ui.Cells.TextCheckCell r7 = r8.approveCell
            boolean r7 = r7.isChecked()
            if (r6 == r7) goto L_0x01a3
            int r5 = r10.flags
            r5 = r5 | 8
            r10.flags = r5
            org.telegram.ui.Cells.TextCheckCell r5 = r8.approveCell
            boolean r5 = r5.isChecked()
            r10.request_needed = r5
            if (r5 == 0) goto L_0x01a2
            int r5 = r10.flags
            r5 = r5 | 2
            r10.flags = r5
            r10.usage_limit = r1
        L_0x01a2:
            r5 = 1
        L_0x01a3:
            if (r5 == 0) goto L_0x01be
            r8.loading = r4
            org.telegram.ui.ActionBar.AlertDialog r1 = new org.telegram.ui.ActionBar.AlertDialog
            r1.<init>(r9, r0)
            r8.progressDialog = r1
            r1.showDelayed(r2)
            org.telegram.tgnet.ConnectionsManager r9 = r8.getConnectionsManager()
            org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda9 r0 = new org.telegram.ui.LinkEditActivity$$ExternalSyntheticLambda9
            r0.<init>(r8)
            r9.sendRequest(r10, r0)
            goto L_0x01c1
        L_0x01be:
            r8.finishFragment()
        L_0x01c1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LinkEditActivity.lambda$createView$12(android.content.Context, android.view.View):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$9(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LinkEditActivity$$ExternalSyntheticLambda6(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$8(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
    public /* synthetic */ void lambda$createView$11(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LinkEditActivity$$ExternalSyntheticLambda7(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$10(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
        LinkEditActivity$$ExternalSyntheticLambda10 linkEditActivity$$ExternalSyntheticLambda10 = new LinkEditActivity$$ExternalSyntheticLambda10(this);
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
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        LinkEditActivity$$ExternalSyntheticLambda10 linkEditActivity$$ExternalSyntheticLambda102 = linkEditActivity$$ExternalSyntheticLambda10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, linkEditActivity$$ExternalSyntheticLambda102, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, linkEditActivity$$ExternalSyntheticLambda102, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, linkEditActivity$$ExternalSyntheticLambda102, "featuredStickers_addButtonPressed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, linkEditActivity$$ExternalSyntheticLambda102, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, linkEditActivity$$ExternalSyntheticLambda102, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, linkEditActivity$$ExternalSyntheticLambda102, "featuredStickers_buttonText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, linkEditActivity$$ExternalSyntheticLambda102, "windowBackgroundWhiteRedText5"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$13() {
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
        }
    }
}
