package ch.unibe.eindermu.utils;

import java.util.ArrayList;
import java.util.List;
@SuppressWarnings("serial")
abstract public class NumberList<T extends Number> extends ArrayList<T> {

    public static class Integer extends NumberList<java.lang.Integer>{
        public Integer(int[] array) {
            super(new java.lang.Integer[]{});
            for(int i:array) add(i);
        }

        public Integer() {
        }

        public void addAll(int[] array){
            for(int i:array) add(i);
        }
        
        @Override
        protected java.lang.Integer cast(double d) {
            return (int) d;
        }

    }
    
    public static class Double extends NumberList<java.lang.Double> {
        public Double(double[] array) {
            super(new java.lang.Double[]{});
            for(double i:array) add(i);
        }
        
        
        public Double() {
        }

        public void addAll(double[] array){
            for(double i:array) add(i);
        }
        
        @Override
        protected  java.lang.Double cast(double d) {
            return d;
        }
        
        public List<java.lang.Integer> getOrderedMaxima(){
            Integer indexes = new NumberList.Integer();
            Double values = new Double();
            Integer maxs = getLocalMaxima();
            for(int i = 0;i<maxs.size();i++){
                int j = 0;
                while(values.size()>j && values.get(j)>get(maxs.get(i))){
                    j++;
                }
                if(values.size()<=j){
                    values.add(get(maxs.get(i)));
                    indexes.add(maxs.get(i));
                }else{
                    values.set(j, get(maxs.get(i)));
                    indexes.set(j,maxs.get(i));
                }
            }
            return indexes;
        }
        public double[] toDoubleArray(){
        	double[] result = new double[size()];
        	int i = 0;
        	for(java.lang.Double d : this){
        		result[i++]=d;
        	}
        	return result;
        }

    }

    /**
     * 
     */
    
    abstract protected T cast(double d);
    
    /**
     * Empty constructor
     */
    public NumberList(){
        
    }
    
    /**
     * Constructor initializing the list by adding all
     * elements specified by the array.
     * @param array
     */
    public NumberList(T[] array){
        addAll(array);
    }
    
    /**
     * Adds all elements of the array to the end of the list
     * in the order they are present in the array.
     * @param array Array of items that will be added
     */
    public void addAll(T[] array){
        for(T i:array) add(i);
    }
    
    /**
     * Smoothes the histogramm by a gaussian blur. With the parameter stdDev you can
     * specify standard deviation of the gaussian distribution used to generate the mask.
     * @param stdDev
     */
    public NumberList<T> smooth(double stdDev) {
        int range = 1+(int)(3.0*stdDev);
        double[] mask = new double[2*range+1];
        double sum = 0;
        for (int i = 0; i < range; i++) {
            double y = Math.exp(-(i*i)/(2.0*(stdDev*stdDev)));
            sum += (i==0)?y:2*y;
            mask[range-i] = y;
            mask[range+i] = y;
        }
        for (int j = 0; j < mask.length; j++) {
            mask[j]/=sum;
        }
        double[] result = new double[size()];
        // convolve the serie
        for (int i = 0; i < size(); i++) {
        	double missed = 0;
        	double got = 0;
            for (int j = 0; j < mask.length; j++) {
                if (j + i - range < 0 || j + i - range >= size()) {
                	missed += mask[j];
                    continue;
                }
                got += mask[j];
                result[i] += get(i + j - range).doubleValue()*mask[j];
            }
            
            result[i] += result[i] * missed; 
        }
        for(int i = 0;i<size();i++){
            set(i,cast(result[i]));
        }
        return this;
    }
    
    /**
     * return index of maximal value of list
     * @return
     */
    public int getMaxIndex() {
        int index = 0;
        double value = java.lang.Double.NEGATIVE_INFINITY;
        for(int i = 0;i<this.size();i++){
            if(get(i).doubleValue()>value){
                index = i;
                value = get(i).doubleValue();
            }
        }
        return index;
    }
    
    /**
     * return maximal value of list
     * @return
     */
    public T getMax() {
        return get(getMaxIndex());
    }
    
    /**
     * return index of maximal value of list
     * @return
     */
    public int getMinIndex() {
        int index = 0;
        double value = java.lang.Double.POSITIVE_INFINITY;
        for(int i = 0;i<this.size();i++){
            if(get(i).doubleValue()<value){
                index = i;
                value = get(i).doubleValue();
            }
        }
        return index;
    }
    
