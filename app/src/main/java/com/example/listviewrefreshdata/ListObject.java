package com.example.listviewrefreshdata;

/**
 * 数据源对象
 */
public class ListObject {
    private int resId;
    private String text;

    public ListObject(int resId, String text) {
        super();
        this.resId = resId;
        this.text = text;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "ListObject [resId=" + resId + ", text=" + text + "]";
    }

}
