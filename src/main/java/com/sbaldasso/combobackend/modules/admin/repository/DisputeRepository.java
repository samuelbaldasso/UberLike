package com.sbaldasso.combobackend.modules.admin.repository;

import com.sbaldasso.combobackend.modules.admin.domain.Dispute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, UUID> {
    Page<Dispute> findByStatus(Dispute.DisputeStatus status, Pageable pageable);
    Page<Dispute> findByReporterId(UUID reporterId, Pageable pageable);
    long countByStatus(Dispute.DisputeStatus status);
}
