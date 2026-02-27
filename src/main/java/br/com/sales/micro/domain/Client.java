package br.com.sales.micro.domain;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Clients")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private String id;
    private String name;
    private Long cpf;
    private Email email;
    private Long phone;
    private String address;
}
