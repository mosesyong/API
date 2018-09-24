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
public class CollatedTransaction {
    public String collatedTransactionName;
    public double amount; //amount of sales
    public int count; //number of sales
    
    public CollatedTransaction(String collatedTransactionName, double amount, int count){
        this.collatedTransactionName = collatedTransactionName;
        this.amount = amount;
        this.count = count;
    }
    
    @Override
    public String toString(){
        return("Name: " + collatedTransactionName + ", Amount: " + amount + ", Count: " + count);
    }
}
