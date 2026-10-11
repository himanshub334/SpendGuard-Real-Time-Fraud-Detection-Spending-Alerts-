package com.spendguard.model;
import jakarta.validation.constraints.*;
public record TransactionRequest(@NotBlank String customerId,@NotBlank String merchant,@NotBlank String merchantCategory,
 @Positive @Max(1000000) double amount,@Size(min=3,max=3) String currency,@NotBlank String deviceId,
 @NotBlank String country,@Min(0) int transactionsLastHour,@Min(0) int transactionsLast24Hours,
 boolean isCardNotPresent,@DecimalMin("0.0") double distanceFromUsualKm, boolean deviceConsistent) {}
