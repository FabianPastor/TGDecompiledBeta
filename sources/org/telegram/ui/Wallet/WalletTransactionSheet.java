package org.telegram.ui.Wallet;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.TonController;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.TypefaceSpan;

public class WalletTransactionSheet extends BottomSheet {
    private static final int MAX_COMMENT_LENGTH = 128;
    private int amountInfoRow;
    private long amountValue;
    private int balanceRow;
    private int commentHeaderRow;
    private int commentRow;
    private String commentString;
    private long currentBalance = -1;
    private long currentDate;
    private long currentStorageFee;
    private long currentTransactionFee;
    private boolean hasWalletInBack = true;
    private RecyclerListView listView;
    private BaseFragment parentFragment;
    private int recipientHeaderRow;
    private int recipientInfoRow;
    private int recipientRow;
    private String recipientString;
    private int rowCount;
    private int sendRow;
    private int storageFeeHeaderRow;
    private int storageFeeInfoRow;
    private int storageFeeRow;
    private int transactionFeeHeaderRow;
    private int transactionFeeInfoRow;
    private int transactionFeeRow;
    private String walletAddress;
    private boolean wasFirstAttach;

    public class BalanceCell extends FrameLayout {
        private Typeface defaultTypeFace;
        private TextView titleView = new TextView(getContext());
        private SimpleTextView valueTextView;
        private TextView yourBalanceTextView;

        public BalanceCell(Context context) {
            super(context);
            this.titleView.setLines(1);
            this.titleView.setSingleLine(true);
            this.titleView.setText(LocaleController.getString("WalletTransaction", NUM));
            this.titleView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.titleView.setTextSize(1, 20.0f);
            String str = "fonts/rmedium.ttf";
            this.titleView.setTypeface(AndroidUtilities.getTypeface(str));
            this.titleView.setPadding(AndroidUtilities.dp(27.0f), AndroidUtilities.dp(18.0f), AndroidUtilities.dp(27.0f), AndroidUtilities.dp(8.0f));
            this.titleView.setEllipsize(TruncateAt.MIDDLE);
            this.titleView.setGravity(16);
            addView(this.titleView, LayoutHelper.createFrame(-1, 48.0f));
            this.valueTextView = new SimpleTextView(context);
            this.valueTextView.setTextSize(32);
            this.valueTextView.setRightDrawable(NUM);
            this.valueTextView.setRightDrawableScale(0.8f);
            this.valueTextView.setDrawablePadding(AndroidUtilities.dp(7.0f));
            this.valueTextView.setGravity(1);
            this.valueTextView.setTypeface(AndroidUtilities.getTypeface(str));
            addView(this.valueTextView, LayoutHelper.createFrame(-1, -2.0f, 1, 0.0f, 78.0f, 0.0f, 0.0f));
            this.yourBalanceTextView = new TextView(context);
            this.yourBalanceTextView.setTextSize(1, 13.0f);
            this.yourBalanceTextView.setTextColor(Theme.getColor("dialogTextGray2"));
            this.defaultTypeFace = this.yourBalanceTextView.getTypeface();
            addView(this.yourBalanceTextView, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 119.0f, 0.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(168.0f), NUM));
        }

