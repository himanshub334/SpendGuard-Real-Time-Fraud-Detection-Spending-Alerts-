package com.spendguard.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="transactions") public class TransactionRecord {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String customerId; private String merchant; private String merchantCategory; private double amount; private String currency;
 private String deviceId; private String country; private int transactionsLastHour; private int transactionsLast24Hours;
 private boolean cardNotPresent; private double distanceFromUsualKm; private boolean deviceConsistent;
 private double riskScore; private String riskLevel; @Column(length=700) private String alertMessage; private Instant createdAt=Instant.now();
 protected TransactionRecord() {}
 public TransactionRecord(TransactionRequest r, double score, String level, String message){customerId=r.customerId();merchant=r.merchant();merchantCategory=r.merchantCategory();amount=r.amount();currency=r.currency();deviceId=r.deviceId();country=r.country();transactionsLastHour=r.transactionsLastHour();transactionsLast24Hours=r.transactionsLast24Hours();cardNotPresent=r.isCardNotPresent();distanceFromUsualKm=r.distanceFromUsualKm();deviceConsistent=r.deviceConsistent();riskScore=score;riskLevel=level;alertMessage=message;}
 public Long getId(){return id;} public String getCustomerId(){return customerId;} public String getMerchant(){return merchant;} public String getMerchantCategory(){return merchantCategory;} public double getAmount(){return amount;} public String getCurrency(){return currency;} public String getDeviceId(){return deviceId;} public String getCountry(){return country;} public int getTransactionsLastHour(){return transactionsLastHour;} public int getTransactionsLast24Hours(){return transactionsLast24Hours;} public boolean isCardNotPresent(){return cardNotPresent;} public double getDistanceFromUsualKm(){return distanceFromUsualKm;} public boolean isDeviceConsistent(){return deviceConsistent;} public double getRiskScore(){return riskScore;} public String getRiskLevel(){return riskLevel;} public String getAlertMessage(){return alertMessage;} public Instant getCreatedAt(){return createdAt;}
}
