package com.neuSep17.ui.inventoryList.CenterSection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class FilterPanel extends JPanel {
	private Map<String, Map<String, Integer>> checkBoxPanelsMap;
	private DealerMainWindow parent;
	private JScrollPane filterScrollPane;
	private FilterCheckBoxPanel[] checkBoxPanels;
	private String[] filterKeys = { "category", "make", "type" };
	private JPanel filterPanel;
	private int northPanelHeight = 280;

	private JTextField priceFrom;
	private JTextField priceTo;
	private JTextField yearFrom;
	private JTextField yearTo;
	private JButton confirm;

	public FilterPanel(DealerMainWindow parent) {
		this.parent = parent;
		this.setLayout(new BorderLayout(0, 0));
		this.setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(210, parent.getSize().height - northPanelHeight));
		createComponents();
		addComponents();
	}

	public void setCheckBoxPanelsMap(Map<String, Map<String, Integer>> checkBoxPanelsMap) {
		this.checkBoxPanelsMap = checkBoxPanelsMap;
		filterScrollPane.getVerticalScrollBar().setValue(0);
		updateFilterCheckBoxPanels();
	}

	public void setEnableCheckBoxMap(Map<String, Map<String, Integer>> enableCheckBoxesMap, String currentChecked) {
		for (int i = 0; i < checkBoxPanels.length; i++) {
			checkBoxPanels[i].setEnableCheckBoxes(enableCheckBoxesMap.get(filterKeys[i]), currentChecked);
		}
	}

	private void createComponents() {
		filterPanel = new JPanel();
		filterScrollPane = new JScrollPane();
		filterScrollPane.setPreferredSize(new Dimension(200, parent.getSize().height - northPanelHeight));
		filterScrollPane.setOpaque(false);
		checkBoxPanels = new FilterCheckBoxPanel[filterKeys.length];
		for (int i = 0; i < checkBoxPanels.length; i++) {
			checkBoxPanels[i] = new FilterCheckBoxPanel(filterKeys[i], this);
		}

		yearFrom = new JTextField();
		yearTo = new JTextField();
		priceFrom = new JTextField();
		priceTo = new JTextField();
		confirm = new JButton("Ok");
	}

	private void addComponents() {
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
		filterPanel.setOpaque(false);
		filterPanel.setAutoscrolls(true);
		filterPanel.setBorder(new TitledBorder("Narrow Matches By: "));
		JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		linePanel.setOpaque(false);
		linePanel.add(confirm);


		JPanel yearPanel = new JPanel();
		JPanel pricePanel = new JPanel();

		yearPanel.setLayout(new GridLayout(0, 4));
		yearPanel.setBackground(Color.WHITE);

		pricePanel.setLayout(new GridLayout(0, 4));
		pricePanel.setBackground(Color.WHITE);

		yearFrom.setPreferredSize(new Dimension(5,15));
		yearTo.setPreferredSize(new Dimension(5,15));

		priceFrom.setPreferredSize(new Dimension(5,15));
		priceTo.setPreferredSize(new Dimension(5,15));

		yearPanel.add(new JLabel(" YEAR"));
		yearPanel.add(yearFrom);
		yearPanel.add(new JLabel("     to"));
		yearPanel.add(yearTo);

		yearPanel.add(new JLabel(" PRICE"));
		yearPanel.add(priceFrom);
		yearPanel.add(new JLabel("     to"));
		yearPanel.add(priceTo);

		filterPanel.add(yearPanel);
		filterPanel.add(linePanel);
		filterPanel.add(pricePanel);

		filterPanel.add(checkBoxPanels[0]);
		filterPanel.add(checkBoxPanels[1]);
		filterPanel.add(checkBoxPanels[2]);

		filterPanel.add(Box.createVerticalStrut(0));
		filterScrollPane.setViewportView(filterPanel);
		this.add(filterScrollPane);
	}

	public void addListeners() {
		confirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFilterConditions("");
			}
		});
	}

	private void updateFilterCheckBoxPanels() {
		for (int i = 0; i < checkBoxPanels.length; i++) {
			checkBoxPanels[i].populateCheckBoxes(checkBoxPanelsMap.get(filterKeys[i]));
		}
	}

	private void resetFilterCheckBox() {
		for (int i = 0; i < checkBoxPanels.length; i++) {
			checkBoxPanels[i].clearAllChecked();
		}
	}

	public void updateFilterConditions(String currentChecked) {
		String categoriesFilter = checkBoxPanels[0].generateFilterCondition();
		if (categoriesFilter != null) {
			categoriesFilter = categoriesFilter.trim().toLowerCase();
		}

		String fromYear = yearFrom.getText();
		String toYear = yearTo.getText();
		String fromPrice = priceFrom.getText();
		String toPrice = priceTo.getText();

		parent.doFilter(categoriesFilter, fromYear, checkBoxPanels[1].generateFilterCondition(), fromPrice,
				checkBoxPanels[2].generateFilterCondition(), currentChecked);
	}

	public void resizeFilterPanel() {
		int height = 30;
		for (FilterCheckBoxPanel checkBoxPanel : checkBoxPanels) {
			height += checkBoxPanel.getCurrentHeight();
		}

		if (height < parent.getSize().height - northPanelHeight) {
			filterPanel.remove(filterPanel.getComponentCount() - 1);
			filterPanel.add(Box.createVerticalStrut(parent.getSize().height - northPanelHeight - height));
		} else {
			filterPanel.remove(filterPanel.getComponentCount() - 1);
			filterPanel.add(Box.createVerticalStrut(0));
		}
	}
}

