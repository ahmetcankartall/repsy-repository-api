package com.repsy.repsy_repository_api.repository;

import com.repsy.repsy_repository_api.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PackageRepository extends JpaRepository<Package, Long> {
    Optional<Package> findByNameAndVersion(String name, String version);
} 