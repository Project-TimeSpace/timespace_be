package com.backend.Admin.Repository;

import com.backend.Admin.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

}
