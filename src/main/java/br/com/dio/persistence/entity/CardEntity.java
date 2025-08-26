package br.com.dio.persistence.entity;

import lombok.Data;

@Data
public class CardEntity {

    private Long id;
    private String title;
    private String description;
    private CardPriorityEnum priority = CardPriorityEnum.MEDIUM; // Default priority
    private java.time.LocalDateTime dueDate;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    private BoardColumnEntity boardColumn = new BoardColumnEntity();

}
