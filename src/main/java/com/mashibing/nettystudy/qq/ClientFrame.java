package com.mashibing.nettystudy.qq;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @Auther: TianHao
 * @Date: 2021/6/25 - 06 - 25 - 9:59
 * @Description: com.mashibing.nettystudy.qq
 * @version: 1.0
 */
public class ClientFrame extends Frame{

    public static final ClientFrame INSTANCE = new ClientFrame();


    Client c = null;
    TextArea ta = new TextArea();
    TextField tf = new TextField();

    public ClientFrame() {
        this.setSize(600, 400);
        this.setLocation(100,20);
        this.add(ta, BorderLayout.CENTER);
        this.add(tf, BorderLayout.SOUTH);
        tf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                c.send(tf.getText());
                //ta.setText(ta.getText() + tf.getText());
                tf.setText("");
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                c.closeConnect();
                System.exit(0);
            }
        });
        this.setVisible(true);
        //connecttoserver();这儿做成单例以后，就不要在初始化的时候连接了
    }

    private void connecttoserver() {
        c = new Client();
        c.connect();
    }


    public static void main(String[] args) {
        ClientFrame frame = ClientFrame.INSTANCE;
        frame.setVisible(true);
        frame.connecttoserver();
    }

    public void updateText(String msgAccepted) {
        ta.setText(ta.getText()+System.getProperty("line.separator")+msgAccepted);
    }
}
