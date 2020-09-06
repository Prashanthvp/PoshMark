package model;

import java.util.List;
import service.CombinationCalcService;
import util.Utils;

public class ServerRequestModel {	

	private int hours;

	private int cpus;

	private float price;
	
	private CombinationCalcService combinationCalcService = new CombinationCalcService();

	public ServerRequestModel() {}	

	public ServerRequestModel(int hours,int cpus,float price) {

		this.hours = hours;

		this.cpus = cpus;

		this.price = price;
	}


	public void getCosts() throws Exception {

		if (hours != 0 && cpus != 0 && price == 0) {
			List<FinalObject> r = combinationCalcService.calculatePriceforHoursandCPU(hours,cpus);
			Utils.printResult(r,true);
		} else if (hours != 0 && price != 0 && cpus == 0) {
			List<FinalObject> r =  combinationCalcService.calculateCPUsforHoursandPrice(hours,price);
			Utils.printResult(r,true);
		} else if (hours != 0 && cpus != 0 && price != 0) {
			List<FinalObject> r = combinationCalcService.calculateComboforPriceHourCPU(hours,cpus,price);
			Utils.printResult(r,false);
		} else
			System.out.println("Combo not available");		
	}	
}
	