class FilterCheckBoxPanel extends JPanel {
	private List<JCheckBox> checkBoxes;
	private String title;
	private JButton buttonMore;
	private JButton buttonLess;
	private JPanel checkBoxesPanel;
	private boolean isButtonMore;
	private FilterCheckBoxListener fcbl;
	private FilterPanel parent;

	public FilterCheckBoxPanel(String title, FilterPanel parent) {
		this.checkBoxes = new ArrayList<JCheckBox>();
		this.title = title;
		this.parent = parent;
		this.isButtonMore = true;
		this.fcbl = new FilterCheckBoxListener();
		this.setOpaque(false);
		createPanelComponents();
		addPanelComponents();
		addPanelListeners();
	}

	private void createPanelComponents() {
		checkBoxesPanel = new JPanel();
	}

	private void addPanelComponents() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		addShowAndHideComponents();
		this.add(checkBoxesPanel);
	}

	private void addPanelListeners() {
		addButtonsListeners();
	}

	public void clearAllChecked() {
		removeCheckBoxesListeners();
		for (JCheckBox checkBox : checkBoxes) {
			checkBox.setSelected(false);
		}
		addCheckBoxesListeners();
	}

	public int getCurrentHeight() {
		if (!isButtonMore) {
			return 40 + 30 + checkBoxes.size() * 25;
		}

		if (checkBoxes.size() > 4) {
			return 40 + 30 + 4 * 25;
		}
		return 40 + checkBoxes.size() * 25;
	}

	public void populateCheckBoxes(Map<String, Integer> itemsMap) {
		if (itemsMap == null) {
			return;
		}

		removeCheckBoxesListeners();
		checkBoxes.clear();
		createCheckBoxesComponents(itemsMap);
		addCheckBoxesComponents();
		hideCheckBoxesFrom(200);
		addCheckBoxesListeners();
	}

	public void setEnableCheckBoxes(Map<String, Integer> enableItemsMap, String currentChecked) {
		if (enableItemsMap == null || (title.equals(currentChecked) && !isAllUnchecked())) {
			return;
		}

		Set<String> enableItems = enableItemsMap.keySet();
		for (JCheckBox checkBox : checkBoxes) {
			if (enableItems.contains(checkBox.getName())) {
				checkBox.setText(checkBox.getName());

				checkBox.setEnabled(true);
			} else {
				checkBox.setText(checkBox.getName());
				checkBox.setEnabled(false);
			}
		}
	}

	private boolean isAllUnchecked() {
		for (JCheckBox checkBox : checkBoxes) {
			if (checkBox.isSelected()) {
				return false;
			}
		}
		return true;
	}


	private void removeCheckBoxesListeners() {
		for (JCheckBox checkBox : checkBoxes) {
			checkBox.removeActionListener(fcbl);
		}
	}

	private void createCheckBoxesComponents(Map<String, Integer> itemsMap) {
		for (Entry<String, Integer> entry : itemsMap.entrySet()) {
			JCheckBox checkBox = new JCheckBox();
			checkBox.setText(entry.getKey());
			checkBox.setName(entry.getKey());
			checkBoxes.add(checkBox);
		}
	}

	private void addShowAndHideComponents() {
		JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		linePanel.setOpaque(false);
		linePanel.add(new JLabel(title.toUpperCase()));
		this.add(linePanel);
	}

	private void addCheckBoxesComponents() {
		checkBoxesPanel.removeAll();
		checkBoxesPanel.setLayout(new BoxLayout(checkBoxesPanel, BoxLayout.Y_AXIS));
		checkBoxesPanel.setOpaque(false);
		for (JCheckBox checkBox : checkBoxes) {
			JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
			linePanel.setOpaque(false);
			linePanel.add(checkBox);
			linePanel.setPreferredSize(
					new Dimension(checkBox.getPreferredSize().width, checkBox.getPreferredSize().height + 2));
			checkBoxesPanel.add(linePanel);
		}
	}


	private void hideCheckBoxesFrom(int index) {
		for (int i = 0; i < index && i < checkBoxes.size(); i++) {
			checkBoxesPanel.getComponent(i).setVisible(true);
		}

		for (int i = index; i < checkBoxes.size(); i++) {
			checkBoxesPanel.getComponent(i).setVisible(false);
		}
	}

	private void addCheckBoxesListeners() {
		for (JCheckBox checkBox : checkBoxes) {
			checkBox.addActionListener(fcbl);
		}
	}

	private void addButtonsListeners() {
		DisplayButtonsListener dbl = new DisplayButtonsListener();

	}

	public String generateFilterCondition() {
		StringBuilder filter = new StringBuilder();
		int count = 0;
		for (JCheckBox checkBox : checkBoxes) {
			if (checkBox.isSelected()) {
				filter.append(checkBox.getName()).append(";");
				count++;
			}
		}

		if (count == 0 || count == checkBoxes.size()) {
			filter = null;
		} else {
			filter.deleteCharAt(filter.length() - 1);
		}

		return filter == null ? null : filter.toString();
	}


	class DisplayButtonsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int index = 0;

			if (e.getSource() == buttonMore) {
				buttonMore.setVisible(false);
				buttonLess.setVisible(true);
				isButtonMore = false;
				index = checkBoxes.size();
			} else if (e.getSource() == buttonLess) {
				buttonLess.setVisible(false);
				buttonMore.setVisible(true);
				isButtonMore = true;
				index = 200;
			}
			hideCheckBoxesFrom(index);
			parent.resizeFilterPanel();
		}
	}


	class FilterCheckBoxListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			parent.updateFilterConditions(title);
		}
	}
}