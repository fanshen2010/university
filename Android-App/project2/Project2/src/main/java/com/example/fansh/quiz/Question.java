package com.example.fansh.quiz;

/**
 * Created by fansh on 2016/10/12.
 */
public class Question {

    private Integer id;
    private String question;
    private String q1;
    private String q2;
    private String q3;
    private String q4;
    private String answer;
    private Integer time;

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() { return id; }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQ1() {
        return q1;
    }

    public String getQ2() {
        return q2;
    }

    public String getQ3() {
        return q3;
    }

    public String getQ4() {
        return q4;
    }

    public void setQ1(String q1) {
        this.q1 = q1;
    }

    public void setQ2(String q2) {
        this.q2 = q2;
    }

    public void setQ3(String q3) {
        this.q3 = q3;
    }

    public void setQ4(String q4) {
        this.q4 = q4;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
