package com.sbaldasso.combobackend.modules.user.repository;

import com.sbaldasso.combobackend.modules.user.domain.UserDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserDocumentRepository extends JpaRepository<UserDocument, UUID> {
    List<UserDocument> findByUserId(UUID userId);
    List<UserDocument> findByVerifiedFalse();
}
