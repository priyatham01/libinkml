package ch.unibe.inkml;

import java.util.Vector;


abstract public class InkTableLike extends InkUniqueElement {

	public InkTableLike(InkInk ink) {
		super(ink);
	}

	public InkTableLike(InkInk ink, String id) {
		super(ink,id);
	}

	private Vector<Vector<Object>> table;
	
	public Vector<Vector<Object>> getTable() {
		return table;
	}

	public void buildTable(String content){
		this.table = new Vector<Vector<Object>>();
		String[] rows = content.split(",");
		for(String row : rows){
			row = row.trim();
			if(row.equals("")){
				continue;
			}
			String[] values = row.split(" ");
			Vector<Object> vrow = new Vector<Object>();
			for(String value : values){
				value = value.trim();
				if(!value.equals("")){
					vrow.add(this.loadTableElement(value));
				}
			}
			this.table.add(vrow);
		}
	}
	
	
	private Object loadTableElement(String value) {
		String bool = value.toLowerCase().substring(0,1);
		if(bool.equals("t")){
			return new Boolean(true);
		}else if(bool.equals("f")){
			return new Boolean(false);
		}else{
			return Double.parseDouble(value);
		}
	}
	
	protected String tableToString() {
		StringBuffer result = new StringBuffer();
		for(Vector<Object> row : this.table){
			for(Object value : row){
				if( value instanceof Boolean){
					result.append(((value.equals(Boolean.TRUE))?"T":"F"));
				}else{
					result.append(value.toString());
				}
				result.append(" ");
			}
			result.append(",");
		}
		return result.toString();
	}
	
	protected void setTable(Vector<Vector<Object>> table) {
		this.table = table;
		
	}
	abstract public Vector<Object> transform(Vector<Object> point);
}
