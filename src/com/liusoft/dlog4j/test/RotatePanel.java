package com.liusoft.dlog4j.test;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

public class RotatePanel extends JPanel {
   private Image image;
   private double currentAngle;

   public RotatePanel(Image image) {
     this.image = image;
     MediaTracker mt = new MediaTracker(this);
     mt.addImage(image, 0);
     try {
       mt.waitForID(0);
     }
     catch (Exception e) {
       e.printStackTrace();
     }
   }

   public void rotate() {
     //rotate 5 degrees at a time
     currentAngle+=5.0;
     if (currentAngle >= 360.0) {
       currentAngle = 0;
     }
     repaint();
   }
   protected void paintComponent(Graphics g) {
     super.paintComponent(g);
     Graphics2D g2d = (Graphics2D)g;
     AffineTransform origXform = g2d.getTransform();
     AffineTransform newXform = (AffineTransform)(origXform.clone());
     //center of rotation is center of the panel
     int xRot = this.getWidth()/2;
     int yRot = this.getHeight()/2;
     newXform.rotate(Math.toRadians(currentAngle), xRot, yRot);
     g2d.setTransform(newXform);
     //draw image centered in panel
     int x = (getWidth() - image.getWidth(this))/2;
     int y = (getHeight() - image.getHeight(this))/2;
     g2d.drawImage(image, x, y, this);
     g2d.setTransform(origXform);
   }

   public Dimension getPreferredSize() {
     return new Dimension (image.getWidth(this), image.getHeight(this));
   }


   public static void main(String[] args) {
     JFrame f = new JFrame();
     Container cp = f.getContentPane();
     cp.setLayout(new BorderLayout());
     Image testImage =
         Toolkit.getDefaultToolkit().getImage("c:/gumby.gif");
     final RotatePanel rotatePanel = new RotatePanel(testImage);
     JButton b = new JButton ("Rotate");
     b.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent ae) {
           rotatePanel.rotate();
       }
     });
     cp.add(rotatePanel, BorderLayout.CENTER);
     cp.add(b, BorderLayout.SOUTH);
     f.pack();
     f.setVisible(true);
   }
}

