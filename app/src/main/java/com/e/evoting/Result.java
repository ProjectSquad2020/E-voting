package com.e.evoting;

public class Result {

    String partyName;
    int numberOfVotes;

    public Result() {
    }

    public Result(String partyName, int numberOfVotes) {
        this.partyName = partyName;
        this.numberOfVotes = numberOfVotes;
    }

    public String getPartyName() {
        return this.partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public int getNumberOfVotes() {
        return this.numberOfVotes;
    }

    public void setNumberOfVotes(int numberOfVotes) {
        this.numberOfVotes = numberOfVotes;
    }
}
