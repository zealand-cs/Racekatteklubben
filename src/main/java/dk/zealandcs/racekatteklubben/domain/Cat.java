package dk.zealandcs.racekatteklubben.domain;

import java.sql.Date;

public class Cat {

    private int id;
    private int ownerId;
    private String name;
    private String race;
    private String gender;
    private Date dateOfBirth;
    private int points;
    private String imageUrl;

    public Cat() { }

    public Cat(int id, String name, String race, String gender, Date dateOfBirth, int points, String imageUrl) {
        this.id = id;
        this.name = name;
        this.race = race;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.points = points;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
