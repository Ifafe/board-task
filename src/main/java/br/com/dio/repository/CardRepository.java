package br.com.dio.repository;

import br.com.dio.dto.CardDetailsDTO;
import br.com.dio.persistence.entity.CardEntity;

import java.sql.SQLException;
import java.util.Optional;

public interface CardRepository {
    CardEntity save(CardEntity card) throws SQLException;
    Optional<CardDetailsDTO> findById(Long id) throws SQLException;
    void moveToColumn(Long columnId, Long cardId) throws SQLException;
    void deleteById(Long id) throws SQLException;
}