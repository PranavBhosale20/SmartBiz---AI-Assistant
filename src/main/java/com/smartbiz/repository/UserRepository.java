package com.smartbiz.repository;

import com.smartbiz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// JpaRepository<User, Long> gives us a huge set of database operations
// for free - save(), findAll(), findById(), deleteById(), and more -
// without us writing a single line of SQL or implementation code.
// "User" is the entity type this repository manages, "Long" is the
// type of that entity's @Id field.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA reads this method's NAME and auto-generates the
    // SQL behind it. "findByEmail" literally becomes:
    // SELECT * FROM users WHERE email = ?
    // We never write that query ourselves - the method name IS the query.
    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);
}