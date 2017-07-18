package com.example.juan.newsapp;

class News {

    private final String title;
    private final String author;
    private final String section;
    private final String nURL;
    private String publishedDate;
    private String informationText;

    public News(String section, String publishedDate, String title, String informationText, String author, String nURL) {

        this.title = title;
        this.author = author;
        this.section = section;
        this.publishedDate = publishedDate;
        this.informationText = informationText;
        this.nURL = nURL;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getSection() {
        return section;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getInformationText() {
        return informationText;
    }

    public void setInformationText(String informationText) {
        this.informationText = informationText;
    }

    public String getnURL() {
        return nURL;
    }
}
