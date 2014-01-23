import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Vector;


public class NBTrain {
	
	static Vector<String> tokenizeDoc(String cur_doc) {
        String[] words = cur_doc.split("\\s+");
        Vector<String> tokens = new Vector<String>();
        for (int i = 0; i < words.length; i++) {
        	words[i] = words[i].replaceAll("\\W", "");
        	if (words[i].length() > 0) {
        		tokens.add(words[i]);
        	}
        }
        return tokens;
	}

	
	public static void main(String[] args) {
		
		String inputPath = args[0];
		
		//vector of dictionaries: one hashmap per class
		Vector<HashMap<String,Integer>> vectorOfDics= new Vector<HashMap<String,Integer>>(); 
		//hashmap that links ClassName to array number of vectorOfDics
		HashMap<String,Integer> classPosition = new HashMap<String,Integer>();
		
		//labels of interest
		String[] sL = new String[]{"CCAT","ECAT","GCAT","MCAT"};
		HashSet<String> selectedLabels = new HashSet<String>(Arrays.asList(sL));
		
		//dic for current label
		HashMap<String,Integer> currentDic;
		
		// +--------------------------------------------------------
		// |     READING AND COUNTING (TRAINING)
		// |--------------------------------------------------------
		// |  To increase performance, we won't keep track
		// |  of Y=*  or any Y=y|W=* 
		// | we will only count them once data is consolidated
		// +--------------------------------------------------------
		
		//long startTime = System.currentTimeMillis(); 
		try {
			
	        BufferedReader br = new BufferedReader(new FileReader(inputPath));
			String line = br.readLine(); 
			while (line != null) {
				
				//read labels and words
				String[] labelsAndTokens = line.split("\\t",2);
				String[] labels = labelsAndTokens[0].split(",");
				Vector<String> tokens = tokenizeDoc(labelsAndTokens[1]);
				
				//filter out unselected labels
				Vector<String> filteredLabels = new Vector<String>();
				for(String label : labels){
					if(selectedLabels.contains(label))
						filteredLabels.add(label);
				}
				
				//for each label
				for(String label : filteredLabels){
					
					//if is label was previously unseen
					if(!classPosition.containsKey(label)){
						int assignedPos = vectorOfDics.size();
						classPosition.put(label, assignedPos);
						vectorOfDics.add(new HashMap<String,Integer>());
					}
					
					//get dic for current label
					currentDic = vectorOfDics.elementAt(classPosition.get(label));
					
					//for each token, count
					for(String token : tokens){
						if(!currentDic.containsKey(token)){
							currentDic.put(token, 1);
						}
						else{
							currentDic.put(token, currentDic.get(token)+1);
						}
					}
					
				}//end for label			
				
				line = br.readLine();	
			}
			
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		//long estimatedTime = System.currentTimeMillis() - startTime;
		//System.out.println("Training Time: "+estimatedTime);
		
		
		// +--------------------------------------------------------
		// |     OUTPUT COUNTING RESULTS (FOR FUTURE TESTING)
		// |--------------------------------------------------------
		// |  We will now count Y=*  and  Y=y|W=* while outputting 
		// |  the results to stdout
		// +--------------------------------------------------------
			
		int totalCount = 0;
		//for each label
		for(String label : classPosition.keySet()){
			int labelCount = 0;
			currentDic = vectorOfDics.elementAt(classPosition.get(label));
			
			//output each count of type Y=y,W=w
			for (Entry<String, Integer> entry : currentDic.entrySet()) {
			    String token = entry.getKey();
			    int tokenCount = entry.getValue();
			    System.out.println("Y="+label+",W="+token+"\t"+tokenCount);
			    labelCount += tokenCount;
			}
			
			//output count of Y=y,W=*
			System.out.println("Y="+label+",W=*\t"+labelCount);
			
			totalCount += labelCount;
		}
		//output count of Y=*
		System.out.println("Y=*\t"+totalCount);
		
			
			
		
		

	}

}
