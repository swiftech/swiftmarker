package com.github.swiftech.datamodel;

/**
 * @author swiftech 2018-11-26
 **/
public class Option {
    private String index;
    private String option;
    private String extra;

    public Option(String index, String option) {
        this.index = index;
        this.option = option;
    }

    public Option(String index, String option, String extra) {
        this.index = index;
        this.option = option;
        this.extra = extra;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
