package org.telegram.ui;

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
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.SlideChooseView;

public class LinkEditActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public TextView buttonTextView;
    private Callback callback;
    private final int chatId;
    int currentInviteDate;
    private final int[] defaultDates = {3600, 86400, 604800};
    private final int[] defaultUses = {1, 10, 100};
    private ArrayList<Integer> dispalyedDates = new ArrayList<>();
    private ArrayList<Integer> dispalyedUses = new ArrayList<>();
    /* access modifiers changed from: private */
    public TextInfoPrivacyCell divider;
    /* access modifiers changed from: private */
    public TextInfoPrivacyCell dividerUses;
    /* access modifiers changed from: private */
    public boolean finished;
    /* access modifiers changed from: private */
    public boolean ignoreSet;
    TLRPC$TL_chatInviteExported inviteToEdit;
    boolean loading;
    AlertDialog progressDialog;
    /* access modifiers changed from: private */
    public TextSettingsCell revokeLink;
    boolean scrollToEnd;
    /* access modifiers changed from: private */
    public ScrollView scrollView;
    private SlideChooseView timeChooseView;
    /* access modifiers changed from: private */
    public TextView timeEditText;
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

    public LinkEditActivity(int i, int i2) {
        this.type = i;
        this.chatId = i2;
    }

    public View createView(Context context) {
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
        this.scrollView = new ScrollView(context);
        AnonymousClass2 r0 = new SizeNotifierFrameLayout(context) {
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
                if (i3 != i4 && i4 > AndroidUtilities.dp(20.0f)) {
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
        this.fragmentView = r0;
        AnonymousClass3 r2 = new LinearLayout(context) {
            boolean firstLayout = true;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int i3;
                super.onMeasure(i, i2);
                int size = View.MeasureSpec.getSize(i2);
                int i4 = 0;
                for (int i5 = 0; i5 < getChildCount(); i5++) {
                    View childAt = getChildAt(i5);
                    if (childAt != LinkEditActivity.this.buttonTextView) {
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
                    if (!this.firstLayout) {
                        LinkEditActivity.this.buttonTextView.setTranslationY((float) (i6 - i3));
                        LinkEditActivity.this.buttonTextView.animate().translationY(0.0f).setDuration(250).setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator).start();
                    }
                    super.onMeasure(i, i2);
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                this.firstLayout = false;
            }
        };
        r2.setOrientation(1);
        this.scrollView.addView(r2);
        TextView textView = new TextView(context);
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
        HeaderCell headerCell = new HeaderCell(context);
        this.timeHeaderCell = headerCell;
        headerCell.setText(LocaleController.getString("LimitByPeriod", NUM));
        r2.addView(this.timeHeaderCell);
        SlideChooseView slideChooseView = new SlideChooseView(context);
        this.timeChooseView = slideChooseView;
        r2.addView(slideChooseView);
        TextView textView2 = new TextView(context);
        this.timeEditText = textView2;
        textView2.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
        this.timeEditText.setGravity(16);
        this.timeEditText.setTextSize(1, 16.0f);
        this.timeEditText.setHint(LocaleController.getString("TimeLimitHint", NUM));
        this.timeEditText.setOnClickListener(new View.OnClickListener(context) {
            public final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                LinkEditActivity.this.lambda$createView$1$LinkEditActivity(this.f$1, view);
            }
        });
        this.timeChooseView.setCallback(new SlideChooseView.Callback() {
            public final void onOptionSelected(int i) {
                LinkEditActivity.this.lambda$createView$2$LinkEditActivity(i);
            }

            public /* synthetic */ void onTouchEnd() {
                SlideChooseView.Callback.CC.$default$onTouchEnd(this);
            }
        });
        resetDates();
        r2.addView(this.timeEditText, LayoutHelper.createLinear(-1, 50));
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
        this.divider = textInfoPrivacyCell;
        textInfoPrivacyCell.setText(LocaleController.getString("TimeLimitHelp", NUM));
        r2.addView(this.divider);
        HeaderCell headerCell2 = new HeaderCell(context);
        this.usesHeaderCell = headerCell2;
        headerCell2.setText(LocaleController.getString("LimitNumberOfUses", NUM));
        r2.addView(this.usesHeaderCell);
        SlideChooseView slideChooseView2 = new SlideChooseView(context);
        this.usesChooseView = slideChooseView2;
        slideChooseView2.setCallback(new SlideChooseView.Callback() {
            public final void onOptionSelected(int i) {
                LinkEditActivity.this.lambda$createView$3$LinkEditActivity(i);
            }

            public /* synthetic */ void onTouchEnd() {
                SlideChooseView.Callback.CC.$default$onTouchEnd(this);
            }
        });
        resetUses();
        r2.addView(this.usesChooseView);
        AnonymousClass4 r3 = new EditText(this, context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 1) {
                    setCursorVisible(true);
                }
                return super.onTouchEvent(motionEvent);
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
        r2.addView(this.usesEditText, LayoutHelper.createLinear(-1, 50));
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
        this.dividerUses = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setText(LocaleController.getString("UsesLimitHelp", NUM));
        r2.addView(this.dividerUses);
        if (this.type == 1) {
            TextSettingsCell textSettingsCell = new TextSettingsCell(context);
            this.revokeLink = textSettingsCell;
            textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.revokeLink.setText(LocaleController.getString("RevokeLink", NUM), false);
            this.revokeLink.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
            this.revokeLink.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    LinkEditActivity.this.lambda$createView$5$LinkEditActivity(view);
                }
            });
            r2.addView(this.revokeLink);
        }
        r0.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        r2.addView(this.buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 15.0f, 16.0f, 16.0f));
        this.timeHeaderCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.timeChooseView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.timeEditText.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.usesHeaderCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.usesChooseView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.usesEditText.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        r0.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.buttonTextView.setOnClickListener(new View.OnClickListener(context) {
            public final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                LinkEditActivity.this.lambda$createView$10$LinkEditActivity(this.f$1, view);
            }
        });
        this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.dividerUses.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
        this.divider.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
        this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        this.usesEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.usesEditText.setHintTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        this.timeEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.timeEditText.setHintTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        this.usesEditText.setCursorVisible(false);
        setInviteToEdit(this.inviteToEdit);
        r0.setClipChildren(false);
        this.scrollView.setClipChildren(false);
        r2.setClipChildren(false);
        return r0;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$1 */
    public /* synthetic */ void lambda$createView$1$LinkEditActivity(Context context, View view) {
        AlertsCreator.createDatePickerDialog(context, -1, new AlertsCreator.ScheduleDatePickerDelegate() {
            public final void didSelectDate(boolean z, int i) {
                LinkEditActivity.this.lambda$null$0$LinkEditActivity(z, i);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$0 */
    public /* synthetic */ void lambda$null$0$LinkEditActivity(boolean z, int i) {
        chooseDate(i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$2 */
    public /* synthetic */ void lambda$createView$2$LinkEditActivity(int i) {
        if (i < this.dispalyedDates.size()) {
            this.timeEditText.setText(LocaleController.formatDateAudio((long) (this.dispalyedDates.get(i).intValue() + getConnectionsManager().getCurrentTime()), false));
            return;
        }
        this.timeEditText.setText("");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$3 */
    public /* synthetic */ void lambda$createView$3$LinkEditActivity(int i) {
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
    /* renamed from: lambda$createView$5 */
    public /* synthetic */ void lambda$createView$5$LinkEditActivity(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setMessage(LocaleController.getString("RevokeAlert", NUM));
        builder.setTitle(LocaleController.getString("RevokeLink", NUM));
        builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                LinkEditActivity.this.lambda$null$4$LinkEditActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$4 */
    public /* synthetic */ void lambda$null$4$LinkEditActivity(DialogInterface dialogInterface, int i) {
        this.callback.revokeLink(this.inviteToEdit);
        finishFragment();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0155  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0170  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0182  */
    /* JADX WARNING: Removed duplicated region for block: B:54:? A[RETURN, SYNTHETIC] */
    /* renamed from: lambda$createView$10 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$10$LinkEditActivity(android.content.Context r6, android.view.View r7) {
        /*
            r5 = this;
            boolean r7 = r5.loading
            if (r7 == 0) goto L_0x0005
            return
        L_0x0005:
            org.telegram.ui.Components.SlideChooseView r7 = r5.timeChooseView
            int r7 = r7.getSelectedIndex()
            java.util.ArrayList<java.lang.Integer> r0 = r5.dispalyedDates
            int r0 = r0.size()
            r1 = 0
            if (r7 >= r0) goto L_0x0040
            java.util.ArrayList<java.lang.Integer> r0 = r5.dispalyedDates
            java.lang.Object r7 = r0.get(r7)
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r7 = r7.intValue()
            if (r7 >= 0) goto L_0x0040
            android.widget.TextView r6 = r5.timeEditText
            r7 = 1073741824(0x40000000, float:2.0)
            org.telegram.messenger.AndroidUtilities.shakeView(r6, r7, r1)
            android.widget.TextView r6 = r5.timeEditText
            android.content.Context r6 = r6.getContext()
            java.lang.String r7 = "vibrator"
            java.lang.Object r6 = r6.getSystemService(r7)
            android.os.Vibrator r6 = (android.os.Vibrator) r6
            if (r6 == 0) goto L_0x003f
            r0 = 200(0xc8, double:9.9E-322)
            r6.vibrate(r0)
        L_0x003f:
            return
        L_0x0040:
            int r7 = r5.type
            r2 = 500(0x1f4, double:2.47E-321)
            r0 = 3
            r4 = 1
            if (r7 != 0) goto L_0x00cf
            org.telegram.ui.ActionBar.AlertDialog r7 = r5.progressDialog
            if (r7 == 0) goto L_0x004f
            r7.dismiss()
        L_0x004f:
            r5.loading = r4
            org.telegram.ui.ActionBar.AlertDialog r7 = new org.telegram.ui.ActionBar.AlertDialog
            r7.<init>(r6, r0)
            r5.progressDialog = r7
            r7.showDelayed(r2)
            org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite r6 = new org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite
            r6.<init>()
            org.telegram.messenger.MessagesController r7 = r5.getMessagesController()
            int r0 = r5.chatId
            int r0 = -r0
            org.telegram.tgnet.TLRPC$InputPeer r7 = r7.getInputPeer((int) r0)
            r6.peer = r7
            r6.legacy_revoke_permanent = r1
            org.telegram.ui.Components.SlideChooseView r7 = r5.timeChooseView
            int r7 = r7.getSelectedIndex()
            int r0 = r6.flags
            r0 = r0 | r4
            r6.flags = r0
            java.util.ArrayList<java.lang.Integer> r0 = r5.dispalyedDates
            int r0 = r0.size()
            if (r7 >= r0) goto L_0x009a
            java.util.ArrayList<java.lang.Integer> r0 = r5.dispalyedDates
            java.lang.Object r7 = r0.get(r7)
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r7 = r7.intValue()
            org.telegram.tgnet.ConnectionsManager r0 = r5.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            int r7 = r7 + r0
            r6.expire_date = r7
            goto L_0x009c
        L_0x009a:
            r6.expire_date = r1
        L_0x009c:
            org.telegram.ui.Components.SlideChooseView r7 = r5.usesChooseView
            int r7 = r7.getSelectedIndex()
            int r0 = r6.flags
            r0 = r0 | 2
            r6.flags = r0
            java.util.ArrayList<java.lang.Integer> r0 = r5.dispalyedUses
            int r0 = r0.size()
            if (r7 >= r0) goto L_0x00bf
            java.util.ArrayList<java.lang.Integer> r0 = r5.dispalyedUses
            java.lang.Object r7 = r0.get(r7)
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r7 = r7.intValue()
            r6.usage_limit = r7
            goto L_0x00c1
        L_0x00bf:
            r6.usage_limit = r1
        L_0x00c1:
            org.telegram.tgnet.ConnectionsManager r7 = r5.getConnectionsManager()
            org.telegram.ui.-$$Lambda$LinkEditActivity$e6mHs6Pk_fvDCFD4Da59Z-BSyXE r0 = new org.telegram.ui.-$$Lambda$LinkEditActivity$e6mHs6Pk_fvDCFD4Da59Z-BSyXE
            r0.<init>()
            r7.sendRequest(r6, r0)
            goto L_0x018e
        L_0x00cf:
            if (r7 != r4) goto L_0x018e
            org.telegram.ui.ActionBar.AlertDialog r7 = r5.progressDialog
            if (r7 == 0) goto L_0x00d8
            r7.dismiss()
        L_0x00d8:
            r5.loading = r4
            org.telegram.ui.ActionBar.AlertDialog r7 = new org.telegram.ui.ActionBar.AlertDialog
            r7.<init>(r6, r0)
            r5.progressDialog = r7
            r7.showDelayed(r2)
            org.telegram.tgnet.TLRPC$TL_messages_editExportedChatInvite r6 = new org.telegram.tgnet.TLRPC$TL_messages_editExportedChatInvite
            r6.<init>()
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r7 = r5.inviteToEdit
            java.lang.String r7 = r7.link
            r6.link = r7
            r6.revoked = r1
            org.telegram.messenger.MessagesController r7 = r5.getMessagesController()
            int r0 = r5.chatId
            int r0 = -r0
            org.telegram.tgnet.TLRPC$InputPeer r7 = r7.getInputPeer((int) r0)
            r6.peer = r7
            org.telegram.ui.Components.SlideChooseView r7 = r5.timeChooseView
            int r7 = r7.getSelectedIndex()
            java.util.ArrayList<java.lang.Integer> r0 = r5.dispalyedDates
            int r0 = r0.size()
            if (r7 >= r0) goto L_0x0139
            int r0 = r5.currentInviteDate
            java.util.ArrayList<java.lang.Integer> r2 = r5.dispalyedDates
            java.lang.Object r2 = r2.get(r7)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            if (r0 == r2) goto L_0x0146
            int r0 = r6.flags
            r0 = r0 | r4
            r6.flags = r0
            java.util.ArrayList<java.lang.Integer> r0 = r5.dispalyedDates
            java.lang.Object r7 = r0.get(r7)
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r7 = r7.intValue()
            org.telegram.tgnet.ConnectionsManager r0 = r5.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            int r7 = r7 + r0
            r6.expire_date = r7
            goto L_0x0144
        L_0x0139:
            int r7 = r5.currentInviteDate
            if (r7 == 0) goto L_0x0146
            int r7 = r6.flags
            r7 = r7 | r4
            r6.flags = r7
            r6.expire_date = r1
        L_0x0144:
            r7 = 1
            goto L_0x0147
        L_0x0146:
            r7 = 0
        L_0x0147:
            org.telegram.ui.Components.SlideChooseView r0 = r5.usesChooseView
            int r0 = r0.getSelectedIndex()
            java.util.ArrayList<java.lang.Integer> r2 = r5.dispalyedUses
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x0170
            java.util.ArrayList<java.lang.Integer> r1 = r5.dispalyedUses
            java.lang.Object r0 = r1.get(r0)
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r5.inviteToEdit
            int r1 = r1.usage_limit
            if (r1 == r0) goto L_0x017f
            int r7 = r6.flags
            r7 = r7 | 2
            r6.flags = r7
            r6.usage_limit = r0
            goto L_0x0180
        L_0x0170:
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r0 = r5.inviteToEdit
            int r0 = r0.usage_limit
            if (r0 == 0) goto L_0x017f
            int r7 = r6.flags
            r7 = r7 | 2
            r6.flags = r7
            r6.usage_limit = r1
            goto L_0x0180
        L_0x017f:
            r4 = r7
        L_0x0180:
            if (r4 == 0) goto L_0x018e
            org.telegram.tgnet.ConnectionsManager r7 = r5.getConnectionsManager()
            org.telegram.ui.-$$Lambda$LinkEditActivity$MT79i4ppBzsL-oe6ewA_DcDzklU r0 = new org.telegram.ui.-$$Lambda$LinkEditActivity$MT79i4ppBzsL-oe6ewA_DcDzklU
            r0.<init>()
            r7.sendRequest(r6, r0)
        L_0x018e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LinkEditActivity.lambda$createView$10$LinkEditActivity(android.content.Context, android.view.View):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$7 */
    public /* synthetic */ void lambda$null$7$LinkEditActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LinkEditActivity.this.lambda$null$6$LinkEditActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$6 */
    public /* synthetic */ void lambda$null$6$LinkEditActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
    /* renamed from: lambda$null$9 */
    public /* synthetic */ void lambda$null$9$LinkEditActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LinkEditActivity.this.lambda$null$8$LinkEditActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$8 */
    public /* synthetic */ void lambda$null$8$LinkEditActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        this.loading = false;
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        if (tLRPC$TL_error == null) {
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
                this.timeChooseView.setOptions(4, LocaleController.formatPluralString("Hours", 1), LocaleController.formatPluralString("Days", 1), LocaleController.formatPluralString("Weeks", 1), LocaleController.getString("NoLimit", NUM));
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
                this.usesChooseView.setOptions(4, "1", "10", "100", LocaleController.getString("NoLimit", NUM));
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
        }
    }

    public void finishFragment() {
        this.scrollView.getLayoutParams().height = this.scrollView.getHeight();
        this.finished = true;
        super.finishFragment();
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        AnonymousClass6 r9 = new ThemeDescription.ThemeDescriptionDelegate() {
            public void didSetColor() {
                if (LinkEditActivity.this.dividerUses != null) {
                    Context context = LinkEditActivity.this.dividerUses.getContext();
                    LinkEditActivity.this.dividerUses.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
                    LinkEditActivity.this.divider.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
                    LinkEditActivity.this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
                    LinkEditActivity.this.usesEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                    LinkEditActivity.this.usesEditText.setHintTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
                    LinkEditActivity.this.timeEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                    LinkEditActivity.this.timeEditText.setHintTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
                    LinkEditActivity.this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
                    if (LinkEditActivity.this.revokeLink != null) {
                        LinkEditActivity.this.revokeLink.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
                    }
                }
            }
        };
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
        AnonymousClass6 r7 = r9;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "featuredStickers_addButtonPressed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "featuredStickers_buttonText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "windowBackgroundWhiteRedText5"));
        return arrayList;
    }
}
