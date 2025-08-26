package br.com.dio.service;

import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.dto.CardDetailsDTO;
import br.com.dio.exception.CardBlockedException;
import br.com.dio.exception.CardFinishedException;
import br.com.dio.exception.EntityNotFoundException;
import br.com.dio.persistence.dao.BlockDAO;
import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.CardEntity;
import br.com.dio.validation.ValidationUtils;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static br.com.dio.persistence.entity.BoardColumnKindEnum.CANCEL;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.FINAL;


@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public CardEntity create(final CardEntity entity) throws SQLException {
        ValidationUtils.validateCard(entity);
        
        try {
            CardDAO dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();
            return entity;
        } catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException{
        ValidationUtils.validateId(cardId);
        
        try{
            CardDAO dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            CardDetailsDTO dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card with id %s was not found".formatted(cardId))
            );
            if (dto.blocked()){
                var message = "Card %s is blocked. Please unblock it before moving.".formatted(cardId);
                throw new CardBlockedException(message);
            }
            BoardColumnInfoDTO currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("The specified card belongs to another board"));
            if (currentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("Card has already been finished");
            }
            BoardColumnInfoDTO nextColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalStateException("Card is cancelled"));
            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId ,
                       final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException{
        ValidationUtils.validateId(cardId);
        
        try{
            CardDAO dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            CardDetailsDTO dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card with id %s was not found".formatted(cardId))
            );
            if (dto.blocked()){
                var message = "Card %s is blocked. Please unblock it before moving.".formatted(cardId);
                throw new CardBlockedException(message);
            }
            BoardColumnInfoDTO currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("The specified card belongs to another board"));
            if (currentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("Card has already been finished");
            }
            boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalStateException("Card is cancelled"));
            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }

    public void block(final Long id, final String reason, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        ValidationUtils.validateId(id);
        
        try{
            CardDAO dao = new CardDAO(connection);
            var optional = dao.findById(id);
            CardDetailsDTO dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card with id %s was not found".formatted(id))
            );
            if (dto.blocked()){
                var message = "Card %s is already blocked".formatted(id);
                throw new CardBlockedException(message);
            }
            BoardColumnInfoDTO currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow();
            if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)){
                var message = "Card is in a column of type %s and cannot be blocked"
                        .formatted(currentColumn.kind());
                throw new IllegalStateException(message);
            }
            BlockDAO blockDAO = new BlockDAO(connection);
            blockDAO.block(reason, id);
            connection.commit();
        }catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void unblock(final Long id, final String reason) throws SQLException {
        ValidationUtils.validateId(id);
        
        try{
            CardDAO dao = new CardDAO(connection);
            var optional = dao.findById(id);
            CardDetailsDTO dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Card with id %s was not found".formatted(id))
            );
            if (!dto.blocked()){
                var message = "Card %s is not blocked".formatted(id);
                throw new CardBlockedException(message);
            }
            BlockDAO blockDAO = new BlockDAO(connection);
            blockDAO.unblock(reason, id);
            connection.commit();
        }catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }
    
    // Utility method to reduce code duplication
    private CardDetailsDTO getCardIfExists(Long cardId) throws SQLException {
        ValidationUtils.validateId(cardId);
        
        CardDAO dao = new CardDAO(connection);
        var optional = dao.findById(cardId);
        return optional.orElseThrow(
                () -> new EntityNotFoundException("Card with id %s was not found".formatted(cardId))
        );
    }
}
