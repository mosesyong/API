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
public class MenuItem {
    public String itemName;
    public double price;
    public double cost;
    public String imageName;
    
    public MenuItem(String itemName, double price, double cost, String imageName){
        this.itemName = itemName;
        this.price = price;
        this.cost = cost;
        this.imageName = imageName;
    }
    
    @Override
    public String toString(){
        return (itemName + ", " + price + ", " + cost + ", " + imageName);
    }
}
