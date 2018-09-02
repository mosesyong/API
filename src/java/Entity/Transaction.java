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
    public String employeeName;
    public String outletName;
    public String companyName;
    public double totalPrice;
    public String dateTime;
    public String type;
    public ArrayList<FoodItem> foodList;
    
    public Transaction(String employeeName, String dateTime, String type){
        this.employeeName = employeeName;
        outletName = UserDao.getOutlet(employeeName);
        companyName = UserDao.getCompanyName(employeeName);
        this.dateTime = dateTime;
        foodList = new ArrayList<>();
        this.type = type;
    }
    
    public Transaction(String employeeName, String dateTime, double totalPrice){
        this.employeeName = employeeName;
        outletName = UserDao.getOutlet(employeeName);
        companyName = UserDao.getCompanyName(employeeName);
        this.dateTime = dateTime;
        this.totalPrice = totalPrice;
    }
    
    public Transaction(String employeeName, String dateTime, String type, double totalPrice){
        this.employeeName = employeeName;
        outletName = UserDao.getOutlet(employeeName);
        this.dateTime = dateTime;
        this.totalPrice = totalPrice;
        this.type = type;
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
