package com.shelleynason.expensetracker.service.user;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 * A repository for {@link User} objects.
 * To be magically implemented by Spring Data.
 *
 */
public interface UserRepository extends CrudRepository<User, Long>{

    List<User> findByUsername(String username);
}
