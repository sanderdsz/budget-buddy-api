package com.asana.budgetbuddy.repository;

import com.asana.budgetbuddy.model.UserConnectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserConnectionRequestRepository extends JpaRepository<UserConnectionRequest, Long> {
    List<UserConnectionRequest> findAllById(Long id);

    List<UserConnectionRequest> findAllByUserParent_IdOrderByCreatedAtDesc(Long id);

    List<UserConnectionRequest> findAllByUserChildren_IdOrderByCreatedAtDesc(Long id);

    Optional<UserConnectionRequest> findByUserChildren_IdOrUserParent_IdAndUserParent_IdOrUserChildren_Id(Long id1, Long id2, Long id3, Long id4);
}
