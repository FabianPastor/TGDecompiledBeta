package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;

public class SearchField extends FrameLayout {
    /* access modifiers changed from: private */
    public ImageView clearSearchImageView;
    private CloseProgressDrawable2 progressDrawable;
    private final Theme.ResourcesProvider resourcesProvider;
    private View searchBackground;
    /* access modifiers changed from: private */
    public EditTextBoldCursor searchEditText;
    private ImageView searchIconImageView;

    public SearchField(Context context, boolean supportRtl, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        FrameLayout.LayoutParams lp;
        FrameLayout.LayoutParams lp2;
        FrameLayout.LayoutParams lp3;
        FrameLayout.LayoutParams lp4;
        this.resourcesProvider = resourcesProvider2;
        View view = new View(context);
        this.searchBackground = view;
        view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), getThemedColor("dialogSearchBackground")));
        if (supportRtl) {
            lp = LayoutHelper.createFrameRelatively(-1.0f, 36.0f, 8388659, 14.0f, 11.0f, 14.0f, 0.0f);
        } else {
            lp = LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f);
        }
        addView(this.searchBackground, lp);
        ImageView imageView = new ImageView(context);
        this.searchIconImageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.searchIconImageView.setImageResource(NUM);
        this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogSearchIcon"), PorterDuff.Mode.MULTIPLY));
        if (supportRtl) {
            lp2 = LayoutHelper.createFrameRelatively(36.0f, 36.0f, 8388659, 16.0f, 11.0f, 0.0f, 0.0f);
        } else {
            lp2 = LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f);
        }
        addView(this.searchIconImageView, lp2);
        ImageView imageView2 = new ImageView(context);
        this.clearSearchImageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        ImageView imageView3 = this.clearSearchImageView;
        AnonymousClass1 r2 = new CloseProgressDrawable2() {
            /* access modifiers changed from: protected */
            public int getCurrentColor() {
                return SearchField.this.getThemedColor("dialogSearchIcon");
            }
        };
        this.progressDrawable = r2;
        imageView3.setImageDrawable(r2);
        this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
        this.clearSearchImageView.setScaleX(0.1f);
        this.clearSearchImageView.setScaleY(0.1f);
        this.clearSearchImageView.setAlpha(0.0f);
        if (supportRtl) {
            lp3 = LayoutHelper.createFrameRelatively(36.0f, 36.0f, 8388661, 14.0f, 11.0f, 14.0f, 0.0f);
        } else {
            lp3 = LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f);
        }
        addView(this.clearSearchImageView, lp3);
        this.clearSearchImageView.setOnClickListener(new SearchField$$ExternalSyntheticLambda0(this));
        AnonymousClass2 r1 = new EditTextBoldCursor(context) {
            public boolean dispatchTouchEvent(MotionEvent event) {
                SearchField.this.processTouchEvent(event);
                return super.dispatchTouchEvent(event);
            }

            public boolean onTouchEvent(MotionEvent event) {
                if (!isEnabled()) {
                    return false;
                }
                if (event.getAction() == 1) {
                    SearchField.this.onFieldTouchUp(this);
                }
                return super.onTouchEvent(event);
            }
        };
        this.searchEditText = r1;
        r1.setTextSize(1, 16.0f);
        this.searchEditText.setHintTextColor(getThemedColor("dialogSearchHint"));
        this.searchEditText.setTextColor(getThemedColor("dialogSearchText"));
        this.searchEditText.setBackgroundDrawable((Drawable) null);
        this.searchEditText.setPadding(0, 0, 0, 0);
        this.searchEditText.setMaxLines(1);
        this.searchEditText.setLines(1);
        this.searchEditText.setSingleLine(true);
        this.searchEditText.setGravity((supportRtl ? LayoutHelper.getAbsoluteGravityStart() : 3) | 16);
        this.searchEditText.setImeOptions(NUM);
        this.searchEditText.setCursorColor(getThemedColor("featuredStickers_addedIcon"));
        this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
        this.searchEditText.setCursorWidth(1.5f);
        if (supportRtl) {
            lp4 = LayoutHelper.createFrameRelatively(-1.0f, 40.0f, 8388659, 54.0f, 9.0f, 46.0f, 0.0f);
        } else {
            lp4 = LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f);
        }
        addView(this.searchEditText, lp4);
        this.searchEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                boolean showed = true;
                boolean show = SearchField.this.searchEditText.length() > 0;
                float f = 0.0f;
                if (SearchField.this.clearSearchImageView.getAlpha() == 0.0f) {
                    showed = false;
                }
                if (show != showed) {
                    ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                    float f2 = 1.0f;
                    if (show) {
                        f = 1.0f;
                    }
                    ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150).scaleX(show ? 1.0f : 0.1f);
                    if (!show) {
                        f2 = 0.1f;
                    }
                    scaleX.scaleY(f2).start();
                }
                SearchField searchField = SearchField.this;
                searchField.onTextChange(searchField.searchEditText.getText().toString());
            }
        });
        this.searchEditText.setOnEditorActionListener(new SearchField$$ExternalSyntheticLambda1(this));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-SearchField  reason: not valid java name */
    public /* synthetic */ void m4316lambda$new$0$orgtelegramuiComponentsSearchField(View v) {
        this.searchEditText.setText("");
        AndroidUtilities.showKeyboard(this.searchEditText);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-SearchField  reason: not valid java name */
    public /* synthetic */ boolean m4317lambda$new$1$orgtelegramuiComponentsSearchField(TextView v, int actionId, KeyEvent event) {
        if (event == null) {
            return false;
        }
        if ((event.getAction() != 1 || event.getKeyCode() != 84) && (event.getAction() != 0 || event.getKeyCode() != 66)) {
            return false;
        }
        this.searchEditText.hideActionMode();
        AndroidUtilities.hideKeyboard(this.searchEditText);
        return false;
    }

    public void hideKeyboard() {
        AndroidUtilities.hideKeyboard(this.searchEditText);
    }

    public void setHint(String text) {
        this.searchEditText.setHint(text);
    }

    /* access modifiers changed from: protected */
    public void onFieldTouchUp(EditTextBoldCursor editText) {
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    public void processTouchEvent(MotionEvent event) {
    }

    public void onTextChange(String text) {
    }

    public View getSearchBackground() {
        return this.searchBackground;
    }

    public EditTextBoldCursor getSearchEditText() {
        return this.searchEditText;
    }

    public CloseProgressDrawable2 getProgressDrawable() {
        return this.progressDrawable;
    }

    public void getThemeDescriptions(List<ThemeDescription> descriptions) {
        List<ThemeDescription> list = descriptions;
        list.add(new ThemeDescription(this.searchBackground, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchBackground"));
        list.add(new ThemeDescription(this.searchIconImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchIcon"));
        list.add(new ThemeDescription(this.clearSearchImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchIcon"));
        list.add(new ThemeDescription(this.searchEditText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchText"));
        list.add(new ThemeDescription(this.searchEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchHint"));
        list.add(new ThemeDescription(this.searchEditText, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon"));
    }

    /* access modifiers changed from: private */
    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
