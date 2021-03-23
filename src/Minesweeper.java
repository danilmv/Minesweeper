import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.function.Function;

public class Minesweeper extends JFrame {
    private final int WINDOW_WIDTH = 640;
    private final int WINDOW_HEIGHT = 480;

    private Timer timer;
    private long startTimer;

    private Field panelField;

    private final int NUMBER_OF_MINES_NEWBIEW = 10;
    private final int SIZE_HORIZONTAL_NEWBIE = 9;
    private final int SIZE_VERTICAL_NEWBIE = 9;

    private final int NUMBER_OF_MINES_AVERAGE = 40;
    private final int SIZE_HORIZONTAL_AVERAGE = 16;
    private final int SIZE_VERTICAL_AVERAGE = 16;


    private JLabel labelNumberOfMinesLeft;
    private JLabel labelTimer;
    private int numberOfMinesStart = NUMBER_OF_MINES_AVERAGE;

    private int numberOfMinesLeft;
    int numFlag = 0;
    int numShown = 0;


    private int sizeHorizontal = SIZE_HORIZONTAL_AVERAGE;
    private int sizeVertical = SIZE_VERTICAL_AVERAGE;

    private Cell[][] field;

    private Random random = new Random();

    private final int GAME_MODE_INIT = 0;
    private final int GAME_MODE_READY = 1;
    private final int GAME_MODE_STARTED = 2;
    private final int GAME_MODE_FINISHED = 3;
    private int gameMode = GAME_MODE_INIT;

    private int cellWidth;
    private int cellHeight;
    private int screenWidth;
    private int screenHeight;

    private int blowX = -1;
    private int blowY = -1;

    private Settings settings = new Settings(this);

    public static void main(String[] args) {
        new Minesweeper();
    }


    public Minesweeper() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        add(createTopPanel(), BorderLayout.NORTH);
        add(createFieldPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

//        setResizable(false);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                resized();
            }
        });

        setVisible(true);
    }


    private JPanel createTopPanel() {
        JPanel panelTopButtons = new JPanel();

        JButton buttonNew = new JButton("Новая игра");
        buttonNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame(sizeHorizontal, sizeVertical, numberOfMinesStart);
            }
        });

        JButton buttonSettings = new JButton("Настройки");
        buttonSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settings.showWindow();
            }
        });
        panelTopButtons.add(buttonNew);
        panelTopButtons.add(buttonSettings);

        JButton buttonHelp = new JButton("Сделать ход");
        buttonHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelField.makeMovePC();
            }
        });

        panelTopButtons.add(buttonHelp);

        return panelTopButtons;
    }

    private Field createFieldPanel() {
        panelField = new Field();
        return panelField;
    }

    private JPanel createBottomPanel() {
        JPanel panelBottom = new JPanel();
        panelBottom.setLayout(new GridLayout(1, 2));

        labelTimer = new JLabel("время:");
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                labelTimer.setText("время:" + (int) ((System.currentTimeMillis() - startTimer) * 0.001f));
            }
        });
        panelBottom.add(labelTimer, BorderLayout.EAST);

        labelNumberOfMinesLeft = new JLabel("Мин осталось:" + numberOfMinesLeft);
        panelBottom.add(labelNumberOfMinesLeft, BorderLayout.WEST);

        return panelBottom;
    }

    void startGame(int sizeHorizontal, int sizeVertical, int numberOfMines) {

        this.sizeHorizontal = sizeHorizontal;
        this.sizeVertical = sizeVertical;
        this.numberOfMinesLeft = this.numberOfMinesStart = numberOfMines;

        resized();
        panelField.resized();

        gameMode = GAME_MODE_READY;

        blowX = blowY = -1;

        numFlag = 0;
        numShown = 0;

        labelNumberOfMinesLeft.setText("Мин осталось:" + numberOfMinesLeft);
        timer.stop();
        labelTimer.setText("время:" + 0);

        field = new Cell[sizeHorizontal][sizeVertical];
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                field[i][j] = new Cell();
            }
        }

        repaint();
    }

    private void resized() {
        screenWidth = panelField.getWidth();
        screenHeight = panelField.getHeight();

        cellWidth = screenWidth / sizeHorizontal;
        cellHeight = screenHeight / sizeVertical;
        if (cellHeight > cellWidth)
            cellHeight = cellWidth;
        else
            cellWidth = cellHeight;

    }

    private void gameOver(boolean win) {
        gameMode = GAME_MODE_FINISHED;

        timer.stop();

        if (!win) {
            for (int x = 0; x < sizeHorizontal; x++) {
                for (int y = 0; y < sizeVertical; y++) {
                    if (field[x][y].isMined())
                        field[x][y].show();
                }
            }
        } else {

        }
    }

    private void setField(int firstX, int firstY) {
        final int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        final int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};

        for (int i = 0; i < numberOfMinesStart; i++) {
            do {
                int place = random.nextInt(sizeHorizontal * sizeVertical);
                int x = place / sizeVertical;
                int y = place % sizeVertical;

                if (x == firstX && y == firstY)
                    continue;

                if (field[x][y].isMined())
                    continue;

                field[x][y].setMined(true);
                break;

            } while (true);
        }

        for (int x = 0; x < sizeHorizontal; x++) {
            for (int y = 0; y < sizeVertical; y++) {
                if (field[x][y].isMined())
                    continue;
                for (int i = 0; i < dx.length; i++) {
                    if (checkField(x + dx[i], y + dy[i]))
                        field[x][y].increaseValue();
                }
            }
        }
    }

    private boolean checkField(int x, int y) {
        if (x < 0 || x >= sizeHorizontal)
            return false;
        if (y < 0 || y >= sizeVertical)
            return false;

        if (field[x][y].isMined())
            return true;

        return false;
    }

    class Field extends JPanel {
        private final Color COLOR_BACKGROUND = Color.DARK_GRAY;
        private final Color COLOR_LINES = Color.BLACK;

        private final Color[] COLOR_VALUE = {
                Color.GRAY,
                new Color(0, 0, 255),
                new Color(0, 128, 0),
                new Color(255, 0, 0),
                new Color(0, 0, 128),
                new Color(128, 0, 0),
                new Color(0, 128, 128),
                new Color(0, 0, 0),
                new Color(228, 128, 128)
        };
        private final Color COLOR_WRONG = Color.ORANGE;

        private Font fontValue;

        public Field() {
            setBackground(COLOR_BACKGROUND);
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);

                    if (gameMode == GAME_MODE_FINISHED) {
                        repaint();
                        return;
                    }

                    int x = e.getX() / cellWidth;
                    int y = e.getY() / cellHeight;

                    if (x >= sizeHorizontal || y >= sizeVertical) return;

                    if (gameMode == GAME_MODE_READY) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            setField(x, y);
                            resized();

                            startTimer = System.currentTimeMillis();
                            timer.start();

                            gameMode = GAME_MODE_STARTED;
                        }
                    }

                    makeMove(x, y, e.getButton());
