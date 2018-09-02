/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.util.ArrayList;

/**
 *
 * @author moses
 */
public class MenuItem {
    public String itemName;
    public double price;
    public double cost;
    public String imageName;
    public String desc;
    public ArrayList<String> categoryList;
    
    public MenuItem(String itemName, double price, double cost, String imageName, String desc){
        this.itemName = itemName;
        this.price = price;
        this.cost = cost;
        this.imageName = imageName;
        categoryList = new ArrayList<>();
        this.desc = desc;
    }
    
    @Override
    public String toString(){
        return (itemName + ", " + price + ", " + cost + ", " + imageName);
    }
    
    public void addCategory(String category){
        categoryList.add(category);
    }
    
    public void setCategory(ArrayList<String> categoryList){
        this.categoryList = categoryList;
    }
}
