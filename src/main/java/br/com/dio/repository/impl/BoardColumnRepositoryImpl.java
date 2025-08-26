package br.com.dio.repository.impl;

import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.repository.BoardColumnRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class BoardColumnRepositoryImpl implements BoardColumnRepository {
    private final Connection connection;
    
    public BoardColumnRepositoryImpl(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public List<BoardColumnEntity> findByBoardId(Long boardId) throws SQLException {
        return new BoardColumnDAO(connection).findByBoardId(boardId);
    }
    
    @Override
    public Optional<BoardColumnEntity> findById(Long id) throws SQLException {
        return new BoardColumnDAO(connection).findById(id);
    }
}