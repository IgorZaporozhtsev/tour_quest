package com.tour.quest.repository;

import com.tour.quest.model.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessagesRepository extends CrudRepository<Message, Integer> {

    List<Message> findByTag(String tag);
}
