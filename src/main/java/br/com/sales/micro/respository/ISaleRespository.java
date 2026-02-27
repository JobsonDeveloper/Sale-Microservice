package br.com.sales.micro.respository;

import br.com.sales.micro.domain.Client;
import br.com.sales.micro.domain.Sale;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ISaleRespository extends MongoRepository<Sale, String> {
    public Optional<Sale> findByClient(Client client);
}
