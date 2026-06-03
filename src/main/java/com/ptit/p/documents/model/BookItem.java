package com.ptit.p.documents.model;

public class BookItem {
    private int id;
    private String status;

    public BookItem() {
    }

    public BookItem(int id, String status) {
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BookItem{" +
                "id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
