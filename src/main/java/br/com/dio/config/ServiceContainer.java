package br.com.dio.config;

import br.com.dio.repository.impl.*;
import br.com.dio.service.*;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;

@RequiredArgsConstructor
public class ServiceContainer {
    private final Connection connection;
    
    private BoardService boardService;
    private BoardQueryService boardQueryService;
    private CardService cardService;
    private CardQueryService cardQueryService;
    private BoardColumnQueryService boardColumnQueryService;
    
    public BoardService getBoardService() {
        if (boardService == null) {
            boardService = new BoardService(connection);
        }
        return boardService;
    }
    
    public BoardQueryService getBoardQueryService() {
        if (boardQueryService == null) {
            boardQueryService = new BoardQueryService(connection);
        }
        return boardQueryService;
    }
    
    public CardService getCardService() {
        if (cardService == null) {
            cardService = new CardService(connection);
        }
        return cardService;
    }
    
    public CardQueryService getCardQueryService() {
        if (cardQueryService == null) {
            cardQueryService = new CardQueryService(connection);
        }
        return cardQueryService;
    }
    
    public BoardColumnQueryService getBoardColumnQueryService() {
        if (boardColumnQueryService == null) {
            boardColumnQueryService = new BoardColumnQueryService(connection);
        }
        return boardColumnQueryService;
    }
}