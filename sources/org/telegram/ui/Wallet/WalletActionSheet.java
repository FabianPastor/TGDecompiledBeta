package org.telegram.ui.Wallet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.widget.NestedScrollView;
import java.net.URLEncoder;
import javax.crypto.Cipher;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.TonController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.BiometricPromtHelper;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.TypefaceSpan;

public class WalletActionSheet extends BottomSheet {
    private static final int MAX_COMMENT_LENGTH = 500;
    public static final int SEND_ACTIVITY_RESULT_CODE = 33;
    public static final int TYPE_INVOICE = 1;
    public static final int TYPE_SEND = 0;
    public static final int TYPE_TRANSACTION = 2;
    private ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private View actionBarShadow;
    private int amountHeaderRow;
    private int amountRow;
    private long amountValue;
    private Paint backgroundPaint = new Paint(1);
    private int balanceRow;
    private BiometricPromtHelper biometricPromtHelper;
    private int commentHeaderRow;
    private int commentRow;
    private String commentString;
    private long currentBalance;
    private long currentDate;
    private long currentStorageFee;
    private long currentTransactionFee;
    private int currentType;
    private int dateHeaderRow;
    private int dateRow;
    private WalletActionSheetDelegate delegate;
    private Drawable gemDrawable;
    private boolean hasWalletInBack;
    private boolean inLayout;
    private int invoiceInfoRow;
    private LinearLayout linearLayout;
    private ListAdapter listAdapter;
    private BaseFragment parentFragment;
    private int recipientHeaderRow;
    private int recipientRow;
    private String recipientString;
    private int rowCount;
    private int scrollOffsetY;
    private NestedScrollView scrollView;
    private int sendBalanceRow;
    private View shadow;
    private AnimatorSet shadowAnimation;
    private int titleRow;
    private String walletAddress;
    private boolean wasFirstAttach;

    public class BalanceCell extends FrameLayout {
        private SimpleTextView valueTextView;
        private TextView yourBalanceTextView;

