package br.com.dio.ui;

import br.com.dio.config.ServiceContainer;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnKindEnum;
import br.com.dio.persistence.entity.BoardEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.CANCEL;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.FINAL;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.INITIAL;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.PENDING;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    public void execute() throws SQLException {
        System.out.println("Welcome to the board manager, please choose an option");
        var option = -1;
        while (true){
            System.out.println("1 - Create a new board");
            System.out.println("2 - Select an existing board");
            System.out.println("3 - Delete a board");
            System.out.println("4 - Exit");
            option = scanner.nextInt();
            switch (option){
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Invalid option, please select an option from the menu");
            }
        }
    }

    private void createBoard() throws SQLException {
        var entity = new BoardEntity();
        System.out.println("Please enter the name of your board");
        entity.setName(scanner.next());

        System.out.println("Will your board have columns in addition to the 3 standard ones? If yes, enter how many, otherwise type '0'");
        var additionalColumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Please enter the name of the initial board column");
        var initialColumnName = scanner.next();
        var initialColumn = createColumn(initialColumnName, INITIAL, 0);
        columns.add(initialColumn);

        for (int i = 0; i < additionalColumns; i++) {
            System.out.println("Please enter the name of the pending task column for the board");
            var pendingColumnName = scanner.next();
            var pendingColumn = createColumn(pendingColumnName, PENDING, i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("Please enter the name of the final column");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, FINAL, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Please enter the name of the cancellation column for the board");
        var cancelColumnName = scanner.next();
        var cancelColumn = createColumn(cancelColumnName, CANCEL, additionalColumns + 2);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);
        try(var connection = getConnection()){
            var serviceContainer = new ServiceContainer(connection);
            serviceContainer.getBoardService().insert(entity);
        }

    }

    private void selectBoard() throws SQLException {
        System.out.println("Please enter the ID of the board you want to select");
        var id = scanner.nextLong();
        try(var connection = getConnection()){
            var serviceContainer = new ServiceContainer(connection);
            var optional = serviceContainer.getBoardQueryService().findById(id);
            optional.ifPresentOrElse(
                    b -> new BoardMenu(b).execute(),
                    () -> System.out.printf("No board was found with ID %s\n", id)
            );
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("Please enter the ID of the board to be deleted");
        var id = scanner.nextLong();
        try(var connection = getConnection()){
            var serviceContainer = new ServiceContainer(connection);
            if (serviceContainer.getBoardService().delete(id)){
                System.out.printf("Board %s has been deleted\n", id);
            } else {
                System.out.printf("No board was found with ID %s\n", id);
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order){
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }

}
