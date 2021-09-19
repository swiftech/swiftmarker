package com.github.swiftech.swiftmarker;

import java.util.List;

/**
 * 循环模型
 *
 * @author swiftech 2018-12-07
 **/
public class LoopModel {

    private List<Object> matrix;

    public LoopModel(List<Object> matrix) {
        this.matrix = matrix;
    }

    public List<Object> getMatrix() {
        return matrix;
    }

    public void setMatrix(List<Object> matrix) {
        this.matrix = matrix;
    }
}
