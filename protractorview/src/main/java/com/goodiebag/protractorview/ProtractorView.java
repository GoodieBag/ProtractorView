/*
MIT License
Copyright (c) 2017 GoodieBag
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.goodiebag.protractorview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * This class implements a widget for android which is a semi-circular seekbar drawn like a protractor with ticks.
 * Tick angles can be defined, angle value can be drawn at a desired interval of angles.
 * Ticks are highlighted as the seekbar passes through the respective tick's angle.
 * Date : 10/01/17.
 *
 * @author Krishanu
 * @author Pavan
 * @author Koushik
 */

public class ProtractorView extends View {

    private static final int MAX = 180;
    private final float DENSITY = getContext().getResources().getDisplayMetrics().density;

    /**
     * Private variables
     */
    //Rectangle for the arc
    private RectF mArcRect = new RectF();

    //Paints required for drawing
    private Paint mArcPaint;
    private Paint mArcProgressPaint;
    private Paint mTickPaint;
    private Paint mTickProgressPaint;
    private Paint mTickTextPaint;
    private Paint mTickTextColoredPaint;

    //Arc related dimens
    private int mArcRadius = 0;
    private int mArcWidth = 2;
    private int mArcProgressWidth = 2;
    private boolean mRoundedEdges = true;

    //Thumb Drawable
    private Drawable mThumb;

    //Thumb position related coordinates
    private int mTranslateX;
    private int mTranslateY;
    private int mThumbXPos;
    private int mThumbYPos;

    private int mAngleTextSize = 12;

    private int mTickOffset = 12;
    private int mTickLength = 10;
    private int mTickWidth = 2;
    private int mTickProgressWidth = 2;
    private int mAngle = 0;
    private boolean mTouchInside = true;
    private boolean mEnabled = true;
    private TicksBetweenLabel mTicksBetweenLabel = TicksBetweenLabel.TWO;
    private int mTickIntervals = 15;
    private double mTouchAngle = 0;
    private float mTouchIgnoreRadius;

    //Event listener
    private OnProtractorViewChangeListener mOnProtractorViewChangeListener = null;


    //Interface for event listener
    public interface OnProtractorViewChangeListener {
        void onProgressChanged(ProtractorView protractorView, int progress, boolean fromUser);

        void onStartTrackingTouch(ProtractorView protractorView);

        void onStopTrackingTouch(ProtractorView protractorView);
    }

    public enum TicksBetweenLabel {
        ZERO, ONE, TWO, THREE
    }

