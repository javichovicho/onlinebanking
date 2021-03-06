/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.onlinebanking.service;

import com.mycompany.onlinebanking.model.Account;
import com.mycompany.onlinebanking.model.Customer;
import com.mycompany.onlinebanking.model.Transaction;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
/**
 * 01/08/2018
 * @author jagon
 */
public class AccountService {
    //===========================================
    //=	Attributes
    //===========================================

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("Unit");
    private EntityManager em = emf.createEntityManager();
    private EntityTransaction tx = em.getTransaction();

    List<Account> list = new ArrayList<>();
    int length;
           
    public AccountService() {
        System.out.println("Account Service constructor");
    }
    
    /*public List<Message> getAllMessages() {
        return list;
    }*/
    
    // Return a single message instance from the DB, 
    //based on the message id in the HTTP call
    public Account getAccount(int id) {
        Account test = em.find(Account.class, id); 
        // Close the entity manager and EM factory
        //em.close();
        //emf.close();
        return test;
    }
    
    // Return a single user instance from the DB, 
    //based on the user id in the HTTP call
    public Customer getCustomer(int id) {
        Customer test = em.find(Customer.class, id); 
            // Close the entity manager and EM factory
            //em.close();
            //emf.close();
            return test;
    }

    /*public int getLength(){
        return length;
    }*/
    public Transaction getTransaction(int id){
        Transaction test = em.find(Transaction.class, id);
        //em.close();
        //emf.close();
        return test;
    }

    
    // Messages are created, by creating/updating the User
    public void createAccount(Account a1) {

            tx.begin();
            em.persist(a1);
            tx.commit();
            
            // Close the entity manager and EM factory
            em.close();
            emf.close();

        
    }

    public void createCustomer(Customer c1) {
            System.out.println("In createUser, user object in java: " + c1.toString());
            int id = 1;
            Customer test = em.find(Customer.class, id);
            tx.begin();
            em.persist(c1);
            tx.commit();
            // Do not close em/emf here as updateUser is called in MessageResource
            // Need to leave open otherwise Emtity Manager error in console
            //em.close();
            //emf.close();
    }    
    
    public void editCustomer(Customer u1, int id) {
            System.out.println("In updateUser, user object in java: " + u1.toString());
            Customer test = em.find(Customer.class, id);
            u1.setId(id);
            u1.setPin(test.getPin());
            u1.setAddress(test.getAddress());
            u1.setEmail(test.getEmail());
            tx.begin();
            em.merge(u1);
            tx.commit();
            
            // Close em/emf here as create user/messages process finished
            em.close();
            emf.close();
    }
    public void editAccount(Account account, int id){
        Account test = em.find(Account.class, id);
        account.setId(id);
        account.setCustomer(test.getCustomer());
        tx.begin();
        em.merge(account);
        tx.commit();
        //em.close();
        //emf.close();
    }
    public void updateCustomer(Customer u1) {
            System.out.println("In updateUser, user object in java: " + u1.toString());
            
            tx.begin();
            em.persist(u1);            
            tx.commit();
            
            // Close em/emf here as create user/messages process finished
            em.close();
            emf.close();
    }
    
    public void createTransaction(Transaction t1){
        System.out.println("In createUser, user object in java: " + t1.toString());
        int id = 1;
        int number = 1;
        Account acc = em.find(Account.class, number);
        Transaction test = em.find(Transaction.class, id);
        double newBalance = acc.getBalance() + 69;
        Account updatedAccount = new Account(acc.getId(), acc.getType(), acc.getNumber(), newBalance);
        
        Customer customer = em.find(Customer.class, 1);
        updatedAccount.setCustomer(customer);
        
        t1.setAccount(acc);
        tx.begin();
        em.persist(t1);
        em.merge(updatedAccount);
        tx.commit();
    }
    
    public void lodge(Transaction t1){
        int id = 1;
        Transaction test = em.find(Transaction.class, id);
        tx.begin();
        em.persist(t1);
        tx.commit();
    }
    
    public void deleteCustomer(int id){
        Customer test = em.find(Customer.class, id);
        if(test != null){
            tx.begin();
            em.remove(test);
            tx.commit();
            // em.close();
        }
            
    }
    
    public void deleteAccount(int id){
        Account test = em.find(Account.class, id);
        if(test != null){
            tx.begin();
            Query query = em.createNativeQuery("DELETE FROM ACCOUNT WHERE ID = " + id);
            query.executeUpdate();
            //em.remove(test);
            tx.commit();
            // em.close();
        }
            
    }
    // Resource: https://howtodoinjava.com/jpa/jpa-remove-delete-entity-example/
    public void deleteTransaction(int id){
        Transaction test = em.find(Transaction.class, id);
        if(test != null){
            tx.begin();
            Query query = em.createNativeQuery("DELETE FROM TRANSACTION WHERE ID = " + id);
            query.executeUpdate();
            //em.remove(test);
            tx.commit();
            //em.close();
        }
            
    }
    
    public int getCustomerIdByName(String name){
        int num = 1;
        Customer test = em.find(Customer.class, num);
        
        int id = ((Number)(em.createNativeQuery("SELECT ID FROM CUSTOMER WHERE NAME LIKE \'" + name + 
                "%\'").getSingleResult())).intValue();
        return id;
    }
    
    public int getAccountIdByNumber(int num){
        int id = ((Number)(em.createNativeQuery("SELECT ID FROM ACCOUNT WHERE NUMBER = " + 
                num).getSingleResult())).intValue();
        return id;
    }
    
    public boolean checkName(String name){
        int id = ((Number)(em.createNativeQuery("SELECT ID FROM CUSTOMER WHERE NAME LIKE \'" + name + 
                "%\'").getSingleResult())).intValue();
        boolean condition = true;
        if(id > 0)
            condition = false;
        return condition;
    }

}
