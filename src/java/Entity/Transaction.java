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
    public double totalPrice;
    public String dateTime;
    public ArrayList<FoodItem> foodList;
    
    public Transaction(String employeeName, String dateTime){
        this.employeeName = employeeName;
        outletName = UserDao.getOutlet(employeeName);
        this.dateTime = dateTime;
        foodList = new ArrayList<>();
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
