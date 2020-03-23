package com.e.evoting;


import java.security.MessageDigest;
import java.util.Date;


public class Block {

    public String hash;
    public String previousHash;
    public String data; //our data will be a simple message.
    public String timeStamp; //as number of milliseconds since 1/1/1970.
    public Vote vote;

    public Block() {
    }

    //Block Constructor.
    public Block(Vote vote, String previousHash) {
        this.vote = vote;
        this.previousHash = previousHash;
        this.timeStamp = String.valueOf(new Date().getTime());
        this.hash = calculateHash(); //Making sure we do this after we set the other values.
    }


    public String calculateHash() {
        String calculatedhash = StringUtil.applySha256(
                this.previousHash +
                        this.timeStamp +
                        this.vote.getPartyName()
        );
        return calculatedhash;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return this.previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Vote getVote() {
        return this.vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }
}


class StringUtil {
    //Applies Sha256 to a string and returns the result.
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //Applies sha256 to our input,
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
