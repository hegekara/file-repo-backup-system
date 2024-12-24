package com.filesystem.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {
    private Object object;
    private String token;
    private String message = null;
}
