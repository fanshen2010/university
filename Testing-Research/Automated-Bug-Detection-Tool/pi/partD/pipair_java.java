
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class pipair_java{
	
	/**
	 * key is the list of ordered pipair callee and single callee, 	example: [A],[A,A],[A,B],[B,A]....
	 * value is the set of callers for the callee, 					example: SET(SCOPE1,SCOPE2),....
	 */
	public static Map<List<String>,Set<String>> location= new HashMap<List<String>,Set<String>>();
	/**
	 * the set to help initialize HashMap 'location'
	 */
	public static List<String> nearByCallees= new ArrayList<String>();
	
    public static void main(String[] args) throws Exception{  
    	
    	// accept argument to set threshold support and confidence
    	int support=3;
    	double confidence=0.65;
    	int argsLength = args.length;
    	if(argsLength>=1){
			if(argsLength>=2){
				support = Integer.parseInt(args[1]);
				if(argsLength>=3){
				    confidence = Double.parseDouble(args[2]);
				    confidence = confidence/100;
				}
			}
		}
    	
		Scanner scanner=new Scanner(System.in);
		Pattern caller_p = Pattern.compile("Call.*'(.*)'.*"); 
		Pattern callee_p = Pattern.compile(".*'(.*)'.*");
		Matcher caller_m = null;
		Matcher callee_m = null;
		String line =null;
		while(scanner.hasNext()){
			line = scanner.nextLine();
			caller_m = caller_p.matcher(line);
			if(caller_m.matches() /*&& !"main".equals(caller_m.group(1))*/) {
				nearByCallees.clear();
				while(scanner.hasNext()){
					line = scanner.nextLine();
					if(line.length()==0){
						break;
					}
					callee_m = callee_p.matcher(line);
					if(callee_m.matches()){
						addValueToMap(callee_m.group(1),caller_m.group(1));
						nearByCallees.add(callee_m.group(1));
					}
				}
				
			}
		}
		scanner.close();
		// Traverse HashMap 'location' and print bug
		printBug(support,confidence);
    }
    
    
    /**
	 * the function to help initialize HashMap 'location'
	 */
    public static void addValueToMap(String addValue,String addLocation){
    	
    	//----------------------location-------------------------
    	List<String> locationMapKeyList = null;
    	Set<String> locationMapValueSet = null;
    	for(String key:nearByCallees){
    		// update pair function and its callers
    		locationMapKeyList = new ArrayList<String>();
    		locationMapKeyList.add(key);
    		locationMapKeyList.add(addValue);
    		if(!location.containsKey(locationMapKeyList)){
    			locationMapValueSet = new HashSet<String>();
    		}else{
    			locationMapValueSet = location.get(locationMapKeyList);
    		}
    		locationMapValueSet.add(addLocation);
    		location.put(locationMapKeyList, locationMapValueSet);
    		
    	}
    	
    	// update single function and its callers
		locationMapKeyList = new ArrayList<String>();
		locationMapKeyList.add(addValue);
		if(!location.containsKey(locationMapKeyList)){
			locationMapValueSet = new HashSet<String>();
		}else{
			locationMapValueSet = location.get(locationMapKeyList);
		}
		locationMapValueSet.add(addLocation);
		location.put(locationMapKeyList, locationMapValueSet);
		
    	return;
    }
    
    
    
    /**
	 * Traverse HashMap 'location' to calculate support and confidence for each pipair
	 * and determine if a callee function is a bug in a caller function.
	 */
    public static void printBug(int support,double confidence){
    	
    	Integer supportKey = 0;
		Integer supportPair = 0;
		Integer supportPair2 = 0;
		double confidencePair = 0;
		double confidencePair2 = 0;
		String singleKeyStr =null;
		String pairStr =null;
		String pairStr2 =null;		
		List<List<String>> visit = new ArrayList<List<String>>();
		
    	for(List<String> key:location.keySet()){
    		if(key.size()==1){
    			List<String> singleKey = key;
    			// support of single function
    			supportKey = location.get(singleKey).size();
    			// singleKeyStr is for bug line printout 
				singleKeyStr = singleKey.get(0);
				
    			for(List<String> pipairKey:location.keySet()){
        			if(pipairKey.size()>1 && pipairKey.contains(singleKeyStr)){
        				// support of pipair function
        				supportPair = location.get(pipairKey).size();
        				// support of pipair function
        				confidencePair = (double)supportPair/(double)supportKey;
        				// check pipair to meet threshold support and confidence
        				if(supportKey>=support && supportPair>=support && confidencePair<1 && confidencePair>=confidence){
        					
            				// pipairKey2 is the pipairKey in reverse order, example: pipairKey is [A,B] and pipairKey2 is [B,A]
        					List<String> pipairKey2 = new ArrayList<String>();
        					Set<String>locationTemp2 = new HashSet<String>();
        					pipairKey2.add(pipairKey.get(1));
        					pipairKey2.add(pipairKey.get(0));
        					locationTemp2 = location.get(pipairKey2);  
            				
            				// pairStr is for bug line printout
            				pairStr = "("+pipairKey.get(0)+", "+pipairKey.get(1)+")"; 
            				// pairStr2 is for bug line printout
            				pairStr2 = "("+pipairKey2.get(0)+", "+pipairKey2.get(1)+")";
            				// location for pipair should take place of single function 
            				Set<String> locationTemp = new HashSet<String>();
            				String locationTempStr =null;
            				locationTemp.addAll(location.get(singleKey));
            				locationTemp.removeAll(location.get(pipairKey));
            				for(String locationTempElem:locationTemp){
            					locationTempStr = locationTempElem;
            					
            					// to search the situation that both pipairKey is the pipairKey2 meet threshold support and confidence
            					// and print bug only once, for example: bug A in scope1 pipair:[A,B] or pipair:[B,A]
            					if(locationTemp2!=null && !locationTemp2.isEmpty() && !pipairKey2.get(0).equals(pipairKey2.get(1))){
        							supportPair2 = location.get(pipairKey2).size();
                    				confidencePair2 = (double)supportPair2/(double)supportKey;
                    				if(supportKey>=support && supportPair2>=support && confidencePair2<1 && confidencePair2>=confidence){
                    					Set<String> locationTempComp = new HashSet<String>();
    	                				locationTempComp.addAll(location.get(singleKey));
    	                				locationTempComp.removeAll(location.get(pipairKey2));
    	                				if(locationTempComp.contains(locationTempStr)){
    	                					
    	                					List<String> visitList = new ArrayList<String>();
    	                					visitList.add(singleKeyStr);
    	                					visitList.add(pairStr2);
    	                					visitList.add(locationTempStr);
    	                					if(!visit.contains(visitList)){
    	                						// printbug
    	                						System.out.println("bug: "+singleKeyStr+" in "+locationTempStr+", pair: "+pairStr+", support: "+supportPair+", confidence: "+String.format("%.2f", confidencePair*100)+
        	                							"% or pair: "+pairStr2+", support: "+supportPair2+", confidence: "+String.format("%.2f", confidencePair2*100)+"%");
    	                						visitList = new ArrayList<String>();
        	                					visitList.add(singleKeyStr);
        	                					visitList.add(pairStr);
        	                					visitList.add(locationTempStr);
        	                					visit.add(visitList);
    	            						}
    	                					continue;
    	                				}
                    				}
        						}
            					
            					// print other bug
            					System.out.println("bug: "+singleKeyStr+" in "+locationTempStr+", pair: "+pairStr+", support: "+supportPair+", confidence: "+String.format("%.2f", confidencePair*100)+"%");
            					
            				}
            			}
        			}     				
        		}
    		}
    		
    	}
    	
    }
    
}

