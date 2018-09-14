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
public class Category {
    public String companyName;
    public String outletName;
    public String categoryName;
    public String image;
    
    public Category(String companyName, String outletName, String categoryName, String image){
        this.companyName = companyName;
        this.outletName = outletName;
        this.categoryName = categoryName;
        this.image = image;
    }
}
