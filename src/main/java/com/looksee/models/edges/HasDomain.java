package com.looksee.models.edges;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import com.looksee.models.Account;
import com.looksee.models.Domain;

@RelationshipEntity
public class HasDomain {
 
    @Id
    Long id;
 
    //private Collection<String> roles;
    @StartNode
    private Account account;
    
    @EndNode
    private Domain domain;
    
    public Domain getDomain(){
    	return this.domain;
    }
    
    public void setDomain(Domain domain){
    	this.domain = domain;
    }
    
    public Account getAccount(){
    	return this.account;
    }
    
    public void setAccount(Account account){
    	this.account = account;
    }
}