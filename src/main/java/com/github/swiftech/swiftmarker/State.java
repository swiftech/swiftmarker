package com.github.swiftech.swiftmarker;

/**
 * @author Allen 2018-12-14
 **/
public class State {

    private String value;

    private State(String value) {
        this.value = value;
    }

    public static State newLoop() {
        return new State("loop");
    }

    public static State newLogicTrue() {
        return new State("logic_true");
    }

    public static State newLogicFalse() {
        return new State("logic_false");
    }

    public boolean isLoop() {
        return "loop".equals(value);
    }

    public boolean isLogic() {
        return isLogicTrue() || isLogicFalse();
    }

    public boolean isLogicTrue() {
        return "logic_true".equals(value);
    }

    public boolean isLogicFalse() {
        return "logic_false".equals(value);
    }

}
