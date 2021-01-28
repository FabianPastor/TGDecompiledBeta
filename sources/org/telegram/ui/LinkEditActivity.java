package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_editExportedChatInvite;
import org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite;
import org.telegram.ui.ActionBar.ActionBar;
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
    private final int[] defaultDates = {3600, 86400, 604800};
    private final int[] defaultUses = {1, 10, 100};
    private ArrayList<Integer> dispalyedDates = new ArrayList<>();
    private ArrayList<Integer> dispalyedUses = new ArrayList<>();
    /* access modifiers changed from: private */
    public TextInfoPrivacyCell divider;
    /* access modifiers changed from: private */
    public TextInfoPrivacyCell dividerUses;
    /* access modifiers changed from: private */
    public boolean ignoreSet;
    TLRPC$TL_chatInviteExported inviteToEdit;
    boolean loading;
    AlertDialog progressDialog;
    /* access modifiers changed from: private */
    public TextSettingsCell revokeLink;
    boolean scrollToEnd;
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

    static /* synthetic */ void lambda$createView$0(int i, boolean z) {
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
                }
            }
        });
        final ScrollView scrollView = new ScrollView(context);
        AnonymousClass2 r2 = new SizeNotifierFrameLayout(context) {
            int oldKeyboardHeight;

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
                this.oldKeyboardHeight = this.keyboardHeight;
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                LinkEditActivity linkEditActivity = LinkEditActivity.this;
                if (linkEditActivity.scrollToEnd) {
                    linkEditActivity.scrollToEnd = false;
                    ScrollView scrollView = scrollView;
                    scrollView.scrollTo(0, Math.max(0, (scrollView.getChildAt(0).getMeasuredHeight() - scrollView.getMeasuredHeight()) - AndroidUtilities.dp(16.0f)));
                }
            }
        };
        r2.setDelegate($$Lambda$LinkEditActivity$kfhhV_pxcKpxsLigNYlQ3uJhBg.INSTANCE);
        this.fragmentView = r2;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        scrollView.addView(linearLayout);
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
        linearLayout.addView(this.timeHeaderCell);
        SlideChooseView slideChooseView = new SlideChooseView(context);
        this.timeChooseView = slideChooseView;
        linearLayout.addView(slideChooseView);
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
                LinkEditActivity.this.lambda$createView$2$LinkEditActivity(this.f$1, view);
            }
        });
        this.timeChooseView.setCallback(new SlideChooseView.Callback() {
            public final void onOptionSelected(int i) {
                LinkEditActivity.this.lambda$createView$3$LinkEditActivity(i);
            }
        });
        resetDates();
        linearLayout.addView(this.timeEditText, LayoutHelper.createLinear(-1, 50));
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
        this.divider = textInfoPrivacyCell;
        textInfoPrivacyCell.setText(LocaleController.getString("TimeLimitHelp", NUM));
        linearLayout.addView(this.divider);
        HeaderCell headerCell2 = new HeaderCell(context);
        this.usesHeaderCell = headerCell2;
        headerCell2.setText(LocaleController.getString("LimitNumberOfUses", NUM));
        linearLayout.addView(this.usesHeaderCell);
        SlideChooseView slideChooseView2 = new SlideChooseView(context);
        this.usesChooseView = slideChooseView2;
        slideChooseView2.setCallback(new SlideChooseView.Callback() {
            public final void onOptionSelected(int i) {
                LinkEditActivity.this.lambda$createView$4$LinkEditActivity(i);
            }
        });
        resetUses();
        linearLayout.addView(this.usesChooseView);
        EditText editText = new EditText(context);
        this.usesEditText = editText;
        editText.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
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
                        LinkEditActivity.this.chooseUses(Integer.parseInt(editable.toString()));
                    } catch (NumberFormatException unused) {
                        LinkEditActivity.this.resetUses();
                    }
                }
            }
        });
        linearLayout.addView(this.usesEditText, LayoutHelper.createLinear(-1, 50));
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
        this.dividerUses = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setText(LocaleController.getString("UsesLimitHelp", NUM));
        linearLayout.addView(this.dividerUses);
        if (this.type == 1) {
            TextSettingsCell textSettingsCell = new TextSettingsCell(context);
            this.revokeLink = textSettingsCell;
            textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.revokeLink.setText(LocaleController.getString("RevokeLink", NUM), false);
            this.revokeLink.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
            this.revokeLink.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    LinkEditActivity.this.lambda$createView$6$LinkEditActivity(view);
                }
            });
            linearLayout.addView(this.revokeLink);
        }
        r2.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 0.0f, 0.0f, 80.0f));
        r2.addView(this.buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 15.0f, 16.0f, 16.0f));
        this.timeHeaderCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.timeChooseView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.timeEditText.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.usesHeaderCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.usesChooseView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.usesEditText.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        r2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.buttonTextView.setOnClickListener(new View.OnClickListener(context) {
            public final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                LinkEditActivity.this.lambda$createView$11$LinkEditActivity(this.f$1, view);
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
        setInviteToEdit(this.inviteToEdit);
        return r2;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$2 */
    public /* synthetic */ void lambda$createView$2$LinkEditActivity(Context context, View view) {
        AlertsCreator.createDatePickerDialog(context, -1, new AlertsCreator.ScheduleDatePickerDelegate() {
            public final void didSelectDate(boolean z, int i) {
                LinkEditActivity.this.lambda$null$1$LinkEditActivity(z, i);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$1 */
    public /* synthetic */ void lambda$null$1$LinkEditActivity(boolean z, int i) {
        chooseDate(i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$3 */
    public /* synthetic */ void lambda$createView$3$LinkEditActivity(int i) {
        if (i < this.dispalyedDates.size()) {
            this.timeEditText.setText(LocaleController.formatDateAudio((long) (this.dispalyedDates.get(i).intValue() + getConnectionsManager().getCurrentTime()), false));
            return;
        }
        this.timeEditText.setText("");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$4 */
    public /* synthetic */ void lambda$createView$4$LinkEditActivity(int i) {
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
    /* renamed from: lambda$createView$6 */
    public /* synthetic */ void lambda$createView$6$LinkEditActivity(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setMessage(LocaleController.getString("RevokeAlert", NUM));
        builder.setTitle(LocaleController.getString("RevokeLink", NUM));
        builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                LinkEditActivity.this.lambda$null$5$LinkEditActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$5 */
    public /* synthetic */ void lambda$null$5$LinkEditActivity(DialogInterface dialogInterface, int i) {
        this.callback.revokeLink(this.inviteToEdit);
        finishFragment();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$11 */
    public /* synthetic */ void lambda$createView$11$LinkEditActivity(Context context, View view) {
        if (!this.loading) {
            int i = this.type;
            if (i == 0) {
                AlertDialog alertDialog = this.progressDialog;
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
                this.loading = true;
                AlertDialog alertDialog2 = new AlertDialog(context, 3);
                this.progressDialog = alertDialog2;
                alertDialog2.showDelayed(500);
                TLRPC$TL_messages_exportChatInvite tLRPC$TL_messages_exportChatInvite = new TLRPC$TL_messages_exportChatInvite();
                tLRPC$TL_messages_exportChatInvite.peer = getMessagesController().getInputPeer(-this.chatId);
                getConnectionsManager().sendRequest(tLRPC$TL_messages_exportChatInvite, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        LinkEditActivity.this.lambda$null$8$LinkEditActivity(tLObject, tLRPC$TL_error);
                    }
                });
            } else if (i == 1) {
                AlertDialog alertDialog3 = this.progressDialog;
                if (alertDialog3 != null) {
                    alertDialog3.dismiss();
                }
                this.loading = true;
                AlertDialog alertDialog4 = new AlertDialog(context, 3);
                this.progressDialog = alertDialog4;
                alertDialog4.showDelayed(500);
                TLRPC$TL_messages_editExportedChatInvite tLRPC$TL_messages_editExportedChatInvite = new TLRPC$TL_messages_editExportedChatInvite();
                tLRPC$TL_messages_editExportedChatInvite.link = this.inviteToEdit.link;
                tLRPC$TL_messages_editExportedChatInvite.revoked = false;
                tLRPC$TL_messages_editExportedChatInvite.peer = getMessagesController().getInputPeer(-this.chatId);
                int selectedIndex = this.timeChooseView.getSelectedIndex();
                tLRPC$TL_messages_editExportedChatInvite.flags |= 1;
                if (selectedIndex < this.dispalyedDates.size()) {
                    tLRPC$TL_messages_editExportedChatInvite.expire_date = this.dispalyedDates.get(selectedIndex).intValue() + getConnectionsManager().getCurrentTime();
                } else {
                    tLRPC$TL_messages_editExportedChatInvite.expire_date = 0;
                }
                int selectedIndex2 = this.usesChooseView.getSelectedIndex();
                tLRPC$TL_messages_editExportedChatInvite.flags |= 2;
                if (selectedIndex2 < this.dispalyedUses.size()) {
                    tLRPC$TL_messages_editExportedChatInvite.usage_limit = this.dispalyedUses.get(selectedIndex2).intValue();
                } else {
                    tLRPC$TL_messages_editExportedChatInvite.usage_limit = 0;
                }
                getConnectionsManager().sendRequest(tLRPC$TL_messages_editExportedChatInvite, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        LinkEditActivity.this.lambda$null$10$LinkEditActivity(tLObject, tLRPC$TL_error);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$8 */
    public /* synthetic */ void lambda$null$8$LinkEditActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LinkEditActivity.this.lambda$null$7$LinkEditActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$7 */
    public /* synthetic */ void lambda$null$7$LinkEditActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
    /* renamed from: lambda$null$10 */
    public /* synthetic */ void lambda$null$10$LinkEditActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LinkEditActivity.this.lambda$null$9$LinkEditActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$9 */
    public /* synthetic */ void lambda$null$9$LinkEditActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
            }
            int i2 = tLRPC$TL_chatInviteExported.usage_limit;
            if (i2 > 0) {
                chooseUses(i2);
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        AnonymousClass4 r9 = new ThemeDescription.ThemeDescriptionDelegate() {
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
        AnonymousClass4 r7 = r9;
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
