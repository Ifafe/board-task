package br.com.dio.repository;

import br.com.dio.dto.BoardDetailsDTO;
import br.com.dio.persistence.entity.BoardEntity;

import java.sql.SQLException;
import java.util.Optional;

public interface BoardRepository {
    BoardEntity save(BoardEntity board) throws SQLException;
    Optional<BoardEntity> findById(Long id) throws SQLException;
    Optional<BoardDetailsDTO> findDetailsById(Long id) throws SQLException;
    boolean deleteById(Long id) throws SQLException;
    boolean existsById(Long id) throws SQLException;
}