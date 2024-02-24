package com.ia.model;

public class Subject {
    private String name;
    private String acronym;
    private int numberOfTopics;

    public Subject() {
    }

    public Subject(String name, String acronym, int numberOfTopics) {
        this.name = name;
        this.acronym = acronym;
        this.numberOfTopics = numberOfTopics;
    }

    public String getName() {
        return name;
    }

    public String getAcronym() {
        return acronym;
    }

    public int getNumberOfTopics() {
        return numberOfTopics;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public void setNumberOfTopics(int numberOfTopics) {
        this.numberOfTopics = numberOfTopics;
    }
}
