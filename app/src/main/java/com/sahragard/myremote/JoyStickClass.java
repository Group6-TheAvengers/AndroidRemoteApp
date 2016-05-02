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
    private int joyLevel; // the distance level of the joystick e.g. "1" is one level above the centre of the joystick, etc.
    private int prevJoyLevel;
    private int prevJoyDirection;
    //Default Opacity Inner Joystick
    private int STICK_ALPHA = 200;
    //Default Opacity outer Joystick
    private int LAYOUT_ALPHA = 200;
    //Default Joystick Boundary
    private int OFFSET = 0;


    private Context mContext;
    private ViewGroup mLayout;
    private LayoutParams params;
    private int stick_width, stick_height;

    private int position_x = 0, position_y = 0, min_distance = 0;
    private float distance = 0, angle = 0;

    private DrawCanvas draw;
    private Paint paint;
    private Bitmap stick;
    SmartCar car;
    private boolean touch_state = false;


    public JoyStickClass(Context context, ViewGroup layout, int stick_res_id, final Bluetooth bt, final TextView textView) {

        mContext = context;
        car = new SmartCar(bt, textView);
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

    public void drawStick() {
        draw.position(params.width / 2, params.height / 2);
        draw();
    }

    public void move(MotionEvent arg1, Bluetooth bt) {


        position_x = (int) (arg1.getX() - (params.width / 2));

        position_y = (int) (arg1.getY() - (params.height / 2));

        distance = (float) Math.sqrt(Math.pow(position_x, 2) + Math.pow(position_y, 2));

        angle = (float) cal_angle(position_x, position_y);

        if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
            if (distance <= (params.width / 2) - OFFSET) {
                draw.position(arg1.getX(), arg1.getY());
                draw();
                touch_state = true;
            }
        } else if (arg1.getAction() == MotionEvent.ACTION_MOVE && touch_state) {
            if (distance <= (params.width / 2) - OFFSET) {
                draw.position(arg1.getX(), arg1.getY());
                draw();
            } else if (distance > (params.width / 2) - OFFSET) {
                float x = (float) (Math.cos(Math.toRadians(cal_angle(position_x, position_y))) * ((params.width / 2) - OFFSET));
                float y = (float) (Math.sin(Math.toRadians(cal_angle(position_x, position_y))) * ((params.height / 2) - OFFSET));
                x += (params.width / 2);
                y += (params.height / 2);
                draw.position(x, y);
                draw();
            } else {
                mLayout.removeView(draw);
            }
        } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
            draw.position(params.width / 2, params.height / 2);
            touch_state = false;
        }

        //Motion Listioner
        if (arg1.getAction() == MotionEvent.ACTION_DOWN
                || arg1.getAction() == MotionEvent.ACTION_MOVE) {
            if (distance < 250 / 3) {
                joyLevel = 1;
            } else if (distance < (250 / 3) * 2) {
                joyLevel = 2;
            } else if (distance > (250 / 3) * 2) {
                joyLevel = 3;
            }
            int direction = get12Direction();
            if (direction == JoyStickClass.STICK_UP) {
                STICK_UP(direction);
            } else if (direction == JoyStickClass.STICK_UPRIGHT) {
                STICK_UPRIGHT(direction);
            } else if (direction == JoyStickClass.STICK_UPRIGHTRIGHT) {
                STICK_UPRIGHTRIGHT(direction);
            } else if (direction == JoyStickClass.STICK_RIGHT) {
                STICK_RIGHT(direction);
            } else if (direction == JoyStickClass.STICK_DOWNRIGHTRIGHT) {
                STICK_DOWNRIGHTRIGHT(direction);
            } else if (direction == JoyStickClass.STICK_DOWNRIGHT) {
                STICK_DOWNRIGHT(direction);
            } else if (direction == JoyStickClass.STICK_DOWN) {
                STICK_DOWN(direction);
            } else if (direction == JoyStickClass.STICK_DOWNLEFT) {
                STICK_DOWNLEFT(direction);
            } else if (direction == JoyStickClass.STICK_DOWNLEFTLEFT) {
                STICK_DOWNLEFTLEFT(direction);
            } else if (direction == JoyStickClass.STICK_LEFT) {
                STICK_LEFT(direction);
            } else if (direction == JoyStickClass.STICK_UPLEFTLEFT) {
                STICK_UPLEFTLEFT(direction);
            } else if (direction == JoyStickClass.STICK_UPLEFT) {
                STICK_UPLEFT(direction);
            } else if (direction == JoyStickClass.STICK_NONE) {
                STICK_NONE(direction);
            }
        } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
            STICK_NONE(STICK_NONE);
        }
    }

    public int[] getPosition() {
        if (distance > min_distance && touch_state) {
            return new int[]{position_x, position_y};
        }
        return new int[]{0, 0};
    }

    public int getX() {
        if (distance > min_distance && touch_state) {
            return position_x;
        }
        return 0;
    }

    public int getY() {
        if (distance > min_distance && touch_state) {
            return position_y;
        }
        return 0;
    }

    public float getAngle() {
        if (distance > min_distance && touch_state) {
            return angle;
        }
        return 0;
    }

    public float getDistance() {
        if (distance > min_distance && touch_state) {
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
        if (distance > min_distance && touch_state) {
            if (angle >= 255 && angle < 285) {
                return STICK_UP;
            } else if (angle >= 285 && angle < 315) {
                return STICK_UPRIGHT;
            } else if (angle >= 315 && angle < 345) {
                return STICK_UPRIGHTRIGHT;
            } else if (angle >= 345 || angle < 15) {
                return STICK_RIGHT;
            } else if (angle >= 15 && angle < 45) {
                return STICK_DOWNRIGHTRIGHT;
            } else if (angle >= 45 && angle < 75) {
                return STICK_DOWNRIGHT;
            } else if (angle >= 75 && angle < 105) {
                return STICK_DOWN;
            } else if (angle >= 105 && angle < 135) {
                return STICK_DOWNLEFT;
            } else if (angle >= 135 && angle < 165) {
                return STICK_DOWNLEFTLEFT;
            } else if (angle >= 165 && angle < 195) {
                return STICK_LEFT;
            } else if (angle >= 195 && angle < 225) {
                return STICK_UPLEFTLEFT;
            } else if (angle >= 225 && angle < 255) {
                return STICK_UPLEFT;
            }
        } else if (distance <= min_distance && touch_state) {
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

    public void STICK_NONE(int direction) {
        if (prevJoyDirection != direction) {
            car.stop();
            prevJoyDirection = direction;
            prevJoyLevel = 0;
        }
    }

    public void STICK_UP(int direction) {
        switch (joyLevel) {
            case 1:
                if (prevJoyDirection != direction) {
                    car.moveForward(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 2:
                if (prevJoyDirection != direction) {
                    car.moveForward(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 3:
                if (prevJoyDirection != direction) {
                    car.moveForward(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
        }
    }


    public void STICK_UPRIGHT(int direction) {
        switch (joyLevel) {
            case 1:
                if (prevJoyDirection != direction) {
                    car.moveForwardRight(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 2:
                if (prevJoyDirection != direction) {
                    car.moveForwardRight(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 3:
                if (prevJoyDirection != direction) {
                    car.moveForwardRight(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
        }


    }

    public void STICK_RIGHT(int direction) {
        if (prevJoyDirection != direction) {
            car.moveRight();
            prevJoyDirection = direction;
        }
    }

    public void STICK_DOWNRIGHT(int direction) {

        switch (joyLevel) {
            case 1:
                if (prevJoyDirection != direction) {
                    car.moveBackwardRight(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 2:
                if (prevJoyDirection != direction) {
                    car.moveBackwardRight(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 3:
                if (prevJoyDirection != direction) {
                    car.moveBackwardRight(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
        }
    }


    public void STICK_DOWN(int direction) {

        switch (joyLevel) {
            case 1:
                if (prevJoyDirection != direction) {
                    car.moveBackward(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 2:
                if (prevJoyDirection != direction) {
                    car.moveBackward(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 3:
                if (prevJoyDirection != direction) {
                    car.moveBackward(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
        }
    }


    public void STICK_DOWNLEFT(int direction) {

        switch (joyLevel) {
            case 1:
                if (prevJoyDirection != direction) {
                    car.moveBackwardLeft(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 2:
                if (prevJoyDirection != direction) {
                    car.moveBackwardLeft(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 3:
                if (prevJoyDirection != direction) {
                    car.moveBackwardLeft(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
        }
    }


    public void STICK_LEFT(int direction) {
        if (prevJoyDirection != direction) {
            car.moveLeft();
            prevJoyDirection = direction;
        }
    }
    public void  STICK_UPLEFT(int direction) {
        switch (joyLevel) {
            case 1:
                if (prevJoyDirection != direction) {
                    car.moveForwardLeft(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 2:
                if (prevJoyDirection != direction) {
                    car.moveForwardLeft(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 3:
                if (prevJoyDirection != direction) {
                    car.moveForwardLeft(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
        }
    }

    public void  STICK_UPRIGHTRIGHT(int direction) {
        switch (joyLevel) {
            case 1:
                if (prevJoyDirection != direction) {
                    car.moveForwardRightRight(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 2:
                if (prevJoyDirection != direction) {
                    car.moveForwardRightRight(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 3:
                if (prevJoyDirection != direction) {
                    car.moveForwardRightRight(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
        }
    }

    public void  STICK_DOWNRIGHTRIGHT(int direction) {
        switch (joyLevel) {
            case 1:
                if (prevJoyDirection != direction) {
                    car.moveBackwardRightRight(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 2:
                if (prevJoyDirection != direction) {
                    car.moveBackwardRightRight(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 3:
                if (prevJoyDirection != direction) {
                    car.moveBackwardRightRight(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
        }
    }

    public void  STICK_DOWNLEFTLEFT(int direction) {
        switch (joyLevel) {
            case 1:
                if (prevJoyDirection != direction) {
                    car.moveBackwardLeftLeft(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 2:
                if (prevJoyDirection != direction) {
                    car.moveBackwardLeftLeft(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 3:
                if (prevJoyDirection != direction) {
                    car.moveBackwardLeftLeft(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
        }
    }

    public void  STICK_UPLEFTLEFT(int direction) {
        switch (joyLevel) {
            case 1:
                if (prevJoyDirection != direction){
                    car.moveForwardLeftLeft(joyLevel);
                    prevJoyDirection=direction;
                }
                break;
            case 2:
                if (prevJoyDirection != direction) {
                    car.moveForwardLeftLeft(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
            case 3:
                if (prevJoyDirection != direction) {
                    car.moveForwardLeftLeft(joyLevel);
                    prevJoyDirection = direction;
                }
                break;
        }
    }

    private double cal_angle(float x, float y) {
        if (x >= 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x));
        else if (x < 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if (x < 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if (x >= 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 360;
        return 0;
    }

    private void draw() {
        try {
            mLayout.removeView(draw);
        } catch (Exception e) {
        }
        mLayout.addView(draw);
    }


    private class DrawCanvas extends View {
        float x, y;

        private DrawCanvas(Context mContext) {
            super(mContext);
        }

        public void onDraw(Canvas canvas) {
            canvas.drawBitmap(stick, x, y, paint);
        }

        public void position(float pos_x, float pos_y) {
            x = pos_x - (stick_width / 2);
            y = pos_y - (stick_height / 2);
        }
    }
}
