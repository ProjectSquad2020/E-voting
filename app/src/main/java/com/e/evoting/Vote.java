package com.e.evoting;

public class Vote {

    String userId;
    String partyName;



    public Vote(String userId, String partyName) {
        this.userId = userId;
        this.partyName = partyName;
    }

    Vote(){

    }

    public Vote(String partyName) {
        this.partyName = partyName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }
}
