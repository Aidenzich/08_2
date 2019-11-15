package tw.org.iii.a07;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.LinkedList;

public class MyView extends View {
    //private LinkedList<Point> line; 一條線
    private LinkedList<LinkedList<Point>> lines,recycler;  //多條線
    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.BLUE);

        //line = new LinkedList<>();
        lines = new LinkedList<>();
        recycler = new LinkedList<>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float ex = event.getX(), ey = event.getY();
        Point point = new Point(ex, ey);
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            recycler.clear();
            LinkedList<Point> line = new LinkedList<>();
            lines.add(line);
        }  //多條線時使用
        //line.add(point); 只會條線時用
        lines.getLast().add(point); //取得最後一條，
        //  Java => repaint
        invalidate();
        return true; //super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(10);

        for (LinkedList<Point> line : lines) {
            for (int i = 1; i < line.size(); i++) {
                Point p0 = line.get(i - 1);
                Point p1 = line.get(i);
                canvas.drawLine(p0.x, p0.y, p1.x, p1.y, paint);
            }
            //僅一條線時：
            //for(int i=1; i<line.size(); i++){
            //    Point p0 = line.get(i-1); Point p1 = line.get(i);
            //    canvas.drawLine(p0.x, p0.y, p1.x, p1.y, paint);}

        }
    }
    public void clear(){
        lines.clear();
        invalidate();//清掉
    }
    public void undo(){
        if (lines.size()>0) {
            lines.removeLast(); //移掉最後一條
          