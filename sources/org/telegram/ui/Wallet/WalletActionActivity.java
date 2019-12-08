package org.telegram.ui.Wallet;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.net.URLEncoder;
import javax.crypto.Cipher;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MrzRecognizer.Result;
import org.telegram.messenger.TonController;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.CameraScanActivity;
import org.telegram.ui.CameraScanActivity.CameraScanActivityDelegate;
import org.telegram.ui.CameraScanActivity.CameraScanActivityDelegate.-CC;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BiometricPromtHelper;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.ShareAlert;

public class WalletActionActivity extends BaseFragment {
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 34;
    private static final int MAX_COMMENT_LENGTH = 500;
    public static final int SEND_ACTIVITY_RESULT_CODE = 33;
    public static final int TYPE_INVOICE = 1;
    public static final int TYPE_SEND = 0;
    private int amountHeaderRow;
    private int amountInfoRow;
    private int amountRow;
    private long amountValue;
    private BiometricPromtHelper biometricPromtHelper;
    private int commentRow;
    private String commentString;
    private long currentBalance = -1;
    private long currentDate;
    private int currentType;
    private TextView doneItem;
    private boolean hasWalletInBack = true;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private InvoiceQrCell qrCell;
    private int qrRow;
    private int recipientHeaderRow;
    private int recipientInfoRow;
    private int recipientRow;
    private String recipientString;
    private int rowCount;
    private int shareInfoRow;
    private int shareRow;
    private String walletAddress;
    private boolean wasFirstAttach;

    private class InvoiceQrCell extends FrameLayout {
        private ImageView copyButton;
        private ImageView imageView;
        private Bitmap qrBitmap;
        private TextView textView;
        private Runnable updateRunnable;

