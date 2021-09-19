package com.shopme.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopme.common.entity.User;

public interface UserRepository extends SearchRepository<User, Integer> {
	@Query("SELECT u FROM User u WHERE u.email = :email")
	public User getUserByEmail(@Param("email") String email);
	
	@Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ',"
			+ " u.lastName) LIKE %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);
	
	@Query("UPDATE User u SET u.votesUp = ?2 WHERE u.id = ?1")
	@Modifying
	public void updateVotesUpStatus(int id, int vote);
	
	@Query("UPDATE User u SET u.votesDown = ?2 WHERE u.id = ?1")
	@Modifying
	public void updateVotesDownStatus(int id, int vote);
}
