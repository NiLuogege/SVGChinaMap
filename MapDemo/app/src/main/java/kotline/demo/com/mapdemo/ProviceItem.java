package kotline.demo.com.mapdemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

/**
 * Created by ${LuoChen} on 2017/6/5 14:02.
 * email:luochen0519@foxmail.com
 */

public class ProviceItem {
    private Path mPath;
    private int mdrawColor;

    /**
     * 绘制自己省份的方法
     *
     * @param canvas
     * @param paint
     * @param isSelected
     */
    public void onDraw(Canvas canvas, Paint paint, boolean isSelected) {
        if (isSelected) {//选中画描边
            /**
             * 画后面的黑色层
             */
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(2);
            canvas.drawPath(mPath, paint);

            /**
             * 画真实的
             */
            paint.setColor(mdrawColor);
            paint.setStrokeWidth(1);
            canvas.drawPath(mPath, paint);

        } else {
            paint.setColor(mdrawColor);
            paint.setStrokeWidth(1);
            canvas.drawPath(mPath, paint);
        }

    }

    /**
     * 确定自己是否被点击
     *
     * @param x 用户点击的X坐标
     * @param y 用户点击的Y坐标
     * @return
     */
    public boolean isSelected(float x, float y) {
        //构造一个区域
        RectF rectF = new RectF();
//计算控制点的边界--->计算path的边界
        mPath.computeBounds(rectF, true);
        Region region = new Region();
        region.setPath(mPath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        boolean contains = region.contains((int) x, (int) y);
        return contains;
    }

    public Path getPath() {
        return mPath;
    }

    public void setPath(Path path) {
        mPath = path;
    }

    public int getMdrawColor() {
        return mdrawColor;
    }

    public void setMdrawColor(int mdrawColor) {
        this.mdrawColor = mdrawColor;
    }
}
