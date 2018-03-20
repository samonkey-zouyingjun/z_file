package pers.lic.tool.bean;

import java.util.Arrays;

public class GPSInfo {
    private String adcode;
    private String address;
    private String business;
    private String city;
    private int cityCode;
    private String country;
    private int countryCode;
    private String direction;
    private String distance;
    private String district;
    private double lat;
    private double lng;
    private String[] poiRegions;
    private String[] pois;
    private String province;
    private String sematicDescription;
    private String street;
    private String streetNumber;

    public double getLat() {
        return this.lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return this.lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBusiness() {
        return this.business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(int countryCode) {
        this.countryCode = countryCode;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return this.district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAdcode() {
        return this.adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return this.streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getDirection() {
        return this.direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDistance() {
        return this.distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String[] getPois() {
        return this.pois;
    }

    public void setPois(String[] pois) {
        this.pois = pois;
    }

    public String[] getPoiRegions() {
        return this.poiRegions;
    }

    public void setPoiRegions(String[] poiRegions) {
        this.poiRegions = poiRegions;
    }

    public String getSematicDescription() {
        return this.sematicDescription;
    }

    public void setSematicDescription(String sematicDescription) {
        this.sematicDescription = sematicDescription;
    }

    public int getCityCode() {
        return this.cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String toString() {
        return "GPSInfo{lat=" + this.lat + ", lng=" + this.lng + ", address='" + this.address + '\'' + ", business='" + this.business + '\'' + ", country='" + this.country + '\'' + ", countryCode=" + this.countryCode + ", province='" + this.province + '\'' + ", city='" + this.city + '\'' + ", district='" + this.district + '\'' + ", adcode='" + this.adcode + '\'' + ", street='" + this.street + '\'' + ", streetNumber='" + this.streetNumber + '\'' + ", direction='" + this.direction + '\'' + ", distance='" + this.distance + '\'' + ", pois=" + Arrays.toString(this.pois) + ", poiRegions=" + Arrays.toString(this.poiRegions) + ", sematicDescription='" + this.sematicDescription + '\'' + ", cityCode=" + this.cityCode + '}';
    }
}
