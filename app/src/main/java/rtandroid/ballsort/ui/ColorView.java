/*
 * Copyright (C) 2017 RTAndroid Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rtandroid.ballsort.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import rtandroid.ballsort.settings.Constants;

public class ColorView extends View
{
    private Paint mSplitPaint = null;
    private Paint mInnerPaint = null;
    private Paint mOuterPaint = null;

    public ColorView(Context context)
    {
        super(context);
        init();
    }

    public ColorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ColorView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        mSplitPaint = new Paint();
        mSplitPaint.setStyle(Paint.Style.FILL);
        mSplitPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mSplitPaint.setColor(Color.GRAY);

        mInnerPaint = new Paint();
        mInnerPaint.setStyle(Paint.Style.FILL);
        mInnerPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mInnerPaint.setColor(Color.BLACK);

        mOuterPaint = new Paint();
        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mOuterPaint.setColor(Color.BLACK);
        mOuterPaint.setStrokeWidth(Constants.PATTERN_IMAGE_BORDER);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        int x = getWidth() / 2;
        int y = getHeight() / 2;

        int borderSize = Constants.PATTERN_IMAGE_BORDER;
        int splitSize = 2;

        int fullRadius = Math.min(x, y);
        int splitRadius = fullRadius - splitSize;
        int outerRadius = splitRadius - splitSize * 3;
        int innerRadius = outerRadius - borderSize;

        canvas.drawCircle(x, y, splitRadius, mSplitPaint);
        canvas.drawCircle(x, y, innerRadius, mInnerPaint);
        canvas.drawCircle(x, y, outerRadius, mOuterPaint);
    }

    public void setInnerColor(int color)
    {
        mInnerPaint.setColor(color);
        invalidate();
    }

    public void setOuterColor(int color)
    {
        mOuterPaint.setColor(color);
        invalidate();
    }
}
