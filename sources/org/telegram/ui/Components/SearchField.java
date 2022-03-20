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

    /* access modifiers changed from: protected */
    public void onFieldTouchUp(EditTextBoldCursor editTextBoldCursor) {
    }

    public void onTextChange(String str) {
    }

    public void processTouchEvent(MotionEvent motionEvent) {
    }

    public SearchField(Context context, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        FrameLayout.LayoutParams layoutParams;
        FrameLayout.LayoutParams layoutParams2;
        FrameLayout.LayoutParams layoutParams3;
        FrameLayout.LayoutParams layoutParams4;
        this.resourcesProvider = resourcesProvider2;
        View view = new View(context);
        this.searchBackground = view;
        view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), getThemedColor("dialogSearchBackground")));
        if (z) {
            layoutParams = LayoutHelper.createFrameRelatively(-1.0f, 36.0f, 8388659, 14.0f, 11.0f, 14.0f, 0.0f);
        } else {
            layoutParams = LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f);
        }
        addView(this.searchBackground, layoutParams);
        ImageView imageView = new ImageView(context);
        this.searchIconImageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.searchIconImageView.setImageResource(NUM);
        this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogSearchIcon"), PorterDuff.Mode.MULTIPLY));
        if (z) {
            layoutParams2 = LayoutHelper.createFrameRelatively(36.0f, 36.0f, 8388659, 16.0f, 11.0f, 0.0f, 0.0f);
        } else {
            layoutParams2 = LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f);
        }
        addView(this.searchIconImageView, layoutParams2);
        ImageView imageView2 = new ImageView(context);
        this.clearSearchImageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        ImageView imageView3 = this.clearSearchImageView;
        AnonymousClass1 r0 = new CloseProgressDrawable2() {
            /* access modifiers changed from: protected */
            public int getCurrentColor() {
                return SearchField.this.getThemedColor("dialogSearchIcon");
            }
        };
        this.progressDrawable = r0;
        imageView3.setImageDrawable(r0);
        this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
        this.clearSearchImageView.setScaleX(0.1f);
        this.clearSearchImageView.setScaleY(0.1f);
        this.clearSearchImageView.setAlpha(0.0f);
        if (z) {
            layoutParams3 = LayoutHelper.createFrameRelatively(36.0f, 36.0f, 8388661, 14.0f, 11.0f, 14.0f, 0.0f);
        } else {
            layoutParams3 = LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f);
        }
        addView(this.clearSearchImageView, layoutParams3);
        this.clearSearchImageView.setOnClickListener(new SearchField$$ExternalSyntheticLambda0(this));
        AnonymousClass2 r13 = new EditTextBoldCursor(context) {
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                SearchField.this.processTouchEvent(motionEvent);
                return super.dispatchTouchEvent(motionEvent);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (!isEnabled()) {
                    return false;
                }
                if (motionEvent.getAction() == 1) {
                    SearchField.this.onFieldTouchUp(this);
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.searchEditText = r13;
        r13.setTextSize(1, 16.0f);
        this.searchEditText.setHintTextColor(getThemedColor("dialogSearchHint"));
        this.searchEditText.setTextColor(getThemedColor("dialogSearchText"));
        this.searchEditText.setBackgroundDrawable((Drawable) null);
        this.searchEditText.setPadding(0, 0, 0, 0);
        this.searchEditText.setMaxLines(1);
        this.searchEditText.setLines(1);
        this.searchEditText.setSingleLine(true);
        this.searchEditText.setGravity((z ? LayoutHelper.getAbsoluteGravityStart() : 3) | 16);
        this.searchEditText.setImeOptions(NUM);
        this.searchEditText.setCursorColor(getThemedColor("featuredStickers_addedIcon"));
        this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
        this.searchEditText.setCursorWidth(1.5f);
        if (z) {
            layoutParams4 = LayoutHelper.createFrameRelatively(-1.0f, 40.0f, 8388659, 54.0f, 9.0f, 46.0f, 0.0f);
        } else {
            layoutParams4 = LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f);
        }
        addView(this.searchEditText, layoutParams4);
        this.searchEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                boolean z = true;
                boolean z2 = SearchField.this.searchEditText.length() > 0;
                float f = 0.0f;
                if (SearchField.this.clearSearchImageView.getAlpha() == 0.0f) {
                    z = false;
                }
                if (z2 != z) {
                    ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                    float f2 = 1.0f;
                    if (z2) {
                        f = 1.0f;
                    }
                    ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150).scaleX(z2 ? 1.0f : 0.1f);
                    if (!z2) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        this.searchEditText.setText("");
        AndroidUtilities.showKeyboard(this.searchEditText);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$1(TextView textView, int i, KeyEvent keyEvent) {
        if (keyEvent == null) {
            return false;
        }
        if ((keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 84) && (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
            return false;
        }
        this.searchEditText.hideActionMode();
        AndroidUtilities.hideKeyboard(this.searchEditText);
        return false;
    }

    public void setHint(String str) {
        this.searchEditText.setHint(str);
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        super.requestDisallowInterceptTouchEvent(z);
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

    public void getThemeDescriptions(List<ThemeDescription> list) {
        List<ThemeDescription> list2 = list;
        list2.add(new ThemeDescription(this.searchBackground, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchBackground"));
        list2.add(new ThemeDescription(this.searchIconImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchIcon"));
        list2.add(new ThemeDescription(this.clearSearchImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchIcon"));
        list2.add(new ThemeDescription(this.searchEditText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchText"));
        list2.add(new ThemeDescription(this.searchEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogSearchHint"));
        list2.add(new ThemeDescription(this.searchEditText, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon"));
    }

    /* access modifiers changed from: private */
    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
