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
public class Discount {
    public String name;
    public String companyName;
    public String outletName;
    public double discountPercentage;
    
    public Discount(String name, String companyName, String outletName, double discountPercentage){
        this.name = name;
        this.discountPercentage = discountPercentage;
        this.companyName = companyName;
        this.outletName = outletName;
    }
}
