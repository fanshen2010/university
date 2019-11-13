import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class pipair_java{
	
	/**
	 * key is the set of pipair callee and single callee, example: SET(A), SET(A,B),....
	 * value is the set of caller for callee, example: SET(SCOPE1,SCOPE2),....
	 */
	public static Map<Set<String>,Set<String>> location= new HashMap<Set<String>,Set<String>>();
	/**
	 * the set to help initialize HashMap 'location'
	 */
	public static Set<String> nearByCallees= new HashSet<String>();
	
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
		// 
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
    	
    	// check same addvalue
    	if(nearByCallees.contains(addValue)){
    		return;
    	}
    	
    	//----------------------location-------------------------
    	Set<String> locationMapKeySet = null;
    	Set<String> locationMapValueSet = null;
    	for(String key:nearByCallees){
    		// update pair function and its callers
    		locationMapKeySet = new HashSet<String>();
    		locationMapKeySet.add(addValue);
    		locationMapKeySet.add(key);
    		if(!location.containsKey(locationMapKeySet)){
    			locationMapValueSet = new HashSet<String>();
    		}else{
    			locationMapValueSet = location.get(locationMapKeySet);
    		}
    		locationMapValueSet.add(addLocation);
    		location.put(locationMapKeySet, locationMapValueSet);
    		
    	}
    	
    	// update single function and its callers
		locationMapKeySet = new HashSet<String>();
		locationMapKeySet.add(addValue);
		if(!location.containsKey(locationMapKeySet)){
			locationMapValueSet = new HashSet<String>();
		}else{
			locationMapValueSet = location.get(locationMapKeySet);
		}
		locationMapValueSet.add(addLocation);
		location.put(locationMapKeySet, locationMapValueSet);
		
    	return;
    }
    
    
    
    /**
	 * Traverse HashMap 'location' to calculate support and confidence for each pipair
	 * and determine if a callee function is a bug in a caller function.
	 */
    public static void printBug(int support,double confidence){
    	
    	Integer supportKey = 0;
		Integer supportPair = 0;
		double confidencePair = 0;
		String singleKeyStr =null;
		String pairStr1 =null;
		String pairStr2 = null;
		
    	for(Set<String> singleKey:location.keySet()){
    		if(singleKey.size()==1){
				// support of single function
    			supportKey = location.get(singleKey).size();
				// singleKeyStr is for bug line printout 
				singleKeyStr = singleKey.toArray()[0].toString();
				
    			for(Set<String> pipairKey:location.keySet()){
        			if(pipairKey.size()>1 && pipairKey.contains(singleKeyStr)){
						// support of pipair function
        				supportPair = location.get(pipairKey).size();
						// confidence of pipair function
        				confidencePair = (double)supportPair/(double)supportKey;

						// check pipair to meet threshold support and confidence
        				if(supportKey>=support && supportPair>=support && confidencePair<1 && confidencePair>=confidence){
        					
        					// pairStr1 is for bug line printout
            				pairStr1 = pipairKey.toArray()[0].toString();
            				pairStr2 = pipairKey.toArray()[1].toString();
            				if(pairStr1.compareTo(pairStr2)<0){
            					pairStr1 = "("+pairStr1+", "+pairStr2+")";
            				}else{
            					pairStr1 = "("+pairStr2+", "+pairStr1+")";
            				}
            				            				
            				// location for pipair should take place of single function 
            				Set<String> locationTemp = new HashSet<String>();
            				String locationTempStr =null;
            				locationTemp.addAll(location.get(singleKey));
            				locationTemp.removeAll(location.get(pipairKey));
            				for(String locationTempElem:locationTemp){
            					locationTempStr = locationTempElem;
            					// printbug
                				System.out.println("bug: "+singleKeyStr+" in "+locationTempStr+", pair: "+pairStr1+", support: "+supportPair+", confidence: "+String.format("%.2f", confidencePair*100)+"%");
            				}
            			}
        			}
        		}
    		}
    	}
    	
    }
    
}