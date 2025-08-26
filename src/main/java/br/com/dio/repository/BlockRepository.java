package br.com.dio.repository;

import java.sql.SQLException;

public interface BlockRepository {
    void block(String reason, Long cardId) throws SQLException;
    void unblock(String reason, Long cardId) throws SQLException;
}