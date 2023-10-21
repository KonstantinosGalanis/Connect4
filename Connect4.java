package ce326.hw3;

import java.util.Scanner;
import javax.swing.*;
import java.io.ObjectInputStream;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.KeyAdapter;
import java.io.FileReader;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.awt.event.MouseAdapter;
import java.io.FilenameFilter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.awt.geom.Ellipse2D;
import java.io.FileWriter;
import org.json.*;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.util.Arrays;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.io.*;
import java.text.ParseException;

public class Connect4 {

    private static int difficultyLevel;
    private static int movesCount = 0;
    private static boolean firstPlayerIsHuman = false;

    private static int[][] gameBoard = new int [6][7];
    private static int[][][] gameMoves = new int [42][6][7];
    private static int gameStatus = 0;

    private static int ROWS = 6;

    private static int COLUMNS = 7;

    private static int currentPlayer = 0;

    private static int[] filledColumns = new int [7];

    private static CirclePanel[][] circlePanels = new CirclePanel[6][7];
    
    private static JPanel gamePanel = new JPanel(new GridLayout(6, 7, 5, 5));
    private static ArrayList<GameHistory> gameHistories = new ArrayList<>();
    private static boolean isExecuting = false;

    public static void main(String[] args) {
        String userHomeDirectory = System.getProperty("user.home");
        File connect4Directory = new File(userHomeDirectory, "connect4");
        if (!connect4Directory.exists()) {
            connect4Directory.mkdir();
        }
        JFrame frame = new JFrame("Connect4");

        JPanel menuPanel = new JPanel();


        // Create the menu buttons

        JButton newGameButton = new JButton("New Game");

        JButton firstPlayerButton = new JButton("1st Player");

        JButton historyButton = new JButton("History");

        JButton helpButton = new JButton("Help");

       

        JPopupMenu newGameMenu = new JPopupMenu();

        JMenuItem trivialMenuItem = new JMenuItem("Trivial");

        JMenuItem mediumMenuItem = new JMenuItem("Medium");

        JMenuItem hardMenuItem = new JMenuItem("Hard");

        newGameMenu.add(trivialMenuItem);

        newGameMenu.add(mediumMenuItem);

        newGameMenu.add(hardMenuItem);

       

        JPopupMenu firstPlayerMenu = new JPopupMenu();

        JRadioButtonMenuItem aiMenuItem = new JRadioButtonMenuItem("AI", true);

        JRadioButtonMenuItem youMenuItem = new JRadioButtonMenuItem("You");

        ButtonGroup firstPlayerGroup = new ButtonGroup();

        firstPlayerGroup.add(aiMenuItem);

        firstPlayerGroup.add(youMenuItem);

        firstPlayerMenu.add(aiMenuItem);

        firstPlayerMenu.add(youMenuItem);

      

        aiMenuItem.addActionListener(new ActionListener() {

            @Override

            public void actionPerformed(ActionEvent e) {

                firstPlayerIsHuman = false;

            }

        });

        youMenuItem.addActionListener(new ActionListener() {

            @Override

            public void actionPerformed(ActionEvent e) {

                firstPlayerIsHuman = true;

            }

        });


        // Add the "1st Player" menu to the menu bar

        firstPlayerButton.addActionListener(new ActionListener() {

           @Override

            public void actionPerformed(ActionEvent e) {

                firstPlayerMenu.show(firstPlayerButton, 0, firstPlayerButton.getHeight());

            }

        });

       

        // Add the action listeners to the menu items

        trivialMenuItem.addActionListener(new ActionListener() {

            @Override

            public void actionPerformed(ActionEvent e) {

                // Start a new game with "Trivial" difficulty

                startNewGame(1,firstPlayerIsHuman,gamePanel);

            }

        });

        mediumMenuItem.addActionListener(new ActionListener() {

            @Override

            public void actionPerformed(ActionEvent e) {

                // Start a new game with "Medium" difficulty

                startNewGame(3,firstPlayerIsHuman,gamePanel);

            }

        });

        hardMenuItem.addActionListener(new ActionListener() {

            @Override

            public void actionPerformed(ActionEvent e) {

                // Start a new game with "Hard" difficulty

               startNewGame(5,firstPlayerIsHuman,gamePanel);

            }

        });


        // Add the "New Game" menu to the menu bar

       newGameButton.addActionListener(new ActionListener() {

            @Override

            public void actionPerformed(ActionEvent e) {

                newGameMenu.show(newGameButton, 0, newGameButton.getHeight());

            }

        });

       

       historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame historyFrame = new JFrame("Game History");
                historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                historyFrame.setSize(400, 400);

                DefaultListModel<String> historyListModel = new DefaultListModel<>();
                
                // Retrieve and display the list of completed games
                File connect4Dir = new File("C:/Users/Kostas/connect4");
                if (connect4Dir.isDirectory()) {
                    File[] gameFiles = connect4Dir.listFiles();
                    Arrays.sort(gameFiles, Comparator.comparing(File::lastModified).reversed()); // Sort files by last modified time, newest first
                    for (File gameFile : gameFiles) {
                        if (!gameFile.isDirectory() && gameFile.getName().endsWith(".json")) {
                            try (FileReader reader = new FileReader(gameFile)) {
                                JSONObject game = new JSONObject(new JSONTokener(reader));
                                String fileName = gameFile.getName();
                                fileName = fileName.replaceAll(".json", "");
                                fileName = fileName.replaceAll("_", ":");
                                String[] tokens = fileName.split(" ");
                                String dateString = fileName.substring(0, fileName.indexOf(" L:"));
                                String levelString = tokens[6]; // remove the "L:" prefix
                                String winnerString = tokens[4]; // remove the "W:" prefix
                                JSONArray movesInfos = game.getJSONArray("board");
                                String movesInfosString = movesInfos.toString();
                                String movesCount = game.getString("movesCount");
                                String infoString = movesInfosString + " C: " + movesCount;
                                String gameString = dateString + " L: " + levelString + " W: " + winnerString;
                                historyListModel.addElement(gameString + "|" + infoString);
                            } catch (IOException | JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }

                JList<String> historyList = new JList<>(historyListModel);
                historyList.setCellRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        String[] parts = value.toString().split("\\|");
                        setText(parts[0]);
                        return this;
                    }
                });

                JScrollPane scrollPane = new JScrollPane(historyList);
                historyFrame.add(scrollPane);
                

                // Add a double-click listener to the history list to replay selected game
                historyList.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent evt) {
                        playAgain(gamePanel);
                        if (evt.getClickCount() == 2) {
                            String selectedGameString = historyList.getSelectedValue();
                            if (selectedGameString != null) {
                                int pipeIndex = selectedGameString.indexOf('|');
                                String gameString = selectedGameString.substring(0, pipeIndex);
                                String movesInfoString = selectedGameString.substring(pipeIndex + 1);
                                int movesCount = Integer.parseInt(movesInfoString.substring(movesInfoString.indexOf("C: ") + 3));
                                String board = movesInfoString.substring(0, movesInfoString.indexOf(" C: "));
                                final int[][][] movesInfo = stringToIntArray(board);

                                // Extract the level and winner information from the selected game string
                                String[] tokens = gameString.split(" ");
                                String levelString = tokens[4];
                                String winnerString = tokens[6];
                                int level = levelString.equals("Medium") ? 3 : levelString.equals("Hard") ? 5 : 1;
                                boolean firstPlayerIsHuman = !winnerString.equals("AI");
                                
                                // Define a timer with a 3 second delay
                                Timer timer = new Timer(3000, new ActionListener() {
                                    int moveIndex = 0;
                                    int row = 0;
                                    int column = 0;
                                    int player = 0;
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        if (moveIndex < movesCount) {
                                            for (int j = 0; j < 6; j++) {
                                                for (int k = 0; k < 7; k++) {
                                                    if(movesInfo[moveIndex][j][k] == 1) {
                                                        row = j;
                                                        column = k;
                                                        player = 1;
                                                        break;
                                                    }
                                                    else if(movesInfo[moveIndex][j][k] == 2) {
                                                        row = j;
                                                        column = k;
                                                        player = 2;
                                                        break;
                                                    }
                                                }
                                            }
                                            repaint(null, column, row, player);
                                            moveIndex++;
                                        } else {
                                            ((Timer) e.getSource()).stop(); // Stop the timer after all moves have been replayed
                                        }
                                    }
                                });
                                timer.start(); // Start the timer

                                historyFrame.dispose(); // Close the historyFrame
                            }
                        }
                    }
                });

                historyFrame.setVisible(true);
            }
        });

        // Customize the menu buttons

        Dimension buttonSize = new Dimension(100, 30);

        newGameButton.setPreferredSize(buttonSize);

        firstPlayerButton.setPreferredSize(buttonSize);

        historyButton.setPreferredSize(buttonSize);

        helpButton.setPreferredSize(buttonSize);

       

        // Add the menu buttons to the menu panel

        menuPanel.add(newGameButton);

        menuPanel.add(firstPlayerButton);

        menuPanel.add(historyButton);

        menuPanel.add(helpButton);

       

        // Customize the menu panel

        menuPanel.setBackground(new Color(240, 240, 240));

        menuPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        menuPanel.setPreferredSize(new Dimension(250, 40)); // Set height to 40 pixels

       

        for (int i = 0; i < ROWS; i++) {          

            for (int j = 0; j < COLUMNS; j++) {

                CirclePanel circlePanel = new CirclePanel();

                circlePanel.setPreferredSize(new Dimension(55, 55));

                circlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                circlePanels[i][j] = circlePanel;

                gamePanel.add(circlePanel);

            }

        }

       

        // Add the menu panel to the top of the frame

        frame.getContentPane().add(menuPanel, BorderLayout.NORTH);

       

        // Customize the game panel

        gamePanel.setBackground(new Color(240, 240, 240));

        gamePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

       

        // Add the game panel to the center of the frame

        frame.getContentPane().add(gamePanel, BorderLayout.CENTER);

       

        // Display the frame

        frame.pack();

        frame.setVisible(true);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
    
    public static int[][][] stringToIntArray(String str) {
        str = str.replaceAll("\\[|\\]", "");
        str = str.replaceAll(" ", "");
        String[] values = str.split(",");
        int[][][] arr = new int[42][6][7];
        int index = 0;
        for (int i = 0; i < 42; i++) {
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 7; k++) {
                    arr[i][j][k] = Integer.parseInt(values[index]);
                    index++;
                }
            }
        }
        return arr;
    }
    
    private static class GameHistory {
        private int[][][] movesInfo;
        private int difficultyLevel;
        private int winner;
        private int moves;
        private Date date;

        public GameHistory(int[][][] movesInfo, int difficultyLevel, int winner, int moves) {
            this.movesInfo = movesInfo;
            this.difficultyLevel = difficultyLevel;
            this.winner = winner;
            this.moves = moves;
            this.date = new Date();
        }

        public int[][][] movesInfo() {
            return movesInfo;
        }

        public String getDifficultyLevel() {
            return Integer.toString(difficultyLevel);
        }

        public String getWinner() {
            if (winner == 1) {
                return "P";
            } else if (winner == 2) {
                return "AI";
            } else {
                return "Tie";
            }
        }
        
        public String getDate() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd - HH_mm");
            return formatter.format(date);
        }
        
        public String getMoves() {
            return Integer.toString(moves);
        }

    }

    public static void addGameHistory(int[][][] movesInfo, int difficultyLevel, int winner, int moves) {
        GameHistory history = new GameHistory(movesInfo, difficultyLevel, winner, moves);
        gameHistories.add(history);
        
        try {
            String userHomeDir = System.getProperty("user.home");
            String logDirPath = userHomeDir + File.separator + "connect4";
            File logDir = new File(logDirPath);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            String identifier = history.getDate() + " L_ " + history.getWinner() + " W_ " + history.getDifficultyLevel();
            String logFilePath = logDirPath + File.separator + identifier + ".json";
            JSONObject json = new JSONObject();
            json.put("movesCount", history.getMoves());
            json.put("board", history.movesInfo());
            FileWriter writer = new FileWriter(logFilePath, true);
            writer.write(json.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static class CirclePanel extends JPanel {

        private Color circleColor = Color.WHITE;


        @Override

        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


            // Draw black circle border

            Shape border = new Ellipse2D.Float(1, 1, getWidth() - 3, getHeight() - 3);

            g2.setColor(Color.BLACK);

            g2.draw(border);


            // Draw white circle

            Shape shape = new Ellipse2D.Float(2, 2, getWidth() - 5, getHeight() - 5);

            g2.setColor(circleColor);

            g2.fill(shape);


            g2.dispose();

        }


        public void setCircleColor(Color color) {

            this.circleColor = color;

            repaint();

        }

    }


    public static void playAgain(JPanel gamePanel) {

        for (int i = 0; i < ROWS; i++) {

            for (int j = 0; j < COLUMNS; j++) {

                CirclePanel circlePanel = circlePanels[i][j];

                circlePanel.setCircleColor(Color.WHITE); // Reset the color of the CirclePanel

            }

        }

    }

   

    public static void startNewGame(int difficultyLevel, boolean firstPlayerIsHuman,JPanel gamePanel) {
        playAgain(gamePanel);
        gameStatus = 0;
        movesCount = 0;
        for (int i = 0; i < ROWS; i++) {        

            for (int j = 0; j < COLUMNS; j++) {

                gameBoard[i][j] = 0;
                
                filledColumns[j] = 0;

            }

        }
        for(int k = 0; k < 42; k++) {
            for (int i = 0; i < ROWS; i++) {        
                for (int j = 0; j < COLUMNS; j++) {
                    gameMoves[k][i][j] = 0;
                }
            }
        }

        if(firstPlayerIsHuman) {

            currentPlayer = 1;

        }

        else {

            currentPlayer = 2;

            makeComputerMove(gamePanel,difficultyLevel);

        }

       

        // Add the mouse listener to the game panel

        gamePanel.addMouseListener(new MouseAdapter() {

            @Override

            public void mouseClicked(MouseEvent e) {

                if(e.getClickCount() == 2) {

                    if (gameStatus != 0) {

                        return;

                    }


                    int column = getColumnFromMousePosition(e,gamePanel);

                   

                    if (column >= 0 && column < 7 && filledColumns[column] < 6) {

                        int row = 5 - filledColumns[column];

                        gameBoard[row][column] = 1;
                        gameMoves[movesCount][row][column] = 1;
                        movesCount++;
                        gameStatus = checkGameStatus();

                        repaint(gamePanel,column,row,1);

                        if(gameStatus!=0) {

                            displayModalBox(currentPlayer == 1 ? "You won!" : "You lost!");
                            addGameHistory(gameMoves, difficultyLevel, gameStatus, movesCount);

                            playAgain(gamePanel);
                            
                            return;

                        }

                        currentPlayer = 2;

                        filledColumns[column]++;

                        if (gameStatus == 0 && currentPlayer == 2) {

                            makeComputerMove(gamePanel,difficultyLevel);

                        }

                    }

                }

            }

        });


        // Add the key listener to the game panel

        gamePanel.setFocusable(true);

        gamePanel.requestFocusInWindow();

        gamePanel.addKeyListener(new KeyAdapter() {

            @Override

            public void keyPressed(KeyEvent e) {
                if (isExecuting) {
                    return; // Method is already being executed, do nothing
                }
                
                
                if (gameStatus != 0) {

                    return;

                }


                int column = -1;


                switch (e.getKeyCode()) {

                    case KeyEvent.VK_0:

                    case KeyEvent.VK_NUMPAD0:

                        column = 0;

                        break;

                    case KeyEvent.VK_1:

                    case KeyEvent.VK_NUMPAD1:

                        column = 1;

                        break;

                    case KeyEvent.VK_2:

                    case KeyEvent.VK_NUMPAD2:

                        column = 2;

                        break;

                    case KeyEvent.VK_3:

                    case KeyEvent.VK_NUMPAD3:

                        column = 3;

                        break;

                    case KeyEvent.VK_4:

                    case KeyEvent.VK_NUMPAD4:

                        column = 4;

                        break;

                    case KeyEvent.VK_5:

                    case KeyEvent.VK_NUMPAD5:

                        column = 5;

                        break;

                    case KeyEvent.VK_6:

                    case KeyEvent.VK_NUMPAD6:

                        column = 6;

                        break;

                }

                if (column >= 0 && column < 7 && filledColumns[column] < 6) {

                    int row = 5 - filledColumns[column];
                    
                    filledColumns[column]++;

                    gameBoard[row][column] = 1;
                    gameMoves[movesCount][row][column] = 1;
                    movesCount++;
                    gameStatus = checkGameStatus();

                    repaint(gamePanel,column,row,1);

                    if(gameStatus!=0) {

                        displayModalBox(currentPlayer == 1 ? "You won!" : "You lost!");
                        addGameHistory(gameMoves, difficultyLevel, gameStatus, movesCount);

                        playAgain(gamePanel);
                        
                        return;

                    }

                    currentPlayer = 2;

                    

                    if (gameStatus == 0 && currentPlayer == 2) {

                        makeComputerMove(gamePanel,difficultyLevel);

                    }

                }

            }

        });
    }


    public static int getColumnFromMousePosition(MouseEvent e,JPanel gamePanel) {

        int mouseX = e.getX();

        int panelWidth = gamePanel.getWidth();

        int columnWidth = panelWidth / 7;

        int column = mouseX / columnWidth;

        return column;

    }

   private static int[][] copyGameBoard(int[][] board) {
        int[][] copy = new int[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                copy[i][j] = board[i][j];
            }
        }
        return copy;
    }


    public static void makeComputerMove(JPanel gamePanel, int depth) {
        if (isExecuting) {
            return; // Method is already being executed, do nothing
        }

        isExecuting = true;
        
        final int[] filledColumnsCopy = Arrays.copyOf(filledColumns, filledColumns.length);
        final int[][] gameBoardCopy = copyGameBoard(gameBoard);
        final int currentPlayerCopy = currentPlayer;
        Timer timer = null; 

        timer = new Timer(700, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Use filledColumnsCopy, gameBoardCopy, currentPlayerCopy here
                // instead of filledColumns, gameBoard, currentPlayer

                int bestColumn = -1;

                int bestScore = Integer.MIN_VALUE;

                // Evaluate each potential move using alpha-beta pruning

                for (int column = 0; column < COLUMNS; column++) {
                    if (filledColumnsCopy[column] >= 6) {
                        continue; // column is full, can't play here
                    }

                    int row = 5 - filledColumns[column];

                    gameBoard[row][column] = 2;

                    filledColumns[column]++;

                    int score = alphaBetaMinimax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                    filledColumns[column]--;

                    gameBoard[row][column] = 0;
                    
                    // Update the best move if this one is better
                    if (score > bestScore || (score == bestScore && Math.abs(column - 3) < Math.abs(bestColumn - 3))) {
                        bestColumn = column;
                        bestScore = score;
                    }
                }

                // Make the best move
                int row = 5 - filledColumnsCopy[bestColumn];
                gameBoardCopy[row][bestColumn] = 2;
                gameBoard[row][bestColumn] = gameBoardCopy[row][bestColumn];
                gameMoves[movesCount][row][bestColumn] = 2;
                movesCount++;
                gameStatus = checkGameStatus();
                repaint(gamePanel, bestColumn, row, 2);
                if (gameStatus != 0) {
                    displayModalBox(currentPlayerCopy == 1 ? "You won!" : "You lost!");
                    addGameHistory(gameMoves, depth, gameStatus, movesCount);
                    playAgain(gamePanel);
                }
                filledColumns[bestColumn] = filledColumnsCopy[bestColumn] + 1;
                currentPlayer = 1;
                isExecuting = false;
            }
        });
        timer.setRepeats(false); // Only execute the ActionListener once
        timer.start(); // Start the timer to delay the execution of makeComputerMove()
    }

   

    public static int alphaBetaMinimax(int depth, int alpha, int beta, boolean maximizingPlayer) {

        int score = evaluatePosition();


        // Base case: either the maximum depth has been reached or the game has ended

        if (depth == 0 || gameStatus != 0) {

            return score;

        }


        if (maximizingPlayer) {

            int maxScore = Integer.MIN_VALUE;


            for (int column = 0; column < COLUMNS; column++) {

                if (filledColumns[column] >= 6) {

                    continue;

                }



                int row = 5 - filledColumns[column];

                gameBoard[row][column] = 1;

                filledColumns[column]++;


                int currentScore = alphaBetaMinimax(depth - 1, alpha, beta, false);

                maxScore = Math.max(maxScore, currentScore);


                // Undo the move

                filledColumns[column]--;

                gameBoard[row][column] = 0;


                // Alpha-beta pruning

                alpha = Math.max(alpha, currentScore);

                if (beta <= alpha) {

                    break;

                }

            }


            return maxScore;

        } else {

            int minScore = Integer.MAX_VALUE;


            for (int column = 0; column < COLUMNS; column++) {

                if (filledColumns[column] >= 6) {

                    continue;

                }


                // Simulate placing the user's checker in this column

                int row = 5 - filledColumns[column];

                gameBoard[row][column] = 1;

                filledColumns[column]++;


                int currentScore = alphaBetaMinimax(depth - 1, alpha, beta, true);

                minScore = Math.min(minScore, currentScore);


                // Undo the move

                filledColumns[column]--;

                gameBoard[row][column] = 0;


                // Alpha-beta pruning

                beta = Math.min(beta, currentScore);

                if (beta <= alpha) {

                    break;

                }

            }


            return minScore;

        }

    }

   

    private static int evaluatePosition() {

        int score = 0;


        // Evaluate potential horizontal quads

        for (int row = 0; row < ROWS; row++) {

            for (int col = 0; col <= COLUMNS - 4; col++) {

                int[] quad = new int[4];

                for (int i = 0; i < 4; i++) {

                    quad[i] = gameBoard[row][col+i];

                }

                score += evaluateQuad(quad);

            }

        }


        // Evaluate potential vertical quads

        for (int col = 0; col < COLUMNS; col++) {

            for (int row = 0; row <= ROWS - 4; row++) {

                int[] quad = new int[4];

                for (int i = 0; i < 4; i++) {

                    quad[i] = gameBoard[row+i][col];

                }

                score += evaluateQuad(quad);

            }

        }


        // Evaluate potential upper diagonal quads

        for (int row = 0; row <= ROWS - 4; row++) {

            for (int col = 0; col <= COLUMNS - 4; col++) {

                int[] quad = new int[4];

                for (int i = 0; i < 4; i++) {

                    quad[i] = gameBoard[row+i][col+i];

                }

                score += evaluateQuad(quad);

            }

        }


        // Evaluate potential lower diagonal quads

        for (int row = 3; row < ROWS; row++) {

            for (int col = 0; col <= COLUMNS - 4; col++) {

                int[] quad = new int[4];

                for (int i = 0; i < 4; i++) {

                    quad[i] = gameBoard[row-i][col+i];

                }

                score += evaluateQuad(quad);

            }

        }


        return score;

    }

   

    private static int evaluateQuad(int[] quad) {

        int aiCount = 0;

        int userCount = 0;

        int blankCount = 0;


        for (int i = 0; i < 4; i++) {

            if (quad[i] == 1) {

                userCount++;

            } else if (quad[i] == 2) {

                aiCount++;

            } else {

                blankCount++;

            }

        }


        if (aiCount == 4) {

            return 10000;

        } else if (userCount == 4) {

            return -10000;

        } else if (blankCount == 4) {

            return 0;

        } else if (aiCount == 3 && blankCount == 1) {

            return 16;

        } else if (aiCount == 2 && blankCount == 2) {

            return 4;

        } else if (aiCount == 1 && blankCount == 3) {

            return 1;

        } else if (userCount == 3 && blankCount == 1) {

            return -16;

        } else if (userCount == 2 && blankCount == 2) {

            return -4;

        } else if (userCount == 1 && blankCount == 3) {

            return -1;

        } else {

            return 0;

        }

    }

   

    public static int checkGameStatus() {

        // Check for a win in any row

        for (int i = 0; i < ROWS; i++) {

            for (int j = 0; j < COLUMNS - 3; j++) {

                if (gameBoard[i][j] == currentPlayer && gameBoard[i][j+1] == currentPlayer

                    && gameBoard[i][j+2] == currentPlayer && gameBoard[i][j+3] == currentPlayer) {

                    return currentPlayer;

                }

            }

        }


        // Check for a win in any column

        for (int j = 0; j < COLUMNS; j++) {

            for (int i = 0; i < ROWS - 3; i++) {

                if (gameBoard[i][j] == currentPlayer && gameBoard[i+1][j] == currentPlayer

                    && gameBoard[i+2][j] == currentPlayer && gameBoard[i+3][j] == currentPlayer) {

                    return currentPlayer;

                }

            }

        }


        // Check for a win on any diagonal (both forward and backward)

        for (int i = 0; i < ROWS - 3; i++) {

            for (int j = 0; j < COLUMNS - 3; j++) {

                if (gameBoard[i][j] == currentPlayer && gameBoard[i+1][j+1] == currentPlayer

                    && gameBoard[i+2][j+2] == currentPlayer && gameBoard[i+3][j+3] == currentPlayer) {

                    return currentPlayer;

                }

            }

        }


        for (int i = ROWS - 1; i >= 3; i--) {

            for (int j = 0; j < COLUMNS - 3; j++) {

                if (gameBoard[i][j] == currentPlayer && gameBoard[i-1][j+1] == currentPlayer

                    && gameBoard[i-2][j+2] == currentPlayer && gameBoard[i-3][j+3] == currentPlayer) {

                    return currentPlayer;

                }

            }

        }


        // Check for a tie game

        boolean isTie = true;

        for (int i = 0; i < ROWS; i++) {

            for (int j = 0; j < COLUMNS; j++) {

                if (gameBoard[i][j] == 0) {

                    isTie = false;

                    break;

                }

            }

            if (!isTie) {

                break;

            }

        }

        if (isTie) {

            return 3;

        }


        // If no win or tie, return 0 to indicate the game is still ongoing

        return 0;

    }

   

    public static void displayModalBox(String message) {

        JFrame frame = new JFrame();

        JOptionPane.showMessageDialog(frame, message);

    }

   

    public static void repaint(JPanel gamePanel, int column, int row, int player) {

        CirclePanel lastCirclePanel = circlePanels[row][column];

        int delay = 100; // Delay in milliseconds

        Timer[] timer = {null}; // Initialize the timer variable to null

        timer[0] = new Timer(delay, new ActionListener() {

            int count = 0;


            @Override

            public void actionPerformed(ActionEvent e) {

                CirclePanel circlePanel = circlePanels[count][column];

                if (player == 1) {

                    circlePanel.setCircleColor(Color.RED);

                } else if (player == 2) {

                    circlePanel.setCircleColor(Color.YELLOW);

                }

                if (count > 0) {

                    CirclePanel prevCirclePanel = circlePanels[count - 1][column];

                    prevCirclePanel.setCircleColor(Color.WHITE);

                }

                count++;

                if (count > row ) {

                    timer[0].stop();

                    if (player == 1) {

                        lastCirclePanel.setCircleColor(Color.RED);

                    } else if (player == 2) {

                        lastCirclePanel.setCircleColor(Color.YELLOW);

                    }

                }

            }

        });

        timer[0].start();

    }

}
