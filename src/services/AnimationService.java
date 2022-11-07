package com.looksee.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.looksee.models.Animation;
import com.looksee.models.repository.AnimationRepository;

@Service
public class AnimationService {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(AnimationService.class.getName());

	@Autowired
	private AnimationRepository animation_repo;
	
	public Animation findByKey(String key){
		return animation_repo.findByKey(key);
	}
	
	public Animation save(Animation animation){
		Animation record = findByKey(animation.getKey());
		if(record == null){
			record = animation_repo.save(animation);
		}
		return record;
	}
}
