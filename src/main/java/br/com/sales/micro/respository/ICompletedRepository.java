package br.com.sales.micro.respository;

import br.com.sales.micro.domain.Completed;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICompletedRepository extends MongoRepository<Completed, String> {
    public Optional<Completed> findBySaleId(String saleId);
}
