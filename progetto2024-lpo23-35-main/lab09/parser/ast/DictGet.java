package lab09.parser.ast;

import lab09.visitors.Visitor;

public class DictGet extends BinaryOp {
	public DictGet (Exp dict, Exp key) {
		super(dict, key);
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitDictGet(left, right);
	}
}