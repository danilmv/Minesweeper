import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Settings extends JDialog {
    private Minesweeper main;

    private final static int WINDOW_WIDTH = 250;
    private final static int WINDOW_HEIGHT = 380;

    private final static int MODE_NEWBIEW = 0;
    private final static int MODE_AVERAGE = 1;
    private final static int MODE_PRO = 2;
    private final static int MODE_CUSTOM = 3;

    private int mode;
    private int sizeVertical;
    private int sizeHorizontal;
    private int numberOfMines;

    private final int[] SIZE_VERTICAL = {9, 16, 16};
    private final int[] SIZE_HORIZONTAL = {9, 16, 30};
    private final int[] NUMBER_OF_MINES = {10, 40, 99};

    private final String strWidth = "Ширина: ";
    private final String strHeight = "Высота: ";
    private final String strMines = "Количество мин: ";
    private int currentWidth = SIZE_HORIZONTAL[0];
    private int currentHeight = SIZE_VERTICAL[0];
    private int currentMines = NUMBER_OF_MINES[0];

    Settings(Minesweeper main) {
        this.main = main;

        setLocationRelativeTo(main);

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        setTitle("Настройки");
        setModal(true);

        setLayout(new GridLayout(12, 1));

        addControls();

    }

    private void addControls() {
        JPanel pnlTitle = new JPanel();
        pnlTitle.setBackground(Color.GRAY);
        JLabel lblTitle = new JLabel("Размер поля");
        lblTitle.setForeground(Color.WHITE);
        pnlTitle.add(lblTitle, BorderLayout.CENTER);
        add(pnlTitle);

        JSlider sliderWidth = new JSlider(2, 50, currentWidth);
        JSlider sliderHeight = new JSlider(2, 50, currentHeight);
        JSlider sliderMines = new JSlider(1, 80, currentMines);

        JRadioButton radioNewbie = new JRadioButton("Новичок", true);
        JRadioButton radioAverage = new JRadioButton("Продвинутый");
        JRadioButton radioPro = new JRadioButton("Профи");
        JRadioButton radioCustom = new JRadioButton("Заданный пользователем");
        ButtonGroup group = new ButtonGroup();
        group.add(radioNewbie);
        group.add(radioAverage);
        group.add(radioPro);
        group.add(radioCustom);

        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (radioNewbie.isSelected())
                    mode = MODE_NEWBIEW;
                else if (radioAverage.isSelected())
                    mode = MODE_AVERAGE;
                else if (radioPro.isSelected())
                    mode = MODE_PRO;
                else
                    mode = MODE_CUSTOM;

                if (mode != MODE_CUSTOM) {
                    currentHeight = SIZE_VERTICAL[mode];
                    currentWidth = SIZE_HORIZONTAL[mode];
                    currentMines = NUMBER_OF_MINES[mode];

                    sliderMines.setValue(currentMines);
                    sliderHeight.setValue(currentHeight);
                    sliderWidth.setValue(currentWidth);
                }

                sliderHeight.setEnabled(mode == MODE_CUSTOM);
                sliderWidth.setEnabled(mode == MODE_CUSTOM);
                sliderMines.setEnabled(mode == MODE_CUSTOM);
            }
        };

        radioNewbie.addActionListener(listener);
        radioAverage.addActionListener(listener);
        radioPro.addActionListener(listener);
        radioCustom.addActionListener(listener);

        add(radioNewbie);
        add(radioAverage);
        add(radioPro);
        add(radioCustom);


        JLabel labelWidth = new JLabel(strWidth + currentWidth);
        sliderWidth.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                currentWidth = sliderWidth.getValue();
                labelWidth.setText(strWidth + currentWidth);
                sliderMines.setMaximum(currentWidth * currentHeight - 1);
            }
        });
        add(labelWidth);
        add(sliderWidth);

        JLabel labelHeight = new JLabel(strHeight + currentHeight);
        sliderHeight.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                currentHeight = sliderHeight.getValue();
                labelHeight.setText(strHeight + currentHeight);
                sliderMines.setMaximum(currentWidth * currentHeight - 1);
            }
        });
        add(labelHeight);
        add(sliderHeight);

        JLabel labelMines = new JLabel(strMines + currentMines);
        sliderMines.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                currentMines = sliderMines.getValue();
                labelMines.setText(strMines + currentMines);
            }
        });
        add(labelMines);
        add(sliderMines);


        JButton buttonNewGame = new JButton("Новая игра");
        buttonNewGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.startGame(currentWidth, currentHeight, currentMines);

                setVisible(false);
            }
        });

        add(buttonNewGame);

    }

    public void showWindow() {
        setVisible(true);
    }


}
