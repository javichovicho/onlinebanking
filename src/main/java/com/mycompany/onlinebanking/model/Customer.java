/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.onlinebanking.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * 28/07/2018
 * @author jagon
 */
@Entity
@Table
@XmlRootElement
@XmlSeeAlso({Account.class})

public class Customer implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private int pin;
    
    @LazyCollection(LazyCollectionOption.FALSE)
    //@OneToMany(targetEntity=Account.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy="customer")
    @OneToMany(targetEntity=Account.class, cascade = CascadeType.ALL, mappedBy="customer")
    private List<Account> accounts = new ArrayList<>();

    public Customer() {
        //accounts = new ArrayList<>();
    }
    public Customer(String name) {
        this.name = name;
    }

    public Customer(int id) {
        this.id = id;
    }
    
    public Customer(String name, int pin) {
        this.name = name;
        this.pin = pin;
    }

    public Customer(int id, String name, int pin) {
        this.id = id;
        this.name = name;
        this.pin = pin;
        //accounts = new ArrayList<>();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPin() {
        return pin;
    }
    
    // XmlElementWrapper is used to label the Messages array/xml tag 
    // when fetching/GETting the user from postman
    @XmlElementWrapper(name="accounts")
    @XmlElementRef()
    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    
    
    // Use this version of toString, otherwise get Stack Overflow error
    // since there is a circular relationship between User and Message
    @Override
    public String toString() {
        return "Customer{" + "id=" + id + ", name=" + name + '}';
    }
    
    /*
    public void addAccount(Account account){
        accounts.add(account);
    }
    */

}
