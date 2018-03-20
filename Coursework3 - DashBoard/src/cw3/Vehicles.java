package cw3;

//*****************************************
//  Vehicles Class - POJO for vehicle data
//*****************************************
public class Vehicles
{
    private final String Region, Vehicle;
    private final Integer Quantity, Year;
    private final byte QTR;
    
    //**************
    //  Constructor
    //**************
    public Vehicles (String region, String vehicle, Integer quantity, Integer year, byte qtr)
    {
        this.QTR = qtr;
        this.Quantity = quantity;
        this.Region = region;
        this.Vehicle = vehicle;
        this.Year = year;        
    }
    
    //**********************************************************
    //  toString - returns the sales data as a formatted string
    //**********************************************************
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
    
    //*********************************************
    //  getRegion - returns the region as a string
    //*********************************************
    public String getRegion() 
    {
        return Region;
    }
    
    //***********************************************
    //  getVehicle - returns the vehicle as a string
    //***********************************************
    public String getVehicle()
    {
        return Vehicle;
    }
    
    //***************************************************
    //  getQuantity - returns the quantity as an integer
    //***************************************************
    public Integer getQuantity()
    {
        return Quantity;
    }
    
    //*******************************************************
    //  getQuantityString - returns the quantity as a string
    //*******************************************************
    public String getQuantityString()
    {
        return Quantity.toString();
    }
    
    //*******************************************
    //  getYear - returns the year as an integer
    //*******************************************
    public Integer getYear()
    {
        return Year;
    }
    
    //************************************************
    //  getYearString - returns the year as an string
    //************************************************
    public String getYearString()
    {
        return Year.toString();
    }

    //*****************************************
    //  getQTR - returns the quarter as a byte
    //*****************************************
    public byte getQTR()
    {
        return QTR;
    }
    
    //*************************************************
    //  getQTRString - returns the quarter as a string
    //*************************************************
    public String getQTRString()
    {
        return String.format("%s",(QTR));
    }
}
