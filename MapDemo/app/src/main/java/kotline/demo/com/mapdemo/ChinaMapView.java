package kotline.demo.com.mapdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by ${LuoChen} on 2017/6/5 13:33.
 * email:luochen0519@foxmail.com
 */

public class ChinaMapView extends View {
    private final int minWidth = dp2px(200);
    private final int minHeight = dp2px(200);

    private List<ProviceItem> mProviceItemList = new ArrayList<>();
    private int[] colorArray = new int[]{0xFF239BD7, 0xFF30A9E5, 0xFF80CBF1, 0xFFFFFFFF};
    private float scale = 1f;
    private ProviceItem selectedItem = null;
    private static final int parseEnd = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what == parseEnd) {
                if (BuildConfig.DEBUG)
                    Log.e("ChinaMapView", "mProviceItemList.size():" + mProviceItemList.size());

                int totalNumber = mProviceItemList.size();
                for (int i = 0; i < totalNumber; i++) {
                    int color = Color.WHITE;
                    int flag = i % 4;
                    switch (flag) {
                        case 1:
                            color = colorArray[0];
                            break;
                        case 2:
                            color = colorArray[1];
                            break;
                        case 3:
                            color = colorArray[2];
                            break;
                        default:
                            color = Color.WHITE;
                            break;
                    }
                    mProviceItemList.get(i).setMdrawColor(color);
                    postInvalidate();
                }
            }
        }
    };
    private Paint mPaint;

    public ChinaMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        initPaint();
        parserMap();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    /**
     * 解析地图
     */
    private void parserMap() {
        ParserThread.start();
    }

    Thread ParserThread = new Thread() {
        @Override
        public void run() {
            super.run();

            try {
                mProviceItemList.clear();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                InputStream is = getResources().openRawResource(R.raw.china);
                Document document = builder.parse(is);
                NodeList path = document.getElementsByTagName("path");//获取到path的集合
                for (int i = 0; i < path.getLength(); i++) {
                    Element item = (Element) path.item(i);
                    String attribute = item.getAttribute("android:pathData");
                    Path provicePath = PathParser.createPathFromPathData(attribute);
                    ProviceItem provice = new ProviceItem();
                    provice.setPath(provicePath);
                    mProviceItemList.add(provice);
                }
                mHandler.sendEmptyMessage(parseEnd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                if (widthSize < minWidth) {
                    widthSize = minWidth;
                }
                break;

            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                widthSize = minWidth;
                break;
        }

        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                if (heightSize < minHeight) {
                    heightSize = minHeight;
                }
                break;

            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                heightSize = minHeight;
                break;
        }

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.scale(scale, scale);
        if (mProviceItemList != null) {
            for (int i = 0; i < mProviceItemList.size(); i++) {
                ProviceItem proviceItem = mProviceItemList.get(i);
                if (selectedItem != null) {
                    if (selectedItem != proviceItem) {
                        proviceItem.onDraw(canvas, mPaint, false);
                    } else {
                        proviceItem.onDraw(canvas, mPaint, true);
                    }
                } else {
                    proviceItem.onDraw(canvas, mPaint, false);
                }

            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (mProviceItemList != null) {
                for (int i = 0; i < mProviceItemList.size(); i++) {
                    ProviceItem proviceItem = mProviceItemList.get(i);
                    boolean selected = proviceItem.isSelected(event.getX() / scale, event.getY() / scale);//因为之前画布放大了,但是path是没有放大的,所以这里要进行对应的缩小
                    if (BuildConfig.DEBUG) Log.e("ChinaMapView", "selected:" + selected);
                    if (selected) {
                        selectedItem = proviceItem;
                        break;
                    }
                }
            }
        }
        postInvalidate();
        return true;
    }

    public int dp2px(int dp) {
        float densityDpi = getResources().getDisplayMetrics().density;
        return (int) (dp * densityDpi + 0.5f);
    }
}
