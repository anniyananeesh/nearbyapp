package models;

public class Config {

    //private variables
    int _id;
    String _distance_unit;
    String _temperature_unit;
    String _enable_notifications;
    String _last_offerID;

    // Empty constructor
    public Config(){

    }
    // constructor
    public Config(int id, String _distance_unit, String _temperature_unit, String _enable_notifications, String _last_offerID){
        this._id = id;
        this._distance_unit = _distance_unit;
        this._temperature_unit = _temperature_unit;
        this._enable_notifications = _enable_notifications;
        this._last_offerID = _last_offerID;
    }

    // constructor
    public Config(String _distance_unit, String _temperature_unit, String _enable_notifications, String _last_offerID){
        this._distance_unit = _distance_unit;
        this._temperature_unit = _temperature_unit;
        this._enable_notifications = _enable_notifications;
        this._last_offerID = _last_offerID;
    }

    public String get_enable_notifications() {
        return _enable_notifications;
    }

    public void set_enable_notifications(String _enable_notifications) {
        this._enable_notifications = _enable_notifications;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_distance_unit() {
        return _distance_unit;
    }

    public void set_distance_unit(String _distance_unit) {
        this._distance_unit = _distance_unit;
    }

    public String get_temperature_unit() {
        return _temperature_unit;
    }

    public void set_temperature_unit(String _temperature_unit) {
        this._temperature_unit = _temperature_unit;
    }

    public String get_last_offerID() {
        return _last_offerID;
    }

    public void set_last_offerID(String _last_offerID) {
        this._last_offerID = _last_offerID;
    }

    @Override
    public String toString() {
        return _distance_unit + " == " + _temperature_unit + "==" + _enable_notifications;
    }
}