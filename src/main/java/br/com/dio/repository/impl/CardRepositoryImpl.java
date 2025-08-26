package br.com.dio.repository.impl;

import br.com.dio.dto.CardDetailsDTO;
import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.CardEntity;
import br.com.dio.repository.CardRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class CardRepositoryImpl implements CardRepository {
    private final Connection connection;
    
    public CardRepositoryImpl(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public CardEntity save(CardEntity card) throws SQLException {
        return new CardDAO(connection).insert(card);
    }
    
    @Override
    public Optional<CardDetailsDTO> findById(Long id) throws SQLException {
        return new CardDAO(connection).findById(id);
    }
    
    @Override
    public void moveToColumn(Long columnId, Long cardId) throws SQLException {
        new CardDAO(connection).moveToColumn(columnId, cardId);
    }
    
    @Override
    public void deleteById(Long id) throws SQLException {
        // Implementation would depend on whether you want to delete or mark as deleted
        throw new UnsupportedOperationException("Delete operation not implemented yet");
    }
}