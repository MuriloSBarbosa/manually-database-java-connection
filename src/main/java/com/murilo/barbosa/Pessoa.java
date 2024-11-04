package com.murilo.barbosa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("pessoa")
public class Pessoa {

    private int id;
    private String nome;
}
