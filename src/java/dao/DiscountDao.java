/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import Entity.Discount;
import java.util.ArrayList;

/**
 *
 * @author moses
 */
public class DiscountDao {
    public void addDiscount(Discount d){
        String name = d.name;
        String companyName = d.companyName;
        String outletName = d.outletName;
        double discountPercentage = d.discountPercentage;
        
        //db stuff
    }
    
    public ArrayList<Discount> getDiscounts(String companyName, String outletName){
        ArrayList<Discount> discountList = new ArrayList<>();
        
        //retrieve from db
        
        return discountList;
    }
}
