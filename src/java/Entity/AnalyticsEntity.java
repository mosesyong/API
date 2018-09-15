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
public class AnalyticsEntity {
    public String paymentType;
    public String dateTime;
    public String foodName;
    public int quantity;
    public double totalPrice;
    public String TID;
    public String cashierName;
    
    public AnalyticsEntity(String paymentType, String dateTime, String foodName, int quantity, double totalPrice, String TID, String cashierName){
        this.paymentType = paymentType;
        this.dateTime = dateTime;
        this.foodName = foodName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.TID = TID;
        this.cashierName = cashierName;
    }
    
    @Override
    public String toString(){
        return "foodName: " + foodName + ", paymentType: " + paymentType + ", dateTime: " + dateTime + ", quantity: " + quantity + ", total price: " + totalPrice;
    }
}
