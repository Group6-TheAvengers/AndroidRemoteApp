package com.sahragard.myremote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class JoyStickClass {

    //The Joystick Direction
    public static final int STICK_NONE = 0;
    public static final int STICK_UP = 1;
    public static final int STICK_UPRIGHT = 2;
    public static final int STICK_RIGHT = 3;
    public static final int STICK_DOWNRIGHT = 4;
    public static final int STICK_DOWN = 5;
    public static final int STICK_DOWNLEFT = 6;
    public static final int STICK_LEFT = 7;
    public static final int STICK_UPLEFT = 8;
    public static final int STICK_UPRIGHTRIGHT = 9;
    public static final int STICK_DOWNRIGHTRIGHT = 10;
    public static final int STICK_DOWNLEFTLEFT = 11;
    public static final int STICK_UPLEFTLEFT = 12;

    //Default Opacity Inner Joystick
    private int STICK_ALPHA = 200;
    //Default Opacity outer Joystick
    private int LAYOUT_ALPHA = 200;
    //Default Joystick Boundary
    private int OFFSET = 0;


    private Context mContext;
    //
    private ViewGroup mLayout;
    private LayoutParams params;
    private int stick_width, stick_height;

    private int position_x = 0, position_y = 0, min_distance = 0;
    private float distance = 0, angle = 0;

    private DrawCanvas draw;
    private Paint paint;
    private Bitmap stick;

    private boolean touch_state = false;

    private String where;
    private String currentM;

    public JoyStickClass (Context context, ViewGroup layout, int stick_res_id, final Bluetooth bt) {

        mContext = context;

        stick = BitmapFactory.decodeResource(mContext.getResources(),
                stick_res_id);

        stick_width = stick.getWidth();
        stick_height = stick.getHeight();

        draw = new DrawCanvas(mContext);
        paint = new Paint();
        mLayout = layout;
        params = mLayout.getLayoutParams();

        // Text to try out
        layout.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent arg1) {
                move(arg1, bt);

                return true;
            }
        });
    }
    public void drawStick(){
        draw.position(params.width / 2, params.height / 2);
        draw();
    }

    public void move(MotionEvent arg1, Bluetooth bt) {


        position_x = (int) (arg1.getX() - (params.width / 2));
        //if((arg1.getX() - (params.width / 2))>250){
          //  position_x = 250;
        //}
        //else if(-250>(arg1.getX() - (params.width / 2))){
          //  position_x = -250;
        //}

        position_y = (int) (arg1.getY() - (params.height / 2));
        //if((arg1.getY() - (params.height / 2))>250){
         //   position_y = 250;
        //}
        //else if(-250>(arg1.getY() - (params.height / 2))){
        //    position_y = -250;
        //}

        distance = (float) Math.sqrt(Math.pow(position_x, 2) + Math.pow(position_y, 2));
        angle = (float) cal_angle(position_x, position_y);

        if(arg1.getAction() == MotionEvent.ACTION_DOWN) {
            if(distance <= (params.width / 2) - OFFSET) {
                draw.position(arg1.getX(), arg1.getY());
                draw();
                touch_state = true;
            }
        } else if(arg1.getAction() == MotionEvent.ACTION_MOVE && touch_state) {
            if(distance <= (params.width / 2) - OFFSET) {
                draw.position(arg1.getX(), arg1.getY());
                draw();
            } else if(distance > (params.width / 2) - OFFSET){
                float x = (float) (Math.cos(Math.toRadians(cal_angle(position_x, position_y))) * ((params.width / 2) - OFFSET));
                float y = (float) (Math.sin(Math.toRadians(cal_angle(position_x, position_y))) * ((params.height / 2) - OFFSET));
                x += (params.width / 2);
                y += (params.height / 2);
                draw.position(x, y);
                draw();
            } else {
                mLayout.removeView(draw);
            }
        } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
            draw.position(params.width / 2, params.height / 2);
            touch_state = false;
        }

        //Motion Listioner
        if (arg1.getAction() == MotionEvent.ACTION_DOWN
                || arg1.getAction() == MotionEvent.ACTION_MOVE) {

            int direction = get12Direction();
            if (direction == JoyStickClass.STICK_UP) {
                if(distance<(250/3)){
                    if(currentM == "a"){}
                    else {
                        bt.send("a");
                        where = "a";
                        setCurrentM("a");
                    }
                }
                else if(distance<((250/3)*2)){
                    if(currentM == "b"){}
                    else {
                        bt.send("b");
                        where = "b";
                        setCurrentM("b");
                    }
                }
                else if(distance>=((250/3)*2)){
                    if(currentM == "c"){}
                    else {
                        bt.send("c");
                        where = "c";
                        setCurrentM("c");
                    }
                }
            } else if (direction == JoyStickClass.STICK_UPRIGHT) {
                if(distance<(250/3)){
                    if(currentM == "ag"){}
                    else {
                        bt.send("a");
                        bt.send("g");
                        setCurrentM("ag");
                        where = "ag";
                    }
                }
                else if(distance<((250/3)*2)){
                    if(currentM == "bg"){}
                    else {
                        bt.send("b");
                        bt.send("g");
                        setCurrentM("bg");
                        where = "bg";
                    }
                }
                else if(distance>=((250/3)*2)){
                    if(currentM == "cg"){}
                    else {
                        bt.send("c");
                        bt.send("g");
                        setCurrentM("cg");
                        where = "cg";
                    }
                }
            } else if (direction == JoyStickClass.STICK_UPRIGHTRIGHT) {
                if(distance<(250/3)){
                    if(currentM == "ah"){}
                    else {
                        bt.send("a");
                        bt.send("h");
                        setCurrentM("ah");
                        where = "ah";
                    }
                }
                else if(distance<((250/3)*2)){
                    if(currentM == "bh"){}
                    else {
                        bt.send("b");
                        bt.send("h");
                        setCurrentM("bh");
                        where = "bh";
                    }
                }
                else if(distance>=((250/3)*2)){
                    if(currentM == "ch"){}
                    else {
                        bt.send("c");
                        bt.send("h");
                        setCurrentM("ch");
                        where = "ch";
                    }
                }
            } else if (direction == JoyStickClass.STICK_RIGHT) {
                    if(currentM == "i"){}
                    else {
                        bt.send("i");
                        setCurrentM("i");
                        where = "i";
                    }
            } else if (direction == JoyStickClass.STICK_DOWNRIGHTRIGHT) {
                if(distance<(250/3)){
                    if(currentM == "dh"){}
                    else {
                        bt.send("d");
                        bt.send("h");
                        setCurrentM("dh");
                        where = "dh";
                    }
                }
                else if(distance<((250/3)*2)){
                    if(currentM == "eh"){}
                    else {
                        bt.send("e");
                        bt.send("h");
                        setCurrentM("eh");
                        where = "eh";
                    }
                }
                else if(distance>=((250/3)*2)){
                    if(currentM == "fh"){}
                    else {
                        bt.send("f");
                        bt.send("h");
                        setCurrentM("fh");
                        where = "fh";
                    }
                }
            } else if (direction == JoyStickClass.STICK_DOWNRIGHT) {
                if(distance<(250/3)){
                    if(currentM == "dg"){}
                    else {
                        bt.send("d");
                        bt.send("g");
                        setCurrentM("dg");
                        where = "dg";
                    }
                }
                else if(distance<((250/3)*2)){
                    if(currentM == "eg"){}
                    else {
                        bt.send("e");
                        bt.send("g");
                        setCurrentM("eg");
                        where = "eg";
                    }
                }
                else if(distance>=((250/3)*2)){
                    if(currentM == "fg"){}
                    else {
                        bt.send("f");
                        bt.send("g");
                        setCurrentM("fg");
                        where = "fg";
                    }
                }
            } else if (direction == JoyStickClass.STICK_DOWN) {
                if(distance<(250/3)){
                    if(currentM == "d"){}
                    else {
                        bt.send("d");
                        setCurrentM("d");
                        where = "d";
                    }
                }
                else if(distance<((250/3)*2)){
                    if(currentM == "e"){}
                    else {
                        bt.send("e");
                        setCurrentM("e");
                        where = "e";
                    }
                }
                else if(distance>=((250/3)*2)){
                    if(currentM == "f"){}
                    else {
                        bt.send("f");
                        setCurrentM("f");
                        where = "f";
                    }
                }
            } else if (direction == JoyStickClass.STICK_DOWNLEFT) {
                if(distance<(250/3)){
                    if(currentM == "dj"){}
                    else {
                        bt.send("d");
                        bt.send("j");
                        setCurrentM("dj");
                        where = "dj";
                    }
                }
                else if(distance<((250/3)*2)){
                    if(currentM == "ej"){}
                    else {
                        bt.send("e");
                        bt.send("j");
                        setCurrentM("ej");
                        where = "ej";
                    }
                }
                else if(distance>=((250/3)*2)){
                    if(currentM == "fj"){}
                    else {
                        bt.send("f");
                        bt.send("j");
                        setCurrentM("fj");
                        where = "fj";
                    }
                }
            } else if (direction == JoyStickClass.STICK_DOWNLEFTLEFT) {
                if(distance<(250/3)){
                    if(currentM == "dk"){}
                    else {
                        bt.send("d");
                        bt.send("k");
                        setCurrentM("dk");
                        where = "dk";
                    }
                }
                else if(distance<((250/3)*2)){
                    if(currentM == "ek"){}
                    else {
                        bt.send("e");
                        bt.send("k");
                        setCurrentM("ek");
                        where = "ek";
                    }
                }
                else if(distance>=((250/3)*2)){
                    if(currentM == "fk"){}
                    else {
                        bt.send("f");
                        setCurrentM("k");
                        setCurrentM("fk");
                        where = "fk";
                    }
                }
            } else if (direction == JoyStickClass.STICK_LEFT) {
                    if(currentM == "l"){}
                    else {
                        bt.send("l");
                        setCurrentM("l");
                        where = "l";
                    }
            } else if (direction == JoyStickClass.STICK_UPLEFTLEFT) {
                if(distance<(250/3)){
                    if(currentM == "ak"){}
                    else {
                        bt.send("a");
                        bt.send("k");
                        setCurrentM("ak");
                        where = "ak";
                    }
                }
                else if(distance<((250/3)*2)){
                    if(currentM == "bk"){}
                    else {
                        bt.send("b");
                        bt.send("k");
                        setCurrentM("bk");
                        where = "bk";
                    }
                }
                else if(distance>=((250/3)*2)){
                    if(currentM == "ck"){}
                    else {
                        bt.send("c");
                        bt.send("k");
                        setCurrentM("ck");
                        where = "ck";
                    }
                }
            } else if (direction == JoyStickClass.STICK_UPLEFT) {
                if(distance<(250/3)){
                    if(currentM == "aj"){}
                    else {
                        bt.send("a");
                        bt.send("j");
                        setCurrentM("aj");
                        where = "aj";
                    }
                }
                else if(distance<((250/3)*2)){
                    if(currentM == "bj"){}
                    else {
                        bt.send("b");
                        bt.send("j");
                        setCurrentM("bj");
                        where = "bj";
                    }
                }
                else if(distance>=((250/3)*2)){
                    if(currentM == "cj"){}
                    else {
                        bt.send("c");
                        bt.send("j");
                        setCurrentM("cj");
                        where = "cj";
                    }
                }
            } else if (direction == JoyStickClass.STICK_NONE) {
                bt.send("m");
                setCurrentM("m");
            }
        }
        else if(arg1.getAction() == MotionEvent.ACTION_UP) {
            where = "m";
            bt.send("m");
            setCurrentM("m");
        }
    }

    public void setCurrentM(String N) {
        currentM = N;
    }

    public int[] getPosition() {
        if(distance > min_distance && touch_state) {
            return new int[] { position_x, position_y };
        }
        return new int[] { 0, 0 };
    }

    public int getX() {
        if(distance > min_distance && touch_state) {
            return position_x;
        }
        return 0;
    }

    public int getY() {
        if(distance > min_distance && touch_state) {
            return position_y;
        }
        return 0;
    }

    public float getAngle() {
        if(distance > min_distance && touch_state) {
            return angle;
        }
        return 0;
    }

    public float getDistance() {
        if(distance > min_distance && touch_state) {
            return distance;
        }
        return 0;
    }

    public void setMinimumDistance(int minDistance) {
        min_distance = minDistance;
    }

    public int getMinimumDistance() {
        return min_distance;
    }

    public int get12Direction() {
        if(distance > min_distance && touch_state) {
            if(angle >= 255 && angle < 285 ) {
                return STICK_UP;
            } else if(angle >= 285 && angle < 315 ) {
                return STICK_UPRIGHT;
            } else if(angle >= 315 && angle < 345 ) {
                return STICK_UPRIGHTRIGHT;
            } else if(angle >= 345 || angle < 15 ) {
                return STICK_RIGHT;
            } else if(angle >= 15 && angle < 45 ) {
                return STICK_DOWNRIGHTRIGHT;
            } else if(angle >= 45 && angle < 75 ) {
                return STICK_DOWNRIGHT;
            } else if(angle >= 75 && angle < 105 ) {
                return STICK_DOWN;
            } else if(angle >= 105 && angle < 135 ) {
                return STICK_DOWNLEFT;
            } else if(angle >= 135 && angle < 165 ) {
                return STICK_DOWNLEFTLEFT;
            } else if(angle >= 165 && angle < 195 ) {
                return STICK_LEFT;
            } else if(angle >= 195 && angle < 225 ) {
                return STICK_UPLEFTLEFT;
            } else if(angle >= 225 && angle < 255 ) {
                return STICK_UPLEFT;
            }
        } else if(distance <= min_distance && touch_state) {
            return STICK_NONE;
        }
        return 0;
    }

    public int get8Direction() {
        if(distance > min_distance && touch_state) {
            if(angle >= 247.5 && angle < 292.5 ) {
                return STICK_UP;
            } else if(angle >= 292.5 && angle < 337.5 ) {
                return STICK_UPRIGHT;
            } else if(angle >= 337.5 || angle < 22.5 ) {
                return STICK_RIGHT;
            } else if(angle >= 22.5 && angle < 67.5 ) {
                return STICK_DOWNRIGHT;
            } else if(angle >= 67.5 && angle < 112.5 ) {
                return STICK_DOWN;
            } else if(angle >= 112.5 && angle < 157.5 ) {
                return STICK_DOWNLEFT;
            } else if(angle >= 157.5 && angle < 202.5 ) {
                return STICK_LEFT;
            } else if(angle >= 202.5 && angle < 247.5 ) {
                return STICK_UPLEFT;
            }
        } else if(distance <= min_distance && touch_state) {
            return STICK_NONE;
        }
        return 0;
    }

    public int get4Direction() {
        if(distance > min_distance && touch_state) {
            if(angle >= 225 && angle < 315 ) {
                return STICK_UP;
            } else if(angle >= 315 || angle < 45 ) {
                return STICK_RIGHT;
            } else if(angle >= 45 && angle < 135 ) {
                return STICK_DOWN;
            } else if(angle >= 135 && angle < 225 ) {
                return STICK_LEFT;
            }
        } else if(distance <= min_distance && touch_state) {
            return STICK_NONE;
        }
        return 0;
    }

    public void setOffset(int offset) {
        OFFSET = offset;
    }

    public int getOffset() {
        return OFFSET;
    }

    public void setStickAlpha(int alpha) {
        STICK_ALPHA = alpha;
        paint.setAlpha(alpha);
    }

    public int getStickAlpha() {
        return STICK_ALPHA;
    }

    public void setLayoutAlpha(int alpha) {
        LAYOUT_ALPHA = alpha;
        mLayout.getBackground().setAlpha(alpha);
    }

    public int getLayoutAlpha() {
        return LAYOUT_ALPHA;
    }

    public void setStickSize(int width, int height) {
        stick = Bitmap.createScaledBitmap(stick, width, height, false);
        stick_width = stick.getWidth();
        stick_height = stick.getHeight();
    }

    public void setStickWidth(int width) {
        stick = Bitmap.createScaledBitmap(stick, width, stick_height, false);
        stick_width = stick.getWidth();
    }

    public void setStickHeight(int height) {
        stick = Bitmap.createScaledBitmap(stick, stick_width, height, false);
        stick_height = stick.getHeight();
    }

    public int getStickWidth() {
        return stick_width;
    }

    public int getStickHeight() {
        return stick_height;
    }

    public void setLayoutSize(int width, int height) {
        params.width = width;
        params.height = height;
    }

    public int getLayoutWidth() {
        return params.width;
    }

    public int getLayoutHeight() {
        return params.height;
    }

    private double cal_angle(float x, float y) {
        if(x >= 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x));
        else if(x < 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if(x < 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if(x >= 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 360;
        return 0;
    }

    private void draw() {
        try {
            mLayout.removeView(draw);
        } catch (Exception e) { }
        mLayout.addView(draw);
    }

    private class DrawCanvas extends View{
        float x, y;

        private DrawCanvas(Context mContext) {
            super(mContext);
        }

        public void onDraw(Canvas canvas) {
            canvas.drawBitmap(stick, x, y, paint);
        }

        private void position(float pos_x, float pos_y) {
            x = pos_x - (stick_width / 2);
            y = pos_y - (stick_height / 2);
        }
    }
}