        public InvoiceQrCell(Context context) {
            super(context);
            setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            setWillNotDraw(false);
            this.imageView = new ImageView(context);
            addView(this.imageView, LayoutHelper.createFrame(160, 160.0f, 49, 0.0f, 14.0f, 0.0f, 0.0f));
            this.imageView.setOnLongClickListener(new -$$Lambda$WalletActionActivity$InvoiceQrCell$vy7pNmYgA6PHYRU4jNs6aNTJ_J0(this));
            this.textView = new TextView(context);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            this.textView.setTextSize(1, 16.0f);
            int i = 5;
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 80.0f : 21.0f, 180.0f, LocaleController.isRTL ? 21.0f : 80.0f, 17.0f));
            this.copyButton = new ImageView(context);
            this.copyButton.setImageResource(NUM);
            this.copyButton.setScaleType(ScaleType.CENTER);
            this.copyButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlueHeader"), Mode.MULTIPLY));
            this.copyButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("actionBarWhiteSelector"), 6));
            ImageView imageView = this.copyButton;
            if (LocaleController.isRTL) {
                i = 3;
            }
            addView(imageView, LayoutHelper.createFrame(48, 48.0f, i | 48, 4.0f, 180.0f, 4.0f, 0.0f));
            this.copyButton.setOnClickListener(new -$$Lambda$WalletActionActivity$InvoiceQrCell$B2fNad_SczSr8uhdS7Kzhipf7q8(this));
            update();
        }

        public /* synthetic */ boolean lambda$new$0$WalletActionActivity$InvoiceQrCell(View view) {
            TonController.shareBitmap(WalletActionActivity.this.getParentActivity(), view, this.textView.getText().toString());
            return true;
        }

        public /* synthetic */ void lambda$new$1$WalletActionActivity$InvoiceQrCell(View view) {
            AndroidUtilities.addToClipboard(getTonUrl());
            Toast.makeText(WalletActionActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }

        private String getTonUrl() {
            StringBuilder stringBuilder;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("ton://transfer/");
            stringBuilder2.append(WalletActionActivity.this.walletAddress);
            String stringBuilder3 = stringBuilder2.toString();
            if (WalletActionActivity.this.amountValue != 0) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(stringBuilder3);
                stringBuilder.append("/?amount=");
                stringBuilder.append(WalletActionActivity.this.amountValue);
                stringBuilder3 = stringBuilder.toString();
            }
            if (TextUtils.isEmpty(WalletActionActivity.this.commentString)) {
                return stringBuilder3;
            }
            try {
                stringBuilder = new StringBuilder();
                stringBuilder.append(stringBuilder3);
                stringBuilder.append(WalletActionActivity.this.amountValue != 0 ? "&" : "/?");
                stringBuilder.append("text=");
                stringBuilder.append(URLEncoder.encode(WalletActionActivity.this.commentString, "UTF-8"));
                return stringBuilder.toString();
            } catch (Exception e) {
                FileLog.e(e);
                return stringBuilder3;
            }
        }

        public void update() {
            String tonUrl = getTonUrl();
            this.qrBitmap = WalletActionActivity.this.getTonController().createTonQR(getContext(), tonUrl, this.qrBitmap);
            this.imageView.setImageBitmap(this.qrBitmap);
            this.textView.setText(tonUrl);
        }

        public void scheduleUpdate() {
            if (this.updateRunnable != null) {
                Utilities.globalQueue.cancelRunnable(this.updateRunnable);
            }
            String tonUrl = getTonUrl();
            this.textView.setText(tonUrl);
            DispatchQueue dispatchQueue = Utilities.globalQueue;
            -$$Lambda$WalletActionActivity$InvoiceQrCell$c0rob2d5Wg1-JQ8mNUMecnDZpZx4 -__lambda_walletactionactivity_invoiceqrcell_c0rob2d5wg1-jq8mNUMecndzpzx4 = new -$$Lambda$WalletActionActivity$InvoiceQrCell$c0rob2d5Wg1-JQ8mNUMecnDZpZx4(this, tonUrl);
            this.updateRunnable = -__lambda_walletactionactivity_invoiceqrcell_c0rob2d5wg1-jq8mNUMecndzpzx4;
            dispatchQueue.postRunnable(-__lambda_walletactionactivity_invoiceqrcell_c0rob2d5wg1-jq8mNUMecndzpzx4, 500);
        }

        public /* synthetic */ void lambda$scheduleUpdate$3$WalletActionActivity$InvoiceQrCell(String str) {
            this.qrBitmap = WalletActionActivity.this.getTonController().createTonQR(getContext(), str, this.qrBitmap);
            AndroidUtilities.runOnUIThread(new -$$Lambda$WalletActionActivity$InvoiceQrCell$Cja93mwjFKY24thR8BabjaOoJTU(this));
        }

        public /* synthetic */ void lambda$null$2$WalletActionActivity$InvoiceQrCell() {
            this.updateRunnable = null;
            this.imageView.setImageBitmap(this.qrBitmap);
        }
    }

    private class SendAddressCell extends PollEditTextCell {
        private ImageView copyButton;
        private ImageView qrButton;
        final /* synthetic */ WalletActionActivity this$0;

        public SendAddressCell(WalletActionActivity walletActionActivity, Context context) {
            final WalletActionActivity walletActionActivity2 = walletActionActivity;
            Context context2 = context;
            this.this$0 = walletActionActivity2;
            super(context2, null);
            setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
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
                        SendAddressCell.this.this$0.checkDoneButton(true);
                    }
                }
            });
            String str = "actionBarWhiteSelector";
            String str2 = "windowBackgroundWhiteBlueHeader";
            ImageView imageView;
            if (walletActionActivity.currentType == 0) {
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
                this.qrButton.setOnClickListener(new -$$Lambda$WalletActionActivity$SendAddressCell$WRstc9z9HqLL-yxshrNxWUmeRRI(this));
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
            addView(imageView, LayoutHelper.createFrame(48, 48.0f, i2 | 48, 6.0f, 0.0f, 6.0f, 0.0f));
            this.copyButton.setOnClickListener(new -$$Lambda$WalletActionActivity$SendAddressCell$Z-iL8V6BeDcIFsNJO2xKa-u-iRI(this));
        }

        public /* synthetic */ void lambda$new$0$WalletActionActivity$SendAddressCell(View view) {
            if (VERSION.SDK_INT >= 23) {
                if (this.this$0.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                    this.this$0.getParentActivity().requestPermissions(new String[]{r0}, 34);
                    return;
                }
            }
            this.this$0.openQrReader();
        }

        public /* synthetic */ void lambda$new$1$WalletActionActivity$SendAddressCell(View view) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ton://transfer/");
            stringBuilder.append(this.this$0.recipientString.replace("\n", ""));
            AndroidUtilities.addToClipboard(stringBuilder.toString());
            Toast.makeText(this.this$0.getParentActivity(), LocaleController.getString("WalletTransactionAddressCopied", NUM), 0).show();
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            ImageView imageView = this.qrButton;
            if (imageView != null) {
                measureChildWithMargins(imageView, i, 0, i2, 0);
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return WalletActionActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            HeaderCell headerCell;
            if (itemViewType == 0) {
                headerCell = (HeaderCell) viewHolder.itemView;
                if (i == WalletActionActivity.this.recipientHeaderRow) {
                    headerCell.setText(LocaleController.getString("WalletSendRecipient", NUM));
                }
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                textInfoPrivacyCell.setFixedSize(0);
                String str = "windowBackgroundGrayShadow";
                int i2 = NUM;
                if (i == WalletActionActivity.this.amountInfoRow) {
                    if (WalletActionActivity.this.currentType == 1) {
                        textInfoPrivacyCell.setText(LocaleController.getString("WalletInvoiceInfo", NUM));
                    } else {
                        textInfoPrivacyCell.setText(null);
                    }
                    Context context = this.mContext;
                    if (WalletActionActivity.this.currentType == 1) {
                        i2 = NUM;
                    }
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context, i2, str));
                } else if (i == WalletActionActivity.this.shareInfoRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("WalletShareInvoiceUrlInfo", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                } else if (i == WalletActionActivity.this.recipientInfoRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("WalletAddressCopy", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                }
            } else if (itemViewType == 2) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                if (i == WalletActionActivity.this.shareRow) {
                    textSettingsCell.setText(LocaleController.getString("WalletShareInvoiceUrl", NUM), false);
                }
            } else if (itemViewType == 3) {
                ((PollEditTextCell) viewHolder.itemView).setTextAndHint(WalletActionActivity.this.commentString, LocaleController.getString("WalletComment", NUM), false);
            } else if (itemViewType == 4) {
                String str2;
                PollEditTextCell pollEditTextCell = (PollEditTextCell) viewHolder.itemView;
                if (WalletActionActivity.this.currentType == 0) {
                    i = NUM;
                    str2 = "WalletGramsToSend";
                } else {
                    i = NUM;
                    str2 = "WalletGramsToReceive";
                }
                pollEditTextCell.setTextAndHint(WalletActionActivity.this.amountValue != 0 ? TonController.formatCurrency(WalletActionActivity.this.amountValue) : "", LocaleController.getString(str2, i), true);
            } else if (itemViewType == 6) {
                ((SendAddressCell) viewHolder.itemView).setTextAndHint(WalletActionActivity.this.recipientString, LocaleController.getString("WalletEnterWalletAddress", NUM), false);
            } else if (itemViewType == 7) {
                headerCell = (HeaderCell) viewHolder.itemView;
                if (i == WalletActionActivity.this.amountHeaderRow) {
                    headerCell.setText(LocaleController.getString("WalletAmount", NUM));
                    if (WalletActionActivity.this.currentType == 0 && WalletActionActivity.this.getTonController().getCachedAccountState() != null) {
                        headerCell.setText2(LocaleController.formatString("WalletSendBalance", NUM, TonController.formatCurrency(WalletActionActivity.this.currentBalance = TonController.getBalance(WalletActionActivity.this.getTonController().getCachedAccountState()))));
                        headerCell.getTextView2().setRightDrawable(NUM);
                    }
                }
            }
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 7 || itemViewType == 3) {
                WalletActionActivity.this.setTextLeft(viewHolder.itemView);
            } else if (itemViewType == 4 && !WalletActionActivity.this.wasFirstAttach) {
                if (WalletActionActivity.this.recipientString.codePointCount(0, WalletActionActivity.this.recipientString.length()) == 48) {
                    ((PollEditTextCell) viewHolder.itemView).getTextView().requestFocus();
                }
                WalletActionActivity.this.wasFirstAttach = true;
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() == WalletActionActivity.this.shareRow;
        }

        public /* synthetic */ boolean lambda$onCreateViewHolder$0$WalletActionActivity$ListAdapter(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            ViewHolder findViewHolderForAdapterPosition = WalletActionActivity.this.listView.findViewHolderForAdapterPosition(WalletActionActivity.this.commentRow);
            if (findViewHolderForAdapterPosition != null) {
                ((PollEditTextCell) findViewHolderForAdapterPosition.itemView).getTextView().requestFocus();
            }
            return true;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textInfoPrivacyCell;
            final View pollEditTextCell;
            String str = "windowBackgroundWhite";
            View headerCell;
            switch (i) {
                case 0:
                    headerCell = new HeaderCell(this.mContext, false, 21, 15, false);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 1:
                    textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    textInfoPrivacyCell = new TextSettingsCell(this.mContext);
                    textInfoPrivacyCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 3:
                    pollEditTextCell = new PollEditTextCell(this.mContext, null);
                    pollEditTextCell.createErrorTextView();
                    pollEditTextCell.setBackgroundColor(Theme.getColor(str));
                    pollEditTextCell.getTextView().setFilters(new InputFilter[]{new LengthFilter(500)});
                    pollEditTextCell.addTextWatcher(new TextWatcher() {
                        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        public void afterTextChanged(Editable editable) {
                            if (pollEditTextCell.getTag() == null) {
                                WalletActionActivity.this.commentString = editable.toString();
                                ViewHolder findViewHolderForAdapterPosition = WalletActionActivity.this.listView.findViewHolderForAdapterPosition(WalletActionActivity.this.commentRow);
                                if (findViewHolderForAdapterPosition != null) {
                                    WalletActionActivity.this.setTextLeft(findViewHolderForAdapterPosition.itemView);
                                }
                                WalletActionActivity.this.checkDoneButton(true);
                                if (WalletActionActivity.this.qrCell != null) {
                                    WalletActionActivity.this.qrCell.scheduleUpdate();
                                }
                            }
                        }
                    });
                    break;
                case 4:
                    pollEditTextCell = new PollEditTextCell(this.mContext, null) {
                        /* Access modifiers changed, original: protected */
                        public boolean drawDivider() {
                            return WalletActionActivity.this.commentRow != -1;
                        }
                    };
                    pollEditTextCell.setBackgroundColor(Theme.getColor(str));
                    final EditTextBoldCursor textView = pollEditTextCell.getTextView();
                    pollEditTextCell.setShowNextButton(true);
                    textView.setImeOptions(textView.getImeOptions() | 5);
                    textView.setInputType(3);
                    textView.setOnEditorActionListener(new -$$Lambda$WalletActionActivity$ListAdapter$f0Hi0Z6Xpx_UzAvAcpeR0yOl87E(this));
                    pollEditTextCell.addTextWatcher(new TextWatcher() {
                        private boolean adding;
                        private boolean ignoreTextChange;

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
                                    String charSequence2 = editable.subSequence(i + 1, editable.length()).toString();
                                    WalletActionActivity walletActionActivity = WalletActionActivity.this;
                                    long longValue = Utilities.parseLong(charSequence).longValue() * NUM;
                                    double longValue2 = (double) Utilities.parseLong(charSequence2).longValue();
                                    double pow = Math.pow(10.0d, (double) (9 - charSequence2.length()));
                                    Double.isNaN(longValue2);
                                    walletActionActivity.amountValue = longValue + ((long) ((int) (longValue2 * pow)));
                                } else {
                                    if (editable.length() > 9) {
                                        editable.delete(9, editable.length());
                                    }
                                    WalletActionActivity.this.amountValue = Utilities.parseLong(editable.toString()).longValue() * NUM;
                                }
                                this.ignoreTextChange = false;
                                if (WalletActionActivity.this.qrCell != null) {
                                    WalletActionActivity.this.qrCell.scheduleUpdate();
                                }
                                WalletActionActivity.this.checkDoneButton(true);
                                ViewHolder findViewHolderForAdapterPosition = WalletActionActivity.this.listView.findViewHolderForAdapterPosition(WalletActionActivity.this.amountHeaderRow);
                                if (findViewHolderForAdapterPosition != null) {
                                    WalletActionActivity.this.setTextLeft(findViewHolderForAdapterPosition.itemView);
                                }
                            }
                        }
                    });
                    break;
                case 5:
                    textInfoPrivacyCell = WalletActionActivity.this.qrCell;
                    break;
                case 6:
                    textInfoPrivacyCell = new SendAddressCell(WalletActionActivity.this, this.mContext);
                    break;
                default:
                    headerCell = new HeaderCell(this.mContext, false, 21, 15, true);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    headerCell.getTextView2().setTextColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
                    break;
            }
            textInfoPrivacyCell = pollEditTextCell;
            textInfoPrivacyCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(textInfoPrivacyCell);
        }

        public int getItemViewType(int i) {
            if (i == WalletActionActivity.this.recipientHeaderRow) {
                return 0;
            }
            if (i == WalletActionActivity.this.amountInfoRow || i == WalletActionActivity.this.shareInfoRow || i == WalletActionActivity.this.recipientInfoRow) {
                return 1;
            }
            if (i == WalletActionActivity.this.shareRow) {
                return 2;
            }
            if (i == WalletActionActivity.this.commentRow) {
                return 3;
            }
            if (i == WalletActionActivity.this.amountRow) {
                return 4;
            }
            if (i == WalletActionActivity.this.qrRow) {
                return 5;
            }
            return i == WalletActionActivity.this.recipientRow ? 6 : 7;
        }
    }

    public WalletActionActivity(int i, String str) {
        String str2 = "";
        this.commentString = str2;
        this.recipientString = str2;
        this.walletAddress = str;
        if (this.walletAddress == null) {
            this.walletAddress = getTonController().getWalletAddress(getUserConfig().tonPublicKey);
        }
        this.currentType = i;
    }

    public void setRecipientString(String str, boolean z) {
        this.recipientString = str;
        this.hasWalletInBack = z;
    }

    private void parseTonUrl(Editable editable, String str) {
        try {
            ViewHolder findViewHolderForAdapterPosition;
            Uri parse = Uri.parse(str);
            String path = parse.getPath();
            String queryParameter = parse.getQueryParameter("text");
            str = parse.getQueryParameter("amount");
            if (!TextUtils.isEmpty(path) && path.length() > 1) {
                this.recipientString = path.replace("/", "");
                if (editable == null && this.listView != null) {
                    findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.recipientRow);
                    if (findViewHolderForAdapterPosition != null) {
                        this.listAdapter.onBindViewHolder(findViewHolderForAdapterPosition, this.recipientRow);
                    }
                }
            }
            if (!TextUtils.isEmpty(queryParameter)) {
                this.commentString = queryParameter;
                if (this.listView != null) {
                    findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.commentRow);
                    if (findViewHolderForAdapterPosition != null) {
                        this.listAdapter.onBindViewHolder(findViewHolderForAdapterPosition, this.commentRow);
                    }
                }
            }
            if (!TextUtils.isEmpty(str)) {
                this.amountValue = Utilities.parseLong(str).longValue();
                if (this.listView != null) {
                    ViewHolder findViewHolderForAdapterPosition2 = this.listView.findViewHolderForAdapterPosition(this.amountRow);
                    if (findViewHolderForAdapterPosition2 != null) {
                        this.listAdapter.onBindViewHolder(findViewHolderForAdapterPosition2, this.amountRow);
                    }
                }
            }
            if (editable != null) {
                editable.replace(0, editable.length(), this.recipientString);
            }
        } catch (Exception unused) {
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    /* Access modifiers changed, original: protected */
    public ActionBar createActionBar(Context context) {
        AnonymousClass1 anonymousClass1 = new ActionBar(context) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                if (WalletActionActivity.this.doneItem != null) {
                    ((FrameLayout.LayoutParams) WalletActionActivity.this.doneItem.getLayoutParams()).topMargin = (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(34.0f)) / 2);
                }
                super.onMeasure(i, i2);
            }
        };
        anonymousClass1.setBackButtonImage(NUM);
        anonymousClass1.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        anonymousClass1.setTitleColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        anonymousClass1.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        anonymousClass1.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
        if (this.currentType == 1) {
            anonymousClass1.setTitle(LocaleController.getString("WalletCreateInvoiceTitle", NUM));
        } else {
            anonymousClass1.setTitle(LocaleController.getString("WalletSendGrams", NUM));
        }
        if (AndroidUtilities.isTablet()) {
            anonymousClass1.setOccupyStatusBar(false);
        }
        anonymousClass1.setAllowOverlayTitle(false);
        anonymousClass1.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    WalletActionActivity.this.finishFragment();
                }
            }
        });
        return anonymousClass1;
    }

    public View createView(Context context) {
        if (this.currentType == 0) {
            this.biometricPromtHelper = new BiometricPromtHelper(this);
            this.doneItem = new TextView(context);
            this.doneItem.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(17.0f), 0);
            this.doneItem.setGravity(17);
            this.doneItem.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            this.doneItem.setTextSize(1, 14.0f);
            this.doneItem.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.doneItem.setText(LocaleController.getString("Done", NUM));
            this.doneItem.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
            this.actionBar.addView(this.doneItem, LayoutHelper.createFrame(-2, 34.0f, 53, 0.0f, 0.0f, 11.0f, 0.0f));
            this.doneItem.setOnClickListener(new -$$Lambda$WalletActionActivity$TU2mLGaT45tlo0n7TuGI4Z8BjHY(this));
        } else {
            this.qrCell = new InvoiceQrCell(context);
        }
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setFocusable(true);
        frameLayout.setFocusableInTouchMode(true);
        this.listView = new RecyclerListView(context) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
            }

            public void requestLayout() {
                super.requestLayout();
            }
        };
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setGlowColor(Theme.getColor("windowBackgroundWhite"));
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new -$$Lambda$WalletActionActivity$MjK39ndYLabUpg_ATSrMnM27OR4(this));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(WalletActionActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        checkDoneButton(false);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$WalletActionActivity(View view) {
        String str = "";
        String str2 = "\n";
        if (this.walletAddress.replace(str2, str).equals(this.recipientString.replace(str2, str))) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("Wallet", NUM));
            builder.setMessage(LocaleController.getString("WalletSendSameWalletText", NUM));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            builder.setPositiveButton(LocaleController.getString("WalletSendSameWalletProceed", NUM), new -$$Lambda$WalletActionActivity$TXuiHTFe3sFms6NI0xIHqlyNdKM(this));
            showDialog(builder.show());
            return;
        }
        doSend();
    }

    public /* synthetic */ void lambda$null$0$WalletActionActivity(DialogInterface dialogInterface, int i) {
        doSend();
    }

    public /* synthetic */ void lambda$createView$2$WalletActionActivity(View view, int i) {
        if (i == this.shareRow) {
            String charSequence = this.qrCell.textView.getText().toString();
            ShareAlert shareAlert = new ShareAlert(getParentActivity(), null, charSequence, false, charSequence, false);
            shareAlert.setDelegate(new -$$Lambda$ds-XYhaTvNG1g_dlfbEiPZv8_Uk(this));
            showDialog(shareAlert);
        }
    }

    public void onPause() {
        super.onPause();
        BiometricPromtHelper biometricPromtHelper = this.biometricPromtHelper;
        if (biometricPromtHelper != null) {
            biometricPromtHelper.onPause();
        }
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$WalletActionActivity$eIyUokdo_IaLwOkcjBNWLXrBnLY(this), 100);
        }
    }

    public /* synthetic */ void lambda$onTransitionAnimationEnd$3$WalletActionActivity() {
        if (this.listView != null) {
            int i;
            if (this.currentType == 1) {
                i = this.amountRow;
            } else {
                String str = this.recipientString;
                i = str.codePointCount(0, str.length()) == 48 ? this.amountRow : this.recipientRow;
            }
            ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
            if (findViewHolderForAdapterPosition != null) {
                EditTextBoldCursor textView = ((PollEditTextCell) findViewHolderForAdapterPosition.itemView).getTextView();
                textView.requestFocus();
                AndroidUtilities.showKeyboard(textView);
            }
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i == 33 && i2 == -1) {
            presentFragment(new WalletPasscodeActivity(false, null, this.walletAddress, this.recipientString, this.amountValue, this.commentString, this.hasWalletInBack), true);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 34) {
            if (iArr.length > 0 && iArr[0] == 0) {
                openQrReader();
            } else if (getParentActivity() != null) {
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setMessage(LocaleController.getString("WalletPermissionNoCamera", NUM));
                builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$WalletActionActivity$E4kcVRY2Rtm2EcN4CTGx9QA_bw8(this));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                builder.show();
            }
        }
    }

    public /* synthetic */ void lambda$onRequestPermissionsResultFragment$4$WalletActionActivity(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("package:");
            stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
            intent.setData(Uri.parse(stringBuilder.toString()));
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void doSend() {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("WalletConfirmation", NUM));
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("WalletConfirmationText", NUM, TonController.formatCurrency(this.amountValue))));
            FrameLayout frameLayout = new FrameLayout(getParentActivity());
            TextView textView = new TextView(getParentActivity());
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
            frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2, 17));
            MarginLayoutParams marginLayoutParams = new MarginLayoutParams(-1, -2);
            marginLayoutParams.bottomMargin = AndroidUtilities.dp(6.0f);
            frameLayout.setLayoutParams(marginLayoutParams);
            frameLayout.setClipToPadding(false);
            builder.setView(frameLayout);
            builder.setPositiveButton(LocaleController.getString("WalletConfirm", NUM).toUpperCase(), new -$$Lambda$WalletActionActivity$p6-tWLJB5mP4I29Zoa1Q6nq6eCU(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$doSend$6$WalletActionActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            int keyProtectionType = getTonController().getKeyProtectionType();
            if (keyProtectionType != 0) {
                String str = "WalletSendConfirmCredentials";
                if (keyProtectionType != 1) {
                    if (keyProtectionType == 2) {
                        this.biometricPromtHelper.promtWithCipher(getTonController().getCipherForDecrypt(), LocaleController.getString(str, NUM), new -$$Lambda$WalletActionActivity$OQogbZLi-v4tUK279KmMT8AUlQE(this));
                    }
                } else if (VERSION.SDK_INT >= 23) {
                    getParentActivity().startActivityForResult(((KeyguardManager) ApplicationLoader.applicationContext.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(LocaleController.getString("Wallet", NUM), LocaleController.getString(str, NUM)), 33);
                }
            } else {
                presentFragment(new WalletPasscodeActivity(true, null, this.walletAddress, this.recipientString, this.amountValue, this.commentString, this.hasWalletInBack), true);
            }
        }
    }

    public /* synthetic */ void lambda$null$5$WalletActionActivity(Cipher cipher) {
        presentFragment(new WalletPasscodeActivity(false, cipher, this.walletAddress, this.recipientString, this.amountValue, this.commentString, this.hasWalletInBack), true);
    }

    private void openQrReader() {
        CameraScanActivity cameraScanActivity = new CameraScanActivity(1);
        cameraScanActivity.setDelegate(new CameraScanActivityDelegate() {
            public /* synthetic */ void didFindMrzInfo(Result result) {
                -CC.$default$didFindMrzInfo(this, result);
            }

            public void didFindQr(String str) {
                WalletActionActivity.this.parseTonUrl(null, str);
            }
        });
        presentFragment(cameraScanActivity);
    }

    private void checkDoneButton(boolean z) {
        if (this.doneItem != null) {
            boolean z2 = false;
            if (!this.wasFirstAttach) {
                z = false;
            }
            String str = this.recipientString;
            int codePointCount = str.codePointCount(0, str.length());
            long j = this.amountValue;
            if (j != 0) {
                long j2 = this.currentBalance;
                if ((j2 == -1 || j <= j2) && codePointCount == 48 && getTonController().isValidWalletAddress(this.recipientString)) {
                    z2 = true;
                }
            }
            this.doneItem.setEnabled(z2);
            float f = 1.0f;
            if (z) {
                ViewPropertyAnimator scaleY = this.doneItem.animate().scaleX(z2 ? 1.0f : 0.0f).scaleY(z2 ? 1.0f : 0.0f);
                if (!z2) {
                    f = 0.0f;
                }
                scaleY.alpha(f).setDuration(180).start();
            } else {
                this.doneItem.animate().cancel();
                this.doneItem.setScaleX(z2 ? 1.0f : 0.0f);
                this.doneItem.setScaleY(z2 ? 1.0f : 0.0f);
                TextView textView = this.doneItem;
                if (!z2) {
                    f = 0.0f;
                }
                textView.setAlpha(f);
            }
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        this.recipientHeaderRow = -1;
        this.recipientRow = -1;
        this.recipientInfoRow = -1;
        this.amountHeaderRow = -1;
        this.amountRow = -1;
        this.commentRow = -1;
        this.amountInfoRow = -1;
        this.qrRow = -1;
        this.shareRow = -1;
        this.shareInfoRow = -1;
        int i;
        if (this.currentType == 1) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.amountHeaderRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.amountRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.commentRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.amountInfoRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.qrRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.shareRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.shareInfoRow = i;
            return;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.recipientHeaderRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.recipientRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.recipientInfoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.amountHeaderRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.amountRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.commentRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.amountInfoRow = i;
    }

    private void setTextLeft(View view) {
        Object obj = "windowBackgroundWhiteRedText5";
        if (view instanceof PollEditTextCell) {
            PollEditTextCell pollEditTextCell = (PollEditTextCell) view;
            int length = 500 - this.commentString.length();
            if (((float) length) <= 150.0f) {
                String str;
                pollEditTextCell.setText2(String.format("%d", new Object[]{Integer.valueOf(length)}));
                SimpleTextView textView2 = pollEditTextCell.getTextView2();
                if (length >= 0) {
                    str = "windowBackgroundWhiteGrayText3";
                }
                textView2.setTextColor(Theme.getColor(str));
                textView2.setTag(str);
                return;
            }
            pollEditTextCell.setText2("");
        } else if (view instanceof HeaderCell) {
            long j = this.currentBalance;
            if (j >= 0 && this.currentType == 0) {
                HeaderCell headerCell = (HeaderCell) view;
                if (this.amountValue <= j) {
                    obj = "windowBackgroundWhiteBlueHeader";
                }
                headerCell.getTextView2().setTag(obj);
                headerCell.getTextView2().setTextColor(Theme.getColor(obj));
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[19];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextSettingsCell.class, PollEditTextCell.class}, null, null, null, "windowBackgroundWhite");
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        r1[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        r1[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "windowBackgroundWhite");
        r1[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2");
        r1[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarWhiteSelector");
        View view = this.listView;
        Class[] clsArr = new Class[]{HeaderCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        r1[7] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueHeader");
        view = this.listView;
        int i = ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{HeaderCell.class};
        strArr = new String[1];
        strArr[0] = "textView2";
        r1[8] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueHeader");
        r1[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, null, null, null, "windowBackgroundWhiteRedText5");
        r1[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r1[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteHintText");
        view = this.listView;
        i = ThemeDescription.FLAG_HINTTEXTCOLOR;
        clsArr = new Class[]{PollEditTextCell.class};
        strArr = new String[1];
        strArr[0] = "deleteImageView";
        r1[12] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText");
        r1[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{PollEditTextCell.class}, new String[]{"deleteImageView"}, null, null, null, "stickers_menuSelector");
        r1[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{PollEditTextCell.class}, new String[]{"textView2"}, null, null, null, "windowBackgroundWhiteRedText5");
        r1[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{PollEditTextCell.class}, new String[]{"textView2"}, null, null, null, "windowBackgroundWhiteGrayText3");
        r1[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r1[17] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r1[18] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteHintText");
        return r1;
    }
}
