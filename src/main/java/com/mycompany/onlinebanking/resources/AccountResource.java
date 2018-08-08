/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.onlinebanking.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycompany.onlinebanking.model.Account;
import com.mycompany.onlinebanking.model.Customer;
import com.mycompany.onlinebanking.model.Transaction;
import com.mycompany.onlinebanking.service.AccountService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * 01/08/2018
 * @author jagon
 */
@Path("/accounts")
//@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
//@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    // http://127.0.0.1:49000/api/accounts/createCustomer
    // Postman body: {"name":"MrBrennan"}
    // {"name":"Javier Gonzales","pin":"1234"}
    // CREATE CUSTOMER !!!
    @POST
    @Path("/createCustomer")
        
        public Response createCustomer(String body) {

        Gson gson = new Gson(); 
        Customer customer = gson.fromJson(body, Customer.class);
        
        AccountService ms = new AccountService();
        
        ms.createCustomer(customer);
        return Response.status(200).entity(gson.toJson("Customer: " + customer.getName() +
                " created.")).build();
    }
        
    // curl -v -X GET http://localhost:49000/api/accounts/customer/1
    // GET CUSTOMER'S DETAILS !!!
    @GET
    @Path("/customer/{customerId}")
    //@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_XML)
    public Response getCustomer(@PathParam("customerId") int id) {

       Gson gson = new Gson(); 
       AccountService ms = new AccountService();

       //return Response.status(200).entity(gson.toJson(ms.getUser(id))).build();
       return Response.status(200).entity(ms.getCustomer(id)).build();
    }
    
    //http://127.0.0.1:49000/api/accounts/editCustomer/1
    //Postman body: {"name":"Eddy"}
    // EDIT CUSTOMER'S NAME !!!
    @POST
    @Path("/editCustomer/{customerId}")
    public Response editUser(@PathParam("customerId") int id, String body) {
        
        Gson gson = new Gson(); 
        AccountService ms = new AccountService();
        
        Customer customer = gson.fromJson(body, Customer.class);
        ms.editCustomer(customer, id);
        
        return Response.status(200).entity("Customer edited").build();
    }
    
    // http://127.0.0.1:49000/api/accounts/createAccountExistingUser/1
    // {"type":"Savings"}
    // CREATE CUSTOMER ACCOUNT !!!
    @POST
    @Path("/createAccountExistingUser/{customerId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response createAccountExistingUser(@PathParam("customerId") int id,
            String body) {

        Gson gson = new Gson(); 
        AccountService as = new AccountService();

        Account a1 = gson.fromJson(body, Account.class);
        
        int ran = 0;
        String flo = "";
        for(int i = 0; i < 6; i++)
            flo += (int)(Math.random()*10);
        ran = Integer.parseInt(flo);
        a1.setNumber(ran);
        a1.setBalance(0.0);
        
        Customer customer = new Customer(id);
        
        a1.setCustomer(customer);
        
        as.createAccount(a1);
        
        return Response.status(200).entity("Account added to " + a1.getCustomer()).build();
    }
      
    // http://localhost:49000/api/accounts/1
    // GET ACCOUNT INFO ON ID - simple!!!
    @GET
    @Path("/account/{accountId}")
    //@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_XML)
    public Response getAccountById(@PathParam("accountId") int id) {

       Gson gson = new Gson();
       AccountService as = new AccountService();

       return Response.status(200).entity((as.getAccount(id))).build();
    }
    
    // http://localhost:49000/api/accounts/customer/1/account/2
    // GET ACCOUNT INFO ON ID and user id !!!
    // Got up to here Tuesday 7/08/2018 12:10 pm
    @GET
    @Path("/customer/{customerId}/account/{accountId}")
    //@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_XML)
    public Response getAccountByUserId(@PathParam("customerId") int id, 
        @PathParam("accountId") int aid) {
        
        Gson gson = new Gson();
        AccountService as = new AccountService();
        
        // Find the user by id
        Customer customer = as.getCustomer(id);
        
        // Find/match account by id
        List list = customer.getAccounts();
        for(int i = 0; i < list.size(); i++){
            Account account = (Account)list.get(i);
            if(account.getId() == aid)
                return Response.status(200).entity(account).build();
        }
        return Response.status(200).entity(Response.Status.NOT_FOUND).build();
    }
    /*    
    // http://127.0.0.1:49000/api/accounts/createAccountNewUser
    // {"type":"Savings","number":"123456","balance":"0.0"}
    @POST
    @Path("/createAccountNewUser")
    @Produces(MediaType.APPLICATION_XML)   
        public Response createAccountNewUser(String body) {

        Gson gson = new Gson(); 
        //Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        AccountService ms = new AccountService();
        
        Customer user1 = new Customer();
        user1.setName("Meeeee2");
        ms.createCustomer(user1);
        
        //User user1;
        //user1 = ms.getUser(2);

        Account m1 = gson.fromJson(body, Account.class);
        
        Account m2 = new Account();
        m2.setType("Savings");
        m2.setNumber(789123);
        m2.setBalance(0.0);

        //ms.createMessage(m2);
        
        m1.setCustomer(user1);
        m2.setCustomer(user1);
                
        //ms.createUser(user1);  
        //ms.createMessage(m1);  
        
        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(m1);
        accounts.add(m2);
        user1.setAccounts(accounts);
        
        //user1.getMess().add(m2);
        //user1.getMess().add(m1);
        System.out.println("In cremess2, user object in java: " + user1.toString());
        //ms.createUser(user1);
        ms.updateCustomer(user1);
        //return Response.status(200).entity(gson.toJson(m1)).build();
        return Response.status(200).entity(m1).build();
    }*/
    
    // http://localhost:49000/api/accounts/createTransaction/1
    // {"amount":"15.50","description":"pay","type":"lodgement","created": "2018-08-08"}
    @POST
    @Path("/createTransaction/{accountId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response createTransaction(@PathParam("accountId") int aid, String body) {
        
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        AccountService as = new AccountService();
        
        Transaction t1 = gson.fromJson(body, Transaction.class);
        
        Account account = as.getAccount(aid);
        double existingBalance = account.getBalance();
        t1.setBalance(t1.getAmount() + existingBalance);
        t1.setAccount(account);
        
        as.lodge(t1);
        Account updatedAccount = new Account();
        updatedAccount.setType(account.getType());
        updatedAccount.setCustomer(account.getCustomer());
        updatedAccount.setNumber(account.getNumber());
        updatedAccount.setId(account.getId());
        updatedAccount.setBalance(account.getBalance() + t1.getAmount());
                
        as.editAccount(updatedAccount, account.getId());
        
        return Response.status(200).entity("Transaction created").build();
    }

}
