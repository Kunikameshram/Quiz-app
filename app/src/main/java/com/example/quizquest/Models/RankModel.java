package com.example.quizquest.Models;

public class RankModel {

    private String uName;
    private int rank;
    private int score;
    private String photoUrl;
    private int bookmarksCount;

    public RankModel()
    {

    }

    public RankModel(String uName, int rank, int score, String photoUrl) {
        this.uName = uName;
        this.rank = rank;
        this.score = score;
        this.photoUrl = photoUrl;
    }

    public int getBookmarksCount() {
        return bookmarksCount;
    }

    public void setBookmarksCount(int bookmarksCount) {
        this.bookmarksCount = bookmarksCount;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
