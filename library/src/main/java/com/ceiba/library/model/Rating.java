package com.ceiba.library.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating implements Serializable {

    private Long id;
    private Long bookId;
    private Stars stars;
}