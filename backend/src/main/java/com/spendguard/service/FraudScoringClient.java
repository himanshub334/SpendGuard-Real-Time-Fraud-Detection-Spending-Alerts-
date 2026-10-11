package com.spendguard.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spendguard.model.TransactionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Map;
@Service public class FraudScoringClient {
 private final RestClient client; private final ObjectMapper mapper=new ObjectMapper();
 public FraudScoringClient(@Value("${ml.service.url}") String url){client=RestClient.builder().baseUrl(url).build();}
 public JsonNode score(TransactionRequest r){
  try { Map<String,Object> body=mapper.convertValue(r, Map.class);
   String result=client.post().uri("/score").body(body).retrieve().body(String.class); return mapper.readTree(result);
  } catch(Exception e){throw new IllegalStateException("Risk scoring service unavailable",e);}
 }
}
