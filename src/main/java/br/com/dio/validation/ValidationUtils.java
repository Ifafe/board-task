package br.com.dio.validation;

import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.persistence.entity.CardEntity;

public class ValidationUtils {
    
    public static void validateBoard(BoardEntity board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }
        if (board.getName() == null || board.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Board name cannot be empty");
        }
        if (board.getName().length() > 255) {
            throw new IllegalArgumentException("Board name cannot exceed 255 characters");
        }
    }
    
    public static void validateCard(CardEntity card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        if (card.getTitle() == null || card.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Card title cannot be empty");
        }
        if (card.getTitle().length() > 255) {
            throw new IllegalArgumentException("Card title cannot exceed 255 characters");
        }
        if (card.getDescription() == null || card.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Card description cannot be empty");
        }
        if (card.getDescription().length() > 255) {
            throw new IllegalArgumentException("Card description cannot exceed 255 characters");
        }
        if (card.getBoardColumn() == null) {
            throw new IllegalArgumentException("Card must belong to a board column");
        }
    }
    
    public static void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID provided");
        }
    }
}