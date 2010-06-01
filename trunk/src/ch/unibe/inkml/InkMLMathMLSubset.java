package ch.unibe.inkml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class InkMLMathMLSubset {

	private MathElement root;
	private Map<String,Identifier> parameters = new HashMap<String,Identifier>();
	public enum ArithmOp {PLUS,MINUS,TIMES,DIVIDE,QUOTIENT,REM,POWER,ROOT,MIN,MAX,ABS,
		FLOOR,CEILING,SIN,COS,TAN,ARCSIN,ARCCOS,ARCTAN,EXP,LN,LOG,
		AND, OR, XOR, NOT, EQ, NEQ, GT, LT, GEQ, LEQ};
	
	
	public void initializeMathMl(Element mathmlRoot) throws InkMLComplianceException{
		this.root = build(mathmlRoot);
		
	}
		
	public void bind(String name, InkChannel.Type type, Object value){
		if(parameters.containsKey(name)){
			Identifier id = parameters.get(name);
			id.setValue(value);
			id.setValue(type);
		}
	}
	public Value getResult() throws InkMLComplianceException{
		return this.root.getValue();
	}

	private MathElement build(Element element) throws InkMLComplianceException {
		String n = element.getNodeName(); 
		if(n.equals("apply")){
			Element ce = (Element) element.getFirstChild();
			Operator op = getOperator(ce);
			while (ce.getNextSibling() != null){
				ce = (Element) ce.getNextSibling();
				op.appendParameter((Value) build(ce));
			}
			return op;
		}else if(n.equals("cn")){
			return new Value(Double.parseDouble(element.getTextContent()));
		}else if(n.equals("exponentiale")){
			return new Value(Math.E);
		}else if(n.equals("pe")){
			return new Value(Math.PI);
		}else if(n.equals("true")){
			return new Value(true);
		}else if(n.equals("false")){
			return new Value(false);
		}else if(n.equals("ci")){
			Identifier id = new Identifier(element.getTextContent());
			this.parameters.put(id.getId(), id);
			return id;
		}else{
			throw new InkMLComplianceException("Element '"+n+"' is not supported");
		}
	}
	
	private Operator getOperator(Element fc) {
		
		return null;
	}
	
	abstract public class MathElement{
		abstract public Value getValue() throws InkMLComplianceException;

		public abstract void exportToInkML(Element parent, Document d);
	}
	
	public class Identifier extends MathElement{
		private String name;
		private Object value;
		private InkChannel.Type type;

		public Identifier(String name) {
			this.name = name;
		}

		public String getId() {
			return name;
		}

		public void setValue(Object value) {
			this.value = value;
		}
		
		public Value getValue() throws InkMLComplianceException{
			switch(type){
			case DECIMAL:
				return new Value((double)((Double)value));
			case INTEGER:
				return new Value((int)((Integer)value));
			case BOOLEAN:
				return new Value((boolean)((Boolean)value));
			default:
				throw new InkMLComplianceException("No value has been assigned to the identifier '"+name+"'");
			}
		}

		@Override
		public void exportToInkML(Element parent, Document d) {
			Element ci = d.createElement("ci");
			ci.setTextContent(this.name);
			parent.appendChild(ci);
		}
		
	}

	public class Value extends MathElement{
		private InkChannel.Type type;
		private double dvalue;
		private int ivalue;
		private boolean bvalue;
		public Value(double d) {
			this.dvalue = d;
			type = InkChannel.Type.DECIMAL;
		}
		public Value(int i){
			this.ivalue = i;
			type = InkChannel.Type.INTEGER;
		}
		public Value(boolean b){
			this.bvalue = b;
			type = InkChannel.Type.BOOLEAN;
		}
		public double getDouble() {
			if(type == InkChannel.Type.DECIMAL){
				return dvalue;
			}else if (type == InkChannel.Type.INTEGER){
				return (double) ivalue;
			}
			throw new ClassCastException("cannot concert "+type+" to double");
		}
		public int getInteger() {
			if(type == InkChannel.Type.DECIMAL){
				return (int) dvalue;
			}else if (type == InkChannel.Type.INTEGER){
				return ivalue;
			}
			throw new ClassCastException("cannot concert "+type+" to int.");
		}
		public boolean getBoolean() {
			if(type == InkChannel.Type.BOOLEAN){
				return bvalue;
			}else{ 
				throw new ClassCastException("cannot concert "+type+" to int.");
			}
		}
		public Value getValue(){
			return this;
		}
		public Object getObject(InkChannel.Type type) {
			switch(type){
			case BOOLEAN:
				return new Boolean(getBoolean());
			case DECIMAL:
				return new Double(getDouble());
			case INTEGER:
				return new Integer(getInteger());
			}
			return null;
		}
		@Override
		public void exportToInkML(Element parent, Document d) {
			switch(type){
			case DECIMAL:
			case INTEGER:
				Element nc = d.createElement("nc");
				nc.setTextContent(Double.toString(getDouble()));
				parent.appendChild(nc);
				break;
			case BOOLEAN:
				Element bool = d.createElement(Boolean.toString(getBoolean()));
				parent.appendChild(bool);
				break;
			}
		}
	}
	public class Operator extends MathElement{
		private ArithmOp type;
		protected List<Value> parameters = new ArrayList<Value>();
		public void appendParameter(Value mathElement) {
			parameters.add(mathElement);
		}
		public Value getValue() {
			switch(type){
				case PLUS :
					return new Value(parameters.get(0).getDouble() + parameters.get(1).getDouble());
				case MINUS :
					return new Value(parameters.get(0).getDouble() - parameters.get(1).getDouble());
				case TIMES :
					return new Value(parameters.get(0).getDouble() * parameters.get(1).getDouble());
				case DIVIDE :
					return new Value(parameters.get(0).getDouble() / parameters.get(1).getDouble());
				case QUOTIENT :
					return new Value(parameters.get(0).getDouble() / parameters.get(1).getInteger());
				case REM :
					return new Value(parameters.get(0).getDouble() % parameters.get(1).getInteger());
				case POWER :
					return new Value(Math.pow(parameters.get(0).getDouble(),parameters.get(1).getDouble()));	
				case ROOT :
					return new Value(Math.sqrt(parameters.get(0).getDouble()));
				case MIN :
					return new Value(Math.min(parameters.get(0).getDouble(),parameters.get(1).getDouble()));	
				case MAX :
					return new Value(Math.max(parameters.get(0).getDouble(),parameters.get(1).getDouble()));
				case ABS :
					return new Value(Math.abs(parameters.get(0).getDouble()));
				case FLOOR :
					return new Value(Math.floor(parameters.get(0).getDouble()));
				case CEILING :
					return new Value(Math.ceil(parameters.get(0).getDouble()));		
				case SIN:
					return new Value(Math.sin(parameters.get(0).getDouble()));
				case COS:
					return new Value(Math.cos(parameters.get(0).getDouble()));
				case TAN:
					return new Value(Math.tan(parameters.get(0).getDouble()));
				case ARCSIN:
					return new Value(Math.asin(parameters.get(0).getDouble()));
				case ARCCOS:
					return new Value(Math.acos(parameters.get(0).getDouble()));
				case ARCTAN:
					return new Value(Math.atan(parameters.get(0).getDouble()));
				case EXP:
					return new Value(Math.exp(parameters.get(0).getDouble()));
				case LN:
					return new Value(Math.log(parameters.get(0).getDouble()));
				case LOG:
					return new Value(Math.log10(parameters.get(0).getDouble()));			
				case AND:
					return new Value(parameters.get(0).getBoolean() && parameters.get(1).getBoolean());
				case OR:
					return new Value(parameters.get(0).getBoolean() || parameters.get(1).getBoolean());
				case XOR:
					boolean b1 = parameters.get(0).getBoolean(),b2 = parameters.get(2).getBoolean();  
					return new Value(b1 || b2 & !(b1 && b2));
				case NOT:
					return new Value(!parameters.get(0).getBoolean());
				case EQ:
					return new Value(parameters.get(0).equals(parameters.get(1)));
				case NEQ:
					return new Value(!parameters.get(0).equals(parameters.get(1)));
				case GT:
					return new Value(parameters.get(0).getDouble() > parameters.get(1).getDouble());
				case LT:
					return new Value(parameters.get(0).getDouble() < parameters.get(1).getDouble());
				case GEQ:
					return new Value(parameters.get(0).getDouble() >= parameters.get(1).getDouble());
				case LEQ:
					return new Value(parameters.get(0).getDouble() <= parameters.get(1).getDouble());
				default:
					return new Value(0);
			}
			
		}
		@Override
		public void exportToInkML(Element parent, Document d) {
			Element apply = d.createElement("apply");
			parent.appendChild(apply);
			Element op = d.createElement(type.toString().toLowerCase());
			apply.appendChild(op);
			for(MathElement el: parameters){
				el.exportToInkML(apply, d);
			}
			
		}
	}
	public void exportToInkML(Element listNode) {
		Document d = listNode.getOwnerDocument();
		this.root.exportToInkML(listNode,d);
	}

	

}
