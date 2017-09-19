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

 private JTextField textField_1;
 private JTextField textField_2;
 private JTextField textField_3;

 private int devi, look, surface;

 
 //TODO DI for defaults
 
 
 //defaults to go down the line 
 //
 //
 //
 private static final String devToMakeItValidRoutable = "56";
 private static final String lookAheadAndBack = "3";
 private static final String surfaceLimit = "46";
 //
 //
 //
 //

 public void run() {

  System.out.println("ControlWin is EDT " + SwingUtilities.isEventDispatchThread());

  JFrame frame = new JFrame("Controls");
  Dimension def = new Dimension(260, 320);
  Dimension min = new Dimension(80, 160);
  frame.setSize(def);
  frame.setMinimumSize(min);
  GridLayout myLayout = new GridLayout(0, 1);

  frame.getContentPane().setLayout(myLayout);
  frame.setAlwaysOnTop(true);
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  JLabel label1 = new JLabel("dev to make it valid routable");
  label1.setBorder(new EmptyBorder(10, 10, 10, 10));
  label1.setHorizontalAlignment(SwingConstants.CENTER);
  label1.setToolTipText("toolTip1");
  frame.getContentPane().add(label1);

  textField_1 = new JTextField();
  textField_1.setText(devToMakeItValidRoutable); //default for dev to make it valid routable
  textField_1.setBorder(new EmptyBorder(10, 10, 10, 10));
  textField_1.setHorizontalAlignment(SwingConstants.CENTER);
  frame.getContentPane().add(textField_1);
  textField_1.setColumns(10);

  JLabel label2 = new JLabel("look ahead and back");
  label2.setBorder(new EmptyBorder(10, 10, 10, 10));
  label2.setHorizontalAlignment(SwingConstants.CENTER);
  label2.setToolTipText("toolTip2");
  frame.getContentPane().add(label2);

  textField_2 = new JTextField();
  textField_2.setText(lookAheadAndBack); //default for look ahead and back
  textField_2.setBorder(new EmptyBorder(10, 10, 10, 10));
  textField_2.setHorizontalAlignment(SwingConstants.CENTER);
  frame.getContentPane().add(textField_2);
  textField_2.setColumns(10);

  JLabel label3 = new JLabel("surface limit");
  label3.setBorder(new EmptyBorder(10, 10, 10, 10));
  label3.setHorizontalAlignment(SwingConstants.CENTER);
  label3.setToolTipText("toolTip3");
  frame.getContentPane().add(label3);

  textField_3 = new JTextField();
  textField_3.setText(surfaceLimit); //default for surface limit
  textField_3.setBorder(new EmptyBorder(10, 10, 10, 10));
  textField_3.setHorizontalAlignment(SwingConstants.CENTER);
  frame.getContentPane().add(textField_3);
  textField_3.setColumns(10);

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
