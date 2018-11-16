/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

/**
 *
 * @author moses
 */
public class Reservation {
    public String companyName;
    public String outletName;
    public String customerName;
    public String customerPhoneNumber;
    public String pax;
    public String dateTime;
    public String status = "0";
    
    public Reservation(String companyName, String outletName, String customerName, String customerPhoneNumber, String pax, String dateTime){
        this.companyName = companyName;
        this.outletName = outletName;
        this.customerName = customerName;
        this.customerPhoneNumber = customerPhoneNumber;
        this.pax = pax;
        this.dateTime = dateTime;
    }
    
    public Reservation(String companyName, String outletName, String customerName, String customerPhoneNumber, String pax, String dateTime, String status){
        this.companyName = companyName;
        this.outletName = outletName;
        this.customerName = customerName;
        this.customerPhoneNumber = customerPhoneNumber;
        this.pax = pax;
        this.dateTime = dateTime;
        this.status = status;
    }
    
    public Reservation(String companyName, String outletName, String customerName, String dateTime, String status){
        this.companyName = companyName;
        this.outletName = outletName;
        this.customerName = customerName;
        this.dateTime = dateTime;
        this.status = status;
    }
}