        public BalanceCell(Context context) {
            super(context);
            this.valueTextView = new SimpleTextView(context);
            this.valueTextView.setTextSize(30);
            this.valueTextView.setRightDrawable(NUM);
            this.valueTextView.setRightDrawableScale(0.8f);
            this.valueTextView.setDrawablePadding(AndroidUtilities.dp(7.0f));
            this.valueTextView.setGravity(1);
            this.valueTextView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.valueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            addView(this.valueTextView, LayoutHelper.createFrame(-1, -2.0f, 1, 0.0f, 18.0f, 0.0f, 0.0f));
            this.yourBalanceTextView = new TextView(context);
            this.yourBalanceTextView.setTextSize(1, 12.0f);
            this.yourBalanceTextView.setTextColor(Theme.getColor("dialogTextGray2"));
            this.yourBalanceTextView.setLineSpacing((float) AndroidUtilities.dp(4.0f), 1.0f);
            this.yourBalanceTextView.setGravity(1);
            addView(this.yourBalanceTextView, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 59.0f, 0.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
        }

        public void setBalance(long j, long j2, long j3) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(j > 0 ? "+" : "");
            stringBuilder.append(TonController.formatCurrency(j));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder.toString());
            int indexOf = TextUtils.indexOf(spannableStringBuilder, '.');
            if (indexOf >= 0) {
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), AndroidUtilities.dp(22.0f)), indexOf + 1, spannableStringBuilder.length(), 33);
            }
            this.valueTextView.setText(spannableStringBuilder);
            if (j2 == 0 && j3 == 0) {
                this.yourBalanceTextView.setVisibility(4);
                return;
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            if (j3 != 0) {
                stringBuilder2.append(LocaleController.formatString("WalletTransactionFee", NUM, TonController.formatCurrency(j3)));
            }
            if (j2 != 0) {
                if (stringBuilder2.length() != 0) {
                    stringBuilder2.append(10);
                }
                stringBuilder2.append(LocaleController.formatString("WalletStorageFee", NUM, TonController.formatCurrency(j2)));
            }
            this.yourBalanceTextView.setText(stringBuilder2);
            this.yourBalanceTextView.setVisibility(0);
        }
    }

    public static class ByteLengthFilter implements InputFilter {
        private final int mMax;

        public ByteLengthFilter(int i) {
            this.mMax = i;
        }

        public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
            int length = this.mMax - (spanned.toString().getBytes().length - (i4 - i3));
            String str = "";
            if (length <= 0) {
                return str;
            }
            if (length >= i2 - i) {
                return null;
            }
            try {
                return new String(charSequence.toString().getBytes(), i, length + i, "UTF-8");
            } catch (Exception unused) {
                return str;
            }
        }

        public int getMax() {
            return this.mMax;
        }
    }

    private class ListAdapter {
        private ListAdapter() {
        }

        /* synthetic */ ListAdapter(WalletActionSheet walletActionSheet, AnonymousClass1 anonymousClass1) {
            this();
        }

        public int getItemCount() {
            return WalletActionSheet.this.rowCount;
        }

        public void onBindViewHolder(View view, int i, int i2) {
            CharSequence charSequence = "";
            TextInfoPrivacyCell textInfoPrivacyCell;
            PollEditTextCell pollEditTextCell;
            switch (i2) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) view;
                    if (i == WalletActionSheet.this.recipientHeaderRow) {
                        if (WalletActionSheet.this.currentType != 2) {
                            headerCell.setText(LocaleController.getString("WalletSendRecipient", NUM));
                            return;
                        } else if (WalletActionSheet.this.amountValue > 0) {
                            headerCell.setText(LocaleController.getString("WalletTransactionSender", NUM));
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("WalletTransactionRecipient", NUM));
                            return;
                        }
                    } else if (i == WalletActionSheet.this.commentHeaderRow) {
                        headerCell.setText(LocaleController.getString("WalletTransactionComment", NUM));
                        return;
                    } else if (i == WalletActionSheet.this.dateHeaderRow) {
                        headerCell.setText(LocaleController.getString("WalletDate", NUM));
                        return;
                    } else if (i == WalletActionSheet.this.amountHeaderRow) {
                        headerCell.setText(LocaleController.getString("WalletAmount", NUM));
                        return;
                    } else {
                        return;
                    }
                case 1:
                    textInfoPrivacyCell = (TextInfoPrivacyCell) view;
                    if (i == WalletActionSheet.this.invoiceInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("WalletInvoiceInfo", NUM));
                        return;
                    }
                    return;
                case 3:
                    pollEditTextCell = (PollEditTextCell) view;
                    if (i == WalletActionSheet.this.dateRow) {
                        pollEditTextCell.setTextAndHint(LocaleController.getInstance().formatterStats.format(WalletActionSheet.this.currentDate * 1000), charSequence, false);
                        return;
                    } else if (i == WalletActionSheet.this.commentRow) {
                        pollEditTextCell.setTextAndHint(WalletActionSheet.this.commentString, LocaleController.getString("WalletComment", NUM), false);
                        return;
                    } else {
                        return;
                    }
                case 4:
                    pollEditTextCell = (PollEditTextCell) view;
                    if (WalletActionSheet.this.amountValue != 0) {
                        charSequence = TonController.formatCurrency(WalletActionSheet.this.amountValue);
                    }
                    pollEditTextCell.setText(charSequence, true);
                    return;
                case 6:
                    ((SendAddressCell) view).setTextAndHint(WalletActionSheet.this.recipientString, LocaleController.getString("WalletEnterWalletAddress", NUM), false);
                    return;
                case 7:
                    textInfoPrivacyCell = (TextInfoPrivacyCell) view;
                    if (i == WalletActionSheet.this.sendBalanceRow && TonController.getInstance(WalletActionSheet.this.currentAccount).getCachedAccountState() != null) {
                        textInfoPrivacyCell.setText(LocaleController.formatString("WalletSendBalance", NUM, TonController.formatCurrency(WalletActionSheet.this.currentBalance = TonController.getBalance(TonController.getInstance(WalletActionSheet.this.currentAccount).getCachedAccountState()))));
                        return;
                    }
                    return;
                case 8:
                    ((BalanceCell) view).setBalance(WalletActionSheet.this.amountValue, WalletActionSheet.this.currentStorageFee, WalletActionSheet.this.currentTransactionFee);
                    return;
                case 9:
                    TitleCell titleCell = (TitleCell) view;
                    if (i != WalletActionSheet.this.titleRow) {
                        return;
                    }
                    if (WalletActionSheet.this.currentType == 1) {
                        titleCell.setText(LocaleController.getString("WalletCreateInvoiceTitle", NUM));
                        return;
                    } else if (WalletActionSheet.this.currentType == 2) {
                        titleCell.setText(LocaleController.getString("WalletTransaction", NUM));
                        return;
                    } else if (WalletActionSheet.this.currentType == 0) {
                        titleCell.setText(LocaleController.getString("WalletSendGrams", NUM));
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public View createView(Context context, int i) {
            View textInfoPrivacyCell;
            int itemViewType = getItemViewType(i);
            EditTextBoldCursor textView;
            if (itemViewType == 0) {
                View headerCell = new HeaderCell(context, false, 21, 12, false);
            } else if (itemViewType == 1) {
                textInfoPrivacyCell = new TextInfoPrivacyCell(context);
            } else if (itemViewType == 3) {
                final View anonymousClass1 = new PollEditTextCell(context, null) {
                    /* Access modifiers changed, original: protected */
                    public void onAttachedToWindow() {
                        super.onAttachedToWindow();
                        WalletActionSheet.this.setTextLeft(this);
                    }
                };
                textView = anonymousClass1.getTextView();
                if (WalletActionSheet.this.currentType == 2) {
                    textView.setEnabled(false);
                    textView.setFocusable(false);
                    textView.setClickable(false);
                } else {
                    textView.setBackground(Theme.createEditTextDrawable(context, true));
                    textView.setPadding(0, AndroidUtilities.dp(14.0f), AndroidUtilities.dp(37.0f), AndroidUtilities.dp(14.0f));
                    anonymousClass1.createErrorTextView();
                    textView.setFilters(new InputFilter[]{new ByteLengthFilter(500)});
                    anonymousClass1.addTextWatcher(new TextWatcher() {
                        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        public void afterTextChanged(Editable editable) {
                            if (anonymousClass1.getTag() == null) {
                                WalletActionSheet.this.commentString = editable.toString();
                                View childAt = WalletActionSheet.this.linearLayout.getChildAt(WalletActionSheet.this.commentRow);
                                if (childAt != null) {
                                    WalletActionSheet.this.setTextLeft(childAt);
                                }
                            }
                        }
                    });
                }
                textInfoPrivacyCell = anonymousClass1;
            } else if (itemViewType != 4) {
                textInfoPrivacyCell = itemViewType != 6 ? itemViewType != 7 ? itemViewType != 8 ? new TitleCell(context) : new BalanceCell(context) : new TextInfoPrivacyCell(context) {
                    /* Access modifiers changed, original: protected */
                    public void onAttachedToWindow() {
                        super.onAttachedToWindow();
                        WalletActionSheet.this.setTextLeft(this);
                    }
                } : new SendAddressCell(WalletActionSheet.this, context);
            } else {
                View anonymousClass3 = new PollEditTextCell(context, null) {
                    /* Access modifiers changed, original: protected */
                    public boolean drawDivider() {
                        return false;
                    }

                    /* Access modifiers changed, original: protected */
                    public void onEditTextDraw(EditTextBoldCursor editTextBoldCursor, Canvas canvas) {
                        Layout layout;
                        int dp = AndroidUtilities.dp(7.0f);
                        if (editTextBoldCursor.length() > 0) {
                            layout = editTextBoldCursor.getLayout();
                        } else {
                            layout = editTextBoldCursor.getHintLayoutEx();
                        }
                        int i = 0;
                        if (layout != null) {
                            i = AndroidUtilities.dp(6.0f) + ((int) Math.ceil((double) layout.getLineWidth(0)));
                        }
                        if (i != 0) {
                            WalletActionSheet.this.gemDrawable.setBounds(i, dp, ((int) (((float) WalletActionSheet.this.gemDrawable.getIntrinsicWidth()) * 0.74f)) + i, ((int) (((float) WalletActionSheet.this.gemDrawable.getIntrinsicHeight()) * 0.74f)) + dp);
                            WalletActionSheet.this.gemDrawable.draw(canvas);
                        }
                    }

                    /* Access modifiers changed, original: protected */
                    public void onAttachedToWindow() {
                        super.onAttachedToWindow();
                        if (!WalletActionSheet.this.wasFirstAttach && WalletActionSheet.this.currentType != 2) {
                            if (WalletActionSheet.this.recipientString.codePointCount(0, WalletActionSheet.this.recipientString.length()) == 48) {
                                getTextView().requestFocus();
                            }
                            WalletActionSheet.this.wasFirstAttach = true;
                        }
                    }
                };
                textView = anonymousClass3.getTextView();
                anonymousClass3.setShowNextButton(true);
                textView.setTextColor(Theme.getColor("dialogTextBlack"));
                textView.setHintColor(Theme.getColor("dialogTextHint"));
                textView.setTextSize(1, 30.0f);
                textView.setBackground(Theme.createEditTextDrawable(context, true));
                textView.setImeOptions(textView.getImeOptions() | 5);
                textView.setCursorSize(AndroidUtilities.dp(30.0f));
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("0.0");
                spannableStringBuilder.setSpan(new RelativeSizeSpan(0.73f), spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 33);
                textView.setHintText(spannableStringBuilder);
                textView.setInputType(3);
                textView.setOnEditorActionListener(new -$$Lambda$WalletActionSheet$ListAdapter$ZFDAbICnPCIUoc8-bv49sieBXSk(this));
                anonymousClass3.addTextWatcher(new TextWatcher() {
                    private boolean adding;
                    private boolean ignoreTextChange;
                    private RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.73f);

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        boolean z = true;
                        if (!(i2 == 0 && i3 == 1)) {
                            z = false;
                        }
                        this.adding = z;
                    }

                    public void afterTextChanged(Editable editable) {
                        if (!this.ignoreTextChange && textView.getTag() == null) {
                            textView.getSelectionStart();
                            this.ignoreTextChange = true;
                            int i = 0;
                            int i2 = 0;
                            while (i < editable.length()) {
                                char charAt = editable.charAt(i);
                                if (charAt == ',' || charAt == '#' || charAt == '*') {
                                    editable.replace(i, i + 1, ".");
                                    charAt = '.';
                                }
                                if (charAt == '.' && i2 == 0) {
                                    i2++;
                                } else if (charAt < '0' || charAt > '9') {
                                    editable.delete(i, i + 1);
                                    i--;
                                }
                                i++;
                            }
                            if (editable.length() > 0 && editable.charAt(0) == '.') {
                                editable.insert(0, "0");
                            }
                            if (this.adding && editable.length() == 1 && editable.charAt(0) == '0') {
                                editable.replace(0, editable.length(), "0.");
                            }
                            i = TextUtils.indexOf(editable, '.');
                            if (i >= 0) {
                                if (editable.length() - i > 10) {
                                    editable.delete(i + 10, editable.length());
                                }
                                if (i > 9) {
                                    int i3 = i - 9;
                                    editable.delete(9, i3 + 9);
                                    i -= i3;
                                }
                                String charSequence = editable.subSequence(0, i).toString();
                                i++;
                                String charSequence2 = editable.subSequence(i, editable.length()).toString();
                                WalletActionSheet walletActionSheet = WalletActionSheet.this;
                                long longValue = Utilities.parseLong(charSequence).longValue() * NUM;
                                double longValue2 = (double) Utilities.parseLong(charSequence2).longValue();
                                double pow = Math.pow(10.0d, (double) (9 - charSequence2.length()));
                                Double.isNaN(longValue2);
                                walletActionSheet.amountValue = longValue + ((long) ((int) (longValue2 * pow)));
                                editable.setSpan(this.sizeSpan, i, editable.length(), 33);
                            } else {
                                if (editable.length() > 9) {
                                    editable.delete(9, editable.length());
                                }
                                WalletActionSheet.this.amountValue = Utilities.parseLong(editable.toString()).longValue() * NUM;
                            }
                            this.ignoreTextChange = false;
                            View childAt = WalletActionSheet.this.linearLayout.getChildAt(WalletActionSheet.this.sendBalanceRow);
                            if (childAt != null) {
                                WalletActionSheet.this.setTextLeft(childAt);
                            }
                        }
                    }
                });
                textInfoPrivacyCell = anonymousClass3;
            }
            onBindViewHolder(textInfoPrivacyCell, i, itemViewType);
            return textInfoPrivacyCell;
        }

        public /* synthetic */ boolean lambda$createView$0$WalletActionSheet$ListAdapter(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            View childAt = WalletActionSheet.this.linearLayout.getChildAt(WalletActionSheet.this.commentRow);
            if (childAt != null) {
                ((PollEditTextCell) childAt).getTextView().requestFocus();
            }
            return true;
        }

        public int getItemViewType(int i) {
            if (i == WalletActionSheet.this.recipientHeaderRow || i == WalletActionSheet.this.commentHeaderRow || i == WalletActionSheet.this.dateHeaderRow || i == WalletActionSheet.this.amountHeaderRow) {
                return 0;
            }
            if (i == WalletActionSheet.this.invoiceInfoRow) {
                return 1;
            }
            if (i == WalletActionSheet.this.commentRow || i == WalletActionSheet.this.dateRow) {
                return 3;
            }
            if (i == WalletActionSheet.this.amountRow) {
                return 4;
            }
            if (i == WalletActionSheet.this.recipientRow) {
                return 6;
            }
            if (i == WalletActionSheet.this.sendBalanceRow) {
                return 7;
            }
            return i == WalletActionSheet.this.balanceRow ? 8 : 9;
        }
    }

    private class TitleCell extends FrameLayout {
        private TextView titleView = new TextView(getContext());

        public TitleCell(Context context) {
            super(context);
            this.titleView.setLines(1);
            this.titleView.setSingleLine(true);
            this.titleView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.titleView.setTextSize(1, 20.0f);
            this.titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titleView.setPadding(AndroidUtilities.dp(22.0f), AndroidUtilities.dp(15.0f), AndroidUtilities.dp(22.0f), AndroidUtilities.dp(8.0f));
            this.titleView.setEllipsize(TruncateAt.MIDDLE);
            this.titleView.setGravity(16);
            addView(this.titleView, LayoutHelper.createFrame(-1, 60.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM));
        }

        public void setText(String str) {
            this.titleView.setText(str);
        }
    }

    public interface WalletActionSheetDelegate {

        public final /* synthetic */ class -CC {
            public static void $default$openInvoice(WalletActionSheetDelegate walletActionSheetDelegate, String str, long j) {
            }

            public static void $default$openQrReader(WalletActionSheetDelegate walletActionSheetDelegate) {
            }

            public static void $default$openSendToAddress(WalletActionSheetDelegate walletActionSheetDelegate, String str) {
            }
        }

        void openInvoice(String str, long j);

        void openQrReader();

        void openSendToAddress(String str);
    }

    private class SendAddressCell extends PollEditTextCell {
        private ImageView copyButton;
        private ImageView qrButton;
        final /* synthetic */ WalletActionSheet this$0;

        public SendAddressCell(WalletActionSheet walletActionSheet, Context context) {
            final WalletActionSheet walletActionSheet2 = walletActionSheet;
            Context context2 = context;
            this.this$0 = walletActionSheet2;
            super(context2, null);
            EditTextBoldCursor textView = getTextView();
            int i = 0;
            textView.setSingleLine(false);
            textView.setInputType(655505);
            textView.setMinLines(2);
            textView.setTypeface(Typeface.DEFAULT);
            int i2 = 5;
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            addTextWatcher(new TextWatcher() {
                private boolean ignoreTextChange;
                private boolean isPaste;

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    this.isPaste = i3 >= 24;
                }

                public void afterTextChanged(Editable editable) {
                    if (!this.ignoreTextChange) {
                        String obj = editable.toString();
                        if (this.isPaste && obj.toLowerCase().startsWith("ton://transfer")) {
                            this.ignoreTextChange = true;
                            SendAddressCell.this.this$0.parseTonUrl(editable, obj);
                            this.ignoreTextChange = false;
                        } else {
                            SendAddressCell.this.this$0.recipientString = obj;
                        }
                    }
                }
            });
            String str = "actionBarWhiteSelector";
            String str2 = "windowBackgroundWhiteBlueHeader";
            ImageView imageView;
            if (walletActionSheet.currentType == 0) {
                textView.setBackground(Theme.createEditTextDrawable(context2, true));
                this.qrButton = new ImageView(context2);
                this.qrButton.setImageResource(NUM);
                this.qrButton.setScaleType(ScaleType.CENTER);
                this.qrButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str2), Mode.MULTIPLY));
                this.qrButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(str), 6));
                imageView = this.qrButton;
                if (LocaleController.isRTL) {
                    i2 = 3;
                }
                addView(imageView, LayoutHelper.createFrame(48, 48.0f, i2 | 48, 6.0f, 0.0f, 6.0f, 0.0f));
                this.qrButton.setOnClickListener(new -$$Lambda$WalletActionSheet$SendAddressCell$14b9M9CVT7zAKhJBLIaOAlxl2xA(this));
                int dp = LocaleController.isRTL ? AndroidUtilities.dp(60.0f) : 0;
                int dp2 = AndroidUtilities.dp(13.0f);
                if (!LocaleController.isRTL) {
                    i = AndroidUtilities.dp(60.0f);
                }
                textView.setPadding(dp, dp2, i, AndroidUtilities.dp(8.0f));
                return;
            }
            textView.setFocusable(false);
            textView.setEnabled(false);
            textView.setTypeface(Typeface.MONOSPACE);
            textView.setPadding(0, AndroidUtilities.dp(13.0f), 0, AndroidUtilities.dp(10.0f));
            this.copyButton = new ImageView(context2);
            this.copyButton.setImageResource(NUM);
            this.copyButton.setScaleType(ScaleType.CENTER);
            this.copyButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str2), Mode.MULTIPLY));
            this.copyButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(str), 6));
            imageView = this.copyButton;
            if (LocaleController.isRTL) {
                i2 = 3;
            }
            addView(imageView, LayoutHelper.createFrame(48, 48.0f, i2 | 48, 6.0f, 10.0f, 6.0f, 0.0f));
            this.copyButton.setOnClickListener(new -$$Lambda$WalletActionSheet$SendAddressCell$A3gvDCpvXUcB-rNjyoAe1A4uz14(this));
        }

        public /* synthetic */ void lambda$new$0$WalletActionSheet$SendAddressCell(View view) {
            AndroidUtilities.hideKeyboard(this.this$0.getCurrentFocus());
            this.this$0.delegate.openQrReader();
        }

        public /* synthetic */ void lambda$new$1$WalletActionSheet$SendAddressCell(View view) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ton://transfer/");
            stringBuilder.append(this.this$0.recipientString.replace("\n", ""));
            AndroidUtilities.addToClipboard(stringBuilder.toString());
            Toast.makeText(view.getContext(), LocaleController.getString("WalletTransactionAddressCopied", NUM), 0).show();
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            ImageView imageView = this.qrButton;
            if (imageView != null) {
                measureChildWithMargins(imageView, i, 0, i2, 0);
            }
            ImageView imageView2 = this.copyButton;
            if (imageView2 != null) {
                measureChildWithMargins(imageView2, i, 0, i2, 0);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public void onResume() {
    }

    public WalletActionSheet(BaseFragment baseFragment, int i, String str) {
        super(baseFragment.getParentActivity(), true);
        String str2 = "";
        this.commentString = str2;
        this.recipientString = str2;
        this.hasWalletInBack = true;
        this.currentBalance = -1;
        this.walletAddress = str;
        if (this.walletAddress == null) {
            this.walletAddress = TonController.getInstance(this.currentAccount).getWalletAddress(UserConfig.getInstance(this.currentAccount).tonPublicKey);
        }
        this.currentType = i;
        this.parentFragment = baseFragment;
        init(baseFragment.getParentActivity());
    }

    public WalletActionSheet(BaseFragment baseFragment, String str, String str2, String str3, long j, long j2, long j3, long j4) {
        super(baseFragment.getParentActivity(), false);
        String str4 = "";
        this.commentString = str4;
        this.recipientString = str4;
        this.hasWalletInBack = true;
        this.currentBalance = -1;
        this.walletAddress = str;
        this.recipientString = str2;
        this.commentString = str3;
        this.amountValue = j;
        this.currentDate = j2;
        this.currentType = 2;
        this.currentStorageFee = j3;
        this.currentTransactionFee = j4;
        this.parentFragment = baseFragment;
        init(baseFragment.getParentActivity());
    }

    private void init(Context context) {
        final Context context2 = context;
        updateRows();
        this.gemDrawable = context.getResources().getDrawable(NUM);
        AnonymousClass1 anonymousClass1 = new FrameLayout(context2) {
            private boolean ignoreLayout;
            private RectF rect = new RectF();

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || WalletActionSheet.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) WalletActionSheet.this.scrollOffsetY) || WalletActionSheet.this.actionBar.getAlpha() != 0.0f) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                WalletActionSheet.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return !WalletActionSheet.this.isDismissed() && super.onTouchEvent(motionEvent);
            }

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                i2 = MeasureSpec.getSize(i2);
                if (VERSION.SDK_INT >= 21) {
                    this.ignoreLayout = true;
                    setPadding(WalletActionSheet.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, WalletActionSheet.this.backgroundPaddingLeft, 0);
                    this.ignoreLayout = false;
                }
                int paddingTop = i2 - getPaddingTop();
                MeasureSpec.getSize(i);
                WalletActionSheet.this.backgroundPaddingLeft;
                ((LayoutParams) WalletActionSheet.this.actionBarShadow.getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight();
                this.ignoreLayout = true;
                int dp = AndroidUtilities.dp(80.0f);
                int itemCount = WalletActionSheet.this.listAdapter.getItemCount();
                int i3 = dp;
                for (dp = 0; dp < itemCount; dp++) {
                    View createView = WalletActionSheet.this.listAdapter.createView(context2, dp);
                    createView.measure(i, MeasureSpec.makeMeasureSpec(0, 0));
                    i3 += createView.getMeasuredHeight();
                }
                paddingTop = i3 < paddingTop ? paddingTop - i3 : WalletActionSheet.this.currentType == 2 ? paddingTop / 5 : 0;
                if (WalletActionSheet.this.scrollView.getPaddingTop() != paddingTop) {
                    WalletActionSheet.this.scrollView.getPaddingTop();
                    WalletActionSheet.this.scrollView.setPadding(0, paddingTop, 0, 0);
                }
                this.ignoreLayout = false;
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(i2, NUM));
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                WalletActionSheet.this.inLayout = true;
                super.onLayout(z, i, i2, i3, i4);
                WalletActionSheet.this.inLayout = false;
                WalletActionSheet.this.updateLayout(false);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                int access$300 = WalletActionSheet.this.scrollOffsetY - WalletActionSheet.this.backgroundPaddingTop;
                int measuredHeight = (getMeasuredHeight() + AndroidUtilities.dp(30.0f)) + WalletActionSheet.this.backgroundPaddingTop;
                float dp = (float) AndroidUtilities.dp(12.0f);
                float min = ((float) (WalletActionSheet.this.backgroundPaddingTop + access$300)) < dp ? 1.0f - Math.min(1.0f, ((dp - ((float) access$300)) - ((float) WalletActionSheet.this.backgroundPaddingTop)) / dp) : 1.0f;
                if (VERSION.SDK_INT >= 21) {
                    int i = AndroidUtilities.statusBarHeight;
                    access$300 += i;
                    measuredHeight -= i;
                }
                WalletActionSheet.this.shadowDrawable.setBounds(0, access$300, getMeasuredWidth(), measuredHeight);
                WalletActionSheet.this.shadowDrawable.draw(canvas);
                String str = "dialogBackground";
                if (min != 1.0f) {
                    WalletActionSheet.this.backgroundPaint.setColor(Theme.getColor(str));
                    this.rect.set((float) WalletActionSheet.this.backgroundPaddingLeft, (float) (WalletActionSheet.this.backgroundPaddingTop + access$300), (float) (getMeasuredWidth() - WalletActionSheet.this.backgroundPaddingLeft), (float) ((WalletActionSheet.this.backgroundPaddingTop + access$300) + AndroidUtilities.dp(24.0f)));
                    dp *= min;
                    canvas.drawRoundRect(this.rect, dp, dp, WalletActionSheet.this.backgroundPaint);
                }
                access$300 = Theme.getColor(str);
                WalletActionSheet.this.backgroundPaint.setColor(Color.argb((int) (WalletActionSheet.this.actionBar.getAlpha() * 255.0f), (int) (((float) Color.red(access$300)) * 0.8f), (int) (((float) Color.green(access$300)) * 0.8f), (int) (((float) Color.blue(access$300)) * 0.8f)));
                canvas.drawRect((float) WalletActionSheet.this.backgroundPaddingLeft, 0.0f, (float) (getMeasuredWidth() - WalletActionSheet.this.backgroundPaddingLeft), (float) AndroidUtilities.statusBarHeight, WalletActionSheet.this.backgroundPaint);
            }
        };
        anonymousClass1.setWillNotDraw(false);
        this.containerView = anonymousClass1;
        setApplyTopPadding(false);
        setApplyBottomPadding(false);
        if (this.currentType == 0) {
            this.biometricPromtHelper = new BiometricPromtHelper(this.parentFragment);
        }
        this.listAdapter = new ListAdapter(this, null);
        this.scrollView = new NestedScrollView(context2) {
            private View focusingView;

            public void requestChildFocus(View view, View view2) {
                this.focusingView = view2;
                super.requestChildFocus(view, view2);
            }

            /* Access modifiers changed, original: protected */
            public int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
                if (WalletActionSheet.this.linearLayout.getTop() != getPaddingTop()) {
                    return 0;
                }
                int computeScrollDeltaToGetChildRectOnScreen = super.computeScrollDeltaToGetChildRectOnScreen(rect);
                int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() - (((this.focusingView.getTop() - getScrollY()) + rect.top) + computeScrollDeltaToGetChildRectOnScreen);
                if (currentActionBarHeight > 0) {
                    computeScrollDeltaToGetChildRectOnScreen -= currentActionBarHeight + AndroidUtilities.dp(10.0f);
                }
                return computeScrollDeltaToGetChildRectOnScreen;
            }
        };
        this.scrollView.setClipToPadding(false);
        this.scrollView.setVerticalScrollBarEnabled(false);
        anonymousClass1.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 80.0f));
        this.linearLayout = new LinearLayout(context2);
        this.linearLayout.setOrientation(1);
        this.scrollView.addView(this.linearLayout, LayoutHelper.createScroll(-1, -1, 51));
        this.scrollView.setOnScrollChangeListener(new -$$Lambda$WalletActionSheet$a5_dqjmDEiGVPOlRWmToCx8sXGk(this));
        int itemCount = this.listAdapter.getItemCount();
        int i = 0;
        while (i < itemCount) {
            View createView = this.listAdapter.createView(context2, i);
            this.linearLayout.addView(createView, LayoutHelper.createLinear(-1, -2));
            if (this.currentType == 2 && i == this.commentRow) {
                createView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                createView.setOnClickListener(new -$$Lambda$WalletActionSheet$AG5j6EQhBhWuXx7tIHQgJENWO-8(this));
                createView.setOnLongClickListener(new -$$Lambda$WalletActionSheet$2d-j8Y9GqeCYwQ18vb_GDAMKh28(this));
            }
            i++;
        }
        this.actionBar = new ActionBar(context2) {
            public void setAlpha(float f) {
                super.setAlpha(f);
                WalletActionSheet.this.containerView.invalidate();
            }
        };
        this.actionBar.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.actionBar.setBackButtonImage(NUM);
        String str = "dialogTextBlack";
        this.actionBar.setItemsColor(Theme.getColor(str), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setTitleColor(Theme.getColor(str));
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setAlpha(0.0f);
        itemCount = this.currentType;
        String str2 = "WalletSendGrams";
        String str3 = "WalletCreateInvoiceTitle";
        if (itemCount == 1) {
            this.actionBar.setTitle(LocaleController.getString(str3, NUM));
        } else if (itemCount == 2) {
            this.actionBar.setTitle(LocaleController.getString("WalletTransaction", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString(str2, NUM));
        }
        this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    WalletActionSheet.this.dismiss();
                }
            }
        });
        this.actionBarShadow = new View(context2);
        this.actionBarShadow.setAlpha(0.0f);
        String str4 = "dialogShadowLine";
        this.actionBarShadow.setBackgroundColor(Theme.getColor(str4));
        this.containerView.addView(this.actionBarShadow, LayoutHelper.createFrame(-1, 1.0f));
        this.shadow = new View(context2);
        this.shadow.setBackgroundColor(Theme.getColor(str4));
        this.shadow.setAlpha(0.0f);
        this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 1.0f, 83, 0.0f, 0.0f, 0.0f, 80.0f));
        TextView textView = new TextView(context2);
        textView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        textView.setGravity(17);
        textView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        textView.setTextSize(1, 14.0f);
        int i2 = this.currentType;
        if (i2 == 2) {
            textView.setText(LocaleController.getString("WalletTransactionSendGrams", NUM));
        } else if (i2 == 0) {
            textView.setText(LocaleController.getString(str2, NUM));
        } else {
            textView.setText(LocaleController.getString(str3, NUM));
        }
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        anonymousClass1.addView(textView, LayoutHelper.createFrame(-1, 42.0f, 83, 16.0f, 16.0f, 16.0f, 16.0f));
        textView.setOnClickListener(new -$$Lambda$WalletActionSheet$xqGNVjR3gi9UpYBl6UAcTv4pyHM(this));
    }

    public /* synthetic */ void lambda$init$0$WalletActionSheet(NestedScrollView nestedScrollView, int i, int i2, int i3, int i4) {
        updateLayout(this.inLayout ^ 1);
    }

    public /* synthetic */ void lambda$init$1$WalletActionSheet(View view) {
        AndroidUtilities.addToClipboard(this.commentString);
        Toast.makeText(view.getContext(), LocaleController.getString("TextCopied", NUM), 0).show();
    }

    public /* synthetic */ boolean lambda$init$2$WalletActionSheet(View view) {
        AndroidUtilities.addToClipboard(this.commentString);
        Toast.makeText(view.getContext(), LocaleController.getString("TextCopied", NUM), 0).show();
        return true;
    }

    public /* synthetic */ void lambda$init$4$WalletActionSheet(View view) {
        int i = this.currentType;
        String str = "";
        String str2 = "\n";
        if (i == 2) {
            this.delegate.openSendToAddress(this.recipientString.replace(str2, str));
            dismiss();
        } else if (i == 0) {
            String str3 = this.recipientString;
            if (str3.codePointCount(0, str3.length()) == 48 && TonController.getInstance(this.currentAccount).isValidWalletAddress(this.recipientString)) {
                long j = this.amountValue;
                if (j <= 0 || j > this.currentBalance) {
                    onFieldError(this.amountRow);
                } else if (this.walletAddress.replace(str2, str).equals(this.recipientString.replace(str2, str))) {
                    Builder builder = new Builder(view.getContext());
                    builder.setTitle(LocaleController.getString("Wallet", NUM));
                    builder.setMessage(LocaleController.getString("WalletSendSameWalletText", NUM));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    builder.setPositiveButton(LocaleController.getString("WalletSendSameWalletProceed", NUM), new -$$Lambda$WalletActionSheet$dUyBUjIW6_bB28kLgJU_eFS6gNo(this));
                    builder.show();
                } else {
                    doSend();
                }
            } else {
                onFieldError(this.recipientRow);
            }
        } else if (i == 1) {
            if (this.amountValue <= 0) {
                onFieldError(this.amountRow);
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ton://transfer/");
            stringBuilder.append(this.walletAddress);
            stringBuilder.append("/?amount=");
            stringBuilder.append(this.amountValue);
            String stringBuilder2 = stringBuilder.toString();
            if (!TextUtils.isEmpty(this.commentString)) {
                try {
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(stringBuilder2);
                    stringBuilder3.append("&text=");
                    stringBuilder3.append(URLEncoder.encode(this.commentString, "UTF-8").replaceAll("\\+", "%20"));
                    stringBuilder2 = stringBuilder3.toString();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            dismiss();
            this.delegate.openInvoice(stringBuilder2, this.amountValue);
        }
    }

    public /* synthetic */ void lambda$null$3$WalletActionSheet(DialogInterface dialogInterface, int i) {
        doSend();
    }

    public void setRecipientString(String str, boolean z) {
        this.recipientString = str;
        this.hasWalletInBack = z;
        if (this.scrollView != null) {
            View childAt = this.linearLayout.getChildAt(this.recipientRow);
            if (childAt != null) {
                ListAdapter listAdapter = this.listAdapter;
                int i = this.recipientRow;
                listAdapter.onBindViewHolder(childAt, i, listAdapter.getItemViewType(i));
            }
        }
    }

    public void parseTonUrl(Editable editable, String str) {
        try {
            View childAt;
            Uri parse = Uri.parse(str);
            String path = parse.getPath();
            String queryParameter = parse.getQueryParameter("text");
            str = parse.getQueryParameter("amount");
            if (!TextUtils.isEmpty(path) && path.length() > 1) {
                this.recipientString = path.replace("/", "");
                if (editable == null && this.scrollView != null) {
                    childAt = this.linearLayout.getChildAt(this.recipientRow);
                    if (childAt != null) {
                        this.listAdapter.onBindViewHolder(childAt, this.recipientRow, this.listAdapter.getItemViewType(this.recipientRow));
                    }
                }
            }
            if (!TextUtils.isEmpty(queryParameter)) {
                this.commentString = queryParameter;
                if (this.scrollView != null) {
                    childAt = this.linearLayout.getChildAt(this.commentRow);
                    if (childAt != null) {
                        this.listAdapter.onBindViewHolder(childAt, this.commentRow, this.listAdapter.getItemViewType(this.commentRow));
                    }
                }
            }
            if (!TextUtils.isEmpty(str)) {
                this.amountValue = Utilities.parseLong(str).longValue();
            }
            if (this.scrollView != null) {
                childAt = this.linearLayout.getChildAt(this.amountRow);
                if (childAt != null) {
                    if (!TextUtils.isEmpty(str)) {
                        this.listAdapter.onBindViewHolder(childAt, this.amountRow, this.listAdapter.getItemViewType(this.amountRow));
                    }
                    EditTextBoldCursor textView = ((PollEditTextCell) childAt).getTextView();
                    textView.setSelection(textView.length());
                    textView.requestFocus();
                    AndroidUtilities.showKeyboard(textView);
                }
            }
            if (editable != null) {
                editable.replace(0, editable.length(), this.recipientString);
            }
        } catch (Exception unused) {
        }
    }

    public void onPause() {
        BiometricPromtHelper biometricPromtHelper = this.biometricPromtHelper;
        if (biometricPromtHelper != null) {
            biometricPromtHelper.onPause();
        }
    }

    public void setDelegate(WalletActionSheetDelegate walletActionSheetDelegate) {
        this.delegate = walletActionSheetDelegate;
    }

    private void updateLayout(boolean z) {
        View childAt = this.scrollView.getChildAt(0);
        int top = childAt.getTop() - this.scrollView.getScrollY();
        if (top < 0) {
            top = 0;
        }
        Object obj = top <= 0 ? 1 : null;
        float f = 1.0f;
        if ((obj != null && this.actionBar.getTag() == null) || (obj == null && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(obj != null ? Integer.valueOf(1) : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            if (z) {
                this.actionBarAnimation = new AnimatorSet();
                this.actionBarAnimation.setDuration(180);
                animatorSet = this.actionBarAnimation;
                Animator[] animatorArr = new Animator[2];
                ActionBar actionBar = this.actionBar;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = obj != null ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(actionBar, property, fArr);
                View view = this.actionBarShadow;
                property = View.ALPHA;
                fArr = new float[1];
                fArr[0] = obj != null ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(view, property, fArr);
                animatorSet.playTogether(animatorArr);
                this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        WalletActionSheet.this.actionBarAnimation = null;
                    }
                });
                this.actionBarAnimation.start();
            } else {
                this.actionBar.setAlpha(obj != null ? 1.0f : 0.0f);
                this.actionBarShadow.setAlpha(obj != null ? 1.0f : 0.0f);
            }
        }
        if (this.scrollOffsetY != top) {
            this.scrollOffsetY = top;
            this.containerView.invalidate();
        }
        childAt.getBottom();
        this.scrollView.getMeasuredHeight();
        Object obj2 = childAt.getBottom() - this.scrollView.getScrollY() > this.scrollView.getMeasuredHeight() ? 1 : null;
        if ((obj2 != null && this.shadow.getTag() == null) || (obj2 == null && this.shadow.getTag() != null)) {
            this.shadow.setTag(obj2 != null ? Integer.valueOf(1) : null);
            AnimatorSet animatorSet2 = this.shadowAnimation;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                this.shadowAnimation = null;
            }
            if (z) {
                this.shadowAnimation = new AnimatorSet();
                this.shadowAnimation.setDuration(180);
                animatorSet2 = this.shadowAnimation;
                Animator[] animatorArr2 = new Animator[1];
                View view2 = this.shadow;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                if (obj2 == null) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                animatorArr2[0] = ObjectAnimator.ofFloat(view2, property2, fArr2);
                animatorSet2.playTogether(animatorArr2);
                this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        WalletActionSheet.this.shadowAnimation = null;
                    }
                });
                this.shadowAnimation.start();
                return;
            }
            View view3 = this.shadow;
            if (obj2 == null) {
                f = 0.0f;
            }
            view3.setAlpha(f);
        }
    }

    public void dismiss() {
        AndroidUtilities.hideKeyboard(getCurrentFocus());
        super.dismiss();
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i == 33 && i2 == -1) {
            dismiss();
            this.parentFragment.presentFragment(new WalletPasscodeActivity(false, null, this.walletAddress, this.recipientString, this.amountValue, this.commentString, this.hasWalletInBack));
        }
    }

    private void doSend() {
        AlertDialog alertDialog = new AlertDialog(getContext(), 3);
        alertDialog.setCanCacnel(false);
        alertDialog.show();
        TonController.getInstance(this.currentAccount).getSendFee(this.walletAddress, this.recipientString, this.amountValue, this.commentString, new -$$Lambda$WalletActionSheet$spBmwL4WiIDV783PfgmnfLmK3Mk(this, alertDialog));
    }

    public /* synthetic */ void lambda$doSend$7$WalletActionSheet(AlertDialog alertDialog, long j) {
        int i;
        alertDialog.dismiss();
        Context context = getContext();
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("WalletConfirmation", NUM));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("WalletConfirmationText", NUM, TonController.formatCurrency(this.amountValue))));
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setClipToPadding(false);
        TextView textView = new TextView(context);
        textView.setTextSize(1, 15.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        textView.setGravity(17);
        textView.setPadding(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(9.0f));
        textView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor("wallet_addressConfirmBackground")));
        StringBuilder stringBuilder = new StringBuilder(this.recipientString);
        stringBuilder.insert(stringBuilder.length() / 2, " \n ");
        String str = " ";
        stringBuilder.insert(0, str);
        stringBuilder.insert(stringBuilder.length(), str);
        textView.setText(stringBuilder);
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2, 1));
        if (j > 0) {
            textView = new TextView(context);
            textView.setTextSize(1, 13.0f);
            textView.setTextColor(Theme.getColor("dialogTextGray3"));
            textView.setGravity(17);
            textView.setText(LocaleController.formatString("WalletFee", NUM, TonController.formatCurrency(j)));
            frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 67.0f, 0.0f, 0.0f));
            i = 98;
        } else {
            i = 64;
        }
        builder.setView(frameLayout, i);
        builder.setPositiveButton(LocaleController.getString("WalletConfirm", NUM).toUpperCase(), new -$$Lambda$WalletActionSheet$pmz2h485JXLJcxJWRK8i7FUTZrE(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        builder.show();
    }

    public /* synthetic */ void lambda$null$6$WalletActionSheet(DialogInterface dialogInterface, int i) {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            int keyProtectionType = TonController.getInstance(this.currentAccount).getKeyProtectionType();
            if (keyProtectionType != 0) {
                String str = "WalletSendConfirmCredentials";
                if (keyProtectionType != 1) {
                    if (keyProtectionType == 2) {
                        this.biometricPromtHelper.promtWithCipher(TonController.getInstance(this.currentAccount).getCipherForDecrypt(), LocaleController.getString(str, NUM), new -$$Lambda$WalletActionSheet$YjkN6s-q6qf1_9swBcxBU2ISDJM(this));
                        return;
                    }
                    return;
                } else if (VERSION.SDK_INT >= 23) {
                    this.parentFragment.getParentActivity().startActivityForResult(((KeyguardManager) ApplicationLoader.applicationContext.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(LocaleController.getString("Wallet", NUM), LocaleController.getString(str, NUM)), 33);
                    return;
                } else {
                    return;
                }
            }
            AndroidUtilities.hideKeyboard(getCurrentFocus());
            this.parentFragment.presentFragment(new WalletPasscodeActivity(true, null, this.walletAddress, this.recipientString, this.amountValue, this.commentString, this.hasWalletInBack));
            dismiss();
        }
    }

    public /* synthetic */ void lambda$null$5$WalletActionSheet(Cipher cipher) {
        dismiss();
        this.parentFragment.presentFragment(new WalletPasscodeActivity(false, cipher, this.walletAddress, this.recipientString, this.amountValue, this.commentString, this.hasWalletInBack));
    }

    private void updateRows() {
        this.rowCount = 0;
        this.recipientHeaderRow = -1;
        this.recipientRow = -1;
        this.amountHeaderRow = -1;
        this.amountRow = -1;
        this.commentRow = -1;
        this.commentHeaderRow = -1;
        this.balanceRow = -1;
        this.dateRow = -1;
        this.invoiceInfoRow = -1;
        this.dateHeaderRow = -1;
        this.sendBalanceRow = -1;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.titleRow = i;
        i = this.currentType;
        if (i == 1) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.invoiceInfoRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.amountHeaderRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.amountRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.commentRow = i;
        } else if (i == 2) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.balanceRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.recipientHeaderRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.recipientRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.dateHeaderRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.dateRow = i;
            if (!TextUtils.isEmpty(this.commentString)) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.commentHeaderRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.commentRow = i;
            }
        } else {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.recipientHeaderRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.recipientRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.amountHeaderRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.amountRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.sendBalanceRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.commentRow = i;
        }
    }

    private void onFieldError(int i) {
        View childAt = this.linearLayout.getChildAt(i);
        if (childAt != null) {
            AndroidUtilities.shakeView(childAt, 2.0f, 0);
            try {
                Vibrator vibrator = (Vibrator) this.parentFragment.getParentActivity().getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private void setTextLeft(View view) {
        int i = this.currentType;
        if (i != 2) {
            Object obj = "windowBackgroundWhiteRedText5";
            if (view instanceof PollEditTextCell) {
                PollEditTextCell pollEditTextCell = (PollEditTextCell) view;
                i = 500 - this.commentString.getBytes().length;
                if (((float) i) <= 150.0f) {
                    String str;
                    pollEditTextCell.setText2(String.format("%d", new Object[]{Integer.valueOf(i)}));
                    SimpleTextView textView2 = pollEditTextCell.getTextView2();
                    if (i >= 0) {
                        str = "windowBackgroundWhiteGrayText3";
                    }
                    textView2.setTextColor(Theme.getColor(str));
                    textView2.setTag(str);
                } else {
                    pollEditTextCell.setText2("");
                }
            } else if (view instanceof TextInfoPrivacyCell) {
                long j = this.currentBalance;
                if (j >= 0 && i == 0) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) view;
                    if (this.amountValue <= j) {
                        obj = "windowBackgroundWhiteBlueHeader";
                    }
                    textInfoPrivacyCell.getTextView().setTag(obj);
                    textInfoPrivacyCell.getTextView().setTextColor(Theme.getColor(obj));
                }
            }
        }
    }
}
