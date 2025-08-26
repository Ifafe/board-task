package br.com.dio.repository.impl;

import br.com.dio.dto.BoardDetailsDTO;
import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.dao.BoardDAO;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.repository.BoardRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class BoardRepositoryImpl implements BoardRepository {
    private final Connection connection;
    
    public BoardRepositoryImpl(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public BoardEntity save(BoardEntity board) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        try{
            dao.insert(board);
            var columns = board.getBoardColumns().stream().map(c -> {
                c.setBoard(board);
                return c;
            }).toList();
            for (var column :  columns){
                boardColumnDAO.insert(column);
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
        return board;
    }
    
    @Override
    public Optional<BoardEntity> findById(Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        var optional = dao.findById(id);
        if (optional.isPresent()){
            var entity = optional.get();
            entity.setBoardColumns(boardColumnDAO.findByBoardId(entity.getId()));
            return Optional.of(entity);
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<BoardDetailsDTO> findDetailsById(Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        var optional = dao.findById(id);
        if (optional.isPresent()){
            var entity = optional.get();
            var columns = boardColumnDAO.findByBoardIdWithDetails(entity.getId());
            var dto = new BoardDetailsDTO(entity.getId(), entity.getName(), columns);
            return Optional.of(dto);
        }
        return Optional.empty();
    }
    
    @Override
    public boolean deleteById(Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        try{
            if (!dao.exists(id)) {
                return false;
            }
            dao.delete(id);
            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }
    
    @Override
    public boolean existsById(Long id) throws SQLException {
        return new BoardDAO(connection).exists(id);
    }
}