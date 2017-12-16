package com.neuSep17.ui.inventoryList.CenterSection;

import com.neuSep17.dto.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class VehicleImageCell extends JPanel {
    private JLabel next,background,carImage, nameLabel;
    private ImageIcon preImageEnter,preImageExit,backgroundIcon,carIcon;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Vehicle vehicle;


    public VehicleImageCell(Vehicle v, ImageIcon icon, float discount, CardLayout cardLayout, JPanel cardPanel) throws IOException{
        super();
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.carIcon = icon;
        this.vehicle = v;


        creatComponent(icon,discount);
        addListener();
        addComponent();
    }
    private void creatComponent(ImageIcon icon, float discount){

        carImage = new JLabel(carIcon);
        carImage.setBounds(0,0,300,300);

        backgroundIcon = new ImageIcon("src/com/neuSep17/ui/newInventoryList/material/background.png");
        background = new JLabel(backgroundIcon);
        background.setBounds(0,0,300,80);
        StringBuilder title= new StringBuilder("<html>"+"<body style=\"padding:10px;\" >"+"<font color='white'>"+(vehicle.getCategory().toString()+
                " "+vehicle.getYear() + " " + vehicle.getMake())+"<br></font>");
        if (discount > 0 ) title.append("<font color='white'> On Sale: "+Math.max(vehicle.getPrice()-discount,0)+"!</font>");
        title.append("</body><html>");

        nameLabel = new JLabel(title.toString()+vehicle.getId());
        nameLabel.setFont(new Font(nameLabel.getFont().getFontName(), Font.BOLD, 24));
        nameLabel.setVerticalAlignment(SwingConstants.TOP);
        nameLabel.setBounds(0,0,300,200);

        preImageEnter = new ImageIcon("src/com/neuSep17/ui/inventoryList/material/right80%.png");
        preImageExit = new ImageIcon("src/com/neuSep17/ui/inventoryList/material/right50%.png");

        next = new JLabel(preImageExit);
        next.setBounds(250,0,50,300);


    }

    private void addListener(){

        next.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.next(cardPanel);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                next.setIcon(preImageEnter);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                next.setIcon(preImageExit);
            }
        });

    }

    private void addComponent(){
        setSize(300,300);
        setBackground(Color.RED);
        setLayout(null);
        add(nameLabel);
        add(background);
        add(next);

        add(carImage);
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
}



