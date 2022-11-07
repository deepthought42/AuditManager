package com.looksee.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.looksee.models.audit.performance.BugMessage;
import com.looksee.models.repository.BugMessageRepository;

@Service
public class BugMessageService {

	@Autowired
	private BugMessageRepository bug_message_repo;
	
	/**
	 * Objects are expected to be immutable as of 3/14/19. When this method is ran, if a 
	 * {@link BugMessage} already exists with a given message then it will be loaded from the database, otherwise it will be saved
	 * 
	 * @param bug_message {@link BugMessage} 
	 * @return
	 */
	public BugMessage save(BugMessage bug_message){
		BugMessage bug_message_record = bug_message_repo.findByMessage(bug_message.getMessage());
		if(bug_message_record == null){
			return bug_message_repo.save(bug_message);
		}
		return bug_message_record;
	}
}
