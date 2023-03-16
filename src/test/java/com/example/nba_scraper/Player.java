package com.example.nba_scraper;

public class Player {
    private String FirstName;
    private String LastName;
    private String NbaDotComPlayerID;

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getNbaDotComPlayerID() {
        return NbaDotComPlayerID;
    }

    public void setNbaDotComPlayerID(String nbaDotComPlayerID) {
        NbaDotComPlayerID = nbaDotComPlayerID;
    }
}