    public ProtractorView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ProtractorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.protractorViewStyle);
    }

    public ProtractorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * A method to initialise the attributes from the XML.
     * If not specified in the xml the default values are taken.
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    private void init(Context context, AttributeSet attrs, int defStyle) {

        final Resources res = getResources();

        /**
         * Defaults, may need to link this into theme settings
         */
        int arcColor = res.getColor(R.color.progress_gray);
        int arcProgressColor = res.getColor(R.color.default_blue_light);
        int textColor = res.getColor(R.color.progress_gray);
        int textProgressColor = res.getColor(R.color.default_blue_light);
        int tickColor = res.getColor(R.color.progress_gray);
        int tickProgressColor = res.getColor(R.color.default_blue_light);
        int thumbHalfHeight = 0;
        int thumbHalfWidth = 0;

        mThumb = res.getDrawable(R.drawable.thumb_selector);

        /**
         * Convert all default dimens to pixels for current density
         */
        mArcWidth = (int) (mArcWidth * DENSITY);
        mArcProgressWidth = (int) (mArcProgressWidth * DENSITY);
        mAngleTextSize = (int) (mAngleTextSize * DENSITY);
        mTickOffset = (int) (mTickOffset * DENSITY);
        mTickLength = (int) (mTickLength * DENSITY);
        mTickWidth = (int) (mTickWidth * DENSITY);
        mTickProgressWidth = (int) (mTickProgressWidth * DENSITY);

        if (attrs != null) {
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ProtractorView, defStyle, 0);
            Drawable thumb = array.getDrawable(R.styleable.ProtractorView_thumb);
            if (thumb != null) {
                mThumb = thumb;
            }
            thumbHalfHeight = mThumb.getIntrinsicHeight() / 2;
            thumbHalfWidth = mThumb.getIntrinsicWidth() / 2;
            mThumb.setBounds(-thumbHalfWidth, -thumbHalfHeight, thumbHalfWidth, thumbHalfHeight);
            //Dimensions
            mAngleTextSize = (int) array.getDimension(R.styleable.ProtractorView_angleTextSize, mAngleTextSize);
            mArcProgressWidth = (int) array.getDimension(R.styleable.ProtractorView_progressWidth, mArcProgressWidth);
            mTickOffset = (int) array.getDimension(R.styleable.ProtractorView_tickOffset, mTickOffset);
            mTickLength = (int) array.getDimension(R.styleable.ProtractorView_tickLength, mTickLength);
            mArcWidth = (int) array.getDimension(R.styleable.ProtractorView_arcWidth, mArcWidth);
            //Integers
            mAngle = array.getInteger(R.styleable.ProtractorView_angle, mAngle);
            mTickIntervals = array.getInt(R.styleable.ProtractorView_tickIntervals, mTickIntervals);
            //Colors
            arcColor = array.getColor(R.styleable.ProtractorView_arcColor, arcColor);
            arcProgressColor = array.getColor(R.styleable.ProtractorView_arcProgressColor, arcProgressColor);
            textColor = array.getColor(R.styleable.ProtractorView_textColor, textColor);
            textProgressColor = array.getColor(R.styleable.ProtractorView_textProgressColor, textProgressColor);
            tickColor = array.getColor(R.styleable.ProtractorView_tickColor, tickColor);
            tickProgressColor = array.getColor(R.styleable.ProtractorView_tickProgressColor, tickProgressColor);
            //Boolean
            mRoundedEdges = array.getBoolean(R.styleable.ProtractorView_roundEdges, mRoundedEdges);
            mEnabled = array.getBoolean(R.styleable.ProtractorView_enabled, mEnabled);
            mTouchInside = array.getBoolean(R.styleable.ProtractorView_touchInside, mTouchInside);
            int ordinal = array.getInt(R.styleable.ProtractorView_ticksBetweenLabel, mTicksBetweenLabel.ordinal());
            mTicksBetweenLabel = TicksBetweenLabel.values()[ordinal];

        }
        /**
         * Creating and configuring the paints as  required.
         */
        mAngle = (mAngle > MAX) ? MAX : ((mAngle < 0) ? 0 : mAngle);

        mArcPaint = new Paint();
        mArcPaint.setColor(arcColor);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);

        mArcProgressPaint = new Paint();
        mArcProgressPaint.setColor(arcProgressColor);
        mArcProgressPaint.setAntiAlias(true);
        mArcProgressPaint.setStyle(Paint.Style.STROKE);
        mArcProgressPaint.setStrokeWidth(mArcProgressWidth);

        if (mRoundedEdges) {
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
            mArcProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        mTickPaint = new Paint();
        mTickPaint.setColor(tickColor);
        mTickPaint.setAntiAlias(true);
        mTickPaint.setStyle(Paint.Style.STROKE);
        mTickPaint.setStrokeWidth(mTickWidth);


        mTickProgressPaint = new Paint();
        mTickProgressPaint.setColor(tickProgressColor);
        mTickProgressPaint.setAntiAlias(true);
        mTickProgressPaint.setStyle(Paint.Style.STROKE);
        mTickProgressPaint.setStrokeWidth(mTickProgressWidth);

        mTickTextPaint = new Paint();
        mTickTextPaint.setColor(textColor);
        mTickTextPaint.setAntiAlias(true);
        mTickTextPaint.setStyle(Paint.Style.FILL);
        mTickTextPaint.setTextSize(mAngleTextSize);
        mTickTextPaint.setTextAlign(Paint.Align.CENTER);

        mTickTextColoredPaint = new Paint();
        mTickTextColoredPaint.setColor(textProgressColor);
        mTickTextColoredPaint.setAntiAlias(true);
        mTickTextColoredPaint.setStyle(Paint.Style.FILL);
        mTickTextColoredPaint.setTextSize(mAngleTextSize);
        mTickTextColoredPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
        int min = Math.min(width, height);
        //width = min;
        height = min / 2;


        float top = 0;
        float left = 0;
        int arcDiameter = 0;

        int tickEndToArc = (mTickOffset + mTickLength);

        arcDiameter = min - 2 * tickEndToArc;
        arcDiameter = (int) (arcDiameter - 2 * 20 * DENSITY);
        mArcRadius = arcDiameter / 2;


        top = height - (mArcRadius);
        left = width / 2 - mArcRadius;

        mArcRect.set(left, top, left + arcDiameter, top + arcDiameter);

        mTranslateX = (int) mArcRect.centerX();
        mTranslateY = (int) mArcRect.centerY();


        int thumbAngle = mAngle;
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
        setTouchInside(mTouchInside);
        setMeasuredDimension(width, height + tickEndToArc);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(1, -1, mArcRect.centerX(), mArcRect.centerY());
        canvas.drawArc(mArcRect, 0, MAX, false, mArcPaint);
        canvas.drawArc(mArcRect, 0, mAngle, false, mArcProgressPaint);

        canvas.restore();
        double slope, startTickX, startTickY, endTickX, endTickY, midTickX, midTickY, thetaInRadians;
        double radiusOffset = mArcRadius + mTickOffset;

        //TicksBetweenLabel
        /**
         * Mechanism to draw the tick and text.
         * Tan(theta) gives the slope.
         * Formula for a straight line is y = mx + c. y is calculated for varying values of x and the ticks are drawn.
         */

        int count = mTicksBetweenLabel.ordinal();
        for (int i = 360; i >= 180; i -= mTickIntervals) {
            canvas.save();
            if (count == mTicksBetweenLabel.ordinal()) {
                //for text
                canvas.translate(mArcRect.centerX(), mArcRect.centerY());
                thetaInRadians = Math.toRadians(i);
                slope = Math.tan(thetaInRadians);
                startTickX = (radiusOffset * Math.cos(thetaInRadians));
                midTickX = startTickX + (((mTickLength / 2)) * Math.cos(thetaInRadians));
                midTickY = slope * midTickX;
                canvas.drawText("" + (360 - i), (float) midTickX, (float) midTickY, (mAngle <= 359 - i) ? mTickTextPaint : mTickTextColoredPaint);
                count = 0;
            } else {
                //for tick
                canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY());
                canvas.translate(mArcRect.centerX(), mArcRect.centerY());
                canvas.rotate(180);
                thetaInRadians = Math.toRadians(360 - i);
                slope = Math.tan(thetaInRadians);
                startTickX = (radiusOffset * Math.cos(thetaInRadians));
                startTickY = slope * startTickX;
                endTickX = startTickX + ((mTickLength) * Math.cos(thetaInRadians));
                endTickY = slope * endTickX;
                canvas.drawLine((float) startTickX, (float) startTickY, (float) endTickX, (float) endTickY, (mAngle <= 359 - i) ? mTickPaint : mTickProgressPaint);
                count++;
            }
            canvas.restore();
        }


        if (mEnabled) {
            // Draw the thumb nail
            canvas.save();
            canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY());
            canvas.translate(mTranslateX - mThumbXPos, mTranslateY - mThumbYPos);
            mThumb.draw(canvas);
            canvas.restore();
        }
    }


    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mThumb != null && mThumb.isStateful()) {
            int[] state = getDrawableState();
            mThumb.setState(state);
        }
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnabled) {
            this.getParent().requestDisallowInterceptTouchEvent(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (ignoreTouch(event.getX(), event.getY())) {
                        return false;
                    }
                    onStartTrackingTouch();
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_UP:
                    onStopTrackingTouch();
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    onStopTrackingTouch();
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        }
        return false;
    }

    private void onStartTrackingTouch() {
        if (mOnProtractorViewChangeListener != null) {
            mOnProtractorViewChangeListener.onStartTrackingTouch(this);
        }
    }

    private void onStopTrackingTouch() {
        if (mOnProtractorViewChangeListener != null) {
            mOnProtractorViewChangeListener.onStopTrackingTouch(this);
        }
    }


    private boolean ignoreTouch(float xPos, float yPos) {
        boolean ignore = false;
        float x = xPos - mTranslateX;
        float y = yPos - mTranslateY;

        float touchRadius = (float) Math.sqrt(((x * x) + (y * y)));
        if (touchRadius < mTouchIgnoreRadius || touchRadius > (mArcRadius + mTickLength + mTickOffset)) {
            ignore = true;
        }
        return ignore;
    }

    private void updateOnTouch(MotionEvent event) {
        boolean ignoreTouch = ignoreTouch(event.getX(), event.getY());
        if (ignoreTouch) {
            return;
        }
        setPressed(true);
        mTouchAngle = getTouchDegrees(event.getX(), event.getY());
        onProgressRefresh((int) mTouchAngle, true);
    }


    private double getTouchDegrees(float xPos, float yPos) {
        float x = xPos - mTranslateX;
        float y = yPos - mTranslateY;
        x = -x;
        // convert to arc Angle
        double angle = Math.toDegrees(Math.atan2(y, x) + (Math.PI));
        if (angle > 270)
            angle = 0;
        else if (angle > 180)
            angle = 180;
        return angle;
    }

    private void onProgressRefresh(int angle, boolean fromUser) {
        updateAngle(angle, fromUser);
    }

    private void updateAngle(int angle, boolean fromUser) {
        mAngle = (angle > MAX) ? MAX : (angle < 0) ? 0 : angle;

        if (mOnProtractorViewChangeListener != null) {
            mOnProtractorViewChangeListener.onProgressChanged(this, mAngle, fromUser);
        }
        updateThumbPosition();
        invalidate();
    }


    private void updateThumbPosition() {
        int thumbAngle = mAngle; //(int) (mStartAngle + mProgressSweep + mRotation + 90);
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
    }


    //*****************************************************
    // Setters and Getters
    //*****************************************************

    public boolean getTouchInside() {
        return mTouchInside;
    }

    public void setTouchInside(boolean isEnabled) {
        int thumbHalfheight = (int) mThumb.getIntrinsicHeight() / 2;
        int thumbHalfWidth = (int) mThumb.getIntrinsicWidth() / 2;
        mTouchInside = isEnabled;
        if (mTouchInside) {
            mTouchIgnoreRadius = (float) (mArcRadius / 1.5);
        } else {
            mTouchIgnoreRadius = mArcRadius - Math.min(thumbHalfWidth, thumbHalfheight);
        }
    }

    public void setOnProtractorViewChangeListener(OnProtractorViewChangeListener l) {
        mOnProtractorViewChangeListener = l;
    }

    public OnProtractorViewChangeListener getOnProtractorViewChangeListener() {
        return mOnProtractorViewChangeListener;
    }

    public int getAngle() {
        return mAngle;
    }

    public void setAngle(int angle) {
        this.mAngle = angle;
        onProgressRefresh(mAngle, false);
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
        invalidate();
    }

    public int getProgressColor() {
        return mArcProgressPaint.getColor();
    }

    public void setProgressColor(@ColorInt int color) {
        mArcProgressPaint.setColor(color);
        invalidate();
    }

    public int getArcColor() {
        return mArcPaint.getColor();
    }

    public void setArcColor(@ColorInt int color) {
        mArcPaint.setColor(color);
        invalidate();
    }

    public int getArcProgressWidth() {
        return mArcProgressWidth;
    }

    public void setArcProgressWidth(int arcProgressWidth) {
        this.mArcProgressWidth = arcProgressWidth;
        mArcProgressPaint.setStrokeWidth(arcProgressWidth);
        invalidate();
    }

    public int getArcWidth() {
        return mArcWidth;
    }

    public void setArcWidth(int arcWidth) {
        this.mArcWidth = arcWidth;
        mArcPaint.setStrokeWidth(arcWidth);
        invalidate();
    }

    public boolean isRoundedEdges() {
        return mRoundedEdges;
    }

    public void setRoundedEdges(boolean roundedEdges) {
        this.mRoundedEdges = roundedEdges;
        if (roundedEdges) {
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
            mArcProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        } else {
            mArcPaint.setStrokeCap(Paint.Cap.SQUARE);
            mArcPaint.setStrokeCap(Paint.Cap.SQUARE);
        }
        invalidate();
    }

    public Drawable getThumb() {
        return mThumb;
    }

    public void setThumb(Drawable thumb) {
        this.mThumb = thumb;
        invalidate();
    }

    public int getAngleTextSize() {
        return mAngleTextSize;
    }

    public void setAngleTextSize(int angleTextSize) {
        this.mAngleTextSize = angleTextSize;
        invalidate();
    }

    public int getTickOffset() {
        return mTickOffset;
    }

    public void setTickOffset(int tickOffset) {
        this.mTickOffset = tickOffset;
    }

    public int getTickLength() {
        return mTickLength;
    }

    public void setTickLength(int tickLength) {
        this.mTickLength = tickLength;
    }

    public TicksBetweenLabel getTicksBetweenLabel() {
        return mTicksBetweenLabel;
    }

    public void setTicksBetweenLabel(TicksBetweenLabel ticksBetweenLabel) {
        this.mTicksBetweenLabel = mTicksBetweenLabel;
        invalidate();
    }

    public int getTickIntervals() {
        return mTickIntervals;
    }

    public void setTickIntervals(int tickIntervals) {
        this.mTickIntervals = tickIntervals;
        invalidate();
    }
}
