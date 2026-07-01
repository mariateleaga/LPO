package lab09.parser.ast;

import lab09.visitors.Visitor;

public class DictCons extends BinaryOp {
	public DictCons(Exp key, Exp value) {
		super(key, value);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitDictCons(left, right);
	}
	
}