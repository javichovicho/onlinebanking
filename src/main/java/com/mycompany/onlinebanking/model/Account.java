/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.onlinebanking.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * 26/07/2018
 * @author jagon
 */
@Entity
@Table
@XmlRootElement
public class Account implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String type;
    private int number;
    private double balance;
    @ManyToOne(fetch=FetchType.EAGER) // EAGER - fetch all of the relationships
    @JoinColumn(name="customer_id")
    private Customer customer;
    
    @LazyCollection(LazyCollectionOption.FALSE)
    //@OneToMany(targetEntity=Transaction.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy="account")
    @OneToMany(targetEntity=Transaction.class, cascade = CascadeType.ALL, mappedBy="account")
    private List<Transaction> transactions = new ArrayList<>();
    
    public Account(){
        //transactions = new ArrayList<>();
    }

    public Account(String type) {
        this.type = type;
    }

    public Account(int id, String type, int number, double balance, Customer customer) {
        this.id = id;
        this.type = type;
        this.number = number;
        this.balance = balance;
        this.customer = customer;
    }
    
    public Account(int id, String type, int number, double balance){
        this.id = id;
        this.type = type;
        this.number = number;
        this.balance = balance;
        //transactions = new ArrayList<>();
    }
    
    public Account(int id, String type, int number){
        this.id = id;
        this.type = type;
        this.number = number;
        //transactions = new ArrayList<>();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }

    public double getBalance() {
        return balance;
    }
    
    // XmlElementWrapper is used to label the Messages array/xml tag 
    // when fetching/GETting the user from postman
    @XmlElementWrapper(name="transactions")
    @XmlElementRef()
    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    
    
    // Use this version of toString, otherwise get Stack Overflow error
    // since there is a circular relationship between User and Message
    @Override
    public String toString() {
        return "Account{" + "id=" + id + ", type=" + type + ", number= " +
                number + ", balance= " + balance + '}';
    }
    
    /*
    public void addTransaction(Transaction transaction){
        transactions.add(transaction);
    }
    */
    
    // @XmlTransient - do NOT return the user reference when 
    // GETting the message in postman
    @XmlTransient
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

}
