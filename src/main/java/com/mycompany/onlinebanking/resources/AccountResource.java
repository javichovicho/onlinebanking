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
import javax.ws.rs.DELETE;
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
    // Postman body:
    // {"name":"Javier Gonzales","address":"12 Twelve Street","email":"ja@go.com","pin":"1234"}
    // CREATE CUSTOMER !!!
    @POST
    @Path("/createCustomer")
    // @Produces(MediaType.APPLICATION_XML)
    // @Produces(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
        public Response createCustomer(String body) {

        Gson gson = new Gson(); 
        Customer customer = gson.fromJson(body, Customer.class);
        
        AccountService as = new AccountService();
        
        if(as.checkName(customer.getName()))
            return Response.status(200).entity("User name already exists").build();
        
        as.createCustomer(customer);
        return Response.status(200)
                .type(MediaType.APPLICATION_JSON)
                .entity(gson.toJson("Customer: " + customer.getName() +
                " created.")).build();
        // return Response.status(200)
        //    .type(MediaType.APPLICATION_XML)
        //    .entity(gson.toJson("Customer: " + customer.getName() +
        //        " created.")).build();
        
    }
        
    // curl -v -X GET http://localhost:49000/api/accounts/customer/1
    // GET CUSTOMER'S DETAILS !!!
    @GET
    @Path("/customer/{customerId}")
    // @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_XML)
    public Response getCustomer(@PathParam("customerId") int id) {

       Gson gson = new Gson(); 
       AccountService as = new AccountService();

       //return Response.status(200).entity(gson.toJson(ms.getUser(id))).build();
       return Response.status(200).entity(as.getCustomer(id)).build();
    }
    
    // http://127.0.0.1:49000/api/accounts/editCustomer/1
    // Postman body: {"name":"Eddy"}
    // EDIT CUSTOMER'S NAME !!!
    @POST
    @Path("/editCustomer/{customerId}")
    public Response editUser(@PathParam("customerId") int id, String body) {
        
        Gson gson = new Gson(); 
        AccountService as = new AccountService();
        
        Customer customer = gson.fromJson(body, Customer.class);
        as.editCustomer(customer, id);
        
        return Response.status(200).entity("Customer edited").build();
    }
    
    // http://127.0.0.1:49000/api/accounts/createAccountExistingUser/1
    // Postman body:
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
        
        return Response.status(200).entity("Account added to customer with id: " 
                + a1.getCustomer().getId()).build();
    }
      
    // http://localhost:49000/api/accounts/account/1
    // GET ACCOUNT INFO ON ID - simple!!!
    @GET
    @Path("/account/{accountId}")
    // @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_XML)
    public Response getAccountById(@PathParam("accountId") int id) {

       Gson gson = new Gson();
       AccountService as = new AccountService();

       return Response.status(200).entity((as.getAccount(id))).build();
    }
    
    // http://localhost:49000/api/accounts/customer/1/account/2
    // GET ACCOUNT INFO ON ID and user id !!!
    @GET
    @Path("/customer/{customerId}/account/{accountId}")
    // @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
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
    
    // Lodgement when the user deposits money into an account
    // http://localhost:49000/api/accounts/createTransaction/1
    // Postman body:
    // {"amount":"15.50","description":"pay","type":"lodgement","created":"2018-08-08"}
    @POST
    @Path("/createTransaction/{accountId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response createTransaction(@PathParam("accountId") int aid, String body) {
        
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        //Gson gson = new Gson();
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
    
    // http://localhost:49000/api/accounts/customer/7/pin/4444
    // Validate User
    @GET
    @Path("/customer/{customerId}/pin/{pin}")
    @Produces(MediaType.APPLICATION_XML)
    public Response AuthenticateCustomer(@PathParam("customerId") int id, 
        @PathParam("pin") int pin) {
        
        Gson gson = new Gson();
        AccountService as = new AccountService();
        
        // Find the user by name
        Customer customer = as.getCustomer(id);
        int verifyingPin = customer.getPin();
        if(verifyingPin == pin){
            return Response.status(200).entity("Credentials successfully verified").build();
        }
        
        return Response.status(200).entity(Response.Status.NOT_FOUND).build();
    }
    
    // TRANSFER FROM ONE ACCOUNT TO ANOTHER
    // http://localhost:49000/api/accounts/transfer/2/4
    // Postman body:
    // {"amount":"20","description":"save","type":"transfer","created":"2018-08-08"}
    @POST
    @Path("/transfer/{origin}/{recipient}")
    @Produces(MediaType.APPLICATION_XML)
    public Response transferFunds(@PathParam("origin") int oid, 
            @PathParam("recipient") int rid, String body){
        
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        
        AccountService as = new AccountService();
            
        Transaction t1 = gson.fromJson(body, Transaction.class);
        
        Account origin = as.getAccount(oid);
        if(t1.getAmount() > origin.getBalance())
            return Response.status(200).entity("No funds available").build();
        Account newOrigin = new Account();
        newOrigin.setId(origin.getId());
        newOrigin.setType(origin.getType());
        newOrigin.setNumber(origin.getNumber());
        newOrigin.setBalance(origin.getBalance() - t1.getAmount());
        newOrigin.setCustomer(origin.getCustomer());
        
        as.editAccount(newOrigin, origin.getId());
        
        Account recipient = as.getAccount(rid);
        Account newRecipient = new Account();
        newRecipient.setId(recipient.getId());
        newRecipient.setType(recipient.getType());
        newRecipient.setNumber(recipient.getNumber());
        newRecipient.setBalance(recipient.getBalance() + t1.getAmount());
        newRecipient.setCustomer(recipient.getCustomer());
        
        as.editAccount(newRecipient, recipient.getId());
        
        t1.setBalance(recipient.getBalance() + t1.getAmount());
        t1.setAccount(origin);
        as.lodge(t1);
            
        return Response.status(200).entity("Funds transferred").build();
    }
    
    // http://127.0.0.1:49000/api/accounts/deleteCustomer/8
    // DELETE CUSTOMER !!!
    @DELETE
    @Path("/deleteCustomer/{customerId}")
    // @Produces(MediaType.APPLICATION_XML)
    // @Produces(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
        public Response deleteCustomer(@PathParam("customerId") int id) {

        Gson gson = new Gson(); 
        
        AccountService ms = new AccountService();
        
        try{
            Customer customer = ms.getCustomer(id);
            String name = customer.getName();
            ms.deleteCustomer(id);
            
            return Response.status(200)
                .type(MediaType.APPLICATION_JSON)
                .entity(gson.toJson("Customer " + name + " deleted.")).build();
        }catch(Exception e){
            // if customer not found
            return Response.status(200).entity(Response.Status.NOT_FOUND).build();
        }
        
        // return Response.status(200)
        //    .type(MediaType.APPLICATION_XML)
        //    .entity(gson.toJson("Customer: " + customer.getName() +
        //        " created.")).build();
        
    }
    
    // http://127.0.0.1:49000/api/accounts/balance/3
    // GET BALANCE for a specified account (id)
    @GET
    @Path("/balance/{accountId}")
    //@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_XML)
    public Response getAccountBalance(@PathParam("accountId") int id) {

        Gson gson = new Gson(); 
        AccountService as = new AccountService();
       
        try{
            Account account = as.getAccount(id);
            String balance = "Balance for account with id: " + id + " is: " 
                + account.getBalance() + " euro";
            
            return Response.status(200).entity(balance).build();
            
        }catch(Exception e){
            // if account not found
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    
    // http://127.0.0.1:49000/api/accounts/deleteAccount/3
    // DELETE ACCOUNT !!!
    @DELETE
    @Path("/deleteAccount/{accountId}")
    // @Produces(MediaType.APPLICATION_XML)
    // @Produces(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
        public Response deleteAccount(@PathParam("accountId") int id) {

        Gson gson = new Gson(); 
        
        AccountService as = new AccountService();
        
        try{
            Account account = as.getAccount(id);
            int number = account.getNumber();
            as.deleteAccount(id);
            
            return Response.status(200)
                .type(MediaType.APPLICATION_JSON)
                .entity(gson.toJson("Account with number: " + 
                        number + " deleted.")).build();
        }catch(Exception e){
            // if account not found
            return Response.status(200).entity(Response.Status.NOT_FOUND).build();
        }
        
        // return Response.status(200)
        //    .type(MediaType.APPLICATION_XML)
        //    .entity(gson.toJson("Account with number: " +
        //        number + " deleted.")).build();
        
    }
        
    // Withdraw money from account
    // http://localhost:49000/api/accounts/withdraw/2
    // Postman body: 
    // {"amount":"10","description":"cash out","type":"withdrawal","created":"2018-08-08"}
    @POST
    @Path("/withdraw/{origin}")
    @Produces(MediaType.APPLICATION_XML)
    public Response withdrawFunds(@PathParam("origin") int oid, String body){
        
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        
        AccountService as = new AccountService();
            
        Transaction t1 = gson.fromJson(body, Transaction.class);
        
        Account origin = as.getAccount(oid);
        if(t1.getAmount() > origin.getBalance())
            return Response.status(200).entity("No funds available").build();
        Account newOrigin = new Account();
        newOrigin.setId(origin.getId());
        newOrigin.setType(origin.getType());
        newOrigin.setNumber(origin.getNumber());
        double fundsWithdrawn = t1.getAmount();
        newOrigin.setBalance(origin.getBalance() - t1.getAmount());
        newOrigin.setCustomer(origin.getCustomer());
        
        as.editAccount(newOrigin, origin.getId());
        
        t1.setBalance(origin.getBalance());
        t1.setAccount(origin);
        as.lodge(t1);
            
        return Response.status(200).entity("Funds withdrawn: " + 
                fundsWithdrawn).build();
    }
    
    // http://127.0.0.1:49000/api/accounts/deleteTransaction/9
    // DELETE Transaction !!!
    @DELETE
    @Path("/deleteTransaction/{transactionId}")
    // @Produces(MediaType.APPLICATION_XML)
    // @Produces(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
        public Response deleteTransaction(@PathParam("transactionId") int id) {

        Gson gson = new Gson();
        
        AccountService as = new AccountService();
        
        try{
            Transaction transaction = as.getTransaction(id);
            int number = transaction.getId();
            as.deleteTransaction(id);
            
            return Response.status(200)
                .type(MediaType.APPLICATION_JSON)
                .entity(gson.toJson("Transaction with id: " + 
                        number + " deleted.")).build();
        }catch(Exception e){
            // if account not found
            return Response.status(200).entity(Response.Status.NOT_FOUND).build();
        }
        
        // return Response.status(200)
        //    .type(MediaType.APPLICATION_XML)
        //    .entity(gson.toJson("Transaction with id: " + 
        //        number + " deleted.")).build();
        
    }
        
    // Withdraw money from account by specifying name, pin and account number
    // http://localhost:49000/api/accounts/withdraw/Stuart/5566/609837/5
    @POST
    @Path("/withdraw/{customerName}/{pin}/{accNum}/{amount}")
    @Produces(MediaType.APPLICATION_XML)
    public Response withdraw(@PathParam("customerName") String name,
            @PathParam("pin") int pin, @PathParam("accNum") int num,
            @PathParam("amount") double amount){
        
        Gson gson = new Gson();
        
        AccountService as = new AccountService();
        // get customer validated
        int id = as.getCustomerIdByName(name);
        Customer customer = as.getCustomer(id);
        // get account
        if(customer.getPin() != pin)
            return Response.status(200).entity("Sorry wrong pin").build();
        // check if funds available
        int aid = as.getAccountIdByNumber(num);
        Account account = as.getAccount(aid);
        if(account.getBalance() < amount)
            return Response.status(200).entity("Insufficient funds").build();
        account.setBalance(account.getBalance() - amount);
        // update account
        as.editAccount(account, aid);
        // create transaction record
        Transaction transaction = new Transaction("Withdrawal", "Cash out", amount);
        transaction.setBalance(account.getBalance());
        transaction.setCreated(new Date());
        transaction.setAccount(account);
        as.lodge(transaction);
        
        return Response.status(200).entity("Here's your cash: " + amount + " euro.").build();
        
        //return Response.status(200).entity("Funds withdrawn: " + 
        //        amount).build();
    }
    
    // http://127.0.0.1:49000/api/accounts/transaction/3
    // GET TRANSACTION DETAILS
    @GET
    @Path("/transaction/{transactionId}")
    //@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_XML)
    public Response getTransactionDetails(@PathParam("transactionId") int id) {

        Gson gson = new Gson(); 
        AccountService as = new AccountService();
       
        try{
            Transaction transaction = as.getTransaction(id);
            
            return Response.status(200).entity(transaction).build();
            
        }catch(Exception e){
            // if transaction not found
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
