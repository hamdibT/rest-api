package com.innso.restapi.repository;

import com.innso.restapi.model.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository  extends CrudRepository<Message, Long> {
}
