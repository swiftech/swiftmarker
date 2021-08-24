package com.github.swiftech.swiftmarker.template;

/**
 * @author swiftech
 * @see MarkerBuilder
 * @since 3.0
 */
public class Marker extends Directive {

    private boolean isLogicBegin = false;
    private boolean isLogicEnd = false;
    private boolean isLoopBegin = false;
    private boolean isLoopEnd = false;
    private boolean isVar = false;
    private boolean isAvailable = true;

    protected Marker(String value, boolean isLogicBegin, boolean isLogicEnd, boolean isLoopBegin, boolean isLoopEnd, boolean isVar, boolean isAvailable) {
        super(value);
        this.isLogicBegin = isLogicBegin;
        this.isLogicEnd = isLogicEnd;
        this.isLoopBegin = isLoopBegin;
        this.isLoopEnd = isLoopEnd;
        this.isVar = isVar;
        this.isAvailable = isAvailable;
    }
//
//    public static boolean isLogicMarker(Directive directive) {
//        if (directive instanceof Marker) {
//            return ((Marker) directive).isLogicMarker();
//        }
//        return false;
//    }
//
//    public static boolean isLoopMarker(Directive directive) {
//        if (directive instanceof Marker) {
//            return ((Marker) directive).isLoopMarker();
//        }
//        return false;
//    }

//    public static boolean isLoopMarkerBegin(Directive directive) {
//        if (directive instanceof Marker) {
//            return ((Marker) directive).isLoopBegin();
//        }
//        return false;
//    }
//
//    public static boolean isLoopMarkerEnd(Directive directive) {
//        if (directive instanceof Marker) {
//            return ((Marker) directive).isLoopEnd();
//        }
//        return false;
//    }
//
//    public static boolean isNestableMarker(Directive directive){
//        return isLogicMarker(directive) || isLoopMarker(directive);
//    }
//
//    public boolean isLogicMarker() {
//        return isLogicBegin || isLogicEnd;
//    }
//
//    public boolean isLoopMarker() {
//        return isLoopBegin || isLoopEnd;
//    }
//
//    public boolean isNestableMarker() {
//        return isLogicMarker() || isLoopMarker();
//    }

//    public String getValue() {
//        return value;
//    }


    public boolean isLogicBegin() {
        return isLogicBegin;
    }

    public void setLogicBegin(boolean logicBegin) {
        isLogicBegin = logicBegin;
    }

    public boolean isLogicEnd() {
        return isLogicEnd;
    }

    public void setLogicEnd(boolean logicEnd) {
        isLogicEnd = logicEnd;
    }

    public boolean isLoopBegin() {
        return isLoopBegin;
    }

    public void setLoopBegin(boolean loopBegin) {
        isLoopBegin = loopBegin;
    }

    public boolean isLoopEnd() {
        return isLoopEnd;
    }

    public void setLoopEnd(boolean loopEnd) {
        isLoopEnd = loopEnd;
    }

    public boolean isVar() {
        return isVar;
    }

    public void setVar(boolean var) {
        isVar = var;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }


    @Override
    public String toString() {
        return "Marker{" +
                "value='" + value + '\'' +
                ", isLogicBegin=" + isLogicBegin +
                ", isLogicEnd=" + isLogicEnd +
                ", isLoopBegin=" + isLoopBegin +
                ", isLoopEnd=" + isLoopEnd +
                ", isVar=" + isVar +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
