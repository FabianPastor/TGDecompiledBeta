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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.URLSpan;
import android.util.Property;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import drinkless.org.ton.TonApi;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import javax.crypto.Cipher;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.TonController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BiometricPromtHelper;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Wallet.WalletCreateActivity;

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
    /* access modifiers changed from: private */
    public static int item_logout = 1;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimator;
    /* access modifiers changed from: private */
    public View actionBarBackground;
    private boolean backToWallet;
    private BiometricPromtHelper biometricPromtHelper;
    /* access modifiers changed from: private */
    public TextView buttonTextView;
    private Runnable cancelOnDestroyRunnable;
    private boolean changingPasscode;
    /* access modifiers changed from: private */
    public ArrayList<Integer> checkWordIndices;
    private String checkingPasscode;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public TextView descriptionText;
    /* access modifiers changed from: private */
    public TextView descriptionText2;
    private LinearLayout editTextContainer;
    /* access modifiers changed from: private */
    public NumericEditText[] editTexts;
    private boolean exportingWords;
    /* access modifiers changed from: private */
    public WalletCreateActivity fragmentToRemove;
    /* access modifiers changed from: private */
    public boolean globalIgnoreTextChange;
    /* access modifiers changed from: private */
    public HintAdapter hintAdapter;
    /* access modifiers changed from: private */
    public NumericEditText hintEditText;
    /* access modifiers changed from: private */
    public LinearLayoutManager hintLayoutManager;
    private RecyclerListView hintListView;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow.ActionBarPopupWindowLayout hintPopupLayout;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow hintPopupWindow;
    /* access modifiers changed from: private */
    public String[] hintWords;
    /* access modifiers changed from: private */
    public RLottieImageView imageView;
    /* access modifiers changed from: private */
    public TextView importButton;
    private LinearLayout leftColumn;
    /* access modifiers changed from: private */
    public int maxEditNumberWidth;
    /* access modifiers changed from: private */
    public int maxNumberWidth;
    /* access modifiers changed from: private */
    public EditTextBoldCursor passcodeEditText;
    /* access modifiers changed from: private */
    public View passcodeNumbersView;
    /* access modifiers changed from: private */
    public int passcodeType;
    private boolean resumeCreation;
    private LinearLayout rightColumn;
    /* access modifiers changed from: private */
    public ScrollView scrollView;
    /* access modifiers changed from: private */
    public String[] secretWords;
    private CharSequence sendText;
    private long showTime = SystemClock.uptimeMillis();
    /* access modifiers changed from: private */
    public TextView titleTextView;

    static /* synthetic */ boolean lambda$createView$6(View view, MotionEvent motionEvent) {
        return true;
    }

    private class HintAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private int[] location = new int[2];
        private ArrayList<String> searchResult = new ArrayList<>();
        private Runnable searchRunnable;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public HintAdapter(Context context2) {
            this.context = context2;
        }

        public void searchHintsFor(NumericEditText numericEditText) {
            String obj = numericEditText.getText().toString();
            if (obj.length() != 0) {
                if (this.searchRunnable != null) {
                    Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                    this.searchRunnable = null;
                }
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                $$Lambda$WalletCreateActivity$HintAdapter$186TVSL5v2U5YjgFwRKdcTc1FZc r2 = new Runnable(obj, numericEditText) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ WalletCreateActivity.NumericEditText f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        WalletCreateActivity.HintAdapter.this.lambda$searchHintsFor$1$WalletCreateActivity$HintAdapter(this.f$1, this.f$2);
                    }
                };
                this.searchRunnable = r2;
                dispatchQueue.postRunnable(r2, 200);
            } else if (WalletCreateActivity.this.hintPopupWindow.isShowing()) {
                WalletCreateActivity.this.hintPopupWindow.dismiss();
            }
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
            AndroidUtilities.runOnUIThread(new Runnable(arrayList, numericEditText) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ WalletCreateActivity.NumericEditText f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    WalletCreateActivity.HintAdapter.this.lambda$null$0$WalletCreateActivity$HintAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$WalletCreateActivity$HintAdapter(ArrayList arrayList, NumericEditText numericEditText) {
            this.searchRunnable = null;
            this.searchResult = arrayList;
            notifyDataSetChanged();
            if (this.searchResult.isEmpty()) {
                WalletCreateActivity.this.hideHint();
                return;
            }
            if (WalletCreateActivity.this.hintEditText != numericEditText || !WalletCreateActivity.this.hintPopupWindow.isShowing()) {
                WalletCreateActivity.this.hintPopupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
                numericEditText.getLocationInWindow(this.location);
                WalletCreateActivity.this.hintLayoutManager.scrollToPositionWithOffset(0, 10000);
                WalletCreateActivity.this.hintPopupWindow.showAtLocation(WalletCreateActivity.this.fragmentView, 51, this.location[0] - AndroidUtilities.dp(48.0f), this.location[1] - AndroidUtilities.dp(64.0f));
            }
            NumericEditText unused = WalletCreateActivity.this.hintEditText = numericEditText;
        }

        public String getItem(int i) {
            return this.searchResult.get(i);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextView textView = new TextView(this.context);
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            textView.setGravity(16);
            textView.setLayoutParams(new RecyclerView.LayoutParams(-2, -1));
            return new RecyclerListView.Holder(textView);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TextView textView = (TextView) viewHolder.itemView;
            textView.setPadding(AndroidUtilities.dp(9.0f), 0, AndroidUtilities.dp(9.0f), 0);
            textView.setText(this.searchResult.get(i));
        }

        public int getItemCount() {
            return this.searchResult.size();
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
            int unused = walletCreateActivity.maxNumberWidth = Math.max(walletCreateActivity.maxNumberWidth, this.numberWidth);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.number != null) {
                this.numericPaint.setColor(Theme.getColor("windowBackgroundWhiteHintText"));
                canvas.drawText(this.number, (float) (WalletCreateActivity.this.maxNumberWidth - this.numberWidth), (float) AndroidUtilities.dp(17.0f), this.numericPaint);
            }
        }
    }

    private class NumericEditText extends FrameLayout {
        private ImageView deleteImageView;
        /* access modifiers changed from: private */
        public EditTextBoldCursor editText;
        /* access modifiers changed from: private */
        public boolean ignoreSearch;
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
            int i2 = 5;
            if (WalletCreateActivity.this.currentType == 5) {
                setNumber(String.format(Locale.US, "%d:", new Object[]{Integer.valueOf(((Integer) WalletCreateActivity.this.checkWordIndices.get(i)).intValue() + 1)}));
            } else {
                setNumber(String.format(Locale.US, "%d:", new Object[]{Integer.valueOf(i + 1)}));
            }
            this.editText.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
            this.editText.setPadding(AndroidUtilities.dp(31.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(30.0f), 0);
            this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.editText.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.editText.setCursorWidth(1.5f);
            this.editText.setMaxLines(1);
            this.editText.setLines(1);
            this.editText.setSingleLine(true);
            this.editText.setImeOptions(NUM | (i == WalletCreateActivity.this.editTexts.length - 1 ? 6 : i2));
            this.editText.setInputType(180224);
            this.editText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.editText.setGravity(3);
            addView(this.editText, LayoutHelper.createFrame(220, 36.0f));
            this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return WalletCreateActivity.NumericEditText.this.lambda$new$0$WalletCreateActivity$NumericEditText(textView, i, keyEvent);
                }
            });
            this.editText.addTextChangedListener(new TextWatcher(WalletCreateActivity.this) {
                private boolean ignoreTextChange;
                private boolean isPaste;

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    if (WalletCreateActivity.this.currentType == 6) {
                        this.isPaste = i3 > i2 && i3 > 40;
                    }
                }

                public void afterTextChanged(Editable editable) {
                    if (!this.ignoreTextChange && !WalletCreateActivity.this.globalIgnoreTextChange) {
                        this.ignoreTextChange = true;
                        if (this.isPaste) {
                            boolean unused = WalletCreateActivity.this.globalIgnoreTextChange = true;
                            try {
                                String[] split = editable.toString().split("\n");
                                if (split.length == 24) {
                                    for (int i = 0; i < 24; i++) {
                                        WalletCreateActivity.this.editTexts[i].editText.setText(split[i].toLowerCase());
                                    }
                                }
                                WalletCreateActivity.this.editTexts[23].editText.requestFocus();
                                return;
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            } finally {
                                boolean unused2 = WalletCreateActivity.this.globalIgnoreTextChange = false;
                            }
                        }
                        editable.replace(0, editable.length(), editable.toString().toLowerCase().trim());
                        this.ignoreTextChange = false;
                        NumericEditText.this.updateClearButton();
                        if (WalletCreateActivity.this.hintAdapter != null && !NumericEditText.this.ignoreSearch) {
                            WalletCreateActivity.this.hintAdapter.searchHintsFor(NumericEditText.this);
                        }
                    }
                }
            });
            this.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public final void onFocusChange(View view, boolean z) {
                    WalletCreateActivity.NumericEditText.this.lambda$new$1$WalletCreateActivity$NumericEditText(view, z);
                }
            });
            this.deleteImageView = new ImageView(context);
            this.deleteImageView.setFocusable(false);
            this.deleteImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.deleteImageView.setImageResource(NUM);
            this.deleteImageView.setAlpha(0.0f);
            this.deleteImageView.setScaleX(0.0f);
            this.deleteImageView.setScaleY(0.0f);
            this.deleteImageView.setRotation(45.0f);
            this.deleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText7"), PorterDuff.Mode.MULTIPLY));
            this.deleteImageView.setContentDescription(LocaleController.getString("ClearButton", NUM));
            addView(this.deleteImageView, LayoutHelper.createFrame(30, 30, 53));
            this.deleteImageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    WalletCreateActivity.NumericEditText.this.lambda$new$2$WalletCreateActivity$NumericEditText(view);
                }
            });
        }

        public /* synthetic */ boolean lambda$new$0$WalletCreateActivity$NumericEditText(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5) {
                int intValue = ((Integer) textView.getTag()).intValue();
                if (intValue < WalletCreateActivity.this.editTexts.length - 1) {
                    int i2 = intValue + 1;
                    WalletCreateActivity.this.editTexts[i2].editText.requestFocus();
                    WalletCreateActivity.this.editTexts[i2].editText.setSelection(WalletCreateActivity.this.editTexts[i2].length());
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

        /* access modifiers changed from: private */
        public void updateClearButton() {
            boolean z = false;
            boolean z2 = this.editText.length() > 0 && this.editText.hasFocus();
            if (this.deleteImageView.getTag() != null) {
                z = true;
            }
            if (z2 != z) {
                this.deleteImageView.setTag(z2 ? 1 : null);
                float f = 1.0f;
                float f2 = 0.0f;
                ViewPropertyAnimator scaleX = this.deleteImageView.animate().alpha(z2 ? 1.0f : 0.0f).scaleX(z2 ? 1.0f : 0.0f);
                if (!z2) {
                    f = 0.0f;
                }
                ViewPropertyAnimator scaleY = scaleX.scaleY(f);
                if (!z2) {
                    f2 = 45.0f;
                }
                scaleY.rotation(f2).setDuration(150).start();
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
            int unused = walletCreateActivity.maxEditNumberWidth = Math.max(walletCreateActivity.maxEditNumberWidth, this.numberWidth);
        }

        public void setText(CharSequence charSequence) {
            this.ignoreSearch = true;
            this.editText.setText(charSequence);
            EditTextBoldCursor editTextBoldCursor = this.editText;
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
            this.ignoreSearch = false;
            int intValue = ((Integer) this.editText.getTag()).intValue();
            if (intValue < WalletCreateActivity.this.editTexts.length - 1) {
                int i = intValue + 1;
                WalletCreateActivity.this.editTexts[i].editText.requestFocus();
                WalletCreateActivity.this.editTexts[i].editText.setSelection(WalletCreateActivity.this.editTexts[i].length());
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.number != null) {
                this.numericPaint.setColor(Theme.getColor("windowBackgroundWhiteHintText"));
                canvas.drawText(this.number, (float) ((WalletCreateActivity.this.maxEditNumberWidth - this.numberWidth) / 2), (float) AndroidUtilities.dp(20.0f), this.numericPaint);
            }
        }
    }

    public WalletCreateActivity(int i) {
        this.currentType = i;
    }

    /* access modifiers changed from: private */
    public void hideHint() {
        ActionBarPopupWindow actionBarPopupWindow = this.hintPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.hintPopupWindow.dismiss();
        }
    }

    public boolean onFragmentCreate() {
        String[] strArr;
        int i = this.currentType;
        if ((i == 4 || i == 5) && ((strArr = this.secretWords) == null || strArr.length != 24)) {
            return false;
        }
        int i2 = this.currentType;
        if (i2 == 6 || i2 == 5) {
            this.hintWords = getTonController().getHintWords();
            if (this.hintWords == null) {
                return false;
            }
        }
        if (this.currentType == 2) {
            getTonController().saveWalletKeys(true);
        }
        if (this.currentType == 5) {
            this.checkWordIndices = new ArrayList<>();
            while (this.checkWordIndices.size() < 3) {
                int nextInt = Utilities.random.nextInt(24);
                if (!this.checkWordIndices.contains(Integer.valueOf(nextInt))) {
                    this.checkWordIndices.add(Integer.valueOf(nextInt));
                }
            }
            Collections.sort(this.checkWordIndices);
        }
        if (this.currentType == 4 && !this.exportingWords) {
            this.cancelOnDestroyRunnable = new Runnable() {
                public final void run() {
                    WalletCreateActivity.this.lambda$onFragmentCreate$0$WalletCreateActivity();
                }
            };
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
        int i;
        super.onFragmentDestroy();
        int i2 = this.currentType;
        if (i2 == 5 || i2 == 8) {
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
        if (Build.VERSION.SDK_INT >= 23 && AndroidUtilities.allowScreenCapture() && (i = this.currentType) != 9 && i != 0) {
            AndroidUtilities.setFlagSecure(this, false);
        }
    }

    public View createView(Context context) {
        final Context context2 = context;
        boolean z = canGoBack() && (this.currentType != 0 || !BuildVars.TON_WALLET_STANDALONE);
        this.swipeBackEnabled = z;
        if (z) {
            this.actionBar.setBackButtonImage(NUM);
            if (this.currentType == 5) {
                this.swipeBackEnabled = false;
            }
        }
        this.actionBar.setBackgroundDrawable((Drawable) null);
        this.actionBar.setTitleColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setAddToContainer(false);
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
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
        if (i == 0) {
            this.importButton = new TextView(context2);
            this.importButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText2"));
            this.importButton.setTextSize(1, 14.0f);
            this.importButton.setText(LocaleController.getString("ImportExistingWallet", NUM));
            this.importButton.setGravity(16);
            this.actionBar.addView(this.importButton, LayoutHelper.createFrame(-2, -1.0f, 53, 0.0f, 0.0f, 22.0f, 0.0f));
            this.importButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    WalletCreateActivity.this.lambda$createView$1$WalletCreateActivity(view);
                }
            });
        } else if (i == 6 || i == 5) {
            this.hintPopupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context2);
            this.hintPopupLayout.setAnimationEnabled(false);
            this.hintPopupLayout.setOnTouchListener(new View.OnTouchListener() {
                private Rect popupRect = new Rect();

                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getActionMasked() != 0 || WalletCreateActivity.this.hintPopupWindow == null || !WalletCreateActivity.this.hintPopupWindow.isShowing()) {
                        return false;
                    }
                    view.getHitRect(this.popupRect);
                    if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                        return false;
                    }
                    WalletCreateActivity.this.hintPopupWindow.dismiss();
                    return false;
                }
            });
            this.hintPopupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                    WalletCreateActivity.this.lambda$createView$2$WalletCreateActivity(keyEvent);
                }
            });
            this.hintPopupLayout.setShowedFromBotton(false);
            this.hintListView = new RecyclerListView(context2);
            RecyclerListView recyclerListView = this.hintListView;
            HintAdapter hintAdapter2 = new HintAdapter(context2);
            this.hintAdapter = hintAdapter2;
            recyclerListView.setAdapter(hintAdapter2);
            this.hintListView.setPadding(AndroidUtilities.dp(9.0f), 0, AndroidUtilities.dp(9.0f), 0);
            RecyclerListView recyclerListView2 = this.hintListView;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 0, false);
            this.hintLayoutManager = linearLayoutManager;
            recyclerListView2.setLayoutManager(linearLayoutManager);
            this.hintListView.setClipToPadding(false);
            this.hintPopupLayout.addView(this.hintListView, LayoutHelper.createFrame(-1, 48.0f));
            this.hintListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                public final void onItemClick(View view, int i) {
                    WalletCreateActivity.this.lambda$createView$3$WalletCreateActivity(view, i);
                }
            });
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
        this.imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.titleTextView = new TextView(context2);
        this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.titleTextView.setGravity(1);
        this.titleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.titleTextView.setTextSize(1, 24.0f);
        this.descriptionText = new TextView(context2);
        this.descriptionText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.descriptionText.setGravity(1);
        this.descriptionText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText.setTextSize(1, 15.0f);
        this.descriptionText.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.descriptionText2 = new TextView(context2);
        this.descriptionText2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.descriptionText2.setGravity(1);
        this.descriptionText2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText2.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.descriptionText2.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.descriptionText2.setVisibility(8);
        this.buttonTextView = new TextView(context2);
        this.buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        this.buttonTextView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WalletCreateActivity.this.lambda$createView$5$WalletCreateActivity(view);
            }
        });
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
                    this.descriptionText2.setLinkTextColor(Theme.getColor("windowBackgroundWhiteBlueText2"));
                    this.descriptionText2.setTag("windowBackgroundWhiteBlueText2");
                } else {
                    this.descriptionText2.setTextSize(1, 13.0f);
                    this.descriptionText2.setLinkTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
                    this.descriptionText2.setTag("windowBackgroundWhiteGrayText6");
                }
                AnonymousClass3 r2 = new ViewGroup(context2) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        int size = View.MeasureSpec.getSize(i);
                        int size2 = View.MeasureSpec.getSize(i2);
                        if (WalletCreateActivity.this.importButton != null && Build.VERSION.SDK_INT >= 21) {
                            ((FrameLayout.LayoutParams) WalletCreateActivity.this.importButton.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
                        }
                        WalletCreateActivity.this.actionBar.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), i2);
                        if (size > size2) {
                            float f = (float) size;
                            WalletCreateActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec((int) (0.45f * f), NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.68f), NUM));
                            int i3 = (int) (f * 0.6f);
                            WalletCreateActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            WalletCreateActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            WalletCreateActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            WalletCreateActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                        } else {
                            WalletCreateActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.399f), NUM));
                            WalletCreateActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            WalletCreateActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            WalletCreateActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            WalletCreateActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                        }
                        setMeasuredDimension(size, size2);
                    }

                    /* access modifiers changed from: protected */
                    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                        WalletCreateActivity.this.actionBar.layout(0, 0, i3, WalletCreateActivity.this.actionBar.getMeasuredHeight());
                        int i5 = i3 - i;
                        int i6 = i4 - i2;
                        if (i3 > i4) {
                            int measuredHeight = (i6 - WalletCreateActivity.this.imageView.getMeasuredHeight()) / 2;
                            WalletCreateActivity.this.imageView.layout(0, measuredHeight, WalletCreateActivity.this.imageView.getMeasuredWidth(), WalletCreateActivity.this.imageView.getMeasuredHeight() + measuredHeight);
                            float f = (float) i5;
                            float f2 = 0.4f * f;
                            int i7 = (int) f2;
                            float f3 = (float) i6;
                            int i8 = (int) (0.22f * f3);
                            WalletCreateActivity.this.titleTextView.layout(i7, i8, WalletCreateActivity.this.titleTextView.getMeasuredWidth() + i7, WalletCreateActivity.this.titleTextView.getMeasuredHeight() + i8);
                            int i9 = (int) (0.39f * f3);
                            WalletCreateActivity.this.descriptionText.layout(i7, i9, WalletCreateActivity.this.descriptionText.getMeasuredWidth() + i7, WalletCreateActivity.this.descriptionText.getMeasuredHeight() + i9);
                            int measuredWidth = (int) (f2 + (((f * 0.6f) - ((float) WalletCreateActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                            int i10 = (int) (((WalletCreateActivity.this.currentType == 1 || WalletCreateActivity.this.currentType == 9) ? 0.74f : 0.64f) * f3);
                            WalletCreateActivity.this.buttonTextView.layout(measuredWidth, i10, WalletCreateActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth, WalletCreateActivity.this.buttonTextView.getMeasuredHeight() + i10);
                            int i11 = (int) (f3 * 0.8f);
                            WalletCreateActivity.this.descriptionText2.layout(i7, i11, WalletCreateActivity.this.descriptionText2.getMeasuredWidth() + i7, WalletCreateActivity.this.descriptionText2.getMeasuredHeight() + i11);
                            return;
                        }
                        float f4 = (float) i6;
                        int i12 = (int) (0.148f * f4);
                        WalletCreateActivity.this.imageView.layout(0, i12, WalletCreateActivity.this.imageView.getMeasuredWidth(), WalletCreateActivity.this.imageView.getMeasuredHeight() + i12);
                        int i13 = (int) (0.458f * f4);
                        WalletCreateActivity.this.titleTextView.layout(0, i13, WalletCreateActivity.this.titleTextView.getMeasuredWidth(), WalletCreateActivity.this.titleTextView.getMeasuredHeight() + i13);
                        int i14 = (int) (0.52f * f4);
                        WalletCreateActivity.this.descriptionText.layout(0, i14, WalletCreateActivity.this.descriptionText.getMeasuredWidth(), WalletCreateActivity.this.descriptionText.getMeasuredHeight() + i14);
                        int measuredWidth2 = (i5 - WalletCreateActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        int i15 = (int) (0.791f * f4);
                        WalletCreateActivity.this.buttonTextView.layout(measuredWidth2, i15, WalletCreateActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth2, WalletCreateActivity.this.buttonTextView.getMeasuredHeight() + i15);
                        int i16 = (int) (f4 * 0.894f);
                        WalletCreateActivity.this.descriptionText2.layout(0, i16, WalletCreateActivity.this.descriptionText2.getMeasuredWidth(), WalletCreateActivity.this.descriptionText2.getMeasuredHeight() + i16);
                    }
                };
                r2.setOnTouchListener($$Lambda$WalletCreateActivity$91xVmYiVI79i4BfBbl2wPByWIq4.INSTANCE);
                r2.addView(this.actionBar);
                r2.addView(this.imageView);
                r2.addView(this.titleTextView);
                r2.addView(this.descriptionText);
                r2.addView(this.descriptionText2);
                r2.addView(this.buttonTextView);
                this.fragmentView = r2;
                break;
            case 4:
            case 5:
            case 6:
                this.buttonTextView.setMinWidth(AndroidUtilities.dp(220.0f));
                this.descriptionText2.setLinkTextColor(Theme.getColor("windowBackgroundWhiteBlueText2"));
                this.descriptionText2.setTag("windowBackgroundWhiteBlueText2");
                if (this.currentType == 6) {
                    this.descriptionText2.setTextSize(1, 15.0f);
                } else {
                    this.descriptionText2.setTextSize(1, 14.0f);
                }
                AnonymousClass4 r22 = new ViewGroup(context2) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        int size = View.MeasureSpec.getSize(i);
                        int size2 = View.MeasureSpec.getSize(i2);
                        if (WalletCreateActivity.this.importButton != null) {
                            ((FrameLayout.LayoutParams) WalletCreateActivity.this.importButton.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
                        }
                        WalletCreateActivity.this.actionBar.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), i2);
                        WalletCreateActivity.this.actionBarBackground.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(WalletCreateActivity.this.actionBar.getMeasuredHeight() + AndroidUtilities.dp(3.0f), NUM));
                        WalletCreateActivity.this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), i2);
                        setMeasuredDimension(size, size2);
                    }

                    /* access modifiers changed from: protected */
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

                    /* access modifiers changed from: protected */
                    public void onScrollChanged(int i, int i2, int i3, int i4) {
                        super.onScrollChanged(i, i2, i3, i4);
                        WalletCreateActivity.this.hideHint();
                        if (WalletCreateActivity.this.titleTextView != null) {
                            WalletCreateActivity.this.titleTextView.getLocationOnScreen(this.location);
                            boolean z = this.location[1] + WalletCreateActivity.this.titleTextView.getMeasuredHeight() < WalletCreateActivity.this.actionBar.getBottom();
                            if (z != (WalletCreateActivity.this.titleTextView.getTag() == null)) {
                                WalletCreateActivity.this.titleTextView.setTag(z ? null : 1);
                                if (WalletCreateActivity.this.actionBarAnimator != null) {
                                    WalletCreateActivity.this.actionBarAnimator.cancel();
                                    AnimatorSet unused = WalletCreateActivity.this.actionBarAnimator = null;
                                }
                                AnimatorSet unused2 = WalletCreateActivity.this.actionBarAnimator = new AnimatorSet();
                                AnimatorSet access$3500 = WalletCreateActivity.this.actionBarAnimator;
                                Animator[] animatorArr = new Animator[2];
                                View access$2900 = WalletCreateActivity.this.actionBarBackground;
                                Property property = View.ALPHA;
                                float[] fArr = new float[1];
                                float f = 1.0f;
                                fArr[0] = z ? 1.0f : 0.0f;
                                animatorArr[0] = ObjectAnimator.ofFloat(access$2900, property, fArr);
                                SimpleTextView titleTextView = WalletCreateActivity.this.actionBar.getTitleTextView();
                                Property property2 = View.ALPHA;
                                float[] fArr2 = new float[1];
                                if (!z) {
                                    f = 0.0f;
                                }
                                fArr2[0] = f;
                                animatorArr[1] = ObjectAnimator.ofFloat(titleTextView, property2, fArr2);
                                access$3500.playTogether(animatorArr);
                                WalletCreateActivity.this.actionBarAnimator.setDuration(150);
                                WalletCreateActivity.this.actionBarAnimator.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        if (animator.equals(WalletCreateActivity.this.actionBarAnimator)) {
                                            AnimatorSet unused = WalletCreateActivity.this.actionBarAnimator = null;
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
                        if (WalletCreateActivity.this.editTexts == null || WalletCreateActivity.this.editTexts[WalletCreateActivity.this.editTexts.length - 1].editText != view) {
                            this.tempRect.bottom += AndroidUtilities.dp(10.0f);
                        } else {
                            this.tempRect.bottom += AndroidUtilities.dp(90.0f);
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
                        if (Build.VERSION.SDK_INT < 29 && view2 != null && !this.isLayoutDirty) {
                            scrollToDescendant(view2);
                        }
                        super.requestChildFocus(view, view2);
                    }

                    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                        if (Build.VERSION.SDK_INT < 23) {
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

                    /* access modifiers changed from: protected */
                    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                        this.isLayoutDirty = false;
                        super.onLayout(z, i, i2, i3, i4);
                    }
                };
                this.scrollView.setVerticalScrollBarEnabled(false);
                r22.addView(this.scrollView);
                LinearLayout linearLayout = new LinearLayout(context2);
                linearLayout.setOrientation(1);
                this.scrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -1, 51));
                linearLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 69, 0, 0));
                linearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 8, 0, 0));
                linearLayout.addView(this.descriptionText, LayoutHelper.createLinear(-2, -2, 49, 0, 9, 0, 0));
                linearLayout.addView(this.descriptionText2, LayoutHelper.createLinear(-2, -2, 49, 0, 17, 0, 0));
                int i2 = this.currentType;
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
                    int i3 = 0;
                    while (i3 < 12) {
                        int i4 = 0;
                        while (i4 < 2) {
                            NumericTextView numericTextView = new NumericTextView(context2);
                            numericTextView.setGravity(51);
                            numericTextView.setTextSize(1, 16.0f);
                            numericTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
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
                    int i5 = 0;
                    while (true) {
                        NumericEditText[] numericEditTextArr = this.editTexts;
                        if (i5 < numericEditTextArr.length) {
                            NumericEditText numericEditText = new NumericEditText(context2, i5);
                            numericEditTextArr[i5] = numericEditText;
                            linearLayout.addView(numericEditText, LayoutHelper.createLinear(220, 36, 1, 0, i5 == 0 ? 21 : 13, 0, 0));
                            i5++;
                        }
                    }
                }
                linearLayout.addView(this.buttonTextView, LayoutHelper.createLinear(-2, 42, 49, 0, 36, 0, 33));
                this.fragmentView = r22;
                this.actionBarBackground = new View(context2) {
                    private Paint paint = new Paint();

                    /* access modifiers changed from: protected */
                    public void onDraw(Canvas canvas) {
                        this.paint.setColor(Theme.getColor("windowBackgroundWhite"));
                        int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(3.0f);
                        canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) measuredHeight, this.paint);
                        WalletCreateActivity.this.parentLayout.drawHeaderShadow(canvas, measuredHeight);
                    }
                };
                this.actionBarBackground.setAlpha(0.0f);
                r22.addView(this.actionBarBackground);
                r22.addView(this.actionBar);
                break;
            case 8:
                this.descriptionText2.setLinkTextColor(Theme.getColor("windowBackgroundWhiteBlueText2"));
                this.descriptionText2.setTag("windowBackgroundWhiteBlueText2");
                this.descriptionText2.setTextSize(1, 14.0f);
                this.scrollView = new ScrollView(context2);
                this.scrollView.setFillViewport(true);
                final FrameLayout frameLayout = new FrameLayout(context2);
                this.scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -1, 51));
                AnonymousClass7 r5 = new ViewGroup(context2) {
                    private boolean ignoreLayout;

                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        int size = View.MeasureSpec.getSize(i);
                        int size2 = View.MeasureSpec.getSize(i2);
                        this.ignoreLayout = true;
                        WalletCreateActivity.this.actionBar.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), i2);
                        frameLayout.setPadding(0, WalletCreateActivity.this.actionBar.getMeasuredHeight(), 0, 0);
                        WalletCreateActivity.this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, NUM));
                        this.ignoreLayout = false;
                        setMeasuredDimension(size, size2);
                    }

                    /* access modifiers changed from: protected */
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
                r5.addView(this.scrollView);
                frameLayout.addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 0.0f, 0.0f, 0.0f));
                frameLayout.addView(this.titleTextView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 120.0f, 0.0f, 0.0f));
                frameLayout.addView(this.descriptionText2, LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 260.0f, 0.0f, 22.0f));
                final Paint paint = new Paint(1);
                final Paint paint2 = new Paint(1);
                paint2.setStyle(Paint.Style.STROKE);
                paint2.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
                this.passcodeNumbersView = new View(context2) {
                    /* access modifiers changed from: protected */
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
                this.passcodeEditText.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.passcodeEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
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
                this.passcodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        return WalletCreateActivity.this.lambda$createView$7$WalletCreateActivity(textView, i, keyEvent);
                    }
                });
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
                this.passcodeEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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
                this.fragmentView = r5;
                r5.addView(this.actionBar);
                break;
        }
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        switch (this.currentType) {
            case 0:
                this.imageView.setAutoRepeat(true);
                this.imageView.setAnimation(NUM, 120, 120);
                this.titleTextView.setText(LocaleController.getString("GramWallet", NUM));
                this.descriptionText.setText(LocaleController.getString("GramWalletInfo", NUM));
                this.buttonTextView.setText(LocaleController.getString("CreateMyWallet", NUM));
                if (!BuildVars.TON_WALLET_STANDALONE) {
                    String string = LocaleController.getString("CreateMyWalletTerms", NUM);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
                    int indexOf = string.indexOf(42);
                    int lastIndexOf = string.lastIndexOf(42);
                    if (!(indexOf == -1 || lastIndexOf == -1)) {
                        spannableStringBuilder.replace(lastIndexOf, lastIndexOf + 1, "");
                        spannableStringBuilder.replace(indexOf, indexOf + 1, "");
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
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(LocaleController.getString("WalletTooBadCreate", NUM));
                spannableStringBuilder2.setSpan(new URLSpanNoUnderline("") {
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
                this.descriptionText.setText(AndroidUtilities.replaceTags(LocaleController.formatString("WalletTestTimeInfo", NUM, Integer.valueOf(this.checkWordIndices.get(0).intValue() + 1), Integer.valueOf(this.checkWordIndices.get(1).intValue() + 1), Integer.valueOf(this.checkWordIndices.get(2).intValue() + 1))));
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
                SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder(LocaleController.getString("WalletImportDontHave", NUM));
                spannableStringBuilder3.setSpan(new URLSpanNoUnderline("") {
                    public void onClick(View view) {
                        WalletCreateActivity walletCreateActivity = new WalletCreateActivity(3);
                        WalletCreateActivity unused = walletCreateActivity.fragmentToRemove = WalletCreateActivity.this;
                        WalletCreateActivity.this.presentFragment(walletCreateActivity);
                    }
                }, 0, spannableStringBuilder3.length(), 33);
                this.descriptionText2.setText(spannableStringBuilder3);
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
                SpannableStringBuilder spannableStringBuilder4 = new SpannableStringBuilder(LocaleController.getString("WalletSetPasscodeOptions", NUM));
                spannableStringBuilder4.setSpan(new URLSpanNoUnderline("") {
                    public void onClick(View view) {
                        BottomSheet.Builder builder = new BottomSheet.Builder(context2);
                        builder.setTitle(LocaleController.getString("WalletSetPasscodeChooseType", NUM), true);
                        builder.setItems(new CharSequence[]{LocaleController.getString("WalletSetPasscode4Digit", NUM), LocaleController.getString("WalletSetPasscode6Digit", NUM), LocaleController.getString("WalletSetPasscodeCustom", NUM)}, new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                WalletCreateActivity.AnonymousClass11.this.lambda$onClick$0$WalletCreateActivity$11(dialogInterface, i);
                            }
                        });
                        WalletCreateActivity.this.showDialog(builder.create());
                    }

                    public /* synthetic */ void lambda$onClick$0$WalletCreateActivity$11(DialogInterface dialogInterface, int i) {
                        if (WalletCreateActivity.this.passcodeType != i) {
                            int unused = WalletCreateActivity.this.passcodeType = i;
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
                }, 0, spannableStringBuilder4.length(), 33);
                this.descriptionText2.setText(spannableStringBuilder4);
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
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.hintPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.hintPopupWindow.dismiss();
        }
    }

    public /* synthetic */ void lambda$createView$3$WalletCreateActivity(View view, int i) {
        this.hintEditText.setText(this.hintAdapter.getItem(i));
        this.hintPopupWindow.dismiss();
    }

    public /* synthetic */ void lambda$createView$5$WalletCreateActivity(View view) {
        if (getParentActivity() != null) {
            hideHint();
            switch (this.currentType) {
                case 0:
                    createWallet();
                    return;
                case 1:
                    if (this.resumeCreation) {
                        int keyProtectionType = getTonController().getKeyProtectionType();
                        if (keyProtectionType != 1) {
                            if (keyProtectionType == 2) {
                                this.biometricPromtHelper.promtWithCipher(getTonController().getCipherForDecrypt(), LocaleController.getString("WalletExportConfirmContinue", NUM), new BiometricPromtHelper.CipherCallback() {
                                    public final void run(Cipher cipher) {
                                        WalletCreateActivity.this.doExport(cipher);
                                    }
                                });
                                return;
                            }
                            return;
                        } else if (Build.VERSION.SDK_INT >= 23) {
                            getParentActivity().startActivityForResult(((KeyguardManager) ApplicationLoader.applicationContext.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(LocaleController.getString("Wallet", NUM), LocaleController.getString("WalletExportConfirmContinue", NUM)), 34);
                            return;
                        } else {
                            return;
                        }
                    } else {
                        WalletCreateActivity walletCreateActivity = new WalletCreateActivity(4);
                        walletCreateActivity.secretWords = this.secretWords;
                        presentFragment(walletCreateActivity, true);
                        return;
                    }
                case 2:
                    presentFragment(new WalletActivity(), true);
                    return;
                case 3:
                    finishFragment();
                    return;
                case 4:
                    if (this.exportingWords) {
                        finishFragment();
                        return;
                    } else if (SystemClock.uptimeMillis() - this.showTime < 60000) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                        builder.setTopAnimation(NUM, Theme.getColor("dialogBackgroundGray"));
                        builder.setTitle(LocaleController.getString("WalletSecretWordsAlertTitle", NUM));
                        builder.setMessage(LocaleController.getString("WalletSecretWordsAlertText", NUM));
                        builder.setPositiveButton(LocaleController.getString("WalletSecretWordsAlertButton", NUM), (DialogInterface.OnClickListener) null);
                        showDialog(builder.create());
                        return;
                    } else {
                        WalletCreateActivity walletCreateActivity2 = new WalletCreateActivity(5);
                        walletCreateActivity2.fragmentToRemove = this;
                        walletCreateActivity2.secretWords = this.secretWords;
                        presentFragment(walletCreateActivity2);
                        return;
                    }
                case 5:
                    if (checkEditTexts()) {
                        int size = this.checkWordIndices.size();
                        for (int i = 0; i < size; i++) {
                            if (!this.secretWords[this.checkWordIndices.get(i).intValue()].equals(this.editTexts[i].getText().toString())) {
                                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                                builder2.setTitle(LocaleController.getString("WalletTestTimeAlertTitle", NUM));
                                builder2.setMessage(LocaleController.getString("WalletTestTimeAlertText", NUM));
                                builder2.setNegativeButton(LocaleController.getString("WalletTestTimeAlertButtonSee", NUM), new DialogInterface.OnClickListener() {
                                    public final void onClick(DialogInterface dialogInterface, int i) {
                                        WalletCreateActivity.this.lambda$null$4$WalletCreateActivity(dialogInterface, i);
                                    }
                                });
                                builder2.setPositiveButton(LocaleController.getString("WalletTestTimeAlertButtonTry", NUM), (DialogInterface.OnClickListener) null);
                                showDialog(builder2.create());
                                return;
                            }
                        }
                        WalletCreateActivity walletCreateActivity3 = this.fragmentToRemove;
                        if (walletCreateActivity3 != null) {
                            walletCreateActivity3.removeSelfFromStack();
                        }
                        if (getTonController().isWaitingForUserPasscode()) {
                            presentFragment(new WalletCreateActivity(7), true);
                            return;
                        } else {
                            presentFragment(new WalletCreateActivity(2), true);
                            return;
                        }
                    } else {
                        return;
                    }
                case 6:
                    if (checkEditTexts()) {
                        createWallet();
                        return;
                    }
                    return;
                case 7:
                    presentFragment(new WalletCreateActivity(8), true);
                    return;
                case 9:
                    if (this.backToWallet) {
                        finishFragment();
                        return;
                    } else {
                        presentFragment(new WalletActivity(), true);
                        return;
                    }
                default:
                    return;
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
        BiometricPromtHelper.askForBiometric(this, new BiometricPromtHelper.ContinueCallback() {
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
                WalletCreateActivity.this.getTonController().createWallet(strArr, z, new TonController.WordsCallback() {
                    public final void run(String[] strArr) {
                        WalletCreateActivity.AnonymousClass14.this.lambda$doCreate$0$WalletCreateActivity$14(strArr);
                    }
                }, new TonController.ErrorCallback() {
                    public final void run(String str, TonApi.Error error) {
                        WalletCreateActivity.AnonymousClass14.this.lambda$doCreate$1$WalletCreateActivity$14(str, error);
                    }
                });
            }

            public /* synthetic */ void lambda$doCreate$0$WalletCreateActivity$14(String[] strArr) {
                this.progressDialog.dismiss();
                if (WalletCreateActivity.this.currentType == 0) {
                    WalletCreateActivity walletCreateActivity = new WalletCreateActivity(1);
                    String[] unused = walletCreateActivity.secretWords = strArr;
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

            public /* synthetic */ void lambda$doCreate$1$WalletCreateActivity$14(String str, TonApi.Error error) {
                this.progressDialog.dismiss();
                if (WalletCreateActivity.this.currentType == 0) {
                    WalletCreateActivity walletCreateActivity = WalletCreateActivity.this;
                    String string = LocaleController.getString("Wallet", NUM);
                    StringBuilder sb = new StringBuilder();
                    sb.append(LocaleController.getString("ErrorOccurred", NUM));
                    sb.append("\n");
                    if (error != null) {
                        str = error.message;
                    }
                    sb.append(str);
                    AlertsCreator.showSimpleAlert(walletCreateActivity, string, sb.toString());
                } else if (str.startsWith("TONLIB")) {
                    AlertsCreator.showSimpleAlert(WalletCreateActivity.this, LocaleController.getString("WalletImportAlertTitle", NUM), LocaleController.getString("WalletImportAlertText", NUM));
                } else {
                    WalletCreateActivity walletCreateActivity2 = WalletCreateActivity.this;
                    String string2 = LocaleController.getString("Wallet", NUM);
                    AlertsCreator.showSimpleAlert(walletCreateActivity2, string2, LocaleController.getString("ErrorOccurred", NUM) + "\n" + str);
                }
            }

            public void run(boolean z) {
                this.progressDialog = new AlertDialog(WalletCreateActivity.this.getParentActivity(), 3);
                this.progressDialog.setCanCacnel(false);
                this.progressDialog.show();
                UserConfig userConfig = WalletCreateActivity.this.getUserConfig();
                if (userConfig.walletConfigType != 0 || !TextUtils.isEmpty(userConfig.walletConfigFromUrl)) {
                    doCreate(z);
                } else {
                    WalletConfigLoader.loadConfig(userConfig.walletConfigUrl, new TonController.StringCallback(userConfig, z) {
                        private final /* synthetic */ UserConfig f$1;
                        private final /* synthetic */ boolean f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(String str) {
                            WalletCreateActivity.AnonymousClass14.this.lambda$run$2$WalletCreateActivity$14(this.f$1, this.f$2, str);
                        }
                    });
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

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        int i;
        int i2;
        if (z) {
            if ((this.currentType == 5 && !z2) || (i2 = this.currentType) == 6) {
                this.editTexts[0].editText.requestFocus();
                AndroidUtilities.showKeyboard(this.editTexts[0].editText);
            } else if (i2 == 8 && !z2) {
                this.passcodeEditText.requestFocus();
                AndroidUtilities.showKeyboard(this.passcodeEditText);
            }
            if (Build.VERSION.SDK_INT >= 23 && AndroidUtilities.allowScreenCapture() && (i = this.currentType) != 9 && i != 0) {
                AndroidUtilities.setFlagSecure(this, true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        hideHint();
    }

    public boolean onBackPressed() {
        return canGoBack();
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i == 34 && i2 == -1) {
            doExport((Cipher) null);
        }
    }

    /* access modifiers changed from: private */
    public void doExport(Cipher cipher) {
        if (getParentActivity() != null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.show();
            getTonController().getSecretWords((String) null, cipher, new TonController.WordsCallback(alertDialog) {
                private final /* synthetic */ AlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(String[] strArr) {
                    WalletCreateActivity.this.lambda$doExport$8$WalletCreateActivity(this.f$1, strArr);
                }
            }, new TonController.ErrorCallback(alertDialog) {
                private final /* synthetic */ AlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(String str, TonApi.Error error) {
                    WalletCreateActivity.this.lambda$doExport$9$WalletCreateActivity(this.f$1, str, error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$doExport$8$WalletCreateActivity(AlertDialog alertDialog, String[] strArr) {
        alertDialog.dismiss();
        WalletCreateActivity walletCreateActivity = new WalletCreateActivity(4);
        walletCreateActivity.secretWords = strArr;
        presentFragment(walletCreateActivity, true);
    }

    public /* synthetic */ void lambda$doExport$9$WalletCreateActivity(AlertDialog alertDialog, String str, TonApi.Error error) {
        alertDialog.dismiss();
        if (str.equals("KEYSTORE_FAIL")) {
            getTonController().cleanup();
            UserConfig userConfig = getUserConfig();
            userConfig.clearTonConfig();
            userConfig.saveConfig(false);
            finishFragment();
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(LocaleController.getString("ErrorOccurred", NUM));
        sb.append("\n");
        if (error != null) {
            str = error.message;
        }
        sb.append(str);
        AlertsCreator.showSimpleAlert(this, sb.toString());
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
                    FileLog.e((Throwable) e);
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

    /* access modifiers changed from: private */
    public void setCurrentPasscodeLengthLimit() {
        int i;
        int i2 = this.passcodeType;
        if (i2 == 0) {
            i = 4;
        } else if (i2 == 1) {
            i = 6;
        } else if (i2 == 2) {
            i = 32;
        } else {
            return;
        }
        this.passcodeEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(i)});
    }

    /* access modifiers changed from: private */
    public void showPasscodeConfirm() {
        EditTextBoldCursor editTextBoldCursor = this.passcodeEditText;
        if (editTextBoldCursor != null) {
            this.checkingPasscode = editTextBoldCursor.getText().toString();
            this.passcodeEditText.setText("");
            this.passcodeNumbersView.invalidate();
            this.descriptionText2.setVisibility(4);
            this.titleTextView.setText(LocaleController.getString("WalletSetPasscodeRepeat", NUM));
        }
    }

    /* access modifiers changed from: private */
    public void onPasscodeEnter() {
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
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        WalletCreateActivity.this.showPasscodeConfirm();
                    }
                }, 150);
            }
        } else {
            String obj = this.passcodeEditText.getText().toString();
            if (!obj.equals(this.checkingPasscode)) {
                Toast.makeText(getParentActivity(), LocaleController.getString("WalletSetPasscodeError", NUM), 0).show();
                this.titleTextView.setText(LocaleController.getString("WalletSetPasscode", NUM));
                onPasscodeError();
                this.checkingPasscode = null;
                this.passcodeEditText.setText("");
                this.descriptionText2.setVisibility(0);
                this.passcodeNumbersView.invalidate();
                return;
            }
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.show();
            getTonController().setUserPasscode(obj, this.passcodeType, new Runnable(alertDialog) {
                private final /* synthetic */ AlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    WalletCreateActivity.this.lambda$onPasscodeEnter$10$WalletCreateActivity(this.f$1);
                }
            });
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
            FileLog.e((Throwable) e);
        }
    }

    public void setSendText(CharSequence charSequence, boolean z) {
        this.sendText = charSequence;
        this.backToWallet = z;
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarWhiteSelector"), new ThemeDescription(this.passcodeNumbersView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.passcodeNumbersView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.actionBarBackground, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.hintListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"), new ThemeDescription((View) this.editTextContainer, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{NumericEditText.class}, new String[]{"editText"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.editTextContainer, ThemeDescription.FLAG_CURSORCOLOR, new Class[]{NumericEditText.class}, new String[]{"editText"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.editTextContainer, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{NumericEditText.class}, new String[]{"deleteImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText7"), new ThemeDescription(this.editTextContainer, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.leftColumn, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{NumericTextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.rightColumn, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{NumericTextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.leftColumn, 0, new Class[]{NumericTextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.rightColumn, 0, new Class[]{NumericTextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.passcodeEditText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.passcodeEditText, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.passcodeEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.importButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"), new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(this.descriptionText2, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(this.descriptionText2, ThemeDescription.FLAG_LINKCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"), new ThemeDescription(this.descriptionText2, ThemeDescription.FLAG_LINKCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_buttonText"), new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButton"), new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButtonPressed")};
    }
}
