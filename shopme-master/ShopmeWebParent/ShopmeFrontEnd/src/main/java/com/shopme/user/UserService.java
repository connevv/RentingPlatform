package com.shopme.user;

import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.User;

@Service
@Transactional
public class UserService {
	public static final int USERS_PER_PAGE = 4;
	
	@Autowired
	private UserRepository userRepo;
	
	public User getByEmail(String email) {
		return userRepo.getUserByEmail(email);
	}
	
	public User getById(Integer id) throws  UserNotFoundException{
		try {
			return userRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
	}
	
	public void updateVote(Integer id, String voteType, int vote) {
		if (voteType.equals("voteUp")) {
			userRepo.updateVotesUpStatus(id, vote + 1);
		} else if (voteType.equals("voteDown")) {
			userRepo.updateVotesDownStatus(id, vote + 1);
		}
	}
}
