package br.com.dio.ui;

import br.com.dio.config.ServiceContainer;
import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    private final BoardEntity entity;

    public void execute() {
        try {
            System.out.printf("Welcome to board %s, please select an operation\n", entity.getId());
            var option = -1;
            while (option != 9) {
                System.out.println("1 - Create a card");
                System.out.println("2 - Move a card");
                System.out.println("3 - Block a card");
                System.out.println("4 - Unblock a card");
                System.out.println("5 - Cancel a card");
                System.out.println("6 - View board");
                System.out.println("7 - View column with cards");
                System.out.println("8 - View card");
                System.out.println("9 - Go back to the previous menu");
                System.out.println("10 - Exit");
                option = scanner.nextInt();
                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Returning to the previous menu");
                    case 10 -> System.exit(0);
                    default -> System.out.println("Invalid option, please select an option from the menu");
                }
            }
        }catch (SQLException ex){
            ex.printStackTrace();
            System.exit(0);
        }
    }

    private void createCard() throws SQLException{
        var card = new CardEntity();
        System.out.println("Please enter the card title");
        card.setTitle(scanner.next());
        System.out.println("Please enter the card description");
        card.setDescription(scanner.next());
        
        // Set card priority
        System.out.println("Please select the card priority:");
        System.out.println("1 - Low");
        System.out.println("2 - Medium");
        System.out.println("3 - High");
        System.out.println("4 - Critical");
        int priorityChoice = scanner.nextInt();
        switch (priorityChoice) {
            case 1 -> card.setPriority(br.com.dio.persistence.entity.CardPriorityEnum.LOW);
            case 2 -> card.setPriority(br.com.dio.persistence.entity.CardPriorityEnum.MEDIUM);
            case 3 -> card.setPriority(br.com.dio.persistence.entity.CardPriorityEnum.HIGH);
            case 4 -> card.setPriority(br.com.dio.persistence.entity.CardPriorityEnum.CRITICAL);
            default -> {
                System.out.println("Invalid priority selected. Setting to Medium priority.");
                card.setPriority(br.com.dio.persistence.entity.CardPriorityEnum.MEDIUM);
            }
        }
        
        // Set due date (optional)
        System.out.println("Enter due date for the card (yyyy-MM-dd HH:mm) or leave blank for no due date:");
        scanner.nextLine(); // Consume the newline character
        String dueDateStr = scanner.nextLine();
        if (!dueDateStr.isEmpty()) {
            try {
                java.time.LocalDateTime dueDate = java.time.LocalDateTime.parse(dueDateStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                card.setDueDate(dueDate);
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("Invalid date format. Card will have no due date.");
            }
        }
        
        card.setBoardColumn(entity.getInitialColumn());
        try(var connection = getConnection()){
            var serviceContainer = new ServiceContainer(connection);
            serviceContainer.getCardService().create(card);
        }
    }

    private void moveCardToNextColumn() throws SQLException {
        System.out.println("Please enter the ID of the card you want to move to the next column");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            var serviceContainer = new ServiceContainer(connection);
            serviceContainer.getCardService().moveToNextColumn(cardId, boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void blockCard() throws SQLException {
        System.out.println("Please enter the ID of the card to be blocked");
        var cardId = scanner.nextLong();
        System.out.println("Please enter the reason for blocking the card");
        var reason = scanner.next();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            var serviceContainer = new ServiceContainer(connection);
            serviceContainer.getCardService().block(cardId, reason, boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void unblockCard() throws SQLException {
        System.out.println("Please enter the ID of the card to be unblocked");
        var cardId = scanner.nextLong();
        System.out.println("Please enter the reason for unblocking the card");
        var reason = scanner.next();
        try(var connection = getConnection()){
            var serviceContainer = new ServiceContainer(connection);
            serviceContainer.getCardService().unblock(cardId, reason);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void cancelCard() throws SQLException {
        System.out.println("Please enter the ID of the card you want to move to the cancellation column");
        var cardId = scanner.nextLong();
        var cancelColumn = entity.getCancelColumn();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            var serviceContainer = new ServiceContainer(connection);
            serviceContainer.getCardService().cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void showBoard() throws SQLException {
        try(var connection = getConnection()){
            var serviceContainer = new ServiceContainer(connection);
            var optional = serviceContainer.getBoardQueryService().showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                b.columns().forEach(c ->
                        System.out.printf("Column [%s] type: [%s] has %s cards\n", c.name(), c.kind(), c.cardsAmount())
                );
            });
        }
    }

    private void showColumn() throws SQLException {
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumnId = -1L;
        while (!columnsIds.contains(selectedColumnId)){
            System.out.printf("Choose a column from board %s by ID\n", entity.getName());
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumnId = scanner.nextLong();
        }
        try(var connection = getConnection()){
            var serviceContainer = new ServiceContainer(connection);
            var column = serviceContainer.getBoardColumnQueryService().findById(selectedColumnId);
            column.ifPresent(co -> {
                System.out.printf("Column %s type %s\n", co.getName(), co.getKind());
                displayCardsWithPagination(co.getCards(), 0);
            });
        }
    }
    
    private void displayCardsWithPagination(List<CardEntity> cards, int page) {
        final int ITEMS_PER_PAGE = 10;
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, cards.size());
        
        if (cards.isEmpty()) {
            System.out.println("No cards in this column.");
            return;
        }
        
        System.out.println("Cards (Page " + (page + 1) + " of " +
                          ((cards.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE) + ")");
        
        for (int i = startIndex; i < endIndex; i++) {
            CardEntity card = cards.get(i);
            System.out.printf("Card %s - %s [%s]\n", card.getId(), card.getTitle(),
                             card.getPriority() != null ? card.getPriority() : "MEDIUM");
        }
        
        if (endIndex < cards.size()) {
            System.out.println("Enter 'n' for next page, 'p' for previous page, or 'b' to go back");
            scanner.nextLine(); // Consume newline
            String input = scanner.nextLine();
            if ("n".equals(input) && endIndex < cards.size()) {
                displayCardsWithPagination(cards, page + 1);
            } else if ("p".equals(input) && page > 0) {
                displayCardsWithPagination(cards, page - 1);
            } else if (!"b".equals(input)) {
                System.out.println("Invalid input. Showing current page.");
                displayCardsWithPagination(cards, page);
            }
        } else {
            System.out.println("End of cards. Enter 'p' for previous page, or 'b' to go back");
            scanner.nextLine(); // Consume newline
            String input = scanner.nextLine();
            if ("p".equals(input) && page > 0) {
                displayCardsWithPagination(cards, page - 1);
            } else if (!"b".equals(input)) {
                System.out.println("Invalid input. Going back.");
            }
        }
    }

    private void showCard() throws SQLException {
        System.out.println("Please enter the ID of the card you want to view");
        var selectedCardId = scanner.nextLong();
        try(var connection  = getConnection()){
            var serviceContainer = new ServiceContainer(connection);
            serviceContainer.getCardQueryService().findById(selectedCardId)
                    .ifPresentOrElse(
                            c -> {
                                System.out.printf("Card %s - %s.\n", c.id(), c.title());
                                System.out.printf("Description: %s\n", c.description());
                                System.out.printf("Priority: %s\n", c.priority());
                                if (c.dueDate() != null) {
                                    System.out.printf("Due Date: %s\n", c.dueDate().toString());
                                } else {
                                    System.out.println("Due Date: None");
                                }
                                System.out.println(c.blocked() ?
                                        "Is blocked. Reason: " + c.blockReason() :
                                        "Is not blocked");
                                System.out.printf("Has been blocked %s times\n", c.blocksAmount());
                                System.out.printf("Is currently in column %s - %s\n", c.columnId(), c.columnName());
                            },
                            () -> System.out.printf("No card exists with ID %s\n", selectedCardId));
        }
    }

}
