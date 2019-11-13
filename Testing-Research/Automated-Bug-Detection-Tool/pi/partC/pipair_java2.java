
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
	 * key is the set of pipair callee and single callee, 	example: SET(A), SET(A,B),....
	 * value is the set of callers for the callee, 			example: SET(SCOPE1,SCOPE2),....
	 */
	public static Map<Set<String>,Set<String>> location= new HashMap<Set<String>,Set<String>>();
	/**
	 * key is the caller,									example: scope1,....
	 * value is the set of callees for the caller.			example: SET(A,B,C,D)....
	 */
	public static Map<String,Set<String>> callerCallee= new HashMap<String,Set<String>>();
	/**
	 * key is the caller,								 	example: scope4,....
	 * value is the set of callees for after expansion      example: SET(A,B,C,D,printf),....
	 */
	public static Map<String,Set<String>> callerCalleeExtend= new HashMap<String,Set<String>>();
	/**
	 * control expansion level
	 */
	public static int expandLevel=1;
	
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
				    if(argsLength>=4){
				    	expandLevel = Integer.parseInt(args[3]);
					}
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
				Set<String> callees = new HashSet<String>();
				while(scanner.hasNext()){
					line = scanner.nextLine();
					if(line.length()==0){
						break;
					}
					callee_m = callee_p.matcher(line);
					if(callee_m.matches()){
						callees.add(callee_m.group(1));
					}
				}
				if(!callees.isEmpty()){
					callerCallee.put(caller_m.group(1), callees);
				}
			}
		}
		scanner.close();
		// Update HashMap 'location' according to expansion result
		initInterLocation();
		// Traverse HashMap 'location' and print bug
		printBug(support,confidence);
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
    



	/**
	 * Expand the functions according to HashMap 'callerCallee' to Create HashMap 'callerCalleeExtend'
	 * Update HashMap 'location' according to HashMap 'callerCalleeExtend'
	 */
    public static void initInterLocation(){

    	// Expand the functions according to HashMap 'callerCallee' to Create HashMap 'callerCalleeExtend'
    	List<String> tranList = new ArrayList<String>();
    	Set<String> tranSet = new HashSet<String>();
        for(String caller:callerCallee.keySet()){
        	tranList = new ArrayList<String>();
        	tranSet = new HashSet<String>();
        	tranList = getCalleeExtend(tranList,caller,0);
        	tranSet.addAll(tranList);
        	callerCalleeExtend.put(caller, tranSet);
        }
        
    	// Update HashMap 'location' according to HashMap 'callerCalleeExtend'
        int i,j;
        location.clear();        
        for(String caller:callerCalleeExtend.keySet()){
        	Set<String> extendCallees = callerCalleeExtend.get(caller);
        	Object[] extendCalleesArray = extendCallees.toArray();
			
        	// find combination and add pipair and its callers
        	for(i=0;i<extendCalleesArray.length;i++){
        		for(j=0;j<extendCalleesArray.length;j++){
        			// add pipair and its callers
        			Set<String> tempKey = new HashSet<String>();
        			Set<String> tempValue = null;
        			tempKey.add(extendCalleesArray[i].toString());
        			tempKey.add(extendCalleesArray[j].toString());
        			if(!location.containsKey(tempKey)){
        				tempValue = new HashSet<String>();
        			}else{
        				tempValue = location.get(tempKey);
        			}
        			tempValue.add(caller);
        			location.put(tempKey, tempValue);
        			
        		}
        	}
        }
    	
    }
    
    

	/**
	 * Expand caller function into a predetermined level.
	 * The return value is a set that expanded callee functions
	 */
    public static List<String> getCalleeExtend(List<String> result,String caller,Integer levelCount){
    	
    	// expandLevel is expand level, default =1
    	if(levelCount>expandLevel){
    		return result;
    	}else{
    		Set<String> callees = callerCallee.get(caller);
    		if(callees!=null && !callees.isEmpty()){
    			result.remove(caller);
    			result.addAll(callees);
        		for(String callee:callees){
            		result = getCalleeExtend(result,callee,levelCount+1);
        		}
    		}
        	return result;
    	}
    	
    }
    
    
}

