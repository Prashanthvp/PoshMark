package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import model.FinalObject;
import model.ServerCPUDetails;
import util.Utils;

public class CombinationCalcService {

	/*Get the CPU count for the price and hours expected by the user
	 * 
	 * If the combo cpu count is greater or equal than user cpu count, display the combo or else print "Combo Not possible"
	 * 
	 * */

	public List<FinalObject> calculateComboforPriceHourCPU(int hours, int cpus, float price) {

		List<FinalObject> r = calculateCPUsforHoursandPrice(hours,price);

		List<FinalObject> filteredList = r.stream().filter(x -> x.getCount() >= cpus).collect(Collectors.toList());		

		return filteredList;
	}


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

			float totalCPUCountPerRegion = Utils.sum(cpuArray);

			float totalCPUPricePerRegion = Utils.getTotalCPUPrice(region);

			// Get the count of max possible full stack for specific region

			float fullStack = (float) Math.ceil(requiredCPUCount/totalCPUCountPerRegion);			

			int maxFullStackCPUsCount = (int)(fullStack * totalCPUCountPerRegion);

			int remainingStackToBeObtained = maxFullStackCPUsCount - requiredCPUCount;

			List<List<Integer>> removalCombo = Utils.combinationSum(cpuArray, remainingStackToBeObtained, fullStack);

			float priceOfFullStackPerHour = fullStack * totalCPUPricePerRegion;

			float priceOfFullStackforRequiredHours = priceOfFullStackPerHour * hours;

			//For certain region, combo is not possible for remaining CPUs count.

			if(!removalCombo.isEmpty())			
				calculateFinalPricePerRegion(region, priceOfFullStackforRequiredHours, fullStack, removalCombo, smallestComboInAllRegions);

			Collections.sort(smallestComboInAllRegions,FinalObject.getpriceComparator());	//To get smallest price combo in all regions	

		}
		return smallestComboInAllRegions;
	}


	/*
	 * Calculate the price of each combo after removal in each region
	 * 
	 * Minimum price combo per region is added to "smallestComboInAllRegions" list
	 * 
	 */

	private void calculateFinalPricePerRegion(String region, float priceOfFullStackforRequiredHours, float maxFullstack,
			List<List<Integer>> removalCombinations,List<FinalObject> smallestComboInAllRegions) {

		List<FinalObject> combosInParticularRegion = new ArrayList<FinalObject>();		

		// Sort the list of combinations of a single region based on price 
		Collections.sort(combosInParticularRegion, FinalObject.getpriceComparator()); 

		// Remove the extra CPUs from full stack to achieve the user required CPUs count

		for (List<Integer> removalCombination:removalCombinations) 
			combosInParticularRegion.add(calculatePricePerComboRemoval(region, maxFullstack, priceOfFullStackforRequiredHours,removalCombination));		

		smallestComboInAllRegions.add(combosInParticularRegion.get(0));  //To get smallest price combo in a particular region.
	}

	private FinalObject calculatePricePerComboRemoval(String region, float count, float priceOfFullStackforRequiredHours, List<Integer> removalCombination) {

		HashMap<Integer, String> cpuNameMap = Utils.getserverCPUDetails();

		HashMap<String, Float> cpuSizeMap = ServerCPUDetails.serverDetails.get(region);

		HashMap<String, Float> maxStack = buildMaxStack(cpuSizeMap,count);

		for (Integer i:removalCombination) {

			String sizeName = cpuNameMap.get(i);

			Float pricePerSize = cpuSizeMap.get(cpuNameMap.get(i));

			priceOfFullStackforRequiredHours = priceOfFullStackforRequiredHours - pricePerSize;			

			maxStack.put(sizeName, maxStack.get(sizeName) - 1);  //Remove a single instance to achieve the required CPU size.
		}

		FinalObject f = new FinalObject(region, priceOfFullStackforRequiredHours, maxStack);

		return f;
	}

	/*Max count of CPUs per size for the price*/
	private HashMap<String, Float> buildMaxStack(HashMap<String, Float> c, float max) {

		HashMap<String, Float> maxStack = new HashMap<>();

		for (Map.Entry<String, Float> entry : c.entrySet()) 
			maxStack.put(entry.getKey(), max);

		return maxStack;
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

	private void calcForRemainingcost(float remainingCost, float totalHours, HashMap<String, Float> stackdetails,HashMap<String, Integer> cpuDetails,
			FinalObject obj) {

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
}