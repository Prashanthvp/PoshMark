package model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;



/**
 * Server Details is initialized using static block
 * 
 * Regions Available - USE,USW,Asia
 * 
 * */

public class ServerCPUDetails {	

	public static final List<String> regions = new ArrayList<>(Arrays.asList("USE","USW","ASIA"));

	public static final HashMap<String,Integer> serverCPUDetailSize = new HashMap<>();

	static final HashMap<String,Float> USEServerDetails = new HashMap<>();

	static final HashMap<String,Float> USWServerDetails = new HashMap<>();

	static final HashMap<String,Float> AsiaServerDetails = new HashMap<>();	

	public static final HashMap<String,HashMap<String,Float>> serverDetails = new HashMap<>();

	static {		
		serverCPUDetailSize.put("large", 1);
		serverCPUDetailSize.put("xlarge", 2);
		serverCPUDetailSize.put("2xlarge", 4);
		serverCPUDetailSize.put("4xlarge", 8);
		serverCPUDetailSize.put("8xlarge", 16);
		serverCPUDetailSize.put("10xlarge", 32);


		USEServerDetails.put("large", 0.12f);
		USEServerDetails.put("xlarge", 0.23f);
		USEServerDetails.put("2xlarge", 0.45f);
		USEServerDetails.put("4xlarge", 0.774f);
		USEServerDetails.put("8xlarge", 1.4f);
		USEServerDetails.put("10xlarge", 2.85f);


		USWServerDetails.put("large", 0.14f);		
		USWServerDetails.put("2xlarge", 0.413f);
		USWServerDetails.put("4xlarge", 0.89f);
		USWServerDetails.put("8xlarge", 1.3f);
		USWServerDetails.put("10xlarge", 2.97f);


		AsiaServerDetails.put("large", 0.11f);		
		AsiaServerDetails.put("2xlarge", 0.45f);
		AsiaServerDetails.put("4xlarge", 0.67f);
		AsiaServerDetails.put("8xlarge", 1.18f);


		serverDetails.put("USE", USEServerDetails);
		serverDetails.put("USW", USWServerDetails);
		serverDetails.put("ASIA", AsiaServerDetails);	

	}
}
