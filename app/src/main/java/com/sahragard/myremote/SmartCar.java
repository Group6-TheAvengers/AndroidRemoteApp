package com.sahragard.myremote;

public class SmartCar {

    private Bluetooth bt;
    private int currentSpeed;
    public static String currentDirection;
    private int distance;

    public SmartCar(final Bluetooth bt) {
        this.bt = bt;
        currentDirection = "isNotMoving";

    }


    public void moveForward(int speed) {
        if (currentDirection == "isNotMoving" || currentDirection == "isForward") {
            currentDirection = "isNotMoving";
            switch (speed) {
                case 1:
                    bt.send("a");
                    System.out.println("------Working-----");
                    break;
                case 2:
                    bt.send("b");
                    System.out.println("------Working-----");
                    break;
                case 3:
                    bt.send("c");
                    System.out.println("------Working-----");
                    break;
            }
        }
    }

    public void moveBackward(int speed) {
        if (currentDirection == "isNotMoving" || currentDirection == "isBackward") {
            currentDirection = "isNotMoving";
            switch (speed) {
                case 1:
                    bt.send("d");
                    break;
                case 2:
                    bt.send("e");
                    break;
                case 3:
                    bt.send("f");
                    break;
            }

        }
    }

    public void moveLeft() {
        if (currentDirection == "isNotMoving" || currentDirection == "isForward") {
            currentDirection = "isForward";

            bt.send("a");
            bt.send("l");
        }
        if (currentDirection == "isBackward") {
            currentDirection = "isBackward";

            bt.send("d");
            bt.send("l");
        }
    }

    public void moveRight() {
        if (currentDirection == "isForward") {
            currentDirection = "isForward";

            bt.send("a");
            bt.send("i");
        }
        if (currentDirection == "isBackward") {
            currentDirection = "isBackward";

            bt.send("d");
            bt.send("i");
        }
    }


    public void moveForwardLeft(int speed){
        if(currentDirection == "isForward") {
            currentDirection = "isForward";

            switch (speed) {
                case 1:
                    bt.send("a");
                    bt.send("j");
                    break;
                case 2:
                    bt.send("b");
                    bt.send("j");
                    break;
                case 3:
                    bt.send("b");
                    bt.send("j");
                    break;
            }
        }
    }

    public void moveForwardLeftLeft(int speed){
        if(currentDirection == "isNotMoving" || currentDirection == "isForward") {
            currentDirection = "isForward";

            switch (speed) {
                case 1:
                    bt.send("a");
                    bt.send("k");
                    break;
                case 2:
                    bt.send("b");
                    bt.send("k");
                    break;
                case 3:
                    bt.send("b");
                    bt.send("k");
                    break;
            }

        }
    }

    public void moveForwardRight(int speed){
        if(currentDirection == "isNotMoving" || currentDirection == "isForward") {
            currentDirection = "isNotMoving";

            switch (speed) {
                case 1:
                    bt.send("a");
                    bt.send("g");
                    break;
                case 2:
                    bt.send("b");
                    bt.send("g");
                    break;
                case 3:
                    bt.send("b");
                    bt.send("g");
                    break;
            }
        }
    }

    public void moveForwardRightRight(int speed){
        if(currentDirection == "isNotMoving" || currentDirection == "isForward") {
            currentDirection = "isForward";

            switch (speed) {
                case 1:
                    bt.send("a");
                    bt.send("h");
                    break;
                case 2:
                    bt.send("b");
                    bt.send("h");
                    break;
                case 3:
                    bt.send("b");
                    bt.send("h");
                    break;
            }
        }
    }

    public void moveBackwardLeft(int speed) {
        if(currentDirection == "isNotMoving" || currentDirection == "isBackward") {
            currentDirection = "isNotMoving";
            switch (speed) {
                case 1:
                    bt.send("d");
                    bt.send("j");
                    break;
                case 2:
                    bt.send("e");
                    bt.send("j");
                    break;
                case 3:
                    bt.send("e");
                    bt.send("j");
                    break;
            }
        }
    }

    public void moveBackwardLeftLeft(int speed) {
        if(currentDirection == "isNotMoving" || currentDirection == "isBackward") {
            currentDirection = "isBackward";
            switch (speed) {
                case 1:
                    bt.send("d");
                    bt.send("k");
                    break;
                case 2:
                    bt.send("e");
                    bt.send("k");
                    break;
                case 3:
                    bt.send("e");
                    bt.send("k");
                    break;
            }
        }
    }

    public void moveBackwardRight(int speed) {
        if(currentDirection == "isNotMoving" || currentDirection == "isBackward") {
            currentDirection = "isNotMoving";
            switch (speed) {
                case 1:
                    bt.send("d");
                    bt.send("g");
                    break;
                case 2:
                    bt.send("e");
                    bt.send("g");
                    break;
                case 3:
                    bt.send("e");
                    bt.send("g");
                    break;
            }
        }
    }

    public void moveBackwardRightRight(int speed) {
        if(currentDirection == "isNotMoving" || currentDirection == "isBackward") {
            currentDirection = "isBackward";
            switch (speed) {
                case 1:
                    bt.send("d");
                    bt.send("h");
                    break;
                case 2:
                    bt.send("e");
                    bt.send("h");
                    break;
                case 3:
                    bt.send("e");
                    bt.send("h");
                    break;
            }
        }
    }

    public void stop(){
        currentDirection = "isNotMoving";
        bt.send("m");

    }

}

