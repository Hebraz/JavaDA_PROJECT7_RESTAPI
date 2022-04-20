package com.nnk.springboot.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "rating")
public class Rating {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Integer id;
    @NotBlank(message = "Moodys rating is mandatory")
    @Size(max = 125, message = "Must be at most 125 characters in length")
    String moodysRating;
    @NotBlank(message = "SandP rating is mandatory")
    @Size(max = 125, message = "Must be at most 125 characters in length")
    String sandPRating;
    @NotBlank(message = "Fitch rating is mandatory")
    @Size(max = 125, message = "Must be at most 125 characters in length")
    String fitchRating;
    @NotNull(message = "must not be null")
    @Max(value=127, message = "must be less than 128")
    @Min(value=0, message = "must be positive")
    Integer orderNumber;
}
