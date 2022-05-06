package com.nnk.springboot.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

/**
 * Rule name entity
 */
@Getter
@Setter
@Entity
@Table(name = "rulename")
public class RuleName {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Integer id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 125, message = "Must be at most 125 characters in length")
    String name;

    @Size(max = 125, message = "Must be at most 125 characters in length")
    String description;

    @Size(max = 125, message = "Must be at most 125 characters in length")
    String json;

    @Size(max = 512, message = "Must be at most 512 characters in length")
    String template;

    @Size(max = 125, message = "Must be at most 125 characters in length")
    String sqlStr;

    @Size(max = 125, message = "Must be at most 125 characters in length")
    String sqlPart;

    public RuleName(String name, String description, String json, String template, String sqlStr, String sqlPart) {
        this.name = name;
        this.description = description;
        this.json = json;
        this.template = template;
        this.sqlStr = sqlStr;
        this.sqlPart = sqlPart;
    }

    public RuleName() {
    }
}
