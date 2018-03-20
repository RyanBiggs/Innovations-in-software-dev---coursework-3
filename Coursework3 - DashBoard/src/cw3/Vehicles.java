package cw3;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/////////////
//POJO
/////////////

//public class Vehicle{
//    private StringProperty Region = new SimpleStringProperty();
//    private StringProperty Vehicle = new SimpleStringProperty();
//    private IntegerProperty Quantity = new SimpleIntegerProperty();
//    private IntegerProperty Year = new SimpleIntegerProperty();
//    private ObjectProperty<Byte> Qtr = new SimpleObjectProperty<>();
//
//     public Vehicle(String region, String vehicle, Integer quantity, Integer year, byte qtr) {
//        this.Region.set(region);
//        this.Vehicle.set(vehicle);
//        this.Quantity.set(quantity);
//        this.Year.set(year);
//        this.Qtr.set(qtr);
//    }
////         @Override
////    public String toString() {
////        return String.format("%s%s%s", ("Region:" + Region + " "), ("Vehicle:" + Vehicle + " "), ("Quantity:" + Quantity + " "), ("Year:" + Year + " "), ("Qtr:" + Qtr + " "));
////    }
//
//    public void setRegion(String region) {
//        this.Region.set(region);
//    }    
//    
//    public String getRegion() {
//        return Region.get();
//    }
//    public StringProperty regionProperty() {
//        return Region;
//    }
//    
//    public void setVehicle(String vehicle) {
//        this.Vehicle.set(vehicle);
//    }    
//    
//    public String getVehicle() {
//        return Vehicle.get();
//    }
//    
//    public StringProperty vehicleProperty() {
//        return Vehicle;
//    }
//    
//    public void setQuantity(Integer quantity) {
//        this.Quantity.set(quantity);
//    }    
//    
//    public Integer getQuantity() {
//        return Quantity.get();
//    }
//    
//    public IntegerProperty quantityProperty() {
//        return Quantity;
//    }
//    
//    public void setYear(Integer year) {
//        this.Year.set(year);
//    }    
//    
//    public Integer getYear() {
//        return Year.get();
//    }
//    public String getYearString() {
//        return Year.asString().get();
//    }
//
//    public IntegerProperty yearProperty() {
//        return Year;
//    }
//    public void setQtr(Byte qtr) {
//        this.Qtr.set(qtr);
//    }    
//    
//    public Byte getQtr() {
//        return Qtr.get();
//    }
//    
//    public ObjectProperty<Byte> qtrProperty() {
//        return Qtr;
//    }
//    
//    

//}

public class Vehicles
{
    private final String Region, Vehicle;
    private final Integer Quantity, Year;
    private final byte QTR;
    
    public Vehicles (String region, String vehicle, Integer quantity, Integer year, byte qtr)
    {
        this.QTR = qtr;
        this.Quantity = quantity;
        this.Region = region;
        this.Vehicle = vehicle;
        this.Year = year;        
    }
    
    @Override
    public String toString()
    {
        return String.format("%s%s%s%s%s", 
                            ("Region:" + Region + " "), 
                            ("Vehicle:" + Vehicle + " "), 
                            ("Quantity:" + Quantity + " "), 
                            ("Year:" + Year + " "), 
                            ("Qtr:" + QTR + " "));
    }

    public String getRegion() 
    {
        return Region;
    }
    
    public String getVehicle()
    {
        return Vehicle;
    }
    
    public Integer getQuantity()
    {
        return Quantity;
    }
    
    public String getQuantityString()
    {
        return Quantity.toString();
    }
    
    public Integer getYear()
    {
        return Year;
    }
    
    public String getYearString()
    {
        return Year.toString();
    }

    public byte getQTR()
    {
        return QTR;
    }
    
    public String getQTRString()
    {
        return String.format("%s",(QTR));
    }
}
