package com.neuSep17.ui.manageIncentives;

import com.neuSep17.dto.Category;
import com.neuSep17.dto.Incentive;
import com.neuSep17.dto.IncentiveTableModel;
import com.neuSep17.service.IncentiveServiceAPI_Test;
import com.neuSep17.service.InventoryServiceAPI_Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IncentiveAddEditDialog extends JDialog {

    //service
    private IncentiveServiceAPI_Test incentiveAPI;
    private InventoryServiceAPI_Test inventoryAPI;
    private JTable incentive_list;
    //current dealer
    private String dealerId;
    private Incentive incentive;
    //label
    private JLabel labelTitle, labelDiscount, labelStart, labelEnd, labelCriterion, labelDescription;
    private JTextField fieldTitle, fieldDiscount;
    JSpinner startDate, endDate;
    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    private JTextArea description;
    private JScrollPane scrollPane;
    //comboBoxes for criterion
    private JComboBox[] criterions;
    private Map<String, Map<String, Integer>> criterionMap;
    private String[] criterionKey = {"range", "category", "year", "make", "price"};
    private JButton buttonSave;
    private JButton buttonCancel;
    private JOptionPane alert;
    //incentive file, should be replaced by dealer_Incentive.txt
    String path = "data/" + dealerId + "Incentive.txt";
    String file = path;

    public IncentiveAddEditDialog(String dealerId, JTable incentive_list) {
        this.dealerId = dealerId;
        this.incentive_list = incentive_list;
        setTitle("add incentive");
        init();
    }

    //edit constructor
    public IncentiveAddEditDialog(String dealerId, Incentive incentive, JTable incentive_list) {
        this.dealerId = dealerId;
        this.incentive = incentive;
        this.incentive_list = incentive_list;
        init();
        setTitle("edit incentive");
    }

    private void init() {
        incentiveAPI = new IncentiveServiceAPI_Test("data/" + dealerId+"Incentive.txt");
        inventoryAPI = new InventoryServiceAPI_Test("data/" + dealerId);
        initComponents();
        addComponents();
        makeListeners();
        display();
    }

    private void initComponents() {
        labelTitle = new JLabel("Title: ");
        labelDiscount = new JLabel("Discount: ");
        labelStart = new JLabel("Start Date: ");
        labelEnd = new JLabel("End Date: ");
        labelCriterion = new JLabel("Criterion: ");
        labelDescription = new JLabel("Description: ");


        fieldTitle = new JTextField(incentive == null ? "" : incentive.getTitle(), 20);
        fieldDiscount = new JTextField(incentive == null ? "" : String.valueOf(incentive.getDiscount()), 20);

        createDateComponents();

        description = new JTextArea(incentive == null ? "" : incentive.getDescription(), 3, 20);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        scrollPane = new JScrollPane(description);

        createCriterionComponents();

        if (incentive != null) {
            initCriterion(incentive);
        }

        buttonSave = new JButton("Save");
        buttonCancel = new JButton("Cancel");
    }

    private void createDateComponents() {
        startDate = new JSpinner(new SpinnerDateModel());
        endDate = new JSpinner(new SpinnerDateModel());
        try {

            JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startDate, "yyyy-MM-dd");
            JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endDate, "yyyy-MM-dd");

            startDate.setEditor(startEditor);
            endDate.setEditor(endEditor);

            startDate.setValue(incentive == null ?
                    new Date() : fmt.parse(incentive.getStartDate()));
            endDate.setValue(incentive == null ?
                    new Date() : fmt.parse(incentive.getEndDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void createCriterionComponents() {
        criterions = new JComboBox[criterionKey.length];
        criterionMap = inventoryAPI.getComboBoxItemsMap(inventoryAPI.getVehicles());
        for (int i = 0; i < criterions.length; i++) {
            criterions[i] = new JComboBox();
            criterions[i].addItem("Choose " + criterionKey[i] + "...");
            criterions[i].setPreferredSize(new Dimension(200, 30));
            criterions[i].setName(criterionKey[i]);
            if (i > 1) {
                for (String item : criterionMap.get(criterionKey[i]).keySet()) {
                    if (criterionKey[i].equals("price")) {
                        item = item.substring(item.indexOf("~") + 1);
                    }
                    if (!item.isEmpty()) {
                        criterions[i].addItem(item);
                    }
                }
            }
        }
        criterions[0].addItem("all");
        for (Category s : Category.values()) {
            criterions[1].addItem(s);
        }
    }


    private void initCriterion(Incentive incentive) {
        //criterion data,get data from vehicle
        ArrayList<String> criterion = incentive.getCriterion();
        String[] crit = new String[5];
        int i = 0;
        while (i < 4) {
            crit[i] = criterion.get(i);
            i++;
        }
        crit[4] = criterion.get(criterion.size() - 1);

        if (crit[0].replace("[", "").equals("all")) {
            criterions[0].setSelectedIndex(1);
            for (i = 1; i < criterions.length; i++) {
                criterions[i].setEnabled(false);
            }
        } else {
            for (i = 1; i < criterions.length; i++) {
                criterions[i].setSelectedItem(crit[i].replace("]", "").equals("no") ? criterions[i].getItemAt(0) : crit[i]);
            }
        }
    }

    private void addComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.WEST;

        addLine(constraints, labelTitle, fieldTitle);
        addLine(constraints, labelDiscount, fieldDiscount);
        addLine(constraints, labelStart, startDate);
        addLine(constraints, labelEnd, endDate);
        addCriterion(constraints);
//        constraints.ipady = 40;
        addLine(constraints, labelDescription, scrollPane);

        addButton(constraints);
    }

    private void addLine(GridBagConstraints c, JComponent label, JComponent text) {
        c.gridx = 0;
        add(label, c);
        c.gridx = 1;
        add(text, c);
        c.gridy++;
    }

    private void addCriterion(GridBagConstraints c) {
        c.gridx = 0;
        add(labelCriterion, c);
        c.gridheight = 1;
        c.gridx = 1;
        for (int i = 0; i < criterions.length; i++) {
            add(criterions[i], c);
            c.gridy++;
        }
    }

    private void addButton(GridBagConstraints constraints) {
        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelButtons.add(buttonSave);
        panelButtons.add(buttonCancel);

        constraints.gridy++;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        add(panelButtons, constraints);
    }

    private void makeListeners() {
        CriterionActionListener cl = new CriterionActionListener();
        criterions[0].addActionListener(cl);

        ValidationActionListener vl = new ValidationActionListener();
        buttonSave.addActionListener(vl);

        buttonCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
    }

    class CriterionActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateCreterion();
        }
    }

    private void updateCreterion() {
        if (criterions[0].getSelectedIndex() != 0) {
            for (int i = 1; i < criterions.length; i++) {
                criterions[i].setSelectedItem(criterions[i].getItemAt(0));
                criterions[i].setEnabled(false);
            }
        } else {
            for (int i = 1; i < criterions.length; i++) {
                criterions[i].setEnabled(true);
            }
        }
    }

    //Jing
    /*when click "Save" button,check textFields and comboBox */
    class ValidationActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (!isVaildText(fieldTitle.getText())) {
                AlertDialog(labelTitle.getText(), "");
            } else if (!isVaildText(fieldDiscount.getText()) || !isValidNum(fieldDiscount.getText())) {
                if (!isVaildText(fieldDiscount.getText())) {
                    AlertDialog(labelDiscount.getText(), "");
                } else {
                    AlertDialog(labelDiscount.getText(), "price");
                }
            } else if (!isVaildDate()) {
                alert.showMessageDialog(new JFrame(),
                        "Start date should less than end date.",
                        "Input Invalid",
                        JOptionPane.WARNING_MESSAGE);
            } else if (!isVaildText(description.getText())) {
                AlertDialog(labelDescription.getText(), "");
            } else if (isValidCriterions()) {
                saveIncentive();
            }
        }
    }

    private boolean isVaildText(String text) {
        if (text == null || text.trim().equals("")) {
            return false;
        }
        return true;
    }

    private boolean isValidNum(String text) {
        //string.trim().matches(s)
        Pattern p = Pattern.compile("^\\d+(.\\d+)?$");//not negative number- float/double
        Matcher m = p.matcher(fieldDiscount.getText());
        if (m.matches()) {
            return true;
        }
        return false;
    }

    private boolean isVaildDate() {
        String start = fmt.format(startDate.getValue());
        String end = fmt.format(endDate.getValue());
        return start.compareTo(end) < 0;
    }

    private boolean isValidCriterions() {
        if (criterions[0].getSelectedIndex() != 0) {
            return true;
        }
        boolean result = false;
        for (int i = 1; i < criterions.length; i++) {
            if (criterions[i].getSelectedIndex() != 0) {
                result = true;
            }
        }
        if (!result) {
            AlertDialog("Criterion", "");
            return false;
        }
        return true;
    }

    private void AlertDialog(String content, String flag) {
        String messge = "";
        if (flag.equals("price")) {
            messge = " Price must be digits.";
        } else {
            messge = content + " is not supposed to be NULL.";
        }
        alert.showMessageDialog(new JFrame(),
                messge,
                "Input Invalid",
                JOptionPane.WARNING_MESSAGE);
    }

    private void saveIncentive() {
        String[] arr = new String[8];
        arr[1] = dealerId;
        arr[2] = fieldTitle.getText().trim();
        arr[3] = fieldDiscount.getText();
        arr[4] = fmt.format(startDate.getValue());
        arr[5] = fmt.format(endDate.getValue());
        arr[6] = getCriterionValue();
        arr[7] = description.getText();
        if (incentive == null) {
            arr[0] = generateIncentiveID();
        } else {
            arr[0] = incentive.getId();
        }
        incentive = new Incentive(arr);
        incentiveAPI.addIncentive(incentive);
        incentiveAPI.saveIncentiveToFile();


        refresh();
        close();

    }

    private String generateIncentiveID() {
        int max = 0;
        for (Incentive incentive : incentiveAPI.getIncentives()) {
            int i = Integer.parseInt(incentive.getId());
            if (i > max) {
                max = i;
            }
        }
        max++;
        DecimalFormat format = new DecimalFormat("000000");
        return format.format(max);
    }

    private String getCriterionValue() {
        //VIN(or all, or no),category,year,make,model,trim,type,price
        if (criterions[0].getSelectedIndex() == 1) {
            return "all,no,no,no,no,no,no,no";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("no,");
            //category,year,make
            for (int i = 1; i < 4; i++) {
                sb.append(criterions[i].getSelectedIndex() == 0 ? "no," : criterions[i].getSelectedItem()+",");
            }
            //model,trim,type(we haven't decided to add all these fields, just add it here.)
            sb.append("no,no,no,");
            //price
            sb.append(criterions[4].getSelectedIndex() == 0 ? "no" : criterions[4].getSelectedItem());
            return sb.toString();
        }
    }

    private void display() {
        setSize(500, 800);
        setVisible(true);
    }

    private void close() {
        dispose();
    }

    private void refresh() {
        incentive_list.setModel(new IncentiveTableModel(new IncentiveServiceAPI_Test("data/" + dealerId + "Incentive.txt")));
        incentive_list.updateUI();
    }
}