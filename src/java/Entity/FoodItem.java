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
public class FoodItem {
    public String foodName;
    public int quantity;
    public double totalPrice;
    
    public FoodItem(String foodName, int quantity, double totalPrice){
        this.foodName = foodName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }
    
    @Override
    public String toString(){
        return (foodName + ", " + quantity + ", " + totalPrice);
    }
}
