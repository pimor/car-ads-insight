package model;

import java.io.Serializable;


/**
 * Created on 25/09/2017.
 */
public class Cars implements Serializable {

    private String country;
    private String uniqueId;
    private String urlAnonymized;
    private String make;
    private String model;
    private String year;
    private String mileage;
    private String price;
    private String doors;
    private String fuel;
    private String carType;
    private String transmission;
    private String color;
    private String region;
    private String city;
    private String date;
    private String titleChunk;
    private String contentChunk;


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUrlAnonymized() {
        return urlAnonymized;
    }

    public void setUrlAnonymized(String urlAnonymized) {
        this.urlAnonymized = urlAnonymized;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDoors() {
        return doors;
    }

    public void setDoors(String doors) {
        this.doors = doors;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitleChunk() {
        return titleChunk;
    }

    public void setTitleChunk(String titleChunk) {
        this.titleChunk = titleChunk;
    }

    public String getContentChunk() {
        return contentChunk;
    }

    public void setContentChunk(String contentChunk) {
        this.contentChunk = contentChunk;
    }

    @Override
    public String toString() {
        return "\"" +
               country + "\",\"" +
               uniqueId + "\",\"" +
               urlAnonymized + "\",\"" +
               make + "\",\"" +
               model + "\",\"" +
               year + "\",\"" +
               mileage + "\",\"" +
               price + "\",\"" +
               doors + "\",\"" +
               fuel + "\",\"" +
               carType + "\",\"" +
               transmission + "\",\"" +
               color + "\",\"" +
               region + "\",\"" +
               city + "\",\"" +
               date + "\",\"" +
               titleChunk + "\",\"" +
               contentChunk + "\"";
    }
}