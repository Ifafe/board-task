package br.com.dio.repository;

import br.com.dio.persistence.entity.BoardColumnEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BoardColumnRepository {
    List<BoardColumnEntity> findByBoardId(Long boardId) throws SQLException;
    Optional<BoardColumnEntity> findById(Long id) throws SQLException;
}