/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import dao.UserDao;
import java.util.ArrayList;

/**
 *
 * @author moses
 */
public class Transaction {
    public String transactionId;
    public String employeeName;
    public String outletName;
    public String companyName;
    public boolean dineIn;
    public double recievedTotal;
    public double totalPrice;
    public String dateTime;
    public String type;
    public ArrayList<FoodItem> foodList;
    public boolean refunded;
    public String refundedBy = null;
    public String refundedDate = null;
    public String discountName;
    
    public Transaction(String employeeName, String dateTime, String type, double receivedTotal, boolean dineIn, String discountName){
        this.employeeName = employeeName;
        outletName = UserDao.getOutlet(employeeName);
        companyName = UserDao.getCompanyName(employeeName);
        this.dateTime = dateTime;
        foodList = new ArrayList<>();
        this.type = type;
        this.recievedTotal = receivedTotal;
        this.dineIn = dineIn;
        this.discountName = discountName;
    }
    
    public Transaction(String employeeName, String dateTime, double totalPrice){
        this.employeeName = employeeName;
        outletName = UserDao.getOutlet(employeeName);
        companyName = UserDao.getCompanyName(employeeName);
        this.dateTime = dateTime;
        this.totalPrice = totalPrice;
    }
    
    public Transaction(String transactionId, String employeeName, String dateTime, String type, double totalPrice, String refund){
        this.transactionId = transactionId;
        this.employeeName = employeeName;
        outletName = UserDao.getOutlet(employeeName);
        this.dateTime = dateTime;
        this.totalPrice = totalPrice;
        this.type = type;
        if(refund.equals("1")){
            this.refunded = true;
        }else{
            this.refunded = false;
        }
    }
    
    public void addFoodItem(FoodItem foodItem){
        foodList.add(foodItem);
    }
    
    public double getTotalPrice(){
        totalPrice = 0;
        for(FoodItem foodItem : foodList){
            totalPrice += foodItem.totalPrice;
        }
        return totalPrice;
    }
    
    
    @Override
    public String toString(){
        return employeeName + ", " + outletName + ", " + totalPrice + ", " + dateTime + ", " + foodList;
    }
}