//                    if (e.getButton() == MouseEvent.BUTTON1 && field[x][y].getFlagStatus() != Cell.FLAG_STATUS_SET) {
//                        int result = field[x][y].show();
//                        if (field[x][y].isMined()) {
//                            blowX = x;
//                            blowY = y;
//
//                            gameOver(false);
//                        } else {
//                            numShown++;
//                            if (result == 0)
//                                showNeighbours(x, y);
//                        }
//
//                    } else if (e.getButton() == MouseEvent.BUTTON3) {
//                        if (field[x][y].isHidden()) {
//                            field[x][y].nextFlagStatus();
//                        }
//                    } else if (e.getButton() == MouseEvent.BUTTON2) {
//                        if (field[x][y].getValue() > 0)
//                            showNeighbours(x, y);
//                    }
//
//                    labelNumberOfMinesLeft.setText("" + numberOfMinesLeft);
//
//                    if (numShown + numberOfMinesStart == sizeVertical * sizeHorizontal)
//                        gameOver(true);
//
//                    repaint();
                }
            });
        }

        private void makeMove(int x, int y, int mouseButton) {
            if (mouseButton == MouseEvent.BUTTON1 && field[x][y].getFlagStatus() != Cell.FLAG_STATUS_SET) {
                int result = field[x][y].show();
                if (field[x][y].isMined()) {
                    blowX = x;
                    blowY = y;

                    gameOver(false);
                } else {
                    numShown++;
                    if (result == 0)
                        showNeighbours(x, y);
                }
            } else if (mouseButton == MouseEvent.BUTTON3) {
                if (field[x][y].isHidden()) {
                    field[x][y].nextFlagStatus();
                }
            } else if (mouseButton == MouseEvent.BUTTON2) {
                if (field[x][y].getValue() > 0)
                    showNeighbours(x, y);
            }

            labelNumberOfMinesLeft.setText("Мин осталось:" + numberOfMinesLeft);

            if (numShown + numberOfMinesStart == sizeVertical * sizeHorizontal)
                gameOver(true);

            repaint();
        }

        public void makeMovePC() {
            final int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
            final int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};
            int x0, y0;

            if (gameMode != GAME_MODE_STARTED)
                return;

            for (int x = 0; x < sizeHorizontal; x++) {
                for (int y = 0; y < sizeVertical; y++) {
                    field[x][y].neighbourFlags = field[x][y].neighbourHidden = 0;

                    if (field[x][y].isHidden())
                        continue;

                    for (int i = 0; i < dx.length; i++) {
                        x0 = x + dx[i];
                        y0 = y + dy[i];

                        if (x0 < 0 || x0 >= sizeHorizontal || y0 < 0 || y0 >= sizeVertical)
                            continue;

                        if (field[x0][y0].getFlagStatus() == Cell.FLAG_STATUS_SET) {
                            field[x][y].neighbourFlags++;
                            continue;
                        }

                        if (field[x0][y0].isHidden())
                            field[x][y].neighbourHidden++;
                        else
                            continue;
                    }
                    if (field[x][y].neighbourHidden <= 0)
                        continue;

                    for (int i = 0; i < dx.length; i++) {
                        x0 = x + dx[i];
                        y0 = y + dy[i];

                        if (x0 < 0 || x0 >= sizeHorizontal || y0 < 0 || y0 >= sizeVertical)
                            continue;
                        if (!field[x0][y0].isHidden())
                            continue;
                        if (field[x0][y0].getFlagStatus() == Cell.FLAG_STATUS_SET)
                            continue;


                        if (field[x][y].getValue() <= field[x][y].neighbourFlags) {
                            makeMove(x0, y0, MouseEvent.BUTTON1);
                            return;
                        }

                        if (field[x][y].getValue() - field[x][y].neighbourFlags >= field[x][y].neighbourHidden) {
                            makeMove(x0, y0, MouseEvent.BUTTON3);
                            return;
                        }
                    }
                }
            }
        }

        private void resized() {
            if (cellHeight != 0)
                fontValue = new Font("Times New Roman", Font.BOLD, cellHeight - 2);
        }

        private void showNeighbours(int x, int y) {
            final int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
            final int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};

            int x0, y0;
            int result;
            int value = field[x][y].getValue();
            int numFlags = 0;

            if (value == 0) {
                for (int i = 0; i < dx.length; i++) {
                    x0 = x + dx[i];
                    y0 = y + dy[i];

                    if (x0 < 0 || x0 >= sizeHorizontal)
                        continue;
                    if (y0 < 0 || y0 >= sizeVertical)
                        continue;
                    if (!field[x0][y0].isHidden())
                        continue;
                    result = field[x0][y0].show();
                    if (result == 0)
                        showNeighbours(x0, y0);
                    if (result >= 0)
                        numShown++;
                }
            } else {
                if (value > 0 && !field[x][y].isHidden()) {
                    for (int i = 0; i < dx.length; i++) {
                        x0 = x + dx[i];
                        y0 = y + dy[i];

                        if (x0 < 0 || x0 >= sizeHorizontal)
                            continue;
                        if (y0 < 0 || y0 >= sizeVertical)
                            continue;

                        if (field[x0][y0].getFlagStatus() == Cell.FLAG_STATUS_SET)
                            numFlags++;
                    }
                }
                if (numFlags == value) {
                    for (int i = 0; i < dx.length; i++) {
                        x0 = x + dx[i];
                        y0 = y + dy[i];

                        if (x0 < 0 || x0 >= sizeHorizontal)
                            continue;
                        if (y0 < 0 || y0 >= sizeVertical)
                            continue;

                        if (field[x0][y0].isHidden() && field[x0][y0].getFlagStatus() != Cell.FLAG_STATUS_SET) {
                            if (field[x0][y0].isMined()) {
                                blowX = x0;
                                blowY = y0;
                                gameOver(false);
                            }

                            result = field[x0][y0].show();
                            if (result == 0)
                                showNeighbours(x0, y0);
                            if (result >= 0)
                                numShown++;
                        }
                    }
                }
            }
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            render(g);
        }

        private void render(Graphics g) {
            int value;

            setBackground(COLOR_BACKGROUND);

            if (gameMode == GAME_MODE_INIT)
                return;

            g.setColor(COLOR_LINES);
            for (int x = 0; x <= sizeHorizontal; x++)
                g.drawLine(x * cellWidth, 0, x * cellWidth, sizeVertical * cellHeight);
            for (int y = 0; y <= sizeVertical; y++)
                g.drawLine(0, y * cellHeight, sizeHorizontal * cellWidth, y * cellHeight);

            for (int x = 0; x < sizeHorizontal; x++) {
                for (int y = 0; y < sizeVertical; y++) {
                    if (!field[x][y].isHidden()) {
                        if (field[x][y].isMined())
                            if (x == blowX && y == blowY)
                                drawMine(g, x * cellWidth, y * cellHeight, Color.RED);
                            else
                                drawMine(g, x * cellWidth, y * cellHeight, Color.BLACK);

                        else {
                            value = field[x][y].getValue();

                            g.setColor(COLOR_VALUE[0]);
                            g.fillRect(x * cellWidth + 2, y * cellHeight + 2, cellWidth - 4, cellHeight - 4);

                            if (value > 0) {
                                g.setColor(COLOR_VALUE[value]);
                                g.setFont(fontValue);
                                g.drawString("" + value, (int) ((x + 1 / 3.0f) * cellWidth), (int) ((y + 1 - 1 / 6.0f) * cellHeight));
                            }
                        }
                    }
                    if (field[x][y].getFlagStatus() != Cell.FLAG_STATUS_EMPTY) {
                        drawFlagStatus(g, x * cellWidth, y * cellHeight, field[x][y].getFlagStatus());
                        if (gameMode == GAME_MODE_FINISHED && !field[x][y].isMined()) {
                            g.setColor(COLOR_WRONG);
                            g.drawLine(x * cellWidth + 1, y * cellHeight + 1, (x + 1) * cellWidth - 1, (y + 1) * cellHeight - 1);
                            g.drawLine(x * cellWidth + 1, (y + 1) * cellHeight - 1, (x + 1) * cellWidth - 1, y * cellHeight + 1);
                        }
                    }
                }
            }
        }

        private void drawFlagStatus(Graphics g, int x, int y, int flag) {
            if (flag == Cell.FLAG_STATUS_SET) {
                g.setColor(Color.RED);
                g.fillRect(x + cellWidth / 2, y + cellHeight / 4, cellWidth / 3, cellHeight / 3);

                g.setColor(Color.GRAY);
                g.drawLine(x + cellWidth / 2, y + cellHeight / 4, x + cellWidth / 2, y + cellHeight * 3 / 4);
                g.drawLine(x + cellWidth / 3, y + cellHeight * 3 / 4, x + cellWidth * 2 / 3, y + cellHeight * 3 / 4);

            } else if (flag == Cell.FLAG_STATUS_POSSIBLE) {
                g.setColor(Color.WHITE);
                g.setFont(fontValue);
                g.drawString("?", x + cellWidth / 3, y + cellHeight * 5 / 6);
            }
        }

        private void drawMine(Graphics g, int x, int y, Color color) {
            g.setColor(color);
            g.drawLine(x + cellWidth / 4 + 1, y + cellHeight / 4 + 1, x + 3 * cellWidth / 4 - 1, y + 3 * cellHeight / 4 - 1);
            g.drawLine(x + 3 * cellWidth / 4 - 1, y + cellHeight / 4 + 1, x + cellWidth / 4 + 1, y + 3 * cellHeight / 4 - 1);
            g.drawLine(x + cellWidth / 4 - 1, y + cellHeight / 2, x + 3 * cellWidth / 4 + 1, y + cellHeight / 2);
            g.drawLine(x + cellWidth / 2, y + cellHeight / 4 - 1, x + cellWidth / 2, y + 3 * cellHeight / 4 + 1);
            g.fillOval(x + cellWidth / 4, y + cellHeight / 4, cellWidth / 2, cellHeight / 2);
        }
    }

    class Cell {
        private boolean mined = false;

        final int VALUE_MINED = -1;
        private int value = 0;

        final static int FLAG_STATUS_EMPTY = 0;
        final static int FLAG_STATUS_SET = 1;
        final static int FLAG_STATUS_POSSIBLE = 2;
        final static int FLAG_STATUS_MAX = 3;

        private int flagStatus = FLAG_STATUS_EMPTY;

        private boolean hidden = true;

        int neighbourHidden;
        int neighbourFlags;
        float chances;
        float sum_chances;
        float difference; //

        public Cell() {

        }

        public boolean isMined() {
            return mined;
        }

        public void setMined(boolean mined) {
            this.mined = mined;
            this.value = VALUE_MINED;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getFlagStatus() {
            return flagStatus;
        }

        public void setFlagStatus(int flagStatus) {
            flagStatus %= FLAG_STATUS_MAX;

            if (this.flagStatus == FLAG_STATUS_SET && flagStatus != FLAG_STATUS_SET) {
                numFlag--;
                numberOfMinesLeft++;
            } else if (this.flagStatus != FLAG_STATUS_SET && flagStatus == FLAG_STATUS_SET) {
                numFlag++;
                numberOfMinesLeft--;
            }

            this.flagStatus = flagStatus;
        }

        public void nextFlagStatus() {
            setFlagStatus(getFlagStatus() + 1);
        }

        public int show() {
            if (!mined)
                setFlagStatus(FLAG_STATUS_EMPTY);
            hidden = false;
            return value;
        }

        public boolean isHidden() {
            return hidden;
        }

        public void increaseValue() {
            value += 1;
        }
    }
}
