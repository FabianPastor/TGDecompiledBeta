package net.hockeyapp.android.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.widget.ImageView;
import java.util.Iterator;
import java.util.Stack;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.ImageUtils;

@SuppressLint({"ViewConstructor"})
public class PaintView extends ImageView {
    private float mX;
    private float mY;
    private Paint paint = new Paint();
    private Path path = new Path();
    private Stack<Path> paths = new Stack();

    @SuppressLint({"StaticFieldLeak"})
    public PaintView(Context context, Uri imageUri, int displayWidth, int displayHeight) {
        super(context);
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setColor(-65536);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeJoin(Join.ROUND);
        this.paint.setStrokeCap(Cap.ROUND);
        this.paint.setStrokeWidth(12.0f);
        new AsyncTask<Object, Void, Bitmap>() {
            protected void onPreExecute() {
                PaintView.this.setAdjustViewBounds(true);
            }

            protected Bitmap doInBackground(Object... args) {
                try {
                    return ImageUtils.decodeSampledBitmap(args[0], args[1], args[2].intValue(), args[3].intValue());
                } catch (Throwable e) {
                    HockeyLog.error("Could not load image into ImageView.", e);
                    return null;
                }
            }

            protected void onPostExecute(Bitmap bm) {
                if (bm != null) {
                    PaintView.this.setImageBitmap(bm);
                }
            }
        }.execute(new Object[]{context, imageUri, Integer.valueOf(displayWidth), Integer.valueOf(displayHeight)});
    }

    public void clearImage() {
        this.paths.clear();
        invalidate();
    }

    public void undo() {
        if (!this.paths.empty()) {
            this.paths.pop();
            invalidate();
        }
    }

    public boolean isClear() {
        return this.paths.empty();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Iterator it = this.paths.iterator();
        while (it.hasNext()) {
            canvas.drawPath((Path) it.next(), this.paint);
        }
        canvas.drawPath(this.path, this.paint);
    }

    private void touchStart(float x, float y) {
        this.path.reset();
        this.path.moveTo(x, y);
        this.mX = x;
        this.mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - this.mX);
        float dy = Math.abs(y - this.mY);
        if (dx >= 4.0f || dy >= 4.0f) {
            this.path.quadTo(this.mX, this.mY, (this.mX + x) / 2.0f, (this.mY + y) / 2.0f);
            this.mX = x;
            this.mY = y;
        }
    }

    private void touchUp() {
        this.path.lineTo(this.mX, this.mY);
        this.paths.push(this.path);
        this.path = new Path();
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case 0:
                touchStart(x, y);
                invalidate();
                break;
            case 1:
                touchUp();
                invalidate();
                break;
            case 2:
                touchMove(x, y);
                invalidate();
                break;
        }
        return true;
    }
}
