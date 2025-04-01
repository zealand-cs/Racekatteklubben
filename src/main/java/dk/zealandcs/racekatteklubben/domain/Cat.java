package dk.zealandcs.racekatteklubben.domain;

import java.time.LocalDate;

public class Cat {

    private int id;
    private String name;
    private String race;
    private String gender;
    private LocalDate birthdate;
    private int points;
    private String image;


    public Cat(int id, String name, String race, String gender, LocalDate birthdate, int points, String image) {
        this.id = id;
        this.name = name;
        this.race = race;
        this.gender = gender;
        this.birthdate = birthdate;
        this.points = points;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
