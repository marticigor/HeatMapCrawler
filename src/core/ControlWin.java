package core;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.SwingConstants;
import javax.swing.JTextField;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.border.EmptyBorder;
import javax.swing.SwingUtilities;

public class ControlWin implements Runnable {

	// TODO
	private static final String NAME = "test_3";
	
	private static final String SHOTS = "_number_of_shots";
	private static final String NODES = "_nodes";
	private static final String ADJACENTS = "_adjacents_nodes";
	
	public static final String TABLE_NODES = NAME + NODES;
	public static final String TABLE_ADJACENTS = NAME + ADJACENTS;
	public static final String TABLE_SHOTS = NAME + SHOTS;
	
	
    private JTextField textField_1;
    private JTextField textField_2;
    private JTextField textField_3;
    private JTextField textField_4;
    private JTextField textField_5;
    @SuppressWarnings("unused")
	private JTextField textField_6;
    @SuppressWarnings("unused")
	private JTextField textField_7;
    @SuppressWarnings("unused")
	private JTextField textField_8;
    @SuppressWarnings("unused")
	private JTextField textField_9;

    private int devi, look, surface;


    //TODO DI for defaults


    //defaults to go down the line
    //
    //
    //
    private static final String devToMakeItValidRoutable = "30";
    private static final String lookAheadAndBack = "2";
    private static final String surfaceLimit = "32";//42
    //
    private static final String param4 = "param4";
    private static final String param5 = "param5";
    @SuppressWarnings("unused")
	private static final String param6 = "param6";
    @SuppressWarnings("unused")
	private static final String param7 = "param7";
    @SuppressWarnings("unused")
	private static final String param8 = "param8";
    @SuppressWarnings("unused")
	private static final String param9 = "param9";
    //
    //
    //
    //
    private JFrame frame;

    private void decorateTF(JTextField tf) {
        tf.setBorder(new EmptyBorder(10, 10, 10, 10));
        tf.setHorizontalAlignment(SwingConstants.CENTER);
        frame.getContentPane().add(tf);
        tf.setColumns(10);
    }
    private void decorateL(JLabel lb) {
        lb.setBorder(new EmptyBorder(10, 10, 10, 10));
        lb.setHorizontalAlignment(SwingConstants.CENTER);
        lb.setToolTipText("toolTip");
        frame.getContentPane().add(lb);
    }

    public void run() {

        //

        System.out.println("ControlWin is EDT " + SwingUtilities.isEventDispatchThread());

        frame = new JFrame("Controls");
        Dimension def = new Dimension(260, 520);
        Dimension min = new Dimension(80, 160);
        frame.setSize(def);
        frame.setMinimumSize(min);
        GridLayout myLayout = new GridLayout(0, 1);

        frame.getContentPane().setLayout(myLayout);
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label1 = new JLabel("dev to make it valid routable");
        decorateL(label1);

        textField_1 = new JTextField();
        textField_1.setText(devToMakeItValidRoutable); //default for dev to make it valid routable
        decorateTF(textField_1);

        
        
        JLabel label2 = new JLabel("look ahead and back");
        decorateL(label2);

        textField_2 = new JTextField();
        textField_2.setText(lookAheadAndBack); //default for look ahead and back
        decorateTF(textField_2);

        
        
        JLabel label3 = new JLabel("surface limit");
        decorateL(label3);

        textField_3 = new JTextField();
        textField_3.setText(surfaceLimit); //default for surface limit
        decorateTF(textField_3);

        
        
        JLabel label4 = new JLabel("name");
        decorateL(label4);

        textField_4 = new JTextField();
        textField_4.setText(param4);
        decorateTF(textField_4);

        
        
        JLabel label5 = new JLabel("param5");
        decorateL(label5);

        textField_5 = new JTextField();
        textField_5.setText(param5);
        decorateTF(textField_5);

        JButton button1 = new JButton("OK");
        button1.setBorder(new EmptyBorder(10, 10, 10, 10));
        button1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                devi = Integer.parseInt(textField_1.getText());
                look = Integer.parseInt(textField_2.getText());
                surface = Integer.parseInt(textField_3.getText());

                Runner runner = new Runner(devi, look, surface);
                Thread forked = new Thread(runner);
                forked.start();
            }
        });
        frame.getContentPane().add(button1);
        frame.setVisible(true);
    }
}
