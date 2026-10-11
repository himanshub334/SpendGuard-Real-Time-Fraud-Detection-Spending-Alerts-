package com.spendguard.repository;
import com.spendguard.model.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TransactionRepository extends JpaRepository<TransactionRecord,Long>{ List<TransactionRecord> findTop100ByOrderByCreatedAtDesc(); }
