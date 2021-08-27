package com.github.swiftech.swiftmarker;

import java.util.List;
import java.util.Map;

/**
 * 循环矩阵
 *
 * @author swiftech 2018-12-07
 **/
public class LoopMatrix {

    private List<Map<String, Object>> matrix;

    public LoopMatrix(List<Map<String, Object>> matrix) {
        this.matrix = matrix;
    }

    public List<Map<String, Object>> getMatrix() {
        return matrix;
    }

    public void setMatrix(List<Map<String, Object>> matrix) {
        this.matrix = matrix;
    }
}
