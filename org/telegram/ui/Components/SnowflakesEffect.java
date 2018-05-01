package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.os.Build.VERSION;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import java.security.SecureRandom;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;

public class SnowflakesEffect
{
  final float angleDiff = 1.0471976F;
  private ArrayList<Particle> freeParticles = new ArrayList();
  private long lastAnimationTime;
  private Paint particlePaint = new Paint(1);
  private Paint particleThinPaint;
  private ArrayList<Particle> particles = new ArrayList();
  
  public SnowflakesEffect()
  {
    this.particlePaint.setStrokeWidth(AndroidUtilities.dp(1.5F));
    this.particlePaint.setColor(Theme.getColor("actionBarDefaultTitle") & 0xFFE6E6E6);
    this.particlePaint.setStrokeCap(Paint.Cap.ROUND);
    this.particlePaint.setStyle(Paint.Style.STROKE);
    this.particleThinPaint = new Paint(1);
    this.particleThinPaint.setStrokeWidth(AndroidUtilities.dp(0.5F));
    this.particleThinPaint.setColor(Theme.getColor("actionBarDefaultTitle") & 0xFFE6E6E6);
    this.particleThinPaint.setStrokeCap(Paint.Cap.ROUND);
    this.particleThinPaint.setStyle(Paint.Style.STROKE);
    for (int i = 0; i < 20; i++) {
      this.freeParticles.add(new Particle(null));
    }
  }
  
  private void updateParticles(long paramLong)
  {
    int i = this.particles.size();
    int j = 0;
    while (j < i)
    {
      Particle localParticle = (Particle)this.particles.get(j);
      if (localParticle.currentTime >= localParticle.lifeTime)
      {
        if (this.freeParticles.size() < 40) {
          this.freeParticles.add(localParticle);
        }
        this.particles.remove(j);
        j--;
        i--;
        j++;
      }
      else
      {
        if (localParticle.currentTime < 200.0F) {}
        for (localParticle.alpha = AndroidUtilities.accelerateInterpolator.getInterpolation(localParticle.currentTime / 200.0F);; localParticle.alpha = (1.0F - AndroidUtilities.decelerateInterpolator.getInterpolation((localParticle.currentTime - 200.0F) / (localParticle.lifeTime - 200.0F))))
        {
          localParticle.x += localParticle.vx * localParticle.velocity * (float)paramLong / 500.0F;
          localParticle.y += localParticle.vy * localParticle.velocity * (float)paramLong / 500.0F;
          localParticle.currentTime += (float)paramLong;
          break;
        }
      }
    }
  }
  
  public void onDraw(View paramView, Canvas paramCanvas)
  {
    if ((paramView == null) || (paramCanvas == null)) {
      return;
    }
    int i = this.particles.size();
    for (int j = 0; j < i; j++) {
      ((Particle)this.particles.get(j)).draw(paramCanvas);
    }
    label85:
    float f1;
    float f2;
    float f3;
    float f4;
    float f5;
    float f6;
    float f7;
    if ((Utilities.random.nextFloat() > 0.7F) && (this.particles.size() < 100))
    {
      if (Build.VERSION.SDK_INT < 21) {
        break label346;
      }
      j = AndroidUtilities.statusBarHeight;
      f1 = Utilities.random.nextFloat();
      f2 = paramView.getMeasuredWidth();
      f3 = j;
      f4 = Utilities.random.nextFloat();
      f5 = paramView.getMeasuredHeight() - AndroidUtilities.dp(20.0F) - j;
      j = Utilities.random.nextInt(40) - 20 + 90;
      f6 = (float)Math.cos(0.017453292519943295D * j);
      f7 = (float)Math.sin(0.017453292519943295D * j);
      if (this.freeParticles.isEmpty()) {
        break label352;
      }
      paramCanvas = (Particle)this.freeParticles.get(0);
      this.freeParticles.remove(0);
    }
    for (;;)
    {
      paramCanvas.x = (f1 * f2);
      paramCanvas.y = (f3 + f4 * f5);
      paramCanvas.vx = f6;
      paramCanvas.vy = f7;
      paramCanvas.alpha = 0.0F;
      paramCanvas.currentTime = 0.0F;
      paramCanvas.scale = (Utilities.random.nextFloat() * 1.2F);
      paramCanvas.type = Utilities.random.nextInt(2);
      paramCanvas.lifeTime = (Utilities.random.nextInt(100) + 2000);
      paramCanvas.velocity = (20.0F + Utilities.random.nextFloat() * 4.0F);
      this.particles.add(paramCanvas);
      long l = System.currentTimeMillis();
      updateParticles(Math.min(17L, l - this.lastAnimationTime));
      this.lastAnimationTime = l;
      paramView.invalidate();
      break;
      label346:
      j = 0;
      break label85;
      label352:
      paramCanvas = new Particle(null);
    }
  }
  
  private class Particle
  {
    float alpha;
    float currentTime;
    float lifeTime;
    float scale;
    int type;
    float velocity;
    float vx;
    float vy;
    float x;
    float y;
    
    private Particle() {}
    
    public void draw(Canvas paramCanvas)
    {
      float f1;
      float f2;
      float f3;
      float f4;
      int i;
      switch (this.type)
      {
      default: 
        SnowflakesEffect.this.particleThinPaint.setAlpha((int)(255.0F * this.alpha));
        f1 = -1.5707964F;
        f2 = AndroidUtilities.dpf2(2.0F) * 2.0F * this.scale;
        f3 = -AndroidUtilities.dpf2(0.57F) * 2.0F * this.scale;
        f4 = AndroidUtilities.dpf2(1.55F) * 2.0F * this.scale;
        i = 0;
      }
      while (i < 6)
      {
        float f5 = (float)Math.cos(f1) * f2;
        float f6 = (float)Math.sin(f1) * f2;
        float f7 = f5 * 0.66F;
        float f8 = f6 * 0.66F;
        paramCanvas.drawLine(this.x, this.y, this.x + f5, this.y + f6, SnowflakesEffect.this.particleThinPaint);
        f5 = (float)(f1 - 1.5707963267948966D);
        float f9 = (float)(Math.cos(f5) * f3 - Math.sin(f5) * f4);
        f6 = (float)(Math.sin(f5) * f3 + Math.cos(f5) * f4);
        paramCanvas.drawLine(this.x + f7, this.y + f8, this.x + f9, this.y + f6, SnowflakesEffect.this.particleThinPaint);
        f6 = (float)(-Math.cos(f5) * f3 - Math.sin(f5) * f4);
        f5 = (float)(-Math.sin(f5) * f3 + Math.cos(f5) * f4);
        paramCanvas.drawLine(this.x + f7, this.y + f8, this.x + f6, this.y + f5, SnowflakesEffect.this.particleThinPaint);
        f1 += 1.0471976F;
        i++;
        continue;
        SnowflakesEffect.this.particlePaint.setAlpha((int)(255.0F * this.alpha));
        paramCanvas.drawPoint(this.x, this.y, SnowflakesEffect.this.particlePaint);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/SnowflakesEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */