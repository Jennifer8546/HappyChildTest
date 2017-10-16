package com.example.vrml.happychildapp.Turn_page;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

import com.example.vrml.happychildapp.R;

import java.util.ArrayList;
import java.util.List;


public class turnView extends View {
    private static final float CURVATURE = 1 / 4F;
    private static final float BUFF_AREA = 1 / 5F; //底部緩衝區域佔比
    private static final float Auto_Area_Button_Right = 3 / 5F, Auto_Area_Button_Left = 1 / 2F;//左側與右下角自動滑入比例
    private static final float AUTO_SLIDE_BL_V = 1 / 25F, AUTO_SLIDE_BR_V = 1 / 100F;//滑動參數
    private static final float VALUE_ADDED = 1 / 500F; //精準度佔的比例

    private List<Bitmap> mBitmaps;// 全部的圖片

    int width, height;
    float startX, startY;
    private float mCurPointX = 0, mCurPointY = 0; //當前事件觸控點的X,Y座標

    private Path mPath;//摺疊路徑
    private Path mPathFoldAndNext;//一塊包含摺疊頁和下一頁區域的Path

    private Slide mslide;
    private boolean isSlide, isNextPage, isLastPage;

    private int pageIndex;//目前顯示Bitmap的數據標
    private float mAutoSlideV_BL, mAutoSlideV_BR;//滑動速度
    private float mAutoAreaButton, mAutoAreaRight, mAutoAreaLeft;//右下角與左側自動滑入
    private float mBuffArea; //底部緩衝區域
    private float mDegrees;//目前Y邊與Y軸的夾角

    private float mValueAdded;//精準度

    private Region mRegionShortSize;//短邊的有效區域
    private Region mRegionCurrent;//當前頁區域(矩形的大小)

    private Region regionFold ;
    private Region regionNext ;

    private SlideHandler mSlideHandler = new SlideHandler();
    private Ratio mRatio;

    public turnView(Context context) {
        super(context);
        mRegionShortSize = new Region();
        mRegionCurrent = new Region();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath = new Path();
        //drawBitmap(canvas);

        Paint mPaint = new Paint();
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);
        Path mPathTrap = new Path();
        Path mPathSemicircleBtm = new Path();
        Path mPathSemicircleLeft = new Path();
        Region mRegionSemicircle = new Region();

        if (null == mBitmaps || mBitmaps.size() == 0) {
            //  應該告訴使用者壞掉了RRRR
            return;
        }
        mPath.reset();
        mPathFoldAndNext.reset();

        if (mCurPointX == 0 && mCurPointY == 0) {
            canvas.drawBitmap(mBitmaps.get(mBitmaps.size() - 1), 0, 0, null);
            return;
        }

        if (!mRegionShortSize.contains((int) mCurPointX, (int) mCurPointY)) {

            mCurPointY = (float) (Math.sqrt((Math.pow(width, 2) - Math.pow(mCurPointX, 2))) - height);
            mCurPointY = Math.abs(mCurPointY)+ mValueAdded;

        }
        float area = height - mBuffArea;//緩衝區判斷
        if (!isSlide && mCurPointY >= area) {
            mCurPointY = area;
        }
        float mK = width - mCurPointX;
        float mL = height - mCurPointY;

        float temp = (float) (Math.pow(mK, 2) + Math.pow(mL, 2));//避免重覆計算所需的參數

        float sizeShort = temp / (2F * mK);//計算三角形短邊與長邊
        float sizeLong = temp / (2F * mL);

        float tempAM = mK - sizeShort;

        if (sizeShort < sizeLong) {//根據長短邊計算旋轉角度
            mRatio = Ratio.SHORT;
            float sin = tempAM / sizeShort;
            mDegrees = (float) (Math.asin(sin) / Math.PI * 180);
        } else {
            mRatio = Ratio.LONG;
            float cos = mK / sizeLong;
            mDegrees = (float) (Math.acos(cos) / Math.PI * 180);
        }


        if (sizeLong > height) {
            float an = sizeLong - height;//AN邊
            float largerTrianShortSize = an / (sizeLong - (height - mCurPointY)) * (width - mCurPointX);//MN邊
            float smallTrianShortSize = an / sizeLong * sizeShort;//QN邊

            float topX1 = width - largerTrianShortSize;//參數計算
            float topX2 = width - smallTrianShortSize;
            float btmX2 = width - sizeShort;

            float startXBtm = btmX2 - CURVATURE * sizeShort;//曲線起點G
            float startYBtm = height;

            float endXBtm = mCurPointX + (1 - CURVATURE) * tempAM;//曲線終點F
            float endYBtm = mCurPointY + (1 - CURVATURE) * mL;

            float controlXBtm = btmX2;//控制點A
            float controlYBtm = height;

            float bezierPeakXBtm = 0.25F * startXBtm + 0.5F * controlXBtm + 0.25F * endXBtm;
            float bezierPeakYBtm = 0.25F * startYBtm + 0.5F * controlYBtm + 0.25F * endYBtm;
            //帶曲線的四邊形

            mPath.moveTo(startXBtm, startYBtm);
            mPath.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
            mPath.lineTo(mCurPointX, mCurPointY);
            mPath.lineTo(topX1, 0);
            mPath.lineTo(topX2, 0);

            //補四邊形Path
            mPathTrap.moveTo(startXBtm, startYBtm);
            mPathTrap.lineTo(topX2, 0);
            mPathTrap.lineTo(bezierPeakXBtm, bezierPeakYBtm);
            mPathTrap.close();

            //底部半月Path
            mPathSemicircleBtm.moveTo(startXBtm, startYBtm);
            mPathSemicircleBtm.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
            mPathSemicircleBtm.close();

            mPathFoldAndNext.moveTo(startXBtm,startYBtm);
            mPathFoldAndNext.quadTo(controlXBtm,controlYBtm,endXBtm,endYBtm);
            mPathFoldAndNext.lineTo(mCurPointX,mCurPointY);
            mPathFoldAndNext.lineTo(topX1, 0);
            mPathFoldAndNext.lineTo(width,0);
            mPathFoldAndNext.lineTo(width,height);
            mPathFoldAndNext.close();

            //計算半圓區域
            mRegionSemicircle = computeRegion(mPathSemicircleBtm);

        } else {

            float lefty = height - sizeLong;//參數計算
            float btmX = width - sizeShort;

            float startXBtm = btmX - CURVATURE * sizeShort;
            float startYBtm = height;
            float startXLeft = width;
            float startYLeft = lefty - CURVATURE * sizeLong;

            float endXBtm = mCurPointX + (1 - CURVATURE) * (tempAM);
            float endYBtm = mCurPointY + (1 - CURVATURE) * mL;
            float endXLeft = mCurPointX + (1 - CURVATURE) * mK;
            float endYLeft = mCurPointY - (1 - CURVATURE) * (sizeLong - mL);

            float controlXBtm = btmX;
            float controlYBtm = height;
            float controlXLeft = width;
            float controlYLeft = lefty;

            float bezierPeakXBtm = 0.25F * startXBtm + 0.5F * controlXBtm + 0.25F * endXBtm;
            float bezierPeakYBtm = 0.25F * startYBtm + 0.5F * controlYBtm + 0.25F * endYBtm;
            float bezierPeakXLeft = 0.25F * startXLeft + 0.5F * controlXLeft + 0.25F * endXLeft;
            float bezierPeakYLeft = 0.25F * startYLeft + 0.5F * controlYLeft + 0.25F * endYLeft;

            if (startYLeft <= 0) {//限制右側曲線起點
                startYLeft = 0;
            }

            if (startXBtm <= 0) {//限制左側曲線起點
                startXBtm = 0;
            }

            float partOfShortLength = CURVATURE * sizeShort;
            if (btmX >= -mValueAdded && btmX <= partOfShortLength - mValueAdded) {
                float f = btmX / partOfShortLength;
                float t = 0.5F * f;

                float bezierPeakTemp = 1 - t;
                float bezierPeakTemp1 = bezierPeakTemp * bezierPeakTemp;
                float bezierPeakTemp2 = 2 * t * bezierPeakTemp;
                float bezierPeakTemp3 = t * t;

                bezierPeakXBtm = bezierPeakTemp1 * startXBtm + bezierPeakTemp2 * controlXLeft + bezierPeakTemp3 * endXBtm;
                bezierPeakYBtm = bezierPeakTemp1 * startYBtm + bezierPeakTemp2 * controlYLeft + bezierPeakTemp3 * endYBtm;
            }
            float partOfLongLength = CURVATURE * sizeLong;
            if (lefty >= -mValueAdded && lefty <= partOfLongLength - mValueAdded) {
                float f = lefty / partOfLongLength;
                float t = 0.5F * f;

                float bezierPeakTemp = 1 - t;
                float bezierPeakTemp1 = bezierPeakTemp * bezierPeakTemp;
                float bezierPeakTemp2 = 2 * t * bezierPeakTemp;
                float bezierPeakTemp3 = t * t;

                bezierPeakXLeft = bezierPeakTemp1 * startXLeft + bezierPeakTemp2 * controlXLeft + bezierPeakTemp3 * endXLeft;
                bezierPeakYLeft = bezierPeakTemp1 * startYLeft + bezierPeakTemp2 * controlYLeft + bezierPeakTemp3 * endYLeft;
            }

            //替補區域Path
            mPathTrap.moveTo(startXBtm, startYBtm);
            mPathTrap.lineTo(startXLeft, startYLeft);
            mPathTrap.lineTo(bezierPeakXLeft, bezierPeakYLeft);
            mPathTrap.lineTo(bezierPeakXBtm, bezierPeakYBtm);
            mPathTrap.close();

            //帶曲線的三角形
            mPath.moveTo(startXBtm, startYBtm);
            mPath.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
            mPath.lineTo(mCurPointX, mCurPointY);
            mPath.lineTo(endXLeft, endYLeft);
            mPath.quadTo(controlXLeft, controlYLeft, startXLeft, startYLeft);

            //劃出底部半圓的Path
            mPathSemicircleBtm.moveTo(startXBtm, startYBtm);
            mPathSemicircleBtm.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
            mPathSemicircleBtm.close();

            //生成右側的半圓Path
            mPathSemicircleLeft.moveTo(endXLeft, endYLeft);
            mPathSemicircleLeft.quadTo(controlXLeft, controlYLeft, startXLeft, startYLeft);
            mPathSemicircleLeft.close();

            //生成下摺疊和下頁的路徑

            mPathFoldAndNext.moveTo(startXBtm,startYBtm);
            mPathFoldAndNext.quadTo(controlXBtm,controlYBtm,endXBtm,endYBtm);
            mPathFoldAndNext.lineTo(mCurPointX,mCurPointY);
            mPathFoldAndNext.lineTo(endXLeft,endYLeft);
            mPathFoldAndNext.quadTo(controlXLeft,controlYLeft,startXLeft,startYLeft);
            mPathFoldAndNext.lineTo(width,height);
            mPathFoldAndNext.close();

            //計算底部和右側兩月半圓區域
            Region regionSemicircleBtm = computeRegion(mPathSemicircleBtm);
            Region regionSemicircleLeft = computeRegion(mPathSemicircleLeft);

            //合併兩月半圓區域
            mRegionSemicircle.op(regionSemicircleBtm, regionSemicircleLeft, Region.Op.UNION);
        }
        //根據Path生成的折疊區域
        regionFold = computeRegion(mPath);

