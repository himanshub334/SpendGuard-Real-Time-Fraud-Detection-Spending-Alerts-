package com.spendguard.generator;
import java.net.URI;import java.net.http.*;import java.time.Duration;import java.util.*;
/** Posts synthetic transactions to a local SpendGuard API. Never use live payment data. */
public class TransactionGenerator {
 public static void main(String[] args)throws Exception{String api=args.length>0?args[0]:"http://localhost:8080/api/transactions";var http=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();var random=new Random(42);
  for(int i=0;i<30;i++){boolean fraud=i%4==0;String body="""
   {"customerId":"generator-%d","merchant":"%s","merchantCategory":"%s","amount":%.2f,"currency":"USD","deviceId":"%s","deviceConsistent":%s,"country":"%s","transactionsLastHour":%d,"transactionsLast24Hours":%d,"isCardNotPresent":%s,"distanceFromUsualKm":%.1f}
   """.formatted(1000+random.nextInt(12),fraud?"Nova Electronics":"Green Market",fraud?"electronics":"grocery",fraud?450+random.nextDouble()*700:10+random.nextDouble()*80,fraud?"new-device":"usual-device",!fraud,fraud?"GB":"US",fraud?4+random.nextInt(4):1, fraud?7+random.nextInt(7):3,fraud, fraud?600+random.nextDouble()*1500:5+random.nextDouble()*25);
   var request=HttpRequest.newBuilder(URI.create(api)).header("Content-Type","application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build();var response=http.send(request,HttpResponse.BodyHandlers.ofString());System.out.println(response.statusCode()+" "+response.body());Thread.sleep(100);
  }
 }
}
