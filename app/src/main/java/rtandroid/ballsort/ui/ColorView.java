/*
 * Copyright (C) 2016 RTAndroid Project
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

public class ColorView extends View
{
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
        mInnerPaint = new Paint();
        mInnerPaint.setStyle(Paint.Style.FILL);
        mInnerPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mInnerPaint.setColor(Color.BLACK);

        mOuterPaint = new Paint();
        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mOuterPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        int x = getWidth() / 2;
        int y = getHeight() / 2;
        int radius = Math.min(x, y) - 2;

        canvas.drawCircle(x, y, radius, mInnerPaint);
        canvas.drawCircle(x, y, radius, mOuterPaint);
    }

    public void setColor(int color)
    {
        mInnerPaint.setColor(color);
        invalidate();
    }
}
