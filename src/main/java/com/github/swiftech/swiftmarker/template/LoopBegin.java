package com.github.swiftech.swiftmarker.template;

/**
 * @author allen
 */
public class LoopBegin extends NestableDirective implements Begin {


    public LoopBegin(String value) {
        super(value);
    }

    public LoopBegin(String value, boolean isAvailable) {
        super(value);
        super.isAvailable = isAvailable;
    }

//    @Override
//    public String toString() {
//        return "LoopBegin{" +
//                "previous=" + (previous == null ? "null" : previous.getClass().getSimpleName()) +
//                ", next=" + (next == null ? "null" : next.getClass().getSimpleName()) +
//                ", value='" + value + '\'' +
//                ", isAvailable=" + isAvailable +
//                '}';
//    }
}