        //替補區域
        Region regionTrap = computeRegion(mPathTrap);

        //令摺疊區域與替補區域相加
        regionFold.op(regionTrap, Region.Op.UNION);

        //相加後的區域中替除掉半圓的去愈獲得新的摺頁區
        regionFold.op(mRegionSemicircle, Region.Op.DIFFERENCE);

        regionNext = computeRegion(mPathFoldAndNext);
        regionNext.op(regionFold,Region.Op.DIFFERENCE);

        //canvas.drawPath(mPath, mPaint);
        drawBitmaps(canvas);
    }

    private void initBitmaps() {
        mBitmaps = new ArrayList<>();
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.animal));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.plant));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.facialfeatures));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.lastpage));
        List<Bitmap> temp = new ArrayList<Bitmap>();
        for (int i = mBitmaps.size() - 1; i >= 0; i--) {
            Bitmap bitmap = Bitmap.createScaledBitmap(mBitmaps.get(i), width, height, true);
            temp.add(bitmap);
        }
        mBitmaps = temp;
    }


    private enum Ratio {
        LONG, SHORT
    }

    private void drawBitmaps(Canvas canvas) {
        isLastPage = false;

        pageIndex = pageIndex < 0 ? 0 : pageIndex;//限制pageIndex範圍限制
        pageIndex = pageIndex > mBitmaps.size() ? mBitmaps.size() : pageIndex;

        int start = mBitmaps.size() - 2 - pageIndex;//計算起始位置
        int end = mBitmaps.size() - pageIndex;

        if (start < 0) {//表示小於兩張圖 因此只需要目前到最後一張就好
            isLastPage = true;
            //代入下面的for迴圈印出最後一張

            start = 0;
            end = 1;
        }

        canvas.save();
        canvas.clipRegion(mRegionCurrent);
        canvas.drawBitmap(mBitmaps.get(end - 1), 0, 0, null);
        canvas.restore();

        canvas.save();
        canvas.clipRegion(regionFold);

        canvas.translate(mCurPointX, mCurPointY);

        //根據長短邊標示計算摺疊區域圖像
        if (mRatio == Ratio.SHORT) {
            canvas.rotate(90 - mDegrees);
            canvas.translate(0, -height);
            canvas.scale(-1, 1);
            canvas.translate(-width, 0);
        } else {
            canvas.rotate(-(90 - mDegrees));
            canvas.translate(-width, 0);
            canvas.scale(1, -1);
            canvas.translate(0, -height);
        }

        canvas.drawBitmap(mBitmaps.get(end - 1), 0, 0, null);
        canvas.restore();

        //計算下一頁的區域
        canvas.save();
        canvas.clipRegion(regionNext);
        canvas.drawBitmap(mBitmaps.get(start), 0, 0, null);
        canvas.restore();
    }


    @SuppressLint("HandlerLeak")
    private class SlideHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            turnView.this.slide();
            turnView.this.invalidate();
        }

        public void sleep(long delaMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delaMillis);
        }
    }

    private enum Slide {
        LEFT_BUTTON, RIGHT_BUTTON
    }

    private void slide() {

        if (!isSlide) {
            return;
        }

        if (!isLastPage && isNextPage && (mCurPointX - mAutoSlideV_BL <= -width)) {
            mCurPointX = -width;
            mCurPointY = height;
            pageIndex++;
            invalidate();
        }

        if (mslide == Slide.RIGHT_BUTTON && mCurPointX < width) {
            mCurPointX += mAutoSlideV_BR;
            mCurPointY = startY + ((mCurPointX - startX) * (height - startY)) / (width - startX);
            mSlideHandler.sleep(5);
        }

        if (mslide == Slide.LEFT_BUTTON && mCurPointX > -width) {
            mCurPointX -= mAutoSlideV_BL;
            mCurPointY = startY + ((mCurPointX - startX) * (height - startY)) / (-width - startX);
            mSlideHandler.sleep(5);
        }
    }


    public void slideStop() {
        isSlide = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = getRootView().getWidth();
        height = getRootView().getHeight();
        mPathFoldAndNext = new Path();
        mValueAdded = height * VALUE_ADDED;
        if (null == mBitmaps) {//初始那個圖片
            initBitmaps();
        }

        mBuffArea = height * BUFF_AREA;
        mAutoAreaButton = height * Auto_Area_Button_Right;//計算自動滑入位置
        mAutoAreaRight = width * Auto_Area_Button_Right;
        mAutoAreaLeft = width * Auto_Area_Button_Left;

        mRegionCurrent.set(0, 0, width, height);//計算當前頁區域
        computeShortSizeRegion();

        mAutoSlideV_BL = width * AUTO_SLIDE_BL_V;
        mAutoSlideV_BR = width * AUTO_SLIDE_BR_V;
    }

    private Region computeRegion(Path path) {
        Region region = new Region();
        RectF f = new RectF();

        path.computeBounds(f, true);
        region.setPath(path, new Region((int) f.left, (int) f.top, (int) f.right, (int) f.bottom));
        return region;
    }

    private void computeShortSizeRegion() {
        Path pathShortSize = new Path();
        RectF rectShortSize = new RectF();//把path邊界值存進矩形

        pathShortSize.addCircle(0, height, width, Path.Direction.CCW); //畫圓形到path
        pathShortSize.computeBounds(rectShortSize, true);//計算邊界
        mRegionShortSize.setPath(pathShortSize, new Region(// 將Path轉化為Region
                (int) rectShortSize.left, (int) rectShortSize.top, (int) rectShortSize.right, (int) rectShortSize.bottom));
    }


    public boolean onTouchEvent(MotionEvent event) {
        mCurPointX = event.getX();//獲取觸摸點X的座標
        mCurPointY = event.getY();

        isNextPage = true;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isSlide = false;

                if (mCurPointX < mAutoAreaLeft)//判斷觸點起始位置是否在左邊 左往右滑為上一頁
                {
                    isNextPage = false;
                    pageIndex--;
                    invalidate();
                }
                downAndMove(event);
                break;
            case MotionEvent.ACTION_MOVE:

                downAndMove(event);
                break;
            case MotionEvent.ACTION_UP:
                if (isNextPage) {

                    float x = event.getX(), y = event.getY();
                    if (mCurPointX > mAutoAreaRight && mCurPointY > mAutoAreaButton) {

                        mslide = Slide.RIGHT_BUTTON;//當為右下滑
                        justSlide(x, y);
                    }

                    if (mCurPointX < mAutoAreaLeft) {

                        mslide = Slide.LEFT_BUTTON;//當為左下滑
                        justSlide(x, y);
                    }
                }
                break;
        }
        invalidate();
        return true;
    }

    private void downAndMove(MotionEvent event) {
        if (!isLastPage) {
            mCurPointX = event.getX();
            mCurPointY = event.getY();
            invalidate();
        }
    }

    private void justSlide(float x, float y) {
        startX = x;
        startY = y;

        isSlide = true;//開始滑動

        slide();
    }


}
