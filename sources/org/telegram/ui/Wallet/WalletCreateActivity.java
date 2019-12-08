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
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.URLSpan;
import android.util.Property;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import drinkless.org.ton.TonApi.Error;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import javax.crypto.Cipher;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.LinkMovementMethodMy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BiometricPromtHelper;
import org.telegram.ui.Components.BiometricPromtHelper.ContinueCallback;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class WalletCreateActivity extends BaseFragment {
    public static final int TYPE_24_WORDS = 4;
    public static final int TYPE_CREATE = 0;
    public static final int TYPE_IMPORT = 6;
    public static final int TYPE_KEY_GENERATED = 1;
    public static final int TYPE_PERFECT = 7;
    public static final int TYPE_READY = 2;
    public static final int TYPE_SEND_DONE = 9;
    public static final int TYPE_SET_PASSCODE = 8;
    public static final int TYPE_TOO_BAD = 3;
    public static final int TYPE_WORDS_CHECK = 5;
    private static int item_logout = 1;
    private AnimatorSet actionBarAnimator;
    private View actionBarBackground;
    private boolean backToWallet;
    private BiometricPromtHelper biometricPromtHelper;
    private TextView buttonTextView;
    private Runnable cancelOnDestroyRunnable;
    private boolean changingPasscode;
    private ArrayList<Integer> checkWordIndices;
    private String checkingPasscode;
    private int currentType;
    private TextView descriptionText;
    private TextView descriptionText2;
    private LinearLayout editTextContainer;
    private NumericEditText[] editTexts;
    private boolean exportingWords;
    private WalletCreateActivity fragmentToRemove;
    private boolean globalIgnoreTextChange;
    private HintAdapter hintAdapter;
    private NumericEditText hintEditText;
    private LinearLayoutManager hintLayoutManager;
    private RecyclerListView hintListView;
    private ActionBarPopupWindowLayout hintPopupLayout;
    private ActionBarPopupWindow hintPopupWindow;
    private String[] hintWords;
    private RLottieImageView imageView;
    private TextView importButton;
    private LinearLayout leftColumn;
    private int maxEditNumberWidth;
    private int maxNumberWidth;
    private EditTextBoldCursor passcodeEditText;
    private View passcodeNumbersView;
    private int passcodeType;
    private boolean resumeCreation;
    private LinearLayout rightColumn;
    private ScrollView scrollView;
    private String[] secretWords;
    private CharSequence sendText;
    private long showTime = SystemClock.uptimeMillis();
    private TextView titleTextView;

    private class NumericEditText extends FrameLayout {
        private ImageView deleteImageView;
        private EditTextBoldCursor editText;
        private boolean ignoreSearch;
        private String number;
        private int numberWidth;
        private TextPaint numericPaint = new TextPaint(1);

        public NumericEditText(Context context, int i) {
            super(context);
            setWillNotDraw(false);
            this.numericPaint.setTextSize((float) AndroidUtilities.dp(17.0f));
            this.editText = new EditTextBoldCursor(context);
            this.editText.setTag(Integer.valueOf(i));
            this.editText.setTextSize(1, 17.0f);
            String str = "%d:";
            int i2 = 5;
            if (WalletCreateActivity.this.currentType == 5) {
                setNumber(String.format(Locale.US, str, new Object[]{Integer.valueOf(((Integer) r8.checkWordIndices.get(i)).intValue() + 1)}));
            } else {
                setNumber(String.format(Locale.US, str, new Object[]{Integer.valueOf(i + 1)}));
            }
            this.editText.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
            this.editText.setPadding(AndroidUtilities.dp(31.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(30.0f), 0);
            str = "windowBackgroundWhiteBlackText";
            this.editText.setTextColor(Theme.getColor(str));
            this.editText.setCursorColor(Theme.getColor(str));
            this.editText.setCursorWidth(1.5f);
            this.editText.setMaxLines(1);
            this.editText.setLines(1);
            this.editText.setSingleLine(true);
            EditTextBoldCursor editTextBoldCursor = this.editText;
            if (i == WalletCreateActivity.this.editTexts.length - 1) {
                i2 = 6;
            }
            editTextBoldCursor.setImeOptions(NUM | i2);
            this.editText.setInputType(180224);
            this.editText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.editText.setGravity(3);
            addView(this.editText, LayoutHelper.createFrame(220, 36.0f));
            this.editText.setOnEditorActionListener(new -$$Lambda$WalletCreateActivity$NumericEditText$D04vagdG3oCwaHFzX92CNkT1Okw(this));
            this.editText.addTextChangedListener(new TextWatcher(WalletCreateActivity.this) {
                private boolean ignoreTextChange;
                private boolean isPaste;

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    if (WalletCreateActivity.this.currentType == 6) {
                        boolean z = i3 > i2 && i3 > 40;
                        this.isPaste = z;
                    }
                }

                public void afterTextChanged(Editable editable) {
                    if (!(this.ignoreTextChange || WalletCreateActivity.this.globalIgnoreTextChange)) {
                        this.ignoreTextChange = true;
                        if (this.isPaste) {
                            WalletCreateActivity.this.globalIgnoreTextChange = true;
                            try {
                                String[] split = editable.toString().split("\n");
                                if (split.length == 24) {
                                    for (int i = 0; i < 24; i++) {
                                        WalletCreateActivity.this.editTexts[i].editText.setText(split[i].toLowerCase());
                                    }
                                }
                                WalletCreateActivity.this.editTexts[23].editText.requestFocus();
                            } catch (Exception e) {
                                FileLog.e(e);
                            } finally {
                                WalletCreateActivity.this.globalIgnoreTextChange = false;
                            }
                            return;
                        }
                        editable.replace(0, editable.length(), editable.toString().toLowerCase().trim());
                        this.ignoreTextChange = false;
                        NumericEditText.this.updateClearButton();
                        if (!(WalletCreateActivity.this.hintAdapter == null || NumericEditText.this.ignoreSearch)) {
                            WalletCreateActivity.this.hintAdapter.searchHintsFor(NumericEditText.this);
                        }
                    }
                }
            });
            this.editText.setOnFocusChangeListener(new -$$Lambda$WalletCreateActivity$NumericEditText$myp3r6DUkXmEq3mJw6HlQBBYNeQ(this));
            this.deleteImageView = new ImageView(context);
            this.deleteImageView.setFocusable(false);
            this.deleteImageView.setScaleType(ScaleType.CENTER);
            this.deleteImageView.setImageResource(NUM);
            this.deleteImageView.setAlpha(0.0f);
            this.deleteImageView.setScaleX(0.0f);
            this.deleteImageView.setScaleY(0.0f);
            this.deleteImageView.setRotation(45.0f);
            this.deleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText7"), Mode.MULTIPLY));
            this.deleteImageView.setContentDescription(LocaleController.getString("ClearButton", NUM));
            addView(this.deleteImageView, LayoutHelper.createFrame(30, 30, 53));
            this.deleteImageView.setOnClickListener(new -$$Lambda$WalletCreateActivity$NumericEditText$MmtnPex_UVKrU4B_1Ugum_lpG7M(this));
        }

        public /* synthetic */ boolean lambda$new$0$WalletCreateActivity$NumericEditText(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5) {
                int intValue = ((Integer) textView.getTag()).intValue();
                if (intValue < WalletCreateActivity.this.editTexts.length - 1) {
                    intValue++;
                    WalletCreateActivity.this.editTexts[intValue].editText.requestFocus();
                    WalletCreateActivity.this.editTexts[intValue].editText.setSelection(WalletCreateActivity.this.editTexts[intValue].length());
                }
                WalletCreateActivity.this.hideHint();
                return true;
            } else if (i != 6) {
                return false;
            } else {
                WalletCreateActivity.this.buttonTextView.callOnClick();
                return true;
            }
        }

        public /* synthetic */ void lambda$new$1$WalletCreateActivity$NumericEditText(View view, boolean z) {
            updateClearButton();
            WalletCreateActivity.this.hideHint();
        }

        public /* synthetic */ void lambda$new$2$WalletCreateActivity$NumericEditText(View view) {
            if (this.deleteImageView.getAlpha() == 1.0f) {
                this.editText.setText("");
            }
        }

        private void updateClearButton() {
            Object obj = null;
            Object obj2 = (this.editText.length() <= 0 || !this.editText.hasFocus()) ? null : 1;
            if (this.deleteImageView.getTag() != null) {
                obj = 1;
            }
            if (obj2 != obj) {
                this.deleteImageView.setTag(obj2 != null ? Integer.valueOf(1) : null);
                float f = 1.0f;
                float f2 = 0.0f;
                ViewPropertyAnimator scaleX = this.deleteImageView.animate().alpha(obj2 != null ? 1.0f : 0.0f).scaleX(obj2 != null ? 1.0f : 0.0f);
                if (obj2 == null) {
                    f = 0.0f;
                }
                scaleX = scaleX.scaleY(f);
                if (obj2 == null) {
                    f2 = 45.0f;
                }
                scaleX.rotation(f2).setDuration(150).start();
            }
        }

        public int length() {
            return this.editText.length();
        }

        public Editable getText() {
            return this.editText.getText();
        }

        public void setNumber(String str) {
            this.number = str;
            this.numberWidth = (int) Math.ceil((double) this.numericPaint.measureText(this.number));
            WalletCreateActivity walletCreateActivity = WalletCreateActivity.this;
            walletCreateActivity.maxEditNumberWidth = Math.max(walletCreateActivity.maxEditNumberWidth, this.numberWidth);
        }

        public void setText(CharSequence charSequence) {
            this.ignoreSearch = true;
            this.editText.setText(charSequence);
            EditTextBoldCursor editTextBoldCursor = this.editText;
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
            this.ignoreSearch = false;
            int intValue = ((Integer) this.editText.getTag()).intValue();
            if (intValue < WalletCreateActivity.this.editTexts.length - 1) {
                intValue++;
                WalletCreateActivity.this.editTexts[intValue].editText.requestFocus();
                WalletCreateActivity.this.editTexts[intValue].editText.setSelection(WalletCreateActivity.this.editTexts[intValue].length());
            }
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.number != null) {
                this.numericPaint.setColor(Theme.getColor("windowBackgroundWhiteHintText"));
                canvas.drawText(this.number, (float) ((WalletCreateActivity.this.maxEditNumberWidth - this.numberWidth) / 2), (float) AndroidUtilities.dp(20.0f), this.numericPaint);
            }
        }
    }

    private class NumericTextView extends TextView {
        private String number;
        private int numberWidth;
        private TextPaint numericPaint = new TextPaint(1);

        public NumericTextView(Context context) {
            super(context);
            setPadding(AndroidUtilities.dp(31.0f), 0, 0, 0);
            this.numericPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        }

        public void setNumber(String str) {
            this.number = str;
            this.numberWidth = (int) Math.ceil((double) this.numericPaint.measureText(this.number));
            WalletCreateActivity walletCreateActivity = WalletCreateActivity.this;
            walletCreateActivity.maxNumberWidth = Math.max(walletCreateActivity.maxNumberWidth, this.numberWidth);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.number != null) {
                this.numericPaint.setColor(Theme.getColor("windowBackgroundWhiteHintText"));
                canvas.drawText(this.number, (float) (WalletCreateActivity.this.maxNumberWidth - this.numberWidth), (float) AndroidUtilities.dp(17.0f), this.numericPaint);
            }
        }
    }

    private class HintAdapter extends SelectionAdapter {
        private Context context;
        private int[] location = new int[2];
        private ArrayList<String> searchResult = new ArrayList();
        private Runnable searchRunnable;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public HintAdapter(Context context) {
            this.context = context;
        }

        public void searchHintsFor(NumericEditText numericEditText) {
            String obj = numericEditText.getText().toString();
            if (obj.length() == 0) {
                if (WalletCreateActivity.this.hintPopupWindow.isShowing()) {
                    WalletCreateActivity.this.hintPopupWindow.dismiss();
                }
                return;
            }
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            -$$Lambda$WalletCreateActivity$HintAdapter$186TVSL5v2U5YjgFwRKdcTc1FZc -__lambda_walletcreateactivity_hintadapter_186tvsl5v2u5yjgfwrkdctc1fzc = new -$$Lambda$WalletCreateActivity$HintAdapter$186TVSL5v2U5YjgFwRKdcTc1FZc(this, obj, numericEditText);
            this.searchRunnable = -__lambda_walletcreateactivity_hintadapter_186tvsl5v2u5yjgfwrkdctc1fzc;
            dispatchQueue.postRunnable(-__lambda_walletcreateactivity_hintadapter_186tvsl5v2u5yjgfwrkdctc1fzc, 200);
        }

        public /* synthetic */ void lambda$searchHintsFor$1$WalletCreateActivity$HintAdapter(String str, NumericEditText numericEditText) {
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < WalletCreateActivity.this.hintWords.length; i++) {
                if (WalletCreateActivity.this.hintWords[i].startsWith(str)) {
                    arrayList.add(WalletCreateActivity.this.hintWords[i]);
                }
            }
            if (arrayList.size() == 1 && ((String) arrayList.get(0)).equals(str)) {
                arrayList.clear();
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$WalletCreateActivity$HintAdapter$pI326TJW-gAOGS0EGb-k25VIwS4(this, arrayList, numericEditText));
        }

        public /* synthetic */ void lambda$null$0$WalletCreateActivity$HintAdapter(ArrayList arrayList, NumericEditText numericEditText) {
            this.searchRunnable = null;
            this.searchResult = arrayList;
            notifyDataSetChanged();
            if (this.searchResult.isEmpty()) {
                WalletCreateActivity.this.hideHint();
                return;
            }
            if (!(WalletCreateActivity.this.hintEditText == numericEditText && WalletCreateActivity.this.hintPopupWindow.isShowing())) {
                WalletCreateActivity.this.hintPopupLayout.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
                numericEditText.getLocationInWindow(this.location);
                WalletCreateActivity.this.hintLayoutManager.scrollToPositionWithOffset(0, 10000);
                WalletCreateActivity.this.hintPopupWindow.showAtLocation(WalletCreateActivity.this.fragmentView, 51, this.location[0] - AndroidUtilities.dp(48.0f), this.location[1] - AndroidUtilities.dp(64.0f));
            }
            WalletCreateActivity.this.hintEditText = numericEditText;
        }

        public String getItem(int i) {
            return (String) this.searchResult.get(i);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextView textView = new TextView(this.context);
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            textView.setGravity(16);
            textView.setLayoutParams(new LayoutParams(-2, -1));
            return new Holder(textView);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            TextView textView = (TextView) viewHolder.itemView;
            textView.setPadding(AndroidUtilities.dp(9.0f), 0, AndroidUtilities.dp(9.0f), 0);
            textView.setText((CharSequence) this.searchResult.get(i));
        }

        public int getItemCount() {
            return this.searchResult.size();
        }
    }

    public WalletCreateActivity(int i) {
        this.currentType = i;
    }

    private void hideHint() {
        ActionBarPopupWindow actionBarPopupWindow = this.hintPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.hintPopupWindow.dismiss();
        }
    }

    public boolean onFragmentCreate() {
        int i = this.currentType;
        if (i == 4 || i == 5) {
            String[] strArr = this.secretWords;
            if (strArr == null || strArr.length != 24) {
                return false;
            }
        }
        i = this.currentType;
        if (i == 6 || i == 5) {
            this.hintWords = getTonController().getHintWords();
            if (this.hintWords == null) {
                return false;
            }
        }
        if (this.currentType == 2) {
            getTonController().saveWalletKeys(true);
        }
        if (this.currentType == 5) {
            this.checkWordIndices = new ArrayList();
            while (this.checkWordIndices.size() < 3) {
                i = Utilities.random.nextInt(24);
                if (!this.checkWordIndices.contains(Integer.valueOf(i))) {
                    this.checkWordIndices.add(Integer.valueOf(i));
                }
            }
            Collections.sort(this.checkWordIndices);
        }
        if (this.currentType == 4 && !this.exportingWords) {
            this.cancelOnDestroyRunnable = new -$$Lambda$WalletCreateActivity$VxyhPihiDtOkD-VJVW6TPE8xt7Q(this);
            AndroidUtilities.runOnUIThread(this.cancelOnDestroyRunnable, 60000);
        }
        return super.onFragmentCreate();
    }

    public /* synthetic */ void lambda$onFragmentCreate$0$WalletCreateActivity() {
        View view = this.fragmentView;
        if (view != null) {
            view.setKeepScreenOn(false);
        }
        this.cancelOnDestroyRunnable = null;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        int i = this.currentType;
        if (i == 5 || i == 8) {
            AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        }
        if (this.currentType == 8) {
            getTonController().finishSettingUserPasscode();
        }
        Runnable runnable = this.cancelOnDestroyRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.cancelOnDestroyRunnable = null;
        }
        if (VERSION.SDK_INT >= 23 && AndroidUtilities.allowScreenCapture()) {
            i = this.currentType;
            if (i != 9 && i != 0) {
                AndroidUtilities.setFlagSecure(this, false);
            }
        }
    }

    public View createView(Context context) {
        final Context context2 = context;
        boolean z = canGoBack() && !(this.currentType == 0 && BuildVars.TON_WALLET_STANDALONE);
        this.swipeBackEnabled = z;
        if (z) {
            this.actionBar.setBackButtonImage(NUM);
            if (this.currentType == 5) {
                this.swipeBackEnabled = false;
            }
        }
        this.actionBar.setBackgroundDrawable(null);
        String str = "windowBackgroundWhiteBlackText";
        this.actionBar.setTitleColor(Theme.getColor(str));
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setAddToContainer(false);
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    WalletCreateActivity.this.finishFragment();
                } else if (i == WalletCreateActivity.item_logout) {
                    WalletCreateActivity.this.getTonController().cleanup();
                    UserConfig userConfig = WalletCreateActivity.this.getUserConfig();
                    userConfig.clearTonConfig();
                    userConfig.saveConfig(false);
                    WalletCreateActivity.this.finishFragment();
                }
            }
        });
        int i = this.currentType;
        String str2 = "windowBackgroundWhiteBlueText2";
        if (i == 0) {
            this.importButton = new TextView(context2);
            this.importButton.setTextColor(Theme.getColor(str2));
            this.importButton.setTextSize(1, 14.0f);
            this.importButton.setText(LocaleController.getString("ImportExistingWallet", NUM));
            this.importButton.setGravity(16);
            this.actionBar.addView(this.importButton, LayoutHelper.createFrame(-2, -1.0f, 53, 0.0f, 0.0f, 22.0f, 0.0f));
            this.importButton.setOnClickListener(new -$$Lambda$WalletCreateActivity$Zuy9igJHlOaYuyYQzTLHAeoQ6MA(this));
        } else if (i == 6 || i == 5) {
            this.hintPopupLayout = new ActionBarPopupWindowLayout(context2);
            this.hintPopupLayout.setAnimationEnabled(false);
            this.hintPopupLayout.setOnTouchListener(new OnTouchListener() {
                private Rect popupRect = new Rect();

                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getActionMasked() == 0 && WalletCreateActivity.this.hintPopupWindow != null && WalletCreateActivity.this.hintPopupWindow.isShowing()) {
                        view.getHitRect(this.popupRect);
                        if (!this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            WalletCreateActivity.this.hintPopupWindow.dismiss();
                        }
                    }
                    return false;
                }
            });
            this.hintPopupLayout.setDispatchKeyEventListener(new -$$Lambda$WalletCreateActivity$TEkvVG4gmVYzZbEbBIGi74zlGJY(this));
            this.hintPopupLayout.setShowedFromBotton(false);
            this.hintListView = new RecyclerListView(context2);
            RecyclerListView recyclerListView = this.hintListView;
            HintAdapter hintAdapter = new HintAdapter(context2);
            this.hintAdapter = hintAdapter;
            recyclerListView.setAdapter(hintAdapter);
            this.hintListView.setPadding(AndroidUtilities.dp(9.0f), 0, AndroidUtilities.dp(9.0f), 0);
            recyclerListView = this.hintListView;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 0, false);
            this.hintLayoutManager = linearLayoutManager;
            recyclerListView.setLayoutManager(linearLayoutManager);
            this.hintListView.setClipToPadding(false);
            this.hintPopupLayout.addView(this.hintListView, LayoutHelper.createFrame(-1, 48.0f));
            this.hintListView.setOnItemClickListener(new -$$Lambda$WalletCreateActivity$_idK0xOv5eA3AW_l0F3Xh9YKuYo(this));
            this.hintPopupWindow = new ActionBarPopupWindow(this.hintPopupLayout, -2, -2);
            this.hintPopupWindow.setAnimationEnabled(false);
            this.hintPopupWindow.setAnimationStyle(NUM);
            this.hintPopupWindow.setClippingEnabled(true);
            this.hintPopupWindow.setInputMethodMode(2);
            this.hintPopupWindow.setSoftInputMode(0);
            this.hintPopupWindow.getContentView().setFocusableInTouchMode(true);
            this.hintPopupWindow.setFocusable(false);
        } else if (i == 1 && this.resumeCreation) {
            this.biometricPromtHelper = new BiometricPromtHelper(this);
            if (BuildVars.DEBUG_VERSION) {
                this.actionBar.createMenu().addItemWithWidth(item_logout, NUM, AndroidUtilities.dp(56.0f));
            }
        }
        this.imageView = new RLottieImageView(context2);
        this.imageView.setScaleType(ScaleType.CENTER);
        this.titleTextView = new TextView(context2);
        this.titleTextView.setTextColor(Theme.getColor(str));
        this.titleTextView.setGravity(1);
        this.titleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.titleTextView.setTextSize(1, 24.0f);
        this.descriptionText = new TextView(context2);
        String str3 = "windowBackgroundWhiteGrayText6";
        this.descriptionText.setTextColor(Theme.getColor(str3));
        this.descriptionText.setGravity(1);
        this.descriptionText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText.setTextSize(1, 15.0f);
        this.descriptionText.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.descriptionText2 = new TextView(context2);
        this.descriptionText2.setTextColor(Theme.getColor(str3));
        this.descriptionText2.setGravity(1);
        this.descriptionText2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText2.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.descriptionText2.setMovementMethod(new LinkMovementMethodMy());
        this.descriptionText2.setVisibility(8);
        this.buttonTextView = new TextView(context2);
        this.buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        this.buttonTextView.setOnClickListener(new -$$Lambda$WalletCreateActivity$deo47Vs2HpyExVa6xs6h1TBmLl0(this));
        switch (this.currentType) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 7:
            case 9:
                this.buttonTextView.setMinWidth(AndroidUtilities.dp(150.0f));
                if (this.currentType == 3) {
                    this.descriptionText2.setTextSize(1, 14.0f);
                    this.descriptionText2.setLinkTextColor(Theme.getColor(str2));
                    this.descriptionText2.setTag(str2);
                } else {
                    this.descriptionText2.setTextSize(1, 13.0f);
                    this.descriptionText2.setLinkTextColor(Theme.getColor(str3));
                    this.descriptionText2.setTag(str3);
                }
                AnonymousClass3 anonymousClass3 = new ViewGroup(context2) {
                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        i = MeasureSpec.getSize(i);
                        int size = MeasureSpec.getSize(i2);
                        if (WalletCreateActivity.this.importButton != null && VERSION.SDK_INT >= 21) {
                            ((FrameLayout.LayoutParams) WalletCreateActivity.this.importButton.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
                        }
                        WalletCreateActivity.this.actionBar.measure(MeasureSpec.makeMeasureSpec(i, NUM), i2);
                        if (i > size) {
                            float f = (float) i;
                            WalletCreateActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec((int) (0.45f * f), NUM), MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.68f), NUM));
                            int i3 = (int) (f * 0.6f);
                            WalletCreateActivity.this.titleTextView.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                            WalletCreateActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                            WalletCreateActivity.this.descriptionText2.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                            WalletCreateActivity.this.buttonTextView.measure(MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                        } else {
                            WalletCreateActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.399f), NUM));
                            WalletCreateActivity.this.titleTextView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                            WalletCreateActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                            WalletCreateActivity.this.descriptionText2.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                            WalletCreateActivity.this.buttonTextView.measure(MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                        }
                        setMeasuredDimension(i, size);
                    }

                    /* Access modifiers changed, original: protected */
                    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                        WalletCreateActivity.this.actionBar.layout(0, 0, i3, WalletCreateActivity.this.actionBar.getMeasuredHeight());
                        int i5 = i3 - i;
                        i = i4 - i2;
                        float f;
                        if (i3 > i4) {
                            i2 = (i - WalletCreateActivity.this.imageView.getMeasuredHeight()) / 2;
                            WalletCreateActivity.this.imageView.layout(0, i2, WalletCreateActivity.this.imageView.getMeasuredWidth(), WalletCreateActivity.this.imageView.getMeasuredHeight() + i2);
                            float f2 = (float) i5;
                            float f3 = 0.4f * f2;
                            i3 = (int) f3;
                            f = (float) i;
                            i4 = (int) (0.22f * f);
                            WalletCreateActivity.this.titleTextView.layout(i3, i4, WalletCreateActivity.this.titleTextView.getMeasuredWidth() + i3, WalletCreateActivity.this.titleTextView.getMeasuredHeight() + i4);
                            i4 = (int) (0.39f * f);
                            WalletCreateActivity.this.descriptionText.layout(i3, i4, WalletCreateActivity.this.descriptionText.getMeasuredWidth() + i3, WalletCreateActivity.this.descriptionText.getMeasuredHeight() + i4);
                            i5 = (int) (f3 + (((f2 * 0.6f) - ((float) WalletCreateActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                            f3 = (WalletCreateActivity.this.currentType == 1 || WalletCreateActivity.this.currentType == 9) ? 0.74f : 0.64f;
                            i2 = (int) (f3 * f);
                            WalletCreateActivity.this.buttonTextView.layout(i5, i2, WalletCreateActivity.this.buttonTextView.getMeasuredWidth() + i5, WalletCreateActivity.this.buttonTextView.getMeasuredHeight() + i2);
                            i5 = (int) (f * 0.8f);
                            WalletCreateActivity.this.descriptionText2.layout(i3, i5, WalletCreateActivity.this.descriptionText2.getMeasuredWidth() + i3, WalletCreateActivity.this.descriptionText2.getMeasuredHeight() + i5);
                            return;
                        }
                        f = (float) i;
                        i2 = (int) (0.148f * f);
                        WalletCreateActivity.this.imageView.layout(0, i2, WalletCreateActivity.this.imageView.getMeasuredWidth(), WalletCreateActivity.this.imageView.getMeasuredHeight() + i2);
                        i2 = (int) (0.458f * f);
                        WalletCreateActivity.this.titleTextView.layout(0, i2, WalletCreateActivity.this.titleTextView.getMeasuredWidth(), WalletCreateActivity.this.titleTextView.getMeasuredHeight() + i2);
                        i2 = (int) (0.52f * f);
                        WalletCreateActivity.this.descriptionText.layout(0, i2, WalletCreateActivity.this.descriptionText.getMeasuredWidth(), WalletCreateActivity.this.descriptionText.getMeasuredHeight() + i2);
                        i5 = (i5 - WalletCreateActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        i2 = (int) (0.791f * f);
                        WalletCreateActivity.this.buttonTextView.layout(i5, i2, WalletCreateActivity.this.buttonTextView.getMeasuredWidth() + i5, WalletCreateActivity.this.buttonTextView.getMeasuredHeight() + i2);
                        i5 = (int) (f * 0.894f);
                        WalletCreateActivity.this.descriptionText2.layout(0, i5, WalletCreateActivity.this.descriptionText2.getMeasuredWidth(), WalletCreateActivity.this.descriptionText2.getMeasuredHeight() + i5);
                    }
                };
                anonymousClass3.setOnTouchListener(-$$Lambda$WalletCreateActivity$91xVmYiVI79i4BfBbl2wPByWIq4.INSTANCE);
                anonymousClass3.addView(this.actionBar);
                anonymousClass3.addView(this.imageView);
                anonymousClass3.addView(this.titleTextView);
                anonymousClass3.addView(this.descriptionText);
                anonymousClass3.addView(this.descriptionText2);
                anonymousClass3.addView(this.buttonTextView);
                this.fragmentView = anonymousClass3;
                break;
            case 4:
            case 5:
            case 6:
                this.buttonTextView.setMinWidth(AndroidUtilities.dp(220.0f));
                this.descriptionText2.setLinkTextColor(Theme.getColor(str2));
                this.descriptionText2.setTag(str2);
                if (this.currentType == 6) {
                    this.descriptionText2.setTextSize(1, 15.0f);
                } else {
                    this.descriptionText2.setTextSize(1, 14.0f);
                }
                AnonymousClass4 anonymousClass4 = new ViewGroup(context2) {
                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        i = MeasureSpec.getSize(i);
                        int size = MeasureSpec.getSize(i2);
                        if (WalletCreateActivity.this.importButton != null) {
                            ((FrameLayout.LayoutParams) WalletCreateActivity.this.importButton.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
                        }
                        WalletCreateActivity.this.actionBar.measure(MeasureSpec.makeMeasureSpec(i, NUM), i2);
                        WalletCreateActivity.this.actionBarBackground.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(WalletCreateActivity.this.actionBar.getMeasuredHeight() + AndroidUtilities.dp(3.0f), NUM));
                        WalletCreateActivity.this.scrollView.measure(MeasureSpec.makeMeasureSpec(i, NUM), i2);
                        setMeasuredDimension(i, size);
                    }

                    /* Access modifiers changed, original: protected */
                    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                        WalletCreateActivity.this.actionBar.layout(0, 0, WalletCreateActivity.this.actionBar.getMeasuredWidth(), WalletCreateActivity.this.actionBar.getMeasuredHeight());
                        WalletCreateActivity.this.actionBarBackground.layout(0, 0, WalletCreateActivity.this.actionBarBackground.getMeasuredWidth(), WalletCreateActivity.this.actionBarBackground.getMeasuredHeight());
                        WalletCreateActivity.this.scrollView.layout(0, 0, WalletCreateActivity.this.scrollView.getMeasuredWidth(), WalletCreateActivity.this.scrollView.getMeasuredHeight());
                    }
                };
                this.scrollView = new ScrollView(context2) {
                    private boolean isLayoutDirty = true;
                    private int[] location = new int[2];
                    private View scrollingToChild;
                    private int scrollingUp;
                    private Rect tempRect = new Rect();

                    /* Access modifiers changed, original: protected */
                    public void onScrollChanged(int i, int i2, int i3, int i4) {
                        super.onScrollChanged(i, i2, i3, i4);
                        WalletCreateActivity.this.hideHint();
                        if (WalletCreateActivity.this.titleTextView != null) {
                            WalletCreateActivity.this.titleTextView.getLocationOnScreen(this.location);
                            Object obj = this.location[1] + WalletCreateActivity.this.titleTextView.getMeasuredHeight() < WalletCreateActivity.this.actionBar.getBottom() ? 1 : null;
                            if (obj != (WalletCreateActivity.this.titleTextView.getTag() == null ? 1 : null)) {
                                WalletCreateActivity.this.titleTextView.setTag(obj != null ? null : Integer.valueOf(1));
                                if (WalletCreateActivity.this.actionBarAnimator != null) {
                                    WalletCreateActivity.this.actionBarAnimator.cancel();
                                    WalletCreateActivity.this.actionBarAnimator = null;
                                }
                                WalletCreateActivity.this.actionBarAnimator = new AnimatorSet();
                                AnimatorSet access$3500 = WalletCreateActivity.this.actionBarAnimator;
                                Animator[] animatorArr = new Animator[2];
                                View access$2900 = WalletCreateActivity.this.actionBarBackground;
                                Property property = View.ALPHA;
                                float[] fArr = new float[1];
                                float f = 1.0f;
                                fArr[0] = obj != null ? 1.0f : 0.0f;
                                animatorArr[0] = ObjectAnimator.ofFloat(access$2900, property, fArr);
                                SimpleTextView titleTextView = WalletCreateActivity.this.actionBar.getTitleTextView();
                                property = View.ALPHA;
                                fArr = new float[1];
                                if (obj == null) {
                                    f = 0.0f;
                                }
                                fArr[0] = f;
                                animatorArr[1] = ObjectAnimator.ofFloat(titleTextView, property, fArr);
                                access$3500.playTogether(animatorArr);
                                WalletCreateActivity.this.actionBarAnimator.setDuration(150);
                                WalletCreateActivity.this.actionBarAnimator.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        if (animator.equals(WalletCreateActivity.this.actionBarAnimator)) {
                                            WalletCreateActivity.this.actionBarAnimator = null;
                                        }
                                    }
                                });
                                WalletCreateActivity.this.actionBarAnimator.start();
                            }
                        }
                    }

                    public void scrollToDescendant(View view) {
                        this.scrollingToChild = view;
                        view.getDrawingRect(this.tempRect);
                        offsetDescendantRectToMyCoords(view, this.tempRect);
                        Rect rect;
                        if (WalletCreateActivity.this.editTexts == null || WalletCreateActivity.this.editTexts[WalletCreateActivity.this.editTexts.length - 1].editText != view) {
                            rect = this.tempRect;
                            rect.bottom += AndroidUtilities.dp(10.0f);
                        } else {
                            rect = this.tempRect;
                            rect.bottom += AndroidUtilities.dp(90.0f);
                        }
                        int computeScrollDeltaToGetChildRectOnScreen = computeScrollDeltaToGetChildRectOnScreen(this.tempRect);
                        if (computeScrollDeltaToGetChildRectOnScreen < 0) {
                            int measuredHeight = (getMeasuredHeight() - view.getMeasuredHeight()) / 2;
                            this.scrollingUp = measuredHeight;
                            computeScrollDeltaToGetChildRectOnScreen -= measuredHeight;
                        } else {
                            this.scrollingUp = 0;
                        }
                        if (computeScrollDeltaToGetChildRectOnScreen != 0) {
                            smoothScrollBy(0, computeScrollDeltaToGetChildRectOnScreen);
                        }
                    }

                    public void requestChildFocus(View view, View view2) {
                        if (!(VERSION.SDK_INT >= 29 || view2 == null || this.isLayoutDirty)) {
                            scrollToDescendant(view2);
                        }
                        super.requestChildFocus(view, view2);
                    }

                    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                        if (VERSION.SDK_INT < 23) {
                            if (WalletCreateActivity.this.editTexts == null || WalletCreateActivity.this.editTexts[WalletCreateActivity.this.editTexts.length - 1].editText != this.scrollingToChild) {
                                rect.bottom += AndroidUtilities.dp(16.0f);
                            } else {
                                rect.bottom += AndroidUtilities.dp(90.0f);
                            }
                            int i = this.scrollingUp;
                            if (i != 0) {
                                rect.top -= i;
                                rect.bottom -= i;
                                this.scrollingUp = 0;
                            }
                        }
                        return super.requestChildRectangleOnScreen(view, rect, z);
                    }

                    public void requestLayout() {
                        this.isLayoutDirty = true;
                        super.requestLayout();
                    }

                    /* Access modifiers changed, original: protected */
                    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                        this.isLayoutDirty = false;
                        super.onLayout(z, i, i2, i3, i4);
                    }
                };
                this.scrollView.setVerticalScrollBarEnabled(false);
                anonymousClass4.addView(this.scrollView);
                LinearLayout linearLayout = new LinearLayout(context2);
                linearLayout.setOrientation(1);
                this.scrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -1, 51));
                linearLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 69, 0, 0));
                linearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 8, 0, 0));
                linearLayout.addView(this.descriptionText, LayoutHelper.createLinear(-2, -2, 49, 0, 9, 0, 0));
                linearLayout.addView(this.descriptionText2, LayoutHelper.createLinear(-2, -2, 49, 0, 17, 0, 0));
                int i2 = this.currentType;
                int i3;
                if (i2 == 4) {
                    this.leftColumn = new LinearLayout(context2);
                    this.leftColumn.setOrientation(1);
                    this.rightColumn = new LinearLayout(context2);
                    this.rightColumn.setOrientation(1);
                    LinearLayout linearLayout2 = new LinearLayout(context2);
                    linearLayout2.setOrientation(0);
                    linearLayout2.addView(this.leftColumn, LayoutHelper.createLinear(-2, -2));
                    linearLayout2.addView(this.rightColumn, LayoutHelper.createLinear(-2, -2, 57.0f, 0.0f, 0.0f, 0.0f));
                    linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-2, -2, 49, 0, 30, 0, 0));
                    this.maxNumberWidth = 0;
                    i3 = 0;
                    while (i3 < 12) {
                        int i4 = 0;
                        while (i4 < 2) {
                            NumericTextView numericTextView = new NumericTextView(context2);
                            numericTextView.setGravity(51);
                            numericTextView.setTextSize(1, 16.0f);
                            numericTextView.setTextColor(Theme.getColor(str));
                            numericTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                            Locale locale = Locale.US;
                            Object[] objArr = new Object[1];
                            objArr[0] = Integer.valueOf(i4 == 0 ? i3 + 1 : i3 + 13);
                            numericTextView.setNumber(String.format(locale, "%d.", objArr));
                            numericTextView.setText(this.secretWords[i4 == 0 ? i3 : i3 + 12]);
                            (i4 == 0 ? this.leftColumn : this.rightColumn).addView(numericTextView, LayoutHelper.createLinear(-2, -2, 0.0f, i3 == 0 ? 0.0f : 10.0f, 0.0f, 0.0f));
                            i4++;
                        }
                        i3++;
                    }
                } else if (i2 == 5 || i2 == 6) {
                    this.maxEditNumberWidth = 0;
                    this.editTexts = new NumericEditText[(this.currentType == 5 ? 3 : 24)];
                    this.editTextContainer = linearLayout;
                    i3 = 0;
                    while (true) {
                        NumericEditText[] numericEditTextArr = this.editTexts;
                        if (i3 < numericEditTextArr.length) {
                            NumericEditText numericEditText = new NumericEditText(context2, i3);
                            numericEditTextArr[i3] = numericEditText;
                            linearLayout.addView(numericEditText, LayoutHelper.createLinear(220, 36, 1, 0, i3 == 0 ? 21 : 13, 0, 0));
                            i3++;
                        }
                    }
                }
                linearLayout.addView(this.buttonTextView, LayoutHelper.createLinear(-2, 42, 49, 0, 36, 0, 33));
                this.fragmentView = anonymousClass4;
                this.actionBarBackground = new View(context2) {
                    private Paint paint = new Paint();

                    /* Access modifiers changed, original: protected */
                    public void onDraw(Canvas canvas) {
                        this.paint.setColor(Theme.getColor("windowBackgroundWhite"));
                        int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(3.0f);
                        canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) measuredHeight, this.paint);
                        WalletCreateActivity.this.parentLayout.drawHeaderShadow(canvas, measuredHeight);
                    }
                };
                this.actionBarBackground.setAlpha(0.0f);
                anonymousClass4.addView(this.actionBarBackground);
                anonymousClass4.addView(this.actionBar);
                break;
            case 8:
                this.descriptionText2.setLinkTextColor(Theme.getColor(str2));
                this.descriptionText2.setTag(str2);
                this.descriptionText2.setTextSize(1, 14.0f);
                this.scrollView = new ScrollView(context2);
                this.scrollView.setFillViewport(true);
                final FrameLayout frameLayout = new FrameLayout(context2);
                this.scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -1, 51));
                AnonymousClass7 anonymousClass7 = new ViewGroup(context2) {
                    private boolean ignoreLayout;

                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        i = MeasureSpec.getSize(i);
                        int size = MeasureSpec.getSize(i2);
                        this.ignoreLayout = true;
                        WalletCreateActivity.this.actionBar.measure(MeasureSpec.makeMeasureSpec(i, NUM), i2);
                        frameLayout.setPadding(0, WalletCreateActivity.this.actionBar.getMeasuredHeight(), 0, 0);
                        WalletCreateActivity.this.scrollView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(size, NUM));
                        this.ignoreLayout = false;
                        setMeasuredDimension(i, size);
                    }

                    /* Access modifiers changed, original: protected */
                    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                        WalletCreateActivity.this.actionBar.layout(0, 0, WalletCreateActivity.this.actionBar.getMeasuredWidth(), WalletCreateActivity.this.actionBar.getMeasuredHeight());
                        WalletCreateActivity.this.actionBar.getMeasuredHeight();
                        WalletCreateActivity.this.scrollView.layout(0, 0, WalletCreateActivity.this.scrollView.getMeasuredWidth(), WalletCreateActivity.this.scrollView.getMeasuredHeight());
                    }

                    public void requestLayout() {
                        if (!this.ignoreLayout) {
                            super.requestLayout();
                        }
                    }
                };
                anonymousClass7.addView(this.scrollView);
                frameLayout.addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 0.0f, 0.0f, 0.0f));
                frameLayout.addView(this.titleTextView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 120.0f, 0.0f, 0.0f));
                frameLayout.addView(this.descriptionText2, LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 260.0f, 0.0f, 22.0f));
                final Paint paint = new Paint(1);
                final Paint paint2 = new Paint(1);
                paint2.setStyle(Style.STROKE);
                paint2.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
                this.passcodeNumbersView = new View(context2) {
                    /* Access modifiers changed, original: protected */
                    public void onDraw(Canvas canvas) {
                        if (WalletCreateActivity.this.passcodeEditText != null && WalletCreateActivity.this.passcodeType != 2) {
                            paint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                            paint2.setColor(Theme.getColor("windowBackgroundWhiteHintText"));
                            int i = WalletCreateActivity.this.passcodeType == 0 ? 4 : 6;
                            int dp = AndroidUtilities.dp(8.0f);
                            int dp2 = AndroidUtilities.dp(11.0f);
                            int i2 = dp * 2;
                            int measuredWidth = (getMeasuredWidth() - (((i - 1) * dp2) + (i2 * i))) / 2;
                            int measuredHeight = (getMeasuredHeight() - i2) / 2;
                            int length = WalletCreateActivity.this.passcodeEditText.length();
                            int i3 = 0;
                            while (i3 < i) {
                                canvas.drawCircle((float) (measuredWidth + dp), (float) measuredHeight, (float) dp, i3 < length ? paint : paint2);
                                measuredWidth += i2 + dp2;
                                i3++;
                            }
                        }
                    }
                };
                frameLayout.addView(this.passcodeNumbersView, LayoutHelper.createFrame(220, 36.0f, 49, 0.0f, 190.0f, 0.0f, 0.0f));
                this.passcodeEditText = new EditTextBoldCursor(context2);
                this.passcodeEditText.setAlpha(0.0f);
                this.passcodeEditText.setTextSize(1, 17.0f);
                this.passcodeEditText.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
                this.passcodeEditText.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
                this.passcodeEditText.setCursorColor(Theme.getColor(str));
                this.passcodeEditText.setTextColor(Theme.getColor(str));
                this.passcodeEditText.setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
                this.passcodeEditText.setCursorWidth(1.5f);
                this.passcodeEditText.setMaxLines(1);
                this.passcodeEditText.setHint(LocaleController.getString("WalletSetPasscodeEnterCode", NUM));
                this.passcodeEditText.setLines(1);
                this.passcodeEditText.setSingleLine(true);
                this.passcodeEditText.setGravity(1);
                this.passcodeEditText.setImeOptions(NUM);
                this.passcodeEditText.setCursorSize(AndroidUtilities.dp(20.0f));
                this.passcodeEditText.setInputType(130);
                this.passcodeEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.passcodeEditText.setTypeface(Typeface.DEFAULT);
                setCurrentPasscodeLengthLimit();
                frameLayout.addView(this.passcodeEditText, LayoutHelper.createFrame(220, 36.0f, 49, 0.0f, 190.0f, 0.0f, 0.0f));
                this.passcodeEditText.setOnEditorActionListener(new -$$Lambda$WalletCreateActivity$SFvPrkAon93SP2nQXrpB4PRWLKQ(this));
                this.passcodeEditText.addTextChangedListener(new TextWatcher() {
                    private boolean ignoreTextChange;

                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        if (!this.ignoreTextChange) {
                            this.ignoreTextChange = true;
                            int i = 0;
                            while (i < editable.length()) {
                                char charAt = editable.charAt(i);
                                if (charAt < '0' || charAt > '9') {
                                    editable.delete(i, i + 1);
                                    i--;
                                }
                                i++;
                            }
                            this.ignoreTextChange = false;
                            if (WalletCreateActivity.this.passcodeType == 0) {
                                if (editable.length() == 4) {
                                    WalletCreateActivity.this.onPasscodeEnter();
                                }
                            } else if (WalletCreateActivity.this.passcodeType == 1 && editable.length() == 6) {
                                WalletCreateActivity.this.onPasscodeEnter();
                            }
                            WalletCreateActivity.this.passcodeNumbersView.invalidate();
                        }
                    }
                });
                this.passcodeEditText.setCustomSelectionActionModeCallback(new Callback() {
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                        return false;
                    }

                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }

                    public void onDestroyActionMode(ActionMode actionMode) {
                    }

                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }
                });
                this.fragmentView = anonymousClass7;
                anonymousClass7.addView(this.actionBar);
                break;
        }
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        String str4 = "";
        SpannableStringBuilder spannableStringBuilder;
        SpannableStringBuilder spannableStringBuilder2;
        switch (this.currentType) {
            case 0:
                this.imageView.setAutoRepeat(true);
                this.imageView.setAnimation(NUM, 120, 120);
                this.titleTextView.setText(LocaleController.getString("GramWallet", NUM));
                this.descriptionText.setText(LocaleController.getString("GramWalletInfo", NUM));
                this.buttonTextView.setText(LocaleController.getString("CreateMyWallet", NUM));
                if (!BuildVars.TON_WALLET_STANDALONE) {
                    String string = LocaleController.getString("CreateMyWalletTerms", NUM);
                    spannableStringBuilder = new SpannableStringBuilder(string);
                    int indexOf = string.indexOf(42);
                    int lastIndexOf = string.lastIndexOf(42);
                    if (!(indexOf == -1 || lastIndexOf == -1)) {
                        spannableStringBuilder.replace(lastIndexOf, lastIndexOf + 1, str4);
                        spannableStringBuilder.replace(indexOf, indexOf + 1, str4);
                        spannableStringBuilder.setSpan(new URLSpan(LocaleController.getString("WalletTosUrl", NUM)), indexOf, lastIndexOf - 1, 33);
                    }
                    this.descriptionText2.setText(spannableStringBuilder);
                    this.descriptionText2.setVisibility(0);
                    break;
                }
                break;
            case 1:
                this.imageView.setAnimation(NUM, 120, 120);
                this.titleTextView.setText(LocaleController.getString("WalletCongratulations", NUM));
                this.descriptionText.setText(LocaleController.getString("WalletCongratulationsinfo", NUM));
                this.buttonTextView.setText(LocaleController.getString("WalletContinue", NUM));
                break;
            case 2:
                this.imageView.setAnimation(NUM, 130, 130);
                this.imageView.setPadding(AndroidUtilities.dp(27.0f), 0, 0, 0);
                this.titleTextView.setText(LocaleController.getString("WalletReady", NUM));
                this.descriptionText.setText(LocaleController.getString("WalletReadyInfo", NUM));
                this.buttonTextView.setText(LocaleController.getString("WalletView", NUM));
                break;
            case 3:
                this.imageView.setAutoRepeat(true);
                this.imageView.setAnimation(NUM, 120, 120);
                this.titleTextView.setText(LocaleController.getString("WalletTooBad", NUM));
                this.descriptionText.setText(LocaleController.getString("WalletTooBadInfo", NUM));
                this.buttonTextView.setText(LocaleController.getString("WalletTooBadEnter", NUM));
                this.descriptionText2.setVisibility(0);
                spannableStringBuilder2 = new SpannableStringBuilder(LocaleController.getString("WalletTooBadCreate", NUM));
                spannableStringBuilder2.setSpan(new URLSpanNoUnderline(str4) {
                    public void onClick(View view) {
                        if (WalletCreateActivity.this.fragmentToRemove != null) {
                            WalletCreateActivity.this.fragmentToRemove.removeSelfFromStack();
                        }
                        WalletCreateActivity.this.finishFragment();
                    }
                }, 0, spannableStringBuilder2.length(), 33);
                this.descriptionText2.setText(spannableStringBuilder2);
                break;
            case 4:
                this.imageView.setAnimation(NUM, 112, 112);
                this.fragmentView.setKeepScreenOn(true);
                this.actionBar.setTitle(LocaleController.getString("WalletSecretWordsTitle", NUM));
                this.titleTextView.setText(LocaleController.getString("WalletSecretWords", NUM));
                this.descriptionText.setText(LocaleController.getString("WalletSecretWordsInfo", NUM));
                this.buttonTextView.setText(LocaleController.getString("WalletDone", NUM));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                break;
            case 5:
                this.imageView.setAnimation(NUM, 104, 104);
                this.actionBar.setTitle(LocaleController.getString("WalletTestTimeTitle", NUM));
                this.titleTextView.setText(LocaleController.getString("WalletTestTime", NUM));
                this.descriptionText.setText(AndroidUtilities.replaceTags(LocaleController.formatString("WalletTestTimeInfo", NUM, Integer.valueOf(((Integer) this.checkWordIndices.get(0)).intValue() + 1), Integer.valueOf(((Integer) this.checkWordIndices.get(1)).intValue() + 1), Integer.valueOf(((Integer) this.checkWordIndices.get(2)).intValue() + 1))));
                this.buttonTextView.setText(LocaleController.getString("WalletContinue", NUM));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                break;
            case 6:
                this.imageView.setAnimation(NUM, 112, 112);
                this.actionBar.setTitle(LocaleController.getString("WalletSecretWordsTitle", NUM));
                this.titleTextView.setText(LocaleController.getString("WalletSecretWords", NUM));
                this.descriptionText.setText(LocaleController.getString("WalletImportInfo", NUM));
                this.buttonTextView.setText(LocaleController.getString("WalletContinue", NUM));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.descriptionText2.setVisibility(0);
                spannableStringBuilder2 = new SpannableStringBuilder(LocaleController.getString("WalletImportDontHave", NUM));
                spannableStringBuilder2.setSpan(new URLSpanNoUnderline(str4) {
                    public void onClick(View view) {
                        WalletCreateActivity walletCreateActivity = new WalletCreateActivity(3);
                        walletCreateActivity.fragmentToRemove = WalletCreateActivity.this;
                        WalletCreateActivity.this.presentFragment(walletCreateActivity);
                    }
                }, 0, spannableStringBuilder2.length(), 33);
                this.descriptionText2.setText(spannableStringBuilder2);
                break;
            case 7:
                this.imageView.setAutoRepeat(true);
                this.imageView.setAnimation(NUM, 130, 130);
                this.titleTextView.setText(LocaleController.getString("WalletPerfect", NUM));
                this.descriptionText.setText(LocaleController.getString("WalletPerfectInfo", NUM));
                this.buttonTextView.setText(LocaleController.getString("WalletPerfectSetPasscode", NUM));
                break;
            case 8:
                this.imageView.setAutoRepeat(true);
                this.imageView.setAnimation(NUM, 120, 120);
                this.titleTextView.setText(LocaleController.getString("WalletSetPasscode", NUM));
                this.descriptionText2.setVisibility(0);
                spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString("WalletSetPasscodeOptions", NUM));
                spannableStringBuilder.setSpan(new URLSpanNoUnderline(str4) {
                    public void onClick(View view) {
                        Builder builder = new Builder(context2);
                        builder.setTitle(LocaleController.getString("WalletSetPasscodeChooseType", NUM), true);
                        builder.setItems(new CharSequence[]{LocaleController.getString("WalletSetPasscode4Digit", NUM), LocaleController.getString("WalletSetPasscode6Digit", NUM), LocaleController.getString("WalletSetPasscodeCustom", NUM)}, new -$$Lambda$WalletCreateActivity$11$6ctHZgfxY4ya5DKg2FKDij9POeQ(this));
                        WalletCreateActivity.this.showDialog(builder.create());
                    }

                    public /* synthetic */ void lambda$onClick$0$WalletCreateActivity$11(DialogInterface dialogInterface, int i) {
                        if (WalletCreateActivity.this.passcodeType != i) {
                            WalletCreateActivity.this.passcodeType = i;
                            float f = 1.0f;
                            WalletCreateActivity.this.passcodeEditText.setAlpha(i == 2 ? 1.0f : 0.0f);
                            View access$4700 = WalletCreateActivity.this.passcodeNumbersView;
                            if (i == 2) {
                                f = 0.0f;
                            }
                            access$4700.setAlpha(f);
                            WalletCreateActivity.this.passcodeEditText.setText("");
                            WalletCreateActivity.this.setCurrentPasscodeLengthLimit();
                        }
                    }
                }, 0, spannableStringBuilder.length(), 33);
                this.descriptionText2.setText(spannableStringBuilder);
                break;
            case 9:
                this.imageView.setAnimation(NUM, 130, 130);
                this.imageView.setPadding(AndroidUtilities.dp(27.0f), 0, 0, 0);
                this.titleTextView.setText(LocaleController.getString("WalletSendDone", NUM));
                this.descriptionText.setText(this.sendText);
                this.buttonTextView.setText(LocaleController.getString("WalletView", NUM));
                break;
        }
        this.imageView.playAnimation();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$WalletCreateActivity(View view) {
        WalletCreateActivity walletCreateActivity = new WalletCreateActivity(6);
        walletCreateActivity.fragmentToRemove = this;
        presentFragment(walletCreateActivity);
    }

    public /* synthetic */ void lambda$createView$2$WalletCreateActivity(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0) {
            ActionBarPopupWindow actionBarPopupWindow = this.hintPopupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.hintPopupWindow.dismiss();
            }
        }
    }

    public /* synthetic */ void lambda$createView$3$WalletCreateActivity(View view, int i) {
        this.hintEditText.setText(this.hintAdapter.getItem(i));
        this.hintPopupWindow.dismiss();
    }

    public /* synthetic */ void lambda$createView$5$WalletCreateActivity(View view) {
        if (getParentActivity() != null) {
            hideHint();
            WalletCreateActivity walletCreateActivity;
            int keyProtectionType;
            AlertDialog.Builder builder;
            switch (this.currentType) {
                case 0:
                    createWallet();
                    break;
                case 1:
                    if (!this.resumeCreation) {
                        walletCreateActivity = new WalletCreateActivity(4);
                        walletCreateActivity.secretWords = this.secretWords;
                        presentFragment(walletCreateActivity, true);
                        break;
                    }
                    keyProtectionType = getTonController().getKeyProtectionType();
                    String str = "WalletExportConfirmContinue";
                    if (keyProtectionType == 1) {
                        if (VERSION.SDK_INT >= 23) {
                            getParentActivity().startActivityForResult(((KeyguardManager) ApplicationLoader.applicationContext.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(LocaleController.getString("Wallet", NUM), LocaleController.getString(str, NUM)), 34);
                            break;
                        }
                    } else if (keyProtectionType == 2) {
                        this.biometricPromtHelper.promtWithCipher(getTonController().getCipherForDecrypt(), LocaleController.getString(str, NUM), new -$$Lambda$WalletCreateActivity$hxGciPi6qDkdBcXTJwtmPE9-4x0(this));
                        break;
                    }
                    break;
                case 2:
                    presentFragment(new WalletActivity(), true);
                    break;
                case 3:
                    finishFragment();
                    break;
                case 4:
                    if (!this.exportingWords) {
                        if (SystemClock.uptimeMillis() - this.showTime >= 60000) {
                            walletCreateActivity = new WalletCreateActivity(5);
                            walletCreateActivity.fragmentToRemove = this;
                            walletCreateActivity.secretWords = this.secretWords;
                            presentFragment(walletCreateActivity);
                            break;
                        }
                        builder = new AlertDialog.Builder(getParentActivity());
                        builder.setTopAnimation(NUM, Theme.getColor("dialogBackgroundGray"));
                        builder.setTitle(LocaleController.getString("WalletSecretWordsAlertTitle", NUM));
                        builder.setMessage(LocaleController.getString("WalletSecretWordsAlertText", NUM));
                        builder.setPositiveButton(LocaleController.getString("WalletSecretWordsAlertButton", NUM), null);
                        showDialog(builder.create());
                        return;
                    }
                    finishFragment();
                    return;
                case 5:
                    if (checkEditTexts()) {
                        keyProtectionType = 0;
                        int size = this.checkWordIndices.size();
                        while (keyProtectionType < size) {
                            if (this.secretWords[((Integer) this.checkWordIndices.get(keyProtectionType)).intValue()].equals(this.editTexts[keyProtectionType].getText().toString())) {
                                keyProtectionType++;
                            } else {
                                builder = new AlertDialog.Builder(getParentActivity());
                                builder.setTitle(LocaleController.getString("WalletTestTimeAlertTitle", NUM));
                                builder.setMessage(LocaleController.getString("WalletTestTimeAlertText", NUM));
                                builder.setNegativeButton(LocaleController.getString("WalletTestTimeAlertButtonSee", NUM), new -$$Lambda$WalletCreateActivity$5k-y9kNPe_E_P8CSD4sSHOmdUcc(this));
                                builder.setPositiveButton(LocaleController.getString("WalletTestTimeAlertButtonTry", NUM), null);
                                showDialog(builder.create());
                                return;
                            }
                        }
                        walletCreateActivity = this.fragmentToRemove;
                        if (walletCreateActivity != null) {
                            walletCreateActivity.removeSelfFromStack();
                        }
                        if (!getTonController().isWaitingForUserPasscode()) {
                            presentFragment(new WalletCreateActivity(2), true);
                            break;
                        } else {
                            presentFragment(new WalletCreateActivity(7), true);
                            break;
                        }
                    }
                    return;
                case 6:
                    if (checkEditTexts()) {
                        createWallet();
                        break;
                    }
                    return;
                case 7:
                    presentFragment(new WalletCreateActivity(8), true);
                    break;
                case 9:
                    if (!this.backToWallet) {
                        presentFragment(new WalletActivity(), true);
                        break;
                    } else {
                        finishFragment();
                        break;
                    }
            }
        }
    }

    public /* synthetic */ void lambda$null$4$WalletCreateActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    public /* synthetic */ boolean lambda$createView$7$WalletCreateActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        onPasscodeEnter();
        return true;
    }

    private void createWallet() {
        BiometricPromtHelper.askForBiometric(this, new ContinueCallback() {
            private AlertDialog progressDialog;

            private void doCreate(boolean z) {
                String[] strArr;
                if (WalletCreateActivity.this.currentType == 0) {
                    strArr = null;
                } else {
                    String[] strArr2 = new String[24];
                    for (int i = 0; i < 24; i++) {
                        strArr2[i] = WalletCreateActivity.this.editTexts[i].getText().toString();
                    }
                    strArr = strArr2;
                }
                WalletCreateActivity.this.getTonController().createWallet(strArr, z, new -$$Lambda$WalletCreateActivity$14$gdSWiu_xFOjUTNCDn1lfUBoivGA(this), new -$$Lambda$WalletCreateActivity$14$J-zC5quhSiCjHDcKnMX9EBgJB0w(this));
            }

            public /* synthetic */ void lambda$doCreate$0$WalletCreateActivity$14(String[] strArr) {
                this.progressDialog.dismiss();
                if (WalletCreateActivity.this.currentType == 0) {
                    WalletCreateActivity walletCreateActivity = new WalletCreateActivity(1);
                    walletCreateActivity.secretWords = strArr;
                    WalletCreateActivity.this.presentFragment(walletCreateActivity, true);
                    return;
                }
                if (WalletCreateActivity.this.fragmentToRemove != null) {
                    WalletCreateActivity.this.fragmentToRemove.removeSelfFromStack();
                }
                if (WalletCreateActivity.this.getTonController().isWaitingForUserPasscode()) {
                    WalletCreateActivity.this.presentFragment(new WalletCreateActivity(7), true);
                } else {
                    WalletCreateActivity.this.presentFragment(new WalletCreateActivity(2), true);
                }
            }

            public /* synthetic */ void lambda$doCreate$1$WalletCreateActivity$14(String str, Error error) {
                this.progressDialog.dismiss();
                String str2 = "\n";
                String str3 = "ErrorOccurred";
                String str4 = "Wallet";
                if (WalletCreateActivity.this.currentType == 0) {
                    WalletCreateActivity walletCreateActivity = WalletCreateActivity.this;
                    String string = LocaleController.getString(str4, NUM);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(LocaleController.getString(str3, NUM));
                    stringBuilder.append(str2);
                    if (error != null) {
                        str = error.message;
                    }
                    stringBuilder.append(str);
                    AlertsCreator.showSimpleAlert(walletCreateActivity, string, stringBuilder.toString());
                } else if (str.startsWith("TONLIB")) {
                    AlertsCreator.showSimpleAlert(WalletCreateActivity.this, LocaleController.getString("WalletImportAlertTitle", NUM), LocaleController.getString("WalletImportAlertText", NUM));
                } else {
                    WalletCreateActivity walletCreateActivity2 = WalletCreateActivity.this;
                    String string2 = LocaleController.getString(str4, NUM);
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(LocaleController.getString(str3, NUM));
                    stringBuilder2.append(str2);
                    stringBuilder2.append(str);
                    AlertsCreator.showSimpleAlert(walletCreateActivity2, string2, stringBuilder2.toString());
                }
            }

            public void run(boolean z) {
                this.progressDialog = new AlertDialog(WalletCreateActivity.this.getParentActivity(), 3);
                this.progressDialog.setCanCacnel(false);
                this.progressDialog.show();
                UserConfig userConfig = WalletCreateActivity.this.getUserConfig();
                if (userConfig.walletConfigType == 0 && TextUtils.isEmpty(userConfig.walletConfigFromUrl)) {
                    WalletConfigLoader.loadConfig(userConfig.walletConfigUrl, new -$$Lambda$WalletCreateActivity$14$q7v4Cm-pGPnn8ufS-qBz9b6G-Y4(this, userConfig, z));
                } else {
                    doCreate(z);
                }
            }

            public /* synthetic */ void lambda$run$2$WalletCreateActivity$14(UserConfig userConfig, boolean z, String str) {
                if (TextUtils.isEmpty(str)) {
                    this.progressDialog.dismiss();
                    AlertsCreator.showSimpleAlert(WalletCreateActivity.this, LocaleController.getString("WalletError", NUM), LocaleController.getString("WalletCreateBlockchainConfigLoadError", NUM));
                    return;
                }
                userConfig.walletConfigFromUrl = str;
                userConfig.saveConfig(false);
                doCreate(z);
            }
        }, LocaleController.getString("WalletSecurityAlertCreateContinue", NUM));
    }

    public void onResume() {
        super.onResume();
        int i = this.currentType;
        if (i == 5 || i == 6 || i == 8) {
            AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        }
    }

    public void onPause() {
        super.onPause();
        hideHint();
        if (getParentActivity() != null) {
            AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        }
    }

    public void onBeginSlide() {
        super.onBeginSlide();
        hideHint();
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            if (this.currentType != 5 || z2) {
                int i = this.currentType;
                if (i != 6) {
                    if (i == 8 && !z2) {
                        this.passcodeEditText.requestFocus();
                        AndroidUtilities.showKeyboard(this.passcodeEditText);
                    }
                    if (VERSION.SDK_INT >= 23 && AndroidUtilities.allowScreenCapture()) {
                        i = this.currentType;
                        if (i != 9 && i != 0) {
                            AndroidUtilities.setFlagSecure(this, true);
                            return;
                        }
                        return;
                    }
                }
            }
            this.editTexts[0].editText.requestFocus();
            AndroidUtilities.showKeyboard(this.editTexts[0].editText);
            if (VERSION.SDK_INT >= 23) {
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        hideHint();
    }

    public boolean onBackPressed() {
        return canGoBack();
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i == 34 && i2 == -1) {
            doExport(null);
        }
    }

    private void doExport(Cipher cipher) {
        if (getParentActivity() != null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.show();
            getTonController().getSecretWords(null, cipher, new -$$Lambda$WalletCreateActivity$LY4ARfL7NoAk4YfAIWYHc-DVdLc(this, alertDialog), new -$$Lambda$WalletCreateActivity$QH2pmyadLy4pXjjtKXAO2468Ae4(this, alertDialog));
        }
    }

    public /* synthetic */ void lambda$doExport$8$WalletCreateActivity(AlertDialog alertDialog, String[] strArr) {
        alertDialog.dismiss();
        WalletCreateActivity walletCreateActivity = new WalletCreateActivity(4);
        walletCreateActivity.secretWords = strArr;
        presentFragment(walletCreateActivity, true);
    }

    public /* synthetic */ void lambda$doExport$9$WalletCreateActivity(AlertDialog alertDialog, String str, Error error) {
        alertDialog.dismiss();
        if (str.equals("KEYSTORE_FAIL")) {
            getTonController().cleanup();
            UserConfig userConfig = getUserConfig();
            userConfig.clearTonConfig();
            userConfig.saveConfig(false);
            finishFragment();
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(LocaleController.getString("ErrorOccurred", NUM));
        stringBuilder.append("\n");
        if (error != null) {
            str = error.message;
        }
        stringBuilder.append(str);
        AlertsCreator.showSimpleAlert(this, stringBuilder.toString());
    }

    private boolean checkEditTexts() {
        if (this.editTexts == null) {
            return true;
        }
        int i = 0;
        while (true) {
            NumericEditText[] numericEditTextArr = this.editTexts;
            if (i >= numericEditTextArr.length) {
                return true;
            }
            if (numericEditTextArr[i].length() == 0) {
                this.editTexts[i].editText.clearFocus();
                this.editTexts[i].editText.requestFocus();
                AndroidUtilities.shakeView(this.editTexts[i], 2.0f, 0);
                try {
                    Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
                    if (vibrator != null) {
                        vibrator.vibrate(200);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                return false;
            }
            i++;
        }
    }

    private boolean canGoBack() {
        int i = this.currentType;
        return (i == 2 || i == 9) ? false : true;
    }

    public void setSecretWords(String[] strArr) {
        this.secretWords = strArr;
        this.exportingWords = true;
    }

    public void setResumeCreation() {
        this.resumeCreation = true;
    }

    public void setChangingPasscode() {
        this.changingPasscode = true;
    }

    private void setCurrentPasscodeLengthLimit() {
        int i = this.passcodeType;
        if (i == 0) {
            i = 4;
        } else if (i == 1) {
            i = 6;
        } else if (i == 2) {
            i = 32;
        } else {
            return;
        }
        this.passcodeEditText.setFilters(new InputFilter[]{new LengthFilter(i)});
    }

    private void showPasscodeConfirm() {
        EditTextBoldCursor editTextBoldCursor = this.passcodeEditText;
        if (editTextBoldCursor != null) {
            this.checkingPasscode = editTextBoldCursor.getText().toString();
            this.passcodeEditText.setText("");
            this.passcodeNumbersView.invalidate();
            this.descriptionText2.setVisibility(4);
            this.titleTextView.setText(LocaleController.getString("WalletSetPasscodeRepeat", NUM));
        }
    }

    private void onPasscodeEnter() {
        if (this.checkingPasscode == null) {
            int length = this.passcodeEditText.length();
            int i = this.passcodeType;
            int i2 = 4;
            if (i != 2) {
                if (i != 0) {
                    i2 = 6;
                }
                if (length != i2) {
                    onPasscodeError();
                    return;
                }
            } else if (length < 4) {
                onPasscodeError();
                Toast.makeText(getParentActivity(), LocaleController.getString("WalletSetPasscodeMinLength", NUM), 0).show();
                return;
            }
            if (this.currentType == 2) {
                showPasscodeConfirm();
            } else {
                AndroidUtilities.runOnUIThread(new -$$Lambda$WalletCreateActivity$Qh33G28KwH_4yHhvldpJl2dXjcI(this), 150);
            }
        } else {
            String obj = this.passcodeEditText.getText().toString();
            if (obj.equals(this.checkingPasscode)) {
                AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
                alertDialog.setCanCacnel(false);
                alertDialog.show();
                getTonController().setUserPasscode(obj, this.passcodeType, new -$$Lambda$WalletCreateActivity$3OkLQHoSI3SMpEsSjlTvNx9d9-M(this, alertDialog));
            } else {
                Toast.makeText(getParentActivity(), LocaleController.getString("WalletSetPasscodeError", NUM), 0).show();
                this.titleTextView.setText(LocaleController.getString("WalletSetPasscode", NUM));
                onPasscodeError();
                this.checkingPasscode = null;
                this.passcodeEditText.setText("");
                this.descriptionText2.setVisibility(0);
                this.passcodeNumbersView.invalidate();
            }
        }
    }

    public /* synthetic */ void lambda$onPasscodeEnter$10$WalletCreateActivity(AlertDialog alertDialog) {
        alertDialog.dismiss();
        if (this.changingPasscode) {
            getTonController().saveWalletKeys(true);
            finishFragment();
            return;
        }
        presentFragment(new WalletCreateActivity(2), true);
    }

    private void onPasscodeError() {
        AndroidUtilities.shakeView(this.passcodeType == 2 ? this.passcodeEditText : this.passcodeNumbersView, 2.0f, 0);
        try {
            Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setSendText(CharSequence charSequence, boolean z) {
        this.sendText = charSequence;
        this.backToWallet = z;
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[28];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarWhiteSelector");
        themeDescriptionArr[4] = new ThemeDescription(this.passcodeNumbersView, 0, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[5] = new ThemeDescription(this.passcodeNumbersView, 0, null, null, null, null, "windowBackgroundWhiteHintText");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBarBackground, 0, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[7] = new ThemeDescription(this.hintListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextView.class}, null, null, null, "actionBarDefaultSubmenuItem");
        View view = this.editTextContainer;
        int i = ThemeDescription.FLAG_TEXTCOLOR;
        Class[] clsArr = new Class[]{NumericEditText.class};
        String[] strArr = new String[1];
        strArr[0] = "editText";
        themeDescriptionArr[8] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[9] = new ThemeDescription(this.editTextContainer, ThemeDescription.FLAG_CURSORCOLOR, new Class[]{NumericEditText.class}, new String[]{"editText"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[10] = new ThemeDescription(this.editTextContainer, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{NumericEditText.class}, new String[]{"deleteImageView"}, null, null, null, "windowBackgroundWhiteGrayText7");
        themeDescriptionArr[11] = new ThemeDescription(this.editTextContainer, 0, null, null, null, null, "windowBackgroundWhiteHintText");
        themeDescriptionArr[12] = new ThemeDescription(this.leftColumn, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{NumericTextView.class}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[13] = new ThemeDescription(this.rightColumn, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{NumericTextView.class}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[14] = new ThemeDescription(this.leftColumn, 0, new Class[]{NumericTextView.class}, null, null, null, "windowBackgroundWhiteHintText");
        themeDescriptionArr[15] = new ThemeDescription(this.rightColumn, 0, new Class[]{NumericTextView.class}, null, null, null, "windowBackgroundWhiteHintText");
        themeDescriptionArr[16] = new ThemeDescription(this.passcodeEditText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[17] = new ThemeDescription(this.passcodeEditText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[18] = new ThemeDescription(this.passcodeEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText");
        themeDescriptionArr[19] = new ThemeDescription(this.importButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText2");
        themeDescriptionArr[20] = new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[21] = new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6");
        themeDescriptionArr[22] = new ThemeDescription(this.descriptionText2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6");
        themeDescriptionArr[23] = new ThemeDescription(this.descriptionText2, ThemeDescription.FLAG_LINKCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteBlueText2");
        themeDescriptionArr[24] = new ThemeDescription(this.descriptionText2, ThemeDescription.FLAG_LINKCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteGrayText6");
        themeDescriptionArr[25] = new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "featuredStickers_buttonText");
        themeDescriptionArr[26] = new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, null, null, null, null, "featuredStickers_addButton");
        themeDescriptionArr[27] = new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "featuredStickers_addButtonPressed");
        return themeDescriptionArr;
    }
}