    /**
     * return maximal value of list
     * @return
     */
    public T getMin() {
        return get(getMinIndex());
    }
    
    
    /**
     * returns the sum of all values
     * @return
     */
    public T getSum(){
        double sum= 0;
        for(T d:this){
            sum += d.doubleValue();
        }
        return cast(sum);
    }
    
    /**
     * returns the mean of all values in the List
     * @return
     */
    public double getMean() {
        return getSum().doubleValue()/(double)size();
    }
    

    
    /**
     * returns the variance of all values in the List
     * @return
     */
    public java.lang.Double getVariance() {
        double mean = 0;
        double var = 0;
        for(T d:this){
            mean += d.doubleValue();
            var += d.doubleValue()*d.doubleValue();
        }
        mean = mean/(double)size();
        return   var/(double)size() - mean*mean;
    }
    
    /**
     * returns list of indexes of the local maxima in the list.
     */
    public NumberList.Integer getLocalMaxima(){
        return getLocalMaxima(0);
    }
    
    /**
     * returns list of indexes of the local maxima in the list.
     * The vallies around a local maxima must be deeper than threshold.
     * @param threshold depth of the local maxima. If 0, all local maximas are considered.   
     * @return
     */
    public NumberList.Integer getLocalMaxima(double threshold){
        int lastLocalMax = -1;
        double triggerValue = java.lang.Double.NEGATIVE_INFINITY;
        boolean on = true;
        double prev = java.lang.Double.NEGATIVE_INFINITY;
        NumberList.Integer list = new NumberList.Integer();
        int index = 0;
        int plateau = 0;
        for(T i: this){
            double d = i.doubleValue();
            if(prev < d){
                if(triggerValue < d - threshold){
                    triggerValue = d-threshold;
                    lastLocalMax = index;
                }
                on = true;
                plateau = 0;
            }else if(prev > d){
                if(d < triggerValue && on){
                    list.add(lastLocalMax-plateau/2);
                    triggerValue = java.lang.Double.NEGATIVE_INFINITY;
                    on = false;
                }
                plateau = 0;
            }else{
                if(d == triggerValue + threshold){
                    lastLocalMax = index;
                    plateau++;
                }
            }
            index ++;
            prev = i.doubleValue();
        }
        return list;
    }

    
    /**
     * returns list of indexes of the local maxima in the list.
     * @param threshold minimal depth of the local minima.
     * @return
     */
    public NumberList.Integer getLocalMinima(double threshold){
        int lastLocalMin = -1;
        double triggerValue = java.lang.Double.POSITIVE_INFINITY;
        boolean on = true;
        double prev = java.lang.Double.POSITIVE_INFINITY;
        NumberList.Integer list = new NumberList.Integer();
        int index = 0;
        int plateau = 0;
        for(T i: this){
            double d = i.doubleValue();
            if(prev > d){
                if(triggerValue > d + threshold){
                    triggerValue = d+threshold;
                    lastLocalMin = index;
                }
                on = true;
                plateau = 0;
            }else if(prev < d){
                if(d > triggerValue && on){
                    list.add(lastLocalMin-plateau/2);
                    triggerValue = java.lang.Double.POSITIVE_INFINITY;
                    on = false;
                }
                plateau = 0;
            }else{
                if(d == triggerValue - threshold){
                    lastLocalMin = index;
                    plateau++;
                }
            }
            index ++;
            prev = i.doubleValue();
        }
        return list;
    }
    
    public NumberList.Double getZeroPoints() {
        return getZeroPoints(0);
    }
    
    public NumberList.Double getZeroPoints(double level) {
        NumberList.Double result = new NumberList.Double();
        for(int i = 0;i<size()-1;i++){
            double d1 = get(i).doubleValue()-level,d2 = get(i+1).doubleValue()-level; 
            if (d1*d2 < 0 ){
                if(d1 > 0){
                    result.add(i+(d1/(d1-d2)));
                }else{
                    result.add(i+1-(d2/(d2-d1)));
                }
            }
            if(d1 == 0){
                if(d2!=0){
                    result.add((double)i);
                }else{
                    int j = i+1;
                    while(j<size() && get(j).doubleValue()-level==0)
                        j++;
                    result.add((double)(i + (j-1-i)/2.0));
                    i = j;
                }
            }
        }
        return result;
    }
    
}
