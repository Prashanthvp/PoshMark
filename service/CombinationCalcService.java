package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import model.FinalObject;
import model.ServerCPUDetails;
import util.Utils;

public class CombinationCalcService {	

	/*Get the minimum cost to be paid to achieve the combination of CPUs and hours expected by the user
	 * 
	 * Calculation is done for all regions i.e USE,USW,Asia
	 * 
	 * Combo with least price is given as output
	 * 
	 * */

	public List<FinalObject> calculatePriceforHoursandCPU(int hours, int requiredCPUCount) {

		List<String> regionsList = ServerCPUDetails.regions;	

		List<FinalObject> smallestComboInAllRegions = new ArrayList<>();	

		for (String region:regionsList) {

			List<Integer> cpuArray = Utils.getCPUsCount(region);

			List<List<Integer>> cpuCombo = Utils.combinationSum(cpuArray, requiredCPUCount);

			if(!cpuCombo.isEmpty())			
				calculateFinalPricePerRegion(region, hours, cpuCombo, smallestComboInAllRegions);			

			Collections.sort(smallestComboInAllRegions,FinalObject.getpriceComparator());	//To get smallest price combo in all regions	

		}
		return smallestComboInAllRegions;
	}


	/*
	 * Calculate the price of each combo in each region
	 * 
	 * Minimum price combo per region is added to "smallestComboInAllRegions" list
	 * 
	 */

	private void calculateFinalPricePerRegion(String region, int hours, List<List<Integer>> cpuCombinations,
			List<FinalObject> smallestComboInAllRegions) {

		List<FinalObject> combosInParticularRegion = new ArrayList<FinalObject>();

		// Remove the extra CPUs from full stack to achieve the user required CPUs count

		for (List<Integer> cpuCombination:cpuCombinations) 
			combosInParticularRegion.add(calculatePricePerComboRemoval(region, hours, cpuCombination));		

		// Sort the list of combinations of a single region based on price 
		Collections.sort(combosInParticularRegion, FinalObject.getpriceComparator());

		smallestComboInAllRegions.add(combosInParticularRegion.get(0));  //To get smallest price combo in a particular region.
	}

	private FinalObject calculatePricePerComboRemoval(String region, int hours, List<Integer> cpuCombination) {

		HashMap<Integer, String> cpuNameMap = Utils.getserverCPUDetails();

		HashMap<String, Float> cpuSizeMap = ServerCPUDetails.serverDetails.get(region);

		HashMap<String, Float> newStack = new HashMap<String, Float>();

		float totalPrice = 0;

		for (Integer i:cpuCombination) {

			String sizeName = cpuNameMap.get(i);

			Float pricePerSize = cpuSizeMap.get(cpuNameMap.get(i));

			if (newStack.containsKey(sizeName)) 				
				newStack.put(sizeName, newStack.get(sizeName) + 1f);				
			else 
				newStack.put(sizeName,1f);			

			totalPrice = totalPrice + pricePerSize;		
		}

		totalPrice = totalPrice * hours;

		FinalObject f = new FinalObject(region, totalPrice, newStack);

		return f;
	}
	
	
	/*Get the maximum CPU count that can obtained for the price spent by the user for the required hours
	 * 
	 * Calculation is done for all regions i.e USE,USW,Asia
	 * 
	 * Combo with max CPU count is given as output
	 * 
	 * Price per hour is calculated for which the combo is derived.
	 * 
	 * */	

	public List<FinalObject> calculateCPUsforHoursandPrice(int totalHours, float totalPrice) {		

		List<FinalObject> finalList = new ArrayList<FinalObject>();		

		float pricePerHR = totalPrice/totalHours;

		List<String> regionsList = ServerCPUDetails.regions;		

		HashMap<String,Integer> cpuDetails = ServerCPUDetails.serverCPUDetailSize;

		for (String region:regionsList) {

			HashMap<String, Float> regionStackDetails = ServerCPUDetails.serverDetails.get(region);

			calcMaxCombo(region, totalPrice, pricePerHR,totalHours, regionStackDetails, cpuDetails, finalList);
		}

		Collections.sort(finalList,FinalObject.getcountComparator());

		return finalList;
	}

	private void calcMaxCombo(String region, float totalPrice, float pricePerHR, float totalHours, HashMap<String, Float> regionStackDetails, 
			HashMap<String,Integer> cpuDetails, List<FinalObject> finalList) {			

		for (Entry<String, Float> entry : regionStackDetails.entrySet()) {

			HashMap<String,Float> configuredStack = new HashMap<>();

			String key = entry.getKey();

			float value = entry.getValue();

			float fullValue = pricePerHR/value;

			float remaining = fullValue - (float)Math.floor(fullValue);

			float count = fullValue - remaining;

			float stackPrice = count * value * totalHours;

			float remainingCost = pricePerHR - (count * value);

			int cpusPerSize = cpuDetails.get(key);				

			float cpuCountPerSize = count * cpusPerSize;

			configuredStack.put(key, count);

			FinalObject stackObj = new FinalObject(region, stackPrice, configuredStack, cpuCountPerSize);

			calcForRemainingcost(remainingCost, totalHours, regionStackDetails, cpuDetails, stackObj);

			finalList.add(stackObj);

		};
	}

	private void calcForRemainingcost(float remainingCost, float totalHours, HashMap<String, Float> stackdetails,
			HashMap<String, Integer> cpuDetails, FinalObject obj) {

		String newCPUName = null ;

		float newCPUCount = 0;	

		float newPrice = 0;

		for (Entry<String, Float> entry : stackdetails.entrySet()) {

			String key = entry.getKey();

			float value = entry.getValue();

			if (value <= remainingCost) {

				float fullValue = remainingCost/value;

				float remaining = fullValue - (float)Math.floor(fullValue);

				float count = fullValue - remaining;

				float price = count * value * totalHours;

				remainingCost = remainingCost - (count * value);							

				int cpusPerSize = cpuDetails.get(key);				

				float cpuCountPerSize = count * cpusPerSize;

				/*Get CPU details of max count*/
				if (cpuCountPerSize >= newCPUCount) {
					newCPUName = key;
					newCPUCount = cpuCountPerSize;					
					newPrice = price;
				}
			}
		}

		if(newCPUName != null) {
			obj.getStack().put(newCPUName, newCPUCount);
			obj.setCount(obj.getCount() + newCPUCount);
			obj.setPrice(obj.getPrice() + newPrice);
		}
	}
	
	
	/*Get the CPU count for the price and hours expected by the user
	 * 
	 * If the combo CPU count is greater or equal than user CPU count, display the combo or else print "Combo Not possible"
	 * 
	 * */

	public List<FinalObject> calculateComboforPriceHourCPU(int hours, int cpus, float price) {

		List<FinalObject> r = calculateCPUsforHoursandPrice(hours,price);

		List<FinalObject> filteredList = r.stream().filter(x -> x.getCount() >= cpus).collect(Collectors.toList());		

		return filteredList;
	}

}
