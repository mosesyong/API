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
    public String name;
    public int quantity;
    public double price;
    public double totalPrice;
    
    public AnalyticsEntity(){
    }
    
    @Override
    public String toString(){
        return name + ", " + quantity + ", " + price + ", " + totalPrice;
    }
}
