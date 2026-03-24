package br.com.sales.micro.domain;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private String id;
    private String cpf;
}
