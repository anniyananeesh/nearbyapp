package models;

public class Favourite {

    //private variables
    int _id;
    String _name;
    String _phone_number;
    String _latitude;
    String _longitude;
    String _address;
    String _placeId;

    // Empty constructor
    public Favourite(){

    }
    // constructor
    public Favourite(int id, String name, String _phone_number, String _latitude, String _longitude, String _address, String _placeId){
        this._id = id;
        this._name = name;
        this._phone_number = _phone_number;
        this._latitude = _latitude;
        this._longitude = _longitude;
        this._address = _address;
        this._placeId = _placeId;
    }

    // constructor
    public Favourite(String name, String _phone_number, String _latitude, String _longitude, String _address , String _placeId){
        this._name = name;
        this._phone_number = _phone_number;
        this._latitude = _latitude;
        this._longitude = _longitude;
        this._address = _address;
        this._placeId = _placeId;
    }

    public String get_placeId() {
        return _placeId;
    }

    public void set_placeId(String _placeId) {
        this._placeId = _placeId;
    }

    public String get_address() {
        return _address;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_phone_number() {
        return _phone_number;
    }

    public void set_phone_number(String _phone_number) {
        this._phone_number = _phone_number;
    }

    public String get_latitude() {
        return _latitude;
    }

    public void set_latitude(String _latitude) {
        this._latitude = _latitude;
    }

    public String get_longitude() {
        return _longitude;
    }

    public void set_longitude(String _longitude) {
        this._longitude = _longitude;
    }

}