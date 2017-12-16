# This is the service API: InventoryServiceAPI_Test.java & IncentiveServiceAPI_Test.java are ready for use
Updated 2017-12-13


InventoryServiceAPI_Test API Documentation: (details are given below)
1. InventoryServiceAPI_Test(String file): the constructor of InventoryServiceAPI_Test, ".txt" inventory data file is pass as argument.

2. getVehicles(): return an ArrayList of vehicles(inventory) of a particular dealer based on the ".txt" inventory data file.

3. getFileName(): return the ".txt" inventory data file name of a particular dealer.

4. saveInventoryToFile(): save all the inventory into the ".txt" inventory data file of a particular dealer.

5. getVehiclesMap(String file): return a LinkedHashMap<VIN, vehivle> with VIN as the Key & vehicle as the Value based on ".txt" data file.

6. getTotalVehicleAmount(): return total amount of vehicles of a particular dealer's inventory has.

7. createVehicleFromInput(String[] vehicleData): return a vehicle instance according to the user's input on the UI page, String[] vehicleData is pass as the argument, for data at each index of String[], please refer to the constructor of Vehicle.java.

8. addVehicle(Vehicle vehicle): put VIN & vehicle instance to the vehicleMap data structure.

9. deleteVehicle(String vin): delete a vehicle instance based on the Key: VIN from the vehicleMap data structure.	

10. search(String text, TableRowSorter<TableModel> sorter): the method is based on the TableModel, the text String to be search is pass as argument, plus, a TableRowSorter<TableModel> instance is pass.( TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel) ).

11. cancelSearch(TableRowSorter<TableModel> sorter): return to the original page before searching

12. sortByColumn(int columnIndexToSort, TableRowSorter<TableModel> sorter, int Asc_Desc): the method is based on the TableModel, an integer is pass as the 1st argument to specifiy which column to sort, a TableRowSorter<TableModel> instance is pass as the 2nd argument, an integer is pass as the 3rd argument to specify:   0~ASCENDING, 1~DESCENDING, -1~UNSORTED(return to the unsorted pattern).





1. InventoryServiceAPI_Test(String file): the constructor of InventoryServiceAPI_Test, ".txt" inventory data file is pass as argument.
	public InventoryServiceAPI_Test(String file) {
		this.fileName = file;
		try {
			vehiclesMap = getVehiclesMap(file);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


2. getVehicles(): return an ArrayList of vehicles(inventory) of a particular dealer based on the ".txt" inventory data file.
	public ArrayList<Vehicle> getVehicles() {
		return new ArrayList<Vehicle>(vehiclesMap.values());
	}
  
	
3. getFileName(): return the ".txt" inventory data file name of a particular dealer.
	public String getFileName() {
		return fileName;
	}


4. saveInventoryToFile(): save all the inventory into the ".txt" inventory data file of a particular dealer.
	public void saveInventoryToFile() {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(new File(fileName)));
			writer.println("id~webId~category~year~make~model~trim~type~price~photo");
			for (Vehicle vehicle: vehiclesMap.values()) {
				writer.println(vehicle);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


5. getVehiclesMap(String file): return a LinkedHashMap<VIN, vehivle> with VIN as the Key & vehicle as the Value based on ".txt" data file.
	public static LinkedHashMap<String, Vehicle> getVehiclesMap(String file) throws IOException {
		File inventoryFile = new File(file);
		BufferedReader reader = new BufferedReader(new FileReader(inventoryFile));
		LinkedHashMap<String, Vehicle> vehicles = new LinkedHashMap<String, Vehicle>();		
		String line = null;
		int count = 0;
		while((line=reader.readLine())!=null) {
			count++;
			if(count==1) {continue;}
			String[] vehicleDataArray = line.split("~");
			Vehicle vehicle =new Vehicle(vehicleDataArray);
			vehicles.put(vehicle.getId(), vehicle);
		}
		
		reader.close();
		return vehicles;

	}


6. getTotalVehicleAmount(): return total amount of vehicles of a particular dealer's inventory has.
	public int getTotalVehicleAmount() {
		return vehiclesMap.size();
	}


7. createVehicleFromInput(String[] vehicleData): return a vehicle instance according to the user's input on the UI page, String[] 							 vehicleData is pass as the argument, for data at each index of String[], please refer to 						   the constructor of Vehicle.java.	
	public Vehicle createVehicleFromInput(String[] vehicleData) {
		Vehicle vehicle = new Vehicle(vehicleData);
		return vehicle;
	}

	
8. addVehicle(Vehicle vehicle): put VIN & vehicle instance to the vehicleMap data structure.
	public void addVehicle(Vehicle vehicle) {
		vehiclesMap.put(vehicle.getId(), vehicle);

	}


9. deleteVehicle(String vin): delete a vehicle instance based on the Key: VIN from the vehicleMap data structure.	
	public void deleteVehicle(String vin) {
		vehiclesMap.remove(vin);	
		
	}


10. search(String text, TableRowSorter<TableModel> sorter): the method is based on the TableModel, the text String to be search is pass as argument, plus, a TableRowSorter<TableModel> instance is pass.( TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel) )
	public void search(String text, TableRowSorter<TableModel> sorter) { 
		if (text.length() == 0) {
			sorter.setRowFilter(null);
		} 
		else {
			// (?i) regex flag: case-insensitive matching
			sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
		}
	}
	

11. cancelSearch(TableRowSorter<TableModel> sorter): return to the original page before searching.
  	public void cancelSearch(TableRowSorter<TableModel> sorter) {
		sorter.setRowFilter(null);
	}


12. sortByColumn(int columnIndexToSort, TableRowSorter<TableModel> sorter, int Asc_Desc): the method is based on the TableModel, an integer is pass as the 1st argument to specifiy which column to sort, a TableRowSorter<TableModel> instance is pass as the 2nd argument, an integer is pass as the 3rd argument to specify:   0~ASCENDING, 1~DESCENDING, -1~UNSORTED(return to the unsorted pattern).
	public void sortByColumn(int columnIndexToSort, TableRowSorter<TableModel> sorter, int Asc_Desc) {
		ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>();
		
		switch (Asc_Desc) {
		case 0:
			sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.ASCENDING));break;
		case 1:
			sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));break;
		case -1:
			sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.UNSORTED));break;
		}

		sorter.setSortKeys(sortKeys);
		sorter.sort();
	}

	
  // To be continued...

	public Vehicle getVehicleDetails(String id){
		return vehiclesMap.get(id);
	}
	
	
}
