package com.github.swiftech.swiftmarker.constant;

import java.util.Arrays;
import java.util.List;

/**
 * @author swiftech 2018-12-04
 **/
public interface Constants {
    String EXP_LOGIC_END = "?{}";
    String EXP_LOOP_END = "$[]";


    // States of template parser.
    String S_READY = "ready"; // 这个状态只是为了方便处理加的
    String S_PENDING_LOGIC = "pending_logic";
    String S_PENDING_OTHER = "pending_other";
    String S_IN_LOGIC = "in_logic";
    String S_IN_LOOP = "in_loop";
    String S_IN_VAR = "in_var";
    //    String S_IN_EXP = "in_exp";
    String S_IN_EXP_LOGIC = "in_exp_logic";
    String S_IN_EXP_LOOP = "in_exp_loop";
    String S_IN_EXP_VAR = "in_exp_var";
    String S_IN_STANZA = "in_stanza";
    String S_ESCAPING = "escaping";


    String LOGIC_EXP_EQ = "=";
    String LOGIC_EXP_NE = "!=";
    String LOGIC_EXP_GT = ">";
    String LOGIC_EXP_LT = "<";
    String LOGIC_EXP_GE = ">=";
    String LOGIC_EXP_LE = "<=";

    List<String> ALL_LOGIC_EXPS = Arrays.asList(LOGIC_EXP_NE, LOGIC_EXP_GE, LOGIC_EXP_LE, LOGIC_EXP_EQ, LOGIC_EXP_GT, LOGIC_EXP_LT);
}
