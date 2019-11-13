package com.example.fansh.quiz;

/**
 * Created by fansh on 2016/10/12.
 */
public class User {

    private Integer id;
    private String name;
    private String password;
    private Integer totalCorrect;
    private Integer totalAnswered;

    public Integer getTotalCorrect() {
        return totalCorrect;
    }

    public Integer getTotalAnswered() {
        return totalAnswered;
    }

    public void setTotalCorrect(Integer totalCorrect) {
        this.totalCorrect = totalCorrect;
    }

    public void setTotalAnswered(Integer totalAnswered) {
        this.totalAnswered = totalAnswered;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

}
