package com.example.fansh.quiz;

/**
 * Created by fansh on 2016/10/16.
 */
public class UserQuestion {
    private Integer id;
    private Integer userId;
    private Integer questionId;

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

}