        public void setBalance(long j, long j2) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(j > 0 ? "+" : "");
            stringBuilder.append(TonController.formatCurrency(j));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder.toString());
            int indexOf = TextUtils.indexOf(spannableStringBuilder, '.');
            if (indexOf >= 0) {
                spannableStringBuilder.setSpan(new TypefaceSpan(this.defaultTypeFace, AndroidUtilities.dp(21.0f)), indexOf + 1, spannableStringBuilder.length(), 33);
            }
            String str;
            if (j > 0) {
                str = "wallet_greenText";
                this.valueTextView.setTextColor(Theme.getColor(str));
                this.valueTextView.setTag(str);
            } else {
                str = "wallet_redText";
                this.valueTextView.setTextColor(Theme.getColor(str));
                this.valueTextView.setTag(str);
            }
            this.valueTextView.setText(spannableStringBuilder);
            this.yourBalanceTextView.setText(LocaleController.getInstance().formatterStats.format(j2 * 1000));
        }
    }

    public class FeeCell extends FrameLayout {
        private SimpleTextView valueTextView;

        public FeeCell(Context context) {
            super(context);
            this.valueTextView = new SimpleTextView(context);
            this.valueTextView.setTextSize(16);
            this.valueTextView.setRightDrawable(NUM);
            this.valueTextView.setDrawablePadding(AndroidUtilities.dp(6.0f));
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.valueTextView, LayoutHelper.createFrame(-1, -2.0f, 16, 21.0f, 0.0f, 21.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
        }

        public void setFee(long j) {
            this.valueTextView.setText(TonController.formatCurrency(j));
        }
    }

    private class SendAddressCell extends PollEditTextCell {
        private ImageView copyButton;
        private ImageView qrButton;

        public SendAddressCell(Context context) {
            super(context, null);
            EditTextBoldCursor textView = getTextView();
            textView.setSingleLine(false);
            textView.setInputType(655505);
            textView.setMinLines(2);
            textView.setTypeface(Typeface.DEFAULT);
            int i = 5;
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            textView.setFocusable(false);
            textView.setEnabled(false);
            textView.setTypeface(Typeface.MONOSPACE);
            textView.setPadding(0, AndroidUtilities.dp(13.0f), 0, AndroidUtilities.dp(12.0f));
            this.copyButton = new ImageView(context);
            this.copyButton.setImageResource(NUM);
            this.copyButton.setScaleType(ScaleType.CENTER);
            this.copyButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogTextBlue2"), Mode.MULTIPLY));
            this.copyButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("actionBarWhiteSelector"), 6));
            ImageView imageView = this.copyButton;
            if (LocaleController.isRTL) {
                i = 3;
            }
            addView(imageView, LayoutHelper.createFrame(48, 48.0f, i | 48, 6.0f, 10.0f, 6.0f, 0.0f));
            this.copyButton.setOnClickListener(new -$$Lambda$WalletTransactionSheet$SendAddressCell$jX0J4ZLJOdtoTHVYZLeixnsPjAM(this));
        }

        public /* synthetic */ void lambda$new$0$WalletTransactionSheet$SendAddressCell(View view) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ton://transfer/");
            stringBuilder.append(WalletTransactionSheet.this.recipientString.replace("\n", ""));
            AndroidUtilities.addToClipboard(stringBuilder.toString());
            Toast.makeText(getContext(), LocaleController.getString("WalletTransactionAddressCopied", NUM), 0).show();
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            ImageView imageView = this.copyButton;
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
            return WalletTransactionSheet.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == WalletTransactionSheet.this.recipientHeaderRow) {
                    if (WalletTransactionSheet.this.amountValue > 0) {
                        headerCell.setText(LocaleController.getString("WalletTransactionSender", NUM));
                    } else {
                        headerCell.setText(LocaleController.getString("WalletTransactionRecipient", NUM));
                    }
                } else if (i == WalletTransactionSheet.this.commentHeaderRow) {
                    headerCell.setText(LocaleController.getString("WalletTransactionComment", NUM));
                } else if (i == WalletTransactionSheet.this.storageFeeHeaderRow) {
                    headerCell.setText(LocaleController.getString("WalletStorageFeeHeader", NUM));
                } else if (i == WalletTransactionSheet.this.transactionFeeHeaderRow) {
                    headerCell.setText(LocaleController.getString("WalletTransactionFeeHeader", NUM));
                }
            } else if (itemViewType == 6) {
                ((SendAddressCell) viewHolder.itemView).setTextAndHint(WalletTransactionSheet.this.recipientString, LocaleController.getString("WalletEnterWalletAddress", NUM), true);
            } else if (itemViewType == 8) {
                BalanceCell balanceCell = (BalanceCell) viewHolder.itemView;
                if (i == WalletTransactionSheet.this.balanceRow) {
                    balanceCell.setBalance(WalletTransactionSheet.this.amountValue, WalletTransactionSheet.this.currentDate);
                }
            } else if (itemViewType == 2) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                if (i == WalletTransactionSheet.this.sendRow) {
                    textSettingsCell.setText(LocaleController.getString("WalletTransactionSendGrams", NUM), false);
                }
            } else if (itemViewType == 3) {
                PollEditTextCell pollEditTextCell = (PollEditTextCell) viewHolder.itemView;
                if (i == WalletTransactionSheet.this.commentRow) {
                    pollEditTextCell.setTextAndHint(WalletTransactionSheet.this.commentString, "", false);
                    pollEditTextCell.getTextView().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            } else if (itemViewType == 4) {
                FeeCell feeCell = (FeeCell) viewHolder.itemView;
                if (i == WalletTransactionSheet.this.storageFeeRow) {
                    feeCell.setFee(WalletTransactionSheet.this.currentStorageFee);
                } else if (i == WalletTransactionSheet.this.transactionFeeRow) {
                    feeCell.setFee(WalletTransactionSheet.this.currentTransactionFee);
                }
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == WalletTransactionSheet.this.sendRow || adapterPosition == WalletTransactionSheet.this.commentRow;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textSettingsCell;
            if (i != 0) {
                View shadowSectionCell;
                if (i == 1) {
                    shadowSectionCell = new ShadowSectionCell(this.mContext);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("dialogBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    combinedDrawable.setFullsize(true);
                    shadowSectionCell.setBackgroundDrawable(combinedDrawable);
                } else if (i == 2) {
                    textSettingsCell = new TextSettingsCell(this.mContext);
                } else if (i != 3) {
                    textSettingsCell = i != 4 ? i != 6 ? new BalanceCell(this.mContext) : new SendAddressCell(this.mContext) : new FeeCell(this.mContext);
                } else {
                    shadowSectionCell = new PollEditTextCell(this.mContext, null);
                    shadowSectionCell.createErrorTextView();
                    EditTextBoldCursor textView = shadowSectionCell.getTextView();
                    textView.setFilters(new InputFilter[]{new LengthFilter(128)});
                    textView.setEnabled(false);
                    textView.setFocusable(false);
                    textView.setClickable(false);
                }
                textSettingsCell = shadowSectionCell;
            } else {
                View headerCell = new HeaderCell(this.mContext, false, 21, 15, false);
            }
            textSettingsCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(textSettingsCell);
        }

        public int getItemViewType(int i) {
            if (i == WalletTransactionSheet.this.recipientHeaderRow || i == WalletTransactionSheet.this.commentHeaderRow || i == WalletTransactionSheet.this.storageFeeHeaderRow || i == WalletTransactionSheet.this.transactionFeeHeaderRow) {
                return 0;
            }
            if (i == WalletTransactionSheet.this.amountInfoRow || i == WalletTransactionSheet.this.recipientInfoRow || i == WalletTransactionSheet.this.storageFeeInfoRow || i == WalletTransactionSheet.this.transactionFeeInfoRow) {
                return 1;
            }
            if (i == WalletTransactionSheet.this.sendRow) {
                return 2;
            }
            if (i == WalletTransactionSheet.this.commentRow) {
                return 3;
            }
            if (i == WalletTransactionSheet.this.storageFeeRow || i == WalletTransactionSheet.this.transactionFeeRow) {
                return 4;
            }
            return i == WalletTransactionSheet.this.recipientRow ? 6 : 8;
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public WalletTransactionSheet(BaseFragment baseFragment, String str, String str2, String str3, long j, long j2, long j3, long j4) {
        super(baseFragment.getParentActivity(), false);
        this.walletAddress = str;
        this.recipientString = str2;
        this.commentString = str3;
        this.amountValue = j;
        this.currentDate = j2;
        this.currentStorageFee = j3;
        this.currentTransactionFee = j4;
        this.parentFragment = baseFragment;
        updateRows();
        Activity parentActivity = baseFragment.getParentActivity();
        ListAdapter listAdapter = new ListAdapter(parentActivity);
        FrameLayout frameLayout = new FrameLayout(parentActivity);
        frameLayout.setBackgroundDrawable(this.shadowDrawable);
        frameLayout.setFocusable(true);
        frameLayout.setFocusableInTouchMode(true);
        this.containerView = frameLayout;
        this.containerView.setWillNotDraw(false);
        setApplyTopPadding(false);
        setApplyBottomPadding(false);
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, this.backgroundPaddingTop, i, 0);
        this.listView = new RecyclerListView(parentActivity) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
            }

            public void requestLayout() {
                super.requestLayout();
            }
        };
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setGlowColor(Theme.getColor("dialogBackground"));
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(parentActivity, 1, false));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(listAdapter);
        this.listView.setOnItemClickListener(new -$$Lambda$WalletTransactionSheet$t7i22PCkeLgpKyV5_3QEh8heqHw(this));
        this.listView.setOnItemLongClickListener(new -$$Lambda$WalletTransactionSheet$JW-0DduPzmucZX3lAjEcYbcWg6E(this));
    }

    public /* synthetic */ void lambda$new$0$WalletTransactionSheet(View view, int i) {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            if (i == this.sendRow) {
                dismiss();
                WalletActionActivity walletActionActivity = new WalletActionActivity(0, this.walletAddress);
                walletActionActivity.setRecipientString(this.recipientString.replace("\n", ""), true);
                this.parentFragment.presentFragment(walletActionActivity, false);
            } else if (i == this.commentRow) {
                AndroidUtilities.addToClipboard(this.commentString);
                Toast.makeText(this.parentFragment.getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
            }
        }
    }

    public /* synthetic */ boolean lambda$new$1$WalletTransactionSheet(View view, int i) {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null || i != this.commentRow) {
            return false;
        }
        AndroidUtilities.addToClipboard(this.commentString);
        Toast.makeText(this.parentFragment.getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
        return true;
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.balanceRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.amountInfoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.recipientHeaderRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.recipientRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.sendRow = i;
        if (this.currentTransactionFee == 0 && this.currentStorageFee == 0 && TextUtils.isEmpty(this.commentString)) {
            this.recipientInfoRow = -1;
        } else {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.recipientInfoRow = i;
        }
        if (TextUtils.isEmpty(this.commentString)) {
            this.commentHeaderRow = -1;
            this.commentRow = -1;
            this.storageFeeInfoRow = -1;
        } else {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.commentHeaderRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.commentRow = i;
            if (this.currentTransactionFee == 0 && this.currentStorageFee == 0) {
                this.storageFeeInfoRow = -1;
            } else {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.storageFeeInfoRow = i;
            }
        }
        if (this.currentStorageFee != 0) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.storageFeeHeaderRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.storageFeeRow = i;
            if (this.currentTransactionFee != 0) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.transactionFeeInfoRow = i;
            } else {
                this.transactionFeeInfoRow = -1;
            }
        } else {
            this.storageFeeHeaderRow = -1;
            this.storageFeeRow = -1;
            this.transactionFeeInfoRow = -1;
        }
        if (this.currentTransactionFee != 0) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.transactionFeeHeaderRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.transactionFeeRow = i;
            return;
        }
        this.transactionFeeHeaderRow = -1;
        this.transactionFeeRow = -1;
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[17];
        themeDescriptionArr[0] = new ThemeDescription(this.containerView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[2] = new ThemeDescription(this.listView, 0, new Class[]{BalanceCell.class}, new String[]{"yourBalanceTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        View view = this.listView;
        int i = ThemeDescription.FLAG_CHECKTAG;
        Class[] clsArr = new Class[]{BalanceCell.class};
        String[] strArr = new String[1];
        strArr[0] = "valueTextView";
        themeDescriptionArr[3] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[4] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{BalanceCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{HeaderCell.class};
        strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[5] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueHeader");
        View view2 = this.listView;
        int i2 = ThemeDescription.FLAG_CHECKTAG;
        Class[] clsArr2 = new Class[]{HeaderCell.class};
        String[] strArr2 = new String[1];
        strArr2[0] = "textView2";
        themeDescriptionArr[6] = new ThemeDescription(view2, i2, clsArr2, strArr2, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, null, null, null, "windowBackgroundWhiteRedText5");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteHintText");
        View view3 = this.listView;
        int i3 = ThemeDescription.FLAG_HINTTEXTCOLOR;
        Class[] clsArr3 = new Class[]{PollEditTextCell.class};
        String[] strArr3 = new String[1];
        strArr3[0] = "deleteImageView";
        themeDescriptionArr[10] = new ThemeDescription(view3, i3, clsArr3, strArr3, null, null, null, "windowBackgroundWhiteGrayText");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{PollEditTextCell.class}, new String[]{"deleteImageView"}, null, null, null, "stickers_menuSelector");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{PollEditTextCell.class}, new String[]{"textView2"}, null, null, null, "windowBackgroundWhiteRedText5");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{PollEditTextCell.class}, new String[]{"textView2"}, null, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteHintText");
        return themeDescriptionArr;
    }
}
