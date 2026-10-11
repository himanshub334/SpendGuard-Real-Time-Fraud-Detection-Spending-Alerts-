package com.spendguard.controller;
import com.fasterxml.jackson.databind.JsonNode;
import com.spendguard.model.*;
import com.spendguard.repository.TransactionRepository;
import com.spendguard.service.FraudScoringClient;
import jakarta.validation.Valid;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;
@RestController @RequestMapping("/api") public class TransactionController {
 private final TransactionRepository repo; private final FraudScoringClient scoring; private final SimpMessagingTemplate ws; private final StringRedisTemplate redis;
 public TransactionController(TransactionRepository r,FraudScoringClient s,SimpMessagingTemplate w,StringRedisTemplate redis){repo=r;scoring=s;ws=w;this.redis=redis;}
 @GetMapping("/health") public Map<String,String> health(){return Map.of("status","ok");}
 @GetMapping("/transactions") public List<TransactionRecord> list(){return repo.findTop100ByOrderByCreatedAtDesc();}
 @PostMapping("/transactions") public TransactionRecord create(@Valid @RequestBody TransactionRequest req){
  try { String key="spendguard:features:"+req.customerId(); redis.opsForValue().set(key,req.transactionsLastHour()+":"+req.transactionsLast24Hours()); } catch(Exception ignored) { /* cache is best-effort in local demo */ }
  JsonNode s; try{s=scoring.score(req);}catch(Exception e){throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Scoring service unavailable");}
  TransactionRecord record=repo.save(new TransactionRecord(req,s.path("riskScore").asDouble(),s.path("riskLevel").asText("low"),s.path("alertMessage").asText("Transaction scored")));
  ws.convertAndSend("/topic/alerts",record); return record;
 }
}
