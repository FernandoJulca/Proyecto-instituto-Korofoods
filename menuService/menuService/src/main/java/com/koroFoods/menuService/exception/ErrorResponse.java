package com.koroFoods.menuService.exception;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

	private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
}
