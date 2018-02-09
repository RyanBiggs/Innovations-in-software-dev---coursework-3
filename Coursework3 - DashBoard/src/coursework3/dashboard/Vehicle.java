/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coursework3.dashboard;

/**
 *
 * @author Ryan
 */


/////////////
//POJO
/////////////

public class Vehicle {
    final private String region, vehicle;
    final private int quantity, year;
    final private byte qtr;
    
    public Vehicle (String region, String vehicle, int quantity, int year, byte qtr){
        this.qtr = qtr;
        this.quantity = quantity;
        this.region = region;
        this.vehicle = vehicle;
        this.year = year;
    }
    
    public String getRegion() {
        return region;
    }
    
    public String getVehicle() {
        return vehicle;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public int getYear() {
        return year;
    }
    public byte getQTR() {
        return qtr;
    }
}
