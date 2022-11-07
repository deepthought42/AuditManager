package com.looksee.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.looksee.models.PageLoadAnimation;
import com.looksee.models.repository.PageLoadAnimationRepository;

@Service
public class PageLoadAnimationService {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(PageLoadAnimation.class.getName());

	@Autowired
	private PageLoadAnimationRepository animation_repo;

	public PageLoadAnimation findByKey(String key){
		return animation_repo.findByKey(key);
	}
	
	public PageLoadAnimation save(PageLoadAnimation animation){
		PageLoadAnimation record = findByKey(animation.getKey());
		if(record == null){
			record = animation_repo.save(animation);
		}
		return record;
	}
}
