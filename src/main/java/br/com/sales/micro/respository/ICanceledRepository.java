package br.com.sales.micro.respository;

import br.com.sales.micro.domain.Canceled;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICanceledRepository extends MongoRepository<Canceled, String> {
}
