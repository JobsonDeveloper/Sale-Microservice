package br.com.sales.micro.respository;

import br.com.sales.micro.domain.Sale;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISaleRepository extends MongoRepository<Sale, String> {
}
