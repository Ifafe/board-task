package br.com.dio.repository.impl;

import br.com.dio.persistence.dao.BlockDAO;
import br.com.dio.repository.BlockRepository;

import java.sql.Connection;
import java.sql.SQLException;

public class BlockRepositoryImpl implements BlockRepository {
    private final Connection connection;
    
    public BlockRepositoryImpl(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public void block(String reason, Long cardId) throws SQLException {
        new BlockDAO(connection).block(reason, cardId);
    }
    
    @Override
    public void unblock(String reason, Long cardId) throws SQLException {
        new BlockDAO(connection).unblock(reason, cardId);
    }
}